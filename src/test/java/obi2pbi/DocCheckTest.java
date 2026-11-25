package obi2pbi;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import obi2pbi.core.Citation;
import obi2pbi.core.DocCheck;
import obi2pbi.core.DocCheckResult;
import obi2pbi.core.KbData;
import obi2pbi.core.KbRows;
import obi2pbi.in.kb.CsvTable;

/**
 * The {@code check-doc} command (story 2.3; Architecture AD-12, AD-14, AD-16, AD-18), end to end on a small
 * document written here for a fixture Slice: once with every required row cited, then with one fault of each
 * kind. The text conventions it relies on are in {@code docs/skill-conventions.md}.
 */
public final class DocCheckTest {

    private static final String QUESTIONS = "## Open questions\n"
            + "\n"
            + "### Q1. Which currency is Amount in?\n"
            + "Why it matters: the new model must state the currency of every amount.\n"
            + "How to get the answer: ask the owner of the sales reports, or look at the column comment of "
            + "SALES.AMOUNT in the database, and bring back the currency code.\n"
            + "Answer:\n"
            + "\n"
            + "## Answered questions\n"
            + "\n"
            + "### Q2. Is the rolling amount still used?\n"
            + "Why it matters: an unused measure need not be rebuilt.\n"
            + "How to get the answer: ask the report authors.\n"
            + "Answer: Yes, on the monthly dashboard.\n"
            + "\n";
    private static final String ANSWERS = "# Answers\n\n## Q2\nQuestion: Is the rolling amount still used?\n"
            + "Answered by: the report owner, 2026-11-23\nAnswer: Yes, on the monthly dashboard.\n";

    /** A Slice of the combined hand-made estate, and where the document goes. */
    private static final class Case {
        final Path slice;
        final Path document;
        final List<String> required = new ArrayList<>();

        Case(String name) {
            Path work = TestSupport.work("check-doc-" + name);
            Path out = work.resolve("slices");
            Check.equal(0, SliceFixture.slice(SliceTest.pairKb(work), out, "--all").exit, "slice");
            slice = out.resolve("constructed-sa");
            document = work.resolve("as-is/constructed-pair/constructed-sa.md");
            KbRows rows = SliceFixture.read(slice).table("required_rows");
            for (String[] row : rows.rows) {
                required.add(rows.get(row, "row_id"));
            }
            // Six more since deliberate difference 33: the physical table of the pair, its definition and its columns.
            Check.equal(13, required.size(), "premise: the slice has thirteen required rows");
        }

        /** A document that cites every required row but the ones named, which go under 'Not described'. */
        String text(String status, List<String> notDescribed) {
            StringBuilder text = new StringBuilder(status + "\n# Constructed SA: the model as it is today\n\n## What it holds\n\n");
            for (String id : required) {
                if (!notDescribed.contains(id)) {
                    text.append("The model holds this, as the repository records it ").append(Citation.of(id)).append(".\n");
                }
            }
            text.append("The rolling amount is still in use [answer:Q2].\n\n").append(QUESTIONS).append("## Not described\n\n");
            for (String id : notDescribed) {
                text.append("- ").append(Citation.of(id)).append(" — a technical row with no meaning for a business reader\n");
            }
            return text.toString();
        }

        TestSupport.Run check(String text, boolean answers) {
            TestSupport.write(document, text);
            if (answers) {
                TestSupport.write(document.resolveSibling("constructed-sa.answers.md"), ANSWERS);
            }
            return TestSupport.run("check-doc", document.toString(), "--slice", slice.toString());
        }

        CsvTable findings() {
            return TestSupport.table(document.getParent(), "constructed-sa.md.check");
        }

        /** The findings as {@code kind} or {@code kind@line}. */
        List<String> kinds() {
            List<String> kinds = new ArrayList<>();
            CsvTable table = findings();
            for (String[] row : table.rows) {
                kinds.add(table.value(row, "finding") + (table.value(row, "line").isEmpty() ? "" : "@" + table.value(row, "line")));
            }
            return kinds;
        }
    }

    private static final String DRAFT = "Status: draft, not reviewed";

    public void testAllCoveredGivesOnlyAHeaderAndExit0() throws Exception {
        Case c = new Case("covered");
        String text = c.text(DRAFT, List.of(c.required.get(0)));
        TestSupport.Run run = c.check(text, true);
        Check.equal(0, run.exit, "exit code: " + run.out + run.err);
        Check.equal(List.of("finding", "row_id", "table", "line", "unit_id", "detail"), c.findings().header, "check.csv columns");
        Check.equal(0, c.findings().rows.size(), "check.csv has only its header");
        Check.contains(run.out, "(status: draft)", "summary: status");
        Check.contains(run.out, "required:   13 rows", "summary: rows required");
        Check.contains(run.out, "covered:    13 rows (1 only listed as not described)", "summary: rows covered");
        Check.contains(run.out, "citations:  13 [kb:...] checked, 1 [answer:...] checked", "summary: citations checked");
        Check.contains(run.out, "questions:  1 open, 1 answered", "summary: open and answered questions");
        Check.contains(run.out, "findings:   0", "summary: findings");
        Check.equal(text, SliceFixture.text(c.document), "the document is never changed");
    }

    public void testAnUncoveredRowIsReportedWithItsTable() {
        Case c = new Case("uncovered");
        String missing = c.required.get(3);
        String text = c.text(DRAFT, List.of()).replace("The model holds this, as the repository records it " + Citation.of(missing) + ".\n", "");
        TestSupport.Run run = c.check(text, true);
        Check.equal(3, run.exit, "exit code");
        Check.equal(List.of("not_covered"), c.kinds(), "exactly its finding");
        CsvTable findings = c.findings();
        Check.equal(missing, findings.value(findings.rows.get(0), "row_id"), "the row identifier");
        KbRows required = SliceFixture.read(c.slice).table("required_rows");
        String table = "";
        for (String[] row : required.rows) {
            if (required.get(row, "row_id").equals(missing)) {
                table = required.get(row, "table");
            }
        }
        Check.equal(table, findings.value(findings.rows.get(0), "table"), "and its table");
        String unit = "";
        for (String[] row : required.rows) {
            if (required.get(row, "row_id").equals(missing)) {
                unit = required.get(row, "unit_id");
            }
        }
        Check.equal(unit, findings.value(findings.rows.get(0), "unit_id"), "and the unit whose packet states it");
        Check.contains(run.out, "by unit:", "the summary counts the findings per unit");
        Check.contains(run.out, "  " + unit, "naming the unit");
        Check.that(!table.isEmpty(), "premise: the table is known");
        Check.contains(run.out, "covered:    12 rows", "summary");
    }

    public void testACitationOfARowThatIsNotInTheSliceIsReportedWithItsLine() {
        Case c = new Case("unknown");
        String text = c.text(DRAFT, List.of()).replace("## What it holds\n", "## What it holds\nRevenue is doubled [kb:constructed-pair::bmm::No.Such.Row].\n"
                + "And this one is never closed [kb:constructed-pair::bmm::No.Such\n");
        TestSupport.Run run = c.check(text, true);
        Check.equal(3, run.exit, "exit code");
        Check.equal(List.of("unknown_citation@5", "unknown_citation@6"), c.kinds(), "exactly its findings, with the line numbers");
        Check.equal("constructed-pair::bmm::No.Such.Row", c.findings().value(c.findings().rows.get(0), "row_id"), "the identifier cited");
    }

    public void testAnAnswerCitationIsCheckedAgainstTheAnswersFile() {
        Case c = new Case("answer");
        String text = c.text(DRAFT, List.of()).replace("[answer:Q2].", "[answer:Q2] and also [answer:Q4] and [answer:4].");
        Check.equal(3, c.check(text, true).exit, "exit code");
        int line = 5 + c.required.size() + 1;
        Check.equal(List.of("unknown_answer@" + line, "unknown_answer@" + line), c.kinds(), "Q4 has no answer; '4' is not a question number; Q2 is fine");

        // With no answers file at all, the answer that was fine is unknown too, and so is the answered question.
        Case none = new Case("answer-no-file");
        Check.equal(3, none.check(none.text(DRAFT, List.of()), false).exit, "exit code with no answers file");
        Check.equal(2, none.kinds().size(), "the citation and the answered question: " + none.kinds());
        Check.that(none.kinds().get(0).startsWith("unknown_answer@") && none.kinds().get(1).startsWith("unknown_answer@"), "both unknown_answer");
        Check.equal(java.util.Set.of("2", "7"), DocCheck.answersIn("## Q2\nAnswer: yes\n## Q3\nAnswer:\n\n## Q7.\nAnswer:\nOn the next line.\n## Notes\nAnswer: x\n"),
                "an answer needs text; a heading that is not Q<n> is not an answer");
    }

    public void testAnOpenQuestionWithoutItsLinesIsMalformed() {
        Case c = new Case("question");
        String text = c.text(DRAFT, List.of()).replace("Answer:\n\n## Answered questions", "\n### Q3. And who loads the table?\n"
                + "Why it matters: it decides who is asked.\nHow to get the answer: ask Data Engineering.\n\n### Notes\n\n## Answered questions");
        Check.equal(3, c.check(text, true).exit, "exit code");
        List<String> kinds = c.kinds();
        Check.equal(3, kinds.size(), "Q1 lost its Answer line, Q3 never had one, and a heading that is not a question: " + kinds);
        for (String kind : kinds) {
            Check.that(kind.startsWith("malformed_question@"), "malformed_question with a line: " + kind);
        }
        Check.contains(c.findings().value(c.findings().rows.get(0), "detail"), "no line starting 'Answer:'", "says what is missing");

        Case missing = new Case("question-section");
        String without = missing.text(DRAFT, List.of());
        without = without.substring(0, without.indexOf("## Open questions")) + without.substring(without.indexOf("## Answered questions"));
        Check.equal(3, missing.check(without, true).exit, "a document with no Open questions section");
        Check.equal(List.of("malformed_question"), missing.kinds(), "is reported once");
    }

    public void testANotDescribedEntryNeedsAReason() {
        Case c = new Case("reason");
        String id = c.required.get(1);
        String text = c.text(DRAFT, List.of(id)).replace(" — a technical row with no meaning for a business reader", " — ");
        Check.equal(3, c.check(text, true).exit, "exit code");
        Check.equal(1, c.kinds().size(), "one finding: " + c.kinds());
        Check.that(c.kinds().get(0).startsWith("not_described_without_reason@"), "exactly its finding");
        Check.equal(id, c.findings().value(c.findings().rows.get(0), "row_id"), "the row");
    }

    public void testAReviewedDocumentNeedsNoFindingsAndAReviewLog() {
        Case c = new Case("reviewed");
        String reviewed = "Status: reviewed 2026-11-23 by the review skill";
        Check.equal(3, c.check(c.text(reviewed, List.of()), true).exit, "reviewed, no review log beside it");
        Check.equal(List.of("reviewed_without_review_log@1"), c.kinds(), "exactly its finding");

        TestSupport.write(c.document.resolveSibling("constructed-sa.md.review.csv"), "\"unit_id\",\"finding\"\n");
        TestSupport.Run run = c.check(c.text(reviewed, List.of()), true);
        Check.equal(0, run.exit, "reviewed, with a review log and no finding: " + run.out);
        Check.contains(run.out, "(status: reviewed)", "summary: status");

        String faulty = c.text(reviewed, List.of()).replace(Citation.of(c.required.get(0)), "");
        Check.equal(3, c.check(faulty, true).exit, "reviewed, with a finding");
        Check.equal(List.of("not_covered", "reviewed_with_findings@1"), c.kinds(), "the finding, and that a reviewed document has one");

        Check.equal(3, c.check(c.text("# A title where the status should be", List.of()), true).exit, "no status line");
        Check.equal(List.of("bad_status@1"), c.kinds(), "exactly its finding");
        Check.equal(3, c.check(c.text("Status: reviewed", List.of()), true).exit, "reviewed with no date");
        Check.equal(List.of("bad_status@1"), c.kinds(), "is not a status");
    }

    public void testAMissingDocumentOrSliceIsNamedAndExits2() {
        Case c = new Case("missing");
        TestSupport.Run noDocument = TestSupport.run("check-doc", c.document.toString(), "--slice", c.slice.toString());
        Check.equal(2, noDocument.exit, "no document");
        Check.contains(noDocument.err, "the document " + c.document + " was not found", "names it");
        TestSupport.write(c.document, c.text(DRAFT, List.of()));
        TestSupport.Run noSlice = TestSupport.run("check-doc", c.document.toString(), "--slice", c.slice.resolve("nowhere").toString());
        Check.equal(2, noSlice.exit, "no slice");
        Check.contains(noSlice.err, "the Slice folder " + c.slice.resolve("nowhere") + " was not found", "names it");
        Check.equal(2, TestSupport.run("check-doc", c.document.toString()).exit, "no --slice");
        Check.equal(2, TestSupport.run("check-doc", c.document.toString(), "--slice", c.document.getParent().toString()).exit, "a folder that is not a slice");
        Check.equal(2, TestSupport.run("check-doc").exit, "no arguments");
        Check.that(!Files.exists(c.document.resolveSibling("constructed-sa.md.check.csv")), "nothing is written on wrong usage");
        Check.contains(TestSupport.run("--help").out, "check-doc", "listed among the commands");
    }

    /** Story 2.3's Design Notes: the rows to cover are a parameter, so a to-be design can pass its own list. */
    public void testTheRequiredRowsAreAParameterOfTheCheck() {
        Case c = new Case("parameter");
        Path list = c.document.resolveSibling("tab-rows.csv");
        TestSupport.write(list, "\"row_id\",\"table\"\n\"" + c.required.get(0) + "\",\"logical_tables\"\n"
                + "\"constructed-pair::pres::Constructed SA\",\"presentation_subject_areas\"\n");
        TestSupport.write(c.document, c.text(DRAFT, List.of()));
        TestSupport.write(c.document.resolveSibling("constructed-sa.answers.md"), ANSWERS);
        TestSupport.Run run = TestSupport.run("check-doc", c.document.toString(), "--slice", c.slice.toString(), "--required", list.toString());
        Check.equal(3, run.exit, "exit code");
        Check.contains(run.out, "required:   2 rows", "the list given is the list checked");
        Check.equal(List.of("not_covered"), c.kinds(), "the row of the list the document does not cite");

        // And directly, with no Slice at all.
        Map<String, String> known = new LinkedHashMap<>();
        known.put("e::pres::A.B", "presentation_tables");
        known.put("e::pres::A.B.C", "presentation_columns");
        DocCheckResult result = DocCheck.check("Status: draft, not reviewed\nB holds C [kb:e::pres::A.B.C].\n## Open questions\n", null,
                List.of(new String[] {"e::pres::A.B.C", "presentation_columns"}, new String[] {"e::pres::A.B", "presentation_tables"}), known, false);
        Check.equal(1, result.findings.size(), "one finding");
        Check.equal("not_covered", result.findings.get(0)[0], "the row not cited");
        Check.equal("e::pres::A.B", result.findings.get(0)[1], "which one");
        Check.equal(2, result.rowsRequired, "rows required");
        Check.equal(1, result.rowsCovered, "rows covered");
    }

    /** A document for every slice of a sample estate, written from the packets' own citations, passes. */
    public void testADocumentThatRepeatsEveryPacketCitationPassesOnTheSampleEstate() {
        Path estate = TestSupport.root().resolve("estates/sampleapp");
        if (!Files.isDirectory(estate.resolve("rpd"))) {
            throw new SkipException("the sample estates are not under estates/");
        }
        Path work = TestSupport.work("check-doc-sample");
        TestSupport.extract(estate, work.resolve("kb"));
        Check.equal(0, SliceFixture.slice(work.resolve("kb"), work.resolve("slices"), "--subject-area", "Sample Sales").exit, "slice");
        Path slice = work.resolve("slices/sample-sales");
        // What a skill does: one section per unit, repeating the citations of the packet it read.
        StringBuilder text = new StringBuilder(DRAFT + "\n# Sample Sales\n\n");
        for (Map.Entry<String, String> packet : SliceFixture.packets(slice).entrySet()) {
            text.append("## ").append(packet.getKey()).append("\n\n");
            for (String line : packet.getValue().split("\n")) {
                for (Citation citation : Citation.find(line, 0)) {
                    text.append("A statement ").append(Citation.of(citation.rowId)).append(".\n");
                }
            }
        }
        text.append("\n## Open questions\n\n## Not described\n");
        Path document = TestSupport.write(work.resolve("as-is/sampleapp/sample-sales.md"), text.toString());
        TestSupport.Run run = TestSupport.run("check-doc", document.toString(), "--slice", slice.toString());
        Check.equal(0, run.exit, "exit code: " + run.out);
        KbData data = SliceFixture.read(slice);
        Check.contains(run.out, "required:   " + data.table("required_rows").rows.size() + " rows", "every required row");
        Check.contains(run.out, "findings:   0", "no finding: every citation a packet prints names a row of the slice");
    }

    // ------------------------------------------------------------------ each part against its packet

    /** Writes one part per unit that repeats every citation of the unit's packet, and returns the parts folder. */
    private static Path partsFor(Case c, Map<String, String> packets) {
        Path parts = c.document.resolveSibling("constructed-sa.parts");
        TestSupport.write(parts.resolve("000-summary.md"), DRAFT + "\n# Constructed SA\n\nRead `this` as you like: the summary is not a unit.\n");
        int position = 1;
        for (Map.Entry<String, String> packet : packets.entrySet()) {
            StringBuilder text = new StringBuilder("## " + packet.getKey() + "\n\n");
            for (String line : packet.getValue().split("\n")) {
                for (Citation citation : Citation.find(line, 0)) {
                    text.append("A statement ").append(Citation.of(citation.rowId)).append(".\n");
                }
            }
            TestSupport.write(parts.resolve(String.format("%03d-%s.md", position++, packet.getKey())), text.toString());
        }
        TestSupport.write(parts.resolve("990-open-questions.md"), "## Open questions\n");
        TestSupport.write(parts.resolve("992-not-described.md"), "## Not described\n");
        return parts;
    }

    private static TestSupport.Run assembleAndCheck(Case c) {
        Check.equal(0, TestSupport.run("assemble-doc", c.document.toString()).exit, "assemble-doc");
        return TestSupport.run("check-doc", c.document.toString(), "--slice", c.slice.toString());
    }

    public void testEachPartIsComparedWithItsPacketForCitationsAndQuotes() {
        Case c = new Case("per-unit");
        Map<String, String> packets = SliceFixture.packets(c.slice);
        Path parts = partsFor(c, packets);
        TestSupport.Run clean = assembleAndCheck(c);
        Check.equal(0, clean.exit, "a document that repeats its packets passes: " + clean.out);
        Check.contains(clean.out, "quotes:     0 texts between backticks or fence lines compared with the packets", "nothing quoted in a unit's part");
        Check.contains(clean.out, "warnings:   0", "no warning");

        // A unit whose packet holds a value between backticks, and a row only another unit's packet states.
        String unit = null;
        String value = null;
        java.util.regex.Pattern quoted = java.util.regex.Pattern.compile(": `([^`]+)` \\[kb:");
        for (Map.Entry<String, String> packet : packets.entrySet()) {
            java.util.regex.Matcher found = quoted.matcher(packet.getValue());
            if (unit == null && packet.getKey().startsWith("table-") && found.find()) {
                unit = packet.getKey();
                value = found.group(1);
            }
        }
        Check.that(unit != null, "premise: a table packet with a value between backticks");
        String foreign = null;
        for (Citation citation : Citation.find(String.join(" ", packets.get("overview").split("\n")), 0)) {
            if (foreign == null && !packets.get(unit).contains(Citation.of(citation.rowId))) {
                foreign = citation.rowId;
            }
        }
        Check.that(foreign != null, "premise: a row the overview packet states and the table packet does not");
        Path part = null;
        for (String name : TestSupport.fileNames(parts)) {
            if (name.endsWith("-" + unit + ".md")) {
                part = parts.resolve(name);
            }
        }
        String base = SliceFixture.text(part);
        int at = base.split("\n", -1).length;

        // Only a warning: a citation from another unit's packet.
        TestSupport.write(part, base + "See also the presentation table " + Citation.of(foreign) + ".\n");
        TestSupport.Run warned = assembleAndCheck(c);
        Check.equal(0, warned.exit, "a warning does not fail the check: " + warned.out);
        Check.equal(List.of("citation_from_other_unit"), TestSupport.column(c.findings(), "finding"), "it is listed in check.csv");
        Check.equal(unit, c.findings().value(c.findings().rows.get(0), "unit_id"), "with the unit of the part");
        Check.contains(c.findings().value(c.findings().rows.get(0), "detail"), "It is stated in the packet of: overview", "and where the row is stated");
        Check.contains(warned.out, "findings:   0", "no finding");
        Check.contains(warned.out, "warnings:   1 {citation_from_other_unit=1}", "one warning");
        Check.contains(warned.out, "each part against the packets:\n  " + unit + "\n    line ", "printed under its unit");

        // Quotes: what is between backticks or fence lines must be in a packet, character for character.
        TestSupport.write(part, base
                + "It is `" + value + "` in the repository.\n"                                  // copied: fine
                + "It is ` " + value + "  ` with spaces at the ends.\n"                         // ends ignored: fine
                + "It is `" + value + "x` nearly.\n"                                            // one character more
                + "It is `" + value.substring(0, value.length() - 1) + "~` nearly.\n"      // a different last character
                + "A name of the writer's own, `made_up_name`, and ``one with a ` inside``.\n"  // two quotes, neither in a packet
                + "An unclosed ` backtick is an ordinary character.\n"
                + "```\n" + value + "\n```\n"                                                   // a fenced copy: fine
                + "  ```sql\n  select made_up from nowhere\n  ```\n"                            // a fenced invention
                + "And a made-up row " + Citation.of("constructed-pair::bmm::No.Such.Row") + ".\n");
        TestSupport.Run run = assembleAndCheck(c);
        Check.equal(3, run.exit, "exit code: " + run.out);
        int first = java.util.Arrays.asList(SliceFixture.text(c.document).split("\n", -1)).indexOf("It is `" + value + "` in the repository.") + 1;
        Check.that(first > at, "premise: the added lines are in the document, from line " + first);
        List<String> expected = new ArrayList<>();
        for (int[] offsetAndKind : new int[][] {{2, 1}, {3, 1}, {4, 1}, {4, 1}, {9, 1}, {12, 0}}) {
            expected.add((offsetAndKind[1] == 1 ? "quote_not_in_packet@" : "unknown_citation@") + (first + offsetAndKind[0]));
        }
        List<String> kinds = new ArrayList<>(c.kinds());
        kinds.sort(java.util.Comparator.comparingInt(k -> Integer.parseInt(k.substring(k.indexOf('@') + 1))));
        Check.equal(expected, kinds, "exactly the quotes that are not in a packet, and the made-up row, at their lines: " + run.out);
        CsvTable findings = c.findings();
        for (String[] row : findings.rows) {
            Check.equal(unit, findings.value(row, "unit_id"), "each names the unit to correct");
        }
        Check.contains(String.join("\n", TestSupport.column(findings, "detail")), "character for character: select made_up from nowhere",
                "the detail gives the text, with the fence's indentation taken off");
        Check.contains(String.join("\n", TestSupport.column(findings, "detail")), "character for character: one with a ` inside", "a quote between double backticks");
        Check.contains(run.out, "quotes:     8 texts between backticks or fence lines compared with the packets", "how many were compared");
        Check.contains(run.out, "    line " + (first + 2) + "  ", "printed per unit with the line");
        Check.contains(run.out, "quote_not_in_packet       made_up_name", "and the text");
    }

    public void testAQuoteMayComeFromAnotherPacketAndAValueCutOverTwoPacketsIsOneQuote() {
        Map<String, String> packets = new java.util.LinkedHashMap<>();
        packets.put("a-p1", "# A (part 1 of 2)\nUnit: a-p1\n\n- Its SQL, exactly as extracted, between the two fence lines: [kb:e::phys::T.def]\n"
                + "```\nselect 1,\n  2,\n```\n(the value continues in the next part)\n\n");
        packets.put("a-p2", "# A (part 2 of 2)\nUnit: a-p2\n\n- (continued from the previous part: table)\n(the value continues from the previous part)\n"
                + "```\n  3\nfrom dual\n```\n- Name: `Only In Two` [kb:e::phys::T]\n");
        Map<String, String> known = Map.of("e::phys::T", "physical_tables", "e::phys::T.def", "physical_table_definitions");
        String document = DRAFT + "\n## A one\n\nThe query " + Citation.of("e::phys::T.def") + ":\n\n```\nselect 1,\n  2,\n  3\nfrom dual\n```\n\n"
                + "Named `Only In Two` " + Citation.of("e::phys::T") + ".\n\n```\nselect 1,\n  3\nfrom dual\n```\n\n## Open questions\n";
        List<String> unitOfLine = new ArrayList<>();
        for (int i = 0; i < document.split("\n", -1).length; i++) {
            unitOfLine.add(i == 0 ? "summary" : i < 19 ? "a-p1" : "open-questions");
        }
        obi2pbi.core.DocCheckResult result = obi2pbi.core.DocCheck.check(document, null, List.of(), known, false, unitOfLine, packets);
        List<String> kinds = new ArrayList<>();
        for (String[] finding : result.findings) {
            kinds.add(finding[0] + "@" + finding[3]);
        }
        Check.equal(List.of("citation_from_other_unit@13", "quote_not_in_packet@15"), kinds,
                "the whole value is found though a packet boundary cut it; a quote from another packet is fine; a value with a line "
                        + "left out is not in any packet; the citation from the other part is a warning");
        Check.equal(3, result.quotesChecked, "three quotes compared");
        Check.that(!result.passed() && result.failing() == 1 && result.warnings() == 1, "one finding, one warning");
        // Without the packets nothing of this is looked at, as before.
        Check.that(obi2pbi.core.DocCheck.check(document, null, List.of(), known, false, unitOfLine).passed(), "without packets: no such finding");
    }

    public void testAReviewedDocumentWithOnlyWarningsHasNoFindings() {
        Map<String, String> packets = new java.util.LinkedHashMap<>();
        packets.put("a", "- One [kb:e::x::1]\n");
        packets.put("b", "- Two [kb:e::x::2]\n");
        Map<String, String> known = Map.of("e::x::1", "t", "e::x::2", "t");
        String document = "Status: reviewed 2026-11-23\n## A\nOne " + Citation.of("e::x::1") + " and two " + Citation.of("e::x::2") + ".\n## Open questions\n";
        obi2pbi.core.DocCheckResult result = obi2pbi.core.DocCheck.check(document, null, List.of(), known, true,
                List.of("summary", "a", "a", "open-questions", "open-questions"), packets);
        Check.equal(1, result.findings.size(), "the warning is listed");
        Check.equal("citation_from_other_unit", result.findings.get(0)[0], "as a warning");
        Check.that(result.passed(), "and the reviewed document still passes: a warning is not a finding");
    }
}
