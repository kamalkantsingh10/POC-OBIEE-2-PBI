package obi2pbi.core;

import java.util.List;

/**
 * A physical join. The source has two shapes, told apart by {@link #kind}: a foreign key on a table, and a
 * complex join between two tables.
 */
public final class PhysicalJoin implements Row {

    public static final String FOREIGN_KEY = "foreign_key";
    public static final String COMPLEX_JOIN = "complex_join";

    public final String rowId;
    public final String rpdId;
    public final String kind;
    public final String name;
    public final String table1Id;
    public final String table2Id;
    public final String cardinality;
    public final Expression expression;
    /**
     * Foreign key only: the Row identifier of each of the key's columns, in source order. An entry is empty
     * when its reference did not resolve, so the list keeps its length.
     */
    public final List<String> columns;
    /**
     * Foreign key only: the Row identifier of each column of the key the foreign key points at, in the
     * source order of that key, which is the order of {@link #columns} (deliberate difference 26). An entry
     * is empty when its reference did not resolve.
     */
    public final List<String> referencedColumns;

    public PhysicalJoin(String rowId, String rpdId, String kind, String name, String table1Id, String table2Id,
            String cardinality, Expression expression, List<String> columns) {
        this(rowId, rpdId, kind, name, table1Id, table2Id, cardinality, expression, columns, List.of());
    }

    public PhysicalJoin(String rowId, String rpdId, String kind, String name, String table1Id, String table2Id,
            String cardinality, Expression expression, List<String> columns, List<String> referencedColumns) {
        this.referencedColumns = List.copyOf(referencedColumns);
        this.rowId = rowId;
        this.rpdId = rpdId;
        this.kind = kind;
        this.name = name;
        this.table1Id = table1Id;
        this.table2Id = table2Id;
        this.cardinality = cardinality;
        this.expression = expression;
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
