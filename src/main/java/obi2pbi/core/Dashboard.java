package obi2pbi.core;

/** A dashboard: the catalog object that lists its pages in order. */
public final class Dashboard implements Row {

    public final String rowId;
    public final String rpdId;
    /** The name of the folder that holds the dashboard's layout object; that folder is the dashboard. */
    public final String name;
    /** The decoded catalog path of the layout object, relative to the catalog root. */
    public final String catalogPath;
    /** The version the object declares, as written; empty when it declares none. */
    public final String formatVersion;
    /** How many page references the dashboard holds, those that did not resolve included. */
    public final int pageCount;

    public Dashboard(String rowId, String rpdId, String name, String catalogPath, String formatVersion,
            int pageCount) {
        this.rowId = rowId;
        this.rpdId = rpdId;
        this.name = name;
        this.catalogPath = catalogPath;
        this.formatVersion = formatVersion;
        this.pageCount = pageCount;
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
