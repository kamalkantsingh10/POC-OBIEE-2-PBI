---
name: obiee-as-is
description: "Describe one OBIEE subject area as it exists today, in plain language, from its Slice: every table, field, measure, source, join, drill path, variable and security rule, each statement cited, ending in open questions a person can answer. Use when the user asks to describe a subject area, write or regenerate an as-is document, or take in answers to its open questions."
---

# As-is: describe one subject area

## What this produces, and why it works the way it does

You write `as-is/<estate>/<name>.md`: a description of one OBIEE subject area for people who will confirm it and then build from it. Four things matter more than anything else:

- **Complete.** Every row the Slice lists as required is described and cited, or listed under "Not described" with a reason. A program (`check-doc`) verifies this; you do not finish until it passes.
- **Traceable.** Every statement of fact ends with the marker of the Knowledge Base row it came from, copied exactly, and every piece of logic is quoted exactly. `check-doc` compares both with the packets.
- **Honest.** You describe what was recorded. Where the record does not say, you write an open question that tells the reader how to find the answer. You never fill a gap with a likely meaning.
- **Small steps.** You may be running with a short memory. So the work is cut into units; you read one small file (a packet), write one section, record it, and move on. Nothing is kept in your head between units: everything you will need later is written to a file. The run can stop anywhere and be picked up from the files.

The document describes the existing system only. It proposes nothing for Power BI.

## Rules that hold throughout

- The packets are your only source of facts about the subject area. Do not open the other files of the Slice (its `.csv` tables) or anything under `kb/`, apart from `units.csv` and `manifest.csv`, which are short. The jar has already joined everything a section needs into its packet; re-deriving it from the tables is how wrong lineage gets written.
- Never write into `slices/` or `kb/`. If a packet looks wrong, say so in the section and in your final report; the fix belongs in the export or the jar.
- Never read `<doc>.md` whole and never edit it. The jar builds it from the parts (`assemble-doc`).
- Backticks and fenced blocks are only for text copied character for character from a packet. Everything else, including a query you suggest in a question, is ordinary text.
- Never change or remove an entry of the answers file.
- If the user asks you to take in answers, read `references/answers.md` and follow that instead of the steps below.

## Inputs

Write `<doc>` for `as-is/<estate>/<name>`.

| Input | Needed |
|---|---|
| `build/obi2pbi.jar` | Yes. If missing, tell the user to run `./build.sh` (Linux, macOS) or `build.cmd` (Windows), and stop |
| `slices/<estate>/<name>/units.csv`, `manifest.csv`, `packets/` | Yes. If missing, see step 1. The user may name another Slice folder; then use that path wherever these steps say `slices/<estate>/<name>`, also after `--slice` |
| `references/document-structure.md` (beside this file) | Yes. The wording rules, what each kind of section holds, a worked example |
| `references/questions.md` (beside this file) | Yes. What a good question is, its form, observations across sections |
| `references/assemble.md` (beside this file) | Yes. The files of a document, the commands that join and check it, what to do about a finding |
| `<doc>.answers.md` | If it exists: answers given earlier. They are applied again (step 2) |
| `<doc>.progress.csv` | If it exists: where an earlier run stopped |

## Steps

### Step 1. Settle the subject area and its Slice

1. If the user has not named an estate and a subject area, list what exists and ask. `java -jar build/obi2pbi.jar slice kb/<estate>` lists the subject areas of an estate and writes nothing; the folders under `kb/` are the estates.
2. If `kb/<estate>` does not exist, tell the user the Knowledge Base has not been extracted, give the command `java -jar build/obi2pbi.jar extract estates/<estate>`, list the estates that do exist, and stop.
3. If the Slice folder is missing, cut it: `java -jar build/obi2pbi.jar slice kb/<estate> --subject-area "<exact name>"`. The folder name it writes is `<name>`. An unknown name is refused with the list of names; show the user that list and stop.
4. If the Slice exists, read `slices/<estate>/<name>/manifest.csv` (the value `kb_extracted_at`) and the line `extracted_at` of `kb/<estate>/manifest.csv`. If they differ, the Knowledge Base is newer than the Slice: cut the Slice again with the same command.
5. Read `references/document-structure.md`, `references/questions.md` and `references/assemble.md`.
6. Read `slices/<estate>/<name>/units.csv`. It is the list of work, in the order to do it: `unit_id`, `position`, `kind`, `title`, `part`, `parts`, `packet_file`, `line_count`, `required_rows`. The kinds come in this order: the overview, the business tables (`logical_table`), the database tables (`physical_table`, `dbtable-…`), then joins, drill paths, variables, security, reports, gaps. A large table or list is cut into several units (`table-01-fact-revenue-p1`, `-p2`; `parts` says how many). Each is a unit like any other: its own packet, its own part file, its own line of progress.

### Step 2. Decide: new run, resume, or regenerate

Read `<doc>.progress.csv` if it exists. Its columns are `unit_id,status,last_question,note`. Find its last line whose `unit_id` is `_run`; only the lines after that one count. The `note` of that line is the `kb_extracted_at` the run started from.

| What you find | What it is | Do |
|---|---|---|
| No progress file | A new run | Step 3 |
| The `_run` note equals the Slice's `kb_extracted_at`, and some unit has no `written` line | An interrupted run | Tell the user you are resuming and at which unit. Read `990-open-questions.md` once: if it already holds questions whose `Raised in:` line names the unit you are about to write, an earlier attempt at that unit was cut short; use those questions and numbers again and do not add them twice. Go to step 4 at the first unit with no `written` line |
| The same, and every unit is `written` | Already written | If there is no `_document,assembled` line, go to step 5. Otherwise say it is written and offer: review it, take in answers, or write it again. Write it again only if the user asks; then treat it as regenerate |
| The `_run` note differs from the Slice's `kb_extracted_at` | The Knowledge Base changed: regenerate | Read `references/answers.md`, section "Regenerating", and follow it. It sends you back here to step 4 |

### Step 3. Start a new run

Create these files. `<kb_extracted_at>` comes from the Slice's `manifest.csv`.

1. `<doc>.progress.csv`, two lines:
   ```
   unit_id,status,last_question,note
   _run,start,0,<kb_extracted_at>
   ```
   If an answers file exists, use the largest `## Q<n>` number in it in place of 0.
2. `<doc>.notes.md`, one line: `# Notes for the summary`.
3. `<doc>.parts/990-open-questions.md`: the line `## Open questions`, an empty line, then: `These are the points the export could not settle. Each says how to find the answer and ends with an empty answer line. Write the answer on that line, or tell the agent; the document will then be updated.`
4. `<doc>.parts/991-answered-questions.md`: `## Answered questions`, an empty line, `Questions that have been answered. Statements that rest on an answer carry the question's number in their marker.`
5. `<doc>.parts/992-not-described.md`: `## Not described`, an empty line, `Rows of the Knowledge Base that this document deliberately leaves out, each with the reason.`
6. `<doc>.parts/993-questions-the-export-answered.md`: `## Questions the export answered`, an empty line, `Questions raised while one section was written and settled by a later section. Their numbers are not used again.`

### Step 4. Write the units, one at a time

Take the units in `units.csv` order. For each unit, do all of a to g before you look at the next unit. Do not read ahead, and do not read a second packet "for context": what another unit says belongs in that unit's section.

**a. Read the unit's packet**, `slices/<estate>/<name>/<packet_file>`, whole. It is short (120 lines unless the Slice was cut otherwise). Its header gives the unit, the kind, and how many rows of the packet a document must cover. Every fact in it is followed by its marker in square brackets. A line that ends with `(REQUIRED)` states one of the rows that must be covered; the words `(REQUIRED)` are a note to you and are not part of the marker. `-> <identifier>` without brackets only points at another row and is not a marker.

**b. Read the whole notes file, `<doc>.notes.md`,** from its first line to its last, before every unit, even when you believe you remember it. After a resume, or once earlier turns have dropped out of your memory, the notes are the only record of what earlier sections established, where a split unit's previous part stopped, which points and observations are waiting, and which questions were already asked. Reading only its end is not enough: the question you are about to repeat may be in its first lines.

**c. Decide the questions.** Go through the packet against `questions.md`. First check the question lists in the notes: a doubt an earlier question covers is not asked again (point to that question; you may add a `See also:` line to it, as `questions.md` says). The next question number is one more than `last_question` on the last line of the progress file. Give each new question its number now, so the section can point to it. Also look the other way: if this packet settles a question the notes list as asked earlier, say so in the notes (step g); do not touch that question now.

**d. Write the section** to `<doc>.parts/<nnn>-<unit_id>.md`, where `<nnn>` is the unit's `position` as three digits. Follow the entry for the unit's kind in `document-structure.md`, its rule for a unit that was split, and its rules for empty blocks and for saying that something is absent. Before you save, check four things against the packet:

- the marker of every line that ends with `(REQUIRED)` is in the section; count them and compare with the number in the packet's header;
- every item of the packet (a line starting `- ` at the left margin) appears, and where the packet gives a count ("7 not shown", "4 columns") your rows number the same;
- every formula, rule, filter, query and condition is quoted exactly, and nothing stands between backticks or in a fenced block that is not copied from the packet;
- every marker in the section is one this packet prints. Something that needs another unit's row goes to the notes as an observation (step g), not into this section.

If the file already exists (an earlier attempt, or a regeneration), write it anew from the packet; do not patch the old text.

**e. Append the new questions** to `<doc>.parts/990-open-questions.md`, each in the fixed form, with an empty line after it.

**f. Append to `<doc>.parts/992-not-described.md`** one line for each required row of this packet that you deliberately did not describe, with the reason. Usually there is none.

**g. Append to the notes, then to the progress file.** To `<doc>.notes.md`:

```
## <unit_id>: <the section's heading, or the sub-heading a later part of a split unit ended with>
- <one to three lines: what a reader should remember from this section, each with the marker of the fact it rests on>
- Observation: <something that only shows next to another unit's facts; what this packet says, with its marker, and what to set it beside>     (only if there is one)
- Point to note: <for a part of a split unit that is not the last: a point for the last part's "Points to note", with its marker>
- To check in a later part: <a doubt a later part of this split unit may settle>
- Settles Q5: <the fact this packet states that answers the earlier question, with its marker>                      (only if there is one)
- Questions: Q7 what distinguishes All Months Revenue from Revenue; Q8 …        (or "Questions: none")
```

For the `overview` unit, also note when the Knowledge Base was extracted and what the packet says was not examined. For a database table (`dbtable-…`), one of the lines is always: the table's name, how many columns, its key and the key's columns or that no key is recorded, and which sources read it, with the table's marker. The opening is written from these. Then one line to `<doc>.progress.csv`: `<unit_id>,written,<the highest question number used so far>,`. This line is what makes the unit done. Write it last: if the run stops before it, the unit is simply written again.

After each unit, tell the user in one line which unit is done and how many remain, unless nobody is there to read it. Then go on without waiting.

### Step 5. After the last unit: questions, observations, opening

When every unit has a `written` line, read `<doc>.notes.md` and `<doc>.parts/990-open-questions.md` once. Do not read the sections.

1. **Questions the export has since answered.** A question can go stale: asked in one unit, answered by a later packet. For each open question, look in the notes for a `Settles Q<n>` line, or for a note of a later unit that plainly answers it. For each such question:
   - take it out of `990-open-questions.md` and append it to `993-questions-the-export-answered.md` as its heading, its `Raised in:` line, and one line `Settled by: <the fact, as the note states it, with the note's marker>. See the section "<that unit's heading>".`;
   - read the part of the unit the question was raised in, replace the pointer "(open question Q<n>)" by "(see the section <heading>)", write it back, and append a `written` line for that unit to the progress file with the note `Q<n> settled`.
   The question is not deleted and its number is not used again. A question the notes do not clearly settle stays open. A `- To check in a later part` line that no later part settled becomes an open question now, raised in the part that noted it.
2. **Observations.** Write `<doc>.parts/980-observations.md` from the `- Observation:` lines of the notes, as "Observations across sections" in `questions.md` lays out: pair what the units say, each side with the marker its note carries, and conclude nothing.
3. **The opening.** Read the Slice's `manifest.csv` and write `<doc>.parts/000-summary.md` as laid out under "The opening and the closing parts" in `document-structure.md`. Its first line is exactly `Status: draft, not reviewed`. Everything under "The model in one page" comes from the notes, with the markers the notes carry, including the table of database tables. State up front what was not examined or went wrong.
4. Read `<doc>.parts/992-not-described.md`. If it has no line starting `- `, append: `Nothing was left out: every required row is described above.` If `993-questions-the-export-answered.md` has no question, append: `None.`

### Step 6. Assemble and check

Follow `references/assemble.md`: run `assemble-doc`, run `check-doc`, fix every finding in the parts, and repeat until the exit code of `check-doc` is 0. Then append to the progress file: `_document,assembled,<last question number>,`.

### Step 7. Hand over

The document is a draft until it has been reviewed. Tell the user:

- the path of the document, and that its status is "draft, not reviewed";
- the last output of `check-doc`: rows required and covered, citations checked, questions open, warnings left for the review;
- what the document could not see: errors, unread content, reports not examined;
- that the open questions are at the end, each with how to find the answer, and that answers can be typed after `Answer:` or told to the agent;
- that the next step is the review, and that it should be run in a new chat: a reviewer who has just written the text tends to agree with it.

Do not call the document ready, complete or final yourself.

## Worked example of one unit

Unit `table-02-fact-target`, position 4, of `sampleapp` / `sample-sales`; the last progress line is `table-01-fact-revenue-p2,written,7,`.

- a. Read `slices/sampleapp/sample-sales/packets/table-02-fact-target.md` (59 lines; its header says 9 rows must be covered, and 9 lines end with `(REQUIRED)`: the table, its source, the two measures, and the five fields shown to report authors).
- b. Read `as-is/sampleapp/sample-sales.notes.md`. It says the source "SAMP_REVENUE_F" of Fact Revenue names a level of "Sales Rep Hierarchy".
- c. The packet shows both measures as writeable and says nothing on what a target is or what happens to a typed value. One question, Q8.
- d. Write `as-is/sampleapp/sample-sales.parts/004-table-02-fact-target.md` (the example section in `document-structure.md` is the start of it). The markers of all 9 `(REQUIRED)` lines are in it. That this source names no level of "Sales Rep Hierarchy" while Fact Revenue's does is not written here: it needs the other unit's row.
- e. Append Q8 to `990-open-questions.md`.
- f. Nothing to add to `992-not-described.md`.
- g. Append to the notes:
  ```
  ## table-02-fact-target: Fact Target
  - Fact Target holds two measures, Revenue and Units, both summed, both from the database table SAMP_TARGETS_F [kb:sampleapp::bmm::SAMPLESALES.Fact Target.src.SAMP_TARGETS_F].
  - Both measures are writeable: users can type values in [kb:sampleapp::bmm::SAMPLESALES.Fact Target.Revenue] [kb:sampleapp::bmm::SAMPLESALES.Fact Target.Units].
  - Observation: the level of detail of the source "SAMP_TARGETS_F" names no level of "Sales Rep Hierarchy" [kb:sampleapp::bmm::SAMPLESALES.Fact Target.src.SAMP_TARGETS_F].
  - Questions: Q8 what the target measures hold and what happens to typed values
  ```
  and to the progress file: `table-02-fact-target,written,8,`.

## If something is missing or goes wrong

- An input in the table above is missing: name it, say how to produce it, and stop. Do not write a document from memory or from another subject area.
- A packet named in `units.csv` is missing: the Slice is damaged. Cut it again (step 1.3) and start the unit again.
- A unit's packet has nothing to describe (for example no reports): still write its section, in one or two sentences saying so, and record the unit.
- You notice that an earlier section is wrong: correct that part from its packet (read the packet, change the part), append a `- Correction (<unit_id>): <what it says now, with its marker>` line to the notes, and append a `written` line for the unit. Do not carry the correction in your head.
