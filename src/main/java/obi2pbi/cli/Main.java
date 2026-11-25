package obi2pbi.cli;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;

/**
 * The jar's entry point: finds the command named first on the command line and runs it.
 *
 * <p><b>To add a command, write a class that implements {@link Command} and add it to
 * {@link #COMMANDS}.</b> Nothing else here changes.
 */
public final class Main {

    private static final List<Command> COMMANDS = List.of(
            new ExtractCommand(),
            new SliceCommand(),
            new CheckDocCommand(),
            new AssembleDocCommand(),
            new FsdTextCommand(),
            new FindColumnsCommand(),
            new CheckMatchesCommand(),
            new FindPagesCommand());

    private Main() {
    }

    public static void main(String[] args) {
        System.exit(run(args, System.out, System.err));
    }

    /** Runs a command line and returns the exit code. Never calls {@code System.exit}, so tests can use it. */
    public static int run(String[] args, PrintStream out, PrintStream err) {
        if (args.length == 0) {
            err.println("error: no command given");
            err.println();
            err.print(usage());
            return Command.WRONG_USAGE;
        }
        String name = args[0];
        if (isHelp(name)) {
            out.print(usage());
            return Command.OK;
        }
        for (Command command : COMMANDS) {
            if (!command.name().equals(name)) {
                continue;
            }
            List<String> arguments = Arrays.asList(args).subList(1, args.length);
            if (arguments.stream().anyMatch(Main::isHelp)) {
                out.print(command.usage());
                return Command.OK;
            }
            try {
                return command.run(arguments, out, err);
            } catch (UsageException e) {
                err.println("error: " + e.getMessage());
                if (e.showUsage()) {
                    err.println();
                    err.print(command.usage());
                }
                return Command.WRONG_USAGE;
            }
        }
        err.println("error: unknown command '" + name + "'");
        err.println();
        err.print(usage());
        return Command.WRONG_USAGE;
    }

    static String usage() {
        StringBuilder text = new StringBuilder();
        text.append("usage: java -jar obi2pbi.jar <command> [arguments]\n\ncommands:\n");
        for (Command command : COMMANDS) {
            text.append(String.format("  %-14s %s%n", command.name(), command.summary()).replace("\r\n", "\n"));
        }
        text.append("\nRun a command with --help for its arguments.\n");
        text.append("Exit codes: 0 success; 2 wrong usage, nothing written; 3 finished and found problems.\n");
        return text.toString();
    }

    private static boolean isHelp(String argument) {
        return argument.equals("--help") || argument.equals("-h");
    }
}
