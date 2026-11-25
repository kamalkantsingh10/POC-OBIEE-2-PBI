---
title: 'Link report columns to the repository'
type: 'feature'
created: '2026-11-23'
status: 'done'
route: ''
review_loop_iteration: 0
context:
  - '{project-root}/docs/port-guide/00-read-me-first.md'
  - '{project-root}/docs/port-guide/02-catalog-reader.md'
  - '{project-root}/documents/planning-artifacts/architecture/architecture-POC-OBIEE-2-PBI-2026-11-23/ARCHITECTURE-SPINE.md'
---

# Story 1.10: Link report columns to the repository

<frozen-after-approval reason="human-owned intent — do not modify unless human renegotiates">

## Intent

**Problem:** A report column and the repository are in separate halves of the Knowledge Base. Without a link, an existing report cannot be followed down to the database.

**Approach:** Port the report-lineage resolver: for each report column, find the presentation column it uses and chain to repository lineage, or record why not.

## Boundaries & Constraints

**Always:**

- Port the Python reference as it is. Change behaviour only where `docs/port-guide/00-read-me-first.md` lists a deliberate difference; a new difference is added to that list in this story.
- Java 11, `java.base` and `java.xml` only. All XML through the recording layer (AD-3). All CSV through the one writer (AD-8). Text exactly as the source holds it (AD-5).
- The link is made only inside one estate (AD-7).
- Results go to `report_lineage.csv`; `lineage.csv` is unchanged.
- The resolver lives in `core` and does no I/O.
- Every report column gets exactly one row.

**Never:**

- Parsing calculations to find the columns inside them; the reference does not, and marks them instead.
- Matching on subject area names across estates.

## I/O & Edge-Case Matrix

| Scenario | Input / State | Expected Output / Behavior | Error Handling |
|----------|--------------|---------------------------|----------------|
| Exact match | Report column `"Sales"."Revenue"` in a subject area the estate defines | `resolution` `exact`; presentation, logical and physical identifiers filled | N/A |
| Match after folding case | Names differ only in case | `resolution` `case_normalised` | N/A |
| Calculation | Formula over several columns | `resolution` `not_a_column_reference` | N/A |
| Uploaded dataset | Formula naming `XSA('user'.'dataset')` | `resolution` `outside_semantic_model` | N/A |
| Derived logical column | Resolves to a logical column with no physical column | `source_state` `computed_not_sourced` | N/A |
| No match | Subject area or column not in the estate | `resolution` `unresolved` | N/A |
| One half only | Estate with a catalog and no repository | Every row unresolved for that reason | N/A |
| Several lineage rows | A presentation column with more than one repository lineage row | The row chosen is fixed by a stated rule (see Design Notes) | N/A |

</frozen-after-approval>

## Code Map

- `docs/port-guide/02-catalog-reader.md` -- §4.12 the table, §6 the algorithm (§6.2 per column, §6.3 values, §6.4 XSA handling, §6.5 test vector), §12 fixtures to rebuild
- `reference/test-corpus/constructed-cases/` -- the hand-made matched pair: `constructed/` (repository) and `constructed-catalog/`, with `GROUND-TRUTH.md`
- `reference/knowledge-base/report_lineage.csv` -- expected rows on the samples; none resolve, because the sample halves come from different systems

## Tasks & Acceptance

**Execution:**

- [x] `src/main/java/obi2pbi/core/ReportLineage.java` -- the resolver -- guide 02 §6
- [x] `src/main/java/obi2pbi/out/KnowledgeBaseWriter.java` -- write `report_lineage.csv`
- [x] `src/test/java/obi2pbi/` -- tests for the matrix rows on the constructed pair; a full-run comparison test
- [x] `README.md` -- the full table list with one line each; how to lay out an estate folder

**Acceptance Criteria:**

- Given the constructed pair in one estate folder, when `extract` runs, then the resolved rows equal `GROUND-TRUTH.md`.
- Given the sample catalog and repositories are fetched, when `extract` runs on each estate and `KbCompare` runs over every table, then every difference is on the deliberate-differences list, and row counts per table are recorded in Implementation Notes next to the reference's.
- Given that comparison, when the story is closed, then one command builds the whole Knowledge Base. Epic 1 is complete only after story 1.11, the full comparison with the reference, has been confirmed by Kamal.

## Implementation Notes

**What was built.** `extract` now writes `report_lineage.csv`: one row for every report column, saying which presentation column its formula names and, through repository lineage, which logical and physical column, or why there is no link. With this the jar writes every table the reference has. `lineage.csv` is unchanged (a test compares its bytes with and without a catalog).

**Files.**

- `src/main/java/obi2pbi/core/ReportLineage.java` (new): the resolver. No I/O; reads the merged `Estate` only.
- `src/main/java/obi2pbi/core/ReportLineageRow.java` (new): the row, and the names of the `resolution` and `source_state` values.
- `src/main/java/obi2pbi/core/Estate.java`: the `reportLineage` list, in `rowLists()` (so duplicate identifiers are checked, as in the Python) and in `merge`.
- `src/main/java/obi2pbi/core/KbSchema.java`: the `report_lineage` table, with a meaning for every column, the five tables it refers to, and notes on how to read `resolution`, `source_state` and the one-of-several physical column.
- `src/main/java/obi2pbi/cli/ExtractCommand.java`: calls the resolver after the halves are merged and before the uniqueness check, the Python's order. `KnowledgeBaseWriter` needed no change: it writes every table `KbSchema` declares.
- `src/test/java/obi2pbi/ReportLineageTest.java` (new, 10 tests), `src/test/fixtures/linked-estate/` (new fixture: a repository and a catalog that match).
- `src/test/java/obi2pbi/KbCompare.java`, `KbCompareTest.java`: recognise difference 22, and difference 8 where `report_lineage.reference_text` copies a formula (2 tests).
- `src/test/java/obi2pbi/ExtractCommandTest.java`: the list of files a run writes gained `report_lineage.csv`. The test was right for its time; the list is what changed.
- `README.md`, `docs/port-guide/00-read-me-first.md`.

**The lineage-row question (Design Notes).** I checked the Python before changing anything. What I found:

- The choice **is** order-dependent, and it is repeatable. `report_lineage.py` builds `{presentation_column_id: row}` over `estate.lineage`, so the last row wins. `_build_lineage` in `mds_xml.py` appends rows presentation column by presentation column, and for each in the order of `estate.column_mappings`, which is reading order: logical table source files sorted by file name, then mappings in source order. No set or hash order is involved, so the same files always give the same answer.
- The rule that results is "the mapping in the source file whose name sorts last". Those file names are OBIEE's internal ids (`00000297-07f5-0000-714b-e31d00000000.xml`). The Knowledge Base holds neither the file name nor the id, so a reader cannot tell from the tables which row was used, and re-exporting the repository could change it.
- Measured by running the Python's reader on the samples: `sampleapp` has 11 presentation columns with several lineage rows, `bi-analytics-2013` has 36. "Last read" and "first in `lineage.csv` order" give a different physical column for 6 and 2 of them. Example: `Sample Sales.Fact Revenue.Revenue` has four sources; the Python's rule gives `SAMP_REVENUE_A_YR` (the yearly aggregate), the new rule `SAMP_REVENUE_A`. Neither gives the detail table `SAMP_REVENUE_F`.

So it is deterministic but not well-defined in terms of anything the Knowledge Base holds. I therefore did what the Design Notes ask: the first row in `lineage.csv` order (smallest Row identifier, then the rest of the row). It is **deliberate difference 22**, stated in the README and in `schema.csv` (note on `physical_column_id`). If Kamal prefers the Python's rule, the change is one method, `ReportLineage.firstLineageRows`. Neither rule is "the source the BI Server would use"; the README says so.

**Decisions the story did not fix.**

1. *Two names that differ only in letter case.* The Python's lower-cased lookups are plain dictionaries: of two presentation columns (or subject areas) that fold to the same key, the one read last wins, silently. Ported as it is (the fixture has `Region` and `REGION`; the jar and the Python both pick `REGION`). It is reading-order dependent too, but it only matters when the report names neither exactly, and changing it means inventing an `ambiguous` outcome the reference does not have. Listed under "Kept as it is"; see open points.
2. *Lower case* is Java's `toLowerCase(Locale.ROOT)` against Python's `str.lower()`. Equal on everything in the samples and fixtures.
3. *An empty formula* is `not_a_column_reference`, as the Python's condition gives (guide 02 says the same).
4. *`reference_text`* is the jar's own `analysis_columns.formula`, so it carries difference 8: 15 cells on the sample catalog. `KbCompare` explains such a cell only when it equals the formula on both sides and the formula's own difference passes the check for 8. The `resolution` of those 15 rows does not change.
5. *The extra test estate* is a checked-in fixture (`src/test/fixtures/linked-estate`), not built in the test, so that the Python can be run on the same folder.
6. *No error rows.* An unresolved report column is not an error, as in the Python; `errors.csv` and the exit code are unaffected.

**Guide against Python.** Guide 02 sections 4.12 and 6 agree with the Python in everything I could check. The one thing the guide could not say (which lineage row) is settled above.

**Evidence.**

- *Constructed pair* (`build/compare-all/in/constructed-pair`, built by `./build.sh compare-all` from `reference/test-corpus/constructed-cases/`): the jar's 2 `report_lineage` rows equal `GROUND-TRUTH.md` and guide 02 section 6.5 (`Amount`: `exact`, `sourced`, logical and physical filled; `Rolling Amount 3M`: `exact`, `computed_not_sourced`), and equal the Python's output on the same folder cell for cell. Over all 39 tables of that estate the only differences from the Python are differences 8, 9, 10, 16 and 17. Here both lineage rules pick the same row, by coincidence of the file names.
- *Linked estate* (20 report columns): every `resolution` value (`exact` 6, `case_normalised` 3, `unresolved` 4, `not_a_column_reference` 6, `outside_semantic_model` 1) and every `source_state` value (`sourced` 7, `computed_not_sourced` 2, empty 11). Against the Python on the same folder: 17 rows identical, 3 differ in `physical_column_id` only, exactly the three columns that resolve to `Amount`, which has two lineage rows (difference 22: Python `F_AGG.AMOUNT`, jar `F_DETAIL.AMOUNT`).
- *Samples:* `report_lineage` has 11,143 rows on `sampleapp-v607` in both; 11,128 identical, 15 differ in `reference_text` (difference 8); none resolves on either side. 0 rows on the two repository estates in both.

**Row counts per table, reference / new** (from `kb-comparison-report.md`; every difference in count is explained there):

| Table | `sampleapp` reference / new | `bi-analytics-2013` reference / new | `sampleapp-v607` reference / new |
|---|---:|---:|---:|
| `aggregation_rules` | 13 / 13 | 22 / 22 | 0 / 0 |
| `analyses` | 0 / 0 | 0 / 0 | 1,607 / 1,607 |
| `analysis_columns` | 0 / 0 | 0 / 0 | 11,143 / 11,143 |
| `analysis_criteria` | 0 / 0 | 0 / 0 | 1,789 / 1,789 |
| `analysis_filters` | 0 / 0 | 0 / 0 | 7,135 / 8,495 |
| `analysis_prompts` | 0 / 0 | 0 / 0 | 4 / 4 |
| `analysis_views` | 0 / 0 | 0 / 0 | 10,879 / 10,879 |
| `bmm_joins` | 9 / 9 | 75 / 75 | 0 / 0 |
| `catalog_versions` | 0 / 0 | 0 / 0 | 60 / 60 |
| `coverage` | 30 / 29 | 28 / 27 | 65 / 65 |
| `coverage_summary` | 0 / 0 | 0 / 0 | 5 / 5 |
| `dashboard_contents` | 0 / 0 | 0 / 0 | 3,718 / 3,718 |
| `dashboard_pages` | 0 / 0 | 0 / 0 | 1,036 / 1,036 |
| `dashboards` | 0 / 0 | 0 / 0 | 111 / 111 |
| `errors` | 2 / 0 | 2 / 0 | 1,552 / 1,185 |
| `init_blocks` | 2 / 2 | 152 / 152 | 0 / 0 |
| `lineage` | 72 / 72 | 1,501 / 1,501 | 0 / 0 |
| `logical_columns` | 77 / 77 | 933 / 933 | 0 / 0 |
| `logical_dimensions` | 5 / 5 | 20 / 20 | 0 / 0 |
| `logical_levels` | 22 / 22 | 84 / 84 | 0 / 0 |
| `logical_table_sources` | 17 / 17 | 75 / 75 | 0 / 0 |
| `logical_tables` | 11 / 11 | 71 / 71 | 0 / 0 |
| `lts_column_map` | 95 / 95 | 940 / 940 | 0 / 0 |
| `manifest` | 7 / 7 | 7 / 7 | 7 / 7 |
| `physical_columns` | 139 / 139 | 957 / 957 | 0 / 0 |
| `physical_joins` | 22 / 22 | 82 / 82 | 0 / 0 |
| `physical_table_definitions` | 12 / 12 | 32 / 32 | 0 / 0 |
| `physical_tables` | 22 / 22 | 78 / 78 | 0 / 0 |
| `presentation_columns` | 59 / 59 | 1,484 / 1,484 | 0 / 0 |
| `presentation_hierarchies` | 5 / 5 | 53 / 53 | 0 / 0 |
| `presentation_level_columns` | 16 / 16 | 183 / 183 | 0 / 0 |
| `presentation_levels` | 20 / 20 | 226 / 226 | 0 / 0 |
| `presentation_subject_areas` | 3 / 3 | 18 / 18 | 0 / 0 |
| `presentation_tables` | 10 / 10 | 118 / 118 | 0 / 0 |
| `report_lineage` | 0 / 0 | 0 / 0 | 11,143 / 11,143 |
| `saved_filters` | 0 / 0 | 0 / 0 | 96 / 96 |
| `security_rls` | 9 / 9 | 189 / 189 | 0 / 0 |
| `unparsed` | 146 / 146 | 238 / 239 | 1,050 / 1,053 |
| `variables` | 2 / 2 | 343 / 343 | 0 / 0 |

The jar also writes `schema` (327 rows) and `schema_tables` (41 rows), which the reference does not have (difference 16).

**Surprises.** The reference's own hand-made pair cannot tell the two lineage rules apart, though `Amount` has two sources there: the file names happen to put the same source last that sorts first. The fixture for this story names its files the other way round on purpose.

**Not done.** Nothing in the story is left out. The acceptance criterion's last sentence (Epic 1 complete only after Kamal confirms story 1.11) is not mine to meet.

**Open points.**

1. Difference 22 needs Kamal's confirmation.
2. Should two presentation columns that differ only in case be reported as ambiguous rather than the last one taken (decision 1)? Ported as it is for now.
3. Linking is still proven only on hand-made data.

## Spec Change Log

## Review Triage Log

## Design Notes

Guide 02 could not determine which repository lineage row the reference picks when a presentation column has several: it takes the last in an in-memory list. Do not copy an accident. Pick the first row in `lineage.csv` order (sorted by Row identifier), state it in the README, and add it to the deliberate-differences list.

Linking has only ever succeeded on the hand-made pair. Say so in the README; it stays unproven until an estate with both halves from one OBIEE instance is available.

## Verification

**Commands:**

- `./build.sh` -- expected: the jar is built at `build/obi2pbi.jar` and every test passes
- `java -jar build/obi2pbi.jar extract <estate with both halves>` -- expected: writes all tables; prints counts
