---
title: 'Propose matches with the trace skill'
type: 'feature'
created: '2026-11-23'
status: 'done'
route: ''
review_loop_iteration: 0
context:
  - '{project-root}/documents/planning-artifacts/architecture/architecture-POC-OBIEE-2-PBI-2026-11-23/ARCHITECTURE-SPINE.md'
  - '{project-root}/documents/planning-artifacts/prds/prd-POC-OBIEE-2-PBI-2026-11-23/prd.md'
---

# Story 3.2: Propose matches with the trace skill

<frozen-after-approval reason="human-owned intent — do not modify unless human renegotiates">

## Intent

**Problem:** For each item an FSD puts on a tab, someone has to find the OBIEE column behind it. Done by hand, this is the slow, expert step.

**Approach:** Write the trace skill: it reads `fsd.md` and the Knowledge Base, lists the FSD's items, and proposes an OBIEE presentation column for each with a confidence score and the reason for it.

## Boundaries & Constraints

**Always:**

- **Agent.** This skill is a menu item of `obiee-migration-agent` (created in story 2.2), so it can be started from GitHub Copilot and from Claude. This story adds "Trace an FSD".
- **Open questions.** Wherever the AI is unsure or wants more information, it writes a numbered question with why it matters, how to get the answer, and an empty `Answer:` line. Answers are the user's, are kept in an answers file `work/<tab>/trace.answers.md`, and are applied on every later run, exactly as in story 2.2. Questions go in `work/<tab>/trace-questions.md`; each names the FSD anchor it is about.
- The skill is plain markdown. It may only read a file, write a file, and run a local command; it names no client-specific tool (AD-13).
- It is edited under `skills/` and copied unchanged to `.agents/skills/` (GitHub Copilot) and `.claude/skills/` (Claude). A test fails if the copies differ.
- It begins by listing its input files and stops with a message if one is missing.
- It never writes into `kb/`, `slices/` or a `slice/` folder (AD-1).
- `matches.csv` has the columns `item_id, fsd_anchor, fsd_text, row_id, confidence, basis, status` (AD-11). `status` is `proposed` or `unmatched`; `confidence` is a whole number from 0 to 100.
- The skill never writes `confirmed` or `rejected`, and never writes `decisions.csv`; only the Operator does.
- An item with no plausible column is written as `unmatched`; the nearest name is not picked to fill the gap.
- The FSD is referred to only by anchor (AD-10).

- **Small context (AD-17, NFR-7).** Production is GitHub Copilot with GPT-5.6 Sol, a short-context model. Work one unit at a time: take a few FSD items, append their rows to `fsd_items.csv` and `matches.csv`, record the unit in `progress.csv`, move on. Never read a whole Slice, a whole Knowledge Base file or the whole output document. A run can stop and resume at any unit. Keep the skill file under 300 lines.
- **Lookups go through the jar.** `find-columns` searches the Knowledge Base for a term and returns a short ranked list of candidate presentation columns, each with its subject area, logical column, aggregation and Row identifier, and any report column that uses it. It is plain text matching, with no AI.

- **Review before hand-over (AD-18).** The output starts with `Status: draft, not reviewed`. The review skill (story 2.4) then goes through it against its inputs and logs findings to a `.review.csv` file. The agent reports the output as ready for users only when its status is reviewed. For matches the review re-reads each FSD item and the proposed row from `find-columns`, and checks the basis is true and the score fits the scoring guide; a match it doubts has its score lowered and the reason added. It runs after `check-matches` and before the Operator is asked to confirm.

**Never:**

- Treating a matching name as proof of the same meaning.
- Opening a Knowledge Base file whole, or relying on `grep` or `findstr` being present; use `find-columns`.

## I/O & Edge-Case Matrix

| Scenario | Input / State | Expected Output / Behavior | Error Handling |
|----------|--------------|---------------------------|----------------|
| Happy path | `work/<tab>/fsd.md`, `kb/<estate>/` | `fsd_items.csv` (one row per item that needs data, with anchor and text) and `matches.csv` | N/A |
| Exact name | FSD names a subject area and column that exist | `proposed`, high confidence, basis `exact name` | N/A |
| Business term | FSD says "net revenue"; several columns are close | Best candidate `proposed` with a lower score; basis names the runners-up | N/A |
| Existing report | FSD names the dashboard page it replaces, and that page's reports are in the Knowledge Base | A match agreeing with a report column says so and cites the report column | N/A |
| Calculated item | FSD describes a calculation | Matched to the measure or report formula that computes it, formula quoted, or `unmatched` with a note | N/A |
| Nothing plausible | An item with no candidate | `unmatched`, empty `row_id` | N/A |
| Ambiguous item | The FSD text could mean two different measures | Best candidate `proposed` with a low score, and a question in `trace-questions.md` for the FSD author | N/A |
| Answer given | The user answers a question | Recorded in the answers file; the match updated; basis says it rests on the answer | N/A |
| Missing input | No `fsd.md` or no Knowledge Base | Names what is missing | Stops |

</frozen-after-approval>

## Code Map

- `documents/planning-artifacts/architecture/architecture-POC-OBIEE-2-PBI-2026-11-23/ARCHITECTURE-SPINE.md` -- AD-10, AD-11, AD-13
- `documents/planning-artifacts/prds/prd-POC-OBIEE-2-PBI-2026-11-23/prd.md` -- FR-10, FR-12, and open question 1
- `kb/<estate>/presentation_columns.csv, logical_columns.csv, analysis_columns.csv, report_lineage.csv` -- where candidates come from
- `reference/test-corpus/constructed-cases/` -- a small estate to write a test FSD against

## Tasks & Acceptance

**Execution:**

- [x] `skills/obiee-migration-agent/SKILL.md` -- add menu item "Trace an FSD"; the agent runs `fsd-text` first if `fsd.md` is missing, and `check-matches` after
- [x] `skills/trace/SKILL.md` -- the skill: inputs, steps, the scoring guide, the open-question rules, output files
- [x] `src/main/java/obi2pbi/core/ColumnSearch.java`, `src/main/java/obi2pbi/cli/` -- the `find-columns` command: `find-columns kb/<estate> "<term>" [--subject-area <name>] [--limit 10]`; match on whole name, then all words, then any word; tests for ranking and for the limit
- [x] `skills/review/SKILL.md` -- add the match review: inputs `fsd.md`, `fsd_items.csv`, `matches.csv`; findings to `matches.review.csv`; status recorded in `work/<tab>/trace-status.md`
- [x] `skills/trace/references/scoring.md` -- what each confidence band means and which evidence earns it
- [x] `.agents/skills/trace/, .claude/skills/trace/` -- identical copies
- [ ] `src/test/fixtures/fsd/` -- a hand-made FSD for the constructed estate, mixing exact names, business terms and one item with no match
- [x] `src/test/java/obi2pbi/SkillCopiesTest.java` -- extend to the trace skill

**Acceptance Criteria:**

- Given the hand-made FSD and the constructed estate's Knowledge Base, when the skill runs, then every FSD item has a row, the exact-name items point to the right columns, and the no-match item is `unmatched`; the result is recorded in Implementation Notes.
- Given the output, when `check-matches` (story 3.3) is later run on it, then it reports no missing items or unknown identifiers.
- Given `matches.csv` with three wrong matches planted by hand, when the review runs, then it flags them; the count found is recorded in Implementation Notes.
- Given the skill files, when the tests run, then the three copies are identical.

## Implementation Notes

**`find-columns` (Java part, built with story 3.3's batch; the skill is not written yet).** `java -jar build/obi2pbi.jar find-columns kb/<estate> "<term>" [--subject-area <name>] [--formula-contains <text>] [--limit 10]`, in `src/main/java/obi2pbi/core/ColumnSearch.java` and `src/main/java/obi2pbi/cli/FindColumnsCommand.java`; tests in `src/test/java/obi2pbi/FindColumnsTest.java` (ranking, limit, output, abbreviations, `--formula-contains`, usage). It compares the term's words with each presentation column's name, its logical column's name, the presentation and logical table names, and the logical column's formulas (derived formula, aggregation rules, mapping expressions); letter case, punctuation, camel case and plural endings are ignored; a short word that is an abbreviation of a longer one (same first letter, letters in order: `Discnt`, `Rep`, `Mth`) counts for less. Groups: whole name (column, logical column, or table + column), then all words, then some words; inside a group a word in the column's own name counts 3, in a table name 2, in a formula 1, then fewer extra words in the name, then the Row identifier. Small words (`of`, `the`, `vs` ...) are not looked for. Each candidate: Row identifier as a citation, subject area, presentation table, column, logical column, measure and default aggregation (with the rule's Row identifier), the formula of a derived column and what it refers to, database columns through lineage, and up to three report columns from `report_lineage`. Writes nothing; exit 0 also when nothing matches. How the skill reads the output is in `docs/skill-conventions.md`, section 10.3. On `kb/sampleapp` and the test FSD's items, the top candidate is the expected column for net revenue (Fact Revenue.Revenue), units, revenue target (Fact Target.Revenue), office, product brand, month and discount (Discnt Value); "region" and "customers" have no candidate in Sample Sales.

**The skill (2026-11-24).** Written, copied and covered by `SkillCopiesTest`; not run by a model. The skills carry the `obiee-` prefix agreed in story 2.2's follow-up, so the task paths `skills/trace` and `skills/review` are `skills/obiee-trace` and `skills/obiee-review`.

Files:

- `skills/obiee-trace/SKILL.md` (162 lines): inputs; the files it writes; step 1 new run / resume / FSD changed (by `source_sha256`); step 2 the tab, the subject area, the `replaces` row; step 3 list the items one FSD section at a time; step 4 match three items at a time (`find-columns`, judge the meaning, optionally one part of the as-is document, score, write); step 5 `check-matches`, what to do per failure and warning, and the hand-over, which says that only the Operator confirms and how `decisions.csv` is written; the question form.
- `skills/obiee-trace/references/scoring.md` (50): the bands of section 10.6, how to choose a number inside a band, what a basis must hold and in which order, three examples.
- `skills/obiee-trace/references/answers.md` (51): the answers file, taking in answers, what to do when the FSD changed.
- `skills/obiee-review/references/matches.md` (101): the match review; `skills/obiee-review/SKILL.md` (182) sends there when the thing to review is `matches.csv`.
- `skills/obiee-migration-agent/SKILL.md` (106): items 4 "Trace an FSD", 5 "Take in answers to trace questions", 6 "Review the matches of an FSD"; the agent never writes `decisions.csv`.
- Copies under `.agents/skills/` and `.claude/skills/`; `SkillCopiesTest` now expects the four skills, `scoring.md` and `matches.md`. `.github/agents/obiee-migration-agent.agent.md`: description updated.

Decisions the story and section 10 did not fix:

- **`progress.csv`:** `unit,status,last_question,note`, append-only. `_run,start,<n>,<source_sha256> | <subject area>` opens a run; `section <first>-<last anchor>,listed`; `<item_id>,matched`; `_matches,checked`; the review adds `<item_id>,reviewed` and `_matches,reviewed`. An item whose last line is `matched` needs review, so an item changed by an answer is reviewed again alone.
- **Two phases:** all items are listed first (section by section), then matched three at a time, because the subject area and the replaced page must be known before the first lookup, and `fsd_items.csv` is then the list of work, as `units.csv` is for the as-is skill.
- **The agent runs `fsd-text` every time** before tracing. It is deterministic, so this covers both "missing" and "older than the `.docx`" without comparing file dates, which a skill cannot do with read / write / run alone. A changed FSD is recognised by `source_sha256` against the run's start line.
- **Item kinds:** an access rule ("each manager sees only their own offices") is listed as kind `filter`, since section 10.4 has no kind for security and it restricts rows by a field. Items without an FSD number are named after what they belong to (`V2-office`, `S1-own-offices`, `N1-currency`).
- **A calculation** gets one `proposed` row per column it is built from, each scored for itself and capped at 79 when the way the pieces combine is the writer's reading of the FSD and not recorded in OBIEE. If one OBIEE column already computes it, that one is proposed instead.
- **Scoring inside the bands** (`scoring.md`): a name alone never passes 59; two candidates about equal, at most 59; another subject area, at most 59; unmatched is 0 when nothing came back and 10 to 29 when candidates were judged not to fit.
- **The basis has a fixed order** and starts with `searched "<term>"` for every lookup, so that the review can run the same lookups.
- **Questions** have two lines more than the as-is form: `Item:` and `FSD anchor:`. Answers are kept as in the as-is skill. A question whose item has left the FSD moves under `## No longer asked`; nothing is written to the answers file for it.
- **As-is document:** the skill reads one part file of the candidate's table, found through `units.csv`, and says in the basis whether the document is reviewed or draft.
- **Match review:** it only lowers. Finding kinds `wrong_column`, `basis_untrue`, `score_too_high`, `forced_match`, `missing_column`, `missing_item`, `missed_candidate`, `score_too_low`, `bad_question`; log `matches.review.csv` with `item_id,row_id,kind,detail,action`. A missing item is added as `unmatched` with confidence 0 for the trace skill to match. Status line: `Status: reviewed <yyyy-mm-dd> (<a> matches corrected, <b> turned into questions, <c> items unmatched)`.
- **Independence:** as for documents, after tracing the agent stops and asks for the review in a new chat unless the request states the whole task.

Dry check by hand (not a run of the skill) on a copy, `build/skill-dry-run/work/sample-sales-monthly/`; `work/sample-sales-monthly/` was not touched. Items R1 (replaces), F3, F4, K1. Lookups run: "product brand", "brand", "region", "sales region", "territory", "area", "region" with no subject area, "net revenue", "discount". Result: F3 proposed `Dim Products.Brand` at 80; F4 unmatched at 15 (nearest `Dim Offices.Area Name`, question Q2); K1 two rows, `Fact Revenue.Revenue` at 70 and `Fact Revenue.Discnt Value` at 65 (question Q3 asks whether Revenue is already net); Q1 asks for the replaced page's Row identifier. `check-matches`: 3 items and 1 `replaces` row, 4 match rows (3 proposed, 1 unmatched), 0 failures, 0 warnings, exit 0, with the notes that R1 names a page not in the Knowledge Base and that no existing report was available.

Not done:

- `src/test/fixtures/fsd/`, a hand-made FSD for the constructed estate: not written. The test FSD in `work/sample-sales-monthly/` (Sample Sales) is what the dry check used.
- Acceptance criteria 1 to 3 need a run of the skill and of the review with planted errors; not verified. GitHub Copilot: not verified.

Open points:

- **The replaced page cannot be looked up.** Section 10.4 says to find its Row identifier in `dashboard_pages`, but that table has 1,037 rows in the sample catalog and no jar command searches it; the skill may not open it whole. The skill fills `existing_id` only from an identifier it was given or that `find-columns` printed, and otherwise asks. A small lookup command for pages and reports would close this.
- `trace-status.md` and `matches.review.csv` are not in the file table of section 10.1; I did not change the conventions document.

**`find-columns --formula-contains` shows why it matched (later fix).** Each candidate found that way has a line `matched in the <which formula>: <text>`: the derived formula, its template, the definition kept on the column, an aggregation rule or a mapping (the last two with their Row identifier), and the formula with runs of white space as one space; longer than 160 characters, only the match with up to 60 characters on each side and `...`. Test: `FindColumnsTest.testAMatchByFormulaShowsWhichFormulaMatchedAndTheTextAroundIt`.

**`find-pages` (added with story 4.1).** `java -jar build/obi2pbi.jar find-pages kb/<estate> "<term>" [--limit 10]` (`core/PageSearch.java`, `cli/FindPagesCommand.java`, `FindPagesTest`): plain text search of `dashboards`, `dashboard_pages` and `analyses` by name and catalog path (a page's dashboard name counts too), printing the Row identifier to cite, the dashboard, the page's position, and the reports placed on it with their subject areas. It is how the trace skill fills `existing_id` of a `replaces` row (docs/skill-conventions.md, section 10.4). On `kb/sampleapp-v607`, "overview" gives 124 candidates; the first is page `01. quickstart/_portal/1.30 quickstart/overview` with four reports on "A - Sample Sales".

## Spec Change Log

## Review Triage Log

## Design Notes

No real FSD has been seen yet, so the skill is designed for the harder case: an FSD written in business terms. It then also handles an FSD that names OBIEE columns outright. When a real FSD arrives, run the skill on it and adjust the scoring guide.

Suggested scoring bands: 90-100 exact subject area and column name, or agreement with the existing report; 60-89 same column name in another table, or a close name with matching aggregation; 30-59 a plausible name only; below 30 write `unmatched`. Evidence from an existing report outranks a name.

To find candidates the skill runs `find-columns` and reads only the rows returned. The FSD itself is read a section at a time by anchor range, not whole.

## Verification

**Commands:**

- `./build.sh` -- expected: the jar is built at `build/obi2pbi.jar` and every test passes

**Manual checks (if no CLI):**

- Run the skill once in GitHub Copilot if it is available; otherwise record that as not verified.
