package obi2pbi;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import obi2pbi.core.DocAssembly;
import obi2pbi.in.kb.CsvTable;

/**
 * {@code assemble-doc}: the document is its parts joined in file-name order, always to the same bytes, and an
 * answer a user typed into the document is never overwritten before it is in the answers file (Architecture
 * AD-16, AD-17; docs/skill-conventions.md).
 */
public final class AssembleDocTest {

    private static final String OPEN = "\n## Open questions\n\n### Q1. What is the rate?\nWhy it matters: it changes the number.\n"
            + "How to get the answer: read the rate table.\nAnswer:\n\n### Q2. Which currency?\nWhy it matters: it is not recorded.\n"
            + "How to get the answer: read the ledger.\nAnswer:\n";

    private static final class Case {
        final Path document;
        final Path parts;
        final Path answers;

        Case(String name) {
            Path work = TestSupport.work("assemble-doc-" + name);
            document = work.resolve("as-is/e/sales.md");
            parts = work.resolve("as-is/e/sales.parts");
            answers = work.resolve("as-is/e/sales.answers.md");
            part("000-summary.md", "Status: draft, not reviewed\n\n# Sales\n");
            part("001-overview.md", "\n## What report authors see\n\nOne table.\n");
            part("990-open-questions.md", OPEN);
        }

        void part(String name, String text) {
            TestSupport.write(parts.resolve(name), text);
        }

        TestSupport.Run run() {
            return TestSupport.run("assemble-doc", document.toString());
        }

        String text() throws Exception {
            return new String(Files.readAllBytes(document), StandardCharsets.UTF_8);
        }
    }

    public void testPartsAreJoinedInFileNameOrderWithOneEmptyLineBetween() throws Exception {
        Case c = new Case("order");
        c.part("010-joins.md", "## How the tables relate\r\n\r\nTwo joins.\r\n\r\n\r\n");
        c.part("002-table.md", "\n\n## Fact\n\n```\n  kept  \n```");
        c.part("003-stale.md", "\n  \n");
        c.part("notes.txt", "not a part");
        TestSupport.Run run = c.run();
        Check.equal(0, run.exit, "exit code");
        Check.equal("Status: draft, not reviewed\n\n# Sales\n\n## What report authors see\n\nOne table.\n\n## Fact\n\n```\n  kept  \n```\n\n"
                + "## How the tables relate\n\nTwo joins.\n" + OPEN, c.text(),
                "file-name order; line feeds; empty lines at the ends of a part dropped; one empty line between parts; "
                        + "an empty part adds nothing; a last line with no line end gets one; spaces inside a line kept");
        Check.contains(run.out, "parts:      6 (1 empty, adding nothing)", "the number of parts");
        Check.contains(run.out, "lines:      " + (c.text().split("\n", -1).length - 1), "the number of lines");
        Check.contains(run.out, "010-joins.md", "each part is listed");
        Check.contains(run.out, "lines 5-7", "with the lines it became");
        byte[] first = Files.readAllBytes(c.document);
        Check.equal(0, c.run().exit, "a second run");
        Check.sameBytes(first, Files.readAllBytes(c.document), "the same parts give the same bytes");
    }

    public void testNamesSortByCodePointSoNumberedPartsStayInOrder() {
        DocAssembly.Joined joined = DocAssembly.join(List.of("000-summary.md", "002-table-01-fact.md"), List.of("a\nb\n", "c\n"));
        Check.equal("a\nb\n\nc\n", joined.text, "the text");
        Check.equal(List.of("summary", "summary", "table-01-fact", "table-01-fact"), joined.unitOfLine,
                "each line's unit is its part's name without the number; the empty line before a part goes with it");
        Check.equal("open-questions", DocAssembly.unitOf("990-open-questions.md"), "a closing part");
        Check.equal("notes", DocAssembly.unitOf("notes.md"), "a name with no number");
    }

    public void testAnAnswerTypedIntoTheDocumentStopsTheOverwriteUntilItIsRecorded() throws Exception {
        Case c = new Case("answers");
        Check.equal(0, c.run().exit, "first assembly");
        String typed = c.text().replace("read the ledger.\nAnswer:\n", "read the ledger.\nAnswer: Euro,\n  since 2019.\n");
        Check.that(!typed.equals(c.text()), "premise: the user typed an answer to Q2");
        TestSupport.write(c.document, typed);
        c.part("001-overview.md", "\n## What report authors see\n\nTwo tables.\n");

        TestSupport.Run refused = c.run();
        Check.equal(3, refused.exit, "exit code: not written");
        Check.contains(refused.out, "questions:  Q2", "the question whose answer is not recorded, and only that one");
        Check.contains(refused.out, "  Q2: Euro, since 2019.", "with the answer as typed, so that it can be recorded");
        Check.equal(typed, c.text(), "the document is untouched");

        TestSupport.write(c.answers, "# Answers\n\n## Q2\nQuestion: Which currency?\nAnswer: Dollar.\n");
        Check.equal(3, c.run().exit, "an entry for the number with another answer does not record this one");

        TestSupport.write(c.answers, "# Answers\n\n## Q2\nQuestion: Which currency?\nAnswer: Dollar.\n\n## Q2\nQuestion: Which currency?\n"
                + "Answered by: finance, 2026-11-24\nAnswer: Euro, since 2019.\n");
        TestSupport.Run written = c.run();
        Check.equal(0, written.exit, "recorded as a further entry (white space aside): written");
        Check.contains(c.text(), "Two tables.", "the new text");
        Check.contains(c.text(), "read the ledger.\nAnswer:\n", "from the parts, where the question is still open");
    }

    public void testAnAnswerAlreadyRecordedDoesNotStopIt() throws Exception {
        Case c = new Case("recorded");
        c.part("990-open-questions.md", "\n## Open questions\n");
        c.part("991-answered-questions.md", "\n## Answered questions\n\n### Q1. What is the rate?\nWhy it matters: it changes the number.\n"
                + "How to get the answer: read the rate table.\nAnswer: 0.76\n");
        Check.equal(0, c.run().exit, "no document yet: nothing to protect");
        Check.equal(3, c.run().exit, "the answered question is in no answers file");
        TestSupport.write(c.answers, "# Answers\n\n## Q1\nAnswer: 0.76\n");
        Check.equal(0, c.run().exit, "it is recorded");
    }

    public void testMissingPartsAndWrongUsageExit2AndWriteNothing() {
        Path work = TestSupport.work("assemble-doc-usage");
        Path document = work.resolve("as-is/e/sales.md");
        TestSupport.Run none = TestSupport.run("assemble-doc", document.toString());
        Check.equal(2, none.exit, "no parts folder");
        Check.contains(none.err, "sales.parts", "names the folder it looked for");
        TestSupport.write(work.resolve("as-is/e/sales.parts/readme.txt"), "x");
        Check.equal(2, TestSupport.run("assemble-doc", document.toString()).exit, "a parts folder with no .md file");
        Check.that(!Files.exists(document), "nothing written");
        Check.equal(2, TestSupport.run("assemble-doc").exit, "no document");
        Check.equal(2, TestSupport.run("assemble-doc", work.resolve("as-is/e/sales.txt").toString()).exit, "not a .md name");
        Check.equal(2, TestSupport.run("assemble-doc", document.toString(), "--force").exit, "an unknown option");
        Check.contains(TestSupport.run("--help").out, "assemble-doc", "listed among the commands");
    }

    /** check-doc names the part a finding at a line is in, when the document is exactly its parts joined. */
    public void testCheckDocNamesThePartOfAFindingAtALine() throws Exception {
        Path work = TestSupport.work("assemble-doc-check");
        Path out = work.resolve("slices");
        Check.equal(0, SliceFixture.slice(SliceTest.pairKb(work), out, "--all").exit, "slice");
        Path slice = out.resolve("constructed-sa");
        Path document = work.resolve("as-is/constructed-pair/constructed-sa.md");
        Path parts = work.resolve("as-is/constructed-pair/constructed-sa.parts");
        TestSupport.write(parts.resolve("000-summary.md"), "Status: draft, not reviewed\n\n# Constructed SA\n");
        TestSupport.write(parts.resolve("001-overview.md"), "\n## What report authors see\n\nA row that is not there [kb:constructed-pair::bmm::No.Such.Row].\n");
        TestSupport.write(parts.resolve("990-open-questions.md"), "\n## Open questions\n\n### Q1. What?\nWhy it matters: it does.\nAnswer:\n");
        Check.equal(0, TestSupport.run("assemble-doc", document.toString()).exit, "assemble");
        TestSupport.Run run = TestSupport.run("check-doc", document.toString(), "--slice", slice.toString());
        Check.equal(3, run.exit, "findings");
        CsvTable findings = TestSupport.table(document.getParent(), "constructed-sa.md.check");
        String citation = "";
        String question = "";
        int uncovered = 0;
        for (String[] row : findings.rows) {
            String kind = findings.value(row, "finding");
            if (kind.equals("unknown_citation")) {
                citation = findings.value(row, "unit_id");
            } else if (kind.equals("malformed_question")) {
                question = findings.value(row, "unit_id");
            } else if (kind.equals("not_covered")) {
                uncovered++;
                Check.that(!findings.value(row, "unit_id").isEmpty(), "a row not covered names its unit");
            }
        }
        Check.equal("overview", citation, "the part the wrong citation is in");
        Check.equal("open-questions", question, "the part the malformed question is in");
        Check.equal(13, uncovered, "premise: the thirteen required rows are not covered");
        Check.contains(run.out, "by unit:", "the summary groups the findings by unit");
        Check.contains(run.out, "  open-questions", "one line per unit");

        TestSupport.write(document, new String(Files.readAllBytes(document), StandardCharsets.UTF_8).replace("# Constructed SA\n", "# Edited by hand\n\nA line.\n"));
        TestSupport.Run edited = TestSupport.run("check-doc", document.toString(), "--slice", slice.toString());
        findings = TestSupport.table(document.getParent(), "constructed-sa.md.check");
        for (String[] row : findings.rows) {
            if (!findings.value(row, "line").isEmpty()) {
                Check.equal("", findings.value(row, "unit_id"), "a document that is not its parts joined: no unit is claimed for a line");
            }
        }
        Check.contains(edited.out, "Run assemble-doc", "and the summary says why");
    }
}
