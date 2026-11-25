package obi2pbi;

import java.nio.file.Path;
import java.util.List;
import obi2pbi.core.Expression;
import obi2pbi.core.TimeSeries;
import obi2pbi.in.kb.CsvTable;

/**
 * The logical layer: tables, columns with their formulas, aggregation rules, table sources and column
 * mappings (story 1.3; guide 01 §2.3, §2.4, §3.4, §4, §6.1 to §6.3; deliberate differences 5 and 6).
 */
public final class LogicalLayerTest {

    private static final String NS = " xmlns=\"" + TestSupport.XMLNS + "\"";
    private static final String BASE = "/oracle/bi/server/base/";

    private static String refs(String... targets) {
        StringBuilder list = new StringBuilder("<ObjectRefList>");
        for (String target : targets) {
            list.append("<RefObject refId=\"r\" objectRef=\"").append(BASE).append(target).append("\"/>");
        }
        return list.append("</ObjectRefList>").toString();
    }

    private static String expression(String tag, String text, String textDesc, String... targets) {
        return "<" + tag + "><ExprText><![CDATA[" + text + "]]></ExprText><ExprTextDesc><![CDATA[" + textDesc
                + "]]></ExprTextDesc>" + (targets.length == 0 ? "" : refs(targets)) + "</" + tag + ">";
    }

    public void testFixtureMatchesItsGroundTruth() {
        Path work = TestSupport.work("bmm-testrpd");
        Path kb = work.resolve("kb");
        TestSupport.Run run = TestSupport.extract(TestSupport.estateFrom(work, "testrpd", TestSupport.testRpd()), kb);
        Check.equal(0, run.exit, "exit code: " + run.err);
        String fact = "testrpd::bmm::Sample Sales.Fact Revenue";
        String revenueColumn = "testrpd::phys::SALES_SCHEMA.SAMP_REVENUE_F.REVENUE";

        CsvTable tables = TestSupport.table(kb, "logical_tables");
        Check.equal(List.of("row_id", "rpd_id", "business_model", "name", "source_count", "is_lookup_table"), tables.header,
                "columns as in the reference, and is_lookup_table at the end (difference 37)");
        Check.equal(1, tables.rows.size(), "one logical table");
        String[] table = TestSupport.row(tables, "row_id", fact);
        Check.equal("Sample Sales", tables.value(table, "business_model"), "business model, by its name");
        Check.equal("Fact Revenue", tables.value(table, "name"), "name");
        Check.equal("2", tables.value(table, "source_count"), "two sources declared");

        CsvTable columns = TestSupport.table(kb, "logical_columns");
        Check.equal(List.of("row_id", "rpd_id", "table_id", "name", "is_writeable", "is_measure", "source_count",
                "mapped_source_count", "time_series", "derived_expr_text", "derived_expr_text_desc",
                "derived_expr_refs", "mapping_expr_text", "mapping_expr_text_desc", "mapping_expr_refs", "descriptor_id_column_id"),
                columns.header, "columns as in the reference, without the guard columns, and descriptor_id_column_id at the end (difference 37)");
        Check.equal(List.of(fact + ".Negative Margin", fact + ".Revenue"), TestSupport.column(columns, "row_id"), "columns");
        String[] revenue = TestSupport.row(columns, "name", "Revenue");
        Check.equal("true", columns.value(revenue, "is_measure"), "Revenue is a measure");
        Check.equal("false", columns.value(revenue, "is_writeable"), "is_writeable as written");
        Check.equal("2", columns.value(revenue, "source_count"), "Revenue: sources on the table");
        Check.equal("2", columns.value(revenue, "mapped_source_count"), "Revenue: sources that map it");
        Check.equal("", columns.value(revenue, "derived_expr_text"), "Revenue has no formula of its own");
        String[] margin = TestSupport.row(columns, "name", "Negative Margin");
        Check.equal("false", columns.value(margin, "is_measure"), "no measure definition, so not a measure");
        Check.equal("", columns.value(margin, "is_writeable"), "an absent attribute is an empty cell");
        Check.equal("2", columns.value(margin, "source_count"), "Negative Margin: sources on the table");
        Check.equal("1", columns.value(margin, "mapped_source_count"), "Negative Margin: only one source maps it");
        Check.equal("-1 * \"%1\", \"x\"\nnext", columns.value(margin, "derived_expr_text"),
                "a formula starting with '-', with a comma, quotes and a line break, exactly as written");
        Check.equal("-1 * \"Sample Sales\".\"Fact Revenue\".\"Revenue\", \"x\"\nnext",
                columns.value(margin, "derived_expr_text_desc"), "the rendered text, exactly as written");
        Check.equal(revenueColumn, columns.value(margin, "derived_expr_refs"), "the reference list");
        Check.equal("", columns.value(margin, "time_series"), "no time-series function");

        CsvTable rules = TestSupport.table(kb, "aggregation_rules");
        Check.equal(List.of("row_id", "rpd_id", "logical_column_id", "name", "is_default", "expr_text",
                "expr_text_desc", "expr_refs"), rules.header, "columns");
        Check.equal(2, rules.rows.size(), "two rules, never merged into one");
        String[] byDefault = TestSupport.row(rules, "row_id", fact + ".Revenue#aggr0");
        Check.equal("AggRule_default", rules.value(byDefault, "name"), "default rule name");
        Check.equal("true", rules.value(byDefault, "is_default"), "default flagged");
        Check.equal(" SUM(\"%1\")", rules.value(byDefault, "expr_text"), "the leading space is kept");
        Check.equal(" SUM(\"Sample Sales\".\"Fact Revenue\".\"Revenue\")", rules.value(byDefault, "expr_text_desc"), "rendered text");
        Check.equal(revenueColumn, rules.value(byDefault, "expr_refs"), "reference");
        Check.equal(fact + ".Revenue", rules.value(byDefault, "logical_column_id"), "the measure");
        String[] override = TestSupport.row(rules, "row_id", fact + ".Revenue#aggr1");
        Check.equal("AggRule_override", rules.value(override, "name"), "override rule name");
        Check.equal("false", rules.value(override, "is_default"), "not the default");
        Check.equal("COUNT(\"%1\")", rules.value(override, "expr_text"), "override text");
        Check.equal("", rules.value(override, "expr_refs"), "no reference list, and none taken from the other rule");

        CsvTable sources = TestSupport.table(kb, "logical_table_sources");
        Check.equal(List.of("row_id", "rpd_id", "logical_table_id", "name", "is_active", "priority", "fragment_text",
                "fragment_text_desc", "fragment_refs", "where_text", "where_text_desc", "where_refs",
                "group_by_text", "group_by_text_desc", "group_by_refs", "closure_table_id"), sources.header,
                "columns as in the reference, and closure_table_id at the end (difference 36)");
        Check.equal(List.of(fact + ".src.SRC_AGG", fact + ".src.SRC_DETAIL"), TestSupport.column(sources, "row_id"), "sources");
        String[] detail = TestSupport.row(sources, "name", "SRC_DETAIL");
        Check.equal(fact, sources.value(detail, "logical_table_id"), "owning table");
        Check.equal("true", sources.value(detail, "is_active"), "is_active as written");
        Check.equal("-1 = VALUEOF(NQ_SESSION.X)", sources.value(detail, "where_text"), "content filter starting with '-'");
        Check.equal("-1 = VALUEOF(NQ_SESSION.X)", sources.value(detail, "where_text_desc"), "its rendered text");
        Check.equal("", sources.value(detail, "fragment_text"), "a fragment that is present and empty");
        for (String[] source : sources.rows) {
            Check.equal("", sources.value(source, "priority"), "no priority recorded, and none made up");
        }

        CsvTable map = TestSupport.table(kb, "lts_column_map");
        Check.equal(List.of("row_id", "rpd_id", "source_id", "logical_column_id", "physical_column_id",
                "logical_expr_text", "logical_expr_text_desc", "logical_expr_refs", "physical_expr_text",
                "physical_expr_text_desc", "physical_expr_refs"), map.header, "columns");
        Check.equal(List.of(fact + ".src.SRC_AGG#map0", fact + ".src.SRC_DETAIL#map0", fact + ".src.SRC_DETAIL#map1"),
                TestSupport.column(map, "row_id"), "mappings, numbered from 0 within their source");
        String[] second = TestSupport.row(map, "row_id", fact + ".src.SRC_DETAIL#map1");
        Check.equal(fact + ".src.SRC_DETAIL", map.value(second, "source_id"), "source");
        Check.equal(fact + ".Negative Margin", map.value(second, "logical_column_id"), "logical column");
        Check.equal("testrpd::phys::SALES_SCHEMA.SAMP_REVENUE_F.00123_CODE", map.value(second, "physical_column_id"), "physical column");
        Check.equal("\"%1\"", map.value(second, "physical_expr_text"), "physical template");
        Check.equal("\"SALES_SCHEMA\".\"SAMP_REVENUE_F\".\"00123_CODE\"", map.value(second, "physical_expr_text_desc"), "physical rendered text");

        CsvTable coverage = TestSupport.table(kb, "coverage");
        for (String[] expected : new String[][] {{"LogicalTable", "1"}, {"LogicalColumn", "2"}, {"AggrRule", "2"},
                {"LogicalTableSource", "2"}, {"ColumnMapping", "3"}}) {
            Check.equal(expected[1], coverage.value(TestSupport.row(coverage, "object_type", expected[0]), "parsed"),
                    expected[0] + " parsed");
        }
        Check.equal(0, TestSupport.table(kb, "errors").rows.size(), "no errors");
    }

    public void testConstructedCases() {
        Path work = TestSupport.work("bmm-constructed");
        Path kb = work.resolve("kb");
        TestSupport.Run run = TestSupport.extract(TestSupport.estateFrom(work, "constructed", TestSupport.constructed()), kb);
        Check.equal(0, run.exit, "exit code: " + run.err);
        String fact = "constructed::bmm::Constructed BM.Fact Sales";

        CsvTable sources = TestSupport.table(kb, "logical_table_sources");
        Check.equal(2, sources.rows.size(), "two sources");
        String[] detail = TestSupport.row(sources, "row_id", fact + ".src.LTS_DETAIL");
        Check.equal("0", sources.value(detail, "priority"), "priority as written");
        Check.equal("\"CC_SCHEMA\".\"FCT_SALES_DETAIL\".\"DAY_DT\" >= TIMESTAMP '2020-01-01 00:00:00'",
                sources.value(detail, "fragment_text_desc"), "fragment of the detail source");
        String[] aggregate = TestSupport.row(sources, "row_id", fact + ".src.LTS_MONTHLY_AGG");
        Check.equal("1", sources.value(aggregate, "priority"), "priority as written");
        Check.equal("\"CC_SCHEMA\".\"FCT_SALES_MONTHLY\".\"MONTH_DT\" <  TIMESTAMP '2020-01-01 00:00:00'",
                sources.value(aggregate, "fragment_text_desc"), "the double space is kept");
        Check.equal("\"%1\" <  TIMESTAMP '2020-01-01 00:00:00'", sources.value(aggregate, "fragment_text"), "template text");
        Check.equal("constructed::phys::CC_SCHEMA.FCT_SALES_MONTHLY.MONTH_DT", sources.value(aggregate, "fragment_refs"), "fragment reference");

        CsvTable columns = TestSupport.table(kb, "logical_columns");
        String[] rolling = TestSupport.row(columns, "row_id", fact + ".Rolling Amount 3M");
        Check.equal("PERIODROLLING(\"%1\", -2, 0)", columns.value(rolling, "derived_expr_text"), "template");
        Check.equal("PERIODROLLING(\"Constructed BM\".\"Fact Sales\".\"Amount\", -2, 0)", columns.value(rolling, "derived_expr_text_desc"), "rendered");
        Check.equal(fact + ".Amount", columns.value(rolling, "derived_expr_refs"), "reference");
        Check.equal("false", columns.value(rolling, "is_measure"), "no measure definition");
        Check.equal("PERIODROLLING", columns.value(rolling, "time_series"), "time-series function flagged");
        Check.equal("0", columns.value(rolling, "mapped_source_count"), "defined only by its formula");

        CsvTable rules = TestSupport.table(kb, "aggregation_rules");
        Check.equal(2, rules.rows.size(), "two rules for Amount");
        String[] byDefault = TestSupport.row(rules, "name", "AggRule_Default");
        Check.equal("true", rules.value(byDefault, "is_default"), "default");
        Check.equal(" SUM(\"%1\")", rules.value(byDefault, "expr_text"), "default text with its leading space");
        String[] override = TestSupport.row(rules, "name", "AggRule_Override_Time");
        Check.equal("false", rules.value(override, "is_default"), "override");
        Check.equal("LAST(\"%1\")", rules.value(override, "expr_text"), "override text");
        Check.equal(fact + ".Amount", rules.value(override, "logical_column_id"), "both on Amount");

        CsvTable coverage = TestSupport.table(kb, "coverage");
        for (String[] expected : new String[][] {{"LogicalTable", "1"}, {"LogicalColumn", "2"}, {"AggrRule", "2"},
                {"LogicalTableSource", "2"}, {"ColumnMapping", "2"}}) {
            Check.equal(expected[1], coverage.value(TestSupport.row(coverage, "object_type", expected[0]), "parsed"),
                    expected[0] + " parsed");
        }
        Check.equal(0, TestSupport.table(kb, "errors").rows.size(), "no errors");
        Check.equal(0, TestSupport.rows(TestSupport.table(kb, "unparsed"), "kind", "unmodelled_type").size(), "every type present has a reader");
    }

    /** Differences 5 and 6, a time-series formula, and formulas a spreadsheet would alter. */
    public void testReferencesResolveWhateverTheReadingOrderAndKeepTheirPlaces() {
        Path work = TestSupport.work("bmm-forward");
        Path estate = work.resolve("fwd");
        String model = " subjectAreaRef=\"" + BASE + "BusinessModel/bm.xml#mbm\"";
        TestSupport.object(estate, "BusinessModel", "bm.xml", "<BusinessModel mdsid=\"mbm\" name=\"BM\"" + NS + "/>");
        // File a.xml is read before z.xml, the dimension and the variable; all four kinds of target are "later".
        TestSupport.object(estate, "LogicalTable", "a.xml", "<LogicalTable mdsid=\"mta\" name=\"A\"" + NS + model + ">"
                + "<LogicalColumn mdsid=\"mca1\" name=\"Calc\">"
                + expression("Expr", " ago (\"%1\" ,  \"%2\" , 1) + %3 + \"%4\" + \"%5\" ", " ago (rendered) ",
                        "LogicalTable/z.xml#mcz", "Dimension/d.xml#mlv", "PhysicalTable/gone.xml#nothing",
                        "Variable/v.xml#mv", "LogicalTable/a.xml#mca3")
                + "</LogicalColumn>"
                + "<LogicalColumn mdsid=\"mca2\" name=\"Eq\">" + expression("Expr", "=1+1", "=1+1") + "</LogicalColumn>"
                + "<LogicalColumn mdsid=\"mca3\" name=\"Mapped\"><AttrDefn>"
                + expression("Expr", "TODATE(\"%1\")", "TODATE(x)", "LogicalTable/a.xml#mca3") + "</AttrDefn></LogicalColumn>"
                + "<LogicalColumn mdsid=\"mca4\" name=\"Lone\">"
                + expression("Expr", "\"%1\"", "x", "LogicalTable/gone.xml#nothing") + "</LogicalColumn>"
                + "<LogicalColumn mdsid=\"mca5\" name=\"Named Like\">"
                + expression("Expr", "'PERIODROLLING' || 'AGO'", "x") + "</LogicalColumn>"
                + "</LogicalTable>");
        TestSupport.object(estate, "LogicalTable", "z.xml", "<LogicalTable mdsid=\"mtz\" name=\"Z Table\"" + NS + model + ">"
                + "<LogicalColumn mdsid=\"mcz\" name=\"Z Col\"/></LogicalTable>");
        TestSupport.object(estate, "Dimension", "d.xml", "<Dimension mdsid=\"mdm\" name=\"Time Dim\"" + NS + model + ">"
                + "<LogicalLevel mdsid=\"mlv\" name=\"Month Level\"/></Dimension>");
        TestSupport.object(estate, "Variable", "v.xml", "<Variable mdsid=\"mv\" name=\"CUR\"" + NS + "/>");
        TestSupport.object(estate, "LogicalTableSource", "s.xml", "<LogicalTableSource mdsid=\"mls\" name=\"S\"" + NS
                + " logicalTableRef=\"" + BASE + "LogicalTable/a.xml#mta\">"
                + expression("GroupBy", " GROUPBYLEVEL(\"%1\")", "x", "Dimension/d.xml#mlv")
                + "<ColumnMapping>" + expression("LogicalColumnExpr", "\"%1\"", "x", "LogicalTable/gone.xml#nothing", "LogicalTable/a.xml#mca3")
                + "</ColumnMapping></LogicalTableSource>");

        Path kb = work.resolve("kb");
        TestSupport.Run run = TestSupport.extract(estate, kb);
        Check.equal(3, run.exit, "exit 3: three references do not resolve. " + run.err);

        CsvTable columns = TestSupport.table(kb, "logical_columns");
        String[] calc = TestSupport.row(columns, "row_id", "fwd::bmm::BM.A.Calc");
        Check.equal("fwd::bmm::BM.Z Table.Z Col\nfwd::bmm::BM.Time Dim.Month Level\n\nfwd::var::CUR\nfwd::bmm::BM.A.Mapped",
                columns.value(calc, "derived_expr_refs"),
                "one reference per line: a column of a table read later, a level, an empty line for the one "
                        + "that does not resolve, a variable, a column further down the same table");
        Check.equal(" ago (\"%1\" ,  \"%2\" , 1) + %3 + \"%4\" + \"%5\" ", columns.value(calc, "derived_expr_text"),
                "leading, doubled and trailing spaces are kept");
        Check.equal("AGO", columns.value(calc, "time_series"), "'ago (' flags AGO whatever the letter case");
        Check.equal("=1+1", columns.value(TestSupport.row(columns, "name", "Eq"), "derived_expr_text"), "a formula starting with '=' is unchanged");
        String[] mapped = TestSupport.row(columns, "name", "Mapped");
        Check.equal("TODATE(\"%1\")", columns.value(mapped, "mapping_expr_text"), "the column's own definition");
        Check.equal("fwd::bmm::BM.A.Mapped", columns.value(mapped, "mapping_expr_refs"), "a column may refer to itself");
        Check.equal("TODATE", columns.value(mapped, "time_series"), "the definition is searched too");
        Check.equal("1", columns.value(mapped, "mapped_source_count"), "mapped by the one source");
        Check.equal("", columns.value(TestSupport.row(columns, "name", "Lone"), "derived_expr_refs"),
                "a single reference that does not resolve leaves an empty cell");
        Check.equal("", columns.value(TestSupport.row(columns, "name", "Named Like"), "time_series"),
                "a function name with no opening bracket after it is not flagged");

        CsvTable sources = TestSupport.table(kb, "logical_table_sources");
        Check.equal("fwd::bmm::BM.Time Dim.Month Level", sources.value(sources.rows.get(0), "group_by_refs"), "the grain names a level");
        Check.equal(" GROUPBYLEVEL(\"%1\")", sources.value(sources.rows.get(0), "group_by_text"), "grain text");
        CsvTable map = TestSupport.table(kb, "lts_column_map");
        Check.equal("\nfwd::bmm::BM.A.Mapped", map.value(map.rows.get(0), "logical_expr_refs"), "the empty line keeps the second entry in second place");
        Check.equal("fwd::bmm::BM.A.Mapped", map.value(map.rows.get(0), "logical_column_id"), "the first reference that resolved");
        Check.equal("", map.value(map.rows.get(0), "physical_column_id"), "no physical expression");
        Check.equal("", map.value(map.rows.get(0), "physical_expr_text"), "no physical expression");

        CsvTable errors = TestSupport.table(kb, "errors");
        Check.equal(3, errors.rows.size(), "three errors: " + TestSupport.column(errors, "source_id"));
        String[] error = TestSupport.row(errors, "source_id", "fwd::bmm::BM.A.Calc");
        Check.equal("unresolved_reference", errors.value(error, "kind"), "kind");
        Check.equal("ExpressionRef", errors.value(error, "object_type"), "marker type");
        Check.equal("reference to PhysicalTable could not be resolved to a Knowledge Base identifier", errors.value(error, "reason"), "reason");
        Check.equal("reference to LogicalTable could not be resolved to a Knowledge Base identifier",
                errors.value(TestSupport.row(errors, "source_id", "fwd::bmm::BM.A.src.S"), "reason"), "a mapping's error is against its source");

        // Until story 1.6 the dimension and the variable were only named ahead of their readers, and this
        // asserted that they still counted as skipped and unmodelled. They have readers now.
        CsvTable coverage = TestSupport.table(kb, "coverage");
        for (String type : new String[] {"Dimension", "LogicalLevel", "Variable"}) {
            String[] counted = TestSupport.row(coverage, "object_type", type);
            Check.equal("1", coverage.value(counted, "parsed"), type + " is read");
            Check.equal("0", coverage.value(counted, "skipped"), type + " is not skipped");
        }
        Check.equal("3", coverage.value(TestSupport.row(coverage, "object_type", "ExpressionRef"), "skipped"), "each error is counted");
        Check.equal(0, TestSupport.rows(TestSupport.table(kb, "unparsed"), "kind", "unmodelled_type").size(),
                "neither is reported as a type with no reader any more");
        Check.equal(List.of("fwd::bmm::BM.Time Dim.Month Level"), TestSupport.column(TestSupport.table(kb, "logical_levels"), "row_id"),
                "the level a formula names is a row, under the same identifier");
        Check.equal(List.of("fwd::var::CUR"), TestSupport.column(TestSupport.table(kb, "variables"), "row_id"),
                "and so is the variable");
    }

    public void testObjectsThatCannotBeNamedAreErrorsAndTheRestIsRead() {
        Path work = TestSupport.work("bmm-unnamed");
        Path estate = work.resolve("bad");
        String model = " subjectAreaRef=\"" + BASE + "BusinessModel/bm.xml#mbm\"";
        TestSupport.object(estate, "BusinessModel", "bm.xml", "<BusinessModel mdsid=\"mbm\" name=\"BM\"" + NS + "/>");
        TestSupport.object(estate, "LogicalTable", "a.xml", "<LogicalTable mdsid=\"mt1\"" + NS + model + "/>");
        TestSupport.object(estate, "LogicalTable", "b.xml", "<LogicalTable mdsid=\"mt2\" name=\"It's lost\"" + NS
                + " subjectAreaRef=\"" + BASE + "BusinessModel/gone.xml#nothing\"/>");
        TestSupport.object(estate, "LogicalTable", "c.xml", "<LogicalTable mdsid=\"mt3\" name=\"Good\"" + NS + model + ">"
                + "<LogicalColumn mdsid=\"mc1\"/><LogicalColumn mdsid=\"mc2\" name=\"Kept\"><MeasureDefn/></LogicalColumn></LogicalTable>");
        TestSupport.object(estate, "LogicalTableSource", "s1.xml", "<LogicalTableSource mdsid=\"ms1\" name=\"Orphan\"" + NS
                + " logicalTableRef=\"" + BASE + "LogicalTable/b.xml#mt2\"/>");
        TestSupport.object(estate, "LogicalTableSource", "s2.xml", "<LogicalTableSource mdsid=\"ms2\"" + NS
                + " logicalTableRef=\"" + BASE + "LogicalTable/c.xml#mt3\"/>");

        Path kb = work.resolve("kb");
        Check.equal(3, TestSupport.extract(estate, kb).exit, "exit 3");
        Check.equal(List.of("bad::bmm::BM.Good"), TestSupport.column(TestSupport.table(kb, "logical_tables"), "row_id"), "the good table is read");
        CsvTable columns = TestSupport.table(kb, "logical_columns");
        Check.equal(List.of("bad::bmm::BM.Good.Kept"), TestSupport.column(columns, "row_id"), "and its named column");
        Check.equal("true", columns.value(columns.rows.get(0), "is_measure"), "a measure definition with no rule still makes a measure");
        Check.equal(0, TestSupport.table(kb, "aggregation_rules").rows.size(), "and gives no rule rows");
        Check.equal(0, TestSupport.table(kb, "logical_table_sources").rows.size(), "no source could be named");

        CsvTable errors = TestSupport.table(kb, "errors");
        Check.equal(List.of("mc1", "ms1", "ms2", "mt1", "mt2"), sorted(TestSupport.column(errors, "source_id")), "one error per object, by its source id");
        Check.equal("missing_name", errors.value(TestSupport.row(errors, "source_id", "mt1"), "kind"), "a table with no name");
        Check.equal("table has no name", errors.value(TestSupport.row(errors, "source_id", "mt1"), "reason"), "reason");
        String[] lost = TestSupport.row(errors, "source_id", "mt2");
        Check.equal("unresolved_reference", errors.value(lost, "kind"), "a table whose business model is not found");
        Check.equal("business model for logical table \"It's lost\" could not be resolved", errors.value(lost, "reason"), "reason, quoted as the reference quotes it");
        Check.equal("LogicalColumn", errors.value(TestSupport.row(errors, "source_id", "mc1"), "object_type"), "a column with no name");
        Check.equal("source name or owning logical table could not be resolved", errors.value(TestSupport.row(errors, "source_id", "ms1"), "reason"), "a source whose table was refused");
        Check.equal("unresolved_reference", errors.value(TestSupport.row(errors, "source_id", "ms2"), "kind"), "a source with no name");
    }

    public void testTimeSeriesSearch() {
        Check.equal(List.of("AGO", "TODATE"), TimeSeries.used(
                new Expression("ToDate(\"%1\", \"%2\")", "", List.of()), new Expression("", "x + Ago\n(\"%1\")", List.of())),
                "found in either text of either expression, reported in fixed order");
        Check.equal(List.of(), TimeSeries.used(new Expression("LONGAGO(1) + AGO_X(2) + AGO", "", List.of())),
                "the name must stand alone and be followed by a bracket");
        Check.equal(List.of("PERIODROLLING"), TimeSeries.used(new Expression("", "(periodrolling (x))", List.of())), "any letter case");
        Check.equal(List.of(), TimeSeries.used(Expression.EMPTY, Expression.EMPTY), "nothing in nothing");
    }

    private static List<String> sorted(List<String> values) {
        List<String> copy = new java.util.ArrayList<>(values);
        copy.sort(null);
        return copy;
    }
}
