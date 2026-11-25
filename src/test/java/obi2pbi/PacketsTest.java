package obi2pbi;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import obi2pbi.core.Citation;
import obi2pbi.core.KbColumn;
import obi2pbi.core.KbData;
import obi2pbi.core.KbRows;
import obi2pbi.core.KbTable;
import obi2pbi.core.PacketUnit;
import obi2pbi.core.Packets;
import obi2pbi.core.Slice;
import obi2pbi.core.SliceSchema;

/**
 * Work units and packets (story 2.1; Architecture AD-17): what a skill reads in place of the Slice's tables.
 */
public final class PacketsTest {

    private static List<PacketUnit> units(SliceFixture fixture) {
        return Packets.build(Slice.cut(fixture.build(), fixture.pres("SA")));
    }

    private static PacketUnit unit(List<PacketUnit> units, String id) {
        for (PacketUnit unit : units) {
            if (unit.unitId.equals(id)) {
                return unit;
            }
        }
        throw new AssertionError("no unit " + id + " among " + ids(units));
    }

    private static List<String> ids(List<PacketUnit> units) {
        List<String> ids = new ArrayList<>();
        for (PacketUnit unit : units) {
            ids.add(unit.unitId);
        }
        return ids;
    }

    public void testUnitsComeInWritingOrderEachWithAHeader() {
        SliceFixture f = SliceFixture.estate("order").area().column("Amount", true, "").column("Name", false, "");
        f.add("logical_tables", "row_id", f.bmm("BM.Dim"), "business_model", "BM", "name", "Dim");
        f.add("logical_columns", "row_id", f.bmm("BM.Dim.Code"), "table_id", f.bmm("BM.Dim"), "name", "Code", "is_measure", "false");
        f.add("presentation_columns", "row_id", f.pres("SA.T.Code"), "table_id", f.pres("SA.T"), "name", "Code", "logical_column_id", f.bmm("BM.Dim.Code"));
        List<PacketUnit> units = units(f);
        Check.equal(List.of("overview", "table-01-f", "table-02-dim", "dbtable-01-p", "joins", "hierarchies", "variables", "security", "reports", "gaps"),
                ids(units), "overview, logical tables (those with measures first), physical tables, joins, hierarchies, variables, security, reports, gaps");
        PacketUnit table = unit(units, "table-01-f");
        Check.equal(List.of("# Logical table \"F\"", "Unit: table-01-f", "Kind: logical_table", "Subject area: SA (estate order)"),
                table.lines.subList(0, 4), "header: title, unit id, kind, subject area");
        // Until difference 33 this header also named physical_columns and physical_tables. Their rows are
        // required rows of the physical table's own unit now, and a required row is cited in one packet only.
        Check.equal("Facts from: lineage, logical_columns, logical_table_sources, logical_tables, lts_column_map, "
                + "presentation_columns", table.lines.get(4), "header: the tables its facts came from");
        Check.equal("Rows of this packet a document must cover: 5. Each is on a line that ends with (REQUIRED).", table.lines.get(5),
                "the logical table, its source, its measure, and the two presentation columns");
        Check.equal(Set.of(f.bmm("BM.F"), f.bmm("BM.F.src.S"), f.bmm("BM.F.Amount"), f.pres("SA.T.Amount"), f.pres("SA.T.Name")),
                table.requiredRows.keySet(), "which rows");
        Check.that(!table.requiredRows.containsKey(f.bmm("BM.F.Name")), "a logical column that is not a measure is in the packet and is not required");
        Check.that(table.text().contains(Citation.of(f.bmm("BM.F.Name"))), "it is still described, with its citation");
        Check.equal(Set.of(f.pres("SA.T")), unit(units, "overview").requiredRows.keySet(), "the presentation table is covered by the overview");
        Check.equal("reports", unit(units, "reports").kind, "a slice with no report still has a reports unit");
        Check.contains(unit(units, "reports").text(), "No report of the estate's Web Catalog names this subject area", "which says so");
    }

    public void testEveryFactIsFollowedByItsCitationAndFormulasAreExactlyAsExtracted() {
        String formula = " CASE WHEN \"%1\" > 0\n\tTHEN `x`   \nELSE 2 END ";
        SliceFixture f = SliceFixture.estate("exact").area().column("Amount", true, formula).column("One", true, " SUM(\"%1\") ");
        f.add("aggregation_rules", "row_id", f.bmm("BM.F.One#aggr0"), "logical_column_id", f.bmm("BM.F.One"), "is_default", "true",
                "expr_text", " SUM(\"%1\")", "expr_text_desc", " SUM(\"BM\".\"F\".\"One\")", "expr_refs", f.bmm("BM.F.One"));
        String text = unit(units(f), "table-01-f").text();
        Check.contains(text, "    - Formula (calculated from other logical objects): ` SUM(\"%1\") ` " + Citation.of(f.bmm("BM.F.One")),
                "a one-line formula on the line, spaces kept, then its citation");
        Check.contains(text, "    - Aggregation rule (default): ` SUM(\"BM\".\"F\".\"One\")` " + Citation.of(f.bmm("BM.F.One#aggr0")),
                "the aggregation rule with its own row's citation");
        Check.contains(text, "exactly as extracted, between the two fence lines: " + Citation.of(f.bmm("BM.F.Amount"))
                + "\n```\n" + formula + "\n```\n", "a formula of several lines between fence lines, character for character");
        int mappings = 0;
        for (String line : text.split("\n")) {
            mappings += line.contains(" here is ") || line.contains("Lineage:") ? 1 : 0;
            if (line.startsWith("- Logical column") || line.contains("Shown to report authors") || line.contains(" here is ")
                    || line.contains("Lineage:")) {
                String fact = line.endsWith(" " + Packets.REQUIRED_MARK)
                        ? line.substring(0, line.length() - Packets.REQUIRED_MARK.length() - 1) : line;
                Check.that(fact.endsWith("]") && fact.contains(Citation.OPEN), "a fact line ends with a citation: " + line);
            }
        }
        Check.equal(4, mappings, "the two mapping lines and the two lineage lines were among them");
    }

    public void testALargeLogicalTableIsSplitIntoNumberedPartsOfAtMostTheLimit() {
        SliceFixture f = SliceFixture.estate("big").area();
        for (int i = 0; i < 180; i++) {
            f.column(String.format("C%03d", i), i % 2 == 0, "");
        }
        KbData slice = Slice.cut(f.build(), f.pres("SA"));
        Check.equal(120, Packets.DEFAULT_MAX_LINES, "the default limit");
        for (int limit : new int[] {Packets.DEFAULT_MAX_LINES, 400, 60}) {
            List<PacketUnit> units = limit == Packets.DEFAULT_MAX_LINES ? Packets.build(slice) : Packets.build(slice, limit);
            List<PacketUnit> parts = new ArrayList<>();
            for (PacketUnit unit : units) {
                Check.that(unit.lines.size() <= limit, unit.unitId + ": " + unit.lines.size() + " lines, limit " + limit);
                if (unit.kind.equals("logical_table")) {
                    parts.add(unit);
                }
            }
            Check.that(parts.size() >= 3, "the table needs several packets: " + parts.size());
            Set<String> required = new TreeSet<>();
            int notFull = 0;
            int firstOfSources = -1;
            for (int i = 0; i < parts.size(); i++) {
                PacketUnit part = parts.get(i);
                Check.equal("table-01-f-p" + (i + 1), part.unitId, "numbered parts");
                Check.equal(i + 1, part.part, "part number");
                Check.equal(parts.size(), part.parts, "of how many");
                Check.contains(part.lines.get(0), "(part " + (i + 1) + " of " + parts.size() + ")", "the title says which part");
                Check.that(part.lines.get(7).startsWith("This is part " + (i + 1) + " of " + parts.size() + " of what was too long for one packet"),
                        "each part stands alone: its header says which part it is: " + part.lines.get(7));
                Check.equal("Subject area: SA (estate big)", part.lines.get(3), "and repeats the whole header");
                notFull += part.lines.size() <= limit / 2 ? 1 : 0;
                if (firstOfSources < 0 && part.text().contains("\n- Source \"S\"")) {
                    firstOfSources = i;
                }
                for (String id : part.requiredRows.keySet()) {
                    Check.that(required.add(id), "a required row is in one part only: " + id);
                    Check.that(part.text().contains(Citation.of(id)), "and is cited in it: " + id);
                }
            }
            Check.that(notFull <= 2, "parts are filled before a new one starts, but for the last of the columns and the last of all: " + notFull);
            Check.equal(1 + 1 + 90 + 180, required.size(), "the table, its source, 90 measures and 180 presentation columns");
            Check.contains(parts.get(1).text(), "## Columns: what the table offers", "a part that starts inside a section repeats its heading");
            // The natural lines: the columns first, then the sources; never the middle of an item.
            Check.equal("## Table sources: where the table's data comes from", parts.get(firstOfSources).lines.get(9),
                    "the sources start a part of their own");
            Check.that(!parts.get(firstOfSources).text().contains("- Logical column \"C000\": "), "which holds no column of the table");
            for (int i = 0; i < parts.size(); i++) {
                String first = parts.get(i).lines.get(11);
                if (i <= firstOfSources) {
                    Check.that(first.startsWith("- Logical ") || first.startsWith("- Source "), "a part starts where an item starts: " + first);
                } else {
                    Check.equal("- (continued from the previous part: table source \"S\" of logical table \"F\")", first,
                            "a part that continues a source says which");
                    Check.that(parts.get(i).lines.get(12).startsWith("    - Logical column "), "and goes on with a whole mapping: " + parts.get(i).lines.get(12));
                }
                List<String> lines = parts.get(i).lines;
                String last = lines.get(lines.size() - 2);
                Check.that(last.startsWith("        - Lineage: ") || last.startsWith("    - ") || last.startsWith("- "),
                        "a part ends where an item ends: " + last);
                if (i > firstOfSources) {
                    Check.that(last.startsWith("        - Lineage: "), "a mapping is never parted from its lineage line: " + last);
                }
            }
        }
    }

    /**
     * Deliberate difference 33: one unit per physical table states the table whole. Its table, definitions,
     * keys and columns are required rows of that unit and are cited nowhere else.
     */
    public void testAPhysicalTableUnitStatesTheWholeTableAndItsRowsAreRequiredThereOnly() {
        SliceFixture f = SliceFixture.estate("db").area().column("Amount", true, "");
        f.add("physical_table_definitions", "row_id", f.phys("DB.P.def.D"), "table_id", f.phys("DB.P"), "database", "D", "text", "P_REAL");
        f.add("physical_columns", "row_id", f.phys("DB.P.MTH_KEY"), "table_id", f.phys("DB.P"), "name", "MTH_KEY", "data_type", "DOUBLE",
                "precision", "15", "scale", "2", "nullable", "false", "external_name", "ext", "position", "1");
        f.add("physical_keys", "row_id", f.phys("DB.P.key.P_Key"), "table_id", f.phys("DB.P"), "name", "P_Key", "columns", f.phys("DB.P.MTH_KEY"));
        // A second table, an alias of P, read by nothing: it is in the slice because its join to P is.
        f.add("physical_tables", "row_id", f.phys("DB.A"), "container", "DB", "name", "A", "table_type", "select", "source_table_id", f.phys("DB.P"));
        f.add("physical_columns", "row_id", f.phys("DB.A.K"), "table_id", f.phys("DB.A"), "name", "K", "position", "1");
        f.add("logical_tables", "row_id", f.bmm("BM.Dim"), "business_model", "BM", "name", "Dim");
        f.add("logical_columns", "row_id", f.bmm("BM.Dim.Code"), "table_id", f.bmm("BM.Dim"), "name", "Code", "is_measure", "false");
        f.add("presentation_columns", "row_id", f.pres("SA.T.Code"), "table_id", f.pres("SA.T"), "name", "Code", "logical_column_id", f.bmm("BM.Dim.Code"));
        f.add("logical_table_sources", "row_id", f.bmm("BM.Dim.src.DS"), "logical_table_id", f.bmm("BM.Dim"), "name", "DS",
                "group_by_text_desc", "GROUPBYLEVEL(\"BM\".\"D\".\"L\")");
        f.add("lts_column_map", "row_id", f.bmm("BM.Dim.src.DS#map0"), "source_id", f.bmm("BM.Dim.src.DS"), "logical_column_id", f.bmm("BM.Dim.Code"),
                "physical_column_id", f.phys("DB.A.K"));
        f.add("physical_joins", "row_id", f.phys("DB.A.A_FK"), "kind", "foreign_key", "name", "A_FK", "table1_id", f.phys("DB.A"),
                "table2_id", f.phys("DB.P"), "columns", f.phys("DB.A.K"), "referenced_columns", f.phys("DB.P.MTH_KEY"));
        f.add("logical_keys", "row_id", f.bmm("BM.F.key.F_Key"), "logical_table_id", f.bmm("BM.F"), "name", "F_Key", "is_primary", "true",
                "columns", f.bmm("BM.F.Amount"));
        List<PacketUnit> units = units(f);
        Check.equal(List.of("overview", "table-01-f", "table-02-dim", "dbtable-01-p", "dbtable-02-a", "joins", "hierarchies", "variables",
                "security", "reports", "gaps"), ids(units), "one unit per physical table, after the logical tables and before the joins");
        PacketUnit p = unit(units, "dbtable-01-p");
        Check.equal(List.of("# Physical table \"P\"", "Unit: dbtable-01-p", "Kind: physical_table"), p.lines.subList(0, 3), "header");
        Check.equal(Set.of(f.phys("DB.P"), f.phys("DB.P.def.D"), f.phys("DB.P.key.P_Key"), f.phys("DB.P.Amount"), f.phys("DB.P.MTH_KEY")),
                p.requiredRows.keySet(), "required: the table, its definition, its key and every column");
        String text = p.text();
        Check.contains(text, "- Physical table \"P\" in `DB` (the containers above it in the repository, outermost first; usually database "
                + "and schema); type `none`. " + Citation.of(f.phys("DB.P")) + " (REQUIRED)\n    - It is not an alias of another physical table.\n"
                + "    - Description in the repository: not recorded.\n    - Its object in the database for database type `D`: `P_REAL` " + Citation.of(f.phys("DB.P.def.D")) + " (REQUIRED)\n",
                "the table, whether it is an alias, and what it is in the database");
        Check.contains(text, "- Columns the repository lists for it: 2, of which mapped by a logical column of this slice: 1; not mapped by any: 1. Keys: 1.",
                "the counts");
        Check.contains(text, "    - Source \"S\" of logical table \"F\" (unit table-01-f) -> " + f.bmm("BM.F.src.S") + "\n", "the table sources that read it");
        Check.contains(text, "- Key \"P_Key\" of physical table \"P\": made of column(s) \"MTH_KEY\". " + Citation.of(f.phys("DB.P.key.P_Key"))
                + " (REQUIRED)\n    - Key 1 of 1 the repository declares for the table. The repository does not say whether a physical key is the "
                + "primary key; this is the only key it declares for the table. Foreign keys of this slice that point at exactly these column(s): 1.\n",
                "the key, and plainly that the repository does not call it primary");
        Check.contains(text, "- Foreign key \"A_FK\" of \"A\" (\"K\") TO this table (\"MTH_KEY\"). -> " + f.phys("DB.A.A_FK") + "\n",
                "a foreign key is pointed at, not cited: it is a required row of the joins unit");
        Check.contains(text, "- Column 1 \"MTH_KEY\": type DOUBLE; precision 15; scale 2; nullable false; external name `ext`; NOT MAPPED by any "
                + "logical column of this slice. " + Citation.of(f.phys("DB.P.MTH_KEY")) + " (REQUIRED)\n", "a column nothing maps, with everything recorded");
        Check.contains(text, "- Column not recorded \"Amount\": type DOUBLE; precision not recorded; scale not recorded; nullable not stated; "
                + "external name not recorded; MAPPED by logical column(s) \"F\".\"Amount\". " + Citation.of(f.phys("DB.P.Amount")) + " (REQUIRED)\n",
                "a mapped column; what the repository does not record is said to be not recorded");
        String a = unit(units, "dbtable-02-a").text();
        Check.contains(a, "type `select` (not a database table: it is a query kept in the repository). " + Citation.of(f.phys("DB.A"))
                + " (REQUIRED)\n    - It is an alias: another name in the repository for physical table \"P\" (unit dbtable-01-p) -> " + f.phys("DB.P"),
                "an alias says what it is an alias of");
        Check.contains(a, "    - Its SQL: not recorded. (physical_table_definitions; no Row identifier)\n", "a query with no SQL recorded says so");
        Check.contains(a, "- Keys of physical table \"A\": not recorded. The repository declares no key for it", "a table with no key says so");
        Check.contains(a, "- Foreign key \"A_FK\" FROM this table (\"K\") to \"P\" (\"MTH_KEY\"). -> ", "the same foreign key seen from its own table");

        // The logical-table packets: the key, the grain, nullable on every mapping, and a pointer to the physical unit.
        String logical = unit(units, "table-01-f").text();
        Check.contains(logical, "- Key \"F_Key\" of logical table \"F\": marked as primary key true; made of column(s) \"Amount\". "
                + Citation.of(f.bmm("BM.F.key.F_Key")) + " (REQUIRED)\n", "the logical table's key is a required row");
        Check.contains(logical, "    - Level of detail (grain): not recorded. The repository gives this source no level of detail. "
                + Citation.of(f.bmm("BM.F.src.S")) + "\n", "a source with no grain says so plainly");
        Check.contains(logical, "    - Physical tables this source is built on: 1 (the source's own list of tables is not recorded; taken from its "
                + "mappings).\n        - Reads physical table \"P\" in `DB`; type `none`. Unit dbtable-01-p states the table whole: what it is in the "
                + "database, every column, its keys. -> " + f.phys("DB.P") + "\n", "the physical table is pointed at, not cited");
        Check.contains(logical, "    - Logical column \"Amount\" here is `\"P\".\"Amount\"`; physical column \"P\".\"Amount\" (type DOUBLE, "
                + "nullable not stated). " + Citation.of(f.bmm("BM.F.src.S#mapAmount")) + "\n", "nullable is printed for every mapped field");
        Check.that(!logical.contains(Citation.of(f.phys("DB.P"))) && !logical.contains(Citation.of(f.phys("DB.P.Amount"))),
                "a required row of the physical table's unit is cited in no other packet");
        String dim = unit(units, "table-02-dim").text();
        Check.contains(dim, "- Keys of logical table \"Dim\": not recorded. The repository declares no key for it.", "a logical table with no key says so");
        Check.contains(dim, "    - Level of detail (grain; the source's group by): `GROUPBYLEVEL(\"BM\".\"D\".\"L\")` ", "a recorded grain is printed exactly");
    }

    /**
     * Deliberate differences 34 to 37 in the slice and the packets: a table a source joins and maps nothing
     * from comes whole; each join inside a source is a required row with its type; the hierarchies packet
     * gives each level's parent and what defines a parent-child dimension.
     */
    public void testTheJoinsInsideASourceAreRequiredAndAParentChildDimensionSaysWhatDefinesIt() {
        SliceFixture f = SliceFixture.estate("path").area().column("Amount", true, "");
        String source = f.bmm("BM.F.src.S");
        // The source is built on P and on R; nothing is mapped from R. R is the relationship table.
        f.add("physical_tables", "row_id", f.phys("DB.R"), "container", "DB", "name", "R", "table_type", "none", "description", "who reports to whom",
                "member_key_column_id", f.phys("DB.R.M"), "parent_key_column_id", f.phys("DB.R.A"));
        f.add("physical_columns", "row_id", f.phys("DB.R.M"), "table_id", f.phys("DB.R"), "name", "M", "nullable", "true", "position", "1");
        f.add("physical_columns", "row_id", f.phys("DB.R.A"), "table_id", f.phys("DB.R"), "name", "A", "position", "2",
                "source_column_id", f.phys("DB.P.Amount"));
        f.add("physical_joins", "row_id", f.phys("DB.R.R_FK"), "kind", "foreign_key", "name", "R_FK", "table1_id", f.phys("DB.R"),
                "table2_id", f.phys("DB.P"), "columns", f.phys("DB.R.A"), "referenced_columns", f.phys("DB.P.Amount"));
        f.add("lts_tables", "row_id", source + "#table0", "source_id", source, "position", "0", "physical_table_id", f.phys("DB.P"));
        f.add("lts_tables", "row_id", source + "#table1", "source_id", source, "position", "1", "physical_table_id", f.phys("DB.R"));
        f.add("lts_joins", "row_id", source + "#table0.join0", "source_id", source, "source_table_id", source + "#table0", "position", "0",
                "join_type", "LeftOuter", "cardinality", " ONE TO MANY", "table1_id", f.phys("DB.P"), "table2_id", f.phys("DB.R"),
                "physical_join_id", f.phys("DB.R.R_FK"));
        f.add("lts_joins", "row_id", source + "#table1.join0", "source_id", source, "source_table_id", source + "#table1", "position", "0",
                "join_type", "RightOuter", "table1_id", f.phys("DB.R"), "table2_id", f.phys("DB.P"));
        // A parent-child dimension keyed on F, with two levels in child-before-parent order.
        f.add("logical_dimensions", "row_id", f.bmm("BM.Tree"), "name", "Tree", "business_model", "BM", "is_value_based", "true", "level_count", "2");
        f.add("logical_levels", "row_id", f.bmm("BM.Tree.Detail"), "dimension_id", f.bmm("BM.Tree"), "position", "0", "name", "Detail",
                "drill_key_column_id", f.bmm("BM.F.Amount"), "parent_level_ids", f.bmm("BM.Tree.Total"));
        f.add("logical_levels", "row_id", f.bmm("BM.Tree.Total"), "dimension_id", f.bmm("BM.Tree"), "position", "1", "name", "Total",
                "child_level_ids", f.bmm("BM.Tree.Detail"));
        // The fixture's own source, logical table and column get the three new cells.
        KbData kb = f.build();
        KbRows sources = kb.table("logical_table_sources");
        sources.rows.get(0)[sources.column("closure_table_id")] = f.phys("DB.R");
        KbRows tables = kb.table("logical_tables");
        tables.rows.get(0)[tables.column("is_lookup_table")] = "true";
        KbRows columns = kb.table("logical_columns");
        columns.rows.get(0)[columns.column("descriptor_id_column_id")] = f.bmm("BM.F.Amount");

        KbData slice = Slice.cut(kb, f.pres("SA"));
        Check.that(SliceFixture.rowIds(slice).containsAll(Set.of(f.phys("DB.R"), f.phys("DB.R.M"), f.phys("DB.R.A"), source + "#table1",
                source + "#table0.join0", f.phys("DB.R.R_FK"))), "a table the source joins comes whole, with the source's joins, though nothing maps it");
        Check.equal(List.of(), SliceFixture.danglingReferences(slice, SliceFixture.rowIds(kb)), "every reference resolves inside the slice");
        List<PacketUnit> units = Packets.build(slice);
        PacketUnit table = unit(units, "table-01-f");
        Check.that(table.requiredRows.keySet().containsAll(Set.of(source + "#table0.join0", source + "#table1.join0")), "each join inside a source is a required row");
        String text = table.text();
        Check.contains(text, "- Marked in the repository as a lookup table: true.", "the lookup flag");
        Check.contains(text, "    - Its identifier is held by logical column \"F\".\"Amount\" (OBIEE's \"descriptor ID\")", "the descriptor column");
        Check.contains(text, "    - Parent-child hierarchy: this source names physical table \"R\" (unit dbtable-02-r) as the table that holds the "
                + "parent-child relationships (its closure table).", "the relationship table, cited with the source");
        Check.contains(text, "    - Physical tables this source is built on: 2 (more than one, so the source joins them; the joins follow the tables).\n"
                + "        - Reads physical table \"P\" in `DB`; type `none`. Unit dbtable-01-p states the table whole: what it is in the database, every "
                + "column, its keys. -> " + f.phys("DB.P") + " " + Citation.of(source + "#table0") + "\n"
                + "        - Reads physical table \"R\" in `DB`; type `none`. Unit dbtable-02-r states the table whole: what it is in the database, every "
                + "column, its keys. No column of this slice is mapped from it; the source still joins it. -> " + f.phys("DB.R") + " "
                + Citation.of(source + "#table1") + "\n", "the tables in the source's order; one nothing is mapped from says so");
        Check.contains(text, "    - Joins this source makes between its tables: 2 line(s) for 2 join(s).", "the count of lines and of joins");
        Check.contains(text, "        - Listed under \"P\": \"P\" joined to \"R\"; join type `LeftOuter`; cardinality ` ONE TO MANY`; on foreign key \"R_FK\": "
                + "\"R\" (\"A\") = \"P\" (\"Amount\"). -> " + f.phys("DB.R.R_FK") + " " + Citation.of(source + "#table0.join0") + " (REQUIRED)\n",
                "a join with its type and cardinality exactly as written, and the columns of its condition");
        Check.contains(text, "        - Listed under \"R\": \"R\" joined to \"P\"; join type `RightOuter`; cardinality not recorded; physical join not "
                + "recorded, so the condition is not known. " + Citation.of(source + "#table1.join0") + " (REQUIRED)\n", "what is not recorded is said so");
        Check.that(!text.contains(Citation.of(f.phys("DB.R"))) && !text.contains(Citation.of(f.phys("DB.R.R_FK"))),
                "a required row of another unit is pointed at, not cited");

        String r = unit(units, "dbtable-02-r").text();
        Check.contains(r, "    - Description in the repository: `who reports to whom` " + Citation.of(f.phys("DB.R")) + "\n", "the description");
        Check.contains(r, "    - It is the relationship table of a parent-child hierarchy (one row per member and ancestor). Member column: \"R\".\"M\"; "
                + "parent (ancestor) column: \"R\".\"A\"; distance column (steps between the two): not recorded; leaf-flag column: not recorded. "
                + Citation.of(f.phys("DB.R")) + "\n", "the four columns, each named or said to be not recorded");
        Check.contains(r, "- Column 1 \"M\": type not recorded; precision not recorded; scale not recorded; nullable true;", "nullable as the file states it");
        Check.contains(r, "- Column 2 \"A\": type not recorded; precision not recorded; scale not recorded; nullable not stated; external name not "
                + "recorded; stands for column \"P\".\"Amount\" of the aliased table; NOT MAPPED", "an absent nullable is 'not stated', never true or false");
        Check.contains(r, "- About nullable: the repository export writes the nullable attribute only as true; it never writes false.", "and the packet says what that means");

        String hierarchies = unit(units, "hierarchies").text();
        Check.contains(hierarchies, "        - Level directly above it (its parent): \"Total\"; level(s) directly below it (its children): none recorded "
                + "(a lowest level). " + Citation.of(f.bmm("BM.Tree.Detail")) + "\n", "a level says which level is above it, whatever its position");
        Check.contains(hierarchies, "    - Parent-child relationships: table source \"S\" of logical table \"F\" names physical table \"R\" as the table "
                + "that holds them (stated with its citation in unit table-01-f). -> " + source + "\n"
                + "        - In that table: Member column: \"R\".\"M\"; parent (ancestor) column: \"R\".\"A\"; distance column (steps between the two): not "
                + "recorded; leaf-flag column: not recorded (stated with its citation in unit dbtable-02-r). -> " + f.phys("DB.R") + "\n",
                "a parent-child dimension says which table and columns define it, by pointer");
        Check.that(!hierarchies.contains(Citation.of(source)) && !hierarchies.contains(Citation.of(f.phys("DB.R"))),
                "and cites neither row: both are required rows of other units");
        for (PacketUnit unit : units) {
            Check.that(unit.lines.size() <= Packets.DEFAULT_MAX_LINES, unit.unitId + ": " + unit.lines.size() + " lines");
        }
    }

    /** A table too wide for one packet is split between columns, and each column stays a required row of its part. */
    public void testAWidePhysicalTableIsSplitBetweenColumns() {
        SliceFixture f = SliceFixture.estate("wide").area().column("Amount", true, "");
        for (int i = 1; i <= 400; i++) {
            f.add("physical_columns", "row_id", f.phys("DB.P.C" + i), "table_id", f.phys("DB.P"), "name", "C" + i, "position", Integer.toString(i + 1));
        }
        int parts = 0;
        Set<String> required = new TreeSet<>();
        List<String> order = new ArrayList<>();
        for (PacketUnit unit : units(f)) {
            Check.that(unit.lines.size() <= Packets.DEFAULT_MAX_LINES, unit.unitId + ": " + unit.lines.size() + " lines");
            if (unit.kind.equals("physical_table")) {
                parts++;
                required.addAll(unit.requiredRows.keySet());
                for (String line : unit.lines) {
                    if (line.startsWith("- Column ") && line.endsWith(Packets.REQUIRED_MARK)) {
                        order.add(line.substring("- Column ".length(), line.indexOf(' ', "- Column ".length())));
                    }
                }
            }
        }
        Check.that(parts >= 4, "the table needed several packets: " + parts);
        Check.equal(402, required.size(), "the table and each of its 401 columns is required in exactly one part");
        Check.equal(401, order.size(), "one marked line per column");
        Check.equal(List.of("2", "3", "4"), order.subList(0, 3), "the columns are in the order of their place in the table, not in name order");
        Check.equal("401", order.get(399), "to the last");
        Check.equal("not", order.get(400), "a column whose place is not recorded comes after the others and says so");
    }

    public void testAForeignKeyIsStatedFromTableAndColumnsToTableAndColumns() {
        SliceFixture f = SliceFixture.estate("fk").area().column("Amount", true, "");
        f.add("physical_tables", "row_id", f.phys("DB.D"), "container", "DB", "name", "D", "table_type", "none");
        // D must be in the slice for its joins to be: a second logical table reads it.
        f.add("logical_tables", "row_id", f.bmm("BM.Dim"), "business_model", "BM", "name", "Dim");
        f.add("logical_columns", "row_id", f.bmm("BM.Dim.Code"), "table_id", f.bmm("BM.Dim"), "name", "Code", "is_measure", "false");
        f.add("presentation_columns", "row_id", f.pres("SA.T.Code"), "table_id", f.pres("SA.T"), "name", "Code", "logical_column_id", f.bmm("BM.Dim.Code"));
        f.add("logical_table_sources", "row_id", f.bmm("BM.Dim.src.DS"), "logical_table_id", f.bmm("BM.Dim"), "name", "DS");
        f.add("lts_column_map", "row_id", f.bmm("BM.Dim.src.DS#map0"), "source_id", f.bmm("BM.Dim.src.DS"), "logical_column_id", f.bmm("BM.Dim.Code"),
                "physical_column_id", f.phys("DB.D.ID1"));
        for (String column : new String[] {"DB.P.K1", "DB.P.K2", "DB.D.ID1", "DB.D.ID2"}) {
            f.add("physical_columns", "row_id", f.phys(column), "table_id", f.phys(column.substring(0, 4)), "name", column.substring(5), "data_type", "INT");
        }
        f.add("physical_joins", "row_id", f.phys("DB.P.Two"), "kind", "foreign_key", "name", "Two", "table1_id", f.phys("DB.P"),
                "table2_id", f.phys("DB.D"), "columns", f.phys("DB.P.K1") + "\n" + f.phys("DB.P.K2"),
                "referenced_columns", f.phys("DB.D.ID1") + "\n" + f.phys("DB.D.ID2"));
        f.add("physical_joins", "row_id", f.phys("DB.P.Blind"), "kind", "foreign_key", "name", "Blind", "table1_id", f.phys("DB.P"),
                "columns", f.phys("DB.P.K1"));
        f.add("physical_joins", "row_id", f.phys("DB.P.Uneven"), "kind", "foreign_key", "name", "Uneven", "table1_id", f.phys("DB.P"),
                "table2_id", f.phys("DB.D"), "columns", f.phys("DB.P.K1") + "\n" + f.phys("DB.P.K2"), "referenced_columns", f.phys("DB.D.ID1"));
        KbData slice = Slice.cut(f.build(), f.pres("SA"));
        Check.that(SliceFixture.rowIds(slice).contains(f.phys("DB.D.ID2")), "a column at the far end of a foreign key is in the slice, though nothing maps it");
        String text = unit(Packets.build(slice), "joins").text();
        Check.contains(text, "## Physical joins (between database tables of the slice)\n", "the heading no longer claims what a line may not know");
        Check.contains(text, "- foreign_key \"Two\": from \"P\" (\"K1\", \"K2\") to \"D\" (\"ID1\", \"ID2\"). " + Citation.of(f.phys("DB.P.Two")) + " (REQUIRED)\n"
                + "    - Column pair 1 of 2: \"P\".\"K1\" (type INT, nullable not stated) = \"D\".\"ID1\" (type INT, nullable not stated). " + Citation.of(f.phys("DB.P.Two")) + "\n"
                + "    - Column pair 2 of 2: \"P\".\"K2\" (type INT, nullable not stated) = \"D\".\"ID2\" (type INT, nullable not stated). " + Citation.of(f.phys("DB.P.Two")) + "\n",
                "a foreign key in full: from table and columns to table and columns, then pair by pair");
        Check.contains(text, "- foreign_key \"Blind\": from \"P\" (\"K1\") to a table the repository does not name (the foreign key has no link "
                + "to a key, or the link did not resolve). " + Citation.of(f.phys("DB.P.Blind")) + " (REQUIRED)\n"
                + "    - Foreign-key column(s), on the first table: \"P\".\"K1\" (type INT, nullable not stated). ", "a foreign key whose far side is not known says so on its own line");
        Check.contains(text, "    - The two lists differ in length, so the column pairs cannot be stated.", "no pairs are invented");
    }

    public void testEveryVariableNameUsedIsListedWithWhereAndWhetherTheRepositoryDefinesIt() {
        SliceFixture f = SliceFixture.estate("names").area()
                .column("Amount", true, "VALUEOF(NQ_SESSION.\"CUR\") * valueof( nq_session.WEBLANGUAGE ) + VALUEOF(cur_month)")
                .column("Other", false, "VALUEOF(NQ_SESSION.WEBLANGUAGE)");
        f.add("variables", "row_id", f.var("CUR"), "name", "CUR", "is_session", "true");
        f.add("variables", "row_id", f.var("Cur_Month"), "name", "Cur_Month", "is_session", "false");
        f.add("variables", "row_id", f.var("UNUSED"), "name", "UNUSED");
        KbData kb = f.build();
        KbRows presented = kb.table("presentation_columns");
        presented.rows.get(0)[presented.column("display_name")] = "VALUEOF(NQ_SESSION.CN_Amount)";
        KbData slice = Slice.cut(kb, f.pres("SA"));
        Check.equal(Set.of(f.var("CUR"), f.var("Cur_Month")), SliceFixture.rowIds(slice).stream().filter(id -> id.contains("::var::"))
                .collect(java.util.stream.Collectors.toSet()), "the variable named exactly, and the one whose name differs only in letter case");
        List<PacketUnit> units = Packets.build(slice);
        PacketUnit variables = unit(units, "variables");
        String text = variables.text();
        Check.equal(Set.of(f.var("CUR"), f.var("Cur_Month")), variables.requiredRows.keySet(), "variables in the slice are required rows; names without a row are not");
        Check.contains(text, "inside VALUEOF(...): 4.\n", "four names are used: CUR, WEBLANGUAGE, cur_month, CN_Amount");
        Check.contains(text, "(the Knowledge Base has a variables row): 2; used by name and not defined in the repository: 2.", "two of each");
        Check.contains(text, "    - Named exactly, inside VALUEOF(...), in 1 row(s) of the slice:\n        - logical_columns -> " + f.bmm("BM.F.Amount")
                + " (in derived_expr_text, derived_expr_text_desc)\n", "where a defined variable is used, as a pointer to the row");
        Check.contains(text, "    - Written in another letter case, as `cur_month`, in 1 row(s) of the slice:\n        - logical_columns -> "
                + f.bmm("BM.F.Amount"), "a name in another letter case is listed with the variable it stands for, and says so");
        Check.contains(text, "- Name `WEBLANGUAGE`: used by name, not defined in the repository. (variables; no Row identifier)\n"
                + "    - Named inside VALUEOF(...) in 2 row(s) of the slice:\n        - logical_columns -> " + f.bmm("BM.F.Amount")
                + " (in derived_expr_text, derived_expr_text_desc)\n        - logical_columns -> " + f.bmm("BM.F.Other"),
                "a name the repository does not define, with every row that uses it");
        Check.contains(text, "- Name `CN_Amount`: used by name, not defined in the repository.", "a name in a display-name formula");
        Check.contains(text, "        - presentation_columns -> " + f.pres("SA.T.Amount") + " (in display_name)", "with its pointer");
        Check.that(!text.contains("UNUSED"), "a variable nothing names is not listed");
        // The formula itself points at the unit that explains the name.
        Check.contains(unit(units, "table-01-f").text(), "        - Names variable `WEBLANGUAGE` inside VALUEOF(...): unit variables lists every such "
                + "name and says whether the repository defines it.", "under the formula that names it");
        Check.equal(List.of("LEDGER", "A B"), obi2pbi.core.Variables.namesIn("valueof(0,NQ_SESSION.LEDGER) || VALUEOF( \"A B\" ) || VALUEOF(NQ_SESSION.\"%1\") || VALUEOF(%2)"),
                "a number before the name is passed over; a placeholder of a template is not a name");
    }

    public void testEachRoleIsPrintedOnItsOwnLineWithTheNamesTheRepositoryRecords() {
        SliceFixture f = SliceFixture.estate("roles").area().column("Amount", true, "");
        String hex = "6D7874D30FD9EC2E 3FA146A018E99435";
        f.add("security_rls", "row_id", "roles::sec::obj.P1.o0", "kind", "object_privilege", "subject_id", f.pres("SA.T.Amount"),
                "detail", "write via roles::sec::pkg.Pack");
        f.add("security_rls", "row_id", "roles::sec::obj.P2.o0", "kind", "object_privilege", "subject_id", f.pres("SA.T.Amount"),
                "detail", "read via roles::sec::pkg.Bare");
        f.add("security_rls", "row_id", "roles::sec::obj.P3.o0", "kind", "object_privilege", "subject_id", f.pres("SA.T.Amount"),
                "detail", "read via roles::sec::pkg.Nobody");
        f.add("security_rls", "row_id", "roles::sec::pkg.Pack.q0", "kind", "query_privilege", "role", hex, "subject_id", "QP1", "detail", "Pack",
                "role_display_name", "BI Author Role", "role_guid", "BIAuthor");
        f.add("security_rls", "row_id", "roles::sec::pkg.Pack.q1", "kind", "query_privilege", "role", hex, "subject_id", "QP2", "detail", "Pack",
                "role_display_name", "BI Author Role", "role_guid", "BIAuthor");
        f.add("security_rls", "row_id", "roles::sec::pkg.Bare.q0", "kind", "query_privilege", "role", "", "subject_id", "QP", "detail", "Bare");
        String text = unit(units(f), "security").text();
        String role = "Role with display name `BI Author Role`; name in the repository `" + hex + "` (this is the name of one role: the space is "
                + "part of the name); guid `BIAuthor`.";
        Check.contains(text, "given through privilege package -> roles::sec::pkg.Pack. " + Citation.of("roles::sec::obj.P1.o0") + " (REQUIRED)\n"
                + "    - Held by 1 role(s), through that privilege package, one per line:\n        - " + role + " -> roles::sec::pkg.Pack.q0\n",
                "one role, though its name holds a space and the package has two query privileges");
        Check.contains(text, "    - Held by 1 role(s), through that privilege package, one per line:\n        - Role with display name not recorded in the "
                + "repository; name not recorded in the repository; guid not recorded in the repository. -> roles::sec::pkg.Bare.q0\n",
                "a role the repository gives no name says so");
        Check.contains(text, "    - Held by: no role is recorded in the slice for that privilege package.", "a package nobody holds");
        Check.contains(text, "## Roles that hold a privilege package (one role per line)\n\n- " + role + " It holds query privilege `QP1` of privilege "
                + "package `Pack` (kind query_privilege). " + Citation.of("roles::sec::pkg.Pack.q0") + " (REQUIRED)\n", "the same words in both places");
        Check.that(!text.contains("role(s) holding"), "the old wording is gone");
    }

    public void testTheGapsPacketNamesWhatWasLeftOutAndGivesTheTotals() {
        SliceFixture f = SliceFixture.estate("left").area().column("Amount", true, "");
        f.add("unparsed", "kind", "unread_attribute", "object_type", "LogicalTable", "detail", "LogicalTable@x", "occurrences", "3");
        f.add("unparsed", "kind", "unread_attribute", "object_type", "LogicalTable", "detail", "LogicalTable@y", "occurrences", "40");
        f.add("unparsed", "kind", "unread_element", "object_type", "PhysicalTable", "detail", "PhysicalKey", "occurrences", "5");
        f.add("unparsed", "kind", "unread_attribute", "object_type", "PresentationHierarchy", "detail", "H@x", "occurrences", "7");
        for (String file : new String[] {"a.xml", "b.xml"}) {
            f.add("unparsed", "kind", "unmodelled_type", "object_type", "User", "detail", file, "occurrences", "1");
        }
        f.add("unparsed", "kind", "unmodelled_type", "object_type", "QueryPrivilege", "detail", "q.xml", "occurrences", "1");
        f.add("unparsed", "kind", "unmodelled_type", "object_type", "kpi", "detail", "12 object(s), e.g. x", "occurrences", "12");
        KbData slice = Slice.cut(f.build(), f.pres("SA"));
        Check.equal("unmodelled_type | QueryPrivilege | 1 | 1\nunmodelled_type | User | 2 | 2\nunmodelled_type | kpi | 1 | 12\n"
                + "unread_attribute | PresentationHierarchy | 1 | 7", slice.fact("unparsed_not_copied_by_type"),
                "the manifest names what was left out: kind, object type, rows, occurrences");
        StringBuilder all = new StringBuilder();
        for (PacketUnit unit : Packets.build(slice)) {
            all.append(unit.kind.equals("gaps") ? unit.text() : "");
        }
        String text = all.toString();
        Check.contains(text, "- All kinds together: 3 rows of unparsed, 48 occurrences in the source.\n- Kind unread_attribute: 2 rows, 43 occurrences.\n"
                + "- Kind unread_element: 1 rows, 5 occurrences.\n", "totals per kind, computed");
        Check.contains(text, "- unread_attribute in LogicalTable: 2 row(s), 43 occurrences in all; each with how often it occurs "
                + "(several to a line, \"; \" between them):\n    - LogicalTable@x (x3); LogicalTable@y (x40)\n", "and per kind of object");
        Check.contains(text, "    - Left out, kind unmodelled_type (a whole type of object that has no reader; nothing of such an object is in the "
                + "Knowledge Base): 3 object type(s), 4 row(s), 15 object(s) in all; per object type:\n"
                + "        - `QueryPrivilege`: 1 row(s), 1 object(s)\n        - `User`: 2 row(s), 2 object(s)\n        - `kpi`: 1 row(s), 12 object(s)\n",
                "the object types with no reader are named, with counts");
        Check.contains(text, "    - Left out, kind unread_attribute: 1 object type(s), 1 row(s), 7 occurrences in all; per object type:\n"
                + "        - `PresentationHierarchy`: 1 row(s), 7 occurrences\n", "and so is what is about kinds of object the slice does not hold");
    }

    public void testAValueLongerThanAPacketIsCutBetweenFenceLines() {
        StringBuilder sql = new StringBuilder("select");
        for (int i = 0; i < 900; i++) {
            sql.append("\n  column_").append(i).append(",");
        }
        SliceFixture f = SliceFixture.estate("long").area().column("Amount", true, "");
        f.add("physical_table_definitions", "row_id", f.phys("DB.P.def.D"), "table_id", f.phys("DB.P"), "database", "D", "text", sql.toString());
        List<PacketUnit> units = units(f);
        StringBuilder joined = new StringBuilder();
        int parts = 0;
        for (PacketUnit unit : units) {
            Check.that(unit.lines.size() <= Packets.DEFAULT_MAX_LINES, unit.unitId + ": " + unit.lines.size() + " lines");
            // The SQL of a physical table is stated in the table's own unit (it was in the logical table's
            // unit until difference 33).
            if (unit.kind.equals("physical_table")) {
                parts++;
                int fences = 0;
                for (String line : unit.lines) {
                    fences += line.equals("```") ? 1 : 0;
                    if (line.startsWith("  column_") || line.equals("select")) {
                        joined.append(line).append('\n');
                    }
                }
                Check.equal(0, fences % 2, unit.unitId + ": every fence that opens also closes");
            }
        }
        Check.that(parts >= 3, "the value needed several packets: " + parts);
        Check.equal(sql + "\n", joined.toString(), "put together again, the value is exactly what was extracted");
    }

    /**
     * A skill is not told which kinds of line matter: the packet marks the one line that states each required
     * row. Checked on the hand-made fixture and on every packet of the sample estates.
     */
    public void testEachRequiredRowIsMarkedOnExactlyOneLineOfItsPacket() throws Exception {
        SliceFixture f = SliceFixture.estate("marked").area().column("Amount", true, "").column("Name", false, "");
        List<PacketUnit> units = new ArrayList<>(units(f));
        PacketUnit table = unit(units, "table-01-f");
        Check.contains(table.text(), "- Logical column \"Amount\": MEASURE; writeable not recorded; mapped in 1 of 1 table sources. "
                + Citation.of(f.bmm("BM.F.Amount")) + " (REQUIRED)\n", "a measure's line is marked");
        Check.contains(table.text(), Citation.of(f.bmm("BM.F.Name")) + "\n", "a column that is not required is not");
        Check.equal("Rows of this packet a document must cover: 0", unit(units, "gaps").lines.get(5), "a packet with none says only the count");
        int packets = 0;
        for (String estate : new String[] {"sampleapp", "bi-analytics-2013"}) {
            Path kb = TestSupport.root().resolve("kb").resolve(estate);
            if (!java.nio.file.Files.isRegularFile(kb.resolve("manifest.csv"))) {
                continue;
            }
            Path out = TestSupport.work("marked-" + estate);
            Check.that(SliceFixture.slice(kb, out, "--all").exit != 2, "slice " + estate);
            try (java.util.stream.Stream<Path> areas = java.nio.file.Files.list(out)) {
                for (Path area : areas.sorted().collect(java.util.stream.Collectors.toList())) {
                    KbRows required = SliceFixture.read(area).table("required_rows");
                    Map<String, Set<String>> byUnit = new java.util.TreeMap<>();
                    for (String[] row : required.rows) {
                        byUnit.computeIfAbsent(required.get(row, "unit_id"), u -> new java.util.TreeSet<>()).add(required.get(row, "row_id"));
                    }
                    try (java.util.stream.Stream<Path> files = java.nio.file.Files.list(area.resolve("packets"))) {
                        for (Path packet : files.sorted().collect(java.util.stream.Collectors.toList())) {
                            String unitId = packet.getFileName().toString().replace(".md", "");
                            Set<String> marked = new java.util.TreeSet<>();
                            int lines = 0;
                            for (String line : java.nio.file.Files.readAllLines(packet)) {
                                if (!line.endsWith("] " + Packets.REQUIRED_MARK)) {
                                    continue;
                                }
                                lines++;
                                List<Citation> cited = Citation.find(line, 1);
                                marked.add(cited.get(cited.size() - 1).rowId);
                            }
                            Set<String> expected = byUnit.getOrDefault(unitId, Set.of());
                            Check.equal(expected, marked, "the rows marked in " + packet);
                            Check.equal(expected.size(), lines, "one marked line per required row in " + packet);
                            packets++;
                        }
                    }
                }
            }
        }
        if (packets == 0) {
            throw new SkipException("checked on the fixture only: no sample Knowledge Base under kb/");
        }
    }

    public void testReportColumnsThatAreCalculationsAreRequiredAndPlainOnesAreNot() {
        SliceFixture f = SliceFixture.estate("calc").area().column("Amount", true, "");
        String report = f.cat("shared/r");
        f.add("analyses", "row_id", report, "name", "r", "catalog_path", "shared/r", "criteria_count", "1", "column_count", "5");
        f.add("analysis_criteria", "row_id", report + "#criteria0", "analysis_id", report, "position", "0", "subject_area", "\"SA\"");
        String[][] columns = {{"0", "\"T\".\"Amount\"", "false"}, {"1", "\"T\".\"Amount\" * 2", "false"}, {"2", "", "true"},
                {"3", "  \"SA\".\"T\".\"Amount\"  ", "false"}, {"4", "RANK(\"T\".\"Amount\")\nBY 1", "false"}, {"5", "", "false"}};
        for (String[] column : columns) {
            f.add("analysis_columns", "row_id", report + "#c0." + column[0], "analysis_id", report, "criteria_id", report + "#criteria0",
                    "position", column[0], "formula", column[1], "is_computation", column[2]);
        }
        List<PacketUnit> units = units(f);
        PacketUnit reports = unit(units, "reports-01-shared");
        Check.equal("Reports in catalog folder \"shared\" that no dashboard in the slice places", reports.title, "a report on no dashboard is grouped by folder");
        Check.equal(Set.of(report + "#c0.1", report + "#c0.2", report + "#c0.4"), reports.requiredRows.keySet(),
                "a formula that is more than one column reference, or a column the source types as a computation");
        Check.contains(reports.text(), "Column 1: CALCULATION; formula: `\"T\".\"Amount\" * 2` " + Citation.of(report + "#c0.1") + " (REQUIRED)", "marked and cited");
        Check.contains(reports.text(), "Column 0: plain column; formula: `\"T\".\"Amount\"` " + Citation.of(report + "#c0.0"), "a plain column is described too");
        Check.contains(reports.text(), "RANK(\"T\".\"Amount\")\nBY 1\n```", "a formula of several lines, exactly");
    }

    public void testPacketsAreDerivedFromTheSlicesOwnTablesAlone() throws Exception {
        Path work = TestSupport.work("packets-derived");
        Path out = work.resolve("slices");
        Check.equal(0, SliceFixture.slice(SliceTest.pairKb(work), out, "--all").exit, "slice");
        Path folder = out.resolve("constructed-sa");
        // Built again from nothing but the folder, the packets are the same bytes.
        List<PacketUnit> rebuilt = Packets.build(SliceFixture.read(folder));
        Map<String, String> written = SliceFixture.packets(folder);
        Check.equal(new ArrayList<>(written.keySet()), ids(rebuilt), "the same units in the same order");
        for (PacketUnit unit : rebuilt) {
            Check.equal(written.get(unit.unitId), unit.text(), unit.unitId);
        }
        List<String> files = new ArrayList<>();
        for (String id : written.keySet()) {
            files.add(id + ".md");
        }
        files.sort(null);
        Check.equal(files, TestSupport.fileNames(folder.resolve("packets")), "one packet file per unit and no other");
        int[] numbers = SliceFixture.checkPackets(folder, "pair");
        // Until difference 33: 9 units and 7 required rows. The two physical tables are units of their own now,
        // and each table and each of its columns is a required row.
        Check.equal(11, numbers[0], "units");
        Check.that(Files.size(folder.resolve("required_rows.csv")) > 0 && numbers[2] == 13, "required rows: " + numbers[2]);
    }

    public void testEverySliceTableAndColumnIsDescribed() {
        for (KbTable table : SliceSchema.sliceTables()) {
            Check.that(table.description.length() > 20, table.name + ": a real description");
            Check.that(table.rowMeaning.length() > 10, table.name + ": a real row meaning");
            for (KbColumn column : table.columns) {
                Check.that(column.meaning.length() > 10, table.name + "." + column.name + ": a real meaning");
            }
        }
        for (KbColumn column : SliceSchema.check().columns) {
            Check.that(column.meaning.length() > 10, "check." + column.name + ": a real meaning");
        }
        Check.equal(List.of("unit_id", "position", "kind", "title", "part", "parts", "packet_file", "line_count", "required_rows"),
                SliceSchema.units().header(), "units.csv columns");
        Check.equal(List.of("row_id", "table", "unit_id"), SliceSchema.requiredRows().header(), "required_rows.csv columns");
    }

    public void testIsCalculation() {
        Check.that(!Packets.isCalculation(Map.of("is_computation", "false", "formula", "\"A\".\"B\"")), "one column reference");
        Check.that(Packets.isCalculation(Map.of("is_computation", "false", "formula", "COUNT(DISTINCT \"A\".\"B\")")), "a function");
        Check.that(Packets.isCalculation(Map.of("is_computation", "true", "formula", "")), "typed as a computation");
        Check.that(!Packets.isCalculation(Map.of("is_computation", "false", "formula", "")), "no formula");
    }

    public void testAnIdentifierOfAnElementWhoseContentIsReadIsListedApartInTheGapsPacket() {
        SliceFixture fixture = SliceFixture.estate("e").area().column("Amount", true, "");
        fixture.add("unparsed", "kind", "unread_attribute", "object_type", "LogicalTableSource", "detail", "WhereClause@mdsid",
                "occurrences", "17");
        fixture.add("unparsed", "kind", "unread_attribute", "object_type", "LogicalTableSource", "detail", "WhereClause@name",
                "occurrences", "17");
        fixture.add("unparsed", "kind", "unread_attribute", "object_type", "LogicalTableSource", "detail",
                "LogicalTableSource@isSysGen", "occurrences", "3");
        java.nio.file.Path work = TestSupport.work("packets-identifiers");
        java.nio.file.Path kb = fixture.write(work.resolve("kb"));
        Check.equal(0, SliceFixture.slice(kb, work.resolve("slices"), "--all").exit, "slice");
        java.nio.file.Path folder = work.resolve("slices/sa");
        String gaps = SliceFixture.text(folder.resolve("packets/gaps.md"));
        Check.contains(gaps, "    - @mdsid, identifier of WhereClause (x17): identifier of each not read; its content is read.",
                "the identifiers, apart, by attribute");
        Check.contains(gaps, "    - @name, identifier of WhereClause (x17): identifier of each not read; its content is read.",
                "one line per attribute");
        Check.contains(gaps, "    - LogicalTableSource@isSysGen (x3)", "content not read stays in the main list");
        Check.that(!gaps.contains("- WhereClause@mdsid (x17); "), "an identifier is not listed as content");
        Check.contains(gaps, "- All kinds together: 3 rows of unparsed", "the totals still count every row");
        Check.equal("WhereClause@mdsid\nWhereClause@name",
                SliceFixture.read(folder).fact(obi2pbi.core.Packets.IDENTIFIERS_FACT), "named in the manifest");
        Check.that(obi2pbi.in.mds.IdentifierAttributes.isIdentifierOnly("RefLogicalColumn@refId"), "a reference's id");
        Check.that(!obi2pbi.in.mds.IdentifierAttributes.isIdentifierOnly("DBMapItem@name"), "a name that is content");
        Check.that(!obi2pbi.in.mds.IdentifierAttributes.isIdentifierOnly("{com.siebel.analytics.web/report/v1.1}view@name"),
                "a catalog attribute");
    }
}
