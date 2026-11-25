---
status: final
stepsCompleted: [1, 2, 3, 4]
inputDocuments:
  - documents/planning-artifacts/prds/prd-POC-OBIEE-2-PBI-2026-11-23/prd.md
  - documents/planning-artifacts/architecture/architecture-POC-OBIEE-2-PBI-2026-11-23/ARCHITECTURE-SPINE.md
  - documents/planning-artifacts/migration-plan.md
---

# POC-OBIEE-2-PBI - Epic Breakdown

## Overview

This document breaks phase 1 of POC-OBIEE-2-PBI into epics and stories. It draws on the PRD and the Architecture Spine. There is no UX design, because the product has no user interface.

Phase 1 is finished when one FSD has been taken to a to-be design (PRD SM-1). The Knowledge Base covers both the repository and the Web Catalog from the start (owner's decision, 2026-11-23), so Epic 1 builds both.

The first attempt's Python extractor worked well and is the reference implementation. Epic 1 is a port of it to Java, guided by `docs/port-guide/`.

Today's semantic model is a result in its own right and comes straight after the Knowledge Base (owner's decision, 2026-11-23), so Epic 2 delivers it per subject area before any FSD work.

All four epics were drafted in one pass at the owner's request, without the step-by-step approvals the workflow normally asks for.

## Requirements Inventory

### Functional Requirements

FR-1: The jar reads a repository exported as MDS XML and writes tables, columns, formulas, joins, hierarchies, variables and security rules.
FR-2: The jar reads a Web Catalog folder and writes dashboards, dashboard pages, reports, report columns and filters.
FR-3: Every formula and expression is written character for character as the source holds it.
FR-4: For each presentation column, the Knowledge Base holds the chain to its logical column, logical table source, and physical table and column.
FR-5: For each report column, the Knowledge Base records the presentation column it uses, or why none was found, within one Estate only.
FR-6: Each run writes an errors list and a list of source content that no table carries; the jar exits non-zero when the errors list is not empty.
FR-7: Every row has a Row identifier that is the same on every run of the same Estate.
FR-8: Given a confirmed Match table, or subject area names, the jar writes the Slice needed for that piece of work.
FR-9: The jar reads a `.docx` FSD and writes its text, keeping headings, lists and tables.
FR-10: The trace Skill reads the FSD text and the Knowledge Base and writes the Match table, each Match with a confidence score, its basis, and the Row identifier it points to.
FR-11: A check reads the Match table and reports failures: missing rows, lineage that does not reach a database table, FSD items with no row, and disagreement with an existing report.
FR-12: The Operator sets each Match to confirmed or rejected; later steps use confirmed Matches only.
FR-13: The as-is Skill reads the Slice and writes one As-is document per subject area the Tab touches, with citations and numbered open points.
FR-14: The to-be Skill lists each new table and view the Tab needs, with columns, types, and the old column each new column comes from.
FR-15: The to-be Skill lists the Power BI tables, relationships, measures and hierarchies the Tab needs, each tied to the As-is document.
FR-16: The To-be design lists each field the FSD puts on the Tab, the semantic model item that supplies it, and the confirmed Match behind it.
FR-17: The three Skills work in GitHub Copilot and in Claude, as plain markdown, each its own workflow, and are started through one agent.
FR-18: A check reads a skill-written document and its Slice and reports rows not covered and citations that point nowhere.
FR-19: A review Skill goes through every AI-written output against its inputs before it is given to users; outputs are drafts until reviewed.
FR-20: The jar's Knowledge Base is compared with the first attempt's on the same input, and the Operator confirms the comparison.
FR-21: Each Knowledge Base and Slice describes itself in `schema_tables.csv` and `schema.csv`: every table and column, its meaning, and what it refers to.

### NonFunctional Requirements

NFR-1: The jar runs on Java 11 and uses nothing outside the Java standard library.
NFR-2: The jar is one file, copied to the target system; no installer, no network access.
NFR-3: No AI takes part in anything the jar does.
NFR-4: Any step can be run again without manual clean-up and gives the same result on the same input.
NFR-5: Documents are markdown and tables are CSV.
NFR-6: The jar needs no database access and no running OBIEE server.
NFR-7: The Skills run in GitHub Copilot with GPT-5.6 Sol, a short-context model: one small unit at a time, resumable, with the jar preparing the small inputs.

### Additional Requirements

- No starter template. The project is built with `javac --release 11` and `jar` from `build.sh` and `build.cmd`; tests are plain Java classes run by a small in-repo runner (AD-15).
- Package layout `obi2pbi.cli`, `obi2pbi.core`, `obi2pbi.in.mds`, `obi2pbi.in.catalog`, `obi2pbi.in.docx`, `obi2pbi.out`; `core` depends on the standard library only (AD-6).
- All XML is read through one recording wrapper, so unread content is found by observation (AD-3).
- Row identifiers are `<estate>::<layer>::<qualified name>` (AD-4).
- One CSV writer: UTF-8, RFC 4180, `\n`, fixed column order, rows sorted, `manifest.csv` per output folder (AD-8).
- A Slice has the Knowledge Base's file names and columns (AD-9).
- `fsd.md` carries an anchor on every paragraph and table cell (AD-10).
- `matches.csv` is written by the trace skill only; the operator's verdicts go in `decisions.csv` (AD-11).
- Skill documents cite rows as `[kb:<row identifier>]` (AD-12).
- Each skill is authored under `skills/` and copied unchanged to `.agents/skills/` and `.claude/skills/`; a test checks the copies match (AD-13).
- One agent, `obiee-migration-agent`, starts the skills. It is wired like the BMad developer agent (`.github/agents/*.agent.md` for GitHub Copilot, a skill for Claude) and does not depend on BMad's scripts.
- Skills work in a small context (AD-17): `slice` writes `units.csv` and one packet per work unit; a skill reads one packet, appends one section, records progress, and can resume. Lookups go through the jar's `find-columns` command.
- Nothing an AI wrote goes to users unreviewed (AD-18): outputs start as drafts, one review skill checks them against their inputs, and the agent only presents a reviewed output as ready.
- Every skill-written document has an Open questions section; answers are the user's, are kept in an answers file, and survive regeneration.
- One jar, four commands, exit codes 0, 2 and 3; a command rewrites its own output folder only (AD-14).
- The first attempt's Python extractor is the reference implementation. `docs/port-guide/` describes what it reads, the tables and columns it writes, and its rules; `docs/formats/` holds its notes on the two source formats.
- Knowledge Base table names and columns follow the port guide, without the `_guarded` columns. The estate name takes the place of the Python program's `rpd-id`.
- The port rule: port the reference as it is, and change behaviour only where `docs/port-guide/00-read-me-first.md` lists a deliberate difference.
- Licence is not a constraint on the sample data (owner, 2026-11-23). `estates/`, `kb/` and `slices/` are git-ignored only because they are large or generated.

### UX Design Requirements

None. The product has no user interface.

### FR Coverage Map

FR-1: Epic 1 - read the repository
FR-2: Epic 1 - read the Web Catalog
FR-3: Epic 1 - formulas kept exactly, in the repository and in reports
FR-4: Epic 1 - lineage
FR-5: Epic 1 - link report columns to the repository
FR-6: Epic 1 - errors and not-read lists, for both halves
FR-7: Epic 1 - row identifiers
FR-8: Epic 2 - slice by subject area; Epic 4 - slice by confirmed matches
FR-9: Epic 3 - FSD to text
FR-10: Epic 3 - propose matches, using existing reports as evidence
FR-11: Epic 3 - check matches, including against an existing report
FR-12: Epic 3 and Epic 4 - operator decisions, used by slice and the to-be skill
FR-13: Epic 2 - as-is document, per subject area
FR-14: Epic 4 - new tables and views
FR-15: Epic 4 - Power BI semantic model
FR-16: Epic 4 - the tab's fields
FR-17: Epics 2, 3 and 4 - one agent and three skills, in both clients
FR-18: Epic 2 - check-doc; reused in Epic 4
FR-19: Epic 2 - review skill; extended in Epics 3 and 4
FR-20: Epic 1 - comparison with the reference, confirmed by the owner
FR-21: Epic 1 - schema files, from story 1.1 onwards; carried into every Slice in Epics 2 and 4

## Epic List

### Epic 1: A Knowledge Base from the repository and the Web Catalog

The Operator runs one command on an OBIEE export and gets a Knowledge Base he can open and trust: every layer of the repository, every dashboard and report in the catalog, every formula exactly as written, lineage down to the database column, and a plain list of what was not read.

**FRs covered:** FR-1, FR-2, FR-3, FR-4, FR-5, FR-6, FR-7, FR-20, FR-21

### Epic 2: Today's semantic model

The Operator starts the migration agent from GitHub Copilot or Claude, picks a subject area, and gets a complete plain-language description of the semantic model as it exists today. Every statement is tied to a Knowledge Base row, and the AI's open questions are listed for the Operator to answer. No FSD is needed.

**FRs covered:** FR-8, FR-13, FR-17, FR-18, FR-19

#### Story 2.4: Review the as-is document before it goes to users

As an Operator,
I want every as-is document reviewed against the Knowledge Base before I pass it on,
So that people do not build from a statement that reads well and is wrong.

**Acceptance Criteria:**

**Given** a document the as-is skill has just written
**When** I look at its first line
**Then** it says `Status: draft, not reviewed`

**Given** a draft
**When** the review skill runs, from the agent menu or as the last step of "Describe a subject area"
**Then** it goes through the document one unit at a time, reading the packet before the section, and checks that every statement is supported by the row it cites, nothing in the packet is missing, formulas are quoted exactly, and each open question says how to get the answer

**Given** a finding
**When** it is recorded
**Then** it is a row in `<document>.review.csv`; a clear error is corrected, and a doubt that cannot be settled becomes an open question

**Given** every unit reviewed and `check-doc` passing
**When** the review ends
**Then** the first line becomes `Status: reviewed <date>` with the counts of findings fixed and questions open

**Given** a document that is still a draft
**When** I ask the agent for it
**Then** it tells me it is a draft and not ready to share

**Given** a copy of the Sample Sales document with errors planted by hand
**When** the review runs
**Then** the number it finds is recorded, as the measure of whether the review works

## Epic 3: From an FSD to confirmed matches

The Operator takes an FSD and ends with a checked list of which OBIEE column stands behind each item on the tab, with his own decision recorded against each.

**FRs covered:** FR-9, FR-10, FR-11, FR-12, FR-17, FR-19

### Epic 4: The to-be design

The Operator gets the To-be design for the tab, built on the confirmed matches and today's semantic model, and has taken one FSD through the whole chain.

**FRs covered:** FR-8, FR-12, FR-14, FR-15, FR-16, FR-17, FR-19

## Epic 1: A Knowledge Base from the repository and the Web Catalog

The Operator runs one command on an OBIEE export and gets a Knowledge Base he can open and trust. Stories 1.1 to 1.6 port the repository reader; stories 1.7 to 1.10 port the catalog reader. Each story follows `docs/port-guide/` for the tables, columns and rules.

### Story 1.1: Build the jar and extract the physical layer

As an Operator,
I want to build one jar and run `extract` on a repository export,
So that I get the database tables and columns OBIEE knows about as CSV files.

**Acceptance Criteria:**

**Given** a machine with a JDK and no other tooling
**When** I run `build.sh` or `build.cmd`
**Then** one jar is produced, compiled with `--release 11`, using only `java.base` and `java.xml`
**And** the in-repo test runner runs every test and reports pass or fail

**Given** an estate folder `estates/<estate>/rpd/` holding MDS XML
**When** I run `extract`
**Then** `kb/<estate>/` contains `physical_tables.csv`, `physical_columns.csv` and `manifest.csv`
**And** every row has a Row identifier of the form `<estate>::phys::<qualified name>`
**And** an alias table names the table it stands for

**Given** the same input
**When** I run `extract` twice
**Then** every file except `manifest.csv` is byte-identical between the runs

**Given** the port guide
**When** the story is done
**Then** the tables written have the columns the port guide lists for them, without `_guarded` columns, and `estates/` and `kb/` are git-ignored

**Given** any Knowledge Base the jar writes
**When** I open `schema_tables.csv` and `schema.csv`
**Then** every table and every column is listed with a plain description, and each column that refers to another table names that table and column
**And** every later story adds its tables and columns to the schema in the same story

**Given** a wrong command line
**When** I run the jar
**Then** it prints usage and exits with code 2

**Given** the list of deliberate differences in `docs/port-guide/00-read-me-first.md`
**When** this or any later Epic 1 story changes behaviour from the reference
**Then** the change is on that list, or is added to it in the same story

### Story 1.2: List what was not read, and what went wrong

As an Operator,
I want every run to tell me what it skipped and what failed,
So that a gap in the Knowledge Base is something I can see.

**Acceptance Criteria:**

**Given** a source file with an attribute, element or text node that no table carries
**When** I run `extract`
**Then** `unparsed.csv` has a row naming the object type, the item and how many times it occurred
**And** that row comes from what the readers actually read, with no hand-kept list in the code

**Given** an object type with no reader
**When** I run `extract`
**Then** `unparsed.csv` lists it with a count, and the run continues

**Given** a file with broken XML, or two objects that produce the same Row identifier
**When** I run `extract`
**Then** `errors.csv` has a row for it with a kind and a reason, the other files are still written, and the jar exits with code 3

**Given** a clean fixture
**When** I run `extract`
**Then** `errors.csv` holds only its header and the jar exits with code 0

### Story 1.3: Extract the logical layer with formulas exactly as written

As an Operator,
I want the business model in the Knowledge Base,
So that I can read what each measure computes without opening OBIEE.

**Acceptance Criteria:**

**Given** a repository with logical tables, logical columns and logical table sources
**When** I run `extract`
**Then** `logical_tables.csv`, `logical_columns.csv`, `logical_table_sources.csv`, `lts_column_map.csv` and `aggregation_rules.csv` are written with `bmm` Row identifiers

**Given** a formula with leading spaces, or one starting with `-` or `=`
**When** it is extracted
**Then** the text in the CSV equals the source text character for character

**Given** an expression the source holds in three forms (template, rendered text, references)
**When** it is extracted
**Then** each form is in its own column

**Given** a measure with a default aggregation rule and a dimension-specific override
**When** it is extracted
**Then** both rules are rows in `aggregation_rules.csv`, with the default flagged

**Given** a logical table source with a content filter
**When** it is extracted
**Then** the filter text is captured exactly

### Story 1.4: Extract the presentation layer

As an Operator,
I want subject areas, presentation tables and presentation columns in the Knowledge Base,
So that I can start from the names a report author sees.

**Acceptance Criteria:**

**Given** a repository with subject areas
**When** I run `extract`
**Then** `presentation_subject_areas.csv`, `presentation_tables.csv` and `presentation_columns.csv` are written with `pres` Row identifiers

**Given** a presentation column
**When** it is extracted
**Then** its row names the logical column it shows

**Given** a subject area with an implicit fact column
**When** it is extracted
**Then** the subject area's row names that column

**Given** a presentation column whose logical column cannot be found
**When** it is extracted
**Then** the row is still written and `errors.csv` records the unresolved reference

### Story 1.5: Record lineage from presentation column to database column

As an Operator,
I want one table that traces each presentation column to the database,
So that "where does this number come from" is one lookup.

**Acceptance Criteria:**

**Given** the three layers are extracted
**When** I run `extract`
**Then** `lineage.csv` has a row for each chain: presentation column, logical column, logical table source, physical table, physical column

**Given** a logical column fed by more than one logical table source
**When** lineage is written
**Then** there is one row per source

**Given** a presentation column that reaches no physical column
**When** lineage is written
**Then** it still has a row, with a `resolution` value giving the reason

**Given** the hand-made fixture with a known chain
**When** the tests run
**Then** the lineage rows equal the expected rows recorded with the fixture

### Story 1.6: Extract joins, hierarchies, variables and security rules

As an Operator,
I want the parts of the repository that change a number without changing its formula,
So that the As-is document can describe them.

**Acceptance Criteria:**

**Given** a repository with physical and logical joins
**When** I run `extract`
**Then** `physical_joins.csv` and `bmm_joins.csv` hold each join with its tables, its type (inner or outer) and its condition text exactly as written

**Given** dimensions with levels
**When** I run `extract`
**Then** `logical_dimensions.csv`, `logical_levels.csv`, `presentation_hierarchies.csv`, `presentation_levels.csv` and `presentation_level_columns.csv` hold each hierarchy and its levels in order, with ragged, skip-level and parent-child kept as flags

**Given** session and repository variables and their init blocks
**When** I run `extract`
**Then** `variables.csv` and `init_blocks.csv` hold each one with its text exactly as written, and a value the port guide treats as a credential is redacted

**Given** application roles with row-level filters
**When** I run `extract`
**Then** `security_rls.csv` holds each filter and privilege with its kind, the role it belongs to and the object it applies to

**Given** the public sample repository, if it is available locally
**When** I run `extract`
**Then** the run completes, and the counts of parsed objects, `unparsed.csv` rows and `errors.csv` rows are noted in the story's completion notes

### Story 1.7: Extract dashboards, their pages and what is placed on them

As an Operator,
I want `extract` to read the Web Catalog when the estate has one,
So that the Knowledge Base shows which dashboards and pages exist and what each page holds.

**Acceptance Criteria:**

**Given** `estates/<estate>/catalog/`
**When** I run `extract`
**Then** `dashboards.csv`, `dashboard_pages.csv` and `dashboard_contents.csv` are written with `cat` Row identifiers, pages and placements in order

**Given** a page that places a report or a saved filter by path
**When** it is extracted
**Then** the placement's row names the object it points to and how the path resolved (exact, after folding case, or unresolved)

**Given** an estate with a catalog and no repository, or a repository and no catalog
**When** I run `extract`
**Then** it extracts what is there, and `manifest.csv` records which halves were present

**Given** a file in the catalog folder that is not a catalog object (an image, a template)
**When** I run `extract`
**Then** it is counted in `unparsed.csv` as not a catalog object, and is not an error

**Given** a catalog object type with no reader
**When** I run `extract`
**Then** `unparsed.csv` lists it with a count, and `coverage.csv`, `coverage_summary.csv` and `catalog_versions.csv` are written as the port guide describes

### Story 1.8: Extract reports, their columns and their views

As an Operator,
I want each existing report's columns, formulas and views in the Knowledge Base,
So that calculations that exist only in reports are not missed.

**Acceptance Criteria:**

**Given** a catalog with reports (OBIEE calls them analyses)
**When** I run `extract`
**Then** `analyses.csv`, `analysis_criteria.csv`, `analysis_columns.csv` and `analysis_views.csv` are written

**Given** a report column with a formula
**When** it is extracted
**Then** the formula text equals the source text character for character, and the row says whether it is a plain column or a calculation

**Given** the subject area name a report was built on
**When** it is extracted
**Then** it is captured exactly as the catalog wrote it, quotes included

**Given** a view inside a report
**When** it is extracted
**Then** its row holds the view type in the source's own words, its position, and the chart kind where the source gives one

### Story 1.9: Extract filters, prompts and saved filters

As an Operator,
I want the filters and prompts on reports and dashboard pages in the Knowledge Base,
So that the As-is document can say what each report restricts.

**Acceptance Criteria:**

**Given** a report or dashboard page with filters
**When** I run `extract`
**Then** `analysis_filters.csv` holds each filter expression with its scope (report or dashboard), its operator and its text exactly as written

**Given** a filter that points to a saved filter by path
**When** it is extracted
**Then** its row names the saved filter and how the path resolved

**Given** saved filters and prompts inside reports
**When** I run `extract`
**Then** `saved_filters.csv` and `analysis_prompts.csv` are written

**Given** prompts that live in separate view objects, which the reference implementation does not read
**When** I run `extract`
**Then** those objects are counted in `unparsed.csv`, and the README says that `analysis_prompts.csv` covers prompts inside reports only

### Story 1.10: Link report columns to the repository

As an Operator,
I want each report column traced to its presentation column,
So that an existing report can be followed down to the database.

**Acceptance Criteria:**

**Given** an estate with both halves
**When** I run `extract`
**Then** `report_lineage.csv` has one row per report column, with the presentation column it uses or a `resolution` value saying why none was found

**Given** a report column whose formula is a calculation over several columns, or one that names an uploaded dataset outside the repository
**When** it is processed
**Then** its `resolution` says so, and it is not forced onto one presentation column

**Given** an estate with only one half
**When** I run `extract`
**Then** `report_lineage.csv` is written with every row unresolved for that reason, and `lineage.csv` is unchanged

**Given** the hand-made matched fixture
**When** the tests run
**Then** the resolved rows equal the expected rows recorded with the fixture

**Given** the public sample repository and catalog, if available locally
**When** I run `extract` on each
**Then** the table row counts are compared with the reference implementation's Knowledge Base, and any difference is explained in the completion notes

### Story 1.11: Confirm the Knowledge Base matches the reference

As an Operator,
I want a comparison of the jar's Knowledge Base with the one the first attempt produced from the same input,
So that I can confirm the port is right before anything is built on it.

**Acceptance Criteria:**

**Given** the sample repositories and catalog fetched at the versions the first attempt used
**When** `extract` and the comparison run
**Then** a report covers every table in `reference/knowledge-base/`, cell by cell, ignoring only the `_guarded` columns

**Given** a difference
**When** it is reported
**Then** it is assigned to a numbered entry of the deliberate-differences list, or listed as a defect with the table, Row identifier, column and both values

**Given** a defect
**When** the story is worked
**Then** it is fixed in the jar, or the list gains a numbered entry with the reason; it is never ignored

**Given** the finished report
**When** Kamal has read it and confirmed
**Then** Epic 1 is marked done, and not before

## Epic 2: Today's semantic model

The Operator picks a subject area and gets a plain-language description of the semantic model as it exists today. This is the first result after the Knowledge Base and needs no FSD.

### Story 2.1: Cut a slice for a subject area

As an Operator,
I want to run `slice` with a subject area name,
So that a skill reads only that subject area and not the whole Knowledge Base.

**Acceptance Criteria:**

**Given** a Knowledge Base and one or more subject area names
**When** I run `slice`
**Then** `slices/<estate>/<subject-area>/` holds the same file names and columns as the Knowledge Base, with only the rows reachable from that subject area's presentation columns through lineage

**Given** those columns
**When** the slice is cut
**Then** it includes the joins, variables, hierarchies and security rules that apply to their tables
**And** it includes what the reference slicer left out: hierarchies, the definitions of tables that are really queries, and columns that exist only as formulas
**And** variables are matched by exact name within the estate

**Given** the Knowledge Base holds reports built on that subject area
**When** the slice is cut
**Then** it includes those reports, their columns, filters and views, and the dashboard pages that place them

**Given** the Knowledge Base's `errors.csv` and `unparsed.csv`
**When** the slice is cut
**Then** the slice's own copies hold only the entries about objects inside it

**Given** a slice
**When** it is written
**Then** it also holds `units.csv`, the ordered list of work units, and one packet per unit under `packets/`: a text file of at most 400 lines with everything about that unit already joined

**Given** no subject area name
**When** I run `slice`
**Then** it lists the subject areas in the Knowledge Base with their column and measure counts

**Given** a name that matches no subject area
**When** I run `slice`
**Then** it says so, lists the names that exist, and exits with code 2

### Story 2.2: Describe today's semantic model with the as-is skill

As an Operator,
I want an agent I can start from GitHub Copilot or Claude that writes a complete As-is document for a subject area,
So that I have today's semantic model in plain language, with the AI's open questions listed for me to answer, before any FSD arrives.

**Acceptance Criteria:**

**Given** the agent's files
**When** I start `obiee-migration-agent` in GitHub Copilot or in Claude
**Then** it greets me, shows a menu, and can run "Describe a subject area"

**Given** an estate and a subject area
**When** I ask the agent to describe it
**Then** it cuts the slice if needed and writes `as-is/<estate>/<subject-area>.md`, in the plain business-language layout of the first attempt's Sample Sales document

**Given** the As-is document
**When** I read it
**Then** it covers every presentation table and column, measure, logical table and source, join, hierarchy, variable and security rule in the slice, and every report-level calculation
**And** anything left out is listed in an appendix with the reason

**Given** any factual statement in it
**When** I look at its end
**Then** it cites a Row identifier as `[kb:<row identifier>]`

**Given** a subject area of any size, in GitHub Copilot with a short-context model
**When** the skill runs
**Then** it works one unit at a time from the packets, appends each section to the document, records progress, and can be stopped and resumed without losing or repeating work

**Given** something the slice does not answer, or the AI is unsure about, or wants more information on
**When** the document is written
**Then** it appears in an Open questions section as a numbered question, with why it matters, how to get the answer, and an empty `Answer:` line

**Given** I answer a question, in the document or in chat
**When** I ask the agent to take in the answers
**Then** the answer is kept in `as-is/<estate>/<subject-area>.answers.md`, the affected sections are rewritten and cite `[answer:Q<n>]`, and the question moves to an Answered questions section

**Given** the Knowledge Base is re-extracted and the document regenerated
**When** the skill runs
**Then** earlier answers are still applied, and none is changed or removed

**Given** the skill and agent files
**When** the tests run
**Then** the copies in both client folders equal `skills/`

### Story 2.3: Check the as-is document is complete

As an Operator,
I want to run `check-doc` on an As-is document,
So that "complete" is checked by a program and not taken on the AI's word.

**Acceptance Criteria:**

**Given** a document and the slice it was written from
**When** I run `check-doc`
**Then** it reports every required row the document neither cites nor lists as not described

**Given** a citation `[kb:...]` naming a row that is not in the slice, or `[answer:Q<n>]` with no such answer
**When** I run `check-doc`
**Then** it reports it with the line number

**Given** any finding
**When** the run ends
**Then** the jar exits with code 3; with none, code 0, and it prints rows required, rows covered, and open and answered question counts

**Given** the as-is skill has written a document
**When** it finishes
**Then** it has run `check-doc` and dealt with every finding before reporting done

## Epic 3: From an FSD to confirmed matches

The Operator takes an FSD and ends with a checked list of which OBIEE column stands behind each item on the tab.

### Story 3.1: Turn the FSD into text with anchors

As an Operator,
I want to run `fsd-text` on an FSD,
So that a skill can read it and point to exact places in it.

**Acceptance Criteria:**

**Given** `work/<tab>/fsd.docx`
**When** I run `fsd-text`
**Then** `work/<tab>/fsd.md` holds the document's headings, paragraphs, lists and tables in order

**Given** any paragraph or table cell
**When** it is written
**Then** it carries an anchor (`[p12]`, `[t3.r2.c1]`) numbered in document order, and the same input always gives the same anchors

**Given** a table with merged or empty cells
**When** it is written
**Then** rows and cells stay aligned

**Given** an old-style `.doc`, or a file that is not a Word document
**When** I run `fsd-text`
**Then** the jar refuses it with a message saying to save it as `.docx`, and exits with code 2

### Story 3.2: Propose matches with the trace skill

As an Operator,
I want a skill that reads the FSD and the Knowledge Base and proposes a match for each item,
So that I review proposals and do not search the repository by hand.

**Acceptance Criteria:**

**Given** `fsd.md` and `kb/<estate>/`
**When** I run the trace skill in GitHub Copilot or in Claude
**Then** it writes `fsd_items.csv` (one row per FSD item that needs data, with its anchor) and `matches.csv` with the columns fixed in AD-11

**Given** an FSD item to look up
**When** the skill searches for candidates
**Then** it runs the jar's `find-columns` command, which returns a short ranked list, and never opens a Knowledge Base file whole

**Given** an FSD with many items
**When** the skill runs
**Then** it works a few items at a time, appends their rows, records progress, and can resume

**Given** an item with a plausible presentation column
**When** the skill writes its row
**Then** the row has status `proposed`, a confidence from 0 to 100, one line of basis, and the Row identifier

**Given** an item with no plausible column
**When** the skill writes its row
**Then** the row has status `unmatched` and no Row identifier

**Given** the FSD names the dashboard page it replaces, and the Knowledge Base holds that page's reports
**When** the skill proposes a match that agrees with a column of one of those reports
**Then** the basis says so and cites the report column

**Given** the proposals are written and `check-matches` has run
**When** the review skill runs
**Then** it re-checks each proposed match against the FSD item and the Knowledge Base row, lowers the score of any it doubts and says why, and only then am I asked to confirm

**Given** a missing input file
**When** the skill starts
**Then** it names the missing file and stops

**Given** the skill's files
**When** the tests run
**Then** the copies under `.agents/skills/trace/` and `.claude/skills/trace/` equal `skills/trace/`

### Story 3.3: Check the matches

As an Operator,
I want to run `check-matches` on the match table,
So that mechanical mistakes are caught before I spend time judging meaning.

**Acceptance Criteria:**

**Given** a match whose Row identifier is not in the Knowledge Base
**When** I run `check-matches`
**Then** `check.csv` records a failure for that item

**Given** a match whose lineage does not reach a database table
**When** I run `check-matches`
**Then** `check.csv` records a failure with the reason from `lineage.csv`

**Given** an item in `fsd_items.csv` with no row in `matches.csv`, or an anchor that is not in `fsd.md`
**When** I run `check-matches`
**Then** `check.csv` records a failure

**Given** a match that disagrees with the columns of the existing report the FSD replaces
**When** I run `check-matches`
**Then** `check.csv` flags it as a warning, with the report column it disagrees with; a warning does not change the exit code

**Given** any failure
**When** the run ends
**Then** the jar exits with code 3; with none, code 0

**Given** `decisions.csv` exists
**When** I run `check-matches`
**Then** it also reports any decision that names an unknown item, an unknown Row identifier, or a value other than `confirmed` or `rejected`

## Epic 4: The to-be design

The Operator gets the To-be design for the tab and takes one FSD through the whole chain.

### Story 4.1: Cut a slice for a tab

As an Operator,
I want to run `slice` for my confirmed matches,
So that the to-be skill reads only what the tab needs.

**Acceptance Criteria:**

**Given** `work/<tab>/decisions.csv` with confirmed rows
**When** I run `slice`
**Then** `work/<tab>/slice/` holds the same file names and columns as the Knowledge Base, with only the rows reachable from the confirmed columns through lineage, by the same rules as a subject-area slice

**Given** rejected or undecided matches
**When** I run `slice`
**Then** they contribute nothing

**Given** the confirmed columns
**When** the slice is cut
**Then** the run prints the subject areas the tab touches, and says for each whether `as-is/<estate>/<subject-area>.md` exists

### Story 4.2: Design the new tables and views with the to-be skill

As an Operator,
I want a skill that sets out the new tables and views the tab needs,
So that Data Engineering has a starting point they can correct and sign.

**Acceptance Criteria:**

**Given** the tab's slice, `decisions.csv`, and the As-is document for each subject area the tab touches
**When** I run the to-be skill
**Then** `work/<tab>/to-be.md` lists each new table and view with its columns and types

**Given** a subject area the tab touches has no As-is document
**When** the skill starts
**Then** it names the missing document and stops

**Given** each new column
**When** I read its row
**Then** it names the old column it comes from, with a citation, and is marked as a proposal

**Given** something the Knowledge Base cannot supply (keys, decimal places, whether a column may be empty)
**When** the design is written
**Then** it is marked as missing and nothing is guessed

**Given** no target platform has been chosen
**When** the design is written
**Then** types are given in platform-neutral terms and the document says so

### Story 4.3: Design the Power BI model and list the tab's fields

As an Operator,
I want the to-be skill to also set out the Power BI semantic model and the tab's fields,
So that the To-be design is complete enough to build from.

**Acceptance Criteria:**

**Given** the As-is documents
**When** I run the to-be skill
**Then** `to-be.md` lists the tables, relationships, measures and hierarchies the tab needs, each tied to the As-is document

**Given** a measure
**When** I read its entry
**Then** it shows the OBIEE formula it replaces, exactly as extracted

**Given** an OBIEE feature with no direct Power BI equivalent
**When** the design is written
**Then** it is flagged for a decision and not translated silently

**Given** `to-be.md` is written and `check-doc` passes
**When** the review skill runs
**Then** it checks each new column, measure and tab field against its inputs, and the document's status becomes reviewed only when it finishes

**Given** `fsd_items.csv`
**When** the design is written
**Then** every item appears in the list of tab fields with the model item that supplies it and the confirmed match behind it, or is listed as unresolved

**Given** the skill's files
**When** the tests run
**Then** the copies in both client folders equal `skills/to-be/`

### Story 4.4: Take one FSD from start to finish

As an Operator,
I want to run the whole chain once on one FSD and have the steps written down,
So that phase 1's finish line is met.

**Acceptance Criteria:**

**Given** an OBIEE export and one FSD
**When** I follow the README's eight steps
**Then** I end with `to-be.md`, using only the jar and the three skills

**Given** the As-is document from that run
**When** I check twenty of its statements against the rows they cite
**Then** the result is recorded in the story's completion notes

**Given** the run
**When** it is finished
**Then** the completion notes say which data it ran on (sample or real), what failed or needed a manual fix, and which open questions from the PRD it answered

**Given** no real FSD or export is available yet
**When** the story is worked
**Then** it runs on the hand-made fixtures, and the notes state that SM-1 is met on fixture data only
