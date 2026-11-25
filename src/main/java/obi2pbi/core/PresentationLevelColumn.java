package obi2pbi.core;

/**
 * One presentation column shown at a level of a presentation hierarchy, with its place among them from 0.
 */
public final class PresentationLevelColumn implements Row {

    public final String rowId;
    public final String rpdId;
    public final String levelId;
    public final int position;
    public final String presentationColumnId;

    public PresentationLevelColumn(String rowId, String rpdId, String levelId, int position, String
            presentationColumnId) {
        this.rowId = rowId;
        this.rpdId = rpdId;
        this.levelId = levelId;
        this.position = position;
        this.presentationColumnId = presentationColumnId;
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
