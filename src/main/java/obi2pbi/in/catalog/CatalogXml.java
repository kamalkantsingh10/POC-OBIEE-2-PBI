package obi2pbi.in.catalog;

import java.util.List;
import java.util.Locale;
import java.util.Set;
import obi2pbi.in.xml.XmlNode;

/**
 * The names the catalog readers look for. Elements and attributes are always matched by namespace and
 * local name together: {@code saw:reportRef} and {@code sawd:reportRef} are different elements.
 */
final class CatalogXml {

    static final String SAW = "com.siebel.analytics.web/report/v1.1";
    static final String SAWD = "com.siebel.analytics.web/dashboard/v1.1";
    static final String SAWX = "com.siebel.analytics.web/expression/v1.1";
    static final String SAWSF = "com.siebel.analytics.web/savedfilter/v1.1";
    static final String XSI = "http://www.w3.org/2001/XMLSchema-instance";

    /** The attribute that types an element. Its value is compared as written, prefix included. */
    static final String XSI_TYPE = XmlNode.qualified(XSI, "type");
    /** On the root element only. Text, never interpreted. */
    static final String XML_VERSION = "xmlVersion";
    static final String PATH = "path";

    /** Root elements: the object types. */
    static final String DASHBOARD = XmlNode.qualified(SAWD, "dashboard");
    static final String DASHBOARD_PAGE = XmlNode.qualified(SAWD, "dashboardPage");
    static final String REPORT = XmlNode.qualified(SAW, "report");
    static final String SAVED_FILTER = XmlNode.qualified(SAWSF, "savedFilterObject");

    /** References held by a dashboard and by a page. */
    static final String DASHBOARD_PAGE_REF = XmlNode.qualified(SAWD, "dashboardPageRef");
    static final String REPORT_REF = XmlNode.qualified(SAWD, "reportRef");
    static final String GLOBAL_FILTER_VIEW = XmlNode.qualified(SAWD, "globalFilterView");
    static final String SCORECARD_VIEW_REF = XmlNode.qualified(SAWD, "scorecardViewRef");
    static final String FOLDER_VIEW = XmlNode.qualified(SAWD, "folderView");

    /** Inside a report. */
    static final String CRITERIA = XmlNode.qualified(SAW, "criteria");
    static final String COLUMN = XmlNode.qualified(SAW, "column");
    static final String COLUMN_FORMULA = XmlNode.qualified(SAW, "columnFormula");
    static final String COLUMN_HEADING = XmlNode.qualified(SAW, "columnHeading");
    static final String TABLE_HEADING = XmlNode.qualified(SAW, "tableHeading");
    static final String CAPTION = XmlNode.qualified(SAW, "caption");
    static final String CAPTION_TEXT = XmlNode.qualified(SAW, "text");
    static final String FILTER = XmlNode.qualified(SAW, "filter");
    static final String PROMPT = XmlNode.qualified(SAW, "prompt");
    static final String FORMULA = XmlNode.qualified(SAW, "formula");
    static final String PROMPT_OPERATOR = XmlNode.qualified(SAW, "promptOperator");
    static final String PROMPT_UI_CONTROL = XmlNode.qualified(SAW, "promptUIControl");
    static final String SET_PROMPT_VARIABLE = XmlNode.qualified(SAW, "setPromptVariable");
    static final String VIEWS = XmlNode.qualified(SAW, "views");
    static final String VIEW = XmlNode.qualified(SAW, "view");
    static final String CV_CELL = XmlNode.qualified(SAW, "cvCell");
    static final String DISPLAY = XmlNode.qualified(SAW, "display");

    /** An expression: a formula, a filter condition, or one operand of either. */
    static final String EXPR = XmlNode.qualified(SAWX, "expr");

    /** The type of a column that is a plain reference. Any other type makes the column a computation. */
    static final String REGULAR_COLUMN = "saw:regularColumn";
    /** The type of an expression that calls a database function directly ({@code EVALUATE}). */
    static final String EVAL_TYPE = "sawx:eval";
    /**
     * The type that makes an expression a reference to a saved filter. There is no element of that name:
     * the reference is an ordinary expression element with this type and a {@code path}.
     */
    static final String SAVED_FILTER_TYPE = "sawx:savedFilter";
    /** Written as the view type of a view that declares none, so that "none" is not mistaken for "not read". */
    static final String NO_VIEW_TYPE = "(no xsi:type)";
    /** The label under which the views inside reports are counted, apart from standalone view objects. */
    static final String VIEW_IN_ANALYSIS = "saw:view (element, inside an Analysis)";

    /** Parts of an attribute name that make it styling, which is out of scope. */
    private static final List<String> STYLING_MARKERS = List.of(
            "align", "bgcolor", "border", "color", "fmt", "font", "format", "height", "indent", "padding",
            "style", "width", "wraptext");

    /** Names the markers catch that change the data shown, not its appearance. These stay in scope. */
    private static final Set<String> STYLING_EXCEPTIONS = Set.of(
            "fmt", "textFormat", "customFormat", "formatMask", "biFormat", "defaultFormat", "outputFormat",
            "format", "formatOutputType", "renderFormat");

    private CatalogXml() {
    }

    /** Whether an attribute, given by its full name, is styling and so left out by a scope decision. */
    static boolean isStyling(String attribute) {
        String local = attribute.substring(attribute.lastIndexOf('}') + 1);
        if (STYLING_EXCEPTIONS.contains(local)) {
            return false;
        }
        String lowered = local.toLowerCase(Locale.ROOT);
        for (String marker : STYLING_MARKERS) {
            if (lowered.contains(marker)) {
                return true;
            }
        }
        return false;
    }
}
