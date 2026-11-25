# Physical table "EMP_PARENT_CHILD"
Unit: dbtable-02-emp-parent-child
Kind: physical_table
Tab: test-tab (subject areas: Sample Sales; estate sampleapp)
Facts from: physical_columns, physical_keys, physical_table_definitions, physical_tables
Rows of this packet a document must cover: 7. Each is on a line that ends with (REQUIRED).
To cite a fact, copy the marker in square brackets that follows it, exactly as written. "->" points at another row.

## The table, as the repository knows it

- Physical table "EMP_PARENT_CHILD" in `xe.SAMPLESALES` (the containers above it in the repository, outermost first; usually database and schema); type `none`. [kb:sampleapp::phys::xe.SAMPLESALES.EMP_PARENT_CHILD] (REQUIRED)
    - It is not an alias of another physical table.
    - Description in the repository: not recorded.
    - It is the relationship table of a parent-child hierarchy (one row per member and ancestor). Member column: "EMP_PARENT_CHILD"."MEMBER_KEY"; parent (ancestor) column: "EMP_PARENT_CHILD"."ANCESTOR_KEY"; distance column (steps between the two): "EMP_PARENT_CHILD"."DISTANCE"; leaf-flag column: "EMP_PARENT_CHILD"."IS_LEAF". [kb:sampleapp::phys::xe.SAMPLESALES.EMP_PARENT_CHILD]
    - Its object in the database for database type `DefaultMulDB`: `EMP_PARENT_CHILD` [kb:sampleapp::phys::xe.SAMPLESALES.EMP_PARENT_CHILD.def.DefaultMulDB] (REQUIRED)
- Columns the repository lists for it: 4, of which mapped by a logical column of this slice: 1; not mapped by any: 3. Keys: 1. (counted; no Row identifier)
- Logical table sources of this slice that read it: 1.
    - Source "LTS Employee" of logical table "Dim Sales Rep" (unit table-06-dim-sales-rep) -> sampleapp::bmm::SAMPLESALES.Dim Sales Rep.src.LTS Employee

## Keys

- Key "EMP_PARENT_CHILD_Key" of physical table "EMP_PARENT_CHILD": made of column(s) "MEMBER_KEY". [kb:sampleapp::phys::xe.SAMPLESALES.EMP_PARENT_CHILD.key.EMP_PARENT_CHILD_Key] (REQUIRED)
    - Key 1 of 1 the repository declares for the table. The repository does not say whether a physical key is the primary key; this is the only key it declares for the table. Foreign keys of this slice that point at exactly these column(s): 1.

## Foreign keys and joins (each is stated in full in the joins unit)

- Foreign key "EMP_PARENT_CHILD_FKey" FROM this table ("ANCESTOR_KEY") to "EMPLOYEE" ("EMP_ID"). -> sampleapp::phys::xe.SAMPLESALES.EMP_PARENT_CHILD.EMP_PARENT_CHILD_FKey
- Foreign key "SAMP_REVENUE_F_FKey#4" of "SAMP_REVENUE_F" ("EMPLOYEE_KEY") TO this table ("MEMBER_KEY"). -> sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F.SAMP_REVENUE_F_FKey#4

## Columns, in the order the repository lists them

- Each line below is one column: its place, name, data type, precision (length), scale (digits after the decimal point), whether it may be empty (nullable), its external name, and whether a logical column of this slice is mapped from it. "not recorded" means the repository says nothing; it does not mean no, and it does not mean 0.
- About nullable: the repository export writes the nullable attribute only as true; it never writes false. "nullable not stated" therefore means the attribute is absent: most likely the column is not nullable in the repository, but the file does not say so, and the repository is not proof of the constraint in the database.
- Column 1 "MEMBER_KEY": type DOUBLE; precision 15; scale not recorded; nullable not stated; external name not recorded; NOT MAPPED by any logical column of this slice. [kb:sampleapp::phys::xe.SAMPLESALES.EMP_PARENT_CHILD.MEMBER_KEY] (REQUIRED)
- Column 2 "ANCESTOR_KEY": type DOUBLE; precision 15; scale not recorded; nullable not stated; external name not recorded; NOT MAPPED by any logical column of this slice. [kb:sampleapp::phys::xe.SAMPLESALES.EMP_PARENT_CHILD.ANCESTOR_KEY] (REQUIRED)
- Column 3 "DISTANCE": type INT; precision not recorded; scale not recorded; nullable not stated; external name not recorded; MAPPED by logical column(s) "Dim Sales Rep"."Distance". [kb:sampleapp::phys::xe.SAMPLESALES.EMP_PARENT_CHILD.DISTANCE] (REQUIRED)
- Column 4 "IS_LEAF": type INT; precision not recorded; scale not recorded; nullable not stated; external name not recorded; NOT MAPPED by any logical column of this slice. [kb:sampleapp::phys::xe.SAMPLESALES.EMP_PARENT_CHILD.IS_LEAF] (REQUIRED)

