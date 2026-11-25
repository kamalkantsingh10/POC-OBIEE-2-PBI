package obi2pbi.in.catalog;

/**
 * Reads the catalog objects of one type into the estate.
 *
 * <p>Every read of the object's XML goes through the recording methods of
 * {@link obi2pbi.in.xml.XmlNode}; whatever a reader does not take is reported by the audit.
 */
interface CatalogObjectReader {

    /** The root element this reader handles, as a full name. */
    String objectType();

    /**
     * Produces the object's rows. The object already has its Row identifier. The reader counts the object
     * as read and records its version through the context.
     */
    void read(CatalogObject object, CatalogContext context);
}
