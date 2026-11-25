package obi2pbi.cli;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import obi2pbi.core.Estate;
import obi2pbi.core.ReportLineage;
import obi2pbi.in.catalog.CatalogReader;
import obi2pbi.in.mds.RepositoryLayoutException;
import obi2pbi.in.mds.RepositoryReader;
import obi2pbi.out.KnowledgeBaseWriter;
import obi2pbi.out.NotAKnowledgeBaseException;

/**
 * {@code extract <estate-folder> [--out <folder>]}: reads an estate and writes its Knowledge Base.
 *
 * <p>Everything is read before anything is deleted or written, so a run that fails on its input leaves the
 * output folder as it was. A bad source file or object does not stop the run: it becomes a row in
 * {@code errors.csv} and the exit code is 3.
 */
public final class ExtractCommand implements Command {

    @Override
    public String name() {
        return "extract";
    }

    @Override
    public String summary() {
        return "read an estate folder and write its Knowledge Base";
    }

    @Override
    public String usage() {
        return "usage: java -jar obi2pbi.jar extract <estate-folder> [--out <folder>] [--timestamp <text>]\n"
                + "\n"
                + "  <estate-folder>     the folder to read; its name is the estate name\n"
                + "  --out <folder>      where to write the Knowledge Base (default: kb/<estate-name>)\n"
                + "  --timestamp <text>  the time stamp to put in manifest.csv, for repeatable output\n"
                + "                      (default: the current time)\n"
                + "\n"
                + EstateLayout.EXPECTED
                + "\n"
                + "The output folder is rewritten on each run. A folder that is not empty and has no\n"
                + "manifest.csv is refused. Exit codes: 0 written, errors.csv empty; 3 written, errors.csv\n"
                + "has rows; 2 wrong usage, nothing written.\n";
    }

    @Override
    public int run(List<String> arguments, PrintStream out, PrintStream err) throws UsageException {
        String folderArgument = null;
        String outArgument = null;
        String timestamp = null;
        for (int i = 0; i < arguments.size(); i++) {
            String argument = arguments.get(i);
            if (argument.equals("--out") || argument.equals("--timestamp")) {
                if (i + 1 >= arguments.size()) {
                    throw new UsageException(argument + " needs a value", true);
                }
                String value = arguments.get(++i);
                if (argument.equals("--out")) {
                    outArgument = value;
                } else {
                    timestamp = value;
                }
            } else if (argument.startsWith("--")) {
                throw new UsageException("unknown option '" + argument + "'", true);
            } else if (folderArgument == null) {
                folderArgument = argument;
            } else {
                throw new UsageException("more than one estate folder given: '" + folderArgument + "' and '"
                        + argument + "'", true);
            }
        }
        if (folderArgument == null) {
            throw new UsageException("no estate folder given", true);
        }

        EstateLayout layout;
        Path outFolder;
        try {
            layout = EstateLayout.resolve(Paths.get(folderArgument));
            outFolder = outArgument != null ? Paths.get(outArgument) : Paths.get("kb", layout.name);
        } catch (InvalidPathException e) {
            throw new UsageException("not a usable path: " + e.getMessage(), true);
        }

        try {
            // Checked before the work, acted on after it: a refusal costs nothing, and a failed read
            // leaves the output folder untouched.
            KnowledgeBaseWriter.checkOurs(outFolder);
            Estate estate = extract(layout);
            KnowledgeBaseWriter.write(estate, outFolder, folderArgument, timestamp);
            printSummary(layout, estate, outFolder, out);
            return estate.errors.isEmpty() ? OK : PROBLEMS_FOUND;
        } catch (NotAKnowledgeBaseException | RepositoryLayoutException e) {
            throw new UsageException(e.getMessage(), false);
        } catch (IOException e) {
            throw new UsageException("cannot write the Knowledge Base to " + outFolder + ": " + e, false);
        }
    }

    /** Reads every half the Operator supplied, each into an Estate of its own, and merges them. */
    static Estate extract(EstateLayout layout) throws RepositoryLayoutException {
        Estate estate = new Estate();
        estate.rpdIds.add(layout.name);
        estate.halvesPresent.put(layout.name, layout.halves());
        if (layout.rpd != null) {
            estate.merge(RepositoryReader.load(layout.rpd, layout.name));
        }
        if (layout.catalog != null) {
            estate.merge(CatalogReader.load(layout.catalog, layout.name));
        }
        // After the merge: neither reader sees the other half. Only halves in one estate folder are linked.
        ReportLineage.resolve(estate);
        estate.checkIdentifierUniqueness();
        return estate;
    }

    private static void printSummary(EstateLayout layout, Estate estate, Path outFolder, PrintStream out) {
        out.println("estate:   " + layout.name + " (" + String.join(" + ", layout.halves()) + ")");
        out.println("parsed:   " + estate.parsedTotal() + " objects");
        out.println("skipped:  " + estate.skippedTotal() + " objects");
        out.println("gaps:     " + estate.unparsed.size() + " not modelled  -> " + outFolder.resolve("unparsed.csv"));
        out.println("errors:   " + estate.errors.size() + "                 -> " + outFolder.resolve("errors.csv"));
        out.println("written:  " + outFolder);
    }
}
