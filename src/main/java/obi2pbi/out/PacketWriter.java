package obi2pbi.out;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import obi2pbi.core.PacketUnit;
import obi2pbi.core.Packets;
import obi2pbi.core.SliceSchema;

/**
 * Writes the work units of a Slice (Architecture AD-17): {@code units.csv}, {@code required_rows.csv} and
 * one packet per unit under {@code packets/}. Packets are UTF-8 text with line feeds.
 */
public final class PacketWriter {

    public static final String FOLDER = "packets";

    private PacketWriter() {
    }

    /** Removes the packets of an earlier run. Only {@code packets/*.md} is ever removed. */
    public static void clean(Path sliceFolder) throws IOException {
        Path packets = sliceFolder.resolve(FOLDER);
        if (!Files.isDirectory(packets)) {
            return;
        }
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(packets, "*.md")) {
            for (Path file : stream) {
                if (Files.isRegularFile(file)) {
                    Files.delete(file);
                }
            }
        }
    }

    public static void write(Path sliceFolder, List<PacketUnit> units) throws IOException {
        Files.createDirectories(sliceFolder.resolve(FOLDER));
        for (PacketUnit unit : units) {
            Files.write(sliceFolder.resolve(FOLDER).resolve(unit.unitId + ".md"),
                    unit.text().getBytes(StandardCharsets.UTF_8));
        }
        CsvWriter.write(sliceFolder.resolve(SliceSchema.UNITS + ".csv"), SliceSchema.units().header(),
                Packets.unitRows(units));
        CsvWriter.writeSorted(sliceFolder.resolve(SliceSchema.REQUIRED_ROWS + ".csv"),
                SliceSchema.requiredRows().header(), Packets.requiredRows(units));
    }
}
