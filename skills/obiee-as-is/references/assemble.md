# The files of a document, and how it is assembled and checked

The as-is skill, the answers procedure and the review skill all use this page. Run every command from the project folder.

## The files

`<estate>` is the estate's name, `<name>` the Slice's folder name (for example `sampleapp`, `sample-sales`). Write `<doc>` for `as-is/<estate>/<name>`.

| File | What it is | Who changes it |
|---|---|---|
| `slices/<estate>/<name>/` | The Slice: `units.csv`, `manifest.csv`, `packets/` | Only the jar. Never write here |
| `<doc>.parts/` | The document in pieces, one small file per section | The skills. This is where all writing and correcting happens |
| `<doc>.md` | The document people read | Only the jar's `assemble-doc`. Never edit it yourself |
| `<doc>.answers.md` | Users' answers | Added to, never changed |
| `<doc>.progress.csv` | Which unit is written, which is reviewed | Lines are added at the end, never changed |
| `<doc>.notes.md` | Two or three lines per unit, for the summary | Added to |
| `<doc>.md.check.csv` | Findings of `check-doc` | The jar |
| `<doc>.md.review.csv` | Findings of the review | The review skill |

The parts, in the order they are joined (the order of their file names):

- `000-summary.md`: the status line, the title, the opening, "The model in one page".
- `<nnn>-<unit_id>.md`: one per unit; `<nnn>` is the unit's `position` in `units.csv` as three digits (`001-overview.md`, `002-table-01-fact-revenue-p1.md`). The name matters: `check-doc` compares a part with the packet of the unit it is named after.
- `980-observations.md`: what shows only when two units are put side by side.
- `990-open-questions.md`, `991-answered-questions.md`, `992-not-described.md`, `993-questions-the-export-answered.md`.

Why pieces: no step ever has to read or rewrite the whole document. A correction touches one small file, and the jar rebuilds the document from the pieces, always the same way.

## Assembling

```
java -jar build/obi2pbi.jar assemble-doc as-is/<estate>/<name>.md
```

It joins every `.md` file of `<doc>.parts/` in file-name order, puts one empty line between two parts, and writes `<doc>.md`. It prints each part with the lines it became, the number of parts and the number of lines. A part that is empty adds nothing.

- **Exit code 0:** written.
- **Exit code 3: not written.** The document that is there holds an answer a user typed after `Answer:` which the answers file does not hold. The command lists those questions with the answers as typed. Nothing is lost: take those answers in (`answers.md`, beside this page, steps 1 and 2), then run the command again. Never get round this by changing or deleting the document.
- **Exit code 2:** the parts folder is missing or has no part; the message says which.

## Checking

```
java -jar build/obi2pbi.jar check-doc as-is/<estate>/<name>.md --slice slices/<estate>/<name>
```

If the user named another Slice folder, give that folder after `--slice`.

It prints the status, rows required and covered, citations checked, questions open and answered, the number of findings and of warnings, the findings counted per unit, and, unit by unit, each wrong marker and each wrong quote with its line. Exit code 0 means no finding (warnings do not count). Code 3 means findings: they are in `<doc>.md.check.csv`, with the columns `finding`, `row_id`, `table`, `line`, `unit_id`, `detail`. Code 2 means an input is missing; the message names it.

`unit_id` says where to make the correction: the unit whose packet states the row, or the part the line is in (`overview` is the part `001-overview.md`; `summary`, `observations`, `open-questions` and so on are the opening and closing parts).

It also prints a note naming the parts that belong to no unit (`980-observations.md`, `993-questions-the-export-answered.md`) and are therefore not compared with a packet. That is expected.

## Fixing findings

Work one unit at a time, in the order the summary lists them. For a unit: read at most 30 lines of the check file to see its findings, read the unit's packet, read the unit's part, and correct it: change only the lines concerned, in place if you can edit a file that way, or by writing the part back with them changed. When the units are done, assemble and check again. Repeat until the exit code is 0. Correct in the part, never in `<doc>.md`.

| Finding | What to do |
|---|---|
| `not_covered` | A required row is not cited. In the packet of the unit named, find the line that carries this row's marker (it ends with `(REQUIRED)`), and add what it says to the unit's part, with the marker. Only if there is nothing to say about it, add a line for it to `992-not-described.md` with the reason |
| `unknown_citation` | A marker was mistyped or cut, or names a row that no packet states. In the part named, find it (the `line` is the line of `<doc>.md`; the output of `assemble-doc` says at which line each part starts), and copy the marker again from the packet, exactly. If no packet line carries such a marker, the statement has no source: take it out |
| `quote_not_in_packet` | A text between backticks or in a fenced block is not in any packet character for character. If it is meant as a quote, copy it again from the packet, exactly. If it is your own wording (a column written as TABLE.COLUMN, a file name, a suggested query, a word stressed), take the backticks or the fence away |
| `citation_from_other_unit` | A warning, not a failure. A unit's section cites a row that another unit's packet states. Leave a plain pointer as it is. If the sentence says something about the other unit's row, move it to the notes as an observation and out of the section (`questions.md`, "Observations across sections"). A marker from an earlier part of the same split unit, under "Points to note", is expected. The review looks at each one that remains |
| `unknown_answer` | An answer marker or an answered question has no entry in the answers file. If the user gave that answer, add it (`answers.md`); otherwise the question belongs back in `990-open-questions.md` and the statement loses its answer marker |
| `malformed_question` | Restore the form in `questions.md`: the heading, the lines `Why it matters:`, `How to get the answer:`, `Answer:`, a number used once |
| `not_described_without_reason` | Add the reason after the marker |
| `bad_status` | Line 1 of `000-summary.md` must be exactly `Status: draft, not reviewed` or the reviewed form |
| `reviewed_with_findings`, `reviewed_without_review_log` | The document says reviewed too early. Set line 1 of `000-summary.md` back to `Status: draft, not reviewed`, fix the other findings, and run the review |

If the summary says the document is not its parts joined, run `assemble-doc` and check again before fixing anything.

A document is never called complete, checked or ready while `check-doc` reports a finding.
