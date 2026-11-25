package obi2pbi.core;

/**
 * One position of an FSD table: a row and a grid column. Every position is a cell, so that rows stay
 * aligned: a position covered by a merged cell is a cell of its own with no text, which names the cell it
 * is merged into.
 */
public final class FsdCell {

    /** The cell's text exactly as written; the cell's paragraphs are separated by a line feed. */
    public final String text;
    /**
     * For a position covered by a merged cell: the anchor of the cell that holds the text, without brackets
     * ({@code t1.r2.c1}). Empty for a cell of its own. Set by {@link FsdDocument}.
     */
    public final String mergedInto;
    /** Set by the reader before anchors exist: the row and column (from 1) of the cell holding the text, or 0. */
    public final int mergedRow;
    public final int mergedColumn;
    /** The cell's anchor without brackets, for example {@code t3.r2.c1}. Set by {@link FsdDocument}. */
    public final String anchor;

    private FsdCell(String text, int mergedRow, int mergedColumn, String mergedInto, String anchor) {
        this.text = text;
        this.mergedRow = mergedRow;
        this.mergedColumn = mergedColumn;
        this.mergedInto = mergedInto;
        this.anchor = anchor;
    }

    public static FsdCell of(String text) {
        return new FsdCell(text, 0, 0, "", "");
    }

    /** A position covered by the cell at the given row and column (both counted from 1). */
    public static FsdCell mergedInto(int row, int column) {
        return new FsdCell("", row, column, "", "");
    }

    public boolean isMerged() {
        return mergedRow > 0;
    }

    FsdCell anchored(String cellAnchor, String mergedAnchor) {
        return new FsdCell(text, mergedRow, mergedColumn, mergedAnchor, cellAnchor);
    }
}
