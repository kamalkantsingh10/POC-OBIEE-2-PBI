package obi2pbi.core;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * A Knowledge Base or a Slice held in memory: the manifest's keys and values, and the tables the manifest
 * lists. A Slice has the Knowledge Base's shape (Architecture AD-9), so one type serves both.
 */
public final class KbData {

    /** The manifest's rows, key to value, in file order. */
    public final Map<String, String> manifest = new LinkedHashMap<>();
    private final Map<String, KbRows> tables = new LinkedHashMap<>();

    public void put(KbRows table) {
        tables.put(table.name, table);
    }

    public boolean has(String table) {
        return tables.containsKey(table);
    }

    /** The named table; an empty table with no columns when the manifest does not list it. */
    public KbRows table(String name) {
        KbRows table = tables.get(name);
        return table != null ? table : new KbRows(name, List.of(), List.of());
    }

    /** The table names, in the order they were added. */
    public List<String> tableNames() {
        return new ArrayList<>(tables.keySet());
    }

    /** A manifest value; empty when the key is absent. */
    public String fact(String key) {
        return manifest.getOrDefault(key, "");
    }

    /** The estate name: the manifest's {@code rpd_ids} value. */
    public String estate() {
        return fact("rpd_ids");
    }

    /** The total number of data rows over all tables. */
    public int rowCount() {
        int total = 0;
        for (KbRows table : tables.values()) {
            total += table.rows.size();
        }
        return total;
    }
}
