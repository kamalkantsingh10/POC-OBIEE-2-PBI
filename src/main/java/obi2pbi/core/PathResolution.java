package obi2pbi.core;

/**
 * How one path reference in the Web Catalog resolved.
 *
 * <p>Matching exactly and matching after folding case are kept apart on purpose: folding case is a
 * judgement, and on an estate that distinguishes case it could join two different objects. A reference that
 * matches several paths once case is folded is not resolved at all.
 */
public final class PathResolution {

    public static final String EXACT = "exact";
    public static final String CASE_NORMALISED = "case_normalised";
    public static final String AMBIGUOUS = "ambiguous";
    public static final String UNRESOLVED = "unresolved";

    /** What a catalog path names. */
    public static final String OBJECT = "object";
    public static final String FOLDER = "folder";

    public final String outcome;
    /** The Row identifier of the target; empty unless the outcome is exact or case_normalised. */
    public final String targetId;
    public final String targetKind;
    /** Why it did not resolve, or that it resolved to the other kind of thing; empty otherwise. */
    public final String detail;

    public PathResolution(String outcome, String targetId, String targetKind, String detail) {
        this.outcome = outcome;
        this.targetId = targetId;
        this.targetKind = targetKind;
        this.detail = detail;
    }

    public boolean resolved() {
        return !targetId.isEmpty();
    }
}
