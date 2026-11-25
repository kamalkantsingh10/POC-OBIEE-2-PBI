---
title: 'Record lineage from presentation column to database column'
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

# Story 1.5: Record lineage from presentation column to database column

<frozen-after-approval reason="human-owned intent — do not modify unless human renegotiates">

## Intent

**Problem:** "Where does this number come from" should be one lookup. Today it would take a five-way join across the tables from stories 1.1 to 1.4.

**Approach:** Port the lineage builder: one `lineage.csv` row per chain from presentation column through logical column and logical table source to physical table and column.

## Boundaries & Constraints

**Always:**

- Port the Python reference as it is. Change behaviour only where `docs/port-guide/00-read-me-first.md` lists a deliberate difference; a new difference is added to that list in this story.
- Java 11 language level, `java.base` and `java.xml` only (AD-15). No build tool, no test library.
- All XML is read through the recording layer, so unread content is found by observation (AD-3).
- All CSV goes through the one CSV writer (AD-8). Text is written exactly as the source holds it (AD-5).
- `lineage.csv` holds repository lineage only and is never widened to carry report columns (AD-7).
- The builder lives in `core` and does no I/O.

**Never:**

- Resolving a view's SQL to the tables it reads.
- Recording more than the first database column of a mapping that uses several; this known limit is kept (guide 00, "Kept as it is").

## I/O & Edge-Case Matrix

| Scenario | Input / State | Expected Output / Behavior | Error Handling |
|----------|--------------|---------------------------|----------------|
| Happy path | A presentation column mapped by one source | One row with all five identifiers | N/A |
| Several sources | A logical column mapped by two sources | One row per source | N/A |
| Derived column | A logical column defined by a formula, with no physical column | No lineage row, as the Python and guide 01 §5.3 have it. A mapping to a constant gives a row with empty physical identifiers | N/A |
| Alias | A source mapped to an alias table | The row names the alias; the original table is reachable through `source_table_id` | N/A |
| No chain | A presentation column with no logical column | No physical identifiers; the cause is already in `errors.csv` | N/A |

</frozen-after-approval>

## Code Map

- `docs/port-guide/01-repository-reader.md` -- §3.6 the lineage table, §5 lineage step by step (§5.2 several sources, §5.3 derived columns, §5.4 aliases, §5.5 views)
- `src/test/fixtures/GROUND-TRUTH.md, reference/test-corpus/constructed-cases/GROUND-TRUTH.md` -- expected chains
- `reference/knowledge-base/lineage.csv` -- 1,573 expected rows on the large samples

## Tasks & Acceptance

**Execution:**

- [x] `src/main/java/obi2pbi/core/Lineage.java` -- build lineage rows from the model
- [x] `src/main/java/obi2pbi/out/KnowledgeBaseWriter.java` -- write `lineage.csv`
- [x] `src/test/java/obi2pbi/` -- tests for the matrix rows; a test that every identifier in `lineage.csv` exists in its table

**Acceptance Criteria:**

- Given the fixtures, when `extract` runs, then `lineage.csv` equals the expected rows.
- Given any Knowledge Base, when the tests run, then every identifier in `lineage.csv` resolves to a row in the table it points to.
- Given the sample repositories are fetched, when `KbCompare` runs on `lineage.csv`, then there are no unexplained differences.

## Implementation Notes

Built together with stories 1.3 and 1.4; the shared notes are in story 1.3.

**Built.** `extract` now writes `lineage.csv`: one row for each presentation column and each table source that maps its logical column, with the physical table and column the mapping ends at.

**Files.**

| File | What |
|---|---|
| `core/LineageRow` | The model |
| `core/Lineage.build` | The builder. Reads only the Estate; the architecture test keeps files and XML out of `core` |
| `core/KbSchema` | The table; every identifier column declares the table it points at |
| `in/mds/RepositoryReader` | Calls `Lineage.countMappedSources` and `Lineage.build` after every reader has run |
| `test/LineageTest` | 6 tests |

**How each matrix row is met.**

| Scenario | Test |
|---|---|
| Happy path | `testFixtureMatchesItsGroundTruth` (the 3 rows of the ground truth), `testConstructedCases` |
| Several sources | `testSeveralSourcesDerivedColumnsAliasesAndBrokenChains` |
| Derived column: no row; the formula's references say what it is computed from | the same |
| Alias: the row names the alias; `source_table_id` leads to the table | the same |
| No chain: no row; the cause is in `errors.csv` once | the same |
| Every identifier exists in its table | `testEveryIdentifierInLineageExistsInItsTable` (three fixtures), and `...OnTheSampleEstates` (the two real repositories; skipped, visibly, when `estates/` is not there) |

**Decisions.**

1. **A derived column gives no lineage row**, as in the Python and guide 01 §5.3. The matrix row says "a row with empty physical identifiers, as guide 01 §5.3 describes"; the guide describes no row for a column defined only by a formula, and a row with empty physical identifiers for a mapping that reads no physical column (a constant). Both are built as the guide and the Python have them, and both are tested.
2. `lineage` is declared in the schema under layer `pres`, because a row starts at a presentation column. The schema has no layer for a table that spans the three.
3. The lineage identifier check is written out in `LineageTest`, apart from the schema's general check of declared links, so the story's test does not depend on the schema declarations being right. The sample-estate test runs both.
4. Lineage is built in the repository reader's own Estate, before the merge, as in the Python.

**Kept as it is.** Only the first physical column of a mapping is recorded. Two mappings of one logical column in one source give two lineage rows with the same identifier, reported as `duplicate_identifier`; not seen in the samples.

**Result on the samples.**

| Table | Estate | Reference | Generated | Missing | Extra | Differing cells |
|---|---|---|---|---|---|---|
| `lineage` | sampleapp | 72 | 72 | 0 | 0 | 0 |
| `lineage` | bi-analytics-2013 | 1501 | 1501 | 0 | 0 | 0 |

1,573 rows in all, as the story expects. Every identifier in both files is a row of the table it points to.

**Open points.** The wording of the "Derived column" matrix row (decision 1): a person should confirm that "no row" is what is wanted.

**Not done.** Nothing in this story is left out.

## Spec Change Log

## Review Triage Log

## Verification

**Commands:**

- `./build.sh` -- expected: the jar is built at `build/obi2pbi.jar` and every test passes
