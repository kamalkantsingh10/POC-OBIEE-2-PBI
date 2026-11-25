package obi2pbi.core;

/**
 * A hierarchy as a report author sees it, on a presentation table.
 *
 * <p>A parent-child hierarchy has no levels; its one column is in {@link #displayColumnId}.
 */
public final class PresentationHierarchy implements Row {

    public final String rowId;
    public final String rpdId;
    public final String tableId;
    public final String name;
    public final String dimensionId;
    public final int levelCount;
    public final String displayColumnId;

    public PresentationHierarchy(String rowId, String rpdId, String tableId, String name, String dimensionId,
            int levelCount, String displayColumnId) {
        this.rowId = rowId;
        this.rpdId = rpdId;
        this.tableId = tableId;
        this.name = name;
        this.dimensionId = dimensionId;
        this.levelCount = levelCount;
        this.displayColumnId = displayColumnId;
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
