package obi2pbi.core;

/**
 * An initialization block: the query that fills one or more variables. Its text is kept exactly as written.
 */
public final class InitBlock implements Row {

    public final String rowId;
    public final String rpdId;
    public final String name;
    public final String refreshPeriod;
    public final String isSession;
    public final String isEnabled;
    public final String initString;

    public InitBlock(String rowId, String rpdId, String name, String refreshPeriod, String isSession, String
            isEnabled, String initString) {
        this.rowId = rowId;
        this.rpdId = rpdId;
        this.name = name;
        this.refreshPeriod = refreshPeriod;
        this.isSession = isSession;
        this.isEnabled = isEnabled;
        this.initString = initString;
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
