---
title: 'Extract filters, prompts and saved filters'
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

# Story 1.9: Extract filters, prompts and saved filters

<frozen-after-approval reason="human-owned intent — do not modify unless human renegotiates">

## Intent

**Problem:** A report's filters decide which rows it shows. The As-is document cannot say what a report restricts unless the Knowledge Base holds them.

**Approach:** Port the filter, prompt and saved-filter readers, keeping the structure of nested conditions and the full content of saved filters.

## Boundaries & Constraints

**Always:**

- Port the Python reference as it is. Change behaviour only where `docs/port-guide/00-read-me-first.md` lists a deliberate difference; a new difference is added to that list in this story.
- Java 11, `java.base` and `java.xml` only. All XML through the recording layer (AD-3). All CSV through the one writer (AD-8). Text exactly as the source holds it (AD-5).
- Each filter row records its parent row and depth, so nested and/or groups can be rebuilt (difference 10).
- Every expression of a saved filter is written (difference 9).
- A filter that points to a saved filter records how the path resolved.

**Never:**

- Reading prompts that live in standalone view objects; the reference does not, and they stay counted in `unparsed.csv`.
- Evaluating or simplifying a filter.

## I/O & Edge-Case Matrix

| Scenario | Input / State | Expected Output / Behavior | Error Handling |
|----------|--------------|---------------------------|----------------|
| Report filter | A report with `Year = 2024` | `analysis_filters.csv` row: scope `analysis`, operator, expression text exactly | N/A |
| Dashboard filter | A filter on a dashboard page | Row with scope `dashboard`, owner the page | N/A |
| Nested conditions | `A and (B or C)` | Rows for each node with parent and depth; order kept | N/A |
| Saved filter reference | A filter naming `/shared/.../my filter` | `target_id` and `resolution` set | N/A |
| Prompted filter | Operator `prompted` | Stored as is | N/A |
| Saved filter | A saved filter with three conditions | `saved_filters.csv` row for the object; all conditions written | N/A |
| Prompt in a report | A column prompt inside a report | `analysis_prompts.csv` row with the variable it sets | N/A |
| Global filter view | The reference type that was resolved twice | Resolved once; one error row if unresolved (difference 11) | N/A |

</frozen-after-approval>

## Code Map

- `docs/port-guide/02-catalog-reader.md` -- §3.7 filters, §3.8 prompts, §3.10 saved filter, §4.9 to §4.11 tables, §5.5 where resolution is used, §7.3 filter expressions and nested trees, §7.4 prompts, §11 known gaps
- `reference/test-corpus/constructed-cases/constructed-catalog/constructed/_filters/` -- one saved filter, `recent+periods`
- `reference/knowledge-base/` -- `analysis_filters`, `analysis_prompts` (4 rows), `saved_filters`

## Tasks & Acceptance

**Execution:**

- [x] `src/main/java/obi2pbi/in/catalog/` -- filter, prompt and saved-filter readers
- [x] `src/main/java/obi2pbi/core/` -- filter node with parent and depth; prompt; saved filter
- [x] `src/main/java/obi2pbi/out/KnowledgeBaseWriter.java` -- write the three tables; add `parent_id` and `depth` columns to `analysis_filters.csv`
- [x] `docs/port-guide/00-read-me-first.md` -- record where saved-filter conditions are written (own rows in `analysis_filters.csv` with scope `saved_filter`, or extra columns) once decided
- [x] `README.md` -- state that `analysis_prompts.csv` covers prompts inside reports only
- [x] `src/test/java/obi2pbi/` -- tests for the matrix rows

**Acceptance Criteria:**

- Given the constructed catalog, when `extract` runs, then filter and saved-filter rows equal its ground truth, apart from the added parent and depth columns.
- Given the sample catalog is fetched, when `KbCompare` runs, then differences in these tables are explained by differences 9, 10 and 11.

## Implementation Notes

Built together with story 1.8, by one agent, on 23 November 2026. That story's file has the notes on the readers' shared parts (difference 8, the audit, memory).

**What was built.** Three tables are new: `analysis_filters` (with `parent_id` and `depth`), `analysis_prompts`, `saved_filters`. Saved filters are read and audited; they are no longer listed as `unmodelled_type`.

**Files.**

- `src/main/java/obi2pbi/in/catalog/`: `FilterExpressions` (one row per expression below a filter element, with parent and depth; used for reports and for saved filters), `SavedFilterReader`, `AnalysisReader` (prompts), `DashboardPageReader` (the dashboard-scope rows).
- `src/main/java/obi2pbi/core/`: `AnalysisFilter`, `AnalysisPrompt`, `SavedFilter`; `Estate`; `KbSchema`.
- Tests: `ReportsAndFiltersTest` (7 tests for this story's matrix), `CatalogTest` (the constructed pair), `KbCompareTest` (4). `KbCompare` and `CatalogSource`.
- Docs: `README.md` (the tables; `analysis_prompts` covers prompts inside reports only), `docs/port-guide/00-read-me-first.md` (notes for 8, 9, 10, 11).

**Results on the sample catalog** (`./build.sh compare sampleapp-v607`, unfiltered, exit 0):

| Table | Reference | Generated | Missing | Extra | Explained | Unexplained |
|---|---|---|---|---|---|---|
| `analysis_filters` | 7,135 | 8,495 | 0 | 0 | 440 cells (8); 1,360 extra rows (9); 2 added columns (10) | 0 |
| `analysis_prompts` | 4 | 4 | 0 | 0 | 0 | 0 |
| `saved_filters` | 96 | 96 | 0 | 0 | 3 cells (8) | 0 |

The 8,495 rows are 6,248 of reports, 887 dashboard prompts on pages, and 1,360 of saved filters. Depths: 1,973 rows at 0, 3,178 at 1, 3,114 at 2, 226 at 3, 4 at 4. The rows shared with the reference have the reference's identifiers, positions and every other value; only `expr_text` differs, on 440 rows that enclose other expressions, and there only by white space (below).

**Decisions.**

- *Difference 9, where the conditions go.* As the Design Notes say: ordinary `analysis_filters` rows, `scope` = `saved_filter`, `owner_id` = the saved filter, Row identifier `<saved filter path>#f<k>`. Every `sawx:expr` below the object's direct `saw:filter` elements, in document order. `saved_filters` keeps one row per object.
- *What `saved_filters.op`, `expr_type`, `expr_text` hold.* The outermost expression's operator, type and own text. `op` and `expr_type` equal the reference on all 96 rows; `expr_text` on 93, and on the other 3 it only gains white space.
- *Difference 8 on filter rows: the own text, not the whole text.* First built with the whole text, so that a group or comparison row held the text of all its operands run together (`"Flight Facts"."# of Flights"20`); 2,622 cells differed from the reference. The coordinator asked for it to be narrowed, and it now is: `expr_text` of every `analysis_filters` row and of `saved_filters` is all the character data directly inside the expression element (before, between and after its children) plus the whole text of nested elements that are not `sawx:expr`, and never the text of a nested `sawx:expr`, which has its own row. `FilterExpressions.ownText`, on `XmlNode.ownText`. Column and prompt formulas keep the whole-text rule.
- *What still differs under difference 8 on the sample:* 440 `analysis_filters` cells and 3 `saved_filters` cells, every one white space only. The reference stops at the first child; the jar also keeps the line breaks and tabs between and after the children. Two real ones: `…/line geometries thickness#f0.0` (`sawx:logical`), reference `"\t\t\t\t\n            "`, generated `"\t\t\t\t\n            \n\t\t\t\t\n            "`; `06. dashboard design/actions/simple report example#f0.3`, reference `"\n\t\t\t\t\t"`, generated `"\n\t\t\t\t\t\n\t\t\t\t"`. By type: `sawx:special` 189, `sawx:logical` 96, `sawx:comparison` 82, `sawx:list` 34, `sawx:columnExpression` 32, `sawx:rank` 6, `sawx:subQuery` 1. No filter row in the sample gains anything but white space; the cases that do (text after a nested expression, text of a nested element that is not an expression) are covered by tests.
- *References between saved filters are resolved* (76 on the sample: 57 resolve, 19 do not and get an error row). The reference never reaches them. Leaving `resolution` empty on a row that has a saved-filter reference would read as "not attempted", which story 1.9 forbids ("a filter that points to a saved filter records how the path resolved"). Cost: 19 more `errors` rows and `coverage.skipped` for the expression element 176 to 195, both explained by rule.
- *Parent and depth* are relative to the filter element: the parent is the nearest enclosing `sawx:expr` below the same `saw:filter`, so an element in between that is not an expression is passed over. The two columns are last in the table.
- *Difference 11.* The dashboard-scope row is written in `DashboardPageReader`, in the placement loop, from the outcome just obtained. There is no second resolve anywhere.
- *`KbCompare` rules read the source.* Differences 8, 9 and 10 cannot be checked from the two Knowledge Bases alone, so the comparison reads the estate's report and saved-filter files itself with the JDK's DOM parser (`CatalogSource`), with the numbering rules written out a second time. A rule passes only when the file bears the generated value out; `KbCompareTest` has a wrong text, the whole text on a filter row, a wrong depth, a wrong parent, a wrong operator, a dropped row and a wrong count, none of which is explained.

**Open points.**

- The 443 filter cells that differ from the reference only by trailing white space: kept, because the rule is "nothing after a nested element is lost" and nothing is trimmed. If byte equality with the reference matters more there, the rule would need "own text, unless it is all white space".
- On the two repository estates the comparison now prints "difference 10: 2": the two added columns of an `analysis_filters` table with no rows. True, and noise.

**Not done.**

- Prompts in standalone `saw:view` objects are not read, as the story says; `analysis_prompts` has the reference's 4 rows.
- A saved filter's own `subjectArea` attribute is not read (as in the reference); `unparsed.csv` lists it, 96 times.
- `target_id` of a filter row is not checked to be a saved filter: a reference that resolves to something else is recorded as resolved, as in the reference.

## Spec Change Log

## Review Triage Log

## Design Notes

Saved-filter conditions: the simplest shape is to write them as ordinary `analysis_filters.csv` rows with scope `saved_filter` and the saved filter as owner. That reuses the nested-condition columns and keeps one place to look for any filter. `saved_filters.csv` then holds one row per saved-filter object, as in the reference.

## Verification

**Commands:**

- `./build.sh` -- expected: the jar is built at `build/obi2pbi.jar` and every test passes
