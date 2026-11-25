package obi2pbi.core;

/**
 * One criteria block of a report: one query definition. A report that combines queries has several, and
 * the operands of a set operation are criteria blocks nested inside the outer one.
 */
public final class AnalysisCriteria implements Row {

    public final String rowId;
    public final String rpdId;
    public final String analysisId;
    /** The block's number in the report, in source order, counting from 0. */
    public final int position;
    /** The subject area as the catalog wrote it, quotes included; empty when the block names none. */
    public final String subjectArea;

    public AnalysisCriteria(String rowId, String rpdId, String analysisId, int position, String subjectArea) {
        this.rowId = rowId;
        this.rpdId = rpdId;
        this.analysisId = analysisId;
        this.position = position;
        this.subjectArea = subjectArea;
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
