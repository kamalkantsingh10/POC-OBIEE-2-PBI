package obi2pbi.in.catalog;

import obi2pbi.core.Coverage;
import obi2pbi.core.Estate;
import obi2pbi.core.PathResolution;

/** What a {@link CatalogObjectReader} works with: the estate being filled, its name, and path resolution. */
final class CatalogContext {

    final String rpdId;
    final Estate estate;
    private final PathResolver resolver;

    CatalogContext(String rpdId, Estate estate, PathResolver resolver) {
        this.rpdId = rpdId;
        this.estate = estate;
        this.resolver = resolver;
    }

    /**
     * Resolves one path reference written in an object, expecting it to name an object. An error row is
     * written when it does not resolve, so <b>resolve each reference once</b> and reuse the result.
     */
    PathResolution resolve(String reference, CatalogObject owner, String element) {
        return resolver.resolve(reference, owner.path, element, PathResolution.OBJECT);
    }

    /** As {@link #resolve(String, CatalogObject, String)}, saying what kind of thing the path should name. */
    PathResolution resolve(String reference, CatalogObject owner, String element, String expected) {
        return resolver.resolve(reference, owner.path, element, expected);
    }

    /** Counts one whole object as read, and records the version it declares. */
    void objectRead(CatalogObject object, String version) {
        estate.countParsed(rpdId, Coverage.CATALOG, object.type);
        estate.recordVersion(rpdId, object.type, version);
    }

    /** Counts parts of objects as read under a label of their own. */
    void partsRead(String label, int howMany) {
        estate.countParsed(rpdId, Coverage.CATALOG, label, howMany);
    }
}
