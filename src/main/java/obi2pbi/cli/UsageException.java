package obi2pbi.cli;

/** The command line is wrong, or points at something unusable. Nothing has been written. Exit code 2. */
public final class UsageException extends Exception {

    private static final long serialVersionUID = 1L;

    private final boolean showUsage;

    /** @param showUsage true to print the command's usage text after the message */
    public UsageException(String message, boolean showUsage) {
        super(message);
        this.showUsage = showUsage;
    }

    public boolean showUsage() {
        return showUsage;
    }
}
