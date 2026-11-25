package obi2pbi.core;

/**
 * One column of a criteria block, with its formula exactly as written.
 *
 * <p>{@code formula} is the whole text of the formula expression: nested expressions are part of it
 * (deliberate difference 8).
 */
public final class AnalysisColumn implements Row {

    public final String rowId;
    public final String rpdId;
    public final String analysisId;
    public final String criteriaId;
    /** The column's number within its criteria block, counting from 0. */
    public final int position;
    /** The identifier the report gives the column; views refer to columns by it. */
    public final String columnId;
    public final String heading;
    public final String tableHeading;
    /** The full name of the element that carried the formula; empty when the column has none. */
    public final String formulaConstruct;
    public final String formulaType;
    public final String formula;
    /** True when the column element is typed as something other than a regular column. */
    public final boolean isComputation;
    /** True when a formula of the column calls a database function directly. */
    public final boolean usesEval;

    public AnalysisColumn(String rowId, String rpdId, String analysisId, String criteriaId, int position, String columnId,
            String heading, String tableHeading, String formulaConstruct, String formulaType,
            String formula, boolean isComputation, boolean usesEval) {
        this.rowId = rowId;
        this.rpdId = rpdId;
        this.analysisId = analysisId;
        this.criteriaId = criteriaId;
        this.position = position;
        this.columnId = columnId;
        this.heading = heading;
        this.tableHeading = tableHeading;
        this.formulaConstruct = formulaConstruct;
        this.formulaType = formulaType;
        this.formula = formula;
        this.isComputation = isComputation;
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
