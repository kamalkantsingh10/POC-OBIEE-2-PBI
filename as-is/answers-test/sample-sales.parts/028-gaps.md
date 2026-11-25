## What the export did not capture

### Errors

None: the extraction recorded no error about an object of this subject area (from the errors table; no Row identifier).

### Source content that no table holds

Totals, from the unparsed table: all kinds together 95 rows, 1769 occurrences in the source; unread_attribute 78 rows, 1674 occurrences; unread_element 15 rows, 91 occurrences; unread_text 2 rows, 4 occurrences. A row is one attribute, element or text of one kind of object; its occurrences count the whole estate, not only this subject area. Each item below was not read.

| Kind | In object | Not read (occurrences) |
|---|---|---|
| unread_attribute | ComplexJoin (3 rows, 12) | ComplexJoin@mdsid (4); Expr@mdsid (4); Expr@name (4) |
| unread_attribute | Dimension (7 rows, 179) | LogicalKey@mdsid (24); LogicalLevel@isFDUC (22); LogicalLevel@levelConst (22); RefLogicalColumn@logicalColumnRef (34); RefLogicalColumn@refId (58); RefLogicalLevel@logicalLevelRef (1); RefLogicalLevel@refId (18) |
| unread_attribute | Group (4 rows, 14) | Group@displayName (5); Group@guid (5); RefGroup@groupRef (2); RefGroup@refId (2) |
| unread_attribute | InitBlock (5 rows, 9) | DBMapItem@name (2); InitBlock@connectionPoolRef (2); InitBlock@isRowWiseInit (1); RefVariable@refId (2); RefVariable@variableRef (2) |
| unread_attribute | LogicalComplexJoin (1 row, 9) | LogicalComplexJoin@mdsid (9) |
| unread_attribute | LogicalTable (13 rows, 177) | AggrRule@mdsid (13); Expr@mdsid (15); Expr@name (15); LogicalColumn@isDerived (2); LogicalKey@mdsid (8); MeasureDefn@isCommutative (13); MeasureDefn@mdsid (13); MeasureDefn@name (13); RefLogicalColumn@refId (12); RefLogicalTableSource@logicalTableSourceRef (17); RefLogicalTableSource@refId (17); RefObject@objectTypeId (19); RefObject@refId (20) |
| unread_attribute | LogicalTableSource (12 rows, 894) | Expr@mdsid (95); Expr@name (95); FragmentContent@mdsid (17); FragmentContent@name (17); GroupBy@mdsid (17); GroupBy@name (17); LogicalColumnExpr@mdsid (95); LogicalColumnExpr@name (95); RefObject@objectTypeId (206); RefObject@refId (206); WhereClause@mdsid (17); WhereClause@name (17) |
| unread_attribute | ObjectPrivilege (3 rows, 22) | ObjectPrivilege@mdsid (6); RefObject@objectTypeId (8); RefObject@refId (8) |
| unread_attribute | PhysicalTable (10 rows, 89) | PhysicalColumn@lastUpdated (2); PhysicalColumn@rowCount (2); PhysicalForeignKey@mdsid (18); PhysicalKey@mdsid (14); PhysicalTable@cacheExpiry (1); PhysicalTable@iconIndex (4); PhysicalTable@isCacheable (4); PhysicalTable@lastUpdated (4); PhysicalTable@rowCount (4); RefColumn@refId (36) |
| unread_attribute | PresentationCatalog (4 rows, 26) | PresentationCatalog@hasDispDescription (3); PresentationCatalog@hasDispName (3); RefPresentationTable@presentationTableRef (10); RefPresentationTable@refId (10) |
| unread_attribute | PresentationHierarchy (6 rows, 87) | PresentationHierarchy@hasDispDescription (5); PresentationHierarchy@hasDispName (5); PresentationLevel@containerRef (20); PresentationLevel@hasDispDescription (20); PresentationLevel@hasDispName (20); RefPresentationColumn@refId (17) |
| unread_attribute | PresentationTable (6 rows, 148) | PresentationColumn@hasDispDescription (59); PresentationColumn@hasDispName (59); PresentationTable@hasDispDescription (10); PresentationTable@hasDispName (10); RefPresentationHierarchy@presentationHierarchyRef (5); RefPresentationHierarchy@refId (5) |
| unread_attribute | PrivilegePackage (2 rows, 4) | PrivilegePackage@userRef (3); QueryPrivilegeMapping@databaseRef (1) |
| unread_attribute | Variable (2 rows, 4) | Expr@mdsid (2); Expr@name (2) |
| unread_element | Dimension (4 rows, 54) | RefLogicalColumn (34); RefLogicalColumns (18); RefLogicalLevel (1); RefPreferredDrillDown (1) |
| unread_element | Group (3 rows, 6) | Description (2); RefChildGroups (2); RefGroup (2) |
| unread_element | InitBlock (4 rows, 8) | DBMapItem (2); RefVariable (2); RefVariables (2); Value (2) |
| unread_element | PresentationCatalog (2 rows, 13) | RefPresentationTable (10); RefTables (3) |
| unread_element | PresentationTable (2 rows, 10) | RefHierarchies (5); RefPresentationHierarchy (5) |
| unread_text | Group (1 row, 2) | Description#text (2) |
| unread_text | InitBlock (1 row, 2) | Value#text (2) |

### Object types left out

From the manifest: 0 rows of the errors table are not about an object of this subject area. 15 rows of the unparsed table concern whole object types that have no reader (kind unmodelled_type), so nothing of such an object is in the Knowledge Base: LogicalSourceFolder (11 rows, 11 objects), QueryPrivilege (1 row, 1 object), User (3 rows, 3 objects). 0 rows are in the subject area only so that a reference from another row resolves.

Whether any of the items not read holds a condition on a source or the holder of a privilege package is not recorded (open question Q13).
