# Logical table "Dim Time"
Unit: table-07-dim-time
Kind: logical_table
Tab: test-tab (subject areas: Sample Sales; estate sampleapp)
Facts from: lineage, logical_columns, logical_keys, logical_table_sources, logical_tables, lts_column_map, lts_tables, presentation_columns
Rows of this packet a document must cover: 6. Each is on a line that ends with (REQUIRED).
To cite a fact, copy the marker in square brackets that follows it, exactly as written. "->" points at another row.

## The table

- Logical table "Dim Time" in business model "SAMPLESALES"; table sources the repository declares for it: 2. [kb:sampleapp::bmm::SAMPLESALES.Dim Time] (REQUIRED)
- Columns of it in this slice: 6, of which measures: 0; not confirmed for the tab: 4. (counted; no Row identifier)
- Table sources of it in this slice: 2: "LTS Time Day", "LTS Time Month". Each is described below under its own heading, with the physical tables it reads and what it maps each column from. (counted; no Row identifier)
- Key "SAMP_TIME_DAY_D_Key" of logical table "Dim Time": marked as primary key true; made of column(s) "Calendar Date". [kb:sampleapp::bmm::SAMPLESALES.Dim Time.key.SAMP_TIME_DAY_D_Key] (REQUIRED)

## Columns: what the table offers

- Logical column "Calendar Date": not a measure; writeable false; mapped in 1 of 2 table sources. [kb:sampleapp::bmm::SAMPLESALES.Dim Time.Calendar Date]
    - NOT CONFIRMED FOR THE TAB: no item of the tab uses it. It is described because its logical table is part of the model behind the tab.
    - Mapped in table source(s): "LTS Time Day". What it is mapped from is stated with each source.

- Logical column "Per Name Half": not a measure; writeable false; mapped in 2 of 2 table sources. [kb:sampleapp::bmm::SAMPLESALES.Dim Time.Per Name Half]
    - NOT CONFIRMED FOR THE TAB: no item of the tab uses it. It is described because its logical table is part of the model behind the tab.
    - Mapped in table source(s): "LTS Time Day", "LTS Time Month". What it is mapped from is stated with each source.

- Logical column "Per Name Month": not a measure; writeable false; mapped in 2 of 2 table sources. [kb:sampleapp::bmm::SAMPLESALES.Dim Time.Per Name Month]
    - Shown to report authors as "Per Name Month" in presentation table "Dim Time"; name set by hand: false; CONFIRMED FOR THE TAB, items V1-month. [kb:sampleapp::pres::Sample Sales.Dim Time.Per Name Month] (REQUIRED)
        - Name shown to users (display name), as written: `VALUEOF(NQ_SESSION.CN_Sample_Sales_Dim_Time_Per_Name_Month)` [kb:sampleapp::pres::Sample Sales.Dim Time.Per Name Month]
            - Names variable `CN_Sample_Sales_Dim_Time_Per_Name_Month` inside VALUEOF(...): unit variables lists every such name and says whether the repository defines it.
    - Mapped in table source(s): "LTS Time Day", "LTS Time Month". What it is mapped from is stated with each source.

- Logical column "Per Name Qtr": not a measure; writeable false; mapped in 2 of 2 table sources. [kb:sampleapp::bmm::SAMPLESALES.Dim Time.Per Name Qtr]
    - NOT CONFIRMED FOR THE TAB: no item of the tab uses it. It is described because its logical table is part of the model behind the tab.
    - Mapped in table source(s): "LTS Time Day", "LTS Time Month". What it is mapped from is stated with each source.

- Logical column "Per Name Week": not a measure; writeable false; mapped in 1 of 2 table sources. [kb:sampleapp::bmm::SAMPLESALES.Dim Time.Per Name Week]
    - NOT CONFIRMED FOR THE TAB: no item of the tab uses it. It is described because its logical table is part of the model behind the tab.
    - Mapped in table source(s): "LTS Time Day". What it is mapped from is stated with each source.

- Logical column "Per Name Year": not a measure; writeable false; mapped in 2 of 2 table sources. [kb:sampleapp::bmm::SAMPLESALES.Dim Time.Per Name Year]
    - Shown to report authors as "Per Name Year" in presentation table "Dim Time"; name set by hand: false; CONFIRMED FOR THE TAB, items F1. [kb:sampleapp::pres::Sample Sales.Dim Time.Per Name Year] (REQUIRED)
        - Name shown to users (display name), as written: `VALUEOF(NQ_SESSION.CN_Sample_Sales_Dim_Time_Per_Name_Year)` [kb:sampleapp::pres::Sample Sales.Dim Time.Per Name Year]
            - Names variable `CN_Sample_Sales_Dim_Time_Per_Name_Year` inside VALUEOF(...): unit variables lists every such name and says whether the repository defines it.
    - Mapped in table source(s): "LTS Time Day", "LTS Time Month". What it is mapped from is stated with each source.

## Table sources: where the table's data comes from

- Source "LTS Time Day" of logical table "Dim Time": active true; priority group not recorded (empty; it does not mean lowest). [kb:sampleapp::bmm::SAMPLESALES.Dim Time.src.LTS Time Day] (REQUIRED)
    - Level of detail (grain; the source's group by): ` GROUPBYLEVEL("SAMPLESALES"."Time Hierarchy"."Dim Time Detail")` [kb:sampleapp::bmm::SAMPLESALES.Dim Time.src.LTS Time Day]
        - %1 -> sampleapp::bmm::SAMPLESALES.Time Hierarchy.Dim Time Detail
    - Physical tables this source is built on: 1 (one table, so the source makes no join).
        - Reads physical table "SAMP_TIME_DAY_D" in `xe.SAMPLESALES`; type `none`. Unit dbtable-12-samp-time-day-d states the table whole: what it is in the database, every column, its keys. -> sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_DAY_D [kb:sampleapp::bmm::SAMPLESALES.Dim Time.src.LTS Time Day#table0]
    - Logical column "Per Name Year" here is `"xe".""."SAMPLESALES"."SAMP_TIME_DAY_D"."PER_NAME_YEAR"`; physical column "SAMP_TIME_DAY_D"."PER_NAME_YEAR" (type VARCHAR, precision 4, nullable not stated). [kb:sampleapp::bmm::SAMPLESALES.Dim Time.src.LTS Time Day#map0]
        - Lineage: presentation column "Dim Time"."Per Name Year" <- source "LTS Time Day" <- physical column "SAMP_TIME_DAY_D"."PER_NAME_YEAR". [kb:sampleapp::pres::Sample Sales.Dim Time.Per Name Year#sampleapp::bmm::SAMPLESALES.Dim Time.src.LTS Time Day]
    - Logical column "Per Name Week" here is `"xe".""."SAMPLESALES"."SAMP_TIME_DAY_D"."PER_NAME_WEEK"`; physical column "SAMP_TIME_DAY_D"."PER_NAME_WEEK" (type VARCHAR, precision 12, nullable not stated). [kb:sampleapp::bmm::SAMPLESALES.Dim Time.src.LTS Time Day#map1]
    - Logical column "Per Name Half" here is `"xe".""."SAMPLESALES"."SAMP_TIME_DAY_D"."PER_NAME_HALF"`; physical column "SAMP_TIME_DAY_D"."PER_NAME_HALF" (type VARCHAR, precision 8, nullable not stated). [kb:sampleapp::bmm::SAMPLESALES.Dim Time.src.LTS Time Day#map2]
    - Logical column "Per Name Month" here is `"xe".""."SAMPLESALES"."SAMP_TIME_DAY_D"."PER_NAME_MONTH"`; physical column "SAMP_TIME_DAY_D"."PER_NAME_MONTH" (type VARCHAR, precision 9, nullable not stated). [kb:sampleapp::bmm::SAMPLESALES.Dim Time.src.LTS Time Day#map3]
        - Lineage: presentation column "Dim Time"."Per Name Month" <- source "LTS Time Day" <- physical column "SAMP_TIME_DAY_D"."PER_NAME_MONTH". [kb:sampleapp::pres::Sample Sales.Dim Time.Per Name Month#sampleapp::bmm::SAMPLESALES.Dim Time.src.LTS Time Day]
    - Logical column "Per Name Qtr" here is `"xe".""."SAMPLESALES"."SAMP_TIME_DAY_D"."PER_NAME_QTR"`; physical column "SAMP_TIME_DAY_D"."PER_NAME_QTR" (type VARCHAR, precision 7, nullable not stated). [kb:sampleapp::bmm::SAMPLESALES.Dim Time.src.LTS Time Day#map4]
    - Logical column "Calendar Date" here is `"xe".""."SAMPLESALES"."SAMP_TIME_DAY_D"."CALENDAR_DATE"`; physical column "SAMP_TIME_DAY_D"."CALENDAR_DATE" (type DATE, nullable not stated). [kb:sampleapp::bmm::SAMPLESALES.Dim Time.src.LTS Time Day#map5]

- Source "LTS Time Month" of logical table "Dim Time": active true; priority group not recorded (empty; it does not mean lowest). [kb:sampleapp::bmm::SAMPLESALES.Dim Time.src.LTS Time Month] (REQUIRED)
    - Level of detail (grain; the source's group by): ` GROUPBYLEVEL("SAMPLESALES"."Time Hierarchy"."Month")` [kb:sampleapp::bmm::SAMPLESALES.Dim Time.src.LTS Time Month]
        - %1 -> sampleapp::bmm::SAMPLESALES.Time Hierarchy.Month
    - Physical tables this source is built on: 1 (one table, so the source makes no join).
        - Reads physical table "SAMP_TIME_MTH_D" in `xe.SAMPLESALES`; type `none`. Unit dbtable-13-samp-time-mth-d states the table whole: what it is in the database, every column, its keys. -> sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D [kb:sampleapp::bmm::SAMPLESALES.Dim Time.src.LTS Time Month#table0]
    - Logical column "Per Name Year" here is `"xe".""."SAMPLESALES"."SAMP_TIME_MTH_D"."PER_NAME_YEAR"`; physical column "SAMP_TIME_MTH_D"."PER_NAME_YEAR" (type VARCHAR, precision 4, nullable true). [kb:sampleapp::bmm::SAMPLESALES.Dim Time.src.LTS Time Month#map0]
        - Lineage: presentation column "Dim Time"."Per Name Year" <- source "LTS Time Month" <- physical column "SAMP_TIME_MTH_D"."PER_NAME_YEAR". [kb:sampleapp::pres::Sample Sales.Dim Time.Per Name Year#sampleapp::bmm::SAMPLESALES.Dim Time.src.LTS Time Month]
    - Logical column "Per Name Half" here is `"xe".""."SAMPLESALES"."SAMP_TIME_MTH_D"."PER_NAME_HALF"`; physical column "SAMP_TIME_MTH_D"."PER_NAME_HALF" (type VARCHAR, precision 8, nullable true). [kb:sampleapp::bmm::SAMPLESALES.Dim Time.src.LTS Time Month#map1]
    - Logical column "Per Name Month" here is `"xe".""."SAMPLESALES"."SAMP_TIME_MTH_D"."PER_NAME_MONTH"`; physical column "SAMP_TIME_MTH_D"."PER_NAME_MONTH" (type VARCHAR, precision 9, nullable true). [kb:sampleapp::bmm::SAMPLESALES.Dim Time.src.LTS Time Month#map2]
        - Lineage: presentation column "Dim Time"."Per Name Month" <- source "LTS Time Month" <- physical column "SAMP_TIME_MTH_D"."PER_NAME_MONTH". [kb:sampleapp::pres::Sample Sales.Dim Time.Per Name Month#sampleapp::bmm::SAMPLESALES.Dim Time.src.LTS Time Month]
    - Logical column "Per Name Qtr" here is `"xe".""."SAMPLESALES"."SAMP_TIME_MTH_D"."PER_NAME_QTR"`; physical column "SAMP_TIME_MTH_D"."PER_NAME_QTR" (type VARCHAR, precision 7, nullable true). [kb:sampleapp::bmm::SAMPLESALES.Dim Time.src.LTS Time Month#map3]

