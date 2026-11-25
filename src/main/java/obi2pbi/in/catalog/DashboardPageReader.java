package obi2pbi.in.catalog;

import java.util.List;
import obi2pbi.core.AnalysisFilter;
import obi2pbi.core.DashboardContent;
import obi2pbi.core.DashboardPage;
import obi2pbi.core.PathResolution;
import obi2pbi.in.xml.XmlNode;

/**
 * A dashboard page and the references placed on it.
 *
 * <p>The four kinds of reference are taken one kind after the other, each in document order, with one
 * counter across all four. So a placement's position is "reports first, then dashboard prompts, then
 * scorecard views, then folder views"; it is not the layout order on the page.
 *
 * <p>The page's owning dashboard is filled in afterwards, from the dashboards that refer to it
 * ({@link DashboardContent#linkPagesToDashboards}); it is never taken from the folder the page sits in.
 *
 * <p>A dashboard prompt placed on a page (a global filter view) restricts every report on the page. So
 * each one is written twice: as a placement, and as a filter row with scope
 * {@link AnalysisFilter#SCOPE_DASHBOARD}, owned by the page and numbered among the page's global filter
 * views in source order.
 *
 * <p>Deliberate difference 11: each reference is resolved once, and the filter row takes its outcome from
 * the placement. The reference implementation resolves a global filter view a second time for the filter
 * row, which doubles its error rows.
 */
final class DashboardPageReader implements CatalogObjectReader {

    /** The referring elements, in the order their placements are numbered. */
    private static final List<String> PLACED = List.of(CatalogXml.REPORT_REF, CatalogXml.GLOBAL_FILTER_VIEW,
            CatalogXml.SCORECARD_VIEW_REF, CatalogXml.FOLDER_VIEW);

    @Override
    public String objectType() {
        return CatalogXml.DASHBOARD_PAGE;
    }

    @Override
    public void read(CatalogObject object, CatalogContext context) {
        String version = object.root.attr(CatalogXml.XML_VERSION);

        int position = 0;
        for (String element : PLACED) {
            String expected = element.equals(CatalogXml.FOLDER_VIEW) ? PathResolution.FOLDER : PathResolution.OBJECT;
            int order = 0;
            for (XmlNode placed : object.root.below(element)) {
                String reference = placed.attr(CatalogXml.PATH);
                PathResolution resolved = context.resolve(reference, object, element, expected);
                context.estate.dashboardContents.add(new DashboardContent(object.rowId + "#item" + position,
                        context.rpdId, object.rowId, element, reference, position, resolved));
                if (element.equals(CatalogXml.GLOBAL_FILTER_VIEW)) {
                    // The outcome just obtained, never a second resolve (difference 11).
                    context.estate.analysisFilters.add(new AnalysisFilter(object.rowId + "#gfilter" + order,
                            context.rpdId, AnalysisFilter.SCOPE_DASHBOARD, object.rowId, order, "", "", "",
                            reference, resolved.targetId, resolved.outcome, false, "", 0));
                }
                position++;
                order++;
            }
        }

        context.estate.dashboardPages.add(new DashboardPage(object.rowId, context.rpdId,
                CatalogNames.nameOf(object.path), object.path, version, "", 0));
        context.objectRead(object, version);
    }
}
