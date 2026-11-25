# Port guide 02 — Web Catalog reader and report lineage

This guide describes the part of the Python reference implementation that reads an OBIEE Web Catalog (dashboards, dashboard pages, analyses, filters, prompts, views, saved filters) into CSV tables and links analysis columns to the semantic layer. It is written so the behaviour can be re-implemented in Java 11 with the JDK only.

Python names are given in backticks so each claim can be traced: `catalog_xml.py` (the reader), `layout.py` (input layout), `report_lineage.py` (the join to the repository), `model.py` (row types and counters), `identifiers.py` (row identifiers), `observing.py` (the read audit), `knowledge_base.py` and `csv_writer.py` (output), `cli.py` (orchestration).

Numbers quoted as "the fixture" come from one real catalog, SampleApp v607 (5371 files), extracted under estate id `sampleapp-v607`. They show that a construct exists. They say nothing about any other estate.

Where behaviour was read from code but is not pinned by a test or visible in the committed output, the text says "by code reading". Where something could not be determined, it says so.

---

## 0. Pipeline in one page

`cli.extract` runs, in this order:

1. `layout.resolve` decides which halves (`rpd`, `catalog`) exist for which estate id.
2. For each half, the matching adapter loads it into its own in-memory `Estate`; `Estate.merge` adds the rows and counters to the combined one.
3. `report_lineage.resolve` runs once over the merged estate.
4. `Estate.check_identifier_uniqueness` runs once over the merged estate.
5. `knowledge_base.write` writes every table.

`catalog_xml.load(catalog_root, rpd_id)` does, in this order:

1. Fail with `CatalogError` if the root is not a directory. Validate the estate id.
2. `_catalog_files`: list every file, with its decoded catalog path.
3. If there are no files: record one error and return (section 9).
4. `_parse_all`: try to parse every file as XML. Failures are classified by leading bytes, not reported as errors.
5. `_build_index`: index every decoded path (all files, parseable or not) and every ancestor folder.
6. Four passes over the parsed trees, in this fixed order, each visiting trees in file order: dashboards, dashboard pages, analyses, saved filters.
7. `_link_pages_to_dashboards`.
8. `_report_unmodelled`: one row per root element type no pass handles.
9. `_audit`: report every element, attribute and text the passes did not read, in modelled objects only.

Terms used below:

- **estate id** (`rpd_id` in the code and CSV): a lower-kebab name the operator declares. Regex `^[a-z0-9]+(?:-[a-z0-9]+)*$` (`identifiers.validate_rpd_id`).
- **decoded path**: the object's path relative to the catalog root, each segment URL-decoded, joined with `/`.
- **Clark name**: `{namespace-uri}localname`, or just `localname` when the element has no namespace. This is how element names are written to CSV.

---

## 1. Input layout: the catalog folder

### 1.1 What is on disk

A catalog is a directory tree. Folders are catalog folders. Each catalog object is one file **with no extension**. In the fixture, names are lowercase on disk and URL-encoded:

```
01%2e+quickstart/_portal/1%2e00+general+index/dashboard+layout
01%2e+quickstart/_portal/1%2e00+general+index/page+1
demo/_filters/region+filter
05%2e+published+reporting/.../sales+history+report%2exdo/...
```

Conventions seen in the fixture (not enforced or used by the reader):

- A dashboard is a folder `_portal/<dashboard name>/` holding a file named `dashboard layout` (the `sawd:dashboard` object) and one file per page.
- Saved filters live in `_filters/` folders.
- Visual Analyzer projects are files named `_projectdefn`.
- Suffixes such as `.xdo`, `.xdm`, `.png` appear percent-encoded inside names (`%2exdo`), usually on **folder** names. A test against the raw on-disk name for a `.xdo` suffix finds nothing.

Real object files are typically one XML declaration line followed by the whole document on a single line. Namespace declarations may sit on the root or on a child. Examples from the fixture:

```xml
<?xml version="1.0" encoding="utf-8"?>
<sawd:dashboard xmlns:sawd="com.siebel.analytics.web/dashboard/v1.1" style="" xmlVersion="200810300"><sawd:dashboardPageRef path="page 1" type="catalog" hidden="false"/></sawd:dashboard>
```

```xml
<sawd:dashboardPage xmlns:sawd="..." xmlns:saw="..." xmlVersion="200810300" isEmpty="true" duid="2fbiu7rl1vnt5pur"/>
```

```xml
<saw:report xmlns:saw="com.siebel.analytics.web/report/v1.1" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlVersion="201201160" xmlns:sawx="com.siebel.analytics.web/expression/v1.1"><saw:criteria xsi:type="saw:simpleCriteria" subjectArea="&quot;A - Sample Sales&quot;"><saw:columns><saw:column xsi:type="saw:regularColumn" columnID="c585afea602063c88"><saw:columnFormula><sawx:expr xsi:type="sawx:sqlExpression">&quot;Time&quot;.&quot;T05 Per Name Year&quot;</sawx:expr></saw:columnFormula></saw:column></saw:columns></saw:criteria></saw:report>
```

```xml
<sawsf:savedFilterObject xmlns:sawsf="..." xmlns:xsi="..." xmlns:sawx="..." subjectArea="&quot;Y13 - Sports Demo&quot;"><saw:filter xmlns:saw="com.siebel.analytics.web/report/v1.1"><sawx:expr xsi:type="sawx:savedFilter" path="/company_shared/Common Objects/_filters/PC1/All Direct and Star Dims" name="All Direct and Star Dims"/></saw:filter></sawsf:savedFilterObject>
```

**Companion files.** A real catalog stores a binary `<object>.atr` file next to each object (permissions, owner, timestamps). The fixture contains none, and the reader has no special handling for them. By code reading, an `.atr` file would be treated like any other file: it fails to parse as XML, is counted as `not_a_catalog_object` under "unrecognised binary or text", and is added to the path index as an object named `<name>.atr`. This is untested.

**Non-object files.** 1030 of the fixture's 5371 files are not XML: images, PDFs, RTF templates, Office documents, Flash, `data:` URI text. They are legitimate content of the tree.

### 1.2 Discovery (`_catalog_files`)

- Recursively list everything under the catalog root. Keep regular files only. A path whose `is_file` check raises an OS error is skipped silently.
- Skip any file that has a path segment exactly equal to `.git` (`SKIP_DIRS`). Nothing else is skipped: there is no extension filter and dot-files are included (a `.gitignore` would be read and classified as non-XML).
- Result order is the sorted order of the on-disk relative paths. Python sorts `Path` objects **segment by segment**, each segment compared as a string by code point, using the raw (still encoded) names. This is not the same as sorting the full path strings: `a/b` sorts before `a+b` segment-wise, after it string-wise. File order affects a few outputs (sections 3.2, 8.1, 10), so reproduce it.
- Whether symbolic links to directories are followed was not determined; it depends on the Python version's `rglob`. Decide explicitly in the port.

### 1.3 Name decoding (`decode_path`)

Applied to each path segment separately, then segments are joined with `/`:

1. Replace every `+` with a space.
2. Then decode every `%XX` (two hex digits, either case) to a byte, and decode the resulting bytes as UTF-8.

The order matters: `a%2bb` must decode to `a+b`, not `a b`. The rule is general, not a table: `co%6dponents` decodes to `components`. Tests pin these three cases:

| On disk | Decoded |
|---|---|
| `co%6dponents` | `components` |
| `05%2e+published+reporting` | `05. published reporting` |
| `a%2bb` | `a+b` |

Python's `urllib.parse.unquote_plus` leaves a malformed escape such as `%zz` unchanged and replaces invalid UTF-8 with U+FFFD. `java.net.URLDecoder.decode` throws `IllegalArgumentException` on a malformed escape. Write the decoder by hand, or catch and fall back, so a strange file name cannot abort the run.

Case is **not** changed by decoding. Decoded paths are lowercase in the fixture only because the files are lowercase on disk.

### 1.4 Parsing and classification (`_parse_all`)

For each file, in order:

1. Count `files_seen` (census, section 8.3).
2. Parse it as XML.
   - **Parse succeeds**: count `parsed_as_xml`; keep `(decoded path, root element)`.
   - **Parse fails with an XML error**: read the first 16 bytes, classify them (`classify_bytes`), count `not_a_catalog_object`. No error row is written. If reading those 16 bytes fails with an OS error, record an `unreadable` error instead (section 9) and count neither population.
   - **Opening or reading the file fails with an OS error**: record an `unreadable` error; count neither population.
3. After all files: one `unparsed` row per byte class, kind `not_a_catalog_object`, with the number of files in that class (section 9.2).

Byte signatures, tested in this order with "starts with" (`MAGIC`):

| Leading bytes | Class label |
|---|---|
| `89 50 4E 47 0D 0A` (`\x89PNG\r\n`) | `PNG image` |
| `FF D8 FF` | `JPEG image` |
| `GIF87a` | `GIF image` |
| `GIF89a` | `GIF image` |
| `%PDF-` | `PDF document` |
| `{\rtf` | `RTF template` |
| `PK 03 04` | `ZIP container (Office document or archive)` |
| `D0 CF 11 E0` | `OLE compound document (legacy Office)` |
| `CWS` | `Flash movie` |
| `FWS` | `Flash movie` |
| `data:` | `data: URI text` |
| anything else, including an empty file | `unrecognised binary or text` |

Consequences to keep in mind:

- There is no "malformed XML" outcome for the catalog. A truncated or damaged catalog object fails to parse and is counted as `unrecognised binary or text`, exactly like a text file. Tests assert that the error kind `malformed_xml` never appears.
- What counts as "parses as XML" is decided by the XML parser. Python uses expat. A Java parser may accept or reject a different set of edge-case files (unusual encodings, DOCTYPE declarations, XML 1.1). This was not measured. Configure the Java parser to be namespace-aware, to not load external DTDs or entities, and to not validate.

### 1.5 Object type, namespaces, `xsi:type`, `xmlVersion`

**The object type is the root element's namespace URI plus local name. Nothing else.** The prefix is ignored. The file name and folder are ignored.

| Prefix used in this guide | Namespace URI |
|---|---|
| `saw` | `com.siebel.analytics.web/report/v1.1` |
| `sawd` | `com.siebel.analytics.web/dashboard/v1.1` |
| `sawx` | `com.siebel.analytics.web/expression/v1.1` |
| `sawsf` | `com.siebel.analytics.web/savedfilter/v1.1` |
| `xsi` | `http://www.w3.org/2001/XMLSchema-instance` |

Modelled root types (`MODELLED`):

| Root element | Meaning | Parser |
|---|---|---|
| `sawd:dashboard` | Dashboard (layout object listing its pages) | `_parse_dashboard` |
| `sawd:dashboardPage` | Dashboard page | `_parse_dashboard_page` |
| `saw:report` | Analysis (report) | `_parse_analysis` |
| `sawsf:savedFilterObject` | Saved filter | `_parse_saved_filter` |

Every other root type is "unmodelled" (section 9.2). The fixture has 59 root types; 55 are unmodelled.

Rules that follow from matching on namespace + local name:

- `saw:reportRef` and `sawd:reportRef` are different elements. Only `sawd:reportRef` is read.
- A document may declare the namespace as default (no prefix). It is still the same element.
- **All element matching inside objects is also by namespace + local name.**

`xsi:type` is read as the attribute `{http://www.w3.org/2001/XMLSchema-instance}type`. Its **value is a plain string and is compared literally, prefix included**: `saw:regularColumn`, `sawx:eval`, `sawx:savedFilter`. The value's prefix is not resolved to a namespace. A document that bound those namespaces to different prefixes would not match. The fixture also has one occurrence of the 1999 XSI namespace; the reader does not treat it as `xsi:type`.

`xmlVersion` is an un-namespaced attribute on the **root element only**. It is stored as text, never interpreted, and no behaviour depends on it. Absent is stored as the empty string and is the most common state in the fixture (1643 of 4341 parseable roots, including every saved filter and 103 analyses).

### 1.6 What "text" means

Every text value in this guide is the Python `element.text`: the character data between the element's start tag and its **first child element** (or its end tag if it has no children). Comments and processing instructions do not end it. Text after a child element is never read. Entities are resolved. Nothing is trimmed, normalised or unquoted (`observing.read_text`).

Example pinned by a test fixture:

```xml
<sawx:expr xsi:type="sawx:sqlExpression">CAST(<sawx:expr xsi:type="sawx:eval">EVALUATE('OWNER(%1)', "Facts"."Id")</sawx:expr>)</sawx:expr>
```

The outer expression's text is `CAST(`. The closing `)` is lost. Java's DOM `getTextContent()` would instead return `CAST(EVALUATE('OWNER(%1)', "Facts"."Id"))`, which is different. To match the reference, read only the text before the first child element.

For an expression that only contains child expressions, the text is the whitespace between the tags (for example a newline and tabs). It is stored as is.

---

## 2. Estate layout (`layout.py`)

`layout.resolve(corpus_root, rpd_id)` looks only at directory names. It never reads file content.

Constants: RPD marker path `oracle/bi/server/base` (`BASE`); half folder names `rpd` and `catalog`.

### 2.1 Accepted layouts

1. **Estate folder** (needs an estate id): `<corpus>/rpd/...` and/or `<corpus>/catalog/...`.
2. **Bare repository tree** (needs an estate id): `<corpus>/oracle/bi/server/base/...`. RPD half only.
3. **Several estates** (no estate id given): `<corpus>/<estate-id>/...` where each child folder is layout 1 or 2 and the folder name is the estate id.

### 2.2 Rules

`_resolve_one(root, id, declared_by)` for one estate:

1. If `root/rpd` is a directory, add an RPD half rooted at `root/rpd`. If `root/catalog` is a directory, add a catalog half rooted at `root/catalog`. Order is always RPD first. If either was found, stop.
2. Otherwise, if `root/oracle/bi/server/base` is a directory, the result is one RPD half rooted at `root`.
3. Otherwise, no halves.

Points to note:

- `rpd/` is accepted as the RPD half **only because it is a directory with that name**. Step 1 does not check for `oracle/bi/server/base` inside it.
- `catalog/` is accepted the same way. A test pins that a `catalog/` folder containing only `readme.txt` is still the catalog half. The reader then reports what it could not read; the folder is never reclassified.
- Either half may be absent. `Layout.halves_of(id)` returns the present ones in the fixed order `[rpd, catalog]`. `is_paired(id)` is true only when both are present.
- Each half records how it was declared (`declared_by`): `"--rpd-id with --corpus"` or `"the folder name under --corpus"`.

`resolve` with an estate id:

1. `corpus_root` not a directory → `LayoutError`.
2. Validate the id (`IdentifierError` if not lower-kebab).
3. `_resolve_one(corpus_root, id, ...)`. Halves found → shape `estate` if `rpd/` or `catalog/` exists, else `repository`. None → `LayoutError`.

`resolve` without an estate id:

1. `corpus_root` not a directory → `LayoutError`.
2. List child directories, sorted. A listing failure → `LayoutError`.
3. For each child, `_resolve_one(child, child name, ...)`. A child with no halves is **skipped silently**. A child with halves must have a lower-kebab name, otherwise `IdentifierError` aborts the run. The name is validated only after the child has proved to be an estate, so an unrelated sibling folder with an odd name does not fail the run.
4. No halves at all → `LayoutError`. Otherwise shape `multi`.

A consequence, by code reading and not covered by a test: pointing at an estate folder (one that itself contains `rpd/` and `catalog/`) **without** an estate id runs the multi-estate branch. The child `rpd` is accepted as an estate named `rpd` if it contains `oracle/bi/server/base`, and the child `catalog` is skipped unless it happens to contain a folder named `rpd` or `catalog`. The catalog is dropped with no message.

### 2.3 What happens when a half is absent

- **No catalog half**: the catalog reader is not called. In the Python tool all catalog tables are still written, with a header and no rows. `report_lineage` is empty.
- **No RPD half**: the catalog is read normally. `report_lineage` still gets one row per analysis column; every bare reference is `unresolved` and `subject_area_id` is empty (this is the state of the committed fixture output: 5806 `unresolved`, 4586 `not_a_column_reference`, 751 `outside_semantic_model`). No error rows are written for those.
- **`catalog/` present but empty**: the half counts as present. The reader records one `unreadable` error (section 9) and returns no rows.
- The manifest records, per estate id, `halves_present:<id>` (`rpd`, `catalog`, `rpd catalog`) and `pairing:<id>` (`knowledge_base._halves_rows`). Pairing is recorded as declared by the operator. It is never inferred from matching names.

### 2.4 Error messages

Every `LayoutError` ends with this block (`ACCEPTED`), verbatim:

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

A BAR archive is not a layout. Unzip it, put content/catalog/ into catalog/,
and supply the repository as MDS XML in rpd/ — a BAR carries a binary .rpd,
which is out of scope. See README.md.
```

| Situation | Message (before the block above) |
|---|---|
| Corpus path is not a directory | `<path> is not a directory.` |
| Child listing fails | `cannot read <path>: <os error>` |
| Nothing matched, and the path is named `base` or has a child directory containing `*.xml` files (`_looks_like_base_dir`) | `<path> looks like the 'base' directory itself. --corpus wants the directory CONTAINING 'oracle/'.` then `       Try:  --corpus <path four levels up>`, then the generic line |
| Nothing matched, no id given, and the path itself contains `oracle/bi/server/base` | `<path> is itself a repository tree, so it needs a name. Add --rpd-id.` then the generic line |
| Nothing matched, id given | `--rpd-id was given, so <path> must hold rpd/, catalog/, or oracle/bi/server/base/ — it holds none of them.` then the generic line |
| Generic line, always present on a no-match | `<path> matches no accepted layout.` |

The three hints are tested in that order and only the first that applies is used (`_no_match_message`).

Other fatal messages:

- Bad estate id (`identifiers.validate_rpd_id`): `rpd-id '<id>' is not lower-kebab. Use e.g. 'my-estate'. ...`
- Catalog root not a directory (`catalog_xml.load`): `catalog root <path> is not a directory`.

Process exit codes (`cli.main`): `2` for a layout, identifier or catalog-root error (nothing is written); `3` when the run completed but at least one error row exists; `0` otherwise. The layout is resolved before any output is deleted or written.

---

## 3. What is read from each object type

In the tables below, "descendant" means any depth below the element, in document order, not including the element itself. "Direct child" means exactly one level down.

All row identifiers start with `<estate-id>::cat::` (section 4.1). `P` stands for the object's decoded path.

### 3.1 Dashboard — root `sawd:dashboard` (`_parse_dashboard`)

| Source | Feeds |
|---|---|
| Decoded path `P` | `dashboards.row_id`, `dashboards.catalog_path` |
| Last segment of the **folder containing** the file. If the file is at the top level, the file name. | `dashboards.name` |
| Root `@xmlVersion` | `dashboards.xml_version`; also `catalog_versions` |
| Every descendant `sawd:dashboardPageRef`, in document order, index `n` from 0 | one `dashboard_contents` row, id `P#page<n>`, `position` = `n` |
| `sawd:dashboardPageRef/@path` | `dashboard_contents.reference` (verbatim), then resolved (section 5) into `resolution`, `target_id`, `target_kind`, `detail` |
| Count of `sawd:dashboardPageRef` | `dashboards.page_count` (includes references that did not resolve) |

The name rule exists because every dashboard file in the fixture is called `dashboard layout`; the name is the folder. `sawd:dashboardPageGroup` grouping, the group caption, `@hidden` and `@type` on the reference are not read.

### 3.2 Dashboard page — root `sawd:dashboardPage` (`_parse_dashboard_page`)

| Source | Feeds |
|---|---|
| Decoded path `P` | `dashboard_pages.row_id`, `catalog_path` |
| Last path segment | `dashboard_pages.name` |
| Root `@xmlVersion` | `dashboard_pages.xml_version`; `catalog_versions` |
| Descendant `sawd:reportRef/@path` | `dashboard_contents` row, expected target kind `object` |
| Descendant `sawd:globalFilterView/@path` | `dashboard_contents` row, expected `object`; **and** an `analysis_filters` row with `scope` = `dashboard` |
| Descendant `sawd:scorecardViewRef/@path` | `dashboard_contents` row, expected `object` |
| Descendant `sawd:folderView/@path` | `dashboard_contents` row, expected `folder` |

`dashboard_contents` rows for a page:

- The four element types are processed **in the order listed above**, each in document order. One counter runs across all four. The row id is `P#item<n>` and `position` = `n`.
- So `position` is "all report references first, then all global filter views, then scorecard views, then folder views". It is **not** the layout order on the page.

`analysis_filters` rows for a page (dashboard scope):

- A second, separate loop over `sawd:globalFilterView`, index `k` from 0. Row id `P#gfilter<k>`, `scope` = `dashboard`, `owner_id` = the page's row id, `position` = `k`, `reference` = `@path`, `target_id` and `resolution` from the resolver. `op`, `expr_type`, `expr_text` are empty; `uses_eval` is `false`.
- The reference is resolved a second time here. If it fails, **a second identical error row is written** (the fixture has 386 unresolved global filter views and 772 error rows for them).

`dashboard_id` and `position` on `dashboard_pages` are filled afterwards by `_link_pages_to_dashboards`:

- Walk all `dashboard_contents` rows in the order they were created. Consider only rows whose `element` is `sawd:dashboardPageRef` and whose `target_id` is non-empty.
- The **first** such row that targets a page sets that page's `dashboard_id` (the row's `container_id`) and `position` (the row's `position`). Later references to the same page are ignored.
- A page that no dashboard references keeps `dashboard_id` empty and `position` 0. The fixture has 6. Position 0 with an empty `dashboard_id` does not mean "first page".
- The owning dashboard is never inferred from the folder the page sits in.

Not read on a page: sections, columns, HTML views, images, links, KPIs, `sawd:reportView` attributes (name, display, showView), and everything else. These show up in `unparsed` (section 9.2).

### 3.3 Analysis — root `saw:report` (`_parse_analysis`)

| Source | Feeds |
|---|---|
| Decoded path `P` | `analyses.row_id`, `catalog_path` |
| Last path segment | `analyses.name` (for a Visual Analyzer project this is `_projectdefn`) |
| Root `@xmlVersion` | `analyses.xml_version`; `catalog_versions` |
| Number of `saw:criteria` elements anywhere in the document | `analyses.criteria_count` |
| Total columns captured across all criteria | `analyses.column_count` |

Then criteria, columns, filters, prompts and views as below.

### 3.4 Criteria — every `saw:criteria` in the analysis

All `saw:criteria` elements anywhere under the root, **including nested ones**, in document order, index `i` from 0.

| Source | Feeds |
|---|---|
| index `i` | `analysis_criteria.row_id` = `P#criteria<i>`, `position` = `i` |
| the analysis | `analysis_criteria.analysis_id` |
| `saw:criteria/@subjectArea` | `analysis_criteria.subject_area`, verbatim including its quotes; empty if absent |

Facts from the fixture: 1789 criteria in 1607 analyses; 182 are nested inside another criteria (a set operation such as a union wraps each operand in its own criteria); 109 have no `subjectArea`. `@xsi:type` on the criteria (`saw:simpleCriteria` etc.) is not read.

### 3.5 Ownership: nearest ancestor (`_owned_by_nearest`)

Because criteria nest, "the columns of a criteria" cannot be "all `saw:column` descendants". The rule:

- For every element of the wanted type anywhere in the document, walk up its ancestors. The **first** ancestor of the owner type is its owner. An element with no such ancestor has no owner and is ignored.
- The owned elements of one owner keep document order.

It is used three times:

| Owner | Wanted | Result |
|---|---|---|
| `saw:criteria` | `saw:column` | the columns of each criteria |
| `saw:criteria` | `saw:filter` | the filter holders of each criteria |
| `saw:column` | `saw:columnFormula` | the formula elements of each column |

Effects:

- A `saw:column` outside every criteria (for example inside a view) is not captured. The fixture has 14,429 `saw:column` elements in analyses and 11,143 captured columns.
- A `saw:columnFormula` at any depth inside a column belongs to that column, not only a direct child (72 in the fixture sit deeper).
- A `saw:columnFormula` whose nearest `saw:column` ancestor does not exist (132 in the fixture, inside filters) belongs to no column. Its expression is captured as a filter expression instead (section 3.7).

In Java, build a parent map once per document, or carry a parent pointer in your own node class.

### 3.6 Columns (`_parse_columns`)

For criteria index `i`, its owned columns in document order, index `j` from 0:

| Source | Feeds |
|---|---|
| indexes | `analysis_columns.row_id` = `P#c<i>.<j>`, `position` = `j` |
| owner | `analysis_id`, `criteria_id` |
| `saw:column/@columnID` | `column_id` |
| Direct child `saw:columnHeading`, then its direct child `saw:caption`, then its direct child `saw:text`: text | `heading` |
| Direct child `saw:tableHeading` → `saw:caption` → `saw:text`: text | `table_heading` |
| The column's **first** owned `saw:columnFormula` exists | `formula_construct` = the Clark name `{com.siebel.analytics.web/report/v1.1}columnFormula`; otherwise empty |
| That formula's **direct child** `sawx:expr`: `@xsi:type` | `formula_type` |
| That same `sawx:expr`: text (section 1.6) | `formula` |
| `saw:column/@xsi:type` | `is_computation` (section 7.1). The type itself is not stored. |
| Any `sawx:expr` with `xsi:type` = `sawx:eval` under **any** of the column's owned formulas | `uses_eval` |

If the column has more than one owned formula (2 columns in the fixture), only the first feeds `formula`. The others are visible only through the audit (their text is reported as unread).

If the first formula has no direct child `sawx:expr`, `formula_type` and `formula` are empty but `formula_construct` is still set.

### 3.7 Filters inside an analysis (`_parse_filters`)

For criteria index `i`: take its owned `saw:filter` elements in document order. For each, take **every descendant `sawx:expr`** in document order. One counter `k` runs across all filter holders of the criteria.

| Source | Feeds |
|---|---|
| indexes | `analysis_filters.row_id` = `P#f<i>.<k>`, `position` = `k` |
| constant | `scope` = `analysis` |
| the analysis (not the criteria) | `owner_id` |
| `sawx:expr/@op` | `op` |
| `sawx:expr/@xsi:type` | `expr_type` |
| `sawx:expr` text (section 1.6) | `expr_text` |
| `sawx:expr/@path` | `reference` (any expression type) |
| If `xsi:type` = `sawx:savedFilter` **and** `@path` is non-empty: resolve the path (section 5) | `target_id`, `resolution`. Otherwise both empty. |
| This expression or any expression below it has `xsi:type` = `sawx:eval` | `uses_eval` |

So a filter tree is flattened: one row per expression node, pre-order. See section 7.3.

### 3.8 Prompts inside an analysis (`_parse_prompts`)

All descendant `saw:prompt` elements of the root, in document order, index `m` from 0. An element with no `xsi:type` is **skipped** (it is a plain text parameter, not a prompt) but still consumes its index, so `p` numbers can have gaps.

| Source | Feeds |
|---|---|
| index | `analysis_prompts.row_id` = `P#p<m>`, `position` = `m` |
| `saw:prompt/@xsi:type` | `prompt_type` |
| Direct child `saw:formula` → direct child `sawx:expr`: `@xsi:type` | `column_type` |
| Same `sawx:expr`: text | `column_formula` |
| Direct child `saw:promptOperator/@op` | `operator` |
| Direct child `saw:promptUIControl/@xsi:type` | `ui_control` |
| Every descendant `saw:setPromptVariable/@variableFormula`, non-empty values joined with ` | ` (space, pipe, space) | `set_variable` |

The fixture yields only 4 rows (section 11).

### 3.9 Views inside an analysis (`_parse_views`)

All `saw:view` elements anywhere under the root (any depth, document order), index `v` from 0.

| Source | Feeds |
|---|---|
| index | `analysis_views.row_id` = `P#view<v>`, `position` = `v` |
| `saw:view/@xsi:type`; if absent or empty, the literal `(no xsi:type)` | `view_type` |
| `saw:view/@name` | `name` |
| Number of descendant `saw:cvCell` elements with a non-empty `@viewName` | `references_views` |
| Number of descendant elements of any name with a non-empty un-namespaced `@columnID` | `bound_column_count` |
| **Direct child** `saw:display/@type` | `display_type` |
| Direct child `saw:display/@subtype` | `display_subtype` |

`saw:views/@currentView` is read and discarded (it is read only so the audit does not report it).

### 3.10 Saved filter — root `sawsf:savedFilterObject` (`_parse_saved_filter`)

| Source | Feeds |
|---|---|
| Decoded path `P` | `saved_filters.row_id`, `catalog_path` |
| Last path segment | `name` |
| Root `@xmlVersion` | `xml_version` (always empty in the fixture); `catalog_versions` |
| Direct child `saw:filter` → its **first direct child** `sawx:expr`: `@op` | `op` |
| Same expression: `@xsi:type` | `expr_type` |
| Same expression: text | `expr_text` |
| Any `sawx:expr` with `xsi:type` = `sawx:eval` anywhere under the `saw:filter` | `uses_eval` |

**Only the outermost expression is captured.** Unlike analysis filters, the tree is not flattened into rows. In the fixture 88 of 96 saved filters have an outermost `sawx:logical` / `and`, whose text is whitespace, so the table holds almost none of the actual predicates. The nested expressions appear only in `unparsed` (`sawx:expr#text` 579, `sawx:expr@op` 564, `sawx:expr@path` 76 for this object type). The root's `@subjectArea` is not read (reported unread, 96). A saved filter that references another saved filter by `@path` is not resolved.

---

## 4. Output tables

### 4.1 Common rules

**Row identifier** (`identifiers.make_id`): `<estate-id>::<layer>::<object-path>`. The layer for everything in this guide is `cat`. For a catalog object the object path is the whole decoded path passed as **one** part, so `/` and `.` inside names survive (`05. published reporting/...`). Sub-rows append `#<suffix>` to the path.

- `make_id` refuses an empty part and refuses a part containing `::`. By code reading, a decoded file path containing `::` raises `IdentifierError`, which aborts the whole run with exit code 2. Decide whether the port should record and continue instead.
- `identifiers.parse_id` splits on `::` and requires exactly three pieces.

**Sorting.** Each table is written with its rows sorted by comparing the row's cells left to right as strings, by code point (`sorted()` over lists of strings in `knowledge_base.write`). Since `row_id` is first and unique, this is a plain string sort on `row_id`: `#view10` comes before `#view2`. Java's `String.compareTo` compares UTF-16 units, which differs from code point order only for characters outside the BMP.

**Encoding.** UTF-8, comma delimiter, LF line endings, one header row, **every field quoted**, quotes doubled. Line breaks inside values are kept inside the quoted field. Booleans are `true` / `false`. Integers are decimal strings.

**The `_guarded` columns.** The Python writer adds, after some columns, a companion `<column>_guarded` boolean, and prefixes the value with one apostrophe when a spreadsheet would misread it (value starts with `=`, `+`, `-`, `@`, tab, CR or LF, or looks like a number a spreadsheet would mangle). The new project drops the companion columns, so they are omitted below. Be aware that in the committed reference CSVs a value whose companion is `true` carries a leading apostrophe that is **not** part of the source value (for example an `expr_text` shown as `'` followed by a newline is really just whitespace). All examples in this guide show the unguarded value.

**Reference lists** in the tables below use these names for the path-resolution outcome: `exact`, `case_normalised`, `ambiguous`, `unresolved` (section 5).

### 4.2 `dashboards`

| # | Column | Holds |
|---|---|---|
| 1 | `row_id` | `<id>::cat::<decoded path of the dashboard layout file>` |
| 2 | `rpd_id` | estate id |
| 3 | `name` | name of the containing folder (section 3.1) |
| 4 | `catalog_path` | decoded path |
| 5 | `xml_version` | root `xmlVersion`, empty if absent |
| 6 | `page_count` | number of page references |

Examples:

- `sampleapp-v607::cat::00. va projects/_portal/git test dashboard/dashboard layout` (name `git test dashboard`, 1 page)
- `sampleapp-v607::cat::01. quickstart/_portal/1.00 general index/dashboard layout` (name `1.00 general index`, 5 pages)

### 4.3 `dashboard_pages`

| # | Column | Holds |
|---|---|---|
| 1 | `row_id` | `<id>::cat::<decoded path of the page>` |
| 2 | `rpd_id` | estate id |
| 3 | `dashboard_id` | `row_id` of the first dashboard that references this page; empty if none |
| 4 | `position` | that reference's position in the dashboard; 0 if none |
| 5 | `name` | last path segment |
| 6 | `catalog_path` | decoded path |
| 7 | `xml_version` | root `xmlVersion` |

Examples:

- `sampleapp-v607::cat::00. va projects/_portal/git test dashboard/page 1` (position 0)
- `sampleapp-v607::cat::01. quickstart/_portal/1.00 general index/how catalog content is organized` (position 2)

### 4.4 `dashboard_contents`

One row per reference placed on a dashboard or a page.

| # | Column | Holds |
|---|---|---|
| 1 | `row_id` | dashboard: `<container path>#page<n>`; page: `<container path>#item<n>` |
| 2 | `rpd_id` | estate id |
| 3 | `container_id` | `row_id` of the dashboard or page holding the reference |
| 4 | `position` | `n` (sections 3.1, 3.2) |
| 5 | `element` | Clark name of the referencing element, e.g. `{com.siebel.analytics.web/dashboard/v1.1}reportRef` |
| 6 | `reference` | the `@path` value, verbatim, never repaired |
| 7 | `resolution` | `exact`, `case_normalised`, `ambiguous`, `unresolved` |
| 8 | `target_id` | `row_id`-style identifier of the target path; empty when not resolved |
| 9 | `target_kind` | `object`, `folder`, or empty when not resolved |
| 10 | `detail` | empty, or a reason (section 5.4) |

Examples:

- `sampleapp-v607::cat::00. va projects/_portal/git test dashboard/dashboard layout#page0` — `dashboardPageRef`, reference `page 1`, `exact`, target `...git test dashboard/page 1`, `object`.
- `sampleapp-v607::cat::00. va projects/_portal/git test dashboard/page 1#item0` — `reportRef`, reference `/shared/GIT_TEST_DASH/GIT_TEST_ANALYSES/Revenues by Product Type`, `case_normalised`, target `sampleapp-v607::cat::git_test_dash/git_test_analyses/revenues by product type`.
- `sampleapp-v607::cat::01. quickstart/_portal/1.00 general index/how catalog content is organized#item0` — `folderView`, reference `/shared/00. VA Projects`, `case_normalised`, target `sampleapp-v607::cat::00. va projects`, `folder`.

### 4.5 `analyses`

| # | Column | Holds |
|---|---|---|
| 1 | `row_id` | `<id>::cat::<decoded path>` |
| 2 | `rpd_id` | estate id |
| 3 | `name` | last path segment |
| 4 | `catalog_path` | decoded path |
| 5 | `xml_version` | root `xmlVersion` |
| 6 | `criteria_count` | number of `saw:criteria` |
| 7 | `column_count` | number of `analysis_columns` rows for this analysis |

Examples:

- `sampleapp-v607::cat::01. quickstart/build_607`
- `sampleapp-v607::cat::02. visualizations/google charts/google sankey` (4 criteria, 12 columns)
- `sampleapp-v607::cat::00. va projects/advanced analytics/aa - binning i/_projectdefn` (a Visual Analyzer project; name `_projectdefn`, no version)

### 4.6 `analysis_criteria`

| # | Column | Holds |
|---|---|---|
| 1 | `row_id` | `<analysis path>#criteria<i>` |
| 2 | `rpd_id` | estate id |
| 3 | `analysis_id` | owning analysis |
| 4 | `position` | `i` |
| 5 | `subject_area` | `@subjectArea` verbatim, quotes included |

Examples:

- `sampleapp-v607::cat::00. va projects/advanced analytics/aa - binning i/_projectdefn#criteria0` — `"A - Sample Sales"` (the quotes are part of the value)
- `sampleapp-v607::cat::00. va projects/advanced analytics/aa - binning : lottery payouts over time/_projectdefn#criteria0` — `XSA('prodney'.'US 5Y Florida lotto')`
- `sampleapp-v607::cat::02. visualizations/google charts/google sankey#criteria0` — empty (outer block of a set operation); `#criteria2` and `#criteria3` hold `"A - Sample Sales"`.

### 4.7 `analysis_columns`

| # | Column | Holds |
|---|---|---|
| 1 | `row_id` | `<analysis path>#c<i>.<j>` (criteria index, column index within that criteria) |
| 2 | `rpd_id` | estate id |
| 3 | `analysis_id` | owning analysis |
| 4 | `criteria_id` | owning criteria row |
| 5 | `position` | `j` |
| 6 | `column_id` | `@columnID` |
| 7 | `heading` | custom column heading text, if any |
| 8 | `table_heading` | custom table heading text, if any |
| 9 | `formula_construct` | Clark name of the element that carried the formula, or empty |
| 10 | `formula_type` | `xsi:type` of the formula's expression |
| 11 | `formula` | the expression text, verbatim |
| 12 | `is_computation` | section 7.1 |
| 13 | `uses_eval` | section 7.1 |

Examples:

- `sampleapp-v607::cat::00. va projects/advanced analytics/aa - binning : lottery payouts over time/_projectdefn#c0.0` — column id `Day`, type `sawx:sqlExpression`, formula `XSA('prodney'.'US 5Y Florida lotto')."Columns"."Day #"`.
- `sampleapp-v607::cat::02. visualizations/google charts/google sankey#c2.1` — formula `"Offices"."D4  Company"` (two spaces, kept).
- `sampleapp-v607::cat::02. visualizations/google charts/google sankey#c0.0` — column of the outer block: heading `IN`, no formula, `is_computation` = `true`.

### 4.8 `analysis_views`

| # | Column | Holds |
|---|---|---|
| 1 | `row_id` | `<analysis path>#view<v>` |
| 2 | `rpd_id` | estate id |
| 3 | `analysis_id` | owning analysis |
| 4 | `position` | `v` |
| 5 | `view_type` | `xsi:type` verbatim, or `(no xsi:type)` |
| 6 | `name` | `@name`, e.g. `compoundView!1` |
| 7 | `references_views` | count (section 3.9) |
| 8 | `bound_column_count` | count (section 3.9) |
| 9 | `display_type` | e.g. `bar`, `line`, `pie`; empty for most view types |
| 10 | `display_subtype` | e.g. `stacked`, `default`, `horizontal` |

Examples (analysis `02. visualizations/google charts/google sankey`):

- `...google sankey#view0` — `saw:compoundView`, `compoundView!1`, references 3, bound 0.
- `...google sankey#view2` — `saw:tableView`, `tableView!1`, references 0, bound 5.
- `...aa - binning : lottery payouts over time/_projectdefn#view1` — `saw:pluginView`, `view!1`, bound 4.

### 4.9 `analysis_filters`

Holds two populations, distinguished by `scope`.

| # | Column | Holds |
|---|---|---|
| 1 | `row_id` | analysis scope: `<analysis path>#f<i>.<k>`; dashboard scope: `<page path>#gfilter<k>` |
| 2 | `rpd_id` | estate id |
| 3 | `scope` | `analysis` or `dashboard` |
| 4 | `owner_id` | the analysis `row_id`, or the page `row_id` |
| 5 | `position` | `k` |
| 6 | `op` | `@op` verbatim; empty for dashboard scope |
| 7 | `expr_type` | `xsi:type` verbatim; empty for dashboard scope |
| 8 | `expr_text` | expression text; empty for dashboard scope |
| 9 | `reference` | `@path` verbatim |
| 10 | `target_id` | resolved target, or empty |
| 11 | `resolution` | resolver outcome; **empty** when no resolution was attempted |
| 12 | `uses_eval` | section 7.1 |

Examples:

- `sampleapp-v607::cat::01. quickstart/_portal/1.10 whats new/apex integration#gfilter0` — `dashboard`, reference `/shared/06. Dashboard Design/Commentary/Apex Prompt`, `case_normalised`.
- `sampleapp-v607::cat::01. quickstart/new features/11.1.1.7.1/line geometries thickness#f0.0` — `analysis`, op `and`, type `sawx:logical`, text is whitespace.
- `...line geometries thickness#f0.1` — type `sawx:savedFilter`, reference `/shared/01. QuickStart/_filters/X - Airlines Delay/Prt Filters - All Dims`, `case_normalised`.

### 4.10 `analysis_prompts`

| # | Column | Holds |
|---|---|---|
| 1 | `row_id` | `<analysis path>#p<m>` |
| 2 | `rpd_id` | estate id |
| 3 | `analysis_id` | owning analysis |
| 4 | `position` | `m` |
| 5 | `prompt_type` | `xsi:type` of the prompt, e.g. `saw:columnFilterPrompt` |
| 6 | `column_type` | `xsi:type` of the prompt's formula expression |
| 7 | `column_formula` | formula text, e.g. `"Offices"."D1  Office"` |
| 8 | `set_variable` | variables the prompt sets, ` | `-joined |
| 9 | `operator` | e.g. `in` |
| 10 | `ui_control` | e.g. `saw:browse` |

Examples (all four fixture rows are of this shape):

- `sampleapp-v607::cat::02. visualizations/smart view/heatmap using conditional formatting#p0`
- `sampleapp-v607::cat::02. visualizations/smart view/p&l by time - sv#p1`

### 4.11 `saved_filters`

| # | Column | Holds |
|---|---|---|
| 1 | `row_id` | `<id>::cat::<decoded path>` |
| 2 | `rpd_id` | estate id |
| 3 | `name` | last path segment |
| 4 | `catalog_path` | decoded path |
| 5 | `xml_version` | root `xmlVersion` |
| 6 | `op` | outermost expression's `@op` |
| 7 | `expr_type` | outermost expression's `xsi:type` |
| 8 | `expr_text` | outermost expression's text |
| 9 | `uses_eval` | section 7.1 |

Examples:

- `sampleapp-v607::cat::01. quickstart/_filters/a - sample sales/short prompted filters` — `and`, `sawx:logical`.
- `sampleapp-v607::cat::11. demos/_filters/pc1/pred timespan` — `in`, `sawx:list`, `uses_eval` = `true`.

### 4.12 `report_lineage`

One row per `analysis_columns` row. Section 6.

| # | Column | Holds |
|---|---|---|
| 1 | `row_id` | `<analysis path>#c<i>.<j>#lineage` |
| 2 | `rpd_id` | estate id |
| 3 | `analysis_id` | from the column |
| 4 | `criteria_id` | from the column |
| 5 | `analysis_column_id` | the column's `row_id` |
| 6 | `subject_area` | the criteria's `subject_area`, verbatim with quotes |
| 7 | `subject_area_id` | `row_id` of the matched subject area, or empty |
| 8 | `reference_text` | the column's `formula`, verbatim (not trimmed) |
| 9 | `presentation_column_id` | `row_id` of the matched presentation column, or empty |
| 10 | `resolution` | `exact`, `case_normalised`, `unresolved`, `not_a_column_reference`, `outside_semantic_model` |
| 11 | `logical_column_id` | from repository lineage, or empty |
| 12 | `physical_column_id` | from repository lineage, or empty |
| 13 | `source_state` | `sourced`, `computed_not_sourced`, or empty |

Examples:

- `sampleapp-v607::cat::01. quickstart/build_607#c0.0#lineage` — subject area `"A - Sample Sales"`, reference `"Time"."T05 Per Name Year"`, `unresolved` (the fixture has no matching repository).
- `sampleapp-v607::cat::00. va projects/advanced analytics/aa - binning : lottery payouts over time/_projectdefn#c0.0#lineage` — reference `XSA('prodney'.'US 5Y Florida lotto')."Columns"."Day #"`, `outside_semantic_model`.
- `...lottery payouts over time/_projectdefn#c0.11#lineage` — reference starting `BIN(XSA('prodney'...`, `not_a_column_reference`.

### 4.13 `catalog_versions`, `coverage`, `coverage_summary`

Section 8. `errors` and `unparsed`: section 9.

---

## 5. Path reference resolution

Catalog objects refer to each other by path string, not by id. Resolution is string work over the decoded paths found on disk.

### 5.1 The index (`Index`, `_build_index`)

Built from **every** file found, parseable or not.

- For each decoded file path: register it with kind `object`. Then register every proper ancestor prefix (`a`, `a/b`, ... ) with kind `folder`.
- A path already registered is left unchanged (first registration wins).
- A second map goes from the lowercased path to the list of registered paths with that lowercase form.
- Folders that contain no file anywhere beneath them are not indexed.

Folders are indexed because `sawd:folderView` targets a folder. Non-XML files are indexed because a reference may legitimately point at one.

### 5.2 Normalising a reference (`normalise_reference(reference, owner)`)

`owner` is the decoded path of the object that contains the reference. Three rules, in order:

1. Reference starts with `/shared/` → remove that prefix. **The catalog root on disk is assumed to be the content of `/shared`.**
2. Reference starts with `/` (anything else) → out of tree. The result is the empty string.
3. Otherwise it is relative to the owner's folder: `<owner folder>/<reference>`, or just `<reference>` if the owner is at the top level.

Nothing else is done. The reference is not URL-decoded, not trimmed, not case-changed. `.` and `..`, doubled or trailing slashes, and escaped separators are not interpreted. The fixture contains one reference with an escaped separator inside a name (`.../m\/d linking_mob`); it is kept verbatim and does not resolve.

By code reading: a reference equal to `/shared` (no trailing slash) is out of tree. A missing `@path` gives an empty reference, which becomes `<owner folder>/` and fails to resolve (or, for a top-level owner, is treated as out of tree). If an operator puts a folder named `shared` inside `catalog/`, nothing resolves, because the prefix is stripped.

### 5.3 Lookup (`Index.lookup`)

For a non-empty normalised path:

1. Registered exactly → `exact`. An exact match wins even if other paths differ only by case.
2. Otherwise look up the lowercased path. Exactly one registered path → `case_normalised`, target = that path.
3. More than one → `ambiguous`. No target is chosen.
4. None → `unresolved`.

Lowercasing is Python `str.lower()`. In Java use `toLowerCase(Locale.ROOT)`.

Case folding matters: in the fixture, exact matching resolves 11 of 3718 dashboard content references and case folding resolves 2717. The disk tree is lowercase; references keep display case.

### 5.4 Outcomes (`Resolver.resolve`)

| `resolution` | When | `target_id` | `target_kind` | `detail` | Error row |
|---|---|---|---|---|---|
| `exact` | normalised path is registered as written | `<id>::cat::<target path>` | `object` or `folder` | empty, or a kind mismatch note | none |
| `case_normalised` | exactly one registered path matches ignoring case | same | same | same | none |
| `ambiguous` | two or more registered paths match ignoring case and none exactly | empty | empty | `two paths differ only in case: <a> | <b>` (sorted, joined with ` | `) | kind `ambiguous_reference` |
| `unresolved` | reference is under another root | empty | empty | `names a catalog root this tree does not contain` | kind `unresolved_reference` |
| `unresolved` | no registered path matches | empty | empty | `no object or folder at this path` | kind `unresolved_reference` |

`target_kind` says only whether the target path is a file (`object`) or a folder (`folder`). It does **not** say what type of catalog object the file is. A `reportRef` that resolves to a PNG or to an unmodelled object type is still `object`. To know whether a target is an analysis, join `target_id` to `analyses.row_id`.

Kind mismatch: each referencing element has an expected kind (`folder` for `sawd:folderView`, `object` for everything else). If the reference resolves to the other kind, the resolution still stands, no error is written, and `detail` is `expected a catalog <expected>, resolved to a <actual>`. The fixture has no such case.

### 5.5 Where resolution is used

| Reference | Row written | Notes |
|---|---|---|
| `sawd:dashboardPageRef/@path` in a dashboard | `dashboard_contents` | Usually relative (`page 1`), so it resolves against the dashboard's own folder |
| `sawd:reportRef/@path` on a page | `dashboard_contents` | The page-to-analysis link. Join `target_id` to `analyses.row_id` |
| `sawd:globalFilterView/@path` on a page | `dashboard_contents` and `analysis_filters` (scope `dashboard`) | Resolved twice. In the fixture the targets are named like prompts (`.../Apex Prompt`) and appear to be `saw:view` prompt objects, which are not modelled, so `target_id` generally joins to no modelled table. This was inferred from names and the format notes, not verified row by row |
| `sawd:scorecardViewRef/@path` on a page | `dashboard_contents` | Target type is unmodelled |
| `sawd:folderView/@path` on a page | `dashboard_contents` | Expected kind `folder` |
| `sawx:expr[@xsi:type='sawx:savedFilter']/@path` in an analysis filter | `analysis_filters` (scope `analysis`) | Join `target_id` to `saved_filters.row_id`. `target_kind` and `detail` are discarded here, so a reference landing on a folder looks resolved |

A saved-filter reference is recognised by the `xsi:type` value on an ordinary `sawx:expr`. There is no element named `savedFilter`; searching by element name finds nothing.

Other elements that carry `@path` in the fixture (`saw:reportRef`, `saw:catalogColumnRef`, `sawbsc:perspectiveRef`, `cond:conditionRef`, BI Publisher font paths and others) are not resolved.

Fixture figures: 1161 references point into `/company_shared/`, a second catalog root the export does not contain. A partial export is a normal input, which is why these are recorded and never fatal.

---

## 6. Report lineage (`report_lineage.resolve`)

A pass over the merged in-memory estate. It reads only rows already extracted: `analysis_columns`, `analysis_criteria` from the catalog; `presentation_subject_areas`, `presentation_tables`, `presentation_columns` and `lineage` from the repository. It writes exactly one `report_lineage` row per analysis column and records no errors. It clears its output first, so running it twice gives the same result.

Matching is by name because the catalog stores only names. Rows are only ever matched within the same estate id.

### 6.1 Lookup tables built first

1. **Presentation columns** (`_index_presentation`): key `(estate id, subject area name, presentation table name, presentation column name)` → presentation column `row_id`. The subject area name comes from the column's table's subject area. A column whose table or subject area cannot be found is left out. Only the `name` fields are used; the `aliases` and `display_name` columns of the repository tables are not consulted.
2. The same map with the three names lowercased.
3. **Subject areas**: `(estate id, name)` → subject area `row_id`, and the same with the name lowercased.
4. **Criteria** by `row_id`.
5. **Repository lineage**: `presentation_column_id` → lineage row.

Maps 2 and 3 (lowercased) and map 5 are plain maps filled in iteration order: **if two entries share a key, the later one silently replaces the earlier**. There is no ambiguity detection here, unlike path resolution. For map 5 this means that when the repository lineage has several rows for one presentation column (several sources), the row used is the last one in the in-memory lineage list. That list's order is produced by the repository reader and is outside this guide; it could not be determined here. Define the tie-break explicitly in the port.

### 6.2 Per analysis column

Let `stated` = the `subject_area` of the column's criteria (empty if the criteria is missing).

**Subject area.**

1. `area_name` = `stated` with all leading and all trailing `"` characters removed (Python `strip('"')`; not limited to one pair).
2. `subject_area_id` = exact lookup of `(estate id, area_name)`; if not found, the lowercased lookup; else empty.

`subject_area_id` is set independently of whether the column itself resolves.

**Parsing the formula.**

1. `formula` = the column's `formula` with leading and trailing whitespace removed. (`reference_text` in the output is the untrimmed original.)
2. Test `formula` against the whole-string pattern

   ```
   ^"([^"]+)"\."([^"]+)"$
   ```

   That is: a double-quoted name, a dot, a double-quoted name, and nothing else. Group 1 is the presentation table name, group 2 the presentation column name. The quotes are dropped by the capture; nothing inside is trimmed or unescaped.
3. This is the only parsing done. A formula with three parts (`"Area"."Table"."Column"`), a doubled quote inside a name, an unquoted name, a function call, arithmetic, a variable reference such as `@{Dim01}{"Products"."P4  Brand"}`, or any surrounding text does not match. Column references inside larger expressions are not extracted.

The subject area is never taken from the formula. It always comes from the criteria.

**Initial `resolution`.**

| Condition, tested in this order | `resolution` |
|---|---|
| `formula` is empty | `not_a_column_reference` |
| pattern does not match and `formula` starts with `XSA(` | `outside_semantic_model` |
| pattern does not match | `not_a_column_reference` |
| pattern matches | `unresolved` (may be upgraded below) |

**Lookup.** Only when the pattern matched **and** `area_name` is non-empty:

1. Look up `(estate id, area_name, table, column)` exactly. Found → `resolution` = `exact`.
2. Else look up with all three names lowercased. Found → `resolution` = `case_normalised`.
3. Else stays `unresolved`.

**Join to repository lineage.** Only when a presentation column was found:

- A repository lineage row exists for that presentation column → copy its `logical_column_id` and `physical_column_id`; `source_state` = `sourced`.
- None exists → both ids empty; `source_state` = `computed_not_sourced`. This is a presentation column that is calculated in the business model and has no physical column. It is a fact about the column, not a failed lookup.

When no presentation column was found, `logical_column_id`, `physical_column_id` and `source_state` are all empty.

**Row id.** Take the object-path part of the analysis column's `row_id` and append `#lineage`: `<id>::cat::<analysis path>#c<i>.<j>#lineage`. The column id is not nested inside another id, because `::` may appear only twice.

### 6.3 Value summary

| `resolution` | Meaning |
|---|---|
| `exact` | bare `"Table"."Column"` found with names exactly as written |
| `case_normalised` | found only after lowercasing all three names |
| `unresolved` | bare reference, but no such presentation column in this estate (or the criteria has no subject area, or there is no repository half) |
| `not_a_column_reference` | empty formula, or a computation that this pass does not parse |
| `outside_semantic_model` | formula starts with `XSA(` and is not a bare reference: a Visual Analyzer uploaded dataset, which was never in the repository |

| `source_state` | Meaning |
|---|---|
| `sourced` | presentation column found and the repository has a lineage row for it |
| `computed_not_sourced` | presentation column found, no lineage row |
| empty | no presentation column found |

### 6.4 `XSA(...)` handling, exactly

`XSA('user'.'dataset')` names an uploaded dataset. It appears in two places:

- In `saw:criteria/@subjectArea` (40 of the fixture's 67 distinct values). It is stored verbatim. In lineage, `area_name` is the same string (it has no quotes to strip), it matches no subject area, and `subject_area_id` stays empty.
- At the start of a column formula: `XSA('prodney'.'US 5Y Florida lotto')."Columns"."Day #"`. It does not match the bare pattern and starts with `XSA(` → `outside_semantic_model`.

Edge cases, by code reading:

- `BIN(XSA(...)."Columns"."x" BY ...)` does not start with `XSA(` → `not_a_column_reference` (seen in the fixture).
- A bare `"Table"."Column"` formula inside a criteria whose subject area is an `XSA(...)` value → pattern matches, lookup fails → `unresolved`, not `outside_semantic_model`. Whether this occurs in real data was not checked.
- The `XSA(` test is on the trimmed formula and is case-sensitive.

### 6.5 Test vector (hand-made pair)

The small fixture `constructed-catalog` (section 12) paired with a repository that defines subject area `Constructed SA`, presentation table `Sales Facts`, columns `Amount` (physical source exists) and `Rolling Amount 3M` (calculated in the business model). Tests in `test_constructed_pair.py` pin:

| `reference_text` | `subject_area` | `subject_area_id` | `presentation_column_id` ends with | ids | `source_state` |
|---|---|---|---|---|---|
| `"Sales Facts"."Amount"` | `"Constructed SA"` | non-empty | `Constructed SA.Sales Facts.Amount` | logical and physical both non-empty | `sourced` |
| `"Sales Facts"."Rolling Amount 3M"` | `"Constructed SA"` | non-empty | non-empty | physical empty | `computed_not_sourced` |

The row count equals the `analysis_columns` row count. These two halves were written by the same author, so they prove the mechanism only.

---

## 7. Formulas, filters, prompts, views

### 7.1 Formula capture and the four flags

The catalog stores an expression once, as the text of a `sawx:expr` element. It is captured verbatim (`_expression`, section 1.6). There is no resolved or rendered form, and none is invented.

| Field | Exact computation | What it does and does not mean |
|---|---|---|
| `formula_construct` | Clark name of `saw:columnFormula` if the column owns at least one, else empty | Names the element that carried the formula. Only one value is ever produced today. Visual Analyzer's `saw:paSimpleColumnFormula` is not captured (section 11) |
| `formula_type` | `xsi:type` of the direct-child `sawx:expr` of the first owned formula | The expression's own type. In the fixture: `sawx:sqlExpression` (10,325), `sawx:binned` (15), empty (803) |
| `is_computation` | `true` when the **column's** `xsi:type` is non-empty and not equal to `saw:regularColumn`; otherwise `false` (`_parse_columns`) | Derived from the column element's type, never from the formula text. A column with no `xsi:type` is `false` |
| `uses_eval` (columns) | `true` when any `sawx:expr` under any of the column's owned formulas has `xsi:type` = `sawx:eval` (`_uses_eval`) | A direct database function call (`EVALUATE`). The logic lives in the database, not in OBIEE |
| `uses_eval` (analysis filters) | `true` when the expression itself or any expression nested in it has `xsi:type` = `sawx:eval` | Every ancestor of an eval expression is also flagged (fixture: 297 expressions of type `sawx:eval`, 655 rows flagged) |
| `uses_eval` (saved filters) | `true` when any `sawx:expr` under the `saw:filter` has `xsi:type` = `sawx:eval` | |

Read `is_computation` with care. It answers "is the column element typed as something other than a regular column", nothing more:

- `false` does not mean the formula is a bare reference. A `saw:regularColumn` can hold `count(distinct "Orders Dates"."R14  Billed Date")`. In the fixture `is_computation` is `true` on 803 columns, while lineage classifies 4586 formulas as `not_a_column_reference`.
- In the fixture all 803 `true` columns have **no captured formula**. Some are the result columns of the outer block of a set operation. Their `xsi:type` values are not stored anywhere, so what the others are could not be determined from the outputs.
- To find calculated columns, use `report_lineage.resolution`, not this flag.

`sawx:eval` is never the outermost expression type in the fixture, which is why the check scans nested expressions. In the fixture every eval is in a filter and none is in a column formula.

### 7.2 Headings

`heading` and `table_heading` are the text of `saw:caption/saw:text` under `saw:columnHeading` / `saw:tableHeading`, each found as a direct child chain (`_caption_text`). They are present only when the author overrode the default heading. `saw:caption/@fmt` and `@captionID` are not read.

### 7.3 Filter expressions and nested and/or trees

A `saw:filter` wraps one expression tree. Logical nodes (`xsi:type` = `sawx:logical`, `op` = `and` / `or`) contain comparison nodes, which contain operand nodes.

The tree is stored **flattened in document order (pre-order), one row per `sawx:expr`**, numbered `#f<i>.<k>`:

```xml
<saw:filter>
 <sawx:expr xsi:type="sawx:logical" op="and">                         f0.0  op=and      text = whitespace
  <sawx:expr xsi:type="sawx:savedFilter" path="/shared/.../X"/>        f0.1  reference set, resolved
  <sawx:expr xsi:type="sawx:comparison" op="equal">                    f0.2  op=equal    text = whitespace
   <sawx:expr xsi:type="sawx:sqlExpression">"Origin"."Orig Airport Desc"</sawx:expr>   f0.3
   <sawx:expr xsi:type="xsd:string">SFO - San Francisco International</sawx:expr>      f0.4
  </sawx:expr>
 </sawx:expr>
</saw:filter>
```

**No parent pointer and no depth are stored.** The structure cannot be rebuilt from the rows: after flattening, `and(A, or(B, C))` and `and(A, or(B), C)` look the same. If the new project needs the tree, add a parent or depth column; that would be an extension, not a port.

Other points:

- `op` is stored as written and never checked against a list. Fixture values include `prompted`, `and`, `or`, `equal`, `in`, `notIn`, `between`, `greater`, `top`, `bottom`, `notNull`, `null`, `like`, `beginsWith`, `server`.
- `prompted` (type `sawx:special`) is the most common operator. A prompted filter has no value in the file; the value comes from a dashboard prompt at run time. It is stored as `op` = `prompted`, never as an empty comparison.
- Literal operands are rows of their own, typed `xsd:string`, `xsd:decimal`, `xsd:date`, `xsd:dateTime`, `sawx:untypedLiteral`, with the literal as `expr_text`.
- Fixture expression types: `sawx:sqlExpression`, `sawx:special`, `xsd:string`, `sawx:logical`, `sawx:comparison`, `sawx:savedFilter`, `sawx:eval`, `sawx:list`, `sawx:columnExpression`, `xsd:decimal`, `sawx:rank`, `sawx:untypedLiteral`, `sawx:sql`, `sawx:subQuery`, `xsd:date`, `sawx:variable`, `xsd:dateTime`.
- Filters are attached to the **analysis** (`owner_id`), and the criteria is recoverable only from the `<i>` in the row id.
- By code reading: if a `saw:criteria` is nested inside a `saw:filter` (a sub-query), the inner criteria's filter expressions are emitted twice, once as descendants of the outer filter (`#f<outer>.<k>`) and once for the inner criteria (`#f<inner>.<k>`). Whether the fixture contains this shape was not checked.
- Dashboard-scope rows (section 3.2) carry only a reference; they have no expression.

### 7.4 Prompts

See section 3.8. `set_variable` is captured because a prompt that sets a presentation variable creates a dependency on any column formula that reads that variable, and that link is invisible from the column binding alone. Prompt labels, default values, choice lists and constraints are not read.

### 7.5 Views

See section 3.9.

- `view_type` is the source's own token (`saw:pivotTableView`, `saw:dvtchart`, `saw:compoundView`, ...). It is never mapped to a friendly name and never validated. An unknown type yields a normal row, not an error (pinned by a test with `saw:viewFromANewerOBIEE`).
- A view with no `xsi:type` gets the literal `(no xsi:type)`, so "absent" is distinct from "not read". The fixture has none.
- `references_views` and `bound_column_count` let a reader separate layout containers from data visuals without a list of type names. Fixture: 1580 of 1581 `saw:compoundView` have `references_views` > 0 and bind nothing; every `saw:titleView` has both at 0; all 1739 `saw:dvtchart` bind columns.
- `bound_column_count` counts **elements**, not distinct columns. It counts every descendant with a non-empty `columnID` at any depth, because the binding sits at different depths in a pivot, a chart and a trellis. If views nest, the outer view's count includes the inner view's bindings.
- `display_type` / `display_subtype` come from a **direct child** `saw:display` only. In the fixture only the `dvt*` chart family has it (`line`/`default`, `scatter`/`default`, `bar`/`stacked`, `pie`/`default`, `bar`/`horizontal`, `lineBar`/`basic`, ...).
- Views are not separate catalog objects here. Standalone `saw:view` **root** objects are a different population and are unmodelled (section 11).
- Fixture view types by frequency: `saw:dvtchart`, `saw:compoundView`, `saw:titleView`, `saw:pluginView`, `saw:tableView`, `saw:pivotTableView`, `saw:htmlview`, `saw:headlineView`, `saw:narrativeView`, `saw:trellisView`, `saw:dvtgauge`, `saw:canvas`, `saw:mapview`, `saw:filtersView`, `saw:viewSelector`, `saw:dvtmicrochart`, `saw:legendView`, `saw:sqlview`, `saw:treemapView`, `saw:columnSelectorView`, `saw:dvtfunnel`, `saw:selectionView`, `saw:noresultsview`, `saw:heatmapView`, `saw:tickerview`.

---

## 8. `catalog_versions`, `coverage`, `coverage_summary`

### 8.1 `catalog_versions`

Columns: `rpd_id`, `object_type`, `xml_version`, `objects`.

A counter keyed by (estate id, root Clark name, root `xmlVersion`) (`Estate.record_version`):

- incremented once for each **modelled** object as it is parsed;
- incremented once for each **unmodelled** XML root (`_report_unmodelled`).

So it covers every file that parsed as XML, and no file that did not. An absent version is counted under the empty string and **written as `(none)`**. Rows are sorted after that substitution.

Examples:

| `object_type` | `xml_version` | `objects` |
|---|---|---|
| `{com.siebel.analytics.web/report/v1.1}report` | `201201160` | 1504 |
| `{com.siebel.analytics.web/report/v1.1}report` | `(none)` | 103 |
| `{com.siebel.analytics.web/savedfilter/v1.1}savedFilterObject` | `(none)` | 96 |
| `APXPBFEG` (a root with no namespace) | `(none)` | 1 |

### 8.2 `coverage`

Columns: `rpd_id`, `layer`, `object_type`, `parsed`, `skipped`.

A counter keyed by (estate id, layer, object type) with two numbers (`Estate.count`). The catalog reader always uses layer `cat` (the repository reader uses `rpd`; both write to the same table). Increments made by the catalog reader:

| Event | `object_type` | Bucket | Amount |
|---|---|---|---|
| A modelled object parsed | root Clark name | `parsed` | 1 |
| Views found in an analysis | the literal `saw:view (element, inside an Analysis)` | `parsed` | number of views (only if > 0) |
| Unmodelled root type | root Clark name | `skipped` | number of objects of that type |
| Any error row (`Estate.record_error`) | the error's `object_type`: the Clark name of the referencing element, or `File`, or `Catalog` | `skipped` | 1 per error row |

Consequences:

- The table mixes objects and references. It has rows such as `{...dashboard/v1.1}reportRef`, parsed 0, skipped 599, which count unresolved references, not objects.
- The doubled error for `sawd:globalFilterView` doubles its `skipped` count (772 for 386 unresolved references).
- The view label is spelled out so that views inside analyses (10,879) are not confused with standalone `saw:view` root objects (530, a `skipped` row under the plain Clark name).
- Criteria, columns, filters and prompts are not counted in this table.
- A non-XML file is not counted here at all.

### 8.3 `coverage_summary`

Columns: `rpd_id`, `measure`, `count`, `of_files_seen`, `percent`.

Source: the input census, a counter keyed by (estate id, measure) (`Estate.census`). Only the catalog reader writes to it.

| Measure | Incremented when |
|---|---|
| `files_seen` | once per file found, before parsing |
| `parsed_as_xml` | the file parsed as XML |
| `not_a_catalog_object` | the file failed to parse and was classified |
| `modelled_object` | once per object handled by one of the four passes |
| `unmodelled_object` | by the number of XML roots of unmodelled types |

One output row per (estate id, measure) (`knowledge_base._summary_rows`): `of_files_seen` = that estate's `files_seen`; `percent` = `round(100 * count / files_seen)` followed by `%`, or `n/a` when `files_seen` is 0 or missing.

Python's `round` rounds halves to the nearest even integer (2.5 → 2, 3.5 → 4). Java's `Math.round` rounds halves up. Use `Math.rint` or an explicit rule to match.

Invariants pinned by tests: `parsed_as_xml + not_a_catalog_object = files_seen` and `modelled_object + unmodelled_object = parsed_as_xml`. A file that hits an OS read error is counted in `files_seen` only, so the first invariant fails by that number.

Fixture values: `files_seen` 5371 (100%), `parsed_as_xml` 4341 (81%), `not_a_catalog_object` 1030 (19%), `modelled_object` 2850 (53%), `unmodelled_object` 1491 (28%).

A measure with no increments has no row. An estate with no catalog half, or with an empty `catalog/`, has no rows at all.

---

## 9. Errors and unparsed rows

Two separate tables. `errors` is "something that should have been captured was not". `unparsed` is "present, deliberately not modelled or not read". Neither stops the run.

### 9.1 `errors`

Columns: `rpd_id`, `kind`, `source_id`, `object_type`, `reason`. Sorted; identical rows are kept, not de-duplicated. Every error row also adds 1 to `coverage.skipped` (section 8.2), except `duplicate_identifier`.

Note that for catalog errors `source_id` is the **decoded path** of the owning object (no `<id>::cat::` prefix), or a file-system path.

| `kind` | Raised when | `source_id` | `object_type` | `reason` |
|---|---|---|---|---|
| `unreadable` | the declared catalog directory contains no files (`load`) | the catalog root path as given | `Catalog` | `the declared catalog directory holds no files. Extraction continues; the manifest records that the catalog half was present and empty, which is not the same as absent.` |
| `unreadable` | a file cannot be opened or read, either during parsing or while reading its first 16 bytes (`_parse_all`) | decoded path of the file | `File` | the operating system's error text |
| `unresolved_reference` | a reference is under another root, or matches no path (`Resolver._record_unresolved`) | decoded path of the object holding the reference | Clark name of the referencing element (`sawd:dashboardPageRef`, `sawd:reportRef`, `sawd:globalFilterView`, `sawd:scorecardViewRef`, `sawd:folderView`, `sawx:expr`) | `<reference in quotes> did not resolve: <detail>. Recorded rather than repaired (AD-3).` where `<detail>` is one of the two `unresolved` texts in section 5.4 |
| `ambiguous_reference` | a reference matches two or more paths ignoring case (`Resolver.resolve`) | same | same | `<reference in quotes> matches more than one object once case is folded — <a> | <b>. Choosing between them would be a guess, so it is recorded as unresolved.` |
| `duplicate_identifier` | after the merge, two rows of one table share a `row_id` (`Estate.check_identifier_uniqueness`) | the duplicated `row_id` | the in-memory table name (e.g. `analyses`, `dashboard_contents`) | `<n> rows in <table> share this identifier; a Group Spec citing it cannot name which one. The identifier does not carry a distinction the source made.` |

Real examples:

```
source_id:   01. quickstart/_portal/1.10 whats new/dashboard layout_orig
object_type: {com.siebel.analytics.web/dashboard/v1.1}dashboardPageRef
reason:      'V406 (11.1.1.7)' did not resolve: no object or folder at this path. Recorded rather than repaired (AD-3).

source_id:   11. demos/_portal/11.31 cricket stats/overview
object_type: {com.siebel.analytics.web/dashboard/v1.1}reportRef
reason:      '/company_shared/UT/UT_insert' did not resolve: names a catalog root this tree does not contain. Recorded rather than repaired (AD-3).
```

The quoted reference in `reason` is Python's `repr()` of the string: normally single quotes, but double quotes when the text contains a single quote, with backslash escapes for some characters. Byte-identical reasons in Java would need that rule re-implemented. Decide whether that matters.

A catalog `duplicate_identifier` can arise when two on-disk names decode to the same path (for example `a+b` and `a%20b`). Both files are parsed and both produce rows. The check covers every table that has a `row_id`.

The fixture has 1552 catalog error rows, all `unresolved_reference`: 772 `globalFilterView` (386 doubled), 599 `reportRef`, 176 `sawx:expr`, 3 `dashboardPageRef`, 2 `scorecardViewRef`. It has no `ambiguous_reference` and no `unreadable` rows.

Fatal conditions (no tables written, exit code 2): section 2.4, plus an `IdentifierError` from building an id (section 4.1).

### 9.2 `unparsed`

Columns: `rpd_id`, `kind`, `object_type`, `detail`, `occurrences`. These rows do not add to `coverage.skipped`, except `unmodelled_type` (which does so through `_report_unmodelled`).

| `kind` | Meaning | `object_type` | `detail` | `occurrences` |
|---|---|---|---|---|
| `not_a_catalog_object` | files that are not XML, one row per byte class | `File` | `<class label> — present in the tree, never a catalog object` (em dash, U+2014) | files in the class |
| `unmodelled_type` | XML roots of a type no pass handles, one row per root type | root Clark name | `<n> object(s), e.g. <decoded path of the first one in file order>` | `n` |
| `unread_element` | an element in a modelled object, with nothing in its subtree read | root Clark name of the containing object | Clark name of the element | count |
| `unread_attribute` | an attribute nobody read, not styling | same | `<element Clark name>@<attribute name>` | count |
| `unread_text` | non-blank leading text nobody read | same | `<element Clark name>#text` | count |
| `excluded_by_scope` | an attribute nobody read that is styling | same | `<element Clark name>@<attribute name>` | count |

`unmodelled_type` rows come from enumerating what was parsed, never from a fixed list, so a new root type always gets a row. Fixture examples: `{com.siebel.analytics.web/report/v1.1}view` (530), `{com.siebel.analytics.web/report/v1.1}action` (115), `{http://xmlns.oracle.com/oxp/xmlp}report` (156, BI Publisher), `DATA_DS` (86, a data export with no namespace).

### 9.3 The read audit (`observing.audit_unread`, `_audit`)

The audit does not use a declared list of "fields we read". It records what the parsing code actually touched on each element and reports the rest. To port it, the XML node class needs three pieces of state: the set of attribute names that were asked for, a "text was read" flag, and a "was looked for" flag.

What sets them:

| Action in the parser | Marks |
|---|---|
| Asking an element for an attribute value, **whether or not the attribute exists** | that name added to the element's "asked" set |
| Reading an element's text through `read_text`, even if it is empty | "text was read" |
| An element returned by a find / find-all, or iterated on purpose by a parser | "was looked for" |

An element is **touched** if any of the three is set. An element is **reached** if it or anything in its subtree is touched.

Audit rules, applied to every element of every **modelled** object (unmodelled objects are skipped; they are already reported whole):

1. Element not reached → `unread_element` +1 for its Clark name. Its descendants are still visited and each reported individually.
2. For every attribute present and not in the "asked" set: if styling → `excluded_by_scope` +1; else → `unread_attribute` +1. This applies at every element, including inside unreached subtrees.
3. Element text (section 1.6) that is non-blank after trimming, with "text was read" not set → `unread_text` +1. Text after a child element is never examined.
4. Counts are grouped by (estate id, root type of the containing object, detail).

Attribute names in `detail` are Clark names when the attribute is namespaced, e.g. `...@{http://www.w3.org/2001/XMLSchema-instance}type`. Namespace declarations (`xmlns`, `xmlns:*`) are not attributes in Python's tree and never appear. A namespace-aware Java DOM does expose them as attributes; exclude them.

Styling rule (`is_styling`), applied to the attribute's local name:

- Not styling if the name is exactly one of: `fmt`, `textFormat`, `customFormat`, `formatMask`, `biFormat`, `defaultFormat`, `outputFormat`, `format`, `formatOutputType`, `renderFormat`. These change displayed data or delivery format, not appearance.
- Otherwise styling if the lowercased name contains any of: `align`, `bgcolor`, `border`, `color`, `fmt`, `font`, `format`, `height`, `indent`, `padding`, `style`, `width`, `wraptext`.

Which reads the parsers make is listed in section 3. Side effects that shape the audit output and are easy to miss:

- `_owned_by_nearest` marks nothing. Only the code that consumes an element marks it. That is why a second formula on a column, and formulas that belong to no column, still show up as unread.
- The eval check asks every `sawx:expr` under a formula or filter for its `xsi:type`, so those elements count as touched even when their text is never read.
- The bound-column count asks **every descendant of every view** for `columnID`. So every element inside a `saw:view` counts as touched and is never reported as `unread_element`. Their other attributes and their text are still reported.
- `saw:views/@currentView` is read only to keep it out of the report.

If the port's reads differ in any of these ways, its `unparsed` counts will differ from the reference while the data tables stay identical. Decide how closely the audit must match. The fixture produces 625 `unread_attribute`, 197 `unread_element`, 51 `unread_text`, 112 `excluded_by_scope`, 55 `unmodelled_type` and 10 `not_a_catalog_object` rows for the catalog.

---

## 10. Gotchas and decisions

Input and classification

1. Objects have no extension. Every file must be attempted. Filtering by name loses objects.
2. A non-XML file is never an error. A damaged XML object is indistinguishable from one (section 1.4).
3. The folder is a catalog because the operator put it in `catalog/`. Content is never used to decide that (`layout.py`, `catalog_xml.load`).
4. There is no BAR (zip) code path. A test asserts no module imports the zip library. The operator unpacks the archive.
5. `+` before `%XX` when decoding names.
6. Type membership tests on names (such as "is inside a `.xdo` folder") must use the decoded path.

Names and namespaces

7. Match elements and attributes by namespace URI and local name. Never by prefix or local name alone (`sawd:reportRef` vs `saw:reportRef`; `xsi:type` vs another `type`).
8. `xsi:type` **values** are compared as literal prefixed strings.
9. Dashboard name = containing folder name; page and analysis name = file name.
10. Catalog paths go into identifiers as one piece. Names in the fixture contain `.`, `:`, `&`, `%`, spaces and commas. A name containing `#` could collide with the `#<suffix>` sub-row scheme; the reference has no guard for that.

Capture rules

11. Everything textual is verbatim: no trimming, no quote stripping, no whitespace collapsing. Double spaces inside names (`"D4  Company"`) are significant.
12. Text means text before the first child element (section 1.6).
13. An analysis can have several criteria, and they nest. Use nearest-ancestor ownership or columns are counted more than once (the reference once reported 13,091 columns where 11,143 exist).
14. A formula is found at any depth under its column, not only as a direct child.
15. Only the first formula of a column is stored.
16. `subject_area` keeps its quotes in `analysis_criteria` and `report_lineage`. Quote stripping happens only inside the lineage lookup.
17. Not every `subject_area` is a subject area: `XSA(...)` values are uploaded datasets.
18. A missing `xmlVersion` is a normal value, written `(none)` in `catalog_versions` and empty elsewhere.
19. A missing view type is written `(no xsi:type)`; an unknown one is stored as is.
20. `op`, `view_type`, `expr_type`, `prompt_type` are open vocabularies. Never validate them against a list.
21. An untyped `saw:prompt` is skipped but uses up a position number.

References

22. Exact match first, case-insensitive second, and the outcome records which. Never fold silently; never choose between ambiguous candidates.
23. Index folders and non-XML files as well as objects.
24. `/shared/` is the only root that maps to the tree. Other roots are recorded as unresolved, not as broken.
25. References are never repaired, even when visibly malformed.
26. A page's owning dashboard comes from a resolved reference, first one wins, never from the folder.
27. `position` on page contents is grouped by element type (section 3.2).
28. The global filter view is resolved twice, doubling its error rows.
29. Dashboard-scope and analysis-scope filters share a table but must stay distinguishable by `scope`.
30. Saved-filter references are found by `xsi:type`, not by element name.

Lineage

31. Lineage is its own table. It is never merged into the repository's lineage table.
32. Lineage runs once, after both halves are merged, never inside either reader. Each reader loads into its own estate object first; sharing one object made post-passes run twice and duplicated rows.
33. Only a whole-formula `"Table"."Column"` is resolved. Computations are labelled, not parsed.
34. The lowercased lookups in lineage have no ambiguity detection (section 6.1).
35. An empty physical column with `computed_not_sourced` is a result, not a failure.

Output

36. Row order is a string sort; file discovery order still matters for: which dashboard owns a page referenced twice, the example path in `unmodelled_type` rows, and which registration wins in the index.
37. `percent` uses round-half-to-even.
38. Python string operations used: `lower()` for case folding, `strip()` for trimming the formula (Unicode whitespace, including the no-break space, which Java's `String.strip()` does not remove), `strip('"')` for quotes.
39. Extraction must be deterministic: the same input gives the same rows (pinned by tests).

Java-specific notes

- JAXP (`javax.xml.parsers`, SAX or StAX) is part of the JDK. A small custom node tree built from SAX events is the simplest way to get: Clark names, text-before-first-child, parent pointers for nearest-ancestor ownership, document-order traversal, and the three audit flags.
- Disable external DTD and entity loading. Enable namespace awareness.
- `File.listFiles` order is undefined. Sort explicitly (section 1.2).

---

## 11. Known gaps

These are limits of the reference implementation. They are recorded there as deferred work.

**Prompts mostly live in objects that are not read.** 991 of the fixture's 995 `saw:columnFilterPrompt` elements are inside standalone `saw:view` root objects (530 objects, unmodelled). Only 4 are inside an analysis, so `analysis_prompts` has 4 rows. Since `prompted` is the most common filter operator (1054 analysis filter rows), the values those filters receive at run time are defined almost entirely in objects nobody parses. A dashboard prompt object looks like this:

```xml
<saw:view xmlns:saw="..." xmlns:xsi="..." xsi:type="saw:globalFilterPrompt" name="">
  <saw:prompts scope="dashboard" subjectArea="&quot;A - Sample Sales&quot;">
    <saw:promptStep>
      <saw:individualPrompts>
        <saw:prompt xsi:type="saw:currencySelectorPrompt" subjectArea="&quot;A - Sample Sales&quot;"> ... </saw:prompt>
```

Modelling the `saw:view` root type is the single most valuable addition. It is a new object type, not an adjustment. It would also give `sawd:globalFilterView` references something to join to.

**Visual Analyzer projects.** Files named `_projectdefn` have a `saw:report` root, so they are read as analyses, but they are a different thing: no `xmlVersion`, subject area given as `XSA('user'.'dataset')`, views of type `saw:canvas` and `saw:pluginView`. They hold 42 formulas in `saw:paSimpleColumnFormula`, which stores its expression directly as element text with no `sawx:expr` child and no `xsi:type`, hangs off `saw:propertyAddition`, and is never inside a criteria. These are not captured. They appear in `unparsed` (the committed output lists them as `unread_text`, 42; the format notes say `unread_element`). Capturing them means modelling the project structure.

**Scorecards, KPIs, actions, agents.** `sawbsc:*` scorecard objects, `sawkpi:kpi`, `saw:action` and `saw:ibot` (agents) are unmodelled: 918 objects in the fixture, each reported per type as `unmodelled_type`. Each is its own object shape. A page's `sawd:scorecardViewRef` resolves to a path but the target is not read. `sawd:kpiRef` on pages (777) is not read.

**BI Publisher content** shares the tree (`{http://xmlns.oracle.com/oxp/xmlp}dataModel`, `...report`, `{http://xmlns.oracle.com/oxp/config/}config`) and is a different product. It is reported as unmodelled and should stay so.

Further gaps found while reading the code:

- Saved filter content below the outermost expression is not captured (section 3.10).
- Filter tree structure is lost by flattening (section 7.3).
- The column element's `xsi:type` is used for a flag but not stored.
- Column references inside computed formulas are not extracted, so lineage covers bare references only.
- Subject-area and presentation-column aliases in the repository are not used for matching.
- Dashboard page layout (sections, columns, order across element types), hidden pages, page groups, and view-to-page display settings are not captured.
- `saw:column` elements outside a criteria, `saw:reportRef` (report namespace) and the other `@path` carriers listed in section 5.5 are not captured.
- Access control (`.atr`) is not read at all. Absence of permission data must not be read as "unrestricted".
- Styling is excluded on purpose. Two excluded attributes can hide content: `style` (values include `display:none`) and `height` (values include `0`).

---

## 12. Test fixtures to rebuild

**Unit fixture** (`test_catalog.py`, built in a temp folder; estate id `demo-estate`). Eight files:

| On-disk path | Content |
|---|---|
| `demo/_portal/sales+overview/dashboard+layout` | `sawd:dashboard`, version `200810300`, three page refs: `Page One`, `Page Two`, `/company_shared/Absent/Page` |
| `demo/_portal/sales+overview/page+one`, `page+two` | `sawd:dashboardPage` with an unknown attribute, styling attributes (`wrapText`, `fontSize`), a `sawd:reportRef` to `/shared/Demo/Revenue Analysis`, an unknown element `sawd:unknownView`, a `sawd:globalFilterView` to `/shared/Demo/_filters/Region Filter` |
| `demo/revenue+analysis` | `saw:report` with two criteria, three columns in the first (a plain reference; a formula starting with `-` containing commas, doubled quotes and a newline; a formula with a nested `sawx:eval`), one column in the second, a filter with `and` → `prompted`, two saved-filter references (one resolvable, one not), six views (compound, title, pivot, chart with `bar`/`stacked`, an unknown type, one with no type), and one typed prompt setting `pv_region` |
| `demo/_filters/region+filter` | `sawsf:savedFilterObject`, no version, `and` → `prompted` |
| `demo/co%6dponents` | an unmodelled scorecard root `{com.siebel.analytics.web/scorecard/v1}objective` |
| `demo/bip+model` | an unmodelled default-namespace root `{http://xmlns.oracle.com/oxp/xmlp}dataModel` |
| `demo/thumbnail%2epng` | PNG signature followed by zero bytes |

Pinned results include: census `files_seen` 8 and both invariants; `Page One` → `case_normalised`; `/company_shared/Absent/Page` → `unresolved` plus an `unresolved_reference` error; page positions 0 and 1; `wrapText` and `fontSize` under `excluded_by_scope` and not under `unread_attribute`; `unknownView` as `unread_element`; `unknownAttribute` as `unread_attribute`; 1 analysis, 2 criteria; formulas byte-identical; `c1` not a computation, `c2` a computation; `c3` `uses_eval`; 6 views with positions 0–5; compound view references 2 and binds 0; pivot binds 2; chart binds 1 with `bar`/`stacked`; 2 saved-filter references of which 1 resolves to `...demo/_filters/region filter`; `prompted` and `and` among filter ops; both scopes present; 1 prompt (`saw:columnFilterPrompt`, `"Cust"."Region"`, `in`, `pv_region`); 1 saved filter with `op` `and`; 1 `not_a_catalog_object` row mentioning `PNG image`; both unmodelled root types listed; `catalog_versions` has (`savedFilterObject`, empty) = 1 and (`dashboard`, `200810300`) = 1; `saw:view` and `saw:views` not reported as unread elements; all coverage rows in layer `cat`.

**Paired fixture** (`constructed-catalog`, four files under `constructed/`):

| On-disk path | Content |
|---|---|
| `constructed/_portal/sales+overview/dashboard+layout` | one page ref `Page One` |
| `constructed/_portal/sales+overview/page+one` | `sawd:reportRef` → `/shared/Constructed/Revenue by Month`; `sawd:globalFilterView` → `/shared/Constructed/_filters/Recent Periods` |
| `constructed/revenue+by+month` | one criteria, subject area `"Constructed SA"`, columns `"Sales Facts"."Amount"` and `"Sales Facts"."Rolling Amount 3M"`, filter `and` → saved-filter reference `/shared/Constructed/_filters/Recent Periods` |
| `constructed/_filters/recent+periods` | saved filter, `and` → `prompted` |

Expected, with estate id `constructed-pair` (row counts are stated in the fixture's ground-truth notes; the identifiers below were derived by applying the rules in this guide and were not read from an output file):

| Table | Rows | Identifiers |
|---|---|---|
| `dashboards` | 1 | `constructed-pair::cat::constructed/_portal/sales overview/dashboard layout`, name `sales overview` |
| `dashboard_pages` | 1 | `...constructed/_portal/sales overview/page one`, position 0 |
| `dashboard_contents` | 3, all resolved, all `case_normalised` | `...dashboard layout#page0`, `...page one#item0`, `...page one#item1` |
| `analyses` / `analysis_criteria` / `analysis_columns` | 1 / 1 / 2 | `...constructed/revenue by month`, `#criteria0`, `#c0.0`, `#c0.1` |
| `analysis_filters` | 3 | `...page one#gfilter0` (dashboard), `...revenue by month#f0.0` (`and`), `#f0.1` (saved-filter reference → `...constructed/_filters/recent periods`) |
| `saved_filters` | 1 | `...constructed/_filters/recent periods` |
| `report_lineage` | 2 | section 6.5 |
| `errors` | 0 | |

The catalog half alone is not an estate: placed next to a repository folder under a multi-estate root it is skipped, and the layout reports only the repository's estate id (pinned by `test_the_catalog_half_is_not_itself_an_estate`).
