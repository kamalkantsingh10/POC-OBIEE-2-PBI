---
title: 'Cut a slice for a subject area'
type: 'feature'
created: '2026-11-23'
status: 'done'
route: ''
review_loop_iteration: 0
context:
  - '{project-root}/docs/port-guide/00-read-me-first.md'
  - '{project-root}/docs/port-guide/03-core-output-slice.md'
  - '{project-root}/documents/planning-artifacts/architecture/architecture-POC-OBIEE-2-PBI-2026-11-23/ARCHITECTURE-SPINE.md'
  - '{project-root}/documents/planning-artifacts/prds/prd-POC-OBIEE-2-PBI-2026-11-23/prd.md'
---

# Story 2.1: Cut a slice for a subject area

<frozen-after-approval reason="human-owned intent — do not modify unless human renegotiates">

## Intent

**Problem:** The whole Knowledge Base is too large for a skill to read, and most of it is irrelevant to one subject area.

**Approach:** Add the `slice` command, which copies out the rows one subject area needs, in the Knowledge Base's own shape, closing the gaps the reference slicer had.

## Boundaries & Constraints

**Always:**

- A Slice has the same file names and columns as the Knowledge Base, fewer rows, and its own `manifest.csv`, `errors.csv` and `unparsed.csv` (AD-9).
- The slice logic lives in `core` and does no I/O. No AI is involved.
- The walk follows guide 03 §6.4, with the corrections of difference 13.
- Java 11, standard library only. All CSV through the one writer; the same input gives byte-identical output.

- **Packets (AD-17).** Besides the CSV tables, the Slice holds `units.csv` and `packets/<unit>.md`. A packet is plain text of at most 400 lines with everything about one unit already joined, each fact followed by its Row identifier. Packets are derived only from the Slice's own tables, so the same Slice always gives the same packets.

- A Slice carries `schema_tables.csv` and `schema.csv`, limited to the tables it holds. The header of every packet names the tables its facts came from.

**Never:**

- Changing or summarising any value while slicing.
- Matching variables by partial name, or across estates.
- Slicing by confirmed matches (story 4.1).

## I/O & Edge-Case Matrix

| Scenario | Input / State | Expected Output / Behavior | Error Handling |
|----------|--------------|---------------------------|----------------|
| Happy path | `slice kb/<estate> --subject-area "Sample Sales"` | `slices/<estate>/sample-sales/` with the rows reachable from that subject area's presentation columns through lineage | N/A |
| What applies to the tables | The subject area's logical and physical tables | Their joins, aggregation rules, sources, variables used in their expressions, and security rules are included | N/A |
| Gaps of the reference slicer | Hierarchies; tables that are queries; formula-only columns | Included (difference 13) | N/A |
| Reports | Reports built on the subject area | Their rows, columns, filters, views, and the dashboard pages placing them, are included | N/A |
| Errors and gaps | Knowledge Base `errors.csv` / `unparsed.csv` | Slice copies hold only entries about objects in the slice | N/A |
| Units | Any slice | `units.csv` lists units in writing order: overview; one per logical table (its columns, measures and aggregation rules, sources with priority and filters, lineage to database tables); joins; hierarchies; variables and security; report-level logic, one per dashboard | N/A |
| Large unit | A logical table whose packet would exceed 400 lines | Split into numbered parts, each a unit | N/A |
| List | No `--subject-area` | Lists subject areas with column and measure counts; writes nothing | N/A |
| Unknown name | A name matching no subject area | Says so and lists the names | Exit 2 |
| Several names | Two subject areas | One slice folder each | N/A |

</frozen-after-approval>

## Code Map

- `docs/port-guide/03-core-output-slice.md` -- §6 the slicer: §6.3 starting set, §6.4 the walk, §6.5 what it left out, §6.6 the slice manifest; §8.8 tests
- `docs/port-guide/00-read-me-first.md` -- difference 13
- `reference/analysis/sample-sales/` -- the slice the reference produced for Sample Sales, with `slice-manifest.csv`
- `reference/knowledge-base/` -- the input to slice for a comparison run

## Tasks & Acceptance

**Execution:**

- [x] `src/main/java/obi2pbi/core/Slice.java` -- the walk from a starting set of presentation columns -- shared with story 4.1
- [x] `src/main/java/obi2pbi/in/kb/` -- read a Knowledge Base folder back through its `manifest.csv`
- [x] `src/main/java/obi2pbi/core/Packets.java` -- compute work units and build each packet's content from the Slice
- [x] `src/main/java/obi2pbi/out/PacketWriter.java` -- write `units.csv` and `packets/`
- [x] `src/main/java/obi2pbi/cli/` -- the `slice` command, list mode, messages, exit codes
- [x] `src/test/java/obi2pbi/` -- tests for the matrix rows; a test that every identifier in a slice resolves inside the slice

**Acceptance Criteria:**

- Given the Knowledge Base built from the fixtures, when `slice` runs for a subject area, then every reference inside the slice resolves inside the slice.
- Given the sampleapp Knowledge Base, when `slice` runs for Sample Sales, then it contains every row of `reference/analysis/sample-sales/` for the tables both have, plus the rows difference 13 adds; the extra row counts are recorded in Implementation Notes.
- Given a slice, when the tests run, then every row of the tables `check-doc` requires appears in exactly one packet, and no packet exceeds 400 lines.
- Given the same input, when `slice` runs twice, then the output is byte-identical apart from `manifest.csv`.

## Implementation Notes

**Built (23 November 2026).** `slice <kb-folder> [--subject-area <name>]... [--all] [--out <folder>] [--timestamp <text>]`. It reads a Knowledge Base through its manifest, cuts one Slice per subject area, and writes the Knowledge Base's tables with fewer rows, `errors.csv`, `unparsed.csv`, the two schema tables limited to the folder, `manifest.csv`, `units.csv`, `required_rows.csv` and `packets/<unit>.md`.

**Files.** New in `core`: `Slice` (the walk), `Packets` and `PacketUnit` (units and packet text), `Citation` (the citation form, shared with `check-doc`), `Variables` (names inside `VALUEOF`), `KbData` and `KbRows` (a folder in memory), `SliceSchema` (the Slice's own tables: `units`, `required_rows`, the Slice manifest, and `check`). `in/kb`: `KbFolderReader`, `KbFormatException`. `out`: `SliceWriter`, `PacketWriter`. `cli`: `SliceCommand`, registered in `Main.COMMANDS`. Changed: `KbSchema` (two public helpers that produce schema rows for a given table list), `KnowledgeBaseWriter.now()` made public. Tests: `SliceTest` (17), `PacketsTest` (8), `CitationTest` (3), helper `SliceFixture`. Docs: `README.md`, `docs/port-guide/00-read-me-first.md` (differences 23 and 24, notes), `docs/skill-conventions.md`. `build.cmd` lists no jar commands, so it did not change.

**The walk.** Guide 03 §6.4 and the Python agree; ported as they are (name or Row identifier matched exactly; physical joins need both ends, business-model joins either). Then:

- Difference 13: presentation hierarchies, levels and level columns; dimensions and logical levels; every definition of a physical table of the slice; logical columns named in a formula, repeated until nothing is added; variables by Row identifier in a reference list or by exact name inside `VALUEOF(...)`.
- Difference 23 (new, awaiting confirmation): security rows about any row of the slice, and the role rows of their packages. The old as-is document needed these and the old slice had none.
- Difference 24 (new, awaiting confirmation): every reference resolves inside the slice. Alias source tables and join columns are part of the walk; anything else pointed at is added as a single row and counted (`boundary_rows`).
- Catalog side: a report belongs when a criteria block names the subject area (quotes off, exact, then ignoring case, as `report_lineage` does). The whole report is kept with columns, views, prompts and filters; `report_lineage` only for blocks on this subject area; the pages that place it, their dashboards, the dashboard prompts on those pages, and saved filters reached from any kept filter.
- `errors`: rows whose `source_id` is a row or catalog path of the slice. `unparsed` counts per object type, not per object, so a row is kept when the slice holds objects of that type; a withheld value when its variable is in the slice. Rows about whole object types or files are left out and counted in the manifest.

**Units.** `overview` (subject area, presentation tables, counts, provenance); one `logical_table` unit per logical table, tables with measures first (sources with priority, group-by, filter, fragmentation; the physical tables they read with alias and definition; each column with presentation name, formula, aggregation rules, mappings with physical column and type, lineage); `joins`; `hierarchies`; `variables`; `security`; `reports` per dashboard, then per catalog folder for reports on no dashboard; `saved_filters` when there are any; `gaps`. Changes from the suggested split, and why: variables and security are two units, because they feed two sections of the document and split independently; saved filters have a unit of their own so each is described once; errors and unread content have their own `gaps` unit at the end, because on the samples the list is 130 to 210 lines and would crowd the overview. `joins`, `hierarchies`, `variables`, `security`, `reports` and `gaps` exist even when empty and say so, so a document can state "none" with a source.

**Required rows** are in `required_rows.csv` (`row_id`, `table`, `unit_id`). Presentation tables are covered by the overview; presentation columns by the unit of their logical table.

**Citation rule.** `[kb:<id>]` with `]` written `\]` and `\` written `\\`. Of 58,299 identifiers in the three sample Knowledge Bases, 0 need escaping; 2 have a space at one end.

**Numbers from the real run.**

| Estate | Subject areas | Units = packets | Largest packet | Required rows |
|---|---|---|---|---|
| `sampleapp` | 3 | 32 | 205 lines (`sample-sales/gaps`) | 157 |
| `bi-analytics-2013` | 18 | 245 | 314 lines (`anton-tserakhau/table-05-restaurants`) | 2,502 |

No unit needed splitting on the samples; splitting is proven by two tests on made-up data (180 columns; a 900-line SQL text). `sampleapp-v607` has no repository, so it has no subject area to cut. Reports in a slice are proven on the combined hand-made estate (`constructed` + `constructed-catalog`) and on in-memory cases.

**Sample Sales against `reference/analysis/sample-sales/`.** All 349 reference rows of this estate are in the slice (16 tables). The 2 other reference rows belong to `bi-analytics-2013` (a variable and its init block) and are correctly absent. Added, reference to now: `logical_columns` 49 to 52 (`Revenue (EUR)`, `Revenue (GBP)`, `Dim Products (Translated).PROD_DESC`), `logical_tables` 6 to 7, `logical_table_sources` 11 to 12, `lts_column_map` 62 to 67, `aggregation_rules` 8 to 10, `physical_columns` 58 to 64, `physical_tables` 12 to 13, `physical_joins` 16 to 17, `security_rls` 0 to 7 (difference 23). New tables: `logical_dimensions` 4, `logical_levels` 19, `presentation_hierarchies` 4, `presentation_levels` 17, `presentation_level_columns` 14, `physical_table_definitions` 8, `unparsed` 131, catalog tables empty. Cells that differ in shared rows are all reference lists (differences 5, 6, 7).

**Surprises.**

- The packet header first read "copy the [kb:...]", which is itself read as a citation. The header now says "the marker in square brackets", and the conventions forbid writing those four characters outside a citation.
- Two kinds of object can share an identifier text: in `bi-analytics-2013`, 3 dimensions are named like a logical table. Such a text is required once per table and one citation covers both.
- `Revenue (INR)` is not in the Sample Sales slice: nothing names it. The old document mentions it, from the full Knowledge Base.

**Change after first review (coordinator, 23 November 2026).**

- Difference 25 (new, awaiting confirmation): a logical table in the slice comes with every one of its logical columns, their mappings, aggregation rules and physical columns and tables. A measure among them is a required row. The packet line reads "NOT SHOWN IN THIS SUBJECT AREA: it has no presentation column here." Test: `SliceTest.testALogicalTableComesWholeWithTheColumnsTheSubjectAreaDoesNotShow`. This supersedes the `Revenue (INR)` remarks above.
- Not done as asked: lineage rows of such columns are not copied. `lineage` is keyed by presentation column, so the only rows that exist for a column not shown here belong to another subject area's presentation column, and copying them would pull that subject area in. The mapping rows give the same source and physical column.
- The overview packet has a section "Estate-wide items not in this slice": counts, from new manifest keys `not_in_slice:*`, of object permissions with no target, role privileges, init blocks and variables that are not in the slice, each with the table and filter to find them. None is a required row. The counts are "not in this slice"; whether a variable or init block is in another subject area's slice is not worked out.
- Sample Sales after the change: `logical_columns` 52 to 63, `lts_column_map` 67 to 80, `aggregation_rules` 10 to 11, `physical_columns` 64 to 71; other tables unchanged; 14 units; 125 required rows (was 124: `Revenue (INR)`). All 349 reference rows still present. `sampleapp`: 32 units, 158 required rows. `bi-analytics-2013`: 245 units, 2,502 required rows, unchanged. Largest packet across both: 314 lines. Tests: 192 pass.
- A second kind of shared identifier text turned up: a foreign key named like a column of its table (`POP_POPULATION.CITY_ID`). The join is required and cited in `joins`; the column is cited in its table's packet. One citation text, two rows.

**Not done / open.** See the final report: object permissions with no recorded target are not in any slice; the 400-line limit is untested in GitHub Copilot.

**Changed on 23 November 2026, after the first real run of the skills (Sample Sales).** The run showed what the packets did not give a writer. What changed, and why:

- **Foreign keys in full.** With deliberate difference 26 (story 1.6) a foreign key row names its far table and columns. The walk takes the columns at both ends (`referenced_columns` is a reference list like `columns`). The reference rule "both ends in the slice" now also applies to a foreign key, which before had no far end: on Sample Sales all 14 stay. The `joins` packet states each one as "from table (columns) to table (columns)" and then pair by pair, and its heading no longer says "both tables are in the slice" over lines that could not know.
- **Variables used by name.** The packet listed one variable while other packets quoted nine more names. `Variables.namesIn` now also passes over a number and comma before the name and ignores `%n` placeholders. A name that no variable has exactly is matched to a variable whose name differs only in letter case (decision: OBIEE does not tell them apart; the packet says the letter case differs). The `variables` packet lists every name used inside `VALUEOF(...)` in any expression, display-name formula, filter or SQL text of the slice, with pointers to the rows, and for each either its `variables` row or "used by name, not defined in the repository". Only variables with a row are required rows. Every formula that names a variable gets a pointer line to the unit (this also gives `Prod Dsc (Translated)` its pointer for `WEBLANGUAGE`).
- **Gaps.** The manifest has `unparsed_not_copied_by_type` (kind, object type, rows, occurrences); the packet names the object types that have no reader with their counts, gives computed totals per kind and per kind of object, and puts six rows on a line.
- **Security.** Each role is one line: display name, name in the repository, guid, each "not recorded in the repository" where the source has none; a name with a space is said to be one name. The same sentence is used under a permission and in the list of roles.
- **Packet size.** `--max-packet-lines <n>` (default 120, at least 40; kept in the manifest as `max_packet_lines`, so packets can be rebuilt from the folder). A logical table is laid out as what it offers (the table, its columns with presentation names, formulas and aggregation rules) and then where its data comes from (one table source after the other, each with its physical tables, mappings and lineage). A unit too long for one packet is cut between those two halves, then where an item starts, never inside one while an item boundary is in reach; only a value longer than a packet is cut, between fence lines. Each part repeats the header, says which part it is, and a continued block starts with a line naming what it continues.
- Numbers (limit 120): `sampleapp` 3 subject areas, 33 units (32 at a limit of 400), largest packet 119 lines, 157 required rows; `bi-analytics-2013` 18 subject areas, 325 units (245 at a limit of 400), largest 120, 2,502 required rows. Sample Sales: 15 units; `table-01-fact-revenue` is now `-p1` (119 lines) and `-p2` (103 lines).
- Tests: seven new in `PacketsTest`, two in `SliceTest`; `testALargeLogicalTableIsSplitIntoNumberedPartsOfAtMost400Lines` was rewritten for the new limit and the new cut rule (it asserted parts of more than 300 lines), and `SliceFixture.checkPackets` reads the limit from the manifest instead of a fixed 400.
- Open: the limit counts lines, not characters; a packet of mapping lines is about 23,000 characters at 103 lines. A document written from the old units has part files named after units that no longer exist (`table-01-fact-revenue`); it must be written again from the new packets.

### 23 November 2026: whole physical tables, keys, and a unit per database table (deliberate difference 33)

Added after an independent review judged the first as-is document "not enough to create the tables". The status of this story is unchanged.

- **What changed in the walk** (`core/Slice`). A physical table of the Slice comes whole: every column, every key, its definitions. This is one step, `wholePhysicalTables`, repeated until it adds nothing, and run again after the references are closed, so a table that comes in only because a row points at it is whole too. The keys of the Slice's logical tables and of its levels come with them. `isReferenceList` knows the three `columns` cells of the key tables.
- **What changed in the packets** (`core/Packets`). One more kind of unit, `physical_table` (`dbtable-<nn>-<name>`), one per physical table, after the logical-table units and before `joins`. It states the table, whether it is an alias and of what, its object in the database or its SQL exactly, its keys, pointers to the foreign keys from and to it, the table sources that read it, and every column in repository order with type, precision, scale, nullable and external name, marked MAPPED (by which logical columns) or NOT MAPPED. A logical-table packet states the table's keys, each source's level of detail, and `nullable` on every mapping, and says "not recorded" where the repository records nothing. The hierarchies packet lists each level's keys.
- **Decision: required rows.** The physical table, each of its definitions, each of its keys and each of its columns are required rows of the table's unit; so are the keys of a logical table, in its unit. Reason: the unit exists so that a person can create the table, and `check-doc` can only tell that a document dropped a column if the column is required. Level keys are not required: the dimension is, as before.
- **Consequence of that decision.** A required row is cited in one packet only (the contract with `check-doc`), so a logical-table packet no longer cites a physical table or column: it names them, gives the column's type, precision, scale and nullable beside the mapping, and points at the table with `->`. The SQL of a table that is a query moved from the logical-table packet to the physical-table packet.
- **Numbers.** Sample Sales: 15 units to 28 (13 physical tables), required rows 125 to 272, physical columns 74 to 113 (43 mapped by no logical column), largest packet 120 lines, 0 boundary rows. `sampleapp`, 3 slices: 33 units to 51, 157 required rows to 333. `bi-analytics-2013`, 18 slices: 325 to 458, 2,502 to 4,625. No packet over 120 lines.
- **Tests changed, and why.** `DocCheckTest` and `AssembleDocTest` state the number of required rows of the pair fixture as a premise: 7 before, 13 now (two physical tables and their four columns). `PacketsTest`: the unit list has a `dbtable` unit; the logical table's header no longer names `physical_columns` and `physical_tables`; a column pair prints `nullable not recorded`; the long SQL is looked for in the `physical_table` unit. `SliceFixture.expectedRequired` and `danglingReferences` know the new required rows and reference lists. New tests: the walk (`SliceTest`), the unit's text and a 401-column table split between columns (`PacketsTest`).
- **Surprise.** A complex join's condition can name a column of a third table. Before, that table came as a boundary row; now it comes whole and is not counted as one. It did not occur on the samples (boundary rows are unchanged).
- **What this breaks.** A document written from an older Slice does not pass `check-doc` against a new one: its physical tables, columns and keys are not covered. The skills under `skills/` were not touched; they do not yet know the unit kind `physical_table`.

### 23 November 2026 (second round): join paths, level order and parent-child definitions in the slice and the packets

- **Walk.** `lts_tables` and `lts_joins` come with their source. A physical table a source lists or joins belongs to the Slice, whole, even when no column is mapped from it; so does a source's closure table, and the physical join a source's join points at.
- **Logical-table packet.** Under each source: the tables it is built on, in the source's order (each line cites its `lts_tables` row and points at the table's unit; a table nothing is mapped from says so), then each join with join type and cardinality exactly as written and the columns of its condition. A source with one table says it makes no join; a source with several tables and no join recorded says so. A source that names a closure table says so. A lookup table and a descriptor column are stated.
- **Decision: each `lts_joins` row is a required row** of the logical table's unit. It is the fact that decides which rows a measure counts. `lts_tables` rows are cited and not required.
- **Hierarchies packet.** Each level states the level directly above and below it. A parent-child dimension states its relationship table and the member, parent, distance and leaf columns, found through the logical tables its level keys are on; or says none is recorded. Those lines carry `->` pointers and name the units that hold the citations, because the source and the physical table are required rows of other units.
- **Physical-table packet.** The description, the parent-child columns, `stands for column ...` on an alias column, and `nullable true` or `nullable not stated` with one line explaining what the export does.
- **Invariant checked.** On all 21 sample slices every required row is marked on exactly one line of exactly one packet and no packet is over 120 lines (`PacketsTest`, `SliceTest`).
- **Numbers.** Sample Sales: 28 units, required rows 272 to 274 (the two join rows of `LTS Employee`). `sampleapp`: 51 units, 335 required rows. `bi-analytics-2013`: 458 units to 475, 4,625 required rows to 4,683.

**Gaps wording (later fix, during story 4.1).** In Sample Sales, `WhereClause@mdsid` in the gaps packet led a writer to ask whether content filters were read; they are. A row of `unparsed` that is only an identifying attribute of an element whose content is read is now listed apart, one line per attribute: `- @mdsid, identifier of AggrRule (x13), ...: identifier of each not read; its content is read.`, under the heading "Identifiers that were not read; the content of their elements is read", and left out of the per-object-type lists; the totals still count every row. Which rows these are is source-format knowledge, so it is decided in the repository adapter (`src/main/java/obi2pbi/in/mds/IdentifierAttributes.java`: any `@mdsid`; `@refId` of a `Ref...` element; `@name` of `Expr`, `WhereClause`, `FragmentContent`, `GroupBy`, `LogicalColumnExpr`, `MeasureDefn`, `AttrDefn`; never a catalog attribute) and recorded by the slice command in the Slice's manifest (`unparsed_identifiers_only`), which `Packets` reads, so core holds no format words and packets can be rebuilt from the folder. Test: `PacketsTest.testAnIdentifierOfAnElementWhoseContentIsReadIsListedApartInTheGapsPacket`. Effect on the re-cut slices: only the `gaps` packet of each of the 21 subject areas changed (shorter per-type lists, the new 3-line list); no unit was added, split or renamed; `check-doc` on `as-is/sampleapp/sample-sales.md` and `as-is/bi-analytics-2013/iryna-bradzetskaya-mt-row-level-security.md` still gives 0 findings. (`overview` packets also differ, only in the Knowledge Base's extraction time stamp, because `compare-all` re-extracted `kb/`.)

## Spec Change Log

## Review Triage Log

## Design Notes

The folder name for a subject area is its name in lower case with runs of other characters replaced by one hyphen. The exact name is kept in the slice's `manifest.csv`.

A reader for the Knowledge Base's own CSV is needed here for the first time. It is an inbound adapter like the others and reads the table list from `manifest.csv`, never from the directory.

## Verification

**Commands:**

- `./build.sh` -- expected: the jar is built at `build/obi2pbi.jar` and every test passes
- `java -jar build/obi2pbi.jar slice kb/sampleapp` -- expected: lists the subject areas
