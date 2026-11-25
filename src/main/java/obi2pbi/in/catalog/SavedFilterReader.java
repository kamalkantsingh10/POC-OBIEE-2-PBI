package obi2pbi.in.catalog;

import obi2pbi.core.AnalysisFilter;
import obi2pbi.core.SavedFilter;
import obi2pbi.in.xml.XmlNode;

/**
 * A saved filter: a filter kept as an object of its own, so that reports can share it.
 *
 * <p>Two things are written. The {@code saved_filters} row describes the object, with the operator, type
 * and own text of its outermost expression (the first expression directly inside its first filter
 * element). And every expression of the filter is written as a filter row with scope
 * {@link AnalysisFilter#SCOPE_SAVED_FILTER}, owned by the saved filter, exactly as the filter of a report
 * is (deliberate difference 9; the reference implementation keeps the outermost expression only, which is
 * nearly always a bare "and"). A reference from one saved filter to another is resolved like any other.
 *
 * <p>The object's own subject area attribute is not read, as in the reference implementation; the audit
 * reports it.
 */
final class SavedFilterReader implements CatalogObjectReader {

    @Override
    public String objectType() {
        return CatalogXml.SAVED_FILTER;
    }

    @Override
    public void read(CatalogObject object, CatalogContext context) {
        String version = object.root.attr(CatalogXml.XML_VERSION);
        XmlNode holder = object.root.firstChild(CatalogXml.FILTER);
        XmlNode outer = holder == null ? null : holder.firstChild(CatalogXml.EXPR);

        context.estate.savedFilters.add(new SavedFilter(object.rowId, context.rpdId,
                CatalogNames.nameOf(object.path), object.path, version,
                outer == null ? "" : outer.attr("op"),
                outer == null ? "" : outer.attr(CatalogXml.XSI_TYPE),
                outer == null ? "" : FilterExpressions.ownText(outer),
                holder != null && FilterExpressions.usesEval(holder)));

        FilterExpressions.read(object.root.children(CatalogXml.FILTER), object.rowId + "#f",
                AnalysisFilter.SCOPE_SAVED_FILTER, object, context);
        context.objectRead(object, version);
    }
}
