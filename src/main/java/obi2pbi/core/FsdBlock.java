package obi2pbi.core;

import java.util.ArrayList;
import java.util.List;

/**
 * One block of an FSD in document order: a heading, a paragraph, a list item or a table. Built by the
 * document reader; the anchor is set by {@link FsdDocument}.
 */
public final class FsdBlock {

    public enum Kind { HEADING, PARAGRAPH, LIST_ITEM, TABLE }

    public final Kind kind;
    /** A heading's level (1 to 6); a list item's depth (0 for the outermost); otherwise 0. */
    public final int level;
    /** A list item's marker as the reader would see it: {@code -} for a bullet, {@code 1.} or {@code a)} for a number. */
    public final String marker;
    /** The text of a heading, paragraph or list item; empty for a table. */
    public final List<FsdRun> runs;
    /** A table's rows, each with one cell per grid column; empty for other blocks. */
    public final List<List<FsdCell>> rows;
    /** {@code p12} for a paragraph-like block, {@code t3} for a table; set by {@link FsdDocument}. */
    public final String anchor;

    private FsdBlock(Kind kind, int level, String marker, List<FsdRun> runs, List<List<FsdCell>> rows, String anchor) {
        this.kind = kind;
        this.level = level;
        this.marker = marker;
        this.runs = List.copyOf(runs);
        List<List<FsdCell>> copied = new ArrayList<>();
        for (List<FsdCell> row : rows) {
            copied.add(List.copyOf(row));
        }
        this.rows = List.copyOf(copied);
        this.anchor = anchor;
    }

    public static FsdBlock heading(int level, List<FsdRun> runs) {
        return new FsdBlock(Kind.HEADING, level, "", runs, List.of(), "");
    }

    public static FsdBlock paragraph(List<FsdRun> runs) {
        return new FsdBlock(Kind.PARAGRAPH, 0, "", runs, List.of(), "");
    }

    public static FsdBlock listItem(int depth, String marker, List<FsdRun> runs) {
        return new FsdBlock(Kind.LIST_ITEM, depth, marker, runs, List.of(), "");
    }

    public static FsdBlock table(List<List<FsdCell>> rows) {
        return new FsdBlock(Kind.TABLE, 0, "", List.of(), rows, "");
    }

    /** The block's text with no formatting. */
    public String text() {
        StringBuilder text = new StringBuilder();
        for (FsdRun run : runs) {
            text.append(run.text);
        }
        return text.toString();
    }

    /** The number of grid columns of a table: the length of its longest row. */
    public int columnCount() {
        int columns = 0;
        for (List<FsdCell> row : rows) {
            columns = Math.max(columns, row.size());
        }
        return columns;
    }

    FsdBlock anchored(String blockAnchor, List<List<FsdCell>> anchoredRows) {
        return new FsdBlock(kind, level, marker, runs, anchoredRows, blockAnchor);
    }
}
