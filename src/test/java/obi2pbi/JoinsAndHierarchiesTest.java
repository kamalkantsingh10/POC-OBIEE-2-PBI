package obi2pbi;

import java.nio.file.Path;
import java.util.List;
import obi2pbi.in.kb.CsvTable;

/**
 * Physical and logical joins, logical dimensions and presentation hierarchies (story 1.6; guide 01 §2.2,
 * §2.5, §2.6, §2.9, §3.3 to §3.5, §6.4, §6.5).
 */
public final class JoinsAndHierarchiesTest {

    private static final String NS = " xmlns=\"" + TestSupport.XMLNS + "\"";
    private static final String BASE = "/oracle/bi/server/base/";

    /** Writes the hand-made estate {@code shapes} the tests below read, and returns its folder. */
    static Path shapes(Path work) {
        Path estate = work.resolve("shapes");
        TestSupport.schema(estate, "ms", "S");
        String inSchema = " containerRef=\"" + TestSupport.schemaRef("ms") + "\"";
        TestSupport.object(estate, "PhysicalTable", "a.xml", "<PhysicalTable mdsid=\"mta\" name=\"A\" type=\"none\"" + NS + inSchema + ">"
                + "<PhysicalColumn mdsid=\"ma1\" name=\"ID\"/><PhysicalColumn mdsid=\"ma2\" name=\"B ID\"/>"
                + "<PhysicalForeignKey mdsid=\"mfk1\" name=\"A_FKey\" counterPartKeyRef=\"" + BASE + "PhysicalTable/b.xml#mbk\"><RefColumns>"
                + "<RefColumn refId=\"r1\" columnRef=\"" + BASE + "PhysicalTable/a.xml#ma2\"/>"
                + "<RefColumn refId=\"r2\" columnRef=\"" + BASE + "PhysicalTable/gone.xml#nothing\"/>"
                + "<RefColumn refId=\"r3\" columnRef=\"" + BASE + "PhysicalTable/a.xml#ma1\"/>"
                + "</RefColumns></PhysicalForeignKey>"
                + "<PhysicalForeignKey mdsid=\"mfk2\"/>"
                + "</PhysicalTable>");
        TestSupport.object(estate, "PhysicalTable", "b.xml", "<PhysicalTable mdsid=\"mtb\" name=\"B\" type=\"none\"" + NS + inSchema + ">"
                + "<PhysicalColumn mdsid=\"mb1\" name=\"ID\"/>"
                + "<PhysicalKey mdsid=\"mbk\" name=\"B_Key\"><RefColumns>"
                + "<RefColumn refId=\"k1\" columnRef=\"" + BASE + "PhysicalTable/b.xml#mb1\"/>"
                + "<RefColumn refId=\"k2\" columnRef=\"" + BASE + "PhysicalTable/gone.xml#nothing\"/>"
                + "</RefColumns></PhysicalKey>"
                + "<PhysicalKey mdsid=\"mbu\" name=\"B_Unused\"><RefColumns>"
                + "<RefColumn refId=\"k3\" columnRef=\"" + BASE + "PhysicalTable/b.xml#mb1\"/></RefColumns></PhysicalKey>"
                + "</PhysicalTable>");
        TestSupport.object(estate, "ComplexJoin", "j1.xml", "<ComplexJoin mdsid=\"mj1\" name=\"Complex Join#1\" cardinality=\"ONE TO ONE\"" + NS
                + " table1Ref=\"" + BASE + "PhysicalTable/a.xml#mta\" table2Ref=\"" + BASE + "PhysicalTable/b.xml#mtb\">"
                + "<Expr mdsid=\"me1\" name=\"Expr\"><ExprText><![CDATA[ \"%1\" =  \"%2\" ]]></ExprText>"
                + "<ExprTextDesc><![CDATA[ \"S\".\"A\".\"B ID\" =  \"S\".\"B\".\"ID\" ]]></ExprTextDesc>"
                + "<ObjectRefList><RefObject objectRef=\"" + BASE + "PhysicalTable/a.xml#ma2\"/>"
                + "<RefObject objectRef=\"" + BASE + "PhysicalTable/b.xml#mb1\"/></ObjectRefList></Expr></ComplexJoin>");
        TestSupport.object(estate, "ComplexJoin", "j2.xml", "<ComplexJoin mdsid=\"mj2\" name=\"Loose\"" + NS
                + " table1Ref=\"" + BASE + "PhysicalTable/gone.xml#nothing\">"
                + "<Expr><ExprText>-1 = \"%1\"</ExprText><ObjectRefList><RefObject objectRef=\"" + BASE + "PhysicalTable/gone.xml#nothing\"/>"
                + "</ObjectRefList></Expr></ComplexJoin>");
        TestSupport.object(estate, "ComplexJoin", "j3.xml", "<ComplexJoin mdsid=\"mj3\"" + NS + "/>");

        String model = " subjectAreaRef=\"" + BASE + "BusinessModel/bm.xml#mbm\"";
        TestSupport.object(estate, "BusinessModel", "bm.xml", "<BusinessModel mdsid=\"mbm\" name=\"BM\"" + NS + "/>");
        TestSupport.object(estate, "LogicalTable", "fact.xml", "<LogicalTable mdsid=\"mlf\" name=\"Fact\"" + NS + model + ">"
                + "<LogicalColumn mdsid=\"mlf1\" name=\"Amount\"/></LogicalTable>");
        TestSupport.object(estate, "LogicalTable", "time.xml", "<LogicalTable mdsid=\"mlt\" name=\"Time\"" + NS + model + ">"
                + "<LogicalColumn mdsid=\"mlt1\" name=\"Year\"/><LogicalColumn mdsid=\"mlt2\" name=\"Year Name\"/></LogicalTable>");
        TestSupport.object(estate, "LogicalComplexJoin", "lj1.xml", "<LogicalComplexJoin mdsid=\"mlj1\" name=\"Rel_1\" type=\"LeftOuter\"" + NS
                + " logicalTable1Ref=\"" + BASE + "LogicalTable/time.xml#mlt\" multiplicity1=\"0..1\" isAggregate1=\"false\""
                + " logicalTable2Ref=\"" + BASE + "LogicalTable/fact.xml#mlf\" multiplicity2=\"0..n\" isAggregate2=\"true\"/>");
        TestSupport.object(estate, "LogicalComplexJoin", "lj2.xml", "<LogicalComplexJoin mdsid=\"mlj2\" name=\"Rel_2\" type=\"Inner\"" + NS
                + " logicalTable1Ref=\"" + BASE + "LogicalTable/gone.xml#nothing\"/>");
        TestSupport.object(estate, "LogicalComplexJoin", "lj3.xml", "<LogicalComplexJoin mdsid=\"mlj3\"" + NS + "/>");

        // A ragged, skip-level dimension: a grand total, a level with no name, and a level with two keys.
        TestSupport.object(estate, "Dimension", "time.xml", "<Dimension mdsid=\"mdt\" name=\"Time Dim\" isRagged=\"true\" isSkipped=\"true\" isValueBased=\"false\""
                + NS + model + " defaultRootLevelRef=\"" + BASE + "Dimension/time.xml#mdt1\">"
                + "<LogicalLevel mdsid=\"mdt1\" name=\"Total\" isGTA=\"true\" memberCount=\"1\"/>"
                + "<LogicalLevel mdsid=\"mdt2\"/>"
                + "<LogicalLevel mdsid=\"mdt3\" name=\"Year\" memberCount=\"10\">"
                + "<LogicalKey name=\"Display\" isForDrillDown=\"false\"><RefColumns><RefLogicalColumn logicalColumnRef=\"" + BASE + "LogicalTable/fact.xml#mlf1\"/></RefColumns></LogicalKey>"
                + "<LogicalKey name=\"Dangling\" isForDrillDown=\"true\"><RefColumns><RefLogicalColumn logicalColumnRef=\"" + BASE + "LogicalTable/gone.xml#nothing\"/></RefColumns></LogicalKey>"
                + "<LogicalKey name=\"Key\" isForDrillDown=\"true\"><RefColumns>"
                + "<RefLogicalColumn logicalColumnRef=\"" + BASE + "LogicalTable/gone.xml#nothing\"/>"
                + "<RefLogicalColumn logicalColumnRef=\"" + BASE + "LogicalTable/time.xml#mlt2\"/>"
                + "<RefLogicalColumn logicalColumnRef=\"" + BASE + "LogicalTable/time.xml#mlt1\"/>"
                + "</RefColumns></LogicalKey>"
                + "</LogicalLevel></Dimension>");
        // A parent-child dimension. It has no levels here; "TRUE" is not the source's 'true'.
        TestSupport.object(estate, "Dimension", "tree.xml", "<Dimension mdsid=\"mdp\" name=\"Rep Tree\" isValueBased=\"true\" isRagged=\"TRUE\"" + NS + model + "/>");
        TestSupport.object(estate, "Dimension", "lost.xml", "<Dimension mdsid=\"mdl\" name=\"Lost's\"" + NS
                + " subjectAreaRef=\"" + BASE + "BusinessModel/gone.xml#nothing\"><LogicalLevel mdsid=\"mdl1\" name=\"L\"/></Dimension>");
        TestSupport.object(estate, "Dimension", "unnamed.xml", "<Dimension mdsid=\"mdu\"" + NS + model + "/>");

        TestSupport.object(estate, "PresentationCatalog", "sa.xml", "<PresentationCatalog mdsid=\"msa\" name=\"Area\"" + NS + "/>");
        TestSupport.object(estate, "PresentationTable", "pt.xml", "<PresentationTable mdsid=\"mpt\" name=\"Time\"" + NS
                + " containerRef=\"" + BASE + "PresentationCatalog/sa.xml#msa\">"
                + "<PresentationColumn mdsid=\"mpc1\" name=\"Year\" logicalColumnRef=\"" + BASE + "LogicalTable/time.xml#mlt1\"/>"
                + "<PresentationColumn mdsid=\"mpc2\" name=\"Year Name\" logicalColumnRef=\"" + BASE + "LogicalTable/time.xml#mlt2\"/>"
                + "</PresentationTable>");
        String onTable = " containerRef=\"" + BASE + "PresentationTable/pt.xml#mpt\"";
        // The hierarchy file sorts before the dimension's and the table's readers have nothing to do with order.
        TestSupport.object(estate, "PresentationHierarchy", "h.xml", "<PresentationHierarchy mdsid=\"mh\" name=\"Time Hier\"" + NS + onTable
                + " logicalDimensionRef=\"" + BASE + "Dimension/time.xml#mdt\">"
                + "<PresentationLevel mdsid=\"mh1\" name=\"Total\" logicalLevelRef=\"" + BASE + "Dimension/time.xml#mdt1\"/>"
                + "<PresentationLevel mdsid=\"mh2\"><RefDisplayColumns><RefPresentationColumn presentationColumnRef=\"" + BASE + "PresentationTable/pt.xml#mpc1\"/></RefDisplayColumns></PresentationLevel>"
                + "<PresentationLevel mdsid=\"mh3\" name=\"Year\" logicalLevelRef=\"" + BASE + "Dimension/time.xml#mdt3\"><RefDisplayColumns>"
                + "<RefPresentationColumn presentationColumnRef=\"" + BASE + "PresentationTable/pt.xml#mpc2\"/>"
                + "<RefPresentationColumn presentationColumnRef=\"" + BASE + "PresentationTable/gone.xml#nothing\"/>"
                + "<RefPresentationColumn presentationColumnRef=\"" + BASE + "PresentationTable/pt.xml#mpc1\"/>"
                + "</RefDisplayColumns></PresentationLevel>"
                + "</PresentationHierarchy>");
        TestSupport.object(estate, "PresentationHierarchy", "tree.xml", "<PresentationHierarchy mdsid=\"mhp\" name=\"Rep Tree\"" + NS + onTable
                + " logicalDimensionRef=\"" + BASE + "Dimension/tree.xml#mdp\"><RefDisplayColumns>"
                + "<RefPresentationColumn presentationColumnRef=\"" + BASE + "PresentationTable/gone.xml#nothing\"/>"
                + "<RefPresentationColumn presentationColumnRef=\"" + BASE + "PresentationTable/pt.xml#mpc2\"/>"
                + "<RefPresentationColumn presentationColumnRef=\"" + BASE + "PresentationTable/pt.xml#mpc1\"/>"
                + "</RefDisplayColumns></PresentationHierarchy>");
        TestSupport.object(estate, "PresentationHierarchy", "orphan.xml", "<PresentationHierarchy mdsid=\"mho\" name=\"Orphan\"" + NS
                + " containerRef=\"" + BASE + "PresentationTable/gone.xml#nothing\"/>");
        return estate;
    }

    public void testFixtureMatchesItsGroundTruth() {
        Path work = TestSupport.work("hier-testrpd");
        Path kb = work.resolve("kb");
        TestSupport.Run run = TestSupport.extract(TestSupport.estateFrom(work, "testrpd", TestSupport.testRpd()), kb);
        Check.equal(0, run.exit, "exit code: " + run.err);

        CsvTable dimensions = TestSupport.table(kb, "logical_dimensions");
        Check.equal(List.of("row_id", "rpd_id", "name", "business_model", "is_value_based", "is_ragged", "is_skipped",
                "default_root_level_id", "level_count", "is_time"), dimensions.header,
                "columns as in the reference, without the guard columns, and is_time at the end (difference 32)");
        Check.equal(List.of("testrpd::bmm::Sample Sales.Rep Tree", "testrpd::bmm::Sample Sales.Time Dim"),
                TestSupport.column(dimensions, "row_id"), "identifiers: <estate>::bmm::<business model>.<dimension>");
        String[] time = TestSupport.row(dimensions, "name", "Time Dim");
        Check.equal("Sample Sales", dimensions.value(time, "business_model"), "business model, by its name");
        Check.equal("false", dimensions.value(time, "is_value_based"), "level-based");
        Check.equal("true", dimensions.value(time, "is_ragged"), "ragged: the flag is kept");
        Check.equal("false", dimensions.value(time, "is_skipped"), "not skip-level");
        Check.equal("testrpd::bmm::Sample Sales.Time Dim.Total", dimensions.value(time, "default_root_level_id"), "default root level");
        Check.equal("2", dimensions.value(time, "level_count"), "two levels");
        String[] tree = TestSupport.row(dimensions, "name", "Rep Tree");
        Check.equal("true", dimensions.value(tree, "is_value_based"), "parent-child");
        Check.equal("", dimensions.value(tree, "default_root_level_id"), "no default root level");
        Check.equal("1", dimensions.value(tree, "level_count"), "a parent-child dimension can still have a level");

        CsvTable levels = TestSupport.table(kb, "logical_levels");
        Check.equal(List.of("row_id", "rpd_id", "dimension_id", "position", "name", "member_count", "is_grand_total",
                "drill_key_column_id", "child_level_ids", "parent_level_ids"), levels.header,
                "columns as in the reference, and the two level lists of difference 35 at the end");
        Check.equal(3, levels.rows.size(), "three levels");
        String[] total = TestSupport.row(levels, "row_id", "testrpd::bmm::Sample Sales.Time Dim.Total");
        Check.equal("0", levels.value(total, "position"), "first in the source");
        Check.equal("true", levels.value(total, "is_grand_total"), "the grand-total level");
        Check.equal("1", levels.value(total, "member_count"), "member count as written");
        Check.equal("", levels.value(total, "drill_key_column_id"), "a grand total has no drill key");
        String[] year = TestSupport.row(levels, "row_id", "testrpd::bmm::Sample Sales.Time Dim.Year");
        Check.equal("1", levels.value(year, "position"), "second");
        Check.equal("testrpd::bmm::Sample Sales.Time Dim", levels.value(year, "dimension_id"), "owning dimension");
        Check.equal("false", levels.value(year, "is_grand_total"), "not a grand total");
        Check.equal("testrpd::bmm::Sample Sales.Fact Revenue.Revenue", levels.value(year, "drill_key_column_id"), "the column of the drill-down key");

        CsvTable hierarchies = TestSupport.table(kb, "presentation_hierarchies");
        Check.equal(List.of("row_id", "rpd_id", "table_id", "name", "dimension_id", "level_count", "display_column_id"),
                hierarchies.header, "columns");
        String[] timeHierarchy = TestSupport.row(hierarchies, "row_id", "testrpd::pres::Sample Sales SA.Facts.Time");
        Check.equal("testrpd::pres::Sample Sales SA.Facts", hierarchies.value(timeHierarchy, "table_id"), "owning table");
        Check.equal("testrpd::bmm::Sample Sales.Time Dim", hierarchies.value(timeHierarchy, "dimension_id"), "its dimension");
        Check.equal("2", hierarchies.value(timeHierarchy, "level_count"), "two levels");
        Check.equal("", hierarchies.value(timeHierarchy, "display_column_id"), "a hierarchy with levels has no column of its own");
        String[] treeHierarchy = TestSupport.row(hierarchies, "row_id", "testrpd::pres::Sample Sales SA.Facts.Rep Tree");
        Check.equal("testrpd::bmm::Sample Sales.Rep Tree", hierarchies.value(treeHierarchy, "dimension_id"), "its dimension, which is parent-child");
        Check.equal("0", hierarchies.value(treeHierarchy, "level_count"), "a parent-child hierarchy has no levels");
        Check.equal("testrpd::pres::Sample Sales SA.Facts.Margin", hierarchies.value(treeHierarchy, "display_column_id"), "its single column");

        CsvTable presentationLevels = TestSupport.table(kb, "presentation_levels");
        Check.equal(List.of("row_id", "rpd_id", "hierarchy_id", "position", "name", "logical_level_id"), presentationLevels.header, "columns");
        Check.equal(List.of("testrpd::pres::Sample Sales SA.Facts.Time.Total", "testrpd::pres::Sample Sales SA.Facts.Time.Year"),
                TestSupport.column(presentationLevels, "row_id"), "the levels of the one hierarchy that has any");
        Check.equal(List.of("0", "1"), TestSupport.column(presentationLevels, "position"), "levels in order, top first");
        Check.equal("testrpd::bmm::Sample Sales.Time Dim.Year", presentationLevels.value(presentationLevels.rows.get(1), "logical_level_id"), "logical level");
        Check.equal("testrpd::pres::Sample Sales SA.Facts.Time", presentationLevels.value(presentationLevels.rows.get(1), "hierarchy_id"), "owning hierarchy");

        CsvTable levelColumns = TestSupport.table(kb, "presentation_level_columns");
        Check.equal(List.of("row_id", "rpd_id", "level_id", "position", "presentation_column_id"), levelColumns.header, "columns");
        Check.equal(List.of("testrpd::pres::Sample Sales SA.Facts.Time.Year.0", "testrpd::pres::Sample Sales SA.Facts.Time.Year.1"),
                TestSupport.column(levelColumns, "row_id"), "identifiers: <level>.<n>");
        Check.equal(List.of("testrpd::pres::Sample Sales SA.Facts.Total Revenue", "testrpd::pres::Sample Sales SA.Facts.Margin"),
                TestSupport.column(levelColumns, "presentation_column_id"), "the level's columns in source order");
        Check.equal(List.of("0", "1"), TestSupport.column(levelColumns, "position"), "positions");

        CsvTable coverage = TestSupport.table(kb, "coverage");
        for (String[] expected : new String[][] {{"Dimension", "2"}, {"LogicalLevel", "3"}, {"PresentationHierarchy", "2"}, {"PresentationLevel", "2"}}) {
            Check.equal(expected[1], coverage.value(TestSupport.row(coverage, "object_type", expected[0]), "parsed"), expected[0] + " parsed");
        }
        Check.equal(0, TestSupport.table(kb, "errors").rows.size(), "no errors");
        Check.equal(0, TestSupport.rows(TestSupport.table(kb, "unparsed"), "kind", "unmodelled_type").size(), "every type present has a reader");
        Check.equal(List.of(), SchemaTest.valueProblems(kb), "every declared link holds");
    }

    public void testPhysicalJoinsOfBothKinds() {
        Path work = TestSupport.work("joins-physical");
        Path kb = work.resolve("kb");
        Check.equal(3, TestSupport.extract(shapes(work), kb).exit, "exit 3: the estate has errors on purpose");

        CsvTable joins = TestSupport.table(kb, "physical_joins");
        Check.equal(List.of("row_id", "rpd_id", "kind", "name", "table1_id", "table2_id", "cardinality", "expr_text",
                "expr_text_desc", "expr_refs", "columns", "referenced_columns"), joins.header,
                "columns as in the reference, without the guard columns, and one more for difference 26");
        Check.equal(List.of("shapes::phys::S.A.A_FKey", "shapes::phys::S.A.fk", "shapes::phys::joins.Complex Join#1", "shapes::phys::joins.Loose"),
                TestSupport.column(joins, "row_id"), "two foreign keys and two complex joins; the join with no name is left out");

        String[] key = TestSupport.row(joins, "row_id", "shapes::phys::S.A.A_FKey");
        Check.equal("foreign_key", joins.value(key, "kind"), "kind");
        Check.equal("A_FKey", joins.value(key, "name"), "name");
        Check.equal("shapes::phys::S.A", joins.value(key, "table1_id"), "the table the key is on");
        Check.equal("shapes::phys::S.B", joins.value(key, "table2_id"),
                "difference 26: the table that holds the key the foreign key points at");
        Check.equal("shapes::phys::S.B.ID\n", joins.value(key, "referenced_columns"),
                "difference 26: that key's columns, one per line, an empty line for the column that was not found");
        Check.equal("", joins.value(key, "cardinality"), "nor a cardinality");
        Check.equal("", joins.value(key, "expr_text"), "nor a condition");
        Check.equal("shapes::phys::S.A.B ID\n\nshapes::phys::S.A.ID", joins.value(key, "columns"),
                "difference 7: Row identifiers, one per line, an empty line for the column that was not found");
        String[] unnamed = TestSupport.row(joins, "row_id", "shapes::phys::S.A.fk");
        Check.equal("", joins.value(unnamed, "name"), "a key with no name is still a row");
        Check.equal("", joins.value(unnamed, "columns"), "with no columns");
        Check.equal("", joins.value(unnamed, "table2_id"), "a foreign key that names no key points at nothing");
        Check.equal("", joins.value(unnamed, "referenced_columns"), "and has no columns at the far end");

        String[] join = TestSupport.row(joins, "row_id", "shapes::phys::joins.Complex Join#1");
        Check.equal("complex_join", joins.value(join, "kind"), "kind");
        Check.equal("shapes::phys::S.A", joins.value(join, "table1_id"), "first table");
        Check.equal("shapes::phys::S.B", joins.value(join, "table2_id"), "second table");
        Check.equal("ONE TO ONE", joins.value(join, "cardinality"), "cardinality as written");
        Check.equal(" \"%1\" =  \"%2\" ", joins.value(join, "expr_text"), "the condition's template, spaces kept");
        Check.equal(" \"S\".\"A\".\"B ID\" =  \"S\".\"B\".\"ID\" ", joins.value(join, "expr_text_desc"), "the condition with names");
        Check.equal("shapes::phys::S.A.B ID\nshapes::phys::S.B.ID", joins.value(join, "expr_refs"), "what the condition refers to");
        Check.equal("", joins.value(join, "columns"), "a complex join lists no columns");
        Check.equal("", joins.value(join, "referenced_columns"), "at either end");

        String[] loose = TestSupport.row(joins, "row_id", "shapes::phys::joins.Loose");
        Check.equal("", joins.value(loose, "table1_id"), "a table that is not found leaves the cell empty");
        Check.equal("", joins.value(loose, "table2_id"), "and so does a table that is not named");
        Check.equal("-1 = \"%1\"", joins.value(loose, "expr_text"), "a condition starting with '-' is unchanged");
        Check.equal("", joins.value(loose, "expr_refs"), "its one reference does not resolve");

        CsvTable errors = TestSupport.table(kb, "errors");
        String[] reference = TestSupport.row(errors, "source_id", "Loose");
        Check.equal("ExpressionRef", errors.value(reference, "object_type"), "the unresolved reference is reported against the join's name");
        String[] noName = TestSupport.row(errors, "source_id", "mj3");
        Check.equal("missing_name", errors.value(noName, "kind"), "a join with no name");
        Check.equal("ComplexJoin", errors.value(noName, "object_type"), "its type");
        Check.equal("join has no name", errors.value(noName, "reason"), "its reason");
        Check.equal(0, TestSupport.rows(errors, "source_id", "mj2").size(), "tables that are not found are not errors");

        CsvTable coverage = TestSupport.table(kb, "coverage");
        String[] counted = TestSupport.row(coverage, "object_type", "ComplexJoin");
        Check.equal("2", coverage.value(counted, "parsed"), "two joins read");
        Check.equal("1", coverage.value(counted, "skipped"), "one skipped");
        Check.equal("2", coverage.value(TestSupport.row(coverage, "object_type", "PhysicalForeignKey"), "parsed"), "two foreign keys");
        CsvTable unparsed = TestSupport.table(kb, "unparsed");
        Check.equal(0, TestSupport.rows(unparsed, "detail", "PhysicalForeignKey@counterPartKeyRef").size(),
                "difference 26: the key a foreign key points at is read, so it is no longer listed as a gap");
        // Until difference 28 the key no foreign key points at, its column and both keys' names were listed
        // here as not read. Every key is a row of physical_keys now, so none of them is.
        for (String detail : new String[] {"PhysicalKey", "PhysicalKey@name", "RefColumn@columnRef"}) {
            Check.equal(0, TestSupport.rows(unparsed, "detail", detail).size(), "difference 28: no longer a gap: " + detail);
        }
        Check.equal("2", coverage.value(TestSupport.row(coverage, "object_type", "PhysicalKey"), "parsed"), "two keys read");
    }

    /** Deliberate difference 28: every key of a physical table is a row, whether or not a foreign key points at it. */
    public void testEveryPhysicalKeyIsARowWithItsColumnsInSourceOrder() {
        Path work = TestSupport.work("joins-keys");
        Path kb = work.resolve("kb");
        TestSupport.extract(shapes(work), kb);
        CsvTable keys = TestSupport.table(kb, "physical_keys");
        Check.equal(List.of("row_id", "rpd_id", "table_id", "name", "columns"), keys.header, "columns");
        Check.equal(List.of("shapes::phys::S.B.key.B_Key", "shapes::phys::S.B.key.B_Unused"), TestSupport.column(keys, "row_id"),
                "identifiers: <table>.key.<key name>, so a key and a column of one name differ");
        String[] key = TestSupport.row(keys, "name", "B_Key");
        Check.equal("shapes::phys::S.B", keys.value(key, "table_id"), "its table");
        Check.equal("shapes::phys::S.B.ID\n", keys.value(key, "columns"),
                "its columns one per line in source order; a column that is not found leaves an empty line");
        Check.equal("shapes::phys::S.B.ID", keys.value(TestSupport.row(keys, "name", "B_Unused"), "columns"),
                "the key no foreign key points at is there too");
        Check.equal(List.of(), SchemaTest.valueProblems(kb), "every link of the new table resolves");
    }

    /** Deliberate difference 26: a foreign key row says which table it joins to, and on which columns. */
    public void testAForeignKeySaysWhichTableAndColumnsItPointsAt() {
        Path work = TestSupport.work("joins-far-side");
        Path estate = work.resolve("far");
        TestSupport.schema(estate, "ms", "S");
        String inSchema = " containerRef=\"" + TestSupport.schemaRef("ms") + "\"";
        String fact = BASE + "PhysicalTable/fact.xml#";
        String dim = BASE + "PhysicalTable/dim.xml#";
        // The dimension's file is read after the fact's: reading order must not matter.
        TestSupport.object(estate, "PhysicalTable", "fact.xml", "<PhysicalTable mdsid=\"mf\" name=\"FACT\" type=\"none\"" + NS + inSchema + ">"
                + "<PhysicalColumn mdsid=\"mf1\" name=\"DAY_KEY\"/><PhysicalColumn mdsid=\"mf2\" name=\"SHOP_KEY\"/>"
                + "<PhysicalForeignKey mdsid=\"mfk1\" name=\"Two columns\" counterPartKeyRef=\"" + dim + "mdk\"><RefColumns>"
                + "<RefColumn columnRef=\"" + fact + "mf2\"/><RefColumn columnRef=\"" + fact + "mf1\"/></RefColumns></PhysicalForeignKey>"
                + "<PhysicalForeignKey mdsid=\"mfk2\" name=\"Gone\" counterPartKeyRef=\"" + BASE + "PhysicalTable/gone.xml#nothing\"><RefColumns>"
                + "<RefColumn columnRef=\"" + fact + "mf1\"/></RefColumns></PhysicalForeignKey>"
                + "<PhysicalForeignKey mdsid=\"mfk3\" name=\"Not a key\" counterPartKeyRef=\"" + dim + "md1\"/>"
                + "<PhysicalForeignKey mdsid=\"mfk4\" name=\"No link\"/>"
                + "<PhysicalForeignKey mdsid=\"mfk5\" name=\"Empty key\" counterPartKeyRef=\"" + dim + "mde\"/>"
                + "</PhysicalTable>");
        TestSupport.object(estate, "PhysicalTable", "dim.xml", "<PhysicalTable mdsid=\"md\" name=\"DIM\" type=\"none\"" + NS + inSchema + ">"
                + "<PhysicalColumn mdsid=\"md1\" name=\"DAY\"/><PhysicalColumn mdsid=\"md2\" name=\"SHOP\"/>"
                + "<PhysicalKey mdsid=\"mdk\" name=\"DIM_Key\"><RefColumns>"
                + "<RefColumn columnRef=\"" + dim + "md2\"/><RefColumn columnRef=\"" + dim + "md1\"/></RefColumns></PhysicalKey>"
                + "<PhysicalKey mdsid=\"mde\" name=\"DIM_Empty\"/>"
                + "</PhysicalTable>");
        Path kb = work.resolve("kb");
        Check.equal(3, TestSupport.extract(estate, kb).exit, "exit 3: two links do not resolve");

        CsvTable joins = TestSupport.table(kb, "physical_joins");
        String[] two = TestSupport.row(joins, "name", "Two columns");
        Check.equal("far::phys::S.FACT", joins.value(two, "table1_id"), "from table");
        Check.equal("far::phys::S.DIM", joins.value(two, "table2_id"), "to table, though its file is read later");
        Check.equal("far::phys::S.FACT.SHOP_KEY\nfar::phys::S.FACT.DAY_KEY", joins.value(two, "columns"), "from columns, in source order");
        Check.equal("far::phys::S.DIM.SHOP\nfar::phys::S.DIM.DAY", joins.value(two, "referenced_columns"),
                "to columns, in the key's own order: line n pairs with line n of columns");
        for (String name : new String[] {"Gone", "Not a key", "No link"}) {
            String[] row = TestSupport.row(joins, "name", name);
            Check.equal("", joins.value(row, "table2_id"), name + ": no far table");
            Check.equal("", joins.value(row, "referenced_columns"), name + ": no far columns");
        }
        String[] empty = TestSupport.row(joins, "name", "Empty key");
        Check.equal("far::phys::S.DIM", joins.value(empty, "table2_id"), "a key with no column list still says which table");
        Check.equal("", joins.value(empty, "referenced_columns"), "and gives no columns");

        CsvTable errors = TestSupport.table(kb, "errors");
        Check.equal(List.of("far::phys::S.FACT.Gone", "far::phys::S.FACT.Not a key"), TestSupport.column(errors, "source_id"),
                "a link that is there and leads to no key is an error against the foreign key; no link at all is not");
        Check.equal(List.of("unresolved_reference", "unresolved_reference"), TestSupport.column(errors, "kind"), "kind");
        Check.equal("PhysicalForeignKey", errors.value(errors.rows.get(0), "object_type"), "object type");

        CsvTable unparsed = TestSupport.table(kb, "unparsed");
        Check.equal(0, TestSupport.rows(unparsed, "detail", "PhysicalForeignKey@counterPartKeyRef").size(), "the link is read");
        Check.equal(0, TestSupport.rows(unparsed, "detail", "PhysicalKey").size(), "both keys were followed");
        Check.equal(0, TestSupport.rows(unparsed, "detail", "RefColumn@columnRef").size(), "and their columns read");
        Check.equal(0, TestSupport.rows(unparsed, "detail", "PhysicalKey@name").size(),
                "difference 28: a key's name is in physical_keys now, so it is no longer listed (it was, before 28)");
        CsvTable keys = TestSupport.table(kb, "physical_keys");
        Check.equal("far::phys::S.DIM.SHOP\nfar::phys::S.DIM.DAY", keys.value(TestSupport.row(keys, "name", "DIM_Key"), "columns"),
                "the key's own row lists the same columns the foreign key's referenced_columns has");
        Check.equal("", keys.value(TestSupport.row(keys, "name", "DIM_Empty"), "columns"), "a key with no column list has an empty cell");
    }

    /**
     * Deliberate differences 34 to 37: the tables and joins of a table source, the order of levels, what
     * defines a parent-child hierarchy, and four smaller attributes. The rows the jar writes are also checked
     * against what the comparison's own reader takes from the same files.
     */
    public void testJoinPathLevelOrderParentChildAndTheSmallerAttributes() throws Exception {
        Path work = TestSupport.work("joins-paths");
        Path estate = work.resolve("paths");
        TestSupport.schema(estate, "ms", "S");
        String inSchema = " containerRef=\"" + TestSupport.schemaRef("ms") + "\"";
        String emp = BASE + "PhysicalTable/emp.xml#";
        String rel = BASE + "PhysicalTable/rel.xml#";
        TestSupport.object(estate, "PhysicalTable", "emp.xml", "<PhysicalTable mdsid=\"me\" name=\"EMP\" type=\"none\"" + NS + inSchema + ">"
                + "<Description><![CDATA[ People, one row each ]]></Description>"
                + "<PhysicalColumn mdsid=\"me1\" name=\"ID\"/>"
                + "<PhysicalKey mdsid=\"mek\" name=\"EMP_Key\"><RefColumns><RefColumn columnRef=\"" + emp + "me1\"/></RefColumns></PhysicalKey>"
                + "</PhysicalTable>");
        TestSupport.object(estate, "PhysicalTable", "rel.xml", "<PhysicalTable mdsid=\"mr\" name=\"REL\" type=\"none\"" + NS + inSchema
                + " memberKeyRef=\"" + rel + "mr1\" parentKeyRef=\"" + rel + "mr2\" distanceColumnRef=\"" + rel + "mr3\""
                + " leafColumnRef=\"" + BASE + "PhysicalTable/gone.xml#nothing\">"
                + "<PhysicalColumn mdsid=\"mr1\" name=\"MEMBER\"/><PhysicalColumn mdsid=\"mr2\" name=\"ANCESTOR\"/>"
                + "<PhysicalColumn mdsid=\"mr3\" name=\"DISTANCE\"/>"
                + "<PhysicalForeignKey mdsid=\"mrf\" name=\"REL_FK\" counterPartKeyRef=\"" + emp + "mek\"><RefColumns>"
                + "<RefColumn columnRef=\"" + rel + "mr2\"/></RefColumns></PhysicalForeignKey></PhysicalTable>");
        TestSupport.object(estate, "PhysicalTable", "alias.xml", "<PhysicalTable mdsid=\"ma\" name=\"EMP_ALIAS\" type=\"alias\"" + NS + inSchema
                + " sourceTableRef=\"" + emp + "me\"><PhysicalColumn mdsid=\"ma1\" name=\"ID\" sourceColumnRef=\"" + emp + "me1\"/></PhysicalTable>");
        TestSupport.object(estate, "ComplexJoin", "j.xml", "<ComplexJoin mdsid=\"mj\" name=\"Loose\"" + NS + " table1Ref=\"" + emp + "me\" table2Ref=\""
                + BASE + "PhysicalTable/alias.xml#ma\"/>");
        String model = " subjectAreaRef=\"" + BASE + "BusinessModel/bm.xml#mbm\"";
        TestSupport.object(estate, "BusinessModel", "bm.xml", "<BusinessModel mdsid=\"mbm\" name=\"BM\"" + NS + "/>");
        TestSupport.object(estate, "LogicalTable", "rep.xml", "<LogicalTable mdsid=\"ml\" name=\"Rep\" isLookupTable=\"true\"" + NS + model + ">"
                + "<LogicalColumn mdsid=\"ml1\" name=\"Rep Id\"/>"
                + "<LogicalColumn mdsid=\"ml2\" name=\"Rep Name\" descriptorIDRef=\"" + BASE + "LogicalTable/rep.xml#ml1\"/></LogicalTable>");
        TestSupport.object(estate, "LogicalTableSource", "s.xml", "<LogicalTableSource mdsid=\"mls\" name=\"Src\"" + NS
                + " logicalTableRef=\"" + BASE + "LogicalTable/rep.xml#ml\" closureTableRef=\"" + rel + "mr\">"
                + "<Link startNodeTableRef=\"" + emp + "me\">"
                + "<Join type=\"LeftOuter\" cardinality=\" ONE TO MANY\" physicalTable1Ref=\"" + emp + "me\" physicalTable2Ref=\"" + rel + "mr\""
                + " physicalJoinRef=\"" + rel + "mrf\"/>"
                + "<Join type=\"Inner\" physicalTable1Ref=\"" + emp + "me\" physicalTable2Ref=\"" + BASE + "PhysicalTable/alias.xml#ma\""
                + " physicalJoinRef=\"" + BASE + "ComplexJoin/j.xml#mj\"/></Link>"
                + "<Link startNodeTableRef=\"" + rel + "mr\">"
                + "<Join type=\"RightOuter\" cardinality=\"MANY TO ONE\" physicalTable1Ref=\"" + rel + "mr\" physicalTable2Ref=\"" + emp + "me\""
                + " physicalJoinRef=\"" + BASE + "PhysicalTable/gone.xml#nothing\"/></Link>"
                + "<Link startNodeTableRef=\"" + BASE + "PhysicalTable/gone.xml#nothing\"/></LogicalTableSource>");
        String dim = BASE + "Dimension/d.xml#";
        TestSupport.object(estate, "Dimension", "d.xml", "<Dimension mdsid=\"md\" name=\"Geo\"" + NS + model + ">"
                + "<LogicalLevel mdsid=\"md3\" name=\"City\"/>"
                + "<LogicalLevel mdsid=\"md1\" name=\"Total\" isGTA=\"true\"><RefChildLevels><RefLogicalLevel logicalLevelRef=\"" + dim + "md2\"/>"
                + "<RefLogicalLevel logicalLevelRef=\"" + BASE + "Dimension/gone.xml#nothing\"/></RefChildLevels></LogicalLevel>"
                + "<LogicalLevel mdsid=\"md2\" name=\"Country\"><RefChildLevels><RefLogicalLevel logicalLevelRef=\"" + dim + "md3\"/></RefChildLevels>"
                + "<RefPreferredDrillDown><RefLogicalLevel logicalLevelRef=\"" + dim + "md3\"/></RefPreferredDrillDown></LogicalLevel></Dimension>");
        Path kb = work.resolve("kb");
        Check.equal(0, TestSupport.extract(estate, kb).exit, "a link that does not resolve leaves its cell empty and is not an error");
        String src = "paths::bmm::BM.Rep.src.Src";

        // Difference 34.
        CsvTable tables = TestSupport.table(kb, "lts_tables");
        Check.equal(List.of("row_id", "rpd_id", "source_id", "position", "physical_table_id"), tables.header, "lts_tables columns");
        Check.equal(List.of(src + "#table0", src + "#table1", src + "#table2"), TestSupport.column(tables, "row_id"), "one row per table the source lists");
        Check.equal(List.of("paths::phys::S.EMP", "paths::phys::S.REL", ""), TestSupport.column(tables, "physical_table_id"),
                "the tables in the source's order; one that is not found leaves the cell empty");
        CsvTable joins = TestSupport.table(kb, "lts_joins");
        Check.equal(List.of("row_id", "rpd_id", "source_id", "source_table_id", "position", "join_type", "cardinality", "table1_id", "table2_id",
                "physical_join_id"), joins.header, "lts_joins columns");
        Check.equal(List.of(src + "#table0.join0", src + "#table0.join1", src + "#table1.join0"), TestSupport.column(joins, "row_id"),
                "one row per join, under the table it is listed under");
        String[] outer = TestSupport.row(joins, "row_id", src + "#table0.join0");
        Check.equal("LeftOuter", joins.value(outer, "join_type"), "the join type as written");
        Check.equal(" ONE TO MANY", joins.value(outer, "cardinality"), "the cardinality exactly as written, leading space kept");
        Check.equal("paths::phys::S.EMP|paths::phys::S.REL", joins.value(outer, "table1_id") + "|" + joins.value(outer, "table2_id"), "its two tables");
        Check.equal("paths::phys::S.REL.REL_FK", joins.value(outer, "physical_join_id"), "the foreign key that gives the condition");
        Check.equal(1, TestSupport.rows(TestSupport.table(kb, "physical_joins"), "row_id", "paths::phys::S.REL.REL_FK").size(), "which is a row of physical_joins");
        String[] inner = TestSupport.row(joins, "row_id", src + "#table0.join1");
        Check.equal("paths::phys::joins.Loose", joins.value(inner, "physical_join_id"), "or the complex join");
        Check.equal("", joins.value(inner, "cardinality"), "an absent cardinality is empty");
        Check.equal("", joins.value(TestSupport.row(joins, "row_id", src + "#table1.join0"), "physical_join_id"), "a physical join that is not found leaves the cell empty");
        CsvTable coverage = TestSupport.table(kb, "coverage");
        Check.equal("3", coverage.value(TestSupport.row(coverage, "object_type", "Link"), "parsed"), "three source tables counted");
        Check.equal("3", coverage.value(TestSupport.row(coverage, "object_type", "Join"), "parsed"), "three source joins counted");

        // Difference 35.
        CsvTable levels = TestSupport.table(kb, "logical_levels");
        String[] total = TestSupport.row(levels, "name", "Total");
        Check.equal("paths::bmm::BM.Geo.Country\n", levels.value(total, "child_level_ids"), "the children as listed; one that is not found leaves an empty line");
        Check.equal("", levels.value(total, "parent_level_ids"), "a top level has no parent");
        String[] city = TestSupport.row(levels, "name", "City");
        Check.equal("paths::bmm::BM.Geo.Country", levels.value(city, "parent_level_ids"), "the parent is the level that lists it, though the file lists the child first");
        Check.equal("0", levels.value(city, "position"), "position alone would have put it at the top");

        // Difference 36.
        CsvTable physical = TestSupport.table(kb, "physical_tables");
        String[] relation = TestSupport.row(physical, "name", "REL");
        Check.equal("paths::phys::S.REL.MEMBER|paths::phys::S.REL.ANCESTOR|paths::phys::S.REL.DISTANCE|", physical.value(relation, "member_key_column_id")
                + "|" + physical.value(relation, "parent_key_column_id") + "|" + physical.value(relation, "distance_column_id") + "|"
                + physical.value(relation, "leaf_column_id"), "the columns of the relationship table; a link that is not found is empty");
        Check.equal("paths::phys::S.REL", TestSupport.table(kb, "logical_table_sources").value(
                TestSupport.table(kb, "logical_table_sources").rows.get(0), "closure_table_id"), "the source names its relationship table");

        // Difference 37.
        Check.equal(" People, one row each ", physical.value(TestSupport.row(physical, "name", "EMP"), "description"), "the description exactly as written");
        Check.equal("", physical.value(relation, "description"), "none is empty");
        CsvTable columns = TestSupport.table(kb, "physical_columns");
        Check.equal("paths::phys::S.EMP.ID", columns.value(TestSupport.row(columns, "row_id", "paths::phys::S.EMP_ALIAS.ID"), "source_column_id"),
                "an alias column says which column it stands for");
        Check.equal("true", TestSupport.table(kb, "logical_tables").value(TestSupport.table(kb, "logical_tables").rows.get(0), "is_lookup_table"), "the lookup flag as written");
        CsvTable logical = TestSupport.table(kb, "logical_columns");
        Check.equal("paths::bmm::BM.Rep.Rep Id", logical.value(TestSupport.row(logical, "name", "Rep Name"), "descriptor_id_column_id"), "the descriptor column");

        CsvTable unparsed = TestSupport.table(kb, "unparsed");
        for (String detail : new String[] {"Link", "Join", "Join@type", "Link@startNodeTableRef", "RefChildLevels", "Description", "Description#text",
                "PhysicalTable@memberKeyRef", "LogicalTableSource@closureTableRef", "LogicalTable@isLookupTable", "LogicalColumn@descriptorIDRef",
                "PhysicalColumn@sourceColumnRef"}) {
            Check.equal(0, TestSupport.rows(unparsed, "detail", detail).size(), "no longer a gap: " + detail);
        }
        Check.equal("1", unparsed.value(TestSupport.row(unparsed, "detail", "RefLogicalLevel"), "occurrences"),
                "the preferred drill path is still not read, and still listed");
        Check.equal(List.of(), SchemaTest.valueProblems(kb), "every declared link of the new tables and columns resolves");

        // The comparison's own reader takes the same rows and cells from the same files.
        KbCompare.Sides sides = new KbCompare.Sides(kb, work.resolve("no-reference"), estate, "paths");
        for (String table : new String[] {"lts_tables", "lts_joins", "physical_keys"}) {
            Check.that(KbCompare.explainsNewTable(sides, table) != 0, table + ": every row is what the files say, read a second time");
        }
        for (String table : KbCompare.ADDED_COLUMNS.keySet()) {
            CsvTable generated = TestSupport.table(kb, table);
            Check.that(KbCompare.explainsAddedColumns(sides, table, generated, generated.rows), table + ": every added cell is what the files say");
        }
    }

    public void testLogicalJoinsKeepTypeAndMultiplicity() {
        Path work = TestSupport.work("joins-logical");
        Path kb = work.resolve("kb");
        TestSupport.extract(shapes(work), kb);

        CsvTable joins = TestSupport.table(kb, "bmm_joins");
        Check.equal(List.of("row_id", "rpd_id", "name", "join_type", "table1_id", "multiplicity1", "is_aggregate1", "table2_id",
                "multiplicity2", "is_aggregate2"), joins.header, "columns as in the reference, without the guard columns");
        Check.equal(List.of("shapes::bmm::joins.Rel_1", "shapes::bmm::joins.Rel_2"), TestSupport.column(joins, "row_id"),
                "identifiers: <estate>::bmm::joins.<name>; the join with no name is left out");
        String[] outer = TestSupport.row(joins, "name", "Rel_1");
        Check.equal("LeftOuter", joins.value(outer, "join_type"), "an outer join, as the source writes it");
        Check.equal("shapes::bmm::BM.Time", joins.value(outer, "table1_id"), "first table");
        Check.equal("0..1", joins.value(outer, "multiplicity1"), "its multiplicity");
        Check.equal("false", joins.value(outer, "is_aggregate1"), "its aggregate flag");
        Check.equal("shapes::bmm::BM.Fact", joins.value(outer, "table2_id"), "second table");
        Check.equal("0..n", joins.value(outer, "multiplicity2"), "its multiplicity");
        Check.equal("true", joins.value(outer, "is_aggregate2"), "its aggregate flag");
        String[] inner = TestSupport.row(joins, "name", "Rel_2");
        Check.equal("Inner", joins.value(inner, "join_type"), "an inner join");
        Check.equal("", joins.value(inner, "table1_id"), "a table that is not found leaves the cell empty");
        Check.equal("", joins.value(inner, "multiplicity2"), "an attribute that is absent is empty");

        CsvTable errors = TestSupport.table(kb, "errors");
        String[] noName = TestSupport.row(errors, "source_id", "mlj3");
        Check.equal("missing_name", errors.value(noName, "kind"), "a logical join with no name");
        Check.equal("no name", errors.value(noName, "reason"), "its reason");
        Check.equal(0, TestSupport.rows(errors, "source_id", "mlj2").size(), "a table that is not found is not an error");
        Check.equal("2", TestSupport.table(kb, "coverage").value(TestSupport.row(TestSupport.table(kb, "coverage"), "object_type", "LogicalComplexJoin"), "parsed"), "two read");
    }

    public void testDimensionsKeepTheirFlagsAndLevelsTheirPlaces() {
        Path work = TestSupport.work("hier-logical");
        Path kb = work.resolve("kb");
        TestSupport.extract(shapes(work), kb);

        CsvTable dimensions = TestSupport.table(kb, "logical_dimensions");
        Check.equal(List.of("shapes::bmm::BM.Rep Tree", "shapes::bmm::BM.Time Dim"), TestSupport.column(dimensions, "row_id"),
                "the two dimensions that can be named");
        String[] time = TestSupport.row(dimensions, "name", "Time Dim");
        Check.equal("true", dimensions.value(time, "is_ragged"), "ragged");
        Check.equal("true", dimensions.value(time, "is_skipped"), "skip-level");
        Check.equal("false", dimensions.value(time, "is_value_based"), "not parent-child");
        Check.equal("3", dimensions.value(time, "level_count"), "the count includes the level that has no name");
        Check.equal("shapes::bmm::BM.Time Dim.Total", dimensions.value(time, "default_root_level_id"), "default root level");
        String[] tree = TestSupport.row(dimensions, "name", "Rep Tree");
        Check.equal("true", dimensions.value(tree, "is_value_based"), "parent-child");
        Check.equal("false", dimensions.value(tree, "is_ragged"), "only the exact text 'true' counts as true");
        Check.equal("0", dimensions.value(tree, "level_count"), "no levels");

        CsvTable levels = TestSupport.table(kb, "logical_levels");
        Check.equal(List.of("shapes::bmm::BM.Time Dim.Total", "shapes::bmm::BM.Time Dim.Year"), TestSupport.column(levels, "row_id"), "the named levels");
        Check.equal(List.of("0", "2"), TestSupport.column(levels, "position"), "the unnamed level keeps its place, so there is a gap");
        String[] year = TestSupport.row(levels, "name", "Year");
        Check.equal("shapes::bmm::BM.Time.Year Name", levels.value(year, "drill_key_column_id"),
                "the first column that resolves, in the first drill-down key that has one");
        Check.equal("false", levels.value(year, "is_grand_total"), "an absent flag is false");
        Check.equal("true", levels.value(TestSupport.row(levels, "name", "Total"), "is_grand_total"), "grand total");

        CsvTable errors = TestSupport.table(kb, "errors");
        String[] level = TestSupport.row(errors, "source_id", "mdt2");
        Check.equal("missing_name", errors.value(level, "kind"), "a level with no name");
        Check.equal("LogicalLevel", errors.value(level, "object_type"), "its type");
        String[] lost = TestSupport.row(errors, "source_id", "mdl");
        Check.equal("unresolved_reference", errors.value(lost, "kind"), "a dimension whose business model is not found");
        Check.equal("business model for dimension \"Lost's\" could not be resolved", errors.value(lost, "reason"), "its reason, the name quoted as the reference does");
        Check.equal("missing_name", errors.value(TestSupport.row(errors, "source_id", "mdu"), "kind"), "a dimension with no name");

        CsvTable unparsed = TestSupport.table(kb, "unparsed");
        // Until difference 30 two column links were listed here as not read (the key not used for drill-down,
        // and the column after the one taken). Every key of a level is a row now, with all its columns.
        Check.equal(0, TestSupport.rows(unparsed, "detail", "RefLogicalColumn@logicalColumnRef").size(),
                "difference 30: every column of every level key is read");

        CsvTable keys = TestSupport.table(kb, "logical_level_keys");
        Check.equal(List.of("row_id", "rpd_id", "level_id", "name", "is_primary", "is_chronological", "is_for_drill_down", "columns"),
                keys.header, "columns");
        Check.equal(List.of("shapes::bmm::BM.Time Dim.Year.key.Dangling", "shapes::bmm::BM.Time Dim.Year.key.Display",
                "shapes::bmm::BM.Time Dim.Year.key.Key"), TestSupport.column(keys, "row_id"), "identifiers: <level>.key.<key name>");
        String[] display = TestSupport.row(keys, "name", "Display");
        Check.equal("shapes::bmm::BM.Time Dim.Year", keys.value(display, "level_id"), "its level");
        Check.equal("false", keys.value(display, "is_for_drill_down"), "a flag as the source writes it");
        Check.equal("", keys.value(display, "is_primary"), "a flag the source does not carry is empty, not false");
        Check.equal("", keys.value(display, "is_chronological"), "and so is this one");
        Check.equal("shapes::bmm::BM.Fact.Amount", keys.value(display, "columns"), "its column");
        Check.equal("\nshapes::bmm::BM.Time.Year Name\nshapes::bmm::BM.Time.Year", keys.value(TestSupport.row(keys, "name", "Key"), "columns"),
                "all the columns in source order; one that is not found leaves an empty line");
        Check.equal("", dimensions.value(time, "is_time"), "difference 32: the time flag as written; absent is empty");
    }

    public void testHierarchyLevelsInOrderAndTheParentChildColumn() {
        Path work = TestSupport.work("hier-presentation");
        Path kb = work.resolve("kb");
        TestSupport.extract(shapes(work), kb);

        CsvTable hierarchies = TestSupport.table(kb, "presentation_hierarchies");
        Check.equal(List.of("shapes::pres::Area.Time.Rep Tree", "shapes::pres::Area.Time.Time Hier"), TestSupport.column(hierarchies, "row_id"),
                "identifiers: <presentation table>.<hierarchy>");
        String[] time = TestSupport.row(hierarchies, "name", "Time Hier");
        Check.equal("shapes::bmm::BM.Time Dim", hierarchies.value(time, "dimension_id"), "its dimension");
        Check.equal("3", hierarchies.value(time, "level_count"), "the count includes the level that has no name");
        Check.equal("", hierarchies.value(time, "display_column_id"), "the levels' columns are not taken for the hierarchy's own");
        String[] tree = TestSupport.row(hierarchies, "name", "Rep Tree");
        Check.equal("shapes::bmm::BM.Rep Tree", hierarchies.value(tree, "dimension_id"), "the parent-child dimension");
        Check.equal("0", hierarchies.value(tree, "level_count"), "no levels");
        Check.equal("shapes::pres::Area.Time.Year Name", hierarchies.value(tree, "display_column_id"), "the first column that resolves");

        CsvTable levels = TestSupport.table(kb, "presentation_levels");
        Check.equal(List.of("shapes::pres::Area.Time.Time Hier.Total", "shapes::pres::Area.Time.Time Hier.Year"), TestSupport.column(levels, "row_id"), "the named levels");
        Check.equal(List.of("0", "2"), TestSupport.column(levels, "position"), "drill order; the unnamed level keeps its place");
        Check.equal("shapes::bmm::BM.Time Dim.Year", levels.value(levels.rows.get(1), "logical_level_id"), "logical level");

        CsvTable columns = TestSupport.table(kb, "presentation_level_columns");
        Check.equal(List.of("shapes::pres::Area.Time.Time Hier.Year.0", "shapes::pres::Area.Time.Time Hier.Year.2"), TestSupport.column(columns, "row_id"),
                "the column that does not resolve has no row and keeps its number; the unnamed level's column is left out");
        Check.equal(List.of("shapes::pres::Area.Time.Year Name", "shapes::pres::Area.Time.Year"), TestSupport.column(columns, "presentation_column_id"), "in source order");
        Check.equal("shapes::pres::Area.Time.Time Hier.Year", columns.value(columns.rows.get(0), "level_id"), "owning level");

        CsvTable errors = TestSupport.table(kb, "errors");
        String[] column = TestSupport.row(errors, "source_id", "shapes::pres::Area.Time.Time Hier.Year");
        Check.equal("RefPresentationColumn", errors.value(column, "object_type"), "the level column that does not resolve, against its level");
        Check.equal("display column reference does not resolve to a known column", errors.value(column, "reason"), "its reason");
        Check.equal("missing_name", errors.value(TestSupport.row(errors, "source_id", "mh2"), "kind"), "a level with no name");
        String[] orphan = TestSupport.row(errors, "source_id", "mho");
        Check.equal("unresolved_reference", errors.value(orphan, "kind"), "a hierarchy whose table is not found");
        Check.equal("hierarchy name or owning presentation table could not be resolved", errors.value(orphan, "reason"), "its reason");
        Check.equal(0, TestSupport.rows(errors, "source_id", "mhp").size(), "the parent-child hierarchy's dangling column is not an error");

        CsvTable coverage = TestSupport.table(kb, "coverage");
        Check.equal("2", coverage.value(TestSupport.row(coverage, "object_type", "PresentationHierarchy"), "parsed"), "two hierarchies read");
        Check.equal("2", coverage.value(TestSupport.row(coverage, "object_type", "PresentationLevel"), "parsed"), "two levels read");
        Check.equal("1", coverage.value(TestSupport.row(coverage, "object_type", "RefPresentationColumn"), "skipped"), "the error is counted under its marker type");
        Check.equal(List.of(), SchemaTest.valueProblems(kb), "every declared link holds");
    }

    /** A name that cannot be part of an identifier costs one object, not the run (deliberate difference 3). */
    public void testANameThatCannotBeUsedIsAnErrorRowAndTheRunGoesOn() {
        Path work = TestSupport.work("hier-invalid");
        Path estate = work.resolve("bad");
        String model = " subjectAreaRef=\"" + BASE + "BusinessModel/bm.xml#mbm\"";
        TestSupport.object(estate, "BusinessModel", "bm.xml", "<BusinessModel mdsid=\"mbm\" name=\"BM\"" + NS + "/>");
        TestSupport.object(estate, "Dimension", "d.xml", "<Dimension mdsid=\"md\" name=\"D\"" + NS + model + ">"
                + "<LogicalLevel mdsid=\"md1\" name=\"a::b\"/><LogicalLevel mdsid=\"md2\" name=\"Good\"/></Dimension>");
        TestSupport.object(estate, "LogicalComplexJoin", "j.xml", "<LogicalComplexJoin mdsid=\"mj\" name=\"x::y\"" + NS + "/>");
        TestSupport.object(estate, "ComplexJoin", "c.xml", "<ComplexJoin mdsid=\"mc\" name=\"   \"" + NS + "/>");
        Path kb = work.resolve("kb");
        Check.equal(3, TestSupport.extract(estate, kb).exit, "exit 3, not a stop");
        Check.equal(List.of("bad::bmm::BM.D.Good"), TestSupport.column(TestSupport.table(kb, "logical_levels"), "row_id"), "the other level is read");
        Check.equal(0, TestSupport.table(kb, "bmm_joins").rows.size(), "the join is left out");
        CsvTable errors = TestSupport.table(kb, "errors");
        Check.equal(List.of("invalid_name", "invalid_name", "invalid_name"), TestSupport.column(errors, "kind"), "three errors: " + TestSupport.column(errors, "source_id"));
        Check.equal(List.of("mc", "md1", "mj"), TestSupport.column(errors, "source_id"), "each against the object's own id");
    }
}
