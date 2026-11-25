package obi2pbi.in.catalog;

import obi2pbi.in.xml.XmlNode;

/** One catalog file that is XML: where it is, what type of object it holds, and its tree. */
final class CatalogObject {

    /** The decoded catalog path. Every identifier and every error about the object is built from it. */
    final String path;
    /** The object type: the root element's full name. */
    final String type;
    /** The root element. Its children are present only when the type has a reader. */
    final XmlNode root;
    /** The object's Row identifier; set just before its reader runs. */
    String rowId;

    CatalogObject(String path, XmlNode root) {
        this.path = path;
        this.type = root.tag();
        this.root = root;
    }
}
