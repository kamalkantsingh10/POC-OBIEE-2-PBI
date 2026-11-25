package obi2pbi.in.mds;

import java.util.HashSet;
import java.util.Set;
import obi2pbi.core.Coverage;
import obi2pbi.core.Estate;
import obi2pbi.core.ExtractionError;
import obi2pbi.core.IdentifierException;
import obi2pbi.core.Identifiers;
import obi2pbi.in.xml.XmlNode;

/** What an {@link ObjectReader} works with: the estate being filled, its name, and the reference index. */
public final class ReadContext {

    public final String rpdId;
    public final Estate estate;
    public final ReferenceIndex index;
    /**
     * Values withheld as credentials in this run, without surrounding white space. Held in memory only, so
     * that the same value can be withheld wherever else it is a variable's value. <b>Never write an entry
     * of this set anywhere: not to a table, an error, a message or a log.</b>
     */
    public final Set<String> withheldValues = new HashSet<>();

    public ReadContext(String rpdId, Estate estate, ReferenceIndex index) {
        this.rpdId = rpdId;
        this.estate = estate;
        this.index = index;
    }

    /** Counts one object, or one part of an object, as read. */
    public void parsed(String objectType) {
        estate.countParsed(rpdId, Coverage.REPOSITORY, objectType);
    }

    public void error(String kind, String sourceId, String objectType, String reason) {
        estate.recordError(rpdId, Coverage.REPOSITORY, kind, sourceId, objectType, reason);
    }

    /** The element's {@code mdsid} for an error row, or {@code ?} when it has none. Recorded as read. */
    public static String sourceIdOf(XmlNode node) {
        return node.attr("mdsid", "?");
    }

    /**
     * Builds a Row identifier. When a name cannot be part of one (only spaces, or containing {@code ::}),
     * records an {@code invalid_name} error against the object and returns null, so one bad object does not
     * cost the whole run (deliberate difference 3).
     */
    public String makeId(XmlNode source, String objectType, String layer, String... parts) {
        try {
            return Identifiers.makeId(rpdId, layer, parts);
        } catch (IdentifierException e) {
            error(ExtractionError.INVALID_NAME, sourceIdOf(source), objectType, e.getMessage());
            return null;
        }
    }
}
