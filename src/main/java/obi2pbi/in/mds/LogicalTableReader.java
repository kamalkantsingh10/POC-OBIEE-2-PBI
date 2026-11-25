package obi2pbi.in.mds;

import obi2pbi.core.AggregationRule;
import obi2pbi.core.Expression;
import obi2pbi.core.ExtractionError;
import obi2pbi.core.Identifiers;
import java.util.ArrayList;
import java.util.List;
import obi2pbi.core.LogicalColumn;
import obi2pbi.core.LogicalKey;
import obi2pbi.core.LogicalTable;
import obi2pbi.core.TimeSeries;
import obi2pbi.in.xml.XmlNode;

/**
 * Reads {@code LogicalTable} files: the table, its columns with their formulas, and the aggregation rules
 * of its measures.
 *
 * <p>The keys of the table are rows of their own (deliberate difference 29).
 *
 * <p>Not read, and therefore reported in {@code unparsed.csv}: the sort-order link of a column, and the
 * references to the table's sources (they are counted, not followed).
 */
public final class LogicalTableReader implements ObjectReader {

    private static final String TABLE = "LogicalTable";
    private static final String COLUMN = "LogicalColumn";
    private static final String RULE = "AggrRule";
    private static final String KEY = "LogicalKey";

    @Override
    public String objectType() {
        return TABLE;
    }

    @Override
    public void bind(XmlNode root, ReadContext context) {
        String name = root.attr("name");
        if (name.isEmpty()) {
            context.error(ExtractionError.MISSING_NAME, ReadContext.sourceIdOf(root), TABLE, "table has no name");
            return;
        }
        String businessModel = businessModel(root, context);
        if (businessModel.isEmpty()) {
            // The business model is the identifier's container. Without it the identifier could name tables
            // of two models, so the table is refused; no placeholder is made up.
            context.error(ExtractionError.UNRESOLVED_REFERENCE, ReadContext.sourceIdOf(root), TABLE,
                    "business model for logical table " + Expressions.quoted(name) + " could not be resolved");
            return;
        }
        String tableId = context.makeId(root, TABLE, Identifiers.BUSINESS_MODEL, businessModel, name);
        if (tableId == null) {
            return;
        }
        context.index.bind(root, tableId);

        for (XmlNode column : root.children(COLUMN)) {
            String columnName = column.attr("name");
            if (columnName.isEmpty()) {
                context.error(ExtractionError.MISSING_NAME, ReadContext.sourceIdOf(column), COLUMN,
                        "column has no name");
                continue;
            }
            String columnId =
                    context.makeId(column, COLUMN, Identifiers.BUSINESS_MODEL, businessModel, name, columnName);
            if (columnId != null) {
                context.index.bind(column, columnId);
            }
        }
    }

    @Override
    public void read(XmlNode root, ReadContext context) {
        String tableId = context.index.identifierOf(root);
        if (tableId == null) {
            return;
        }
        String rpdId = context.rpdId;

        XmlNode sources = root.firstChild("RefTableSources");
        int sourceCount = sources == null ? 0 : sources.children("RefLogicalTableSource").size();

        context.estate.logicalTables.add(
                new LogicalTable(tableId, rpdId, businessModel(root, context), root.attr("name"), sourceCount,
                        root.attr("isLookupTable")));
        context.parsed(TABLE);

        // Difference 29: the table's keys, each with its columns in source order and the primary flag as
        // the source writes it.
        int keyPlace = 0;
        for (XmlNode key : root.children(KEY)) {
            keyPlace++;
            String keyName = key.attr("name");
            String keyId = context.makeId(key, KEY, Identifiers.BUSINESS_MODEL, businessModel(root, context),
                    root.attr("name"), "key", keyName.isEmpty() ? "unnamed-" + keyPlace : keyName);
            if (keyId == null) {
                continue;
            }
            List<String> columns = new ArrayList<>();
            // The columns sit one wrapper below the key.
            for (XmlNode ref : key.descendants("RefLogicalColumn")) {
                columns.add(context.index.resolve(ref.attr("logicalColumnRef")));
            }
            context.estate.logicalKeys.add(
                    new LogicalKey(keyId, rpdId, tableId, keyName, key.attr("isPrimary"), columns));
            context.parsed(KEY);
        }

        for (XmlNode column : root.children(COLUMN)) {
            String columnId = context.index.identifierOf(column);
            if (columnId == null) {
                continue;
            }
            // The column's own formula.
            Expression derived = Expressions.read(column.firstChild("Expr"), context, columnId);
            // The definition the source keeps on the column itself.
            XmlNode attributeDefinition = column.firstChild("AttrDefn");
            Expression mapping = attributeDefinition == null ? Expression.EMPTY
                    : Expressions.read(attributeDefinition.firstChild("Expr"), context, columnId);

            // A column is a measure when it has a measure definition. That is the only test.
            XmlNode measure = column.firstChild("MeasureDefn");
            context.estate.logicalColumns.add(new LogicalColumn(
                    columnId, rpdId, tableId, column.attr("name"), column.attr("isWriteable"), measure != null,
                    TimeSeries.used(derived, mapping), sourceCount, 0, derived, mapping,
                    // Difference 37: the column that holds the identifier this column is the text of.
                    context.index.resolve(column.attr("descriptorIDRef"))));
            context.parsed(COLUMN);

            if (measure == null) {
                continue;
            }
            // One row per rule, numbered from 0 in source order. A measure may have a default rule and
            // others that replace it for particular dimensions; merging them would lose the others.
            int position = 0;
            for (XmlNode rule : measure.children(RULE)) {
                context.estate.aggregationRules.add(new AggregationRule(
                        columnId + "#aggr" + position, rpdId, columnId, rule.attr("name"),
                        rule.attr("isDefault"), Expressions.read(rule.firstChild("Expr"), context, columnId)));
                context.parsed(RULE);
                position++;
            }
        }
    }

    /** The name of the business model the table belongs to, or empty. The attribute's name misleads. */
    private static String businessModel(XmlNode root, ReadContext context) {
        ReferenceIndex.Entry model = context.index.entry(ReferenceIndex.mdsidOf(root.attr("subjectAreaRef")));
        return model == null ? "" : model.name;
    }
}
