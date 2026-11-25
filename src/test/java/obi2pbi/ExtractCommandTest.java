package obi2pbi;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import obi2pbi.in.kb.CsvTable;

/** The command line and the output folder (story 1.1 matrix; Architecture AD-14). */
public final class ExtractCommandTest {

    /**
     * Story 1.6 added the ten tables for joins, hierarchies, variables and security. Story 1.7 added five
     * for the Web Catalog; like the reference implementation, every table is written for every estate, with
     * a header and no rows when the estate has no catalog. Stories 1.8 and 1.9 added seven for reports,
     * filters, prompts and saved filters.
     */
    private static final List<String> FILES = List.of(
            "aggregation_rules.csv", "analyses.csv", "analysis_columns.csv", "analysis_criteria.csv",
            "analysis_filters.csv", "analysis_prompts.csv", "analysis_views.csv", "bmm_joins.csv", "catalog_versions.csv", "coverage.csv", "coverage_summary.csv",
            "dashboard_contents.csv", "dashboard_pages.csv", "dashboards.csv", "errors.csv", "init_blocks.csv", "lineage.csv",
            "logical_columns.csv", "logical_dimensions.csv", "logical_keys.csv", "logical_level_keys.csv", "logical_levels.csv", "logical_table_sources.csv",
            "logical_tables.csv", "lts_column_map.csv", "lts_joins.csv", "lts_tables.csv", "manifest.csv", "physical_columns.csv", "physical_joins.csv", "physical_keys.csv",
            "physical_table_definitions.csv", "physical_tables.csv", "presentation_columns.csv",
            "presentation_hierarchies.csv", "presentation_level_columns.csv", "presentation_levels.csv",
            "presentation_subject_areas.csv", "presentation_tables.csv", "report_lineage.csv", "saved_filters.csv", "schema.csv", "schema_tables.csv",
            "security_rls.csv", "unparsed.csv", "variables.csv");

    public void testHappyPathWritesTheTablesAndPrintsCounts() {
        Path work = TestSupport.work("cmd-happy");
        Path estate = TestSupport.estateFrom(work, "testrpd", TestSupport.testRpd());
        Path kb = work.resolve("out/kb");
        TestSupport.Run run = TestSupport.run("extract", estate.toString(), "--out", kb.toString());
        Check.equal(0, run.exit, "exit code: " + run.err);
        Check.equal(FILES, TestSupport.fileNames(kb), "files written");
        Check.contains(run.out, "estate:   testrpd (rpd)", "estate line");
        // Until story 1.6: 19 parsed and 4 skipped, the 4 being the two dimensions and two hierarchies.
        // Until difference 30: 28. The key of the level "Year" is a row of logical_level_keys now and is counted.
        Check.contains(run.out, "parsed:   29 objects",
                "parsed count: 19, and 2 dimensions, 3 logical levels, 1 level key, 2 hierarchies, 2 presentation levels");
        Check.contains(run.out, "skipped:  0 objects", "skipped count");
        Check.contains(run.out, "errors:   0", "error count");
        Check.contains(run.out, "written:  " + kb, "output folder");

        CsvTable manifest = TestSupport.table(kb, "manifest");
        Check.equal(List.of("extractor_version", "extracted_at", "corpus_root", "rpd_ids", "halves_present:testrpd",
                "pairing:testrpd", "tables"), TestSupport.column(manifest, "key"), "manifest keys in fixed order");
        Check.equal("testrpd", manifest.value(TestSupport.row(manifest, "key", "rpd_ids"), "value"), "estate name");
        Check.equal("rpd", manifest.value(TestSupport.row(manifest, "key", "halves_present:testrpd"), "value"), "halves");
        Check.that(manifest.value(TestSupport.row(manifest, "key", "extracted_at"), "value")
                .matches("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}Z"), "time stamp in UTC");
    }

    public void testFilesAreExactlyTheManifestsTablesPlusTheManifest() {
        Path work = TestSupport.work("cmd-shape");
        Path kb = work.resolve("kb");
        TestSupport.extract(TestSupport.estateFrom(work, "testrpd", TestSupport.testRpd()), kb);
        CsvTable manifest = TestSupport.table(kb, "manifest");
        List<String> expected = new ArrayList<>();
        for (String table : manifest.value(TestSupport.row(manifest, "key", "tables"), "value").split(" ")) {
            expected.add(table + ".csv");
        }
        expected.add("manifest.csv");
        expected.sort(null);
        Check.equal(expected, TestSupport.fileNames(kb), "files in the folder");
        for (String file : TestSupport.fileNames(kb)) {
            CsvTable table = TestSupport.table(kb, file.substring(0, file.length() - 4));
            Check.that(!table.header.isEmpty(), file + " has a header");
            for (String column : table.header) {
                Check.that(!column.endsWith("_guarded"), file + " has no _guarded column: " + column);
            }
        }
    }

    public void testRerunIsByteIdentical() throws Exception {
        Path work = TestSupport.work("cmd-rerun");
        Path estate = TestSupport.estateFrom(work, "testrpd", TestSupport.testRpd());
        Path first = work.resolve("kb1");
        Path second = work.resolve("kb2");
        Check.equal(0, TestSupport.run("extract", estate.toString(), "--out", first.toString(), "--timestamp", "one").exit, "first run");
        Check.equal(0, TestSupport.run("extract", estate.toString(), "--out", second.toString(), "--timestamp", "two").exit, "second run");
        // And once more over the first folder, to show that rewriting gives the same bytes as writing.
        Check.equal(0, TestSupport.run("extract", estate.toString(), "--out", first.toString(), "--timestamp", "two").exit, "third run");
        Check.equal(TestSupport.fileNames(first), TestSupport.fileNames(second), "same files");
        for (String file : TestSupport.fileNames(first)) {
            Check.sameBytes(Files.readAllBytes(first.resolve(file)), Files.readAllBytes(second.resolve(file)), file);
        }
    }

    public void testOnlyTheManifestCarriesTheTimeStamp() throws Exception {
        Path work = TestSupport.work("cmd-stamp");
        Path estate = TestSupport.estateFrom(work, "testrpd", TestSupport.testRpd());
        Path first = work.resolve("kb1");
        Path second = work.resolve("kb2");
        TestSupport.run("extract", estate.toString(), "--out", first.toString(), "--timestamp", "one");
        TestSupport.run("extract", estate.toString(), "--out", second.toString(), "--timestamp", "two");
        for (String file : TestSupport.fileNames(first)) {
            boolean same = java.util.Arrays.equals(Files.readAllBytes(first.resolve(file)), Files.readAllBytes(second.resolve(file)));
            Check.equal(!file.equals("manifest.csv"), same, file + " identical across runs with different time stamps");
        }
    }

    public void testNoRepositoryFoundIsWrongUsageAndWritesNothing() {
        Path work = TestSupport.work("cmd-nothing");
        Path estate = work.resolve("empty-estate");
        TestSupport.write(estate.resolve("notes.txt"), "not an estate");
        Path kb = work.resolve("kb");
        TestSupport.Run run = TestSupport.run("extract", estate.toString(), "--out", kb.toString());
        Check.equal(2, run.exit, "exit code");
        Check.contains(run.err, "holds neither rpd/ nor catalog/", "says what is missing");
        Check.contains(run.err, "<estate-folder>/rpd/oracle/bi/server/base/", "names the expected layout");
        Check.that(!Files.exists(kb), "nothing written");

        TestSupport.Run missing = TestSupport.run("extract", work.resolve("no-such-folder").toString(), "--out", kb.toString());
        Check.equal(2, missing.exit, "a folder that does not exist");
        Check.that(!Files.exists(kb), "nothing written");
    }

    public void testAnRpdFolderThatIsNotARepositoryIsWrongUsage() {
        Path work = TestSupport.work("cmd-not-a-repository");
        Path estate = work.resolve("hollow");
        TestSupport.write(estate.resolve("rpd/readme.txt"), "no oracle/bi/server/base here");
        Path kb = work.resolve("kb");
        TestSupport.Run run = TestSupport.run("extract", estate.toString(), "--out", kb.toString());
        Check.equal(2, run.exit, "exit code");
        Check.contains(run.err, "oracle/bi/server/base", "says what was expected");
        Check.that(!Files.exists(kb), "nothing written");
    }

    public void testABareRepositoryTreeGetsAHint() {
        TestSupport.Run run = TestSupport.run("extract", TestSupport.testRpd().toString(), "--out",
                TestSupport.work("cmd-bare").resolve("kb").toString());
        Check.equal(2, run.exit, "exit code");
        Check.contains(run.err, "looks like a repository export itself", "hint");
    }

    public void testEstateNameMustBeUsableInIdentifiers() {
        Path work = TestSupport.work("cmd-name");
        Path estate = TestSupport.estateFrom(work, "Bad_Name", TestSupport.testRpd());
        TestSupport.Run run = TestSupport.run("extract", estate.toString(), "--out", work.resolve("kb").toString());
        Check.equal(2, run.exit, "exit code");
        Check.contains(run.err, "Bad_Name", "names the offending folder");
        Check.that(!Files.exists(work.resolve("kb")), "nothing written");
    }

    public void testWrongCommandLinePrintsUsage() {
        TestSupport.Run none = TestSupport.run();
        Check.equal(2, none.exit, "no command");
        Check.contains(none.err, "usage:", "usage text");
        TestSupport.Run unknown = TestSupport.run("explode");
        Check.equal(2, unknown.exit, "unknown command");
        Check.contains(unknown.err, "unknown command 'explode'", "names it");
        Check.contains(unknown.err, "extract", "lists the commands");
        TestSupport.Run noFolder = TestSupport.run("extract");
        Check.equal(2, noFolder.exit, "missing argument");
        Check.contains(noFolder.err, "usage: java -jar obi2pbi.jar extract", "the command's usage");
        Check.equal(2, TestSupport.run("extract", "a", "--out").exit, "option without a value");
        Check.equal(2, TestSupport.run("extract", "a", "--frobnicate", "x").exit, "unknown option");
        Check.equal(2, TestSupport.run("extract", "a", "b").exit, "two folders");
        TestSupport.Run help = TestSupport.run("extract", "--help");
        Check.equal(0, help.exit, "--help is not an error");
        Check.contains(help.out, "--out <folder>", "help text on standard output");
        Check.equal(0, TestSupport.run("--help").exit, "top-level --help");
    }

    public void testAFolderThisJarDidNotWriteIsRefusedUntouched() throws Exception {
        Path work = TestSupport.work("cmd-not-ours");
        Path estate = TestSupport.estateFrom(work, "testrpd", TestSupport.testRpd());
        Path out = work.resolve("documents");
        TestSupport.write(out.resolve("budget.csv"), "mine\n");
        TestSupport.Run run = TestSupport.run("extract", estate.toString(), "--out", out.toString());
        Check.equal(2, run.exit, "exit code");
        Check.contains(run.err, "refusing to empty", "says why");
        Check.equal(List.of("budget.csv"), TestSupport.fileNames(out), "the folder is as it was");
        Check.equal("mine\n", new String(Files.readAllBytes(out.resolve("budget.csv")), "UTF-8"), "and so is the file");
    }

    public void testAKnowledgeBaseFolderIsRewrittenWholeAndOtherContentIsLeftAlone() {
        Path work = TestSupport.work("cmd-rewrite");
        Path estate = TestSupport.estateFrom(work, "testrpd", TestSupport.testRpd());
        Path kb = work.resolve("kb");
        Check.equal(0, TestSupport.extract(estate, kb).exit, "first run");
        TestSupport.write(kb.resolve("stale_table.csv"), "\"x\"\n");
        TestSupport.write(kb.resolve("notes.md"), "keep me");
        TestSupport.write(kb.resolve("sub/inner.csv"), "keep me too");
        Check.equal(0, TestSupport.extract(estate, kb).exit, "second run");
        Check.that(!Files.exists(kb.resolve("stale_table.csv")), "a stale table is removed");
        Check.that(Files.exists(kb.resolve("notes.md")), "a file that is not a table is left alone");
        Check.that(Files.exists(kb.resolve("sub/inner.csv")), "a subfolder is left alone");

        Path fresh = work.resolve("fresh");
        TestSupport.write(fresh.resolve(".gitignore"), "*\n");
        Check.equal(0, TestSupport.extract(estate, fresh).exit, "a folder holding only .gitignore counts as empty");
    }

    public void testTheJarRunsAndDefaultsToKbEstateName() throws Exception {
        Path jar = TestSupport.root().resolve("build/obi2pbi.jar");
        if (!Files.isRegularFile(jar)) {
            throw new SkipException("build/obi2pbi.jar is not built");
        }
        Path work = TestSupport.work("cmd-jar");
        Path estate = TestSupport.estateFrom(work, "testrpd", TestSupport.testRpd());
        String java = Path.of(System.getProperty("java.home"), "bin", "java").toString();
        Process process = new ProcessBuilder(java, "-jar", jar.toString(), "extract", estate.toString())
                .directory(work.toFile()).redirectErrorStream(true).start();
        String output = new String(process.getInputStream().readAllBytes(), "UTF-8");
        Check.equal(0, process.waitFor(), "exit code of java -jar: " + output);
        Check.equal(FILES, TestSupport.fileNames(work.resolve("kb/testrpd")), "written to kb/<estate> under the working folder");

        Process bad = new ProcessBuilder(java, "-jar", jar.toString(), "nonsense")
                .directory(work.toFile()).redirectErrorStream(true).start();
        bad.getInputStream().readAllBytes();
        Check.equal(2, bad.waitFor(), "the process exit code for wrong usage");
    }
}
