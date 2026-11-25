package obi2pbi.in.mds;

import obi2pbi.core.BmmJoin;
import obi2pbi.core.ExtractionError;
import obi2pbi.core.Identifiers;
import obi2pbi.in.xml.XmlNode;

/**
 * Reads {@code LogicalComplexJoin} files: a join between two logical tables, with its type (inner or
 * outer) and the multiplicity of each side, all as the source writes them.
 *
 * <p>The identifier is not qualified by business model, as in the reference implementation; two joins of
 * the same name are reported as a duplicate identifier. A logical join is given no identifier that others
 * can refer to, so everything happens in {@link #read}.
 */
public final class LogicalComplexJoinReader implements ObjectReader {

    private static final String JOIN = "LogicalComplexJoin";

    @Override
    public String objectType() {
        return JOIN;
    }

    @Override
    public void bind(XmlNode root, ReadContext context) {
        // Nothing refers to a logical join.
    }

    @Override
    public void read(XmlNode root, ReadContext context) {
        String name = root.attr("name");
        if (name.isEmpty()) {
            context.error(ExtractionError.MISSING_NAME, ReadContext.sourceIdOf(root), JOIN, "no name");
            return;
        }
        String rowId = context.makeId(root, JOIN, Identifiers.BUSINESS_MODEL, "joins", name);
        if (rowId == null) {
            return;
        }
        // A table that is not found leaves its cell empty; that is not an error.
        context.estate.bmmJoins.add(new BmmJoin(
                rowId, context.rpdId, name, root.attr("type"),
                context.index.resolve(root.attr("logicalTable1Ref")), root.attr("multiplicity1"),
                root.attr("isAggregate1"),
                context.index.resolve(root.attr("logicalTable2Ref")), root.attr("multiplicity2"),
                root.attr("isAggregate2")));
        context.parsed(JOIN);
    }
}
