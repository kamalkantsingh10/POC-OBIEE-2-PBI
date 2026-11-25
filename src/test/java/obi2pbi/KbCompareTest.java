package obi2pbi;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.Map;
import java.util.Set;

/** KbCompare itself: it must pass what matches and report each kind of difference. */
public final class KbCompareTest {

    private static final String REFERENCE_HEADER = "\"row_id\",\"rpd_id\",\"name\",\"name_guarded\",\"size\"\n";

    private static Path[] folders(String work, String reference, String generated) {
        Path folder = TestSupport.work(work);
        TestSupport.write(folder.resolve("ref/things.csv"), reference);
        TestSupport.write(folder.resolve("kb/things.csv"), generated);
        TestSupport.write(folder.resolve("kb/manifest.csv"), "\"key\",\"value\"\n\"tables\",\"things schema\"\n");
        return new Path[] {folder.resolve("kb"), folder.resolve("ref")};
    }

    public void testGuardedColumnsAndOtherEstatesAreIgnored() throws Exception {
        Path[] f = folders("compare-same",
                REFERENCE_HEADER
                        + "\"e::phys::a\",\"e\",\"'-minus\",\"true\",\"1\"\n"
                        + "\"e::phys::b\",\"e\",\"'quoted\",\"false\",\"2\"\n"
                        + "\"other::phys::z\",\"other\",\"z\",\"false\",\"9\"\n",
                "\"row_id\",\"rpd_id\",\"name\",\"size\"\n"
                        + "\"e::phys::a\",\"e\",\"-minus\",\"1\"\n"
                        + "\"e::phys::b\",\"e\",\"'quoted\",\"2\"\n");
        KbCompare.Result result = KbCompare.compare(f[0], f[1], "e", "things", Map.of());
        Check.that(result.same(), "no differences: " + result.summary());
        Check.equal(2, result.referenceRows, "reference rows for the estate only");
        Check.equal(2, result.generatedRows, "generated rows");
    }

    public void testMissingExtraAndDifferingAreEachReported() throws Exception {
        Path[] f = folders("compare-different",
                REFERENCE_HEADER
                        + "\"e::phys::a\",\"e\",\"a\",\"false\",\"1\"\n"
                        + "\"e::phys::b\",\"e\",\"b\",\"false\",\"2\"\n"
                        + "\"e::phys::dup\",\"e\",\"d\",\"false\",\"1\"\n"
                        + "\"e::phys::dup\",\"e\",\"d\",\"false\",\"2\"\n",
                "\"row_id\",\"rpd_id\",\"name\",\"size\",\"added\"\n"
                        + "\"e::phys::a\",\"e\",\"a\",\"7\",\"x\"\n"
                        + "\"e::phys::c\",\"e\",\"c\",\"3\",\"x\"\n"
                        + "\"e::phys::dup\",\"e\",\"d\",\"2\",\"x\"\n"
                        + "\"e::phys::dup\",\"e\",\"d\",\"5\",\"x\"\n");
        KbCompare.Result result = KbCompare.compare(f[0], f[1], "e", "things", Map.of());
        Check.that(!result.same(), "differences found");
        Check.equal(1, result.differing.size(), "one differing cell: " + result.differing);
        Check.contains(result.differing.get(0), "[size] reference \"1\" generated \"7\"", "names the column and both values");
        Check.equal(2, result.missing.size(), "row b, and one of the duplicated rows: " + result.missing);
        Check.equal(2, result.extra.size(), "row c, and one of the duplicated rows: " + result.extra);
        Check.equal(java.util.List.of("added"), result.columnsOnlyInGenerated, "a column only the generated table has");

        KbCompare.Result limited = KbCompare.compare(f[0], f[1], "e", "things", Map.of("name", Set.of("b", "c")));
        Check.equal(1, limited.missing.size(), "--where limits both sides");
        Check.equal(1, limited.extra.size(), "--where limits both sides");
        Check.equal(0, limited.differing.size(), "row a is left out");
    }

    public void testWholeFolderRunSkipsSchemaAndSetsTheExitCode() throws Exception {
        Path[] f = folders("compare-run", REFERENCE_HEADER + "\"e::phys::a\",\"e\",\"a\",\"false\",\"1\"\n",
                "\"row_id\",\"rpd_id\",\"name\",\"size\"\n\"e::phys::a\",\"e\",\"a\",\"1\"\n");
        TestSupport.write(f[1].resolve("later.csv"), "\"row_id\",\"rpd_id\"\n\"e::bmm::x\",\"e\"\n");
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int exit = KbCompare.run(new String[] {"e", "--kb", f[0].toString(), "--ref", f[1].toString()}, new PrintStream(out));
        String text = out.toString();
        Check.equal(0, exit, "exit 0 when nothing differs: " + text);
        Check.contains(text, "schema                       skipped", "the schema tables are not compared");
        Check.contains(text, "not generated yet, reference rows for e: later (1)", "reference tables not yet written are listed");

        TestSupport.write(f[0].resolve("things.csv"), "\"row_id\",\"rpd_id\",\"name\",\"size\"\n");
        Check.equal(1, KbCompare.run(new String[] {"e", "--kb", f[0].toString(), "--ref", f[1].toString()},
                new PrintStream(new ByteArrayOutputStream())), "exit 1 when something differs");
    }

    // ------------------------------------------------------------------ deliberate differences 5 and 6

    private static final String REFS_REFERENCE_HEADER = "\"row_id\",\"rpd_id\",\"expr_refs\",\"expr_refs_guarded\"\n";
    private static final String REFS_GENERATED_HEADER = "\"row_id\",\"rpd_id\",\"expr_refs\"\n";
    private static final String ERRORS_HEADER = "\"rpd_id\",\"kind\",\"source_id\",\"object_type\",\"reason\"\n";

    private static String unresolved(String sourceId) {
        return "\"e\",\"unresolved_reference\",\"" + sourceId + "\",\"ExpressionRef\",\"reference to X could not be resolved\"\n";
    }

    public void testAReferenceCellIsSplitWhereAnIdentifierStarts() {
        Check.equal(java.util.List.of("e::bmm::M.A B.C D", "e::phys::S.T.X"), KbCompare.referenceList("e::bmm::M.A B.C D e::phys::S.T.X", "e"),
                "names with spaces stay whole");
        Check.equal(java.util.List.of("e::bmm::M.Ends with space ", "e::var::V"), KbCompare.referenceList("e::bmm::M.Ends with space  e::var::V", "e"),
                "a name ending in a space keeps it");
        Check.equal(java.util.List.of("e::bmm::M.mentions e:: and bmm", "e::bmm::M.B"), KbCompare.referenceList("e::bmm::M.mentions e:: and bmm e::bmm::M.B", "e"),
                "only the whole prefix <estate>::<layer>:: starts an identifier");
        Check.equal(java.util.List.of(), KbCompare.referenceList("", "e"), "an empty cell is an empty list");
        Check.equal(null, KbCompare.referenceList("something else", "e"), "not a list of identifiers");
        Check.equal(null, KbCompare.referenceList("e::bmm::M.A,e::bmm::M.B", "e"), "identifiers not separated by a space");
        Check.equal(java.util.List.of("e::bmm::A", "e::bmm::B"), KbCompare.generatedList("e::bmm::A\n\ne::bmm::B\n"), "generated: lines, the empty ones left out");
    }

    public void testTheNewListFormIsExplainedByDifferenceSixAndOnlyWhenTheListsAreEqual() throws Exception {
        Path[] f = folders("compare-refs", "x", "x");
        TestSupport.write(f[1].resolve("things.csv"), REFS_REFERENCE_HEADER
                + "\"e::bmm::same\",\"e\",\"e::bmm::M.One Two\",\"false\"\n"
                + "\"e::bmm::list\",\"e\",\"e::bmm::M.One Two e::phys::S.T.C e::bmm::M.Three\",\"false\"\n"
                + "\"e::bmm::gap\",\"e\",\"e::bmm::M.A e::bmm::M.B\",\"false\"\n"
                + "\"e::bmm::order\",\"e\",\"e::bmm::M.A e::bmm::M.B\",\"false\"\n"
                + "\"e::bmm::split\",\"e\",\"e::bmm::M.A B e::bmm::M.C\",\"false\"\n"
                + "\"e::bmm::lost\",\"e\",\"e::bmm::M.A e::bmm::M.B\",\"false\"\n"
                + "\"e::bmm::other\",\"e\",\"e::bmm::M.A e::bmm::M.B\",\"false\"\n");
        TestSupport.write(f[0].resolve("things.csv"), REFS_GENERATED_HEADER
                + "\"e::bmm::same\",\"e\",\"e::bmm::M.One Two\"\n"
                + "\"e::bmm::list\",\"e\",\"e::bmm::M.One Two\ne::phys::S.T.C\ne::bmm::M.Three\"\n"
                + "\"e::bmm::gap\",\"e\",\"e::bmm::M.A\n\ne::bmm::M.B\"\n"
                + "\"e::bmm::order\",\"e\",\"e::bmm::M.B\ne::bmm::M.A\"\n"
                + "\"e::bmm::split\",\"e\",\"e::bmm::M.A\nB e::bmm::M.C\"\n"
                + "\"e::bmm::lost\",\"e\",\"e::bmm::M.A\"\n"
                + "\"e::bmm::other\",\"e\",\"e::bmm::M.A\nother::bmm::M.B\"\n");
        KbCompare.Result result = KbCompare.compare(f[0], f[1], "e", "things", Map.of());
        Check.equal(java.util.Set.of(6), result.explained.keySet(), "only difference 6 explains anything here");
        Check.equal(2, result.explained.get(6).size(), "the list, and the list with an unresolved entry: " + result.explained);
        Check.contains(result.explained.get(6).get(1), "e::bmm::list", "the plain list");
        Check.contains(result.explained.get(6).get(0), "e::bmm::gap", "an empty line stands for the entry the reference left out");
        Check.equal(4, result.differing.size(), "a different order, a different cut, a lost entry and a foreign identifier stay unexplained: " + result.differing);
        Check.that(!result.nothingUnexplained(), "so the table is not clean");
        Check.contains(result.summary(), "differing cells     4  explained cells 2 (difference 6: 2)", "both counts are in the summary");

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int exit = KbCompare.run(new String[] {"e", "things", "--kb", f[0].toString(), "--ref", f[1].toString(), "--show-explained", "1"}, new PrintStream(out));
        Check.equal(1, exit, "exit 1 while anything is unexplained");
        Check.contains(out.toString(), "result: UNEXPLAINED DIFFERENCES found; also explained by deliberate differences: difference 6: 2", "the last line says both");
        Check.contains(out.toString(), "  differing: e::bmm::lost", "unexplained differences are listed");
        Check.contains(out.toString(), "  explained by difference 6: e::bmm::gap", "explained ones are listed on request");
    }

    public void testEntriesTheReferenceCouldNotResolveAreExplainedByDifferenceFiveWhenItsErrorsSaySo() throws Exception {
        Path[] f = folders("compare-forward", "x", "x");
        TestSupport.write(f[1].resolve("things.csv"), REFS_REFERENCE_HEADER
                + "\"e::bmm::M.T.told\",\"e\",\"e::bmm::M.A\",\"false\"\n"
                + "\"e::bmm::M.T.told#aggr0\",\"e\",\"\",\"false\"\n"
                + "\"e::bmm::M.T.two\",\"e\",\"e::bmm::M.A\",\"false\"\n"
                + "\"e::bmm::M.T.untold\",\"e\",\"e::bmm::M.A\",\"false\"\n");
        TestSupport.write(f[0].resolve("things.csv"), REFS_GENERATED_HEADER
                + "\"e::bmm::M.T.told\",\"e\",\"e::bmm::M.A\ne::bmm::M.Level One\"\n"
                + "\"e::bmm::M.T.told#aggr0\",\"e\",\"e::var::V\"\n"
                + "\"e::bmm::M.T.two\",\"e\",\"e::var::V\ne::bmm::M.A\ne::var::W\"\n"
                + "\"e::bmm::M.T.untold\",\"e\",\"e::bmm::M.A\ne::bmm::M.B\"\n");
        // The reference says: two references of "told" did not resolve, one of "two", none of "untold".
        TestSupport.write(f[1].resolve("errors.csv"), ERRORS_HEADER + unresolved("e::bmm::M.T.told") + unresolved("e::bmm::M.T.told")
                + unresolved("e::bmm::M.T.two") + unresolved("e::bmm::M.T.nowhere"));
        TestSupport.write(f[0].resolve("errors.csv"), ERRORS_HEADER);
        TestSupport.write(f[0].resolve("manifest.csv"), "\"key\",\"value\"\n\"tables\",\"things errors\"\n");

        KbCompare.Result result = KbCompare.compare(f[0], f[1], "e", "things", Map.of());
        Check.equal(2, result.explained.get(5).size(), "the row itself and the row made from it with '#': " + result.explained);
        Check.equal(2, result.differing.size(), "more entries gained than errors reported, and a gain no error reports: " + result.differing);
        Check.contains(String.join(" ", result.differing), "e::bmm::M.T.two", "two gained, one reported");
        Check.contains(String.join(" ", result.differing), "e::bmm::M.T.untold", "one gained, none reported");

        KbCompare.Result errors = KbCompare.compare(f[0], f[1], "e", "errors", Map.of());
        Check.equal(3, errors.explainedMissing.get(5).size(), "an error row is explained when the generated side resolves that reference: " + errors.explainedMissing);
        Check.equal(1, errors.missing.size(), "an error for an object that gained nothing stays unexplained: " + errors.missing);
        Check.contains(errors.missing.get(0), "e::bmm::M.T.nowhere", "and is named");
        Check.contains(errors.summary(), "missing     1  extra     0  differing cells     0  explained missing rows 3 (difference 5: 3)", "summary");
    }

    public void testExitCodeIsZeroWhenEveryDifferenceIsExplained() throws Exception {
        Path[] f = folders("compare-explained", "x", "x");
        TestSupport.write(f[1].resolve("things.csv"), REFS_REFERENCE_HEADER + "\"e::bmm::a\",\"e\",\"e::bmm::M.A e::bmm::M.B\",\"false\"\n");
        TestSupport.write(f[0].resolve("things.csv"), REFS_GENERATED_HEADER + "\"e::bmm::a\",\"e\",\"e::bmm::M.A\ne::bmm::M.B\"\n");
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int exit = KbCompare.run(new String[] {"e", "things", "--kb", f[0].toString(), "--ref", f[1].toString()}, new PrintStream(out));
        Check.equal(0, exit, "exit 0: " + out);
        Check.contains(out.toString(), "result: no unexplained differences in the tables compared; explained by deliberate differences: difference 6: 1",
                "the last line still says that something differs, and why");
        KbCompare.Result result = KbCompare.compare(f[0], f[1], "e", "things", Map.of());
        Check.that(!result.same() && result.nothingUnexplained(), "not the same, and nothing unexplained");
    }
    // ------------------------------------------------------------------ deliberate differences 26 and 27

    private static final String FAR_HEADER = "\"row_id\",\"rpd_id\",\"kind\",\"table2_id\"";

    /** An estate with a fact table whose foreign keys point at keys of a dimension table, and two roles. */
    private static Path farEstate(Path[] f) {
        Path estate = f[0].getParent().resolve("estate");
        String ns = " xmlns=\"" + TestSupport.XMLNS + "\"";
        TestSupport.object(estate, "Database", "db.xml", "<Database mdsid=\"mdb\" name=\"DB\"" + ns + "/>");
        TestSupport.object(estate, "Schema", "s.xml", "<Schema mdsid=\"ms\" name=\"S\"" + ns + " containerRef=\"" + BASE + "Database/db.xml#mdb\"/>");
        String inSchema = " containerRef=\"" + BASE + "Schema/s.xml#ms\"";
        String dim = BASE + "PhysicalTable/d.xml#";
        TestSupport.object(estate, "PhysicalTable", "f.xml", "<PhysicalTable mdsid=\"mf\" name=\"F\"" + ns + inSchema + ">"
                + "<PhysicalColumn mdsid=\"mf1\" name=\"K1\"/><PhysicalColumn mdsid=\"mf2\" name=\"K2\"/>"
                + "<PhysicalForeignKey mdsid=\"mfa\" name=\"one\" counterPartKeyRef=\"" + dim + "mdk\"/>"
                + "<PhysicalForeignKey mdsid=\"mfb\" name=\"again\" counterPartKeyRef=\"" + dim + "mdk\"/>"
                + "<PhysicalForeignKey mdsid=\"mfc\" name=\"dangling\" counterPartKeyRef=\"" + BASE + "PhysicalTable/gone.xml#nothing\"/>"
                + "<PhysicalForeignKey mdsid=\"mfd\" name=\"bare\"/></PhysicalTable>");
        TestSupport.object(estate, "PhysicalTable", "d.xml", "<PhysicalTable mdsid=\"md\" name=\"D\"" + ns + inSchema + ">"
                + "<PhysicalColumn mdsid=\"md1\" name=\"ID1\"/><PhysicalColumn mdsid=\"md2\" name=\"ID2\"/>"
                + "<PhysicalKey mdsid=\"mdk\" name=\"D_Key\"><RefColumns><RefColumn columnRef=\"" + dim + "md2\"/>"
                + "<RefColumn columnRef=\"" + dim + "md1\"/></RefColumns></PhysicalKey>"
                + "<PhysicalKey mdsid=\"mdu\" name=\"D_Unused\"><RefColumns><RefColumn columnRef=\"" + dim + "md1\"/></RefColumns></PhysicalKey>"
                + "</PhysicalTable>");
        TestSupport.object(estate, "Group", "g.xml", "<Group mdsid=\"mg\" name=\"R\" displayName=\"Role shown\" guid=\"RG\"" + ns + "/>");
        TestSupport.object(estate, "Group", "h.xml", "<Group mdsid=\"mh\" name=\"H\" displayName=\"Held by nobody\"" + ns + "/>");
        TestSupport.object(estate, "Group", "n.xml", "<Group mdsid=\"mn\" name=\"N\"" + ns + "/>");
        TestSupport.object(estate, "PrivilegePackage", "p.xml", "<PrivilegePackage mdsid=\"mp\" name=\"P\"" + ns + " roleRef=\"" + BASE
                + "Group/g.xml#mg\"><QueryPrivilegeMapping queryPrivilegeRef=\"x\"/></PrivilegePackage>");
        TestSupport.object(estate, "PrivilegePackage", "q.xml", "<PrivilegePackage mdsid=\"mq\" name=\"Q\"" + ns + " roleRef=\"" + BASE
                + "Group/n.xml#mn\"><QueryPrivilegeMapping queryPrivilegeRef=\"x\"/></PrivilegePackage>");
        TestSupport.object(estate, "PrivilegePackage", "e.xml", "<PrivilegePackage mdsid=\"me\" name=\"E\"" + ns + " roleRef=\"" + BASE
                + "Group/h.xml#mh\"/>");
        TestSupport.write(f[0].resolve("physical_tables.csv"), "\"row_id\",\"rpd_id\"\n\"e::phys::DB.S.F\",\"e\"\n\"e::phys::DB.S.D\",\"e\"\n");
        return estate;
    }

    private static String far(String name, String kind, String table2, String referenced) {
        return "\"e::phys::" + name + "\",\"e\",\"" + kind + "\",\"" + table2 + "\"" + (referenced == null ? "" : ",\"" + referenced + "\"") + "\n";
    }

    public void testAForeignKeysFarSideIsExplainedByDifferenceTwentySixOnlyWhenTheSourceAgrees() throws Exception {
        Path[] f = folders("compare-far", "x", "x");
        Path estate = farEstate(f);
        TestSupport.write(f[1].resolve("physical_joins.csv"), FAR_HEADER + "\n" + far("DB.S.F.one", "foreign_key", "", null)
                + far("DB.S.F.again", "foreign_key", "", null) + far("DB.S.F.dangling", "foreign_key", "", null)
                + far("DB.S.F.bare", "foreign_key", "", null) + far("joins.J", "complex_join", "e::phys::DB.S.D", null));
        String columns = "e::phys::DB.S.D.ID2\ne::phys::DB.S.D.ID1";
        String right = FAR_HEADER + ",\"referenced_columns\"\n" + far("DB.S.F.one", "foreign_key", "e::phys::DB.S.D", columns)
                + far("DB.S.F.again", "foreign_key", "e::phys::DB.S.D", columns) + far("DB.S.F.dangling", "foreign_key", "", "")
                + far("DB.S.F.bare", "foreign_key", "", "") + far("joins.J", "complex_join", "e::phys::DB.S.D", "");
        TestSupport.write(f[0].resolve("physical_joins.csv"), right);

        KbCompare.Result result = KbCompare.compare(f[0], f[1], estate, "e", "physical_joins", Map.of());
        Check.that(result.nothingUnexplained(), "the far table and the far columns are what the source says: " + result.summary());
        Check.equal(2, result.explained.get(26).size(), "two foreign keys gained their far table");
        Check.equal(java.util.List.of("referenced_columns"), result.explainedColumns.get(26), "the added column, every value checked");

        Check.that(!KbCompare.compare(f[0], f[1], "e", "physical_joins", Map.of()).nothingUnexplained(), "with no source to check against, nothing is explained");
        String[][] wrong = {
                {"e::phys::DB.S.D\",\"" + columns + "\"\n\"e::phys::DB.S.F.again", "e::phys::DB.S.F\",\"" + columns + "\"\n\"e::phys::DB.S.F.again", "the wrong far table"},
                {columns + "\"\n\"e::phys::DB.S.F.again", "e::phys::DB.S.D.ID1\ne::phys::DB.S.D.ID2\"\n\"e::phys::DB.S.F.again", "the far columns in another order"},
                {columns + "\"\n\"e::phys::DB.S.F.again", "e::phys::DB.S.D.ID2\"\n\"e::phys::DB.S.F.again", "a far column missing"},
                {"\"foreign_key\",\"\",\"\"\n\"e::phys::DB.S.F.bare", "\"foreign_key\",\"e::phys::DB.S.D\",\"\"\n\"e::phys::DB.S.F.bare", "a far table the source does not give"},
                {"\"complex_join\",\"e::phys::DB.S.D\",\"\"", "\"complex_join\",\"e::phys::DB.S.D\",\"e::phys::DB.S.D.ID1\"", "columns on a complex join"}};
        for (String[] change : wrong) {
            Check.that(right.contains(change[0]), "the test replaces something: " + change[2]);
            TestSupport.write(f[0].resolve("physical_joins.csv"), right.replace(change[0], change[1]));
            Check.that(!KbCompare.compare(f[0], f[1], estate, "e", "physical_joins", Map.of()).nothingUnexplained(), "not explained: " + change[2]);
        }
        // A far table that is not a row of the generated physical_tables cannot be vouched for.
        TestSupport.write(f[0].resolve("physical_joins.csv"), right);
        TestSupport.write(f[0].resolve("physical_tables.csv"), "\"row_id\",\"rpd_id\"\n\"e::phys::DB.S.F\",\"e\"\n");
        Check.equal(2, KbCompare.compare(f[0], f[1], estate, "e", "physical_joins", Map.of()).differing.size(),
                "a far table that is not a generated physical table is not explained");
    }

    /**
     * Differences 28 and 31: a table the reference does not have is a new table only when every row of it is
     * what the estate's own files hold, and the added columns only when every value is.
     */
    public void testAKeyTableAndTheAddedColumnColumnsAreExplainedOnlyWhenTheSourceAgrees() throws Exception {
        Path[] f = folders("compare-keys", "x", "x");
        Path estate = farEstate(f);
        KbCompare.Sides sides = new KbCompare.Sides(f[0], f[1], estate, "e");
        String header = "\"row_id\",\"rpd_id\",\"table_id\",\"name\",\"columns\"\n";
        String key = "\"e::phys::DB.S.D.key.D_Key\",\"e\",\"e::phys::DB.S.D\",\"D_Key\",\"e::phys::DB.S.D.ID2\ne::phys::DB.S.D.ID1\"\n";
        String unused = "\"e::phys::DB.S.D.key.D_Unused\",\"e\",\"e::phys::DB.S.D\",\"D_Unused\",\"e::phys::DB.S.D.ID1\"\n";
        TestSupport.write(f[0].resolve("physical_keys.csv"), header + key + unused);
        Check.equal(28, KbCompare.explainsNewTable(sides, "physical_keys"), "both keys of the files, cell for cell: a new table of difference 28");
        Check.equal(0, KbCompare.explainsNewTable(new KbCompare.Sides(f[0], f[1], null, "e"), "physical_keys"),
                "with no source to check against, nothing is explained");
        Check.equal(0, KbCompare.explainsNewTable(sides, "things"), "a table no deliberate difference adds is never a new table");
        String[][] wrong = {
                {header + key, "a key of the files is missing"},
                {header + key + unused + unused.replace("D_Unused", "D_More"), "a key the files do not hold"},
                {header + key.replace("ID2\ne::phys::DB.S.D.ID1", "ID1\ne::phys::DB.S.D.ID2") + unused, "the columns in another order"},
                {header + key.replace("\"e::phys::DB.S.D\",\"D_Key\"", "\"e::phys::DB.S.F\",\"D_Key\"") + unused, "the wrong table"},
                {header.replace("\"columns\"", "\"columns\",\"is_primary\"") + key.replace("ID1\"\n", "ID1\",\"true\"\n")
                        + unused.replace("ID1\"\n", "ID1\",\"\"\n"), "a column the difference does not add"}};
        for (String[] change : wrong) {
            TestSupport.write(f[0].resolve("physical_keys.csv"), change[0]);
            Check.equal(0, KbCompare.explainsNewTable(sides, "physical_keys"), "not explained: " + change[1]);
        }

        // Difference 31: in the files K1 is the first column of F and K2 the second; neither has a scale.
        TestSupport.write(f[1].resolve("physical_columns.csv"), "\"row_id\",\"rpd_id\"\n\"e::phys::DB.S.F.K1\",\"e\"\n\"e::phys::DB.S.F.K2\",\"e\"\n");
        String columns = "\"row_id\",\"rpd_id\",\"scale\",\"position\",\"source_column_id\"\n\"e::phys::DB.S.F.K1\",\"e\",\"\",\"1\",\"\"\n"
                + "\"e::phys::DB.S.F.K2\",\"e\",\"\",\"2\",\"\"\n";
        TestSupport.write(f[0].resolve("physical_columns.csv"), columns);
        KbCompare.Result result = KbCompare.compare(f[0], f[1], estate, "e", "physical_columns", Map.of());
        Check.that(result.nothingUnexplained(), "scale and position are what the files say: " + result.summary());
        Check.equal(java.util.List.of("scale", "position"), result.explainedColumns.get(31), "the two added columns, every value checked");
        Check.equal(java.util.List.of("source_column_id"), result.explainedColumns.get(37), "and the one of difference 37");
        TestSupport.write(f[0].resolve("physical_columns.csv"), columns.replace("\"\",\"2\",", "\"\",\"3\","));
        Check.that(!KbCompare.compare(f[0], f[1], estate, "e", "physical_columns", Map.of()).nothingUnexplained(), "a wrong position is not explained");
        TestSupport.write(f[0].resolve("physical_columns.csv"), columns.replace("\"\",\"1\",", "\"2\",\"1\","));
        Check.that(!KbCompare.compare(f[0], f[1], estate, "e", "physical_columns", Map.of()).nothingUnexplained(), "a scale the file does not give is not explained");

        // In unparsed: the files hold 2 keys, each with a name. The jar reads every one, so none may be left.
        String gaps = "\"rpd_id\",\"kind\",\"object_type\",\"detail\",\"occurrences\"\n";
        TestSupport.write(f[1].resolve("unparsed.csv"), gaps + "\"e\",\"unread_attribute\",\"PhysicalTable\",\"PhysicalKey@name\",\"2\"\n");
        TestSupport.write(f[0].resolve("unparsed.csv"), gaps);
        result = KbCompare.compare(f[0], f[1], estate, "e", "unparsed", Map.of());
        Check.that(result.nothingUnexplained(), "the names of the keys are read now: " + result.summary());
        Check.equal(1, result.explainedMissing.get(28).size(), "the row that is gone, under difference 28");
        TestSupport.write(f[0].resolve("unparsed.csv"), gaps + "\"e\",\"unread_attribute\",\"PhysicalTable\",\"PhysicalKey@name\",\"1\"\n");
        Check.that(!KbCompare.compare(f[0], f[1], estate, "e", "unparsed", Map.of()).nothingUnexplained(),
                "a name still listed as not read, where the files leave none outside what the jar reads, is not explained");
    }

    public void testRoleNamesAreExplainedByDifferenceTwentySevenOnlyWhenTheSourceAgrees() throws Exception {
        Path[] f = folders("compare-roles", "x", "x");
        Path estate = farEstate(f);
        String header = "\"row_id\",\"rpd_id\",\"kind\",\"role\"";
        String rows = "\"e::sec::obj.O.o0\",\"e\",\"object_privilege\",\"\"%s\n\"e::sec::pkg.P.q0\",\"e\",\"query_privilege\",\"R\"%s\n"
                + "\"e::sec::pkg.Q.q0\",\"e\",\"query_privilege\",\"N\"%s\n";
        TestSupport.write(f[1].resolve("security_rls.csv"), header + "\n" + String.format(rows, "", "", ""));
        String added = header + ",\"role_display_name\",\"role_guid\"\n";
        TestSupport.write(f[0].resolve("security_rls.csv"), added + String.format(rows, ",\"\",\"\"", ",\"Role shown\",\"RG\"", ",\"\",\"\""));
        KbCompare.Result result = KbCompare.compare(f[0], f[1], estate, "e", "security_rls", Map.of());
        Check.that(result.nothingUnexplained(), "the two added columns are what the role's file says: " + result.summary());
        Check.equal(java.util.List.of("role_display_name", "role_guid"), result.explainedColumns.get(27), "both, every value checked");
        Check.that(!KbCompare.compare(f[0], f[1], "e", "security_rls", Map.of()).nothingUnexplained(), "with no source, nothing is explained");
        String[][] wrong = {{",\"\",\"\"", ",\"Role shown\",\"RX\"", ",\"\",\"\"", "a wrong guid"},
                {",\"\",\"\"", ",\"Role shown\",\"RG\"", ",\"Held by nobody\",\"\"", "another role's display name"},
                {",\"Role shown\",\"RG\"", ",\"Role shown\",\"RG\"", ",\"\",\"\"", "a role on an object privilege row"}};
        for (String[] change : wrong) {
            TestSupport.write(f[0].resolve("security_rls.csv"), added + String.format(rows, change[0], change[1], change[2]));
            Check.that(!KbCompare.compare(f[0], f[1], estate, "e", "security_rls", Map.of()).nothingUnexplained(), "not explained: " + change[3]);
        }
    }

    public void testCountsOfUnreadContentThatAFollowedLinkReadsAreExplainedOnlyWhenTheSourceAgrees() throws Exception {
        Path[] f = folders("compare-followed", "x", "x");
        Path estate = farEstate(f);
        String header = "\"rpd_id\",\"kind\",\"object_type\",\"detail\",\"occurrences\"\n";
        String row = "\"e\",\"%s\",\"%s\",\"%s\",\"%s\"\n";
        // In the estate: 3 foreign keys carry the link; 1 of 2 keys is pointed at, with 2 of the 3 column
        // references; 2 roles are pointed at by a package with a query privilege, 1 of them has a display name.
        TestSupport.write(f[1].resolve("unparsed.csv"), header
                + String.format(row, "unread_attribute", "PhysicalTable", "PhysicalForeignKey@counterPartKeyRef", "3")
                + String.format(row, "unread_element", "PhysicalTable", "PhysicalKey", "2")
                + String.format(row, "unread_element", "PhysicalTable", "RefColumns", "2")
                + String.format(row, "unread_element", "PhysicalTable", "RefColumn", "3")
                + String.format(row, "unread_attribute", "PhysicalTable", "RefColumn@columnRef", "3")
                + String.format(row, "unread_attribute", "PhysicalTable", "PhysicalKey@name", "2")
                + String.format(row, "unread_attribute", "Group", "Group@displayName", "2")
                + String.format(row, "unread_attribute", "Group", "Group@guid", "1"));
        String generated = header
                + String.format(row, "unread_element", "PhysicalTable", "PhysicalKey", "1")
                + String.format(row, "unread_element", "PhysicalTable", "RefColumns", "1")
                + String.format(row, "unread_element", "PhysicalTable", "RefColumn", "1")
                + String.format(row, "unread_attribute", "PhysicalTable", "RefColumn@columnRef", "1")
                + String.format(row, "unread_attribute", "PhysicalTable", "PhysicalKey@name", "2")
                + String.format(row, "unread_attribute", "Group", "Group@displayName", "1");
        TestSupport.write(f[0].resolve("unparsed.csv"), generated);
        KbCompare.Result result = KbCompare.compare(f[0], f[1], estate, "e", "unparsed", Map.of());
        Check.that(result.nothingUnexplained(), "every count fell by what the files hold: " + result.summary());
        Check.equal(4, result.explained.get(26).size(), "the key, its column list, its columns twice");
        Check.equal(1, result.explainedMissing.get(26).size(), "the link attribute is gone from the list");
        Check.equal(1, result.explained.get(27).size(), "one display name read");
        Check.equal(1, result.explainedMissing.get(27).size(), "the one guid read");
        Check.that(!KbCompare.compare(f[0], f[1], "e", "unparsed", Map.of()).nothingUnexplained(), "with no source, nothing is explained");
        // Until difference 28 the first of the two wrong counts here was a key count of 0. That is now exactly
        // what difference 28 requires (every key is read), so the test uses a column count that is wrong under
        // both differences: 26 needs it to fall by 2, 28 needs it to be 0.
        TestSupport.write(f[0].resolve("unparsed.csv"), generated.replace("\"RefColumn\",\"1\"", "\"RefColumn\",\"2\"")
                .replace("\"PhysicalKey@name\",\"2\"", "\"PhysicalKey@name\",\"1\""));
        KbCompare.Result off = KbCompare.compare(f[0], f[1], estate, "e", "unparsed", Map.of());
        Check.equal(2, off.differing.size(), "a count that fell by another number, and a count nothing reads, stay unexplained: " + off.differing);
    }

    // ------------------------------------------------------------------ deliberate difference 7

    private static final String BASE = "/oracle/bi/server/base/";
    private static final String JOINS_REFERENCE_HEADER = "\"row_id\",\"rpd_id\",\"kind\",\"columns\"\n";

    /** A source estate with one database, one schema and two tables, and the generated physical_columns for it. */
    private static Path sourceEstate(Path[] f) {
        Path estate = f[0].getParent().resolve("estate");
        String ns = " xmlns=\"" + TestSupport.XMLNS + "\"";
        TestSupport.object(estate, "Database", "db.xml", "<Database mdsid=\"mdb\" name=\"DB\"" + ns + "/>");
        TestSupport.object(estate, "Schema", "s.xml", "<Schema mdsid=\"ms\" name=\"S\"" + ns + " containerRef=\"" + BASE + "Database/db.xml#mdb\"/>");
        String inSchema = " containerRef=\"" + BASE + "Schema/s.xml#ms\"";
        TestSupport.object(estate, "PhysicalTable", "a.xml", "<PhysicalTable mdsid=\"mta\" name=\"A\"" + ns + inSchema + ">"
                + "<PhysicalColumn mdsid=\"ma1\" name=\"ID\"/><PhysicalColumn mdsid=\"ma2\" name=\"B ID\"/><PhysicalKey mdsid=\"mak\" name=\"K\"/></PhysicalTable>");
        TestSupport.object(estate, "PhysicalTable", "b.xml", "<PhysicalTable mdsid=\"mtb\" name=\"B\"" + ns + inSchema + ">"
                + "<PhysicalColumn mdsid=\"mb1\" name=\"ID\"/></PhysicalTable>");
        TestSupport.write(f[0].resolve("physical_columns.csv"), "\"row_id\",\"rpd_id\"\n\"e::phys::DB.S.A.ID\",\"e\"\n\"e::phys::DB.S.A.B ID\",\"e\"\n\"e::phys::DB.S.B.ID\",\"e\"\n");
        return estate;
    }

    private static String join(String name, String columns) {
        return "\"e::phys::DB.S.A." + name + "\",\"e\",\"foreign_key\",\"" + columns + "\"\n";
    }

    public void testForeignKeyColumnsAreExplainedByDifferenceSevenOnlyWhenTheSourceAgrees() throws Exception {
        Path[] f = folders("compare-fk", "x", "x");
        Path estate = sourceEstate(f);
        TestSupport.write(f[1].resolve("physical_joins.csv"), JOINS_REFERENCE_HEADER
                + join("one", "ma2") + join("two", "ma2 ma1") + join("dangling", "ma2 gone ma1") + join("unreadable", "ma2 ")
                + join("wrong column", "ma2") + join("wrong table", "ma1") + join("wrong order", "ma2 ma1") + join("too many", "ma2")
                + join("too few", "ma2 ma1") + join("invented", "gone") + join("not a column", "mak") + join("still an id", "ma2")
                + join("same", ""));
        TestSupport.write(f[0].resolve("physical_joins.csv"), JOINS_REFERENCE_HEADER
                + join("one", "e::phys::DB.S.A.B ID") + join("two", "e::phys::DB.S.A.B ID\ne::phys::DB.S.A.ID")
                + join("dangling", "e::phys::DB.S.A.B ID\n\ne::phys::DB.S.A.ID") + join("unreadable", "e::phys::DB.S.A.B ID\n")
                + join("wrong column", "e::phys::DB.S.A.ID") + join("wrong table", "e::phys::DB.S.B.ID")
                + join("wrong order", "e::phys::DB.S.A.ID\ne::phys::DB.S.A.B ID") + join("too many", "e::phys::DB.S.A.B ID\ne::phys::DB.S.A.ID")
                + join("too few", "e::phys::DB.S.A.B ID") + join("invented", "e::phys::DB.S.A.ID") + join("not a column", "e::phys::DB.S.A.K")
                + join("still an id", "ma2x") + join("same", ""));

        KbCompare.Result result = KbCompare.compare(f[0], f[1], estate, "e", "physical_joins", Map.of());
        Check.equal(java.util.Set.of(7), result.explained.keySet(), "only difference 7 explains anything here");
        String explained = String.join("\n", result.explained.get(7));
        Check.equal(4, result.explained.get(7).size(), "one column, two columns, a column that was not found, a link that could not be read: " + explained);
        for (String name : new String[] {"A.one|", "A.two|", "A.dangling|", "A.unreadable|"}) {
            Check.contains(explained, name, "explained");
        }
        String differing = String.join("\n", result.differing);
        Check.equal(8, result.differing.size(), "every wrong identifier stays unexplained: " + differing);
        for (String name : new String[] {"wrong column", "wrong table", "wrong order", "too many", "too few", "invented", "not a column", "still an id"}) {
            Check.contains(differing, "A." + name + "|", "not explained away");
        }
        Check.contains(result.summary(), "differing cells     8  explained cells 4 (difference 7: 4)", "summary");

        KbCompare.Result unchecked = KbCompare.compare(f[0], f[1], "e", "physical_joins", Map.of());
        Check.equal(12, unchecked.differing.size(), "with no source to check against, nothing is explained");
        KbCompare.Result elsewhere = KbCompare.compare(f[0], f[1], f[0], "e", "physical_joins", Map.of());
        Check.equal(12, elsewhere.differing.size(), "nor with a folder that is not the estate");

        // A row the generated physical_columns does not have cannot be vouched for.
        TestSupport.write(f[0].resolve("physical_columns.csv"), "\"row_id\",\"rpd_id\"\n\"e::phys::DB.S.A.ID\",\"e\"\n");
        Check.equal(12, KbCompare.compare(f[0], f[1], estate, "e", "physical_joins", Map.of()).differing.size(),
                "an identifier that is not a generated physical column is not explained, however right it looks: "
                        + "all four explained cells name the column that is now missing");
    }

    // ------------------------------------------------------------------ deliberate difference 19

    private static final String VARIABLES_REFERENCE_HEADER = "\"row_id\",\"rpd_id\",\"name\",\"redacted\",\"expr_text\",\"expr_text_desc\",\"expr_refs\"\n";
    private static final String UNPARSED_HEADER = "\"rpd_id\",\"kind\",\"object_type\",\"detail\",\"occurrences\"\n";
    private static final String SAME_VALUE = " — value withheld: it is the same as the value of a variable whose name indicates a credential";

    private static String variableRow(String name, String redacted, String text) {
        return "\"e::var::" + name + "\",\"e\",\"" + name + "\",\"" + redacted + "\",\"" + text + "\",\"" + text + "\",\"\"\n";
    }

    public void testAValueWithheldForBeingACredentialElsewhereIsExplainedByDifferenceNineteenOnlyWhenTheSourceSaysSo() throws Exception {
        Path[] f = folders("compare-redacted", "x", "x");
        Path estate = f[0].getParent().resolve("estate");
        String ns = " xmlns=\"" + TestSupport.XMLNS + "\"";
        String planted = "'made-up-" + "value'";
        TestSupport.object(estate, "Variable", "p.xml", "<Variable mdsid=\"m1\" name=\"REP PASSWORD\"" + ns + "><Expr><ExprText><![CDATA[ " + planted
                + " ]]></ExprText><ExprTextDesc><![CDATA[" + planted + "]]></ExprTextDesc></Expr></Variable>");
        TestSupport.object(estate, "Variable", "u.xml", "<Variable mdsid=\"m2\" name=\"REP USER\"" + ns + "><Expr><ExprText><![CDATA[" + planted
                + "]]></ExprText></Expr></Variable>");
        TestSupport.write(f[1].resolve("variables.csv"), VARIABLES_REFERENCE_HEADER
                + variableRow("REP PASSWORD", "true", "") + variableRow("REP USER", "false", planted)
                + variableRow("YEAR", "false", "2020") + variableRow("LOST", "false", "2021") + variableRow("KEPT", "false", planted));
        TestSupport.write(f[0].resolve("variables.csv"), VARIABLES_REFERENCE_HEADER
                + variableRow("REP PASSWORD", "true", "") + variableRow("REP USER", "true", "")
                + variableRow("YEAR", "true", "") + variableRow("LOST", "false", "") + variableRow("KEPT", "false", planted));

        KbCompare.Result result = KbCompare.compare(f[0], f[1], estate, "e", "variables", Map.of());
        Check.equal(java.util.Set.of(19), result.explained.keySet(), "only difference 19 explains anything here");
        Check.equal(3, result.explained.get(19).size(), "the flag and the two value cells of the one row");
        for (String line : result.explained.get(19)) {
            Check.that(line.startsWith("e::var::REP USER|"), "all three are of the variable holding the same value as the password variable");
        }
        Check.equal(5, result.differing.size(), "a value withheld that the source gives to no telling name (3 cells), and a value lost without the flag (2 cells)");
        for (String line : result.differing) {
            Check.that(line.startsWith("e::var::YEAR|") || line.startsWith("e::var::LOST|"), "the unexplained ones are named");
        }
        Check.equal(8, KbCompare.compare(f[0], f[1], "e", "variables", Map.of()).differing.size(), "with no source to check against, nothing is explained");

        TestSupport.write(f[1].resolve("unparsed.csv"), UNPARSED_HEADER);
        TestSupport.write(f[0].resolve("unparsed.csv"), UNPARSED_HEADER
                + "\"e\",\"redacted_value\",\"Variable\",\"REP USER" + SAME_VALUE + "\",\"1\"\n"
                + "\"e\",\"redacted_value\",\"Variable\",\"YEAR" + SAME_VALUE + "\",\"1\"\n"
                + "\"e\",\"redacted_value\",\"Variable\",\"NOBODY" + SAME_VALUE + "\",\"1\"\n"
                + "\"e\",\"redacted_value\",\"Variable\",\"REP USER — some other reason\",\"1\"\n");
        KbCompare.Result unparsed = KbCompare.compare(f[0], f[1], estate, "e", "unparsed", Map.of());
        Check.equal(1, unparsed.explainedExtra.get(19).size(), "the record of that withholding is explained");
        Check.contains(unparsed.explainedExtra.get(19).get(0), "REP USER" + SAME_VALUE, "and is the right row");
        Check.equal(3, unparsed.extra.size(), "a record for a variable whose value is no credential, for a variable that does not exist, or with another reason, is not: " + unparsed.extra);
        Check.contains(unparsed.summary(), "extra     3  differing cells     0  explained extra rows 1 (difference 19: 1)", "summary");
        Check.that(!unparsed.same() && !unparsed.nothingUnexplained(), "so the table is not clean");
    }

    // ------------------------------------------------------------------ story 1.7: --where-not, differences 11 and 21

    public void testWhereNotLeavesRowsOutOnBothSidesAndIgnoresATableWithoutTheColumn() throws Exception {
        Path[] f = folders("compare-where-not",
                REFERENCE_HEADER
                        + "\"e::phys::a\",\"e\",\"a\",\"false\",\"1\"\n"
                        + "\"e::phys::b\",\"e\",\"b, with a comma\",\"false\",\"2\"\n",
                "\"row_id\",\"rpd_id\",\"name\",\"size\"\n"
                        + "\"e::phys::a\",\"e\",\"a\",\"1\"\n"
                        + "\"e::phys::c\",\"e\",\"later\",\"3\"\n");
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int exit = KbCompare.run(new String[] {"e", "things", "--kb", f[0].toString(), "--ref", f[1].toString(),
                "--where-not", "name=b, with a comma", "--where-not", "name=later", "--where-not", "no_such_column=x"}, new PrintStream(out));
        Check.equal(0, exit, "the two rows left out were the only differences: " + out);
        Check.contains(out.toString(), "reference      1  generated      1", "one row left on each side");
        Check.equal(1, KbCompare.run(new String[] {"e", "things", "--kb", f[0].toString(), "--ref", f[1].toString(),
                "--where-not", "name=later"}, new PrintStream(new ByteArrayOutputStream())), "the other difference is still found");
    }

    private static final String GFV = "{com.siebel.analytics.web/dashboard/v1.1}globalFilterView";
    private static final String REPORT_REF = "{com.siebel.analytics.web/dashboard/v1.1}reportRef";
    private static final String CONTENTS_HEADER = "\"row_id\",\"rpd_id\",\"container_id\",\"element\",\"resolution\"\n";

    private static String pathError(String page, String element, String reference) {
        return "\"e\",\"unresolved_reference\",\"" + page + "\",\"" + element + "\",\"'" + reference + "' did not resolve\"\n";
    }

    private static String placement(String page, int n, String element, String resolution) {
        return "\"e::cat::" + page + "#item" + n + "\",\"e\",\"e::cat::" + page + "\",\"" + element + "\",\"" + resolution + "\"\n";
    }

    public void testDoubledGlobalFilterErrorsAreExplainedByDifferenceElevenOnlyWhenExactlyDoubled() throws Exception {
        Path[] f = folders("compare-doubled", "x", "x");
        TestSupport.write(f[1].resolve("errors.csv"), ERRORS_HEADER
                // p1: one unresolved prompt, written twice by the reference.
                + pathError("p1", GFV, "/x/a") + pathError("p1", GFV, "/x/a")
                // p2: the same prompt placed twice, so four identical rows against two.
                + pathError("p2", GFV, "/x/b") + pathError("p2", GFV, "/x/b") + pathError("p2", GFV, "/x/b") + pathError("p2", GFV, "/x/b")
                // p3: three copies against one is not a doubling.
                + pathError("p3", GFV, "/x/c") + pathError("p3", GFV, "/x/c") + pathError("p3", GFV, "/x/c")
                // p4: the generated table lacks the error altogether.
                + pathError("p4", GFV, "/x/d") + pathError("p4", GFV, "/x/d")
                // p5: the same pattern on another element is not difference 11.
                + pathError("p5", REPORT_REF, "/x/e") + pathError("p5", REPORT_REF, "/x/e")
                // p6: doubled, but the generated placements do not account for the generated errors.
                + pathError("p6", GFV, "/x/f") + pathError("p6", GFV, "/x/f"));
        TestSupport.write(f[0].resolve("errors.csv"), ERRORS_HEADER
                + pathError("p1", GFV, "/x/a")
                + pathError("p2", GFV, "/x/b") + pathError("p2", GFV, "/x/b")
                + pathError("p3", GFV, "/x/c")
                + pathError("p5", REPORT_REF, "/x/e")
                + pathError("p6", GFV, "/x/f"));
        TestSupport.write(f[0].resolve("dashboard_contents.csv"), CONTENTS_HEADER
                + placement("p1", 0, REPORT_REF, "unresolved") + placement("p1", 1, GFV, "unresolved") + placement("p1", 2, GFV, "exact")
                + placement("p2", 0, GFV, "unresolved") + placement("p2", 1, GFV, "unresolved")
                + placement("p3", 0, GFV, "unresolved")
                + placement("p5", 0, REPORT_REF, "unresolved")
                + placement("p6", 0, GFV, "exact"));

        KbCompare.Result result = KbCompare.compare(f[0], f[1], "e", "errors", Map.of());
        Check.equal(Set.of(11), result.explainedMissing.keySet(), "only difference 11 explains anything here");
        Check.equal(3, result.explainedMissing.get(11).size(), "one row of p1 and two of p2: " + result.explainedMissing);
        String missing = String.join("\n", result.missing);
        Check.equal(6, result.missing.size(), "two of p3, two of p4, one of p5, one of p6 stay unexplained: " + missing);
        for (String page : new String[] {"p3", "p4", "p5", "p6"}) {
            Check.contains(missing, "| " + page + " |", "not explained away");
        }
        Check.equal(0, result.extra.size(), "no extra rows");
    }

    public void testTheHalvedSkippedCountIsExplainedOnlyWhenTheErrorRowsAccountForIt() throws Exception {
        Path[] f = folders("compare-halved", "x", "x");
        String coverageHeader = "\"rpd_id\",\"layer\",\"object_type\",\"parsed\",\"skipped\"\n";
        TestSupport.write(f[1].resolve("errors.csv"), ERRORS_HEADER + pathError("p1", GFV, "/x/a") + pathError("p1", GFV, "/x/a")
                + pathError("p2", GFV, "/x/b") + pathError("p2", GFV, "/x/b"));
        TestSupport.write(f[0].resolve("errors.csv"), ERRORS_HEADER + pathError("p1", GFV, "/x/a") + pathError("p2", GFV, "/x/b"));
        TestSupport.write(f[0].resolve("dashboard_contents.csv"), CONTENTS_HEADER
                + placement("p1", 0, GFV, "unresolved") + placement("p2", 0, GFV, "unresolved"));
        TestSupport.write(f[1].resolve("coverage.csv"), coverageHeader + "\"e\",\"cat\",\"" + GFV + "\",\"0\",\"4\"\n"
                + "\"e\",\"cat\",\"" + REPORT_REF + "\",\"0\",\"4\"\n");
        TestSupport.write(f[0].resolve("coverage.csv"), coverageHeader + "\"e\",\"cat\",\"" + GFV + "\",\"0\",\"2\"\n"
                + "\"e\",\"cat\",\"" + REPORT_REF + "\",\"0\",\"2\"\n");
        KbCompare.Result result = KbCompare.compare(f[0], f[1], "e", "coverage", Map.of());
        Check.equal(1, result.explained.getOrDefault(11, java.util.List.of()).size(), "the global filter view count: " + result.summary());
        Check.equal(1, result.differing.size(), "the same halving on another element is not explained: " + result.differing);
        Check.contains(result.differing.get(0), "reportRef", "the unexplained one");

        // One generated error too few: the counts no longer follow from the error rows.
        TestSupport.write(f[0].resolve("errors.csv"), ERRORS_HEADER + pathError("p1", GFV, "/x/a"));
        Check.equal(2, KbCompare.compare(f[0], f[1], "e", "coverage", Map.of()).differing.size(),
                "a halved count that the error rows do not account for is not explained");
    }

    public void testAnUndecodedNameRowIsExplainedByDifferenceTwentyOneOnlyWhenTheFileIsThereAndItsNameIsMalformed() throws Exception {
        Path[] f = folders("compare-undecoded", "x", "x");
        Path estate = f[0].getParent().resolve("estate");
        TestSupport.catalogFile(estate, "a/t%edtulo/x", "x");
        TestSupport.catalogFile(estate, "a/50%zz", "x");
        TestSupport.catalogFile(estate, "a/fine%2ename", "x");
        String why = " \u2014 the name holds an escape that cannot be decoded; read as ";
        TestSupport.write(f[1].resolve("unparsed.csv"), UNPARSED_HEADER);
        TestSupport.write(f[0].resolve("unparsed.csv"), UNPARSED_HEADER
                + "\"e\",\"undecoded_name\",\"File\",\"a/t%edtulo/x" + why + "a/t\uFFFDtulo/x\",\"1\"\n"
                + "\"e\",\"undecoded_name\",\"File\",\"a/50%zz" + why + "a/50%zz\",\"1\"\n"
                + "\"e\",\"undecoded_name\",\"File\",\"a/fine%2ename" + why + "a/fine.name\",\"1\"\n"
                + "\"e\",\"undecoded_name\",\"File\",\"a/not%zzthere" + why + "a/not%zzthere\",\"1\"\n"
                + "\"e\",\"unread_attribute\",\"File\",\"a/50%zz" + why + "a/50%zz\",\"1\"\n");
        KbCompare.Result result = KbCompare.compare(f[0], f[1], estate, "e", "unparsed", Map.of());
        Check.equal(2, result.explainedExtra.getOrDefault(21, java.util.List.of()).size(), "the two malformed names: " + result.explainedExtra);
        Check.equal(3, result.extra.size(), "a well-formed name, a file that is not there, and another kind stay unexplained: " + result.extra);
        Check.equal(5, KbCompare.compare(f[0], f[1], "e", "unparsed", Map.of()).extra.size(), "with no estate folder, nothing is explained");
    }

    // ------------------------------------------------------------------ differences 8, 9 and 10

    private static final String SAW_NS = " xmlns:saw=\"com.siebel.analytics.web/report/v1.1\" xmlns:sawx=\"com.siebel.analytics.web/expression/v1.1\""
            + " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"";
    private static final String EXPR = "{com.siebel.analytics.web/expression/v1.1}expr";
    private static final String REPORT = "{com.siebel.analytics.web/report/v1.1}report";
    private static final String SAVED = "{com.siebel.analytics.web/savedfilter/v1.1}savedFilterObject";

    /** Rewrites one table of a Knowledge Base folder, each row passed through the change; null drops the row. */
    private static void rewrite(Path from, Path to, String table, java.util.List<String> dropColumns,
            java.util.function.BiFunction<obi2pbi.in.kb.CsvTable, String[], String[]> change) {
        obi2pbi.in.kb.CsvTable read = TestSupport.table(from, table);
        StringBuilder out = new StringBuilder();
        java.util.List<String[]> rows = new java.util.ArrayList<>();
        rows.add(read.header.toArray(new String[0]));
        for (String[] row : read.rows) {
            String[] changed = change.apply(read, row.clone());
            if (changed != null) {
                rows.add(changed);
            }
        }
        for (String[] row : rows) {
            java.util.List<String> cells = new java.util.ArrayList<>();
            for (int i = 0; i < row.length; i++) {
                if (!dropColumns.contains(read.header.get(i))) {
                    cells.add("\"" + row[i].replace("\"", "\"\"") + "\"");
                }
            }
            out.append(String.join(",", cells)).append("\n");
        }
        TestSupport.write(to.resolve(table + ".csv"), out.toString());
    }

    private static String[] set(obi2pbi.in.kb.CsvTable table, String[] row, String column, String value) {
        row[table.indexOf(column)] = value;
        return row;
    }

    /**
     * A small catalog, the jar's Knowledge Base for it in {@code kb}, and in {@code ref} what the reference
     * implementation writes for it: texts cut at the first nested element, no parent or depth, the
     * outermost expression of the saved filter only, its reference to another saved filter never resolved,
     * and the content it left unread listed. Returns the estate folder, {@code kb} and {@code ref}.
     */
    private static Path[] reportsFixture(String work) {
        Path folder = TestSupport.work(work);
        Path estate = folder.resolve("e");
        TestSupport.catalogFile(estate, "s/r", "<saw:report" + SAW_NS + "><saw:criteria><saw:columns>"
                + "<saw:column xsi:type=\"saw:regularColumn\" columnID=\"c1\"><saw:columnFormula><sawx:expr xsi:type=\"sawx:sqlExpression\">CAST("
                + "<sawx:expr xsi:type=\"sawx:eval\">EVALUATE('f')</sawx:expr> AS INT)</sawx:expr></saw:columnFormula></saw:column></saw:columns>"
                + "<saw:filter><sawx:expr xsi:type=\"sawx:logical\" op=\"and\">\n <sawx:expr xsi:type=\"sawx:comparison\" op=\"equal\">"
                + "<sawx:expr xsi:type=\"sawx:sqlExpression\">\"T\".\"A\"</sawx:expr><sawx:expr xsi:type=\"xsd:string\">1</sawx:expr></sawx:expr>"
                + "\n</sawx:expr></saw:filter></saw:criteria></saw:report>");
        TestSupport.catalogFile(estate, "s/_filters/f", "<sawsf:savedFilterObject xmlns:sawsf=\"com.siebel.analytics.web/savedfilter/v1.1\""
                + SAW_NS + "><saw:filter><sawx:expr xsi:type=\"sawx:logical\" op=\"and\"><sawx:expr xsi:type=\"sawx:special\" op=\"prompted\">"
                + "<sawx:expr xsi:type=\"sawx:sqlExpression\">\"T\".\"B\"</sawx:expr></sawx:expr>"
                + "<sawx:expr xsi:type=\"sawx:savedFilter\" path=\"/shared/s/_filters/none\"/> </sawx:expr></saw:filter></sawsf:savedFilterObject>");
        Path kb = folder.resolve("kb");
        Path ref = folder.resolve("ref");
        Check.equal(3, TestSupport.extract(estate, kb).exit, "one unresolved reference");

        rewrite(kb, ref, "analyses", java.util.List.of(), (t, row) -> row);
        rewrite(kb, ref, "analysis_columns", java.util.List.of(), (t, row) -> set(t, row, "formula", "CAST("));
        rewrite(kb, ref, "saved_filters", java.util.List.of(), (t, row) -> set(t, row, "expr_text", ""));
        rewrite(kb, ref, "analysis_filters", java.util.List.of("parent_id", "depth"), (t, row) -> {
            if (t.value(row, "scope").equals("saved_filter")) {
                return null;
            }
            String id = t.value(row, "row_id");
            return id.endsWith("#f0.0") ? set(t, row, "expr_text", "\n ") : id.endsWith("#f0.1") ? set(t, row, "expr_text", "") : row;
        });
        TestSupport.write(ref.resolve("errors.csv"), ERRORS_HEADER);
        TestSupport.write(ref.resolve("coverage.csv"), "\"rpd_id\",\"layer\",\"object_type\",\"parsed\",\"skipped\"\n"
                + "\"e\",\"cat\",\"" + REPORT + "\",\"1\",\"0\"\n\"e\",\"cat\",\"" + SAVED + "\",\"1\",\"0\"\n");
        StringBuilder unparsed = new StringBuilder();
        obi2pbi.in.kb.CsvTable generated = TestSupport.table(kb, "unparsed");
        unparsed.append(UNPARSED_HEADER);
        for (String[] row : generated.rows) {
            unparsed.append("\"").append(String.join("\",\"", row)).append("\"\n");
        }
        unparsed.append("\"e\",\"unread_text\",\"" + REPORT + "\",\"" + EXPR + "#text\",\"1\"\n");
        unparsed.append("\"e\",\"unread_attribute\",\"" + SAVED + "\",\"" + EXPR + "@op\",\"1\"\n");
        unparsed.append("\"e\",\"unread_attribute\",\"" + SAVED + "\",\"" + EXPR + "@path\",\"1\"\n");
        unparsed.append("\"e\",\"unread_text\",\"" + SAVED + "\",\"" + EXPR + "#text\",\"1\"\n");
        TestSupport.write(ref.resolve("unparsed.csv"), unparsed.toString());
        return new Path[] {estate, kb, ref};
    }

    public void testEveryDifferenceOfTheReportAndFilterTablesIsExplainedWhenTheSourceBearsItOut() throws Exception {
        Path[] f = reportsFixture("compare-reports");
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int exit = KbCompare.run(new String[] {"e", "analyses", "analysis_columns", "analysis_filters", "saved_filters", "errors", "coverage",
                "unparsed", "--kb", f[1].toString(), "--ref", f[2].toString(), "--source", f[0].toString()}, new PrintStream(out));
        Check.equal(0, exit, "nothing unexplained: " + out);
        Check.contains(out.toString(), "difference 8: 4, difference 9: 9, difference 10: 2", "what explains them: " + out);

        KbCompare.Result columns = KbCompare.compare(f[1], f[2], f[0], "e", "analysis_columns", Map.of());
        Check.equal(1, columns.explained.get(8).size(), "the formula the reference cut at the nested element");
        KbCompare.Result filters = KbCompare.compare(f[1], f[2], f[0], "e", "analysis_filters", Map.of());
        Check.equal(1, filters.explained.get(8).size(), "the group row, which now also holds the text after its nested expression");
        Check.equal("\n \n", TestSupport.table(f[1], "analysis_filters").value(
                TestSupport.row(TestSupport.table(f[1], "analysis_filters"), "row_id", "e::cat::s/r#f0.0"), "expr_text"),
                "its own text only: what is nested in it is in the rows of the nested expressions");
        Check.equal(1, KbCompare.compare(f[1], f[2], f[0], "e", "saved_filters", Map.of()).explained.get(8).size(),
                "the same for the saved filter's outermost expression");
        Check.equal(4, filters.explainedExtra.get(9).size(), "the four expressions of the saved filter");
        Check.equal(java.util.List.of("parent_id", "depth"), filters.explainedColumns.get(10), "the two added columns, every value checked");
        Check.that(filters.nothingUnexplained() && !filters.same(), "explained, and still reported as a difference");
        KbCompare.Result unparsed = KbCompare.compare(f[1], f[2], f[0], "e", "unparsed", Map.of());
        Check.equal(1, unparsed.explainedMissing.get(8).size(), "the nested text of the formula is no longer unread");
        Check.equal(3, unparsed.explainedMissing.get(9).size(), "nor the operator, path and text of the saved filter's expressions");
        Check.equal(1, KbCompare.compare(f[1], f[2], f[0], "e", "errors", Map.of()).explainedExtra.get(9).size(),
                "the saved filter's reference is now resolved, and fails");
        Check.equal(1, KbCompare.compare(f[1], f[2], f[0], "e", "coverage", Map.of()).explainedExtra.get(9).size(), "and is counted");

        // Without the estate's files nothing can be checked, so nothing is explained.
        Check.equal(1, KbCompare.compare(f[1], f[2], "e", "analysis_columns", Map.of()).differing.size(), "no source: the formula");
        KbCompare.Result blind = KbCompare.compare(f[1], f[2], "e", "analysis_filters", Map.of());
        Check.equal(4, blind.extra.size(), "no source: the saved filter rows");
        Check.equal(java.util.List.of("parent_id", "depth"), blind.columnsOnlyInGenerated, "no source: the added columns");
    }

    public void testAWholeTextThatIsNotTheSourcesIsNotExplainedByDifferenceEight() throws Exception {
        Path[] f = reportsFixture("compare-whole-text");
        // Longer than the reference and starting with it, but not what the file holds.
        rewrite(f[1], f[1], "analysis_columns", java.util.List.of(), (t, row) -> set(t, row, "formula", "CAST(EVALUATE('f') AS INTEGER)"));
        KbCompare.Result wrong = KbCompare.compare(f[1], f[2], f[0], "e", "analysis_columns", Map.of());
        Check.equal(1, wrong.differing.size(), "a wrong value is not explained away: " + wrong.summary());
        Check.that(wrong.explained.isEmpty(), "nothing explained");

        // A filter row that holds the text of its nested expressions as well: they have rows of their own.
        rewrite(f[1], f[1], "analysis_filters", java.util.List.of(),
                (t, row) -> t.value(row, "row_id").endsWith("#f0.0") ? set(t, row, "expr_text", "\n \"T\".\"A\"1\n") : row);
        KbCompare.Result run = KbCompare.compare(f[1], f[2], f[0], "e", "analysis_filters", Map.of());
        Check.equal(1, run.differing.size(), "the whole text on a filter row is not the filter rule: " + run.summary());

        // The generated value is right, but the reference value is not the text before the first nested element.
        Path[] g = reportsFixture("compare-whole-text-2");
        rewrite(g[2], g[2], "analysis_columns", java.util.List.of(), (t, row) -> set(t, row, "formula", "CAS"));
        Check.equal(1, KbCompare.compare(g[1], g[2], g[0], "e", "analysis_columns", Map.of()).differing.size(),
                "a reference value that is a prefix but not the source's leading text is not difference 8");
        rewrite(g[2], g[2], "analysis_columns", java.util.List.of(), (t, row) -> set(t, row, "formula", "other"));
        Check.equal(1, KbCompare.compare(g[1], g[2], g[0], "e", "analysis_columns", Map.of()).differing.size(), "nor one that is no prefix at all");
    }

    public void testAWrongParentDepthOrSavedFilterRowIsNotExplained() throws Exception {
        Path[] f = reportsFixture("compare-tree");
        rewrite(f[1], f[1], "analysis_filters", java.util.List.of(),
                (t, row) -> t.value(row, "row_id").endsWith("#f0.2") ? set(t, row, "depth", "1") : row);
        KbCompare.Result depth = KbCompare.compare(f[1], f[2], f[0], "e", "analysis_filters", Map.of());
        Check.equal(java.util.List.of("parent_id", "depth"), depth.columnsOnlyInGenerated, "one wrong depth and the columns are not explained");
        Check.that(!depth.nothingUnexplained(), "reported");

        Path[] g = reportsFixture("compare-tree-2");
        rewrite(g[1], g[1], "analysis_filters", java.util.List.of(),
                (t, row) -> t.value(row, "row_id").endsWith("#f0.1") ? set(t, row, "parent_id", "") : row);
        Check.that(!KbCompare.compare(g[1], g[2], g[0], "e", "analysis_filters", Map.of()).columnsOnlyInGenerated.isEmpty(), "nor with one wrong parent");

        Path[] h = reportsFixture("compare-tree-3");
        rewrite(h[1], h[1], "analysis_filters", java.util.List.of(),
                (t, row) -> t.value(row, "row_id").endsWith("/f#f1") ? set(t, row, "op", "equal") : row);
        KbCompare.Result op = KbCompare.compare(h[1], h[2], h[0], "e", "analysis_filters", Map.of());
        Check.equal(1, op.extra.size(), "a saved filter row with an operator the source does not have: " + op.extra);
        Check.equal(3, op.explainedExtra.get(9).size(), "the other three are as the source has them");

        Path[] k = reportsFixture("compare-tree-4");
        rewrite(k[1], k[1], "analysis_filters", java.util.List.of(), (t, row) -> t.value(row, "row_id").endsWith("/f#f2") ? null : row);
        KbCompare.Result dropped = KbCompare.compare(k[1], k[2], k[0], "e", "analysis_filters", Map.of());
        Check.equal(3, dropped.extra.size(), "a saved filter with an expression missing: none of its rows is explained");
    }

    public void testAnUnreadCountThatDropsByAnotherNumberIsNotExplained() throws Exception {
        Path[] f = reportsFixture("compare-now-read");
        String more = "\"e\",\"unread_attribute\",\"" + SAVED + "\",\"" + EXPR + "@op\",\"1\"\n";
        String text = new String(java.nio.file.Files.readAllBytes(f[2].resolve("unparsed.csv")), java.nio.charset.StandardCharsets.UTF_8);
        TestSupport.write(f[2].resolve("unparsed.csv"), text.replace(more, more.replace("\"1\"\n", "\"2\"\n"))
                + "\"e\",\"unread_text\",\"" + REPORT + "\",\"{urn:x}never#text\",\"1\"\n");
        KbCompare.Result result = KbCompare.compare(f[1], f[2], f[0], "e", "unparsed", Map.of());
        Check.equal(2, result.missing.size(), "two operators where the source has one, and a row the source gives no reason for: " + result.missing);
        Check.equal(3, result.explainedMissingRows(), "the rest is still explained");
    }

    public void testAFormulaCopiedIntoReportLineageIsExplainedByDifferenceEightOnlyWhenTheReportColumnIs() throws Exception {
        Path[] f = reportsFixture("compare-copied-formula");
        rewrite(f[1], f[2], "report_lineage", java.util.List.of(), (t, row) -> set(t, row, "reference_text", "CAST("));
        KbCompare.Result result = KbCompare.compare(f[1], f[2], f[0], "e", "report_lineage", Map.of());
        Check.equal(1, result.explained.get(8).size(), "the copy of a formula difference 8 changed: " + result.summary());
        Check.that(result.nothingUnexplained(), "nothing else: " + result.summary());
        Check.equal(1, result.rowsOnlyExplained, "one row that differs only as explained");
        Check.equal(0, result.identicalRows, "and no other row");

        // A reference value that is not the reference's own formula for that column is not a copy.
        rewrite(f[1], f[2], "report_lineage", java.util.List.of(), (t, row) -> set(t, row, "reference_text", "CAS"));
        Check.equal(1, KbCompare.compare(f[1], f[2], f[0], "e", "report_lineage", Map.of()).differing.size(),
                "not the reference's formula");
    }

    private static final String LINEAGE_HEADER = "\"row_id\",\"rpd_id\",\"presentation_column_id\",\"logical_column_id\","
            + "\"source_id\",\"physical_table_id\",\"physical_column_id\"\n";
    private static final String LINEAGE_ROWS = "\"e::pres::A.T.C#s1\",\"e\",\"e::pres::A.T.C\",\"l\",\"s1\",\"t1\",\"p1\"\n"
            + "\"e::pres::A.T.C#s2\",\"e\",\"e::pres::A.T.C\",\"l\",\"s2\",\"t2\",\"p2\"\n"
            + "\"e::pres::A.T.D#s1\",\"e\",\"e::pres::A.T.D\",\"l\",\"s1\",\"t1\",\"p9\"\n";

    private static String reportLineage(String presentation, String physical) {
        return "\"row_id\",\"rpd_id\",\"presentation_column_id\",\"resolution\",\"logical_column_id\",\"physical_column_id\"\n"
                + "\"e::cat::r#c0.0#lineage\",\"e\",\"" + presentation + "\",\"exact\",\"l\",\"" + physical + "\"\n";
    }

    private static KbCompare.Result lineageChoice(String work, String presentation, String referencePhysical,
            String generatedPhysical) throws Exception {
        Path folder = TestSupport.work(work);
        TestSupport.write(folder.resolve("ref/lineage.csv"), LINEAGE_HEADER + LINEAGE_ROWS);
        TestSupport.write(folder.resolve("kb/lineage.csv"), LINEAGE_HEADER + LINEAGE_ROWS);
        TestSupport.write(folder.resolve("ref/report_lineage.csv"), reportLineage(presentation, referencePhysical));
        TestSupport.write(folder.resolve("kb/report_lineage.csv"), reportLineage(presentation, generatedPhysical));
        return KbCompare.compare(folder.resolve("kb"), folder.resolve("ref"), "e", "report_lineage", Map.of());
    }

    public void testAnotherLineageRowIsExplainedByDifferenceTwentyTwoOnlyWhenTheGeneratedOneIsTheFirst() throws Exception {
        KbCompare.Result first = lineageChoice("compare-22", "e::pres::A.T.C", "p2", "p1");
        Check.equal(1, first.explained.get(22).size(), "the reference used the second row, the jar the first: " + first.summary());
        Check.that(first.nothingUnexplained(), "nothing else");

        Check.equal(1, lineageChoice("compare-22-not-first", "e::pres::A.T.C", "p1", "p2").differing.size(),
                "the generated value is not the first row's");
        Check.equal(1, lineageChoice("compare-22-no-such-row", "e::pres::A.T.C", "p7", "p1").differing.size(),
                "the reference value is on no lineage row of that presentation column");
        Check.equal(1, lineageChoice("compare-22-one-row", "e::pres::A.T.D", "p2", "p9").differing.size(),
                "a presentation column with one lineage row leaves no choice");
    }
}
