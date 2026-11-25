package obi2pbi.in.catalog;

import obi2pbi.core.Coverage;
import obi2pbi.core.Estate;
import obi2pbi.core.ExtractionError;
import obi2pbi.core.IdentifierException;
import obi2pbi.core.Identifiers;
import obi2pbi.core.PathResolution;

/**
 * Resolves the path references of catalog objects, and records how each one resolved.
 *
 * <p>Three rules turn a reference into a path below the catalog root. A reference starting with
 * {@code /shared/} is absolute, and the tree on disk begins one level below that root, so the prefix is
 * removed. A reference with no leading {@code /} is relative to the folder of the object that holds it. A
 * reference under any other root names content this tree does not contain.
 *
 * <p>A reference is never repaired and never guessed at: one that does not resolve, or that matches
 * several paths once case is ignored, gets an error row and an empty target.
 */
final class PathResolver {

    static final String SHARED_ROOT = "/shared/";

    private final PathIndex index;
    private final String rpdId;
    private final Estate estate;

    PathResolver(PathIndex index, String rpdId, Estate estate) {
        this.index = index;
        this.rpdId = rpdId;
        this.estate = estate;
    }

    /** The reference as a path below the catalog root, or empty when it is outside the tree. */
    static String normalise(String reference, String ownerPath) {
        if (reference.startsWith(SHARED_ROOT)) {
            return reference.substring(SHARED_ROOT.length());
        }
        if (reference.startsWith("/")) {
            return "";
        }
        String ownerFolder = CatalogNames.folderOf(ownerPath);
        return ownerFolder.isEmpty() ? reference : ownerFolder + "/" + reference;
    }

    /**
     * @param reference the path as the source wrote it
     * @param ownerPath the decoded path of the object that holds the reference; also the error's source
     * @param element   the full name of the referring element; the error's object type
     * @param expected  {@link PathResolution#OBJECT} or {@link PathResolution#FOLDER}: what the element
     *                  should point at. The other kind still resolves, and the detail says so.
     */
    PathResolution resolve(String reference, String ownerPath, String element, String expected) {
        String candidate = normalise(reference, ownerPath);
        if (candidate.isEmpty()) {
            return unresolved(reference, ownerPath, element, "names a catalog root this tree does not contain");
        }
        PathIndex.Found found = index.lookup(candidate);
        if (found.outcome.equals(PathResolution.UNRESOLVED)) {
            return unresolved(reference, ownerPath, element, "no object or folder at this path");
        }
        if (found.outcome.equals(PathResolution.AMBIGUOUS)) {
            estate.recordError(rpdId, Coverage.CATALOG, ExtractionError.AMBIGUOUS_REFERENCE, ownerPath, element,
                    PythonText.repr(reference) + " matches more than one object once case is folded — "
                            + found.target + ". Choosing between them would be a guess, so it is recorded as "
                            + "unresolved.");
            return new PathResolution(PathResolution.AMBIGUOUS, "", "",
                    "two paths differ only in case: " + found.target);
        }
        String targetId;
        try {
            targetId = Identifiers.makeId(rpdId, Identifiers.CATALOG, found.target);
        } catch (IdentifierException e) {
            // Deliberate difference 3: the reference implementation stops the whole run here.
            estate.recordError(rpdId, Coverage.CATALOG, ExtractionError.INVALID_NAME, ownerPath, element,
                    PythonText.repr(reference) + " points at a path that cannot be part of a Row identifier: "
                            + e.getMessage());
            return new PathResolution(PathResolution.UNRESOLVED, "", "",
                    "the path it points at cannot be part of a Row identifier");
        }
        String detail = found.kind.equals(expected) ? ""
                : "expected a catalog " + expected + ", resolved to a " + found.kind;
        return new PathResolution(found.outcome, targetId, found.kind, detail);
    }

    private PathResolution unresolved(String reference, String ownerPath, String element, String detail) {
        estate.recordError(rpdId, Coverage.CATALOG, ExtractionError.UNRESOLVED_REFERENCE, ownerPath, element,
                PythonText.repr(reference) + " did not resolve: " + detail
                        + ". Recorded rather than repaired (AD-3).");
        return new PathResolution(PathResolution.UNRESOLVED, "", "", detail);
    }
}
