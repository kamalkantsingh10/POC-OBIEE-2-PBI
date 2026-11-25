package obi2pbi.in.catalog;

/**
 * Text written the way the reference implementation writes it, so that error reasons are the same bytes.
 */
final class PythonText {

    private PythonText() {
    }

    /**
     * The text in quotes, as Python's {@code repr()} gives it: single quotes, or double quotes when the
     * text holds a single quote and no double quote; a backslash before the quote character and before a
     * backslash; {@code \n}, {@code \r}, {@code \t}; and a hex escape for any character that is not
     * printable (control and format characters, separators other than the space, unassigned code points).
     */
    static String repr(String text) {
        char quote = text.indexOf('\'') >= 0 && text.indexOf('"') < 0 ? '"' : '\'';
        StringBuilder out = new StringBuilder(text.length() + 2);
        out.append(quote);
        for (int i = 0; i < text.length();) {
            int c = text.codePointAt(i);
            i += Character.charCount(c);
            if (c == quote || c == '\\') {
                out.append('\\').appendCodePoint(c);
            } else if (c == '\n') {
                out.append("\\n");
            } else if (c == '\r') {
                out.append("\\r");
            } else if (c == '\t') {
                out.append("\\t");
            } else if (c >= 0x20 && c < 0x7F) {
                out.appendCodePoint(c);
            } else if (c < 0x7F || !printable(c)) {
                if (c < 0x100) {
                    out.append(String.format("\\x%02x", c));
                } else if (c < 0x10000) {
                    out.append(String.format("\\u%04x", c));
                } else {
                    out.append(String.format("\\U%08x", c));
                }
            } else {
                out.appendCodePoint(c);
            }
        }
        return out.append(quote).toString();
    }

    private static boolean printable(int codePoint) {
        switch (Character.getType(codePoint)) {
            case Character.CONTROL:
            case Character.FORMAT:
            case Character.SURROGATE:
            case Character.PRIVATE_USE:
            case Character.UNASSIGNED:
            case Character.LINE_SEPARATOR:
            case Character.PARAGRAPH_SEPARATOR:
            case Character.SPACE_SEPARATOR:
                return false;
            default:
                return true;
        }
    }
}
