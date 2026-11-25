package obi2pbi.in.xml;

/** One parsed source file: which estate it belongs to, what type of object it holds, and its tree. */
public final class SourceTree {

    public final String rpdId;
    /** Repository: the object-type folder the file sits in. Catalog: the root element's full name. */
    public final String objectType;
    /** What to call the file in messages: its name or its path within the source. */
    public final String sourceName;
    public final XmlNode root;

    public SourceTree(String rpdId, String objectType, String sourceName, XmlNode root) {
        this.rpdId = rpdId;
        this.objectType = objectType;
        this.sourceName = sourceName;
        this.root = root;
    }
}
