package obi2pbi.cli;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import obi2pbi.core.FsdDocument;
import obi2pbi.in.docx.DocxException;
import obi2pbi.in.docx.DocxReader;
import obi2pbi.out.FsdMarkdownWriter;
import obi2pbi.out.KnowledgeBaseWriter;
import obi2pbi.out.NotAKnowledgeBaseException;

/**
 * {@code fsd-text <fsd.docx> [--out <folder>]}: turns an FSD saved as a Word file into {@code fsd.md}, with an
 * anchor on every paragraph and table cell (story 3.1; Architecture AD-10).
 */
public final class FsdTextCommand implements Command {

    @Override
    public String name() {
        return "fsd-text";
    }

    @Override
    public String summary() {
        return "turn an FSD (.docx) into fsd.md, with an anchor on every paragraph and table cell";
    }

    @Override
    public String usage() {
        return "usage: java -jar obi2pbi.jar fsd-text <fsd.docx> [--out <folder>]\n"
                + "\n"
                + "  <fsd.docx>       the FSD as a Word document, for example work/monthly-overview/fsd.docx\n"
                + "  --out <folder>   where to write (default: the folder the .docx is in)\n"
                + "\n"
                + "Writes fsd.md: the FSD's headings, paragraphs, lists and tables in document order, the text as\n"
                + "written, each paragraph anchored [p12] and each table cell [t3.r2.c1]. The same file always gives\n"
                + "the same bytes. Also writes fsd.manifest.csv (the source file's name, size and SHA-256, and the\n"
                + "counts), and, when fsd.md is longer than " + FsdMarkdownWriter.SECTION_THRESHOLD + " lines, "
                + "fsd.sections/ (index.md and one file per\nsection of at most " + (FsdMarkdownWriter.SECTION_LINES + 2)
                + " lines) for a skill that reads one section at a time.\n"
                + "Not read: images, comments, deleted text of tracked changes, headers, footers, footnotes.\n"
                + "\n"
                + "Exit codes: 0 written; 2 wrong usage, or the file is not a .docx (an old .doc must be saved as\n"
                + ".docx first), nothing written.\n";
    }

    @Override
    public int run(List<String> arguments, PrintStream out, PrintStream err) throws UsageException {
        String fileArgument = null;
        String outArgument = null;
        for (int i = 0; i < arguments.size(); i++) {
            String argument = arguments.get(i);
            if (argument.equals("--out")) {
                if (i + 1 >= arguments.size()) {
                    throw new UsageException("--out needs a value", true);
                }
                outArgument = arguments.get(++i);
            } else if (argument.startsWith("--")) {
                throw new UsageException("unknown option '" + argument + "'", true);
            } else if (fileArgument == null) {
                fileArgument = argument;
            } else {
                throw new UsageException("more than one file given: '" + fileArgument + "' and '" + argument + "'", true);
            }
        }
        if (fileArgument == null) {
            throw new UsageException("no .docx file given", true);
        }
        Path file;
        Path folder;
        try {
            file = Paths.get(fileArgument);
            folder = outArgument != null ? Paths.get(outArgument)
                    : file.getParent() != null ? file.getParent() : Paths.get(".");
        } catch (InvalidPathException e) {
            throw new UsageException("not a usable path: " + e.getMessage(), true);
        }

        FsdDocument document;
        try {
            document = DocxReader.read(file);
        } catch (DocxException e) {
            throw new UsageException(e.getMessage(), false);
        }
        FsdMarkdownWriter.Written written;
        try {
            FsdMarkdownWriter.checkOurs(folder);
            List<String[]> manifest = new ArrayList<>();
            manifest.add(new String[] {"written_by", "obi2pbi fsd-text " + KnowledgeBaseWriter.VERSION});
            manifest.add(new String[] {"source_file", file.getFileName().toString()});
            manifest.add(new String[] {"source_bytes", Long.toString(Files.size(file))});
            manifest.add(new String[] {"source_sha256", sha256(file)});
            written = FsdMarkdownWriter.write(document, folder, manifest);
        } catch (NotAKnowledgeBaseException e) {
            throw new UsageException(e.getMessage(), false);
        } catch (IOException e) {
            throw new UsageException("cannot write to " + folder + ": " + e, false);
        }
        out.println("read:       " + fileArgument);
        out.println("paragraphs: " + document.paragraphCount() + " (headings, paragraphs and list items, each [p<n>])");
        out.println("tables:     " + document.tableCount() + " (" + document.cellCount() + " cells, each [t<n>.r<n>.c<n>])");
        out.println("written:    " + folder.resolve(FsdMarkdownWriter.MARKDOWN) + " (" + written.lines + " lines)");
        out.println("manifest:   " + folder.resolve(FsdMarkdownWriter.MANIFEST));
        if (!written.sections.isEmpty()) {
            out.println("sections:   " + written.sections.size() + " -> " + folder.resolve(FsdMarkdownWriter.SECTIONS)
                    .resolve(FsdMarkdownWriter.INDEX) + " (fsd.md is longer than " + FsdMarkdownWriter.SECTION_THRESHOLD
                    + " lines; read one section at a time)");
        }
        return OK;
    }

    /** The file's SHA-256, as lower-case hex, so a later step can tell that the FSD changed. */
    static String sha256(Path file) throws IOException {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("every Java runtime has SHA-256", e);
        }
        try (InputStream in = Files.newInputStream(file)) {
            byte[] buffer = new byte[8192];
            for (int read = in.read(buffer); read > 0; read = in.read(buffer)) {
                digest.update(buffer, 0, read);
            }
        }
        StringBuilder hex = new StringBuilder();
        for (byte b : digest.digest()) {
            hex.append(String.format("%02x", b & 0xff));
        }
        return hex.toString();
    }
}
