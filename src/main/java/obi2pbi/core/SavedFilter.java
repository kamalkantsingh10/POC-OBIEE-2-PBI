package obi2pbi.core;

/**
 * One saved filter object. The row describes the object and its outermost expression; every expression
 * of the filter is an {@link AnalysisFilter} row with scope {@link AnalysisFilter#SCOPE_SAVED_FILTER}.
 */
public final class SavedFilter implements Row {

    public final String rowId;
    public final String rpdId;
    public final String name;
    public final String catalogPath;
    public final String formatVersion;
    public final String op;
    public final String exprType;
    public final String exprText;
    public final boolean usesEval;

    public SavedFilter(String rowId, String rpdId, String name, String catalogPath, String formatVersion, String op,
            String exprType, String exprText, boolean usesEval) {
        this.rowId = rowId;
        this.rpdId = rpdId;
        this.name = name;
        this.catalogPath = catalogPath;
        this.formatVersion = formatVersion;
        this.op = op;
        this.exprType = exprType;
        this.exprText = exprText;
        this.usesEval = usesEval;
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
