# Logical table "Fact Target"
Unit: table-02-fact-target
Kind: logical_table
Tab: test-tab (subject areas: Sample Sales; estate sampleapp)
Facts from: aggregation_rules, lineage, logical_columns, logical_table_sources, logical_tables, lts_column_map, lts_tables, presentation_columns
Rows of this packet a document must cover: 5. Each is on a line that ends with (REQUIRED).
To cite a fact, copy the marker in square brackets that follows it, exactly as written. "->" points at another row.

## The table

- Logical table "Fact Target" in business model "SAMPLESALES"; table sources the repository declares for it: 1. [kb:sampleapp::bmm::SAMPLESALES.Fact Target] (REQUIRED)
- Columns of it in this slice: 5, of which measures: 2; not confirmed for the tab: 4. (counted; no Row identifier)
- Table sources of it in this slice: 1: "SAMP_TARGETS_F". Each is described below under its own heading, with the physical tables it reads and what it maps each column from. (counted; no Row identifier)
- Keys of logical table "Fact Target": not recorded. The repository declares no key for it. (logical_keys; no Row identifier)

## Columns: what the table offers

- Logical column "Bill Mth Key": not a measure; writeable false; mapped in 1 of 1 table sources. [kb:sampleapp::bmm::SAMPLESALES.Fact Target.Bill Mth Key]
    - NOT CONFIRMED FOR THE TAB: no item of the tab uses it. It is described because its logical table is part of the model behind the tab.
    - Mapped in table source(s): "SAMP_TARGETS_F". What it is mapped from is stated with each source.

- Logical column "Office Key": not a measure; writeable false; mapped in 1 of 1 table sources. [kb:sampleapp::bmm::SAMPLESALES.Fact Target.Office Key]
    - NOT CONFIRMED FOR THE TAB: no item of the tab uses it. It is described because its logical table is part of the model behind the tab.
    - Mapped in table source(s): "SAMP_TARGETS_F". What it is mapped from is stated with each source.

- Logical column "Prod Key": not a measure; writeable false; mapped in 1 of 1 table sources. [kb:sampleapp::bmm::SAMPLESALES.Fact Target.Prod Key]
    - NOT CONFIRMED FOR THE TAB: no item of the tab uses it. It is described because its logical table is part of the model behind the tab.
    - Mapped in table source(s): "SAMP_TARGETS_F". What it is mapped from is stated with each source.

- Logical column "Revenue": MEASURE; writeable true; mapped in 1 of 1 table sources. [kb:sampleapp::bmm::SAMPLESALES.Fact Target.Revenue] (REQUIRED)
    - Shown to report authors as "Revenue" in presentation table "Fact Target"; name set by hand: false; CONFIRMED FOR THE TAB, items K3, V1-target. [kb:sampleapp::pres::Sample Sales.Fact Target.Revenue] (REQUIRED)
    - Aggregation rule (default): ` SUM("SAMPLESALES"."Fact Target"."Revenue")` [kb:sampleapp::bmm::SAMPLESALES.Fact Target.Revenue#aggr0]
    - Mapped in table source(s): "SAMP_TARGETS_F". What it is mapped from is stated with each source.

- Logical column "Units": MEASURE; writeable true; mapped in 1 of 1 table sources. [kb:sampleapp::bmm::SAMPLESALES.Fact Target.Units] (REQUIRED)
    - NOT CONFIRMED FOR THE TAB: no item of the tab uses it. It is described because its logical table is part of the model behind the tab.
    - Aggregation rule (default): ` SUM("SAMPLESALES"."Fact Target"."Units")` [kb:sampleapp::bmm::SAMPLESALES.Fact Target.Units#aggr0]
    - Mapped in table source(s): "SAMP_TARGETS_F". What it is mapped from is stated with each source.

## Table sources: where the table's data comes from

- Source "SAMP_TARGETS_F" of logical table "Fact Target": active true; priority group not recorded (empty; it does not mean lowest). [kb:sampleapp::bmm::SAMPLESALES.Fact Target.src.SAMP_TARGETS_F] (REQUIRED)
    - Level of detail (grain; the source's group by): ` GROUPBYLEVEL("SAMPLESALES"."Time Hierarchy"."Dim Time Detail", "SAMPLESALES"."Office Hierarchy"."Office Detail", "SAMPLESALES"."Products Hierararchy"."Product Detail")` [kb:sampleapp::bmm::SAMPLESALES.Fact Target.src.SAMP_TARGETS_F]
        - %1 -> sampleapp::bmm::SAMPLESALES.Time Hierarchy.Dim Time Detail
        - %2 -> sampleapp::bmm::SAMPLESALES.Office Hierarchy.Office Detail
        - %3 -> sampleapp::bmm::SAMPLESALES.Products Hierararchy.Product Detail
    - Physical tables this source is built on: 1 (one table, so the source makes no join).
        - Reads physical table "SAMP_TARGETS_F" in `xe.SAMPLESALES`; type `none`. Unit dbtable-11-samp-targets-f states the table whole: what it is in the database, every column, its keys. -> sampleapp::phys::xe.SAMPLESALES.SAMP_TARGETS_F [kb:sampleapp::bmm::SAMPLESALES.Fact Target.src.SAMP_TARGETS_F#table0]
    - Logical column "Bill Mth Key" here is `"xe".""."SAMPLESALES"."SAMP_TARGETS_F"."BILL_MTH_KEY"`; physical column "SAMP_TARGETS_F"."BILL_MTH_KEY" (type DOUBLE, precision 15, nullable true). [kb:sampleapp::bmm::SAMPLESALES.Fact Target.src.SAMP_TARGETS_F#map0]
    - Logical column "Office Key" here is `"xe".""."SAMPLESALES"."SAMP_TARGETS_F"."OFFICE_KEY"`; physical column "SAMP_TARGETS_F"."OFFICE_KEY" (type DOUBLE, precision 15, nullable true). [kb:sampleapp::bmm::SAMPLESALES.Fact Target.src.SAMP_TARGETS_F#map1]
    - Logical column "Prod Key" here is `"xe".""."SAMPLESALES"."SAMP_TARGETS_F"."PROD_KEY"`; physical column "SAMP_TARGETS_F"."PROD_KEY" (type DOUBLE, precision 15, nullable true). [kb:sampleapp::bmm::SAMPLESALES.Fact Target.src.SAMP_TARGETS_F#map2]
    - Logical column "Revenue" here is `"xe".""."SAMPLESALES"."SAMP_TARGETS_F"."REVENUE"`; physical column "SAMP_TARGETS_F"."REVENUE" (type DOUBLE, precision 8, scale 2, nullable true). [kb:sampleapp::bmm::SAMPLESALES.Fact Target.src.SAMP_TARGETS_F#map3]
        - Lineage: presentation column "Fact Target"."Revenue" <- source "SAMP_TARGETS_F" <- physical column "SAMP_TARGETS_F"."REVENUE". [kb:sampleapp::pres::Sample Sales.Fact Target.Revenue#sampleapp::bmm::SAMPLESALES.Fact Target.src.SAMP_TARGETS_F]
    - Logical column "Units" here is `"xe".""."SAMPLESALES"."SAMP_TARGETS_F"."UNITS"`; physical column "SAMP_TARGETS_F"."UNITS" (type DOUBLE, precision 15, nullable true). [kb:sampleapp::bmm::SAMPLESALES.Fact Target.src.SAMP_TARGETS_F#map4]

