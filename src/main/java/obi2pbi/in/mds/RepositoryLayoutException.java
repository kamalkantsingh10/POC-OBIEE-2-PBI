package obi2pbi.in.mds;

/** The folder given as a repository is not one. The command treats this as wrong usage: nothing is written. */
public final class RepositoryLayoutException extends Exception {

    private static final long serialVersionUID = 1L;

    public RepositoryLayoutException(String message) {
        super(message);
    }
}
