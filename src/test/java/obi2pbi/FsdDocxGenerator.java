package obi2pbi;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Turns a test FSD written as simple markdown into a real Word document, JDK only, so that {@code fsd-text}
 * can be tried on a {@code .docx} built the way Word builds one: {@code #}, {@code ##}, {@code ###} become
 * paragraphs styled "heading 1" to "heading 3", a {@code |} table becomes a Word table (the line of dashes
 * is left out), {@code - } lines become a Word bullet list, {@code **bold**} becomes a bold run, and the
 * lines of a paragraph are joined by one space. The wording is not changed.
 *
 * <pre>
 * ./build.sh test FsdDocxGenerator   # compiles; then:
 * java -cp build/classes/main:build/classes/test obi2pbi.FsdDocxGenerator work/sample-sales-monthly/fsd-source.md work/sample-sales-monthly/fsd.docx
 * </pre>
 */
public final class FsdDocxGenerator {

    private FsdDocxGenerator() {
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 2) {
            System.err.println("usage: FsdDocxGenerator <source.md> <target.docx>");
            System.exit(2);
        }
        String markdown = new String(Files.readAllBytes(Paths.get(args[0])), StandardCharsets.UTF_8);
        Path target = DocxFixture.write(Paths.get(args[1]), DocxFixture.parts(body(markdown)));
        System.out.println("written: " + target + " (" + Files.size(target) + " bytes)");
    }

    /** The children of w:body for the markdown text. */
    public static String body(String markdown) {
        StringBuilder xml = new StringBuilder();
        List<String> paragraph = new ArrayList<>();
        List<List<String>> table = new ArrayList<>();
        for (String raw : markdown.replace("\r\n", "\n").split("\n", -1)) {
            String line = raw.trim();
            boolean tableLine = line.startsWith("|");
            if (!tableLine && !table.isEmpty()) {
                xml.append(DocxFixture.table(table)).append('\n');
                table.clear();
            }
            if (line.isEmpty() || line.startsWith("#") || line.startsWith("- ") || tableLine) {
                if (!paragraph.isEmpty()) {
                    xml.append(DocxFixture.paragraph("", runs(String.join(" ", paragraph)))).append('\n');
                    paragraph.clear();
                }
            }
            if (line.isEmpty()) {
                continue;
            }
            if (line.startsWith("#")) {
                int level = 0;
                while (level < line.length() && line.charAt(level) == '#') {
                    level++;
                }
                xml.append(DocxFixture.paragraph("Heading" + Math.min(3, level), runs(line.substring(level).trim())))
                        .append('\n');
            } else if (line.startsWith("- ")) {
                xml.append(DocxFixture.listItem(1, 0, runs(line.substring(2)))).append('\n');
            } else if (tableLine) {
                if (line.matches("\\|[\\s:|-]+\\|")) {
                    continue;
                }
                List<String> cells = new ArrayList<>();
                for (String cell : line.substring(1, line.endsWith("|") ? line.length() - 1 : line.length()).split("\\|", -1)) {
                    cells.add(cell.trim());
                }
                table.add(cells);
            } else {
                paragraph.add(line);
            }
        }
        if (!table.isEmpty()) {
            xml.append(DocxFixture.table(table)).append('\n');
        }
        if (!paragraph.isEmpty()) {
            xml.append(DocxFixture.paragraph("", runs(String.join(" ", paragraph)))).append('\n');
        }
        return xml.toString();
    }

    /** Runs for a line of text: {@code **...**} bold, the rest plain. */
    static String runs(String text) {
        StringBuilder xml = new StringBuilder();
        boolean bold = false;
        for (String piece : text.split("\\*\\*", -1)) {
            if (!piece.isEmpty()) {
                xml.append(DocxFixture.run(piece, bold));
            }
            bold = !bold;
        }
        return xml.toString();
    }
}
