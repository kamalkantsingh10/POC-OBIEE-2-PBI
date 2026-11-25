package obi2pbi.core;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Finds the time-series functions an expression uses. A text search for the function name followed by an
 * opening bracket, in any letter case; the formula is not parsed, so a quoted name that looks like a call
 * also matches.
 */
public final class TimeSeries {

    /** The functions looked for, in the order they are reported. */
    public static final List<String> FUNCTIONS = List.of("AGO", "TODATE", "PERIODROLLING");

    private static final List<Pattern> PATTERNS = patterns();

    private TimeSeries() {
    }

    private static List<Pattern> patterns() {
        List<Pattern> patterns = new ArrayList<>();
        for (String name : FUNCTIONS) {
            // The Unicode flags make \b, \s and the case folding behave as the reference implementation's do.
            patterns.add(Pattern.compile("\\b" + name + "\\s*\\(",
                    Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE | Pattern.UNICODE_CHARACTER_CLASS));
        }
        return patterns;
    }

    /** The functions found in the template text or the rendered text of any of the expressions. */
    public static List<String> used(Expression... expressions) {
        StringBuilder blob = new StringBuilder();
        for (int i = 0; i < expressions.length; i++) {
            if (i > 0) {
                blob.append(' ');
            }
            blob.append(expressions[i].text).append(' ').append(expressions[i].textDesc);
        }
        List<String> found = new ArrayList<>();
        for (int i = 0; i < FUNCTIONS.size(); i++) {
            if (PATTERNS.get(i).matcher(blob).find()) {
                found.add(FUNCTIONS.get(i));
            }
        }
        return found;
    }
}
