package obi2pbi.out;

/** Refused to empty a folder that this jar did not write (Architecture AD-14). */
public final class NotAKnowledgeBaseException extends Exception {

    private static final long serialVersionUID = 1L;

    public NotAKnowledgeBaseException(String message) {
        super(message);
    }
}
