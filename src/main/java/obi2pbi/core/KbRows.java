package obi2pbi.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * One table of a Knowledge Base or Slice as read back from its folder: the header and the rows, every cell a
 * string exactly as written. Rows are in file order.
 */
public final class KbRows {

    public final String name;
    public final List<String> header;
    public final List<String[]> rows;
    private List<Map<String, String>> records;

    public KbRows(String name, List<String> header, List<String[]> rows) {
        this.name = name;
        this.header = List.copyOf(header);
        this.rows = Collections.unmodifiableList(new ArrayList<>(rows));
    }

    /** The position of a column, or -1 when the table has no such column. */
    public int column(String columnName) {
        return header.indexOf(columnName);
    }

    public boolean has(String columnName) {
        return header.contains(columnName);
    }

    /** The value of a named column in a row; empty when the table has no such column. */
    public String get(String[] row, String columnName) {
        int index = column(columnName);
        return index < 0 ? "" : row[index];
    }

    /** The rows as column-name-to-value maps, in file order. A column the table lacks reads as null. */
    public List<Map<String, String>> records() {
        if (records == null) {
            List<Map<String, String>> built = new ArrayList<>();
            for (String[] row : rows) {
                Map<String, String> record = new LinkedHashMap<>();
                for (int i = 0; i < header.size(); i++) {
                    record.put(header.get(i), row[i]);
                }
                built.add(Collections.unmodifiableMap(record));
            }
            records = Collections.unmodifiableList(built);
        }
        return records;
    }
}
