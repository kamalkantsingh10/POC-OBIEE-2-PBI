package obi2pbi.core;

/**
 * What a physical table stands for in the database, exactly as written, one row per database type.
 *
 * <p>For a table of type {@code select} the text is its SQL; for any other type it is the name of the
 * database object, which need not be the name the repository shows. The table's type says which. The text is
 * never parsed.
 */
public final class PhysicalTableDefinition implements Row {

    public final String rowId;
    public final String rpdId;
    public final String tableId;
    public final String database;
    public final String text;

    public PhysicalTableDefinition(String rowId, String rpdId, String tableId, String database, String text) {
        this.rowId = rowId;
        this.rpdId = rpdId;
        this.tableId = tableId;
        this.database = database;
        this.text = text;
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
