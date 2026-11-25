# Port guide 03 — read-audit, output writer, command line, slicer, lineage sheet, tests, as-is skill

**What this covers.** The shared machinery of the Python extractor (`tools/rpd_extract`), described so it can be re-implemented in Java 11 with the JDK only. The repository (MDS XML) reader and the Web Catalog reader are covered by guides 01 and 02 and are mentioned here only where this machinery touches them.

**How to read it.** Python function names are in backticks so each claim can be traced. "Not determined" marks behaviour I could not establish from the source. "Recommendation" marks advice for the Java port; everything else is a statement of what the Python does.

**Terms used throughout.**

| Term | Meaning |
|---|---|
| Knowledge Base (KB) | The output directory of CSV tables plus `manifest.csv`. |
| Estate | The in-memory model (`core/model.py`, class `Estate`): one list per output table, plus `unparsed`, `errors`, and four counters. |
| rpd-id | A name the operator declares for one estate. It is the first column of every table and the prefix of every identifier. |
| Half | An estate has two halves: `rpd` (repository XML) and `catalog` (Web Catalog). Either may be absent. |
| row_id | `<rpd-id>::<layer>::<object-path>`. Layers: `phys`, `bmm`, `pres`, `sec`, `var`, `cat`. |

---

## 1. The read-audit ("what was not read")

Source: `adapters/inbound/observing.py`. Both readers use it. It produces the `unread_attribute`, `unread_element` and `unread_text` rows (and `excluded_by_scope`) in `unparsed.csv`.

### 1.1 Principle

The audit does not compare the XML against a declared list of what the parsers should read. It records what the parser code actually read during this run, then reports everything present in the XML that was never read. An earlier version used a hand-maintained list; six entries had drifted from the code and one (`PrivilegePackage@userRef`) hid three privilege grants. Do not port a declared list.

### 1.2 How XML access is wrapped

Every file is parsed once into a tree whose nodes carry read-tracking state. The trees are kept in memory until all parser passes have run, so reads from every pass accumulate on the same nodes (`_parse_all` in both readers; the docstring measures 28 MB of trees for 9 MB of source).

Each element (`Observed`) holds three pieces of state:

| State | Set by | Meaning |
|---|---|---|
| `attributes_read` (set of names) | `RecordingAttrib.get(key)` and `RecordingAttrib[key]` | The parser asked for this attribute by name. |
| `text_read` (boolean) | `read_text(element)` | The parser asked for this element's text. |
| `sought` (boolean) | `sought(element)` | The parser looked this element up, whatever it then took from it. |

Rules that matter:

- **An attribute read is recorded when asked for, not when found.** `get("x")` on an element that has no `x` still adds `x` to the set. This is harmless because the audit only subtracts the set from the attributes present.
- **Only `get` and `[]` record.** Listing keys, iterating, `items()` and membership tests do not. The audit itself reads the present keys through the non-recording path (`dict.keys(element.attrib)`).
- **There is a deliberate bypass.** The repository reader's index builder (`_build_index` in `mds_xml.py`) reads `mdsid`, `name` and `containerRef` on every element through `dict.get(element.attrib, ...)`, which does not record. Reason given in the code: indexing touches those attributes on every element only to build the reference graph; if that counted as a read, every nested structure would look consumed. The Java port needs the same two access paths: a recording one for parsers and a non-recording one for indexing and for the audit.
- **Text is marked read before the empty check.** `read_text` sets `text_read` even when the text turns out to be absent. `read_text` returns the text verbatim, with no trimming, and returns `""` for a missing element or missing text. It is the single place element text is read.
- **`sought` is set by the lookup helpers, on the elements they return.** In the repository reader: `_first_child`, `_children`, `_descendants`. In the catalog reader: `_find`, `_findall`, plus direct `observing.sought(...)` calls on `iter()` results. Plain iteration over children marks nothing.
- **Reading an element's tag is not tracked at all.**

### 1.3 What counts as read

An element is *touched* when `attributes_read` is non-empty, or `text_read` is true, or `sought` is true (`touched` inside `audit_unread`).

An element is *reached* when it is touched or any descendant is touched (`mark`, computed bottom-up once per tree). A wrapper element the parser walked through to get to a child is therefore reached even though nothing was read from the wrapper itself.

Reads are tracked per element instance, never per tag name. Reading `Ref@pick` under element `A` does not silence `Ref@pick` under element `B` (test `test_reads_are_counted_per_element_not_per_tag_name`).

### 1.4 The audit pass

`audit_unread(trees, estate, handled, tag_of, out_of_scope, out_of_scope_kind)`:

- `trees` is a list of `(rpd_id, object_type, root)`.
  - Repository reader: `object_type` is the name of the directory the file sits in (for example `PhysicalTable`).
  - Catalog reader: `object_type` is the root element's tag in `{namespace}local` form.
- A tree whose `object_type` is not in `handled` is skipped entirely. Unhandled types are already reported whole as `unmodelled_type`.
- `tag_of` turns an element into the name used in the report. Repository reader: local name only (namespace stripped). Catalog reader: the full `{namespace}local` name, because two catalog namespaces share local names.

For each handled tree, after `mark`, every element is visited top-down (`visit`), including elements inside an unreached subtree. At each element:

1. **Element.** If the element is not reached, count one against `(rpd_id, object_type, tag)`.
2. **Attributes.** For each attribute present and not in `attributes_read` (in sorted name order): if `out_of_scope(tag, attribute)` is true, count one against the *excluded* counter under `tag@attribute`; otherwise count one against the gap counter under `tag@attribute`. This is done at every level, including on reached elements and inside unreached subtrees.
3. **Text.** If the element's text, after trimming whitespace, is non-empty and `text_read` is false, count one under `tag#text`.

"Text" here is the text between the element's start tag and its first child element. Text that follows a child element (tail text in mixed content) is never audited. Comments and processing instructions are not in the tree and are not audited.

### 1.5 Aggregation and `occurrences`

There are two counters, gaps and excluded, both keyed by `(rpd_id, object_type, detail)`.

- `object_type` is the type of the **file** (see 1.4), not of the element.
- `detail` names the **element itself** by tag, not by its path: `Tag`, `Tag@attribute` or `Tag#text`. The same child tag under different parents inside the same object type is summed into one row.
- `occurrences` is the number of element instances counted: unreached elements for an element row, elements carrying that unread attribute for an attribute row, elements with unread non-blank text for a text row. It is summed across every file of that object type for that rpd-id.

After all trees are visited, one `unparsed` row is written per key (`estate.record_gap`). The kind is derived from the detail string:

```
"@" in detail            -> unread_attribute
detail ends with "#text" -> unread_text
otherwise                -> unread_element
```

Excluded rows get the kind passed in as `out_of_scope_kind`.

Recommendation: carry the kind as a field of the counter key in Java instead of re-deriving it from the string. The Python derivation would misclassify a tag whose namespace URI contained `@`. None does in the sample data.

Example rows (logical columns `rpd_id, kind, object_type, detail, occurrences`):

```
bi-analytics-2013, unread_attribute, Dimension, LogicalKey@isPrimary, 69
sampleapp-v607, unread_element, {com.siebel.analytics.web/dashboard/v1.1}dashboardPage, {com.siebel.analytics.web/dashboard/v1.1}actionLink, 81
sampleapp-v607, excluded_by_scope, {com.siebel.analytics.web/dashboard/v1.1}dashboard, {com.siebel.analytics.web/dashboard/v1.1}dashboard@style, 111
```

### 1.6 Scope exclusion (catalog only)

The repository reader passes no exclusion: every attribute is read or reported (`_audit_unread` in `mds_xml.py`).

The catalog reader passes `out_of_scope = is_styling(attribute)` and kind `excluded_by_scope` (`_audit` in `catalog_xml.py`). `is_styling` takes the attribute's local name (text after the last `}`) and applies:

- If the local name is exactly one of these, it is **not** styling: `fmt`, `textFormat`, `customFormat`, `formatMask`, `biFormat`, `defaultFormat`, `outputFormat`, `format`, `formatOutputType`, `renderFormat`. These change displayed data, not appearance.
- Otherwise it is styling if its lower-cased local name contains any of: `align`, `bgcolor`, `border`, `color`, `fmt`, `font`, `format`, `height`, `indent`, `padding`, `style`, `width`, `wraptext`.

The exception test is case-sensitive; the marker test is case-insensitive. Excluded attributes are still written, counted, so that an exclusion can be told from an oversight. The exclusion applies to attributes only; elements and text are never excluded.

### 1.7 When the audit runs

After every parser pass and every post-pass of that reader, as the last step before unmodelled types are recorded (`load` in `mds_xml.py`) or as the very last step (`load` in `catalog_xml.py`). Running it earlier would report content a later pass reads.

### 1.8 All `kind` values in `unparsed.csv`

`unparsed.csv` holds known gaps: things present in the input that the output does not carry. Nothing here is a failure. Columns: `rpd_id, kind, object_type, detail, occurrences`.

| kind | Produced by | When | object_type | detail | occurrences |
|---|---|---|---|---|---|
| `undiscovered_file` | `_report_undiscovered` (`mds_xml.py`) | A file exists anywhere under the repository root that file discovery did not pick up. Discovery takes only `<base>/<TypeDir>/*.xml`, one level deep. Catches nested files, files without `.xml`, and ordinary strays such as a README. | `File` | Path relative to the root passed to the reader | 1, one row per file |
| `unmodelled_type` (repository) | `Estate.record_unmodelled`, called from `load` in `mds_xml.py` | A file's type directory is in neither the handled set nor the index-only set (`Database`, `Schema`, `ConnectionPool`, `BusinessModel`). | The directory name | The file name | 1, one row per file |
| `unmodelled_type` (catalog) | `_report_unmodelled` (`catalog_xml.py`) | A parsed XML root whose tag no parser handles. Enumerated from what was parsed, never from a list. | The root tag | `"<n> object(s), e.g. <decoded path of the first one met>"` | n, one row per root type |
| `unread_attribute` | `audit_unread` | See 1.4 | File type | `Tag@attr` | Summed |
| `unread_element` | `audit_unread` | See 1.4 | File type | `Tag` | Summed |
| `unread_text` | `audit_unread` | See 1.4 | File type | `Tag#text` | Summed |
| `not_a_catalog_object` | `_parse_all` (`catalog_xml.py`) | A file in the catalog tree does not parse as XML. Its first 16 bytes are classified by signature (`classify_bytes`): PNG, JPEG, GIF, PDF, RTF, ZIP, OLE, Flash, `data:` URI, or `unrecognised binary or text`. | `File` | `"<class> — present in the tree, never a catalog object"` | Files of that class, one row per class |
| `excluded_by_scope` | `audit_unread` via `_audit` (`catalog_xml.py`) | An unread attribute that `is_styling` puts out of scope. | Root tag | `Tag@attr` | Summed |
| `redacted_value` | `_parse_variable` (`mds_xml.py`) | A variable whose name indicates a credential. Its value is withheld; the variable row is kept. | `Variable` | `"<name> — value withheld: the name indicates a credential (NFR11)"` | 1 |

Notes:

- The comment on the `Unparsed` class lists seven kinds and omits `redacted_value`. The code produces eight. I found no others.
- The two flavours of `unmodelled_type` differ in grain: per file on the repository side, per root type on the catalog side.
- `undiscovered_file` is produced only by the repository reader. The catalog reader walks every file (`_catalog_files`), skipping only paths with a `.git` component, so it has nothing to be undiscovered.
- **Quirk:** `_report_undiscovered` writes `corpus_root.name` in the `rpd_id` column, that is, the last component of the directory the reader was given. When the command line calls the reader for an estate folder, that directory is `<estate>/rpd`, so the column reads `rpd`, not the estate's rpd-id. The committed sample KB has no such rows, so this has not been seen in output. Recommendation: write the real rpd-id in Java.
- Coverage effect: `record_gap` does not touch coverage. `record_unmodelled` (repository) adds 1 to `skipped`; the catalog `_report_unmodelled` adds n to `skipped`.

### 1.9 Getting the same effect in Java (design level)

The Python relies on subclassing the parser's element and attribute-map classes. `org.w3c.dom` elements cannot be subclassed that way. Two workable designs:

1. **Own tree built from StAX (recommended).** Read each file with `XMLStreamReader` into a small node class of your own: tag, ordered attribute map, text, children, plus `attributesRead`, `textRead`, `sought`. Give it two accessors: `attr(name)` records, `rawAttr(name)` does not. Route all text reads through one method that sets `textRead` before checking for empty. Make lookup helpers (`firstChild`, `children`, `descendants`) set `sought` on what they return. The audit is then a direct port of `mark` and `visit`.
2. **DOM plus side state.** Parse with `DocumentBuilder` and keep the read state in an `IdentityHashMap<Element, State>` (or `Node.setUserData`). All parser code must go through wrapper functions; any direct `getAttribute` call silently escapes the audit. This is easier to get wrong.

Points to hold to in either design:

- Parse namespace-aware. Python names tags and namespaced attributes as `{uri}local`; `javax.xml.namespace.QName.toString()` produces the same form. Attributes with no namespace are the bare name.
- Namespace declarations (`xmlns`, `xmlns:x`) are not attributes in the Python tree and must not be audited. StAX keeps them separate. DOM presents them as attributes, so filter them out.
- "Text" must mean the character data before the first child element, with CDATA merged in (set `XMLInputFactory.IS_COALESCING`). Keep it verbatim.
- The blank test is Python `str.strip()`. Java `String.strip()` is close but does not treat U+00A0 and a few other no-break spaces as whitespace, where Python does. This only changes whether an element containing nothing but such characters is reported.
- Disable DTD loading and external entities on the factory. Python's parser does not fetch external entities; an undefined entity is a parse error.
- Keep every tree until the audit has run. Do not re-parse per pass.
- The Python `mark` and `visit` are recursive. Use an explicit stack if deep documents are possible.

---

## 2. `errors.csv`

`errors.csv` holds failures: content that should have been captured and was not. It is deliberately a separate file from `unparsed.csv` so that many known gaps cannot bury a few broken references.

Columns: `rpd_id, kind, source_id, object_type, reason`.

### 2.1 `kind` values

| kind | Produced by | When | source_id | object_type |
|---|---|---|---|---|
| `malformed_xml` | `_parse_all` (`mds_xml.py`) | A repository `.xml` file fails to parse. The file is skipped. | File name | Type directory name |
| `unresolved_reference` (repository) | Expression parsing and many `_parse_*` functions in `mds_xml.py` | A reference inside the XML does not resolve to an identifier. The cell is left blank; a raw GUID is never written. | The owning object's identifier, or its `mdsid` | The owner's type, or the pseudo-type `ExpressionRef` for a reference inside an expression |
| `unresolved_reference` (catalog) | `Resolver._record_unresolved` | A catalog path reference names no object or folder in the tree, or names a catalog root the tree does not contain. | Decoded path of the object holding the reference | The referencing element's tag |
| `ambiguous_reference` | `Resolver.resolve` (`catalog_xml.py`) | A reference matches more than one path once case is folded. Recorded as unresolved; no choice is made. | As above | As above |
| `missing_name` | `_parse_*` functions in `mds_xml.py` | An object or sub-object has no `name`, so no identifier can be built. The object is skipped. | Its `mdsid`, or `?` | Its type |
| `unreadable` (repository) | `load` in `mds_xml.py`, from `_object_files` / `_single_repository_files` | A directory cannot be listed or probed (permissions). The run continues. | The root path given to the reader | `Directory` |
| `unreadable` (catalog) | `_parse_all` and `load` in `catalog_xml.py` | A file cannot be opened; or the declared catalog directory holds no files at all. | Decoded path; or the catalog root | `File`; or `Catalog` |
| `duplicate_identifier` | `Estate.check_identifier_uniqueness` | Two or more rows in one table share a `row_id`. One error per duplicated identifier per table. | The identifier | The table's attribute name on `Estate`, for example `physical_tables` |

Notes:

- The comment on `ExtractionError.kind` lists four values. The code produces six: it omits `ambiguous_reference` and `duplicate_identifier`.
- Exact `reason` texts and the exact conditions for each repository `unresolved_reference` / `missing_name` belong to guides 01 and 02.
- The committed sample `errors.csv` contains only `unresolved_reference` (1,556 rows).
- A non-XML file in the catalog is **not** an error (it is `not_a_catalog_object`). A non-parsing `.xml` file in the repository **is** an error.

### 2.2 Coverage side-effect

`Estate.record_error` appends the error and also adds 1 to `skipped` in coverage for `(rpd_id, layer, object_type)` of the error. Consequences:

- Pseudo-types such as `ExpressionRef`, `Directory`, `File`, `Catalog`, and catalog element tags appear as rows in `coverage.csv` with `parsed = 0`.
- An object with one unresolved reference among many is still counted `parsed` under its own type, and the reference is counted `skipped` under the pseudo-type.
- `duplicate_identifier` is appended directly to the error list and does **not** change coverage: the objects did parse (test `test_a_duplicate_is_not_counted_as_skipped`).

### 2.3 Exit-code behaviour

Errors never stop the run. The reader records and continues, the KB is written in full, and the process then exits with code 3 if `errors.csv` has at least one row, else 0 (`main` in `cli.py`). The committed sample KB was produced by a run that exited 3; its README states that this is expected for that data. Scripts must treat 3 as "written, with errors", not as failure to write. Full exit-code table in section 5.

---

## 3. The CSV writer

Source: `adapters/outbound/csv_writer.py`, functions `write_table` and `read_table`.

### 3.1 Format rules

| Aspect | Rule |
|---|---|
| Encoding | UTF-8, no byte-order mark. |
| Delimiter | Comma. |
| Quoting | Every field is wrapped in double quotes, including the header row, numbers and empty strings (Python `csv.QUOTE_ALL`). An empty value is written `""`. |
| Escaping | A double quote inside a value is doubled. Nothing else is escaped. |
| Line terminator | `\n` (LF) after every row, including the last. Never CRLF. |
| Embedded line breaks | A `\n` or `\r` inside a value is written raw inside the quotes. One logical row can span several physical lines. |
| Header | Exactly one header row, always written, even when there are no data rows. |
| Column order | Exactly the header the caller passes. The writer never reorders. |
| Row order | Exactly the order the caller passes. The writer never sorts. Sorting is the caller's job (3.3). |
| Parent directories | Created if missing. |

The writer refuses rather than repairs (all raise an exception):

- Duplicate column names in the header.
- A row whose length differs from the header's. The message names the row index.
- A value that is not a string. The writer does no conversion; the caller converts.

Recommendation: there is no CSV class in the JDK. Write a small writer and a small reader by hand. The reader must handle quoted fields that contain commas, doubled quotes and line breaks; splitting on lines or commas is wrong. Open the output with an explicit UTF-8 charset and write `\n` explicitly; do not use `PrintWriter.println` or `System.lineSeparator()`.

### 3.2 How values are rendered

All rendering happens in `knowledge_base.write`, before the writer is called.

| Python value | Written as |
|---|---|
| String field | Verbatim. No trimming, no case change, no whitespace normalisation. |
| Absent value | The empty string. Model fields default to `""`; `None` never reaches the writer. Empty means "not recorded". |
| Integer field (counts, `position`) | Decimal digits, `str(n)`. |
| Boolean field | `true` or `false`, lower case. |
| Flag copied from the source XML as a string (for example `is_default`, `is_session`, `is_active`, `nullable`, `is_writeable`) | Whatever the source held, verbatim. Usually `true`/`false`, possibly empty. These are not normalised. |
| List of references or names (`*_refs`, `physical_joins.columns`, `logical_columns.time_series`) | Items joined with one space. Empty list gives the empty string. |
| List of aliases (`presentation_subject_areas.aliases`, `presentation_tables.aliases`) | Items joined with `" | "` (space, pipe, space). |
| An expression (`Expression`) | Three adjacent columns in fixed order: text, text_desc, refs (`_expr_cells`). |
| `catalog_versions.xml_version` when empty | The literal `(none)`. This is the only place an empty value is replaced. |

Which model fields are true booleans (written by `"true" if x else "false"`): `logical_columns.is_measure`; `logical_dimensions.is_value_based`, `is_ragged`, `is_skipped`; `logical_levels.is_grand_total`; `variables.redacted`; `analysis_columns.is_computation`, `uses_eval`; `analysis_filters.uses_eval`; `saved_filters.uses_eval`.

Caution on the space separator: identifiers contain spaces (`sampleapp::bmm::SAMPLESALES.Fact Revenue.Revenue`), so a `*_refs` cell with more than one identifier cannot be split back on spaces reliably. The Python never splits them. Either keep the behaviour for compatibility or choose an unambiguous separator in the new project; decide once and record it.

### 3.3 Row ordering

Every table except `manifest` is written as `sorted(rows)`, where each row is a list of the already-rendered strings. Python compares the lists element by element, left to right, each string by Unicode code point.

- For tables with a `row_id`, it is the first column and is unique, so the order is by `row_id`.
- For `unparsed`, `errors`, `coverage`, `catalog_versions`, `coverage_summary`, the order is by the whole row. Numbers are compared as strings and only as a late tie-break.
- Identical rows are kept, not de-duplicated.
- `manifest` rows are written in a fixed order, not sorted (4.2).

Java `String.compareTo` compares UTF-16 code units, which differs from code-point order for characters outside the Basic Multilingual Plane. To reproduce Python's order exactly, compare by code point (or by unsigned UTF-8 bytes, which gives the same order).

### 3.4 The `_guarded` spreadsheet guard, and what dropping it removes

The Python writer has a mechanism the new project does not carry over. It exists because a spreadsheet rewrites certain values when a CSV is opened by double-click, even when the field is quoted.

How it works:

- The caller declares some columns as guard columns. For each, the file gets an extra column named `<column>_guarded` immediately after it (`_expand_header`).
- `needs_guard(value)` is true when the value is non-empty and: starts with `=`, `+`, `-` or `@`; or starts with tab, CR or LF; or matches `^0\d+$` (leading zero); or `^[+-]?\d+(?:\.\d+)?[eE][+-]?\d+$` (scientific notation); or `^\d{16,}$` (16 or more digits). A leading space is deliberately not guarded.
- In a guard column, a value needing a guard is written with one apostrophe in front and the companion cell is `true`; otherwise the value is written unchanged and the companion is `false`.
- In a column not declared as a guard column, a value needing a guard makes the writer raise.
- `read_table` detects a guarded column by its companion being the next column, strips exactly one apostrophe where the flag is `true`, and returns the logical header without companions. It raises if a flagged value has no apostrophe ("the file has been edited"), if a flag is neither `true` nor `false`, if a row has the wrong number of fields, or if the file is empty.

The decision record (`decisions/csv-formula-guard.md`) states the claim precisely: the *value* is recoverable byte for byte; the bytes on disk are altered. Its manual spreadsheet check was never recorded as done (the table in the record is still "Pending").

What the new project loses by writing plain CSV:

- The `_guarded` columns disappear, so headers are exactly the logical headers in section 4.1. Sample Python output has these extra columns; ignore them when comparing.
- No apostrophe is ever added. Values are on disk exactly as extracted.
- Nothing protects a value from a spreadsheet. A formula starting with `-` or `=`, an identifier such as `00123`, a value such as `1E5`, or a 16-digit number will be reinterpreted if the file is opened by double-click and saved. Readers must import columns as text, or not save from a spreadsheet.
- The loud failure for an unexpected hazardous value in an undeclared column goes away. Nothing replaces it.
- The reader no longer needs guard reversal or the tamper check. The slicer no longer needs to re-guard on write (`_GUARDABLE` in `slice.py`), and the lineage sheet no longer needs its `--guarded` option.

What must be kept: all of 3.1 to 3.3, including the refusals for duplicate headers, wrong row width and non-string values.

Recommendation: keep `needs_guard` as a pure function and report a count of at-risk values at the end of a run, as `table_lineage.at_risk` does. It costs little and states the risk instead of hiding it.

---

## 4. `knowledge_base.py`

Source: `adapters/outbound/knowledge_base.py`, functions `write` and `clean`. `VERSION = "0.2.0"`.

`write(estate, out_dir, corpus_root, timestamp)` creates the directory, writes every table below in this order, always, even when a table has no rows, and returns the list of names written. The file name is `<table>.csv`.

### 4.1 Tables, in write order

"Guarded" lists the columns that had a `_guarded` companion in the Python output; the new project has none.

**Repository tables** (source: lists on `Estate` filled by the repository reader)

| # | Table | Source list | Columns, in order | Guarded in Python |
|---|---|---|---|---|
| 1 | `physical_tables` | `physical_tables` | row_id, rpd_id, container, name, table_type, max_conn, source_table_id | name, container |
| 2 | `physical_table_definitions` | `physical_table_definitions` | row_id, rpd_id, table_id, database, text | database, text |
| 3 | `physical_columns` | `physical_columns` | row_id, rpd_id, table_id, name, data_type, special_type, precision, nullable, external_name | name, external_name, precision |
| 4 | `physical_joins` | `physical_joins` | row_id, rpd_id, kind, name, table1_id, table2_id, cardinality, expr_text, expr_text_desc, expr_refs, columns | name, expr_* |
| 5 | `logical_tables` | `logical_tables` | row_id, rpd_id, business_model, name, source_count | name, business_model |
| 6 | `logical_columns` | `logical_columns` | row_id, rpd_id, table_id, name, is_writeable, is_measure, source_count, mapped_source_count, time_series, derived_expr_text, derived_expr_text_desc, derived_expr_refs, mapping_expr_text, mapping_expr_text_desc, mapping_expr_refs | name, all six expr columns |
| 7 | `aggregation_rules` | `aggregation_rules` | row_id, rpd_id, logical_column_id, name, is_default, expr_text, expr_text_desc, expr_refs | name, expr_* |
| 8 | `presentation_subject_areas` | `subject_areas` | row_id, rpd_id, name, business_model, implicit_fact_column_id, is_auto_aggregate, aliases | name, business_model, aliases |
| 9 | `presentation_tables` | `presentation_tables` | row_id, rpd_id, subject_area_id, name, display_name, aliases | name, display_name, aliases |
| 10 | `presentation_columns` | `presentation_columns` | row_id, rpd_id, table_id, name, logical_column_id, override_logical_name, display_name | name, display_name |
| 11 | `presentation_hierarchies` | `presentation_hierarchies` | row_id, rpd_id, table_id, name, dimension_id, level_count, display_column_id | name |
| 12 | `presentation_levels` | `presentation_levels` | row_id, rpd_id, hierarchy_id, position, name, logical_level_id | name |
| 13 | `presentation_level_columns` | `presentation_level_columns` | row_id, rpd_id, level_id, position, presentation_column_id | none |
| 14 | `logical_dimensions` | `logical_dimensions` | row_id, rpd_id, name, business_model, is_value_based, is_ragged, is_skipped, default_root_level_id, level_count | name |
| 15 | `logical_levels` | `logical_levels` | row_id, rpd_id, dimension_id, position, name, member_count, is_grand_total, drill_key_column_id | name, member_count |
| 16 | `logical_table_sources` | `logical_table_sources` | row_id, rpd_id, logical_table_id, name, is_active, priority, fragment_text, fragment_text_desc, fragment_refs, where_text, where_text_desc, where_refs, group_by_text, group_by_text_desc, group_by_refs | name, all nine expression columns |
| 17 | `lts_column_map` | `column_mappings` | row_id, rpd_id, source_id, logical_column_id, physical_column_id, logical_expr_text, logical_expr_text_desc, logical_expr_refs, physical_expr_text, physical_expr_text_desc, physical_expr_refs | all six expr columns |
| 18 | `lineage` | `lineage` (built by a repository-reader post-pass) | row_id, rpd_id, presentation_column_id, logical_column_id, source_id, physical_table_id, physical_column_id | none |
| 19 | `bmm_joins` | `bmm_joins` | row_id, rpd_id, name, join_type, table1_id, multiplicity1, is_aggregate1, table2_id, multiplicity2, is_aggregate2 | name |
| 20 | `variables` | `variables` | row_id, rpd_id, name, is_session, init_block_id, security_mask, redacted, expr_text, expr_text_desc, expr_refs | name, expr_* |
| 21 | `init_blocks` | `init_blocks` | row_id, rpd_id, name, refresh_period, is_session, is_enabled, init_string | name, init_string |
| 22 | `security_rls` | `security_rules` | row_id, rpd_id, kind, role, subject_id, detail, expr_text, expr_text_desc, expr_refs | role, detail, expr_* |

**Catalog tables** (source: lists filled by the catalog reader, except `report_lineage`)

| # | Table | Source list | Columns, in order | Guarded in Python |
|---|---|---|---|---|
| 23 | `dashboards` | `dashboards` | row_id, rpd_id, name, catalog_path, xml_version, page_count | name, catalog_path |
| 24 | `dashboard_pages` | `dashboard_pages` | row_id, rpd_id, dashboard_id, position, name, catalog_path, xml_version | name, catalog_path |
| 25 | `dashboard_contents` | `dashboard_contents` | row_id, rpd_id, container_id, position, element, reference, resolution, target_id, target_kind, detail | element, reference, detail |
| 26 | `analyses` | `analyses` | row_id, rpd_id, name, catalog_path, xml_version, criteria_count, column_count | name, catalog_path |
| 27 | `analysis_criteria` | `analysis_criteria` | row_id, rpd_id, analysis_id, position, subject_area | subject_area |
| 28 | `analysis_columns` | `analysis_columns` | row_id, rpd_id, analysis_id, criteria_id, position, column_id, heading, table_heading, formula_construct, formula_type, formula, is_computation, uses_eval | heading, table_heading, formula_construct, formula_type, formula, column_id |
| 29 | `analysis_views` | `analysis_views` | row_id, rpd_id, analysis_id, position, view_type, name, references_views, bound_column_count, display_type, display_subtype | view_type, name, display_type, display_subtype |
| 30 | `report_lineage` | `report_lineage`, built by `core/report_lineage.resolve` after both halves are merged (5.4) | row_id, rpd_id, analysis_id, criteria_id, analysis_column_id, subject_area, subject_area_id, reference_text, presentation_column_id, resolution, logical_column_id, physical_column_id, source_state | subject_area, reference_text |
| 31 | `analysis_filters` | `analysis_filters` | row_id, rpd_id, scope, owner_id, position, op, expr_type, expr_text, reference, target_id, resolution, uses_eval | op, expr_type, expr_text, reference |
| 32 | `analysis_prompts` | `analysis_prompts` | row_id, rpd_id, analysis_id, position, prompt_type, column_type, column_formula, set_variable, operator, ui_control | prompt_type, column_type, column_formula, set_variable, operator, ui_control |
| 33 | `saved_filters` | `saved_filters` | row_id, rpd_id, name, catalog_path, xml_version, op, expr_type, expr_text, uses_eval | name, catalog_path, op, expr_type, expr_text |

**Run-report tables**

| # | Table | Source | Columns, in order | Guarded in Python |
|---|---|---|---|---|
| 34 | `unparsed` | `estate.unparsed` (section 1.8) | rpd_id, kind, object_type, detail, occurrences | detail |
| 35 | `errors` | `estate.errors` (section 2) | rpd_id, kind, source_id, object_type, reason | source_id, reason |
| 36 | `coverage` | `estate.coverage` (4.3) | rpd_id, layer, object_type, parsed, skipped | object_type |
| 37 | `catalog_versions` | `estate.catalog_versions` (4.5) | rpd_id, object_type, xml_version, objects | object_type |
| 38 | `coverage_summary` | `estate.input_census` via `_summary_rows` (4.4) | rpd_id, measure, count, of_files_seen, percent | percent |
| 39 | `manifest` | Built in `write` (4.2) | key, value | value |

Every one of the 39 files is written on every run. With a repository-only input the catalog tables are header-only, and the manifest says which halves were supplied.

### 4.2 `manifest.csv`

Two columns, `key` and `value`. Rows in this fixed order, not sorted:

| key | value |
|---|---|
| `extractor_version` | The constant `VERSION`. |
| `extracted_at` | The `--timestamp` argument verbatim if given (no validation; tests pass `-`). Otherwise the current UTC time as `yyyy-MM-ddTHH:mm:ssZ` (`_timestamp`). |
| `corpus_root` | The `--corpus` argument as typed, not made absolute. |
| `rpd_ids` | All rpd-ids, sorted, joined with one space. |
| `halves_present:<rpd-id>` | One row per rpd-id in sorted order: the halves supplied, in the order `rpd` then `catalog`, joined with a space; `none` if empty. |
| `pairing:<rpd-id>` | Directly after its `halves_present` row. Two halves: `declared by the Operator: both halves supplied in one estate folder under one rpd-id (AD-22). Never inferred.` One half: `not paired: only the <half> half was supplied`. None: `not paired: no half was supplied`. |
| `tables` | The names of every table written before the manifest, sorted, joined with one space. `manifest` itself is not in the list (38 names). |

The `halves_present` and `pairing` rows alternate per rpd-id (`_halves_rows`). The manifest is written last.

The manifest has three jobs elsewhere in the system:

- It is proof that the extractor wrote a directory (`clean`, 4.6).
- It is the only source of the table list for downstream tools (`_load` in `slice.py` and `table_lineage.py`). They never list the directory, so a stale file from an older run is ignored.
- It makes "no dashboards" distinguishable from "dashboards not extracted".

### 4.3 `coverage.csv`

`estate.coverage` maps `(rpd_id, layer, object_type)` to two counters, `parsed` and `skipped`. One output row per key.

- `layer` here is the store, not the identifier layer: `rpd` for everything the repository reader counts, `cat` for the catalog reader.
- `parsed` is incremented by the readers with `Estate.count(rpd_id, type, "parsed")`: once per object, and also once per sub-object under its own type name (for example `PhysicalColumn`, `AggrRule`, `ColumnMapping`, `LogicalLevel`). Index-only repository types are counted `parsed` once per file. The catalog reader counts views inside analyses in bulk under the label `saw:view (element, inside an Analysis)`.
- `skipped` is incremented by `record_error` (2.2), by `record_unmodelled`, and by the catalog's `_report_unmodelled`.
- When two halves are merged, counters with the same key are added (`Estate.merge`).

The exact list of `count` calls belongs to guides 01 and 02.

### 4.4 `coverage_summary.csv`

`estate.input_census` maps `(rpd_id, measure)` to a count. Only the catalog reader fills it (`Estate.census`). Measures:

| measure | Incremented |
|---|---|
| `files_seen` | Once per file found in the catalog tree, before any parse attempt. |
| `parsed_as_xml` | Once per file that parsed as XML. |
| `not_a_catalog_object` | Once per file that did not parse as XML (and could be opened). |
| `modelled_object` | Once per parsed root handled by a parser. |
| `unmodelled_object` | n per unmodelled root type (the same n as the `unmodelled_type` row). |

`_summary_rows` writes one row per `(rpd_id, measure)`:

- `count` = the count.
- `of_files_seen` = that rpd-id's `files_seen` count, or `0` if it has none.
- `percent` = `round(100 * count / files_seen)` followed by `%`, or `n/a` when `files_seen` is zero or missing.

Python `round` rounds exact halves to the even integer. Use `Math.rint(100.0 * count / total)` in Java, not `Math.round`.

Facts worth knowing:

- An rpd-only estate has no rows in this table at all. The repository reader does not count input files.
- A file that could not be opened is in `files_seen` and in no other measure, so the measures need not add up to `files_seen`.
- A catalog directory with no files returns before any census call, so it has no rows.
- Sample: `files_seen 5371`, `parsed_as_xml 4341 (81%)`, `not_a_catalog_object 1030 (19%)`, `modelled_object 2850 (53%)`, `unmodelled_object 1491 (28%)`.

### 4.5 `catalog_versions.csv`

`estate.catalog_versions` maps `(rpd_id, object_type, xml_version)` to a number of objects (`Estate.record_version`). An empty version is a counted value and is written as `(none)`. Which objects are recorded is the catalog reader's business (guide 02); `_report_unmodelled` records every unmodelled root.

### 4.6 `clean` and the `--clean` safety rule

`clean(out_dir)`:

1. If the directory does not exist, do nothing and return an empty list.
2. List its entries, ignoring one named `.gitignore`. If any entry remains and there is no `manifest.csv`, refuse (`NotAKnowledgeBase`): "refusing to clean <dir>: it is not empty and has no manifest.csv, so this extractor did not write it. Point --out at a Knowledge Base directory, or at a new one."
3. Otherwise delete every `*.csv` at the top level of the directory, in sorted name order, and return their names.

Only top-level `.csv` files are removed. Subdirectories, `.gitignore`, a README and anything else are left alone. Note that step 3 deletes every top-level CSV, including ones the extractor never wrote, once a manifest is present.

Without `--clean`, `write` overwrites the files it writes and leaves any other file in place. That is how a stale table from an older version can survive; the manifest's `tables` list is what keeps downstream tools from reading it.

---

## 5. The command line (`cli.py`, with `layout.py`)

Invocation: `python -m tools.rpd_extract.cli`. No install step.

### 5.1 Arguments

| Argument | Required | Meaning |
|---|---|---|
| `--corpus PATH` | yes | The estate folder, a repository tree, or a directory of estates (5.2). |
| `--out PATH` | yes | Output directory for the KB. |
| `--rpd-id NAME` | no | Declares the rpd-id when `--corpus` is one estate. Omit when `--corpus` holds one folder per estate. |
| `--clean` | no (flag) | Delete existing KB tables in `--out` before writing (4.6). |
| `--timestamp TEXT` | no | Overrides the manifest timestamp, for reproducible runs. |

`--help` prints the description and the accepted-layouts text (`layout.ACCEPTED`) and exits 0. Deliberately absent: any option to extract a single dashboard or subject area (extraction is always estate-wide), and any handling of BAR archives.

Python's `argparse` also accepts unambiguous prefixes of option names (`--corp`). The Java port need not reproduce that.

### 5.2 Estate and rpd-id rules (`layout.resolve`)

rpd-id format (`identifiers.validate_rpd_id`): must match `^[a-z0-9]+(?:-[a-z0-9]+)*$` (lower-case letters and digits, single hyphens between groups). It is never derived from a file name, timestamp or hash.

Layout is decided from directory names only. No file content is inspected. Markers:

- A directory is a *repository tree* if it contains the directory path `oracle/bi/server/base`.
- A directory is an *estate folder* if it contains a subdirectory `rpd` and/or a subdirectory `catalog`. Whatever is in `catalog/` is the catalog half because the operator put it there.

Resolving one candidate directory under a known rpd-id (`_resolve_one`):

1. If it has `rpd/` and/or `catalog/`, those are the halves (`rpd` first). The `rpd/` directory is taken as the repository root without checking that it contains `oracle/bi/server/base`; the repository reader checks later.
2. Otherwise, if the directory itself is a repository tree, it is one `rpd` half.
3. Otherwise nothing.

`resolve(corpus, rpd_id)`:

- `--corpus` is not a directory: error.
- **`--rpd-id` given:** validate it, resolve `--corpus` as one candidate. No halves: error.
- **`--rpd-id` omitted:** take the immediate subdirectories of `--corpus` in sorted name order. Resolve each as a candidate, using the folder name as rpd-id. A subdirectory that yields nothing is skipped silently. A subdirectory that yields halves must have a valid rpd-id as its name, otherwise error. The name is validated only after the folder has proved to be an estate, so an unrelated neighbour folder with a non-kebab name does not break the run. No halves at all: error.

A run that matches nothing raises rather than writing an empty KB, because a directory of empty tables reads like an estate that contains nothing.

The no-match message (`_no_match_message`) starts with a hint when one applies, then `<corpus> matches no accepted layout.`, then the accepted-layouts text:

- `--corpus` is named `base` or has a subdirectory holding `.xml` files: says it looks like the `base` directory itself and suggests the directory four levels up.
- No `--rpd-id` and `--corpus` is itself a repository tree: "is itself a repository tree, so it needs a name. Add --rpd-id."
- `--rpd-id` given: "must hold rpd/, catalog/, or oracle/bi/server/base/ — it holds none of them."

The accepted-layouts text, for reuse in the Java help:

```
accepted layouts:

  1. estate folder, with either half optional  (needs --rpd-id)
       <corpus>/rpd/oracle/bi/server/base/...
       <corpus>/catalog/...

  2. one repository tree, RPD only             (needs --rpd-id)
       <corpus>/oracle/bi/server/base/...

  3. several estates, one folder each          (no --rpd-id; the folder
       <corpus>/<rpd-id>/...                    name IS the rpd-id)
                        ...where each <rpd-id> folder is layout 1 or 2
```

`Layout` also records a `shape` (`estate`, `repository`, `multi`) and each half records `declared_by`. Neither is written to any output file I read; tests assert only that they are set.

### 5.3 Order of work in `main`

1. Parse arguments.
2. Resolve the layout. On `LayoutError` or `IdentifierError`: print `error: <message>` to standard error, exit 2. Nothing has been written or deleted.
3. Extract (`extract`, 5.4). On `CorpusError`, `CatalogError` or `IdentifierError`: print `error: <message>` to standard error, exit 2. Still nothing written or deleted.
4. If `--clean`: run `clean`. On refusal: `error: <message>`, exit 2. Otherwise print the `cleaned:` line.
5. Write the KB.
6. Print the summary. Exit 3 if there are error rows, else 0.

Layout resolution and extraction both complete before anything is deleted or written. A failed run leaves the output directory untouched.

### 5.4 `extract`

1. Create an empty estate. Set its rpd-id list from the layout (sorted, unique) and record the halves present per rpd-id.
2. For each half, in layout order: run the matching reader into its **own** new estate, then merge it in (`Estate.merge`). The repository reader is always called with the rpd-id, so it runs in single-repository mode.
3. Run `report_lineage.resolve` on the merged estate.
4. Run `check_identifier_uniqueness` on the merged estate.

`Estate.merge` is additive: lists are appended, coverage counters added per key, other counters added per key, `halves_present` updated. The rpd-id list is deliberately not merged; the caller's list stands, so an estate whose half turned out empty is still listed. The code comment records why each reader gets its own estate: sharing one made each reader's post-passes run over the other's rows, duplicating lineage and security rows and reporting 1,507 false duplicates.

`report_lineage.resolve` (in `core/`, not a reader) fills `report_lineage` with one row per analysis column. In brief:

- It clears the list first, so it is idempotent. It does nothing when there are no analysis columns.
- The subject area named by the analysis (quotes stripped for matching; the verbatim text is kept in the `subject_area` column) is matched to a subject area of the **same rpd-id** by exact name, then by lower-cased name.
- A formula that is exactly `"Table"."Column"` (regex `^"([^"]+)"\."([^"]+)"$` on the trimmed formula) is looked up as (rpd-id, subject area, table, column): exact match gives `resolution = exact`; a lower-cased match gives `case_normalised`; otherwise `unresolved`.
- A formula that is not a bare reference gives `not_a_column_reference`, except one starting `XSA(`, which gives `outside_semantic_model`. An empty formula gives `not_a_column_reference`.
- When a presentation column was found: if `lineage` has a row for it, copy that row's logical and physical column ids and set `source_state = sourced`; otherwise `computed_not_sourced`. Where a presentation column has several lineage rows, the last one in list order wins (a dictionary keyed by presentation column id).
- `row_id` is the analysis column's object path with `#lineage` appended, in layer `cat`.

Because matching is within one rpd-id, it only ever joins halves the operator declared as one estate.

`check_identifier_uniqueness` sweeps every list on the estate, in attribute-name order, and reports each `row_id` that occurs more than once in the same list (2.1). The sweep is generic so a table added later is covered without being listed. In Java, without reflection, the equivalent is a single registry of all tables that both the writer and this check iterate.

### 5.5 Messages printed

On success, to standard output, in this order (`<out>` is `--out` as typed):

```
cleaned:  <n> table(s) removed from <out>          (only with --clean)
rpd-ids:  <id>, <id>, ...
  <id>: rpd + catalog                               (one line per rpd-id; halves joined with " + ", or "none")
parsed:   <sum of all coverage "parsed" counters> objects
gaps:     <number of rows in unparsed> not modelled  -> <out>/unparsed.csv
errors:   <number of rows in errors>                 -> <out>/errors.csv
written:  <out>
```

`gaps` and `errors` are row counts, not sums of `occurrences`. The `parsed` figure includes sub-objects (4.3).

### 5.6 Exit codes

| Code | Meaning |
|---|---|
| 0 | KB written; `errors.csv` has no rows. |
| 3 | KB written; `errors.csv` has at least one row. |
| 2 | Nothing written: bad layout, invalid rpd-id, repository root unusable or holding no XML (`CorpusError`), catalog root not a directory (`CatalogError`), identifier that cannot be built (`IdentifierError`), or `--clean` refused. Also argument errors (argparse's own exit code). |
| 1 | Not set by the code. Any other exception (for example a file-system error during writing) escapes as a Python traceback, which exits 1. |

Asymmetry to preserve or consciously change: a declared `rpd/` half with no XML files stops the run with exit 2; a declared `catalog/` half with no files records one `unreadable` error and the run continues (exit 3).

### 5.7 Identifiers (`core/identifiers.py`)

- `make_id(rpd_id, layer, *parts)` returns `<rpd-id>::<layer>::<part>.<part>...`. Parts are joined with `.`.
- It refuses: an invalid rpd-id; a layer outside `phys`, `bmm`, `pres`, `sec`, `var`, `cat`; no parts; a part that is empty or only whitespace; a part containing `::`.
- A catalog object passes its whole decoded path as **one** part. Catalog names contain literal dots and are `/`-separated; splitting them would make different objects collide.
- `parse_id(row_id)` splits on `::` and requires exactly three pieces, a valid rpd-id and a known layer. It returns the object path unsplit. Names inside the path may contain dots, so the path cannot be split back into parts.
- Not every `row_id` in the output is a plain `make_id` result. Several are built by appending a suffix to another identifier: `<column id>#aggr<n>` (aggregation rules), `<source id>#map<n>` (column mappings), `<source id>#filter` (row-filter rules, `_collect_row_filters`), and `<presentation column id>#<source id>` (lineage rows). The lineage form embeds one identifier inside another, so it contains `::` four times and `parse_id` rejects it. Do not assume every `row_id` parses; guides 01 and 02 own the full identifier scheme.

---

## 6. The slicer (`slice.py`)

Purpose: cut every row reachable from one subject area out of the KB into a small directory of CSVs, so that an analysis reads a few thousand rows instead of the whole KB, and so that the foreign-key walk is done by deterministic code, not by a language model.

Invocation: `python -m tools.rpd_extract.slice --kb <dir> [--subject-area <name or row_id>] [--out <dir>]`.

### 6.1 Loading (`_load`)

1. `manifest.csv` must exist in `--kb`; otherwise error ("has no manifest.csv, so it is not a Knowledge Base", with the extractor command).
2. Read the `tables` value and split on whitespace.
3. Read each listed table. A listed table whose file is missing is an error ("Re-run the extractor with --clean").
4. Files in the directory that the manifest does not list are never read.

### 6.2 Listing mode

With no `--subject-area`: print one line per subject area, sorted by `(row_id, rpd_id, name)`, formatted as two spaces, the rpd-id left-aligned and padded to 20 characters, one space, the name. Exit 0. `--out` is not needed.

### 6.3 The starting set

The argument is compared, exactly and case-sensitively, with both `row_id` and `name` of every row of `presentation_subject_areas`.

- No match: error listing the available names, sorted (or `(none)`).
- More than one match (the same name in two repositories): error listing the matching `row_id`s and asking for the full `row_id`.
- Exactly one match: that row is the starting set.

### 6.4 The walk (`build`)

Each step filters one KB table using id sets built by earlier steps. `S` is the starting subject area row.

| Step | Slice table | Rows kept | Ids collected |
|---|---|---|---|
| 1 | `presentation_subject_areas` | `S` | area id |
| 2 | `presentation_tables` | `subject_area_id` = area id | presentation table ids |
| 3 | `presentation_columns` | `table_id` in presentation table ids | presentation column ids |
| 4 | `logical_columns` | `row_id` in L, where L = every non-empty `logical_column_id` from step 3, plus `S.implicit_fact_column_id` if non-empty | L (logical column ids) |
| 5 | `logical_tables` | `row_id` in LT, where LT = the `table_id` of every row kept in step 4 | LT (logical table ids) |
| 6 | `aggregation_rules` | `logical_column_id` in L | |
| 7 | `logical_table_sources` | `logical_table_id` in LT | SRC (source ids) |
| 8 | `lts_column_map` | `source_id` in SRC **and** `logical_column_id` in L | |
| 9 | `lineage` | `presentation_column_id` in presentation column ids | |
| 10 | `physical_columns` | `row_id` in PC, where PC = non-empty `physical_column_id` from step 9 rows and from step 8 rows | |
| 11 | `physical_tables` | `row_id` in PT, where PT = the `table_id` of every row kept in step 10 | PT (physical table ids) |
| 12 | `physical_joins` | `table1_id` in PT **and** (`table2_id` empty **or** in PT) | |
| 13 | `bmm_joins` | `table1_id` in LT **or** `table2_id` in LT | |
| 14 | `variables` | Name match against expression text, see below | init block ids |
| 15 | `init_blocks` | `row_id` in the non-empty `init_block_id`s of step 14 rows | |
| 16 | `security_rls` | `subject_id` in LT **or** in SRC | |

Step 14 in detail. Build one string by joining, with spaces, every cell of the rows kept in steps 4, 6, 7 and 8 whose column name contains `expr` or starts with `where`, `fragment` or `group_by` (this includes the `_text`, `_text_desc` and `_refs` columns). Keep every row of `variables` whose `name` is non-empty and occurs as a substring of that string.

Why each table is included:

- Steps 1 to 3: what report authors see.
- Step 4 adds the implicit fact column explicitly because it can sit outside the presentation columns (test `test_implicit_fact_column_is_pulled_in`).
- Steps 5 to 8: the logic behind each presented column, and which sources can supply it.
- Steps 9 to 11: where the data comes from physically.
- Step 12 keeps physical joins only when both ends are in scope. A join reaching outside is not part of the subject area's picture.
- Step 13 is looser: either end in scope is enough. The code comment above both filters says "both sides"; that is true of the physical filter only.
- Steps 14 to 16: variables, their initialisation blocks, and security attached to the logical tables or sources in scope.

Output assembly: for each of the 16 tables, if the manifest lists it, the slice holds the table's own header (in KB order) and the kept rows sorted the same way the KB sorts (whole-row string order). A table the manifest does not list is silently left out. A table with no kept rows is still written, header only. Rows are exact copies of KB rows (test `test_slice_is_a_strict_subset`).

### 6.5 What the slice leaves out (known limits)

These are properties of the Python as written. The sample analysis document records, in its "notes for the tool team", that it had to read the full KB to work round several of them.

- Not sliced at all: `physical_table_definitions`, `presentation_hierarchies`, `presentation_levels`, `presentation_level_columns`, `logical_dimensions`, `logical_levels`, every catalog table, and `errors` / `unparsed` / `coverage`. So opaque-view SQL, drill paths, and the gaps relevant to this subject area are absent from the slice.
- Logical columns referenced only from another column's formula are not pulled in. Only columns named directly by a presentation column, plus the implicit fact column, are.
- A physical table that an alias stands for is not pulled in unless one of its own columns is in scope.
- `security_rls` rows whose subject is a presentation object or a logical column (object privileges) are not matched; only subjects that are logical tables or sources are.
- The variable match is a plain substring test over all variables of **all** rpd-ids. A variable from another repository with the same name is included, and a short name can match inside a longer word.
- Physical joins, security predicates and init-block SQL are not searched for variable names.

Recommendation: decide for each of these whether the Java slicer reproduces the limit or closes it, and record the decision. Closing the first one (hierarchies and view definitions) and restricting variables to the slice's own rpd-id are the two that the sample document needed most.

### 6.6 Writing (`write`) and the slice manifest

Each slice table is written as `<out>/<table>.csv` with the standard writer. Then `<out>/slice-manifest.csv`, columns `key, value`, rows in this order:

| key | value |
|---|---|
| `subject_area` | The `--subject-area` argument as typed (a name or a row_id; not normalised). |
| `knowledge_base` | The `--kb` argument as typed. |
| `tables` | The slice's table names, sorted, joined with one space. |
| `rows` | The total number of data rows across all slice tables. |

Sample: `subject_area = Sample Sales`, `knowledge_base = estate/knowledge-base`, 16 table names, `rows = 351`.

The slice manifest records neither the rpd-id nor the extraction timestamp. A reader needing them must open the KB's own manifest.

### 6.7 Messages and exit codes

On success, to standard output:

```
subject area: <argument>
tables:       <number of slice tables>
rows:         <total rows>
written:      <out>
```

| Code | When |
|---|---|
| 0 | Listing printed, or slice written. |
| 2 | `SliceError` (`error: <message>` on standard error): no manifest, listed table missing, no such subject area, ambiguous name. Also `--subject-area` given without `--out` (argparse usage error). |

The slicer does not clean its output directory. Files from an earlier slice into the same directory stay.

---

## 7. The table-lineage sheet (`table_lineage.py`) — optional handout

Invocation: `python -m tools.rpd_extract.table_lineage --kb <dir> --out <file.csv> [--guarded]`.

It reads the KB only and writes one flat CSV with one row per chain, for the whole KB (all rpd-ids):

```
rpd_id, subject_area, presentation_table, logical_table, logical_table_source,
physical_table_alias, physical_table_original, intermediate_view, final_table, comment
```

How it is built (`build`):

1. Load through the manifest, as the slicer does. It needs `presentation_subject_areas`, `presentation_tables`, `presentation_columns`, `logical_tables`, `logical_columns`, `logical_table_sources`, `lineage`, `physical_tables`, `physical_table_definitions`. If the manifest does not list one, or `physical_tables` has no `source_table_id` column, it refuses and asks for a re-extraction.
2. Group by the pair (presentation table id, logical table id). Every presentation column contributes its pair, with the logical table found through its logical column (empty if unresolved). Every `lineage` row adds its `(source_id, physical_table_id)` to the pair of its presentation column. Grouping is on identifiers, never names.
3. For each presentation table: no pair at all gives one row with the first three cells filled and the rest empty. A pair with no lineage gives one row filled through `logical_table`. Otherwise one row per `(source, physical table)`.
4. Physical cells (`_physical_cells`):
   - If the physical table has a `source_table_id` that resolves, it is an alias: `physical_table_alias` is the alias, `physical_table_original` is the table it stands for. Otherwise the alias cell is empty and the table is the original.
   - Table names are printed as `<container>.<name>` (`_qualified`), because bare names collide.
   - If the original's `table_type` is `select` (an opaque view): `intermediate_view` is the original's qualified name and `final_table` is its definition text verbatim (the SQL).
   - Otherwise `intermediate_view` is empty and `final_table` is the recorded definition text (the name in the database), or the table's own bare name when none is recorded.
   - Definition text comes from `physical_table_definitions` for the original, plus the alias's own when it is an alias. If all texts are identical, that one text; if they differ, one line per distinct `(database, text)`, sorted, formatted `[<database>] <text>` and joined with line breaks (`_definition`).
   - A physical table id that is not in `physical_tables` is shown as the raw id in `physical_table_original`.
5. A name that cannot be resolved is shown as the identifier itself. `comment` is always empty; it is for the reader to fill in.
6. Rows are sorted as whole rows.

Nothing is dropped for lack of lineage: an empty cell means "not recorded", and a missing row cannot happen.

Output: plain CSV by default, with the standard format rules and no companion columns. It prints `rows`, `aliases`, `opaque views`, `no physical` counts and the output path. If any value would be altered by a spreadsheet (`at_risk`, using `needs_guard`) it prints a warning with the count to standard error. `--guarded` writes the guarded form instead; the new project has no such form. Exit 0 on success, 2 if the KB is unusable (and no file is written).

### 7.1 Two smaller scripts, for completeness

- `semantic_model.py` is an unpackaged script (`python semantic_model.py <kb> <subject area name>`) that prints a markdown description of one subject area: tables and columns with kind, OBIEE logic and physical source; how each logical table is loaded; relationships; row-level security; variables referenced. It reads the CSVs directly with a plain CSV reader, so it does not reverse guards. It has no error handling and no tests, and its closing note about hierarchies not being modelled is out of date. Treat it as a sketch, not as reference behaviour.
- `verify.py` writes a per-construct accuracy report (columns `construct, evidence, status, instances, detail`). `evidence` is `corpus` (the construct was found in real input) or `constructed` (compared value by value with hand-written ground truth); the two are never merged. `status` is `pass`, `fail`, or `unimplemented` (no input exercises the construct; never reported as a pass). A check that throws is a `fail`. It prints a tally and exits 1 if anything failed, 2 on an unusable input, else 0. The checks themselves are tied to repository-reader behaviour (guide 01).

---

## 8. Invariants the tests enforce — checklist for the Java test suite

The Python tests use only the standard library's `unittest`. The Java suite must likewise add no third-party dependency to the product; whether a test-only framework is allowed is a project decision this guide cannot make.

A rule repeated across the Python tests: **every check is shown to be able to fail.** Each detector has a test that plants the defect and asserts it is reported. Keep that pattern.

### 8.1 Determinism

- [ ] Writing the same table twice gives byte-identical files (`test_writing_twice_is_byte_identical`).
- [ ] Extracting the same input twice with the same `--timestamp` gives a byte-identical KB, every file including the manifest (`test_rerun_is_byte_identical`).
- [ ] Slicing twice gives equal results; building the lineage sheet twice gives byte-identical files.
- [ ] No output depends on file-system listing order, hash-map iteration order, the clock (except the manifest timestamp, which can be overridden), locale or default charset.
- [ ] A committed KB matches a fresh extraction of the same input byte for byte, using the committed manifest's own `corpus_root` and `extracted_at`. This test is skipped, visibly, when the input is not available (`Reproduction`).

### 8.2 CSV format

- [ ] No `\r` anywhere in a written file produced from values without `\r`; LF line endings.
- [ ] No byte-order mark; the file decodes as UTF-8; non-ASCII text survives a write and read.
- [ ] Every field is quoted; the file starts with the quoted header.
- [ ] Row order and column order are exactly as supplied.
- [ ] Round trip: each of these values is identical after write then read — ordinary text; empty string; embedded comma, double quote, line break; trailing comma; leading `-`, `=`, `+`, `@`; `00123`; `1E5`, `1e5`, `-1.5e-3`; a 16-digit number; a value starting with an apostrophe; non-ASCII text; real expression text such as `"xe".""."SAMPLESALES"."SAMP_REVENUE_F"."BILL_DAY_DT"` and `"%1"`.
- [ ] The writer raises on: duplicate header names; a row of the wrong width (message names the row); a non-string value. The reader raises on an empty file and on a row of the wrong width.
- [ ] A hazardous value written through the whole pipeline comes back unchanged: the fixture's `Negative Margin` formula is `-1 * "%1", "x"` followed by a line break and `next`.

The Python also tests guard behaviour (apostrophe on disk, companion flags, tamper detection, undeclared-column failure). Those tests have no counterpart once the guard is dropped.

### 8.3 Knowledge Base shape

- [ ] The set of CSV files in a KB is exactly the manifest's `tables` list plus `manifest.csv`.
- [ ] Each file's header line is byte-identical to what the writer emits for an empty estate.
- [ ] Every row of every file has as many fields as its header.
- [ ] Planted defects are detected: stray characters before a header; a renamed table (reported both as missing and as unexpected); a short row.
- [ ] The manifest carries `extractor_version`, `extracted_at`, `rpd_ids` and `tables`, with the expected values.
- [ ] Tests look rows up by column name, never by position. A positional lookup broke silently when a column was added.

### 8.4 Identifiers and references

- [ ] No `row_id` occurs twice within a table, for every fixture.
- [ ] A planted duplicate produces exactly one `duplicate_identifier` error naming the identifier and the table, and coverage does not change.
- [ ] The uniqueness check covers every table without a hand-kept list.
- [ ] Foreign keys resolve. The Python asserts only one: every `physical_columns.table_id` is a `physical_tables.row_id` (`test_referential_integrity`). Recommendation: assert every non-empty `*_id` column against its target table. Empty is allowed and means "not resolved"; an unresolved reference must have a matching row in `errors.csv`.
- [ ] `make_id` / `parse_id` round-trip; a non-kebab rpd-id, an empty part and a part containing `::` are refused.

### 8.5 Read-audit

Using a small inline XML document and a reader that touches chosen parts:

- [ ] An attribute nothing reads is reported as `unread_attribute`.
- [ ] An attribute the parser reads is not reported.
- [ ] With no reads at all, every attribute, the unknown child element and its attribute are reported.
- [ ] Element text nobody reads is reported as `unread_text`; text read through the text accessor is not.
- [ ] A child the parser looked up but took nothing from is not an `unread_element`, yet its unread attribute is still reported.
- [ ] Reading an attribute under one parent does not silence the same tag and attribute under another parent; `occurrences` is 1 for the unread one.
- [ ] The root of a handled object is not reported as an unread element.
- [ ] Gaps carry only the known `kind` values; gaps and errors are separate collections.
- [ ] Catalog: a styling attribute lands under `excluded_by_scope`, not `unread_attribute`.

### 8.6 Nothing vanishes silently

- [ ] A file nested one level too deep, and a file without the `.xml` extension, are each reported as `undiscovered_file`. An ordinary tree reports none.
- [ ] An object type with no parser yields an `unmodelled_type` gap and no error.
- [ ] A directory that cannot be listed yields one `unreadable` error and the rest is still extracted.
- [ ] A non-XML file in the catalog is a `not_a_catalog_object` gap, never an error.
- [ ] The catalog census accounts for every file.

### 8.7 `--clean`

- [ ] Stale CSVs in a KB directory are removed.
- [ ] A non-empty directory without a manifest is refused and its files are untouched.
- [ ] An empty directory and a missing directory are both fine.
- [ ] Non-CSV content (`.gitignore`, subdirectories) is left alone.

### 8.8 Slicer and lineage sheet

- [ ] Listing returns the fixture's subject areas.
- [ ] The slice reaches every layer (the key tables are non-empty).
- [ ] Every slice row is an exact row of the KB, under the same header.
- [ ] The implicit fact column is included.
- [ ] An unknown subject area raises and the message lists the available ones; a directory without a manifest is refused.
- [ ] The slice manifest records the subject area and the tables.
- [ ] Lineage sheet: one row per chain, not per column; ordinary table, renamed-in-database table, opaque view, alias of a view, disagreeing definitions, and a presentation table with no lineage each give the cells described in section 7; `comment` is always empty; a KB missing a needed table is refused; a non-KB directory exits 2 and writes no file.

### 8.9 Dependencies and layering

- [ ] No third-party code. Python parses each source file and asserts every import is in the standard library. Java equivalent: no dependencies in the build file, plus a test that scans `import` statements and fails on anything outside `java.*`, `javax.xml.*` and the project's own packages.
- [ ] The core model package imports nothing from the reader or writer packages.
- [ ] The core contains no source-format concepts. Python strips comments and docstrings, then asserts none of `mdsid`, `.xml`, `objectRef`, `ExprTextDesc`, `oracle/bi/server` appears in core code.
- [ ] No code path handles a BAR archive.
- [ ] The skill directory exists as byte-identical copies in `.claude/skills/` and `.agents/skills/`, and no skill markdown hard-codes either path (`test_skill_parity.py`).

---

## 9. The existing as-is skill (`obiee-as-is-analysis`)

Files: `SKILL.md`, `references/document-structure.md`, `customize.toml` (empty hooks: steps to prepend or append, persistent facts, an on-complete action). The skill is a set of instructions for an AI assistant; it contains no code.

### 9.1 Purpose and inputs

It produces one markdown document describing what one subject area does today, beside the CSV slice the document was derived from. It proposes no Power BI design, no DAX and no target schema.

Inputs it expects:

- A project configuration file (`_bmad/bmm/config.yaml`) giving the user's name and languages.
- An extracted KB at `estate/knowledge-base/`, recognised by `manifest.csv`.
- The slicer, run as a local command.
- The user's choice of subject area.

It needs only three capabilities: read a file, write a file, run a local command.

### 9.2 Steps

1. Load the configuration.
2. Check `manifest.csv` exists. If not, tell the user, give the extraction command and stop. Read `extracted_at`, `rpd_ids`, `extractor_version` and the table list from the manifest, never from a directory listing.
3. Report extraction health before any analysis: read `errors.csv` and `unparsed.csv` and state the counts and kinds plainly. A non-zero error count is said before a document is produced.
4. List subject areas with the slicer's listing mode, grouped by rpd-id, and ask which one. Accept a name or a full `row_id`. If the name exists in more than one repository, ask; do not guess. If the user asks for a dashboard or report, explain that the unit of analysis is the subject area and offer the likely ones.
5. Run the slicer into `estate/analysis/<lower-kebab-slug>/`.
6. Read the slice only: `slice-manifest.csv` first, then the tables. For a large subject area read `logical_columns`, `aggregation_rules` and `logical_table_sources` in full and sample the rest.
7. Write `estate/analysis/<slug>/as-is-analysis.md` following the reference structure.
8. State in the document what it could not see, near the top.
9. Hand back: file locations, row counts, constructs found (multi-source measures, time-series functions, aggregation overrides, row-level predicates, fragmentation), what could not be seen, and that the document is plain markdown under version control so edits are the correction record.

Step 4's explanation text says dashboard-level analysis "is not yet supported". The extractor now produces catalog tables, so that text is out of date; the slicer still ignores them.

### 9.3 Rules

- Read the current state from the files; never assume an earlier step or another skill ran.
- Never write to the KB directory. Write only under `estate/analysis/`.
- **Citations.** Every factual statement carries the `row_id` it came from, inline in parentheses or as a trailing table column, consistently. The stated reason: a reviewer can challenge one claim and have that single claim re-derived.
- **Never invent a value.** If the slice does not hold something, say "not recorded". An empty `priority` means not recorded, never lowest. Present-but-empty is not the same as absent, and the document says which.
- Read the slice, not the whole KB, and do not re-derive joins by hand.
- No delegation to sub-agents; the slice exists so the task fits one context.
- Propose no target design.
- Writing standard: direct, short sentences, reference material in tables, formulas verbatim in fenced code blocks, no fact stated twice.

### 9.4 Document structure prescribed by `references/document-structure.md`

Three readers: a human reviewer (verbatim logic and citations), data engineering (grain, filters, joins), an architect (measures, dimensions, security).

| § | Section | Content |
|---|---|---|
| 1 | What this covers, and who it is for | Subject area, rpd-id, extraction timestamp, extractor version; one paragraph on what the subject area is about. |
| 2 | What this document cannot see | Error and unparsed counts for the slice; report-level logic absent; physical detail partial (no DDL, indexes, partitioning, ETL, procedure bodies). Placed before the content. |
| 3 | Measures | One subsection per measure: presentation name and logical column (both ids); formula verbatim in a code block, giving the readable form and noting that text plus refs is authoritative; every aggregation rule with the default marked; time-series function and window; `mapped_source_count` of `source_count`, flagged when they differ. |
| 4 | Attributes and hierarchies | Non-measure columns by presentation table: presentation name, logical column, physical column, data type, rename chain. |
| 5 | Physical objects | Tables with name, schema, type (`table` or `select`), and dependent measures; `select` tables called out separately because the query must be rebuilt; physical joins with columns; business-model joins with their type. |
| 6 | Filters, fragmentation and variables | Per source: content filter verbatim, fragmentation verbatim, priority; variables with initialisation SQL and session or repository scope. |
| 7 | Security | Roles, privileges, row-level predicates and how they connect; a role with no predicate and a predicate reachable from no role are both shown. |
| 8 | Open questions for review | Whatever was recorded but not resolved, and anything ambiguous. Stated as the point of the document, not a blemish. |
| 9 | Provenance | KB path, `extracted_at`, extractor version, slice row counts, the regenerating command. |

### 9.5 What the committed example actually looks like

The example at `estate/analysis/sample-sales/as-is-analysis.md` does **not** follow the prescribed structure. I could not determine which of the two is the intended standard; the example reads as the later, reviewed form. A new skill must choose one explicitly.

The example's shape:

- An opening block: what this is, who it is for, how to read it, and a small table (subject area, repository, date read, "Reports and dashboards: Not examined").
- It is written for business readers in plain language ("Revenue is stored four times", "Currency and language are personal"), not in OBIEE terms.
- Sections: 1 The model in one page; 2 The measures; 3 The dimensions; 4 How the tables relate; 5 Where the data comes from; 6 Behaviour that depends on who is logged in; 7 Security and write-back; 8 What was confirmed, at a glance; 9 Open points; 10 What this document did not look at.
- Appendices: A Formulas exactly as OBIEE stores them (fenced code); B Field-to-column map; C Where this came from, and how to check a statement; D Notes for the tool team.

How it handles the two rules that matter most:

- **Open points.** The body states facts and, where the export does not answer something, says so and points to a numbered open point (`**Open point 1.**`). Section 9 lists them (15 in the example). Each has a bold numbered question, a short statement of what was observed, a line `*Who can answer:*` naming a role, and an empty `*Answer:*` line for the reader to fill in. Section 8 is a status table per area: Complete, Open, Confirmed absent, Not examined.
- **Citations.** Row ids are not attached to each statement. Appendix C gives, per section, the tables used and the `row_id` pattern of the rows behind it, with the KB path, repository, extraction timestamp and extractor version. A dagger marks material read from the full KB because the slice did not contain it. This is weaker than the skill's per-statement rule.
- **Limits.** Stated in the opening table and in section 10, not as a section 2.
- **Tool gaps.** Appendix D lists extractor and slicer gaps and which open point each causes, so that fixing the tool closes the point without asking anyone.

---

## 10. Non-obvious gotchas and decisions

Input layout

1. **Forgetting `--rpd-id` on an estate folder gives a wrong but valid result.** With `--corpus <estate>` (containing `rpd/` and `catalog/`) and no `--rpd-id`, `resolve` treats the subdirectories as estates. `rpd/` is a repository tree, so it becomes an estate whose rpd-id is `rpd`, and `catalog/` is skipped. The run succeeds, every identifier starts `rpd::`, and the catalog is not extracted. No test covers this. Recommendation: refuse or warn in Java.
2. Pairing a repository with a catalog is a declaration (same folder, same rpd-id), never inferred from matching names. The sample catalog is kept as its own estate because none of its subject area names match the sample repository.
3. An invalid folder name stops the run only if that folder is actually an estate.

Honesty mechanics

4. Gaps and errors are two files on purpose. Do not merge them.
5. Unresolved references are written as blank cells plus an error row. A raw GUID is never written to a table.
6. Empty means "not recorded". The one exception is `(none)` in `catalog_versions.xml_version`, because absence there is the most common value.
7. A credential-named variable keeps its row, loses its value, has `redacted = true`, and gets a `redacted_value` gap. Withholding is never silent.
8. `coverage.csv` contains pseudo-types created by errors (2.2). "skipped" under `ExpressionRef` counts broken references, not objects.
9. The summary line `parsed: N objects` counts sub-objects too, and `gaps:` counts rows, not occurrences.
10. `undiscovered_file` rows can carry the wrong rpd-id (1.8).
11. Tail text, comments and processing instructions are outside the read-audit (1.4).
12. The index builder must not count as a reader (1.2). If the Java index goes through the recording accessor, the audit goes quiet exactly where it is needed.

Output

13. The writer never sorts and never converts. Sorting and string rendering belong to the caller. Keeping that split is what makes the determinism tests meaningful.
14. Sort order is by Unicode code point over the whole rendered row (3.3). Java's default string comparison is not identical.
15. Percent rounding is half-to-even (4.4).
16. Space-joined `*_refs` cells are ambiguous because identifiers contain spaces (3.2).
17. Flags copied from the XML are not normalised, so a "boolean" column may hold an empty string.
18. `clean` deletes all top-level CSVs once a manifest is present, not only the ones it wrote (4.6).
19. The timestamp override is unvalidated text; tests rely on that.
20. Each reader fills its own estate, merged afterwards (5.4). Sharing one estate produced 1,507 false duplicates.
21. The identifier-uniqueness check exists because names are not unique in OBIEE: three schemas named `SH` under different databases collapsed 152 identifiers while every test passed. Identifier construction must carry every distinction the source makes.
22. A committed output file was once edited by hand (three stray characters before a header) and no test noticed. Hence the shape and reproduction tests (8.3, 8.1).
23. The decision record's manual spreadsheet verification was never completed. The guard's protection was asserted by design, not by a recorded check.

Slicer and documents

24. The slice is incomplete in known ways (6.5), and the example document had to read the full KB to compensate.
25. The slice's variable match crosses repositories and is a substring match.
26. The two join filters differ: physical needs both ends in scope, business-model needs one.
27. The slice manifest stores the subject area exactly as typed, so the same slice can be recorded under a name or under a `row_id`.
28. The skill's prescribed structure and its only committed output disagree (9.5).
29. The as-is skill's text about dashboards being unsupported is out of date (9.2).

Not determined

- Whether any repository or catalog parser reads an attribute through a path other than the recording accessors or the one documented bypass. I read the audit and its call sites, not every parser line; guides 01 and 02 should confirm.
- The exact `reason` strings and trigger conditions of each repository-side `unresolved_reference` and `missing_name` error (owned by guide 01).
- Which catalog objects `record_version` is called for beyond unmodelled roots (owned by guide 02).
- Whether `Layout.shape` and `Half.declared_by` are meant to reach an output. They do not today.
- Which as-is document form is the intended standard (9.5).
