package obi2pbi.core;

/** A table the repository knows in a database. */
public final class PhysicalTable implements Row {

    public final String rowId;
    public final String rpdId;
    /** The containers above the table, outermost first, joined with a dot: usually database and schema. */
    public final String container;
    public final String name;
    /** As the source gives it. {@code select} means the table is an SQL statement, not a database table. */
    public final String tableType;
    public final String maxConn;
    /**
     * The table this one is an alias of. Empty means the source records no such link. A link that is present
     * and does not resolve also leaves this empty, and has a row in the error list.
     */
    public final String sourceTableId;

    /** The table's description in the repository, exactly as written; empty when it has none (difference 37). */
    public final String description;
    /**
     * For the relationship table of a parent-child hierarchy, the four columns that define it (difference
     * 36): the member, its ancestor, how many steps apart the two are, and whether the member is a leaf.
     * Row identifiers of physical columns; empty on every other table.
     */
    public final String memberKeyColumnId;
    public final String parentKeyColumnId;
    public final String distanceColumnId;
    public final String leafColumnId;

    public PhysicalTable(String rowId, String rpdId, String container, String name, String tableType,
            String maxConn, String sourceTableId) {
        this(rowId, rpdId, container, name, tableType, maxConn, sourceTableId, "", "", "", "", "");
    }

    public PhysicalTable(String rowId, String rpdId, String container, String name, String tableType,
            String maxConn, String sourceTableId, String description, String memberKeyColumnId,
            String parentKeyColumnId, String distanceColumnId, String leafColumnId) {
        this.description = description;
        this.memberKeyColumnId = memberKeyColumnId;
        this.parentKeyColumnId = parentKeyColumnId;
        this.distanceColumnId = distanceColumnId;
        this.leafColumnId = leafColumnId;
        this.rowId = rowId;
        this.rpdId = rpdId;
        this.container = container;
        this.name = name;
        this.tableType = tableType;
        this.maxConn = maxConn;
        this.sourceTableId = sourceTableId;
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
