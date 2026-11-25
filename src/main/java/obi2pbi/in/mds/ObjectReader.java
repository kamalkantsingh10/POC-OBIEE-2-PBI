package obi2pbi.in.mds;

import obi2pbi.in.xml.XmlNode;

/**
 * Reads the files of one repository object type. One class per object type.
 *
 * <p>Reading is in two phases over all files, so references never depend on reading order (deliberate
 * difference 5):
 * <ol>
 * <li>{@link #bind}: give the object, and each part of it that can be referred to, its Row identifier, with
 * {@link ReferenceIndex#bind}. Report here anything that stops an identifier being built (no name, a name
 * that cannot be used). Phase one runs for every reader in registration order, so a reader may rely on
 * identifiers bound by readers registered before it (a presentation table needs its subject area's).</li>
 * <li>{@link #read}: produce rows. By now every identifier is bound, so every reference can be resolved.
 * Skip an object or part whose {@link ReferenceIndex#identifierOf} is null: phase one already said why.</li>
 * </ol>
 *
 * <p>Take everything from the XML through {@link XmlNode}'s recording accessors. Whatever a reader does not
 * read is reported in {@code unparsed.csv} with no further work, and stops being reported the moment a
 * reader starts reading it.
 */
public interface ObjectReader {

    /** The object-type folder this reader handles, for example {@code PhysicalTable}. */
    String objectType();

    void bind(XmlNode root, ReadContext context);

    void read(XmlNode root, ReadContext context);
}
