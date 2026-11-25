Status: draft, not reviewed

# Sample Sales — the semantic model as it exists today

**What this is.** A plain-language description of the Sample Sales subject area in OBIEE: what report authors see, what each number is, how the tables relate and where the data comes from. It describes the existing system only.

**How to read it.** Statements of fact end with a marker in square brackets. The marker names the row of the Knowledge Base the statement came from, so any statement can be checked. A marker that starts with "answer" means the statement rests on an answer a person gave, not on OBIEE. Text shown as code is copied from OBIEE exactly. Where the export does not settle something, the text points to a numbered open question near the end; each says how to find the answer and has an empty Answer line for you.

| | |
|---|---|
| Subject area | Sample Sales |
| Estate | sampleapp |
| Knowledge Base extracted | 2026-11-23T09:33:41Z |
| Reports and dashboards | not examined |

## The model in one page

**What was not examined.** Only the repository half of the estate was extracted. The estate has no Web Catalog half, so reports and dashboards were not examined; where this document shows 0 reports, that means not looked at (from the manifest). The extraction recorded no error about an object of this subject area, and left 130 kinds of source content unread, among them the keys of tables (from the errors and unparsed tables). The section "What the export did not capture" lists them.

**What the subject area holds.** "Sample Sales" is built on the business model "SAMPLESALES" [kb:sampleapp::pres::Sample Sales]. Report authors see 6 tables with 49 fields, 8 of them measures; behind them are 7 business tables with 63 fields, 11 of them measures, fed by 12 sources that read 13 database tables; there are 7 business-model joins, 17 database joins and 4 drill paths (counted).

- **Fact Revenue**: 22 fields, 9 measures, 5 sources [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue]. Report authors see the measures All Months Revenue, All Products Revenue, Discnt Value, Preserve, Revenue and Units; Revenue (EUR), Revenue (GBP) and Revenue (INR) exist and are not shown.
- **Fact Target**: 5 fields, the measures Revenue and Units, one source reading SAMP_TARGETS_F [kb:sampleapp::bmm::SAMPLESALES.Fact Target.src.SAMP_TARGETS_F].
- **Dim Offices**: 10 fields, 9 shown, all from SAMP_OFFICES_D [kb:sampleapp::bmm::SAMPLESALES.Dim Offices.src.SAMP_OFFICES_D].
- **Dim Products**: 12 fields, 9 shown, 11 of them from SAMP_PRODUCTS_D [kb:sampleapp::bmm::SAMPLESALES.Dim Products.src.SAMP_PRODUCTS_D]; and **Dim Products (Translated)**, 3 fields, none shown, from SAMP_PRODUCTS_D_TRANSLATION [kb:sampleapp::bmm::SAMPLESALES.Dim Products (Translated).src.SAMP_PRODUCTS_D_TRANSLATION].
- **Dim Sales Rep**: 5 fields, all shown, one source reading EMPLOYEE and EMP_PARENT_CHILD [kb:sampleapp::bmm::SAMPLESALES.Dim Sales Rep.src.LTS Employee].
- **Dim Time**: 6 fields, all shown, two sources: one reading SAMP_TIME_DAY_D [kb:sampleapp::bmm::SAMPLESALES.Dim Time.src.LTS Time Day] and one reading SAMP_TIME_MTH_D [kb:sampleapp::bmm::SAMPLESALES.Dim Time.src.LTS Time Month].
- **Drill paths**: "Office Hierarchy" [kb:sampleapp::bmm::SAMPLESALES.Office Hierarchy], "Products Hierararchy" [kb:sampleapp::bmm::SAMPLESALES.Products Hierararchy], "Time Hierarchy" [kb:sampleapp::bmm::SAMPLESALES.Time Hierarchy] and the parent-child "Sales Rep Hierarchy" [kb:sampleapp::bmm::SAMPLESALES.Sales Rep Hierarchy]. All four are offered to report authors.

**What a reader must know before relying on a number.**

1. **Revenue and Units of Fact Revenue can come from four database tables.** The five sources are all active and a priority group is recorded for one of them only [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue]. Which table a number comes from, and whether the tables agree, is not recorded (open question Q7).
2. **The three currency measures are mapped two ways.** Revenue (GBP), (EUR) and (INR) are the database column REVENUE multiplied by 0.62, 0.76 and 54.68 in the source "SAMP_REVENUE_F" [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.SAMP_REVENUE_F#map15], and REVENUE with no multiplication in the source "Aggregate Revenue" [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue#map8] (open question Q6).
3. **The field "Preferred Currency" depends on the user.** It is a formula that reads the value PREFERRED_CURRENCY and names Revenue, Revenue (EUR) and Revenue (GBP), not Revenue (INR) [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Preferred Currency]. The query recorded as setting that value names PREFERED_CURRENCY, with one R, which the repository does not define [kb:sampleapp::var::init.initBlockPrefCurrency] (open questions Q2 and Q16).
4. **The target measures can be typed into, and one of them is the default measure.** Both measures of Fact Target are writeable [kb:sampleapp::bmm::SAMPLESALES.Fact Target.Revenue] [kb:sampleapp::bmm::SAMPLESALES.Fact Target.Units], and "Revenue" of "Fact Target" is the measure OBIEE uses when a query names none [kb:sampleapp::pres::Sample Sales]. What the targets hold and at what level of time is not recorded (open question Q8).
5. **Names and texts that depend on the user are not recorded.** The displayed names of the table "Dim Time" and of its six fields are formulas [kb:sampleapp::pres::Sample Sales.Dim Time] [kb:sampleapp::pres::Sample Sales.Dim Time.Calendar Date], and "Prod Dsc (Translated)" is a lookup by the value WEBLANGUAGE [kb:sampleapp::bmm::SAMPLESALES.Dim Products.Prod Dsc (Translated)]. Nine of the ten values the subject area names are used by name and not defined in the repository (counted) (open questions Q1, Q9 and Q11).

Also to note: the measure "Preserve" comes from a query kept in the repository, not from a database table [kb:sampleapp::phys::xe.SAMPLESALES.FactCrossJoin.def.DefaultMulDB] (open question Q3); no row filter is recorded, and six permissions are recorded, all on the two measures of Fact Target [kb:sampleapp::sec::obj.ObjPriv_1500:18131355661677.o0] (open question Q17); no key of any table is recorded (open question Q19).

**Open questions.** 19 questions are open, Q1 to Q19. None has been answered yet, and none was settled by a later section. They are listed near the end, each with how to find the answer.

## What report authors see

"Sample Sales" is a subject area built on the business model "SAMPLESALES" [kb:sampleapp::pres::Sample Sales]. It has one other name (alias): `SAMPLESALES` [kb:sampleapp::pres::Sample Sales]. When a query names no measure, OBIEE uses the measure "Revenue" of "Fact Target" (the "implicit fact column") [kb:sampleapp::pres::Sample Sales].

### Tables report authors see

| Table | Number of fields | Business table behind it | Evidence |
|---|---|---|---|
| Dim Offices | 9 | Dim Offices | [kb:sampleapp::pres::Sample Sales.Dim Offices] |
| Dim Products | 9 | Dim Products | [kb:sampleapp::pres::Sample Sales.Dim Products] |
| Dim Sales Rep | 5 | Dim Sales Rep | [kb:sampleapp::pres::Sample Sales.Dim Sales Rep] |
| Dim Time | 6 | Dim Time | [kb:sampleapp::pres::Sample Sales.Dim Time] |
| Fact Revenue | 15 | Fact Revenue | [kb:sampleapp::pres::Sample Sales.Fact Revenue] |
| Fact Target | 5 | Fact Target | [kb:sampleapp::pres::Sample Sales.Fact Target] |

The name shown to users for the table "Dim Time" is not a fixed text. It is recorded as `VALUEOF(NQ_SESSION.CN_Sample_Sales_Dim_Time)` [kb:sampleapp::pres::Sample Sales.Dim Time]. This names the value `CN_Sample_Sales_Dim_Time`; whether the repository defines it is stated in the section "Values set per user or for the whole system". What name a report author actually sees for this table is not recorded (open question Q1).

### Counts

These numbers were counted from the tables of the slice and are copied as given (counted).

- Tables report authors see: 6; fields they see: 49, of which measures: 8.
- Business tables described: 7; their fields: 63, of which measures: 11. Measures not shown to report authors are described with their business table.
- Sources: 12; database tables: 13; database columns used: 74.
- Business-model joins: 7; database joins: 17.
- Drill paths: 4; drill paths offered to report authors: 4.
- Variables: 1; security rows: 7.
- Reports built on the subject area: 0; dashboards placing them: 0; saved filters they use: 0. Reports, dashboards and saved filters were not examined (see "What was examined"), so 0 here means not looked at, not that none exist.
- Errors about objects of the slice: 0; kinds of source content not read: 130. Both are listed in the section "What the export did not capture".

### Items of the estate that are in no subject area, or not in this one

These exist in the full Knowledge Base and are not described in any section of this document (counts from the manifest).

- Object permissions whose target was not recorded: 2.
- Role privileges of packages that no permission of this subject area goes through: 0.
- Queries that set a value ("init blocks") but set no variable of this subject area: 1.
- Variables that no expression of this subject area names: 1.

### What was examined

- Estate: sampleapp. Of the two halves of an estate, the one extracted is: rpd (from the manifest).
- The estate has no Web Catalog half, so reports and dashboards were not examined (from the manifest).
- The Knowledge Base was extracted at 2026-11-23T09:33:41Z by extractor version 0.1.0 (from the manifest).
- The sections of this document follow the units of the slice, in this order: overview, Fact Revenue, Fact Target, Dim Offices, Dim Products, Dim Products (Translated), Dim Sales Rep, Dim Time, joins, drill paths, variables, security, reports, gaps (from the manifest). The manifest also names a unit for saved filters "when there are any"; the slice holds none.

## Fact Revenue

Fact Revenue is a business table of the model "SAMPLESALES"; the repository declares 5 sources for it [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue]. It has 22 fields, 9 of them measures; 7 of the 22 are not shown to report authors in this subject area (counted). Its 5 sources are "Aggregate Revenue", "Aggregate Revenue By Month", "Aggregate Revenue by Year", "FactCrossJoin" and "SAMP_REVENUE_F"; each is described under "Where the data comes from" (counted).

### Measures

None of these measures can be typed into (writeable is recorded as false for each). What each measure holds in business terms is not recorded.

| Measure | How it is summarised | Can be typed into | In sources | Evidence |
|---|---|---|---|---|
| All Months Revenue | Sum: ` SUM("SAMPLESALES"."Fact Revenue"."All Months Revenue")` | no | 2 of 5: Aggregate Revenue, SAMP_REVENUE_F | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.All Months Revenue] [kb:sampleapp::pres::Sample Sales.Fact Revenue.All Months Revenue] [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.All Months Revenue#aggr0] |
| All Products Revenue | Sum: ` SUM("SAMPLESALES"."Fact Revenue"."All Products Revenue")` | no | 2 of 5: Aggregate Revenue, SAMP_REVENUE_F | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.All Products Revenue] [kb:sampleapp::pres::Sample Sales.Fact Revenue.All Products Revenue] [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.All Products Revenue#aggr0] |
| Discnt Value | Sum: ` SUM("SAMPLESALES"."Fact Revenue"."Discnt Value")` | no | 2 of 5: Aggregate Revenue, SAMP_REVENUE_F | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Discnt Value] [kb:sampleapp::pres::Sample Sales.Fact Revenue.Discnt Value] [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Discnt Value#aggr0] |
| Preserve | Maximum: ` MAX("SAMPLESALES"."Fact Revenue"."Preserve")` | no | 1 of 5: FactCrossJoin | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Preserve] [kb:sampleapp::pres::Sample Sales.Fact Revenue.Preserve] [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Preserve#aggr0] |
| Revenue | Sum: ` SUM("SAMPLESALES"."Fact Revenue"."Revenue")` | no | 4 of 5: Aggregate Revenue By Month, Aggregate Revenue by Year, Aggregate Revenue, SAMP_REVENUE_F | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Revenue] [kb:sampleapp::pres::Sample Sales.Fact Revenue.Revenue] [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Revenue#aggr0] |
| Units | Sum: ` SUM("SAMPLESALES"."Fact Revenue"."Units")` | no | 4 of 5: Aggregate Revenue By Month, Aggregate Revenue by Year, Aggregate Revenue, SAMP_REVENUE_F | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Units] [kb:sampleapp::pres::Sample Sales.Fact Revenue.Units] [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Units#aggr0] |

The name report authors see is the same as the business table's name for all six. What "Preserve" holds, and why it is the only measure summarised as a maximum, is not recorded (open question Q3).

### Fields

| Field | In sources | Evidence |
|---|---|---|
| Bill Day Dt | 1 of 5: SAMP_REVENUE_F | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Bill Day Dt] [kb:sampleapp::pres::Sample Sales.Fact Revenue.Bill Day Dt] |
| Bill Mth Key | 2 of 5: Aggregate Revenue, SAMP_REVENUE_F | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Bill Mth Key] [kb:sampleapp::pres::Sample Sales.Fact Revenue.Bill Mth Key] |
| Office Key | 1 of 5: SAMP_REVENUE_F | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Office Key] [kb:sampleapp::pres::Sample Sales.Fact Revenue.Office Key] |
| Order Day Dt | 1 of 5: SAMP_REVENUE_F | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Order Day Dt] [kb:sampleapp::pres::Sample Sales.Fact Revenue.Order Day Dt] |
| Order Number | 1 of 5: SAMP_REVENUE_F | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Order Number] [kb:sampleapp::pres::Sample Sales.Fact Revenue.Order Number] |
| Order Status | 1 of 5: SAMP_REVENUE_F | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Order Status] [kb:sampleapp::pres::Sample Sales.Fact Revenue.Order Status] |
| Order Type | 1 of 5: SAMP_REVENUE_F | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Order Type] [kb:sampleapp::pres::Sample Sales.Fact Revenue.Order Type] |
| Preferred Currency | 0 of 5: it is calculated, see below | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Preferred Currency] [kb:sampleapp::pres::Sample Sales.Fact Revenue.Preferred Currency] |
| Prod Key | 2 of 5: Aggregate Revenue, SAMP_REVENUE_F | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Prod Key] [kb:sampleapp::pres::Sample Sales.Fact Revenue.Prod Key] |

"Preferred Currency" is calculated from other fields of the business model. Its formula, exactly as recorded [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Preferred Currency]:

```
 INDEXCOL( CASE  VALUEOF(NQ_SESSION."PREFERRED_CURRENCY") WHEN 'gc1' THEN 0 WHEN 'gc2' THEN 1 WHEN 'gc3' THEN 2 END , "SAMPLESALES"."Fact Revenue"."Revenue", "SAMPLESALES"."Fact Revenue"."Revenue (EUR)", "SAMPLESALES"."Fact Revenue"."Revenue (GBP)")
```

The formula reads the value `PREFERRED_CURRENCY`, a value set per user at login; whether the repository defines it is stated in the section "Values set per user or for the whole system". The formula lists three values, 'gc1', 'gc2' and 'gc3', with the numbers 0, 1 and 2, and then names three measures of this table in this order: "Revenue", "Revenue (EUR)", "Revenue (GBP)" [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Preferred Currency]. It does not name "Revenue (INR)". The packet records this field as not a measure. What the three values stand for, and what the field gives when the value is none of the three, is not recorded (open question Q2).

### Measures and fields report authors do not see

These belong to the business table but have no field in this subject area.

| Measure | How it is summarised | Can be typed into | In sources | Evidence |
|---|---|---|---|---|
| Revenue (EUR) | Sum: ` SUM("SAMPLESALES"."Fact Revenue"."Revenue (EUR)")` | no | 2 of 5: Aggregate Revenue, SAMP_REVENUE_F | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Revenue (EUR)] [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Revenue (EUR)#aggr0] |
| Revenue (GBP) | Sum: ` SUM("SAMPLESALES"."Fact Revenue"."Revenue (GBP)")` | no | 2 of 5: Aggregate Revenue, SAMP_REVENUE_F | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Revenue (GBP)] [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Revenue (GBP)#aggr0] |
| Revenue (INR) | Sum: ` SUM("SAMPLESALES"."Fact Revenue"."Revenue (INR)")` | no | 2 of 5: Aggregate Revenue, SAMP_REVENUE_F | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Revenue (INR)] [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Revenue (INR)#aggr0] |

| Field | In sources | Evidence |
|---|---|---|
| BILL_MTH_KEY | 1 of 5: Aggregate Revenue By Month | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.BILL_MTH_KEY] |
| BILL_YEAR_KEY | 1 of 5: Aggregate Revenue by Year | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.BILL_YEAR_KEY] |
| DISCNT_VALUE | 2 of 5: Aggregate Revenue By Month, Aggregate Revenue by Year | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.DISCNT_VALUE] |
| Sales Employee Key | 1 of 5: SAMP_REVENUE_F | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Sales Employee Key] |

### Points to note

- The table holds two fields whose names differ only in letter case and underscore. "Discnt Value" is a measure, summed, in the sources "Aggregate Revenue" and "SAMP_REVENUE_F" [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Discnt Value]; "DISCNT_VALUE" is recorded as not a measure, is in the sources "Aggregate Revenue By Month" and "Aggregate Revenue by Year", and is not shown to report authors [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.DISCNT_VALUE]. No summarising rule is recorded for "DISCNT_VALUE" (open question Q4).
- In the same way "Bill Mth Key" is shown and is in the sources "Aggregate Revenue" and "SAMP_REVENUE_F" [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Bill Mth Key], while "BILL_MTH_KEY" is not shown and is in the source "Aggregate Revenue By Month" only [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.BILL_MTH_KEY].
- "Revenue" and "Units" are in 4 of the 5 sources; every other measure is in 1 or 2 [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Revenue] [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Units]. What each source maps them from is under "Where the data comes from".
- "All Months Revenue" and "All Products Revenue" carry the same kind of rule as "Revenue", a sum of the field itself [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.All Months Revenue#aggr0] [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.All Products Revenue#aggr0]. What distinguishes them from "Revenue" is not recorded in the list of fields (open question Q5).

### Where the data comes from

Fact Revenue has five sources. All five are active. A priority group is recorded for one of them only, "Aggregate Revenue"; for the other four it is not recorded. Which source OBIEE reads for a given query is not recorded (open question Q7). No content filter is recorded for any of the five.

#### Source "Aggregate Revenue"

The source is active; its priority group is recorded as `1` [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue]. Its level of detail names the level "Month" of the drill path "Time Hierarchy" and the level "Product Detail" of the drill path "Products Hierararchy"; exactly as recorded [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue]:

```
 GROUPBYLEVEL("SAMPLESALES"."Time Hierarchy"."Month", "SAMPLESALES"."Products Hierararchy"."Product Detail")
```

It reads the database table SAMP_REVENUE_A in `xe.SAMPLESALES`; the table's type is recorded as `none` [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A]. Its object in the database, for the database type `DefaultMulDB`, is `SAMP_REVENUE_A` [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A.def.DefaultMulDB].

| Field | Database column | Type | Evidence |
|---|---|---|---|
| Bill Mth Key | SAMP_REVENUE_A.BILL_MTH_KEY | DOUBLE, precision 15 | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue#map0] [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A.BILL_MTH_KEY] |
| Discnt Value | SAMP_REVENUE_A.DISCNT_VALUE | DOUBLE, precision 7 | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue#map1] [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A.DISCNT_VALUE] |
| Prod Key | SAMP_REVENUE_A.PROD_KEY | DOUBLE, precision 15 | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue#map2] [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A.PROD_KEY] |
| Revenue | SAMP_REVENUE_A.REVENUE | DOUBLE, precision 8 | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue#map3] [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A.REVENUE] |
| Units | SAMP_REVENUE_A.UNITS | DOUBLE, precision 15 | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue#map4] [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A.UNITS] |
| All Products Revenue | SAMP_REVENUE_A.REVENUE | DOUBLE, precision 8 | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue#map5] [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A.REVENUE] |
| All Months Revenue | SAMP_REVENUE_A.REVENUE | DOUBLE, precision 8 | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue#map6] [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A.REVENUE] |
| Revenue (GBP) | SAMP_REVENUE_A.REVENUE | DOUBLE, precision 8 | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue#map7] [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A.REVENUE] |
| Revenue (EUR) | SAMP_REVENUE_A.REVENUE | DOUBLE, precision 8 | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue#map8] [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A.REVENUE] |
| Revenue (INR) | SAMP_REVENUE_A.REVENUE | DOUBLE, precision 8 | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue#map9] [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A.REVENUE] |

#### Source "Aggregate Revenue By Month"

The source is active; its priority group is not recorded [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue By Month]. Its level of detail names the level "Month" of the drill path "Time Hierarchy"; exactly as recorded [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue By Month]:

```
 GROUPBYLEVEL("SAMPLESALES"."Time Hierarchy"."Month")
```

It reads the database table SAMP_REVENUE_A_MTH in `xe.SAMPLESALES`; the table's type is recorded as `none` [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A_MTH]. No object name in the database is given for this table.

| Field | Database column | Type | Evidence |
|---|---|---|---|
| Revenue | SAMP_REVENUE_A_MTH.REVENUE | DOUBLE, precision 22 | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue By Month#map0] [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A_MTH.REVENUE] |
| Units | SAMP_REVENUE_A_MTH.UNITS | DOUBLE, precision 22 | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue By Month#map1] [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A_MTH.UNITS] |
| BILL_MTH_KEY | SAMP_REVENUE_A_MTH.BILL_MTH_KEY | DOUBLE, precision 22 | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue By Month#map2] [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A_MTH.BILL_MTH_KEY] |
| DISCNT_VALUE | SAMP_REVENUE_A_MTH.DISCNT_VALUE | DOUBLE, precision 22 | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue By Month#map3] [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A_MTH.DISCNT_VALUE] |

#### Source "Aggregate Revenue by Year"

The source is active; its priority group is not recorded [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue by Year]. Its level of detail names the level "Year" of the drill path "Time Hierarchy"; exactly as recorded [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue by Year]:

```
 GROUPBYLEVEL("SAMPLESALES"."Time Hierarchy"."Year")
```

It reads the database table SAMP_REVENUE_A_YR in `xe.SAMPLESALES`; the table's type is recorded as `none` [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A_YR]. Its object in the database, for the database type `DefaultMulDB`, is `SAMP_REVENUE_A_YR` [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A_YR.def.DefaultMulDB].

| Field | Database column | Type | Evidence |
|---|---|---|---|
| Revenue | SAMP_REVENUE_A_YR.REVENUE | DOUBLE, precision 22 | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue by Year#map0] [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A_YR.REVENUE] |
| Units | SAMP_REVENUE_A_YR.UNITS | DOUBLE, precision 22 | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue by Year#map1] [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A_YR.UNITS] |
| DISCNT_VALUE | SAMP_REVENUE_A_YR.DISCNT_VALUE | DOUBLE, precision 22 | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue by Year#map2] [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A_YR.DISCNT_VALUE] |
| BILL_YEAR_KEY | SAMP_REVENUE_A_YR.BILL_YEAR_KEY | DOUBLE | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue by Year#map3] [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A_YR.BILL_YEAR_KEY] |

#### Source "FactCrossJoin"

The source is active; its priority group is not recorded [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.FactCrossJoin]. No level of detail is recorded for it. It reads "FactCrossJoin" in `xe.SAMPLESALES`, whose type is recorded as `select`: it is not a database table but a query kept in the repository [kb:sampleapp::phys::xe.SAMPLESALES.FactCrossJoin]. The query, for the database type `DefaultMulDB`, is `select 1 Preserve from dual` [kb:sampleapp::phys::xe.SAMPLESALES.FactCrossJoin.def.DefaultMulDB]. Why the model holds this query is not recorded (open question Q3).

| Field | Database column | Type | Evidence |
|---|---|---|---|
| Preserve | FactCrossJoin.Preserve (a column of the query above) | DOUBLE | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.FactCrossJoin#map0] [kb:sampleapp::phys::xe.SAMPLESALES.FactCrossJoin.Preserve] |

#### Source "SAMP_REVENUE_F"

The source is active; its priority group is not recorded [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.SAMP_REVENUE_F]. Its level of detail names the level "Detail" of the drill path "Sales Rep Hierarchy", "Dim Time Detail" of "Time Hierarchy", "Office Detail" of "Office Hierarchy" and "Product Detail" of "Products Hierararchy"; exactly as recorded [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.SAMP_REVENUE_F]:

```
 GROUPBYLEVEL("SAMPLESALES"."Sales Rep Hierarchy"."Detail", "SAMPLESALES"."Time Hierarchy"."Dim Time Detail", "SAMPLESALES"."Office Hierarchy"."Office Detail", "SAMPLESALES"."Products Hierararchy"."Product Detail")
```

It reads the database table SAMP_REVENUE_F in `xe.SAMPLESALES`; the table's type is recorded as `none` [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F]. Its object in the database, for the database type `DefaultMulDB`, is `SAMP_REVENUE_F` [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F.def.DefaultMulDB].

| Field | Database column | Type | Evidence |
|---|---|---|---|
| Bill Day Dt | SAMP_REVENUE_F.BILL_DAY_DT | DATE | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.SAMP_REVENUE_F#map0] [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F.BILL_DAY_DT] |
| Bill Mth Key | SAMP_REVENUE_F.BILL_MTH_KEY | DOUBLE, precision 15 | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.SAMP_REVENUE_F#map1] [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F.BILL_MTH_KEY] |
| Units | SAMP_REVENUE_F.UNITS | DOUBLE, precision 15 | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.SAMP_REVENUE_F#map10] [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F.UNITS] |
| All Products Revenue | SAMP_REVENUE_F.REVENUE | DOUBLE, precision 7 | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.SAMP_REVENUE_F#map11] [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F.REVENUE] |
| All Months Revenue | SAMP_REVENUE_F.REVENUE | DOUBLE, precision 7 | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.SAMP_REVENUE_F#map12] [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F.REVENUE] |
| Sales Employee Key | SAMP_REVENUE_F.EMPLOYEE_KEY | INT | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.SAMP_REVENUE_F#map13] [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F.EMPLOYEE_KEY] |
| Revenue (GBP) | `"xe".""."SAMPLESALES"."SAMP_REVENUE_F"."REVENUE" * 0.62` (the database column REVENUE multiplied by 0.62) | DOUBLE, precision 7 | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.SAMP_REVENUE_F#map14] [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F.REVENUE] |
| Revenue (EUR) | `"xe".""."SAMPLESALES"."SAMP_REVENUE_F"."REVENUE" * 0.76` (the database column REVENUE multiplied by 0.76) | DOUBLE, precision 7 | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.SAMP_REVENUE_F#map15] [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F.REVENUE] |
| Revenue (INR) | `"xe".""."SAMPLESALES"."SAMP_REVENUE_F"."REVENUE" * 54.68` (the database column REVENUE multiplied by 54.68) | DOUBLE, precision 7 | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.SAMP_REVENUE_F#map16] [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F.REVENUE] |
| Discnt Value | SAMP_REVENUE_F.DISCNT_VALUE | DOUBLE, precision 6 | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.SAMP_REVENUE_F#map2] [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F.DISCNT_VALUE] |
| Office Key | SAMP_REVENUE_F.OFFICE_KEY | DOUBLE, precision 15 | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.SAMP_REVENUE_F#map3] [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F.OFFICE_KEY] |
| Order Day Dt | SAMP_REVENUE_F.ORDER_DAY_DT | DATETIME | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.SAMP_REVENUE_F#map4] [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F.ORDER_DAY_DT] |
| Order Number | SAMP_REVENUE_F.ORDER_NUMBER | DOUBLE, precision 15 | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.SAMP_REVENUE_F#map5] [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F.ORDER_NUMBER] |
| Order Status | SAMP_REVENUE_F.ORDER_STATUS | VARCHAR, precision 11 | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.SAMP_REVENUE_F#map6] [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F.ORDER_STATUS] |
| Order Type | SAMP_REVENUE_F.ORDER_TYPE | VARCHAR, precision 8 | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.SAMP_REVENUE_F#map7] [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F.ORDER_TYPE] |
| Prod Key | SAMP_REVENUE_F.PROD_KEY | DOUBLE, precision 15 | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.SAMP_REVENUE_F#map8] [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F.PROD_KEY] |
| Revenue | SAMP_REVENUE_F.REVENUE | DOUBLE, precision 7 | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.SAMP_REVENUE_F#map9] [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F.REVENUE] |

### Points to note about the sources

- "Revenue (GBP)", "Revenue (EUR)" and "Revenue (INR)" are mapped differently in the two sources that hold them. In "SAMP_REVENUE_F" each is the database column REVENUE multiplied by a fixed number: 0.62, 0.76 and 54.68 [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.SAMP_REVENUE_F#map14] [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.SAMP_REVENUE_F#map15] [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.SAMP_REVENUE_F#map16]. In "Aggregate Revenue" each is the column REVENUE of SAMP_REVENUE_A with no multiplication [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue#map7] [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue#map8] [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue#map9]. Where the three numbers come from is not recorded (open question Q6).
- "Revenue", "All Products Revenue" and "All Months Revenue" are mapped from the same database column in each of the two sources that hold all three: SAMP_REVENUE_A.REVENUE in "Aggregate Revenue" [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue#map3] [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue#map5] [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue#map6], and SAMP_REVENUE_F.REVENUE in "SAMP_REVENUE_F" [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.SAMP_REVENUE_F#map9] [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.SAMP_REVENUE_F#map11] [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.SAMP_REVENUE_F#map12]. The mappings therefore do not say what distinguishes the three (open question Q5).
- "Revenue" and "Units" can come from four database tables: SAMP_REVENUE_A, SAMP_REVENUE_A_MTH, SAMP_REVENUE_A_YR and SAMP_REVENUE_F [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue#map3] [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue By Month#map0] [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue by Year#map0] [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.SAMP_REVENUE_F#map9]. Whether the four tables hold the same totals is not recorded (open question Q7).
- The upper-case fields "BILL_MTH_KEY", "BILL_YEAR_KEY" and "DISCNT_VALUE" are mapped only in the sources "Aggregate Revenue By Month" and "Aggregate Revenue by Year" [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue By Month#map2] [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue by Year#map3] [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue By Month#map3] [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue by Year#map2]. Those two sources do not map "Discnt Value" or "Bill Mth Key" (open question Q4).
- The database column REVENUE is recorded with precision 8 in SAMP_REVENUE_A, 22 in SAMP_REVENUE_A_MTH and SAMP_REVENUE_A_YR, and 7 in SAMP_REVENUE_F [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A.REVENUE] [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A_MTH.REVENUE] [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A_YR.REVENUE] [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F.REVENUE].

## Fact Target

Fact Target is a business table of the model "SAMPLESALES"; the repository declares 1 source for it [kb:sampleapp::bmm::SAMPLESALES.Fact Target]. It has 5 fields, 2 of them measures, and report authors see all 5 (counted). Its one source is "SAMP_TARGETS_F" (counted).

### Measures

| Measure | How it is summarised | Can be typed into | In sources | Evidence |
|---|---|---|---|---|
| Revenue | Sum: ` SUM("SAMPLESALES"."Fact Target"."Revenue")` | yes | 1 of 1: SAMP_TARGETS_F | [kb:sampleapp::bmm::SAMPLESALES.Fact Target.Revenue] [kb:sampleapp::pres::Sample Sales.Fact Target.Revenue] [kb:sampleapp::bmm::SAMPLESALES.Fact Target.Revenue#aggr0] |
| Units | Sum: ` SUM("SAMPLESALES"."Fact Target"."Units")` | yes | 1 of 1: SAMP_TARGETS_F | [kb:sampleapp::bmm::SAMPLESALES.Fact Target.Units] [kb:sampleapp::pres::Sample Sales.Fact Target.Units] [kb:sampleapp::bmm::SAMPLESALES.Fact Target.Units#aggr0] |

The name report authors see is the same as the business table's name for both. Both measures are recorded as writeable: values can be typed into them. What the two measures hold in business terms, and what happens to a typed value, is not recorded (open question Q8).

### Fields

| Field | In sources | Evidence |
|---|---|---|
| Bill Mth Key | 1 of 1: SAMP_TARGETS_F | [kb:sampleapp::bmm::SAMPLESALES.Fact Target.Bill Mth Key] [kb:sampleapp::pres::Sample Sales.Fact Target.Bill Mth Key] |
| Office Key | 1 of 1: SAMP_TARGETS_F | [kb:sampleapp::bmm::SAMPLESALES.Fact Target.Office Key] [kb:sampleapp::pres::Sample Sales.Fact Target.Office Key] |
| Prod Key | 1 of 1: SAMP_TARGETS_F | [kb:sampleapp::bmm::SAMPLESALES.Fact Target.Prod Key] [kb:sampleapp::pres::Sample Sales.Fact Target.Prod Key] |

There are no measures or fields of this table that report authors do not see (counted).

### Where the data comes from

#### Source "SAMP_TARGETS_F"

The source is active; its priority group is not recorded [kb:sampleapp::bmm::SAMPLESALES.Fact Target.src.SAMP_TARGETS_F]. No content filter is recorded. Its level of detail names the level "Dim Time Detail" of the drill path "Time Hierarchy", "Office Detail" of "Office Hierarchy" and "Product Detail" of "Products Hierararchy"; exactly as recorded [kb:sampleapp::bmm::SAMPLESALES.Fact Target.src.SAMP_TARGETS_F]:

```
 GROUPBYLEVEL("SAMPLESALES"."Time Hierarchy"."Dim Time Detail", "SAMPLESALES"."Office Hierarchy"."Office Detail", "SAMPLESALES"."Products Hierararchy"."Product Detail")
```

It reads the database table SAMP_TARGETS_F in `xe.SAMPLESALES`; the table's type is recorded as `none` [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TARGETS_F]. Its object in the database, for the database type `DefaultMulDB`, is `SAMP_TARGETS_F` [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TARGETS_F.def.DefaultMulDB].

| Field | Database column | Type | Evidence |
|---|---|---|---|
| Bill Mth Key | SAMP_TARGETS_F.BILL_MTH_KEY | DOUBLE, precision 15 | [kb:sampleapp::bmm::SAMPLESALES.Fact Target.src.SAMP_TARGETS_F#map0] [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TARGETS_F.BILL_MTH_KEY] |
| Office Key | SAMP_TARGETS_F.OFFICE_KEY | DOUBLE, precision 15 | [kb:sampleapp::bmm::SAMPLESALES.Fact Target.src.SAMP_TARGETS_F#map1] [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TARGETS_F.OFFICE_KEY] |
| Prod Key | SAMP_TARGETS_F.PROD_KEY | DOUBLE, precision 15 | [kb:sampleapp::bmm::SAMPLESALES.Fact Target.src.SAMP_TARGETS_F#map2] [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TARGETS_F.PROD_KEY] |
| Revenue | SAMP_TARGETS_F.REVENUE | DOUBLE, precision 8 | [kb:sampleapp::bmm::SAMPLESALES.Fact Target.src.SAMP_TARGETS_F#map3] [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TARGETS_F.REVENUE] |
| Units | SAMP_TARGETS_F.UNITS | DOUBLE, precision 15 | [kb:sampleapp::bmm::SAMPLESALES.Fact Target.src.SAMP_TARGETS_F#map4] [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TARGETS_F.UNITS] |

### Points to note

- Both measures can be typed into [kb:sampleapp::bmm::SAMPLESALES.Fact Target.Revenue] [kb:sampleapp::bmm::SAMPLESALES.Fact Target.Units] (open question Q8).
- The level of detail names the level "Dim Time Detail", while the only time field the source maps is "Bill Mth Key", from the column BILL_MTH_KEY [kb:sampleapp::bmm::SAMPLESALES.Fact Target.src.SAMP_TARGETS_F] [kb:sampleapp::bmm::SAMPLESALES.Fact Target.src.SAMP_TARGETS_F#map0]. At what level of time the table SAMP_TARGETS_F actually holds its rows is not recorded (open question Q8).

## Dim Offices

Dim Offices is a business table of the model "SAMPLESALES"; the repository declares 1 source for it [kb:sampleapp::bmm::SAMPLESALES.Dim Offices]. It has 10 fields, none of them a measure; 1 of the 10 is not shown to report authors in this subject area (counted). Its one source is "SAMP_OFFICES_D" (counted).

### Measures

The table has no measures (counted).

### Fields

| Field | In sources | Evidence |
|---|---|---|
| Area Name | 1 of 1: SAMP_OFFICES_D | [kb:sampleapp::bmm::SAMPLESALES.Dim Offices.Area Name] [kb:sampleapp::pres::Sample Sales.Dim Offices.Area Name] |
| Company | 1 of 1: SAMP_OFFICES_D | [kb:sampleapp::bmm::SAMPLESALES.Dim Offices.Company] [kb:sampleapp::pres::Sample Sales.Dim Offices.Company] |
| Company Key | 1 of 1: SAMP_OFFICES_D | [kb:sampleapp::bmm::SAMPLESALES.Dim Offices.Company Key] [kb:sampleapp::pres::Sample Sales.Dim Offices.Company Key] |
| Department | 1 of 1: SAMP_OFFICES_D | [kb:sampleapp::bmm::SAMPLESALES.Dim Offices.Department] [kb:sampleapp::pres::Sample Sales.Dim Offices.Department] |
| Dept Key | 1 of 1: SAMP_OFFICES_D | [kb:sampleapp::bmm::SAMPLESALES.Dim Offices.Dept Key] [kb:sampleapp::pres::Sample Sales.Dim Offices.Dept Key] |
| Office Dsc | 1 of 1: SAMP_OFFICES_D | [kb:sampleapp::bmm::SAMPLESALES.Dim Offices.Office Dsc] [kb:sampleapp::pres::Sample Sales.Dim Offices.Office Dsc] |
| Office Key | 1 of 1: SAMP_OFFICES_D | [kb:sampleapp::bmm::SAMPLESALES.Dim Offices.Office Key] [kb:sampleapp::pres::Sample Sales.Dim Offices.Office Key] |
| Org Key | 1 of 1: SAMP_OFFICES_D | [kb:sampleapp::bmm::SAMPLESALES.Dim Offices.Org Key] [kb:sampleapp::pres::Sample Sales.Dim Offices.Org Key] |
| Organization | 1 of 1: SAMP_OFFICES_D | [kb:sampleapp::bmm::SAMPLESALES.Dim Offices.Organization] [kb:sampleapp::pres::Sample Sales.Dim Offices.Organization] |

The name report authors see is the same as the business table's name for all nine.

### Measures and fields report authors do not see

| Field | In sources | Evidence |
|---|---|---|
| Sequence | 1 of 1: SAMP_OFFICES_D | [kb:sampleapp::bmm::SAMPLESALES.Dim Offices.Sequence] |

### Where the data comes from

#### Source "SAMP_OFFICES_D"

The source is active; its priority group is not recorded [kb:sampleapp::bmm::SAMPLESALES.Dim Offices.src.SAMP_OFFICES_D]. No content filter is recorded. Its level of detail names the level "Office Detail" of the drill path "Office Hierarchy"; exactly as recorded: ` GROUPBYLEVEL("SAMPLESALES"."Office Hierarchy"."Office Detail")` [kb:sampleapp::bmm::SAMPLESALES.Dim Offices.src.SAMP_OFFICES_D].

It reads the database table SAMP_OFFICES_D in `xe.SAMPLESALES`; the table's type is recorded as `none` [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_OFFICES_D]. No object name in the database is given for this table.

| Field | Database column | Type | Evidence |
|---|---|---|---|
| Company | SAMP_OFFICES_D.COMPANY | VARCHAR, precision 14 | [kb:sampleapp::bmm::SAMPLESALES.Dim Offices.src.SAMP_OFFICES_D#map0] [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_OFFICES_D.COMPANY] |
| Company Key | SAMP_OFFICES_D.COMPANY_KEY | DOUBLE, precision 15 | [kb:sampleapp::bmm::SAMPLESALES.Dim Offices.src.SAMP_OFFICES_D#map1] [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_OFFICES_D.COMPANY_KEY] |
| Department | SAMP_OFFICES_D.DEPARTMENT | VARCHAR, precision 25 | [kb:sampleapp::bmm::SAMPLESALES.Dim Offices.src.SAMP_OFFICES_D#map2] [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_OFFICES_D.DEPARTMENT] |
| Dept Key | SAMP_OFFICES_D.DEPT_KEY | DOUBLE, precision 15 | [kb:sampleapp::bmm::SAMPLESALES.Dim Offices.src.SAMP_OFFICES_D#map3] [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_OFFICES_D.DEPT_KEY] |
| Office Dsc | SAMP_OFFICES_D.OFFICE_DSC | VARCHAR, precision 17 | [kb:sampleapp::bmm::SAMPLESALES.Dim Offices.src.SAMP_OFFICES_D#map4] [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_OFFICES_D.OFFICE_DSC] |
| Office Key | SAMP_OFFICES_D.OFFICE_KEY | DOUBLE, precision 15 | [kb:sampleapp::bmm::SAMPLESALES.Dim Offices.src.SAMP_OFFICES_D#map5] [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_OFFICES_D.OFFICE_KEY] |
| Org Key | SAMP_OFFICES_D.ORG_KEY | DOUBLE, precision 15 | [kb:sampleapp::bmm::SAMPLESALES.Dim Offices.src.SAMP_OFFICES_D#map6] [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_OFFICES_D.ORG_KEY] |
| Organization | SAMP_OFFICES_D.ORGANIZATION | VARCHAR, precision 18 | [kb:sampleapp::bmm::SAMPLESALES.Dim Offices.src.SAMP_OFFICES_D#map7] [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_OFFICES_D.ORGANIZATION] |
| Sequence | SAMP_OFFICES_D.SEQUENCE | DOUBLE, precision 15 | [kb:sampleapp::bmm::SAMPLESALES.Dim Offices.src.SAMP_OFFICES_D#map8] [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_OFFICES_D.SEQUENCE] |
| Area Name | SAMP_OFFICES_D.AREA_NAME | VARCHAR, precision 25 | [kb:sampleapp::bmm::SAMPLESALES.Dim Offices.src.SAMP_OFFICES_D#map9] [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_OFFICES_D.AREA_NAME] |

### Points to note

- Every field of the table is one database column of SAMP_OFFICES_D, with no calculation [kb:sampleapp::bmm::SAMPLESALES.Dim Offices.src.SAMP_OFFICES_D].
- "Sequence" is mapped but not shown to report authors [kb:sampleapp::bmm::SAMPLESALES.Dim Offices.Sequence].

## Dim Products

Dim Products is a business table of the model "SAMPLESALES"; the repository declares 1 source for it [kb:sampleapp::bmm::SAMPLESALES.Dim Products]. It has 12 fields, none of them a measure; 3 of the 12 are not shown to report authors in this subject area (counted). Its one source is "SAMP_PRODUCTS_D" (counted).

### Measures

The table has no measures (counted).

### Fields

| Field | In sources | Evidence |
|---|---|---|
| Brand | 1 of 1: SAMP_PRODUCTS_D | [kb:sampleapp::bmm::SAMPLESALES.Dim Products.Brand] [kb:sampleapp::pres::Sample Sales.Dim Products.Brand] |
| Brand Key | 1 of 1: SAMP_PRODUCTS_D | [kb:sampleapp::bmm::SAMPLESALES.Dim Products.Brand Key] [kb:sampleapp::pres::Sample Sales.Dim Products.Brand Key] |
| Lob | 1 of 1: SAMP_PRODUCTS_D | [kb:sampleapp::bmm::SAMPLESALES.Dim Products.Lob] [kb:sampleapp::pres::Sample Sales.Dim Products.Lob] |
| Lob Key | 1 of 1: SAMP_PRODUCTS_D | [kb:sampleapp::bmm::SAMPLESALES.Dim Products.Lob Key] [kb:sampleapp::pres::Sample Sales.Dim Products.Lob Key] |
| Prod Dsc | 1 of 1: SAMP_PRODUCTS_D | [kb:sampleapp::bmm::SAMPLESALES.Dim Products.Prod Dsc] [kb:sampleapp::pres::Sample Sales.Dim Products.Prod Dsc] |
| Prod Dsc (Translated) | 0 of 1: it is calculated, see below | [kb:sampleapp::bmm::SAMPLESALES.Dim Products.Prod Dsc (Translated)] [kb:sampleapp::pres::Sample Sales.Dim Products.Prod Dsc (Translated)] |
| Prod Key | 1 of 1: SAMP_PRODUCTS_D | [kb:sampleapp::bmm::SAMPLESALES.Dim Products.Prod Key] [kb:sampleapp::pres::Sample Sales.Dim Products.Prod Key] |
| Type | 1 of 1: SAMP_PRODUCTS_D | [kb:sampleapp::bmm::SAMPLESALES.Dim Products.Type] [kb:sampleapp::pres::Sample Sales.Dim Products.Type] |
| Type Key | 1 of 1: SAMP_PRODUCTS_D | [kb:sampleapp::bmm::SAMPLESALES.Dim Products.Type Key] [kb:sampleapp::pres::Sample Sales.Dim Products.Type Key] |

The name report authors see is the same as the business table's name for all nine.

"Prod Dsc (Translated)" is calculated from other fields of the business model. Its formula, exactly as recorded [kb:sampleapp::bmm::SAMPLESALES.Dim Products.Prod Dsc (Translated)]:

```
Lookup(SPARSE   "SAMPLESALES"."Dim Products (Translated)"."PROD_DESC" ,  "SAMPLESALES"."Dim Products"."Prod Dsc"  , "SAMPLESALES"."Dim Products"."Prod Key", VALUEOF(NQ_SESSION.WEBLANGUAGE) ) 
```

The formula names, in this order: the field "PROD_DESC" of the business table "Dim Products (Translated)", the fields "Prod Dsc" and "Prod Key" of this table, and the value `WEBLANGUAGE`, a value set per user at login [kb:sampleapp::bmm::SAMPLESALES.Dim Products.Prod Dsc (Translated)]. Whether the repository defines `WEBLANGUAGE` is stated in the section "Values set per user or for the whole system"; the other table is described in the section "Dim Products (Translated)". Which values `WEBLANGUAGE` takes and what a report author gets from this field for each is not recorded (open question Q9).

### Measures and fields report authors do not see

| Field | In sources | Evidence |
|---|---|---|
| Attribute 1 | 1 of 1: SAMP_PRODUCTS_D | [kb:sampleapp::bmm::SAMPLESALES.Dim Products.Attribute 1] |
| Attribute 2 | 1 of 1: SAMP_PRODUCTS_D | [kb:sampleapp::bmm::SAMPLESALES.Dim Products.Attribute 2] |
| Sequence | 1 of 1: SAMP_PRODUCTS_D | [kb:sampleapp::bmm::SAMPLESALES.Dim Products.Sequence] |

### Where the data comes from

#### Source "SAMP_PRODUCTS_D"

The source is active; its priority group is not recorded [kb:sampleapp::bmm::SAMPLESALES.Dim Products.src.SAMP_PRODUCTS_D]. No content filter is recorded. Its level of detail names the level "Product Detail" of the drill path "Products Hierararchy"; exactly as recorded: ` GROUPBYLEVEL("SAMPLESALES"."Products Hierararchy"."Product Detail")` [kb:sampleapp::bmm::SAMPLESALES.Dim Products.src.SAMP_PRODUCTS_D].

It reads the database table SAMP_PRODUCTS_D in `xe.SAMPLESALES`; the table's type is recorded as `none` [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_PRODUCTS_D]. No object name in the database is given for this table.

| Field | Database column | Type | Evidence |
|---|---|---|---|
| Attribute 1 | SAMP_PRODUCTS_D.ATTRIBUTE_1 | VARCHAR, precision 7 | [kb:sampleapp::bmm::SAMPLESALES.Dim Products.src.SAMP_PRODUCTS_D#map0] [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_PRODUCTS_D.ATTRIBUTE_1] |
| Attribute 2 | SAMP_PRODUCTS_D.ATTRIBUTE_2 | VARCHAR, precision 6 | [kb:sampleapp::bmm::SAMPLESALES.Dim Products.src.SAMP_PRODUCTS_D#map1] [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_PRODUCTS_D.ATTRIBUTE_2] |
| Type Key | SAMP_PRODUCTS_D.TYPE_KEY | DOUBLE, precision 15 | [kb:sampleapp::bmm::SAMPLESALES.Dim Products.src.SAMP_PRODUCTS_D#map10] [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_PRODUCTS_D.TYPE_KEY] |
| Brand | SAMP_PRODUCTS_D.BRAND | VARCHAR, precision 8 | [kb:sampleapp::bmm::SAMPLESALES.Dim Products.src.SAMP_PRODUCTS_D#map2] [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_PRODUCTS_D.BRAND] |
| Brand Key | SAMP_PRODUCTS_D.BRAND_KEY | DOUBLE, precision 15 | [kb:sampleapp::bmm::SAMPLESALES.Dim Products.src.SAMP_PRODUCTS_D#map3] [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_PRODUCTS_D.BRAND_KEY] |
| Lob | SAMP_PRODUCTS_D.LOB | VARCHAR, precision 13 | [kb:sampleapp::bmm::SAMPLESALES.Dim Products.src.SAMP_PRODUCTS_D#map4] [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_PRODUCTS_D.LOB] |
| Lob Key | SAMP_PRODUCTS_D.LOB_KEY | DOUBLE, precision 15 | [kb:sampleapp::bmm::SAMPLESALES.Dim Products.src.SAMP_PRODUCTS_D#map5] [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_PRODUCTS_D.LOB_KEY] |
| Prod Dsc | SAMP_PRODUCTS_D.PROD_DSC | VARCHAR, precision 26 | [kb:sampleapp::bmm::SAMPLESALES.Dim Products.src.SAMP_PRODUCTS_D#map6] [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_PRODUCTS_D.PROD_DSC] |
| Prod Key | SAMP_PRODUCTS_D.PROD_KEY | DOUBLE, precision 15 | [kb:sampleapp::bmm::SAMPLESALES.Dim Products.src.SAMP_PRODUCTS_D#map7] [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_PRODUCTS_D.PROD_KEY] |
| Sequence | SAMP_PRODUCTS_D.SEQUENCE | DOUBLE, precision 15 | [kb:sampleapp::bmm::SAMPLESALES.Dim Products.src.SAMP_PRODUCTS_D#map8] [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_PRODUCTS_D.SEQUENCE] |
| Type | SAMP_PRODUCTS_D.TYPE | VARCHAR, precision 12 | [kb:sampleapp::bmm::SAMPLESALES.Dim Products.src.SAMP_PRODUCTS_D#map9] [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_PRODUCTS_D.TYPE] |

### Points to note

- "Prod Dsc (Translated)" is the only field of the table that is in no source: it is a formula that names another business table and a value set per user at login [kb:sampleapp::bmm::SAMPLESALES.Dim Products.Prod Dsc (Translated)] (open question Q9).
- "Attribute 1", "Attribute 2" and "Sequence" are mapped but not shown to report authors [kb:sampleapp::bmm::SAMPLESALES.Dim Products.Attribute 1] [kb:sampleapp::bmm::SAMPLESALES.Dim Products.Attribute 2] [kb:sampleapp::bmm::SAMPLESALES.Dim Products.Sequence].

## Dim Products (Translated)

Dim Products (Translated) is a business table of the model "SAMPLESALES"; the repository declares 1 source for it [kb:sampleapp::bmm::SAMPLESALES.Dim Products (Translated)]. It has 3 fields, none of them a measure, and none of the 3 is shown to report authors in this subject area (counted). It is described because it is part of the model behind the subject area. Its one source is "SAMP_PRODUCTS_D_TRANSLATION" (counted).

### Measures

The table has no measures (counted).

### Fields

Report authors see no field of this table (counted).

### Measures and fields report authors do not see

| Field | In sources | Evidence |
|---|---|---|
| LANG_ID | 1 of 1: SAMP_PRODUCTS_D_TRANSLATION | [kb:sampleapp::bmm::SAMPLESALES.Dim Products (Translated).LANG_ID] |
| PROD_DESC | 1 of 1: SAMP_PRODUCTS_D_TRANSLATION | [kb:sampleapp::bmm::SAMPLESALES.Dim Products (Translated).PROD_DESC] |
| PROD_KEY | 1 of 1: SAMP_PRODUCTS_D_TRANSLATION | [kb:sampleapp::bmm::SAMPLESALES.Dim Products (Translated).PROD_KEY] |

### Where the data comes from

#### Source "SAMP_PRODUCTS_D_TRANSLATION"

The source is active; its priority group is not recorded [kb:sampleapp::bmm::SAMPLESALES.Dim Products (Translated).src.SAMP_PRODUCTS_D_TRANSLATION]. No level of detail and no content filter are recorded for it.

It reads the database table SAMP_PRODUCTS_D_TRANSLATION in `xe.SAMPLESALES`; the table's type is recorded as `none` [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_PRODUCTS_D_TRANSLATION]. Its object in the database, for the database type `DefaultMulDB`, is `SAMP_PRODUCTS_D_TRANSLATION` [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_PRODUCTS_D_TRANSLATION.def.DefaultMulDB].

| Field | Database column | Type | Evidence |
|---|---|---|---|
| LANG_ID | SAMP_PRODUCTS_D_TRANSLATION.LANG_ID | VARCHAR, precision 2 | [kb:sampleapp::bmm::SAMPLESALES.Dim Products (Translated).src.SAMP_PRODUCTS_D_TRANSLATION#map0] [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_PRODUCTS_D_TRANSLATION.LANG_ID] |
| PROD_DESC | SAMP_PRODUCTS_D_TRANSLATION.PROD_DESC | VARCHAR, precision 50 | [kb:sampleapp::bmm::SAMPLESALES.Dim Products (Translated).src.SAMP_PRODUCTS_D_TRANSLATION#map1] [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_PRODUCTS_D_TRANSLATION.PROD_DESC] |
| PROD_KEY | SAMP_PRODUCTS_D_TRANSLATION.PROD_KEY | DOUBLE, precision 15 | [kb:sampleapp::bmm::SAMPLESALES.Dim Products (Translated).src.SAMP_PRODUCTS_D_TRANSLATION#map2] [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_PRODUCTS_D_TRANSLATION.PROD_KEY] |

### Points to note

- No field of this table is shown to report authors (counted). Where the subject area uses the table is in the section "Dim Products", at the field "Prod Dsc (Translated)".
- This is a source with no recorded level of detail [kb:sampleapp::bmm::SAMPLESALES.Dim Products (Translated).src.SAMP_PRODUCTS_D_TRANSLATION].
- Which values the column LANG_ID holds is not recorded [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_PRODUCTS_D_TRANSLATION.LANG_ID] (open question Q9, which asks for the languages in use).

## Dim Sales Rep

Dim Sales Rep is a business table of the model "SAMPLESALES"; the repository declares 1 source for it [kb:sampleapp::bmm::SAMPLESALES.Dim Sales Rep]. It has 5 fields, none of them a measure, and report authors see all 5 (counted). Its one source is "LTS Employee" (counted).

### Measures

The table has no measures (counted).

### Fields

| Field | In sources | Evidence |
|---|---|---|
| Distance | 1 of 1: LTS Employee | [kb:sampleapp::bmm::SAMPLESALES.Dim Sales Rep.Distance] [kb:sampleapp::pres::Sample Sales.Dim Sales Rep.Distance] |
| Employee ID | 1 of 1: LTS Employee | [kb:sampleapp::bmm::SAMPLESALES.Dim Sales Rep.Employee ID] [kb:sampleapp::pres::Sample Sales.Dim Sales Rep.Employee ID] |
| Employee Name | 1 of 1: LTS Employee | [kb:sampleapp::bmm::SAMPLESALES.Dim Sales Rep.Employee Name] [kb:sampleapp::pres::Sample Sales.Dim Sales Rep.Employee Name] |
| Manager ID | 1 of 1: LTS Employee | [kb:sampleapp::bmm::SAMPLESALES.Dim Sales Rep.Manager ID] [kb:sampleapp::pres::Sample Sales.Dim Sales Rep.Manager ID] |
| Office ID | 1 of 1: LTS Employee | [kb:sampleapp::bmm::SAMPLESALES.Dim Sales Rep.Office ID] [kb:sampleapp::pres::Sample Sales.Dim Sales Rep.Office ID] |

The name report authors see is the same as the business table's name for all five. There are no measures or fields of this table that report authors do not see (counted).

### Where the data comes from

#### Source "LTS Employee"

The source is active; its priority group is not recorded [kb:sampleapp::bmm::SAMPLESALES.Dim Sales Rep.src.LTS Employee]. No content filter is recorded. Its level of detail names the level "Detail" of the drill path "Sales Rep Hierarchy"; exactly as recorded: ` GROUPBYLEVEL("SAMPLESALES"."Sales Rep Hierarchy"."Detail")` [kb:sampleapp::bmm::SAMPLESALES.Dim Sales Rep.src.LTS Employee].

The source reads two database tables:

- EMPLOYEE in `xe.SAMPLESALES`; type recorded as `none` [kb:sampleapp::phys::xe.SAMPLESALES.EMPLOYEE]. No object name in the database is given for this table.
- EMP_PARENT_CHILD in `xe.SAMPLESALES`; type recorded as `none` [kb:sampleapp::phys::xe.SAMPLESALES.EMP_PARENT_CHILD]. Its object in the database, for the database type `DefaultMulDB`, is `EMP_PARENT_CHILD` [kb:sampleapp::phys::xe.SAMPLESALES.EMP_PARENT_CHILD.def.DefaultMulDB].

| Field | Database column | Type | Evidence |
|---|---|---|---|
| Employee ID | EMPLOYEE.EMP_ID | DOUBLE, precision 15 | [kb:sampleapp::bmm::SAMPLESALES.Dim Sales Rep.src.LTS Employee#map0] [kb:sampleapp::phys::xe.SAMPLESALES.EMPLOYEE.EMP_ID] |
| Employee Name | EMPLOYEE.EMP_NAME | VARCHAR, precision 40 | [kb:sampleapp::bmm::SAMPLESALES.Dim Sales Rep.src.LTS Employee#map1] [kb:sampleapp::phys::xe.SAMPLESALES.EMPLOYEE.EMP_NAME] |
| Manager ID | EMPLOYEE.MANAGER_ID | DOUBLE, precision 15 | [kb:sampleapp::bmm::SAMPLESALES.Dim Sales Rep.src.LTS Employee#map2] [kb:sampleapp::phys::xe.SAMPLESALES.EMPLOYEE.MANAGER_ID] |
| Office ID | EMPLOYEE.OFFICE_ID | DOUBLE, precision 15 | [kb:sampleapp::bmm::SAMPLESALES.Dim Sales Rep.src.LTS Employee#map3] [kb:sampleapp::phys::xe.SAMPLESALES.EMPLOYEE.OFFICE_ID] |
| Distance | EMP_PARENT_CHILD.DISTANCE | INT | [kb:sampleapp::bmm::SAMPLESALES.Dim Sales Rep.src.LTS Employee#map4] [kb:sampleapp::phys::xe.SAMPLESALES.EMP_PARENT_CHILD.DISTANCE] |

### Points to note

- This one source reads two database tables: four fields come from EMPLOYEE and "Distance" comes from EMP_PARENT_CHILD [kb:sampleapp::bmm::SAMPLESALES.Dim Sales Rep.src.LTS Employee#map0] [kb:sampleapp::bmm::SAMPLESALES.Dim Sales Rep.src.LTS Employee#map4]. How the two tables are joined is in the section "How the tables relate". What "Distance" holds, and how many rows the source gives per employee, is not recorded (open question Q10).

## Dim Time

Dim Time is a business table of the model "SAMPLESALES"; the repository declares 2 sources for it [kb:sampleapp::bmm::SAMPLESALES.Dim Time]. It has 6 fields, none of them a measure, and report authors see all 6 (counted). Its two sources are "LTS Time Day" and "LTS Time Month" (counted).

### Measures

The table has no measures (counted).

### Fields

| Field | In sources | Evidence |
|---|---|---|
| Calendar Date | 1 of 2: LTS Time Day | [kb:sampleapp::bmm::SAMPLESALES.Dim Time.Calendar Date] [kb:sampleapp::pres::Sample Sales.Dim Time.Calendar Date] |
| Per Name Half | 2 of 2: LTS Time Day, LTS Time Month | [kb:sampleapp::bmm::SAMPLESALES.Dim Time.Per Name Half] [kb:sampleapp::pres::Sample Sales.Dim Time.Per Name Half] |
| Per Name Month | 2 of 2: LTS Time Day, LTS Time Month | [kb:sampleapp::bmm::SAMPLESALES.Dim Time.Per Name Month] [kb:sampleapp::pres::Sample Sales.Dim Time.Per Name Month] |
| Per Name Qtr | 2 of 2: LTS Time Day, LTS Time Month | [kb:sampleapp::bmm::SAMPLESALES.Dim Time.Per Name Qtr] [kb:sampleapp::pres::Sample Sales.Dim Time.Per Name Qtr] |
| Per Name Week | 1 of 2: LTS Time Day | [kb:sampleapp::bmm::SAMPLESALES.Dim Time.Per Name Week] [kb:sampleapp::pres::Sample Sales.Dim Time.Per Name Week] |
| Per Name Year | 2 of 2: LTS Time Day, LTS Time Month | [kb:sampleapp::bmm::SAMPLESALES.Dim Time.Per Name Year] [kb:sampleapp::pres::Sample Sales.Dim Time.Per Name Year] |

The name shown to users is not a fixed text for any of the six fields. Each is recorded as a formula that reads a value set per user at login:

| Field | Name shown to users, as recorded | Evidence |
|---|---|---|
| Calendar Date | `VALUEOF(NQ_SESSION.CN_Sample_Sales_Dim_Time_Calendar_Date)` | [kb:sampleapp::pres::Sample Sales.Dim Time.Calendar Date] |
| Per Name Half | `VALUEOF(NQ_SESSION.CN_Sample_Sales_Dim_Time_Per_Name_Half)` | [kb:sampleapp::pres::Sample Sales.Dim Time.Per Name Half] |
| Per Name Month | `VALUEOF(NQ_SESSION.CN_Sample_Sales_Dim_Time_Per_Name_Month)` | [kb:sampleapp::pres::Sample Sales.Dim Time.Per Name Month] |
| Per Name Qtr | `VALUEOF(NQ_SESSION.CN_Sample_Sales_Dim_Time_Per_Name_Qtr)` | [kb:sampleapp::pres::Sample Sales.Dim Time.Per Name Qtr] |
| Per Name Week | `VALUEOF(NQ_SESSION.CN_Sample_Sales_Dim_Time_Per_Name_Week)` | [kb:sampleapp::pres::Sample Sales.Dim Time.Per Name Week] |
| Per Name Year | `VALUEOF(NQ_SESSION.CN_Sample_Sales_Dim_Time_Per_Name_Year)` | [kb:sampleapp::pres::Sample Sales.Dim Time.Per Name Year] |

Whether the repository defines these six values is stated in the section "Values set per user or for the whole system". What names report authors actually see is not recorded (open question Q11). There are no measures or fields of this table that report authors do not see (counted).

### Where the data comes from

Both sources are active and neither has a recorded priority group. Which of the two OBIEE reads for a given query is not recorded (open question Q12).

#### Source "LTS Time Day"

The source is active; its priority group is not recorded [kb:sampleapp::bmm::SAMPLESALES.Dim Time.src.LTS Time Day]. No content filter is recorded. Its level of detail names the level "Dim Time Detail" of the drill path "Time Hierarchy"; exactly as recorded: ` GROUPBYLEVEL("SAMPLESALES"."Time Hierarchy"."Dim Time Detail")` [kb:sampleapp::bmm::SAMPLESALES.Dim Time.src.LTS Time Day].

It reads the database table SAMP_TIME_DAY_D in `xe.SAMPLESALES`; the table's type is recorded as `none` [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_DAY_D]. Its object in the database, for the database type `DefaultMulDB`, is `SAMP_TIME_DAY_D` [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_DAY_D.def.DefaultMulDB].

| Field | Database column | Type | Evidence |
|---|---|---|---|
| Per Name Year | SAMP_TIME_DAY_D.PER_NAME_YEAR | VARCHAR, precision 4 | [kb:sampleapp::bmm::SAMPLESALES.Dim Time.src.LTS Time Day#map0] [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_DAY_D.PER_NAME_YEAR] |
| Per Name Week | SAMP_TIME_DAY_D.PER_NAME_WEEK | VARCHAR, precision 12 | [kb:sampleapp::bmm::SAMPLESALES.Dim Time.src.LTS Time Day#map1] [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_DAY_D.PER_NAME_WEEK] |
| Per Name Half | SAMP_TIME_DAY_D.PER_NAME_HALF | VARCHAR, precision 8 | [kb:sampleapp::bmm::SAMPLESALES.Dim Time.src.LTS Time Day#map2] [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_DAY_D.PER_NAME_HALF] |
| Per Name Month | SAMP_TIME_DAY_D.PER_NAME_MONTH | VARCHAR, precision 9 | [kb:sampleapp::bmm::SAMPLESALES.Dim Time.src.LTS Time Day#map3] [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_DAY_D.PER_NAME_MONTH] |
| Per Name Qtr | SAMP_TIME_DAY_D.PER_NAME_QTR | VARCHAR, precision 7 | [kb:sampleapp::bmm::SAMPLESALES.Dim Time.src.LTS Time Day#map4] [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_DAY_D.PER_NAME_QTR] |
| Calendar Date | SAMP_TIME_DAY_D.CALENDAR_DATE | DATE | [kb:sampleapp::bmm::SAMPLESALES.Dim Time.src.LTS Time Day#map5] [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_DAY_D.CALENDAR_DATE] |

#### Source "LTS Time Month"

The source is active; its priority group is not recorded [kb:sampleapp::bmm::SAMPLESALES.Dim Time.src.LTS Time Month]. No content filter is recorded. Its level of detail names the level "Month" of the drill path "Time Hierarchy"; exactly as recorded: ` GROUPBYLEVEL("SAMPLESALES"."Time Hierarchy"."Month")` [kb:sampleapp::bmm::SAMPLESALES.Dim Time.src.LTS Time Month].

It reads the database table SAMP_TIME_MTH_D in `xe.SAMPLESALES`; the table's type is recorded as `none` [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D]. No object name in the database is given for this table.

| Field | Database column | Type | Evidence |
|---|---|---|---|
| Per Name Year | SAMP_TIME_MTH_D.PER_NAME_YEAR | VARCHAR, precision 4 | [kb:sampleapp::bmm::SAMPLESALES.Dim Time.src.LTS Time Month#map0] [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.PER_NAME_YEAR] |
| Per Name Half | SAMP_TIME_MTH_D.PER_NAME_HALF | VARCHAR, precision 8 | [kb:sampleapp::bmm::SAMPLESALES.Dim Time.src.LTS Time Month#map1] [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.PER_NAME_HALF] |
| Per Name Month | SAMP_TIME_MTH_D.PER_NAME_MONTH | VARCHAR, precision 9 | [kb:sampleapp::bmm::SAMPLESALES.Dim Time.src.LTS Time Month#map2] [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.PER_NAME_MONTH] |
| Per Name Qtr | SAMP_TIME_MTH_D.PER_NAME_QTR | VARCHAR, precision 7 | [kb:sampleapp::bmm::SAMPLESALES.Dim Time.src.LTS Time Month#map3] [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.PER_NAME_QTR] |

### Points to note

- "Calendar Date" and "Per Name Week" are in the source "LTS Time Day" only; the other four fields are in both sources, read from SAMP_TIME_DAY_D in one and from SAMP_TIME_MTH_D in the other [kb:sampleapp::bmm::SAMPLESALES.Dim Time.src.LTS Time Day] [kb:sampleapp::bmm::SAMPLESALES.Dim Time.src.LTS Time Month] (open question Q12).
- No key field is among the six fields of this table: the sources map no column other than the six shown above [kb:sampleapp::bmm::SAMPLESALES.Dim Time.src.LTS Time Day#map5] [kb:sampleapp::bmm::SAMPLESALES.Dim Time.src.LTS Time Month#map2].
- The displayed name of every field of this table is a formula, not a text [kb:sampleapp::pres::Sample Sales.Dim Time.Calendar Date] (open question Q11).

## How the tables relate

### Joins between business tables

The business model holds 7 joins between the business tables of this subject area. The numbers in brackets are how many rows on each side, exactly as recorded.

| From | To | Rows on each side, as recorded | Join type | Evidence |
|---|---|---|---|---|
| Dim Time | Fact Target | "Dim Time" (0..1), "Fact Target" (0..n) | Inner | [kb:sampleapp::bmm::joins.Relationship_2004:11061353928600] |
| Dim Time | Fact Revenue | "Dim Time" (0..1), "Fact Revenue" (0..n) | Inner | [kb:sampleapp::bmm::joins.Relationship_2004:11091353928666] |
| Dim Sales Rep | Fact Revenue | "Dim Sales Rep" (1..1), "Fact Revenue" (0..n) | Inner | [kb:sampleapp::bmm::joins.Relationship_2004:17511354089145] |
| Fact Revenue | Dim Offices | "Fact Revenue" (n..n), "Dim Offices" (0..1) | Inner | [kb:sampleapp::bmm::joins.Relationship_2004:2111353851711] |
| Fact Revenue | Dim Products | "Fact Revenue" (n..n), "Dim Products" (0..1) | Inner | [kb:sampleapp::bmm::joins.Relationship_2004:2141353851711] |
| Fact Target | Dim Offices | "Fact Target" (n..n), "Dim Offices" (0..1) | Inner | [kb:sampleapp::bmm::joins.Relationship_2004:2301353851711] |
| Fact Target | Dim Products | "Fact Target" (n..n), "Dim Products" (0..1) | Inner | [kb:sampleapp::bmm::joins.Relationship_2004:2331353851711] |

No business-model join is recorded between "Dim Sales Rep" and "Fact Target", and none that names "Dim Products (Translated)".

### Joins between database tables

17 joins are recorded between the database tables of this subject area: 3 of the kind "complex join", which carry a condition, and 14 foreign keys, which pair one column with one column.

| Kind | Name | From table and column | To table and column | Evidence |
|---|---|---|---|---|
| Complex join | Complex Join | FactCrossJoin | SAMP_OFFICES_D | [kb:sampleapp::phys::joins.Complex Join] |
| Complex join | Complex Join#1 | FactCrossJoin | SAMP_PRODUCTS_D | [kb:sampleapp::phys::joins.Complex Join#1] |
| Complex join | Complex Join#2 | FactCrossJoin | EMPLOYEE | [kb:sampleapp::phys::joins.Complex Join#2] |
| Foreign key | EMP_PARENT_CHILD_FKey | EMP_PARENT_CHILD.ANCESTOR_KEY (DOUBLE, precision 15) | EMPLOYEE.EMP_ID (DOUBLE, precision 15) | [kb:sampleapp::phys::xe.SAMPLESALES.EMP_PARENT_CHILD.EMP_PARENT_CHILD_FKey] |
| Foreign key | SAMP_PRODUCTS_D_TRANSLATION_FKey | SAMP_PRODUCTS_D_TRANSLATION.PROD_KEY (DOUBLE, precision 15) | SAMP_PRODUCTS_D.PROD_KEY (DOUBLE, precision 15) | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_PRODUCTS_D_TRANSLATION.SAMP_PRODUCTS_D_TRANSLATION_FKey] |
| Foreign key | SAMP_REVENUE_A_FKey | SAMP_REVENUE_A.BILL_MTH_KEY (DOUBLE, precision 15) | SAMP_TIME_MTH_D.MTH_KEY (DOUBLE, precision 15) | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A.SAMP_REVENUE_A_FKey] |
| Foreign key | SAMP_REVENUE_A_FKey#1 | SAMP_REVENUE_A.PROD_KEY (DOUBLE, precision 15) | SAMP_PRODUCTS_D.PROD_KEY (DOUBLE, precision 15) | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A.SAMP_REVENUE_A_FKey#1] |
| Foreign key | SAMP_REVENUE_A_MTH_Foreign Key | SAMP_REVENUE_A_MTH.BILL_MTH_KEY (DOUBLE, precision 22) | SAMP_TIME_MTH_D.MTH_KEY (DOUBLE, precision 15) | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A_MTH.SAMP_REVENUE_A_MTH_Foreign Key] |
| Foreign key | SAMP_REVENUE_A_YR_Foreign Key | SAMP_REVENUE_A_YR.BILL_YEAR_KEY (DOUBLE) | SAMP_TIME_MTH_D.CAL_YEAR (DOUBLE, precision 15) | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A_YR.SAMP_REVENUE_A_YR_Foreign Key] |
| Foreign key | SAMP_REVENUE_F_FKey | SAMP_REVENUE_F.BILL_MTH_KEY (DOUBLE, precision 15) | SAMP_TIME_MTH_D.MTH_KEY (DOUBLE, precision 15) | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F.SAMP_REVENUE_F_FKey] |
| Foreign key | SAMP_REVENUE_F_FKey#1 | SAMP_REVENUE_F.OFFICE_KEY (DOUBLE, precision 15) | SAMP_OFFICES_D.OFFICE_KEY (DOUBLE, precision 15) | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F.SAMP_REVENUE_F_FKey#1] |
| Foreign key | SAMP_REVENUE_F_FKey#2 | SAMP_REVENUE_F.PROD_KEY (DOUBLE, precision 15) | SAMP_PRODUCTS_D.PROD_KEY (DOUBLE, precision 15) | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F.SAMP_REVENUE_F_FKey#2] |
| Foreign key | SAMP_REVENUE_F_FKey#3 | SAMP_REVENUE_F.BILL_DAY_DT (DATE) | SAMP_TIME_DAY_D.CALENDAR_DATE (DATE) | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F.SAMP_REVENUE_F_FKey#3] |
| Foreign key | SAMP_REVENUE_F_FKey#4 | SAMP_REVENUE_F.EMPLOYEE_KEY (INT) | EMP_PARENT_CHILD.MEMBER_KEY (DOUBLE, precision 15) | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F.SAMP_REVENUE_F_FKey#4] |
| Foreign key | SAMP_TARGETS_F_FKey | SAMP_TARGETS_F.BILL_MTH_KEY (DOUBLE, precision 15) | SAMP_TIME_MTH_D.MTH_KEY (DOUBLE, precision 15) | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TARGETS_F.SAMP_TARGETS_F_FKey] |
| Foreign key | SAMP_TARGETS_F_FKey#1 | SAMP_TARGETS_F.OFFICE_KEY (DOUBLE, precision 15) | SAMP_OFFICES_D.OFFICE_KEY (DOUBLE, precision 15) | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TARGETS_F.SAMP_TARGETS_F_FKey#1] |
| Foreign key | SAMP_TARGETS_F_FKey#2 | SAMP_TARGETS_F.PROD_KEY (DOUBLE, precision 15) | SAMP_PRODUCTS_D.PROD_KEY (DOUBLE, precision 15) | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TARGETS_F.SAMP_TARGETS_F_FKey#2] |

Each of the three complex joins has the join condition `1=1` and the cardinality `ONE TO ONE`, exactly as recorded [kb:sampleapp::phys::joins.Complex Join] [kb:sampleapp::phys::joins.Complex Join#1] [kb:sampleapp::phys::joins.Complex Join#2]. Each foreign key is one pair of columns set equal, as the table shows. Why "FactCrossJoin" is joined to three tables with this condition is not recorded (open question Q3).

### Points to note

- The join from SAMP_REVENUE_F to the employee data goes to EMP_PARENT_CHILD.MEMBER_KEY, and EMP_PARENT_CHILD is joined to EMPLOYEE by ANCESTOR_KEY = EMP_ID [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F.SAMP_REVENUE_F_FKey#4] [kb:sampleapp::phys::xe.SAMPLESALES.EMP_PARENT_CHILD.EMP_PARENT_CHILD_FKey]. How many rows of EMP_PARENT_CHILD exist per employee is not recorded (open question Q10).
- The join of SAMP_REVENUE_A_YR goes to the column CAL_YEAR of SAMP_TIME_MTH_D, the same table that the other joins reach by MTH_KEY [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A_YR.SAMP_REVENUE_A_YR_Foreign Key] [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A_MTH.SAMP_REVENUE_A_MTH_Foreign Key]. Whether SAMP_TIME_MTH_D holds one row or several per value of CAL_YEAR is not recorded (open question Q13).
- SAMP_REVENUE_F is joined to both time tables: by BILL_MTH_KEY to SAMP_TIME_MTH_D and by BILL_DAY_DT to SAMP_TIME_DAY_D [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F.SAMP_REVENUE_F_FKey] [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F.SAMP_REVENUE_F_FKey#3]. SAMP_TARGETS_F is joined to SAMP_TIME_MTH_D only; no join from it to SAMP_TIME_DAY_D is recorded [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TARGETS_F.SAMP_TARGETS_F_FKey] (open question Q8).
- No database join is recorded between SAMP_TIME_DAY_D and SAMP_TIME_MTH_D, and none from SAMP_TARGETS_F to EMPLOYEE or EMP_PARENT_CHILD.
- The two columns of the join SAMP_REVENUE_F_FKey#4 have different types as recorded: INT and DOUBLE, precision 15 [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F.SAMP_REVENUE_F_FKey#4].

## Drill paths

The business model holds four drill paths ("dimensions") for this subject area, and report authors are offered all four. The member counts below are the numbers recorded in the repository, not counts of the data.

### Office Hierarchy

Path: Dim Offices Total, Company, Organization, Department, Office Detail. The drill path has 5 levels and its default root level is "Dim Offices Total" [kb:sampleapp::bmm::SAMPLESALES.Office Hierarchy].

| Level | Key field | Member count as recorded | Shown to report authors as | Evidence |
|---|---|---|---|---|
| 0 Dim Offices Total (grand total) | none recorded | 1 | level "Dim Offices Total", no field | [kb:sampleapp::bmm::SAMPLESALES.Office Hierarchy.Dim Offices Total] [kb:sampleapp::pres::Sample Sales.Dim Offices.Office Hierarchy.Dim Offices Total] |
| 1 Company | "Company" of Dim Offices | 1 | level "Company", field "Company" | [kb:sampleapp::bmm::SAMPLESALES.Office Hierarchy.Company] [kb:sampleapp::pres::Sample Sales.Dim Offices.Office Hierarchy.Company] [kb:sampleapp::pres::Sample Sales.Dim Offices.Office Hierarchy.Company.0] |
| 2 Organization | "Organization" of Dim Offices | 1 | level "Organization", field "Organization" | [kb:sampleapp::bmm::SAMPLESALES.Office Hierarchy.Organization] [kb:sampleapp::pres::Sample Sales.Dim Offices.Office Hierarchy.Organization] [kb:sampleapp::pres::Sample Sales.Dim Offices.Office Hierarchy.Organization.0] |
| 3 Department | "Department" of Dim Offices | 1 | level "Department", field "Department" | [kb:sampleapp::bmm::SAMPLESALES.Office Hierarchy.Department] [kb:sampleapp::pres::Sample Sales.Dim Offices.Office Hierarchy.Department] [kb:sampleapp::pres::Sample Sales.Dim Offices.Office Hierarchy.Department.0] |
| 4 Office Detail | "Office Dsc" of Dim Offices | 1 | level "Office Detail", field "Office Dsc" | [kb:sampleapp::bmm::SAMPLESALES.Office Hierarchy.Office Detail] [kb:sampleapp::pres::Sample Sales.Dim Offices.Office Hierarchy.Office Detail] [kb:sampleapp::pres::Sample Sales.Dim Offices.Office Hierarchy.Office Detail.0] |

Report authors are offered it as "Office Hierarchy" in the table "Dim Offices", with 5 levels [kb:sampleapp::pres::Sample Sales.Dim Offices.Office Hierarchy].

### Products Hierararchy

The name is spelled this way in the repository. Path: Dim Products Total, Brand, Line Of Business, Products Type, Product Detail. The drill path has 5 levels and its default root level is "Dim Products Total" [kb:sampleapp::bmm::SAMPLESALES.Products Hierararchy].

| Level | Key field | Member count as recorded | Shown to report authors as | Evidence |
|---|---|---|---|---|
| 0 Dim Products Total (grand total) | none recorded | 1 | level "Dim Products Total", no field | [kb:sampleapp::bmm::SAMPLESALES.Products Hierararchy.Dim Products Total] [kb:sampleapp::pres::Sample Sales.Dim Products.Products Hierararchy.Dim Products Total] |
| 1 Brand | "Brand" of Dim Products | 1 | level "Brand", field "Brand" | [kb:sampleapp::bmm::SAMPLESALES.Products Hierararchy.Brand] [kb:sampleapp::pres::Sample Sales.Dim Products.Products Hierararchy.Brand] [kb:sampleapp::pres::Sample Sales.Dim Products.Products Hierararchy.Brand.0] |
| 2 Line Of Business | "Lob" of Dim Products | 1 | level "Line Of Business", field "Lob" | [kb:sampleapp::bmm::SAMPLESALES.Products Hierararchy.Line Of Business] [kb:sampleapp::pres::Sample Sales.Dim Products.Products Hierararchy.Line Of Business] [kb:sampleapp::pres::Sample Sales.Dim Products.Products Hierararchy.Line Of Business.0] |
| 3 Products Type | "Type" of Dim Products | 1 | level "Products Type", field "Type" | [kb:sampleapp::bmm::SAMPLESALES.Products Hierararchy.Products Type] [kb:sampleapp::pres::Sample Sales.Dim Products.Products Hierararchy.Products Type] [kb:sampleapp::pres::Sample Sales.Dim Products.Products Hierararchy.Products Type.0] |
| 4 Product Detail | "Prod Dsc" of Dim Products | 1 | level "Product Detail", field "Prod Dsc" | [kb:sampleapp::bmm::SAMPLESALES.Products Hierararchy.Product Detail] [kb:sampleapp::pres::Sample Sales.Dim Products.Products Hierararchy.Product Detail] [kb:sampleapp::pres::Sample Sales.Dim Products.Products Hierararchy.Product Detail.0] |

Report authors are offered it as "Products Hierararchy" in the table "Dim Products", with 5 levels [kb:sampleapp::pres::Sample Sales.Dim Products.Products Hierararchy].

### Sales Rep Hierarchy

Path: Total, Detail. The drill path has 2 levels and is recorded as parent-child (value based: the depth is not fixed); its default root level is "Total" [kb:sampleapp::bmm::SAMPLESALES.Sales Rep Hierarchy].

| Level | Key field | Member count as recorded | Shown to report authors as | Evidence |
|---|---|---|---|---|
| 0 Total (grand total) | none recorded | 1 | no level is offered | [kb:sampleapp::bmm::SAMPLESALES.Sales Rep Hierarchy.Total] |
| 1 Detail | "Employee ID" of Dim Sales Rep | 1 | no level is offered | [kb:sampleapp::bmm::SAMPLESALES.Sales Rep Hierarchy.Detail] |

Report authors are offered it as "Sales Rep Hierarchy" in the table "Dim Sales Rep", with 0 levels; it is shown by the field "Employee Name" [kb:sampleapp::pres::Sample Sales.Dim Sales Rep.Sales Rep Hierarchy]. Which fields say who is whose parent in this drill path is not recorded (open question Q15).

### Time Hierarchy

Path: Dim Time Total, Year, Half Year, Quarter, Month, Week, Dim Time Detail. The drill path has 7 levels and its default root level is "Dim Time Total" [kb:sampleapp::bmm::SAMPLESALES.Time Hierarchy].

| Level | Key field | Member count as recorded | Shown to report authors as | Evidence |
|---|---|---|---|---|
| 0 Dim Time Total (grand total) | none recorded | 1 | level "Dim Time Total", no field | [kb:sampleapp::bmm::SAMPLESALES.Time Hierarchy.Dim Time Total] [kb:sampleapp::pres::Sample Sales.Dim Time.Time Hierarchy.Dim Time Total] |
| 1 Year | "Per Name Year" of Dim Time | 2 | level "Year", field "Per Name Year" | [kb:sampleapp::bmm::SAMPLESALES.Time Hierarchy.Year] [kb:sampleapp::pres::Sample Sales.Dim Time.Time Hierarchy.Year] [kb:sampleapp::pres::Sample Sales.Dim Time.Time Hierarchy.Year.0] |
| 2 Half Year | "Per Name Half" of Dim Time | 180 | level "Half Year", field "Per Name Half" | [kb:sampleapp::bmm::SAMPLESALES.Time Hierarchy.Half Year] [kb:sampleapp::pres::Sample Sales.Dim Time.Time Hierarchy.Half Year] [kb:sampleapp::pres::Sample Sales.Dim Time.Time Hierarchy.Half Year.0] |
| 3 Quarter | "Per Name Qtr" of Dim Time | 270 | level "Quarter", field "Per Name Qtr" | [kb:sampleapp::bmm::SAMPLESALES.Time Hierarchy.Quarter] [kb:sampleapp::pres::Sample Sales.Dim Time.Time Hierarchy.Quarter] [kb:sampleapp::pres::Sample Sales.Dim Time.Time Hierarchy.Quarter.0] |
| 4 Month | "Per Name Month" of Dim Time | 322 | level "Month", field "Per Name Month" | [kb:sampleapp::bmm::SAMPLESALES.Time Hierarchy.Month] [kb:sampleapp::pres::Sample Sales.Dim Time.Time Hierarchy.Month] [kb:sampleapp::pres::Sample Sales.Dim Time.Time Hierarchy.Month.0] |
| 5 Week | "Per Name Week" of Dim Time | 350 | level "Week", field "Per Name Week" | [kb:sampleapp::bmm::SAMPLESALES.Time Hierarchy.Week] [kb:sampleapp::pres::Sample Sales.Dim Time.Time Hierarchy.Week] [kb:sampleapp::pres::Sample Sales.Dim Time.Time Hierarchy.Week.0] |
| 6 Dim Time Detail | "Calendar Date" of Dim Time | 365 | level "Dim Time Detail", field "Calendar Date" | [kb:sampleapp::bmm::SAMPLESALES.Time Hierarchy.Dim Time Detail] [kb:sampleapp::pres::Sample Sales.Dim Time.Time Hierarchy.Dim Time Detail] [kb:sampleapp::pres::Sample Sales.Dim Time.Time Hierarchy.Dim Time Detail.0] |

Report authors are offered it as "Time Hierarchy" in the table "Dim Time", with 7 levels [kb:sampleapp::pres::Sample Sales.Dim Time.Time Hierarchy].

### Points to note

- The key of every level below the grand total is a descriptive field, not a key field: for example "Office Dsc" for "Office Detail", "Prod Dsc" for "Product Detail" and "Per Name Month" for "Month" [kb:sampleapp::bmm::SAMPLESALES.Office Hierarchy.Office Detail] [kb:sampleapp::bmm::SAMPLESALES.Products Hierararchy.Product Detail] [kb:sampleapp::bmm::SAMPLESALES.Time Hierarchy.Month]. Whether each of these fields has a different value for every member is not recorded (open question Q14).
- The recorded member counts are 1 for every level of "Office Hierarchy", "Products Hierararchy" and "Sales Rep Hierarchy", and 2, 180, 270, 322, 350 and 365 for the levels of "Time Hierarchy" from "Year" to "Dim Time Detail" [kb:sampleapp::bmm::SAMPLESALES.Time Hierarchy.Year] [kb:sampleapp::bmm::SAMPLESALES.Time Hierarchy.Half Year] [kb:sampleapp::bmm::SAMPLESALES.Time Hierarchy.Dim Time Detail]. Whether these numbers match the data is not recorded (open question Q14).
- "Sales Rep Hierarchy" is the only drill path recorded as parent-child, and the only one offered to report authors with 0 levels [kb:sampleapp::bmm::SAMPLESALES.Sales Rep Hierarchy] [kb:sampleapp::pres::Sample Sales.Dim Sales Rep.Sales Rep Hierarchy].

## Values set per user or for the whole system

The formulas, displayed names, filters and queries of this subject area name 10 values inside VALUEOF(...). Of these 10 names, 1 is defined in the repository and 9 are used by name and not defined in the repository. The slice holds 1 variable (counted).

### Defined in the repository

**PREFERRED_CURRENCY** is a value set per user at login (a "session variable"); its security mask is recorded as `2` [kb:sampleapp::var::PREFERRED_CURRENCY].

- Its default value is `'gc1'` [kb:sampleapp::var::PREFERRED_CURRENCY].
- It is set by the query-that-sets-the-value ("init block") named "initBlockPrefCurrency", which is recorded as a session init block; whether it is enabled is not recorded, and its refresh period is not recorded [kb:sampleapp::var::init.initBlockPrefCurrency].
- The query, exactly as recorded, is `select 'VALUEOF(NQ_SESSION.PREFERED_CURRENCY)' from dual` [kb:sampleapp::var::init.initBlockPrefCurrency]. The query names `PREFERED_CURRENCY`, spelled with one R, which is one of the names not defined in the repository (table below).
- Where the subject area uses it: in 1 place, the formula of the field "Preferred Currency" of the business table "Fact Revenue", which names it inside VALUEOF(...) and also lists it among the objects the formula refers to [kb:sampleapp::var::PREFERRED_CURRENCY]. The formula itself is in the section "Fact Revenue".

What value a user actually gets at login is not recorded (open question Q16).

### Used by name, not defined in the repository

Each name below is used by name and not defined in the repository: the Knowledge Base holds no variable with exactly this name, nor one that differs only in letter case (from the variables table). The repository does not say whether such a name is a value the server sets itself or one set outside the repository.

| Name | Used in |
|---|---|
| `CN_Sample_Sales_Dim_Time` | the displayed name of the table "Dim Time" that report authors see |
| `CN_Sample_Sales_Dim_Time_Calendar_Date` | the displayed name of the field "Calendar Date" of "Dim Time" |
| `CN_Sample_Sales_Dim_Time_Per_Name_Half` | the displayed name of the field "Per Name Half" of "Dim Time" |
| `CN_Sample_Sales_Dim_Time_Per_Name_Month` | the displayed name of the field "Per Name Month" of "Dim Time" |
| `CN_Sample_Sales_Dim_Time_Per_Name_Qtr` | the displayed name of the field "Per Name Qtr" of "Dim Time" |
| `CN_Sample_Sales_Dim_Time_Per_Name_Week` | the displayed name of the field "Per Name Week" of "Dim Time" |
| `CN_Sample_Sales_Dim_Time_Per_Name_Year` | the displayed name of the field "Per Name Year" of "Dim Time" |
| `PREFERED_CURRENCY` | the query of the init block "initBlockPrefCurrency" |
| `WEBLANGUAGE` | the formula of the field "Prod Dsc (Translated)" of the business table "Dim Products" |

What these nine values hold at run time is not recorded. For the seven names that begin with CN_ see open questions Q1 and Q11; for `WEBLANGUAGE` see open question Q9; for `PREFERED_CURRENCY` see open question Q16.

### Points to note

- The query recorded as setting PREFERRED_CURRENCY reads no database table other than dual, and the text it selects stands between single quotation marks [kb:sampleapp::var::init.initBlockPrefCurrency] (open question Q16).
- The name inside that query, `PREFERED_CURRENCY`, differs from the variable's own name by one letter and is not defined in the repository (from the variables table).

## Security

### Row filters

No row filter is recorded for this subject area: the security rows of the slice hold permissions on objects and one role, and no filter on rows of data.

### Permissions on tables and fields

Six permissions are recorded. All six are on the two measures of the table "Fact Target" that report authors see; none is recorded on any other table or field of the subject area. Each permission is given through a "privilege package", named in the third column.

| Permission | On | Given through privilege package | Held by | Evidence |
|---|---|---|---|---|
| `write` | field "Revenue" of "Fact Target" | PrivPack_4209:18101355661677 | 1 role: BI Author Role | [kb:sampleapp::sec::obj.ObjPriv_1500:18131355661677.o0] |
| `write` | field "Units" of "Fact Target" | PrivPack_4209:18101355661677 | 1 role: BI Author Role | [kb:sampleapp::sec::obj.ObjPriv_1500:18131355661677.o1] |
| `read` | field "Revenue" of "Fact Target" | PrivPack_4209:18141355661677 | no role is recorded in the slice for that privilege package | [kb:sampleapp::sec::obj.ObjPriv_1500:18161355661677.o0] |
| `read` | field "Units" of "Fact Target" | PrivPack_4209:18141355661677 | no role is recorded in the slice for that privilege package | [kb:sampleapp::sec::obj.ObjPriv_1500:18161355661677.o1] |
| `read` | field "Revenue" of "Fact Target" | PrivPack_4209:18181355661677 | no role is recorded in the slice for that privilege package | [kb:sampleapp::sec::obj.ObjPriv_1500:18201355661677.o0] |
| `write` | field "Revenue" of "Fact Target" | PrivPack_4209:18181355661677 | no role is recorded in the slice for that privilege package | [kb:sampleapp::sec::obj.ObjPriv_1500:18211355661677.o0] |

Who holds the four permissions for which no role is recorded, and what applies to the tables and fields on which no permission is recorded, is not recorded (open question Q17).

### Roles

One role is recorded as holding a privilege package:

- Display name `BI Author Role`; guid `BIAuthor`. It holds the query privilege `QueryPriv_4204:18331355753308` of the privilege package `PrivPack_4209:18101355661677` [kb:sampleapp::sec::pkg.PrivPack_4209:18101355661677.q0]. Its name in the repository is the following; it is the name of one role, and the space is part of the name [kb:sampleapp::sec::pkg.PrivPack_4209:18101355661677.q0]:

```
6D7874D30FD9EC2E7C15CF8DD0C61529AF3BDCAD955E5AD1372E6D902B14F0A9 3FA146A018E99435FCE83B3F7BDDBF5A30BAF7BEF918C3AFAA354A94E310F903
```

### Points to note

- The only role recorded with a permission here, "BI Author Role", holds `write` on "Revenue" and "Units" of "Fact Target" [kb:sampleapp::sec::obj.ObjPriv_1500:18131355661677.o0] [kb:sampleapp::sec::obj.ObjPriv_1500:18131355661677.o1].
- The field "Revenue" of "Fact Target" carries four permissions through three privilege packages; the field "Units" carries two through two [kb:sampleapp::sec::obj.ObjPriv_1500:18201355661677.o0] [kb:sampleapp::sec::obj.ObjPriv_1500:18211355661677.o0] [kb:sampleapp::sec::obj.ObjPriv_1500:18161355661677.o1].

## Reports built on this subject area

Reports and dashboards were not examined: the estate has no Web Catalog half (from the manifest). No report is therefore described here, and that does not mean none exists. Calculations, filters and prompts defined inside reports are unknown (from the manifest). Which reports and dashboards use this subject area, and which fields they use, is an open question (open question Q18).

## What the export did not capture

### Errors

None: the extraction recorded no error about an object of this subject area (from the errors table).

### Totals of what was not read

These totals are copied from the list of unread content (from the unparsed table). A row is one attribute, element or text of one kind of object; its occurrences are how often it is in the source over the whole estate, not only in this subject area.

- All kinds together: 130 rows, 2043 occurrences in the source.
- Kind unread_attribute: 101 rows, 1837 occurrences.
- Kind unread_element: 26 rows, 201 occurrences.
- Kind unread_text: 3 rows, 5 occurrences.

### What was not read, per kind of object

Each line names what was not read, exactly as the export names it, with how often it occurs in brackets. Nothing is said here about what any of it holds: it was not read.

Attributes not read (unread_attribute):

| Kind of object | Rows | Occurrences | Not read |
|---|---|---|---|
| ComplexJoin | 3 | 12 | ComplexJoin@mdsid (x4); Expr@mdsid (x4); Expr@name (x4) |
| Dimension | 11 | 251 | Dimension@isTimeDim (x1); LogicalKey@isChronKey (x6); LogicalKey@isPrimary (x17); LogicalKey@mdsid (x24); LogicalKey@name (x24); LogicalLevel@isFDUC (x22); LogicalLevel@levelConst (x22); RefLogicalColumn@logicalColumnRef (x41); RefLogicalColumn@refId (x58); RefLogicalLevel@logicalLevelRef (x18); RefLogicalLevel@refId (x18) |
| Group | 4 | 14 | Group@displayName (x5); Group@guid (x5); RefGroup@groupRef (x2); RefGroup@refId (x2) |
| InitBlock | 5 | 9 | DBMapItem@name (x2); InitBlock@connectionPoolRef (x2); InitBlock@isRowWiseInit (x1); RefVariable@refId (x2); RefVariable@variableRef (x2) |
| LogicalComplexJoin | 1 | 9 | LogicalComplexJoin@mdsid (x9) |
| LogicalTable | 18 | 209 | AggrRule@mdsid (x13); Expr@mdsid (x15); Expr@name (x15); LogicalColumn@descriptorIDRef (x3); LogicalColumn@isDerived (x2); LogicalKey@isPrimary (x8); LogicalKey@mdsid (x8); LogicalKey@name (x8); LogicalTable@isLookupTable (x1); MeasureDefn@isCommutative (x13); MeasureDefn@mdsid (x13); MeasureDefn@name (x13); RefLogicalColumn@logicalColumnRef (x12); RefLogicalColumn@refId (x12); RefLogicalTableSource@logicalTableSourceRef (x17); RefLogicalTableSource@refId (x17); RefObject@objectTypeId (x19); RefObject@refId (x20) |
| LogicalTableSource | 19 | 923 | Expr@mdsid (x95); Expr@name (x95); FragmentContent@mdsid (x17); FragmentContent@name (x17); GroupBy@mdsid (x17); GroupBy@name (x17); Join@cardinality (x2); Join@physicalJoinRef (x2); Join@physicalTable1Ref (x2); Join@physicalTable2Ref (x2); Join@type (x2); Link@startNodeTableRef (x18); LogicalColumnExpr@mdsid (x95); LogicalColumnExpr@name (x95); LogicalTableSource@closureTableRef (x1); RefObject@objectTypeId (x206); RefObject@refId (x206); WhereClause@mdsid (x17); WhereClause@name (x17) |
| ObjectPrivilege | 3 | 22 | ObjectPrivilege@mdsid (x6); RefObject@objectTypeId (x8); RefObject@refId (x8) |
| PhysicalTable | 17 | 119 | PhysicalColumn@lastUpdated (x2); PhysicalColumn@rowCount (x2); PhysicalColumn@scale (x5); PhysicalForeignKey@mdsid (x18); PhysicalKey@mdsid (x14); PhysicalKey@name (x14); PhysicalTable@cacheExpiry (x1); PhysicalTable@distanceColumnRef (x1); PhysicalTable@iconIndex (x4); PhysicalTable@isCacheable (x4); PhysicalTable@lastUpdated (x4); PhysicalTable@leafColumnRef (x1); PhysicalTable@memberKeyRef (x1); PhysicalTable@parentKeyRef (x1); PhysicalTable@rowCount (x4); RefColumn@columnRef (x7); RefColumn@refId (x36) |
| PresentationCatalog | 4 | 26 | PresentationCatalog@hasDispDescription (x3); PresentationCatalog@hasDispName (x3); RefPresentationTable@presentationTableRef (x10); RefPresentationTable@refId (x10) |
| PresentationHierarchy | 6 | 87 | PresentationHierarchy@hasDispDescription (x5); PresentationHierarchy@hasDispName (x5); PresentationLevel@containerRef (x20); PresentationLevel@hasDispDescription (x20); PresentationLevel@hasDispName (x20); RefPresentationColumn@refId (x17) |
| PresentationTable | 6 | 148 | PresentationColumn@hasDispDescription (x59); PresentationColumn@hasDispName (x59); PresentationTable@hasDispDescription (x10); PresentationTable@hasDispName (x10); RefPresentationHierarchy@presentationHierarchyRef (x5); RefPresentationHierarchy@refId (x5) |
| PrivilegePackage | 2 | 4 | PrivilegePackage@userRef (x3); QueryPrivilegeMapping@databaseRef (x1) |
| Variable | 2 | 4 | Expr@mdsid (x2); Expr@name (x2) |

Elements not read (unread_element):

| Kind of object | Rows | Occurrences | Not read |
|---|---|---|---|
| Dimension | 6 | 102 | RefChildLevels (x17); RefColumns (x7); RefLogicalColumn (x41); RefLogicalColumns (x18); RefLogicalLevel (x18); RefPreferredDrillDown (x1) |
| Group | 3 | 6 | Description (x2); RefChildGroups (x2); RefGroup (x2) |
| InitBlock | 4 | 8 | DBMapItem (x2); RefVariable (x2); RefVariables (x2); Value (x2) |
| LogicalTable | 3 | 28 | LogicalKey (x8); RefColumns (x8); RefLogicalColumn (x12) |
| LogicalTableSource | 2 | 20 | Join (x2); Link (x18) |
| PhysicalTable | 4 | 14 | Description (x1); PhysicalKey (x3); RefColumn (x7); RefColumns (x3) |
| PresentationCatalog | 2 | 13 | RefPresentationTable (x10); RefTables (x3) |
| PresentationTable | 2 | 10 | RefHierarchies (x5); RefPresentationHierarchy (x5) |

Texts not read (unread_text):

| Kind of object | Rows | Occurrences | Not read |
|---|---|---|---|
| Group | 1 | 2 | Description#text (x2) |
| InitBlock | 1 | 2 | Value#text (x2) |
| PhysicalTable | 1 | 1 | Description#text (x1) |

### Left out of this subject area's slice

These are from the manifest.

- Errors of the Knowledge Base that are not about an object of this subject area: 0.
- Rows of unread content that are about whole object types or files with no reader, or about kinds of object this subject area does not hold: 15, all of the kind unmodelled_type. Nothing of such an object is in the Knowledge Base. There are 3 object types, 15 rows, 15 objects in all:
  - LogicalSourceFolder: 11 rows, 11 objects.
  - QueryPrivilege: 1 row, 1 object.
  - User: 3 rows, 3 objects.
- Rows that are in the slice only so that a reference from another row resolves: 0.

### Where a gap touches an earlier section

- LogicalKey (under LogicalTable and under Dimension) and PhysicalKey (under PhysicalTable) were not read. No section of this document states a key of a business table or of a database table (open question Q19).
- PhysicalTable@distanceColumnRef, PhysicalTable@leafColumnRef, PhysicalTable@memberKeyRef, PhysicalTable@parentKeyRef and LogicalTableSource@closureTableRef were not read, each occurring once. Open question Q15, on the drill path "Sales Rep Hierarchy", asks for what the repository shows there.
- PrivilegePackage@userRef (x3) was not read, and 3 objects of the type User and 1 of the type QueryPrivilege were left out. The section "Security" records no role for four of its six permissions (open question Q17).
- InitBlock@connectionPoolRef, InitBlock@isRowWiseInit and the element Value of InitBlock were not read. The section "Values set per user or for the whole system" quotes the query of the init block as recorded (open question Q16).
- Join (x2) under LogicalTableSource was not read. The section "Dim Sales Rep" describes a source that reads two database tables (open question Q10).

## Observations across sections

Each point puts side by side what two sections record. Nothing is concluded from them here.

- The level of detail of the source "SAMP_REVENUE_F" of Fact Revenue names the level "Detail" of the drill path "Sales Rep Hierarchy" [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.SAMP_REVENUE_F]; the level of detail of the source "SAMP_TARGETS_F" of Fact Target names "Dim Time Detail", "Office Detail" and "Product Detail" and no level of "Sales Rep Hierarchy" [kb:sampleapp::bmm::SAMPLESALES.Fact Target.src.SAMP_TARGETS_F].
- The formula of the field "Preferred Currency" of Fact Revenue reads the value PREFERRED_CURRENCY [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Preferred Currency]; PREFERRED_CURRENCY is defined in the repository as a value set per user at login with the default 'gc1' [kb:sampleapp::var::PREFERRED_CURRENCY].
- The formula of "Prod Dsc (Translated)" of Dim Products names the field "PROD_DESC" of "Dim Products (Translated)" [kb:sampleapp::bmm::SAMPLESALES.Dim Products.Prod Dsc (Translated)]; "Dim Products (Translated)" has the fields LANG_ID, PROD_DESC and PROD_KEY, read from SAMP_PRODUCTS_D_TRANSLATION, where LANG_ID is VARCHAR, precision 2 [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_PRODUCTS_D_TRANSLATION.LANG_ID].
- The displayed name of the table "Dim Time" reads the value CN_Sample_Sales_Dim_Time [kb:sampleapp::pres::Sample Sales.Dim Time], and the displayed names of its six fields read six values whose names begin with CN_Sample_Sales_Dim_Time [kb:sampleapp::pres::Sample Sales.Dim Time.Calendar Date]; the section "Values set per user or for the whole system" lists all seven names as used by name and not defined in the repository (from the variables table).
- The formula of "Prod Dsc (Translated)" reads the value WEBLANGUAGE [kb:sampleapp::bmm::SAMPLESALES.Dim Products.Prod Dsc (Translated)]; the section "Values set per user or for the whole system" lists WEBLANGUAGE as used by name and not defined in the repository (from the variables table).
- The level of detail of the source "SAMP_TARGETS_F" of Fact Target names the level "Dim Time Detail" [kb:sampleapp::bmm::SAMPLESALES.Fact Target.src.SAMP_TARGETS_F]; the level "Dim Time Detail" of "Time Hierarchy" has the key field "Calendar Date" [kb:sampleapp::bmm::SAMPLESALES.Time Hierarchy.Dim Time Detail]; SAMP_TARGETS_F is joined to SAMP_TIME_MTH_D by BILL_MTH_KEY = MTH_KEY, and no join from it to SAMP_TIME_DAY_D is recorded [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TARGETS_F.SAMP_TARGETS_F_FKey].
- The sources of Dim Time map no key field; its six fields are Calendar Date and five period names [kb:sampleapp::bmm::SAMPLESALES.Dim Time.src.LTS Time Day]; the database joins to the time tables use the columns MTH_KEY and CAL_YEAR of SAMP_TIME_MTH_D and CALENDAR_DATE of SAMP_TIME_DAY_D [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F.SAMP_REVENUE_F_FKey].
- The source "LTS Employee" of Dim Sales Rep reads both EMPLOYEE and EMP_PARENT_CHILD [kb:sampleapp::bmm::SAMPLESALES.Dim Sales Rep.src.LTS Employee]; the join from SAMP_REVENUE_F to the employee data goes to EMP_PARENT_CHILD, which is joined to EMPLOYEE by ANCESTOR_KEY = EMP_ID [kb:sampleapp::phys::xe.SAMPLESALES.EMP_PARENT_CHILD.EMP_PARENT_CHILD_FKey]; "Sales Rep Hierarchy" is parent-child with the key "Employee ID" [kb:sampleapp::bmm::SAMPLESALES.Sales Rep Hierarchy].
- The measure "Preserve" of Fact Revenue comes from "FactCrossJoin", a query kept in the repository, not a database table [kb:sampleapp::phys::xe.SAMPLESALES.FactCrossJoin.def.DefaultMulDB]; "FactCrossJoin" is joined with the condition 1=1 to SAMP_OFFICES_D, SAMP_PRODUCTS_D and EMPLOYEE [kb:sampleapp::phys::joins.Complex Join].
- The levels of detail of Fact Revenue's sources spell the drill path "Products Hierararchy" [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue]; the drill path of Dim Products is recorded under that same name, with the levels Brand, Line Of Business, Products Type and Product Detail [kb:sampleapp::bmm::SAMPLESALES.Products Hierararchy].
- The measure OBIEE uses when a query names none is "Revenue" of "Fact Target" [kb:sampleapp::pres::Sample Sales]; that measure is writeable [kb:sampleapp::bmm::SAMPLESALES.Fact Target.Revenue]; the role "BI Author Role" holds the permission write on the fields "Revenue" and "Units" of "Fact Target" [kb:sampleapp::sec::obj.ObjPriv_1500:18131355661677.o0].

## Open questions

These are the points the export could not settle. Each says how to find the answer. Write the answer after "Answer:", on the same line, or tell the agent; the document will then be updated.

### Q1. What name do report authors see for the table "Dim Time"?
Raised in: overview
Why it matters: the displayed name of the table "Dim Time" is recorded as a formula that reads a value set per user at login, CN_Sample_Sales_Dim_Time, not as a text. The export records the formula, not the name a user gets. People who confirm this document know the table by the name on their screen.
Assumed meanwhile: nothing. The table is called "Dim Time" throughout, which is its name in the repository, and the formula is quoted exactly.
How to get the answer: log in to OBIEE as a report author, open the subject area "Sample Sales" where a new analysis is built, and bring back the name shown for the table that holds the time fields. If it can be done for a second user with another language setting, bring back that name too.
Answer:

### Q2. What do the values 'gc1', 'gc2' and 'gc3' of PREFERRED_CURRENCY stand for, and what does the field "Preferred Currency" give when the value is none of them?
Raised in: table-01-fact-revenue-p1
Why it matters: the field "Preferred Currency" of Fact Revenue is a formula that reads the value PREFERRED_CURRENCY, set per user at login, lists the three values with the numbers 0, 1 and 2, and names the measures "Revenue", "Revenue (EUR)" and "Revenue (GBP)". So the number a report author gets from this one field depends on the user. The formula does not name "Revenue (INR)" and records nothing for any other value. The field is recorded as not a measure, so no rule for summarising it is recorded either.
Assumed meanwhile: nothing. The formula is quoted exactly; no currency is attached to any of the three values.
How to get the answer: log in to OBIEE as a report author, build an analysis on "Sample Sales" with the fields "Preferred Currency", "Revenue" and one field of "Dim Time", and bring back the result together with the value of this statement, run where OBIEE lets you issue logical SQL: select VALUEOF(NQ_SESSION.PREFERRED_CURRENCY) from "Sample Sales". Repeat for a user who has chosen another currency, if a choice is offered, and bring back the list of choices offered and the three results.
Answer:

### Q3. What does the measure "Preserve" of Fact Revenue hold, and what is it used for?
Raised in: table-01-fact-revenue-p1
Why it matters: "Preserve" is shown to report authors as a measure, is the only measure of the table summarised as a maximum, and is in one source only, "FactCrossJoin", in which no other measure is. Its name does not say what number it holds, and a reader cannot tell whether reports rely on it.
Assumed meanwhile: nothing. The measure is listed with its rule and its source exactly as recorded.
How to get the answer: in the OBIEE Administration Tool, open the business table "Fact Revenue", open the measure "Preserve" and bring back its description, if one was entered. Then build an analysis on "Sample Sales" with "Preserve" alone, and one with "Preserve" and one field of "Dim Products", and bring back both results and the SQL that OBIEE sent to the database for each.
Answer:

### Q4. Is "DISCNT_VALUE" of Fact Revenue the same thing as the measure "Discnt Value", and how is it meant to be summarised?
Raised in: table-01-fact-revenue-p1
Why it matters: the business table holds "Discnt Value", a measure that is summed and is in the sources "Aggregate Revenue" and "SAMP_REVENUE_F", and "DISCNT_VALUE", recorded as not a measure, in the sources "Aggregate Revenue By Month" and "Aggregate Revenue by Year", and not shown to report authors. The same holds for "Bill Mth Key" and "BILL_MTH_KEY". Two readings are possible: the upper-case fields are left over and unused, or they carry the same numbers at month and year level and something relies on them.
Assumed meanwhile: nothing. Both fields of each pair are listed as recorded and are treated as separate fields.
How to get the answer: in the OBIEE Administration Tool, open the business table "Fact Revenue" and, for "DISCNT_VALUE", "BILL_MTH_KEY" and "BILL_YEAR_KEY", bring back the list of objects that use each (other fields, sources, tables that report authors see in any subject area). If nothing uses them, say so.
Answer:

### Q5. What distinguishes "All Months Revenue" and "All Products Revenue" from "Revenue"?
Raised in: table-01-fact-revenue-p1
Why it matters: the three measures are shown side by side to report authors, each summarised as a sum of itself, and "All Months Revenue" and "All Products Revenue" are in the same two sources, "Aggregate Revenue" and "SAMP_REVENUE_F". As listed, nothing says how their numbers differ from "Revenue". A reader who takes them for the same number, or for a total over all months or all products, may be wrong either way.
Assumed meanwhile: nothing. The three are described as separate measures with the rule and sources recorded for each.
How to get the answer: in the OBIEE Administration Tool, open the measures "All Months Revenue" and "All Products Revenue" of the business table "Fact Revenue" and bring back everything recorded for each beyond the sum: any level they are fixed to, and the expression each source maps them from. Then build an analysis on "Sample Sales" with one field of "Dim Time", one field of "Dim Products", "Revenue", "All Months Revenue" and "All Products Revenue", and bring back the result.
Answer:

### Q6. Where do the numbers 0.62, 0.76 and 54.68 come from, and why are "Revenue (GBP)", "Revenue (EUR)" and "Revenue (INR)" multiplied in one source only?
Raised in: table-01-fact-revenue-p2
Why it matters: in the source "SAMP_REVENUE_F" the three measures are the database column REVENUE multiplied by 0.62, 0.76 and 54.68; in the source "Aggregate Revenue" they are the column REVENUE of SAMP_REVENUE_A with no multiplication. As recorded, the same measure gives different numbers depending on the source OBIEE reads. The three numbers are written into the mappings; where they come from and for what date they hold is not recorded.
Assumed meanwhile: nothing. Both mappings are described exactly as recorded, and no currency meaning is stated.
How to get the answer: in the OBIEE Administration Tool, open the source "Aggregate Revenue" of the business table "Fact Revenue" and bring back the expression its column mapping shows for each of the three measures. Then run these two queries against the schema SAMPLESALES and bring back both totals: select sum(REVENUE) from SAMP_REVENUE_A, and select sum(REVENUE) from SAMP_REVENUE_F. For the numbers 0.62, 0.76 and 54.68, bring back the rate table or document they were taken from, if there is one, and the date they applied.
Answer:

### Q7. Which of the five sources of Fact Revenue does OBIEE read for a query, and do the four database tables hold the same totals?
Raised in: table-01-fact-revenue-p2
Why it matters: "Revenue" and "Units" are mapped in four sources, reading SAMP_REVENUE_A, SAMP_REVENUE_A_MTH, SAMP_REVENUE_A_YR and SAMP_REVENUE_F. A priority group is recorded for "Aggregate Revenue" only (1); for the other four it is not recorded. Each source names a different level of detail. The export records neither the order in which the sources are tried nor whether the tables agree, so it cannot be said which table a given number on a report comes from, or whether the number would be the same from another.
Assumed meanwhile: nothing. Each source is described as recorded; no order among them is stated.
How to get the answer: run these four queries against the schema SAMPLESALES and bring back the eight totals: select sum(REVENUE), sum(UNITS) from SAMP_REVENUE_F; the same from SAMP_REVENUE_A; the same from SAMP_REVENUE_A_MTH; the same from SAMP_REVENUE_A_YR. Then, in OBIEE, build three analyses on "Sample Sales" with "Revenue" and (1) the year field of "Dim Time", (2) the month field of "Dim Time", (3) the month field and one field of "Dim Products", and for each bring back the SQL that OBIEE sent to the database, which names the table read.
Answer:

### Q8. What do the measures "Revenue" and "Units" of Fact Target hold, at what level of time, and what happens to a value a user types in?
Raised in: table-02-fact-target
Why it matters: both measures of Fact Target are recorded as writeable, so users can type values in; where a typed value is stored and whether anyone does this is not recorded. The table's name suggests targets, but nothing recorded says what kind (a plan, a budget, a quota) or who sets them. The source's level of detail names the level "Dim Time Detail", yet the only time field it maps is "Bill Mth Key" from the column BILL_MTH_KEY, so it cannot be said from the export whether a row of SAMP_TARGETS_F is for a day or for a month. That decides how targets may be compared with the numbers of Fact Revenue.
Assumed meanwhile: nothing. The measures are described as recorded, no business meaning is given, and the level of detail is quoted exactly.
How to get the answer: run these queries against the schema SAMPLESALES and bring back the results: select count(*), count(distinct BILL_MTH_KEY), min(BILL_MTH_KEY), max(BILL_MTH_KEY) from SAMP_TARGETS_F; and select BILL_MTH_KEY, OFFICE_KEY, PROD_KEY, count(*) from SAMP_TARGETS_F group by BILL_MTH_KEY, OFFICE_KEY, PROD_KEY having count(*) > 1. For the typed values: bring back the name of any report or dashboard in which users enter targets, and say what was done to load the rows now in SAMP_TARGETS_F.
Answer:

### Q9. Which values does WEBLANGUAGE take, and what does "Prod Dsc (Translated)" show a report author for each?
Raised in: table-04-dim-products
Why it matters: the field "Prod Dsc (Translated)" of Dim Products is a formula that names the field "PROD_DESC" of the business table "Dim Products (Translated)", the fields "Prod Dsc" and "Prod Key", and the value WEBLANGUAGE, set per user at login. So the text a report author sees in this field depends on the user. The export records the formula only: not the languages in use, and not what is shown for a product or a language that has no translation.
Assumed meanwhile: nothing. The formula is quoted exactly and no language is named.
How to get the answer: log in to OBIEE as a report author, run this statement where OBIEE lets you issue logical SQL, and bring back the value: select VALUEOF(NQ_SESSION.WEBLANGUAGE) from "Sample Sales". Build an analysis on "Sample Sales" with "Prod Key", "Prod Dsc" and "Prod Dsc (Translated)" and bring back the result. Change the language at login to another one offered, repeat both, and bring back the list of languages offered and the two results.
Answer:

### Q10. What does the field "Distance" of Dim Sales Rep hold, and how many rows does Dim Sales Rep give per employee?
Raised in: table-06-dim-sales-rep
Why it matters: the source "LTS Employee" reads two database tables. "Employee ID", "Employee Name", "Manager ID" and "Office ID" come from EMPLOYEE; "Distance" comes from EMP_PARENT_CHILD. If EMP_PARENT_CHILD holds several rows for one employee, the business table may give several rows per employee, and any number of Fact Revenue shown by sales rep could be counted more than once. The export records the two tables and the mapping, not the contents.
Assumed meanwhile: nothing. The field is listed with its database column; no meaning is given to it and no statement is made about rows per employee.
How to get the answer: in the OBIEE Administration Tool, open the physical table EMP_PARENT_CHILD and bring back its list of columns. Then run these queries against the schema SAMPLESALES and bring back the results: select count(*) from EMPLOYEE; select count(*) from EMP_PARENT_CHILD; select DISTANCE, count(*) from EMP_PARENT_CHILD group by DISTANCE order by DISTANCE. Then build an analysis on "Sample Sales" with "Employee Name" and "Distance" only, and bring back the result and the SQL that OBIEE sent to the database.
Answer:

### Q11. What names do report authors see for the six fields of "Dim Time"?
Raised in: table-07-dim-time
Why it matters: the displayed name of each field of "Dim Time" ("Calendar Date", "Per Name Half", "Per Name Month", "Per Name Qtr", "Per Name Week", "Per Name Year") is recorded as a formula that reads a value set per user at login, such as CN_Sample_Sales_Dim_Time_Calendar_Date. The export records the formulas, not the names users get, and the names may differ by user. People who confirm this document, and reports that refer to the fields by their displayed name, use the names on screen.
Assumed meanwhile: nothing. The six fields are called by their names in the repository and each formula is quoted exactly.
How to get the answer: log in to OBIEE as a report author, open the subject area "Sample Sales" where a new analysis is built, and bring back the six field names shown under the table that holds the time fields, in the order shown. If it can be done for a second user with another language setting, bring back those six names too.
Answer:

### Q12. Which of the two sources of Dim Time does OBIEE read, and do SAMP_TIME_DAY_D and SAMP_TIME_MTH_D give the same year, half, quarter and month names?
Raised in: table-07-dim-time
Why it matters: "Per Name Year", "Per Name Half", "Per Name Qtr" and "Per Name Month" are mapped in both sources, "LTS Time Day" (table SAMP_TIME_DAY_D) and "LTS Time Month" (table SAMP_TIME_MTH_D). Neither source has a recorded priority group. If the two tables spell or cover the periods differently, a report gives different period labels, or loses rows, depending on the source read. The export does not record the contents of either table.
Assumed meanwhile: nothing. Both sources are described as recorded; no order between them is stated.
How to get the answer: run these two queries against the schema SAMPLESALES and bring back both results: select distinct PER_NAME_YEAR, PER_NAME_HALF, PER_NAME_QTR, PER_NAME_MONTH from SAMP_TIME_DAY_D order by 1, 2, 3, 4; and the same from SAMP_TIME_MTH_D. Say whether the two lists are identical; if not, bring back the rows that are in one list and not in the other.
Answer:

### Q13. Does SAMP_TIME_MTH_D hold one row per year, or several, for the join from SAMP_REVENUE_A_YR?
Raised in: joins
Why it matters: the foreign key "SAMP_REVENUE_A_YR_Foreign Key" sets SAMP_REVENUE_A_YR.BILL_YEAR_KEY equal to SAMP_TIME_MTH_D.CAL_YEAR. The other joins reach SAMP_TIME_MTH_D by MTH_KEY, which suggests one row per month. If the table holds several rows with the same CAL_YEAR, each row of the yearly table is paired with several rows, and what a yearly Revenue or Units then shows depends on how OBIEE writes the query. The export records the join, not the contents of the table.
Assumed meanwhile: nothing. The join is listed exactly as recorded.
How to get the answer: run this query against the schema SAMPLESALES and bring back the result: select CAL_YEAR, count(*) from SAMP_TIME_MTH_D group by CAL_YEAR order by CAL_YEAR. Then build an analysis on "Sample Sales" with "Per Name Year" and "Revenue" only, bring back the result and the SQL that OBIEE sent to the database, and compare the total with: select sum(REVENUE) from SAMP_REVENUE_A_YR.
Answer:

### Q14. Do the level keys of the drill paths identify one member each, and do the recorded member counts match the data?
Raised in: hierarchies
Why it matters: every level below the grand total has a descriptive field as its key: "Company", "Organization", "Department", "Office Dsc"; "Brand", "Lob", "Type", "Prod Dsc"; "Per Name Year", "Per Name Half", "Per Name Qtr", "Per Name Month", "Per Name Week", "Calendar Date". If two offices or two products carry the same description, or a month name repeats in another year, numbers for different members are added together when a report drills. The recorded member counts do not help: they are 1 for every level of "Office Hierarchy" and "Products Hierararchy", and 2, 180, 270, 322, 350 and 365 for the time levels, and nothing recorded says they were taken from the data.
Assumed meanwhile: nothing. Key fields and member counts are given exactly as recorded, and no statement is made about uniqueness.
How to get the answer: run these queries against the schema SAMPLESALES and bring back the results: select count(*), count(distinct OFFICE_KEY), count(distinct OFFICE_DSC), count(distinct DEPARTMENT), count(distinct ORGANIZATION), count(distinct COMPANY) from SAMP_OFFICES_D; select count(*), count(distinct PROD_KEY), count(distinct PROD_DSC), count(distinct TYPE), count(distinct LOB), count(distinct BRAND) from SAMP_PRODUCTS_D; select count(*), count(distinct CALENDAR_DATE), count(distinct PER_NAME_WEEK), count(distinct PER_NAME_MONTH), count(distinct PER_NAME_QTR), count(distinct PER_NAME_HALF), count(distinct PER_NAME_YEAR) from SAMP_TIME_DAY_D.
Answer:

### Q15. Which fields say who is whose parent in "Sales Rep Hierarchy", and how deep does it go?
Raised in: hierarchies
Why it matters: "Sales Rep Hierarchy" is recorded as parent-child with a depth that is not fixed, with the levels "Total" and "Detail", the key "Employee ID", and it is offered to report authors with 0 levels, shown by "Employee Name". The export does not record which field holds the parent of an employee, nor the table that lists every employee with each of the people above. Without that, a report that totals revenue by manager cannot be described or rebuilt.
Assumed meanwhile: nothing. The drill path is described as recorded; no field is named as the parent.
How to get the answer: in the OBIEE Administration Tool, open the drill path "Sales Rep Hierarchy" of the business model "SAMPLESALES" and bring back what it shows as the member key, the parent column, and the settings of its parent-child relationship table (the table name and the names of its four columns). Then run this query against the schema SAMPLESALES and bring back the result: select max(DISTANCE) from EMP_PARENT_CHILD.
Answer:

### Q16. Does any user ever get a value of PREFERRED_CURRENCY other than the default 'gc1', and is the name PREFERED_CURRENCY in its query intended?
Raised in: variables
Why it matters: the field "Preferred Currency" of Fact Revenue shows a different measure depending on PREFERRED_CURRENCY. Its default is 'gc1'. The query recorded as setting it is: select 'VALUEOF(NQ_SESSION.PREFERED_CURRENCY)' from dual. It reads no table of user settings, the text it selects stands between single quotation marks, and it names PREFERED_CURRENCY, with one R, which the repository does not define. Whether the init block is enabled is not recorded. As recorded it cannot be said whether any user ever gets another value than the default, and so whether the "Revenue (EUR)" and "Revenue (GBP)" branches of the field are ever used.
Assumed meanwhile: nothing. The default and the query are quoted exactly in the section; no run-time value is stated.
How to get the answer: log in to OBIEE as a report author, run this statement where OBIEE lets you issue logical SQL, and bring back the value: select VALUEOF(NQ_SESSION.PREFERRED_CURRENCY) from "Sample Sales". Then change the currency in the user's own preferences, if a currency choice is offered there, log out and in, run the statement again, and bring back the new value and the list of choices offered. In the OBIEE Administration Tool, open the init block "initBlockPrefCurrency" and bring back whether it is disabled and the full text of its query.
Answer:

### Q17. Who may read and who may write "Revenue" and "Units" of Fact Target, and who may see the rest of the subject area?
Raised in: security
Why it matters: six permissions are recorded, all on the measures "Revenue" and "Units" of the table "Fact Target". Two (write) are held by the role with the display name "BI Author Role". For the other four (read on both measures through the privilege package PrivPack_4209:18141355661677; read and write on "Revenue" through PrivPack_4209:18181355661677) no role is recorded in the slice. On every other table and field no permission is recorded, and no row filter is recorded; "not recorded" does not say that everyone may see everything. The manifest also counts 2 object permissions in the estate whose target was not recorded. Without this it cannot be said who sees which numbers today.
Assumed meanwhile: nothing. The six permissions and the one role are listed exactly as recorded; no statement is made about who can see or change anything else.
How to get the answer: in the OBIEE Administration Tool, open the identity manager, and for each application role bring back its object permissions on the subject area "Sample Sales", on its six tables and on the fields "Revenue" and "Units" of "Fact Target", and its data filters, if any. For the two privilege packages named above, bring back the name of the role or user each belongs to.
Answer:

### Q18. Which reports and dashboards are built on "Sample Sales", and what calculations, filters and prompts do they define?
Raised in: reports
Why it matters: the export of this estate holds the repository only and no Web Catalog, so no report or dashboard was examined. Calculated columns, filters and prompts that exist only inside reports are unknown, and so is which of the fields described here are actually used. Several open questions of this document (the measure "Preserve", the upper-case fields of Fact Revenue, the typed-in targets) depend on what reports do.
Assumed meanwhile: nothing. The document describes the model only and states no report.
How to get the answer: export the Web Catalog folders that hold the analyses and dashboards built on "Sample Sales" (an archive of the catalog folder, or the folder copied from the server's file system), place it in the estate folder sampleapp beside the repository export, and have the extraction run again. If an export is not possible, bring back the list of analyses on "Sample Sales" with, for each, its folder path and the XML shown on its advanced tab.
Answer:

### Q19. What is the key of each business table and of each database table?
Raised in: gaps
Why it matters: the export did not read LogicalKey (8 occurrences under LogicalTable, 24 under Dimension) nor PhysicalKey (under PhysicalTable). So no section of this document states which field identifies a row of Dim Offices, Dim Products, Dim Sales Rep, Dim Time, Fact Revenue or Fact Target, or which column is the key of a database table. The joins are recorded, the keys are not. Anyone building tables from this document needs to know the keys, and Dim Time in particular shows no key field among its six fields.
Assumed meanwhile: nothing. No key is stated anywhere in the document.
How to get the answer: in the OBIEE Administration Tool, open each of the seven business tables of the business model "SAMPLESALES" and bring back the name and the columns of each key it shows; then open each of the 13 database tables named in this document under the schema SAMPLESALES and bring back the same. If the extraction can be extended to read LogicalKey and PhysicalKey, run it again instead.
Answer:

## Answered questions

Questions that have been answered. Statements that rest on an answer carry the question's number in their marker.

## Not described

Rows of the Knowledge Base that this document deliberately leaves out, each with the reason.

Nothing was left out: every required row is described above.

## Questions the export answered

Questions raised while one section was written and settled by a later section. Their numbers are not used again.

None.
