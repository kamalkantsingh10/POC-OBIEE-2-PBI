package obi2pbi.in.docx;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import obi2pbi.in.xml.XmlNode;

/**
 * The paragraph styles of a Word document ({@code word/styles.xml}): which style is a heading of which level,
 * and which style carries list numbering. A style inherits what it does not set from the style it is based on.
 */
final class DocxStyles {

    private static final Pattern HEADING_NAME = Pattern.compile("heading ?([1-9])");

    private static final class Style {
        String name = "";
        String basedOn = "";
        int outlineLevel = -1;
        String numId = "";
        int numLevel = -1;
    }

    private final Map<String, Style> styles = new HashMap<>();

    static DocxStyles read(XmlNode root) {
        DocxStyles result = new DocxStyles();
        if (root == null) {
            return result;
        }
        for (XmlNode node : root.children()) {
            if (!DocxReader.isW(node, "style")) {
                continue;
            }
            String id = DocxReader.wAttr(node, "styleId");
            if (id.isEmpty()) {
                continue;
            }
            Style style = new Style();
            for (XmlNode part : node.children()) {
                if (DocxReader.isW(part, "name")) {
                    style.name = DocxReader.wAttr(part, "val");
                } else if (DocxReader.isW(part, "basedOn")) {
                    style.basedOn = DocxReader.wAttr(part, "val");
                } else if (DocxReader.isW(part, "pPr")) {
                    XmlNode outline = DocxReader.wChild(part, "outlineLvl");
                    if (outline != null) {
                        style.outlineLevel = DocxReader.number(DocxReader.wAttr(outline, "val"), -1);
                    }
                    XmlNode numPr = DocxReader.wChild(part, "numPr");
                    if (numPr != null) {
                        XmlNode numId = DocxReader.wChild(numPr, "numId");
                        XmlNode ilvl = DocxReader.wChild(numPr, "ilvl");
                        style.numId = numId == null ? "" : DocxReader.wAttr(numId, "val");
                        style.numLevel = ilvl == null ? 0 : DocxReader.number(DocxReader.wAttr(ilvl, "val"), 0);
                    }
                }
            }
            result.styles.put(id, style);
        }
        return result;
    }

    /**
     * The heading level (1 to 9) a paragraph of this style has, or 0. A style named "heading N" (Word's
     * built-in names) is level N and "Title" is level 1; without a name, a style id "HeadingN" is level N;
     * otherwise an outline level set on the style or a style it is based on.
     */
    int headingLevel(String styleId) {
        Set<String> seen = new HashSet<>();
        for (String id = styleId; !id.isEmpty() && seen.add(id); ) {
            Style style = styles.get(id);
            String name = style == null || style.name.isEmpty() ? id : style.name;
            String lower = name.toLowerCase(Locale.ROOT);
            Matcher heading = HEADING_NAME.matcher(lower);
            if (heading.matches()) {
                return Integer.parseInt(heading.group(1));
            }
            if (lower.equals("title")) {
                return 1;
            }
            if (style == null) {
                return 0;
            }
            if (style.outlineLevel >= 0 && style.outlineLevel < 9) {
                return style.outlineLevel + 1;
            }
            id = style.basedOn;
        }
        return 0;
    }

    /** The list numbering a style carries, as {numId, level}, or null. */
    String[] numbering(String styleId) {
        Set<String> seen = new HashSet<>();
        for (String id = styleId; !id.isEmpty() && seen.add(id); ) {
            Style style = styles.get(id);
            if (style == null) {
                return null;
            }
            if (!style.numId.isEmpty()) {
                return new String[] {style.numId, Integer.toString(Math.max(0, style.numLevel))};
            }
            id = style.basedOn;
        }
        return null;
    }
}
