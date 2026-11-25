package obi2pbi;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import obi2pbi.cli.Main;
import obi2pbi.in.kb.CsvReader;
import obi2pbi.in.kb.CsvTable;

/**
 * Shared test plumbing: where things are, building a temporary estate folder under {@code build/}, running
 * a command in-process, and reading a table back.
 *
 * <p>Fixtures are never edited. A test copies a fixture into an estate folder of its own, or writes small
 * XML files there.
 */
public final class TestSupport {

    public static final String XMLNS = "http://www.oracle.com/obis/repository";

    /** The result of running a command line in-process. */
    public static final class Run {
        public final int exit;
        public final String out;
        public final String err;

        Run(int exit, String out, String err) {
            this.exit = exit;
            this.out = out;
            this.err = err;
        }
    }

    private TestSupport() {
    }

    /** The project root. The build scripts run the tests from it. */
    public static Path root() {
        return Paths.get(System.getProperty("obi2pbi.root", "")).toAbsolutePath().normalize();
    }

    /** The first attempt's small repository fixture: a bare repository tree. */
    public static Path testRpd() {
        return root().resolve("src/test/fixtures/testrpd");
    }

    /** The second hand-made repository: a bare repository tree. */
    public static Path constructed() {
        return root().resolve("reference/test-corpus/constructed-cases/constructed");
    }

    /** The hand-made catalog: one dashboard, one page, one report, one saved filter. A bare catalog tree. */
    public static Path constructedCatalog() {
        return root().resolve("reference/test-corpus/constructed-cases/constructed-catalog");
    }

    /** Writes one catalog file into an estate: {@code <estate>/catalog/<path as on disk>}. */
    public static Path catalogFile(Path estateFolder, String onDisk, String text) {
        return write(estateFolder.resolve("catalog").resolve(onDisk), text);
    }

    /** Writes one catalog file that is not text. */
    public static Path catalogBytes(Path estateFolder, String onDisk, byte[] content) {
        Path file = estateFolder.resolve("catalog").resolve(onDisk);
        try {
            Files.createDirectories(file.getParent());
            Files.write(file, content);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return file;
    }

    /** A fresh, empty folder under {@code build/test-work/} for one test. */
    public static Path work(String name) {
        Path folder = root().resolve("build/test-work").resolve(name);
        try {
            deleteTree(folder);
            Files.createDirectories(folder);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return folder;
    }

    /** Makes {@code <work>/<estate>/rpd} as a copy of a bare repository tree. Returns the estate folder. */
    public static Path estateFrom(Path work, String estate, Path repositoryTree) {
        Path folder = work.resolve(estate);
        try {
            copyTree(repositoryTree, folder.resolve("rpd"));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return folder;
    }

    /** Writes one repository object file into an estate: {@code <estate>/rpd/oracle/bi/server/base/<type>/<file>}. */
    public static Path object(Path estateFolder, String type, String file, String xml) {
        return write(estateFolder.resolve("rpd/oracle/bi/server/base").resolve(type).resolve(file), xml);
    }

    /** A schema file, so that physical tables in a hand-written estate have a container. */
    public static void schema(Path estateFolder, String mdsid, String name) {
        object(estateFolder, "Schema", mdsid + ".xml",
                "<Schema mdsid=\"" + mdsid + "\" name=\"" + name + "\" xmlns=\"" + XMLNS + "\"/>");
    }

    public static String schemaRef(String mdsid) {
        return "/oracle/bi/server/base/Schema/" + mdsid + ".xml#" + mdsid;
    }

    public static Path write(Path file, String text) {
        try {
            Files.createDirectories(file.getParent());
            Files.write(file, text.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return file;
    }

    public static Run run(String... args) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayOutputStream err = new ByteArrayOutputStream();
        int exit;
        try (PrintStream o = new PrintStream(out, true, "UTF-8"); PrintStream e = new PrintStream(err, true, "UTF-8")) {
            exit = Main.run(args, o, e);
        } catch (java.io.UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }
        return new Run(exit, new String(out.toByteArray(), StandardCharsets.UTF_8),
                new String(err.toByteArray(), StandardCharsets.UTF_8));
    }

    /** Runs {@code extract <estate> --out <out> --timestamp -} in-process. */
    public static Run extract(Path estateFolder, Path out) {
        return run("extract", estateFolder.toString(), "--out", out.toString(), "--timestamp", "-");
    }

    public static CsvTable table(Path kb, String name) {
        try {
            return CsvReader.read(kb.resolve(name + ".csv"));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /** The rows whose named column has the given value. */
    public static List<String[]> rows(CsvTable table, String column, String value) {
        List<String[]> found = new ArrayList<>();
        for (String[] row : table.rows) {
            if (table.value(row, column).equals(value)) {
                found.add(row);
            }
        }
        return found;
    }

    /** The one row whose named column has the given value; fails when there is not exactly one. */
    public static String[] row(CsvTable table, String column, String value) {
        List<String[]> found = rows(table, column, value);
        Check.equal(1, found.size(), "rows with " + column + " = " + value);
        return found.get(0);
    }

    /** The values of one column, in file order. */
    public static List<String> column(CsvTable table, String column) {
        List<String> values = new ArrayList<>();
        for (String[] row : table.rows) {
            values.add(table.value(row, column));
        }
        return values;
    }

    /** The names of the regular files directly in a folder, sorted. */
    public static List<String> fileNames(Path folder) {
        try (Stream<Path> entries = Files.list(folder)) {
            return entries.filter(Files::isRegularFile).map(p -> p.getFileName().toString()).sorted()
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static void copyTree(Path from, Path to) throws IOException {
        try (Stream<Path> walk = Files.walk(from)) {
            for (Path source : walk.collect(Collectors.toList())) {
                Path target = to.resolve(from.relativize(source).toString());
                if (Files.isDirectory(source)) {
                    Files.createDirectories(target);
                } else {
                    Files.createDirectories(target.getParent());
                    Files.copy(source, target);
                }
            }
        }
    }

    public static void deleteTree(Path folder) throws IOException {
        if (!Files.exists(folder)) {
            return;
        }
        try (Stream<Path> walk = Files.walk(folder)) {
            for (Path path : walk.sorted(Comparator.reverseOrder()).collect(Collectors.toList())) {
                Files.delete(path);
            }
        }
    }
}
