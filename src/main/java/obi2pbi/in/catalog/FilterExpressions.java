package obi2pbi.in.catalog;

import java.util.ArrayList;
import java.util.List;
import obi2pbi.core.AnalysisFilter;
import obi2pbi.core.PathResolution;
import obi2pbi.in.xml.XmlNode;

/**
 * Turns filter elements into filter rows: one row for every expression below a filter, in document order.
 *
 * <p>A filter wraps one tree of expressions: and/or groups at the top, comparisons below them, operands at
 * the leaves. Every expression is a row, because the conditions are in the nested ones. So that the tree
 * can be rebuilt, each row names the row of the expression that directly encloses it and says how deep it
 * sits (deliberate difference 10). Elements between two expressions that are not expressions themselves
 * are passed through: the parent is the nearest enclosing <em>expression</em> of the same filter.
 *
 * <p>The text of a row is the expression's <em>own</em> text (deliberate difference 8, the rule for
 * filters): all the character data inside the element, before, between and after its children, and the
 * text of nested elements that are not expressions; but not the text of a nested expression, which is a
 * row of its own. So a leaf holds its column, formula or literal, and a group or comparison holds only
 * what is written directly in it, usually white space or nothing.
 *
 * <p>Used for the filters of a report and, in the same form, for a saved filter (deliberate difference 9).
 */
final class FilterExpressions {

    private FilterExpressions() {
    }

    /** One element waiting to be visited, with the row and depth of the expression that encloses it. */
    private static final class Pending {
        final XmlNode node;
        final String parentId;
        final int depth;

        Pending(XmlNode node, String parentId, int depth) {
            this.node = node;
            this.parentId = parentId;
            this.depth = depth;
        }
    }

    /**
     * Writes the rows of the filters of one owner, numbering them with one counter across the filters.
     *
     * @param holders  the filter elements, in document order
     * @param idPrefix what a row's number is appended to, to make its Row identifier
     * @param scope    {@link AnalysisFilter#SCOPE_ANALYSIS} or {@link AnalysisFilter#SCOPE_SAVED_FILTER}
     * @param object   the catalog object that holds the filters; it owns the rows
     */
    static void read(List<XmlNode> holders, String idPrefix, String scope, CatalogObject object,
            CatalogContext context) {
        int order = 0;
        for (XmlNode holder : holders) {
            List<Pending> pending = new ArrayList<>();
            push(pending, holder, "", 0);
            while (!pending.isEmpty()) {
                Pending next = pending.remove(pending.size() - 1);
                XmlNode node = next.node;
                if (!node.is(CatalogXml.EXPR)) {
                    push(pending, node, next.parentId, next.depth);
                    continue;
                }
                node.seek();
                String rowId = idPrefix + order;
                String type = node.attr(CatalogXml.XSI_TYPE);
                String reference = node.attr(CatalogXml.PATH);
                String target = "";
                String outcome = "";
                if (type.equals(CatalogXml.SAVED_FILTER_TYPE) && !reference.isEmpty()) {
                    PathResolution resolved = context.resolve(reference, object, CatalogXml.EXPR);
                    target = resolved.targetId;
                    outcome = resolved.outcome;
                }
                // Captured, never checked against a list: arithmetic and set operators share the attribute.
                String op = node.attr("op");
                context.estate.analysisFilters.add(new AnalysisFilter(rowId, context.rpdId, scope, object.rowId,
                        order, op, type, ownText(node), reference, target, outcome,
                        type.equals(CatalogXml.EVAL_TYPE) || usesEval(node), next.parentId, next.depth));
                order++;
                push(pending, node, rowId, next.depth + 1);
            }
        }
    }

    /** The text of a filter expression that is its own: nested expressions, which are rows themselves, left out. */
    static String ownText(XmlNode expr) {
        return expr.ownText(CatalogXml.EXPR);
    }

    private static void push(List<Pending> pending, XmlNode parent, String parentId, int depth) {
        List<XmlNode> children = parent.children();
        for (int i = children.size() - 1; i >= 0; i--) {
            pending.add(new Pending(children.get(i), parentId, depth));
        }
    }

    /**
     * True when this element or anything below it is an expression that calls a database function directly.
     * Such a call is never the outermost expression, so the nested ones must be looked at.
     *
     * <p>Stops at the first one found, as the reference implementation does. That matters to the audit:
     * every expression asked for its type counts as looked at, and the ones after the first hit are not
     * asked.
     */
    static boolean usesEval(XmlNode container) {
        for (XmlNode node : container.subtree()) {
            if (node.is(CatalogXml.EXPR) && node.attr(CatalogXml.XSI_TYPE).equals(CatalogXml.EVAL_TYPE)) {
                return true;
            }
        }
        return false;
    }
}
