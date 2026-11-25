package obi2pbi.in.mds;

import obi2pbi.core.Identifiers;
import obi2pbi.in.xml.XmlNode;

/**
 * Reads {@code Group} files (application roles). A role has no row of its own: it is given an identifier
 * so that references to it resolve, and it is counted. Its name reaches {@code security_rls.role} through
 * the privilege packages that point at it.
 *
 * <p>A role with no name is passed over without an error, as in the reference implementation.
 *
 * <p>The display name and guid of a role that holds a privilege package are read by
 * {@link PrivilegePackageReader}, which follows the package's link to the role (deliberate difference 27).
 *
 * <p>Not read, and therefore reported in {@code unparsed.csv}: the display name and guid of a role no
 * privilege package with a query privilege points at, the description and the roles nested inside a role.
 */
public final class GroupReader implements ObjectReader {

    private static final String GROUP = "Group";

    @Override
    public String objectType() {
        return GROUP;
    }

    @Override
    public void bind(XmlNode root, ReadContext context) {
        String name = root.attr("name");
        if (name.isEmpty()) {
            return;
        }
        String rowId = context.makeId(root, GROUP, Identifiers.SECURITY, name);
        if (rowId != null) {
            context.index.bind(root, rowId);
        }
    }

    @Override
    public void read(XmlNode root, ReadContext context) {
        if (context.index.identifierOf(root) != null) {
            context.parsed(GROUP);
        }
    }
}
