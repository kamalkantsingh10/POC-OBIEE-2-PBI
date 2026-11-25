package obi2pbi.core;

/**
 * A known gap: content that is in the source and not in the Knowledge Base. Nothing is broken.
 *
 * <p>Kept apart from {@link ExtractionError} so that "we have not built this" and "this went wrong" can
 * never be confused. The two are written to different files and are never merged (Architecture AD-3).
 */
public final class Unparsed {

    public static final String UNMODELLED_TYPE = "unmodelled_type";
    public static final String UNREAD_ATTRIBUTE = "unread_attribute";
    public static final String UNREAD_ELEMENT = "unread_element";
    public static final String UNREAD_TEXT = "unread_text";
    public static final String UNDISCOVERED_FILE = "undiscovered_file";
    public static final String NOT_A_CATALOG_OBJECT = "not_a_catalog_object";
    public static final String EXCLUDED_BY_SCOPE = "excluded_by_scope";
    /** A catalog file name with a malformed escape; the name is kept as far as it could be decoded. */
    public static final String UNDECODED_NAME = "undecoded_name";
    public static final String REDACTED_VALUE = "redacted_value";

    public final String rpdId;
    public final String kind;
    public final String objectType;
    public final String detail;
    public final int occurrences;

    public Unparsed(String rpdId, String kind, String objectType, String detail, int occurrences) {
        this.rpdId = rpdId;
        this.kind = kind;
        this.objectType = objectType;
        this.detail = detail;
        this.occurrences = occurrences;
    }
}
