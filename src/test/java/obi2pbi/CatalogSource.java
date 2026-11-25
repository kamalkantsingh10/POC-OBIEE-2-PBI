package obi2pbi;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * What an estate's Web Catalog files say about the expressions in reports and saved filters, read here
 * with the JDK's DOM parser and <b>not</b> with the jar's reader. {@link KbCompare} uses it to check the
 * deliberate differences 8, 9 and 10 against the source instead of taking them on trust.
 *
 * <p>For one report or saved filter it works out, from the file alone:
 * <ul>
 * <li>the text each captured expression must be stored as: for a column or prompt formula the whole text
 * (DOM text content: all character data inside the element, nested elements included, comments left out);
 * for a filter expression its own text (the same with nested expression elements left out); and the text
 * before its first child element, which is what the reference implementation stores;</li>
 * <li>for each filter expression, its operator, type, path, the expression that encloses it and its
 * depth;</li>
 * <li>how many attributes and texts the reference implementation left unread that are read once
 * expressions are taken whole (difference 8) and every expression of a saved filter is a row
 * (difference 9).</li>
 * </ul>
 * The numbering rules (criteria blocks in document order, columns and filters under the nearest block,
 * formulas under the nearest column, one counter per block across its filters) are written out here a
 * second time on purpose.
 */
final class CatalogSource {

    static final String SAW = "com.siebel.analytics.web/report/v1.1";
    static final String SAWX = "com.siebel.analytics.web/expression/v1.1";
    static final String SAWSF = "com.siebel.analytics.web/savedfilter/v1.1";
    static final String XSI = "http://www.w3.org/2001/XMLSchema-instance";
    static final String REPORT = "{" + SAW + "}report";
    static final String SAVED_FILTER = "{" + SAWSF + "}savedFilterObject";
    static final String EXPR = "{" + SAWX + "}expr";

    /** One filter expression, as the source has it. */
    static final class Expr {
        /** The text a filter row must hold: the element's text with nested expressions left out. */
        String whole;
        String own;
        String op;
        String type;
        String path;
        /** The row suffix of the enclosing expression of the same filter, or empty. */
        String parent;
        int depth;
        boolean usesEval;
    }

    /** One count of content that is newly read, and the deliberate difference that makes it so. */
    static final class Gain {
        int count;
        final int difference;

        Gain(int difference) {
            this.difference = difference;
        }
    }

    /** What one report or saved filter file says. */
    static final class Facts {
        String rootTag = "";
        /** Row suffix ({@code #c0.1}, {@code #p0}; empty for a saved filter's own row) to whole and own text. */
        final Map<String, String[]> texts = new HashMap<>();
        /** Row suffix ({@code #f0.3} in a report, {@code #f3} in a saved filter) to the expression. */
        final Map<String, Expr> filterRows = new LinkedHashMap<>();
        /** {@code kind|detail} of an {@code unparsed} row to what is newly read in this object. */
        final Map<String, Gain> gains = new HashMap<>();
    }

    private static final Map<Path, CatalogSource> KNOWN = new HashMap<>();

    /** Decoded catalog path to the file; a path two files decode to is held with a null file. */
    private final Map<String, Path> files = new HashMap<>();
    private final Map<String, Facts> facts = new HashMap<>();
    private final DocumentBuilder parser;

    /** The catalog of an estate folder, or null when the folder has none. */
    static CatalogSource of(Path estateFolder) throws IOException {
        if (estateFolder == null || !Files.isDirectory(estateFolder.resolve("catalog"))) {
            return null;
        }
        Path root = estateFolder.resolve("catalog").toRealPath();
        CatalogSource known = KNOWN.get(root);
        if (known == null) {
            known = new CatalogSource(root);
            KNOWN.put(root, known);
        }
        return known;
    }

    private CatalogSource(Path root) throws IOException {
        List<Path> found;
        try (Stream<Path> walk = Files.walk(root)) {
            found = walk.filter(Files::isRegularFile).collect(Collectors.toList());
        }
        for (Path file : found) {
            StringBuilder decoded = new StringBuilder();
            boolean usable = true;
            for (Path segment : root.relativize(file)) {
                String name = segment.toString();
                if (name.equals(".git")) {
                    usable = false;
                    break;
                }
                decoded.append(decoded.length() == 0 ? "" : "/").append(decode(name));
            }
            if (usable) {
                String path = decoded.toString();
                files.put(path, files.containsKey(path) ? null : file);
            }
        }
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
            factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            parser = factory.newDocumentBuilder();
            parser.setErrorHandler(new org.xml.sax.helpers.DefaultHandler());
        } catch (javax.xml.parsers.ParserConfigurationException e) {
            throw new IOException(e);
        }
    }

    /**
     * A name on disk as the catalog name: {@code +} is a space, then {@code %XX} is a byte, and the bytes
     * are UTF-8. A {@code %} that is not followed by two hex digits stays as it is, and bytes that are not
     * UTF-8 become the replacement character, as in the reference implementation.
     */
    static String decode(String onDisk) {
        java.io.ByteArrayOutputStream bytes = new java.io.ByteArrayOutputStream();
        byte[] raw = onDisk.replace('+', ' ').getBytes(StandardCharsets.UTF_8);
        for (int i = 0; i < raw.length; i++) {
            if (raw[i] == '%' && i + 2 < raw.length && Character.digit(raw[i + 1], 16) >= 0
                    && Character.digit(raw[i + 2], 16) >= 0) {
                bytes.write(Character.digit(raw[i + 1], 16) * 16 + Character.digit(raw[i + 2], 16));
                i += 2;
            } else {
                bytes.write(raw[i]);
            }
        }
        return new String(bytes.toByteArray(), StandardCharsets.UTF_8);
    }

    /** What the object at this decoded path says; null when there is no such file or it cannot be parsed. */
    Facts object(String decodedPath) {
        if (facts.containsKey(decodedPath)) {
            return facts.get(decodedPath);
        }
        Facts read = null;
        Path file = files.get(decodedPath);
        if (file != null) {
            try (InputStream in = Files.newInputStream(file)) {
                read = read(parser.parse(in).getDocumentElement());
            } catch (IOException | org.xml.sax.SAXException e) {
                read = null;
            }
        }
        facts.put(decodedPath, read);
        return read;
    }

    // ------------------------------------------------------------------ reading one object

    private static Facts read(Element root) {
        Facts facts = new Facts();
        facts.rootTag = tag(root);
        if (facts.rootTag.equals(REPORT)) {
            readReport(root, facts);
        } else if (facts.rootTag.equals(SAVED_FILTER)) {
            readSavedFilter(root, facts);
        }
        return facts;
    }

    private static void readReport(Element root, Facts facts) {
        List<Element> all = below(root);
        all.add(0, root);
        // The expressions whose text both programs read; the reference only up to the first child element.
        Set<Element> taken = new HashSet<>();
        List<Element> blocks = named(all, SAW, "criteria");
        for (int i = 0; i < blocks.size(); i++) {
            Element block = blocks.get(i);
            int j = 0;
            for (Element column : named(all, SAW, "column")) {
                if (nearest(column, SAW, "criteria") != block) {
                    continue;
                }
                Element formula = null;
                for (Element candidate : named(below(column), SAW, "columnFormula")) {
                    if (nearest(candidate, SAW, "column") == column) {
                        formula = candidate;
                        break;
                    }
                }
                text(facts, "#c" + i + "." + j, formula == null ? null : child(formula, SAWX, "expr"), taken);
                j++;
            }
            int k = 0;
            for (Element holder : named(all, SAW, "filter")) {
                if (nearest(holder, SAW, "criteria") == block) {
                    k = filterRows(holder, "#f" + i + ".", k, facts, taken);
                }
            }
        }
        List<Element> prompts = named(below(root), SAW, "prompt");
        for (int m = 0; m < prompts.size(); m++) {
            if (!prompts.get(m).getAttributeNS(XSI, "type").isEmpty()) {
                Element formula = child(prompts.get(m), SAW, "formula");
                text(facts, "#p" + m, formula == null ? null : child(formula, SAWX, "expr"), taken);
            }
        }
        // Difference 8: text of elements inside a captured expression, which the reference never read.
        for (Element element : all) {
            if (!taken.contains(element) && !blank(ownText(element)) && inside(element, taken)) {
                facts.gains.computeIfAbsent("unread_text|" + tag(element) + "#text", key -> new Gain(8)).count++;
            }
        }
    }

    private static void readSavedFilter(Element root, Facts facts) {
        List<Element> holders = new ArrayList<>();
        for (Node node = root.getFirstChild(); node != null; node = node.getNextSibling()) {
            if (node instanceof Element && is((Element) node, SAW, "filter")) {
                holders.add((Element) node);
            }
        }
        Set<Element> rows = new HashSet<>();
        int k = 0;
        for (Element holder : holders) {
            k = filterRows(holder, "#f", k, facts, rows);
        }
        Element outer = holders.isEmpty() ? null : child(holders.get(0), SAWX, "expr");
        facts.texts.put("", outer == null ? new String[] {"", ""} : new String[] {textOutsideExpressions(outer), ownText(outer)});

        // What the reference read: of the outermost expression its operator, type and text; and the type of
        // every expression of the first filter up to the first database function call, which it looked for.
        Set<Element> typeRead = new HashSet<>();
        if (outer != null) {
            typeRead.add(outer);
        }
        if (!holders.isEmpty()) {
            for (Element expr : named(below(holders.get(0)), SAWX, "expr")) {
                typeRead.add(expr);
                if (expr.getAttributeNS(XSI, "type").equals("sawx:eval")) {
                    break;
                }
            }
        }
        // Difference 9: every expression is now a row, so its operator, path, type and text are read.
        for (Element expr : rows) {
            if (expr.hasAttribute("op") && expr != outer) {
                gain(facts, "unread_attribute|" + EXPR + "@op", 9);
            }
            if (expr.hasAttribute("path")) {
                gain(facts, "unread_attribute|" + EXPR + "@path", 9);
            }
            if (expr.hasAttributeNS(XSI, "type") && !typeRead.contains(expr)) {
                gain(facts, "unread_attribute|" + EXPR + "@{" + XSI + "}type", 9);
            }
        }
        List<Element> all = below(root);
        for (Element element : all) {
            if (element == outer || blank(ownText(element))) {
                continue;
            }
            if (rows.contains(element)) {
                gain(facts, "unread_text|" + tag(element) + "#text", 9);
            } else if (inside(element, rows)) {
                // Difference 8: taken as part of the whole text of the expression around it.
                gain(facts, "unread_text|" + tag(element) + "#text", 8);
            }
        }
    }

    private static void gain(Facts facts, String key, int difference) {
        facts.gains.computeIfAbsent(key, k -> new Gain(difference)).count++;
    }

    /** One row per expression below the filter element, in document order; returns the next number. */
    private static int filterRows(Element holder, String prefix, int next, Facts facts, Set<Element> taken) {
        Map<Element, String> suffixOf = new HashMap<>();
        Map<Element, Integer> depthOf = new HashMap<>();
        for (Element expr : named(below(holder), SAWX, "expr")) {
            Expr row = new Expr();
            row.whole = textOutsideExpressions(expr);
            row.own = ownText(expr);
            row.op = expr.getAttribute("op");
            row.type = expr.getAttributeNS(XSI, "type");
            row.path = expr.getAttribute("path");
            row.parent = "";
            for (Node up = expr.getParentNode(); up != holder && up != null; up = up.getParentNode()) {
                if (suffixOf.containsKey(up)) {
                    row.parent = suffixOf.get(up);
                    row.depth = depthOf.get(up) + 1;
                    break;
                }
            }
            row.usesEval = row.type.equals("sawx:eval");
            for (Element inner : named(below(expr), SAWX, "expr")) {
                row.usesEval = row.usesEval || inner.getAttributeNS(XSI, "type").equals("sawx:eval");
            }
            String suffix = prefix + next++;
            suffixOf.put(expr, suffix);
            depthOf.put(expr, row.depth);
            facts.filterRows.put(suffix, row);
            taken.add(expr);
        }
        return next;
    }

    private static void text(Facts facts, String suffix, Element expr, Set<Element> taken) {
        if (expr == null) {
            facts.texts.put(suffix, new String[] {"", ""});
            return;
        }
        facts.texts.put(suffix, new String[] {expr.getTextContent(), ownText(expr)});
        taken.add(expr);
    }

    // ------------------------------------------------------------------ small DOM tools

    static String tag(Element element) {
        String namespace = element.getNamespaceURI();
        return namespace == null || namespace.isEmpty() ? element.getLocalName() : "{" + namespace + "}" + element.getLocalName();
    }

    private static boolean is(Element element, String namespace, String local) {
        return namespace.equals(element.getNamespaceURI()) && local.equals(element.getLocalName());
    }

    /** Every element below this one, in document order; the element itself is not included. */
    private static List<Element> below(Element top) {
        List<Element> all = new ArrayList<>();
        Node node = top.getFirstChild();
        while (node != null) {
            if (node instanceof Element) {
                all.add((Element) node);
            }
            if (node.getFirstChild() != null) {
                node = node.getFirstChild();
                continue;
            }
            while (node != top && node.getNextSibling() == null) {
                node = node.getParentNode();
            }
            node = node == top ? null : node.getNextSibling();
        }
        return all;
    }

    private static List<Element> named(List<Element> elements, String namespace, String local) {
        List<Element> found = new ArrayList<>();
        for (Element element : elements) {
            if (is(element, namespace, local)) {
                found.add(element);
            }
        }
        return found;
    }

    /** The first enclosing element with this name, or null. */
    private static Element nearest(Element element, String namespace, String local) {
        for (Node up = element.getParentNode(); up instanceof Element; up = up.getParentNode()) {
            if (is((Element) up, namespace, local)) {
                return (Element) up;
            }
        }
        return null;
    }

    private static Element child(Element parent, String namespace, String local) {
        for (Node node = parent.getFirstChild(); node != null; node = node.getNextSibling()) {
            if (node instanceof Element && is((Element) node, namespace, local)) {
                return (Element) node;
            }
        }
        return null;
    }

    private static boolean inside(Element element, Set<Element> outer) {
        for (Node up = element.getParentNode(); up instanceof Element; up = up.getParentNode()) {
            if (outer.contains(up)) {
                return true;
            }
        }
        return false;
    }

    /** The character data before the first child element: what the reference implementation calls text. */
    static String ownText(Element element) {
        StringBuilder text = new StringBuilder();
        for (Node node = element.getFirstChild(); node != null && !(node instanceof Element); node = node.getNextSibling()) {
            if (node.getNodeType() == Node.TEXT_NODE || node.getNodeType() == Node.CDATA_SECTION_NODE) {
                text.append(node.getNodeValue());
            }
        }
        return text.toString();
    }

    /**
     * The text of a filter expression that is its own: every text node inside the element that is not
     * inside a nested expression element, in document order. Text after a nested expression is kept.
     */
    static String textOutsideExpressions(Element expr) {
        StringBuilder text = new StringBuilder();
        collect(expr, text);
        return text.toString();
    }

    private static void collect(Element element, StringBuilder text) {
        for (Node node = element.getFirstChild(); node != null; node = node.getNextSibling()) {
            if (node.getNodeType() == Node.TEXT_NODE || node.getNodeType() == Node.CDATA_SECTION_NODE) {
                text.append(node.getNodeValue());
            } else if (node instanceof Element && !is((Element) node, SAWX, "expr")) {
                collect((Element) node, text);
            }
        }
    }

    private static boolean blank(String text) {
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (!Character.isWhitespace(c) && c != ' ' && c != '\u0085') {
                return false;
            }
        }
        return true;
    }
}
