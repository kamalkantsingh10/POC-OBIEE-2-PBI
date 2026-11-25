package obi2pbi.core;

import java.util.Comparator;

/**
 * Text rules shared by the whole jar: the sort order of the Knowledge Base and the test for "only spaces".
 *
 * <p>Both follow the Python reference, not Java's defaults. Java compares strings by UTF-16 code unit and has
 * a narrower idea of white space; either default would make the output differ from the reference for rare
 * characters.
 */
public final class Text {

    /** Orders strings by Unicode code point, as Python does. */
    public static final Comparator<String> CODE_POINT_ORDER = Text::compareCodePoints;

    /** Orders rows cell by cell, left to right, each cell by code point; a shorter row sorts first. */
    public static final Comparator<String[]> ROW_ORDER = Text::compareRows;

    private Text() {
    }

    public static int compareCodePoints(String left, String right) {
        int i = 0;
        int j = 0;
        while (i < left.length() && j < right.length()) {
            int a = left.codePointAt(i);
            int b = right.codePointAt(j);
            if (a != b) {
                return a < b ? -1 : 1;
            }
            i += Character.charCount(a);
            j += Character.charCount(b);
        }
        return Integer.compare(left.length() - i, right.length() - j);
    }

    public static int compareRows(String[] left, String[] right) {
        int shared = Math.min(left.length, right.length);
        for (int i = 0; i < shared; i++) {
            int order = compareCodePoints(left[i], right[i]);
            if (order != 0) {
                return order;
            }
        }
        return Integer.compare(left.length, right.length);
    }

    /** True when the text is empty or holds only white space, by Python's definition of white space. */
    public static boolean isBlank(String text) {
        for (int i = 0; i < text.length(); i++) {
            if (!isWhitespace(text.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /** The text without leading and trailing white space, by the same definition of white space. */
    public static String strip(String text) {
        int start = 0;
        int end = text.length();
        while (start < end && isWhitespace(text.charAt(start))) {
            start++;
        }
        while (end > start && isWhitespace(text.charAt(end - 1))) {
            end--;
        }
        return text.substring(start, end);
    }

    /** The characters Python's {@code str.strip()} removes. Wider than {@code Character.isWhitespace}. */
    public static boolean isWhitespace(char c) {
        return (c >= 0x09 && c <= 0x0D)
                || (c >= 0x1C && c <= 0x20)
                || c == 0x85
                || c == 0xA0
                || c == 0x1680
                || (c >= 0x2000 && c <= 0x200A)
                || c == 0x2028
                || c == 0x2029
                || c == 0x202F
                || c == 0x205F
                || c == 0x3000;
    }
}
