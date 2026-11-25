package obi2pbi.core;

/**
 * A logical dimension: the drill structure of a hierarchy, in the business model.
 *
 * <p>The three flags are kept as they are and the hierarchy is never flattened: {@link #isRagged} (a branch
 * may stop above the lowest level), {@link #isSkipped} (a member may have no parent at the level directly
 * above) and {@link #isValueBased} (parent-child: the members point at their own parents).
 *
 * <p>{@link #isTime} is the source's time-dimension flag as written: {@code true}, {@code false}, or empty
 * when the source says nothing (deliberate difference 32).
 */
public final class LogicalDimension implements Row {

    public final String rowId;
    public final String rpdId;
    public final String name;
    public final String businessModel;
    public final boolean isValueBased;
    public final boolean isRagged;
    public final boolean isSkipped;
    public final String defaultRootLevelId;
    public final int levelCount;
    public final String isTime;

    public LogicalDimension(String rowId, String rpdId, String name, String businessModel, boolean
            isValueBased, boolean isRagged, boolean isSkipped, String defaultRootLevelId, int levelCount,
            String isTime) {
        this.rowId = rowId;
        this.rpdId = rpdId;
        this.name = name;
        this.businessModel = businessModel;
        this.isValueBased = isValueBased;
        this.isRagged = isRagged;
        this.isSkipped = isSkipped;
        this.defaultRootLevelId = defaultRootLevelId;
        this.levelCount = levelCount;
        this.isTime = isTime;
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
