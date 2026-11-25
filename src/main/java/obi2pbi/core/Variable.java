package obi2pbi.core;

import java.util.regex.Pattern;

/**
 * A repository or session variable.
 *
 * <p>{@link #redacted} marks a variable whose value was withheld: because its name suggests a credential, or
 * because its value is the same as the value of such a variable. The row is still written, so a reader knows the variable exists, and the withholding is recorded as a
 * known gap. A withheld value is never held in this object: {@link #expression} is then empty.
 */
public final class Variable implements Row {

    /**
     * Variable names whose value must never reach the Knowledge Base. Broad on purpose: withholding too
     * much costs a lookup in the source, withholding too little writes a credential to disk.
     */
    private static final Pattern CREDENTIAL_NAME = Pattern.compile(
            "password|passwd|pwd|secret|token|credential|apikey|api[_ -]?key",
            Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);

    public final String rowId;
    public final String rpdId;
    public final String name;
    public final String isSession;
    public final String initBlockId;
    public final String securityMask;
    public final boolean redacted;
    /** The default value in its three forms; empty when {@link #redacted}. */
    public final Expression expression;

    public Variable(String rowId, String rpdId, String name, String isSession, String initBlockId,
            String securityMask, boolean redacted, Expression expression) {
        this.rowId = rowId;
        this.rpdId = rpdId;
        this.name = name;
        this.isSession = isSession;
        this.initBlockId = initBlockId;
        this.securityMask = securityMask;
        this.redacted = redacted;
        // Enforced here, so no caller can put a withheld value into the model by mistake.
        this.expression = redacted ? Expression.EMPTY : expression;
    }

    /** True when the name, anywhere in it and in any letter case, suggests the value is a credential. */
    public static boolean isCredentialName(String name) {
        return name != null && CREDENTIAL_NAME.matcher(name).find();
    }

    /** The detail of the known-gap row for a value withheld because of the variable's own name. */
    public static String redactionDetail(String name) {
        return name + " \u2014 value withheld: the name indicates a credential (NFR11)";
    }

    /**
     * The detail of the known-gap row for a value withheld because it is the same as a value withheld for
     * its name (deliberate difference 19): the variable's own name gave no warning.
     */
    public static String sameValueDetail(String name) {
        return name + " \u2014 value withheld: it is the same as the value of a variable whose name indicates "
                + "a credential";
    }

    @Override
    public String rowId() {
        return rowId;
    }

    @Override
    public String rpdId() {
        return rpdId;
    }
}
