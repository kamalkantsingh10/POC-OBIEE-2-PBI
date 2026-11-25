package obi2pbi.core;

/**
 * One column of a Knowledge Base table: its name, what it means, and what it points at.
 *
 * <p>Immutable. Start with {@link #of} or {@link #key} and add to it:
 * {@code KbColumn.of("table_id", "The table this column belongs to.").references("physical_tables", "row_id")}.
 */
public final class KbColumn {

    public final String name;
    /** One plain sentence. Never empty. */
    public final String meaning;
    public final boolean isKey;
    /** The table this column's values are found in, or empty. */
    public final String referencesTable;
    public final String referencesColumn;
    /** What a reader must know before joining or parsing this column, or empty. */
    public final String note;

    private KbColumn(String name, String meaning, boolean isKey, String referencesTable,
            String referencesColumn, String note) {
        this.name = name;
        this.meaning = meaning;
        this.isKey = isKey;
        this.referencesTable = referencesTable;
        this.referencesColumn = referencesColumn;
        this.note = note;
    }

    public static KbColumn of(String name, String meaning) {
        return new KbColumn(name, meaning, false, "", "", "");
    }

    /** The column that identifies a row of its table. */
    public static KbColumn key(String name, String meaning) {
        return new KbColumn(name, meaning, true, "", "", "");
    }

    /** Every non-empty value of this column is a value of {@code table.column}. */
    public KbColumn references(String table, String column) {
        return new KbColumn(name, meaning, isKey, table, column, note);
    }

    public KbColumn note(String text) {
        return new KbColumn(name, meaning, isKey, referencesTable, referencesColumn, text);
    }
}
