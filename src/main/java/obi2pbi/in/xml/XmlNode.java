package obi2pbi.in.xml;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * One XML element that remembers what was read from it (Architecture AD-3).
 *
 * <p>Every reader takes XML content only through this class. After the readers have run, {@link ReadAudit}
 * reports whatever is present and was never read. There is no list of "what the readers read" anywhere:
 * the record is made by the act of reading.
 *
 * <p>Two ways in, on purpose:
 * <ul>
 * <li><b>Recording</b> (for readers): {@link #attr}, {@link #text}, {@link #deepText}, {@link #ownText}, {@link #firstChild},
 * {@link #children(String)}, {@link #descendants}, {@link #below}, {@link #seek}.</li>
 * <li><b>Not recording</b> (for indexing and for the audit itself): {@link #rawAttr}, {@link #rawText},
 * {@link #attributeNames}, {@link #children()}, {@link #tag}. Use these only where looking must not count
 * as reading; anything a reader takes this way escapes the audit.</li>
 * </ul>
 *
 * <p>Names: an element or attribute in a namespace is named {@code {namespace-uri}local}; one in no
 * namespace is its bare name. Namespace declarations ({@code xmlns}, {@code xmlns:x}) are not attributes
 * and are never held here. Lookup methods take either the full name (starting with <code>{</code>) or a
 * bare local name, which matches in any namespace.
 */
public final class XmlNode {

    private final String namespace;
    private final String localName;
    private final String tag;
    private final Map<String, String> attributes;
    private final List<XmlNode> children = new ArrayList<>();
    private String text = "";
    private String tail = "";

    private Set<String> attributesRead;
    private boolean textRead;
    /** True when the text was taken as part of an enclosing element's whole text ({@link #deepText}). */
    private boolean textTaken;
    private boolean sought;

    /**
     * @param namespace  the namespace URI, or empty
     * @param attributes attribute name to value, in document order; namespace declarations excluded
     */
    public XmlNode(String namespace, String localName, Map<String, String> attributes) {
        this.namespace = namespace == null ? "" : namespace;
        this.localName = localName;
        this.tag = qualified(this.namespace, localName);
        this.attributes = new LinkedHashMap<>(attributes);
    }

    /** {@code {uri}local} when the URI is not empty, else the bare local name. */
    public static String qualified(String namespace, String localName) {
        return namespace == null || namespace.isEmpty() ? localName : "{" + namespace + "}" + localName;
    }

    // ------------------------------------------------------------------ names (never recorded)

    /** The element's full name: {@code {namespace-uri}local}, or the bare name when it has no namespace. */
    public String tag() {
        return tag;
    }

    public String localName() {
        return localName;
    }

    public String namespace() {
        return namespace;
    }

    /** True when this element has the given name: a full name, or a bare local name in any namespace. */
    public boolean is(String name) {
        return name.startsWith("{") ? tag.equals(name) : localName.equals(name);
    }

    // ------------------------------------------------------------------ recording access, for readers

    /** The attribute's value, exactly as written, or empty when absent. Recorded as read either way. */
    public String attr(String name) {
        return attr(name, "");
    }

    /** As {@link #attr(String)}, with the value to return when the attribute is absent. */
    public String attr(String name, String whenAbsent) {
        if (attributesRead == null) {
            attributesRead = new HashSet<>();
        }
        attributesRead.add(name);
        String value = attributes.get(name);
        return value == null ? whenAbsent : value;
    }

    /**
     * The text between the start tag and the first child element, exactly as written; empty when there is
     * none. Recorded as read even when empty. This is what the reference implementation means by "text".
     */
    public String text() {
        textRead = true;
        return text;
    }

    /**
     * The whole text of this element: all the character data inside it in document order, with the text of
     * the elements nested in it and the text between and after them, exactly as written. Comments and
     * processing instructions are not text. Tags and attributes of nested elements are not part of it.
     *
     * <p>Records this element's text as read. The text of each nested element is recorded as taken, so
     * the audit does not report it as unread text; but a nested element is <b>not</b> thereby looked at:
     * unless a reader reads it in its own right it is still reported as an unread element, and its
     * attributes as unread attributes. Taking its words is not understanding its structure.
     */
    public String deepText() {
        return ownText(null);
    }

    /**
     * The text that is this element's own when nested elements of one name are read separately: all the
     * character data inside it in document order, as {@link #deepText}, but leaving out every nested
     * element with the given name together with everything inside it. The text after such an element is
     * kept, because it belongs to the element around it. Exactly as written; nothing is trimmed.
     *
     * <p>Recorded like {@link #deepText}: this element's text as read, the text of each nested element that
     * was included as taken. The elements left out are not recorded at all.
     *
     * @param readSeparately a full or bare element name; null leaves nothing out
     */
    public String ownText(String readSeparately) {
        StringBuilder all = new StringBuilder();
        List<Object> pending = new ArrayList<>();
        pending.add(this);
        textRead = true;
        while (!pending.isEmpty()) {
            Object next = pending.remove(pending.size() - 1);
            if (next instanceof String) {
                all.append((String) next);
                continue;
            }
            XmlNode node = (XmlNode) next;
            if (node != this && readSeparately != null && node.is(readSeparately)) {
                continue;
            }
            node.textTaken = true;
            all.append(node.text);
            for (int i = node.children.size() - 1; i >= 0; i--) {
                XmlNode child = node.children.get(i);
                pending.add(child.tail);
                pending.add(child);
            }
        }
        return all.toString();
    }

    /** Marks this element as looked for, whatever is then taken from it. Returns this element. */
    public XmlNode seek() {
        sought = true;
        return this;
    }

    /** The first direct child with this name, marked as looked for; null when there is none. */
    public XmlNode firstChild(String name) {
        for (XmlNode child : children) {
            if (child.is(name)) {
                return child.seek();
            }
        }
        return null;
    }

    /** Every direct child with this name, in document order, each marked as looked for. */
    public List<XmlNode> children(String name) {
        List<XmlNode> found = new ArrayList<>();
        for (XmlNode child : children) {
            if (child.is(name)) {
                found.add(child.seek());
            }
        }
        return found;
    }

    /**
     * This element and every element below it that has this name, in document order, each marked as looked
     * for. Reference lists sit one wrapper deep, so direct children alone would miss them.
     */
    public List<XmlNode> descendants(String name) {
        List<XmlNode> found = new ArrayList<>();
        for (XmlNode node : subtree()) {
            if (node.is(name)) {
                found.add(node.seek());
            }
        }
        return found;
    }

    /**
     * Every element below this one that has this name, in document order, each marked as looked for. This
     * element itself is never included, whatever its name.
     */
    public List<XmlNode> below(String name) {
        List<XmlNode> found = new ArrayList<>();
        for (XmlNode node : subtree()) {
            if (node != this && node.is(name)) {
                found.add(node.seek());
            }
        }
        return found;
    }

    // ------------------------------------------------------------------ non-recording access

    /** The attribute's value or empty, <b>without</b> recording a read. For indexing and the audit only. */
    public String rawAttr(String name) {
        String value = attributes.get(name);
        return value == null ? "" : value;
    }

    /** The names of the attributes present, in document order. Not recorded. */
    public Set<String> attributeNames() {
        return Collections.unmodifiableSet(attributes.keySet());
    }

    /** The text before the first child element, <b>without</b> recording a read. */
    public String rawText() {
        return text;
    }

    /** The direct children in document order. Walking them records nothing. */
    public List<XmlNode> children() {
        return Collections.unmodifiableList(children);
    }

    /** This element and everything below it, in document order. Records nothing. */
    public List<XmlNode> subtree() {
        List<XmlNode> all = new ArrayList<>();
        List<XmlNode> pending = new ArrayList<>();
        pending.add(this);
        while (!pending.isEmpty()) {
            XmlNode node = pending.remove(pending.size() - 1);
            all.add(node);
            for (int i = node.children.size() - 1; i >= 0; i--) {
                pending.add(node.children.get(i));
            }
        }
        return all;
    }

    // ------------------------------------------------------------------ what was read, for the audit

    public boolean wasAttributeRead(String name) {
        return attributesRead != null && attributesRead.contains(name);
    }

    /** True when the text was read, on its own or as part of an enclosing element's whole text. */
    public boolean wasTextRead() {
        return textRead || textTaken;
    }

    /** True when a reader asked this element for an attribute or its text, or looked it up. */
    public boolean wasTouched() {
        return textRead || sought || (attributesRead != null && !attributesRead.isEmpty());
    }

    // ------------------------------------------------------------------ building, for XmlTreeReader

    void addChild(XmlNode child) {
        children.add(child);
    }

    /** Character data met while this element is open: its own text, or the tail of its last child. */
    void appendCharacters(String characters) {
        if (children.isEmpty()) {
            text = text.isEmpty() ? characters : text + characters;
        } else {
            XmlNode last = children.get(children.size() - 1);
            last.tail = last.tail.isEmpty() ? characters : last.tail + characters;
        }
    }
}
