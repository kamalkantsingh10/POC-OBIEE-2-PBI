package obi2pbi.in.mds;

import java.util.ArrayList;
import java.util.List;
import obi2pbi.core.Expression;
import obi2pbi.core.ExtractionError;
import obi2pbi.in.xml.XmlNode;

/** Reading shared by the repository readers: an expression in its three forms, and small helpers. */
final class Expressions {

    /** The object type an unresolved reference inside an expression is reported under. */
    static final String EXPRESSION_REF = "ExpressionRef";

    private Expressions() {
    }

    /**
     * Reads an expression-shaped element: the template text, the rendered text, and what the placeholders
     * refer to. Text is taken exactly as written. When a tag occurs twice, the last one wins.
     *
     * <p>The reference list keeps one entry per reference, in order, so entry 1 belongs to {@code %1}. A
     * reference that does not resolve leaves an empty entry and an {@code unresolved_reference} error
     * against {@code ownerId} (deliberate difference 6). The source's own id is never written out.
     *
     * @param container the element holding the three parts, or null when the source has none
     */
    static Expression read(XmlNode container, ReadContext context, String ownerId) {
        if (container == null) {
            return Expression.EMPTY;
        }
        String text = "";
        String textDesc = "";
        List<String> refs = new ArrayList<>();
        for (XmlNode child : container.children()) {
            if (child.is("ExprText")) {
                text = child.text();
            } else if (child.is("ExprTextDesc")) {
                textDesc = child.text();
            } else if (child.is("ObjectRefList")) {
                for (XmlNode refObject : child.children()) {
                    if (!refObject.is("RefObject")) {
                        continue;
                    }
                    String reference = refObject.attr("objectRef");
                    String resolved = context.index.resolve(reference);
                    refs.add(resolved);
                    if (resolved.isEmpty()) {
                        String type = ReferenceIndex.typeOf(reference);
                        context.error(ExtractionError.UNRESOLVED_REFERENCE, ownerId, EXPRESSION_REF,
                                "reference to " + (type.isEmpty() ? "unknown type" : type)
                                        + " could not be resolved to a Knowledge Base identifier");
                    }
                }
            }
        }
        return new Expression(text, textDesc, refs);
    }

    /** The first entry that resolved, or empty. Entries before it that did not resolve are passed over. */
    static String firstResolved(Expression expression) {
        for (String ref : expression.refs) {
            if (!ref.isEmpty()) {
                return ref;
            }
        }
        return "";
    }

    /** The {@code name} of each direct {@code Alias} child, in source order. */
    static List<String> aliases(XmlNode root) {
        List<String> names = new ArrayList<>();
        for (XmlNode alias : root.children("Alias")) {
            names.add(alias.attr("name"));
        }
        return names;
    }

    /**
     * A name in quotes, as the reference implementation prints one inside an error reason, so the reasons
     * compare equal: single quotes, or double quotes when the name holds a single quote and no double one.
     */
    static String quoted(String name) {
        boolean useDouble = name.indexOf('\'') >= 0 && name.indexOf('"') < 0;
        char quote = useDouble ? '"' : '\'';
        StringBuilder out = new StringBuilder().append(quote);
        for (int i = 0; i < name.length(); i++) {
            char c = name.charAt(i);
            if (c == '\\' || c == quote) {
                out.append('\\').append(c);
            } else if (c == '\n') {
                out.append("\\n");
            } else if (c == '\r') {
                out.append("\\r");
            } else if (c == '\t') {
                out.append("\\t");
            } else {
                out.append(c);
            }
        }
        return out.append(quote).toString();
    }
}
