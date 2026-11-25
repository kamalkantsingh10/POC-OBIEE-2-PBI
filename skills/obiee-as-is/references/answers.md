# Answers: taking them in, and keeping them when the document is written again

An answer is the user's. Once recorded it is never changed or removed, by any skill, and it survives every regeneration of the document. A statement that rests on an answer says so with the marker `[answer:Q<n>]`, so that nobody reads it as something OBIEE recorded.

Read `assemble.md` (beside this page) first: it names the files and gives the two commands, `assemble-doc` and `check-doc`. `<doc>` is `as-is/<estate>/<name>`.

## Inputs

`<doc>.md`, `<doc>.parts/`, `<doc>.progress.csv`, the Slice `slices/<estate>/<name>/`, and `<doc>.answers.md` if it exists. If the document or its parts are missing, there is nothing to take answers into: say so and stop.

## The answers file

`<doc>.answers.md` starts with the line `# Answers`. Each answer is one entry, added at the end:

```
## Q4
Question: What currency is plain Revenue in?
Raised in: table-01-fact-revenue
Answered by: finance, 2026-11-24
Answer: Euro.
```

`Question:` is the question's heading text. `Raised in:` is copied from the question. `Answered by:` is who and when, as far as the user said (otherwise "the user" and today's date). `Answer:` is the user's words, unchanged.

If the user later corrects an answer, add a further `## Q4` entry at the end with the new date. The earlier entry stays; the last entry for a number is the one that applies.

## Taking in answers

**1. Collect.** Answers come two ways:

- typed into the document: run `java -jar build/obi2pbi.jar assemble-doc as-is/<estate>/<name>.md`. If it ends with exit code 3, it has written nothing and prints each question whose typed answer is not in the answers file yet, with the answer as typed (`Q4: Euro.`). Those are the answers to take in. If it ends with 0, no answer was waiting in the document;
- said in the chat: match each to its question number. If you cannot tell which question an answer belongs to, ask.

"I don't know" or "to be checked" is not an answer: leave the question open and say so.

**2. Record each answer first.** Append its entry to `<doc>.answers.md` (create the file with `# Answers` if it is not there). Do this for all collected answers before changing any section, so that nothing is lost if the run stops.

**3. For each answer, in turn:**

a. Read `990-open-questions.md`. Take the question out of it (write the file back without that question) and append the question to `991-answered-questions.md`, in the same form, with the user's answer after `Answer:`. For a corrected answer the question is already there: put the new answer after its `Answer:` in place of the old one (the answers file keeps both).

b. Read the packet of the unit named in `Raised in:`, then that unit's part. Change only the statements the answer bears on: where the section said "not recorded (open question Q4)", state what the answer says and end the statement with `[answer:Q4]`. Leave everything else in the section as it is. Write the part back.

c. If the answer disagrees with what the packet records, do not overwrite the recorded fact. State both: what the export records, with its marker, and what the answer says, with its answer marker. If someone has to decide between them, add a new open question saying how to check.

d. If the answer is partial or raises a further doubt, add a new question with the next number (one more than `last_question` on the last line of the progress file).

e. Read `000-summary.md`. If it mentions the question or the point, update it the same way and write it back. If its first line says reviewed, set it to `Status: draft, not reviewed`: the text has changed since the review.

f. Append to `<doc>.notes.md` under a line `## answers`: `- Q4 answered: <the statement> [answer:Q4]`.

g. Append to `<doc>.progress.csv`: `<unit_id>,written,<highest question number>,answer Q4`. The `written` line tells the review to look at this section again.

**4. Assemble and check** as in `assemble.md`, until `assemble-doc` writes the document and `check-doc` has no finding.

**5. Report**: which answers were recorded, which sections changed, how many questions are still open, and that the document is a draft until the changed sections have been reviewed.

## Regenerating: writing the document again after the Knowledge Base changed

The sections are written again from the new packets. The questions and the answers are kept and applied again.

**1.** If `<doc>.md` exists, run `assemble-doc`. It lists the parts that exist, and it refuses (exit code 3) if the document holds a typed answer that is not recorded: record those first (steps 1 and 2 above) and run it again.

**2.** Read `<doc>.answers.md`, `990-open-questions.md`, `991-answered-questions.md` and `993-questions-the-export-answered.md`. Find the highest question number in any of them and in the progress file.
If the two question parts do not exist (the working files were deleted), create them as in step 3 of the skill, and for each entry of the answers file append a question to `991-answered-questions.md` built from the entry: its heading from `Question:`, `Raised in:`, `Why it matters: not kept from the earlier run.`, `How to get the answer: answered already.`, and `Answer:` with the answer.

**3.** Start the new run:
- append to `<doc>.progress.csv` (create it with its header line if missing): `_run,start,<highest question number>,<kb_extracted_at of the Slice>`;
- write `<doc>.notes.md` anew with its one heading line, and `992-not-described.md` and `993-questions-the-export-answered.md` anew as in step 3 of the skill. A question that was in the old `993` part is not carried over: if the doubt comes up again it is a new question with a new number;
- look at the parts `assemble-doc` listed in step 1. A part numbered 001 to 989 whose name is not `<nnn>-<unit_id>.md` of a unit in the new `units.csv` belongs to a unit that no longer exists: write it as an empty file, so that it adds nothing when the parts are joined.

**4.** Go back to the skill, step 4, and write every unit. For each unit, after reading its packet and before deciding the questions, read the two question parts and take the questions whose `Raised in:` line names this unit. The unit ids may have changed since the question was asked (a table that is now cut into `-p1`, `-p2`): a question belongs to the unit whose packet now holds what it asks about, and when you keep it you correct its `Raised in:` line:

| The question is | And the new packet | Do |
|---|---|---|
| open | still leaves the doubt | Keep it as it is, with its number. Point to it from the section. Do not add it again |
| open | now settles it | Take it out of `990-open-questions.md` and append it to `993-questions-the-export-answered.md` with a `Settled by:` line (step 5.1 of the skill). State the fact in the section with its marker |
| answered | does not contradict the answer, and what it is about still exists | Apply the answer: state it in the section with `[answer:Q<n>]` |
| answered | contradicts it, or what it was about is gone | Do not apply it. Add a new open question with a new number that asks again, with one more line, placed before its `Answer:` line: `Earlier answer (Q<n>, <date>): <the answer> — no longer fits because <what changed>`. The old question stays under Answered questions and its entry stays in the answers file |

New doubts get new numbers, as always. A number is never used again.

**5.** When all units are written, finish with steps 5 to 7 of the skill. In the report, list: answers applied again, questions asked again because the answer no longer fits, questions the export now answers, and any answered question whose unit no longer exists (its answer is kept in the answers file and applied nowhere).
