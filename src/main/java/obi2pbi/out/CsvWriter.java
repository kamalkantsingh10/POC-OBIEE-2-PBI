package obi2pbi.out;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import obi2pbi.core.Text;

/**
 * The one CSV writer (Architecture AD-8). Every CSV file the jar writes goes through here.
 *
 * <p>Format: UTF-8 with no byte-order mark; comma delimiter; every field in double quotes, the header and
 * empty values included; a double quote inside a value doubled and nothing else escaped; a line feed after
 * every row, the last included; line breaks inside a value written as they are. Values are written exactly
 * as given: nothing is trimmed, guarded or converted.
 *
 * <p>The writer refuses instead of repairing: a repeated column name, a row of the wrong width and a null
 * value each raise {@link IllegalArgumentException}.
 */
public final class CsvWriter {

    private CsvWriter() {
    }

    /** Writes the rows in the order given. */
    public static void write(Path file, List<String> header, List<String[]> rows) throws IOException {
        byte[] content = render(header, rows).getBytes(StandardCharsets.UTF_8);
        if (file.getParent() != null) {
            Files.createDirectories(file.getParent());
        }
        Files.write(file, content);
    }

    /**
     * Writes the rows sorted cell by cell, left to right, by Unicode code point. This is the order of every
     * Knowledge Base table that is not in a fixed order; identical rows are kept.
     */
    public static void writeSorted(Path file, List<String> header, List<String[]> rows) throws IOException {
        List<String[]> sorted = new ArrayList<>(rows);
        sorted.sort(Text.ROW_ORDER);
        write(file, header, sorted);
    }

    static String render(List<String> header, List<String[]> rows) {
        if (new HashSet<>(header).size() != header.size()) {
            throw new IllegalArgumentException("duplicate column name in header: " + header);
        }
        StringBuilder out = new StringBuilder();
        appendRow(out, header.toArray(new String[0]), -1, header);
        int index = 0;
        for (String[] row : rows) {
            if (row.length != header.size()) {
                throw new IllegalArgumentException(
                        "row " + index + " has " + row.length + " values, expected " + header.size()
                                + " to match header " + header);
            }
            appendRow(out, row, index, header);
            index++;
        }
        return out.toString();
    }

    private static void appendRow(StringBuilder out, String[] row, int index, List<String> header) {
        for (int i = 0; i < row.length; i++) {
            String value = row[i];
            if (value == null) {
                throw new IllegalArgumentException(
                        "row " + index + ", column '" + header.get(i) + "': value is null. Write an empty "
                                + "string for a value the source did not have.");
            }
            if (i > 0) {
                out.append(',');
            }
            out.append('"');
            for (int c = 0; c < value.length(); c++) {
                char ch = value.charAt(c);
                if (ch == '"') {
                    out.append('"');
                }
                out.append(ch);
            }
            out.append('"');
        }
        out.append('\n');
    }
}
