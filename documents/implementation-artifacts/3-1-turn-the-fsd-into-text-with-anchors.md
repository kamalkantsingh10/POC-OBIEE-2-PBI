---
title: 'Turn the FSD into text with anchors'
type: 'feature'
created: '2026-11-23'
status: 'done'
route: ''
review_loop_iteration: 0
context:
  - '{project-root}/documents/planning-artifacts/architecture/architecture-POC-OBIEE-2-PBI-2026-11-23/ARCHITECTURE-SPINE.md'
  - '{project-root}/documents/planning-artifacts/prds/prd-POC-OBIEE-2-PBI-2026-11-23/prd.md'
---

# Story 3.1: Turn the FSD into text with anchors

<frozen-after-approval reason="human-owned intent — do not modify unless human renegotiates">

## Intent

**Problem:** An FSD arrives as a Word file. A skill cannot be relied on to open one, and it needs a way to point at exact places in the document.

**Approach:** Add the `fsd-text` command: read a `.docx` with the JDK's zip and XML support and write `fsd.md`, with an anchor on every paragraph and table cell.

## Boundaries & Constraints

**Always:**

- Java 11, standard library only: `java.util.zip` and `java.xml`. The `.docx` reader is its own inbound adapter (AD-6).
- Anchors are `[p12]` for paragraphs and `[t3.r2.c1]` for table cells, numbered in document order; the same input always gives the same anchors (AD-10).
- Text is kept as written. Headings, lists and tables keep their structure.

**Never:**

- Reading old-style `.doc` files.
- Interpreting the FSD's content; that is the trace skill's job.
- Images, comments, tracked changes, headers and footers.

## I/O & Edge-Case Matrix

| Scenario | Input / State | Expected Output / Behavior | Error Handling |
|----------|--------------|---------------------------|----------------|
| Happy path | `work/<tab>/fsd.docx` | `work/<tab>/fsd.md` with headings, paragraphs, lists and tables in order, each anchored | N/A |
| Heading styles | Paragraphs styled Heading 1 to 3 | Written as markdown headings | N/A |
| Table | A table of fields | A markdown table; each cell anchored; rows and cells aligned | N/A |
| Merged or empty cells | A table with merged cells | Alignment kept; an empty cell still has an anchor | N/A |
| Text split across runs | One word stored as several runs | Joined into one text | N/A |
| Old format | A `.doc` file | Message: save it as `.docx` | Exit 2 |
| Not a Word file | A zip with no `word/document.xml`, or not a zip | Message naming the problem | Exit 2 |

</frozen-after-approval>

## Code Map

- `documents/planning-artifacts/architecture/architecture-POC-OBIEE-2-PBI-2026-11-23/ARCHITECTURE-SPINE.md` -- AD-10 anchors, AD-14 command behaviour
- `src/main/java/obi2pbi/in/mds/` -- the XML tree from story 1.1 can be reused for `word/document.xml`

## Tasks & Acceptance

**Execution:**

- [x] `src/main/java/obi2pbi/in/docx/` -- open the zip, read `word/document.xml` and `word/styles.xml`, produce a simple document model
- [x] `src/main/java/obi2pbi/core/` -- document model: heading, paragraph, list item, table, cell
- [x] `src/main/java/obi2pbi/out/FsdMarkdownWriter.java` -- write `fsd.md` with anchors
- [x] `src/main/java/obi2pbi/cli/` -- the `fsd-text` command
- [x] `src/test/fixtures/fsd/` -- a small hand-made `.docx` with headings, a list and a table with a merged cell, and the expected `fsd.md`
- [x] `src/test/java/obi2pbi/` -- tests for the matrix rows

**Acceptance Criteria:**

- Given the fixture `.docx`, when `fsd-text` runs, then `fsd.md` equals the expected file.
- Given the same file, when `fsd-text` runs twice, then the output is byte-identical.

## Implementation Notes

**Built.** The `fsd-text` command: `java -jar build/obi2pbi.jar fsd-text work/<tab>/fsd.docx [--out <folder>]` writes `fsd.md` beside the `.docx`, plus `fsd.manifest.csv` and, for a long FSD, `fsd.sections/`. Prints paragraph, table and cell counts.

**Files.**
- `src/main/java/obi2pbi/in/docx/DocxReader.java` (the adapter: `ZipFile`, `word/document.xml`, `word/styles.xml`, `word/numbering.xml`, parsed with the existing `XmlTreeReader`), `DocxStyles.java` (heading level by style, following `basedOn`), `DocxNumbering.java` (list markers from `numFmt` / `lvlText` / `start`), `DocxException.java`.
- `src/main/java/obi2pbi/core/FsdDocument.java`, `FsdBlock.java`, `FsdCell.java`, `FsdRun.java`: the model; `FsdDocument` assigns the anchors and holds the anchor pattern that `check-matches` also uses.
- `src/main/java/obi2pbi/out/FsdMarkdownWriter.java`: `fsd.md`, the sections and their index, the manifest (through `CsvWriter`).
- `src/main/java/obi2pbi/cli/FsdTextCommand.java`, registered in `Main.COMMANDS`.
- `src/test/fixtures/fsd/small/` (the hand-made `.docx` as its XML parts: Title and heading 1-3, a word in three runs, bold, a tracked deletion and insertion, empty paragraphs, a bullet list with a nested level and a list from a style, a numbered list with a lettered level, a tab, a hyperlink, a table with a vertical and a horizontal merge, an empty cell, a `|` and two paragraphs in one cell, a line break) and `small.expected.md`.
- `src/test/java/obi2pbi/DocxFixture.java` (zips XML strings into a `.docx`, fixed entry times so the bytes repeat), `FsdDocxGenerator.java` (simple markdown to a real Word document, JDK only), `FsdTextTest.java` (11 tests: every matrix row, the two acceptance criteria, sections, the sample FSD).
- `work/sample-sales-monthly/fsd.docx` built from `fsd-source.md` by `FsdDocxGenerator` (wording unchanged; headings as Word heading styles, two Word tables, a Word bullet list, bold runs), and `fsd.md` / `fsd.manifest.csv` from `fsd-text`. LibreOffice opens the `.docx` and sees 1 h1, 6 h2, 2 tables and a 2-item list. With the anchors taken out, `fsd.md` equals `fsd-source.md` character for character (a test checks this).

**Decisions.**
- Anchor placement: first on the line, after a heading's `#` or a list marker (`## [p3] 1. Purpose`, `- [p14] ...`, `| [t1.r2.c1] F1 |`), so no FSD text can be read as markdown structure.
- `p` numbers count headings, paragraphs and list items; paragraphs inside a table cell are part of the cell and get no `p` anchor. Empty paragraphs are left out and get no anchor (otherwise blank lines in Word would shift every number after them).
- Cells are numbered by grid column, so merged cells keep alignment: a covered position is a cell `(merged with t1.r2.c1)`; a short row (`gridBefore`, missing cells) is padded with empty anchored cells. The first table row is written as the markdown header row.
- Bold is kept as `**...**` in paragraphs and list items (not in headings or cells), so `fsd.md` reads like the FSD. Line breaks are `<br>`, a tab stays a tab, `|` in a cell is `\|`.
- Inserted text of tracked changes is read as shown; deleted text (`w:delText`), field codes, drawings, text boxes, footnotes, comments, headers and footers are not read. Strict Open XML's namespace is accepted too.
- An old `.doc` is recognised by its OLE signature (whatever its name) or by a `.doc` name on a non-zip file. Not a zip, no `word/document.xml`, or a document part that does not parse: exit 2 with the reason.
- `fsd.manifest.csv` (`key`, `value`): `written_by`, `source_file` (name only, so the output does not depend on how the path was typed), `source_bytes`, `source_sha256`, counts, `sections`, `files`. No time stamp, so reruns are byte-identical.
- Sections: written only when `fsd.md` exceeds 300 lines; each at most 152 lines (150 of text and a header line), cut at headings of level 1-2 or when full; a longer table continues with its header row repeated. `fsd.sections/index.md` lists file, first and last anchor, lines and heading. A sections folder without `index.md` is refused; a stale one is removed when no longer needed.

**Surprises.** None in the format. The JDK's XML reader closing a stream did not arise: entries are read to bytes from `ZipFile` before parsing.

**Not done.** Old `.doc` files (out of scope). Word's automatic numbers on headings are not reproduced (the heading text is kept; Word stores the number outside the text). Equations (`m:oMath`) are not read.

## Spec Change Log

## Review Triage Log

## Design Notes

Open the file with `java.util.zip.ZipFile`, not `ZipInputStream`: the JDK's XML reader closes the stream it is given, which breaks reading a second entry.

The fixture `.docx` is built by a small test helper from XML strings, so the repository holds no binary file that cannot be reviewed.

## Verification

**Commands:**

- `./build.sh` -- expected: the jar is built at `build/obi2pbi.jar` and every test passes
- `java -jar build/obi2pbi.jar fsd-text work/<tab>/fsd.docx` -- expected: writes `work/<tab>/fsd.md`; prints paragraph and table counts
