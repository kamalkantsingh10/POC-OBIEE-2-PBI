package obi2pbi.core;

/**
 * One aggregation rule of a measure. A measure can have several: one default and others that replace it for
 * particular dimensions. They are never merged into one.
 */
public final class AggregationRule implements Row {

    public final String rowId;
    public final String rpdId;
    public final String logicalColumnId;
    public final String name;
    /** As the source gives it: {@code true} for the default rule. */
    public final String isDefault;
    public final Expression expression;

    public AggregationRule(String rowId, String rpdId, String logicalColumnId, String name, String isDefault,
            Expression expression) {
        this.rowId = rowId;
        this.rpdId = rpdId;
        this.logicalColumnId = logicalColumnId;
        this.name = name;
        this.isDefault = isDefault;
        this.expression = expression;
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
