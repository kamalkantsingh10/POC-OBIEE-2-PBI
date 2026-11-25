package obi2pbi;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Enumeration;
import java.util.Optional;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.spi.ToolProvider;

/** What the build produces: one jar of Java 11 class files that needs only java.base and java.xml. */
public final class BuildTest {

    private static final int JAVA_11_CLASS_VERSION = 55;

    private static Path jar() {
        Path jar = TestSupport.root().resolve("build/obi2pbi.jar");
        if (!Files.isRegularFile(jar)) {
            throw new SkipException("build/obi2pbi.jar is not built");
        }
        return jar;
    }

    public void testTheJarHoldsJava11ClassFilesAndNamesItsMainClass() throws Exception {
        int classes = 0;
        try (JarFile jar = new JarFile(jar().toFile())) {
            Check.equal("obi2pbi.cli.Main", jar.getManifest().getMainAttributes().getValue("Main-Class"), "Main-Class");
            for (Enumeration<JarEntry> entries = jar.entries(); entries.hasMoreElements();) {
                JarEntry entry = entries.nextElement();
                if (!entry.getName().endsWith(".class")) {
                    continue;
                }
                Check.that(entry.getName().startsWith("obi2pbi/"), "only the project's own classes: " + entry.getName());
                try (InputStream in = jar.getInputStream(entry); DataInputStream data = new DataInputStream(in)) {
                    Check.equal(0xCAFEBABE, data.readInt(), entry.getName() + ": class file");
                    data.readUnsignedShort();
                    Check.equal(JAVA_11_CLASS_VERSION, data.readUnsignedShort(), entry.getName() + ": class file version");
                }
                classes++;
            }
        }
        Check.that(classes > 20, "the jar holds the classes: " + classes);
    }

    public void testTheJarNeedsOnlyJavaBaseAndJavaXml() {
        Optional<ToolProvider> jdeps = ToolProvider.findFirst("jdeps");
        if (jdeps.isEmpty()) {
            throw new SkipException("this Java runtime has no jdeps tool");
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream stream = new PrintStream(out);
        int exit = jdeps.get().run(stream, stream, "--print-module-deps", jar().toString());
        String modules = out.toString().trim();
        Check.equal(0, exit, "jdeps ran: " + modules);
        Check.equal("java.base,java.xml", modules, "modules the jar depends on");
    }
}
