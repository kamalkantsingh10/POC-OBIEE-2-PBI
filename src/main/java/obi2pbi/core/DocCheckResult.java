package obi2pbi.core;

import java.util.ArrayList;
import java.util.List;

/** What {@link DocCheck} found in one document: the findings, and the numbers for the printed summary. */
public final class DocCheckResult {

    /** Finding kinds that are warnings: listed for a reviewer to look at, and they do not fail the check. */
    public static final java.util.Set<String> WARNINGS = java.util.Set.of("citation_from_other_unit");

    /**
     * Rows of the check table: {@code {finding, row_id, table, line, unit_id, detail}}, warnings included.
     * Holds no row other than a warning when the document passes.
     */
    public final List<String[]> findings = new ArrayList<>();
    /** Whether each part of the document that is a unit of the Slice was compared with its packet. */
    public boolean packetsCompared;
    /** How many texts between backticks or fence lines were compared with the packets. */
    public int quotesChecked;
    /** 'draft', 'reviewed', or 'missing' when the first line is not a status line. */
    public String status = "missing";
    public int rowsRequired;
    public int rowsCovered;
    /** How many of the covered rows are covered by an entry of the 'Not described' section only. */
    public int rowsNotDescribed;
    public int citationsChecked;
    public int answerCitationsChecked;
    public int openQuestions;
    public int answeredQuestions;

    /** The unit of each line of the document (index 0 is line 1), or null when that is not known. */
    List<String> unitOfLine;

    /** A finding at a line: its unit is the part of the document the line is in, when that is known. */
    void add(String finding, String rowId, String table, int line, String detail) {
        String unit = unitOfLine != null && line > 0 && line <= unitOfLine.size() ? unitOfLine.get(line - 1) : "";
        add(finding, rowId, table, line, unit, detail);
    }

    void add(String finding, String rowId, String table, int line, String unit, String detail) {
        findings.add(new String[] {finding, rowId, table, line > 0 ? Integer.toString(line) : "", unit, detail});
    }

    /** How many findings are not warnings. */
    public int failing() {
        int count = 0;
        for (String[] finding : findings) {
            count += WARNINGS.contains(finding[0]) ? 0 : 1;
        }
        return count;
    }

    public int warnings() {
        return findings.size() - failing();
    }

    public boolean passed() {
        return failing() == 0;
    }
}
