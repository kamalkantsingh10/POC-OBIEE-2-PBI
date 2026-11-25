---
title: 'Design the new tables and views with the to-be skill'
type: 'feature'
created: '2026-11-23'
status: 'done'
route: ''
review_loop_iteration: 0
context:
  - '{project-root}/documents/planning-artifacts/architecture/architecture-POC-OBIEE-2-PBI-2026-11-23/ARCHITECTURE-SPINE.md'
  - '{project-root}/documents/planning-artifacts/prds/prd-POC-OBIEE-2-PBI-2026-11-23/prd.md'
---

# Story 4.2: Design the new tables and views with the to-be skill

<frozen-after-approval reason="human-owned intent — do not modify unless human renegotiates">

## Intent

**Problem:** Data Engineering has to build new tables and views for the tab. Today nobody gives them a starting point tied to what the old report used.

**Approach:** Write the to-be skill, first part: from the tab's Slice, the confirmed decisions and the As-is documents, set out the new tables and views with their columns and the old column each comes from.

## Boundaries & Constraints

**Always:**

- **Agent.** This skill is a menu item of `obiee-migration-agent` (created in story 2.2), so it can be started from GitHub Copilot and from Claude. This story adds "Write the to-be design".
- **Open questions.** Wherever the AI is unsure or wants more information, it writes a numbered question with why it matters, how to get the answer, and an empty `Answer:` line. Answers are the user's, are kept in an answers file `work/<tab>/to-be.answers.md`, and are applied on every later run, exactly as in story 2.2. In `to-be.md` they form the Open questions section; statements resting on an answer cite `[answer:Q<n>]`.
- The skill is plain markdown, needs only read file, write file, run a local command, and is copied unchanged to `.agents/skills/` and `.claude/skills/` (AD-13).
- Every new column names the old column it comes from, with a citation `[kb:<row identifier>]`, and is marked as a proposal until a person signs it.
- What the Knowledge Base cannot supply (keys, decimal places, whether a column may be empty, indexes, volumes) is marked as missing.
- Types are platform-neutral until a target platform is chosen, and the document says so.
- It reuses the As-is documents; it does not re-describe today's model.

- **Small context (AD-17, NFR-7).** Production is GitHub Copilot with GPT-5.6 Sol, a short-context model. Work one unit at a time: take one source table or one group of tab fields, read its packet from the tab's Slice, append its part of the design, record the unit in `progress.csv`, move on. Never read a whole Slice, a whole Knowledge Base file or the whole output document. A run can stop and resume at any unit. Keep the skill file under 300 lines. The As-is documents are read one section at a time, by heading.

- `to-be.md` starts with `Status: draft, not reviewed`; the review is added in story 4.3, once the document is complete (AD-18).

**Never:**

- Writing `CREATE TABLE` scripts for a specific platform.
- Redesigning the model; this is a like-for-like design unless the FSD says otherwise.
- Guessing a missing property.

## I/O & Edge-Case Matrix

| Scenario | Input / State | Expected Output / Behavior | Error Handling |
|----------|--------------|---------------------------|----------------|
| Happy path | `work/<tab>/slice/`, `decisions.csv`, `fsd.md`, the As-is documents | `work/<tab>/to-be.md`, section "New tables and views" | N/A |
| Table that is a query | A source table defined by SQL in OBIEE | Proposed as a view, with the SQL quoted exactly | N/A |
| Several source tables for one measure | Detail and aggregate tables | Each listed, with a note that keeping or dropping the aggregates is a decision for a person | N/A |
| Alias | A table joined several ways through aliases | One new table, with the roles it plays listed | N/A |
| Missing As-is document | A subject area the tab touches has none | Names it | Stops |
| Missing input | No slice or no confirmed decisions | Names what is missing | Stops |

</frozen-after-approval>

## Code Map

- `documents/planning-artifacts/migration-plan.md` -- stage 7 and stage 8: what the design must give Data Engineering, and what the repository can and cannot tell us
- `documents/planning-artifacts/prds/prd-POC-OBIEE-2-PBI-2026-11-23/prd.md` -- FR-14, open question 2
- `as-is/<estate>/` -- the As-is documents from story 2.2

## Tasks & Acceptance

**Execution:**

- [x] `skills/obiee-migration-agent/SKILL.md` -- add menu item "Write the to-be design"; the agent runs `slice --tab` first if the tab's Slice is missing
- [x] `skills/to-be/SKILL.md` -- the skill: inputs, steps, rules, the open-question rules, output path
- [x] `skills/to-be/references/document-structure.md` -- the sections of `to-be.md`; this story fills "New tables and views" and "Old-to-new column list"
- [x] `.agents/skills/to-be/, .claude/skills/to-be/` -- identical copies
- [x] `src/test/java/obi2pbi/SkillCopiesTest.java` -- extend to the to-be skill

**Acceptance Criteria:**

- Given the hand-made FSD's confirmed matches on the constructed estate, when the skill runs, then every confirmed column appears in the old-to-new column list, each with a citation; the result is recorded in Implementation Notes.
- Given the skill files, when the tests run, then the three copies are identical.

## Implementation Notes

**Built (2026-11-24).** The to-be skill, both parts (this story and 4.3 were written together), copied and covered by `SkillCopiesTest`. Not run by a model. The skill is named `obiee-to-be`, with the prefix agreed in story 2.2, so the task paths `skills/to-be` are `skills/obiee-to-be`.

Files:

- `skills/obiee-to-be/SKILL.md` (173 lines): inputs; step 1 cut the tab's Slice and check the as-is documents; step 2 new run / resume / regenerate; step 3 the fixed parts; step 4 one unit at a time (packet, whole notes file, optionally one part of the as-is document, decisions and questions, the part, the shared parts, notes, progress); step 5 settled questions, decisions, the once-each check of FSD items, the opening; step 6 `assemble-doc` and `check-doc --slice work/<tab>/slice`; step 7 hand-over; the question form with its `Options:` line.
- `skills/obiee-to-be/references/document-structure.md` (163): the three kinds of sentence (fact, proposal, missing); the parts and their order; the rules for every part; the layout per kind of unit; Decisions needed; the opening.
- `skills/obiee-to-be/references/obiee-features.md` (25; one table of 17 features): how to recognise each in a packet, why it needs a decision, the options, what the question must bring back.
- `skills/obiee-to-be/references/answers.md` (42): the answers file, taking in answers (a decision is taken by answering its question), regenerating.
- `skills/obiee-migration-agent/SKILL.md` (108): items 7 "Write the to-be design", 8 "Take in answers to the to-be design's questions", 9 "Review a to-be design".
- Copies under `.agents/skills/` and `.claude/skills/`; `SkillCopiesTest` expects the five skills and the to-be reference files.

Decisions the stories did not fix:

- **Parts are numbered by section, not by unit order.** The document must read: one page, new tables, old-to-new list, model, fields, unresolved, decisions, closing sections; but the units come as overview, tab fields, logical tables, database tables. A part is named `<block digit><position as three digits>-<unit_id>.md` (`1022-dbtable-11-samp-targets-f.md`, `3006-table-02-fact-target.md`, `4002-tab-fields-p1.md`), with fixed heading parts `1000`, `2000`, `3000`, `4000`, `5000`, `6000` and closing parts `9900` to `9930`. `assemble-doc` orders by name and `check-doc` still compares each unit's part with its packet (verified in the dry check).
- **Units are written in `units.csv` order**, so the tab's fields come first and fix the names of the model items; the logical tables then record in the notes which fields the tab uses and from which database column; the database tables, written last, fill "Used by tab items" from those notes.
- **A proposal is shown** as a line starting `Proposal (not from OBIEE; to be signed):`, with a definition or query as a block indented by four spaces under it. No backticks and no fence, because `check-doc` compares both with the packets; an indented block is not a quote for it (verified).
- **The old-to-new list** is a part of its own (`2000-old-to-new.md`); each database-table unit appends its rows, each with the column's citation and the status "proposal, to be signed by Data Engineering".
- **Platform-neutral types** are a fixed small table (text with length, whole number, number with precision and scale as recorded, date, date and time). Decimal places are "missing" where the scale is not recorded; "May be empty" is "yes, as recorded" only for nullable true, otherwise "missing (not stated in the export)".
- **Required rows the tab does not use** (unconfirmed measures of a touched table) go under `## Not described` with the reason `not used by this tab`, as decided by the lead agent.
- **`slice --tab` is run every time** by the skill's first step; it is deterministic, so this covers "missing or older than `decisions.csv`" without comparing file dates. The run's start line holds `kb_extracted_at | decisions` from the manifest; another value means regenerate.
- **The as-is document** is read one part at a time through the subject-area Slice's `units.csv`, plus its two question parts once at the start. It is referred to by section name; its markers are not copied and its question numbers are named as "as-is document, question Q<n>".
- **Grain** is not stated in a new table's section (the packet does not give it there); the section points to the model table whose source reads it.

Dry check by hand (not a run of the skill) on a copy, `build/skill-dry-run/work/test-tab/`; `work/test-tab/` is unchanged. Units `tab-fields-p1`, `table-02-fact-target`, `dbtable-11-samp-targets-f`. `slice --tab` on the copy: 30 packets, 256 required rows, as-is document reviewed. `assemble-doc`: 14 parts, 208 lines. `check-doc --slice`: status draft; 256 required, 32 covered (20 + 5 + 7, one of them only under Not described); 54 citations; 31 quotes compared; 0 findings other than `not_covered` (224, the units not written); 0 warnings; the note names the fixed parts as belonging to no unit, which is expected. The walk-through changed the layout in three places: where the external name goes, key fields that are not confirmed but needed by relationships, and question numbers inside decision notes.

Not done:

- Acceptance criterion 1 (the hand-made FSD on the constructed estate) needs a run; not verified. GitHub Copilot: not verified.

Open points:

- The manifest's `decisions` value is a summary of counts. A decision changed without changing the counts is not recognised as a change by step 2; the user must ask for the design to be written again.
- "Used by tab items" in a new table's section and in the old-to-new list comes from the notes of the logical-table units, not from the database-table packet, so it carries no marker of its own; the review checks it against the logical-table packet.
- The decisions in `work/test-tab/decisions.csv` are test decisions by the lead agent; nothing designed from them is for use.

## Spec Change Log

## Review Triage Log

## Verification

**Commands:**

- `./build.sh` -- expected: the jar is built at `build/obi2pbi.jar` and every test passes

**Manual checks (if no CLI):**

- Read the design as Data Engineering would: could a table be built from it, and is everything unknown marked as missing?
