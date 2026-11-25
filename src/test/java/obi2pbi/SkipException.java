package obi2pbi;

/** Thrown by a test that cannot run here, for a stated reason. The runner reports it as skipped. */
public final class SkipException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public SkipException(String reason) {
        super(reason);
    }
}
