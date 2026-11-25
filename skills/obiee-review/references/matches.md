# Reviewing the matches proposed for an FSD

## Why, and what you may do

The trace skill has proposed an OBIEE column for each item of an FSD, with a score and a reason. A person, the Operator, will confirm or reject each one, and they will lean on the score and the reason. A confident score on a wrong column, or a reason that is not true, is how a wrong match gets confirmed. You go through every item before the Operator does.

- **You check each match against the FSD's own words and a lookup you run yourself.** Not against the basis, and not against what you remember: the basis is the claim under review.
- **You lower, you do not raise.** A match you doubt gets a lower score and the reason added to its basis. If nothing supports it, it becomes `unmatched`. A score you think too low is logged and left for the Operator.
- **You never write `decisions.csv`,** and never `confirmed` or `rejected` anywhere. Never change an entry of the answers file. Never write into `kb/` or `slices/`.
- The status changes to reviewed only at the very end.

## Inputs

All in `work/<tab>/` unless said otherwise. `<estate>` and the subject area are on the lines of `trace-status.md`.

| Input | Needed |
|---|---|
| `build/obi2pbi.jar`, `kb/<estate>/` | Yes |
| `fsd.md` (and `fsd.sections/` when it is long), `fsd_items.csv`, `matches.csv`, `progress.csv`, `trace-status.md` | Yes. If one is missing, the trace has not been written or not finished: say which, and stop |
| `../obiee-trace/references/scoring.md` (relative to this skill's folder: the trace skill's folder is beside it) | Yes. The bands, and what a basis must hold |
| `docs/skill-conventions.md`, section 10 | If you need the exact columns and values of a file |
| `trace-questions.md`, `trace.answers.md` | If they exist |

## The review log

`work/<tab>/matches.review.csv`, one line per finding, every value in double quotes:

```
"item_id","row_id","kind","detail","action"
```

- `kind`: `wrong_column` (the column is not what the FSD's words mean), `basis_untrue` (the basis says something the lookup or the FSD does not show), `score_too_high` (the evidence earns a lower band or a lower place in it), `forced_match` (nothing supports it; it should be unmatched), `missing_column` (a calculation lacks one of the columns it is built from), `missing_item` (the FSD has an item that needs data and `fsd_items.csv` has no row for it), `missed_candidate` (the item is unmatched, or matched low, and your lookup shows a column that fits better), `score_too_low`, `bad_question`.
- `detail`: what the FSD says at the anchor, what your lookup showed, and what the row claimed.
- `action`: `lowered from <n> to <m>`, `made unmatched`, `basis corrected`, `row added`, `item added`, `question rewritten`, `asked as Q<n>`, or `left for the Operator`.

An item with no finding gets no line.

## Steps

### Step 1. See what there is to review

1. Read `scoring.md`, then `trace-status.md` and `progress.csv`. Only the lines after the last `_run` line count. An item's state is the status on its last line: `matched` needs review, `reviewed` is done. If there is no `_matches,checked` line, the trace is not finished: say so and stop.
2. Run `java -jar build/obi2pbi.jar check-matches work/<tab> --kb kb/<estate>`. If it reports a failure, the files are not yet fit to review: correct what is plainly mechanical (a mistyped anchor or identifier, a score outside its band) and log it as `basis_untrue` or `score_too_high` as fits; anything else goes back to the trace skill. Note the warnings; you will look at each in step 3.
3. If no item is `reviewed` yet, write the log anew with only its header. Otherwise add to it.

### Step 2. Is every item of the FSD listed?

Read the FSD once more, a section at a time (the whole of `fsd.md` if it has no sections), and `fsd_items.csv`. For each filter, figure, chart axis and series, table row and column, access rule and other sentence that needs a field, there must be a row. For one that is missing: log `missing_item`, append the row to `fsd_items.csv`, and append an `unmatched` row to `matches.csv` with confidence 0 and the basis `not searched: added by the review; the trace skill must match it`. Then it is for the trace skill to match; say so in your report.

### Step 3. Review the items, three at a time

For each item whose state is `matched`, in `fsd_items.csv` order:

**a. Read the FSD's words at the item's anchors**, from the file. Decide for yourself what kind of thing is wanted, and what its definition says, before you look at the match.

**b. Read the item's rows in `matches.csv`.**

**c. Run the lookups again.** The basis names each term searched; run `find-columns` with those terms and the subject area, and one more with the words you would have used yourself. Read what it prints for the proposed column and for the two or three candidates around it.

**d. Check, in this order.**

1. *Is it the right kind of thing?* A number for a figure, summed or counted as the FSD's definition wants; the right table (actuals or targets); a name where the FSD wants a name and not its key. If not: `wrong_column`.
2. *Is every word of the basis true?* The group and the words found, the aggregation, the table, the formula, the report column, what the as-is section is said to say (read that one part if the basis leans on it), what an answer is said to say (read the entry). Anything the lookup, the part or the entry does not show: `basis_untrue`.
3. *Does the score fit the evidence?* Apply `scoring.md` to what you have just seen. A name alone is at most 59. Two candidates about as good: at most 59. A piece of a calculation whose combination is the writer's reading: at most 79. Another subject area: at most 59. Higher than the evidence earns: `score_too_high`.
4. *Is anything supporting it at all?* If the only link is a shared word and the definition points elsewhere: `forced_match`.
5. *A calculation:* does the FSD's definition name something no row covers? `missing_column`. Does the basis say how the pieces combine?
6. *An unmatched item:* did your own lookup show a column that fits? `missed_candidate`. Is the basis honest about what was searched?
7. *A warning of `check-matches` on this item:* `derived_column` should be said in the basis; `differs_from_existing_report` is strong evidence against the match unless the basis answers it.

**e. Act.**

| Kind | What you do |
|---|---|
| `score_too_high` | Lower the confidence to what the evidence earns and append to the basis: `; review: lowered from <n> because <reason>` |
| `basis_untrue` | Take the untrue part out of the basis, append `; review: <what the lookup shows>`, and score again on what is left |
| `wrong_column`, `forced_match` | Make the row `unmatched`: empty `row_id`, confidence below 30, and a basis that keeps what was proposed and says why it does not fit. If your lookup shows the right column, say so in the basis; do not propose it yourself above 59 |
| `missing_column` | Append a `proposed` row for the column your lookup shows, scored by `scoring.md`, basis starting `added by the review: ` |
| `missed_candidate` | Append to the basis `; review: <table and column> fits <why>`; action `left for the Operator`, or, if the fit is plain, replace the unmatched row by a `proposed` row scored at most 59 |
| `score_too_low` | Change nothing; action `left for the Operator` |

To change rows of `matches.csv`, change only the lines of that item. If a doubt cannot be settled from the FSD and the lookup, lower the score into the 30-59 band and ask a question in `trace-questions.md` in the trace skill's form (`asked as Q<n>`; the next number is one more than `last_question` on the last line of `progress.csv`).

**f. Log, then record:** append the findings to the log, then `<item_id>,reviewed,<last question number>,<n> findings` to `progress.csv`.

### Step 4. Review the open questions, once

Read `trace-questions.md`. Each open question must name its item and FSD anchor, state only what the FSD and the lookups show, and say concretely how to get the answer: what to look at or decide, where, and what to bring back, with no menu paths and no person named. One that fails is `bad_question`: rewrite the failing line, action `question rewritten`. Never remove a question; never touch an `Answer:` line that has text.

### Step 5. Check again, and set the status

1. Run `check-matches` again. It must end with exit code 0. If your changes caused a failure, correct it.
2. Only when every item's last progress line says `reviewed` and the check has no failure: count from the log `a` = lines whose action is not `asked as …` and not `left for the Operator`, `b` = lines with `asked as`, and take `c`, the number of items that are now unmatched, from `matches.csv`. Change line 1 of `trace-status.md` to exactly:
   ```
   Status: reviewed <yyyy-mm-dd> (<a> matches corrected, <b> turned into questions, <c> items unmatched)
   ```
   with `; reviewed in the same session as the tracing` before the closing bracket if you also wrote the matches in this chat. Leave the other lines of the file. Append to `progress.csv`: `_matches,reviewed,<last question number>,<date>`.
3. Report: the status line; items reviewed; findings by kind; which scores were lowered and why; which items are unmatched; the open questions. Then say what comes next: the Operator reads `matches.csv`, writes `decisions.csv` themselves (`item_id`, `decision`, `row_id`, `note`), and runs `check-matches` again. You have confirmed nothing.

## Worked example

Item `F2`, FSD text "Office: The sales office". The row proposes `Dim Offices.Office Key` at 85 with the basis `searched "office"; whole name; table Dim Offices`. Your lookup for "office" shows `Office Dsc` and `Office Key` in Dim Offices, both "all words", neither a whole name. The FSD wants the office as a slicer value, a name; the key is its identifier. So: `basis_untrue` (the lookup does not say "whole name") and `wrong_column`. The row becomes `unmatched` with confidence 20 and the basis `proposed Dim Offices.Office Key; review: the lookup shows it under "all words", not as a whole name, and it is the key, while the FSD asks for the office itself; Dim Offices.Office Dsc fits better`, and the log gets two lines, both with the action `made unmatched`.
