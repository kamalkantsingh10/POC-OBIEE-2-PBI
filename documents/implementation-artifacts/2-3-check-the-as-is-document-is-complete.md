---
title: 'Check the as-is document is complete'
type: 'feature'
created: '2026-11-23'
status: 'done'
route: ''
review_loop_iteration: 0
context:
  - '{project-root}/documents/planning-artifacts/architecture/architecture-POC-OBIEE-2-PBI-2026-11-23/ARCHITECTURE-SPINE.md'
  - '{project-root}/documents/planning-artifacts/prds/prd-POC-OBIEE-2-PBI-2026-11-23/prd.md'
---

# Story 2.3: Check the as-is document is complete

<frozen-after-approval reason="human-owned intent — do not modify unless human renegotiates">

## Intent

**Problem:** "Complete" cannot rest on the AI saying so. A missed measure or a citation to a row that does not exist looks exactly like a finished document.

**Approach:** Add the `check-doc` command: it reads a skill-written document and the Slice it was written from, and reports rows the document never mentions and citations that point nowhere.

## Boundaries & Constraints

**Always:**

- The check is mechanical; no AI. The logic lives in `core`.
- A row counts as covered if the document cites it as `[kb:<row identifier>]`, or lists it in the "Not described" appendix with a reason.
- Rows that must be covered: presentation tables and columns, logical tables, logical columns that are measures, logical table sources, joins, dimensions and hierarchies, variables, security rules, and report columns that are calculations.
- It writes `<document>.check.csv` next to the document and never edits the document.
- Java 11, standard library only; CSV through the one writer; exit 3 on any failure (AD-14).

**Never:**

- Judging whether a statement is true; this checks coverage and citations only.
- Rewriting the document.

## I/O & Edge-Case Matrix

| Scenario | Input / State | Expected Output / Behavior | Error Handling |
|----------|--------------|---------------------------|----------------|
| All covered | Every required row cited or listed as not described | `check.csv` has only its header | Exit 0 |
| Uncovered row | A measure in the Slice the document never cites | Finding `not_covered` with the row identifier and its table | Exit 3 |
| Unknown citation | `[kb:...]` naming a row not in the Slice | Finding `unknown_citation` with the line number | Exit 3 |
| Answer citation | `[answer:Q4]` | Checked against the answers file; `unknown_answer` if there is no Q4 | Exit 3 |
| Open question without a line | A question with no `Answer:` line | Finding `malformed_question` | Exit 3 |
| Summary | Any run | Prints rows required, rows covered, citations checked, open and answered question counts | N/A |
| Missing input | Document or Slice not found | Names what is missing | Exit 2 |

</frozen-after-approval>

## Code Map

- `documents/planning-artifacts/architecture/architecture-POC-OBIEE-2-PBI-2026-11-23/ARCHITECTURE-SPINE.md` -- AD-12 the citation form, AD-14 command behaviour
- `src/main/java/obi2pbi/in/kb/` -- the Knowledge Base and Slice reader from story 2.1
- `skills/as-is/references/document-structure.md` -- the section names the check relies on: Open questions, Answered questions, Not described

## Tasks & Acceptance

**Execution:**

- [x] `src/main/java/obi2pbi/core/DocCheck.java` -- find citations and questions in the text; compute required rows from a Slice; return findings
- [x] `src/main/java/obi2pbi/cli/` -- the `check-doc` command: `check-doc <document> --slice <folder>`
- [x] `src/test/java/obi2pbi/` -- one test per matrix row, on small documents written in the test
- [ ] `skills/as-is/SKILL.md` -- run `check-doc` after writing and fix or list every uncovered row before reporting done

**Acceptance Criteria:**

- Given a document with one fault of each kind, when the tests run, then each fault yields exactly its finding.
- Given the Sample Sales document from story 2.2, when `check-doc` runs, then it exits 0, and the printed summary is recorded in Implementation Notes.

## Implementation Notes

**Built (23 November 2026).** `check-doc <document> --slice <folder> [--required <csv>]`. Files: `core/DocCheck`, `core/DocCheckResult`, `core/Citation` (shared with `slice`), the `check` table in `core/SliceSchema`, `cli/CheckDocCommand` (registered in `Main.COMMANDS`), `DocCheckTest` (10 tests), `CitationTest` (3), `docs/skill-conventions.md`, `README.md`.

**What it does.** Reads the document, the Slice (through its manifest), `<name>.answers.md` and `<name>.md.review.csv` when present; writes `<name>.md.check.csv` (`finding`, `row_id`, `table`, `line`, `detail`) through the one CSV writer; prints status, rows required and covered, citations checked, open and answered questions, findings by kind. Exit 0, 3 or 2. The document is never changed.

**Decisions.**

- The required rows are a parameter of `DocCheck.check`. The command takes them from the Slice's `required_rows.csv`, or from `--required <csv>` (columns `row_id`, `table`) for story 4.3.
- A citation may name any row of the Slice that has a Row identifier, not only required ones.
- Citation syntax: `]` inside an identifier is written `\]`, a backslash `\\`. 0 of 58,299 sample identifiers need it.
- The skill did not exist, so the text conventions were fixed here and written in `docs/skill-conventions.md` instead of `skills/as-is/references/document-structure.md` (the Code Map's path). The skill author should follow that file or move it.
- Findings beyond the story's matrix: `not_described_without_reason`, `bad_status`, `reviewed_with_findings`, `reviewed_without_review_log` (asked for by the coordinator, AD-18). `malformed_question` also covers a missing "Why it matters:" or "How to get the answer:" line (AD-16), a reused number, a heading in the section that is not a question, and a document with no Open questions section. `unknown_answer` also covers a question under Answered questions with no entry in the answers file.
- File names: answers `<name>.answers.md`; review log and check file are the document's full name plus `.review.csv` / `.check.csv`.

**Tests.** One per matrix row on a document written in the test for the Slice of the combined hand-made estate (7 required rows): all covered, uncovered row, unknown citation, answer citation, malformed question, missing input, summary; plus the not-described reason, the status rules, and the required list as a parameter. One more writes a document from every citation the Sample Sales packets print and requires 0 findings (124 required rows, 503 citations).

**Not done.**

- `skills/as-is/SKILL.md` is story 2.2's; the task "run check-doc after writing" is not ticked.
- The second acceptance criterion (the Sample Sales document from story 2.2 exits 0) cannot be met until that document exists. Summary of the stand-in run: `required: 124 rows; covered: 124 rows (0 only listed as not described); citations: 503 checked; questions: 0 open, 0 answered; findings: 0`.

**Changed on 23 November 2026, after the first real run of the skills (Sample Sales).** Why: the reviewer had to write its own script to compare a section with its packet. `check-doc` now does it (`DocCheck.check(..., packets)`, `CheckDocCommand` reads `units.csv` and the packet files of the Slice).

- For every part of the document that is a unit of the Slice (file `<nnn>-<unit_id>.md`): a citation that no packet states is `unknown_citation` (also when the row is in the Slice's tables); a citation stated only in another unit's packet is the **warning** `citation_from_other_unit`, written to `check.csv` and not counted as a finding (exit code and `reviewed_with_findings` ignore it); a text between backticks or fence lines that is in no packet, character for character, is `quote_not_in_packet`.
- What a quote is, exactly, is in `docs/skill-conventions.md` section 9: text between two runs of backticks of equal length on one line, or the lines between two fence lines; white space at the ends ignored; the indentation of an indented fence taken off; a value a packet boundary cut may be quoted whole. Decision: there is **no** "not a quote" marker; text that is not in a packet does not go between backticks.
- Decision: quotes are compared only in parts that are units. The summary and the question sections may use backticks freely (a suggested query belongs in an open question). A part named after no unit is not compared and is named in a printed note.
- Decision: a quote may come from any packet of the Slice, not only the unit's own, as the task asked.
- Without a parts folder, or when the document is not its parts joined, no part is compared and a note says so.
- Printed: a `quotes:` line, a `warnings:` line, and under "each part against the packets" the three kinds line by line per unit (25 per unit, the rest in `check.csv`).
- Tests: three new in `DocCheckTest`. On a copy of the existing `as-is/sampleapp/sample-sales.md` against the re-cut Slice: 2 `quote_not_in_packet`, 0 `unknown_citation`, 0 warnings, and `reviewed_with_findings` because of the two.

## Spec Change Log

## Review Triage Log

## Design Notes

The same command checks a to-be design in story 4.3, where the required rows are the confirmed columns of the tab instead of a whole subject area. Keep the list of required rows as a parameter of the check, not built into it.

## Verification

**Commands:**

- `./build.sh` -- expected: the jar is built at `build/obi2pbi.jar` and every test passes
- `java -jar build/obi2pbi.jar check-doc <document> --slice <folder>` -- expected: prints the summary; exit 0 or 3
