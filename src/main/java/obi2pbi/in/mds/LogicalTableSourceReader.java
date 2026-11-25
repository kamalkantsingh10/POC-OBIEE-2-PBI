package obi2pbi.in.mds;

import obi2pbi.core.ColumnMapping;
import obi2pbi.core.Expression;
import obi2pbi.core.ExtractionError;
import obi2pbi.core.Identifiers;
import obi2pbi.core.LogicalTableSource;
import obi2pbi.core.SourceJoin;
import obi2pbi.core.SourceTable;
import obi2pbi.in.xml.XmlNode;

/**
 * Reads {@code LogicalTableSource} files: the source, its fragment, filter and grain expressions, and its
 * column mappings.
 *
 * <p>The physical tables the source is built on and the joins it makes between them are rows of their own
 * (deliberate difference 34), and the source carries its closure table (difference 36).
 */
public final class LogicalTableSourceReader implements ObjectReader {

    private static final String SOURCE = "LogicalTableSource";
    private static final String MAPPING = "ColumnMapping";

    @Override
    public String objectType() {
        return SOURCE;
    }

    @Override
    public void bind(XmlNode root, ReadContext context) {
        String name = root.attr("name");
        // The logical table was bound by its own reader, which is registered before this one.
        String logicalTableId = context.index.resolve(root.attr("logicalTableRef"));
        if (name.isEmpty() || logicalTableId.isEmpty()) {
            context.error(ExtractionError.UNRESOLVED_REFERENCE, ReadContext.sourceIdOf(root), SOURCE,
                    "source name or owning logical table could not be resolved");
            return;
        }
        String sourceId = context.makeId(root, SOURCE, Identifiers.BUSINESS_MODEL,
                Identifiers.parseId(logicalTableId)[2], "src", name);
        if (sourceId != null) {
            context.index.bind(root, sourceId);
        }
    }

    @Override
    public void read(XmlNode root, ReadContext context) {
        String sourceId = context.index.identifierOf(root);
        if (sourceId == null) {
            return;
        }
        String rpdId = context.rpdId;

        // The three elements are themselves expression-shaped; they do not wrap an Expr.
        context.estate.logicalTableSources.add(new LogicalTableSource(
                sourceId, rpdId, context.index.resolve(root.attr("logicalTableRef")), root.attr("name"),
                root.attr("isActive"),
                // Empty means the source records no priority. It is never worked out from position.
                root.attr("priorityGroup"),
                Expressions.read(root.firstChild("FragmentContent"), context, sourceId),
                Expressions.read(root.firstChild("WhereClause"), context, sourceId),
                Expressions.read(root.firstChild("GroupBy"), context, sourceId),
                // Difference 36: for a source of a parent-child hierarchy, the table of the relationships.
                context.index.resolve(root.attr("closureTableRef"))));
        context.parsed(SOURCE);

        // Difference 34: the physical tables the source is built on, in the source's order, and under each
        // the joins the source makes from it. Join type and cardinality are kept exactly as written. A
        // table or join that does not resolve leaves its cell empty.
        int place = 0;
        for (XmlNode link : root.children("Link")) {
            String tableRowId = sourceId + "#table" + place;
            context.estate.sourceTables.add(new SourceTable(tableRowId, rpdId, sourceId, place,
                    context.index.resolve(link.attr("startNodeTableRef"))));
            context.parsed("Link");
            int joinPlace = 0;
            for (XmlNode join : link.children("Join")) {
                context.estate.sourceJoins.add(new SourceJoin(tableRowId + ".join" + joinPlace, rpdId, sourceId,
                        tableRowId, joinPlace, join.attr("type"), join.attr("cardinality"),
                        context.index.resolve(join.attr("physicalTable1Ref")),
                        context.index.resolve(join.attr("physicalTable2Ref")),
                        PhysicalTableReader.physicalJoinId(join.attr("physicalJoinRef"), context)));
                context.parsed("Join");
                joinPlace++;
            }
            place++;
        }

        int position = 0;
        for (XmlNode mapping : root.children(MAPPING)) {
            Expression logical = Expressions.read(mapping.firstChild("LogicalColumnExpr"), context, sourceId);
            Expression physical = Expressions.read(mapping.firstChild("Expr"), context, sourceId);
            context.estate.columnMappings.add(new ColumnMapping(
                    sourceId + "#map" + position, rpdId, sourceId, Expressions.firstResolved(logical),
                    Expressions.firstResolved(physical), logical, physical));
            context.parsed(MAPPING);
            position++;
        }
    }
}
