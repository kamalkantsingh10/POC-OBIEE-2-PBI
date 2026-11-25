package obi2pbi.in.kb;

import java.util.List;

/** A CSV file read back: its header and its rows, every cell a string exactly as written. */
public final class CsvTable {

    public final List<String> header;
    public final List<String[]> rows;

    public CsvTable(List<String> header, List<String[]> rows) {
        this.header = List.copyOf(header);
        this.rows = rows;
    }

    /** The position of a column, or -1. Look columns up by name, never by a fixed position. */
    public int indexOf(String column) {
        return header.indexOf(column);
    }

    /** The value of a named column in a row. */
    public String value(String[] row, String column) {
        int index = indexOf(column);
        if (index < 0) {
            throw new IllegalArgumentException("no column '" + column + "' in " + header);
        }
        return row[index];
    }
}
