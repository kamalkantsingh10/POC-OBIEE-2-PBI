package obi2pbi.in.catalog;

import java.io.IOException;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import obi2pbi.core.Text;

/**
 * Finds every file under a catalog root.
 *
 * <p>There is no filter on names: catalog objects have no extension, so every file is a candidate, dot
 * files included. The one thing left out is anything named {@code .git}, at any depth: version-control
 * data, not catalog content. A {@code .gitignore} is not left out; it is read and found not to be a
 * catalog object.
 *
 * <p>Order: depth first, the entries of each folder sorted by their on-disk name in code-point order. This
 * is the reference implementation's order (paths compared segment by segment) and never depends on the
 * order the file system lists things in. Links are followed; a folder reached twice is walked once.
 */
final class CatalogFiles {

    static final String VERSION_CONTROL = ".git";

    final List<CatalogFile> files = new ArrayList<>();
    /** Folders that could not be listed: the on-disk path below the root, and why. */
    final List<String[]> unlistable = new ArrayList<>();

    private CatalogFiles() {
    }

    static CatalogFiles discover(Path root) {
        CatalogFiles found = new CatalogFiles();
        found.walk(root, "", "", true, new HashSet<>());
        return found;
    }

    private void walk(Path folder, String onDisk, String decoded, boolean clean, Set<Path> visited) {
        List<Path> entries;
        try {
            if (!visited.add(folder.toRealPath())) {
                return;
            }
            entries = list(folder);
        } catch (IOException e) {
            unlistable.add(new String[] {onDisk.isEmpty() ? "." : onDisk, "cannot list the folder: " + e});
            return;
        }
        for (Path entry : entries) {
            String name = entry.getFileName().toString();
            if (name.equals(VERSION_CONTROL)) {
                continue;
            }
            CatalogNames.Decoded segment = CatalogNames.decode(name);
            String entryOnDisk = onDisk.isEmpty() ? name : onDisk + "/" + name;
            String entryDecoded = decoded.isEmpty() && onDisk.isEmpty() ? segment.text : decoded + "/" + segment.text;
            boolean entryClean = clean && segment.clean;
            if (Files.isDirectory(entry)) {
                walk(entry, entryOnDisk, entryDecoded, entryClean, visited);
            } else if (Files.isRegularFile(entry)) {
                files.add(new CatalogFile(entry, entryOnDisk, entryDecoded, entryClean));
            }
        }
    }

    private static List<Path> list(Path folder) throws IOException {
        List<Path> entries = new ArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(folder)) {
            for (Path entry : stream) {
                entries.add(entry);
            }
        } catch (DirectoryIteratorException e) {
            throw e.getCause();
        }
        entries.sort((a, b) -> Text.compareCodePoints(a.getFileName().toString(), b.getFileName().toString()));
        return entries;
    }
}
