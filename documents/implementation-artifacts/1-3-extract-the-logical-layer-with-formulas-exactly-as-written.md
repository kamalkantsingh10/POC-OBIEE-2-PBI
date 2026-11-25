---
title: 'Extract the logical layer with formulas exactly as written'
type: 'feature'
created: '2026-11-23'
status: 'done'
route: ''
review_loop_iteration: 0
context:
  - '{project-root}/docs/port-guide/00-read-me-first.md'
  - '{project-root}/docs/port-guide/01-repository-reader.md'
  - '{project-root}/documents/planning-artifacts/architecture/architecture-POC-OBIEE-2-PBI-2026-11-23/ARCHITECTURE-SPINE.md'
---

# Story 1.3: Extract the logical layer with formulas exactly as written

<frozen-after-approval reason="human-owned intent — do not modify unless human renegotiates">

## Intent

**Problem:** The business model is where each measure is defined. Without it the Knowledge Base cannot say what a number computes.

**Approach:** Port the logical-layer readers: logical tables and columns, logical table sources and their column maps, and aggregation rules, with every expression kept in all the forms the source holds.

## Boundaries & Constraints

**Always:**

- Port the Python reference as it is. Change behaviour only where `docs/port-guide/00-read-me-first.md` lists a deliberate difference; a new difference is added to that list in this story.
- Java 11 language level, `java.base` and `java.xml` only (AD-15). No build tool, no test library.
- All XML is read through the recording layer, so unread content is found by observation (AD-3).
- All CSV goes through the one CSV writer (AD-8). Text is written exactly as the source holds it (AD-5).
- An expression is stored in three columns: template text, rendered text, and reference list (guide 01 §4). No trimming.
- Reference lists hold one reference per line; an unresolved reference leaves an empty line (difference 6).
- All objects are indexed before references are resolved, so a reference to something read later resolves (difference 5).

**Never:**

- Parsing or rewriting formula text.
- Choosing one form of an expression and dropping the others.
- Presentation layer and lineage (stories 1.4, 1.5).

## I/O & Edge-Case Matrix

| Scenario | Input / State | Expected Output / Behavior | Error Handling |
|----------|--------------|---------------------------|----------------|
| Happy path | Logical tables, columns and sources | `logical_tables.csv`, `logical_columns.csv`, `logical_table_sources.csv`, `lts_column_map.csv`, `aggregation_rules.csv` with `bmm` identifiers | N/A |
| Leading spaces | Formula `' SUM("%1")'` | Stored with the leading space | N/A |
| Formula starting with `-` or `=` | Such a formula | Stored unchanged | N/A |
| Default and override rules | A measure with a default rule and a dimension-specific one | Two `aggregation_rules.csv` rows, default flagged | N/A |
| Content filter | A source with a where clause | `where_text` exactly as written, plus rendered text and references | N/A |
| Several sources | A logical column mapped by 1 of 2 sources | `source_count` 2, `mapped_source_count` 1 | N/A |
| Time-series function | A formula using `AGO`, `TODATE` or `PERIODROLLING` | `time_series` set as guide 01 §6.2 describes; the level reference resolves | N/A |
| Unresolved reference | A reference to a missing object | Empty line in the reference list; `errors.csv` row `unresolved_reference` | Exit 3 |

</frozen-after-approval>

## Code Map

- `docs/port-guide/01-repository-reader.md` -- §2.3 LogicalTable, §2.4 LogicalTableSource, §3.4 business model tables, §4 expressions, §6.1 aggregation rules, §6.2 time-series flag, §6.3 source priority / fragment / where / group-by, §7 processing order (and §7.3 for difference 5)
- `src/test/fixtures/GROUND-TRUTH.md` -- expected logical rows for `testrpd`
- `reference/test-corpus/constructed-cases/GROUND-TRUTH.md` -- expected rows for the detail and aggregate sources
- `reference/knowledge-base/logical_*.csv, lts_column_map.csv, aggregation_rules.csv` -- expected output on the large samples

## Tasks & Acceptance

**Execution:**

- [x] `src/main/java/obi2pbi/core/` -- logical table, column, source, column map, aggregation rule; the three-form expression value
- [x] `src/main/java/obi2pbi/in/mds/` -- LogicalTable and LogicalTableSource readers; two-sweep index then resolve
- [x] `src/main/java/obi2pbi/out/KnowledgeBaseWriter.java` -- write the five tables
- [x] `src/test/java/obi2pbi/` -- tests for the matrix rows and both ground-truth files

**Acceptance Criteria:**

- Given `testrpd` and the constructed cases, when `extract` runs, then the logical rows equal those in the two `GROUND-TRUTH.md` files.
- Given the sample repositories are fetched, when `KbCompare` runs on the five tables, then every difference is explained by differences 5 and 6.
- Given the reference's four reading-order errors on the samples, when `extract` runs, then they no longer appear in `errors.csv`.

## Implementation Notes

Built together with stories 1.4 and 1.5, in one pass.

**Built.** `extract` now writes `logical_tables.csv`, `logical_columns.csv`, `aggregation_rules.csv`, `logical_table_sources.csv` and `lts_column_map.csv`. Every expression is held in three columns (template, rendered text, reference list), exactly as written. Reference lists hold one identifier per line, with an empty line for a reference that did not resolve (difference 6). Every object is named before any reference is resolved (difference 5).

**Files.**

| File | What |
|---|---|
| `core/LogicalTable`, `LogicalColumn`, `AggregationRule`, `LogicalTableSource`, `ColumnMapping` | The model |
| `core/TimeSeries` | The `AGO` / `TODATE` / `PERIODROLLING` text search (guide 01 §6.2) |
| `core/Lineage.countMappedSources` | `mapped_source_count`, worked out after all sources are read |
| `core/Estate` | The new lists, also in `rowLists()` (uniqueness check) and `merge` |
| `core/KbSchema` | The five tables, with a shared helper for the three expression columns |
| `in/mds/Expressions` | Reads an expression-shaped element; shared by the readers |
| `in/mds/LogicalTableReader`, `LogicalTableSourceReader` | The two readers, registered in `RepositoryReader.READERS` |
| `in/mds/NamesAhead`, `ReferenceIndex.bindWithoutReading` | See decision 1 |
| `test/LogicalLayerTest` | 5 tests: both ground-truth files, the matrix rows, error cases |
| `test/KbCompare`, `KbCompareTest` | Explained differences (4 new tests) |

`out/KnowledgeBaseWriter.java` did not need a change: it writes whatever `KbSchema.TABLES` lists.

**Decisions.**

1. **Dimensions, levels and variables are named now, though their readers are story 1.6.** The matrix asks that the level reference of a time-series formula resolves, and the acceptance criteria ask that all four reading-order errors go; three of the four point at a level or a variable. `NamesAhead` binds the identifiers (`<estate>::bmm::<model>.<dimension>`, `...<dimension>.<level>`, `<estate>::var::<name>`, the same as the Python builds) and does nothing else: no row, no count, no error, no recorded read. Those types are still `unmodelled_type` in `unparsed.csv` and `skipped` in `coverage.csv`, exactly as before; a test checks it. Story 1.6 must move the naming into its readers' `bind` and delete the class.
2. **A reference to any other object without an identifier** (init block, group, join, key) stays unresolved with an error row, as in the Python.
3. **`logical_column_id` / `physical_column_id` of a mapping are the first reference that resolved**, as in the Python (guide 01 G4), not "the first reference". See open point 1.
4. **`time_series` and `aliases` keep the Python's separators.** They are not reference lists.
5. **Error reasons quote a name as Python's `repr` does** (`'name'`, or `"it's"`), so `errors.csv` compares equal. Checked against the Python on a hand-made estate.
6. **`KbCompare` exit code** is now 0 when no difference is left unexplained (it was: when nothing differs). The last line still says whether anything differed. `Result.same()` keeps its meaning; `Result.nothingUnexplained()` is new.
7. **How difference 6 is checked.** The old cell is split where an identifier starts: the text `<estate>::<layer>::` cannot occur inside an identifier, because both programs refuse a name containing `::`. The list must equal the new cell's non-empty lines. No list of known identifiers is needed, and none is used.
8. **How difference 5 is checked.** Cell: the old list is the new one with *n* entries taken out, and the old `errors.csv` has at least *n* unresolved-reference rows for the object. Missing `errors` row: the new Knowledge Base has more resolved entries for that object than the old one. Missing `coverage` row for `ExpressionRef`: all the errors it counts are explained.

**Tests changed.** `ExtractCommandTest`: the list of files written and the counts printed for `testrpd` (5 parsed / 9 skipped became 19 / 4). The assertions are the same; the expected values moved because more is read.

**Checked against the Python.** Besides the two sample estates, the Python was run on four hand-made test estates (written under `build/pyref/`). All tables matched apart from differences 5 and 6.

**Result on the samples** (`./build.sh compare <estate> <tables>`):

| Table | Estate | Reference | Generated | Missing | Extra | Cells explained | Cells unexplained |
|---|---|---|---|---|---|---|---|
| `logical_tables` | sampleapp / bi-analytics-2013 | 11 / 71 | 11 / 71 | 0 | 0 | 0 | 0 |
| `logical_columns` | sampleapp | 77 | 77 | 0 | 0 | 2 (difference 5) | 0 |
| `logical_columns` | bi-analytics-2013 | 933 | 933 | 0 | 0 | 9 (5: 2, 6: 7) | 0 |
| `aggregation_rules` | sampleapp / bi-analytics-2013 | 13 / 22 | 13 / 22 | 0 | 0 | 0 | 0 |
| `logical_table_sources` | sampleapp | 17 | 17 | 0 | 0 | 3 (difference 6) | 0 |
| `logical_table_sources` | bi-analytics-2013 | 75 | 75 | 0 | 0 | 0 | 0 |
| `lts_column_map` | sampleapp / bi-analytics-2013 | 95 / 940 | 95 / 940 | 0 | 0 | 0 | 0 |
| `errors` | each | 2 | 0 | 2, both explained by 5 | 0 | 0 | 0 |

Only 10 `_refs` cells differ for difference 6, not every one: a cell with a single entry is the same text in both forms. `unparsed` and `coverage`, limited to the object types read so far: no differences (79 and 74 `unparsed` rows), apart from the `ExpressionRef` coverage row, which goes with the four errors.

**The four reading-order errors.** All four are gone from `errors.csv` (both estates now exit 0), and the four cells have the entries the Python dropped: `...Dim Products (Translated).PROD_DESC`, `sampleapp::var::PREFERRED_CURRENCY`, `...Time: Year-Quarter-Month-Week-Day.Month`, `...DIM_TIMESDim.Years`. Until story 1.6, the last three name objects whose own tables are not written yet.

**Open points.**

1. Should a mapping whose first reference does not resolve take the second as its column (Python, kept) or be left empty? Not seen in the samples.
2. A `_refs` cell that is wholly empty is either "no references" or "one reference that did not resolve". Only `errors.csv` tells them apart. Difference 6 as confirmed does not say what to do here.
3. `build.cmd compare` passes at most eight arguments, so on Windows the nine tables of these stories need two calls. Not changed: there is no Windows machine to test on.

**Not done.** Nothing in this story is left out.

### 23 November 2026: keys of logical tables (deliberate difference 29)

Added after the review of the first as-is document. The status of this story is unchanged.

- **What changed.** `LogicalTableReader` writes every key of a logical table to a new table, `logical_keys` (`row_id` = `<logical table row_id>.key.<key name>`, `rpd_id`, `logical_table_id`, `name`, `is_primary` as the source writes it, `columns` one Row identifier of `logical_columns` per line in source order), counted in `coverage` as `LogicalKey`. New: `core/LogicalKey`.
- **Why.** The key of a logical table is what the business model itself says identifies a row. It was listed in `unparsed.csv` as not read.
- **Decision.** `is_primary` is the source's text, empty when the attribute is absent; nothing is inferred. A column link that does not resolve leaves an empty line and no error row, as for a foreign key's columns.
- **Numbers.** `sampleapp`: 8 keys on 8 of 11 logical tables, all marked primary. `bi-analytics-2013`: 57 on 57 of 71, all marked primary.
- **Not done.** `LogicalColumn@descriptorIDRef`, `@customSortOrderRef`, `@isDerived`, `LogicalTable@isLookupTable` and `MeasureDefn@isCommutative` stay unread.

### 23 November 2026 (second round): the join path inside a table source (difference 34), closure table (36), lookup flag and descriptor column (37)

- **What changed.** `LogicalTableSourceReader` writes two new tables: `lts_tables` (the physical tables a source lists, in order) and `lts_joins` (each join under the table it is listed under, with `join_type` and `cardinality` exactly as written, its two tables and `physical_join_id`). Both are counted in `coverage` (`Link`, `Join`). `logical_table_sources` has `closure_table_id`; `logical_tables` has `is_lookup_table`; `logical_columns` has `descriptor_id_column_id`. New: `core/SourceTable`, `core/SourceJoin`.
- **Why.** An inner join inside a source drops rows and no formula shows it. Guide 01 lists the unread join path as a known limit; the port rule allows closing it as a numbered difference.
- **Decision: one row per `Join` element.** The source lists a join once under each of its two tables, with the cardinality and an outer join's side stated from that table. Both rows are kept; rows with one `physical_join_id` are the same join. Merging them would mean deciding which side is "the" statement.
- **Decision: the condition is not copied.** A `Join` carries no condition; it points at a physical join. The packet prints that join's columns beside the join type.
- **Numbers.** `sampleapp`: 18 source tables, 2 join rows (1 join, `Inner`). `bi-analytics-2013`: 93 and 36 (20 `Inner`, 8 `LeftOuter`, 8 `RightOuter`).
- **Not checked.** That the server really writes the recorded join type into its SQL has not been compared with a running OBIEE.

## Spec Change Log

## Review Triage Log

## Verification

**Commands:**

- `./build.sh` -- expected: the jar is built at `build/obi2pbi.jar` and every test passes
