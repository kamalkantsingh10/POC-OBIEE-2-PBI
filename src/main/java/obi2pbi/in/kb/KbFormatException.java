package obi2pbi.in.kb;

/** The folder is not a Knowledge Base or Slice this jar can read: no manifest, or a listed table is missing. */
public final class KbFormatException extends Exception {

    private static final long serialVersionUID = 1L;

    public KbFormatException(String message) {
        super(message);
    }
}
