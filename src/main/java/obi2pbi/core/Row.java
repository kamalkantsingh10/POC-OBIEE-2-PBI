package obi2pbi.core;

/** A Knowledge Base row that has a Row identifier. Every such row is covered by the uniqueness check. */
public interface Row {

    String rowId();

    /** The estate name. The column keeps the reference implementation's name, {@code rpd_id}. */
    String rpdId();
}
