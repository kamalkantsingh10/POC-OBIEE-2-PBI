---
name: obiee-trace
description: "Trace a functional specification (FSD) for a new report tab back to OBIEE: list every item of the FSD that needs data and propose, for each, the OBIEE column behind it with a confidence score and the reason, or say that none fits. Use when the user asks to trace an FSD, match an FSD to OBIEE columns, propose matches, or take in answers to trace questions."
---

# Trace: from an FSD to proposed OBIEE columns

## What this produces, and why it works the way it does

An FSD describes a new report tab in business words: filters, figures, charts, tables. Before anyone can build it, somebody has to say which OBIEE column stands behind each of those words. That is slow expert work. You do the first pass: you list the FSD's items and, for each, propose the column you think it means, say how sure you are and why, or say plainly that you found none.

You propose. **Only the Operator confirms.** A person reads your proposals, checks them, and writes the verdicts in a file of their own. Everything downstream uses confirmed matches only. So your job is not to be right at any price; it is to be honest about what the evidence is:

- **A matching name is not proof of the same meaning.** "Revenue" in the FSD and a column called "Revenue" may be different things (actual or target, gross or net). The name gets a candidate onto the list; the score comes from the evidence.
- **Nothing is forced.** An item with no fitting column is written `unmatched`. A wrong match that looks confident costs far more than an honest gap.
- **The reason is written down.** Every row has a basis a reviewer can check against the same lookup you ran.
- **Small steps.** You may be running with a short memory. You take a few items, look them up, write their rows, record them, and move on. The run can stop anywhere and be picked up from the files.

## Rules that hold throughout

- You learn about OBIEE only through the jar's `find-columns`, and, where it exists, one section of the as-is document at a time (step 4c). Never open a file under `kb/` or `slices/` to search it, apart from the two short files named below. Never write into `kb/` or `slices/`.
- You refer to the FSD only by anchor. Never edit `fsd.md`.
- You write `proposed` or `unmatched`, never `confirmed` or `rejected`, and you never create or change `decisions.csv`. It is the Operator's.
- Never change or remove an entry of the answers file.
- The exact columns and values of the files you write are in `docs/skill-conventions.md`, section 10, and repeated below. A program (`check-matches`) checks them; you do not finish until it reports no failure.
- CSV files: UTF-8, a header row, every value in double quotes, a double quote inside a value doubled. Rows in FSD order.

## Inputs

`<tab>` is the folder of one FSD under `work/`; `<estate>` is the estate whose Knowledge Base is searched.

| Input | Needed |
|---|---|
| `build/obi2pbi.jar` | Yes. If missing: `./build.sh` or `build.cmd`; stop |
| `work/<tab>/fsd.md` and `fsd.manifest.csv` | Yes. If missing and `work/<tab>/fsd.docx` is there, run `java -jar build/obi2pbi.jar fsd-text work/<tab>/fsd.docx`. If neither is there, say that the FSD must be put there as `fsd.docx`, and stop |
| `kb/<estate>/manifest.csv` | Yes. If missing, the export has not been read: give `java -jar build/obi2pbi.jar extract estates/<estate>`, list the folders under `kb/`, and stop |
| `references/scoring.md` (beside this file) | Yes. What each confidence band means and how to word a basis |
| `work/<tab>/trace.answers.md`, `progress.csv` | If they exist: earlier answers, and where an earlier run stopped |
| `as-is/<estate>/<name>.md` with its `.parts/` folder, and `slices/<estate>/<name>/units.csv` | If they exist: the description of the subject area, used in step 4c |

## The files you write, all in `work/<tab>/`

| File | Holds |
|---|---|
| `fsd_items.csv` | `item_id`, `kind`, `fsd_anchor`, `fsd_text`, `existing_id`: one row per item that needs data, plus a `replaces` row |
| `matches.csv` | `item_id`, `fsd_anchor`, `fsd_text`, `row_id`, `confidence`, `basis`, `status`: one row per column an item needs |
| `trace-questions.md` | Your open questions, and the answered ones |
| `trace.answers.md` | The user's answers, added to and never changed |
| `progress.csv` | `unit,status,last_question,note`: lines are added at the end, never changed |
| `trace-status.md` | Line 1 is the status: `Status: draft, not reviewed` until the review has passed |

## Steps

### Step 1. Read the FSD's shape and decide: new run, resume, or FSD changed

1. Read `work/<tab>/fsd.manifest.csv`. Note `source_sha256`, `lines` and `sections`.
2. If `sections` is 0, the FSD is short: read `fsd.md` whole, once. Otherwise read `fsd.sections/index.md` only; you will read one section file at a time and never `fsd.md` whole.
3. Read `work/<tab>/progress.csv` if it exists. Find its last line whose `unit` is `_run`; only the lines after it count. Its `note` is the `source_sha256` and the subject area the run started with, separated by ` | `.
   - No progress file: a new run. Go on to step 2.
   - The same `source_sha256`: an interrupted run. Say where you resume, and continue at the first section with no `listed` line (step 3) or the first item with no `matched` line (step 4).
   - Another `source_sha256`: the FSD has changed. Read `references/answers.md`, "When the FSD changed", and follow it.

### Step 2. Start a new run

1. From what you read in step 1, note three things about the tab: its name; the page or report it says it replaces, if any, with the anchor of that sentence; the subject area it names, if any.
2. **The subject area.** If the FSD names one, use it. If the user named one, use it. Otherwise find it: run `find-columns` without `--subject-area` for the two or three most distinctive figures of the FSD and see in which subject area the best candidates lie. If one subject area clearly holds them, use it and say so in your report; if two compete, ask the user, or, when nobody is there, take the one with more whole-name candidates and raise an open question. `java -jar build/obi2pbi.jar slice kb/<estate>` lists the subject areas.
3. Create the files:
   - `progress.csv`: the header `unit,status,last_question,note` and the line `_run,start,0,<source_sha256> | <subject area>`. If an answers file exists, use the largest `## Q<n>` in it in place of 0.
   - `fsd_items.csv` and `matches.csv`: the header row only.
   - `trace-questions.md`: `# Questions about the trace of <tab>`, an empty line, `## Open questions`, an empty line, `## Answered questions`.
   - `trace-status.md`: `Status: draft, not reviewed`, an empty line, and three lines: `Tab: <tab>`, `Estate: <estate>`, `Subject area: <name>`.
4. **The page the tab replaces.** If the FSD names one, append a row to `fsd_items.csv`: `item_id` `R1`, kind `replaces`, the anchor and words of that sentence, and `existing_id`. You can fill `existing_id` only with a Row identifier you were given or that `find-columns` printed among a candidate's report columns. If `kb/<estate>/manifest.csv` shows no catalog half for the estate, or you cannot determine the identifier, leave it empty and raise an open question that asks for the Row identifier of that page. A `replaces` row gets no match.

### Step 3. List the items, one FSD section at a time

For each section of the FSD in order (a `fsd.sections/` file, or, for a short FSD, each `## ` heading of `fsd.md` in turn), find every item that needs data and append one row per item to `fsd_items.csv`. Then append to `progress.csv`: `section <first anchor>-<last anchor>,listed,<last question number>,<number of items>`.

**What is an item.** Anything the tab cannot show without a field from the data:

- each filter, slicer or prompt (kind `filter`);
- each figure, card or key number: `measure` when it is one number that is added up or counted, `calculation` when the FSD defines it from other figures ("as a percentage of", "divided by", "after discounts");
- for each chart: what is on each axis, each line or bar series, and what it is split or ranked by, each as its own item (`attribute` for what is shown or grouped by, `measure` or `calculation` for the numbers);
- for each table: each row heading and each column, each as its own item;
- each requirement about who may see what ("each manager sees only their own offices"): kind `filter`, because it restricts rows by a field;
- any other sentence that needs a field ("amounts are shown in the user's preferred currency").

A sentence that needs no field (purpose, audience without a restriction, layout, colours, who maintains something) is not an item.

**How to write the row.**

- `item_id`: the FSD's own number when the item has one (`F1`, `K3`). Otherwise the number of the thing it belongs to and a word: `V2-office`, `V1-month`, `S1-own-offices`, `N1-currency`. Unique; letters, digits, `-` and `_` only.
- `fsd_anchor`: the anchor where the item is named, without brackets (`t2.r2.c2`). If its definition is in another cell or paragraph, add that anchor after a space (`t2.r2.c2 t2.r2.c3`). Several items may share an anchor (the items of one sentence).
- `fsd_text`: the item's words copied from `fsd.md`: for a table row, the name and the definition joined with ": "; for an item inside a sentence, the words that name it.
- The same figure used twice (a card and a chart line) is two items. The second one's match may say "the same as K1".

### Step 4. Match the items, a few at a time

Read `fsd_items.csv` (it is your list of work). Take the items with no `matched` line in `progress.csv`, in order, three at a time, skipping `replaces` rows. For each item do a to e; after each item append its rows and its progress line before starting the next.

**a. Read the item's words again** at its anchors (from the section file, or from `fsd.md` if short). The definition matters more than the name.

**b. Look it up.** Run `find-columns` with the subject area. Start with the item's own words; then try what a repository might call it: fewer words, the plain noun, an abbreviation, a near word ("region": area, territory). For a calculation, look up each thing it is built from. For a figure the FSD defines by a rule ("year ago", "to date"), also try `--formula-contains` with a function or word the formula would hold. Two to four lookups are usual; note each term, because the basis records them.

```
java -jar build/obi2pbi.jar find-columns kb/<estate> "net revenue" --subject-area "Sample Sales"
java -jar build/obi2pbi.jar find-columns kb/<estate> --subject-area "Sample Sales" --formula-contains "AGO("
```

Read only what it prints. For each candidate it gives the Row identifier as a marker in square brackets, the subject area, table and column, the logical column, whether it is a measure and how it is summarised, a formula if it is calculated, the database columns behind it, and report columns that use it. If nothing in the subject area fits, one lookup without `--subject-area` shows whether the term exists elsewhere in the estate; a column of another subject area is never proposed without saying so in the basis and scoring it as a name only.

**c. Judge the meaning.** Ask of the best candidates: is it the same kind of thing (a number where the FSD wants a number, summed where the FSD adds up, counted where it says "number of")? Does the table it sits in fit (actuals or targets)? Does a report of the replaced page use it for this figure? If the as-is document of the subject area exists, you may read the one section about the candidate's table: find the table's unit in `slices/<estate>/<name>/units.csv` (its `title` names the table, its `position` and `unit_id` give the part file `as-is/<estate>/<name>.parts/<nnn>-<unit_id>.md`) and read that one part, never the whole document. Say in the basis that you used it, and whether the document's first line says reviewed or draft.

**d. Decide and score**, by `references/scoring.md`:

- one fitting column: one `proposed` row;
- a calculation: one `proposed` row for each column it is built from, all with the item's id, and a basis that says how the item is computed from them ("net revenue = Revenue minus Discnt Value; this row is the Revenue part"). If a single OBIEE column already computes it (a calculated measure, a report formula), propose that one instead and quote its formula;
- two candidates you cannot choose between: the better one `proposed` in the 30-59 band, the runner-up named in the basis, and an open question;
- nothing fitting: one `unmatched` row, empty `row_id`, confidence 0 to 29, and a basis that lists what you searched and why the nearest candidates do not fit.

**e. Write.** Append the rows to `matches.csv`. `row_id` is the candidate's marker without its opening `[kb:` and its closing `]`, copied character for character. `fsd_anchor` is the item's main anchor; `fsd_text` as in `fsd_items.csv`. If the item raised a question, append it to `trace-questions.md` under `## Open questions` (form below). Then append to `progress.csv`: `<item_id>,matched,<last question number>,`.

### Step 5. Check, and hand over

1. Run `java -jar build/obi2pbi.jar check-matches work/<tab> --kb kb/<estate>`. Exit code 0: no failure. 3: failures, listed in `work/<tab>/check.csv` (`finding`, `severity`, `file`, `row`, `item_id`, `row_id`, `detail`) and counted in what is printed. Fix every failure in `fsd_items.csv` or `matches.csv` and run it again until the exit code is 0:
   - `item_without_match`: do step 4 for that item. `unknown_item`, `unknown_anchor`: a mistyped id or anchor; copy it again.
   - `unknown_row`: the `row_id` was not copied exactly; run the lookup again and copy it. `no_physical_source`: the column reaches no database table; it cannot be proposed. Make the item `unmatched` and say why in the basis.
   - `bad_value`: the detail says which value; the commonest is a confidence outside its band (`proposed` needs 30 or more, `unmatched` less than 30).
   Warnings do not fail the check. Read them: `derived_column` means the column is computed by a formula, which the basis should say; `differs_from_existing_report` means the replaced page uses other columns for this item. Look at the columns it names and reconsider the match; if you keep it, say in the basis why.
2. Append to `progress.csv`: `_matches,checked,<last question number>,`. Write `trace-status.md` again with line 1 unchanged and, below the three lines, the counts: items, proposed rows, unmatched items, open questions.
3. Tell the Operator, plainly:
   - where the files are, how many items, how many proposed and at which confidence, how many unmatched, how many questions are open, and what `check-matches` printed;
   - that these are proposals and a draft: the review should be run first, best in a new chat, and it may lower scores;
   - that **only the Operator confirms a match**. After the review they write `work/<tab>/decisions.csv` themselves, one row per verdict, with the columns `item_id`, `decision` (`confirmed` or `rejected`), `row_id` (empty when the item has one proposed column; the column's identifier when it has several, or when they correct the proposal to another column) and `note`; then run `check-matches` again, which checks the decisions too;
   - that the open questions are in `trace-questions.md`, and that answers can be typed after `Answer:` or told to the agent.

Do not call the matches final, confirmed or ready.

## Open questions

Raise a question when the FSD's words can mean two columns, when a definition is missing or contradicts the name, when an item has no column and someone must decide what to do about it, or when you need something only a person can supply (the identifier of the replaced page). The form is the as-is skill's, with two more lines that say what the question is about:

```
### Q2. Does "Net revenue" mean Revenue minus the discount value, or is Revenue already net?
Item: K1
FSD anchor: t2.r2.c2 t2.r2.c3
Why it matters: the FSD defines net revenue as revenue after discounts. Sample Sales has a measure Revenue and a measure Discnt Value and nothing that says whether Revenue already has discounts taken off. The two readings give different numbers on every card and chart that shows net revenue.
Assumed meanwhile: Revenue minus Discnt Value; both columns are proposed for K1.
How to get the answer: for one order of the database table SAMP_REVENUE_F, compare the columns REVENUE and DISCNT_VALUE with the invoiced amount of that order, and bring back whether REVENUE is the amount before or after the discount. If the FSD means something else by "discounts", bring back the wording to put at t2.r2.c3.
Answer:
```

- A number is used once, ever. `Answer:` is the last line of a question and is left empty.
- "How to get the answer" says what to look at or decide, where, and what to bring back. It may point at the FSD by anchor, name any table or column `find-columns` printed, and give a query as ordinary text. It gives no menu paths or screen names, and names no person.
- Before raising one, read the open questions you already have; add `See also: <item_id>` above an earlier question's `Answer:` line rather than asking the same thing twice.

Taking in answers, and what to do when the FSD changed, are in `references/answers.md`.

## If something is missing or goes wrong

- An input is missing: name it, say how to produce it, stop.
- `find-columns` ends with exit code 2: the message says what is wrong (usually the subject area's name; copy it exactly from the list `slice kb/<estate>` prints).
- `decisions.csv` already exists: leave it alone. If you change a match the Operator has decided on, say so in your report; `check-matches` will show whether the decision still names a proposed column.
