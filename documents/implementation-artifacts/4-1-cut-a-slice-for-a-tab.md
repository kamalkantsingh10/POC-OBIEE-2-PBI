---
title: 'Cut a slice for a tab'
type: 'feature'
created: '2026-11-23'
status: 'done'
route: ''
review_loop_iteration: 0
context:
  - '{project-root}/documents/planning-artifacts/architecture/architecture-POC-OBIEE-2-PBI-2026-11-23/ARCHITECTURE-SPINE.md'
  - '{project-root}/documents/planning-artifacts/prds/prd-POC-OBIEE-2-PBI-2026-11-23/prd.md'
---

# Story 4.1: Cut a slice for a tab

<frozen-after-approval reason="human-owned intent — do not modify unless human renegotiates">

## Intent

**Problem:** The to-be skill needs the Knowledge Base rows behind one tab's confirmed matches, and it needs to know which subject areas the tab touches.

**Approach:** Extend `slice` to start from a tab's confirmed decisions, using the same walk as story 2.1.

## Boundaries & Constraints

**Always:**

- Only rows with decision `confirmed` contribute (FR-12). The column used is the decision's `row_id` when given, otherwise the proposed one.
- The Slice follows AD-9, exactly as a subject-area Slice does, including `units.csv` and packets (AD-17).
- Java 11, standard library only; no AI.

**Never:**

- Changing the walk from story 2.1.
- Writing or editing `decisions.csv`.

## I/O & Edge-Case Matrix

| Scenario | Input / State | Expected Output / Behavior | Error Handling |
|----------|--------------|---------------------------|----------------|
| Happy path | `slice kb/<estate> --tab work/<tab>` with confirmed rows | `work/<tab>/slice/` with the rows reachable from the confirmed columns | N/A |
| Rejected or undecided | Items not confirmed | Contribute nothing; the run prints how many | N/A |
| Subject areas touched | Confirmed columns from two subject areas | The run prints both, and for each whether `as-is/<estate>/<subject-area>.md` exists | N/A |
| Nothing confirmed | `decisions.csv` has no confirmed row, or is missing | Says so; writes nothing | Exit 2 |
| Unknown identifier | A confirmed `row_id` not in the Knowledge Base | Says which; writes nothing | Exit 3 |

</frozen-after-approval>

## Code Map

- `src/main/java/obi2pbi/core/Slice.java` -- the walk from story 2.1
- `documents/planning-artifacts/architecture/architecture-POC-OBIEE-2-PBI-2026-11-23/ARCHITECTURE-SPINE.md` -- AD-9, AD-11

## Tasks & Acceptance

**Execution:**

- [x] `src/main/java/obi2pbi/core/Slice.java` -- accept a starting set built from decisions
- [x] `src/main/java/obi2pbi/cli/` -- the `--tab` option; print subject areas touched and whether each As-is document exists
- [x] `src/test/java/obi2pbi/` -- tests for the matrix rows

**Acceptance Criteria:**

- Given confirmed decisions on the constructed estate, when `slice --tab` runs, then every reference inside the slice resolves inside the slice.
- Given the same decisions, when the slice is compared with the subject-area slice, then it is a subset of it.

## Implementation Notes

**Built.** `java -jar build/obi2pbi.jar slice kb/<estate> --tab work/<tab> [--out] [--timestamp] [--max-packet-lines]` writes `work/<tab>/slice/`, a Slice with the same files and rules as a subject-area Slice (AD-9, AD-17), plus a table `tab_fields` and a unit `tab-fields`.

**Files.** `core/Slice.java` (`cutForTab`; the walk split into `walkRepository` -> `walkFromPresentation` and `walkCatalog` -> `walkReports`, and the common tail `finish`, so the subject-area walk is unchanged: every existing test and the re-cut subject-area Slices are identical apart from the gaps fix); `core/TabDecisions.java` (decisions to starting columns and `tab_fields` rows); `core/SliceSchema.java` (`tab_fields` declared, manifest keys described); `core/Packets.java` (tab overview, `tab-fields` unit, tab wording in the logical-table units and the packet header); `cli/SliceCommand.java` (`--tab`); `src/test/java/obi2pbi/TabSliceTest.java` (6 tests: the matrix rows, the two acceptance criteria on the constructed pair, rerun, and the test tab on `kb/sampleapp`). Documented in `README.md` ("Cut a Slice for a tab") and `docs/skill-conventions.md` section 10.10.

**Decisions.**
- Starting set: a confirmed presentation column brings its presentation table and subject area (so references resolve), not the table's other columns; a logical column starts at the logical layer; a report column brings its report, whole, and the presentation column `report_lineage` links it to. From there the subject-area walk runs unchanged (logical tables whole, physical tables whole, joins, hierarchies of the presentation tables touched, variables, security, references closed). The one difference: a subject area's implicit fact column is not a starting point, since the tab did not ask for it.
- Column of a decision: its `row_id`, or the item's one proposed column when empty. Rejected, unmatched and undecided items contribute nothing and are counted and listed.
- `tab_fields` (row_id `item::<item_id>`, citable like any row) holds every FSD item that needs data with `status` confirmed / rejected / unmatched / undecided, the confirmed columns and the distinct decision notes; each row is a required row, so a to-be document accounts for every item.
- Units: `overview` (tab, subject areas, presentation tables with their confirmed columns, counts), `tab-fields`, one per logical table touched, one per physical table, `joins`, `hierarchies`, `variables`, `security`, `reports`, `gaps`. Required rows: those of a subject-area Slice for the rows held (presentation columns only where confirmed) plus every `tab_fields` row.
- Exit codes: 2 when there is no `decisions.csv` or nothing is confirmed; 3, nothing written, when a confirmed identifier is unknown, a confirmed decision names no column and the item has not exactly one proposal, or a decision names an item not in `fsd_items.csv`.
- As-is documents: looked for at `as-is/<estate>/<subject-area folder name>.md` relative to the current folder; the first line (status) is printed when it exists.

**Results.** Constructed pair: the tab Slice is a subset of the subject-area Slice, table by table, and every reference resolves inside it. `slice kb/sampleapp --tab work/test-tab` (TEST decisions): 27 decisions (26 confirmed, 1 rejected), 21 items (18 confirmed, 1 rejected, 2 unmatched), 11 confirmed columns, subject area Sample Sales with its reviewed as-is document, 606 rows in 41 tables, 30 units, largest 120 lines (`table-01-fact-revenue-p1`), 256 required rows, 0 errors.

**What a to-be writer still lacks from the Slice.** The FSD's own calculations (net revenue = revenue - discount, ratios, average price) are not in OBIEE; the Slice gives the parts only. The unmatched items (region, active customers) and the rejected security item have no rows. `sampleapp` has no catalog, so no existing report formula can be compared. Data volumes and values are not in the Knowledge Base.

**Not done.** Nothing in the story's tasks.

## Spec Change Log

## Review Triage Log

## Verification

**Commands:**

- `./build.sh` -- expected: the jar is built at `build/obi2pbi.jar` and every test passes
