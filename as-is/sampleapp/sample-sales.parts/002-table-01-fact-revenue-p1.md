## Fact Revenue

"Fact Revenue" is a business table of the business model "SAMPLESALES"; the repository declares 5 sources for it [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue]. In this subject area it has 22 fields, of which 9 are measures; 7 of the 22 are not shown to report authors here (counted; no Row identifier). Its 5 sources are "Aggregate Revenue", "Aggregate Revenue By Month", "Aggregate Revenue by Year", "FactCrossJoin" and "SAMP_REVENUE_F"; each is described under "Where the data comes from" below (counted; no Row identifier).

### Keys

Keys of the business table "Fact Revenue": not recorded. The repository declares no key for it (listed in the packet without a Row identifier).

### Measures

All measures below are shown to report authors in the table "Fact Revenue" under the same name.

| Measure | How it is summarised | Can be typed into | In sources | Evidence |
|---|---|---|---|---|
| All Months Revenue | `SUM("SAMPLESALES"."Fact Revenue"."All Months Revenue")` | no | 2 of 5: "Aggregate Revenue", "SAMP_REVENUE_F" | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.All Months Revenue] [kb:sampleapp::pres::Sample Sales.Fact Revenue.All Months Revenue] [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.All Months Revenue#aggr0] |
| All Products Revenue | `SUM("SAMPLESALES"."Fact Revenue"."All Products Revenue")` | no | 2 of 5: "Aggregate Revenue", "SAMP_REVENUE_F" | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.All Products Revenue] [kb:sampleapp::pres::Sample Sales.Fact Revenue.All Products Revenue] [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.All Products Revenue#aggr0] |
| Discnt Value | `SUM("SAMPLESALES"."Fact Revenue"."Discnt Value")` | no | 2 of 5: "Aggregate Revenue", "SAMP_REVENUE_F" | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Discnt Value] [kb:sampleapp::pres::Sample Sales.Fact Revenue.Discnt Value] [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Discnt Value#aggr0] |
| Preserve | `MAX("SAMPLESALES"."Fact Revenue"."Preserve")` | no | 1 of 5: "FactCrossJoin" | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Preserve] [kb:sampleapp::pres::Sample Sales.Fact Revenue.Preserve] [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Preserve#aggr0] |
| Revenue | `SUM("SAMPLESALES"."Fact Revenue"."Revenue")` | no | 4 of 5: "Aggregate Revenue By Month", "Aggregate Revenue by Year", "Aggregate Revenue", "SAMP_REVENUE_F" | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Revenue] [kb:sampleapp::pres::Sample Sales.Fact Revenue.Revenue] [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Revenue#aggr0] |
| Units | `SUM("SAMPLESALES"."Fact Revenue"."Units")` | no | 4 of 5: "Aggregate Revenue By Month", "Aggregate Revenue by Year", "Aggregate Revenue", "SAMP_REVENUE_F" | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Units] [kb:sampleapp::pres::Sample Sales.Fact Revenue.Units] [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Units#aggr0] |

What each measure is mapped from is stated with each source under "Where the data comes from".

### Fields

All fields below are shown to report authors in the table "Fact Revenue" under the same name.

| Field | In sources | Evidence |
|---|---|---|
| Bill Day Dt | 1 of 5: "SAMP_REVENUE_F" | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Bill Day Dt] [kb:sampleapp::pres::Sample Sales.Fact Revenue.Bill Day Dt] |
| Bill Mth Key | 2 of 5: "Aggregate Revenue", "SAMP_REVENUE_F" | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Bill Mth Key] [kb:sampleapp::pres::Sample Sales.Fact Revenue.Bill Mth Key] |
| Office Key | 1 of 5: "SAMP_REVENUE_F" | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Office Key] [kb:sampleapp::pres::Sample Sales.Fact Revenue.Office Key] |
| Order Day Dt | 1 of 5: "SAMP_REVENUE_F" | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Order Day Dt] [kb:sampleapp::pres::Sample Sales.Fact Revenue.Order Day Dt] |
| Order Number | 1 of 5: "SAMP_REVENUE_F" | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Order Number] [kb:sampleapp::pres::Sample Sales.Fact Revenue.Order Number] |
| Order Status | 1 of 5: "SAMP_REVENUE_F" | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Order Status] [kb:sampleapp::pres::Sample Sales.Fact Revenue.Order Status] |
| Order Type | 1 of 5: "SAMP_REVENUE_F" | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Order Type] [kb:sampleapp::pres::Sample Sales.Fact Revenue.Order Type] |
| Preferred Currency | 0 of 5 (calculated, see below) | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Preferred Currency] [kb:sampleapp::pres::Sample Sales.Fact Revenue.Preferred Currency] |
| Prod Key | 2 of 5: "Aggregate Revenue", "SAMP_REVENUE_F" | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Prod Key] [kb:sampleapp::pres::Sample Sales.Fact Revenue.Prod Key] |

"Preferred Currency" is calculated from other fields of the model and is mapped in no source. Its formula, exactly [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Preferred Currency]:

```
INDEXCOL( CASE  VALUEOF(NQ_SESSION."PREFERRED_CURRENCY") WHEN 'gc1' THEN 0 WHEN 'gc2' THEN 1 WHEN 'gc3' THEN 2 END , "SAMPLESALES"."Fact Revenue"."Revenue", "SAMPLESALES"."Fact Revenue"."Revenue (EUR)", "SAMPLESALES"."Fact Revenue"."Revenue (GBP)")
```

It reads the variable PREFERRED_CURRENCY and the fields "Revenue", "Revenue (EUR)" and "Revenue (GBP)" of this table. Put mechanically: the value 'gc1' of the variable gives 0, 'gc2' gives 1 and 'gc3' gives 2; the three fields are listed after it. Whether the repository defines PREFERRED_CURRENCY is stated in the section Values set per user or for the whole system. Which value a user has at run time is not recorded (open question Q2).

### Measures and fields report authors do not see

These have no field that report authors see in this subject area. They are described because their business table is part of the model behind the subject area.

| Measure | How it is summarised | Can be typed into | In sources | Evidence |
|---|---|---|---|---|
| Revenue (EUR) | `SUM("SAMPLESALES"."Fact Revenue"."Revenue (EUR)")` | no | 2 of 5: "Aggregate Revenue", "SAMP_REVENUE_F" | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Revenue (EUR)] [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Revenue (EUR)#aggr0] |
| Revenue (GBP) | `SUM("SAMPLESALES"."Fact Revenue"."Revenue (GBP)")` | no | 2 of 5: "Aggregate Revenue", "SAMP_REVENUE_F" | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Revenue (GBP)] [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Revenue (GBP)#aggr0] |
| Revenue (INR) | `SUM("SAMPLESALES"."Fact Revenue"."Revenue (INR)")` | no | 2 of 5: "Aggregate Revenue", "SAMP_REVENUE_F" | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Revenue (INR)] [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Revenue (INR)#aggr0] |

| Field | In sources | Evidence |
|---|---|---|
| BILL_MTH_KEY | 1 of 5: "Aggregate Revenue By Month" | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.BILL_MTH_KEY] |
| BILL_YEAR_KEY | 1 of 5: "Aggregate Revenue by Year" | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.BILL_YEAR_KEY] |
| DISCNT_VALUE | 2 of 5: "Aggregate Revenue By Month", "Aggregate Revenue by Year" | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.DISCNT_VALUE] |
| Sales Employee Key | 1 of 5: "SAMP_REVENUE_F" | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Sales Employee Key] |

The business table holds both a field "DISCNT_VALUE" (not a measure, not shown) and a measure "Discnt Value" (shown), and both a field "BILL_MTH_KEY" (not shown) and a field "Bill Mth Key" (shown); they are mapped in different sources [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.DISCNT_VALUE] [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Discnt Value] [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.BILL_MTH_KEY] [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Bill Mth Key]. What each is mapped from is stated with each source below.
