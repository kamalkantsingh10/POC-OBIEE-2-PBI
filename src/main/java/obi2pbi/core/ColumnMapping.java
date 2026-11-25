package obi2pbi.core;

/**
 * One logical column mapped through one table source.
 *
 * <p>{@link #logicalColumnId} and {@link #physicalColumnId} are the first resolved reference of the two
 * expressions. A mapping that uses several physical columns therefore names only the first here; all of
 * them are in {@link #physicalExpression}.
 */
public final class ColumnMapping implements Row {

    public final String rowId;
    public final String rpdId;
    public final String sourceId;
    public final String logicalColumnId;
    public final String physicalColumnId;
    public final Expression logicalExpression;
    public final Expression physicalExpression;

    public ColumnMapping(String rowId, String rpdId, String sourceId, String logicalColumnId,
            String physicalColumnId, Expression logicalExpression, Expression physicalExpression) {
        this.rowId = rowId;
        this.rpdId = rpdId;
        this.sourceId = sourceId;
        this.logicalColumnId = logicalColumnId;
        this.physicalColumnId = physicalColumnId;
        this.logicalExpression = logicalExpression;
        this.physicalExpression = physicalExpression;
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
