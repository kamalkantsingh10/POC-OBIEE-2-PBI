package obi2pbi.core;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * One work unit of a Slice and its packet (Architecture AD-17): a small text a skill reads to write one
 * section of a document. A unit too long for one packet is split into parts, and each part is a unit.
 */
public final class PacketUnit {

    public final String unitId;
    public final String kind;
    public final String title;
    /** Which part of a split unit this is, from 1; 1 when the unit was not split. */
    public final int part;
    public final int parts;
    /** The packet's text, line by line, with no line ends. */
    public final List<String> lines;
    /** The required rows this packet carries: Row identifier to the Slice table it is in. */
    public final Map<String, String> requiredRows;

    PacketUnit(String unitId, String kind, String title, int part, int parts, List<String> lines,
            Map<String, String> requiredRows) {
        this.unitId = unitId;
        this.kind = kind;
        this.title = title;
        this.part = part;
        this.parts = parts;
        this.lines = List.copyOf(lines);
        this.requiredRows = Collections.unmodifiableMap(new LinkedHashMap<>(requiredRows));
    }

    /** The packet's path inside the Slice folder, with forward slashes. */
    public String packetFile() {
        return "packets/" + unitId + ".md";
    }

    /** The packet as one text: every line followed by a line feed. */
    public String text() {
        StringBuilder out = new StringBuilder();
        for (String line : lines) {
            out.append(line).append('\n');
        }
        return out.toString();
    }
}
