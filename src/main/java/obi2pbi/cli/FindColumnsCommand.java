package obi2pbi.cli;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import obi2pbi.core.Citation;
import obi2pbi.core.ColumnSearch;
import obi2pbi.core.KbData;
import obi2pbi.in.kb.KbFolderReader;
import obi2pbi.in.kb.KbFormatException;

/**
 * {@code find-columns <kb-folder> "<term>" [--subject-area <name>] [--formula-contains <text>] [--limit <n>]}:
 * a short ranked list of the presentation columns a term may mean, for the trace skill (story 3.2; Architecture
 * AD-17). Plain text matching, no AI; writes nothing.
 */
public final class FindColumnsCommand implements Command {

    static final int DEFAULT_LIMIT = 10;
    private static final int SHOWN_PHYSICAL = 4;
    private static final int SHOWN_REPORTS = 3;

    @Override
    public String name() {
        return "find-columns";
    }

    @Override
    public String summary() {
        return "list the presentation columns a business term may mean, ranked (plain text matching)";
    }

    @Override
    public String usage() {
        return "usage: java -jar obi2pbi.jar find-columns <kb-folder> \"<term>\" [--subject-area <name>]\n"
                + "                                 [--formula-contains <text>] [--limit <n>]\n"
                + "\n"
                + "  <kb-folder>                a Knowledge Base written by extract, for example kb/my-estate\n"
                + "  \"<term>\"                   the words to look for, for example \"net revenue\"; may be left out\n"
                + "                             when --formula-contains is given\n"
                + "  --subject-area <name>      only this subject area (its exact name, or its Row identifier)\n"
                + "  --formula-contains <text>  only columns one of whose formulas (derived formula, aggregation\n"
                + "                             rule, mapping from the database) contains this text; letter case and\n"
                + "                             runs of spaces are ignored\n"
                + "  --limit <n>                show at most n candidates (default " + DEFAULT_LIMIT + ")\n"
                + "\n"
                + "Compares the term's words with each presentation column's name, its logical column's name, the\n"
                + "table names and the logical column's formulas, ignoring letter case, punctuation and plural\n"
                + "endings, and taking a short word as an abbreviation of a longer one (Discnt, discount). Ranks the\n"
                + "columns whose whole name is the term first, then those with all its words, then those with some.\n"
                + "For each candidate: its Row identifier as a document cites it, subject area, presentation table,\n"
                + "column, logical column, measure and default aggregation, the database columns behind it through\n"
                + "lineage, and the report columns that use it. A name that matches is not proof of the same meaning.\n"
                + "\n"
                + "Exit codes: 0 searched (also when nothing was found); 2 wrong usage or no Knowledge Base.\n";
    }

    @Override
    public int run(List<String> arguments, PrintStream out, PrintStream err) throws UsageException {
        String kbArgument = null;
        String term = null;
        String subjectArea = "";
        String formulaContains = "";
        int limit = DEFAULT_LIMIT;
        for (int i = 0; i < arguments.size(); i++) {
            String argument = arguments.get(i);
            if (argument.equals("--subject-area") || argument.equals("--formula-contains") || argument.equals("--limit")) {
                if (i + 1 >= arguments.size()) {
                    throw new UsageException(argument + " needs a value", true);
                }
                String value = arguments.get(++i);
                if (argument.equals("--subject-area")) {
                    subjectArea = value;
                } else if (argument.equals("--formula-contains")) {
                    formulaContains = value;
                } else {
                    try {
                        limit = Integer.parseInt(value);
                    } catch (NumberFormatException e) {
                        limit = 0;
                    }
                    if (limit < 1) {
                        throw new UsageException("--limit needs a whole number of at least 1, not '" + value + "'", true);
                    }
                }
            } else if (argument.startsWith("--")) {
                throw new UsageException("unknown option '" + argument + "'", true);
            } else if (kbArgument == null) {
                kbArgument = argument;
            } else if (term == null) {
                term = argument;
            } else {
                throw new UsageException("more than one term given: '" + term + "' and '" + argument
                        + "'. Put a term of several words in quotes.", true);
            }
        }
        if (kbArgument == null) {
            throw new UsageException("no Knowledge Base folder given", true);
        }
        if ((term == null || term.trim().isEmpty()) && formulaContains.trim().isEmpty()) {
            throw new UsageException("no term given: give the words to look for, or --formula-contains <text>", true);
        }
        if (term == null) {
            term = "";
        }

        KbData kb;
        try {
            Path folder = Paths.get(kbArgument);
            if (!Files.isDirectory(folder)) {
                throw new UsageException("the Knowledge Base folder " + kbArgument + " was not found.", false);
            }
            kb = KbFolderReader.read(folder, ColumnSearch.TABLES);
        } catch (InvalidPathException e) {
            throw new UsageException("not a usable path: " + e.getMessage(), true);
        } catch (KbFormatException e) {
            throw new UsageException(e.getMessage(), false);
        } catch (IOException e) {
            throw new UsageException("cannot read " + kbArgument + ": " + e, false);
        }

        List<String> areas = ColumnSearch.subjectAreas(kb);
        String area = "";
        if (!subjectArea.isEmpty()) {
            for (String name : areas) {
                if (name.equals(subjectArea) || (kb.estate() + "::pres::" + name).equals(subjectArea)) {
                    area = name;
                }
            }
            if (area.isEmpty()) {
                throw new UsageException("no subject area '" + subjectArea + "' in " + kbArgument + ". The subject areas: "
                        + String.join(", ", areas), false);
            }
        }

        ColumnSearch.Result result = new ColumnSearch(kb).search(term, area, formulaContains);
        int shown = Math.min(limit, result.candidates.size());
        out.println("find-columns " + (term.isEmpty() ? "" : "\"" + term + "\" ") + "in " + kbArgument
                + (area.isEmpty() ? ", all subject areas" : ", subject area \"" + area + "\"")
                + (formulaContains.isEmpty() ? "" : ", formula contains \"" + formulaContains + "\""));
        if (!result.words.isEmpty()) {
            out.println("words looked for: " + String.join(", ", result.words));
        }
        out.println("candidates: " + result.candidates.size() + (result.candidates.size() > shown
                ? ", the first " + shown + " shown (--limit " + limit + ")" : ""));
        if (result.candidates.isEmpty()) {
            out.println();
            out.println("No presentation column matches. Try other or fewer words, an abbreviation the repository "
                    + "may use, or --formula-contains; if nothing fits, the item has no column in this Knowledge Base.");
            return OK;
        }
        for (int i = 0; i < shown; i++) {
            out.println();
            describe(i + 1, result.candidates.get(i), result.reportTablesPresent, out);
        }
        if (!result.reportTablesPresent) {
            out.println();
            out.println("note: this Knowledge Base has no report columns (no catalog was extracted), so no existing "
                    + "report can confirm a candidate.");
        }
        return OK;
    }

    /** A score with at most two decimals, no trailing zeros. */
    static String score(double score) {
        String text = String.format(java.util.Locale.ROOT, "%.2f", score);
        return text.replaceAll("0+$", "").replaceAll("\\.$", "");
    }

    private static void describe(int number, ColumnSearch.Candidate c, boolean reportTables, PrintStream out) {
        StringBuilder match = new StringBuilder(c.group);
        if (!c.group.equals(ColumnSearch.FORMULA)) {
            match.append(": ").append(String.join(", ", c.wordsFound));
            if (!c.wordsNotFound.isEmpty()) {
                match.append("; not found: ").append(String.join(", ", c.wordsNotFound));
            }
            match.append("; score ").append(score(c.score));
        }
        out.println(number + ". " + Citation.of(c.rowId) + "  (" + match + ")");
        out.println("   subject area \"" + c.subjectArea + "\", presentation table \"" + c.presentationTable
                + "\", column \"" + c.column + "\"");
        if (c.logicalColumnId.isEmpty()) {
            out.println("   logical column: none recorded");
        } else {
            StringBuilder logical = new StringBuilder("   logical column " + Citation.of(c.logicalColumnId) + ": ");
            logical.append(c.measure ? "measure" : "not a measure");
            if (!c.aggregation.isEmpty()) {
                logical.append(", default aggregation ").append(c.aggregation).append(' ').append(Citation.of(c.aggregationRuleId));
            }
            if (!c.otherAggregationRuleIds.isEmpty()) {
                logical.append(", and ").append(c.otherAggregationRuleIds.size()).append(" other aggregation rule")
                        .append(c.otherAggregationRuleIds.size() == 1 ? "" : "s");
            }
            out.println(logical);
        }
        if (!c.formulaMatch.isEmpty()) {
            out.println("   matched in the " + c.formulaMatch);
        }
        if (!c.formula.isEmpty()) {
            out.println("   formula: " + c.formula.replace("\r\n", " ").replace('\n', ' ').trim());
            if (!c.formulaRefs.isEmpty()) {
                List<String> refs = new ArrayList<>();
                for (String ref : c.formulaRefs) {
                    refs.add(Citation.of(ref));
                }
                out.println("   formula refers to: " + String.join(", ", refs));
            }
        }
        if (c.physicalColumnIds.isEmpty()) {
            out.println("   database columns: none through lineage" + (c.formula.isEmpty() ? ""
                    : " (a derived column: computed by its formula)"));
        } else {
            List<String> shown = new ArrayList<>();
            for (String id : c.physicalColumnIds.subList(0, Math.min(SHOWN_PHYSICAL, c.physicalColumnIds.size()))) {
                shown.add(Citation.of(id));
            }
            out.println("   database columns: " + String.join(", ", shown)
                    + (c.physicalColumnIds.size() > SHOWN_PHYSICAL ? ", and " + (c.physicalColumnIds.size() - SHOWN_PHYSICAL)
                    + " more" : "") + " (" + c.lineagePaths + " lineage path" + (c.lineagePaths == 1 ? "" : "s") + ")");
        }
        if (!reportTables) {
            out.println("   report columns: not known (no catalog in this Knowledge Base)");
        } else if (c.reports.isEmpty()) {
            out.println("   report columns: none uses it");
        } else {
            out.println("   report columns: " + c.reports.size() + " use it, for example:");
            for (ColumnSearch.ReportUse use : c.reports.subList(0, Math.min(SHOWN_REPORTS, c.reports.size()))) {
                out.println("     " + Citation.of(use.analysisColumnId) + (use.heading.isEmpty() ? "" : " heading \""
                        + use.heading + "\"") + " in report \"" + use.analysisPath + "\"");
            }
        }
    }
}
