package obi2pbi.core;

/**
 * One security row. Security is spread over several source objects, so three shapes share this class, told
 * apart by {@link #kind}:
 * <ul>
 * <li>{@link #QUERY_PRIVILEGE}: a role holds a query privilege through a privilege package. {@link #role}
 * and {@link #subjectId} are <b>names</b>, not Row identifiers.</li>
 * <li>{@link #OBJECT_PRIVILEGE}: a privilege package grants or denies an object. {@link #subjectId} is the
 * object's Row identifier; {@link #detail} names the permission and the package.</li>
 * <li>{@link #ROW_FILTER}: the content filter of a table source. {@link #subjectId} is the logical table;
 * the role is not known, because the source keeps it only inside the filter text.</li>
 * </ul>
 */
public final class SecurityRule implements Row {

    public static final String QUERY_PRIVILEGE = "query_privilege";
    public static final String OBJECT_PRIVILEGE = "object_privilege";
    public static final String ROW_FILTER = "row_filter";

    public final String rowId;
    public final String rpdId;
    public final String kind;
    public final String role;
    public final String subjectId;
    public final String detail;
    public final Expression expression;
    /**
     * {@link #QUERY_PRIVILEGE} only: the role's display name and its guid, as the role's own object records
     * them (deliberate difference 27). Empty when the source has none, and for the other two kinds.
     */
    public final String roleDisplayName;
    public final String roleGuid;

    public SecurityRule(String rowId, String rpdId, String kind, String role, String subjectId, String detail,
            Expression expression) {
        this(rowId, rpdId, kind, role, subjectId, detail, expression, "", "");
    }

    public SecurityRule(String rowId, String rpdId, String kind, String role, String subjectId, String detail,
            Expression expression, String roleDisplayName, String roleGuid) {
        this.roleDisplayName = roleDisplayName;
        this.roleGuid = roleGuid;
        this.rowId = rowId;
        this.rpdId = rpdId;
        this.kind = kind;
        this.role = role;
        this.subjectId = subjectId;
        this.detail = detail;
        this.expression = expression;
    }

    /**
     * Adds one {@link #ROW_FILTER} row for every table source that has a content filter. Run once every
     * table source has been read.
     *
     * <p>The filter is recorded where the source keeps it. Which role it belongs to is not worked out: the
     * role names are inside the filter text and are not taken out of it.
     */
    public static void collectRowFilters(Estate estate) {
        for (LogicalTableSource source : estate.logicalTableSources) {
            if (source.whereClause.isEmpty()) {
                continue;
            }
            estate.securityRules.add(new SecurityRule(source.rowId + "#filter", source.rpdId, ROW_FILTER, "",
                    source.logicalTableId, source.name, source.whereClause));
        }
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
