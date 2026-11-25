package obi2pbi.in.kb;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Reads a CSV file written by the jar's CSV writer (or by the reference implementation's).
 *
 * <p>Handles quoted fields holding commas, doubled quotes and line breaks; a row can span several lines.
 * Refuses an empty file, a file that is not UTF-8, and a row whose width differs from the header's.
 */
public final class CsvReader {

    private CsvReader() {
    }

    public static CsvTable read(Path file) throws IOException {
        String text;
        try {
            text = StandardCharsets.UTF_8.newDecoder()
                    .onMalformedInput(CodingErrorAction.REPORT)
                    .onUnmappableCharacter(CodingErrorAction.REPORT)
                    .decode(ByteBuffer.wrap(Files.readAllBytes(file)))
                    .toString();
        } catch (CharacterCodingException e) {
            throw new IOException(file + " is not valid UTF-8", e);
        }
        List<String[]> records = parse(text, file.toString());
        if (records.isEmpty()) {
            throw new IOException(file + " is empty; expected at least a header row");
        }
        List<String> header = Arrays.asList(records.get(0));
        List<String[]> rows = new ArrayList<>(records.subList(1, records.size()));
        for (int i = 0; i < rows.size(); i++) {
            if (rows.get(i).length != header.size()) {
                throw new IOException(
                        file + ": row " + i + " has " + rows.get(i).length + " fields, expected " + header.size());
            }
        }
        return new CsvTable(header, rows);
    }

    static List<String[]> parse(String text, String name) throws IOException {
        List<String[]> records = new ArrayList<>();
        List<String> fields = new ArrayList<>();
        StringBuilder field = new StringBuilder();
        boolean quoted = false;
        boolean anything = false;
        int i = 0;
        int length = text.length();
        while (i < length) {
            char c = text.charAt(i);
            if (quoted) {
                if (c == '"') {
                    if (i + 1 < length && text.charAt(i + 1) == '"') {
                        field.append('"');
                        i += 2;
                        continue;
                    }
                    quoted = false;
                } else {
                    field.append(c);
                }
                i++;
                continue;
            }
            if (c == '"') {
                quoted = true;
                anything = true;
            } else if (c == ',') {
                fields.add(field.toString());
                field.setLength(0);
                anything = true;
            } else if (c == '\n' || c == '\r') {
                if (c == '\r' && i + 1 < length && text.charAt(i + 1) == '\n') {
                    i++;
                }
                if (anything || field.length() > 0) {
                    fields.add(field.toString());
                    records.add(fields.toArray(new String[0]));
                }
                fields.clear();
                field.setLength(0);
                anything = false;
            } else {
                field.append(c);
                anything = true;
            }
            i++;
        }
        if (quoted) {
            throw new IOException(name + ": a quoted field is never closed");
        }
        if (anything || field.length() > 0) {
            fields.add(field.toString());
            records.add(fields.toArray(new String[0]));
        }
        return records;
    }
}
