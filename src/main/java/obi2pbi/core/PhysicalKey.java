package obi2pbi.core;

import java.util.List;

/**
 * A key declared on a physical table: a name and the columns it is made of (deliberate difference 28).
 *
 * <p>The source has no flag that says whether a physical key is the table's primary key, so none is kept.
 * {@link #columns} holds Row identifiers of physical columns in source order; an entry that did not resolve
 * is empty.
 */
public final class PhysicalKey implements Row {

    public final String rowId;
    public final String rpdId;
    public final String tableId;
    public final String name;
    public final List<String> columns;

    public PhysicalKey(String rowId, String rpdId, String tableId, String name, List<String> columns) {
        this.rowId = rowId;
        this.rpdId = rpdId;
        this.tableId = tableId;
        this.name = name;
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
