---
name: obiee-to-be
description: "Write the to-be design for one report tab from the columns the Operator confirmed: the new tables and views with every column and the old column it comes from, the Power BI semantic model with each measure and the OBIEE formula it replaces, what supplies each field of the FSD, and the decisions a person must take. Use when the user asks to write or regenerate a to-be design for a tab, or to take in answers to its open questions."
---

# To-be: design the new tables and the Power BI model for one tab

## What this produces, and why it works the way it does

You write `work/<tab>/to-be.md`: the starting point for the people who build the new tables and the Power BI model for one tab of an FSD. Until now they got nothing that tied the new thing to what the old report used. This document does that, column by column.

Four things matter more than anything else:

- **Tied to the old.** Every new column names the old column it comes from, with the marker of its row. Every measure shows the OBIEE formula it replaces, quoted exactly.
- **A proposal, and marked as one.** You suggest; people sign. A reader must never mistake your suggestion for a fact from OBIEE or for something already agreed.
- **Nothing guessed, nothing decided silently.** What the export does not hold (a key, decimal places, whether a column must be filled) is "missing". Where OBIEE does something Power BI does not do the same way, you set out the options and ask.
- **Small steps.** You may be running with a short memory. The work is cut into units; you read one packet, write one part, record it, move on. Everything you need later is in a file. The run can stop anywhere and be picked up.

You design like for like. You do not redesign the model, and you do not describe today's model again: the as-is document does that.

## Rules that hold throughout

- Your facts come from the packets of the tab's Slice, `work/<tab>/slice/packets/`, one unit at a time. Apart from `units.csv` and `manifest.csv`, do not open the other files of the Slice, and nothing under `kb/` or `slices/`. Never write into any of them.
- The as-is document is read one part at a time (step 4b), never whole. Never read `work/<tab>/to-be.md` whole and never edit it: the jar builds it from the parts.
- Backticks and fenced blocks are only for text copied exactly from a packet. A proposal is never between backticks (`references/document-structure.md` shows the form).
- You never write `decisions.csv`, `matches.csv` or `fsd_items.csv`. Only confirmed decisions count, and they are the Operator's.
- Never change or remove an entry of the answers file.
- If the user asks you to take in answers, read `references/answers.md` and follow that instead of the steps below.

## Inputs

`<tab>` is the folder of one FSD under `work/`. Write `<doc>` for `work/<tab>/to-be`.

| Input | Needed |
|---|---|
| `build/obi2pbi.jar` | Yes. If missing: `./build.sh` or `build.cmd`; stop |
| `work/<tab>/decisions.csv` with at least one `confirmed` row | Yes. If missing: the Operator has not decided yet. Say that the matches must be reviewed and then confirmed by the Operator, and stop |
| `work/<tab>/slice/units.csv`, `manifest.csv`, `packets/` | Yes. Cut by step 1 |
| A reviewed as-is document for every subject area the tab touches | Yes. Step 1 checks it |
| `references/document-structure.md`, `references/obiee-features.md` (beside this file) | Yes |
| `<doc>.answers.md`, `<doc>.progress.csv` | If they exist: earlier answers, and where an earlier run stopped |

## The files you write, all beside `work/<tab>/to-be.md`

| File | What it is |
|---|---|
| `<doc>.parts/` | The document in pieces. All writing and correcting happens here |
| `<doc>.md` | The document. Written only by the jar's `assemble-doc` |
| `<doc>.progress.csv` | `unit_id,status,last_question,note`; lines are added, never changed |
| `<doc>.notes.md` | A few lines per unit, for later units and for the summary |
| `<doc>.answers.md` | Users' answers, added to and never changed |
| `<doc>.md.check.csv`, `<doc>.md.review.csv` | Findings of `check-doc`; findings of the review |

## Steps

### Step 1. Cut the tab's Slice and check the as-is documents

1. Run `java -jar build/obi2pbi.jar slice kb/<estate> --tab work/<tab>`, every time. It is quick and gives the same Slice for the same decisions, so the Slice is never older than `decisions.csv`. Exit code 2 or 3 means nothing was written: show the message (no decisions, nothing confirmed, or a confirmed column that is not in the Knowledge Base) and stop.
2. Read what it printed. For each subject area it names the as-is document and its status line. If one does not exist, or its status is not reviewed, stop: name the subject area, and say that it must be described and reviewed first ("Describe a subject area", then "Review a document"). A design is not built on a draft.
3. Read `references/document-structure.md` and `references/obiee-features.md`.
4. Read `work/<tab>/slice/manifest.csv` (note `kb_extracted_at`, `decisions`, `subject_areas`) and `work/<tab>/slice/units.csv`, your list of work: `unit_id`, `position`, `kind`, `title`, `part`, `parts`, `packet_file`, `line_count`, `required_rows`.

### Step 2. Decide: new run, resume, or regenerate

Read `<doc>.progress.csv` if it exists. Only the lines after its last `_run` line count; that line's `note` is `<kb_extracted_at> | <decisions>` as the manifest had them.

- No progress file: a new run, step 3.
- The same note, and some unit has no `written` line: resume at that unit (step 4). Read `9900-open-questions.md` once: questions whose `Raised in:` names that unit come from an attempt that was cut short; use them again and do not add them twice.
- The same note, every unit `written`, no `_document,assembled` line: go to step 5. With that line: say it is written, and offer the review or taking in answers.
- Another note: the Knowledge Base or the decisions changed. Read `references/answers.md`, "Regenerating", and follow it.

### Step 3. Start a new run

Create, with `<doc>.parts/` as the folder for the parts:

1. `<doc>.progress.csv`: `unit_id,status,last_question,note`, then `_run,start,0,<kb_extracted_at> | <decisions>` (the largest `## Q<n>` of an existing answers file in place of 0).
2. `<doc>.notes.md`: the line `# Notes`.
3. The fixed parts, each with its heading and the sentence given here:
   - `1000-new-tables.md`: `## New tables and views`. `One section per database table the tab's model reads. Each is a proposal to be signed by Data Engineering. It is like for like: the old table, column by column. Types are platform-neutral, because no target platform has been chosen. What the export does not hold is marked "missing" and is not guessed.`
   - `2000-old-to-new.md`: `## Old-to-new column list`, the same sentence about proposals, and the table header `| New table.column | Old database column | Old field | Used by tab items | Evidence | Status |` with its separator line.
   - `3000-model.md`: `## Power BI semantic model`. `The model tables, their measures, relationships, hierarchies and security, each tied to what OBIEE holds. Every definition is a proposal to be signed. Where OBIEE behaves in a way Power BI does not reproduce by itself, no definition is proposed; the choice is listed under Decisions needed.`
   - `4000-fields.md`: `## Fields on the tab`. `Every item of the FSD that needs data, once: here with what supplies it, or under Unresolved.`
   - `5000-unresolved.md`: `## Unresolved`, `Items of the FSD that no confirmed column supplies. The design does not cover them.`, and the table header `| Item | FSD anchor | FSD text | Status | Decision note | Evidence |` with its separator line.
   - `9900-open-questions.md`: `## Open questions`, `Each says how to find the answer and ends with an empty answer line. Write the answer on that line, or tell the agent.`
   - `9910-answered-questions.md`: `## Answered questions`. `9920-not-described.md`: `## Not described`, `Rows of the Slice that this design leaves out, each with the reason.` `9930-questions-the-export-answered.md`: `## Questions the export answered`.
4. Read the as-is document's two question parts once, `as-is/<estate>/<name>.parts/990-open-questions.md` and `991-answered-questions.md`, and append to the notes, under `## as-is`, one line for each question that bears on a field or table of this tab: its number, whether it is answered, and the answer. You refer to them as "as-is document, question Q<n>"; their numbers are not this document's.

### Step 4. Write the units, one at a time

Take the units in `units.csv` order. For each, do all of a to g before the next. Do not read ahead.

**a. Read the unit's packet**, `work/<tab>/slice/<packet_file>`, whole. Lines that end with `(REQUIRED)` state rows the document must cover. In a logical-table packet each column says `CONFIRMED FOR THE TAB, items …` or `NOT CONFIRMED FOR THE TAB`.

**b. Read the whole notes file**, from its first line, before every unit: after a resume or once earlier turns have dropped out of your memory it is the only record of the names you gave to model items, which fields the tab uses, and which questions exist. Then, for a table unit, you may read the one part of the as-is document about the same table: find the unit with the same title in `slices/<estate>/<name>/units.csv`; its part is `as-is/<estate>/<name>.parts/<nnn>-<unit_id>.md` (`<nnn>` its position there, three digits). Take from it only what a designer must know (its "Points to note"), and refer to it by section name.

**c. Find the decisions and the questions.** Go through the packet with `references/obiee-features.md` beside you: every feature on that page that the packet shows is a decision. So is every place where the design has two reasonable shapes. Each decision is an open question with its options. Also ask where something a builder needs is missing and matters for this tab (the key of a table the tab reads). Check the question lists in the notes first; do not ask twice. The next number is one more than `last_question` on the last line of the progress file.

**d. Write the part**, named as `references/document-structure.md` says, following the layout for the unit's kind. Before you save, check against the packet:

- the marker of every `(REQUIRED)` line is in the part, or in the line you are about to add to `9920-not-described.md` or `5000-unresolved.md`; count them against the header's number;
- every table has as many rows as the packet has items of that kind (every column of a database table; every confirmed field; every item of the FSD);
- every formula, rule, filter and query is quoted exactly, and nothing else stands between backticks or in a fenced block;
- every definition, relationship and new object of yours starts with `Proposal (not from OBIEE; to be signed):`;
- every marker is one this packet prints.

**e. Append to the shared parts** what the layout says: rows to `2000-old-to-new.md` (a database table), rows to `5000-unresolved.md` (FSD items not confirmed), lines to `9920-not-described.md` (required rows the tab does not use, with the reason `not used by this tab`), questions to `9900-open-questions.md`.

**f. Append to the notes:**

```
## <unit_id>: <the part's heading>
- <one to three lines a later unit or the summary needs, each with the marker it rests on>
- Model item: <the name you gave> = <what it is>; items <ids>                       (tab_fields and logical_table units)
- Tab uses: <business table>, <field> (items <ids>), read from <DB TABLE.COLUMN>    (logical_table units; one line per confirmed field)
- New table: <NAME>, <n> columns, key <name and columns | missing>, read by <sources>   (physical_table units)
- Decision: <what OBIEE does, with its marker>; options <…>; Q<n>
- Settles Q<n>: <the fact, with its marker>                                          (if this packet answers an earlier question)
- Questions: Q<n> <a few words>; …                                                   (or "Questions: none")
```

**g. Append to `<doc>.progress.csv`:** `<unit_id>,written,<highest question number so far>,`. This line makes the unit done; write it last. Tell the user in one line which unit is done and how many remain, unless nobody is there to read it.

### Step 5. After the last unit

Read the notes and `9900-open-questions.md` once. Do not read the parts.

1. **Questions the export has since answered** (`Settles Q<n>` lines): take the question out of `9900-open-questions.md`, append it to `9930-questions-the-export-answered.md` with its heading, its `Raised in:` line and `Settled by: <the fact, with the note's marker>. See the section "<heading>".`, and change the pointer in the part that raised it. Its number is not used again.
2. **Decisions needed**: write `6000-decisions.md` from the `- Decision:` lines, as `references/document-structure.md` lays out. If there is none: `No decision is needed beyond the open questions.`
3. **Fields on the tab, once each**: from the `tab_fields` notes and the header's counts, check that the number of rows in the fields tables plus the rows under Unresolved equals the number of FSD items the overview packet gave. If not, find the unit and correct it.
4. **The opening**: write `0000-summary.md` as laid out in `references/document-structure.md`, from the notes. Line 1 is exactly `Status: draft, not reviewed`.
5. If `9920-not-described.md` has no line starting `- `, append `Nothing was left out.`; if `5000-unresolved.md` has no row, replace its table header by `Every item of the FSD is supplied.`; if `9930-…` has no question, append `None.`

### Step 6. Assemble and check

```
java -jar build/obi2pbi.jar assemble-doc work/<tab>/to-be.md
java -jar build/obi2pbi.jar check-doc work/<tab>/to-be.md --slice work/<tab>/slice
```

`assemble-doc` exits with 3 and writes nothing when the document holds an answer typed after `Answer:` that the answers file lacks: take it in first (`references/answers.md`). `check-doc` exits with 0 when there is no finding; otherwise the findings are in `<doc>.md.check.csv`, each with the unit to correct, and counted per unit in what is printed. Correct in the parts, assemble, check again, until the exit code is 0. What each finding means and what to do about it is in `../obiee-as-is/references/assemble.md`, "Fixing findings" (the as-is skill's folder is beside this skill's); the two that matter most here:

- `not_covered`: a required row is cited nowhere. If the tab uses it, describe it in its unit's part; if not, add it to `9920-not-described.md` with `not used by this tab`.
- `quote_not_in_packet`: something of yours is between backticks or in a fence. A proposal goes in an indented block under its `Proposal` line.

The note that the fixed parts (`1000-new-tables`, `2000-old-to-new` and so on) belong to no unit is expected. Then append to the progress file: `_document,assembled,<last question number>,`.

### Step 7. Hand over

The design is a draft until it has been reviewed. Tell the user: the path and that its status is "draft, not reviewed"; what `check-doc` printed; how many new tables and views, model tables and measures; how many FSD items are supplied and how many are unresolved, by name; how many decisions are needed; that everything marked "Proposal" waits for a signature and nothing has been agreed; and that the review comes next, best in a new chat. Do not call the design ready, complete or final.

## Open questions and decisions

The form is the one the as-is skill uses, with one more line when the question is a decision:

```
### Q4. Targets can be typed in by users in OBIEE. Should the new tab allow that, and where would typed values be stored?
Raised in: table-02-fact-target
Why it matters: the measure "Revenue" of "Fact Target" is recorded as writeable. A Power BI measure cannot be typed into. If users still enter targets, something outside the model has to take them.
Options: (a) targets become read-only in the tab and are loaded into the new table SAMP_TARGETS_F by a process to be named; (b) a separate entry form writes to that table; (c) writing back is no longer needed. Like for like would be (b).
Assumed meanwhile: nothing. The measure is designed as read from the new table; no write-back is designed.
How to get the answer: find, in the FSD or with its author, whether the tab is meant to take target entries (the FSD speaks of targets at p15), and bring back which of the three options applies and, for (a), the name of the process that loads SAMP_TARGETS_F.
Answer:
```

- The lines start exactly with `Raised in:`, `Why it matters:`, `Options:` (decisions only), `Assumed meanwhile:`, `How to get the answer:`, `Answer:`. `Answer:` is the last line and is left empty. A number is used once, ever.
- "How to get the answer" says what to look at, run or decide, where, and what to bring back. It may name what a packet names, point at the FSD by anchor, and give a query as ordinary text. No menu paths or screen names, no backticks. Naming the FSD's author for a choice only they can make is fine; "ask the developer" is not.
- "Assumed meanwhile" says what the design does until the answer comes. It never takes the decision.

## If something is missing or goes wrong

- An input is missing: name it, say how to produce it, stop. Never design from memory or from the as-is document alone.
- A packet shows something the layout has no place for: state it in the part under `#### Also in this packet`, with its marker, and say so in your report.
- You notice an earlier part is wrong: correct it from its packet, append a `- Correction (<unit_id>): …` line to the notes and a `written` line to the progress file.
