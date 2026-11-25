# POC-OBIEE-2-PBI

One Java 11 jar, `obi2pbi.jar`, with no third-party libraries. It reads an OBIEE estate (a repository export, a Web Catalog, or both) and writes a Knowledge Base of CSV tables. It is a port of the first attempt's Python extractor, kept in `reference/python-extractor/`.

This file covers building the jar, fetching the sample data, laying out an estate folder, running `extract`, reading what it writes, and checking it against the first attempt's output. It also has `slice`, which cuts a Knowledge Base down to one subject area and writes small packets for the skills, `assemble-doc`, which joins the parts a skill wrote into the document, and `check-doc`, which checks a skill-written document against its Slice; all three are described below. `extract` is complete: it writes every table of the Knowledge Base, from the repository, from the Web Catalog (dashboards, their pages and what is placed on them, reports with their columns, views, filters and prompts, saved filters), and the link between the two (`report_lineage`). For one FSD (a Word document describing a new report tab) there are three more, also described below: `fsd-text`, which turns the `.docx` into text with an anchor on every paragraph and table cell, `find-columns`, which lists the OBIEE columns a business term may mean, and `check-matches`, which checks the matches the trace skill proposes and the decisions the Operator records. `find-pages` finds the Row identifier of the page a tab replaces, and `slice --tab` cuts the Slice of one tab from its confirmed decisions.

**Note on `reference/`.** The first attempt's Knowledge Base, sample files and Python source, which `compare` and `compare-all` use to check the port, are kept on the development machine only and are not in this repository: they contain values from public sample data that look like credentials. Everything else builds and all tests pass without them.

## From an OBIEE export to a to-be design, step by step

The whole chain for one tab. Steps marked *jar* are commands; steps marked *agent* are done by starting the migration agent (`obiee-migration-agent`) in GitHub Copilot or Claude and choosing the menu item named. Every AI-written output starts as a draft; run its review in a new chat before sharing it.

| # | Step | How |
|---|---|---|
| 1 | Build the Knowledge Base | *jar* `java -jar build/obi2pbi.jar extract estates/<estate>`, then read `kb/<estate>/errors.csv` and `unparsed.csv` |
| 2 | Describe today's semantic model, once per subject area | *agent* "Describe a subject area" (it runs `slice` itself); then, in a new chat, "Review a document" |
| 3 | Answer the open questions of the as-is document | Fill the `Answer:` lines, or tell the agent; *agent* "Take in answers to open questions" |
| 4 | Put the FSD in place | Copy the `.docx` to `work/<tab>/fsd.docx` |
| 5 | Trace the FSD | *agent* "Trace an FSD" (it runs `fsd-text` and `check-matches` itself); then, in a new chat, "Review the matches of an FSD" |
| 6 | Decide the matches | You write `work/<tab>/decisions.csv` yourself (columns `item_id`, `decision`, `row_id`, `note`; see "Record the decisions" below), then *jar* `java -jar build/obi2pbi.jar check-matches work/<tab> --kb kb/<estate>` |
| 7 | Write the to-be design | *agent* "Write the to-be design" (it runs `slice --tab` itself); then, in a new chat, "Review a to-be design" |
| 8 | Take decisions and hand over | Answer the design's open questions (*agent* "Take in answers to the to-be design's questions"); the old-to-new column list goes to Data Engineering to sign |

Worked examples from the build, all on public sample data: `as-is/sampleapp/sample-sales.md` (step 2, reviewed), `work/sample-sales-monthly/` (step 5, on a test FSD), and `work/test-tab/to-be.md` (step 7, built on **test decisions written by the build agent, not by a person**: see `work/test-tab/README-TEST.md`).

## Build

You need a JDK, version 11 or later. Nothing is downloaded.

```
./build.sh            # Linux, macOS
build.cmd             # Windows
```

This compiles with `javac --release 11`, builds `build/obi2pbi.jar`, and runs every test with the project's own test runner. Each test is printed as `PASS`, `FAIL` or `SKIP`; the build fails if any test fails.

| Command | Does |
|---|---|
| `./build.sh` | Compile, build the jar, run all tests |
| `./build.sh test <text>` | The same, running only tests whose `Class.method` name contains the text |
| `./build.sh compare <estate> [table ...]` | Compare `kb/<estate>` with `reference/knowledge-base/` (see below) |
| `./build.sh skills` | Copy each folder of `skills/` unchanged to `.agents/skills/` and `.claude/skills/`. The agent and its skills are edited only under `skills/`; `SkillCopiesTest` fails the build when a copy differs |
| `./build.sh compare-all` | Extract the three sample estates, compare every table with the reference, and write `documents/implementation-artifacts/kb-comparison-report.md` (see below) |

## Fetch the sample repositories

Two public OBIEE repositories are used to check the jar. They are not kept in this project (they are other people's work, with no licence). To fetch them, at the commits the reference Knowledge Base was made from:

```
mkdir -p ~/obiee-corpus && cd ~/obiee-corpus
git clone https://github.com/VasiliKorzun/BI-Analytics-2013.git
git -C BI-Analytics-2013 checkout 19bef42a9574a039a90a40d986c10194aa3376f5
git clone https://github.com/rvijay17/obiee.git
git -C obiee checkout 9a1c0a28211d981f320e38247b416c754c334398
```

Then, from this project's folder, make one estate folder for each, holding only the repository files (the clones carry other material that is not repository metadata):

```
for pair in "bi-analytics-2013:BI-Analytics-2013" "sampleapp:obiee"; do
  estate="${pair%%:*}"; clone="${pair##*:}"
  mkdir -p "estates/$estate/rpd/oracle/bi/server"
  cp -r ~/obiee-corpus/"$clone"/oracle/bi/server/base "estates/$estate/rpd/oracle/bi/server/base"
done
```

`estates/<estate>/rpd` may instead be a link to a folder that holds `oracle/bi/server/base/`. Only `<ObjectType>/*.xml` below that is read; any other file below `rpd` is listed in `unparsed.csv` as `undiscovered_file`. Expect 1252 XML files for `bi-analytics-2013` and 131 for `sampleapp`. The estate names must be exactly these: they are the first part of every Row identifier, and the comparison pairs rows by identifier. `reference/test-corpus/corpus.md` has more on where the samples come from.

## Lay out an estate folder

An estate is one OBIEE system: its repository, its Web Catalog, or both. Put the input in one folder per estate. The folder's name is the estate name: lower-case letters and digits, with single hyphens between groups (`finance-prod`). It becomes the first part of every Row identifier, so choose it once and keep it.

```
estates/
  <estate>/
    rpd/                                    the repository export (MDS XML)
      oracle/bi/server/base/
        PhysicalTable/*.xml
        LogicalTable/*.xml
        PresentationTable/*.xml
        ...                                 one folder per object type
    catalog/                                the Web Catalog, as the folder tree OBIEE keeps on disk
      shared/...
      users/...
```

| Rule | Detail |
|---|---|
| Either half may be absent | `rpd/` alone, `catalog/` alone, or both. `manifest.csv` says which were present (`halves_present:<estate>`) |
| Both halves in one folder means they belong together | That is the only thing that pairs a repository with a catalog. Nothing is inferred from names. Put a repository and a catalog in one estate folder only when they come from the same OBIEE system |
| One estate per run | `extract` takes one estate folder. Two systems are two folders and two Knowledge Bases |
| Links are allowed | `rpd` and `catalog` may be links to folders elsewhere |
| The folders must have these names | `rpd` and `catalog`, lower case. A bare repository tree given as the estate folder is refused with a hint |
| A BAR archive must be unpacked first | The catalog is read as a folder tree |

When both halves are there, `report_lineage.csv` links each report column to the repository by name: subject area, presentation table, presentation column. With one half only, the table is still written; with a catalog alone every report column is `unresolved` or marked as not a column reference.

## Extract a Knowledge Base

Put the input in an estate folder as above.

Every file under `catalog/` is tried, because catalog objects have no file extension; only folders and files named `.git` are left out. A file that is not XML (an image, a template, a `.gitignore`) is counted in `unparsed.csv` as `not_a_catalog_object`; it is never an error.

Then:

```
java -jar build/obi2pbi.jar extract estates/<estate>
```

| Argument | Meaning |
|---|---|
| `<estate-folder>` | The folder to read |
| `--out <folder>` | Where to write. Default: `kb/<estate>` under the current folder |
| `--timestamp <text>` | The time stamp for `manifest.csv`, for repeatable output. Default: the current time |

The command prints the counts and the output folder:

```
estate:   sampleapp (rpd)
parsed:   559 objects
skipped:  15 objects
gaps:     145 not modelled  -> kb/sampleapp/unparsed.csv
errors:   0                 -> kb/sampleapp/errors.csv
written:  kb/sampleapp
```

`parsed` and `skipped` are the totals of `coverage.csv`. A part of an object (a column, a level) is counted under its own type, so `parsed` is larger than the number of files. The first line names the halves that were read: `(rpd)`, `(catalog)` or `(rpd + catalog)`. For a catalog, `skipped` also counts each reference that did not resolve. `gaps` is the number of rows in `unparsed.csv` and `errors` the number of rows in `errors.csv`.

| Exit code | Meaning |
|---|---|
| 0 | Written; `errors.csv` has no rows |
| 3 | Written; `errors.csv` has rows. Read them before using the Knowledge Base |
| 2 | Wrong usage; nothing was written |

The output folder is rewritten on every run: its top-level `.csv` files are removed first, and anything else in it is left alone. A folder that is not empty and has no `manifest.csv` was not written by this program and is refused.

A bad source file or object does not stop the run. It becomes a row in `errors.csv`.

## Read `errors.csv` and `unparsed.csv` first

These two files say how far the Knowledge Base can be trusted. They are kept apart on purpose: one is "this went wrong", the other is "this was never read".

**`errors.csv`: something that should have been captured was not.** Exit code 3 means it has rows. Columns: `rpd_id`, `kind`, `source_id`, `object_type`, `reason`.

| `kind` | Means | What to do |
|---|---|---|
| `malformed_xml`, `unreadable` | A source file could not be parsed or read. `source_id` is the file name. (A catalog file that does not parse is not an error: see `not_a_catalog_object` below) | Nothing from that file is in the Knowledge Base. Fix or re-export the file |
| `missing_name`, `invalid_name` | An object has no name, or a name that cannot be part of a Row identifier (only spaces, or containing `::`). `source_id` is the source's own id for the object | That object, and its parts, are left out |
| `unresolved_reference` | A link points at something that was not found. `source_id` is the Row identifier of the row that holds the link, where it has one; for a catalog, the catalog path of the dashboard, page, report or saved filter that holds it, and `object_type` is the referring element (`...expression/v1.1}expr` for a filter that names a saved filter) | The row is still written, with the link empty (in a `..._refs` cell, an empty line). Do not read an empty link as "no link" without looking here. In a catalog export most of these point at `/company_shared/` or another root that was not exported |
| `ambiguous_reference` | A catalog path matches several objects once letter case is ignored | The link is left empty; the reason names the candidates |
| `duplicate_identifier` | Two rows of one table have the same Row identifier, because the source has two objects with the same names | Both rows are written. A join on that identifier matches both |

**`unparsed.csv`: known gaps. Nothing here is a failure.** Columns: `rpd_id`, `kind`, `object_type`, `detail`, `occurrences`.

| `kind` | Means |
|---|---|
| `unmodelled_type` | A whole object type has no reader. One row per file. In the samples: `QueryPrivilege`, `Project`, `LogicalSourceFolder`, `User` |
| `unread_attribute`, `unread_element`, `unread_text` | A part of an object that was read, which no table carries. `detail` is `Tag@attribute`, `Tag` or `Tag#text`; `occurrences` is how many times it occurs. Example: `PhysicalKey@name` is the name of a physical key; no table carries it. For keys and roles the count is of the ones nothing points at: a key a foreign key points at, and a role that holds a privilege package, are read through that link |
| `undiscovered_file` | A file under the repository folder that is not where repository files are expected |
| `unmodelled_type` (catalog) | One row per object type, not per file: `detail` gives the number of objects and the path of one. In the sample: standalone views (which hold the dashboard prompts), scorecards, KPIs, actions, agents and BI Publisher objects |
| `not_a_catalog_object` | Files in the catalog that are not XML, one row per kind of file (PNG image, RTF template, ...), with a count |
| `excluded_by_scope` | Styling attributes of catalog objects (colour, width, font, ...). Left out on purpose and still counted |
| `undecoded_name` | A catalog file whose name on disk holds an escape that cannot be decoded. The name is kept as far as it decodes |
| `redacted_value` | A variable's value was withheld as a credential (see below). `detail` names the variable and the reason |

These rows are found by observation, not from a list: every read of the source is recorded, and whatever was present and never read is reported. If a question needs something that is listed here, the Knowledge Base cannot answer it; go to the source.

`coverage.csv` gives the counts behind both: for each object type, how many were read and how many were skipped. Each `errors.csv` row adds one to `skipped`. For a catalog, `coverage_summary.csv` says what share of the files was read (files seen, XML, not catalog objects, of a type that is read, of a type that is not), and `catalog_versions.csv` counts the objects of each type by the format version they declare.

**Credentials.** A variable's value is withheld when the variable's name suggests a credential (it contains `password`, `passwd`, `pwd`, `secret`, `token`, `credential`, `apikey` or `api key`, in any letter case), and also when its value is the same as such a variable's value. The row stays in `variables.csv` with `redacted` = `true` and its three value cells empty. This is a rule about names: a credential under an innocent name, with a value found nowhere else, is written out, and so is one inside an init block's query (`init_blocks.init_string` is never withheld). Check before sharing a Knowledge Base outside the team.

**What the repository reader deliberately leaves unread.** These stay in `unparsed.csv` on purpose: they do not change what a table or a number is. Occurrences in `sampleapp` / `bi-analytics-2013`:

| Left unread | `sampleapp` | `bi-analytics-2013` |
|---|---:|---:|
| Row counts (`rowCount`) and update times (`lastUpdated`) of tables and columns | 6 / 6 | 10 / 10 |
| Cache settings (`isCacheable`, `cacheExpiry`, `pollFreq`) | 5 | 14 |
| A level's constant and flag (`levelConst`, `isFDUC`) | 22 / 22 | 84 / 84 |
| Flags that say a display name or description is set (`hasDispName`, `hasDispDescription`) | 97 / 97 | 1,899 / 1,899 |
| `iconIndex` | 4 | 3 |
| Internal ids (`mdsid`, `refId`, `objectTypeId`) of parts that nothing refers to | 371 / 411 / 233 | 4,859 / 4,398 / 2,973 |
| `MeasureDefn@isCommutative`, `LogicalColumn@isDerived` | 13 / 2 | 22 / 13 |

Also still unread, and not yet judged: the preferred drill path and the columns a level lists outside its keys, a column's custom sort order (6 in `bi-analytics-2013`), the descriptions of variables, init blocks and roles, an init block's per-database texts, connection pool and predecessors, and presentation aliases.

## Reading a Knowledge Base

**Start with the two schema files.** Every Knowledge Base folder describes itself:

| File | Holds |
|---|---|
| `schema_tables.csv` | One row per table: what it holds, what one row stands for, its key column, and its layer (`phys`, `bmm`, `pres`, `cat`, or `run` for tables about the extraction itself) |
| `schema.csv` | One row per column: what it means, whether it is the key, which table and column it refers to, and a note where there is something to know before joining |

To join two tables, find the column in `schema.csv` and use its `references_table` and `references_column`. An empty value in a referring column means the source had no link, or the link did not resolve (then `errors.csv` has a row).

The tables, all 46, in the order of `schema_tables.csv`. Every one is written on every run; a table the estate has nothing for has a header and no rows.

| Table | Holds |
|---|---|
| `physical_tables` | The database tables the repository knows about, with the repository's `description` and, on the relationship table of a parent-child hierarchy, its member, parent, distance and leaf columns |
| `physical_columns` | Their columns: data type, precision, `scale`, nullable, external name, and `position` (the column's place in its table in the repository; sort by it, the rows are in identifier order), and `source_column_id` for a column of an alias |
| `physical_table_definitions` | The SQL of a table that is a query, or the database object's name |
| `physical_joins` | Foreign keys and complex joins between physical tables. Read `kind` first |
| `physical_keys` | The keys declared on physical tables: each key's table, name and columns in order. The repository has no flag that says which key is the primary key |
| `logical_tables` | The tables of the business models |
| `logical_columns` | Their columns, with each formula exactly as written. This is where a measure is defined |
| `logical_keys` | The keys declared on logical tables: each key's table, name, primary flag as written, and columns in order |
| `aggregation_rules` | How each measure is aggregated: the default rule and any rule that replaces it |
| `logical_table_sources` | The ways each logical table is answered from physical tables, with filter, fragment and grain |
| `lts_column_map` | Which physical expression supplies each logical column through each source |
| `lts_tables` | The physical tables each table source is built on, in the source's order |
| `lts_joins` | The joins a table source makes between its tables: join type (`Inner`, `LeftOuter`, ...) and cardinality as written, and the physical join that gives the condition. One join is normally two rows, one under each table |
| `bmm_joins` | The joins between logical tables: inner or outer, and how many rows each side may have |
| `logical_dimensions` | The drill structure behind each hierarchy, with flags for ragged, skip-level and parent-child, and `is_time` for a time dimension |
| `logical_levels` | The levels of each logical dimension, with the column a drill follows, the levels directly below (`child_level_ids`) and directly above (`parent_level_ids`) |
| `logical_level_keys` | Every key of every level: its name, whether it is the level's primary key, a chronological key, the key shown on drill-down (each as written), and its columns in order |
| `presentation_subject_areas` | The subject areas report authors choose from |
| `presentation_tables` | Their tables |
| `presentation_columns` | Their columns, each tied to its logical column |
| `presentation_hierarchies` | The hierarchies a report author sees, each tied to its logical dimension |
| `presentation_levels` | Their levels, in drill order |
| `presentation_level_columns` | The columns shown at each level |
| `lineage` | One row per path: presentation column, logical column, table source, physical table and column |
| `variables` | Repository and session variables with their default values. Read `redacted` first |
| `init_blocks` | The queries that fill variables |
| `security_rls` | Which role holds which query privilege, which permission applies to which object, and the row filters. Read `kind` first |
| `dashboards` | The dashboards in the Web Catalog. The name is the folder's name |
| `dashboard_pages` | Their pages, each with its dashboard and its place in it |
| `dashboard_contents` | What each dashboard and page holds: page references, reports, dashboard prompts, scorecard views, folders, each with how the reference resolved. Read `resolution` first |
| `analyses` | The reports in the Web Catalog (OBIEE calls a report an analysis) |
| `analysis_criteria` | Their criteria blocks: one query definition each, with the subject area as the catalog wrote it, quotes included |
| `analysis_columns` | Their columns, each with its formula exactly as written. This is where a calculation that exists only in a report is defined |
| `analysis_views` | Their views: what each report is shown as, with the view type in the source's own words |
| `analysis_filters` | Filters: every expression of the filters inside reports and of the saved filters, with parent and depth, and the dashboard prompts placed on pages. Read `scope` first |
| `analysis_prompts` | The prompts defined **inside reports only**. Dashboard prompts are separate objects that are not read, so this table is small (4 rows on the sample, where 991 prompts live in objects of their own) |
| `saved_filters` | The saved filter objects, one row each. Their conditions are in `analysis_filters` |
| `report_lineage` | One row per report column: the presentation column its formula names and, through `lineage`, the logical and physical column, or why there is no link. The only table that joins the catalog to the repository. Read `resolution` first |
| `unparsed` | Known gaps: content in the source that no table carries |
| `errors` | Things that went wrong |
| `coverage` | Objects read and skipped, by object type |
| `catalog_versions` | Catalog objects by type and declared format version |
| `coverage_summary` | What share of the catalog's files was read |
| `schema_tables` | One row per table: what it holds and what one row stands for |
| `schema` | One row per column of every table: its meaning, what it refers to, and what to know before using it |
| `manifest` | What produced the folder, and the list of its tables. Take the table list from here, not from the folder |

Things to know:

- Row identifiers look like `<estate>::phys::<database>.<schema>.<table>`. They are built from names. Do not split them on `.`: names can contain dots. Use the `*_id` columns to move between tables.
- A formula is held in three columns: `..._text` is the template (`SUM("%1")`), `..._text_desc` is the same with names filled in, and `..._refs` lists what `%1`, `%2` ... stand for. A `..._refs` cell holds **one Row identifier per line**; line 1 is `%1`. Split it on the line feed only, never on spaces. An empty line is a reference that did not resolve.
- "Where does this presentation column come from" is one lookup in `lineage`. A presentation column with no row there is computed from other logical columns: read `logical_columns.derived_expr_refs`. A row that ends at an alias names the alias; `physical_tables.source_table_id` gives the table behind it.
- Some content changes a number without changing its formula. Look for it in `bmm_joins` (an inner join drops rows that have no partner), in `logical_table_sources.where_*` and `security_rls` rows of kind `row_filter` (a filter on a source), and in `variables` (a value used inside a formula or filter).
- A `..._refs` cell can name rows of several tables: a logical column, a logical level (the second argument of `AGO`), a variable, a physical column. The layer part of the identifier (`bmm`, `var`, `phys`) and its shape say which.
- A hierarchy is never flattened. `logical_dimensions` keeps the three flags `is_ragged`, `is_skipped` and `is_value_based`. A parent-child hierarchy (`is_value_based` = `true`) has no presentation levels; its one column is in `presentation_hierarchies.display_column_id`. For drill order use `presentation_levels.position`, not `logical_levels.position`, which is source order.
- `security_rls` holds three kinds of row, and `kind` decides what the other columns mean. `subject_id` is a name on a `query_privilege` row, a Row identifier of any table on an `object_privilege` row, and a logical table on a `row_filter` row. A row filter's role is not known: the source keeps it only inside the filter text. On a `query_privilege` row, `role` is the role's name in the repository, which can be an opaque text (the sample has one that is two 64-character hexadecimal strings with a space between them: one name, one role); `role_display_name` and `role_guid` are what the role's own object calls it (`BI Author Role`, `BIAuthor`; deliberate difference 27). `schema.csv` has the details.
- **How two database tables join** is in `physical_joins`. A foreign key row gives the table it is declared on (`table1_id`) with its own columns (`columns`), and the table it points at (`table2_id`) with the columns of the key it refers to (`referenced_columns`). Both lists hold Row identifiers, one per line; line *n* of one pairs with line *n* of the other. `table2_id` is empty only when the source names no key or the key was not found. A complex join gives its two tables and a condition. (The first attempt left a foreign key's far side empty; this is deliberate difference 26.)
- **What identifies a row of a table** is in three tables the first attempt did not have (deliberate differences 28 to 30). `physical_keys` has the keys declared on database tables; a foreign key points at one of them, and its `referenced_columns` equals that key's `columns`. The repository does not say which physical key is the primary key: a table with one key has one candidate, a table with none has nothing recorded (in `sampleapp`, 9 of 22 tables, among them every fact table behind Sample Sales). `logical_keys` has the keys of logical tables, with `is_primary` as the repository writes it. `logical_level_keys` has the keys of hierarchy levels; a level usually has one key that identifies a member (`is_primary`) and one that a user sees when drilling (`is_for_drill_down`), and the levels of a time dimension have a chronological key. In all three, `columns` holds Row identifiers one per line, in the repository's order.
- **The joins inside a table source** are in `lts_joins` (deliberate difference 34). A source built on more than one physical table (`lts_tables`) joins them, and `join_type` says how. An `Inner` join drops every row that has no partner in the other table and an outer join keeps them, so the join type decides what a measure counts although no formula mentions it. `LeftOuter` keeps every row of `table1_id`. The columns of the condition are on the `physical_joins` row that `physical_join_id` names. In `bi-analytics-2013`, 16 of the 36 join rows are outer joins.
- **Drill order** is `logical_levels.parent_level_ids` and `child_level_ids` (difference 35), not `position`, which is the order of the file.
- **A parent-child hierarchy** (`logical_dimensions.is_value_based` = `true`) is defined by a relationship table: the table source names it in `logical_table_sources.closure_table_id`, and that table's `physical_tables` row names the member, parent (ancestor), distance and leaf columns (difference 36).
- **`nullable`** is `true` or empty. The export writes the attribute only when it is true (635 columns in the two sample repositories; never `false`), so empty most likely means "not nullable in the repository"; the files do not say so, and the packets print `nullable not stated`.
- **A column's place and decimal digits** are `physical_columns.position` and `scale` (deliberate difference 31). Empty `scale`, `precision` or `nullable` means the repository says nothing; it does not mean 0 or false.
- A catalog Row identifier is `<estate>::cat::<path>`, the path being the folder and object names joined with `/`, decoded from the names on disk and in the letter case the disk has (usually lower case). A part of an object adds `#...` (`#page0`, `#item3`). Names contain dots, commas and spaces: never split the path.
- A catalog reference (`dashboard_contents.reference`) is kept as written. `resolution` says how it was matched to a path: `exact`, `case_normalised` (only after ignoring letter case; the usual outcome, because the export is lower case), `ambiguous` or `unresolved`. `target_id` can name something that has no table: a folder, or an object of a type that is not read.
- `dashboard_contents.position` on a page is not layout order: reports come first, then dashboard prompts, then scorecard views, then folder views. `dashboard_pages.position` is page order.
- A report's formula (`analysis_columns.formula`, `analysis_prompts.column_formula`) is the **whole text** of the source's expression element: its own text and the text of everything nested in it, in source order, with nothing trimmed. A filter text (`analysis_filters.expr_text`, `saved_filters.expr_text`) is the expression's **own text**: what is written directly in it, before, between and after its parts, without the text of nested expressions, because each of those is a row of its own. The catalog holds an expression once; there is no template form and no `..._refs` list for it.
- `analysis_columns.is_computation` says only that the source types the column as something other than a regular column. `false` does not mean the formula is a bare column reference: read the formula.
- A filter is a tree. `analysis_filters` has one row per expression, parent first, then its children in order; `parent_id` and `depth` rebuild the tree (`A and (B or C)` and `A and (B) and C` have the same rows in the same order and differ only there). The row of an and/or group or a comparison holds no condition itself (its `expr_text` is usually white space or empty); the columns, formulas and values are in the leaf rows below it. An operator `prompted` means the value comes from a dashboard prompt at run time and is not in the report.
- `analysis_filters.scope` decides what `owner_id` is: a report (`analysis`), a saved filter (`saved_filter`) or a dashboard page (`dashboard`: a dashboard prompt placed on the page, with a reference and no expression). A filter expression of type `sawx:savedFilter` names a saved filter by path; `resolution` and `target_id` say how it resolved, and the saved filter's own conditions are the `saved_filter` rows whose `owner_id` is that target.
- **From a report column to the database** is `report_lineage`. Read `resolution` first. `exact` and `case_normalised` mean the formula is nothing but `"Table"."Column"` and that presentation column exists in the estate's repository (matched by subject area, table and column name; `case_normalised` when only after ignoring letter case). `unresolved` means it is such a reference and nothing in the estate has those names. `not_a_column_reference` means the formula is empty or a calculation: the columns inside a calculation are **not** looked for, so read `reference_text`. `outside_semantic_model` means the formula starts with `XSA(`, an uploaded dataset that was never in the repository.
- `report_lineage.source_state` says whether the presentation column reaches a database column: `sourced` (repository lineage has a row for it), `computed_not_sourced` (found, and calculated in the business model, so there is no physical column: read `logical_columns.derived_expr_refs`), or empty (no presentation column was found).
- **`report_lineage` names one physical column where there may be several.** When `lineage` has more than one row for the presentation column (one for each table source that maps it), `report_lineage` uses the first of them in `lineage.csv` order: the row with the smallest `row_id`. This is a fixed rule, not the source the BI Server would choose for a query. For every path, read `lineage` where `presentation_column_id` is the same. (The first attempt took whichever row it had read last; this is deliberate difference 22.)
- **Linking has only ever succeeded on hand-made data.** The public sample repositories and the sample catalog come from different systems, so on them not one of 11,143 report columns links. The link is tested on two small estates written by hand so that the halves match (`reference/test-corpus/constructed-cases/` and `src/test/fixtures/linked-estate/`). It stays unproven on real data until an estate with both halves from one OBIEE system is available.
- `analysis_prompts` covers prompts inside reports only. Prompts saved as separate view objects, Visual Analyzer project formulas, scorecards, KPIs, actions and agents are not read; `unparsed.csv` counts them.
- Every table is written for every estate. An estate with no catalog has the catalog tables with a header and no rows; `manifest.csv` tells "no dashboards" from "no catalog".
- The column `rpd_id` holds the estate name. It keeps its name from the first Knowledge Base.
- The files are plain CSV: UTF-8, every field quoted, line feeds, rows sorted. Text is exactly as the source wrote it. **Do not open a table in a spreadsheet and save it:** a spreadsheet can alter values that start with `=`, `-`, `+` or `@`, or that look like numbers.
- The same input always gives the same bytes, apart from the time stamp in `manifest.csv`.

## Cut a Slice for a subject area

A skill never reads the whole Knowledge Base. `slice` cuts out what one subject area needs and joins it into small text files, one per piece of work.

```
java -jar build/obi2pbi.jar slice kb/sampleapp                                  # list the subject areas; writes nothing
java -jar build/obi2pbi.jar slice kb/sampleapp --subject-area "Sample Sales"    # writes slices/sampleapp/sample-sales/
java -jar build/obi2pbi.jar slice kb/sampleapp --all                            # one folder per subject area
```

| Argument | Meaning |
|---|---|
| `<kb-folder>` | A Knowledge Base written by `extract` |
| `--subject-area <name>` | The name exactly as listed (letter case matters), or the Row identifier. May be repeated: one folder each |
| `--all` | Every subject area |
| `--out <folder>` | The folder that gets one folder per subject area. Default `slices/<estate>` |
| `--timestamp <text>` | The time stamp for `manifest.csv`, for repeatable output |
| `--max-packet-lines <n>` | The most lines a packet may have. Default 120, at least 40. Kept in the Slice's `manifest.csv` (`max_packet_lines`) |

The folder name is the subject area's name in lower case with every run of other characters as one hyphen; the exact name is in the Slice's `manifest.csv` (`subject_area`). An unknown name is refused with the list of names (exit 2). Exit 3 means the Slice was written and its `errors.csv` has rows. The folder is rewritten on each run; a folder that is not empty and has no `manifest.csv` is refused.

**What a Slice folder holds**

| File | Holds |
|---|---|
| The Knowledge Base's tables | The same file names and columns, with only the rows the subject area needs. Rows are exact copies, in the same order. `coverage`, `coverage_summary` and `catalog_versions` are not carried: they count the whole estate |
| `errors.csv`, `unparsed.csv` | Only entries about the Slice: an error whose `source_id` is a row or catalog object of the Slice; an `unparsed` row for a kind of object the Slice holds. What was left out is counted in the manifest (`errors_not_copied`, `unparsed_not_copied`) |
| `schema_tables.csv`, `schema.csv` | The description of exactly the tables in the folder |
| `manifest.csv` | The subject area, the Knowledge Base it was cut from, the packet size limit, what was left out (by kind, and in `unparsed_not_copied_by_type` by kind and object type), and the table list. The only file with a time stamp |
| `units.csv` | The work units in the order a document should be written: `unit_id`, `position`, `kind`, `title`, `part`, `parts`, `packet_file`, `line_count`, `required_rows` |
| `packets/<unit_id>.md` | One packet per unit: everything about the unit already joined, at most `--max-packet-lines` lines (120 unless told otherwise) |
| `required_rows.csv` | The rows a document must cover (`row_id`, `table`, `unit_id`). `check-doc` reads it |

**What the walk brings in.** The subject area's presentation tables, columns and hierarchies; the logical columns behind them, the ones their formulas name, and every other column of the same logical tables, shown in this subject area or not (with their tables, sources, mappings and aggregation rules; a column with no presentation column here is marked "NOT SHOWN IN THIS SUBJECT AREA" in its packet); the dimensions and levels; lineage, the physical tables behind them and the table an alias stands for, each **whole**: every column of the table, whether or not a logical column maps it, every key, and the table's definition (the SQL of a table that is a query); the tables each source is built on and the joins it makes between them (a table a source joins comes whole even when nothing is mapped from it); the relationship table of a parent-child source; the keys of the logical tables and of the levels; physical joins with both ends in the Slice (a foreign key says what it points at, so one that points at a table outside the Slice stays out) with the columns at both ends, and business-model joins with either end; variables that an expression, display-name formula, filter or SQL text names inside `VALUEOF(...)` (exactly; when no variable has exactly that name, one whose name differs only in letter case) or refers to, and their init blocks; security rows about any row of the Slice, with the roles of their packages; and, when the estate has a catalog, the reports whose criteria name the subject area (`analysis_criteria.subject_area`, quotes taken off, matched exactly and then ignoring letter case) with their columns, filters, prompts and views, the dashboard pages that place them, the dashboards, the dashboard prompts on those pages, and the saved filters they use. Every reference inside a Slice resolves inside it; a row that is there only to be pointed at (the table at the far end of a join) is counted in the manifest as `boundary_rows`. Things that belong to no subject area (object permissions with no recorded target, role privileges, init blocks and variables the Slice does not use) are in no Slice; the manifest counts them (`not_in_slice:…`) and the overview packet lists the counts and where to find them in the full Knowledge Base.

**Units and packets.** The units are: `overview`; one `logical_table` unit per logical table (tables with measures first), holding its keys (or the statement that the repository records none), its sources, each source's level of detail (or "not recorded"), the physical tables it is built on (by name, in the source's order, with a pointer to the unit that states each), the joins it makes between them with join type and cardinality exactly as written and the columns of the condition, its parent-child relationship table when it names one, and every column with its presentation name, formula, aggregation rules, mappings (each with the type, precision, scale and nullable of the database column it reads) and lineage; one `physical_table` unit per physical table of the Slice (`dbtable-<nn>-<name>`), for the person who builds the new tables: the table's name and container, whether it is an alias and of what, what it is in the database (for a query, its SQL exactly), its keys, pointers to the foreign keys from and to it, the table sources that read it, and every column in repository order with type, precision, scale, nullable and external name, marked `MAPPED` (by which logical columns) or `NOT MAPPED`; `joins` (each foreign key in full: from table and columns to table and columns, pair by pair); `hierarchies` (each level with the level directly above and below it and every key it has with the key's flags; for a parent-child dimension, the relationship table and its member, parent, distance and leaf columns); `variables` (every name used inside `VALUEOF(...)`, the rows that use it, and for each either its `variables` row or the statement "used by name, not defined in the repository"); `security` (each role on a line of its own, with its display name, its name in the repository and its guid, or "not recorded in the repository"); one `reports` unit per dashboard, then one per catalog folder for reports no dashboard places; `saved_filters` when there are any; `gaps` (errors and unread content, with totals per kind and the object types that have no reader, by name).

**Packet size.** No packet is longer than `--max-packet-lines` lines (default 120), so that a model with a short context can take one whole. A unit that is longer is split into parts `-p1`, `-p2` …, each a unit of its own with its own row in `units.csv`, its own packet and its own section in a document. A logical table is cut first between its two halves, what the table offers (the table, its columns with their presentation names, formulas and aggregation rules) and where its data comes from (one table source after the other, each with the physical tables it reads, what it maps each column from, and the lineage through it); then between items. A cut never falls inside an item (a column, a mapping with its lineage line, a join, a report) as long as an item boundary is in reach; only a single value longer than a packet (a long SQL text) is cut, between fence lines, with a note on both sides. Every part repeats the whole header, says "part 2 of 3" in its title and in a header line, and a part that continues something starts with a line saying what it continues.

**What a document must cover.** `required_rows.csv` lists: presentation tables and columns; logical tables, their keys, their measures, their table sources and each join inside a source; physical tables with each definition, each key and each column; joins; dimensions and hierarchies; variables; security rows; report columns that are calculations. A physical table and its columns are required rows of the table's own `physical_table` unit and are cited there only: the other packets name them and point at them with `->`. Where the repository records nothing (no key, no level of detail, no scale, nullable absent) the packet says "not recorded" in words, so that a document does not pass silence off as a fact.

A packet starts with its unit id, kind, subject area, the tables its facts came from, and how many rows it carries that a document must cover. Every fact is followed by the citation of the row it came from; formulas are printed exactly as extracted, a multi-line one between fence lines. The one line that states a row a document must cover ends with `(REQUIRED)`, so a skill needs no list of which kinds of line matter.

```
- Logical column "Revenue": MEASURE; writeable true; mapped in 1 of 1 table sources. [kb:sampleapp::bmm::SAMPLESALES.Fact Target.Revenue] (REQUIRED)
    - Shown to report authors as "Revenue" in presentation table "Fact Target"; name set by hand: false. [kb:sampleapp::pres::Sample Sales.Fact Target.Revenue] (REQUIRED)
    - Aggregation rule (default): ` SUM("SAMPLESALES"."Fact Target"."Revenue")` [kb:sampleapp::bmm::SAMPLESALES.Fact Target.Revenue#aggr0]
```

The same Knowledge Base always gives the same Slice, byte for byte, apart from `manifest.csv`. Packets are built from the Slice's own tables only.

## Cut a Slice for a tab

```
java -jar build/obi2pbi.jar slice kb/sampleapp --tab work/test-tab
```

Cuts `work/<tab>/slice/` from the columns the Operator confirmed in `work/<tab>/decisions.csv` (with `fsd_items.csv` and, when there, `matches.csv`), for the to-be skill. Only `confirmed` decisions count; an empty `row_id` means the item's one proposed column. A confirmed column may be a presentation, logical or report column. The walk is the subject-area walk started from those columns, so the Slice is a subset of the subject areas' Slices; it has the same files, units and packets, plus a table `tab_fields` and a unit `tab-fields` listing every FSD item with its confirmed columns, decision notes, FSD text and anchor (a required row per item; items not confirmed are listed and contribute nothing). `docs/skill-conventions.md`, section 10.10, lists the units and required rows.

```
tab:          test-tab (work/test-tab)
decisions:    27 rows: 26 confirmed, 1 rejected
items:        21 that need data: 18 confirmed; not confirmed, contributing nothing: 1 rejected, 2 unmatched, 0 undecided
columns:      11 confirmed columns start the walk
subject area: Sample Sales; as-is document as-is/sampleapp/sample-sales.md exists (Status: reviewed ...)
units:        30 packets, the largest 120 lines (table-01-fact-revenue-p1; limit 120)
required:     256 rows a document must cover
```

`--out`, `--timestamp` and `--max-packet-lines` work as for a subject area. Exit 2, nothing written: no `decisions.csv`, or nothing confirmed. Exit 3, nothing written: a confirmed identifier that is not in the Knowledge Base (named), or a decision that names no column. `decisions.csv` is never changed.

**Gaps packet.** In every Slice, a row of `unparsed` that is only an internal identifier (`@mdsid`, a reference's `@refId`) or the internal `@name` of an element whose expression is read (`Expr`, `WhereClause`, ...) is listed apart, one line per attribute, as "identifier of each not read; its content is read", so that it is not taken for missed content. Which rows these are is decided by the repository adapter (`in/mds/IdentifierAttributes.java`) and recorded in the Slice's manifest (`unparsed_identifiers_only`).

## Join a document from its parts

A skill writes a document as small files, one per section, in `<document without .md>.parts/`. It never reads or rewrites the whole document; the jar builds it.

```
java -jar build/obi2pbi.jar assemble-doc as-is/sampleapp/sample-sales.md
```

It joins every `.md` file of `as-is/sampleapp/sample-sales.parts/` in file-name order and writes the document: line feeds, UTF-8, empty lines at the start and end of a part dropped, one empty line between two parts, nothing for an empty part. The same parts always give the same bytes. It prints each part with the lines it became, the number of parts and the number of lines.

Before it overwrites a document, it reads the one that is there. If a question in it has text after `Answer:` that `sample-sales.answers.md` does not hold, **nothing is written**: the questions are printed with the answers as typed, and the exit code is 3. Record the answers in the answers file and run it again. Exit 0: written; 2: no parts folder, no part, or wrong usage.

## Check a skill-written document

```
java -jar build/obi2pbi.jar check-doc as-is/sampleapp/sample-sales.md --slice slices/sampleapp/sample-sales
```

It reads the document, the Slice, `sample-sales.answers.md` and `sample-sales.md.review.csv` when they are beside the document, and writes `sample-sales.md.check.csv`: one row per finding, only a header when the document passes. It never changes the document. It prints the status, rows required and covered, citations checked, how many quoted texts were compared with the packets, open and answered question counts, the findings and the warnings, and the findings counted per unit. Each finding names the unit to correct (`unit_id`): for a row that is not covered, the unit whose packet states it; for a finding at a line, the part of the document the line is in, when the document is exactly its parts joined. Exit 0: no finding (warnings do not count); 3: findings; 2: the document or the Slice is missing.

**Each part against its packet.** When the document is its parts joined, every part that is a unit of the Slice (its file is `<nnn>-<unit_id>.md`) is compared with the packets, and what is found is printed unit by unit:

| Finding | Raised when | Fails the check |
|---|---|---|
| `unknown_citation` | A `[kb:...]` marker names a row that is not in the Slice, or one that no packet states | yes |
| `citation_from_other_unit` | A marker in a unit's part is stated in another unit's packet and not in its own. A warning: a section may speak about another unit's row | no |
| `quote_not_in_packet` | A text between backticks, or between fence lines, does not occur character for character in the unit's packet or in any other packet of the Slice | yes |

A quote is what stands between two runs of backticks of the same length on one line, or the lines between two fence lines (three or more backticks or tildes). White space at the two ends of a quote is ignored. So backticks are only for text copied from a packet: a formula, a name, a value. `docs/skill-conventions.md`, section 9, has the exact rules. Parts that are not units (`000-summary.md`, the question sections, `992-not-described.md`) are not compared for quotes; a part named after no unit is listed in a note.

A required row is covered when the document cites it as `[kb:<row identifier>]` or lists it under `## Not described` with a reason. A citation of a row that is not in the Slice or that no packet states, a quoted text that is in no packet, an `[answer:Q<n>]` with no such answer, a question without its `Answer:` line, a wrong status line, and a document marked reviewed that has findings or no review log are all findings. `--required <csv>` replaces the Slice's list of required rows with another (columns `row_id`, `table`).

The exact text rules a skill must follow are in `docs/skill-conventions.md`.

## From an FSD to checked matches

One folder per FSD, `work/<tab>/`. The Operator puts the FSD there as `fsd.docx`; the trace skill proposes a column for each item; `check-matches` checks the proposal; the Operator records the verdicts. The exact columns and values of every file are in `docs/skill-conventions.md`, section 10.

### Turn the FSD into text

```
java -jar build/obi2pbi.jar fsd-text work/sample-sales-monthly/fsd.docx
```

It reads the `.docx` with the JDK's zip and XML support and writes, beside it:

| File | Holds |
|---|---|
| `fsd.md` | The FSD's headings, paragraphs, lists and tables in document order, text as written, every paragraph anchored `[p12]` and every table cell `[t3.r2.c1]` (table, row, grid column). The same file always gives the same bytes |
| `fsd.manifest.csv` | The source file's name, size and SHA-256, so a later step can tell that the FSD changed, and the counts |
| `fsd.sections/` | Only when `fsd.md` has more than 300 lines: `index.md` and the same text in sections of at most 152 lines, cut at headings, for a skill with a short context |

```
# [p1] Functional Specification — Sales Performance Dashboard, tab "Monthly Overview"

## [p7] 3. Filters (slicers at the top of the page)

| [t1.r1.c1] # | [t1.r1.c2] Filter | [t1.r1.c3] Description | [t1.r1.c4] Default |
|---|---|---|---|
| [t1.r2.c1] F1 | [t1.r2.c2] Year | [t1.r2.c3] Calendar year of the order | [t1.r2.c4] Current year |
```

Headings are the paragraphs styled "heading 1" to "heading 6" (or "Title"); a list keeps its bullets or numbers; a merged cell's other positions are cells of their own, `(merged with t1.r2.c1)`, so rows stay aligned; an empty cell keeps its anchor. Not read: images, comments, deleted text of tracked changes, headers, footers. An old `.doc` is refused with a request to save it as `.docx`; a file that is not a zip, or a zip with no `word/document.xml`, is refused by name (exit 2). `--out <folder>` writes elsewhere. A `fsd.sections` folder with no `index.md` is not this command's and is refused.

`work/sample-sales-monthly/fsd.docx` is a test FSD built from `fsd-source.md` by `src/test/java/obi2pbi/FsdDocxGenerator.java` (JDK only: headings as Word heading styles, the tables as Word tables, the list as a Word list): `java -cp build/classes/main:build/classes/test obi2pbi.FsdDocxGenerator work/sample-sales-monthly/fsd-source.md work/sample-sales-monthly/fsd.docx`.

### Look a business term up

```
java -jar build/obi2pbi.jar find-columns kb/sampleapp "net revenue" --subject-area "Sample Sales"
java -jar build/obi2pbi.jar find-columns kb/sampleapp --formula-contains "VALUEOF(NQ_SESSION"
```

A short ranked list of presentation columns, by plain text matching (no AI), for the trace skill, which never opens a Knowledge Base file. It compares the term's words with each presentation column's name, its logical column's name, the table names and the logical column's formulas, ignoring letter case, punctuation and plural endings, and taking a short word as an abbreviation of a longer one (`Discnt` for discount, `Rep` for representative), which counts for less. Columns whose whole name is the term come first, then those with all its words, then those with some; inside a group a word in the column's own name counts more than one in a table's name or a formula. For each candidate it prints the Row identifier as a document cites it, the subject area, presentation table and column, the logical column, whether it is a measure and its default aggregation, the formula of a derived column, the database columns behind it through lineage, and the report columns that use it (when the estate has a catalog).

| Argument | Meaning |
|---|---|
| `<kb-folder>` | A Knowledge Base written by `extract` |
| `"<term>"` | The words to look for, in quotes when several |
| `--subject-area <name>` | Only this subject area (its exact name or Row identifier) |
| `--formula-contains <text>` | Only columns one of whose formulas contains the text (letter case and runs of spaces ignored); the term may then be left out. Each candidate then shows which formula matched and its text (around the match, when it is long) |
| `--limit <n>` | At most n candidates (default 10); the count of all is printed |

It writes nothing. Exit 0, also when nothing matches (it says so); 2 for wrong usage.

### Name the page the tab replaces

```
java -jar build/obi2pbi.jar find-pages kb/sampleapp-v607 "overview" --limit 3
```

Plain text search of dashboards, dashboard pages and reports by name and catalog path (a page's dashboard name counts too). For a page: the Row identifier to put in `fsd_items.csv` (`existing_id` of the `replaces` row), its dashboard, its position, and the reports placed on it with their subject areas; for a dashboard, its pages; for a report, its subject areas and the pages that place it. A Knowledge Base built without a catalog has no page to name, and it says so. Writes nothing; exit 0, or 2 for wrong usage.

### Check the matches

```
java -jar build/obi2pbi.jar check-matches work/sample-sales-monthly --kb kb/sampleapp
```

It reads `fsd.md`, `fsd_items.csv` and `matches.csv` (written by the trace skill), `decisions.csv` when the Operator has written it, and the Knowledge Base, and writes `check.csv`: one row per finding (`finding`, `severity`, `file`, `row`, `item_id`, `row_id`, `detail`), only a header when nothing is wrong. It never changes the other files and does not judge whether a match means the right thing.

| Finding | Severity | Raised when |
|---|---|---|
| `unknown_row` | failure | A column (or a replaced page) is not in the Knowledge Base |
| `no_physical_source` | failure | The column's lineage reaches no database table |
| `item_without_match` | failure | An item of `fsd_items.csv` has no row in `matches.csv` |
| `unknown_anchor` | failure | An anchor is not in `fsd.md` |
| `unknown_item` | failure | `matches.csv` or `decisions.csv` names an item that is not in `fsd_items.csv` |
| `bad_value` | failure | A value the contract does not allow: confidence outside 0-100 or its band, an unknown status, `proposed` with no column, a decision other than `confirmed` / `rejected` |
| `derived_column` | warning | The column is computed by a formula and has no database column of its own |
| `differs_from_existing_report` | warning | The FSD names the page the tab replaces, and the column is not among that page's report columns; the detail names the page's report columns that share a word with the item |

Exit 0: no failure (warnings do not count); 3: failures; 2: a missing input or wrong usage. When the Knowledge Base has no catalog, or the FSD names no page it replaces, the comparison with the existing report is skipped and the run says so.

### Record the decisions

The Operator writes `work/<tab>/decisions.csv` by hand; no program writes it. `slice` and the later skills use the confirmed rows only.

| Column | Meaning |
|---|---|
| `item_id` | The item, as in `fsd_items.csv` |
| `decision` | `confirmed` or `rejected` |
| `row_id` | The column the verdict is about. Empty: the item's one proposed column. With `confirmed`, a column that was not proposed is a correction: the column to use instead |
| `note` | The reason; may be empty |

```
"item_id","decision","row_id","note"
"K1","confirmed","","matches the Overview page"
"K3","confirmed","sampleapp::pres::Sample Sales.Fact Target.Revenue","the target, not the actual"
"F4","rejected","","no region in Sample Sales; ask the FSD author"
```

Copy a `row_id` from `matches.csv` or from `find-columns` (without the square brackets and the `kb:` before it), and run `check-matches` again afterwards: it checks the decisions too.

## Compare with the reference Knowledge Base

`reference/knowledge-base/` holds the Python extractor's output for the sample estates `sampleapp` and `bi-analytics-2013` (repositories) and `sampleapp-v607` (catalog). To check the jar against it:

```
java -jar build/obi2pbi.jar extract estates/sampleapp
./build.sh compare sampleapp                               # every table in kb/sampleapp
./build.sh compare sampleapp physical_tables physical_columns
./build.sh compare sampleapp unparsed --where object_type=PhysicalTable
```

For each table it reports rows missing, extra rows, and differing cells. It ignores the reference's `_guarded` columns and skips `manifest`, `schema` and `schema_tables` (`compare-all`, below, compares the manifest too). Options: `--kb <folder>`, `--ref <folder>`, `--source <estate folder>` (default `estates/<estate>`), `--where <column>=<v1>,<v2>` (keep these rows), `--where-not <column>=<value>` (leave these rows out; one value per option, repeatable), `--show <n>`, `--show-explained <n>`.

Differences that are intended are listed, with numbers, in `docs/port-guide/00-read-me-first.md`. Any other difference is a bug. The comparison knows twelve of the intended ones and checks each cell or row; it never passes a difference because of the column it is in:

| Difference | Recognised when |
|---|---|
| 6, reference lists one per line | In a `..._refs` column, the reference's space-joined list and the generated lines are the same identifiers in the same order |
| 5, references resolved whatever the reading order | The reference's list is the generated one with entries missing, and the reference's `errors.csv` reports that many unresolved references for that object. Also the `errors` rows themselves, when the generated Knowledge Base resolves the reference |
| 7, foreign-key columns as Row identifiers | Each source id in the reference's `physical_joins.columns` cell is looked up in the estate's own files (read by the comparison, not by the jar); the identifier built from the names found there must be the generated entry in the same place, and a row of the generated `physical_columns` |
| 19, a credential value withheld under an innocent name | The generated `variables` row is redacted, the reference row is not, and the value the reference wrote is, in the estate's files, the value of a variable whose name suggests a credential. Also the `unparsed` row that records it |

| 8, formula and filter text stored whole | The estate's own file is read again with the JDK's DOM parser and the element the row stands for is found. The reference value must be that element's text before its first nested element; the generated value must be its whole text content for a formula, and for a filter row its text content with nested expressions left out. Also an `unparsed` count of unread text that drops by exactly the number of nested texts counted in the files |
| 9, every expression of a saved filter written | An extra `analysis_filters` row of scope `saved_filter` that is, column for column, what the saved filter's file holds at that position, where the saved filter has exactly as many rows as it has expressions. Also the error row of such a row whose reference did not resolve, the `coverage.skipped` count that follows from those error rows, and the `unparsed` rows for content of saved filters that is now read, when the counts in the files agree |
| 10, parent and depth of each filter row | The two columns `analysis_filters` adds, when the enclosing expression and the depth worked out again from the files are what **every** row says. One wrong value and the columns are reported as unexplained |
| 11, a dashboard prompt reference resolved once | An `errors` row about a global filter view that the reference has exactly twice as often as the generated table, where the generated `dashboard_contents` has one unresolved placement for each generated error of that page. Also the `coverage.skipped` count of that element, when it is exactly halved and the error rows account for it |
| 21, a file name that cannot be decoded is reported | An extra `unparsed` row of kind `undecoded_name`, when the file is in the estate's catalog and the JDK's own decoder also finds its name malformed |
| 26, a foreign key says what it points at | `physical_joins.table2_id`: the reference cell is empty on a foreign key row and the generated value is the table that, in the estate's own files, holds the key the foreign key's link names. The added column `referenced_columns` is checked on every row against the same files: that key's columns, in its own order. Also the `unparsed` counts for the link, the keys and their column lists, when they fall by exactly what the files hold |
| 28, 29, 30, the three key tables | A table the reference does not have is reported as **new table (difference N)**, and only when every row of it is what the estate's own files hold: the comparison reads the files itself (`KeySource`), builds each key's Row identifier from the names there, and requires the same rows, cell for cell, no more and no fewer. Also the `coverage` rows `PhysicalKey` and `LogicalKey`, when they count exactly the keys in the files, and the `unparsed` counts for keys, their names, flags and columns, when the generated count is exactly what the files leave outside what the jar now reads |
| 31, a column's scale and place; 32, a dimension's time flag | The added columns `physical_columns.scale` and `position`, and `logical_dimensions.is_time`, when every value is what the estate's own files say. Also the `unparsed` rows for the two attributes, when they are gone |
| 27, a role's display name and guid | The two columns `security_rls` adds, when on every query privilege row they are what the file of the role says that the row's privilege package points at, and empty on every other row. Also the `unparsed` counts of those two attributes, when they fall by exactly the number of roles so pointed at |
| 22, which lineage row a report column is linked through | In `report_lineage.logical_column_id` and `physical_column_id`: both sides name the same presentation column, the generated value is that of the `lineage` row with the smallest Row identifier, and the reference value is that of another `lineage` row of the same presentation column |

Difference 8 is also recognised in `report_lineage.reference_text`, which is a copy of the report column's formula: the cell must equal `analysis_columns.formula` on each side, and that difference must itself pass the check for 8.

Differences 7, 8, 9, 10, 19, 21, 26 and 27 need the estate folder, so without it they are listed as unexplained. These are counted per difference number and shown apart, for example `explained cells 9 (difference 5: 2, difference 6: 7)`. Everything else is listed as before. The exit code is 0 when no difference is left unexplained, and the last line says whether anything differed at all.

On the two sample repositories the comparison of every repository table, `errors`, `unparsed` and `coverage` leaves nothing unexplained.

On the sample catalog (`sampleapp-v607`, 5,371 files, about three seconds, in a 96 MB heap) the comparison of every table, unfiltered, leaves nothing unexplained:

```
java -jar build/obi2pbi.jar extract estates/sampleapp-v607
./build.sh compare sampleapp-v607
```

`analyses` (1,607 rows), `analysis_criteria` (1,789), `analysis_views` (10,879), `analysis_prompts` (4), `dashboards`, `dashboard_pages`, `dashboard_contents`, `catalog_versions` and `coverage_summary` are equal to the reference. `analysis_columns` (11,143) differs in 15 formulas (difference 8), and `report_lineage` (11,143) in the same 15, which it copies. `analysis_filters` has 440 texts that gain the white space after a nested expression (8), 1,360 more rows for saved filters (9) and the two added columns (10). `saved_filters` (96) differs in 3 texts, the same way (8). `errors`, `coverage` and `unparsed` differ only as differences 8, 9, 11 and 21 make them.

The sample catalog is a checkout of a public git repository, `github.com/gittestbyat/obiee_sample_git` (`docs/formats/web-catalog-findings.md`, section 2, gives the commit). Link the checkout itself to `estates/sampleapp-v607/catalog`; its `.git` folder is left out by the reader and its `.gitignore` is counted as one of the 5,371 files.

### The whole comparison, and its report

```
./build.sh compare-all          # Linux, macOS
build.cmd compare-all           # Windows
```

This is the evidence that the port matches the first attempt. It needs the three sample estates under `estates/` (above) and takes about fifteen seconds. It:

1. builds the jar;
2. runs `extract` on `estates/sampleapp`, `estates/bi-analytics-2013` and `estates/sampleapp-v607`, writing `kb/<estate>`;
3. compares all 39 reference tables for each estate, row by row and cell by cell, and `manifest.csv` key by key;
4. builds two small estates with both halves under `build/compare-all/` (the first attempt's hand-made pair and `src/test/fixtures/linked-estate`), runs the jar and, if Python 3 is installed, the first attempt's Python program on each, and compares the two outputs. This is the only place where a report column links to a repository;
5. writes `documents/implementation-artifacts/kb-comparison-report.md` and prints the totals.

```
report:   documents/implementation-artifacts/kb-comparison-report.md
estates:  3 compared
tables:   117 table comparisons
rows:     60,138 reference rows compared
unexplained differences: 0
```

The exit code is 0 only when all three estates were compared and nothing is unexplained. The report is written for a reader who has not seen the code: what was compared and how, a line for every table of every estate, each deliberate difference that occurred with its counts and three real examples, anything unexplained, and what the comparison does not prove. It never shows the value of a variable. A sample estate whose folder is missing is reported as not confirmed. `--no-extract` compares what is already in `kb/` and `build/compare-all/`; `--out <file>` writes the report elsewhere.

## Where things are

| Path | Holds |
|---|---|
| `src/main/java/obi2pbi/cli` | `Main` and one class per command |
| `src/main/java/obi2pbi/core` | The model, identifiers, `ReportLineage` (the link from report columns to the repository), `KbSchema`, the one place every table and column is declared, `Slice` (the walk), `Packets` (units and packet text), `Citation` and `DocCheck`, and `SliceSchema` for the Slice's own tables; for an FSD, the document model (`FsdDocument`, with the anchors), `ColumnSearch` (behind `find-columns`), `MatchSchema` (the columns of `fsd_items`, `matches`, `decisions` and `check`), `MatchCheck`, `PageSearch` (behind `find-pages`) and `TabDecisions` (the starting columns of a tab's Slice) |
| `src/main/java/obi2pbi/in/xml` | The recording XML layer and the audit that finds unread content |
| `src/main/java/obi2pbi/in/mds` | The repository reader |
| `src/main/java/obi2pbi/in/catalog` | The Web Catalog reader: discovery, name decoding, the path index and resolver, and one reader per object type |
| `src/main/java/obi2pbi/in/kb` | Reading a CSV table back, and a whole Knowledge Base or Slice folder through its manifest |
| `src/main/java/obi2pbi/in/docx` | The FSD reader: a `.docx` opened as a zip, its document, styles and numbering read into the document model |
| `src/main/java/obi2pbi/out` | The CSV writer, the Knowledge Base writer, the Slice writer, the packet writer and `FsdMarkdownWriter` (`fsd.md` and its sections) |
| `skills/` | The migration agent (`obiee-migration-agent`) and its skills (`obiee-as-is`, `obiee-review`): plain markdown, copied to `.agents/skills/` (GitHub Copilot, started from `.github/agents/obiee-migration-agent.agent.md`) and `.claude/skills/` (Claude) by `./build.sh skills` |
| `docs/skill-conventions.md` | The text rules for skill-written documents that `check-doc` relies on, and (section 10) the contract of the FSD, items, matches, decisions and check files |
| `src/test/java/obi2pbi` | The test runner, the tests, `KbCompare`, and `KbCompareReport`, which writes the comparison report |
| `src/test/fixtures` | Small hand-made input: a repository (`testrpd`), an estate with both halves (`linked-estate`), an FSD as the XML parts of a `.docx` with the `fsd.md` it must give (`fsd`), and a tab's match files with one fault of each kind (`matches`) |
| `work/<tab>/` | One folder per FSD; `work/sample-sales-monthly/` holds the test FSD (`fsd-source.md`, `fsd.docx`, `fsd.md`) |
| `documents/implementation-artifacts/kb-comparison-report.md` | The last full comparison with the reference Knowledge Base |
| `docs/port-guide/` | What the Python program does, and the list of deliberate differences |
