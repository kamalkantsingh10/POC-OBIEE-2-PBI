### Where the data comes from

"Fact Revenue" has five sources. Only "Aggregate Revenue" has a priority group recorded; for the other four the priority group is not recorded (empty; it does not mean lowest). Which source OBIEE uses for a given query is not recorded (open question Q5).

#### Source "Aggregate Revenue"

Active: true. Priority group: `1` [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue]. Level of detail, exactly [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue]:

```
GROUPBYLEVEL("SAMPLESALES"."Time Hierarchy"."Month", "SAMPLESALES"."Products Hierararchy"."Product Detail")
```

That is the level "Month" of the drill path "Time Hierarchy" and the level "Product Detail" of the drill path "Products Hierararchy" (spelled so in the repository) [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue].

No content filter is recorded for this source.

| Database table | Schema | Type | Evidence |
|---|---|---|---|
| SAMP_REVENUE_A (see the section Database table SAMP_REVENUE_A) | `xe.SAMPLESALES` | `none` | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue#table0] |

This source reads one table, so it makes no join.

| Field | Database column | Type, precision, nullable | Evidence |
|---|---|---|---|
| Bill Mth Key | SAMP_REVENUE_A.BILL_MTH_KEY | type DOUBLE, precision 15, nullable true | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue#map0] |
| Discnt Value | SAMP_REVENUE_A.DISCNT_VALUE | type DOUBLE, precision 7, scale 2, nullable true | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue#map1] |
| Prod Key | SAMP_REVENUE_A.PROD_KEY | type DOUBLE, precision 15, nullable true | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue#map2] |
| Revenue | SAMP_REVENUE_A.REVENUE | type DOUBLE, precision 8, scale 2, nullable true | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue#map3] |
| Units | SAMP_REVENUE_A.UNITS | type DOUBLE, precision 15, nullable true | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue#map4] |
| All Products Revenue | SAMP_REVENUE_A.REVENUE | type DOUBLE, precision 8, scale 2, nullable true | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue#map5] |
| All Months Revenue | SAMP_REVENUE_A.REVENUE | type DOUBLE, precision 8, scale 2, nullable true | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue#map6] |
| Revenue (GBP) | SAMP_REVENUE_A.REVENUE | type DOUBLE, precision 8, scale 2, nullable true | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue#map7] |
| Revenue (EUR) | SAMP_REVENUE_A.REVENUE | type DOUBLE, precision 8, scale 2, nullable true | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue#map8] |
| Revenue (INR) | SAMP_REVENUE_A.REVENUE | type DOUBLE, precision 8, scale 2, nullable true | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue#map9] |

In this source six fields are the same database column SAMP_REVENUE_A.REVENUE: "Revenue", "All Products Revenue", "All Months Revenue", "Revenue (GBP)", "Revenue (EUR)" and "Revenue (INR)" [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue#map3] (open questions Q3 and Q4).

#### Source "Aggregate Revenue By Month"

Active: true. Priority group not recorded (empty; it does not mean lowest) [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue By Month]. Level of detail, exactly: `GROUPBYLEVEL("SAMPLESALES"."Time Hierarchy"."Month")`, the level "Month" of the drill path "Time Hierarchy" [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue By Month].

No content filter is recorded for this source.

| Database table | Schema | Type | Evidence |
|---|---|---|---|
| SAMP_REVENUE_A_MTH (see the section Database table SAMP_REVENUE_A_MTH) | `xe.SAMPLESALES` | `none` | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue By Month#table0] |

This source reads one table, so it makes no join.

| Field | Database column | Type, precision, nullable | Evidence |
|---|---|---|---|
| Revenue | SAMP_REVENUE_A_MTH.REVENUE | type DOUBLE, precision 22, nullable true | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue By Month#map0] |
| Units | SAMP_REVENUE_A_MTH.UNITS | type DOUBLE, precision 22, nullable true | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue By Month#map1] |
| BILL_MTH_KEY | SAMP_REVENUE_A_MTH.BILL_MTH_KEY | type DOUBLE, precision 22, nullable true | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue By Month#map2] |
| DISCNT_VALUE | SAMP_REVENUE_A_MTH.DISCNT_VALUE | type DOUBLE, precision 22, nullable true | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue By Month#map3] |

#### Source "Aggregate Revenue by Year"

Active: true. Priority group not recorded (empty; it does not mean lowest) [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue by Year]. Level of detail, exactly: `GROUPBYLEVEL("SAMPLESALES"."Time Hierarchy"."Year")`, the level "Year" of the drill path "Time Hierarchy" [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue by Year].

No content filter is recorded for this source.

| Database table | Schema | Type | Evidence |
|---|---|---|---|
| SAMP_REVENUE_A_YR (see the section Database table SAMP_REVENUE_A_YR) | `xe.SAMPLESALES` | `none` | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue by Year#table0] |

This source reads one table, so it makes no join.

| Field | Database column | Type, precision, nullable | Evidence |
|---|---|---|---|
| Revenue | SAMP_REVENUE_A_YR.REVENUE | type DOUBLE, precision 22, nullable true | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue by Year#map0] |
| Units | SAMP_REVENUE_A_YR.UNITS | type DOUBLE, precision 22, nullable true | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue by Year#map1] |
| DISCNT_VALUE | SAMP_REVENUE_A_YR.DISCNT_VALUE | type DOUBLE, precision 22, nullable true | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue by Year#map2] |
| BILL_YEAR_KEY | SAMP_REVENUE_A_YR.BILL_YEAR_KEY | type DOUBLE, nullable true | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue by Year#map3] |

#### Source "FactCrossJoin"

Active: true. Priority group not recorded (empty; it does not mean lowest) [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.FactCrossJoin]. Level of detail: not recorded. The repository gives this source no level of detail [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.FactCrossJoin].

No content filter is recorded for this source.

| Database table | Schema | Type | Evidence |
|---|---|---|---|
| FactCrossJoin (see the section Database table FactCrossJoin) | `xe.SAMPLESALES` | `select` (not a database table: it is a query kept in the repository) | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.FactCrossJoin#table0] |

This source reads one table, so it makes no join.

| Field | Database column | Type, precision, nullable | Evidence |
|---|---|---|---|
| Preserve | FactCrossJoin.Preserve | type DOUBLE, nullable not stated | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.FactCrossJoin#map0] |

What "Preserve" holds is not recorded (open question Q6).

#### Source "SAMP_REVENUE_F"

Active: true. Priority group not recorded (empty; it does not mean lowest) [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.SAMP_REVENUE_F]. Level of detail, exactly [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.SAMP_REVENUE_F]:

```
GROUPBYLEVEL("SAMPLESALES"."Sales Rep Hierarchy"."Detail", "SAMPLESALES"."Time Hierarchy"."Dim Time Detail", "SAMPLESALES"."Office Hierarchy"."Office Detail", "SAMPLESALES"."Products Hierararchy"."Product Detail")
```

That is the level "Detail" of "Sales Rep Hierarchy", "Dim Time Detail" of "Time Hierarchy", "Office Detail" of "Office Hierarchy" and "Product Detail" of "Products Hierararchy" [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.SAMP_REVENUE_F].

No content filter is recorded for this source.

| Database table | Schema | Type | Evidence |
|---|---|---|---|
| SAMP_REVENUE_F (see the section Database table SAMP_REVENUE_F) | `xe.SAMPLESALES` | `none` | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.SAMP_REVENUE_F#table0] |

This source reads one table, so it makes no join.

| Field | Database column | Type, precision, nullable | Evidence |
|---|---|---|---|
| Bill Day Dt | SAMP_REVENUE_F.BILL_DAY_DT | type DATE, nullable true | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.SAMP_REVENUE_F#map0] |
| Bill Mth Key | SAMP_REVENUE_F.BILL_MTH_KEY | type DOUBLE, precision 15, nullable true | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.SAMP_REVENUE_F#map1] |
| Units | SAMP_REVENUE_F.UNITS | type DOUBLE, precision 15, nullable true | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.SAMP_REVENUE_F#map10] |
| All Products Revenue | SAMP_REVENUE_F.REVENUE | type DOUBLE, precision 7, scale 2, nullable true | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.SAMP_REVENUE_F#map11] |
| All Months Revenue | SAMP_REVENUE_F.REVENUE | type DOUBLE, precision 7, scale 2, nullable true | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.SAMP_REVENUE_F#map12] |
| Sales Employee Key | SAMP_REVENUE_F.EMPLOYEE_KEY | type INT, nullable not stated | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.SAMP_REVENUE_F#map13] |
| Revenue (GBP) | `"xe".""."SAMPLESALES"."SAMP_REVENUE_F"."REVENUE" * 0.62` | type DOUBLE, precision 7, scale 2, nullable true | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.SAMP_REVENUE_F#map14] |
| Revenue (EUR) | `"xe".""."SAMPLESALES"."SAMP_REVENUE_F"."REVENUE" * 0.76` | type DOUBLE, precision 7, scale 2, nullable true | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.SAMP_REVENUE_F#map15] |
| Revenue (INR) | `"xe".""."SAMPLESALES"."SAMP_REVENUE_F"."REVENUE" * 54.68` | type DOUBLE, precision 7, scale 2, nullable true | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.SAMP_REVENUE_F#map16] |
| Discnt Value | SAMP_REVENUE_F.DISCNT_VALUE | type DOUBLE, precision 6, scale 2, nullable true | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.SAMP_REVENUE_F#map2] |
| Office Key | SAMP_REVENUE_F.OFFICE_KEY | type DOUBLE, precision 15, nullable true | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.SAMP_REVENUE_F#map3] |
| Order Day Dt | SAMP_REVENUE_F.ORDER_DAY_DT | type DATETIME, nullable true | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.SAMP_REVENUE_F#map4] |
| Order Number | SAMP_REVENUE_F.ORDER_NUMBER | type DOUBLE, precision 15, nullable true | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.SAMP_REVENUE_F#map5] |
| Order Status | SAMP_REVENUE_F.ORDER_STATUS | type VARCHAR, precision 11, nullable true | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.SAMP_REVENUE_F#map6] |
| Order Type | SAMP_REVENUE_F.ORDER_TYPE | type VARCHAR, precision 8, nullable true | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.SAMP_REVENUE_F#map7] |
| Prod Key | SAMP_REVENUE_F.PROD_KEY | type DOUBLE, precision 15, nullable true | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.SAMP_REVENUE_F#map8] |
| Revenue | SAMP_REVENUE_F.REVENUE | type DOUBLE, precision 7, scale 2, nullable true | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.SAMP_REVENUE_F#map9] |

Put mechanically: in this source "Revenue (GBP)" is the database column REVENUE multiplied by 0.62, "Revenue (EUR)" multiplied by 0.76 and "Revenue (INR)" multiplied by 54.68 [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.SAMP_REVENUE_F#map14] [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.SAMP_REVENUE_F#map15] [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.SAMP_REVENUE_F#map16] (open question Q4).

### Points to note

- "Revenue", "All Products Revenue" and "All Months Revenue" are all mapped from the same database column REVENUE, in "Aggregate Revenue" [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue#map5] and in "SAMP_REVENUE_F" [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.SAMP_REVENUE_F#map12], and all three are summarised with SUM [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Revenue#aggr0] [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.All Products Revenue#aggr0] [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.All Months Revenue#aggr0]. What distinguishes them is not recorded (open question Q3).
- "Revenue (EUR)", "Revenue (GBP)" and "Revenue (INR)" are the column REVENUE multiplied by a fixed number in "SAMP_REVENUE_F" [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.SAMP_REVENUE_F#map15], and the column REVENUE with no multiplication in "Aggregate Revenue" [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue#map8] (open question Q4).
- The sources "Aggregate Revenue By Month" and "Aggregate Revenue by Year" map "Revenue" and "Units", but map the discount column to the field "DISCNT_VALUE" and the month or year key to "BILL_MTH_KEY" or "BILL_YEAR_KEY", none of which report authors see; they map neither "Discnt Value" nor "Bill Mth Key" [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue By Month#map2] [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue By Month#map3] [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue by Year#map2] [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue by Year#map3] [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.DISCNT_VALUE] [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.BILL_MTH_KEY] [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.BILL_YEAR_KEY].
- The source "FactCrossJoin" has no level of detail recorded and reads a query kept in the repository, not a database table [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.FactCrossJoin] [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.FactCrossJoin#table0].
- "Preferred Currency" is mapped in no source; its formula names the variable PREFERRED_CURRENCY and the fields "Revenue", "Revenue (EUR)" and "Revenue (GBP)"; "Revenue (INR)" is not among them [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Preferred Currency] (open question Q2).
- The table holds both "DISCNT_VALUE" (a field, not shown) and "Discnt Value" (a measure, shown), mapped in different sources [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.DISCNT_VALUE] [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Discnt Value].
