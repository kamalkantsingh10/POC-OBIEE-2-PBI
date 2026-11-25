package obi2pbi;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The dependency rules of the Architecture Spine, checked on the main sources:
 * <ul>
 * <li>AD-15: nothing outside {@code java.base}, {@code java.xml} and the project's own packages.</li>
 * <li>Layering: {@code cli} may use everything; {@code in.*} and {@code out} may use {@code core};
 * {@code core} uses none of them. Inbound adapters share {@code in.xml} and do not use each other.</li>
 * <li>AD-6: {@code core} has no file, XML or source-format types.</li>
 * </ul>
 * The rules are a function of package and source text, so each is also shown to fail on a planted defect.
 */
public final class ArchitectureTest {

    private static final Pattern PACKAGE = Pattern.compile("(?m)^\\s*package\\s+([\\w.]+)\\s*;");
    private static final Pattern IMPORT = Pattern.compile("(?m)^\\s*import\\s+(?:static\\s+)?([\\w.]+?)(?:\\.\\*)?\\s*;");

    /** Packages of the modules java.base and java.xml that the jar may use. */
    private static final List<String> JDK_ALLOWED = List.of(
            "java.io.", "java.lang.", "java.math.", "java.net.", "java.nio.", "java.security.", "java.text.",
            "java.time.", "java.util.",
            "javax.xml.XMLConstants", "javax.xml.catalog.", "javax.xml.datatype.", "javax.xml.namespace.",
            "javax.xml.parsers.", "javax.xml.stream.", "javax.xml.transform.", "javax.xml.validation.",
            "javax.xml.xpath.", "org.w3c.dom.", "org.xml.sax.");

    /** Packages that look allowed by prefix and belong to other modules (java.logging, java.prefs, java.net.http). */
    private static final List<String> JDK_DENIED = List.of("java.util.logging.", "java.util.prefs.", "java.net.http.");

    /** What core must not import: files, networks and XML belong to the adapters. */
    private static final List<String> CORE_FORBIDDEN_IMPORTS = List.of(
            "java.io.", "java.nio.file.", "java.nio.channels.", "java.net.", "java.util.zip.", "java.util.jar.",
            "javax.xml.", "org.w3c.dom.", "org.xml.sax.");

    /** Source-format words that must not appear in core code (comments excluded). */
    private static final List<String> CORE_FORBIDDEN_WORDS =
            List.of("mdsid", ".xml", "objectRef", "ExprTextDesc", "oracle/bi/server");

    public void testMainSourcesFollowTheRules() throws Exception {
        List<String> problems = new ArrayList<>();
        int files = 0;
        Path main = TestSupport.root().resolve("src/main/java");
        try (Stream<Path> walk = Files.walk(main)) {
            for (Path file : walk.filter(p -> p.toString().endsWith(".java")).sorted().collect(Collectors.toList())) {
                String source = new String(Files.readAllBytes(file), StandardCharsets.UTF_8);
                for (String problem : violations(source)) {
                    problems.add(main.relativize(file) + ": " + problem);
                }
                String expected = main.relativize(file.getParent()).toString().replace(java.io.File.separatorChar, '.');
                Check.equal(expected, packageOf(source), main.relativize(file) + ": package matches its folder");
                files++;
            }
        }
        Check.that(files > 20, "the main sources were found: " + files);
        Check.equal(List.of(), problems, "rule violations");
    }

    public void testCoreImportingAnAdapterIsCaught() {
        for (String adapter : new String[] {"obi2pbi.in.mds.RepositoryReader", "obi2pbi.in.xml.XmlNode",
                "obi2pbi.out.CsvWriter", "obi2pbi.cli.Main", "obi2pbi.in.kb.CsvReader"}) {
            Check.equal(1, violations("package obi2pbi.core;\nimport " + adapter + ";\nclass X {}").size(),
                    "core importing " + adapter);
        }
        Check.equal(0, violations("package obi2pbi.core;\nimport obi2pbi.core.Estate;\nimport java.util.List;\nclass X {}").size(),
                "core importing core and java.util is fine");
    }

    public void testAnImportOutsideJavaBaseAndJavaXmlIsCaught() {
        for (String outside : new String[] {"org.junit.Test", "com.google.common.collect.Lists", "java.sql.Connection",
                "java.awt.Color", "javax.swing.JFrame", "java.util.logging.Logger", "javax.xml.crypto.Data",
                "javax.xml.bind.JAXB", "org.apache.commons.csv.CSVFormat", "jdk.internal.misc.Unsafe"}) {
            Check.equal(1, violations("package obi2pbi.out;\nimport " + outside + ";\nclass X {}").size(), "import " + outside);
        }
        Check.equal(1, violations("package obi2pbi.cli;\nimport static org.junit.Assert.*;\nclass X {}").size(), "a static star import");
        Check.equal(0, violations("package obi2pbi.in.xml;\nimport javax.xml.stream.XMLStreamReader;\nimport org.w3c.dom.Node;\n"
                + "import java.nio.file.Path;\nclass X {}").size(), "java.xml and java.base are fine");
    }

    public void testAdaptersImportingEachOtherIsCaught() {
        Check.equal(1, violations("package obi2pbi.out;\nimport obi2pbi.in.mds.RepositoryReader;\nclass X {}").size(), "out importing in");
        Check.equal(1, violations("package obi2pbi.in.mds;\nimport obi2pbi.out.CsvWriter;\nclass X {}").size(), "in importing out");
        Check.equal(1, violations("package obi2pbi.in.mds;\nimport obi2pbi.cli.Main;\nclass X {}").size(), "in importing cli");
        Check.equal(1, violations("package obi2pbi.in.catalog;\nimport obi2pbi.in.mds.ReferenceIndex;\nclass X {}").size(),
                "one inbound adapter importing another");
        Check.equal(1, violations("package obi2pbi.in.xml;\nimport obi2pbi.in.mds.ReferenceIndex;\nclass X {}").size(),
                "the shared XML layer importing an adapter");
        Check.equal(0, violations("package obi2pbi.in.catalog;\nimport obi2pbi.in.xml.XmlNode;\nimport obi2pbi.core.Estate;\nclass X {}").size(),
                "an inbound adapter using the shared XML layer and core is fine");
        Check.equal(0, violations("package obi2pbi.cli;\nimport obi2pbi.in.mds.RepositoryReader;\nimport obi2pbi.out.CsvWriter;\nclass X {}").size(),
                "cli may use everything");
    }

    public void testFileXmlAndSourceFormatConceptsInCoreAreCaught() {
        Check.equal(1, violations("package obi2pbi.core;\nimport java.nio.file.Path;\nclass X {}").size(), "a file-path type in core");
        Check.equal(1, violations("package obi2pbi.core;\nimport javax.xml.stream.XMLStreamReader;\nclass X {}").size(), "an XML type in core");
        Check.equal(1, violations("package obi2pbi.core;\nclass X { String k = \"mdsid\"; }").size(), "a source-format word in core");
        Check.equal(0, violations("package obi2pbi.core;\n/** Never holds an mdsid. */\nclass X { } // nor a .xml name").size(),
                "the same words in comments are fine");
        Check.equal(0, violations("package obi2pbi.in.mds;\nclass X { String k = \"mdsid\"; }").size(), "and fine in the adapter");
    }

    /** The rule violations in one source file. */
    static List<String> violations(String source) {
        List<String> problems = new ArrayList<>();
        String pkg = packageOf(source);
        Matcher imports = IMPORT.matcher(source);
        while (imports.find()) {
            String imported = imports.group(1);
            if (imported.startsWith("obi2pbi.")) {
                if (!mayUse(pkg, imported)) {
                    problems.add(pkg + " must not import " + imported);
                }
            } else if (JDK_DENIED.stream().anyMatch((imported + ".")::startsWith)
                    || JDK_ALLOWED.stream().noneMatch(a -> (imported + ".").startsWith(a) || imported.equals(a))) {
                problems.add("import outside java.base and java.xml: " + imported);
            } else if (pkg.equals("obi2pbi.core") && CORE_FORBIDDEN_IMPORTS.stream().anyMatch((imported + ".")::startsWith)) {
                problems.add("core must not use file, network or XML types: " + imported);
            }
        }
        if (pkg.equals("obi2pbi.core")) {
            String code = IMPORT.matcher(withoutComments(source)).replaceAll("");
            for (String word : CORE_FORBIDDEN_WORDS) {
                if (code.contains(word)) {
                    problems.add("core must not hold the source-format concept '" + word + "'");
                }
            }
        }
        return problems;
    }

    private static boolean mayUse(String pkg, String imported) {
        if (pkg.startsWith("obi2pbi.cli")) {
            return true;
        }
        if (imported.startsWith("obi2pbi.core.")) {
            return true;
        }
        if (pkg.equals("obi2pbi.core")) {
            return false;
        }
        if (pkg.equals("obi2pbi.out")) {
            return imported.startsWith("obi2pbi.out.");
        }
        if (pkg.equals("obi2pbi.in.xml")) {
            return imported.startsWith("obi2pbi.in.xml.");
        }
        if (pkg.startsWith("obi2pbi.in.")) {
            return imported.startsWith("obi2pbi.in.xml.") || imported.startsWith(pkg + ".");
        }
        return false;
    }

    private static String packageOf(String source) {
        Matcher match = PACKAGE.matcher(source);
        return match.find() ? match.group(1) : "";
    }

    private static String withoutComments(String source) {
        return source.replaceAll("(?s)/\\*.*?\\*/", "").replaceAll("(?m)//.*$", "");
    }
}
