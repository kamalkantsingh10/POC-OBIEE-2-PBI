package obi2pbi.in.xml;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.BiPredicate;
import java.util.function.Function;
import obi2pbi.core.Estate;
import obi2pbi.core.Text;
import obi2pbi.core.Unparsed;

/**
 * Reports source content the readers never took, found by observation (Architecture AD-3).
 *
 * <p>Run it once per reader, after every pass of that reader, so it sees all the reads. For each tree of a
 * handled object type it reports:
 * <ul>
 * <li>{@code unread_element}: an element where nothing in its whole subtree was read or looked for. A
 * wrapper the readers walked through is not reported, though nothing was read from the wrapper itself.</li>
 * <li>{@code unread_attribute}: an attribute present and never asked for, at every level, including inside
 * an unread element.</li>
 * <li>{@code unread_text}: non-blank text before the first child element that was never read.</li>
 * </ul>
 * Rows are grouped by estate, object type of the file, and the element's own name (not its path), and
 * carry the number of elements counted.
 */
public final class ReadAudit {

    private ReadAudit() {
    }

    /** Audits with nothing out of scope, naming elements by local name. The repository reader's form. */
    public static void audit(List<SourceTree> trees, Set<String> handledTypes, Estate estate) {
        audit(trees, handledTypes, XmlNode::localName, (tag, attribute) -> false, "", estate);
    }

    /**
     * @param handledTypes   object types that have a reader; trees of other types are skipped, because they
     *                       are already reported whole
     * @param nameOf         the name an element is reported under
     * @param outOfScope     given element name and attribute name, true when the attribute is left out by a
     *                       scope decision; it is then still written, under {@code outOfScopeKind}
     * @param outOfScopeKind the kind for those rows
     */
    public static void audit(List<SourceTree> trees, Set<String> handledTypes,
            Function<XmlNode, String> nameOf, BiPredicate<String, String> outOfScope, String outOfScopeKind,
            Estate estate) {
        Tally tally = new Tally(nameOf, outOfScope, outOfScopeKind);
        for (SourceTree tree : trees) {
            if (handledTypes.contains(tree.objectType)) {
                tally.add(tree);
            }
        }
        tally.report(estate);
    }

    /**
     * The audit taken one tree at a time, for a reader that does not keep its trees. Give it each tree
     * once every reader that reads the tree has run; nothing of the tree is held afterwards, only counts.
     * Call {@link #report} once at the end.
     */
    public static final class Tally {

        private final Map<String[], Integer> counts = new TreeMap<>(Text.ROW_ORDER);
        private final Function<XmlNode, String> nameOf;
        private final BiPredicate<String, String> outOfScope;
        private final String outOfScopeKind;

        /** The parameters are those of {@link ReadAudit#audit(List, Set, Function, BiPredicate, String, Estate)}. */
        public Tally(Function<XmlNode, String> nameOf, BiPredicate<String, String> outOfScope, String outOfScopeKind) {
            this.nameOf = nameOf;
            this.outOfScope = outOfScope;
            this.outOfScopeKind = outOfScopeKind;
        }

        /** Counts what was left unread in one tree of a handled object type. */
        public void add(SourceTree tree) {
            List<XmlNode> nodes = tree.root.subtree();
            Map<XmlNode, Boolean> reached = reached(nodes);
            for (XmlNode node : nodes) {
                String name = nameOf.apply(node);
                if (!reached.get(node)) {
                    count(counts, tree, Unparsed.UNREAD_ELEMENT, name);
                }
                for (String attribute : node.attributeNames()) {
                    if (node.wasAttributeRead(attribute)) {
                        continue;
                    }
                    String kind = outOfScope.test(name, attribute) ? outOfScopeKind : Unparsed.UNREAD_ATTRIBUTE;
                    count(counts, tree, kind, name + "@" + attribute);
                }
                if (!node.wasTextRead() && !Text.isBlank(node.rawText())) {
                    count(counts, tree, Unparsed.UNREAD_TEXT, name + "#text");
                }
            }
        }

        /** Writes one gap row per kind of unread content counted so far. */
        public void report(Estate estate) {
            for (Map.Entry<String[], Integer> entry : counts.entrySet()) {
                String[] key = entry.getKey();
                estate.recordGap(key[0], key[3], key[1], key[2], entry.getValue());
            }
        }
    }

    /** For each element: was it, or anything below it, touched? Works bottom-up without recursion. */
    private static Map<XmlNode, Boolean> reached(List<XmlNode> documentOrder) {
        Map<XmlNode, Boolean> reached = new IdentityHashMap<>();
        // In reverse document order every child comes before its parent.
        for (int i = documentOrder.size() - 1; i >= 0; i--) {
            XmlNode node = documentOrder.get(i);
            boolean here = node.wasTouched();
            for (XmlNode child : node.children()) {
                here = here || reached.get(child);
            }
            reached.put(node, here);
        }
        return reached;
    }

    private static void count(Map<String[], Integer> counts, SourceTree tree, String kind, String detail) {
        counts.merge(new String[] {tree.rpdId, tree.objectType, detail, kind}, 1, Integer::sum);
    }

}
