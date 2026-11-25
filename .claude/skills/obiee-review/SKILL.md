---
name: obiee-review
description: "Review what a skill wrote before it goes to users. An as-is document and a to-be design are checked against the packets they were written from, one unit at a time; the matches proposed for an FSD are checked item by item against the FSD and a fresh lookup. Findings are logged, clear errors corrected, doubts turned into questions, and only then is the output marked reviewed. Use when the user asks to review or check an as-is document, the matches of an FSD or a to-be design, or after one has been written or changed."
---

# Review a document before it goes to users

## Why this exists

The as-is document was written by an AI, and people will build tables from it. A sentence that reads well and is wrong is the worst outcome of this whole project. Your job is to find such sentences before a user does.

The work is divided. `check-doc` does the mechanical part: it proves that every required row is cited, that every marker is one a packet prints, and that every text between backticks or in a fenced block is in a packet character for character. It cannot tell whether a sentence says what its row says. That is your part, and it is where your effort goes: **meaning**.

Three habits make a review worth having:

- **Do not trust the writer, and do not trust your memory.** A review is only worth anything if it checks the text against the source and not against what someone remembers of the source. So you read each packet again from the file, every time, even if you wrote the section a moment ago and are sure you know the packet. If you wrote this document earlier in this chat, the review is less independent: say so, and record it in the status line (step 7).
- **Read the packet first, the section second.** Form your own view of what the section must say before you see what it says. Then check the section against the packet, never the packet against the section.
- **Settle or ask; never smooth over.** A clear error is corrected from the packet and logged. A doubt you cannot settle from the packet becomes an open question. Nothing is quietly reworded to sound safer.

You are not an editor. Leave correct text alone, however you would have phrased it.

This skill reviews three things. **An as-is document**: the rest of this file. **The matches the trace skill proposed for an FSD** (`work/<tab>/matches.csv`): read `references/matches.md`, beside this file, and follow that instead of the steps below. **A to-be design** (`work/<tab>/to-be.md`): read `references/to-be.md` and follow it; it builds on the rules and the log of this file. The three habits above hold for all three.

## Rules that hold throughout

- One unit at a time: one packet, one section, the findings logged, the unit recorded, then the next. Never read the whole document, a table of the Slice, or anything under `kb/`.
- Corrections are made in the parts, never in `<doc>.md`, which the jar rebuilds from them (`assemble-doc`). To correct a part, change only the lines the finding concerns, in whatever way you can edit a file: replace those lines in place, or read the part and write it back with them changed. Nothing else in the part changes.
- Backticks and fenced blocks are only for text copied exactly from a packet. A correction never adds any other text between them.
- Never change or remove an entry of the answers file. Never remove an open question that has no answer.
- Never write into `slices/` or `kb/`.
- The status line changes to reviewed at the very end, and only if every unit was reviewed and `check-doc` passes. If you stop early for any reason, the document stays a draft.

## Inputs

`<doc>` is `as-is/<estate>/<name>`, and the Slice is `slices/<estate>/<name>` unless the user names another Slice folder; then use that path wherever these steps say `slices/<estate>/<name>`, also after `--slice`. The skills live side by side, so `../obiee-as-is/` is the as-is skill's folder.

| Input | Needed |
|---|---|
| `build/obi2pbi.jar` | Yes. If missing: `./build.sh` or `build.cmd`; stop |
| `slices/<estate>/<name>/units.csv` and `packets/` | Yes. If missing, the document cannot be reviewed; say so and stop |
| `<doc>.parts/` and `<doc>.progress.csv` | Yes. If missing, there is no document written by the as-is skill at that path; list the documents under `as-is/` and stop |
| `../obiee-as-is/references/document-structure.md`, `questions.md` | Yes. What a section must contain, the wording rules; the form of a question, observations |
| `../obiee-as-is/references/assemble.md` | Yes. The files, the commands `assemble-doc` and `check-doc`, what to do about each finding |
| `<doc>.notes.md`, `<doc>.answers.md` | The notes always, but you do not read them before step 5: until then you only append to them, so that the writer's summary does not colour your reading of the packets. The answers file if it exists |

## The review log

`<doc>.md.review.csv`, one line per finding. Put every value in double quotes and double any double quote inside a value.

```
"unit","line","kind","detail","action"
```

- `unit`: the unit id. For a finding about a question: the unit in the question's `Raised in:` line. For the opening: `_summary`; for the observations part: `_observations`.
- `line`: the line number, in the part file, where the statement stood before your correction (for `missing`, the line where you added it). For a finding about a question: the line of its `### Q<n>.` heading in `990-open-questions.md` as the file was when you first read it in step 4, before any correction; note the numbers then.
- `kind`: one of `unsupported`, `wrong`, `missing`, `misquoted`, `unclear`, `bad_question`. `bad_question` is for the form and the how-to of a question. A wrong or unsupported fact inside a question's "Why it matters" or "Assumed meanwhile" is `wrong` or `unsupported`, logged under the question's unit.
- `detail`: the first words of the statement, what was wrong with it, and what the packet says.
- `action`: `corrected: <what it says now>`, `removed`, `added`, `moved to observations`, `question rewritten`, `noted: the export answers it`, or `asked as Q<n>`.

A unit with no finding gets no line.

**Every finding that changes a fact also goes into the notes.** The notes file is only ever added to, and the opening was written from it, so a statement you correct in a section can live on there and return in the summary. Append to `<doc>.notes.md`, under a line `## review`, one line per such finding, in one of three forms. Each names the note it supersedes by that note's first words, so that step 5 can match them; you find those words in step 5, when you read the notes, and until then write `supersedes: to be matched`.

- changed: `- Correction (<unit_id>): <what the section says now, with its marker>; no longer: "<first words of what it said>"; supersedes the note "<first words>"`
- added (`missing`): `- Correction (<unit_id>): added: <the statement, with its marker>; supersedes no note`
- removed: `- Correction (<unit_id>): removed, the packet does not say it: "<first words>"; supersedes the note "<first words>"`

## Steps

### Step 1. See what there is to review

1. Read the two reference files named above, then `units.csv`.
2. Read `<doc>.progress.csv` (columns `unit_id,status,last_question,note`). Only the lines after the last line whose `unit_id` is `_run` count. For each unit, its state is the status on its last line: `written` means it needs review, `reviewed` means it has been reviewed since it was last written.
3. If a unit of `units.csv` has no line at all, the document is not finished. Say which units are missing, say the as-is skill must finish first, and stop.
4. If no unit is `reviewed` yet, this is a fresh review: write the log anew with only its header line. Otherwise leave it and add to it.
5. Tell the user how many units are to be reviewed and, if some are already done, that you are resuming.

### Step 2. Let the jar do the mechanical part first

Run `assemble-doc` and `check-doc` (`assemble.md`). If `assemble-doc` refuses because the user has typed answers into the document, stop the review, say so, and take the answers in first.

`check-doc` covers only the rows marked `(REQUIRED)`, the markers and the quotes. An item of a packet that is not required can be missing from a section without any finding; step 3 is where that is caught.

Deal with what `check-doc` prints, unit by unit, as `assemble.md` says, before you read anything for meaning. Log each correction: a wrong quote as `misquoted`, a marker that no packet states as `unsupported`, a required row not covered as `missing`. Leave the `citation_from_other_unit` warnings for step 3: note which units have them.

### Step 3. Review the units, one at a time

Take the units whose state is `written`, in `units.csv` order. For each, do all of a to d before the next.

**a. Read the packet from the file**, `slices/<estate>/<name>/<packet_file>`, now, whatever you remember of it. Before opening the section, note for yourself what the section must say: the items (lines starting `- ` at the left margin), the lines that end with `(REQUIRED)`, every piece of logic, and **the packet's own counts** ("7 not shown", "5 levels", "4 columns", "2 line(s) for 1 join(s)"). You will count the section's rows against them: in a test with planted errors, every missing item was found this way and in no other.

**b. Read the section**, `<doc>.parts/<nnn>-<unit_id>.md` (`<nnn>` is the unit's position as three digits). Read every line, and in a table every row and every cell: most statements of a section are table rows, and a wrong cell is a wrong statement. If the section has answer markers, read `<doc>.answers.md` too.

**c. Check each statement for meaning.** Each failure is a finding of the kind shown.

1. *The sentence says what its row says.* For each marker, find the packet line that carries it. Does that line say what the sentence or table row says: the same thing, the same number, the same names, no more? If the line says nothing of the kind: `unsupported`. If it says something different (a sum described as an average, the wrong database table, a wrong count, "none" where the packet says "not recorded"): `wrong`.
2. *No statement of fact stands without a marker.* The exceptions: lines the packet marks "no Row identifier"; a fact from an unmarked or `->` line, which carries the marker of the item it belongs to; a sentence that something is absent, in the form `document-structure.md` gives ("No content filter is recorded for this source."), when the packet indeed has no such line; a sentence that starts "In OBIEE generally,"; pointers to other sections or to questions. Anything else without a marker: `unsupported`. If half of a sentence is supported and half is not, cut it back to the supported half.
3. *Nothing was added.* A business meaning, a purpose, a reason, what a user sees, what a formula or condition does when it runs, what an unread attribute might hold, a sum of amounts the writer added up, "not null" where the packet says nullable not stated, a recommendation, any mention of Power BI: `unsupported`, unless a packet line states it. A count of items the section itself lists is fine, and so is a count taken from another unit's packet when that unit's section is named.
4. *Nothing in the packet is missing.* Count the rows of each table and list against the packet's counts. Each item of the packet appears; each piece of logic is quoted; the columns `document-structure.md` gives for this kind of section are there; a block with nothing in it keeps its heading and one sentence. Anything absent: `missing`.
5. *Statements resting on an answer say what the answer says.* Compare with the last entry for that question in the answers file. A difference is `wrong`, and it is the statement you correct, never the answer.
6. *A marker from another unit* (the warnings of step 2). A plain pointer to another section is fine: leave it. So is a marker from an earlier part of the same split unit under "Points to note"; check it against that part's packet. A sentence that says something about the other unit's row cannot be checked against this packet: do not delete it for that reason. Move it: take it out of the section, append it to the notes as `- Observation: …` with its markers, and log it `unsupported`, action `moved to observations`. Step 5 checks it there.
7. *Anything you cannot settle.* If the packet itself can be read two ways and the section picked one without asking: `unclear`.

**d. Act, log, record.**

| Kind | What you do |
|---|---|
| `unsupported` | If the packet supports the statement under another marker, put the right marker. If it supports nothing of the kind, remove the statement; if the point matters to a reader, ask it as an open question instead |
| `wrong` | Restate it from the packet line, keeping the marker |
| `missing` | Add it from the packet, with its marker, in the place `document-structure.md` gives it |
| `misquoted` | Replace the quoted text with the packet's text, exactly |
| `unclear` | Add a new open question with the next number (one more than `last_question` on the last line of the progress file), in the fixed form, to `990-open-questions.md`; in the section, state only what is recorded and point to the question |

Append the findings to the log and the correction lines to the notes. Then append to `<doc>.progress.csv`: `<unit_id>,reviewed,<highest question number>,<number of findings> findings`. If you find an error in a section later (in step 4 or 5, or while checking another unit), that is fine: correct it, log it under its unit, and append a further line `<unit_id>,reviewed,<highest question number>,<n> more findings`. The counts of step 7 are taken from the log, not from these notes. Tell the user in one line: the unit, the number of findings, how many units remain.

### Step 4. Review the open questions, once

Questions are not read with each unit. Read `<doc>.parts/990-open-questions.md` once, now, and note the line number of each heading. Group the questions by their `Raised in:` unit. Read again, from the file, the packet of each unit that raised a question, and only those: a question is judged against its packet, and by now you have read many packets since that one. First check the facts each question states in "Why it matters" and "Assumed meanwhile" against the packet (`wrong`, `unsupported`; correct them in the question). Then the question itself fails as `bad_question` when:

- the packet answers it. State the fact in the section with its marker if it is not there, and add to the question, before its `Answer:` line, a line `Note from the review: the export records <fact>; confirm or correct.` Do not delete it: only an answer closes a question. Action: `noted: the export answers it`;
- "How to get the answer" does not say what to look at or run, where, and what to bring back; or it names a person, a team or a role in place of a step; or it gives a menu path, a tab or a screen name as if verified. It may name what a packet names, describe an action in general terms, and give a query as ordinary text. Rewrite the line. Action: `question rewritten`;
- it has text between backticks that is not copied from a packet. Take the backticks away;
- one of its lines is missing, or `Answer:` is not its last line.

Also read `<doc>.parts/993-questions-the-export-answered.md`: each `Settled by:` line must say what the notes line with the same marker says. If it does not, the question was closed wrongly: move it back to `990-open-questions.md` in the fixed form and log it `bad_question`.

Log each finding (unit and line as defined above), then append to the progress file: `_questions,reviewed,<highest question number>,`.

### Step 5. Review the observations, the opening and the closing parts

1. Read `<doc>.notes.md` now, in full, and your log. For each `- Correction` line you wrote, find the note line of that unit that says what was corrected, and append a line that completes it: `- Correction (<unit_id>) supersedes the note "<first words of that line>"`, or `supersedes no note`. From here on a superseded note does not count.
2. Read `<doc>.parts/980-observations.md`, adding any observation you moved to the notes in step 3. Markers from several units are legitimate here; that is what this part is for. For each observation, read the packet lines that carry its markers (one packet per side) and check that it states only what those lines say, side by side, and concludes nothing. Each side has one marker, and that marker's line must carry the whole of the side's claim; a side the packet marks "no Row identifier" names its section instead. An observation with three sides, or a side resting on two rows, is split into two. A conclusion is `unsupported`: cut the observation back to the two facts, or ask an open question. Log with unit `_observations`.
3. Read `<doc>.parts/000-summary.md`. Each statement of fact under "The model in one page" must match a line of the notes and carry the same marker. A statement with no such note is `unsupported`; one that says what a `- Correction` line says is no longer so is `wrong`. Correct it; log it with unit `_summary`. Leave line 1 as it is for now.
4. Read `<doc>.parts/992-not-described.md`. A line whose reason says nothing ("not relevant", "minor") hides a required row: log `missing` for the row's unit, take the line out, and describe the row in its unit's section from the packet (after the next `check-doc`, the finding `not_covered` names the unit).
5. Append to the progress file: `_summary,reviewed,<highest question number>,`.

### Step 6. Assemble and check

Run `assemble-doc` and `check-doc`, fix every finding, and repeat until the exit code of `check-doc` is 0. A correction of yours may have dropped a marker or changed a quote; this is where that shows.

### Step 7. Mark the document reviewed

Only when all hold: every unit's last progress line says `reviewed`; `_questions` and `_summary` are reviewed; `check-doc` just ended with exit code 0.

1. Count from the log, every line of it, whichever step wrote it: `a` = lines whose action starts `corrected`, `removed`, `added`, `moved`, `question rewritten` or `noted`; `b` = lines whose action starts `asked as`. Every line falls into one of the two, so `a` + `b` is the number of lines in the log. Take `c`, the number of open questions, from the last `check-doc` output.
2. Change line 1 of `000-summary.md` to exactly this form, with the three numbers written as digits, 0 included:
   ```
   Status: reviewed <yyyy-mm-dd> (<a> findings corrected, <b> turned into questions, <c> questions open)
   ```
   If the document was written in this same chat, the form is:
   ```
   Status: reviewed <yyyy-mm-dd> (<a> findings corrected, <b> turned into questions, <c> questions open; reviewed in the same session as the writing)
   ```
   The date is today's. If you do not know it, ask the user, or run `date +%F` (Linux, macOS) or `powershell -c "Get-Date -Format yyyy-MM-dd"` (Windows).
3. Run `assemble-doc` and `check-doc` once more. It must end with exit code 0 and print the reviewed status. If it does not, set line 1 back to `Status: draft, not reviewed`, fix what it reports, and come back to this step.
4. Append to the progress file: `_document,reviewed,<highest question number>,<date>`.

### Step 8. Report

Tell the user: the document's path and its status line; units reviewed; findings by kind; what was corrected and what became a question; the number of open questions and that each says how to find its answer; and, if so, that the review ran in the same session as the writing and an independent one in a new chat would be worth more. If you stopped before step 7, say plainly that the document is still a draft and why.

## Worked example of one finding

Unit `table-02-fact-target`. The packet says:

```
- Logical column "Units": MEASURE; writeable true; mapped in 1 of 1 table sources. [kb:sampleapp::bmm::SAMPLESALES.Fact Target.Units] (REQUIRED)
    - Aggregation rule (default): ` SUM("SAMPLESALES"."Fact Target"."Units")` [kb:sampleapp::bmm::SAMPLESALES.Fact Target.Units#aggr0]
```

Line 9 of the section is the table row for Units. Its second cell reads "Average:" followed by the rule, quoted correctly; its third cell says the measure cannot be typed into. `check-doc` has nothing to report: the markers and the quote are right. Both cells are `wrong`: the rule is a sum, and the packet says writeable true. Both are corrected from the packet line, the log gets

```
"table-02-fact-target","9","wrong","Units is described as an average; the packet's rule is SUM","corrected: Sum"
"table-02-fact-target","9","wrong","Units is said not to be typed into; the packet says writeable true","corrected: yes"
```

and the notes get, under `## review`: `- Correction (table-02-fact-target): Units is summed and can be typed into [kb:sampleapp::bmm::SAMPLESALES.Fact Target.Units]; no longer: Units is an average`.

## If the run is interrupted

Start again at step 1. The progress file shows which units are reviewed; the log already holds their findings. The status line has not been touched, so the document is still a draft, which is correct.
