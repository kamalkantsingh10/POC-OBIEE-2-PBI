package obi2pbi.core;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Row identifiers: {@code <estate>::<layer>::<part>.<part>...} (Architecture AD-4).
 *
 * <p>Parts are object names, parent first, written exactly as the source holds them. An identifier is never
 * built from a GUID, a file name, a timestamp or a hash.
 */
public final class Identifiers {

    public static final String SEPARATOR = "::";

    public static final String PHYSICAL = "phys";
    public static final String BUSINESS_MODEL = "bmm";
    public static final String PRESENTATION = "pres";
    public static final String SECURITY = "sec";
    public static final String VARIABLE = "var";
    public static final String CATALOG = "cat";

    public static final List<String> LAYERS =
            List.of(BUSINESS_MODEL, CATALOG, PHYSICAL, PRESENTATION, SECURITY, VARIABLE);

    private static final Pattern ESTATE_NAME = Pattern.compile("[a-z0-9]+(?:-[a-z0-9]+)*");

    private Identifiers() {
    }

    /** Lower-case letters and digits, groups joined by single hyphens. Returns the name when valid. */
    public static String validateEstateName(String estate) {
        if (estate == null || !ESTATE_NAME.matcher(estate).matches()) {
            throw new IdentifierException(
                    "estate name '" + estate + "' is not lower-case with hyphens. Use e.g. 'my-estate'. "
                            + "It is the estate folder's name and becomes the permanent prefix of every row "
                            + "identifier, so it must be stable and readable.");
        }
        return estate;
    }

    /**
     * Builds an identifier. Refuses an empty or all-space part, and a part containing {@code ::}, because
     * either would give an identifier that looks valid and cannot be read back.
     *
     * <p>A catalog object passes its whole decoded path as one part, since catalog names contain dots.
     */
    public static String makeId(String estate, String layer, String... parts) {
        validateEstateName(estate);
        if (!LAYERS.contains(layer)) {
            throw new IdentifierException("unknown layer '" + layer + "'; expected one of " + LAYERS);
        }
        if (parts.length == 0) {
            throw new IdentifierException(estate + SEPARATOR + layer + SEPARATOR + " has no object path");
        }
        for (int i = 0; i < parts.length; i++) {
            String part = parts[i];
            if (part == null || Text.isBlank(part)) {
                throw new IdentifierException(
                        estate + SEPARATOR + layer + SEPARATOR + " object path part " + i
                                + " is empty; parts=" + Arrays.toString(parts)
                                + ". Refusing to build an identifier that resolves to nothing.");
            }
            if (part.contains(SEPARATOR)) {
                throw new IdentifierException(
                        estate + SEPARATOR + layer + SEPARATOR + " object path part " + i + " contains '"
                                + SEPARATOR + "': '" + part + "'. That is the identifier separator, so the "
                                + "result would not parse back into the parts it was built from.");
            }
        }
        return estate + SEPARATOR + layer + SEPARATOR + String.join(".", parts);
    }

    /** Splits an identifier into estate, layer and object path. The object path is not split further. */
    public static String[] parseId(String rowId) {
        String[] pieces = rowId.split(Pattern.quote(SEPARATOR), -1);
        if (pieces.length != 3) {
            throw new IdentifierException(
                    "'" + rowId + "' is not a qualified identifier; expected <estate>" + SEPARATOR + "<layer>"
                            + SEPARATOR + "<object-path>");
        }
        validateEstateName(pieces[0]);
        if (!LAYERS.contains(pieces[1])) {
            throw new IdentifierException("'" + rowId + "' has unknown layer '" + pieces[1] + "'");
        }
        return pieces;
    }
}
