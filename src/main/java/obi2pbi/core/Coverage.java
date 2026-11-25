package obi2pbi.core;

/** How many objects of one type were read and how many were skipped, for one estate and one store. */
public final class Coverage {

    /** The store an object type belongs to: the repository. */
    public static final String REPOSITORY = "rpd";
    /** The store an object type belongs to: the Web Catalog. */
    public static final String CATALOG = "cat";

    public final String rpdId;
    public final String layer;
    public final String objectType;
    private int parsed;
    private int skipped;

    public Coverage(String rpdId, String layer, String objectType) {
        this.rpdId = rpdId;
        this.layer = layer;
        this.objectType = objectType;
    }

    public int parsed() {
        return parsed;
    }

    public int skipped() {
        return skipped;
    }

    void add(int parsedMore, int skippedMore) {
        parsed += parsedMore;
        skipped += skippedMore;
    }
}
