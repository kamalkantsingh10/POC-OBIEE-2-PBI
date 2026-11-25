package obi2pbi.in.catalog;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import javax.xml.stream.XMLStreamException;
import obi2pbi.core.Coverage;
import obi2pbi.core.DashboardContent;
import obi2pbi.core.Estate;
import obi2pbi.core.ExtractionError;
import obi2pbi.core.IdentifierException;
import obi2pbi.core.Identifiers;
import obi2pbi.core.Text;
import obi2pbi.core.Unparsed;
import obi2pbi.in.xml.ReadAudit;
import obi2pbi.in.xml.SourceTree;
import obi2pbi.in.xml.XmlNode;
import obi2pbi.in.xml.XmlTreeReader;

/**
 * Reads one Web Catalog folder into an {@link Estate}, under identifier layer {@code cat}.
 *
 * <p>The folder is a catalog because the Operator put it at {@code catalog/}, never because of what is in
 * it. Order of work: find every file; index every path, objects and folders alike; then take the files
 * one at a time, in file order: try the file as XML once (a file that is not XML is classified by its
 * first bytes, never reported as an error), run the reader of its type, count what the reader did not
 * take, and let the tree go. Last: give pages their dashboards, report the object types no reader
 * handles, and write the audit's counts.
 *
 * <p>One object at a time, because the trees of a whole catalog do not fit in a small heap once reports
 * are read. It is sound because a reader needs nothing but its own object and the path index: no row
 * depends on another object having been read first, pages get their dashboards afterwards from the rows,
 * and every table is sorted when written. Dashboards are still met in file order, which decides the owner
 * of a page that two dashboards list.
 */
public final class CatalogReader {

    /**
     * The readers, in the order they run. <b>To read a new object type, add its reader here.</b> Nothing
     * else changes: discovery, classification and the path index already cover every file, and an object
     * type with no reader is reported whole as {@code unmodelled_type}.
     */
    private static final List<CatalogObjectReader> READERS = List.of(
            new DashboardReader(),
            new DashboardPageReader(),
            new AnalysisReader(),
            new SavedFilterReader());

    private CatalogReader() {
    }

    public static Estate load(Path root, String rpdId) {
        return load(root, rpdId, READERS);
    }

    static Estate load(Path root, String rpdId, List<CatalogObjectReader> readers) {
        Identifiers.validateEstateName(rpdId);
        Estate estate = new Estate();

        CatalogFiles found = CatalogFiles.discover(root);
        for (String[] folder : found.unlistable) {
            // Deliberate difference 20: the reference implementation passes such a folder over in silence.
            estate.recordError(rpdId, Coverage.CATALOG, ExtractionError.UNREADABLE, folder[0], "Directory", folder[1]);
        }
        if (found.files.isEmpty()) {
            estate.recordError(rpdId, Coverage.CATALOG, ExtractionError.UNREADABLE, root.toString(), "Catalog",
                    "the declared catalog directory holds no files. Extraction continues; the manifest records "
                            + "that the catalog half was present and empty, which is not the same as absent.");
            return estate;
        }

        Set<String> handled = new LinkedHashSet<>();
        for (CatalogObjectReader reader : readers) {
            handled.add(reader.objectType());
        }

        // From every file, not only those that are XML: a reference may point at any of them. Built from
        // the names alone, so it is complete before the first object is read.
        PathIndex index = new PathIndex();
        for (CatalogFile file : found.files) {
            index.addObject(file.decoded);
        }
        CatalogContext context = new CatalogContext(rpdId, estate, new PathResolver(index, rpdId, estate));
        Map<String, CatalogObjectReader> readerOf = new LinkedHashMap<>();
        for (CatalogObjectReader reader : readers) {
            readerOf.putIfAbsent(reader.objectType(), reader);
        }
        ReadAudit.Tally unread = new ReadAudit.Tally(XmlNode::tag,
                (element, attribute) -> CatalogXml.isStyling(attribute), Unparsed.EXCLUDED_BY_SCOPE);

        List<CatalogObject> unmodelled = new ArrayList<>();
        Map<String, Integer> notCatalog = new TreeMap<>(Text.CODE_POINT_ORDER);
        for (CatalogFile file : found.files) {
            CatalogObject object = parse(file, handled, notCatalog, rpdId, estate);
            if (object == null) {
                continue;
            }
            CatalogObjectReader reader = readerOf.get(object.type);
            if (reader == null) {
                unmodelled.add(object);
                continue;
            }
            if (!identify(object, rpdId, estate)) {
                continue;
            }
            reader.read(object, context);
            estate.census(rpdId, Estate.MODELLED_OBJECT, 1);
            // Straight after its reader, so the audit sees all the reads; then the tree is let go.
            unread.add(new SourceTree(rpdId, object.type, object.path, object.root));
        }
        for (Map.Entry<String, Integer> entry : notCatalog.entrySet()) {
            estate.recordGap(rpdId, Unparsed.NOT_A_CATALOG_OBJECT, "File",
                    entry.getKey() + " — present in the tree, never a catalog object", entry.getValue());
        }

        DashboardContent.linkPagesToDashboards(estate, CatalogXml.DASHBOARD_PAGE_REF);
        reportUnmodelled(unmodelled, rpdId, estate);
        unread.report(estate);
        return estate;
    }

    /**
     * Tries one file as XML, once. The tree is kept only for a type that has a reader; for any other type
     * the root element alone is kept, which is all that is reported about it. Returns null for a file that
     * cannot be read (an error row) or is not XML (counted by what it is).
     */
    private static CatalogObject parse(CatalogFile file, Set<String> handled, Map<String, Integer> notCatalog,
            String rpdId, Estate estate) {
        estate.census(rpdId, Estate.FILES_SEEN, 1);
        if (!file.nameClean) {
            // Deliberate difference 21: the name is kept as far as it decodes, and said to be so.
            estate.recordGap(rpdId, Unparsed.UNDECODED_NAME, "File", file.onDisk
                    + " — the name holds an escape that cannot be decoded; read as " + file.decoded, 1);
        }
        byte[] content;
        try {
            content = Files.readAllBytes(file.path);
        } catch (IOException e) {
            estate.recordError(rpdId, Coverage.CATALOG, ExtractionError.UNREADABLE, file.decoded, "File",
                    "cannot read the file: " + e);
            return null;
        }
        XmlNode root;
        try {
            root = XmlTreeReader.parse(content, candidate -> handled.contains(candidate.tag()));
        } catch (XMLStreamException | RuntimeException e) {
            // Not XML, so not a catalog object. Never an error: images, templates and documents are
            // rightful content of the tree. A damaged object cannot be told from one of them.
            notCatalog.merge(ByteClass.classify(content), 1, Integer::sum);
            estate.census(rpdId, Estate.NOT_A_CATALOG_OBJECT, 1);
            return null;
        }
        estate.census(rpdId, Estate.PARSED_AS_XML, 1);
        return new CatalogObject(file.decoded, root);
    }

    /**
     * Gives the object its Row identifier. When its path cannot be part of one (it contains {@code ::} or
     * is only spaces), the object is left out with an {@code invalid_name} error row and the run goes on
     * (deliberate difference 3; the reference implementation stops).
     */
    private static boolean identify(CatalogObject object, String rpdId, Estate estate) {
        try {
            object.rowId = Identifiers.makeId(rpdId, Identifiers.CATALOG, object.path);
            return true;
        } catch (IdentifierException e) {
            estate.recordError(rpdId, Coverage.CATALOG, ExtractionError.INVALID_NAME, object.path, object.type,
                    e.getMessage());
            return false;
        }
    }

    /**
     * One row per object type that no reader handles (the objects given), found by looking at what was
     * parsed and never from a list, with the number of objects and the path of the first one in file order.
     */
    private static void reportUnmodelled(List<CatalogObject> objects, String rpdId, Estate estate) {
        Map<String, Integer> counts = new TreeMap<>(Text.CODE_POINT_ORDER);
        Map<String, String> example = new LinkedHashMap<>();
        for (CatalogObject object : objects) {
            counts.merge(object.type, 1, Integer::sum);
            example.putIfAbsent(object.type, object.path);
            // Not a recorded read: an unmodelled object is reported whole and is not audited.
            estate.recordVersion(rpdId, object.type, object.root.rawAttr(CatalogXml.XML_VERSION));
        }
        for (Map.Entry<String, Integer> entry : counts.entrySet()) {
            int count = entry.getValue();
            estate.recordGap(rpdId, Unparsed.UNMODELLED_TYPE, entry.getKey(),
                    count + " object(s), e.g. " + example.get(entry.getKey()), count);
            estate.countSkipped(rpdId, Coverage.CATALOG, entry.getKey(), count);
            estate.census(rpdId, Estate.UNMODELLED_OBJECT, count);
        }
    }
}
