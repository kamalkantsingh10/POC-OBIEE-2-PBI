package obi2pbi.core;

import java.util.List;

/**
 * A subject area: the group of presentation tables a report author picks from. Not the Web Catalog, which
 * is the store of reports, though the source's name for a subject area suggests it.
 */
public final class SubjectArea implements Row {

    public final String rowId;
    public final String rpdId;
    public final String name;
    public final String businessModel;
    /** The logical column used as the fact when a query selects only dimension columns, or empty. */
    public final String implicitFactColumnId;
    public final String isAutoAggregate;
    /** Other names the subject area answers to, in source order. */
    public final List<String> aliases;

    public SubjectArea(String rowId, String rpdId, String name, String businessModel,
            String implicitFactColumnId, String isAutoAggregate, List<String> aliases) {
        this.rowId = rowId;
        this.rpdId = rpdId;
        this.name = name;
        this.businessModel = businessModel;
        this.implicitFactColumnId = implicitFactColumnId;
        this.isAutoAggregate = isAutoAggregate;
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
