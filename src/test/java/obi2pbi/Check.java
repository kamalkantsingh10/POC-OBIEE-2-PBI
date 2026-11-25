package obi2pbi;

import java.util.Arrays;
import java.util.Objects;

/** The few assertions the tests need. Each throws {@link AssertionError} with what was expected and found. */
public final class Check {

    /** A block of test code that may throw anything. */
    public interface Action {
        void run() throws Exception;
    }

    private Check() {
    }

    public static void that(boolean condition, String what) {
        if (!condition) {
            throw new AssertionError(what);
        }
    }

    public static void equal(Object expected, Object actual, String what) {
        if (!Objects.equals(expected, actual)) {
            throw new AssertionError(what + "\n  expected: " + show(expected) + "\n  actual:   " + show(actual));
        }
    }

    public static void contains(String text, String part, String what) {
        if (text == null || !text.contains(part)) {
            throw new AssertionError(what + "\n  expected to contain: " + part + "\n  actual: " + text);
        }
    }

    public static void sameBytes(byte[] expected, byte[] actual, String what) {
        if (!Arrays.equals(expected, actual)) {
            throw new AssertionError(what + ": the bytes differ");
        }
    }

    /** Runs the action and requires it to throw the given kind of exception, which is returned. */
    public static <T extends Throwable> T raises(Class<T> kind, Action action, String what) {
        try {
            action.run();
        } catch (Throwable thrown) {
            if (kind.isInstance(thrown)) {
                return kind.cast(thrown);
            }
            throw new AssertionError(what + ": expected " + kind.getSimpleName() + ", got " + thrown, thrown);
        }
        throw new AssertionError(what + ": expected " + kind.getSimpleName() + ", nothing was thrown");
    }

    private static String show(Object value) {
        return value instanceof String ? "\"" + value + "\"" : String.valueOf(value);
    }
}
