package obi2pbi.in.catalog;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import obi2pbi.in.xml.XmlNode;

/**
 * Which elements belong to which owner, by nearest enclosing owner.
 *
 * <p>Criteria blocks nest (a set operation wraps each operand in a block of its own), so "the columns of a
 * block" cannot be "every column below it": a column would then be counted once for each block around it.
 * An element belongs to the first owner met walking up from it. An element with no owner above it belongs
 * to nobody and is not in the result.
 *
 * <p>Working this out records nothing as read. Only the reader that takes an element marks it, so an
 * element that belongs to nobody still shows up in the audit.
 */
final class Ownership {

    private final Map<XmlNode, List<XmlNode>> owned = new IdentityHashMap<>();

    /**
     * @param ownerTag  the full name of the owning element
     * @param wantedTag the full name of the elements to hand out
     */
    Ownership(XmlNode root, String ownerTag, String wantedTag) {
        // Document order, without recursion: each entry is an element and the nearest owner above it.
        List<XmlNode[]> pending = new ArrayList<>();
        pending.add(new XmlNode[] {root, null});
        while (!pending.isEmpty()) {
            XmlNode[] next = pending.remove(pending.size() - 1);
            XmlNode node = next[0];
            XmlNode owner = next[1];
            if (owner != null && node.is(wantedTag)) {
                owned.computeIfAbsent(owner, o -> new ArrayList<>()).add(node);
            }
            XmlNode below = node.is(ownerTag) ? node : owner;
            List<XmlNode> children = node.children();
            for (int i = children.size() - 1; i >= 0; i--) {
                pending.add(new XmlNode[] {children.get(i), below});
            }
        }
    }

    /** The elements that belong to this owner, in document order; empty when it has none. */
    List<XmlNode> of(XmlNode owner) {
        return owned.getOrDefault(owner, List.of());
    }
}
