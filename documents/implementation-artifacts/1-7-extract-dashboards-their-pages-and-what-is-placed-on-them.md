---
title: 'Extract dashboards, their pages and what is placed on them'
type: 'feature'
created: '2026-11-23'
status: 'done'
route: ''
review_loop_iteration: 0
context:
  - '{project-root}/docs/port-guide/00-read-me-first.md'
  - '{project-root}/docs/port-guide/02-catalog-reader.md'
  - '{project-root}/docs/port-guide/03-core-output-slice.md'
  - '{project-root}/documents/planning-artifacts/architecture/architecture-POC-OBIEE-2-PBI-2026-11-23/ARCHITECTURE-SPINE.md'
---

# Story 1.7: Extract dashboards, their pages and what is placed on them

<frozen-after-approval reason="human-owned intent — do not modify unless human renegotiates">

## Intent

**Problem:** The Knowledge Base holds nothing about reports yet. The Operator needs to see which dashboards and pages exist and what each page holds.

**Approach:** Port the catalog side of the estate layout, catalog file discovery and classification, path resolution, and the dashboard and dashboard-page readers.

## Boundaries & Constraints

**Always:**

- Port the Python reference as it is. Change behaviour only where `docs/port-guide/00-read-me-first.md` lists a deliberate difference; a new difference is added to that list in this story.
- Java 11, `java.base` and `java.xml` only. All XML through the recording layer (AD-3). All CSV through the one writer (AD-8). Text exactly as the source holds it (AD-5).
- An estate may have a repository, a catalog, or both; `manifest.csv` records which halves were present (AD-7).
- Catalog identifiers use layer `cat`.
- A path reference records how it resolved: exact, after folding case, or unresolved (guide 02 §5).

**Never:**

- Pairing a catalog with a repository from a different estate folder.
- Reading reports, filters or prompts (stories 1.8, 1.9).
- Treating a non-object file (image, template) as an error.

## I/O & Edge-Case Matrix

| Scenario | Input / State | Expected Output / Behavior | Error Handling |
|----------|--------------|---------------------------|----------------|
| Happy path | `estates/<estate>/catalog/` with dashboards | `dashboards.csv`, `dashboard_pages.csv`, `dashboard_contents.csv`; pages and placements in order | N/A |
| Encoded names | File names with `+` and `%2e` | Decoded as guide 02 §1.3 describes; a name that cannot be decoded is kept and reported, not thrown | N/A |
| Not a catalog object | A PNG or RTF in the tree | `unparsed.csv` row, kind `not_a_catalog_object` | N/A |
| Unmodelled object type | A scorecard, KPI, action | `unparsed.csv` row, kind `unmodelled_type`, with a count | N/A |
| Placement resolves after folding case | Reference `/Shared/Sales/X`, file stored lower-case | `resolution` `case_normalised`, `target_id` set | N/A |
| Placement does not resolve | Reference into a folder not in the export | `resolution` `unresolved`; `errors.csv` row | Exit 3 |
| Catalog only / repository only | One half absent | Extracts what is there; manifest says which | N/A |
| Styling attributes | Formatting content out of scope | `unparsed.csv` row, kind `excluded_by_scope` | N/A |

</frozen-after-approval>

## Code Map

- `docs/port-guide/02-catalog-reader.md` -- §0 pipeline, §1 catalog folder (discovery, name decoding, classification, object type, what "text" means), §2 estate layout, §3.1 dashboard, §3.2 dashboard page, §4.2 to §4.4 tables, §5 path resolution, §8 coverage tables, §9 errors and gaps
- `reference/test-corpus/constructed-cases/constructed-catalog/` -- hand-made catalog with one dashboard, one page, one report, one saved filter
- `reference/processed-inputs/samples/catalog/` -- one real file per catalog object type
- `reference/knowledge-base/` -- `dashboards`, `dashboard_pages`, `dashboard_contents`, `catalog_versions`, `coverage`, `coverage_summary`
- `docs/formats/catalog-xml-schema.md, web-catalog-findings.md` -- format notes, including where to get the sample catalog

## Tasks & Acceptance

**Execution:**

- [x] `src/main/java/obi2pbi/cli/` -- estate layout: find `rpd/` and `catalog/`, messages for wrong paths -- guide 02 §2
- [x] `src/main/java/obi2pbi/in/catalog/` -- discovery, name decoding, classification, path index and resolver, dashboard and page readers
- [x] `src/main/java/obi2pbi/core/` -- dashboard, page, placement; resolution outcome
- [x] `src/main/java/obi2pbi/out/KnowledgeBaseWriter.java` -- write the three tables plus `catalog_versions.csv` and `coverage_summary.csv`; extend `coverage.csv` with the catalog layer
- [x] `src/test/java/obi2pbi/` -- tests for the matrix rows on the constructed catalog and on small files written in the test

**Acceptance Criteria:**

- Given the constructed catalog, when `extract` runs, then the dashboard, page and placement rows equal its `GROUND-TRUTH.md`.
- Given a catalog file order that differs between file systems, when `extract` runs twice, then the output is identical (file order follows guide 02 §1.2).
- Given the sample catalog is fetched, when `KbCompare` runs on these tables, then differences are explained by the deliberate-differences list (difference 11 changes error and skipped counts).

## Implementation Notes

**What was built.** `extract` now reads `catalog/` as well as `rpd/`, into one Knowledge Base. Five tables are new: `dashboards`, `dashboard_pages`, `dashboard_contents`, `catalog_versions`, `coverage_summary`; `coverage`, `unparsed` and `errors` carry the catalog's rows. The "catalog not read" warning is gone.

**Files.**

- `src/main/java/obi2pbi/in/catalog/` (new): `CatalogReader` (the pipeline and the list of readers), `CatalogFiles` and `CatalogFile` (discovery), `CatalogNames` (name decoding), `ByteClass` (what a non-XML file is), `PathIndex` and `PathResolver` (path references), `CatalogObject`, `CatalogContext`, `CatalogObjectReader` (the seam for stories 1.8 and 1.9), `DashboardReader`, `DashboardPageReader`, `CatalogXml` (element names, the styling rule), `PythonText` (quoting as Python's `repr`).
- `src/main/java/obi2pbi/core/`: `Dashboard`, `DashboardPage`, `DashboardContent` (with the page-to-dashboard link pass), `PathResolution` (the outcome); `Estate` (three row lists, catalog versions, the input census, merge, uniqueness check); `KbSchema` (five tables, and the meanings of `unparsed`, `errors`, `coverage` widened for the catalog); `Unparsed.UNDECODED_NAME`.
- `src/main/java/obi2pbi/in/xml/`: `XmlNode.below` (descendants without the element itself); `XmlTreeReader.parse(bytes, keepTree)` and strict decoding of UTF-8 before parsing.
- `src/main/java/obi2pbi/cli/ExtractCommand.java`: calls the catalog reader; warning removed. `EstateLayout` already found both halves and needed no change.
- `src/main/java/obi2pbi/out/KnowledgeBaseWriter.java`: no change needed; it writes whatever `KbSchema.TABLES` lists, and the manifest already recorded the halves.
- Tests: `CatalogTest` (14 tests), four more in `KbCompareTest`, three more in `XmlLayerTest`; `ExtractCommandTest` file list extended by the five tables; `TestSupport` helpers. `KbCompare`: `--where-not`, and rules for differences 11 and 21.
- Docs: `README.md`, `docs/port-guide/00-read-me-first.md` (differences 20 and 21, notes for 1.7), `build.sh` and `build.cmd` usage comments.

**Results on the sample catalog** (`estates/sampleapp-v607`, 5,371 files, 420 MB): about 2 seconds; runs in a 96 MB heap (`-Xmx96m`, 250 MB resident); with the JVM's default heap on this 94 GB machine it takes what it is given (up to 1.3 GB resident). Exit 3 (990 error rows, all unresolved references).

| Table | Reference | Generated | Missing | Extra | Explained | Unexplained |
|---|---|---|---|---|---|---|
| `dashboards` | 111 | 111 | 0 | 0 | 0 | 0 |
| `dashboard_pages` | 1,036 | 1,036 | 0 | 0 | 0 | 0 |
| `dashboard_contents` | 3,718 | 3,718 | 0 | 0 | 0 | 0 |
| `catalog_versions` | 60 | 60 | 0 | 0 | 0 | 0 |
| `errors`, without `expression/v1.1}expr` rows | 1,376 | 990 | 0 | 0 | 386 missing rows, difference 11 | 0 |
| `unparsed`, without `report` and `savedFilterObject` rows | 324 | 330 | 0 | 0 | 6 extra rows, difference 21 | 0 |
| `coverage`, without the four later-story types | 61 | 61 | 0 | 0 | 1 cell, difference 11 (772 to 386) | 0 |
| `coverage_summary`, without `modelled_object`, `unmodelled_object` | 3 | 3 | 0 | 0 | 0 | 0 |

The filtered command is in `README.md` ("Compare with the reference Knowledge Base"). Unfiltered, `./build.sh compare sampleapp-v607` ends "UNEXPLAINED DIFFERENCES found", and every one belongs to a later story:

- `coverage`: `report` is 0 parsed / 1,607 skipped where the reference has 1,607 / 0; `savedFilterObject` 0 / 96 against 96 / 0; the rows `saw:view (element, inside an Analysis)` (10,879) and `expression/v1.1}expr` (176 skipped) are absent. Story 1.8 reads reports and their views; story 1.9 reads saved filters and the filter expressions whose saved-filter references produce the 176 errors.
- `coverage_summary`: `modelled_object` 1,147 against 2,850 and `unmodelled_object` 3,194 against 1,491. The difference is 1,703 = 1,607 reports + 96 saved filters.
- `unparsed`: 726 reference rows whose `object_type` is `report` (713) or `savedFilterObject` (13) are absent, because the audit runs only on types that have a reader; in their place are two `unmodelled_type` rows.
- `errors`: the 176 `expression/v1.1}expr` rows are absent (story 1.9).
- Reference tables not generated yet: `analyses`, `analysis_columns`, `analysis_criteria`, `analysis_filters`, `analysis_prompts`, `analysis_views`, `report_lineage`, `saved_filters`.

**Other checks.** The constructed pair (`constructed/` as `rpd`, `constructed-catalog/` as `catalog`): 1 dashboard, 1 page, 3 placements all resolved, 0 errors, as `GROUND-TRUTH.md` says; the Python program run on the same pair (output under `build/`) gives the same rows in those three tables. `./build.sh compare sampleapp` and `bi-analytics-2013`: no unexplained differences, same explained counts as before the story.

**Decisions.**

- *Reports and saved filters are reported as `unmodelled_type` until they have readers.* The reference treats four root types as modelled. With no reader for two of them, the honest state is "found, not read": one gap row per type, counted as skipped. The alternative, auditing them with no reader, would write several thousand `unread_*` rows that mean nothing. The set of handled types is taken from `CatalogReader.READERS`, so adding a reader is the whole change; all files are already discovered, classified, versioned and indexed.
- *Difference 11* is built into `DashboardPageReader`, not patched afterwards: one resolve per reference. Story 1.9 must take the dashboard-scope filter rows from the placement rows (see the class comment).
- *Difference 3*, catalog side: see the notes in `00-read-me-first.md`. A path with `::` cannot occur on Windows file systems; the test is skipped there.
- *New difference 20* (a folder that cannot be listed is an error row) and *new difference 21* (a file name that cannot be decoded is kept and reported in `unparsed`, kind `undecoded_name`). 21 comes from this story's matrix ("kept and reported, not thrown"); the reference keeps such a name and says nothing. The decoded text is byte for byte what Python's `unquote_plus` gives.
- *`.git` and `.gitignore`* as the reference: anything named `.git` is left out at any depth, a `.gitignore` is read and classified. Links to folders are followed.
- *Memory.* A tree is kept only for a type that has a reader; any other file is parsed to the end and only its root kept. Needed because the sample has XML data files up to 16 MB.
- *Strict UTF-8 decoding before parsing* (`XmlTreeReader`). The JDK's parser prints "[Fatal Error]" to the console for bytes that are not UTF-8 (681 files on the sample) and the public API cannot stop it. This also touches the repository reader, which uses the same class; both repository comparisons are unchanged.
- *`dashboard_contents.container_id` and `target_id`* are declared without `references_table`, with a note, because each points at more than one table (and `target_id` at things with no table). `dashboard_pages.dashboard_id` is declared as a reference to `dashboards.row_id`.
- *Error reasons keep the reference's wording*, "(AD-3)" included, and quote the reference as Python's `repr` does, so the rows compare equal. The cost is a rule number from the first attempt in the text.
- *Every table is written for every estate* (the reference does the same): a repository-only Knowledge Base now has five more files with a header and no rows. `ExtractCommandTest`'s expected file list was extended for that reason; no test was weakened.
- *Core field name.* The architecture test forbids the text `.xml` in core code, which a field called `xmlVersion` trips when it is read as `d.xmlVersion`. The fields are `formatVersion`; the column is still `xml_version`.

**Where the Python and the guide disagree.** Nothing material was found for this story. Guide 02 section 1.2 leaves symbolic links undecided (followed here) and section 9.1 leaves the `repr` quoting undecided (reproduced here).

**Surprises.**

- The sample has six files under names that are not valid escapes: five under `sin t%edtulo%2exdo` (Latin-1 `í`) and one with `%t2`. The reference Knowledge Base carries the first as `sin t\uFFFDtulo`.
- The JDK's parser and expat agree on which of the 5,371 files are XML. This was measured on this sample only.

**Not done.**

- No reader for reports, saved filters, filters or prompts (stories 1.8 and 1.9), as the story says.
- `PythonText.repr` decides "printable" from the running JDK's Unicode tables, Python from its own; a character assigned in one and not the other would be quoted differently. Not seen on the sample.
- The largest file is read into memory whole (bytes, then text) before parsing. Fine at 16 MB; a catalog with files of several hundred MB would need a streaming path.
- `build.cmd compare` still passes only eight arguments, so the long filtered comparison cannot be typed on Windows as one command. It was like this before the story.

## Spec Change Log

## Review Triage Log

## Design Notes

Guide 02 §1.6: the reference's idea of an element's text is the text before its first child element. Java's DOM `getTextContent()` returns something else. Use the project's own tree from story 1.1 and expose both "text before first child" and "all text", so difference 8 can be applied deliberately in story 1.8.

## Verification

**Commands:**

- `./build.sh` -- expected: the jar is built at `build/obi2pbi.jar` and every test passes
