package obi2pbi;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import obi2pbi.core.KbData;
import obi2pbi.core.KbRows;
import obi2pbi.core.SliceSchema;

/**
 * {@code slice <kb> --tab <work/tab>}: a Slice cut from a tab's confirmed decisions (story 4.1; Architecture
 * AD-9, AD-11). The main estate is the first attempt's constructed pair (one subject area, "Constructed SA", with
 * Amount and Rolling Amount 3M, and a report "revenue by month" on a dashboard page).
 */
public final class TabSliceTest {

    private static final String ITEMS = "\"item_id\",\"kind\",\"fsd_anchor\",\"fsd_text\",\"existing_id\"\n";
    private static final String MATCHES = "\"item_id\",\"fsd_anchor\",\"fsd_text\",\"row_id\",\"confidence\",\"basis\",\"status\"\n";
    private static final String DECISIONS = "\"item_id\",\"decision\",\"row_id\",\"note\"\n";
    private static final String AMOUNT = "cp::pres::Constructed SA.Sales Facts.Amount";
    private static final String ROLLING_LOGICAL = "cp::bmm::Constructed BM.Fact Sales.Rolling Amount 3M";
    private static final String REPORT_COLUMN = "cp::cat::constructed/revenue by month#c0.1";

    private static Path pairKb;

    /** The constructed pair as estate "cp". */
    private static Path pairKb() throws Exception {
        if (pairKb == null) {
            Path work = TestSupport.work("tab-slice-kb");
            Path estate = TestSupport.estateFrom(work, "cp", TestSupport.constructed());
            TestSupport.copyTree(TestSupport.constructedCatalog(), estate.resolve("catalog"));
            pairKb = work.resolve("kb");
            Check.equal(0, TestSupport.extract(estate, pairKb).exit, "extract");
        }
        return pairKb;
    }

    /** A tab folder with the three files. */
    private static Path tab(String name, String items, String matches, String decisions) {
        Path tab = TestSupport.work("tab-slice-" + name).resolve(name);
        TestSupport.write(tab.resolve("fsd_items.csv"), ITEMS + items);
        TestSupport.write(tab.resolve("matches.csv"), MATCHES + matches);
        if (decisions != null) {
            TestSupport.write(tab.resolve("decisions.csv"), DECISIONS + decisions);
        }
        return tab;
    }

    private static final String HAPPY_ITEMS = "\"R1\",\"replaces\",\"p1\",\"the page\",\"\"\n"
            + "\"K1\",\"measure\",\"t1.r2.c2\",\"Amount sold\",\"\"\n"
            + "\"K2\",\"calculation\",\"t1.r3.c2\",\"Amount over three months\",\"\"\n"
            + "\"K3\",\"calculation\",\"t1.r4.c2\",\"Rolling amount as the report shows it\",\"\"\n"
            + "\"K4\",\"measure\",\"t1.r5.c2\",\"Cost\",\"\"\n"
            + "\"K5\",\"attribute\",\"t1.r6.c2\",\"Region\",\"\"\n"
            + "\"K6\",\"attribute\",\"t1.r7.c2\",\"Customer\",\"\"\n";
    private static final String HAPPY_MATCHES = "\"K1\",\"t1.r2.c2\",\"Amount sold\",\"" + AMOUNT + "\",\"95\",\"exact\",\"proposed\"\n"
            + "\"K2\",\"t1.r3.c2\",\"x\",\"" + ROLLING_LOGICAL + "\",\"80\",\"x\",\"proposed\"\n"
            + "\"K3\",\"t1.r4.c2\",\"x\",\"" + REPORT_COLUMN + "\",\"90\",\"x\",\"proposed\"\n"
            + "\"K4\",\"t1.r5.c2\",\"x\",\"" + AMOUNT + "\",\"40\",\"x\",\"proposed\"\n"
            + "\"K5\",\"t1.r6.c2\",\"x\",\"\",\"0\",\"x\",\"unmatched\"\n";
    private static final String HAPPY_DECISIONS = "\"K1\",\"confirmed\",\"\",\"the proposal\"\n"
            + "\"K2\",\"confirmed\",\"" + ROLLING_LOGICAL + "\",\"a logical column\"\n"
            + "\"K3\",\"confirmed\",\"" + REPORT_COLUMN + "\",\"a report column\"\n"
            + "\"K4\",\"rejected\",\"\",\"not this\"\n";

    public void testConfirmedColumnsOfThreeKindsGiveAClosedSliceThatIsASubsetOfTheSubjectAreaSlice() throws Exception {
        Path tab = tab("happy", HAPPY_ITEMS, HAPPY_MATCHES, HAPPY_DECISIONS);
        TestSupport.Run run = TestSupport.run("slice", pairKb().toString(), "--tab", tab.toString(), "--timestamp", "-");
        Check.equal(0, run.exit, "exit; " + run.out + run.err);
        Check.contains(run.out, "items:        6 that need data: 3 confirmed; not confirmed, contributing nothing: 1 rejected, "
                + "1 unmatched, 1 undecided", "how many items contribute nothing");
        Check.contains(run.out, "subject area: Constructed SA; as-is document as-is/cp/constructed-sa.md does not exist",
                "the subject area touched and its as-is document");
        Path folder = tab.resolve("slice");
        KbData slice = SliceFixture.read(folder);
        Check.equal("tab", slice.fact("cut_by"), "cut by tab");
        Check.equal("happy", slice.fact("tab"), "the tab's name");
        Check.equal("Constructed SA", slice.fact("subject_areas"), "the subject areas");

        // AD-9: every reference resolves inside it; the packets keep the contract with check-doc.
        Check.equal(List.of(), SchemaTest.valueProblems(folder), "every declared link resolves inside the slice");
        Set<String> all = SliceFixture.rowIds(SliceFixture.read(pairKb()));
        Check.equal(List.of(), SliceFixture.danglingReferences(slice, all), "every reference resolves inside the slice");
        SliceFixture.checkPackets(folder, "tab");

        // A subset of the subject-area Slice, table by table.
        Path areaSlices = TestSupport.work("tab-slice-happy-area");
        Check.equal(0, SliceFixture.slice(pairKb(), areaSlices, "--all").exit, "the subject-area slice");
        KbData area = SliceFixture.read(areaSlices.resolve("constructed-sa"));
        for (String table : slice.tableNames()) {
            if (List.of(SliceSchema.UNITS, SliceSchema.REQUIRED_ROWS, SliceSchema.TAB_FIELDS, "schema", "schema_tables",
                    "manifest").contains(table)) {
                continue;
            }
            Set<String> have = rows(area.table(table));
            for (String row : rows(slice.table(table))) {
                Check.that(have.contains(row), table + ": a row of the tab slice is in the subject-area slice: " + row);
            }
        }

        // What the three kinds of column bring.
        Check.that(rows(slice.table("presentation_columns")).stream().anyMatch(r -> r.startsWith(AMOUNT + "|")),
                "a presentation column");
        Check.that(rows(slice.table("logical_columns")).stream().anyMatch(r -> r.startsWith(ROLLING_LOGICAL + "|")),
                "a logical column");
        Check.equal(1, slice.table("analyses").rows.size(), "a report column brings its report");
        Check.that(slice.table("dashboard_pages").rows.size() == 1, "and the page that places it");

        // tab_fields: one required row per item, in the FSD's order.
        KbRows fields = slice.table(SliceSchema.TAB_FIELDS);
        List<String> statuses = new ArrayList<>();
        for (String[] row : fields.rows) {
            statuses.add(fields.get(row, "item_id") + " " + fields.get(row, "status"));
        }
        Check.equal(List.of("K1 confirmed", "K2 confirmed", "K3 confirmed", "K4 rejected", "K5 unmatched", "K6 undecided"),
                statuses, "every item, the page row left out");
        Check.equal(AMOUNT, fields.get(fields.rows.get(0), "row_ids"), "an empty row_id is the item's one proposal");
        Set<String> required = SliceFixture.required(slice);
        for (String[] row : fields.rows) {
            Check.that(required.contains(fields.get(row, "row_id")), "a required row per item: " + fields.get(row, "row_id"));
        }
        String packet = SliceFixture.text(folder.resolve("packets/tab-fields.md"));
        Check.contains(packet, "- Item K4 (measure), FSD anchor t1.r5.c2: NOT CONFIRMED (rejected): contributes nothing "
                + "[kb:item::K4] (REQUIRED)", "a rejected item is listed as not confirmed");
        Check.contains(packet, "    - Column: presentation column \"Sales Facts\".\"Amount\", logical column \"Fact Sales\".\"Amount\", "
                + "a measure (unit table-01-fact-sales) -> " + AMOUNT, "a confirmed column, with its unit");
        Check.contains(SliceFixture.text(folder.resolve("packets/overview.md")), "Overview of tab \"happy\"", "the tab's overview");
        Check.contains(SliceFixture.text(folder.resolve("packets/table-01-fact-sales.md")), "CONFIRMED FOR THE TAB, items K1",
                "the logical table's unit says which items use the column");
    }

    public void testASecondRunGivesTheSameFilesApartFromTheManifest() throws Exception {
        Path tab = tab("rerun", HAPPY_ITEMS, HAPPY_MATCHES, HAPPY_DECISIONS);
        Check.equal(0, TestSupport.run("slice", pairKb().toString(), "--tab", tab.toString()).exit, "first");
        java.util.Map<String, String> first = SliceFixture.packets(tab.resolve("slice"));
        Check.equal(0, TestSupport.run("slice", pairKb().toString(), "--tab", tab.toString()).exit, "second");
        Check.equal(first, SliceFixture.packets(tab.resolve("slice")), "the same packets");
    }

    public void testTwoSubjectAreasAreBothPrintedWithTheirAsIsDocuments() throws Exception {
        // Estate "answers-test" has an as-is document for a subject area named "Sample Sales" (as-is/answers-test/).
        SliceFixture fixture = SliceFixture.estate("answers-test").area().column("Amount", true, "").column("Qty", true, "");
        fixture.add("presentation_subject_areas", "row_id", fixture.pres("Sample Sales"), "name", "Sample Sales",
                "business_model", "BM");
        fixture.add("presentation_tables", "row_id", fixture.pres("Sample Sales.T2"), "subject_area_id",
                fixture.pres("Sample Sales"), "name", "T2");
        fixture.add("presentation_columns", "row_id", fixture.pres("Sample Sales.T2.Qty"), "table_id",
                fixture.pres("Sample Sales.T2"), "name", "Qty", "logical_column_id", fixture.bmm("BM.F.Qty"));
        Path kb = fixture.write(TestSupport.work("tab-slice-two-kb").resolve("kb"));
        Path tab = tab("two", "\"K1\",\"measure\",\"p1\",\"Amount\",\"\"\n\"K2\",\"measure\",\"p2\",\"Quantity\",\"\"\n", "",
                "\"K1\",\"confirmed\",\"" + fixture.pres("SA.T.Amount") + "\",\"\"\n"
                        + "\"K2\",\"confirmed\",\"" + fixture.pres("Sample Sales.T2.Qty") + "\",\"\"\n");
        TestSupport.Run run = TestSupport.run("slice", kb.toString(), "--tab", tab.toString());
        Check.equal(0, run.exit, "exit; " + run.out + run.err);
        Check.contains(run.out, "subject area: SA; as-is document as-is/answers-test/sa.md does not exist", "the first");
        Check.contains(run.out, "subject area: Sample Sales; as-is document as-is/answers-test/sample-sales.md exists (Status: ",
                "the second, with its status line");
        Check.equal("SA\nSample Sales", SliceFixture.read(tab.resolve("slice")).fact("subject_areas"), "both in the manifest");
    }

    public void testNothingConfirmedOrNoDecisionsWritesNothingAndExits2() throws Exception {
        Path rejected = tab("none", HAPPY_ITEMS, HAPPY_MATCHES, "\"K4\",\"rejected\",\"\",\"\"\n");
        TestSupport.Run run = TestSupport.run("slice", pairKb().toString(), "--tab", rejected.toString());
        Check.equal(2, run.exit, "nothing confirmed");
        Check.contains(run.err, "confirms no column", "says so");
        Check.that(!Files.exists(rejected.resolve("slice")), "nothing written");
        Path missing = tab("missing", HAPPY_ITEMS, HAPPY_MATCHES, null);
        run = TestSupport.run("slice", pairKb().toString(), "--tab", missing.toString());
        Check.equal(2, run.exit, "no decisions.csv");
        Check.contains(run.err, "has no decisions.csv", "says so");
        Check.that(!Files.exists(missing.resolve("slice")), "nothing written");
        Check.equal(2, TestSupport.run("slice", pairKb().toString(), "--tab", missing.toString(), "--all").exit,
                "--tab with --all is wrong usage");
    }

    public void testAnUnknownConfirmedIdentifierWritesNothingAndExits3() throws Exception {
        Path tab = tab("unknown", HAPPY_ITEMS, HAPPY_MATCHES, HAPPY_DECISIONS
                + "\"K6\",\"confirmed\",\"cp::pres::Constructed SA.Sales Facts.Customer\",\"\"\n");
        TestSupport.Run run = TestSupport.run("slice", pairKb().toString(), "--tab", tab.toString());
        Check.equal(3, run.exit, "exit");
        Check.contains(run.out, "decisions.csv row 5, item K6: cp::pres::Constructed SA.Sales Facts.Customer is not a "
                + "presentation, logical or report column", "says which");
        Check.that(!Files.exists(tab.resolve("slice")), "nothing written");
        Check.sameBytes((DECISIONS + HAPPY_DECISIONS + "\"K6\",\"confirmed\",\"cp::pres::Constructed SA.Sales Facts.Customer\",\"\"\n")
                .getBytes(java.nio.charset.StandardCharsets.UTF_8), Files.readAllBytes(tab.resolve("decisions.csv")),
                "decisions.csv is not changed");
    }

    public void testTheTestTabOnTheSampleEstate() throws Exception {
        Path kb = TestSupport.root().resolve("kb/sampleapp");
        Path tab = TestSupport.root().resolve("work/test-tab");
        if (!Files.isRegularFile(kb.resolve("manifest.csv")) || !Files.isRegularFile(tab.resolve("decisions.csv"))) {
            throw new SkipException("kb/sampleapp or work/test-tab is not there");
        }
        Path out = TestSupport.work("tab-slice-sample").resolve("slice");
        TestSupport.Run run = TestSupport.run("slice", kb.toString(), "--tab", tab.toString(), "--out", out.toString());
        Check.equal(0, run.exit, "exit; " + run.out + run.err);
        KbData slice = SliceFixture.read(out);
        Set<String> all = SliceFixture.rowIds(SliceFixture.read(kb));
        Check.equal(List.of(), SchemaTest.valueProblems(out), "declared links resolve");
        Check.equal(List.of(), SliceFixture.danglingReferences(slice, all), "references resolve");
        SliceFixture.checkPackets(out, "test-tab");
        Check.equal(21, slice.table(SliceSchema.TAB_FIELDS).rows.size(), "every item of the test FSD");
    }

    /** Each row as its cells joined by '|', so rows can be compared between two folders. */
    private static Set<String> rows(KbRows table) {
        Set<String> rows = new TreeSet<>();
        for (String[] row : table.rows) {
            rows.add(String.join("|", row));
        }
        return rows;
    }
}
