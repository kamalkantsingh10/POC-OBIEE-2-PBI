package obi2pbi.out;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import obi2pbi.core.FsdBlock;
import obi2pbi.core.FsdCell;
import obi2pbi.core.FsdDocument;
import obi2pbi.core.FsdRun;

/**
 * Writes an FSD as {@code fsd.md}: markdown with an anchor on every paragraph and every table cell
 * (Architecture AD-10), and, when it is long, the same text in sections a short-context skill can read one at
 * a time (AD-17).
 *
 * <p>Layout: a heading is {@code ## [p3] 1. Purpose}; a paragraph is {@code [p4] text}; a list item is
 * {@code - [p9] text} or {@code 1. [p9] text}, indented two spaces per level; a table is a markdown table whose
 * first row is the header row and whose every cell starts with its anchor ({@code | [t1.r2.c1] F1 |}). The
 * anchor comes first, so no text of the FSD can be mistaken for markdown structure. Text is written as the
 * document holds it; bold becomes {@code **...**} outside headings and tables; a line break inside a paragraph
 * or cell is {@code <br>}; a {@code |} inside a cell is {@code \|}. A cell that a merged cell covers is
 * {@code [t1.r3.c1] (merged with t1.r2.c1)}. One empty line stands between blocks, none between list items.
 * UTF-8, line feeds, a line feed at the end. The same document always gives the same bytes.
 */
public final class FsdMarkdownWriter {

    /** {@code fsd.md} longer than this is also written in sections. */
    public static final int SECTION_THRESHOLD = 300;
    /** The most lines a section has, its heading line included. */
    public static final int SECTION_LINES = 150;
    public static final String MARKDOWN = "fsd.md";
    public static final String MANIFEST = "fsd.manifest.csv";
    public static final String SECTIONS = "fsd.sections";
    public static final String INDEX = "index.md";

    /** One section of a long {@code fsd.md}. */
    public static final class Section {
        public final String fileName;
        public final String text;
        public final String firstAnchor;
        public final String lastAnchor;
        public final int lines;
        public final String heading;

        Section(String fileName, String text, String firstAnchor, String lastAnchor, int lines, String heading) {
            this.fileName = fileName;
            this.text = text;
            this.firstAnchor = firstAnchor;
            this.lastAnchor = lastAnchor;
            this.lines = lines;
            this.heading = heading;
        }
    }

    /** What was written. */
    public static final class Written {
        public final int lines;
        public final List<Section> sections;

        Written(int lines, List<Section> sections) {
            this.lines = lines;
            this.sections = sections;
        }
    }

    /** A block as lines: for a table, the header row and separator apart from the other rows, so a long table can be cut. */
    private static final class Rendered {
        final FsdBlock block;
        final List<String> head = new ArrayList<>();
        final List<String> body = new ArrayList<>();
        final List<String> bodyFirstAnchor = new ArrayList<>();
        final List<String> bodyLastAnchor = new ArrayList<>();

        Rendered(FsdBlock block) {
            this.block = block;
        }

        int size() {
            return head.size() + body.size();
        }
    }

    private FsdMarkdownWriter() {
    }

    /** The text of {@code fsd.md}. */
    public static String render(FsdDocument document) {
        StringBuilder text = new StringBuilder();
        FsdBlock previous = null;
        for (Rendered block : rendered(document)) {
            if (previous != null && !(previous.kind == FsdBlock.Kind.LIST_ITEM && block.block.kind == FsdBlock.Kind.LIST_ITEM)) {
                text.append('\n');
            }
            for (String line : block.head) {
                text.append(line).append('\n');
            }
            for (String line : block.body) {
                text.append(line).append('\n');
            }
            previous = block.block;
        }
        return text.toString();
    }

    /**
     * The sections of the document: a new section starts at each heading of level 1 or 2, and wherever the next
     * block would make the section longer than {@code maxLines}. A table longer than that is cut between rows;
     * each later piece repeats the header row and says which table it continues.
     */
    public static List<Section> sections(FsdDocument document, int maxLines) {
        List<List<String>> texts = new ArrayList<>();
        List<String[]> anchors = new ArrayList<>();
        List<String> headings = new ArrayList<>();
        List<String> current = new ArrayList<>();
        String[] range = {"", ""};
        String under = "";
        String sectionHeading = "";
        FsdBlock previous = null;
        for (Rendered block : rendered(document)) {
            boolean listContinues = previous != null && previous.kind == FsdBlock.Kind.LIST_ITEM
                    && block.block.kind == FsdBlock.Kind.LIST_ITEM;
            int gap = current.isEmpty() || listContinues ? 0 : 1;
            boolean breakHere = !current.isEmpty()
                    && ((block.block.kind == FsdBlock.Kind.HEADING && block.block.level <= 2)
                    || current.size() + gap + block.size() > maxLines);
            if (block.block.kind == FsdBlock.Kind.HEADING) {
                under = headingLine(block.block);
            }
            if (breakHere) {
                texts.add(current);
                anchors.add(range);
                headings.add(sectionHeading);
                current = new ArrayList<>();
                range = new String[] {"", ""};
                gap = 0;
            }
            if (current.isEmpty()) {
                sectionHeading = under;
            }
            if (block.size() + gap <= maxLines - current.size()) {
                if (gap == 1) {
                    current.add("");
                }
                current.addAll(block.head);
                current.addAll(block.body);
                note(range, block.block.anchor.startsWith("t") ? block.block.anchor + ".r1.c1" : block.block.anchor);
                if (!block.bodyLastAnchor.isEmpty()) {
                    note(range, block.bodyLastAnchor.get(block.bodyLastAnchor.size() - 1));
                }
            } else {
                // A table longer than a section: cut between rows, repeating the header row.
                int row = 0;
                boolean first = true;
                while (row < block.body.size()) {
                    if (!current.isEmpty() && current.size() + 1 + block.head.size() + 2 > maxLines) {
                        texts.add(current);
                        anchors.add(range);
                        headings.add(sectionHeading);
                        current = new ArrayList<>();
                        range = new String[] {"", ""};
                        sectionHeading = under;
                    }
                    if (!current.isEmpty()) {
                        current.add("");
                    }
                    if (!first) {
                        current.add("(table " + block.block.anchor + " continued: the header row is repeated; rows from "
                                + rowOf(block.bodyFirstAnchor.get(row)) + ")");
                    }
                    current.addAll(block.head);
                    note(range, first ? block.block.anchor + ".r1.c1" : block.bodyFirstAnchor.get(row));
                    do {
                        current.add(block.body.get(row));
                        note(range, block.bodyLastAnchor.get(row));
                        row++;
                    } while (row < block.body.size() && current.size() < maxLines);
                    first = false;
                }
            }
            previous = block.block;
        }
        if (!current.isEmpty()) {
            texts.add(current);
            anchors.add(range);
            headings.add(sectionHeading);
        }
        List<Section> sections = new ArrayList<>();
        int width = Math.max(2, Integer.toString(texts.size()).length());
        for (int i = 0; i < texts.size(); i++) {
            String heading = headings.get(i);
            String number = String.format("%0" + width + "d", i + 1);
            String slug = slug(heading.replaceFirst("^#+ \\[p[0-9]+\\] ", ""));
            String fileName = number + "-" + (slug.isEmpty() ? "start" : slug) + ".md";
            StringBuilder text = new StringBuilder();
            text.append("Section ").append(i + 1).append(" of ").append(texts.size()).append(" of ").append(MARKDOWN)
                    .append(": anchors ").append(anchors.get(i)[0]).append(" to ").append(anchors.get(i)[1]);
            if (!heading.isEmpty()) {
                text.append(", under the heading ").append(heading.replaceFirst("^#+ ", ""));
            }
            text.append(".\n\n");
            for (String line : texts.get(i)) {
                text.append(line).append('\n');
            }
            sections.add(new Section(fileName, text.toString(), anchors.get(i)[0], anchors.get(i)[1],
                    texts.get(i).size() + 2, heading.replaceFirst("^#+ ", "")));
        }
        return sections;
    }

    /** The text of {@code fsd.sections/index.md}. */
    public static String index(List<Section> sections, int totalLines) {
        StringBuilder text = new StringBuilder();
        text.append("# Sections of ").append(MARKDOWN).append("\n\n");
        text.append(MARKDOWN).append(" has ").append(totalLines).append(" lines, so it is also written here in ")
                .append(sections.size()).append(" sections of at most ").append(SECTION_LINES + 2)
                .append(" lines. Each section is the text of ").append(MARKDOWN)
                .append(" from its first anchor to its last, with the same anchors. Read one section at a time.\n\n");
        text.append("| Section | File | First anchor | Last anchor | Lines | Under the heading |\n");
        text.append("|---|---|---|---|---|---|\n");
        for (int i = 0; i < sections.size(); i++) {
            Section section = sections.get(i);
            text.append("| ").append(i + 1).append(" | ").append(section.fileName).append(" | ")
                    .append(section.firstAnchor).append(" | ").append(section.lastAnchor).append(" | ")
                    .append(section.lines).append(" | ").append(section.heading.replace("|", "\\|")).append(" |\n");
        }
        return text.toString();
    }

    /**
     * Refuses, before anything is written, a sections folder this command did not write (one with no
     * {@code index.md}).
     */
    public static void checkOurs(Path folder) throws NotAKnowledgeBaseException {
        Path sections = folder.resolve(SECTIONS);
        if (Files.exists(sections) && !Files.isRegularFile(sections.resolve(INDEX))) {
            throw new NotAKnowledgeBaseException(sections + " exists and has no " + INDEX + ", so it was not written by "
                    + "fsd-text. Move it away, then run fsd-text again.");
        }
    }

    /**
     * Writes {@code fsd.md}, {@code fsd.manifest.csv} and, when {@code fsd.md} is longer than
     * {@link #SECTION_THRESHOLD} lines, {@code fsd.sections/}; removes a sections folder of an earlier run that
     * is no longer needed.
     *
     * @param manifest key and value rows describing the source, written first in the manifest
     */
    public static Written write(FsdDocument document, Path folder, List<String[]> manifest) throws IOException {
        String markdown = render(document);
        int lines = markdown.isEmpty() ? 0 : markdown.split("\n", -1).length - 1;
        List<Section> sections = lines > SECTION_THRESHOLD ? sections(document, SECTION_LINES) : List.of();
        Files.createDirectories(folder);
        Files.write(folder.resolve(MARKDOWN), markdown.getBytes(StandardCharsets.UTF_8));
        Path sectionFolder = folder.resolve(SECTIONS);
        if (Files.isDirectory(sectionFolder)) {
            try (Stream<Path> files = Files.list(sectionFolder)) {
                for (Path file : files.sorted().collect(Collectors.toList())) {
                    if (Files.isRegularFile(file) && file.getFileName().toString().endsWith(".md")) {
                        Files.delete(file);
                    }
                }
            }
            if (sections.isEmpty()) {
                try (Stream<Path> left = Files.list(sectionFolder)) {
                    if (left.findAny().isEmpty()) {
                        Files.delete(sectionFolder);
                    }
                }
            }
        }
        if (!sections.isEmpty()) {
            Files.createDirectories(sectionFolder);
            for (Section section : sections) {
                Files.write(sectionFolder.resolve(section.fileName), section.text.getBytes(StandardCharsets.UTF_8));
            }
            Files.write(sectionFolder.resolve(INDEX), index(sections, lines).getBytes(StandardCharsets.UTF_8));
        }
        List<String[]> rows = new ArrayList<>(manifest);
        rows.add(new String[] {"paragraphs", Integer.toString(document.paragraphCount())});
        rows.add(new String[] {"tables", Integer.toString(document.tableCount())});
        rows.add(new String[] {"cells", Integer.toString(document.cellCount())});
        rows.add(new String[] {"lines", Integer.toString(lines)});
        rows.add(new String[] {"sections", Integer.toString(sections.size())});
        rows.add(new String[] {"section_max_lines", Integer.toString(SECTION_LINES + 2)});
        rows.add(new String[] {"files", sections.isEmpty() ? MARKDOWN : MARKDOWN + " " + SECTIONS + "/" + INDEX + " "
                + sections.stream().map(s -> SECTIONS + "/" + s.fileName).collect(Collectors.joining(" "))});
        CsvWriter.write(folder.resolve(MANIFEST), List.of("key", "value"), rows);
        return new Written(lines, sections);
    }

    // ------------------------------------------------------------------ rendering

    private static List<Rendered> rendered(FsdDocument document) {
        List<Rendered> all = new ArrayList<>();
        for (FsdBlock block : document.blocks) {
            Rendered r = new Rendered(block);
            switch (block.kind) {
                case HEADING:
                    r.head.add(headingLine(block));
                    break;
                case PARAGRAPH:
                    r.head.add("[" + block.anchor + "] " + inline(block.runs, true));
                    break;
                case LIST_ITEM:
                    r.head.add("  ".repeat(block.level) + block.marker + " [" + block.anchor + "] "
                            + inline(block.runs, true));
                    break;
                default:
                    table(block, r);
                    break;
            }
            all.add(r);
        }
        return all;
    }

    private static String headingLine(FsdBlock block) {
        return "#".repeat(block.level) + " [" + block.anchor + "] " + block.text().replace("\n", " ");
    }

    private static void table(FsdBlock block, Rendered r) {
        int width = block.columnCount();
        for (int i = 0; i < block.rows.size(); i++) {
            List<FsdCell> row = block.rows.get(i);
            StringBuilder line = new StringBuilder("|");
            for (FsdCell cell : row) {
                line.append(" [").append(cell.anchor).append(']');
                String text = cell.isMerged() ? "(merged with " + cell.mergedInto + ")" : cellText(cell.text);
                if (!text.isEmpty()) {
                    line.append(' ').append(text);
                }
                line.append(" |");
            }
            if (i == 0) {
                r.head.add(line.toString());
                r.head.add("|" + "---|".repeat(width));
            } else {
                r.body.add(line.toString());
                r.bodyFirstAnchor.add(row.get(0).anchor);
                r.bodyLastAnchor.add(row.get(row.size() - 1).anchor);
            }
        }
    }

    private static String cellText(String text) {
        return text.replace("|", "\\|").replace("\r\n", "\n").replace("\n", "<br>");
    }

    private static String inline(List<FsdRun> runs, boolean bold) {
        StringBuilder text = new StringBuilder();
        for (FsdRun run : runs) {
            String value = run.text.replace("\r\n", "\n").replace("\n", "<br>");
            if (!bold || !run.bold || value.trim().isEmpty()) {
                text.append(value);
                continue;
            }
            int start = 0;
            int end = value.length();
            while (start < end && Character.isWhitespace(value.charAt(start))) {
                start++;
            }
            while (end > start && Character.isWhitespace(value.charAt(end - 1))) {
                end--;
            }
            text.append(value, 0, start).append("**").append(value, start, end).append("**").append(value.substring(end));
        }
        return text.toString();
    }

    private static void note(String[] range, String anchor) {
        if (range[0].isEmpty()) {
            range[0] = anchor;
        }
        range[1] = anchor;
    }

    private static String rowOf(String cellAnchor) {
        int dot = cellAnchor.indexOf('.');
        int second = cellAnchor.indexOf('.', dot + 1);
        return second < 0 ? cellAnchor : cellAnchor.substring(dot + 1, second);
    }

    /** Lower case, every run of other characters one hyphen, at most 40 characters. */
    static String slug(String text) {
        String slug = text.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]+", "-").replaceAll("^-+|-+$", "");
        if (slug.length() > 40) {
            slug = slug.substring(0, 40).replaceAll("-+$", "");
        }
        return slug;
    }
}
