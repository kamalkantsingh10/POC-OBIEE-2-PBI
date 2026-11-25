package obi2pbi.cli;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import obi2pbi.core.DocAssembly;
import obi2pbi.core.Text;

/**
 * {@code assemble-doc <document>}: joins the parts a skill wrote in {@code <document without .md>.parts/}
 * into the document, in file-name order. A skill therefore needs no shell command to build its document
 * (Architecture AD-13), and never reads or rewrites the whole of it (AD-17).
 *
 * <p>Before it overwrites a document it looks for answers a user typed into that document. If one is not in
 * the answers file yet, nothing is written (AD-16: an answer is never lost).
 */
public final class AssembleDocCommand implements Command {

    @Override
    public String name() {
        return "assemble-doc";
    }

    @Override
    public String summary() {
        return "join the parts a skill wrote into the document, keeping answers typed into it safe";
    }

    @Override
    public String usage() {
        return "usage: java -jar obi2pbi.jar assemble-doc <document>\n"
                + "\n"
                + "  <document>   the markdown document to write, for example as-is/my-estate/sales.md\n"
                + "\n"
                + "Read: every .md file of the folder <document without .md>.parts, in file-name order. Line ends\n"
                + "become line feeds, empty lines at the start and end of a part are dropped, one empty line is\n"
                + "put between two parts, and an empty part adds nothing. The same parts always give the same\n"
                + "bytes (UTF-8).\n"
                + "\n"
                + "Before writing, the document that is there is read. If a question in it has an answer after\n"
                + "'Answer:' that <document without .md>.answers.md does not hold, nothing is written and the\n"
                + "questions are listed with the answers typed: record those answers in the answers file, then run this again.\n"
                + "\n"
                + "Exit codes: 0 written; 3 not written, answers to record first; 2 wrong usage or no parts,\n"
                + "nothing written.\n";
    }

    @Override
    public int run(List<String> arguments, PrintStream out, PrintStream err) throws UsageException {
        String documentArgument = null;
        for (String argument : arguments) {
            if (argument.startsWith("--")) {
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
        try {
            Path document = Paths.get(documentArgument);
            String fileName = document.getFileName().toString();
            if (!fileName.endsWith(".md")) {
                throw new UsageException("the document's name must end in .md: " + documentArgument, false);
            }
            String base = fileName.substring(0, fileName.length() - 3);
            Path partsFolder = document.resolveSibling(base + ".parts");
            Path answersFile = document.resolveSibling(base + ".answers.md");
            if (!Files.isDirectory(partsFolder)) {
                throw new UsageException("the parts folder " + partsFolder + " was not found.", false);
            }
            List<String> names = partNames(partsFolder);
            if (names.isEmpty()) {
                throw new UsageException("the parts folder " + partsFolder + " holds no .md file.", false);
            }
            List<String> texts = new ArrayList<>();
            for (String name : names) {
                texts.add(new String(Files.readAllBytes(partsFolder.resolve(name)), StandardCharsets.UTF_8));
            }
            DocAssembly.Joined joined = DocAssembly.join(names, texts);

            if (Files.isRegularFile(document)) {
                String existing = new String(Files.readAllBytes(document), StandardCharsets.UTF_8);
                String answers = Files.isRegularFile(answersFile)
                        ? new String(Files.readAllBytes(answersFile), StandardCharsets.UTF_8) : null;
                Map<String, String> unrecorded = DocAssembly.unrecordedAnswers(existing, answers);
                if (!unrecorded.isEmpty()) {
                    out.println("not written: " + documentArgument + " holds " + unrecorded.size()
                            + " answer(s) that " + answersFile + " does not hold yet.");
                    out.println("questions:  " + unrecorded.keySet().stream().map(n -> "Q" + n).collect(Collectors.joining(", ")));
                    for (Map.Entry<String, String> answer : unrecorded.entrySet()) {
                        out.println("  Q" + answer.getKey() + ": " + answer.getValue());
                    }
                    out.println("Add each answer to the answers file as a new entry (never change an entry that "
                            + "is there), then run this again.");
                    return PROBLEMS_FOUND;
                }
            }
            Files.write(document, joined.text.getBytes(StandardCharsets.UTF_8));
            for (String[] part : joined.parts) {
                out.println(String.format("  %-44s lines %s-%s", part[0], part[1], part[2]));
            }
            int empty = names.size() - joined.parts.size();
            out.println("parts:      " + names.size() + (empty == 0 ? "" : " (" + empty + " empty, adding nothing)"));
            out.println("lines:      " + joined.lines);
            out.println("written:    " + documentArgument);
            return OK;
        } catch (InvalidPathException e) {
            throw new UsageException("not a usable path: " + e.getMessage(), true);
        } catch (IOException e) {
            throw new UsageException("cannot read or write: " + e, false);
        }
    }

    /** The {@code .md} files of the parts folder, in code-point order of their names. */
    static List<String> partNames(Path partsFolder) throws IOException {
        try (Stream<Path> found = Files.list(partsFolder)) {
            return found.filter(Files::isRegularFile)
                    .map(p -> p.getFileName().toString())
                    .filter(n -> n.endsWith(".md"))
                    .sorted(Text.CODE_POINT_ORDER)
                    .collect(Collectors.toList());
        }
    }
}
