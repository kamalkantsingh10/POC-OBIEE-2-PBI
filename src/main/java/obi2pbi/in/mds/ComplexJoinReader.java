package obi2pbi.in.mds;

import java.util.List;
import obi2pbi.core.ExtractionError;
import obi2pbi.core.Identifiers;
import obi2pbi.core.PhysicalJoin;
import obi2pbi.in.xml.XmlNode;

/**
 * Reads {@code ComplexJoin} files: a join between two physical tables with a condition of its own. (A
 * foreign key is read with its table, by {@link PhysicalTableReader}.)
 *
 * <p>A complex join is given no identifier that others can refer to, as in the reference implementation:
 * a formula that refers to one stays unresolved, with its error row. So everything happens in
 * {@link #read}.
 */
public final class ComplexJoinReader implements ObjectReader {

    private static final String JOIN = "ComplexJoin";

    @Override
    public String objectType() {
        return JOIN;
    }

    @Override
    public void bind(XmlNode root, ReadContext context) {
        // Nothing refers to a complex join.
    }

    @Override
    public void read(XmlNode root, ReadContext context) {
        String name = root.attr("name");
        if (name.isEmpty()) {
            context.error(ExtractionError.MISSING_NAME, ReadContext.sourceIdOf(root), JOIN, "join has no name");
            return;
        }
        String rowId = context.makeId(root, JOIN, Identifiers.PHYSICAL, "joins", name);
        if (rowId == null) {
            return;
        }
        // A table that is not found leaves its cell empty; that is not an error.
        String table1 = context.index.resolve(root.attr("table1Ref"));
        String table2 = context.index.resolve(root.attr("table2Ref"));
        // An unresolved reference inside the condition is reported against the join's name, as the
        // reference implementation does.
        context.estate.physicalJoins.add(new PhysicalJoin(
                rowId, context.rpdId, PhysicalJoin.COMPLEX_JOIN, name, table1, table2, root.attr("cardinality"),
                Expressions.read(root.firstChild("Expr"), context, name), List.of()));
        context.parsed(JOIN);
    }
}
