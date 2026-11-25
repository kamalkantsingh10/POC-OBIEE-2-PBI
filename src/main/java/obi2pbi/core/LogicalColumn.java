package obi2pbi.core;

import java.util.List;

/**
 * A column of a logical table.
 *
 * <p>{@link #derived} is the column's own formula, present on few columns. {@link #mapping} is the
 * definition the source keeps on the column itself, present on most. Both are kept: they answer different
 * questions.
 */
public final class LogicalColumn implements Row {

    public final String rowId;
    public final String rpdId;
    public final String tableId;
    public final String name;
    public final String isWriteable;
    public final boolean isMeasure;
    /** The time-series functions the column's two expressions use, in the order of {@link TimeSeries#FUNCTIONS}. */
    public final List<String> timeSeries;
    /** Sources declared on the owning table. */
    public final int sourceCount;
    /** Sources that map this column. Never more than {@link #sourceCount}; a gap means a source cannot supply it. */
    public final int mappedSourceCount;
    public final Expression derived;
    public final Expression mapping;

    /**
     * The logical column that holds this column's identifier, when the source names one (difference 37):
     * this column is then the text shown for that identifier. Empty otherwise.
     */
    public final String descriptorIdColumnId;

    public LogicalColumn(String rowId, String rpdId, String tableId, String name, String isWriteable,
            boolean isMeasure, List<String> timeSeries, int sourceCount, int mappedSourceCount,
            Expression derived, Expression mapping) {
        this(rowId, rpdId, tableId, name, isWriteable, isMeasure, timeSeries, sourceCount, mappedSourceCount,
                derived, mapping, "");
    }

    public LogicalColumn(String rowId, String rpdId, String tableId, String name, String isWriteable,
            boolean isMeasure, List<String> timeSeries, int sourceCount, int mappedSourceCount,
            Expression derived, Expression mapping, String descriptorIdColumnId) {
        this.descriptorIdColumnId = descriptorIdColumnId;
        this.rowId = rowId;
        this.rpdId = rpdId;
        this.tableId = tableId;
        this.name = name;
        this.isWriteable = isWriteable;
        this.isMeasure = isMeasure;
        this.timeSeries = List.copyOf(timeSeries);
        this.sourceCount = sourceCount;
        this.mappedSourceCount = mappedSourceCount;
        this.derived = derived;
        this.mapping = mapping;
    }

    /** The same column with another count of sources that map it. */
    public LogicalColumn withMappedSourceCount(int count) {
        return new LogicalColumn(rowId, rpdId, tableId, name, isWriteable, isMeasure, timeSeries, sourceCount,
                count, derived, mapping, descriptorIdColumnId);
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
