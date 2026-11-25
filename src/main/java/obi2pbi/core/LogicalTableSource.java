package obi2pbi.core;

/**
 * One way a logical table can be answered from physical tables.
 *
 * <p>{@link #priority} is empty when the source records none. Empty means not recorded, never lowest.
 */
public final class LogicalTableSource implements Row {

    public final String rowId;
    public final String rpdId;
    public final String logicalTableId;
    public final String name;
    public final String isActive;
    public final String priority;
    /** Which slice of the data this source holds, when the table is split over several sources. */
    public final Expression fragment;
    /** The source's content filter. */
    public final Expression whereClause;
    /** The grain the source is aggregated to. */
    public final Expression groupBy;

    /**
     * For a source of a parent-child hierarchy, the physical table that holds the parent-child
     * relationships (difference 36); empty otherwise.
     */
    public final String closureTableId;

    public LogicalTableSource(String rowId, String rpdId, String logicalTableId, String name, String isActive,
            String priority, Expression fragment, Expression whereClause, Expression groupBy) {
        this(rowId, rpdId, logicalTableId, name, isActive, priority, fragment, whereClause, groupBy, "");
    }

    public LogicalTableSource(String rowId, String rpdId, String logicalTableId, String name, String isActive,
            String priority, Expression fragment, Expression whereClause, Expression groupBy, String closureTableId) {
        this.closureTableId = closureTableId;
        this.rowId = rowId;
        this.rpdId = rpdId;
        this.logicalTableId = logicalTableId;
        this.name = name;
        this.isActive = isActive;
        this.priority = priority;
        this.fragment = fragment;
        this.whereClause = whereClause;
        this.groupBy = groupBy;
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
