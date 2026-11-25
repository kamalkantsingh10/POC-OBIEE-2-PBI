package obi2pbi;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The project's test runner (Architecture AD-15: no test library).
 *
 * <p>Finds every class named {@code *Test} among the compiled test classes and runs each public method
 * whose name starts with {@code test} and takes no arguments, on a fresh instance. A test passes when it
 * returns, fails when it throws, and is skipped (visibly) when it throws {@link SkipException}. Every
 * result is printed. The exit code is 1 when any test fails or none is found.
 *
 * <p>An optional argument runs only tests whose {@code Class.method} name contains it.
 */
public final class TestRunner {

    private TestRunner() {
    }

    public static void main(String[] args) throws Exception {
        String filter = args.length > 0 ? args[0] : "";
        int passed = 0;
        int skipped = 0;
        List<String> failed = new ArrayList<>();
        for (Class<?> testClass : testClasses()) {
            List<Method> methods = Stream.of(testClass.getMethods())
                    .filter(m -> m.getName().startsWith("test") && m.getParameterCount() == 0
                            && !Modifier.isStatic(m.getModifiers()))
                    .sorted(Comparator.comparing(Method::getName))
                    .collect(Collectors.toList());
            for (Method method : methods) {
                String name = testClass.getSimpleName() + "." + method.getName();
                if (!name.contains(filter)) {
                    continue;
                }
                try {
                    method.invoke(testClass.getDeclaredConstructor().newInstance());
                    System.out.println("PASS  " + name);
                    passed++;
                } catch (InvocationTargetException e) {
                    Throwable cause = e.getCause();
                    if (cause instanceof SkipException) {
                        System.out.println("SKIP  " + name + "  (" + cause.getMessage() + ")");
                        skipped++;
                    } else {
                        System.out.println("FAIL  " + name);
                        System.out.println(indent(stackTrace(cause)));
                        failed.add(name);
                    }
                }
            }
        }
        System.out.println();
        System.out.println("tests: " + passed + " passed, " + failed.size() + " failed, " + skipped + " skipped");
        for (String name : failed) {
            System.out.println("  failed: " + name);
        }
        if (!failed.isEmpty() || passed == 0) {
            System.exit(1);
        }
    }

    private static List<Class<?>> testClasses() throws URISyntaxException, java.io.IOException,
            ClassNotFoundException {
        Path classes = Paths.get(TestRunner.class.getProtectionDomain().getCodeSource().getLocation().toURI());
        List<String> names;
        try (Stream<Path> walk = Files.walk(classes)) {
            names = walk.filter(p -> p.getFileName().toString().endsWith("Test.class"))
                    .map(p -> classes.relativize(p).toString())
                    .map(p -> p.substring(0, p.length() - ".class".length()).replace(File.separatorChar, '.'))
                    .sorted()
                    .collect(Collectors.toList());
        }
        List<Class<?>> found = new ArrayList<>();
        for (String name : names) {
            found.add(Class.forName(name));
        }
        return found;
    }

    private static String stackTrace(Throwable error) {
        StringWriter text = new StringWriter();
        error.printStackTrace(new PrintWriter(text));
        return text.toString().trim();
    }

    private static String indent(String text) {
        return "      " + text.replace("\n", "\n      ");
    }
}
