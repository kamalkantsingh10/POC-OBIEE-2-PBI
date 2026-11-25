package obi2pbi;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import obi2pbi.in.kb.CsvReader;
import obi2pbi.in.kb.CsvTable;
import obi2pbi.out.CsvWriter;

/** The CSV writer's format rules and refusals, and the reader that reads its files back (guide 03 §8.2). */
public final class CsvTest {

    private static final List<String> HEADER = List.of("a", "b");

    public void testEveryFieldIsQuotedWithLineFeedsAndNoByteOrderMark() throws Exception {
        Path file = TestSupport.work("csv-format").resolve("t.csv");
        CsvWriter.write(file, HEADER, rows(new String[] {"x", ""}, new String[] {"1", "say \"hi\", ok"}));
        String text = new String(Files.readAllBytes(file), StandardCharsets.UTF_8);
        Check.equal("\"a\",\"b\"\n\"x\",\"\"\n\"1\",\"say \"\"hi\"\", ok\"\n", text, "file content");
    }

    public void testHeaderIsWrittenForATableWithNoRows() throws Exception {
        Path file = TestSupport.work("csv-empty").resolve("t.csv");
        CsvWriter.write(file, HEADER, new ArrayList<>());
        Check.equal("\"a\",\"b\"\n", new String(Files.readAllBytes(file), StandardCharsets.UTF_8), "header only");
    }

    public void testValuesComeBackExactlyAsWritten() throws Exception {
        String[] values = {
            "ordinary text", "", "a,b", "say \"x\"", "line\nbreak", "cr\r\nlf", "trailing,",
            "-1 * \"%1\", \"x\"\nnext", "=SUM(A1)", "+1", "@x", "00123", "1E5", "1e5", "-1.5e-3",
            "1234567890123456", "'apostrophe", " leading space", "trailing space ", "\ttab",
            "café 日本 😀",
            "\"xe\".\"\".\"SAMPLESALES\".\"SAMP_REVENUE_F\".\"BILL_DAY_DT\"", "\"%1\""};
        List<String[]> rows = new ArrayList<>();
        for (String value : values) {
            rows.add(new String[] {value, "next"});
        }
        Path file = TestSupport.work("csv-round-trip").resolve("t.csv");
        CsvWriter.write(file, HEADER, rows);
        CsvTable back = CsvReader.read(file);
        Check.equal(HEADER, back.header, "header");
        Check.equal(values.length, back.rows.size(), "row count");
        for (int i = 0; i < values.length; i++) {
            Check.equal(values[i], back.rows.get(i)[0], "value " + i);
            Check.equal("next", back.rows.get(i)[1], "neighbour of value " + i);
        }
    }

    public void testRowsAndColumnsStayInTheOrderGiven() throws Exception {
        Path file = TestSupport.work("csv-order").resolve("t.csv");
        CsvWriter.write(file, List.of("z", "a"), rows(new String[] {"2", "y"}, new String[] {"1", "x"}));
        CsvTable back = CsvReader.read(file);
        Check.equal(List.of("z", "a"), back.header, "column order");
        Check.equal("2", back.rows.get(0)[0], "row order");
    }

    public void testSortedWriteUsesCodePointOrderNotJavasDefault() throws Exception {
        String bmp = "～";            // U+FF5E, one UTF-16 unit
        String astral = "😀";   // U+1F600, two UTF-16 units starting 0xD83D
        Check.that(bmp.compareTo(astral) > 0, "premise: Java's default order puts U+1F600 before U+FF5E");
        Path file = TestSupport.work("csv-sorted").resolve("t.csv");
        CsvWriter.writeSorted(file, HEADER, rows(
                new String[] {astral, "1"}, new String[] {bmp, "1"}, new String[] {"B", "2"},
                new String[] {"B", "10"}, new String[] {"a", "1"}, new String[] {"B", "10"}));
        CsvTable back = CsvReader.read(file);
        Check.equal(Arrays.asList("B", "B", "B", "a", bmp, astral), TestSupport.column(back, "a"),
                "first column in code-point order, identical rows kept");
        Check.equal(Arrays.asList("10", "10", "2", "1", "1", "1"), TestSupport.column(back, "b"),
                "ties broken by the next cell, compared as text");
    }

    public void testWritingTwiceIsByteIdentical() throws Exception {
        Path work = TestSupport.work("csv-twice");
        List<String[]> rows = rows(new String[] {"b", "2"}, new String[] {"a", "1"});
        CsvWriter.writeSorted(work.resolve("1.csv"), HEADER, rows);
        CsvWriter.writeSorted(work.resolve("2.csv"), HEADER, rows);
        Check.sameBytes(Files.readAllBytes(work.resolve("1.csv")), Files.readAllBytes(work.resolve("2.csv")), "two writes");
    }

    public void testWriterRefusesInsteadOfRepairing() {
        Path file = TestSupport.work("csv-refuse").resolve("t.csv");
        Check.raises(IllegalArgumentException.class,
                () -> CsvWriter.write(file, List.of("a", "a"), new ArrayList<>()), "duplicate column name");
        IllegalArgumentException width = Check.raises(IllegalArgumentException.class,
                () -> CsvWriter.write(file, HEADER, rows(new String[] {"1", "2"}, new String[] {"only one"})),
                "row of the wrong width");
        Check.contains(width.getMessage(), "row 1", "the message names the row");
        Check.raises(IllegalArgumentException.class,
                () -> CsvWriter.write(file, HEADER, rows(new String[] {"1", null})), "null value");
        Check.that(!Files.exists(file), "nothing is written when the writer refuses");
    }

    public void testReaderRefusesAnEmptyFileAndAShortRow() {
        Path work = TestSupport.work("csv-reader-refuse");
        Path empty = TestSupport.write(work.resolve("empty.csv"), "");
        Check.raises(java.io.IOException.class, () -> CsvReader.read(empty), "empty file");
        Path shortRow = TestSupport.write(work.resolve("short.csv"), "\"a\",\"b\"\n\"1\"\n");
        Check.raises(java.io.IOException.class, () -> CsvReader.read(shortRow), "short row");
    }

    private static List<String[]> rows(String[]... rows) {
        return new ArrayList<>(Arrays.asList(rows));
    }
}
