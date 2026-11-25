package obi2pbi.core;

/**
 * A column as a report author sees it. The logical column, the rename flag and the display name are kept
 * as three values, so each step from logical name to presentation name can be followed.
 */
public final class PresentationColumn implements Row {

    public final String rowId;
    public final String rpdId;
    public final String tableId;
    public final String name;
    /** Empty when the source has no link, or the link did not resolve (then the error list has a row). */
    public final String logicalColumnId;
    public final String overrideLogicalName;
    public final String displayName;

    public PresentationColumn(String rowId, String rpdId, String tableId, String name, String logicalColumnId,
            String overrideLogicalName, String displayName) {
        this.rowId = rowId;
        this.rpdId = rpdId;
        this.tableId = tableId;
        this.name = name;
        this.logicalColumnId = logicalColumnId;
        this.overrideLogicalName = overrideLogicalName;
        this.displayName = displayName;
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
