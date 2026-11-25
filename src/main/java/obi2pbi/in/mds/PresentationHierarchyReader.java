package obi2pbi.in.mds;

import java.util.List;
import obi2pbi.core.ExtractionError;
import obi2pbi.core.Identifiers;
import obi2pbi.core.PresentationHierarchy;
import obi2pbi.core.PresentationLevel;
import obi2pbi.core.PresentationLevelColumn;
import obi2pbi.in.xml.XmlNode;

/**
 * Reads {@code PresentationHierarchy} files: a hierarchy as a report author sees it, its levels in drill
 * order, and the columns shown at each level.
 *
 * <p>A parent-child hierarchy has no levels; its one column sits on the hierarchy itself.
 *
 * <p>Not read, and therefore reported in {@code unparsed.csv}: aliases and the display-name flags.
 */
public final class PresentationHierarchyReader implements ObjectReader {

    private static final String HIERARCHY = "PresentationHierarchy";
    private static final String LEVEL = "PresentationLevel";
    private static final String COLUMN_REF = "RefPresentationColumn";

    @Override
    public String objectType() {
        return HIERARCHY;
    }

    @Override
    public void bind(XmlNode root, ReadContext context) {
        String name = root.attr("name");
        // The presentation table was bound by its own reader, which is registered before this one.
        String tableId = context.index.resolve(root.attr("containerRef"));
        if (name.isEmpty() || tableId.isEmpty()) {
            context.error(ExtractionError.UNRESOLVED_REFERENCE, ReadContext.sourceIdOf(root), HIERARCHY,
                    "hierarchy name or owning presentation table could not be resolved");
            return;
        }
        String tablePath = Identifiers.parseId(tableId)[2];
        String hierarchyId = context.makeId(root, HIERARCHY, Identifiers.PRESENTATION, tablePath, name);
        if (hierarchyId == null) {
            return;
        }
        context.index.bind(root, hierarchyId);

        for (XmlNode level : root.children(LEVEL)) {
            String levelName = level.attr("name");
            if (levelName.isEmpty()) {
                context.error(ExtractionError.MISSING_NAME, ReadContext.sourceIdOf(level), LEVEL, "no name");
                continue;
            }
            String levelId = context.makeId(level, LEVEL, Identifiers.PRESENTATION, tablePath, name, levelName);
            if (levelId != null) {
                context.index.bind(level, levelId);
            }
        }
    }

    @Override
    public void read(XmlNode root, ReadContext context) {
        String hierarchyId = context.index.identifierOf(root);
        if (hierarchyId == null) {
            return;
        }
        String rpdId = context.rpdId;

        List<XmlNode> levels = root.children(LEVEL);
        for (int position = 0; position < levels.size(); position++) {
            XmlNode level = levels.get(position);
            String levelId = context.index.identifierOf(level);
            if (levelId == null) {
                // Not named in the first phase: the level and its columns are left out, its position kept.
                continue;
            }
            context.estate.presentationLevels.add(new PresentationLevel(
                    levelId, rpdId, hierarchyId, position, level.attr("name"),
                    context.index.resolve(level.attr("logicalLevelRef"))));
            context.parsed(LEVEL);

            // The columns shown at this level, in order. They sit one wrapper below the level.
            int place = 0;
            for (XmlNode ref : level.descendants(COLUMN_REF)) {
                String columnId = context.index.resolve(ref.attr("presentationColumnRef"));
                if (columnId.isEmpty()) {
                    // It still takes its place number, so the numbers can have gaps.
                    context.error(ExtractionError.UNRESOLVED_REFERENCE, levelId, COLUMN_REF,
                            "display column reference does not resolve to a known column");
                } else {
                    context.estate.presentationLevelColumns.add(new PresentationLevelColumn(
                            levelId + "." + place, rpdId, levelId, place, columnId));
                }
                place++;
            }
        }

        context.estate.presentationHierarchies.add(new PresentationHierarchy(
                hierarchyId, rpdId, context.index.resolve(root.attr("containerRef")), root.attr("name"),
                context.index.resolve(root.attr("logicalDimensionRef")), levels.size(),
                ownDisplayColumn(root, context)));
        context.parsed(HIERARCHY);
    }

    /**
     * The column that sits on the hierarchy itself, which is how a parent-child hierarchy names its one
     * column: the first that resolves. Taken from the hierarchy's own direct list only, so that a level's
     * columns are never picked up here. Empty when there is none; that is not an error.
     */
    private static String ownDisplayColumn(XmlNode root, ReadContext context) {
        XmlNode own = root.firstChild("RefDisplayColumns");
        if (own == null) {
            return "";
        }
        for (XmlNode ref : own.descendants(COLUMN_REF)) {
            String column = context.index.resolve(ref.attr("presentationColumnRef"));
            if (!column.isEmpty()) {
                return column;
            }
        }
        return "";
    }
}
