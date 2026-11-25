package obi2pbi.in.kb;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import obi2pbi.core.KbData;
import obi2pbi.core.KbRows;

/**
 * Reads a Knowledge Base folder, or a Slice folder, back into memory.
 *
 * <p>The table list comes from {@code manifest.csv}, never from the directory (Architecture AD-8): a file
 * the manifest does not list is not read, and a listed table whose file is missing is an error.
 */
public final class KbFolderReader {

    private static final String MANIFEST = "manifest";

    private KbFolderReader() {
    }

    /** Reads the manifest and every table it lists. */
    public static KbData read(Path folder) throws KbFormatException, IOException {
        return read(folder, null);
    }

    /**
     * Reads the manifest and the listed tables that are also named in {@code only}; all of them when
     * {@code only} is null. A table named in {@code only} and not listed by the manifest is simply absent.
     */
    public static KbData read(Path folder, java.util.Collection<String> only) throws KbFormatException, IOException {
        if (!Files.isDirectory(folder)) {
            throw new KbFormatException(folder + " is not a folder.");
        }
        Path manifestFile = folder.resolve(MANIFEST + ".csv");
        if (!Files.isRegularFile(manifestFile)) {
            throw new KbFormatException(folder + " has no manifest.csv, so it is not a Knowledge Base or a "
                    + "Slice written by this program.");
        }
        CsvTable manifest = CsvReader.read(manifestFile);
        if (manifest.indexOf("key") < 0 || manifest.indexOf("value") < 0) {
            throw new KbFormatException(manifestFile + " has no 'key' and 'value' columns.");
        }
        KbData data = new KbData();
        for (String[] row : manifest.rows) {
            data.manifest.put(manifest.value(row, "key"), manifest.value(row, "value"));
        }
        data.put(new KbRows(MANIFEST, manifest.header, manifest.rows));
        for (String name : data.fact("tables").split(" ")) {
            if (name.isEmpty() || name.equals(MANIFEST) || (only != null && !only.contains(name))) {
                continue;
            }
            Path file = folder.resolve(name + ".csv");
            if (!Files.isRegularFile(file)) {
                throw new KbFormatException("manifest.csv lists table '" + name + "' but " + file
                        + " is missing. Write the folder again.");
            }
            CsvTable table = CsvReader.read(file);
            data.put(new KbRows(name, table.header, table.rows));
        }
        return data;
    }
}
