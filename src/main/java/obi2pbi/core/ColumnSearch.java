package obi2pbi.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Finds the presentation columns of a Knowledge Base that a business term may mean, by plain text matching
 * (story 3.2: the {@code find-columns} command; no AI). It is how the trace skill looks a term up without
 * opening a Knowledge Base file (Architecture AD-17).
 *
 * <p><b>What is compared.</b> The term's words with the words of each presentation column's name, its
 * logical column's name, the presentation table's and logical table's names, and the logical column's
 * formulas (its derived formula, its aggregation rules and the expressions that map it from the database).
 * Words are compared loosely: letter case and punctuation are ignored ({@code Discnt_Value}, {@code discnt
 * value} and {@code DiscntValue} are the same two words), a plural ending is ignored ({@code offices} is
 * {@code office}), and a short word is taken as an abbreviation of a longer one when it starts with the same
 * letter and its letters occur in the longer word in order ({@code Discnt} for {@code discount}, {@code Rep}
 * for {@code representative}, {@code Mth} for {@code month}), which counts less than the word itself, and the
 * less the more letters it leaves out. Small words ({@code of},
 * {@code the}, {@code vs} ...) are not looked for.
 *
 * <p><b>Ranking.</b> First the columns whose whole name is the term (the column's name, its logical column's
 * name, or the table's name followed by the column's), then those that have all the term's words somewhere,
 * then those that have any of them. Inside each group: a word found in the column's own name counts 3, in a
 * table's name 2, in a formula 1 (an abbreviation a part of that); the higher total first, then the more words
 * found in the column's own name, then the fewer other words the column's name has, then the more words a
 * table's name also has, then the Row identifier. The same Knowledge Base and term always give the
 * same list.
 */
public final class ColumnSearch {

    public static final String WHOLE_NAME = "whole name";
    public static final String ALL_WORDS = "all words";
    public static final String ANY_WORD = "some words";
    public static final String FORMULA = "formula";

    /** Words a term often has that say nothing about a column. */
    static final Set<String> STOP_WORDS = Set.of("a", "an", "and", "at", "by", "for", "from", "in", "is", "of", "on",
            "or", "per", "the", "to", "vs", "versus", "with");

    /** One report column that uses a presentation column (from {@code report_lineage}). */
    public static final class ReportUse {
        public final String analysisColumnId;
        public final String heading;
        public final String analysisPath;

        ReportUse(String analysisColumnId, String heading, String analysisPath) {
            this.analysisColumnId = analysisColumnId;
            this.heading = heading;
            this.analysisPath = analysisPath;
        }
    }

    /** One presentation column found, with what a skill needs to judge it. */
    public static final class Candidate {
        public final String rowId;
        public final String subjectArea;
        public final String presentationTable;
        public final String column;
        public final String logicalColumnId;
        public final String logicalColumn;
        public final String logicalTable;
        public final boolean measure;
        /** The default aggregation's function, for example SUM; empty when there is none. */
        public final String aggregation;
        public final String aggregationRuleId;
        /** The other (not default) aggregation rules, by Row identifier. */
        public final List<String> otherAggregationRuleIds;
        /** The derived formula as the repository shows it (names, not placeholders); empty when mapped from the database. */
        public final String formula;
        public final List<String> formulaRefs;
        public final List<String> physicalColumnIds;
        public final int lineagePaths;
        public final List<ReportUse> reports;
        public String group = "";
        public double score;
        public int wordsInName;
        /** Words of the column's own name that the term does not have: the fewer, the closer the name. */
        public int extraNameWords;
        /** Term words that a table's name also has. */
        public int wordsInTable;
        public final List<String> wordsFound = new ArrayList<>();
        public final List<String> wordsNotFound = new ArrayList<>();
        final Map<String, List<String>> fields = new LinkedHashMap<>();
        final List<String> formulaTexts = new ArrayList<>();
        /** What each of {@link #formulaTexts} is, in the same order. */
        final List<String> formulaLabels = new ArrayList<>();
        /**
         * With {@code --formula-contains}: which formula matched and the text around the match (the whole formula
         * when it is short), runs of white space shown as one space. Empty otherwise.
         */
        public String formulaMatch = "";

        Candidate(String rowId, String subjectArea, String presentationTable, String column, String logicalColumnId,
                String logicalColumn, String logicalTable, boolean measure, String aggregation, String aggregationRuleId,
                List<String> otherAggregationRuleIds, String formula, List<String> formulaRefs, List<String> physicalColumnIds,
                int lineagePaths, List<ReportUse> reports) {
            this.rowId = rowId;
            this.subjectArea = subjectArea;
            this.presentationTable = presentationTable;
            this.column = column;
            this.logicalColumnId = logicalColumnId;
            this.logicalColumn = logicalColumn;
            this.logicalTable = logicalTable;
            this.measure = measure;
            this.aggregation = aggregation;
            this.aggregationRuleId = aggregationRuleId;
            this.otherAggregationRuleIds = otherAggregationRuleIds;
            this.formula = formula;
            this.formulaRefs = formulaRefs;
            this.physicalColumnIds = physicalColumnIds;
            this.lineagePaths = lineagePaths;
            this.reports = reports;
        }

        /** A copy without the results of a search, so that each search returns candidates of its own. */
        Candidate fresh() {
            Candidate copy = new Candidate(rowId, subjectArea, presentationTable, column, logicalColumnId, logicalColumn,
                    logicalTable, measure, aggregation, aggregationRuleId, otherAggregationRuleIds, formula, formulaRefs,
                    physicalColumnIds, lineagePaths, reports);
            copy.fields.putAll(fields);
            copy.formulaTexts.addAll(formulaTexts);
            copy.formulaLabels.addAll(formulaLabels);
            return copy;
        }
    }

    /** The result of a search: every candidate found, ranked; the caller shows as many as it wants. */
    public static final class Result {
        public final List<String> words;
        public final List<Candidate> candidates;
        public final boolean reportTablesPresent;

        Result(List<String> words, List<Candidate> candidates, boolean reportTablesPresent) {
            this.words = words;
            this.candidates = candidates;
            this.reportTablesPresent = reportTablesPresent;
        }
    }

    /** The Knowledge Base tables a search reads. */
    public static final List<String> TABLES = List.of("presentation_subject_areas", "presentation_tables",
            "presentation_columns", "logical_tables", "logical_columns", "aggregation_rules", "lts_column_map", "lineage",
            "report_lineage", "analysis_columns", "analyses");

    private final List<Candidate> all = new ArrayList<>();
    private final boolean reportTablesPresent;

    /** Joins, once, every presentation column with what the search compares and shows. */
    public ColumnSearch(KbData kb) {
        Map<String, String> areaName = names(kb.table("presentation_subject_areas"));
        KbRows presTables = kb.table("presentation_tables");
        Map<String, String[]> presTable = byId(presTables);
        Map<String, String> logicalTableName = names(kb.table("logical_tables"));
        KbRows logical = kb.table("logical_columns");
        Map<String, String[]> logicalById = byId(logical);
        KbRows rules = kb.table("aggregation_rules");
        Map<String, List<String[]>> rulesOf = groupBy(rules, "logical_column_id");
        KbRows maps = kb.table("lts_column_map");
        Map<String, List<String[]>> mapsOf = groupBy(maps, "logical_column_id");
        KbRows lineage = kb.table("lineage");
        Map<String, List<String[]>> lineageOf = groupBy(lineage, "presentation_column_id");
        KbRows reportLineage = kb.table("report_lineage");
        Map<String, List<String[]>> reportsOf = groupBy(reportLineage, "presentation_column_id");
        KbRows analysisColumns = kb.table("analysis_columns");
        Map<String, String[]> analysisColumnById = byId(analysisColumns);
        Map<String, String[]> analysisById = byId(kb.table("analyses"));
        KbRows analyses = kb.table("analyses");
        reportTablesPresent = !analysisColumns.rows.isEmpty();

        KbRows columns = kb.table("presentation_columns");
        for (String[] row : columns.rows) {
            String rowId = columns.get(row, "row_id");
            String[] table = presTable.get(columns.get(row, "table_id"));
            String tableName = table == null ? "" : presTables.get(table, "name");
            String area = table == null ? "" : areaName.getOrDefault(presTables.get(table, "subject_area_id"), "");
            String logicalId = columns.get(row, "logical_column_id");
            String[] l = logicalById.get(logicalId);
            String logicalName = l == null ? "" : logical.get(l, "name");
            String logicalTable = l == null ? "" : logicalTableName.getOrDefault(logical.get(l, "table_id"), "");
            boolean measure = l != null && logical.get(l, "is_measure").equals("true");
            String formula = l == null ? "" : logical.get(l, "derived_expr_text_desc");
            List<String> formulaRefs = l == null ? List.of() : lines(logical.get(l, "derived_expr_refs"));

            String aggregation = "";
            String aggregationRule = "";
            List<String> otherRules = new ArrayList<>();
            List<String> formulaTexts = new ArrayList<>();
            List<String> formulaLabels = new ArrayList<>();
            if (l != null) {
                formulaTexts.add(formula);
                formulaLabels.add("derived formula");
                formulaTexts.add(logical.get(l, "derived_expr_text"));
                formulaLabels.add("derived formula, as a template");
                formulaTexts.add(logical.get(l, "mapping_expr_text_desc"));
                formulaLabels.add("definition kept on the logical column");
            }
            for (String[] rule : rulesOf.getOrDefault(logicalId, List.of())) {
                formulaTexts.add(rules.get(rule, "expr_text_desc"));
                formulaLabels.add("aggregation rule " + Citation.of(rules.get(rule, "row_id")));
                if (rules.get(rule, "is_default").equals("true") && aggregationRule.isEmpty()) {
                    aggregationRule = rules.get(rule, "row_id");
                    aggregation = function(rules.get(rule, "expr_text"));
                } else {
                    otherRules.add(rules.get(rule, "row_id"));
                }
            }
            for (String[] map : mapsOf.getOrDefault(logicalId, List.of())) {
                formulaTexts.add(maps.get(map, "logical_expr_text_desc"));
                formulaLabels.add("mapping " + Citation.of(maps.get(map, "row_id")));
                formulaTexts.add(maps.get(map, "physical_expr_text_desc"));
                formulaLabels.add("mapping from the database " + Citation.of(maps.get(map, "row_id")));
            }
            Set<String> physical = new TreeSet<>(Text.CODE_POINT_ORDER);
            List<String[]> paths = lineageOf.getOrDefault(rowId, List.of());
            for (String[] path : paths) {
                if (!lineage.get(path, "physical_column_id").isEmpty()) {
                    physical.add(lineage.get(path, "physical_column_id"));
                }
            }
            List<ReportUse> reports = new ArrayList<>();
            for (String[] use : reportsOf.getOrDefault(rowId, List.of())) {
                String columnId = reportLineage.get(use, "analysis_column_id");
                String[] ac = analysisColumnById.get(columnId);
                String[] analysis = analysisById.get(reportLineage.get(use, "analysis_id"));
                reports.add(new ReportUse(columnId, ac == null ? "" : analysisColumns.get(ac, "heading"),
                        analysis == null ? "" : analyses.get(analysis, "catalog_path")));
            }
            reports.sort((a, b) -> Text.compareCodePoints(a.analysisColumnId, b.analysisColumnId));

            Candidate candidate = new Candidate(rowId, area, tableName, columns.get(row, "name"), logicalId, logicalName,
                    logicalTable, measure, aggregation, aggregationRule, otherRules, formula, formulaRefs,
                    new ArrayList<>(physical), paths.size(), reports);
            candidate.fields.put("name", words(columns.get(row, "name")));
            candidate.fields.put("logical", words(logicalName));
            candidate.fields.put("table", words(tableName));
            candidate.fields.put("logical_table", words(logicalTable));
            List<String> formulaWords = new ArrayList<>();
            for (String text : formulaTexts) {
                formulaWords.addAll(words(text));
            }
            candidate.fields.put("formula", formulaWords);
            for (int i = 0; i < formulaTexts.size(); i++) {
                if (!formulaTexts.get(i).isEmpty()) {
                    candidate.formulaTexts.add(formulaTexts.get(i));
                    candidate.formulaLabels.add(formulaLabels.get(i));
                }
            }
            all.add(candidate);
        }
    }

    /** The subject areas, by name, as the Knowledge Base has them. */
    public static List<String> subjectAreas(KbData kb) {
        List<String> names = new ArrayList<>();
        KbRows areas = kb.table("presentation_subject_areas");
        for (String[] row : areas.rows) {
            names.add(areas.get(row, "name"));
        }
        names.sort(Text.CODE_POINT_ORDER);
        return names;
    }

    /**
     * Searches.
     *
     * @param term            the words to look for; may be empty when {@code formulaContains} is given
     * @param subjectArea     a subject area's exact name, or empty for all
     * @param formulaContains text one of the column's formulas must contain (letter case and runs of white space
     *                        ignored), or empty
     */
    public Result search(String term, String subjectArea, String formulaContains) {
        List<String> termWords = new ArrayList<>();
        for (String word : words(term)) {
            if (!STOP_WORDS.contains(word)) {
                termWords.add(word);
            }
        }
        if (termWords.isEmpty()) {
            termWords.addAll(words(term));
        }
        String contains = squeeze(formulaContains);
        List<Candidate> found = new ArrayList<>();
        for (Candidate known : all) {
            if (!subjectArea.isEmpty() && !known.subjectArea.equals(subjectArea)) {
                continue;
            }
            Candidate candidate = known.fresh();
            if (!contains.isEmpty()) {
                for (int i = 0; i < candidate.formulaTexts.size() && candidate.formulaMatch.isEmpty(); i++) {
                    String text = candidate.formulaTexts.get(i);
                    if (squeeze(text).contains(contains)) {
                        candidate.formulaMatch = candidate.formulaLabels.get(i) + ": " + excerpt(text, contains);
                    }
                }
                if (candidate.formulaMatch.isEmpty()) {
                    continue;
                }
            }
            if (termWords.isEmpty()) {
                candidate.group = FORMULA;
                found.add(candidate);
                continue;
            }
            for (String word : termWords) {
                double best = 0;
                boolean inName = false;
                boolean inTable = false;
                for (Map.Entry<String, List<String>> field : candidate.fields.entrySet()) {
                    int weight = field.getKey().equals("name") || field.getKey().equals("logical") ? 3
                            : field.getKey().equals("formula") ? 1 : 2;
                    double quality = 0;
                    for (String other : field.getValue()) {
                        quality = Math.max(quality, likeness(word, other));
                    }
                    if (quality * weight > best) {
                        best = quality * weight;
                    }
                    inName |= weight == 3 && quality > 0;
                    if (weight == 2 && quality > 0) {
                        inTable = true;
                    }
                }
                if (best > 0) {
                    candidate.wordsFound.add(word);
                    candidate.score += best;
                    candidate.wordsInName += inName ? 1 : 0;
                    candidate.wordsInTable += inTable ? 1 : 0;
                } else {
                    candidate.wordsNotFound.add(word);
                }
            }
            if (candidate.wordsFound.isEmpty()) {
                continue;
            }
            for (String own : candidate.fields.get("name")) {
                if (termWords.stream().noneMatch(word -> likeness(word, own) > 0)) {
                    candidate.extraNameWords++;
                }
            }
            if (isWholeName(termWords, candidate)) {
                candidate.group = WHOLE_NAME;
            } else if (candidate.wordsNotFound.isEmpty()) {
                candidate.group = ALL_WORDS;
            } else {
                candidate.group = ANY_WORD;
            }
            found.add(candidate);
        }
        found.sort((a, b) -> {
            int byGroup = Integer.compare(rank(a.group), rank(b.group));
            if (byGroup != 0) {
                return byGroup;
            }
            int byScore = Double.compare(b.score, a.score);
            if (byScore != 0) {
                return byScore;
            }
            int byName = Integer.compare(b.wordsInName, a.wordsInName);
            if (byName != 0) {
                return byName;
            }
            int byExtra = Integer.compare(a.extraNameWords, b.extraNameWords);
            if (byExtra != 0) {
                return byExtra;
            }
            int byTable = Integer.compare(b.wordsInTable, a.wordsInTable);
            return byTable != 0 ? byTable : Text.compareCodePoints(a.rowId, b.rowId);
        });
        return new Result(termWords, found, reportTablesPresent);
    }

    private static int rank(String group) {
        switch (group) {
            case WHOLE_NAME:
                return 0;
            case ALL_WORDS:
                return 1;
            case ANY_WORD:
                return 2;
            default:
                return 3;
        }
    }

    private static boolean isWholeName(List<String> termWords, Candidate candidate) {
        List<String> name = stems(candidate.fields.get("name"));
        List<String> logicalName = stems(candidate.fields.get("logical"));
        List<String> term = stems(termWords);
        List<String> qualified = stems(candidate.fields.get("table"));
        qualified.addAll(name);
        return term.equals(name) || term.equals(logicalName) || term.equals(qualified);
    }

    // ------------------------------------------------------------------ words

    /**
     * The words of a text: split where a small letter is followed by a capital, and at every character that is
     * not a letter or digit; lower case.
     */
    public static List<String> words(String text) {
        List<String> words = new ArrayList<>();
        if (text == null || text.isEmpty()) {
            return words;
        }
        String split = text.replaceAll("(\\p{Ll})(\\p{Lu})", "$1 $2").toLowerCase(Locale.ROOT);
        for (String word : split.split("[^\\p{L}\\p{N}]+")) {
            if (!word.isEmpty()) {
                words.add(word);
            }
        }
        return words;
    }

    /** A word without its plural ending: {@code offices} to {@code office}, {@code categories} to {@code category}. */
    public static String stem(String word) {
        if (word.length() > 4 && word.endsWith("ies")) {
            return word.substring(0, word.length() - 3) + "y";
        }
        if (word.length() > 3 && word.endsWith("s") && !word.endsWith("ss")) {
            return word.substring(0, word.length() - 1);
        }
        return word;
    }

    private static List<String> stems(List<String> words) {
        List<String> stems = new ArrayList<>();
        for (String word : words) {
            stems.add(stem(word));
        }
        return stems;
    }

    /**
     * 1 when two words are the same (plural endings ignored); between 0.25 and 0.75 when the shorter, of at
     * least three letters, is an abbreviation of the longer (same first letter, its letters in the longer word in
     * order): 0.25 plus half the share of the longer word's letters it keeps, so {@code Discnt} is closer to
     * {@code discount} than {@code Dsc} is; else 0.
     */
    public static double likeness(String a, String b) {
        String x = stem(a);
        String y = stem(b);
        if (x.equals(y)) {
            return 1;
        }
        String shorter = x.length() <= y.length() ? x : y;
        String longer = shorter == x ? y : x;
        if (shorter.length() < 3 || longer.length() < 4 || shorter.charAt(0) != longer.charAt(0)
                || !shorter.chars().allMatch(Character::isLetter)) {
            return 0;
        }
        int at = 0;
        for (int i = 0; i < longer.length() && at < shorter.length(); i++) {
            if (longer.charAt(i) == shorter.charAt(at)) {
                at++;
            }
        }
        // An abbreviation counts less than the word itself, and less the more letters it leaves out.
        return at == shorter.length() ? 0.25 + 0.5 * shorter.length() / longer.length() : 0;
    }

    /** Longer than this, a formula is shown as the part around the match. */
    static final int WHOLE_FORMULA = 160;
    private static final int AROUND = 60;

    /**
     * The formula with runs of white space as one space; when longer than {@link #WHOLE_FORMULA}, only the match
     * and up to {@code 60} characters on each side, with "..." where text was left out.
     */
    public static String excerpt(String text, String squeezedMatch) {
        String flat = text.replaceAll("\\s+", " ").trim();
        if (flat.length() <= WHOLE_FORMULA) {
            return flat;
        }
        int at = flat.toLowerCase(Locale.ROOT).indexOf(squeezedMatch);
        if (at < 0) {
            return flat.substring(0, WHOLE_FORMULA) + " ...";
        }
        int from = Math.max(0, at - AROUND);
        int to = Math.min(flat.length(), at + squeezedMatch.length() + AROUND);
        return (from > 0 ? "... " : "") + flat.substring(from, to) + (to < flat.length() ? " ..." : "");
    }

    private static String squeeze(String text) {
        return text == null ? "" : text.toLowerCase(Locale.ROOT).replaceAll("\\s+", " ").trim();
    }

    /** The function a rule applies, for example {@code SUM} for {@code  SUM("%1")}; the text trimmed otherwise. */
    static String function(String rule) {
        String trimmed = rule.trim();
        int open = trimmed.indexOf('(');
        String head = open > 0 ? trimmed.substring(0, open).trim() : "";
        return head.matches("[A-Za-z_][A-Za-z_0-9 ]*") ? head : trimmed;
    }

    // ------------------------------------------------------------------ joining

    private static Map<String, String[]> byId(KbRows table) {
        Map<String, String[]> rows = new HashMap<>();
        for (String[] row : table.rows) {
            rows.putIfAbsent(table.get(row, "row_id"), row);
        }
        return rows;
    }

    private static Map<String, String> names(KbRows table) {
        Map<String, String> names = new HashMap<>();
        for (String[] row : table.rows) {
            names.putIfAbsent(table.get(row, "row_id"), table.get(row, "name"));
        }
        return names;
    }

    private static Map<String, List<String[]>> groupBy(KbRows table, String column) {
        Map<String, List<String[]>> groups = new HashMap<>();
        for (String[] row : table.rows) {
            groups.computeIfAbsent(table.get(row, column), k -> new ArrayList<>()).add(row);
        }
        return groups;
    }

    private static List<String> lines(String text) {
        Set<String> lines = new LinkedHashSet<>();
        for (String line : text.split("\n")) {
            if (!line.isEmpty()) {
                lines.add(line);
            }
        }
        return new ArrayList<>(lines);
    }
}
