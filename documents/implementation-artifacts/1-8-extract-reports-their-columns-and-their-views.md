---
title: 'Extract reports, their columns and their views'
type: 'feature'
created: '2026-11-23'
status: 'done'
route: ''
review_loop_iteration: 0
context:
  - '{project-root}/docs/port-guide/00-read-me-first.md'
  - '{project-root}/docs/port-guide/02-catalog-reader.md'
  - '{project-root}/documents/planning-artifacts/architecture/architecture-POC-OBIEE-2-PBI-2026-11-23/ARCHITECTURE-SPINE.md'
---

# Story 1.8: Extract reports, their columns and their views

<frozen-after-approval reason="human-owned intent — do not modify unless human renegotiates">

## Intent

**Problem:** Most reports define calculations that exist nowhere in the repository. Without report columns and formulas the Knowledge Base misses that logic.

**Approach:** Port the analysis reader: reports (OBIEE calls them analyses), their criteria blocks, columns with formulas, and the views each report renders as.

## Boundaries & Constraints

**Always:**

- Port the Python reference as it is. Change behaviour only where `docs/port-guide/00-read-me-first.md` lists a deliberate difference; a new difference is added to that list in this story.
- Java 11, `java.base` and `java.xml` only. All XML through the recording layer (AD-3). All CSV through the one writer (AD-8). Text exactly as the source holds it (AD-5).
- The view type is stored in the source's own words (`saw:pivotTableView`), never translated to a Power BI visual.
- The subject area name is stored as the catalog wrote it, quotes included.
- A formula is stored whole, nested parts included (difference 8).

**Never:**

- Choosing a Power BI visual for a view.
- Reading Visual Analyzer project formulas or standalone view objects; they stay counted in `unparsed.csv` (guide 02 §11).

## I/O & Edge-Case Matrix

| Scenario | Input / State | Expected Output / Behavior | Error Handling |
|----------|--------------|---------------------------|----------------|
| Happy path | A report with one criteria block | `analyses.csv`, `analysis_criteria.csv`, `analysis_columns.csv`, `analysis_views.csv` | N/A |
| Plain column | Formula `"Sales"."Revenue"` | Formula stored exactly; `is_computation` false | N/A |
| Calculation | Formula with a function or arithmetic | Formula stored exactly; `is_computation` true | N/A |
| Database function | Formula using `EVALUATE` | `uses_eval` true | N/A |
| Nested formula | `CAST(` followed by a nested expression element | Whole text stored, not just `CAST(` | N/A |
| Several criteria blocks | A report combining queries | One `analysis_criteria.csv` row each; columns belong to the nearest block (guide 02 §3.5) | N/A |
| Chart view | A `dvt` view | `display_type` and `display_subtype` filled | N/A |
| Layout view | A compound view arranging others | `references_views` > 0 | N/A |
| View with no type | No `xsi:type` | `view_type` `(no xsi:type)` | N/A |

</frozen-after-approval>

## Code Map

- `docs/port-guide/02-catalog-reader.md` -- §3.3 analysis, §3.4 criteria, §3.5 ownership, §3.6 columns, §3.9 views, §4.5 to §4.8 tables, §7.1 formula capture and the four flags, §7.5 views, §10 gotchas
- `reference/test-corpus/constructed-cases/constructed-catalog/` -- one report, `revenue+by+month`
- `reference/processed-inputs/samples/catalog/report/` -- a real report file
- `reference/knowledge-base/` -- `analyses` (1,607 rows), `analysis_criteria`, `analysis_columns`, `analysis_views`

## Tasks & Acceptance

**Execution:**

- [x] `docs/port-guide/00-read-me-first.md` -- before coding, confirm from guide 02 §1.6 and §7.1 that the nested part of a formula is not already captured in another column; record the finding against difference 8
- [x] `src/main/java/obi2pbi/in/catalog/` -- analysis, criteria, column and view readers
- [x] `src/main/java/obi2pbi/core/` -- analysis, criteria, column, view
- [x] `src/main/java/obi2pbi/out/KnowledgeBaseWriter.java` -- write the four tables
- [x] `src/test/java/obi2pbi/` -- tests for the matrix rows

**Acceptance Criteria:**

- Given the constructed catalog, when `extract` runs, then the report rows equal its ground truth.
- Given the sample catalog is fetched, when `KbCompare` runs on the four tables, then the only differences are formulas that the reference cut short (difference 8), and their number is recorded in Implementation Notes.

## Implementation Notes

Built together with story 1.9, by one agent, on 23 November 2026. The notes on filters, prompts and saved filters are in that story's file.

**What was built.** `extract` reads reports. Four tables are new: `analyses`, `analysis_criteria`, `analysis_columns`, `analysis_views`. Reports are no longer listed as `unmodelled_type`; they are audited like every other read type, so `coverage`, `coverage_summary`, `unparsed` and `errors` now carry their rows.

**Files.**

- `src/main/java/obi2pbi/in/catalog/`: `AnalysisReader` (criteria, columns, prompts, views; filters through `FilterExpressions`), `Ownership` (nearest enclosing owner), `CatalogXml` (the element names and the fixed words); `CatalogReader` (the two readers registered; one object at a time, see "Memory"); `CatalogObject` (the `leftOut` field is gone, nothing reads it now).
- `src/main/java/obi2pbi/core/`: `Analysis`, `AnalysisCriteria`, `AnalysisColumn`, `AnalysisView`; `Estate` (lists, merge, uniqueness check); `KbSchema` (the tables, with meanings, notes and references).
- `src/main/java/obi2pbi/in/xml/`: `XmlNode.deepText` (see "Difference 8 and the audit"); `ReadAudit.Tally` (the same audit, one tree at a time).
- `src/main/java/obi2pbi/out/KnowledgeBaseWriter.java`: no change needed; it writes whatever `KbSchema.TABLES` lists.
- Tests: `ReportsAndFiltersTest` (4 tests for this story's matrix), `CatalogTest` (the constructed pair), `XmlLayerTest` (1), `KbCompareTest` (4, shared with 1.9), `ExtractCommandTest` (file list). `KbCompare` and the new `CatalogSource` (test tree): rules for differences 8, 9 and 10.
- Docs: `README.md`, `docs/port-guide/00-read-me-first.md`.

**The check asked for before coding (difference 8).** Read: `catalog_xml.py` (`_expression`, `_parse_columns`, `_parse_filters`, `_parse_prompts`, `_parse_saved_filter`), `observing.read_text`, guide 02 §1.6, §3.6, §3.7, §7.1. Finding: the Python stores `element.text`, the text before the first child element, and never the text after a child. For a column formula and a prompt formula the nested part is captured nowhere else; it only shows as unread text in `unparsed.csv`. For a filter, each nested `sawx:expr` is a row of its own, but text after a nested element and the text of nested elements that are not `sawx:expr` is captured nowhere. Recorded against difference 8 in `00-read-me-first.md`, with two rules. Formulas (`analysis_columns.formula`, `analysis_prompts.column_formula`) are stored whole: all character data inside the element in document order, nested elements' text included, tags and attributes not, nothing trimmed. Filter texts follow the narrower own-text rule (story 1.9's notes). The schema note of each column states its rule.

**Results on the sample catalog** (`./build.sh compare sampleapp-v607`, unfiltered, exit 0, "no unexplained differences"):

| Table | Reference | Generated | Missing | Extra | Explained | Unexplained |
|---|---|---|---|---|---|---|
| `analyses` | 1,607 | 1,607 | 0 | 0 | 0 | 0 |
| `analysis_criteria` | 1,789 | 1,789 | 0 | 0 | 0 | 0 |
| `analysis_columns` | 11,143 | 11,143 | 0 | 0 | 15 cells, difference 8 | 0 |
| `analysis_views` | 10,879 | 10,879 | 0 | 0 | 0 | 0 |
| `coverage` | 65 | 65 | 0 | 0 | 2 cells (difference 9: 1, difference 11: 1) | 0 |
| `coverage_summary` | 5 | 5 | 0 | 0 | 0 | 0 |
| `unparsed` | 1,050 | 1,053 | 0 | 0 | 1 cell (8), 3 missing rows (9), 6 extra rows (21) | 0 |
| `errors` | 1,552 | 1,185 | 0 | 0 | 386 missing rows (11), 19 extra rows (9) | 0 |

**How many formulas the reference had cut short: 15**, all in `analysis_columns.formula`, all of type `sawx:binned` (a column that puts values into bins). The reference holds nothing or white space for them; the jar holds the text of the base formula followed by the text of the bin rules, which the reference captured nowhere. No formula in the sample has the `CAST(` shape, text of its own before a nested expression; that shape is covered by tests only. The other 11,128 formulas are byte for byte the reference's. The `unparsed` cell is `...expr#text` for reports, 4,426 to 4,269: the 157 nested texts inside those 15 formulas, counted again from the files by the comparison.

**Other checks.** The Python reference was run on the constructed pair and on every catalog fixture of `ReportsAndFiltersTest` and `KbCompareTest` (output under `build/`), and the jar's output compared with it: nothing unexplained in any of them. `./build.sh compare sampleapp` and `bi-analytics-2013`: no unexplained differences, same explained counts as before (plus "difference 10: 2" for the two columns of the empty `analysis_filters`).

**Decisions.**

- *Difference 8 and the audit.* Taking an element's whole text (or, for a filter row, its own text) marks the text of the nested elements as read, so it is no longer listed as `unread_text`. It does not mark the nested elements as looked at: one that no reader reads in its own right is still an `unread_element`, and its attributes `unread_attribute`. The alternative (text taken counts as the element being read) would have removed `unread_element` rows for structure the jar does not understand. `XmlNode.deepText` was changed for this (it had no caller before), and `XmlNode.ownText(name)` added for the filter rule.
- *The audit follows the Python read for read*, including that the check for a database function stops at the first one found. Otherwise the `unparsed` counts drift from the reference with nothing wrong in the data tables (guide 02 §9.3).
- *Memory.* With reports read, keeping every tree until one audit at the end needed a 256 MB heap on the sample (96 MB before the story). `CatalogReader` now takes one object at a time: parse, read, audit, let go. It runs in 96 MB again and writes the same bytes (checked with `diff -r` against the output of the earlier form). This changes the order in which readers run, from "type by type" to "file by file"; nothing depends on it, because the path index is built from file names beforehand, pages get their dashboards afterwards, and every table is sorted when written.
- *A binned column's formula* is the run-together text of its base formula and rule values. That is what "whole text" gives; the structure of the rules (which value belongs to which bin) is not captured, and their elements are still listed in `unparsed.csv`.
- *`formula_construct`* keeps the reference's value, the full element name.
- *The test that asserted the interim state* ("until stories 1.8 and 1.9, the report is listed as a type with no reader", in `CatalogTest`) was replaced by assertions on the rows the constructed pair must now give. Not weakened: it checks more.

**Where the Python and the guide disagree.** Nothing material. Guide 02 §11 says the two sources differ on whether Visual Analyzer formulas show as `unread_text` or `unread_element`; the jar gives what the committed reference output has.

**Not done.**

- `report_lineage` (story 1.10).
- The column element's own `xsi:type` is still used for `is_computation` and not stored, as in the reference.
- Visual Analyzer project formulas, standalone views and the other known gaps of guide 02 §11 stay unread and counted, as the story says.

## Spec Change Log

## Review Triage Log

## Verification

**Commands:**

- `./build.sh` -- expected: the jar is built at `build/obi2pbi.jar` and every test passes
