package obi2pbi;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import obi2pbi.core.FsdDocument;
import obi2pbi.in.kb.CsvTable;

/**
 * {@code fsd-text}: an FSD saved as {@code .docx} becomes {@code fsd.md} with an anchor on every paragraph and
 * table cell, in document order, always the same bytes (story 3.1; Architecture AD-10). Every {@code .docx}
 * here is built from XML text by {@link DocxFixture}.
 */
public final class FsdTextTest {

    private static Path fixture() {
        return TestSupport.root().resolve("src/test/fixtures/fsd");
    }

    private static String read(Path file) throws Exception {
        return new String(Files.readAllBytes(file), StandardCharsets.UTF_8);
    }

    /** Builds the hand-made fixture's .docx in a fresh folder and runs fsd-text on it. */
    private static Path smallRun(String name) {
        Path work = TestSupport.work("fsd-text-" + name);
        Path docx = DocxFixture.write(work.resolve("tab/fsd.docx"), DocxFixture.partsOf(fixture().resolve("small")));
        TestSupport.Run run = TestSupport.run("fsd-text", docx.toString());
        Check.equal(0, run.exit, "exit code; err: " + run.err);
        return work.resolve("tab");
    }

    // ------------------------------------------------------------------ the matrix

    public void testTheHandMadeFixtureGivesTheExpectedText() throws Exception {
        Path tab = smallRun("fixture");
        Check.equal(read(fixture().resolve("small.expected.md")), read(tab.resolve("fsd.md")),
                "fsd.md: Title and heading 1 to 3 as headings; a word in three runs joined; bold kept; deleted text "
                        + "left out and inserted text kept; empty paragraphs left out; bullets, a nested bullet, a list "
                        + "from a style and a numbered list with a lettered level; a tab and a hyperlink's text kept; a "
                        + "table with a vertically and a horizontally merged cell, an empty cell, a '|' and two "
                        + "paragraphs in one cell; a line break");
    }

    public void testTheSameFileTwiceGivesTheSameBytes() throws Exception {
        Path tab = smallRun("twice");
        byte[] markdown = Files.readAllBytes(tab.resolve("fsd.md"));
        byte[] manifest = Files.readAllBytes(tab.resolve("fsd.manifest.csv"));
        Check.equal(0, TestSupport.run("fsd-text", tab.resolve("fsd.docx").toString()).exit, "second run");
        Check.sameBytes(markdown, Files.readAllBytes(tab.resolve("fsd.md")), "fsd.md is byte-identical");
        Check.sameBytes(manifest, Files.readAllBytes(tab.resolve("fsd.manifest.csv")), "the manifest is byte-identical");
    }

    public void testTheManifestRecordsTheSourceSoAChangeCanBeSeen() throws Exception {
        Path tab = smallRun("manifest");
        CsvTable manifest = TestSupport.table(tab, "fsd.manifest");
        Map<String, String> facts = new LinkedHashMap<>();
        for (String[] row : manifest.rows) {
            facts.put(manifest.value(row, "key"), manifest.value(row, "value"));
        }
        Check.equal("fsd.docx", facts.get("source_file"), "source file");
        Check.equal(Long.toString(Files.size(tab.resolve("fsd.docx"))), facts.get("source_bytes"), "size");
        Check.that(facts.get("source_sha256").matches("[0-9a-f]{64}"), "a SHA-256: " + facts.get("source_sha256"));
        Check.equal("12", facts.get("paragraphs"), "paragraph count");
        Check.equal("1", facts.get("tables"), "table count");
        Check.equal("12", facts.get("cells"), "cell count: every grid position");
        Check.equal("0", facts.get("sections"), "a short document is not split");
        String before = facts.get("source_sha256");
        Map<String, String> parts = DocxFixture.partsOf(fixture().resolve("small"));
        parts.put("word/document.xml", parts.get("word/document.xml").replace("Targets may be revised.", "Targets change."));
        DocxFixture.write(tab.resolve("fsd.docx"), parts);
        Check.equal(0, TestSupport.run("fsd-text", tab.resolve("fsd.docx").toString()).exit, "rerun");
        Check.that(!read(tab.resolve("fsd.manifest.csv")).contains(before), "a changed FSD has a different hash");
    }

    public void testHeadingStylesBecomeMarkdownHeadings() throws Exception {
        String body = DocxFixture.paragraph("Heading1", DocxFixture.run("One", false))
                + DocxFixture.paragraph("Heading2", DocxFixture.run("Two", false))
                + DocxFixture.paragraph("Heading3", DocxFixture.run("Three", true))
                + DocxFixture.paragraph("", DocxFixture.run("Body", false))
                // No styles part at all: a style id HeadingN still says the level; an outline level does too.
                + "<w:p><w:pPr><w:outlineLvl w:val=\"3\"/></w:pPr>" + DocxFixture.run("Four", false) + "</w:p>";
        Check.equal("# [p1] One\n\n## [p2] Two\n\n### [p3] Three\n\n[p4] Body\n\n#### [p5] Four\n", text("headings", body, true),
                "headings with styles");
        Map<String, String> parts = new LinkedHashMap<>();
        parts.put("word/document.xml", DocxFixture.document(DocxFixture.paragraph("Heading2", DocxFixture.run("Two", false))));
        Check.equal("## [p1] Two\n", textOf("headings-bare", parts), "a style id with no styles part");
    }

    public void testATableKeepsRowsAndCellsAlignedAndEveryCellAnchored() throws Exception {
        String body = DocxFixture.table(List.of(List.of("Item", "Meaning"), List.of("K1", ""), List.of("", "x")))
                + "<w:tbl><w:tr><w:trPr><w:gridBefore w:val=\"1\"/></w:trPr><w:tc><w:p>" + DocxFixture.run("late", false)
                + "</w:p></w:tc></w:tr><w:tr><w:tc><w:p/></w:tc><w:tc><w:p/></w:tc><w:tc><w:p/></w:tc></w:tr></w:tbl>";
        Check.equal("| [t1.r1.c1] Item | [t1.r1.c2] Meaning |\n|---|---|\n| [t1.r2.c1] K1 | [t1.r2.c2] |\n"
                        + "| [t1.r3.c1] | [t1.r3.c2] x |\n\n"
                        + "| [t2.r1.c1] | [t2.r1.c2] late | [t2.r1.c3] |\n|---|---|---|\n| [t2.r2.c1] | [t2.r2.c2] | [t2.r2.c3] |\n",
                text("table", body, true), "empty cells keep their anchors; a short row is padded to the grid");
    }

    public void testTextSplitAcrossRunsIsJoined() throws Exception {
        String body = "<w:p>" + DocxFixture.run("Rev", false) + DocxFixture.run("en", false) + DocxFixture.run("ue", false)
                + "<w:proofErr w:type=\"spellStart\"/>" + DocxFixture.run(" tar", false) + "<w:bookmarkStart w:id=\"0\" w:name=\"x\"/>"
                + DocxFixture.run("get", false) + "</w:p>";
        Check.equal("[p1] Revenue target\n", text("runs", body, true), "one text");
    }

    public void testAnOldDocFileIsRefused() throws Exception {
        Path work = TestSupport.work("fsd-text-doc");
        Path doc = work.resolve("fsd.doc");
        byte[] ole = {(byte) 0xD0, (byte) 0xCF, 0x11, (byte) 0xE0, (byte) 0xA1, (byte) 0xB1, 0x1A, (byte) 0xE1, 0, 0, 0, 0};
        Files.write(doc, ole);
        TestSupport.Run run = TestSupport.run("fsd-text", doc.toString());
        Check.equal(2, run.exit, "exit code");
        Check.contains(run.err, "save it as a Word Document (.docx)", "the message says what to do");
        Path renamed = work.resolve("fsd.docx");
        Files.write(renamed, ole);
        Check.contains(TestSupport.run("fsd-text", renamed.toString()).err, "old-style Word file (.doc)",
                "recognised by its content, whatever its name");
        Check.that(!Files.exists(work.resolve("fsd.md")), "nothing written");
    }

    public void testAFileThatIsNotAWordDocumentIsRefused() throws Exception {
        Path work = TestSupport.work("fsd-text-not-word");
        Map<String, String> noDocument = new LinkedHashMap<>();
        noDocument.put("xl/workbook.xml", "<workbook/>");
        Path zip = DocxFixture.write(work.resolve("book.docx"), noDocument);
        TestSupport.Run run = TestSupport.run("fsd-text", zip.toString());
        Check.equal(2, run.exit, "a zip with no word/document.xml");
        Check.contains(run.err, "no word/document.xml", "names the problem");
        Path text = TestSupport.write(work.resolve("notes.docx"), "just text\n");
        run = TestSupport.run("fsd-text", text.toString());
        Check.equal(2, run.exit, "not a zip");
        Check.contains(run.err, "it is not a zip archive", "names the problem");
        Map<String, String> broken = new LinkedHashMap<>();
        broken.put("word/document.xml", "<w:document");
        run = TestSupport.run("fsd-text", DocxFixture.write(work.resolve("broken.docx"), broken).toString());
        Check.equal(2, run.exit, "a document part that does not parse");
        Check.contains(run.err, "not well-formed XML", "names the problem");
        Check.equal(2, TestSupport.run("fsd-text", work.resolve("missing.docx").toString()).exit, "a missing file");
        Check.equal(2, TestSupport.run("fsd-text").exit, "no file");
        Check.that(!Files.exists(work.resolve("fsd.md")), "nothing written");
    }

    // ------------------------------------------------------------------ long documents, sections

    public void testALongDocumentIsAlsoWrittenInSectionsThatCoverEveryAnchor() throws Exception {
        StringBuilder body = new StringBuilder();
        for (int h = 1; h <= 3; h++) {
            body.append(DocxFixture.paragraph("Heading1", DocxFixture.run("Part " + h, false)));
            for (int p = 0; p < 40; p++) {
                body.append(DocxFixture.paragraph("", DocxFixture.run("Paragraph " + h + "." + p, false)));
            }
        }
        List<List<String>> rows = new ArrayList<>();
        rows.add(List.of("Item", "Meaning"));
        for (int r = 0; r < 200; r++) {
            rows.add(List.of("I" + r, "meaning " + r));
        }
        body.append(DocxFixture.table(rows));
        Path work = TestSupport.work("fsd-text-long");
        Path docx = DocxFixture.write(work.resolve("fsd.docx"), DocxFixture.parts(body.toString()));
        TestSupport.Run run = TestSupport.run("fsd-text", docx.toString());
        Check.equal(0, run.exit, "exit; " + run.err);
        Check.contains(run.out, "sections:", "the sections are reported");
        String markdown = read(work.resolve("fsd.md"));
        Path sections = work.resolve("fsd.sections");
        String index = read(sections.resolve("index.md"));
        List<String> files = TestSupport.fileNames(sections);
        Check.that(files.size() >= 4, "several sections: " + files);
        Check.equal("01-part-1.md", files.get(0), "named after its heading");
        List<String> seen = new ArrayList<>();
        for (String file : files) {
            if (file.equals("index.md")) {
                continue;
            }
            Check.contains(index, file, "the index lists " + file);
            String text = read(sections.resolve(file));
            int lines = text.split("\n", -1).length - 1;
            Check.that(lines <= 152, file + " has at most 152 lines: " + lines);
            Check.that(text.startsWith("Section "), file + " says which section it is");
            for (String anchor : FsdDocument.anchorsIn(text.substring(text.indexOf('\n')))) {
                if (!seen.contains(anchor)) {
                    seen.add(anchor);
                }
            }
        }
        Check.equal(FsdDocument.anchorsIn(markdown), seen, "the sections hold every anchor of fsd.md, in order");
        Check.contains(markdown, "[t1.r201.c2] meaning 199", "the whole table is in fsd.md");

        // A short document written over it removes the sections, which are no longer true.
        DocxFixture.write(docx, DocxFixture.parts(DocxFixture.paragraph("", DocxFixture.run("Short", false))));
        Check.equal(0, TestSupport.run("fsd-text", docx.toString()).exit, "rerun");
        Check.that(!Files.exists(sections), "the sections folder is removed");
    }

    public void testASectionsFolderThisCommandDidNotWriteIsRefused() throws Exception {
        Path work = TestSupport.work("fsd-text-refuse");
        TestSupport.write(work.resolve("fsd.sections/notes.md"), "mine\n");
        Path docx = DocxFixture.write(work.resolve("fsd.docx"), DocxFixture.parts(DocxFixture.paragraph("", DocxFixture.run("x", false))));
        TestSupport.Run run = TestSupport.run("fsd-text", docx.toString());
        Check.equal(2, run.exit, "refused");
        Check.that(!Files.exists(work.resolve("fsd.md")), "nothing written");
        Check.that(Files.exists(work.resolve("fsd.sections/notes.md")), "the folder is left alone");
    }

    // ------------------------------------------------------------------ the test FSD

    public void testTheSampleFsdReadsLikeItsSourceWithAnchorsAdded() throws Exception {
        Path source = TestSupport.root().resolve("work/sample-sales-monthly/fsd-source.md");
        if (!Files.isRegularFile(source)) {
            throw new SkipException("work/sample-sales-monthly/fsd-source.md is not there");
        }
        Path work = TestSupport.work("fsd-text-sample");
        String markdown = read(source);
        Path docx = DocxFixture.write(work.resolve("fsd.docx"), DocxFixture.parts(FsdDocxGenerator.body(markdown)));
        Check.equal(0, TestSupport.run("fsd-text", docx.toString()).exit, "exit");
        String text = read(work.resolve("fsd.md"));
        Check.equal(markdown, text.replaceAll("\\[(p[0-9]+|t[0-9]+\\.r[0-9]+\\.c[0-9]+)\\] ", ""),
                "with the anchors taken out, fsd.md is the source, character for character");
        List<String> anchors = FsdDocument.anchorsIn(text);
        Check.equal(anchors.size(), new java.util.LinkedHashSet<>(anchors).size(), "every anchor once");
        Check.equal("p1", anchors.get(0), "numbered from 1 in document order");
    }

    // ------------------------------------------------------------------ helpers

    private static String text(String name, String body, boolean withStyles) throws Exception {
        return textOf(name, withStyles ? DocxFixture.parts(body) : Map.of("word/document.xml", DocxFixture.document(body)));
    }

    private static String textOf(String name, Map<String, String> parts) throws Exception {
        Path work = TestSupport.work("fsd-text-" + name);
        Path docx = DocxFixture.write(work.resolve("fsd.docx"), parts);
        TestSupport.Run run = TestSupport.run("fsd-text", docx.toString());
        Check.equal(0, run.exit, "exit; " + run.err);
        return read(work.resolve("fsd.md"));
    }
}
