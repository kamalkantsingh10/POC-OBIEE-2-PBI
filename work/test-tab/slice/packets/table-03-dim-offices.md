# Logical table "Dim Offices"
Unit: table-03-dim-offices
Kind: logical_table
Tab: test-tab (subject areas: Sample Sales; estate sampleapp)
Facts from: lineage, logical_columns, logical_keys, logical_table_sources, logical_tables, lts_column_map, lts_tables, presentation_columns
Rows of this packet a document must cover: 4. Each is on a line that ends with (REQUIRED).
To cite a fact, copy the marker in square brackets that follows it, exactly as written. "->" points at another row.

## The table

- Logical table "Dim Offices" in business model "SAMPLESALES"; table sources the repository declares for it: 1. [kb:sampleapp::bmm::SAMPLESALES.Dim Offices] (REQUIRED)
- Columns of it in this slice: 10, of which measures: 0; not confirmed for the tab: 9. (counted; no Row identifier)
- Table sources of it in this slice: 1: "SAMP_OFFICES_D". Each is described below under its own heading, with the physical tables it reads and what it maps each column from. (counted; no Row identifier)
- Key "SAMP_OFFICES_D_Key" of logical table "Dim Offices": marked as primary key true; made of column(s) "Office Key". [kb:sampleapp::bmm::SAMPLESALES.Dim Offices.key.SAMP_OFFICES_D_Key] (REQUIRED)

## Columns: what the table offers

- Logical column "Area Name": not a measure; writeable false; mapped in 1 of 1 table sources. [kb:sampleapp::bmm::SAMPLESALES.Dim Offices.Area Name]
    - NOT CONFIRMED FOR THE TAB: no item of the tab uses it. It is described because its logical table is part of the model behind the tab.
    - Mapped in table source(s): "SAMP_OFFICES_D". What it is mapped from is stated with each source.

- Logical column "Company": not a measure; writeable false; mapped in 1 of 1 table sources. [kb:sampleapp::bmm::SAMPLESALES.Dim Offices.Company]
    - NOT CONFIRMED FOR THE TAB: no item of the tab uses it. It is described because its logical table is part of the model behind the tab.
    - Mapped in table source(s): "SAMP_OFFICES_D". What it is mapped from is stated with each source.

- Logical column "Company Key": not a measure; writeable false; mapped in 1 of 1 table sources. [kb:sampleapp::bmm::SAMPLESALES.Dim Offices.Company Key]
    - NOT CONFIRMED FOR THE TAB: no item of the tab uses it. It is described because its logical table is part of the model behind the tab.
    - Mapped in table source(s): "SAMP_OFFICES_D". What it is mapped from is stated with each source.

- Logical column "Department": not a measure; writeable false; mapped in 1 of 1 table sources. [kb:sampleapp::bmm::SAMPLESALES.Dim Offices.Department]
    - NOT CONFIRMED FOR THE TAB: no item of the tab uses it. It is described because its logical table is part of the model behind the tab.
    - Mapped in table source(s): "SAMP_OFFICES_D". What it is mapped from is stated with each source.

- Logical column "Dept Key": not a measure; writeable false; mapped in 1 of 1 table sources. [kb:sampleapp::bmm::SAMPLESALES.Dim Offices.Dept Key]
    - NOT CONFIRMED FOR THE TAB: no item of the tab uses it. It is described because its logical table is part of the model behind the tab.
    - Mapped in table source(s): "SAMP_OFFICES_D". What it is mapped from is stated with each source.

- Logical column "Office Dsc": not a measure; writeable false; mapped in 1 of 1 table sources. [kb:sampleapp::bmm::SAMPLESALES.Dim Offices.Office Dsc]
    - Shown to report authors as "Office Dsc" in presentation table "Dim Offices"; name set by hand: false; CONFIRMED FOR THE TAB, items F2, V2-office. [kb:sampleapp::pres::Sample Sales.Dim Offices.Office Dsc] (REQUIRED)
    - Mapped in table source(s): "SAMP_OFFICES_D". What it is mapped from is stated with each source.

- Logical column "Office Key": not a measure; writeable false; mapped in 1 of 1 table sources. [kb:sampleapp::bmm::SAMPLESALES.Dim Offices.Office Key]
    - NOT CONFIRMED FOR THE TAB: no item of the tab uses it. It is described because its logical table is part of the model behind the tab.
    - Mapped in table source(s): "SAMP_OFFICES_D". What it is mapped from is stated with each source.

- Logical column "Org Key": not a measure; writeable false; mapped in 1 of 1 table sources. [kb:sampleapp::bmm::SAMPLESALES.Dim Offices.Org Key]
    - NOT CONFIRMED FOR THE TAB: no item of the tab uses it. It is described because its logical table is part of the model behind the tab.
    - Mapped in table source(s): "SAMP_OFFICES_D". What it is mapped from is stated with each source.

- Logical column "Organization": not a measure; writeable false; mapped in 1 of 1 table sources. [kb:sampleapp::bmm::SAMPLESALES.Dim Offices.Organization]
    - NOT CONFIRMED FOR THE TAB: no item of the tab uses it. It is described because its logical table is part of the model behind the tab.
    - Mapped in table source(s): "SAMP_OFFICES_D". What it is mapped from is stated with each source.

- Logical column "Sequence": not a measure; writeable false; mapped in 1 of 1 table sources. [kb:sampleapp::bmm::SAMPLESALES.Dim Offices.Sequence]
    - NOT CONFIRMED FOR THE TAB: no item of the tab uses it. It is described because its logical table is part of the model behind the tab.
    - Mapped in table source(s): "SAMP_OFFICES_D". What it is mapped from is stated with each source.

## Table sources: where the table's data comes from

- Source "SAMP_OFFICES_D" of logical table "Dim Offices": active true; priority group not recorded (empty; it does not mean lowest). [kb:sampleapp::bmm::SAMPLESALES.Dim Offices.src.SAMP_OFFICES_D] (REQUIRED)
    - Level of detail (grain; the source's group by): ` GROUPBYLEVEL("SAMPLESALES"."Office Hierarchy"."Office Detail")` [kb:sampleapp::bmm::SAMPLESALES.Dim Offices.src.SAMP_OFFICES_D]
        - %1 -> sampleapp::bmm::SAMPLESALES.Office Hierarchy.Office Detail
    - Physical tables this source is built on: 1 (one table, so the source makes no join).
        - Reads physical table "SAMP_OFFICES_D" in `xe.SAMPLESALES`; type `none`. Unit dbtable-04-samp-offices-d states the table whole: what it is in the database, every column, its keys. -> sampleapp::phys::xe.SAMPLESALES.SAMP_OFFICES_D [kb:sampleapp::bmm::SAMPLESALES.Dim Offices.src.SAMP_OFFICES_D#table0]
    - Logical column "Company" here is `"xe".""."SAMPLESALES"."SAMP_OFFICES_D"."COMPANY"`; physical column "SAMP_OFFICES_D"."COMPANY" (type VARCHAR, precision 14, nullable true). [kb:sampleapp::bmm::SAMPLESALES.Dim Offices.src.SAMP_OFFICES_D#map0]
    - Logical column "Company Key" here is `"xe".""."SAMPLESALES"."SAMP_OFFICES_D"."COMPANY_KEY"`; physical column "SAMP_OFFICES_D"."COMPANY_KEY" (type DOUBLE, precision 15, nullable not stated). [kb:sampleapp::bmm::SAMPLESALES.Dim Offices.src.SAMP_OFFICES_D#map1]
    - Logical column "Department" here is `"xe".""."SAMPLESALES"."SAMP_OFFICES_D"."DEPARTMENT"`; physical column "SAMP_OFFICES_D"."DEPARTMENT" (type VARCHAR, precision 25, nullable true). [kb:sampleapp::bmm::SAMPLESALES.Dim Offices.src.SAMP_OFFICES_D#map2]
    - Logical column "Dept Key" here is `"xe".""."SAMPLESALES"."SAMP_OFFICES_D"."DEPT_KEY"`; physical column "SAMP_OFFICES_D"."DEPT_KEY" (type DOUBLE, precision 15, nullable not stated). [kb:sampleapp::bmm::SAMPLESALES.Dim Offices.src.SAMP_OFFICES_D#map3]
    - Logical column "Office Dsc" here is `"xe".""."SAMPLESALES"."SAMP_OFFICES_D"."OFFICE_DSC"`; physical column "SAMP_OFFICES_D"."OFFICE_DSC" (type VARCHAR, precision 17, nullable true). [kb:sampleapp::bmm::SAMPLESALES.Dim Offices.src.SAMP_OFFICES_D#map4]
        - Lineage: presentation column "Dim Offices"."Office Dsc" <- source "SAMP_OFFICES_D" <- physical column "SAMP_OFFICES_D"."OFFICE_DSC". [kb:sampleapp::pres::Sample Sales.Dim Offices.Office Dsc#sampleapp::bmm::SAMPLESALES.Dim Offices.src.SAMP_OFFICES_D]
    - Logical column "Office Key" here is `"xe".""."SAMPLESALES"."SAMP_OFFICES_D"."OFFICE_KEY"`; physical column "SAMP_OFFICES_D"."OFFICE_KEY" (type DOUBLE, precision 15, nullable not stated). [kb:sampleapp::bmm::SAMPLESALES.Dim Offices.src.SAMP_OFFICES_D#map5]
    - Logical column "Org Key" here is `"xe".""."SAMPLESALES"."SAMP_OFFICES_D"."ORG_KEY"`; physical column "SAMP_OFFICES_D"."ORG_KEY" (type DOUBLE, precision 15, nullable not stated). [kb:sampleapp::bmm::SAMPLESALES.Dim Offices.src.SAMP_OFFICES_D#map6]
    - Logical column "Organization" here is `"xe".""."SAMPLESALES"."SAMP_OFFICES_D"."ORGANIZATION"`; physical column "SAMP_OFFICES_D"."ORGANIZATION" (type VARCHAR, precision 18, nullable true). [kb:sampleapp::bmm::SAMPLESALES.Dim Offices.src.SAMP_OFFICES_D#map7]
    - Logical column "Sequence" here is `"xe".""."SAMPLESALES"."SAMP_OFFICES_D"."SEQUENCE"`; physical column "SAMP_OFFICES_D"."SEQUENCE" (type DOUBLE, precision 15, nullable true). [kb:sampleapp::bmm::SAMPLESALES.Dim Offices.src.SAMP_OFFICES_D#map8]
    - Logical column "Area Name" here is `"xe".""."SAMPLESALES"."SAMP_OFFICES_D"."AREA_NAME"`; physical column "SAMP_OFFICES_D"."AREA_NAME" (type VARCHAR, precision 25, nullable true). [kb:sampleapp::bmm::SAMPLESALES.Dim Offices.src.SAMP_OFFICES_D#map9]

