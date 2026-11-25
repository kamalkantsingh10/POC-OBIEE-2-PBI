---
title: 'Describe today's semantic model with the as-is skill'
type: 'feature'
created: '2026-11-23'
status: 'done'
route: ''
review_loop_iteration: 0
context:
  - '{project-root}/documents/planning-artifacts/architecture/architecture-POC-OBIEE-2-PBI-2026-11-23/ARCHITECTURE-SPINE.md'
  - '{project-root}/documents/planning-artifacts/prds/prd-POC-OBIEE-2-PBI-2026-11-23/prd.md'
  - '{project-root}/docs/port-guide/03-core-output-slice.md'
---

# Story 2.2: Describe today's semantic model with the as-is skill

<frozen-after-approval reason="human-owned intent — do not modify unless human renegotiates">

## Intent

**Problem:** The Knowledge Base is tables. Business owners, report authors and Data Engineering need today's semantic model in plain language: complete, traceable, and honest about what the AI could not work out, before any FSD arrives.

**Approach:** Create the migration agent, which can be started from GitHub Copilot and from Claude, and its first skill, as-is. The skill reads one subject-area Slice and writes a complete As-is document, ending in an Open questions section that a person answers; answers are kept and folded in on the next run.

## Boundaries & Constraints

**Always:**

- **Agent.** One agent, `obiee-migration-agent`, wired the way the BMad developer agent is: `.github/agents/obiee-migration-agent.agent.md` for GitHub Copilot, pointing at `.agents/skills/obiee-migration-agent/SKILL.md`; the same skill under `.claude/skills/` for Claude. It greets, shows a menu, and runs the chosen skill. This story adds menu items "Describe a subject area" and "Take in answers to open questions".
- The agent and its skills are plain markdown. They need only read file, write file, run a local command. They do not depend on BMad's scripts, Python or `uv`, because the target system has only Java (AD-13).
- Edited once under `skills/`, copied unchanged to `.agents/skills/` and `.claude/skills/`; a test fails if the copies differ.
- **Complete.** The document covers every presentation table and column, every measure, every logical table and source, every join, hierarchy, variable and security rule in the Slice, and every report-level calculation. Anything left out is listed in an appendix with the reason; nothing is dropped silently.
- Every factual statement ends with a citation `[kb:<row identifier>]` (AD-12). Formulas are quoted exactly as extracted.
- **Open questions.** Wherever the Slice does not answer something, the AI is unsure which reading is right, or it wants more information, it writes a numbered question in the Open questions section: the question, why it matters, what it assumed meanwhile if anything, how to get the answer, and an empty `Answer:` line. "How to get the answer" is a concrete step: what to look at or run and where (a screen in the OBIEE Administration Tool, a query against a named table, a named database object's definition, a specific document), and what to bring back. It does not name a person or a role. The body text points to the question number.
- **Answers are the user's and are kept.** Answers live in `as-is/<estate>/<subject-area>.answers.md`. The skill adds an answer there when the user gives one, in chat or by filling an `Answer:` line; it never changes or removes an existing answer. Every run reads that file first. A statement that rests on an answer is cited `[answer:Q<n>]`, not `[kb:...]`.
- The agent may run the jar itself: `slice` before writing if the Slice is missing or older than the Knowledge Base, and `check-doc` (story 2.3) after writing.
- The document describes the existing system only and proposes nothing for Power BI. The skill never writes into `kb/` or `slices/` (AD-1).

- **Small context (AD-17, NFR-7).** Production is GitHub Copilot with GPT-5.6 Sol, a short-context model. Work one unit at a time: read one packet, append one section to the output, record the unit in `progress.csv`, move on. Never read a whole Slice, a whole Knowledge Base file or the whole output document. A run can stop and resume at any unit. Keep the skill file under 300 lines. The agent file stays under 150 lines.

**Never:**

- Reading the full Knowledge Base, or a Slice table whole; the packets are the data input.
- Guessing a business meaning the Slice does not record; ask in an open question.
- Presenting a user's answer as if it came from OBIEE.
- Calling the document complete while `check-doc` reports uncovered rows.

## I/O & Edge-Case Matrix

| Scenario | Input / State | Expected Output / Behavior | Error Handling |
|----------|--------------|---------------------------|----------------|
| Start from GitHub Copilot | User picks the agent in Copilot | Agent loads its skill, greets, shows the menu | N/A |
| Start from Claude | User runs the agent's skill | Same greeting and menu | N/A |
| Describe a subject area | User names an estate and subject area | Runs `slice` if needed, writes `as-is/<estate>/<subject-area>.md`, runs `check-doc`, reports the result and the number of open questions | N/A |
| Measure with several sources | Detail and aggregate tables behind one measure | Says which source applies when, from priority and content filters; asks an open question if that cannot be told | N/A |
| Unclear meaning | Two measures with the same formula and different names | Both described; an open question asks what distinguishes them | N/A |
| Gap in extraction | Entries in the Slice's `errors.csv` or `unparsed.csv` | Stated up front; an open question where a gap affects the description | N/A |
| Report-level logic | Reports in the Slice with calculations | A section listing them with formulas exactly as extracted | N/A |
| Take in answers | User fills `Answer:` lines, or tells the agent an answer | Answer recorded in the answers file; the affected sections rewritten with `[answer:Q<n>]`; the question moved to "Answered questions" with its answer | N/A |
| Regenerate | Knowledge Base re-extracted, document rewritten | Earlier answers still applied; a question whose answer no longer fits is re-opened with the old answer shown | N/A |
| Any size, short context | A subject area with hundreds of columns | One packet read and one section appended per unit; `progress.csv` updated after each; coverage still complete | N/A |
| Stop and resume | The run is interrupted after unit 7 | The next run reads `progress.csv` and continues at unit 8; nothing is repeated or lost | N/A |
| Final sections | All units done | The one-page summary and the Open questions section are written last, from the short notes and the questions file, not from the whole document | N/A |
| Missing input | No Knowledge Base for the estate, or unknown subject area | Names what is missing and lists what exists | Stops |

</frozen-after-approval>

## Code Map

- `.github/agents/bmad-agent-dev.agent.md, .agents/skills/bmad-agent-dev/` -- the wiring pattern to copy for the agent file and skill folder
- `reference/analysis/sample-sales/as-is-analysis.md` -- the layout and tone to follow, including its numbered open points with `Answer:` lines (the new skill says how to get the answer, where that example said who can answer)
- `reference/as-is-skill/` -- the first attempt's skill; start from its rules, not its layout
- `docs/port-guide/03-core-output-slice.md` -- §9 the old skill, and how its prescribed layout differs from the example (§9.4, §9.5)
- `documents/planning-artifacts/prds/prd-POC-OBIEE-2-PBI-2026-11-23/prd.md` -- FR-13, FR-17, FR-18

## Tasks & Acceptance

**Execution:**

- [x] `skills/obiee-migration-agent/SKILL.md` -- the agent: who it is, the menu, how it runs a skill, how it runs the jar
- [x] `.github/agents/obiee-migration-agent.agent.md` -- GitHub Copilot entry: description plus the line that loads the skill from `.agents/skills/`
- [x] `skills/obiee-as-is/SKILL.md` -- the skill: inputs, steps, the completeness rule, the open-question rules, the answers file, output path
- [x] `skills/obiee-as-is/references/document-structure.md` -- the sections and what each must contain, from the Sample Sales example, ending with Open questions, Answered questions, and the Not described appendix
- [x] `.agents/skills/, .claude/skills/` -- identical copies of both skill folders
- [x] `src/test/java/obi2pbi/SkillCopiesTest.java` -- fails if a copy differs from `skills/`
- [ ] `as-is/sampleapp/sample-sales.md` -- one real run on Sample Sales, kept as the example output

**Acceptance Criteria:**

- Given the Sample Sales slice, when the skill runs in Claude, then the document has every section in `document-structure.md`, `check-doc` reports no uncovered row, and a spot check of twenty statements finds each supported by the row it cites; results recorded in Implementation Notes.
- Given that document, when two open questions are answered and the agent takes them in, then the answers file holds both, the affected sections cite `[answer:Q<n>]`, and a full regeneration keeps them.
- Given the largest subject area in the sample Knowledge Base, when the skill runs, then it finishes with no step reading more than one packet, and `check-doc` passes.
- Given GitHub Copilot with GPT-5.6 Sol, when the skill runs on Sample Sales, then it completes and `check-doc` passes. This can only be verified on a machine with that client; until then it is recorded as not verified.
- Given the agent files, when it is started in GitHub Copilot, then it shows the menu and can run the skill. If Copilot cannot be tried from this machine, that is recorded as not verified.
- Given the skill files, when the tests run, then the copies are identical.
- Given this story and story 2.3 are closed, then PRD milestone SM-0 is met: one subject area fully described from the Knowledge Base alone.

## Implementation Notes

**Built (2026-11-23).** The agent, the as-is skill and their copies. The real Sample Sales run is left to a separate, fresh agent, as instructed, so the acceptance criteria that need a run are not verified here.

Files:

- `skills/obiee-migration-agent/SKILL.md` (86 lines): greeting, menu (Describe a subject area; Take in answers to open questions; Review a document), how a skill is started, the draft/ready rule, the jar commands. The two later items are named as not available yet.
- `.github/agents/obiee-migration-agent.agent.md`: same shape as the BMad entry; loads `.agents/skills/obiee-migration-agent/SKILL.md`.
- `skills/obiee-as-is/SKILL.md` (159 lines): inputs, new run / resume / regenerate, the per-unit routine (a to g), the opening, assembly and check, hand-over, one worked unit.
- `skills/obiee-as-is/references/document-structure.md` (165 lines): wording, the three levels of "must be in the section", citations, the section per kind of unit with the packet lines that carry required rows, a worked section, the open-question rules and form, the opening text.
- `skills/obiee-as-is/references/assemble.md` (66 lines): the files of a document, the jar commands `assemble-doc` and `check-doc`, what to do about each finding. Shared with the review skill.
- `skills/obiee-as-is/references/answers.md` (83 lines): the answers file, taking in answers, regenerating with answers re-applied.
- `.agents/skills/` and `.claude/skills/`: copies of `obiee-as-is`, `obiee-migration-agent`, `obiee-review`, made by `./build.sh skills` (new target, mirrored in `build.cmd`).
- `src/test/java/obi2pbi/SkillCopiesTest.java` (5 tests): the skills exist with name and description; each copy is byte-identical and has no extra file; the agent's files are under 150 lines and every other skill file under 300; the Copilot entry loads the agent from `.agents/skills/`; no skill file mentions `uv run`, `_bmad`, Python or `{skill-root}`.
- `README.md`: the `skills` build target and the `skills/` folder.

Decisions the story did not fix:

- **The document is built from parts.** Each unit's section is its own file, `as-is/<estate>/<name>.parts/<nnn>-<unit_id>.md` (`<nnn>` = position in `units.csv`), with `000-summary.md` (status line, opening, one-page summary; written last from the notes) and `990-open-questions.md`, `991-answered-questions.md`, `992-not-described.md` (created at the start, appended to per unit). `<name>.md` is produced only by joining the parts in file-name order (since the follow-up below: by the jar's `assemble-doc`). Reason: the summary stands at the top but is written last, and corrections (answers, review) must not require reading or rewriting the whole document. The document is never edited directly. So "appending to the document" became "writing one part per unit".
- **The question parts are the questions file.** Questions are appended in their final form to `990-open-questions.md`; there is no separate `<name>.questions.md`. `<name>.notes.md` holds two or three lines per unit with their citations, plus the titles of questions asked, and is read before each unit so that questions are not asked twice.
- **`progress.csv`** has the columns `unit_id,status,last_question,note` and is append-only. A `_run,start,<n>,<kb_extracted_at>` line opens a run; a unit's state is its last line after that (`written`, later `reviewed`). `last_question` carries the question counter so numbers are never reused, also across regenerations. A run whose `_run` note differs from the Slice's `kb_extracted_at` is a regeneration.
- **A question has two lines more than the conventions require:** `Raised in: <unit_id>` (so answers and regeneration find the section) and `Assumed meanwhile:` (the story asks for it). `check-doc` allows other lines between the fixed ones; verified in the dry run.
- **A corrected answer** is a further `## Q<n>` entry at the end of the answers file; the last entry applies. **A question whose answer no longer fits** after regeneration is asked again under a new number with a line quoting the earlier answer; the old question stays under Answered questions. Both keep the answers file append-only.
- **Answers typed into the document are protected:** no assembly overwrites a document that holds an answer not yet in the answers file (since the follow-up: enforced by `assemble-doc`).
- **Citation density.** Required rows must be cited; every packet item must appear with a marker; logic is quoted exactly; lineage lines need not be restated. Tables carry their markers in a last `Evidence` column. Without this limit a table section would be mostly markers.
- **General OBIEE behaviour** may be stated only where needed, starting "In OBIEE generally," and without a marker.
- `SkillCopiesTest` applies the 300-line limit to the reference files as well as to `SKILL.md`.

Dry check (by hand, not a run of the skill): units `overview` and `table-02-fact-target` of Sample Sales, written under `build/skill-dry-run/as-is/sampleapp/`, assembled, then `java -jar build/obi2pbi.jar check-doc build/skill-dry-run/as-is/sampleapp/sample-sales.md --slice slices/sampleapp/sample-sales`: status draft; 125 rows required, 15 covered (all 6 of `overview` and all 9 of the table); 37 citations checked, none unknown; 3 open questions parsed; the only findings are 110 `not_covered` rows of the twelve units not written. The walk-through changed the skill in five places: a level of detail must not be translated into "day" or "month" (I did exactly that); which marker an item needs; the overview's "where this came from" block goes to the notes; a how-to may name a screen only when sure of it; the intro line of the open-questions part.

**Follow-up (2026-11-23), after the first hand-over.** Four changes asked for before the real run:

- **Skills renamed** to `obiee-as-is` and `obiee-review` (the agent stays `obiee-migration-agent`), in `skills/`, `.agents/skills/`, `.claude/skills/`, the `name:` lines, every cross-reference, `SkillCopiesTest` and `README.md`. The old copies were removed.
- **New jar command `assemble-doc <document>`** (`cli/AssembleDocCommand.java`, `core/DocAssembly.java`, `AssembleDocTest`, 6 tests). It joins `<document without .md>.parts/*.md` in code-point order of the file names: line feeds, UTF-8, empty lines at the start and end of a part dropped, one empty line between two parts, an empty part adds nothing. Before overwriting it reads the existing document; if a question there has text after `Answer:` that no entry of the answers file for that number contains, it writes nothing, prints the questions with the answers as typed, and exits 3. The skills no longer contain `cat`, `copy /b`, `grep`, `findstr`, `ls` or `dir`; the only commands a skill runs are jar commands (and, in the review, a date command if the date is unknown).
- **`check-doc` names the unit of a finding:** new column `unit_id` in `<document>.check.csv` (before `detail`), from `required_rows.csv` for `not_covered`, and from the parts folder for a finding at a line when the document is exactly its parts joined (the part's file name without number and `.md`). The summary prints the findings per unit, and a note when the document is not its parts joined. `assemble.md` lost its table-to-unit mapping.
- **Packets mark required rows:** `slice` ends the one line that states each required row with `(REQUIRED)`, and the header says so. `document-structure.md` no longer lists line kinds per unit. `PacketsTest.testEachRequiredRowIsMarkedOnExactlyOneLineOfItsPacket` checks, for every packet of both sample estates, that the marked lines are exactly the unit's rows in `required_rows.csv`. `slices/sampleapp` and `slices/bi-analytics-2013` were cut again.

Decisions in the follow-up: parts are separated by one empty line, not a bare line feed, and leading and trailing empty lines of a part are dropped, so a skill need not remember to start a part with an empty line and the markdown stays readable; `Answer:` is defined as the last line of a question (both commands read everything after it as the answer), so the lines the skills add ("Earlier answer", "Note from the review") go before it; an answer typed in the document counts as recorded only when its text is in an entry for that number, so a correction typed over an old answer is caught too. `docs/skill-conventions.md` now describes the parts, `assemble-doc`, the `(REQUIRED)` mark, the `unit_id` column, the last-line rule and corrected answers.

Dry check repeated with the jar only: `assemble-doc` wrote 6 parts, 131 lines; `check-doc` gave status draft, 125 required, 15 covered, 37 citations, 3 open questions, 110 `not_covered`, listed per unit. Typing an answer into the document made `assemble-doc` refuse with exit 3 until the answer was in the answers file.

Line counts now: agent 86; `obiee-as-is/SKILL.md` 159, `document-structure.md` 165, `assemble.md` 66, `answers.md` 83; `obiee-review/SKILL.md` 144.

Open: Architecture AD-14 names six commands; `assemble-doc` is a seventh and the Spine has not been changed.

**Second follow-up (2026-11-23), after the first real run.** The skills were changed for the jar's new packets and per-unit checks and for the seventeen points the run reported. No Java was changed.

- **Split units.** Each part of a split unit is a unit with its own part file. The part with `part` = 1 carries the `## ` heading and the opening; a later part has no `## ` heading and no introduction and starts with the `### ` heading of the block its packet holds.
- **Backticks and fences are for exact quotes only**, in every part including questions; a suggested query is ordinary text. A mapping that is exactly one database column is written as TABLE.COLUMN without backticks; anything else is quoted exactly.
- **Table sections follow the packet's blocks** (the table, its columns, its sources) with fixed columns: type and precision carried, nullable and lineage left out, "name set by hand" only when true, writeable for every measure, active and priority for every source. Hierarchies carry the member count. The gaps section names unread attributes exactly as the packet does, explains none, and copies totals. The overview says what was and was not examined.
- **Observations across sections:** new part `980-observations.md`, written from `- Observation:` lines in the notes; a unit's section cites only its own packet.
- **Stale questions (rule chosen):** a question that a later packet settles is moved from `990-open-questions.md` to a new part `993-questions-the-export-answered.md` (free section `## Questions the export answered`) with a `Settled by:` line carrying the citation, and the pointer in the raising section is changed. It is not put in the answers file, which stays the users' own. `check-doc` accepts this today; no Java change is needed. The number is not reused because the counter lives in `progress.csv`.
- **How-to steps** may name what a packet names, describe an action in general terms and give a query as plain text; they give no menu paths or screen names. Two questions from the run (Q3, Q16) are in `document-structure.md` as examples, rewritten to these rules.
- `docs/skill-conventions.md` gained two sentences on the parts `980` and `993`.

Dry check against the re-cut Slice (15 units, Fact Revenue split in two), written under `build/skill-dry-run/`: `overview`, `table-01-fact-revenue-p1`, `table-01-fact-revenue-p2`. `assemble-doc`: 9 parts, 252 lines. `check-doc`: 125 required, 36 covered (6 + 25 + 5), 114 citations, 34 quotes compared, 0 `quote_not_in_packet`, 0 `unknown_citation`, 0 warnings, 2 open questions; the 89 findings are the `not_covered` rows of the twelve units not written. The two table parts were laid out by a throwaway script that copies markers from the packet lines, to test the layout and the conventions, not a model's care in copying.

Line counts now: agent 92; `obiee-as-is/SKILL.md` 169, `document-structure.md` 202, `assemble.md` 71, `answers.md` 83; `obiee-review/SKILL.md` 176.

Changed against the story's text: after "Describe a subject area" the agent now stops and asks for the review to be started in a new chat, unless the request already states the whole task; story 2.4 says the review runs automatically.

**Third follow-up (2026-11-23), after run 2.** Skills only; no Java changed.

- New section layout for the unit kind `physical_table` (`## Database table <NAME>`): opening (where, type, alias, description, object name or query, special role, counts), `### Columns` (one row per column: # | Column | Type | Precision | Scale | Nullable | External name | Mapped by | Evidence, values as the packet prints them), `### Keys`, `### Foreign keys and joins`, `### Read by`.
- Logical-table layout follows the new packets: `### Keys`; per source the tables it reads, its joins (one row per packet line, join type and cardinality copied) and a mapping table that copies type, precision and nullable from the packet; database tables are named and pointed to, not cited. Nullable is carried exactly as printed.
- New fixed rules: an empty block keeps its heading and one sentence; an absence is stated in one of two fixed forms (the packet's own words with its marker, or "No … is recorded for this …" with no marker when the packet has no line); a fact on an unmarked or `->` line takes the marker of the item it belongs to; one `### Points to note` in the last part of a split unit; "See also:" lines on earlier questions; `- To check in a later part` notes.
- The question and observation rules moved to a new reference file, `references/questions.md`, to keep `document-structure.md` readable.
- The opening gets a table of the database tables (columns, key or none recorded, read by), from one fixed note line per database-table unit.
- The Slice path may be given explicitly (agent, as-is, review).

Dry check on the re-cut Slice (28 units): `overview`, `table-06-dim-sales-rep`, `dbtable-02-emp-parent-child`, written by hand. `assemble-doc`: 9 parts, 176 lines. `check-doc`: 274 required, 23 covered (6 + 10 + 7), 46 citations, 18 quotes compared, 0 `quote_not_in_packet`, 0 `unknown_citation`, 0 warnings; all remaining findings are `not_covered` rows of units not written.

Line counts now: agent 96; `obiee-as-is/SKILL.md` 172, `document-structure.md` 196, `questions.md` 68, `assemble.md` 73, `answers.md` 83; `obiee-review/SKILL.md` 182.

Surprises:

- (Resolved in the follow-up.) The packet did not mark which of its lines carry required rows; it gave only the count.
- (Resolved in the follow-up.) `check-doc` reported a finding's row and table but not its unit.

Not done:

- `as-is/sampleapp/sample-sales.md`, the real run, and with it acceptance criteria 1, 2 and 3 (spot check of twenty statements; two answers taken in and kept across regeneration; the largest subject area).
- GitHub Copilot with GPT-5.6 Sol: not verified, no such client on this machine. `build.cmd skills` is not verified (no Windows machine).

## Spec Change Log

## Review Triage Log

## Design Notes

The first attempt had two layouts: a technical one prescribed by its skill, and the plain business-language one actually used for Sample Sales. Kamal chose the second, with citations next to each statement.

One agent with a menu, not one agent per epic: the user starts the same agent whether describing a subject area, tracing an FSD (story 3.2 adds that item) or writing a to-be design (story 4.2). The agent file stays thin; each capability is its own skill.

Completeness is checked by program (story 2.3), not left to the AI's judgement. Build 2.3 first or alongside; until it exists, the skill writes the coverage appendix by hand.

## Verification

**Commands:**

- `./build.sh` -- expected: the jar is built at `build/obi2pbi.jar` and every test passes
- `java -jar build/obi2pbi.jar check-doc as-is/sampleapp/sample-sales.md --slice slices/sampleapp/sample-sales` -- expected: no uncovered rows, no unknown citations; exit 0

**Manual checks (if no CLI):**

- Read the Sample Sales output next to `reference/analysis/sample-sales/as-is-analysis.md`: same facts, same plain language, plus citations and the open questions.
