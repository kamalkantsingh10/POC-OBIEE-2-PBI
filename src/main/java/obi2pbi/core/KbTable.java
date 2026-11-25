package obi2pbi.core;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * The definition of one Knowledge Base table: its name, what a row is, its columns in order, and how its
 * rows are taken from an {@link Estate}.
 *
 * <p>This is the only place a table's columns are declared. The writer takes the header from here and
 * {@code schema.csv} is written from here, so the two cannot drift apart.
 */
public final class KbTable {

    /**
     * Schema layers: the three repository layers, the repository's variables and its security, the catalog,
     * and tables about the run itself. The repository ones are the layer part of the Row identifiers.
     */
    public static final String PHYS = "phys";
    public static final String BMM = "bmm";
    public static final String PRES = "pres";
    public static final String VAR = "var";
    public static final String SEC = "sec";
    public static final String CAT = "cat";
    public static final String RUN = "run";

    public final String name;
    public final String layer;
    /** One sentence: what the table holds. */
    public final String description;
    /** One sentence: what one row stands for. */
    public final String rowMeaning;
    public final List<KbColumn> columns;
    /** False for the few tables written in a fixed order; true for all others (sorted by whole row). */
    public final boolean sorted;
    private final Function<Estate, List<String[]>> rows;

    public KbTable(String name, String layer, String description, String rowMeaning, boolean sorted,
            List<KbColumn> columns, Function<Estate, List<String[]>> rows) {
        this.name = name;
        this.layer = layer;
        this.description = description;
        this.rowMeaning = rowMeaning;
        this.sorted = sorted;
        this.columns = List.copyOf(columns);
        this.rows = rows;
    }

    public List<String> header() {
        List<String> names = new ArrayList<>();
        for (KbColumn column : columns) {
            names.add(column.name);
        }
        return names;
    }

    /** The key column's name, or empty when no single column identifies a row. */
    public String keyColumn() {
        for (KbColumn column : columns) {
            if (column.isKey) {
                return column.name;
            }
        }
        return "";
    }

    public KbColumn column(String columnName) {
        for (KbColumn column : columns) {
            if (column.name.equals(columnName)) {
                return column;
            }
        }
        return null;
    }

    /** True when the rows come from the Estate; false when the writer supplies them (the manifest). */
    public boolean hasRows() {
        return rows != null;
    }

    /** The table's rows, every cell a string, in production order. */
    public List<String[]> rows(Estate estate) {
        return rows.apply(estate);
    }
}
