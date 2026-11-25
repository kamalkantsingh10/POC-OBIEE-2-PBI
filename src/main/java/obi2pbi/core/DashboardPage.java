package obi2pbi.core;

/**
 * One dashboard page.
 *
 * <p>{@code dashboardId} is empty for a page that no dashboard refers to. That is a real state, and the
 * owning dashboard is never guessed from the folder the page sits in. {@code position} is then 0 and does
 * not mean "first page".
 */
public final class DashboardPage implements Row {

    public final String rowId;
    public final String rpdId;
    public final String name;
    public final String catalogPath;
    public final String formatVersion;
    public final String dashboardId;
    /** The page's place among its dashboard's page references, counting from 0. */
    public final int position;

    public DashboardPage(String rowId, String rpdId, String name, String catalogPath, String formatVersion,
            String dashboardId, int position) {
        this.rowId = rowId;
        this.rpdId = rpdId;
        this.name = name;
        this.catalogPath = catalogPath;
        this.formatVersion = formatVersion;
        this.dashboardId = dashboardId;
        this.position = position;
    }

    /** The same page, owned by a dashboard. */
    public DashboardPage ownedBy(String dashboard, int place) {
        return new DashboardPage(rowId, rpdId, name, catalogPath, formatVersion, dashboard, place);
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
