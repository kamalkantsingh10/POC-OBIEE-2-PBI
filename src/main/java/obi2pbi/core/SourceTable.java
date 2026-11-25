package obi2pbi.core;

/**
 * One physical table a logical table source is built on, as the source itself lists it (deliberate
 * difference 34). {@link #position} is the table's place in that list, from 0.
 */
public final class SourceTable implements Row {

    public final String rowId;
    public final String rpdId;
    public final String sourceId;
    public final int position;
    public final String physicalTableId;

    public SourceTable(String rowId, String rpdId, String sourceId, int position, String physicalTableId) {
        this.rowId = rowId;
        this.rpdId = rpdId;
        this.sourceId = sourceId;
        this.position = position;
        this.physicalTableId = physicalTableId;
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
