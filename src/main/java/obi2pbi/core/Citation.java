package obi2pbi.core;

import java.util.ArrayList;
import java.util.List;

/**
 * The one citation form (Architecture AD-12): {@code [kb:<row identifier>]}.
 *
 * <p>The rule that makes it unambiguous to read back: inside a citation a {@code ]} that belongs to the
 * identifier is written {@code \]}, a backslash is written {@code \\}, a line feed {@code \n} and a carriage
 * return {@code \r}. Nothing else is changed, spaces included. A citation therefore ends at the first
 * {@code ]} that has no backslash before it, and never runs past the end of its line.
 */
public final class Citation {

    public static final String OPEN = "[kb:";

    /** The identifier found, and where: line and column count from 1. */
    public final String rowId;
    public final int line;
    public final int column;
    /** The text after the citation on its line. */
    public final String rest;

    private Citation(String rowId, int line, int column, String rest) {
        this.rowId = rowId;
        this.line = line;
        this.column = column;
        this.rest = rest;
    }

    /** The citation of a row, as a packet prints it and a document must repeat it. */
    public static String of(String rowId) {
        return OPEN + escape(rowId) + "]";
    }

    public static String escape(String rowId) {
        StringBuilder out = new StringBuilder(rowId.length() + 2);
        for (int i = 0; i < rowId.length(); i++) {
            char c = rowId.charAt(i);
            if (c == '\\') {
                out.append("\\\\");
            } else if (c == ']') {
                out.append("\\]");
            } else if (c == '\n') {
                out.append("\\n");
            } else if (c == '\r') {
                out.append("\\r");
            } else {
                out.append(c);
            }
        }
        return out.toString();
    }

    /** True when the identifier is written in a citation exactly as it is, with nothing escaped. */
    public static boolean isPlain(String rowId) {
        return escape(rowId).equals(rowId);
    }

    /**
     * Every citation in one line of text, left to right. A {@code [kb:} with no closing {@code ]} on the
     * line is returned with a null identifier, so the caller can report it.
     */
    public static List<Citation> find(String text, int lineNumber) {
        List<Citation> found = new ArrayList<>();
        int at = text.indexOf(OPEN);
        while (at >= 0) {
            StringBuilder id = new StringBuilder();
            int i = at + OPEN.length();
            boolean closed = false;
            while (i < text.length()) {
                char c = text.charAt(i);
                if (c == '\\' && i + 1 < text.length()) {
                    char next = text.charAt(i + 1);
                    id.append(next == 'n' ? '\n' : next == 'r' ? '\r' : next);
                    i += 2;
                    continue;
                }
                if (c == ']') {
                    closed = true;
                    break;
                }
                id.append(c);
                i++;
            }
            if (!closed) {
                found.add(new Citation(null, lineNumber, at + 1, ""));
                break;
            }
            found.add(new Citation(id.toString(), lineNumber, at + 1, text.substring(i + 1)));
            at = text.indexOf(OPEN, i + 1);
        }
        return found;
    }
}
