---
title: 'Extract joins, hierarchies, variables and security rules'
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

# Story 1.6: Extract joins, hierarchies, variables and security rules

<frozen-after-approval reason="human-owned intent — do not modify unless human renegotiates">

## Intent

**Problem:** Some repository content changes a number without changing its formula: an inner join that drops rows, a variable inside a filter, a row-level security rule. The As-is document must be able to describe these.

**Approach:** Port the remaining repository readers: physical and logical joins, dimensions and hierarchies, variables and init blocks, and security. This completes the repository half.

## Boundaries & Constraints

**Always:**

- Port the Python reference as it is. Change behaviour only where `docs/port-guide/00-read-me-first.md` lists a deliberate difference; a new difference is added to that list in this story.
- Java 11, `java.base` and `java.xml` only. All XML through the recording layer (AD-3). All CSV through the one writer (AD-8). Text exactly as the source holds it (AD-5).
- Ragged, skip-level and parent-child hierarchies keep their flags; a hierarchy is never flattened.
- A value the reference treats as a credential is redacted in the output, and the redaction is recorded (guide 01 §6.6).
- `physical_joins.columns` holds Row identifiers, not OBIEE's internal ids (difference 7).

**Never:**

- Writing a credential to any output file.
- Inferring which role a filter belongs to when the source does not say; record the gap as the reference does.

## I/O & Edge-Case Matrix

| Scenario | Input / State | Expected Output / Behavior | Error Handling |
|----------|--------------|---------------------------|----------------|
| Physical joins | Foreign keys and complex joins | `physical_joins.csv` with kind, tables, cardinality and the condition in three forms | N/A |
| Logical joins | Logical complex joins | `bmm_joins.csv` with join type (inner or outer) and both multiplicities | N/A |
| Level-based hierarchy | A dimension with levels | `logical_dimensions.csv`, `logical_levels.csv`; presentation side in `presentation_hierarchies.csv`, `presentation_levels.csv`, `presentation_level_columns.csv`, levels in order | N/A |
| Parent-child hierarchy | A dimension with no levels | `is_value_based` true; its single column in `display_column_id` | N/A |
| Variables | Session and repository variables with init blocks | `variables.csv` and `init_blocks.csv`, text exactly as written | N/A |
| Credential | An init block or variable value the reference redacts | Value replaced; `redacted` true; `unparsed.csv` row of kind `redacted_value` | N/A |
| Security | Roles with row filters, object and query privileges | `security_rls.csv` rows with their `kind` | N/A |

</frozen-after-approval>

## Code Map

- `docs/port-guide/01-repository-reader.md` -- §2.2 ComplexJoin, §2.5 LogicalComplexJoin, §2.6 Dimension, §2.9 PresentationHierarchy, §2.10 InitBlock, §2.11 Variable, §2.12 security; §3.3 to §3.8 for the tables; §6.4 join types, §6.5 hierarchies, §6.6 redaction, §6.7 security rows; §10 known gaps
- `src/test/fixtures/testrpd/` -- has two dimensions and two presentation hierarchies
- `reference/knowledge-base/` -- `physical_joins`, `bmm_joins`, `logical_dimensions`, `logical_levels`, `presentation_hierarchies`, `presentation_levels`, `presentation_level_columns`, `variables`, `init_blocks`, `security_rls`
- `reference/test-corpus/corpus.md` -- how to fetch the two sample repositories

## Tasks & Acceptance

**Execution:**

- [x] `src/main/java/obi2pbi/core/` -- joins, dimensions, levels, hierarchies, variables, init blocks, security rows
- [x] `src/main/java/obi2pbi/in/mds/` -- the readers listed in the Code Map
- [x] `src/main/java/obi2pbi/out/KnowledgeBaseWriter.java` -- write the ten tables
- [x] `src/test/java/obi2pbi/` -- tests for the matrix rows, including one asserting no credential text reaches any output file
- [x] `README.md` -- how to build, how to fetch the sample repositories, how to run `extract`, how to read `errors.csv` and `unparsed.csv`

**Acceptance Criteria:**

- Given the fixtures, when `extract` runs, then the rows for these tables equal the ground truth.
- Given the two sample repositories fetched into `estates/`, when `extract` runs on each, then it completes, and `KbCompare` over every repository table shows only differences on the deliberate-differences list.
- Given that run, when the story is closed, then the counts of parsed objects, `unparsed.csv` rows and `errors.csv` rows are recorded in Implementation Notes next to the reference's counts.

## Implementation Notes

Built on 23 November 2026.

**What was built.** The remaining repository readers, so the repository half is complete: every object type the Python reader handles now has a Java reader, and ten more tables are written.

| Table | Reader | Rows: sampleapp / bi-analytics-2013 |
|---|---|---|
| `physical_joins` | `PhysicalTableReader` (foreign keys, from story 1.1), `ComplexJoinReader` | 22 / 82 |
| `bmm_joins` | `LogicalComplexJoinReader` | 9 / 75 |
| `logical_dimensions`, `logical_levels` | `DimensionReader` | 5, 22 / 20, 84 |
| `presentation_hierarchies`, `presentation_levels`, `presentation_level_columns` | `PresentationHierarchyReader` | 5, 20, 16 / 53, 226, 183 |
| `init_blocks` | `InitBlockReader` | 2 / 152 |
| `variables` | `VariableReader` | 2 / 343 |
| `security_rls` | `PrivilegePackageReader`, `ObjectPrivilegeReader`, and `SecurityRule.collectRowFilters` after lineage; `GroupReader` only names roles | 9 / 189 |

**Files.**

- New in `core/`: `BmmJoin`, `LogicalDimension`, `LogicalLevel`, `PresentationHierarchy`, `PresentationLevel`, `PresentationLevelColumn`, `Variable` (with the credential name rule), `InitBlock`, `SecurityRule` (with the row-filter step). Changed: `Estate` (nine lists, in the uniqueness check under the Python list names), `KbSchema` (ten tables), `KbTable` (schema layers `var` and `sec`), `Text` (`strip`).
- New in `in/mds/`: the nine readers named above. Changed: `RepositoryReader` (registration, in the Python pass order), `ReadContext` (the in-memory set of withheld values), `ReferenceIndex` (`bindWithoutReading` removed). **Deleted: `NamesAhead`**; its naming is now the first phase of `DimensionReader` and `VariableReader`, with the same identifiers.
- `out/KnowledgeBaseWriter.java` needed no change: it writes whatever `KbSchema.TABLES` declares.
- Tests: new `JoinsAndHierarchiesTest` (6 tests) and `VariablesAndSecurityTest` (7 tests); `KbCompareTest` (2 more); `KbCompare` (differences 7 and 19, `--source`, explained extra rows).
- `README.md`, `docs/port-guide/00-read-me-first.md` (difference 19, notes for 5 and 7), `src/test/fixtures/GROUND-TRUTH.md` (a section for the dimension and hierarchy fixtures), `build.sh` and `build.cmd` (usage text only).

**Counts next to the reference** (`java -jar build/obi2pbi.jar extract estates/<estate>`, exit 0 for both).

| | sampleapp: reference | sampleapp: jar | bi-analytics-2013: reference | bi-analytics-2013: jar |
|---|---|---|---|---|
| Parsed objects (sum of `coverage.parsed`) | 559 | 559 | 5878 | 5878 |
| Skipped (sum of `coverage.skipped`) | 17 | 15 | 104 | 102 |
| `unparsed.csv` rows | 146 | 146 | 238 | 239 |
| `errors.csv` rows | 2 | 0 | 2 | 0 |

The two errors fewer per estate, and the two skipped fewer, are the reading-order errors removed by difference 5. The one more `unparsed` row is the record of the value withheld under difference 19.

**Comparison.** `./build.sh compare sampleapp` and `./build.sh compare bi-analytics-2013`, unfiltered, over all 22 repository tables plus `errors`, `unparsed` and `coverage`: both exit 0, no missing row, no extra row and no differing cell left unexplained. Explained: sampleapp 5 by difference 5, 3 by 6, 18 by 7; bi-analytics-2013 5 by 5, 7 by 6, 82 by 7, 4 by 19. No reference table with rows for either estate is left ungenerated.

I also ran the Python reference on six hand-made estates written by the new tests (the fixture, and estates with unnamed parts, dangling links, empty packages, a privilege with no object, planted credential values) and compared every table: no unexplained difference in any. The outputs are under `build/pyref/s16-*`.

**Decisions, and why.**

1. **Difference 19 (new, not confirmed): a value withheld for one variable is withheld wherever else it is a variable's value.** The story's own test ("no credential text reaches any output file") failed on real data with an exact port: in `bi-analytics-2013` the USER variable beside the PASSWORD variable holds the same value, and the Python writes it. The build rules say never to write a credential to any file, and that outranks "port as it is", so I widened the rule by the smallest step that makes the test hold, recorded it in the list, and made `KbCompare` check it against the source. It changes three cells of one `variables` row and adds one `unparsed` row. To go back to the exact port, remove the `withheldValues` test in `VariableReader.read`; the sample-estate test will then fail.
2. **`physical_joins.columns` is one Row identifier per line**, with an empty line for a column that is not found, as difference 6 does for `_refs` cells. Difference 7 says "Row identifiers" and not how they are joined; identifiers contain spaces.
3. **Schema layers `var` and `sec`** for `variables`, `init_blocks` and `security_rls`, matching the layer part of their Row identifiers. None of the five existing layers fits. `SchemaTest`'s list of allowed layers gained the two.
4. **`security_rls.subject_id` declares no reference.** It is a name, an identifier of any table, or a logical table, depending on `kind`; a declared target would be false for two kinds of three. The note says how to read it. `physical_joins.columns` likewise (a list).
5. **Joins and object privileges do everything in the second phase** and are given no identifier that can be referred to, as in the Python (which also never reads their `mdsid`, so `unparsed.csv` matches). Init blocks, roles and privilege packages are named in the first phase, so a formula that refers to one now resolves whatever the order (difference 5).
6. **`KbCompare` reads the estate's files for differences 7 and 19**, with the JDK's parser and not the jar's reader, so the check is independent of the code it checks. A wrong column, table, order or count, an identifier that is not a generated physical column, and a missing source folder all stay unexplained; tests show each.
7. A name that cannot be part of an identifier gives an `invalid_name` row for that object only (difference 3), for every new object type.

**Tests changed, and why.** None was weakened. `ExtractCommandTest` (file list; 19 parsed / 4 skipped became 28 / 0), `PresentationLayerTest` (hierarchies were "not read": 0 / 2 became 2 / 0), `LogicalLayerTest` (the dimension and variable were asserted to be still unmodelled; now asserted read, and present as rows under the same identifiers). Each asserted the absence of this story's readers.

**Where the guide and the Python disagree.** Nothing that changes output. Guide 01 §3.8 says an object privilege's object is "observed: always a subject area"; in `sampleapp` it is also a presentation column. The schema note says "any table".

**Surprises.**

- The credential finding above. The reference Knowledge Base itself holds the value: `reference/knowledge-base/variables.csv`, row of the USER variable. I did not edit it (`reference/` is not to be edited). The source is a public sample repository.
- In both samples every foreign key has exactly one column, and it is a column of the key's own table.

**Not done.** Nothing in the story is left out. Not in scope and not touched: the Web Catalog, `sprint-status.yaml`, other story files. Difference 19 and the two schema layers need Kamal's confirmation.

**Changed on 23 November 2026, after the first real run of the skills (Sample Sales).** Two additions to what this story extracts; both are new deliberate differences, awaiting confirmation (`docs/port-guide/00-read-me-first.md`).

- **Difference 26: a foreign key says what it points at.** Why: the as-is document had to write "to a table that was not recorded" for all 14 foreign keys of Sample Sales, so nobody could say how a fact table joins a dimension table. What: `PhysicalTableReader` follows `PhysicalForeignKey@counterPartKeyRef` to the `PhysicalKey` element it names. `physical_joins.table2_id` is the table that encloses that key; the new column `physical_joins.referenced_columns` is that key's columns as Row identifiers, one per line, in the key's order (line *n* pairs with line *n* of `columns`). A link that is present and leads to no key of a physical table gives an `unresolved_reference` error against the foreign key. A foreign key with no link is not an error. `ReferenceIndex.node(mdsid)` gives a reader the element behind a reference; what is taken from it goes through the recording accessors, so the audit counts as read only the keys that were followed. On the samples: 18 of 18 and 82 of 82 foreign keys resolve, no error row.
- **Difference 27: a role's display name and guid.** Why: the security packet printed the role as two 64-character hexadecimal strings with a space. In the source that text is the `name` of **one** `Group` (`guid="BIAuthor"`, `displayName="BI Author Role"`). What: `PrivilegePackageReader` follows `roleRef` to the role's element and writes `security_rls.role_display_name` and `role_guid` on every `query_privilege` row (two new columns, at the end; empty on the other kinds). `role` is unchanged.
- `KbSchema` describes the new columns and no longer says a foreign key does not name its far table. `KbCompare` explains both differences by reading the estate's files itself (far table and columns rebuilt for every foreign key; role names for every query privilege row; the `unparsed` counts must fall by exactly what the files hold); `KbCompareReport` describes them. `./build.sh compare-all`: 0 unexplained.
- Tests: `JoinsAndHierarchiesTest.testAForeignKeySaysWhichTableAndColumnsItPointsAt` (new), `testPhysicalJoinsOfBothKinds` and `VariablesAndSecurityTest.testSecurityRowsOfTheThreeKinds` (changed: they asserted the old column lists, an empty `table2_id`, and the link as a known gap; the fixture `shapes` now has a real key to point at), three new tests in `KbCompareTest`.
- Not done: a privilege package granted to a user (`PrivilegePackage@userRef`) is still not read; physical keys nothing points at are still not read; a key's name is carried by no table.

### 23 November 2026: keys of levels, and the time flag (deliberate differences 30 and 32)

Added after the review of the first as-is document. The status of this story is unchanged.

- **What changed.** `DimensionReader` writes every key of every level to a new table, `logical_level_keys` (`row_id` = `<level row_id>.key.<key name>`, `rpd_id`, `level_id`, `name`, `is_primary`, `is_chronological`, `is_for_drill_down`, `columns`), counted in `coverage` as `LogicalKey` together with the keys of logical tables. `logical_dimensions` has one more column at the end, `is_time`. `logical_levels` is unchanged: `drill_key_column_id` is still the first column that resolves of the first drill-down key. New: `core/LogicalLevelKey`.
- **Why a table and not columns.** A level has several keys (in `sampleapp`, 24 keys on 17 levels): one that identifies a member and one a user sees when drilling. Two columns on `logical_levels` could not hold that.
- **Decision.** The three flags and `is_time` are the source's text, empty when absent. (The older flags of `logical_dimensions` turn an absent attribute into `false`; they were left as they are, to stay equal to the reference.)
- **Numbers.** `sampleapp`: 24 level keys (17 primary, 6 chronological, 17 for drill-down); 1 of 5 dimensions is a time dimension. `bi-analytics-2013`: 127 level keys (69, 24, 69); 6 of 20.
- **Test changed, and why.** `testDimensionsKeepTheirFlagsAndLevelsTheirPlaces` asserted that two column links stay listed as not read; they are read now, and the test checks the three rows the fixture's level gives.
- **Not done.** Which level is the child of which (`RefChildLevels`), the preferred drill path, and the columns a level lists outside its keys stay unread.

### 23 November 2026 (second round): the order of levels (difference 35)

- **What changed.** `DimensionReader` reads each level's `RefChildLevels`. `logical_levels` has two more columns at the end: `child_level_ids` (as listed) and `parent_level_ids` (the levels of the same dimension that list the level, in source order).
- **Why.** `position` is the order of the file, which is not always depth; the drill order should not rest on it.
- **Decision.** `parent_level_ids` is derived, from `child_level_ids` alone. It is written because every reader needs "what is above this level" and would otherwise invert the list itself. Neither column has a declared single-row reference in `schema.csv`, because each holds a list.
- **Numbers.** `unparsed`: `RefChildLevels` gone; `RefLogicalLevel` 18 to 1 (`sampleapp`), 77 to 5 (`bi-analytics-2013`).
- **Not done.** The preferred drill path (`RefPreferredDrillDown`) stays unread.

## Spec Change Log

## Review Triage Log

## Design Notes

This is the first story where a full comparison with `reference/knowledge-base/` is expected to pass for the repository tables. The reference Knowledge Base holds both sample repositories and the catalog under their own estate names; compare per estate.

## Verification

**Commands:**

- `./build.sh` -- expected: the jar is built at `build/obi2pbi.jar` and every test passes
- `java -jar build/obi2pbi.jar extract estates/sampleapp` -- expected: completes; exit 0 or 3, with the error count explained
