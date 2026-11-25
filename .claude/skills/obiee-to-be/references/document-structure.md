# To-be design: what each section holds

Read this once before you write the first part. The steps are in `../SKILL.md`; the OBIEE features that need a decision are in `obiee-features.md`.

## Who reads the document, and what it is

Data engineers who will build the new tables, the person who will build the Power BI model, and the people who must sign both off. It is a **like-for-like design**: the new thing does what the old thing did, unless the FSD says otherwise. It does not describe today's model again; the as-is document does that, and you point to it by section name.

Three kinds of sentence, and the reader must always be able to tell which is which:

- **What OBIEE holds**: a fact from a packet, with its marker. Logic is quoted exactly.
- **A proposal**: what you suggest building. Always labelled "Proposal". Nothing you propose is a fact, and nobody has agreed to it yet.
- **Missing**: something the export does not hold and you may not guess: a key, decimal places, whether a column must be filled, indexes, volumes, how a table is loaded. Say "missing", say what is missing, and ask where it matters.

## The parts, and the order of the document

`<doc>` is `work/<tab>/to-be`. Every part is a file in `<doc>.parts/`. The four digits in front decide the order in the document; the name after them says which packet `check-doc` compares the part with. For a unit, the name is the first digit of its block, then the unit's `position` in `units.csv` as three digits, then `-<unit_id>.md`.

| Part | Holds | Written |
|---|---|---|
| `0000-summary.md` | Status line, title, how to read, `## The tab in one page` | last, from the notes |
| `0<pos>-overview.md` | `## What this design covers` | from the `overview` unit |
| `1000-new-tables.md` | The heading `## New tables and views` and its fixed introduction | at the start |
| `1<pos>-dbtable-….md` | `### New table <NAME>` or `### New view <NAME>` | one per `physical_table` unit |
| `2000-old-to-new.md` | `## Old-to-new column list`: one row per new column | at the start; rows appended by each `physical_table` unit |
| `3000-model.md` | The heading `## Power BI semantic model` and its fixed introduction | at the start |
| `3<pos>-table-….md` | `### Model table <name>` | one per `logical_table` unit |
| `3<pos>-joins.md`, `-hierarchies.md`, `-variables.md`, `-security.md`, `-reports.md` | `### Relationships`, `### Hierarchies`, `### Values set per user or for the whole system`, `### Security`, `### Report-level calculations` | from those units |
| `4000-fields.md` | The heading `## Fields on the tab` and its fixed introduction | at the start |
| `4<pos>-tab-fields….md` | The items of the FSD and what supplies each | one per `tab_fields` unit |
| `5000-unresolved.md` | `## Unresolved`: the FSD items that are not confirmed | at the start; rows appended by the `tab_fields` units |
| `6000-decisions.md` | `## Decisions needed` | near the end, from the notes |
| `7<pos>-gaps.md` | `## What this design could not see` | from the `gaps` unit |
| `9900-open-questions.md`, `9910-answered-questions.md`, `9920-not-described.md`, `9930-questions-the-export-answered.md` | The closing sections | at the start; filled as you go |

Example: the unit `dbtable-11-samp-targets-f` at position 22 is written to `1022-dbtable-11-samp-targets-f.md`; `table-02-fact-target` at position 6 to `3006-table-02-fact-target.md`; `tab-fields-p1` at position 2 to `4002-tab-fields-p1.md`.

You write the units in `units.csv` order (the tab's fields first, then the model tables, then the database tables); the numbers put them in reading order. A unit that was split (`parts` greater than 1): each part of it is its own file and continues the block of the part before it, with no new introduction.

## Rules for every part

- **Markers.** A fact ends with the marker of the packet line it came from, copied character for character. In a unit's part, cite only that unit's packet. A fact on an indented line or a `->` line with no marker of its own takes the marker of the item it belongs to. A line the packet marks "no Row identifier" is stated without one. In a table the last column is `Evidence`.
- **Lines marked `(REQUIRED)`.** The marker of each must be in the document: in the unit's part, or, for a row the tab does not use, in `9920-not-described.md` with the reason (below). Count them against the packet's header.
- **Backticks and fenced blocks are only for text copied exactly from a packet**: a formula, a query, a condition, a value, a name the packet prints, the FSD's words as the tab-fields packet prints them. `check-doc` compares each with the packets and fails the document otherwise. A new name, a type, a file name, a proposed definition never go between backticks.
- **A proposal is shown like this**: a line that starts `Proposal (not from OBIEE; to be signed):`, and, when it is a definition or a query, the text under it as a block indented by four spaces. No backticks, no fence. The words "Proposal" and "to be signed" are never left out, however obvious the proposal is.
- **Absent and missing.** Where the packet says "not recorded" or "not stated", repeat its words with its marker; never sharpen them ("nullable not stated" is not "must be filled"). Where the export has no line at all for something the layout names, write "Missing: …" with no marker.
- **Nothing decided silently.** Wherever OBIEE does something Power BI does not do the same way (`obiee-features.md`), or the design has two reasonable shapes, you do not pick: you state what OBIEE holds, give the options, and ask (a decision, below). You may say which option is like for like.
- **Platform-neutral types.** No target platform is chosen. Use these words and no platform's type names:

  | The packet says | Write |
  |---|---|
  | VARCHAR, CHAR, with precision n | text, length n |
  | INT, INTEGER, SMALLINT, BIGINT | whole number |
  | DOUBLE, FLOAT, DECIMAL, NUMERIC | number; precision and scale exactly as recorded; where scale is not recorded: "decimal places: missing" |
  | DATE | date |
  | DATETIME, TIMESTAMP | date and time |
  | anything else | the packet's word, and "type to be decided" |

- No `CREATE TABLE`, no script for any platform, no Power BI file, no visual styling, no redesign.
- The as-is document: read one part of it, by its section, when you need what it says about the same table (step 4b of the skill). Refer to it as "as-is document, section <heading>". Do not copy markers from it, and do not repeat what it describes.

## The sections

### overview — `## What this design covers`

The tab; the subject areas its confirmed columns are in; the tables report authors see, each with its confirmed columns (Table | Confirmed fields | Evidence); the packet's counts of decisions and items, copied; the counts of what the Slice holds; where it came from. One sentence each on the as-is documents used and their status lines.

### physical_table — `### New table <NAME>` (or `### New view <NAME>`)

One per database table of the Slice, all in this layout.

1. **Opening**, starting `Proposal (not from OBIEE; to be signed): `: build it as a table, like for like from the OBIEE database table; then the facts: where the old table is and its type, in the packet's words; its object name in the database, exactly; whether it is an alias.
   - **A table that is a query** (type `select`): propose a **view**, and quote its SQL exactly in a fenced block.
   - **An alias** of another table: propose no table of its own; say which new table it is a role of, and list the roles in that table's section from your notes.
2. **Columns**, one row per column, in the packet's order, every column whether the tab uses it or not:

   | # | New column | Proposed type | Length or precision | Decimal places | May be empty | From old column | Old field | Used by tab items | Evidence |

   New column: the old name, unless there is a reason, which you then state. Length or precision and Decimal places: as recorded, or "missing". May be empty: "yes, as recorded" for nullable true; "missing (not stated in the export)" otherwise. Old field: the business table and field the packet says map it, or "not mapped". Used by tab items: the item ids your notes give for that field, or "none". The external name, when the packet records one, goes in one sentence under the table, quoted (or in a column of its own if the columns differ).
3. **Key**: the old key and its columns with its marker, proposed as the new key; or "Missing: the export records no key for this table", and an open question for a table the tab uses.
4. **Grain**: one sentence pointing to the model table whose source reads this table ("the level of detail is stated in the section Model table Fact Target"). Do not state a grain the packet does not give.
5. **Links to other tables**: the packet's foreign keys and joins, one line each, "proposed as a foreign key; stated in full under Relationships". No marker: these lines only point.
6. **Read by**: the sources that read it, with their model tables.
7. **Missing**: one list of what the export does not hold for this table: decimal places and whether a column must be filled, for the columns concerned; the key if none; and always: indexes, partitioning, volumes, how and when it is loaded.

Then append to `2000-old-to-new.md` one row per column: New table.column | Old database column | Old field | Used by tab items | Evidence (the column's marker) | Status, which is always "proposal, to be signed by Data Engineering".

### logical_table — `### Model table <name>`

1. **Opening**: `Proposal (not from OBIEE; to be signed): ` a table of the Power BI model named as the business table, read from the new table(s) below; then the facts: the business table and its marker, its counts, how many of its fields the tab uses.
2. **Keys**: the packet's keys with their markers, or its sentence that none is recorded.
3. **Where it reads from**: every source, each with its marker: active or not, priority group as recorded, level of detail exactly, content filter exactly if any, the database tables it reads (as "new table <NAME>"), and each join inside the source with its type and cardinality as printed (one row per packet line, with its marker). More than one source, a content filter, or a join inside a source is a decision.
4. **Fields carried into the model**: only those confirmed for the tab: Field | From new table.column | Type, precision, nullable as the packet prints them | Tab items | Evidence. End with one sentence counting the fields not confirmed, which are not carried as fields; if some of them are key fields, say that the relationships may still need their columns and point to Relationships.
5. **Measures**, one block per measure confirmed for the tab:

   ```
   #### Measure: <proposed name>

   - Replaces: the OBIEE measure "<name>" of "<table>", with its markers; confirmed for the tab, items <ids>.
   - OBIEE formula it replaces, exactly: <the aggregation rule, and the formula if it is calculated, each in backticks or a fenced block, each with its marker>
   - Read from: new table <NAME>, column <COLUMN>, with the mapping's marker; one line per source that maps it.
   - Writeable in OBIEE: <as recorded>.
   - Proposal (not from OBIEE; to be signed):

         <proposed name> = <definition>
   ```

   A measure whose formula uses anything in `obiee-features.md` gets no proposed definition: write `Proposal: none until decision Q<n> is taken.`
6. **Not used by this tab**: a measure marked NOT CONFIRMED FOR THE TAB is not designed. Append one line for it to `9920-not-described.md`: `- ` + its marker + ` — not used by this tab`. The same for any other `(REQUIRED)` row of the packet that the tab does not use.

### tab_fields — the rows of `## Fields on the tab`

1. A table with one row per **confirmed** item, in the packet's order:

   | Item | FSD anchor | FSD text | Supplied by | Confirmed column(s) | Evidence |

   FSD text: quoted exactly, in backticks. Supplied by: the model item, which you name here and keep to afterwards: for an attribute or filter, model table and field; for a measure, the proposed measure name; for a calculation, the name of the FSD measure below. Confirmed column(s): table and column as the packet names them. Evidence: the item's marker.
2. `#### FSD measure: <name>` for each item of kind `calculation`, once per distinct calculation (several items may share one): the items it supplies; `The FSD's definition:` and the FSD text in backticks with the item's marker; the confirmed columns it is built from; then `Proposal (not from OBIEE; to be signed):` with the combination in an indented block, built from the base measures of those columns and nothing else. The combination is the FSD's definition, not something OBIEE records: say so. If the FSD text does not say how the columns combine, write no definition and ask.
3. Every item that is **not confirmed** (rejected, unmatched, undecided) is not in the table. Append it to `5000-unresolved.md` as one row: Item | FSD anchor | FSD text | Status | Decision note | Evidence (the item's marker). A question number inside a decision note ("Q2") belongs to the trace questions, not to this document; copy the note as it is and add "(trace question)" after such a number. So every FSD item appears exactly once: in the table of fields, or under Unresolved.

### joins — `### Relationships`

Business-model joins: From model table | Rows (as printed) | To model table | Rows (as printed) | Join type | Evidence, and under the table, for each, `Proposal (not from OBIEE; to be signed):` one relationship, with the "many" and "one" side and a single filter direction from the one side to the many side. A cardinality that is not one-to-many as printed, or a join type other than inner, is a decision. Database joins: Kind | Name | From new table.column | To new table.column | Evidence, proposed as foreign keys; a join with a condition has it quoted exactly and is a decision.

### hierarchies — `### Hierarchies`

Per drill path: its kind in the packet's words, its levels in order with their key fields (Level | Key field | Keys | Evidence), whether report authors are offered it, and `Proposal (not from OBIEE; to be signed):` a hierarchy of the same levels in the model table that holds the fields. A parent-child drill path is a decision.

### variables — `### Values set per user or for the whole system`

Each variable with its default and its query, exactly; each name used and not defined in the repository. Every one that a confirmed field or a filter depends on is a decision.

### security — `### Security`

Row filters, each quoted exactly, with the source it restricts; permissions and the roles that hold them, as the packet gives them. Each row filter is a decision (how it becomes a role in the model). If an FSD item about access is unresolved, point to Unresolved.

### reports — `### Report-level calculations`

Each confirmed report column: its report, its formula exactly, the item it supplies; it becomes a measure block as above. If the packet says there are none, say so in its words.

### gaps — `## What this design could not see`

Errors, each one. The packet's totals, copied. What was not read, named exactly as the packet names it, with its counts. Never explain what an attribute might hold.

## Decisions needed (`6000-decisions.md`)

Written from the `- Decision:` lines of your notes: one row per decision: What OBIEE does (with the marker the note carries) | Where (the section) | Options | Like for like would be | Question. Every row has an open question; the decision is taken by answering it.

## The opening (`0000-summary.md`)

```
Status: draft, not reviewed

# <Tab> — to-be design

**What this is.** A like-for-like design for the tab "<tab>": the new tables and views, the Power BI semantic model, and what supplies each field of the FSD. It is built from the columns the Operator confirmed. Everything marked "Proposal" is a suggestion to be signed by a person; nothing here has been agreed.

**How to read it.** A statement that ends with a marker in square brackets is a fact from the OBIEE export, and the marker names the row it came from. Text shown as code is copied from OBIEE or from the FSD exactly. A marker that starts with "answer" means the statement rests on an answer a person gave. "Missing" means the export does not hold it. Types are platform-neutral, because no target platform has been chosen. Today's model is described in the as-is document; this document points to it and does not repeat it.

## The tab in one page
```

Under it, from the notes only: the items of the FSD in numbers (confirmed, unresolved); the new tables and views in one table (New table or view | Columns | Key or "missing" | Read by); the model tables and measures by name; the decisions needed, in numbers, with the three that matter most; what is missing most often; how many questions are open.
