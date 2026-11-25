---
title: 'Check the matches'
type: 'feature'
created: '2026-11-23'
status: 'done'
route: ''
review_loop_iteration: 0
context:
  - '{project-root}/documents/planning-artifacts/architecture/architecture-POC-OBIEE-2-PBI-2026-11-23/ARCHITECTURE-SPINE.md'
  - '{project-root}/documents/planning-artifacts/prds/prd-POC-OBIEE-2-PBI-2026-11-23/prd.md'
---

# Story 3.3: Check the matches

<frozen-after-approval reason="human-owned intent — do not modify unless human renegotiates">

## Intent

**Problem:** A proposed match can be wrong in ways a program can catch: an identifier that does not exist, a column that leads to no database table, an FSD item left out. The Operator's time should go on meaning, not on those.

**Approach:** Add the `check-matches` command: it reads the trace skill's files and the Knowledge Base and writes `check.csv`, one row per finding.

## Boundaries & Constraints

**Always:**

- The checks are mechanical and need no AI. The check logic lives in `core`.
- `check-matches` writes `check.csv` only. It never edits `matches.csv` or `decisions.csv` (AD-2).
- A failure sets exit code 3; a warning does not (AD-14).
- Java 11, standard library only; all CSV through the one writer.

**Never:**

- Judging whether a match means the right thing.
- Comparing data values.

## I/O & Edge-Case Matrix

| Scenario | Input / State | Expected Output / Behavior | Error Handling |
|----------|--------------|---------------------------|----------------|
| Unknown identifier | `row_id` not in the Knowledge Base | Failure `unknown_row` | Exit 3 |
| No database table | The column's lineage reaches no physical table | Failure `no_physical_source`, unless lineage marks it as a derived column, which is a warning | Exit 3 / none |
| Item with no row | An item in `fsd_items.csv` missing from `matches.csv` | Failure `item_without_match` | Exit 3 |
| Bad anchor | An anchor not present in `fsd.md` | Failure `unknown_anchor` | Exit 3 |
| Bad values | `confidence` outside 0-100, unknown `status`, `proposed` with empty `row_id` | Failure `bad_value` | Exit 3 |
| Disagrees with existing report | The FSD names the page it replaces; a match's column is not among that page's report columns | Warning `differs_from_existing_report`, naming the report column | None |
| No report tables | Knowledge Base built without a catalog | Runs; prints that no existing report was available | None |
| Decisions present | `decisions.csv` exists | Also reports unknown `item_id`, unknown `row_id`, and values other than `confirmed` / `rejected` | Exit 3 |
| Clean | Nothing wrong | `check.csv` holds only its header | Exit 0 |

</frozen-after-approval>

## Code Map

- `documents/planning-artifacts/architecture/architecture-POC-OBIEE-2-PBI-2026-11-23/ARCHITECTURE-SPINE.md` -- AD-10, AD-11, AD-14
- `src/main/java/obi2pbi/in/kb/` -- the Knowledge Base reader from story 2.1
- `kb/<estate>/lineage.csv, report_lineage.csv, dashboard_contents.csv` -- what the checks read

## Tasks & Acceptance

**Execution:**

- [x] `src/main/java/obi2pbi/core/MatchCheck.java` -- the checks, each returning findings
- [x] `src/main/java/obi2pbi/cli/` -- the `check-matches` command: `check-matches work/<tab> --kb kb/<estate>`
- [x] `src/test/java/obi2pbi/` -- one test per matrix row, on small files written in the test
- [x] `README.md` -- how to record decisions: the columns of `decisions.csv` and an example

**Acceptance Criteria:**

- Given the trace skill's output from story 3.2, when `check-matches` runs, then it completes and its findings are recorded in Implementation Notes.
- Given `matches.csv` with one fault of each kind, when the tests run, then each fault yields exactly its finding.

## Implementation Notes

**Built.** The `check-matches` command: `java -jar build/obi2pbi.jar check-matches work/<tab> --kb kb/<estate>` reads `fsd.md`, `fsd_items.csv`, `matches.csv`, `decisions.csv` when present, and the Knowledge Base, and writes `work/<tab>/check.csv` only. Exit 0 no failure, 3 failures, 2 missing input or wrong usage.

**Files.**
- `src/main/java/obi2pbi/core/MatchSchema.java`: the columns, meanings and allowed values of `fsd_items`, `matches`, `decisions` and `check`, declared once (no Knowledge Base table was added, so `KbSchema` is unchanged).
- `src/main/java/obi2pbi/core/MatchCheck.java`: the checks; `src/main/java/obi2pbi/cli/CheckMatchesCommand.java`, registered in `Main.COMMANDS`.
- `docs/skill-conventions.md`, new section 10: the files of a tab, reading `fsd.md` and its sections, `find-columns`, the exact columns and values of the four CSV files, the confidence bands, how an FSD item names the page it replaces, the findings. `README.md`: the three commands and how to record decisions, with an example.
- `src/test/fixtures/matches/`: a hand-made `fsd.md`, `fsd_items.csv`, `matches.csv`, `decisions.csv` with one fault of each kind, and `expected-check.csv` (11 rows, one per fault).
- `src/test/java/obi2pbi/CheckMatchesTest.java`: 13 tests, one per matrix row, the fixture set, the files left unchanged, wrong usage, and a test that section 10 names every declared column and value. The Knowledge Base is `src/test/fixtures/linked-estate` with a dashboard page added in the test (it places the report "every outcome").

**Decisions.**
- `fsd_items.csv` columns: `item_id, kind, fsd_anchor, fsd_text, existing_id`; `kind` is `measure`, `attribute`, `filter`, `calculation` or `replaces`. The page the tab replaces (Design Notes: "a line in fsd_items.csv") is a row of kind `replaces` whose `existing_id` is the Row identifier of a dashboard page, a dashboard or a report. Such a row needs no match.
- `matches.csv` may hold several rows for one item (an item computed from two columns). `row_id` may be a presentation, logical or report column. `bad_value` also covers: a proposed row below 30 or an unmatched row at 30 or more (the bands), an empty basis or anchor, a repeated item or column, an item both proposed and unmatched.
- `check.csv` columns: `finding, severity, file, row, item_id, row_id, detail`; rows sorted by file (items, matches, decisions) and row. A `severity` column makes warnings visible without a lookup.
- "Lineage marks it as a derived column" is read as: a logical column with a derived formula and no table source mapping it, or a report column that is a calculation (`is_computation`, `computed_not_sourced`, `not_a_column_reference`). That is the warning `derived_column`; its detail says whether each column the formula refers to reaches the database. A column with no logical column, or a mapping that reads no database column, is `no_physical_source`.
- `differs_from_existing_report` "naming the report column": the detail names the page's report columns whose heading or formula shares a word with the item's text (up to three), or says none does. A match agrees when its column is a report column of the page, or the presentation or logical column one is linked to.
- Decisions: empty `row_id` means the item's one proposal (more than one is `bad_value`); a confirmed column that was not proposed is a correction and is itself checked (exists, reaches the database); a rejection of a column that was not proposed is `bad_value`.
- A file missing a contract column stops the run with exit 2; an extra column is named in a note and ignored.

**Findings on the trace skill's output from story 3.2.** Not available: the trace skill has not been written yet, so this acceptance check is open. On the hand-made set the run finds exactly the 11 planted faults.

**Not done.** The 3.2 acceptance check above.

## Spec Change Log

## Review Triage Log

## Design Notes

`decisions.csv` columns are `item_id, decision, row_id, note` (AD-11). `row_id` lets the Operator point an item at a different column than the one proposed. The Operator creates the file; nothing generates it, so that no program ever writes a decision.

The page an FSD replaces is found from a line in `fsd_items.csv` the trace skill writes when the FSD names one. If there is none, the existing-report check is skipped and the run says so.

## Verification

**Commands:**

- `./build.sh` -- expected: the jar is built at `build/obi2pbi.jar` and every test passes
- `java -jar build/obi2pbi.jar check-matches work/<tab> --kb kb/<estate>` -- expected: writes `work/<tab>/check.csv`; exit 0 or 3
