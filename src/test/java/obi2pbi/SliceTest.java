package obi2pbi;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import obi2pbi.core.KbData;
import obi2pbi.core.KbRows;
import obi2pbi.core.Slice;
import obi2pbi.core.Variables;

/**
 * The {@code slice} command and the walk behind it (story 2.1; Architecture AD-9, AD-14; deliberate
 * differences 13, 23 and 24).
 */
public final class SliceTest {

    /** The first attempt's hand-made repository and catalog in one estate: a dashboard, a page, a report, a saved filter. */
    static Path pairKb(Path work) {
        Path estate = TestSupport.estateFrom(work, "constructed-pair", TestSupport.constructed());
        try {
            TestSupport.copyTree(TestSupport.constructedCatalog(), estate.resolve("catalog"));
        } catch (java.io.IOException e) {
            throw new java.io.UncheckedIOException(e);
        }
        Path kb = work.resolve("kb");
        Check.equal(0, TestSupport.extract(estate, kb).exit, "extract of the combined estate");
        return kb;
    }

    static Path linkedKb(Path work) {
        Path estate = work.resolve("linked");
        try {
            TestSupport.copyTree(TestSupport.root().resolve("src/test/fixtures/linked-estate"), estate);
        } catch (java.io.IOException e) {
            throw new java.io.UncheckedIOException(e);
        }
        Path kb = work.resolve("kb");
        TestSupport.extract(estate, kb);
        return kb;
    }

    private static Set<String> ids(KbData data, String table) {
        Set<String> ids = new TreeSet<>();
        KbRows rows = data.table(table);
        for (String[] row : rows.rows) {
            ids.add(rows.get(row, "row_id"));
        }
        return ids;
    }

    // ------------------------------------------------------------------ the command line

    public void testWithNoSubjectAreaItListsThemWithCountsAndWritesNothing() {
        Path work = TestSupport.work("slice-list");
        Path out = work.resolve("slices");
        TestSupport.Run run = TestSupport.run("slice", pairKb(work).toString(), "--out", out.toString());
        Check.equal(0, run.exit, "exit code: " + run.err);
        Check.contains(run.out, "constructed-pair     Constructed SA  columns: 2  measures: 1", "the subject area with its counts");
        Check.that(!Files.exists(out), "nothing is written");
    }

    public void testAnUnknownNameSaysSoListsTheNamesAndExits2() {
        Path work = TestSupport.work("slice-unknown");
        Path out = work.resolve("slices");
        TestSupport.Run run = SliceFixture.slice(pairKb(work), out, "--subject-area", "constructed sa");
        Check.equal(2, run.exit, "exit code");
        Check.contains(run.err, "no subject area named 'constructed sa'", "says so; the name is matched exactly");
        Check.contains(run.err, "Available: Constructed SA", "lists the names");
        Check.that(!Files.exists(out), "nothing is written");
    }

    public void testWrongUsageAndAFolderThatIsNotAKnowledgeBaseExit2() {
        Path work = TestSupport.work("slice-usage");
        Check.equal(2, TestSupport.run("slice").exit, "no folder");
        Check.equal(2, TestSupport.run("slice", work.toString()).exit, "a folder with no manifest");
        Check.contains(TestSupport.run("slice", work.toString()).err, "has no manifest.csv", "says what is missing");
        Check.equal(2, TestSupport.run("slice", work.toString(), "--nonsense").exit, "unknown option");
        Check.equal(2, TestSupport.run("slice", work.toString(), "--subject-area").exit, "option without a value");
        Check.equal(0, TestSupport.run("slice", "--help").exit, "help");
        Check.contains(TestSupport.run("--help").out, "slice", "listed among the commands");
    }

    public void testHappyPathWritesASliceInTheKnowledgeBasesShape() {
        Path work = TestSupport.work("slice-happy");
        Path kb = pairKb(work);
        Path out = work.resolve("slices");
        TestSupport.Run run = SliceFixture.slice(kb, out, "--subject-area", "Constructed SA");
        Check.equal(0, run.exit, "exit code: " + run.err);
        Path folder = out.resolve("constructed-sa");
        Check.contains(run.out, "subject area: Constructed SA (constructed-pair)", "summary names the subject area");
        Check.contains(run.out, "written:      " + folder, "summary names the folder");

        KbData slice = SliceFixture.read(folder);
        Check.equal("Constructed SA", slice.fact("subject_area"), "the exact name is in the manifest");
        Check.equal("constructed-pair::pres::Constructed SA", slice.fact("subject_area_id"), "and its Row identifier");
        Check.equal("subject_area", slice.fact("cut_by"), "what the slice was cut by");
        Check.equal(kb.toString(), slice.fact("knowledge_base"), "the Knowledge Base as typed");

        // Same file names and columns as the Knowledge Base, its own manifest, errors and unparsed, the two
        // schema tables, and the units. The estate-wide count tables are not carried.
        List<String> expected = new ArrayList<>();
        for (String file : TestSupport.fileNames(kb)) {
            if (!List.of("coverage.csv", "coverage_summary.csv", "catalog_versions.csv").contains(file)) {
                expected.add(file);
            }
        }
        expected.add("required_rows.csv");
        expected.add("units.csv");
        expected.sort(null);
        Check.equal(expected, TestSupport.fileNames(folder), "files of the slice");
        List<String> listed = new ArrayList<>(List.of(slice.fact("tables").split(" ")));
        listed.add("manifest");
        listed.sort(null);
        List<String> files = new ArrayList<>();
        for (String file : TestSupport.fileNames(folder)) {
            files.add(file.substring(0, file.length() - 4));
        }
        Check.equal(files, listed, "the manifest lists exactly the files");
        KbData whole = SliceFixture.read(kb);
        for (String table : slice.tableNames()) {
            if (whole.has(table) && !table.startsWith("schema") && !table.equals("manifest")) {
                Check.equal(whole.table(table).header, slice.table(table).header, table + ": same columns");
                Set<String> source = new TreeSet<>();
                for (String[] row : whole.table(table).rows) {
                    source.add(String.join("\u0000", row));
                }
                for (String[] row : slice.table(table).rows) {
                    Check.that(source.contains(String.join("\u0000", row)), table + ": a slice row is an exact copy of a Knowledge Base row");
                }
            }
        }
        // The schema tables describe exactly what the folder holds.
        Check.equal(new TreeSet<>(files), new TreeSet<>(TestSupport.column(TestSupport.table(folder, "schema_tables"), "table")),
                "schema_tables is limited to the tables of the slice");
        Check.equal(new TreeSet<>(files), new TreeSet<>(TestSupport.column(TestSupport.table(folder, "schema"), "table")),
                "schema is limited to the tables of the slice");
        for (String file : files) {
            List<String> columns = new ArrayList<>();
            for (String[] row : TestSupport.rows(TestSupport.table(folder, "schema"), "table", file)) {
                columns.add(row[2]);
                Check.that(row[3].length() > 10, file + "." + row[2] + ": a real meaning");
            }
            Check.equal(TestSupport.table(folder, file).header, columns, file + ": schema columns are the file's header");
        }
    }

    public void testTwoNamesGiveOneFolderEachAndAllCutsEveryone() {
        Path work = TestSupport.work("slice-two");
        SliceFixture fixture = SliceFixture.estate("two").area().column("Amount", true, "");
        fixture.add("presentation_subject_areas", "row_id", fixture.pres("Other Area!"), "name", "Other Area!");
        fixture.add("presentation_tables", "row_id", fixture.pres("Other Area!.T"), "subject_area_id", fixture.pres("Other Area!"), "name", "T");
        Path kb = fixture.write(work.resolve("kb"));
        Path out = work.resolve("slices");
        TestSupport.Run run = SliceFixture.slice(kb, out, "--subject-area", "SA", "--subject-area", fixture.pres("Other Area!"));
        Check.equal(0, run.exit, "exit code: " + run.err);
        Check.equal("SA", SliceFixture.read(out.resolve("sa")).fact("subject_area"), "first slice");
        Check.equal("Other Area!", SliceFixture.read(out.resolve("other-area")).fact("subject_area"), "second slice, named by Row identifier");
        Check.equal(1, SliceFixture.read(out.resolve("sa")).table("presentation_tables").rows.size(), "each holds only its own tables");

        Path all = work.resolve("all");
        Check.equal(0, SliceFixture.slice(kb, all, "--all").exit, "--all");
        Check.that(Files.isRegularFile(all.resolve("sa/manifest.csv")) && Files.isRegularFile(all.resolve("other-area/manifest.csv")),
                "--all writes one folder per subject area");
        Check.equal(2, SliceFixture.slice(kb, all, "--all", "--subject-area", "SA").exit, "--all with a name is wrong usage");
    }

    public void testFolderNameIsLowerCaseWithOtherCharactersAsOneHyphen() {
        Check.equal("sample-sales", Slice.folderName("Sample Sales"), "a space");
        Check.equal("densification-us-crime-by-state", Slice.folderName("Densification - US Crime By State"), "a run of other characters");
        Check.equal("a-b", Slice.folderName("  A__b!! "), "none at either end");
        Check.equal("subject-area", Slice.folderName("***"), "a name with no letter or digit");
    }

    public void testRerunIsByteIdenticalApartFromTheManifest() throws Exception {
        Path work = TestSupport.work("slice-rerun");
        Path kb = pairKb(work);
        Path first = work.resolve("one");
        Path second = work.resolve("two");
        Check.equal(0, TestSupport.run("slice", kb.toString(), "--all", "--out", first.toString(), "--timestamp", "one").exit, "first run");
        Check.equal(0, TestSupport.run("slice", kb.toString(), "--all", "--out", second.toString(), "--timestamp", "two").exit, "second run");
        // Once more over the first folder with a stray packet in it: rewriting gives the same as writing.
        TestSupport.write(first.resolve("constructed-sa/packets/left-over.md"), "old\n");
        Check.equal(0, TestSupport.run("slice", kb.toString(), "--all", "--out", first.toString(), "--timestamp", "three").exit, "third run");
        for (String sub : new String[] {"constructed-sa", "constructed-sa/packets"}) {
            Check.equal(TestSupport.fileNames(first.resolve(sub)), TestSupport.fileNames(second.resolve(sub)), sub + ": same files");
            for (String file : TestSupport.fileNames(first.resolve(sub))) {
                if (!file.equals("manifest.csv")) {
                    Check.sameBytes(Files.readAllBytes(first.resolve(sub).resolve(file)), Files.readAllBytes(second.resolve(sub).resolve(file)), sub + "/" + file);
                }
            }
        }
        Check.that(!SliceFixture.text(first.resolve("constructed-sa/manifest.csv"))
                .equals(SliceFixture.text(second.resolve("constructed-sa/manifest.csv"))), "only the manifest carries the time stamp");
    }

    public void testAFolderThisProgramDidNotWriteIsRefused() {
        Path work = TestSupport.work("slice-refuse");
        Path kb = pairKb(work);
        Path out = work.resolve("slices");
        TestSupport.write(out.resolve("constructed-sa/notes.txt"), "mine");
        TestSupport.Run run = SliceFixture.slice(kb, out, "--subject-area", "Constructed SA");
        Check.equal(2, run.exit, "exit code");
        Check.contains(run.err, "refusing to empty", "says why");
        Check.equal(List.of("notes.txt"), TestSupport.fileNames(out.resolve("constructed-sa")), "the folder is untouched");
    }

    // ------------------------------------------------------------------ the walk

    public void testEveryReferenceInsideASliceResolvesInsideIt() {
        int slices = 0;
        for (String fixture : new String[] {"testrpd", "pair", "linked"}) {
            Path work = TestSupport.work("slice-closed-" + fixture);
            Path kb = fixture.equals("pair") ? pairKb(work) : fixture.equals("linked") ? linkedKb(work) : work.resolve("kb");
            if (fixture.equals("testrpd")) {
                TestSupport.extract(TestSupport.estateFrom(work, "testrpd", TestSupport.testRpd()), kb);
            }
            slices += checkEverySlice(kb, work.resolve("slices"), fixture);
        }
        Check.that(slices >= 3, "slices checked: " + slices);
    }

    /** Cuts every subject area and checks each slice; returns how many. */
    static int checkEverySlice(Path kb, Path out, String what) {
        TestSupport.Run run = SliceFixture.slice(kb, out, "--all");
        Check.that(run.exit == 0 || run.exit == 3, what + ": slice --all: " + run.err);
        Set<String> all = SliceFixture.rowIds(SliceFixture.read(kb));
        int slices = 0;
        for (String[] area : Slice.subjectAreas(SliceFixture.read(kb))) {
            Path folder = out.resolve(Slice.folderName(area[2]));
            String name = what + "/" + area[2];
            Check.equal(List.of(), SchemaTest.valueProblems(folder), name + ": every declared link resolves inside the slice");
            KbData slice = SliceFixture.read(folder);
            Check.equal(List.of(), SliceFixture.danglingReferences(slice, all),
                    name + ": every reference-list entry and polymorphic link that exists resolves inside the slice");
            SliceFixture.checkPackets(folder, name);
            Check.equal(SliceFixture.expectedRequired(slice), SliceFixture.required(slice), name + ": the required rows");
            slices++;
        }
        return slices;
    }

    public void testDifference13HierarchiesQueryDefinitionsAndFormulaOnlyColumns() {
        SliceFixture f = SliceFixture.estate("d13").area().column("Amount", true, "");
        // A column shown to authors whose formula names a column nobody is shown, in another logical table.
        f.column("Converted", true, "\"%1\" * 2");
        f.add("logical_tables", "row_id", f.bmm("BM.Hidden"), "business_model", "BM", "name", "Hidden");
        f.add("logical_columns", "row_id", f.bmm("BM.Hidden.Rate"), "table_id", f.bmm("BM.Hidden"), "name", "Rate", "is_measure", "true");
        f.add("logical_columns", "row_id", f.bmm("BM.Hidden.Unused"), "table_id", f.bmm("BM.Hidden"), "name", "Unused");
        f.add("logical_table_sources", "row_id", f.bmm("BM.Hidden.src.H"), "logical_table_id", f.bmm("BM.Hidden"), "name", "H");
        f.add("physical_tables", "row_id", f.phys("DB.RATES"), "container", "DB", "name", "RATES", "table_type", "select");
        f.add("physical_table_definitions", "row_id", f.phys("DB.RATES.def.D"), "table_id", f.phys("DB.RATES"), "database", "D",
                "text", "select 1 from dual");
        f.add("physical_columns", "row_id", f.phys("DB.RATES.RATE"), "table_id", f.phys("DB.RATES"), "name", "RATE");
        f.add("lts_column_map", "row_id", f.bmm("BM.Hidden.src.H#map0"), "source_id", f.bmm("BM.Hidden.src.H"),
                "logical_column_id", f.bmm("BM.Hidden.Rate"), "physical_column_id", f.phys("DB.RATES.RATE"));
        f.add("aggregation_rules", "row_id", f.bmm("BM.Hidden.Rate#aggr0"), "logical_column_id", f.bmm("BM.Hidden.Rate"), "is_default", "true");
        // A drill path offered to authors, and one that only exists in the business model on the same table.
        f.add("logical_dimensions", "row_id", f.bmm("BM.Shown Dim"), "name", "Shown Dim", "business_model", "BM");
        f.add("logical_levels", "row_id", f.bmm("BM.Shown Dim.Total"), "dimension_id", f.bmm("BM.Shown Dim"), "position", "0", "name", "Total");
        f.add("logical_dimensions", "row_id", f.bmm("BM.Model Dim"), "name", "Model Dim", "business_model", "BM");
        f.add("logical_levels", "row_id", f.bmm("BM.Model Dim.Detail"), "dimension_id", f.bmm("BM.Model Dim"), "position", "1",
                "name", "Detail", "drill_key_column_id", f.bmm("BM.F.Amount"));
        f.add("logical_dimensions", "row_id", f.bmm("BM.Elsewhere Dim"), "name", "Elsewhere Dim", "business_model", "BM");
        f.add("presentation_hierarchies", "row_id", f.pres("SA.T.H"), "table_id", f.pres("SA.T"), "name", "H", "dimension_id", f.bmm("BM.Shown Dim"));
        f.add("presentation_levels", "row_id", f.pres("SA.T.H.Total"), "hierarchy_id", f.pres("SA.T.H"), "position", "0", "name", "Total",
                "logical_level_id", f.bmm("BM.Shown Dim.Total"));
        f.add("presentation_level_columns", "row_id", f.pres("SA.T.H.Total.0"), "level_id", f.pres("SA.T.H.Total"), "position", "0",
                "presentation_column_id", f.pres("SA.T.Amount"));
        KbData kb = f.build();
        // Point the formula at the hidden column.
        int refs = kb.table("logical_columns").column("derived_expr_refs");
        for (String[] row : kb.table("logical_columns").rows) {
            if (row[0].equals(f.bmm("BM.F.Converted"))) {
                row[refs] = f.bmm("BM.F.Amount") + "\n" + f.bmm("BM.Hidden.Rate");
            }
        }
        KbData slice = Slice.cut(kb, f.pres("SA"));

        Check.that(ids(slice, "logical_columns").contains(f.bmm("BM.Hidden.Rate")), "a column named only in a formula is in the slice");
        Check.that(ids(slice, "logical_columns").contains(f.bmm("BM.Hidden.Unused")),
                "and so is every other column of its logical table (difference 25)");
        Check.that(ids(slice, "logical_tables").contains(f.bmm("BM.Hidden")), "with its logical table");
        Check.equal(Set.of(f.bmm("BM.F.src.S"), f.bmm("BM.Hidden.src.H")), ids(slice, "logical_table_sources"), "its sources");
        Check.that(ids(slice, "lts_column_map").contains(f.bmm("BM.Hidden.src.H#map0")), "its mapping");
        Check.that(ids(slice, "aggregation_rules").contains(f.bmm("BM.Hidden.Rate#aggr0")), "its aggregation rule");
        Check.that(ids(slice, "physical_columns").contains(f.phys("DB.RATES.RATE")), "its physical column");
        Check.equal(Set.of(f.phys("DB.RATES.def.D")), ids(slice, "physical_table_definitions"), "the definition of a table that is a query");
        Check.equal(Set.of(f.bmm("BM.Model Dim"), f.bmm("BM.Shown Dim")), ids(slice, "logical_dimensions"),
                "dimensions shown as a hierarchy or keyed on a table of the slice, and no other");
        Check.equal(2, slice.table("logical_levels").rows.size(), "their levels");
        Check.equal(1, slice.table("presentation_hierarchies").rows.size(), "the presentation hierarchy");
        Check.equal(1, slice.table("presentation_levels").rows.size(), "its levels");
        Check.equal(1, slice.table("presentation_level_columns").rows.size(), "and their columns");
    }

    public void testALogicalTableComesWholeWithTheColumnsTheSubjectAreaDoesNotShow() {
        SliceFixture f = SliceFixture.estate("whole").area().column("Amount", true, "");
        // A measure of F that no presentation column of SA shows, mapped to its own physical table, and a
        // plain column whose formula names a variable. Another subject area shows the measure.
        f.add("logical_columns", "row_id", f.bmm("BM.F.Hidden Measure"), "table_id", f.bmm("BM.F"), "name", "Hidden Measure", "is_measure", "true");
        f.add("aggregation_rules", "row_id", f.bmm("BM.F.Hidden Measure#aggr0"), "logical_column_id", f.bmm("BM.F.Hidden Measure"), "is_default", "true");
        f.add("physical_tables", "row_id", f.phys("DB.Q"), "container", "DB", "name", "Q");
        f.add("physical_columns", "row_id", f.phys("DB.Q.H"), "table_id", f.phys("DB.Q"), "name", "H");
        f.add("lts_column_map", "row_id", f.bmm("BM.F.src.S#mapH"), "source_id", f.bmm("BM.F.src.S"), "logical_column_id",
                f.bmm("BM.F.Hidden Measure"), "physical_column_id", f.phys("DB.Q.H"));
        f.add("logical_columns", "row_id", f.bmm("BM.F.Hidden Plain"), "table_id", f.bmm("BM.F"), "name", "Hidden Plain", "is_measure", "false",
                "derived_expr_text_desc", "VALUEOF(RATE)");
        f.add("variables", "row_id", f.var("RATE"), "name", "RATE");
        f.add("presentation_subject_areas", "row_id", f.pres("Other"), "name", "Other");
        f.add("presentation_tables", "row_id", f.pres("Other.T"), "subject_area_id", f.pres("Other"), "name", "T");
        f.add("presentation_columns", "row_id", f.pres("Other.T.Hidden Measure"), "table_id", f.pres("Other.T"), "name", "Hidden Measure",
                "logical_column_id", f.bmm("BM.F.Hidden Measure"));
        f.add("lineage", "row_id", f.pres("Other.T.Hidden Measure") + "#" + f.bmm("BM.F.src.S"), "presentation_column_id",
                f.pres("Other.T.Hidden Measure"), "logical_column_id", f.bmm("BM.F.Hidden Measure"), "source_id", f.bmm("BM.F.src.S"));
        f.add("security_rls", "row_id", "whole::sec::obj.P.o0", "kind", "object_privilege", "detail", "read via whole::sec::pkg.X");
        f.add("security_rls", "row_id", "whole::sec::pkg.X.q0", "kind", "query_privilege", "role", "R", "detail", "X");
        f.add("init_blocks", "row_id", f.var("init.Unused"), "name", "Unused");
        f.add("variables", "row_id", f.var("NOT_NAMED"), "name", "NOT_NAMED");
        KbData kb = f.build();
        KbData slice = Slice.cut(kb, f.pres("SA"));
        Check.equal(Set.of(f.bmm("BM.F.Amount"), f.bmm("BM.F.Hidden Measure"), f.bmm("BM.F.Hidden Plain")), ids(slice, "logical_columns"),
                "every column of the logical table");
        Check.that(ids(slice, "aggregation_rules").contains(f.bmm("BM.F.Hidden Measure#aggr0")), "its aggregation rule");
        Check.that(ids(slice, "lts_column_map").contains(f.bmm("BM.F.src.S#mapH")), "its mapping");
        Check.that(ids(slice, "physical_columns").contains(f.phys("DB.Q.H")) && ids(slice, "physical_tables").contains(f.phys("DB.Q")),
                "the physical column and table it maps to");
        Check.equal(Set.of(f.var("RATE")), ids(slice, "variables"), "a variable its formula names");
        Check.equal(1, slice.table("presentation_subject_areas").rows.size(), "the other subject area that shows it is not pulled in");
        Check.equal(1, slice.table("lineage").rows.size(), "lineage stays that of this subject area's presentation columns");
        Check.equal(List.of(), SliceFixture.danglingReferences(slice, SliceFixture.rowIds(kb)), "every reference still resolves");

        List<obi2pbi.core.PacketUnit> units = obi2pbi.core.Packets.build(slice);
        obi2pbi.core.PacketUnit table = units.get(1);
        Check.that(table.requiredRows.containsKey(f.bmm("BM.F.Hidden Measure")), "a measure not shown here is a required row like any other");
        Check.that(!table.requiredRows.containsKey(f.bmm("BM.F.Hidden Plain")), "a plain column is not");
        Check.contains(table.text(), "- Logical column \"Hidden Measure\": MEASURE; writeable not recorded; mapped in  of  table sources. [kb:"
                + f.bmm("BM.F.Hidden Measure") + "] (REQUIRED)\n    - NOT SHOWN IN THIS SUBJECT AREA: it has no presentation column here.", "and the packet says so plainly");
        String overview = units.get(0).text();
        Check.contains(overview, "## Estate-wide items not in this slice", "the overview lists what is in no slice");
        Check.contains(overview, "Object permissions whose target was not recorded: 1. Find them in security_rls: kind = object_privilege and subject_id empty.",
                "permissions with no target, counted, with where to find them");
        Check.contains(overview, "Role privileges of packages that no permission of this slice goes through: 1.", "role rows");
        Check.contains(overview, "Init blocks that set no variable of this slice: 1.", "init blocks");
        Check.contains(overview, "Variables that no expression of this slice names: 1.", "variables");
        Check.equal(Set.of(f.pres("SA.T")), units.get(0).requiredRows.keySet(), "none of them is a required row");
    }

    /** Deliberate difference 33: a physical table of the slice comes whole, and keys come with their owners. */
    public void testAPhysicalTableComesWholeWithItsKeysAndSoDoTheKeysOfLogicalTablesAndLevels() {
        SliceFixture f = SliceFixture.estate("keys").area().column("Amount", true, "");
        // A column of P nothing maps (a join column), a key of P on it, and a table nothing reads.
        f.add("physical_columns", "row_id", f.phys("DB.P.MTH_KEY"), "table_id", f.phys("DB.P"), "name", "MTH_KEY", "position", "2");
        f.add("physical_keys", "row_id", f.phys("DB.P.key.P_Key"), "table_id", f.phys("DB.P"), "name", "P_Key", "columns", f.phys("DB.P.MTH_KEY"));
        f.add("physical_tables", "row_id", f.phys("DB.OUT"), "container", "DB", "name", "OUT");
        f.add("physical_columns", "row_id", f.phys("DB.OUT.ID"), "table_id", f.phys("DB.OUT"), "name", "ID");
        f.add("physical_keys", "row_id", f.phys("DB.OUT.key.OUT_Key"), "table_id", f.phys("DB.OUT"), "name", "OUT_Key", "columns", f.phys("DB.OUT.ID"));
        // A key of the logical table, a dimension keyed on it with a level that has two keys, and keys elsewhere.
        f.add("logical_keys", "row_id", f.bmm("BM.F.key.F_Key"), "logical_table_id", f.bmm("BM.F"), "name", "F_Key", "is_primary", "true",
                "columns", f.bmm("BM.F.Amount"));
        f.add("logical_keys", "row_id", f.bmm("BM.Other.key.O_Key"), "logical_table_id", f.bmm("BM.Other"), "name", "O_Key");
        f.add("logical_dimensions", "row_id", f.bmm("BM.D"), "name", "D", "business_model", "BM", "level_count", "1", "is_time", "true");
        f.add("logical_levels", "row_id", f.bmm("BM.D.L"), "dimension_id", f.bmm("BM.D"), "position", "0", "name", "L",
                "drill_key_column_id", f.bmm("BM.F.Amount"));
        f.add("logical_level_keys", "row_id", f.bmm("BM.D.L.key.Shown"), "level_id", f.bmm("BM.D.L"), "name", "Shown",
                "is_for_drill_down", "true", "columns", f.bmm("BM.F.Amount"));
        f.add("logical_level_keys", "row_id", f.bmm("BM.D.L.key.Id"), "level_id", f.bmm("BM.D.L"), "name", "Id", "is_primary", "true",
                "is_chronological", "true", "columns", f.bmm("BM.F.Amount"));
        f.add("logical_level_keys", "row_id", f.bmm("BM.Else.L.key.K"), "level_id", f.bmm("BM.Else.L"), "name", "K");
        KbData kb = f.build();
        KbData slice = Slice.cut(kb, f.pres("SA"));
        Check.equal(Set.of(f.phys("DB.P.Amount"), f.phys("DB.P.MTH_KEY")), ids(slice, "physical_columns"),
                "every column of a physical table of the slice, also the one no logical column maps; nothing of a table outside");
        Check.equal(Set.of(f.phys("DB.P.key.P_Key")), ids(slice, "physical_keys"), "the keys of the slice's physical tables, and only those");
        Check.equal(Set.of(f.bmm("BM.F.key.F_Key")), ids(slice, "logical_keys"), "the keys of the slice's logical tables, and only those");
        Check.equal(Set.of(f.bmm("BM.D.L.key.Id"), f.bmm("BM.D.L.key.Shown")), ids(slice, "logical_level_keys"),
                "every key of a level of the slice, and only those");
        Check.equal(List.of(), SliceFixture.danglingReferences(slice, SliceFixture.rowIds(kb)), "every reference resolves inside the slice");
        Check.equal("0", slice.fact("boundary_rows"), "none of this is a boundary row");
    }

    public void testVariablesAreMatchedByExactNameAndNeverByAPartOfIt() {
        Check.equal(List.of("CUR", "LANG", "A B", "X"), Variables.namesIn(
                "CASE VALUEOF(NQ_SESSION.\"CUR\") WHEN 1 THEN valueof( NQ_SESSION.LANG ) ELSE VALUEOF(\"A B\") + ValueOf(X) END"),
                "the four ways a variable is read");
        Check.equal(List.of(), Variables.namesIn("\"T\".\"CUR\" + CURRENT_DATE + 'VALUEOF'"), "a name outside VALUEOF is not a variable");

        SliceFixture f = SliceFixture.estate("vars").area().column("Amount", true, "VALUEOF(NQ_SESSION.\"CUR\") * \"%1\"");
        f.add("variables", "row_id", f.var("CUR"), "name", "CUR", "is_session", "true", "init_block_id", f.var("init.Set CUR"));
        f.add("variables", "row_id", f.var("CUR_OLD"), "name", "CUR_OLD");
        f.add("variables", "row_id", f.var("C"), "name", "C");
        f.add("variables", "row_id", f.var("Amount"), "name", "Amount");
        f.add("variables", "row_id", f.var("BY_REF"), "name", "BY_REF");
        f.add("variables", "row_id", f.var("IN_INIT"), "name", "IN_INIT");
        f.add("init_blocks", "row_id", f.var("init.Set CUR"), "name", "Set CUR", "init_string", "select 'VALUEOF(IN_INIT)' from dual");
        f.add("init_blocks", "row_id", f.var("init.Other"), "name", "Other");
        KbData kb = f.build();
        int refs = kb.table("lts_column_map").column("physical_expr_refs");
        kb.table("lts_column_map").rows.get(0)[refs] = f.phys("DB.P.Amount") + "\n" + f.var("BY_REF");
        KbData slice = Slice.cut(kb, f.pres("SA"));
        Check.equal(Set.of(f.var("CUR"), f.var("BY_REF"), f.var("IN_INIT")), ids(slice, "variables"),
                "named exactly in VALUEOF, referred to by identifier, or named by an init block of the slice; "
                        + "not CUR_OLD, not C, and not a variable that shares a column's name");
        Check.equal(Set.of(f.var("init.Set CUR")), ids(slice, "init_blocks"), "the init block that sets a variable of the slice");
    }

    public void testSecurityRowsAboutAnyRowOfTheSliceAndTheRolesBehindThem() {
        SliceFixture f = SliceFixture.estate("sec").area().column("Amount", true, "");
        f.add("security_rls", "row_id", f.bmm("BM.F.src.S#filter"), "kind", "row_filter", "subject_id", f.bmm("BM.F"), "detail", "S",
                "expr_text_desc", "\"P\".\"REGION\" = VALUEOF(NQ_SESSION.REGION)");
        f.add("security_rls", "row_id", "sec::sec::obj.P1.o0", "kind", "object_privilege", "subject_id", f.pres("SA.T.Amount"),
                "detail", "read via sec::sec::pkg.Pack");
        f.add("security_rls", "row_id", "sec::sec::pkg.Pack.q0", "kind", "query_privilege", "role", "Analysts", "subject_id", "QP", "detail", "Pack");
        f.add("security_rls", "row_id", "sec::sec::obj.P2.o0", "kind", "object_privilege", "subject_id", "sec::pres::Elsewhere.T.C",
                "detail", "read via sec::sec::pkg.Other");
        f.add("security_rls", "row_id", "sec::sec::pkg.Other.q0", "kind", "query_privilege", "role", "Others", "subject_id", "QP", "detail", "Other");
        f.add("variables", "row_id", f.var("REGION"), "name", "REGION");
        KbData slice = Slice.cut(f.build(), f.pres("SA"));
        Check.equal(Set.of(f.bmm("BM.F.src.S#filter"), "sec::sec::obj.P1.o0", "sec::sec::pkg.Pack.q0"), ids(slice, "security_rls"),
                "the row filter on a table of the slice, the permission on one of its columns, and the role of that package");
        Check.equal(Set.of(f.var("REGION")), ids(slice, "variables"), "a variable named by a row filter");
    }

    public void testJoinsBothEndsForPhysicalEitherEndForLogicalAndTheFarTableComesAlong() {
        SliceFixture f = SliceFixture.estate("joins").area().column("Amount", true, "");
        f.add("logical_tables", "row_id", f.bmm("BM.Outside"), "business_model", "BM", "name", "Outside");
        f.add("bmm_joins", "row_id", f.bmm("joins.J1"), "name", "J1", "table1_id", f.bmm("BM.F"), "table2_id", f.bmm("BM.Outside"));
        f.add("bmm_joins", "row_id", f.bmm("joins.J2"), "name", "J2", "table1_id", f.bmm("BM.Outside"), "table2_id", f.bmm("BM.Outside"));
        f.add("physical_tables", "row_id", f.phys("DB.FAR"), "container", "DB", "name", "FAR");
        f.add("physical_tables", "row_id", f.phys("DB.ALIAS_SOURCE"), "container", "DB", "name", "ALIAS_SOURCE");
        f.add("physical_columns", "row_id", f.phys("DB.P.KEY"), "table_id", f.phys("DB.P"), "name", "KEY");
        f.add("physical_joins", "row_id", f.phys("DB.P.Self"), "kind", "foreign_key", "name", "Self", "table1_id", f.phys("DB.P"),
                "columns", f.phys("DB.P.KEY"));
        f.add("physical_joins", "row_id", f.phys("DB.P.Out"), "kind", "foreign_key", "name", "Out", "table1_id", f.phys("DB.P"),
                "table2_id", f.phys("DB.FAR"));
        KbData kb = f.build();
        // P is an alias of ALIAS_SOURCE.
        kb.table("physical_tables").rows.get(0)[kb.table("physical_tables").column("source_table_id")] = f.phys("DB.ALIAS_SOURCE");
        KbData slice = Slice.cut(kb, f.pres("SA"));
        Check.equal(Set.of(f.bmm("joins.J1")), ids(slice, "bmm_joins"), "a logical join with either end in the slice");
        Check.that(ids(slice, "logical_tables").contains(f.bmm("BM.Outside")), "the table at its far end is in the slice, so the join can be read");
        Check.equal("1", slice.fact("boundary_rows"), "and is counted as a row that is there only to be pointed at");
        Check.equal(Set.of(f.phys("DB.P.Self")), ids(slice, "physical_joins"), "a physical join only with both ends in the slice");
        Check.that(ids(slice, "physical_columns").contains(f.phys("DB.P.KEY")), "its foreign-key column comes along");
        Check.equal(Set.of(f.phys("DB.P"), f.phys("DB.ALIAS_SOURCE")), ids(slice, "physical_tables"), "the table an alias stands for comes along");
    }

    public void testErrorsAndUnparsedHoldOnlyEntriesAboutTheSlice() {
        SliceFixture f = SliceFixture.estate("gaps").area().column("Amount", true, "VALUEOF(SECRET)");
        f.add("variables", "row_id", f.var("SECRET"), "name", "SECRET", "redacted", "true");
        f.add("errors", "kind", "unresolved_reference", "source_id", f.bmm("BM.F.Amount"), "object_type", "LogicalColumn", "reason", "in");
        f.add("errors", "kind", "unresolved_reference", "source_id", f.bmm("BM.Elsewhere.X"), "object_type", "LogicalColumn", "reason", "out");
        f.add("unparsed", "kind", "unread_attribute", "object_type", "LogicalTable", "detail", "LogicalTable@x", "occurrences", "3");
        f.add("unparsed", "kind", "unread_attribute", "object_type", "PresentationHierarchy", "detail", "H@x", "occurrences", "1");
        f.add("unparsed", "kind", "unmodelled_type", "object_type", "User", "detail", "u", "occurrences", "1");
        f.add("unparsed", "kind", "redacted_value", "object_type", "Variable", "detail", "SECRET — value withheld", "occurrences", "1");
        f.add("unparsed", "kind", "redacted_value", "object_type", "Variable", "detail", "OTHER — value withheld", "occurrences", "1");
        KbData slice = Slice.cut(f.build(), f.pres("SA"));
        Check.equal(1, slice.table("errors").rows.size(), "errors about an object of the slice");
        Check.equal("in", slice.table("errors").get(slice.table("errors").rows.get(0), "reason"), "the right one");
        Check.equal(List.of("LogicalTable@x", "SECRET — value withheld"), slice.table("unparsed").records().stream()
                .map(r -> r.get("detail")).collect(java.util.stream.Collectors.toList()),
                "gaps for kinds of object the slice holds, and a withheld value of one of its variables");
        Check.equal("1 (1 unresolved_reference)", slice.fact("errors_not_copied"), "what was left out is counted in the manifest");
        Check.equal("3 (1 redacted_value, 1 unmodelled_type, 1 unread_attribute)", slice.fact("unparsed_not_copied"), "by kind");

        Path work = TestSupport.work("slice-errors");
        TestSupport.Run run = SliceFixture.slice(f.write(work.resolve("kb")), work.resolve("slices"), "--subject-area", "SA");
        Check.equal(3, run.exit, "a slice whose errors.csv has rows exits 3");
    }

    public void testReportsBuiltOnTheSubjectAreaComeWithTheirPagesAndSavedFilters() {
        Path work = TestSupport.work("slice-reports");
        Path kb = pairKb(work);
        Check.equal(0, SliceFixture.slice(kb, work.resolve("slices"), "--subject-area", "Constructed SA").exit, "slice");
        KbData slice = SliceFixture.read(work.resolve("slices/constructed-sa"));
        String cat = "constructed-pair::cat::constructed/";
        Check.equal(Set.of(cat + "revenue by month"), ids(slice, "analyses"), "the report built on the subject area");
        Check.equal(1, slice.table("analysis_criteria").rows.size(), "its criteria block");
        Check.equal(2, slice.table("analysis_columns").rows.size(), "its columns");
        Check.equal(2, slice.table("report_lineage").rows.size(), "their links to the repository");
        Check.equal(Set.of(cat + "_portal/sales overview/page one"), ids(slice, "dashboard_pages"), "the page that places it");
        Check.equal(Set.of(cat + "_portal/sales overview/dashboard layout"), ids(slice, "dashboards"), "the page's dashboard");
        Check.equal(3, slice.table("dashboard_contents").rows.size(), "the page in the dashboard, the report on the page, the prompt on the page");
        Check.equal(Set.of(cat + "_filters/recent periods"), ids(slice, "saved_filters"), "the saved filter the report's filter names");
        Check.equal(Set.of("analysis", "dashboard", "saved_filter"), new TreeSet<>(slice.table("analysis_filters").records().stream()
                .map(r -> r.get("scope")).collect(java.util.stream.Collectors.toList())), "filter rows of the report, the page and the saved filter");
        String packet = SliceFixture.text(work.resolve("slices/constructed-sa/packets/reports-01-sales-overview.md"));
        Check.contains(packet, "Kind: reports", "a unit for the dashboard");
        Check.contains(packet, "formula: `\"Sales Facts\".\"Amount\"` [kb:" + cat + "revenue by month#c0.0]", "the report's columns, cited");
    }

    public void testAReportIsMatchedByTheSubjectAreaItNamesAndOtherAreasStayOut() {
        SliceFixture f = SliceFixture.estate("rep").area().column("Amount", true, "");
        f.add("presentation_subject_areas", "row_id", f.pres("Other"), "name", "Other");
        String[][] reports = {{"quoted", "\"SA\""}, {"bare", "SA"}, {"folded", "\"sa\""}, {"other", "\"Other\""}, {"dataset", "XSA('u'.'d')"},
                {"none", ""}};
        for (String[] report : reports) {
            String id = f.cat("shared/" + report[0]);
            f.add("analyses", "row_id", id, "name", report[0], "catalog_path", "shared/" + report[0], "criteria_count", "1", "column_count", "1");
            f.add("analysis_criteria", "row_id", id + "#criteria0", "analysis_id", id, "position", "0", "subject_area", report[1]);
            f.add("analysis_columns", "row_id", id + "#c0.0", "analysis_id", id, "criteria_id", id + "#criteria0", "position", "0",
                    "formula", "\"T\".\"Amount\"", "is_computation", "false");
            f.add("report_lineage", "row_id", id + "#c0.0#lineage", "analysis_id", id, "criteria_id", id + "#criteria0",
                    "analysis_column_id", id + "#c0.0", "resolution", "exact");
            f.add("analysis_views", "row_id", id + "#view0", "analysis_id", id, "position", "0", "view_type", "saw:tableView");
            f.add("analysis_filters", "row_id", id + "#f0.0", "scope", "analysis", "owner_id", id, "position", "0", "op", "and", "depth", "0");
        }
        // A page with one report of the slice, one report of another area, and a prompt.
        f.add("dashboards", "row_id", f.cat("shared/_portal/D/dashboard layout"), "name", "D", "catalog_path", "shared/_portal/D/dashboard layout");
        f.add("dashboard_pages", "row_id", f.cat("shared/_portal/D/p1"), "dashboard_id", f.cat("shared/_portal/D/dashboard layout"), "position", "0", "name", "p1");
        f.add("dashboard_pages", "row_id", f.cat("shared/_portal/D/p2"), "dashboard_id", f.cat("shared/_portal/D/dashboard layout"), "position", "1", "name", "p2");
        f.add("dashboard_contents", "row_id", f.cat("shared/_portal/D/dashboard layout#page0"), "container_id", f.cat("shared/_portal/D/dashboard layout"),
                "element", "{d}dashboardPageRef", "target_id", f.cat("shared/_portal/D/p1"));
        f.add("dashboard_contents", "row_id", f.cat("shared/_portal/D/dashboard layout#page1"), "container_id", f.cat("shared/_portal/D/dashboard layout"),
                "element", "{d}dashboardPageRef", "target_id", f.cat("shared/_portal/D/p2"));
        f.add("dashboard_contents", "row_id", f.cat("shared/_portal/D/p1#item0"), "container_id", f.cat("shared/_portal/D/p1"), "element", "{d}reportRef",
                "target_id", f.cat("shared/quoted"));
        f.add("dashboard_contents", "row_id", f.cat("shared/_portal/D/p1#item1"), "container_id", f.cat("shared/_portal/D/p1"), "element", "{d}reportRef",
                "target_id", f.cat("shared/other"));
        f.add("dashboard_contents", "row_id", f.cat("shared/_portal/D/p1#item2"), "container_id", f.cat("shared/_portal/D/p1"), "element", "{d}globalFilterView",
                "reference", "/shared/prompt");
        f.add("dashboard_contents", "row_id", f.cat("shared/_portal/D/p2#item0"), "container_id", f.cat("shared/_portal/D/p2"), "element", "{d}reportRef",
                "target_id", f.cat("shared/other"));
        KbData slice = Slice.cut(f.build(), f.pres("SA"));
        Check.equal(Set.of(f.cat("shared/quoted"), f.cat("shared/bare"), f.cat("shared/folded")), ids(slice, "analyses"),
                "in quotes, without quotes, and in another letter case; not another area, a dataset or none");
        Check.equal(3, slice.table("analysis_columns").rows.size(), "their columns");
        Check.equal(3, slice.table("analysis_views").rows.size(), "their views");
        Check.equal(3, slice.table("analysis_filters").rows.size(), "their filters");
        Check.equal(3, slice.table("report_lineage").rows.size(), "their links");
        Check.equal(Set.of(f.cat("shared/_portal/D/p1")), ids(slice, "dashboard_pages"), "only the page that places one of them");
        Check.equal(Set.of(f.cat("shared/_portal/D/dashboard layout#page0"), f.cat("shared/_portal/D/p1#item0"), f.cat("shared/_portal/D/p1#item2")),
                ids(slice, "dashboard_contents"), "the page reference, the placement and the prompt; not the other area's report");
        Check.equal(1, slice.table("presentation_subject_areas").rows.size(), "the other subject area is not pulled in");
    }

    // ------------------------------------------------------------------ the sample estates

    /**
     * With the sample estates in place: every subject area of the two repositories is cut and checked, and
     * the Sample Sales slice holds every row of the slice the first attempt produced.
     */
    public void testTheMostLinesOfAPacketIsAnOptionAndIsKeptInTheManifest() {
        SliceFixture f = SliceFixture.estate("limit").area();
        for (int i = 0; i < 40; i++) {
            f.column(String.format("C%02d", i), true, "");
        }
        Path work = TestSupport.work("slice-limit");
        Path kb = f.write(work.resolve("kb"));
        TestSupport.Run usual = SliceFixture.slice(kb, work.resolve("usual"), "--subject-area", "SA");
        Check.equal(0, usual.exit, "slice: " + usual.err);
        KbData byDefault = SliceFixture.read(work.resolve("usual/sa"));
        Check.equal("120", byDefault.fact("max_packet_lines"), "the default is in the manifest");
        int[] numbers = SliceFixture.checkPackets(work.resolve("usual/sa"), "default");
        Check.that(numbers[1] <= 120, "no packet over 120 lines: " + numbers[1]);
        Check.contains(usual.out, "limit 120)", "and is printed with the largest packet");

        TestSupport.Run small = SliceFixture.slice(kb, work.resolve("small"), "--subject-area", "SA", "--max-packet-lines", "50");
        Check.equal(0, small.exit, "slice: " + small.err);
        KbData cut = SliceFixture.read(work.resolve("small/sa"));
        Check.equal("50", cut.fact("max_packet_lines"), "the option is in the manifest");
        int[] smaller = SliceFixture.checkPackets(work.resolve("small/sa"), "small");
        Check.that(smaller[1] <= 50 && smaller[0] > numbers[0], "smaller packets, and more of them: " + smaller[0] + " against " + numbers[0]);
        Check.equal(numbers[2], smaller[2], "the same rows to cover, whatever the size");
        Check.equal(SliceFixture.required(byDefault), SliceFixture.required(cut), "the very same rows");
        // Built again from the folder alone, the packets are the same: the limit is read from the manifest.
        for (obi2pbi.core.PacketUnit unit : obi2pbi.core.Packets.build(cut)) {
            Check.equal(SliceFixture.packets(work.resolve("small/sa")).get(unit.unitId), unit.text(), unit.unitId);
        }
        for (String bad : new String[] {"39", "many", "-5"}) {
            TestSupport.Run refused = SliceFixture.slice(kb, work.resolve("bad"), "--subject-area", "SA", "--max-packet-lines", bad);
            Check.equal(2, refused.exit, "a limit of '" + bad + "' is wrong usage");
            Check.contains(refused.err, "--max-packet-lines needs a whole number of at least 40", "and says what is allowed");
        }
        Check.that(!Files.exists(work.resolve("bad")), "nothing is written");
    }

    public void testAForeignKeyIsInTheSliceOnlyWhenTheTableItPointsAtIs() {
        SliceFixture f = SliceFixture.estate("far").area().column("Amount", true, "");
        f.add("physical_tables", "row_id", f.phys("DB.OUT"), "container", "DB", "name", "OUT");
        f.add("physical_columns", "row_id", f.phys("DB.P.KEY"), "table_id", f.phys("DB.P"), "name", "KEY");
        f.add("physical_columns", "row_id", f.phys("DB.P.ID"), "table_id", f.phys("DB.P"), "name", "ID");
        f.add("physical_columns", "row_id", f.phys("DB.OUT.ID"), "table_id", f.phys("DB.OUT"), "name", "ID");
        f.add("physical_joins", "row_id", f.phys("DB.P.Self"), "kind", "foreign_key", "name", "Self", "table1_id", f.phys("DB.P"),
                "table2_id", f.phys("DB.P"), "columns", f.phys("DB.P.KEY"), "referenced_columns", f.phys("DB.P.ID"));
        f.add("physical_joins", "row_id", f.phys("DB.P.Away"), "kind", "foreign_key", "name", "Away", "table1_id", f.phys("DB.P"),
                "table2_id", f.phys("DB.OUT"), "columns", f.phys("DB.P.KEY"), "referenced_columns", f.phys("DB.OUT.ID"));
        KbData kb = f.build();
        KbData slice = Slice.cut(kb, f.pres("SA"));
        Check.equal(Set.of(f.phys("DB.P.Self")), ids(slice, "physical_joins"),
                "now that a foreign key says what it points at, one that points outside the slice stays out, like any physical join");
        Check.that(ids(slice, "physical_columns").containsAll(Set.of(f.phys("DB.P.KEY"), f.phys("DB.P.ID"))), "the columns at both ends come along");
        Check.that(!ids(slice, "physical_columns").contains(f.phys("DB.OUT.ID")), "and nothing of the table outside");
        Check.equal(List.of(), SliceFixture.danglingReferences(slice, SliceFixture.rowIds(kb)), "every reference resolves inside the slice");
    }

    public void testTheSampleEstatesSliceCleanlyAndSampleSalesHoldsEveryReferenceRow() {
        int estates = 0;
        for (String estate : new String[] {"sampleapp", "bi-analytics-2013"}) {
            Path folder = TestSupport.root().resolve("estates").resolve(estate);
            if (!Files.isDirectory(folder.resolve("rpd"))) {
                continue;
            }
            Path work = TestSupport.work("slice-sample-" + estate);
            Path kb = work.resolve("kb");
            TestSupport.extract(folder, kb);
            int slices = checkEverySlice(kb, work.resolve("slices"), estate);
            Check.equal(estate.equals("sampleapp") ? 3 : 18, slices, estate + ": subject areas cut");
            estates++;
            if (!estate.equals("sampleapp")) {
                continue;
            }
            Path reference = TestSupport.root().resolve("reference/analysis/sample-sales");
            KbData slice = SliceFixture.read(work.resolve("slices/sample-sales"));
            int compared = 0;
            for (String file : TestSupport.fileNames(reference)) {
                String table = file.substring(0, file.length() - 4);
                if (!file.endsWith(".csv") || !slice.has(table) || table.equals("manifest")) {
                    continue;
                }
                Set<String> have = ids(slice, table);
                obi2pbi.in.kb.CsvTable old = TestSupport.table(reference, table);
                for (String[] row : old.rows) {
                    // Difference 13: the first attempt matched variables across estates; this Knowledge
                    // Base holds one estate, so a row of another estate cannot be here.
                    if (old.value(row, "rpd_id").equals("sampleapp")) {
                        Check.that(have.contains(old.value(row, "row_id")), table + ": reference row is in the slice: " + old.value(row, "row_id"));
                        compared++;
                    }
                }
            }
            Check.equal(349, compared, "reference rows of this estate compared");
            // What difference 13 adds, by name.
            Check.that(ids(slice, "logical_columns").contains("sampleapp::bmm::SAMPLESALES.Fact Revenue.Revenue (EUR)"), "a formula-only column");
            Check.that(ids(slice, "logical_tables").contains("sampleapp::bmm::SAMPLESALES.Dim Products (Translated)"), "the table of a lookup");
            Check.equal(4, slice.table("logical_dimensions").rows.size(), "the four drill paths");
            Check.that(ids(slice, "physical_table_definitions").contains("sampleapp::phys::xe.SAMPLESALES.FactCrossJoin.def.DefaultMulDB"), "the SQL of the query table");
            Check.equal(Set.of("sampleapp::var::PREFERRED_CURRENCY"), ids(slice, "variables"), "the one variable its formulas name");
        }
        if (estates == 0) {
            throw new SkipException("the sample estates are not under estates/");
        }
    }
}
