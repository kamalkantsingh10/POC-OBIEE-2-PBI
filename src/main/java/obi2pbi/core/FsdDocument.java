package obi2pbi.core;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * An FSD as a list of blocks in document order, each with its anchor (Architecture AD-10).
 *
 * <p>Anchors are numbered in document order and depend only on the order of the blocks: headings,
 * paragraphs and list items are {@code p1, p2, ...}; tables are {@code t1, t2, ...}; the cell at row r and
 * grid column c of table n is {@code tn.rr.cc}, rows and columns counted from 1. A paragraph inside a table
 * is part of its cell and has no {@code p} anchor. Every grid position of a table is a cell with an anchor,
 * also when it is empty or covered by a merged cell. The same blocks always give the same anchors.
 */
public final class FsdDocument {

    /** An anchor as written in {@code fsd.md}, brackets included. Group 1 is the anchor without brackets. */
    public static final Pattern ANCHOR = Pattern.compile("\\[(p[1-9][0-9]*|t[1-9][0-9]*\\.r[1-9][0-9]*\\.c[1-9][0-9]*)\\]");
    /** An anchor without brackets, as {@code fsd_items.csv} and {@code matches.csv} write it. */
    public static final Pattern BARE_ANCHOR = Pattern.compile("p[1-9][0-9]*|t[1-9][0-9]*\\.r[1-9][0-9]*\\.c[1-9][0-9]*");

    public final List<FsdBlock> blocks;

    public FsdDocument(List<FsdBlock> unanchored) {
        List<FsdBlock> anchored = new ArrayList<>();
        int paragraph = 0;
        int table = 0;
        for (FsdBlock block : unanchored) {
            if (block.kind != FsdBlock.Kind.TABLE) {
                anchored.add(block.anchored("p" + (++paragraph), List.of()));
                continue;
            }
            String t = "t" + (++table);
            List<List<FsdCell>> rows = new ArrayList<>();
            for (int r = 0; r < block.rows.size(); r++) {
                List<FsdCell> row = new ArrayList<>();
                for (int c = 0; c < block.rows.get(r).size(); c++) {
                    FsdCell cell = block.rows.get(r).get(c);
                    String merged = cell.isMerged() ? t + ".r" + cell.mergedRow + ".c" + cell.mergedColumn : "";
                    row.add(cell.anchored(t + ".r" + (r + 1) + ".c" + (c + 1), merged));
                }
                rows.add(row);
            }
            anchored.add(block.anchored(t, rows));
        }
        this.blocks = List.copyOf(anchored);
    }

    /** Headings, paragraphs and list items. */
    public int paragraphCount() {
        int count = 0;
        for (FsdBlock block : blocks) {
            count += block.kind == FsdBlock.Kind.TABLE ? 0 : 1;
        }
        return count;
    }

    public int tableCount() {
        return blocks.size() - paragraphCount();
    }

    /** Every grid position of every table. */
    public int cellCount() {
        int count = 0;
        for (FsdBlock block : blocks) {
            for (List<FsdCell> row : block.rows) {
                count += row.size();
            }
        }
        return count;
    }

    /** The anchors (without brackets) written in a text, in order of appearance. */
    public static List<String> anchorsIn(String text) {
        List<String> found = new ArrayList<>();
        Matcher match = ANCHOR.matcher(text);
        while (match.find()) {
            found.add(match.group(1));
        }
        return found;
    }
}
