package obi2pbi;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import obi2pbi.core.KbSchema;
import obi2pbi.core.KbTable;
import obi2pbi.in.kb.CsvTable;

/**
 * The Knowledge Base describes itself: {@code schema_tables.csv} and {@code schema.csv} cover every table
 * and column written, say what each means, and the links they declare hold in the data.
 */
public final class SchemaTest {

    /**
     * Columns declared as referring to another table whose values need not all resolve, as
     * {@code table.column}. For a column that points at different tables depending on another column.
     * Empty so far: add an entry only with a note on the column saying how to read it.
     */
    private static final Set<String> POLYMORPHIC = Set.of();

    private static final Set<String> LAYERS = Set.of("phys", "bmm", "pres", "var", "sec", "cat", "run");

    private static Path fixtureKb(String work) {
        Path folder = TestSupport.work(work);
        Path kb = folder.resolve("kb");
        Check.equal(0, TestSupport.extract(TestSupport.estateFrom(folder, "testrpd", TestSupport.testRpd()), kb).exit, "extract");
        return kb;
    }

    public void testEveryTableWrittenHasASchemaTablesRow() {
        Path kb = fixtureKb("schema-tables");
        CsvTable schemaTables = TestSupport.table(kb, "schema_tables");
        Check.equal(List.of("table", "description", "row_meaning", "key_column", "layer"), schemaTables.header, "columns");
        List<String> described = TestSupport.column(schemaTables, "table");
        List<String> written = new ArrayList<>();
        for (String file : TestSupport.fileNames(kb)) {
            written.add(file.substring(0, file.length() - ".csv".length()));
        }
        List<String> sorted = new ArrayList<>(described);
        sorted.sort(null);
        Check.equal(written, sorted, "one schema_tables row per file written, and no row for a file not written");
        for (String[] row : schemaTables.rows) {
            String table = schemaTables.value(row, "table");
            Check.that(schemaTables.value(row, "description").length() > 20, table + ": a real description");
            Check.that(schemaTables.value(row, "row_meaning").length() > 10, table + ": a real row meaning");
            Check.that(LAYERS.contains(schemaTables.value(row, "layer")), table + ": a known layer");
            String key = schemaTables.value(row, "key_column");
            Check.that(key.isEmpty() || TestSupport.table(kb, table).header.contains(key), table + ": key column exists");
        }
        Check.equal("row_id", schemaTables.value(TestSupport.row(schemaTables, "table", "physical_tables"), "key_column"), "key of physical_tables");
        Check.equal("phys", schemaTables.value(TestSupport.row(schemaTables, "table", "physical_columns"), "layer"), "layer of physical_columns");
        Check.equal("run", schemaTables.value(TestSupport.row(schemaTables, "table", "errors"), "layer"), "layer of errors");
    }

    public void testEveryColumnWrittenHasASchemaRowWithAMeaning() {
        Path kb = fixtureKb("schema-columns");
        CsvTable schema = TestSupport.table(kb, "schema");
        Check.equal(List.of("table", "position", "column", "meaning", "is_key", "references_table",
                "references_column", "note"), schema.header, "columns");
        int total = 0;
        for (String file : TestSupport.fileNames(kb)) {
            String table = file.substring(0, file.length() - ".csv".length());
            List<String[]> rows = TestSupport.rows(schema, "table", table);
            List<String> header = TestSupport.table(kb, table).header;
            Check.equal(header.size(), rows.size(), table + ": one schema row per column");
            for (int i = 0; i < header.size(); i++) {
                String[] row = rows.get(i);
                Check.equal(header.get(i), schema.value(row, "column"), table + ": column " + (i + 1) + " in file order");
                Check.equal(Integer.toString(i + 1), schema.value(row, "position"), table + "." + header.get(i) + ": position");
                Check.that(schema.value(row, "meaning").length() > 10, table + "." + header.get(i) + ": a real meaning");
                Check.that(Set.of("true", "false").contains(schema.value(row, "is_key")), table + "." + header.get(i) + ": is_key");
            }
            total += header.size();
        }
        Check.equal(total, schema.rows.size(), "no schema row for a column that is not written");
    }

    public void testEveryDeclaredReferenceNamesATableAndColumnInTheSchema() {
        Check.equal(List.of(), declaredReferenceProblems(TestSupport.table(fixtureKb("schema-declared"), "schema")), "problems");
    }

    public void testTheWriterAndTheSchemaComeFromOneDefinition() {
        Path kb = fixtureKb("schema-one-definition");
        for (KbTable table : KbSchema.TABLES) {
            Check.equal(table.header(), TestSupport.table(kb, table.name).header, table.name + ": header as declared");
            Check.that(!table.description.isEmpty() && !table.rowMeaning.isEmpty(), table.name + ": described");
            Set<String> names = new HashSet<>(table.header());
            Check.equal(table.columns.size(), names.size(), table.name + ": column names are unique");
        }
    }

    public void testEveryReferringValueResolvesInTheFixtureKnowledgeBases() {
        Check.equal(List.of(), valueProblems(fixtureKb("schema-values-testrpd")), "testrpd");

        Path work = TestSupport.work("schema-values-constructed");
        Path kb = work.resolve("kb");
        TestSupport.extract(TestSupport.estateFrom(work, "constructed", TestSupport.constructed()), kb);
        Check.equal(List.of(), valueProblems(kb), "constructed");

        // An estate with an alias, so that a referring column other than table_id holds a value.
        Path aliases = TestSupport.work("schema-values-alias");
        Path estate = aliases.resolve("alias");
        TestSupport.schema(estate, "ms", "S");
        String open = "<PhysicalTable type=\"none\" xmlns=\"" + TestSupport.XMLNS + "\" containerRef=\"" + TestSupport.schemaRef("ms") + "\" ";
        TestSupport.object(estate, "PhysicalTable", "a.xml", open + "mdsid=\"ma\" name=\"A\"><DBMapItem name=\"D\"><Value>A_T</Value></DBMapItem></PhysicalTable>");
        TestSupport.object(estate, "PhysicalTable", "b.xml", open + "mdsid=\"mb\" name=\"B\" sourceTableRef=\"/oracle/bi/server/base/PhysicalTable/a.xml#ma\"/>");
        Path aliasKb = aliases.resolve("kb");
        Check.equal(0, TestSupport.extract(estate, aliasKb).exit, "extract");
        Check.equal("alias::phys::S.A", TestSupport.table(aliasKb, "physical_tables").value(
                TestSupport.row(TestSupport.table(aliasKb, "physical_tables"), "name", "B"), "source_table_id"), "premise: a link is present");
        Check.equal(List.of(), valueProblems(aliasKb), "alias estate");
    }

    public void testABrokenLinkIsDetected() throws Exception {
        Path kb = fixtureKb("schema-planted");
        Path columns = kb.resolve("physical_columns.csv");
        String text = new String(Files.readAllBytes(columns), StandardCharsets.UTF_8);
        String broken = text.replace("\"testrpd::phys::SALES_SCHEMA.SAMP_REVENUE_F\",\"REVENUE\"", "\"testrpd::phys::NOWHERE\",\"REVENUE\"");
        Check.that(!broken.equals(text), "premise: the value was planted");
        Files.write(columns, broken.getBytes(StandardCharsets.UTF_8));
        List<String> problems = valueProblems(kb);
        Check.equal(1, problems.size(), "one problem: " + problems);
        Check.contains(problems.get(0), "physical_columns.table_id", "names the column");

        Path schema = kb.resolve("schema.csv");
        String schemaText = new String(Files.readAllBytes(schema), StandardCharsets.UTF_8);
        Files.write(schema, schemaText.replace("\"physical_tables\",\"row_id\"", "\"no_such_table\",\"row_id\"").getBytes(StandardCharsets.UTF_8));
        Check.that(!declaredReferenceProblems(TestSupport.table(kb, "schema")).isEmpty(), "a reference to a table that does not exist is detected");
    }

    /** Declared references whose target table or column is not itself in the schema. */
    static List<String> declaredReferenceProblems(CsvTable schema) {
        Set<String> known = new HashSet<>();
        for (String[] row : schema.rows) {
            known.add(schema.value(row, "table") + "." + schema.value(row, "column"));
        }
        List<String> problems = new ArrayList<>();
        for (String[] row : schema.rows) {
            String table = schema.value(row, "references_table");
            String column = schema.value(row, "references_column");
            String here = schema.value(row, "table") + "." + schema.value(row, "column");
            if (table.isEmpty() != column.isEmpty()) {
                problems.add(here + ": references_table and references_column must be given together");
            } else if (!table.isEmpty() && !known.contains(table + "." + column)) {
                problems.add(here + ": refers to " + table + "." + column + ", which is not in the schema");
            }
        }
        return problems;
    }

    /** Non-empty values of referring columns that are not found in the column they refer to. */
    static List<String> valueProblems(Path kb) {
        CsvTable schema = TestSupport.table(kb, "schema");
        List<String> problems = new ArrayList<>();
        for (String[] row : schema.rows) {
            String targetTable = schema.value(row, "references_table");
            if (targetTable.isEmpty()) {
                continue;
            }
            String table = schema.value(row, "table");
            String column = schema.value(row, "column");
            if (POLYMORPHIC.contains(table + "." + column)) {
                continue;
            }
            Set<String> targets = new HashSet<>(TestSupport.column(TestSupport.table(kb, targetTable), schema.value(row, "references_column")));
            for (String value : TestSupport.column(TestSupport.table(kb, table), column)) {
                if (!value.isEmpty() && !targets.contains(value)) {
                    problems.add(table + "." + column + ": '" + value + "' is not in " + targetTable + "."
                            + schema.value(row, "references_column"));
                }
            }
        }
        return problems;
    }
}
