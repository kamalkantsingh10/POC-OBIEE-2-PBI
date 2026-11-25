package obi2pbi;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;
import obi2pbi.core.Variable;
import obi2pbi.in.kb.CsvTable;

/**
 * Variables, init blocks, credential redaction and security rows (story 1.6; guide 01 §2.10 to §2.12, §3.7,
 * §3.8, §6.6, §6.7).
 *
 * <p>The tests that deal with withheld values never put such a value into a failure message.
 */
public final class VariablesAndSecurityTest {

    private static final String NS = " xmlns=\"" + TestSupport.XMLNS + "\"";
    private static final String BASE = "/oracle/bi/server/base/";

    /** Made-up values, planted as the values of variables with telling names. They are not real secrets. */
    private static final String PLANTED_ONE = "'planted-" + "value-one'";
    private static final String PLANTED_TWO = "planted \"quoted\", value-two";
    private static final String PLANTED_RENDERED = "planted-rendered-" + "three";

    /** The reference implementation's rule, written here a second time so the test does not lean on the jar's. */
    private static final Pattern CREDENTIAL_NAME = Pattern.compile(
            "password|passwd|pwd|secret|token|credential|apikey|api[_ -]?key", Pattern.CASE_INSENSITIVE);

    private static String variable(String mdsid, String name, String more, String text, String rendered) {
        return "<Variable mdsid=\"" + mdsid + "\" name=\"" + name + "\"" + NS + more + ">"
                + (text == null ? "" : "<Expr mdsid=\"e" + mdsid + "\" name=\"Expr\"><ExprText><![CDATA[" + text + "]]></ExprText>"
                        + "<ExprTextDesc><![CDATA[" + rendered + "]]></ExprTextDesc></Expr>")
                + "</Variable>";
    }

    public void testVariablesAndInitBlocksKeepTheirTextExactly() {
        Path work = TestSupport.work("var-plain");
        Path estate = work.resolve("vars");
        String sql = " select -1,\n  'x' from DUAL where \"A\" = 'VALUEOF(NQ_SESSION.USER)' ";
        TestSupport.object(estate, "InitBlock", "ib.xml", "<InitBlock mdsid=\"mib\" name=\"Set Year (=2020)\" refreshPeriod=\"3600\" isSessionVar=\"true\" isEnabled=\"false\""
                + NS + " connectionPoolRef=\"x\"><InitString><![CDATA[" + sql + "]]></InitString>"
                + "<DBMapItem name=\"DefaultMulDB\"><Value><![CDATA[select 2 from dual]]></Value></DBMapItem></InitBlock>");
        TestSupport.object(estate, "InitBlock", "empty.xml", "<InitBlock mdsid=\"mie\" name=\"Empty\"" + NS + "/>");
        TestSupport.object(estate, "InitBlock", "unnamed.xml", "<InitBlock mdsid=\"miu\"" + NS + "/>");
        // The variable's file sorts after nothing it needs: its init block is found whatever the order.
        TestSupport.object(estate, "Variable", "a.xml", variable("mva", "CURRENT_YEAR", " isSessionVar=\"true\" securityMask=\"2\""
                + " initBlockRef=\"" + BASE + "InitBlock/ib.xml#mib\"", " TIMESTAMP '2001-12-01 00:00:00'", " TIMESTAMP '2001-12-01 00:00:00'"));
        TestSupport.object(estate, "Variable", "b.xml", variable("mvb", "REGION", " initBlockRef=\"" + BASE + "InitBlock/gone.xml#nothing\"", "=1+1", "=1+1"));
        TestSupport.object(estate, "Variable", "c.xml", variable("mvc", "Passed", "", null, null));
        TestSupport.object(estate, "Variable", "d.xml", "<Variable mdsid=\"mvd\" name=\"Uses Another\"" + NS + "><Expr><ExprText>VALUEOF(%1) + %2</ExprText>"
                + "<ObjectRefList><RefObject objectRef=\"" + BASE + "Variable/b.xml#mvb\"/><RefObject objectRef=\"" + BASE + "Variable/gone.xml#nothing\"/></ObjectRefList></Expr></Variable>");
        TestSupport.object(estate, "Variable", "unnamed.xml", "<Variable mdsid=\"mvu\"" + NS + "/>");

        Path kb = work.resolve("kb");
        Check.equal(3, TestSupport.extract(estate, kb).exit, "exit 3: the estate has errors on purpose");

        CsvTable blocks = TestSupport.table(kb, "init_blocks");
        Check.equal(List.of("row_id", "rpd_id", "name", "refresh_period", "is_session", "is_enabled", "init_string"), blocks.header,
                "columns as in the reference, without the guard columns");
        Check.equal(List.of("vars::var::init.Empty", "vars::var::init.Set Year (=2020)"), TestSupport.column(blocks, "row_id"), "identifiers: <estate>::var::init.<name>");
        String[] block = TestSupport.row(blocks, "name", "Set Year (=2020)");
        Check.equal(sql, blocks.value(block, "init_string"), "the query exactly as written: leading space, line break, quotes, trailing space");
        Check.equal("3600", blocks.value(block, "refresh_period"), "refresh period as written");
        Check.equal("true", blocks.value(block, "is_session"), "session flag as written");
        Check.equal("false", blocks.value(block, "is_enabled"), "enabled flag as written");
        String[] empty = TestSupport.row(blocks, "name", "Empty");
        Check.equal("", blocks.value(empty, "init_string"), "a block with no query is still a row");
        Check.equal("", blocks.value(empty, "is_enabled"), "an absent flag is empty, not 'false'");

        CsvTable variables = TestSupport.table(kb, "variables");
        Check.equal(List.of("row_id", "rpd_id", "name", "is_session", "init_block_id", "security_mask", "redacted", "expr_text",
                "expr_text_desc", "expr_refs"), variables.header, "columns as in the reference, without the guard columns");
        Check.equal(List.of("vars::var::CURRENT_YEAR", "vars::var::Passed", "vars::var::REGION", "vars::var::Uses Another"),
                TestSupport.column(variables, "row_id"), "identifiers: <estate>::var::<name>");
        String[] year = TestSupport.row(variables, "name", "CURRENT_YEAR");
        Check.equal("true", variables.value(year, "is_session"), "a session variable");
        Check.equal("vars::var::init.Set Year (=2020)", variables.value(year, "init_block_id"), "its init block");
        Check.equal("2", variables.value(year, "security_mask"), "security mask as written");
        Check.equal("false", variables.value(year, "redacted"), "not withheld");
        Check.equal(" TIMESTAMP '2001-12-01 00:00:00'", variables.value(year, "expr_text"), "value exactly as written, leading space kept");
        String[] region = TestSupport.row(variables, "name", "REGION");
        Check.equal("", variables.value(region, "is_session"), "a repository variable: the flag is absent");
        Check.equal("", variables.value(region, "init_block_id"), "an init block that is not found leaves the cell empty");
        Check.equal("=1+1", variables.value(region, "expr_text"), "a value starting with '=' is unchanged");
        Check.equal("false", variables.value(TestSupport.row(variables, "name", "Passed"), "redacted"), "'Passed' is not a telling name");
        Check.equal("vars::var::REGION\n", variables.value(TestSupport.row(variables, "name", "Uses Another"), "expr_refs"),
                "references one per line; the one that does not resolve leaves an empty line");

        CsvTable errors = TestSupport.table(kb, "errors");
        Check.equal(3, errors.rows.size(), "three errors: " + TestSupport.column(errors, "source_id"));
        Check.equal("missing_name", errors.value(TestSupport.row(errors, "source_id", "miu"), "kind"), "an init block with no name");
        Check.equal("missing_name", errors.value(TestSupport.row(errors, "source_id", "mvu"), "kind"), "a variable with no name");
        Check.equal("ExpressionRef", errors.value(TestSupport.row(errors, "source_id", "vars::var::Uses Another"), "object_type"), "the reference that does not resolve");

        CsvTable unparsed = TestSupport.table(kb, "unparsed");
        Check.equal(0, TestSupport.rows(unparsed, "kind", "redacted_value").size(), "nothing was withheld");
        Check.equal(1, TestSupport.rows(unparsed, "detail", "Value#text").size(), "the per-database query is not read, and says so");
        Check.equal(1, TestSupport.rows(unparsed, "detail", "InitBlock@connectionPoolRef").size(), "nor is the connection pool");
        Check.equal(List.of(), SchemaTest.valueProblems(kb), "every declared link holds");
    }

    public void testTheNameRuleIsTheReferenceImplementations() {
        for (String name : new String[] {"REP OLAP PASSWORD", "db secret", "PASSWORD", "db_pwd", "API Key", "api_key", "api-key", "ApiKey",
                "auth token", "client secret", "Passwd", "my credentials"}) {
            Check.that(Variable.isCredentialName(name), name + " is a telling name");
        }
        for (String name : new String[] {"CURRENT_YEAR", "Passed", "REGION", "REP OLAP USER", "api  key", ""}) {
            Check.that(!Variable.isCredentialName(name), name + " is not a telling name");
        }
        Check.that(!Variable.isCredentialName(null), "no name");
    }

    /** Writes an estate with planted values under telling names, and one of them again under an innocent name. */
    private static Path planted(Path work) {
        Path estate = work.resolve("creds");
        TestSupport.object(estate, "Variable", "a.xml", variable("m1", "internal - REP OLAP PASSWORD", " isSessionVar=\"true\" securityMask=\"5\"", PLANTED_ONE, PLANTED_RENDERED));
        TestSupport.object(estate, "Variable", "b.xml", variable("m2", "API Key", "", PLANTED_TWO, PLANTED_TWO));
        // Read before the variable whose name gives its value away: reading order must not matter.
        TestSupport.object(estate, "Variable", "0-user.xml", variable("m3", "REP OLAP USER", "", " " + PLANTED_ONE + "\n", "kept"));
        TestSupport.object(estate, "Variable", "c.xml", variable("m4", "CURRENT_YEAR", "", "2020", "2020"));
        TestSupport.object(estate, "Variable", "d.xml", "<Variable mdsid=\"m5\" name=\"db_pwd\"" + NS + "><Expr><ExprText>" + PLANTED_RENDERED + "%1</ExprText>"
                + "<ObjectRefList><RefObject objectRef=\"" + BASE + "Variable/gone.xml#nothing\"/></ObjectRefList></Expr></Variable>");
        TestSupport.object(estate, "Variable", "e.xml", variable("m6", "empty token", "", "", ""));
        TestSupport.object(estate, "Variable", "f.xml", variable("m7", "No Value", "", "", ""));
        return estate;
    }

    public void testACredentialIsWithheldAndTheWithholdingIsRecorded() {
        Path work = TestSupport.work("var-redacted");
        Path kb = work.resolve("kb");
        TestSupport.Run run = TestSupport.extract(planted(work), kb);
        Check.equal(3, run.exit, "exit 3: one reference does not resolve");

        CsvTable variables = TestSupport.table(kb, "variables");
        Check.equal(7, variables.rows.size(), "every variable is still a row");
        for (String name : new String[] {"internal - REP OLAP PASSWORD", "API Key", "db_pwd", "empty token", "REP OLAP USER"}) {
            String[] row = TestSupport.row(variables, "name", name);
            Check.equal("true", variables.value(row, "redacted"), name + ": redacted");
            Check.that(variables.value(row, "expr_text").isEmpty() && variables.value(row, "expr_text_desc").isEmpty()
                    && variables.value(row, "expr_refs").isEmpty(), name + ": the three value cells are empty");
        }
        String[] password = TestSupport.row(variables, "name", "internal - REP OLAP PASSWORD");
        Check.equal("true", variables.value(password, "is_session"), "the rest of the row is kept");
        Check.equal("5", variables.value(password, "security_mask"), "the rest of the row is kept");
        String[] year = TestSupport.row(variables, "name", "CURRENT_YEAR");
        Check.equal("false", variables.value(year, "redacted"), "an ordinary variable is not touched");
        Check.equal("2020", variables.value(year, "expr_text"), "and keeps its value");
        Check.equal("false", variables.value(TestSupport.row(variables, "name", "No Value"), "redacted"),
                "an empty value under a telling name does not make every empty value a credential");

        CsvTable unparsed = TestSupport.table(kb, "unparsed");
        List<String[]> gaps = TestSupport.rows(unparsed, "kind", "redacted_value");
        Check.equal(5, gaps.size(), "one known-gap row per withheld value");
        String[] named = TestSupport.row(unparsed, "detail", "API Key — value withheld: the name indicates a credential (NFR11)");
        Check.equal("Variable", unparsed.value(named, "object_type"), "object type");
        Check.equal("1", unparsed.value(named, "occurrences"), "occurrences");
        Check.equal(1, TestSupport.rows(unparsed, "detail",
                "REP OLAP USER — value withheld: it is the same as the value of a variable whose name indicates a credential").size(),
                "difference 19: the same value under an innocent name is withheld too, and the row says why");
        Check.equal(0, TestSupport.rows(unparsed, "detail", "Expr#text").size(), "a withheld value is not also reported as unread");
        Check.equal(0, TestSupport.rows(unparsed, "detail", "ExprText#text").size(), "a withheld value is not also reported as unread");

        CsvTable errors = TestSupport.table(kb, "errors");
        Check.equal(List.of("creds::var::db_pwd"), TestSupport.column(errors, "source_id"),
                "a reference inside a withheld value that does not resolve is still reported");
    }

    public void testNoWithheldValueReachesAnyOutputFileOrTheConsole() throws IOException {
        Path work = TestSupport.work("var-no-leak");
        Path kb = work.resolve("kb");
        TestSupport.Run run = TestSupport.extract(planted(work), kb);
        List<String> values = List.of(PLANTED_ONE, PLANTED_TWO, PLANTED_RENDERED);
        Check.equal(List.of(), filesHolding(kb, values), "output files holding a planted value");
        Check.that(!holds(run.out, values) && !holds(run.err, values), "nothing printed holds a planted value");

        // The check itself must be able to fail: plant a value in an output file and look again.
        Path spoiled = kb.resolve("init_blocks.csv");
        Files.write(spoiled, (new String(Files.readAllBytes(spoiled), StandardCharsets.UTF_8) + "\"" + PLANTED_TWO.replace("\"", "\"\"") + "\"\n")
                .getBytes(StandardCharsets.UTF_8));
        Check.equal(List.of("init_blocks.csv"), filesHolding(kb, values), "a planted leak is found, in its CSV form too");
    }

    /**
     * The two sample repositories: every value the source gives to a variable with a telling name, read here
     * with the JDK's parser, is in no output file. This is the test that found the user-name variable
     * holding the password variable's value (deliberate difference 19).
     */
    public void testNoWithheldValueOfTheSampleRepositoriesReachesAnyOutputFile() throws Exception {
        int checked = 0;
        for (String estateName : new String[] {"sampleapp", "bi-analytics-2013"}) {
            Path estate = TestSupport.root().resolve("estates").resolve(estateName);
            Path variableFolder = estate.resolve("rpd/oracle/bi/server/base/Variable");
            if (!Files.isDirectory(variableFolder)) {
                continue;
            }
            List<String> values = new ArrayList<>();
            int telling = 0;
            List<Path> files;
            try (Stream<Path> list = Files.list(variableFolder)) {
                files = list.filter(p -> p.toString().endsWith(".xml")).sorted().collect(Collectors.toList());
            }
            for (Path file : files) {
                List<String> found = valuesUnderATellingName(file);
                if (found != null) {
                    telling++;
                    values.addAll(found);
                }
            }
            Path kb = TestSupport.work("var-no-leak-" + estateName).resolve("kb");
            TestSupport.Run run = TestSupport.extract(estate, kb);
            Check.that(run.exit == 0 || run.exit == 3, estateName + ": extract ran");
            Check.equal(List.of(), filesHolding(kb, values), estateName + ": output files holding a value of a variable with a telling name");
            Check.that(!holds(run.out, values) && !holds(run.err, values), estateName + ": nothing printed holds one");

            CsvTable variables = TestSupport.table(kb, "variables");
            int redacted = TestSupport.rows(variables, "redacted", "true").size();
            Check.that(redacted >= telling, estateName + ": every variable with a telling name is marked redacted (" + redacted + " of " + telling + ")");
            Check.equal(redacted, TestSupport.rows(TestSupport.table(kb, "unparsed"), "kind", "redacted_value").size(),
                    estateName + ": one known-gap row per redacted variable");
            for (String[] row : TestSupport.rows(variables, "redacted", "true")) {
                Check.that(variables.value(row, "expr_text").isEmpty() && variables.value(row, "expr_text_desc").isEmpty()
                        && variables.value(row, "expr_refs").isEmpty(), estateName + ": a redacted row's value cells are empty: " + variables.value(row, "name"));
            }
            checked++;
        }
        if (checked == 0) {
            throw new SkipException("the sample estates are not under estates/");
        }
    }

    /** The non-blank value texts of a variable file whose variable has a telling name; null when it has not. */
    private static List<String> valuesUnderATellingName(Path file) throws Exception {
        XMLInputFactory factory = XMLInputFactory.newInstance();
        factory.setProperty(XMLInputFactory.SUPPORT_DTD, false);
        factory.setProperty(XMLInputFactory.IS_COALESCING, true);
        List<String> values = new ArrayList<>();
        try (InputStream in = Files.newInputStream(file)) {
            XMLStreamReader xml = factory.createXMLStreamReader(in);
            boolean first = true;
            while (xml.hasNext()) {
                if (xml.next() != XMLStreamConstants.START_ELEMENT) {
                    continue;
                }
                if (first) {
                    first = false;
                    String name = xml.getAttributeValue(null, "name");
                    if (name == null || !CREDENTIAL_NAME.matcher(name).find()) {
                        return null;
                    }
                } else if (xml.getLocalName().equals("ExprText") || xml.getLocalName().equals("ExprTextDesc")) {
                    String value = xml.getElementText().strip();
                    if (!value.isEmpty()) {
                        values.add(value);
                    }
                }
            }
            xml.close();
        }
        return values;
    }

    /** The names of the files in a folder that hold any of the values, as written or as a CSV field. */
    private static List<String> filesHolding(Path folder, List<String> values) throws IOException {
        List<String> holding = new ArrayList<>();
        for (String name : TestSupport.fileNames(folder)) {
            if (holds(new String(Files.readAllBytes(folder.resolve(name)), StandardCharsets.UTF_8), values)) {
                holding.add(name);
            }
        }
        return holding;
    }

    private static boolean holds(String text, List<String> values) {
        for (String value : values) {
            if (text.contains(value) || text.contains(value.replace("\"", "\"\""))) {
                return true;
            }
        }
        return false;
    }

    public void testSecurityRowsOfTheThreeKinds() {
        Path work = TestSupport.work("sec-rows");
        Path estate = work.resolve("sec");
        String model = " subjectAreaRef=\"" + BASE + "BusinessModel/bm.xml#mbm\"";
        TestSupport.object(estate, "BusinessModel", "bm.xml", "<BusinessModel mdsid=\"mbm\" name=\"BM\"" + NS + "/>");
        TestSupport.object(estate, "LogicalTable", "lt.xml", "<LogicalTable mdsid=\"mlt\" name=\"Fact\"" + NS + model + "><LogicalColumn mdsid=\"mlc\" name=\"Region\"/></LogicalTable>");
        String filter = "Position(';Role A;' in ';' || valueof(NQ_SESSION.ROLEGUIDS) || ';') > 0 and \"%1\" = 'EMEA'";
        TestSupport.object(estate, "LogicalTableSource", "s1.xml", "<LogicalTableSource mdsid=\"ms1\" name=\"Filtered\"" + NS
                + " logicalTableRef=\"" + BASE + "LogicalTable/lt.xml#mlt\"><WhereClause><ExprText><![CDATA[" + filter + "]]></ExprText>"
                + "<ExprTextDesc><![CDATA[rendered]]></ExprTextDesc><ObjectRefList><RefObject objectRef=\"" + BASE + "LogicalTable/lt.xml#mlc\"/></ObjectRefList></WhereClause></LogicalTableSource>");
        TestSupport.object(estate, "LogicalTableSource", "s2.xml", "<LogicalTableSource mdsid=\"ms2\" name=\"Open\"" + NS
                + " logicalTableRef=\"" + BASE + "LogicalTable/lt.xml#mlt\"><WhereClause><ExprText>  </ExprText></WhereClause></LogicalTableSource>");
        TestSupport.object(estate, "PresentationCatalog", "sa.xml", "<PresentationCatalog mdsid=\"msa\" name=\"Area\"" + NS + "/>");

        TestSupport.object(estate, "Group", "g.xml", "<Group mdsid=\"mg\" name=\"Role A\" displayName=\"Role A shown\" guid=\"RoleA\"" + NS + "/>");
        TestSupport.object(estate, "Group", "idle.xml", "<Group mdsid=\"mgi\" name=\"Role Idle\" displayName=\"Idle shown\"" + NS + "/>");
        TestSupport.object(estate, "Group", "unnamed.xml", "<Group mdsid=\"mgu\"" + NS + "/>");
        TestSupport.object(estate, "QueryPrivilege", "qp.xml", "<QueryPrivilege mdsid=\"mqp\" name=\"QueryPriv_1\"" + NS + "/>");
        // The object privilege's file is read before the package it names; reading order must not matter.
        TestSupport.object(estate, "PrivilegePackage", "p.xml", "<PrivilegePackage mdsid=\"mp\" name=\"PrivPack_1\"" + NS
                + " roleRef=\"" + BASE + "Group/g.xml#mg\">"
                + "<QueryPrivilegeMapping queryPrivilegeRef=\"" + BASE + "QueryPrivilege/qp.xml#mqp\" databaseRef=\"x\"/>"
                + "<QueryPrivilegeMapping queryPrivilegeRef=\"" + BASE + "QueryPrivilege/gone.xml#nothing\"/></PrivilegePackage>");
        TestSupport.object(estate, "PrivilegePackage", "user.xml", "<PrivilegePackage mdsid=\"mpu\" name=\"PrivPack_User\" userRef=\"x\"" + NS + "/>");
        TestSupport.object(estate, "PrivilegePackage", "unnamed.xml", "<PrivilegePackage mdsid=\"mpn\"" + NS
                + " roleRef=\"" + BASE + "Group/gone.xml#nothing\"><QueryPrivilegeMapping queryPrivilegeRef=\"" + BASE + "QueryPrivilege/qp.xml#mqp\"/></PrivilegePackage>");
        TestSupport.object(estate, "ObjectPrivilege", "o.xml", "<ObjectPrivilege mdsid=\"mo\" name=\"ObjPriv_1\" type=\"read\"" + NS
                + " privilegePackageRef=\"" + BASE + "PrivilegePackage/p.xml#mp\"><RefObjects>"
                + "<RefObject objectRef=\"" + BASE + "PresentationCatalog/sa.xml#msa\"/>"
                + "<RefObject objectRef=\"" + BASE + "PresentationCatalog/gone.xml#nothing\"/>"
                + "<RefObject objectRef=\"" + BASE + "LogicalTable/lt.xml#mlc\"/></RefObjects></ObjectPrivilege>");
        TestSupport.object(estate, "ObjectPrivilege", "bare.xml", "<ObjectPrivilege mdsid=\"mob\" type=\"none\"" + NS
                + " privilegePackageRef=\"" + BASE + "PrivilegePackage/gone.xml#nothing\"/>");

        Path kb = work.resolve("kb");
        TestSupport.Run run = TestSupport.extract(estate, kb);
        Check.equal(0, run.exit, "exit 0: none of this is an error. " + run.err);

        CsvTable rules = TestSupport.table(kb, "security_rls");
        Check.equal(List.of("row_id", "rpd_id", "kind", "role", "subject_id", "detail", "expr_text", "expr_text_desc", "expr_refs",
                "role_display_name", "role_guid"), rules.header,
                "columns as in the reference, without the guard columns, and two more for difference 27");
        Check.equal(List.of("sec::bmm::BM.Fact.src.Filtered#filter", "sec::sec::obj.?.o0", "sec::sec::obj.ObjPriv_1.o0", "sec::sec::obj.ObjPriv_1.o1",
                "sec::sec::obj.ObjPriv_1.o2", "sec::sec::pkg.?.q0", "sec::sec::pkg.PrivPack_1.q0", "sec::sec::pkg.PrivPack_1.q1"),
                TestSupport.column(rules, "row_id"), "one row per query privilege, per object, and per source with a filter");

        String[] query = TestSupport.row(rules, "row_id", "sec::sec::pkg.PrivPack_1.q0");
        Check.equal("query_privilege", rules.value(query, "kind"), "kind");
        Check.equal("Role A", rules.value(query, "role"), "the role, by its name");
        Check.equal("Role A shown", rules.value(query, "role_display_name"), "difference 27: what the role's own object shows it as");
        Check.equal("RoleA", rules.value(query, "role_guid"), "difference 27: and its guid");
        Check.equal("Role A shown", rules.value(TestSupport.row(rules, "row_id", "sec::sec::pkg.PrivPack_1.q1"), "role_display_name"),
                "on every query privilege row of the package");
        Check.equal("QueryPriv_1", rules.value(query, "subject_id"), "the query privilege, by its name, though its type has no reader");
        Check.equal("PrivPack_1", rules.value(query, "detail"), "the package's name");
        Check.equal("", rules.value(query, "expr_text"), "no expression");
        Check.equal("", rules.value(TestSupport.row(rules, "row_id", "sec::sec::pkg.PrivPack_1.q1"), "subject_id"), "a query privilege that is not found");
        String[] nameless = TestSupport.row(rules, "row_id", "sec::sec::pkg.?.q0");
        Check.equal("", rules.value(nameless, "role"), "a role that is not found leaves the cell empty");
        Check.equal("", rules.value(nameless, "role_display_name") + rules.value(nameless, "role_guid"), "and so do its display name and guid");
        Check.equal("", rules.value(nameless, "detail"), "a package with no name: '?' in the identifier, empty here");

        String[] object = TestSupport.row(rules, "row_id", "sec::sec::obj.ObjPriv_1.o0");
        Check.equal("object_privilege", rules.value(object, "kind"), "kind");
        Check.equal("", rules.value(object, "role"), "the role is not on an object privilege row");
        Check.equal("", rules.value(object, "role_display_name") + rules.value(object, "role_guid"), "nor its display name or guid");
        Check.equal("sec::pres::Area", rules.value(object, "subject_id"), "the object, as a Row identifier");
        Check.equal("read via sec::sec::pkg.PrivPack_1", rules.value(object, "detail"), "the permission and the package, though the package is read later");
        Check.equal("", rules.value(TestSupport.row(rules, "row_id", "sec::sec::obj.ObjPriv_1.o1"), "subject_id"), "an object that is not found; its place is kept");
        Check.equal("sec::bmm::BM.Fact.Region", rules.value(TestSupport.row(rules, "row_id", "sec::sec::obj.ObjPriv_1.o2"), "subject_id"),
                "an object of another kind: the column is polymorphic");
        String[] bare = TestSupport.row(rules, "row_id", "sec::sec::obj.?.o0");
        Check.equal("", rules.value(bare, "subject_id"), "a privilege that lists no object still has one row");
        Check.equal("none via ", rules.value(bare, "detail"), "a package that is not found");

        String[] rowFilter = TestSupport.row(rules, "row_id", "sec::bmm::BM.Fact.src.Filtered#filter");
        Check.equal("row_filter", rules.value(rowFilter, "kind"), "kind");
        Check.equal("", rules.value(rowFilter, "role"), "the role is not worked out from the filter text");
        Check.equal("sec::bmm::BM.Fact", rules.value(rowFilter, "subject_id"), "the logical table");
        Check.equal("Filtered", rules.value(rowFilter, "detail"), "the table source's name");
        Check.equal(filter, rules.value(rowFilter, "expr_text"), "the filter exactly as written");
        Check.equal("rendered", rules.value(rowFilter, "expr_text_desc"), "the rendered form");
        Check.equal("sec::bmm::BM.Fact.Region", rules.value(rowFilter, "expr_refs"), "what it refers to");
        Check.equal(filter, TestSupport.table(kb, "logical_table_sources").value(
                TestSupport.row(TestSupport.table(kb, "logical_table_sources"), "name", "Filtered"), "where_text"), "the same filter is on the table source");

        CsvTable coverage = TestSupport.table(kb, "coverage");
        Check.equal("2", coverage.value(TestSupport.row(coverage, "object_type", "Group"), "parsed"), "the two named roles are counted; the unnamed one is passed over");
        Check.equal("0", coverage.value(TestSupport.row(coverage, "object_type", "Group"), "skipped"), "without an error");
        Check.equal("3", coverage.value(TestSupport.row(coverage, "object_type", "PrivilegePackage"), "parsed"), "three packages, one of them with no row");
        Check.equal("2", coverage.value(TestSupport.row(coverage, "object_type", "ObjectPrivilege"), "parsed"), "two object privileges");
        Check.equal("1", coverage.value(TestSupport.row(coverage, "object_type", "QueryPrivilege"), "skipped"), "the query privilege's type has no reader");

        CsvTable unparsed = TestSupport.table(kb, "unparsed");
        Check.equal(1, TestSupport.rows(unparsed, "detail", "PrivilegePackage@userRef").size(), "a grant to a user is a known gap");
        Check.equal(1, TestSupport.rows(unparsed, "detail", "QueryPrivilegeMapping@databaseRef").size(), "so is the database of a query privilege");
        Check.equal("1", unparsed.value(TestSupport.row(unparsed, "detail", "Group@displayName"), "occurrences"),
                "and the display name of the role no privilege package points at; the other role's is read (difference 27)");
        Check.equal(0, TestSupport.rows(unparsed, "detail", "Group@guid").size(), "as is its guid");
        Check.equal(1, TestSupport.rows(unparsed, "detail", "qp.xml").size(), "the query privilege file is reported whole");
    }

    /** A formula may name a role or an init block; both have identifiers now, whatever the reading order. */
    public void testARoleAndAnInitBlockCanBeReferredTo() {
        Path work = TestSupport.work("sec-referred");
        Path estate = work.resolve("refd");
        TestSupport.object(estate, "BusinessModel", "bm.xml", "<BusinessModel mdsid=\"mbm\" name=\"BM\"" + NS + "/>");
        TestSupport.object(estate, "LogicalTable", "lt.xml", "<LogicalTable mdsid=\"mlt\" name=\"T\"" + NS
                + " subjectAreaRef=\"" + BASE + "BusinessModel/bm.xml#mbm\"><LogicalColumn mdsid=\"mlc\" name=\"C\"><Expr><ExprText>%1 %2 %3</ExprText><ObjectRefList>"
                + "<RefObject objectRef=\"" + BASE + "Group/g.xml#mg\"/><RefObject objectRef=\"" + BASE + "InitBlock/ib.xml#mib\"/>"
                + "<RefObject objectRef=\"" + BASE + "LogicalComplexJoin/j.xml#mj\"/></ObjectRefList></Expr></LogicalColumn></LogicalTable>");
        TestSupport.object(estate, "Group", "g.xml", "<Group mdsid=\"mg\" name=\"Role A\"" + NS + "/>");
        TestSupport.object(estate, "InitBlock", "ib.xml", "<InitBlock mdsid=\"mib\" name=\"IB\"" + NS + "/>");
        TestSupport.object(estate, "LogicalComplexJoin", "j.xml", "<LogicalComplexJoin mdsid=\"mj\" name=\"J\"" + NS + "/>");
        Path kb = work.resolve("kb");
        Check.equal(3, TestSupport.extract(estate, kb).exit, "exit 3: the join cannot be referred to");
        CsvTable columns = TestSupport.table(kb, "logical_columns");
        Check.equal("refd::sec::Role A\nrefd::var::init.IB\n", columns.value(columns.rows.get(0), "derived_expr_refs"),
                "the role and the init block resolve; a join has no identifier that can be referred to, as in the reference implementation");
        Check.equal(1, TestSupport.table(kb, "errors").rows.size(), "one error, for the join");
    }
}
