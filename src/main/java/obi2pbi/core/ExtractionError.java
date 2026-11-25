package obi2pbi.core;

/**
 * Something went wrong: content that should have been captured was not. The run records it and continues.
 */
public final class ExtractionError {

    public static final String MALFORMED_XML = "malformed_xml";
    public static final String UNREADABLE = "unreadable";
    public static final String MISSING_NAME = "missing_name";
    /** A name that cannot be part of an identifier: only spaces, or containing {@code ::}. */
    public static final String INVALID_NAME = "invalid_name";
    public static final String UNRESOLVED_REFERENCE = "unresolved_reference";
    public static final String AMBIGUOUS_REFERENCE = "ambiguous_reference";
    public static final String DUPLICATE_IDENTIFIER = "duplicate_identifier";

    public final String rpdId;
    public final String kind;
    public final String sourceId;
    public final String objectType;
    public final String reason;

    public ExtractionError(String rpdId, String kind, String sourceId, String objectType, String reason) {
        this.rpdId = rpdId;
        this.kind = kind;
        this.sourceId = sourceId;
        this.objectType = objectType;
        this.reason = reason;
    }
}
