package obi2pbi.in.catalog;

import java.nio.file.Path;

/** One file found under the catalog root. */
final class CatalogFile {

    final Path path;
    /** The path below the catalog root as it is on disk, segments joined with {@code /}. */
    final String onDisk;
    /** The same path with every segment decoded: the catalog path. */
    final String decoded;
    /** False when a segment holds an escape that could not be decoded. */
    final boolean nameClean;

    CatalogFile(Path path, String onDisk, String decoded, boolean nameClean) {
        this.path = path;
        this.onDisk = onDisk;
        this.decoded = decoded;
        this.nameClean = nameClean;
    }
}
