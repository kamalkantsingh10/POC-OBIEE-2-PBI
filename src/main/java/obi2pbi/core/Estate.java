package obi2pbi.core;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Everything read from one estate. Plain data: no files, no XML, no source identifiers (Architecture AD-6).
 *
 * <p>Each reader fills an Estate of its own and the command merges them. Sharing one Estate between readers
 * would let one reader's closing steps run over the other's rows.
 */
public final class Estate {

    public final List<PhysicalTable> physicalTables = new ArrayList<>();
    public final List<PhysicalTableDefinition> physicalTableDefinitions = new ArrayList<>();
    public final List<PhysicalColumn> physicalColumns = new ArrayList<>();
    /** Foreign keys and complex joins, told apart by their kind. */
    public final List<PhysicalJoin> physicalJoins = new ArrayList<>();
    /** The keys declared on physical tables (deliberate difference 28). */
    public final List<PhysicalKey> physicalKeys = new ArrayList<>();

    public final List<LogicalTable> logicalTables = new ArrayList<>();
    public final List<LogicalColumn> logicalColumns = new ArrayList<>();
    public final List<AggregationRule> aggregationRules = new ArrayList<>();
    public final List<LogicalTableSource> logicalTableSources = new ArrayList<>();
    public final List<ColumnMapping> columnMappings = new ArrayList<>();
    /** The physical tables each table source is built on, and the joins it makes between them (difference 34). */
    public final List<SourceTable> sourceTables = new ArrayList<>();
    public final List<SourceJoin> sourceJoins = new ArrayList<>();
    public final List<BmmJoin> bmmJoins = new ArrayList<>();
    public final List<LogicalDimension> logicalDimensions = new ArrayList<>();
    public final List<LogicalLevel> logicalLevels = new ArrayList<>();
    /** The keys declared on logical tables, and on levels of logical dimensions (differences 29 and 30). */
    public final List<LogicalKey> logicalKeys = new ArrayList<>();
    public final List<LogicalLevelKey> logicalLevelKeys = new ArrayList<>();

    public final List<SubjectArea> subjectAreas = new ArrayList<>();
    public final List<PresentationTable> presentationTables = new ArrayList<>();
    public final List<PresentationColumn> presentationColumns = new ArrayList<>();
    public final List<PresentationHierarchy> presentationHierarchies = new ArrayList<>();
    public final List<PresentationLevel> presentationLevels = new ArrayList<>();
    public final List<PresentationLevelColumn> presentationLevelColumns = new ArrayList<>();

    public final List<Variable> variables = new ArrayList<>();
    public final List<InitBlock> initBlocks = new ArrayList<>();
    /** Privileges, and the row filters added by {@link SecurityRule#collectRowFilters}. */
    public final List<SecurityRule> securityRules = new ArrayList<>();

    /** Web Catalog: dashboards, their pages, and the references each holds. */
    public final List<Dashboard> dashboards = new ArrayList<>();
    public final List<DashboardPage> dashboardPages = new ArrayList<>();
    public final List<DashboardContent> dashboardContents = new ArrayList<>();

    /** Web Catalog: reports with their criteria blocks, columns, views, filters and prompts; saved filters. */
    public final List<Analysis> analyses = new ArrayList<>();
    public final List<AnalysisCriteria> analysisCriteria = new ArrayList<>();
    public final List<AnalysisColumn> analysisColumns = new ArrayList<>();
    public final List<AnalysisView> analysisViews = new ArrayList<>();
    /** Filter expressions of reports and of saved filters, and the dashboard prompts placed on pages. */
    public final List<AnalysisFilter> analysisFilters = new ArrayList<>();
    public final List<AnalysisPrompt> analysisPrompts = new ArrayList<>();
    public final List<SavedFilter> savedFilters = new ArrayList<>();

    /** Built by {@link Lineage#build} from the lists above, after every object has been read. */
    public final List<LineageRow> lineage = new ArrayList<>();
    /** Built by {@link ReportLineage#resolve} once both halves are merged: one row per report column. */
    public final List<ReportLineageRow> reportLineage = new ArrayList<>();

    public final List<Unparsed> unparsed = new ArrayList<>();
    public final List<ExtractionError> errors = new ArrayList<>();

    /** Estate names, sorted. Set by the command; one entry in normal use. */
    public final List<String> rpdIds = new ArrayList<>();
    /** Estate name to the halves supplied, in the order {@code rpd}, {@code catalog}. */
    public final Map<String, List<String>> halvesPresent = new TreeMap<>(Text.CODE_POINT_ORDER);

    private final Map<String, Coverage> coverage = new LinkedHashMap<>();
    /** Estate name, object type, declared version (empty when none): how many catalog objects. */
    private final Map<String[], Integer> catalogVersions = new TreeMap<>(Text.ROW_ORDER);
    /** Estate name, measure: how many input files. Only the catalog reader counts here. */
    private final Map<String[], Integer> inputCensus = new TreeMap<>(Text.ROW_ORDER);

    /** The measures of the input census, as written to {@code coverage_summary}. */
    public static final String FILES_SEEN = "files_seen";
    public static final String PARSED_AS_XML = "parsed_as_xml";
    public static final String NOT_A_CATALOG_OBJECT = "not_a_catalog_object";
    public static final String MODELLED_OBJECT = "modelled_object";
    public static final String UNMODELLED_OBJECT = "unmodelled_object";

    /**
     * Every list of rows that carry a Row identifier, by the reference implementation's list name, in name
     * order. The uniqueness check walks this, so a list added here is checked without further work.
     * <b>A later story that adds a row list adds it here.</b>
     */
    public Map<String, List<? extends Row>> rowLists() {
        Map<String, List<? extends Row>> lists = new TreeMap<>(Text.CODE_POINT_ORDER);
        lists.put("physical_tables", physicalTables);
        lists.put("physical_table_definitions", physicalTableDefinitions);
        lists.put("physical_columns", physicalColumns);
        lists.put("physical_joins", physicalJoins);
        lists.put("logical_tables", logicalTables);
        lists.put("logical_columns", logicalColumns);
        lists.put("aggregation_rules", aggregationRules);
        lists.put("logical_table_sources", logicalTableSources);
        lists.put("column_mappings", columnMappings);
        lists.put("lts_tables", sourceTables);
        lists.put("lts_joins", sourceJoins);
        lists.put("subject_areas", subjectAreas);
        lists.put("presentation_tables", presentationTables);
        lists.put("presentation_columns", presentationColumns);
        lists.put("lineage", lineage);
        lists.put("bmm_joins", bmmJoins);
        lists.put("logical_dimensions", logicalDimensions);
        lists.put("logical_levels", logicalLevels);
        lists.put("physical_keys", physicalKeys);
        lists.put("logical_keys", logicalKeys);
        lists.put("logical_level_keys", logicalLevelKeys);
        lists.put("presentation_hierarchies", presentationHierarchies);
        lists.put("presentation_levels", presentationLevels);
        lists.put("presentation_level_columns", presentationLevelColumns);
        lists.put("variables", variables);
        lists.put("init_blocks", initBlocks);
        lists.put("security_rules", securityRules);
        lists.put("dashboards", dashboards);
        lists.put("dashboard_pages", dashboardPages);
        lists.put("dashboard_contents", dashboardContents);
        lists.put("analyses", analyses);
        lists.put("analysis_criteria", analysisCriteria);
        lists.put("analysis_columns", analysisColumns);
        lists.put("analysis_views", analysisViews);
        lists.put("analysis_filters", analysisFilters);
        lists.put("analysis_prompts", analysisPrompts);
        lists.put("saved_filters", savedFilters);
        lists.put("report_lineage", reportLineage);
        return lists;
    }

    /** Absorbs another Estate's rows and counters. The estate name list is the caller's and is left alone. */
    public void merge(Estate other) {
        physicalTables.addAll(other.physicalTables);
        physicalTableDefinitions.addAll(other.physicalTableDefinitions);
        physicalColumns.addAll(other.physicalColumns);
        physicalJoins.addAll(other.physicalJoins);
        logicalTables.addAll(other.logicalTables);
        logicalColumns.addAll(other.logicalColumns);
        aggregationRules.addAll(other.aggregationRules);
        logicalTableSources.addAll(other.logicalTableSources);
        columnMappings.addAll(other.columnMappings);
        sourceTables.addAll(other.sourceTables);
        sourceJoins.addAll(other.sourceJoins);
        subjectAreas.addAll(other.subjectAreas);
        presentationTables.addAll(other.presentationTables);
        presentationColumns.addAll(other.presentationColumns);
        lineage.addAll(other.lineage);
        bmmJoins.addAll(other.bmmJoins);
        logicalDimensions.addAll(other.logicalDimensions);
        logicalLevels.addAll(other.logicalLevels);
        physicalKeys.addAll(other.physicalKeys);
        logicalKeys.addAll(other.logicalKeys);
        logicalLevelKeys.addAll(other.logicalLevelKeys);
        presentationHierarchies.addAll(other.presentationHierarchies);
        presentationLevels.addAll(other.presentationLevels);
        presentationLevelColumns.addAll(other.presentationLevelColumns);
        variables.addAll(other.variables);
        initBlocks.addAll(other.initBlocks);
        securityRules.addAll(other.securityRules);
        dashboards.addAll(other.dashboards);
        dashboardPages.addAll(other.dashboardPages);
        dashboardContents.addAll(other.dashboardContents);
        analyses.addAll(other.analyses);
        analysisCriteria.addAll(other.analysisCriteria);
        analysisColumns.addAll(other.analysisColumns);
        analysisViews.addAll(other.analysisViews);
        analysisFilters.addAll(other.analysisFilters);
        analysisPrompts.addAll(other.analysisPrompts);
        savedFilters.addAll(other.savedFilters);
        reportLineage.addAll(other.reportLineage);
        unparsed.addAll(other.unparsed);
        errors.addAll(other.errors);
        halvesPresent.putAll(other.halvesPresent);
        for (Coverage theirs : other.coverage.values()) {
            bucket(theirs.rpdId, theirs.layer, theirs.objectType).add(theirs.parsed(), theirs.skipped());
        }
        for (Map.Entry<String[], Integer> theirs : other.catalogVersions.entrySet()) {
            catalogVersions.merge(theirs.getKey(), theirs.getValue(), Integer::sum);
        }
        for (Map.Entry<String[], Integer> theirs : other.inputCensus.entrySet()) {
            inputCensus.merge(theirs.getKey(), theirs.getValue(), Integer::sum);
        }
    }

    /** Counts one catalog object under its type and the version it declares; no version is a value too. */
    public void recordVersion(String rpdId, String objectType, String version) {
        catalogVersions.merge(new String[] {rpdId, objectType, version}, 1, Integer::sum);
    }

    /** Rows of estate name, object type, version (empty when none declared), and the count as text. */
    public List<String[]> catalogVersions() {
        List<String[]> rows = new ArrayList<>();
        for (Map.Entry<String[], Integer> entry : catalogVersions.entrySet()) {
            String[] key = entry.getKey();
            rows.add(new String[] {key[0], key[1], key[2], Integer.toString(entry.getValue())});
        }
        return rows;
    }

    /** Counts input files against one of the census measures. */
    public void census(String rpdId, String measure, int howMany) {
        inputCensus.merge(new String[] {rpdId, measure}, howMany, Integer::sum);
    }

    /** The census count for one measure of one estate; 0 when nothing was counted. */
    public int censusOf(String rpdId, String measure) {
        for (Map.Entry<String[], Integer> entry : inputCensus.entrySet()) {
            if (entry.getKey()[0].equals(rpdId) && entry.getKey()[1].equals(measure)) {
                return entry.getValue();
            }
        }
        return 0;
    }

    /**
     * Rows of estate name, measure, count, files seen, and the count as a whole-number percentage of the
     * files seen ({@code n/a} when no file was seen). A half is rounded to the even number, as the
     * reference implementation does.
     */
    public List<String[]> censusSummary() {
        List<String[]> rows = new ArrayList<>();
        for (Map.Entry<String[], Integer> entry : inputCensus.entrySet()) {
            String rpdId = entry.getKey()[0];
            int count = entry.getValue();
            int total = censusOf(rpdId, FILES_SEEN);
            String percent = total == 0 ? "n/a" : (long) Math.rint(100.0 * count / total) + "%";
            rows.add(new String[] {rpdId, entry.getKey()[1], Integer.toString(count), Integer.toString(total),
                    percent});
        }
        return rows;
    }

    public List<Coverage> coverage() {
        return new ArrayList<>(coverage.values());
    }

    /** The sum of every "parsed" counter. Sub-objects such as columns are counted under their own type. */
    public int parsedTotal() {
        int total = 0;
        for (Coverage c : coverage.values()) {
            total += c.parsed();
        }
        return total;
    }

    public int skippedTotal() {
        int total = 0;
        for (Coverage c : coverage.values()) {
            total += c.skipped();
        }
        return total;
    }

    public void countParsed(String rpdId, String layer, String objectType) {
        bucket(rpdId, layer, objectType).add(1, 0);
    }

    public void countParsed(String rpdId, String layer, String objectType, int howMany) {
        bucket(rpdId, layer, objectType).add(howMany, 0);
    }

    public void countSkipped(String rpdId, String layer, String objectType, int howMany) {
        bucket(rpdId, layer, objectType).add(0, howMany);
    }

    /** Something broke. Always counted as skipped under the error's object type, so coverage stays honest. */
    public void recordError(String rpdId, String layer, String kind, String sourceId, String objectType,
            String reason) {
        errors.add(new ExtractionError(rpdId, kind, sourceId, objectType, reason));
        countSkipped(rpdId, layer, objectType, 1);
    }

    /** A known gap. Not counted as skipped: the object itself was read. */
    public void recordGap(String rpdId, String kind, String objectType, String detail, int occurrences) {
        unparsed.add(new Unparsed(rpdId, kind, objectType, detail, occurrences));
    }

    /** A whole object of a type no reader handles. Counted as skipped. */
    public void recordUnmodelled(String rpdId, String layer, String objectType, String detail) {
        unparsed.add(new Unparsed(rpdId, Unparsed.UNMODELLED_TYPE, objectType, detail, 1));
        countSkipped(rpdId, layer, objectType, 1);
    }

    /**
     * Reports every Row identifier that occurs more than once within one list. Both rows stay; the error
     * says the identifier cannot name one of them. Not counted as skipped, because the objects were read.
     *
     * <p>The check is per list: the same text in two different tables is not reported.
     */
    public void checkIdentifierUniqueness() {
        for (Map.Entry<String, List<? extends Row>> list : rowLists().entrySet()) {
            String table = list.getKey();
            Map<String, String> firstEstate = new LinkedHashMap<>();
            Map<String, Integer> duplicated = new TreeMap<>(Text.CODE_POINT_ORDER);
            for (Row row : list.getValue()) {
                String rowId = row.rowId();
                if (rowId == null || rowId.isEmpty()) {
                    continue;
                }
                if (firstEstate.containsKey(rowId)) {
                    duplicated.merge(rowId, 1, Integer::sum);
                } else {
                    firstEstate.put(rowId, row.rpdId());
                }
            }
            for (Map.Entry<String, Integer> entry : duplicated.entrySet()) {
                int rows = entry.getValue() + 1;
                errors.add(new ExtractionError(
                        firstEstate.get(entry.getKey()),
                        ExtractionError.DUPLICATE_IDENTIFIER,
                        entry.getKey(),
                        table,
                        rows + " rows in " + table + " share this identifier; a Group Spec citing it cannot "
                                + "name which one. The identifier does not carry a distinction the source "
                                + "made."));
            }
        }
    }

    private Coverage bucket(String rpdId, String layer, String objectType) {
        String key = rpdId + '\u0000' + layer + '\u0000' + objectType;
        return coverage.computeIfAbsent(key, k -> new Coverage(rpdId, layer, objectType));
    }
}
