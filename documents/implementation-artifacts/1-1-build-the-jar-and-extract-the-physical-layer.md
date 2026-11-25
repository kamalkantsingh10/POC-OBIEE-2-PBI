---
title: 'Build the jar and extract the physical layer'
type: 'feature'
created: '2026-11-23'
status: 'done'
route: ''
review_loop_iteration: 0
context:
  - '{project-root}/docs/port-guide/00-read-me-first.md'
  - '{project-root}/docs/port-guide/01-repository-reader.md'
  - '{project-root}/docs/port-guide/03-core-output-slice.md'
  - '{project-root}/documents/planning-artifacts/architecture/architecture-POC-OBIEE-2-PBI-2026-11-23/ARCHITECTURE-SPINE.md'
---

# Story 1.1: Build the jar and extract the physical layer

<frozen-after-approval reason="human-owned intent — do not modify unless human renegotiates">

## Intent

**Problem:** There is no Java project yet. The Operator needs one jar that reads a repository export and writes the database tables and columns OBIEE knows about, so that every later story has a working skeleton to extend.

**Approach:** Create the project skeleton (build scripts, test runner, package layout, command line, CSV writer, manifest) and port the physical-layer part of the Python repository reader on top of it.

## Boundaries & Constraints

**Always:**

- Port the Python reference as it is. Change behaviour only where `docs/port-guide/00-read-me-first.md` lists a deliberate difference; a new difference is added to that list in this story.
- Java 11 language level, `java.base` and `java.xml` only (AD-15). No build tool, no test library.
- All XML is read through the recording layer, so unread content is found by observation (AD-3).
- All CSV goes through the one CSV writer (AD-8). Text is written exactly as the source holds it (AD-5).
- Package layout and dependency direction follow the Architecture Spine: `cli` -> `in.*` / `out` -> `core`; `core` has no XML or file types (AD-6).
- Row identifiers are `<estate>::phys::<qualified name>`, built from names (AD-4, guide 01 §3.1). The estate name is the estate folder's name.
- The same input gives byte-identical files, apart from `manifest.csv` (AD-8).

- **Schema (FR-21, AD-8).** Each table is declared once in the code, with a description, what a row means, and for every column its meaning, whether it is the key, and what it refers to. The writer takes its columns from that declaration, and `extract` writes `schema_tables.csv` and `schema.csv` from it into every Knowledge Base.

**Never:**

- Logical, presentation and catalog content (later stories).
- `_guarded` columns.
- Maven, Gradle, JUnit, or any downloaded dependency.
- Copying sample estates into version control; `estates/` and `kb/` are git-ignored.

## I/O & Edge-Case Matrix

| Scenario | Input / State | Expected Output / Behavior | Error Handling |
|----------|--------------|---------------------------|----------------|
| Happy path | `extract estates/<estate>` where the folder holds `rpd/oracle/bi/server/base/` | `kb/<estate>/` with `physical_tables.csv`, `physical_columns.csv`, `physical_table_definitions.csv`, `manifest.csv`; exit 0 | N/A |
| Alias table | A physical table that stands for another | `source_table_id` names the table it stands for | N/A |
| Table that is a query | A physical table of type `select` | Its SQL is in `physical_table_definitions.csv`, exactly as written | N/A |
| Rerun | Same input twice | All files except `manifest.csv` byte-identical | N/A |
| No repository found | Folder has neither `rpd/` nor `catalog/` | Message naming the expected layout; nothing written | Exit 2 |
| Wrong command line | Unknown command or missing argument | Usage text | Exit 2 |
| Output folder not ours | `--out` points at a non-empty folder with no `manifest.csv` | Refuses to empty it (AD-14) | Exit 2 |

</frozen-after-approval>

## Code Map

- `docs/port-guide/01-repository-reader.md` -- §1 input layout and cross-references, §2.1 PhysicalTable, §3.1 identifier rules, §3.2 cell conventions, §3.3 physical tables, §5.4 aliases, §5.5 opaque views
- `docs/port-guide/03-core-output-slice.md` -- §3 CSV writer rules, §4.2 manifest, §5 command line, §8 invariants the tests must reproduce
- `src/test/fixtures/testrpd/` -- small repository fixture from the first attempt; expected results in `src/test/fixtures/GROUND-TRUTH.md`
- `reference/test-corpus/constructed-cases/` -- second hand-made repository (and catalog) with its own `GROUND-TRUTH.md`
- `reference/knowledge-base/physical_*.csv` -- expected output on the large samples; compare when the samples are fetched
- `reference/test-corpus/corpus.md` -- how to fetch the two sample repositories into `estates/`

## Tasks & Acceptance

**Execution:**

- [x] `build.sh, build.cmd` -- compile with `javac --release 11`, package the jar, run the tests -- the only build path (AD-15)
- [x] `src/main/java/obi2pbi/cli/Main.java` -- command dispatch, usage, exit codes 0/2/3 -- shared by all four commands (AD-14)
- [x] `src/main/java/obi2pbi/core/` -- physical table, column and definition model; identifier builder -- no I/O
- [x] `src/main/java/obi2pbi/in/mds/` -- file discovery, recording XML layer, reference index, PhysicalTable reader -- port of guide 01 §1 and §2.1
- [x] `src/main/java/obi2pbi/out/CsvWriter.java` -- the one CSV writer: UTF-8, every field quoted, LF, sorted rows -- guide 03 §3 minus `_guarded`
- [x] `src/main/java/obi2pbi/out/KnowledgeBaseWriter.java` -- writes the tables and `manifest.csv`; refuses to empty a folder it did not write
- [x] `src/test/java/obi2pbi/` -- test runner plus tests for the I/O matrix, determinism, and the dependency rules
- [x] `src/test/java/obi2pbi/KbCompare.java` -- compares a generated table with `reference/knowledge-base/`, ignoring `_guarded` columns -- used by every Epic 1 story
- [x] `.gitignore` -- ignore `build/`, `estates/`, `kb/`

**Acceptance Criteria:**

- Given a machine with only a JDK, when `build.sh` or `build.cmd` runs, then one jar is produced whose classes are Java 11 class files, and the test runner reports every test.
- Given `src/test/fixtures/testrpd`, when `extract` runs, then the physical rows equal those recorded in `GROUND-TRUTH.md`.
- Given the code, when the tests run, then a test fails if `core` imports from `in`, `out` or `cli`, or if any class imports outside `java.base` and `java.xml`.
- Given the sample repositories are fetched, when `KbCompare` runs on the physical tables, then every difference is explained by the deliberate-differences list.

## Implementation Notes

**Built.** `./build.sh` compiles with `javac --release 11`, builds `build/obi2pbi.jar`, and runs the project's own test runner. `java -jar build/obi2pbi.jar extract <estate-folder> [--out <folder>] [--timestamp <text>]` reads `<estate>/rpd` and writes `physical_tables`, `physical_columns`, `physical_table_definitions`, the run tables, the two schema tables and `manifest.csv`.

**Files.**

- `build.sh`, `build.cmd`, `README.md`.
- `cli/`: `Main` (a list of commands; a new command is one line), `Command`, `UsageException`, `ExtractCommand`, `EstateLayout`.
- `core/`: `Identifiers`, `IdentifierException`, `Text` (code-point order, Python's idea of white space), `Estate`, `Row`, `Expression`, `PhysicalTable`, `PhysicalColumn`, `PhysicalTableDefinition`, `PhysicalJoin`, `Unparsed`, `ExtractionError`, `Coverage`, and `KbColumn` / `KbTable` / `KbSchema`.
- `in/xml/`: `XmlNode`, `XmlTreeReader`, `SourceTree`, `ReadAudit`.
- `in/mds/`: `RepositoryFiles`, `ObjectFile`, `ReferenceIndex`, `ReadContext`, `ObjectReader`, `PhysicalTableReader`, `RepositoryReader`, `RepositoryLayoutException`.
- `in/kb/`: `CsvReader`, `CsvTable`.
- `out/`: `CsvWriter`, `KnowledgeBaseWriter`, `NotAKnowledgeBaseException`.
- `src/test/java/obi2pbi/`: `TestRunner`, `Check`, `SkipException`, `TestSupport`, `KbCompare`, and the test classes `CsvTest`, `IdentifiersTest`, `XmlLayerTest`, `PhysicalLayerTest`, `ExtractCommandTest`, `GapsAndErrorsTest`, `SchemaTest`, `ArchitectureTest`, `BuildTest`, `KbCompareTest`.

**Patterns later stories extend.**

- A new table: one method in `core/KbSchema` plus one entry in `KbSchema.TABLES`. The writer, the manifest, `schema.csv` and the shape tests follow. A new row list is also added to `Estate.rowLists()` and `Estate.merge`.
- A new repository object type: one class implementing `in/mds/ObjectReader`, added to `RepositoryReader.READERS`. The set of handled types comes from that list.
- A new command: one class implementing `cli/Command`, added to `Main.COMMANDS`.
- All XML goes through `XmlNode`. `text()` is the text before the first child element; `deepText()` is all text, nested parts included (needed for difference 8). `rawAttr`, `rawText` and `children()` do not record and are for indexing and the audit only.

**Decisions.**

1. The recording XML layer is in `obi2pbi.in.xml`, not `in.mds` as the task line says, because the catalog reader must reuse it and one inbound adapter should not import another. `ArchitectureTest` enforces: adapters may use `core` and `in.xml`, never each other.
2. Readers work in two phases (bind identifiers for all files, then read rows), which is deliberate difference 5 built in from the start. The Python's separate alias pass is therefore not needed.
3. Foreign keys are read into `Estate.physicalJoins` now, with their columns already resolved to Row identifiers (difference 7), so that `coverage` and `unparsed` agree with the reference for `PhysicalTable`. The `physical_joins` table is not written (story 1.6).
4. One table definition (`KbSchema`) drives the writer and the two schema files (added requirement, difference 16). It sits in `core` because it is plain data and the slicer and `in.kb` will need it.
5. `CsvReader` was built now, in `in/kb`, because `KbCompare` and the round-trip tests need it; story 2.1 extends that package.
6. The test runner finds tests by reflection. The "no reflection" convention is kept for the jar's code; a hand-kept list of tests could silently miss one.
7. The output folder is always emptied of its top-level `.csv` files before writing (AD-14); there is no `--clean`. The refusal is checked before any reading, so a refused run costs nothing.
8. Only an estate folder is accepted. A bare repository tree is refused with a hint (difference 15).
9. `--timestamp` is kept from the Python, so tests can produce a byte-identical manifest.
10. An `rpd/` folder with no `oracle/bi/server/base/`, or with no `.xml` file, is wrong usage (exit 2, nothing written), as in the Python.
11. New entries 14 to 17 were added to the deliberate-differences list, marked as not yet confirmed.

**Surprises.** None in the data: the physical tables of both sample repositories match the reference with no difference.

**Where the guide and the Python disagree.** Guide 01 §1.4 recommends DOM; guide 03 §1.9 and this story recommend a tree built from the streaming reader. The streaming reader was used.

**Not done.**

- `build.cmd` was written to mirror `build.sh` and has not been run: there is no Windows machine here.
- A `catalog/` folder is recognised and listed in the manifest's `halves_present`, and is not read; `extract` prints a warning line. A catalog-only estate gives a Knowledge Base with empty tables and exit 0 until the catalog reader exists.

**Checked against the reference** (`./build.sh compare <estate> physical_tables physical_columns physical_table_definitions`): sampleapp 22 / 139 / 12 rows, bi-analytics-2013 78 / 957 / 32 rows; no missing rows, no extra rows, no differing cells; row order also identical.

### 23 November 2026: physical keys, column scale and place (deliberate differences 28 and 31)

Added after an independent review judged the first as-is document "not enough to create the tables". The status of this story is unchanged.

- **What changed.** `PhysicalTableReader` now writes every key of a physical table to a new table, `physical_keys` (`row_id` = `<table row_id>.key.<key name>`, `rpd_id`, `table_id`, `name`, `columns` one Row identifier per line in source order), and counts it in `coverage` as `PhysicalKey`. `physical_columns` has two more columns at the end: `scale` and `position` (the column's place in its table in the source, from 1). New: `core/PhysicalKey`; changed: `core/PhysicalColumn`, `core/Estate`, `core/KbSchema`.
- **Why.** No document could state what identifies a row of a table, or a numeric column's decimal digits, because the Knowledge Base did not hold them. `unparsed.csv` listed both as not read.
- **Decision: no "is primary" column.** In both sample repositories a `PhysicalKey` element carries only `mdsid` and `name`. The source does not say which key is the primary key, so the table does not either; the schema note says so. `SAMP_TIME_MTH_D` has two keys.
- **Decision: the word `key` in the identifier.** Three `sampleapp` tables have a key named `ID` beside a column `ID`; without the fixed word they would share an identifier text.
- **Decision: `position` counts from 1 and keeps gaps.** `physical_columns.csv` is sorted by Row identifier, so the source order was lost; in 4 of 22 `sampleapp` tables it is not name order.
- **Numbers.** `sampleapp`: 14 keys on 13 of 22 tables, 5 columns with a scale. `bi-analytics-2013`: 63 keys on 63 of 78 tables, 44 columns with a scale. `unparsed` rows: 145 to 128 and 238 to 221 over differences 28 to 32 together.
- **Comparison.** `KbCompare` reports a table with no reference counterpart as "new table (difference N)" only when every row equals what the new test class `KeySource` reads from the estate's files with the JDK's parser; the two added columns are checked the same way. `./build.sh compare-all`: 0 unexplained.
- **Tests changed, and why.** `GapsAndErrorsTest.testUnreadAttributeElementAndTextAreListed` used a physical key and a column's scale as its examples of unread content; both are read now, so it uses an element and an attribute no reader takes. `JoinsAndHierarchiesTest` asserted that a key's name "stays listed" as not read; it now asserts the opposite and checks the new rows. `ExtractCommandTest` lists the three new files.
- **Not done.** `PhysicalKey@mdsid`, a table's description, row counts, cache settings, an alias column's link to its source column (`PhysicalColumn@sourceColumnRef`, 101 in `bi-analytics-2013`) and the parent-child links of a table stay unread and listed.

### 23 November 2026 (second round): parent-child columns, description, alias columns (differences 36 and 37), and `nullable`

- **What changed.** `physical_tables` has five more columns at the end: `description`, and `member_key_column_id`, `parent_key_column_id`, `distance_column_id`, `leaf_column_id` for the relationship table of a parent-child hierarchy. `physical_columns` has one more, `source_column_id` (the column an alias column stands for). `PhysicalTableReader.physicalJoinId` builds the Row identifier of a foreign key or complex join for `lts_joins` (story 1.3 note).
- **`nullable`, looked at in the files.** Both sample repositories write the attribute only as `true` (635 columns) and leave it out on the other 461; `false` never occurs. The value in the Knowledge Base is unchanged (`true` or empty). The schema note now says what an absent attribute most likely means and that the files do not state it; the packets print `nullable true` or `nullable not stated`.
- **Comparison.** The added-column check in `KbCompare` is now one rule for seven tables (`ADDED_COLUMNS`), every cell checked against `KeySource`. `./build.sh compare-all`: 0 unexplained.
- **Test changed, and why.** `GapsAndErrorsTest` used a table's `Description` as its example of unread text; it is read now, so the example is another element.
- **Numbers.** `unparsed` rows: `sampleapp` 128 to 110, `bi-analytics-2013` 221 to 211, over differences 34 to 37 together.
- **Left unread on purpose:** row counts, update times, cache settings, `iconIndex`, internal ids. The README lists them with counts.

## Spec Change Log

## Review Triage Log

## Design Notes

The recording XML layer is the part most easily got wrong. Guide 03 §1.9 recommends reading each file with the JDK's streaming reader into a small tree of our own, where every attribute, child and text read is recorded, and reporting whatever was never touched. Build it here, even though `unparsed.csv` is only written in story 1.2, because every reader depends on it.

The fixture is a bare repository tree. Wrap it in an estate folder for the tests (`<estate>/rpd/oracle/...`) without editing the fixture files.

## Verification

**Commands:**

- `./build.sh` -- expected: the jar is built at `build/obi2pbi.jar` and every test passes
- `java -jar build/obi2pbi.jar extract <estate folder> --out build/kb-test` -- expected: prints counts and the output folder; exit 0
