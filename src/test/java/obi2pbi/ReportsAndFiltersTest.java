package obi2pbi;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import obi2pbi.in.kb.CsvTable;

/**
 * Reports, their criteria blocks, columns and views (story 1.8); filters, prompts and saved filters
 * (story 1.9). Every catalog file is written by the test under {@code build/}.
 */
public final class ReportsAndFiltersTest {

    private static final String SAW = "com.siebel.analytics.web/report/v1.1";
    private static final String SAWX = "com.siebel.analytics.web/expression/v1.1";
    private static final String R = "{" + SAW + "}";
    private static final String X = "{" + SAWX + "}";
    private static final String REPORT = R + "report";
    private static final String SAVED_FILTER = "{com.siebel.analytics.web/savedfilter/v1.1}savedFilterObject";
    private static final String NS = " xmlns:saw=\"" + SAW + "\" xmlns:sawx=\"" + SAWX
            + "\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"";

    private static String report(String body) {
        return "<saw:report" + NS + " xmlVersion=\"201201160\">" + body + "</saw:report>";
    }

    private static String savedFilter(String body) {
        return "<sawsf:savedFilterObject xmlns:sawsf=\"com.siebel.analytics.web/savedfilter/v1.1\"" + NS
                + " subjectArea=\"&quot;Sales&quot;\">" + body + "</sawsf:savedFilterObject>";
    }

    private static String column(String type, String id, String formula) {
        return "<saw:column xsi:type=\"" + type + "\" columnID=\"" + id + "\"><saw:columnFormula>" + formula
                + "</saw:columnFormula></saw:column>";
    }

    private static String sql(String text) {
        return "<sawx:expr xsi:type=\"sawx:sqlExpression\">" + text + "</sawx:expr>";
    }

    private static String literal(String text) {
        return "<sawx:expr xsi:type=\"xsd:string\">" + text + "</sawx:expr>";
    }

    private static String comparison(String op, String... operands) {
        return "<sawx:expr xsi:type=\"sawx:comparison\" op=\"" + op + "\">" + String.join("", operands) + "</sawx:expr>";
    }

    private static String logical(String op, String... parts) {
        return "<sawx:expr xsi:type=\"sawx:logical\" op=\"" + op + "\">" + String.join("", parts) + "</sawx:expr>";
    }

    private static Path extract(String work, int exit, String... nameThenXml) {
        Path folder = TestSupport.work(work);
        Path estate = folder.resolve("est");
        for (int i = 0; i < nameThenXml.length; i += 2) {
            TestSupport.catalogFile(estate, nameThenXml[i], nameThenXml[i + 1]);
        }
        Path kb = folder.resolve("kb");
        TestSupport.Run run = TestSupport.extract(estate, kb);
        Check.equal(exit, run.exit, run.out + run.err);
        return kb;
    }

    private static List<String> cells(CsvTable table, String rowId, String... columns) {
        String[] row = TestSupport.row(table, "row_id", rowId);
        List<String> values = new ArrayList<>();
        for (String column : columns) {
            values.add(table.value(row, column));
        }
        return values;
    }

    private static String unread(CsvTable unparsed, String kind, String detail) {
        for (String[] row : unparsed.rows) {
            if (unparsed.value(row, "kind").equals(kind) && unparsed.value(row, "detail").equals(detail)) {
                return unparsed.value(row, "occurrences");
            }
        }
        return "0";
    }

    // ------------------------------------------------------------------ story 1.8: reports, columns, views

    public void testColumnsKeepTheirFormulaExactlyAndTheTwoFlagsComeFromTheSource() {
        Path kb = extract("report-columns", 0, "sales/by+month", report(
                "<saw:criteria xsi:type=\"saw:simpleCriteria\" subjectArea=\"&quot;A - Sample Sales&quot;\"><saw:columns>"
                        + column("saw:regularColumn", "c1", sql("\"Sales\".\"Revenue\""))
                        + column("saw:calculatedColumn", "c2", sql(" \"Sales\".\"Revenue\"  /  NULLIF(\"Sales\".\"Units\", 0) "))
                        + column("saw:regularColumn", "c3", sql("count(distinct \"Orders\".\"Id\")"))
                        + "<saw:column columnID=\"c4\"><saw:columnHeading><saw:caption fmt=\"text\"><saw:text> My Heading </saw:text>"
                        + "</saw:caption></saw:columnHeading><saw:tableHeading><saw:caption><saw:text>My Table</saw:text></saw:caption>"
                        + "</saw:tableHeading></saw:column>"
                        + "</saw:columns></saw:criteria>"));
        String id = "est::cat::sales/by month";
        CsvTable analyses = TestSupport.table(kb, "analyses");
        Check.equal(List.of("row_id", "rpd_id", "name", "catalog_path", "xml_version", "criteria_count", "column_count"), analyses.header, "analyses columns");
        Check.equal(List.of(id, "est", "by month", "sales/by month", "201201160", "1", "4"), List.of(analyses.rows.get(0)), "the report");
        CsvTable criteria = TestSupport.table(kb, "analysis_criteria");
        Check.equal(List.of(id + "#criteria0", "est", id, "0", "\"A - Sample Sales\""), List.of(criteria.rows.get(0)),
                "the subject area as the catalog wrote it, quotes included");

        CsvTable columns = TestSupport.table(kb, "analysis_columns");
        Check.equal(List.of("row_id", "rpd_id", "analysis_id", "criteria_id", "position", "column_id", "heading", "table_heading",
                "formula_construct", "formula_type", "formula", "is_computation", "uses_eval"), columns.header, "analysis_columns columns");
        Check.equal(List.of(id + "#c0.0", "est", id, id + "#criteria0", "0", "c1", "", "", R + "columnFormula", "sawx:sqlExpression",
                "\"Sales\".\"Revenue\"", "false", "false"), List.of(TestSupport.row(columns, "row_id", id + "#c0.0")), "a plain column");
        Check.equal(List.of(" \"Sales\".\"Revenue\"  /  NULLIF(\"Sales\".\"Units\", 0) ", "true"),
                cells(columns, id + "#c0.1", "formula", "is_computation"), "a calculation: stored exactly, spaces included");
        Check.equal(List.of("count(distinct \"Orders\".\"Id\")", "false"), cells(columns, id + "#c0.2", "formula", "is_computation"),
                "is_computation follows the column's type, never the formula text");
        Check.equal(List.of("c4", " My Heading ", "My Table", "", "", "", "false"),
                cells(columns, id + "#c0.3", "column_id", "heading", "table_heading", "formula_construct", "formula_type", "formula", "is_computation"),
                "headings as typed; a column with no formula and no type");
        Check.equal(List.of(), SchemaTest.valueProblems(kb), "every declared link holds");
    }

    public void testANestedFormulaIsStoredWholeAndADatabaseFunctionIsFlagged() {
        Path kb = extract("report-nested", 0, "r", report("<saw:criteria><saw:columns>"
                + column("saw:regularColumn", "c1", "<sawx:expr xsi:type=\"sawx:sqlExpression\">CAST(<sawx:expr xsi:type=\"sawx:eval\">"
                        + "EVALUATE('OWNER(%1)', \"Facts\".\"Id\")</sawx:expr> AS <x:other xmlns:x=\"urn:x\" k=\"1\">INT</x:other>)</sawx:expr>")
                + column("saw:regularColumn", "c2", sql("1") + "<saw:deeper>" + sql("2") + "</saw:deeper>")
                + "<saw:column columnID=\"c3\"><saw:wrap><saw:columnFormula>" + sql("3") + "</saw:columnFormula></saw:wrap>"
                + "<saw:columnFormula><sawx:expr xsi:type=\"sawx:sqlExpression\">second <sawx:expr xsi:type=\"sawx:eval\">E</sawx:expr></sawx:expr>"
                + "</saw:columnFormula></saw:column>"
                + "</saw:columns></saw:criteria>"));
        CsvTable columns = TestSupport.table(kb, "analysis_columns");
        Check.equal(List.of("CAST(EVALUATE('OWNER(%1)', \"Facts\".\"Id\") AS INT)", "sawx:sqlExpression", "true"),
                cells(columns, "est::cat::r#c0.0", "formula", "formula_type", "uses_eval"),
                "difference 8: the whole text, not 'CAST('; and the nested database function is flagged");
        Check.equal(List.of("1", "false"), cells(columns, "est::cat::r#c0.1", "formula", "uses_eval"),
                "only the expression directly inside the formula element is the formula");
        Check.equal(List.of("3", "true"), cells(columns, "est::cat::r#c0.2", "formula", "uses_eval"),
                "a formula at any depth belongs to its column; the first is stored, and a database function in any of them is flagged");

        CsvTable unparsed = TestSupport.table(kb, "unparsed");
        Check.equal("3", unread(unparsed, "unread_text", X + "expr#text"),
                "audited: the second expression of c2 and both of c3's second formula are unread; the nested text taken whole is not");
        Check.equal("0", unread(unparsed, "unread_text", "{urn:x}other#text"), "text taken as part of the whole formula is not unread");
        Check.equal("1", unread(unparsed, "unread_element", "{urn:x}other"), "but the nested element itself was not looked at, and is still listed");
        Check.equal("1", unread(unparsed, "unread_attribute", "{urn:x}other@k"), "with its attributes");
    }

    public void testColumnsAndFiltersBelongToTheNearestCriteriaBlock() {
        Path kb = extract("report-blocks", 0, "union", report(
                "<saw:criteria xsi:type=\"saw:setOperation\"><saw:columns><saw:column xsi:type=\"saw:resultColumn\" columnID=\"o1\"/></saw:columns>"
                        + "<saw:criteria subjectArea=\"&quot;A&quot;\"><saw:columns>" + column("saw:regularColumn", "a1", sql("\"T\".\"A\""))
                        + column("saw:regularColumn", "a2", sql("\"T\".\"B\"")) + "</saw:columns>"
                        + "<saw:filter>" + comparison("equal", sql("\"T\".\"Y\""), literal("2024")) + "</saw:filter></saw:criteria>"
                        + "<saw:criteria subjectArea=\"XSA('u'.'data')\"><saw:columns>" + column("saw:regularColumn", "b1", sql("\"T\".\"C\""))
                        + "</saw:columns></saw:criteria></saw:criteria>"
                        + "<saw:views><saw:view xsi:type=\"saw:tableView\" name=\"t\"><saw:column columnID=\"o1\"/></saw:view></saw:views>"));
        String id = "est::cat::union";
        CsvTable criteria = TestSupport.table(kb, "analysis_criteria");
        Check.equal(List.of("", "\"A\"", "XSA('u'.'data')"), TestSupport.column(criteria, "subject_area"), "three blocks, the outer one first");
        Check.equal(List.of("3", "4"), cells(TestSupport.table(kb, "analyses"), id, "criteria_count", "column_count"), "counts");
        CsvTable columns = TestSupport.table(kb, "analysis_columns");
        Check.equal(List.of(id + "#c0.0", id + "#c1.0", id + "#c1.1", id + "#c2.0"), TestSupport.column(columns, "row_id"),
                "each column once, under its nearest block; the column inside the view belongs to no block");
        Check.equal(List.of(id + "#criteria0", "o1", "", "true"), cells(columns, id + "#c0.0", "criteria_id", "column_id", "formula", "is_computation"),
                "the outer block's result column");
        CsvTable filters = TestSupport.table(kb, "analysis_filters");
        Check.equal(List.of(id + "#f1.0", id + "#f1.1", id + "#f1.2"), TestSupport.column(filters, "row_id"),
                "the filter is numbered under the block it belongs to, and owned by the report");
        Check.equal(List.of(id, id, id), TestSupport.column(filters, "owner_id"), "owner is the report");
    }

    public void testViewsKeepTheirTypeInTheSourcesWordsAndCountWhatTheyArrangeAndBind() {
        Path kb = extract("report-views", 0, "v", report("<saw:criteria/><saw:views currentView=\"0\">"
                + "<saw:view xsi:type=\"saw:compoundView\" name=\"compoundView!1\"><saw:cvTable><saw:cvRow>"
                + "<saw:cvCell viewName=\"titleView!1\"/><saw:cvCell viewName=\"dvtchart!1\"/><saw:cvCell viewName=\"\"/></saw:cvRow></saw:cvTable></saw:view>"
                + "<saw:view xsi:type=\"saw:dvtchart\" name=\"dvtchart!1\"><saw:display type=\"bar\" subtype=\"stacked\"/>"
                + "<saw:selections><saw:categories><saw:category><saw:columnRef columnID=\"c1\"/></saw:category></saw:categories>"
                + "<saw:measures><saw:column columnID=\"c2\"/><saw:column columnID=\"\"/></saw:measures></saw:selections></saw:view>"
                + "<saw:view name=\"untyped\"><saw:inner><saw:display type=\"line\"/></saw:inner></saw:view>"
                + "<saw:view xsi:type=\"saw:viewFromANewerOBIEE\" name=\"new!1\"/>"
                + "</saw:views>"));
        String id = "est::cat::v";
        CsvTable views = TestSupport.table(kb, "analysis_views");
        Check.equal(List.of("row_id", "rpd_id", "analysis_id", "position", "view_type", "name", "references_views", "bound_column_count",
                "display_type", "display_subtype"), views.header, "analysis_views columns");
        Check.equal(List.of(id + "#view0", "est", id, "0", "saw:compoundView", "compoundView!1", "2", "0", "", ""),
                List.of(TestSupport.row(views, "row_id", id + "#view0")), "a layout view: it names two other views");
        Check.equal(List.of("saw:dvtchart", "0", "2", "bar", "stacked"),
                cells(views, id + "#view1", "view_type", "references_views", "bound_column_count", "display_type", "display_subtype"),
                "a chart: type in the source's words, two bound columns, kind and variant");
        Check.equal(List.of("(no xsi:type)", "untyped", "", ""), cells(views, id + "#view2", "view_type", "name", "display_type", "display_subtype"),
                "a view with no type says so; a display that is not directly inside the view is not its display");
        Check.equal(List.of("saw:viewFromANewerOBIEE"), cells(views, id + "#view3", "view_type"), "an unknown type is a row, not an error");
        CsvTable coverage = TestSupport.table(kb, "coverage");
        Check.equal("4", coverage.value(TestSupport.row(coverage, "object_type", "saw:view (element, inside an Analysis)"), "parsed"),
                "views are counted under a label of their own");
        CsvTable unparsed = TestSupport.table(kb, "unparsed");
        Check.equal("0", unread(unparsed, "unread_attribute", R + "views@currentView"), "read so that it is not reported");
        Check.equal("0", unread(unparsed, "unread_element", R + "inner"), "every element below a view was asked for a column binding");
    }

    // ------------------------------------------------------------------ story 1.9: filters, prompts, saved filters

    private static final String[] FILTER = {"scope", "position", "op", "expr_type", "expr_text", "reference", "target_id", "resolution",
            "uses_eval", "parent_id", "depth"};

    public void testAReportFilterIsOneRowPerExpressionWithParentAndDepth() {
        Path kb = extract("filter-simple", 0, "f", report("<saw:criteria><saw:filter>"
                + comparison("equal", sql("\"Time\".\"Year\""), "<sawx:expr xsi:type=\"xsd:decimal\">2024</sawx:expr>")
                + "</saw:filter></saw:criteria>"));
        String id = "est::cat::f";
        CsvTable filters = TestSupport.table(kb, "analysis_filters");
        Check.equal(List.of("row_id", "rpd_id", "scope", "owner_id", "position", "op", "expr_type", "expr_text", "reference", "target_id",
                "resolution", "uses_eval", "parent_id", "depth"), filters.header, "analysis_filters columns: the reference's, then parent and depth");
        Check.equal(List.of("analysis", "0", "equal", "sawx:comparison", "", "", "", "", "false", "", "0"),
                cells(filters, id + "#f0.0", FILTER), "the comparison; its operands are rows of their own, so it has no text itself");
        Check.equal(List.of("analysis", "1", "", "sawx:sqlExpression", "\"Time\".\"Year\"", "", "", "", "false", id + "#f0.0", "1"),
                cells(filters, id + "#f0.1", FILTER), "the column, exactly");
        Check.equal(List.of("analysis", "2", "", "xsd:decimal", "2024", "", "", "", "false", id + "#f0.0", "1"),
                cells(filters, id + "#f0.2", FILTER), "the literal");
        Check.equal(List.of(), SchemaTest.valueProblems(kb), "parent_id points at rows of the same table");
    }

    public void testAFilterRowHoldsItsOwnTextAndTheTextOfNestedElementsThatHaveNoRow() {
        Path kb = extract("filter-own-text", 0, "m", report("<saw:criteria><saw:filter>"
                + "<sawx:expr xsi:type=\"sawx:list\" op=\"in\"> a " + sql("\"T\".\"C\"") + " b "
                + "<sawx:expr xsi:type=\"saw:memberExpr\"><saw:group><saw:value>FunPod</saw:value>, <saw:value>BizTech</saw:value>"
                + "<saw:deep>" + literal("x") + " after</saw:deep></saw:group></sawx:expr> c </sawx:expr>"
                + "</saw:filter></saw:criteria>"));
        CsvTable filters = TestSupport.table(kb, "analysis_filters");
        String id = "est::cat::m#f0.";
        Check.equal(List.of(" a  b  c "), cells(filters, id + 0, "expr_text"),
                "the text directly inside, before, between and after the nested expressions; not theirs");
        Check.equal(List.of("\"T\".\"C\""), cells(filters, id + 1, "expr_text"), "a leaf is unchanged");
        Check.equal(List.of("FunPod, BizTech after"), cells(filters, id + 2, "expr_text"),
                "nested elements that are not expressions have no row, so their text is kept here; the expression inside them is not");
        Check.equal(List.of("x", id + 2, "2"), cells(filters, id + 3, "expr_text", "parent_id", "depth"), "which has its own row");
        CsvTable unparsed = TestSupport.table(kb, "unparsed");
        Check.equal("0", unread(unparsed, "unread_text", R + "value#text"), "text kept in a row is not unread");
        Check.equal("2", unread(unparsed, "unread_element", R + "value"), "the elements themselves were not looked at, and are listed");
    }

    public void testNestedConditionsCanBeRebuiltFromParentAndDepth() {
        // A and (B or C), then the same leaves grouped differently: A and (B) and C.
        String a = comparison("equal", sql("A"), literal("1"));
        String b = "<sawx:expr xsi:type=\"sawx:special\" op=\"prompted\">" + sql("B") + "</sawx:expr>";
        String c = comparison("in", "<saw:wrapper>" + sql("C") + "</saw:wrapper>", literal("3"));
        Path kb = extract("filter-nested", 0,
                "one", report("<saw:criteria><saw:filter>" + logical("and", a, logical("or", b, c)) + "</saw:filter></saw:criteria>"),
                "two", report("<saw:criteria><saw:filter>" + logical("and", a, logical("or", b), c) + "</saw:filter></saw:criteria>"));
        CsvTable filters = TestSupport.table(kb, "analysis_filters");
        String one = "est::cat::one#f0.";
        String two = "est::cat::two#f0.";
        List<String> parentsOne = new ArrayList<>();
        List<String> parentsTwo = new ArrayList<>();
        List<String> opsOne = new ArrayList<>();
        for (int k = 0; k < 10; k++) {
            parentsOne.add(cells(filters, one + k, "parent_id", "depth").toString().replace(one, "#"));
            parentsTwo.add(cells(filters, two + k, "parent_id", "depth").toString().replace(two, "#"));
            opsOne.add(cells(filters, one + k, "op").get(0));
        }
        Check.equal(List.of("and", "equal", "", "", "or", "prompted", "", "in", "", ""), opsOne, "pre-order, source order kept");
        Check.equal(List.of("[, 0]", "[#0, 1]", "[#1, 2]", "[#1, 2]", "[#0, 1]", "[#4, 2]", "[#5, 3]", "[#4, 2]", "[#7, 3]", "[#7, 3]"),
                parentsOne, "A and (B or C): C's comparison hangs under the or-group; an element that is not an expression is passed over");
        Check.equal(List.of("[, 0]", "[#0, 1]", "[#1, 2]", "[#1, 2]", "[#0, 1]", "[#4, 2]", "[#5, 3]", "[#0, 1]", "[#7, 2]", "[#7, 2]"),
                parentsTwo, "A and (B) and C: the same rows in the same order, told apart only by parent and depth");
        Check.equal(List.of("prompted", "sawx:special"), cells(filters, one + 5, "op", "expr_type"), "a prompted filter is stored as it is");
    }

    public void testASavedFilterReferenceRecordsHowItResolvedAndADatabaseFunctionFlagsItsAncestors() {
        String ref = "<sawx:expr xsi:type=\"sawx:savedFilter\" path=\"/shared/Sales/_filters/My Filter\"/>";
        String gone = "<sawx:expr xsi:type=\"sawx:savedFilter\" path=\"/shared/Sales/_filters/Gone\"/>";
        String notSaved = "<sawx:expr xsi:type=\"sawx:other\" path=\"/shared/Sales/_filters/Gone\"/>";
        String eval = comparison("greater", "<sawx:expr xsi:type=\"sawx:sqlExpression\">CAST(<sawx:expr xsi:type=\"sawx:eval\">EVALUATE('f(%1)', X)"
                + "</sawx:expr> AS INT)</sawx:expr>", literal("5"));
        Path kb = extract("filter-refs", 3,
                "sales/r", report("<saw:criteria><saw:filter>" + logical("and", ref, gone, notSaved, eval) + "</saw:filter></saw:criteria>"),
                "sales/_filters/my+filter", savedFilter("<saw:filter>" + comparison("equal", sql("X"), literal("1")) + "</saw:filter>"));
        String id = "est::cat::sales/r#f0.";
        CsvTable filters = TestSupport.table(kb, "analysis_filters");
        Check.equal(List.of("/shared/Sales/_filters/My Filter", "est::cat::sales/_filters/my filter", "case_normalised"),
                cells(filters, id + 1, "reference", "target_id", "resolution"), "a reference that resolves, after ignoring case");
        Check.equal(List.of("/shared/Sales/_filters/Gone", "", "unresolved"), cells(filters, id + 2, "reference", "target_id", "resolution"),
                "one that does not");
        Check.equal(List.of("/shared/Sales/_filters/Gone", "", ""), cells(filters, id + 3, "reference", "target_id", "resolution"),
                "a path on another type of expression is kept and not resolved");
        CsvTable errors = TestSupport.table(kb, "errors");
        Check.equal(1, errors.rows.size(), "one error row for the one failed resolution");
        Check.equal(List.of("est", "unresolved_reference", "sales/r", X + "expr",
                "'/shared/Sales/_filters/Gone' did not resolve: no object or folder at this path. Recorded rather than repaired (AD-3)."),
                List.of(errors.rows.get(0)), "the error names the report and the expression element");
        Check.equal(List.of("true", "true", "true", "true", "false", "false"),
                List.of(cells(filters, id + 0, "uses_eval").get(0), cells(filters, id + 4, "uses_eval").get(0), cells(filters, id + 5, "uses_eval").get(0),
                        cells(filters, id + 6, "uses_eval").get(0), cells(filters, id + 7, "uses_eval").get(0), cells(filters, id + 1, "uses_eval").get(0)),
                "the call and every expression around it are flagged; its siblings are not");
        Check.equal(List.of("CAST( AS INT)", "EVALUATE('f(%1)', X)"), List.of(cells(filters, id + 5, "expr_text").get(0), cells(filters, id + 6, "expr_text").get(0)),
                "difference 8 in a filter: the text after the nested expression is kept; the nested expression's text is in its own row");
    }

    public void testEveryExpressionOfASavedFilterIsWrittenAndTheObjectRowKeepsItsOutermost() {
        String three = logical("and", " own ", comparison("equal", sql("\"T\".\"A\""), literal("1")), " text ",
                "<sawx:expr xsi:type=\"sawx:special\" op=\"prompted\"><saw:wrap>" + sql("\"T\".\"B\"") + "</saw:wrap></sawx:expr>",
                "<sawx:expr xsi:type=\"sawx:savedFilter\" path=\"other\"/>",
                "<sawx:expr xsi:type=\"sawx:savedFilter\" path=\"/shared/x/_filters/missing\"/>");
        Path kb = extract("saved-filter", 3,
                "x/_filters/three+conditions", savedFilter("<saw:filter>" + three + "</saw:filter>"),
                "x/_filters/other", savedFilter("<saw:filter><sawx:expr xsi:type=\"sawx:list\" op=\"in\">" + sql("Y")
                        + "<sawx:expr xsi:type=\"sawx:eval\">EVALUATE('g')</sawx:expr></sawx:expr></saw:filter>"),
                "x/_filters/empty", savedFilter(""));
        String id = "est::cat::x/_filters/three conditions";
        CsvTable saved = TestSupport.table(kb, "saved_filters");
        Check.equal(List.of("row_id", "rpd_id", "name", "catalog_path", "xml_version", "op", "expr_type", "expr_text", "uses_eval"), saved.header,
                "saved_filters columns");
        Check.equal(3, saved.rows.size(), "one row per saved filter object");
        Check.equal(List.of(id, "est", "three conditions", "x/_filters/three conditions", "", "and", "sawx:logical", " own  text ", "false"),
                List.of(TestSupport.row(saved, "row_id", id)), "the object, with the operator, type and own text of its outermost expression");
        Check.equal(List.of("in", "sawx:list", "", "true"), cells(saved, "est::cat::x/_filters/other", "op", "expr_type", "expr_text", "uses_eval"),
                "a database function anywhere in the filter flags the object");
        Check.equal(List.of("", "", "", "false"), cells(saved, "est::cat::x/_filters/empty", "op", "expr_type", "expr_text", "uses_eval"),
                "a saved filter with no filter element is still a row");

        CsvTable filters = TestSupport.table(kb, "analysis_filters");
        Check.equal(8, TestSupport.rows(filters, "owner_id", id).size(), "difference 9: all eight expressions, not only the outermost");
        Check.equal(List.of("saved_filter", "0", "and", "sawx:logical", " own  text ", "", "", "", "false", "", "0"),
                cells(filters, id + "#f0", FILTER), "the outermost: what is written directly in it, before, between and after its parts");
        Check.equal(List.of("saved_filter", "1", "equal", "sawx:comparison", "", "", "", "", "false", id + "#f0", "1"),
                cells(filters, id + "#f1", FILTER), "the first condition");
        Check.equal(List.of("saved_filter", "4", "prompted", "sawx:special", "", "", "", "", "false", id + "#f0", "1"),
                cells(filters, id + "#f4", FILTER), "the second");
        Check.equal(List.of("saved_filter", "6", "", "sawx:savedFilter", "", "other", "est::cat::x/_filters/other", "exact", "false", id + "#f0", "1"),
                cells(filters, id + "#f6", FILTER), "a reference to another saved filter, relative to this one's folder, resolved");
        Check.equal(List.of("unresolved", ""), cells(filters, id + "#f7", "resolution", "target_id"), "and one that does not resolve");
        CsvTable errors = TestSupport.table(kb, "errors");
        Check.equal(List.of("x/_filters/three conditions", X + "expr"), List.of(errors.rows.get(0)[2], errors.rows.get(0)[3]),
                "its error names the saved filter");
        Check.equal(1, errors.rows.size(), "one error");
        CsvTable unparsed = TestSupport.table(kb, "unparsed");
        Check.equal("0", unread(unparsed, "unread_attribute", X + "expr@op"), "nothing of the expressions is left unread");
        Check.equal("0", unread(unparsed, "unread_text", X + "expr#text"), "nor their text");
        Check.equal("3", unread(unparsed, "unread_attribute", SAVED_FILTER + "@subjectArea"), "the object's own subject area is not read, and is listed");
        Check.equal(List.of(), SchemaTest.valueProblems(kb), "every declared link holds");
    }

    public void testAPromptInsideAReportIsWrittenWithTheVariableItSets() {
        Path kb = extract("report-prompts", 0, "p", report("<saw:criteria/><saw:prompts>"
                + "<saw:prompt>plain text parameter</saw:prompt>"
                + "<saw:prompt xsi:type=\"saw:columnFilterPrompt\"><saw:formula>" + sql("\"Offices\".\"D1  Office\"") + "</saw:formula>"
                + "<saw:promptOperator op=\"in\"/><saw:promptUIControl xsi:type=\"saw:browse\"/>"
                + "<saw:deep><saw:setPromptVariable variableFormula=\"@{pvOffice}\"/><saw:setPromptVariable variableFormula=\"\"/>"
                + "<saw:setPromptVariable variableFormula=\"@{pvOther}\"/></saw:deep></saw:prompt>"
                + "<saw:prompt xsi:type=\"saw:variablePrompt\"/></saw:prompts>"));
        CsvTable prompts = TestSupport.table(kb, "analysis_prompts");
        Check.equal(List.of("row_id", "rpd_id", "analysis_id", "position", "prompt_type", "column_type", "column_formula", "set_variable",
                "operator", "ui_control"), prompts.header, "analysis_prompts columns");
        Check.equal(2, prompts.rows.size(), "the untyped element is not a prompt");
        Check.equal(List.of("est::cat::p#p1", "est", "est::cat::p", "1", "saw:columnFilterPrompt", "sawx:sqlExpression", "\"Offices\".\"D1  Office\"",
                "@{pvOffice} | @{pvOther}", "in", "saw:browse"), List.of(prompts.rows.get(0)), "the prompt; the skipped element still used number 0");
        Check.equal(List.of("est::cat::p#p2", "saw:variablePrompt", "", "", "", "", ""),
                cells(prompts, "est::cat::p#p2", "row_id", "prompt_type", "column_type", "column_formula", "set_variable", "operator", "ui_control"),
                "a prompt with nothing else in it");
        Check.equal("1", unread(TestSupport.table(kb, "unparsed"), "unread_text", R + "prompt#text"), "the text parameter is listed as not read");
    }

    public void testADashboardPromptOnAPageIsAFilterRowTakenFromItsPlacementAndResolvedOnce() {
        String sawd = "com.siebel.analytics.web/dashboard/v1.1";
        Path kb = extract("filter-dashboard", 3,
                "s/_portal/d/page+1", "<sawd:dashboardPage xmlns:sawd=\"" + sawd + "\"><sawd:reportRef path=\"/shared/s/r\"/>"
                        + "<sawd:globalFilterView path=\"/shared/S/Prompt A\"/><sawd:globalFilterView path=\"/company_shared/x\"/></sawd:dashboardPage>",
                "s/r", report("<saw:criteria/>"), "s/prompt+a", "<saw:view xmlns:saw=\"" + SAW + "\"/>");
        String page = "est::cat::s/_portal/d/page 1";
        CsvTable filters = TestSupport.table(kb, "analysis_filters");
        Check.equal(2, filters.rows.size(), "one row per dashboard prompt placed on the page");
        Check.equal(List.of("dashboard", "0", "", "", "", "/shared/S/Prompt A", "est::cat::s/prompt a", "case_normalised", "false", "", "0"),
                cells(filters, page + "#gfilter0", FILTER), "scope dashboard, owned by the page, no expression");
        Check.equal(List.of(page, "/company_shared/x", "", "unresolved"), cells(filters, page + "#gfilter1", "owner_id", "reference", "target_id", "resolution"),
                "numbered among the page's prompts, not among all its placements");
        CsvTable contents = TestSupport.table(kb, "dashboard_contents");
        for (int k = 0; k < 2; k++) {
            Check.equal(cells(contents, page + "#item" + (k + 1), "reference", "target_id", "resolution"),
                    cells(filters, page + "#gfilter" + k, "reference", "target_id", "resolution"), "the filter row repeats its placement's outcome");
        }
        Check.equal(1, TestSupport.table(kb, "errors").rows.size(), "difference 11: one error row for the unresolved prompt, not two");
    }
}
