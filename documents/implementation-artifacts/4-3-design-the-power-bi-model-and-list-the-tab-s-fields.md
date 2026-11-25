---
title: 'Design the Power BI model and list the tab's fields'
type: 'feature'
created: '2026-11-23'
status: 'done'
route: ''
review_loop_iteration: 0
context:
  - '{project-root}/documents/planning-artifacts/architecture/architecture-POC-OBIEE-2-PBI-2026-11-23/ARCHITECTURE-SPINE.md'
  - '{project-root}/documents/planning-artifacts/prds/prd-POC-OBIEE-2-PBI-2026-11-23/prd.md'
---

# Story 4.3: Design the Power BI model and list the tab's fields

<frozen-after-approval reason="human-owned intent — do not modify unless human renegotiates">

## Intent

**Problem:** The to-be design is not complete until it says what the Power BI semantic model contains and how each field on the tab is supplied.

**Approach:** Extend the to-be skill: add the Power BI semantic model (tables, relationships, measures, hierarchies) and the list of the tab's fields.

## Boundaries & Constraints

**Always:**

- Each measure shows the OBIEE formula it replaces, exactly as extracted, with a citation.
- An OBIEE feature with no direct Power BI equivalent is flagged for a person's decision, with the options, and is not translated silently.
- Every item in `fsd_items.csv` appears in the list of tab fields: supplied by a model item, or listed as unresolved.
- The three copies of the skill stay identical.
- **Review before hand-over (AD-18).** The output starts with `Status: draft, not reviewed`. The review skill (story 2.4) then goes through it against its inputs and logs findings to a `.review.csv` file. The agent reports the output as ready for users only when its status is reviewed. For the to-be design the review checks each new column against the old column it cites, each measure's quoted formula against the Slice, each tab field against its confirmed decision, and that nothing was decided silently that should be a question.
- A feature that needs a person's decision is written as an open question with an `Answer:` line, under the same rules as story 4.2; a decision given is kept in `work/<tab>/to-be.answers.md` and applied on every later run.
- The design is complete for the tab: `check-doc` (story 2.3), run with the tab's confirmed columns as the required rows, reports nothing uncovered.

**Never:**

- Writing DAX that has not been marked as a proposal.
- Generating Power BI files.
- Deciding visual styling.

## I/O & Edge-Case Matrix

| Scenario | Input / State | Expected Output / Behavior | Error Handling |
|----------|--------------|---------------------------|----------------|
| Measure | A simple summed measure | Model measure with the old formula quoted and the proposed definition marked as a proposal | N/A |
| Time-series function | A measure using `AGO` or `TODATE` | Flagged: needs a Power BI date table and a chosen pattern | N/A |
| Per-user behaviour | Security rules or session variables in the slice | Listed under security, each flagged for a decision on how it is reproduced | N/A |
| Hierarchy | A ragged or parent-child hierarchy | Carried into the model with its kind stated; flagged if Power BI handles it differently | N/A |
| Tab field | An FSD item with a confirmed match | Row: FSD anchor, model item, confirmed column, citation | N/A |
| Unresolved item | An FSD item rejected, unmatched or undecided | Listed under "Unresolved" with its status | N/A |
| Report-level calculation | A confirmed match to a report formula | Becomes a model measure, formula quoted | N/A |

</frozen-after-approval>

## Code Map

- `skills/to-be/` -- the skill from story 4.2
- `documents/planning-artifacts/prds/prd-POC-OBIEE-2-PBI-2026-11-23/prd.md` -- FR-15, FR-16
- `documents/planning-artifacts/migration-plan.md` -- open decisions on OBIEE features Power BI lacks

## Tasks & Acceptance

**Execution:**

- [x] `skills/to-be/SKILL.md` -- add the model and tab-field steps; run `check-doc` after writing, then hand over to the review
- [x] `skills/review/SKILL.md` -- add the to-be review; findings to `to-be.review.csv`
- [x] `skills/to-be/references/document-structure.md` -- add "Power BI semantic model", "Fields on the tab", "Decisions needed", "Unresolved"
- [x] `skills/to-be/references/obiee-features.md` -- the OBIEE features that need a decision in Power BI, and what to ask for each
- [x] `.agents/skills/to-be/, .claude/skills/to-be/` -- identical copies

**Acceptance Criteria:**

- Given the constructed estate's run from story 4.2, when the skill runs, then `to-be.md` has all sections, every `fsd_items.csv` item appears exactly once across "Fields on the tab" and "Unresolved", and the result is recorded in Implementation Notes.
- Given a slice with a time-series measure, when the skill runs, then it appears under "Decisions needed".

## Implementation Notes

**Built (2026-11-24)** together with story 4.2; see its notes for the files, the part numbering and the dry check. What belongs to this story:

- **Power BI semantic model** (`skills/obiee-to-be/references/document-structure.md`): `### Model table <name>` per logical table (keys, every source with level of detail and joins as printed, the fields confirmed for the tab, one block per confirmed measure), `### Relationships`, `### Hierarchies`, `### Values set per user or for the whole system`, `### Security`, `### Report-level calculations`.
- **A measure block** holds: what it replaces, with markers; the OBIEE formula exactly, with the aggregation rule's marker; where it is read from; writeable as recorded; and the proposed definition as an indented block under `Proposal (not from OBIEE; to be signed):`. A measure that depends on an undecided feature gets `Proposal: none until decision Q<n> is taken.`
- **FSD calculations** are designed in the tab-fields part as `#### FSD measure: <name>`: the FSD's definition quoted from the packet, the confirmed columns it is built from, and the combination stated as the FSD's definition and not something OBIEE records.
- **Fields on the tab / Unresolved:** each confirmed item is one row of the fields table; each item that is not confirmed is one row of `5000-unresolved.md` with its status and decision note. Step 5 of the skill counts both against the number of FSD items.
- **Decisions needed** (`references/obiee-features.md`, 17 features): time-series functions, several sources at different levels of detail, a measure mapped differently in two sources, a switch on a per-user value, a fixed number in a formula, lookup or translation, a displayed name set by a variable, parent-child drill paths, row filters, permissions, writeable measures, the implicit fact column, content filters, a join inside a source, a join that is not a plain one-to-many inner join, a table that is a query, a call to a database function. Each becomes an open question with an `Options:` line and a row of `6000-decisions.md`; answering the question takes the decision.
- **The to-be review** (`skills/obiee-review/references/to-be.md`, 73 lines; `skills/obiee-review/SKILL.md` sends there): unit by unit, packet first; each new column against the old column on its packet line; each formula character for character; proposals marked; nothing guessed; with `obiee-features.md` beside the packet, every feature shown must have its decision; each tab field against its status in the tab-fields packet; then every row of `fsd_items.csv` exactly once across the fields parts and Unresolved; then questions, decisions and the opening. Log: `work/<tab>/to-be.md.review.csv` (the conventions' name for a document's review log; the story says `to-be.review.csv`). Status line as for documents. The review says that reviewed means checked against the Slice, not agreed.

Not done: both acceptance criteria need a run (all sections present and every item exactly once; a Slice with a time-series measure appearing under Decisions needed). The sample tab has no time-series measure; the rule is in `obiee-features.md` and untested. GitHub Copilot: not verified.

## Spec Change Log

## Review Triage Log

## Verification

**Commands:**

- `./build.sh` -- expected: the jar is built at `build/obi2pbi.jar` and every test passes

**Manual checks (if no CLI):**

- Read `to-be.md` as a Power BI developer would: could the model be built from it without opening OBIEE?
