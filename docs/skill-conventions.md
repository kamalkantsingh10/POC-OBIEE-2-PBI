# Conventions for skill-written documents

These are the text rules that the jar's `check-doc` command relies on. A skill that writes a document from a Slice (the as-is skill, later the to-be skill) follows them exactly. They are few on purpose. The code that reads them is `src/main/java/obi2pbi/core/DocCheck.java` and `Citation.java`; the tests are `DocCheckTest` and `CitationTest`.

Section 10 is different: it is the contract of the files between an FSD and the Operator's decisions (Epic 3), which the trace skill writes, the Operator writes, and `check-matches` checks.

`check-doc` checks coverage, citations, quoted text, the shape of questions and the status line. It compares each section with the packet it was written from (section 9). It does not judge whether a statement is true. `assemble-doc` builds the document from its parts (section 1) and relies on the question form (section 5) and the answers file (section 6) to keep typed answers safe.

## 1. Files

| File | Written by | Rule |
|---|---|---|
| `as-is/<estate>/<name>.parts/*.md` | the skill | The document in pieces, one file per section. All writing and correcting happens here |
| `as-is/<estate>/<name>.md` | `assemble-doc` | The document: the parts joined in file-name order. Not edited by a skill |
| `as-is/<estate>/<name>.answers.md` | the skill, adding users' answers only | The document's name with `.md` replaced by `.answers.md` |
| `as-is/<estate>/<name>.md.review.csv` | the review skill | The document's full file name plus `.review.csv` |
| `as-is/<estate>/<name>.md.check.csv` | `check-doc` | The document's full file name plus `.check.csv`. Rewritten on every run. Header only when the document passes |

**Parts.** A skill writes `000-summary.md` (status line, title, opening), one `<nnn>-<unit_id>.md` per unit (`<nnn>` is the unit's `position` in `units.csv` as three digits), and `990-open-questions.md`, `991-answered-questions.md`, `992-not-described.md`. The as-is skill also writes `980-observations.md` (statements that put rows of two units side by side; its heading is `## Observations across sections`) and `993-questions-the-export-answered.md` (questions a later packet settled, moved out of Open questions with a `Settled by:` line and a citation; its heading is `## Questions the export answered`, a free section whose questions `check-doc` does not read). Both belong to no unit: their citations are checked, they are not compared with a packet, and `check-doc` names them in its note. The file-name order decides the order of the document. The name decides which packet a part is compared with: `check-doc` compares a part named `<nnn>-<unit_id>.md` with the packet of that unit (section 9). **A unit that `slice` split into parts is several units**: `table-01-fact-revenue-p1` and `table-01-fact-revenue-p2` are two rows of `units.csv`, two packets, and two part files, each written from its own packet alone.

Join: `java -jar build/obi2pbi.jar assemble-doc as-is/<estate>/<name>.md`. Every `.md` file of the parts folder, in code-point order of the file names. Line ends become line feeds; empty lines at the start and end of a part are dropped; a part with no other line adds nothing; one empty line stands between two parts; the text is UTF-8 and ends with one line feed. The same parts always give the same bytes. Exit code 0: written. 2: no parts folder or no part. 3: **not written**, because the document that is there has a question (in either question section) with text after `Answer:` that no entry of the answers file for that number contains (runs of white space compared as one space); the questions are printed with the answers as typed. Record them in the answers file and run it again.

Check: `java -jar build/obi2pbi.jar check-doc as-is/<estate>/<name>.md --slice slices/<estate>/<name>`. Exit code 0: no finding. 3: findings. 2: a missing input or wrong usage.

## 2. Status line

The **first line** of the document is exactly one of:

```
Status: draft, not reviewed
Status: reviewed 2026-11-23
```

- The reviewed form is `Status: reviewed ` followed by a date as `yyyy-mm-dd`. More text may follow the date after a space (`Status: reviewed 2026-11-23 by the review skill`).
- Anything else on line 1 is the finding `bad_status`.
- A document marked reviewed fails when the check has any other finding (`reviewed_with_findings`) or when `<name>.md.review.csv` is not beside it (`reviewed_without_review_log`).

## 3. Citing a Knowledge Base row

```
Revenue is added up over every dimension [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Revenue#aggr0].
```

- The form is `[kb:` + the Row identifier + `]`, on one line, at the end of the statement it supports. One identifier per pair of brackets; several citations may follow each other.
- **Copy the citation from the packet, character for character.** Every fact in a packet is followed by its citation, already in this form. Do not retype, shorten, trim or re-case an identifier. Spaces, dots, quotes, `#` and `/` inside it are part of it.
- **The one escaping rule.** Inside a citation, a `]` that belongs to the identifier is written `\]` and a backslash is written `\\` (a line break would be `\n` or `\r`). Nothing else is changed. The citation therefore ends at the first `]` with no backslash before it. The packets already print identifiers this way, so copying is enough. In the three sample Knowledge Bases, 0 of 58,299 identifiers need it; 2 have a space at one end, which copying preserves.
- A citation of a row that is not in the Slice, of a row that is in the Slice's tables and that no packet states, or a `[kb:` with no closing `]` on its line, is the finding `unknown_citation`, with the line number. A citation is therefore always copied from a packet.
- In a unit's part, a citation that the unit's own packet does not state and another unit's packet does is listed as `citation_from_other_unit`. This is a **warning**: it does not fail the check, because a section may rightly speak about another unit's row. The review looks at each one.
- **Do not write the characters `[kb:` anywhere else**, for example in an explanation of how to read the document. They would be read as a citation. Say "the markers in square brackets" instead.
- In a packet, `-> <identifier>` (no brackets) points at another row for the reader's orientation. It is not a citation, and it does not make the row citable from that packet: to cite the row, take its citation from the packet that states it.
- Rows of `errors` and `unparsed`, counts, and manifest facts have no Row identifier. A packet marks them "no Row identifier". State them without a citation and name the table.

## 4. Citing a user's answer

```
Revenue is held in euros [answer:Q4].
```

- The form is `[answer:Q<n>]`, `<n>` a whole number.
- The answers file must hold an answer for that number (section 6). Otherwise the finding is `unknown_answer`.

## 5. The three fixed sections

Each is a level-2 heading with exactly this text. Letter case is ignored; nothing may be added to the heading (no numbering). A section ends at the next line starting `## ` or `# `. The rest of the document is free.

### `## Open questions`

Every document has this section, even when it is empty. Each question is:

```
### Q7. How are the database tables joined, column to column?
Why it matters: the new tables need the same keys, and the export records the joins without their columns.
How to get the answer: open the Physical layer in the OBIEE Administration Tool, right-click SAMP_REVENUE_F, choose Properties, Foreign Keys, and bring back the column pairs.
Answer:
```

- The heading is `### Q<n>. <question>`: three hashes, a space, `Q`, the number, a full stop, a space, the question. Numbers are never reused, in either section.
- Below it, three lines that **start** with `Why it matters:`, `How to get the answer:` and `Answer:`. The `Answer:` line is left empty for the user. Other lines may stand between them (the as-is skill adds `Raised in: <unit_id>` and `Assumed meanwhile:`).
- `Answer:` is the last line of a question: everything after it, up to the next heading, is read as the answer, by `check-doc` and by `assemble-doc`.
- Any `### ` heading in this section that is not of that form, a missing line, or a number used twice is the finding `malformed_question`. So is a document with no `## Open questions` section.

### `## Answered questions`

Questions that have an answer in the answers file move here, in the same form, with the answer after `Answer:`. A question here whose `Answer:` is empty is `malformed_question`; one with no answer in the answers file is `unknown_answer`.

### `## Not described`

Rows the document deliberately does not describe, one per line, each with a reason:

```
- [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F.SAMP_REVENUE_F_FKey#2] — a database key with no meaning for a business reader; listed in the field map
```

- One row per line. The reason is the text after the (last) citation on the line; a dash, colon or other separator before it is ignored.
- An entry with no reason is the finding `not_described_without_reason`.

## 6. The answers file

```
# Answers

## Q4
Question: What currency is plain Revenue in?
Answered by: finance, 2026-11-24
Answer: Euro.
```

- One `## Q<n>` heading per answered question.
- An answer exists when, before the next heading, there is a line starting `Answer:` with text after it on that line or on the lines below.
- A skill adds to this file and never changes or removes an entry (Architecture AD-16). `check-doc` and `assemble-doc` only read it. No file means no answers.
- A corrected answer is a further `## Q<n>` entry at the end; the last entry for a number is the one that applies. Other lines may stand in an entry before `Answer:` (the as-is skill adds `Raised in:`).

## 7. What must be covered

A row is covered when the document cites it anywhere, or lists it under `## Not described` with a reason. The rows that must be covered are the Slice's `required_rows.csv` (`row_id`, `table`, `unit_id`), written by `slice`:

- presentation tables and presentation columns of the subject area;
- logical tables that have columns in the Slice, their keys, their columns that are measures, their table sources, and each join a source makes between its physical tables (`lts_joins`: the join type decides which rows a measure counts);
- physical tables of the Slice, each with its definitions (the object's name in the database, or the SQL of a table that is a query), its keys and **every** column of it, mapped by a logical column or not. These are the rows of the `physical_table` units (`dbtable-<nn>-<name>`), which state one database table whole for the person who builds the new tables;
- business-model joins and physical joins;
- dimensions and the hierarchies offered to report authors;
- variables that have a row in the Slice (a name used inside `VALUEOF(...)` that the repository does not define has no row: the `variables` packet lists it as "used by name, not defined in the repository"; state it without a citation);
- security rows (row filters, object permissions, the roles of their packages);
- report columns that are calculations (typed by the source as a computation, or a formula that is more than one quoted column reference).

Each required row is cited in exactly one packet, the one `required_rows.unit_id` names (so a physical table and its columns are cited in the table's own unit; a logical-table packet names them, gives type, precision, scale and nullable beside each mapping, and points at the table with `->`), and `units.csv` gives the count per unit. **In the packet, the one line that states a required row ends with `(REQUIRED)`** after its citation, and the packet's header gives the number of such lines. The words are a note to the skill, not part of the citation, and are not copied into a document. A skill that writes one section per unit and cites (or lists as not described) the row of every line marked `(REQUIRED)` will pass. A required row the document never mentions is the finding `not_covered`, with its table.

The list is a parameter: `check-doc --required <csv>` takes any file with the columns `row_id` and `table` in place of the Slice's list (for a to-be design, the confirmed columns of a tab).

Two rows of different kinds can have the same identifier text (a dimension and a logical table of one name; 3 cases in the `bi-analytics-2013` sample). One citation of that text covers both.

## 8. Findings

`<name>.md.check.csv` has the columns `finding`, `row_id`, `table`, `line`, `unit_id`, `detail`. It holds findings and warnings. A warning is listed for the reviewer and does not fail the check: the exit code is 0 when there are only warnings. `unit_id` says where to correct: for `not_covered`, the unit whose packet states the row; for a finding at a line, the part the line is in, as the part's file name without its number and `.md` (`overview`, `table-01-fact-revenue`, `summary`, `open-questions`). It is empty when the document has no parts folder, is not exactly its parts joined, or the finding has no line. The printed summary counts the findings per unit.

| Finding | Meaning |
|---|---|
| `not_covered` | A required row is neither cited nor listed as not described |
| `unknown_citation` | A citation names a row that is not in the Slice, or one that no packet states, or is not closed on its line |
| `quote_not_in_packet` | In a unit's part, a text between backticks or between fence lines is not in any packet, character for character (section 9) |
| `citation_from_other_unit` | **Warning.** In a unit's part, a citation that is stated in another unit's packet and not in its own (section 9) |
| `unknown_answer` | An answer citation, or a question under Answered questions, has no answer in the answers file |
| `malformed_question` | A question lacks one of its lines, has a wrong heading or a reused number; or the section is missing |
| `not_described_without_reason` | An entry under Not described gives no reason |
| `bad_status` | Line 1 is not a status line |
| `reviewed_with_findings` | Marked reviewed, and the check has findings |
| `reviewed_without_review_log` | Marked reviewed, and there is no review log beside it |

## 9. Quoting from a packet, and how each part is compared with its packet

When the document is exactly its parts joined, `check-doc` compares every part that is a unit of the Slice with the packets. A part is a unit when its file name is `<nnn>-<unit_id>.md` and `<unit_id>` is a row of `units.csv`. The other parts (`000-summary.md`, `990-open-questions.md`, `991-answered-questions.md`, `992-not-described.md`) are not units; their citations are still checked, their quotes are not. A part named after no unit is not compared and is named in a note that `check-doc` prints.

**What counts as a quote.** Two things, and nothing else:

1. **Text between backticks on one line.** A run of one or more backticks opens the quote; the next run of the same number of backticks on the same line closes it. The quote is everything between the two runs. A backtick with no closing run of the same length on its line is an ordinary character.
2. **The lines between two fence lines.** A fence line starts, after any indentation, with three or more backticks, or three or more tildes (`~~~`); a word may follow the opening one (` ```sql `). The quote is every line up to the next fence line of the same character and at least the same length. If the opening fence line is indented, that many spaces are taken off the start of each line of the quote.

**The rule.** Every quote in a unit's part must occur **character for character** in that unit's packet or in any other packet of the Slice: the same letters, digits, spaces, quotation marks, punctuation and letter case, and for a quote of several lines the same line breaks. Only white space at the two ends of a quote is ignored, so `` ` SUM("%1")` `` in a packet may be quoted as `` `SUM("%1")` ``. A quote that is in no packet is the finding `quote_not_in_packet`, with its line and its text. It fails the check.

What follows from the rule:

- **Use backticks and fence lines only for text copied from a packet**: a formula, an SQL text, a name, a value. A packet prints these between backticks or fence lines already; copy from there.
- **Do not put your own words between backticks**: not a file name, not a suggested query, not a word for emphasis. Write a suggested query in plain text inside an open question's `How to get the answer:` line (the question sections are not compared), and use bold for emphasis.
- A name the packet prints between double quotes (`"Fact Revenue"`) may be written between backticks, because the text between the backticks (`Fact Revenue`) is in the packet. A name spelt differently is not.
- A quote need not be the whole value: any piece of a packet's text passes, as long as the piece is exact.
- A value that a packet boundary cut in two (the packets say "the value continues in the next part") may be quoted whole, across the cut.
- There is no way to mark a text between backticks as "not a quote". If a text is not in a packet, it does not go between backticks.

**Citations, per part.** In a unit's part a citation should come from that unit's packet. One that comes from another unit's packet is the warning `citation_from_other_unit` (section 3); one that is in no packet is the finding `unknown_citation`.

`check-doc` prints these three kinds of line (`unknown_citation`, `citation_from_other_unit`, `quote_not_in_packet`) grouped by unit, each with its line number and the citation or the quoted text, after the counts per unit. When the document has no parts folder, or is not its parts joined, no part is compared and a note says so: run `assemble-doc` first.

## 10. From an FSD to checked matches (Epic 3)

This section is the contract between `fsd-text`, `find-columns`, the trace skill, the Operator and `check-matches`. The trace skill writes its files exactly as described here; `check-matches` checks them. The columns are declared once in code, in `src/main/java/obi2pbi/core/MatchSchema.java`; a test (`CheckMatchesTest`) fails when this section leaves a declared column or value out. The checks are in `MatchCheck.java`.

### 10.1 The files of a tab

One folder per FSD: `work/<tab>/`.

| File | Written by | Holds |
|---|---|---|
| `fsd.docx` | the Operator | The FSD as a Word document |
| `fsd.md` | `fsd-text` | The FSD as text, with an anchor on every paragraph and table cell (10.2). Never edited by hand or by a skill |
| `fsd.manifest.csv` | `fsd-text` | The source file's name, size and SHA-256, and the counts (10.2) |
| `fsd.sections/` | `fsd-text` | Only when `fsd.md` has more than 300 lines: `index.md` and the same text in sections (10.2) |
| `fsd_items.csv` | the trace skill | The FSD's items that need data, and the page the tab replaces (10.4) |
| `matches.csv` | the trace skill | The column proposed for each item, or `unmatched` (10.5) |
| `progress.csv`, `trace-questions.md` | the trace skill | Its progress, unit by unit, and its open questions (story 3.2) |
| `trace.answers.md` | the trace skill, adding the user's answers only | The answers (section 6) |
| `check.csv` | `check-matches` | Its findings (10.9). Rewritten on every run |
| `decisions.csv` | the Operator | The verdicts (10.8). No program writes it |

All four CSV files are UTF-8 with a header row and RFC 4180 quoting (a value holding a comma, a quote or a line break is put in double quotes, and a quote inside is doubled). Columns are found by name, so their order does not matter; a column the contract does not have is ignored and named in a note; a missing column stops `check-matches` with exit code 2. Write rows in FSD order. `check.csv` numbers rows from 1, the first row after the header.

### 10.2 Reading `fsd.md`

```
java -jar build/obi2pbi.jar fsd-text work/<tab>/fsd.docx
```

- Every heading, paragraph and list item has an anchor `[p<n>]`; every table cell `[t<n>.r<n>.c<n>]` (table, row, grid column, all from 1). Numbered in document order. The anchor comes first on its line, after a heading's `#` marks or a list item's marker: `## [p3] 1. Purpose`, `[p4] The tab ...`, `- [p14] Amounts ...`, `| [t1.r2.c2] Year |`.
- A table's first row is written as the markdown header row. Every grid position is a cell with an anchor; an empty cell is just its anchor (`| [t1.r4.c2] |`); a position a merged cell covers is `[t1.r3.c1] (merged with t1.r2.c1)`, the text being at the named anchor.
- Text is as the FSD has it. Bold outside headings and tables is `**...**`; a line break inside a paragraph or cell is `<br>`; a `|` in a cell is `\|`. Empty paragraphs, images, comments, deleted text, headers and footers are not there.
- **Long FSDs.** When `fsd.md` has more than 300 lines, `fsd.sections/index.md` lists sections of at most 152 lines, with each section's file, first and last anchor and the heading it is under; read the index, then one section at a time. A section starts at a heading of level 1 or 2, or where the previous one was full; a table longer than a section continues in the next one with its header row repeated. The anchors are those of `fsd.md`.
- `fsd.manifest.csv` (`key`, `value`): `written_by`, `source_file`, `source_bytes`, `source_sha256`, `paragraphs`, `tables`, `cells`, `lines`, `sections` (0 when not split), `section_max_lines`, `files`. When `source_sha256` differs from the one a run of the skill started from, the FSD has changed.

In the CSV files an anchor is written **without** brackets: `p12`, `t3.r2.c1`. A value that holds several anchors separates them with single spaces.

### 10.3 Looking a term up: `find-columns`

```
java -jar build/obi2pbi.jar find-columns kb/<estate> "net revenue" --subject-area "Sample Sales" --limit 10
java -jar build/obi2pbi.jar find-columns kb/<estate> --formula-contains "AGO("
```

It prints at most `--limit` candidates (10), best first: whole name, then all words, then some words (the group is printed after the identifier, with the words found, the words not found and a score). For each: subject area, presentation table, column, logical column, measure or not, default aggregation, formula (a derived column), the database columns behind it through lineage, and up to three report columns that use it. A candidate found with `--formula-contains` also has a line `matched in the <which formula>: <text>`, naming the formula that matched (derived formula, aggregation rule, mapping, mapping from the database, each with its Row identifier where it has one) and its text with runs of white space as one space; a formula longer than 160 characters is shown as the match with up to 60 characters on each side, and `...` where text was left out. A candidate's Row identifier is printed as a citation; in `matches.csv` and `decisions.csv` write the text between the opening marker and the last `]`, with `\]` written back as `]` and `\\` as `\` (none of the sample Knowledge Bases has either). A name that matches is not proof of the same meaning; `find-columns` does not judge, the skill does, and an item with no fitting candidate is `unmatched`.

### 10.4 `fsd_items.csv`

One row per item of the FSD that needs data, plus one row of kind `replaces` when the FSD names the page or report the tab replaces.

| Column | Meaning |
|---|---|
| `item_id` | Unique in the file; letters, digits, `-` and `_`. The FSD's own number when it has one (`F1`, `K3`); otherwise `V2-office`, `I07` |
| `kind` | `measure` (a number that is added up), `attribute` (a value shown or grouped by), `filter` (a slicer or prompt), `calculation` (worked out from other figures), or `replaces` (not an item: the page or report the tab replaces) |
| `fsd_anchor` | Where the item is: one anchor, or several separated by single spaces, the main one first (`t2.r2.c2 t2.r2.c3`: the figure's name and its definition) |
| `fsd_text` | The item's words, copied from `fsd.md` |
| `existing_id` | Only on the `replaces` row: the Row identifier of the dashboard page (`dashboard_pages`), dashboard (`dashboards`) or report (`analyses`) the tab replaces. Empty on every other row |

**How an FSD item names the existing report it replaces.** When the FSD says which OBIEE page or report the tab replaces ("It replaces the OBIEE dashboard page "Sample Sales / Overview""), the skill writes one row of kind `replaces`, with the anchor and words of that sentence, and puts the page's Row identifier in `existing_id`. Find it with `find-pages` (below); never by opening `dashboard_pages.csv`. When the Knowledge Base has no catalog, or holds no such page, leave `existing_id` empty and raise an open question; `check-matches` then says the page was not compared. There may be more than one `replaces` row. A `replaces` row has no match.

```
java -jar build/obi2pbi.jar find-pages kb/<estate> "sales overview" --limit 10
```

`find-pages` compares the words of the term with the names and catalog paths of dashboards, dashboard pages and reports (as `find-columns` compares words; a page's dashboard name counts too, so "Sample Sales / Overview" finds page "Overview" of dashboard "Sample Sales" as a whole name). Pages come before dashboards and reports when the scores are equal. For a page it prints its Row identifier as a citation, the dashboard and its Row identifier, the page's position on the dashboard (from 0), its catalog path, and the reports placed on it with their subject areas (a placement whose report is not in the catalog is printed as not resolved); for a dashboard, its pages; for a report, its subject areas and the pages that place it. Put the page's identifier (the text between the opening marker and the last `]`) in `existing_id`. When the Knowledge Base has no catalog, it says so: leave `existing_id` empty.

### 10.5 `matches.csv`

The columns are fixed (Architecture AD-11). One row per column an item needs: an item computed from two columns (revenue against target) has two rows with the same `item_id`. An item with no fitting column has one `unmatched` row.

| Column | Meaning |
|---|---|
| `item_id` | The item, as in `fsd_items.csv` |
| `fsd_anchor` | The anchor the match is about, usually the item's main anchor |
| `fsd_text` | The FSD's words for it, copied from `fsd.md` |
| `row_id` | The column: a presentation column (preferred), a logical column, or a report column (`analysis_columns`) when the item is a report's own formula. Empty when `unmatched` |
| `confidence` | A whole number from 0 to 100, by the bands of 10.6 |
| `basis` | Why: the evidence and the score; the runners-up when several candidates were close; for a formula, the formula; for `unmatched`, what was searched. Never empty |
| `status` | `proposed` or `unmatched`. The skill never writes `confirmed` or `rejected` |

### 10.6 Confidence bands

| Band | Means | Evidence that earns it |
|---|---|---|
| 90-100 | Almost certainly this column | The FSD names the subject area and the column exactly, or the column is used by the report the tab replaces for the same figure |
| 60-89 | Probably this column | The same column name in another table, or a close name with matching aggregation (a sum for an amount, a count for "number of") |
| 30-59 | Plausible, needs the Operator | A plausible name only |
| 0-29 | No column | Written as `unmatched`, with an empty `row_id`; never the nearest name to fill the gap |

Evidence from an existing report outranks a name. A `proposed` row scores 30 or more and an `unmatched` row less than 30; `check-matches` fails anything else.

### 10.7 What `check-matches` compares with the replaced page

When `fsd_items.csv` names a page (or dashboard, or report) in `existing_id`, the report columns of that page are those of every report the page places (`dashboard_contents`); a match agrees when its column is one of them, or is the presentation or logical column one of them is linked to (`report_lineage`). A match that does not agree is the warning `differs_from_existing_report`, which names the page's report columns that share a word with the item's text.

### 10.8 `decisions.csv`

Written by the Operator, never by a program (Architecture AD-2, AD-11). One row per verdict.

| Column | Meaning |
|---|---|
| `item_id` | The item |
| `decision` | `confirmed` or `rejected` |
| `row_id` | The column the verdict is about. Empty: the item's one proposed column (an item with several proposed columns needs the column named). With `confirmed`, a column that was not proposed is the Operator's correction: the column to use instead |
| `note` | The Operator's reason; may be empty |

```
"item_id","decision","row_id","note"
"K1","confirmed","","matches the Overview page"
"K3","confirmed","sampleapp::pres::Sample Sales.Fact Target.Revenue","the target, not the actual"
"F4","rejected","","no region in Sample Sales; ask the FSD author"
```

`slice` and the later skills use confirmed rows only.

### 10.9 `check.csv` and its findings

```
java -jar build/obi2pbi.jar check-matches work/<tab> --kb kb/<estate>
```

Exit code 0: no failure (warnings do not count). 3: failures. 2: a missing input or wrong usage; nothing written.

| Column | Meaning |
|---|---|
| `finding` | The kind, in the table below |
| `severity` | `failure` or `warning` |
| `file` | `fsd_items.csv`, `matches.csv` or `decisions.csv` |
| `row` | The row of that file, from 1 after the header |
| `item_id` | The item of that row |
| `row_id` | The Row identifier the finding is about; empty when none |
| `detail` | What is wrong, and what to look at |

| Finding | Severity | Raised when |
|---|---|---|
| `unknown_row` | failure | A `row_id` (or `existing_id`) is not a presentation, logical or report column (or dashboard page, dashboard or report) of the Knowledge Base |
| `no_physical_source` | failure | The column's lineage reaches no database table: no table source maps it, or the mapping reads no database column, or it has no logical column |
| `item_without_match` | failure | An item of `fsd_items.csv` (not `replaces`) has no row in `matches.csv` |
| `unknown_anchor` | failure | An anchor is not in `fsd.md`, or is not written as an anchor |
| `unknown_item` | failure | An `item_id` of `matches.csv` or `decisions.csv` is not in `fsd_items.csv` |
| `bad_value` | failure | A value the contract does not allow: `confidence` not a whole number from 0 to 100 or outside its band, `status` other than `proposed` / `unmatched`, `proposed` with an empty `row_id`, `unmatched` with one, an empty `basis` or `fsd_anchor`, a repeated `item_id` or column, an item both proposed and unmatched, a `kind` not listed, `existing_id` on an item, a match for a `replaces` row, a `decision` other than `confirmed` / `rejected`, an empty decision `row_id` that does not name one proposal |
| `derived_column` | warning | The column has no database column of its own because a formula computes it (a logical column with a derived formula, or a report column that is a calculation); the detail says what the formula refers to and whether that reaches the database |
| `differs_from_existing_report` | warning | The tab replaces a page, and the column is not among that page's report columns (10.7) |

`check-matches` never changes `fsd_items.csv`, `matches.csv` or `decisions.csv`, and does not judge whether a match means the right thing.

### 10.10 The tab's Slice (Epic 4)

```
java -jar build/obi2pbi.jar slice kb/<estate> --tab work/<tab>
```

Writes `work/<tab>/slice/`, a Slice with the same files and rules as a subject-area Slice (sections 1 to 9 apply to a document written from it, with `check-doc --slice work/<tab>/slice`). Only `confirmed` decisions contribute; the column is the decision's `row_id`, or the item's one proposed column when it is empty. The walk starts from those columns: a presentation column brings its presentation table and subject area but not the table's other columns; a logical column starts at the logical layer; a report column brings its report, whole, and the presentation column it is linked to. From there it is the subject-area walk: every logical table touched comes whole, with its sources, the physical tables they read (each whole), joins, the hierarchies of the presentation tables touched, variables, security, and every reference closed. A subject area's implicit fact column is not a starting point. The result is a subset of the Slices of the subject areas touched.

The Slice has one table of its own, `tab_fields`: one row per FSD item that needs data (`row_id` is `item::<item_id>`, cited as any row; `item_id`, `kind`, `fsd_anchor`, `fsd_text`, `status` (`confirmed`, `rejected`, `unmatched` or `undecided`), `row_ids` (the confirmed columns, one per line), `notes` (the decision notes)). Its manifest has `cut_by` = `tab`, `tab`, `subject_areas` and `subject_area_ids` (one per line) and `decisions`.

**Units**, in writing order:

| Unit | Holds |
|---|---|
| `overview` | The tab, the subject areas the confirmed columns are in, the presentation tables with their confirmed columns, counts, the unit list |
| `tab-fields` | Every FSD item in FSD order: kind, anchor, FSD text, status, each confirmed column (what it is, its logical column, measure or not, and the unit that describes it) and the decision notes. An item not confirmed is listed as such and contributes nothing |
| `table-<nn>-<name>` | One per logical table touched, whole, as in a subject-area Slice: keys, sources, every column with formula, aggregation rules, mappings and lineage. A column confirmed for the tab says `CONFIRMED FOR THE TAB, items ...`; one that is not says `NOT CONFIRMED FOR THE TAB` |
| `dbtable-<nn>-<name>` | One per physical table, whole: every column with type, keys, definition |
| `joins`, `hierarchies`, `variables`, `security` | As in a subject-area Slice, limited to the Slice |
| `reports` | The reports a confirmed report column comes from (otherwise a line saying there are none) |
| `gaps` | Errors and unread content about the Slice's objects |

**Required rows** are those of a subject-area Slice for the rows the Slice holds (section 7; presentation columns only where confirmed), plus every row of `tab_fields`: a to-be document cites each item, confirmed or not, so that none is lost.

`slice --tab` prints the decisions and how many items are confirmed and not, the subject areas touched and, for each, whether `as-is/<estate>/<subject-area>.md` exists (with its status line). Exit 2, nothing written: no `decisions.csv`, or no confirmed column. Exit 3, nothing written: a confirmed `row_id` that is not a presentation, logical or report column of the Knowledge Base, a confirmed decision with an empty `row_id` and not exactly one proposal, or a decision for an item that is not in `fsd_items.csv`.
