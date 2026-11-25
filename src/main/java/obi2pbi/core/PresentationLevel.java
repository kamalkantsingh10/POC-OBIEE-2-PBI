package obi2pbi.core;

/**
 * One level of a presentation hierarchy. {@link #position} counts from 0 and is the drill order, top first.
 */
public final class PresentationLevel implements Row {

    public final String rowId;
    public final String rpdId;
    public final String hierarchyId;
    public final int position;
    public final String name;
    public final String logicalLevelId;

    public PresentationLevel(String rowId, String rpdId, String hierarchyId, int position, String name, String
            logicalLevelId) {
        this.rowId = rowId;
        this.rpdId = rpdId;
        this.hierarchyId = hierarchyId;
        this.position = position;
        this.name = name;
        this.logicalLevelId = logicalLevelId;
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
