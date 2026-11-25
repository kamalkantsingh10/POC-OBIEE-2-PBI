# Logical table "Dim Sales Rep"
Unit: table-06-dim-sales-rep
Kind: logical_table
Tab: test-tab (subject areas: Sample Sales; estate sampleapp)
Facts from: lineage, logical_columns, logical_keys, logical_table_sources, logical_tables, lts_column_map, lts_joins, lts_tables, presentation_columns
Rows of this packet a document must cover: 6. Each is on a line that ends with (REQUIRED).
To cite a fact, copy the marker in square brackets that follows it, exactly as written. "->" points at another row.

## The table

- Logical table "Dim Sales Rep" in business model "SAMPLESALES"; table sources the repository declares for it: 1. [kb:sampleapp::bmm::SAMPLESALES.Dim Sales Rep] (REQUIRED)
- Columns of it in this slice: 5, of which measures: 0; not confirmed for the tab: 4. (counted; no Row identifier)
- Table sources of it in this slice: 1: "LTS Employee". Each is described below under its own heading, with the physical tables it reads and what it maps each column from. (counted; no Row identifier)
- Key "EMP_ID" of logical table "Dim Sales Rep": marked as primary key true; made of column(s) "Employee ID". [kb:sampleapp::bmm::SAMPLESALES.Dim Sales Rep.key.EMP_ID] (REQUIRED)

## Columns: what the table offers

- Logical column "Distance": not a measure; writeable false; mapped in 1 of 1 table sources. [kb:sampleapp::bmm::SAMPLESALES.Dim Sales Rep.Distance]
    - NOT CONFIRMED FOR THE TAB: no item of the tab uses it. It is described because its logical table is part of the model behind the tab.
    - Mapped in table source(s): "LTS Employee". What it is mapped from is stated with each source.

- Logical column "Employee ID": not a measure; writeable false; mapped in 1 of 1 table sources. [kb:sampleapp::bmm::SAMPLESALES.Dim Sales Rep.Employee ID]
    - NOT CONFIRMED FOR THE TAB: no item of the tab uses it. It is described because its logical table is part of the model behind the tab.
    - Mapped in table source(s): "LTS Employee". What it is mapped from is stated with each source.

- Logical column "Employee Name": not a measure; writeable false; mapped in 1 of 1 table sources. [kb:sampleapp::bmm::SAMPLESALES.Dim Sales Rep.Employee Name]
    - Shown to report authors as "Employee Name" in presentation table "Dim Sales Rep"; name set by hand: false; CONFIRMED FOR THE TAB, items V3-rep. [kb:sampleapp::pres::Sample Sales.Dim Sales Rep.Employee Name] (REQUIRED)
    - Mapped in table source(s): "LTS Employee". What it is mapped from is stated with each source.

- Logical column "Manager ID": not a measure; writeable false; mapped in 1 of 1 table sources. [kb:sampleapp::bmm::SAMPLESALES.Dim Sales Rep.Manager ID]
    - NOT CONFIRMED FOR THE TAB: no item of the tab uses it. It is described because its logical table is part of the model behind the tab.
    - Mapped in table source(s): "LTS Employee". What it is mapped from is stated with each source.

- Logical column "Office ID": not a measure; writeable false; mapped in 1 of 1 table sources. [kb:sampleapp::bmm::SAMPLESALES.Dim Sales Rep.Office ID]
    - NOT CONFIRMED FOR THE TAB: no item of the tab uses it. It is described because its logical table is part of the model behind the tab.
    - Mapped in table source(s): "LTS Employee". What it is mapped from is stated with each source.

## Table sources: where the table's data comes from

- Source "LTS Employee" of logical table "Dim Sales Rep": active true; priority group not recorded (empty; it does not mean lowest). [kb:sampleapp::bmm::SAMPLESALES.Dim Sales Rep.src.LTS Employee] (REQUIRED)
    - Level of detail (grain; the source's group by): ` GROUPBYLEVEL("SAMPLESALES"."Sales Rep Hierarchy"."Detail")` [kb:sampleapp::bmm::SAMPLESALES.Dim Sales Rep.src.LTS Employee]
        - %1 -> sampleapp::bmm::SAMPLESALES.Sales Rep Hierarchy.Detail
    - Parent-child hierarchy: this source names physical table "EMP_PARENT_CHILD" (unit dbtable-02-emp-parent-child) as the table that holds the parent-child relationships (its closure table). That table's unit names the member, parent, distance and leaf columns. -> sampleapp::phys::xe.SAMPLESALES.EMP_PARENT_CHILD [kb:sampleapp::bmm::SAMPLESALES.Dim Sales Rep.src.LTS Employee]
    - Physical tables this source is built on: 2 (more than one, so the source joins them; the joins follow the tables).
        - Reads physical table "EMPLOYEE" in `xe.SAMPLESALES`; type `none`. Unit dbtable-01-employee states the table whole: what it is in the database, every column, its keys. -> sampleapp::phys::xe.SAMPLESALES.EMPLOYEE [kb:sampleapp::bmm::SAMPLESALES.Dim Sales Rep.src.LTS Employee#table0]
        - Reads physical table "EMP_PARENT_CHILD" in `xe.SAMPLESALES`; type `none`. Unit dbtable-02-emp-parent-child states the table whole: what it is in the database, every column, its keys. -> sampleapp::phys::xe.SAMPLESALES.EMP_PARENT_CHILD [kb:sampleapp::bmm::SAMPLESALES.Dim Sales Rep.src.LTS Employee#table1]
    - Joins this source makes between its tables: 2 line(s) for 1 join(s). The repository lists a join once under each of the two tables it joins; "left" and "right" in a join type, and the cardinality, read from the first table named on the line to the second.
        - Listed under "EMPLOYEE": "EMPLOYEE" joined to "EMP_PARENT_CHILD"; join type `Inner`; cardinality ` ONE TO MANY`; on foreign key "EMP_PARENT_CHILD_FKey": "EMP_PARENT_CHILD" ("ANCESTOR_KEY") = "EMPLOYEE" ("EMP_ID"). -> sampleapp::phys::xe.SAMPLESALES.EMP_PARENT_CHILD.EMP_PARENT_CHILD_FKey [kb:sampleapp::bmm::SAMPLESALES.Dim Sales Rep.src.LTS Employee#table0.join0] (REQUIRED)
        - Listed under "EMP_PARENT_CHILD": "EMP_PARENT_CHILD" joined to "EMPLOYEE"; join type `Inner`; cardinality `MANY TO ONE`; on foreign key "EMP_PARENT_CHILD_FKey": "EMP_PARENT_CHILD" ("ANCESTOR_KEY") = "EMPLOYEE" ("EMP_ID"). -> sampleapp::phys::xe.SAMPLESALES.EMP_PARENT_CHILD.EMP_PARENT_CHILD_FKey [kb:sampleapp::bmm::SAMPLESALES.Dim Sales Rep.src.LTS Employee#table1.join0] (REQUIRED)
    - Logical column "Employee ID" here is `"xe".""."SAMPLESALES"."EMPLOYEE"."EMP_ID"`; physical column "EMPLOYEE"."EMP_ID" (type DOUBLE, precision 15, nullable not stated). [kb:sampleapp::bmm::SAMPLESALES.Dim Sales Rep.src.LTS Employee#map0]
    - Logical column "Employee Name" here is `"xe".""."SAMPLESALES"."EMPLOYEE"."EMP_NAME"`; physical column "EMPLOYEE"."EMP_NAME" (type VARCHAR, precision 40, nullable true). [kb:sampleapp::bmm::SAMPLESALES.Dim Sales Rep.src.LTS Employee#map1]
        - Lineage: presentation column "Dim Sales Rep"."Employee Name" <- source "LTS Employee" <- physical column "EMPLOYEE"."EMP_NAME". [kb:sampleapp::pres::Sample Sales.Dim Sales Rep.Employee Name#sampleapp::bmm::SAMPLESALES.Dim Sales Rep.src.LTS Employee]
    - Logical column "Manager ID" here is `"xe".""."SAMPLESALES"."EMPLOYEE"."MANAGER_ID"`; physical column "EMPLOYEE"."MANAGER_ID" (type DOUBLE, precision 15, nullable true). [kb:sampleapp::bmm::SAMPLESALES.Dim Sales Rep.src.LTS Employee#map2]
    - Logical column "Office ID" here is `"xe".""."SAMPLESALES"."EMPLOYEE"."OFFICE_ID"`; physical column "EMPLOYEE"."OFFICE_ID" (type DOUBLE, precision 15, nullable true). [kb:sampleapp::bmm::SAMPLESALES.Dim Sales Rep.src.LTS Employee#map3]
    - Logical column "Distance" here is `"xe".""."SAMPLESALES"."EMP_PARENT_CHILD"."DISTANCE"`; physical column "EMP_PARENT_CHILD"."DISTANCE" (type INT, nullable not stated). [kb:sampleapp::bmm::SAMPLESALES.Dim Sales Rep.src.LTS Employee#map4]

