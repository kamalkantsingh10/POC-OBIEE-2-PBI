package obi2pbi.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Finds the variables an expression names. OBIEE reads a variable with {@code VALUEOF(name)},
 * {@code VALUEOF("name")}, {@code VALUEOF(NQ_SESSION.name)} or {@code VALUEOF(NQ_SESSION."name")}.
 *
 * <p>Only these forms are read, and the name is returned exactly as written, so that a variable is matched
 * by its whole name and never by a piece of it (deliberate difference 13).
 *
 * <p>What the sample Knowledge Bases hold, and how it is read: the word {@code VALUEOF} and the prefix
 * {@code NQ_SESSION} in any letter case ({@code valueof(NQ_SESSION.ROLEGUIDS)}); white space after the
 * word, inside the brackets and around the dot; a number and a comma before the name
 * ({@code valueof(0,NQ_SESSION.LEDGER)}), which are passed over. A name written {@code %1}, {@code %2} ...
 * is not a name: it is a placeholder of a formula template, whose reference list names the variable.
 */
public final class Variables {

    private static final String CALL = "VALUEOF";
    private static final String SESSION = "NQ_SESSION";

    private Variables() {
    }

    /** The names inside every {@code VALUEOF(...)} of the text, in order, repeats included. */
    public static List<String> namesIn(String text) {
        List<String> names = new ArrayList<>();
        String upper = text.toUpperCase(Locale.ROOT);
        if (upper.length() != text.length()) {
            // A letter whose upper-case form is longer; positions would not line up. Compare in place.
            upper = text;
        }
        int at = upper.indexOf(CALL);
        while (at >= 0) {
            int i = skipSpaces(text, at + CALL.length());
            if (i < text.length() && text.charAt(i) == '(') {
                i = skipSpaces(text, i + 1);
                if (upper.startsWith(SESSION, i)) {
                    int dot = skipSpaces(text, i + SESSION.length());
                    if (dot < text.length() && text.charAt(dot) == '.') {
                        i = skipSpaces(text, dot + 1);
                    }
                }
                // A leading number and comma, as in VALUEOF(0, NQ_SESSION.NAME).
                int digits = i;
                while (digits < text.length() && text.charAt(digits) >= '0' && text.charAt(digits) <= '9') {
                    digits++;
                }
                int comma = skipSpaces(text, digits);
                if (digits > i && comma < text.length() && text.charAt(comma) == ',') {
                    i = skipSpaces(text, comma + 1);
                }
                if (upper.startsWith(SESSION, i)) {
                    int dot = skipSpaces(text, i + SESSION.length());
                    if (dot < text.length() && text.charAt(dot) == '.') {
                        i = skipSpaces(text, dot + 1);
                    }
                }
                if (i < text.length() && text.charAt(i) == '"') {
                    int close = text.indexOf('"', i + 1);
                    if (close > i + 1 && !isPlaceholder(text.substring(i + 1, close))) {
                        names.add(text.substring(i + 1, close));
                    }
                } else {
                    int end = i;
                    while (end < text.length() && text.charAt(end) != ')' && text.charAt(end) != ','
                            && !Text.isWhitespace(text.charAt(end))) {
                        end++;
                    }
                    if (end > i && !isPlaceholder(text.substring(i, end))) {
                        names.add(text.substring(i, end));
                    }
                }
            }
            at = upper.indexOf(CALL, at + CALL.length());
        }
        return names;
    }

    /** True for {@code %1}, {@code %2} ...: a placeholder of a formula template, not a name. */
    private static boolean isPlaceholder(String name) {
        if (name.length() < 2 || name.charAt(0) != '%') {
            return false;
        }
        for (int i = 1; i < name.length(); i++) {
            if (name.charAt(i) < '0' || name.charAt(i) > '9') {
                return false;
            }
        }
        return true;
    }

    /**
     * The variables a name stands for, out of {@code idsByName} (name exactly as the repository has it, to
     * Row identifiers): the ones with exactly this name; when there is none, the ones whose name differs
     * from it only in letter case. OBIEE itself does not tell variable names apart by letter case, so the
     * second match is real, and a caller that prints it says that the letter case differs.
     */
    public static List<String> idsFor(String name, java.util.Map<String, List<String>> idsByName) {
        List<String> exact = idsByName.get(name);
        if (exact != null) {
            return exact;
        }
        List<String> folded = new ArrayList<>();
        for (java.util.Map.Entry<String, List<String>> known : idsByName.entrySet()) {
            if (known.getKey().equalsIgnoreCase(name)) {
                folded.addAll(known.getValue());
            }
        }
        return folded;
    }

    private static int skipSpaces(String text, int from) {
        int i = from;
        while (i < text.length() && Text.isWhitespace(text.charAt(i))) {
            i++;
        }
        return i;
    }
}
