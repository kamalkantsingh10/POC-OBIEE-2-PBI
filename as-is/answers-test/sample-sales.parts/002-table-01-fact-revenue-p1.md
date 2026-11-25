## Fact Revenue

"Fact Revenue" is a business table of the business model "SAMPLESALES"; the repository declares 5 sources for it [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue]. In this subject area it has 22 fields, of which 9 are measures, and 7 of the 22 are not shown to report authors (counted). Its five sources are "Aggregate Revenue", "Aggregate Revenue By Month", "Aggregate Revenue by Year", "FactCrossJoin" and "SAMP_REVENUE_F", each described under Where the data comes from (counted).

### Keys

Keys of "Fact Revenue": not recorded. The repository declares no key for it (listed in the packet without a Row identifier).

### Measures

| Measure | How it is summarised | Can be typed into | In sources | Evidence |
|---|---|---|---|---|
| All Months Revenue | `SUM("SAMPLESALES"."Fact Revenue"."All Months Revenue")` | no | 2 of 5: "Aggregate Revenue", "SAMP_REVENUE_F" | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.All Months Revenue] [kb:sampleapp::pres::Sample Sales.Fact Revenue.All Months Revenue] [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.All Months Revenue#aggr0] |
| All Products Revenue | `SUM("SAMPLESALES"."Fact Revenue"."All Products Revenue")` | no | 2 of 5: "Aggregate Revenue", "SAMP_REVENUE_F" | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.All Products Revenue] [kb:sampleapp::pres::Sample Sales.Fact Revenue.All Products Revenue] [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.All Products Revenue#aggr0] |
| Discnt Value | `SUM("SAMPLESALES"."Fact Revenue"."Discnt Value")` | no | 2 of 5: "Aggregate Revenue", "SAMP_REVENUE_F" | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Discnt Value] [kb:sampleapp::pres::Sample Sales.Fact Revenue.Discnt Value] [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Discnt Value#aggr0] |
| Preserve | `MAX("SAMPLESALES"."Fact Revenue"."Preserve")` | no | 1 of 5: "FactCrossJoin" | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Preserve] [kb:sampleapp::pres::Sample Sales.Fact Revenue.Preserve] [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Preserve#aggr0] |
| Revenue | `SUM("SAMPLESALES"."Fact Revenue"."Revenue")` | no | 4 of 5: "Aggregate Revenue By Month", "Aggregate Revenue by Year", "Aggregate Revenue", "SAMP_REVENUE_F" | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Revenue] [kb:sampleapp::pres::Sample Sales.Fact Revenue.Revenue] [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Revenue#aggr0] |
| Units | `SUM("SAMPLESALES"."Fact Revenue"."Units")` | no | 4 of 5: "Aggregate Revenue By Month", "Aggregate Revenue by Year", "Aggregate Revenue", "SAMP_REVENUE_F" | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Units] [kb:sampleapp::pres::Sample Sales.Fact Revenue.Units] [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Units#aggr0] |

What "Preserve" holds is described in the section Fact Revenue, under the source "FactCrossJoin" (question Q6, answered).

### Fields

| Field | In sources | Evidence |
|---|---|---|
| Bill Day Dt | 1 of 5: "SAMP_REVENUE_F" | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Bill Day Dt] [kb:sampleapp::pres::Sample Sales.Fact Revenue.Bill Day Dt] |
| Bill Mth Key | 2 of 5: "Aggregate Revenue", "SAMP_REVENUE_F" | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Bill Mth Key] [kb:sampleapp::pres::Sample Sales.Fact Revenue.Bill Mth Key] |
| Office Key | 1 of 5: "SAMP_REVENUE_F" | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Office Key] [kb:sampleapp::pres::Sample Sales.Fact Revenue.Office Key] |
| Order Day Dt | 1 of 5: "SAMP_REVENUE_F" | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Order Day Dt] [kb:sampleapp::pres::Sample Sales.Fact Revenue.Order Day Dt] |
| Order Number | 1 of 5: "SAMP_REVENUE_F" | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Order Number] [kb:sampleapp::pres::Sample Sales.Fact Revenue.Order Number] |
| Order Status | 1 of 5: "SAMP_REVENUE_F" | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Order Status] [kb:sampleapp::pres::Sample Sales.Fact Revenue.Order Status] |
| Order Type | 1 of 5: "SAMP_REVENUE_F" | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Order Type] [kb:sampleapp::pres::Sample Sales.Fact Revenue.Order Type] |
| Preferred Currency | 0 of 5: calculated (formula below) | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Preferred Currency] [kb:sampleapp::pres::Sample Sales.Fact Revenue.Preferred Currency] |
| Prod Key | 2 of 5: "Aggregate Revenue", "SAMP_REVENUE_F" | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Prod Key] [kb:sampleapp::pres::Sample Sales.Fact Revenue.Prod Key] |

"Preferred Currency" is calculated from other fields; its formula, exactly [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Preferred Currency]:

```
INDEXCOL( CASE  VALUEOF(NQ_SESSION."PREFERRED_CURRENCY") WHEN 'gc1' THEN 0 WHEN 'gc2' THEN 1 WHEN 'gc3' THEN 2 END , "SAMPLESALES"."Fact Revenue"."Revenue", "SAMPLESALES"."Fact Revenue"."Revenue (EUR)", "SAMPLESALES"."Fact Revenue"."Revenue (GBP)")
```

It names the variable PREFERRED_CURRENCY and the fields "Revenue", "Revenue (EUR)" and "Revenue (GBP)" of "Fact Revenue" [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Preferred Currency]. The section Values set per user or for the whole system says how the variable is defined. What it holds for a user at run time, and so which measure the field shows, is not recorded (open question Q2).

### Measures and fields report authors do not see

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

What each field is mapped from is stated with each source, in the next part of this section.
