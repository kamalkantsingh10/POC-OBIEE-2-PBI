package obi2pbi.core;

/**
 * One join a logical table source makes between two of its physical tables, as the source lists it under
 * the table it starts from (deliberate difference 34). The source lists a join once under each of its two
 * tables, so a join between A and B is usually two rows: one seen from A and one seen from B.
 *
 * <p>{@link #joinType} and {@link #cardinality} are the source's own text, nothing trimmed. The condition
 * is not on the join: it is that of the physical join {@link #physicalJoinId} points at.
 */
public final class SourceJoin implements Row {

    public final String rowId;
    public final String rpdId;
    public final String sourceId;
    public final String sourceTableId;
    public final int position;
    public final String joinType;
    public final String cardinality;
    public final String table1Id;
    public final String table2Id;
    public final String physicalJoinId;

    public SourceJoin(String rowId, String rpdId, String sourceId, String sourceTableId, int position,
            String joinType, String cardinality, String table1Id, String table2Id, String physicalJoinId) {
        this.rowId = rowId;
        this.rpdId = rpdId;
        this.sourceId = sourceId;
        this.sourceTableId = sourceTableId;
        this.position = position;
        this.joinType = joinType;
        this.cardinality = cardinality;
        this.table1Id = table1Id;
        this.table2Id = table2Id;
        this.physicalJoinId = physicalJoinId;
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
