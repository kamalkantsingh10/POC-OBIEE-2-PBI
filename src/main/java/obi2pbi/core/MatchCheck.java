package obi2pbi.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The mechanical checks of a tab's matches (story 3.3; Architecture AD-10, AD-11): every identifier exists,
 * every column leads to a database table, every item has a row, every anchor is in {@code fsd.md}, every value
 * is one the contract allows, and the matches agree with the report columns of the page the tab replaces. No
 * AI, and no judgement of meaning: a column that exists and leads somewhere can still be the wrong one.
 *
 * <p>The files' columns and values are declared in {@link MatchSchema} and described in
 * {@code docs/skill-conventions.md}, section 10.
 */
public final class MatchCheck {

    public static final String ITEMS_FILE = "fsd_items.csv";
    public static final String MATCHES_FILE = "matches.csv";
    public static final String DECISIONS_FILE = "decisions.csv";

    /** What the check found, and what it could not do. */
    public static final class Result {
        /** Rows of check.csv, in the order of {@link MatchSchema#check()}. */
        public final List<String[]> findings = new ArrayList<>();
        /** Sentences the command prints: what was skipped and why. */
        public final List<String> notes = new ArrayList<>();
        public int items;
        public int replaces;
        public int proposed;
        public int unmatched;
        public int decisions;
        public int reportColumnsCompared;

        public int failures() {
            int count = 0;
            for (String[] finding : findings) {
                count += finding[1].equals("failure") ? 1 : 0;
            }
            return count;
        }

        public int warnings() {
            return findings.size() - failures();
        }

        public boolean passed() {
            return failures() == 0;
        }
    }

    private final KbData kb;
    private final Result result = new Result();
    private final Set<String> presentationColumns = new HashSet<>();
    private final Map<String, String> logicalOfPresentation = new HashMap<>();
    private final Map<String, String[]> logicalColumns = new HashMap<>();
    private final Map<String, String[]> analysisColumns = new HashMap<>();
    private final Map<String, List<String[]>> lineageOf;
    private final Map<String, List<String[]>> mappingsOf;
    private final Map<String, String[]> reportLineageOf = new HashMap<>();

    private MatchCheck(KbData kb) {
        this.kb = kb;
        KbRows pres = kb.table("presentation_columns");
        for (String[] row : pres.rows) {
            presentationColumns.add(pres.get(row, "row_id"));
            logicalOfPresentation.put(pres.get(row, "row_id"), pres.get(row, "logical_column_id"));
        }
        KbRows logical = kb.table("logical_columns");
        for (String[] row : logical.rows) {
            logicalColumns.put(logical.get(row, "row_id"), row);
        }
        KbRows reportColumns = kb.table("analysis_columns");
        for (String[] row : reportColumns.rows) {
            analysisColumns.put(reportColumns.get(row, "row_id"), row);
        }
        lineageOf = groupBy(kb.table("lineage"), "presentation_column_id");
        mappingsOf = groupBy(kb.table("lts_column_map"), "logical_column_id");
        KbRows reportLineage = kb.table("report_lineage");
        for (String[] row : reportLineage.rows) {
            reportLineageOf.putIfAbsent(reportLineage.get(row, "analysis_column_id"), row);
        }
    }

    /** The Knowledge Base tables the checks read. */
    public static final List<String> TABLES = List.of("presentation_columns", "logical_columns", "lineage", "lts_column_map",
            "analysis_columns", "report_lineage", "analyses", "dashboards", "dashboard_pages", "dashboard_contents",
            // Read only to say what a wrong identifier is, when it names a row of one of these.
            "presentation_tables", "presentation_subject_areas", "logical_tables", "physical_tables", "physical_columns");

    /**
     * Runs every check.
     *
     * @param fsdMarkdown the text of {@code fsd.md}
     * @param decisions   {@code decisions.csv}, or null when there is none
     */
    public static Result check(KbData kb, String fsdMarkdown, KbRows items, KbRows matches, KbRows decisions) {
        MatchCheck check = new MatchCheck(kb);
        check.run(new LinkedHashSet<>(FsdDocument.anchorsIn(fsdMarkdown)), items, matches, decisions);
        // File by file in the order the files are written, row by row; on one row, in the order found.
        List<String> files = List.of(ITEMS_FILE, MATCHES_FILE, DECISIONS_FILE);
        check.result.findings.sort((a, b) -> {
            int byFile = Integer.compare(files.indexOf(a[2]), files.indexOf(b[2]));
            return byFile != 0 ? byFile : Integer.compare(Integer.parseInt(a[3]), Integer.parseInt(b[3]));
        });
        return check.result;
    }

    private void run(Set<String> anchors, KbRows items, KbRows matches, KbRows decisions) {
        // ---- fsd_items.csv
        Map<String, String> kindOf = new LinkedHashMap<>();
        Map<String, Integer> itemRow = new HashMap<>();
        List<String> replaced = new ArrayList<>();
        for (int i = 0; i < items.rows.size(); i++) {
            String[] row = items.rows.get(i);
            int number = i + 1;
            String id = items.get(row, "item_id");
            String kind = items.get(row, "kind");
            String existing = items.get(row, "existing_id");
            if (!id.matches("[A-Za-z0-9_-]+")) {
                add("bad_value", ITEMS_FILE, number, id, "", "item_id '" + id + "' is empty or has characters other than "
                        + "letters, digits, '-' and '_'.");
            } else if (kindOf.containsKey(id)) {
                add("bad_value", ITEMS_FILE, number, id, "", "item_id " + id + " is used twice; the first is row "
                        + itemRow.get(id) + ".");
            }
            if (!MatchSchema.ITEM_KINDS.contains(kind)) {
                add("bad_value", ITEMS_FILE, number, id, "", "kind '" + kind + "' is not one of "
                        + String.join(", ", MatchSchema.ITEM_KINDS) + ".");
            }
            if (!kindOf.containsKey(id)) {
                kindOf.put(id, kind);
                itemRow.put(id, number);
            }
            anchors(anchors, items.get(row, "fsd_anchor"), ITEMS_FILE, number, id);
            if (kind.equals(MatchSchema.REPLACES)) {
                result.replaces++;
                if (!existing.isEmpty()) {
                    replaced.add(existing + "\u0000" + number + "\u0000" + id);
                } else {
                    result.notes.add("item " + id + " says the tab replaces a page that is not in the Knowledge Base "
                            + "(existing_id is empty), so it is not compared with the matches.");
                }
            } else {
                result.items++;
                if (!existing.isEmpty()) {
                    add("bad_value", ITEMS_FILE, number, id, existing, "existing_id is only for a row of kind replaces; "
                            + "leave it empty on a " + kind + " item.");
                }
            }
        }

        // ---- matches.csv
        Map<String, List<String>> proposedOf = new HashMap<>();
        Map<String, Set<String>> statusesOf = new HashMap<>();
        Set<String> pairs = new HashSet<>();
        List<String[]> toCompare = new ArrayList<>();
        for (int i = 0; i < matches.rows.size(); i++) {
            String[] row = matches.rows.get(i);
            int number = i + 1;
            String id = matches.get(row, "item_id");
            String rowId = matches.get(row, "row_id");
            String status = matches.get(row, "status");
            String confidence = matches.get(row, "confidence");
            if (!kindOf.containsKey(id)) {
                add("unknown_item", MATCHES_FILE, number, id, rowId, "item_id '" + id + "' is not in " + ITEMS_FILE + ".");
            } else if (kindOf.get(id).equals(MatchSchema.REPLACES)) {
                add("bad_value", MATCHES_FILE, number, id, rowId, "item " + id + " is the page the tab replaces, which "
                        + "has no match.");
            }
            anchors(anchors, matches.get(row, "fsd_anchor"), MATCHES_FILE, number, id);
            if (!MatchSchema.STATUSES.contains(status)) {
                add("bad_value", MATCHES_FILE, number, id, rowId, "status '" + status + "' is not proposed or unmatched "
                        + "(the trace skill never writes confirmed or rejected; those go in " + DECISIONS_FILE + ").");
            }
            int score = -1;
            if (confidence.matches("[0-9]{1,3}")) {
                score = Integer.parseInt(confidence);
            }
            if (score < 0 || score > 100) {
                add("bad_value", MATCHES_FILE, number, id, rowId, "confidence '" + confidence + "' is not a whole number "
                        + "from 0 to 100.");
            } else if (status.equals("proposed") && score < MatchSchema.LOWEST_PROPOSED) {
                add("bad_value", MATCHES_FILE, number, id, rowId, "confidence " + score + " is below "
                        + MatchSchema.LOWEST_PROPOSED + ", so the row should be unmatched, not proposed.");
            } else if (status.equals("unmatched") && score >= MatchSchema.LOWEST_PROPOSED) {
                add("bad_value", MATCHES_FILE, number, id, rowId, "confidence " + score + " is " + MatchSchema.LOWEST_PROPOSED
                        + " or more on an unmatched row; an unmatched row scores below " + MatchSchema.LOWEST_PROPOSED + ".");
            }
            if (matches.get(row, "basis").trim().isEmpty()) {
                add("bad_value", MATCHES_FILE, number, id, rowId, "basis is empty; every row says why.");
            }
            if (status.equals("proposed") && rowId.isEmpty()) {
                add("bad_value", MATCHES_FILE, number, id, rowId, "a proposed row has an empty row_id.");
            }
            if (status.equals("unmatched") && !rowId.isEmpty()) {
                add("bad_value", MATCHES_FILE, number, id, rowId, "an unmatched row has a row_id; leave it empty, and "
                        + "name the nearest candidate in basis if it helps.");
            }
            if (!rowId.isEmpty() && !pairs.add(id + "\u0000" + rowId)) {
                add("bad_value", MATCHES_FILE, number, id, rowId, "the same column is proposed twice for item " + id + ".");
            }
            Set<String> statuses = statusesOf.computeIfAbsent(id, k -> new LinkedHashSet<>());
            String other = status.equals("proposed") ? "unmatched" : "proposed";
            boolean contradicts = MatchSchema.STATUSES.contains(status) && statuses.contains(other) && !statuses.contains(status);
            statuses.add(status);
            if (contradicts) {
                add("bad_value", MATCHES_FILE, number, id, rowId, "item " + id + " has both a proposed and an unmatched "
                        + "row.");
            }
            if (status.equals("proposed")) {
                result.proposed++;
                proposedOf.computeIfAbsent(id, k -> new ArrayList<>()).add(rowId);
            } else if (status.equals("unmatched")) {
                result.unmatched++;
            }
            if (!rowId.isEmpty()) {
                if (source(rowId, MATCHES_FILE, number, id)) {
                    toCompare.add(new String[] {rowId, Integer.toString(number), id, matches.get(row, "fsd_text")});
                }
            }
        }
        for (Map.Entry<String, String> item : kindOf.entrySet()) {
            if (!item.getValue().equals(MatchSchema.REPLACES) && !statusesOf.containsKey(item.getKey())) {
                add("item_without_match", ITEMS_FILE, itemRow.get(item.getKey()), item.getKey(), "", "item "
                        + item.getKey() + " has no row in " + MATCHES_FILE + "; write a proposed or an unmatched row.");
            }
        }

        // ---- the page the tab replaces
        existingReport(replaced, toCompare);

        // ---- decisions.csv
        if (decisions == null) {
            return;
        }
        Set<String> decided = new HashSet<>();
        for (int i = 0; i < decisions.rows.size(); i++) {
            String[] row = decisions.rows.get(i);
            int number = i + 1;
            result.decisions++;
            String id = decisions.get(row, "item_id");
            String decision = decisions.get(row, "decision");
            String rowId = decisions.get(row, "row_id");
            if (!kindOf.containsKey(id) || kindOf.get(id).equals(MatchSchema.REPLACES)) {
                add("unknown_item", DECISIONS_FILE, number, id, rowId, "item_id '" + id + "' is not an item of "
                        + ITEMS_FILE + " that needs data.");
            }
            if (!MatchSchema.DECISION_VALUES.contains(decision)) {
                add("bad_value", DECISIONS_FILE, number, id, rowId, "decision '" + decision + "' is not confirmed or "
                        + "rejected.");
            }
            List<String> proposals = proposedOf.getOrDefault(id, List.of());
            if (rowId.isEmpty()) {
                if (kindOf.containsKey(id) && proposals.size() != 1) {
                    add("bad_value", DECISIONS_FILE, number, id, "", "row_id is empty, which means the item's one "
                            + "proposed column, but item " + id + " has " + proposals.size() + " proposed columns; "
                            + "name the column.");
                }
            } else if (decision.equals("rejected") && !proposals.contains(rowId) && isKnown(rowId)) {
                add("bad_value", DECISIONS_FILE, number, id, rowId, "a rejection names a column that was not proposed "
                        + "for item " + id + ".");
            } else if (decision.equals("confirmed")) {
                source(rowId, DECISIONS_FILE, number, id);
            } else if (!isKnown(rowId)) {
                add("unknown_row", DECISIONS_FILE, number, id, rowId, unknownRow(rowId));
            }
            String key = id + "\u0000" + (rowId.isEmpty() && proposals.size() == 1 ? proposals.get(0) : rowId);
            if (!decided.add(key)) {
                add("bad_value", DECISIONS_FILE, number, id, rowId, "a second verdict on the same column of item " + id
                        + "; keep one.");
            }
        }
    }

    // ------------------------------------------------------------------ identifiers and database sources

    private boolean isKnown(String rowId) {
        return presentationColumns.contains(rowId) || logicalColumns.containsKey(rowId) || analysisColumns.containsKey(rowId);
    }

    private String unknownRow(String rowId) {
        for (String table : kb.tableNames()) {
            KbRows rows = kb.table(table);
            if (rows.has("row_id")) {
                for (String[] row : rows.rows) {
                    if (rows.get(row, "row_id").equals(rowId)) {
                        return rowId + " is a row of " + table + ", not a presentation, logical or report column.";
                    }
                }
            }
        }
        return rowId + " is not a presentation, logical or report column of the Knowledge Base. Copy it from "
                + "find-columns, character for character.";
    }

    /**
     * Checks that a column exists and leads to a database table; adds the finding when not. True when the column
     * exists.
     */
    private boolean source(String rowId, String file, int number, String itemId) {
        if (!isKnown(rowId)) {
            add("unknown_row", file, number, itemId, rowId, unknownRow(rowId));
            return false;
        }
        String logical;
        if (presentationColumns.contains(rowId)) {
            for (String[] path : lineageOf.getOrDefault(rowId, List.of())) {
                if (!kb.table("lineage").get(path, "physical_column_id").isEmpty()) {
                    return true;
                }
            }
            logical = logicalOfPresentation.getOrDefault(rowId, "");
            if (logical.isEmpty()) {
                add("no_physical_source", file, number, itemId, rowId, "the presentation column has no logical column, "
                        + "so its lineage reaches no database table.");
                return true;
            }
        } else if (analysisColumns.containsKey(rowId)) {
            String[] link = reportLineageOf.get(rowId);
            KbRows reportLineage = kb.table("report_lineage");
            if (link != null && !reportLineage.get(link, "physical_column_id").isEmpty()) {
                return true;
            }
            String[] column = analysisColumns.get(rowId);
            boolean computed = kb.table("analysis_columns").get(column, "is_computation").equals("true")
                    || (link != null && reportLineage.get(link, "source_state").equals("computed_not_sourced"))
                    || (link != null && reportLineage.get(link, "resolution").equals("not_a_column_reference"));
            if (computed) {
                add("derived_column", file, number, itemId, rowId, "a report column computed by its formula, `"
                        + kb.table("analysis_columns").get(column, "formula").replace('\n', ' ')
                        + "`, with no database column of its own; check what it is computed from.");
            } else {
                add("no_physical_source", file, number, itemId, rowId, "the report column is not linked to a database "
                        + "column (report_lineage resolution: " + (link == null ? "no row" : reportLineage.get(link, "resolution"))
                        + ").");
            }
            return true;
        } else {
            logical = rowId;
            for (String[] map : mappingsOf.getOrDefault(rowId, List.of())) {
                if (!kb.table("lts_column_map").get(map, "physical_column_id").isEmpty()) {
                    return true;
                }
            }
        }
        String[] column = logicalColumns.get(logical);
        KbRows logicalTable = kb.table("logical_columns");
        if (column != null && !logicalTable.get(column, "derived_expr_text").isEmpty()) {
            List<String> refs = new ArrayList<>();
            for (String ref : logicalTable.get(column, "derived_expr_refs").split("\n")) {
                if (!ref.isEmpty()) {
                    refs.add(ref + (reachesDatabase(ref, new HashSet<>()) ? " (reaches a database column)"
                            : " (reaches no database column)"));
                }
            }
            add("derived_column", file, number, itemId, rowId, "a derived column: the logical column "
                    + logical + " is computed by its formula and no table source maps it"
                    + (refs.isEmpty() ? "." : "; the formula refers to " + String.join(", ", refs) + "."));
        } else {
            add("no_physical_source", file, number, itemId, rowId, "lineage reaches no database table: no table source "
                    + "maps the logical column " + logical + " to a database column, and it has no formula.");
        }
        return true;
    }

    /** True when a logical column, or a column its formula refers to (to any depth), is mapped from a database column. */
    private boolean reachesDatabase(String logical, Set<String> seen) {
        if (!seen.add(logical)) {
            return false;
        }
        for (String[] map : mappingsOf.getOrDefault(logical, List.of())) {
            if (!kb.table("lts_column_map").get(map, "physical_column_id").isEmpty()) {
                return true;
            }
        }
        String[] column = logicalColumns.get(logical);
        if (column == null) {
            return false;
        }
        for (String ref : kb.table("logical_columns").get(column, "derived_expr_refs").split("\n")) {
            if (!ref.isEmpty() && reachesDatabase(ref, seen)) {
                return true;
            }
        }
        return false;
    }

    // ------------------------------------------------------------------ the existing report

    private void existingReport(List<String> replaced, List<String[]> toCompare) {
        KbRows analyses = kb.table("analyses");
        if (analyses.rows.isEmpty()) {
            result.notes.add("no existing report was available: this Knowledge Base has no reports (it was built without "
                    + "a catalog), so the matches were not compared with one.");
            return;
        }
        if (replaced.isEmpty()) {
            if (result.replaces == 0) {
                result.notes.add("the FSD items name no page or report that the tab replaces (no row of kind replaces), "
                        + "so the matches were not compared with an existing report.");
            }
            return;
        }
        Set<String> analysisIds = new HashSet<>();
        for (String[] row : analyses.rows) {
            analysisIds.add(analyses.get(row, "row_id"));
        }
        Set<String> dashboardIds = ids(kb.table("dashboards"));
        KbRows pages = kb.table("dashboard_pages");
        KbRows contents = kb.table("dashboard_contents");
        Set<String> reports = new LinkedHashSet<>();
        List<String> named = new ArrayList<>();
        for (String entry : replaced) {
            String[] parts = entry.split("\u0000");
            String existing = parts[0];
            Set<String> pageIds = new LinkedHashSet<>();
            if (analysisIds.contains(existing)) {
                reports.add(existing);
            } else if (dashboardIds.contains(existing)) {
                for (String[] page : pages.rows) {
                    if (pages.get(page, "dashboard_id").equals(existing)) {
                        pageIds.add(pages.get(page, "row_id"));
                    }
                }
            } else if (ids(pages).contains(existing)) {
                pageIds.add(existing);
            } else {
                add("unknown_row", ITEMS_FILE, Integer.parseInt(parts[1]), parts[2], existing, existing + " is not a "
                        + "dashboard page, dashboard or report of the Knowledge Base.");
                continue;
            }
            named.add(existing);
            for (String[] content : contents.rows) {
                String container = contents.get(content, "container_id");
                if (pageIds.contains(container) || pageIds.stream().anyMatch(p -> container.startsWith(p + "#"))) {
                    String target = contents.get(content, "target_id");
                    if (analysisIds.contains(target)) {
                        reports.add(target);
                    }
                }
            }
        }
        if (named.isEmpty()) {
            return;
        }
        KbRows columns = kb.table("analysis_columns");
        KbRows reportLineage = kb.table("report_lineage");
        Set<String> reportColumns = new LinkedHashSet<>();
        Set<String> reportPresentation = new HashSet<>();
        Set<String> reportLogical = new HashSet<>();
        List<String[]> described = new ArrayList<>();
        for (String[] column : columns.rows) {
            if (reports.contains(columns.get(column, "analysis_id"))) {
                String id = columns.get(column, "row_id");
                reportColumns.add(id);
                described.add(new String[] {id, columns.get(column, "heading") + " " + columns.get(column, "formula")});
                String[] link = reportLineageOf.get(id);
                if (link != null) {
                    reportPresentation.add(reportLineage.get(link, "presentation_column_id"));
                    reportLogical.add(reportLineage.get(link, "logical_column_id"));
                }
            }
        }
        result.reportColumnsCompared = reportColumns.size();
        result.notes.add("compared with " + reports.size() + " report" + (reports.size() == 1 ? "" : "s") + " ("
                + reportColumns.size() + " report columns) of " + String.join(", ", named) + ".");
        for (String[] match : toCompare) {
            String rowId = match[0];
            boolean agrees = reportColumns.contains(rowId) || reportPresentation.contains(rowId) || reportLogical.contains(rowId)
                    || reportLogical.contains(logicalOfPresentation.getOrDefault(rowId, "\u0000"));
            if (agrees) {
                continue;
            }
            List<String> alike = new ArrayList<>();
            List<String> itemWords = ColumnSearch.words(match[3]);
            for (String[] column : described) {
                List<String> words = ColumnSearch.words(column[1]);
                boolean shares = false;
                for (String word : itemWords) {
                    if (ColumnSearch.STOP_WORDS.contains(word)) {
                        continue;
                    }
                    for (String other : words) {
                        shares |= ColumnSearch.likeness(word, other) == 1;
                    }
                }
                if (shares && alike.size() < 3) {
                    alike.add(column[0]);
                }
            }
            add("differs_from_existing_report", MATCHES_FILE, Integer.parseInt(match[1]), match[2], rowId, "the column is "
                    + "not among the " + reportColumns.size() + " report columns of the page the tab replaces; "
                    + (alike.isEmpty() ? "none of them shares a word with the item's text."
                    : "report column" + (alike.size() == 1 ? "" : "s") + " sharing a word with the item's text: "
                    + String.join(", ", alike) + "."));
        }
    }

    // ------------------------------------------------------------------ helpers

    private void anchors(Set<String> known, String value, String file, int number, String itemId) {
        if (value.isEmpty()) {
            add("bad_value", file, number, itemId, "", "fsd_anchor is empty; every row names where it is in fsd.md.");
            return;
        }
        for (String anchor : value.split(" ", -1)) {
            String bare = anchor.startsWith("[") && anchor.endsWith("]") ? anchor.substring(1, anchor.length() - 1) : anchor;
            if (!FsdDocument.BARE_ANCHOR.matcher(bare).matches()) {
                add("unknown_anchor", file, number, itemId, "", "'" + anchor + "' is not an anchor: write p12 or t3.r2.c1, "
                        + "without brackets, several separated by single spaces.");
            } else if (!known.contains(bare)) {
                add("unknown_anchor", file, number, itemId, "", "anchor " + bare + " is not in fsd.md.");
            }
        }
    }

    private void add(String finding, String file, int row, String itemId, String rowId, String detail) {
        String severity = MatchSchema.WARNINGS.contains(finding) ? "warning" : "failure";
        result.findings.add(new String[] {finding, severity, file, Integer.toString(row), itemId, rowId, detail});
    }

    private static Set<String> ids(KbRows table) {
        Set<String> ids = new HashSet<>();
        for (String[] row : table.rows) {
            ids.add(table.get(row, "row_id"));
        }
        return ids;
    }

    private static Map<String, List<String[]>> groupBy(KbRows table, String column) {
        Map<String, List<String[]>> groups = new HashMap<>();
        for (String[] row : table.rows) {
            groups.computeIfAbsent(table.get(row, column), k -> new ArrayList<>()).add(row);
        }
        return groups;
    }
}
