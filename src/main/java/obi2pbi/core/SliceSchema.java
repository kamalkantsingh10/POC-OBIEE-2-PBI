package obi2pbi.core;

import java.util.ArrayList;
import java.util.List;

/**
 * The tables a Slice holds besides the Knowledge Base's own (Architecture AD-9, AD-17), and the table
 * {@code check-doc} writes. Declared once here, like the Knowledge Base tables in {@link KbSchema}; a
 * Slice's {@code schema_tables.csv} and {@code schema.csv} are written from both.
 */
public final class SliceSchema {

    public static final String UNITS = "units";
    public static final String REQUIRED_ROWS = "required_rows";
    public static final String CHECK = "check";
    /** Only in a Slice cut for a tab: the FSD's items with the columns the Operator confirmed. */
    public static final String TAB_FIELDS = "tab_fields";

    /**
     * Knowledge Base tables a Slice does not carry: they count objects over the whole estate and have no
     * rows that belong to one subject area.
     */
    public static final List<String> NOT_SLICED = List.of("coverage", "coverage_summary", "catalog_versions");

    private SliceSchema() {
    }

    /** The Knowledge Base tables a Slice carries, in the Knowledge Base's write order. */
    public static List<KbTable> carriedTables() {
        List<KbTable> carried = new ArrayList<>();
        for (KbTable table : KbSchema.TABLES) {
            if (table.hasRows() && !NOT_SLICED.contains(table.name)
                    && !table.name.equals(KbSchema.SCHEMA_TABLES) && !table.name.equals(KbSchema.SCHEMA_COLUMNS)) {
                carried.add(table);
            }
        }
        return carried;
    }

    /** Every table of a Slice in write order: the carried tables, the Slice's own, the two schema tables, the manifest. */
    public static List<KbTable> sliceTables() {
        List<KbTable> all = carriedTables();
        all.add(units());
        all.add(requiredRows());
        all.add(tabFields());
        all.add(KbSchema.table(KbSchema.SCHEMA_TABLES));
        all.add(KbSchema.table(KbSchema.SCHEMA_COLUMNS));
        all.add(manifest());
        return all;
    }

    public static KbTable units() {
        return new KbTable(UNITS, KbTable.RUN,
                "The work units of this Slice, in the order a document about it should be written. A skill "
                        + "takes one unit at a time and reads only that unit's packet.",
                "One work unit: one packet file under packets/.",
                false,
                List.of(
                        KbColumn.key("unit_id", "The unit's identifier; its packet is packets/<unit_id>.md.")
                                .note("Lower-case letters, digits and hyphens. A unit that was split ends in "
                                        + "-p1, -p2 ...; each part is a unit of its own."),
                        KbColumn.of("position", "The unit's place in writing order, counting from 1; a whole number."),
                        KbColumn.of("kind",
                                "What the unit is about: overview, logical_table, physical_table, joins, "
                                        + "hierarchies, variables, security, reports, saved_filters or gaps.")
                                .note("A logical_table unit says what a table of the business model offers "
                                        + "and where its data comes from; a physical_table unit states one "
                                        + "database table as the repository knows it, whole: every column, "
                                        + "its keys, and what it is in the database."),
                        KbColumn.of("title", "A short title for the unit, naming what it covers."),
                        KbColumn.of("part", "Which part this is of a unit that was split, counting from 1; "
                                + "1 for a unit that was not split."),
                        KbColumn.of("parts", "How many parts the unit was split into; 1 when it was not split."),
                        KbColumn.of("packet_file", "The packet's path inside the Slice folder, with forward slashes."),
                        KbColumn.of("line_count", "How many lines the packet has; never more than the manifest's "
                                + "max_packet_lines (120 unless the slice command was told otherwise)."),
                        KbColumn.of("required_rows",
                                "How many rows of required_rows belong to this unit: the rows a document "
                                        + "must cover when it writes this unit's section.")),
                null);
    }

    /** The FSD's items in a Slice cut for a tab (story 4.1), built from fsd_items.csv, matches.csv and decisions.csv. */
    public static KbTable tabFields() {
        return new KbTable(TAB_FIELDS, KbTable.RUN,
                "The FSD's items that need data, each with the columns the Operator confirmed for it, its decision "
                        + "notes, and the FSD's words and anchor. Only in a Slice cut for a tab. Built from the tab's "
                        + "fsd_items.csv, matches.csv and decisions.csv; the rows a to-be document cites for the tab's "
                        + "fields.",
                "One item of the FSD (not the row naming the page the tab replaces).",
                false,
                List.of(
                        KbColumn.key("row_id", "The item's identifier in this Slice: item:: followed by its item_id.")
                                .note("Cited like any row, as [kb:item::K1]. Not a Knowledge Base identifier: it names an "
                                        + "item of the tab's FSD, not an OBIEE object."),
                        KbColumn.of("item_id", "The item, as in the tab's fsd_items.csv."),
                        KbColumn.of("kind", "measure, attribute, filter or calculation, as fsd_items.csv says."),
                        KbColumn.of("fsd_anchor", "Where the item is in the tab's fsd.md: anchors without brackets, "
                                + "separated by single spaces."),
                        KbColumn.of("fsd_text", "The item's words, as fsd_items.csv has them."),
                        KbColumn.of("status", "confirmed (at least one column confirmed), rejected (every verdict a "
                                + "rejection), unmatched (the trace skill found no column and there is no verdict) or "
                                + "undecided (no verdict yet). Only confirmed items contribute rows to the Slice."),
                        KbColumn.of("row_ids", "The confirmed columns, one Row identifier per line, in decision order: "
                                + "presentation, logical or report columns of this Slice. Empty unless confirmed.")
                                .note("Each line is a row of presentation_columns, logical_columns or analysis_columns."),
                        KbColumn.of("notes", "The Operator's decision notes for the item, one per line, in "
                                + "decision order, each different note once; empty when there are none.")),
                null);
    }

    public static KbTable requiredRows() {
        return new KbTable(REQUIRED_ROWS, KbTable.RUN,
                "The rows a document about this Slice must cover: each must be cited as [kb:<row_id>] or "
                        + "listed in the document's 'Not described' section with a reason. check-doc reads "
                        + "this list; it is the completeness contract between slice and check-doc.",
                "One row of a Slice table that a document must cover.",
                true,
                List.of(
                        KbColumn.key("row_id", "The Row identifier of the row that must be covered.")
                                .note("A row of the table named in the 'table' column. Cited in a packet as "
                                        + "[kb:<row_id>], with ']' written '\\]' and '\\' written '\\\\'."),
                        KbColumn.of("table", "The Slice table the row is in.")
                                .references(KbSchema.SCHEMA_TABLES, "table"),
                        KbColumn.of("unit_id", "The unit whose packet carries the row; every required row is "
                                + "in exactly one packet.")
                                .references(UNITS, "unit_id")),
                null);
    }

    /** The findings table {@code check-doc} writes beside a document, as {@code <document>.check.csv}. */
    public static KbTable check() {
        return new KbTable(CHECK, KbTable.RUN,
                "What check-doc found wrong with a skill-written document. Only a header when the document "
                        + "passes.",
                "One finding.",
                false,
                List.of(
                        KbColumn.of("finding",
                                "The kind of finding: not_covered, unknown_citation, quote_not_in_packet, "
                                        + "unknown_answer, malformed_question, not_described_without_reason, "
                                        + "bad_status, reviewed_with_findings or reviewed_without_review_log; "
                                        + "or the warning citation_from_other_unit.")
                                .note("citation_from_other_unit is a warning: it is listed so that a "
                                        + "reviewer can look at it, and it does not make the check fail."),
                        KbColumn.of("row_id", "The Row identifier the finding is about; empty when it is "
                                + "not about a row."),
                        KbColumn.of("table", "The Slice table of that row; empty when unknown or not about a row."),
                        KbColumn.of("line", "The line of the document the finding is at, counting from 1; "
                                + "empty when it is not at a line (a row the document never mentions)."),
                        KbColumn.of("unit_id", "The unit to correct: for a row that is not covered, the unit "
                                + "whose packet states it (required_rows.unit_id); for a finding at a line, the "
                                + "part of the document the line is in (its file name in the parts folder without "
                                + "the number and '.md'), when the document is the joined parts. Empty when "
                                + "unknown."),
                        KbColumn.of("detail", "A sentence saying what is wrong.")),
                null);
    }

    public static KbTable manifest() {
        return new KbTable(KbSchema.MANIFEST, KbTable.RUN,
                "What produced this Slice, from which Knowledge Base and subject area, and the list of its tables.",
                "One fact about the Slice, as a key and a value.",
                false,
                List.of(
                        KbColumn.key("key",
                                "The fact's name: slicer_version, sliced_at, knowledge_base, kb_extracted_at, "
                                        + "kb_extractor_version, rpd_ids, halves_present:<estate>, cut_by, "
                                        + "subject_area, subject_area_id (a subject-area Slice), tab, subject_areas, "
                                        + "subject_area_ids, decisions (a tab Slice), unparsed_identifiers_only, boundary_rows, errors_not_copied, "
                                        + "unparsed_not_copied, unparsed_not_copied_by_type, "
                                        + "not_in_slice:<what>, max_packet_lines, rows, units or tables."),
                        KbColumn.of("value", "The fact's value.")
                                .note("'subject_area' is the subject area's name exactly as the repository "
                                        + "has it; the folder name is a simplified form of it. 'tables': the "
                                        + "table names separated by single spaces; read the table list from "
                                        + "here, not from the folder. 'sliced_at' is the only time stamp in "
                                        + "the Slice. 'boundary_rows': how many rows are in the Slice only so "
                                        + "that a reference from another row resolves. 'errors_not_copied' "
                                        + "and 'unparsed_not_copied': how many rows of the Knowledge Base's "
                                        + "errors and unparsed tables are not about an object or object type "
                                        + "in this Slice and were left out, by kind. "
                                        + "'unparsed_not_copied_by_type': the same rows of unparsed counted by "
                                        + "kind and object type, one line each inside the value: kind, object "
                                        + "type, number of rows, sum of their occurrences, separated by ' | '. "
                                        + "'max_packet_lines': the most lines a packet of this Slice may have "
                                        + "(the slice command's --max-packet-lines). "
                                        + "'not_in_slice:<what>': how many rows of the "
                                        + "Knowledge Base that belong to no subject area are not in this Slice "
                                        + "(permissions_without_target, role_privileges, init_blocks, variables); "
                                        + "the overview packet says where to find them. 'cut_by': subject_area or "
                                        + "tab. 'tab': the tab's folder name; 'subject_areas' and 'subject_area_ids': "
                                        + "the subject areas the tab's confirmed columns are in, one per line; "
                                        + "'decisions': counts of the tab's decisions and items. "
                                        + "'unparsed_identifiers_only': the details of the unparsed rows that are only "
                                        + "an identifier of an element whose content is read, one per line; the gaps "
                                        + "packet lists them apart.")),
                null);
    }
}
