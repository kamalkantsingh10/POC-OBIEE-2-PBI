package obi2pbi.core;

/** One prompt inside a report: the column it asks about and any variable it sets. */
public final class AnalysisPrompt implements Row {

    public final String rowId;
    public final String rpdId;
    public final String analysisId;
    public final int position;
    public final String promptType;
    public final String columnType;
    public final String columnFormula;
    public final String setVariable;
    public final String operator;
    public final String uiControl;

    public AnalysisPrompt(String rowId, String rpdId, String analysisId, int position, String promptType,
            String columnType, String columnFormula, String setVariable, String operator, String uiControl) {
        this.rowId = rowId;
        this.rpdId = rpdId;
        this.analysisId = analysisId;
        this.position = position;
        this.promptType = promptType;
        this.columnType = columnType;
        this.columnFormula = columnFormula;
        this.setVariable = setVariable;
        this.operator = operator;
        this.uiControl = uiControl;
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
