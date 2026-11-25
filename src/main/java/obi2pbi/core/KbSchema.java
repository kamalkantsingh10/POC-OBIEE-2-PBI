package obi2pbi.core;

import java.util.ArrayList;
import java.util.List;

/**
 * Every Knowledge Base table, declared once, in write order.
 *
 * <p><b>To add a table:</b> write one {@code static KbTable} method below and add it to {@link #TABLES}.
 * Nothing else needs to change: the writer, {@code schema.csv}, {@code schema_tables.csv}, the manifest and
 * the shape tests all read this list. Every description, row meaning and column meaning must be a real
 * sentence; a test fails on an empty one.
 */
public final class KbSchema {

    public static final String MANIFEST = "manifest";
    public static final String SCHEMA_TABLES = "schema_tables";
    public static final String SCHEMA_COLUMNS = "schema";

    private static final String ESTATE_MEANING =
            "The estate this row belongs to: the name of the estate folder that was extracted.";
    private static final String ESTATE_NOTE =
            "Named rpd_id for compatibility with the first Knowledge Base; it holds the estate name.";
    private static final String VERBATIM = "Kept exactly as the source wrote it; nothing is trimmed.";
    private static final String TEMPLATE_NOTE = VERBATIM + " Leading and trailing spaces are real. May start "
            + "with '-' or '=' and may span several lines. %1, %2 ... stand for the entries of the matching "
            + "_refs column, in order.";
    private static final String RENDERED_NOTE = VERBATIM + " Written by OBIEE from the template; use it to "
            + "read the formula, and the _refs column to follow it.";
    private static final String REFS_NOTE = "One Row identifier per line, in placeholder order: line 1 is %1. "
            + "Split on the line feed only; identifiers contain spaces. An empty line is a reference that "
            + "did not resolve (errors.csv has an unresolved_reference row); a cell that is wholly empty is "
            + "either no references or a single one that did not resolve. The entries point at different "
            + "tables, so look an identifier up by its layer part and its shape.";

    /** All tables in write order. The manifest is last because it lists the others. */
    public static final List<KbTable> TABLES = List.of(
            physicalTables(),
            physicalTableDefinitions(),
            physicalColumns(),
            physicalJoins(),
            physicalKeys(),
            logicalTables(),
            logicalColumns(),
            logicalKeys(),
            aggregationRules(),
            logicalTableSources(),
            columnMappings(),
            sourceTables(),
            sourceJoins(),
            bmmJoins(),
            logicalDimensions(),
            logicalLevels(),
            logicalLevelKeys(),
            subjectAreas(),
            presentationTables(),
            presentationColumns(),
            presentationHierarchies(),
            presentationLevels(),
            presentationLevelColumns(),
            lineage(),
            variables(),
            initBlocks(),
            securityRules(),
            dashboards(),
            dashboardPages(),
            dashboardContents(),
            analyses(),
            analysisCriteria(),
            analysisColumns(),
            analysisViews(),
            analysisFilters(),
            analysisPrompts(),
            savedFilters(),
            reportLineage(),
            unparsed(),
            errors(),
            coverage(),
            catalogVersions(),
            coverageSummary(),
            schemaTables(),
            schemaColumns(),
            manifest());

    private KbSchema() {
    }

    public static KbTable table(String name) {
        for (KbTable table : TABLES) {
            if (table.name.equals(name)) {
                return table;
            }
        }
        return null;
    }

    private static KbColumn estate() {
        return KbColumn.of("rpd_id", ESTATE_MEANING).note(ESTATE_NOTE);
    }

    // ------------------------------------------------------------------ physical layer

    private static KbTable physicalTables() {
        return new KbTable("physical_tables", KbTable.PHYS,
                "The database tables the repository knows about, including aliases and tables that are "
                        + "really SQL statements.",
                "One physical table object in the repository.",
                true,
                List.of(
                        KbColumn.key("row_id", "Row identifier: <estate>::phys::<container>.<table name>."),
                        estate(),
                        KbColumn.of("container",
                                "The containers above the table, outermost first, joined with a dot; "
                                        + "usually <database>.<schema>."),
                        KbColumn.of("name", "The table's name as the repository shows it.")
                                .note("This is the repository's name. The name of the object in the database "
                                        + "can differ; see physical_table_definitions."),
                        KbColumn.of("table_type",
                                "The kind of table as the source gives it; 'select' means the table is an SQL "
                                        + "statement (a view defined inside the repository), not a database "
                                        + "table.")
                                .note("Read this before using physical_table_definitions.text: it says whether "
                                        + "that text is SQL or an object name."),
                        KbColumn.of("max_conn", "The table's maximum-connections setting, as the source gives it."),
                        KbColumn.of("source_table_id",
                                "For an alias, the table it stands for; empty when the table is not an alias.")
                                .references("physical_tables", "row_id")
                                .note("One hop only: an alias of an alias is not followed. Also empty when the "
                                        + "link did not resolve; errors.csv then has a row for this table."),
                        KbColumn.of("description", "The description the repository keeps for the table; "
                                + "empty when it has none.")
                                .note(VERBATIM + " Not in the first attempt's Knowledge Base (deliberate "
                                        + "difference 37)."),
                        KbColumn.of("member_key_column_id", "Filled only on the relationship table of a "
                                + "parent-child hierarchy: the column that holds a member.")
                                .references("physical_columns", "row_id")
                                .note(PARENT_CHILD_NOTE),
                        KbColumn.of("parent_key_column_id", "On such a table: the column that holds an "
                                + "ancestor of the member (its parent, its parent's parent, and so on).")
                                .references("physical_columns", "row_id")
                                .note(PARENT_CHILD_NOTE),
                        KbColumn.of("distance_column_id", "On such a table: the column that holds how many "
                                + "steps the ancestor is above the member.")
                                .references("physical_columns", "row_id")
                                .note(PARENT_CHILD_NOTE),
                        KbColumn.of("leaf_column_id", "On such a table: the column that says whether the "
                                + "member is a leaf (has no members below it).")
                                .references("physical_columns", "row_id")
                                .note(PARENT_CHILD_NOTE)),
                estate -> {
                    List<String[]> rows = new ArrayList<>();
                    for (PhysicalTable t : estate.physicalTables) {
                        rows.add(new String[] {t.rowId, t.rpdId, t.container, t.name, t.tableType, t.maxConn,
                                t.sourceTableId, t.description, t.memberKeyColumnId, t.parentKeyColumnId,
                                t.distanceColumnId, t.leafColumnId});
                    }
                    return rows;
                });
    }

    private static final String PARENT_CHILD_NOTE = "A parent-child hierarchy (logical_dimensions.is_value_based) "
            + "is answered from a relationship table with one row per member and ancestor. The table source "
            + "that uses it names it in logical_table_sources.closure_table_id. What each of the four columns "
            + "holds is stated here from OBIEE's names for them (member key, parent key, distance, leaf); the "
            + "repository gives the link and nothing more. Empty on every other table, and when the link did "
            + "not resolve (no error row is written). Not in the first attempt's Knowledge Base (deliberate "
            + "difference 36).";

    private static KbTable physicalTableDefinitions() {
        return new KbTable("physical_table_definitions", KbTable.PHYS,
                "What each physical table stands for in the database: the SQL of a table of type 'select', or "
                        + "the database object's name for any other type.",
                "One definition of one physical table for one database type.",
                true,
                List.of(
                        KbColumn.key("row_id",
                                "Row identifier: <table row_id>.def.<database>, or .def.unnamed-<n> when the "
                                        + "source names no database type.")
                                .note("Two definitions of one table for the same database type share a row_id; "
                                        + "errors.csv then has a duplicate_identifier row."),
                        estate(),
                        KbColumn.of("table_id", "The physical table this definition belongs to.")
                                .references("physical_tables", "row_id"),
                        KbColumn.of("database", "The database type the definition applies to, as the source names it."),
                        KbColumn.of("text",
                                "The SQL statement when the table's table_type is 'select'; otherwise the name "
                                        + "of the object in the database.")
                                .note(VERBATIM + " The SQL is not parsed, so the tables it reads are not "
                                        + "recorded. May span several lines.")),
                estate -> {
                    List<String[]> rows = new ArrayList<>();
                    for (PhysicalTableDefinition d : estate.physicalTableDefinitions) {
                        rows.add(new String[] {d.rowId, d.rpdId, d.tableId, d.database, d.text});
                    }
                    return rows;
                });
    }

    private static KbTable physicalColumns() {
        return new KbTable("physical_columns", KbTable.PHYS,
                "The columns of the physical tables.",
                "One column of one physical table.",
                true,
                List.of(
                        KbColumn.key("row_id", "Row identifier: <table row_id>.<column name>."),
                        estate(),
                        KbColumn.of("table_id", "The physical table the column belongs to.")
                                .references("physical_tables", "row_id"),
                        KbColumn.of("name", "The column's name."),
                        KbColumn.of("data_type", "The data type as the source gives it, for example VARCHAR or DOUBLE."),
                        KbColumn.of("special_type", "The source's special-type marker for the column; usually 'none'."),
                        KbColumn.of("precision", "The precision (length) as the source gives it; empty when not recorded."),
                        KbColumn.of("nullable",
                                "Whether the column may be empty, as the source gives it: 'true', or empty "
                                        + "when the source does not carry the attribute.")
                                .note("In both sample repositories the attribute is written only as 'true' "
                                        + "(635 columns) and is absent on the rest (461 columns); 'false' "
                                        + "never occurs. So the export appears to write it only when it is "
                                        + "true, and an absent attribute most likely means the column is "
                                        + "not nullable in the repository. The files do not state that, so "
                                        + "the cell is left empty and nothing is filled in. The repository's "
                                        + "setting is also not proof of the constraint in the database."),
                        KbColumn.of("external_name", "The column's external name in the source, when it has one."),
                        KbColumn.of("scale", "The scale (the number of digits after the decimal point) as the "
                                + "source gives it; empty when not recorded.")
                                .note("Empty means the source says nothing; it does not mean 0. Not in the "
                                        + "first attempt's Knowledge Base (deliberate difference 31)."),
                        KbColumn.of("position", "The column's place among the table's columns in the "
                                + "repository, counting from 1; a whole number.")
                                .note("The order the repository lists the columns in. Sort a table's columns "
                                        + "by this number to get that order: the rows of this table are in "
                                        + "Row identifier order. It is not proof of the order in the "
                                        + "database. Numbers can have gaps (a column left out for having no "
                                        + "name still takes its place). Not in the first attempt's Knowledge "
                                        + "Base (deliberate difference 31)."),
                        KbColumn.of("source_column_id", "For a column of an alias table, the column of the "
                                + "aliased table it stands for; empty for any other column.")
                                .references("physical_columns", "row_id")
                                .note("Also empty when the source gives an alias column no such link (the "
                                        + "sampleapp repository has no alias; bi-analytics-2013 has the link "
                                        + "on 101 columns), or the link did not resolve (no error row). Not "
                                        + "in the first attempt's Knowledge Base (deliberate difference "
                                        + "37).")),
                estate -> {
                    List<String[]> rows = new ArrayList<>();
                    for (PhysicalColumn c : estate.physicalColumns) {
                        rows.add(new String[] {c.rowId, c.rpdId, c.tableId, c.name, c.dataType, c.specialType,
                                c.precision, c.nullable, c.externalName, c.scale, Integer.toString(c.position),
                                c.sourceColumnId});
                    }
                    return rows;
                });
    }

    private static KbTable physicalJoins() {
        return new KbTable("physical_joins", KbTable.PHYS,
                "The joins between physical tables. Two kinds share the table: a foreign key declared on a "
                        + "table, and a complex join between two tables with a condition of its own.",
                "One foreign key of one physical table, or one complex join.",
                true,
                columns(
                        KbColumn.key("row_id", "Row identifier: <table row_id>.<foreign key name> for a "
                                + "foreign key (.fk when it has no name); <estate>::phys::joins.<join name> "
                                + "for a complex join.")
                                .note("A foreign key and a column of the same table with the same name have "
                                        + "the same identifier text; they are rows of different tables. "
                                        + "'joins' in a complex join's identifier is a fixed word, not a "
                                        + "database or schema."),
                        estate(),
                        KbColumn.of("kind", "'foreign_key' or 'complex_join'.")
                                .note("Read kind first: it decides which of the other columns are filled."),
                        KbColumn.of("name", "The foreign key's or the join's name in the source."),
                        KbColumn.of("table1_id", "For a foreign key, the table the key is declared on; for a "
                                + "complex join, the first of its two tables.")
                                .references("physical_tables", "row_id")
                                .note("For a complex join, empty when the table was not found; no error row "
                                        + "is written for that."),
                        KbColumn.of("table2_id", "For a complex join, the second of its two tables; for a "
                                + "foreign key, the table it points at: the table that holds the key the "
                                + "foreign key refers to.")
                                .references("physical_tables", "row_id")
                                .note("For a foreign key, empty when the source names no key or the key was "
                                        + "not found (then errors.csv has an unresolved_reference row for "
                                        + "the foreign key). The first attempt's Knowledge Base left it "
                                        + "empty for every foreign key (deliberate difference 26)."),
                        KbColumn.of("cardinality", "For a complex join, the cardinality as the source writes "
                                + "it, for example 'ONE TO ONE'; empty for a foreign key.")
                                .note("Neither kind carries a join type (inner or outer); only logical joins "
                                        + "do, in bmm_joins."),
                        expression("expr_", "the join condition of a complex join (always empty for a "
                                + "foreign key)"),
                        KbColumn.of("columns", "For a foreign key, the key's own columns as Row identifiers "
                                + "of physical_columns, in source order; always empty for a complex join.")
                                .note("One Row identifier per line; split on the line feed only. An empty "
                                        + "line is a column that was not found. These are the columns of the "
                                        + "table in table1_id; the columns they point at are in "
                                        + "referenced_columns."),
                        KbColumn.of("referenced_columns", "For a foreign key, the columns of the key it "
                                + "points at, as Row identifiers of physical_columns: columns of the table "
                                + "in table2_id. Always empty for a complex join.")
                                .note("One Row identifier per line, in the source order of the referenced "
                                        + "key; split on the line feed only. Line n pairs with line n of "
                                        + "columns: the join is columns[n] = referenced_columns[n]. An empty "
                                        + "line is a column that was not found; an empty cell on a foreign "
                                        + "key row means the key it points at was not found. Not in the "
                                        + "first attempt's Knowledge Base (deliberate difference 26).")),
                estate -> {
                    List<String[]> rows = new ArrayList<>();
                    for (PhysicalJoin j : estate.physicalJoins) {
                        rows.add(row(j.rowId, j.rpdId, j.kind, j.name, j.table1Id, j.table2Id, j.cardinality,
                                cells(j.expression), String.join("\n", j.columns),
                                String.join("\n", j.referencedColumns)));
                    }
                    return rows;
                });
    }

    private static final String KEY_COLUMNS_NOTE = "One Row identifier per line, in source order; split on the line feed "
                                        + "only. An empty line is a column that was not found; a cell that is "
                                        + "wholly empty is a key that lists no column, or one column that was "
                                        + "not found.";
    private static final String AS_WRITTEN_FLAG_NOTE = "'true', 'false', or empty when the source does not carry "
            + "the flag. Empty means the source says nothing; whether that amounts to 'false' is not recorded.";

    private static final String LEVEL_LIST_NOTE = "One Row identifier per line; split on the line feed only. Not in "
            + "the first attempt's Knowledge Base (deliberate difference 35).";

    private static KbTable sourceTables() {
        return new KbTable("lts_tables", KbTable.BMM,
                "The physical tables each logical table source is built on, as the source itself lists "
                        + "them. A source with more than one joins them; see lts_joins. Not in the first "
                        + "attempt's Knowledge Base (deliberate difference 34).",
                "One physical table of one table source.",
                true,
                List.of(
                        KbColumn.key("row_id", "Row identifier: <source row_id>#table<n>, n being position."),
                        estate(),
                        KbColumn.of("source_id", "The table source.")
                                .references("logical_table_sources", "row_id"),
                        KbColumn.of("position", "The table's place in the source's list, counting from 0; a "
                                + "whole number.")
                                .note("Source order. The source does not say that it is the order in which "
                                        + "the tables are joined."),
                        KbColumn.of("physical_table_id", "The physical table.")
                                .references("physical_tables", "row_id")
                                .note("Empty when the link did not resolve; no error row is written. The "
                                        + "tables a source maps columns from (lts_column_map) are normally "
                                        + "among these; a table listed here from which no column is mapped "
                                        + "is still joined, and an inner join to it still drops rows.")),
                estate -> {
                    List<String[]> rows = new ArrayList<>();
                    for (SourceTable t : estate.sourceTables) {
                        rows.add(new String[] {t.rowId, t.rpdId, t.sourceId, Integer.toString(t.position),
                                t.physicalTableId});
                    }
                    return rows;
                });
    }

    private static KbTable sourceJoins() {
        return new KbTable("lts_joins", KbTable.BMM,
                "The joins each logical table source makes between its physical tables, with the join "
                        + "type. This is where an inner or outer join inside a source is recorded: it "
                        + "decides which rows a measure counts, and no formula shows it. Not in the first "
                        + "attempt's Knowledge Base (deliberate difference 34).",
                "One join of one table source, as listed under one of the two tables it joins.",
                true,
                List.of(
                        KbColumn.key("row_id", "Row identifier: <lts_tables row_id>.join<n>, n being "
                                + "position.")
                                .note("The source lists a join under each of the tables it joins, so a "
                                        + "join between A and B is normally two rows: one under A and one "
                                        + "under B, with the cardinality and an outer join's side stated "
                                        + "from that table's point of view. Rows with the same "
                                        + "physical_join_id are the same join."),
                        estate(),
                        KbColumn.of("source_id", "The table source that makes the join.")
                                .references("logical_table_sources", "row_id"),
                        KbColumn.of("source_table_id", "The table of the source this join is listed under.")
                                .references("lts_tables", "row_id"),
                        KbColumn.of("position", "The join's place among the joins listed under that table, "
                                + "counting from 0; a whole number."),
                        KbColumn.of("join_type", "The join type as the source writes it, for example "
                                + "'Inner', 'LeftOuter' or 'RightOuter'.")
                                .note(VERBATIM + " Left and right refer to table1_id and table2_id of this "
                                        + "row: 'LeftOuter' keeps every row of table1_id. Empty when the "
                                        + "source gives none."),
                        KbColumn.of("cardinality", "The cardinality as the source writes it, for example "
                                + "'MANY TO ONE'.")
                                .note(VERBATIM + " The samples write ' ONE TO MANY' with a leading space. "
                                        + "It reads from table1_id to table2_id."),
                        KbColumn.of("table1_id", "The first table of the join as this row states it.")
                                .references("physical_tables", "row_id")
                                .note("In the samples it is the table the row is listed under. Empty when "
                                        + "the link did not resolve."),
                        KbColumn.of("table2_id", "The second table of the join as this row states it.")
                                .references("physical_tables", "row_id")
                                .note("Empty when the link did not resolve."),
                        KbColumn.of("physical_join_id", "The physical join that gives the join its "
                                + "condition: a foreign key or a complex join.")
                                .references("physical_joins", "row_id")
                                .note("The columns or the condition are on that row. Empty when the source "
                                        + "names no physical join or the link did not resolve; no error "
                                        + "row is written.")),
                estate -> {
                    List<String[]> rows = new ArrayList<>();
                    for (SourceJoin j : estate.sourceJoins) {
                        rows.add(new String[] {j.rowId, j.rpdId, j.sourceId, j.sourceTableId,
                                Integer.toString(j.position), j.joinType, j.cardinality, j.table1Id, j.table2Id,
                                j.physicalJoinId});
                    }
                    return rows;
                });
    }

    private static KbTable physicalKeys() {
        return new KbTable("physical_keys", KbTable.PHYS,
                "The keys declared on physical tables: each names the column or columns whose values "
                        + "identify a row of the table. A foreign key of another table points at one of "
                        + "these. Not in the first attempt's Knowledge Base (deliberate difference 28).",
                "One key of one physical table.",
                true,
                List.of(
                        KbColumn.key("row_id", "Row identifier: <table row_id>.key.<key name> "
                                + "(.key.unnamed-<n> for the n-th key of the table when it has no name).")
                                .note("'key' is a fixed word, so that a key and a column of the same name "
                                        + "have different identifiers."),
                        estate(),
                        KbColumn.of("table_id", "The physical table the key is declared on.")
                                .references("physical_tables", "row_id"),
                        KbColumn.of("name", "The key's name in the repository.")
                                .note("The repository's name; it need not be the name of a constraint in the "
                                        + "database."),
                        KbColumn.of("columns", "The key's columns, as Row identifiers of physical_columns: "
                                + "columns of the table in table_id.")
                                .note(KEY_COLUMNS_NOTE + " The repository has no flag that says whether a "
                                        + "physical key is the table's primary key, so this table has no "
                                        + "such column: a table with one key has one candidate, a table "
                                        + "with several has several. A foreign key that points at this key "
                                        + "has the same list in physical_joins.referenced_columns.")),
                estate -> {
                    List<String[]> rows = new ArrayList<>();
                    for (PhysicalKey k : estate.physicalKeys) {
                        rows.add(new String[] {k.rowId, k.rpdId, k.tableId, k.name, String.join("\n", k.columns)});
                    }
                    return rows;
                });
    }

    private static KbTable logicalKeys() {
        return new KbTable("logical_keys", KbTable.BMM,
                "The keys declared on logical tables: each names the logical column or columns whose "
                        + "values identify a row of the logical table. Not in the first attempt's Knowledge "
                        + "Base (deliberate difference 29).",
                "One key of one logical table.",
                true,
                List.of(
                        KbColumn.key("row_id", "Row identifier: <logical table row_id>.key.<key name> "
                                + "(.key.unnamed-<n> when the key has no name).")
                                .note("'key' is a fixed word, so that a key and a column of the same name "
                                        + "have different identifiers."),
                        estate(),
                        KbColumn.of("logical_table_id", "The logical table the key is declared on.")
                                .references("logical_tables", "row_id"),
                        KbColumn.of("name", "The key's name in the repository."),
                        KbColumn.of("is_primary", "Whether the source marks the key as the table's primary "
                                + "key, as written.")
                                .note(AS_WRITTEN_FLAG_NOTE),
                        KbColumn.of("columns", "The key's columns, as Row identifiers of logical_columns.")
                                .note(KEY_COLUMNS_NOTE)),
                estate -> {
                    List<String[]> rows = new ArrayList<>();
                    for (LogicalKey k : estate.logicalKeys) {
                        rows.add(new String[] {k.rowId, k.rpdId, k.logicalTableId, k.name, k.isPrimary,
                                String.join("\n", k.columns)});
                    }
                    return rows;
                });
    }

    private static KbTable logicalLevelKeys() {
        return new KbTable("logical_level_keys", KbTable.BMM,
                "The keys declared on the levels of logical dimensions: the column or columns whose values "
                        + "identify a member of the level, with the flags that say which key is the "
                        + "level's primary key, which gives the order in time, and which a drill shows. "
                        + "Not in the first attempt's Knowledge Base (deliberate difference 30).",
                "One key of one level of a logical dimension.",
                true,
                List.of(
                        KbColumn.key("row_id", "Row identifier: <level row_id>.key.<key name> "
                                + "(.key.unnamed-<n> when the key has no name)."),
                        estate(),
                        KbColumn.of("level_id", "The level the key is declared on.")
                                .references("logical_levels", "row_id"),
                        KbColumn.of("name", "The key's name in the repository."),
                        KbColumn.of("is_primary", "Whether the source marks the key as the level's primary "
                                + "key (the one that identifies a member), as written.")
                                .note(AS_WRITTEN_FLAG_NOTE),
                        KbColumn.of("is_chronological", "Whether the source marks the key as a chronological "
                                + "key (its values give the order of the members in time; time-series "
                                + "functions such as AGO need one), as written.")
                                .note(AS_WRITTEN_FLAG_NOTE),
                        KbColumn.of("is_for_drill_down", "Whether the source marks the key as the one shown "
                                + "when a user drills, as written.")
                                .note(AS_WRITTEN_FLAG_NOTE + " logical_levels.drill_key_column_id is the "
                                        + "first column that resolved of the first key with 'true' here."),
                        KbColumn.of("columns", "The key's columns, as Row identifiers of logical_columns.")
                                .note(KEY_COLUMNS_NOTE)),
                estate -> {
                    List<String[]> rows = new ArrayList<>();
                    for (LogicalLevelKey k : estate.logicalLevelKeys) {
                        rows.add(new String[] {k.rowId, k.rpdId, k.levelId, k.name, k.isPrimary, k.isChronological,
                                k.isForDrillDown, String.join("\n", k.columns)});
                    }
                    return rows;
                });
    }

    // ------------------------------------------------------------------ business model layer

    /**
     * The three columns that hold one expression: the template text, the rendered text and the reference
     * list (Architecture AD-5). {@code stem} is the column-name stem, for example {@code derived_expr_} or
     * {@code where_}; {@code what} says, in a few words, which expression it is.
     */
    private static List<KbColumn> expression(String stem, String what) {
        return List.of(
                KbColumn.of(stem + "text", "The template text of " + what + ", with %1, %2 ... where it names "
                        + "other objects; empty when there is none.").note(TEMPLATE_NOTE),
                KbColumn.of(stem + "text_desc", "The same text of " + what + " with the names filled in, as "
                        + "OBIEE shows it; empty when there is none.").note(RENDERED_NOTE),
                KbColumn.of(stem + "refs", "The objects that %1, %2 ... stand for in " + what + ", as Row "
                        + "identifiers.").note(REFS_NOTE));
    }

    private static String[] cells(Expression e) {
        return new String[] {e.text, e.textDesc, String.join("\n", e.refs)};
    }

    private static String[] row(Object... parts) {
        List<String> cells = new ArrayList<>();
        for (Object part : parts) {
            if (part instanceof String[]) {
                for (String cell : (String[]) part) {
                    cells.add(cell);
                }
            } else {
                cells.add((String) part);
            }
        }
        return cells.toArray(new String[0]);
    }

    private static List<KbColumn> columns(Object... parts) {
        List<KbColumn> all = new ArrayList<>();
        for (Object part : parts) {
            if (part instanceof KbColumn) {
                all.add((KbColumn) part);
            } else {
                for (Object column : (List<?>) part) {
                    all.add((KbColumn) column);
                }
            }
        }
        return all;
    }

    private static KbTable logicalTables() {
        return new KbTable("logical_tables", KbTable.BMM,
                "The tables of the business models: the logical facts and dimensions that measures and "
                        + "attributes are defined on.",
                "One logical table of one business model.",
                true,
                List.of(
                        KbColumn.key("row_id", "Row identifier: <estate>::bmm::<business model>.<table name>.")
                                .note("A logical dimension of the same business model with the same name has "
                                        + "the same identifier text; it is a row of another table."),
                        estate(),
                        KbColumn.of("business_model", "The name of the business model the table belongs to."),
                        KbColumn.of("name", "The logical table's name."),
                        KbColumn.of("source_count",
                                "How many table sources the logical table declares; a whole number.")
                                .note("Counted from the table's own list of sources. logical_table_sources "
                                        + "holds the sources themselves."),
                        KbColumn.of("is_lookup_table", "Whether the source marks the table as a lookup "
                                + "table (one read through the LOOKUP function, typically for translated "
                                + "names, and not joined like a dimension), as written.")
                                .note(AS_WRITTEN_FLAG_NOTE + " Not in the first attempt's Knowledge Base "
                                        + "(deliberate difference 37).")),
                estate -> {
                    List<String[]> rows = new ArrayList<>();
                    for (LogicalTable t : estate.logicalTables) {
                        rows.add(new String[] {t.rowId, t.rpdId, t.businessModel, t.name,
                                Integer.toString(t.sourceCount), t.isLookupTable});
                    }
                    return rows;
                });
    }

    private static KbTable logicalColumns() {
        return new KbTable("logical_columns", KbTable.BMM,
                "The columns of the logical tables, with their formulas exactly as written. This is where "
                        + "each measure and attribute is defined.",
                "One column of one logical table.",
                true,
                columns(
                        KbColumn.key("row_id", "Row identifier: <logical table row_id>.<column name>."),
                        estate(),
                        KbColumn.of("table_id", "The logical table the column belongs to.")
                                .references("logical_tables", "row_id"),
                        KbColumn.of("name", "The logical column's name."),
                        KbColumn.of("is_writeable", "The source's writeable flag, as written: 'true', 'false' "
                                + "or empty when not recorded."),
                        KbColumn.of("is_measure", "'true' when the column is a measure (it has aggregation "
                                + "rules in aggregation_rules), otherwise 'false'."),
                        KbColumn.of("source_count", "How many table sources the column's logical table "
                                + "declares; a whole number.")
                                .note("The table's count, repeated on each of its columns. Compare with "
                                        + "mapped_source_count."),
                        KbColumn.of("mapped_source_count", "How many table sources map this column; a whole "
                                + "number.")
                                .note("Less than source_count when some source cannot supply the column. 0 "
                                        + "for a column defined only by a formula: it has no lineage rows, "
                                        + "and derived_expr_refs says what it is computed from."),
                        KbColumn.of("time_series", "The time-series functions the column's formulas use: any "
                                + "of AGO, TODATE, PERIODROLLING, in that order, separated by single spaces; "
                                + "empty when none.")
                                .note("Found by searching the four text columns for the function name "
                                        + "followed by '('; the formula is not parsed."),
                        expression("derived_expr_", "the column's own formula (a column computed from other "
                                + "logical columns)"),
                        expression("mapping_expr_", "the definition the source keeps on the column itself "
                                + "(its attribute definition)"),
                        KbColumn.of("descriptor_id_column_id", "The logical column that holds the identifier "
                                + "this column is the text of, when the source names one; empty otherwise.")
                                .references("logical_columns", "row_id")
                                .note("OBIEE's 'descriptor ID': this column is shown to users and the "
                                        + "column named here is what filters and joins use in its place. "
                                        + "Also empty when the link did not resolve (no error row). Not in "
                                        + "the first attempt's Knowledge Base (deliberate difference 37).")),
                estate -> {
                    List<String[]> rows = new ArrayList<>();
                    for (LogicalColumn c : estate.logicalColumns) {
                        rows.add(row(c.rowId, c.rpdId, c.tableId, c.name, c.isWriteable,
                                c.isMeasure ? "true" : "false", Integer.toString(c.sourceCount),
                                Integer.toString(c.mappedSourceCount), String.join(" ", c.timeSeries),
                                cells(c.derived), cells(c.mapping), c.descriptorIdColumnId));
                    }
                    return rows;
                });
    }

    private static KbTable aggregationRules() {
        return new KbTable("aggregation_rules", KbTable.BMM,
                "How each measure is aggregated: its default rule and any rule that replaces the default for "
                        + "particular dimensions.",
                "One aggregation rule of one measure.",
                true,
                columns(
                        KbColumn.key("row_id", "Row identifier: <logical column row_id>#aggr<n>, where n "
                                + "counts the measure's rules from 0 in source order."),
                        estate(),
                        KbColumn.of("logical_column_id", "The measure the rule belongs to.")
                                .references("logical_columns", "row_id"),
                        KbColumn.of("name", "The rule's name in the source."),
                        KbColumn.of("is_default", "'true' for the measure's default rule, 'false' for a rule "
                                + "that replaces it for particular dimensions; as the source writes it.")
                                .note("A measure can have several rows. Which dimension a non-default rule "
                                        + "applies to is not captured."),
                        expression("expr_", "the rule, for example ' SUM(\"%1\")'")),
                estate -> {
                    List<String[]> rows = new ArrayList<>();
                    for (AggregationRule r : estate.aggregationRules) {
                        rows.add(row(r.rowId, r.rpdId, r.logicalColumnId, r.name, r.isDefault,
                                cells(r.expression)));
                    }
                    return rows;
                });
    }

    private static KbTable logicalTableSources() {
        return new KbTable("logical_table_sources", KbTable.BMM,
                "The table sources of the logical tables: each is one way a logical table is answered from "
                        + "physical tables, with its content filter, fragment and grain.",
                "One table source of one logical table.",
                true,
                columns(
                        KbColumn.key("row_id", "Row identifier: <logical table row_id>.src.<source name>."),
                        estate(),
                        KbColumn.of("logical_table_id", "The logical table the source belongs to.")
                                .references("logical_tables", "row_id"),
                        KbColumn.of("name", "The table source's name."),
                        KbColumn.of("is_active", "The source's active flag, as written: 'true', 'false' or "
                                + "empty when not recorded.")
                                .note("An inactive source is still extracted and still appears in lineage."),
                        KbColumn.of("priority", "The source's priority group, as written; empty when the "
                                + "source records none.")
                                .note("Empty means not recorded. It does not mean lowest, and no order may be "
                                        + "assumed from row order."),
                        expression("fragment_", "the fragment condition (which slice of the data this source "
                                + "holds, when a table is split over several sources)"),
                        expression("where_", "the source's content filter (its WHERE clause)"),
                        expression("group_by_", "the source's grain (the levels it is aggregated to)"),
                        KbColumn.of("closure_table_id", "For a source of a parent-child hierarchy, the "
                                + "physical table that holds the parent-child relationships; empty "
                                + "otherwise.")
                                .references("physical_tables", "row_id")
                                .note("That table's row names the four columns that define the hierarchy "
                                        + "(physical_tables.member_key_column_id and the three beside it). "
                                        + "Also empty when the link did not resolve (no error row). Not in "
                                        + "the first attempt's Knowledge Base (deliberate difference 36).")),
                estate -> {
                    List<String[]> rows = new ArrayList<>();
                    for (LogicalTableSource s : estate.logicalTableSources) {
                        rows.add(row(s.rowId, s.rpdId, s.logicalTableId, s.name, s.isActive, s.priority,
                                cells(s.fragment), cells(s.whereClause), cells(s.groupBy), s.closureTableId));
                    }
                    return rows;
                });
    }

    private static KbTable columnMappings() {
        return new KbTable("lts_column_map", KbTable.BMM,
                "The column mappings of the table sources: which physical expression supplies each logical "
                        + "column through each source.",
                "One logical column mapped through one table source.",
                true,
                columns(
                        KbColumn.key("row_id", "Row identifier: <table source row_id>#map<n>, where n counts "
                                + "the source's mappings from 0 in source order."),
                        estate(),
                        KbColumn.of("source_id", "The table source the mapping belongs to.")
                                .references("logical_table_sources", "row_id"),
                        KbColumn.of("logical_column_id", "The logical column that is mapped: the first "
                                + "resolved entry of logical_expr_refs; empty when there is none.")
                                .references("logical_columns", "row_id"),
                        KbColumn.of("physical_column_id", "The first physical column the mapping reads: the "
                                + "first resolved entry of physical_expr_refs; empty when the mapping reads "
                                + "none (a constant, for example).")
                                .references("physical_columns", "row_id")
                                .note("Only the first. A mapping computed from several columns lists all of "
                                        + "them in physical_expr_refs."),
                        expression("logical_expr_", "the logical side of the mapping (the column being "
                                + "mapped)"),
                        expression("physical_expr_", "the physical side of the mapping (the database "
                                + "expression that supplies the column)")),
                estate -> {
                    List<String[]> rows = new ArrayList<>();
                    for (ColumnMapping m : estate.columnMappings) {
                        rows.add(row(m.rowId, m.rpdId, m.sourceId, m.logicalColumnId, m.physicalColumnId,
                                cells(m.logicalExpression), cells(m.physicalExpression)));
                    }
                    return rows;
                });
    }

    private static KbTable bmmJoins() {
        return new KbTable("bmm_joins", KbTable.BMM,
                "The joins between logical tables, with the join type (inner or outer) and how many rows "
                        + "each side may have. An inner join drops rows that have no partner, so it can "
                        + "change a number without changing any formula.",
                "One join between two logical tables.",
                true,
                List.of(
                        KbColumn.key("row_id", "Row identifier: <estate>::bmm::joins.<join name>.")
                                .note("Not qualified by business model: joins of two models with the same "
                                        + "name share an identifier, and errors.csv then has a "
                                        + "duplicate_identifier row. 'joins' is a fixed word."),
                        estate(),
                        KbColumn.of("name", "The join's name in the source; usually a generated name."),
                        KbColumn.of("join_type", "The join type as the source writes it: 'Inner', "
                                + "'LeftOuter', 'RightOuter' or another value the source uses.")
                                .note("Left and right are relative to table1_id and table2_id as written "
                                        + "here. The value is not mapped to a fixed list."),
                        KbColumn.of("table1_id", "The first logical table of the join.")
                                .references("logical_tables", "row_id")
                                .note("Empty when the table was not found; no error row is written for that."),
                        KbColumn.of("multiplicity1", "How many rows of the first table may match one row of "
                                + "the second, as written: for example '0..1', '1..1', '0..n', 'n..n'."),
                        KbColumn.of("is_aggregate1", "The source's aggregate flag for the first side, as "
                                + "written: 'true', 'false' or empty when not recorded."),
                        KbColumn.of("table2_id", "The second logical table of the join.")
                                .references("logical_tables", "row_id")
                                .note("Empty when the table was not found; no error row is written for that."),
                        KbColumn.of("multiplicity2", "How many rows of the second table may match one row of "
                                + "the first, as written."),
                        KbColumn.of("is_aggregate2", "The source's aggregate flag for the second side, as "
                                + "written: 'true', 'false' or empty when not recorded.")),
                estate -> {
                    List<String[]> rows = new ArrayList<>();
                    for (BmmJoin j : estate.bmmJoins) {
                        rows.add(new String[] {j.rowId, j.rpdId, j.name, j.joinType, j.table1Id, j.multiplicity1,
                                j.isAggregate1, j.table2Id, j.multiplicity2, j.isAggregate2});
                    }
                    return rows;
                });
    }

    private static String flag(boolean value) {
        return value ? "true" : "false";
    }

    private static KbTable logicalDimensions() {
        return new KbTable("logical_dimensions", KbTable.BMM,
                "The logical dimensions: the drill structure behind each hierarchy, with flags for ragged, "
                        + "skip-level and parent-child hierarchies.",
                "One logical dimension of one business model.",
                true,
                List.of(
                        KbColumn.key("row_id", "Row identifier: <estate>::bmm::<business model>.<dimension "
                                + "name>.")
                                .note("A logical table of the same business model with the same name has the "
                                        + "same identifier text; it is a row of another table."),
                        estate(),
                        KbColumn.of("name", "The dimension's name."),
                        KbColumn.of("business_model", "The name of the business model the dimension belongs "
                                + "to.")
                                .note("A name, not an identifier. It equals logical_tables.business_model."),
                        KbColumn.of("is_value_based", "'true' for a parent-child hierarchy (each member "
                                + "points at its own parent), otherwise 'false'.")
                                .note("A parent-child dimension can still have rows in logical_levels. Its "
                                        + "presentation hierarchy has no levels; see "
                                        + "presentation_hierarchies.display_column_id."),
                        KbColumn.of("is_ragged", "'true' when a branch may stop above the lowest level "
                                + "(ragged), otherwise 'false'."),
                        KbColumn.of("is_skipped", "'true' when a member may have no parent at the level "
                                + "directly above (skip-level), otherwise 'false'."),
                        KbColumn.of("default_root_level_id", "The level the source names as the dimension's "
                                + "default root; empty when it names none.")
                                .references("logical_levels", "row_id")
                                .note("Also empty when the link did not resolve; no error row is written for "
                                        + "that."),
                        KbColumn.of("level_count", "How many levels the source gives the dimension; a whole "
                                + "number.")
                                .note("Counts a level that was left out for having no name, so it can exceed "
                                        + "the number of logical_levels rows; errors.csv then says why."),
                        KbColumn.of("is_time", "Whether the source marks the dimension as a time dimension, "
                                + "as written.")
                                .note(AS_WRITTEN_FLAG_NOTE + " A time dimension is what time-series functions "
                                        + "(AGO, TODATE) work on; its levels need a chronological key, see "
                                        + "logical_level_keys.is_chronological. Not in the first attempt's "
                                        + "Knowledge Base (deliberate difference 32).")),
                estate -> {
                    List<String[]> rows = new ArrayList<>();
                    for (LogicalDimension d : estate.logicalDimensions) {
                        rows.add(new String[] {d.rowId, d.rpdId, d.name, d.businessModel, flag(d.isValueBased),
                                flag(d.isRagged), flag(d.isSkipped), d.defaultRootLevelId,
                                Integer.toString(d.levelCount), d.isTime});
                    }
                    return rows;
                });
    }

    private static KbTable logicalLevels() {
        return new KbTable("logical_levels", KbTable.BMM,
                "The levels of the logical dimensions, each with the column a drill follows.",
                "One level of one logical dimension.",
                true,
                List.of(
                        KbColumn.key("row_id", "Row identifier: <dimension row_id>.<level name>.")
                                .note("A logical column can have the same identifier text (a table named "
                                        + "like the dimension, a column named like the level); it is a row "
                                        + "of another table. Formulas' _refs cells may name a level."),
                        estate(),
                        KbColumn.of("dimension_id", "The logical dimension the level belongs to.")
                                .references("logical_dimensions", "row_id"),
                        KbColumn.of("position", "The level's place among the dimension's levels in the "
                                + "source, counting from 0; a whole number.")
                                .note("Source order, which is not always depth: a dimension with two "
                                        + "branches lists them one after the other. Which level is the child "
                                        + "of which is in child_level_ids and parent_level_ids. Numbers can "
                                        + "have gaps."),
                        KbColumn.of("name", "The level's name."),
                        KbColumn.of("member_count", "The number of members the source records for the level, "
                                + "as written; often an estimate, and empty when not recorded."),
                        KbColumn.of("is_grand_total", "'true' for the grand-total level (the single 'all' "
                                + "member at the top), otherwise 'false'."),
                        KbColumn.of("drill_key_column_id", "The logical column a drill on this level "
                                + "follows: the first column of the level's key marked for drill-down; "
                                + "empty when the level has none.")
                                .references("logical_columns", "row_id")
                                .note("Only the first column of a key that has several; every key of the "
                                        + "level, with all its columns and its flags, is in "
                                        + "logical_level_keys. Usually empty for a grand-total level. No "
                                        + "error row is written when it is empty."),
                        KbColumn.of("child_level_ids", "The levels the source lists as this level's "
                                + "children (the levels directly below it), as Row identifiers of "
                                + "logical_levels.")
                                .note(LEVEL_LIST_NOTE + " An empty line is a child that was not found. Empty "
                                        + "for a lowest level."),
                        KbColumn.of("parent_level_ids", "The levels of the same dimension that list this "
                                + "level as a child (the levels directly above it), as Row identifiers of "
                                + "logical_levels.")
                                .note(LEVEL_LIST_NOTE + " Worked out from child_level_ids of the other levels, "
                                        + "in source order; nothing else is inferred. Empty for a top level. "
                                        + "A level with two entries sits on two drill paths.")),
                estate -> {
                    List<String[]> rows = new ArrayList<>();
                    for (LogicalLevel l : estate.logicalLevels) {
                        rows.add(new String[] {l.rowId, l.rpdId, l.dimensionId, Integer.toString(l.position),
                                l.name, l.memberCount, flag(l.isGrandTotal), l.drillKeyColumnId,
                                String.join("\n", l.childLevelIds), String.join("\n", l.parentLevelIds)});
                    }
                    return rows;
                });
    }

    // ------------------------------------------------------------------ presentation layer

    private static KbTable subjectAreas() {
        return new KbTable("presentation_subject_areas", KbTable.PRES,
                "The subject areas: the groups of presentation tables a report author chooses from. A report "
                        + "names its subject area.",
                "One subject area.",
                true,
                List.of(
                        KbColumn.key("row_id", "Row identifier: <estate>::pres::<subject area name>."),
                        estate(),
                        KbColumn.of("name", "The subject area's name, as reports refer to it."),
                        KbColumn.of("business_model", "The name of the business model the subject area is "
                                + "built on; empty when the link did not resolve.")
                                .note("A name, not an identifier. It equals logical_tables.business_model."),
                        KbColumn.of("implicit_fact_column_id", "The logical column used as the fact when a "
                                + "query selects only dimension columns; empty when the subject area has "
                                + "none.")
                                .references("logical_columns", "row_id")
                                .note("Also empty when the link did not resolve; no error row is written for "
                                        + "that."),
                        KbColumn.of("is_auto_aggregate", "The source's automatic-aggregation flag, as "
                                + "written."),
                        KbColumn.of("aliases", "Other names the subject area answers to, separated by ' | ' "
                                + "(space, bar, space); empty when it has none.")
                                .note("Older reports may name the subject area by an alias.")),
                estate -> {
                    List<String[]> rows = new ArrayList<>();
                    for (SubjectArea s : estate.subjectAreas) {
                        rows.add(new String[] {s.rowId, s.rpdId, s.name, s.businessModel, s.implicitFactColumnId,
                                s.isAutoAggregate, String.join(" | ", s.aliases)});
                    }
                    return rows;
                });
    }

    private static KbTable presentationTables() {
        return new KbTable("presentation_tables", KbTable.PRES,
                "The tables of the subject areas, as a report author sees them (folders of columns).",
                "One presentation table of one subject area.",
                true,
                List.of(
                        KbColumn.key("row_id", "Row identifier: <subject area row_id>.<table name>."),
                        estate(),
                        KbColumn.of("subject_area_id", "The subject area the table belongs to.")
                                .references("presentation_subject_areas", "row_id"),
                        KbColumn.of("name", "The presentation table's name, as reports refer to it."),
                        KbColumn.of("display_name", "The source's display-name setting for the table; empty "
                                + "when it has none.")
                                .note("Not a caption: where present it is usually an expression used for "
                                        + "translation."),
                        KbColumn.of("aliases", "Other names the table answers to, separated by ' | ' (space, "
                                + "bar, space); empty when it has none.")),
                estate -> {
                    List<String[]> rows = new ArrayList<>();
                    for (PresentationTable t : estate.presentationTables) {
                        rows.add(new String[] {t.rowId, t.rpdId, t.subjectAreaId, t.name, t.displayName,
                                String.join(" | ", t.aliases)});
                    }
                    return rows;
                });
    }

    private static KbTable presentationColumns() {
        return new KbTable("presentation_columns", KbTable.PRES,
                "The columns of the presentation tables, as a report author sees them, each tied to the "
                        + "logical column it shows.",
                "One column of one presentation table.",
                true,
                List.of(
                        KbColumn.key("row_id", "Row identifier: <presentation table row_id>.<column name>.")
                                .note("A presentation hierarchy of the same table with the same name has the "
                                        + "same identifier text; it is a row of another table."),
                        estate(),
                        KbColumn.of("table_id", "The presentation table the column belongs to.")
                                .references("presentation_tables", "row_id"),
                        KbColumn.of("name", "The presentation column's name, as reports refer to it."),
                        KbColumn.of("logical_column_id", "The logical column this presentation column shows.")
                                .references("logical_columns", "row_id")
                                .note("Empty when the source has no link, or the link did not resolve; "
                                        + "errors.csv then has an unresolved_reference row for the column."),
                        KbColumn.of("override_logical_name", "The source's flag saying the presentation name "
                                + "was set by hand and does not follow the logical column's name; as "
                                + "written: 'true', 'false' or empty."),
                        KbColumn.of("display_name", "The source's display-name setting for the column; empty "
                                + "when it has none.")
                                .note("Not a caption: where present it is usually an expression used for "
                                        + "translation.")),
                estate -> {
                    List<String[]> rows = new ArrayList<>();
                    for (PresentationColumn c : estate.presentationColumns) {
                        rows.add(new String[] {c.rowId, c.rpdId, c.tableId, c.name, c.logicalColumnId,
                                c.overrideLogicalName, c.displayName});
                    }
                    return rows;
                });
    }

    private static KbTable presentationHierarchies() {
        return new KbTable("presentation_hierarchies", KbTable.PRES,
                "The hierarchies a report author sees on a presentation table, each tied to its logical "
                        + "dimension.",
                "One hierarchy of one presentation table.",
                true,
                List.of(
                        KbColumn.key("row_id", "Row identifier: <presentation table row_id>.<hierarchy "
                                + "name>.")
                                .note("A presentation column of the same table with the same name has the "
                                        + "same identifier text; it is a row of another table."),
                        estate(),
                        KbColumn.of("table_id", "The presentation table the hierarchy belongs to.")
                                .references("presentation_tables", "row_id"),
                        KbColumn.of("name", "The hierarchy's name, as reports refer to it."),
                        KbColumn.of("dimension_id", "The logical dimension the hierarchy shows; its flags "
                                + "say whether the hierarchy is ragged, skip-level or parent-child.")
                                .references("logical_dimensions", "row_id")
                                .note("Empty when the source has no link or the link did not resolve; no "
                                        + "error row is written for that."),
                        KbColumn.of("level_count", "How many levels the source gives the hierarchy; a whole "
                                + "number. 0 for a parent-child hierarchy.")
                                .note("Counts a level that was left out for having no name."),
                        KbColumn.of("display_column_id", "For a parent-child hierarchy, the one presentation "
                                + "column it shows; empty for a hierarchy with levels.")
                                .references("presentation_columns", "row_id")
                                .note("Taken from the hierarchy's own column list: the first that resolves. "
                                        + "The columns of a hierarchy with levels are in "
                                        + "presentation_level_columns.")),
                estate -> {
                    List<String[]> rows = new ArrayList<>();
                    for (PresentationHierarchy h : estate.presentationHierarchies) {
                        rows.add(new String[] {h.rowId, h.rpdId, h.tableId, h.name, h.dimensionId,
                                Integer.toString(h.levelCount), h.displayColumnId});
                    }
                    return rows;
                });
    }

    private static KbTable presentationLevels() {
        return new KbTable("presentation_levels", KbTable.PRES,
                "The levels of the presentation hierarchies, in drill order.",
                "One level of one presentation hierarchy.",
                true,
                List.of(
                        KbColumn.key("row_id", "Row identifier: <hierarchy row_id>.<level name>."),
                        estate(),
                        KbColumn.of("hierarchy_id", "The presentation hierarchy the level belongs to.")
                                .references("presentation_hierarchies", "row_id"),
                        KbColumn.of("position", "The level's place in the hierarchy, counting from 0 at the "
                                + "top; a whole number. This is the drill order.")
                                .note("Sort on it as a number, not as text. Numbers can have gaps when a "
                                        + "level was left out for having no name."),
                        KbColumn.of("name", "The level's name, as reports show it."),
                        KbColumn.of("logical_level_id", "The logical level this presentation level shows.")
                                .references("logical_levels", "row_id")
                                .note("Empty when the source has no link or the link did not resolve; no "
                                        + "error row is written for that.")),
                estate -> {
                    List<String[]> rows = new ArrayList<>();
                    for (PresentationLevel l : estate.presentationLevels) {
                        rows.add(new String[] {l.rowId, l.rpdId, l.hierarchyId, Integer.toString(l.position),
                                l.name, l.logicalLevelId});
                    }
                    return rows;
                });
    }

    private static KbTable presentationLevelColumns() {
        return new KbTable("presentation_level_columns", KbTable.PRES,
                "The presentation columns shown at each level of a presentation hierarchy.",
                "One presentation column shown at one level.",
                true,
                List.of(
                        KbColumn.key("row_id", "Row identifier: <presentation level row_id>.<n>, where n is "
                                + "the column's place at the level, counting from 0."),
                        estate(),
                        KbColumn.of("level_id", "The presentation level the column is shown at.")
                                .references("presentation_levels", "row_id"),
                        KbColumn.of("position", "The column's place among the level's columns, counting from "
                                + "0; a whole number.")
                                .note("A column whose link did not resolve has no row here and keeps its "
                                        + "number, so numbers can have gaps; errors.csv has a row for it."),
                        KbColumn.of("presentation_column_id", "The presentation column shown.")
                                .references("presentation_columns", "row_id")),
                estate -> {
                    List<String[]> rows = new ArrayList<>();
                    for (PresentationLevelColumn c : estate.presentationLevelColumns) {
                        rows.add(new String[] {c.rowId, c.rpdId, c.levelId, Integer.toString(c.position),
                                c.presentationColumnId});
                    }
                    return rows;
                });
    }

    private static KbTable lineage() {
        return new KbTable("lineage", KbTable.PRES,
                "Where each presentation column comes from: the path from presentation column through "
                        + "logical column and table source to physical table and column. Repository lineage "
                        + "only; report columns are not in this table.",
                "One path: one presentation column through one table source that maps its logical column.",
                true,
                List.of(
                        KbColumn.key("row_id", "Row identifier: <presentation column row_id>#<table source "
                                + "row_id>."),
                        estate(),
                        KbColumn.of("presentation_column_id", "The presentation column the path starts at.")
                                .references("presentation_columns", "row_id")
                                .note("A presentation column has one row per table source that maps its "
                                        + "logical column, and no row at all when no source maps it (a "
                                        + "column defined only by a formula; see "
                                        + "logical_columns.derived_expr_refs) or when it has no logical "
                                        + "column."),
                        KbColumn.of("logical_column_id", "The logical column the presentation column shows.")
                                .references("logical_columns", "row_id"),
                        KbColumn.of("source_id", "The table source that maps the logical column on this path.")
                                .references("logical_table_sources", "row_id")
                                .note("Inactive sources are included; check logical_table_sources.is_active."),
                        KbColumn.of("physical_table_id", "The physical table the path ends at; empty when the "
                                + "mapping reads no physical column.")
                                .references("physical_tables", "row_id")
                                .note("For an alias this is the alias; follow physical_tables.source_table_id "
                                        + "to the table it stands for. For a table of type 'select' the SQL "
                                        + "is in physical_table_definitions and is not followed further."),
                        KbColumn.of("physical_column_id", "The physical column the path ends at; empty when "
                                + "the mapping reads no physical column.")
                                .references("physical_columns", "row_id")
                                .note("Only the first column of a mapping that uses several; the others are "
                                        + "in lts_column_map.physical_expr_refs.")),
                estate -> {
                    List<String[]> rows = new ArrayList<>();
                    for (LineageRow r : estate.lineage) {
                        rows.add(new String[] {r.rowId, r.rpdId, r.presentationColumnId, r.logicalColumnId,
                                r.sourceId, r.physicalTableId, r.physicalColumnId});
                    }
                    return rows;
                });
    }

    // ------------------------------------------------------------------ variables and security

    private static KbTable variables() {
        return new KbTable("variables", KbTable.VAR,
                "The repository and session variables, with their default values. A formula or filter that "
                        + "uses a variable changes its result when the variable changes.",
                "One variable.",
                true,
                columns(
                        KbColumn.key("row_id", "Row identifier: <estate>::var::<variable name>."),
                        estate(),
                        KbColumn.of("name", "The variable's name, as formulas refer to it."),
                        KbColumn.of("is_session", "'true' for a session variable (one value per user "
                                + "session); empty for a repository variable (one value for everyone).")
                                .note("As the source writes it. Empty means the source does not mark it as a "
                                        + "session variable."),
                        KbColumn.of("init_block_id", "The initialization block that fills the variable; empty "
                                + "when it has none (a fixed value).")
                                .references("init_blocks", "row_id")
                                .note("Also empty when the link did not resolve; no error row is written for "
                                        + "that."),
                        KbColumn.of("security_mask", "The source's security-mask setting for the variable, as "
                                + "written; empty when not recorded."),
                        KbColumn.of("redacted", "'true' when the value was withheld as a credential, "
                                + "otherwise 'false'.")
                                .note("Read this before the three expr_ columns: when 'true' they are empty "
                                        + "by decision, not because the variable has no value. A value is "
                                        + "withheld when the variable's name suggests a credential, and "
                                        + "when it is the same as such a variable's value; unparsed.csv has "
                                        + "a redacted_value row for each, saying which. A credential under "
                                        + "an innocent name, with a value found nowhere else, is not "
                                        + "recognised: 'false' is not a promise that the value is harmless."),
                        expression("expr_", "the variable's default value (empty when redacted is 'true')")),
                estate -> {
                    List<String[]> rows = new ArrayList<>();
                    for (Variable v : estate.variables) {
                        rows.add(row(v.rowId, v.rpdId, v.name, v.isSession, v.initBlockId, v.securityMask,
                                flag(v.redacted), cells(v.expression)));
                    }
                    return rows;
                });
    }

    private static KbTable initBlocks() {
        return new KbTable("init_blocks", KbTable.VAR,
                "The initialization blocks: the queries that fill variables when the server starts or a "
                        + "user signs in.",
                "One initialization block.",
                true,
                List.of(
                        KbColumn.key("row_id", "Row identifier: <estate>::var::init.<block name>.")
                                .note("'init' is a fixed word. A variable named 'init.<something>' could "
                                        + "have the same identifier text; it is a row of another table."),
                        estate(),
                        KbColumn.of("name", "The block's name."),
                        KbColumn.of("refresh_period", "How often the block runs again, in seconds, as "
                                + "written; empty when the source records none."),
                        KbColumn.of("is_session", "'true' when the block fills session variables (it runs "
                                + "when a user signs in); as written, empty when not recorded."),
                        KbColumn.of("is_enabled", "The source's enabled flag, as written: 'false' for a "
                                + "block that is switched off.")
                                .note("Empty means the source says nothing, which is the usual case for a "
                                        + "block that is in use; it does not mean 'false'."),
                        KbColumn.of("init_string", "The block's query, usually SQL; empty when the source "
                                + "has none.")
                                .note(VERBATIM + " May span several lines. Never withheld: only variable "
                                        + "values are (variables.redacted). The per-database variants of "
                                        + "the query are not read. Which variables the block fills is on "
                                        + "the variable: variables.init_block_id.")),
                estate -> {
                    List<String[]> rows = new ArrayList<>();
                    for (InitBlock b : estate.initBlocks) {
                        rows.add(new String[] {b.rowId, b.rpdId, b.name, b.refreshPeriod, b.isSession,
                                b.isEnabled, b.initString});
                    }
                    return rows;
                });
    }

    private static KbTable securityRules() {
        return new KbTable("security_rls", KbTable.SEC,
                "The repository's security: which role holds which query privilege, which permission a "
                        + "privilege package has on which object, and the row filters on table sources. "
                        + "Three kinds of row share the table.",
                "One query privilege of one role, one object one permission applies to, or one row filter.",
                true,
                columns(
                        KbColumn.key("row_id", "Row identifier: <estate>::sec::pkg.<package name>.q<n> for a "
                                + "query privilege, <estate>::sec::obj.<privilege name>.o<n> for an object "
                                + "privilege (n counts from 0; a missing name is written '?'), and <table "
                                + "source row_id>#filter for a row filter."),
                        estate(),
                        KbColumn.of("kind", "'query_privilege', 'object_privilege' or 'row_filter'.")
                                .note("Read kind first: it decides what role, subject_id and detail hold."),
                        KbColumn.of("role", "For a query_privilege row, the name of the role that holds the "
                                + "privilege; always empty for the other two kinds.")
                                .note("A name, not a Row identifier. For an object_privilege row, find the "
                                        + "role through the package: the package identifier ends detail, "
                                        + "and query_privilege rows of that package (their row_id starts "
                                        + "with it) carry the role. A package with no query privilege has "
                                        + "no such row. For a row_filter row the role is not known: the "
                                        + "source keeps it only inside the filter text."),
                        KbColumn.of("subject_id", "What the row is about. query_privilege: the name of the "
                                + "query privilege. object_privilege: the Row identifier of the object the "
                                + "permission applies to. row_filter: the Row identifier of the logical "
                                + "table that is filtered.")
                                .note("Polymorphic: read kind first. query_privilege: a plain name, not an "
                                        + "identifier, and it joins to no table. object_privilege: an "
                                        + "identifier of any table, usually presentation_subject_areas, "
                                        + "presentation_tables or presentation_columns; find the table by "
                                        + "the identifier's layer part and shape; empty when the privilege "
                                        + "lists no object or the object was not found. row_filter: "
                                        + "logical_tables.row_id."),
                        KbColumn.of("detail", "query_privilege: the privilege package's name. "
                                + "object_privilege: '<permission> via <package identifier>', for example "
                                + "'read via <estate>::sec::pkg.<package name>'. row_filter: the table "
                                + "source's name.")
                                .note("The permission is the source's own word (observed: read, write, "
                                        + "none). The package identifier is empty when the package was not "
                                        + "found. For a row_filter row the table source is the row_id "
                                        + "without its '#filter' ending."),
                        expression("expr_", "the row filter (always empty for the other two kinds)"),
                        KbColumn.of("role_display_name", "For a query_privilege row, the display name the "
                                + "role's own object records, for example 'BI Author Role'; always empty for "
                                + "the other two kinds.")
                                .note("Use it to name the role to a person: role can be an opaque text (the "
                                        + "sample has a role whose name is two 64-character hexadecimal "
                                        + "strings with a space between them; that is one name of one role). "
                                        + "Empty when the source records none. Not in the first attempt's "
                                        + "Knowledge Base (deliberate difference 27)."),
                        KbColumn.of("role_guid", "For a query_privilege row, the guid the role's own object "
                                + "records, for example 'BIAuthor'; always empty for the other two kinds.")
                                .note("The identifier the security provider knows the application role by. "
                                        + "Empty when the source records none. Not in the first attempt's "
                                        + "Knowledge Base (deliberate difference 27).")),
                estate -> {
                    List<String[]> rows = new ArrayList<>();
                    for (SecurityRule r : estate.securityRules) {
                        rows.add(row(r.rowId, r.rpdId, r.kind, r.role, r.subjectId, r.detail,
                                cells(r.expression), r.roleDisplayName, r.roleGuid));
                    }
                    return rows;
                });
    }

    // ------------------------------------------------------------------ run tables

    // ------------------------------------------------------------------ Web Catalog

    private static final String CATALOG_PATH_NOTE = "Folder and object names joined with '/', decoded from "
            + "the names on disk ('+' is a space, '%XX' a byte). Letter case is as on disk, which is often "
            + "lower case where the catalog shows mixed case. Names can contain dots, commas and '#'.";
    private static final String XML_VERSION_NOTE = "Text, never interpreted. Empty means the object declares "
            + "no version, which is common.";

    private static KbTable dashboards() {
        return new KbTable("dashboards", KbTable.CAT,
                "The dashboards in the Web Catalog. A dashboard is a layout object that lists its pages.",
                "One dashboard layout object.",
                true,
                List.of(
                        KbColumn.key("row_id", "Row identifier: <estate>::cat::<catalog_path>.")
                                .note("The whole path is one part of the identifier; do not split it on "
                                        + "dots or slashes."),
                        estate(),
                        KbColumn.of("name", "The dashboard's name: the name of the folder that holds the layout "
                                + "object.")
                                .note("The layout object itself is usually called 'dashboard layout', so the "
                                        + "folder carries the name. For an object at the top of the catalog "
                                        + "it is the object's own name. Names are not unique; use row_id."),
                        KbColumn.of("catalog_path", "The path of the layout object below the catalog root.")
                                .note(CATALOG_PATH_NOTE),
                        KbColumn.of("xml_version", "The version the object declares in the source.")
                                .note(XML_VERSION_NOTE),
                        KbColumn.of("page_count", "How many page references the dashboard holds; a whole number.")
                                .note("Counts references, including those that did not resolve, so it can "
                                        + "be larger than the number of dashboard_pages rows that name this "
                                        + "dashboard. The references are the dashboard_contents rows whose "
                                        + "container_id is this row.")),
                estate -> {
                    List<String[]> rows = new ArrayList<>();
                    for (Dashboard d : estate.dashboards) {
                        rows.add(new String[] {d.rowId, d.rpdId, d.name, d.catalogPath, d.formatVersion,
                                Integer.toString(d.pageCount)});
                    }
                    return rows;
                });
    }

    private static KbTable dashboardPages() {
        return new KbTable("dashboard_pages", KbTable.CAT,
                "The dashboard pages in the Web Catalog, each with the dashboard that lists it.",
                "One dashboard page object.",
                true,
                List.of(
                        KbColumn.key("row_id", "Row identifier: <estate>::cat::<catalog_path>."),
                        estate(),
                        KbColumn.of("dashboard_id", "The dashboard that lists this page; empty when no "
                                + "dashboard refers to it.")
                                .references("dashboards", "row_id")
                                .note("Taken from a page reference that resolved, never from the folder the "
                                        + "page is in. When several dashboards refer to one page, the first "
                                        + "in file order is given; dashboard_contents has them all."),
                        KbColumn.of("position", "The page's place among its dashboard's pages, counting "
                                + "from 0; a whole number.")
                                .note("0 with an empty dashboard_id means 'no dashboard', not 'first page'. "
                                        + "Hidden pages and page groups are not recorded."),
                        KbColumn.of("name", "The page's name: the last part of its path."),
                        KbColumn.of("catalog_path", "The path of the page object below the catalog root.")
                                .note(CATALOG_PATH_NOTE),
                        KbColumn.of("xml_version", "The version the object declares in the source.")
                                .note(XML_VERSION_NOTE)),
                estate -> {
                    List<String[]> rows = new ArrayList<>();
                    for (DashboardPage p : estate.dashboardPages) {
                        rows.add(new String[] {p.rowId, p.rpdId, p.dashboardId, Integer.toString(p.position),
                                p.name, p.catalogPath, p.formatVersion});
                    }
                    return rows;
                });
    }

    private static KbTable dashboardContents() {
        return new KbTable("dashboard_contents", KbTable.CAT,
                "What dashboards and pages hold: a dashboard's references to its pages, and a page's "
                        + "references to reports, dashboard prompts, scorecard views and folders, each with "
                        + "how the reference resolved.",
                "One reference written in a dashboard or a dashboard page.",
                true,
                List.of(
                        KbColumn.key("row_id", "Row identifier: <container row_id>#page<n> for a dashboard's "
                                + "page reference, <container row_id>#item<n> for a reference on a page."),
                        estate(),
                        KbColumn.of("container_id", "The dashboard or the page that holds the reference.")
                                .note("Polymorphic: a row of dashboards when element ends in "
                                        + "'dashboardPageRef', otherwise a row of dashboard_pages. Read "
                                        + "element first."),
                        KbColumn.of("position", "The reference's number within its container, counting "
                                + "from 0; a whole number.")
                                .note("On a dashboard this is page order. On a page it is NOT layout order: "
                                        + "all report references come first, then dashboard prompts, then "
                                        + "scorecard views, then folder views, each group in source order."),
                        KbColumn.of("element", "The full name of the referring element, as "
                                + "{namespace}local-name: dashboardPageRef (a page), reportRef (a report), "
                                + "globalFilterView (a dashboard prompt), scorecardViewRef or folderView."),
                        KbColumn.of("reference", "The path the source wrote.")
                                .note(VERBATIM + " A path starting with '/shared/' is absolute; one with no "
                                        + "leading '/' is relative to the container's folder; any other root "
                                        + "(for example '/company_shared/') is outside this catalog export."),
                        KbColumn.of("resolution", "How the reference resolved: 'exact', 'case_normalised' "
                                + "(matched only after ignoring letter case), 'ambiguous' (several paths "
                                + "match once case is ignored) or 'unresolved'.")
                                .note("Read this before target_id. 'ambiguous' and 'unresolved' rows have an "
                                        + "empty target_id and a row in errors.csv."),
                        KbColumn.of("target_id", "The Row identifier of the catalog object or folder the "
                                + "reference points at; empty when it did not resolve.")
                                .note("Polymorphic: <estate>::cat::<path> of whatever is at that path. It is "
                                        + "a row of dashboard_pages for a page reference; for the others it "
                                        + "is a report, a saved prompt, a scorecard object or a folder, which "
                                        + "may have no table in this Knowledge Base (folders and object "
                                        + "types that are not read never have one). A value with no matching "
                                        + "row is not an error."),
                        KbColumn.of("target_kind", "'object' or 'folder': what the path names; empty when the "
                                + "reference did not resolve."),
                        KbColumn.of("detail", "Why the reference did not resolve, or that it resolved to a "
                                + "folder where an object was expected (or the reverse); empty otherwise.")),
                estate -> {
                    List<String[]> rows = new ArrayList<>();
                    for (DashboardContent c : estate.dashboardContents) {
                        rows.add(new String[] {c.rowId, c.rpdId, c.containerId, Integer.toString(c.position),
                                c.element, c.reference, c.resolution, c.targetId, c.targetKind, c.detail});
                    }
                    return rows;
                });
    }

    // ------------------------------------------------------------------ Web Catalog: reports and filters

    private static final String WHOLE_TEXT_NOTE = VERBATIM + " This is the whole text of the expression "
            + "element: all the character data inside it in source order, including the text of elements "
            + "nested in it and the text between and after them. Tags and attributes of nested elements are "
            + "not part of it, so the text of nested parts follows on with nothing in between. (Filter rows "
            + "follow a narrower rule, because their nested expressions are rows: see "
            + "analysis_filters.expr_text.) The catalog "
            + "holds an expression once; there is no template form and no list of references.";
    private static final String OWN_TEXT_NOTE = VERBATIM + " This is the expression's own text: all the "
            + "character data directly inside the expression element, before, between and after the elements "
            + "nested in it, together with the whole text of nested elements that are not expressions. The "
            + "text of a nested expression is NOT included: every nested expression is a row of its own in "
            + "analysis_filters (follow parent_id). So a leaf row holds its column, formula or literal; the "
            + "row of an and/or group or a comparison usually holds only the white space the source has "
            + "between its parts, or nothing.";
    private static final String POSITION_FROM_0 = "; counting from 0; a whole number.";
    private static final String FLAG_NOTE = "'true' or 'false'.";

    private static KbTable analyses() {
        return new KbTable("analyses", KbTable.CAT,
                "The reports in the Web Catalog. OBIEE calls a report an analysis, and the tables keep that "
                        + "word.",
                "One report object.",
                true,
                List.of(
                        KbColumn.key("row_id", "Row identifier: <estate>::cat::<catalog_path>.")
                                .note("The whole path is one part of the identifier; do not split it on "
                                        + "dots or slashes."),
                        estate(),
                        KbColumn.of("name", "The report's name: the last part of its path.")
                                .note("A Visual Analyzer project is stored as a report whose file is called "
                                        + "'_projectdefn'; its own formulas are not read (see unparsed.csv). "
                                        + "Names are not unique; use row_id."),
                        KbColumn.of("catalog_path", "The path of the report below the catalog root.")
                                .note(CATALOG_PATH_NOTE),
                        KbColumn.of("xml_version", "The version the object declares in the source.")
                                .note(XML_VERSION_NOTE),
                        KbColumn.of("criteria_count", "How many criteria blocks (query definitions) the "
                                + "report holds, nested ones included; a whole number.")
                                .note("More than 1 means the report combines queries; the blocks are the "
                                        + "analysis_criteria rows whose analysis_id is this row."),
                        KbColumn.of("column_count", "How many analysis_columns rows the report has; a whole "
                                + "number.")),
                estate -> {
                    List<String[]> rows = new ArrayList<>();
                    for (Analysis a : estate.analyses) {
                        rows.add(new String[] {a.rowId, a.rpdId, a.name, a.catalogPath, a.formatVersion,
                                Integer.toString(a.criteriaCount), Integer.toString(a.columnCount)});
                    }
                    return rows;
                });
    }

    private static KbTable analysisCriteria() {
        return new KbTable("analysis_criteria", KbTable.CAT,
                "The criteria blocks of reports. A criteria block is one query definition: a subject area, "
                        + "columns and filters. A report that combines queries has several.",
                "One criteria block of one report.",
                true,
                List.of(
                        KbColumn.key("row_id", "Row identifier: <analysis row_id>#criteria<n>, n being the "
                                + "block's position."),
                        estate(),
                        KbColumn.of("analysis_id", "The report the block belongs to.")
                                .references("analyses", "row_id"),
                        KbColumn.of("position", "The block's number within its report, in source order"
                                + POSITION_FROM_0)
                                .note("Blocks nest: the operands of a set operation (a union, say) are "
                                        + "blocks inside the outer block and are numbered after it. The "
                                        + "nesting itself is not recorded."),
                        KbColumn.of("subject_area", "The subject area the block queries, as the catalog "
                                + "wrote it; empty when the block names none.")
                                .note(VERBATIM + " The double quotes around the name are part of the "
                                        + "value: \"A - Sample Sales\". Remove them before comparing with "
                                        + "presentation_subject_areas.name. A value of the form XSA(...) "
                                        + "is an uploaded dataset, not a subject area. The outer block of "
                                        + "a set operation has none.")),
                estate -> {
                    List<String[]> rows = new ArrayList<>();
                    for (AnalysisCriteria c : estate.analysisCriteria) {
                        rows.add(new String[] {c.rowId, c.rpdId, c.analysisId, Integer.toString(c.position),
                                c.subjectArea});
                    }
                    return rows;
                });
    }

    private static KbTable analysisColumns() {
        return new KbTable("analysis_columns", KbTable.CAT,
                "The columns of reports, each with its formula exactly as written. This is where a "
                        + "calculation that exists only in a report is defined.",
                "One column of one criteria block of a report.",
                true,
                List.of(
                        KbColumn.key("row_id", "Row identifier: <analysis row_id>#c<i>.<j>, i being the "
                                + "criteria block's position and j the column's."),
                        estate(),
                        KbColumn.of("analysis_id", "The report the column belongs to.")
                                .references("analyses", "row_id"),
                        KbColumn.of("criteria_id", "The criteria block the column belongs to: the nearest "
                                + "block around it.")
                                .references("analysis_criteria", "row_id"),
                        KbColumn.of("position", "The column's number within its criteria block, in source "
                                + "order" + POSITION_FROM_0),
                        KbColumn.of("column_id", "The identifier the report gives the column; the report's "
                                + "views and filters refer to the column by it.")
                                .note("Unique within a report at most. Not a Row identifier."),
                        KbColumn.of("heading", "The column heading the author typed in place of the default; "
                                + "empty when the default is used.")
                                .note(VERBATIM),
                        KbColumn.of("table_heading", "The table heading the author typed in place of the "
                                + "default; empty when the default is used.")
                                .note(VERBATIM),
                        KbColumn.of("formula_construct", "The full name of the source element that carried "
                                + "the formula, as {namespace}local-name; empty when the column has no "
                                + "formula.")
                                .note("Only columnFormula is read. The result columns of the outer block of "
                                        + "a set operation have no formula."),
                        KbColumn.of("formula_type", "The type the source gives the formula's expression, in "
                                + "the source's own words; empty when there is none.")
                                .note("Usually 'sawx:sqlExpression'. 'sawx:binned' is a column that puts "
                                        + "values into bins: its formula cell then holds the text of the "
                                        + "base formula followed by the text of the bin rules."),
                        KbColumn.of("formula", "The column's formula; empty when it has none.")
                                .note(WHOLE_TEXT_NOTE + " When a column has more than one formula element, "
                                        + "this is the first; the others are counted in unparsed.csv."),
                        KbColumn.of("is_computation", "'true' when the source types the column as something "
                                + "other than a regular column.")
                                .note(FLAG_NOTE + " Taken from the column element's type, never from the "
                                        + "formula. 'false' does NOT mean the formula is a bare column "
                                        + "reference: a regular column can hold count(distinct ...). Read "
                                        + "the formula to know."),
                        KbColumn.of("uses_eval", "'true' when a formula of the column calls a database "
                                + "function directly (EVALUATE).")
                                .note(FLAG_NOTE + " The logic of such a call is in the database, not in "
                                        + "OBIEE.")),
                estate -> {
                    List<String[]> rows = new ArrayList<>();
                    for (AnalysisColumn c : estate.analysisColumns) {
                        rows.add(new String[] {c.rowId, c.rpdId, c.analysisId, c.criteriaId,
                                Integer.toString(c.position), c.columnId, c.heading, c.tableHeading,
                                c.formulaConstruct, c.formulaType, c.formula, flag(c.isComputation),
                                flag(c.usesEval)});
                    }
                    return rows;
                });
    }

    private static KbTable analysisViews() {
        return new KbTable("analysis_views", KbTable.CAT,
                "The views inside reports: what each report is shown as (a table, a pivot table, a chart, a "
                        + "title, a layout that arranges other views).",
                "One view inside one report.",
                true,
                List.of(
                        KbColumn.key("row_id", "Row identifier: <analysis row_id>#view<n>, n being the "
                                + "view's position."),
                        estate(),
                        KbColumn.of("analysis_id", "The report the view belongs to.")
                                .references("analyses", "row_id"),
                        KbColumn.of("position", "The view's number within its report, in source order"
                                + POSITION_FROM_0),
                        KbColumn.of("view_type", "The view's type in the source's own words, for example "
                                + "'saw:pivotTableView'; '(no xsi:type)' when the view declares none.")
                                .note("Never translated and never checked against a list: a type this "
                                        + "program has not met is written as it is. It is not a Power BI "
                                        + "visual."),
                        KbColumn.of("name", "The view's name within the report, for example "
                                + "'compoundView!1'.")
                                .note("A layout view refers to the views it arranges by this name."),
                        KbColumn.of("references_views", "How many cells of this view name another view; a "
                                + "whole number.")
                                .note("Greater than 0 marks a layout view that arranges others."),
                        KbColumn.of("bound_column_count", "How many elements inside the view are bound to a "
                                + "report column; a whole number.")
                                .note("Counts elements, not distinct columns, at any depth; a view that "
                                        + "holds another view counts that view's bindings too. Greater "
                                        + "than 0 marks a view that shows data."),
                        KbColumn.of("display_type", "For a chart, its kind in the source's own words, for "
                                + "example 'bar', 'line', 'pie'; empty for other views."),
                        KbColumn.of("display_subtype", "For a chart, the variant of its kind, for example "
                                + "'stacked', 'horizontal', 'default'; empty for other views.")),
                estate -> {
                    List<String[]> rows = new ArrayList<>();
                    for (AnalysisView v : estate.analysisViews) {
                        rows.add(new String[] {v.rowId, v.rpdId, v.analysisId, Integer.toString(v.position),
                                v.viewType, v.name, Integer.toString(v.referencesViews),
                                Integer.toString(v.boundColumnCount), v.displayType, v.displaySubtype});
                    }
                    return rows;
                });
    }

    private static KbTable analysisFilters() {
        return new KbTable("analysis_filters", KbTable.CAT,
                "Filters: every expression of the filters inside reports, every expression of the saved "
                        + "filters, and the dashboard prompts placed on dashboard pages. Read scope first.",
                "One expression of a filter (scope 'analysis' or 'saved_filter'), or one dashboard prompt "
                        + "placed on a page (scope 'dashboard').",
                true,
                List.of(
                        KbColumn.key("row_id", "Row identifier: <analysis row_id>#f<i>.<k> for a report "
                                + "(i the criteria block's position, k the expression's number within the "
                                + "block), <saved filter row_id>#f<k> for a saved filter, <page "
                                + "row_id>#gfilter<k> for a dashboard prompt on a page."),
                        estate(),
                        KbColumn.of("scope", "Which kind of row: 'analysis' (an expression of a filter "
                                + "inside a report), 'saved_filter' (an expression of a saved filter "
                                + "object) or 'dashboard' (a dashboard prompt placed on a page, which "
                                + "restricts every report on that page).")
                                .note("Decides what owner_id points at. A 'dashboard' row has a reference "
                                        + "and no expression: op, expr_type and expr_text are empty."),
                        KbColumn.of("owner_id", "The report, the saved filter or the dashboard page the row "
                                + "belongs to.")
                                .note("Polymorphic: analyses.row_id when scope is 'analysis', "
                                        + "saved_filters.row_id when 'saved_filter', dashboard_pages.row_id "
                                        + "when 'dashboard'. A report's criteria block is only in the "
                                        + "<i> of row_id."),
                        KbColumn.of("position", "The row's number among the rows of its owner (for a "
                                + "report: of its criteria block), in source order" + POSITION_FROM_0)
                                .note("A filter is a tree of expressions written parent first, then its "
                                        + "children in order (pre-order). Use parent_id and depth to "
                                        + "rebuild the tree; position alone does not show the grouping."),
                        KbColumn.of("op", "The expression's operator, as the source wrote it: 'and', 'or', "
                                + "'equal', 'in', 'between', 'prompted' and others; empty when it has none.")
                                .note("Never checked against a list. 'prompted' means the value comes from "
                                        + "a dashboard prompt at run time and is not in the report."),
                        KbColumn.of("expr_type", "The type the source gives the expression, in the source's "
                                + "own words: 'sawx:logical' (an and/or group), 'sawx:comparison', "
                                + "'sawx:sqlExpression' (a column or formula), 'xsd:string' (a literal), "
                                + "'sawx:savedFilter' (a reference to a saved filter) and others.")
                                .note("Never checked against a list."),
                        KbColumn.of("expr_text", "The text of the expression.")
                                .note(OWN_TEXT_NOTE + " Empty for a 'dashboard' row."),
                        KbColumn.of("reference", "The catalog path the row names, as the source wrote it: "
                                + "the saved filter an expression refers to, or the dashboard prompt placed "
                                + "on the page; empty when the expression names none.")
                                .note(VERBATIM),
                        KbColumn.of("target_id", "The Row identifier of the catalog object the reference "
                                + "points at; empty when there is no reference, when it was not resolved, "
                                + "or when it did not resolve.")
                                .note("Read resolution first. <estate>::cat::<path> of whatever is at that "
                                        + "path: a row of saved_filters for a saved-filter reference that "
                                        + "resolved to a saved filter; for a dashboard prompt, an object "
                                        + "of a type that is not read, with no table. A value with no "
                                        + "matching row is not an error."),
                        KbColumn.of("resolution", "How the reference resolved: 'exact', 'case_normalised', "
                                + "'ambiguous' or 'unresolved'; empty when no resolution was attempted.")
                                .note("Attempted for an expression of type 'sawx:savedFilter' that has a "
                                        + "path, and for every 'dashboard' row. 'ambiguous' and "
                                        + "'unresolved' rows have a row in errors.csv. A 'dashboard' row "
                                        + "repeats the outcome of the dashboard_contents row for the same "
                                        + "placement; the reference is resolved once."),
                        KbColumn.of("uses_eval", "'true' when the expression, or one nested in it, calls a "
                                + "database function directly (EVALUATE).")
                                .note(FLAG_NOTE + " Every row that encloses such a call is flagged, not "
                                        + "only the call itself."),
                        KbColumn.of("parent_id", "The row of the expression that directly encloses this "
                                + "one; empty for the outermost expression of a filter and for 'dashboard' "
                                + "rows.")
                                .references("analysis_filters", "row_id")
                                .note("The nearest enclosing expression of the same filter; source elements "
                                        + "between the two that are not expressions are passed over."),
                        KbColumn.of("depth", "How many expressions of the same filter enclose this one: 0 "
                                + "for the outermost; a whole number.")),
                estate -> {
                    List<String[]> rows = new ArrayList<>();
                    for (AnalysisFilter f : estate.analysisFilters) {
                        rows.add(new String[] {f.rowId, f.rpdId, f.scope, f.ownerId, Integer.toString(f.position),
                                f.op, f.exprType, f.exprText, f.reference, f.targetId, f.resolution,
                                flag(f.usesEval), f.parentId, Integer.toString(f.depth)});
                    }
                    return rows;
                });
    }

    private static KbTable analysisPrompts() {
        return new KbTable("analysis_prompts", KbTable.CAT,
                "The prompts defined inside reports. Dashboard prompts are separate catalog objects, which "
                        + "are not read, so this table holds only the few prompts that sit inside a report.",
                "One prompt inside one report.",
                true,
                List.of(
                        KbColumn.key("row_id", "Row identifier: <analysis row_id>#p<n>, n being the "
                                + "prompt's position."),
                        estate(),
                        KbColumn.of("analysis_id", "The report the prompt is in.")
                                .references("analyses", "row_id"),
                        KbColumn.of("position", "The prompt's number among the prompt elements of its "
                                + "report, in source order" + POSITION_FROM_0)
                                .note("An untyped prompt element is a plain text parameter: it has no row "
                                        + "and still uses up a number, so the numbers can have gaps."),
                        KbColumn.of("prompt_type", "The prompt's type in the source's own words, for "
                                + "example 'saw:columnFilterPrompt'."),
                        KbColumn.of("column_type", "The type the source gives the expression the prompt "
                                + "asks about; empty when it has none."),
                        KbColumn.of("column_formula", "The column or formula the prompt asks about; empty "
                                + "when it has none.")
                                .note(WHOLE_TEXT_NOTE),
                        KbColumn.of("set_variable", "The variables the prompt sets, joined with ' | '; "
                                + "empty when it sets none.")
                                .note("A formula elsewhere in the report may read such a variable."),
                        KbColumn.of("operator", "The operator the prompt applies, for example 'in'; empty "
                                + "when it states none."),
                        KbColumn.of("ui_control", "The kind of control the prompt shows, in the source's own "
                                + "words, for example 'saw:browse'; empty when it states none.")
                                .note("Labels, default values and choice lists are not read.")),
                estate -> {
                    List<String[]> rows = new ArrayList<>();
                    for (AnalysisPrompt p : estate.analysisPrompts) {
                        rows.add(new String[] {p.rowId, p.rpdId, p.analysisId, Integer.toString(p.position),
                                p.promptType, p.columnType, p.columnFormula, p.setVariable, p.operator,
                                p.uiControl});
                    }
                    return rows;
                });
    }

    private static KbTable savedFilters() {
        return new KbTable("saved_filters", KbTable.CAT,
                "The saved filters in the Web Catalog: filters kept as objects of their own, which reports "
                        + "refer to by path. The conditions of each are in analysis_filters.",
                "One saved filter object.",
                true,
                List.of(
                        KbColumn.key("row_id", "Row identifier: <estate>::cat::<catalog_path>."),
                        estate(),
                        KbColumn.of("name", "The saved filter's name: the last part of its path."),
                        KbColumn.of("catalog_path", "The path of the saved filter below the catalog root.")
                                .note(CATALOG_PATH_NOTE),
                        KbColumn.of("xml_version", "The version the object declares in the source.")
                                .note(XML_VERSION_NOTE),
                        KbColumn.of("op", "The operator of the filter's outermost expression; empty when it "
                                + "has none.")
                                .note("Usually 'and': the group that holds the conditions. The conditions "
                                        + "are the analysis_filters rows with scope 'saved_filter' whose "
                                        + "owner_id is this row; the outermost expression is the one with "
                                        + "position 0."),
                        KbColumn.of("expr_type", "The type the source gives the outermost expression, in the "
                                + "source's own words; empty when the filter holds no expression."),
                        KbColumn.of("expr_text", "The outermost expression's own text; empty when the filter "
                                + "holds no expression.")
                                .note(OWN_TEXT_NOTE + " It is the same value as expr_text of the "
                                        + "analysis_filters row with position 0 of this saved filter."),
                        KbColumn.of("uses_eval", "'true' when any expression of the filter calls a database "
                                + "function directly (EVALUATE).")
                                .note(FLAG_NOTE)),
                estate -> {
                    List<String[]> rows = new ArrayList<>();
                    for (SavedFilter f : estate.savedFilters) {
                        rows.add(new String[] {f.rowId, f.rpdId, f.name, f.catalogPath, f.formatVersion, f.op,
                                f.exprType, f.exprText, flag(f.usesEval)});
                    }
                    return rows;
                });
    }

    private static KbTable reportLineage() {
        return new KbTable("report_lineage", KbTable.CAT,
                "Each report column followed towards the repository: the presentation column its formula "
                        + "names and, through repository lineage, the logical and physical column; or the "
                        + "reason there is no link. This is the only table that joins the Web Catalog to "
                        + "the repository.",
                "One report column: exactly one row for every row of analysis_columns.",
                true,
                List.of(
                        KbColumn.key("row_id", "Row identifier: <analysis_columns row_id>#lineage."),
                        estate(),
                        KbColumn.of("analysis_id", "The report the column belongs to.")
                                .references("analyses", "row_id"),
                        KbColumn.of("criteria_id", "The criteria block the column belongs to.")
                                .references("analysis_criteria", "row_id"),
                        KbColumn.of("analysis_column_id", "The report column this row is about.")
                                .references("analysis_columns", "row_id"),
                        KbColumn.of("subject_area", "The subject area the criteria block names, as the "
                                + "catalog wrote it, quotes included; empty when the block names none.")
                                .note(VERBATIM + " The same value as analysis_criteria.subject_area. A "
                                        + "value starting with XSA( names an uploaded dataset, not a "
                                        + "subject area."),
                        KbColumn.of("subject_area_id", "The subject area of this estate's repository with "
                                + "that name; empty when there is none.")
                                .references("presentation_subject_areas", "row_id")
                                .note("Matched by name: the double quotes at both ends are taken off, then "
                                        + "the name is looked up exactly and, failing that, ignoring letter "
                                        + "case. Only presentation_subject_areas.name is used, not its "
                                        + "aliases. Filled whether or not the column itself resolves, so a "
                                        + "filled value here with resolution 'unresolved' means the "
                                        + "subject area exists and the column was not found in it."),
                        KbColumn.of("reference_text", "The report column's formula, as stored in "
                                + "analysis_columns.formula.")
                                .note(VERBATIM + " For the lookup only, white space at both ends is "
                                        + "ignored."),
                        KbColumn.of("presentation_column_id", "The presentation column the formula names; "
                                + "empty unless resolution is 'exact' or 'case_normalised'.")
                                .references("presentation_columns", "row_id")
                                .note("Matched by subject area name, presentation table name and column "
                                        + "name within the estate. Only the name columns are used, never "
                                        + "aliases or display names. If two presentation columns differ "
                                        + "only in letter case and neither matches exactly, the one read "
                                        + "last from the repository is taken."),
                        KbColumn.of("resolution", "How the formula was linked: 'exact', 'case_normalised', "
                                + "'unresolved', 'not_a_column_reference' or 'outside_semantic_model'.")
                                .note("Read this first. 'exact': the formula is nothing but "
                                        + "\"Table\".\"Column\" and the names match as written. "
                                        + "'case_normalised': they match only when letter case is ignored. "
                                        + "'unresolved': the formula is such a reference and the estate has "
                                        + "no presentation column with those names (or the criteria block "
                                        + "names no subject area, or the estate has no repository; "
                                        + "manifest.csv says which halves were present). "
                                        + "'not_a_column_reference': the formula is empty or is a "
                                        + "calculation; the columns inside a calculation are NOT looked "
                                        + "for, so read reference_text. 'outside_semantic_model': the "
                                        + "formula starts with XSA( , an uploaded dataset that was never in "
                                        + "the repository. Only the first three say anything about a "
                                        + "lookup; the last two are facts about the formula."),
                        KbColumn.of("logical_column_id", "The logical column behind the presentation "
                                + "column, from repository lineage; empty unless source_state is 'sourced'.")
                                .references("logical_columns", "row_id")
                                .note("For a column that is 'computed_not_sourced' this is empty; "
                                        + "presentation_columns.logical_column_id still gives its logical "
                                        + "column, and logical_columns.derived_expr_refs what that is "
                                        + "computed from."),
                        KbColumn.of("physical_column_id", "The physical column the logical column is "
                                + "mapped to, from repository lineage; empty when there is none.")
                                .references("physical_columns", "row_id")
                                .note("ONE of possibly several. When lineage has more than one row for the "
                                        + "presentation column (one per table source that maps it), this "
                                        + "row uses the first of them in lineage.csv order, that is the "
                                        + "one with the smallest row_id. It is a fixed choice, not the "
                                        + "source the BI Server would pick. For every path, read lineage "
                                        + "where presentation_column_id is this row's."),
                        KbColumn.of("source_state", "Whether the presentation column reaches a database "
                                + "column: 'sourced', 'computed_not_sourced', or empty.")
                                .note("'sourced': lineage has a row for the presentation column (its "
                                        + "physical_column_id can still be empty, when the mapping reads no "
                                        + "physical column). 'computed_not_sourced': the presentation "
                                        + "column was found and lineage has no row for it, because it is "
                                        + "calculated in the business model or has no logical column. "
                                        + "Empty: no presentation column was found.")),
                estate -> {
                    List<String[]> rows = new ArrayList<>();
                    for (ReportLineageRow r : estate.reportLineage) {
                        rows.add(new String[] {r.rowId, r.rpdId, r.analysisId, r.criteriaId,
                                r.analysisColumnId, r.subjectArea, r.subjectAreaId, r.referenceText,
                                r.presentationColumnId, r.resolution, r.logicalColumnId, r.physicalColumnId,
                                r.sourceState});
                    }
                    return rows;
                });
    }

    private static KbTable catalogVersions() {
        return new KbTable("catalog_versions", KbTable.RUN,
                "How many Web Catalog objects of each type declare each format version.",
                "One object type with one declared version, and how many objects have both.",
                true,
                List.of(
                        estate(),
                        KbColumn.of("object_type", "The object's type: the full name of its root element, as "
                                + "{namespace}local-name, or the bare name when it has no namespace.")
                                .note("Covers every catalog file that is XML, including types that are not "
                                        + "read; files that are not XML are not counted here."),
                        KbColumn.of("xml_version", "The version the objects declare, or '(none)' when they "
                                + "declare none.")
                                .note("'(none)' is written here only; the xml_version columns of other tables "
                                        + "are empty in that case."),
                        KbColumn.of("objects", "How many objects; a whole number.")),
                estate -> {
                    List<String[]> rows = new ArrayList<>();
                    for (String[] row : estate.catalogVersions()) {
                        rows.add(new String[] {row[0], row[1], row[2].isEmpty() ? "(none)" : row[2], row[3]});
                    }
                    return rows;
                });
    }

    private static KbTable coverageSummary() {
        return new KbTable("coverage_summary", KbTable.RUN,
                "What share of the Web Catalog's files was read: files seen, files that are XML, files that "
                        + "are not catalog objects, objects of a type that is read, and objects of a type that "
                        + "is not.",
                "One count of catalog files for one estate.",
                true,
                List.of(
                        estate(),
                        KbColumn.of("measure", "What is counted: files_seen, parsed_as_xml, "
                                + "not_a_catalog_object, modelled_object or unmodelled_object.")
                                .note("parsed_as_xml + not_a_catalog_object = files_seen, less any file "
                                        + "that could not be read (errors.csv). modelled_object + "
                                        + "unmodelled_object = parsed_as_xml, less any object left out for "
                                        + "its name (errors.csv, invalid_name). A measure that counted "
                                        + "nothing has no row; an estate with no catalog has no rows."),
                        KbColumn.of("count", "How many files; a whole number."),
                        KbColumn.of("of_files_seen", "The estate's files_seen count, repeated on every row; a "
                                + "whole number."),
                        KbColumn.of("percent", "count as a percentage of of_files_seen, rounded to a whole "
                                + "number and followed by '%'; 'n/a' when no file was seen.")
                                .note("Rounded, so the rows need not add up to 100%. Use count for "
                                        + "arithmetic.")),
                Estate::censusSummary);
    }

    private static KbTable unparsed() {
        return new KbTable("unparsed", KbTable.RUN,
                "Known gaps: content that is in the source and is not carried by any Knowledge Base table. "
                        + "Nothing here is a failure.",
                "One kind of content that was not read, for one object type, with how often it occurs.",
                true,
                List.of(
                        estate(),
                        KbColumn.of("kind",
                                "What sort of gap: unmodelled_type (a whole object type with no reader), "
                                        + "unread_attribute, unread_element, unread_text (parts of an object "
                                        + "that was read), undiscovered_file (a file the reader never "
                                        + "opened), not_a_catalog_object (files in the catalog that are not "
                                        + "XML: images, templates, documents), excluded_by_scope (styling "
                                        + "attributes, left out on purpose), undecoded_name (a catalog file "
                                        + "name with a malformed escape) or redacted_value.")
                                .note("Read kind first: it decides what object_type and detail mean."),
                        KbColumn.of("object_type",
                                "The type of source object the gap is in: for a repository, the object-type "
                                        + "folder of the file (for example PhysicalTable); for a catalog, the "
                                        + "full name of the object's root element; 'File' for an undiscovered "
                                        + "file, a file that is not a catalog object, or a file name that "
                                        + "could not be decoded."),
                        KbColumn.of("detail",
                                "What was not read: 'Tag@attribute' for an attribute, 'Tag' for an element, "
                                        + "'Tag#text' for text, a file name for unmodelled_type, a path for "
                                        + "undiscovered_file, the variable's name and the reason for "
                                        + "redacted_value. For a catalog: unmodelled_type gives the number "
                                        + "of objects and the path of one of them, not_a_catalog_object "
                                        + "gives what the files are, and tags are full names, "
                                        + "{namespace}local-name.")
                                .note("An element is named by its tag alone, not by its path, so the same tag "
                                        + "under different parents is one row."),
                        KbColumn.of("occurrences",
                                "How many times it occurs across all objects of that type; for a catalog's "
                                        + "unmodelled_type and not_a_catalog_object rows, how many files; a "
                                        + "whole number.")),
                estate -> {
                    List<String[]> rows = new ArrayList<>();
                    for (Unparsed u : estate.unparsed) {
                        rows.add(new String[] {u.rpdId, u.kind, u.objectType, u.detail,
                                Integer.toString(u.occurrences)});
                    }
                    return rows;
                });
    }

    private static KbTable errors() {
        return new KbTable("errors", KbTable.RUN,
                "Things that went wrong: content that should have been captured and was not. Empty when the "
                        + "run was clean.",
                "One problem found during the run.",
                true,
                List.of(
                        estate(),
                        KbColumn.of("kind",
                                "What went wrong: malformed_xml, unreadable, missing_name, invalid_name, "
                                        + "unresolved_reference, ambiguous_reference or duplicate_identifier.")
                                .note("Read kind first: it decides what source_id holds."),
                        KbColumn.of("source_id",
                                "What the problem is about: a Row identifier where one exists, otherwise a "
                                        + "file name, a path, or the source's own id for the object. For a "
                                        + "catalog problem: the catalog path of the object that holds the "
                                        + "reference (the catalog_path of a dashboard, a page, a report or "
                                        + "a saved filter).")
                                .note("Not a reliable join column: only some values are Row identifiers, and "
                                        + "for duplicate_identifier the identifier names more than one row."),
                        KbColumn.of("object_type",
                                "The type of object the problem is in; for duplicate_identifier, the table "
                                        + "that holds the duplicates; for a catalog reference that did not "
                                        + "resolve, the full name of the referring element.")
                                .note("Some values are not real object types: 'Directory', 'File', 'Catalog', "
                                        + "'ExpressionRef' and 'RefPresentationColumn' mark where the "
                                        + "problem was found."),
                        KbColumn.of("reason", "A sentence saying what happened.")
                                .note("May contain text from the XML parser or the operating system, which can "
                                        + "span several lines.")),
                estate -> {
                    List<String[]> rows = new ArrayList<>();
                    for (ExtractionError e : estate.errors) {
                        rows.add(new String[] {e.rpdId, e.kind, e.sourceId, e.objectType, e.reason});
                    }
                    return rows;
                });
    }

    private static KbTable coverage() {
        return new KbTable("coverage", KbTable.RUN,
                "How many source objects of each type were read into the Knowledge Base and how many were "
                        + "skipped.",
                "The counts for one object type in one store of one estate.",
                true,
                List.of(
                        estate(),
                        KbColumn.of("layer", "The store the objects come from: 'rpd' for the repository, 'cat' for the Web Catalog.")
                                .note("This is the store, not the layer part of a Row identifier."),
                        KbColumn.of("object_type",
                                "The source object type. Parts of an object are counted under their own type, "
                                        + "for example PhysicalColumn. For a catalog: the full name of the "
                                        + "object's root element.")
                                .note("Each errors.csv row adds one to 'skipped' under its own object_type, so "
                                        + "marker types such as 'ExpressionRef' and 'Directory' appear here, "
                                        + "and so do catalog reference elements such as reportRef: their "
                                        + "'skipped' counts references that did not resolve, not objects."),
                        KbColumn.of("parsed", "How many objects of this type were read; a whole number."),
                        KbColumn.of("skipped",
                                "How many were not read: one for each object of a type with no reader, and "
                                        + "one for each error; a whole number.")
                                .note("An object can be counted in both columns: read, with one of its "
                                        + "references in error.")),
                estate -> {
                    List<String[]> rows = new ArrayList<>();
                    for (Coverage c : estate.coverage()) {
                        rows.add(new String[] {c.rpdId, c.layer, c.objectType, Integer.toString(c.parsed()),
                                Integer.toString(c.skipped())});
                    }
                    return rows;
                });
    }

    // ------------------------------------------------------------------ the Knowledge Base describing itself

    private static KbTable schemaTables() {
        return new KbTable(SCHEMA_TABLES, KbTable.RUN,
                "The list of tables in this Knowledge Base and what each holds. Start here.",
                "One table (one CSV file) of the Knowledge Base.",
                false,
                List.of(
                        KbColumn.key("table", "The table's name; its file is <table>.csv."),
                        KbColumn.of("description", "What the table holds."),
                        KbColumn.of("row_meaning", "What one row of the table stands for."),
                        KbColumn.of("key_column",
                                "The column that identifies a row; empty when no single column does.")
                                .note("A key can repeat when the source has two objects with the same names; "
                                        + "errors.csv reports each case as duplicate_identifier."),
                        KbColumn.of("layer",
                                "Where the table's content comes from: phys, bmm or pres (repository layers), "
                                        + "var (repository variables), sec (repository security), cat (Web "
                                        + "Catalog) or run (about the extraction itself).")),
                estate -> schemaTableRows(TABLES));
    }

    /** The {@code schema_tables.csv} rows that describe the given tables, in the order given. */
    public static List<String[]> schemaTableRows(List<KbTable> tables) {
        List<String[]> rows = new ArrayList<>();
        for (KbTable t : tables) {
            rows.add(new String[] {t.name, t.description, t.rowMeaning, t.keyColumn(), t.layer});
        }
        return rows;
    }

    /** The {@code schema.csv} rows that describe the columns of the given tables, in the order given. */
    public static List<String[]> schemaColumnRows(List<KbTable> tables) {
        List<String[]> rows = new ArrayList<>();
        for (KbTable t : tables) {
            int position = 1;
            for (KbColumn c : t.columns) {
                rows.add(new String[] {t.name, Integer.toString(position++), c.name, c.meaning,
                        c.isKey ? "true" : "false", c.referencesTable, c.referencesColumn, c.note});
            }
        }
        return rows;
    }

    private static KbTable schemaColumns() {
        return new KbTable(SCHEMA_COLUMNS, KbTable.RUN,
                "Every column of every table: what it means and which other table it points at.",
                "One column of one table.",
                false,
                List.of(
                        KbColumn.of("table", "The table the column is in.")
                                .references(SCHEMA_TABLES, "table"),
                        KbColumn.of("position", "The column's place in the file, counting from 1."),
                        KbColumn.of("column", "The column's name, as in the file's header row."),
                        KbColumn.of("meaning", "What the column holds."),
                        KbColumn.of("is_key", "'true' when the column identifies a row of its table, otherwise 'false'."),
                        KbColumn.of("references_table",
                                "The table whose rows this column's values point at; empty when it points at "
                                        + "none.")
                                .references(SCHEMA_TABLES, "table"),
                        KbColumn.of("references_column",
                                "The column in references_table that holds the matching value.")
                                .note("Join on equality. An empty value in the referring column means the "
                                        + "source had no link, or the link did not resolve."),
                        KbColumn.of("note", "Anything to know before joining on or parsing the column; empty when there is nothing.")),
                estate -> schemaColumnRows(TABLES));
    }

    private static KbTable manifest() {
        return new KbTable(MANIFEST, KbTable.RUN,
                "What produced this Knowledge Base, from what, and the list of its tables.",
                "One fact about the run, as a key and a value.",
                false,
                List.of(
                        KbColumn.key("key",
                                "The fact's name: extractor_version, extracted_at, corpus_root, rpd_ids, "
                                        + "halves_present:<estate>, pairing:<estate> or tables."),
                        KbColumn.of("value", "The fact's value.")
                                .note("For 'tables': the table names separated by single spaces; read the "
                                        + "table list from here, not from the folder. 'extracted_at' is the "
                                        + "only time stamp in the Knowledge Base.")),
                null);
    }
}
