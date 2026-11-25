package obi2pbi;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import obi2pbi.core.AnalysisColumn;
import obi2pbi.core.AnalysisCriteria;
import obi2pbi.core.Estate;
import obi2pbi.core.LineageRow;
import obi2pbi.core.PresentationColumn;
import obi2pbi.core.PresentationTable;
import obi2pbi.core.ReportLineage;
import obi2pbi.core.ReportLineageRow;
import obi2pbi.core.SubjectArea;
import obi2pbi.in.kb.CsvTable;

/**
 * Linking report columns to the repository (story 1.10).
 *
 * <p>Two estates with both halves are used:
 * <ul>
 * <li>the first attempt's hand-made pair, {@code reference/test-corpus/constructed-cases/}, whose expected
 * result is in its {@code GROUND-TRUTH.md} and in guide 02 section 6.5;</li>
 * <li>{@code src/test/fixtures/linked-estate}, written for this story so that every value of
 * {@code resolution} and of {@code source_state} occurs. Its repository has one subject area
 * {@code Linked SA} with one table {@code Facts}; the column {@code Amount} is mapped by two table sources,
 * and their files are named so that the one read last ({@code lts-2.xml}, source {@code B Aggregate}) is
 * not the one whose lineage row sorts first ({@code A Detail}). That is the one case where the jar and the
 * reference implementation differ (deliberate difference 22). {@code Margin} is a formula with no mapping,
 * {@code Note} is mapped to a literal, {@code Unbound} has no logical column, and {@code Region} and
 * {@code REGION} differ only in case. The expected values below were worked out from guide 02 section 6
 * and then checked against the reference implementation run on the same folder: the 20 rows are equal
 * except the three cells difference 22 changes.</li>
 * </ul>
 */
public final class ReportLineageTest {

    private static final String E = "linked::cat::shared/linked/";
    private static final String AREA = "linked::pres::Linked SA";
    private static final String PRES = "linked::pres::Linked SA.Facts.";
    private static final String BMM = "linked::bmm::Linked BM.Fact.";
    private static final String PHYS = "linked::phys::LK.";

    private static Path linkedFixture() {
        return TestSupport.root().resolve("src/test/fixtures/linked-estate");
    }

    /** Copies the fixture's halves into an estate folder named {@code linked}; either may be left out. */
    private static Path linkedEstate(Path work, boolean rpd, boolean catalog) throws Exception {
        Path estate = work.resolve("linked");
        if (rpd) {
            TestSupport.copyTree(linkedFixture().resolve("rpd"), estate.resolve("rpd"));
        }
        if (catalog) {
            TestSupport.copyTree(linkedFixture().resolve("catalog"), estate.resolve("catalog"));
        }
        return estate;
    }

    private static void expect(CsvTable table, String column, String formula, String areaId, String presentation,
            String resolution, String logical, String physical, String state) {
        String[] row = TestSupport.row(table, "row_id", E + column + "#lineage");
        String what = column + " ";
        Check.equal(E + column, table.value(row, "analysis_column_id"), what + "analysis_column_id");
        Check.equal(formula, table.value(row, "reference_text"), what + "reference_text, exactly as stored");
        Check.equal(areaId, table.value(row, "subject_area_id"), what + "subject_area_id");
        Check.equal(resolution, table.value(row, "resolution"), what + "resolution");
        Check.equal(presentation.isEmpty() ? "" : PRES + presentation, table.value(row, "presentation_column_id"),
                what + "presentation_column_id");
        Check.equal(logical.isEmpty() ? "" : BMM + logical, table.value(row, "logical_column_id"),
                what + "logical_column_id");
        Check.equal(physical.isEmpty() ? "" : PHYS + physical, table.value(row, "physical_column_id"),
                what + "physical_column_id");
        Check.equal(state, table.value(row, "source_state"), what + "source_state");
    }

    public void testTheConstructedPairGivesTheGroundTruthRows() throws Exception {
        Path work = TestSupport.work("lineage-pair");
        Path estate = work.resolve("constructed-pair");
        TestSupport.copyTree(TestSupport.constructed(), estate.resolve("rpd"));
        TestSupport.copyTree(TestSupport.constructedCatalog(), estate.resolve("catalog"));
        Path kb = work.resolve("kb");
        TestSupport.Run run = TestSupport.extract(estate, kb);
        Check.equal(0, run.exit, "every reference of the pair resolves: " + run.out + run.err);
        Check.contains(run.out, "(rpd + catalog)", "both halves were read");

        CsvTable table = TestSupport.table(kb, "report_lineage");
        Check.equal(List.of("row_id", "rpd_id", "analysis_id", "criteria_id", "analysis_column_id", "subject_area",
                "subject_area_id", "reference_text", "presentation_column_id", "resolution", "logical_column_id",
                "physical_column_id", "source_state"), table.header, "the reference's columns, in its order");
        Check.equal(TestSupport.table(kb, "analysis_columns").rows.size(), table.rows.size(),
                "one row per report column");
        Check.equal(2, table.rows.size(), "two report columns");

        String report = "constructed-pair::cat::constructed/revenue by month";
        String[] amount = TestSupport.row(table, "reference_text", "\"Sales Facts\".\"Amount\"");
        Check.equal(report + "#c0.0#lineage", table.value(amount, "row_id"), "row_id");
        Check.equal(report, table.value(amount, "analysis_id"), "analysis_id");
        Check.equal(report + "#criteria0", table.value(amount, "criteria_id"), "criteria_id");
        Check.equal(report + "#c0.0", table.value(amount, "analysis_column_id"), "analysis_column_id");
        Check.equal("\"Constructed SA\"", table.value(amount, "subject_area"), "as the catalog wrote it, quotes included");
        Check.equal("constructed-pair::pres::Constructed SA", table.value(amount, "subject_area_id"), "subject_area_id");
        Check.equal("constructed-pair::pres::Constructed SA.Sales Facts.Amount",
                table.value(amount, "presentation_column_id"), "presentation_column_id");
        Check.equal("exact", table.value(amount, "resolution"), "resolution");
        Check.equal("constructed-pair::bmm::Constructed BM.Fact Sales.Amount", table.value(amount, "logical_column_id"),
                "logical_column_id");
        Check.equal("constructed-pair::phys::CC_SCHEMA.FCT_SALES_DETAIL.AMOUNT", table.value(amount, "physical_column_id"),
                "of the two sources that map Amount, the first in lineage.csv order");
        Check.equal("sourced", table.value(amount, "source_state"), "source_state");

        String[] rolling = TestSupport.row(table, "reference_text", "\"Sales Facts\".\"Rolling Amount 3M\"");
        Check.equal("constructed-pair::pres::Constructed SA.Sales Facts.Rolling Amount 3M",
                table.value(rolling, "presentation_column_id"), "presentation_column_id");
        Check.equal("exact", table.value(rolling, "resolution"), "resolution");
        Check.equal("", table.value(rolling, "logical_column_id"), "no lineage row, so no logical column here");
        Check.equal("", table.value(rolling, "physical_column_id"), "a formula has no physical column");
        Check.equal("computed_not_sourced", table.value(rolling, "source_state"), "calculated in the business model");

        Check.equal(0, TestSupport.table(kb, "errors").rows.size(), "linking writes no error row");
    }

    public void testEveryResolutionAndSourceStateOnTheLinkedEstate() throws Exception {
        Path work = TestSupport.work("lineage-linked");
        Path kb = work.resolve("kb");
        TestSupport.Run run = TestSupport.extract(linkedEstate(work, true, true), kb);
        Check.equal(0, run.exit, run.out + run.err);
        CsvTable t = TestSupport.table(kb, "report_lineage");
        Check.equal(20, t.rows.size(), "rows");
        Check.equal(TestSupport.table(kb, "analysis_columns").rows.size(), t.rows.size(), "one row per report column");

        String q = "every outcome#c0.";
        expect(t, q + "0", "\"Facts\".\"Amount\"", AREA, "Amount", "exact", "Amount", "F_DETAIL.AMOUNT", "sourced");
        expect(t, q + "1", "\"facts\".\"QUANTITY\"", AREA, "Quantity", "case_normalised", "Quantity", "F_DETAIL.QTY",
                "sourced");
        expect(t, q + "2", "\"Facts\".\"Margin\"", AREA, "Margin", "exact", "", "", "computed_not_sourced");
        expect(t, q + "3", "\"Facts\".\"Missing\"", AREA, "", "unresolved", "", "", "");
        expect(t, q + "4", "\"Facts\".\"Amount\" * 2", AREA, "", "not_a_column_reference", "", "", "");
        expect(t, q + "5", "XSA('u'.'d').\"Columns\".\"X\"", AREA, "", "outside_semantic_model", "", "", "");
        expect(t, q + "6", "\n   \"Facts\".\"Quantity\"  ", AREA, "Quantity", "exact", "Quantity", "F_DETAIL.QTY",
                "sourced");
        expect(t, q + "7", "", AREA, "", "not_a_column_reference", "", "", "");
        expect(t, q + "8", "\"Linked SA\".\"Facts\".\"Amount\"", AREA, "", "not_a_column_reference", "", "", "");
        expect(t, q + "9", "\"Facts\".\"Note\"", AREA, "Note", "exact", "Note", "", "sourced");
        expect(t, q + "10", "\"Facts\".\"region\"", AREA, "REGION", "case_normalised", "Region Upper",
                "F_DETAIL.REGION_UC", "sourced");
        expect(t, q + "11", "BIN(XSA('u'.'d').\"Columns\".\"X\" BY 1)", AREA, "", "not_a_column_reference", "", "", "");
        expect(t, q + "12", "xsa('u'.'d').\"Columns\".\"X\"", AREA, "", "not_a_column_reference", "", "", "");
        expect(t, q + "13", "\"Facts\".\"Unbound\"", AREA, "Unbound", "exact", "", "", "computed_not_sourced");
        expect(t, q + "14", "\"Facts\".\"Am\"\"ount\"", AREA, "", "not_a_column_reference", "", "", "");

        String bare = "\"Facts\".\"Amount\"";
        expect(t, "area in other case#c0.0", bare, AREA, "Amount", "case_normalised", "Amount", "F_DETAIL.AMOUNT", "sourced");
        expect(t, "area without quotes#c0.0", bare, AREA, "Amount", "exact", "Amount", "F_DETAIL.AMOUNT", "sourced");
        expect(t, "area not in estate#c0.0", bare, "", "", "unresolved", "", "", "");
        expect(t, "area not stated#c0.0", bare, "", "", "unresolved", "", "", "");
        expect(t, "area is a dataset#c0.0", bare, "", "", "unresolved", "", "", "");

        Check.equal("\"linked sa\"", t.value(TestSupport.row(t, "row_id", E + "area in other case#c0.0#lineage"),
                "subject_area"), "the subject area stays as the catalog wrote it");
        Check.equal("XSA('u'.'d')", t.value(TestSupport.row(t, "row_id", E + "area is a dataset#c0.0#lineage"),
                "subject_area"), "an uploaded dataset as subject area is kept as written");

        Set<String> resolutions = new TreeSet<>(TestSupport.column(t, "resolution"));
        Check.equal(new TreeSet<>(List.of("case_normalised", "exact", "not_a_column_reference", "outside_semantic_model",
                "unresolved")), resolutions, "every value of resolution occurs");
        Check.equal(new TreeSet<>(List.of("", "computed_not_sourced", "sourced")),
                new TreeSet<>(TestSupport.column(t, "source_state")), "every value of source_state occurs");
        Check.equal(0, TestSupport.table(kb, "errors").rows.size(), "an unresolved report column is not an error");
    }

    /** Deliberate difference 22. */
    public void testOfSeveralLineageRowsTheFirstInLineageCsvOrderIsUsed() throws Exception {
        Path work = TestSupport.work("lineage-several");
        Path kb = work.resolve("kb");
        TestSupport.extract(linkedEstate(work, true, true), kb);
        CsvTable lineage = TestSupport.table(kb, "lineage");
        List<String[]> rows = TestSupport.rows(lineage, "presentation_column_id", PRES + "Amount");
        Check.equal(2, rows.size(), "Amount is mapped by two table sources");
        Check.equal(BMM + "src.A Detail", lineage.value(rows.get(0), "source_id"), "the first row in the file");
        Check.equal(BMM + "src.B Aggregate", lineage.value(rows.get(1), "source_id"),
                "the second row; its file is read last, so the reference implementation would use it");
        CsvTable t = TestSupport.table(kb, "report_lineage");
        Check.equal(lineage.value(rows.get(0), "physical_column_id"),
                t.value(TestSupport.row(t, "row_id", E + "every outcome#c0.0#lineage"), "physical_column_id"),
                "the report column is linked through the first row");
    }

    public void testTheChoiceAmongLineageRowsDoesNotDependOnTheirOrderInMemory() {
        List<LineageRow> rows = new ArrayList<>(List.of(
                new LineageRow("e::pres::A.T.C#e::bmm::M.T.src.S2", "e", "e::pres::A.T.C", "e::bmm::M.T.C", "e::bmm::M.T.src.S2", "t2", "p2"),
                new LineageRow("e::pres::A.T.C#e::bmm::M.T.src.S1", "e", "e::pres::A.T.C", "e::bmm::M.T.C", "e::bmm::M.T.src.S1", "t1", "p1"),
                new LineageRow("e::pres::A.T.C#e::bmm::M.T.src.S3", "e", "e::pres::A.T.C", "e::bmm::M.T.C", "e::bmm::M.T.src.S3", "t3", "p3")));
        for (int turn = 0; turn < 3; turn++) {
            Estate estate = bareEstate("e", "\"A\"", "\"T\".\"C\"");
            estate.lineage.addAll(rows);
            ReportLineage.resolve(estate);
            Check.equal("p1", estate.reportLineage.get(0).physicalColumnId, "the row with the smallest Row identifier, turn " + turn);
            Collections.rotate(rows, 1);
        }
    }

    public void testACatalogWithoutARepositoryLeavesEveryReferenceUnresolved() throws Exception {
        Path work = TestSupport.work("lineage-catalog-only");
        Path kb = work.resolve("kb");
        TestSupport.Run run = TestSupport.extract(linkedEstate(work, false, true), kb);
        Check.equal(0, run.exit, run.out + run.err);
        CsvTable t = TestSupport.table(kb, "report_lineage");
        Check.equal(20, t.rows.size(), "still one row per report column");
        for (String[] row : t.rows) {
            String formula = obi2pbi.core.Text.strip(t.value(row, "reference_text"));
            boolean bare = formula.matches("\"[^\"]+\"\\.\"[^\"]+\"");
            String expected = bare ? "unresolved" : formula.startsWith("XSA(") ? "outside_semantic_model" : "not_a_column_reference";
            Check.equal(expected, t.value(row, "resolution"), t.value(row, "row_id"));
            for (String column : List.of("subject_area_id", "presentation_column_id", "logical_column_id",
                    "physical_column_id", "source_state")) {
                Check.equal("", t.value(row, column), column + " of " + t.value(row, "row_id"));
            }
        }
    }

    public void testARepositoryWithoutACatalogGivesAnEmptyTableAndLineageIsUnchangedByACatalog() throws Exception {
        Path alone = TestSupport.work("lineage-rpd-only");
        TestSupport.extract(linkedEstate(alone, true, false), alone.resolve("kb"));
        Check.equal(0, TestSupport.table(alone.resolve("kb"), "report_lineage").rows.size(), "no report columns, no rows");

        Path both = TestSupport.work("lineage-rpd-and-catalog");
        TestSupport.extract(linkedEstate(both, true, true), both.resolve("kb"));
        Check.sameBytes(Files.readAllBytes(alone.resolve("kb/lineage.csv")), Files.readAllBytes(both.resolve("kb/lineage.csv")),
                "lineage.csv is the repository's alone");
    }

    public void testNamesAreMatchedOnlyInsideOneEstate() {
        Estate estate = bareEstate("e", "\"A\"", "\"T\".\"C\"");
        ReportLineage.resolve(estate);
        Check.equal("exact", estate.reportLineage.get(0).resolution, "same estate: found");

        Estate other = bareEstate("e", "\"A\"", "\"T\".\"C\"");
        other.analysisColumns.clear();
        other.analysisColumns.add(column("f", "f::cat::r#c0.0", "f::cat::r#criteria0", "\"T\".\"C\""));
        other.analysisCriteria.add(new AnalysisCriteria("f::cat::r#criteria0", "f", "f::cat::r", 0, "\"A\""));
        ReportLineage.resolve(other);
        ReportLineageRow row = other.reportLineage.get(0);
        Check.equal("unresolved", row.resolution, "the same names in another estate are not a match");
        Check.equal("", row.subjectAreaId, "nor is the subject area");
        Check.equal("f::cat::r#c0.0#lineage", row.rowId, "the row belongs to the report's estate");
    }

    public void testResolvingTwiceGivesTheSameRows() {
        Estate estate = bareEstate("e", "\"A\"", "\"T\".\"C\"");
        ReportLineage.resolve(estate);
        ReportLineage.resolve(estate);
        Check.equal(1, estate.reportLineage.size(), "the rows are replaced, not added to");
    }

    public void testAColumnWhoseCriteriaBlockIsNotFoundIsUnresolvedWithAnEmptySubjectArea() {
        Estate estate = bareEstate("e", "\"A\"", "\"T\".\"C\"");
        estate.analysisCriteria.clear();
        ReportLineage.resolve(estate);
        ReportLineageRow row = estate.reportLineage.get(0);
        Check.equal("", row.subjectArea, "no block, no stated subject area");
        Check.equal("unresolved", row.resolution, "a bare reference with nowhere to look it up");
    }

    public void testEveryQuoteAtBothEndsOfTheSubjectAreaIsTakenOff() {
        Estate estate = bareEstate("e", "\"\"A\"\"\"", "\"T\".\"C\"");
        ReportLineage.resolve(estate);
        Check.equal("exact", estate.reportLineage.get(0).resolution, "all leading and trailing quotes are ignored");
        Check.equal("\"\"A\"\"\"", estate.reportLineage.get(0).subjectArea, "and the stored value keeps them");
    }

    /** One subject area A, table T, column C in estate {@code e}; one report column with the given formula. */
    private static Estate bareEstate(String rpd, String statedArea, String formula) {
        Estate estate = new Estate();
        estate.subjectAreas.add(new SubjectArea("e::pres::A", "e", "A", "M", "", "", List.of()));
        estate.presentationTables.add(new PresentationTable("e::pres::A.T", "e", "e::pres::A", "T", "", List.of()));
        estate.presentationColumns.add(new PresentationColumn("e::pres::A.T.C", "e", "e::pres::A.T", "C", "e::bmm::M.T.C", "", ""));
        estate.analysisCriteria.add(new AnalysisCriteria(rpd + "::cat::r#criteria0", rpd, rpd + "::cat::r", 0, statedArea));
        estate.analysisColumns.add(column(rpd, rpd + "::cat::r#c0.0", rpd + "::cat::r#criteria0", formula));
        return estate;
    }

    private static AnalysisColumn column(String rpd, String rowId, String criteriaId, String formula) {
        return new AnalysisColumn(rowId, rpd, rpd + "::cat::r", criteriaId, 0, "c1", "", "", "", "", formula, false, false);
    }
}
