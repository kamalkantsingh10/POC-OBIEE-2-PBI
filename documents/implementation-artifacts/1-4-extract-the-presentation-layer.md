---
title: 'Extract the presentation layer'
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

# Story 1.4: Extract the presentation layer

<frozen-after-approval reason="human-owned intent — do not modify unless human renegotiates">

## Intent

**Problem:** Report authors, and FSD authors, speak in presentation names. The Knowledge Base must hold subject areas, presentation tables and presentation columns so that work can start from those names.

**Approach:** Port the presentation-layer readers and tie each presentation column to its logical column.

## Boundaries & Constraints

**Always:**

- Port the Python reference as it is. Change behaviour only where `docs/port-guide/00-read-me-first.md` lists a deliberate difference; a new difference is added to that list in this story.
- Java 11 language level, `java.base` and `java.xml` only (AD-15). No build tool, no test library.
- All XML is read through the recording layer, so unread content is found by observation (AD-3).
- All CSV goes through the one CSV writer (AD-8). Text is written exactly as the source holds it (AD-5).
- Identifiers are `<estate>::pres::<subject area>.<table>.<column>`.
- Aliases and display names are kept as the source holds them.

**Never:**

- Presentation hierarchies (story 1.6).
- Lineage rows (story 1.5).

## I/O & Edge-Case Matrix

| Scenario | Input / State | Expected Output / Behavior | Error Handling |
|----------|--------------|---------------------------|----------------|
| Happy path | Subject areas with tables and columns | `presentation_subject_areas.csv`, `presentation_tables.csv`, `presentation_columns.csv` | N/A |
| Column link | A presentation column | `logical_column_id` names its logical column | N/A |
| Renamed column | A presentation column whose name overrides the logical name | `override_logical_name` set as in guide 01 §3.5 | N/A |
| Implicit fact column | A subject area that has one | `implicit_fact_column_id` set | N/A |
| Missing logical column | A presentation column whose reference does not resolve | Row still written; `errors.csv` row | Exit 3 |

</frozen-after-approval>

## Code Map

- `docs/port-guide/01-repository-reader.md` -- §2.7 PresentationCatalog, §2.8 PresentationTable, §3.5 presentation tables
- `src/test/fixtures/GROUND-TRUTH.md` -- expected presentation rows
- `reference/knowledge-base/presentation_subject_areas.csv, presentation_tables.csv, presentation_columns.csv` -- expected output on the large samples

## Tasks & Acceptance

**Execution:**

- [x] `src/main/java/obi2pbi/core/` -- subject area, presentation table, presentation column
- [x] `src/main/java/obi2pbi/in/mds/` -- PresentationCatalog and PresentationTable readers
- [x] `src/main/java/obi2pbi/out/KnowledgeBaseWriter.java` -- write the three tables
- [x] `src/test/java/obi2pbi/` -- tests for the matrix rows and ground truth

**Acceptance Criteria:**

- Given `testrpd` and the constructed cases, when `extract` runs, then the presentation rows equal the ground truth.
- Given the sample repositories are fetched, when `KbCompare` runs on the three tables, then there are no unexplained differences.

## Implementation Notes

Built together with stories 1.3 and 1.5; the shared notes (expressions, `KbCompare`, decisions) are in story 1.3.

**Built.** `extract` now writes `presentation_subject_areas.csv`, `presentation_tables.csv` and `presentation_columns.csv`. Each presentation column names its logical column; the rename flag and the display name are kept as the source holds them.

**Files.**

| File | What |
|---|---|
| `core/SubjectArea`, `PresentationTable`, `PresentationColumn` | The model |
| `core/KbSchema` | The three tables |
| `in/mds/PresentationCatalogReader`, `PresentationTableReader` | The two readers, registered in `RepositoryReader.READERS` |
| `test/PresentationLayerTest` | 3 tests: both ground-truth files, broken links, aliases, parts with no name |

**How each matrix row is met.**

| Scenario | Test |
|---|---|
| Happy path, column link, renamed column | `testFixtureMatchesItsGroundTruth` |
| Implicit fact column | the same, and `testConstructedImplicitFactColumn` |
| Missing logical column: row still written, error row, exit 3 | `testBrokenLinksAliasesAndUnnamedParts` |

**Decisions.**

1. The subject area reader is registered before the presentation table reader, so a table finds its subject area's identifier in the first phase whatever the file order. A test puts the table's file first.
2. A subject area whose business model or implicit fact column does not resolve gets an empty cell and no error row, as in the Python.
3. `aliases` is joined with ` | `, as in the Python. An alias that itself contains ` | ` cannot be told from two aliases; kept, and the test shows it.
4. Presentation hierarchies are untouched: still `unmodelled_type` and `skipped`.

**Result on the samples.**

| Table | Estate | Reference | Generated | Missing | Extra | Differing cells |
|---|---|---|---|---|---|---|
| `presentation_subject_areas` | sampleapp / bi-analytics-2013 | 3 / 18 | 3 / 18 | 0 | 0 | 0 |
| `presentation_tables` | sampleapp / bi-analytics-2013 | 10 / 118 | 10 / 118 | 0 | 0 | 0 |
| `presentation_columns` | sampleapp / bi-analytics-2013 | 59 / 1484 | 59 / 1484 | 0 | 0 | 0 |

**Open points.** None.

**Not done.** Nothing in this story is left out.

## Spec Change Log

## Review Triage Log

## Verification

**Commands:**

- `./build.sh` -- expected: the jar is built at `build/obi2pbi.jar` and every test passes
