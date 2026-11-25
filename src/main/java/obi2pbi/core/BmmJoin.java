package obi2pbi.core;

/**
 * A join between two logical tables of a business model.
 *
 * <p>{@link #joinType} is the source's own word (observed: {@code Inner}, {@code LeftOuter}, {@code RightOuter}).
 * The side of an outer join is relative to table 1 and table 2 as the source wrote them. An inner join drops
 * rows that have no partner, so it can change a number without changing any formula.
 */
public final class BmmJoin implements Row {

    public final String rowId;
    public final String rpdId;
    public final String name;
    public final String joinType;
    public final String table1Id;
    public final String multiplicity1;
    public final String isAggregate1;
    public final String table2Id;
    public final String multiplicity2;
    public final String isAggregate2;

    public BmmJoin(String rowId, String rpdId, String name, String joinType, String table1Id, String
            multiplicity1, String isAggregate1, String table2Id, String multiplicity2, String isAggregate2) {
        this.rowId = rowId;
        this.rpdId = rpdId;
        this.name = name;
        this.joinType = joinType;
        this.table1Id = table1Id;
        this.multiplicity1 = multiplicity1;
        this.isAggregate1 = isAggregate1;
        this.table2Id = table2Id;
        this.multiplicity2 = multiplicity2;
        this.isAggregate2 = isAggregate2;
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
