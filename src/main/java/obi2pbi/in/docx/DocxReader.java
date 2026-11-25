package obi2pbi.in.docx;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import javax.xml.stream.XMLStreamException;
import obi2pbi.core.FsdBlock;
import obi2pbi.core.FsdCell;
import obi2pbi.core.FsdDocument;
import obi2pbi.core.FsdRun;
import obi2pbi.in.xml.XmlNode;
import obi2pbi.in.xml.XmlTreeReader;

/**
 * Reads an FSD saved as a Word {@code .docx} file into the document model (story 3.1; Architecture AD-6,
 * AD-10). JDK only: the file is opened with {@link ZipFile} (not a zip stream, whose entries the XML reader
 * would close), and {@code word/document.xml}, {@code word/styles.xml} and {@code word/numbering.xml} are
 * parsed with the project's XML tree reader.
 *
 * <p>What is read: the body's paragraphs and tables in document order, also inside content controls.
 * A paragraph whose style is a heading (Word's "heading 1" to "heading 9", or "Title") is a heading; one with
 * list numbering (its own or its style's) is a list item, with the marker Word would show; the rest are
 * paragraphs. Runs are joined into one text; bold is kept. Table cells keep their grid position; a merged
 * cell's other positions are cells that name it. A tab is kept as a tab, a line break as a line feed.
 *
 * <p>What is not read (story 3.1, Never): images and drawings, text boxes, comments, deleted text of tracked
 * changes (inserted text is read as it is shown), headers, footers, footnotes, field codes and equations.
 * Empty paragraphs are left out and get no anchor.
 */
public final class DocxReader {

    static final String W = "http://schemas.openxmlformats.org/wordprocessingml/2006/main";
    /** The namespace of Word's "Strict Open XML" format. */
    static final String W_STRICT = "http://purl.oclc.org/ooxml/wordprocessingml/main";

    private static final byte[] OLE_SIGNATURE = {(byte) 0xD0, (byte) 0xCF, 0x11, (byte) 0xE0, (byte) 0xA1, (byte) 0xB1, 0x1A,
            (byte) 0xE1};

    private final DocxStyles styles;
    private final DocxNumbering numbering;

    private DocxReader(DocxStyles styles, DocxNumbering numbering) {
        this.styles = styles;
        this.numbering = numbering;
    }

    /** Reads the file. */
    public static FsdDocument read(Path file) throws DocxException {
        if (!Files.isRegularFile(file)) {
            throw new DocxException(file + " was not found.");
        }
        byte[] start = new byte[8];
        int length;
        try (InputStream in = Files.newInputStream(file)) {
            length = in.readNBytes(start, 0, start.length);
        } catch (IOException e) {
            throw new DocxException("cannot read " + file + ": " + e.getMessage());
        }
        boolean ole = length == OLE_SIGNATURE.length && java.util.Arrays.equals(start, OLE_SIGNATURE);
        boolean zipSignature = length >= 2 && start[0] == 'P' && start[1] == 'K';
        if (ole || (!zipSignature && file.getFileName().toString().toLowerCase(Locale.ROOT).endsWith(".doc"))) {
            throw new DocxException(file + " is an old-style Word file (.doc), which is not read. Open it in Word, "
                    + "save it as a Word Document (.docx), and run fsd-text on the .docx file.");
        }
        try (ZipFile zip = new ZipFile(file.toFile())) {
            XmlNode document = part(zip, "word/document.xml");
            if (document == null) {
                throw new DocxException(file + " is a zip archive with no word/document.xml, so it is not a Word "
                        + "document (.docx).");
            }
            DocxReader reader = new DocxReader(DocxStyles.read(part(zip, "word/styles.xml")),
                    DocxNumbering.read(part(zip, "word/numbering.xml")));
            XmlNode body = wChild(document, "body");
            List<FsdBlock> blocks = new ArrayList<>();
            if (body != null) {
                reader.blocks(body, blocks);
            }
            return new FsdDocument(blocks);
        } catch (ZipException e) {
            throw new DocxException(file + " is not a Word document (.docx): it is not a zip archive ("
                    + e.getMessage() + ").");
        } catch (IOException e) {
            throw new DocxException("cannot read " + file + ": " + e.getMessage());
        }
    }

    /** The parsed part, or null when the archive has no such entry. */
    private static XmlNode part(ZipFile zip, String name) throws IOException, DocxException {
        ZipEntry entry = zip.getEntry(name);
        if (entry == null) {
            return null;
        }
        byte[] content;
        try (InputStream in = zip.getInputStream(entry)) {
            content = in.readAllBytes();
        }
        try {
            return XmlTreeReader.parse(content);
        } catch (XMLStreamException e) {
            throw new DocxException(name + " inside the file is not well-formed XML: " + e.getMessage());
        }
    }

    // ------------------------------------------------------------------ body

    private void blocks(XmlNode container, List<FsdBlock> out) {
        for (XmlNode node : container.children()) {
            if (isW(node, "p")) {
                FsdBlock block = paragraph(node);
                if (block != null) {
                    out.add(block);
                }
            } else if (isW(node, "tbl")) {
                FsdBlock table = table(node);
                if (table != null) {
                    out.add(table);
                }
            } else if (isW(node, "sdt")) {
                XmlNode content = wChild(node, "sdtContent");
                if (content != null) {
                    blocks(content, out);
                }
            } else if (isW(node, "customXml") || isW(node, "ins") || isW(node, "moveTo")) {
                blocks(node, out);
            }
        }
    }

    /** A heading, list item or paragraph; null for an empty paragraph. */
    private FsdBlock paragraph(XmlNode p) {
        List<FsdRun> runs = new ArrayList<>();
        inline(p, false, runs);
        runs = joined(runs);
        if (isBlank(runs)) {
            return null;
        }
        XmlNode properties = wChild(p, "pPr");
        String styleId = "";
        String numId = null;
        int numLevel = 0;
        int outline = -1;
        if (properties != null) {
            XmlNode style = wChild(properties, "pStyle");
            styleId = style == null ? "" : wAttr(style, "val");
            XmlNode numPr = wChild(properties, "numPr");
            if (numPr != null) {
                XmlNode id = wChild(numPr, "numId");
                XmlNode ilvl = wChild(numPr, "ilvl");
                numId = id == null ? null : wAttr(id, "val");
                numLevel = ilvl == null ? 0 : number(wAttr(ilvl, "val"), 0);
            }
            XmlNode outlineLevel = wChild(properties, "outlineLvl");
            if (outlineLevel != null) {
                outline = number(wAttr(outlineLevel, "val"), -1);
            }
        }
        int heading = styles.headingLevel(styleId);
        if (heading == 0 && outline >= 0 && outline < 9) {
            heading = outline + 1;
        }
        if (heading > 0) {
            List<FsdRun> plain = new ArrayList<>();
            plain.add(new FsdRun(text(runs), false));
            return FsdBlock.heading(Math.min(6, heading), plain);
        }
        if (numId == null) {
            String[] fromStyle = styles.numbering(styleId);
            if (fromStyle != null) {
                numId = fromStyle[0];
                numLevel = Integer.parseInt(fromStyle[1]);
            }
        }
        if (numId != null && !numId.isEmpty() && !numId.equals("0")) {
            return FsdBlock.listItem(numLevel, numbering.next(numId, numLevel), runs);
        }
        return FsdBlock.paragraph(runs);
    }

    /** Collects the text of runs below a node, in document order. */
    private void inline(XmlNode node, boolean bold, List<FsdRun> out) {
        for (XmlNode child : node.children()) {
            if (isW(child, "r")) {
                run(child, out);
            } else if (isW(child, "hyperlink") || isW(child, "smartTag") || isW(child, "ins") || isW(child, "moveTo")
                    || isW(child, "fldSimple") || isW(child, "customXml") || isW(child, "bdo") || isW(child, "dir")) {
                inline(child, bold, out);
            } else if (isW(child, "sdt")) {
                XmlNode content = wChild(child, "sdtContent");
                if (content != null) {
                    inline(content, bold, out);
                }
            }
            // pPr, del, moveFrom, bookmarks, comment ranges, proofing marks, math: not text that is read.
        }
    }

    private static void run(XmlNode r, List<FsdRun> out) {
        boolean bold = false;
        XmlNode properties = wChild(r, "rPr");
        if (properties != null) {
            XmlNode b = wChild(properties, "b");
            if (b != null) {
                String value = wAttr(b, "val");
                bold = !(value.equals("0") || value.equals("false") || value.equals("off"));
            }
        }
        StringBuilder text = new StringBuilder();
        for (XmlNode child : r.children()) {
            if (isW(child, "t")) {
                text.append(child.rawText());
            } else if (isW(child, "tab") || isW(child, "ptab")) {
                text.append('\t');
            } else if (isW(child, "br")) {
                String type = wAttr(child, "type");
                if (type.isEmpty() || type.equals("textWrapping")) {
                    text.append('\n');
                }
            } else if (isW(child, "cr")) {
                text.append('\n');
            } else if (isW(child, "noBreakHyphen")) {
                text.append('-');
            }
            // delText, instrText, drawing, pict, object, footnote and comment references: not read.
        }
        if (text.length() > 0) {
            out.add(new FsdRun(text.toString(), bold));
        }
    }

    /** Joins neighbouring runs with the same formatting, so a word stored as several runs is one text. */
    private static List<FsdRun> joined(List<FsdRun> runs) {
        List<FsdRun> out = new ArrayList<>();
        for (FsdRun run : runs) {
            if (!out.isEmpty() && out.get(out.size() - 1).bold == run.bold) {
                FsdRun last = out.remove(out.size() - 1);
                out.add(new FsdRun(last.text + run.text, run.bold));
            } else {
                out.add(run);
            }
        }
        return out;
    }

    private static String text(List<FsdRun> runs) {
        StringBuilder text = new StringBuilder();
        for (FsdRun run : runs) {
            text.append(run.text);
        }
        return text.toString();
    }

    private static boolean isBlank(List<FsdRun> runs) {
        return text(runs).trim().isEmpty();
    }

    // ------------------------------------------------------------------ tables

    private FsdBlock table(XmlNode tbl) {
        int gridColumns = 0;
        XmlNode grid = wChild(tbl, "tblGrid");
        if (grid != null) {
            for (XmlNode column : grid.children()) {
                gridColumns += isW(column, "gridCol") ? 1 : 0;
            }
        }
        List<XmlNode> rowNodes = new ArrayList<>();
        collect(tbl, "tr", rowNodes);
        List<List<FsdCell>> rows = new ArrayList<>();
        List<Integer> restartRow = new ArrayList<>();
        for (XmlNode tr : rowNodes) {
            int rowNumber = rows.size() + 1;
            List<FsdCell> row = new ArrayList<>();
            XmlNode trPr = wChild(tr, "trPr");
            int before = 0;
            if (trPr != null) {
                XmlNode gridBefore = wChild(trPr, "gridBefore");
                before = gridBefore == null ? 0 : Math.max(0, number(wAttr(gridBefore, "val"), 0));
            }
            for (int i = 0; i < before; i++) {
                row.add(FsdCell.of(""));
            }
            List<XmlNode> cells = new ArrayList<>();
            collect(tr, "tc", cells);
            for (XmlNode tc : cells) {
                int column = row.size();
                int span = 1;
                boolean continued = false;
                XmlNode tcPr = wChild(tc, "tcPr");
                if (tcPr != null) {
                    XmlNode gridSpan = wChild(tcPr, "gridSpan");
                    span = gridSpan == null ? 1 : Math.max(1, number(wAttr(gridSpan, "val"), 1));
                    XmlNode vMerge = wChild(tcPr, "vMerge");
                    continued = vMerge != null && !wAttr(vMerge, "val").equals("restart");
                }
                while (restartRow.size() < column + span) {
                    restartRow.add(0);
                }
                int ownerRow = rowNumber;
                if (continued && restartRow.get(column) > 0) {
                    ownerRow = restartRow.get(column);
                    row.add(FsdCell.mergedInto(ownerRow, column + 1));
                } else {
                    row.add(FsdCell.of(cellText(tc)));
                    for (int i = 0; i < span; i++) {
                        restartRow.set(column + i, rowNumber);
                    }
                }
                for (int i = 1; i < span; i++) {
                    row.add(FsdCell.mergedInto(ownerRow, column + 1));
                }
            }
            rows.add(row);
        }
        if (rows.isEmpty()) {
            return null;
        }
        int width = gridColumns;
        for (List<FsdCell> row : rows) {
            width = Math.max(width, row.size());
        }
        for (List<FsdCell> row : rows) {
            while (row.size() < width) {
                row.add(FsdCell.of(""));
            }
        }
        return FsdBlock.table(rows);
    }

    /** Direct children of the given name, also those wrapped in a content control or custom XML element. */
    private static void collect(XmlNode parent, String name, List<XmlNode> out) {
        for (XmlNode child : parent.children()) {
            if (isW(child, name)) {
                out.add(child);
            } else if (isW(child, "sdt")) {
                XmlNode content = wChild(child, "sdtContent");
                if (content != null) {
                    collect(content, name, out);
                }
            } else if (isW(child, "customXml")) {
                collect(child, name, out);
            }
        }
    }

    /** A cell's paragraphs joined by line feeds; a list item keeps its marker; a nested table's cells are its lines. */
    private String cellText(XmlNode tc) {
        List<FsdBlock> inside = new ArrayList<>();
        blocks(tc, inside);
        List<String> lines = new ArrayList<>();
        for (FsdBlock block : inside) {
            if (block.kind == FsdBlock.Kind.TABLE) {
                for (List<FsdCell> row : block.rows) {
                    for (FsdCell cell : row) {
                        if (!cell.isMerged() && !cell.text.isEmpty()) {
                            lines.add(cell.text);
                        }
                    }
                }
            } else if (block.kind == FsdBlock.Kind.LIST_ITEM) {
                lines.add(block.marker + " " + block.text());
            } else {
                lines.add(block.text());
            }
        }
        return String.join("\n", lines);
    }

    // ------------------------------------------------------------------ small helpers, shared in the package

    static boolean isW(XmlNode node, String local) {
        return node.localName().equals(local) && (node.namespace().equals(W) || node.namespace().equals(W_STRICT));
    }

    static XmlNode wChild(XmlNode node, String local) {
        for (XmlNode child : node.children()) {
            if (isW(child, local)) {
                return child;
            }
        }
        return null;
    }

    /** A WordprocessingML attribute ({@code w:val}), also when written without a prefix. */
    static String wAttr(XmlNode node, String local) {
        String value = node.rawAttr(XmlNode.qualified(W, local));
        if (value.isEmpty()) {
            value = node.rawAttr(XmlNode.qualified(W_STRICT, local));
        }
        if (value.isEmpty()) {
            value = node.rawAttr(local);
        }
        return value;
    }

    static int number(String text, int whenNot) {
        try {
            return Integer.parseInt(text.trim());
        } catch (NumberFormatException e) {
            return whenNot;
        }
    }
}
