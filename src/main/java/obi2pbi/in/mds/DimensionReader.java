package obi2pbi.in.mds;

import java.util.ArrayList;
import java.util.List;
import obi2pbi.core.ExtractionError;
import obi2pbi.core.Identifiers;
import obi2pbi.core.LogicalDimension;
import obi2pbi.core.LogicalLevel;
import obi2pbi.core.LogicalLevelKey;
import obi2pbi.in.xml.XmlNode;

/**
 * Reads {@code Dimension} files: a logical dimension and its levels.
 *
 * <p>The ragged, skip-level and parent-child flags are kept as flags; nothing is flattened. A parent-child
 * dimension can still have levels (the samples have one with two).
 *
 * <p>Every key of a level is a row of its own, with its primary, chronological and drill-down flags and
 * all its columns (deliberate difference 30); the dimension carries its time flag (difference 32).
 *
 * <p>Each level carries the levels it lists as its children and the levels that list it (difference 35).
 *
 * <p>Not read, and therefore reported in {@code unparsed.csv}: the preferred drill path, the columns a
 * level lists outside its keys, and a level's constant.
 */
public final class DimensionReader implements ObjectReader {

    private static final String DIMENSION = "Dimension";
    private static final String LEVEL = "LogicalLevel";
    private static final String KEY = "LogicalKey";

    @Override
    public String objectType() {
        return DIMENSION;
    }

    @Override
    public void bind(XmlNode root, ReadContext context) {
        String name = root.attr("name");
        if (name.isEmpty()) {
            context.error(ExtractionError.MISSING_NAME, ReadContext.sourceIdOf(root), DIMENSION, "no name");
            return;
        }
        String businessModel = businessModel(root, context);
        if (businessModel.isEmpty()) {
            // The business model is the identifier's container, as for a logical table: the samples have
            // four dimensions named "Times". Without it the dimension is refused; no placeholder is made up.
            context.error(ExtractionError.UNRESOLVED_REFERENCE, ReadContext.sourceIdOf(root), DIMENSION,
                    "business model for dimension " + Expressions.quoted(name) + " could not be resolved");
            return;
        }
        String dimensionId = context.makeId(root, DIMENSION, Identifiers.BUSINESS_MODEL, businessModel, name);
        if (dimensionId == null) {
            return;
        }
        context.index.bind(root, dimensionId);

        for (XmlNode level : root.children(LEVEL)) {
            String levelName = level.attr("name");
            if (levelName.isEmpty()) {
                context.error(ExtractionError.MISSING_NAME, ReadContext.sourceIdOf(level), LEVEL, "no name");
                continue;
            }
            String levelId =
                    context.makeId(level, LEVEL, Identifiers.BUSINESS_MODEL, businessModel, name, levelName);
            if (levelId != null) {
                context.index.bind(level, levelId);
            }
        }
    }

    @Override
    public void read(XmlNode root, ReadContext context) {
        String dimensionId = context.index.identifierOf(root);
        if (dimensionId == null) {
            return;
        }
        String rpdId = context.rpdId;

        List<XmlNode> levels = root.children(LEVEL);
        // Difference 35: which level is the child of which. A level lists its children; the parents of a
        // level are the levels of this dimension that list it, in source order.
        java.util.Map<XmlNode, List<String>> children = new java.util.IdentityHashMap<>();
        java.util.Map<String, List<String>> parents = new java.util.HashMap<>();
        for (XmlNode level : levels) {
            String levelId = context.index.identifierOf(level);
            if (levelId == null) {
                continue;
            }
            List<String> listed = new ArrayList<>();
            XmlNode list = level.firstChild("RefChildLevels");
            if (list != null) {
                for (XmlNode ref : list.children("RefLogicalLevel")) {
                    String child = context.index.resolve(ref.attr("logicalLevelRef"));
                    listed.add(child);
                    if (!child.isEmpty()) {
                        parents.computeIfAbsent(child, k -> new ArrayList<>()).add(levelId);
                    }
                }
            }
            children.put(level, listed);
        }
        for (int position = 0; position < levels.size(); position++) {
            XmlNode level = levels.get(position);
            String levelId = context.index.identifierOf(level);
            if (levelId == null) {
                // Not named in the first phase. It still takes its position, so positions can have gaps.
                continue;
            }
            context.estate.logicalLevels.add(new LogicalLevel(
                    levelId, rpdId, dimensionId, position, level.attr("name"), level.attr("memberCount"),
                    level.attr("isGTA").equals("true"), drillKeyColumn(level, context),
                    children.getOrDefault(level, List.of()), parents.getOrDefault(levelId, List.of())));
            context.parsed(LEVEL);

            // Difference 30: every key of the level, with the flags as the source writes them.
            int keyPlace = 0;
            for (XmlNode key : level.children(KEY)) {
                keyPlace++;
                String keyName = key.attr("name");
                String keyId = context.makeId(key, KEY, Identifiers.BUSINESS_MODEL, businessModel(root, context),
                        root.attr("name"), level.attr("name"), "key",
                        keyName.isEmpty() ? "unnamed-" + keyPlace : keyName);
                if (keyId == null) {
                    continue;
                }
                List<String> columns = new ArrayList<>();
                for (XmlNode ref : key.descendants("RefLogicalColumn")) {
                    columns.add(context.index.resolve(ref.attr("logicalColumnRef")));
                }
                context.estate.logicalLevelKeys.add(new LogicalLevelKey(keyId, rpdId, levelId, keyName,
                        key.attr("isPrimary"), key.attr("isChronKey"), key.attr("isForDrillDown"), columns));
                context.parsed(KEY);
            }
        }

        // The count includes a level that was skipped for having no name.
        context.estate.logicalDimensions.add(new LogicalDimension(
                dimensionId, rpdId, root.attr("name"), businessModel(root, context),
                root.attr("isValueBased").equals("true"), root.attr("isRagged").equals("true"),
                root.attr("isSkipped").equals("true"),
                context.index.resolve(root.attr("defaultRootLevelRef")), levels.size(), root.attr("isTimeDim")));
        context.parsed(DIMENSION);
    }

    /**
     * The logical column a drill on this level follows: the first column that resolves in the first key
     * marked for drill-down that has one. Empty when there is none; that is not an error.
     */
    private static String drillKeyColumn(XmlNode level, ReadContext context) {
        for (XmlNode key : level.children("LogicalKey")) {
            if (!key.attr("isForDrillDown").equals("true")) {
                continue;
            }
            // The columns sit one wrapper below the key.
            for (XmlNode ref : key.descendants("RefLogicalColumn")) {
                String column = context.index.resolve(ref.attr("logicalColumnRef"));
                if (!column.isEmpty()) {
                    return column;
                }
            }
        }
        return "";
    }

    /** The name of the business model the dimension belongs to, or empty. The attribute's name misleads. */
    private static String businessModel(XmlNode root, ReadContext context) {
        ReferenceIndex.Entry model = context.index.entry(ReferenceIndex.mdsidOf(root.attr("subjectAreaRef")));
        return model == null ? "" : model.name;
    }
}
