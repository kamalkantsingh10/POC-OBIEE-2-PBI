package obi2pbi;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import obi2pbi.core.Citation;
import obi2pbi.core.KbData;
import obi2pbi.core.KbRows;
import obi2pbi.core.KbTable;
import obi2pbi.core.SliceSchema;
import obi2pbi.in.kb.KbFolderReader;

/**
 * Test plumbing for slices: a small Knowledge Base built in memory row by row, running the two commands,
 * reading a Slice folder back, and the checks several tests share.
 */
final class SliceFixture {

    private final String estate;
    private final Map<String, List<String[]>> rows = new LinkedHashMap<>();

    private SliceFixture(String estate) {
        this.estate = estate;
        for (KbTable table : SliceSchema.carriedTables()) {
            rows.put(table.name, new ArrayList<>());
        }
    }

    static SliceFixture estate(String estate) {
        return new SliceFixture(estate);
    }

    /** Adds a row: column, value, column, value ... Columns not named are empty; rpd_id is the estate. */
    SliceFixture add(String table, String... columnsAndValues) {
        KbTable definition = obi2pbi.core.KbSchema.table(table);
        List<String> header = definition.header();
        String[] row = new String[header.size()];
        java.util.Arrays.fill(row, "");
        if (header.contains("rpd_id")) {
            row[header.indexOf("rpd_id")] = estate;
        }
        for (int i = 0; i < columnsAndValues.length; i += 2) {
            int column = header.indexOf(columnsAndValues[i]);
            Check.that(column >= 0, table + " has a column " + columnsAndValues[i]);
            row[column] = columnsAndValues[i + 1];
        }
        rows.get(table).add(row);
        return this;
    }

    /**
     * A subject area {@code SA} with one presentation table {@code T} over one logical table {@code F}; add
     * columns with {@link #column}.
     */
    SliceFixture area() {
        add("presentation_subject_areas", "row_id", pres("SA"), "name", "SA", "business_model", "BM");
        add("presentation_tables", "row_id", pres("SA.T"), "subject_area_id", pres("SA"), "name", "T");
        add("logical_tables", "row_id", bmm("BM.F"), "business_model", "BM", "name", "F", "source_count", "1");
        add("logical_table_sources", "row_id", bmm("BM.F.src.S"), "logical_table_id", bmm("BM.F"), "name", "S",
                "is_active", "true");
        add("physical_tables", "row_id", phys("DB.P"), "container", "DB", "name", "P", "table_type", "none");
        return this;
    }

    /** A presentation column of {@code T} with its logical column in {@code F}, mapped to a physical column of {@code P}. */
    SliceFixture column(String name, boolean measure, String formula) {
        add("presentation_columns", "row_id", pres("SA.T." + name), "table_id", pres("SA.T"), "name", name,
                "logical_column_id", bmm("BM.F." + name));
        add("logical_columns", "row_id", bmm("BM.F." + name), "table_id", bmm("BM.F"), "name", name, "is_measure",
                measure ? "true" : "false", "source_count", "1", "mapped_source_count", "1", "derived_expr_text",
                formula, "derived_expr_text_desc", formula);
        add("physical_columns", "row_id", phys("DB.P." + name), "table_id", phys("DB.P"), "name", name, "data_type", "DOUBLE");
        add("lts_column_map", "row_id", bmm("BM.F.src.S#map" + name), "source_id", bmm("BM.F.src.S"), "logical_column_id",
                bmm("BM.F." + name), "physical_column_id", phys("DB.P." + name), "physical_expr_text_desc", "\"P\".\"" + name + "\"");
        add("lineage", "row_id", pres("SA.T." + name) + "#" + bmm("BM.F.src.S"), "presentation_column_id", pres("SA.T." + name),
                "logical_column_id", bmm("BM.F." + name), "source_id", bmm("BM.F.src.S"), "physical_table_id", phys("DB.P"),
                "physical_column_id", phys("DB.P." + name));
        return this;
    }

    String pres(String path) {
        return estate + "::pres::" + path;
    }

    String bmm(String path) {
        return estate + "::bmm::" + path;
    }

    String phys(String path) {
        return estate + "::phys::" + path;
    }

    String var(String path) {
        return estate + "::var::" + path;
    }

    String cat(String path) {
        return estate + "::cat::" + path;
    }

    KbData build() {
        KbData kb = new KbData();
        kb.manifest.put("extractor_version", "test");
        kb.manifest.put("extracted_at", "-");
        kb.manifest.put("rpd_ids", estate);
        kb.manifest.put("halves_present:" + estate, "rpd catalog");
        for (KbTable table : SliceSchema.carriedTables()) {
            kb.put(new KbRows(table.name, table.header(), rows.get(table.name)));
        }
        return kb;
    }

    /** Writes the Knowledge Base as a folder the slice command can read. */
    Path write(Path folder) {
        KbData kb = build();
        try {
            List<String> names = new ArrayList<>();
            for (String name : kb.tableNames()) {
                obi2pbi.out.CsvWriter.write(folder.resolve(name + ".csv"), kb.table(name).header, kb.table(name).rows);
                names.add(name);
            }
            names.sort(null);
            List<String[]> manifest = new ArrayList<>();
            for (Map.Entry<String, String> fact : kb.manifest.entrySet()) {
                manifest.add(new String[] {fact.getKey(), fact.getValue()});
            }
            manifest.add(new String[] {"tables", String.join(" ", names)});
            obi2pbi.out.CsvWriter.write(folder.resolve("manifest.csv"), List.of("key", "value"), manifest);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return folder;
    }

    /**
     * The rows a document must cover, worked out again from the Slice's tables without the packet code:
     * presentation tables and columns of the subject area, logical tables that have columns, their measures,
     * keys and sources, physical tables with their definitions, columns and keys, joins, dimensions and
     * presentation hierarchies, variables, security rows, and report columns that are calculations.
     */
    static Set<String> expectedRequired(KbData slice) {
        Set<String> expected = new TreeSet<>();
        String area = slice.fact("subject_area_id");
        Set<String> areaTables = new TreeSet<>();
        for (Map<String, String> row : slice.table("presentation_tables").records()) {
            if (row.get("subject_area_id").equals(area)) {
                areaTables.add(row.get("row_id"));
            }
        }
        expected.addAll(areaTables);
        for (Map<String, String> row : slice.table("presentation_columns").records()) {
            if (areaTables.contains(row.get("table_id"))) {
                expected.add(row.get("row_id"));
            }
        }
        Set<String> described = new TreeSet<>();
        for (Map<String, String> row : slice.table("logical_columns").records()) {
            described.add(row.get("table_id"));
            if (row.get("is_measure").equals("true")) {
                expected.add(row.get("row_id"));
            }
        }
        expected.addAll(described);
        for (Map<String, String> row : slice.table("logical_table_sources").records()) {
            if (described.contains(row.get("logical_table_id"))) {
                expected.add(row.get("row_id"));
            }
        }
        Set<String> describedSources = new TreeSet<>();
        for (Map<String, String> row : slice.table("logical_table_sources").records()) {
            if (described.contains(row.get("logical_table_id"))) {
                describedSources.add(row.get("row_id"));
            }
        }
        for (Map<String, String> row : slice.table("lts_joins").records()) {
            if (describedSources.contains(row.get("source_id"))) {
                expected.add(row.get("row_id"));
            }
        }
        for (Map<String, String> row : slice.table("logical_keys").records()) {
            if (described.contains(row.get("logical_table_id"))) {
                expected.add(row.get("row_id"));
            }
        }
        for (String table : List.of("bmm_joins", "physical_joins", "logical_dimensions", "presentation_hierarchies",
                "variables", "security_rls", "physical_tables", "physical_table_definitions", "physical_columns",
                "physical_keys")) {
            for (Map<String, String> row : slice.table(table).records()) {
                expected.add(row.get("row_id"));
            }
        }
        for (Map<String, String> row : slice.table("analysis_columns").records()) {
            String formula = row.get("formula").trim();
            if (row.get("is_computation").equals("true")
                    || (!formula.isEmpty() && !formula.matches("\"[^\"]+\"(\\.\"[^\"]+\"){1,2}"))) {
                expected.add(row.get("row_id"));
            }
        }
        return expected;
    }

    static Set<String> required(KbData slice) {
        Set<String> ids = new TreeSet<>();
        KbRows required = slice.table(SliceSchema.REQUIRED_ROWS);
        for (String[] row : required.rows) {
            ids.add(required.get(row, "row_id"));
        }
        return ids;
    }

    // ------------------------------------------------------------------ commands and folders

    static TestSupport.Run slice(Path kb, Path out, String... more) {
        List<String> arguments = new ArrayList<>(List.of("slice", kb.toString(), "--out", out.toString(), "--timestamp", "-"));
        arguments.addAll(List.of(more));
        return TestSupport.run(arguments.toArray(new String[0]));
    }

    static KbData read(Path folder) {
        try {
            return KbFolderReader.read(folder);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    static String text(Path file) {
        try {
            return new String(Files.readAllBytes(file), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /** Every Row identifier of a folder's tables, the required_rows table left out. */
    static Set<String> rowIds(KbData data) {
        Set<String> ids = new TreeSet<>();
        for (String name : data.tableNames()) {
            KbRows table = data.table(name);
            if (table.has("row_id") && !name.equals(SliceSchema.REQUIRED_ROWS)) {
                for (String[] row : table.rows) {
                    ids.add(table.get(row, "row_id"));
                }
            }
        }
        return ids;
    }

    /**
     * The identifiers a Slice points at that exist in the Knowledge Base and not in the Slice: through a
     * reference list (one identifier per line) or through a column that points at one of several tables.
     * Declared single-table links are checked by {@code SchemaTest.valueProblems}.
     */
    static List<String> danglingReferences(KbData slice, Set<String> knowledgeBaseIds) {
        Set<String> inSlice = rowIds(slice);
        List<String> problems = new ArrayList<>();
        for (String name : slice.tableNames()) {
            KbRows table = slice.table(name);
            for (String column : table.header) {
                boolean list = column.endsWith("_refs") || (name.equals("physical_joins")
                        && (column.equals("columns") || column.equals("referenced_columns")))
                        || (column.equals("columns") && List.of("physical_keys", "logical_keys", "logical_level_keys").contains(name))
                        || (name.equals("logical_levels") && (column.equals("child_level_ids") || column.equals("parent_level_ids")));
                boolean polymorphic = (name.equals("security_rls") && column.equals("subject_id"))
                        || (name.equals("dashboard_contents") && (column.equals("container_id") || column.equals("target_id")))
                        || (name.equals("analysis_filters") && (column.equals("owner_id") || column.equals("target_id")));
                if (!list && !polymorphic) {
                    continue;
                }
                for (String[] row : table.rows) {
                    for (String id : table.get(row, column).split("\n", -1)) {
                        if (!id.isEmpty() && knowledgeBaseIds.contains(id) && !inSlice.contains(id)) {
                            problems.add(name + "." + column + ": " + id);
                        }
                    }
                }
            }
        }
        return problems;
    }

    /** The packets of a Slice folder, unit id to text, in units.csv order. */
    static Map<String, String> packets(Path folder) {
        Map<String, String> packets = new LinkedHashMap<>();
        KbRows units = read(folder).table(SliceSchema.UNITS);
        for (String[] row : units.rows) {
            packets.put(units.get(row, "unit_id"), text(folder.resolve(units.get(row, "packet_file"))));
        }
        return packets;
    }

    /**
     * Checks the contract between slice and check-doc on a Slice folder: no packet over the manifest's limit, units.csv
     * agrees with the packet files, and every required row is cited in exactly one packet, the one
     * required_rows names. Returns {units, largest packet in lines, required rows}.
     */
    static int[] checkPackets(Path folder, String what) {
        KbData slice = read(folder);
        KbRows units = slice.table(SliceSchema.UNITS);
        Map<String, String> packets = packets(folder);
        int largest = 0;
        int position = 1;
        int limit = Integer.parseInt(slice.fact("max_packet_lines"));
        for (String[] row : units.rows) {
            String id = units.get(row, "unit_id");
            String text = packets.get(id);
            int lines = text.split("\n", -1).length - 1;
            Check.that(text.endsWith("\n"), what + " " + id + ": ends with a line feed");
            Check.that(lines <= limit, what + " " + id + ": " + lines + " lines, at most " + limit);
            Check.equal(Integer.toString(lines), units.get(row, "line_count"), what + " " + id + ": line_count");
            Check.equal(Integer.toString(position++), units.get(row, "position"), what + " " + id + ": position");
            Check.equal("packets/" + id + ".md", units.get(row, "packet_file"), what + " " + id + ": packet_file");
            String[] header = text.split("\n", 8);
            Check.that(header[0].startsWith("# "), what + " " + id + ": header title");
            Check.equal("Unit: " + id, header[1], what + " " + id + ": header unit id");
            Check.equal("Kind: " + units.get(row, "kind"), header[2], what + " " + id + ": header kind");
            Check.that(header[4].startsWith("Facts from: "), what + " " + id + ": header names the tables");
            largest = Math.max(largest, lines);
        }
        KbRows required = slice.table(SliceSchema.REQUIRED_ROWS);
        Map<String, Integer> perUnit = new LinkedHashMap<>();
        Set<String> seen = new TreeSet<>();
        // Two kinds of object can share an identifier text (a known limit of the identifiers); such a text
        // is then required once per table, and cited in exactly the packets of those rows.
        Map<String, List<String>> unitsOfId = new LinkedHashMap<>();
        for (String[] row : required.rows) {
            unitsOfId.computeIfAbsent(required.get(row, "row_id"), k -> new ArrayList<>()).add(required.get(row, "unit_id"));
        }
        for (String[] row : required.rows) {
            String id = required.get(row, "row_id");
            Check.that(seen.add(id + " in " + required.get(row, "table")), what + ": required row listed once: " + id);
            String citation = Citation.of(id);
            List<String> citedIn = new ArrayList<>();
            for (Map.Entry<String, String> packet : packets.entrySet()) {
                if (packet.getValue().contains(citation)) {
                    citedIn.add(packet.getKey());
                }
            }
            List<String> expectedUnits = new ArrayList<>();
            for (String unit : packets.keySet()) {
                if (unitsOfId.get(id).contains(unit)) {
                    expectedUnits.add(unit);
                }
            }
            int holders = 0;
            for (String name : slice.tableNames()) {
                KbRows held = slice.table(name);
                if (held.has("row_id") && !name.equals(SliceSchema.REQUIRED_ROWS)
                        && held.rows.stream().anyMatch(r -> held.get(r, "row_id").equals(id))) {
                    holders++;
                }
            }
            if (holders > 1) {
                // The same text also names a row of another table (a foreign key named like a column), which
                // its own packet may cite; the required row's packet must still be among them.
                Check.that(citedIn.containsAll(expectedUnits), what + ": packets citing required row " + id + ": " + citedIn);
            } else {
                Check.equal(expectedUnits, citedIn, what + ": packets citing required row " + id);
            }
            perUnit.merge(required.get(row, "unit_id"), 1, Integer::sum);
            KbRows table = slice.table(required.get(row, "table"));
            Check.that(table.rows.stream().anyMatch(r -> table.get(r, "row_id").equals(id)),
                    what + ": required row is a row of " + table.name + ": " + id);
        }
        for (String[] row : units.rows) {
            Check.equal(Integer.toString(perUnit.getOrDefault(units.get(row, "unit_id"), 0)), units.get(row, "required_rows"),
                    what + " " + units.get(row, "unit_id") + ": required_rows count");
        }
        return new int[] {units.rows.size(), largest, required.rows.size()};
    }
}
