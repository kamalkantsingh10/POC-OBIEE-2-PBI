package obi2pbi.cli;

import java.io.PrintStream;
import java.util.List;

/**
 * One command of the jar. Each command is a class of its own, registered in {@link Main}.
 *
 * <p>Shared behaviour (Architecture AD-14): exit 0 for success, 2 for wrong usage with nothing written,
 * 3 when the run finished and found problems. A command rewrites its whole output folder and refuses to
 * empty a folder this jar did not write.
 */
public interface Command {

    int OK = 0;
    int WRONG_USAGE = 2;
    int PROBLEMS_FOUND = 3;

    /** The word typed on the command line, for example {@code extract}. */
    String name();

    /** One line for the list of commands. */
    String summary();

    /** The command's full usage text. */
    String usage();

    /**
     * Runs the command.
     *
     * @param arguments what followed the command's name
     * @return the exit code
     * @throws UsageException the arguments are wrong, or point at something that cannot be used
     */
    int run(List<String> arguments, PrintStream out, PrintStream err) throws UsageException;
}
