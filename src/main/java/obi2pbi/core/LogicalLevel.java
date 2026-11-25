package obi2pbi.core;

/**
 * One level of a logical dimension.
 *
 * <p>{@link #position} is the level's place among the dimension's levels in the source, from 0. It is source
 * order, which is not always depth; the drill order is on the presentation levels.
 */
public final class LogicalLevel implements Row {

    public final String rowId;
    public final String rpdId;
    public final String dimensionId;
    public final int position;
    public final String name;
    public final String memberCount;
    public final boolean isGrandTotal;
    public final String drillKeyColumnId;
    /**
     * The levels the source lists as this level's children, and the levels that list this one as a child
     * (difference 35). Row identifiers; a child that did not resolve is an empty entry.
     */
    public final java.util.List<String> childLevelIds;
    public final java.util.List<String> parentLevelIds;

    public LogicalLevel(String rowId, String rpdId, String dimensionId, int position, String name, String
            memberCount, boolean isGrandTotal, String drillKeyColumnId, java.util.List<String> childLevelIds,
            java.util.List<String> parentLevelIds) {
        this.childLevelIds = java.util.List.copyOf(childLevelIds);
        this.parentLevelIds = java.util.List.copyOf(parentLevelIds);
        this.rowId = rowId;
        this.rpdId = rpdId;
        this.dimensionId = dimensionId;
        this.position = position;
        this.name = name;
        this.memberCount = memberCount;
        this.isGrandTotal = isGrandTotal;
        this.drillKeyColumnId = drillKeyColumnId;
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
