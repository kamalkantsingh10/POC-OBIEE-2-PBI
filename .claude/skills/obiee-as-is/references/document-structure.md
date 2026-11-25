# As-is document: what each section holds

Read this once before you write the first section. It says how to word a section and what goes into the section for each kind of unit. Questions and observations are in `questions.md`, beside this page. The steps are in `../SKILL.md`.

## Who reads the document

Business owners, report authors and data engineers who will never open OBIEE. They will confirm the document, and other people will build tables from it. So: short sentences, plain words, reference material in tables. A wrong sentence that reads well is the worst thing you can write; "not recorded" plus an open question is always acceptable.

## Words to use

| The packet says | Write |
|---|---|
| logical table | business table |
| presentation table / column | table / field that report authors see |
| logical table source | source |
| physical table / column | database table / column |
| aggregation rule | how it is summarised |
| dimension, level | drill path, level |
| session variable | a value set per user at login |
| repository variable | a value set once for the whole system |
| init block | the query that sets the value |

Give the OBIEE name once in quotes where a reader may need to look it up.

## What must be in a section

1. **Lines marked `(REQUIRED)`.** The packet ends a line with `(REQUIRED)` when it states a row the document must cover, and its header gives their number. The marker of each such line must be in your section. If you truly have nothing to say about one, list it under Not described with a reason instead. Do not copy the words `(REQUIRED)` into the document.
2. **Every item of the packet.** An item is a line that starts with `- ` at the left margin: one source, table, column, key, join, drill path, variable, rule or report, or one statement about counts or about where the data came from. Each appears in the section, at least as a row of a table or an entry of a list. There is no block of a packet that goes only to your notes. Count: when the packet says "7 not shown" or "5 levels", your table has 7 rows or 5 rows.
3. **Logic, exactly.** Anything the packet prints between backticks or fence lines, other than a mapping that is one database column (below), is logic: a formula, a rule, a filter, a query, a join condition, a default value. Quote it exactly.

"Lineage" lines repeat the mapping lines above them. You need not restate them.

**An empty block keeps its heading.** When the layout below names a block and the packet has nothing for it, write the heading and one sentence: "This table has no measures." "This source reads one table, so it makes no join." The same everywhere, so that a reader never wonders whether a block was forgotten.

**Saying that something is absent.** Two cases, two fixed forms:

- The packet has a line that says so ("priority group not recorded", "nullable not stated", "Keys … not recorded"). Repeat the packet's words, with that line's marker if it has one. Never sharpen them: "not stated" does not become "not null", "not recorded" does not become "none".
- The packet has no line at all for a thing the layout names (a source with no content filter line; a database table with no line for its object name or query). Then the absence itself must be stated, in this form and with no marker: "No content filter is recorded for this source." "No object name in the database is recorded for this table." Such a sentence is required, and the review does not treat it as unsupported.

## Backticks and fences are for exact quotes only

`check-doc` compares every text you put between backticks or between fence lines with the packets, character for character, and fails the document if the text is not there. So:

- Use backticks, or a fenced block, only for text copied from a packet: a formula, a query, a condition, a value, a name the packet prints. Copy every character, including spaces, quotation marks and letter case. A piece of a longer text is fine if the piece is exact.
- Put a quote in backticks in the sentence; if it is longer than about 80 characters, spans lines, or contains `|`, put it in a fenced block directly under the sentence or table, with the marker on the sentence that introduces it.
- Never put your own words between backticks: not a file name, not a path, not a query you suggest, not a made-up example, not a word you want to stress. Write them as ordinary text; use bold for stress. This holds in every part, the questions included.
- **A mapping that is one database column** is the one thing you may reword. When the packet says a column "here is" a text that is nothing but one database column, such as "xe".""."SAMPLESALES"."EMPLOYEE"."EMP_ID" (the packet then names that same column after "physical column"), write it as EMPLOYEE.EMP_ID, without backticks, with the line's marker. A mapping with anything more in it (a number, an operator, a function, a second column) is logic: quote it exactly.

## Citations

- A statement of fact ends with the marker of the packet line it came from. Copy the marker from the packet, character for character, brackets included. Never type one from memory and never build one from a name.
- **A fact on a line with no marker of its own** (an indented line, or a line that only points with `->`) is cited with the marker of the item it belongs to: the nearest line above it, less indented, that carries a marker. The identifier after `->` is not a marker and is never cited from this packet. If no line above carries a marker, the fact has none: say where it came from in words, "(counted)" or "(listed in the packet without a Row identifier)".
- In a unit's section, cite only markers of that unit's packet. What needs a marker from another unit is an observation across sections (`questions.md`). To send the reader to another unit's facts, name its section: "see the section Database table EMPLOYEE".
- In a table, the last column is `Evidence` and holds the markers for that row.
- A line the packet marks "no Row identifier" (counts, the manifest, errors, unread content, a variable name the repository does not define) is stated without a marker.
- A statement that rests on a user's answer ends with `[answer:Q<n>]` and no Knowledge Base marker, so nobody takes it for something OBIEE recorded.
- Outside a citation, never write the four characters that open a Knowledge Base marker. When you explain the document to its reader, say "the markers in square brackets".

## What you may and may not say

- You may restate logic in plain words when the restatement is mechanical: a column multiplied by 0.76 is "the database column REVENUE multiplied by 0.76". Keep the exact text beside it. Do not say what a formula, query or condition does when it runs ("returns one row", "is always true, so every row is paired"): the packet gives the text, not its effect.
- You may not supply a business meaning, a purpose, a reason or what a user sees. A name is only a name. If the meaning matters, ask an open question.
- A level of detail names levels of drill paths ("Dim Time Detail"). Quote it and name the levels; do not turn a level's name into "day" or "month".
- Copy the packet's totals; do not add amounts up yourself. Counting the rows of a list you have just written is fine.
- A sentence about how OBIEE behaves in general is not a fact about this system. Use one only where the reader cannot follow without it, start it with "In OBIEE generally," and give it no marker.
- Say nothing about Power BI, the new platform or how something should be rebuilt.
- Do not repeat a fact in two sections. Point to the other section by its heading.

## Sections by kind of unit

Each section starts with a `## ` heading in plain words, with no number. Never use the headings `Open questions`, `Answered questions` or `Not described` for a section of your own.

**A unit that was split.** When `units.csv` shows `parts` greater than 1, each part is a unit with its own packet and its own part file, written from that packet alone. In the document they read as one section: the part with `part` = 1 starts with the `## ` heading and the opening sentences; a later part has no `## ` heading and no introduction, and starts directly with the `### ` heading of the block its packet holds. If its packet continues a block the part before it ended in (your notes say where that part stopped), the heading is the same with "(continued)". There is one `### Points to note`, in the last part: an earlier part puts its points into the notes as `- Point to note: …` with their markers, and the last part gathers them with its own. (Their markers come from the earlier part's packet; `check-doc` lists them as a warning, and that is expected.)

### overview — `## What report authors see`

The subject area and the business model behind it; other names it has; the measure OBIEE uses when a query names none; a table of the tables report authors see (Table | Fields | Business table behind it | Evidence); any displayed name that is a formula, quoted exactly; the counts, copied, including the database tables, their columns and their keys; the estate-wide items that are in no subject area.

Then `### What was examined`, from the packet's last block: which halves of the estate were extracted, when, and what the packet says was therefore not examined. Say only what the packet says: if it says reports and dashboards were not examined, say that of reports and dashboards, and do not extend it to saved filters or anything else. Beside a count of 0, repeat the packet's statement about that thing if it makes one. End the block with the packet's list of units as one sentence: "This document was written from these units, in order: …".

### logical_table — `## <table name>`

Follow the packet's blocks, in order.

- **Opening**, from "The table": what the table is, how many fields, measures and sources (counted).
- **`### Keys`**: Key | Primary key (as recorded) | Fields | Evidence.
- **`### Measures`**: Measure | How it is summarised (the rule, exactly) | Can be typed into (yes when writeable true, no when false, otherwise not recorded) | In sources (n of m, and their names) | Evidence. A measure calculated from other fields gets its formula, exactly, under the table. Where the name report authors see differs from the business table's name, give both.
- **`### Fields`**: Field | In sources (n of m, and their names) | Evidence. A field with a formula gets it, exactly, under the table.
- **`### Measures and fields report authors do not see`**: the items marked "NOT SHOWN IN THIS SUBJECT AREA", in the same two layouts.
- **`### Where the data comes from`**, and for each source a `#### Source "<name>"` holding, in this order:
  - active or not; priority group as recorded; level of detail (grain), exactly; what the packet says about a parent-child hierarchy, if anything;
  - the content filter, exactly, or the sentence that none is recorded;
  - the database tables it reads: Database table | Schema | Type | Evidence, each with "see the section Database table <name>";
  - the joins between them, one row per packet line (the packet lists a join once under each of its two tables; keep both rows): Listed under | From | To | Join type | Cardinality | On | Evidence. Join type and cardinality are copied as printed;
  - the mapping: Field | Database column (as TABLE.COLUMN, or the exact text when it is logic) | Type, precision, nullable (the words in the packet's brackets, copied) | Evidence.
- **`### Points to note`**: what a reader must not miss, each with its marker, for example a measure mapped with a multiplication in one source and without in another.

Left out for every table: lineage lines, and "name set by hand" unless it is true. Writeable is stated for every measure, and for a field only when true.

### physical_table — `## Database table <NAME>`

This is the section the person who builds the new tables works from. Every column is a required row. Keep it a clean reference:

- **Opening**: where the table is (the containers, quoted as printed) and its type in the packet's words; whether it is an alias, and of what; its description, or that none is recorded; its object name in the database, or its query, exactly, or the sentence that none is recorded; any special role the packet states (for example the relationship table of a parent-child hierarchy, with the member, parent, distance and leaf columns it names); the packet's counts of columns, mapped and not mapped, and keys.
- **`### Columns`**, one row per column, in the packet's order, every value as the packet prints it:

  | # | Column | Type | Precision | Scale | Nullable | External name | Mapped by | Evidence |

  Under Nullable write "true" or "not stated", exactly as the packet has it. Under the table add once: "Nullable: the export records this only when it is true. "Not stated" means the export does not carry it; it is not a statement that the column must be filled." Under Mapped by, the business table and field, or "not mapped".
- **`### Keys`**: Key | Columns | Evidence, and what the packet says about primary keys, in its words. If the packet says no key is recorded, repeat that.
- **`### Foreign keys and joins`**: one line per packet line, saying from which table and column to which, and "stated in full in the section How the tables relate". These lines only point; they carry no marker.
- **`### Read by`**: the sources that read the table, each with its business table, or the packet's sentence that none does.

### joins — `## How the tables relate`

Business-model joins: From | Rows (as printed) | To | Rows (as printed) | Join type | Evidence. Database joins: Kind | Name | From table (column; type, precision, nullable as printed) | To table (column; type, precision, nullable as printed) | Cardinality (as printed, or not recorded) | Evidence, with any join condition quoted exactly. Do not sum up or classify the cardinalities.

### hierarchies — `## Drill paths`

Per drill path: its kind in the packet's words (levels, or parent-child); the path in one line (the level names in the packet's order); then Level | Level above | Level(s) below | Key column | Keys (name; fields; primary and drill-down as recorded) | Member count as recorded | Shown to report authors as | Evidence; and whether report authors are offered it. For a parent-child drill path, state the relationship table and its member, parent, distance and leaf columns as the packet gives them, cite the drill path's own marker, and send the reader to that database table's section.

### variables — `## Values set per user or for the whole system`

The packet's summary counts. Per variable the repository defines: which of the two kinds it is, its default exactly, the query that sets it exactly, and where the subject area uses it. Then Name | Used in, for the names that are used and not defined in the repository, saying exactly that. What a value holds at run time is not recorded: ask.

### security — `## Security`

Row filters first, each with the filter exactly and the source it restricts; or the sentence that none is recorded. Then Permission | On | Privilege package | Held by | Evidence. Then the roles, one per line as the packet gives them: display name, name in the repository, guid, the privilege package and query privilege each holds.

### reports — `## Reports built on this subject area`

Add the dashboard or folder name when there are several units. Per report: where it is, its calculated columns with formulas exactly, its filters and prompts. If the packet says reports were not examined, say so in its words.

### saved_filters — `## Saved filters`

Each saved filter with its conditions exactly.

### gaps — `## What the export did not capture`

Errors first, each one, naming the object. Then the packet's totals, copied. Then one row per kind of object: what was not read, named exactly as the packet names it (ComplexJoin@mdsid, Description#text), with the counts the packet gives; then the object types left out, with their counts. Say only that each was not read; never explain what an attribute or element might hold. No markers and no backticks are needed. To tie a gap to an earlier section you have only your notes: do it only where a note says so, name the section, and quote nothing from it.

## Worked example: a database table

````
## Database table EMP_PARENT_CHILD

EMP_PARENT_CHILD is a database table in `xe.SAMPLESALES`, type `none`; it is not an alias of another table, and no description is recorded [kb:sampleapp::phys::xe.SAMPLESALES.EMP_PARENT_CHILD]. Its object in the database is `EMP_PARENT_CHILD` [kb:sampleapp::phys::xe.SAMPLESALES.EMP_PARENT_CHILD.def.DefaultMulDB]. The export lists 4 columns for it, 1 mapped by a field of this subject area and 3 not, and 1 key (counted).

### Columns

| # | Column | Type | Precision | Scale | Nullable | External name | Mapped by | Evidence |
|---|---|---|---|---|---|---|---|---|
| 1 | MEMBER_KEY | DOUBLE | 15 | not recorded | not stated | not recorded | not mapped | [kb:sampleapp::phys::xe.SAMPLESALES.EMP_PARENT_CHILD.MEMBER_KEY] |
| 3 | DISTANCE | INT | not recorded | not recorded | not stated | not recorded | Dim Sales Rep, Distance | [kb:sampleapp::phys::xe.SAMPLESALES.EMP_PARENT_CHILD.DISTANCE] |

### Keys

| Key | Columns | Evidence |
|---|---|---|
| EMP_PARENT_CHILD_Key | MEMBER_KEY | [kb:sampleapp::phys::xe.SAMPLESALES.EMP_PARENT_CHILD.key.EMP_PARENT_CHILD_Key] |

### Read by

The source "LTS Employee" of the business table Dim Sales Rep (listed in the packet without a Row identifier).
````

Every row ends with markers copied from this unit's packet; only text copied from the packet stands between backticks; "not stated" and "not recorded" are kept as printed.

## The opening and the closing parts

**Opening (`000-summary.md`, written last from the notes file):**

```
Status: draft, not reviewed

# <Subject area> — the semantic model as it exists today

**What this is.** A plain-language description of the <Subject area> subject area in OBIEE: what report authors see, what each number is, how the tables relate, where the data comes from and what the database tables hold. It describes the existing system only.

**How to read it.** Statements of fact end with a marker in square brackets. The marker names the row of the Knowledge Base the statement came from, so any statement can be checked. A marker that starts with "answer" means the statement rests on an answer a person gave, not on OBIEE. Text shown as code is copied from OBIEE exactly. Where the export does not settle something, the text points to a numbered open question near the end; each says how to find the answer and has an empty answer line for you.

| | |
|---|---|
| Subject area | <name> |
| Estate | <estate> |
| Knowledge Base extracted | <kb_extracted_at from the manifest> |
| Reports and dashboards | <what the overview note says was examined> |

## The model in one page
```

Under "The model in one page", from the notes only: what the subject area holds (its tables and measures, in counts and names); a table of the database tables (Database table | Columns | Key | Read by), from the notes of the database-table units, so that it shows at a glance which tables have a key and which have none recorded; the three to five things a reader must know before relying on a number; what was not examined; how many questions are open. Each statement of fact keeps the marker its note carries. Where the notes hold a `- Correction` line, the corrected statement counts and the note it supersedes does not.

**Closing parts**, created at the start and filled as you go: `990-open-questions.md`, `991-answered-questions.md`, `992-not-described.md`, `993-questions-the-export-answered.md`. A line in Not described is `- ` + the row's marker + ` — ` + the reason, one row per line.

You need not put empty lines at the start or end of a part: the jar puts one empty line between two parts when it joins them.
