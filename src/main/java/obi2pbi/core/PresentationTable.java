package obi2pbi.core;

import java.util.List;

/** A table of a subject area, as a report author sees it. */
public final class PresentationTable implements Row {

    public final String rowId;
    public final String rpdId;
    public final String subjectAreaId;
    public final String name;
    public final String displayName;
    /** Other names the table answers to, in source order. */
    public final List<String> aliases;

    public PresentationTable(String rowId, String rpdId, String subjectAreaId, String name, String displayName,
            List<String> aliases) {
        this.rowId = rowId;
        this.rpdId = rpdId;
        this.subjectAreaId = subjectAreaId;
        this.name = name;
        this.displayName = displayName;
        this.aliases = List.copyOf(aliases);
    }

    @Override
    public String rowId() {
        return rowId;
    }

    @Override
    public String rpdId() {
        return rpdId;
    }
}
