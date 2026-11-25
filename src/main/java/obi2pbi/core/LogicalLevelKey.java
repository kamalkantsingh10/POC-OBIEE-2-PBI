package obi2pbi.core;

import java.util.List;

/**
 * A key declared on a level of a logical dimension (deliberate difference 30). The three flags are the
 * source's own text: {@code true}, {@code false}, or empty when the source says nothing.
 * {@link #columns} holds Row identifiers of logical columns in source order; an entry that did not resolve
 * is empty.
 */
public final class LogicalLevelKey implements Row {

    public final String rowId;
    public final String rpdId;
    public final String levelId;
    public final String name;
    public final String isPrimary;
    public final String isChronological;
    public final String isForDrillDown;
    public final List<String> columns;

    public LogicalLevelKey(String rowId, String rpdId, String levelId, String name, String isPrimary,
            String isChronological, String isForDrillDown, List<String> columns) {
        this.rowId = rowId;
        this.rpdId = rpdId;
        this.levelId = levelId;
        this.name = name;
        this.isPrimary = isPrimary;
        this.isChronological = isChronological;
        this.isForDrillDown = isForDrillDown;
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
