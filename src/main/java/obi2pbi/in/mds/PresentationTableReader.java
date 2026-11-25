package obi2pbi.in.mds;

import obi2pbi.core.ExtractionError;
import obi2pbi.core.Identifiers;
import obi2pbi.core.PresentationColumn;
import obi2pbi.core.PresentationTable;
import obi2pbi.in.xml.XmlNode;

/**
 * Reads {@code PresentationTable} files: the table and its columns, each tied to its logical column.
 *
 * <p>Not read, and therefore reported in {@code unparsed.csv}: the references to the table's hierarchies,
 * and aliases below the table's own (those of its columns).
 */
public final class PresentationTableReader implements ObjectReader {

    private static final String TABLE = "PresentationTable";
    private static final String COLUMN = "PresentationColumn";

    @Override
    public String objectType() {
        return TABLE;
    }

    @Override
    public void bind(XmlNode root, ReadContext context) {
        String name = root.attr("name");
        // The subject area was bound by its own reader, which is registered before this one.
        String subjectAreaId = context.index.resolve(root.attr("containerRef"));
        if (name.isEmpty() || subjectAreaId.isEmpty()) {
            context.error(ExtractionError.UNRESOLVED_REFERENCE, ReadContext.sourceIdOf(root), TABLE,
                    "table name or owning Subject Area could not be resolved");
            return;
        }
        String areaPath = Identifiers.parseId(subjectAreaId)[2];
        String tableId = context.makeId(root, TABLE, Identifiers.PRESENTATION, areaPath, name);
        if (tableId == null) {
            return;
        }
        context.index.bind(root, tableId);

        for (XmlNode column : root.children(COLUMN)) {
            String columnName = column.attr("name");
            if (columnName.isEmpty()) {
                context.error(ExtractionError.MISSING_NAME, ReadContext.sourceIdOf(column), COLUMN, "no name");
                continue;
            }
            String columnId = context.makeId(column, COLUMN, Identifiers.PRESENTATION, areaPath, name, columnName);
            if (columnId != null) {
                context.index.bind(column, columnId);
            }
        }
    }

    @Override
    public void read(XmlNode root, ReadContext context) {
        String tableId = context.index.identifierOf(root);
        if (tableId == null) {
            return;
        }
        String rpdId = context.rpdId;
        context.estate.presentationTables.add(new PresentationTable(
                tableId, rpdId, context.index.resolve(root.attr("containerRef")), root.attr("name"),
                root.attr("dispName"), Expressions.aliases(root)));
        context.parsed(TABLE);

        for (XmlNode column : root.children(COLUMN)) {
            String columnId = context.index.identifierOf(column);
            if (columnId == null) {
                continue;
            }
            String columnName = column.attr("name");
            String reference = column.attr("logicalColumnRef");
            String logicalColumnId = context.index.resolve(reference);
            if (!ReferenceIndex.mdsidOf(reference).isEmpty() && logicalColumnId.isEmpty()) {
                // The row is still written, with the link empty, so the column is not lost with its link.
                context.error(ExtractionError.UNRESOLVED_REFERENCE, tableId + "." + columnName, COLUMN,
                        "logical column reference does not resolve to a known logical column");
            }
            context.estate.presentationColumns.add(new PresentationColumn(
                    columnId, rpdId, tableId, columnName, logicalColumnId, column.attr("overrideLogicalName"),
                    column.attr("dispName")));
            context.parsed(COLUMN);
        }
    }
}
