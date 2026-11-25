---
title: 'Review the as-is document before it goes to users'
type: 'feature'
created: '2026-11-23'
status: 'done'
route: ''
review_loop_iteration: 0
context:
  - '{project-root}/documents/planning-artifacts/architecture/architecture-POC-OBIEE-2-PBI-2026-11-23/ARCHITECTURE-SPINE.md'
  - '{project-root}/documents/planning-artifacts/prds/prd-POC-OBIEE-2-PBI-2026-11-23/prd.md'
---

# Story 2.4: Review the as-is document before it goes to users

<frozen-after-approval reason="human-owned intent — do not modify unless human renegotiates">

## Intent

**Problem:** The as-is document is written by an AI, and people will build tables from it. A wrong statement that reads well is the worst outcome, and `check-doc` only proves coverage, not truth.

**Approach:** Add a review skill to the agent. After a document is written it goes through the document again, unit by unit, against the packets, and confirms or corrects it. A document is marked as a draft until the review has passed, and the agent does not hand it over before then.

## Boundaries & Constraints

**Always:**

- **The review does not trust the writer.** For each unit it reads the packet first and the section second, and checks the section against the packet, not the other way round.
- It checks, per unit: every statement is supported by the row it cites; nothing in the packet is missing from the section; formulas are quoted exactly; plain-language wording does not change the meaning; each open question is a real gap and says concretely how to get the answer.
- Findings go to `<document>.review.csv`: unit, line, kind (`unsupported`, `wrong`, `missing`, `misquoted`, `unclear`, `bad_question`), detail, and what was done.
- A clear error is corrected in the document and logged. A doubt the review cannot settle becomes an open question; it is not smoothed over.
- **Status line.** Every skill-written document starts with a status line. It reads `Status: draft, not reviewed` when written, and `Status: reviewed <date>` with the counts of findings fixed and questions open only when the review has finished and `check-doc` passes afterwards.
- The agent tells the user a document is ready to share only when its status is reviewed. Before that it says the document is a draft.
- Works in a small context (AD-17): one unit at a time, with its own `progress.csv` entry, resumable.
- Plain markdown skill, copied unchanged to both client folders, started from the agent menu ("Review a document") and run automatically as the last step of "Describe a subject area".

**Never:**

- Marking a document reviewed when a unit was skipped.
- Rewriting sections for style.
- Removing an open question without an answer.
- Changing a user's recorded answer.

## I/O & Edge-Case Matrix

| Scenario | Input / State | Expected Output / Behavior | Error Handling |
|----------|--------------|---------------------------|----------------|
| Clean unit | Section agrees with its packet | No finding; unit recorded as reviewed | N/A |
| Unsupported statement | A sentence cites a row that does not say that | Finding `unsupported`; sentence corrected from the packet | N/A |
| Missing content | A measure in the packet is absent from the section | Finding `missing`; content added with its citation | N/A |
| Misquoted formula | A formula differs from the packet by a character | Finding `misquoted`; replaced with the exact text | N/A |
| Meaning changed | Plain wording says "sum" where the rule is an average | Finding `wrong`; corrected | N/A |
| Cannot settle | Packet is ambiguous | Finding `unclear`; a new open question written, with how to get the answer | N/A |
| Vague question | An open question that does not say how to get the answer | Finding `bad_question`; rewritten with a concrete step | N/A |
| Review finished | All units reviewed, `check-doc` passes | Status line set to reviewed with counts; agent reports ready to share | N/A |
| `check-doc` fails after review | A correction broke coverage | Status stays draft; the findings are fixed and the check run again | N/A |
| Interrupted | Run stops at unit 5 | Resumes at unit 6; status stays draft until the end | N/A |

</frozen-after-approval>

## Code Map

- `skills/as-is/, skills/obiee-migration-agent/` -- the skill and agent from story 2.2
- `slices/<estate>/<subject-area>/units.csv, packets/` -- the units and packets from story 2.1
- `documents/planning-artifacts/architecture/architecture-POC-OBIEE-2-PBI-2026-11-23/ARCHITECTURE-SPINE.md` -- AD-12, AD-16, AD-17, AD-18

## Tasks & Acceptance

**Execution:**

- [x] `skills/review/SKILL.md` -- the review skill: inputs, the per-unit checks, the findings file, the status line rule
- [x] `skills/obiee-migration-agent/SKILL.md` -- add menu item "Review a document"; run the review as the last step of "Describe a subject area"; never report a draft as ready
- [x] `skills/as-is/SKILL.md` -- write the status line as draft; hand over to the review
- [ ] `src/main/java/obi2pbi/core/DocCheck.java` -- `check-doc` reports the status line and fails a document marked reviewed that has uncovered rows or no `review.csv`
- [x] `.agents/skills/, .claude/skills/` -- identical copies
- [ ] `src/test/java/obi2pbi/` -- tests for the `check-doc` status rules; skill copies test extended

**Acceptance Criteria:**

- Given the Sample Sales document with five errors planted by hand (one of each kind above), when the review runs, then it finds and logs each, and the count found is recorded in Implementation Notes.
- Given a document whose review has not run, when the agent is asked for it, then it says the document is a draft.
- Given a reviewed document, when `check-doc` runs, then it passes and prints the review status.
- Given GitHub Copilot with GPT-5.6 Sol, when the review runs on Sample Sales, then it completes. Recorded as not verified until tried on a machine with that client.

## Implementation Notes

**Built (2026-11-23).** The review skill, its place in the agent's menu, and the draft status in the as-is skill. No run of the review was made; the planted-error test is left to a separate agent.

Files:

- `skills/obiee-review/SKILL.md` (144 lines): why, inputs, per unit: packet first, then the section, the unit's open questions and the answers file; eight checks in order; what to do per kind of finding; the log; then the opening and closing parts, assembly and `check-doc`, and only then the status line. One worked finding.
- `skills/obiee-migration-agent/SKILL.md`: menu item "Review a document"; "Describe a subject area" runs the review afterwards; a document is called ready only when line 1 says reviewed; the agent never sets the status itself; it says once that a new chat makes a more independent review.
- `skills/obiee-as-is/SKILL.md`, `references/document-structure.md`: line 1 is `Status: draft, not reviewed`; hand-over says the document is a draft and that the review is next.
- Copies under `.agents/skills/obiee-review/` and `.claude/skills/obiee-review/`; `SkillCopiesTest` covers all three skills.

Decisions the story did not fix:

- **Review progress lives in the same `progress.csv`** as the writing (`<unit_id>,reviewed,<last question>,<n> findings`). A unit needs review when its last line is `written`. So a section rewritten after an answer is reviewed again, and only that section.
- **The `line` column of `review.csv`** is the line number in the unit's part file, not in the assembled document: the review reads and corrects parts, and the document's line numbers shift with every correction. `detail` starts with the first words of the statement so it can be found.
- **A unit with no finding gets no row** in `review.csv` (the kinds are a fixed list); that it was reviewed is in `progress.csv`. The log is always created with its header, so a clean review still satisfies `reviewed_without_review_log`. A fresh review (no unit reviewed since the last `_run`) writes the log anew; otherwise it is added to.
- **Status line:** `Status: reviewed <yyyy-mm-dd> (<n> findings corrected, <m> turned into a question, <k> questions open)`. The counts follow the date after a space, which `check-doc` accepts.
- **An open question that the packet in fact answers** is not deleted (the story forbids removing a question without an answer): the fact is stated in the section and the question gets a line "Note from the review: …", logged as `bad_question`.
- **Additions the story does not list** as checks: a statement of fact with no marker, an invented business meaning and any Power BI proposal are `unsupported`; a statement resting on an answer is compared with the answers file and the statement, never the answer, is corrected; `992-not-described.md` entries with an empty reason are treated as `missing`; the summary is checked against the notes file and the review's own log.
- The review skill reads two reference files of the as-is skill (`../obiee-as-is/references/document-structure.md`, `assemble.md`) instead of repeating them.

Follow-up (2026-11-23): the skill is now named `obiee-review`; it builds the document with the jar's `assemble-doc` and no shell command; it finds required rows by the `(REQUIRED)` mark the packets now carry; `check-doc` findings name their unit (`unit_id`). Lines the review adds to a question go before its `Answer:` line. Details are in story 2.2's notes.

Second follow-up (2026-11-23), after the first real run. The review now: runs `assemble-doc` and `check-doc` first and deals with the per-unit findings, so its own effort goes on meaning; re-reads every packet from the file before the section and reads every table row; reviews the open questions once, at the end, each against the packet of its `Raised in:` unit (not with every unit); appends a `- Correction` line to the notes for every finding that changes a fact and re-checks the opening against them; moves a sentence about another unit's row to the observations part instead of deleting it, and reviews that part with markers from several units allowed; may change only the lines concerned in a part. In the log, a finding about a question has `unit` = its `Raised in:` unit and `line` = the line of its heading in `990-open-questions.md`. The status line has one exact form, zeros written: `Status: reviewed <yyyy-mm-dd> (<a> findings corrected, <b> turned into questions, <c> questions open)`, with `; reviewed in the same session as the writing` before the bracket closes when that is so. The agent no longer starts the review by itself after writing unless the request states the whole task; it asks for a new chat.

Third follow-up (2026-11-23), after run 2 and the planted-error test. The review does not read the notes before step 5 (it only appends to them), then reads them in full and matches each correction to the note it supersedes. It counts each section's rows against the packet's own counts. Facts inside a question are `wrong`/`unsupported` under the question's unit; `bad_question` is for form and how-to, with the actions `question rewritten` and `noted: the export answers it`. Step 7 counts every log line: `a` = corrected, removed, added, moved, question rewritten, noted; `b` = asked as; `a` + `b` = lines in the log. A late finding gets a further progress line. Absence statements and parent-item markers are not `unsupported`. The Slice path may be given explicitly.

Task `DocCheck.java` and its tests: already present from story 2.3 (`bad_status`, `reviewed_with_findings`, `reviewed_without_review_log`, tested in `DocCheckTest`); not changed here, left unticked.

Not done:

- The planted-error run on Sample Sales and its count (acceptance criterion 1), and the fixture copy with planted errors: there is no Sample Sales document yet.
- Acceptance criteria 2 and 3 need a run of the agent; not verified. GitHub Copilot with GPT-5.6 Sol: not verified.

## Spec Change Log

## Review Triage Log

## Design Notes

A review by the same model in the same chat tends to agree with what it just wrote. Two things reduce that: the review reads the packet before the section, and it works from the files, not from the conversation. Running the review in a new chat is better still; the agent says so, and works either way.

The planted-error test is the only honest measure of whether the review catches anything. Keep the planted copy as a fixture so the test can be repeated when the skill or the model changes.

## Verification

**Commands:**

- `./build.sh` -- expected: the jar is built at `build/obi2pbi.jar` and every test passes
- `java -jar build/obi2pbi.jar check-doc as-is/sampleapp/sample-sales.md --slice slices/sampleapp/sample-sales` -- expected: passes; prints `Status: reviewed`
