package obi2pbi.core;

import java.util.List;

/**
 * A key declared on a logical table (deliberate difference 29). {@link #isPrimary} is the source's own text:
 * {@code true}, {@code false}, or empty when the source says nothing. {@link #columns} holds Row identifiers
 * of logical columns in source order; an entry that did not resolve is empty.
 */
public final class LogicalKey implements Row {

    public final String rowId;
    public final String rpdId;
    public final String logicalTableId;
    public final String name;
    public final String isPrimary;
    public final List<String> columns;

    public LogicalKey(String rowId, String rpdId, String logicalTableId, String name, String isPrimary,
            List<String> columns) {
        this.rowId = rowId;
        this.rpdId = rpdId;
        this.logicalTableId = logicalTableId;
        this.name = name;
        this.isPrimary = isPrimary;
        this.columns = List.copyOf(columns);
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
