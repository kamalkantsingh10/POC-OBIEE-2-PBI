package obi2pbi.in.mds;

import java.util.List;
import obi2pbi.core.Expression;
import obi2pbi.core.Identifiers;
import obi2pbi.core.SecurityRule;
import obi2pbi.in.xml.XmlNode;

/**
 * Reads {@code PrivilegePackage} files: the link from a role to its query privileges. One
 * {@code security_rls} row of kind {@code query_privilege} per query privilege in the package.
 *
 * <p>On these rows the role and the query privilege are written as <b>names</b>, looked up in the index;
 * neither is a Row identifier (a query privilege has no reader). A package with no query privilege gives
 * no row, so its role is then in no table; that is a known limit of the reference implementation, kept.
 *
 * <p>The role's display name and guid are taken from the role's own object (deliberate difference 27).
 *
 * <p>Not read, and therefore reported in {@code unparsed.csv}: a grant made to a user instead of a role,
 * and the database a query privilege applies to.
 */
public final class PrivilegePackageReader implements ObjectReader {

    private static final String PACKAGE = "PrivilegePackage";

    @Override
    public String objectType() {
        return PACKAGE;
    }

    @Override
    public void bind(XmlNode root, ReadContext context) {
        // A package with no name is still given an identifier, with "?" for the name.
        String rowId = context.makeId(root, PACKAGE, Identifiers.SECURITY, "pkg", nameOrMark(root));
        if (rowId != null) {
            context.index.bind(root, rowId);
        }
    }

    @Override
    public void read(XmlNode root, ReadContext context) {
        String packageId = context.index.identifierOf(root);
        if (packageId == null) {
            return;
        }
        String name = root.attr("name");
        String roleRef = root.attr("roleRef");
        String role = nameOf(roleRef, context);
        List<XmlNode> mappings = root.children("QueryPrivilegeMapping");
        // Difference 27: what the role's own object calls it. A role's name can be an opaque text (the
        // sample has one that is two long hexadecimal strings), so the display name and guid are taken
        // too, and only for a role a row is written for.
        String displayName = "";
        String guid = "";
        XmlNode group = mappings.isEmpty() ? null : context.index.node(ReferenceIndex.mdsidOf(roleRef));
        if (group != null && group.is("Group")) {
            displayName = group.attr("displayName");
            guid = group.attr("guid");
        }
        int position = 0;
        for (XmlNode mapping : mappings) {
            context.estate.securityRules.add(new SecurityRule(
                    packageId + ".q" + position, context.rpdId, SecurityRule.QUERY_PRIVILEGE, role,
                    nameOf(mapping.attr("queryPrivilegeRef"), context), name, Expression.EMPTY, displayName,
                    guid));
            position++;
        }
        context.parsed(PACKAGE);
    }

    static String nameOrMark(XmlNode root) {
        String name = root.attr("name");
        return name.isEmpty() ? "?" : name;
    }

    /** The name of the object a reference points at, or empty when it is not found. */
    private static String nameOf(String reference, ReadContext context) {
        ReferenceIndex.Entry entry = context.index.entry(ReferenceIndex.mdsidOf(reference));
        return entry == null ? "" : entry.name;
    }
}
