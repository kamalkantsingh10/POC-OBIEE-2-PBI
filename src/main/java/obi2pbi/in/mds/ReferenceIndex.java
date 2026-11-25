package obi2pbi.in.mds;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import obi2pbi.in.xml.SourceTree;
import obi2pbi.in.xml.XmlNode;

/**
 * The repository's cross-reference graph, for one repository.
 *
 * <p>Repository files point at each other with {@code /oracle/bi/server/base/<Type>/<file>.xml#<mdsid>}.
 * Only the part after {@code #} is used to find the target; it is the target element's own {@code mdsid}.
 * The index answers two different questions:
 * <ul>
 * <li>{@link #entry}: the name and container of any element, known as soon as the index is built. Used for
 * objects that have no table of their own (schema, database, business model).</li>
 * <li>{@link #identifier}: the Row identifier an element was given, known once its reader has bound it.
 * Every reader binds in the first phase and resolves in the second, so a reference never fails merely
 * because its target is read later (deliberate difference 5).</li>
 * </ul>
 * These source ids stay inside this package; nothing here reaches {@code core}.
 */
public final class ReferenceIndex {

    private static final Pattern REFERENCE =
            Pattern.compile("^/oracle/bi/server/base/([^/]+)/[^#]+#(.+)$", Pattern.DOTALL);

    /** What the index knows about one element that has an {@code mdsid}. */
    public static final class Entry {
        public final String objectType;
        public final String tag;
        public final String name;
        /** The {@code mdsid} of the nearest enclosing element that has one, or empty. */
        public final String ownerMdsid;
        /** The {@code mdsid} its {@code containerRef} points at, or empty. */
        public final String containerMdsid;

        Entry(String objectType, String tag, String name, String ownerMdsid, String containerMdsid) {
            this.objectType = objectType;
            this.tag = tag;
            this.name = name;
            this.ownerMdsid = ownerMdsid;
            this.containerMdsid = containerMdsid;
        }
    }

    private final Map<String, Entry> entries = new HashMap<>();
    private final Map<String, XmlNode> nodes = new HashMap<>();
    private final Map<String, String> identifiers = new HashMap<>();
    private final Map<XmlNode, String> identifierOfNode = new IdentityHashMap<>();

    /**
     * Indexes every element of every tree. Reads through the non-recording accessors on purpose: indexing
     * touches every element only to build the graph, and if that counted as reading, nothing would ever be
     * reported unread.
     */
    public static ReferenceIndex build(List<SourceTree> trees) {
        ReferenceIndex index = new ReferenceIndex();
        for (SourceTree tree : trees) {
            index.add(tree.objectType, tree.root, "");
        }
        return index;
    }

    private void add(String objectType, XmlNode root, String rootOwner) {
        Map<XmlNode, String> ownerOf = new IdentityHashMap<>();
        ownerOf.put(root, rootOwner);
        for (XmlNode node : root.subtree()) {
            String owner = ownerOf.get(node);
            String mdsid = node.rawAttr("mdsid");
            if (!mdsid.isEmpty()) {
                entries.put(mdsid, new Entry(objectType, node.localName(), node.rawAttr("name"), owner,
                        mdsidOf(node.rawAttr("containerRef"))));
                nodes.put(mdsid, node);
                owner = mdsid;
            }
            for (XmlNode child : node.children()) {
                ownerOf.put(child, owner);
            }
        }
    }

    /** The object type named inside a reference, or empty when the text is not a reference. */
    public static String typeOf(String reference) {
        Matcher match = REFERENCE.matcher(reference == null ? "" : reference);
        return match.matches() ? match.group(1) : "";
    }

    /** The {@code mdsid} a reference points at, or empty when the text is not a reference. */
    public static String mdsidOf(String reference) {
        Matcher match = REFERENCE.matcher(reference == null ? "" : reference);
        return match.matches() ? match.group(2) : "";
    }

    /** The indexed element with this {@code mdsid}, or null. */
    public Entry entry(String mdsid) {
        return entries.get(mdsid);
    }

    /**
     * The element with this {@code mdsid}, or null. For a reader that follows a reference into an element
     * no reader of its own handles (the key a foreign key points at, the role of a privilege package):
     * what it then takes from the element goes through the recording accessors, so only what really
     * reaches a table counts as read.
     */
    public XmlNode node(String mdsid) {
        return mdsid == null ? null : nodes.get(mdsid);
    }

    /**
     * Records the Row identifier an element resolved to. Reads the element's {@code mdsid} as a reader
     * would. An element without one is still remembered by {@link #identifierOf}, and cannot be referred to.
     */
    public void bind(XmlNode node, String rowId) {
        identifierOfNode.put(node, rowId);
        String mdsid = node.attr("mdsid");
        if (!mdsid.isEmpty()) {
            identifiers.put(mdsid, rowId);
        }
    }

    /** The Row identifier bound to this element in the first phase, or null when it was not bound. */
    public String identifierOf(XmlNode node) {
        return identifierOfNode.get(node);
    }

    /** The Row identifier bound to this {@code mdsid}, or empty. */
    public String identifier(String mdsid) {
        if (mdsid == null || mdsid.isEmpty()) {
            return "";
        }
        return identifiers.getOrDefault(mdsid, "");
    }

    /** The Row identifier a reference points at, or empty when it is not a reference or nothing is bound. */
    public String resolve(String reference) {
        return identifier(mdsidOf(reference));
    }
}
