## What the export did not capture

The rows below come from the errors and unparsed tables of the Slice and from its manifest; they have no Row identifier.

### Errors

None: the extraction recorded no error about an object of this subject area (errors).

### Totals of source content that no table holds

From the unparsed table:

- All kinds together: 95 rows, 1769 occurrences in the source.
- Kind unread_attribute: 78 rows, 1674 occurrences.
- Kind unread_element: 15 rows, 91 occurrences.
- Kind unread_text: 2 rows, 4 occurrences.

A row is one attribute, element or text of one kind of object; its occurrences are how often it is in the source over the whole estate, not only in this subject area.

### Not read, per kind of object (whole estate)

Each item below was not read. The number in brackets is how often it occurs.

| Kind | Object | Rows | Occurrences | Not read |
|---|---|---|---|---|
| unread_attribute | ComplexJoin | 3 | 12 | ComplexJoin@mdsid (x4); Expr@mdsid (x4); Expr@name (x4) |
| unread_attribute | Dimension | 7 | 179 | LogicalKey@mdsid (x24); LogicalLevel@isFDUC (x22); LogicalLevel@levelConst (x22); RefLogicalColumn@logicalColumnRef (x34); RefLogicalColumn@refId (x58); RefLogicalLevel@logicalLevelRef (x1); RefLogicalLevel@refId (x18) |
| unread_attribute | Group | 4 | 14 | Group@displayName (x5); Group@guid (x5); RefGroup@groupRef (x2); RefGroup@refId (x2) |
| unread_attribute | InitBlock | 5 | 9 | DBMapItem@name (x2); InitBlock@connectionPoolRef (x2); InitBlock@isRowWiseInit (x1); RefVariable@refId (x2); RefVariable@variableRef (x2) |
| unread_attribute | LogicalComplexJoin | 1 | 9 | LogicalComplexJoin@mdsid (x9) |
| unread_attribute | LogicalTable | 13 | 177 | AggrRule@mdsid (x13); Expr@mdsid (x15); Expr@name (x15); LogicalColumn@isDerived (x2); LogicalKey@mdsid (x8); MeasureDefn@isCommutative (x13); MeasureDefn@mdsid (x13); MeasureDefn@name (x13); RefLogicalColumn@refId (x12); RefLogicalTableSource@logicalTableSourceRef (x17); RefLogicalTableSource@refId (x17); RefObject@objectTypeId (x19); RefObject@refId (x20) |
| unread_attribute | LogicalTableSource | 12 | 894 | Expr@mdsid (x95); Expr@name (x95); FragmentContent@mdsid (x17); FragmentContent@name (x17); GroupBy@mdsid (x17); GroupBy@name (x17); LogicalColumnExpr@mdsid (x95); LogicalColumnExpr@name (x95); RefObject@objectTypeId (x206); RefObject@refId (x206); WhereClause@mdsid (x17); WhereClause@name (x17) |
| unread_attribute | ObjectPrivilege | 3 | 22 | ObjectPrivilege@mdsid (x6); RefObject@objectTypeId (x8); RefObject@refId (x8) |
| unread_attribute | PhysicalTable | 10 | 89 | PhysicalColumn@lastUpdated (x2); PhysicalColumn@rowCount (x2); PhysicalForeignKey@mdsid (x18); PhysicalKey@mdsid (x14); PhysicalTable@cacheExpiry (x1); PhysicalTable@iconIndex (x4); PhysicalTable@isCacheable (x4); PhysicalTable@lastUpdated (x4); PhysicalTable@rowCount (x4); RefColumn@refId (x36) |
| unread_attribute | PresentationCatalog | 4 | 26 | PresentationCatalog@hasDispDescription (x3); PresentationCatalog@hasDispName (x3); RefPresentationTable@presentationTableRef (x10); RefPresentationTable@refId (x10) |
| unread_attribute | PresentationHierarchy | 6 | 87 | PresentationHierarchy@hasDispDescription (x5); PresentationHierarchy@hasDispName (x5); PresentationLevel@containerRef (x20); PresentationLevel@hasDispDescription (x20); PresentationLevel@hasDispName (x20); RefPresentationColumn@refId (x17) |
| unread_attribute | PresentationTable | 6 | 148 | PresentationColumn@hasDispDescription (x59); PresentationColumn@hasDispName (x59); PresentationTable@hasDispDescription (x10); PresentationTable@hasDispName (x10); RefPresentationHierarchy@presentationHierarchyRef (x5); RefPresentationHierarchy@refId (x5) |
| unread_attribute | PrivilegePackage | 2 | 4 | PrivilegePackage@userRef (x3); QueryPrivilegeMapping@databaseRef (x1) |
| unread_attribute | Variable | 2 | 4 | Expr@mdsid (x2); Expr@name (x2) |
| unread_element | Dimension | 4 | 54 | RefLogicalColumn (x34); RefLogicalColumns (x18); RefLogicalLevel (x1); RefPreferredDrillDown (x1) |
| unread_element | Group | 3 | 6 | Description (x2); RefChildGroups (x2); RefGroup (x2) |
| unread_element | InitBlock | 4 | 8 | DBMapItem (x2); RefVariable (x2); RefVariables (x2); Value (x2) |
| unread_element | PresentationCatalog | 2 | 13 | RefPresentationTable (x10); RefTables (x3) |
| unread_element | PresentationTable | 2 | 10 | RefHierarchies (x5); RefPresentationHierarchy (x5) |
| unread_text | Group | 1 | 2 | Description#text (x2) |
| unread_text | InitBlock | 1 | 2 | Value#text (x2) |

Whether any of the items not read under LogicalTableSource or PrivilegePackage would change what this document says about sources or permissions is not recorded (open question Q13).

### Left out of the Slice

From the manifest:

- Rows of the Knowledge Base's errors table that are not about an object of this subject area: 0.
- Rows of the unparsed table about whole object types or files with no reader, or about kinds of object the Slice does not hold: 15 (15 unmodelled_type). These are whole types of object that have no reader; nothing of such an object is in the Knowledge Base. Per object type:

| Object type not read | Rows | Objects |
|---|---|---|
| LogicalSourceFolder | 11 | 11 |
| QueryPrivilege | 1 | 1 |
| User | 3 | 3 |

- Rows that are in the Slice only so that a reference from another row resolves: 0.
