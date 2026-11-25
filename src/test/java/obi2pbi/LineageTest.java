package obi2pbi;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import obi2pbi.core.ColumnMapping;
import obi2pbi.core.Estate;
import obi2pbi.core.Expression;
import obi2pbi.core.Lineage;
import obi2pbi.core.LogicalColumn;
import obi2pbi.core.PhysicalColumn;
import obi2pbi.core.PresentationColumn;
import obi2pbi.in.kb.CsvTable;

/** Lineage from presentation column to database column (story 1.5; guide 01 §3.6, §5). */
public final class LineageTest {

    private static final String NS = " xmlns=\"" + TestSupport.XMLNS + "\"";
    private static final String BASE = "/oracle/bi/server/base/";
    private static final List<String> HEADER = List.of("row_id", "rpd_id", "presentation_column_id",
            "logical_column_id", "source_id", "physical_table_id", "physical_column_id");

    public void testFixtureMatchesItsGroundTruth() {
        Path work = TestSupport.work("lineage-testrpd");
        Path kb = work.resolve("kb");
        Check.equal(0, TestSupport.extract(TestSupport.estateFrom(work, "testrpd", TestSupport.testRpd()), kb).exit, "exit code");
        CsvTable lineage = TestSupport.table(kb, "lineage");
        Check.equal(HEADER, lineage.header, "columns as in the reference");
        String facts = "testrpd::pres::Sample Sales SA.Facts";
        String fact = "testrpd::bmm::Sample Sales.Fact Revenue";
        String physical = "testrpd::phys::SALES_SCHEMA.SAMP_REVENUE_F";
        Check.equal(List.of(
                facts + ".Margin#" + fact + ".src.SRC_DETAIL",
                facts + ".Total Revenue#" + fact + ".src.SRC_AGG",
                facts + ".Total Revenue#" + fact + ".src.SRC_DETAIL"),
                TestSupport.column(lineage, "row_id"), "three rows: one per presentation column and source that maps it");
        String[] margin = lineage.rows.get(0);
        Check.equal(List.of(facts + ".Margin#" + fact + ".src.SRC_DETAIL", "testrpd", facts + ".Margin", fact + ".Negative Margin",
                fact + ".src.SRC_DETAIL", physical, physical + ".00123_CODE"), List.of(margin), "all five identifiers of one path");
        String[] viaAggregate = lineage.rows.get(1);
        Check.equal(fact + ".Revenue", lineage.value(viaAggregate, "logical_column_id"), "logical column");
        Check.equal(fact + ".src.SRC_AGG", lineage.value(viaAggregate, "source_id"), "source");
        Check.equal(physical + ".REVENUE", lineage.value(viaAggregate, "physical_column_id"), "physical column");
        Check.equal(physical, lineage.value(viaAggregate, "physical_table_id"), "physical table");
    }

    public void testConstructedCases() {
        Path work = TestSupport.work("lineage-constructed");
        Path kb = work.resolve("kb");
        Check.equal(0, TestSupport.extract(TestSupport.estateFrom(work, "constructed", TestSupport.constructed()), kb).exit, "exit code");
        CsvTable lineage = TestSupport.table(kb, "lineage");
        String amount = "constructed::pres::Constructed SA.Sales Facts.Amount";
        String fact = "constructed::bmm::Constructed BM.Fact Sales";
        Check.equal(List.of(amount + "#" + fact + ".src.LTS_DETAIL", amount + "#" + fact + ".src.LTS_MONTHLY_AGG"),
                TestSupport.column(lineage, "row_id"), "Amount through each of its two sources; nothing for the rolling column");
        Check.equal(List.of("constructed::phys::CC_SCHEMA.FCT_SALES_DETAIL", "constructed::phys::CC_SCHEMA.FCT_SALES_MONTHLY"),
                TestSupport.column(lineage, "physical_table_id"), "each row ends at its own source's table");
    }

    public void testEveryIdentifierInLineageExistsInItsTable() {
        for (String[] fixture : new String[][] {{"testrpd", TestSupport.testRpd().toString()}, {"constructed", TestSupport.constructed().toString()}}) {
            Path work = TestSupport.work("lineage-ids-" + fixture[0]);
            Path kb = work.resolve("kb");
            TestSupport.extract(TestSupport.estateFrom(work, fixture[0], Path.of(fixture[1])), kb);
            Check.equal(List.of(), danglingIdentifiers(kb), fixture[0]);
        }
        Check.equal(List.of(), danglingIdentifiers(shapes("lineage-ids-shapes")), "an estate with an alias, a constant mapping and a broken link");
    }

    /** With the sample estates in place, the same check on the two real repositories. */
    public void testEveryIdentifierInLineageExistsInItsTableOnTheSampleEstates() {
        int checked = 0;
        for (String estate : new String[] {"sampleapp", "bi-analytics-2013"}) {
            Path folder = TestSupport.root().resolve("estates").resolve(estate);
            if (!java.nio.file.Files.isDirectory(folder.resolve("rpd"))) {
                continue;
            }
            Path kb = TestSupport.work("lineage-ids-" + estate).resolve("kb");
            TestSupport.extract(folder, kb);
            Check.that(TestSupport.table(kb, "lineage").rows.size() > 50, estate + ": lineage has rows");
            Check.equal(List.of(), danglingIdentifiers(kb), estate);
            Check.equal(List.of(), SchemaTest.valueProblems(kb), estate + ": every declared link holds");
            checked++;
        }
        if (checked == 0) {
            throw new SkipException("the sample estates are not under estates/");
        }
    }

    public void testSeveralSourcesDerivedColumnsAliasesAndBrokenChains() {
        Path kb = shapes("lineage-shapes");
        CsvTable lineage = TestSupport.table(kb, "lineage");
        String table = "lin::pres::SA.T";
        String fact = "lin::bmm::BM.Fact";
        Check.equal(List.of(
                table + ".Amount#" + fact + ".src.Via Alias",
                table + ".Amount#" + fact + ".src.Via Table",
                table + ".Const#" + fact + ".src.Via Table"),
                TestSupport.column(lineage, "row_id"),
                "one row per source for Amount; none for the derived column or the column with a broken link");

        String[] viaAlias = lineage.rows.get(0);
        Check.equal("lin::phys::S.F Alias", lineage.value(viaAlias, "physical_table_id"), "the row names the alias");
        Check.equal("lin::phys::S.F Alias.AMT", lineage.value(viaAlias, "physical_column_id"), "and the alias's column");
        CsvTable physical = TestSupport.table(kb, "physical_tables");
        Check.equal("lin::phys::S.F", physical.value(TestSupport.row(physical, "row_id", lineage.value(viaAlias, "physical_table_id")), "source_table_id"),
                "the original table is reached through source_table_id");
        Check.equal("lin::phys::S.F", lineage.value(lineage.rows.get(1), "physical_table_id"), "the other source ends at the table itself");

        String[] constant = lineage.rows.get(2);
        Check.equal(fact + ".Const", lineage.value(constant, "logical_column_id"), "a mapping to a constant still gives a row");
        Check.equal("", lineage.value(constant, "physical_table_id"), "with no physical table");
        Check.equal("", lineage.value(constant, "physical_column_id"), "and no physical column");

        CsvTable logical = TestSupport.table(kb, "logical_columns");
        Check.equal("2", logical.value(TestSupport.row(logical, "name", "Amount"), "mapped_source_count"), "Amount: two sources, two rows");
        Check.equal("0", logical.value(TestSupport.row(logical, "name", "Derived"), "mapped_source_count"), "Derived: no source, no row");
        Check.equal(fact + ".Amount", logical.value(TestSupport.row(logical, "name", "Derived"), "derived_expr_refs"), "what it is computed from is in its formula's references");
        Check.equal("3", logical.value(TestSupport.row(logical, "name", "Amount"), "source_count"), "the table declares three sources");

        CsvTable errors = TestSupport.table(kb, "errors");
        Check.equal(List.of(table + ".Broken"), TestSupport.column(errors, "source_id"), "the cause of the missing chain is in errors.csv, once");

        // For every logical column: the distinct sources in its lineage rows are its mapped_source_count.
        for (String[] column : logical.rows) {
            Set<String> sources = new HashSet<>();
            for (String[] row : TestSupport.rows(lineage, "logical_column_id", logical.value(column, "row_id"))) {
                sources.add(lineage.value(row, "source_id"));
            }
            Check.equal(logical.value(column, "mapped_source_count"), Integer.toString(sources.size()), logical.value(column, "name") + ": sources in lineage");
        }
    }

    public void testTheBuilderWorksOnTheModelAlone() {
        Estate estate = new Estate();
        estate.physicalColumns.add(new PhysicalColumn("e::phys::S.T.C", "e", "e::phys::S.T", "C", "", "", "", "", "", "", 1, ""));
        estate.logicalColumns.add(new LogicalColumn("e::bmm::M.L.A", "e", "e::bmm::M.L", "A", "", false, List.of(), 2, 0, Expression.EMPTY, Expression.EMPTY));
        estate.logicalColumns.add(new LogicalColumn("e::bmm::M.L.B", "e", "e::bmm::M.L", "B", "", false, List.of(), 2, 0, Expression.EMPTY, Expression.EMPTY));
        estate.columnMappings.add(new ColumnMapping("s1#map0", "e", "s1", "e::bmm::M.L.A", "e::phys::S.T.C", Expression.EMPTY, Expression.EMPTY));
        estate.columnMappings.add(new ColumnMapping("s1#map1", "e", "s1", "e::bmm::M.L.A", "e::phys::S.T.C", Expression.EMPTY, Expression.EMPTY));
        estate.columnMappings.add(new ColumnMapping("s2#map0", "e", "s2", "e::bmm::M.L.A", "e::phys::ELSEWHERE.X", Expression.EMPTY, Expression.EMPTY));
        estate.columnMappings.add(new ColumnMapping("s2#map1", "e", "s2", "", "e::phys::S.T.C", Expression.EMPTY, Expression.EMPTY));
        estate.presentationColumns.add(new PresentationColumn("e::pres::A.T.A", "e", "e::pres::A.T", "A", "e::bmm::M.L.A", "", ""));
        estate.presentationColumns.add(new PresentationColumn("e::pres::A.T.B", "e", "e::pres::A.T", "B", "e::bmm::M.L.B", "", ""));
        estate.presentationColumns.add(new PresentationColumn("e::pres::A.T.N", "e", "e::pres::A.T", "N", "", "", ""));

        Lineage.countMappedSources(estate);
        Lineage.build(estate);
        Check.equal(2, estate.logicalColumns.get(0).mappedSourceCount, "distinct sources, not mappings");
        Check.equal(0, estate.logicalColumns.get(1).mappedSourceCount, "a column no source maps");
        Check.equal(3, estate.lineage.size(), "one row per mapping of the column, in mapping order");
        Check.equal("e::pres::A.T.A#s1", estate.lineage.get(0).rowId, "row identifier: presentation column, '#', source");
        Check.equal("e::phys::S.T", estate.lineage.get(0).physicalTableId, "the table is looked up from the column");
        Check.equal("", estate.lineage.get(2).physicalTableId, "a reference that is not a known physical column has no table");
        Check.equal("e::phys::ELSEWHERE.X", estate.lineage.get(2).physicalColumnId, "and is kept as the mapping gives it");

        estate.checkIdentifierUniqueness();
        Check.equal(1, estate.errors.size(), "two mappings of one column in one source share a lineage identifier, and that is reported");
        Check.equal("lineage", estate.errors.get(0).objectType, "in the lineage list");
    }

    /** An estate with an alias, two sources for one column, a derived column, a constant mapping and a broken link. */
    private static Path shapes(String workName) {
        Path work = TestSupport.work(workName);
        Path estate = work.resolve("lin");
        TestSupport.schema(estate, "ms", "S");
        String inSchema = " containerRef=\"" + TestSupport.schemaRef("ms") + "\"";
        TestSupport.object(estate, "PhysicalTable", "f.xml", "<PhysicalTable mdsid=\"mf\" name=\"F\" type=\"none\"" + NS + inSchema + ">"
                + "<PhysicalColumn mdsid=\"mfc\" name=\"AMT\"/></PhysicalTable>");
        TestSupport.object(estate, "PhysicalTable", "a.xml", "<PhysicalTable mdsid=\"mfa\" name=\"F Alias\" type=\"none\"" + NS + inSchema
                + " sourceTableRef=\"" + BASE + "PhysicalTable/f.xml#mf\"><PhysicalColumn mdsid=\"mfac\" name=\"AMT\"/></PhysicalTable>");
        TestSupport.object(estate, "BusinessModel", "bm.xml", "<BusinessModel mdsid=\"mbm\" name=\"BM\"" + NS + "/>");
        TestSupport.object(estate, "LogicalTable", "lt.xml", "<LogicalTable mdsid=\"mlt\" name=\"Fact\"" + NS
                + " subjectAreaRef=\"" + BASE + "BusinessModel/bm.xml#mbm\">"
                + "<LogicalColumn mdsid=\"mla\" name=\"Amount\"/>"
                + "<LogicalColumn mdsid=\"mld\" name=\"Derived\"><Expr><ExprText>\"%1\" * 2</ExprText><ExprTextDesc>x</ExprTextDesc>"
                + "<ObjectRefList><RefObject objectRef=\"" + BASE + "LogicalTable/lt.xml#mla\"/></ObjectRefList></Expr></LogicalColumn>"
                + "<LogicalColumn mdsid=\"mlk\" name=\"Const\"/>"
                + "<RefTableSources><RefLogicalTableSource/><RefLogicalTableSource/><RefLogicalTableSource/></RefTableSources>"
                + "</LogicalTable>");
        String ofTable = " logicalTableRef=\"" + BASE + "LogicalTable/lt.xml#mlt\"";
        TestSupport.object(estate, "LogicalTableSource", "s1.xml", "<LogicalTableSource mdsid=\"ms1\" name=\"Via Alias\"" + NS + ofTable + ">"
                + mapping("LogicalTable/lt.xml#mla", "PhysicalTable/a.xml#mfac") + "</LogicalTableSource>");
        TestSupport.object(estate, "LogicalTableSource", "s2.xml", "<LogicalTableSource mdsid=\"ms2\" name=\"Via Table\" isActive=\"false\"" + NS + ofTable + ">"
                + mapping("LogicalTable/lt.xml#mla", "PhysicalTable/f.xml#mfc") + mapping("LogicalTable/lt.xml#mlk", null) + "</LogicalTableSource>");
        TestSupport.object(estate, "PresentationCatalog", "sa.xml", "<PresentationCatalog mdsid=\"msa\" name=\"SA\"" + NS + "/>");
        String logical = " logicalColumnRef=\"" + BASE + "LogicalTable/";
        TestSupport.object(estate, "PresentationTable", "pt.xml", "<PresentationTable mdsid=\"mpt\" name=\"T\"" + NS
                + " containerRef=\"" + BASE + "PresentationCatalog/sa.xml#msa\">"
                + "<PresentationColumn mdsid=\"mp1\" name=\"Amount\"" + logical + "lt.xml#mla\"/>"
                + "<PresentationColumn mdsid=\"mp2\" name=\"Derived\"" + logical + "lt.xml#mld\"/>"
                + "<PresentationColumn mdsid=\"mp3\" name=\"Const\"" + logical + "lt.xml#mlk\"/>"
                + "<PresentationColumn mdsid=\"mp4\" name=\"Broken\"" + logical + "gone.xml#nothing\"/>"
                + "</PresentationTable>");
        Path kb = work.resolve("kb");
        Check.equal(3, TestSupport.extract(estate, kb).exit, "exit 3: the broken link is an error");
        return kb;
    }

    private static String mapping(String logicalTarget, String physicalTarget) {
        return "<ColumnMapping><LogicalColumnExpr><ExprText>\"%1\"</ExprText><ObjectRefList><RefObject objectRef=\"" + BASE + logicalTarget
                + "\"/></ObjectRefList></LogicalColumnExpr><Expr><ExprText>" + (physicalTarget == null ? "1" : "\"%1\"") + "</ExprText>"
                + (physicalTarget == null ? "" : "<ObjectRefList><RefObject objectRef=\"" + BASE + physicalTarget + "\"/></ObjectRefList>")
                + "</Expr></ColumnMapping>";
    }

    /** Identifiers in {@code lineage.csv} that are not rows of the table they point to. Written out here, apart from the schema's own check. */
    static List<String> danglingIdentifiers(Path kb) {
        CsvTable lineage = TestSupport.table(kb, "lineage");
        String[][] links = {
                {"presentation_column_id", "presentation_columns"}, {"logical_column_id", "logical_columns"},
                {"source_id", "logical_table_sources"}, {"physical_table_id", "physical_tables"},
                {"physical_column_id", "physical_columns"}};
        List<String> problems = new java.util.ArrayList<>();
        for (String[] link : links) {
            Set<String> known = new HashSet<>(TestSupport.column(TestSupport.table(kb, link[1]), "row_id"));
            for (String value : TestSupport.column(lineage, link[0])) {
                boolean mayBeEmpty = link[0].startsWith("physical_");
                if (value.isEmpty() ? !mayBeEmpty : !known.contains(value)) {
                    problems.add(link[0] + ": '" + value + "' is not in " + link[1]);
                }
            }
        }
        return problems;
    }
}
