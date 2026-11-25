package obi2pbi.out;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import obi2pbi.core.KbData;
import obi2pbi.core.KbSchema;
import obi2pbi.core.KbTable;
import obi2pbi.core.PacketUnit;
import obi2pbi.core.SliceSchema;
import obi2pbi.core.Text;

/**
 * Writes a Slice folder (Architecture AD-9): the Knowledge Base's tables with fewer rows, the work units and
 * their packets, the two schema tables limited to what the folder holds, and its own {@code manifest.csv}.
 *
 * <p>The folder is rewritten whole on each run, under the same rule as a Knowledge Base folder: one that is
 * not empty and has no {@code manifest.csv} is refused, untouched.
 */
public final class SliceWriter {

    private SliceWriter() {
    }

    /**
     * @param runFacts manifest rows about the run, written first: {@code {key, value}} in order
     * @return the names of the tables written, the manifest excluded
     */
    public static List<String> write(KbData slice, List<PacketUnit> units, Path folder, List<String[]> runFacts)
            throws NotAKnowledgeBaseException, IOException {
        KnowledgeBaseWriter.clean(folder);
        PacketWriter.clean(folder);
        Files.createDirectories(folder);

        List<KbTable> held = new ArrayList<>();
        for (KbTable table : SliceSchema.sliceTables()) {
            boolean own = table.name.equals(SliceSchema.UNITS) || table.name.equals(SliceSchema.REQUIRED_ROWS)
                    || table.name.equals(KbSchema.SCHEMA_TABLES) || table.name.equals(KbSchema.SCHEMA_COLUMNS)
                    || table.name.equals(KbSchema.MANIFEST);
            if (own || slice.has(table.name)) {
                held.add(table);
            }
        }
        List<String> written = new ArrayList<>();
        for (KbTable table : held) {
            if (slice.has(table.name) && !table.name.equals(KbSchema.MANIFEST)) {
                // Rows are copies of Knowledge Base rows in Knowledge Base order, so they are written as given.
                CsvWriter.write(folder.resolve(table.name + ".csv"), slice.table(table.name).header,
                        slice.table(table.name).rows);
                written.add(table.name);
            }
        }
        PacketWriter.write(folder, units);
        written.add(SliceSchema.UNITS);
        written.add(SliceSchema.REQUIRED_ROWS);
        CsvWriter.write(folder.resolve(KbSchema.SCHEMA_TABLES + ".csv"), KbSchema.table(KbSchema.SCHEMA_TABLES).header(),
                KbSchema.schemaTableRows(held));
        CsvWriter.write(folder.resolve(KbSchema.SCHEMA_COLUMNS + ".csv"), KbSchema.table(KbSchema.SCHEMA_COLUMNS).header(),
                KbSchema.schemaColumnRows(held));
        written.add(KbSchema.SCHEMA_TABLES);
        written.add(KbSchema.SCHEMA_COLUMNS);

        List<String[]> manifest = new ArrayList<>(runFacts);
        for (Map.Entry<String, String> fact : slice.manifest.entrySet()) {
            manifest.add(new String[] {fact.getKey(), fact.getValue()});
        }
        manifest.add(new String[] {"units", Integer.toString(units.size())});
        List<String> sorted = new ArrayList<>(written);
        sorted.sort(Text.CODE_POINT_ORDER);
        manifest.add(new String[] {"tables", String.join(" ", sorted)});
        CsvWriter.write(folder.resolve(KbSchema.MANIFEST + ".csv"), SliceSchema.manifest().header(), manifest);
        return written;
    }
}
