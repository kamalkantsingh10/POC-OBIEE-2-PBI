package obi2pbi.core;

import java.util.List;

/**
 * The files of a tab's work folder between the FSD and the Operator's decisions (stories 3.2 and 3.3;
 * Architecture AD-10, AD-11): {@code fsd_items.csv} and {@code matches.csv}, written by the trace skill;
 * {@code decisions.csv}, written by the Operator; and {@code check.csv}, written by {@code check-matches}.
 * Declared once here, like the Knowledge Base tables in {@link KbSchema}; {@code docs/skill-conventions.md}
 * (section 10) says the same in words for the skill's author, and a test keeps the two in step.
 */
public final class MatchSchema {

    public static final String ITEMS = "fsd_items";
    public static final String MATCHES = "matches";
    public static final String DECISIONS = "decisions";
    public static final String CHECK = "check";

    /** The kinds of FSD item. {@code replaces} is not an item that needs data: it names the page the tab replaces. */
    public static final List<String> ITEM_KINDS = List.of("measure", "attribute", "filter", "calculation", "replaces");
    public static final String REPLACES = "replaces";
    public static final List<String> STATUSES = List.of("proposed", "unmatched");
    public static final List<String> DECISION_VALUES = List.of("confirmed", "rejected");
    /** A proposed match has at least this confidence; below it the item is written as unmatched (scoring bands). */
    public static final int LOWEST_PROPOSED = 30;

    /** The findings of {@code check-matches}, failures first, then warnings. */
    public static final List<String> FAILURES = List.of("unknown_row", "no_physical_source", "item_without_match",
            "unknown_anchor", "unknown_item", "bad_value");
    public static final List<String> WARNINGS = List.of("derived_column", "differs_from_existing_report");

    private MatchSchema() {
    }

    public static KbTable items() {
        return new KbTable(ITEMS, KbTable.RUN,
                "The FSD's items that need data, as the trace skill lists them, and the page the tab replaces "
                        + "when the FSD names one. check-matches checks that every item has a row in matches.csv.",
                "One item of the FSD: a figure, a field, a filter or a calculation; or the page the tab replaces.",
                false,
                List.of(
                        KbColumn.key("item_id", "The item's identifier, unique in the file: letters, digits, '-' and "
                                + "'_'. The FSD's own number when it has one (F1, K3, V2-office).")
                                .note("matches.csv and decisions.csv refer to the item by this identifier."),
                        KbColumn.of("kind", "What the item is: measure (a number that is added up), attribute (a value "
                                + "shown or grouped by), filter (a slicer or prompt), calculation (worked out from other "
                                + "figures), or replaces (not an item: the page or report the tab replaces).")
                                .note("A replaces row has no match; it lets check-matches compare the matches with the "
                                        + "report columns of the page it names."),
                        KbColumn.of("fsd_anchor", "Where the item is in fsd.md: one anchor without brackets (p12, "
                                + "t3.r2.c1), or several separated by single spaces, the main one first."),
                        KbColumn.of("fsd_text", "The item's words, copied from fsd.md at that anchor."),
                        KbColumn.of("existing_id", "For a replaces row: the Row identifier of the dashboard page, "
                                + "dashboard or report (analysis) the tab replaces; empty when the Knowledge Base does "
                                + "not hold it. Empty on every other row.")
                                .references("dashboard_pages", "row_id")
                                .note("Also a row of dashboards or analyses.")),
                null);
    }

    public static KbTable matches() {
        return new KbTable(MATCHES, KbTable.RUN,
                "The trace skill's proposal of the OBIEE column behind each FSD item (Architecture AD-11). Only the "
                        + "trace skill writes it; it never confirms its own proposal.",
                "One column proposed for an item, or the statement that the item has none. An item that needs two "
                        + "columns (a ratio) has two rows.",
                false,
                List.of(
                        KbColumn.of("item_id", "The item.").references(ITEMS, "item_id"),
                        KbColumn.of("fsd_anchor", "The anchor (without brackets) in fsd.md that the match is about.")
                                .note("Usually the item's main anchor."),
                        KbColumn.of("fsd_text", "The words of the FSD the match is about, copied from fsd.md."),
                        KbColumn.of("row_id", "The Row identifier of the column proposed, exactly as find-columns prints "
                                + "it between [kb: and ]: a presentation column (preferred), a logical column, or a "
                                + "report column (analysis_columns). Empty when status is unmatched.")
                                .references("presentation_columns", "row_id")
                                .note("Also a row of logical_columns or analysis_columns."),
                        KbColumn.of("confidence", "How sure the skill is, a whole number from 0 to 100: 90-100 exact "
                                + "subject area and column name, or agreement with the existing report; 60-89 the same "
                                + "column name in another table, or a close name with matching aggregation; 30-59 a "
                                + "plausible name only; below 30 the row is unmatched."),
                        KbColumn.of("basis", "Why: the evidence for the column and the score, naming the runners-up "
                                + "when there were several close candidates; for unmatched, what was searched."),
                        KbColumn.of("status", "proposed or unmatched.")),
                null);
    }

    public static KbTable decisions() {
        return new KbTable(DECISIONS, KbTable.RUN,
                "The Operator's verdicts on the proposed matches (Architecture AD-11). Written by the Operator only; "
                        + "no program writes it. slice and the later skills use confirmed rows only.",
                "One verdict on one item's column.",
                false,
                List.of(
                        KbColumn.of("item_id", "The item.").references(ITEMS, "item_id"),
                        KbColumn.of("decision", "confirmed or rejected."),
                        KbColumn.of("row_id", "The column the verdict is about. Empty: the item's one proposed column. "
                                + "A different Row identifier with confirmed: the Operator's correction, the column to "
                                + "use instead.")
                                .references("presentation_columns", "row_id")
                                .note("Also a row of logical_columns or analysis_columns."),
                        KbColumn.of("note", "The Operator's reason or remark; may be empty.")),
                null);
    }

    public static KbTable check() {
        return new KbTable(CHECK, KbTable.RUN,
                "What check-matches found wrong with fsd_items.csv, matches.csv and decisions.csv. Only a header "
                        + "when nothing is wrong. Rewritten on every run.",
                "One finding: a failure or a warning.",
                false,
                List.of(
                        KbColumn.of("finding", "The kind of finding. Failures: unknown_row, no_physical_source, "
                                + "item_without_match, unknown_anchor, unknown_item, bad_value. Warnings: "
                                + "derived_column, differs_from_existing_report."),
                        KbColumn.of("severity", "failure (the run exits with 3) or warning (listed for the Operator; "
                                + "does not fail the check)."),
                        KbColumn.of("file", "The file the finding is in: fsd_items.csv, matches.csv or decisions.csv."),
                        KbColumn.of("row", "The data row of that file, counting from 1 after the header."),
                        KbColumn.of("item_id", "The item the row is about; empty when the row has none."),
                        KbColumn.of("row_id", "The Row identifier the finding is about; empty when none."),
                        KbColumn.of("detail", "A sentence saying what is wrong and, where it helps, what to look at.")),
                null);
    }
}
