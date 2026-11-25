package obi2pbi.in.mds;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import obi2pbi.core.Text;

/**
 * Finds the files of one repository export: {@code <root>/oracle/bi/server/base/<ObjectType>/*.xml}, one
 * level deep. The folder name is the object type; the root element is not checked against it.
 *
 * <p>Everything is sorted by name in code-point order, so the result never depends on the order the file
 * system lists things in. Symbolic links are followed.
 */
public final class RepositoryFiles {

    private static final Comparator<Path> BY_NAME =
            (a, b) -> Text.compareCodePoints(a.getFileName().toString(), b.getFileName().toString());

    /** The discovered files, by object type then file name. */
    public final List<ObjectFile> files = new ArrayList<>();
    /** One sentence for each object-type folder that could not be listed. */
    public final List<String> unreadable = new ArrayList<>();
    /** Files under the root that discovery did not pick up, as paths relative to the root with {@code /}. */
    public final List<String> undiscovered = new ArrayList<>();

    private RepositoryFiles() {
    }

    public static Path base(Path root) {
        return root.resolve("oracle").resolve("bi").resolve("server").resolve("base");
    }

    public static RepositoryFiles discover(Path root) throws RepositoryLayoutException {
        Path base = base(root);
        if (!Files.isDirectory(base)) {
            throw new RepositoryLayoutException(
                    root + " does not hold oracle/bi/server/base/, so it is not a repository export.");
        }
        RepositoryFiles found = new RepositoryFiles();
        List<Path> typeFolders;
        try {
            typeFolders = list(base);
        } catch (IOException e) {
            throw new RepositoryLayoutException("cannot list " + base + ": " + e.getMessage());
        }
        for (Path typeFolder : typeFolders) {
            if (!Files.isDirectory(typeFolder)) {
                continue;
            }
            List<Path> entries;
            try {
                entries = list(typeFolder);
            } catch (IOException e) {
                found.unreadable.add("cannot list " + typeFolder + ": " + e.getMessage());
                continue;
            }
            for (Path entry : entries) {
                if (entry.getFileName().toString().endsWith(".xml")) {
                    found.files.add(new ObjectFile(typeFolder.getFileName().toString(), entry));
                }
            }
        }
        found.findUndiscovered(root);
        return found;
    }

    /**
     * Every regular file under the root that is not in {@link #files}. A file nested one level too deep, or
     * saved without the {@code .xml} ending, would otherwise appear in no report at all.
     */
    private void findUndiscovered(Path root) {
        Set<Path> discovered = new HashSet<>();
        for (ObjectFile file : files) {
            discovered.add(file.path);
        }
        Set<Path> visited = new HashSet<>();
        List<Path> pending = new ArrayList<>();
        pending.add(root);
        while (!pending.isEmpty()) {
            Path folder = pending.remove(pending.size() - 1);
            try {
                // A link can point back up the tree; each real folder is walked once.
                if (!visited.add(folder.toRealPath())) {
                    continue;
                }
                for (Path entry : list(folder)) {
                    if (Files.isDirectory(entry)) {
                        if (!hasXmlEnding(entry) || !discovered.contains(entry)) {
                            pending.add(entry);
                        }
                    } else if (!discovered.contains(entry)) {
                        undiscovered.add(relative(root, entry));
                    }
                }
            } catch (IOException e) {
                // A folder that cannot be listed here was either reported by discovery or holds no
                // discovered file; its own entry is reported so that it is not passed over in silence.
                if (!folder.equals(root)) {
                    undiscovered.add(relative(root, folder) + "/ (cannot list: " + e.getMessage() + ")");
                }
            }
        }
        undiscovered.sort(Text.CODE_POINT_ORDER);
    }

    private static boolean hasXmlEnding(Path path) {
        return path.getFileName().toString().endsWith(".xml");
    }

    private static String relative(Path root, Path entry) {
        StringBuilder text = new StringBuilder();
        for (Path part : root.relativize(entry)) {
            if (text.length() > 0) {
                text.append('/');
            }
            text.append(part.toString());
        }
        return text.toString();
    }

    private static List<Path> list(Path folder) throws IOException {
        List<Path> entries = new ArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(folder)) {
            for (Path entry : stream) {
                entries.add(entry);
            }
        } catch (java.nio.file.DirectoryIteratorException e) {
            throw e.getCause();
        }
        entries.sort(BY_NAME);
        return entries;
    }
}
