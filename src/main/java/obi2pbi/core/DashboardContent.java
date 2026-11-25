package obi2pbi.core;

import java.util.HashMap;
import java.util.ListIterator;
import java.util.Map;

/**
 * One reference held by a dashboard (to a page) or by a page (to a report, a prompt, a scorecard view or a
 * folder), and how it resolved.
 *
 * <p>{@code reference} is the path exactly as the source wrote it, including anything malformed in it.
 */
public final class DashboardContent implements Row {

    public final String rowId;
    public final String rpdId;
    /** The dashboard or page that holds the reference. */
    public final String containerId;
    /** The full name of the referring element. */
    public final String element;
    public final String reference;
    public final int position;
    /** One of the outcomes in {@link PathResolution}. */
    public final String resolution;
    /** The Row identifier of what the reference points at; empty unless it resolved. */
    public final String targetId;
    /** {@link PathResolution#OBJECT} or {@link PathResolution#FOLDER}; empty unless it resolved. */
    public final String targetKind;
    public final String detail;

    public DashboardContent(String rowId, String rpdId, String containerId, String element, String reference,
            int position, PathResolution resolved) {
        this.rowId = rowId;
        this.rpdId = rpdId;
        this.containerId = containerId;
        this.element = element;
        this.reference = reference;
        this.position = position;
        this.resolution = resolved.outcome;
        this.targetId = resolved.targetId;
        this.targetKind = resolved.targetKind;
        this.detail = resolved.detail;
    }

    @Override
    public String rowId() {
        return rowId;
    }

    @Override
    public String rpdId() {
        return rpdId;
    }

    /**
     * Gives each page its owning dashboard, from the page references that resolved. Run once, after every
     * dashboard and page has been read, because a page is as often read before its dashboard as after.
     *
     * <p>The first reference to a page, in the order the references were read, decides; later ones are
     * ignored. A page no dashboard refers to is left as it is.
     *
     * @param pageReferenceElement the full name of the element by which a dashboard refers to a page
     */
    public static void linkPagesToDashboards(Estate estate, String pageReferenceElement) {
        Map<String, DashboardContent> owner = new HashMap<>();
        for (DashboardContent content : estate.dashboardContents) {
            if (content.element.equals(pageReferenceElement) && !content.targetId.isEmpty()) {
                owner.putIfAbsent(content.targetId, content);
            }
        }
        for (ListIterator<DashboardPage> pages = estate.dashboardPages.listIterator(); pages.hasNext();) {
            DashboardPage page = pages.next();
            DashboardContent first = owner.get(page.rowId);
            if (first != null) {
                pages.set(page.ownedBy(first.containerId, first.position));
            }
        }
    }
}
