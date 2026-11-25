package obi2pbi.core;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Pattern;

/**
 * Computes the work units of a Slice and the text of each unit's packet (Architecture AD-17). No I/O.
 *
 * <p>Everything is derived from the Slice's own tables and manifest, so the same Slice always gives the same
 * packets. A packet joins what a writer needs about one unit; every fact is followed by the citation of the
 * row it came from, {@code [kb:<row identifier>]}; formulas and expressions are printed exactly as extracted.
 * No packet is longer than the Slice's {@code max_packet_lines} ({@link #DEFAULT_MAX_LINES} unless the slice
 * command was told otherwise): a longer unit is split into numbered parts, between items and never inside
 * one, and each part is a unit of its own that can be read alone.
 *
 * <p>The units also fix which rows a document must cover (see {@link #requiredRows}): presentation tables
 * and columns of the subject area, logical tables with their keys, logical columns that are measures,
 * logical table sources, physical tables with their definitions, columns and keys, joins, dimensions and
 * presentation hierarchies, variables, security rows, and report columns that are calculations. Each such
 * row is cited in exactly one packet.
 */
public final class Packets {

    /** The most lines a packet has unless the Slice's manifest says otherwise ({@code max_packet_lines}). */
    public static final int DEFAULT_MAX_LINES = 120;
    /** The smallest limit that still leaves room for a header, a heading and an item. */
    public static final int SMALLEST_MAX_LINES = 40;
    /** The manifest fact that carries the limit a Slice's packets were cut to. */
    public static final String MAX_LINES_FACT = "max_packet_lines";
    /** Ends the packet line that states a row a document must cover. */
    public static final String REQUIRED_MARK = "(REQUIRED)";

    private static final int HEADER_LINES = 9;
    /** How many rows of unparsed the gaps packet puts on one line. */
    private static final int GAPS_PER_LINE = 6;
    private static final String CONTINUES_NEXT = "(the value continues in the next part)";
    private static final String CONTINUES_PREVIOUS = "(the value continues from the previous part)";
    private static final Pattern COLUMN_REFERENCE = Pattern.compile("\"[^\"]+\"(\\.\"[^\"]+\"){1,2}");
    private static final String NO_ID = "no Row identifier";

    private final KbData slice;
    private final String areaId;
    private final String areaName;
    private final Map<String, Map<String, Map<String, String>>> byId = new HashMap<>();
    private final Map<String, Map<String, List<Map<String, String>>>> grouped = new HashMap<>();
    private final List<Draft> drafts = new ArrayList<>();
    /** Logical table Row identifier to the id of the unit that describes it. */
    private final Map<String, String> unitOfLogicalTable = new LinkedHashMap<>();
    /** Physical table Row identifier to the id of the unit that describes it. */
    private final Map<String, String> unitOfPhysicalTable = new LinkedHashMap<>();

    private final int maxLines;
    private final Set<String> identifiersOnly;
    /** A Slice cut for a tab (story 4.1) rather than for a subject area. */
    private final boolean tab;
    /** In a tab's Slice: each confirmed column with the items it was confirmed for. */
    private final Map<String, List<String>> itemsOfColumn = new LinkedHashMap<>();

    private Packets(KbData slice, int maxLines) {
        this.slice = slice;
        this.maxLines = Math.max(SMALLEST_MAX_LINES, maxLines);
        this.areaId = slice.fact("subject_area_id");
        this.areaName = slice.fact("subject_area");
        this.identifiersOnly = new java.util.HashSet<>(lines(slice.fact(IDENTIFIERS_FACT)));
        this.tab = "tab".equals(slice.fact("cut_by"));
        for (Map<String, String> field : slice.table(SliceSchema.TAB_FIELDS).records()) {
            for (String column : lines(field.get("row_ids"))) {
                itemsOfColumn.computeIfAbsent(column, k -> new ArrayList<>()).add(field.get("item_id"));
            }
        }
    }

    /** What the Slice is about, for sentences: "this subject area", or the tab's subject areas. */
    private String scope() {
        return tab ? "the subject areas this tab touches" : "this subject area";
    }

    /**
     * The units of a Slice in writing order, each with its packet, no packet longer than the Slice's
     * {@code max_packet_lines} fact ({@link #DEFAULT_MAX_LINES} when the manifest has none).
     */
    public static List<PacketUnit> build(KbData slice) {
        int limit = DEFAULT_MAX_LINES;
        try {
            if (!slice.fact(MAX_LINES_FACT).isEmpty()) {
                limit = Integer.parseInt(slice.fact(MAX_LINES_FACT));
            }
        } catch (NumberFormatException e) {
            limit = DEFAULT_MAX_LINES;
        }
        return build(slice, limit);
    }

    /** The same, with the most lines a packet may have given by the caller. */
    public static List<PacketUnit> build(KbData slice, int maxLines) {
        Packets packets = new Packets(slice, maxLines);
        packets.planLogicalTables();
        packets.planPhysicalTables();
        if (packets.tab) {
            packets.tabOverview();
            packets.tabFields();
        } else {
            packets.overview();
        }
        packets.logicalTables();
        packets.physicalTables();
        packets.joins();
        packets.hierarchies();
        packets.variables();
        packets.security();
        packets.reports();
        packets.savedFilters();
        packets.gaps();
        List<PacketUnit> units = new ArrayList<>();
        for (Draft draft : packets.drafts) {
            units.addAll(packets.split(draft));
        }
        return units;
    }

    /** The rows of {@code required_rows}: {@code {row_id, table, unit_id}}, in unit order. */
    public static List<String[]> requiredRows(List<PacketUnit> units) {
        List<String[]> rows = new ArrayList<>();
        for (PacketUnit unit : units) {
            for (Map.Entry<String, String> row : unit.requiredRows.entrySet()) {
                rows.add(new String[] {row.getKey(), row.getValue(), unit.unitId});
            }
        }
        return rows;
    }

    /** The rows of {@code units}, in writing order. */
    public static List<String[]> unitRows(List<PacketUnit> units) {
        List<String[]> rows = new ArrayList<>();
        int position = 1;
        for (PacketUnit unit : units) {
            rows.add(new String[] {unit.unitId, Integer.toString(position++), unit.kind, unit.title,
                    Integer.toString(unit.part), Integer.toString(unit.parts), unit.packetFile(),
                    Integer.toString(unit.lines.size()), Integer.toString(unit.requiredRows.size())});
        }
        return rows;
    }

    /**
     * True when a report column is a calculation: the source types it as something other than a regular
     * column, or its formula is more than one quoted column reference.
     */
    public static boolean isCalculation(Map<String, String> analysisColumn) {
        if ("true".equals(analysisColumn.get("is_computation"))) {
            return true;
        }
        String formula = Text.strip(analysisColumn.getOrDefault("formula", ""));
        return !formula.isEmpty() && !COLUMN_REFERENCE.matcher(formula).matches();
    }

    // ------------------------------------------------------------------ overview

    private void planLogicalTables() {
        List<Map<String, String>> withMeasures = new ArrayList<>();
        List<Map<String, String>> others = new ArrayList<>();
        for (Map<String, String> table : rec("logical_tables")) {
            List<Map<String, String>> columns = where("logical_columns", "table_id", table.get("row_id"));
            if (columns.isEmpty()) {
                continue;
            }
            boolean measure = false;
            for (Map<String, String> column : columns) {
                measure |= "true".equals(column.get("is_measure"));
            }
            (measure ? withMeasures : others).add(table);
        }
        withMeasures.addAll(others);
        int number = 1;
        for (Map<String, String> table : withMeasures) {
            unitOfLogicalTable.put(table.get("row_id"),
                    String.format("table-%02d-%s", number++, slug(table.get("name"))));
        }
    }

    /** One unit per physical table of the Slice, in the Slice's own order (Row identifier order). */
    private void planPhysicalTables() {
        int number = 1;
        for (Map<String, String> table : rec("physical_tables")) {
            unitOfPhysicalTable.putIfAbsent(table.get("row_id"),
                    String.format("dbtable-%02d-%s", number++, slug(table.get("name"))));
        }
    }

    private void overview() {
        Draft draft = draft("overview", "overview", "Overview of subject area \"" + areaName + "\"");
        Map<String, String> area = one("presentation_subject_areas", areaId);
        List<Map<String, String>> tables = where("presentation_tables", "subject_area_id", areaId);
        Set<String> tableIds = new LinkedHashSet<>();
        for (Map<String, String> table : tables) {
            tableIds.add(table.get("row_id"));
        }

        Block about = draft.block("The subject area");
        if (area != null) {
            about.fact("- Subject area " + quote(area.get("name")) + ", built on business model "
                    + quote(area.get("business_model")) + ".", "presentation_subject_areas", areaId);
            String implicit = area.get("implicit_fact_column_id");
            if (implicit.isEmpty()) {
                about.fact("- Implicit fact column (the measure OBIEE uses when a query names no measure): none recorded.",
                        "presentation_subject_areas", areaId);
            } else {
                about.fact("- Implicit fact column (the measure OBIEE uses when a query names no measure): "
                        + logicalColumnName(implicit) + " -> " + implicit, "presentation_subject_areas", areaId);
            }
            if (!area.get("aliases").isEmpty()) {
                about.value("", "Other names (aliases) of the subject area", area.get("aliases"),
                        "presentation_subject_areas", areaId);
            }
            if (!area.get("is_auto_aggregate").isEmpty()) {
                about.value("", "Auto-aggregate setting, as written", area.get("is_auto_aggregate"),
                        "presentation_subject_areas", areaId);
            }
        }

        int areaColumns = 0;
        int shownMeasures = 0;
        List<Map<String, String>> orphans = new ArrayList<>();
        Block shown = draft.block("Presentation tables: what report authors see");
        for (Map<String, String> table : tables) {
            List<Map<String, String>> columns = where("presentation_columns", "table_id", table.get("row_id"));
            Set<String> behind = new LinkedHashSet<>();
            for (Map<String, String> column : columns) {
                areaColumns++;
                Map<String, String> logical = one("logical_columns", column.get("logical_column_id"));
                if (logical == null || !unitOfLogicalTable.containsKey(logical.get("table_id"))) {
                    orphans.add(column);
                    continue;
                }
                if ("true".equals(logical.get("is_measure"))) {
                    shownMeasures++;
                }
                behind.add(quote(name("logical_tables", logical.get("table_id"))) + " (unit "
                        + unitOfLogicalTable.get(logical.get("table_id")) + ")");
            }
            shown.require("presentation_tables", table.get("row_id"));
            shown.fact("- Presentation table " + quote(table.get("name")) + ": " + columns.size() + " columns; "
                    + "logical table(s) behind it: " + (behind.isEmpty() ? "none" : String.join(", ", behind)) + ".",
                    "presentation_tables", table.get("row_id"));
            if (!table.get("display_name").isEmpty()) {
                shown.value("    ", "Name shown to users (display name), as written", table.get("display_name"),
                        "presentation_tables", table.get("row_id"));
                namedVariables(shown, "        ", table.get("display_name"));
            }
            if (!table.get("aliases").isEmpty()) {
                shown.value("    ", "Other names (aliases)", table.get("aliases"), "presentation_tables",
                        table.get("row_id"));
            }
        }
        if (tables.isEmpty()) {
            shown.plain("- The subject area has no presentation tables. (presentation_tables; " + NO_ID + ")");
        }
        if (!orphans.isEmpty()) {
            Block loose = draft.block("Presentation columns with no logical column in the slice");
            for (Map<String, String> column : orphans) {
                loose.require("presentation_columns", column.get("row_id"));
                loose.fact("- Presentation column " + quote(name("presentation_tables", column.get("table_id"))) + "."
                        + quote(column.get("name")) + ": its logical column is "
                        + (column.get("logical_column_id").isEmpty() ? "not recorded" : "-> " + column.get("logical_column_id")
                                + ", which is not in the slice") + ".", "presentation_columns", column.get("row_id"));
            }
        }

        int measures = 0;
        for (Map<String, String> column : rec("logical_columns")) {
            if ("true".equals(column.get("is_measure"))) {
                measures++;
            }
        }
        Block counts = draft.block("Counts (counted from the slice's tables; a count has " + NO_ID + ")");
        counts.plain("- Presentation tables: " + tables.size() + "; presentation columns: " + areaColumns
                + ", of which measures: " + shownMeasures + ".");
        counts.plain("- Logical tables described: " + unitOfLogicalTable.size() + "; logical columns: "
                + rec("logical_columns").size() + ", of which measures: " + measures
                + " (measures not shown to report authors are described with their logical table).");
        counts.plain("- Logical table sources: " + rec("logical_table_sources").size() + "; physical tables: "
                + rec("physical_tables").size() + "; their columns: " + rec("physical_columns").size()
                + " (every column of each table, whether or not a logical column maps it); their keys: "
                + rec("physical_keys").size() + ".");
        counts.plain("- Business-model joins: " + rec("bmm_joins").size() + "; physical joins: "
                + rec("physical_joins").size() + ".");
        counts.plain("- Dimensions (drill paths): " + rec("logical_dimensions").size()
                + "; hierarchies offered to report authors: " + rec("presentation_hierarchies").size() + ".");
        counts.plain("- Variables: " + rec("variables").size() + "; security rows: " + rec("security_rls").size() + ".");
        counts.plain("- Reports built on the subject area: " + rec("analyses").size() + "; dashboards placing them: "
                + rec("dashboards").size() + "; saved filters they use: " + rec("saved_filters").size() + ".");
        counts.plain("- Errors about objects of the slice: " + rec("errors").size()
                + "; kinds of source content not read: " + rec("unparsed").size() + " (both listed in unit gaps).");

        Block elsewhere = draft.block("Estate-wide items not in this slice (counts from the manifest; " + NO_ID + ")");
        elsewhere.plain("- These exist in the full Knowledge Base and belong to no subject area, or not to this one. "
                + "They are not described in any unit and need not be covered; tell the reader they exist.");
        elsewhere.plain("- Object permissions whose target was not recorded: " + slice.fact("not_in_slice:permissions_without_target")
                + ". Find them in security_rls: kind = object_privilege and subject_id empty.");
        elsewhere.plain("- Role privileges of packages that no permission of this slice goes through: "
                + slice.fact("not_in_slice:role_privileges") + ". Find them in security_rls: kind = query_privilege, "
                + "row_id not in this slice's security_rls.");
        elsewhere.plain("- Init blocks that set no variable of this slice: " + slice.fact("not_in_slice:init_blocks")
                + ". Find them in init_blocks: row_id not in this slice's init_blocks.");
        elsewhere.plain("- Variables that no expression of this slice names: " + slice.fact("not_in_slice:variables")
                + ". Find them in variables: row_id not in this slice's variables.");

        Block run = draft.block("Where this came from (manifest; " + NO_ID + ")");
        run.plain("- Estate: " + slice.estate() + "; halves of the estate that were extracted: " + halves() + ".");
        if (!halves().contains("catalog")) {
            run.plain("- The estate has no Web Catalog half, so reports and dashboards were not examined.");
        }
        if (!halves().contains("rpd")) {
            run.plain("- The estate has no repository half.");
        }
        run.plain("- Knowledge Base extracted at " + slice.fact("kb_extracted_at") + " by extractor version "
                + slice.fact("kb_extractor_version") + ".");
        run.plain("- Units of this slice, in writing order: " + String.join(", ", unitOrder()) + ".");
    }

    // ------------------------------------------------------------------ a tab's Slice (story 4.1)

    /** The overview of a tab's Slice: the subject areas touched, the presentation tables, the counts, the units. */
    private void tabOverview() {
        Draft draft = draft("overview", "overview", "Overview of tab \"" + slice.fact("tab") + "\"");
        Block about = draft.block("The tab");
        about.plain("- Tab " + quote(slice.fact("tab")) + ": this slice holds what the columns the Operator confirmed "
                + "for the tab's FSD items need, walked as a subject-area slice is walked. Decisions: "
                + slice.fact("decisions") + ". (manifest; " + NO_ID + ")");
        about.plain("- Unit tab-fields lists every FSD item with its confirmed columns; items not confirmed contribute "
                + "nothing to the slice. (tab_fields; " + NO_ID + ")");
        Block areas = draft.block("Subject areas the confirmed columns are in");
        for (Map<String, String> area : rec("presentation_subject_areas")) {
            areas.require("presentation_subject_areas", area.get("row_id"));
            areas.fact("- Subject area " + quote(area.get("name")) + ", built on business model "
                    + quote(area.get("business_model")) + ".", "presentation_subject_areas", area.get("row_id"));
        }
        if (rec("presentation_subject_areas").isEmpty()) {
            areas.plain("- None: only logical or report columns were confirmed. (presentation_subject_areas; " + NO_ID + ")");
        }
        Block shown = draft.block("Presentation tables the confirmed columns are in");
        for (Map<String, String> table : rec("presentation_tables")) {
            List<String> confirmed = new ArrayList<>();
            for (Map<String, String> column : where("presentation_columns", "table_id", table.get("row_id"))) {
                if (itemsOfColumn.containsKey(column.get("row_id"))) {
                    confirmed.add(quote(column.get("name")));
                }
            }
            shown.require("presentation_tables", table.get("row_id"));
            shown.fact("- Presentation table " + quote(table.get("name")) + " of subject area "
                    + quote(name("presentation_subject_areas", table.get("subject_area_id"))) + ": confirmed columns "
                    + (confirmed.isEmpty() ? "none (in the slice only so that a reference resolves)"
                    : String.join(", ", confirmed)) + ".", "presentation_tables", table.get("row_id"));
        }
        List<Map<String, String>> orphans = new ArrayList<>();
        for (Map<String, String> column : rec("presentation_columns")) {
            Map<String, String> logical = one("logical_columns", column.get("logical_column_id"));
            if (itemsOfColumn.containsKey(column.get("row_id"))
                    && (logical == null || !unitOfLogicalTable.containsKey(logical.get("table_id")))) {
                orphans.add(column);
            }
        }
        if (!orphans.isEmpty()) {
            Block loose = draft.block("Confirmed presentation columns with no logical column in the slice");
            for (Map<String, String> column : orphans) {
                loose.require("presentation_columns", column.get("row_id"));
                loose.fact("- Presentation column " + quote(name("presentation_tables", column.get("table_id"))) + "."
                        + quote(column.get("name")) + ": its logical column is "
                        + (column.get("logical_column_id").isEmpty() ? "not recorded" : "-> " + column.get("logical_column_id")
                                + ", which is not in the slice") + ".", "presentation_columns", column.get("row_id"));
            }
        }
        int measures = 0;
        for (Map<String, String> column : rec("logical_columns")) {
            measures += "true".equals(column.get("is_measure")) ? 1 : 0;
        }
        Block counts = draft.block("Counts (counted from the slice's tables; a count has " + NO_ID + ")");
        counts.plain("- Confirmed columns: " + itemsOfColumn.size() + "; FSD items: " + rec(SliceSchema.TAB_FIELDS).size() + ".");
        counts.plain("- Logical tables described: " + unitOfLogicalTable.size() + " (each whole); logical columns: "
                + rec("logical_columns").size() + ", of which measures: " + measures + ".");
        counts.plain("- Logical table sources: " + rec("logical_table_sources").size() + "; physical tables: "
                + rec("physical_tables").size() + " (each whole); their columns: " + rec("physical_columns").size()
                + "; their keys: " + rec("physical_keys").size() + ".");
        counts.plain("- Business-model joins: " + rec("bmm_joins").size() + "; physical joins: " + rec("physical_joins").size()
                + "; dimensions: " + rec("logical_dimensions").size() + "; hierarchies offered to report authors: "
                + rec("presentation_hierarchies").size() + ".");
        counts.plain("- Variables: " + rec("variables").size() + "; security rows: " + rec("security_rls").size()
                + "; reports: " + rec("analyses").size() + ".");
        Block run = draft.block("Where this came from (manifest; " + NO_ID + ")");
        run.plain("- Estate: " + slice.estate() + "; halves of the estate that were extracted: " + halves() + ".");
        run.plain("- Knowledge Base extracted at " + slice.fact("kb_extracted_at") + " by extractor version "
                + slice.fact("kb_extractor_version") + ".");
        run.plain("- Units of this slice, in writing order: " + String.join(", ", unitOrder()) + ".");
    }

    /** One line per FSD item: its words and anchor, its status, and each confirmed column with what it is. */
    private void tabFields() {
        Draft draft = draft("tab-fields", "tab_fields", "The tab's fields: each FSD item and its confirmed columns");
        Block how = draft.block("How to read this unit");
        how.plain("- One entry per FSD item that needs data, in the FSD's order. A confirmed column is described in "
                + "the unit of its logical table (named after it); an item that is not confirmed contributes nothing. "
                + "The FSD's words are quoted from the tab's fsd.md; the anchor says where. (tab_fields; " + NO_ID + ")");
        for (Map<String, String> field : rec(SliceSchema.TAB_FIELDS)) {
            String id = field.get("row_id");
            Block block = draft.block("FSD items", "item " + field.get("item_id"));
            block.require(SliceSchema.TAB_FIELDS, id);
            boolean confirmed = field.get("status").equals("confirmed");
            block.fact("- Item " + field.get("item_id") + " (" + field.get("kind") + "), FSD anchor " + field.get("fsd_anchor")
                    + ": " + (confirmed ? "CONFIRMED" : "NOT CONFIRMED (" + field.get("status") + "): contributes nothing"),
                    SliceSchema.TAB_FIELDS, id);
            block.value("    ", "FSD text", field.get("fsd_text"), SliceSchema.TAB_FIELDS, id);
            for (String column : lines(field.get("row_ids"))) {
                block.plain("    - Column: " + describeColumn(column) + " -> " + column);
            }
            for (String note : lines(field.get("notes"))) {
                block.value("    ", "Decision note", note, SliceSchema.TAB_FIELDS, id);
            }
        }
        if (rec(SliceSchema.TAB_FIELDS).isEmpty()) {
            draft.block("FSD items").plain("- The tab's fsd_items.csv lists no item. (tab_fields; " + NO_ID + ")");
        }
    }

    /** A confirmed column in words: what it is, its logical column, measure, and the unit that describes it. */
    private String describeColumn(String id) {
        Map<String, String> presentation = one("presentation_columns", id);
        Map<String, String> report = one("analysis_columns", id);
        String logicalId = id;
        StringBuilder text = new StringBuilder();
        if (presentation != null) {
            text.append("presentation column ").append(quote(name("presentation_tables", presentation.get("table_id"))))
                    .append('.').append(quote(presentation.get("name")));
            logicalId = presentation.get("logical_column_id");
        } else if (report != null) {
            text.append("report column ").append(quote(report.get("heading").isEmpty() ? report.get("column_id")
                    : report.get("heading"))).append(" of report ").append(quote(name("analyses", report.get("analysis_id"))));
            if (!report.get("formula").isEmpty() && inline(report.get("formula"))) {
                text.append(", formula ").append(code(report.get("formula")));
            }
            logicalId = "";
            for (Map<String, String> link : where("report_lineage", "analysis_column_id", id)) {
                logicalId = link.get("logical_column_id");
            }
            text.append(" (described with its report in a reports unit)");
        } else {
            text.append("logical column");
        }
        Map<String, String> logical = one("logical_columns", logicalId);
        if (logical != null) {
            text.append(presentation == null && report == null ? " " : ", logical column ")
                    .append(quote(name("logical_tables", logical.get("table_id")))).append('.').append(quote(logical.get("name")))
                    .append("true".equals(logical.get("is_measure")) ? ", a measure" : ", not a measure");
            String unit = unitOfLogicalTable.get(logical.get("table_id"));
            if (unit != null) {
                text.append(" (unit ").append(unit).append(')');
            }
        }
        return text.toString();
    }

    private String halves() {
        String halves = slice.fact("halves_present:" + slice.estate());
        return halves.isEmpty() ? "not recorded" : halves;
    }

    private List<String> unitOrder() {
        List<String> order = new ArrayList<>();
        order.add("overview");
        if (tab) {
            order.add("tab-fields");
        }
        order.addAll(unitOfLogicalTable.values());
        order.addAll(unitOfPhysicalTable.values());
        order.addAll(List.of("joins", "hierarchies", "variables", "security", "reports (one unit per dashboard "
                + "or folder)", "saved-filters (when there are any)", "gaps"));
        return order;
    }

    // ------------------------------------------------------------------ one unit per logical table

    private void logicalTables() {
        Set<String> areaTables = new LinkedHashSet<>();
        for (Map<String, String> table : where("presentation_tables", "subject_area_id", areaId)) {
            areaTables.add(table.get("row_id"));
        }
        for (Map.Entry<String, String> planned : unitOfLogicalTable.entrySet()) {
            String tableId = planned.getKey();
            Map<String, String> table = one("logical_tables", tableId);
            String tableName = quote(table.get("name"));
            Draft draft = draft(planned.getValue(), "logical_table", "Logical table " + tableName);
            List<Map<String, String>> columns = where("logical_columns", "table_id", tableId);
            List<Map<String, String>> sources = where("logical_table_sources", "logical_table_id", tableId);
            Set<String> sourceIds = new LinkedHashSet<>();
            List<String> sourceNames = new ArrayList<>();
            for (Map<String, String> source : sources) {
                sourceIds.add(source.get("row_id"));
                sourceNames.add(quote(source.get("name")));
            }

            // Chapter 0: the table and what it offers. Chapter 1: how each table source feeds it. A unit
            // too long for one packet is cut between the two, and then between items.
            Block head = draft.block("The table");
            head.require("logical_tables", tableId);
            head.fact("- Logical table " + tableName + " in business model "
                    + quote(table.get("business_model")) + "; table sources the repository declares for it: "
                    + table.get("source_count") + ".", "logical_tables", tableId);
            int measures = 0;
            int hidden = 0;
            for (Map<String, String> column : columns) {
                measures += "true".equals(column.get("is_measure")) ? 1 : 0;
                hidden += presentationColumns(column.get("row_id"), areaTables).isEmpty() ? 1 : 0;
            }
            head.plain("- Columns of it in this slice: " + columns.size() + ", of which measures: " + measures
                    + (tab ? "; not confirmed for the tab: " : "; not shown to report authors in this subject area: ")
                    + hidden + ". (counted; " + NO_ID + ")");
            head.plain("- Table sources of it in this slice: " + sources.size() + (sources.isEmpty() ? ""
                    : ": " + String.join(", ", sourceNames)) + ". Each is described below under its own heading, "
                    + "with the physical tables it reads and what it maps each column from. (counted; " + NO_ID + ")");
            if (!table.getOrDefault("is_lookup_table", "").isEmpty()) {
                head.fact("- Marked in the repository as a lookup table: " + table.get("is_lookup_table") + ". A lookup "
                        + "table is read through the LOOKUP function (typically for translated names) and is not "
                        + "joined like a dimension.", "logical_tables", tableId);
            }
            List<Map<String, String>> keys = where("logical_keys", "logical_table_id", tableId);
            for (Map<String, String> key : keys) {
                head.require("logical_keys", key.get("row_id"));
                head.fact("- Key " + quote(key.get("name")) + " of logical table " + tableName + ": marked as primary key "
                        + shown(key.get("is_primary")) + "; made of column(s) " + logicalKeyColumns(key.get("columns"), tableId)
                        + ".", "logical_keys", key.get("row_id"));
            }
            if (keys.isEmpty()) {
                head.plain("- Keys of logical table " + tableName + ": not recorded. The repository declares no key for "
                        + "it. (logical_keys; " + NO_ID + ")");
            }

            for (Map<String, String> column : columns) {
                String id = column.get("row_id");
                Block block = draft.block("Columns: what the table offers", "logical column " + quote(column.get("name"))
                        + " of logical table " + tableName);
                boolean measure = "true".equals(column.get("is_measure"));
                if (measure) {
                    block.require("logical_columns", id);
                }
                block.fact("- Logical column " + quote(column.get("name")) + ": " + (measure ? "MEASURE" : "not a measure")
                        + "; writeable " + shown(column.get("is_writeable")) + "; mapped in "
                        + column.get("mapped_source_count") + " of " + column.get("source_count") + " table sources.",
                        "logical_columns", id);
                List<Map<String, String>> presented = presentationColumns(id, areaTables);
                for (Map<String, String> presentation : presented) {
                    block.require("presentation_columns", presentation.get("row_id"));
                    block.fact("    - Shown to report authors as " + quote(presentation.get("name"))
                            + " in presentation table " + quote(name("presentation_tables", presentation.get("table_id")))
                            + (presentation.get("override_logical_name").isEmpty() ? ""
                                    : "; name set by hand: " + presentation.get("override_logical_name"))
                            + (tab ? "; CONFIRMED FOR THE TAB, items " + String.join(", ",
                                    itemsOfColumn.get(presentation.get("row_id"))) : "") + ".",
                            "presentation_columns", presentation.get("row_id"));
                    if (!presentation.get("display_name").isEmpty()) {
                        block.value("        ", "Name shown to users (display name), as written",
                                presentation.get("display_name"), "presentation_columns", presentation.get("row_id"));
                        namedVariables(block, "            ", presentation.get("display_name"));
                    }
                }
                if (tab && itemsOfColumn.containsKey(id)) {
                    block.plain("    - CONFIRMED FOR THE TAB as a logical column, items " + String.join(", ", itemsOfColumn.get(id))
                            + ".");
                } else if (tab && presented.isEmpty()) {
                    block.plain("    - NOT CONFIRMED FOR THE TAB: no item of the tab uses it. It is described because its "
                            + "logical table is part of the model behind the tab.");
                } else if (presented.isEmpty()) {
                    block.plain("    - NOT SHOWN IN THIS SUBJECT AREA: it has no presentation column here. It is described "
                            + "because its logical table is part of the model behind the subject area.");
                }
                String descriptor = column.getOrDefault("descriptor_id_column_id", "");
                if (!descriptor.isEmpty()) {
                    block.fact("    - Its identifier is held by logical column " + logicalColumnName(descriptor) + " (OBIEE's "
                            + "\"descriptor ID\"): this column is the text shown, and filters use that column in its place. -> "
                            + descriptor, "logical_columns", id);
                }
                if (!column.get("time_series").isEmpty()) {
                    block.value("    ", "Time-series functions its formulas use", column.get("time_series"),
                            "logical_columns", id);
                }
                expression(block, "    ", "Formula (calculated from other logical objects)", column, "derived_expr",
                        "logical_columns", id);
                expression(block, "    ", "Definition kept on the column itself", column, "mapping_expr",
                        "logical_columns", id);
                for (Map<String, String> rule : where("aggregation_rules", "logical_column_id", id)) {
                    expression(block, "    ", "Aggregation rule" + ("true".equals(rule.get("is_default")) ? " (default)"
                            : " (not the default: " + shown(rule.get("is_default")) + ")"), rule, "expr",
                            "aggregation_rules", rule.get("row_id"));
                }
                Set<String> mappedIn = new LinkedHashSet<>();
                for (Map<String, String> map : where("lts_column_map", "logical_column_id", id)) {
                    if (sourceIds.contains(map.get("source_id"))) {
                        mappedIn.add(quote(name("logical_table_sources", map.get("source_id"))));
                    } else {
                        // A mapping whose source is not a source of this table has no source heading to go under.
                        mapping(block, "    ", "In source " + quote(name("logical_table_sources", map.get("source_id")))
                                + " it", map);
                    }
                }
                if (!mappedIn.isEmpty()) {
                    block.plain("    - Mapped in table source(s): " + String.join(", ", mappedIn)
                            + ". What it is mapped from is stated with each source.");
                }
                for (Map<String, String> lineage : where("lineage", "logical_column_id", id)) {
                    if (!sourceIds.contains(lineage.get("source_id"))) {
                        lineage(block, "    ", lineage);
                    }
                }
            }

            draft.chapter = 1;
            for (Map<String, String> source : sources) {
                String id = source.get("row_id");
                Block block = draft.block("Table sources: where the table's data comes from", "table source "
                        + quote(source.get("name")) + " of logical table " + tableName);
                block.require("logical_table_sources", id);
                block.fact("- Source " + quote(source.get("name")) + " of logical table " + tableName + ": active "
                        + shown(source.get("is_active")) + "; priority group " + (source.get("priority").isEmpty()
                                ? "not recorded (empty; it does not mean lowest)" : code(source.get("priority")))
                        + ".", "logical_table_sources", id);
                if (rendered(source, "group_by").isEmpty()) {
                    block.fact("    - Level of detail (grain): not recorded. The repository gives this source no level "
                            + "of detail.", "logical_table_sources", id);
                } else {
                    expression(block, "    ", "Level of detail (grain; the source's group by)", source, "group_by",
                            "logical_table_sources", id);
                }
                expression(block, "    ", "Content filter (where)", source, "where", "logical_table_sources", id);
                expression(block, "    ", "Fragmentation", source, "fragment", "logical_table_sources", id);
                if (one("security_rls", id + "#filter") != null) {
                    block.plain("    - A row filter restricts this source: see unit security.");
                }
                String closure = source.getOrDefault("closure_table_id", "");
                if (!closure.isEmpty()) {
                    block.fact("    - Parent-child hierarchy: this source names physical table "
                            + quote(name("physical_tables", closure)) + (unitOfPhysicalTable.containsKey(closure)
                                    ? " (unit " + unitOfPhysicalTable.get(closure) + ")" : "")
                            + " as the table that holds the parent-child relationships (its closure table). That "
                            + "table's unit names the member, parent, distance and leaf columns. -> " + closure,
                            "logical_table_sources", id);
                }
                List<Map<String, String>> maps = where("lts_column_map", "source_id", id);
                List<Map<String, String>> listed = new ArrayList<>(where("lts_tables", "source_id", id));
                listed.sort(Comparator.comparingInt(row -> number(row.get("position"))));
                Map<String, String> listedAs = new LinkedHashMap<>();
                for (Map<String, String> row : listed) {
                    if (!row.get("physical_table_id").isEmpty()) {
                        listedAs.putIfAbsent(row.get("physical_table_id"), row.get("row_id"));
                    }
                }
                Set<String> reads = new LinkedHashSet<>(listedAs.keySet());
                Set<String> mappedFrom = new LinkedHashSet<>();
                for (Map<String, String> map : maps) {
                    Map<String, String> mappedColumn = one("physical_columns", map.get("physical_column_id"));
                    if (mappedColumn != null) {
                        mappedFrom.add(mappedColumn.get("table_id"));
                    }
                }
                for (Map<String, String> map : maps) {
                    Map<String, String> physical = one("physical_columns", map.get("physical_column_id"));
                    if (physical != null) {
                        reads.add(physical.get("table_id"));
                    }
                }
                List<Map<String, String>> lineages = where("lineage", "source_id", id);
                for (Map<String, String> lineage : lineages) {
                    if (!lineage.get("physical_table_id").isEmpty()) {
                        reads.add(lineage.get("physical_table_id"));
                    }
                }
                for (Map<String, String> lineage : lineages) {
                    if (!lineage.get("physical_table_id").isEmpty()) {
                        mappedFrom.add(lineage.get("physical_table_id"));
                    }
                }
                if (reads.isEmpty()) {
                    block.plain("    - Physical tables this source is built on: none recorded, and no mapping in this "
                            + "slice reads one.");
                } else {
                    block.plain("    - Physical tables this source is built on: " + reads.size() + (listed.isEmpty()
                            ? " (the source's own list of tables is not recorded; taken from its mappings)."
                            : reads.size() == 1 ? " (one table, so the source makes no join)."
                                    : " (more than one, so the source joins them; the joins follow the tables)."));
                }
                for (String read : reads) {
                    physicalTable(block, "        ", read, listedAs.get(read), !listed.isEmpty(), mappedFrom.contains(read));
                }
                for (Map<String, String> row : listed) {
                    if (row.get("physical_table_id").isEmpty()) {
                        block.fact("        - Table " + row.get("position") + " of the source's list did not resolve to a "
                                + "physical table.", "lts_tables", row.get("row_id"));
                    }
                }
                sourceJoins(block, "    ", id, listed);
                Set<Map<String, String>> printed = java.util.Collections.newSetFromMap(new java.util.IdentityHashMap<>());
                for (Map<String, String> map : maps) {
                    Map<String, String> column = one("logical_columns", map.get("logical_column_id"));
                    mapping(block, "    ", "Logical column " + (column == null ? (map.get("logical_column_id").isEmpty()
                            ? "(not recorded)" : "-> " + map.get("logical_column_id")) : quote(column.get("name")))
                            + " here", map);
                    for (Map<String, String> lineage : lineages) {
                        if (!map.get("logical_column_id").isEmpty()
                                && lineage.get("logical_column_id").equals(map.get("logical_column_id")) && printed.add(lineage)) {
                            lineage(block, "        ", lineage);
                        }
                    }
                }
                for (Map<String, String> lineage : lineages) {
                    if (printed.add(lineage)) {
                        lineage(block, "    ", lineage);
                    }
                }
                if (maps.isEmpty()) {
                    block.plain("    - The slice holds no column mapping for this source.");
                }
            }
            if (sources.isEmpty()) {
                draft.block("Table sources: where the table's data comes from")
                        .plain("- The slice holds no table source for this logical table. (logical_table_sources; "
                                + NO_ID + ")");
            }
        }
    }

    /** One mapping line: what a logical column is mapped from in one table source. */
    private void mapping(Block block, String indent, String subject, Map<String, String> map) {
        String mapId = map.get("row_id");
        String text = rendered(map, "physical_expr");
        String physicalId = map.get("physical_column_id");
        String reads = physicalId.isEmpty() ? "no physical column recorded" : "physical column " + physicalColumn(physicalId);
        if (inline(text)) {
            // The physical column is stated here by name, type, precision, scale and nullable. Its own row is
            // cited in the unit of its physical table, and only there: it is a required row of that unit.
            block.fact(indent + "- " + subject + " is " + code(text) + "; " + reads + ".", "lts_column_map", mapId);
        } else {
            block.value(indent, subject + " (" + reads + ") is", text, "lts_column_map", mapId);
        }
        if (lines(map.get("physical_expr_refs")).size() > 1) {
            block.refs(indent + "    ", map.get("physical_expr_refs"));
        }
        namedVariables(block, indent + "    ", text);
    }

    private void lineage(Block block, String indent, Map<String, String> lineage) {
        Map<String, String> presentation = one("presentation_columns", lineage.get("presentation_column_id"));
        block.fact(indent + "- Lineage: presentation column "
                + (presentation == null ? lineage.get("presentation_column_id")
                        : quote(name("presentation_tables", presentation.get("table_id"))) + "."
                                + quote(presentation.get("name")))
                + " <- source " + quote(name("logical_table_sources", lineage.get("source_id"))) + " <- "
                + (lineage.get("physical_column_id").isEmpty()
                        ? (lineage.get("physical_table_id").isEmpty() ? "no physical column"
                                : "physical table " + quote(name("physical_tables", lineage.get("physical_table_id"))))
                        : "physical column " + physicalColumnName(lineage.get("physical_column_id"))) + ".",
                "lineage", lineage.get("row_id"));
    }

    private List<Map<String, String>> presentationColumns(String logicalColumnId, Set<String> areaTables) {
        List<Map<String, String>> found = new ArrayList<>();
        for (Map<String, String> column : where("presentation_columns", "logical_column_id", logicalColumnId)) {
            if (tab ? itemsOfColumn.containsKey(column.get("row_id")) : areaTables.contains(column.get("table_id"))) {
                found.add(column);
            }
        }
        return found;
    }

    /**
     * A physical table a source reads: its name, its kind, and the unit that describes it. The table's own
     * row, its definition (for a table that is a query, its SQL), its columns and its keys are required rows
     * of that unit, so they are cited there and nowhere else.
     */
    private void physicalTable(Block block, String indent, String id, String listedAs, boolean hasList, boolean mapped) {
        Map<String, String> table = one("physical_tables", id);
        if (table == null) {
            block.plain(indent + "- Reads physical table -> " + id + ", which is not in the slice.");
            return;
        }
        String note = (hasList && listedAs == null ? " It is not in the source's own list of tables; a mapping reads it."
                : "") + (mapped ? "" : " No column of this slice is mapped from it; the source still joins it.");
        String type = table.get("table_type");
        String alias = table.get("source_table_id");
        block.plain(indent + "- Reads physical table " + quote(table.get("name")) + " in " + code(table.get("container"))
                + "; type " + code(type)
                + ("select".equals(type) ? " (not a database table: it is a query kept in the repository)" : "")
                + (alias.isEmpty() ? "" : "; an alias of physical table " + quote(name("physical_tables", alias)))
                + ". Unit " + unitOfPhysicalTable.get(id) + " states the table whole: what it is in the database"
                + ("select".equals(type) ? " (its SQL)" : "") + ", every column, its keys." + note + " -> " + id
                + (listedAs == null ? "" : " " + block.cite("lts_tables", listedAs)));
    }

    /**
     * The joins a source makes between its physical tables (deliberate difference 34), one line per row of
     * {@code lts_joins}: a required row, because an inner or outer join inside a source decides which rows a
     * measure counts and no formula shows it. The condition is that of the physical join the row points at.
     */
    private void sourceJoins(Block block, String indent, String sourceId, List<Map<String, String>> listed) {
        List<Map<String, String>> joins = new ArrayList<>(where("lts_joins", "source_id", sourceId));
        if (joins.isEmpty()) {
            if (listed.size() > 1) {
                block.plain(indent + "- Joins between these tables: not recorded. The source lists " + listed.size()
                        + " tables and no join between them.");
            }
            return;
        }
        Map<String, Integer> placeOfTable = new HashMap<>();
        for (Map<String, String> row : listed) {
            placeOfTable.put(row.get("row_id"), number(row.get("position")));
        }
        joins.sort(Comparator.<Map<String, String>>comparingInt(j -> placeOfTable.getOrDefault(j.get("source_table_id"),
                Integer.MAX_VALUE)).thenComparingInt(j -> number(j.get("position"))));
        Set<String> distinct = new LinkedHashSet<>();
        for (Map<String, String> join : joins) {
            distinct.add(join.get("physical_join_id").isEmpty() ? join.get("row_id") : join.get("physical_join_id"));
        }
        block.plain(indent + "- Joins this source makes between its tables: " + joins.size() + " line(s) for " + distinct.size()
                + " join(s). The repository lists a join once under each of the two tables it joins; \"left\" and "
                + "\"right\" in a join type, and the cardinality, read from the first table named on the line to the second.");
        for (Map<String, String> join : joins) {
            Map<String, String> under = one("lts_tables", join.get("source_table_id"));
            Map<String, String> physical = one("physical_joins", join.get("physical_join_id"));
            String on;
            String pointer = physical == null ? "" : " -> " + physical.get("row_id");
            if (join.get("physical_join_id").isEmpty()) {
                on = "physical join not recorded, so the condition is not known";
            } else if (physical == null) {
                on = "physical join -> " + join.get("physical_join_id") + ", which is not in the slice";
            } else if ("foreign_key".equals(physical.get("kind"))) {
                on = "on foreign key " + quote(physical.get("name")) + ": " + physicalTableName(physical.get("table1_id"))
                        + columnNames(lines(physical.get("columns"))) + " = " + physicalTableName(physical.get("table2_id"))
                        + columnNames(lines(physical.get("referenced_columns")));
            } else {
                on = "on " + shown(physical.get("kind")) + " " + quote(physical.get("name")) + ", whose condition is "
                        + "stated in the joins unit";
            }
            block.require("lts_joins", join.get("row_id"));
            block.fact(indent + "    - Listed under " + (under == null ? "a table that is not in the slice"
                    : physicalTableName(under.get("physical_table_id"))) + ": " + physicalTableName(join.get("table1_id"))
                    + " joined to " + physicalTableName(join.get("table2_id")) + "; join type "
                    + (join.get("join_type").isEmpty() ? "not recorded" : code(join.get("join_type"))) + "; cardinality "
                    + (join.get("cardinality").isEmpty() ? "not recorded" : code(join.get("cardinality"))) + "; " + on + "."
                    + pointer,
                    "lts_joins", join.get("row_id"));
        }
    }

    /** The columns of a logical key, by name; a column of another logical table with its table's name. */
    private String logicalKeyColumns(String cell, String ownTableId) {
        List<String> names = new ArrayList<>();
        for (String id : lines(cell)) {
            Map<String, String> column = one("logical_columns", id);
            names.add(id.isEmpty() ? "(a column that did not resolve)" : column == null ? "-> " + id
                    : column.get("table_id").equals(ownTableId) ? quote(column.get("name")) : logicalColumnName(id));
        }
        return names.isEmpty() ? "none recorded" : String.join(", ", names);
    }

    // ------------------------------------------------------------------ one unit per physical table

    /**
     * One unit per physical table of the Slice: the table as the repository knows it, whole. A person who
     * builds the new database tables works from these units, so nothing of the table is left to another
     * unit except the joins, which are stated once, in the joins unit, and pointed at from here.
     *
     * <p>Required rows: the table, each of its definitions, each of its keys and each of its columns. A
     * document that leaves one out has not described the table a builder must create.
     */
    private void physicalTables() {
        // Physical column to the logical columns mapped from it, and physical table to the sources reading it.
        Map<String, Set<String>> mappedBy = new HashMap<>();
        Map<String, Set<String>> readBy = new HashMap<>();
        for (Map<String, String> map : rec("lts_column_map")) {
            Set<String> physical = new LinkedHashSet<>(lines(map.get("physical_expr_refs")));
            physical.add(map.get("physical_column_id"));
            for (String id : physical) {
                Map<String, String> column = one("physical_columns", id);
                if (column == null) {
                    continue;
                }
                if (!map.get("logical_column_id").isEmpty()) {
                    mappedBy.computeIfAbsent(id, k -> new LinkedHashSet<>()).add(map.get("logical_column_id"));
                }
                readBy.computeIfAbsent(column.get("table_id"), k -> new LinkedHashSet<>()).add(map.get("source_id"));
            }
        }
        for (Map<String, String> lineage : rec("lineage")) {
            if (!lineage.get("physical_table_id").isEmpty() && !lineage.get("source_id").isEmpty()) {
                readBy.computeIfAbsent(lineage.get("physical_table_id"), k -> new LinkedHashSet<>())
                        .add(lineage.get("source_id"));
            }
        }
        for (Map.Entry<String, String> planned : unitOfPhysicalTable.entrySet()) {
            String tableId = planned.getKey();
            Map<String, String> table = one("physical_tables", tableId);
            String tableName = quote(table.get("name"));
            String type = table.get("table_type");
            boolean query = "select".equals(type);
            Draft draft = draft(planned.getValue(), "physical_table", "Physical table " + tableName);
            List<Map<String, String>> columns = new ArrayList<>(where("physical_columns", "table_id", tableId));
            columns.sort(Comparator.comparingInt(column -> number(column.getOrDefault("position", ""))));
            List<Map<String, String>> keys = where("physical_keys", "table_id", tableId);

            Block head = draft.block("The table, as the repository knows it");
            head.require("physical_tables", tableId);
            head.fact("- Physical table " + tableName + " in " + code(table.get("container")) + " (the containers above "
                    + "it in the repository, outermost first; usually database and schema); type " + code(type)
                    + (query ? " (not a database table: it is a query kept in the repository)" : "") + ".",
                    "physical_tables", tableId);
            String alias = table.get("source_table_id");
            if (!alias.isEmpty()) {
                head.fact("    - It is an alias: another name in the repository for physical table "
                        + quote(name("physical_tables", alias)) + (unitOfPhysicalTable.containsKey(alias)
                                ? " (unit " + unitOfPhysicalTable.get(alias) + ")" : "") + " -> " + alias
                        + " . An alias is not a table of its own in the database.", "physical_tables", tableId);
            } else {
                head.plain("    - It is not an alias of another physical table.");
            }
            if (table.getOrDefault("description", "").isEmpty()) {
                head.plain("    - Description in the repository: not recorded.");
            } else {
                head.value("    ", "Description in the repository", table.get("description"), "physical_tables", tableId);
            }
            if (!table.getOrDefault("member_key_column_id", "").isEmpty() || !table.getOrDefault("parent_key_column_id", "").isEmpty()
                    || !table.getOrDefault("distance_column_id", "").isEmpty() || !table.getOrDefault("leaf_column_id", "").isEmpty()) {
                head.fact("    - It is the relationship table of a parent-child hierarchy (one row per member and ancestor). "
                        + parentChildColumns(table) + ".", "physical_tables", tableId);
            }
            List<Map<String, String>> definitions = where("physical_table_definitions", "table_id", tableId);
            for (Map<String, String> definition : definitions) {
                head.require("physical_table_definitions", definition.get("row_id"));
                head.value("    ", (query ? "Its SQL" : "Its object in the database") + " for database type "
                        + code(definition.get("database")), definition.get("text"), "physical_table_definitions",
                        definition.get("row_id"));
                namedVariables(head, "        ", definition.get("text"));
            }
            if (definitions.isEmpty()) {
                head.plain("    - " + (query ? "Its SQL: not recorded." : "Its object in the database: no separate name is "
                        + "recorded, so the repository knows it only by the name above.") + " (physical_table_definitions; "
                        + NO_ID + ")");
            }
            int mapped = 0;
            for (Map<String, String> column : columns) {
                mapped += mappedBy.containsKey(column.get("row_id")) ? 1 : 0;
            }
            head.plain("- Columns the repository lists for it: " + columns.size() + ", of which mapped by a logical column "
                    + "of this slice: " + mapped + "; not mapped by any: " + (columns.size() - mapped) + ". Keys: "
                    + keys.size() + ". (counted; " + NO_ID + ")");
            Set<String> readers = readBy.getOrDefault(tableId, Set.of());
            if (readers.isEmpty()) {
                head.plain("- Logical table sources of this slice that read it: none. It is in the slice because "
                        + "another row points at it (an alias of it, or a join).");
            } else {
                head.plain("- Logical table sources of this slice that read it: " + readers.size() + ".");
                for (String reader : readers) {
                    Map<String, String> source = one("logical_table_sources", reader);
                    String unit = source == null ? null : unitOfLogicalTable.get(source.get("logical_table_id"));
                    head.plain("    - " + (source == null ? "A source that is not in the slice"
                            : "Source " + quote(source.get("name")) + " of logical table "
                                    + quote(name("logical_tables", source.get("logical_table_id")))
                                    + (unit == null ? "" : " (unit " + unit + ")")) + " -> " + reader);
                }
            }

            Block keyBlock = draft.block("Keys");
            int number = 0;
            for (Map<String, String> key : keys) {
                number++;
                List<String> keyColumns = lines(key.get("columns"));
                int pointing = 0;
                for (Map<String, String> join : where("physical_joins", "table2_id", tableId)) {
                    pointing += "foreign_key".equals(join.get("kind")) && !keyColumns.isEmpty()
                            && lines(join.get("referenced_columns")).equals(keyColumns) ? 1 : 0;
                }
                keyBlock.require("physical_keys", key.get("row_id"));
                keyBlock.fact("- Key " + quote(key.get("name")) + " of physical table " + tableName + ": made of column(s)"
                        + (keyColumns.isEmpty() ? " none recorded" : columnNames(keyColumns).replace(" (", " ").replace(")", ""))
                        + ".", "physical_keys", key.get("row_id"));
                keyBlock.plain("    - Key " + number + " of " + keys.size() + " the repository declares for the table. The "
                        + "repository does not say whether a physical key is the primary key" + (keys.size() == 1
                                ? "; this is the only key it declares for the table" : "") + ". Foreign keys of this slice "
                        + "that point at exactly these column(s): " + pointing + ".");
            }
            if (keys.isEmpty()) {
                keyBlock.plain("- Keys of physical table " + tableName + ": not recorded. The repository declares no key "
                        + "for it, so nothing in the repository says which column(s) identify a row. (physical_keys; "
                        + NO_ID + ")");
            }

            Block joinBlock = draft.block("Foreign keys and joins (each is stated in full in the joins unit)");
            int stated = 0;
            for (Map<String, String> join : rec("physical_joins")) {
                boolean from = tableId.equals(join.get("table1_id"));
                boolean to = tableId.equals(join.get("table2_id"));
                if (!from && !to) {
                    continue;
                }
                stated++;
                if ("foreign_key".equals(join.get("kind"))) {
                    joinBlock.plain("- Foreign key " + quote(join.get("name")) + (from ? " FROM this table" : " of "
                            + physicalTableName(join.get("table1_id"))) + columnNames(lines(join.get("columns")))
                            + (to ? " TO this table" : " to " + (join.get("table2_id").isEmpty()
                                    ? "a table the repository does not name" : physicalTableName(join.get("table2_id"))))
                            + columnNames(lines(join.get("referenced_columns"))) + ". -> " + join.get("row_id"));
                } else {
                    joinBlock.plain("- " + shown(join.get("kind")) + " " + quote(join.get("name")) + " between "
                            + physicalTableName(join.get("table1_id")) + " and " + physicalTableName(join.get("table2_id"))
                            + "; its condition is in the joins unit. -> " + join.get("row_id"));
                }
            }
            if (stated == 0) {
                joinBlock.plain("- The slice holds no foreign key and no join from or to this table. (physical_joins; "
                        + NO_ID + ")");
            }

            draft.chapter = 1;
            String section = "Columns, in the order the repository lists them";
            if (columns.isEmpty()) {
                draft.block(section).plain("- The repository lists no column for this table. (physical_columns; " + NO_ID
                        + ")");
                continue;
            }
            Block columnBlock = draft.block(section, "the columns of physical table " + tableName);
            columnBlock.plain("- Each line below is one column: its place, name, data type, precision (length), scale "
                    + "(digits after the decimal point), whether it may be empty (nullable), its external name, and "
                    + "whether a logical column of this slice is mapped from it. \"not recorded\" means the repository "
                    + "says nothing; it does not mean no, and it does not mean 0.");
            columnBlock.plain("- About nullable: the repository export writes the nullable attribute only as true; it "
                    + "never writes false. \"nullable not stated\" therefore means the attribute is absent: most likely "
                    + "the column is not nullable in the repository, but the file does not say so, and the repository "
                    + "is not proof of the constraint in the database.");
            for (Map<String, String> column : columns) {
                String id = column.get("row_id");
                Set<String> users = mappedBy.getOrDefault(id, Set.of());
                List<String> userNames = new ArrayList<>();
                for (String user : users) {
                    userNames.add(logicalColumnName(user));
                }
                String special = column.get("special_type");
                columnBlock.require("physical_columns", id);
                columnBlock.fact("- Column " + shown(column.getOrDefault("position", "")) + " " + quote(column.get("name"))
                        + ": type " + shown(column.get("data_type")) + "; precision " + shown(column.get("precision"))
                        + "; scale " + shown(column.getOrDefault("scale", "")) + "; nullable " + nullable(column)
                        + "; external name " + (column.get("external_name").isEmpty() ? "not recorded"
                                : code(column.get("external_name")))
                        + (special.isEmpty() || special.equals("none") ? "" : "; special type " + code(special))
                        + (column.getOrDefault("source_column_id", "").isEmpty() ? "" : "; stands for column "
                                + physicalColumnName(column.get("source_column_id")) + " of the aliased table")
                        + (users.isEmpty() ? "; NOT MAPPED by any logical column of this slice"
                                : "; MAPPED by logical column(s) " + String.join(", ", userNames)) + ".",
                        "physical_columns", id);
            }
        }
    }

    // ------------------------------------------------------------------ joins

    private void joins() {
        Draft draft = draft("joins", "joins", "Joins: how the tables relate");
        for (Map<String, String> join : rec("bmm_joins")) {
            Block block = draft.block("Business-model joins (between logical tables)");
            block.require("bmm_joins", join.get("row_id"));
            block.fact("- " + logicalTable(join.get("table1_id")) + " (" + shown(join.get("multiplicity1")) + ") joins "
                    + logicalTable(join.get("table2_id")) + " (" + shown(join.get("multiplicity2")) + "); join type "
                    + shown(join.get("join_type")) + flag("; first side is an aggregate", join.get("is_aggregate1"))
                    + flag("; second side is an aggregate", join.get("is_aggregate2")) + ".", "bmm_joins",
                    join.get("row_id"));
        }
        if (rec("bmm_joins").isEmpty()) {
            draft.block("Business-model joins (between logical tables)")
                    .plain("- The slice holds no business-model join. (bmm_joins; " + NO_ID + ")");
        }
        String physicalSection = "Physical joins (between database tables of the slice)";
        for (Map<String, String> join : rec("physical_joins")) {
            String id = join.get("row_id");
            Block block = draft.block(physicalSection, shown(join.get("kind")) + " " + quote(join.get("name")));
            block.require("physical_joins", id);
            if (!"foreign_key".equals(join.get("kind"))) {
                block.fact("- " + shown(join.get("kind")) + " " + quote(join.get("name")) + ": "
                        + physicalTableName(join.get("table1_id")) + " to " + physicalTableName(join.get("table2_id"))
                        + (join.get("cardinality").isEmpty() ? "" : "; cardinality " + code(join.get("cardinality"))) + ".",
                        "physical_joins", id);
                expression(block, "    ", "Join condition", join, "expr", "physical_joins", id);
                continue;
            }
            List<String> own = lines(join.get("columns"));
            List<String> far = lines(join.get("referenced_columns"));
            boolean known = !join.get("table2_id").isEmpty();
            block.fact("- foreign_key " + quote(join.get("name")) + ": from " + physicalTableName(join.get("table1_id"))
                    + columnNames(own) + " to " + (known ? physicalTableName(join.get("table2_id")) + columnNames(far)
                            : "a table the repository does not name (the foreign key has no link to a key, or the "
                                    + "link did not resolve)")
                    + (join.get("cardinality").isEmpty() ? "" : "; cardinality " + code(join.get("cardinality"))) + ".",
                    "physical_joins", id);
            if (!own.isEmpty() && own.size() == far.size()) {
                for (int i = 0; i < own.size(); i++) {
                    block.fact("    - Column pair " + (i + 1) + " of " + own.size() + ": " + physicalColumn(own.get(i))
                            + " = " + physicalColumn(far.get(i)) + ".", "physical_joins", id);
                }
                continue;
            }
            if (!own.isEmpty()) {
                block.fact("    - Foreign-key column(s), on the first table: " + String.join("; ", physicalColumns(own))
                        + ".", "physical_joins", id);
            }
            if (!far.isEmpty()) {
                block.fact("    - Column(s) of the key it points at, on the second table: "
                        + String.join("; ", physicalColumns(far)) + ".", "physical_joins", id);
            }
            if (known && far.isEmpty()) {
                block.plain("    - The key it points at lists no column, so the column pairs are not known.");
            } else if (!own.isEmpty() && !far.isEmpty()) {
                block.plain("    - The two lists differ in length, so the column pairs cannot be stated.");
            }
        }
        if (rec("physical_joins").isEmpty()) {
            draft.block(physicalSection).plain("- The slice holds no physical join. (physical_joins; " + NO_ID + ")");
        }
    }

    private String physicalTableName(String id) {
        return id.isEmpty() ? "a table that was not recorded" : quote(name("physical_tables", id));
    }

    /** The bare column names of a foreign key's column list, in brackets, for the line that states the join. */
    private String columnNames(List<String> columnIds) {
        if (columnIds.isEmpty()) {
            return "";
        }
        List<String> names = new ArrayList<>();
        for (String id : columnIds) {
            names.add(id.isEmpty() ? "a column that did not resolve" : one("physical_columns", id) == null ? "-> " + id
                    : quote(name("physical_columns", id)));
        }
        return " (" + String.join(", ", names) + ")";
    }

    private List<String> physicalColumns(List<String> columnIds) {
        List<String> names = new ArrayList<>();
        for (String id : columnIds) {
            names.add(physicalColumn(id));
        }
        return names;
    }

    private String logicalTable(String id) {
        if (id.isEmpty()) {
            return "a table that was not recorded";
        }
        return quote(name("logical_tables", id))
                + (unitOfLogicalTable.containsKey(id) ? "" : " [outside " + scope() + ": only its name is in the slice]");
    }

    private static String flag(String label, String value) {
        return "true".equals(value) ? label : "";
    }

    // ------------------------------------------------------------------ hierarchies

    private void hierarchies() {
        Draft draft = draft("hierarchies", "hierarchies", "Hierarchies: the drill paths");
        Set<String> used = new LinkedHashSet<>();
        for (Map<String, String> dimension : rec("logical_dimensions")) {
            Block block = draft.block("Dimensions and their levels", "dimension " + quote(dimension.get("name")));
            String id = dimension.get("row_id");
            block.require("logical_dimensions", id);
            block.fact("- Dimension " + quote(dimension.get("name")) + " in business model "
                    + quote(dimension.get("business_model")) + ": " + dimension.get("level_count") + " levels"
                    + flag("; parent-child (value based: the depth is not fixed)", dimension.get("is_value_based"))
                    + flag("; ragged", dimension.get("is_ragged")) + flag("; levels can be skipped", dimension.get("is_skipped"))
                    + "; marked as a time dimension: " + shown(dimension.getOrDefault("is_time", ""))
                    + ".", "logical_dimensions", id);
            if (!dimension.get("default_root_level_id").isEmpty()) {
                block.fact("    - Default root level: " + quote(name("logical_levels", dimension.get("default_root_level_id")))
                        + ".", "logical_dimensions", id);
            }
            List<Map<String, String>> levels = new ArrayList<>(where("logical_levels", "dimension_id", id));
            levels.sort(Comparator.comparingInt(level -> number(level.get("position"))));
            for (Map<String, String> level : levels) {
                String key = level.get("drill_key_column_id");
                block.fact("    - Level " + level.get("position") + " " + quote(level.get("name"))
                        + flag(" (grand total)", level.get("is_grand_total"))
                        + (level.get("member_count").isEmpty() ? "" : "; member count as recorded " + level.get("member_count"))
                        + "; key column: " + (key.isEmpty() ? "none recorded" : logicalColumnName(key) + " -> " + key),
                        "logical_levels", level.get("row_id"));
                block.fact("        - Level directly above it (its parent): " + levelNames(level.getOrDefault("parent_level_ids", ""),
                        "none recorded (a top level)") + "; level(s) directly below it (its children): "
                        + levelNames(level.getOrDefault("child_level_ids", ""), "none recorded (a lowest level)") + ".",
                        "logical_levels", level.get("row_id"));
                for (Map<String, String> levelKey : where("logical_level_keys", "level_id", level.get("row_id"))) {
                    block.fact("        - Key " + quote(levelKey.get("name")) + " of level " + quote(level.get("name"))
                            + ": primary key of the level " + shown(levelKey.get("is_primary")) + "; chronological key "
                            + shown(levelKey.get("is_chronological")) + "; used for drill-down "
                            + shown(levelKey.get("is_for_drill_down")) + "; made of column(s) "
                            + logicalKeyColumns(levelKey.get("columns"), "") + ".", "logical_level_keys",
                            levelKey.get("row_id"));
                }
            }
            if ("true".equals(dimension.get("is_value_based"))) {
                parentChild(block, levels);
            }
            List<Map<String, String>> offered = where("presentation_hierarchies", "dimension_id", id);
            for (Map<String, String> hierarchy : offered) {
                used.add(hierarchy.get("row_id"));
                presentationHierarchy(block, hierarchy);
            }
            if (offered.isEmpty()) {
                block.plain("    - Not offered to report authors as a hierarchy in " + scope() + ".");
            }
        }
        for (Map<String, String> hierarchy : rec("presentation_hierarchies")) {
            if (!used.contains(hierarchy.get("row_id"))) {
                presentationHierarchy(draft.block("Hierarchies offered to report authors whose dimension is not in the slice"),
                        hierarchy);
            }
        }
        if (rec("logical_dimensions").isEmpty() && rec("presentation_hierarchies").isEmpty()) {
            draft.block("Dimensions and their levels").plain("- The slice holds no dimension and no hierarchy. "
                    + "(logical_dimensions, presentation_hierarchies; " + NO_ID + ")");
        }
    }

    private String levelNames(String cell, String whenNone) {
        List<String> names = new ArrayList<>();
        for (String id : lines(cell)) {
            names.add(id.isEmpty() ? "(a level that did not resolve)" : quote(name("logical_levels", id)));
        }
        return names.isEmpty() ? whenNone : String.join(", ", names);
    }

    /**
     * What defines a parent-child dimension (deliberate difference 36): the relationship table named by a
     * table source of the logical tables the dimension's level keys are on, and that table's four columns.
     * The rows that state this are required rows of other units, so the lines here carry pointers, not
     * citations, and say which unit to cite from.
     */
    private void parentChild(Block block, List<Map<String, String>> levels) {
        Set<String> logicalTables = new LinkedHashSet<>();
        for (Map<String, String> level : levels) {
            Set<String> columns = new LinkedHashSet<>();
            columns.add(level.get("drill_key_column_id"));
            for (Map<String, String> key : where("logical_level_keys", "level_id", level.get("row_id"))) {
                columns.addAll(lines(key.get("columns")));
            }
            for (String column : columns) {
                Map<String, String> found = one("logical_columns", column);
                if (found != null) {
                    logicalTables.add(found.get("table_id"));
                }
            }
        }
        int stated = 0;
        for (String logicalTable : logicalTables) {
            for (Map<String, String> source : where("logical_table_sources", "logical_table_id", logicalTable)) {
                String closure = source.getOrDefault("closure_table_id", "");
                Map<String, String> table = one("physical_tables", closure);
                if (closure.isEmpty()) {
                    continue;
                }
                stated++;
                String unit = unitOfLogicalTable.get(logicalTable);
                block.plain("    - Parent-child relationships: table source " + quote(source.get("name")) + " of logical table "
                        + quote(name("logical_tables", logicalTable)) + " names physical table "
                        + quote(name("physical_tables", closure)) + " as the table that holds them (stated with its "
                        + "citation in unit " + (unit == null ? "of that logical table" : unit) + "). -> " + source.get("row_id"));
                if (table != null) {
                    block.plain("        - In that table: " + parentChildColumns(table) + " (stated with its citation in unit "
                            + unitOfPhysicalTable.get(closure) + "). -> " + closure);
                }
            }
        }
        if (stated == 0) {
            block.plain("    - Parent-child relationships: not recorded in the slice. No table source of the logical "
                    + "table(s) its level keys are on names a relationship table" + (logicalTables.isEmpty()
                            ? ", and its levels have no key column that says which logical table it is built on" : "") + ".");
        }
    }

    private void presentationHierarchy(Block block, Map<String, String> hierarchy) {
        String id = hierarchy.get("row_id");
        block.require("presentation_hierarchies", id);
        Map<String, String> display = one("presentation_columns", hierarchy.get("display_column_id"));
        block.fact("    - Offered to report authors as hierarchy " + quote(hierarchy.get("name")) + " in presentation table "
                + quote(name("presentation_tables", hierarchy.get("table_id"))) + "; " + hierarchy.get("level_count")
                + " levels" + (display == null ? "" : "; shown by column " + quote(display.get("name"))) + ".",
                "presentation_hierarchies", id);
        List<Map<String, String>> levels = new ArrayList<>(where("presentation_levels", "hierarchy_id", id));
        levels.sort(Comparator.comparingInt(level -> number(level.get("position"))));
        for (Map<String, String> level : levels) {
            block.fact("        - Presentation level " + level.get("position") + " " + quote(level.get("name"))
                    + (level.get("logical_level_id").isEmpty() ? ""
                            : "; logical level " + quote(name("logical_levels", level.get("logical_level_id")))) + ".",
                    "presentation_levels", level.get("row_id"));
            List<Map<String, String>> columns = new ArrayList<>(where("presentation_level_columns", "level_id",
                    level.get("row_id")));
            columns.sort(Comparator.comparingInt(column -> number(column.get("position"))));
            for (Map<String, String> column : columns) {
                block.fact("            - Shows column " + quote(name("presentation_columns", column.get("presentation_column_id")))
                        + ".", "presentation_level_columns", column.get("row_id"));
            }
        }
    }

    // ------------------------------------------------------------------ variables and security

    private void variables() {
        Draft draft = draft("variables", "variables", "Variables: values set per user or per repository");
        // Where each name is written inside VALUEOF, and where each variable is in a reference list.
        Map<String, Map<String, Set<String>>> byName = new TreeMap<>(Text.CODE_POINT_ORDER);
        Map<String, Map<String, Set<String>>> byId = new HashMap<>();
        for (String table : slice.tableNames()) {
            if (!slice.table(table).has("row_id") || table.equals(SliceSchema.REQUIRED_ROWS) || table.equals(SliceSchema.UNITS)) {
                continue;
            }
            for (Map<String, String> row : rec(table)) {
                String pointer = table + " -> " + row.get("row_id");
                for (Map.Entry<String, String> cell : row.entrySet()) {
                    if (cell.getValue().isEmpty()) {
                        continue;
                    }
                    if (Slice.isReferenceList(table, cell.getKey())) {
                        for (String id : lines(cell.getValue())) {
                            if (one("variables", id) != null) {
                                byId.computeIfAbsent(id, k -> new LinkedHashMap<>())
                                        .computeIfAbsent(pointer, k -> new LinkedHashSet<>()).add(cell.getKey());
                            }
                        }
                    } else if (Slice.isExpression(cell.getKey())) {
                        for (String name : Variables.namesIn(cell.getValue())) {
                            byName.computeIfAbsent(name, k -> new LinkedHashMap<>())
                                    .computeIfAbsent(pointer, k -> new LinkedHashSet<>()).add(cell.getKey());
                        }
                    }
                }
            }
        }
        Map<String, List<String>> idsByName = new HashMap<>();
        for (Map<String, String> variable : rec("variables")) {
            idsByName.computeIfAbsent(variable.get("name"), k -> new ArrayList<>()).add(variable.get("row_id"));
        }
        // A name written in another letter case than the repository's goes with the variable it stands for.
        Map<String, List<String>> otherCase = new HashMap<>();
        List<String> undefined = new ArrayList<>();
        for (String name : byName.keySet()) {
            if (idsByName.containsKey(name)) {
                continue;
            }
            List<String> ids = Variables.idsFor(name, idsByName);
            if (ids.isEmpty()) {
                undefined.add(name);
            }
            for (String id : ids) {
                otherCase.computeIfAbsent(id, k -> new ArrayList<>()).add(name);
            }
        }

        if (rec("variables").isEmpty() && byName.isEmpty()) {
            draft.block("Variables the slice's expressions use").plain("- No expression of the slice names a "
                    + "variable. (variables; " + NO_ID + ")");
            return;
        }
        Block summary = draft.block("Summary (counted from the slice's tables; a count has " + NO_ID + ")");
        summary.plain("- Variable names the slice's expressions, display-name formulas, filters and SQL texts use "
                + "inside VALUEOF(...): " + byName.size() + ".");
        summary.plain("- Of these names, defined in the repository (the Knowledge Base has a variables row): "
                + (byName.size() - undefined.size()) + "; used by name and not defined in the repository: "
                + undefined.size() + ".");
        summary.plain("- Variables rows in the slice: " + rec("variables").size() + " (a variable named inside "
                + "VALUEOF, or one a formula's reference list points at).");

        for (Map<String, String> variable : rec("variables")) {
            String id = variable.get("row_id");
            Block block = draft.block("Variables defined in the repository", "variable " + quote(variable.get("name")));
            block.require("variables", id);
            block.fact("- Variable " + quote(variable.get("name")) + ": "
                    + ("true".equals(variable.get("is_session")) ? "session variable (a value per user, set at login)"
                            : "repository variable (one value for everyone); is_session " + shown(variable.get("is_session")))
                    + (variable.get("security_mask").isEmpty() ? "" : "; security mask " + code(variable.get("security_mask")))
                    + ".", "variables", id);
            if ("true".equals(variable.get("redacted"))) {
                block.fact("    - Its value was withheld by the extractor because it is, or equals, a credential.",
                        "variables", id);
            } else {
                expression(block, "    ", "Default value", variable, "expr", "variables", id);
            }
            Map<String, String> init = one("init_blocks", variable.get("init_block_id"));
            if (init != null) {
                String initId = init.get("row_id");
                block.fact("    - Set by init block " + quote(init.get("name")) + ": session " + shown(init.get("is_session"))
                        + "; enabled " + shown(init.get("is_enabled")) + "; refresh period " + shown(init.get("refresh_period"))
                        + ".", "init_blocks", initId);
                block.value("    ", "The init block's query", init.get("init_string"), "init_blocks", initId);
                namedVariables(block, "        ", init.get("init_string"));
            } else {
                block.fact("    - Set by init block: " + (variable.get("init_block_id").isEmpty() ? "none recorded"
                        : "-> " + variable.get("init_block_id")) + ".", "variables", id);
            }
            users(block, "    ", "Named exactly, inside VALUEOF(...), in", byName.get(variable.get("name")));
            for (String name : otherCase.getOrDefault(id, List.of())) {
                users(block, "    ", "Written in another letter case, as " + code(name) + ", in", byName.get(name));
            }
            users(block, "    ", "Pointed at by a formula's reference list (a %n placeholder) in", byId.get(id));
        }
        String undefinedSection = "Variable names used that the repository does not define";
        if (!undefined.isEmpty()) {
            draft.block(undefinedSection).plain("- For each name below the Knowledge Base has no variables row: not "
                    + "with exactly this name, and not with a name that differs only in letter case. Such a name can "
                    + "be a session variable the server sets itself, or one set outside the repository; the repository "
                    + "does not say which. State each as \"used by name, not defined in the repository\". (variables; "
                    + NO_ID + ")");
        }
        for (String name : undefined) {
            Block block = draft.block(undefinedSection, "variable name " + code(name));
            block.plain("- Name " + code(name) + ": used by name, not defined in the repository. (variables; " + NO_ID
                    + ")");
            users(block, "    ", "Named inside VALUEOF(...) in", byName.get(name));
        }
    }

    /** The rows that use a variable or a name, each as a pointer with the columns it was found in. */
    private static void users(Block block, String indent, String label, Map<String, Set<String>> users) {
        if (users == null || users.isEmpty()) {
            return;
        }
        block.plain(indent + "- " + label + " " + users.size() + " row(s) of the slice:");
        for (Map.Entry<String, Set<String>> user : users.entrySet()) {
            block.plain(indent + "    - " + user.getKey() + " (in " + String.join(", ", user.getValue()) + ")");
        }
    }

    /** A pointer under a text that names variables inside VALUEOF(...): the variables unit says what each is. */
    private static void namedVariables(Block block, String indent, String text) {
        for (String name : new LinkedHashSet<>(Variables.namesIn(text))) {
            block.plain(indent + "- Names variable " + code(name) + " inside VALUEOF(...): unit variables lists every "
                    + "such name and says whether the repository defines it.");
        }
    }

    private void security() {
        Draft draft = draft("security", "security", "Security: who may see or change what");
        // The holders of each privilege package: one entry per role, with a pointer to its first row.
        Map<String, Map<String, String>> holdersOfPackage = new LinkedHashMap<>();
        for (Map<String, String> row : rec("security_rls")) {
            if ("query_privilege".equals(row.get("kind"))) {
                String id = row.get("row_id");
                int dot = id.lastIndexOf(".q");
                holdersOfPackage.computeIfAbsent(dot > 0 ? id.substring(0, dot) : id, k -> new LinkedHashMap<>())
                        .putIfAbsent(role(row), id);
            }
        }
        for (Map<String, String> row : rec("security_rls")) {
            String id = row.get("row_id");
            String kind = row.get("kind");
            if ("row_filter".equals(kind)) {
                Block block = draft.block("Row filters: rules that restrict which rows a user sees", "row filter on "
                        + "table source " + quote(row.get("detail")));
                block.require("security_rls", id);
                block.fact("- Row filter on table source " + quote(row.get("detail")) + " of logical table "
                        + quote(name("logical_tables", row.get("subject_id"))) + ".", "security_rls", id);
                expression(block, "    ", "The filter", row, "expr", "security_rls", id);
            } else if ("object_privilege".equals(kind)) {
                Block block = draft.block("Permissions on objects");
                block.require("security_rls", id);
                String detail = row.get("detail");
                int via = detail.indexOf(" via ");
                String permission = via >= 0 ? detail.substring(0, via) : detail;
                String pack = via >= 0 ? detail.substring(via + 5) : "";
                block.fact("- Permission " + code(permission) + " on " + subject(row.get("subject_id"))
                        + "; given through privilege package " + (pack.isEmpty() ? "not recorded" : "-> " + pack) + ".",
                        "security_rls", id);
                Map<String, String> holders = holdersOfPackage.getOrDefault(pack, Map.of());
                if (holders.isEmpty()) {
                    block.plain("    - Held by: no role is recorded in the slice for that privilege package.");
                } else {
                    block.plain("    - Held by " + holders.size() + " role(s), through that privilege package, one per line:");
                    for (Map.Entry<String, String> holder : holders.entrySet()) {
                        block.plain("        - " + holder.getKey() + " -> " + holder.getValue());
                    }
                }
            } else {
                Block block = draft.block("Roles that hold a privilege package (one role per line)");
                block.require("security_rls", id);
                block.fact("- " + role(row) + " It holds query privilege " + code(row.get("subject_id"))
                        + " of privilege package " + code(row.get("detail")) + " (kind " + shown(kind) + ").",
                        "security_rls", id);
            }
        }
        if (rec("security_rls").isEmpty()) {
            draft.block("Row filters: rules that restrict which rows a user sees").plain("- The slice holds no "
                    + "security row: the repository records no row filter and no permission for the tables, sources, "
                    + "columns and presentation objects of " + scope() + ". (security_rls; " + NO_ID + ")");
        }
    }

    /**
     * One role, as a sentence: its display name, its name in the repository, and its guid, each where the
     * repository records one. The name is one text even when it holds a space (the sample has a role whose
     * name is two 64-character hexadecimal strings with a space between them).
     */
    private static String role(Map<String, String> queryPrivilege) {
        String name = queryPrivilege.get("role");
        String display = queryPrivilege.getOrDefault("role_display_name", "");
        String guid = queryPrivilege.getOrDefault("role_guid", "");
        String nameText = name.isEmpty() ? "name not recorded in the repository" : "name in the repository " + code(name)
                + (name.indexOf(' ') >= 0 ? " (this is the name of one role: the space is part of the name)" : "");
        return "Role with display name " + (display.isEmpty() ? "not recorded in the repository" : code(display)) + "; "
                + nameText + "; guid " + (guid.isEmpty() ? "not recorded in the repository" : code(guid)) + ".";
    }

    /** What an object permission is about, by the table that holds the row with that identifier. */
    private String subject(String id) {
        if (id.isEmpty()) {
            return "an object that was not recorded";
        }
        String[][] kinds = {{"presentation_subject_areas", "subject area"}, {"presentation_tables", "presentation table"},
                {"presentation_columns", "presentation column"}, {"logical_tables", "logical table"},
                {"logical_columns", "logical column"}, {"logical_table_sources", "table source"},
                {"physical_tables", "physical table"}, {"physical_columns", "physical column"}};
        for (String[] kind : kinds) {
            Map<String, String> row = one(kind[0], id);
            if (row != null) {
                return kind[1] + " " + quote(row.get("name")) + " -> " + id;
            }
        }
        return "-> " + id;
    }

    // ------------------------------------------------------------------ reports

    private void reports() {
        List<Map<String, String>> reports = rec("analyses");
        if (reports.isEmpty()) {
            Draft draft = draft("reports", "reports", tab ? "Reports the tab's confirmed columns come from"
                    : "Reports built on the subject area");
            draft.block("Reports").plain(tab ? "- No confirmed column of the tab is a report column, so no report is in "
                    + "the slice. (decisions; " + NO_ID + ")" : halves().contains("catalog")
                    ? "- No report of the estate's Web Catalog names this subject area. (analysis_criteria; " + NO_ID + ")"
                    : "- The estate has no Web Catalog half, so reports and dashboards were not examined. Calculations, "
                            + "filters and prompts defined inside reports are unknown. (manifest; " + NO_ID + ")");
            return;
        }
        Map<String, String> dashboardOfPage = new HashMap<>();
        for (Map<String, String> page : rec("dashboard_pages")) {
            dashboardOfPage.put(page.get("row_id"), page.get("dashboard_id"));
        }
        // A report is described once: with the first dashboard that places it, or with its folder.
        Map<String, Set<String>> dashboardsOfReport = new HashMap<>();
        for (Map<String, String> placed : rec("dashboard_contents")) {
            String dashboard = dashboardOfPage.get(placed.get("container_id"));
            if (dashboard != null && one("analyses", placed.get("target_id")) != null) {
                dashboardsOfReport.computeIfAbsent(placed.get("target_id"), k -> new TreeSet<>(Text.CODE_POINT_ORDER))
                        .add(dashboard);
            }
        }
        Map<String, List<Map<String, String>>> byDashboard = new LinkedHashMap<>();
        Map<String, List<Map<String, String>>> byFolder = new TreeMap<>(Text.CODE_POINT_ORDER);
        Map<String, String> unitOfReport = new HashMap<>();
        Map<String, String> unitOfGroup = new LinkedHashMap<>();
        int number = 1;
        for (Map<String, String> dashboard : rec("dashboards")) {
            byDashboard.put(dashboard.get("row_id"), new ArrayList<>());
            unitOfGroup.put(dashboard.get("row_id"), String.format("reports-%02d-%s", number++, slug(dashboard.get("name"))));
        }
        for (Map<String, String> report : reports) {
            Set<String> on = dashboardsOfReport.get(report.get("row_id"));
            String first = on == null ? null : on.iterator().next();
            if (first != null && byDashboard.containsKey(first)) {
                byDashboard.get(first).add(report);
            } else {
                String path = report.get("catalog_path");
                int slash = path.lastIndexOf('/');
                byFolder.computeIfAbsent(slash < 0 ? "" : path.substring(0, slash), k -> new ArrayList<>()).add(report);
            }
        }
        for (String folder : byFolder.keySet()) {
            unitOfGroup.put("folder:" + folder, String.format("reports-%02d-%s", number++,
                    slug(folder.isEmpty() ? "catalog-root" : folder.substring(folder.lastIndexOf('/') + 1))));
        }
        for (Map.Entry<String, List<Map<String, String>>> group : byDashboard.entrySet()) {
            for (Map<String, String> report : group.getValue()) {
                unitOfReport.put(report.get("row_id"), unitOfGroup.get(group.getKey()));
            }
        }
        for (Map.Entry<String, List<Map<String, String>>> group : byFolder.entrySet()) {
            for (Map<String, String> report : group.getValue()) {
                unitOfReport.put(report.get("row_id"), unitOfGroup.get("folder:" + group.getKey()));
            }
        }

        for (Map<String, String> dashboard : rec("dashboards")) {
            String id = dashboard.get("row_id");
            String unit = unitOfGroup.get(id);
            Draft draft = draft(unit, "reports", "Reports on dashboard " + quote(dashboard.get("name")));
            Block block = draft.block("The dashboard and its pages");
            block.fact("- Dashboard " + quote(dashboard.get("name")) + " at catalog path " + code(dashboard.get("catalog_path"))
                    + "; pages it lists: " + dashboard.get("page_count") + ".", "dashboards", id);
            List<Map<String, String>> pages = new ArrayList<>(where("dashboard_pages", "dashboard_id", id));
            pages.sort(Comparator.comparingInt(page -> number(page.get("position"))));
            block.plain("- Only the pages that place a report " + (tab ? "a confirmed column of the tab comes from"
                    : "built on this subject area") + " are in the slice: "
                    + pages.size() + ".");
            for (Map<String, String> page : pages) {
                Block pageBlock = draft.block("The dashboard and its pages");
                String pageId = page.get("row_id");
                StringBuilder listed = new StringBuilder();
                for (Map<String, String> reference : where("dashboard_contents", "container_id", id)) {
                    if (pageId.equals(reference.get("target_id"))) {
                        listed.append(' ').append(pageBlock.cite("dashboard_contents", reference.get("row_id")));
                    }
                }
                pageBlock.lines.add("- Page " + page.get("position") + " " + quote(page.get("name")) + " at catalog path "
                        + code(page.get("catalog_path")) + ". " + pageBlock.cite("dashboard_pages", pageId) + listed);
                List<Map<String, String>> items = new ArrayList<>(where("dashboard_contents", "container_id", pageId));
                items.sort(Comparator.comparingInt(item -> number(item.get("position"))));
                for (Map<String, String> item : items) {
                    Map<String, String> report = one("analyses", item.get("target_id"));
                    if (report != null) {
                        String where = unitOfReport.get(report.get("row_id"));
                        pageBlock.fact("    - Places report " + quote(report.get("name"))
                                + (unit.equals(where) ? " (described below)" : " (described in unit " + where + ")")
                                + ".", "dashboard_contents", item.get("row_id"));
                    } else {
                        StringBuilder twins = new StringBuilder();
                        for (Map<String, String> filter : where("analysis_filters", "owner_id", pageId)) {
                            if ("dashboard".equals(filter.get("scope"))
                                    && filter.get("reference").equals(item.get("reference"))) {
                                twins.append(' ').append(pageBlock.cite("analysis_filters", filter.get("row_id")));
                            }
                        }
                        pageBlock.lines.add("    - Places dashboard prompt " + code(item.get("reference")) + "; reference "
                                + shown(item.get("resolution")) + (item.get("target_id").isEmpty() ? ""
                                        : " -> " + item.get("target_id")) + ". It restricts the reports on the page. "
                                + pageBlock.cite("dashboard_contents", item.get("row_id")) + twins);
                    }
                }
            }
            for (Map<String, String> report : byDashboard.get(id)) {
                report(draft, report);
            }
        }
        for (Map.Entry<String, List<Map<String, String>>> group : byFolder.entrySet()) {
            Draft draft = draft(unitOfGroup.get("folder:" + group.getKey()), "reports", "Reports in catalog folder "
                    + quote(group.getKey().isEmpty() ? "/" : group.getKey()) + " that no dashboard in the slice places");
            for (Map<String, String> report : group.getValue()) {
                report(draft, report);
            }
        }
    }

    private void report(Draft draft, Map<String, String> report) {
        Block block = draft.block("Reports", "report " + quote(report.get("name")));
        String id = report.get("row_id");
        block.fact("- Report " + quote(report.get("name")) + " at catalog path " + code(report.get("catalog_path"))
                + "; criteria blocks " + report.get("criteria_count") + "; columns " + report.get("column_count") + ".",
                "analyses", id);
        List<Map<String, String>> criteria = new ArrayList<>(where("analysis_criteria", "analysis_id", id));
        criteria.sort(Comparator.comparingInt(c -> number(c.get("position"))));
        for (Map<String, String> criterion : criteria) {
            block.fact("    - Criteria block " + criterion.get("position") + " is on subject area "
                    + (criterion.get("subject_area").isEmpty() ? "(none stated)" : code(criterion.get("subject_area")))
                    + ".", "analysis_criteria", criterion.get("row_id"));
            List<Map<String, String>> columns = new ArrayList<>(where("analysis_columns", "criteria_id",
                    criterion.get("row_id")));
            columns.sort(Comparator.comparingInt(c -> number(c.get("position"))));
            for (Map<String, String> column : columns) {
                reportColumn(block, column);
            }
        }
        List<Map<String, String>> filters = new ArrayList<>(where("analysis_filters", "owner_id", id));
        filters.sort(Comparator.<Map<String, String>>comparingInt(f -> filterBlock(f.get("row_id")))
                .thenComparingInt(f -> number(f.get("position"))));
        if (!filters.isEmpty()) {
            block.plain("    - Filter (a tree: a deeper line is inside the line above it):");
            for (Map<String, String> filter : filters) {
                filterLine(block, "        ", filter);
            }
        }
        List<Map<String, String>> prompts = new ArrayList<>(where("analysis_prompts", "analysis_id", id));
        prompts.sort(Comparator.comparingInt(p -> number(p.get("position"))));
        for (Map<String, String> prompt : prompts) {
            String promptId = prompt.get("row_id");
            block.fact("    - Prompt " + prompt.get("position") + ": type " + shown(prompt.get("prompt_type"))
                    + (prompt.get("operator").isEmpty() ? "" : "; operator " + code(prompt.get("operator")))
                    + (prompt.get("set_variable").isEmpty() ? "" : "; sets variable(s) " + code(prompt.get("set_variable")))
                    + (prompt.get("ui_control").isEmpty() ? "" : "; control " + shown(prompt.get("ui_control"))) + ".",
                    "analysis_prompts", promptId);
            if (!prompt.get("column_formula").isEmpty()) {
                block.value("        ", "It asks about", prompt.get("column_formula"), "analysis_prompts", promptId);
                namedVariables(block, "            ", prompt.get("column_formula"));
            }
        }
        List<Map<String, String>> views = new ArrayList<>(where("analysis_views", "analysis_id", id));
        views.sort(Comparator.comparingInt(v -> number(v.get("position"))));
        for (Map<String, String> view : views) {
            block.fact("    - View " + view.get("position") + " " + code(view.get("name")) + ": type "
                    + shown(view.get("view_type"))
                    + (view.get("display_type").isEmpty() ? "" : "; chart kind " + shown(view.get("display_type"))
                            + (view.get("display_subtype").isEmpty() ? "" : " / " + view.get("display_subtype")))
                    + "; elements bound to a report column: " + view.get("bound_column_count")
                    + ("0".equals(view.get("references_views")) ? "" : "; arranges " + view.get("references_views")
                            + " other view(s)") + ".", "analysis_views", view.get("row_id"));
        }
    }

    private void reportColumn(Block block, Map<String, String> column) {
        String id = column.get("row_id");
        boolean calculation = isCalculation(column);
        if (calculation) {
            block.require("analysis_columns", id);
        }
        String what = "        - Column " + column.get("position") + (column.get("column_id").isEmpty() ? ""
                : " (id " + code(column.get("column_id")) + ")") + ": "
                + (calculation ? "CALCULATION" : column.get("formula").isEmpty() ? "no formula" : "plain column")
                + (column.get("heading").isEmpty() ? "" : "; heading " + code(column.get("heading")))
                + (column.get("table_heading").isEmpty() ? "" : "; table heading " + code(column.get("table_heading")))
                + (column.get("formula_type").isEmpty() || "sawx:sqlExpression".equals(column.get("formula_type")) ? ""
                        : "; formula type " + shown(column.get("formula_type")))
                + flag("; typed by the source as a computation", column.get("is_computation"))
                + flag("; calls a database function directly (EVALUATE)", column.get("uses_eval"));
        if (column.get("formula").isEmpty()) {
            block.fact(what + ".", "analysis_columns", id);
        } else {
            block.value("", what + "; formula", column.get("formula"), "analysis_columns", id);
            namedVariables(block, "            ", column.get("formula"));
        }
        Map<String, String> link = one("report_lineage", id + "#lineage");
        if (link == null) {
            return;
        }
        String resolution = link.get("resolution");
        if ("exact".equals(resolution) || "case_normalised".equals(resolution)) {
            block.fact("            - It is presentation column -> " + link.get("presentation_column_id") + " (matched "
                    + resolution + "; " + shown(link.get("source_state")) + ").", "report_lineage", link.get("row_id"));
        } else if ("unresolved".equals(resolution)) {
            block.fact("            - It names a column that matches no presentation column of this estate "
                    + "(resolution unresolved).", "report_lineage", link.get("row_id"));
        } else if ("outside_semantic_model".equals(resolution)) {
            block.fact("            - It reads an uploaded dataset, not the repository (resolution "
                    + "outside_semantic_model).", "report_lineage", link.get("row_id"));
        }
    }

    private void filterLine(Block block, String indent, Map<String, String> filter) {
        StringBuilder pad = new StringBuilder(indent);
        for (int i = 0; i < Math.min(number(filter.get("depth")), 12); i++) {
            pad.append("  ");
        }
        String id = filter.get("row_id");
        String text = filter.get("expr_text");
        String what = "- " + (filter.get("op").isEmpty() ? "" : "operator " + code(filter.get("op")) + " ")
                + (filter.get("expr_type").isEmpty() ? "" : "(" + filter.get("expr_type") + ")")
                + (filter.get("reference").isEmpty() ? "" : " refers to " + code(filter.get("reference")) + ", reference "
                        + shown(filter.get("resolution")) + (filter.get("target_id").isEmpty() ? ""
                                : " -> " + filter.get("target_id")))
                + flag(" calls a database function directly (EVALUATE)", filter.get("uses_eval"));
        if (Text.isBlank(text)) {
            block.fact(pad + what, "analysis_filters", id);
        } else if (inline(text)) {
            block.fact(pad + what + " " + code(text), "analysis_filters", id);
        } else {
            block.value(pad.toString(), what.substring(2) + " text", text, "analysis_filters", id);
        }
        namedVariables(block, pad + "    ", text);
    }

    private static int filterBlock(String rowId) {
        int at = rowId.lastIndexOf("#f");
        int dot = rowId.indexOf('.', Math.max(at, 0));
        return at < 0 || dot < 0 ? 0 : number(rowId.substring(at + 2, dot));
    }

    private void savedFilters() {
        if (rec("saved_filters").isEmpty()) {
            return;
        }
        Draft draft = draft("saved-filters", "saved_filters", "Saved filters the reports use");
        for (Map<String, String> saved : rec("saved_filters")) {
            Block block = draft.block("Saved filters", "saved filter " + quote(saved.get("name")));
            String id = saved.get("row_id");
            block.fact("- Saved filter " + quote(saved.get("name")) + " at catalog path " + code(saved.get("catalog_path"))
                    + flag("; calls a database function directly (EVALUATE)", saved.get("uses_eval")) + ".",
                    "saved_filters", id);
            List<Map<String, String>> conditions = new ArrayList<>(where("analysis_filters", "owner_id", id));
            conditions.sort(Comparator.comparingInt(f -> number(f.get("position"))));
            if (conditions.isEmpty()) {
                block.plain("    - It holds no condition.");
            } else {
                block.plain("    - Conditions (a tree: a deeper line is inside the line above it):");
            }
            for (Map<String, String> condition : conditions) {
                filterLine(block, "        ", condition);
            }
        }
    }

    // ------------------------------------------------------------------ gaps

    private void gaps() {
        Draft draft = draft("gaps", "gaps", "Gaps: what was not read, and what went wrong");
        Block note = draft.block("How to read this unit");
        note.plain("- The rows of errors and unparsed have " + NO_ID + ": state them without a citation and name the "
                + "table they came from.");
        for (Map<String, String> error : rec("errors")) {
            Block block = draft.block("Errors about objects of the slice (errors; " + NO_ID + ")");
            block.plain("- " + error.get("kind") + " in " + error.get("object_type") + ", about " + code(error.get("source_id")) + ":");
            block.fence("    ", error.get("reason"));
        }
        if (rec("errors").isEmpty()) {
            draft.block("Errors about objects of the slice (errors; " + NO_ID + ")")
                    .plain("- None: the extraction recorded no error about an object of the slice.");
        }
        // unparsed: per kind and object type the lines, and the totals a writer would otherwise add up by hand.
        Map<String, List<String>> byType = new LinkedHashMap<>();
        // Identifiers only: an internal id or name of an element whose content is read (see isIdentifierOnly).
        Map<String, Map<String, Long>> identifierCounts = new TreeMap<>(Text.CODE_POINT_ORDER);
        Map<String, long[]> totalOfType = new LinkedHashMap<>();
        Map<String, long[]> totalOfKind = new TreeMap<>(Text.CODE_POINT_ORDER);
        long[] all = new long[2];
        for (Map<String, String> gap : rec("unparsed")) {
            String type = gap.get("kind") + " in " + gap.get("object_type");
            if (isIdentifierOnly(gap)) {
                // Grouped by attribute, each element once with its occurrences over every object type.
                String detail = gap.get("detail");
                String attribute = detail.substring(detail.indexOf('@'));
                String element = detail.substring(0, detail.indexOf('@'));
                identifierCounts.computeIfAbsent(attribute, k -> new TreeMap<>(Text.CODE_POINT_ORDER)).merge(element,
                        occurrences(gap), Long::sum);
            } else {
                byType.computeIfAbsent(type, k -> new ArrayList<>()).add(gap.get("detail") + " (x" + gap.get("occurrences") + ")");
            }
            long occurrences = number(gap.get("occurrences")) == Integer.MAX_VALUE ? 0 : number(gap.get("occurrences"));
            for (long[] total : List.of(totalOfType.computeIfAbsent(type, k -> new long[2]),
                    totalOfKind.computeIfAbsent(gap.get("kind"), k -> new long[2]), all)) {
                total[0]++;
                total[1] += occurrences;
            }
        }
        String unparsedSection = "Source content that no table holds, per kind of object over the whole estate "
                + "(unparsed; " + NO_ID + ")";
        if (!byType.isEmpty()) {
            Block totals = draft.block("Totals of the source content that no table holds (computed from the unparsed "
                    + "rows of the slice; " + NO_ID + ")");
            totals.plain("- All kinds together: " + all[0] + " rows of unparsed, " + all[1] + " occurrences in the source.");
            for (Map.Entry<String, long[]> kind : totalOfKind.entrySet()) {
                totals.plain("- Kind " + kind.getKey() + ": " + kind.getValue()[0] + " rows, " + kind.getValue()[1]
                        + " occurrences.");
            }
            totals.plain("- A row is one attribute, element or text of one kind of object; its occurrences are how "
                    + "often it is in the source over the whole estate, not only in " + scope() + ". The lists "
                    + "below give each row.");
        }
        for (Map.Entry<String, List<String>> type : byType.entrySet()) {
            Block block = draft.block(unparsedSection, type.getKey());
            long[] total = totalOfType.get(type.getKey());
            block.plain("- " + type.getKey() + ": " + total[0] + " row(s), " + total[1] + " occurrences in all; each "
                    + "with how often it occurs (several to a line, \"; \" between them):");
            List<String> details = type.getValue();
            for (int from = 0; from < details.size(); from += GAPS_PER_LINE) {
                block.plain("    - " + String.join("; ", details.subList(from, Math.min(details.size(), from + GAPS_PER_LINE))));
            }
        }
        if (byType.isEmpty()) {
            draft.block(unparsedSection).plain("- None recorded for the kinds of object in the slice"
                    + (identifierCounts.isEmpty() ? "." : ", apart from the identifiers listed next."));
        }
        if (!identifierCounts.isEmpty()) {
            Block ids = draft.block("Identifiers that were not read; the content of their elements is read (unparsed; "
                    + NO_ID + ")");
            ids.plain("- These rows are counted in the totals above. Each is only an internal identifier "
                    + "or the internal name of an element whose content the Knowledge Base holds, so no meaning is "
                    + "missing. Do not report them as content that was not read.");
            for (Map.Entry<String, Map<String, Long>> attribute : identifierCounts.entrySet()) {
                List<String> elements = new ArrayList<>();
                for (Map.Entry<String, Long> element : attribute.getValue().entrySet()) {
                    elements.add(element.getKey() + " (x" + element.getValue() + ")");
                }
                ids.plain("    - " + attribute.getKey() + ", identifier of " + String.join(", ", elements)
                        + ": identifier of each not read; its content is read.");
            }
        }
        Block left = draft.block("Left out of the slice (manifest; " + NO_ID + ")", "what was left out of the slice");
        left.plain("- Rows of the Knowledge Base's errors table that are not about an object of the slice: "
                + slice.fact("errors_not_copied") + ".");
        left.plain("- Rows of the Knowledge Base's unparsed table that are about whole object types or files with no "
                + "reader, or about kinds of object the slice does not hold: " + slice.fact("unparsed_not_copied") + ".");
        // Named, so that a document can say which kinds of object nobody has read.
        Map<String, List<String[]>> leftByKind = new LinkedHashMap<>();
        for (String line : lines(slice.fact("unparsed_not_copied_by_type"))) {
            String[] cells = line.split(java.util.regex.Pattern.quote(Slice.UNPARSED_SEPARATOR), -1);
            if (cells.length >= 4) {
                // An object type may itself hold the separator; the kind is first and the two counts are last.
                String type = String.join(Slice.UNPARSED_SEPARATOR, java.util.Arrays.asList(cells).subList(1, cells.length - 2));
                leftByKind.computeIfAbsent(cells[0], k -> new ArrayList<>()).add(new String[] {type,
                        cells[cells.length - 2], cells[cells.length - 1]});
            }
        }
        for (Map.Entry<String, List<String[]>> kind : leftByKind.entrySet()) {
            long rows = 0;
            long occurrences = 0;
            for (String[] type : kind.getValue()) {
                rows += number(type[1]) == Integer.MAX_VALUE ? 0 : number(type[1]);
                occurrences += number(type[2]) == Integer.MAX_VALUE ? 0 : number(type[2]);
            }
            boolean whole = kind.getKey().equals("unmodelled_type");
            left.plain("    - Left out, kind " + kind.getKey() + (whole ? " (a whole type of object that has no reader; "
                    + "nothing of such an object is in the Knowledge Base)" : "") + ": " + kind.getValue().size()
                    + " object type(s), " + rows + " row(s), " + occurrences + (whole ? " object(s)" : " occurrences")
                    + " in all; per object type:");
            for (String[] type : kind.getValue()) {
                left.plain("        - " + (type[0].isEmpty() ? "(no object type)" : code(type[0])) + ": " + type[1]
                        + " row(s), " + type[2] + (whole ? " object(s)" : " occurrences"));
            }
        }
        left.plain("- Rows that are in the slice only so that a reference from another row resolves (for example the "
                + "table at the far end of a join): " + slice.fact("boundary_rows") + ".");
    }

    /** The manifest fact naming the rows of unparsed that are only identifiers; set by the slice command. */
    public static final String IDENTIFIERS_FACT = "unparsed_identifiers_only";

    /**
     * True for a row of unparsed that is only an identifying attribute of an element whose content is read. Which
     * these are is source-format knowledge, so the slice command lists them, one {@code detail} per line, in the
     * manifest fact {@link #IDENTIFIERS_FACT}; this only looks the row up.
     */
    private static long occurrences(Map<String, String> gap) {
        int count = number(gap.get("occurrences"));
        return count == Integer.MAX_VALUE ? 0 : count;
    }

    private boolean isIdentifierOnly(Map<String, String> gap) {
        return "unread_attribute".equals(gap.get("kind")) && identifiersOnly.contains(gap.get("detail"));
    }

    // ------------------------------------------------------------------ printing values

    /** Prints an expression held in three columns: the rendered text (or the template when there is none) and what it names. */
    private void expression(Block block, String indent, String label, Map<String, String> row, String stem,
            String table, String id) {
        String text = rendered(row, stem);
        if (text.isEmpty()) {
            return;
        }
        block.value(indent, label, text, table, id);
        String refs = row.getOrDefault(stem + "_refs", "");
        // An aggregation rule names its own column; saying so again helps nobody.
        if (!refs.isEmpty() && !refs.equals(row.getOrDefault("logical_column_id", ""))) {
            block.refs(indent + "    ", refs);
        }
        namedVariables(block, indent + "    ", text);
    }

    private static String rendered(Map<String, String> row, String stem) {
        String rendered = row.getOrDefault(stem + "_text_desc", "");
        return rendered.isEmpty() ? row.getOrDefault(stem + "_text", "") : rendered;
    }

    private static boolean inline(String value) {
        return value.indexOf('\n') < 0 && value.indexOf('\r') < 0 && value.indexOf('`') < 0;
    }

    /** A single-line value between back quotes, exactly as extracted; a longer one must go through {@link Block#value}. */
    private static String code(String value) {
        if (value.isEmpty()) {
            return "(empty)";
        }
        return inline(value) ? "`" + value + "`" : "`" + value.replace("\r", " ").replace("\n", " ").replace('`', '\'') + "` (shortened to one line)";
    }

    private static String shown(String value) {
        return value.isEmpty() ? "not recorded" : value;
    }

    private static String quote(String name) {
        return "\"" + name.replace("\r", " ").replace("\n", " ") + "\"";
    }

    private static List<String> lines(String cell) {
        List<String> lines = new ArrayList<>();
        if (cell != null && !cell.isEmpty()) {
            for (String line : cell.split("\n", -1)) {
                lines.add(line);
            }
        }
        return lines;
    }

    private static int number(String text) {
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            return Integer.MAX_VALUE;
        }
    }

    private static String slug(String name) {
        String slug = Slice.folderName(name);
        return slug.length() > 40 ? slug.substring(0, 40).replaceAll("-$", "") : slug;
    }

    private String logicalColumnName(String id) {
        Map<String, String> column = one("logical_columns", id);
        return column == null ? "(not in the slice)" : quote(name("logical_tables", column.get("table_id"))) + "."
                + quote(column.get("name"));
    }

    /** A physical column as "table"."column", without its type: for a line whose neighbour states the type. */
    private String physicalColumnName(String id) {
        Map<String, String> column = one("physical_columns", id);
        return column == null ? "-> " + id : quote(name("physical_tables", column.get("table_id"))) + "." + quote(column.get("name"));
    }

    private String physicalColumn(String id) {
        Map<String, String> column = one("physical_columns", id);
        if (id.isEmpty()) {
            return "(a column that did not resolve)";
        }
        if (column == null) {
            return "-> " + id;
        }
        return quote(name("physical_tables", column.get("table_id"))) + "." + quote(column.get("name")) + " (type "
                + shown(column.get("data_type")) + (column.get("precision").isEmpty() ? "" : ", precision " + column.get("precision"))
                + (column.getOrDefault("scale", "").isEmpty() ? "" : ", scale " + column.get("scale"))
                + ", nullable " + nullable(column) + ")";
    }

    /**
     * A column's nullable value as the file states it. The export writes the attribute only as true, so an
     * absent one is "not stated": the packet never prints a true or false the file does not hold.
     */
    private static String nullable(Map<String, String> column) {
        return column.get("nullable").isEmpty() ? "not stated" : column.get("nullable");
    }

    /** The four columns that define a parent-child relationship table, each by name or "not recorded". */
    private String parentChildColumns(Map<String, String> table) {
        String[][] parts = {{"member_key_column_id", "Member column"}, {"parent_key_column_id", "parent (ancestor) column"},
                {"distance_column_id", "distance column (steps between the two)"}, {"leaf_column_id", "leaf-flag column"}};
        List<String> said = new ArrayList<>();
        for (String[] part : parts) {
            String id = table.getOrDefault(part[0], "");
            said.add(part[1] + ": " + (id.isEmpty() ? "not recorded" : physicalColumnName(id)));
        }
        return String.join("; ", said);
    }

    // ------------------------------------------------------------------ reading the slice

    private List<Map<String, String>> rec(String table) {
        return slice.table(table).records();
    }

    /** The first row of the table with this Row identifier, or null. */
    private Map<String, String> one(String table, String id) {
        if (id == null || id.isEmpty()) {
            return null;
        }
        Map<String, Map<String, String>> index = byId.get(table);
        if (index == null) {
            index = new HashMap<>();
            for (Map<String, String> row : rec(table)) {
                index.putIfAbsent(row.get("row_id"), row);
            }
            byId.put(table, index);
        }
        return index.get(id);
    }

    /** The name of the row with this identifier; the identifier itself when the row is not in the slice. */
    private String name(String table, String id) {
        Map<String, String> row = one(table, id);
        return row == null ? id : row.get("name");
    }

    private List<Map<String, String>> where(String table, String column, String value) {
        Map<String, List<Map<String, String>>> groups = grouped.get(table + "/" + column);
        if (groups == null) {
            groups = new HashMap<>();
            for (Map<String, String> row : rec(table)) {
                groups.computeIfAbsent(row.get(column), k -> new ArrayList<>()).add(row);
            }
            grouped.put(table + "/" + column, groups);
        }
        return groups.getOrDefault(value, List.of());
    }

    // ------------------------------------------------------------------ units, blocks and splitting

    private Draft draft(String id, String kind, String title) {
        Draft draft = new Draft(id, kind, title);
        drafts.add(draft);
        return draft;
    }

    /** A unit before it is cut into packets: blocks of lines, each under a section heading. */
    private static final class Draft {
        final String id;
        final String kind;
        final String title;
        final List<Block> blocks = new ArrayList<>();
        /** The chapter new blocks belong to. A unit too long for one packet starts a new part at each chapter. */
        int chapter;

        Draft(String id, String kind, String title) {
            this.id = id;
            this.kind = kind;
            this.title = title;
        }

        Block block(String section) {
            return block(section, null);
        }

        /** @param label what the block is about, for the note on a part that continues it; or null */
        Block block(String section, String label) {
            Block block = new Block(section);
            block.label = label;
            block.chapter = chapter;
            blocks.add(block);
            return block;
        }
    }

    /**
     * Lines that belong together: a packet boundary falls between blocks whenever it can, and inside a block
     * only where an item starts.
     */
    private static final class Block {
        final String section;
        String label;
        int chapter;
        final List<String> lines = new ArrayList<>();
        final Set<String> tables = new TreeSet<>();
        final Map<String, String> required = new LinkedHashMap<>();

        Block(String section) {
            this.section = section;
        }

        String cite(String table, String id) {
            tables.add(table);
            return Citation.of(id);
        }

        void require(String table, String id) {
            required.put(id, table);
        }

        void plain(String text) {
            lines.add(text);
        }

        void fact(String text, String table, String id) {
            lines.add(text + " " + cite(table, id));
        }

        /** A labelled value exactly as extracted: on the line when it fits on one, otherwise between fence lines. */
        void value(String indent, String label, String value, String table, String id) {
            String start = label.startsWith(" ") ? label : indent + "- " + label;
            if (inline(value) && !value.isEmpty()) {
                lines.add(start + ": `" + value + "` " + cite(table, id));
            } else if (value.isEmpty()) {
                lines.add(start + ": (empty) " + cite(table, id));
            } else {
                lines.add(start + ", exactly as extracted, between the two fence lines: " + cite(table, id));
                fence(indent, value);
            }
        }

        void fence(String indent, String value) {
            int longest = 2;
            int run = 0;
            for (int i = 0; i < value.length(); i++) {
                run = value.charAt(i) == '`' ? run + 1 : 0;
                longest = Math.max(longest, run);
            }
            StringBuilder marks = new StringBuilder();
            for (int i = 0; i <= longest; i++) {
                marks.append('`');
            }
            lines.add(marks.toString());
            for (String line : value.split("\n", -1)) {
                lines.add(line);
            }
            lines.add(marks.toString());
        }

        /** What the placeholders of the expression above stand for, one per line. */
        void refs(String indent, String cell) {
            int n = 1;
            for (String id : cell.split("\n", -1)) {
                lines.add(indent + "- %" + n++ + " -> " + (id.isEmpty() ? "(did not resolve)" : id));
            }
        }
    }

    /** One packet being filled. */
    private static final class Part {
        final List<String> lines = new ArrayList<>();
        final Set<String> tables = new TreeSet<>();
        final Map<String, String> required = new LinkedHashMap<>();
        String section;
    }

    private List<PacketUnit> split(Draft draft) {
        int room = maxLines - HEADER_LINES;
        // Does the whole unit fit in one packet? Only when it does not is it cut at a chapter.
        int whole = 0;
        String last = null;
        for (Block block : draft.blocks) {
            whole += block.lines.size() + 1 + (block.section.equals(last) ? 0 : 2);
            last = block.section;
        }
        boolean fits = whole <= room;
        List<Part> parts = new ArrayList<>();
        Part current = new Part();
        parts.add(current);
        int chapter = draft.blocks.isEmpty() ? 0 : draft.blocks.get(0).chapter;
        for (Block block : draft.blocks) {
            if (!fits && block.chapter != chapter && !current.lines.isEmpty()) {
                current = new Part();
                parts.add(current);
            }
            chapter = block.chapter;
            for (Block piece : pieces(block, room - 3)) {
                boolean heading = !piece.section.equals(current.section);
                int needed = piece.lines.size() + 1 + (heading ? 2 : 0);
                if (!current.lines.isEmpty() && current.lines.size() + needed > room) {
                    current = new Part();
                    parts.add(current);
                    heading = true;
                }
                if (heading) {
                    current.lines.add("## " + piece.section);
                    current.lines.add("");
                    current.section = piece.section;
                }
                current.lines.addAll(piece.lines);
                current.lines.add("");
                current.tables.addAll(piece.tables);
                current.required.putAll(piece.required);
            }
        }
        List<PacketUnit> units = new ArrayList<>();
        for (int i = 0; i < parts.size(); i++) {
            Part part = parts.get(i);
            String id = parts.size() == 1 ? draft.id : draft.id + "-p" + (i + 1);
            String title = parts.size() == 1 ? draft.title : draft.title + " (part " + (i + 1) + " of " + parts.size() + ")";
            List<String> lines = new ArrayList<>();
            lines.add("# " + title);
            lines.add("Unit: " + id);
            lines.add("Kind: " + draft.kind);
            lines.add(tab ? "Tab: " + slice.fact("tab") + " (subject areas: " + String.join(", ", lines(slice.fact("subject_areas")))
                    + "; estate " + slice.estate() + ")" : "Subject area: " + areaName + " (estate " + slice.estate() + ")");
            lines.add("Facts from: " + (part.tables.isEmpty() ? "counts and the manifest only" : String.join(", ", part.tables)));
            lines.add("Rows of this packet a document must cover: " + part.required.size()
                    + (part.required.isEmpty() ? "" : ". Each is on a line that ends with " + REQUIRED_MARK + "."));
            lines.add("To cite a fact, copy the marker in square brackets that follows it, exactly as written. \"->\" points at another row.");
            if (parts.size() > 1) {
                lines.add("This is part " + (i + 1) + " of " + parts.size() + " of what was too long for one packet (the parts are "
                        + draft.id + "-p1 to " + draft.id + "-p" + parts.size() + "). Each part is a unit of its own: "
                        + "write this one as its own section, from this packet alone.");
            }
            lines.add("");
            lines.addAll(markRequired(part.lines, part.required.keySet()));
            units.add(new PacketUnit(id, draft.kind, title, i + 1, parts.size(), lines, part.required));
        }
        return units;
    }

    /**
     * The lines with {@link #REQUIRED_MARK} added to the one line that states each required row: the first
     * line that ends with the row's citation (or, failing that, the first that carries it). A skill then
     * needs no list of which kinds of line matter: it cites every marked line.
     */
    static List<String> markRequired(List<String> lines, Set<String> requiredIds) {
        List<String> marked = new ArrayList<>(lines);
        for (String id : requiredIds) {
            String citation = Citation.of(id);
            int at = -1;
            for (int i = 0; i < marked.size() && at < 0; i++) {
                if (marked.get(i).endsWith(citation)) {
                    at = i;
                }
            }
            for (int i = 0; i < marked.size() && at < 0; i++) {
                if (marked.get(i).contains(citation) && !marked.get(i).endsWith(REQUIRED_MARK)) {
                    at = i;
                }
            }
            if (at >= 0) {
                marked.set(at, marked.get(at) + " " + REQUIRED_MARK);
            }
        }
        return marked;
    }

    /**
     * The block itself when it fits in a packet; otherwise the block cut into pieces of at most {@code room}
     * lines. A cut falls where an item starts (a line that begins, after its indentation, with "- " and is
     * not inside a fenced value): of the places that leave the piece at least a third full, the one with the
     * least indentation, and of those the last. So an item is never cut in the middle while an item boundary
     * is in reach. Only a single value longer than a packet is cut inside: it gets a closing fence line, a
     * note, and a new opening in the next piece. A piece that continues a block starts with a line saying
     * what it continues. Each required row goes with the piece that cites it.
     */
    static List<Block> pieces(Block block, int room) {
        if (block.lines.size() <= room) {
            return List.of(block);
        }
        List<String> lines = block.lines;
        int n = lines.size();
        // For each line: the fence it is inside of (or closes), and its indentation when it starts an item.
        String[] fenceAt = new String[n];
        int[] itemIndent = new int[n];
        String open = null;
        for (int i = 0; i < n; i++) {
            String line = lines.get(i);
            boolean marks = line.length() >= 3 && line.chars().allMatch(c -> c == '`');
            fenceAt[i] = open;
            itemIndent[i] = -1;
            if (open == null) {
                int indent = 0;
                while (indent < line.length() && line.charAt(indent) == ' ') {
                    indent++;
                }
                if (line.startsWith("- ", indent)) {
                    itemIndent[i] = indent;
                }
                if (marks) {
                    open = line;
                }
            } else if (marks && line.equals(open)) {
                open = null;
            }
        }
        String note = "- (continued from the previous part" + (block.label == null ? "" : ": " + block.label) + ")";
        List<Block> pieces = new ArrayList<>();
        Block piece = new Block(block.section);
        int at = 0;
        while (n - at > room - piece.lines.size()) {
            int budget = room - piece.lines.size();
            int cut = -1;
            int least = Integer.MAX_VALUE;
            for (int c = at + Math.max(1, budget / 3); c <= at + budget && c < n; c++) {
                if (itemIndent[c] >= 0 && itemIndent[c] <= least) {
                    cut = c;
                    least = itemIndent[c];
                }
            }
            for (int c = at + 1; cut < 0 && c < at + Math.max(1, budget / 3) && c < n; c++) {
                if (itemIndent[c] >= 0) {
                    cut = c;
                }
            }
            String fence = null;
            if (cut < 0) {
                // No item starts within reach: one value is longer than a packet.
                cut = Math.max(at + 1, at + budget - 2);
                if (fenceAt[cut] != null && lines.get(cut).equals(fenceAt[cut])) {
                    cut++;
                }
                fence = cut < n ? fenceAt[cut] : null;
            }
            piece.lines.addAll(lines.subList(at, cut));
            if (fence != null) {
                piece.lines.add(fence);
                piece.lines.add(CONTINUES_NEXT);
            }
            pieces.add(piece);
            piece = new Block(block.section);
            piece.lines.add(note);
            if (fence != null) {
                piece.lines.add(CONTINUES_PREVIOUS);
                piece.lines.add(fence);
            }
            at = cut;
        }
        piece.lines.addAll(lines.subList(at, n));
        pieces.add(piece);
        for (Block made : pieces) {
            made.tables.addAll(block.tables);
            made.label = block.label;
            made.chapter = block.chapter;
        }
        for (Map.Entry<String, String> required : block.required.entrySet()) {
            String citation = Citation.of(required.getKey());
            Block holder = pieces.get(0);
            for (Block candidate : pieces) {
                boolean cites = false;
                for (String line : candidate.lines) {
                    cites |= line.contains(citation);
                }
                if (cites) {
                    holder = candidate;
                    break;
                }
            }
            holder.required.put(required.getKey(), required.getValue());
        }
        return pieces;
    }
}
