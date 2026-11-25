package obi2pbi.in.catalog;

import java.util.ArrayList;
import java.util.List;
import obi2pbi.core.Analysis;
import obi2pbi.core.AnalysisColumn;
import obi2pbi.core.AnalysisCriteria;
import obi2pbi.core.AnalysisFilter;
import obi2pbi.core.AnalysisPrompt;
import obi2pbi.core.AnalysisView;
import obi2pbi.in.xml.XmlNode;

/**
 * A report (OBIEE: an analysis) and the logic defined inside it: its criteria blocks, their columns with
 * formulas, their filters, the prompts and the views.
 *
 * <p>Rules that are easy to get wrong:
 * <ul>
 * <li>A report can hold several criteria blocks, and they nest. Columns and filters belong to the nearest
 * block around them ({@link Ownership}); a column outside every block (inside a view, say) is not a
 * column of the report's query and is not captured.</li>
 * <li>A column's formula is the first formula element that belongs to it, at any depth. A second one is
 * left to the audit.</li>
 * <li>A formula is the <b>whole</b> text of its expression element, nested expressions included
 * (deliberate difference 8).</li>
 * <li>Whether a column is a computation is taken from the column element's type and never from the
 * formula text.</li>
 * <li>A view's type is stored in the source's own words. An unknown type is a row, not an error.</li>
 * </ul>
 * Not read, as in the reference implementation: the formulas of a Visual Analyzer project, which is
 * stored as a report but keeps them elsewhere. The audit counts them.
 */
final class AnalysisReader implements CatalogObjectReader {

    @Override
    public String objectType() {
        return CatalogXml.REPORT;
    }

    @Override
    public void read(CatalogObject object, CatalogContext context) {
        XmlNode root = object.root;
        String version = root.attr(CatalogXml.XML_VERSION);

        List<XmlNode> blocks = root.descendants(CatalogXml.CRITERIA);
        Ownership columnsOf = new Ownership(root, CatalogXml.CRITERIA, CatalogXml.COLUMN);
        Ownership filtersOf = new Ownership(root, CatalogXml.CRITERIA, CatalogXml.FILTER);
        Ownership formulasOf = new Ownership(root, CatalogXml.COLUMN, CatalogXml.COLUMN_FORMULA);

        int columns = 0;
        for (int order = 0; order < blocks.size(); order++) {
            XmlNode block = blocks.get(order);
            String criteriaId = object.rowId + "#criteria" + order;
            // As written, quotes included. It is matched to the repository in report_lineage, not here.
            context.estate.analysisCriteria.add(new AnalysisCriteria(criteriaId, context.rpdId, object.rowId,
                    order, block.attr("subjectArea")));
            columns += readColumns(columnsOf.of(block), formulasOf, criteriaId, order, object, context);
            FilterExpressions.read(filtersOf.of(block), object.rowId + "#f" + order + ".",
                    AnalysisFilter.SCOPE_ANALYSIS, object, context);
        }

        readPrompts(object, context);
        readViews(object, context);

        context.estate.analyses.add(new Analysis(object.rowId, context.rpdId, CatalogNames.nameOf(object.path),
                object.path, version, blocks.size(), columns));
        context.objectRead(object, version);
    }

    private static int readColumns(List<XmlNode> columns, Ownership formulasOf, String criteriaId,
            int criteriaOrder, CatalogObject object, CatalogContext context) {
        for (int order = 0; order < columns.size(); order++) {
            XmlNode column = columns.get(order);
            String construct = "";
            String[] formula = {"", ""};
            List<XmlNode> held = formulasOf.of(column);
            if (!held.isEmpty()) {
                construct = CatalogXml.COLUMN_FORMULA;
                formula = expression(held.get(0));
            }
            String columnId = column.attr("columnID");
            String heading = captionText(column.firstChild(CatalogXml.COLUMN_HEADING));
            String tableHeading = captionText(column.firstChild(CatalogXml.TABLE_HEADING));
            String columnType = column.attr(CatalogXml.XSI_TYPE);
            boolean usesEval = false;
            for (XmlNode item : held) {
                if (FilterExpressions.usesEval(item)) {
                    usesEval = true;
                    break;
                }
            }
            context.estate.analysisColumns.add(new AnalysisColumn(
                    object.rowId + "#c" + criteriaOrder + "." + order, context.rpdId, object.rowId, criteriaId,
                    order, columnId, heading, tableHeading, construct, formula[0], formula[1],
                    !columnType.isEmpty() && !columnType.equals(CatalogXml.REGULAR_COLUMN), usesEval));
        }
        return columns.size();
    }

    /**
     * The type and the whole text of the expression directly inside this element; two empty strings when
     * the element is absent or holds no expression. Character for character: nothing is trimmed.
     */
    static String[] expression(XmlNode container) {
        XmlNode expr = container == null ? null : container.firstChild(CatalogXml.EXPR);
        if (expr == null) {
            return new String[] {"", ""};
        }
        return new String[] {expr.attr(CatalogXml.XSI_TYPE), expr.deepText()};
    }

    /**
     * The text of the first caption text directly inside a caption directly inside this element; empty
     * when there is none. Present only where the author replaced the default heading.
     */
    private static String captionText(XmlNode element) {
        if (element == null) {
            return "";
        }
        for (XmlNode caption : element.children()) {
            if (!caption.is(CatalogXml.CAPTION)) {
                continue;
            }
            for (XmlNode text : caption.children()) {
                if (text.is(CatalogXml.CAPTION_TEXT)) {
                    return text.seek().text();
                }
            }
        }
        return "";
    }

    /**
     * The prompts inside the report. An element with no type is a plain text parameter, not a prompt: it
     * is skipped, and still uses up its number.
     */
    private static void readPrompts(CatalogObject object, CatalogContext context) {
        List<XmlNode> prompts = object.root.below(CatalogXml.PROMPT);
        for (int order = 0; order < prompts.size(); order++) {
            XmlNode prompt = prompts.get(order);
            String type = prompt.attr(CatalogXml.XSI_TYPE);
            if (type.isEmpty()) {
                continue;
            }
            String[] formula = expression(prompt.firstChild(CatalogXml.FORMULA));
            XmlNode operator = prompt.firstChild(CatalogXml.PROMPT_OPERATOR);
            XmlNode control = prompt.firstChild(CatalogXml.PROMPT_UI_CONTROL);
            // A prompt that sets a variable is more than a filter: a formula elsewhere may read the variable.
            List<String> variables = new ArrayList<>();
            for (XmlNode item : prompt.below(CatalogXml.SET_PROMPT_VARIABLE)) {
                String variable = item.attr("variableFormula");
                if (!variable.isEmpty()) {
                    variables.add(variable);
                }
            }
            context.estate.analysisPrompts.add(new AnalysisPrompt(object.rowId + "#p" + order, context.rpdId,
                    object.rowId, order, type, formula[0], formula[1], String.join(" | ", variables),
                    operator == null ? "" : operator.attr("op"),
                    control == null ? "" : control.attr(CatalogXml.XSI_TYPE)));
        }
    }

    /**
     * Every view inside the report, at any depth. The two counts come from structure the source states,
     * not from a list of type names: a view that arranges others names them in its cells, and a view that
     * shows data binds columns.
     */
    private static void readViews(CatalogObject object, CatalogContext context) {
        for (XmlNode container : object.root.descendants(CatalogXml.VIEWS)) {
            // Read only so that the audit does not report it.
            container.attr("currentView");
        }
        List<XmlNode> views = object.root.descendants(CatalogXml.VIEW);
        for (int order = 0; order < views.size(); order++) {
            XmlNode view = views.get(order);
            int references = 0;
            for (XmlNode cell : view.descendants(CatalogXml.CV_CELL)) {
                if (!cell.attr("viewName").isEmpty()) {
                    references++;
                }
            }
            // Every element below the view is asked: the binding sits at a different depth in a pivot, a
            // chart and a trellis.
            int bound = 0;
            for (XmlNode element : view.subtree()) {
                if (element != view && !element.attr("columnID").isEmpty()) {
                    bound++;
                }
            }
            XmlNode display = view.firstChild(CatalogXml.DISPLAY);
            String declared = view.attr(CatalogXml.XSI_TYPE);
            context.estate.analysisViews.add(new AnalysisView(object.rowId + "#view" + order, context.rpdId,
                    object.rowId, order, declared.isEmpty() ? CatalogXml.NO_VIEW_TYPE : declared,
                    view.attr("name"), references, bound,
                    display == null ? "" : display.attr("type"),
                    display == null ? "" : display.attr("subtype")));
        }
        if (!views.isEmpty()) {
            context.partsRead(CatalogXml.VIEW_IN_ANALYSIS, views.size());
        }
    }
}
