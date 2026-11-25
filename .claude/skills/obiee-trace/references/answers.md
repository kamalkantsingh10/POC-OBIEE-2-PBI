# Trace: taking in answers, and what to do when the FSD changed

An answer is the user's. Once recorded it is never changed or removed, and it is applied again on every later run. A match that rests on an answer says so in its basis (`answer Q<n>`), so that nobody reads it as something found in OBIEE.

All files are in `work/<tab>/`.

## The answers file

`trace.answers.md` starts with the line `# Answers`. Each answer is one entry, added at the end:

```
## Q2
Question: Does "Net revenue" mean Revenue minus the discount value, or is Revenue already net?
Item: K1
FSD anchor: t2.r2.c2 t2.r2.c3
Answered by: the Operator, 2026-11-24
Answer: Revenue is before discounts; net revenue is Revenue minus Discnt Value.
```

`Answer:` is the user's words, unchanged. A corrected answer is a further `## Q<n>` entry at the end; the last entry for a number applies.

## Taking in answers

**1. Collect.** Read `trace-questions.md`. Under `## Open questions`, every question whose `Answer:` line has text has been answered there. Answers may also be given in the chat: match each to its question number, and ask if you cannot tell. "I don't know" is not an answer: leave the question open.

**2. Record every answer first.** Append its entry to `trace.answers.md` (create the file with `# Answers` if it is not there), before you change anything else, so that nothing is lost if the run stops.

**3. For each answer:**

a. In `trace-questions.md`, move the question, with its answer after `Answer:`, from under `## Open questions` to under `## Answered questions`. Change nothing else in the file.

b. Do step 4 of the skill again for the question's item, with the answer as evidence: run the lookups, and decide. An answer that names the column, or settles which of two candidates is meant, earns the 90-100 band for that column; an answer that says no column exists makes the item `unmatched`; an answer that changes how a calculation is built changes its rows.

c. Replace the item's rows in `matches.csv` (read the file, write it back with only that item's rows changed, in the same place). Each changed basis says `answer Q<n>` and what the answer said. If the answer contradicts what the lookup shows (it names a column that `find-columns` does not find), do not invent a `row_id`: keep the item as it was, and ask a new question saying what was looked for.

d. Append to `progress.csv`: `<item_id>,matched,<last question number>,answer Q<n>`. That line also tells the review to look at the item again.

**4.** If line 1 of `trace-status.md` says reviewed, set it back to `Status: draft, not reviewed`: the matches have changed since the review.

**5.** Run `check-matches` until it reports no failure (step 5 of the skill), and report: the answers recorded, the items changed, the questions still open. You still write no decision: the Operator confirms.

## When the FSD changed

`fsd.manifest.csv` shows another `source_sha256` than the `_run` line of `progress.csv`. Anchors may have moved, and items may have been added, changed or dropped.

1. Take in any answers typed into `trace-questions.md` (steps 1 and 2 above).
2. Append to `progress.csv`: `_run,start,<last question number>,<new source_sha256> | <subject area>`. Write `fsd_items.csv` and `matches.csv` anew with their header rows, and set line 1 of `trace-status.md` to `Status: draft, not reviewed`. Leave `trace-questions.md`, `trace.answers.md` and `decisions.csv` as they are.
3. Do steps 2.4, 3 and 4 of the skill for the whole FSD. Use the same `item_id` for an item that is still there.
4. For each recorded answer, find its item by `Item:`. If the item is still in the FSD and its words at the new anchors say the same, apply the answer as in step 3b above and correct the question's `FSD anchor:` line. If the item is gone or its definition has changed, do not apply the answer: ask again under a new number, with a line above `Answer:` saying `Earlier answer (Q<n>, <date>): <the answer> — the FSD has changed here`.
5. An open question whose item is gone is moved under a third heading at the end of `trace-questions.md`, `## No longer asked`, with one more line: `Dropped: the FSD no longer has this item.` Nothing is written to the answers file for it.
6. Finish with step 5 of the skill. In the report, say which items are new, which are gone, and that decisions the Operator recorded for changed items need another look.
