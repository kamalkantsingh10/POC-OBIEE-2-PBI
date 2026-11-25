# Reviewing a to-be design

## Why, and what is different from an as-is review

A to-be design (`work/<tab>/to-be.md`) is what people will build new tables and a Power BI model from. It mixes three kinds of sentence: facts from OBIEE, proposals, and things marked missing. It goes wrong in ways an as-is document cannot: a new column tied to the wrong old column; a proposal that reads like a fact; a choice quietly made that a person should have been asked about; an item of the FSD that got lost or appears twice.

The three habits of this skill hold: do not trust the writer or your memory; read the packet first, the part second; settle or ask. You do not judge whether a proposal is good design. You check that it is marked as a proposal, that it follows from what it cites, and that it decides nothing that needed a decision.

The mechanics are those of the as-is review in this skill's `SKILL.md`: one unit at a time, corrections in the parts only, the same log columns and kinds, a `- Correction` line in the notes for every finding that changes a fact, the same status line at the end. Read that file's sections "Rules that hold throughout" and "The review log" first. What differs is below.

## Inputs

`<doc>` is `work/<tab>/to-be`; the Slice is `work/<tab>/slice`.

| Input | Needed |
|---|---|
| `build/obi2pbi.jar`; `work/<tab>/slice/units.csv` and `packets/`; `<doc>.parts/`, `<doc>.progress.csv`, `<doc>.notes.md` | Yes. If one is missing, say which, and stop |
| `../obiee-to-be/references/document-structure.md` and `obiee-features.md` (relative to this skill's folder: the to-be skill's folder is beside it) | Yes. The layout of each part, how a proposal is shown, which OBIEE features need a decision |
| `<doc>.answers.md` | If it exists |

The log is `<doc>.md.review.csv` (`"unit","line","kind","detail","action"`). Do not run `slice --tab`: you review the design against the Slice it was written from. Never write `decisions.csv`.

## Steps

### Step 1. See what there is to review

Read the two reference files, `units.csv` and the progress file. A unit whose last line (after the last `_run`) is `written` needs review. If a unit has no line, the design is not finished: say so and stop. A fresh review writes the log anew with its header.

### Step 2. The jar first

```
java -jar build/obi2pbi.jar assemble-doc work/<tab>/to-be.md
java -jar build/obi2pbi.jar check-doc work/<tab>/to-be.md --slice work/<tab>/slice
```

Deal with every finding it prints, unit by unit, and log each correction (`misquoted`, `unsupported`, `missing`). `check-doc` covers the `(REQUIRED)` rows, the markers and the quotes; it does not read a proposal, and it cannot see a lost FSD item that was cited somewhere.

### Step 3. Review the units, one at a time

For each unit to review, in `units.csv` order: read its packet from the file; then its part (the name is in `document-structure.md`: block digit, position, unit id); every line, and every row of every table. Check, by the unit's kind:

**Every kind.**
1. Each fact says what the packet line with its marker says (`wrong`, `unsupported`). Counts of rows equal the packet's counts (`missing`).
2. Each thing the writer suggests starts with `Proposal (not from OBIEE; to be signed):`. A suggestion written as a fact, or a definition between backticks, is `unsupported`: mark it as a proposal, in an indented block.
3. Nothing is guessed: a key, decimal places, "must be filled", a grain, a volume that no packet line gives is `unsupported`; replace it by "missing".
4. Nothing is decided silently. With `obiee-features.md` beside you, look in the packet for each feature on that page. If the packet shows one and the part neither asks nor points to a decision: `unclear`; add the question with its options and the `- Decision:` note, and take out any definition that presumed the answer.

**A database table (`### New table` / `### New view`).** One row per column of the packet, in its order. For each row: the new column's old column is the one on that packet line, with that line's marker; the type follows the table of platform-neutral types; length, precision and decimal places are as recorded or "missing"; "May be empty" is "yes, as recorded" only where the packet says nullable true. A table of type `select` is proposed as a view and its SQL is quoted exactly. Then read `2000-old-to-new.md`: it has the same rows for this table, each with the status "proposal, to be signed by Data Engineering".

**A model table.** Every source is there with its marker. Only fields marked CONFIRMED FOR THE TAB are carried, with the items the packet lists. For each measure block: the OBIEE formula is the packet's, character for character, with the aggregation rule's marker; "Read from" matches the mapping lines; writeable is as recorded; the proposed definition uses only that table and column. A measure marked NOT CONFIRMED FOR THE TAB is in `9920-not-described.md` with the reason `not used by this tab`, and is not designed.

**The tab's fields.** For each item of the packet: if CONFIRMED, exactly one row in the fields table, its FSD text quoted exactly, its confirmed column(s) those of the packet, and "Supplied by" naming a model item that the notes show was really designed (`- Model item:` lines). If not confirmed, exactly one row in `5000-unresolved.md` with the packet's status and decision note, and none in the fields table. An item in both, in neither, or twice: `wrong` or `missing`. Each FSD measure: built from exactly the confirmed columns of its items, its combination stated as the FSD's definition, and no step added that the FSD text does not say.

**Relationships, hierarchies, variables, security.** Rows and cardinalities as printed; each proposed relationship follows from the row above it; a join that is not a plain one-to-many inner join, a parent-child drill path, each row filter and each per-user value has its decision.

Act as in the as-is review (correct from the packet; `unclear` becomes a question), log, append the correction lines to the notes, then append `<unit_id>,reviewed,<highest question number>,<n> findings` to the progress file.

### Step 4. Every FSD item exactly once

Read `work/<tab>/fsd_items.csv` (it is short). For each row that is not of kind `replaces`, find its id in the fields parts (`4…-tab-fields….md`) or in `5000-unresolved.md`: once, in one of the two. Log anything else under the unit `_fields` and correct it from the tab-fields packet.

### Step 5. Questions, decisions, opening

1. Read `9900-open-questions.md` once. For each question, read again the packet of its `Raised in:` unit. A wrong fact inside it is `wrong` or `unsupported`; a how-to that says nothing concrete, a decision with no `Options:` line, or an "Assumed meanwhile" that takes the decision, is `bad_question` (`question rewritten`).
2. Read the notes in full and `6000-decisions.md`: each `- Decision:` note has a row, each row an open or answered question, and no row states a choice unless it ends with an answer marker.
3. Read `0000-summary.md` against the notes and your correction lines, as in the as-is review. Read `9920-not-described.md`: a reason other than `not used by this tab` must say something real.
4. Append `_summary,reviewed,<highest question number>,` to the progress file.

### Step 6. Check again, and mark it reviewed

Run `assemble-doc` and `check-doc` until the exit code is 0. Then, only if every unit and `_summary` are reviewed, set line 1 of `0000-summary.md` to the exact form the as-is review gives (`Status: reviewed <yyyy-mm-dd> (<a> findings corrected, <b> turned into questions, <c> questions open)`, with `; reviewed in the same session as the writing` when that is so), run both commands once more, and append `_document,reviewed,<highest question number>,<date>`.

Report: the status line; findings by kind; what became a question; the decisions still open; the FSD items unresolved. Say that reviewed means checked against the Slice, not agreed: every proposal still waits for a person's signature.
