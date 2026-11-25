package obi2pbi;

import java.nio.file.Path;
import java.util.List;
import obi2pbi.in.kb.CsvTable;

/** The presentation layer: subject areas, presentation tables and columns (story 1.4; guide 01 §2.7, §2.8, §3.5). */
public final class PresentationLayerTest {

    private static final String NS = " xmlns=\"" + TestSupport.XMLNS + "\"";
    private static final String BASE = "/oracle/bi/server/base/";

    public void testFixtureMatchesItsGroundTruth() {
        Path work = TestSupport.work("pres-testrpd");
        Path kb = work.resolve("kb");
        TestSupport.Run run = TestSupport.extract(TestSupport.estateFrom(work, "testrpd", TestSupport.testRpd()), kb);
        Check.equal(0, run.exit, "exit code: " + run.err);

        CsvTable areas = TestSupport.table(kb, "presentation_subject_areas");
        Check.equal(List.of("row_id", "rpd_id", "name", "business_model", "implicit_fact_column_id",
                "is_auto_aggregate", "aliases"), areas.header, "columns as in the reference, without the guard columns");
        Check.equal(1, areas.rows.size(), "one subject area");
        String[] area = TestSupport.row(areas, "row_id", "testrpd::pres::Sample Sales SA");
        Check.equal("Sample Sales SA", areas.value(area, "name"), "name");
        Check.equal("Sample Sales", areas.value(area, "business_model"), "business model, by its name");
        Check.equal("testrpd::bmm::Sample Sales.Fact Revenue.Revenue", areas.value(area, "implicit_fact_column_id"), "implicit fact column");
        Check.equal("true", areas.value(area, "is_auto_aggregate"), "flag as written");
        Check.equal("Old Sales SA", areas.value(area, "aliases"), "alias");

        CsvTable tables = TestSupport.table(kb, "presentation_tables");
        Check.equal(List.of("row_id", "rpd_id", "subject_area_id", "name", "display_name", "aliases"), tables.header, "columns");
        String[] table = TestSupport.row(tables, "row_id", "testrpd::pres::Sample Sales SA.Facts");
        Check.equal("testrpd::pres::Sample Sales SA", tables.value(table, "subject_area_id"), "subject area");
        Check.equal("Facts", tables.value(table, "name"), "name");
        Check.equal("", tables.value(table, "display_name"), "no display name");
        Check.equal("", tables.value(table, "aliases"), "no alias");

        CsvTable columns = TestSupport.table(kb, "presentation_columns");
        Check.equal(List.of("row_id", "rpd_id", "table_id", "name", "logical_column_id", "override_logical_name",
                "display_name"), columns.header, "columns");
        Check.equal(List.of("testrpd::pres::Sample Sales SA.Facts.Margin", "testrpd::pres::Sample Sales SA.Facts.Total Revenue"),
                TestSupport.column(columns, "row_id"), "identifiers: <estate>::pres::<subject area>.<table>.<column>");
        String[] total = TestSupport.row(columns, "name", "Total Revenue");
        Check.equal("testrpd::pres::Sample Sales SA.Facts", columns.value(total, "table_id"), "owning table");
        Check.equal("testrpd::bmm::Sample Sales.Fact Revenue.Revenue", columns.value(total, "logical_column_id"), "logical column");
        Check.equal("true", columns.value(total, "override_logical_name"), "renamed: the flag as written");
        Check.equal("Revenue $", columns.value(total, "display_name"), "display name as written");
        String[] margin = TestSupport.row(columns, "name", "Margin");
        Check.equal("testrpd::bmm::Sample Sales.Fact Revenue.Negative Margin", columns.value(margin, "logical_column_id"), "logical column");
        Check.equal("false", columns.value(margin, "override_logical_name"), "not renamed");
        Check.equal("", columns.value(margin, "display_name"), "no display name");

        CsvTable coverage = TestSupport.table(kb, "coverage");
        for (String[] expected : new String[][] {{"PresentationCatalog", "1"}, {"PresentationTable", "1"}, {"PresentationColumn", "2"}}) {
            Check.equal(expected[1], coverage.value(TestSupport.row(coverage, "object_type", expected[0]), "parsed"), expected[0] + " parsed");
        }
        // Until story 1.6 this asserted 0 read and 2 skipped: hierarchies had no reader then.
        String[] hierarchies = TestSupport.row(coverage, "object_type", "PresentationHierarchy");
        Check.equal("2", coverage.value(hierarchies, "parsed"), "both hierarchies are read");
        Check.equal("0", coverage.value(hierarchies, "skipped"), "and none is skipped");
    }

    public void testConstructedImplicitFactColumn() {
        Path work = TestSupport.work("pres-constructed");
        Path kb = work.resolve("kb");
        Check.equal(0, TestSupport.extract(TestSupport.estateFrom(work, "constructed", TestSupport.constructed()), kb).exit, "exit code");
        CsvTable areas = TestSupport.table(kb, "presentation_subject_areas");
        Check.equal("constructed::bmm::Constructed BM.Fact Sales.Amount",
                areas.value(TestSupport.row(areas, "row_id", "constructed::pres::Constructed SA"), "implicit_fact_column_id"),
                "the implicit fact column, as a Row identifier");
        Check.equal(1, TestSupport.table(kb, "presentation_tables").rows.size(), "one presentation table");
        Check.equal(2, TestSupport.table(kb, "presentation_columns").rows.size(), "two presentation columns");
        Check.equal(0, TestSupport.rows(TestSupport.table(kb, "unparsed"), "kind", "unmodelled_type").size(),
                "every type present has a reader");
    }

    public void testBrokenLinksAliasesAndUnnamedParts() {
        Path work = TestSupport.work("pres-shapes");
        Path estate = work.resolve("shapes");
        TestSupport.object(estate, "BusinessModel", "bm.xml", "<BusinessModel mdsid=\"mbm\" name=\"BM\"" + NS + "/>");
        TestSupport.object(estate, "LogicalTable", "lt.xml", "<LogicalTable mdsid=\"mlt\" name=\"Fact\"" + NS
                + " subjectAreaRef=\"" + BASE + "BusinessModel/bm.xml#mbm\"><LogicalColumn mdsid=\"mlc\" name=\"Amount\"/></LogicalTable>");
        // The table file sorts before its subject area's file: reading order must not matter.
        TestSupport.object(estate, "PresentationCatalog", "z-sa.xml", "<PresentationCatalog mdsid=\"msa\" name=\"Sales Area\"" + NS
                + " subjectAreaRef=\"" + BASE + "BusinessModel/gone.xml#nothing\""
                + " defaultFactColumnRef=\"" + BASE + "LogicalTable/gone.xml#nothing\">"
                + "<Alias name=\"Old Area\"/><Alias name=\"Older | Area\"/></PresentationCatalog>");
        TestSupport.object(estate, "PresentationCatalog", "unnamed.xml", "<PresentationCatalog mdsid=\"msx\"" + NS + "/>");
        String inArea = " containerRef=\"" + BASE + "PresentationCatalog/z-sa.xml#msa\"";
        TestSupport.object(estate, "PresentationTable", "a-t.xml", "<PresentationTable mdsid=\"mpt\" name=\"Facts\" dispName=\"VALUEOF(NQ_SESSION.CN_Facts)\""
                + NS + inArea + "><Alias name=\"Old Facts\"/>"
                + "<PresentationColumn mdsid=\"mp1\" name=\"Good\" logicalColumnRef=\"" + BASE + "LogicalTable/lt.xml#mlc\"><Alias name=\"A column's alias\"/></PresentationColumn>"
                + "<PresentationColumn mdsid=\"mp2\" name=\"Broken\" overrideLogicalName=\"true\" logicalColumnRef=\"" + BASE + "LogicalTable/gone.xml#nothing\"/>"
                + "<PresentationColumn mdsid=\"mp3\" name=\"No Link\"/>"
                + "<PresentationColumn mdsid=\"mp4\"/>"
                + "</PresentationTable>");
        TestSupport.object(estate, "PresentationTable", "orphan.xml", "<PresentationTable mdsid=\"mpo\" name=\"Orphan\"" + NS
                + " containerRef=\"" + BASE + "PresentationCatalog/gone.xml#nothing\"/>");

        Path kb = work.resolve("kb");
        TestSupport.Run run = TestSupport.extract(estate, kb);
        Check.equal(3, run.exit, "exit 3: errors were written. " + run.err);

        CsvTable areas = TestSupport.table(kb, "presentation_subject_areas");
        Check.equal(1, areas.rows.size(), "the named subject area");
        Check.equal("", areas.value(areas.rows.get(0), "business_model"), "a business model that is not found leaves the cell empty");
        Check.equal("", areas.value(areas.rows.get(0), "implicit_fact_column_id"), "and so does an implicit fact column that is not found");
        Check.equal("Old Area | Older | Area", areas.value(areas.rows.get(0), "aliases"), "aliases in source order, joined with ' | '");

        CsvTable tables = TestSupport.table(kb, "presentation_tables");
        Check.equal(List.of("shapes::pres::Sales Area.Facts"), TestSupport.column(tables, "row_id"), "the table, though its subject area's file is read later");
        Check.equal("VALUEOF(NQ_SESSION.CN_Facts)", tables.value(tables.rows.get(0), "display_name"), "display name as written");
        Check.equal("Old Facts", tables.value(tables.rows.get(0), "aliases"), "the table's own alias only, not its column's");

        CsvTable columns = TestSupport.table(kb, "presentation_columns");
        Check.equal(List.of("shapes::pres::Sales Area.Facts.Broken", "shapes::pres::Sales Area.Facts.Good", "shapes::pres::Sales Area.Facts.No Link"),
                TestSupport.column(columns, "row_id"), "a column whose link is broken is still written");
        Check.equal("shapes::bmm::BM.Fact.Amount", columns.value(TestSupport.row(columns, "name", "Good"), "logical_column_id"), "a link that resolves");
        String[] broken = TestSupport.row(columns, "name", "Broken");
        Check.equal("", columns.value(broken, "logical_column_id"), "a link that does not resolve leaves the cell empty");
        Check.equal("true", columns.value(broken, "override_logical_name"), "the rest of the row is kept");
        Check.equal("", columns.value(TestSupport.row(columns, "name", "No Link"), "logical_column_id"), "no link at all");

        CsvTable errors = TestSupport.table(kb, "errors");
        Check.equal(4, errors.rows.size(), "four errors: " + TestSupport.column(errors, "source_id"));
        String[] link = TestSupport.row(errors, "source_id", "shapes::pres::Sales Area.Facts.Broken");
        Check.equal("unresolved_reference", errors.value(link, "kind"), "the broken link");
        Check.equal("PresentationColumn", errors.value(link, "object_type"), "its type");
        Check.equal("logical column reference does not resolve to a known logical column", errors.value(link, "reason"), "its reason");
        Check.equal("missing_name", errors.value(TestSupport.row(errors, "source_id", "mp4"), "kind"), "a column with no name");
        Check.equal("missing_name", errors.value(TestSupport.row(errors, "source_id", "msx"), "kind"), "a subject area with no name");
        String[] orphan = TestSupport.row(errors, "source_id", "mpo");
        Check.equal("unresolved_reference", errors.value(orphan, "kind"), "a table whose subject area is not found");
        Check.equal("table name or owning Subject Area could not be resolved", errors.value(orphan, "reason"), "its reason");
        Check.equal(0, TestSupport.rows(errors, "source_id", "shapes::pres::Sales Area.Facts.No Link").size(), "no link is not an error");
        Check.equal(0, TestSupport.rows(errors, "source_id", "msa").size(), "nor are the subject area's two missing links");

        Check.equal(0, TestSupport.table(kb, "lineage").rows.size(), "no source maps anything here, so no lineage");
        CsvTable unparsed = TestSupport.table(kb, "unparsed");
        Check.equal(1, TestSupport.rows(unparsed, "detail", "Alias@name").size(), "the column's alias is not read, and says so");
    }
}
