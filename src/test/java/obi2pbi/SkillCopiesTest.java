package obi2pbi;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The agent and its skills are written once under {@code skills/} and copied unchanged to the two client
 * folders (Architecture AD-13), and they stay short enough for a small context (AD-17).
 *
 * <p>When a copy differs, run {@code ./build.sh skills} (or {@code build.cmd skills}).
 */
public final class SkillCopiesTest {

    private static final String[] CLIENT_FOLDERS = {".agents/skills", ".claude/skills"};
    private static final String AGENT = "obiee-migration-agent";
    private static final int AGENT_LINES = 150;
    private static final int SKILL_LINES = 300;

    private static Path skills() {
        return TestSupport.root().resolve("skills");
    }

    private static List<Path> skillFolders() throws IOException {
        try (Stream<Path> found = Files.list(skills())) {
            return found.filter(Files::isDirectory).sorted().collect(Collectors.toList());
        }
    }

    /** Every file below a folder, as paths relative to it with forward slashes, sorted. */
    private static List<String> files(Path folder) throws IOException {
        if (!Files.isDirectory(folder)) {
            return new ArrayList<>();
        }
        try (Stream<Path> found = Files.walk(folder)) {
            return found.filter(Files::isRegularFile)
                    .map(p -> folder.relativize(p).toString().replace('\\', '/'))
                    .sorted().collect(Collectors.toList());
        }
    }

    private static int lineCount(Path file) throws IOException {
        return Files.readAllLines(file, StandardCharsets.UTF_8).size();
    }

    public void testTheAgentAndItsSkillsAreThere() throws IOException {
        List<String> names = new ArrayList<>();
        for (Path folder : skillFolders()) {
            names.add(folder.getFileName().toString());
            Check.that(Files.isRegularFile(folder.resolve("SKILL.md")), folder + " has a SKILL.md");
            String text = Files.readString(folder.resolve("SKILL.md"), StandardCharsets.UTF_8);
            Check.that(text.startsWith("---\nname: " + folder.getFileName() + "\ndescription: "),
                    folder + "/SKILL.md starts with its name and a description");
        }
        Check.equal(List.of("obiee-as-is", AGENT, "obiee-review", "obiee-to-be", "obiee-trace"), names, "the skills under skills/");
        Check.that(Files.isRegularFile(skills().resolve("obiee-as-is/references/document-structure.md")),
                "the as-is skill has its document structure");
        Check.that(Files.isRegularFile(skills().resolve("obiee-trace/references/scoring.md")),
                "the trace skill has its scoring guide");
        Check.that(Files.isRegularFile(skills().resolve("obiee-review/references/matches.md")),
                "the review skill has its match review");
        Check.that(Files.isRegularFile(skills().resolve("obiee-review/references/to-be.md")),
                "the review skill has its to-be review");
        for (String file : new String[] {"document-structure.md", "obiee-features.md", "answers.md"}) {
            Check.that(Files.isRegularFile(skills().resolve("obiee-to-be/references/" + file)),
                    "the to-be skill has references/" + file);
        }
    }

    public void testEachCopyIsIdenticalToTheSkillItWasCopiedFrom() throws IOException {
        for (Path folder : skillFolders()) {
            String name = folder.getFileName().toString();
            List<String> expected = files(folder);
            for (String client : CLIENT_FOLDERS) {
                Path copy = TestSupport.root().resolve(client).resolve(name);
                Check.equal(expected, files(copy), "the files of " + client + "/" + name
                        + " (run ./build.sh skills)");
                for (String file : expected) {
                    Check.sameBytes(Files.readAllBytes(folder.resolve(file)), Files.readAllBytes(copy.resolve(file)),
                            client + "/" + name + "/" + file + " differs from skills/" + name + "/" + file
                                    + " (run ./build.sh skills)");
                }
            }
        }
    }

    public void testSkillFilesStayShort() throws IOException {
        for (Path folder : skillFolders()) {
            String name = folder.getFileName().toString();
            for (String file : files(folder)) {
                int limit = name.equals(AGENT) ? AGENT_LINES : SKILL_LINES;
                int lines = lineCount(folder.resolve(file));
                Check.that(lines < limit, "skills/" + name + "/" + file + " has " + lines
                        + " lines; it must stay under " + limit);
            }
        }
    }

    public void testTheCopilotEntryLoadsTheAgentFromTheAgentsFolder() throws IOException {
        Path entry = TestSupport.root().resolve(".github/agents/" + AGENT + ".agent.md");
        Check.that(Files.isRegularFile(entry), entry + " exists");
        String text = Files.readString(entry, StandardCharsets.UTF_8);
        Check.that(text.startsWith("---\ndescription: "), "the entry starts with a description");
        Check.contains(text, "{project-root}/.agents/skills/" + AGENT + "/SKILL.md", "the entry loads the agent's skill");
    }

    /** AD-13: the target system has only Java, so nothing may lean on BMad's scripts, Python or uv. */
    public void testSkillsDependOnNothingButJava() throws IOException {
        for (Path folder : skillFolders()) {
            for (String file : files(folder)) {
                String text = Files.readString(folder.resolve(file), StandardCharsets.UTF_8);
                for (String banned : new String[] {"uv run", "_bmad", "python3", "python -m", ".py", "{skill-root}"}) {
                    Check.that(!text.contains(banned), "skills/" + folder.getFileName() + "/" + file
                            + " must not mention '" + banned + "'");
                }
            }
        }
    }
}
