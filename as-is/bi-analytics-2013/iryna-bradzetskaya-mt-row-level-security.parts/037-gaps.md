## What the export did not capture

The figures below come from the errors and unparsed tables of the Knowledge Base and from the manifest; they carry no marker.

### Errors

None: the extraction recorded no error about an object of this subject area (errors table).

### Totals of source content that no table holds

| Kind | Rows | Occurrences in the source |
|---|---|---|
| All kinds together | 107 | 25205 |
| unread_attribute | 82 | 22368 |
| unread_element | 21 | 2160 |
| unread_text | 4 | 677 |

A row is one attribute, element or text of one kind of object; its occurrences are counted over the whole estate, not only this subject area.

### What was not read, per kind of object (unparsed table)

| Kind | Object | Rows | Occurrences | What was not read (occurrences) |
|---|---|---|---|---|
| unread_attribute | Dimension | 7 | 1041 | LogicalKey@mdsid (127); LogicalLevel@isFDUC (84); LogicalLevel@levelConst (84); RefLogicalColumn@logicalColumnRef (268); RefLogicalColumn@refId (396); RefLogicalLevel@logicalLevelRef (5); RefLogicalLevel@refId (77) |
| unread_attribute | Group | 4 | 23 | Group@displayName (9); Group@guid (10); RefGroup@groupRef (2); RefGroup@refId (2) |
| unread_attribute | InitBlock | 10 | 1171 | DBMapItem@name (396); InitBlock@baseTime (50); InitBlock@connectionPoolRef (4); InitBlock@iconIndex (3); InitBlock@isRowWiseCacheEnabled (80); InitBlock@isRowWiseInit (12); RefInitBlock@predecessorRef (44); RefInitBlock@refId (44); RefVariable@refId (269); RefVariable@variableRef (269) |
| unread_attribute | LogicalComplexJoin | 1 | 75 | LogicalComplexJoin@mdsid (75) |
| unread_attribute | LogicalTable | 16 | 6103 | AggrRule@mdsid (22); AttrDefn@mdsid (933); AttrDefn@name (933); Expr@mdsid (968); Expr@name (968); LogicalColumn@customSortOrderRef (6); LogicalColumn@isDerived (13); LogicalKey@mdsid (57); MeasureDefn@isCommutative (22); MeasureDefn@mdsid (22); MeasureDefn@name (22); RefLogicalColumn@refId (57); RefLogicalTableSource@logicalTableSourceRef (75); RefLogicalTableSource@refId (75); RefObject@objectTypeId (965); RefObject@refId (965) |
| unread_attribute | LogicalTableSource | 12 | 8004 | Expr@mdsid (940); Expr@name (940); FragmentContent@mdsid (75); FragmentContent@name (75); GroupBy@mdsid (75); GroupBy@name (75); LogicalColumnExpr@mdsid (940); LogicalColumnExpr@name (940); RefObject@objectTypeId (1897); RefObject@refId (1897); WhereClause@mdsid (75); WhereClause@name (75) |
| unread_attribute | ObjectPrivilege | 3 | 284 | ObjectPrivilege@mdsid (62); RefObject@objectTypeId (111); RefObject@refId (111) |
| unread_attribute | PhysicalTable | 7 | 330 | PhysicalForeignKey@mdsid (82); PhysicalKey@mdsid (63); PhysicalTable@isCacheable (6); PhysicalTable@lastUpdated (10); PhysicalTable@pollFreq (8); PhysicalTable@rowCount (10); RefColumn@refId (151) |
| unread_attribute | PresentationCatalog | 5 | 282 | PresentationCatalog@hasDispDescription (18); PresentationCatalog@hasDispName (18); PresentationCatalog@isDefaultReadable (10); RefPresentationTable@presentationTableRef (118); RefPresentationTable@refId (118) |
| unread_attribute | PresentationHierarchy | 7 | 981 | Alias@name (14); PresentationHierarchy@hasDispDescription (53); PresentationHierarchy@hasDispName (53); PresentationLevel@containerRef (226); PresentationLevel@hasDispDescription (226); PresentationLevel@hasDispName (226); RefPresentationColumn@refId (183) |
| unread_attribute | PresentationTable | 7 | 3316 | Alias@name (6); PresentationColumn@hasDispDescription (1484); PresentationColumn@hasDispName (1484); PresentationTable@hasDispDescription (118); PresentationTable@hasDispName (118); RefPresentationHierarchy@presentationHierarchyRef (53); RefPresentationHierarchy@refId (53) |
| unread_attribute | PrivilegePackage | 1 | 72 | QueryPrivilegeMapping@databaseRef (72) |
| unread_attribute | Variable | 2 | 686 | Expr@mdsid (343); Expr@name (343) |
| unread_element | Dimension | 4 | 347 | RefLogicalColumn (268); RefLogicalColumns (69); RefLogicalLevel (5); RefPreferredDrillDown (5) |
| unread_element | Group | 3 | 16 | Description (12); RefChildGroups (2); RefGroup (2) |
| unread_element | InitBlock | 7 | 1307 | DBMapItem (396); Description (21); RefInitBlock (44); RefPredecessors (42); RefVariable (269); RefVariables (139); Value (396) |
| unread_element | PresentationCatalog | 2 | 136 | RefPresentationTable (118); RefTables (18) |
| unread_element | PresentationHierarchy | 1 | 14 | Alias (14) |
| unread_element | PresentationTable | 3 | 92 | Alias (6); RefHierarchies (33); RefPresentationHierarchy (53) |
| unread_element | Variable | 1 | 248 | Description (248) |
| unread_text | Group | 1 | 12 | Description#text (12) |
| unread_text | InitBlock | 2 | 417 | Description#text (21); Value#text (396) |
| unread_text | Variable | 1 | 248 | Description#text (248) |

Each item above was not read by the extraction.

### Left out of the Slice (manifest)

- Rows of the errors table that are not about an object of this subject area: 0.
- Rows of the unparsed table about whole object types or files with no reader, or about kinds of object the Slice does not hold: 104 (2 redacted_value, 102 unmodelled_type).
  - Kind redacted_value: 1 object type, 2 rows, 2 occurrences: Variable, 2 rows, 2 occurrences.
  - Kind unmodelled_type (a whole type of object that has no reader; nothing of such an object is in the Knowledge Base): 2 object types, 102 rows, 102 objects: Project, 30 rows, 30 objects; QueryPrivilege, 72 rows, 72 objects.
- Rows in the Slice only so that a reference from another row resolves: 0.
