package obi2pbi.core;

/**
 * One view inside a report: what the report is shown as. The type is the source's own word and is never
 * translated or checked against a list.
 */
public final class AnalysisView implements Row {

    public final String rowId;
    public final String rpdId;
    public final String analysisId;
    public final int position;
    public final String viewType;
    public final String name;
    /** How many other views this view arranges by name. */
    public final int referencesViews;
    /** How many elements inside the view are bound to a column. */
    public final int boundColumnCount;
    public final String displayType;
    public final String displaySubtype;

    public AnalysisView(String rowId, String rpdId, String analysisId, int position, String viewType, String name,
            int referencesViews, int boundColumnCount, String displayType, String displaySubtype) {
        this.rowId = rowId;
        this.rpdId = rpdId;
        this.analysisId = analysisId;
        this.position = position;
        this.viewType = viewType;
        this.name = name;
        this.referencesViews = referencesViews;
        this.boundColumnCount = boundColumnCount;
        this.displayType = displayType;
        this.displaySubtype = displaySubtype;
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
