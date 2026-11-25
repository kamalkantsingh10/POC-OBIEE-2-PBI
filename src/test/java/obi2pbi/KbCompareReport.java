package obi2pbi;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import obi2pbi.KbCompare.Change;
import obi2pbi.KbCompare.Result;
import obi2pbi.in.kb.CsvReader;
import obi2pbi.in.kb.CsvTable;

/**
 * The full comparison of story 1.11: runs {@code extract} on the three sample estates, compares every table
 * of {@code reference/knowledge-base/} with what the jar wrote, and writes
 * {@code documents/implementation-artifacts/kb-comparison-report.md}.
 *
 * <pre>
 * ./build.sh compare-all [--out &lt;report file&gt;] [--no-extract]
 * </pre>
 *
 * <p>What it does, in order:
 * <ol>
 * <li>Runs {@code java -jar build/obi2pbi.jar extract estates/&lt;estate&gt;} for each sample estate whose folder
 * exists, writing {@code kb/&lt;estate&gt;}. An estate whose folder is missing is reported as not confirmed.</li>
 * <li>Compares every reference table with {@link KbCompare#compare}, and {@code manifest.csv} key by key
 * here.</li>
 * <li>Builds two small estates with both halves under {@code build/compare-all/} (the first attempt's
 * hand-made pair and this project's {@code linked-estate} fixture), runs the jar and, when Python is on
 * the machine, the reference implementation on each, and compares the two outputs. This is the only place
 * where report lineage resolves.</li>
 * <li>Writes the report and prints the totals. Exit code 0 when nothing is unexplained, 1 otherwise.</li>
 * </ol>
 * Nothing here decides whether a difference is acceptable: every difference is assigned by
 * {@link KbCompare}, which checks each one, or by the manifest rules written out in {@link #compareManifest}.
 */
public final class KbCompareReport {

    private static final List<String> SAMPLE_ESTATES = List.of("sampleapp", "bi-analytics-2013", "sampleapp-v607");
    private static final Path REFERENCE = Paths.get("reference", "knowledge-base");
    private static final Path WORK = Paths.get("build", "compare-all");
    private static final String GUARDED = "_guarded";
    private static final int EXAMPLES = 3;
    private static final int SHOWN = 140;

    /** One sentence for each deliberate difference this comparison can meet. The full list is in the port guide. */
    private static final Map<Integer, String> SENTENCE = new TreeMap<>();

    static {
        SENTENCE.put(1, "The reference has a `..._guarded` column beside every text column and puts an apostrophe in "
                + "front of a value a spreadsheet could alter; the jar writes plain CSV with neither.");
        SENTENCE.put(5, "The reference resolved references while it was still reading, so a reference to something "
                + "read later was dropped and reported as an error; the jar resolves after reading everything, so the "
                + "reference is filled in and the error row is gone.");
        SENTENCE.put(6, "A list of references in one cell (`..._refs`) was joined with spaces in the reference; the "
                + "jar writes one reference per line.");
        SENTENCE.put(7, "`physical_joins.columns` held OBIEE's internal ids in the reference; the jar writes Row "
                + "identifiers.");
        SENTENCE.put(8, "The reference stored a formula or filter text only up to its first nested element; the jar "
                + "stores all of it.");
        SENTENCE.put(9, "The reference kept only the outermost expression of a saved filter; the jar writes every "
                + "expression, resolves the references between saved filters, and no longer lists that content as "
                + "unread.");
        SENTENCE.put(10, "`analysis_filters` has two more columns, `parent_id` and `depth`, so the grouping of "
                + "conditions can be rebuilt.");
        SENTENCE.put(11, "The reference resolved each dashboard prompt reference twice, doubling its error rows and "
                + "skipped counts; the jar resolves it once.");
        SENTENCE.put(15, "The reference was written by one run over three estates; the jar is run once per estate, "
                + "so its manifest names one estate.");
        SENTENCE.put(16, "The jar writes two more tables, `schema_tables` and `schema`, which describe the Knowledge "
                + "Base; the reference has no such tables.");
        SENTENCE.put(17, "The manifest describes the program that wrote it: its own version number and the estate "
                + "folder it was given.");
        SENTENCE.put(19, "The reference wrote out a variable's value when the same value was a credential under "
                + "another variable's name; the jar withholds it and records that it did.");
        SENTENCE.put(21, "A catalog file whose name cannot be fully decoded was used without comment by the "
                + "reference; the jar adds a row to `unparsed` naming it.");
        SENTENCE.put(22, "Where repository lineage has several rows for a presentation column, the reference linked "
                + "a report column through the last one read; the jar uses the first in `lineage.csv` order.");
        SENTENCE.put(26, "A foreign key row in the reference did not say which table it points at; the jar follows the "
                + "foreign key to the key it refers to, fills `physical_joins.table2_id`, adds the column "
                + "`referenced_columns`, and no longer lists that link as unread. (The key itself, which this "
                + "difference was the first to read, is a row of its own since difference 28.)");
        SENTENCE.put(27, "The reference knew a role only by its name; the jar adds the role's display name and guid to "
                + "`security_rls` (`role_display_name`, `role_guid`) and no longer lists them as unread.");
        SENTENCE.put(28, "The reference read no key of a physical table and listed them as unread; the jar writes a new "
                + "table, `physical_keys`, with one row per key (its table, its name, its columns in order), counts "
                + "the keys in `coverage`, and no longer lists them as unread.");
        SENTENCE.put(29, "The reference read no key of a logical table and listed them as unread; the jar writes a new "
                + "table, `logical_keys`, with one row per key (its logical table, its name, the primary flag as "
                + "written, its columns in order), counts the keys in `coverage`, and no longer lists them as unread.");
        SENTENCE.put(30, "Of the keys of a level the reference kept one column (`logical_levels.drill_key_column_id`) "
                + "and listed the rest as unread; the jar also writes a new table, `logical_level_keys`, with one row "
                + "per key (its level, its name, the primary, chronological and drill-down flags as written, all its "
                + "columns in order), and no longer lists them as unread.");
        SENTENCE.put(31, "The reference did not read a physical column's scale and wrote the columns in name order "
                + "only; the jar adds `physical_columns.scale` and `physical_columns.position` (the column's place "
                + "in its table in the repository) and no longer lists the scale as unread.");
        SENTENCE.put(32, "The reference did not read a dimension's time flag; the jar adds "
                + "`logical_dimensions.is_time` and no longer lists the flag as unread.");
        SENTENCE.put(34, "The reference did not read which physical tables a table source is built on, or the joins it "
                + "makes between them, and listed them as unread; the jar writes two new tables, `lts_tables` and "
                + "`lts_joins` (join type and cardinality as written, and the physical join that gives the condition), "
                + "counts them in `coverage`, and no longer lists them as unread.");
        SENTENCE.put(35, "The reference did not read which level is the child of which; the jar adds "
                + "`logical_levels.child_level_ids` and `parent_level_ids` and no longer lists a level's list of "
                + "children as unread.");
        SENTENCE.put(36, "The reference did not read what defines a parent-child hierarchy; the jar adds the four "
                + "columns of the relationship table to `physical_tables` (`member_key_column_id`, "
                + "`parent_key_column_id`, `distance_column_id`, `leaf_column_id`) and the table a source names to "
                + "`logical_table_sources` (`closure_table_id`), and no longer lists them as unread.");
        SENTENCE.put(37, "Four smaller attributes the reference listed as unread are now columns: "
                + "`physical_tables.description`, `physical_columns.source_column_id` (the column an alias column "
                + "stands for), `logical_tables.is_lookup_table` and `logical_columns.descriptor_id_column_id`.");
    }

    /** One comparison: a generated Knowledge Base against a reference one, for one estate. */
    private static final class Comparison {
        final String estate;
        final Path kb;
        final Path reference;
        final Path source;
        /** Why there is nothing to compare, or empty. */
        String notRun = "";
        String extractOutput = "";
        int extractExit = -1;
        final Map<String, Result> results = new LinkedHashMap<>();
        final List<ManifestLine> manifest = new ArrayList<>();
        /** Tables the generated manifest lists that the reference folder does not have. */
        final List<String> generatedOnly = new ArrayList<>();
        final Map<String, Integer> generatedOnlyRows = new TreeMap<>();
        /** For each such table, the difference that explains it once its check has passed; 0 when none does. */
        final Map<String, Integer> generatedOnlyNumber = new TreeMap<>();
        /** True when every such table is explained, and the two schema tables are among them. */
        boolean generatedOnlyExplained;
        int guardColumns;
        int guardTables;
        int guardedCells;
        final List<String> guardExamples = new ArrayList<>();
        final List<String> accountingProblems = new ArrayList<>();

        Comparison(String estate, Path kb, Path reference, Path source) {
            this.estate = estate;
            this.kb = kb;
            this.reference = reference;
            this.source = source;
        }

        int unexplained() {
            int total = 0;
            for (Result result : results.values()) {
                total += unexplainedIn(result);
            }
            for (ManifestLine line : manifest) {
                total += line.verdict.equals(ManifestLine.UNEXPLAINED) ? 1 : 0;
            }
            return total + unexplainedTables().size();
        }

        /** The tables only the jar writes that no deliberate difference explains. */
        List<String> unexplainedTables() {
            List<String> tables = new ArrayList<>();
            for (String table : generatedOnly) {
                if (generatedOnlyNumber.getOrDefault(table, 0) == 0) {
                    tables.add(table);
                }
            }
            return tables;
        }

        long referenceRows() {
            long total = manifest.stream().filter(l -> !l.reference.isEmpty() || l.inReference).count();
            for (Result result : results.values()) {
                total += result.referenceRows;
            }
            return total;
        }

        long generatedRows() {
            long total = manifest.stream().filter(l -> l.inGenerated).count();
            for (Result result : results.values()) {
                total += result.generatedRows;
            }
            return total;
        }
    }

    /** One key of {@code manifest.csv}, as far as it concerns the estate. */
    private static final class ManifestLine {
        static final String IDENTICAL = "identical";
        static final String EXPLAINED = "explained";
        static final String RUN_TIME = "time of the run";
        static final String UNEXPLAINED = "unexplained";

        final String key;
        final String reference;
        final String generated;
        final boolean inReference;
        final boolean inGenerated;
        String verdict = UNEXPLAINED;
        int number;
        String why = "";

        ManifestLine(String key, String reference, String generated, boolean inReference, boolean inGenerated) {
            this.key = key;
            this.reference = reference;
            this.generated = generated;
            this.inReference = inReference;
            this.inGenerated = inGenerated;
        }
    }

    private KbCompareReport() {
    }

    public static void main(String[] args) throws Exception {
        System.exit(run(args, System.out));
    }

    static int run(String[] args, PrintStream out) throws Exception {
        Path report = Paths.get("documents", "implementation-artifacts", "kb-comparison-report.md");
        boolean extract = true;
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("--out") && i + 1 < args.length) {
                report = Paths.get(args[++i]);
            } else if (args[i].equals("--no-extract")) {
                extract = false;
            } else {
                out.println("usage: compare-all [--out <report file>] [--no-extract]");
                return 2;
            }
        }
        Path jar = Paths.get("build", "obi2pbi.jar");
        if (!Files.isRegularFile(jar)) {
            out.println("error: " + jar + " is not there; run ./build.sh first");
            return 2;
        }

        List<Comparison> samples = new ArrayList<>();
        for (String estate : SAMPLE_ESTATES) {
            Path source = Paths.get("estates", estate);
            Comparison c = new Comparison(estate, Paths.get("kb", estate), REFERENCE, source);
            samples.add(c);
            if (!Files.isDirectory(source)) {
                c.notRun = "the estate folder `" + slash(source) + "` is not there, so the input could not be read";
                continue;
            }
            if (extract) {
                out.println("extract " + slash(source));
                runExtract(jar, source, c.kb, c);
            }
            if (!Files.isRegularFile(c.kb.resolve("manifest.csv"))) {
                c.notRun = "`" + slash(c.kb) + "` has no Knowledge Base";
                continue;
            }
            out.println("compare " + estate);
            compareAll(c);
        }

        List<Comparison> handMade = new ArrayList<>();
        String python = extract ? findPython() : null;
        handMade.add(handMade("constructed-pair", Paths.get("reference", "test-corpus", "constructed-cases", "constructed"),
                Paths.get("reference", "test-corpus", "constructed-cases", "constructed-catalog"), jar, python, extract, out));
        handMade.add(handMade("linked-estate", Paths.get("src", "test", "fixtures", "linked-estate", "rpd"),
                Paths.get("src", "test", "fixtures", "linked-estate", "catalog"), jar, python, extract, out));

        String text = write(samples, handMade);
        Files.createDirectories(report.toAbsolutePath().getParent());
        Files.write(report, text.getBytes(StandardCharsets.UTF_8));

        int tables = 0;
        long rows = 0;
        int unexplained = 0;
        int notConfirmed = 0;
        for (Comparison c : samples) {
            if (!c.notRun.isEmpty()) {
                notConfirmed++;
                continue;
            }
            tables += c.results.size() + 1;
            rows += c.referenceRows();
            unexplained += c.unexplained();
        }
        int handMadeUnexplained = 0;
        for (Comparison c : handMade) {
            handMadeUnexplained += c.notRun.isEmpty() ? c.unexplained() : 0;
        }
        out.println("report:   " + slash(report));
        out.println("estates:  " + (samples.size() - notConfirmed) + " compared"
                + (notConfirmed == 0 ? "" : ", " + notConfirmed + " NOT CONFIRMED (input missing)"));
        out.println("tables:   " + tables + " table comparisons");
        out.println("rows:     " + String.format(Locale.ROOT, "%,d", rows) + " reference rows compared");
        out.println("unexplained differences: " + unexplained
                + (handMadeUnexplained == 0 ? "" : " (and " + handMadeUnexplained + " on the hand-made estates)"));
        return unexplained == 0 && handMadeUnexplained == 0 && notConfirmed == 0 ? 0 : 1;
    }

    // ------------------------------------------------------------------ running the programs

    private static void runExtract(Path jar, Path estateFolder, Path kb, Comparison c) throws Exception {
        String java = Paths.get(System.getProperty("java.home"), "bin", "java").toString();
        Process process = new ProcessBuilder(java, "-jar", jar.toString(), "extract", estateFolder.toString(), "--out",
                kb.toString()).redirectErrorStream(true).start();
        c.extractOutput = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        c.extractExit = process.waitFor();
    }

    /** {@code python3} or {@code python}, whichever runs; null when neither does. */
    private static String findPython() {
        for (String name : List.of("python3", "python")) {
            try {
                Process process = new ProcessBuilder(name, "--version").redirectErrorStream(true).start();
                process.getInputStream().readAllBytes();
                if (process.waitFor() == 0) {
                    return name;
                }
            } catch (IOException | InterruptedException e) {
                // not on this machine under that name
            }
        }
        return null;
    }

    /** Builds an estate with both halves under {@code build/compare-all}, runs both programs, compares. */
    private static Comparison handMade(String estate, Path rpd, Path catalog, Path jar, String python, boolean run,
            PrintStream out) throws Exception {
        Path in = WORK.resolve("in").resolve(estate);
        Path kb = WORK.resolve("kb").resolve(estate);
        Path py = WORK.resolve("py").resolve(estate);
        Comparison c = new Comparison(estate, kb, py, in);
        if (run) {
            TestSupport.deleteTree(in);
            TestSupport.deleteTree(kb);
            TestSupport.deleteTree(py);
            TestSupport.copyTree(rpd, in.resolve("rpd"));
            TestSupport.copyTree(catalog, in.resolve("catalog"));
            out.println("extract " + slash(in));
            runExtract(jar, in, kb, c);
            if (python == null) {
                c.notRun = "Python is not on this machine, so the reference implementation could not be run";
                return c;
            }
            out.println("reference implementation on " + slash(in));
            ProcessBuilder builder = new ProcessBuilder(python, "-m", "tools.rpd_extract.cli", "--corpus",
                    in.toAbsolutePath().toString(), "--rpd-id", estate, "--out", py.toAbsolutePath().toString())
                    .directory(new File("reference/python-extractor")).redirectErrorStream(true);
            builder.environment().put("PYTHONDONTWRITEBYTECODE", "1");
            Process process = builder.start();
            String printed = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            int exit = process.waitFor();
            if (!Files.isRegularFile(py.resolve("manifest.csv"))) {
                c.notRun = "the reference implementation did not write a Knowledge Base (exit code " + exit + "): "
                        + printed.trim();
                return c;
            }
        }
        if (!Files.isRegularFile(kb.resolve("manifest.csv")) || !Files.isRegularFile(py.resolve("manifest.csv"))) {
            c.notRun = "not run: `" + slash(kb) + "` or `" + slash(py) + "` is missing";
            return c;
        }
        compareAll(c);
        return c;
    }

    // ------------------------------------------------------------------ comparing

    private static void compareAll(Comparison c) throws IOException {
        List<String> referenceTables = KbCompare.referenceTables(c.reference);
        for (String table : referenceTables) {
            if (table.equals("manifest")) {
                continue;
            }
            Result result = KbCompare.compare(c.kb, c.reference, c.source, c.estate, table, Map.of());
            c.results.put(table, result);
            if (result.problem.isEmpty()) {
                int paired = result.identicalRows + result.rowsOnlyExplained + result.rowsUnexplained;
                int missing = count(result, Change.MISSING, false) + count(result, Change.MISSING, true);
                int extra = count(result, Change.EXTRA, false) + count(result, Change.EXTRA, true);
                if (paired + missing != result.referenceRows || paired + extra != result.generatedRows) {
                    c.accountingProblems.add(table + ": " + paired + " rows on both sides, " + missing + " missing, "
                            + extra + " extra do not add up to " + result.referenceRows + " reference and "
                            + result.generatedRows + " generated rows");
                }
            }
            countGuards(c, table);
        }
        // A table with no reference counterpart is a new table. It is explained only by the difference that
        // adds it: 16 for the two schema tables (both must be there), and 28, 29 or 30 for a key table, each
        // only when every row of it is what the estate's own files say (KbCompare.explainsNewTable).
        KbCompare.Sides sides = new KbCompare.Sides(c.kb, c.reference, c.source, c.estate);
        for (String table : KbCompare.manifestTables(c.kb)) {
            if (!referenceTables.contains(table)) {
                c.generatedOnly.add(table);
                Path file = c.kb.resolve(table + ".csv");
                c.generatedOnlyRows.put(table, Files.isRegularFile(file) ? CsvReader.read(file).rows.size() : 0);
            }
        }
        boolean schema = c.generatedOnly.containsAll(List.of("schema", "schema_tables"));
        for (String table : c.generatedOnly) {
            int number = table.equals("schema") || table.equals("schema_tables") ? (schema ? 16 : 0)
                    : KbCompare.explainsNewTable(sides, table);
            c.generatedOnlyNumber.put(table, number);
        }
        c.generatedOnlyExplained = schema && c.unexplainedTables().isEmpty();
        compareManifest(c);
    }

    /** Difference 1: how many guard columns the reference table has, and how many of the estate's cells carry the apostrophe. */
    private static void countGuards(Comparison c, String table) throws IOException {
        CsvTable raw = CsvReader.read(c.reference.resolve(table + ".csv"));
        CsvTable generated = Files.isRegularFile(c.kb.resolve(table + ".csv")) ? CsvReader.read(c.kb.resolve(table + ".csv")) : null;
        int columns = 0;
        for (int i = 0; i < raw.header.size(); i++) {
            String name = raw.header.get(i);
            if (!name.endsWith(GUARDED)) {
                continue;
            }
            columns++;
            int value = raw.indexOf(name.substring(0, name.length() - GUARDED.length()));
            if (value < 0 || raw.indexOf("rpd_id") < 0) {
                continue;
            }
            for (String[] row : KbCompare.estateRows(raw, c.estate)) {
                if (!row[i].equals("true") || !row[value].startsWith("'")) {
                    continue;
                }
                c.guardedCells++;
                // Never an example from the variables table: its values can be credentials.
                if (c.guardExamples.size() < EXAMPLES && !table.equals("variables") && raw.indexOf("row_id") >= 0
                        && generated != null && generated.indexOf("row_id") >= 0) {
                    String rowId = raw.value(row, "row_id");
                    List<String[]> ours = TestSupport.rows(generated, "row_id", rowId);
                    if (ours.size() == 1 && c.guardExamples.stream().noneMatch(e -> e.startsWith("`" + table + "`"))) {
                        c.guardExamples.add("`" + table + "`, row " + code(rowId) + ", column `" + raw.header.get(value)
                                + "`: reference " + code(shorten(row[value])) + " with `" + name + "` = `true`; generated "
                                + code(shorten(generated.value(ours.get(0), raw.header.get(value)))) + ", and no `" + name
                                + "` column.");
                    }
                }
            }
        }
        if (columns > 0) {
            c.guardColumns += columns;
            c.guardTables++;
        }
    }

    /**
     * Compares {@code manifest.csv} key by key, as far as the two can be compared. The reference manifest
     * is one file for three estates written by another program; the generated one is for one estate.
     *
     * <ul>
     * <li>{@code halves_present:&lt;estate&gt;} and {@code pairing:&lt;estate&gt;}: must be equal. The one allowed
     * change is in the pairing text of an estate with both halves, where "one rpd-id (AD-22)" reads
     * "one estate name (AD-7)" (difference 17).</li>
     * <li>{@code extractor_version}: differs, each program's own (17).</li>
     * <li>{@code corpus_root}: differs, the folder each program was given; the generated one must end in
     * the estate name (17).</li>
     * <li>{@code rpd_ids}: the generated value must be the estate alone and the reference's list must
     * contain it (15).</li>
     * <li>{@code tables}: the generated list must be the reference's plus {@code schema} and
     * {@code schema_tables} (16) and the key tables of differences 28 to 30, each of which must have passed
     * its own check; nothing else.</li>
     * <li>{@code extracted_at}: the time of each run. Never equal, and not a difference of the port.</li>
     * </ul>
     * Any other key, on either side, is unexplained. Rows of the reference manifest about other estates
     * are left out, as rows of other estates are in every table.
     */
    private static void compareManifest(Comparison c) throws IOException {
        Map<String, String> reference = keyValues(c.reference.resolve("manifest.csv"));
        Map<String, String> generated = keyValues(c.kb.resolve("manifest.csv"));
        Set<String> keys = new LinkedHashSet<>(reference.keySet());
        keys.addAll(generated.keySet());
        for (String key : keys) {
            int colon = key.indexOf(':');
            if (colon >= 0 && !key.substring(colon + 1).equals(c.estate) && !generated.containsKey(key)) {
                continue;
            }
            String ref = reference.getOrDefault(key, "");
            String gen = generated.getOrDefault(key, "");
            ManifestLine line = new ManifestLine(key, ref, gen, reference.containsKey(key), generated.containsKey(key));
            c.manifest.add(line);
            if (!line.inReference || !line.inGenerated) {
                line.why = line.inReference ? "the generated manifest has no such key" : "the reference manifest has no such key";
            } else if (key.equals("extracted_at")) {
                line.verdict = ManifestLine.RUN_TIME;
                line.why = "the time each program was run";
            } else if (ref.equals(gen)) {
                line.verdict = ManifestLine.IDENTICAL;
            } else if (key.equals("extractor_version") && !gen.isEmpty()) {
                explain(line, 17, "each program's own version number");
            } else if (key.equals("corpus_root") && slash(Paths.get(gen)).endsWith(c.estate)) {
                explain(line, 17, "the folder each program was given");
            } else if (key.equals("rpd_ids") && gen.equals(c.estate) && Arrays.asList(ref.split(" ")).contains(c.estate)) {
                explain(line, 15, "one estate per run; the reference's list contains it");
            } else if (key.equals("tables")) {
                Set<String> more = new TreeSet<>(Arrays.asList(gen.split(" ")));
                Set<String> fewer = new TreeSet<>(Arrays.asList(ref.split(" ")));
                Set<String> lost = new TreeSet<>(fewer);
                lost.removeAll(more);
                more.removeAll(fewer);
                Set<String> newTables = new TreeSet<>(more);
                newTables.removeAll(List.of("schema", "schema_tables"));
                boolean checked = more.containsAll(List.of("schema", "schema_tables"));
                for (String table : more) {
                    checked &= c.generatedOnlyNumber.getOrDefault(table, 0) != 0;
                }
                if (lost.isEmpty() && checked && newTables.isEmpty()) {
                    explain(line, 16, "the reference's list plus `schema` and `schema_tables`");
                } else if (lost.isEmpty() && checked) {
                    explain(line, 16, "the reference's list plus `schema` and `schema_tables` (16) and the new tables "
                            + newTables + " (differences 28 to 30 and 34), each checked row by row against the estate's files");
                } else {
                    line.why = "tables only in the reference's list: " + lost + "; only in the generated list: " + more;
                }
            } else if (key.startsWith("pairing:") && ref.replace("one rpd-id (AD-22)", "one estate name (AD-7)").equals(gen)) {
                explain(line, 17, "the same sentence, with this project's word for the estate and its rule number");
            } else {
                line.why = "the values differ";
            }
        }
    }

    private static void explain(ManifestLine line, int number, String why) {
        line.verdict = ManifestLine.EXPLAINED;
        line.number = number;
        line.why = why;
    }

    private static Map<String, String> keyValues(Path manifest) throws IOException {
        Map<String, String> values = new LinkedHashMap<>();
        CsvTable table = CsvReader.read(manifest);
        for (String[] row : table.rows) {
            values.put(table.value(row, "key"), table.value(row, "value"));
        }
        return values;
    }

    private static int count(Result result, String kind, boolean explained) {
        int total = 0;
        for (Change change : result.changes) {
            if (change.kind.equals(kind) && (change.number != 0) == explained) {
                total++;
            }
        }
        return total;
    }

    /** Everything in one table that no deliberate difference explains: cells, rows, columns, or the table itself. */
    private static int unexplainedIn(Result result) {
        if (!result.problem.isEmpty()) {
            return 1;
        }
        int total = 0;
        for (Change change : result.changes) {
            total += change.number == 0 ? 1 : 0;
        }
        return total;
    }

    // ------------------------------------------------------------------ writing

    private static String write(List<Comparison> samples, List<Comparison> handMade) throws IOException {
        StringBuilder md = new StringBuilder();
        int unexplained = 0;
        int tables = 0;
        long referenceRows = 0;
        long generatedRows = 0;
        long identical = 0;
        List<Comparison> compared = new ArrayList<>();
        for (Comparison c : samples) {
            if (c.notRun.isEmpty()) {
                compared.add(c);
                unexplained += c.unexplained();
                tables += c.results.size() + 1;
                referenceRows += c.referenceRows();
                generatedRows += c.generatedRows();
                for (Result result : c.results.values()) {
                    identical += result.identicalRows;
                }
                identical += c.manifest.stream().filter(l -> l.verdict.equals(ManifestLine.IDENTICAL)).count();
            }
        }
        int referenceTableCount = KbCompare.referenceTables(REFERENCE).size();

        md.append("# Knowledge Base comparison report\n\n");
        md.append("This report is written by `./build.sh compare-all`. Do not edit it by hand: run the command again. ")
                .append("Last written on ").append(LocalDate.now()).append(".\n\n");
        md.append("**Confirmation by Kamal: NOT YET GIVEN.** Epic 1 is not done until Kamal has read this report and ")
                .append("confirmed it. This line is to be changed by a person, not by the program.\n\n");

        md.append("## The result in short\n\n");
        md.append("| | |\n|---|---|\n");
        md.append("| Sample estates compared | ").append(compared.size()).append(" of ").append(samples.size()).append(" |\n");
        md.append("| Reference tables | ").append(referenceTableCount).append(" (each compared once per estate: ")
                .append(tables).append(" table comparisons) |\n");
        md.append("| Reference rows compared | ").append(n(referenceRows)).append(" |\n");
        md.append("| Generated rows compared | ").append(n(generatedRows)).append(" |\n");
        md.append("| Rows identical on both sides | ").append(n(identical)).append(" |\n");
        md.append("| **Unexplained differences** | **").append(unexplained).append("** |\n\n");
        for (Comparison c : samples) {
            if (!c.notRun.isEmpty()) {
                md.append("**Not confirmed: `").append(c.estate).append("`.** ").append(c.notRun)
                        .append(". None of its tables could be compared.\n\n");
            }
        }
        if (unexplained == 0 && compared.size() == samples.size()) {
            md.append("Every difference between the reference Knowledge Base and the one the jar writes is one of the ")
                    .append("numbered deliberate differences in `docs/port-guide/00-read-me-first.md`. ")
                    .append("Each was checked cell by cell or row by row, as described below; none was accepted ")
                    .append("because of the table or column it is in.\n\n");
        } else if (unexplained > 0) {
            md.append("**There are unexplained differences. They are listed in section 4.**\n\n");
        }

        md.append("## 1. What was compared, and how\n\n");
        md.append("**The reference** is `reference/knowledge-base/`: the ").append(referenceTableCount)
                .append(" CSV tables the first attempt's Python program wrote, in one run, from two sample repositories ")
                .append("(`sampleapp`, `bi-analytics-2013`) and one sample Web Catalog (`sampleapp-v607`).\n\n");
        md.append("**The generated Knowledge Bases** are `kb/<estate>/`, written by this command with ")
                .append("`java -jar build/obi2pbi.jar extract estates/<estate>` from the same input files.\n\n");
        md.append("| Estate | Input | `extract` exit code | What `extract` printed |\n|---|---|---|---|\n");
        for (Comparison c : samples) {
            md.append("| `").append(c.estate).append("` | `").append(slash(c.source)).append("` | ")
                    .append(c.extractExit < 0 ? "not run" : Integer.toString(c.extractExit)).append(" | ")
                    .append(cellText(c.extractOutput.trim().replace("\n", "; "))).append(" |\n");
        }
        md.append("\nExit code 3 means the Knowledge Base was written and `errors.csv` has rows; for the catalog these ")
                .append("are references to parts of the catalog that were not exported, as in the reference.\n\n");
        md.append("**How a table is compared.**\n\n");
        md.append("1. Only the rows of the one estate are taken from the reference table (its `rpd_id` column).\n");
        md.append("2. The reference's `..._guarded` columns are left out, and the apostrophe its writer put in front of ")
                .append("a guarded value is taken off, so that values are compared as the source held them ")
                .append("(difference 1).\n");
        md.append("3. Rows are paired by `row_id`. `unparsed` is paired by estate, kind, object type and detail; ")
                .append("`coverage` by estate, layer and object type; a table with no `row_id` is compared as whole ")
                .append("rows. A `row_id` that occurs twice is compared as whole rows.\n");
        md.append("4. Paired rows are compared cell by cell, in every column both sides have. A row only one side has ")
                .append("is \"missing\" (reference only) or \"extra\" (generated only).\n");
        md.append("5. Every difference is then tested against the deliberate differences. A difference counts as ")
                .append("explained only when a check passes: either a stated transformation of the reference value ")
                .append("gives the generated value, or the estate's own files, read a second time by the comparison ")
                .append("with the JDK's own XML parser, bear the generated value out. Section 3 says what was checked ")
                .append("for each. Anything else is unexplained.\n\n");
        md.append("The comparison is `src/test/java/obi2pbi/KbCompare.java`; this report is written by ")
                .append("`KbCompareReport.java` beside it. In the examples below, a line break inside a value is shown ")
                .append("as `\\n` and a tab as `\\t`, and a long value is cut short with its full length given.\n\n");

        md.append("## 2. Every table, per estate\n\n");
        md.append("Column meanings: **Identical** = rows present on both sides and equal in every shared column. ")
                .append("**Differ only as explained** = rows present on both sides whose differing cells are all ")
                .append("explained. **Missing** = reference rows the jar did not write; **Extra** = rows the jar wrote ")
                .append("that the reference lacks (both counts include explained rows; the last column says how many ")
                .append("are not). **Unexplained** = cells, rows or columns no deliberate difference explains. ")
                .append("**Explained by** names the difference number and what it accounts for. ")
                .append("Reference rows = identical + differ only as explained + rows with unexplained cells + missing. ")
                .append("`manifest.csv` is one file of keys and values for all three estates; its row here counts the ")
                .append("keys that concern the estate, and the table after the three estates shows each of them.\n\n");
        for (Comparison c : samples) {
            md.append("### ").append(c.estate).append("\n\n");
            if (!c.notRun.isEmpty()) {
                md.append("Not compared: ").append(c.notRun).append(".\n\n");
                continue;
            }
            estateTable(md, c, true);
        }

        md.append("### `manifest.csv`, key by key\n\n");
        md.append("The reference manifest was written once for all three estates by the Python program; each generated ")
                .append("manifest is for one estate. Lines of the reference about other estates are left out, as rows ")
                .append("of other estates are in every table.\n\n");
        md.append("| Estate | Key | Reference | Generated | Verdict |\n|---|---|---|---|---|\n");
        for (Comparison c : compared) {
            for (ManifestLine line : c.manifest) {
                String verdict = line.verdict.equals(ManifestLine.EXPLAINED) ? "explained by difference " + line.number + ": " + line.why
                        : line.verdict.equals(ManifestLine.RUN_TIME) ? "not comparable: " + line.why
                        : line.verdict.equals(ManifestLine.UNEXPLAINED) ? "**unexplained**: " + line.why : "identical";
                md.append("| `").append(c.estate).append("` | `").append(line.key).append("` | ")
                        .append(code(shorten(line.reference, 60))).append(" | ").append(code(shorten(line.generated, 60)))
                        .append(" | ").append(verdict).append(" |\n");
            }
        }
        md.append("\n");

        md.append("## 3. The deliberate differences that occurred\n\n");
        md.append("The numbers are those of the list in `docs/port-guide/00-read-me-first.md`, which gives the old ")
                .append("behaviour, the new behaviour and the reason for each.\n\n");
        Set<Integer> occurred = new TreeSet<>();
        for (Comparison c : compared) {
            occurred.addAll(numbersIn(c));
        }
        for (int number : occurred) {
            differenceSection(md, number, compared);
        }
        md.append("### Deliberate differences that did not occur in this comparison\n\n");
        didNotOccur(md, occurred, compared, handMade);

        md.append("## 4. Unexplained differences\n\n");
        boolean any = false;
        for (Comparison c : compared) {
            any |= unexplainedSection(md, c);
        }
        if (!any) {
            md.append("None. Every difference found on the ").append(compared.size())
                    .append(" sample estates is assigned to a numbered deliberate difference.\n\n");
        }

        md.append("## 5. Linking report columns to the repository: the hand-made estates\n\n");
        md.append("On the samples no report column links to a repository (section 6). The link is therefore checked on ")
                .append("two small hand-made estates that have both halves. For each, this command builds the estate ")
                .append("folder under `build/compare-all/in/`, runs the jar on it, runs the reference implementation ")
                .append("(the Python program in `reference/python-extractor/`) on the same folder, and compares the ")
                .append("two outputs in the same way as above.\n\n");
        md.append("- `constructed-pair`: the first attempt's pair, `reference/test-corpus/constructed-cases/` ")
                .append("(`constructed/` is the repository, `constructed-catalog/` the catalog).\n");
        md.append("- `linked-estate`: `src/test/fixtures/linked-estate/`, written in story 1.10 so that every value of ")
                .append("`resolution` and `source_state` occurs, and so that difference 22 occurs.\n\n");
        md.append("The project's tests (`ReportLineageTest`) separately require the jar's rows for `constructed-pair` to ")
                .append("equal the expected result recorded with that pair (`GROUND-TRUTH.md` and guide 02, section ")
                .append("6.5), and the rows for `linked-estate` to equal values worked out by hand from the rules.\n\n");
        for (Comparison c : handMade) {
            md.append("### ").append(c.estate).append("\n\n");
            if (!c.notRun.isEmpty()) {
                md.append("**Not compared:** ").append(c.notRun).append(".\n\n");
                continue;
            }
            estateTable(md, c, false);
            lineageOutcomes(md, c);
            if (numbersIn(c).contains(22)) {
                md.append("Difference 22 on this estate:\n\n");
                examples(md, 22, List.of(c));
            }
            unexplainedSection(md, c);
        }

        md.append("## 6. What this comparison does not prove\n\n");
        doesNotProve(md, compared);
        return md.toString();
    }

    /** The per-table lines of one comparison. With {@code all} false, tables with no difference are summed up in one line. */
    private static void estateTable(StringBuilder md, Comparison c, boolean all) {
        md.append("| Table | Reference rows | Generated rows | Identical | Differ only as explained | Missing | Extra | ")
                .append("Unexplained | Explained by |\n|---|---:|---:|---:|---:|---:|---:|---:|---|\n");
        int same = 0;
        long sameRows = 0;
        long[] totals = new long[7];
        for (Map.Entry<String, Result> entry : withManifest(c).entrySet()) {
            String table = entry.getKey();
            long[] cells;
            String explainedBy;
            if (table.equals("manifest")) {
                long identical = c.manifest.stream().filter(l -> l.verdict.equals(ManifestLine.IDENTICAL)).count();
                long explained = c.manifest.stream().filter(l -> l.verdict.equals(ManifestLine.EXPLAINED)).count();
                long runTime = c.manifest.stream().filter(l -> l.verdict.equals(ManifestLine.RUN_TIME)).count();
                long unexplained = c.manifest.stream().filter(l -> l.verdict.equals(ManifestLine.UNEXPLAINED)).count();
                cells = new long[] {c.manifest.stream().filter(l -> l.inReference).count(),
                        c.manifest.stream().filter(l -> l.inGenerated).count(), identical, explained, 0, 0, unexplained};
                Map<Integer, Integer> per = new TreeMap<>();
                for (ManifestLine line : c.manifest) {
                    if (line.number != 0) {
                        per.merge(line.number, 1, Integer::sum);
                    }
                }
                List<String> parts = new ArrayList<>();
                per.forEach((number, rows) -> parts.add(number + ": " + rows + (rows == 1 ? " row" : " rows")));
                if (runTime > 0) {
                    parts.add("`extracted_at`, 1 row, is the time of the run and is counted in no column");
                }
                explainedBy = String.join("; ", parts);
            } else {
                Result r = entry.getValue();
                if (!r.problem.isEmpty()) {
                    md.append("| `").append(table).append("` | | | | | | | **1** | **").append(r.problem).append("** |\n");
                    totals[6]++;
                    continue;
                }
                int missing = count(r, Change.MISSING, true) + count(r, Change.MISSING, false);
                int extra = count(r, Change.EXTRA, true) + count(r, Change.EXTRA, false);
                cells = new long[] {r.referenceRows, r.generatedRows, r.identicalRows, r.rowsOnlyExplained, missing, extra,
                        unexplainedIn(r)};
                explainedBy = explainedBy(r);
            }
            for (int i = 0; i < totals.length; i++) {
                totals[i] += cells[i];
            }
            boolean nothing = cells[3] == 0 && cells[4] == 0 && cells[5] == 0 && cells[6] == 0 && explainedBy.isEmpty();
            if (!all && nothing) {
                same++;
                sameRows += cells[0];
                continue;
            }
            md.append("| `").append(table).append("` | ").append(n(cells[0])).append(" | ").append(n(cells[1])).append(" | ")
                    .append(n(cells[2])).append(" | ").append(n(cells[3])).append(" | ").append(n(cells[4])).append(" | ")
                    .append(n(cells[5])).append(" | ").append(cells[6] == 0 ? "0" : "**" + n(cells[6]) + "**").append(" | ")
                    .append(explainedBy).append(" |\n");
        }
        if (!all && same > 0) {
            md.append("| *").append(same).append(" other tables, identical* | ").append(n(sameRows)).append(" | ")
                    .append(n(sameRows)).append(" | ").append(n(sameRows)).append(" | 0 | 0 | 0 | 0 | |\n");
        }
        md.append("| **Total** | **").append(n(totals[0])).append("** | **").append(n(totals[1])).append("** | **")
                .append(n(totals[2])).append("** | **").append(n(totals[3])).append("** | **").append(n(totals[4]))
                .append("** | **").append(n(totals[5])).append("** | **").append(n(totals[6])).append("** | |\n\n");
        if (!c.generatedOnly.isEmpty()) {
            List<String> parts = new ArrayList<>();
            for (String table : c.generatedOnly) {
                parts.add("`" + table + "` (" + n(c.generatedOnlyRows.get(table)) + " rows)");
            }
            parts.clear();
            for (String table : c.generatedOnly) {
                int number = c.generatedOnlyNumber.getOrDefault(table, 0);
                parts.add("`" + table + "` (" + n(c.generatedOnlyRows.get(table)) + " rows): "
                        + (number == 0 ? "**unexplained**" : "new table (difference " + number + ")"));
            }
            md.append("Tables the jar writes that the reference does not have: ").append(String.join("; ", parts))
                    .append(c.generatedOnlyExplained ? ".\n\n" : ". **Unexplained: a new table counts as explained only "
                            + "when the difference that adds it is on the list and every row of it passes that "
                            + "difference's check against the estate's files.**\n\n");
        }
        if (all) {
            md.append("Difference 1 applies to every table: ").append(c.guardColumns).append(" `..._guarded` columns in ")
                    .append(c.guardTables).append(" reference tables are not in the generated tables, and ")
                    .append(n(c.guardedCells)).append(" cells of this estate carry the guard apostrophe in the ")
                    .append("reference. They are not counted in the columns above.\n\n");
        }
        for (String problem : c.accountingProblems) {
            md.append("**The row counts do not add up for ").append(problem).append(".**\n\n");
        }
    }

    private static Map<String, Result> withManifest(Comparison c) {
        Map<String, Result> tables = new TreeMap<>(c.results);
        tables.put("manifest", new Result("manifest"));
        return tables;
    }

    private static String explainedBy(Result r) {
        Map<Integer, int[]> per = new TreeMap<>();
        for (Change change : r.changes) {
            if (change.number == 0) {
                continue;
            }
            int[] counts = per.computeIfAbsent(change.number, k -> new int[4]);
            counts[kindIndex(change.kind)]++;
        }
        List<String> parts = new ArrayList<>();
        for (Map.Entry<Integer, int[]> entry : per.entrySet()) {
            int[] counts = entry.getValue();
            List<String> what = new ArrayList<>();
            if (counts[0] > 0) {
                what.add(n(counts[0]) + (counts[0] == 1 ? " cell" : " cells"));
            }
            if (counts[1] > 0) {
                what.add(n(counts[1]) + " missing");
            }
            if (counts[2] > 0) {
                what.add(n(counts[2]) + " extra");
            }
            if (counts[3] > 0) {
                what.add(counts[3] + " added columns");
            }
            parts.add(entry.getKey() + ": " + String.join(", ", what));
        }
        return String.join("; ", parts);
    }

    private static int kindIndex(String kind) {
        return kind.equals(Change.CELL) ? 0 : kind.equals(Change.MISSING) ? 1 : kind.equals(Change.EXTRA) ? 2 : 3;
    }

    private static Set<Integer> numbersIn(Comparison c) {
        Set<Integer> numbers = new TreeSet<>();
        for (Result result : c.results.values()) {
            for (Change change : result.changes) {
                if (change.number != 0) {
                    numbers.add(change.number);
                }
            }
        }
        for (ManifestLine line : c.manifest) {
            if (line.number != 0) {
                numbers.add(line.number);
            }
        }
        if (c.guardColumns > 0) {
            numbers.add(1);
        }
        for (int number : c.generatedOnlyNumber.values()) {
            if (number != 0) {
                numbers.add(number);
            }
        }
        return numbers;
    }

    private static void differenceSection(StringBuilder md, int number, List<Comparison> compared) {
        md.append("### Difference ").append(number).append("\n\n");
        md.append(SENTENCE.getOrDefault(number, "See the list in the port guide.")).append("\n\n");
        md.append("**Accounts for:**\n\n");
        for (Comparison c : compared) {
            List<String> parts = new ArrayList<>();
            if (number == 1 && c.guardColumns > 0) {
                parts.add(c.guardColumns + " guard columns in " + c.guardTables + " tables; " + n(c.guardedCells)
                        + " cells with the apostrophe");
            }
            for (Map.Entry<String, Result> entry : c.results.entrySet()) {
                int[] counts = new int[4];
                for (Change change : entry.getValue().changes) {
                    if (change.number == number) {
                        counts[kindIndex(change.kind)]++;
                    }
                }
                List<String> what = new ArrayList<>();
                if (counts[0] > 0) {
                    what.add(n(counts[0]) + (counts[0] == 1 ? " cell" : " cells"));
                }
                if (counts[1] > 0) {
                    what.add(n(counts[1]) + (counts[1] == 1 ? " reference row gone" : " reference rows gone"));
                }
                if (counts[2] > 0) {
                    what.add(n(counts[2]) + (counts[2] == 1 ? " row added" : " rows added"));
                }
                if (counts[3] > 0) {
                    what.add(counts[3] + " columns added, on " + n(entry.getValue().generatedRows) + " rows");
                }
                if (!what.isEmpty()) {
                    parts.add("`" + entry.getKey() + "` " + String.join(", ", what));
                }
            }
            int manifestRows = 0;
            for (ManifestLine line : c.manifest) {
                manifestRows += line.number == number ? 1 : 0;
            }
            if (manifestRows > 0) {
                parts.add("`manifest` " + manifestRows + (manifestRows == 1 ? " row" : " rows"));
            }
            for (String table : c.generatedOnly) {
                if (c.generatedOnlyNumber.getOrDefault(table, 0) == number) {
                    parts.add("new table `" + table + "`, " + n(c.generatedOnlyRows.get(table)) + " rows");
                }
            }
            if (!parts.isEmpty()) {
                md.append("- `").append(c.estate).append("`: ").append(String.join("; ", parts)).append(".\n");
            }
        }
        md.append("\n**How it was checked:** ").append(howChecked(number)).append("\n\n");
        md.append("**Examples** (reference, then generated):\n\n");
        examples(md, number, compared);
    }

    private static String howChecked(int number) {
        switch (number) {
            case 1:
                return "the guard columns are left out and the apostrophe taken off before comparing; the values must "
                        + "then be equal, and are counted under \"identical\" when they are.";
            case 5:
                return "for a cell, the reference's list must be the generated list with entries taken out, in the same "
                        + "order, and the reference's own `errors.csv` must report at least that many unresolved "
                        + "references for that object. For an error row that is gone, the generated tables must hold "
                        + "that many more resolved references for the object. For the `coverage` row, its count must "
                        + "equal the number of such error rows.";
            case 6:
                return "the reference cell is split where an identifier starts (the text `<estate>::<layer>::`, which "
                        + "cannot occur inside an identifier) and must give exactly the generated cell's lines, in "
                        + "order.";
            case 7:
                return "each internal id in the reference cell is looked up in the estate's repository files by the "
                        + "comparison itself; the Row identifier built from the names found there must be the "
                        + "generated entry in the same position, and a row of the generated `physical_columns`.";
            case 8:
                return "the estate's catalog file is read again with the JDK's DOM parser and the element the row "
                        + "stands for is found. The reference value must be that element's text before its first "
                        + "nested element, and the generated value its whole text (a formula) or its own text without "
                        + "nested expressions (a filter). `report_lineage.reference_text` is a copy of the report "
                        + "column's formula: it must equal `analysis_columns.formula` on each side, and that "
                        + "difference must itself pass the check. A count in `unparsed` must drop by exactly the "
                        + "number of nested texts counted in the files.";
            case 9:
                return "an added `analysis_filters` row must be, column for column, what the saved filter's file holds "
                        + "at that position, and the saved filter must have exactly as many rows as it has "
                        + "expressions. An added error row must belong to such a row whose reference did not resolve. "
                        + "The `coverage` count must rise by exactly the number of those error rows, and the "
                        + "`unparsed` counts must fall by exactly what the files hold.";
            case 10:
                return "parent and depth are worked out again from the estate's files for every row; one wrong value "
                        + "and both columns would be listed as unexplained.";
            case 11:
                return "the reference must hold exactly twice as many copies of the very same error row as the "
                        + "generated table, and the generated `dashboard_contents` must have one unresolved placement "
                        + "for each generated error of that page. The `coverage` count must be exactly halved.";
            case 15:
                return "the generated `rpd_ids` must be the one estate, and the reference's list must contain it.";
            case 16:
                return "the generated table list must be the reference's list plus exactly `schema` and "
                        + "`schema_tables`. The content of these two tables has no reference to compare with; the "
                        + "project's own tests check it (`SchemaTest`).";
            case 17:
                return "`extractor_version` and `corpus_root` are expected to differ; `corpus_root` must end in the "
                        + "estate name. Nothing else in the manifest may differ under this number.";
            case 19:
                return "the generated row must be marked `redacted` with empty value cells where the reference row is "
                        + "not, and the value the reference wrote must be, in the estate's own files, the value of a "
                        + "variable whose name indicates a credential. The added `unparsed` row must record that "
                        + "variable.";
            case 21:
                return "the file must be in the estate's catalog, and the JDK's own URL decoder must also find its "
                        + "name malformed.";
            case 22:
                return "both sides must name the same presentation column; the generated `lineage` must have at least "
                        + "two rows for it; the generated value must be that of the row with the smallest Row "
                        + "identifier; and the reference value must be that of another row of the reference's "
                        + "`lineage` for the same presentation column.";
            case 26:
                return "the comparison reads the estate's repository files itself. For `table2_id` the reference cell "
                        + "must be empty on a foreign key row, and the generated value must be the Row identifier, "
                        + "built from the names in the files, of the table that holds the key the foreign key's link "
                        + "names; it must be a row of the generated `physical_tables`. The added column is checked on "
                        + "every row: the key's columns in the key's own order for a foreign key, empty for a complex "
                        + "join. A count in `unparsed` must fall by exactly the number of links, keys, column lists and "
                        + "column references counted in the files.";
            case 27:
                return "the comparison reads the estate's repository files itself. On every query privilege row the two "
                        + "added cells must be the display name and guid in the file of the role the row's privilege "
                        + "package points at; on every other row they must be empty. A count in `unparsed` must fall by "
                        + "exactly the number of roles so pointed at that have the attribute.";
            case 34:
                return "the comparison reads the estate's repository files itself (`KeySource`). Each of the two new "
                        + "tables must hold exactly one row for each table and each join the files list under a "
                        + "source, with the Row identifier built from the names in the files, the join type and "
                        + "cardinality character for character, and the tables and the physical join as the "
                        + "identifiers their own names give. One wrong cell and the whole table is listed as "
                        + "unexplained. The two added `coverage` rows must count exactly the tables and joins in the "
                        + "files, and each `unparsed` row must be gone or hold exactly what the files leave outside "
                        + "what the jar reads.";
            case 35:
            case 36:
            case 37:
                return "the comparison reads the estate's repository files itself (`KeySource`). On every row of the "
                        + "table the added cells must be what the files say: an attribute exactly as written, a text "
                        + "exactly as written, or the Row identifier built from the names of the element a link "
                        + "points at" + (number == 35 ? "; a level's parents must be exactly the levels of its "
                                + "dimension that list it as a child, in the files' order" : "") + ". One wrong value "
                        + "and all the added columns of that table are listed as unexplained. An `unparsed` count is "
                        + "explained only when it is exactly what the files leave outside what the jar now reads.";
            case 28:
            case 29:
            case 30:
                return "the comparison reads the estate's repository files itself (`KeySource`, the JDK's streaming "
                        + "parser, not the jar's reader). The new table must hold exactly one row for each key the "
                        + "files hold, no more and no fewer, and every cell must be what the files say: the Row "
                        + "identifier built from the names in the files, the owner, the key's name, its flags as "
                        + "written, and its columns one per line in the files' order. One wrong cell and the whole "
                        + "table is listed as unexplained. A count in `unparsed` is explained only when it is exactly "
                        + "the number of occurrences the files hold outside what the jar now reads, and lower than "
                        + "the reference's. The added `coverage` row must count exactly the keys in the files"
                        + (number == 28 ? "." : "; keys of logical tables and of levels are both `LogicalKey` "
                                + "elements and share one row, reported under 29.");
            case 31:
                return "the comparison reads the estate's repository files itself. On every row of `physical_columns` "
                        + "the scale must be the column's `scale` attribute in the file (empty when there is none) "
                        + "and the position its place among the columns of its table in the file, counting from 1. "
                        + "The `unparsed` row for the scale must be gone, or hold exactly the count the files leave "
                        + "outside what the jar reads.";
            case 32:
                return "the comparison reads the estate's repository files itself. On every row of "
                        + "`logical_dimensions` the value must be the dimension's `isTimeDim` attribute exactly as "
                        + "written, and empty when the file has none. The `unparsed` row for the flag must be gone.";
            default:
                return "see `KbCompare.java`.";
        }
    }

    /** Up to three real examples, from as many different tables and columns as there are. */
    private static void examples(StringBuilder md, int number, List<Comparison> compared) {
        List<String> lines = new ArrayList<>();
        if (number == 1) {
            for (Comparison c : compared) {
                for (String example : c.guardExamples) {
                    if (lines.size() < EXAMPLES) {
                        lines.add(example);
                    }
                }
            }
        }
        Map<String, List<String>> byPlace = new LinkedHashMap<>();
        for (Comparison c : compared) {
            if (number == 10 && lines.isEmpty()) {
                lines.addAll(treeExamples(c));
            }
            if (number == 16 && lines.isEmpty() && c.generatedOnlyNumber.getOrDefault("schema", 0) == 16) {
                lines.addAll(schemaExamples(c));
            }
            if (lines.isEmpty()) {
                lines.addAll(newTableExamples(c, number));
                lines.addAll(addedColumnExamples(c, number));
            }
            for (Map.Entry<String, Result> entry : c.results.entrySet()) {
                Result r = entry.getValue();
                for (Change change : r.changes) {
                    if (change.number != number) {
                        continue;
                    }
                    String place = entry.getKey() + "|" + change.kind + "|" + change.column;
                    List<String> found = byPlace.computeIfAbsent(place, p -> new ArrayList<>());
                    if (found.size() < EXAMPLES) {
                        found.add(example(entry.getKey(), r, change));
                    }
                }
            }
            for (ManifestLine line : c.manifest) {
                if (line.number == number) {
                    byPlace.computeIfAbsent("manifest|" + line.key, p -> new ArrayList<>()).add("`manifest` of `" + c.estate
                            + "`, key `" + line.key + "`: reference " + code(shorten(line.reference)) + "; generated "
                            + code(shorten(line.generated)) + ".");
                }
            }
        }
        for (int round = 0; lines.size() < EXAMPLES && round < EXAMPLES; round++) {
            for (List<String> found : byPlace.values()) {
                if (round < found.size() && lines.size() < EXAMPLES && !lines.contains(found.get(round))) {
                    lines.add(found.get(round));
                }
            }
        }
        if (lines.isEmpty()) {
            md.append("- (none to show)\n");
        }
        for (String line : lines) {
            md.append("- ").append(line).append("\n");
        }
        md.append("\n");
    }

    /** Difference 10: three real filter rows with the two added columns, one at each depth where there is one. */
    private static List<String> treeExamples(Comparison c) {
        List<String> lines = new ArrayList<>();
        try {
            CsvTable filters = CsvReader.read(c.kb.resolve("analysis_filters.csv"));
            Set<String> depths = new TreeSet<>();
            for (String[] row : filters.rows) {
                String depth = filters.value(row, "depth");
                if (lines.size() < EXAMPLES && !filters.value(row, "scope").equals("dashboard") && depths.add(depth)) {
                    lines.add("`analysis_filters`, row " + code(filters.value(row, "row_id")) + ": the reference has no "
                            + "such columns; generated `parent_id` = " + code(filters.value(row, "parent_id"))
                            + ", `depth` = `" + depth + "`.");
                }
            }
        } catch (IOException e) {
            lines.add("`analysis_filters.csv` could not be read: " + e.getMessage());
        }
        return lines;
    }

    /** Differences 28 to 30: one real row of the table the difference adds. */
    private static List<String> newTableExamples(Comparison c, int number) {
        List<String> lines = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : c.generatedOnlyNumber.entrySet()) {
            if (entry.getValue() != number || number == 16) {
                continue;
            }
            try {
                CsvTable table = CsvReader.read(c.kb.resolve(entry.getKey() + ".csv"));
                if (!table.rows.isEmpty()) {
                    lines.add("Reference: no such table. Generated `" + entry.getKey() + "` of `" + c.estate + "`, first row: "
                            + code(shorten(describe(table.header, table.rows.get(0)), 420)) + ".");
                }
            } catch (IOException e) {
                lines.add("`" + entry.getKey() + ".csv` could not be read: " + e.getMessage());
            }
        }
        return lines;
    }

    /** A difference that adds columns to a table the reference has: one real row with a value in each such column. */
    private static List<String> addedColumnExamples(Comparison c, int number) {
        List<String> lines = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : new TreeMap<>(KbCompare.ADDED_NUMBER).entrySet()) {
            if (entry.getValue() != number || lines.size() >= EXAMPLES) {
                continue;
            }
            String table = entry.getKey().substring(0, entry.getKey().indexOf('.'));
            String column = entry.getKey().substring(entry.getKey().indexOf('.') + 1);
            Result r = c.results.get(table);
            if (r == null || !r.explainedColumns.getOrDefault(number, List.of()).contains(column)) {
                continue;
            }
            try {
                CsvTable rows = CsvReader.read(c.kb.resolve(table + ".csv"));
                for (String[] row : rows.rows) {
                    if (!rows.value(row, column).isEmpty()) {
                        lines.add("`" + table + "`, row " + code(rows.value(row, "row_id")) + ": the reference has no such "
                                + "column; generated `" + column + "` = " + code(shorten(rows.value(row, column), 200)) + ".");
                        break;
                    }
                }
            } catch (IOException e) {
                lines.add("`" + table + ".csv` could not be read: " + e.getMessage());
            }
        }
        return lines;
    }

    /** Difference 16: what the manifest's table list gains, and two real rows of the added tables. */
    private static List<String> schemaExamples(Comparison c) {
        List<String> lines = new ArrayList<>();
        lines.add("`manifest` of `" + c.estate + "`, key `tables`: the generated list is the reference's list with "
                + "`schema` and `schema_tables` added.");
        try {
            CsvTable tables = CsvReader.read(c.kb.resolve("schema_tables.csv"));
            if (!tables.rows.isEmpty()) {
                lines.add("Reference: no such table. Generated `schema_tables`, first row: "
                        + code(shorten(describe(tables.header, tables.rows.get(0)), 260)) + ".");
            }
            CsvTable columns = CsvReader.read(c.kb.resolve("schema.csv"));
            if (!columns.rows.isEmpty()) {
                lines.add("Reference: no such table. Generated `schema`, first row: "
                        + code(shorten(describe(columns.header, columns.rows.get(0)), 260)) + ".");
            }
        } catch (IOException e) {
            lines.add("The schema tables could not be read: " + e.getMessage());
        }
        return lines;
    }

    /**
     * One difference in words. A value of the {@code variables} table is never shown, and for difference 19
     * only the row is named: the reference holds a credential there.
     */
    static String example(String table, Result r, Change change) {
        String where = "`" + table + "`, row " + code(rowKey(table, r, change.row));
        boolean secret = change.number == 19 || (table.equals("variables") && !change.column.endsWith("_refs"));
        if (change.kind.equals(Change.COLUMN)) {
            return "`" + table + "`: reference has no column `" + change.column + "`; generated has it on every row.";
        }
        if (change.kind.equals(Change.CELL)) {
            if (change.number == 19) {
                return where + ", column `" + change.column + "`: the reference holds a value, the generated cell is "
                        + (change.column.equals("redacted") ? "`true`" : "empty") + ". (Value not shown.)";
            }
            if (secret) {
                return where + ", column `" + change.column + "`: the two values differ. (Values of this table are "
                        + "not shown; run `./build.sh compare` to see them.)";
            }
            return where + ", column `" + change.column + "`: reference " + code(shorten(change.reference)) + "; generated "
                    + code(shorten(change.generated)) + ".";
        }
        String side = change.kind.equals(Change.MISSING) ? "reference has this row, generated does not"
                : "reference has no such row, generated has it";
        if (secret) {
            return where + ": " + side + ". (Content not shown.)";
        }
        return "`" + table + "`: " + side + ": " + code(shorten(describe(r.columns, change.row))) + ".";
    }

    /** The row identifier; for a table without one, the cells that say what the row is about. */
    private static String rowKey(String table, Result r, String[] row) {
        if (row == null) {
            return "";
        }
        int rowId = r.columns.indexOf("row_id");
        if (rowId >= 0) {
            return row[rowId];
        }
        List<String> parts = new ArrayList<>();
        for (String column : List.of("kind", "layer", "object_type", "source_id")) {
            int index = r.columns.indexOf(column);
            if (index >= 0) {
                parts.add(column + "=" + row[index]);
            }
        }
        return parts.isEmpty() ? String.join(" | ", row) : String.join(", ", parts);
    }

    private static String describe(List<String> columns, String[] row) {
        List<String> parts = new ArrayList<>();
        for (int i = 0; i < columns.size(); i++) {
            if (!row[i].isEmpty() && !columns.get(i).equals("rpd_id")) {
                parts.add(columns.get(i) + "=" + row[i]);
            }
        }
        return String.join(", ", parts);
    }

    private static void didNotOccur(StringBuilder md, Set<Integer> occurred, List<Comparison> compared,
            List<Comparison> handMade) throws IOException {
        Map<String, Integer> kinds = new TreeMap<>();
        for (Comparison c : compared) {
            for (String table : List.of("errors", "unparsed")) {
                CsvTable generated = CsvReader.read(c.kb.resolve(table + ".csv"));
                for (String[] row : generated.rows) {
                    kinds.merge(table + "." + generated.value(row, "kind"), 1, Integer::sum);
                }
            }
        }
        int twentyTwo = 0;
        boolean pythonRan = false;
        for (Comparison c : handMade) {
            if (c.notRun.isEmpty()) {
                pythonRan = true;
                Result r = c.results.get("report_lineage");
                if (r != null) {
                    for (Change change : r.changes) {
                        twentyTwo += change.number == 22 ? 1 : 0;
                    }
                }
            }
        }
        Map<Integer, String> why = new TreeMap<>();
        why.put(2, "The estate name is the folder's name. The three estate folders carry the names the reference used, "
                + "so no table shows it.");
        why.put(3, "A name that cannot be part of a Row identifier becomes an error row and the run continues. The "
                + "samples have no such name: `errors` rows of kind `invalid_name` in the generated Knowledge Bases: "
                + kinds.getOrDefault("errors.invalid_name", 0) + ". Checked by the project's tests only.");
        why.put(4, "A source file that cannot be read becomes an error row and the run continues. The samples have "
                + "none: `errors` rows of kind `unreadable`: " + kinds.getOrDefault("errors.unreadable", 0)
                + ". Checked by the project's tests only.");
        why.put(12, "About rows for files that were not discovered. Generated `unparsed` rows of kind "
                + "`undiscovered_file`: " + kinds.getOrDefault("unparsed.undiscovered_file", 0)
                + "; where there are any, they equal the reference's.");
        why.put(13, "About the slicer, which is not part of `extract`. Not covered by this comparison.");
        why.put(14, "About emptying the output folder before writing. It changes no table. Checked by the project's "
                + "tests only.");
        why.put(18, "A repository folder that cannot be listed. The samples have none.");
        why.put(20, "A catalog folder that cannot be listed. The samples have none.");
        why.put(22, "It can only occur where a report column resolves to a presentation column, and none does on the "
                + "samples. " + (pythonRan ? "On the hand-made estates (section 5) it accounts for " + twentyTwo + " cells."
                        : "It could not be checked against the reference implementation in this run (section 5)."));
        md.append("| # | Why it does not show here |\n|---|---|\n");
        for (Map.Entry<Integer, String> entry : why.entrySet()) {
            if (!occurred.contains(entry.getKey())) {
                md.append("| ").append(entry.getKey()).append(" | ").append(entry.getValue()).append(" |\n");
            }
        }
        md.append("\n");
    }

    /** Lists what nothing explains in one comparison. Returns false when there is nothing to list. */
    private static boolean unexplainedSection(StringBuilder md, Comparison c) {
        List<String> lines = new ArrayList<>();
        for (Map.Entry<String, Result> entry : c.results.entrySet()) {
            Result r = entry.getValue();
            if (!r.problem.isEmpty()) {
                lines.add("`" + entry.getKey() + "`: " + r.problem + ".");
                continue;
            }
            for (Change change : r.changes) {
                if (change.number == 0) {
                    lines.add(example(entry.getKey(), r, change));
                }
            }
        }
        for (ManifestLine line : c.manifest) {
            if (line.verdict.equals(ManifestLine.UNEXPLAINED)) {
                lines.add("`manifest`, key `" + line.key + "`: reference " + code(shorten(line.reference)) + "; generated "
                        + code(shorten(line.generated)) + " (" + line.why + ").");
            }
        }
        for (String table : c.unexplainedTables()) {
            lines.add("Table only the jar writes, which no deliberate difference explains: `" + table + "`.");
        }
        if (lines.isEmpty()) {
            return false;
        }
        md.append("**`").append(c.estate).append("`: ").append(lines.size()).append(" unexplained.** Each is an open defect ")
                .append("until it is fixed in the jar or added to the list of deliberate differences with a reason.\n\n");
        int shown = 0;
        for (String line : lines) {
            if (shown++ == 200) {
                md.append("- ... and ").append(lines.size() - 200).append(" more; run `./build.sh compare ").append(c.estate)
                        .append(" --show 100000` for all of them.\n");
                break;
            }
            md.append("- ").append(line).append("\n");
        }
        md.append("\n");
        return true;
    }

    /** How the report columns of a hand-made estate resolved, on both sides. */
    private static void lineageOutcomes(StringBuilder md, Comparison c) throws IOException {
        CsvTable ours = CsvReader.read(c.kb.resolve("report_lineage.csv"));
        CsvTable theirs = KbCompare.reference(c.reference.resolve("report_lineage.csv"));
        Map<String, int[]> counts = new TreeMap<>();
        for (String[] row : ours.rows) {
            counts.computeIfAbsent(outcome(ours, row), k -> new int[2])[0]++;
        }
        for (String[] row : theirs.rows) {
            counts.computeIfAbsent(outcome(theirs, row), k -> new int[2])[1]++;
        }
        md.append("Report columns by outcome (`report_lineage`):\n\n");
        md.append("| `resolution` | `source_state` | Jar | Reference implementation |\n|---|---|---:|---:|\n");
        for (Map.Entry<String, int[]> entry : counts.entrySet()) {
            String[] parts = entry.getKey().split("\\|", -1);
            md.append("| `").append(parts[0]).append("` | ").append(parts[1].isEmpty() ? "(empty)" : "`" + parts[1] + "`")
                    .append(" | ").append(entry.getValue()[0]).append(" | ").append(entry.getValue()[1]).append(" |\n");
        }
        md.append("\n");
    }

    private static String outcome(CsvTable table, String[] row) {
        return table.value(row, "resolution") + "|" + table.value(row, "source_state");
    }

    private static void doesNotProve(StringBuilder md, List<Comparison> compared) throws IOException {
        int lineageRows = 0;
        int resolved = 0;
        int referenceResolved = 0;
        for (Comparison c : compared) {
            CsvTable ours = CsvReader.read(c.kb.resolve("report_lineage.csv"));
            for (String[] row : ours.rows) {
                lineageRows++;
                resolved += ours.value(row, "presentation_column_id").isEmpty() ? 0 : 1;
            }
            CsvTable theirs = KbCompare.reference(c.reference.resolve("report_lineage.csv"));
            for (String[] row : KbCompare.estateRows(theirs, c.estate)) {
                referenceResolved += theirs.value(row, "presentation_column_id").isEmpty() ? 0 : 1;
            }
        }
        md.append("- **It proves the jar matches the Python program, not that either is right about OBIEE.** Where the ")
                .append("Python program misreads a file, the jar misreads it the same way, and the comparison is ")
                .append("silent. Neither program has been checked against a running OBIEE system.\n");
        md.append("- **The sample data comes from three unrelated sources.** Two public repositories and one public ")
                .append("catalog, each from a different system. No estate has a repository and a catalog that belong ")
                .append("together. Features the samples do not use were not compared at all; the list of deliberate ")
                .append("differences that did not occur (section 3) names some.\n");
        md.append("- **Report lineage never resolves on the samples.** Of the ").append(n(lineageRows))
                .append(" `report_lineage` rows the jar wrote, ").append(resolved).append(" name a presentation column; ")
                .append("in the reference, ").append(referenceResolved).append(". The comparison shows the two programs ")
                .append("agree on why each report column does not link, nothing more. That a link is found, and the ")
                .append("right one, is shown only on the two small hand-made estates of section 5, whose halves were ")
                .append("written by hand to match each other. It stays unproven on real data until an estate with ")
                .append("both halves from one OBIEE system is available.\n");
        md.append("- **The schema files have no reference counterpart.** `schema_tables.csv` and `schema.csv` are new ")
                .append("(difference 16). Their wording was written in this project and is checked only by its own ")
                .append("tests, which require a meaning for every column and a real target for every stated link.\n");
        md.append("- **`manifest.csv` differs by design.** Its version, input folder, estate list and table list are ")
                .append("expected to differ (differences 15, 16, 17), and its time stamp always does. Only the two ")
                .append("lines that say which halves were present, and how they were paired, are compared for ")
                .append("equality.\n");
        md.append("- **The checks for explained differences are only as good as their rules.** A difference is ")
                .append("explained when it passes the check described in section 3. For differences 7, 8, 9, 10, 19, ")
                .append("21 and 26 to 37 the comparison reads the source files itself, with different code from the jar's. ")
                .append("Differences 5, 6, 11 and 22 are checked from the two Knowledge Bases alone.\n");
        md.append("- **Identical means identical in the columns both sides have.** The reference's guard columns are ")
                .append("left out (difference 1), and the columns the jar adds (two in `analysis_filters`, difference 10; ")
                .append("one in `physical_joins`, difference 26; two in `security_rls`, difference 27; and the ")
                .append("columns of differences 31, 32 and 35 to 37 in seven repository tables) have ")
                .append("no reference value to compare with; they are checked against the source files instead.\n");
        md.append("- **Five tables have no reference counterpart.** `physical_keys`, `logical_keys`, ")
                .append("`logical_level_keys` (differences 28 to 30), `lts_tables` and `lts_joins` (difference 34) are ")
                .append("new. They are not compared with the reference, ")
                .append("which has nothing to compare with; every row is checked against the estate's own files, read ")
                .append("a second time. That shows the jar wrote what the files hold. It does not show what a key ")
                .append("means in the database: the repository has no flag that says which physical key is the ")
                .append("primary key, and nobody has compared these keys with the constraints of a real database. ")
                .append("Likewise a join type in `lts_joins` is the repository's own word (`Inner`, `LeftOuter`); that ")
                .append("the server writes that join into its SQL has not been checked against a running OBIEE.\n");
        md.append("- **One run, one machine.** The comparison was run on Linux with the JDK on this machine. The ")
                .append("Windows build script `build.cmd` has the same commands and was not run here.\n");
        md.append("- **Later commands are not covered.** The slicer and the skills that read the Knowledge Base are ")
                .append("other stories.\n");
    }

    // ------------------------------------------------------------------ text

    private static String n(long number) {
        return String.format(Locale.ROOT, "%,d", number);
    }

    private static String slash(Path path) {
        return path.toString().replace(File.separatorChar, '/');
    }

    /** A value made short and printable: line breaks and tabs shown as escapes, cut at a fixed length. */
    static String shorten(String value) {
        return shorten(value, SHOWN);
    }

    static String shorten(String value, int length) {
        String shown = value.replace("\\", "\\\\").replace("\r", "\\r").replace("\n", "\\n").replace("\t", "\\t");
        if (shown.length() <= length) {
            return shown;
        }
        int cut = length;
        if (Character.isHighSurrogate(shown.charAt(cut - 1))) {
            cut--;
        }
        return shown.substring(0, cut) + " ...(" + n(value.length()) + " characters in all)";
    }

    /** The value as a Markdown code span, safe inside a table cell or a list item. */
    static String code(String value) {
        if (value.isEmpty()) {
            return "(empty)";
        }
        String text = value.replace("|", "\\|");
        String fence = "`";
        while (text.contains(fence)) {
            fence += "`";
        }
        boolean pad = text.startsWith("`") || text.endsWith("`") || text.startsWith(" ") || text.endsWith(" ");
        return fence + (pad ? " " : "") + text + (pad ? " " : "") + fence;
    }

    private static String cellText(String text) {
        return text.replace("|", "\\|");
    }
}
