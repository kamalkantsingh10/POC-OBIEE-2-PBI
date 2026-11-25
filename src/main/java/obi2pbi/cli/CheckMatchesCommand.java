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
import obi2pbi.core.KbData;
import obi2pbi.core.KbRows;
import obi2pbi.core.KbTable;
import obi2pbi.core.MatchCheck;
import obi2pbi.core.MatchSchema;
import obi2pbi.in.kb.CsvReader;
import obi2pbi.in.kb.CsvTable;
import obi2pbi.in.kb.KbFolderReader;
import obi2pbi.in.kb.KbFormatException;
import obi2pbi.out.CsvWriter;

/**
 * {@code check-matches <work/tab> --kb <kb-folder>}: the mechanical checks of the trace skill's matches and the
 * Operator's decisions (story 3.3; Architecture AD-2, AD-11, AD-14). Writes {@code check.csv} in the work folder
 * and nothing else.
 */
public final class CheckMatchesCommand implements Command {

    private static final int SHOWN = 40;

    @Override
    public String name() {
        return "check-matches";
    }

    @Override
    public String summary() {
        return "check the trace skill's matches (and the decisions) against fsd.md and the Knowledge Base";
    }

    @Override
    public String usage() {
        return "usage: java -jar obi2pbi.jar check-matches <work-folder> --kb <kb-folder>\n"
                + "\n"
                + "  <work-folder>    the tab's folder, for example work/monthly-overview, holding fsd.md (from fsd-text),\n"
                + "                   fsd_items.csv and matches.csv (from the trace skill) and, when the Operator has\n"
                + "                   written it, decisions.csv\n"
                + "  --kb <folder>    the Knowledge Base the matches name rows of, for example kb/my-estate\n"
                + "\n"
                + "Writes <work-folder>/check.csv: one row per finding, only a header when nothing is wrong. Failures:\n"
                + "unknown_row (an identifier not in the Knowledge Base), no_physical_source (lineage reaches no database\n"
                + "table), item_without_match, unknown_anchor (not in fsd.md), unknown_item, bad_value (a value the\n"
                + "contract does not allow). Warnings: derived_column (computed by a formula, no table of its own),\n"
                + "differs_from_existing_report (not among the report columns of the page the tab replaces). Never\n"
                + "changes matches.csv or decisions.csv, and does not judge whether a match means the right thing.\n"
                + "The files' columns and values: docs/skill-conventions.md, section 10.\n"
                + "\n"
                + "Exit codes: 0 no failure (warnings do not count); 3 failures; 2 wrong usage or a missing input.\n";
    }

    @Override
    public int run(List<String> arguments, PrintStream out, PrintStream err) throws UsageException {
        String workArgument = null;
        String kbArgument = null;
        for (int i = 0; i < arguments.size(); i++) {
            String argument = arguments.get(i);
            if (argument.equals("--kb")) {
                if (i + 1 >= arguments.size()) {
                    throw new UsageException("--kb needs a value", true);
                }
                kbArgument = arguments.get(++i);
            } else if (argument.startsWith("--")) {
                throw new UsageException("unknown option '" + argument + "'", true);
            } else if (workArgument == null) {
                workArgument = argument;
            } else {
                throw new UsageException("more than one work folder given: '" + workArgument + "' and '" + argument + "'",
                        true);
            }
        }
        if (workArgument == null) {
            throw new UsageException("no work folder given", true);
        }
        if (kbArgument == null) {
            throw new UsageException("no Knowledge Base given: add --kb <folder>", true);
        }

        Path work;
        KbData kb;
        String markdown;
        KbRows items;
        KbRows matches;
        KbRows decisions = null;
        List<String> notes = new ArrayList<>();
        try {
            work = Paths.get(workArgument);
            if (!Files.isDirectory(work)) {
                throw new UsageException("the work folder " + workArgument + " was not found.", false);
            }
            List<String> missing = new ArrayList<>();
            for (String name : List.of("fsd.md", MatchCheck.ITEMS_FILE, MatchCheck.MATCHES_FILE)) {
                if (!Files.isRegularFile(work.resolve(name))) {
                    missing.add(name);
                }
            }
            if (!missing.isEmpty()) {
                throw new UsageException(workArgument + " has no " + String.join(", no ", missing) + ". fsd.md is written "
                        + "by fsd-text; " + MatchCheck.ITEMS_FILE + " and " + MatchCheck.MATCHES_FILE + " by the trace skill.",
                        false);
            }
            if (!Files.isDirectory(Paths.get(kbArgument))) {
                throw new UsageException("the Knowledge Base folder " + kbArgument + " was not found.", false);
            }
            kb = KbFolderReader.read(Paths.get(kbArgument), MatchCheck.TABLES);
            markdown = new String(Files.readAllBytes(work.resolve("fsd.md")), StandardCharsets.UTF_8);
            items = read(work, MatchCheck.ITEMS_FILE, MatchSchema.items(), notes);
            matches = read(work, MatchCheck.MATCHES_FILE, MatchSchema.matches(), notes);
            if (Files.isRegularFile(work.resolve(MatchCheck.DECISIONS_FILE))) {
                decisions = read(work, MatchCheck.DECISIONS_FILE, MatchSchema.decisions(), notes);
            }
        } catch (InvalidPathException e) {
            throw new UsageException("not a usable path: " + e.getMessage(), true);
        } catch (KbFormatException e) {
            throw new UsageException(e.getMessage(), false);
        } catch (IOException e) {
            throw new UsageException("cannot read the inputs: " + e.getMessage(), false);
        }

        MatchCheck.Result result = MatchCheck.check(kb, markdown, items, matches, decisions);
        Path checkFile = work.resolve("check.csv");
        try {
            CsvWriter.write(checkFile, MatchSchema.check().header(), result.findings);
        } catch (IOException e) {
            throw new UsageException("cannot write " + checkFile + ": " + e, false);
        }

        Map<String, Integer> failures = new LinkedHashMap<>();
        Map<String, Integer> warnings = new LinkedHashMap<>();
        for (String kind : MatchSchema.FAILURES) {
            count(result, kind, failures);
        }
        for (String kind : MatchSchema.WARNINGS) {
            count(result, kind, warnings);
        }
        out.println("work:       " + workArgument + " (fsd.md, " + MatchCheck.ITEMS_FILE + ", " + MatchCheck.MATCHES_FILE
                + (decisions == null ? "" : ", " + MatchCheck.DECISIONS_FILE) + ")");
        out.println("kb:         " + kbArgument);
        out.println("items:      " + result.items + " that need data" + (result.replaces == 0 ? ""
                : ", and " + result.replaces + " naming what the tab replaces"));
        out.println("matches:    " + matches.rows.size() + " rows (" + result.proposed + " proposed, " + result.unmatched
                + " unmatched)");
        out.println("decisions:  " + (decisions == null ? "none yet (" + MatchCheck.DECISIONS_FILE + " is written by the "
                + "Operator)" : result.decisions + " rows"));
        out.println("failures:   " + result.failures() + (failures.isEmpty() ? "" : " " + failures) + "  -> " + checkFile);
        out.println("warnings:   " + result.warnings() + (warnings.isEmpty() ? "" : " " + warnings)
                + (result.warnings() == 0 ? "" : "  (in the same file; they do not fail the check)"));
        int shown = 0;
        for (String[] finding : result.findings) {
            if (shown++ == SHOWN) {
                out.println("  ... and " + (result.findings.size() - SHOWN) + " more, in " + checkFile);
                break;
            }
            out.println(String.format("  %-28s %-14s row %-4s %s%s", finding[0], finding[2], finding[3],
                    finding[4].isEmpty() ? "" : "item " + finding[4] + ": ", finding[6]));
        }
        notes.addAll(result.notes);
        for (String note : notes) {
            out.println("note:       " + note);
        }
        return result.passed() ? OK : PROBLEMS_FOUND;
    }

    /** Reads one of the skill's or the Operator's files; a missing column is wrong usage, an extra one a note. */
    private static KbRows read(Path work, String fileName, KbTable contract, List<String> notes)
            throws IOException, UsageException {
        CsvTable table = CsvReader.read(work.resolve(fileName));
        List<String> missing = new ArrayList<>();
        for (String column : contract.header()) {
            if (table.indexOf(column) < 0) {
                missing.add(column);
            }
        }
        if (!missing.isEmpty()) {
            throw new UsageException(fileName + " has no column " + String.join(", ", missing) + ". Its columns are "
                    + String.join(", ", contract.header()) + " (docs/skill-conventions.md, section 10).", false);
        }
        List<String> extra = new ArrayList<>();
        for (String column : table.header) {
            if (!contract.header().contains(column)) {
                extra.add(column);
            }
        }
        if (!extra.isEmpty()) {
            notes.add(fileName + " has columns the contract does not have, which were not read: " + String.join(", ", extra)
                    + ".");
        }
        return new KbRows(contract.name, table.header, table.rows);
    }

    private static void count(MatchCheck.Result result, String kind, Map<String, Integer> counts) {
        for (String[] finding : result.findings) {
            if (finding[0].equals(kind)) {
                counts.merge(kind, 1, Integer::sum);
            }
        }
    }
}
