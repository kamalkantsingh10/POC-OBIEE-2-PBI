package obi2pbi.cli;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import obi2pbi.core.Citation;
import obi2pbi.core.KbData;
import obi2pbi.core.PageSearch;
import obi2pbi.in.kb.KbFolderReader;
import obi2pbi.in.kb.KbFormatException;

/**
 * {@code find-pages <kb-folder> "<term>" [--limit <n>]}: the dashboards, dashboard pages and reports a name may
 * mean, ranked, so that the trace skill can name the page a tab replaces by its Row identifier. Plain text
 * matching, no AI; writes nothing.
 */
public final class FindPagesCommand implements Command {

    private static final int SHOWN_REPORTS = 8;
    private static final int SHOWN_PAGES = 8;

    @Override
    public String name() {
        return "find-pages";
    }

    @Override
    public String summary() {
        return "list the dashboards, pages and reports a name may mean, with what each page holds";
    }

    @Override
    public String usage() {
        return "usage: java -jar obi2pbi.jar find-pages <kb-folder> \"<term>\" [--limit <n>]\n"
                + "\n"
                + "  <kb-folder>    a Knowledge Base written by extract from an estate with a catalog\n"
                + "  \"<term>\"       the words to look for, for example \"sample sales overview\"\n"
                + "  --limit <n>    show at most n (default " + FindColumnsCommand.DEFAULT_LIMIT + ")\n"
                + "\n"
                + "Compares the term's words with the names and catalog paths of dashboards, dashboard pages and\n"
                + "reports, as find-columns compares words. For a page: its Row identifier as a document cites it,\n"
                + "its dashboard, its position, and the reports placed on it with their subject areas. For a\n"
                + "dashboard: its pages. For a report: its subject areas and the pages that place it.\n"
                + "\n"
                + "Exit codes: 0 searched (also when nothing was found); 2 wrong usage or no Knowledge Base.\n";
    }

    @Override
    public int run(List<String> arguments, PrintStream out, PrintStream err) throws UsageException {
        String kbArgument = null;
        String term = null;
        int limit = FindColumnsCommand.DEFAULT_LIMIT;
        for (int i = 0; i < arguments.size(); i++) {
            String argument = arguments.get(i);
            if (argument.equals("--limit")) {
                if (i + 1 >= arguments.size()) {
                    throw new UsageException("--limit needs a value", true);
                }
                String value = arguments.get(++i);
                try {
                    limit = Integer.parseInt(value);
                } catch (NumberFormatException e) {
                    limit = 0;
                }
                if (limit < 1) {
                    throw new UsageException("--limit needs a whole number of at least 1, not '" + value + "'", true);
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
        if (term == null || term.trim().isEmpty()) {
            throw new UsageException("no term given", true);
        }
        KbData kb;
        try {
            Path folder = Paths.get(kbArgument);
            if (!Files.isDirectory(folder)) {
                throw new UsageException("the Knowledge Base folder " + kbArgument + " was not found.", false);
            }
            kb = KbFolderReader.read(folder, PageSearch.TABLES);
        } catch (InvalidPathException e) {
            throw new UsageException("not a usable path: " + e.getMessage(), true);
        } catch (KbFormatException e) {
            throw new UsageException(e.getMessage(), false);
        } catch (IOException e) {
            throw new UsageException("cannot read " + kbArgument + ": " + e, false);
        }

        out.println("find-pages \"" + term + "\" in " + kbArgument);
        if (kb.table("dashboards").rows.isEmpty() && kb.table("analyses").rows.isEmpty()) {
            out.println("candidates: 0");
            out.println();
            out.println("This Knowledge Base has no dashboards and no reports (it was built without a catalog), so no page "
                    + "can be named. Leave existing_id empty and say so in an open question.");
            return OK;
        }
        List<PageSearch.Found> found = new PageSearch(kb).search(term);
        int shown = Math.min(limit, found.size());
        out.println("candidates: " + found.size() + (found.size() > shown ? ", the first " + shown + " shown (--limit "
                + limit + ")" : ""));
        if (found.isEmpty()) {
            out.println();
            out.println("No dashboard, page or report matches. Try other or fewer words.");
            return OK;
        }
        for (int i = 0; i < shown; i++) {
            PageSearch.Found f = found.get(i);
            out.println();
            out.println((i + 1) + ". " + f.kind + " " + Citation.of(f.rowId) + "  (" + f.group + ": "
                    + String.join(", ", f.wordsFound) + (f.wordsNotFound.isEmpty() ? "" : "; not found: "
                    + String.join(", ", f.wordsNotFound)) + "; score " + FindColumnsCommand.score(f.score) + ")");
            if (f.kind.equals(PageSearch.PAGE)) {
                out.println("   page \"" + f.name + "\", position " + f.position + " of dashboard \"" + f.dashboardName + "\" "
                        + Citation.of(f.dashboardId));
                out.println("   catalog path: " + f.path);
                if (f.reports.isEmpty()) {
                    out.println("   reports placed on it: none");
                } else {
                    out.println("   reports placed on it: " + f.reports.size());
                    for (PageSearch.Placed placed : f.reports.subList(0, Math.min(SHOWN_REPORTS, f.reports.size()))) {
                        out.println("     " + (placed.analysisId.isEmpty() ? "not in the catalog: " + placed.path
                                + " (the reference did not resolve)" : Citation.of(placed.analysisId) + " subject area "
                                + (placed.subjectAreas.isEmpty() ? "not stated" : String.join(", ", placed.subjectAreas))));
                    }
                    if (f.reports.size() > SHOWN_REPORTS) {
                        out.println("     ... and " + (f.reports.size() - SHOWN_REPORTS) + " more");
                    }
                }
            } else if (f.kind.equals(PageSearch.DASHBOARD)) {
                out.println("   dashboard \"" + f.name + "\", catalog path: " + f.path);
                out.println("   pages: " + f.pages.size());
                for (String[] page : f.pages.subList(0, Math.min(SHOWN_PAGES, f.pages.size()))) {
                    out.println("     " + page[2] + ". \"" + page[1] + "\" " + Citation.of(page[0]));
                }
                if (f.pages.size() > SHOWN_PAGES) {
                    out.println("     ... and " + (f.pages.size() - SHOWN_PAGES) + " more");
                }
            } else {
                out.println("   report \"" + f.name + "\", catalog path: " + f.path);
                out.println("   subject area: " + (f.subjectAreas.isEmpty() ? "not stated" : String.join(", ", f.subjectAreas)));
                out.println("   placed on " + (f.pages.isEmpty() ? "no dashboard page" : f.pages.size() + " page(s):"));
                for (String[] page : f.pages.subList(0, Math.min(SHOWN_PAGES, f.pages.size()))) {
                    out.println("     \"" + page[1] + "\" " + Citation.of(page[0]));
                }
            }
        }
        return OK;
    }
}
