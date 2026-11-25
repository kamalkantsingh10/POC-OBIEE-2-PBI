package obi2pbi.in.mds;

import obi2pbi.core.ExtractionError;
import obi2pbi.core.Identifiers;
import obi2pbi.core.SubjectArea;
import obi2pbi.in.xml.XmlNode;

/**
 * Reads {@code PresentationCatalog} files. Each is a subject area; the source's type name has nothing to do
 * with the Web Catalog.
 *
 * <p>Not read, and therefore reported in {@code unparsed.csv}: the order of the subject area's tables.
 */
public final class PresentationCatalogReader implements ObjectReader {

    private static final String SUBJECT_AREA = "PresentationCatalog";

    @Override
    public String objectType() {
        return SUBJECT_AREA;
    }

    @Override
    public void bind(XmlNode root, ReadContext context) {
        String name = root.attr("name");
        if (name.isEmpty()) {
            context.error(ExtractionError.MISSING_NAME, ReadContext.sourceIdOf(root), SUBJECT_AREA, "no name");
            return;
        }
        String rowId = context.makeId(root, SUBJECT_AREA, Identifiers.PRESENTATION, name);
        if (rowId != null) {
            context.index.bind(root, rowId);
        }
    }

    @Override
    public void read(XmlNode root, ReadContext context) {
        String rowId = context.index.identifierOf(root);
        if (rowId == null) {
            return;
        }
        // Neither link is an error when it is absent or does not resolve: the value is then empty.
        ReferenceIndex.Entry model = context.index.entry(ReferenceIndex.mdsidOf(root.attr("subjectAreaRef")));
        context.estate.subjectAreas.add(new SubjectArea(
                rowId, context.rpdId, root.attr("name"), model == null ? "" : model.name,
                context.index.resolve(root.attr("defaultFactColumnRef")), root.attr("isAutoAggr"),
                Expressions.aliases(root)));
        context.parsed(SUBJECT_AREA);
    }
}
