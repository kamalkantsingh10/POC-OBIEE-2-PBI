package obi2pbi.in.docx;

import java.util.HashMap;
import java.util.Map;
import obi2pbi.in.xml.XmlNode;

/**
 * The list definitions of a Word document ({@code word/numbering.xml}), and the counters that turn them into
 * the marker a reader sees: {@code -} for a bullet, {@code 1.}, {@code a)} or {@code 2.1.} for a number,
 * following each level's number format and level text.
 */
final class DocxNumbering {

    private static final class Level {
        String format = "decimal";
        String text = "";
        int start = 1;
    }

    /** abstractNumId to its levels. */
    private final Map<String, Map<Integer, Level>> abstracts = new HashMap<>();
    /** numId to abstractNumId. */
    private final Map<String, String> abstractOf = new HashMap<>();
    /** numId to level to the start value an override sets. */
    private final Map<String, Map<Integer, Integer>> startOverrides = new HashMap<>();
    /** numId to the current counter of each level; 0 means not yet used. */
    private final Map<String, int[]> counters = new HashMap<>();

    static DocxNumbering read(XmlNode root) {
        DocxNumbering result = new DocxNumbering();
        if (root == null) {
            return result;
        }
        for (XmlNode node : root.children()) {
            if (DocxReader.isW(node, "abstractNum")) {
                Map<Integer, Level> levels = new HashMap<>();
                for (XmlNode lvl : node.children()) {
                    if (!DocxReader.isW(lvl, "lvl")) {
                        continue;
                    }
                    Level level = new Level();
                    XmlNode format = DocxReader.wChild(lvl, "numFmt");
                    XmlNode text = DocxReader.wChild(lvl, "lvlText");
                    XmlNode start = DocxReader.wChild(lvl, "start");
                    if (format != null) {
                        level.format = DocxReader.wAttr(format, "val");
                    }
                    if (text != null) {
                        level.text = DocxReader.wAttr(text, "val");
                    }
                    if (start != null) {
                        level.start = DocxReader.number(DocxReader.wAttr(start, "val"), 1);
                    }
                    levels.put(DocxReader.number(DocxReader.wAttr(lvl, "ilvl"), 0), level);
                }
                result.abstracts.put(DocxReader.wAttr(node, "abstractNumId"), levels);
            } else if (DocxReader.isW(node, "num")) {
                String numId = DocxReader.wAttr(node, "numId");
                XmlNode abstractId = DocxReader.wChild(node, "abstractNumId");
                if (abstractId != null) {
                    result.abstractOf.put(numId, DocxReader.wAttr(abstractId, "val"));
                }
                for (XmlNode override : node.children()) {
                    if (DocxReader.isW(override, "lvlOverride")) {
                        XmlNode start = DocxReader.wChild(override, "startOverride");
                        if (start != null) {
                            result.startOverrides.computeIfAbsent(numId, k -> new HashMap<>()).put(
                                    DocxReader.number(DocxReader.wAttr(override, "ilvl"), 0),
                                    DocxReader.number(DocxReader.wAttr(start, "val"), 1));
                        }
                    }
                }
            }
        }
        return result;
    }

    /** True when the list is a bullet list at this level (or unknown, so shown as a bullet). */
    boolean isBullet(String numId, int level) {
        Level definition = level(numId, level);
        return definition == null || definition.format.equals("bullet") || definition.format.equals("none");
    }

    /** Counts one more item of the list at this level and returns its marker. */
    String next(String numId, int level) {
        int depth = Math.max(0, Math.min(8, level));
        int[] count = counters.computeIfAbsent(numId, k -> new int[9]);
        count[depth] = count[depth] == 0 ? start(numId, depth) : count[depth] + 1;
        for (int deeper = depth + 1; deeper < 9; deeper++) {
            count[deeper] = 0;
        }
        if (isBullet(numId, depth)) {
            return "-";
        }
        Level definition = level(numId, depth);
        String text = definition.text.isEmpty() ? "%" + (depth + 1) + "." : definition.text;
        StringBuilder marker = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == '%' && i + 1 < text.length() && text.charAt(i + 1) >= '1' && text.charAt(i + 1) <= '9') {
                int which = text.charAt(i + 1) - '1';
                Level other = level(numId, which);
                int value = count[which] == 0 ? start(numId, which) : count[which];
                marker.append(format(value, other == null ? "decimal" : other.format));
                i++;
            } else {
                marker.append(c);
            }
        }
        String result = marker.toString().trim();
        return result.isEmpty() ? "-" : result;
    }

    private Level level(String numId, int level) {
        Map<Integer, Level> levels = abstracts.get(abstractOf.getOrDefault(numId, ""));
        return levels == null ? null : levels.get(level);
    }

    private int start(String numId, int level) {
        Map<Integer, Integer> overrides = startOverrides.get(numId);
        if (overrides != null && overrides.containsKey(level)) {
            return overrides.get(level);
        }
        Level definition = level(numId, level);
        return definition == null ? 1 : definition.start;
    }

    static String format(int value, String format) {
        switch (format) {
            case "lowerLetter":
                return letters(value).toLowerCase(java.util.Locale.ROOT);
            case "upperLetter":
                return letters(value);
            case "lowerRoman":
                return roman(value).toLowerCase(java.util.Locale.ROOT);
            case "upperRoman":
                return roman(value);
            default:
                return Integer.toString(value);
        }
    }

    /** Word's letters: A..Z, then AA..ZZ, then AAA... */
    private static String letters(int value) {
        if (value < 1) {
            return Integer.toString(value);
        }
        char letter = (char) ('A' + (value - 1) % 26);
        return String.valueOf(letter).repeat((value - 1) / 26 + 1);
    }

    private static String roman(int value) {
        if (value < 1 || value > 3999) {
            return Integer.toString(value);
        }
        int[] values = {1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1};
        String[] symbols = {"M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I"};
        StringBuilder text = new StringBuilder();
        int rest = value;
        for (int i = 0; i < values.length; i++) {
            while (rest >= values[i]) {
                text.append(symbols[i]);
                rest -= values[i];
            }
        }
        return text.toString();
    }
}
