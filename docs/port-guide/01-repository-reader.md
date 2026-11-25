# Port guide 01 — Repository reader (OBIEE MDS XML to CSV tables)

This guide describes what the Python reference implementation does when it reads an OBIEE repository exported as MDS XML and turns it into CSV tables. It is written so the reader can be re-implemented in Java 11 with the standard library only, without access to the Python source.

Python names are given in backticks so each claim can be checked against the reference: `mds_xml.py` (the reader), `model.py` (the data model), `identifiers.py` (identifier rules), `knowledge_base.py` (which column goes where), `observing.py` (the unread-content audit).

Conventions used here:

- "Python does X" is a statement read from the code.
- "Observed" is a statement read from the committed CSVs of the two sample repositories (`bi-analytics-2013`, `sampleapp`).
- "Unsure" marks behaviour that could not be determined from the code alone.
- "Port note" marks a point where Java behaves differently by default, or where the Python behaviour is a defect the port may want to change. Changing it changes the output; decide deliberately.

Columns whose name ends in `_guarded` are omitted throughout. They exist in the old CSVs only. If you compare new output with an old CSV: where an old `<col>_guarded` cell is `true`, the old `<col>` value carries one extra leading apostrophe that is not part of the data.

---

## 1. Input layout

### 1.1 Directory tree

One repository is a directory tree with one XML file per object:

```
<repository-root>/
  oracle/bi/server/base/
    <ObjectType>/            e.g. PhysicalTable, LogicalTable, Variable
      <anything>.xml         usually <guid>.xml
```

`load(corpus_root, rpd_id)` accepts two shapes:

| Shape | Call | Meaning |
|---|---|---|
| Single repository | `rpd_id` given | `corpus_root` itself contains `oracle/bi/server/base/`. Files are found by `_single_repository_files`. |
| Several repositories | `rpd_id` omitted | Each sub-directory of `corpus_root` that contains `oracle/bi/server/base/` is one repository, and the sub-directory name is its `rpd_id`. Files are found by `_object_files`. Sub-directories without that path are skipped silently. |

The command-line driver (`cli.extract`) always calls `load` in the single-repository shape, once per repository, and merges the results. The several-repositories shape is used only by tests. Port note: implement the single-repository shape; see gotcha G1 for why the other shape is unsafe.

The driver's own layout rules (`layout.resolve`), for reference: with `--rpd-id`, the corpus is either an estate folder (`<corpus>/rpd/oracle/bi/server/base/...`, optionally with a sibling `catalog/`) or a bare repository tree (`<corpus>/oracle/bi/server/base/...`). Without `--rpd-id`, each sub-folder of the corpus is one of those two, named by its `rpd_id`.

### 1.2 The repository identifier (`rpd_id`)

Declared by the operator, never derived. It must match this pattern (`validate_rpd_id`):

```
^[a-z0-9]+(?:-[a-z0-9]+)*$
```

Rejected examples pinned by tests: `TestRPD`, `test_rpd`, `test rpd`, `-test`, empty string. A bad `rpd_id` raises `IdentifierError` and the run stops.

### 1.3 File discovery

Exact rules (`_single_repository_files`):

1. List the direct sub-directories of `base/`. Sort them. Each directory name is the **object type** of every file in it.
2. In each type directory, take files matching `*.xml` directly in that directory (one level, no recursion). Sort them.
3. The result is an ordered list of `(rpd_id, object_type, path)`.

Consequences that matter:

- The object type comes from the **directory name**. The root element's tag is not checked against it. Handlers are chosen by directory name.
- The sort order of this list is the processing order inside every pass. It affects output in one visible way: which forward references fail (section 7.3). It also decides which of two colliding rows counts as "first" in the duplicate check (section 8.2). Python sorts `Path` objects; on Linux that is a case-sensitive comparison of the names by code point.
- `*.xml` is matched case-sensitively on Linux. `X.XML` is not picked up.
- Unsure: whether Python's `Path.glob("*.xml")` includes names starting with a dot. It is not exercised by any test.

Files the walk does not pick up are reported, not ignored (`_report_undiscovered`): every regular file anywhere under `corpus_root` (recursive) that is not in the discovered list produces one gap row of kind `undiscovered_file` (section 8.3). Tests pin two cases: a file nested one level too deep (`PhysicalTable/nested/buried.xml`) and a file without the `.xml` extension.

Failures while listing:

| Situation | Result |
|---|---|
| `corpus_root` is not a directory | `CorpusError`, run stops |
| `rpd_id` given but `corpus_root/oracle/bi/server/base` is not a directory | `CorpusError` with a hint (`_wrong_root_message`). If the directory is named `base` or contains a sub-directory holding `*.xml`, the message says it "looks like the 'base' directory itself" and suggests the directory four levels up. |
| `base/` cannot be listed (single-repository shape) | `CorpusError`, run stops |
| One type directory cannot be listed | Skipped; one error row of kind `unreadable` (section 8.1); the run continues |
| No file found at all | `CorpusError` naming both expected layouts |

### 1.4 Parsing, namespace and encoding

- Every file is parsed exactly once (`_parse_all`) and the trees are kept in memory for all later passes. Measured by the authors: about 28 MB for 1383 files / 9 MB of XML.
- The parser is given the file, not decoded text. Encoding is therefore whatever the XML declaration or byte-order mark says, defaulting to UTF-8. The reader has no encoding option and does no transcoding of its own. Fixture files start with `<?xml version="1.0" encoding="UTF-8" ?>`.
- Namespace: every object uses the default namespace `http://www.oracle.com/obis/repository`. Python does not check it. It strips the namespace from every tag and compares local names only (`_tag`: text after the last `}`). The constant `NAMESPACE` is declared and never used. Attributes are unqualified.
- A file that is not well-formed XML produces one error row of kind `malformed_xml` and is left out of everything else (it is not indexed and not parsed). Only XML parse errors are caught. Unsure, but read from the code: an I/O error on a single file (permission denied, a directory named `x.xml`) is not caught and would abort the run with an exception.

Port notes:

- Use `DocumentBuilderFactory` with `setNamespaceAware(true)`, compare `getLocalName()`, and pass a `File` or `InputStream` (never a `Reader`) so the declared encoding is honoured.
- Do not enable any whitespace-ignoring or normalising option. Text must come back character for character (section 4).
- If you disable DOCTYPE declarations for safety, a file with a DOCTYPE fails in Java where Python would have parsed it. No fixture file has a DOCTYPE. Unsure whether real exports ever do.
- `Element.getAttribute` returns `""` for an absent attribute, which matches Python's `attrib.get(name, "")` used everywhere. The one exception is the `mdsid` used as `source_id` in error rows: Python uses `attrib.get("mdsid", "?")`, so absent gives `?` and present-but-empty gives `""`. Use `hasAttribute` there.

### 1.5 Cross-references

Objects point at each other with attributes whose value looks like:

```
/oracle/bi/server/base/<ObjectType>/<file>.xml#<mdsid>
```

`<mdsid>` is the target element's own `mdsid` attribute. The target can be a file's root element or any nested element (a column inside a table file, a level inside a dimension file).

Resolution rule (`_Index.resolve_ref`), applied to every reference attribute:

```
^/oracle/bi/server/base/(?P<type>[^/]+)/[^#]+#(?P<mdsid>.+)$
```

- If the value matches, the result is `(type, mdsid)`. If it does not match (empty, relative, different prefix), the result is `("", "")` and the reference is treated as absent.
- Only the `mdsid` is used for lookup. The file name in the reference is ignored. `type` is used only in one error message.

Two lookups exist, and they are different things:

| Lookup | Filled when | Returns | Used for |
|---|---|---|---|
| `index.get(mdsid)` | Pass 0, for every element with an `mdsid` in every parsed file | tag, `name`, container `mdsid` | Names of containers and of objects that have no table of their own (schema, database, business model, role, query privilege) |
| `index.identifier(mdsid)` | During the type passes, by `index.bind(mdsid, row_id)` at the moment a row identifier is built | the row identifier, or `""` if none bound yet | Every `*_id` column and every expression reference |

Because identifiers are bound during the passes, a reference only resolves if its target was processed **earlier**. This is the reason for the pass order in section 7 and the source of the forward-reference failures described there.

---

## 2. What is read from each object type

"Table.column" below names the output CSV and column. Section 3 gives the full column lists. "Expression" means the three-form capture of section 4.

Three classes of object type directory:

| Class | Types | Treatment |
|---|---|---|
| Handled (`_HANDLED`) | `PhysicalTable`, `ComplexJoin`, `LogicalTable`, `PresentationCatalog`, `PresentationTable`, `PresentationHierarchy`, `Dimension`, `LogicalTableSource`, `LogicalComplexJoin`, `Variable`, `InitBlock`, `Group`, `PrivilegePackage`, `ObjectPrivilege` | A handler produces rows |
| Index only (`_INDEX_ONLY`) | `Database`, `Schema`, `ConnectionPool`, `BusinessModel` | No rows. Their `name` and `containerRef` are used through the index. Counted as parsed, one per file. |
| Everything else | Observed: `QueryPrivilege`, `Project`, `LogicalSourceFolder`, `User`, and any unknown directory | One gap row of kind `unmodelled_type` per file. Their elements are still in the index, so their names can be looked up (this is how `QueryPrivilege` names reach `security_rls`). |

### 2.1 `PhysicalTable` — `_parse_physical`

| Read | Feeds |
|---|---|
| root `@name` | `physical_tables.name`; part of every identifier below |
| root `@containerRef`, followed up the container chain (section 3.1) | `physical_tables.container`; identifier prefix |
| root `@mdsid` | bound to the table identifier |
| root `@type` | `physical_tables.table_type` (observed values: `none`, `select`) |
| root `@maxConn` | `physical_tables.max_conn` |
| root `@sourceTableRef` (read in a later pass, section 5.3) | `physical_tables.source_table_id` |
| each direct child `DBMapItem`: `@name`, and the text of each direct child `Value` | one `physical_table_definitions` row per `Value`: `database` = `DBMapItem@name`, `text` = `Value` text |
| each direct child `PhysicalColumn`: `@name`, `@mdsid`, `@dataType`, `@specialType`, `@precision`, `@nullable`, `@extName` | one `physical_columns` row: `name`, (bound identifier), `data_type`, `special_type`, `precision`, `nullable`, `external_name` |
| each direct child `PhysicalForeignKey`: `@name`; first direct child `RefColumns`, its direct children `RefColumn@columnRef` | one `physical_joins` row with `kind` = `foreign_key`: `name`, `table1_id` = this table, `columns` = the referenced `mdsid` values |

Not read (observed in the gap report): `PhysicalKey` and its columns, `PhysicalForeignKey@counterPartKeyRef`, `PhysicalColumn@sourceColumnRef`, `PhysicalColumn@scale`, `Description`, `@rowCount`, `@lastUpdated`, `@isCacheable`, `@pollFreq`, `@cacheExpiry`, and the parent-child helper refs `@memberKeyRef`, `@parentKeyRef`, `@leafColumnRef`, `@distanceColumnRef`.

### 2.2 `ComplexJoin` (physical) — `_parse_complex_join`

| Read | Feeds |
|---|---|
| root `@name` | `physical_joins.name`, identifier; `kind` = `complex_join` |
| root `@table1Ref`, `@table2Ref` | `physical_joins.table1_id`, `table2_id` (empty when unresolved, no error) |
| root `@cardinality` | `physical_joins.cardinality` (observed: `ONE TO ONE`) |
| first direct child `Expr` | `physical_joins.expr_text`, `expr_text_desc`, `expr_refs` |

`columns` is empty for complex joins. The join's `mdsid` is not bound, so an expression elsewhere that references a physical complex join cannot resolve.

### 2.3 `LogicalTable` — `_parse_logical`

| Read | Feeds |
|---|---|
| root `@name` | `logical_tables.name`, identifier |
| root `@subjectAreaRef` → name of the referenced `BusinessModel` (via `index.get`) | `logical_tables.business_model`, identifier prefix |
| first direct child `RefTableSources`, count of its direct children `RefLogicalTableSource` | `logical_tables.source_count`; copied to `logical_columns.source_count` of every column |
| each direct child `LogicalColumn`: `@name`, `@mdsid`, `@isWriteable` | one `logical_columns` row: `name`, (bound identifier), `is_writeable` |
| column's first direct child `Expr` | `logical_columns.derived_expr_*` (the column's own formula) |
| column's first direct child `AttrDefn`, then its first direct child `Expr` | `logical_columns.mapping_expr_*` |
| column's first direct child `MeasureDefn` (presence only) | `logical_columns.is_measure` |
| each direct child `AggrRule` of `MeasureDefn`: `@name`, `@isDefault`, first direct child `Expr` | one `aggregation_rules` row per rule (section 6.1) |

Despite the attribute name, `subjectAreaRef` on a logical table points at a `BusinessModel`.

Not read: `LogicalKey` and its columns, `LogicalColumn@isDerived`, `@customSortOrderRef`, `@descriptorIDRef`, `LogicalTable@isLookupTable`, `MeasureDefn@isCommutative`. The `RefLogicalTableSource@logicalTableSourceRef` values are counted, not followed.

### 2.4 `LogicalTableSource` — `_parse_table_source`

| Read | Feeds |
|---|---|
| root `@name` | `logical_table_sources.name`, identifier |
| root `@logicalTableRef` | `logical_table_sources.logical_table_id`, identifier prefix |
| root `@isActive` | `logical_table_sources.is_active` |
| root `@priorityGroup` | `logical_table_sources.priority` |
| first direct child `FragmentContent` (itself expression-shaped) | `fragment_text`, `fragment_text_desc`, `fragment_refs` |
| first direct child `WhereClause` (expression-shaped) | `where_text`, `where_text_desc`, `where_refs`; also one `security_rls` row of kind `row_filter` when not empty (section 6.7) |
| first direct child `GroupBy` (expression-shaped) | `group_by_text`, `group_by_text_desc`, `group_by_refs` |
| each direct child `ColumnMapping`: first direct child `LogicalColumnExpr` (expression-shaped) and first direct child `Expr` | one `lts_column_map` row: `logical_expr_*` and `physical_expr_*`; `logical_column_id` and `physical_column_id` are the first resolved reference of each |

Not read: `Link@startNodeTableRef`, `Join` (`@physicalTable1Ref`, `@physicalTable2Ref`, `@physicalJoinRef`, `@type`, `@cardinality`), `@closureTableRef`.

### 2.5 `LogicalComplexJoin` — `_parse_bmm_join`

| Read | Feeds |
|---|---|
| root `@name` | `bmm_joins.name`, identifier |
| root `@type` | `bmm_joins.join_type` |
| root `@logicalTable1Ref`, `@logicalTable2Ref` | `bmm_joins.table1_id`, `table2_id` (empty when unresolved, no error) |
| root `@multiplicity1`, `@multiplicity2` | `bmm_joins.multiplicity1`, `multiplicity2` |
| root `@isAggregate1`, `@isAggregate2` | `bmm_joins.is_aggregate1`, `is_aggregate2` |

### 2.6 `Dimension` — `_parse_dimension`

| Read | Feeds |
|---|---|
| root `@name` | `logical_dimensions.name`, identifier |
| root `@subjectAreaRef` → `BusinessModel` name | `logical_dimensions.business_model`, identifier prefix |
| root `@isValueBased`, `@isRagged`, `@isSkipped` (each compared with the exact string `true`) | `is_value_based`, `is_ragged`, `is_skipped` |
| root `@defaultRootLevelRef` | `logical_dimensions.default_root_level_id` |
| count of direct children `LogicalLevel` | `logical_dimensions.level_count` |
| each direct child `LogicalLevel`: `@name`, `@mdsid`, `@memberCount`, `@isGTA` | one `logical_levels` row: `name`, (bound identifier), `member_count`, `is_grand_total`; `position` = index among `LogicalLevel` children, from 0 |
| each direct child `LogicalKey` of the level: `@isForDrillDown`; `RefLogicalColumn@logicalColumnRef` at any depth below the key | `logical_levels.drill_key_column_id` (section 6.5) |

Not read: `RefChildLevels/RefLogicalLevel` (the parent-to-child structure), `RefPreferredDrillDown`, `LogicalKey@isPrimary`, `LogicalKey@isChronKey`, `Dimension@isTimeDim`, `LogicalLevel@isFDUC`, `LogicalLevel@levelConst`.

### 2.7 `PresentationCatalog` (a subject area) — `_parse_subject_area`

| Read | Feeds |
|---|---|
| root `@name` | `presentation_subject_areas.name`, identifier |
| root `@subjectAreaRef` → `BusinessModel` name | `presentation_subject_areas.business_model` (empty when unresolved, no error) |
| root `@defaultFactColumnRef` | `presentation_subject_areas.implicit_fact_column_id` (empty when absent or unresolved, no error) |
| root `@isAutoAggr` | `presentation_subject_areas.is_auto_aggregate` |
| each direct child `Alias@name` | `presentation_subject_areas.aliases` |

Not read: `RefTables/RefPresentationTable` (table order within the subject area), `@isDefaultReadable`, `@hasDispName`, `@hasDispDescription`.

### 2.8 `PresentationTable` — `_parse_presentation_table`

| Read | Feeds |
|---|---|
| root `@name` | `presentation_tables.name`, identifier |
| root `@containerRef` (points at the `PresentationCatalog`) | `presentation_tables.subject_area_id`, identifier prefix |
| root `@dispName` | `presentation_tables.display_name` |
| each direct child `Alias@name` | `presentation_tables.aliases` |
| each direct child `PresentationColumn`: `@name`, `@mdsid`, `@logicalColumnRef`, `@overrideLogicalName`, `@dispName` | one `presentation_columns` row: `name`, (bound identifier), `logical_column_id`, `override_logical_name`, `display_name` |

Not read: `@hasDispName`, `@hasDispDescription`, `RefHierarchies/RefPresentationHierarchy`, and `Alias` elements that are not direct children of the table root (observed: 6, presumably under `PresentationColumn`; unsure).

### 2.9 `PresentationHierarchy` — `_parse_presentation_hierarchy`

| Read | Feeds |
|---|---|
| root `@name` | `presentation_hierarchies.name`, identifier |
| root `@containerRef` (points at the `PresentationTable`) | `presentation_hierarchies.table_id`, identifier prefix |
| root `@logicalDimensionRef` | `presentation_hierarchies.dimension_id` |
| count of direct children `PresentationLevel` | `presentation_hierarchies.level_count` |
| each direct child `PresentationLevel`: `@name`, `@mdsid`, `@logicalLevelRef` | one `presentation_levels` row: `name`, (bound identifier), `logical_level_id`; `position` = index among `PresentationLevel` children, from 0 |
| each `RefPresentationColumn@presentationColumnRef` at any depth below a level | one `presentation_level_columns` row; `position` = index among those elements, from 0 |
| root's first direct child `RefDisplayColumns`, then `RefPresentationColumn@presentationColumnRef` below it | `presentation_hierarchies.display_column_id` (section 6.5) |

Not read: `Alias`, `PresentationLevel@containerRef`, `@hasDispName`, `@hasDispDescription`.

### 2.10 `InitBlock` — `_parse_init_block`

| Read | Feeds |
|---|---|
| root `@name` | `init_blocks.name`, identifier |
| root `@refreshPeriod` | `init_blocks.refresh_period` |
| root `@isSessionVar` | `init_blocks.is_session` |
| root `@isEnabled` | `init_blocks.is_enabled` |
| text of first direct child `InitString` | `init_blocks.init_string` |

Not read: `DBMapItem/Value` (per-database variants of the init SQL; observed 398 occurrences), `RefVariables/RefVariable`, `RefPredecessors/RefInitBlock` (execution order), `@connectionPoolRef`, `@isRowWiseInit`, `@isRowWiseCacheEnabled`, `@baseTime`, `Description`.

### 2.11 `Variable` — `_parse_variable`

| Read | Feeds |
|---|---|
| root `@name` | `variables.name`, identifier; also decides `redacted` |
| root `@isSessionVar` | `variables.is_session` |
| root `@initBlockRef` | `variables.init_block_id` |
| root `@securityMask` | `variables.security_mask` |
| first direct child `Expr` | `variables.expr_text`, `expr_text_desc`, `expr_refs` (blanked when redacted) |

Not read: `Description`.

### 2.12 Security: `Group`, `PrivilegePackage`, `ObjectPrivilege`

| Type | Read | Feeds |
|---|---|---|
| `Group` (`_parse_group`) | root `@name`, `@mdsid` | No row. Binds the identifier `<rpd>::sec::<name>` so references to the role resolve. Counted as parsed only when it has a name. |
| `PrivilegePackage` (`_parse_privilege_package`) | root `@name`, `@roleRef`; each direct child `QueryPrivilegeMapping@queryPrivilegeRef` | one `security_rls` row of kind `query_privilege` per mapping |
| `ObjectPrivilege` (`_parse_object_privilege`) | root `@name`, `@type`, `@privilegePackageRef`; first direct child `RefObjects`, its direct children `RefObject@objectRef` | one `security_rls` row of kind `object_privilege` per `RefObject` |

Not read: `Group@displayName`, `Group@guid`, `Group/RefChildGroups`, `Group/Description`, `PrivilegePackage@userRef`, `QueryPrivilegeMapping@databaseRef`. The types `QueryPrivilege` and `User` have no handler.

---

## 3. Output tables

### 3.1 Identifier rules (apply to every `row_id` and every `*_id` column)

Format (`make_id`):

```
<rpd-id>::<layer>::<part>.<part>.<part>...
```

- `<layer>` is one of `phys`, `bmm`, `pres`, `sec`, `var` for the repository (`cat` exists for the web catalog and is out of scope here).
- Parts are the objects' `name` attributes, verbatim, joined with a single `.`. Nothing is escaped, trimmed or case-folded. Names contain spaces, `#`, `:`, `/`, `(`, `$` (observed) and may contain `.`.
- `make_id` refuses, by raising `IdentifierError`: an invalid `rpd_id`, an unknown layer, zero parts, a part that is empty or only whitespace, a part that contains `::`. The handlers check for an **empty** name before calling it, so in practice the remaining ways to hit this are a whitespace-only name or a name containing `::`. In both cases the exception is not caught by the reader and the driver ends the whole run with exit code 2. See section 8.4.
- `parse_id` splits on `::` and requires exactly three pieces. It returns `(rpd_id, layer, object_path)`. The object path is not split further. The reader uses `parse_id` to take a parent's path and append to it (presentation tables, hierarchies, table sources).
- Some row identifiers append a `#suffix` to another identifier by plain string concatenation instead of going through `make_id`: `#aggr<n>`, `#map<n>`, `#filter`, and the lineage form `<id>#<id>`.

Physical container path (`_container_path`): start at the table's `@containerRef`; look the `mdsid` up in the index; take its `name`; continue with that entry's own `containerRef`; stop when there is no further container, when an entry is missing or has an empty name, or when an `mdsid` repeats (cycle guard). The names are then put outermost first. Observed: always `Database.Schema`. In the test fixture: `Schema` only. The chain is whatever the files say; the reader does not require a particular depth.

### 3.2 Cell conventions

- Every cell is a string. Attribute values are written verbatim; an absent attribute is the empty string. The reader does not interpret `true`/`false` attributes except where this guide says "compared with `true`".
- Reader-computed booleans are written `true` or `false` (`is_measure`, `redacted`, `is_value_based`, `is_ragged`, `is_skipped`, `is_grand_total`).
- Reader-computed integers are written in decimal (`source_count`, `mapped_source_count`, `level_count`, `position`).
- Lists: `expr_refs`-type cells and `time_series` and `physical_joins.columns` are joined with one space; `aliases` is joined with ` | ` (space, pipe, space).
- Rows are written sorted. Python sorts the list of row arrays, so the order is by `row_id` first and, only for equal `row_id`, by the following cells in column order. Comparison is by Unicode code point. Port note: `String.compareTo` compares UTF-16 code units, which differs from code-point order only when supplementary characters are involved.
- File conventions used by the Python writer (`csv_writer.write_table`): UTF-8 without byte-order mark, comma delimiter, LF line endings, one header row, every field quoted, embedded quotes doubled, embedded newlines kept inside the quoted field.

### 3.3 Physical layer

#### `physical_tables`

Row identifier: `<rpd>::phys::<container path>.<table name>`

Examples:
- `bi-analytics-2013::phys::Anton_Tserakhau_DW.ANTON_TSERAKHAU_DW.DIM_DISHES`
- `sampleapp::phys::xe.SAMPLESALES.FactCrossJoin`
- `testrpd::phys::SALES_SCHEMA.SAMP_REVENUE_F` (fixture, schema only)

| # | Column | Holds |
|---|---|---|
| 1 | `row_id` | identifier |
| 2 | `rpd_id` | repository identifier |
| 3 | `container` | container path joined with `.`, e.g. `Anton_Tserakhau_DW.ANTON_TSERAKHAU_DW` |
| 4 | `name` | `@name` |
| 5 | `table_type` | `@type`. Observed `none` (79) and `select` (21). `select` means the table is an SQL statement (opaque view). |
| 6 | `max_conn` | `@maxConn` |
| 7 | `source_table_id` | identifier of the table this one is an alias of; empty when the table is not an alias, and also empty when the alias link failed to resolve (then an error row exists) |

#### `physical_table_definitions`

Row identifier: `<table identifier>.def.<database>`, where `<database>` is `DBMapItem@name`, or `unnamed-<n>` when that attribute is empty (`n` = 1-based position of the `DBMapItem` among the table's `DBMapItem` children).

Examples:
- `bi-analytics-2013::phys::Anton_Tserakhau_DW.ANTON_TSERAKHAU_DW.DIM_TIME_DD.def.DefaultMulDB`
- `bi-analytics-2013::phys::Anton_Tserakhau_DW.ANTON_TSERAKHAU_DW.FCT_OPERATIONS_DD.def.DefaultMulDB`

| # | Column | Holds |
|---|---|---|
| 1 | `row_id` | identifier |
| 2 | `rpd_id` | |
| 3 | `table_id` | owning physical table |
| 4 | `database` | `DBMapItem@name` (observed: always `DefaultMulDB`) |
| 5 | `text` | `Value` text, verbatim |

Rules:
- One row per `Value`. A `DBMapItem` with no `Value` child gives one row with empty `text`. A table with no `DBMapItem` gives no row.
- A `DBMapItem` with more than one `Value` gives several rows with the same `row_id`, which is later reported as a duplicate identifier (section 8.2). Not observed.
- What the text means is decided by the table's `table_type`, never by looking at the text: for `select` it is the SQL of the view; for anything else it is the name of the object in the database, which can differ from the repository name (observed: repository table `Operations` is database object `FCT_OPERATIONS_DD`).
- The SQL is not parsed. The tables it reads from are not recorded anywhere.

#### `physical_columns`

Row identifier: `<table identifier>.<column name>`

Examples:
- `bi-analytics-2013::phys::Anton_Tserakhau_DW.ANTON_TSERAKHAU_DW.DIM_DISHES.DISH_CODE`
- `bi-analytics-2013::phys::TEAM_VAV.HR_TEAM_VAV.LOCATIONS.COUNTRY_ID`
- `testrpd::phys::SALES_SCHEMA.SAMP_REVENUE_F.00123_CODE`

| # | Column | Holds |
|---|---|---|
| 1 | `row_id` | identifier |
| 2 | `rpd_id` | |
| 3 | `table_id` | owning physical table |
| 4 | `name` | `@name` |
| 5 | `data_type` | `@dataType`, e.g. `VARCHAR`, `DOUBLE` |
| 6 | `special_type` | `@specialType`, e.g. `none` |
| 7 | `precision` | `@precision` |
| 8 | `nullable` | `@nullable` |
| 9 | `external_name` | `@extName` |

#### `physical_joins`

Two kinds share the table.

Row identifier:
- foreign key: `<table identifier>.<foreign key name>`, or `<table identifier>.fk` when the foreign key has no name
- complex join: `<rpd>::phys::joins.<join name>`

Examples:
- `bi-analytics-2013::phys::Anton_Tserakhau_DW.ANTON_TSERAKHAU_DW.Operations.Operations_Foreign Key`
- `bi-analytics-2013::phys::Anton_Tserakhau_DW.ANTON_TSERAKHAU_DW.Operations.Operations_Foreign Key#1`
- `sampleapp::phys::joins.Complex Join#1`

| # | Column | Holds |
|---|---|---|
| 1 | `row_id` | identifier |
| 2 | `rpd_id` | |
| 3 | `kind` | `foreign_key` or `complex_join` |
| 4 | `name` | `@name` |
| 5 | `table1_id` | foreign key: the table that owns the key. Complex join: `@table1Ref` resolved. |
| 6 | `table2_id` | complex join: `@table2Ref` resolved. Always empty for a foreign key. |
| 7 | `cardinality` | complex join: `@cardinality`. Empty for a foreign key. |
| 8 | `expr_text` | complex join condition, template form |
| 9 | `expr_text_desc` | complex join condition, rendered form |
| 10 | `expr_refs` | complex join condition, references |
| 11 | `columns` | foreign key only: the `mdsid` part of each `RefColumn@columnRef`, space-joined. These are raw source ids such as `mb1e1e1e1-0c8e-1000-897a-c0a838650000`, not row identifiers. An unparseable reference contributes an empty string. |

A foreign key row does not say which table or key it points at (`@counterPartKeyRef` is not read). See gotcha G9.

### 3.4 Business model layer

#### `logical_tables`

Row identifier: `<rpd>::bmm::<business model name>.<table name>`

Examples:
- `bi-analytics-2013::bmm::Anton_Tserakhau.Dishes`
- `bi-analytics-2013::bmm::Anton_Tserakhau.Operations with agregation`
- `sampleapp::bmm::SAMPLESALES.Dim Products`

| # | Column | Holds |
|---|---|---|
| 1 | `row_id` | identifier |
| 2 | `rpd_id` | |
| 3 | `business_model` | name of the business model |
| 4 | `name` | `@name` |
| 5 | `source_count` | number of `RefTableSources/RefLogicalTableSource` entries declared on the table |

#### `logical_columns`

Row identifier: `<logical table identifier>.<column name>`

Examples:
- `bi-analytics-2013::bmm::Anton_Tserakhau.Dishes.Dish code`
- `bi-analytics-2013::bmm::Anton_Tserakhau.Operations.Sales ago month ($)`
- `sampleapp::bmm::SAMPLESALES.Fact Revenue.Revenue (EUR)`

| # | Column | Holds |
|---|---|---|
| 1 | `row_id` | identifier |
| 2 | `rpd_id` | |
| 3 | `table_id` | owning logical table |
| 4 | `name` | `@name` |
| 5 | `is_writeable` | `@isWriteable` verbatim |
| 6 | `is_measure` | `true` when the column has a `MeasureDefn` child, else `false` |
| 7 | `source_count` | copy of the owning table's `source_count` |
| 8 | `mapped_source_count` | number of distinct table sources that have a column mapping for this column (section 5.2) |
| 9 | `time_series` | space-joined subset of `AGO TODATE PERIODROLLING`, in that fixed order (section 6.2) |
| 10 | `derived_expr_text` | the column's own `Expr`, template form |
| 11 | `derived_expr_text_desc` | same, rendered form |
| 12 | `derived_expr_refs` | same, references |
| 13 | `mapping_expr_text` | `AttrDefn/Expr`, template form |
| 14 | `mapping_expr_text_desc` | same, rendered form |
| 15 | `mapping_expr_refs` | same, references |

Observed: 15 of 1010 columns have a derived expression; 933 have a mapping expression; 75 have neither.

#### `aggregation_rules`

Row identifier: `<logical column identifier>#aggr<n>`, `n` = 0-based position of the `AggrRule` among the `MeasureDefn`'s `AggrRule` children.

Examples:
- `bi-analytics-2013::bmm::Anton_Tserakhau.Operations.Total Sales ($)#aggr0`
- `bi-analytics-2013::bmm::Anton_Tserakhau.Operations with agregation.FCT_UNIT_AMOUNT#aggr0`

| # | Column | Holds |
|---|---|---|
| 1 | `row_id` | identifier |
| 2 | `rpd_id` | |
| 3 | `logical_column_id` | the measure |
| 4 | `name` | `AggrRule@name`, e.g. `AggRule_2050:4118821381160259` |
| 5 | `is_default` | `AggrRule@isDefault` verbatim |
| 6 | `expr_text` | e.g. ` SUM("%1")` (the leading space is real) |
| 7 | `expr_text_desc` | e.g. ` SUM("Anton_Tserakhau"."Operations"."Total Sales ($)")` |
| 8 | `expr_refs` | references |

#### `logical_table_sources`

Row identifier: `<logical table identifier>.src.<source name>`

Examples:
- `bi-analytics-2013::bmm::Anton_Tserakhau.Dishes.src.Dishes`
- `bi-analytics-2013::bmm::Anton_Tserakhau.Restaurants.src.LTS Restaurants - Locations`
- `bi-analytics-2013::bmm::HR_TEAM_VAV.DEPARTMENTS.src.COUNTRIES`

| # | Column | Holds |
|---|---|---|
| 1 | `row_id` | identifier |
| 2 | `rpd_id` | |
| 3 | `logical_table_id` | owning logical table |
| 4 | `name` | `@name` |
| 5 | `is_active` | `@isActive` verbatim |
| 6 | `priority` | `@priorityGroup` verbatim; empty means the source file records none |
| 7 | `fragment_text` | `FragmentContent`, template form |
| 8 | `fragment_text_desc` | rendered form |
| 9 | `fragment_refs` | references |
| 10 | `where_text` | `WhereClause`, template form |
| 11 | `where_text_desc` | rendered form |
| 12 | `where_refs` | references |
| 13 | `group_by_text` | `GroupBy`, template form, e.g. ` GROUPBYLEVEL("%1")` |
| 14 | `group_by_text_desc` | rendered form |
| 15 | `group_by_refs` | references (logical level identifiers) |

#### `lts_column_map`

Row identifier: `<table source identifier>#map<n>`, `n` = 0-based position of the `ColumnMapping` among the source's `ColumnMapping` children.

Examples:
- `bi-analytics-2013::bmm::Anton_Tserakhau.Dishes.src.Dishes#map0`
- `bi-analytics-2013::bmm::Anton_Tserakhau.Dishes.src.Dishes#map1`

| # | Column | Holds |
|---|---|---|
| 1 | `row_id` | identifier |
| 2 | `rpd_id` | |
| 3 | `source_id` | owning table source |
| 4 | `logical_column_id` | first resolved reference of `LogicalColumnExpr`; empty if none |
| 5 | `physical_column_id` | first resolved reference of the mapping's `Expr`; empty if none |
| 6 | `logical_expr_text` | `LogicalColumnExpr`, template form |
| 7 | `logical_expr_text_desc` | rendered form |
| 8 | `logical_expr_refs` | references |
| 9 | `physical_expr_text` | mapping `Expr`, template form |
| 10 | `physical_expr_text_desc` | rendered form |
| 11 | `physical_expr_refs` | references |

#### `bmm_joins`

Row identifier: `<rpd>::bmm::joins.<join name>`. Not qualified by business model.

Examples:
- `bi-analytics-2013::bmm::joins.Iryna_Bradzetskaya_MT#1:Relationship_2004:4041821380622953`
- `bi-analytics-2013::bmm::joins.Iryna_Bradzetskaya_MT#1:Relationship_2004:4041851380622953`

| # | Column | Holds |
|---|---|---|
| 1 | `row_id` | identifier |
| 2 | `rpd_id` | |
| 3 | `name` | `@name` |
| 4 | `join_type` | `@type` verbatim. Observed: `Inner`, `LeftOuter`, `RightOuter`. |
| 5 | `table1_id` | `@logicalTable1Ref` resolved |
| 6 | `multiplicity1` | `@multiplicity1`. Observed: `n..n`, `0..n`, `0..1`, `1..1`. |
| 7 | `is_aggregate1` | `@isAggregate1` verbatim |
| 8 | `table2_id` | `@logicalTable2Ref` resolved |
| 9 | `multiplicity2` | `@multiplicity2` |
| 10 | `is_aggregate2` | `@isAggregate2` verbatim |

#### `logical_dimensions`

Row identifier: `<rpd>::bmm::<business model name>.<dimension name>`

Examples:
- `bi-analytics-2013::bmm::Anton_Tserakhau.Dishes: Cuisine/Type-Dish`
- `sampleapp::bmm::SAMPLESALES.Sales Rep Hierarchy`

| # | Column | Holds |
|---|---|---|
| 1 | `row_id` | identifier |
| 2 | `rpd_id` | |
| 3 | `name` | `@name` |
| 4 | `business_model` | name of the business model |
| 5 | `is_value_based` | `true` when `@isValueBased` is exactly `true` (parent-child hierarchy) |
| 6 | `is_ragged` | `true` when `@isRagged` is exactly `true` |
| 7 | `is_skipped` | `true` when `@isSkipped` is exactly `true` (skip-level) |
| 8 | `default_root_level_id` | `@defaultRootLevelRef` resolved; empty when absent or unresolved |
| 9 | `level_count` | number of `LogicalLevel` children |

#### `logical_levels`

Row identifier: `<dimension identifier>.<level name>`

Examples:
- `bi-analytics-2013::bmm::Anton_Tserakhau.Dishes: Cuisine/Type-Dish.All Dishes`
- `bi-analytics-2013::bmm::Anton_Tserakhau.Dishes: Cuisine/Type-Dish.Dish Cuisine`
- `sampleapp::bmm::SAMPLESALES.Sales Rep Hierarchy.Detail`

| # | Column | Holds |
|---|---|---|
| 1 | `row_id` | identifier |
| 2 | `rpd_id` | |
| 3 | `dimension_id` | owning dimension |
| 4 | `position` | 0-based position among the dimension's `LogicalLevel` children, in document order |
| 5 | `name` | `@name` |
| 6 | `member_count` | `@memberCount` verbatim |
| 7 | `is_grand_total` | `true` when `@isGTA` is exactly `true` |
| 8 | `drill_key_column_id` | logical column of the level's drill-down key (section 6.5); empty when none |

### 3.5 Presentation layer

#### `presentation_subject_areas`

Row identifier: `<rpd>::pres::<subject area name>`

Examples:
- `bi-analytics-2013::pres::Anton_Tserakhau`
- `bi-analytics-2013::pres::Anton_Tserakhau_demo`
- `sampleapp::pres::Sample Sales`

| # | Column | Holds |
|---|---|---|
| 1 | `row_id` | identifier |
| 2 | `rpd_id` | |
| 3 | `name` | `@name` |
| 4 | `business_model` | name of the business model behind it |
| 5 | `implicit_fact_column_id` | `@defaultFactColumnRef` resolved to a logical column identifier. Observed on 1 of 21 subject areas. |
| 6 | `is_auto_aggregate` | `@isAutoAggr` verbatim |
| 7 | `aliases` | `Alias@name` values in document order, joined with ` | ` |

#### `presentation_tables`

Row identifier: `<subject area path>.<table name>` — the subject area's object path with the table name appended.

Examples:
- `bi-analytics-2013::pres::Anton_Tserakhau.Dishes`
- `bi-analytics-2013::pres::Vasili_Korzun_DW.Fact Sales`
- `sampleapp::pres::Sample Sales.Dim Time`

| # | Column | Holds |
|---|---|---|
| 1 | `row_id` | identifier |
| 2 | `rpd_id` | |
| 3 | `subject_area_id` | owning subject area |
| 4 | `name` | `@name` |
| 5 | `display_name` | `@dispName` verbatim |
| 6 | `aliases` | `Alias@name` values, joined with ` | ` (observed: `FCT_SALES_DD`) |

#### `presentation_columns`

Row identifier: `<presentation table identifier>.<column name>`

Examples:
- `bi-analytics-2013::pres::Anton_Tserakhau.Dishes.Dish code`
- `sampleapp::pres::Sample Sales.Dim Time.Calendar Date`
- `bi-analytics-2013::pres::HR_TEAM_VAV.DEPARTMENTS.COUNTRY_ID`

| # | Column | Holds |
|---|---|---|
| 1 | `row_id` | identifier |
| 2 | `rpd_id` | |
| 3 | `table_id` | owning presentation table |
| 4 | `name` | `@name` |
| 5 | `logical_column_id` | `@logicalColumnRef` resolved; empty when absent or unresolved |
| 6 | `override_logical_name` | `@overrideLogicalName` verbatim |
| 7 | `display_name` | `@dispName` verbatim. Observed values are expressions such as `VALUEOF(NQ_SESSION.CN_Sample_Sales_Dim_Time_Calendar_Date)`, not plain captions. |

The three columns 5–7 are the rename chain (logical column, override flag, display name). They are stored separately on purpose.

#### `presentation_hierarchies`

Row identifier: `<presentation table path>.<hierarchy name>`

Examples:
- `bi-analytics-2013::pres::Anton_Tserakhau.Dishes.Dishes: Dish Cuisine - Dish`
- `sampleapp::pres::Sample Sales.Dim Sales Rep.Sales Rep Hierarchy`

| # | Column | Holds |
|---|---|---|
| 1 | `row_id` | identifier |
| 2 | `rpd_id` | |
| 3 | `table_id` | owning presentation table |
| 4 | `name` | `@name` |
| 5 | `dimension_id` | `@logicalDimensionRef` resolved; empty when absent or unresolved |
| 6 | `level_count` | number of `PresentationLevel` children |
| 7 | `display_column_id` | presentation column named directly on the hierarchy (parent-child hierarchies); otherwise empty |

#### `presentation_levels`

Row identifier: `<hierarchy identifier>.<level name>`

Examples:
- `bi-analytics-2013::pres::Anton_Tserakhau.Dishes.Dishes: Dish Cuisine - Dish.All Dishes`
- `bi-analytics-2013::pres::Anton_Tserakhau.Dishes.Dishes: Dish Cuisine - Dish.Dish Cuisine`

| # | Column | Holds |
|---|---|---|
| 1 | `row_id` | identifier |
| 2 | `rpd_id` | |
| 3 | `hierarchy_id` | owning hierarchy |
| 4 | `position` | 0-based position among the hierarchy's `PresentationLevel` children; this is the drill order |
| 5 | `name` | `@name` |
| 6 | `logical_level_id` | `@logicalLevelRef` resolved; empty when absent or unresolved |

#### `presentation_level_columns`

Row identifier: `<presentation level identifier>.<n>`, `n` = 0-based position of the `RefPresentationColumn` among all such elements under the level.

Examples:
- `bi-analytics-2013::pres::Anton_Tserakhau.Dishes.Dishes: Dish Cuisine - Dish.Dish Cuisine.0`
- `bi-analytics-2013::pres::Anton_Tserakhau.Dishes.Dishes: Dish Cuisine - Dish.Dish.0`

| # | Column | Holds |
|---|---|---|
| 1 | `row_id` | identifier |
| 2 | `rpd_id` | |
| 3 | `level_id` | owning presentation level |
| 4 | `position` | same `n` as in the identifier; display order within the level |
| 5 | `presentation_column_id` | the displayed column |

A reference that does not resolve produces an error row and no row here, but still consumes its position number, so positions can have gaps.

### 3.6 Lineage

#### `lineage`

Row identifier: `<presentation column identifier>#<table source identifier>` (plain concatenation with `#`).

Examples:
- `bi-analytics-2013::pres::Anton_Tserakhau.Dishes.Dish code#bi-analytics-2013::bmm::Anton_Tserakhau.Dishes.src.Dishes`
- `bi-analytics-2013::pres::HR_TEAM_VAV.DEPARTMENTS.COUNTRY_ID#bi-analytics-2013::bmm::HR_TEAM_VAV.DEPARTMENTS.src.COUNTRIES`
- `bi-analytics-2013::pres::HR_TEAM_VAV.DEPARTMENTS.COUNTRY_ID#bi-analytics-2013::bmm::HR_TEAM_VAV.DEPARTMENTS.src.LOCATIONS`

| # | Column | Holds |
|---|---|---|
| 1 | `row_id` | identifier |
| 2 | `rpd_id` | the presentation column's `rpd_id` |
| 3 | `presentation_column_id` | |
| 4 | `logical_column_id` | |
| 5 | `source_id` | the table source that supplies the column on this path |
| 6 | `physical_table_id` | table owning `physical_column_id`; empty when that is empty or is not a known physical column |
| 7 | `physical_column_id` | the column mapping's `physical_column_id` |

Derivation is in section 5.

### 3.7 Variables and init blocks

#### `variables`

Row identifier: `<rpd>::var::<variable name>`

Examples:
- `bi-analytics-2013::var::ACC_NEED_ACT_NUMDAYS`
- `bi-analytics-2013::var::CUBE_NAME`
- `bi-analytics-2013::var::internal - do not ship - REP OLAP PASSWORD`

| # | Column | Holds |
|---|---|---|
| 1 | `row_id` | identifier |
| 2 | `rpd_id` | |
| 3 | `name` | `@name` |
| 4 | `is_session` | `@isSessionVar` verbatim (observed: `true` or empty) |
| 5 | `init_block_id` | `@initBlockRef` resolved; empty when absent or unresolved |
| 6 | `security_mask` | `@securityMask` verbatim (observed: empty, `2`, `5`) |
| 7 | `redacted` | `true` when the value was withheld (section 6.6) |
| 8 | `expr_text` | default value, template form, e.g. `90.0d`, `'BISAMPLE'`, ` TIMESTAMP '2001-12-01 00:00:00'` |
| 9 | `expr_text_desc` | rendered form |
| 10 | `expr_refs` | references |

#### `init_blocks`

Row identifier: `<rpd>::var::init.<init block name>`

Examples:
- `bi-analytics-2013::var::init.AP Aging Latest Snapshot Date`
- `bi-analytics-2013::var::init.ASSIGN_SERVER_IP`
- `bi-analytics-2013::var::init.Verify DUAL Cube Name (=BISAMPLE)`

| # | Column | Holds |
|---|---|---|
| 1 | `row_id` | identifier |
| 2 | `rpd_id` | |
| 3 | `name` | `@name` |
| 4 | `refresh_period` | `@refreshPeriod` verbatim (observed: `86400`, `3600`, `5184000`, empty) |
| 5 | `is_session` | `@isSessionVar` verbatim |
| 6 | `is_enabled` | `@isEnabled` verbatim (observed: empty or `false`) |
| 7 | `init_string` | text of `InitString`, verbatim, usually SQL; empty when the element is absent or empty (observed: 2 of 154) |

### 3.8 Security

#### `security_rls`

Three kinds of row share the table.

| Kind | Row identifier | Example |
|---|---|---|
| `query_privilege` | `<rpd>::sec::pkg.<package name>.q<n>` | `bi-analytics-2013::sec::pkg.PrivPack_4209:4116681381160259.q0` |
| `object_privilege` | `<rpd>::sec::obj.<privilege name>.o<n>` | `bi-analytics-2013::sec::obj.ObjPriv_1500:3723951381075662.o0` |
| `row_filter` | `<table source identifier>#filter` | `bi-analytics-2013::bmm::Anton_Tserakhau.Restaurants.src.LTS Restaurants - Locations#filter` |

`n` is the 0-based position of the child element. An empty package or privilege name is replaced by `?` in the identifier.

| # | Column | `query_privilege` | `object_privilege` | `row_filter` |
|---|---|---|---|---|
| 1 | `row_id` | see above | see above | see above |
| 2 | `rpd_id` | | | the source's `rpd_id` |
| 3 | `kind` | `query_privilege` | `object_privilege` | `row_filter` |
| 4 | `role` | **name** of the object `@roleRef` points at (e.g. `Role_DW_Admin`); empty if unresolved | always empty | always empty |
| 5 | `subject_id` | **name** of the object `@queryPrivilegeRef` points at (e.g. `QueryPriv_4204:4116741381160259`); not an identifier | identifier of the object the `RefObject` points at (observed: always a subject area); empty if unresolved | logical table identifier |
| 6 | `detail` | package `@name` | `<@type> via <package identifier>`, e.g. `read via bi-analytics-2013::sec::pkg.PrivPack_4209:3723931381075662` | table source name |
| 7 | `expr_text` | empty | empty | the `WhereClause`, template form |
| 8 | `expr_text_desc` | empty | empty | rendered form |
| 9 | `expr_refs` | empty | empty | references |

Derivation details are in section 6.7.

---

## 4. Expressions

Every expression in MDS XML is a container element holding up to three children. The reader captures all three and never chooses between them (`_read_expression`, `Expression` in `model.py`).

| Form | Source | Example | Nature |
|---|---|---|---|
| Template text | text of child `ExprText` | `SUM("%1")` | Formula with positional placeholders `%1`, `%2`, ... |
| Rendered text | text of child `ExprTextDesc` | `SUM("Anton_Tserakhau"."Operations"."Total Sales ($)")` | The template with names filled in by OBIEE. Derived by OBIEE; not authoritative. |
| Reference list | child `ObjectRefList`, each direct child `RefObject@objectRef` | `…/LogicalTable/x.xml#m123` | What the placeholders bind to, in order |

Containers the reader reads this way:

| Container | Feeds |
|---|---|
| `LogicalColumn/Expr` | `logical_columns.derived_expr_*` |
| `LogicalColumn/AttrDefn/Expr` | `logical_columns.mapping_expr_*` |
| `LogicalColumn/MeasureDefn/AggrRule/Expr` | `aggregation_rules.expr_*` |
| `LogicalTableSource/FragmentContent` | `logical_table_sources.fragment_*` |
| `LogicalTableSource/WhereClause` | `logical_table_sources.where_*`, `security_rls.expr_*` |
| `LogicalTableSource/GroupBy` | `logical_table_sources.group_by_*` |
| `LogicalTableSource/ColumnMapping/LogicalColumnExpr` | `lts_column_map.logical_expr_*` |
| `LogicalTableSource/ColumnMapping/Expr` | `lts_column_map.physical_expr_*` |
| `Variable/Expr` | `variables.expr_*` |
| `ComplexJoin/Expr` | `physical_joins.expr_*` |

Note that `FragmentContent`, `WhereClause`, `GroupBy` and `LogicalColumnExpr` are themselves the container; they do not wrap an `Expr`.

Exact algorithm:

1. Start with `text = ""`, `text_desc = ""`, `refs = []`.
2. Walk the container's **direct** children in document order.
3. `ExprText`: `text` = the element's text. `ExprTextDesc`: `text_desc` = the element's text. If either tag occurs more than once, the last one wins.
4. `ObjectRefList`: for each direct child with local name `RefObject`, take `@objectRef`, apply the reference rule of section 1.5, and look up the bound identifier.
   - Found: append the identifier to `refs`.
   - Not found (reference did not parse, or nothing bound for that `mdsid` yet): append **nothing** and record one error row of kind `unresolved_reference` (section 8.1). The raw source id is deliberately not written to the output.
5. Other children are ignored. `RefObject@refId` and `@objectTypeId` are not read.
6. When the container element is absent, all three forms are empty.

Whitespace handling:

- Text is taken verbatim (`observing.read_text`). No trim, no collapsing of spaces, no line-ending change, no case change. An element with no text gives the empty string.
- Tests pin these: a leading space (` SUM("%1")`), a double space in the middle (`"MONTH_DT" <  TIMESTAMP`), a trailing space (`Ago("%1" ,  "%2" , 1) ` observed), an embedded newline, embedded double quotes and commas (`-1 * "%1", "x"` + newline + `next`), and a view definition with leading space, line breaks and trailing space.
- The text is usually inside a CDATA section. Python sees the element text with CDATA already merged.
- The XML parser itself converts CRLF to LF inside element text. That is standard XML behaviour in both Python and Java, and is the only change made.

Port notes:

- For these leaf elements use `getTextContent()`. Python's `element.text` is the text before the first child element; the elements read here have no child elements in any observed file, so the two agree. Unsure what Python returns if such an element contained a comment between two text runs; not exercised.
- "Empty" for an expression (`Expression.is_empty`) means both `text` and `text_desc` are empty after trimming whitespace. The trim is used only for this test, never for the stored value.

How the three forms are written:

- `*_text` and `*_text_desc` cells hold the strings verbatim.
- The `*_refs` cell holds the identifiers joined with one space. Identifiers contain spaces (observed in 201 of 1010 logical column identifiers), so this cell cannot be split back reliably by splitting on spaces. See gotcha G2.
- A reference that failed to resolve is missing from the list, so the remaining entries shift left and no longer line up with `%1`, `%2`, ... See gotcha G3.

---

## 5. Lineage

Lineage is built after all type passes, from the rows already produced. It reads no XML.

### 5.1 Steps (`_build_lineage`)

1. Build a lookup: physical column identifier → physical table identifier, from `physical_columns`.
2. Group the `lts_column_map` rows by `logical_column_id`, keeping their order. Rows with an empty `logical_column_id` are left out.
3. For each presentation column, in the order produced:
   - If its `logical_column_id` is empty, skip it.
   - For each column mapping of that logical column, emit one lineage row:
     - `row_id` = presentation column identifier + `#` + mapping's `source_id`
     - `rpd_id` = presentation column's `rpd_id`
     - `presentation_column_id`, `logical_column_id` from the presentation column
     - `source_id` from the mapping
     - `physical_column_id` = the mapping's `physical_column_id`
     - `physical_table_id` = lookup of step 1, or empty

So the path is: presentation column → logical column (attribute `logicalColumnRef`) → each table source that maps that logical column (`ColumnMapping`) → the first physical reference of that mapping.

### 5.2 Multi-source logical columns

A logical column mapped by three table sources gives three lineage rows per presentation column that exposes it. They are never collapsed.

Observed: 1473 presentation columns have 1 row, 43 have 2, 2 have 3, 2 have 4. Example with three: `HR_TEAM_VAV.DEPARTMENTS.COUNTRY_ID` through sources `COUNTRIES`, `DEPARTMENTS`, `LOCATIONS`.

`mapped_source_count` (`_count_mapped_sources`): for each logical column, the number of **distinct** `source_id` values among column mappings whose `logical_column_id` is that column. It is different from `source_count`, which is the number of sources declared on the table. A table with four sources may map a given column in only two. Observed: 53 columns have `mapped_source_count` < `source_count`.

Tests pin: for every logical column, the number of distinct sources in its lineage rows equals `mapped_source_count`, not `source_count`.

### 5.3 Derived columns with no physical column

A logical column defined only by its own formula (`derived_expr_*`) has no column mapping in any table source. Therefore:

- `mapped_source_count` is `0`.
- No lineage row is produced for any presentation column that exposes it. Observed: 23 of 1543 presentation columns have no lineage row.
- The absence of a row is the signal. There is no "computed" marker row. To trace such a column, read `logical_columns.derived_expr_refs`, which lists the logical columns the formula uses, and follow those.

A mapping whose physical expression has no resolvable reference (a constant, for example) still produces a lineage row, with `physical_column_id` and `physical_table_id` empty. Read from the code; not observed in the samples.

When a mapping's physical expression combines several columns, lineage records only the first resolved reference. The others are in `lts_column_map.physical_expr_refs` only. See gotcha G4.

### 5.4 Aliases (`source_table_id`)

In OBIEE an alias is a second physical table object that stands for another physical table. It is a full `PhysicalTable` file with its own columns, plus `@sourceTableRef` pointing at the original.

- The alias and its columns get ordinary rows in `physical_tables` and `physical_columns`, under the alias's own name.
- `_resolve_alias_sources` runs after all type passes, because an alias file can sort before the table it points at. For every `PhysicalTable` file with a non-empty `@sourceTableRef`: if the alias itself produced a row, resolve the reference to the original's identifier and store it in `physical_tables.source_table_id`.
- If the reference does not resolve, `source_table_id` stays empty and an `unresolved_reference` error row is recorded with the alias's identifier as `source_id`.
- If the alias itself failed to parse (it already has its own error), nothing more is recorded.
- Lineage is **not** rewritten. A lineage row ends at the alias table and the alias's column. To reach the underlying table, join `lineage.physical_table_id` to `physical_tables.row_id` and follow `source_table_id`. Observed: 120 lineage rows end at an alias; 8 of 100 tables are aliases.
- There is no column-level alias link: `PhysicalColumn@sourceColumnRef` is not read. The alias column and the original column usually share a name, but the reader does not establish that.
- Chains (an alias of an alias) are not followed; each row records one hop.

### 5.5 Opaque views and `physical_table_definitions`

- A physical table with `table_type` = `select` is an SQL statement, not a database table. Its SQL is in `physical_table_definitions.text`, one row per database type.
- Lineage ends at that physical table like any other. The SQL is not parsed, so the real tables behind the view are not in any CSV. Observed: 521 lineage rows end at a `select` table.
- For other table types the definition row, when present, gives the database object name, which can differ from the repository name.
- For tables typed `none` with no definition row, the repository cannot say whether the object is a table, a view or a synonym.

---

## 6. Other derivations

### 6.1 Aggregation rules

- A column is a measure when it has a `MeasureDefn` child. That is the only test.
- Each `AggrRule` under the `MeasureDefn` becomes its own row, in document order, numbered from 0. Rules are never merged. A measure can have one default rule and further rules that override it for specific dimensions.
- `is_default` is the `@isDefault` attribute verbatim.
- The expression is the rule's `Expr`, three forms. A rule with no `Expr` gives three empty cells. A rule with no `ObjectRefList` gives an empty `expr_refs`; references are never inherited from a sibling rule (pinned by a test).
- A `MeasureDefn` with no `AggrRule` gives `is_measure` = `true` and no rule rows.
- Observed: all 35 rules are default; texts are ` SUM("%1")` (32), ` COUNT("%1")`, ` AVG("%1")`, ` MAX("%1")`, each with a leading space. A non-default rule exists only in the hand-written test case (`LAST("%1")`).
- Which dimension an override applies to is not captured as a separate column. Unsure where the source records it; the hand-written test case does not show it.

### 6.2 Time-series flag (`time_series_used`, applied in `_flag_time_series`)

For each logical column, build one string: `derived.text + " " + derived.text_desc + " " + mapping.text + " " + mapping.text_desc`. For each of `AGO`, `TODATE`, `PERIODROLLING`, in that order, test case-insensitively:

```
\b<NAME>\s*\(
```

Each name that matches is listed once. The cell is the matches joined with a space (for example `AGO`, or `AGO TODATE`).

- Only the column's own two expressions are searched. Aggregation rules and table-source expressions are not.
- It is a text search, so a quoted name that happens to look like `AGO (` would match.
- Observed: `Ago("%1" ,  "%2" , 1) ` and `ago ("%1",    "%2"  , 1 )` both flag `AGO`.

Port note: Python's `\b` and `\s` are Unicode-aware. Use `Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE | Pattern.UNICODE_CHARACTER_CLASS` to match.

### 6.3 Table source priority, fragment, where, group-by

- `priority` = `@priorityGroup` verbatim. Empty means the file records no priority. It must not be defaulted and must not be inferred from file or document order (pinned by a test). Observed: 91 of 92 sources have none; one has `1`.
- `fragment_*` = `FragmentContent`. Observed: present on every source and never populated. The hand-written case has `"%1" <  TIMESTAMP '2020-01-01 00:00:00'`.
- `where_*` = `WhereClause`, the source's content filter. Observed populated on 6 sources; they are row-level security predicates.
- `group_by_*` = `GroupBy`, the aggregation grain. Observed: ` GROUPBYLEVEL("%1")` with a reference to a logical level.
- An element that is present but empty and an element that is absent both produce three empty cells. The CSV cannot tell them apart.
- `is_active` = `@isActive` verbatim. Inactive sources are extracted like any other and take part in lineage.

### 6.4 Join types

- Logical joins: `join_type` is `@type` verbatim; no mapping to a fixed vocabulary. The side of an outer join is relative to `table1_id` / `table2_id` as written in the file.
- `multiplicity1` / `multiplicity2` are verbatim strings.
- Physical complex joins carry `cardinality` and a condition expression but no join type.
- Physical foreign keys carry neither.

### 6.5 Hierarchies

Two layers exist and are linked by identifier:

```
presentation_hierarchies.dimension_id      -> logical_dimensions.row_id
presentation_levels.logical_level_id       -> logical_levels.row_id
presentation_level_columns.presentation_column_id -> presentation_columns.row_id
logical_levels.drill_key_column_id         -> logical_columns.row_id
```

Logical dimension:

- Flags: `is_ragged` (branches may stop above the lowest level), `is_skipped` (a member may have no parent at the level directly above), `is_value_based` (parent-child). Each is `true` only when the attribute is exactly the string `true`; anything else, including absent, is `false`.
- `level_count` counts all `LogicalLevel` children, including any skipped for having no name.
- Levels: `position` is the 0-based index among `LogicalLevel` children in document order. A level with no name is skipped with an error but still consumes its position.
- `is_grand_total` from `@isGTA`.
- Drill key (`drill_key_column_id`): walk the level's direct `LogicalKey` children in order. Ignore keys whose `@isForDrillDown` is not exactly `true`. In the first remaining key, walk every `RefLogicalColumn` at any depth and take the first whose `@logicalColumnRef` resolves to a bound identifier. If that key yields nothing, try the next drill-down key. Empty if none. A multi-column key yields only its first resolvable column. No error is recorded when nothing resolves.
- Observed: 20 of 106 levels have no drill key; these are the 20 grand-total levels.

Presentation hierarchy:

- Levels in document order, `position` from 0. Observed order for one hierarchy: `All Dishes` 0, `Dish Cuisine` 1, `Dish` 2 — top to bottom.
- Display columns of a level: every `RefPresentationColumn` at any depth under the level (in the files they sit under `RefDisplayColumns`), in document order, one `presentation_level_columns` row each.
- Parent-child hierarchy: it has no `PresentationLevel` children at all, so `level_count` is `0` and there are no level rows. Its display column sits directly on the hierarchy. The reader takes the hierarchy root's first **direct** `RefDisplayColumns` child and, inside it, the first `RefPresentationColumn` that resolves; that goes to `display_column_id`. Reading only the direct child prevents a level's columns being picked up twice.
- The reader does this for every hierarchy, not only for value-based ones. A level-based hierarchy normally has no direct `RefDisplayColumns`, so the cell stays empty.
- Observed: one parent-child case. Dimension `sampleapp::bmm::SAMPLESALES.Sales Rep Hierarchy` has `is_value_based` = `true` and two logical levels (`Total`, `Detail`); its presentation hierarchy has `level_count` = `0` and `display_column_id` = `sampleapp::pres::Sample Sales.Dim Sales Rep.Employee Name`.
- The physical-side helpers of a parent-child hierarchy (member key, parent key, closure table) are not read.

### 6.6 Variables, init blocks, and credential redaction

Init blocks are processed before variables so that `variables.init_block_id` resolves.

Variable:

1. Identifier `<rpd>::var::<name>`, bound to the variable's `mdsid`.
2. Read `Expr` (three forms). Unresolved references inside it are recorded as errors as usual, even if the value is then withheld.
3. Redaction test (`is_credential_name`): search the variable **name**, case-insensitively, anywhere in the string, for

   ```
   password|passwd|pwd|secret|token|credential|apikey|api[_ -]?key
   ```

4. If it matches: replace the expression by three empty strings, set `redacted` = `true`, and record one gap row of kind `redacted_value`, object type `Variable`, detail exactly `<name> — value withheld: the name indicates a credential (NFR11)` (the dash is U+2014).
5. The row is always written, so the variable stays visible.

Pinned by tests: `REP OLAP PASSWORD`, `db secret`, `PASSWORD`, `db_pwd`, `API Key`, `auth token`, `client secret` are redacted; `CURRENT_YEAR`, `Passed`, `REGION` are not.

Limits of the redaction, read from the code:

- Only the variable name is tested. A credential in a variable with an innocent name is written out.
- `init_blocks.init_string` is never redacted and init block names are not tested. An init block's SQL is written verbatim.
- Other places a secret could sit (connection pool attributes) are not read at all, so they are not written.

Session versus repository variable: `is_session` is `@isSessionVar` verbatim; empty means the attribute is absent (observed for repository variables). Observed: 103 session variables of 345.

Init block: all columns are verbatim attributes plus the `InitString` text. Which variables an init block fills is recorded only from the variable side (`variables.init_block_id`). Execution order between init blocks and the connection pool used are not read.

### 6.7 Security rows

The source spreads security over several objects:

```
Group (application role)
  <- PrivilegePackage (@roleRef)
       |- QueryPrivilegeMapping (@queryPrivilegeRef) -> QueryPrivilege
       '- (referenced by) ObjectPrivilege (@privilegePackageRef, @type) -> RefObjects/RefObject
row-level predicate: WhereClause on a LogicalTableSource
```

`query_privilege` rows (`_parse_privilege_package`), one per `QueryPrivilegeMapping` child:

- `role` = the `name` of the indexed object `@roleRef` points at. This is a plain name, looked up with `index.get`, not an identifier.
- `subject_id` = the `name` of the indexed object `@queryPrivilegeRef` points at. Also a plain name. (`QueryPrivilege` has no table, but its elements are in the index.)
- `detail` = the package's `@name`.
- The package's `mdsid` is bound to `<rpd>::sec::pkg.<name or ?>` whether or not it has mappings.
- A package with no `QueryPrivilegeMapping` produces **no row**. Its role link is then not in any table.

`object_privilege` rows (`_parse_object_privilege`), one per `RefObjects/RefObject`:

- If there is no `RefObjects` or it has no `RefObject`, exactly one row is still written, numbered `o0`, with empty `subject_id`.
- `subject_id` = bound identifier of the `@objectRef` target; empty when unresolved. No error row is recorded for that.
- `detail` = `@type` + ` via ` + the bound identifier of `@privilegePackageRef` (empty string if unresolved or absent). Observed `@type` values: `none`, `read`, `write`.
- `role` is always empty. To find the role, take the package identifier from `detail` and match its name against `query_privilege` rows' `detail`.

`row_filter` rows (`_collect_row_filters`), built after all passes from `logical_table_sources`: one row for every table source whose `WhereClause` is not empty (section 4 definition of empty). `subject_id` is the logical table, `detail` the source name, the expression is the where clause. `role` is empty: the role names are inside the predicate text, typically as `Position(';<role>;' in ';' || valueof(NQ_SESSION.ROLEGUIDS) || ';') > 0`, and are not extracted.

Observed counts: 119 `object_privilege`, 73 `query_privilege`, 6 `row_filter`.

---

## 7. Processing order

### 7.1 Sequence (`load`)

| Step | Function | What it does |
|---|---|---|
| 0a | `_single_repository_files` | Discover and sort files |
| 0b | `_report_undiscovered` | Gap rows for files the walk missed |
| 0c | — | One `unreadable` error row per directory that could not be listed |
| 0d | `_parse_all` | Parse every file once; `malformed_xml` error rows |
| 0e | `_build_index` | For every element with a non-empty `@mdsid` in every parsed file, of every type: store tag, `@name`, and the `mdsid` part of `@containerRef` |
| 1 | `_parse_physical` | All `PhysicalTable` files |
| 2 | `_parse_complex_join` | All `ComplexJoin` files |
| 3 | `_parse_logical` | All `LogicalTable` files |
| 4 | `_parse_subject_area` | All `PresentationCatalog` files |
| 5 | `_parse_presentation_table` | All `PresentationTable` files |
| 6 | `_parse_dimension` | All `Dimension` files |
| 7 | `_parse_presentation_hierarchy` | All `PresentationHierarchy` files |
| 8 | `_parse_table_source` | All `LogicalTableSource` files |
| 9 | `_parse_bmm_join` | All `LogicalComplexJoin` files |
| 10 | `_parse_init_block` | All `InitBlock` files |
| 11 | `_parse_variable` | All `Variable` files |
| 12 | `_parse_group` | All `Group` files |
| 13 | `_parse_privilege_package` | All `PrivilegePackage` files |
| 14 | `_parse_object_privilege` | All `ObjectPrivilege` files |
| 15 | `_resolve_alias_sources` | Fill `physical_tables.source_table_id` |
| 16 | `_count_mapped_sources` | Fill `logical_columns.mapped_source_count` |
| 17 | `_flag_time_series` | Fill `logical_columns.time_series` |
| 18 | `_build_lineage` | Build `lineage` |
| 19 | `_collect_row_filters` | Add `row_filter` rows to `security_rls` |
| 20 | `_audit_unread` | Gap rows for unread attributes, elements and text |
| 21 | — | For each discovered file: handled type → nothing; index-only type → count parsed; other → `unmodelled_type` gap row |
| 22 | `Estate.check_identifier_uniqueness` (called by the driver after all repositories are merged) | `duplicate_identifier` error rows |

Within each of steps 1–14 the files are visited in the sorted order of step 0a.

### 7.2 Why the order matters

An identifier can be looked up only after the handler that builds it has run. The dependencies the order satisfies:

| Needs | Provided by |
|---|---|
| Container and business-model **names** | step 0e (available to every pass) |
| Physical column identifiers, for mapping expressions and aggregation rules | step 1, before 3 and 8 |
| Physical table identifiers, for complex joins | step 1, before 2 |
| Logical column identifiers, for implicit fact column, presentation columns, drill keys, column mappings | step 3, before 4, 5, 6, 8 |
| Subject area identifiers, for presentation tables | step 4, before 5 |
| Presentation table and column identifiers, for hierarchies | step 5, before 7 |
| Logical dimension and level identifiers, for presentation hierarchies and for `GroupBy` references | step 6, before 7 and 8 |
| Logical table identifiers, for table sources and logical joins | step 3, before 8 and 9 |
| Init block identifiers, for variables | step 10, before 11 |
| Privilege package identifiers, for object privileges | step 13, before 14 |
| All physical table identifiers, for alias links | steps 1–14, before 15 |

Within one logical table file, a column's identifier is bound before its own expressions are read, so a column that references itself resolves (the usual aggregation rule ` SUM("%1")` refers to its own column).

### 7.3 References the order does not satisfy

These fail in the Python implementation because the target is bound later. Each produces an `unresolved_reference` error with object type `ExpressionRef`, and the reference is dropped from the `*_refs` cell.

| Reference | Why it fails | Observed |
|---|---|---|
| A logical column formula referencing a logical level (the second argument of `AGO`, `TODATE`, `PERIODROLLING`) | Logical tables (step 3) are read before dimensions (step 6) | 2 columns, e.g. `…Operations.Sales ago month ($)`; message says `reference to Dimension` |
| A logical column formula referencing a column of another logical table whose file sorts later | Files within step 3 are read in file-name order | 1 column, `sampleapp::bmm::SAMPLESALES.Dim Products.Prod Dsc (Translated)` |
| A logical column formula referencing a column that appears later in the same table file | Columns are bound one by one in document order | Not observed; follows from the code |
| A logical column formula referencing a variable | Variables are step 11 | 1 column, `sampleapp::bmm::SAMPLESALES.Fact Revenue.Preferred Currency` |
| Any expression referencing a physical complex join, a logical join, an object privilege, or an element with no handler (keys, measure definitions, connection pools, databases, schemas, business models) | Those `mdsid`s are never bound | Not observed |

These four observed rows are the only repository-side errors in the sample output. They are ordering artefacts, not faults in the source files.

Port note: binding every identifier in a first sweep and reading expressions in a second sweep would resolve all of the first four cases. It would also change the output: the four error rows disappear and the affected `*_refs` cells gain entries. If the goal is output identical to the Python CSVs, reproduce the order above and sort the files the same way.

---

## 8. Errors, gaps and duplicates

Two separate outputs exist and must stay separate: **errors** ("something that should have been captured was not") and **gaps** ("content the reader does not model"). The principle throughout: record the problem with its source and reason, continue, never guess a value.

### 8.1 Error rows — `errors.csv`

Columns, in order: `rpd_id`, `kind`, `source_id`, `object_type`, `reason`. No `row_id`. Rows are sorted by all five cells in that order.

Every call to `record_error` also adds 1 to the `skipped` coverage counter of `(rpd_id, object_type)`.

| Kind | When | `source_id` | `object_type` | `reason` | Effect on rows |
|---|---|---|---|---|---|
| `unreadable` | A type directory cannot be listed | the corpus root path | `Directory` | `cannot list <dir>: <OS message>` (or `cannot probe …` in the several-repositories shape) | Files in it are not read |
| `malformed_xml` | A file is not well-formed XML | file name without directory | directory name | the XML parser's message | No rows from the file |
| `missing_name` | `PhysicalTable` has no `@name`, or its container chain is empty | `@mdsid` or `?` | `PhysicalTable` | `table name or container could not be resolved` | No table row, and none of its columns, definitions or foreign keys |
| `missing_name` | `PhysicalColumn` has no `@name` | column `@mdsid` or `?` | `PhysicalColumn` | `column has no name` | That column skipped |
| `missing_name` | `ComplexJoin` has no `@name` | `@mdsid` or `?` | `ComplexJoin` | `join has no name` | No row |
| `missing_name` | `LogicalTable` has no `@name` | `@mdsid` or `?` | `LogicalTable` | `table has no name` | No table row and none of its columns |
| `unresolved_reference` | `LogicalTable@subjectAreaRef` does not lead to a named business model | `@mdsid` or `?` | `LogicalTable` | `business model for logical table '<name>' could not be resolved` | No table row and none of its columns |
| `missing_name` | `LogicalColumn` has no `@name` | column `@mdsid` or `?` | `LogicalColumn` | `column has no name` | That column skipped |
| `missing_name` | `PresentationCatalog` has no `@name` | `@mdsid` or `?` | `PresentationCatalog` | `no name` | No row |
| `unresolved_reference` | `PresentationTable` has no `@name`, or `@containerRef` does not resolve to a subject area identifier | `@mdsid` or `?` | `PresentationTable` | `table name or owning Subject Area could not be resolved` | No table row and none of its columns |
| `missing_name` | `PresentationColumn` has no `@name` | column `@mdsid` or `?` | `PresentationColumn` | `no name` | That column skipped |
| `unresolved_reference` | `PresentationColumn@logicalColumnRef` is a well-formed reference but nothing is bound for it | `<table identifier>.<column name>` | `PresentationColumn` | `logical column reference does not resolve to a known logical column` | Row **is** written, with empty `logical_column_id`. The column is counted both skipped and parsed. |
| `missing_name` | `Dimension` has no `@name` | `@mdsid` or `?` | `Dimension` | `no name` | No dimension row, no level rows |
| `unresolved_reference` | `Dimension@subjectAreaRef` does not lead to a named business model | `@mdsid` or `?` | `Dimension` | `business model for dimension '<name>' could not be resolved` | No dimension row, no level rows |
| `missing_name` | `LogicalLevel` has no `@name` | level `@mdsid` or `?` | `LogicalLevel` | `no name` | That level skipped (position still consumed) |
| `unresolved_reference` | `PresentationHierarchy` has no `@name`, or `@containerRef` does not resolve to a presentation table identifier | `@mdsid` or `?` | `PresentationHierarchy` | `hierarchy name or owning presentation table could not be resolved` | No hierarchy, level or level-column rows |
| `missing_name` | `PresentationLevel` has no `@name` | level `@mdsid` or `?` | `PresentationLevel` | `no name` | That level and its display columns skipped |
| `unresolved_reference` | A level's `RefPresentationColumn` does not resolve | presentation level identifier | `RefPresentationColumn` | `display column reference does not resolve to a known column` | That display column skipped (position still consumed) |
| `unresolved_reference` | `LogicalTableSource` has no `@name`, or `@logicalTableRef` does not resolve | `@mdsid` or `?` | `LogicalTableSource` | `source name or owning logical table could not be resolved` | No source row, none of its column mappings, hence no lineage through it |
| `missing_name` | `LogicalComplexJoin` has no `@name` | `@mdsid` or `?` | `LogicalComplexJoin` | `no name` | No row |
| `missing_name` | `Variable` has no `@name` | `@mdsid` or `?` | `Variable` | `no name` | No row |
| `missing_name` | `InitBlock` has no `@name` | `@mdsid` or `?` | `InitBlock` | `no name` | No row |
| `unresolved_reference` | A `RefObject` in any expression does not resolve (section 4) | identifier of the owning row: logical column (for derived, mapping and aggregation rule expressions), table source (fragment, where, group-by, column mappings), variable. For a physical complex join it is the join's **name**, not an identifier. | `ExpressionRef` | `reference to <Type> could not be resolved to a Knowledge Base identifier`, where `<Type>` is the directory name inside the reference, or `unknown type` if the reference did not parse | Row is written; the reference is missing from `*_refs` |
| `unresolved_reference` | `PhysicalTable@sourceTableRef` is present but does not resolve | the alias's table identifier | `PhysicalTable` | `this table is an alias, but the table it is an alias of could not be resolved to a Knowledge Base identifier` | Table row kept, `source_table_id` empty |
| `duplicate_identifier` | Two or more rows of one table share a `row_id` | the shared identifier | the table's internal name (section 8.2) | see 8.2 | Both rows are kept |

Python formats the quoted name in the two "business model …" messages with `repr`, so the name appears in single quotes (`'Fact Revenue'`), with Python escaping if the name itself contains a quote.

Situations that produce **no** error and silently leave a cell empty: unresolved `table1Ref`/`table2Ref` on either join type; unresolved or absent `subjectAreaRef` on a subject area; unresolved `defaultFactColumnRef`; unresolved `logicalDimensionRef`, `logicalLevelRef`, `defaultRootLevelRef`; unresolved drill key column; unresolved hierarchy-level display column on the hierarchy root; unresolved `initBlockRef`; unresolved `roleRef`, `queryPrivilegeRef`, `privilegePackageRef`, or `RefObject` under an object privilege; a `Group` with no name (it is just not counted); a foreign key with no name or an unparseable column reference.

Port note: `malformed_xml` reasons and `unreadable` reasons contain parser and operating-system text. They will not be byte-identical between Python and Java.

### 8.2 Duplicate identifiers (`check_identifier_uniqueness`)

Identifiers are built from names, and OBIEE does not make names unique. After everything is extracted, every row list is checked:

1. For each table, in alphabetical order of its internal name, walk the rows in production order.
2. Count rows whose `row_id` was already seen in that table.
3. For each such identifier, in sorted order, add one error row:
   - `rpd_id` = that of the first row with the identifier
   - `kind` = `duplicate_identifier`
   - `source_id` = the identifier
   - `object_type` = the table's internal name
   - `reason` = `<n> rows in <table> share this identifier; a Group Spec citing it cannot name which one. The identifier does not carry a distinction the source made.` where `<n>` is the total number of rows sharing it (2 for a pair)

Rules:

- The check is per table. The same identifier string in two different tables is not reported (see gotcha G6).
- All colliding rows stay in the output. Nothing is renamed or dropped.
- This error is **not** counted as skipped in coverage, because the objects did parse.
- The internal table names are the Python list names. They differ from the CSV names in three cases: `subject_areas` (CSV `presentation_subject_areas`), `column_mappings` (CSV `lts_column_map`), `security_rules` (CSV `security_rls`).
- Both references to colliding objects resolve to the same identifier, so `*_id` cells pointing at them are ambiguous.

Constructions that can collide, read from the code: two physical tables with the same name in the same container; two `Value` children in one `DBMapItem`, or two `DBMapItem` with the same name; two unnamed foreign keys on one table; two column mappings for the same logical column in one table source (gives two lineage rows with the same identifier); same-named joins; same-named variables.

History the design carries: three different schemas named `SH` under three databases, with two different tables named `COSTS`, are the reason the physical identifier uses the whole container chain; four dimensions named `Times` are the reason a dimension is qualified by its business model. Observed duplicates in the sample output on the repository side: none.

### 8.3 Gap rows — `unparsed.csv`

Columns, in order: `rpd_id`, `kind`, `object_type`, `detail`, `occurrences`. Sorted by all cells.

| Kind | When | `object_type` | `detail` | `occurrences` |
|---|---|---|---|---|
| `unmodelled_type` | A file in a type directory that is neither handled nor index-only | directory name | file name | `1` (one row per file); also adds 1 to `skipped` coverage |
| `undiscovered_file` | A file under the corpus root that discovery did not pick up | `File` | path relative to the corpus root | `1`. The `rpd_id` cell holds the **corpus root directory name**, not the declared `rpd_id` (see gotcha G12). |
| `redacted_value` | A variable's value was withheld | `Variable` | `<name> — value withheld: the name indicates a credential (NFR11)` | `1` |
| `unread_attribute` | An attribute present on an element of a handled file was never read by a handler | directory name | `<Tag>@<attribute>` | number of elements |
| `unread_element` | An element of a handled file where nothing in its whole subtree was read or looked for | directory name | `<Tag>` | number of elements |
| `unread_text` | An element of a handled file with non-whitespace text that no handler read | directory name | `<Tag>#text` | number of elements |

The last three come from an audit (`observing.audit_unread`) that observes what the handlers actually touched instead of comparing against a declared list:

- Each element records the attribute names a handler asked for (whether or not the attribute exists), whether its text was read, and whether a handler looked for it by tag (`_first_child`, `_children`, `_descendants`).
- Reads made while building the index (step 0e) do not count.
- An element is "reached" if it or anything below it was touched. Unreached elements are reported as `unread_element`. Unread attributes and unread text are reported at every level, including inside unreached subtrees.
- Counts are grouped by `(rpd_id, object type directory, detail)`.
- Files of unmodelled types are not audited; they are already reported whole.

Port note: Java DOM has no hook for this. To keep the audit, wrap element access in your own accessor class that records reads. Decide whether the new project needs the audit before building it; without it, a new attribute in a future export is ignored silently.

### 8.4 Conditions that stop the run

| Condition | Exception |
|---|---|
| Corpus root missing or not a directory; wrong root; no XML found; `base/` unlistable | `CorpusError` |
| `rpd_id` not matching the pattern | `IdentifierError` |
| A name used as an identifier part is whitespace-only or contains `::` | `IdentifierError` from `make_id`, raised in the middle of a pass |

The driver catches both exception types, prints `error: <message>` and exits with code 2 without writing any output. So a single object whose name contains `::` prevents the whole extraction. Port note: decide whether to keep this or to turn it into a per-object error row.

### 8.5 Coverage counters

`coverage.csv` (`rpd_id`, `layer`, `object_type`, `parsed`, `skipped`; `layer` is `rpd` for everything here). One `parsed` is added per row-producing object: `PhysicalTable`, `PhysicalColumn`, `PhysicalForeignKey`, `ComplexJoin`, `LogicalTable`, `LogicalColumn`, `AggrRule`, `PresentationCatalog`, `PresentationTable`, `PresentationColumn`, `Dimension`, `LogicalLevel`, `PresentationHierarchy`, `PresentationLevel`, `LogicalTableSource`, `ColumnMapping`, `LogicalComplexJoin`, `Variable`, `InitBlock`, `Group`, `PrivilegePackage`, `ObjectPrivilege`, and one per file for the index-only types. Not counted: definition rows, presentation level columns, lineage rows, security rows. `skipped` gets 1 per error row (under the error's `object_type`, so pseudo-types such as `ExpressionRef`, `RefPresentationColumn` and `Directory` appear) and 1 per unmodelled file.

---

## 9. Gotchas

**G1. The index is keyed by `mdsid` alone.** If two repositories are loaded into one index (the several-repositories shape of `load`), equal `mdsid` values in different repositories overwrite each other and references resolve across repositories. The driver avoids this by loading one repository at a time. Build one index per repository.

**G2. `*_refs` cells cannot be split on spaces.** Identifiers are joined with one space, and identifiers contain spaces. `…Operations with agregation.EVENT_DT` is one identifier. A consumer can only split approximately (for example before each occurrence of `<rpd-id>::`). Port note: a different separator would fix this and would change the file format; decide deliberately.

**G3. An unresolved reference shifts the list.** `refs` holds only resolved identifiers. If `%1` resolves and `%2` does not, the list has one entry; if `%1` does not and `%2` does, the list also has one entry and it belongs to `%2`. The only signal is the error row. Observed: `Ago("%1" ,  "%2" , 1) ` with one reference.

**G4. `logical_column_id` and `physical_column_id` of a column mapping are "the first resolved reference".** They are not validated to be a logical or a physical column. If the first reference fails and the second resolves, the second is used. If the physical expression is a calculation over several columns, only the first is used, and lineage shows only that one.

**G5. Verbatim means verbatim.** Do not trim, collapse or normalise expression text, SQL, init strings, names or attribute values. Leading spaces on aggregation rules and trailing spaces on formulas are real and tests check them.

**G6. Different object kinds share identifier shapes.** A logical dimension and a logical table are both `<rpd>::bmm::<model>.<name>`; a logical level and a logical column are both `<rpd>::bmm::<model>.<x>.<y>`; a presentation hierarchy and a presentation column are both `<rpd>::pres::<area>.<table>.<name>`; a foreign key and a physical column are both `<table>.<name>`. Same-named objects of two kinds get the same string. The duplicate check does not see it because they are in different tables. An identifier alone does not tell you which table it belongs to.

**G7. Identifiers are not parseable by splitting on `.`.** Names are not escaped. Use the `table_id`, `subject_area_id`, `dimension_id`, `hierarchy_id`, `level_id`, `source_id` columns to navigate, never string surgery on `row_id`. The `#` in a name (`Complex Join#1`, `Operations_Foreign Key#1`, alias `Anton_Tserakhau#1`) is OBIEE's own suffix and is unrelated to the `#aggr`, `#map`, `#filter` suffixes the reader appends.

**G8. Attribute names that mislead.** `subjectAreaRef` on `LogicalTable`, `Dimension` and `PresentationCatalog` points at a `BusinessModel`. The MDS type `PresentationCatalog` is a subject area; it has nothing to do with the web catalog (the report store). `containerRef` means schema for a physical table, subject area for a presentation table, presentation table for a hierarchy.

**G9. `physical_joins.columns` holds raw `mdsid` values.** It is the one place a source-format id reaches the output. It lists the foreign key's own columns, not the referenced key's, and `table2_id` is empty, so a foreign key row alone does not describe a join. Port note: resolving these to column identifiers and reading `@counterPartKeyRef` would make the row usable; that is a change from the reference.

**G10. `priority` empty is not "lowest".** Never default it and never derive an order from file or document position.

**G11. `source_count` and `mapped_source_count` are different numbers.** Check lineage row counts against `mapped_source_count`.

**G12. `undiscovered_file` rows carry the corpus directory name in `rpd_id`.** `_report_undiscovered` writes `corpus_root.name`. When the repository sits in an estate folder as `<estate>/rpd/…`, that value is `rpd`.

**G13. Logical level `position` is document order, not necessarily depth.** Observed in a dimension with two branches: `All Dishes` 0, then `Dish` 2 and `Dish Cuisine` 3, while the matching presentation hierarchy orders `Dish Cuisine` above `Dish`. The real parent-child structure is in `RefChildLevels`, which is not read. Use `presentation_levels.position` for drill order. The Python model comment states that document order is the drill order; the sample output does not support that for logical levels.

**G14. A value-based dimension can still have logical levels.** Observed: two (`Total`, `Detail`). "No levels" is true of its presentation hierarchy only.

**G15. `role` and `subject_id` on `query_privilege` rows are names, not identifiers.** Every other `*_id` column holds an identifier or is empty.

**G16. Object type comes from the directory, not the root tag.** A file in the wrong directory is handled by the wrong handler.

**G17. A row can be written and counted as an error at once.** A presentation column with an unresolved logical column is written with an empty `logical_column_id` and has an error row. An index-only file that is malformed is counted both skipped (by the error) and parsed (by step 21).

**G18. Binding with an empty `mdsid`.** Handlers call `index.bind(mdsid, id)` even when the `mdsid` attribute is missing, storing under the empty key. Most lookups guard against an empty `mdsid`, but the two join handlers and `_resolve_alias_sources` do not: `index.identifier("")` could then return whatever was last bound under the empty key. Not observed (every object has an `mdsid`). Port note: do not bind an empty key.

**G19. Duplicate `mdsid` values.** The index keeps the last element stored for a given `mdsid`. The index walk is not in document order. Do not rely on which one wins.

**G20. Boolean tests are exact string comparisons with `true`.** `TRUE`, `1` or `yes` count as false for `is_value_based`, `is_ragged`, `is_skipped`, `is_grand_total`, and for the `isForDrillDown` filter.

**G21. `display_name` is not a caption.** Observed values are session-variable expressions used for translation.

**G22. Names are compared and stored case-sensitively everywhere.**

**G23. Whitespace test in `make_id`.** Python's `strip()` treats more characters as whitespace than Java's `String.strip()` (for example U+00A0). A name consisting only of such characters is refused by Python.

**G24. A source with an inactive flag, or a where clause, still feeds lineage.** Lineage does not look at `is_active`, `priority`, fragment or where.

**G25. Row filters appear twice by design.** Once in `logical_table_sources.where_*` and once as a `row_filter` row in `security_rls`. Every non-empty where clause becomes a `row_filter` row whether or not it mentions a role.

---

## 10. Known gaps in the Python reader

From `documents/implementation-artifacts/deferred-work.md`, checked against the code and the sample output. Two entries in that file are out of date; that is stated where it applies.

Still open:

1. **Row filters carry no role.** `row_filter` rows have an empty `role`. The predicate is captured; which application role it belongs to is only inside the predicate text.
2. **`PrivilegePackage@userRef` is not read.** A grant made to a named user instead of a role loses its grantee. Observed: 3 packages, each with no `roleRef` and no children, so they produce no row at all. Fixing it also needs the `User` type, which has no handler.
3. **Logical keys and table-source joins are not modelled.** `LogicalKey` on logical tables (which columns form the key) and `Join` / `Link` inside a `LogicalTableSource` (the physical join path the source uses) are not read. No table carries either.
4. **Further unread references**, largest first in the sample: `PhysicalForeignKey@counterPartKeyRef` (100), `QueryPrivilegeMapping@databaseRef` (73), `RefPresentationHierarchy@presentationHierarchyRef` (58), `Join@physicalJoinRef` (38), `InitBlock@connectionPoolRef` (6).
5. **Lineage stops at the object OBIEE names.** For `select` tables the SQL is now captured but not parsed, so the tables it reads are unknown. For `none` tables the repository cannot tell a table from a view or a synonym; that needs database metadata, not repository files.
6. **Alias columns.** `PhysicalColumn@sourceColumnRef` (observed 101) is not read.

Out of date in `deferred-work.md` (the code has moved on):

- "Alias tables are not modelled": `PhysicalTable@sourceTableRef` is now read into `physical_tables.source_table_id` (section 5.4). Only the column-level link (item 6) remains open.
- "The view SQL in `DBMapItem/Value` is not captured": it is now captured for physical tables in `physical_table_definitions` (section 5.5).

Further gaps found while reading the code and the sample output, not listed in `deferred-work.md`:

7. Forward references fail (section 7.3), which drops the level argument of every `AGO`/`TODATE`/`PERIODROLLING` formula from `derived_expr_refs`.
8. `InitBlock/DBMapItem/Value` (per-database init SQL, observed 398) is not read; only `InitString` is.
9. `RefInitBlock@predecessorRef` (init block execution order) and `RefVariable@variableRef` are not read.
10. `RefChildLevels` (level parent-child structure), `LogicalKey@isChronKey`, `LogicalKey@isPrimary` and `Dimension@isTimeDim` are not read. Time-series functions depend on the chronological key.
11. `PhysicalKey` (primary keys) and `PhysicalColumn@scale` are not read.
12. A privilege package with no query-privilege mapping leaves no row, so the role behind an object privilege that points at it cannot be found.
13. Which dimension a non-default aggregation rule applies to is not captured as data.
14. `Description` text on tables, variables, init blocks and groups is not read.
15. `Group@displayName` and the group hierarchy (`RefChildGroups`) are not read; roles appear only as names on `query_privilege` rows.
16. Presentation ordering (`RefTables` on a subject area, `RefHierarchies` on a table) is not read; presentation tables and columns are written in identifier order, not display order.
17. The types `QueryPrivilege`, `User`, `Project`, `LogicalSourceFolder` have no handler. `Database`, `Schema`, `ConnectionPool` and `BusinessModel` contribute names only; database type, features and connection details are not written anywhere.
18. Present-but-empty and absent expression containers are indistinguishable in the output (section 6.3).
