---
title: 'Confirm the Knowledge Base matches the reference'
type: 'feature'
created: '2026-11-23'
status: 'in-review'
route: ''
review_loop_iteration: 0
context:
  - '{project-root}/docs/port-guide/00-read-me-first.md'
  - '{project-root}/docs/port-guide/01-repository-reader.md'
  - '{project-root}/docs/port-guide/02-catalog-reader.md'
  - '{project-root}/docs/port-guide/03-core-output-slice.md'
  - '{project-root}/documents/planning-artifacts/architecture/architecture-POC-OBIEE-2-PBI-2026-11-23/ARCHITECTURE-SPINE.md'
---

# Story 1.11: Confirm the Knowledge Base matches the reference

<frozen-after-approval reason="human-owned intent — do not modify unless human renegotiates">

## Intent

**Problem:** Epic 1 is a port. It is only finished when the Knowledge Base the jar produces matches the one the first attempt produced from the same input, and Kamal has seen the evidence.

**Approach:** Fetch the same sample input the first attempt used, run `extract`, compare every table with `reference/knowledge-base/`, and write a comparison report for Kamal to confirm.

## Boundaries & Constraints

**Always:**

- The input is the same as the first attempt's: the two sample repositories and the sample catalog at the versions pinned in `reference/test-corpus/corpus-manifest.csv`.
- Every table in `reference/knowledge-base/` is compared, row by row and cell by cell, ignoring only the `_guarded` columns.
- Every difference is assigned to one numbered entry of the deliberate-differences list in `docs/port-guide/00-read-me-first.md`, or it is a defect.
- A defect is fixed in the jar, or, if the reference is wrong, added to the list with the reason. It is never waved through.
- Epic 1 is not marked done until Kamal has read the report and confirmed.

**Never:**

- Editing files under `reference/`.
- Changing the comparison to hide a difference.
- Declaring a match on row counts alone.

## I/O & Edge-Case Matrix

| Scenario | Input / State | Expected Output / Behavior | Error Handling |
|----------|--------------|---------------------------|----------------|
| Table identical | A table with no differences | Report line: rows compared, 0 differences | N/A |
| Explained difference | Rows differ because of a listed difference (for example reference lists now one per line) | Counted under that difference's number, with three example rows | N/A |
| Unexplained difference | A cell differs for no listed reason | Listed as a defect with table, Row identifier, column, both values | Story stays open |
| Row only in one side | A row in the reference missing from the new output, or the reverse | Listed with its Row identifier; explained or a defect | Story stays open if unexplained |
| Table only in one side | A table the reference has and the jar does not write, or the reverse | Listed; explained or a defect | Story stays open if unexplained |
| Input cannot be fetched | A pinned source is no longer available | Recorded; the comparison runs on what was fetched and the report says which tables could not be confirmed | N/A |

</frozen-after-approval>

## Code Map

- `reference/knowledge-base/` -- the expected output: 39 tables
- `reference/test-corpus/corpus.md, corpus-manifest.csv` -- where the two sample repositories come from and the pinned versions
- `docs/formats/web-catalog-findings.md, reference/processed-inputs/README.md, inputs-manifest.csv` -- where the sample catalog comes from and what the extractor was run against
- `docs/port-guide/00-read-me-first.md` -- the deliberate-differences list
- `src/test/java/obi2pbi/KbCompare.java` -- the comparison helper built up through stories 1.1 to 1.10

## Tasks & Acceptance

**Execution:**

- [x] `estates/` -- fetch the sample repositories and catalog into estate folders named as the reference names them
- [x] `src/test/java/obi2pbi/KbCompare.java` -- extend to a full run: every table, every cell, differences grouped by the numbered entry that explains them
- [x] `documents/implementation-artifacts/kb-comparison-report.md` -- the report: per table, rows in reference, rows in new output, identical rows, differences by numbered entry with examples, defects
- [x] `docs/port-guide/00-read-me-first.md` -- add any difference found to be justified, numbered, with the reason
- [x] `README.md` -- how to re-run the comparison

**Acceptance Criteria:**

- Given the fetched samples, when `extract` and the comparison run, then the report covers every table in `reference/knowledge-base/`.
- Given the report, when it is finished, then it lists zero unexplained differences, or each remaining one is stated plainly as an open defect.
- Given the report, when Kamal has read it, then his confirmation is recorded in Implementation Notes, and only then is Epic 1 marked done.

## Implementation Notes

**CONFIRMATION BY KAMAL: NOT YET GIVEN.** This line is for Kamal to change after reading `documents/implementation-artifacts/kb-comparison-report.md`. Epic 1 is not marked done, and nothing in this story says it is.

**Result.** `./build.sh compare-all`, run on 23 November 2026: 3 estates compared, 39 reference tables each (117 table comparisons), 60,138 reference rows against 61,129 generated rows, 59,137 rows identical, **0 unexplained differences**. Every difference is assigned to one of differences 1, 5, 6, 7, 8, 9, 10, 11, 15, 16, 17, 19 and 21, each checked cell by cell or row by row. On the two hand-made estates with both halves, compared against the Python run on the same folders: 0 unexplained; difference 22 accounts for 3 cells.

**What was built.**

- `./build.sh compare-all` (and `build.cmd compare-all`): compiles, runs the jar's `extract` on the three sample estates, compares, and writes the report. Exit code 0 only when all three estates were compared and nothing is unexplained.
- `src/test/java/obi2pbi/KbCompareReport.java` (new): runs the steps and writes the report. It decides nothing about a difference itself, except the manifest's keys, whose rules are written out in `compareManifest`.
- `src/test/java/obi2pbi/KbCompare.java`: each comparison now also returns its differences as data (`Result.changes`, with the difference number, kind, row, column and both values) and counts identical rows, rows that differ only as explained, and rows with unexplained cells. No check was loosened; the console output of `./build.sh compare` is as before.
- `src/test/java/obi2pbi/KbCompareReportTest.java` (new, 3 tests): how values are shown, and that no value of the `variables` table, and nothing of a difference-19 row but its identifier, is ever printed.
- `documents/implementation-artifacts/kb-comparison-report.md`: written by the command. Do not edit by hand.
- `README.md`: the full table list (41), how to lay out an estate folder, how to re-run the comparison.
- `docs/port-guide/00-read-me-first.md`: the list reviewed; see below.

**Tasks as written, and what was done.**

- *Fetch the samples into `estates/`:* they were already there, as links to the copies on this machine, and I did not change them (the build rules forbid editing `estates/`). Checked: 131 XML files for `sampleapp`, 1,252 for `bi-analytics-2013`, 5,371 files for the catalog, as the reference counts them; the catalog checkout is at the pinned commit `5266344c...`. The two repository folders are plain copies, not git checkouts, so their commit cannot be read from them; that every repository table compares equal is the evidence that they are the pinned input.
- *Extend `KbCompare` to a full run:* done as `KbCompareReport` on top of it.
- *The report, the differences list, the README:* done.

**The manifest.** Compared key by key for each estate. `halves_present:<estate>` and `pairing:<estate>` are identical. `extractor_version` and `corpus_root` differ (17), `rpd_ids` names one estate where the reference names three (15), `tables` is the reference's list plus `schema` and `schema_tables` (16). `extracted_at` is the time of the run; it is reported as "not comparable" and counted neither as identical nor as a difference. On an estate with both halves the pairing sentence differs in wording ("one rpd-id (AD-22)" against "one estate name (AD-7)"); I widened the text of difference 17 to say so exactly, since it only said "cites AD-7".

**Review of the differences list.**

- Every number used in the code, the tests and the comparison (3, 5, 6, 8, 9, 10, 11, 19, 20, 21, 22 in `src/main`; all of 1 to 22 in the guide) is in the list with old behaviour, new behaviour and why.
- The table now has a Status column. 1 to 13: confirmed by Kamal, 23 November 2026. 14 to 22: awaiting confirmation, each with the story that added it. The "not yet confirmed" remarks that sat inside the Why column were moved there, unchanged in substance.
- A blank line inside the table (between 19 and 20) cut it in two when rendered; removed.
- 17 reworded (above). 22 added (story 1.10). Three "kept as it is" entries added for report lineage.
- Not found: any behaviour change in the code that the list lacks. I looked for them by reading the Implementation Notes of stories 1.1 to 1.9 and by the comparison itself, which finds nothing unexplained. A change that the samples do not exercise and that no note mentions would escape both; see open points.

**Decisions the story did not fix.**

1. *Totals count the manifest as a table* (39 = 38 tables with rows per estate, plus the manifest), since the task asks for all 39.
2. *Difference 1 is reported apart.* The 108 guard columns are left out before comparing, so "identical" means identical in the columns both sides have. The report says so and counts the guard columns and the 1,403 cells that carried the apostrophe.
3. *The hand-made estates are in the report* (section 5) though the story names only the samples, because report lineage resolves nowhere else. They need Python on the machine; without it the section says "not compared" and the sample totals are unaffected. Their unexplained count also sets the exit code.
4. *A missing sample estate* makes the run exit 1 and is reported as "not confirmed" (the matrix row "Input cannot be fetched"); the others are still compared.
5. *Examples never show a value from `variables`*, whatever the difference, and for difference 19 only the row identifier. The reference file itself still holds that value; this story does not touch `reference/`.
6. *The report carries the date it was written*, so it is not byte-for-byte repeatable from one day to the next. Everything else in it is.

**Surprises.** None in the tables: the three sample comparisons were already clean per table after story 1.9, and `report_lineage` added only the 15 copied formulas. The one new unexplained difference this story met was the pairing sentence, on the hand-made estates only.

**Not done.**

- Kamal's confirmation is not recorded, because it has not been given. Epic 1 is not marked done; `sprint-status.yaml` is untouched.
- `build.cmd compare-all` was written and not run: there is no Windows machine here.

**Open points.**

1. Differences 14 to 22 await Kamal's confirmation; 22 is new in this pair of stories.
2. The comparison proves agreement with the Python, not correctness about OBIEE, and report lineage only on hand-made data. The report's last section lists the limits.
3. The review of the differences list rests on the stories' notes and on the comparison. A full read of the Java against the Python for behaviour the samples never exercise was not done.

**Commands.**

```
./build.sh                 # 153 tests pass
./build.sh compare-all     # writes the report; prints: unexplained differences: 0
```

## Spec Change Log

## Review Triage Log

## Design Notes

The reference Knowledge Base was produced by code that was never run during this port, so this comparison is the only real test that the Java reader and the guides describe the same thing. Expect it to find mistakes in the guides as well as in the Java.

A first pass on row counts and Row identifiers finds most problems quickly; the cell-by-cell pass then runs only on rows present in both.

## Verification

**Commands:**

- `./build.sh` -- expected: the jar is built at `build/obi2pbi.jar` and every test passes
- `./build.sh compare` -- expected: writes `documents/implementation-artifacts/kb-comparison-report.md` and prints the number of unexplained differences
