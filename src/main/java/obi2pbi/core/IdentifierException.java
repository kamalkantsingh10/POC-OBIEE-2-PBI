package obi2pbi.core;

/** An identifier could not be built or parsed. The message always names the offending value. */
public final class IdentifierException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public IdentifierException(String message) {
        super(message);
    }
}
