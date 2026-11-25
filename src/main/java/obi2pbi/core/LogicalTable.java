package obi2pbi.core;

/** A table of a business model. */
public final class LogicalTable implements Row {

    public final String rowId;
    public final String rpdId;
    public final String businessModel;
    public final String name;
    /** How many table sources the table declares. */
    public final int sourceCount;

    /** The source's lookup-table flag as written: {@code true}, {@code false} or empty when absent (difference 37). */
    public final String isLookupTable;

    public LogicalTable(String rowId, String rpdId, String businessModel, String name, int sourceCount) {
        this(rowId, rpdId, businessModel, name, sourceCount, "");
    }

    public LogicalTable(String rowId, String rpdId, String businessModel, String name, int sourceCount,
            String isLookupTable) {
        this.isLookupTable = isLookupTable;
        this.rowId = rowId;
        this.rpdId = rpdId;
        this.businessModel = businessModel;
        this.name = name;
        this.sourceCount = sourceCount;
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
