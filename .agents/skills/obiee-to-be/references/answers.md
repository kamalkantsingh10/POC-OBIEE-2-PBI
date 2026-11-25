# To-be: taking in answers, and writing the design again

An answer is the user's. Once recorded it is never changed or removed, and it is applied again on every later run. A statement that rests on an answer ends with `[answer:Q<n>]`, so that nobody reads it as something OBIEE recorded. An answer to a decision is how the decision is taken.

`<doc>` is `work/<tab>/to-be`. The answers file, `<doc>.answers.md`, starts with `# Answers`; each answer is one entry added at the end:

```
## Q4
Question: Targets can be typed in by users in OBIEE. Should the new tab allow that?
Raised in: table-02-fact-target
Answered by: the Operator, 2026-11-25
Answer: (a): read-only; the planning team's load fills SAMP_TARGETS_F.
```

A corrected answer is a further `## Q4` entry at the end; the last entry for a number applies.

## Taking in answers

1. **Collect.** Run `java -jar build/obi2pbi.jar assemble-doc work/<tab>/to-be.md`. Exit code 3 means it wrote nothing and prints each question whose typed answer is not recorded yet, with the answer as typed. Answers may also be given in the chat; match each to its number, and ask if you cannot tell. "I don't know" is not an answer.
2. **Record every answer first**: append its entry to `<doc>.answers.md`, before changing any part.
3. **For each answer:**
   - move the question from `9900-open-questions.md` to `9910-answered-questions.md`, in the same form, with the answer after `Answer:`;
   - read the packet of the unit in `Raised in:`, then that unit's part, and change only what the answer bears on. A decision that is now taken: state the chosen option, ending with `[answer:Q<n>]`; where a measure had `Proposal: none until decision Q<n> is taken.`, write the proposal the chosen option leads to, still as a proposal. Something that was missing and is now supplied: state it with `[answer:Q<n>]` in place of "missing";
   - if the answer contradicts a packet, do not overwrite the fact: state both, each with its marker, and ask a new question;
   - in `6000-decisions.md`, add to the decision's row `decided: <the option> [answer:Q<n>]`; update `0000-summary.md` if it mentions the point, and set its line 1 back to `Status: draft, not reviewed` if it said reviewed;
   - append to the notes, under `## answers`: `- Q<n> answered: <the statement> [answer:Q<n>]`; and to the progress file: `<unit_id>,written,<highest question number>,answer Q<n>`, so that the review looks at that part again.
4. **Assemble and check** (step 6 of the skill) until `check-doc` has no finding.
5. **Report**: the answers recorded, the parts changed, the decisions now taken and those still open. The design is a draft until the changed parts are reviewed.

## Regenerating: the Knowledge Base or the decisions changed

The parts are written again from the new packets; questions and answers are kept.

1. If `<doc>.md` exists, run `assemble-doc`; record any typed answers it lists (steps 1 and 2 above). It also lists the parts that exist.
2. Read `<doc>.answers.md`, `9900-open-questions.md` and `9910-answered-questions.md`; note the highest question number in them and in the progress file.
3. Append to the progress file `_run,start,<highest question number>,<kb_extracted_at> | <decisions>`. Write the notes file anew, and the fixed parts `2000-old-to-new.md`, `5000-unresolved.md`, `9920-not-described.md` and `9930-questions-the-export-answered.md` anew as in step 3 of the skill. A part of a unit that is no longer in `units.csv` is written as an empty file, so that it adds nothing.
4. Do step 4 of the skill for every unit. For each unit, after reading its packet, read the two question parts and take the questions raised in it (unit ids may have changed; a question belongs to the unit whose packet now holds what it asks about):
   - open, and the doubt is still there: keep it, with its number;
   - open, and the packet now settles it: move it to `9930-questions-the-export-answered.md`;
   - answered, and the packet does not contradict it and its subject is still in the Slice: apply the answer, with `[answer:Q<n>]`;
   - answered, and it no longer fits (the item is no longer confirmed, the table has gone, the packet says otherwise): do not apply it; ask again under a new number, with a line above `Answer:`: `Earlier answer (Q<n>, <date>): <the answer> — no longer fits because <what changed>`.
5. Finish with steps 5 to 7 of the skill, and say in the report which answers were applied again and which questions were asked again.
