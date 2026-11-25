package obi2pbi.in.catalog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeSet;
import obi2pbi.core.PathResolution;
import obi2pbi.core.Text;

/**
 * Every decoded path in the catalog, and whether it names an object or a folder.
 *
 * <p>Built from every file, whatever it holds: a reference can point at a template or an image as rightly
 * as at a report. Folders are indexed too, because some references name a folder.
 *
 * <p>The second index, by lower-cased path, exists because the files on disk are usually lower case while
 * references keep the case the catalog shows.
 */
final class PathIndex {

    /** The result of looking one path up. */
    static final class Found {
        /** One of the outcomes of {@link PathResolution}. */
        final String outcome;
        /** The indexed path; for an ambiguous match, the candidates joined with {@code " | "}. */
        final String target;
        final String kind;

        Found(String outcome, String target, String kind) {
            this.outcome = outcome;
            this.target = target;
            this.kind = kind;
        }
    }

    private final Map<String, String> kindOf = new HashMap<>();
    private final Map<String, List<String>> folded = new HashMap<>();

    /** Adds a file's path as an object, and every folder above it as a folder. The first entry for a path wins. */
    void addObject(String decoded) {
        add(decoded, PathResolution.OBJECT);
        // Split on every slash, as the reference does: a name that decodes to hold one counts as two levels.
        for (int cut = decoded.indexOf('/'); cut >= 0; cut = decoded.indexOf('/', cut + 1)) {
            add(decoded.substring(0, cut), PathResolution.FOLDER);
        }
    }

    private void add(String decoded, String kind) {
        if (kindOf.containsKey(decoded)) {
            return;
        }
        kindOf.put(decoded, kind);
        folded.computeIfAbsent(fold(decoded), k -> new ArrayList<>()).add(decoded);
    }

    /** Exact match first; then the one path that matches ignoring case; several such is ambiguous. */
    Found lookup(String candidate) {
        String kind = kindOf.get(candidate);
        if (kind != null) {
            return new Found(PathResolution.EXACT, candidate, kind);
        }
        TreeSet<String> matches = new TreeSet<>(Text.CODE_POINT_ORDER);
        matches.addAll(folded.getOrDefault(fold(candidate), List.of()));
        if (matches.size() == 1) {
            return new Found(PathResolution.CASE_NORMALISED, matches.first(), kindOf.get(matches.first()));
        }
        if (matches.size() > 1) {
            return new Found(PathResolution.AMBIGUOUS, String.join(" | ", matches), "");
        }
        return new Found(PathResolution.UNRESOLVED, "", "");
    }

    private static String fold(String path) {
        return path.toLowerCase(Locale.ROOT);
    }
}
