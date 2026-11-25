package obi2pbi.in.mds;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.xml.stream.XMLStreamException;
import obi2pbi.core.Coverage;
import obi2pbi.core.Estate;
import obi2pbi.core.ExtractionError;
import obi2pbi.core.Lineage;
import obi2pbi.core.SecurityRule;
import obi2pbi.core.Unparsed;
import obi2pbi.in.xml.ReadAudit;
import obi2pbi.in.xml.SourceTree;
import obi2pbi.in.xml.XmlNode;
import obi2pbi.in.xml.XmlTreeReader;

/**
 * Reads one repository export (MDS XML) into an {@link Estate}.
 *
 * <p>Order of work: find the files; parse each once and keep the trees; index every element; run every
 * reader's first phase, then every reader's second phase; work out source counts, lineage and row filters from the rows;
 * audit what was not read; count the rest.
 * A file that cannot be read or parsed gets an error row and the run goes on.
 */
public final class RepositoryReader {

    /**
     * The readers, in the order their first phase runs. <b>To read a new object type, add its reader
     * here.</b> The set of handled types is taken from this list; there is no second list to keep in step.
     */
    private static final List<ObjectReader> READERS = List.of(
            new PhysicalTableReader(),
            new ComplexJoinReader(),
            new LogicalTableReader(),
            new PresentationCatalogReader(),
            new PresentationTableReader(),
            new DimensionReader(),
            new PresentationHierarchyReader(),
            new LogicalTableSourceReader(),
            new LogicalComplexJoinReader(),
            new InitBlockReader(),
            new VariableReader(),
            new GroupReader(),
            new PrivilegePackageReader(),
            new ObjectPrivilegeReader());

    /** Types that give names and containers through the index and have no rows of their own. */
    private static final Set<String> INDEX_ONLY = Set.of("Database", "Schema", "ConnectionPool", "BusinessModel");

    private RepositoryReader() {
    }

    public static Estate load(Path root, String rpdId) throws RepositoryLayoutException {
        return load(root, rpdId, READERS);
    }

    /** As {@link #load(Path, String)} with a chosen set of readers; tests use it to show the audit at work. */
    public static Estate load(Path root, String rpdId, List<ObjectReader> readers)
            throws RepositoryLayoutException {
        RepositoryFiles found = RepositoryFiles.discover(root);
        if (found.files.isEmpty()) {
            throw new RepositoryLayoutException(
                    "no repository files found under " + root + ". Expected "
                            + "oracle/bi/server/base/<ObjectType>/*.xml below it.");
        }

        Estate estate = new Estate();
        for (String path : found.undiscovered) {
            estate.recordGap(rpdId, Unparsed.UNDISCOVERED_FILE, "File", path, 1);
        }
        for (String reason : found.unreadable) {
            estate.recordError(rpdId, Coverage.REPOSITORY, ExtractionError.UNREADABLE, root.toString(),
                    "Directory", reason);
        }

        List<SourceTree> trees = parseAll(found.files, rpdId, estate);
        ReadContext context = new ReadContext(rpdId, estate, ReferenceIndex.build(trees));

        Set<String> handled = new LinkedHashSet<>();
        for (ObjectReader reader : readers) {
            handled.add(reader.objectType());
            for (SourceTree tree : trees) {
                if (tree.objectType.equals(reader.objectType())) {
                    reader.bind(tree.root, context);
                }
            }
        }
        for (ObjectReader reader : readers) {
            for (SourceTree tree : trees) {
                if (tree.objectType.equals(reader.objectType())) {
                    reader.read(tree.root, context);
                }
            }
        }

        // Worked out from the rows, once all of them exist.
        Lineage.countMappedSources(estate);
        Lineage.build(estate);
        SecurityRule.collectRowFilters(estate);

        // After every reader, so the audit sees all the reads.
        ReadAudit.audit(trees, handled, estate);

        for (ObjectFile file : found.files) {
            if (handled.contains(file.objectType)) {
                continue;
            }
            if (INDEX_ONLY.contains(file.objectType)) {
                estate.countParsed(rpdId, Coverage.REPOSITORY, file.objectType);
            } else {
                estate.recordUnmodelled(rpdId, Coverage.REPOSITORY, file.objectType, file.fileName);
            }
        }
        return estate;
    }

    /** Parses every file once. The trees are kept so that reads from every phase add up on the same nodes. */
    private static List<SourceTree> parseAll(List<ObjectFile> files, String rpdId, Estate estate) {
        List<SourceTree> trees = new ArrayList<>();
        for (ObjectFile file : files) {
            XmlNode root;
            try {
                root = XmlTreeReader.read(file.path);
            } catch (IOException e) {
                estate.recordError(rpdId, Coverage.REPOSITORY, ExtractionError.UNREADABLE, file.fileName,
                        file.objectType, "cannot read the file: " + e);
                continue;
            } catch (XMLStreamException e) {
                estate.recordError(rpdId, Coverage.REPOSITORY, ExtractionError.MALFORMED_XML, file.fileName,
                        file.objectType, String.valueOf(e.getMessage()));
                continue;
            }
            trees.add(new SourceTree(rpdId, file.objectType, file.fileName, root));
        }
        return trees;
    }
}
