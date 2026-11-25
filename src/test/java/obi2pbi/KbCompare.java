package obi2pbi;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import obi2pbi.in.kb.CsvReader;
import obi2pbi.in.kb.CsvTable;

/**
 * Compares a generated Knowledge Base with the reference one in {@code reference/knowledge-base/}, for one
 * estate. Used by every Epic 1 story.
 *
 * <p>How a table is compared:
 * <ul>
 * <li>The reference holds several estates; only rows whose {@code rpd_id} is the estate are used.</li>
 * <li>The reference's {@code _guarded} columns are ignored, and the apostrophe its writer put in front of
 * a guarded value is removed first, so values are compared as the source held them.</li>
 * <li>Rows are paired by key: {@code row_id} where the table has one; for {@code unparsed} and
 * {@code coverage} the columns that name the thing counted; otherwise the whole row. A key that occurs more
 * than once on either side is compared as whole rows.</li>
 * <li>Reported: rows missing from the generated table, extra rows in it, and differing cells of paired
 * rows; also columns that only one side has.</li>
 * <li>A difference is <b>explained</b> when it is exactly what one of the numbered deliberate differences
 * of {@code docs/port-guide/00-read-me-first.md} produces. That is never assumed from the column's name: a
 * stated transformation of the reference value must give the generated value (see {@link #explainCell} and
 * {@link #explainMissingErrors}, {@link #explainDoubledResolution}, {@link #explainsUndecodedName}), or the
 * estate's own files, read here a second time, must bear the generated value out ({@link #explainsWholeText},
 * {@link #explainsTreeColumns}, {@link #explainsSavedFilterRow}, {@link #explainsSavedFilterError},
 * {@link #explainsNowRead}; the reading is in {@link CatalogSource}). Explained differences are counted per
 * difference number and reported apart from the unexplained ones, which are always listed.</li>
 * </ul>
 * {@code manifest}, {@code schema} and {@code schema_tables} are skipped: the first differs by design and
 * the other two are not in the reference.
 *
 * <pre>
 * ./build.sh compare &lt;estate&gt; [table ...] [--kb &lt;folder&gt;] [--ref &lt;folder&gt;] [--source &lt;estate folder&gt;]
 *         [--where column=v1,v2] [--where-not column=value] [--show n] [--show-explained n]
 * </pre>
 * {@code --where} keeps the rows whose column holds one of the values; {@code --where-not} (one value per
 * option, repeatable) leaves out the rows whose column holds the value. Both limit both sides.
 * Without table names, every table in the generated manifest is compared, and reference tables with rows
 * for the estate that were not generated are listed. Exit code 0 when no difference is left unexplained,
 * 1 otherwise. {@code --source} is the estate folder that was extracted (default {@code estates/<estate>});
 * it is read only to check differences 7, 8, 9, 10, 19, 21 and 26 to 37. Difference 22 is checked against the two
 * {@code lineage} tables.
 */
public final class KbCompare {

    private static final String GUARDED = "_guarded";
    /** In front of a column name in a {@code where} map: keep the rows whose value is <b>not</b> listed. */
    static final String NOT = "!";
    private static final Set<String> SKIPPED = Set.of("manifest", "schema", "schema_tables");
    private static final Map<String, List<String>> KEYS = Map.of(
            "unparsed", List.of("rpd_id", "kind", "object_type", "detail"),
            "coverage", List.of("rpd_id", "layer", "object_type"));

    /** The outcome for one table. */
    public static final class Result {
        public final String table;
        public int referenceRows;
        public int generatedRows;
        public final List<String> columnsOnlyInReference = new ArrayList<>();
        public final List<String> columnsOnlyInGenerated = new ArrayList<>();
        /** One line per row of the reference that the generated table lacks. */
        public final List<String> missing = new ArrayList<>();
        /** One line per row of the generated table that the reference lacks. */
        public final List<String> extra = new ArrayList<>();
        /** One line per cell that differs between paired rows and that no deliberate difference explains. */
        public final List<String> differing = new ArrayList<>();
        /** Deliberate difference number to one line per differing cell it explains. */
        public final Map<Integer, List<String>> explained = new TreeMap<>();
        /** Deliberate difference number to one line per missing reference row it explains. */
        public final Map<Integer, List<String>> explainedMissing = new TreeMap<>();
        /** Deliberate difference number to one line per extra generated row it explains. */
        public final Map<Integer, List<String>> explainedExtra = new TreeMap<>();
        /** Deliberate difference number to the columns it adds to the generated table, every value checked. */
        public final Map<Integer, List<String>> explainedColumns = new TreeMap<>();
        public String problem = "";
        /** The columns both sides have, in the reference's order; the cells of every {@link Change#row}. */
        public List<String> columns = List.of();
        /** Paired rows equal in every shared column, and whole rows found on both sides. */
        public int identicalRows;
        /** Paired rows that differ, every differing cell explained by a deliberate difference. */
        public int rowsOnlyExplained;
        /** Paired rows with at least one differing cell that nothing explains. */
        public int rowsUnexplained;
        /** Every difference found, explained or not, as data; the report is written from this. */
        public final List<Change> changes = new ArrayList<>();

        Result(String table) {
            this.table = table;
        }

        /** True when nothing differs at all, explained or not. */
        public boolean same() {
            return nothingUnexplained() && explained.isEmpty() && explainedMissing.isEmpty()
                    && explainedExtra.isEmpty() && explainedColumns.isEmpty();
        }

        /** True when every difference found is explained by a deliberate difference. */
        public boolean nothingUnexplained() {
            return problem.isEmpty() && missing.isEmpty() && extra.isEmpty() && differing.isEmpty()
                    && columnsOnlyInReference.isEmpty() && columnsOnlyInGenerated.isEmpty();
        }

        public int explainedCells() {
            return count(explained);
        }

        public int explainedMissingRows() {
            return count(explainedMissing);
        }

        public int explainedExtraRows() {
            return count(explainedExtra);
        }

        private static int count(Map<Integer, List<String>> byNumber) {
            int total = 0;
            for (List<String> lines : byNumber.values()) {
                total += lines.size();
            }
            return total;
        }

        /** For example {@code difference 5: 2, difference 6: 14}; empty when the map is. */
        static String perNumber(Map<Integer, ? extends java.util.Collection<String>> byNumber) {
            List<String> parts = new ArrayList<>();
            for (Map.Entry<Integer, ? extends java.util.Collection<String>> entry : byNumber.entrySet()) {
                parts.add("difference " + entry.getKey() + ": " + entry.getValue().size());
            }
            return String.join(", ", parts);
        }

        public String summary() {
            if (!problem.isEmpty()) {
                return String.format("%-28s %s", table, problem);
            }
            String explainedText = "";
            if (!explained.isEmpty()) {
                explainedText += "  explained cells " + explainedCells() + " (" + perNumber(explained) + ")";
            }
            if (!explainedMissing.isEmpty()) {
                explainedText += "  explained missing rows " + explainedMissingRows() + " ("
                        + perNumber(explainedMissing) + ")";
            }
            if (!explainedExtra.isEmpty()) {
                explainedText += "  explained extra rows " + explainedExtraRows() + " ("
                        + perNumber(explainedExtra) + ")";
            }
            if (!explainedColumns.isEmpty()) {
                List<String> parts = new ArrayList<>();
                for (Map.Entry<Integer, List<String>> entry : explainedColumns.entrySet()) {
                    parts.add("difference " + entry.getKey() + ": " + String.join(", ", entry.getValue()));
                }
                explainedText += "  explained added columns (" + String.join("; ", parts) + ")";
            }
            return String.format("%-28s reference %6d  generated %6d  missing %5d  extra %5d  differing cells %5d%s%s",
                    table, referenceRows, generatedRows, missing.size(), extra.size(), differing.size(),
                    explainedText,
                    columnsOnlyInReference.isEmpty() && columnsOnlyInGenerated.isEmpty() ? ""
                            : "  columns only in reference " + columnsOnlyInReference + ", only in generated "
                                    + columnsOnlyInGenerated);
        }
    }

    /** One difference: a cell of a paired row, a row only one side has, or a column only one side has. */
    public static final class Change {
        public static final String CELL = "cell";
        public static final String MISSING = "missing";
        public static final String EXTRA = "extra";
        public static final String COLUMN = "column";

        /** The deliberate difference that explains it; 0 when nothing does. */
        public final int number;
        public final String kind;
        /** The row: the reference's for a cell or a missing row, the generated one for an extra row. */
        public final String[] row;
        /** The differing column, for a cell or a column; empty otherwise. */
        public final String column;
        public final String reference;
        public final String generated;

        Change(int number, String kind, String[] row, String column, String reference, String generated) {
            this.number = number;
            this.kind = kind;
            this.row = row;
            this.column = column;
            this.reference = reference;
            this.generated = generated;
        }
    }

    private KbCompare() {
    }

    public static void main(String[] args) throws IOException {
        System.exit(run(args, System.out));
    }

    static int run(String[] args, PrintStream out) throws IOException {
        String estate = null;
        Path kb = null;
        Path reference = Paths.get("reference", "knowledge-base");
        Path source = null;
        int show = 5;
        int showExplained = 0;
        List<String> tables = new ArrayList<>();
        Map<String, Set<String>> where = new LinkedHashMap<>();
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if (arg.startsWith("--")) {
                if (i + 1 >= args.length) {
                    out.println("error: " + arg + " needs a value");
                    return 2;
                }
                String value = args[++i];
                if (arg.equals("--kb")) {
                    kb = Paths.get(value);
                } else if (arg.equals("--ref")) {
                    reference = Paths.get(value);
                } else if (arg.equals("--source")) {
                    source = Paths.get(value);
                } else if (arg.equals("--show")) {
                    show = Integer.parseInt(value);
                } else if (arg.equals("--show-explained")) {
                    showExplained = Integer.parseInt(value);
                } else if (arg.equals("--where") && value.contains("=")) {
                    int cut = value.indexOf('=');
                    where.put(value.substring(0, cut),
                            new HashSet<>(Arrays.asList(value.substring(cut + 1).split(","))));
                } else if (arg.equals("--where-not") && value.contains("=")) {
                    // One value per option, not split on commas (a value may hold one); repeat the option.
                    int cut = value.indexOf('=');
                    where.computeIfAbsent(NOT + value.substring(0, cut), c -> new HashSet<>())
                            .add(value.substring(cut + 1));
                } else {
                    out.println("error: unknown option " + arg + " " + value);
                    return 2;
                }
            } else if (estate == null) {
                estate = arg;
            } else {
                tables.add(arg.endsWith(".csv") ? arg.substring(0, arg.length() - 4) : arg);
            }
        }
        if (estate == null) {
            out.println("usage: compare <estate> [table ...] [--kb <folder>] [--ref <folder>] "
                    + "[--source <estate folder>] [--where column=v1,v2] [--where-not column=value] [--show n] "
                    + "[--show-explained n]");
            return 2;
        }
        if (kb == null) {
            kb = Paths.get("kb", estate);
        }
        if (source == null) {
            source = Paths.get("estates", estate);
        }
        if (!Files.isRegularFile(kb.resolve("manifest.csv"))) {
            out.println("error: " + kb + " has no manifest.csv; run extract first, or give --kb <folder>");
            return 2;
        }
        boolean whole = tables.isEmpty();
        if (whole) {
            tables = manifestTables(kb);
        }
        out.println("comparing " + kb + " with " + reference + " for estate " + estate
                + (where.isEmpty() ? "" : " where " + where));
        boolean same = true;
        boolean nothingUnexplained = true;
        Map<Integer, List<String>> explainedInAll = new TreeMap<>();
        for (String table : tables) {
            if (SKIPPED.contains(table)) {
                out.println(String.format("%-28s skipped (not comparable with the reference)", table));
                continue;
            }
            Result result = compare(kb, reference, source, estate, table, where);
            out.println(result.summary());
            print(out, "  missing:   ", result.missing, show);
            print(out, "  extra:     ", result.extra, show);
            print(out, "  differing: ", result.differing, show);
            for (Map.Entry<Integer, List<String>> entry : result.explainedMissing.entrySet()) {
                print(out, "  explained by difference " + entry.getKey() + ", missing: ", entry.getValue(), showExplained);
                explainedInAll.computeIfAbsent(entry.getKey(), n -> new ArrayList<>()).addAll(entry.getValue());
            }
            for (Map.Entry<Integer, List<String>> entry : result.explainedExtra.entrySet()) {
                print(out, "  explained by difference " + entry.getKey() + ", extra: ", entry.getValue(), showExplained);
                explainedInAll.computeIfAbsent(entry.getKey(), n -> new ArrayList<>()).addAll(entry.getValue());
            }
            for (Map.Entry<Integer, List<String>> entry : result.explained.entrySet()) {
                print(out, "  explained by difference " + entry.getKey() + ": ", entry.getValue(), showExplained);
                explainedInAll.computeIfAbsent(entry.getKey(), n -> new ArrayList<>()).addAll(entry.getValue());
            }
            for (Map.Entry<Integer, List<String>> entry : result.explainedColumns.entrySet()) {
                explainedInAll.computeIfAbsent(entry.getKey(), n -> new ArrayList<>()).addAll(entry.getValue());
            }
            same = same && result.same();
            nothingUnexplained = nothingUnexplained && result.nothingUnexplained();
        }
        if (whole) {
            List<String> notGenerated = new ArrayList<>();
            for (String table : referenceTables(reference)) {
                if (!tables.contains(table) && !SKIPPED.contains(table)) {
                    int rows = estateRows(reference(reference.resolve(table + ".csv")), estate).size();
                    if (rows > 0) {
                        notGenerated.add(table + " (" + rows + ")");
                    }
                }
            }
            if (!notGenerated.isEmpty()) {
                out.println("not generated yet, reference rows for " + estate + ": " + String.join(", ", notGenerated));
            }
        }
        if (same) {
            out.println("result: no differences in the tables compared");
        } else if (nothingUnexplained) {
            out.println("result: no unexplained differences in the tables compared; explained by deliberate "
                    + "differences: " + Result.perNumber(explainedInAll));
        } else {
            out.println("result: UNEXPLAINED DIFFERENCES found"
                    + (explainedInAll.isEmpty() ? "" : "; also explained by deliberate differences: "
                            + Result.perNumber(explainedInAll)));
        }
        return nothingUnexplained ? 0 : 1;
    }

    private static void print(PrintStream out, String label, List<String> lines, int show) {
        for (int i = 0; i < lines.size() && i < show; i++) {
            out.println(label + lines.get(i).replace("\n", "\\n"));
        }
        if (lines.size() > show && show > 0) {
            out.println(label + "... and " + (lines.size() - show) + " more");
        }
    }

    /** As the six-argument form, with no source estate: differences 7, 8, 9, 10, 19, 21, 26 and 27 cannot then be checked or explained. */
    public static Result compare(Path kb, Path referenceFolder, String estate, String table,
            Map<String, Set<String>> where) throws IOException {
        return compare(kb, referenceFolder, null, estate, table, where);
    }

    /**
     * Compares one table of a generated Knowledge Base with the same-named reference table.
     *
     * @param source the estate folder both were extracted from, or null; needed only to check differences 7
     *               and 19
     */
    public static Result compare(Path kb, Path referenceFolder, Path source, String estate, String table,
            Map<String, Set<String>> where) throws IOException {
        Result result = new Result(table);
        Path generatedFile = kb.resolve(table + ".csv");
        Path referenceFile = referenceFolder.resolve(table + ".csv");
        if (!Files.isRegularFile(generatedFile)) {
            result.problem = "not in the generated Knowledge Base";
            return result;
        }
        if (!Files.isRegularFile(referenceFile)) {
            result.problem = "no reference table";
            return result;
        }
        CsvTable generated = CsvReader.read(generatedFile);
        CsvTable reference = reference(referenceFile);
        if (reference.indexOf("rpd_id") < 0 || generated.indexOf("rpd_id") < 0) {
            result.problem = "no rpd_id column, cannot be limited to one estate";
            return result;
        }

        List<String> shared = new ArrayList<>();
        for (String column : reference.header) {
            if (generated.indexOf(column) >= 0) {
                shared.add(column);
            } else {
                result.columnsOnlyInReference.add(column);
            }
        }
        for (String column : generated.header) {
            if (reference.indexOf(column) < 0) {
                result.columnsOnlyInGenerated.add(column);
            }
        }

        result.columns = shared;
        List<String[]> referenceRows = filter(reference, estateRows(reference, estate), where);
        List<String[]> generatedRows = filter(generated, estateRows(generated, estate), where);
        if (table.equals(FILTERS) && result.columnsOnlyInGenerated.equals(TREE_COLUMNS)
                && explainsTreeColumns(new Sides(kb, referenceFolder, source, estate), generated, generatedRows)) {
            result.columnsOnlyInGenerated.clear();
            result.explainedColumns.put(10, TREE_COLUMNS);
            for (String column : TREE_COLUMNS) {
                result.changes.add(new Change(10, Change.COLUMN, null, column, "", ""));
            }
        }
        Sides columnSides = new Sides(kb, referenceFolder, source, estate);
        if (table.equals("physical_joins") && result.columnsOnlyInGenerated.equals(REFERENCED_COLUMNS)
                && explainsReferencedColumns(columnSides, generated, generatedRows)) {
            result.columnsOnlyInGenerated.clear();
            result.explainedColumns.put(26, REFERENCED_COLUMNS);
            result.changes.add(new Change(26, Change.COLUMN, null, REFERENCED_COLUMNS.get(0), "", ""));
        }
        if (table.equals("security_rls") && result.columnsOnlyInGenerated.equals(ROLE_COLUMNS)
                && explainsRoleColumns(columnSides, generated, generatedRows)) {
            result.columnsOnlyInGenerated.clear();
            result.explainedColumns.put(27, ROLE_COLUMNS);
            for (String column : ROLE_COLUMNS) {
                result.changes.add(new Change(27, Change.COLUMN, null, column, "", ""));
            }
        }
        if (ADDED_COLUMNS.containsKey(table) && result.columnsOnlyInGenerated.equals(ADDED_COLUMNS.get(table))
                && explainsAddedColumns(columnSides, table, generated, generatedRows)) {
            result.columnsOnlyInGenerated.clear();
            for (String column : ADDED_COLUMNS.get(table)) {
                int number = ADDED_NUMBER.get(table + "." + column);
                result.explainedColumns.computeIfAbsent(number, n -> new ArrayList<>()).add(column);
                result.changes.add(new Change(number, Change.COLUMN, null, column, "", ""));
            }
        }
        for (String column : result.columnsOnlyInReference) {
            result.changes.add(new Change(0, Change.COLUMN, null, column, "present", "absent"));
        }
        for (String column : result.columnsOnlyInGenerated) {
            result.changes.add(new Change(0, Change.COLUMN, null, column, "absent", "present"));
        }
        result.referenceRows = referenceRows.size();
        result.generatedRows = generatedRows.size();

        List<String> key = KEYS.get(table);
        if (key == null) {
            key = shared.contains("row_id") ? List.of("row_id") : shared;
        }
        Map<String, List<String[]>> referenceByKey = group(reference, referenceRows, key, shared);
        Map<String, List<String[]>> generatedByKey = group(generated, generatedRows, key, shared);

        Sides sides = new Sides(kb, referenceFolder, source, estate);
        List<String[]> missingRows = new ArrayList<>();
        Set<String> keys = new TreeSet<>(referenceByKey.keySet());
        keys.addAll(generatedByKey.keySet());
        for (String k : keys) {
            List<String[]> ref = referenceByKey.getOrDefault(k, List.of());
            List<String[]> gen = generatedByKey.getOrDefault(k, List.of());
            if (ref.size() == 1 && gen.size() == 1) {
                boolean anyExplained = false;
                boolean anyUnexplained = false;
                for (int i = 0; i < shared.size(); i++) {
                    if (!ref.get(0)[i].equals(gen.get(0)[i])) {
                        String line = k.replace('\u0001', '|') + " [" + shared.get(i) + "] reference \""
                                + ref.get(0)[i] + "\" generated \"" + gen.get(0)[i] + "\"";
                        int number = explainCell(sides, table, shared, ref.get(0), gen.get(0), i);
                        if (number == 0) {
                            result.differing.add(line);
                            anyUnexplained = true;
                        } else {
                            result.explained.computeIfAbsent(number, n -> new ArrayList<>()).add(line);
                            anyExplained = true;
                        }
                        result.changes.add(new Change(number, Change.CELL, ref.get(0), shared.get(i),
                                ref.get(0)[i], gen.get(0)[i]));
                    }
                }
                if (anyUnexplained) {
                    result.rowsUnexplained++;
                } else if (anyExplained) {
                    result.rowsOnlyExplained++;
                } else {
                    result.identicalRows++;
                }
                continue;
            }
            // No pairing possible: compare as whole rows, each row matching at most one on the other side.
            List<String[]> unmatched = new ArrayList<>(gen);
            for (String[] row : ref) {
                String wanted = line(row);
                boolean found = false;
                for (int i = 0; i < unmatched.size() && !found; i++) {
                    if (line(unmatched.get(i)).equals(wanted)) {
                        unmatched.remove(i);
                        found = true;
                    }
                }
                if (!found) {
                    missingRows.add(row);
                } else {
                    result.identicalRows++;
                }
            }
            for (String[] row : unmatched) {
                int number = 0;
                if (table.equals("unparsed") && explainsExtraRedaction(sides, shared, row)) {
                    number = 19;
                } else if (table.equals("unparsed") && explainsUndecodedName(sides, shared, row)) {
                    number = 21;
                } else if (table.equals(FILTERS) && explainsSavedFilterRow(sides, shared, row)) {
                    number = 9;
                } else if (table.equals("errors") && explainsSavedFilterError(sides, shared, row)) {
                    number = 9;
                } else if (table.equals("coverage") && explainsKeyCoverage(sides, shared, row) != 0) {
                    number = explainsKeyCoverage(sides, shared, row);
                } else if (table.equals("coverage") && cell(shared, row, "object_type").equals(CatalogSource.EXPR)
                        && cell(shared, row, "parsed").equals("0") && !cell(shared, row, "skipped").equals("0")
                        && cell(shared, row, "skipped").equals(Integer.toString(savedFilterErrorRows(sides)))) {
                    // The marker row that only counts errors: every error it counts is explained by 9.
                    number = 9;
                }
                if (number == 0) {
                    result.extra.add(line(row));
                } else {
                    result.explainedExtra.computeIfAbsent(number, n -> new ArrayList<>()).add(line(row));
                }
                result.changes.add(new Change(number, Change.EXTRA, row, "", "", ""));
            }
        }
        Map<String[], Integer> explainedRows = new java.util.IdentityHashMap<>();
        if (table.equals("errors")) {
            for (String[] row : explainMissingErrors(sides, shared, missingRows)) {
                explainedRows.put(row, 5);
            }
            for (String[] row : explainDoubledResolution(sides, shared, missingRows, referenceRowsProjected(referenceByKey),
                    referenceRowsProjected(generatedByKey))) {
                explainedRows.putIfAbsent(row, 11);
            }
        } else if (table.equals("coverage")) {
            for (String[] row : explainMissingCoverage(sides, shared, missingRows)) {
                explainedRows.put(row, 5);
            }
        } else if (table.equals("unparsed")) {
            for (String[] row : missingRows) {
                int number = explainsNowRead(sides, shared, row, "0");
                if (number != 0) {
                    explainedRows.put(row, number);
                }
            }
        }
        for (String[] row : missingRows) {
            if (explainedRows.containsKey(row)) {
                result.explainedMissing.computeIfAbsent(explainedRows.get(row), n -> new ArrayList<>()).add(line(row));
            } else {
                result.missing.add(line(row));
            }
            result.changes.add(new Change(explainedRows.getOrDefault(row, 0), Change.MISSING, row, "", "", ""));
        }
        return result;
    }

    // ------------------------------------------------------------------ deliberate differences

    /** The marker under which a reference inside a formula that did not resolve is reported and counted. */
    private static final String EXPRESSION_REF = "ExpressionRef";
    private static final String UNRESOLVED = "unresolved_reference";

    /** The two Knowledge Bases being compared, for the explanations that must look beyond one cell. */
    static final class Sides {
        final Path kb;
        final Path reference;
        /** The estate folder both were extracted from, or null when it was not given. */
        final Path source;
        final String estate;

        Sides(Path kb, Path reference, Path source, String estate) {
            this.kb = kb;
            this.reference = reference;
            this.source = source;
            this.estate = estate;
        }
    }

    /**
     * Says which deliberate difference explains one differing cell, or 0 when none does.
     *
     * <p><b>Difference 6</b> (every column whose name ends in {@code _refs}): the reference joins the
     * identifiers with one space and leaves out a reference that did not resolve; the generated cell has
     * one identifier per line and an empty line for one that did not resolve. Explained when the
     * reference's list (see {@link #referenceList}) equals the generated lines with the empty ones removed.
     *
     * <p><b>Difference 5</b> (same columns): the reference's list lacks entries the generated one has,
     * because the reference resolved references in reading order. Explained when the reference's list is
     * the generated one with {@code n} entries taken out, the rest in the same order, and the reference's
     * {@code errors.csv} has at least {@code n} unresolved-reference rows for the object the row belongs
     * to. Such a cell is also in the new list form; it is counted once, under 5.
     *
     * <p><b>Difference 5</b> ({@code coverage.skipped} of the {@code ExpressionRef} row): explained when
     * the reference counts exactly as many more as {@link #explainMissingErrors} explains.
     *
     * <p><b>Difference 7</b> ({@code physical_joins.columns}): the reference holds the source's own ids,
     * the generated cell holds Row identifiers. Explained only by looking each id up in the source; see
     * {@link #explainsForeignKeyColumns}.
     *
     * <p><b>Difference 19</b> ({@code variables}: {@code redacted} and the three {@code expr_} columns): the
     * generated row withholds a value the reference wrote. See {@link #explainsRepeatedCredential}.
     *
     * <p><b>Difference 11</b> ({@code coverage.skipped} of the global filter view row, which only ever
     * counts errors): explained when the reference counts exactly twice what the generated table counts,
     * and the surplus is exactly the number of error rows {@link #explainDoubledResolution} explains, with
     * no other difference among the error rows of that element.
     */
    static int explainCell(Sides sides, String table, List<String> columns, String[] reference, String[] generated,
            int column) throws IOException {
        String name = columns.get(column);
        if (table.equals("physical_joins") && name.equals("columns")) {
            return explainsForeignKeyColumns(sides, reference[column], generated[column]) ? 7 : 0;
        }
        if (table.equals("physical_joins") && name.equals("table2_id")) {
            return explainsForeignKeyTarget(sides, columns, reference, generated[column]) ? 26 : 0;
        }
        if (table.equals("variables") && REDACTION_COLUMNS.contains(name)
                && explainsRepeatedCredential(sides, columns, reference, generated)) {
            return 19;
        }
        if (table.equals(REPORT_LINEAGE) && name.equals("reference_text")) {
            return explainsCopiedFormula(sides, columns, reference, generated, column) ? 8 : 0;
        }
        if (table.equals(REPORT_LINEAGE) && (name.equals("logical_column_id") || name.equals("physical_column_id"))) {
            return explainsLineageChoice(sides, columns, reference, generated, column) ? 22 : 0;
        }
        if (WHOLE_TEXT_COLUMNS.contains(table + "." + name)) {
            return explainsWholeText(sides, table, columns, reference, generated, column) ? 8 : 0;
        }
        if (table.equals("unparsed") && name.equals("occurrences")) {
            return explainsNowRead(sides, columns, reference, generated[column]);
        }
        if (table.equals("coverage") && name.equals("skipped") && columns.contains("object_type")
                && reference[columns.indexOf("object_type")].equals(CatalogSource.EXPR)) {
            try {
                int more = Integer.parseInt(generated[column]) - Integer.parseInt(reference[column]);
                return more > 0 && more == savedFilterErrorRows(sides) ? 9 : 0;
            } catch (NumberFormatException e) {
                return 0;
            }
        }
        if (name.endsWith("_refs")) {
            List<String> referenceList = referenceList(reference[column], sides.estate);
            if (referenceList == null) {
                return 0;
            }
            List<String> generatedList = generatedList(generated[column]);
            for (String entry : generatedList) {
                if (!entry.startsWith(sides.estate + "::")) {
                    return 0;
                }
            }
            if (referenceList.equals(generatedList)) {
                return 6;
            }
            int taken = generatedList.size() - referenceList.size();
            int rowId = columns.indexOf("row_id");
            if (taken > 0 && rowId >= 0 && isSubsequence(referenceList, generatedList)
                    && unresolvedInReference(sides, reference[rowId]) >= taken) {
                return 5;
            }
            return 0;
        }
        if (table.equals("coverage") && name.equals("skipped") && columns.contains("object_type")
                && reference[columns.indexOf("object_type")].equals(GLOBAL_FILTER_VIEW)) {
            try {
                int ref = Integer.parseInt(reference[column]);
                int gen = Integer.parseInt(generated[column]);
                return gen > 0 && ref == 2 * gen && ref - gen == doubledErrorRows(sides) ? 11 : 0;
            } catch (NumberFormatException e) {
                return 0;
            }
        }
        if (table.equals("coverage") && name.equals("skipped") && columns.contains("object_type")
                && reference[columns.indexOf("object_type")].equals(EXPRESSION_REF)) {
            try {
                int fewer = Integer.parseInt(reference[column]) - Integer.parseInt(generated[column]);
                return fewer > 0 && fewer == explainedErrorRows(sides) ? 5 : 0;
            } catch (NumberFormatException e) {
                return 0;
            }
        }
        return 0;
    }

    /**
     * Splits a reference {@code _refs} cell into its identifiers, or returns null when it is not a list of
     * identifiers of this estate.
     *
     * <p>The cell is identifiers joined by single spaces, and identifiers contain spaces, so it cannot be
     * split on spaces. It can be split soundly all the same: every identifier starts with
     * {@code <estate>::<layer>::}, and no part of an identifier may contain {@code ::} (both programs
     * refuse such a name), so that prefix occurs in the cell only where an identifier starts. The cell is
     * cut at each space that is followed by the prefix. A name that ends in a space keeps it.
     */
    static List<String> referenceList(String cell, String estate) {
        List<String> entries = new ArrayList<>();
        if (cell.isEmpty()) {
            return entries;
        }
        java.util.regex.Pattern start = java.util.regex.Pattern.compile(
                java.util.regex.Pattern.quote(estate) + "::(?:bmm|cat|phys|pres|sec|var)::");
        java.util.regex.Matcher starts = start.matcher(cell);
        List<Integer> at = new ArrayList<>();
        while (starts.find()) {
            at.add(starts.start());
        }
        if (at.isEmpty() || at.get(0) != 0) {
            return null;
        }
        for (int i = 0; i < at.size(); i++) {
            boolean last = i + 1 == at.size();
            int end = last ? cell.length() : at.get(i + 1) - 1;
            if (!last && cell.charAt(end) != ' ') {
                return null;
            }
            entries.add(cell.substring(at.get(i), end));
        }
        return entries;
    }

    /** The resolved entries of a generated {@code _refs} cell: its lines, the empty ones left out. */
    static List<String> generatedList(String cell) {
        List<String> entries = new ArrayList<>();
        for (String line : cell.split("\n", -1)) {
            if (!line.isEmpty()) {
                entries.add(line);
            }
        }
        return entries;
    }

    private static boolean isSubsequence(List<String> shorter, List<String> longer) {
        int next = 0;
        for (String entry : longer) {
            if (next < shorter.size() && shorter.get(next).equals(entry)) {
                next++;
            }
        }
        return next == shorter.size();
    }

    /** True when the row belongs to the object: it is the object's row, or one made from it with {@code #}. */
    private static boolean ownedBy(String rowId, String owner) {
        return rowId.equals(owner) || rowId.startsWith(owner + "#");
    }

    /** How many unresolved-reference rows the reference's errors table has for the object a row belongs to. */
    private static int unresolvedInReference(Sides sides, String rowId) throws IOException {
        Path file = sides.reference.resolve("errors.csv");
        if (!Files.isRegularFile(file)) {
            return 0;
        }
        CsvTable errors = reference(file);
        int count = 0;
        for (String[] row : estateRows(errors, sides.estate)) {
            if (errors.value(row, "kind").equals(UNRESOLVED) && errors.value(row, "object_type").equals(EXPRESSION_REF)
                    && ownedBy(rowId, errors.value(row, "source_id"))) {
                count++;
            }
        }
        return count;
    }

    /**
     * Picks out the missing {@code errors} rows that difference 5 explains: rows saying a reference inside
     * a formula of object X did not resolve, where the generated Knowledge Base does resolve it.
     *
     * <p>Checked by counting, over every table with {@code _refs} columns, the resolved entries on the rows
     * that belong to X. When the generated side has {@code n} more than the reference, up to {@code n}
     * missing error rows for X are explained. A row for an object with no such gain stays unexplained.
     */
    static List<String[]> explainMissingErrors(Sides sides, List<String> columns, List<String[]> missingRows)
            throws IOException {
        List<String[]> explained = new ArrayList<>();
        int kind = columns.indexOf("kind");
        int type = columns.indexOf("object_type");
        int source = columns.indexOf("source_id");
        if (kind < 0 || type < 0 || source < 0) {
            return explained;
        }
        Map<String, List<String[]>> byObject = new TreeMap<>();
        for (String[] row : missingRows) {
            if (row[kind].equals(UNRESOLVED) && row[type].equals(EXPRESSION_REF)) {
                byObject.computeIfAbsent(row[source], o -> new ArrayList<>()).add(row);
            }
        }
        for (Map.Entry<String, List<String[]>> entry : byObject.entrySet()) {
            int gain = resolvedEntries(sides, entry.getKey(), false) - resolvedEntries(sides, entry.getKey(), true);
            List<String[]> rows = entry.getValue();
            for (int i = 0; i < rows.size() && i < gain; i++) {
                explained.add(rows.get(i));
            }
        }
        return explained;
    }

    /**
     * The missing {@code coverage} row of the marker type {@code ExpressionRef} (which only ever counts
     * errors) is explained by difference 5 when every error it counts is.
     */
    private static List<String[]> explainMissingCoverage(Sides sides, List<String> columns, List<String[]> missingRows)
            throws IOException {
        List<String[]> explained = new ArrayList<>();
        int type = columns.indexOf("object_type");
        int parsed = columns.indexOf("parsed");
        int skipped = columns.indexOf("skipped");
        for (String[] row : missingRows) {
            if (type >= 0 && parsed >= 0 && skipped >= 0 && row[type].equals(EXPRESSION_REF) && row[parsed].equals("0")
                    && row[skipped].equals(Integer.toString(explainedErrorRows(sides)))) {
                explained.add(row);
            }
        }
        return explained;
    }

    /** How many of the reference's unresolved-reference rows inside formulas difference 5 explains. */
    private static int explainedErrorRows(Sides sides) throws IOException {
        if (!Files.isRegularFile(sides.kb.resolve("errors.csv"))
                || !Files.isRegularFile(sides.reference.resolve("errors.csv"))) {
            return 0;
        }
        Result errors = compare(sides.kb, sides.reference, sides.source, sides.estate, "errors",
                Map.of("object_type", Set.of(EXPRESSION_REF)));
        if (!errors.extra.isEmpty()) {
            return 0;
        }
        return errors.explainedMissingRows();
    }

    /** The number of resolved entries in every {@code _refs} cell of the rows that belong to an object. */
    private static int resolvedEntries(Sides sides, String owner, boolean inReference) throws IOException {
        Path folder = inReference ? sides.reference : sides.kb;
        int total = 0;
        for (String name : referenceTables(folder)) {
            CsvTable table = inReference ? reference(folder.resolve(name + ".csv")) : CsvReader.read(folder.resolve(name + ".csv"));
            int rowId = table.indexOf("row_id");
            if (rowId < 0 || table.indexOf("rpd_id") < 0) {
                continue;
            }
            for (int column = 0; column < table.header.size(); column++) {
                if (!table.header.get(column).endsWith("_refs")) {
                    continue;
                }
                for (String[] row : estateRows(table, sides.estate)) {
                    if (ownedBy(row[rowId], owner)) {
                        List<String> entries = inReference ? referenceList(row[column], sides.estate)
                                : generatedList(row[column]);
                        total += entries == null ? 0 : entries.size();
                    }
                }
            }
        }
        return total;
    }

    // ------------------------------------------------------------------ differences 8, 9 and 10

    static final String FILTERS = "analysis_filters";
    private static final List<String> TREE_COLUMNS = List.of("parent_id", "depth");
    /** The columns that hold the text of an expression element of the Web Catalog. */
    private static final Set<String> WHOLE_TEXT_COLUMNS = Set.of("analysis_columns.formula",
            "analysis_filters.expr_text", "analysis_prompts.column_formula", "saved_filters.expr_text");
    private static final Set<String> RESOLVED = Set.of("exact", "case_normalised");
    private static final Set<String> NOT_RESOLVED = Set.of("unresolved", "ambiguous");

    /** A catalog Row identifier taken apart: the object's decoded path and the part from the last {@code #}. */
    private static String[] objectAndSuffix(Sides sides, String rowId, boolean hasSuffix) {
        String prefix = sides.estate + "::cat::";
        if (!rowId.startsWith(prefix)) {
            return null;
        }
        String rest = rowId.substring(prefix.length());
        if (!hasSuffix) {
            return new String[] {rest, ""};
        }
        int cut = rest.lastIndexOf('#');
        return cut < 0 ? null : new String[] {rest.substring(0, cut), rest.substring(cut)};
    }

    /** What the estate's own file says about the object a row belongs to; null when it cannot be read. */
    private static CatalogSource.Facts sourceFacts(Sides sides, String decodedPath) throws IOException {
        CatalogSource catalog = CatalogSource.of(sides.source);
        return catalog == null ? null : catalog.object(decodedPath);
    }

    /**
     * <b>Difference 8</b>: true when a formula or filter text differs only in that the reference stops at
     * the first nested element and the generated cell goes on.
     *
     * <p>Checked against the estate's own file, read here with the JDK's DOM parser: the element the row
     * stands for is found by the numbering rules. The reference value must be exactly its text before the
     * first child element, and so a prefix of the generated value. The generated value must be exactly:
     * for a column or prompt formula, the element's whole text content; for a filter row and for a saved
     * filter's outermost expression, the element's own text, which is its text content with every nested
     * expression element (each a row of its own) left out and the text after them kept. A generated value
     * that is longer but is not that text (the whole text on a filter row, say), or a reference value that
     * is not the source's leading text, is not explained. Without the estate folder nothing is.
     */
    static boolean explainsWholeText(Sides sides, String table, List<String> columns, String[] reference,
            String[] generated, int column) throws IOException {
        int rowId = columns.indexOf("row_id");
        String ref = reference[column];
        String gen = generated[column];
        if (rowId < 0 || !gen.startsWith(ref) || gen.length() == ref.length()) {
            return false;
        }
        String[] parts = objectAndSuffix(sides, generated[rowId], !table.equals("saved_filters"));
        CatalogSource.Facts facts = parts == null ? null : sourceFacts(sides, parts[0]);
        if (facts == null) {
            return false;
        }
        String[] texts;
        if (table.equals(FILTERS)) {
            CatalogSource.Expr expr = facts.filterRows.get(parts[1]);
            texts = expr == null ? null : new String[] {expr.whole, expr.own};
        } else {
            texts = facts.texts.get(parts[1]);
        }
        return texts != null && texts[0].equals(gen) && texts[1].equals(ref);
    }

    static final String REPORT_LINEAGE = "report_lineage";

    /**
     * <b>Difference 8</b>, carried into {@code report_lineage.reference_text}: that cell is a copy of the
     * report column's formula. Explained when, on each side, it equals {@code analysis_columns.formula} of
     * the row {@code analysis_column_id} names, and the difference between those two formulas is itself
     * explained by difference 8 (see {@link #explainsWholeText}).
     */
    static boolean explainsCopiedFormula(Sides sides, List<String> columns, String[] reference, String[] generated,
            int column) throws IOException {
        String columnId = cell(columns, generated, "analysis_column_id");
        if (columnId.isEmpty() || !columnId.equals(cell(columns, reference, "analysis_column_id"))) {
            return false;
        }
        String[] referenceFormula = formulaOf(referenceTable(sides, "analysis_columns"), sides.estate, columnId);
        String[] generatedFormula = formulaOf(generatedTable(sides, "analysis_columns"), sides.estate, columnId);
        if (referenceFormula == null || generatedFormula == null || !referenceFormula[1].equals(reference[column])
                || !generatedFormula[1].equals(generated[column])) {
            return false;
        }
        return explainsWholeText(sides, "analysis_columns", List.of("row_id", "formula"), referenceFormula,
                generatedFormula, 1);
    }

    /** Row identifier and formula of one report column, or null when the table has not exactly one such row. */
    private static String[] formulaOf(CsvTable table, String estate, String rowId) {
        if (table.indexOf("row_id") < 0 || table.indexOf("formula") < 0 || table.indexOf("rpd_id") < 0) {
            return null;
        }
        String[] found = null;
        for (String[] row : estateRows(table, estate)) {
            if (table.value(row, "row_id").equals(rowId)) {
                if (found != null) {
                    return null;
                }
                found = new String[] {rowId, table.value(row, "formula")};
            }
        }
        return found;
    }

    /**
     * <b>Difference 22</b> ({@code report_lineage.logical_column_id} and {@code physical_column_id}): where
     * repository lineage has several rows for the presentation column, the generated row is linked through
     * the first of them in {@code lineage.csv} order and the reference through another.
     *
     * <p>Explained when both sides name the same presentation column; the generated {@code lineage} has at
     * least two rows for it; the generated cell is the value of the row with the smallest Row identifier
     * (worked out here, not taken from the file's order); and the reference cell is the value of one of the
     * other rows of the reference's own {@code lineage}. A value found on no lineage row of that
     * presentation column, or a generated value from a row that is not the first, is not explained.
     */
    static boolean explainsLineageChoice(Sides sides, List<String> columns, String[] reference, String[] generated,
            int column) throws IOException {
        String name = columns.get(column);
        String presentationId = cell(columns, generated, "presentation_column_id");
        if (presentationId.isEmpty() || !presentationId.equals(cell(columns, reference, "presentation_column_id"))) {
            return false;
        }
        List<String[]> ours = lineageRows(generatedTable(sides, "lineage"), sides.estate, presentationId, name);
        List<String[]> theirs = lineageRows(referenceTable(sides, "lineage"), sides.estate, presentationId, name);
        if (ours.size() < 2 || theirs.size() < 2) {
            return false;
        }
        String[] first = ours.get(0);
        for (String[] row : ours) {
            if (obi2pbi.core.Text.compareCodePoints(row[0], first[0]) < 0) {
                first = row;
            }
        }
        if (!first[1].equals(generated[column])) {
            return false;
        }
        for (String[] row : theirs) {
            if (!row[0].equals(first[0]) && row[1].equals(reference[column])) {
                return true;
            }
        }
        return false;
    }

    /** Row identifier and one column's value of every lineage row of a presentation column. */
    private static List<String[]> lineageRows(CsvTable lineage, String estate, String presentationId, String column) {
        List<String[]> rows = new ArrayList<>();
        if (lineage.indexOf("row_id") < 0 || lineage.indexOf("presentation_column_id") < 0
                || lineage.indexOf(column) < 0 || lineage.indexOf("rpd_id") < 0) {
            return rows;
        }
        for (String[] row : estateRows(lineage, estate)) {
            if (lineage.value(row, "presentation_column_id").equals(presentationId)) {
                rows.add(new String[] {lineage.value(row, "row_id"), lineage.value(row, column)});
            }
        }
        return rows;
    }

    private static final Map<String, CsvTable> REFERENCE = new java.util.HashMap<>();

    /** A table of the reference Knowledge Base, read once; an empty table when the file is not there. */
    private static CsvTable referenceTable(Sides sides, String table) throws IOException {
        Path file = sides.reference.resolve(table + ".csv").toAbsolutePath();
        String key = file + "@" + (Files.isRegularFile(file) ? Files.getLastModifiedTime(file).toMillis() + "/" + Files.size(file) : 0);
        CsvTable known = REFERENCE.get(key);
        if (known == null) {
            known = Files.isRegularFile(file) ? reference(file) : new CsvTable(List.of(), List.of());
            REFERENCE.put(key, known);
        }
        return known;
    }

    private static final Map<String, CsvTable> GENERATED = new java.util.HashMap<>();

    private static CsvTable generatedTable(Sides sides, String table) throws IOException {
        Path file = sides.kb.resolve(table + ".csv").toAbsolutePath();
        String key = file + "@" + (Files.isRegularFile(file) ? Files.getLastModifiedTime(file).toMillis() + "/" + Files.size(file) : 0);
        CsvTable known = GENERATED.get(key);
        if (known == null) {
            known = Files.isRegularFile(file) ? CsvReader.read(file) : new CsvTable(List.of(), List.of());
            GENERATED.put(key, known);
        }
        return known;
    }

    /**
     * <b>Difference 10</b>: true when the two columns the generated {@code analysis_filters} adds,
     * {@code parent_id} and {@code depth}, are right on <b>every</b> row compared.
     *
     * <p>For each row of a report or a saved filter, the enclosing expression and the depth are worked out
     * again from the estate's own file (the nearest enclosing expression element below the same filter
     * element, and the number of them) and must be what the row says. A dashboard prompt row has no tree:
     * it must have no parent and depth 0. One wrong value, an object that cannot be read, or no estate
     * folder, and the columns stay listed as unexplained.
     */
    static boolean explainsTreeColumns(Sides sides, CsvTable generated, List<String[]> rows) throws IOException {
        if (sides.source == null || generated.indexOf("row_id") < 0 || generated.indexOf("scope") < 0) {
            return false;
        }
        for (String[] row : rows) {
            String parent = generated.value(row, "parent_id");
            String depth = generated.value(row, "depth");
            if (generated.value(row, "scope").equals("dashboard")) {
                if (!parent.isEmpty() || !depth.equals("0")) {
                    return false;
                }
                continue;
            }
            String[] parts = objectAndSuffix(sides, generated.value(row, "row_id"), true);
            CatalogSource.Facts facts = parts == null ? null : sourceFacts(sides, parts[0]);
            CatalogSource.Expr expr = facts == null ? null : facts.filterRows.get(parts[1]);
            if (expr == null) {
                return false;
            }
            String expected = expr.parent.isEmpty() ? "" : sides.estate + "::cat::" + parts[0] + expr.parent;
            if (!parent.equals(expected) || !depth.equals(Integer.toString(expr.depth))) {
                return false;
            }
        }
        return true;
    }

    /**
     * <b>Difference 9</b>: true when an extra {@code analysis_filters} row is one expression of a saved
     * filter, written as the source has it.
     *
     * <p>The row must have scope {@code saved_filter} and belong to an object whose file, read here, is a
     * saved filter. Its number must be the expression's place among the expressions below the object's
     * filter elements; operator, type, path, the database-function flag and the text (own text, as
     * difference 8) must be the source's. A resolution is expected exactly when the expression is a
     * saved-filter reference with a path, and a target only when it resolved, at the path the reference
     * names. And the generated table must hold exactly as many rows for that saved filter as it has
     * expressions, so none was dropped. Anything else is not explained.
     */
    static boolean explainsSavedFilterRow(Sides sides, List<String> columns, String[] row) throws IOException {
        if (sides.source == null || !cell(columns, row, "scope").equals("saved_filter")) {
            return false;
        }
        String[] parts = objectAndSuffix(sides, cell(columns, row, "row_id"), true);
        CatalogSource.Facts facts = parts == null ? null : sourceFacts(sides, parts[0]);
        if (facts == null || !facts.rootTag.equals(CatalogSource.SAVED_FILTER)) {
            return false;
        }
        CatalogSource.Expr expr = facts.filterRows.get(parts[1]);
        String owner = sides.estate + "::cat::" + parts[0];
        if (expr == null || !parts[1].equals("#f" + cell(columns, row, "position"))
                || !cell(columns, row, "owner_id").equals(owner)
                || !cell(columns, row, "op").equals(expr.op)
                || !cell(columns, row, "expr_type").equals(expr.type)
                || !cell(columns, row, "expr_text").equals(expr.whole)
                || !cell(columns, row, "reference").equals(expr.path)
                || !cell(columns, row, "uses_eval").equals(Boolean.toString(expr.usesEval))) {
            return false;
        }
        String resolution = cell(columns, row, "resolution");
        String target = cell(columns, row, "target_id");
        boolean attempted = expr.type.equals("sawx:savedFilter") && !expr.path.isEmpty();
        if (!attempted) {
            if (!resolution.isEmpty() || !target.isEmpty()) {
                return false;
            }
        } else if (NOT_RESOLVED.contains(resolution)) {
            if (!target.isEmpty()) {
                return false;
            }
        } else if (RESOLVED.contains(resolution)) {
            String named = sides.estate + "::cat::" + pathBelowRoot(expr.path, parts[0]);
            if (resolution.equals("exact") ? !target.equals(named)
                    : target.equals(named) || !target.toLowerCase().equals(named.toLowerCase())) {
                return false;
            }
        } else {
            return false;
        }
        int rows = 0;
        CsvTable filters = generatedTable(sides, FILTERS);
        if (filters.indexOf("scope") < 0 || filters.indexOf("owner_id") < 0) {
            return false;
        }
        for (String[] other : filters.rows) {
            if (filters.value(other, "scope").equals("saved_filter") && filters.value(other, "owner_id").equals(owner)) {
                rows++;
            }
        }
        return rows == facts.filterRows.size();
    }

    /** A reference as a path below the catalog root; written here a second time on purpose. */
    private static String pathBelowRoot(String reference, String ownerPath) {
        if (reference.startsWith("/shared/")) {
            return reference.substring("/shared/".length());
        }
        int cut = ownerPath.lastIndexOf('/');
        return cut < 0 || reference.startsWith("/") ? reference : ownerPath.substring(0, cut + 1) + reference;
    }

    private static String cell(List<String> columns, String[] row, String column) {
        int index = columns.indexOf(column);
        return index < 0 ? "" : row[index];
    }

    /**
     * <b>Difference 9</b>: true when an extra {@code errors} row reports a reference from one saved filter
     * to another that did not resolve. The reference implementation never resolves these, because it
     * reads only the outermost expression of a saved filter.
     *
     * <p>Explained only when the row is about an expression element, its source is the path of a saved
     * filter in the estate's files, the generated {@code analysis_filters} has a row of that saved filter
     * that did not resolve and whose reference the reason quotes, that row is itself what the source says
     * ({@link #explainsSavedFilterRow}), and the saved filter has exactly as many such rows as the generated
     * {@code errors} has rows for it.
     */
    static boolean explainsSavedFilterError(Sides sides, List<String> columns, String[] row) throws IOException {
        String source = cell(columns, row, "source_id");
        if (sides.source == null || !cell(columns, row, "object_type").equals(CatalogSource.EXPR)
                || !NOT_RESOLVED.contains(cell(columns, row, "kind").replace("_reference", ""))) {
            return false;
        }
        CatalogSource.Facts facts = sourceFacts(sides, source);
        if (facts == null || !facts.rootTag.equals(CatalogSource.SAVED_FILTER)) {
            return false;
        }
        String owner = sides.estate + "::cat::" + source;
        CsvTable filters = generatedTable(sides, FILTERS);
        CsvTable generatedErrors = generatedTable(sides, "errors");
        if (!filters.header.containsAll(List.of("scope", "owner_id", "resolution", "reference"))
                || !generatedErrors.header.containsAll(List.of("rpd_id", "source_id", "object_type"))) {
            return false;
        }
        int unresolved = 0;
        boolean quoted = false;
        for (String[] filter : filters.rows) {
            if (!filters.value(filter, "owner_id").equals(owner) || !filters.value(filter, "scope").equals("saved_filter")
                    || !NOT_RESOLVED.contains(filters.value(filter, "resolution"))) {
                continue;
            }
            unresolved++;
            if (cell(columns, row, "reason").startsWith("'" + filters.value(filter, "reference") + "' ")
                    && explainsSavedFilterRow(sides, filters.header, filter)) {
                quoted = true;
            }
        }
        int errors = 0;
        for (String[] error : generatedErrors.rows) {
            if (generatedErrors.value(error, "source_id").equals(source)
                    && generatedErrors.value(error, "object_type").equals(CatalogSource.EXPR)
                    && generatedErrors.value(error, "rpd_id").equals(sides.estate)) {
                errors++;
            }
        }
        return quoted && unresolved == errors;
    }

    /**
     * How many extra error rows about expression elements difference 9 explains; 0 when any other
     * difference is left among the error rows of that element.
     */
    private static int savedFilterErrorRows(Sides sides) throws IOException {
        if (!Files.isRegularFile(sides.kb.resolve("errors.csv"))
                || !Files.isRegularFile(sides.reference.resolve("errors.csv"))) {
            return 0;
        }
        Result errors = compare(sides.kb, sides.reference, sides.source, sides.estate, "errors",
                Map.of("object_type", Set.of(CatalogSource.EXPR)));
        if (!errors.extra.isEmpty() || !errors.missing.isEmpty() || !errors.differing.isEmpty()) {
            return 0;
        }
        return errors.explainedExtra.getOrDefault(9, List.of()).size();
    }

    private static final Map<String, Map<String, CatalogSource.Gain>> NOW_READ = new java.util.HashMap<>();

    /**
     * <b>Differences 8 and 9</b>, in {@code unparsed}: says which of them explains a count of unread
     * content that is lower in the generated table (or gone from it), or 0 when neither does.
     *
     * <p>Taking an expression whole (8) reads the text of the elements nested in it; writing every
     * expression of a saved filter (9) reads the operator, path, type and text of each. The reference
     * reported those as unread. The number that must have gone is counted here from the estate's own
     * files, over every report or saved filter the generated Knowledge Base lists: text of elements
     * strictly inside a captured expression, and the attributes and text of saved-filter expressions other
     * than what the reference read of the outermost one. The reference count less the generated count must
     * be exactly that number. Any other change of a count is not explained.
     *
     * @param generatedCount the generated occurrences, {@code "0"} when the generated table has no such row
     */
    static int explainsNowRead(Sides sides, List<String> columns, String[] reference, String generatedCount)
            throws IOException {
        String type = cell(columns, reference, "object_type");
        int followed = explainsFollowedReference(sides, columns, reference, generatedCount);
        if (followed != 0) {
            return followed;
        }
        int keys = explainsKeysNowRead(sides, columns, reference, generatedCount);
        if (keys != 0) {
            return keys;
        }
        boolean report = type.equals(CatalogSource.REPORT);
        if (sides.source == null || CatalogSource.of(sides.source) == null
                || (!report && !type.equals(CatalogSource.SAVED_FILTER))) {
            return 0;
        }
        String cacheKey = sides.kb.toAbsolutePath() + "|" + sides.source.toAbsolutePath() + "|" + sides.estate + "|" + type;
        Map<String, CatalogSource.Gain> gains = NOW_READ.get(cacheKey);
        if (gains == null) {
            gains = new java.util.HashMap<>();
            CsvTable objects = generatedTable(sides, report ? "analyses" : "saved_filters");
            if (objects.indexOf("catalog_path") >= 0) {
                for (String[] object : estateRows(objects, sides.estate)) {
                    CatalogSource.Facts facts = sourceFacts(sides, objects.value(object, "catalog_path"));
                    if (facts == null || !facts.rootTag.equals(type)) {
                        continue;
                    }
                    for (Map.Entry<String, CatalogSource.Gain> gain : facts.gains.entrySet()) {
                        gains.computeIfAbsent(gain.getKey(), k -> new CatalogSource.Gain(gain.getValue().difference)).count
                                += gain.getValue().count;
                    }
                }
            }
            NOW_READ.put(cacheKey, gains);
        }
        CatalogSource.Gain gain = gains.get(cell(columns, reference, "kind") + "|" + cell(columns, reference, "detail"));
        try {
            int fewer = Integer.parseInt(cell(columns, reference, "occurrences")) - Integer.parseInt(generatedCount);
            return gain != null && fewer > 0 && fewer == gain.count ? gain.difference : 0;
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    // ------------------------------------------------------------------ difference 11

    /** The element by which a dashboard page refers to a dashboard prompt. */
    static final String GLOBAL_FILTER_VIEW = "{com.siebel.analytics.web/dashboard/v1.1}globalFilterView";
    private static final Set<String> RESOLUTION_ERRORS = Set.of(UNRESOLVED, "ambiguous_reference");

    private static List<String[]> referenceRowsProjected(Map<String, List<String[]>> byKey) {
        List<String[]> rows = new ArrayList<>();
        for (List<String[]> group : byKey.values()) {
            rows.addAll(group);
        }
        return rows;
    }

    /**
     * Picks out the missing {@code errors} rows that difference 11 explains.
     *
     * <p>The reference resolves each global filter view of a page twice, so every error about one is
     * written twice, as two identical rows. The generated Knowledge Base resolves it once. A missing row is
     * explained only when all of this holds:
     * <ul>
     * <li>it reports an unresolved or ambiguous reference of a global filter view element;</li>
     * <li>the reference has exactly twice as many copies of that very row as the generated table, and the
     * generated table has at least one;</li>
     * <li>the generated {@code dashboard_contents} has, for the page the row names, exactly as many global
     * filter view placements that did not resolve as the generated {@code errors} has rows for that page
     * and element. So each generated error stands for one placement, and none was dropped.</li>
     * </ul>
     * Anything else stays unexplained: a row the generated table lacks altogether, three copies against
     * one, the same pattern on another element.
     */
    static List<String[]> explainDoubledResolution(Sides sides, List<String> columns, List<String[]> missingRows,
            List<String[]> referenceRows, List<String[]> generatedRows) throws IOException {
        List<String[]> explained = new ArrayList<>();
        int kind = columns.indexOf("kind");
        int type = columns.indexOf("object_type");
        int source = columns.indexOf("source_id");
        if (kind < 0 || type < 0 || source < 0 || missingRows.isEmpty()) {
            return explained;
        }
        Map<String, Integer> inReference = copies(referenceRows);
        Map<String, Integer> inGenerated = copies(generatedRows);
        Map<String, Integer> generatedPerPage = new TreeMap<>();
        for (String[] row : generatedRows) {
            if (row[type].equals(GLOBAL_FILTER_VIEW) && RESOLUTION_ERRORS.contains(row[kind])) {
                generatedPerPage.merge(row[source], 1, Integer::sum);
            }
        }
        Map<String, Integer> placements = unresolvedGlobalFilterPlacements(sides);
        Map<String, Integer> taken = new TreeMap<>();
        for (String[] row : missingRows) {
            if (!row[type].equals(GLOBAL_FILTER_VIEW) || !RESOLUTION_ERRORS.contains(row[kind])) {
                continue;
            }
            String line = line(row);
            int ref = inReference.getOrDefault(line, 0);
            int gen = inGenerated.getOrDefault(line, 0);
            String page = sides.estate + "::cat::" + row[source];
            if (gen < 1 || ref != 2 * gen
                    || !generatedPerPage.getOrDefault(row[source], 0).equals(placements.getOrDefault(page, -1))) {
                continue;
            }
            if (taken.merge(line, 1, Integer::sum) <= ref - gen) {
                explained.add(row);
            }
        }
        return explained;
    }

    private static Map<String, Integer> copies(List<String[]> rows) {
        Map<String, Integer> copies = new java.util.HashMap<>();
        for (String[] row : rows) {
            copies.merge(line(row), 1, Integer::sum);
        }
        return copies;
    }

    /** Page Row identifier to the number of its global filter view placements that did not resolve, generated side. */
    private static Map<String, Integer> unresolvedGlobalFilterPlacements(Sides sides) throws IOException {
        Map<String, Integer> counts = new TreeMap<>();
        Path file = sides.kb.resolve("dashboard_contents.csv");
        if (!Files.isRegularFile(file)) {
            return counts;
        }
        CsvTable contents = CsvReader.read(file);
        for (String[] row : estateRows(contents, sides.estate)) {
            String resolution = contents.value(row, "resolution");
            if (contents.value(row, "element").equals(GLOBAL_FILTER_VIEW)
                    && (resolution.equals("unresolved") || resolution.equals("ambiguous"))) {
                counts.merge(contents.value(row, "container_id"), 1, Integer::sum);
            }
        }
        return counts;
    }

    /**
     * How many of the reference's error rows about global filter views difference 11 explains; 0 when any
     * other difference is left among the error rows of that element.
     */
    private static int doubledErrorRows(Sides sides) throws IOException {
        if (!Files.isRegularFile(sides.kb.resolve("errors.csv"))
                || !Files.isRegularFile(sides.reference.resolve("errors.csv"))) {
            return 0;
        }
        Result errors = compare(sides.kb, sides.reference, sides.source, sides.estate, "errors",
                Map.of("object_type", Set.of(GLOBAL_FILTER_VIEW)));
        if (!errors.extra.isEmpty() || !errors.missing.isEmpty() || !errors.differing.isEmpty()) {
            return 0;
        }
        return errors.explainedMissing.getOrDefault(11, List.of()).size();
    }

    // ------------------------------------------------------------------ difference 21

    private static final String UNDECODED = " — the name holds an escape that cannot be decoded; read as ";

    /**
     * True when an extra {@code unparsed} row is the report difference 21 adds: a catalog file whose name
     * on disk holds an escape that cannot be decoded.
     *
     * <p>Checked against the estate's own files, with the JDK's decoder and not the jar's: the file must
     * exist under {@code catalog/} at the on-disk path the row gives, and some segment of that path must
     * hold a {@code %} that is not followed by two hex digits, or escaped bytes that are not UTF-8. Without
     * the estate folder the row stays unexplained.
     */
    static boolean explainsUndecodedName(Sides sides, List<String> columns, String[] row) {
        int kind = columns.indexOf("kind");
        int type = columns.indexOf("object_type");
        int detail = columns.indexOf("detail");
        int occurrences = columns.indexOf("occurrences");
        if (sides.source == null || kind < 0 || type < 0 || detail < 0 || occurrences < 0
                || !row[kind].equals("undecoded_name") || !row[type].equals("File") || !row[occurrences].equals("1")
                || !row[detail].contains(UNDECODED)) {
            return false;
        }
        String onDisk = row[detail].substring(0, row[detail].indexOf(UNDECODED));
        Path file = sides.source.resolve("catalog");
        boolean malformed = false;
        for (String segment : onDisk.split("/")) {
            if (segment.isEmpty() || segment.equals("..")) {
                return false;
            }
            file = file.resolve(segment);
            try {
                String decoded = java.net.URLDecoder.decode(segment, java.nio.charset.StandardCharsets.UTF_8);
                malformed = malformed || (decoded.indexOf('�') >= 0 && segment.indexOf('�') < 0);
            } catch (IllegalArgumentException e) {
                malformed = true;
            }
        }
        return malformed && Files.isRegularFile(file);
    }

    // ------------------------------------------------------------------ difference 7

    /** What the source says about one element that has an id of its own. Read here, not by the jar. */
    static final class SourceElement {
        final String tag;
        final String name;
        /** The id its {@code containerRef} points at, or empty. */
        final String container;
        /** The id of the nearest enclosing element that has one, or empty. */
        final String parent;

        SourceElement(String tag, String name, String container, String parent) {
            this.tag = tag;
            this.name = name;
            this.container = container;
            this.parent = parent;
        }
    }

    private static final Map<Path, Map<String, SourceElement>> SOURCES = new java.util.HashMap<>();

    /**
     * True when a {@code physical_joins.columns} cell differs only as difference 7 says: the reference
     * lists the source's own ids of the foreign key's columns, joined with spaces; the generated cell lists
     * the Row identifiers of the same columns, one per line.
     *
     * <p>Nothing is taken on trust. Each id of the reference cell is looked up in the source files, which
     * are read here with the JDK's parser and not with the jar's reader. The id must belong to a
     * {@code PhysicalColumn} inside a {@code PhysicalTable}; the identifier that column must have is built
     * from the names found in the source (containers outermost first, table, column), and the generated
     * entry in the same place must be exactly that identifier and must be a row of the generated
     * {@code physical_columns}. An id that the source does not hold must leave an empty line. A different
     * column, a different table, a different order, an entry too many or too few, or no source folder to
     * check against: none of these is explained.
     */
    static boolean explainsForeignKeyColumns(Sides sides, String referenceCell, String generatedCell)
            throws IOException {
        if (sides.source == null || referenceCell.isEmpty()) {
            return false;
        }
        Map<String, SourceElement> source = sourceElements(sides.source);
        if (source.isEmpty()) {
            return false;
        }
        String[] ids = referenceCell.split(" ", -1);
        String[] entries = generatedCell.split("\n", -1);
        if (ids.length != entries.length) {
            return false;
        }
        Set<String> generatedColumns = null;
        for (int i = 0; i < ids.length; i++) {
            SourceElement column = ids[i].isEmpty() ? null : source.get(ids[i]);
            if (column == null) {
                // The reference could not read the link, or it points at nothing: no identifier exists.
                if (!entries[i].isEmpty()) {
                    return false;
                }
                continue;
            }
            String expected = physicalColumnIdentifier(sides.estate, column, source);
            if (expected == null || !expected.equals(entries[i])) {
                return false;
            }
            if (generatedColumns == null) {
                generatedColumns = new HashSet<>();
                Path file = sides.kb.resolve("physical_columns.csv");
                if (Files.isRegularFile(file)) {
                    CsvTable table = CsvReader.read(file);
                    for (String[] row : table.rows) {
                        generatedColumns.add(table.value(row, "row_id"));
                    }
                }
            }
            if (!generatedColumns.contains(expected)) {
                return false;
            }
        }
        return true;
    }

    /**
     * The Row identifier a physical column must have, from the source alone:
     * {@code <estate>::phys::<containers>.<table>.<column>}. Null when the element is not a column of a
     * physical table, or a name is missing.
     */
    static String physicalColumnIdentifier(String estate, SourceElement column, Map<String, SourceElement> source) {
        if (!column.tag.equals("PhysicalColumn") || column.name.isEmpty()) {
            return null;
        }
        SourceElement table = source.get(column.parent);
        String tableId = table == null ? null : physicalTableIdentifier(estate, table, source);
        return tableId == null ? null : tableId + "." + column.name;
    }

    /** The Row identifier a physical table must have, from the source alone; null when a name is missing. */
    static String physicalTableIdentifier(String estate, SourceElement table, Map<String, SourceElement> source) {
        if (!table.tag.equals("PhysicalTable") || table.name.isEmpty()) {
            return null;
        }
        List<String> names = new ArrayList<>();
        Set<String> seen = new HashSet<>();
        String at = table.container;
        while (!at.isEmpty() && seen.add(at)) {
            SourceElement container = source.get(at);
            if (container == null || container.name.isEmpty()) {
                break;
            }
            names.add(0, container.name);
            at = container.container;
        }
        if (names.isEmpty()) {
            return null;
        }
        names.add(table.name);
        return estate + "::phys::" + String.join(".", names);
    }

    /** Every element of the estate's repository files that has an id, by that id. Empty when there are none. */
    static Map<String, SourceElement> sourceElements(Path estateFolder) throws IOException {
        Path base = estateFolder.resolve("rpd").resolve("oracle").resolve("bi").resolve("server").resolve("base");
        Map<String, SourceElement> known = SOURCES.get(base.toAbsolutePath());
        if (known != null) {
            return known;
        }
        Map<String, SourceElement> elements = new java.util.HashMap<>();
        if (Files.isDirectory(base)) {
            List<Path> files;
            try (Stream<Path> walk = Files.walk(base, 2)) {
                files = walk.filter(p -> p.toString().endsWith(".xml") && Files.isRegularFile(p)).sorted()
                        .collect(Collectors.toList());
            }
            javax.xml.stream.XMLInputFactory factory = javax.xml.stream.XMLInputFactory.newInstance();
            factory.setProperty(javax.xml.stream.XMLInputFactory.SUPPORT_DTD, false);
            factory.setProperty(javax.xml.stream.XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);
            for (Path file : files) {
                try (java.io.InputStream in = Files.newInputStream(file)) {
                    javax.xml.stream.XMLStreamReader xml = factory.createXMLStreamReader(in);
                    List<String> open = new ArrayList<>();
                    while (xml.hasNext()) {
                        int event = xml.next();
                        if (event == javax.xml.stream.XMLStreamConstants.START_ELEMENT) {
                            String parent = open.isEmpty() ? "" : open.get(open.size() - 1);
                            String id = attribute(xml, "mdsid");
                            if (!id.isEmpty()) {
                                String containerRef = attribute(xml, "containerRef");
                                int hash = containerRef.indexOf('#');
                                elements.put(id, new SourceElement(xml.getLocalName(), attribute(xml, "name"),
                                        hash < 0 ? "" : containerRef.substring(hash + 1), parent));
                            }
                            open.add(id.isEmpty() ? parent : id);
                        } else if (event == javax.xml.stream.XMLStreamConstants.END_ELEMENT) {
                            open.remove(open.size() - 1);
                        }
                    }
                    xml.close();
                } catch (javax.xml.stream.XMLStreamException e) {
                    // A file that cannot be parsed gives no elements; cells that need it stay unexplained.
                }
            }
        }
        SOURCES.put(base.toAbsolutePath(), elements);
        return elements;
    }

    private static String attribute(javax.xml.stream.XMLStreamReader xml, String name) {
        String value = xml.getAttributeValue(null, name);
        return value == null ? "" : value;
    }

    // ------------------------------------------------------------------ differences 26 and 27

    private static final List<String> REFERENCED_COLUMNS = List.of("referenced_columns");
    private static final List<String> ROLE_COLUMNS = List.of("role_display_name", "role_guid");

    /**
     * What the estate's repository files say about foreign keys, the keys they point at, roles and
     * privilege packages. Read here with the JDK's streaming parser, not by the jar.
     */
    static final class RepositoryFacts {
        /** Foreign key Row identifier to the id of the key it points at; null when two keys share an identifier and disagree. */
        final Map<String, String> keyOfForeignKey = new java.util.HashMap<>();
        /** How many foreign keys carry the link attribute. */
        int foreignKeysWithLink;
        /** Key id to the ids of its columns, in source order (the first column list of the key). */
        final Map<String, List<String>> columnsOfKey = new java.util.HashMap<>();
        /** Key ids that have a column list at all. */
        final Set<String> keysWithColumnList = new HashSet<>();
        /** Ids of the keys at least one foreign key points at, where the id belongs to a physical key. */
        final Set<String> keysPointedAt = new HashSet<>();
        /** Role id to {display name or null, guid or null}; null where the attribute is absent. */
        final Map<String, String[]> roles = new java.util.HashMap<>();
        /** Privilege package Row identifier to the id of its role, for packages with a query privilege. */
        final Map<String, String> roleOfPackage = new java.util.HashMap<>();
        final Set<String> packagesInConflict = new HashSet<>();
    }

    private static final Map<Path, RepositoryFacts> REPOSITORY_FACTS = new java.util.HashMap<>();

    private static String idOf(String reference) {
        int hash = reference.indexOf('#');
        return hash < 0 ? "" : reference.substring(hash + 1);
    }

    /** Null when the estate folder has no repository files. */
    static RepositoryFacts repositoryFacts(Sides sides) throws IOException {
        if (sides.source == null) {
            return null;
        }
        Map<String, SourceElement> source = sourceElements(sides.source);
        if (source.isEmpty()) {
            return null;
        }
        Path base = sides.source.resolve("rpd").resolve("oracle").resolve("bi").resolve("server").resolve("base")
                .toAbsolutePath();
        RepositoryFacts facts = REPOSITORY_FACTS.get(base);
        if (facts != null) {
            return facts;
        }
        facts = new RepositoryFacts();
        javax.xml.stream.XMLInputFactory factory = javax.xml.stream.XMLInputFactory.newInstance();
        factory.setProperty(javax.xml.stream.XMLInputFactory.SUPPORT_DTD, false);
        factory.setProperty(javax.xml.stream.XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);
        for (String type : List.of("PhysicalTable", "Group", "PrivilegePackage")) {
            Path folder = base.resolve(type);
            if (!Files.isDirectory(folder)) {
                continue;
            }
            List<Path> files;
            try (Stream<Path> list = Files.list(folder)) {
                files = list.filter(p -> p.toString().endsWith(".xml") && Files.isRegularFile(p)).sorted()
                        .collect(Collectors.toList());
            }
            for (Path file : files) {
                try (java.io.InputStream in = Files.newInputStream(file)) {
                    javax.xml.stream.XMLStreamReader xml = factory.createXMLStreamReader(in);
                    List<String[]> open = new ArrayList<>();
                    String packageId = null;
                    String packageRole = "";
                    while (xml.hasNext()) {
                        int event = xml.next();
                        if (event == javax.xml.stream.XMLStreamConstants.END_ELEMENT) {
                            open.remove(open.size() - 1);
                            continue;
                        }
                        if (event != javax.xml.stream.XMLStreamConstants.START_ELEMENT) {
                            continue;
                        }
                        String tag = xml.getLocalName();
                        String id = attribute(xml, "mdsid");
                        String[] parent = open.isEmpty() ? new String[] {"", "", ""} : open.get(open.size() - 1);
                        String[] grand = open.size() < 2 ? new String[] {"", "", ""} : open.get(open.size() - 2);
                        // {tag, id, "first" when this is the first column list of its key}
                        String[] here = {tag, id, ""};
                        if (tag.equals("PhysicalForeignKey") && parent[0].equals("PhysicalTable")) {
                            String link = xml.getAttributeValue(null, "counterPartKeyRef");
                            if (link != null) {
                                facts.foreignKeysWithLink++;
                            }
                            SourceElement table = source.get(parent[1]);
                            String tableId = table == null ? null : physicalTableIdentifier(sides.estate, table, source);
                            if (tableId != null && link != null && !link.isEmpty()) {
                                String name = attribute(xml, "name");
                                String rowId = tableId + "." + (name.isEmpty() ? "fk" : name);
                                String key = idOf(link);
                                if (facts.keyOfForeignKey.containsKey(rowId)
                                        && !key.equals(facts.keyOfForeignKey.get(rowId))) {
                                    facts.keyOfForeignKey.put(rowId, null);
                                } else {
                                    facts.keyOfForeignKey.put(rowId, key);
                                }
                                SourceElement target = source.get(key);
                                if (target != null && target.tag.equals("PhysicalKey")) {
                                    facts.keysPointedAt.add(key);
                                }
                            }
                        } else if (tag.equals("RefColumns") && parent[0].equals("PhysicalKey")
                                && facts.keysWithColumnList.add(parent[1])) {
                            here[2] = "first";
                            facts.columnsOfKey.put(parent[1], new ArrayList<>());
                        } else if (tag.equals("RefColumn") && parent[0].equals("RefColumns") && parent[2].equals("first")
                                && grand[0].equals("PhysicalKey")) {
                            facts.columnsOfKey.get(grand[1]).add(idOf(attribute(xml, "columnRef")));
                        } else if (tag.equals("Group") && open.isEmpty()) {
                            facts.roles.put(id, new String[] {xml.getAttributeValue(null, "displayName"),
                                    xml.getAttributeValue(null, "guid")});
                        } else if (tag.equals("PrivilegePackage") && open.isEmpty()) {
                            String name = attribute(xml, "name");
                            packageId = sides.estate + "::sec::pkg." + (name.isEmpty() ? "?" : name);
                            packageRole = idOf(attribute(xml, "roleRef"));
                        } else if (tag.equals("QueryPrivilegeMapping") && parent[0].equals("PrivilegePackage")
                                && open.size() == 1 && packageId != null) {
                            if (facts.roleOfPackage.containsKey(packageId)
                                    && !facts.roleOfPackage.get(packageId).equals(packageRole)) {
                                facts.packagesInConflict.add(packageId);
                            }
                            facts.roleOfPackage.put(packageId, packageRole);
                        }
                        open.add(here);
                    }
                    xml.close();
                } catch (javax.xml.stream.XMLStreamException e) {
                    // A file that cannot be parsed gives no facts; what needs it stays unexplained.
                }
            }
        }
        REPOSITORY_FACTS.put(base, facts);
        return facts;
    }

    /** The table and the columns a foreign key must point at, from the source alone: {table, columns one per line}. Null when the source does not say. */
    private static String[] foreignKeyTarget(Sides sides, String foreignKeyRowId) throws IOException {
        RepositoryFacts facts = repositoryFacts(sides);
        String key = facts == null ? null : facts.keyOfForeignKey.get(foreignKeyRowId);
        if (key == null) {
            return null;
        }
        Map<String, SourceElement> source = sourceElements(sides.source);
        SourceElement keyElement = source.get(key);
        if (keyElement == null || !keyElement.tag.equals("PhysicalKey")) {
            return null;
        }
        SourceElement table = source.get(keyElement.parent);
        String tableId = table == null ? null : physicalTableIdentifier(sides.estate, table, source);
        if (tableId == null) {
            return null;
        }
        List<String> columns = new ArrayList<>();
        for (String id : facts.columnsOfKey.getOrDefault(key, List.of())) {
            SourceElement column = source.get(id);
            String columnId = column == null ? null : physicalColumnIdentifier(sides.estate, column, source);
            columns.add(columnId == null ? "" : columnId);
        }
        return new String[] {tableId, String.join("\n", columns)};
    }

    /**
     * <b>Difference 26</b>, {@code physical_joins.table2_id}: true when the reference cell is empty, the
     * row is a foreign key, and the generated cell is the Row identifier of the table that holds the key
     * the foreign key points at. That table is found here, in the estate's own files: the foreign key's
     * link names a key by id, the key sits inside a physical table, and the identifier is built from the
     * names of that table and its containers. It must also be a row of the generated
     * {@code physical_tables}. Without the estate folder nothing is explained.
     */
    static boolean explainsForeignKeyTarget(Sides sides, List<String> columns, String[] reference, String generatedCell)
            throws IOException {
        if (!cell(columns, reference, "table2_id").isEmpty() || !cell(columns, reference, "kind").equals("foreign_key")
                || generatedCell.isEmpty()) {
            return false;
        }
        String[] expected = foreignKeyTarget(sides, cell(columns, reference, "row_id"));
        if (expected == null || !expected[0].equals(generatedCell)) {
            return false;
        }
        CsvTable tables = generatedTable(sides, "physical_tables");
        for (String[] row : tables.rows) {
            if (tables.value(row, "row_id").equals(generatedCell)) {
                return true;
            }
        }
        return false;
    }

    /**
     * <b>Difference 26</b>, the added column {@code physical_joins.referenced_columns}: true when it is
     * right on <b>every</b> row compared. For a foreign key it must list, one per line and in the key's own
     * order, the Row identifiers of the columns of the key the foreign key points at, each built from the
     * names in the estate's files; and the row's {@code table2_id} must be that key's table. A foreign key
     * whose link the source does not resolve must have an empty cell, and so must every complex join. One
     * wrong value, or no estate folder, and the column stays listed as unexplained.
     */
    static boolean explainsReferencedColumns(Sides sides, CsvTable generated, List<String[]> rows) throws IOException {
        if (sides.source == null || generated.indexOf("row_id") < 0 || generated.indexOf("kind") < 0) {
            return false;
        }
        if (rows.isEmpty()) {
            // An estate with no repository half has no row to be wrong.
            return true;
        }
        if (repositoryFacts(sides) == null) {
            return false;
        }
        for (String[] row : rows) {
            String cell = generated.value(row, "referenced_columns");
            if (!generated.value(row, "kind").equals("foreign_key")) {
                if (!cell.isEmpty()) {
                    return false;
                }
                continue;
            }
            String[] expected = foreignKeyTarget(sides, generated.value(row, "row_id"));
            if (expected == null) {
                if (!cell.isEmpty() || !generated.value(row, "table2_id").isEmpty()) {
                    return false;
                }
            } else if (!expected[1].equals(cell) || !expected[0].equals(generated.value(row, "table2_id"))) {
                return false;
            }
        }
        return true;
    }

    /**
     * <b>Difference 27</b>, the added columns {@code security_rls.role_display_name} and
     * {@code role_guid}: true when both are right on <b>every</b> row compared. For a query privilege row
     * they must be the display name and the guid that the estate's own file of the role records, the role
     * being the one the row's privilege package points at; an attribute the file does not have gives an
     * empty cell. Rows of the other two kinds must have both cells empty. One wrong value, two packages of
     * one name with different roles, or no estate folder, and the columns stay listed as unexplained.
     */
    static boolean explainsRoleColumns(Sides sides, CsvTable generated, List<String[]> rows) throws IOException {
        if (sides.source == null || generated.indexOf("row_id") < 0 || generated.indexOf("kind") < 0) {
            return false;
        }
        if (rows.isEmpty()) {
            return true;
        }
        RepositoryFacts facts = repositoryFacts(sides);
        if (facts == null) {
            return false;
        }
        for (String[] row : rows) {
            String display = generated.value(row, "role_display_name");
            String guid = generated.value(row, "role_guid");
            String expectedDisplay = "";
            String expectedGuid = "";
            if (generated.value(row, "kind").equals("query_privilege")) {
                String id = generated.value(row, "row_id");
                int dot = id.lastIndexOf(".q");
                String packageId = dot < 0 ? id : id.substring(0, dot);
                if (!facts.roleOfPackage.containsKey(packageId) || facts.packagesInConflict.contains(packageId)) {
                    return false;
                }
                String[] role = facts.roles.get(facts.roleOfPackage.get(packageId));
                if (role != null) {
                    expectedDisplay = role[0] == null ? "" : role[0];
                    expectedGuid = role[1] == null ? "" : role[1];
                }
            }
            if (!display.equals(expectedDisplay) || !guid.equals(expectedGuid)) {
                return false;
            }
        }
        return true;
    }

    /**
     * <b>Differences 26 and 27</b>, in {@code unparsed}: says which of them explains a count of unread
     * content that is lower in the generated table (or gone from it), or 0 when neither does.
     *
     * <p>Following a foreign key to its key (26) reads the foreign key's link, the key element, its column
     * list and each column reference in it. Following a privilege package to its role (27) reads the role's
     * display name and guid. The reference reported all of these as unread. How many must have gone is
     * counted here from the estate's own files: the foreign keys that carry the link; the distinct keys a
     * foreign key points at, their first column lists, and the column references in those; the distinct
     * roles a privilege package with a query privilege points at that have the attribute. The reference
     * count less the generated count must be exactly that number.
     */
    static int explainsFollowedReference(Sides sides, List<String> columns, String[] reference, String generatedCount)
            throws IOException {
        String type = cell(columns, reference, "object_type");
        if (!type.equals("PhysicalTable") && !type.equals("Group")) {
            return 0;
        }
        RepositoryFacts facts = repositoryFacts(sides);
        if (facts == null) {
            return 0;
        }
        String what = cell(columns, reference, "kind") + "|" + cell(columns, reference, "detail");
        int expected;
        int number;
        if (type.equals("PhysicalTable")) {
            number = 26;
            int lists = 0;
            int references = 0;
            for (String key : facts.keysPointedAt) {
                if (facts.keysWithColumnList.contains(key)) {
                    lists++;
                    references += facts.columnsOfKey.get(key).size();
                }
            }
            switch (what) {
                case "unread_attribute|PhysicalForeignKey@counterPartKeyRef":
                    expected = facts.foreignKeysWithLink;
                    break;
                case "unread_element|PhysicalKey":
                    expected = facts.keysPointedAt.size();
                    break;
                case "unread_element|RefColumns":
                    expected = lists;
                    break;
                case "unread_element|RefColumn":
                case "unread_attribute|RefColumn@columnRef":
                    expected = references;
                    break;
                default:
                    return 0;
            }
        } else {
            number = 27;
            int at = what.equals("unread_attribute|Group@displayName") ? 0
                    : what.equals("unread_attribute|Group@guid") ? 1 : -1;
            if (at < 0) {
                return 0;
            }
            expected = 0;
            for (String role : new HashSet<>(facts.roleOfPackage.values())) {
                String[] found = facts.roles.get(role);
                expected += found != null && found[at] != null ? 1 : 0;
            }
        }
        try {
            int fewer = Integer.parseInt(cell(columns, reference, "occurrences")) - Integer.parseInt(generatedCount);
            return fewer > 0 && fewer == expected ? number : 0;
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    // ------------------------------------------------------------------ differences 28 to 32

    /** The tables differences 28, 29 and 30 add, each with its number. The reference has no such tables. */
    static final Map<String, Integer> NEW_TABLES = Map.of("physical_keys", 28, "logical_keys", 29, "logical_level_keys", 30,
            "lts_tables", 34, "lts_joins", 34);
    private static final Map<String, List<String>> NEW_TABLE_HEADERS = Map.of(
            "physical_keys", List.of("row_id", "rpd_id", "table_id", "name", "columns"),
            "logical_keys", List.of("row_id", "rpd_id", "logical_table_id", "name", "is_primary", "columns"),
            "logical_level_keys", List.of("row_id", "rpd_id", "level_id", "name", "is_primary", "is_chronological",
                    "is_for_drill_down", "columns"),
            "lts_tables", List.of("row_id", "rpd_id", "source_id", "position", "physical_table_id"),
            "lts_joins", List.of("row_id", "rpd_id", "source_id", "source_table_id", "position", "join_type", "cardinality",
                    "table1_id", "table2_id", "physical_join_id"));
    /** The columns the jar adds at the end of a table the reference has, in order. */
    static final Map<String, List<String>> ADDED_COLUMNS = Map.of(
            "physical_tables", List.of("description", "member_key_column_id", "parent_key_column_id", "distance_column_id",
                    "leaf_column_id"),
            "physical_columns", List.of("scale", "position", "source_column_id"),
            "logical_tables", List.of("is_lookup_table"),
            "logical_columns", List.of("descriptor_id_column_id"),
            "logical_table_sources", List.of("closure_table_id"),
            "logical_levels", List.of("child_level_ids", "parent_level_ids"),
            "logical_dimensions", List.of("is_time"));
    /** {@code <table>.<column>} to the difference that adds the column. */
    static final Map<String, Integer> ADDED_NUMBER = addedNumbers();

    private static Map<String, Integer> addedNumbers() {
        Map<String, Integer> numbers = new java.util.HashMap<>();
        numbers.put("physical_tables.description", 37);
        for (String column : List.of("member_key_column_id", "parent_key_column_id", "distance_column_id", "leaf_column_id")) {
            numbers.put("physical_tables." + column, 36);
        }
        numbers.put("physical_columns.scale", 31);
        numbers.put("physical_columns.position", 31);
        numbers.put("physical_columns.source_column_id", 37);
        numbers.put("logical_tables.is_lookup_table", 37);
        numbers.put("logical_columns.descriptor_id_column_id", 37);
        numbers.put("logical_table_sources.closure_table_id", 36);
        numbers.put("logical_levels.child_level_ids", 35);
        numbers.put("logical_levels.parent_level_ids", 35);
        numbers.put("logical_dimensions.is_time", 32);
        return numbers;
    }

    /**
     * <b>Differences 28, 29, 30 and 34</b>, a table the reference does not have: returns the difference's number
     * when the generated table is right on <b>every</b> row, or 0.
     *
     * <p>The rows the table must hold are worked out by {@link KeySource} from the estate's own files, read
     * with the JDK's parser and not by the jar: one row per key of a physical table, of a logical table, or
     * of a level of a dimension, with the Row identifier built from the names in the files, the key's name,
     * its flags as written, and its columns one per line in source order. The generated table must have
     * exactly the expected header and exactly those rows, cell for cell, no more and no fewer. An estate
     * with no repository half must have an empty table. Without the estate folder nothing is explained.
     */
    static int explainsNewTable(Sides sides, String table) throws IOException {
        Integer number = NEW_TABLES.get(table);
        Path file = sides.kb.resolve(table + ".csv");
        if (number == null || sides.source == null || !Files.isRegularFile(file)) {
            return 0;
        }
        CsvTable generated = CsvReader.read(file);
        List<String> header = NEW_TABLE_HEADERS.get(table);
        if (!generated.header.equals(header)) {
            return 0;
        }
        KeySource.Facts facts = KeySource.of(sides.source, sides.estate);
        Map<String, String[]> expected = facts == null ? Map.of() : table.equals("physical_keys") ? facts.physicalKeys
                : table.equals("logical_keys") ? facts.logicalKeys : table.equals("logical_level_keys") ? facts.levelKeys
                        : table.equals("lts_tables") ? facts.sourceTables : facts.sourceJoins;
        List<String[]> rows = estateRows(generated, sides.estate);
        if (rows.size() != generated.rows.size() || rows.size() != expected.size()) {
            return 0;
        }
        Set<String> seen = new HashSet<>();
        for (String[] row : rows) {
            String[] wanted = expected.get(row[0]);
            if (wanted == null || !seen.add(row[0])) {
                return 0;
            }
            for (int i = 0; i < header.size(); i++) {
                if (!row[i].equals(i == 1 ? sides.estate : wanted[i])) {
                    return 0;
                }
            }
        }
        return number;
    }

    /**
     * <b>Differences 31, 32 and 35 to 37</b>, columns the jar adds to a table the reference has: true when
     * every added column is right on <b>every</b> row compared. The values are worked out by
     * {@link KeySource} from the estate's own files: a column's scale and its place in its table, the
     * column an alias column stands for, a dimension's time flag, a level's children and parents, the four
     * columns of a parent-child table and a source's closure table, a table's description, a logical
     * table's lookup flag and a logical column's descriptor column; each exactly as written, or as the Row
     * identifier built from the names in the files. One wrong value, a row the files do not have, two rows
     * with one identifier, or no estate folder, and all the added columns of the table stay unexplained.
     */
    static boolean explainsAddedColumns(Sides sides, String table, CsvTable generated, List<String[]> rows)
            throws IOException {
        if (sides.source == null || generated.indexOf("row_id") < 0) {
            return false;
        }
        if (rows.isEmpty()) {
            return true;
        }
        KeySource.Facts facts = KeySource.of(sides.source, sides.estate);
        if (facts == null) {
            return false;
        }
        List<String> columns = ADDED_COLUMNS.get(table);
        Map<String, String[]> expected = facts.added(table);
        for (String[] row : rows) {
            String[] cells = expected.get(generated.value(row, "row_id"));
            if (cells == null) {
                return false;
            }
            for (int i = 0; i < columns.size(); i++) {
                if (!cells[i].equals(generated.value(row, columns.get(i)))) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * <b>Differences 28 to 37</b>, in {@code unparsed}: says which of them explains a count of unread
     * content that is lower in the generated table (or gone from it), or 0 when none does.
     *
     * <p>The reference read no key of a physical or logical table, of a level only the first column of the
     * first drill-down key, and neither a column's scale nor a dimension's time flag; it listed all of that
     * as not read. The jar now reads every key with its name, flags and columns, the scale and the flag.
     * Explained when the generated count is exactly the number of occurrences that the estate's own files
     * say are <b>still</b> outside what the jar reads (a column list that is not the first of its key, a
     * column link that is in no key, anything in an object that could not be given an identifier), counted
     * by {@link KeySource}, and that number is lower than the reference's. The check does not lean on what
     * the reference happened to read: it fixes what the generated count must be.
     */
    static int explainsKeysNowRead(Sides sides, List<String> columns, String[] reference, String generatedCount)
            throws IOException {
        if (sides.source == null) {
            return 0;
        }
        KeySource.Facts facts = KeySource.of(sides.source, sides.estate);
        String what = cell(columns, reference, "object_type") + "|" + cell(columns, reference, "kind") + "|"
                + cell(columns, reference, "detail");
        if (facts == null || !facts.difference.containsKey(what)) {
            return 0;
        }
        try {
            int generated = Integer.parseInt(generatedCount);
            int before = Integer.parseInt(cell(columns, reference, "occurrences"));
            return generated == facts.remaining.get(what) && generated < before ? facts.difference.get(what) : 0;
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /**
     * <b>Differences 28, 29 and 30</b>, in {@code coverage}: returns the number that explains an added row,
     * or 0. The jar counts each key it writes as read, under the key's element name. The row must be of the
     * repository layer, count nothing as skipped, and count exactly as many keys as the estate's own files
     * hold: for {@code PhysicalKey} the keys of physical tables (28); for {@code LogicalKey} the keys of
     * logical tables and of levels together, because both are {@code LogicalKey} elements (reported under
     * 29; difference 30 shares the row).
     */
    static int explainsKeyCoverage(Sides sides, List<String> columns, String[] row) throws IOException {
        String type = cell(columns, row, "object_type");
        if (sides.source == null || !List.of("PhysicalKey", "LogicalKey", "Link", "Join").contains(type)
                || !cell(columns, row, "layer").equals("rpd") || !cell(columns, row, "skipped").equals("0")) {
            return 0;
        }
        KeySource.Facts facts = KeySource.of(sides.source, sides.estate);
        if (facts == null) {
            return 0;
        }
        // Difference 34 counts each table and each join of a source the same way, as Link and Join.
        int expected = type.equals("PhysicalKey") ? facts.physicalKeyCount : type.equals("LogicalKey")
                ? facts.logicalKeyCount + facts.levelKeyCount : type.equals("Link") ? facts.linkCount : facts.joinCount;
        return expected > 0 && cell(columns, row, "parsed").equals(Integer.toString(expected))
                ? (type.equals("PhysicalKey") ? 28 : type.equals("LogicalKey") ? 29 : 34) : 0;
    }

    // ------------------------------------------------------------------ difference 19

    private static final List<String> REDACTION_COLUMNS = List.of("redacted", "expr_text", "expr_text_desc", "expr_refs");
    /** Written here a second time on purpose: the check must not lean on the jar's own rule. */
    private static final java.util.regex.Pattern CREDENTIAL_NAME = java.util.regex.Pattern.compile(
            "password|passwd|pwd|secret|token|credential|apikey|api[_ -]?key",
            java.util.regex.Pattern.CASE_INSENSITIVE | java.util.regex.Pattern.UNICODE_CASE);
    private static final String SAME_VALUE = " — value withheld: it is the same as the value of a variable whose "
            + "name indicates a credential";
    private static final Map<Path, Set<String>> CREDENTIAL_VALUES = new java.util.HashMap<>();

    /**
     * True when a {@code variables} row differs only as difference 19 says: the reference wrote the value
     * of a variable with an innocent name, and the generated row withholds it because the same value is the
     * value of a variable whose name suggests a credential.
     *
     * <p>Checked against the source, never assumed: the generated row must have {@code redacted} true and
     * its three expression cells empty; the reference row must have {@code redacted} false; and the value
     * the reference wrote must be, in the source files, the value of a variable with a telling name. A
     * value withheld for any other reason, or with no source folder to check against, is not explained.
     * The value itself is never printed by this method.
     */
    static boolean explainsRepeatedCredential(Sides sides, List<String> columns, String[] reference, String[] generated)
            throws IOException {
        int redacted = columns.indexOf("redacted");
        int text = columns.indexOf("expr_text");
        int textDesc = columns.indexOf("expr_text_desc");
        int refs = columns.indexOf("expr_refs");
        if (sides.source == null || redacted < 0 || text < 0 || textDesc < 0 || refs < 0) {
            return false;
        }
        if (!generated[redacted].equals("true") || !reference[redacted].equals("false")
                || !generated[text].isEmpty() || !generated[textDesc].isEmpty() || !generated[refs].isEmpty()) {
            return false;
        }
        Set<String> credentials = credentialValues(sides.source);
        return credentials.contains(reference[text].strip()) || credentials.contains(reference[textDesc].strip());
    }

    /**
     * True when an extra {@code unparsed} row is the record of a value withheld under difference 19: a
     * {@code redacted_value} row for a variable whose reference row holds a value that the source also
     * gives to a variable with a telling name.
     */
    static boolean explainsExtraRedaction(Sides sides, List<String> columns, String[] row) throws IOException {
        int kind = columns.indexOf("kind");
        int type = columns.indexOf("object_type");
        int detail = columns.indexOf("detail");
        int occurrences = columns.indexOf("occurrences");
        if (sides.source == null || kind < 0 || type < 0 || detail < 0 || occurrences < 0
                || !row[kind].equals("redacted_value") || !row[type].equals("Variable")
                || !row[occurrences].equals("1") || !row[detail].endsWith(SAME_VALUE)) {
            return false;
        }
        String name = row[detail].substring(0, row[detail].length() - SAME_VALUE.length());
        Path file = sides.reference.resolve("variables.csv");
        if (CREDENTIAL_NAME.matcher(name).find() || !Files.isRegularFile(file)) {
            return false;
        }
        Set<String> credentials = credentialValues(sides.source);
        CsvTable variables = reference(file);
        for (String[] variable : estateRows(variables, sides.estate)) {
            if (variables.value(variable, "name").equals(name) && variables.value(variable, "redacted").equals("false")
                    && (credentials.contains(variables.value(variable, "expr_text").strip())
                            || credentials.contains(variables.value(variable, "expr_text_desc").strip()))) {
                return true;
            }
        }
        return false;
    }

    /**
     * The values, without surrounding white space, that the source gives to variables whose name suggests
     * a credential. Read from the source files with the JDK's parser. For checking only: never print one.
     */
    static Set<String> credentialValues(Path estateFolder) throws IOException {
        Path folder = estateFolder.resolve("rpd").resolve("oracle").resolve("bi").resolve("server").resolve("base")
                .resolve("Variable");
        Set<String> known = CREDENTIAL_VALUES.get(folder.toAbsolutePath());
        if (known != null) {
            return known;
        }
        Set<String> values = new HashSet<>();
        if (Files.isDirectory(folder)) {
            List<Path> files;
            try (Stream<Path> list = Files.list(folder)) {
                files = list.filter(p -> p.toString().endsWith(".xml") && Files.isRegularFile(p)).sorted()
                        .collect(Collectors.toList());
            }
            javax.xml.stream.XMLInputFactory factory = javax.xml.stream.XMLInputFactory.newInstance();
            factory.setProperty(javax.xml.stream.XMLInputFactory.SUPPORT_DTD, false);
            factory.setProperty(javax.xml.stream.XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);
            factory.setProperty(javax.xml.stream.XMLInputFactory.IS_COALESCING, true);
            for (Path file : files) {
                try (java.io.InputStream in = Files.newInputStream(file)) {
                    javax.xml.stream.XMLStreamReader xml = factory.createXMLStreamReader(in);
                    boolean first = true;
                    boolean telling = false;
                    while (xml.hasNext()) {
                        if (xml.next() != javax.xml.stream.XMLStreamConstants.START_ELEMENT) {
                            continue;
                        }
                        if (first) {
                            first = false;
                            telling = CREDENTIAL_NAME.matcher(attribute(xml, "name")).find();
                        } else if (telling && (xml.getLocalName().equals("ExprText") || xml.getLocalName().equals("ExprTextDesc"))) {
                            String value = xml.getElementText().strip();
                            if (!value.isEmpty()) {
                                values.add(value);
                            }
                        }
                    }
                    xml.close();
                } catch (javax.xml.stream.XMLStreamException e) {
                    // A file that cannot be parsed gives no values; rows that need it stay unexplained.
                }
            }
        }
        CREDENTIAL_VALUES.put(folder.toAbsolutePath(), values);
        return values;
    }

    /** Reads a reference table: removes the guard apostrophes and drops the {@code _guarded} columns. */
    static CsvTable reference(Path file) throws IOException {
        CsvTable raw = CsvReader.read(file);
        List<Integer> keep = new ArrayList<>();
        List<String> header = new ArrayList<>();
        for (int i = 0; i < raw.header.size(); i++) {
            if (!raw.header.get(i).endsWith(GUARDED)) {
                keep.add(i);
                header.add(raw.header.get(i));
            }
        }
        List<String[]> rows = new ArrayList<>();
        for (String[] rawRow : raw.rows) {
            String[] row = new String[keep.size()];
            for (int k = 0; k < keep.size(); k++) {
                int i = keep.get(k);
                String value = rawRow[i];
                boolean guarded = i + 1 < raw.header.size()
                        && raw.header.get(i + 1).equals(raw.header.get(i) + GUARDED)
                        && rawRow[i + 1].equals("true");
                row[k] = guarded && value.startsWith("'") ? value.substring(1) : value;
            }
            rows.add(row);
        }
        return new CsvTable(header, rows);
    }

    static List<String[]> estateRows(CsvTable table, String estate) {
        int index = table.indexOf("rpd_id");
        List<String[]> rows = new ArrayList<>();
        for (String[] row : table.rows) {
            if (index >= 0 && row[index].equals(estate)) {
                rows.add(row);
            }
        }
        return rows;
    }

    private static List<String[]> filter(CsvTable table, List<String[]> rows, Map<String, Set<String>> where) {
        List<String[]> kept = new ArrayList<>();
        for (String[] row : rows) {
            boolean keep = true;
            for (Map.Entry<String, Set<String>> condition : where.entrySet()) {
                boolean not = condition.getKey().startsWith(NOT);
                int index = table.indexOf(not ? condition.getKey().substring(NOT.length()) : condition.getKey());
                // A table without the column: a positive condition drops every row, a negative one none.
                keep = keep && (index < 0 ? not : condition.getValue().contains(row[index]) != not);
            }
            if (keep) {
                kept.add(row);
            }
        }
        return kept;
    }

    /** Groups rows by key; each row is reduced to the shared columns, in the reference's column order. */
    private static Map<String, List<String[]>> group(CsvTable table, List<String[]> rows, List<String> key,
            List<String> shared) {
        Map<String, List<String[]>> groups = new TreeMap<>();
        for (String[] row : rows) {
            StringBuilder k = new StringBuilder();
            for (String column : key) {
                k.append(table.value(row, column)).append('\u0001');
            }
            String[] projected = new String[shared.size()];
            for (int i = 0; i < shared.size(); i++) {
                projected[i] = table.value(row, shared.get(i));
            }
            groups.computeIfAbsent(k.toString(), x -> new ArrayList<>()).add(projected);
        }
        return groups;
    }

    private static String line(String[] row) {
        return String.join(" | ", row);
    }

    static List<String> manifestTables(Path kb) throws IOException {
        CsvTable manifest = CsvReader.read(kb.resolve("manifest.csv"));
        for (String[] row : manifest.rows) {
            if (manifest.value(row, "key").equals("tables")) {
                return new ArrayList<>(Arrays.asList(manifest.value(row, "value").split(" ")));
            }
        }
        return new ArrayList<>();
    }

    static List<String> referenceTables(Path reference) throws IOException {
        try (Stream<Path> files = Files.list(reference)) {
            return files.map(p -> p.getFileName().toString()).filter(n -> n.endsWith(".csv"))
                    .map(n -> n.substring(0, n.length() - 4)).sorted().collect(Collectors.toList());
        }
    }
}
