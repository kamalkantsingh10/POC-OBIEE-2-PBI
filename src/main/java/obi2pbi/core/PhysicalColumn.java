package obi2pbi.core;

/**
 * A column of a physical table. Every value is the source's own text; empty means the source had none.
 *
 * <p>{@link #position} is the column's place among the table's columns in the source, counting from 1
 * (deliberate difference 31). A column left out for having no name still takes its place, so numbers can
 * have gaps.
 */
public final class PhysicalColumn implements Row {

    public final String rowId;
    public final String rpdId;
    public final String tableId;
    public final String name;
    public final String dataType;
    public final String specialType;
    public final String precision;
    public final String nullable;
    public final String externalName;
    public final String scale;
    public final int position;
    /** For a column of an alias table, the column it stands for (difference 37); empty otherwise. */
    public final String sourceColumnId;

    public PhysicalColumn(String rowId, String rpdId, String tableId, String name, String dataType,
            String specialType, String precision, String nullable, String externalName, String scale,
            int position, String sourceColumnId) {
        this.sourceColumnId = sourceColumnId;
        this.rowId = rowId;
        this.rpdId = rpdId;
        this.tableId = tableId;
        this.name = name;
        this.dataType = dataType;
        this.specialType = specialType;
        this.precision = precision;
        this.nullable = nullable;
        this.externalName = externalName;
        this.scale = scale;
        this.position = position;
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
