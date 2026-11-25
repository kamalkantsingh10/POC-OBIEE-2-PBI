package obi2pbi.out;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import obi2pbi.core.Estate;
import obi2pbi.core.KbSchema;
import obi2pbi.core.KbTable;
import obi2pbi.core.Text;

/**
 * Writes an {@link Estate} as a Knowledge Base folder: one CSV file per table of {@link KbSchema}, the two
 * schema files that describe them, and {@code manifest.csv}.
 *
 * <p>The folder is rewritten whole on each run. A folder that is not empty and has no {@code manifest.csv}
 * was not written by this jar and is refused, untouched. Only the top-level {@code .csv} files are ever
 * removed; anything else in the folder is left alone.
 */
public final class KnowledgeBaseWriter {

    public static final String VERSION = "0.1.0";

    private static final String MANIFEST_FILE = KbSchema.MANIFEST + ".csv";

    private KnowledgeBaseWriter() {
    }

    /** Checks, without changing anything, that the folder may be rewritten. Call before doing the work. */
    public static void checkOurs(Path folder) throws NotAKnowledgeBaseException, IOException {
        if (!Files.exists(folder)) {
            return;
        }
        if (!Files.isDirectory(folder)) {
            throw new NotAKnowledgeBaseException(
                    "refusing to write to " + folder + ": it exists and is not a folder.");
        }
        if (Files.exists(folder.resolve(MANIFEST_FILE))) {
            return;
        }
        for (Path entry : list(folder)) {
            if (!entry.getFileName().toString().equals(".gitignore")) {
                throw new NotAKnowledgeBaseException(
                        "refusing to empty " + folder + ": it is not empty and has no manifest.csv, so "
                                + "this program did not write it. Point --out at a Knowledge Base folder, "
                                + "or at a new one.");
            }
        }
    }

    /** Removes the tables of an earlier run. Returns the names of the files removed, sorted. */
    public static List<String> clean(Path folder) throws NotAKnowledgeBaseException, IOException {
        checkOurs(folder);
        List<String> removed = new ArrayList<>();
        if (!Files.exists(folder)) {
            return removed;
        }
        for (Path entry : list(folder)) {
            String name = entry.getFileName().toString();
            if (name.endsWith(".csv") && Files.isRegularFile(entry)) {
                Files.delete(entry);
                removed.add(name);
            }
        }
        return removed;
    }

    /**
     * Empties the folder of an earlier run's tables and writes every table.
     *
     * @param corpusRoot the estate folder as the Operator typed it, for the manifest
     * @param timestamp  the manifest's time stamp, or null for the current time in UTC
     * @return the names of the tables written, the manifest excluded
     */
    public static List<String> write(Estate estate, Path folder, String corpusRoot, String timestamp)
            throws NotAKnowledgeBaseException, IOException {
        clean(folder);
        Files.createDirectories(folder);
        List<String> written = new ArrayList<>();
        for (KbTable table : KbSchema.TABLES) {
            if (!table.hasRows()) {
                continue;
            }
            writeTable(folder, table, table.rows(estate));
            written.add(table.name);
        }
        writeTable(folder, KbSchema.table(KbSchema.MANIFEST),
                manifestRows(estate, corpusRoot, timestamp == null ? now() : timestamp, written));
        return written;
    }

    private static void writeTable(Path folder, KbTable table, List<String[]> rows) throws IOException {
        Path file = folder.resolve(table.name + ".csv");
        if (table.sorted) {
            CsvWriter.writeSorted(file, table.header(), rows);
        } else {
            CsvWriter.write(file, table.header(), rows);
        }
    }

    private static List<String[]> manifestRows(Estate estate, String corpusRoot, String timestamp,
            List<String> written) {
        List<String[]> rows = new ArrayList<>();
        rows.add(new String[] {"extractor_version", VERSION});
        rows.add(new String[] {"extracted_at", timestamp});
        rows.add(new String[] {"corpus_root", corpusRoot});
        rows.add(new String[] {"rpd_ids", String.join(" ", estate.rpdIds)});
        for (String rpdId : estate.halvesPresent.keySet()) {
            List<String> halves = estate.halvesPresent.get(rpdId);
            rows.add(new String[] {"halves_present:" + rpdId, halves.isEmpty() ? "none" : String.join(" ", halves)});
            String pairing;
            if (halves.size() > 1) {
                pairing = "declared by the Operator: both halves supplied in one estate folder under one "
                        + "estate name (AD-7). Never inferred.";
            } else if (halves.size() == 1) {
                pairing = "not paired: only the " + halves.get(0) + " half was supplied";
            } else {
                pairing = "not paired: no half was supplied";
            }
            rows.add(new String[] {"pairing:" + rpdId, pairing});
        }
        List<String> sorted = new ArrayList<>(written);
        sorted.sort(Text.CODE_POINT_ORDER);
        rows.add(new String[] {"tables", String.join(" ", sorted)});
        return rows;
    }

    /** The current time in UTC, as the manifest writes it. */
    public static String now() {
        return ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'"));
    }

    private static List<Path> list(Path folder) throws IOException {
        List<Path> entries = new ArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(folder)) {
            for (Path entry : stream) {
                entries.add(entry);
            }
        }
        entries.sort((a, b) -> Text.compareCodePoints(a.getFileName().toString(), b.getFileName().toString()));
        return entries;
    }
}
