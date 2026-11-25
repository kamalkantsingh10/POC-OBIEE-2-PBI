package obi2pbi;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import obi2pbi.in.kb.CsvTable;

/**
 * The Web Catalog half (story 1.7): discovery, name decoding, classification, path resolution, dashboards,
 * pages and what is placed on them, and the run tables that describe the catalog.
 *
 * <p>Catalog files are written by each test under {@code build/}; the hand-made catalog in
 * {@code reference/} is copied, never edited.
 */
public final class CatalogTest {

    private static final String SAWD = "com.siebel.analytics.web/dashboard/v1.1";
    private static final String D = "{" + SAWD + "}";
    private static final String REPORT = "{com.siebel.analytics.web/report/v1.1}report";
    private static final String SAVED_FILTER = "{com.siebel.analytics.web/savedfilter/v1.1}savedFilterObject";

    private static String dashboard(String... pagePaths) {
        StringBuilder xml = new StringBuilder("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<sawd:dashboard xmlns:sawd=\""
                + SAWD + "\" xmlVersion=\"200810300\">");
        for (String path : pagePaths) {
            xml.append("<sawd:dashboardPageRef path=\"").append(path).append("\" type=\"catalog\" hidden=\"false\"/>");
        }
        return xml.append("</sawd:dashboard>").toString();
    }

    private static String page(String body) {
        return "<sawd:dashboardPage xmlns:sawd=\"" + SAWD + "\" xmlVersion=\"200810300\">" + body + "</sawd:dashboardPage>";
    }

    private static String report() {
        return "<saw:report xmlns:saw=\"com.siebel.analytics.web/report/v1.1\" xmlVersion=\"201201160\"><saw:criteria/></saw:report>";
    }

    private static String value(CsvTable table, String keyColumn, String key, String column) {
        return table.value(TestSupport.row(table, keyColumn, key), column);
    }

    private static List<String> cells(CsvTable table, String[] row, String... columns) {
        List<String> values = new ArrayList<>();
        for (String column : columns) {
            values.add(table.value(row, column));
        }
        return values;
    }

    // ------------------------------------------------------------------ the constructed catalog and the halves

    /** Acceptance: the constructed pair's dashboard, page and placement rows equal its GROUND-TRUTH.md. */
    public void testTheConstructedPairGivesItsGroundTruthRows() throws Exception {
        Path work = TestSupport.work("catalog-pair");
        Path estate = TestSupport.estateFrom(work, "constructed-pair", TestSupport.constructed());
        TestSupport.copyTree(TestSupport.constructedCatalog(), estate.resolve("catalog"));
        Path kb = work.resolve("kb");
        TestSupport.Run run = TestSupport.extract(estate, kb);
        Check.equal(0, run.exit, "errors.csv is empty, so exit 0: " + run.out + run.err);
        Check.contains(run.out, "estate:   constructed-pair (rpd + catalog)", "both halves named");
        Check.that(!run.out.contains("warning"), "no 'catalog not read' warning: " + run.out);
        Check.equal(0, TestSupport.table(kb, "errors").rows.size(), "no errors: every reference has a target");

        String id = "constructed-pair::cat::constructed/_portal/sales overview/";
        CsvTable dashboards = TestSupport.table(kb, "dashboards");
        Check.equal(1, dashboards.rows.size(), "one dashboard");
        Check.equal(List.of(id + "dashboard layout", "constructed-pair", "sales overview",
                "constructed/_portal/sales overview/dashboard layout", "200810300", "1"),
                List.of(dashboards.rows.get(0)), "the dashboard is named by its folder");

        CsvTable pages = TestSupport.table(kb, "dashboard_pages");
        Check.equal(1, pages.rows.size(), "one page");
        Check.equal(List.of(id + "page one", "constructed-pair", id + "dashboard layout", "0", "page one",
                "constructed/_portal/sales overview/page one", "200810300"), List.of(pages.rows.get(0)), "the page, with its dashboard");

        CsvTable contents = TestSupport.table(kb, "dashboard_contents");
        Check.equal(3, contents.rows.size(), "three references");
        String[] columns = {"row_id", "container_id", "position", "element", "reference", "resolution", "target_id", "target_kind", "detail"};
        Check.equal(List.of(id + "dashboard layout#page0", id + "dashboard layout", "0", D + "dashboardPageRef", "Page One",
                "case_normalised", id + "page one", "object", ""), cells(contents, contents.rows.get(0), columns), "the page reference");
        Check.equal(List.of(id + "page one#item0", id + "page one", "0", D + "reportRef", "/shared/Constructed/Revenue by Month",
                "case_normalised", "constructed-pair::cat::constructed/revenue by month", "object", ""),
                cells(contents, contents.rows.get(1), columns), "the report placed on the page");
        Check.equal(List.of(id + "page one#item1", id + "page one", "1", D + "globalFilterView",
                "/shared/Constructed/_filters/Recent Periods", "case_normalised",
                "constructed-pair::cat::constructed/_filters/recent periods", "object", ""),
                cells(contents, contents.rows.get(2), columns), "the dashboard prompt placed on the page");

        // Both halves in one Knowledge Base, and the manifest says so.
        Check.equal(1, TestSupport.table(kb, "presentation_subject_areas").rows.size(), "the repository half is there too");
        CsvTable manifest = TestSupport.table(kb, "manifest");
        Check.equal("rpd catalog", value(manifest, "key", "halves_present:constructed-pair", "value"), "halves");
        Check.contains(value(manifest, "key", "pairing:constructed-pair", "value"), "declared by the Operator: both halves", "pairing");
        Check.equal(List.of(), SchemaTest.valueProblems(kb), "every declared link holds");

        // Stories 1.8 and 1.9: the report and the saved filter are read; GROUND-TRUTH.md gives the counts.
        String report = "constructed-pair::cat::constructed/revenue by month";
        String saved = "constructed-pair::cat::constructed/_filters/recent periods";
        CsvTable analyses = TestSupport.table(kb, "analyses");
        Check.equal(List.of(List.of(report, "constructed-pair", "revenue by month", "constructed/revenue by month", "201201160", "1", "2")),
                List.of(List.of(analyses.rows.get(0))), "analyses: one report, one criteria block, two columns");
        CsvTable criteria = TestSupport.table(kb, "analysis_criteria");
        Check.equal(List.of(report + "#criteria0", "constructed-pair", report, "0", "\"Constructed SA\""),
                List.of(criteria.rows.get(0)), "the subject area keeps its quotes");
        CsvTable reportColumns = TestSupport.table(kb, "analysis_columns");
        Check.equal(2, reportColumns.rows.size(), "two columns");
        Check.equal(List.of(report + "#c0.0", "constructed-pair", report, report + "#criteria0", "0", "c1", "Amount", "",
                "{com.siebel.analytics.web/report/v1.1}columnFormula", "sawx:sqlExpression", "\"Sales Facts\".\"Amount\"", "false", "false"),
                List.of(reportColumns.rows.get(0)), "the first column, formula exactly as written");
        Check.equal("\"Sales Facts\".\"Rolling Amount 3M\"", reportColumns.rows.get(1)[10], "the second formula");
        Check.equal(0, TestSupport.table(kb, "analysis_views").rows.size(), "the report has no views");

        CsvTable filters = TestSupport.table(kb, "analysis_filters");
        String[] filterColumns = {"row_id", "scope", "owner_id", "position", "op", "expr_type", "reference", "target_id",
                "resolution", "uses_eval", "parent_id", "depth"};
        Check.equal(3, TestSupport.rows(filters, "scope", "dashboard").size() + TestSupport.rows(filters, "scope", "analysis").size(),
                "ground truth: three rows, one dashboard-scope and two analysis-scope");
        Check.equal(List.of(id + "page one#gfilter0", "dashboard", id + "page one", "0", "", "", "/shared/Constructed/_filters/Recent Periods",
                saved, "case_normalised", "false", "", "0"),
                cells(filters, TestSupport.row(filters, "row_id", id + "page one#gfilter0"), filterColumns), "the dashboard prompt on the page");
        Check.equal(List.of(report + "#f0.0", "analysis", report, "0", "and", "sawx:logical", "", "", "", "false", "", "0"),
                cells(filters, TestSupport.row(filters, "row_id", report + "#f0.0"), filterColumns), "the report's and-group");
        Check.equal(List.of(report + "#f0.1", "analysis", report, "1", "", "sawx:savedFilter", "/shared/Constructed/_filters/Recent Periods",
                saved, "case_normalised", "false", report + "#f0.0", "1"),
                cells(filters, TestSupport.row(filters, "row_id", report + "#f0.1"), filterColumns), "the reference to the saved filter, resolved");
        // Difference 9: the saved filter's own two expressions are rows too.
        Check.equal(List.of(saved + "#f0", "saved_filter", saved, "0", "and", "sawx:logical", "", "", "", "false", "", "0"),
                cells(filters, TestSupport.row(filters, "row_id", saved + "#f0"), filterColumns), "the saved filter's and-group");
        Check.equal(List.of(saved + "#f1", "saved_filter", saved, "1", "prompted", "sawx:special", "", "", "", "false", saved + "#f0", "1"),
                cells(filters, TestSupport.row(filters, "row_id", saved + "#f1"), filterColumns), "its prompted condition");
        Check.equal(5, filters.rows.size(), "no other rows");

        CsvTable savedFilters = TestSupport.table(kb, "saved_filters");
        Check.equal(1, savedFilters.rows.size(), "one saved filter");
        Check.equal(List.of(saved, "recent periods", "constructed/_filters/recent periods", "", "and", "sawx:logical", "false"),
                cells(savedFilters, savedFilters.rows.get(0), "row_id", "name", "catalog_path", "xml_version", "op", "expr_type", "uses_eval"),
                "the saved filter object, with its outermost expression");

        CsvTable unparsed = TestSupport.table(kb, "unparsed");
        Check.equal(0, TestSupport.rows(unparsed, "kind", "unmodelled_type").size(), "every object type in the pair has a reader");
        Check.equal("1", unparsed.value(TestSupport.row(unparsed, "detail", SAVED_FILTER + "@subjectArea"), "occurrences"),
                "what a reader leaves is audited: the saved filter's own subject area is not read");
        CsvTable coverage = TestSupport.table(kb, "coverage");
        Check.equal(List.of("1", "0"), cells(coverage, TestSupport.row(coverage, "object_type", REPORT), "parsed", "skipped"), "the report is counted as read");
        Check.equal(List.of("1", "0"), cells(coverage, TestSupport.row(coverage, "object_type", SAVED_FILTER), "parsed", "skipped"), "and the saved filter");
    }

    public void testACatalogAloneIsExtractedAndTheManifestSaysWhichHalf() {
        Path work = TestSupport.work("catalog-only");
        Path estate = work.resolve("cat-only");
        TestSupport.catalogFile(estate, "sales/_portal/overview/dashboard+layout", dashboard("page 1"));
        TestSupport.catalogFile(estate, "sales/_portal/overview/page+1", page(""));
        Path kb = work.resolve("kb");
        TestSupport.Run run = TestSupport.extract(estate, kb);
        Check.equal(0, run.exit, run.out + run.err);
        Check.contains(run.out, "estate:   cat-only (catalog)", "the half that was read");
        Check.contains(run.out, "parsed:   2 objects", "a dashboard and a page");
        CsvTable manifest = TestSupport.table(kb, "manifest");
        Check.equal("catalog", value(manifest, "key", "halves_present:cat-only", "value"), "halves");
        Check.equal("not paired: only the catalog half was supplied", value(manifest, "key", "pairing:cat-only", "value"), "pairing");
        Check.equal(0, TestSupport.table(kb, "physical_tables").rows.size(), "repository tables are written, empty");
        Check.equal("exact", value(TestSupport.table(kb, "dashboard_contents"), "reference", "page 1", "resolution"),
                "a reference in the case the disk has resolves exactly");

        CsvTable coverage = TestSupport.table(kb, "coverage");
        Check.equal(List.of("cat-only", "cat", D + "dashboard", "1", "0"), List.of(coverage.rows.get(0)), "coverage, catalog layer");
        Check.equal(List.of("cat-only", "cat", D + "dashboardPage", "1", "0"), List.of(coverage.rows.get(1)), "coverage, catalog layer");
        CsvTable versions = TestSupport.table(kb, "catalog_versions");
        Check.equal(List.of("cat-only", D + "dashboard", "200810300", "1"), List.of(versions.rows.get(0)), "catalog_versions");
    }

    public void testARepositoryAloneHasNoCatalogRowsAndNoWarning() {
        Path work = TestSupport.work("catalog-absent");
        Path kb = work.resolve("kb");
        TestSupport.Run run = TestSupport.extract(TestSupport.estateFrom(work, "testrpd", TestSupport.testRpd()), kb);
        Check.equal(0, run.exit, run.err);
        Check.that(!run.out.contains("warning"), "no warning: " + run.out);
        for (String table : new String[] {"dashboards", "dashboard_pages", "dashboard_contents", "catalog_versions", "coverage_summary"}) {
            Check.equal(0, TestSupport.table(kb, table).rows.size(), table + " is written with no rows");
        }
        Check.equal("not paired: only the rpd half was supplied",
                value(TestSupport.table(kb, "manifest"), "key", "pairing:testrpd", "value"), "pairing");
    }

    public void testAnEmptyCatalogIsPresentAndReportedNotAbsent() {
        Path work = TestSupport.work("catalog-empty");
        Path estate = work.resolve("empty");
        try {
            Files.createDirectories(estate.resolve("catalog/.git"));
        } catch (java.io.IOException e) {
            throw new java.io.UncheckedIOException(e);
        }
        TestSupport.write(estate.resolve("catalog/.git/config"), "[core]");
        Path kb = work.resolve("kb");
        TestSupport.Run run = TestSupport.extract(estate, kb);
        Check.equal(3, run.exit, "one error row: " + run.out + run.err);
        CsvTable errors = TestSupport.table(kb, "errors");
        Check.equal(1, errors.rows.size(), "one error");
        Check.equal(List.of("unreadable", "Catalog"), cells(errors, errors.rows.get(0), "kind", "object_type"), "kind and marker");
        Check.contains(errors.rows.get(0)[4], "the declared catalog directory holds no files", "reason");
        Check.equal("catalog", value(TestSupport.table(kb, "manifest"), "key", "halves_present:empty", "value"), "the half was present");
        Check.equal(0, TestSupport.table(kb, "coverage_summary").rows.size(), "nothing was seen");
    }

    // ------------------------------------------------------------------ discovery and classification

    public void testEveryFileIsTriedAndWhatIsNotXmlIsClassifiedNotFailed() {
        Path work = TestSupport.work("catalog-classify");
        Path estate = work.resolve("mixed");
        TestSupport.catalogFile(estate, "a/_portal/d/dashboard+layout", dashboard());
        TestSupport.catalogBytes(estate, "a/logo%2epng", new byte[] {(byte) 0x89, 'P', 'N', 'G', '\r', '\n', 0x1a, '\n', 0, 0});
        TestSupport.catalogBytes(estate, "a/logo2%2epng", new byte[] {(byte) 0x89, 'P', 'N', 'G', '\r', '\n', 0x1a, '\n'});
        TestSupport.catalogFile(estate, "a/letter%2ertf", "{\\rtf1 hello}");
        TestSupport.catalogFile(estate, "a/notes", "just text");
        TestSupport.catalogFile(estate, "a/damaged", "<sawd:dashboard xmlns:sawd=\"" + SAWD + "\"><unclosed></sawd:dashboard>");
        TestSupport.catalogFile(estate, "a/empty", "");
        TestSupport.catalogFile(estate, ".gitignore", "*.tmp\n");
        TestSupport.catalogFile(estate, ".git/HEAD", "ref: refs/heads/main\n");
        TestSupport.catalogFile(estate, "a/.git/objects/x", "<sawd:dashboard xmlns:sawd=\"" + SAWD + "\"/>");
        Path kb = work.resolve("kb");
        TestSupport.Run run = TestSupport.extract(estate, kb);
        Check.equal(0, run.exit, "a file that is not a catalog object is never an error: " + run.out + run.err);
        Check.equal(0, TestSupport.table(kb, "errors").rows.size(), "no error rows, and so no malformed_xml");
        Check.equal(1, TestSupport.table(kb, "dashboards").rows.size(), "the dashboard under .git is not read");

        CsvTable unparsed = TestSupport.table(kb, "unparsed");
        List<String[]> notObjects = TestSupport.rows(unparsed, "kind", "not_a_catalog_object");
        List<String> lines = new ArrayList<>();
        for (String[] row : notObjects) {
            lines.add(String.join("|", cells(unparsed, row, "object_type", "detail", "occurrences")));
        }
        Check.equal(List.of(
                "File|PNG image — present in the tree, never a catalog object|2",
                "File|RTF template — present in the tree, never a catalog object|1",
                "File|unrecognised binary or text — present in the tree, never a catalog object|4"),
                lines, "one row per kind of file, with a count; .gitignore, plain text, a damaged object and an empty file are 'unrecognised'");

        CsvTable summary = TestSupport.table(kb, "coverage_summary");
        // 8 files: half-to-even gives 12.5 -> 12 and 87.5 -> 88. Rounding halves up would give 13.
        Check.equal(List.of("mixed", "files_seen", "8", "8", "100%"), List.of(summary.rows.get(0)), "files seen: .git is left out, .gitignore is not");
        Check.equal(List.of("mixed", "modelled_object", "1", "8", "12%"), List.of(summary.rows.get(1)), "12.5% rounds to the even 12%");
        Check.equal(List.of("mixed", "not_a_catalog_object", "7", "8", "88%"), List.of(summary.rows.get(2)), "87.5% rounds to the even 88%");
        Check.equal(List.of("mixed", "parsed_as_xml", "1", "8", "12%"), List.of(summary.rows.get(3)), "parsed as XML");
        Check.equal(4, summary.rows.size(), "a measure that counted nothing has no row");
    }

    public void testAnObjectTypeWithNoReaderIsListedOnceWithACount() {
        Path work = TestSupport.work("catalog-unmodelled");
        Path estate = work.resolve("types");
        String kpi = "<sawkpi:kpi xmlns:sawkpi=\"com.siebel.analytics.web/kpi/v1\" xmlVersion=\"201008230\" secret=\"x\"/>";
        TestSupport.catalogFile(estate, "z/kpi+two", kpi);
        TestSupport.catalogFile(estate, "b/kpi+one", kpi);
        TestSupport.catalogFile(estate, "b/action", "<saw:action xmlns:saw=\"com.siebel.analytics.web/report/v1.1\"/>");
        TestSupport.catalogFile(estate, "b/export", "<DATA_DS><ROW/></DATA_DS>");
        Path kb = work.resolve("kb");
        Check.equal(0, TestSupport.extract(estate, kb).exit, "unmodelled types are gaps, not errors");
        CsvTable unparsed = TestSupport.table(kb, "unparsed");
        Check.equal(3, unparsed.rows.size(), "one row per type and nothing from inside the objects: " + TestSupport.column(unparsed, "detail"));
        String kpiType = "{com.siebel.analytics.web/kpi/v1}kpi";
        Check.equal(List.of("unmodelled_type", "2 object(s), e.g. b/kpi one", "2"),
                cells(unparsed, TestSupport.row(unparsed, "object_type", kpiType), "kind", "detail", "occurrences"),
                "count, and the first object in file order as the example");
        Check.equal("1 object(s), e.g. b/export", value(unparsed, "object_type", "DATA_DS", "detail"), "a root with no namespace is its bare name");
        CsvTable coverage = TestSupport.table(kb, "coverage");
        Check.equal(List.of("0", "2"), cells(coverage, TestSupport.row(coverage, "object_type", kpiType), "parsed", "skipped"), "counted as skipped");
        CsvTable versions = TestSupport.table(kb, "catalog_versions");
        Check.equal(List.of("201008230", "2"), cells(versions, TestSupport.row(versions, "object_type", kpiType), "xml_version", "objects"), "version of a type that is not read");
        Check.equal("(none)", value(versions, "object_type", "DATA_DS", "xml_version"), "no version is written as (none)");
        CsvTable summary = TestSupport.table(kb, "coverage_summary");
        Check.equal("4", value(summary, "measure", "unmodelled_object", "count"), "census");
    }

    public void testNamesAreDecodedPlusFirstThenPercentAndNeverThrow() {
        Path work = TestSupport.work("catalog-names");
        Path estate = work.resolve("names");
        TestSupport.catalogFile(estate, "co%6dponents/05%2e+published+reporting/a%2bb", dashboard());
        TestSupport.catalogFile(estate, "caf%c3%a9/_portal/Men%C3%BC/dashboard+layout", dashboard());
        TestSupport.catalogFile(estate, "odd/100%zz", dashboard());
        TestSupport.catalogFile(estate, "odd/t%edtulo/x", dashboard());
        Path kb = work.resolve("kb");
        TestSupport.Run run = TestSupport.extract(estate, kb);
        Check.equal(0, run.exit, "a name that cannot be decoded does not stop the run or count as an error: " + run.out + run.err);
        List<String> paths = TestSupport.column(TestSupport.table(kb, "dashboards"), "catalog_path");
        Check.equal(List.of("café/_portal/Menü/dashboard layout", "components/05. published reporting/a+b",
                "odd/100%zz", "odd/t�tulo/x"), paths,
                "%6d is m, %2e is a dot, + is a space, %2b stays a plus, case is kept, a bad escape is kept as written");
        Check.equal("Menü", value(TestSupport.table(kb, "dashboards"), "catalog_path", paths.get(0), "name"), "an escaped two-byte character");

        CsvTable unparsed = TestSupport.table(kb, "unparsed");
        List<String> reported = new ArrayList<>();
        for (String[] row : TestSupport.rows(unparsed, "kind", "undecoded_name")) {
            reported.add(unparsed.value(row, "detail"));
        }
        Check.equal(List.of(
                "odd/100%zz — the name holds an escape that cannot be decoded; read as odd/100%zz",
                "odd/t%edtulo/x — the name holds an escape that cannot be decoded; read as odd/t�tulo/x"),
                reported, "kept and reported (deliberate difference 21)");
    }

    public void testFileOrderIsBySegmentAndTheOutputNeverDependsOnTheFileSystem() throws Exception {
        Path work = TestSupport.work("catalog-order");
        Path estate = work.resolve("order");
        // Written in an order unlike the sorted one. On disk "a/..." sorts before "a+b/..." segment by
        // segment ("a" < "a+b"), though as whole strings "a+b/" < "a/" ('+' is before '/').
        TestSupport.catalogFile(estate, "a+b/_portal/second/dashboard+layout", dashboard("/shared/pages/shared page"));
        TestSupport.catalogFile(estate, "pages/shared+page", page(""));
        TestSupport.catalogFile(estate, "a/_portal/first/dashboard+layout", dashboard("/shared/pages/missing", "/shared/pages/shared page"));
        Path first = work.resolve("kb1");
        Path second = work.resolve("kb2");
        TestSupport.extract(estate, first);
        TestSupport.extract(estate, second);
        for (String file : TestSupport.fileNames(first)) {
            Check.sameBytes(Files.readAllBytes(first.resolve(file)), Files.readAllBytes(second.resolve(file)), file);
        }
        CsvTable pages = TestSupport.table(first, "dashboard_pages");
        Check.equal(List.of("order::cat::a/_portal/first/dashboard layout", "1"),
                cells(pages, pages.rows.get(0), "dashboard_id", "position"),
                "a page two dashboards refer to belongs to the first in file order, at the place that reference has there");
        Check.equal("2", value(TestSupport.table(first, "dashboards"), "name", "first", "page_count"), "page_count includes the reference that did not resolve");
    }

    /** Deliberate difference 20: a folder that cannot be listed is an error row, not a silent loss. */
    public void testAFolderThatCannotBeListedIsReported() throws Exception {
        Path work = TestSupport.work("catalog-unlistable");
        Path estate = work.resolve("locked");
        TestSupport.catalogFile(estate, "open/_portal/d/dashboard+layout", dashboard());
        Path closed = TestSupport.catalogFile(estate, "closed+folder/page", page("")).getParent();
        try {
            Files.setPosixFilePermissions(closed, java.util.Set.of());
        } catch (UnsupportedOperationException e) {
            throw new SkipException("this file system has no permissions to take away");
        }
        try {
            if (Files.isReadable(closed)) {
                throw new SkipException("the folder can still be listed (running as an administrator)");
            }
            Path kb = work.resolve("kb");
            TestSupport.Run run = TestSupport.extract(estate, kb);
            Check.equal(3, run.exit, "written, with an error row: " + run.out + run.err);
            CsvTable errors = TestSupport.table(kb, "errors");
            Check.equal(List.of("unreadable", "closed+folder", "Directory"),
                    cells(errors, errors.rows.get(0), "kind", "source_id", "object_type"), "the folder is named");
            Check.equal(1, TestSupport.table(kb, "dashboards").rows.size(), "the rest of the catalog is read");
        } finally {
            Files.setPosixFilePermissions(closed, java.nio.file.attribute.PosixFilePermissions.fromString("rwxr-xr-x"));
        }
    }

    // ------------------------------------------------------------------ path resolution

    public void testEachReferenceRecordsHowItResolved() {
        Path work = TestSupport.work("catalog-resolve");
        Path estate = work.resolve("refs");
        TestSupport.catalogFile(estate, "sales/_portal/dash/dashboard+layout", dashboard("page 1", "Page 2", "gone"));
        TestSupport.catalogFile(estate, "sales/_portal/dash/page+1", page(
                "<sawd:folderView path=\"/shared/Sales/Reports\"/>"
                        + "<sawd:scorecardViewRef path=\"/shared/sales/reports\"/>"
                        + "<sawd:globalFilterView path=\"/company_shared/Prompts/It's \\ here\"/>"
                        + "<sawd:globalFilterView path=\"/shared/sales/twin\"/>"
                        + "<sawd:section><sawd:reportRef path=\"/shared/Sales/Reports/X\"/></sawd:section>"
                        + "<sawd:reportRef path=\"/shared/sales/reports/x\"/>"
                        + "<sawd:reportRef path=\"/shared/sales/reports/logo.png\"/>"
                        + "<sawd:reportRef/>"
                        + "<saw:reportRef xmlns:saw=\"com.siebel.analytics.web/report/v1.1\" path=\"/shared/not/read\"/>"));
        TestSupport.catalogFile(estate, "sales/_portal/dash/page+2", page(""));
        TestSupport.catalogFile(estate, "sales/reports/x", report());
        TestSupport.catalogBytes(estate, "sales/reports/logo%2epng", new byte[] {(byte) 0x89, 'P', 'N', 'G', '\r', '\n'});
        TestSupport.catalogFile(estate, "sales/Twin", report());
        TestSupport.catalogFile(estate, "sales/tWin", report());
        Path kb = work.resolve("kb");
        TestSupport.Run run = TestSupport.extract(estate, kb);
        Check.equal(3, run.exit, "unresolved references are errors: " + run.out + run.err);

        CsvTable contents = TestSupport.table(kb, "dashboard_contents");
        String pageId = "refs::cat::sales/_portal/dash/page 1";
        List<String> placed = new ArrayList<>();
        for (String[] row : TestSupport.rows(contents, "container_id", pageId)) {
            placed.add(String.join("|", cells(contents, row, "position", "element", "reference", "resolution", "target_id", "target_kind", "detail")));
        }
        Check.equal(List.of(
                "0|" + D + "reportRef|/shared/Sales/Reports/X|case_normalised|refs::cat::sales/reports/x|object|",
                "1|" + D + "reportRef|/shared/sales/reports/x|exact|refs::cat::sales/reports/x|object|",
                "2|" + D + "reportRef|/shared/sales/reports/logo.png|exact|refs::cat::sales/reports/logo.png|object|",
                "3|" + D + "reportRef||unresolved|||no object or folder at this path",
                "4|" + D + "globalFilterView|/company_shared/Prompts/It's \\ here|unresolved|||names a catalog root this tree does not contain",
                "5|" + D + "globalFilterView|/shared/sales/twin|ambiguous|||two paths differ only in case: sales/Twin | sales/tWin",
                "6|" + D + "scorecardViewRef|/shared/sales/reports|exact|refs::cat::sales/reports|folder|expected a catalog object, resolved to a folder",
                "7|" + D + "folderView|/shared/Sales/Reports|case_normalised|refs::cat::sales/reports|folder|"),
                placed,
                "reports first, then dashboard prompts, scorecard views, folder views, each in document order; a "
                        + "file that is not XML is a rightful target; the report-namespace reportRef is not read");

        List<String[]> pageRefs = TestSupport.rows(contents, "container_id", "refs::cat::sales/_portal/dash/dashboard layout");
        Check.equal(List.of("page 1", "exact", "refs::cat::sales/_portal/dash/page 1"),
                cells(contents, pageRefs.get(0), "reference", "resolution", "target_id"), "relative to the dashboard's folder");
        Check.equal(List.of("Page 2", "case_normalised", "refs::cat::sales/_portal/dash/page 2"),
                cells(contents, pageRefs.get(1), "reference", "resolution", "target_id"), "after folding case");
        Check.equal(List.of("gone", "unresolved", ""), cells(contents, pageRefs.get(2), "reference", "resolution", "target_id"), "no such page");

        CsvTable errors = TestSupport.table(kb, "errors");
        List<String> lines = new ArrayList<>();
        for (String[] row : errors.rows) {
            lines.add(String.join("|", cells(errors, row, "kind", "source_id", "object_type", "reason")));
        }
        Check.equal(List.of(
                "ambiguous_reference|sales/_portal/dash/page 1|" + D + "globalFilterView|'/shared/sales/twin' matches more than one object once case is folded — sales/Twin | sales/tWin. Choosing between them would be a guess, so it is recorded as unresolved.",
                "unresolved_reference|sales/_portal/dash/dashboard layout|" + D + "dashboardPageRef|'gone' did not resolve: no object or folder at this path. Recorded rather than repaired (AD-3).",
                "unresolved_reference|sales/_portal/dash/page 1|" + D + "globalFilterView|\"/company_shared/Prompts/It's \\\\ here\" did not resolve: names a catalog root this tree does not contain. Recorded rather than repaired (AD-3).",
                "unresolved_reference|sales/_portal/dash/page 1|" + D + "reportRef|'' did not resolve: no object or folder at this path. Recorded rather than repaired (AD-3)."),
                lines, "one error row for each reference that did not resolve: the global filter view is resolved once (deliberate difference 11)");

        CsvTable coverage = TestSupport.table(kb, "coverage");
        Check.equal("2", value(coverage, "object_type", D + "globalFilterView", "skipped"), "skipped counts the two references, once each");
        Check.equal(List.of("refs::cat::sales/_portal/dash/dashboard layout", "0"),
                cells(TestSupport.table(kb, "dashboard_pages"), TestSupport.row(TestSupport.table(kb, "dashboard_pages"), "name", "page 1"), "dashboard_id", "position"),
                "the page's dashboard comes from the reference that resolved");
    }

    /** Error reasons quote the reference as the reference implementation does, so the rows compare equal. */
    public void testAReferenceIsQuotedInTheReasonAsTheReferenceImplementationQuotesIt() {
        Path work = TestSupport.work("catalog-quoting");
        Path estate = work.resolve("quote");
        TestSupport.catalogFile(estate, "d/dashboard+layout", dashboard("plain", "it's", "say &quot;hi&quot;", "it's &quot;both&quot;",
                "tab&#9;line&#10;back\\slash", "café nbsp​zero-width"));
        Path kb = work.resolve("kb");
        TestSupport.extract(estate, kb);
        CsvTable errors = TestSupport.table(kb, "errors");
        List<String> quoted = new ArrayList<>();
        for (String[] row : errors.rows) {
            String reason = errors.value(row, "reason");
            quoted.add(reason.substring(0, reason.indexOf(" did not resolve")));
        }
        quoted.sort(null);
        Check.equal(List.of("\"it's\"", "'café\\xa0nbsp\\u200bzero-width'", "'it\\'s \"both\"'", "'plain'", "'say \"hi\"'",
                "'tab\\tline\\nback\\\\slash'"), quoted, "single quotes; double when the text has only single quotes; "
                        + "escapes for the quote, the backslash, control characters and characters that do not print");
        Check.equal("d", value(TestSupport.table(kb, "dashboards"), "row_id", "quote::cat::d/dashboard layout", "name"),
                "a dashboard one level down is named by its folder");
    }

    /** Deliberate difference 3: a path that cannot be part of an identifier costs one object, not the run. */
    public void testAPathThatCannotBeAnIdentifierIsAnErrorRowAndTheRunGoesOn() {
        if (java.io.File.separatorChar != '/') {
            throw new SkipException("a file name with a colon cannot be made on this file system");
        }
        Path work = TestSupport.work("catalog-separator");
        Path estate = work.resolve("sep");
        TestSupport.catalogFile(estate, "a/_portal/bad%3a%3aname/dashboard+layout", dashboard("page"));
        TestSupport.catalogFile(estate, "a/_portal/good/dashboard+layout", dashboard("/shared/a/odd::page", "page"));
        TestSupport.catalogFile(estate, "a/_portal/good/page", page(""));
        TestSupport.catalogFile(estate, "a/odd%3a%3apage", page(""));
        Path kb = work.resolve("kb");
        TestSupport.Run run = TestSupport.extract(estate, kb);
        Check.equal(3, run.exit, "written, with error rows: " + run.out + run.err);
        Check.equal(List.of("good"), TestSupport.column(TestSupport.table(kb, "dashboards"), "name"), "the other dashboard is read");
        Check.equal(List.of("page"), TestSupport.column(TestSupport.table(kb, "dashboard_pages"), "name"), "the other page is read");
        CsvTable errors = TestSupport.table(kb, "errors");
        List<String> lines = new ArrayList<>();
        for (String[] row : errors.rows) {
            lines.add(String.join("|", cells(errors, row, "kind", "source_id", "object_type")));
            Check.contains(errors.value(row, "reason"), "'::'", "the reason names the separator");
        }
        Check.equal(List.of(
                "invalid_name|a/_portal/bad::name/dashboard layout|" + D + "dashboard",
                "invalid_name|a/_portal/good/dashboard layout|" + D + "dashboardPageRef",
                "invalid_name|a/odd::page|" + D + "dashboardPage"), lines, "the two objects, and the reference that points at one of them");
        CsvTable contents = TestSupport.table(kb, "dashboard_contents");
        Check.equal(List.of("unresolved", "", "the path it points at cannot be part of a Row identifier"),
                cells(contents, TestSupport.row(contents, "reference", "/shared/a/odd::page"), "resolution", "target_id", "detail"),
                "the reference is kept, with no target");
        Check.equal(0L, TestSupport.rows(TestSupport.table(kb, "unparsed"), "kind", "unread_attribute").stream()
                .filter(r -> r[3].contains("xmlVersion")).count(), "an object that was left out is not also listed attribute by attribute");
    }

    public void testTwoNamesThatDecodeToOnePathAreReportedAsDuplicates() {
        Path work = TestSupport.work("catalog-duplicate");
        Path estate = work.resolve("dup");
        TestSupport.catalogFile(estate, "p/a+b", page(""));
        TestSupport.catalogFile(estate, "p/a%20b", page(""));
        Path kb = work.resolve("kb");
        Check.equal(3, TestSupport.extract(estate, kb).exit, "a duplicate identifier is an error");
        Check.equal(2, TestSupport.table(kb, "dashboard_pages").rows.size(), "both rows are written");
        CsvTable errors = TestSupport.table(kb, "errors");
        Check.equal(List.of("duplicate_identifier", "dup::cat::p/a b", "dashboard_pages"),
                cells(errors, errors.rows.get(0), "kind", "source_id", "object_type"), "the new tables are covered by the uniqueness check");
    }

    // ------------------------------------------------------------------ the audit

    public void testWhatIsNotReadIsListedAndStylingIsListedApart() {
        Path work = TestSupport.work("catalog-audit");
        Path estate = work.resolve("audit");
        TestSupport.catalogFile(estate, "s/_portal/d/dashboard+layout",
                "<sawd:dashboard xmlns:sawd=\"" + SAWD + "\" xmlVersion=\"1\" style=\"blue\">"
                        + "<sawd:dashboardPageRef path=\"p\" type=\"catalog\" hidden=\"false\"/></sawd:dashboard>");
        TestSupport.catalogFile(estate, "s/_portal/d/p", page(
                "<sawd:dashboardColumn name=\"Column 0\" width=\"50%\" fmt=\"html\">"
                        + "<sawd:reportView name=\"R\" backgroundColor=\"#fff\"><sawd:reportRef path=\"/shared/s/r\"/></sawd:reportView>"
                        + "<sawd:htmlView><sawd:staticText>Read me<sawd:caption fontSize=\"9\"/> tail</sawd:staticText></sawd:htmlView>"
                        + "</sawd:dashboardColumn>"));
        TestSupport.catalogFile(estate, "s/r", report());
        Path kb = work.resolve("kb");
        Check.equal(0, TestSupport.extract(estate, kb).exit, "gaps are not errors");
        CsvTable unparsed = TestSupport.table(kb, "unparsed");
        List<String> lines = new ArrayList<>();
        for (String[] row : unparsed.rows) {
            if (!unparsed.value(row, "kind").equals("unmodelled_type")) {
                lines.add(String.join("|", cells(unparsed, row, "kind", "object_type", "detail", "occurrences")));
            }
        }
        String dash = D + "dashboard";
        String pg = D + "dashboardPage";
        Check.equal(List.of(
                "excluded_by_scope|" + dash + "|" + D + "dashboard@style|1",
                "excluded_by_scope|" + pg + "|" + D + "caption@fontSize|1",
                "excluded_by_scope|" + pg + "|" + D + "dashboardColumn@width|1",
                "excluded_by_scope|" + pg + "|" + D + "reportView@backgroundColor|1",
                "unread_attribute|" + dash + "|" + D + "dashboardPageRef@hidden|1",
                "unread_attribute|" + dash + "|" + D + "dashboardPageRef@type|1",
                "unread_attribute|" + pg + "|" + D + "dashboardColumn@fmt|1",
                "unread_attribute|" + pg + "|" + D + "dashboardColumn@name|1",
                "unread_attribute|" + pg + "|" + D + "reportView@name|1",
                "unread_element|" + pg + "|" + D + "caption|1",
                "unread_element|" + pg + "|" + D + "htmlView|1",
                "unread_element|" + pg + "|" + D + "staticText|1",
                "unread_text|" + pg + "|" + D + "staticText#text|1"),
                lines, "styling attributes under excluded_by_scope ('fmt' is not styling); wrappers that were walked through "
                        + "are not listed; text after a child element is never examined");
    }
}
