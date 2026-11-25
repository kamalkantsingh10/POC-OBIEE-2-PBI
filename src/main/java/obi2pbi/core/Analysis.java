package obi2pbi.core;

/**
 * One report. OBIEE calls a report an analysis; the tables keep that word.
 *
 * <p>A Visual Analyzer project is stored as a report too (its file is called {@code _projectdefn}); it is
 * read as far as it has the shape of a report, and its own formulas are not read.
 */
public final class Analysis implements Row {

    public final String rowId;
    public final String rpdId;
    /** The last part of the catalog path. */
    public final String name;
    public final String catalogPath;
    /** The version the object declares, as written; empty when it declares none. */
    public final String formatVersion;
    /** How many criteria blocks the report holds, nested ones included. */
    public final int criteriaCount;
    /** How many columns were captured across all its criteria blocks. */
    public final int columnCount;

    public Analysis(String rowId, String rpdId, String name, String catalogPath, String formatVersion,
            int criteriaCount, int columnCount) {
        this.rowId = rowId;
        this.rpdId = rpdId;
        this.name = name;
        this.catalogPath = catalogPath;
        this.formatVersion = formatVersion;
        this.criteriaCount = criteriaCount;
        this.columnCount = columnCount;
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
