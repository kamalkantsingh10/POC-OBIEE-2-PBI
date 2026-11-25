package obi2pbi.in.mds;

import java.util.Set;

/**
 * Which unread attributes of the repository's MDS XML are only identifiers of an element whose content is read
 * (story 2.1, the gaps packet). Such a row of {@code unparsed} is not content that was missed: {@code @mdsid} is the
 * repository's internal id of any element; {@code @refId} the internal id of a reference element, whose target is
 * read from its other attribute; {@code @name} the internal name of an element that holds an expression the
 * Knowledge Base keeps ({@code Expr}, {@code WhereClause}, {@code FragmentContent}, {@code GroupBy},
 * {@code LogicalColumnExpr}, {@code MeasureDefn}, {@code AttrDefn}). Catalog attributes (named with a namespace)
 * are never counted as identifiers.
 */
public final class IdentifierAttributes {

    private static final Set<String> EXPRESSION_ELEMENTS = Set.of("Expr", "WhereClause", "FragmentContent", "GroupBy",
            "LogicalColumnExpr", "MeasureDefn", "AttrDefn");

    private IdentifierAttributes() {
    }

    /** @param detail the {@code detail} of an {@code unread_attribute} row: {@code Element@attribute} */
    public static boolean isIdentifierOnly(String detail) {
        if (detail.startsWith("{")) {
            return false;
        }
        int at = detail.indexOf('@');
        if (at <= 0 || detail.indexOf('@', at + 1) >= 0) {
            return false;
        }
        String element = detail.substring(0, at);
        String attribute = detail.substring(at + 1);
        return attribute.equals("mdsid") || (attribute.equals("refId") && element.startsWith("Ref"))
                || (attribute.equals("name") && EXPRESSION_ELEMENTS.contains(element));
    }
}
