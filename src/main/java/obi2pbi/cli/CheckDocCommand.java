package obi2pbi.cli;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import obi2pbi.core.DocAssembly;
import obi2pbi.core.DocCheck;
import obi2pbi.core.DocCheckResult;
import obi2pbi.core.KbData;
import obi2pbi.core.KbRows;
import obi2pbi.core.SliceSchema;
import obi2pbi.in.kb.CsvReader;
import obi2pbi.in.kb.CsvTable;
import obi2pbi.in.kb.KbFolderReader;
import obi2pbi.in.kb.KbFormatException;
import obi2pbi.out.CsvWriter;

/**
 * {@code check-doc <document> --slice <folder>}: checks that a skill-written document covers every required
 * row of its Slice and that every citation points at a real row. Writes {@code <document>.check.csv} beside
 * the document and never changes the document.
 */
public final class CheckDocCommand implements Command {

    /** The findings that come from comparing a part with the packets; printed line by line under their unit. */
    private static final List<String> PER_UNIT = List.of("unknown_citation", "citation_from_other_unit", "quote_not_in_packet");
    private static final int SHOWN_PER_UNIT = 25;
    /** The parts a document has besides one per unit (docs/skill-conventions.md, section 1). */
    private static final List<String> FIXED_PARTS = List.of("summary", "open-questions", "answered-questions", "not-described");

    @Override
    public String name() {
        return "check-doc";
    }

    @Override
    public String summary() {
        return "check that a skill-written document covers its Slice and cites real rows";
    }

    @Override
    public String usage() {
        return "usage: java -jar obi2pbi.jar check-doc <document> --slice <folder> [--required <csv-file>]\n"
                + "\n"
                + "  <document>             the markdown document a skill wrote, for example as-is/my-estate/sales.md\n"
                + "  --slice <folder>       the Slice the document was written from\n"
                + "  --required <csv-file>  the rows the document must cover, as a CSV file with the columns\n"
                + "                         row_id and table (default: required_rows.csv of the Slice)\n"
                + "\n"
                + "Read beside the document, when present: <document without .md>.answers.md (the answers a\n"
                + "[answer:Q<n>] citation may name) and <document>.review.csv (the review log).\n"
                + "Written beside the document: <document>.check.csv, one row per finding, each with the unit to\n"
                + "correct where that is known; only a header when the document passes. The findings are also\n"
                + "counted per unit in what is printed. The text conventions are in docs/skill-conventions.md.\n"
                + "\n"
                + "Each part of the document that is a unit of the Slice (its file in the parts folder is named\n"
                + "<nnn>-<unit_id>.md) is compared with that unit's packet: a [kb:...] marker must be stated in a\n"
                + "packet (unknown_citation), one stated only in another unit's packet is listed as the warning\n"
                + "citation_from_other_unit, and a text between backticks or fence lines must occur in a packet\n"
                + "character for character (quote_not_in_packet). These are printed per unit. A warning does not\n"
                + "fail the check.\n"
                + "\n"
                + "Exit codes: 0 no finding; 3 findings; 2 wrong usage or a missing input, nothing written.\n";
    }

    @Override
    public int run(List<String> arguments, PrintStream out, PrintStream err) throws UsageException {
        String documentArgument = null;
        String sliceArgument = null;
        String requiredArgument = null;
        for (int i = 0; i < arguments.size(); i++) {
            String argument = arguments.get(i);
            if (argument.equals("--slice") || argument.equals("--required")) {
                if (i + 1 >= arguments.size()) {
                    throw new UsageException(argument + " needs a value", true);
                }
                if (argument.equals("--slice")) {
                    sliceArgument = arguments.get(++i);
                } else {
                    requiredArgument = arguments.get(++i);
                }
            } else if (argument.startsWith("--")) {
                throw new UsageException("unknown option '" + argument + "'", true);
            } else if (documentArgument == null) {
                documentArgument = argument;
            } else {
                throw new UsageException("more than one document given: '" + documentArgument + "' and '" + argument
                        + "'", true);
            }
        }
        if (documentArgument == null) {
            throw new UsageException("no document given", true);
        }
        if (sliceArgument == null) {
            throw new UsageException("no Slice given: add --slice <folder>", true);
        }

        Path document;
        KbData slice;
        String text;
        String answers = null;
        boolean reviewLog;
        List<String[]> required = new ArrayList<>();
        Path checkFile;
        List<String> unitOfLine = null;
        boolean partsDiffer = false;
        Map<String, String> packets = null;
        String packetsMissing = null;
        try {
            document = Paths.get(documentArgument);
            if (!Files.isRegularFile(document)) {
                throw new UsageException("the document " + documentArgument + " was not found.", false);
            }
            if (!Files.isDirectory(Paths.get(sliceArgument))) {
                throw new UsageException("the Slice folder " + sliceArgument + " was not found.", false);
            }
            slice = KbFolderReader.read(Paths.get(sliceArgument));
            text = new String(Files.readAllBytes(document), StandardCharsets.UTF_8);
            String fileName = document.getFileName().toString();
            String base = fileName.endsWith(".md") ? fileName.substring(0, fileName.length() - 3) : fileName;
            Path answersFile = document.resolveSibling(base + ".answers.md");
            if (Files.isRegularFile(answersFile)) {
                answers = new String(Files.readAllBytes(answersFile), StandardCharsets.UTF_8);
            }
            reviewLog = Files.isRegularFile(document.resolveSibling(fileName + ".review.csv"));
            checkFile = document.resolveSibling(fileName + ".check.csv");
            // The unit of a line is known when the document is exactly its parts, joined.
            Path partsFolder = document.resolveSibling(base + ".parts");
            if (Files.isDirectory(partsFolder)) {
                List<String> names = AssembleDocCommand.partNames(partsFolder);
                List<String> texts = new ArrayList<>();
                for (String name : names) {
                    texts.add(new String(Files.readAllBytes(partsFolder.resolve(name)), StandardCharsets.UTF_8));
                }
                DocAssembly.Joined joined = DocAssembly.join(names, texts);
                if (joined.text.equals(text)) {
                    unitOfLine = joined.unitOfLine;
                } else {
                    partsDiffer = true;
                }
            }

            // The packets, to compare each part with: a Slice written by the slice command lists them in units.
            if (slice.has(SliceSchema.UNITS)) {
                KbRows units = slice.table(SliceSchema.UNITS);
                packets = new LinkedHashMap<>();
                for (String[] row : units.rows) {
                    Path packet = Paths.get(sliceArgument).resolve(units.get(row, "packet_file"));
                    if (!Files.isRegularFile(packet)) {
                        packetsMissing = packet.toString();
                        break;
                    }
                    packets.put(units.get(row, "unit_id"), new String(Files.readAllBytes(packet), StandardCharsets.UTF_8));
                }
                if (packetsMissing != null) {
                    packets = null;
                }
            }

            if (requiredArgument != null) {
                Path requiredFile = Paths.get(requiredArgument);
                if (!Files.isRegularFile(requiredFile)) {
                    throw new UsageException("the list of required rows " + requiredArgument + " was not found.", false);
                }
                CsvTable table = CsvReader.read(requiredFile);
                if (table.indexOf("row_id") < 0 || table.indexOf("table") < 0) {
                    throw new UsageException(requiredArgument + " needs the columns row_id and table.", false);
                }
                for (String[] row : table.rows) {
                    required.add(new String[] {table.value(row, "row_id"), table.value(row, "table"),
                            table.indexOf("unit_id") < 0 ? "" : table.value(row, "unit_id")});
                }
            } else {
                if (!slice.has(SliceSchema.REQUIRED_ROWS)) {
                    throw new UsageException(sliceArgument + " has no required_rows table, so it is not a Slice "
                            + "written by the slice command. Run slice again, or give --required.", false);
                }
                KbRows table = slice.table(SliceSchema.REQUIRED_ROWS);
                for (String[] row : table.rows) {
                    required.add(new String[] {table.get(row, "row_id"), table.get(row, "table"),
                            table.has("unit_id") ? table.get(row, "unit_id") : ""});
                }
            }
        } catch (InvalidPathException e) {
            throw new UsageException("not a usable path: " + e.getMessage(), true);
        } catch (KbFormatException e) {
            throw new UsageException(e.getMessage(), false);
        } catch (IOException e) {
            throw new UsageException("cannot read the inputs: " + e, false);
        }

        // Every row a citation may name: each row of each Slice table that has a Row identifier.
        Map<String, String> known = new LinkedHashMap<>();
        for (String name : slice.tableNames()) {
            KbRows table = slice.table(name);
            if (name.equals(SliceSchema.REQUIRED_ROWS) || !table.has("row_id")) {
                continue;
            }
            for (String[] row : table.rows) {
                known.putIfAbsent(table.get(row, "row_id"), name);
            }
        }

        DocCheckResult result = DocCheck.check(text, answers, required, known, reviewLog, unitOfLine, packets);
        try {
            CsvWriter.write(checkFile, SliceSchema.check().header(), result.findings);
        } catch (IOException e) {
            throw new UsageException("cannot write " + checkFile + ": " + e, false);
        }

        Map<String, Integer> byKind = new LinkedHashMap<>();
        Map<String, Integer> warningsByKind = new LinkedHashMap<>();
        for (String[] finding : result.findings) {
            (DocCheckResult.WARNINGS.contains(finding[0]) ? warningsByKind : byKind).merge(finding[0], 1, Integer::sum);
        }
        out.println("document:   " + documentArgument + " (status: " + result.status + ")");
        out.println("required:   " + result.rowsRequired + " rows");
        out.println("covered:    " + result.rowsCovered + " rows (" + result.rowsNotDescribed
                + " only listed as not described)");
        out.println("citations:  " + result.citationsChecked + " [kb:...] checked, " + result.answerCitationsChecked
                + " [answer:...] checked");
        out.println("questions:  " + result.openQuestions + " open, " + result.answeredQuestions + " answered");
        out.println("quotes:     " + (result.packetsCompared ? result.quotesChecked + " texts between backticks or "
                + "fence lines compared with the packets" : "not compared with the packets"));
        out.println("findings:   " + result.failing() + (byKind.isEmpty() ? "" : " " + byKind)
                + "  -> " + checkFile);
        out.println("warnings:   " + result.warnings() + (warningsByKind.isEmpty() ? "" : " " + warningsByKind)
                + (result.warnings() == 0 ? "" : "  (listed in the same file; they do not fail the check)"));
        Map<String, Map<String, Integer>> byUnit = new LinkedHashMap<>();
        for (String[] finding : result.findings) {
            byUnit.computeIfAbsent(finding[4].isEmpty() ? "(no unit)" : finding[4], u -> new LinkedHashMap<>())
                    .merge(finding[0], 1, Integer::sum);
        }
        if (!byUnit.isEmpty()) {
            out.println("by unit:");
            for (Map.Entry<String, Map<String, Integer>> unit : byUnit.entrySet()) {
                int count = unit.getValue().values().stream().mapToInt(Integer::intValue).sum();
                out.println(String.format("  %-40s %d %s", unit.getKey(), count, unit.getValue()));
            }
        }
        // What the comparison of each part with its packet found, unit by unit.
        Map<String, List<String[]>> perUnit = new LinkedHashMap<>();
        for (String[] finding : result.findings) {
            if (PER_UNIT.contains(finding[0]) && !finding[4].isEmpty()) {
                perUnit.computeIfAbsent(finding[4], u -> new ArrayList<>()).add(finding);
            }
        }
        if (!perUnit.isEmpty()) {
            out.println("each part against the packets:");
            for (Map.Entry<String, List<String[]>> unit : perUnit.entrySet()) {
                out.println("  " + unit.getKey());
                int shown = 0;
                for (String[] finding : unit.getValue()) {
                    if (shown++ == SHOWN_PER_UNIT) {
                        out.println("    ... and " + (unit.getValue().size() - SHOWN_PER_UNIT) + " more, in " + checkFile);
                        break;
                    }
                    String what = finding[0].equals("quote_not_in_packet")
                            ? finding[5].substring(finding[5].indexOf("character for character: ") + 25)
                            : "[kb:" + finding[1] + "]";
                    out.println(String.format("    line %-5s %-25s %s", finding[3], finding[0],
                            what.length() > 110 ? what.substring(0, 110) + " ..." : what));
                }
            }
        }
        if (unitOfLine != null && packets != null) {
            // A part named after no unit was not compared: say so, in case a unit was split or renamed.
            List<String> strangers = new ArrayList<>();
            for (String unit : unitOfLine) {
                if (!packets.containsKey(unit) && !FIXED_PARTS.contains(unit) && !strangers.contains(unit)) {
                    strangers.add(unit);
                }
            }
            if (!strangers.isEmpty()) {
                out.println("note:       parts that are named after no unit of this Slice and were not compared with a "
                        + "packet: " + String.join(", ", strangers) + ". A part is named <nnn>-<unit_id>.md after a row "
                        + "of units.csv.");
            }
        }
        if (partsDiffer) {
            out.println("note:       the document is not its parts joined, so findings at a line name no unit, and "
                    + "no part was compared with its packet. Run assemble-doc, then check again.");
        } else if (unitOfLine == null && packets != null) {
            out.println("note:       the document has no parts folder, so no part was compared with its packet.");
        }
        if (packetsMissing != null) {
            out.println("note:       the packet " + packetsMissing + " is missing, so no part was compared with its "
                    + "packet. Run slice again.");
        }
        return result.passed() ? OK : PROBLEMS_FOUND;
    }
}
