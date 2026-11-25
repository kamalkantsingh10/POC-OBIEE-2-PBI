package obi2pbi.core;

/**
 * One filter row. Three populations share the table, told apart by {@code scope}:
 * <ul>
 * <li>{@link #SCOPE_ANALYSIS}: one expression of a filter inside a report. A filter is a tree of
 * expressions, written one row per expression in source order.</li>
 * <li>{@link #SCOPE_SAVED_FILTER}: one expression of a saved filter object, in the same form (deliberate
 * difference 9).</li>
 * <li>{@link #SCOPE_DASHBOARD}: a dashboard prompt placed on a page. It has a reference and no
 * expression.</li>
 * </ul>
 * {@code parentId} and {@code depth} say where an expression sits in its tree (deliberate difference 10).
 */
public final class AnalysisFilter implements Row {

    public static final String SCOPE_ANALYSIS = "analysis";
    public static final String SCOPE_DASHBOARD = "dashboard";
    public static final String SCOPE_SAVED_FILTER = "saved_filter";

    public final String rowId;
    public final String rpdId;
    public final String scope;
    /** The report, the saved filter or the page the row belongs to. */
    public final String ownerId;
    public final int position;
    public final String op;
    public final String exprType;
    /** The expression's own text: nested expressions, which are rows of their own, left out. */
    public final String exprText;
    /** The catalog path the expression or the page names, as written; empty when it names none. */
    public final String reference;
    public final String targetId;
    /** How the reference resolved; empty when no resolution was attempted. */
    public final String resolution;
    public final boolean usesEval;
    /** The row of the expression that directly encloses this one; empty at the top of a filter. */
    public final String parentId;
    /** How many expressions of the same filter enclose this one; 0 at the top. */
    public final int depth;

    public AnalysisFilter(String rowId, String rpdId, String scope, String ownerId, int position, String op,
            String exprType, String exprText, String reference, String targetId, String resolution,
            boolean usesEval, String parentId, int depth) {
        this.rowId = rowId;
        this.rpdId = rpdId;
        this.scope = scope;
        this.ownerId = ownerId;
        this.position = position;
        this.op = op;
        this.exprType = exprType;
        this.exprText = exprText;
        this.reference = reference;
        this.targetId = targetId;
        this.resolution = resolution;
        this.usesEval = usesEval;
        this.parentId = parentId;
        this.depth = depth;
    }

    @Override
    public String rowId() {
        return rowId;
    }

    @Override
    public String rpdId() {
        return rpdId;
    }
}
