package obi2pbi.in.catalog;

import java.util.List;
import obi2pbi.core.Dashboard;
import obi2pbi.core.DashboardContent;
import obi2pbi.core.PathResolution;
import obi2pbi.in.xml.XmlNode;

/**
 * A dashboard: the layout object that names its pages, in order.
 *
 * <p>The dashboard's name is the folder it sits in, not its file name: a dashboard is a file called
 * {@code dashboard layout} inside {@code _portal/<dashboard name>/}. The identifier uses the whole path.
 */
final class DashboardReader implements CatalogObjectReader {

    @Override
    public String objectType() {
        return CatalogXml.DASHBOARD;
    }

    @Override
    public void read(CatalogObject object, CatalogContext context) {
        String folder = CatalogNames.folderOf(object.path);
        String name = CatalogNames.nameOf(folder.isEmpty() ? object.path : folder);
        String version = object.root.attr(CatalogXml.XML_VERSION);

        List<XmlNode> pageRefs = object.root.below(CatalogXml.DASHBOARD_PAGE_REF);
        for (int position = 0; position < pageRefs.size(); position++) {
            String reference = pageRefs.get(position).attr(CatalogXml.PATH);
            PathResolution resolved = context.resolve(reference, object, CatalogXml.DASHBOARD_PAGE_REF);
            context.estate.dashboardContents.add(new DashboardContent(object.rowId + "#page" + position,
                    context.rpdId, object.rowId, CatalogXml.DASHBOARD_PAGE_REF, reference, position, resolved));
        }

        context.estate.dashboards.add(new Dashboard(object.rowId, context.rpdId, name, object.path, version,
                pageRefs.size()));
        context.objectRead(object, version);
    }
}
