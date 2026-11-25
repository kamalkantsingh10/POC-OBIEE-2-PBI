package obi2pbi.in.docx;

/** The file cannot be read as a Word document: not found, an old-style {@code .doc}, not a zip, or no document part. */
public final class DocxException extends Exception {

    private static final long serialVersionUID = 1L;

    public DocxException(String message) {
        super(message);
    }
}
