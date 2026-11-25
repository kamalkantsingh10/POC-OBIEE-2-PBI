package obi2pbi.in.mds;

import obi2pbi.core.Expression;
import obi2pbi.core.ExtractionError;
import obi2pbi.core.Identifiers;
import obi2pbi.core.Text;
import obi2pbi.core.Unparsed;
import obi2pbi.core.Variable;
import obi2pbi.in.xml.XmlNode;

/**
 * Reads {@code Variable} files: repository and session variables with their default value.
 *
 * <p><b>Credentials.</b> A value is withheld in two cases:
 * <ul>
 * <li>The variable's name suggests a credential ({@link Variable#isCredentialName}). This is the reference
 * implementation's rule.</li>
 * <li>The value is the same as one withheld by the first rule (deliberate difference 19). The samples have
 * a user-name variable holding the very value of the password variable beside it; the name rule alone
 * writes that value out.</li>
 * </ul>
 * For a withheld value the three expression cells are empty, {@code redacted} is true, and
 * {@code unparsed.csv} gets a {@code redacted_value} row saying which rule applied. The row itself is still
 * written. A credential under an innocent name whose value occurs nowhere under a telling name is not
 * recognised and is written out; so is a credential inside an init block's query.
 *
 * <p>Not read, and therefore reported in {@code unparsed.csv}: the description.
 */
public final class VariableReader implements ObjectReader {

    private static final String VARIABLE = "Variable";

    @Override
    public String objectType() {
        return VARIABLE;
    }

    @Override
    public void bind(XmlNode root, ReadContext context) {
        String name = root.attr("name");
        if (name.isEmpty()) {
            context.error(ExtractionError.MISSING_NAME, ReadContext.sourceIdOf(root), VARIABLE, "no name");
            return;
        }
        // Before any row is produced: every variable file passes through here first, so by the second phase
        // every value withheld for its name is known, whatever the reading order.
        if (Variable.isCredentialName(name)) {
            XmlNode value = root.firstChild("Expr");
            if (value != null) {
                for (XmlNode part : value.children()) {
                    if (part.is("ExprText") || part.is("ExprTextDesc")) {
                        String text = Text.strip(part.text());
                        if (!text.isEmpty()) {
                            context.withheldValues.add(text);
                        }
                    }
                }
            }
        }
        String rowId = context.makeId(root, VARIABLE, Identifiers.VARIABLE, name);
        if (rowId != null) {
            context.index.bind(root, rowId);
        }
    }

    @Override
    public void read(XmlNode root, ReadContext context) {
        String rowId = context.index.identifierOf(root);
        if (rowId == null) {
            return;
        }
        String name = root.attr("name");
        // Read even when it is about to be withheld: a reference in it that does not resolve is still
        // reported, and the audit must see the value as read, not as a gap.
        Expression expression = Expressions.read(root.firstChild("Expr"), context, rowId);
        boolean named = Variable.isCredentialName(name);
        boolean redacted = named || context.withheldValues.contains(Text.strip(expression.text))
                || context.withheldValues.contains(Text.strip(expression.textDesc));
        if (redacted) {
            expression = Expression.EMPTY;
            context.estate.recordGap(context.rpdId, Unparsed.REDACTED_VALUE, VARIABLE,
                    named ? Variable.redactionDetail(name) : Variable.sameValueDetail(name), 1);
        }
        context.estate.variables.add(new Variable(
                rowId, context.rpdId, name, root.attr("isSessionVar"),
                context.index.resolve(root.attr("initBlockRef")), root.attr("securityMask"), redacted,
                expression));
        context.parsed(VARIABLE);
    }
}
