# Logical table "Fact Revenue" (part 1 of 2)
Unit: table-01-fact-revenue-p1
Kind: logical_table
Tab: test-tab (subject areas: Sample Sales; estate sampleapp)
Facts from: aggregation_rules, logical_columns, logical_tables, presentation_columns
Rows of this packet a document must cover: 14. Each is on a line that ends with (REQUIRED).
To cite a fact, copy the marker in square brackets that follows it, exactly as written. "->" points at another row.
This is part 1 of 2 of what was too long for one packet (the parts are table-01-fact-revenue-p1 to table-01-fact-revenue-p2). Each part is a unit of its own: write this one as its own section, from this packet alone.

## The table

- Logical table "Fact Revenue" in business model "SAMPLESALES"; table sources the repository declares for it: 5. [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue] (REQUIRED)
- Columns of it in this slice: 22, of which measures: 9; not confirmed for the tab: 18. (counted; no Row identifier)
- Table sources of it in this slice: 5: "Aggregate Revenue", "Aggregate Revenue By Month", "Aggregate Revenue by Year", "FactCrossJoin", "SAMP_REVENUE_F". Each is described below under its own heading, with the physical tables it reads and what it maps each column from. (counted; no Row identifier)
- Keys of logical table "Fact Revenue": not recorded. The repository declares no key for it. (logical_keys; no Row identifier)

## Columns: what the table offers

- Logical column "All Months Revenue": MEASURE; writeable false; mapped in 2 of 5 table sources. [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.All Months Revenue] (REQUIRED)
    - NOT CONFIRMED FOR THE TAB: no item of the tab uses it. It is described because its logical table is part of the model behind the tab.
    - Aggregation rule (default): ` SUM("SAMPLESALES"."Fact Revenue"."All Months Revenue")` [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.All Months Revenue#aggr0]
    - Mapped in table source(s): "Aggregate Revenue", "SAMP_REVENUE_F". What it is mapped from is stated with each source.

- Logical column "All Products Revenue": MEASURE; writeable false; mapped in 2 of 5 table sources. [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.All Products Revenue] (REQUIRED)
    - NOT CONFIRMED FOR THE TAB: no item of the tab uses it. It is described because its logical table is part of the model behind the tab.
    - Aggregation rule (default): ` SUM("SAMPLESALES"."Fact Revenue"."All Products Revenue")` [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.All Products Revenue#aggr0]
    - Mapped in table source(s): "Aggregate Revenue", "SAMP_REVENUE_F". What it is mapped from is stated with each source.

- Logical column "BILL_MTH_KEY": not a measure; writeable false; mapped in 1 of 5 table sources. [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.BILL_MTH_KEY]
    - NOT CONFIRMED FOR THE TAB: no item of the tab uses it. It is described because its logical table is part of the model behind the tab.
    - Mapped in table source(s): "Aggregate Revenue By Month". What it is mapped from is stated with each source.

- Logical column "BILL_YEAR_KEY": not a measure; writeable false; mapped in 1 of 5 table sources. [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.BILL_YEAR_KEY]
    - NOT CONFIRMED FOR THE TAB: no item of the tab uses it. It is described because its logical table is part of the model behind the tab.
    - Mapped in table source(s): "Aggregate Revenue by Year". What it is mapped from is stated with each source.

- Logical column "Bill Day Dt": not a measure; writeable false; mapped in 1 of 5 table sources. [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Bill Day Dt]
    - NOT CONFIRMED FOR THE TAB: no item of the tab uses it. It is described because its logical table is part of the model behind the tab.
    - Mapped in table source(s): "SAMP_REVENUE_F". What it is mapped from is stated with each source.

- Logical column "Bill Mth Key": not a measure; writeable false; mapped in 2 of 5 table sources. [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Bill Mth Key]
    - NOT CONFIRMED FOR THE TAB: no item of the tab uses it. It is described because its logical table is part of the model behind the tab.
    - Mapped in table source(s): "Aggregate Revenue", "SAMP_REVENUE_F". What it is mapped from is stated with each source.

- Logical column "DISCNT_VALUE": not a measure; writeable false; mapped in 2 of 5 table sources. [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.DISCNT_VALUE]
    - NOT CONFIRMED FOR THE TAB: no item of the tab uses it. It is described because its logical table is part of the model behind the tab.
    - Mapped in table source(s): "Aggregate Revenue By Month", "Aggregate Revenue by Year". What it is mapped from is stated with each source.

- Logical column "Discnt Value": MEASURE; writeable false; mapped in 2 of 5 table sources. [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Discnt Value] (REQUIRED)
    - Shown to report authors as "Discnt Value" in presentation table "Fact Revenue"; name set by hand: false; CONFIRMED FOR THE TAB, items K1, K3, K4, V1-net-revenue, V2-net-revenue, V2-discount, V3-net-revenue. [kb:sampleapp::pres::Sample Sales.Fact Revenue.Discnt Value] (REQUIRED)
    - Aggregation rule (default): ` SUM("SAMPLESALES"."Fact Revenue"."Discnt Value")` [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Discnt Value#aggr0]
    - Mapped in table source(s): "Aggregate Revenue", "SAMP_REVENUE_F". What it is mapped from is stated with each source.

- Logical column "Office Key": not a measure; writeable false; mapped in 1 of 5 table sources. [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Office Key]
    - NOT CONFIRMED FOR THE TAB: no item of the tab uses it. It is described because its logical table is part of the model behind the tab.
    - Mapped in table source(s): "SAMP_REVENUE_F". What it is mapped from is stated with each source.

- Logical column "Order Day Dt": not a measure; writeable false; mapped in 1 of 5 table sources. [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Order Day Dt]
    - NOT CONFIRMED FOR THE TAB: no item of the tab uses it. It is described because its logical table is part of the model behind the tab.
    - Mapped in table source(s): "SAMP_REVENUE_F". What it is mapped from is stated with each source.

- Logical column "Order Number": not a measure; writeable false; mapped in 1 of 5 table sources. [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Order Number]
    - NOT CONFIRMED FOR THE TAB: no item of the tab uses it. It is described because its logical table is part of the model behind the tab.
    - Mapped in table source(s): "SAMP_REVENUE_F". What it is mapped from is stated with each source.

- Logical column "Order Status": not a measure; writeable false; mapped in 1 of 5 table sources. [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Order Status]
    - NOT CONFIRMED FOR THE TAB: no item of the tab uses it. It is described because its logical table is part of the model behind the tab.
    - Mapped in table source(s): "SAMP_REVENUE_F". What it is mapped from is stated with each source.

- Logical column "Order Type": not a measure; writeable false; mapped in 1 of 5 table sources. [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Order Type]
    - NOT CONFIRMED FOR THE TAB: no item of the tab uses it. It is described because its logical table is part of the model behind the tab.
    - Mapped in table source(s): "SAMP_REVENUE_F". What it is mapped from is stated with each source.

- Logical column "Preferred Currency": not a measure; writeable false; mapped in 0 of 5 table sources. [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Preferred Currency]
    - Shown to report authors as "Preferred Currency" in presentation table "Fact Revenue"; name set by hand: false; CONFIRMED FOR THE TAB, items N1-currency. [kb:sampleapp::pres::Sample Sales.Fact Revenue.Preferred Currency] (REQUIRED)
    - Formula (calculated from other logical objects): ` INDEXCOL( CASE  VALUEOF(NQ_SESSION."PREFERRED_CURRENCY") WHEN 'gc1' THEN 0 WHEN 'gc2' THEN 1 WHEN 'gc3' THEN 2 END , "SAMPLESALES"."Fact Revenue"."Revenue", "SAMPLESALES"."Fact Revenue"."Revenue (EUR)", "SAMPLESALES"."Fact Revenue"."Revenue (GBP)")` [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Preferred Currency]
        - %1 -> sampleapp::var::PREFERRED_CURRENCY
        - %2 -> sampleapp::bmm::SAMPLESALES.Fact Revenue.Revenue
        - %3 -> sampleapp::bmm::SAMPLESALES.Fact Revenue.Revenue (EUR)
        - %4 -> sampleapp::bmm::SAMPLESALES.Fact Revenue.Revenue (GBP)
        - Names variable `PREFERRED_CURRENCY` inside VALUEOF(...): unit variables lists every such name and says whether the repository defines it.

- Logical column "Preserve": MEASURE; writeable false; mapped in 1 of 5 table sources. [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Preserve] (REQUIRED)
    - NOT CONFIRMED FOR THE TAB: no item of the tab uses it. It is described because its logical table is part of the model behind the tab.
    - Aggregation rule (default): ` MAX("SAMPLESALES"."Fact Revenue"."Preserve")` [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Preserve#aggr0]
    - Mapped in table source(s): "FactCrossJoin". What it is mapped from is stated with each source.

- Logical column "Prod Key": not a measure; writeable false; mapped in 2 of 5 table sources. [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Prod Key]
    - NOT CONFIRMED FOR THE TAB: no item of the tab uses it. It is described because its logical table is part of the model behind the tab.
    - Mapped in table source(s): "Aggregate Revenue", "SAMP_REVENUE_F". What it is mapped from is stated with each source.

- Logical column "Revenue": MEASURE; writeable false; mapped in 4 of 5 table sources. [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Revenue] (REQUIRED)
    - Shown to report authors as "Revenue" in presentation table "Fact Revenue"; name set by hand: false; CONFIRMED FOR THE TAB, items K1, K3, K4, V1-net-revenue, V2-net-revenue, V3-net-revenue. [kb:sampleapp::pres::Sample Sales.Fact Revenue.Revenue] (REQUIRED)
    - Aggregation rule (default): ` SUM("SAMPLESALES"."Fact Revenue"."Revenue")` [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Revenue#aggr0]
    - Mapped in table source(s): "Aggregate Revenue By Month", "Aggregate Revenue by Year", "Aggregate Revenue", "SAMP_REVENUE_F". What it is mapped from is stated with each source.

- Logical column "Revenue (EUR)": MEASURE; writeable false; mapped in 2 of 5 table sources. [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Revenue (EUR)] (REQUIRED)
    - NOT CONFIRMED FOR THE TAB: no item of the tab uses it. It is described because its logical table is part of the model behind the tab.
    - Aggregation rule (default): ` SUM("SAMPLESALES"."Fact Revenue"."Revenue (EUR)")` [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Revenue (EUR)#aggr0]
    - Mapped in table source(s): "Aggregate Revenue", "SAMP_REVENUE_F". What it is mapped from is stated with each source.

- Logical column "Revenue (GBP)": MEASURE; writeable false; mapped in 2 of 5 table sources. [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Revenue (GBP)] (REQUIRED)
    - NOT CONFIRMED FOR THE TAB: no item of the tab uses it. It is described because its logical table is part of the model behind the tab.
    - Aggregation rule (default): ` SUM("SAMPLESALES"."Fact Revenue"."Revenue (GBP)")` [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Revenue (GBP)#aggr0]
    - Mapped in table source(s): "Aggregate Revenue", "SAMP_REVENUE_F". What it is mapped from is stated with each source.

- Logical column "Revenue (INR)": MEASURE; writeable false; mapped in 2 of 5 table sources. [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Revenue (INR)] (REQUIRED)
    - NOT CONFIRMED FOR THE TAB: no item of the tab uses it. It is described because its logical table is part of the model behind the tab.
    - Aggregation rule (default): ` SUM("SAMPLESALES"."Fact Revenue"."Revenue (INR)")` [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Revenue (INR)#aggr0]
    - Mapped in table source(s): "Aggregate Revenue", "SAMP_REVENUE_F". What it is mapped from is stated with each source.

- Logical column "Sales Employee Key": not a measure; writeable false; mapped in 1 of 5 table sources. [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Sales Employee Key]
    - NOT CONFIRMED FOR THE TAB: no item of the tab uses it. It is described because its logical table is part of the model behind the tab.
    - Mapped in table source(s): "SAMP_REVENUE_F". What it is mapped from is stated with each source.

- Logical column "Units": MEASURE; writeable false; mapped in 4 of 5 table sources. [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Units] (REQUIRED)
    - Shown to report authors as "Units" in presentation table "Fact Revenue"; name set by hand: false; CONFIRMED FOR THE TAB, items K2, K4, V2-units. [kb:sampleapp::pres::Sample Sales.Fact Revenue.Units] (REQUIRED)
    - Aggregation rule (default): ` SUM("SAMPLESALES"."Fact Revenue"."Units")` [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Units#aggr0]
    - Mapped in table source(s): "Aggregate Revenue By Month", "Aggregate Revenue by Year", "Aggregate Revenue", "SAMP_REVENUE_F". What it is mapped from is stated with each source.

