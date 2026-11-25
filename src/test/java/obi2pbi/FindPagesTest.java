package obi2pbi;

import java.nio.file.Path;
import java.nio.file.Files;

/**
 * {@code find-pages}: the dashboards, pages and reports a name may mean, so that the trace skill can name the
 * page a tab replaces (docs/skill-conventions.md, section 10.4). On the constructed pair: dashboard "sales
 * overview" with page "page one", which places the report "revenue by month" on subject area "Constructed SA".
 */
public final class FindPagesTest {

    private static Path kb;

    private static Path kb() throws Exception {
        if (kb == null) {
            Path work = TestSupport.work("find-pages-kb");
            Path estate = TestSupport.estateFrom(work, "cp", TestSupport.constructed());
            TestSupport.copyTree(TestSupport.constructedCatalog(), estate.resolve("catalog"));
            kb = work.resolve("kb");
            Check.equal(0, TestSupport.extract(estate, kb).exit, "extract");
        }
        return kb;
    }

    public void testAPageIsFoundByItsDashboardAndPageNameWithTheReportsOnIt() throws Exception {
        TestSupport.Run run = TestSupport.run("find-pages", kb().toString(), "sales overview / page one");
        Check.equal(0, run.exit, "exit; " + run.err);
        Check.contains(run.out, "1. page [kb:cp::cat::constructed/_portal/sales overview/page one]  (whole name: sales, overview, "
                + "page, one; score 10)", "the dashboard's name and the page's is the page's whole name");
        Check.contains(run.out, "   page \"page one\", position 0 of dashboard \"sales overview\" "
                + "[kb:cp::cat::constructed/_portal/sales overview/dashboard layout]", "its dashboard and position");
        Check.contains(run.out, "     [kb:cp::cat::constructed/revenue by month] subject area Constructed SA",
                "the report on it, with its subject area");
    }

    public void testDashboardsAndReportsAreFoundTooAndTheLimitHolds() throws Exception {
        TestSupport.Run run = TestSupport.run("find-pages", kb().toString(), "sales overview");
        Check.contains(run.out, "1. dashboard [kb:cp::cat::constructed/_portal/sales overview/dashboard layout]  (whole name",
                "the dashboard's own name ranks first");
        Check.contains(run.out, "     0. \"page one\" [kb:cp::cat::constructed/_portal/sales overview/page one]", "its pages");
        Check.contains(run.out, "2. page ", "then its page, which has the words in its dashboard's name");
        TestSupport.Run report = TestSupport.run("find-pages", kb().toString(), "revenue month", "--limit", "1");
        Check.contains(report.out, "1. report [kb:cp::cat::constructed/revenue by month]", "a report");
        Check.contains(report.out, "   placed on 1 page(s):", "with the pages that place it");
        Check.that(!report.out.contains("\n2. "), "the limit holds");
        Check.equal(2, TestSupport.run("find-pages", kb().toString()).exit, "no term");
    }

    public void testWithoutACatalogItSaysNoPageCanBeNamed() throws Exception {
        Path work = TestSupport.work("find-pages-rpd");
        Path rpdOnly = work.resolve("kb");
        Check.equal(0, TestSupport.extract(TestSupport.estateFrom(work, "cp", TestSupport.constructed()), rpdOnly).exit, "extract");
        TestSupport.Run run = TestSupport.run("find-pages", rpdOnly.toString(), "overview");
        Check.equal(0, run.exit, "exit");
        Check.contains(run.out, "built without a catalog", "says why nothing can be found");
        Check.that(Files.isDirectory(rpdOnly), "read only");
    }
}
