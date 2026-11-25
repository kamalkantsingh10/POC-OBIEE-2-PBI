package obi2pbi;

import java.nio.file.Path;
import java.util.List;
import obi2pbi.in.kb.CsvTable;

/** The physical layer: tables, columns, definitions, aliases (story 1.1; guide 01 §2.1, §3.3, §5.4, §5.5). */
public final class PhysicalLayerTest {

    private static final String NS = " xmlns=\"" + TestSupport.XMLNS + "\"";

    public void testFixtureMatchesItsGroundTruth() {
        Path work = TestSupport.work("phys-testrpd");
        Path kb = work.resolve("kb");
        TestSupport.Run run = TestSupport.extract(TestSupport.estateFrom(work, "testrpd", TestSupport.testRpd()), kb);
        Check.equal(0, run.exit, "exit code: " + run.err);

        CsvTable tables = TestSupport.table(kb, "physical_tables");
        Check.equal(1, tables.rows.size(), "one physical table");
        String[] table = TestSupport.row(tables, "row_id", "testrpd::phys::SALES_SCHEMA.SAMP_REVENUE_F");
        Check.equal("testrpd", tables.value(table, "rpd_id"), "estate name in rpd_id");
        Check.equal("SALES_SCHEMA", tables.value(table, "container"), "container from the schema's name");
        Check.equal("SAMP_REVENUE_F", tables.value(table, "name"), "name");
        Check.equal("Table", tables.value(table, "table_type"), "type as the source gives it");
        Check.equal("", tables.value(table, "source_table_id"), "not an alias");

        CsvTable columns = TestSupport.table(kb, "physical_columns");
        Check.equal(List.of("testrpd::phys::SALES_SCHEMA.SAMP_REVENUE_F.00123_CODE",
                "testrpd::phys::SALES_SCHEMA.SAMP_REVENUE_F.REVENUE"),
                TestSupport.column(columns, "row_id"), "column identifiers, sorted");
        String[] revenue = TestSupport.row(columns, "name", "REVENUE");
        Check.equal("DOUBLE", columns.value(revenue, "data_type"), "REVENUE data type");
        Check.equal("true", columns.value(revenue, "nullable"), "REVENUE nullable");
        Check.equal("testrpd::phys::SALES_SCHEMA.SAMP_REVENUE_F", columns.value(revenue, "table_id"), "owning table");
        String[] code = TestSupport.row(columns, "name", "00123_CODE");
        Check.equal("", columns.value(code, "nullable"), "an absent attribute is an empty cell");
        Check.equal("VARCHAR", columns.value(code, "data_type"), "00123_CODE data type");

        CsvTable coverage = TestSupport.table(kb, "coverage");
        Check.equal("1", coverage.value(TestSupport.row(coverage, "object_type", "PhysicalTable"), "parsed"), "tables parsed");
        Check.equal("2", coverage.value(TestSupport.row(coverage, "object_type", "PhysicalColumn"), "parsed"), "columns parsed");
        Check.equal("1", coverage.value(TestSupport.row(coverage, "object_type", "Schema"), "parsed"), "schema counted, index only");
        Check.equal("1", coverage.value(TestSupport.row(coverage, "object_type", "BusinessModel"), "parsed"), "business model counted");
        Check.equal(0, TestSupport.table(kb, "physical_table_definitions").rows.size(), "no definitions in this fixture");
    }

    public void testConstructedCases() {
        Path work = TestSupport.work("phys-constructed");
        Path kb = work.resolve("kb");
        TestSupport.Run run = TestSupport.extract(TestSupport.estateFrom(work, "constructed", TestSupport.constructed()), kb);
        Check.equal(0, run.exit, "exit code: " + run.err);
        Check.equal(List.of("constructed::phys::CC_SCHEMA.FCT_SALES_DETAIL", "constructed::phys::CC_SCHEMA.FCT_SALES_MONTHLY"),
                TestSupport.column(TestSupport.table(kb, "physical_tables"), "row_id"), "tables");
        Check.equal(List.of(
                "constructed::phys::CC_SCHEMA.FCT_SALES_DETAIL.AMOUNT", "constructed::phys::CC_SCHEMA.FCT_SALES_DETAIL.DAY_DT",
                "constructed::phys::CC_SCHEMA.FCT_SALES_MONTHLY.AMOUNT", "constructed::phys::CC_SCHEMA.FCT_SALES_MONTHLY.MONTH_DT"),
                TestSupport.column(TestSupport.table(kb, "physical_columns"), "row_id"), "columns");
    }

    public void testContainerChainAliasesDefinitionsAndForeignKeys() {
        Path work = TestSupport.work("phys-shapes");
        Path estate = work.resolve("shapes");
        TestSupport.object(estate, "Database", "db.xml", "<Database mdsid=\"mdb\" name=\"WH\"" + NS + "/>");
        TestSupport.object(estate, "Schema", "sc.xml", "<Schema mdsid=\"msc\" name=\"SALES\"" + NS
                + " containerRef=\"/oracle/bi/server/base/Database/db.xml#mdb\"/>");
        String container = " containerRef=\"/oracle/bi/server/base/Schema/sc.xml#msc\"";
        // The alias file sorts before the table it stands for: reading order must not matter.
        TestSupport.object(estate, "PhysicalTable", "a-alias.xml", "<PhysicalTable mdsid=\"mal\" name=\"Orders Alias\" type=\"none\""
                + NS + container + " sourceTableRef=\"/oracle/bi/server/base/PhysicalTable/b-orders.xml#mor\">"
                + "<PhysicalColumn mdsid=\"malc\" name=\"ID\" dataType=\"INT\"/></PhysicalTable>");
        TestSupport.object(estate, "PhysicalTable", "b-orders.xml", "<PhysicalTable mdsid=\"mor\" name=\"Orders\" type=\"none\" maxConn=\"7\""
                + NS + container + ">"
                + "<DBMapItem name=\"DefaultMulDB\"><Value>FCT_ORDERS_DD</Value></DBMapItem>"
                + "<PhysicalColumn mdsid=\"morc\" name=\"ID\" dataType=\"INT\" precision=\"10\" extName=\"x\" specialType=\"none\"/>"
                + "<PhysicalForeignKey mdsid=\"mfk\" name=\"Orders_FK\"><RefColumns>"
                + "<RefColumn columnRef=\"/oracle/bi/server/base/PhysicalTable/b-orders.xml#morc\"/>"
                + "<RefColumn columnRef=\"/oracle/bi/server/base/PhysicalTable/gone.xml#nothing\"/>"
                + "</RefColumns></PhysicalForeignKey></PhysicalTable>");
        String sql = " SELECT a,\n  \"b\" FROM t -- as written\n";
        TestSupport.object(estate, "PhysicalTable", "c-view.xml", "<PhysicalTable mdsid=\"mvw\" name=\"A View\" type=\"select\""
                + NS + container + ">"
                + "<DBMapItem name=\"DefaultMulDB\"><Value><![CDATA[" + sql + "]]></Value></DBMapItem>"
                + "<DBMapItem name=\"Oracle\"/><DBMapItem><Value>x</Value></DBMapItem></PhysicalTable>");
        TestSupport.object(estate, "PhysicalTable", "d-dangling.xml", "<PhysicalTable mdsid=\"mdg\" name=\"Dangling\" type=\"none\""
                + NS + container + " sourceTableRef=\"/oracle/bi/server/base/PhysicalTable/gone.xml#nothing\"/>");

        Path kb = work.resolve("kb");
        TestSupport.Run run = TestSupport.extract(estate, kb);
        Check.equal(3, run.exit, "exit 3: the dangling alias is an error. " + run.err);

        CsvTable tables = TestSupport.table(kb, "physical_tables");
        String[] orders = TestSupport.row(tables, "row_id", "shapes::phys::WH.SALES.Orders");
        Check.equal("WH.SALES", tables.value(orders, "container"), "database then schema");
        Check.equal("7", tables.value(orders, "max_conn"), "max_conn");
        String[] alias = TestSupport.row(tables, "name", "Orders Alias");
        Check.equal("shapes::phys::WH.SALES.Orders", tables.value(alias, "source_table_id"),
                "the alias names the table it stands for, though that table is read later");
        Check.equal("", tables.value(TestSupport.row(tables, "name", "Dangling"), "source_table_id"),
                "an alias link that does not resolve leaves the cell empty");

        CsvTable errors = TestSupport.table(kb, "errors");
        Check.equal(1, errors.rows.size(), "one error");
        Check.equal("unresolved_reference", errors.value(errors.rows.get(0), "kind"), "kind");
        Check.equal("shapes::phys::WH.SALES.Dangling", errors.value(errors.rows.get(0), "source_id"), "about the alias");

        CsvTable definitions = TestSupport.table(kb, "physical_table_definitions");
        String[] view = TestSupport.row(definitions, "row_id", "shapes::phys::WH.SALES.A View.def.DefaultMulDB");
        Check.equal(sql, definitions.value(view, "text"), "the SQL exactly as written");
        Check.equal("shapes::phys::WH.SALES.A View", definitions.value(view, "table_id"), "owning table");
        Check.equal("", definitions.value(TestSupport.row(definitions, "database", "Oracle"), "text"),
                "an entry with no value is still a row");
        Check.equal("x", definitions.value(TestSupport.row(definitions, "row_id", "shapes::phys::WH.SALES.A View.def.unnamed-3"), "text"),
                "an entry with no database name is numbered by position");
        Check.equal("FCT_ORDERS_DD", definitions.value(TestSupport.row(definitions, "table_id", "shapes::phys::WH.SALES.Orders"), "text"),
                "for an ordinary table the text is the database object's name");

        CsvTable columns = TestSupport.table(kb, "physical_columns");
        String[] id = TestSupport.row(columns, "row_id", "shapes::phys::WH.SALES.Orders.ID");
        Check.equal("10", columns.value(id, "precision"), "precision");
        Check.equal("x", columns.value(id, "external_name"), "external name");
        Check.equal("none", columns.value(id, "special_type"), "special type");

        CsvTable coverage = TestSupport.table(kb, "coverage");
        Check.equal("1", coverage.value(TestSupport.row(coverage, "object_type", "PhysicalForeignKey"), "parsed"),
                "the foreign key is read and counted");
        CsvTable unparsed = TestSupport.table(kb, "unparsed");
        Check.equal(0, TestSupport.rows(unparsed, "detail", "PhysicalForeignKey").size(), "so it is not an unread element");
        Check.equal(1, TestSupport.rows(unparsed, "detail", "PhysicalForeignKey@mdsid").size(), "what is not read from it is reported");
    }
}
