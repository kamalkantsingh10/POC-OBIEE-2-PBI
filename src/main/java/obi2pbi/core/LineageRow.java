package obi2pbi.core;

/**
 * One path from a presentation column to a database column: presentation column, logical column, table
 * source, physical table, physical column. A logical column mapped by three sources gives three rows.
 */
public final class LineageRow implements Row {

    public final String rowId;
    public final String rpdId;
    public final String presentationColumnId;
    public final String logicalColumnId;
    public final String sourceId;
    public final String physicalTableId;
    public final String physicalColumnId;

    public LineageRow(String rowId, String rpdId, String presentationColumnId, String logicalColumnId,
            String sourceId, String physicalTableId, String physicalColumnId) {
        this.rowId = rowId;
        this.rpdId = rpdId;
        this.presentationColumnId = presentationColumnId;
        this.logicalColumnId = logicalColumnId;
        this.sourceId = sourceId;
        this.physicalTableId = physicalTableId;
        this.physicalColumnId = physicalColumnId;
    }

    @Override
    public String rowId() {
        return rowId;
    }

    @Override
    public String rpdId() {
        return rpdId;
    }
}
