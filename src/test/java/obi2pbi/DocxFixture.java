package obi2pbi;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Builds {@code .docx} files for tests from XML strings (story 3.1, Design Notes: the repository holds no
 * binary file that cannot be reviewed). A {@code .docx} is a zip archive of XML parts; this helper writes the
 * parts given, adding the package parts Word needs ({@code [Content_Types].xml} and the relationships) when
 * they are not given. Entries are written in name order with a fixed time, so the same parts give the same bytes.
 */
public final class DocxFixture {

    public static final String W = "http://schemas.openxmlformats.org/wordprocessingml/2006/main";

    /** Styles with Word's built-in names: Normal, Title, heading 1 to 3, List Paragraph. */
    public static final String STYLES = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n"
            + "<w:styles xmlns:w=\"" + W + "\">\n"
            + "  <w:style w:type=\"paragraph\" w:default=\"1\" w:styleId=\"Normal\"><w:name w:val=\"Normal\"/></w:style>\n"
            + "  <w:style w:type=\"paragraph\" w:styleId=\"Title\"><w:name w:val=\"Title\"/><w:basedOn w:val=\"Normal\"/></w:style>\n"
            + "  <w:style w:type=\"paragraph\" w:styleId=\"Heading1\"><w:name w:val=\"heading 1\"/><w:basedOn w:val=\"Normal\"/>"
            + "<w:pPr><w:outlineLvl w:val=\"0\"/></w:pPr></w:style>\n"
            + "  <w:style w:type=\"paragraph\" w:styleId=\"Heading2\"><w:name w:val=\"heading 2\"/><w:basedOn w:val=\"Normal\"/>"
            + "<w:pPr><w:outlineLvl w:val=\"1\"/></w:pPr></w:style>\n"
            + "  <w:style w:type=\"paragraph\" w:styleId=\"Heading3\"><w:name w:val=\"heading 3\"/><w:basedOn w:val=\"Normal\"/>"
            + "<w:pPr><w:outlineLvl w:val=\"2\"/></w:pPr></w:style>\n"
            + "  <w:style w:type=\"paragraph\" w:styleId=\"ListParagraph\"><w:name w:val=\"List Paragraph\"/>"
            + "<w:basedOn w:val=\"Normal\"/></w:style>\n"
            + "  <w:style w:type=\"table\" w:styleId=\"TableGrid\"><w:name w:val=\"Table Grid\"/></w:style>\n"
            + "</w:styles>\n";

    /** List 1 is a bullet list, list 2 a numbered list (1., 2., ...; a), b), ... one level down). */
    public static final String NUMBERING = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n"
            + "<w:numbering xmlns:w=\"" + W + "\">\n"
            + "  <w:abstractNum w:abstractNumId=\"0\"><w:lvl w:ilvl=\"0\"><w:start w:val=\"1\"/><w:numFmt w:val=\"bullet\"/>"
            + "<w:lvlText w:val=\"•\"/></w:lvl><w:lvl w:ilvl=\"1\"><w:start w:val=\"1\"/><w:numFmt w:val=\"bullet\"/>"
            + "<w:lvlText w:val=\"o\"/></w:lvl></w:abstractNum>\n"
            + "  <w:abstractNum w:abstractNumId=\"1\"><w:lvl w:ilvl=\"0\"><w:start w:val=\"1\"/><w:numFmt w:val=\"decimal\"/>"
            + "<w:lvlText w:val=\"%1.\"/></w:lvl><w:lvl w:ilvl=\"1\"><w:start w:val=\"1\"/><w:numFmt w:val=\"lowerLetter\"/>"
            + "<w:lvlText w:val=\"%2)\"/></w:lvl></w:abstractNum>\n"
            + "  <w:num w:numId=\"1\"><w:abstractNumId w:val=\"0\"/></w:num>\n"
            + "  <w:num w:numId=\"2\"><w:abstractNumId w:val=\"1\"/></w:num>\n"
            + "</w:numbering>\n";

    private static final String CONTENT_TYPES = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n"
            + "<Types xmlns=\"http://schemas.openxmlformats.org/package/2006/content-types\">\n"
            + "  <Default Extension=\"rels\" ContentType=\"application/vnd.openxmlformats-package.relationships+xml\"/>\n"
            + "  <Default Extension=\"xml\" ContentType=\"application/xml\"/>\n"
            + "  <Override PartName=\"/word/document.xml\" ContentType=\"application/vnd.openxmlformats-officedocument."
            + "wordprocessingml.document.main+xml\"/>\n"
            + "  <Override PartName=\"/word/styles.xml\" ContentType=\"application/vnd.openxmlformats-officedocument."
            + "wordprocessingml.styles+xml\"/>\n"
            + "  <Override PartName=\"/word/numbering.xml\" ContentType=\"application/vnd.openxmlformats-officedocument."
            + "wordprocessingml.numbering+xml\"/>\n"
            + "</Types>\n";

    private static final String PACKAGE_RELS = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n"
            + "<Relationships xmlns=\"http://schemas.openxmlformats.org/package/2006/relationships\">\n"
            + "  <Relationship Id=\"rId1\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/"
            + "officeDocument\" Target=\"word/document.xml\"/>\n"
            + "</Relationships>\n";

    private static final String DOCUMENT_RELS = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n"
            + "<Relationships xmlns=\"http://schemas.openxmlformats.org/package/2006/relationships\">\n"
            + "  <Relationship Id=\"rId1\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/"
            + "styles\" Target=\"styles.xml\"/>\n"
            + "  <Relationship Id=\"rId2\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/"
            + "numbering\" Target=\"numbering.xml\"/>\n"
            + "</Relationships>\n";

    /** 1 January 2026, so that entries carry no build time. */
    private static final long FIXED_TIME = 1767225600000L;

    private DocxFixture() {
    }

    /** A complete package around a body: the body's XML (the children of w:body), the default styles and lists. */
    public static Map<String, String> parts(String bodyXml) {
        Map<String, String> parts = new LinkedHashMap<>();
        parts.put("word/document.xml", document(bodyXml));
        parts.put("word/styles.xml", STYLES);
        parts.put("word/numbering.xml", NUMBERING);
        return parts;
    }

    public static String document(String bodyXml) {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n<w:document xmlns:w=\"" + W + "\">\n<w:body>\n"
                + bodyXml + "\n</w:body>\n</w:document>\n";
    }

    /** Every file below a folder as a part, named by its path inside the folder with forward slashes. */
    public static Map<String, String> partsOf(Path folder) {
        Map<String, String> parts = new TreeMap<>();
        try (Stream<Path> walk = Files.walk(folder)) {
            for (Path file : walk.filter(Files::isRegularFile).sorted().collect(Collectors.toList())) {
                parts.put(folder.relativize(file).toString().replace('\\', '/'),
                        new String(Files.readAllBytes(file), StandardCharsets.UTF_8));
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return parts;
    }

    /** The zip archive of the parts, with the package parts added where missing. */
    public static byte[] zip(Map<String, String> given) {
        Map<String, String> parts = new TreeMap<>(given);
        parts.putIfAbsent("[Content_Types].xml", CONTENT_TYPES);
        parts.putIfAbsent("_rels/.rels", PACKAGE_RELS);
        if (parts.containsKey("word/document.xml")) {
            parts.putIfAbsent("word/_rels/document.xml.rels", DOCUMENT_RELS);
        }
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (ZipOutputStream zip = new ZipOutputStream(bytes, StandardCharsets.UTF_8)) {
            for (Map.Entry<String, String> part : parts.entrySet()) {
                ZipEntry entry = new ZipEntry(part.getKey());
                entry.setTime(FIXED_TIME);
                zip.putNextEntry(entry);
                zip.write(part.getValue().getBytes(StandardCharsets.UTF_8));
                zip.closeEntry();
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return bytes.toByteArray();
    }

    public static Path write(Path file, Map<String, String> parts) {
        try {
            Files.createDirectories(file.toAbsolutePath().getParent());
            Files.write(file, zip(parts));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return file;
    }

    // ------------------------------------------------------------------ pieces of a body

    public static String escape(String text) {
        return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
    }

    /** A run of text; {@code xml:space="preserve"} keeps spaces at its ends. */
    public static String run(String text, boolean bold) {
        return "<w:r>" + (bold ? "<w:rPr><w:b/></w:rPr>" : "") + "<w:t xml:space=\"preserve\">" + escape(text)
                + "</w:t></w:r>";
    }

    public static String paragraph(String style, String runsXml) {
        return "<w:p>" + (style.isEmpty() ? "" : "<w:pPr><w:pStyle w:val=\"" + style + "\"/></w:pPr>") + runsXml + "</w:p>";
    }

    public static String listItem(int numId, int level, String runsXml) {
        return "<w:p><w:pPr><w:pStyle w:val=\"ListParagraph\"/><w:numPr><w:ilvl w:val=\"" + level + "\"/><w:numId w:val=\""
                + numId + "\"/></w:numPr></w:pPr>" + runsXml + "</w:p>";
    }

    /** A table of plain cells, the first row bold; every row has the same number of cells. */
    public static String table(List<List<String>> rows) {
        StringBuilder xml = new StringBuilder("<w:tbl><w:tblPr><w:tblStyle w:val=\"TableGrid\"/></w:tblPr><w:tblGrid>");
        for (int i = 0; i < rows.get(0).size(); i++) {
            xml.append("<w:gridCol w:w=\"2000\"/>");
        }
        xml.append("</w:tblGrid>");
        for (int r = 0; r < rows.size(); r++) {
            xml.append("<w:tr>");
            for (String cell : rows.get(r)) {
                xml.append("<w:tc><w:p>").append(cell.isEmpty() ? "" : run(cell, r == 0)).append("</w:p></w:tc>");
            }
            xml.append("</w:tr>");
        }
        return xml.append("</w:tbl>").toString();
    }
}
