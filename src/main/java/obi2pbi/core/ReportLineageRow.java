package obi2pbi.core;

/**
 * One report column followed towards the repository: the presentation column its formula names, and through
 * repository lineage the logical and physical column, or the reason there is none.
 *
 * <p>A table of its own, never part of {@code lineage}: that table describes the repository alone and means
 * the same with or without a catalog.
 */
public final class ReportLineageRow implements Row {

    /** The formula is a bare column reference and the names match exactly as written. */
    public static final String EXACT = "exact";
    /** The formula is a bare column reference and the names match only once letter case is ignored. */
    public static final String CASE_NORMALISED = "case_normalised";
    /** The formula is a bare column reference and no presentation column of the estate has those names. */
    public static final String UNRESOLVED = "unresolved";
    /** The formula is empty or is a calculation; the columns inside a calculation are not looked for. */
    public static final String NOT_A_COLUMN_REFERENCE = "not_a_column_reference";
    /** The formula names an uploaded dataset, which was never in the repository. */
    public static final String OUTSIDE_SEMANTIC_MODEL = "outside_semantic_model";

    /** The presentation column was found and repository lineage has a row for it. */
    public static final String SOURCED = "sourced";
    /** The presentation column was found and repository lineage has no row for it: it is calculated. */
    public static final String COMPUTED_NOT_SOURCED = "computed_not_sourced";

    public final String rowId;
    public final String rpdId;
    public final String analysisId;
    public final String criteriaId;
    public final String analysisColumnId;
    /** The subject area as the catalog wrote it, quotes included. */
    public final String subjectArea;
    /** The matched subject area, or empty. Set whether or not the column itself resolves. */
    public final String subjectAreaId;
    /** The report column's formula, exactly as stored. */
    public final String referenceText;
    public final String presentationColumnId;
    public final String resolution;
    public final String logicalColumnId;
    public final String physicalColumnId;
    /** {@link #SOURCED}, {@link #COMPUTED_NOT_SOURCED}, or empty when no presentation column was found. */
    public final String sourceState;

    public ReportLineageRow(String rowId, String rpdId, String analysisId, String criteriaId,
            String analysisColumnId, String subjectArea, String subjectAreaId, String referenceText,
            String presentationColumnId, String resolution, String logicalColumnId, String physicalColumnId,
            String sourceState) {
        this.rowId = rowId;
        this.rpdId = rpdId;
        this.analysisId = analysisId;
        this.criteriaId = criteriaId;
        this.analysisColumnId = analysisColumnId;
        this.subjectArea = subjectArea;
        this.subjectAreaId = subjectAreaId;
        this.referenceText = referenceText;
        this.presentationColumnId = presentationColumnId;
        this.resolution = resolution;
        this.logicalColumnId = logicalColumnId;
        this.physicalColumnId = physicalColumnId;
        this.sourceState = sourceState;
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
