---
title: PRD — OBIEE to Power BI Migration Assistant (reboot), phase 1
status: final
created: 2026-11-23
updated: 2026-11-23
---

# PRD — OBIEE to Power BI Migration Assistant (reboot), phase 1

## 0. Purpose of this document

This PRD says what phase 1 must do. It is for Kamal, who owns and builds it, and for whoever writes the architecture next. It builds on the product brief at `../../briefs/brief-POC-OBIEE-2-PBI-2026-11-22/brief.md` and does not repeat its reasoning.

The first attempt's Python extractor worked well. It is the reference implementation for the Knowledge Base: the jar is a port of it, described in `docs/port-guide/`, and it covers the repository and the Web Catalog from the start.

The stages are set out in `../../migration-plan.md`. Requirements are numbered FR-1 to FR-21 and keep their numbers. Lines marked `[ASSUMPTION]` are John's inference and are listed again in section 9.

## 1. Vision

Given an FSD for one dashboard tab, Kamal ends up with a to-be design he can build from, and every statement in it can be traced back to what OBIEE holds today.

Two parts do the work. A jar reads the OBIEE export into a Knowledge Base, with no AI involved. Skills running in GitHub Copilot or Claude read the Knowledge Base and the FSD and write the documents.

Today's semantic model is a result in its own right. As soon as the Knowledge Base exists, the Operator can have any subject area described in plain language, with no FSD.

Phase 1 is finished when the whole chain works once, end to end, for one FSD.

## 2. User

One user: **the Operator**, today Kamal. The Operator knows OBIEE and Power BI, runs the jar, runs the skills, and confirms what the skills propose.

**UJ-1. The Operator takes one FSD to a to-be design.**

1. The Operator runs the jar on the OBIEE export. It writes the Knowledge Base and two lists: errors, and content it did not read.
2. The Operator runs the jar to cut a Slice for a subject area, then runs the as-is skill. It writes the As-is document, today's semantic model for that subject area, with open points where OBIEE gives no answer. This needs no FSD and is done once per subject area.
3. The Operator runs the jar on the FSD. It writes the FSD as plain text.
4. The Operator runs the trace skill. It writes a Match table: each item the FSD asks for, the OBIEE column it proposes, and a confidence score.
5. The Operator runs the match check. It reports which Matches fail and why.
6. The Operator corrects and confirms the Match table.
7. The Operator runs the jar to cut a Slice for the confirmed Matches. The jar says which subject areas the Tab touches and whether each has an As-is document.
8. The Operator runs the to-be skill. It writes the To-be design from the Slice and those As-is documents.

**Edge case:** an FSD item has no plausible OBIEE column. The trace skill lists it as unmatched; it does not pick the nearest name.

## 3. Glossary

- **Estate** — one OBIEE export: a repository, a Web Catalog, or both, under one name the Operator chooses.
- **Knowledge Base** — the folder of CSV files the jar writes from an Estate.
- **FSD** — the functional specification document, a `.docx`, describing one new report. One FSD is one Tab.
- **Tab** — a single tab in a dashboard; the unit of work.
- **Match** — a proposed link between one FSD item and one OBIEE presentation column, with a confidence score and a status (proposed, confirmed, rejected, unmatched).
- **Match table** — the CSV file holding all Matches for one FSD.
- **Slice** — a part of the Knowledge Base: either one subject area, or what one Tab needs.
- **As-is document** — the description of today's semantic model for one subject area. Written once per subject area and reused by every Tab that touches it.
- **To-be design** — the document setting out the new tables and views, the Power BI semantic model, and the Tab's fields.
- **Skill** — a markdown instruction file that GitHub Copilot or Claude follows.
- **Row identifier** — the stable name of one Knowledge Base row, prefixed with the Estate name.

## 4. Features

### 4.1 Extract

The jar reads an Estate and writes the Knowledge Base. Realizes UJ-1 step 1.

**FR-1: Read the repository.** The jar reads a repository exported as MDS XML and writes tables, columns, formulas, joins, hierarchies, variables and security rules.
- Physical, logical and presentation layers each have their own tables.
- An Estate with no Web Catalog still extracts.

**FR-2: Read the Web Catalog.** The jar reads a Web Catalog folder and writes dashboards, dashboard pages and what is placed on them, reports (OBIEE calls them analyses), report columns, views, filters, prompts and saved filters.
- An Estate with no repository still extracts.
- A file that is not a catalog object is listed as such, not as an error.

**FR-3: Keep formulas exactly.** Every formula and expression is written character for character as the source holds it.
- A test compares extracted text with source text for at least one formula with leading spaces and one starting with `-` or `=`.

**FR-4: Record lineage.** For each presentation column, the Knowledge Base holds the chain to its logical column, logical table source, and physical table and column.
- A presentation column that reaches no physical column still has a row, with the reason.

**FR-5: Link report columns to the repository.** For each report column, the Knowledge Base records the presentation column it uses, or why none was found.
- The link is made only inside one Estate. The jar never pairs a repository and a catalog by itself.

**FR-6: List what was not read.** Each run writes an errors list and a list of content present in the source that no table carries.
- The jar exits with a non-zero code when the errors list is not empty.
- Nothing is dropped silently: an unknown object type appears in the second list with a count.

**FR-21: The Knowledge Base describes itself.** Each Knowledge Base and Slice contains a schema: one file listing every table with what it holds, and one listing every column with its meaning and the table and column it refers to.
- Every table and every column written appears in the schema with a description.
- Every reference the schema declares resolves on the test fixtures.
- The schema is produced by the jar from the same definitions it writes the tables from.

**FR-7: Stable row identifiers.** Every row has a Row identifier that is the same on every run of the same Estate.
- Two runs on the same input produce identical files.
- A Row identifier that would name two rows is reported as an error.

### 4.2 Slice

**FR-8: Cut a Slice.** Given one or more subject area names, or a confirmed Match table, the jar writes the Knowledge Base rows needed. Realizes UJ-1 steps 2 and 7.
- The Slice contains every row reachable through lineage from the chosen columns, including joins, variables, hierarchies and security rules that apply.
- A subject-area Slice also contains the reports built on that subject area.
- The Slice lists the errors and unread content that fall inside it.

### 4.3 Read the FSD

**FR-9: Turn the FSD into text.** The jar reads a `.docx` FSD and writes its text, keeping headings, lists and tables. Realizes UJ-1 step 3.
- Tables come out with rows and cells intact.
- An old-style `.doc` is refused with a message saying to save it as `.docx`.

`[ASSUMPTION]` The jar always does this conversion, so no Skill depends on the AI client opening Word files.

### 4.4 Trace the FSD

**FR-10: Propose Matches.** The trace Skill reads the FSD text and the Knowledge Base and writes the Match table. Realizes UJ-1 step 4.
- Every FSD item that needs data has a row: matched, or unmatched.
- Each Match carries a confidence score and one line saying what the score rests on (exact name, similar name, same formula in an existing report, and so on).
- Each Match cites the Row identifier it points to.

**FR-11: Check Matches automatically.** A check reads the Match table and reports failures. Realizes UJ-1 step 5.
- It fails a Match whose Row identifier does not exist.
- It fails a Match whose lineage does not reach a database table.
- It fails the table if an FSD item has no row.
- Where the catalog holds an existing report for the Tab, it flags Matches that disagree with that report's columns.
- It does not compare data values.

`[ASSUMPTION]` The check is a jar command, because every check above is decidable and needs no AI.

**FR-12: The Operator confirms Matches.** The Operator sets each Match to confirmed or rejected. Realizes UJ-1 step 6.
- Later steps use confirmed Matches only.
- A low score never blocks and a high score never confirms; only the Operator does.

### 4.5 Describe today's model

**FR-13: Write the As-is document.** The as-is Skill reads the Slice for a subject area and writes the As-is document for it. It does not need an FSD. Realizes UJ-1 step 2.
- It covers measures and how each is summarised, dimensions and hierarchies, how tables join, where the data comes from, and what differs per user.
- Every factual statement cites a Row identifier.
- It is complete: every presentation table and column, measure, logical table and source, join, hierarchy, variable and security rule in the Slice is covered, or listed as not described with the reason.
- Wherever the Slice does not answer something, or the AI is unsure or wants more information, the document has a numbered open question with an empty answer line.
- The Operator answers open questions. Answers are kept in a file of their own, are applied on every later run, and are never changed by the Skill. A statement that rests on an answer says so.

### 4.6 To-be design

**FR-14: Design the new tables and views.** The to-be Skill lists each new table and view the Tab needs, with columns and types, and the old column each new column comes from. Realizes UJ-1 step 8.
- The old-to-new column list is a proposal until a person signs it.
- What the Knowledge Base cannot supply (keys, decimal places, whether a column may be empty) is marked as missing, not guessed.

`[ASSUMPTION]` The design is platform-neutral until the target platform is chosen (open question 2).

**FR-15: Design the Power BI semantic model.** The to-be Skill lists the tables, relationships, measures and hierarchies the Tab needs, each tied to the As-is document.
- Each measure shows the OBIEE formula it replaces, exactly as extracted.
- An OBIEE feature with no direct Power BI equivalent is flagged for a decision, not silently translated.

**FR-16: List the Tab's fields.** The To-be design lists each field the FSD puts on the Tab, the semantic model item that supplies it, and the confirmed Match behind it.
- Every FSD item appears. One without a confirmed Match is listed as unresolved.

### 4.7 Skills

**FR-17: Skills run in both clients.** The three Skills (as-is, trace, to-be) work in GitHub Copilot and in Claude, and are started through one agent, in the way the BMad developer agent is started.
- Each Skill is plain markdown and needs only the ability to read files, write files and run a local command.
- Each Skill states what input files it needs and stops with a clear message if one is missing.
- The agent shows a menu and runs the chosen Skill; it may run the jar's commands for the Operator.
- The trace Skill and the to-be Skill write open questions and keep answers in the same way as the as-is Skill.
- Each Skill is its own workflow: one per stage of the migration plan that a Skill carries out, run on its own.

### 4.8 Check a document

**FR-18: Check that a document is complete.** A jar command reads a Skill-written document and the Slice it was written from.
- It reports every required row the document neither cites nor lists as not described.
- It reports every citation that names a row or an answer that does not exist.
- It does not judge whether a statement is true.

### 4.9 Review before hand-over

**FR-19: Review every AI-written output.** A review Skill goes through each output of the as-is, trace and to-be Skills again, against the inputs it was written from, before the output is given to users.
- Each output is marked as a draft until its review has finished and the document check passes.
- Findings are logged. Clear errors are corrected; what cannot be settled becomes an open question.
- The agent does not present a draft as ready.

### 4.10 Confirm the port

**FR-20: The Knowledge Base matches the first attempt's.** Run on the same sample input, the jar's Knowledge Base is compared table by table with the one the first attempt produced.
- Every difference is on the list of deliberate differences, or is fixed.
- The Operator confirms the comparison report before the Knowledge Base work is called done.

## 5. Requirements that apply everywhere

- **NFR-1.** The jar runs on Java 11 and uses nothing outside the Java standard library.
- **NFR-2.** The jar is one file, copied to the target system. No installer, no network access.
- **NFR-3.** No AI takes part in anything the jar does.
- **NFR-4.** Any step can be run again without manual clean-up, and gives the same result on the same input (wording of Skill output excepted).
- **NFR-5.** Documents are markdown and tables are CSV. `[ASSUMPTION]`
- **NFR-6.** The jar needs no database access and no running OBIEE server.
- **NFR-7.** In production the Skills run in GitHub Copilot with GPT-5.6 Sol, a short-context model. Every Skill works one small unit at a time, can stop and resume, and never needs a whole Slice or a whole document in context. The jar prepares the small inputs.

## 6. Scope

**In phase 1:** everything in section 4, proven on one FSD.

**Out of phase 1:**

- Generating table scripts, views or Power BI files from the To-be design.
- Comparing migrated numbers with legacy numbers, and the sign-off report.
- Challenging a single statement and having it re-derived on its own. (Answers to open questions are in scope and are kept through regeneration.)
- Catalog content the first attempt's extractor did not read: prompts saved as separate view objects, Visual Analyzer projects, scorecards, KPIs, actions and agents. They are counted, not modelled.
- Accuracy reports and benchmark tiers. Ordinary tests stand in for them.
- More than one Operator, and any user interface.

## 7. How we know it works

- **SM-0.** One subject area has an As-is document produced from the Knowledge Base alone. This is the first milestone. Validates FR-1 to FR-8 and FR-13.
- **SM-1.** One FSD has been taken from `.docx` to a To-be design using only the jar and the three Skills. Validates all FRs.
- **SM-2.** Someone who knows OBIEE has read one As-is document and one To-be design and found them correct enough to work from. Validates FR-13 to FR-16.
- **SM-3.** A spot check of twenty statements in the As-is document finds each one supported by the row it cites. Validates FR-13.

**Do not optimise:** the number of Matches confirmed without change. A trace Skill that always sounds confident would score well on that and be worse.

## 8. Open questions

1. **Does an FSD name OBIEE columns or only business terms?** This decides how much FR-10 can do by lookup. Needs a sample FSD.
2. **Which platform are the new tables and views for, and whose naming rules apply?** Blocks FR-14 from being specific.
3. **When will a repository and catalog from one real OBIEE instance be available?** Until then FR-5 can only be tested on a hand-made pair, and SM-1 runs on sample data.
4. ~~May the public sample repositories be used?~~ Settled 2026-11-23: licence is not a problem.

None of these blocks the architecture or the start of the jar.

## 9. Assumptions

- §4.3 — the jar always converts the FSD to text.
- §4.4 — the automatic Match check is a jar command.
- §4.6 — the To-be design is platform-neutral for now.
- §5 NFR-5 — documents are markdown, tables are CSV.
- §2 — the Operator runs each step by hand, in the order shown in UJ-1; nothing chains the steps automatically.
