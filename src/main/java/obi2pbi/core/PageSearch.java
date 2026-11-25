package obi2pbi.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Finds the dashboards, dashboard pages and reports of a Knowledge Base that a name may mean, by plain text
 * matching (the {@code find-pages} command; no AI). It is how the trace skill finds the Row identifier of the
 * page a tab replaces, for {@code fsd_items.csv} (docs/skill-conventions.md, section 10.4).
 *
 * <p>Words are compared as {@link ColumnSearch} compares them (letter case, punctuation and plural endings
 * ignored; an abbreviation counts for less). A word in the object's own name counts 3, in the dashboard's name
 * (for a page) 2, elsewhere in its catalog path 1. First the objects whose whole name is the term (or the
 * dashboard's name followed by the page's), then those with all the words, then those with some; inside a group
 * the higher score, then pages before dashboards before reports, then the Row identifier.
 */
public final class PageSearch {

    public static final String PAGE = "page";
    public static final String DASHBOARD = "dashboard";
    public static final String REPORT = "report";

    /** The tables a search reads. */
    public static final List<String> TABLES = List.of("dashboards", "dashboard_pages", "dashboard_contents", "analyses",
            "analysis_criteria");

    /** A report placed on a page, or a placement whose report is not in the catalog. */
    public static final class Placed {
        public final String position;
        /** The report's Row identifier; empty when the reference did not resolve to a report. */
        public final String analysisId;
        public final String path;
        public final List<String> subjectAreas;

        Placed(String position, String analysisId, String path, List<String> subjectAreas) {
            this.position = position;
            this.analysisId = analysisId;
            this.path = path;
            this.subjectAreas = subjectAreas;
        }
    }

    /** One object found. */
    public static final class Found {
        public final String kind;
        public final String rowId;
        public final String name;
        public final String path;
        /** For a page: its dashboard's Row identifier and name, and its position (from 0); empty otherwise. */
        public final String dashboardId;
        public final String dashboardName;
        public final String position;
        /** For a page: the reports placed on it. For a report: the pages that place it, as {@link Placed#analysisId} empty. */
        public final List<Placed> reports = new ArrayList<>();
        /** For a dashboard: its pages as {Row identifier, name, position}. For a report: the pages placing it. */
        public final List<String[]> pages = new ArrayList<>();
        /** For a report: its subject areas as the report names them. */
        public final List<String> subjectAreas = new ArrayList<>();
        public String group = "";
        public double score;
        public final List<String> wordsFound = new ArrayList<>();
        public final List<String> wordsNotFound = new ArrayList<>();
        final List<String> nameWords;
        final List<String> dashboardWords;
        final List<String> pathWords;

        Found(String kind, String rowId, String name, String path, String dashboardId, String dashboardName,
                String position) {
            this.kind = kind;
            this.rowId = rowId;
            this.name = name;
            this.path = path;
            this.dashboardId = dashboardId;
            this.dashboardName = dashboardName;
            this.position = position;
            this.nameWords = ColumnSearch.words(name);
            this.dashboardWords = ColumnSearch.words(dashboardName);
            this.pathWords = ColumnSearch.words(path);
        }
    }

    private final List<Found> all = new ArrayList<>();

    public PageSearch(KbData kb) {
        KbRows dashboards = kb.table("dashboards");
        KbRows pages = kb.table("dashboard_pages");
        KbRows contents = kb.table("dashboard_contents");
        KbRows analyses = kb.table("analyses");
        KbRows criteria = kb.table("analysis_criteria");
        Map<String, List<String>> areasOf = new HashMap<>();
        for (String[] row : criteria.rows) {
            String area = ReportLineage.stripQuotes(criteria.get(row, "subject_area"));
            List<String> areas = areasOf.computeIfAbsent(criteria.get(row, "analysis_id"), k -> new ArrayList<>());
            if (!area.isEmpty() && !areas.contains(area)) {
                areas.add(area);
            }
        }
        Map<String, String[]> analysisById = new HashMap<>();
        for (String[] row : analyses.rows) {
            analysisById.put(analyses.get(row, "row_id"), row);
        }
        Map<String, String> dashboardName = new HashMap<>();
        for (String[] row : dashboards.rows) {
            dashboardName.put(dashboards.get(row, "row_id"), dashboards.get(row, "name"));
        }
        Map<String, Found> pageById = new HashMap<>();
        Map<String, Found> dashboardById = new HashMap<>();
        for (String[] row : dashboards.rows) {
            Found found = new Found(DASHBOARD, dashboards.get(row, "row_id"), dashboards.get(row, "name"),
                    dashboards.get(row, "catalog_path"), "", "", "");
            dashboardById.put(found.rowId, found);
            all.add(found);
        }
        for (String[] row : pages.rows) {
            String dashboard = pages.get(row, "dashboard_id");
            Found found = new Found(PAGE, pages.get(row, "row_id"), pages.get(row, "name"), pages.get(row, "catalog_path"),
                    dashboard, dashboardName.getOrDefault(dashboard, ""), pages.get(row, "position"));
            pageById.put(found.rowId, found);
            all.add(found);
            Found owner = dashboardById.get(dashboard);
            if (owner != null) {
                owner.pages.add(new String[] {found.rowId, found.name, found.position});
            }
        }
        Map<String, Found> reportById = new HashMap<>();
        for (String[] row : analyses.rows) {
            Found found = new Found(REPORT, analyses.get(row, "row_id"), analyses.get(row, "name"),
                    analyses.get(row, "catalog_path"), "", "", "");
            found.subjectAreas.addAll(areasOf.getOrDefault(found.rowId, List.of()));
            reportById.put(found.rowId, found);
            all.add(found);
        }
        for (String[] row : contents.rows) {
            Found page = pageById.get(contents.get(row, "container_id"));
            if (page == null || !contents.get(row, "element").endsWith("reportRef")) {
                continue;
            }
            String target = contents.get(row, "target_id");
            String[] analysis = analysisById.get(target);
            if (analysis != null) {
                page.reports.add(new Placed(contents.get(row, "position"), target, analyses.get(analysis, "catalog_path"),
                        areasOf.getOrDefault(target, List.of())));
                Found report = reportById.get(target);
                if (report != null) {
                    report.pages.add(new String[] {page.rowId, page.dashboardName + " / " + page.name, page.position});
                }
            } else {
                page.reports.add(new Placed(contents.get(row, "position"), "", contents.get(row, "reference"), List.of()));
            }
        }
    }

    /**
     * Every object the term may mean, ranked; the caller shows as many as it wants. The objects returned carry
     * this search's words and score until the next search.
     */
    public List<Found> search(String term) {
        List<String> words = new ArrayList<>();
        for (String word : ColumnSearch.words(term)) {
            if (!ColumnSearch.STOP_WORDS.contains(word)) {
                words.add(word);
            }
        }
        if (words.isEmpty()) {
            words.addAll(ColumnSearch.words(term));
        }
        List<Found> found = new ArrayList<>();
        for (Found object : all) {
            object.wordsFound.clear();
            object.wordsNotFound.clear();
            object.score = 0;
            for (String word : words) {
                double best = Math.max(3 * best(word, object.nameWords),
                        Math.max(2 * best(word, object.dashboardWords), best(word, object.pathWords)));
                if (best > 0) {
                    object.wordsFound.add(word);
                    object.score += best;
                } else {
                    object.wordsNotFound.add(word);
                }
            }
            if (object.wordsFound.isEmpty()) {
                continue;
            }
            List<String> stems = stems(words);
            List<String> qualified = stems(object.dashboardWords);
            qualified.addAll(stems(object.nameWords));
            if (stems.equals(stems(object.nameWords)) || (!object.dashboardWords.isEmpty() && stems.equals(qualified))) {
                object.group = ColumnSearch.WHOLE_NAME;
            } else {
                object.group = object.wordsNotFound.isEmpty() ? ColumnSearch.ALL_WORDS : ColumnSearch.ANY_WORD;
            }
            found.add(object);
        }
        List<String> groups = List.of(ColumnSearch.WHOLE_NAME, ColumnSearch.ALL_WORDS, ColumnSearch.ANY_WORD);
        List<String> kinds = List.of(PAGE, DASHBOARD, REPORT);
        found.sort((a, b) -> {
            int byGroup = Integer.compare(groups.indexOf(a.group), groups.indexOf(b.group));
            if (byGroup != 0) {
                return byGroup;
            }
            int byScore = Double.compare(b.score, a.score);
            if (byScore != 0) {
                return byScore;
            }
            int byKind = Integer.compare(kinds.indexOf(a.kind), kinds.indexOf(b.kind));
            return byKind != 0 ? byKind : Text.compareCodePoints(a.rowId, b.rowId);
        });
        return found;
    }

    private static double best(String word, List<String> others) {
        double best = 0;
        for (String other : others) {
            best = Math.max(best, ColumnSearch.likeness(word, other));
        }
        return best;
    }

    private static List<String> stems(List<String> words) {
        List<String> stems = new ArrayList<>();
        for (String word : words) {
            stems.add(ColumnSearch.stem(word));
        }
        return stems;
    }
}
