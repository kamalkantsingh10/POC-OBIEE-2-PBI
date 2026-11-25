### Model table Fact Revenue (part 1 of 2)

Proposal (not from OBIEE; to be signed): a table of the Power BI model named "Fact Revenue", like the business table, read from the new tables named with its sources in part 2 of this section.

The business table "Fact Revenue" is in business model "SAMPLESALES", and the repository declares 5 table sources for it. [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue] It has 22 columns in this slice, of which 9 are measures; 18 are not confirmed for the tab. Its sources are "Aggregate Revenue", "Aggregate Revenue By Month", "Aggregate Revenue by Year", "FactCrossJoin" and "SAMP_REVENUE_F"; they are described in part 2. The tab uses four of its fields: "Discnt Value", "Preferred Currency", "Revenue" and "Units".

#### Keys

Keys of logical table "Fact Revenue": not recorded. The repository declares no key for it.

#### Fields carried into the model

| Field | From new table.column | Type, precision, nullable as the packet prints them | Tab items | Evidence |
|---|---|---|---|---|
| Preferred Currency | none: mapped in 0 of 5 table sources; calculated from other logical objects | not a measure; writeable false; type not printed | N1-currency | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Preferred Currency] |

"Preferred Currency" is shown to report authors as "Preferred Currency" in presentation table "Fact Revenue"; name set by hand: false; confirmed for the tab, item N1-currency. [kb:sampleapp::pres::Sample Sales.Fact Revenue.Preferred Currency] Its formula, calculated from other logical objects, is:

```
 INDEXCOL( CASE  VALUEOF(NQ_SESSION."PREFERRED_CURRENCY") WHEN 'gc1' THEN 0 WHEN 'gc2' THEN 1 WHEN 'gc3' THEN 2 END , "SAMPLESALES"."Fact Revenue"."Revenue", "SAMPLESALES"."Fact Revenue"."Revenue (EUR)", "SAMPLESALES"."Fact Revenue"."Revenue (GBP)")
```

[kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Preferred Currency]

The formula names the variable PREFERRED_CURRENCY inside VALUEOF(NQ_SESSION. …) and the measures "Revenue", "Revenue (EUR)" and "Revenue (GBP)" of "Fact Revenue" [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Preferred Currency]. This is a switch on a value set per user: no definition is proposed until decision Q2 is taken. "Revenue (EUR)" [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Revenue (EUR)] and "Revenue (GBP)" [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Revenue (GBP)] are not confirmed for the tab and are not designed as measures here; they are needed only if decision Q2 keeps the switch. Both are measures, writeable false, mapped in 2 of 5 table sources, with the aggregation rules ` SUM("SAMPLESALES"."Fact Revenue"."Revenue (EUR)")` [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Revenue (EUR)#aggr0] and ` SUM("SAMPLESALES"."Fact Revenue"."Revenue (GBP)")` [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Revenue (GBP)#aggr0]; how each source maps them is stated in part 2. The variable is described in the section Values set per user or for the whole system. See also as-is document, question Q2.

The 12 fields of this part that are not measures and not confirmed ("BILL_MTH_KEY", "BILL_YEAR_KEY", "Bill Day Dt", "Bill Mth Key", "DISCNT_VALUE", "Office Key", "Order Day Dt", "Order Number", "Order Status", "Order Type", "Prod Key", "Sales Employee Key") are not carried as fields. Some of them are key fields: the relationships to the dimension tables may still need their columns; see Relationships.

#### Measure: Revenue

- Replaces: the OBIEE measure "Revenue" of "Fact Revenue" [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Revenue], shown to report authors as "Revenue" [kb:sampleapp::pres::Sample Sales.Fact Revenue.Revenue]; confirmed for the tab, items K1, K3, K4, V1-net-revenue, V2-net-revenue, V3-net-revenue.
- OBIEE formula it replaces, exactly: ` SUM("SAMPLESALES"."Fact Revenue"."Revenue")` [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Revenue#aggr0]
- Read from: mapped in 4 of 5 table sources ("Aggregate Revenue By Month", "Aggregate Revenue by Year", "Aggregate Revenue", "SAMP_REVENUE_F"); the column each maps it from is stated with each source in part 2. Which new tables are kept is decision Q3 (part 2).
- Writeable in OBIEE: false.
- Proposal (not from OBIEE; to be signed):

      Revenue = SUM('Fact Revenue'[Revenue])

#### Measure: Discount Value

- Replaces: the OBIEE measure "Discnt Value" of "Fact Revenue" [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Discnt Value], shown to report authors as "Discnt Value" [kb:sampleapp::pres::Sample Sales.Fact Revenue.Discnt Value]; confirmed for the tab, items K1, K3, K4, V1-net-revenue, V2-net-revenue, V2-discount, V3-net-revenue.
- OBIEE formula it replaces, exactly: ` SUM("SAMPLESALES"."Fact Revenue"."Discnt Value")` [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Discnt Value#aggr0]
- Read from: mapped in 2 of 5 table sources ("Aggregate Revenue", "SAMP_REVENUE_F"); the column each maps it from is stated with each source in part 2.
- Writeable in OBIEE: false.
- Proposal (not from OBIEE; to be signed):

      Discount Value = SUM('Fact Revenue'[Discnt Value])

#### Measure: Units

- Replaces: the OBIEE measure "Units" of "Fact Revenue" [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Units], shown to report authors as "Units" [kb:sampleapp::pres::Sample Sales.Fact Revenue.Units]; confirmed for the tab, items K2, K4, V2-units.
- OBIEE formula it replaces, exactly: ` SUM("SAMPLESALES"."Fact Revenue"."Units")` [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Units#aggr0]
- Read from: mapped in 4 of 5 table sources ("Aggregate Revenue By Month", "Aggregate Revenue by Year", "Aggregate Revenue", "SAMP_REVENUE_F"); the column each maps it from is stated with each source in part 2.
- Writeable in OBIEE: false.
- Proposal (not from OBIEE; to be signed):

      Units = SUM('Fact Revenue'[Units])

#### Not used by this tab

The measures "All Months Revenue", "All Products Revenue", "Preserve" and "Revenue (INR)" are not confirmed for the tab and are not designed; they are listed under Not described.
