---
title: 'List what was not read, and what went wrong'
type: 'feature'
created: '2026-11-23'
status: 'done'
route: ''
review_loop_iteration: 0
context:
  - '{project-root}/docs/port-guide/00-read-me-first.md'
  - '{project-root}/docs/port-guide/03-core-output-slice.md'
  - '{project-root}/docs/port-guide/01-repository-reader.md'
  - '{project-root}/documents/planning-artifacts/architecture/architecture-POC-OBIEE-2-PBI-2026-11-23/ARCHITECTURE-SPINE.md'
---

# Story 1.2: List what was not read, and what went wrong

<frozen-after-approval reason="human-owned intent — do not modify unless human renegotiates">

## Intent

**Problem:** A Knowledge Base that silently drops content is worse than none. The Operator needs every run to say what it skipped and what failed.

**Approach:** Port the read audit and the error reporting: write `unparsed.csv`, `errors.csv` and `coverage.csv` from what the recording layer observed, and exit 3 when anything went wrong.

## Boundaries & Constraints

**Always:**

- Port the Python reference as it is. Change behaviour only where `docs/port-guide/00-read-me-first.md` lists a deliberate difference; a new difference is added to that list in this story.
- Java 11 language level, `java.base` and `java.xml` only (AD-15). No build tool, no test library.
- All XML is read through the recording layer, so unread content is found by observation (AD-3).
- All CSV goes through the one CSV writer (AD-8). Text is written exactly as the source holds it (AD-5).
- `unparsed.csv` (known gaps) and `errors.csv` (things that went wrong) are separate files and are never merged.
- A bad source file or object produces an error row and the run continues (deliberate differences 3 and 4).

**Never:**

- A hand-kept list of which attributes the readers read.
- Treating namespace declarations as unread attributes.
- Stopping the run for anything other than a wrong command line.

## I/O & Edge-Case Matrix

| Scenario | Input / State | Expected Output / Behavior | Error Handling |
|----------|--------------|---------------------------|----------------|
| Unread attribute | An object has an attribute no reader touched | `unparsed.csv` row: kind `unread_attribute`, object type, detail, occurrences | N/A |
| Unread element / text | Same for a child element or text | Rows of kind `unread_element` / `unread_text` | N/A |
| Unmodelled type | An object type with no reader | Row of kind `unmodelled_type` with a count; `coverage.csv` counts it as skipped | N/A |
| File never discovered | A file under the estate that discovery did not pick up | Row of kind `undiscovered_file`, carrying the estate name (difference 12) | N/A |
| Broken XML | A file that does not parse | `errors.csv` row, kind `malformed_xml`; other files still processed | Exit 3 |
| Unreadable file | A file that cannot be opened | `errors.csv` row, kind `unreadable` | Exit 3 |
| Duplicate identifier | Two objects give the same Row identifier | Both rows written; `errors.csv` row, kind `duplicate_identifier` | Exit 3 |
| Bad name | A name containing `::` or only spaces | `errors.csv` row; run continues (difference 3) | Exit 3 |
| Clean fixture | Nothing wrong | `errors.csv` holds only its header | Exit 0 |

</frozen-after-approval>

## Code Map

- `docs/port-guide/03-core-output-slice.md` -- §1 the read audit (all `kind` values in §1.8, counting in §1.5), §2 `errors.csv`, §4.3 coverage
- `docs/port-guide/01-repository-reader.md` -- §8 errors, gaps and duplicates; §8.4 conditions that stopped the old run
- `reference/knowledge-base/unparsed.csv, errors.csv, coverage.csv` -- expected shape and values on the large samples

## Tasks & Acceptance

**Execution:**

- [x] `src/main/java/obi2pbi/in/mds/` -- audit pass after each file: report everything present and not read -- guide 03 §1.4
- [x] `src/main/java/obi2pbi/core/` -- error and gap records with their `kind` values; identifier uniqueness check
- [x] `src/main/java/obi2pbi/out/KnowledgeBaseWriter.java` -- write `unparsed.csv`, `errors.csv`, `coverage.csv`
- [x] `src/main/java/obi2pbi/cli/` -- exit 3 when `errors.csv` is not empty; print the counts
- [x] `src/test/java/obi2pbi/` -- tests for each matrix row, using small XML files written inside the test

**Acceptance Criteria:**

- Given a reader is changed to stop reading an attribute, when the tests run, then the attribute appears in `unparsed.csv` with no other code change.
- Given `src/test/fixtures/testrpd`, when `extract` runs, then `errors.csv` is empty and the exit code is 0.
- Given the sample repositories are fetched, when `KbCompare` runs on `unparsed.csv` and `coverage.csv` for the physical object types, then differences are explained by the deliberate-differences list.

## Implementation Notes

Built together with story 1.1; the file list is in that story.

**Built.** After the readers have run, `in/xml/ReadAudit` walks every tree of a handled object type and reports each attribute, element and text that was present and never read. `extract` writes `unparsed.csv`, `errors.csv` and `coverage.csv`, prints the counts, and exits 3 when `errors.csv` has rows.

**How each matrix row is met.**

| Scenario | Where | Test (`GapsAndErrorsTest` unless stated) |
|---|---|---|
| Unread attribute, element, text | `ReadAudit` | `testUnreadAttributeElementAndTextAreListed`; `XmlLayerTest` covers the rules of guide 03 §8.5 one by one |
| Unmodelled type | `RepositoryReader.load`, `Estate.recordUnmodelled` | `testATypeWithNoReaderIsCountedAsSkipped` |
| File never discovered | `RepositoryFiles.findUndiscovered`; carries the estate name (difference 12) | `testAFileDiscoveryDidNotPickUpIsListedUnderTheEstateName` |
| Broken XML | `RepositoryReader.parseAll` | `testBrokenXmlIsAnErrorAndTheOtherFilesAreStillRead` |
| Unreadable file | the same; kind `unreadable` (difference 4) | `testAFileThatCannotBeOpenedIsAnErrorAndTheRunContinues` |
| Duplicate identifier | `Estate.checkIdentifierUniqueness` | `testTwoObjectsWithOneIdentifierAreBothWrittenAndReported` |
| Bad name | `ReadContext.makeId`; kind `invalid_name` (difference 3) | `testANameThatCannotBeAnIdentifierIsAnErrorAndTheRunContinues` |
| Clean fixture | | `testAnOrdinaryTreeHasNoUndiscoveredFiles` |

The first acceptance criterion (a reader stops reading an attribute and it appears with no other change) is `testAnAttributeAReaderStopsReadingAppearsWithNoOtherChange`: two readers that differ only in one `attr` call.

**Decisions.**

1. The kind is carried in the audit's counter key, as guide 03 §1.5 recommends, not derived from the detail text.
2. A bad name gives kind `invalid_name` (a new kind; the confirmed difference 3 did not name one). `source_id` is the object's own source id, as for `missing_name`.
3. An unreadable file gives `source_id` = file name and `object_type` = its type folder, the same shape as `malformed_xml`, so coverage counts it as skipped under its type.
4. The file is read into memory before it is parsed, so "cannot be opened" (`unreadable`) and "does not parse" (`malformed_xml`) cannot be confused.
5. "Only spaces" uses Python's set of white-space characters, which is wider than Java's (guide 01 G23).
6. The `duplicate_identifier` reason text is the Python's, word for word, including "Group Spec", a term this project does not use. Kept so `errors.csv` compares with the reference.
7. A folder that the undiscovered-file walk cannot list is reported (difference 18, not yet confirmed).

**Ported as it is, though odd.** An index-only file (`Schema`, for example) that is broken XML is counted both skipped and parsed (guide 01 G17).

**Open points.**

- The story says a file "under the estate" that discovery missed is reported. The Python looks only under the repository root (`<estate>/rpd`), and so does the jar. A stray file directly in the estate folder, beside `rpd/` and `catalog/`, is not reported.
- The `malformed_xml` reason is the Java parser's message, which spans two lines and differs from Python's wording. The guide expected this.

**Checked against the reference.** With `--where object_type=PhysicalTable,PhysicalColumn,PhysicalForeignKey,Schema,Database,ConnectionPool,BusinessModel,Project,QueryPrivilege,LogicalSourceFolder,User,File`: `unparsed` sampleapp 38 rows and bi-analytics-2013 117 rows, `coverage` 10 and 9 rows; no missing, extra or differing. Without the filter the tables differ only for object types whose readers are later stories: those types appear as `unmodelled_type` (one row per file) and `skipped`, where the reference has their `unread_*` rows and `parsed` counts. `errors`: the reference has 2 rows per repository, all `unresolved_reference` from logical-layer formulas; the jar has none yet.

## Spec Change Log

## Review Triage Log

## Design Notes

At this point only the physical readers exist, so most object types will show as `unmodelled_type`. That is correct, and the count falls as later stories add readers.

## Verification

**Commands:**

- `./build.sh` -- expected: the jar is built at `build/obi2pbi.jar` and every test passes
