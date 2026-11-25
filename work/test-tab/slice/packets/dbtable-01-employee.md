# Physical table "EMPLOYEE"
Unit: dbtable-01-employee
Kind: physical_table
Tab: test-tab (subject areas: Sample Sales; estate sampleapp)
Facts from: physical_columns, physical_keys, physical_tables
Rows of this packet a document must cover: 6. Each is on a line that ends with (REQUIRED).
To cite a fact, copy the marker in square brackets that follows it, exactly as written. "->" points at another row.

## The table, as the repository knows it

- Physical table "EMPLOYEE" in `xe.SAMPLESALES` (the containers above it in the repository, outermost first; usually database and schema); type `none`. [kb:sampleapp::phys::xe.SAMPLESALES.EMPLOYEE] (REQUIRED)
    - It is not an alias of another physical table.
    - Description in the repository: not recorded.
    - Its object in the database: no separate name is recorded, so the repository knows it only by the name above. (physical_table_definitions; no Row identifier)
- Columns the repository lists for it: 4, of which mapped by a logical column of this slice: 4; not mapped by any: 0. Keys: 1. (counted; no Row identifier)
- Logical table sources of this slice that read it: 1.
    - Source "LTS Employee" of logical table "Dim Sales Rep" (unit table-06-dim-sales-rep) -> sampleapp::bmm::SAMPLESALES.Dim Sales Rep.src.LTS Employee

## Keys

- Key "EMP_ID" of physical table "EMPLOYEE": made of column(s) "EMP_ID". [kb:sampleapp::phys::xe.SAMPLESALES.EMPLOYEE.key.EMP_ID] (REQUIRED)
    - Key 1 of 1 the repository declares for the table. The repository does not say whether a physical key is the primary key; this is the only key it declares for the table. Foreign keys of this slice that point at exactly these column(s): 1.

## Foreign keys and joins (each is stated in full in the joins unit)

- complex_join "Complex Join#2" between "FactCrossJoin" and "EMPLOYEE"; its condition is in the joins unit. -> sampleapp::phys::joins.Complex Join#2
- Foreign key "EMP_PARENT_CHILD_FKey" of "EMP_PARENT_CHILD" ("ANCESTOR_KEY") TO this table ("EMP_ID"). -> sampleapp::phys::xe.SAMPLESALES.EMP_PARENT_CHILD.EMP_PARENT_CHILD_FKey

## Columns, in the order the repository lists them

- Each line below is one column: its place, name, data type, precision (length), scale (digits after the decimal point), whether it may be empty (nullable), its external name, and whether a logical column of this slice is mapped from it. "not recorded" means the repository says nothing; it does not mean no, and it does not mean 0.
- About nullable: the repository export writes the nullable attribute only as true; it never writes false. "nullable not stated" therefore means the attribute is absent: most likely the column is not nullable in the repository, but the file does not say so, and the repository is not proof of the constraint in the database.
- Column 1 "EMP_ID": type DOUBLE; precision 15; scale not recorded; nullable not stated; external name `samplesales`; MAPPED by logical column(s) "Dim Sales Rep"."Employee ID". [kb:sampleapp::phys::xe.SAMPLESALES.EMPLOYEE.EMP_ID] (REQUIRED)
- Column 2 "EMP_NAME": type VARCHAR; precision 40; scale not recorded; nullable true; external name `samplesales`; MAPPED by logical column(s) "Dim Sales Rep"."Employee Name". [kb:sampleapp::phys::xe.SAMPLESALES.EMPLOYEE.EMP_NAME] (REQUIRED)
- Column 3 "MANAGER_ID": type DOUBLE; precision 15; scale not recorded; nullable true; external name `samplesales`; MAPPED by logical column(s) "Dim Sales Rep"."Manager ID". [kb:sampleapp::phys::xe.SAMPLESALES.EMPLOYEE.MANAGER_ID] (REQUIRED)
- Column 4 "OFFICE_ID": type DOUBLE; precision 15; scale not recorded; nullable true; external name `samplesales`; MAPPED by logical column(s) "Dim Sales Rep"."Office ID". [kb:sampleapp::phys::xe.SAMPLESALES.EMPLOYEE.OFFICE_ID] (REQUIRED)

