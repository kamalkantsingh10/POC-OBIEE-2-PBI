package obi2pbi;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import obi2pbi.core.Estate;
import obi2pbi.core.Unparsed;
import obi2pbi.in.kb.CsvTable;
import obi2pbi.in.mds.ObjectReader;
import obi2pbi.in.mds.ReadContext;
import obi2pbi.in.mds.RepositoryReader;
import obi2pbi.in.xml.XmlNode;

/**
 * What was not read, and what went wrong (story 1.2 matrix; guide 03 §1, §2, §8.6). Each test writes the
 * small XML files it needs.
 */
public final class GapsAndErrorsTest {

    private static final String NS = " xmlns=\"" + TestSupport.XMLNS + "\"";

    /** A table in schema "S" (mdsid "ms"), with whatever attributes and content the test adds. */
    private static String table(String mdsid, String name, String attributes, String content) {
        return "<PhysicalTable mdsid=\"" + mdsid + "\" name=\"" + name + "\" type=\"none\"" + NS
                + " containerRef=\"" + TestSupport.schemaRef("ms") + "\"" + attributes + ">" + content + "</PhysicalTable>";
    }

    private static Path estate(String work, String name) {
        Path estate = TestSupport.work(work).resolve(name);
        TestSupport.schema(estate, "ms", "S");
        return estate;
    }

    private static String occurrences(CsvTable unparsed, String kind, String objectType, String detail) {
        for (String[] row : unparsed.rows) {
            if (unparsed.value(row, "kind").equals(kind) && unparsed.value(row, "object_type").equals(objectType)
                    && unparsed.value(row, "detail").equals(detail)) {
                return unparsed.value(row, "occurrences");
            }
        }
        return null;
    }

    public void testUnreadAttributeElementAndTextAreListed() {
        Path estate = estate("gaps-unread", "gaps");
        TestSupport.object(estate, "PhysicalTable", "t1.xml", table("m1", "T1", " rowCount=\"5\"",
                // A table's description is read since difference 37, so the example of unread text is another element.
                "<Remark>about T1</Remark>"
                        + "<PhysicalColumn mdsid=\"c1\" name=\"A\" lastUpdated=\"2\"/><PhysicalColumn mdsid=\"c2\" name=\"B\" lastUpdated=\"0\"/>"
                        // Until differences 28 and 31 this test used a physical key and a column's scale as its
                        // examples of unread content. Both are read now, so it uses content no reader takes.
                        + "<NotModelled mdsid=\"k1\" name=\"K\"><Parts><Part refId=\"r\"/></Parts></NotModelled>"));
        TestSupport.object(estate, "PhysicalTable", "t2.xml", table("m2", "T2", " rowCount=\"9\"",
                "<PhysicalColumn mdsid=\"c3\" name=\"A\" lastUpdated=\"1\"/>"));
        Path kb = estate.resolveSibling("kb");
        Check.equal(0, TestSupport.extract(estate, kb).exit, "gaps are not errors: exit 0");
        CsvTable unparsed = TestSupport.table(kb, "unparsed");
        Check.equal("2", occurrences(unparsed, "unread_attribute", "PhysicalTable", "PhysicalTable@rowCount"),
                "an unread attribute, counted across the files of the type");
        Check.equal("3", occurrences(unparsed, "unread_attribute", "PhysicalTable", "PhysicalColumn@lastUpdated"),
                "an unread attribute of a part, under the file's object type");
        Check.equal("1", occurrences(unparsed, "unread_element", "PhysicalTable", "NotModelled"), "an unread element");
        Check.equal("1", occurrences(unparsed, "unread_element", "PhysicalTable", "Part"),
                "elements inside an unread element are listed too");
        Check.equal("1", occurrences(unparsed, "unread_attribute", "PhysicalTable", "Part@refId"), "with their attributes");
        Check.equal("1", occurrences(unparsed, "unread_text", "PhysicalTable", "Remark#text"), "unread text");
        Check.equal(null, occurrences(unparsed, "unread_attribute", "PhysicalTable", "PhysicalTable@name"), "what is read is not listed");
        Check.equal(null, occurrences(unparsed, "unread_attribute", "PhysicalTable", "PhysicalTable@xmlns"),
                "a namespace declaration is never an unread attribute");
        for (String[] row : unparsed.rows) {
            Check.that(!unparsed.value(row, "detail").contains("xmlns"), "no namespace declaration anywhere: "
                    + unparsed.value(row, "detail"));
        }
        Check.equal(0, TestSupport.table(kb, "errors").rows.size(), "errors.csv holds only its header");
    }

    /** A reader for a made-up type. Whether it reads {@code colour} is the only thing the test varies. */
    private static final class WidgetReader implements ObjectReader {
        private final boolean readsColour;

        WidgetReader(boolean readsColour) {
            this.readsColour = readsColour;
        }

        @Override
        public String objectType() {
            return "Widget";
        }

        @Override
        public void bind(XmlNode root, ReadContext context) {
            root.attr("name");
        }

        @Override
        public void read(XmlNode root, ReadContext context) {
            if (readsColour) {
                root.attr("colour");
            }
            context.parsed("Widget");
        }
    }

    public void testAnAttributeAReaderStopsReadingAppearsWithNoOtherChange() throws Exception {
        Path estate = estate("gaps-observed", "observed");
        TestSupport.object(estate, "Widget", "w.xml", "<Widget name=\"w\" colour=\"red\"" + NS + "/>");
        Estate reading = RepositoryReader.load(estate.resolve("rpd"), "observed", List.of(new WidgetReader(true)));
        Estate notReading = RepositoryReader.load(estate.resolve("rpd"), "observed", List.of(new WidgetReader(false)));
        Check.equal(0, count(reading, Unparsed.UNREAD_ATTRIBUTE, "Widget@colour"), "read: not reported");
        Check.equal(1, count(notReading, Unparsed.UNREAD_ATTRIBUTE, "Widget@colour"), "no longer read: reported");
        Check.equal(0, count(reading, Unparsed.UNMODELLED_TYPE, "w.xml"), "a type with a reader is not unmodelled");
    }

    private static int count(Estate estate, String kind, String detail) {
        int found = 0;
        for (Unparsed gap : estate.unparsed) {
            if (gap.kind.equals(kind) && gap.detail.equals(detail)) {
                found++;
            }
        }
        return found;
    }

    public void testATypeWithNoReaderIsCountedAsSkipped() {
        Path estate = estate("gaps-unmodelled", "unmodelled");
        TestSupport.object(estate, "PhysicalTable", "t1.xml", table("m1", "T1", "", ""));
        TestSupport.object(estate, "Gizmo", "g1.xml", "<Gizmo mdsid=\"g1\" name=\"one\"" + NS + "/>");
        TestSupport.object(estate, "Gizmo", "g2.xml", "<Gizmo mdsid=\"g2\" name=\"two\"" + NS + "/>");
        Path kb = estate.resolveSibling("kb");
        Check.equal(0, TestSupport.extract(estate, kb).exit, "a gap, not an error");
        CsvTable unparsed = TestSupport.table(kb, "unparsed");
        Check.equal("1", occurrences(unparsed, "unmodelled_type", "Gizmo", "g1.xml"), "one row per file");
        Check.equal("1", occurrences(unparsed, "unmodelled_type", "Gizmo", "g2.xml"), "one row per file");
        CsvTable coverage = TestSupport.table(kb, "coverage");
        String[] gizmo = TestSupport.row(coverage, "object_type", "Gizmo");
        Check.equal("0", coverage.value(gizmo, "parsed"), "none parsed");
        Check.equal("2", coverage.value(gizmo, "skipped"), "both skipped");
        Check.equal("rpd", coverage.value(gizmo, "layer"), "store");
        Check.equal(0, TestSupport.rows(unparsed, "object_type", "Schema").size(), "an index-only type is not a gap");
    }

    public void testAFileDiscoveryDidNotPickUpIsListedUnderTheEstateName() {
        Path estate = estate("gaps-undiscovered", "strays");
        TestSupport.object(estate, "PhysicalTable", "t1.xml", table("m1", "T1", "", ""));
        TestSupport.object(estate, "PhysicalTable", "nested/buried.xml", table("m2", "Buried", "", ""));
        TestSupport.object(estate, "PhysicalTable", "no-extension", table("m3", "NoExt", "", ""));
        TestSupport.object(estate, "PhysicalTable", "UPPER.XML", table("m4", "Upper", "", ""));
        TestSupport.write(estate.resolve("rpd/README.txt"), "hello");
        Path kb = estate.resolveSibling("kb");
        Check.equal(0, TestSupport.extract(estate, kb).exit, "exit code");
        CsvTable unparsed = TestSupport.table(kb, "unparsed");
        List<String[]> rows = TestSupport.rows(unparsed, "kind", "undiscovered_file");
        Check.equal(List.of("README.txt", "oracle/bi/server/base/PhysicalTable/UPPER.XML",
                "oracle/bi/server/base/PhysicalTable/nested/buried.xml", "oracle/bi/server/base/PhysicalTable/no-extension"),
                List.of(unparsed.value(rows.get(0), "detail"), unparsed.value(rows.get(1), "detail"),
                        unparsed.value(rows.get(2), "detail"), unparsed.value(rows.get(3), "detail")), "paths within the repository");
        for (String[] row : rows) {
            Check.equal("strays", unparsed.value(row, "rpd_id"), "the estate name, not a folder name (difference 12)");
            Check.equal("File", unparsed.value(row, "object_type"), "object type");
        }
        Check.equal(1, TestSupport.table(kb, "physical_tables").rows.size(), "none of them was read as a table");
    }

    public void testAnOrdinaryTreeHasNoUndiscoveredFiles() {
        Path work = TestSupport.work("gaps-clean");
        Path kb = work.resolve("kb");
        TestSupport.Run run = TestSupport.extract(TestSupport.estateFrom(work, "testrpd", TestSupport.testRpd()), kb);
        Check.equal(0, run.exit, "clean fixture: exit 0");
        Check.equal(0, TestSupport.table(kb, "errors").rows.size(), "clean fixture: errors.csv holds only its header");
        Check.equal(0, TestSupport.rows(TestSupport.table(kb, "unparsed"), "kind", "undiscovered_file").size(), "no strays");
    }

    public void testBrokenXmlIsAnErrorAndTheOtherFilesAreStillRead() {
        Path estate = estate("errors-malformed", "broken");
        TestSupport.object(estate, "PhysicalTable", "bad.xml", "<PhysicalTable name=\"oops\"><unclosed></PhysicalTable>");
        TestSupport.object(estate, "PhysicalTable", "good.xml", table("m1", "Good", "", ""));
        Path kb = estate.resolveSibling("kb");
        TestSupport.Run run = TestSupport.extract(estate, kb);
        Check.equal(3, run.exit, "exit 3");
        Check.contains(run.out, "errors:   1", "the count is printed");
        CsvTable errors = TestSupport.table(kb, "errors");
        Check.equal(1, errors.rows.size(), "one error row");
        String[] error = errors.rows.get(0);
        Check.equal("malformed_xml", errors.value(error, "kind"), "kind");
        Check.equal("bad.xml", errors.value(error, "source_id"), "the file");
        Check.equal("PhysicalTable", errors.value(error, "object_type"), "its type");
        Check.equal("broken", errors.value(error, "rpd_id"), "estate");
        Check.that(!errors.value(error, "reason").isEmpty(), "the parser's message");
        Check.equal(List.of("broken::phys::S.Good"), TestSupport.column(TestSupport.table(kb, "physical_tables"), "row_id"),
                "the good file is still read");
        CsvTable coverage = TestSupport.table(kb, "coverage");
        String[] counts = TestSupport.row(coverage, "object_type", "PhysicalTable");
        Check.equal("1", coverage.value(counts, "parsed"), "parsed");
        Check.equal("1", coverage.value(counts, "skipped"), "the broken file counts as skipped");
        Check.equal(0, TestSupport.rows(TestSupport.table(kb, "unparsed"), "kind", "malformed_xml").size(),
                "errors and gaps are separate files");
    }

    public void testAFileThatCannotBeOpenedIsAnErrorAndTheRunContinues() throws Exception {
        Path estate = estate("errors-unreadable", "locked");
        TestSupport.object(estate, "PhysicalTable", "good.xml", table("m1", "Good", "", ""));
        // A folder named like an object file: opening it as a file fails on every platform and for every user.
        Files.createDirectories(estate.resolve("rpd/oracle/bi/server/base/PhysicalTable/folder.xml"));
        Path kb = estate.resolveSibling("kb");
        Check.equal(3, TestSupport.extract(estate, kb).exit, "exit 3");
        CsvTable errors = TestSupport.table(kb, "errors");
        Check.equal(1, errors.rows.size(), "one error row");
        Check.equal("unreadable", errors.value(errors.rows.get(0), "kind"), "kind");
        Check.equal("folder.xml", errors.value(errors.rows.get(0), "source_id"), "the file");
        Check.equal(1, TestSupport.table(kb, "physical_tables").rows.size(), "the good file is still read");
    }

    public void testTwoObjectsWithOneIdentifierAreBothWrittenAndReported() {
        Path estate = estate("errors-duplicate", "twins");
        TestSupport.object(estate, "PhysicalTable", "t1.xml", table("m1", "Same", "", "<PhysicalColumn mdsid=\"c1\" name=\"A\"/>"));
        TestSupport.object(estate, "PhysicalTable", "t2.xml", table("m2", "Same", " maxConn=\"2\"", ""));
        TestSupport.object(estate, "PhysicalTable", "t3.xml", table("m3", "Other", "", ""));
        Path kb = estate.resolveSibling("kb");
        Check.equal(3, TestSupport.extract(estate, kb).exit, "exit 3");
        CsvTable tables = TestSupport.table(kb, "physical_tables");
        Check.equal(2, TestSupport.rows(tables, "row_id", "twins::phys::S.Same").size(), "both rows are written");
        CsvTable errors = TestSupport.table(kb, "errors");
        Check.equal(1, errors.rows.size(), "exactly one error for the pair");
        String[] error = errors.rows.get(0);
        Check.equal("duplicate_identifier", errors.value(error, "kind"), "kind");
        Check.equal("twins::phys::S.Same", errors.value(error, "source_id"), "names the identifier");
        Check.equal("physical_tables", errors.value(error, "object_type"), "names the table");
        Check.contains(errors.value(error, "reason"), "2 rows in physical_tables", "says how many");
        CsvTable coverage = TestSupport.table(kb, "coverage");
        String[] counts = TestSupport.row(coverage, "object_type", "PhysicalTable");
        Check.equal("3", coverage.value(counts, "parsed"), "all three were read");
        Check.equal("0", coverage.value(counts, "skipped"), "a duplicate is not counted as skipped");
        Check.equal(0, TestSupport.rows(coverage, "object_type", "physical_tables").size(), "and adds no coverage row");
    }

    public void testANameThatCannotBeAnIdentifierIsAnErrorAndTheRunContinues() {
        Path estate = estate("errors-bad-name", "names");
        TestSupport.object(estate, "PhysicalTable", "t1.xml", table("m1", "A::B", "", "<PhysicalColumn mdsid=\"c0\" name=\"X\"/>"));
        TestSupport.object(estate, "PhysicalTable", "t2.xml", table("m2", "   ", "", ""));
        TestSupport.object(estate, "PhysicalTable", "t3.xml", table("m3", "Good", "",
                "<PhysicalColumn mdsid=\"c1\" name=\"OK\"/><PhysicalColumn mdsid=\"c2\" name=\"bad::col\"/>"
                        + "<PhysicalColumn mdsid=\"c3\" name=\"\"/>"));
        TestSupport.object(estate, "PhysicalTable", "t4.xml", "<PhysicalTable mdsid=\"m4\" name=\"Orphan\"" + NS + "/>");
        Path kb = estate.resolveSibling("kb");
        TestSupport.Run run = TestSupport.extract(estate, kb);
        Check.equal(3, run.exit, "exit 3, not a stopped run: " + run.err);
        Check.equal(List.of("names::phys::S.Good"), TestSupport.column(TestSupport.table(kb, "physical_tables"), "row_id"),
                "the good table is written");
        Check.equal(List.of("names::phys::S.Good.OK"), TestSupport.column(TestSupport.table(kb, "physical_columns"), "row_id"),
                "and its good column");
        CsvTable errors = TestSupport.table(kb, "errors");
        Check.equal("invalid_name", errors.value(TestSupport.row(errors, "source_id", "m1"), "kind"), "a name containing ::");
        Check.equal("invalid_name", errors.value(TestSupport.row(errors, "source_id", "m2"), "kind"), "a name of spaces only");
        String[] column = TestSupport.row(errors, "source_id", "c2");
        Check.equal("invalid_name", errors.value(column, "kind"), "a column name containing ::");
        Check.equal("PhysicalColumn", errors.value(column, "object_type"), "its type");
        Check.equal("missing_name", errors.value(TestSupport.row(errors, "source_id", "c3"), "kind"), "a column with no name");
        Check.equal("missing_name", errors.value(TestSupport.row(errors, "source_id", "m4"), "kind"), "a table with no container");
        Check.equal(5, errors.rows.size(), "five errors in all");
    }
}
