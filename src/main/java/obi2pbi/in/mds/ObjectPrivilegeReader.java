package obi2pbi.in.mds;

import java.util.ArrayList;
import java.util.List;
import obi2pbi.core.Expression;
import obi2pbi.core.Identifiers;
import obi2pbi.core.SecurityRule;
import obi2pbi.in.xml.XmlNode;

/**
 * Reads {@code ObjectPrivilege} files: a permission (the source's own word, for example {@code read},
 * {@code write} or {@code none}) that a privilege package holds on one or more objects. One
 * {@code security_rls} row of kind {@code object_privilege} per object.
 *
 * <p>The role is not on these rows. It is reached through the package named in {@code detail}. A
 * privilege that lists no object still gives one row, with the object empty. An object or a package that
 * is not found leaves its place empty; that is not an error.
 *
 * <p>An object privilege is given no identifier that others can refer to, so everything happens in
 * {@link #read}.
 */
public final class ObjectPrivilegeReader implements ObjectReader {

    private static final String PRIVILEGE = "ObjectPrivilege";

    @Override
    public String objectType() {
        return PRIVILEGE;
    }

    @Override
    public void bind(XmlNode root, ReadContext context) {
        // Nothing refers to an object privilege.
    }

    @Override
    public void read(XmlNode root, ReadContext context) {
        String name = PrivilegePackageReader.nameOrMark(root);
        String packageId = context.index.resolve(root.attr("privilegePackageRef"));
        XmlNode targets = root.firstChild("RefObjects");
        List<XmlNode> refs = targets == null ? List.of() : targets.children("RefObject");
        List<String> subjects = new ArrayList<>();
        for (XmlNode ref : refs) {
            subjects.add(context.index.resolve(ref.attr("objectRef")));
        }
        if (subjects.isEmpty()) {
            subjects.add("");
        }
        String detail = root.attr("type") + " via " + packageId;
        for (int position = 0; position < subjects.size(); position++) {
            String rowId = context.makeId(root, PRIVILEGE, Identifiers.SECURITY, "obj", name, "o" + position);
            if (rowId == null) {
                // The name cannot be part of an identifier; said once, and the privilege is left out.
                return;
            }
            context.estate.securityRules.add(new SecurityRule(
                    rowId, context.rpdId, SecurityRule.OBJECT_PRIVILEGE, "", subjects.get(position), detail,
                    Expression.EMPTY));
        }
        context.parsed(PRIVILEGE);
    }
}
