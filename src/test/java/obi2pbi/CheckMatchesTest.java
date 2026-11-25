package obi2pbi;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import obi2pbi.core.KbColumn;
import obi2pbi.core.KbTable;
import obi2pbi.core.MatchSchema;
import obi2pbi.in.kb.CsvTable;

/**
 * {@code check-matches}: the mechanical checks of {@code fsd_items.csv}, {@code matches.csv} and
 * {@code decisions.csv} against {@code fsd.md} and the Knowledge Base (story 3.3). One test per row of the
 * story's matrix, on small files written in the test, and the hand-made set in
 * {@code src/test/fixtures/matches/}, which has one fault of each kind.
 *
 * <p>The Knowledge Base is {@code src/test/fixtures/linked-estate} (estate {@code le}) with a dashboard added
 * in the test: one page, "page one", placing the report "every outcome", whose columns use Amount, Quantity,
 * Margin, Note and REGION, and not Region.
 *
 * <p>The faults planted in the hand-made set, one per row of {@code expected-check.csv}: item K8 has no match
 * (item_without_match); K2's confidence is "high" (bad_value); K3 is Margin, a formula (derived_column); K4 is
 * Region, which the page's report does not use (differs_from_existing_report); K5 is Note, whose mapping reads no
 * database column (no_physical_source); K6's match cites anchor t1.r17.c2 (unknown_anchor); K7 names a column
 * that does not exist (unknown_row); a match for K9, which is not an item (unknown_item); and in decisions.csv the
 * decision "approved" (bad_value), a confirmed column that does not exist (unknown_row) and item K42
 * (unknown_item). K1 and its confirmation are clean.
 */
public final class CheckMatchesTest {

    private static final String PAGE = "le::cat::shared/linked/_portal/overview/page one";
    private static final String P = "le::pres::Linked SA.Facts.";
    private static final String ITEMS_HEADER = "\"item_id\",\"kind\",\"fsd_anchor\",\"fsd_text\",\"existing_id\"\n";
    private static final String MATCHES_HEADER = "\"item_id\",\"fsd_anchor\",\"fsd_text\",\"row_id\",\"confidence\",\"basis\",\"status\"\n";
    private static final String DECISIONS_HEADER = "\"item_id\",\"decision\",\"row_id\",\"note\"\n";

    private static Path kb;
    private static Path kbWithoutCatalog;

    private static Path fixture() {
        return TestSupport.root().resolve("src/test/fixtures/matches");
    }

    /** linked-estate with a dashboard page that places the report "every outcome". */
    static Path kb() throws Exception {
        if (kb == null) {
            Path work = TestSupport.work("check-matches-kb");
            Path estate = work.resolve("le");
            TestSupport.copyTree(TestSupport.root().resolve("src/test/fixtures/linked-estate"), estate);
            TestSupport.catalogFile(estate, "shared/linked/_portal/overview/dashboard+layout",
                    "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<sawd:dashboard xmlns:saw=\"com.siebel.analytics.web/report/v1.1\" "
                            + "xmlns:sawd=\"com.siebel.analytics.web/dashboard/v1.1\" xmlVersion=\"200810300\">\n"
                            + " <sawd:dashboardPageRef path=\"Page One\" type=\"catalog\"/>\n</sawd:dashboard>\n");
            TestSupport.catalogFile(estate, "shared/linked/_portal/overview/page+one",
                    "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<sawd:dashboardPage xmlns:sawd=\"com.siebel.analytics.web/dashboard/v1.1\" "
                            + "xmlns:saw=\"com.siebel.analytics.web/report/v1.1\" xmlVersion=\"200810300\">\n <sawd:dashboardColumn name=\"Column 0\">\n"
                            + "  <sawd:reportView name=\"Outcomes\"><sawd:reportRef path=\"/shared/shared/linked/every outcome\"/></sawd:reportView>\n"
                            + " </sawd:dashboardColumn>\n</sawd:dashboardPage>\n");
            kb = work.resolve("kb");
            TestSupport.Run run = TestSupport.extract(estate, kb);
            Check.equal(0, run.exit, "extract; " + run.out);
            Check.equal(1, TestSupport.rows(TestSupport.table(kb, "dashboard_contents"), "target_id",
                    "le::cat::shared/linked/every outcome").size(), "the page places the report");
        }
        return kb;
    }

    static Path kbWithoutCatalog() throws Exception {
        if (kbWithoutCatalog == null) {
            Path work = TestSupport.work("check-matches-kb-rpd");
            Path estate = work.resolve("le");
            TestSupport.copyTree(TestSupport.root().resolve("src/test/fixtures/linked-estate/rpd"), estate.resolve("rpd"));
            kbWithoutCatalog = work.resolve("kb");
            Check.equal(0, TestSupport.extract(estate, kbWithoutCatalog).exit, "extract");
        }
        return kbWithoutCatalog;
    }

    /** A work folder: the fixture's fsd.md, and the three files as given (decisions only when not null). */
    private static Path work(String name, String items, String matches, String decisions) throws Exception {
        Path work = TestSupport.work("check-matches-" + name);
        Files.copy(fixture().resolve("fsd.md"), work.resolve("fsd.md"));
        TestSupport.write(work.resolve("fsd_items.csv"), ITEMS_HEADER + items);
        TestSupport.write(work.resolve("matches.csv"), MATCHES_HEADER + matches);
        if (decisions != null) {
            TestSupport.write(work.resolve("decisions.csv"), DECISIONS_HEADER + decisions);
        }
        return work;
    }

    private static final String CLEAN_ITEMS = "\"PAGE\",\"replaces\",\"p2\",\"Overview / Page One\",\"" + PAGE + "\"\n"
            + "\"K1\",\"measure\",\"t1.r2.c2 t1.r2.c3\",\"Amount\",\"\"\n"
            + "\"K6\",\"measure\",\"t1.r7.c2\",\"Customers\",\"\"\n";
    private static final String CLEAN_MATCHES = "\"K1\",\"t1.r2.c2\",\"Amount\",\"" + P + "Amount\",\"95\",\"exact name\",\"proposed\"\n"
            + "\"K6\",\"t1.r7.c2\",\"Customers\",\"\",\"0\",\"no column\",\"unmatched\"\n";

    private static TestSupport.Run check(Path work, Path kbFolder) {
        return TestSupport.run("check-matches", work.toString(), "--kb", kbFolder.toString());
    }

    /** The findings as "finding file row item row_id". */
    private static List<String> findings(Path work) {
        CsvTable table = TestSupport.table(work, "check");
        Check.equal(MatchSchema.check().header(), table.header, "check.csv's columns");
        List<String> found = new java.util.ArrayList<>();
        for (String[] row : table.rows) {
            found.add(table.value(row, "finding") + " " + table.value(row, "file") + " " + table.value(row, "row") + " "
                    + table.value(row, "item_id") + " " + table.value(row, "row_id"));
        }
        return found;
    }

    // ------------------------------------------------------------------ the matrix

    public void testCleanFilesGiveOnlyAHeader() throws Exception {
        Path work = work("clean", CLEAN_ITEMS, CLEAN_MATCHES, "\"K1\",\"confirmed\",\"\",\"\"\n");
        TestSupport.Run run = check(work, kb());
        Check.equal(0, run.exit, "exit; " + run.out + run.err);
        Check.equal("\"finding\",\"severity\",\"file\",\"row\",\"item_id\",\"row_id\",\"detail\"\n",
                new String(Files.readAllBytes(work.resolve("check.csv")), StandardCharsets.UTF_8), "only the header");
        Check.contains(run.out, "compared with 1 report (15 report columns) of " + PAGE, "the existing report was used");
    }

    public void testAnUnknownIdentifierFails() throws Exception {
        Path work = work("unknown-row", CLEAN_ITEMS, CLEAN_MATCHES.replace(P + "Amount", P + "Amounts"), null);
        TestSupport.Run run = check(work, kb());
        Check.equal(3, run.exit, "exit");
        Check.equal(List.of("unknown_row matches.csv 1 K1 " + P + "Amounts"), findings(work), "the finding");
        Path physical = work("unknown-row-phys", CLEAN_ITEMS,
                CLEAN_MATCHES.replace(P + "Amount", "le::phys::LK.F_DETAIL.AMOUNT"), null);
        check(physical, kb());
        Check.contains(TestSupport.table(physical, "check").rows.get(0)[6], "is a row of physical_columns",
                "says what the identifier is when it names another kind of row");
    }

    public void testAColumnThatReachesNoDatabaseTableFailsUnlessItIsDerived() throws Exception {
        Path work = work("no-physical", CLEAN_ITEMS + "\"K5\",\"attribute\",\"t1.r6.c2\",\"Note\",\"\"\n",
                CLEAN_MATCHES + "\"K5\",\"t1.r6.c2\",\"Note\",\"" + P + "Note\",\"80\",\"exact name\",\"proposed\"\n", null);
        Check.equal(3, check(work, kb()).exit, "exit");
        Check.equal(List.of("no_physical_source matches.csv 3 K5 " + P + "Note"), findings(work), "lineage ends at no table");

        Path unbound = work("no-logical", CLEAN_ITEMS + "\"K5\",\"attribute\",\"t1.r6.c2\",\"Note\",\"\"\n",
                CLEAN_MATCHES + "\"K5\",\"t1.r6.c2\",\"Note\",\"" + P + "Unbound\",\"80\",\"x\",\"proposed\"\n", null);
        Check.equal(3, check(unbound, kb()).exit, "exit");
        Check.equal(List.of("no_physical_source matches.csv 3 K5 " + P + "Unbound"), findings(unbound),
                "no logical column at all");

        Path derived = work("derived", CLEAN_ITEMS + "\"K3\",\"calculation\",\"t1.r4.c2\",\"Margin\",\"\"\n",
                CLEAN_MATCHES + "\"K3\",\"t1.r4.c2\",\"Margin\",\"" + P + "Margin\",\"90\",\"exact name\",\"proposed\"\n", null);
        TestSupport.Run run = check(derived, kb());
        Check.equal(0, run.exit, "a warning does not fail");
        Check.equal(List.of("derived_column matches.csv 3 K3 " + P + "Margin"), findings(derived), "a derived column");
        CsvTable table = TestSupport.table(derived, "check");
        Check.equal("warning", table.value(table.rows.get(0), "severity"), "severity");
        Check.contains(table.value(table.rows.get(0), "detail"), "le::bmm::Linked BM.Fact.Amount (reaches a database column)",
                "says what the formula refers to and whether that reaches the database");
    }

    public void testAnItemWithNoRowFails() throws Exception {
        Path work = work("no-match", CLEAN_ITEMS + "\"K8\",\"measure\",\"t1.r9.c2\",\"Returns\",\"\"\n", CLEAN_MATCHES, null);
        Check.equal(3, check(work, kb()).exit, "exit");
        Check.equal(List.of("item_without_match fsd_items.csv 4 K8 "), findings(work), "the finding, on the item's row");
    }

    public void testAnAnchorNotInFsdMdFails() throws Exception {
        Path work = work("anchor", CLEAN_ITEMS.replace("t1.r2.c3", "t1.r2.c9"), CLEAN_MATCHES.replace("t1.r7.c2\",\"Customers\",\"\"",
                "[t1.r7.c2]\",\"Customers\",\"\""), null);
        Check.equal(3, check(work, kb()).exit, "exit");
        Check.equal(List.of("unknown_anchor fsd_items.csv 2 K1 "), findings(work),
                "an anchor that is not in fsd.md; one written with its brackets is accepted");
        Path malformed = work("anchor-form", CLEAN_ITEMS, CLEAN_MATCHES.replace("\"t1.r2.c2\",\"Amount\"", "\"row 2\",\"Amount\""), null);
        Check.equal(3, check(malformed, kb()).exit, "exit");
        Check.equal(List.of("unknown_anchor matches.csv 1 K1 ", "unknown_anchor matches.csv 1 K1 "), findings(malformed),
                "each word that is not an anchor");
    }

    public void testValuesTheContractDoesNotAllowFail() throws Exception {
        String[][] cases = {
            {"\"101\",\"exact name\",\"proposed\"", "confidence"},
            {"\"9.5\",\"exact name\",\"proposed\"", "confidence"},
            {"\"95\",\"exact name\",\"confirmed\"", "status"},
            {"\"20\",\"exact name\",\"proposed\"", "below 30"},
            {"\"95\",\" \",\"proposed\"", "basis"},
        };
        for (String[] c : cases) {
            Path work = work("bad-" + c[1].replace(' ', '-'), CLEAN_ITEMS,
                    CLEAN_MATCHES.replace("\"95\",\"exact name\",\"proposed\"", c[0]), null);
            Check.equal(3, check(work, kb()).exit, "exit for " + c[0]);
            Check.equal(List.of("bad_value matches.csv 1 K1 " + P + "Amount"), findings(work), "one finding for " + c[0]);
            Check.contains(TestSupport.table(work, "check").rows.get(0)[6], c[1], "names the problem");
        }
        Path empty = work("bad-empty-row", CLEAN_ITEMS, CLEAN_MATCHES.replace("\"" + P + "Amount\"", "\"\""), null);
        Check.equal(3, check(empty, kb()).exit, "exit");
        Check.equal(List.of("bad_value matches.csv 1 K1 "), findings(empty), "proposed with an empty row_id");
        Path both = work("bad-both", CLEAN_ITEMS, CLEAN_MATCHES + "\"K1\",\"t1.r2.c2\",\"Amount\",\"\",\"0\",\"x\",\"unmatched\"\n", null);
        Check.equal(3, check(both, kb()).exit, "exit");
        Check.equal(List.of("bad_value matches.csv 3 K1 "), findings(both), "an item both proposed and unmatched");
        Path kind = work("bad-kind", CLEAN_ITEMS.replace("\"measure\"", "\"figure\""), CLEAN_MATCHES, null);
        check(kind, kb());
        Check.equal(List.of("bad_value fsd_items.csv 2 K1 ", "bad_value fsd_items.csv 3 K6 "), findings(kind), "an unknown kind");
    }

    public void testAMatchTheExistingReportDoesNotUseIsAWarningNamingItsReportColumns() throws Exception {
        Path work = work("differs", CLEAN_ITEMS + "\"K4\",\"attribute\",\"t1.r5.c2\",\"Region\",\"\"\n",
                CLEAN_MATCHES + "\"K4\",\"t1.r5.c2\",\"Region\",\"" + P + "Region\",\"70\",\"same name\",\"proposed\"\n", null);
        TestSupport.Run run = check(work, kb());
        Check.equal(0, run.exit, "a warning does not fail");
        Check.equal(List.of("differs_from_existing_report matches.csv 3 K4 " + P + "Region"), findings(work), "the warning");
        Check.contains(TestSupport.table(work, "check").rows.get(0)[6],
                "report column sharing a word with the item's text: le::cat::shared/linked/every outcome#c0.10",
                "names the report column the page has instead");
        Path dashboard = work("differs-dashboard", CLEAN_ITEMS.replace(PAGE, "le::cat::shared/linked/_portal/overview/dashboard layout"),
                CLEAN_MATCHES, null);
        Check.equal(0, check(dashboard, kb()).exit, "a dashboard may be named instead of a page");
        Path wrong = work("differs-unknown", CLEAN_ITEMS.replace(PAGE, PAGE + "s"), CLEAN_MATCHES, null);
        Check.equal(3, check(wrong, kb()).exit, "exit");
        Check.equal(List.of("unknown_row fsd_items.csv 1 PAGE " + PAGE + "s"), findings(wrong), "a page that does not exist");
    }

    public void testWithoutReportTablesItRunsAndSaysNoExistingReportWasAvailable() throws Exception {
        Path work = work("no-catalog", CLEAN_ITEMS, CLEAN_MATCHES, null);
        TestSupport.Run run = check(work, kbWithoutCatalog());
        Check.equal(0, run.exit, "exit; " + run.out);
        Check.contains(run.out, "no existing report was available", "says so");
        Check.equal(List.of(), findings(work), "and finds nothing");
        Path noPage = work("no-page", CLEAN_ITEMS.substring(CLEAN_ITEMS.indexOf('\n') + 1), CLEAN_MATCHES, null);
        Check.contains(check(noPage, kb()).out, "name no page or report that the tab replaces", "no replaces row: says so");
    }

    public void testDecisionsAreCheckedWhenPresent() throws Exception {
        Path work = work("decisions", CLEAN_ITEMS, CLEAN_MATCHES, "\"K1\",\"approved\",\"\",\"\"\n"
                + "\"K1\",\"confirmed\",\"" + P + "Amountt\",\"a correction\"\n"
                + "\"K2\",\"rejected\",\"\",\"\"\n"
                + "\"K6\",\"confirmed\",\"" + P + "Quantity\",\"the Operator found one\"\n");
        Check.equal(3, check(work, kb()).exit, "exit");
        Check.equal(List.of("bad_value decisions.csv 1 K1 ", "unknown_row decisions.csv 2 K1 " + P + "Amountt",
                        "unknown_item decisions.csv 3 K2 "), findings(work),
                "a value other than confirmed or rejected; an unknown row; an unknown item; a correction to a real "
                        + "column is accepted");
    }

    public void testItNeverChangesTheSkillsOrTheOperatorsFiles() throws Exception {
        Path work = work("readonly", CLEAN_ITEMS, CLEAN_MATCHES + "\"K9\",\"t1.r2.c2\",\"x\",\"\",\"0\",\"x\",\"unmatched\"\n",
                "\"K1\",\"confirmed\",\"\",\"\"\n");
        byte[] matches = Files.readAllBytes(work.resolve("matches.csv"));
        byte[] decisions = Files.readAllBytes(work.resolve("decisions.csv"));
        byte[] items = Files.readAllBytes(work.resolve("fsd_items.csv"));
        Check.equal(3, check(work, kb()).exit, "an unknown item");
        Check.sameBytes(matches, Files.readAllBytes(work.resolve("matches.csv")), "matches.csv unchanged");
        Check.sameBytes(decisions, Files.readAllBytes(work.resolve("decisions.csv")), "decisions.csv unchanged");
        Check.sameBytes(items, Files.readAllBytes(work.resolve("fsd_items.csv")), "fsd_items.csv unchanged");
        Check.equal(List.of("check.csv", "decisions.csv", "fsd.md", "fsd_items.csv", "matches.csv"), TestSupport.fileNames(work),
                "only check.csv was added");
    }

    public void testMissingInputsAreWrongUsage() throws Exception {
        Path work = work("missing", CLEAN_ITEMS, CLEAN_MATCHES, null);
        Files.delete(work.resolve("matches.csv"));
        TestSupport.Run run = check(work, kb());
        Check.equal(2, run.exit, "no matches.csv");
        Check.contains(run.err, "has no matches.csv", "names what is missing");
        Check.equal(2, TestSupport.run("check-matches", work.toString()).exit, "no --kb");
        TestSupport.write(work.resolve("matches.csv"), "\"item_id\",\"row_id\"\n");
        run = check(work, kb());
        Check.equal(2, run.exit, "a file without the contract's columns");
        Check.contains(run.err, "matches.csv has no column fsd_anchor, fsd_text, confidence, basis, status", "names them");
        Check.equal(2, TestSupport.run("check-matches", work.toString(), "--kb", "build/no-such-kb").exit, "no Knowledge Base");
    }

    // ------------------------------------------------------------------ the hand-made set

    public void testTheHandMadeSetGivesExactlyOneFindingPerFault() throws Exception {
        Path work = TestSupport.work("check-matches-fixture");
        for (String name : List.of("fsd.md", "fsd_items.csv", "matches.csv", "decisions.csv")) {
            Files.copy(fixture().resolve(name), work.resolve(name));
        }
        TestSupport.Run run = check(work, kb());
        Check.equal(3, run.exit, "exit; " + run.out);
        Check.equal(new String(Files.readAllBytes(fixture().resolve("expected-check.csv")), StandardCharsets.UTF_8),
                new String(Files.readAllBytes(work.resolve("check.csv")), StandardCharsets.UTF_8),
                "check.csv: one row per planted fault (listed in this class's comment)");
        byte[] first = Files.readAllBytes(work.resolve("check.csv"));
        check(work, kb());
        Check.sameBytes(first, Files.readAllBytes(work.resolve("check.csv")), "the same files give the same bytes");
    }

    // ------------------------------------------------------------------ the contract and its description

    public void testSkillConventionsDescribeEveryDeclaredColumnAndValue() throws Exception {
        String conventions = new String(Files.readAllBytes(TestSupport.root().resolve("docs/skill-conventions.md")),
                StandardCharsets.UTF_8);
        for (KbTable table : List.of(MatchSchema.items(), MatchSchema.matches(), MatchSchema.decisions(), MatchSchema.check())) {
            for (KbColumn column : table.columns) {
                Check.that(!column.meaning.isEmpty(), table.name + "." + column.name + " has a meaning");
                Check.contains(conventions, "| `" + column.name + "` |", "the conventions describe " + table.name + "."
                        + column.name);
            }
        }
        for (List<String> values : List.of(MatchSchema.ITEM_KINDS, MatchSchema.STATUSES, MatchSchema.DECISION_VALUES,
                MatchSchema.FAILURES, MatchSchema.WARNINGS)) {
            for (String value : values) {
                Check.contains(conventions, "`" + value + "`", "the conventions name the value " + value);
            }
        }
    }
}
