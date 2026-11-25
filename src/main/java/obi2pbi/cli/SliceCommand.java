package obi2pbi.cli;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import obi2pbi.core.KbData;
import obi2pbi.core.KbRows;
import obi2pbi.core.KbTable;
import obi2pbi.core.MatchSchema;
import obi2pbi.core.PacketUnit;
import obi2pbi.core.Packets;
import obi2pbi.core.Slice;
import obi2pbi.core.SliceSchema;
import obi2pbi.core.TabDecisions;
import obi2pbi.core.Text;
import obi2pbi.in.kb.CsvReader;
import obi2pbi.in.kb.CsvTable;
import obi2pbi.in.kb.KbFolderReader;
import obi2pbi.in.kb.KbFormatException;
import obi2pbi.in.mds.IdentifierAttributes;
import obi2pbi.out.KnowledgeBaseWriter;
import obi2pbi.out.NotAKnowledgeBaseException;
import obi2pbi.out.SliceWriter;

/**
 * {@code slice <kb-folder> [--subject-area <name>]...}: cuts one Slice per subject area out of a Knowledge
 * Base, with its work units and packets. With no subject area it lists the subject areas and writes nothing.
 *
 * <p>Everything is read and checked before anything is deleted or written.
 */
public final class SliceCommand implements Command {

    @Override
    public String name() {
        return "slice";
    }

    @Override
    public String summary() {
        return "cut a Knowledge Base down to one subject area, with packets for the skills";
    }

    @Override
    public String usage() {
        return "usage: java -jar obi2pbi.jar slice <kb-folder> [--subject-area <name>]... [--all] [--tab <work-folder>]\n"
                + "                                 [--out <folder>] [--timestamp <text>]\n"
                + "                                 [--max-packet-lines <n>]\n"
                + "\n"
                + "  <kb-folder>              a Knowledge Base written by extract, for example kb/my-estate\n"
                + "  --subject-area <name>    the subject area to cut: its name exactly as listed, or its Row\n"
                + "                           identifier. May be given several times: one Slice folder each.\n"
                + "  --all                    cut every subject area of the Knowledge Base\n"
                + "  --tab <work-folder>      cut the Slice of one tab, for example work/monthly-overview, from the\n"
                + "                           columns its decisions.csv confirms; written to <work-folder>/slice\n"
                + "                           (or --out). Reads fsd_items.csv, decisions.csv and, when present,\n"
                + "                           matches.csv. Prints the subject areas touched and whether each has an\n"
                + "                           as-is document under as-is/<estate>/. Exit 2 when nothing is\n"
                + "                           confirmed; 3, nothing written, when a confirmed column is unknown.\n"
                + "  --out <folder>           the folder that gets one folder per subject area\n"
                + "                           (default: slices/<estate-name>)\n"
                + "  --timestamp <text>       the time stamp to put in manifest.csv, for repeatable output\n"
                + "                           (default: the current time)\n"
                + "  --max-packet-lines <n>   the most lines a packet may have (default " + Packets.DEFAULT_MAX_LINES
                + ", at least " + Packets.SMALLEST_MAX_LINES + ").\n"
                + "                           A unit longer than that is split into parts -p1, -p2 ..., between\n"
                + "                           items; each part is a unit of its own with its own packet.\n"
                + "\n"
                + "With neither --subject-area nor --all, lists the subject areas with their column and measure\n"
                + "counts and writes nothing.\n"
                + "\n"
                + "A Slice folder is named after the subject area: lower case, other characters as hyphens. It\n"
                + "holds the Knowledge Base's tables with only the rows the subject area needs, units.csv (the\n"
                + "work units in writing order), required_rows.csv (the rows a document must cover) and one\n"
                + "packet per unit under packets/, each at most --max-packet-lines lines.\n"
                + "\n"
                + "A Slice folder is rewritten on each run. A folder that is not empty and has no manifest.csv\n"
                + "is refused. Exit codes: 0 written (or listed); 3 written, and a Slice's errors.csv has rows;\n"
                + "2 wrong usage or unknown subject area, nothing written.\n";
    }

    @Override
    public int run(List<String> arguments, PrintStream out, PrintStream err) throws UsageException {
        String kbArgument = null;
        String outArgument = null;
        String timestamp = null;
        String tabArgument = null;
        boolean all = false;
        int maxPacketLines = Packets.DEFAULT_MAX_LINES;
        List<String> names = new ArrayList<>();
        for (int i = 0; i < arguments.size(); i++) {
            String argument = arguments.get(i);
            if (argument.equals("--out") || argument.equals("--timestamp") || argument.equals("--subject-area")
                    || argument.equals("--max-packet-lines") || argument.equals("--tab")) {
                if (i + 1 >= arguments.size()) {
                    throw new UsageException(argument + " needs a value", true);
                }
                String value = arguments.get(++i);
                if (argument.equals("--out")) {
                    outArgument = value;
                } else if (argument.equals("--timestamp")) {
                    timestamp = value;
                } else if (argument.equals("--tab")) {
                    tabArgument = value;
                } else if (argument.equals("--max-packet-lines")) {
                    try {
                        maxPacketLines = Integer.parseInt(value);
                    } catch (NumberFormatException e) {
                        maxPacketLines = -1;
                    }
                    if (maxPacketLines < Packets.SMALLEST_MAX_LINES) {
                        throw new UsageException("--max-packet-lines needs a whole number of at least "
                                + Packets.SMALLEST_MAX_LINES + ", not '" + value + "'", true);
                    }
                } else {
                    names.add(value);
                }
            } else if (argument.equals("--all")) {
                all = true;
            } else if (argument.startsWith("--")) {
                throw new UsageException("unknown option '" + argument + "'", true);
            } else if (kbArgument == null) {
                kbArgument = argument;
            } else {
                throw new UsageException("more than one Knowledge Base folder given: '" + kbArgument + "' and '"
                        + argument + "'. Name a subject area with --subject-area.", true);
            }
        }
        if (kbArgument == null) {
            throw new UsageException("no Knowledge Base folder given", true);
        }
        if (all && !names.isEmpty()) {
            throw new UsageException("give --all or --subject-area, not both", true);
        }

        if (tabArgument != null && (all || !names.isEmpty())) {
            throw new UsageException("give --tab, or --subject-area / --all, not both", true);
        }

        KbData kb = readKnowledgeBase(kbArgument);
        if (tabArgument != null) {
            return tab(kb, kbArgument, tabArgument, outArgument, timestamp, maxPacketLines, out);
        }
        List<String[]> areas = Slice.subjectAreas(kb);
        if (!all && names.isEmpty()) {
            list(areas, out);
            return OK;
        }

        // Which subject areas, and where each goes. Checked in full before the first folder is touched.
        Map<String, String> folderOfArea = new LinkedHashMap<>();
        if (all) {
            for (String[] area : areas) {
                names.add(area[0]);
            }
            if (names.isEmpty()) {
                throw new UsageException(kbArgument + " has no subject area to cut.", false);
            }
        }
        for (String name : names) {
            List<String> matches = Slice.match(kb, name);
            if (matches.isEmpty()) {
                throw new UsageException("no subject area named '" + name + "' in " + kbArgument + ". Available: "
                        + available(areas), false);
            }
            if (matches.size() > 1) {
                throw new UsageException("'" + name + "' matches " + matches.size() + " subject areas: "
                        + String.join(", ", matches) + ". Give the Row identifier.", false);
            }
            String areaName = "";
            for (String[] area : areas) {
                if (area[0].equals(matches.get(0))) {
                    areaName = area[2];
                }
            }
            String folder = Slice.folderName(areaName);
            for (Map.Entry<String, String> taken : folderOfArea.entrySet()) {
                if (taken.getValue().equals(folder) && !taken.getKey().equals(matches.get(0))) {
                    throw new UsageException("subject areas " + taken.getKey() + " and " + matches.get(0)
                            + " would both be written to folder '" + folder + "'. Cut them one at a time, each "
                            + "with its own --out.", false);
                }
            }
            folderOfArea.put(matches.get(0), folder);
        }

        Path root;
        try {
            root = outArgument != null ? Paths.get(outArgument) : Paths.get("slices", kb.estate());
            for (String folder : folderOfArea.values()) {
                KnowledgeBaseWriter.checkOurs(root.resolve(folder));
            }
        } catch (InvalidPathException e) {
            throw new UsageException("not a usable path: " + e.getMessage(), true);
        } catch (NotAKnowledgeBaseException e) {
            throw new UsageException(e.getMessage(), false);
        } catch (IOException e) {
            throw new UsageException("cannot use the output folder: " + e, false);
        }

        List<String[]> runFacts = new ArrayList<>();
        runFacts.add(new String[] {"slicer_version", KnowledgeBaseWriter.VERSION});
        runFacts.add(new String[] {"sliced_at", timestamp == null ? KnowledgeBaseWriter.now() : timestamp});
        runFacts.add(new String[] {"knowledge_base", kbArgument});
        int errors = 0;
        for (Map.Entry<String, String> target : folderOfArea.entrySet()) {
            KbData slice = Slice.cut(kb, target.getKey());
            noteIdentifiers(slice);
            // In the manifest, so that the packets can be built again from the folder alone.
            slice.manifest.put(Packets.MAX_LINES_FACT, Integer.toString(maxPacketLines));
            List<PacketUnit> units = Packets.build(slice);
            Path folder = root.resolve(target.getValue());
            try {
                SliceWriter.write(slice, units, folder, runFacts);
            } catch (NotAKnowledgeBaseException e) {
                throw new UsageException(e.getMessage(), false);
            } catch (IOException e) {
                throw new UsageException("cannot write the Slice to " + folder + ": " + e, false);
            }
            int largest = 0;
            int required = 0;
            String largestUnit = "";
            for (PacketUnit unit : units) {
                if (unit.lines.size() > largest) {
                    largest = unit.lines.size();
                    largestUnit = unit.unitId;
                }
                required += unit.requiredRows.size();
            }
            int sliceErrors = slice.table("errors").rows.size();
            errors += sliceErrors;
            out.println("subject area: " + slice.fact("subject_area") + " (" + kb.estate() + ")");
            out.println("rows:         " + slice.rowCount() + " in " + slice.tableNames().size() + " tables");
            out.println("units:        " + units.size() + " packets, the largest " + largest + " lines (" + largestUnit
                    + "; limit " + maxPacketLines + ")");
            out.println("required:     " + required + " rows a document must cover");
            out.println("errors:       " + sliceErrors + "                 -> " + folder.resolve("errors.csv"));
            out.println("written:      " + folder);
            out.println();
        }
        return errors == 0 ? OK : PROBLEMS_FOUND;
    }

    /** Names, in the manifest, the unparsed rows that are only identifiers, so the gaps packet lists them apart. */
    private static void noteIdentifiers(KbData slice) {
        KbRows unparsed = slice.table("unparsed");
        Set<String> details = new TreeSet<>(Text.CODE_POINT_ORDER);
        for (String[] row : unparsed.rows) {
            if (unparsed.get(row, "kind").equals("unread_attribute")
                    && IdentifierAttributes.isIdentifierOnly(unparsed.get(row, "detail"))) {
                details.add(unparsed.get(row, "detail"));
            }
        }
        slice.manifest.put(Packets.IDENTIFIERS_FACT, String.join("\n", details));
    }

    /** {@code --tab}: the Slice of one tab, from its confirmed decisions (story 4.1). */
    private static int tab(KbData kb, String kbArgument, String tabArgument, String outArgument, String timestamp,
            int maxPacketLines, PrintStream out) throws UsageException {
        Path work;
        Path folder;
        KbRows items;
        KbRows matches = null;
        KbRows decisions;
        try {
            work = Paths.get(tabArgument);
            folder = outArgument != null ? Paths.get(outArgument) : work.resolve("slice");
            if (!Files.isDirectory(work)) {
                throw new UsageException("the tab folder " + tabArgument + " was not found.", false);
            }
            if (!Files.isRegularFile(work.resolve("fsd_items.csv"))) {
                throw new UsageException(tabArgument + " has no fsd_items.csv. The trace skill writes it.", false);
            }
            if (!Files.isRegularFile(work.resolve("decisions.csv"))) {
                throw new UsageException(tabArgument + " has no decisions.csv, so no column is confirmed and nothing was "
                        + "written. The Operator records the decisions (README: Record the decisions).", false);
            }
            items = csv(work, "fsd_items.csv", MatchSchema.items());
            decisions = csv(work, "decisions.csv", MatchSchema.decisions());
            if (Files.isRegularFile(work.resolve("matches.csv"))) {
                matches = csv(work, "matches.csv", MatchSchema.matches());
            }
            KnowledgeBaseWriter.checkOurs(folder);
        } catch (InvalidPathException e) {
            throw new UsageException("not a usable path: " + e.getMessage(), true);
        } catch (NotAKnowledgeBaseException e) {
            throw new UsageException(e.getMessage(), false);
        } catch (IOException e) {
            throw new UsageException("cannot read the tab's files: " + e.getMessage(), false);
        }

        Set<String> known = new HashSet<>();
        for (String table : List.of("presentation_columns", "logical_columns", "analysis_columns")) {
            KbRows rows = kb.table(table);
            for (String[] row : rows.rows) {
                known.add(rows.get(row, "row_id"));
            }
        }
        TabDecisions.Result decided = TabDecisions.read(items, matches, decisions, known::contains);
        String tabName = work.toAbsolutePath().normalize().getFileName().toString();
        out.println("tab:          " + tabName + " (" + tabArgument + ")");
        out.println("decisions:    " + decisions.rows.size() + " rows: " + decided.confirmedDecisions + " confirmed, "
                + decided.rejectedDecisions + " rejected");
        out.println("items:        " + decided.fields.size() + " that need data: " + decided.statuses.get("confirmed")
                + " confirmed; not confirmed, contributing nothing: " + decided.statuses.get("rejected") + " rejected, "
                + decided.statuses.get("unmatched") + " unmatched, " + decided.statuses.get("undecided") + " undecided");
        if (!decided.problems.isEmpty()) {
            for (String problem : decided.problems) {
                out.println("problem:      " + problem);
            }
            out.println("nothing written: correct decisions.csv (check-matches lists the same problems), then run again.");
            return PROBLEMS_FOUND;
        }
        if (decided.columns.isEmpty()) {
            throw new UsageException("decisions.csv of " + tabArgument + " confirms no column, so there is nothing to cut "
                    + "and nothing was written.", false);
        }

        KbData slice = Slice.cutForTab(kb, tabName, decided.columns);
        slice.put(new KbRows(SliceSchema.TAB_FIELDS, SliceSchema.tabFields().header(), decided.fields));
        slice.manifest.put("decisions", decisions.rows.size() + " rows (" + decided.confirmedDecisions + " confirmed, "
                + decided.rejectedDecisions + " rejected); items: " + decided.statusCounts());
        slice.manifest.put(Packets.MAX_LINES_FACT, Integer.toString(maxPacketLines));
        noteIdentifiers(slice);
        List<PacketUnit> units = Packets.build(slice);
        List<String[]> runFacts = new ArrayList<>();
        runFacts.add(new String[] {"slicer_version", KnowledgeBaseWriter.VERSION});
        runFacts.add(new String[] {"sliced_at", timestamp == null ? KnowledgeBaseWriter.now() : timestamp});
        runFacts.add(new String[] {"knowledge_base", kbArgument});
        try {
            SliceWriter.write(slice, units, folder, runFacts);
        } catch (NotAKnowledgeBaseException e) {
            throw new UsageException(e.getMessage(), false);
        } catch (IOException e) {
            throw new UsageException("cannot write the Slice to " + folder + ": " + e, false);
        }
        out.println("columns:      " + decided.columns.size() + " confirmed columns start the walk");
        for (String area : slice.fact("subject_areas").split("\n")) {
            if (area.isEmpty()) {
                continue;
            }
            Path asIs = Paths.get("as-is", kb.estate(), Slice.folderName(area) + ".md");
            String status = "";
            if (Files.isRegularFile(asIs)) {
                try {
                    List<String> lines = Files.readAllLines(asIs, StandardCharsets.UTF_8);
                    status = lines.isEmpty() ? "" : " (" + lines.get(0) + ")";
                } catch (IOException e) {
                    status = " (cannot be read)";
                }
            }
            out.println("subject area: " + area + "; as-is document " + asIs.toString().replace('\\', '/')
                    + (Files.isRegularFile(asIs) ? " exists" + status : " does not exist"));
        }
        if (slice.fact("subject_areas").isEmpty()) {
            out.println("subject area: none (only logical or report columns were confirmed)");
        }
        int largest = 0;
        int required = 0;
        String largestUnit = "";
        for (PacketUnit unit : units) {
            if (unit.lines.size() > largest) {
                largest = unit.lines.size();
                largestUnit = unit.unitId;
            }
            required += unit.requiredRows.size();
        }
        int errors = slice.table("errors").rows.size();
        out.println("rows:         " + slice.rowCount() + " in " + slice.tableNames().size() + " tables");
        out.println("units:        " + units.size() + " packets, the largest " + largest + " lines (" + largestUnit
                + "; limit " + maxPacketLines + ")");
        out.println("required:     " + required + " rows a document must cover");
        out.println("errors:       " + errors + "                 -> " + folder.resolve("errors.csv"));
        out.println("written:      " + folder);
        return errors == 0 ? OK : PROBLEMS_FOUND;
    }

    private static KbRows csv(Path work, String name, KbTable contract) throws IOException, UsageException {
        CsvTable table = CsvReader.read(work.resolve(name));
        for (String column : contract.header()) {
            if (table.indexOf(column) < 0) {
                throw new UsageException(name + " has no column " + column + " (docs/skill-conventions.md, section 10).",
                        false);
            }
        }
        return new KbRows(contract.name, table.header, table.rows);
    }

    private static KbData readKnowledgeBase(String argument) throws UsageException {
        KbData kb;
        List<String> wanted = new ArrayList<>();
        for (KbTable table : SliceSchema.carriedTables()) {
            wanted.add(table.name);
        }
        try {
            kb = KbFolderReader.read(Paths.get(argument), wanted);
        } catch (InvalidPathException e) {
            throw new UsageException("not a usable path: " + e.getMessage(), true);
        } catch (KbFormatException e) {
            throw new UsageException(e.getMessage() + " Run extract first: java -jar obi2pbi.jar extract <estate-folder>",
                    false);
        } catch (IOException e) {
            throw new UsageException("cannot read the Knowledge Base at " + argument + ": " + e, false);
        }
        if (kb.estate().isEmpty() || kb.estate().contains(" ")) {
            throw new UsageException(argument + " does not name exactly one estate in its manifest (rpd_ids = '"
                    + kb.estate() + "'). Run extract again.", false);
        }
        for (KbTable table : SliceSchema.carriedTables()) {
            if (!kb.has(table.name)) {
                throw new UsageException("the manifest of " + argument + " does not list table '" + table.name
                        + "'. It was written by another version of this program; run extract again.", false);
            }
            if (!kb.table(table.name).header.equals(table.header())) {
                throw new UsageException("table '" + table.name + "' of " + argument + " does not have the columns "
                        + "this version writes. Run extract again.", false);
            }
        }
        return kb;
    }

    private static void list(List<String[]> areas, PrintStream out) {
        if (areas.isEmpty()) {
            out.println("no subject areas: the Knowledge Base holds no repository half");
            return;
        }
        int width = 0;
        for (String[] area : areas) {
            width = Math.max(width, area[2].length());
        }
        for (String[] area : areas) {
            out.println(String.format("  %-20s %-" + width + "s  columns: %s  measures: %s", area[1], area[2],
                    area[3], area[4]));
        }
        out.println();
        out.println("Cut one with: slice <kb-folder> --subject-area \"<name>\"   (or --all)");
    }

    private static String available(List<String[]> areas) {
        List<String> names = new ArrayList<>();
        for (String[] area : areas) {
            names.add(area[2]);
        }
        return names.isEmpty() ? "(none)" : String.join(", ", names);
    }
}
