package obi2pbi.core;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Cuts the rows one subject area needs out of a Knowledge Base (Architecture AD-9). No I/O, no AI.
 *
 * <p>The walk is the reference slicer's (port guide 03 §6.4) with deliberate differences 13, 23, 24, 25 and
 * 33: hierarchies, the definitions of physical tables, and logical columns reached only through a formula are
 * included; variables are matched by exact name; security rows about any row of the Slice are included; the
 * reports built on the subject area come with it; every reference inside the Slice resolves inside it; and a
 * physical table of the Slice comes whole, with every column and key, as do the keys of its logical tables
 * and levels.
 *
 * <p>Rows are exact copies of Knowledge Base rows, in Knowledge Base order. Nothing is changed or summarised.
 */
public final class Slice {

    /** What a row of {@code unparsed} with a given object type is about: the Slice table that holds such objects. */
    private static final Map<String, String> TABLE_OF_OBJECT_TYPE = objectTypes();

    /** Columns that hold expression text and are not named like one: a variable can be named in them. */
    private static final List<String> EXPRESSION_COLUMNS = List.of("display_name", "text", "init_string", "formula",
            "column_formula");

    /** Between the four parts of a line of the manifest fact {@code unparsed_not_copied_by_type}. */
    public static final String UNPARSED_SEPARATOR = " | ";

    private final KbData kb;
    private final Map<String, BitSet> picked = new LinkedHashMap<>();
    private final Map<String, Map<String, List<Integer>>> byId = new HashMap<>();

    private Slice(KbData kb) {
        this.kb = kb;
        for (KbTable table : SliceSchema.carriedTables()) {
            KbRows rows = kb.table(table.name);
            picked.put(table.name, new BitSet(rows.rows.size()));
            Map<String, List<Integer>> index = new HashMap<>();
            int column = rows.column("row_id");
            if (column >= 0) {
                for (int i = 0; i < rows.rows.size(); i++) {
                    index.computeIfAbsent(rows.rows.get(i)[column], k -> new ArrayList<>(1)).add(i);
                }
            }
            byId.put(table.name, index);
        }
    }

    // ------------------------------------------------------------------ what can be sliced

    /**
     * Every subject area of the Knowledge Base, sorted by Row identifier, as
     * {@code {row_id, estate, name, presentation columns, of which measures}}.
     */
    public static List<String[]> subjectAreas(KbData kb) {
        Map<String, String> areaOfTable = new HashMap<>();
        for (Map<String, String> table : kb.table("presentation_tables").records()) {
            areaOfTable.put(table.get("row_id"), table.get("subject_area_id"));
        }
        Set<String> measures = new LinkedHashSet<>();
        for (Map<String, String> column : kb.table("logical_columns").records()) {
            if ("true".equals(column.get("is_measure"))) {
                measures.add(column.get("row_id"));
            }
        }
        Map<String, int[]> counts = new HashMap<>();
        for (Map<String, String> column : kb.table("presentation_columns").records()) {
            String area = areaOfTable.get(column.get("table_id"));
            if (area != null) {
                int[] count = counts.computeIfAbsent(area, k -> new int[2]);
                count[0]++;
                if (measures.contains(column.get("logical_column_id"))) {
                    count[1]++;
                }
            }
        }
        List<String[]> areas = new ArrayList<>();
        for (Map<String, String> area : kb.table("presentation_subject_areas").records()) {
            int[] count = counts.getOrDefault(area.get("row_id"), new int[2]);
            areas.add(new String[] {area.get("row_id"), area.get("rpd_id"), area.get("name"),
                    Integer.toString(count[0]), Integer.toString(count[1])});
        }
        areas.sort(Text.ROW_ORDER);
        return areas;
    }

    /**
     * The Row identifiers of the subject areas an argument names: it is compared, exactly and with letter
     * case, with both the name and the Row identifier of every subject area.
     */
    public static List<String> match(KbData kb, String argument) {
        List<String> found = new ArrayList<>();
        for (String[] area : subjectAreas(kb)) {
            if (argument.equals(area[0]) || argument.equals(area[2])) {
                found.add(area[0]);
            }
        }
        return found;
    }

    /**
     * The folder name for a subject area: its name in lower case, with every run of characters other than
     * the letters a to z and the digits replaced by one hyphen, and no hyphen at either end.
     */
    public static String folderName(String name) {
        StringBuilder out = new StringBuilder();
        boolean gap = false;
        for (char c : name.toLowerCase(Locale.ROOT).toCharArray()) {
            if ((c >= 'a' && c <= 'z') || (c >= '0' && c <= '9')) {
                if (gap && out.length() > 0) {
                    out.append('-');
                }
                out.append(c);
                gap = false;
            } else {
                gap = true;
            }
        }
        return out.length() == 0 ? "subject-area" : out.toString();
    }

    // ------------------------------------------------------------------ the cut

    /**
     * The Slice for one subject area: the carried tables with the rows the subject area needs, and the
     * manifest facts that depend on the cut. The caller adds the facts about the run and the folder.
     *
     * @param areaId the Row identifier of a row of {@code presentation_subject_areas}
     */
    public static KbData cut(KbData kb, String areaId) {
        Slice slice = new Slice(kb);
        if (!slice.pickId("presentation_subject_areas", areaId)) {
            throw new IllegalArgumentException("no subject area with Row identifier '" + areaId + "'");
        }
        slice.walkRepository();
        slice.walkCatalog();
        return slice.finish("subject_area", areaId);
    }

    /**
     * The Slice for one tab (story 4.1): the same walk as {@link #cut}, started from the columns the Operator
     * confirmed instead of from a subject area. A presentation column brings its presentation table and subject
     * area (so that references resolve) but not the table's other columns; a logical column starts the walk at
     * the logical layer; a report column brings its report, whole, and the presentation column it is linked to.
     * From there every step is the subject-area walk's: logical tables whole, their sources, the physical tables
     * whole, joins, hierarchies of the presentation tables touched, variables, security, and every reference
     * closed. One difference: a subject area's implicit fact column is not a starting point, since the tab did
     * not ask for it. The result is a subset of the union of the Slices of the subject areas it touches.
     *
     * @param columnIds Row identifiers of presentation, logical or report columns, all in the Knowledge Base
     */
    public static KbData cutForTab(KbData kb, String tab, java.util.Collection<String> columnIds) {
        Slice slice = new Slice(kb);
        for (String id : columnIds) {
            if (!slice.pickId("presentation_columns", id) && !slice.pickId("logical_columns", id)
                    && !slice.pickId("analysis_columns", id)) {
                throw new IllegalArgumentException("'" + id + "' is not a presentation, logical or report column");
            }
        }
        Set<String> reportColumns = slice.ids("analysis_columns");
        KbRows reportLineage = kb.table("report_lineage");
        for (String[] row : reportLineage.rows) {
            if (reportColumns.contains(reportLineage.get(row, "analysis_column_id"))) {
                slice.pickId("presentation_columns", reportLineage.get(row, "presentation_column_id"));
            }
        }
        Set<String> reports = slice.values("analysis_columns", "analysis_id");
        Set<String> blocks = slice.values("analysis_columns", "criteria_id");
        slice.pickIds("presentation_tables", slice.values("presentation_columns", "table_id"));
        slice.pickIds("presentation_subject_areas", slice.values("presentation_tables", "subject_area_id"));
        slice.walkFromPresentation(false);
        if (!reports.isEmpty()) {
            slice.walkReports(reports, blocks);
        }
        return slice.finish("tab", tab);
    }

    /** The steps after the starting rows, the same for every cut. */
    private KbData finish(String cutBy, String start) {
        Slice slice = this;
        slice.walkPrivileges();
        // A row added only so that a reference resolves can itself name a variable, so the two steps
        // repeat until neither adds anything.
        int boundary = 0;
        int added;
        boolean grew;
        do {
            slice.walkVariables();
            added = slice.closeReferences();
            boundary += added;
            // Difference 33: a physical table that came in only because a row points at it is made whole too.
            grew = false;
            while (slice.wholePhysicalTables()) {
                grew = true;
            }
        } while (added > 0 || grew);
        return slice.result(cutBy, start, boundary);
    }

    private void walkRepository() {
        Set<String> areaIds = ids("presentation_subject_areas");
        // Steps 1 to 3 of the reference walk: what report authors see.
        pickWhere("presentation_tables", "subject_area_id", areaIds);
        pickWhere("presentation_columns", "table_id", ids("presentation_tables"));
        walkFromPresentation(true);
    }

    /**
     * Everything the walk does once the presentation rows are picked.
     *
     * @param implicitFact whether a subject area's implicit fact column is a starting point (yes for a subject
     *                     area, no for a tab)
     */
    private void walkFromPresentation(boolean implicitFact) {
        Set<String> presentationTables = ids("presentation_tables");
        // Difference 13: the drill paths offered to report authors.
        pickWhere("presentation_hierarchies", "table_id", presentationTables);
        pickWhere("presentation_levels", "hierarchy_id", ids("presentation_hierarchies"));
        pickWhere("presentation_level_columns", "level_id", ids("presentation_levels"));

        // Step 4: the logical columns named by a presentation column, and the implicit fact column, which
        // can sit outside the presentation columns.
        pickIds("logical_columns", values("presentation_columns", "logical_column_id"));
        if (implicitFact) {
            pickIds("logical_columns", values("presentation_subject_areas", "implicit_fact_column_id"));
        }

        Map<String, String> tableOfLogicalColumn = new HashMap<>();
        for (Map<String, String> column : kb.table("logical_columns").records()) {
            tableOfLogicalColumn.put(column.get("row_id"), column.get("table_id"));
        }
        Map<String, String> dimensionOfLevel = new HashMap<>();
        for (Map<String, String> level : kb.table("logical_levels").records()) {
            dimensionOfLevel.put(level.get("row_id"), level.get("dimension_id"));
        }

        // Steps 5 to 8 and 16, repeated until nothing is added. Difference 13 is what makes it a loop: a
        // logical column reached only through a formula brings its own table, sources and formulas.
        boolean changed = true;
        while (changed) {
            changed = false;
            Set<String> references = new LinkedHashSet<>();
            references.addAll(listed("logical_columns", "derived_expr_refs", "mapping_expr_refs"));
            references.addAll(listed("aggregation_rules", "expr_refs"));
            references.addAll(listed("logical_table_sources", "fragment_refs", "where_refs", "group_by_refs"));
            references.addAll(listed("lts_column_map", "logical_expr_refs"));
            references.addAll(listed("security_rls", "expr_refs"));
            for (String reference : references) {
                changed |= pickId("logical_columns", reference);
                String dimension = dimensionOfLevel.get(reference);
                if (dimension != null) {
                    changed |= pickId("logical_dimensions", dimension);
                }
            }
            // Difference 13: hierarchies. A dimension belongs to the Slice when a presentation hierarchy
            // shows it, when a formula or a source names one of its levels (above), or when one of its
            // levels is keyed on a logical table of the Slice.
            changed |= pickIds("logical_dimensions", values("presentation_hierarchies", "dimension_id"));
            Set<String> logicalTables = values("logical_columns", "table_id");
            for (Map<String, String> level : kb.table("logical_levels").records()) {
                String keyTable = tableOfLogicalColumn.get(level.get("drill_key_column_id"));
                if (keyTable != null && logicalTables.contains(keyTable)) {
                    changed |= pickId("logical_dimensions", level.get("dimension_id"));
                }
            }
            changed |= pickWhere("logical_levels", "dimension_id", ids("logical_dimensions"));
            changed |= pickIds("logical_columns", values("logical_levels", "drill_key_column_id"));
            // Difference 33: every key of a level of the Slice, and the columns it is made of.
            changed |= pickWhere("logical_level_keys", "level_id", ids("logical_levels"));
            changed |= pickIds("logical_columns", listed("logical_level_keys", "columns"));

            logicalTables = values("logical_columns", "table_id");
            // Difference 25: a logical table of the Slice comes whole, with the columns this subject area
            // does not show. Their formulas are followed like any other on the next round.
            changed |= pickWhere("logical_columns", "table_id", logicalTables);
            Set<String> logicalColumns = ids("logical_columns");
            changed |= pickIds("logical_tables", logicalTables);
            // Difference 33: the keys of a logical table of the Slice. Their columns are columns of the table.
            changed |= pickWhere("logical_keys", "logical_table_id", logicalTables);
            changed |= pickIds("logical_columns", listed("logical_keys", "columns"));
            changed |= pickWhere("aggregation_rules", "logical_column_id", logicalColumns);
            changed |= pickWhere("logical_table_sources", "logical_table_id", logicalTables);
            Set<String> sources = ids("logical_table_sources");
            // Difference 34: the physical tables each source is built on and the joins it makes between them.
            changed |= pickWhere("lts_tables", "source_id", sources);
            changed |= pickWhere("lts_joins", "source_id", sources);
            KbRows map = kb.table("lts_column_map");
            for (int i = 0; i < map.rows.size(); i++) {
                String[] row = map.rows.get(i);
                if (sources.contains(map.get(row, "source_id"))
                        && logicalColumns.contains(map.get(row, "logical_column_id"))) {
                    changed |= pick("lts_column_map", i);
                }
            }
            // Step 16: security attached to a logical table or a table source of the Slice.
            Set<String> subjects = new LinkedHashSet<>(logicalTables);
            subjects.addAll(sources);
            changed |= pickWhere("security_rls", "subject_id", subjects);
        }

        // Steps 9 to 11: where the data comes from.
        pickWhere("lineage", "presentation_column_id", ids("presentation_columns"));
        pickIds("physical_columns", values("lineage", "physical_column_id"));
        pickIds("physical_columns", values("lts_column_map", "physical_column_id"));
        // The other physical columns a mapping, a source's filter or a row filter names.
        pickIds("physical_columns", listed("lts_column_map", "physical_expr_refs"));
        pickIds("physical_columns", listed("logical_table_sources", "fragment_refs", "where_refs", "group_by_refs"));
        pickIds("physical_columns", listed("security_rls", "expr_refs"));
        pickIds("physical_tables", values("lineage", "physical_table_id"));
        // Difference 34: a table a source joins belongs to the Slice even when no column is mapped from it,
        // and so does the physical join that gives a source's join its condition. Difference 36: the
        // relationship table of a parent-child source.
        pickIds("physical_tables", values("lts_tables", "physical_table_id"));
        pickIds("physical_tables", values("lts_joins", "table1_id"));
        pickIds("physical_tables", values("lts_joins", "table2_id"));
        pickIds("physical_tables", values("logical_table_sources", "closure_table_id"));
        pickIds("physical_joins", values("lts_joins", "physical_join_id"));
        while (wholePhysicalTables()) {
            continue;
        }
        // Step 13: business-model joins with either end in the Slice.
        Set<String> logicalTables = values("logical_columns", "table_id");
        KbRows bmmJoins = kb.table("bmm_joins");
        for (int i = 0; i < bmmJoins.rows.size(); i++) {
            String[] row = bmmJoins.rows.get(i);
            if (logicalTables.contains(bmmJoins.get(row, "table1_id"))
                    || logicalTables.contains(bmmJoins.get(row, "table2_id"))) {
                pick("bmm_joins", i);
            }
        }
    }

    /**
     * One round of making the physical side of the Slice whole. Returns true when it added a row; call it
     * until it returns false.
     *
     * <p>The table of every physical column of the Slice; the table an alias stands for (difference 24; an
     * alias of an alias is followed to the end); for every table, what it is in the database (difference 13:
     * for a table of type 'select', its SQL), <b>every</b> column of it and every key of it (difference 33:
     * a table-builder needs the whole table, not only the columns a logical column maps); the physical joins
     * with both ends in the Slice (step 12 of the reference walk); and the columns such a join is made on.
     */
    private boolean wholePhysicalTables() {
        boolean changed = pickIds("physical_tables", values("physical_columns", "table_id"));
        while (pickIds("physical_tables", values("physical_tables", "source_table_id"))) {
            changed = true;
        }
        Set<String> physicalTables = ids("physical_tables");
        changed |= pickWhere("physical_table_definitions", "table_id", physicalTables);
        changed |= pickWhere("physical_columns", "table_id", physicalTables);
        changed |= pickWhere("physical_keys", "table_id", physicalTables);
        KbRows joins = kb.table("physical_joins");
        for (int i = 0; i < joins.rows.size(); i++) {
            String[] row = joins.rows.get(i);
            String second = joins.get(row, "table2_id");
            if (physicalTables.contains(joins.get(row, "table1_id"))
                    && (second.isEmpty() || physicalTables.contains(second))) {
                changed |= pick("physical_joins", i);
            }
        }
        changed |= pickIds("physical_columns", listed("physical_joins", "columns", "referenced_columns", "expr_refs"));
        changed |= pickIds("physical_columns", listed("physical_keys", "columns"));
        return changed;
    }

    /** The reports built on the subject area, what is in them, and the dashboard pages that place them. */
    private void walkCatalog() {
        Set<String> wanted = ids("presentation_subject_areas");
        Map<String, String> exact = new HashMap<>();
        Map<String, String> folded = new HashMap<>();
        for (Map<String, String> area : kb.table("presentation_subject_areas").records()) {
            exact.put(area.get("name"), area.get("row_id"));
            folded.put(area.get("name").toLowerCase(Locale.ROOT), area.get("row_id"));
        }
        // A report's subject area is the name as the catalog wrote it, usually in double quotes. It is
        // matched as report_lineage matches it: quotes off, then exactly, then ignoring letter case.
        KbRows criteria = kb.table("analysis_criteria");
        Set<String> matchedCriteria = new LinkedHashSet<>();
        Set<String> reports = new LinkedHashSet<>();
        for (String[] row : criteria.rows) {
            String stated = ReportLineage.stripQuotes(criteria.get(row, "subject_area"));
            String areaId = exact.get(stated);
            if (areaId == null) {
                areaId = folded.get(stated.toLowerCase(Locale.ROOT));
            }
            if (areaId != null && wanted.contains(areaId)) {
                matchedCriteria.add(criteria.get(row, "row_id"));
                reports.add(criteria.get(row, "analysis_id"));
            }
        }
        if (reports.isEmpty()) {
            return;
        }
        walkReports(reports, matchedCriteria);
    }

    /** The reports, whole, the link to the repository of the given blocks, and the pages and filters around them. */
    private void walkReports(Set<String> reports, Set<String> matchedCriteria) {
        // The whole report: a report over two subject areas is described once, with all its blocks.
        pickIds("analyses", reports);
        pickWhere("analysis_criteria", "analysis_id", reports);
        pickWhere("analysis_columns", "analysis_id", reports);
        pickWhere("analysis_views", "analysis_id", reports);
        pickWhere("analysis_prompts", "analysis_id", reports);
        // The link to the repository only for blocks on this subject area; another block's link would
        // point at presentation columns outside the Slice.
        pickWhere("report_lineage", "criteria_id", matchedCriteria);

        KbRows filters = kb.table("analysis_filters");
        for (int i = 0; i < filters.rows.size(); i++) {
            String[] row = filters.rows.get(i);
            if ("analysis".equals(filters.get(row, "scope")) && reports.contains(filters.get(row, "owner_id"))) {
                pick("analysis_filters", i);
            }
        }

        // The pages that place a report of the Slice, their dashboards, and the dashboard prompts on them.
        KbRows contents = kb.table("dashboard_contents");
        Set<String> allPages = byId.get("dashboard_pages").keySet();
        for (int i = 0; i < contents.rows.size(); i++) {
            String[] row = contents.rows.get(i);
            String container = contents.get(row, "container_id");
            if (reports.contains(contents.get(row, "target_id")) && allPages.contains(container)) {
                pick("dashboard_contents", i);
                pickId("dashboard_pages", container);
            }
        }
        Set<String> pages = ids("dashboard_pages");
        pickIds("dashboards", values("dashboard_pages", "dashboard_id"));
        Set<String> dashboards = ids("dashboards");
        for (int i = 0; i < contents.rows.size(); i++) {
            String[] row = contents.rows.get(i);
            String container = contents.get(row, "container_id");
            boolean pageOfDashboard = dashboards.contains(container) && pages.contains(contents.get(row, "target_id"));
            boolean promptOnPage = pages.contains(container) && contents.get(row, "element").endsWith("globalFilterView");
            if (pageOfDashboard || promptOnPage) {
                pick("dashboard_contents", i);
            }
        }
        for (int i = 0; i < filters.rows.size(); i++) {
            String[] row = filters.rows.get(i);
            if ("dashboard".equals(filters.get(row, "scope")) && pages.contains(filters.get(row, "owner_id"))) {
                pick("analysis_filters", i);
            }
        }
        // Saved filters a filter of the Slice refers to, and the ones those refer to.
        boolean changed = true;
        while (changed) {
            changed = pickIds("saved_filters", values("analysis_filters", "target_id"));
            Set<String> saved = ids("saved_filters");
            for (int i = 0; i < filters.rows.size(); i++) {
                String[] row = filters.rows.get(i);
                if ("saved_filter".equals(filters.get(row, "scope")) && saved.contains(filters.get(row, "owner_id"))) {
                    changed |= pick("analysis_filters", i);
                }
            }
        }
    }

    /**
     * Difference 23: permissions on any row of the Slice (the reference matched only logical tables and
     * table sources), and the rows that say which role holds the package a permission goes through.
     */
    private void walkPrivileges() {
        Set<String> everything = new LinkedHashSet<>();
        for (String table : picked.keySet()) {
            if (!table.equals("security_rls")) {
                everything.addAll(ids(table));
            }
        }
        pickWhere("security_rls", "subject_id", everything);
        Set<String> packages = new LinkedHashSet<>();
        for (Map<String, String> row : pickedRecords("security_rls")) {
            String detail = row.get("detail");
            int via = detail.indexOf(" via ");
            if ("object_privilege".equals(row.get("kind")) && via >= 0 && via + 5 < detail.length()) {
                packages.add(detail.substring(via + 5));
            }
        }
        KbRows security = kb.table("security_rls");
        for (int i = 0; i < security.rows.size(); i++) {
            String[] row = security.rows.get(i);
            String id = security.get(row, "row_id");
            int dot = id.lastIndexOf(".q");
            if ("query_privilege".equals(security.get(row, "kind")) && dot > 0
                    && packages.contains(id.substring(0, dot))) {
                pick("security_rls", i);
            }
        }
    }

    /**
     * Step 14 and 15 with difference 13: a variable belongs to the Slice when an expression of the Slice
     * refers to it by Row identifier, or names it exactly inside {@code VALUEOF(...)}; when no variable has
     * exactly that name, one whose name differs only in letter case. Only this Knowledge Base's variables are
     * looked at, so only this estate's. Then the init blocks that set them. A name no variable has brings
     * nothing; the variables packet lists it as used and not defined.
     */
    private void walkVariables() {
        Map<String, List<String>> idsByName = new HashMap<>();
        for (Map<String, String> variable : kb.table("variables").records()) {
            if (!variable.get("name").isEmpty()) {
                idsByName.computeIfAbsent(variable.get("name"), k -> new ArrayList<>(1)).add(variable.get("row_id"));
            }
        }
        boolean changed = true;
        while (changed) {
            changed = false;
            for (String table : picked.keySet()) {
                KbRows rows = kb.table(table);
                for (int c = 0; c < rows.header.size(); c++) {
                    String column = rows.header.get(c);
                    boolean references = isReferenceList(table, column);
                    if (!references && !isExpression(column)) {
                        continue;
                    }
                    BitSet set = picked.get(table);
                    for (int i = set.nextSetBit(0); i >= 0; i = set.nextSetBit(i + 1)) {
                        String cell = rows.rows.get(i)[c];
                        if (cell.isEmpty()) {
                            continue;
                        }
                        if (references) {
                            for (String id : cell.split("\n", -1)) {
                                changed |= pickId("variables", id);
                            }
                        } else {
                            for (String name : Variables.namesIn(cell)) {
                                for (String id : Variables.idsFor(name, idsByName)) {
                                    changed |= pickId("variables", id);
                                }
                            }
                        }
                    }
                }
            }
            changed |= pickIds("init_blocks", values("variables", "init_block_id"));
        }
    }

    /**
     * Difference 24: adds the rows that rows of the Slice point at and the walk did not bring, so that every
     * reference resolves inside the Slice. Only the row pointed at is added, not what hangs below it.
     *
     * @return how many rows were added
     */
    private int closeReferences() {
        int added = 0;
        boolean changed = true;
        while (changed) {
            changed = false;
            for (KbTable definition : SliceSchema.carriedTables()) {
                KbRows rows = kb.table(definition.name);
                for (int c = 0; c < rows.header.size(); c++) {
                    String column = rows.header.get(c);
                    KbColumn declared = definition.column(column);
                    String target = declared != null && declared.referencesColumn.equals("row_id")
                            ? declared.referencesTable : "";
                    boolean list = isReferenceList(definition.name, column);
                    if (target.isEmpty() && !list) {
                        continue;
                    }
                    BitSet set = picked.get(definition.name);
                    List<String> cells = new ArrayList<>();
                    for (int i = set.nextSetBit(0); i >= 0; i = set.nextSetBit(i + 1)) {
                        if (!rows.rows.get(i)[c].isEmpty()) {
                            cells.add(rows.rows.get(i)[c]);
                        }
                    }
                    for (String cell : cells) {
                        if (!list) {
                            if (pickId(target, cell)) {
                                added++;
                                changed = true;
                            }
                            continue;
                        }
                        for (String id : cell.split("\n", -1)) {
                            for (String table : picked.keySet()) {
                                if (!id.isEmpty() && pickId(table, id)) {
                                    added += byId.get(table).get(id).size();
                                    changed = true;
                                }
                            }
                        }
                    }
                }
            }
        }
        return added;
    }

    private KbData result(String cutBy, String start, int boundary) {
        String areaId = cutBy.equals("subject_area") ? start : "";
        KbData slice = new KbData();
        Set<String> everything = new LinkedHashSet<>();
        Set<String> catalogPaths = new LinkedHashSet<>();
        for (String table : picked.keySet()) {
            everything.addAll(ids(table));
            catalogPaths.addAll(values(table, "catalog_path"));
        }

        // errors: a row is about an object of the Slice when its source_id is that object's Row
        // identifier or, for a catalog object, its path.
        KbRows errors = kb.table("errors");
        Map<String, Integer> errorsLeft = new TreeMap<>();
        for (int i = 0; i < errors.rows.size(); i++) {
            String source = errors.get(errors.rows.get(i), "source_id");
            if (everything.contains(source) || catalogPaths.contains(source)) {
                pick("errors", i);
            } else {
                errorsLeft.merge(errors.get(errors.rows.get(i), "kind"), 1, Integer::sum);
            }
        }
        // unparsed: its rows are counts per object type, not per object. A row is kept when the Slice
        // holds objects of that type; a withheld value is kept when its variable is in the Slice.
        Set<String> variableNames = values("variables", "name");
        KbRows unparsed = kb.table("unparsed");
        Map<String, Integer> unparsedLeft = new TreeMap<>();
        // What was left out, by kind and object type: {rows, occurrences}.
        Map<String, int[]> leftByType = new TreeMap<>(Text.CODE_POINT_ORDER);
        for (int i = 0; i < unparsed.rows.size(); i++) {
            String[] row = unparsed.rows.get(i);
            String kind = unparsed.get(row, "kind");
            boolean keep;
            if (kind.equals("redacted_value")) {
                String detail = unparsed.get(row, "detail");
                int dash = detail.indexOf(" — ");
                keep = dash > 0 && variableNames.contains(detail.substring(0, dash));
            } else if (kind.equals("unmodelled_type") || kind.equals("undiscovered_file")
                    || kind.equals("not_a_catalog_object") || kind.equals("undecoded_name")) {
                keep = false;
            } else {
                String type = unparsed.get(row, "object_type");
                String table = TABLE_OF_OBJECT_TYPE.get(type.substring(type.lastIndexOf('}') + 1));
                keep = table != null && !picked.get(table).isEmpty();
            }
            if (keep) {
                pick("unparsed", i);
            } else {
                unparsedLeft.merge(kind, 1, Integer::sum);
                int[] left = leftByType.computeIfAbsent(kind + UNPARSED_SEPARATOR + unparsed.get(row, "object_type"),
                        k -> new int[2]);
                left[0]++;
                try {
                    left[1] += Integer.parseInt(unparsed.get(row, "occurrences"));
                } catch (NumberFormatException e) {
                    left[1] += 1;
                }
            }
        }

        for (KbTable definition : SliceSchema.carriedTables()) {
            if (!kb.has(definition.name)) {
                continue;
            }
            KbRows rows = kb.table(definition.name);
            List<String[]> kept = new ArrayList<>();
            BitSet set = picked.get(definition.name);
            for (int i = set.nextSetBit(0); i >= 0; i = set.nextSetBit(i + 1)) {
                kept.add(rows.rows.get(i));
            }
            slice.put(new KbRows(definition.name, rows.header, kept));
        }

        String areaName = "";
        for (Map<String, String> area : slice.table("presentation_subject_areas").records()) {
            if (area.get("row_id").equals(areaId)) {
                areaName = area.get("name");
            }
        }
        slice.manifest.put("kb_extracted_at", kb.fact("extracted_at"));
        slice.manifest.put("kb_extractor_version", kb.fact("extractor_version"));
        slice.manifest.put("rpd_ids", kb.fact("rpd_ids"));
        for (Map.Entry<String, String> fact : kb.manifest.entrySet()) {
            if (fact.getKey().startsWith("halves_present:")) {
                slice.manifest.put(fact.getKey(), fact.getValue());
            }
        }
        slice.manifest.put("cut_by", cutBy);
        if (cutBy.equals("tab")) {
            slice.manifest.put("tab", start);
            List<String> names = new ArrayList<>();
            List<String> areaIds = new ArrayList<>();
            for (Map<String, String> area : slice.table("presentation_subject_areas").records()) {
                names.add(area.get("name"));
                areaIds.add(area.get("row_id"));
            }
            slice.manifest.put("subject_areas", String.join("\n", names));
            slice.manifest.put("subject_area_ids", String.join("\n", areaIds));
        } else {
            slice.manifest.put("subject_area", areaName);
            slice.manifest.put("subject_area_id", areaId);
        }
        slice.manifest.put("boundary_rows", Integer.toString(boundary));
        slice.manifest.put("errors_not_copied", byKind(errorsLeft));
        slice.manifest.put("unparsed_not_copied", byKind(unparsedLeft));
        List<String> leftLines = new ArrayList<>();
        for (Map.Entry<String, int[]> left : leftByType.entrySet()) {
            leftLines.add(left.getKey() + UNPARSED_SEPARATOR + left.getValue()[0] + UNPARSED_SEPARATOR + left.getValue()[1]);
        }
        slice.manifest.put("unparsed_not_copied_by_type", String.join("\n", leftLines));
        // What belongs to no subject area and is therefore easy to miss: counts only, for the overview.
        int noTarget = 0;
        int roles = 0;
        KbRows security = kb.table("security_rls");
        for (int i = 0; i < security.rows.size(); i++) {
            String kind = security.get(security.rows.get(i), "kind");
            if (kind.equals("object_privilege") && security.get(security.rows.get(i), "subject_id").isEmpty()) {
                noTarget++;
            } else if (kind.equals("query_privilege") && !picked.get("security_rls").get(i)) {
                roles++;
            }
        }
        slice.manifest.put("not_in_slice:permissions_without_target", Integer.toString(noTarget));
        slice.manifest.put("not_in_slice:role_privileges", Integer.toString(roles));
        slice.manifest.put("not_in_slice:init_blocks", Integer.toString(
                kb.table("init_blocks").rows.size() - picked.get("init_blocks").cardinality()));
        slice.manifest.put("not_in_slice:variables", Integer.toString(
                kb.table("variables").rows.size() - picked.get("variables").cardinality()));
        slice.manifest.put("rows", Integer.toString(slice.rowCount()));
        return slice;
    }

    private static String byKind(Map<String, Integer> counts) {
        int total = 0;
        List<String> parts = new ArrayList<>();
        for (Map.Entry<String, Integer> count : counts.entrySet()) {
            total += count.getValue();
            parts.add(count.getValue() + " " + count.getKey());
        }
        return total == 0 ? "0" : total + " (" + String.join(", ", parts) + ")";
    }

    // ------------------------------------------------------------------ picking rows

    private boolean pick(String table, int index) {
        BitSet set = picked.get(table);
        if (set.get(index)) {
            return false;
        }
        set.set(index);
        return true;
    }

    /** Picks every row of the table with this Row identifier. True when at least one was not yet picked. */
    private boolean pickId(String table, String id) {
        if (id == null || id.isEmpty()) {
            return false;
        }
        Map<String, List<Integer>> index = byId.get(table);
        List<Integer> rows = index == null ? null : index.get(id);
        if (rows == null) {
            return false;
        }
        boolean added = false;
        for (int row : rows) {
            added |= pick(table, row);
        }
        return added;
    }

    private boolean pickIds(String table, Set<String> ids) {
        boolean added = false;
        for (String id : ids) {
            added |= pickId(table, id);
        }
        return added;
    }

    private boolean pickWhere(String table, String column, Set<String> wanted) {
        KbRows rows = kb.table(table);
        int c = rows.column(column);
        if (c < 0 || wanted.isEmpty()) {
            return false;
        }
        boolean added = false;
        for (int i = 0; i < rows.rows.size(); i++) {
            if (wanted.contains(rows.rows.get(i)[c])) {
                added |= pick(table, i);
            }
        }
        return added;
    }

    private Set<String> ids(String table) {
        return values(table, "row_id");
    }

    /** The non-empty values of one column over the picked rows of a table. */
    private Set<String> values(String table, String column) {
        Set<String> found = new LinkedHashSet<>();
        KbRows rows = kb.table(table);
        int c = rows.column(column);
        BitSet set = picked.get(table);
        if (c < 0 || set == null) {
            return found;
        }
        for (int i = set.nextSetBit(0); i >= 0; i = set.nextSetBit(i + 1)) {
            if (!rows.rows.get(i)[c].isEmpty()) {
                found.add(rows.rows.get(i)[c]);
            }
        }
        return found;
    }

    /** The entries of reference-list columns (one Row identifier per line) over the picked rows of a table. */
    private Set<String> listed(String table, String... columns) {
        Set<String> found = new LinkedHashSet<>();
        for (String column : columns) {
            for (String cell : values(table, column)) {
                for (String id : cell.split("\n", -1)) {
                    if (!id.isEmpty()) {
                        found.add(id);
                    }
                }
            }
        }
        return found;
    }

    private List<Map<String, String>> pickedRecords(String table) {
        List<Map<String, String>> records = kb.table(table).records();
        List<Map<String, String>> found = new ArrayList<>();
        BitSet set = picked.get(table);
        for (int i = set.nextSetBit(0); i >= 0; i = set.nextSetBit(i + 1)) {
            found.add(records.get(i));
        }
        return found;
    }

    /**
     * True for a column that holds Row identifiers one per line: every {@code *_refs} column, the two
     * column lists of a foreign key, the column list of a key, and the two level lists of a level.
     */
    public static boolean isReferenceList(String table, String column) {
        if (column.equals("columns")) {
            return table.equals("physical_joins") || table.equals("physical_keys") || table.equals("logical_keys")
                    || table.equals("logical_level_keys");
        }
        if (table.equals("logical_levels") && (column.equals("child_level_ids") || column.equals("parent_level_ids"))) {
            return true;
        }
        return column.endsWith("_refs") || (table.equals("physical_joins") && column.equals("referenced_columns"));
    }

    /** True for a column that holds expression text in which a variable can be named. */
    static boolean isExpression(String column) {
        if (column.endsWith("_refs")) {
            return false;
        }
        return column.contains("expr") || column.startsWith("where") || column.startsWith("fragment")
                || column.startsWith("group_by") || EXPRESSION_COLUMNS.contains(column);
    }

    private static Map<String, String> objectTypes() {
        Map<String, String> types = new HashMap<>();
        types.put("PhysicalTable", "physical_tables");
        types.put("PhysicalColumn", "physical_columns");
        types.put("Schema", "physical_tables");
        types.put("Database", "physical_tables");
        types.put("ConnectionPool", "physical_tables");
        types.put("PhysicalForeignKey", "physical_joins");
        types.put("PhysicalKey", "physical_keys");
        types.put("LogicalKey", "logical_keys");
        types.put("ComplexJoin", "physical_joins");
        types.put("BusinessModel", "logical_tables");
        types.put("LogicalTable", "logical_tables");
        types.put("LogicalColumn", "logical_columns");
        types.put("AggrRule", "aggregation_rules");
        types.put("LogicalTableSource", "logical_table_sources");
        types.put("ColumnMapping", "lts_column_map");
        types.put("Link", "lts_tables");
        types.put("Join", "lts_joins");
        types.put("LogicalComplexJoin", "bmm_joins");
        types.put("Dimension", "logical_dimensions");
        types.put("LogicalLevel", "logical_levels");
        types.put("PresentationCatalog", "presentation_subject_areas");
        types.put("PresentationTable", "presentation_tables");
        types.put("PresentationColumn", "presentation_columns");
        types.put("PresentationHierarchy", "presentation_hierarchies");
        types.put("PresentationLevel", "presentation_levels");
        types.put("Variable", "variables");
        types.put("InitBlock", "init_blocks");
        types.put("ObjectPrivilege", "security_rls");
        types.put("PrivilegePackage", "security_rls");
        types.put("Group", "security_rls");
        types.put("dashboard", "dashboards");
        types.put("dashboardPage", "dashboard_pages");
        types.put("report", "analyses");
        types.put("savedFilterObject", "saved_filters");
        return types;
    }
}
