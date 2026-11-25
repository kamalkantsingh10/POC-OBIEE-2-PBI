package obi2pbi.core;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Links each report column to the repository: the presentation column its formula names, and through
 * repository lineage the logical and physical column. Reads only the merged Estate; no files, no XML.
 *
 * <p>The link is by name, because a name is all the catalog stores, and only inside one estate
 * (Architecture AD-7). Every report column gets exactly one row; a column that cannot be linked says why
 * in {@code resolution}. Nothing here is an error, so no error row is written.
 *
 * <p>Only a formula that is nothing but {@code "Table"."Column"} is looked up. A calculation is marked
 * {@code not_a_column_reference}; the columns inside it are not looked for.
 */
public final class ReportLineage {

    /** A formula that is one reference and nothing else: a quoted name, a dot, a quoted name. */
    private static final Pattern BARE = Pattern.compile("\"([^\"]+)\"\\.\"([^\"]+)\"");
    /** How a formula over an uploaded dataset starts. Letter case matters. */
    private static final String UPLOADED_DATASET = "XSA(";
    private static final char SEP = '\u0000';

    private ReportLineage() {
    }

    /** Replaces {@code estate.reportLineage} with one row per report column. Running it twice changes nothing. */
    public static void resolve(Estate estate) {
        estate.reportLineage.clear();
        if (estate.analysisColumns.isEmpty()) {
            return;
        }

        // Estate, subject area, table, column -> presentation column. Where two entries fold to the same
        // lower-case key the later one in reading order replaces the earlier, as in the reference.
        Map<String, String> areaNameOf = new HashMap<>();
        for (SubjectArea area : estate.subjectAreas) {
            areaNameOf.put(area.rowId, area.name);
        }
        Map<String, String[]> areaAndTableOf = new HashMap<>();
        for (PresentationTable table : estate.presentationTables) {
            areaAndTableOf.put(table.rowId,
                    new String[] {areaNameOf.getOrDefault(table.subjectAreaId, ""), table.name});
        }
        Map<String, String> columns = new HashMap<>();
        Map<String, String> columnsFolded = new HashMap<>();
        for (PresentationColumn column : estate.presentationColumns) {
            String[] areaAndTable = areaAndTableOf.get(column.tableId);
            if (areaAndTable == null || areaAndTable[0].isEmpty()) {
                continue;
            }
            columns.put(key(column.rpdId, areaAndTable[0], areaAndTable[1], column.name), column.rowId);
            columnsFolded.put(key(column.rpdId, fold(areaAndTable[0]), fold(areaAndTable[1]), fold(column.name)),
                    column.rowId);
        }
        Map<String, String> areas = new HashMap<>();
        Map<String, String> areasFolded = new HashMap<>();
        for (SubjectArea area : estate.subjectAreas) {
            areas.put(key(area.rpdId, area.name), area.rowId);
            areasFolded.put(key(area.rpdId, fold(area.name)), area.rowId);
        }
        Map<String, AnalysisCriteria> criteria = new HashMap<>();
        for (AnalysisCriteria block : estate.analysisCriteria) {
            criteria.put(block.rowId, block);
        }
        Map<String, LineageRow> lineage = firstLineageRows(estate.lineage);

        for (AnalysisColumn column : estate.analysisColumns) {
            AnalysisCriteria block = criteria.get(column.criteriaId);
            // The quotes are the catalog's own. Taking them off is for the lookup only; the row keeps
            // the value as written.
            String stated = block == null ? "" : block.subjectArea;
            String areaName = stripQuotes(stated);
            String areaId = areas.get(key(column.rpdId, areaName));
            if (areaId == null || areaId.isEmpty()) {
                areaId = areasFolded.getOrDefault(key(column.rpdId, fold(areaName)), "");
            }

            String formula = Text.strip(column.formula);
            Matcher bare = BARE.matcher(formula);
            boolean isBare = bare.matches();
            String resolution;
            if (formula.isEmpty()) {
                resolution = ReportLineageRow.NOT_A_COLUMN_REFERENCE;
            } else if (!isBare && formula.startsWith(UPLOADED_DATASET)) {
                resolution = ReportLineageRow.OUTSIDE_SEMANTIC_MODEL;
            } else if (!isBare) {
                resolution = ReportLineageRow.NOT_A_COLUMN_REFERENCE;
            } else {
                resolution = ReportLineageRow.UNRESOLVED;
            }

            String presentationId = "";
            if (isBare && !areaName.isEmpty()) {
                presentationId = columns.getOrDefault(key(column.rpdId, areaName, bare.group(1), bare.group(2)), "");
                if (!presentationId.isEmpty()) {
                    resolution = ReportLineageRow.EXACT;
                } else {
                    presentationId = columnsFolded.getOrDefault(
                            key(column.rpdId, fold(areaName), fold(bare.group(1)), fold(bare.group(2))), "");
                    if (!presentationId.isEmpty()) {
                        resolution = ReportLineageRow.CASE_NORMALISED;
                    }
                }
            }

            String logicalId = "";
            String physicalId = "";
            String sourceState = "";
            if (!presentationId.isEmpty()) {
                LineageRow row = lineage.get(presentationId);
                if (row != null) {
                    logicalId = row.logicalColumnId;
                    physicalId = row.physicalColumnId;
                    sourceState = ReportLineageRow.SOURCED;
                } else {
                    // Found, and calculated in the business model. The empty physical column is a fact
                    // about the column, not a lookup that failed.
                    sourceState = ReportLineageRow.COMPUTED_NOT_SOURCED;
                }
            }

            estate.reportLineage.add(new ReportLineageRow(
                    // From the column's own path, not by nesting its identifier: an identifier holds the
                    // separator, and one inside another could not be read back.
                    Identifiers.makeId(column.rpdId, Identifiers.CATALOG,
                            Identifiers.parseId(column.rowId)[2] + "#lineage"),
                    column.rpdId, column.analysisId, column.criteriaId, column.rowId, stated, areaId,
                    column.formula, presentationId, resolution, logicalId, physicalId, sourceState));
        }
    }

    /**
     * For each presentation column, the repository lineage row a report column is linked through: the
     * first one in the order {@code lineage.csv} is written in (by Row identifier, then by the rest of the
     * row). Deliberate difference 22: the reference takes the last row in reading order, which depends on
     * the names of the source's files and cannot be told from the Knowledge Base.
     */
    static Map<String, LineageRow> firstLineageRows(List<LineageRow> rows) {
        Map<String, LineageRow> first = new HashMap<>();
        for (LineageRow row : rows) {
            LineageRow held = first.get(row.presentationColumnId);
            if (held == null || Text.compareRows(cells(row), cells(held)) < 0) {
                first.put(row.presentationColumnId, row);
            }
        }
        return first;
    }

    /** The row as {@code lineage.csv} holds it, for ordering. */
    private static String[] cells(LineageRow r) {
        return new String[] {r.rowId, r.rpdId, r.presentationColumnId, r.logicalColumnId, r.sourceId,
                r.physicalTableId, r.physicalColumnId};
    }

    /** The text without the double quotes at its start and at its end, however many there are. */
    static String stripQuotes(String text) {
        int start = 0;
        int end = text.length();
        while (start < end && text.charAt(start) == '"') {
            start++;
        }
        while (end > start && text.charAt(end - 1) == '"') {
            end--;
        }
        return text.substring(start, end);
    }

    private static String fold(String name) {
        return name.toLowerCase(Locale.ROOT);
    }

    private static String key(String... parts) {
        return String.join(String.valueOf(SEP), parts);
    }
}
