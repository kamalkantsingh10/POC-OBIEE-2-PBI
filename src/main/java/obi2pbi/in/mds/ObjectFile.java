package obi2pbi.in.mds;

import java.nio.file.Path;

/** One repository file found by discovery. Its object type is the name of the folder it sits in. */
public final class ObjectFile {

    public final String objectType;
    public final String fileName;
    public final Path path;

    public ObjectFile(String objectType, Path path) {
        this.objectType = objectType;
        this.fileName = path.getFileName().toString();
        this.path = path;
    }
}
