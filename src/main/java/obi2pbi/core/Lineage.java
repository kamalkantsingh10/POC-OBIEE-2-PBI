package obi2pbi.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

/**
 * What is worked out from the rows once every object has been read: how many sources map each logical
 * column, and the lineage rows. Reads only the Estate; no files, no XML.
 */
public final class Lineage {

    private Lineage() {
    }

    /**
     * Sets, on each logical column, the number of distinct table sources that map it. This is not the
     * owning table's source count: a table with four sources may map a column in only two.
     */
    public static void countMappedSources(Estate estate) {
        Map<String, Set<String>> sourcesOf = new HashMap<>();
        for (ColumnMapping mapping : estate.columnMappings) {
            if (!mapping.logicalColumnId.isEmpty()) {
                sourcesOf.computeIfAbsent(mapping.logicalColumnId, k -> new HashSet<>()).add(mapping.sourceId);
            }
        }
        ListIterator<LogicalColumn> columns = estate.logicalColumns.listIterator();
        while (columns.hasNext()) {
            LogicalColumn column = columns.next();
            Set<String> sources = sourcesOf.get(column.rowId);
            columns.set(column.withMappedSourceCount(sources == null ? 0 : sources.size()));
        }
    }

    /**
     * Adds one lineage row for each presentation column and each mapping of its logical column.
     *
     * <ul>
     * <li>A presentation column with no logical column gives no row.</li>
     * <li>A logical column no source maps (one defined only by a formula) gives no row; the absence is the
     * signal, and the formula's references say what it is computed from.</li>
     * <li>A mapping with no physical column still gives a row, with the two physical identifiers empty.</li>
     * <li>A row ends at the table the mapping names. For an alias that is the alias; the table it stands
     * for is reached through the alias's own row.</li>
     * <li>Only the first physical column of a mapping is recorded (a known limit, kept).</li>
     * </ul>
     */
    public static void build(Estate estate) {
        Map<String, String> tableOfColumn = new HashMap<>();
        for (PhysicalColumn column : estate.physicalColumns) {
            tableOfColumn.put(column.rowId, column.tableId);
        }
        Map<String, List<ColumnMapping>> mappingsOf = new HashMap<>();
        for (ColumnMapping mapping : estate.columnMappings) {
            if (!mapping.logicalColumnId.isEmpty()) {
                mappingsOf.computeIfAbsent(mapping.logicalColumnId, k -> new ArrayList<>()).add(mapping);
            }
        }
        for (PresentationColumn column : estate.presentationColumns) {
            if (column.logicalColumnId.isEmpty()) {
                continue;
            }
            for (ColumnMapping mapping : mappingsOf.getOrDefault(column.logicalColumnId, List.of())) {
                estate.lineage.add(new LineageRow(
                        column.rowId + "#" + mapping.sourceId,
                        column.rpdId,
                        column.rowId,
                        column.logicalColumnId,
                        mapping.sourceId,
                        tableOfColumn.getOrDefault(mapping.physicalColumnId, ""),
                        mapping.physicalColumnId));
            }
        }
    }
}
