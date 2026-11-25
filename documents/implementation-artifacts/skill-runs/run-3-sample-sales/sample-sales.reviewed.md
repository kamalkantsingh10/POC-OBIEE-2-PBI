Status: reviewed 2026-11-23 (25 findings corrected, 1 turned into questions, 14 questions open)

# Sample Sales — the semantic model as it exists today

**What this is.** A plain-language description of the Sample Sales subject area in OBIEE: what report authors see, what each number is, how the tables relate, where the data comes from and what the database tables hold. It describes the existing system only.

**How to read it.** Statements of fact end with a marker in square brackets. The marker names the row of the Knowledge Base the statement came from, so any statement can be checked. A marker that starts with "answer" means the statement rests on an answer a person gave, not on OBIEE. Text shown as code is copied from OBIEE exactly. Where the export does not settle something, the text points to a numbered open question near the end; each says how to find the answer and has an empty answer line for you.

| | |
|---|---|
| Subject area | Sample Sales |
| Estate | sampleapp |
| Knowledge Base extracted | 2026-11-23T20:55:33Z |
| Reports and dashboards | Not examined: the estate has no Web Catalog half; only the repository half ("rpd") was extracted |

## The model in one page

**What was not examined.** Only the repository half of the estate was extracted. The estate has no Web Catalog half, so reports and dashboards were not examined, and calculations, filters and prompts defined inside reports are unknown (from the manifest; no Row identifier). The extraction recorded no error about an object of this subject area; 95 kinds of source content were not read, and 15 objects of three types that have no reader were left out (see the section What the export did not capture).

**What the subject area holds.** "Sample Sales" is built on the business model "SAMPLESALES" [kb:sampleapp::pres::Sample Sales]. Report authors see six tables: Dim Offices (9 fields), Dim Products (9), Dim Sales Rep (5), Dim Time (6), Fact Revenue (15) and Fact Target (5); 49 fields in all, of which 8 are measures. Behind them are 7 business tables with 63 fields and 11 measures, 12 sources, 13 database tables with 113 columns and 8 keys, 7 joins between business tables, 17 joins between database tables, 4 drill paths, 1 variable and 7 security rows (counted; no Row identifier).

- **Fact Revenue** has 9 measures (All Months Revenue, All Products Revenue, Discnt Value, Preserve, Revenue, Revenue (EUR), Revenue (GBP), Revenue (INR), Units) and five sources: "Aggregate Revenue", "Aggregate Revenue By Month", "Aggregate Revenue by Year", "FactCrossJoin" and "SAMP_REVENUE_F" [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue].
- **Fact Target** holds two measures, Revenue and Units, both summed, both from the database table SAMP_TARGETS_F [kb:sampleapp::bmm::SAMPLESALES.Fact Target.src.SAMP_TARGETS_F]. When a query names no measure, OBIEE uses "Fact Target"."Revenue" [kb:sampleapp::pres::Sample Sales].
- **Dimensions**: Dim Offices, keyed on "Office Key" [kb:sampleapp::bmm::SAMPLESALES.Dim Offices.key.SAMP_OFFICES_D_Key]; Dim Products, keyed on "Prod Key" [kb:sampleapp::bmm::SAMPLESALES.Dim Products.key.SAMP_PRODUCTS_D_Key]; Dim Products (Translated), a lookup table [kb:sampleapp::bmm::SAMPLESALES.Dim Products (Translated)]; Dim Sales Rep, keyed on "Employee ID" [kb:sampleapp::bmm::SAMPLESALES.Dim Sales Rep.key.EMP_ID]; Dim Time, keyed on "Calendar Date" [kb:sampleapp::bmm::SAMPLESALES.Dim Time.key.SAMP_TIME_DAY_D_Key].
- **Drill paths**: "Office Hierarchy" [kb:sampleapp::bmm::SAMPLESALES.Office Hierarchy], "Products Hierararchy" [kb:sampleapp::bmm::SAMPLESALES.Products Hierararchy], the parent-child "Sales Rep Hierarchy" [kb:sampleapp::bmm::SAMPLESALES.Sales Rep Hierarchy] and "Time Hierarchy" [kb:sampleapp::bmm::SAMPLESALES.Time Hierarchy].

**The database tables.**

| Database table | Columns | Key | Read by |
|---|---|---|---|
| EMPLOYEE | 4 | EMP_ID on EMP_ID | "LTS Employee" of Dim Sales Rep |
| EMP_PARENT_CHILD | 4 | EMP_PARENT_CHILD_Key on MEMBER_KEY | "LTS Employee" of Dim Sales Rep |
| FactCrossJoin (a query kept in the repository) | 1 | none recorded | "FactCrossJoin" of Fact Revenue |
| SAMP_OFFICES_D | 10 | SAMP_OFFICES_D_Key on OFFICE_KEY | "SAMP_OFFICES_D" of Dim Offices |
| SAMP_PRODUCTS_D | 11 | SAMP_PRODUCTS_D_Key on PROD_KEY | "SAMP_PRODUCTS_D" of Dim Products |
| SAMP_PRODUCTS_D_TRANSLATION | 3 | PK_PROD_TRANS1 on LANG_ID, PROD_KEY | "SAMP_PRODUCTS_D_TRANSLATION" of Dim Products (Translated) |
| SAMP_REVENUE_A | 5 | none recorded | "Aggregate Revenue" of Fact Revenue |
| SAMP_REVENUE_A_MTH | 4 | none recorded | "Aggregate Revenue By Month" of Fact Revenue |
| SAMP_REVENUE_A_YR | 4 | none recorded | "Aggregate Revenue by Year" of Fact Revenue |
| SAMP_REVENUE_F | 12 | none recorded | "SAMP_REVENUE_F" of Fact Revenue |
| SAMP_TARGETS_F | 5 | none recorded | "SAMP_TARGETS_F" of Fact Target |
| SAMP_TIME_DAY_D | 14 | SAMP_TIME_DAY_D_Key on CALENDAR_DATE | "LTS Time Day" of Dim Time |
| SAMP_TIME_MTH_D | 36 | SAMP_TIME_MTH_D_Key on MTH_KEY; SAMP_TIME_MTH_D_Key#1 on CAL_YEAR | "LTS Time Month" of Dim Time |

The repository does not say whether any key of a database table is its primary key. Each table's section gives its evidence.

**Before relying on a number.**

- "Revenue (EUR)", "Revenue (GBP)" and "Revenue (INR)" are the column REVENUE multiplied by 0.76, 0.62 and 54.68 in the source "SAMP_REVENUE_F" [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.SAMP_REVENUE_F#map15], but the plain column REVENUE in the source "Aggregate Revenue" [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue#map8] (open question Q4). "Revenue", "All Months Revenue" and "All Products Revenue" all map the same column REVENUE (open question Q3).
- Of the five sources of Fact Revenue, only "Aggregate Revenue" has a priority group recorded [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue]; which source a query uses is not recorded (open question Q5).
- Both measures of Fact Target can be typed into [kb:sampleapp::bmm::SAMPLESALES.Fact Target.Revenue] [kb:sampleapp::bmm::SAMPLESALES.Fact Target.Units], and "write" on them is given to "BI Author Role" [kb:sampleapp::sec::obj.ObjPriv_1500:18131355661677.o0] (open questions Q7 and Q12).
- The formula of "Preferred Currency" names the fields "Revenue", "Revenue (EUR)" and "Revenue (GBP)" and the variable PREFERRED_CURRENCY [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Preferred Currency], a session variable with the default 'gc1' [kb:sampleapp::var::PREFERRED_CURRENCY]; the query that sets it names PREFERED_CURRENCY, which the repository does not define (open question Q2).
- Dim Sales Rep joins EMPLOYEE to the closure table EMP_PARENT_CHILD on ANCESTOR_KEY [kb:sampleapp::bmm::SAMPLESALES.Dim Sales Rep.src.LTS Employee#table0.join0], and its field "Distance" comes from that table [kb:sampleapp::bmm::SAMPLESALES.Dim Sales Rep.src.LTS Employee#map4] (open question Q9).

**Open questions.** 14 questions are open, at the end of the document (counted).

## What report authors see

The subject area "Sample Sales" is built on the business model "SAMPLESALES" [kb:sampleapp::pres::Sample Sales]. It has one other name (alias): `SAMPLESALES` [kb:sampleapp::pres::Sample Sales]. When a query names no measure, OBIEE uses the measure "Fact Target"."Revenue" (the "implicit fact column") [kb:sampleapp::pres::Sample Sales].

### Tables report authors see

| Table | Fields | Business table behind it | Evidence |
|---|---|---|---|
| Dim Offices | 9 | Dim Offices (see the section Dim Offices) | [kb:sampleapp::pres::Sample Sales.Dim Offices] |
| Dim Products | 9 | Dim Products (see the section Dim Products) | [kb:sampleapp::pres::Sample Sales.Dim Products] |
| Dim Sales Rep | 5 | Dim Sales Rep (see the section Dim Sales Rep) | [kb:sampleapp::pres::Sample Sales.Dim Sales Rep] |
| Dim Time | 6 | Dim Time (see the section Dim Time) | [kb:sampleapp::pres::Sample Sales.Dim Time] |
| Fact Revenue | 15 | Fact Revenue (see the section Fact Revenue) | [kb:sampleapp::pres::Sample Sales.Fact Revenue] |
| Fact Target | 5 | Fact Target (see the section Fact Target) | [kb:sampleapp::pres::Sample Sales.Fact Target] |

The name shown to users for the table "Dim Time" is not a fixed text but a formula, as written: `VALUEOF(NQ_SESSION.CN_Sample_Sales_Dim_Time)` [kb:sampleapp::pres::Sample Sales.Dim Time]. It names the variable CN_Sample_Sales_Dim_Time; whether the repository defines it is stated in the section Values set per user or for the whole system. What name report authors actually see is not recorded (open question Q1).

### Counts

These are counted from the Slice's tables and have no Row identifier.

- Tables report authors see: 6; fields they see: 49, of which measures: 8.
- Business tables described: 7; their fields: 63, of which measures: 11 (measures not shown to report authors are described with their business table).
- Sources: 12; database tables: 13; their columns: 113 (every column of each table, whether or not a field maps it); their keys: 8.
- Business-model joins: 7; database joins: 17.
- Drill paths: 4; drill paths offered to report authors: 4.
- Variables: 1; security rows: 7.
- Reports built on the subject area: 0; dashboards placing them: 0; saved filters they use: 0.
- Errors about objects of this subject area: 0; kinds of source content not read: 95 (both listed in the section What the export did not capture).

### Items of the estate that are in no subject area

These exist in the full Knowledge Base and belong to no subject area, or not to this one. They are not described in this document. The counts come from the manifest and have no Row identifier.

- Object permissions whose target was not recorded: 2.
- Role privileges of packages that no permission of this subject area goes through: 0.
- Queries that set a value (init blocks) and set no variable of this subject area: 1.
- Variables that no expression of this subject area names: 1.

### What was examined

The estate is sampleapp; of its halves, only the repository half ("rpd") was extracted. The estate has no Web Catalog half, so reports and dashboards were not examined. The Knowledge Base was extracted at 2026-11-23T20:55:33Z by extractor version 0.1.0. (From the manifest; no Row identifier.)

This document was written from these units, in order: overview, table-01-fact-revenue, table-02-fact-target, table-03-dim-offices, table-04-dim-products, table-05-dim-products-translated, table-06-dim-sales-rep, table-07-dim-time, dbtable-01-employee, dbtable-02-emp-parent-child, dbtable-03-factcrossjoin, dbtable-04-samp-offices-d, dbtable-05-samp-products-d, dbtable-06-samp-products-d-translation, dbtable-07-samp-revenue-a, dbtable-08-samp-revenue-a-mth, dbtable-09-samp-revenue-a-yr, dbtable-10-samp-revenue-f, dbtable-11-samp-targets-f, dbtable-12-samp-time-day-d, dbtable-13-samp-time-mth-d, joins, hierarchies, variables, security, reports (one unit per dashboard or folder), saved-filters (when there are any), gaps.

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

## Fact Target

"Fact Target" is a business table of the business model "SAMPLESALES"; the repository declares 1 source for it [kb:sampleapp::bmm::SAMPLESALES.Fact Target]. In this subject area it has 5 fields, of which 2 are measures; all 5 are shown to report authors (counted; no Row identifier). Its one source is "SAMP_TARGETS_F" (counted; no Row identifier).

### Keys

Keys of the business table "Fact Target": not recorded. The repository declares no key for it (listed in the packet without a Row identifier).

### Measures

Both measures are shown to report authors in the table "Fact Target" under the same name.

| Measure | How it is summarised | Can be typed into | In sources | Evidence |
|---|---|---|---|---|
| Revenue | `SUM("SAMPLESALES"."Fact Target"."Revenue")` | yes | 1 of 1: "SAMP_TARGETS_F" | [kb:sampleapp::bmm::SAMPLESALES.Fact Target.Revenue] [kb:sampleapp::pres::Sample Sales.Fact Target.Revenue] [kb:sampleapp::bmm::SAMPLESALES.Fact Target.Revenue#aggr0] |
| Units | `SUM("SAMPLESALES"."Fact Target"."Units")` | yes | 1 of 1: "SAMP_TARGETS_F" | [kb:sampleapp::bmm::SAMPLESALES.Fact Target.Units] [kb:sampleapp::pres::Sample Sales.Fact Target.Units] [kb:sampleapp::bmm::SAMPLESALES.Fact Target.Units#aggr0] |

Both measures are recorded as writeable (open question Q7).

### Fields

All fields below are shown to report authors in the table "Fact Target" under the same name.

| Field | In sources | Evidence |
|---|---|---|
| Bill Mth Key | 1 of 1: "SAMP_TARGETS_F" | [kb:sampleapp::bmm::SAMPLESALES.Fact Target.Bill Mth Key] [kb:sampleapp::pres::Sample Sales.Fact Target.Bill Mth Key] |
| Office Key | 1 of 1: "SAMP_TARGETS_F" | [kb:sampleapp::bmm::SAMPLESALES.Fact Target.Office Key] [kb:sampleapp::pres::Sample Sales.Fact Target.Office Key] |
| Prod Key | 1 of 1: "SAMP_TARGETS_F" | [kb:sampleapp::bmm::SAMPLESALES.Fact Target.Prod Key] [kb:sampleapp::pres::Sample Sales.Fact Target.Prod Key] |

### Measures and fields report authors do not see

This table has no measures or fields that report authors do not see.

### Where the data comes from

#### Source "SAMP_TARGETS_F"

Active: true. Priority group not recorded (empty; it does not mean lowest) [kb:sampleapp::bmm::SAMPLESALES.Fact Target.src.SAMP_TARGETS_F]. Level of detail, exactly [kb:sampleapp::bmm::SAMPLESALES.Fact Target.src.SAMP_TARGETS_F]:

```
GROUPBYLEVEL("SAMPLESALES"."Time Hierarchy"."Dim Time Detail", "SAMPLESALES"."Office Hierarchy"."Office Detail", "SAMPLESALES"."Products Hierararchy"."Product Detail")
```

That is the level "Dim Time Detail" of "Time Hierarchy", "Office Detail" of "Office Hierarchy" and "Product Detail" of "Products Hierararchy" [kb:sampleapp::bmm::SAMPLESALES.Fact Target.src.SAMP_TARGETS_F].

No content filter is recorded for this source.

| Database table | Schema | Type | Evidence |
|---|---|---|---|
| SAMP_TARGETS_F (see the section Database table SAMP_TARGETS_F) | `xe.SAMPLESALES` | `none` | [kb:sampleapp::bmm::SAMPLESALES.Fact Target.src.SAMP_TARGETS_F#table0] |

This source reads one table, so it makes no join.

| Field | Database column | Type, precision, nullable | Evidence |
|---|---|---|---|
| Bill Mth Key | SAMP_TARGETS_F.BILL_MTH_KEY | type DOUBLE, precision 15, nullable true | [kb:sampleapp::bmm::SAMPLESALES.Fact Target.src.SAMP_TARGETS_F#map0] |
| Office Key | SAMP_TARGETS_F.OFFICE_KEY | type DOUBLE, precision 15, nullable true | [kb:sampleapp::bmm::SAMPLESALES.Fact Target.src.SAMP_TARGETS_F#map1] |
| Prod Key | SAMP_TARGETS_F.PROD_KEY | type DOUBLE, precision 15, nullable true | [kb:sampleapp::bmm::SAMPLESALES.Fact Target.src.SAMP_TARGETS_F#map2] |
| Revenue | SAMP_TARGETS_F.REVENUE | type DOUBLE, precision 8, scale 2, nullable true | [kb:sampleapp::bmm::SAMPLESALES.Fact Target.src.SAMP_TARGETS_F#map3] |
| Units | SAMP_TARGETS_F.UNITS | type DOUBLE, precision 15, nullable true | [kb:sampleapp::bmm::SAMPLESALES.Fact Target.src.SAMP_TARGETS_F#map4] |

### Points to note

- Both measures, "Revenue" and "Units", can be typed into (writeable true) [kb:sampleapp::bmm::SAMPLESALES.Fact Target.Revenue] [kb:sampleapp::bmm::SAMPLESALES.Fact Target.Units]. What a typed value does is not recorded (open question Q7).
- The field "Bill Mth Key" is mapped from the column BILL_MTH_KEY, while the level of detail of the source names the level "Dim Time Detail" of "Time Hierarchy" [kb:sampleapp::bmm::SAMPLESALES.Fact Target.src.SAMP_TARGETS_F#map0] [kb:sampleapp::bmm::SAMPLESALES.Fact Target.src.SAMP_TARGETS_F].

## Dim Offices

"Dim Offices" is a business table of the business model "SAMPLESALES"; the repository declares 1 source for it [kb:sampleapp::bmm::SAMPLESALES.Dim Offices]. In this subject area it has 10 fields and no measures; 1 of the 10 is not shown to report authors here (counted; no Row identifier). Its one source is "SAMP_OFFICES_D" (counted; no Row identifier).

### Keys

| Key | Primary key (as recorded) | Fields | Evidence |
|---|---|---|---|
| SAMP_OFFICES_D_Key | true | Office Key | [kb:sampleapp::bmm::SAMPLESALES.Dim Offices.key.SAMP_OFFICES_D_Key] |

### Measures

This table has no measures.

### Fields

All fields below are shown to report authors in the table "Dim Offices" under the same name.

| Field | In sources | Evidence |
|---|---|---|
| Area Name | 1 of 1: "SAMP_OFFICES_D" | [kb:sampleapp::bmm::SAMPLESALES.Dim Offices.Area Name] [kb:sampleapp::pres::Sample Sales.Dim Offices.Area Name] |
| Company | 1 of 1: "SAMP_OFFICES_D" | [kb:sampleapp::bmm::SAMPLESALES.Dim Offices.Company] [kb:sampleapp::pres::Sample Sales.Dim Offices.Company] |
| Company Key | 1 of 1: "SAMP_OFFICES_D" | [kb:sampleapp::bmm::SAMPLESALES.Dim Offices.Company Key] [kb:sampleapp::pres::Sample Sales.Dim Offices.Company Key] |
| Department | 1 of 1: "SAMP_OFFICES_D" | [kb:sampleapp::bmm::SAMPLESALES.Dim Offices.Department] [kb:sampleapp::pres::Sample Sales.Dim Offices.Department] |
| Dept Key | 1 of 1: "SAMP_OFFICES_D" | [kb:sampleapp::bmm::SAMPLESALES.Dim Offices.Dept Key] [kb:sampleapp::pres::Sample Sales.Dim Offices.Dept Key] |
| Office Dsc | 1 of 1: "SAMP_OFFICES_D" | [kb:sampleapp::bmm::SAMPLESALES.Dim Offices.Office Dsc] [kb:sampleapp::pres::Sample Sales.Dim Offices.Office Dsc] |
| Office Key | 1 of 1: "SAMP_OFFICES_D" | [kb:sampleapp::bmm::SAMPLESALES.Dim Offices.Office Key] [kb:sampleapp::pres::Sample Sales.Dim Offices.Office Key] |
| Org Key | 1 of 1: "SAMP_OFFICES_D" | [kb:sampleapp::bmm::SAMPLESALES.Dim Offices.Org Key] [kb:sampleapp::pres::Sample Sales.Dim Offices.Org Key] |
| Organization | 1 of 1: "SAMP_OFFICES_D" | [kb:sampleapp::bmm::SAMPLESALES.Dim Offices.Organization] [kb:sampleapp::pres::Sample Sales.Dim Offices.Organization] |

### Measures and fields report authors do not see

This field is not shown to report authors in this subject area. It is described because its business table is part of the model behind the subject area.

| Field | In sources | Evidence |
|---|---|---|
| Sequence | 1 of 1: "SAMP_OFFICES_D" | [kb:sampleapp::bmm::SAMPLESALES.Dim Offices.Sequence] |

### Where the data comes from

#### Source "SAMP_OFFICES_D"

Active: true. Priority group not recorded (empty; it does not mean lowest) [kb:sampleapp::bmm::SAMPLESALES.Dim Offices.src.SAMP_OFFICES_D]. Level of detail, exactly: `GROUPBYLEVEL("SAMPLESALES"."Office Hierarchy"."Office Detail")`, the level "Office Detail" of the drill path "Office Hierarchy" [kb:sampleapp::bmm::SAMPLESALES.Dim Offices.src.SAMP_OFFICES_D].

No content filter is recorded for this source.

| Database table | Schema | Type | Evidence |
|---|---|---|---|
| SAMP_OFFICES_D (see the section Database table SAMP_OFFICES_D) | `xe.SAMPLESALES` | `none` | [kb:sampleapp::bmm::SAMPLESALES.Dim Offices.src.SAMP_OFFICES_D#table0] |

This source reads one table, so it makes no join.

| Field | Database column | Type, precision, nullable | Evidence |
|---|---|---|---|
| Company | SAMP_OFFICES_D.COMPANY | type VARCHAR, precision 14, nullable true | [kb:sampleapp::bmm::SAMPLESALES.Dim Offices.src.SAMP_OFFICES_D#map0] |
| Company Key | SAMP_OFFICES_D.COMPANY_KEY | type DOUBLE, precision 15, nullable not stated | [kb:sampleapp::bmm::SAMPLESALES.Dim Offices.src.SAMP_OFFICES_D#map1] |
| Department | SAMP_OFFICES_D.DEPARTMENT | type VARCHAR, precision 25, nullable true | [kb:sampleapp::bmm::SAMPLESALES.Dim Offices.src.SAMP_OFFICES_D#map2] |
| Dept Key | SAMP_OFFICES_D.DEPT_KEY | type DOUBLE, precision 15, nullable not stated | [kb:sampleapp::bmm::SAMPLESALES.Dim Offices.src.SAMP_OFFICES_D#map3] |
| Office Dsc | SAMP_OFFICES_D.OFFICE_DSC | type VARCHAR, precision 17, nullable true | [kb:sampleapp::bmm::SAMPLESALES.Dim Offices.src.SAMP_OFFICES_D#map4] |
| Office Key | SAMP_OFFICES_D.OFFICE_KEY | type DOUBLE, precision 15, nullable not stated | [kb:sampleapp::bmm::SAMPLESALES.Dim Offices.src.SAMP_OFFICES_D#map5] |
| Org Key | SAMP_OFFICES_D.ORG_KEY | type DOUBLE, precision 15, nullable not stated | [kb:sampleapp::bmm::SAMPLESALES.Dim Offices.src.SAMP_OFFICES_D#map6] |
| Organization | SAMP_OFFICES_D.ORGANIZATION | type VARCHAR, precision 18, nullable true | [kb:sampleapp::bmm::SAMPLESALES.Dim Offices.src.SAMP_OFFICES_D#map7] |
| Sequence | SAMP_OFFICES_D.SEQUENCE | type DOUBLE, precision 15, nullable true | [kb:sampleapp::bmm::SAMPLESALES.Dim Offices.src.SAMP_OFFICES_D#map8] |
| Area Name | SAMP_OFFICES_D.AREA_NAME | type VARCHAR, precision 25, nullable true | [kb:sampleapp::bmm::SAMPLESALES.Dim Offices.src.SAMP_OFFICES_D#map9] |

### Points to note

- The primary key of "Dim Offices" is the field "Office Key" [kb:sampleapp::bmm::SAMPLESALES.Dim Offices.key.SAMP_OFFICES_D_Key], mapped from SAMP_OFFICES_D.OFFICE_KEY [kb:sampleapp::bmm::SAMPLESALES.Dim Offices.src.SAMP_OFFICES_D#map5].
- Every field comes from the one database table SAMP_OFFICES_D, with no calculation [kb:sampleapp::bmm::SAMPLESALES.Dim Offices.src.SAMP_OFFICES_D#table0].

## Dim Products

"Dim Products" is a business table of the business model "SAMPLESALES"; the repository declares 1 source for it [kb:sampleapp::bmm::SAMPLESALES.Dim Products]. In this subject area it has 12 fields and no measures; 3 of the 12 are not shown to report authors here (counted; no Row identifier). Its one source is "SAMP_PRODUCTS_D" (counted; no Row identifier).

### Keys

| Key | Primary key (as recorded) | Fields | Evidence |
|---|---|---|---|
| SAMP_PRODUCTS_D_Key | true | Prod Key | [kb:sampleapp::bmm::SAMPLESALES.Dim Products.key.SAMP_PRODUCTS_D_Key] |

### Measures

This table has no measures.

### Fields

All fields below are shown to report authors in the table "Dim Products" under the same name.

| Field | In sources | Evidence |
|---|---|---|
| Brand | 1 of 1: "SAMP_PRODUCTS_D" | [kb:sampleapp::bmm::SAMPLESALES.Dim Products.Brand] [kb:sampleapp::pres::Sample Sales.Dim Products.Brand] |
| Brand Key | 1 of 1: "SAMP_PRODUCTS_D" | [kb:sampleapp::bmm::SAMPLESALES.Dim Products.Brand Key] [kb:sampleapp::pres::Sample Sales.Dim Products.Brand Key] |
| Lob | 1 of 1: "SAMP_PRODUCTS_D" | [kb:sampleapp::bmm::SAMPLESALES.Dim Products.Lob] [kb:sampleapp::pres::Sample Sales.Dim Products.Lob] |
| Lob Key | 1 of 1: "SAMP_PRODUCTS_D" | [kb:sampleapp::bmm::SAMPLESALES.Dim Products.Lob Key] [kb:sampleapp::pres::Sample Sales.Dim Products.Lob Key] |
| Prod Dsc | 1 of 1: "SAMP_PRODUCTS_D" | [kb:sampleapp::bmm::SAMPLESALES.Dim Products.Prod Dsc] [kb:sampleapp::pres::Sample Sales.Dim Products.Prod Dsc] |
| Prod Dsc (Translated) | 0 of 1 (calculated, see below) | [kb:sampleapp::bmm::SAMPLESALES.Dim Products.Prod Dsc (Translated)] [kb:sampleapp::pres::Sample Sales.Dim Products.Prod Dsc (Translated)] |
| Prod Key | 1 of 1: "SAMP_PRODUCTS_D" | [kb:sampleapp::bmm::SAMPLESALES.Dim Products.Prod Key] [kb:sampleapp::pres::Sample Sales.Dim Products.Prod Key] |
| Type | 1 of 1: "SAMP_PRODUCTS_D" | [kb:sampleapp::bmm::SAMPLESALES.Dim Products.Type] [kb:sampleapp::pres::Sample Sales.Dim Products.Type] |
| Type Key | 1 of 1: "SAMP_PRODUCTS_D" | [kb:sampleapp::bmm::SAMPLESALES.Dim Products.Type Key] [kb:sampleapp::pres::Sample Sales.Dim Products.Type Key] |

Three fields have their identifier held by another field (OBIEE's "descriptor ID"): the field is the text shown, and filters use the other field in its place.

| Field shown | Identifier held by | Evidence |
|---|---|---|
| Lob | Lob Key | [kb:sampleapp::bmm::SAMPLESALES.Dim Products.Lob] |
| Prod Dsc | Prod Key | [kb:sampleapp::bmm::SAMPLESALES.Dim Products.Prod Dsc] |
| Type | Type Key | [kb:sampleapp::bmm::SAMPLESALES.Dim Products.Type] |

"Prod Dsc (Translated)" is calculated from other fields of the model and is mapped in no source. Its formula, exactly [kb:sampleapp::bmm::SAMPLESALES.Dim Products.Prod Dsc (Translated)]:

```
Lookup(SPARSE   "SAMPLESALES"."Dim Products (Translated)"."PROD_DESC" ,  "SAMPLESALES"."Dim Products"."Prod Dsc"  , "SAMPLESALES"."Dim Products"."Prod Key", VALUEOF(NQ_SESSION.WEBLANGUAGE) )
```

It names the field "PROD_DESC" of the business table "Dim Products (Translated)" (see the section Dim Products (Translated)), the fields "Prod Dsc" and "Prod Key" of this table, and the variable WEBLANGUAGE. Whether the repository defines WEBLANGUAGE is stated in the section Values set per user or for the whole system. What the field shows a given user is not recorded (open question Q8).

### Measures and fields report authors do not see

These fields are not shown to report authors in this subject area. They are described because their business table is part of the model behind the subject area.

| Field | In sources | Evidence |
|---|---|---|
| Attribute 1 | 1 of 1: "SAMP_PRODUCTS_D" | [kb:sampleapp::bmm::SAMPLESALES.Dim Products.Attribute 1] |
| Attribute 2 | 1 of 1: "SAMP_PRODUCTS_D" | [kb:sampleapp::bmm::SAMPLESALES.Dim Products.Attribute 2] |
| Sequence | 1 of 1: "SAMP_PRODUCTS_D" | [kb:sampleapp::bmm::SAMPLESALES.Dim Products.Sequence] |

### Where the data comes from

#### Source "SAMP_PRODUCTS_D"

Active: true. Priority group not recorded (empty; it does not mean lowest) [kb:sampleapp::bmm::SAMPLESALES.Dim Products.src.SAMP_PRODUCTS_D]. Level of detail, exactly: `GROUPBYLEVEL("SAMPLESALES"."Products Hierararchy"."Product Detail")`, the level "Product Detail" of the drill path "Products Hierararchy" [kb:sampleapp::bmm::SAMPLESALES.Dim Products.src.SAMP_PRODUCTS_D].

No content filter is recorded for this source.

| Database table | Schema | Type | Evidence |
|---|---|---|---|
| SAMP_PRODUCTS_D (see the section Database table SAMP_PRODUCTS_D) | `xe.SAMPLESALES` | `none` | [kb:sampleapp::bmm::SAMPLESALES.Dim Products.src.SAMP_PRODUCTS_D#table0] |

This source reads one table, so it makes no join.

| Field | Database column | Type, precision, nullable | Evidence |
|---|---|---|---|
| Attribute 1 | SAMP_PRODUCTS_D.ATTRIBUTE_1 | type VARCHAR, precision 7, nullable true | [kb:sampleapp::bmm::SAMPLESALES.Dim Products.src.SAMP_PRODUCTS_D#map0] |
| Attribute 2 | SAMP_PRODUCTS_D.ATTRIBUTE_2 | type VARCHAR, precision 6, nullable true | [kb:sampleapp::bmm::SAMPLESALES.Dim Products.src.SAMP_PRODUCTS_D#map1] |
| Type Key | SAMP_PRODUCTS_D.TYPE_KEY | type DOUBLE, precision 15, nullable not stated | [kb:sampleapp::bmm::SAMPLESALES.Dim Products.src.SAMP_PRODUCTS_D#map10] |
| Brand | SAMP_PRODUCTS_D.BRAND | type VARCHAR, precision 8, nullable true | [kb:sampleapp::bmm::SAMPLESALES.Dim Products.src.SAMP_PRODUCTS_D#map2] |
| Brand Key | SAMP_PRODUCTS_D.BRAND_KEY | type DOUBLE, precision 15, nullable not stated | [kb:sampleapp::bmm::SAMPLESALES.Dim Products.src.SAMP_PRODUCTS_D#map3] |
| Lob | SAMP_PRODUCTS_D.LOB | type VARCHAR, precision 13, nullable true | [kb:sampleapp::bmm::SAMPLESALES.Dim Products.src.SAMP_PRODUCTS_D#map4] |
| Lob Key | SAMP_PRODUCTS_D.LOB_KEY | type DOUBLE, precision 15, nullable not stated | [kb:sampleapp::bmm::SAMPLESALES.Dim Products.src.SAMP_PRODUCTS_D#map5] |
| Prod Dsc | SAMP_PRODUCTS_D.PROD_DSC | type VARCHAR, precision 26, nullable true | [kb:sampleapp::bmm::SAMPLESALES.Dim Products.src.SAMP_PRODUCTS_D#map6] |
| Prod Key | SAMP_PRODUCTS_D.PROD_KEY | type DOUBLE, precision 15, nullable not stated | [kb:sampleapp::bmm::SAMPLESALES.Dim Products.src.SAMP_PRODUCTS_D#map7] |
| Sequence | SAMP_PRODUCTS_D.SEQUENCE | type DOUBLE, precision 15, nullable not stated | [kb:sampleapp::bmm::SAMPLESALES.Dim Products.src.SAMP_PRODUCTS_D#map8] |
| Type | SAMP_PRODUCTS_D.TYPE | type VARCHAR, precision 12, nullable true | [kb:sampleapp::bmm::SAMPLESALES.Dim Products.src.SAMP_PRODUCTS_D#map9] |

### Points to note

- "Prod Dsc (Translated)" is mapped in no source; its formula starts with Lookup and names the field "PROD_DESC" of the business table "Dim Products (Translated)", the fields "Prod Dsc" and "Prod Key" of this table and the variable WEBLANGUAGE [kb:sampleapp::bmm::SAMPLESALES.Dim Products.Prod Dsc (Translated)] (open question Q8).
- Filters on "Lob", "Prod Dsc" and "Type" use "Lob Key", "Prod Key" and "Type Key" in their place [kb:sampleapp::bmm::SAMPLESALES.Dim Products.Lob] [kb:sampleapp::bmm::SAMPLESALES.Dim Products.Prod Dsc] [kb:sampleapp::bmm::SAMPLESALES.Dim Products.Type].
- The primary key of "Dim Products" is the field "Prod Key" [kb:sampleapp::bmm::SAMPLESALES.Dim Products.key.SAMP_PRODUCTS_D_Key].

## Dim Products (Translated)

"Dim Products (Translated)" is a business table of the business model "SAMPLESALES"; the repository declares 1 source for it [kb:sampleapp::bmm::SAMPLESALES.Dim Products (Translated)]. It is marked in the repository as a lookup table [kb:sampleapp::bmm::SAMPLESALES.Dim Products (Translated)]. In OBIEE generally, a lookup table is read through the LOOKUP function and is not joined like a dimension. In this subject area it has 3 fields and no measures, and none of the 3 is shown to report authors (counted; no Row identifier). Its one source is "SAMP_PRODUCTS_D_TRANSLATION" (counted; no Row identifier). The formula of the field "Prod Dsc (Translated)" of Dim Products names this table's field PROD_DESC (see the section Dim Products).

### Keys

| Key | Primary key (as recorded) | Fields | Evidence |
|---|---|---|---|
| PK_PROD_TRANS1 | true | PROD_KEY, LANG_ID | [kb:sampleapp::bmm::SAMPLESALES.Dim Products (Translated).key.PK_PROD_TRANS1] |

### Measures

This table has no measures.

### Fields

This table has no fields that report authors see.

### Measures and fields report authors do not see

These fields are not shown to report authors in this subject area. They are described because their business table is part of the model behind the subject area.

| Field | In sources | Evidence |
|---|---|---|
| LANG_ID | 1 of 1: "SAMP_PRODUCTS_D_TRANSLATION" | [kb:sampleapp::bmm::SAMPLESALES.Dim Products (Translated).LANG_ID] |
| PROD_DESC | 1 of 1: "SAMP_PRODUCTS_D_TRANSLATION" | [kb:sampleapp::bmm::SAMPLESALES.Dim Products (Translated).PROD_DESC] |
| PROD_KEY | 1 of 1: "SAMP_PRODUCTS_D_TRANSLATION" | [kb:sampleapp::bmm::SAMPLESALES.Dim Products (Translated).PROD_KEY] |

### Where the data comes from

#### Source "SAMP_PRODUCTS_D_TRANSLATION"

Active: true. Priority group not recorded (empty; it does not mean lowest) [kb:sampleapp::bmm::SAMPLESALES.Dim Products (Translated).src.SAMP_PRODUCTS_D_TRANSLATION]. Level of detail: not recorded. The repository gives this source no level of detail [kb:sampleapp::bmm::SAMPLESALES.Dim Products (Translated).src.SAMP_PRODUCTS_D_TRANSLATION].

No content filter is recorded for this source.

| Database table | Schema | Type | Evidence |
|---|---|---|---|
| SAMP_PRODUCTS_D_TRANSLATION (see the section Database table SAMP_PRODUCTS_D_TRANSLATION) | `xe.SAMPLESALES` | `none` | [kb:sampleapp::bmm::SAMPLESALES.Dim Products (Translated).src.SAMP_PRODUCTS_D_TRANSLATION#table0] |

This source reads one table, so it makes no join.

| Field | Database column | Type, precision, nullable | Evidence |
|---|---|---|---|
| LANG_ID | SAMP_PRODUCTS_D_TRANSLATION.LANG_ID | type VARCHAR, precision 2, nullable not stated | [kb:sampleapp::bmm::SAMPLESALES.Dim Products (Translated).src.SAMP_PRODUCTS_D_TRANSLATION#map0] |
| PROD_DESC | SAMP_PRODUCTS_D_TRANSLATION.PROD_DESC | type VARCHAR, precision 50, nullable not stated | [kb:sampleapp::bmm::SAMPLESALES.Dim Products (Translated).src.SAMP_PRODUCTS_D_TRANSLATION#map1] |
| PROD_KEY | SAMP_PRODUCTS_D_TRANSLATION.PROD_KEY | type DOUBLE, precision 15, nullable not stated | [kb:sampleapp::bmm::SAMPLESALES.Dim Products (Translated).src.SAMP_PRODUCTS_D_TRANSLATION#map2] |

### Points to note

- The table is a lookup table, keyed on PROD_KEY and LANG_ID together [kb:sampleapp::bmm::SAMPLESALES.Dim Products (Translated).key.PK_PROD_TRANS1]. Which values LANG_ID holds is not recorded (open question Q8).
- Its source has no level of detail recorded [kb:sampleapp::bmm::SAMPLESALES.Dim Products (Translated).src.SAMP_PRODUCTS_D_TRANSLATION].

## Dim Sales Rep

"Dim Sales Rep" is a business table of the business model "SAMPLESALES"; the repository declares 1 source for it [kb:sampleapp::bmm::SAMPLESALES.Dim Sales Rep]. In this subject area it has 5 fields and no measures; all 5 are shown to report authors (counted; no Row identifier). Its one source is "LTS Employee" (counted; no Row identifier).

### Keys

| Key | Primary key (as recorded) | Fields | Evidence |
|---|---|---|---|
| EMP_ID | true | Employee ID | [kb:sampleapp::bmm::SAMPLESALES.Dim Sales Rep.key.EMP_ID] |

### Measures

This table has no measures.

### Fields

All fields below are shown to report authors in the table "Dim Sales Rep" under the same name.

| Field | In sources | Evidence |
|---|---|---|
| Distance | 1 of 1: "LTS Employee" | [kb:sampleapp::bmm::SAMPLESALES.Dim Sales Rep.Distance] [kb:sampleapp::pres::Sample Sales.Dim Sales Rep.Distance] |
| Employee ID | 1 of 1: "LTS Employee" | [kb:sampleapp::bmm::SAMPLESALES.Dim Sales Rep.Employee ID] [kb:sampleapp::pres::Sample Sales.Dim Sales Rep.Employee ID] |
| Employee Name | 1 of 1: "LTS Employee" | [kb:sampleapp::bmm::SAMPLESALES.Dim Sales Rep.Employee Name] [kb:sampleapp::pres::Sample Sales.Dim Sales Rep.Employee Name] |
| Manager ID | 1 of 1: "LTS Employee" | [kb:sampleapp::bmm::SAMPLESALES.Dim Sales Rep.Manager ID] [kb:sampleapp::pres::Sample Sales.Dim Sales Rep.Manager ID] |
| Office ID | 1 of 1: "LTS Employee" | [kb:sampleapp::bmm::SAMPLESALES.Dim Sales Rep.Office ID] [kb:sampleapp::pres::Sample Sales.Dim Sales Rep.Office ID] |

### Measures and fields report authors do not see

This table has no measures or fields that report authors do not see.

### Where the data comes from

#### Source "LTS Employee"

Active: true. Priority group not recorded (empty; it does not mean lowest) [kb:sampleapp::bmm::SAMPLESALES.Dim Sales Rep.src.LTS Employee]. Level of detail, exactly: `GROUPBYLEVEL("SAMPLESALES"."Sales Rep Hierarchy"."Detail")`, the level "Detail" of the drill path "Sales Rep Hierarchy" [kb:sampleapp::bmm::SAMPLESALES.Dim Sales Rep.src.LTS Employee].

Parent-child hierarchy: this source names the database table EMP_PARENT_CHILD as the table that holds the parent-child relationships (its closure table) [kb:sampleapp::bmm::SAMPLESALES.Dim Sales Rep.src.LTS Employee]. Its member, parent, distance and leaf columns are given in the section Database table EMP_PARENT_CHILD.

No content filter is recorded for this source.

| Database table | Schema | Type | Evidence |
|---|---|---|---|
| EMPLOYEE (see the section Database table EMPLOYEE) | `xe.SAMPLESALES` | `none` | [kb:sampleapp::bmm::SAMPLESALES.Dim Sales Rep.src.LTS Employee#table0] |
| EMP_PARENT_CHILD (see the section Database table EMP_PARENT_CHILD) | `xe.SAMPLESALES` | `none` | [kb:sampleapp::bmm::SAMPLESALES.Dim Sales Rep.src.LTS Employee#table1] |

The source reads two tables and joins them. The repository lists the one join once under each of its two tables; "left" and "right" in a join type, and the cardinality, read from the first table named on the line to the second.

| Listed under | From | To | Join type | Cardinality | On | Evidence |
|---|---|---|---|---|---|---|
| EMPLOYEE | EMPLOYEE | EMP_PARENT_CHILD | `Inner` | `ONE TO MANY` | foreign key "EMP_PARENT_CHILD_FKey": EMP_PARENT_CHILD.ANCESTOR_KEY = EMPLOYEE.EMP_ID | [kb:sampleapp::bmm::SAMPLESALES.Dim Sales Rep.src.LTS Employee#table0.join0] |
| EMP_PARENT_CHILD | EMP_PARENT_CHILD | EMPLOYEE | `Inner` | `MANY TO ONE` | foreign key "EMP_PARENT_CHILD_FKey": EMP_PARENT_CHILD.ANCESTOR_KEY = EMPLOYEE.EMP_ID | [kb:sampleapp::bmm::SAMPLESALES.Dim Sales Rep.src.LTS Employee#table1.join0] |

| Field | Database column | Type, precision, nullable | Evidence |
|---|---|---|---|
| Employee ID | EMPLOYEE.EMP_ID | type DOUBLE, precision 15, nullable not stated | [kb:sampleapp::bmm::SAMPLESALES.Dim Sales Rep.src.LTS Employee#map0] |
| Employee Name | EMPLOYEE.EMP_NAME | type VARCHAR, precision 40, nullable true | [kb:sampleapp::bmm::SAMPLESALES.Dim Sales Rep.src.LTS Employee#map1] |
| Manager ID | EMPLOYEE.MANAGER_ID | type DOUBLE, precision 15, nullable true | [kb:sampleapp::bmm::SAMPLESALES.Dim Sales Rep.src.LTS Employee#map2] |
| Office ID | EMPLOYEE.OFFICE_ID | type DOUBLE, precision 15, nullable true | [kb:sampleapp::bmm::SAMPLESALES.Dim Sales Rep.src.LTS Employee#map3] |
| Distance | EMP_PARENT_CHILD.DISTANCE | type INT, nullable not stated | [kb:sampleapp::bmm::SAMPLESALES.Dim Sales Rep.src.LTS Employee#map4] |

### Points to note

- The source joins EMPLOYEE to the closure table EMP_PARENT_CHILD on EMP_PARENT_CHILD.ANCESTOR_KEY = EMPLOYEE.EMP_ID, as an inner join [kb:sampleapp::bmm::SAMPLESALES.Dim Sales Rep.src.LTS Employee#table0.join0], and the field "Distance", shown to report authors, comes from the closure table [kb:sampleapp::bmm::SAMPLESALES.Dim Sales Rep.src.LTS Employee#map4]. What one row of this table stands for, and what "Distance" holds, is not recorded (open question Q9).
- The fields "Manager ID" and "Office ID" are mapped from EMPLOYEE.MANAGER_ID and EMPLOYEE.OFFICE_ID [kb:sampleapp::bmm::SAMPLESALES.Dim Sales Rep.src.LTS Employee#map2] [kb:sampleapp::bmm::SAMPLESALES.Dim Sales Rep.src.LTS Employee#map3].

## Dim Time

"Dim Time" is a business table of the business model "SAMPLESALES"; the repository declares 2 sources for it [kb:sampleapp::bmm::SAMPLESALES.Dim Time]. In this subject area it has 6 fields and no measures; all 6 are shown to report authors (counted; no Row identifier). Its two sources are "LTS Time Day" and "LTS Time Month" (counted; no Row identifier).

### Keys

| Key | Primary key (as recorded) | Fields | Evidence |
|---|---|---|---|
| SAMP_TIME_DAY_D_Key | true | Calendar Date | [kb:sampleapp::bmm::SAMPLESALES.Dim Time.key.SAMP_TIME_DAY_D_Key] |

### Measures

This table has no measures.

### Fields

All fields below are shown to report authors in the table "Dim Time". For each, the name shown to users is not a fixed text but a formula, given in the second column exactly as written.

| Field | Name shown to users, as written | In sources | Evidence |
|---|---|---|---|
| Calendar Date | `VALUEOF(NQ_SESSION.CN_Sample_Sales_Dim_Time_Calendar_Date)` | 1 of 2: "LTS Time Day" | [kb:sampleapp::bmm::SAMPLESALES.Dim Time.Calendar Date] [kb:sampleapp::pres::Sample Sales.Dim Time.Calendar Date] |
| Per Name Half | `VALUEOF(NQ_SESSION.CN_Sample_Sales_Dim_Time_Per_Name_Half)` | 2 of 2: "LTS Time Day", "LTS Time Month" | [kb:sampleapp::bmm::SAMPLESALES.Dim Time.Per Name Half] [kb:sampleapp::pres::Sample Sales.Dim Time.Per Name Half] |
| Per Name Month | `VALUEOF(NQ_SESSION.CN_Sample_Sales_Dim_Time_Per_Name_Month)` | 2 of 2: "LTS Time Day", "LTS Time Month" | [kb:sampleapp::bmm::SAMPLESALES.Dim Time.Per Name Month] [kb:sampleapp::pres::Sample Sales.Dim Time.Per Name Month] |
| Per Name Qtr | `VALUEOF(NQ_SESSION.CN_Sample_Sales_Dim_Time_Per_Name_Qtr)` | 2 of 2: "LTS Time Day", "LTS Time Month" | [kb:sampleapp::bmm::SAMPLESALES.Dim Time.Per Name Qtr] [kb:sampleapp::pres::Sample Sales.Dim Time.Per Name Qtr] |
| Per Name Week | `VALUEOF(NQ_SESSION.CN_Sample_Sales_Dim_Time_Per_Name_Week)` | 1 of 2: "LTS Time Day" | [kb:sampleapp::bmm::SAMPLESALES.Dim Time.Per Name Week] [kb:sampleapp::pres::Sample Sales.Dim Time.Per Name Week] |
| Per Name Year | `VALUEOF(NQ_SESSION.CN_Sample_Sales_Dim_Time_Per_Name_Year)` | 2 of 2: "LTS Time Day", "LTS Time Month" | [kb:sampleapp::bmm::SAMPLESALES.Dim Time.Per Name Year] [kb:sampleapp::pres::Sample Sales.Dim Time.Per Name Year] |

Each of these formulas names a variable; whether the repository defines it is stated in the section Values set per user or for the whole system. What name report authors actually see is not recorded (open question Q1).

### Measures and fields report authors do not see

This table has no measures or fields that report authors do not see.

### Where the data comes from

Neither source has a priority group recorded.

#### Source "LTS Time Day"

Active: true. Priority group not recorded (empty; it does not mean lowest) [kb:sampleapp::bmm::SAMPLESALES.Dim Time.src.LTS Time Day]. Level of detail, exactly: `GROUPBYLEVEL("SAMPLESALES"."Time Hierarchy"."Dim Time Detail")`, the level "Dim Time Detail" of the drill path "Time Hierarchy" [kb:sampleapp::bmm::SAMPLESALES.Dim Time.src.LTS Time Day].

No content filter is recorded for this source.

| Database table | Schema | Type | Evidence |
|---|---|---|---|
| SAMP_TIME_DAY_D (see the section Database table SAMP_TIME_DAY_D) | `xe.SAMPLESALES` | `none` | [kb:sampleapp::bmm::SAMPLESALES.Dim Time.src.LTS Time Day#table0] |

This source reads one table, so it makes no join.

| Field | Database column | Type, precision, nullable | Evidence |
|---|---|---|---|
| Per Name Year | SAMP_TIME_DAY_D.PER_NAME_YEAR | type VARCHAR, precision 4, nullable not stated | [kb:sampleapp::bmm::SAMPLESALES.Dim Time.src.LTS Time Day#map0] |
| Per Name Week | SAMP_TIME_DAY_D.PER_NAME_WEEK | type VARCHAR, precision 12, nullable not stated | [kb:sampleapp::bmm::SAMPLESALES.Dim Time.src.LTS Time Day#map1] |
| Per Name Half | SAMP_TIME_DAY_D.PER_NAME_HALF | type VARCHAR, precision 8, nullable not stated | [kb:sampleapp::bmm::SAMPLESALES.Dim Time.src.LTS Time Day#map2] |
| Per Name Month | SAMP_TIME_DAY_D.PER_NAME_MONTH | type VARCHAR, precision 9, nullable not stated | [kb:sampleapp::bmm::SAMPLESALES.Dim Time.src.LTS Time Day#map3] |
| Per Name Qtr | SAMP_TIME_DAY_D.PER_NAME_QTR | type VARCHAR, precision 7, nullable not stated | [kb:sampleapp::bmm::SAMPLESALES.Dim Time.src.LTS Time Day#map4] |
| Calendar Date | SAMP_TIME_DAY_D.CALENDAR_DATE | type DATE, nullable not stated | [kb:sampleapp::bmm::SAMPLESALES.Dim Time.src.LTS Time Day#map5] |

#### Source "LTS Time Month"

Active: true. Priority group not recorded (empty; it does not mean lowest) [kb:sampleapp::bmm::SAMPLESALES.Dim Time.src.LTS Time Month]. Level of detail, exactly: `GROUPBYLEVEL("SAMPLESALES"."Time Hierarchy"."Month")`, the level "Month" of the drill path "Time Hierarchy" [kb:sampleapp::bmm::SAMPLESALES.Dim Time.src.LTS Time Month].

No content filter is recorded for this source.

| Database table | Schema | Type | Evidence |
|---|---|---|---|
| SAMP_TIME_MTH_D (see the section Database table SAMP_TIME_MTH_D) | `xe.SAMPLESALES` | `none` | [kb:sampleapp::bmm::SAMPLESALES.Dim Time.src.LTS Time Month#table0] |

This source reads one table, so it makes no join.

| Field | Database column | Type, precision, nullable | Evidence |
|---|---|---|---|
| Per Name Year | SAMP_TIME_MTH_D.PER_NAME_YEAR | type VARCHAR, precision 4, nullable true | [kb:sampleapp::bmm::SAMPLESALES.Dim Time.src.LTS Time Month#map0] |
| Per Name Half | SAMP_TIME_MTH_D.PER_NAME_HALF | type VARCHAR, precision 8, nullable true | [kb:sampleapp::bmm::SAMPLESALES.Dim Time.src.LTS Time Month#map1] |
| Per Name Month | SAMP_TIME_MTH_D.PER_NAME_MONTH | type VARCHAR, precision 9, nullable true | [kb:sampleapp::bmm::SAMPLESALES.Dim Time.src.LTS Time Month#map2] |
| Per Name Qtr | SAMP_TIME_MTH_D.PER_NAME_QTR | type VARCHAR, precision 7, nullable true | [kb:sampleapp::bmm::SAMPLESALES.Dim Time.src.LTS Time Month#map3] |

### Points to note

- The primary key is "Calendar Date" [kb:sampleapp::bmm::SAMPLESALES.Dim Time.key.SAMP_TIME_DAY_D_Key], which only the source "LTS Time Day" maps; "LTS Time Month" maps neither "Calendar Date" nor "Per Name Week" [kb:sampleapp::bmm::SAMPLESALES.Dim Time.src.LTS Time Month].
- The name shown to users of every field of this table is a VALUEOF formula that names a variable, for example `VALUEOF(NQ_SESSION.CN_Sample_Sales_Dim_Time_Calendar_Date)` [kb:sampleapp::pres::Sample Sales.Dim Time.Calendar Date] (open question Q1).

## Database table EMPLOYEE

EMPLOYEE is a database table in `xe.SAMPLESALES`, type `none` [kb:sampleapp::phys::xe.SAMPLESALES.EMPLOYEE]. It is not an alias of another table, and no description is recorded [kb:sampleapp::phys::xe.SAMPLESALES.EMPLOYEE]. No separate object name in the database is recorded, so the repository knows it only by the name EMPLOYEE (listed in the packet without a Row identifier). The export lists 4 columns for it, all 4 mapped by a field of this subject area and none not mapped, and 1 key (counted; no Row identifier).

### Columns

| # | Column | Type | Precision | Scale | Nullable | External name | Mapped by | Evidence |
|---|---|---|---|---|---|---|---|---|
| 1 | EMP_ID | DOUBLE | 15 | not recorded | not stated | `samplesales` | Dim Sales Rep, Employee ID | [kb:sampleapp::phys::xe.SAMPLESALES.EMPLOYEE.EMP_ID] |
| 2 | EMP_NAME | VARCHAR | 40 | not recorded | true | `samplesales` | Dim Sales Rep, Employee Name | [kb:sampleapp::phys::xe.SAMPLESALES.EMPLOYEE.EMP_NAME] |
| 3 | MANAGER_ID | DOUBLE | 15 | not recorded | true | `samplesales` | Dim Sales Rep, Manager ID | [kb:sampleapp::phys::xe.SAMPLESALES.EMPLOYEE.MANAGER_ID] |
| 4 | OFFICE_ID | DOUBLE | 15 | not recorded | true | `samplesales` | Dim Sales Rep, Office ID | [kb:sampleapp::phys::xe.SAMPLESALES.EMPLOYEE.OFFICE_ID] |

Nullable: the export records this only when it is true. "Not stated" means the export does not carry it; it is not a statement that the column must be filled. "Not recorded" means the repository says nothing; it does not mean no, and it does not mean 0.

Every column carries the same external name, `samplesales`, as printed in the packet.

### Keys

| Key | Columns | Evidence |
|---|---|---|
| EMP_ID | EMP_ID | [kb:sampleapp::phys::xe.SAMPLESALES.EMPLOYEE.key.EMP_ID] |

This is key 1 of 1 the repository declares for the table. The repository does not say whether a physical key is the primary key; this is the only key it declares for the table. One foreign key of this subject area points at exactly this column [kb:sampleapp::phys::xe.SAMPLESALES.EMPLOYEE.key.EMP_ID].

### Foreign keys and joins

- A complex join "Complex Join#2" between FactCrossJoin and EMPLOYEE; its condition is stated in full in the section How the tables relate.
- The foreign key "EMP_PARENT_CHILD_FKey" from EMP_PARENT_CHILD (column ANCESTOR_KEY) to this table (column EMP_ID); stated in full in the section How the tables relate.

### Read by

The source "LTS Employee" of the business table Dim Sales Rep (listed in the packet without a Row identifier).

## Database table EMP_PARENT_CHILD

EMP_PARENT_CHILD is a database table in `xe.SAMPLESALES`, type `none`; it is not an alias of another table, and no description is recorded [kb:sampleapp::phys::xe.SAMPLESALES.EMP_PARENT_CHILD]. Its object in the database, for database type `DefaultMulDB`, is `EMP_PARENT_CHILD` [kb:sampleapp::phys::xe.SAMPLESALES.EMP_PARENT_CHILD.def.DefaultMulDB]. The export lists 4 columns for it, 1 mapped by a field of this subject area and 3 not, and 1 key (counted; no Row identifier).

It is the relationship table of a parent-child hierarchy, with one row per member and ancestor [kb:sampleapp::phys::xe.SAMPLESALES.EMP_PARENT_CHILD]:

| Role | Column | Evidence |
|---|---|---|
| Member | MEMBER_KEY | [kb:sampleapp::phys::xe.SAMPLESALES.EMP_PARENT_CHILD] |
| Parent (ancestor) | ANCESTOR_KEY | [kb:sampleapp::phys::xe.SAMPLESALES.EMP_PARENT_CHILD] |
| Distance (steps between the two) | DISTANCE | [kb:sampleapp::phys::xe.SAMPLESALES.EMP_PARENT_CHILD] |
| Leaf flag | IS_LEAF | [kb:sampleapp::phys::xe.SAMPLESALES.EMP_PARENT_CHILD] |

### Columns

| # | Column | Type | Precision | Scale | Nullable | External name | Mapped by | Evidence |
|---|---|---|---|---|---|---|---|---|
| 1 | MEMBER_KEY | DOUBLE | 15 | not recorded | not stated | not recorded | not mapped | [kb:sampleapp::phys::xe.SAMPLESALES.EMP_PARENT_CHILD.MEMBER_KEY] |
| 2 | ANCESTOR_KEY | DOUBLE | 15 | not recorded | not stated | not recorded | not mapped | [kb:sampleapp::phys::xe.SAMPLESALES.EMP_PARENT_CHILD.ANCESTOR_KEY] |
| 3 | DISTANCE | INT | not recorded | not recorded | not stated | not recorded | Dim Sales Rep, Distance | [kb:sampleapp::phys::xe.SAMPLESALES.EMP_PARENT_CHILD.DISTANCE] |
| 4 | IS_LEAF | INT | not recorded | not recorded | not stated | not recorded | not mapped | [kb:sampleapp::phys::xe.SAMPLESALES.EMP_PARENT_CHILD.IS_LEAF] |

Nullable: the export records this only when it is true. "Not stated" means the export does not carry it; it is not a statement that the column must be filled. "Not recorded" means the repository says nothing; it does not mean no, and it does not mean 0.

### Keys

| Key | Columns | Evidence |
|---|---|---|
| EMP_PARENT_CHILD_Key | MEMBER_KEY | [kb:sampleapp::phys::xe.SAMPLESALES.EMP_PARENT_CHILD.key.EMP_PARENT_CHILD_Key] |

This is key 1 of 1 the repository declares for the table. The repository does not say whether a physical key is the primary key; this is the only key it declares for the table. One foreign key of this subject area points at exactly this column [kb:sampleapp::phys::xe.SAMPLESALES.EMP_PARENT_CHILD.key.EMP_PARENT_CHILD_Key]. Whether MEMBER_KEY holds one row per value in this table is not recorded (open question Q14).

### Foreign keys and joins

- The foreign key "EMP_PARENT_CHILD_FKey" from this table (column ANCESTOR_KEY) to EMPLOYEE (column EMP_ID); stated in full in the section How the tables relate.
- The foreign key "SAMP_REVENUE_F_FKey#4" from SAMP_REVENUE_F (column EMPLOYEE_KEY) to this table (column MEMBER_KEY); stated in full in the section How the tables relate.

### Read by

The source "LTS Employee" of the business table Dim Sales Rep (listed in the packet without a Row identifier).

## Database table FactCrossJoin

FactCrossJoin is listed in `xe.SAMPLESALES` with type `select`: it is not a database table but a query kept in the repository [kb:sampleapp::phys::xe.SAMPLESALES.FactCrossJoin]. It is not an alias of another table, and no description is recorded [kb:sampleapp::phys::xe.SAMPLESALES.FactCrossJoin]. Its SQL, for database type `DefaultMulDB`, is exactly `select 1 Preserve from dual` [kb:sampleapp::phys::xe.SAMPLESALES.FactCrossJoin.def.DefaultMulDB]. Put mechanically, the query selects the number 1 under the name Preserve from the table dual. The export lists 1 column for it, mapped by a field of this subject area, and no key (counted; no Row identifier).

### Columns

| # | Column | Type | Precision | Scale | Nullable | External name | Mapped by | Evidence |
|---|---|---|---|---|---|---|---|---|
| 1 | Preserve | DOUBLE | not recorded | not recorded | not stated | not recorded | Fact Revenue, Preserve | [kb:sampleapp::phys::xe.SAMPLESALES.FactCrossJoin.Preserve] |

Nullable: the export records this only when it is true. "Not stated" means the export does not carry it; it is not a statement that the column must be filled. "Not recorded" means the repository says nothing; it does not mean no, and it does not mean 0.

### Keys

Keys of FactCrossJoin: not recorded. The repository declares no key for it, so nothing in the repository says which column(s) identify a row (listed in the packet without a Row identifier).

### Foreign keys and joins

- A complex join "Complex Join" between FactCrossJoin and SAMP_OFFICES_D; its condition is stated in full in the section How the tables relate.
- A complex join "Complex Join#1" between FactCrossJoin and SAMP_PRODUCTS_D; its condition is stated in full in the section How the tables relate.
- A complex join "Complex Join#2" between FactCrossJoin and EMPLOYEE; its condition is stated in full in the section How the tables relate.

### Read by

The source "FactCrossJoin" of the business table Fact Revenue (listed in the packet without a Row identifier). Why the measure "Preserve" is built on this query is not recorded (open question Q6).

## Database table SAMP_OFFICES_D

SAMP_OFFICES_D is a database table in `xe.SAMPLESALES`, type `none`; it is not an alias of another table, and no description is recorded [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_OFFICES_D]. No separate object name in the database is recorded, so the repository knows it only by the name SAMP_OFFICES_D (listed in the packet without a Row identifier). The export lists 10 columns for it, all 10 mapped by a field of this subject area and none not mapped, and 1 key (counted; no Row identifier).

### Columns

| # | Column | Type | Precision | Scale | Nullable | External name | Mapped by | Evidence |
|---|---|---|---|---|---|---|---|---|
| 1 | COMPANY | VARCHAR | 14 | not recorded | true | `samplesales` | Dim Offices, Company | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_OFFICES_D.COMPANY] |
| 2 | COMPANY_KEY | DOUBLE | 15 | not recorded | not stated | `samplesales` | Dim Offices, Company Key | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_OFFICES_D.COMPANY_KEY] |
| 3 | DEPARTMENT | VARCHAR | 25 | not recorded | true | `samplesales` | Dim Offices, Department | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_OFFICES_D.DEPARTMENT] |
| 4 | DEPT_KEY | DOUBLE | 15 | not recorded | not stated | `samplesales` | Dim Offices, Dept Key | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_OFFICES_D.DEPT_KEY] |
| 5 | OFFICE_DSC | VARCHAR | 17 | not recorded | true | `samplesales` | Dim Offices, Office Dsc | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_OFFICES_D.OFFICE_DSC] |
| 6 | OFFICE_KEY | DOUBLE | 15 | not recorded | not stated | `samplesales` | Dim Offices, Office Key | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_OFFICES_D.OFFICE_KEY] |
| 7 | ORGANIZATION | VARCHAR | 18 | not recorded | true | `samplesales` | Dim Offices, Organization | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_OFFICES_D.ORGANIZATION] |
| 8 | ORG_KEY | DOUBLE | 15 | not recorded | not stated | `samplesales` | Dim Offices, Org Key | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_OFFICES_D.ORG_KEY] |
| 9 | SEQUENCE | DOUBLE | 15 | not recorded | true | `samplesales` | Dim Offices, Sequence | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_OFFICES_D.SEQUENCE] |
| 10 | AREA_NAME | VARCHAR | 25 | not recorded | true | not recorded | Dim Offices, Area Name | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_OFFICES_D.AREA_NAME] |

Nullable: the export records this only when it is true. "Not stated" means the export does not carry it; it is not a statement that the column must be filled. "Not recorded" means the repository says nothing; it does not mean no, and it does not mean 0.

### Keys

| Key | Columns | Evidence |
|---|---|---|
| SAMP_OFFICES_D_Key | OFFICE_KEY | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_OFFICES_D.key.SAMP_OFFICES_D_Key] |

This is key 1 of 1 the repository declares for the table. The repository does not say whether a physical key is the primary key; this is the only key it declares for the table. Two foreign keys of this subject area point at exactly this column [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_OFFICES_D.key.SAMP_OFFICES_D_Key].

### Foreign keys and joins

- A complex join "Complex Join" between FactCrossJoin and SAMP_OFFICES_D; its condition is stated in full in the section How the tables relate.
- The foreign key "SAMP_REVENUE_F_FKey#1" from SAMP_REVENUE_F (column OFFICE_KEY) to this table (column OFFICE_KEY); stated in full in the section How the tables relate.
- The foreign key "SAMP_TARGETS_F_FKey#1" from SAMP_TARGETS_F (column OFFICE_KEY) to this table (column OFFICE_KEY); stated in full in the section How the tables relate.

### Read by

The source "SAMP_OFFICES_D" of the business table Dim Offices (listed in the packet without a Row identifier).

## Database table SAMP_PRODUCTS_D

SAMP_PRODUCTS_D is a database table in `xe.SAMPLESALES`, type `none`; it is not an alias of another table, and no description is recorded [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_PRODUCTS_D]. No separate object name in the database is recorded, so the repository knows it only by the name SAMP_PRODUCTS_D (listed in the packet without a Row identifier). The export lists 11 columns for it, all 11 mapped by a field of this subject area and none not mapped, and 1 key (counted; no Row identifier).

### Columns

| # | Column | Type | Precision | Scale | Nullable | External name | Mapped by | Evidence |
|---|---|---|---|---|---|---|---|---|
| 1 | ATTRIBUTE_1 | VARCHAR | 7 | not recorded | true | `samplesales` | Dim Products, Attribute 1 | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_PRODUCTS_D.ATTRIBUTE_1] |
| 2 | ATTRIBUTE_2 | VARCHAR | 6 | not recorded | true | `samplesales` | Dim Products, Attribute 2 | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_PRODUCTS_D.ATTRIBUTE_2] |
| 3 | BRAND | VARCHAR | 8 | not recorded | true | `samplesales` | Dim Products, Brand | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_PRODUCTS_D.BRAND] |
| 4 | BRAND_KEY | DOUBLE | 15 | not recorded | not stated | `samplesales` | Dim Products, Brand Key | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_PRODUCTS_D.BRAND_KEY] |
| 5 | LOB | VARCHAR | 13 | not recorded | true | `samplesales` | Dim Products, Lob | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_PRODUCTS_D.LOB] |
| 6 | LOB_KEY | DOUBLE | 15 | not recorded | not stated | `samplesales` | Dim Products, Lob Key | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_PRODUCTS_D.LOB_KEY] |
| 7 | PROD_DSC | VARCHAR | 26 | not recorded | true | `samplesales` | Dim Products, Prod Dsc | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_PRODUCTS_D.PROD_DSC] |
| 8 | PROD_KEY | DOUBLE | 15 | not recorded | not stated | `samplesales` | Dim Products, Prod Key | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_PRODUCTS_D.PROD_KEY] |
| 9 | SEQUENCE | DOUBLE | 15 | not recorded | not stated | `samplesales` | Dim Products, Sequence | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_PRODUCTS_D.SEQUENCE] |
| 10 | TYPE | VARCHAR | 12 | not recorded | true | `samplesales` | Dim Products, Type | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_PRODUCTS_D.TYPE] |
| 11 | TYPE_KEY | DOUBLE | 15 | not recorded | not stated | `samplesales` | Dim Products, Type Key | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_PRODUCTS_D.TYPE_KEY] |

Nullable: the export records this only when it is true. "Not stated" means the export does not carry it; it is not a statement that the column must be filled. "Not recorded" means the repository says nothing; it does not mean no, and it does not mean 0.

### Keys

| Key | Columns | Evidence |
|---|---|---|
| SAMP_PRODUCTS_D_Key | PROD_KEY | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_PRODUCTS_D.key.SAMP_PRODUCTS_D_Key] |

This is key 1 of 1 the repository declares for the table. The repository does not say whether a physical key is the primary key; this is the only key it declares for the table. Four foreign keys of this subject area point at exactly this column [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_PRODUCTS_D.key.SAMP_PRODUCTS_D_Key].

### Foreign keys and joins

- A complex join "Complex Join#1" between FactCrossJoin and SAMP_PRODUCTS_D; its condition is stated in full in the section How the tables relate.
- The foreign key "SAMP_PRODUCTS_D_TRANSLATION_FKey" from SAMP_PRODUCTS_D_TRANSLATION (column PROD_KEY) to this table (column PROD_KEY); stated in full in the section How the tables relate.
- The foreign key "SAMP_REVENUE_A_FKey#1" from SAMP_REVENUE_A (column PROD_KEY) to this table (column PROD_KEY); stated in full in the section How the tables relate.
- The foreign key "SAMP_REVENUE_F_FKey#2" from SAMP_REVENUE_F (column PROD_KEY) to this table (column PROD_KEY); stated in full in the section How the tables relate.
- The foreign key "SAMP_TARGETS_F_FKey#2" from SAMP_TARGETS_F (column PROD_KEY) to this table (column PROD_KEY); stated in full in the section How the tables relate.

### Read by

The source "SAMP_PRODUCTS_D" of the business table Dim Products (listed in the packet without a Row identifier).

## Database table SAMP_PRODUCTS_D_TRANSLATION

SAMP_PRODUCTS_D_TRANSLATION is a database table in `xe.SAMPLESALES`, type `none`; it is not an alias of another table, and no description is recorded [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_PRODUCTS_D_TRANSLATION]. Its object in the database, for database type `DefaultMulDB`, is `SAMP_PRODUCTS_D_TRANSLATION` [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_PRODUCTS_D_TRANSLATION.def.DefaultMulDB]. The export lists 3 columns for it, all 3 mapped by a field of this subject area and none not mapped, and 1 key (counted; no Row identifier).

### Columns

| # | Column | Type | Precision | Scale | Nullable | External name | Mapped by | Evidence |
|---|---|---|---|---|---|---|---|---|
| 1 | LANG_ID | VARCHAR | 2 | not recorded | not stated | `samplesales` | Dim Products (Translated), LANG_ID | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_PRODUCTS_D_TRANSLATION.LANG_ID] |
| 2 | PROD_DESC | VARCHAR | 50 | not recorded | not stated | `samplesales` | Dim Products (Translated), PROD_DESC | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_PRODUCTS_D_TRANSLATION.PROD_DESC] |
| 3 | PROD_KEY | DOUBLE | 15 | not recorded | not stated | `samplesales` | Dim Products (Translated), PROD_KEY | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_PRODUCTS_D_TRANSLATION.PROD_KEY] |

Nullable: the export records this only when it is true. "Not stated" means the export does not carry it; it is not a statement that the column must be filled. "Not recorded" means the repository says nothing; it does not mean no, and it does not mean 0.

### Keys

| Key | Columns | Evidence |
|---|---|---|
| PK_PROD_TRANS1 | LANG_ID, PROD_KEY | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_PRODUCTS_D_TRANSLATION.key.PK_PROD_TRANS1] |

This is key 1 of 1 the repository declares for the table. The repository does not say whether a physical key is the primary key; this is the only key it declares for the table. No foreign key of this subject area points at exactly these columns [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_PRODUCTS_D_TRANSLATION.key.PK_PROD_TRANS1].

### Foreign keys and joins

- The foreign key "SAMP_PRODUCTS_D_TRANSLATION_FKey" from this table (column PROD_KEY) to SAMP_PRODUCTS_D (column PROD_KEY); stated in full in the section How the tables relate.

### Read by

The source "SAMP_PRODUCTS_D_TRANSLATION" of the business table Dim Products (Translated) (listed in the packet without a Row identifier).

## Database table SAMP_REVENUE_A

SAMP_REVENUE_A is a database table in `xe.SAMPLESALES`, type `none`; it is not an alias of another table, and no description is recorded [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A]. Its object in the database, for database type `DefaultMulDB`, is `SAMP_REVENUE_A` [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A.def.DefaultMulDB]. The export lists 5 columns for it, all 5 mapped by a field of this subject area and none not mapped, and no key (counted; no Row identifier).

### Columns

| # | Column | Type | Precision | Scale | Nullable | External name | Mapped by | Evidence |
|---|---|---|---|---|---|---|---|---|
| 1 | BILL_MTH_KEY | DOUBLE | 15 | not recorded | true | `samplesales` | Fact Revenue, Bill Mth Key | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A.BILL_MTH_KEY] |
| 2 | DISCNT_VALUE | DOUBLE | 7 | 2 | true | `samplesales` | Fact Revenue, Discnt Value | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A.DISCNT_VALUE] |
| 3 | PROD_KEY | DOUBLE | 15 | not recorded | true | `samplesales` | Fact Revenue, Prod Key | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A.PROD_KEY] |
| 4 | REVENUE | DOUBLE | 8 | 2 | true | `samplesales` | Fact Revenue: Revenue, All Products Revenue, All Months Revenue, Revenue (GBP), Revenue (EUR), Revenue (INR) | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A.REVENUE] |
| 5 | UNITS | DOUBLE | 15 | not recorded | true | `samplesales` | Fact Revenue, Units | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A.UNITS] |

Nullable: the export records this only when it is true. "Not stated" means the export does not carry it; it is not a statement that the column must be filled. "Not recorded" means the repository says nothing; it does not mean no, and it does not mean 0.

The one column REVENUE is mapped by six fields of Fact Revenue [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A.REVENUE] (open questions Q3 and Q4).

### Keys

Keys of SAMP_REVENUE_A: not recorded. The repository declares no key for it, so nothing in the repository says which column(s) identify a row (listed in the packet without a Row identifier).

### Foreign keys and joins

- The foreign key "SAMP_REVENUE_A_FKey" from this table (column BILL_MTH_KEY) to SAMP_TIME_MTH_D (column MTH_KEY); stated in full in the section How the tables relate.
- The foreign key "SAMP_REVENUE_A_FKey#1" from this table (column PROD_KEY) to SAMP_PRODUCTS_D (column PROD_KEY); stated in full in the section How the tables relate.

### Read by

The source "Aggregate Revenue" of the business table Fact Revenue (listed in the packet without a Row identifier).

## Database table SAMP_REVENUE_A_MTH

SAMP_REVENUE_A_MTH is a database table in `xe.SAMPLESALES`, type `none`; it is not an alias of another table, and no description is recorded [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A_MTH]. No separate object name in the database is recorded, so the repository knows it only by the name SAMP_REVENUE_A_MTH (listed in the packet without a Row identifier). The export lists 4 columns for it, all 4 mapped by a field of this subject area and none not mapped, and no key (counted; no Row identifier).

### Columns

| # | Column | Type | Precision | Scale | Nullable | External name | Mapped by | Evidence |
|---|---|---|---|---|---|---|---|---|
| 1 | BILL_MTH_KEY | DOUBLE | 22 | not recorded | true | `samplesales` | Fact Revenue, BILL_MTH_KEY | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A_MTH.BILL_MTH_KEY] |
| 2 | DISCNT_VALUE | DOUBLE | 22 | not recorded | true | `samplesales` | Fact Revenue, DISCNT_VALUE | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A_MTH.DISCNT_VALUE] |
| 3 | REVENUE | DOUBLE | 22 | not recorded | true | `samplesales` | Fact Revenue, Revenue | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A_MTH.REVENUE] |
| 4 | UNITS | DOUBLE | 22 | not recorded | true | `samplesales` | Fact Revenue, Units | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A_MTH.UNITS] |

Nullable: the export records this only when it is true. "Not stated" means the export does not carry it; it is not a statement that the column must be filled. "Not recorded" means the repository says nothing; it does not mean no, and it does not mean 0.

### Keys

Keys of SAMP_REVENUE_A_MTH: not recorded. The repository declares no key for it, so nothing in the repository says which column(s) identify a row (listed in the packet without a Row identifier).

### Foreign keys and joins

- The foreign key "SAMP_REVENUE_A_MTH_Foreign Key" from this table (column BILL_MTH_KEY) to SAMP_TIME_MTH_D (column MTH_KEY); stated in full in the section How the tables relate.

### Read by

The source "Aggregate Revenue By Month" of the business table Fact Revenue (listed in the packet without a Row identifier).

## Database table SAMP_REVENUE_A_YR

SAMP_REVENUE_A_YR is a database table in `xe.SAMPLESALES`, type `none`; it is not an alias of another table, and no description is recorded [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A_YR]. Its object in the database, for database type `DefaultMulDB`, is `SAMP_REVENUE_A_YR` [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A_YR.def.DefaultMulDB]. The export lists 4 columns for it, all 4 mapped by a field of this subject area and none not mapped, and no key (counted; no Row identifier).

### Columns

| # | Column | Type | Precision | Scale | Nullable | External name | Mapped by | Evidence |
|---|---|---|---|---|---|---|---|---|
| 1 | BILL_YEAR_KEY | DOUBLE | not recorded | not recorded | true | not recorded | Fact Revenue, BILL_YEAR_KEY | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A_YR.BILL_YEAR_KEY] |
| 2 | DISCNT_VALUE | DOUBLE | 22 | not recorded | true | `samplesales` | Fact Revenue, DISCNT_VALUE | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A_YR.DISCNT_VALUE] |
| 3 | REVENUE | DOUBLE | 22 | not recorded | true | `samplesales` | Fact Revenue, Revenue | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A_YR.REVENUE] |
| 4 | UNITS | DOUBLE | 22 | not recorded | true | `samplesales` | Fact Revenue, Units | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A_YR.UNITS] |

Nullable: the export records this only when it is true. "Not stated" means the export does not carry it; it is not a statement that the column must be filled. "Not recorded" means the repository says nothing; it does not mean no, and it does not mean 0.

### Keys

Keys of SAMP_REVENUE_A_YR: not recorded. The repository declares no key for it, so nothing in the repository says which column(s) identify a row (listed in the packet without a Row identifier).

### Foreign keys and joins

- The foreign key "SAMP_REVENUE_A_YR_Foreign Key" from this table (column BILL_YEAR_KEY) to SAMP_TIME_MTH_D (column CAL_YEAR); stated in full in the section How the tables relate.

### Read by

The source "Aggregate Revenue by Year" of the business table Fact Revenue (listed in the packet without a Row identifier).

## Database table SAMP_REVENUE_F

SAMP_REVENUE_F is a database table in `xe.SAMPLESALES`, type `none`; it is not an alias of another table, and no description is recorded [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F]. Its object in the database, for database type `DefaultMulDB`, is `SAMP_REVENUE_F` [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F.def.DefaultMulDB]. The export lists 12 columns for it, all 12 mapped by a field of this subject area and none not mapped, and no key (counted; no Row identifier).

### Columns

| # | Column | Type | Precision | Scale | Nullable | External name | Mapped by | Evidence |
|---|---|---|---|---|---|---|---|---|
| 1 | BILL_DAY_DT | DATE | not recorded | not recorded | true | not recorded | Fact Revenue, Bill Day Dt | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F.BILL_DAY_DT] |
| 2 | BILL_MTH_KEY | DOUBLE | 15 | not recorded | true | `samplesales` | Fact Revenue, Bill Mth Key | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F.BILL_MTH_KEY] |
| 3 | DISCNT_VALUE | DOUBLE | 6 | 2 | true | `samplesales` | Fact Revenue, Discnt Value | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F.DISCNT_VALUE] |
| 4 | OFFICE_KEY | DOUBLE | 15 | not recorded | true | `samplesales` | Fact Revenue, Office Key | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F.OFFICE_KEY] |
| 5 | ORDER_DAY_DT | DATETIME | not recorded | not recorded | true | `samplesales` | Fact Revenue, Order Day Dt | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F.ORDER_DAY_DT] |
| 6 | ORDER_NUMBER | DOUBLE | 15 | not recorded | true | `samplesales` | Fact Revenue, Order Number | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F.ORDER_NUMBER] |
| 7 | ORDER_STATUS | VARCHAR | 11 | not recorded | true | `samplesales` | Fact Revenue, Order Status | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F.ORDER_STATUS] |
| 8 | ORDER_TYPE | VARCHAR | 8 | not recorded | true | `samplesales` | Fact Revenue, Order Type | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F.ORDER_TYPE] |
| 9 | PROD_KEY | DOUBLE | 15 | not recorded | true | `samplesales` | Fact Revenue, Prod Key | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F.PROD_KEY] |
| 10 | REVENUE | DOUBLE | 7 | 2 | true | `samplesales` | Fact Revenue: All Products Revenue, All Months Revenue, Revenue (GBP), Revenue (EUR), Revenue (INR), Revenue | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F.REVENUE] |
| 11 | UNITS | DOUBLE | 15 | not recorded | true | `samplesales` | Fact Revenue, Units | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F.UNITS] |
| 12 | EMPLOYEE_KEY | INT | not recorded | not recorded | not stated | not recorded | Fact Revenue, Sales Employee Key | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F.EMPLOYEE_KEY] |

Nullable: the export records this only when it is true. "Not stated" means the export does not carry it; it is not a statement that the column must be filled. "Not recorded" means the repository says nothing; it does not mean no, and it does not mean 0.

The one column REVENUE is mapped by six fields of Fact Revenue [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F.REVENUE]; how each is mapped is in the section Fact Revenue (open questions Q3 and Q4).

### Keys

Keys of SAMP_REVENUE_F: not recorded. The repository declares no key for it, so nothing in the repository says which column(s) identify a row (listed in the packet without a Row identifier).

### Foreign keys and joins

- The foreign key "SAMP_REVENUE_F_FKey" from this table (column BILL_MTH_KEY) to SAMP_TIME_MTH_D (column MTH_KEY); stated in full in the section How the tables relate.
- The foreign key "SAMP_REVENUE_F_FKey#1" from this table (column OFFICE_KEY) to SAMP_OFFICES_D (column OFFICE_KEY); stated in full in the section How the tables relate.
- The foreign key "SAMP_REVENUE_F_FKey#2" from this table (column PROD_KEY) to SAMP_PRODUCTS_D (column PROD_KEY); stated in full in the section How the tables relate.
- The foreign key "SAMP_REVENUE_F_FKey#3" from this table (column BILL_DAY_DT) to SAMP_TIME_DAY_D (column CALENDAR_DATE); stated in full in the section How the tables relate.
- The foreign key "SAMP_REVENUE_F_FKey#4" from this table (column EMPLOYEE_KEY) to EMP_PARENT_CHILD (column MEMBER_KEY); stated in full in the section How the tables relate.

### Read by

The source "SAMP_REVENUE_F" of the business table Fact Revenue (listed in the packet without a Row identifier).

## Database table SAMP_TARGETS_F

SAMP_TARGETS_F is a database table in `xe.SAMPLESALES`, type `none`; it is not an alias of another table, and no description is recorded [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TARGETS_F]. Its object in the database, for database type `DefaultMulDB`, is `SAMP_TARGETS_F` [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TARGETS_F.def.DefaultMulDB]. The export lists 5 columns for it, all 5 mapped by a field of this subject area and none not mapped, and no key (counted; no Row identifier).

### Columns

| # | Column | Type | Precision | Scale | Nullable | External name | Mapped by | Evidence |
|---|---|---|---|---|---|---|---|---|
| 1 | BILL_MTH_KEY | DOUBLE | 15 | not recorded | true | `samplesales` | Fact Target, Bill Mth Key | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TARGETS_F.BILL_MTH_KEY] |
| 2 | OFFICE_KEY | DOUBLE | 15 | not recorded | true | `samplesales` | Fact Target, Office Key | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TARGETS_F.OFFICE_KEY] |
| 3 | PROD_KEY | DOUBLE | 15 | not recorded | true | `samplesales` | Fact Target, Prod Key | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TARGETS_F.PROD_KEY] |
| 4 | REVENUE | DOUBLE | 8 | 2 | true | `samplesales` | Fact Target, Revenue | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TARGETS_F.REVENUE] |
| 5 | UNITS | DOUBLE | 15 | not recorded | true | `samplesales` | Fact Target, Units | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TARGETS_F.UNITS] |

Nullable: the export records this only when it is true. "Not stated" means the export does not carry it; it is not a statement that the column must be filled. "Not recorded" means the repository says nothing; it does not mean no, and it does not mean 0.

### Keys

Keys of SAMP_TARGETS_F: not recorded. The repository declares no key for it, so nothing in the repository says which column(s) identify a row (listed in the packet without a Row identifier).

### Foreign keys and joins

- The foreign key "SAMP_TARGETS_F_FKey" from this table (column BILL_MTH_KEY) to SAMP_TIME_MTH_D (column MTH_KEY); stated in full in the section How the tables relate.
- The foreign key "SAMP_TARGETS_F_FKey#1" from this table (column OFFICE_KEY) to SAMP_OFFICES_D (column OFFICE_KEY); stated in full in the section How the tables relate.
- The foreign key "SAMP_TARGETS_F_FKey#2" from this table (column PROD_KEY) to SAMP_PRODUCTS_D (column PROD_KEY); stated in full in the section How the tables relate.

### Read by

The source "SAMP_TARGETS_F" of the business table Fact Target (listed in the packet without a Row identifier).

## Database table SAMP_TIME_DAY_D

SAMP_TIME_DAY_D is a database table in `xe.SAMPLESALES`, type `none`; it is not an alias of another table, and no description is recorded [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_DAY_D]. Its object in the database, for database type `DefaultMulDB`, is `SAMP_TIME_DAY_D` [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_DAY_D.def.DefaultMulDB]. The export lists 14 columns for it, 6 mapped by a field of this subject area and 8 not, and 1 key (counted; no Row identifier).

### Columns

| # | Column | Type | Precision | Scale | Nullable | External name | Mapped by | Evidence |
|---|---|---|---|---|---|---|---|---|
| 1 | CALENDAR_DATE | DATE | not recorded | not recorded | not stated | not recorded | Dim Time, Calendar Date | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_DAY_D.CALENDAR_DATE] |
| 2 | DAY_KEY | DATE | not recorded | not recorded | not stated | not recorded | not mapped | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_DAY_D.DAY_KEY] |
| 3 | DAY_OF_MONTH | DOUBLE | 15 | not recorded | not stated | `samplesales` | not mapped | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_DAY_D.DAY_OF_MONTH] |
| 4 | DAY_OF_WEEK | DOUBLE | 15 | not recorded | not stated | `samplesales` | not mapped | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_DAY_D.DAY_OF_WEEK] |
| 5 | DAY_OF_YEAR | DOUBLE | 15 | not recorded | not stated | `samplesales` | not mapped | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_DAY_D.DAY_OF_YEAR] |
| 6 | JULIAN_DAY_NUM | DOUBLE | 15 | not recorded | not stated | `samplesales` | not mapped | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_DAY_D.JULIAN_DAY_NUM] |
| 7 | JULIAN_MONTH_NUM | DOUBLE | 15 | not recorded | not stated | `samplesales` | not mapped | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_DAY_D.JULIAN_MONTH_NUM] |
| 8 | JULIAN_QTR_NUM | DOUBLE | 15 | not recorded | not stated | `samplesales` | not mapped | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_DAY_D.JULIAN_QTR_NUM] |
| 9 | JULIAN_WEEK_NUM | DOUBLE | 15 | not recorded | not stated | `samplesales` | not mapped | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_DAY_D.JULIAN_WEEK_NUM] |
| 10 | PER_NAME_HALF | VARCHAR | 8 | not recorded | not stated | `samplesales` | Dim Time, Per Name Half | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_DAY_D.PER_NAME_HALF] |
| 11 | PER_NAME_MONTH | VARCHAR | 9 | not recorded | not stated | `samplesales` | Dim Time, Per Name Month | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_DAY_D.PER_NAME_MONTH] |
| 12 | PER_NAME_QTR | VARCHAR | 7 | not recorded | not stated | `samplesales` | Dim Time, Per Name Qtr | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_DAY_D.PER_NAME_QTR] |
| 13 | PER_NAME_WEEK | VARCHAR | 12 | not recorded | not stated | `samplesales` | Dim Time, Per Name Week | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_DAY_D.PER_NAME_WEEK] |
| 14 | PER_NAME_YEAR | VARCHAR | 4 | not recorded | not stated | `samplesales` | Dim Time, Per Name Year | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_DAY_D.PER_NAME_YEAR] |

Nullable: the export records this only when it is true. "Not stated" means the export does not carry it; it is not a statement that the column must be filled. "Not recorded" means the repository says nothing; it does not mean no, and it does not mean 0.

### Keys

| Key | Columns | Evidence |
|---|---|---|
| SAMP_TIME_DAY_D_Key | CALENDAR_DATE | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_DAY_D.key.SAMP_TIME_DAY_D_Key] |

This is key 1 of 1 the repository declares for the table. The repository does not say whether a physical key is the primary key; this is the only key it declares for the table. One foreign key of this subject area points at exactly this column [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_DAY_D.key.SAMP_TIME_DAY_D_Key].

### Foreign keys and joins

- The foreign key "SAMP_REVENUE_F_FKey#3" from SAMP_REVENUE_F (column BILL_DAY_DT) to this table (column CALENDAR_DATE); stated in full in the section How the tables relate.

### Read by

The source "LTS Time Day" of the business table Dim Time (listed in the packet without a Row identifier).

## Database table SAMP_TIME_MTH_D

SAMP_TIME_MTH_D is a database table in `xe.SAMPLESALES`, type `none`; it is not an alias of another table, and no description is recorded [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D]. No separate object name in the database is recorded, so the repository knows it only by the name SAMP_TIME_MTH_D (listed in the packet without a Row identifier). The export lists 36 columns for it, 4 mapped by a field of this subject area and 32 not, and 2 keys (counted; no Row identifier).

### Columns

| # | Column | Type | Precision | Scale | Nullable | External name | Mapped by | Evidence |
|---|---|---|---|---|---|---|---|---|
| 1 | BEG_OF_QTR_WID | DOUBLE | 15 | not recorded | true | `samplesales` | not mapped | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.BEG_OF_QTR_WID] |
| 2 | CAL_HALF | DOUBLE | 15 | not recorded | true | `samplesales` | not mapped | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.CAL_HALF] |
| 3 | CAL_HALF_YEAR_END_DATE | DOUBLE | 15 | not recorded | true | `samplesales` | not mapped | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.CAL_HALF_YEAR_END_DATE] |
| 4 | CAL_MONTH | DOUBLE | 15 | not recorded | true | `samplesales` | not mapped | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.CAL_MONTH] |
| 5 | CAL_MONTH_END_DATE | DOUBLE | 15 | not recorded | true | `samplesales` | not mapped | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.CAL_MONTH_END_DATE] |
| 6 | CAL_QTR | DOUBLE | 15 | not recorded | true | `samplesales` | not mapped | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.CAL_QTR] |
| 7 | CAL_QTR_END_DATE | DOUBLE | 15 | not recorded | true | `samplesales` | not mapped | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.CAL_QTR_END_DATE] |
| 8 | CAL_YEAR | DOUBLE | 15 | not recorded | true | `samplesales` | not mapped | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.CAL_YEAR] |
| 9 | CAL_YEAR_END_DATE | DOUBLE | 15 | not recorded | true | `samplesales` | not mapped | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.CAL_YEAR_END_DATE] |
| 10 | DAYS_IN_FSCL_HALF_YEAR | DOUBLE | 15 | not recorded | true | `samplesales` | not mapped | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.DAYS_IN_FSCL_HALF_YEAR] |
| 11 | DAYS_IN_FSCL_MONTH | DOUBLE | 15 | not recorded | true | `samplesales` | not mapped | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.DAYS_IN_FSCL_MONTH] |
| 12 | DAYS_IN_FSCL_QTR | DOUBLE | 15 | not recorded | true | `samplesales` | not mapped | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.DAYS_IN_FSCL_QTR] |
| 13 | DAYS_IN_FSCL_YEAR | DOUBLE | 15 | not recorded | true | `samplesales` | not mapped | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.DAYS_IN_FSCL_YEAR] |
| 14 | DAYS_IN_HALF_YEAR | DOUBLE | 15 | not recorded | true | `samplesales` | not mapped | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.DAYS_IN_HALF_YEAR] |
| 15 | DAYS_IN_MTH | DOUBLE | 15 | not recorded | true | `samplesales` | not mapped | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.DAYS_IN_MTH] |
| 16 | DAYS_IN_QTR | DOUBLE | 15 | not recorded | true | `samplesales` | not mapped | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.DAYS_IN_QTR] |
| 17 | DAYS_IN_YEAR | DOUBLE | 15 | not recorded | true | `samplesales` | not mapped | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.DAYS_IN_YEAR] |
| 18 | FSCL_HALF | DOUBLE | 15 | not recorded | true | `samplesales` | not mapped | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.FSCL_HALF] |
| 19 | FSCL_HALF_YEAR_END_DATE | DOUBLE | 15 | not recorded | true | `samplesales` | not mapped | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.FSCL_HALF_YEAR_END_DATE] |
| 20 | FSCL_MONTH | DOUBLE | 15 | not recorded | true | `samplesales` | not mapped | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.FSCL_MONTH] |
| 21 | FSCL_MONTH_END_DATE | DOUBLE | 15 | not recorded | true | `samplesales` | not mapped | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.FSCL_MONTH_END_DATE] |
| 22 | FSCL_QTR | DOUBLE | 15 | not recorded | true | `samplesales` | not mapped | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.FSCL_QTR] |
| 23 | FSCL_QTR_END_DATE | DOUBLE | 15 | not recorded | true | `samplesales` | not mapped | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.FSCL_QTR_END_DATE] |
| 24 | FSCL_YEAR | DOUBLE | 15 | not recorded | true | `samplesales` | not mapped | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.FSCL_YEAR] |
| 25 | FSCL_YEAR_END_DATE | DOUBLE | 15 | not recorded | true | `samplesales` | not mapped | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.FSCL_YEAR_END_DATE] |
| 26 | JULIAN_MONTH_NUM | DOUBLE | 15 | not recorded | true | `samplesales` | not mapped | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.JULIAN_MONTH_NUM] |
| 27 | JULIAN_QTR_NUM | DOUBLE | 15 | not recorded | true | `samplesales` | not mapped | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.JULIAN_QTR_NUM] |
| 28 | LOADED | DOUBLE | 15 | not recorded | true | `samplesales` | not mapped | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.LOADED] |
| 29 | MONTH_AGO_WID | DOUBLE | 15 | not recorded | true | `samplesales` | not mapped | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.MONTH_AGO_WID] |
| 30 | MTH_KEY | DOUBLE | 15 | not recorded | true | `samplesales` | not mapped | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.MTH_KEY] |
| 31 | PER_NAME_HALF | VARCHAR | 8 | not recorded | true | `samplesales` | Dim Time, Per Name Half | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.PER_NAME_HALF] |
| 32 | PER_NAME_MONTH | VARCHAR | 9 | not recorded | true | `samplesales` | Dim Time, Per Name Month | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.PER_NAME_MONTH] |
| 33 | PER_NAME_QTR | VARCHAR | 7 | not recorded | true | `samplesales` | Dim Time, Per Name Qtr | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.PER_NAME_QTR] |
| 34 | PER_NAME_YEAR | VARCHAR | 4 | not recorded | true | `samplesales` | Dim Time, Per Name Year | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.PER_NAME_YEAR] |
| 35 | QUARTER_AGO_WID | DOUBLE | 15 | not recorded | true | `samplesales` | not mapped | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.QUARTER_AGO_WID] |
| 36 | YEAR_AGO_WID | DOUBLE | 15 | not recorded | true | `samplesales` | not mapped | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.YEAR_AGO_WID] |

Nullable: the export records this only when it is true. "Not stated" means the export does not carry it; it is not a statement that the column must be filled. "Not recorded" means the repository says nothing; it does not mean no, and it does not mean 0.

### Keys

| Key | Columns | Evidence |
|---|---|---|
| SAMP_TIME_MTH_D_Key | MTH_KEY | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.key.SAMP_TIME_MTH_D_Key] |
| SAMP_TIME_MTH_D_Key#1 | CAL_YEAR | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.key.SAMP_TIME_MTH_D_Key#1] |

The repository declares 2 keys for the table and does not say whether a physical key is the primary key. Four foreign keys of this subject area point at exactly MTH_KEY [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.key.SAMP_TIME_MTH_D_Key], and one points at exactly CAL_YEAR [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.key.SAMP_TIME_MTH_D_Key#1]. Whether CAL_YEAR holds one row per value in this table is not recorded (open question Q10).

### Foreign keys and joins

- The foreign key "SAMP_REVENUE_A_FKey" from SAMP_REVENUE_A (column BILL_MTH_KEY) to this table (column MTH_KEY); stated in full in the section How the tables relate.
- The foreign key "SAMP_REVENUE_A_MTH_Foreign Key" from SAMP_REVENUE_A_MTH (column BILL_MTH_KEY) to this table (column MTH_KEY); stated in full in the section How the tables relate.
- The foreign key "SAMP_REVENUE_A_YR_Foreign Key" from SAMP_REVENUE_A_YR (column BILL_YEAR_KEY) to this table (column CAL_YEAR); stated in full in the section How the tables relate.
- The foreign key "SAMP_REVENUE_F_FKey" from SAMP_REVENUE_F (column BILL_MTH_KEY) to this table (column MTH_KEY); stated in full in the section How the tables relate.
- The foreign key "SAMP_TARGETS_F_FKey" from SAMP_TARGETS_F (column BILL_MTH_KEY) to this table (column MTH_KEY); stated in full in the section How the tables relate.

### Read by

The source "LTS Time Month" of the business table Dim Time (listed in the packet without a Row identifier).

## How the tables relate

### Joins between business tables

The business model records 7 joins between business tables. The row counts in brackets are copied as printed.

| From | Rows | To | Rows | Join type | Evidence |
|---|---|---|---|---|---|
| Dim Time | 0..1 | Fact Target | 0..n | Inner | [kb:sampleapp::bmm::joins.Relationship_2004:11061353928600] |
| Dim Time | 0..1 | Fact Revenue | 0..n | Inner | [kb:sampleapp::bmm::joins.Relationship_2004:11091353928666] |
| Dim Sales Rep | 1..1 | Fact Revenue | 0..n | Inner | [kb:sampleapp::bmm::joins.Relationship_2004:17511354089145] |
| Fact Revenue | n..n | Dim Offices | 0..1 | Inner | [kb:sampleapp::bmm::joins.Relationship_2004:2111353851711] |
| Fact Revenue | n..n | Dim Products | 0..1 | Inner | [kb:sampleapp::bmm::joins.Relationship_2004:2141353851711] |
| Fact Target | n..n | Dim Offices | 0..1 | Inner | [kb:sampleapp::bmm::joins.Relationship_2004:2301353851711] |
| Fact Target | n..n | Dim Products | 0..1 | Inner | [kb:sampleapp::bmm::joins.Relationship_2004:2331353851711] |

No join between "Fact Target" and "Dim Sales Rep" is listed, and none for "Dim Products (Translated)" (counted from the rows above).

### Joins between database tables

The packet lists 17 joins between database tables: 3 complex joins and 14 foreign keys (counted).

| Kind | Name | From table (column; type, precision, nullable) | To table (column; type, precision, nullable) | Cardinality | Evidence |
|---|---|---|---|---|---|
| complex join | Complex Join | FactCrossJoin | SAMP_OFFICES_D | `ONE TO ONE` | [kb:sampleapp::phys::joins.Complex Join] |
| complex join | Complex Join#1 | FactCrossJoin | SAMP_PRODUCTS_D | `ONE TO ONE` | [kb:sampleapp::phys::joins.Complex Join#1] |
| complex join | Complex Join#2 | FactCrossJoin | EMPLOYEE | `ONE TO ONE` | [kb:sampleapp::phys::joins.Complex Join#2] |
| foreign key | EMP_PARENT_CHILD_FKey | EMP_PARENT_CHILD (ANCESTOR_KEY; DOUBLE, precision 15, nullable not stated) | EMPLOYEE (EMP_ID; DOUBLE, precision 15, nullable not stated) | not recorded | [kb:sampleapp::phys::xe.SAMPLESALES.EMP_PARENT_CHILD.EMP_PARENT_CHILD_FKey] |
| foreign key | SAMP_PRODUCTS_D_TRANSLATION_FKey | SAMP_PRODUCTS_D_TRANSLATION (PROD_KEY; DOUBLE, precision 15, nullable not stated) | SAMP_PRODUCTS_D (PROD_KEY; DOUBLE, precision 15, nullable not stated) | not recorded | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_PRODUCTS_D_TRANSLATION.SAMP_PRODUCTS_D_TRANSLATION_FKey] |
| foreign key | SAMP_REVENUE_A_FKey | SAMP_REVENUE_A (BILL_MTH_KEY; DOUBLE, precision 15, nullable true) | SAMP_TIME_MTH_D (MTH_KEY; DOUBLE, precision 15, nullable true) | not recorded | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A.SAMP_REVENUE_A_FKey] |
| foreign key | SAMP_REVENUE_A_FKey#1 | SAMP_REVENUE_A (PROD_KEY; DOUBLE, precision 15, nullable true) | SAMP_PRODUCTS_D (PROD_KEY; DOUBLE, precision 15, nullable not stated) | not recorded | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A.SAMP_REVENUE_A_FKey#1] |
| foreign key | SAMP_REVENUE_A_MTH_Foreign Key | SAMP_REVENUE_A_MTH (BILL_MTH_KEY; DOUBLE, precision 22, nullable true) | SAMP_TIME_MTH_D (MTH_KEY; DOUBLE, precision 15, nullable true) | not recorded | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A_MTH.SAMP_REVENUE_A_MTH_Foreign Key] |
| foreign key | SAMP_REVENUE_A_YR_Foreign Key | SAMP_REVENUE_A_YR (BILL_YEAR_KEY; DOUBLE, nullable true) | SAMP_TIME_MTH_D (CAL_YEAR; DOUBLE, precision 15, nullable true) | not recorded | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A_YR.SAMP_REVENUE_A_YR_Foreign Key] |
| foreign key | SAMP_REVENUE_F_FKey | SAMP_REVENUE_F (BILL_MTH_KEY; DOUBLE, precision 15, nullable true) | SAMP_TIME_MTH_D (MTH_KEY; DOUBLE, precision 15, nullable true) | not recorded | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F.SAMP_REVENUE_F_FKey] |
| foreign key | SAMP_REVENUE_F_FKey#1 | SAMP_REVENUE_F (OFFICE_KEY; DOUBLE, precision 15, nullable true) | SAMP_OFFICES_D (OFFICE_KEY; DOUBLE, precision 15, nullable not stated) | not recorded | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F.SAMP_REVENUE_F_FKey#1] |
| foreign key | SAMP_REVENUE_F_FKey#2 | SAMP_REVENUE_F (PROD_KEY; DOUBLE, precision 15, nullable true) | SAMP_PRODUCTS_D (PROD_KEY; DOUBLE, precision 15, nullable not stated) | not recorded | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F.SAMP_REVENUE_F_FKey#2] |
| foreign key | SAMP_REVENUE_F_FKey#3 | SAMP_REVENUE_F (BILL_DAY_DT; DATE, nullable true) | SAMP_TIME_DAY_D (CALENDAR_DATE; DATE, nullable not stated) | not recorded | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F.SAMP_REVENUE_F_FKey#3] |
| foreign key | SAMP_REVENUE_F_FKey#4 | SAMP_REVENUE_F (EMPLOYEE_KEY; INT, nullable not stated) | EMP_PARENT_CHILD (MEMBER_KEY; DOUBLE, precision 15, nullable not stated) | not recorded | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F.SAMP_REVENUE_F_FKey#4] |
| foreign key | SAMP_TARGETS_F_FKey | SAMP_TARGETS_F (BILL_MTH_KEY; DOUBLE, precision 15, nullable true) | SAMP_TIME_MTH_D (MTH_KEY; DOUBLE, precision 15, nullable true) | not recorded | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TARGETS_F.SAMP_TARGETS_F_FKey] |
| foreign key | SAMP_TARGETS_F_FKey#1 | SAMP_TARGETS_F (OFFICE_KEY; DOUBLE, precision 15, nullable true) | SAMP_OFFICES_D (OFFICE_KEY; DOUBLE, precision 15, nullable not stated) | not recorded | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TARGETS_F.SAMP_TARGETS_F_FKey#1] |
| foreign key | SAMP_TARGETS_F_FKey#2 | SAMP_TARGETS_F (PROD_KEY; DOUBLE, precision 15, nullable true) | SAMP_PRODUCTS_D (PROD_KEY; DOUBLE, precision 15, nullable not stated) | not recorded | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TARGETS_F.SAMP_TARGETS_F_FKey#2] |

Each foreign key has one column pair, the two columns named in its row. Nullable: the export records this only when it is true; "not stated" is not a statement that the column must be filled.

The three complex joins each have the join condition `1=1` [kb:sampleapp::phys::joins.Complex Join] [kb:sampleapp::phys::joins.Complex Join#1] [kb:sampleapp::phys::joins.Complex Join#2]. The condition names no column of either table (open question Q6).

### Points to note

- The foreign key SAMP_REVENUE_F_FKey#4 pairs a column of type INT (SAMP_REVENUE_F.EMPLOYEE_KEY) with a column of type DOUBLE (EMP_PARENT_CHILD.MEMBER_KEY) [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F.SAMP_REVENUE_F_FKey#4].
- The foreign key SAMP_REVENUE_A_YR_Foreign Key goes from SAMP_REVENUE_A_YR.BILL_YEAR_KEY to SAMP_TIME_MTH_D.CAL_YEAR, not to MTH_KEY [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A_YR.SAMP_REVENUE_A_YR_Foreign Key] (open question Q10).

## Drill paths

The business model has four drill paths ("dimensions"); each is offered to report authors as a hierarchy.

### Office Hierarchy

A drill path with levels; 5 levels; marked as a time dimension: not recorded [kb:sampleapp::bmm::SAMPLESALES.Office Hierarchy]. Its default root level is "Dim Offices Total" [kb:sampleapp::bmm::SAMPLESALES.Office Hierarchy].

Path: Dim Offices Total, Company, Organization, Department, Office Detail.

| Level | Level above | Level(s) below | Key column | Keys (name; fields; primary and drill-down as recorded) | Member count as recorded | Shown to report authors as | Evidence |
|---|---|---|---|---|---|---|---|
| Dim Offices Total (grand total) | none recorded (a top level) | Company | none recorded | none | 1 | level "Dim Offices Total" | [kb:sampleapp::bmm::SAMPLESALES.Office Hierarchy.Dim Offices Total] [kb:sampleapp::pres::Sample Sales.Dim Offices.Office Hierarchy.Dim Offices Total] |
| Company | Dim Offices Total | Organization | Dim Offices, Company | Company; Company; primary not recorded, drill-down true. Company Key; Company Key; primary true, drill-down not recorded | 1 | level "Company", showing "Company" | [kb:sampleapp::bmm::SAMPLESALES.Office Hierarchy.Company] [kb:sampleapp::bmm::SAMPLESALES.Office Hierarchy.Company.key.Company] [kb:sampleapp::bmm::SAMPLESALES.Office Hierarchy.Company.key.Company Key] [kb:sampleapp::pres::Sample Sales.Dim Offices.Office Hierarchy.Company] [kb:sampleapp::pres::Sample Sales.Dim Offices.Office Hierarchy.Company.0] |
| Organization | Company | Department | Dim Offices, Organization | Org Key; Org Key; primary true, drill-down not recorded. Organization; Organization; primary not recorded, drill-down true | 1 | level "Organization", showing "Organization" | [kb:sampleapp::bmm::SAMPLESALES.Office Hierarchy.Organization] [kb:sampleapp::bmm::SAMPLESALES.Office Hierarchy.Organization.key.Org Key] [kb:sampleapp::bmm::SAMPLESALES.Office Hierarchy.Organization.key.Organization] [kb:sampleapp::pres::Sample Sales.Dim Offices.Office Hierarchy.Organization] [kb:sampleapp::pres::Sample Sales.Dim Offices.Office Hierarchy.Organization.0] |
| Department | Organization | Office Detail | Dim Offices, Department | Department; Department; primary not recorded, drill-down true. Dept Key; Dept Key; primary true, drill-down not recorded | 1 | level "Department", showing "Department" | [kb:sampleapp::bmm::SAMPLESALES.Office Hierarchy.Department] [kb:sampleapp::bmm::SAMPLESALES.Office Hierarchy.Department.key.Department] [kb:sampleapp::bmm::SAMPLESALES.Office Hierarchy.Department.key.Dept Key] [kb:sampleapp::pres::Sample Sales.Dim Offices.Office Hierarchy.Department] [kb:sampleapp::pres::Sample Sales.Dim Offices.Office Hierarchy.Department.0] |
| Office Detail | Department | none recorded (a lowest level) | Dim Offices, Office Dsc | Office Dsc; Office Dsc; primary not recorded, drill-down true. Office Key; Office Key; primary true, drill-down not recorded | 1 | level "Office Detail", showing "Office Dsc" | [kb:sampleapp::bmm::SAMPLESALES.Office Hierarchy.Office Detail] [kb:sampleapp::bmm::SAMPLESALES.Office Hierarchy.Office Detail.key.Office Dsc] [kb:sampleapp::bmm::SAMPLESALES.Office Hierarchy.Office Detail.key.Office Key] [kb:sampleapp::pres::Sample Sales.Dim Offices.Office Hierarchy.Office Detail] [kb:sampleapp::pres::Sample Sales.Dim Offices.Office Hierarchy.Office Detail.0] |

For every key above, chronological key: not recorded. Offered to report authors as the hierarchy "Office Hierarchy" in the table "Dim Offices", with 5 levels [kb:sampleapp::pres::Sample Sales.Dim Offices.Office Hierarchy].

### Products Hierararchy

The name is spelled so in the repository. A drill path with levels; 5 levels; marked as a time dimension: not recorded [kb:sampleapp::bmm::SAMPLESALES.Products Hierararchy]. Its default root level is "Dim Products Total" [kb:sampleapp::bmm::SAMPLESALES.Products Hierararchy].

Path: Dim Products Total, Brand, Line Of Business, Products Type, Product Detail.

| Level | Level above | Level(s) below | Key column | Keys (name; fields; primary and drill-down as recorded) | Member count as recorded | Shown to report authors as | Evidence |
|---|---|---|---|---|---|---|---|
| Dim Products Total (grand total) | none recorded (a top level) | Brand | none recorded | none | 1 | level "Dim Products Total" | [kb:sampleapp::bmm::SAMPLESALES.Products Hierararchy.Dim Products Total] [kb:sampleapp::pres::Sample Sales.Dim Products.Products Hierararchy.Dim Products Total] |
| Brand | Dim Products Total | Line Of Business | Dim Products, Brand | Brand; Brand; primary true, drill-down true | 1 | level "Brand", showing "Brand" | [kb:sampleapp::bmm::SAMPLESALES.Products Hierararchy.Brand] [kb:sampleapp::bmm::SAMPLESALES.Products Hierararchy.Brand.key.Brand] [kb:sampleapp::pres::Sample Sales.Dim Products.Products Hierararchy.Brand] [kb:sampleapp::pres::Sample Sales.Dim Products.Products Hierararchy.Brand.0] |
| Line Of Business | Brand | Products Type | Dim Products, Lob | Lob; Lob; primary true, drill-down true | 1 | level "Line Of Business", showing "Lob" | [kb:sampleapp::bmm::SAMPLESALES.Products Hierararchy.Line Of Business] [kb:sampleapp::bmm::SAMPLESALES.Products Hierararchy.Line Of Business.key.Lob] [kb:sampleapp::pres::Sample Sales.Dim Products.Products Hierararchy.Line Of Business] [kb:sampleapp::pres::Sample Sales.Dim Products.Products Hierararchy.Line Of Business.0] |
| Products Type | Line Of Business | Product Detail | Dim Products, Type | Type; Type; primary true, drill-down true | 1 | level "Products Type", showing "Type" | [kb:sampleapp::bmm::SAMPLESALES.Products Hierararchy.Products Type] [kb:sampleapp::bmm::SAMPLESALES.Products Hierararchy.Products Type.key.Type] [kb:sampleapp::pres::Sample Sales.Dim Products.Products Hierararchy.Products Type] [kb:sampleapp::pres::Sample Sales.Dim Products.Products Hierararchy.Products Type.0] |
| Product Detail | Products Type | none recorded (a lowest level) | Dim Products, Prod Dsc | Dim Products Detail_Key; Prod Dsc; primary true, drill-down true. Dim Products Detail_Key1; Prod Key; primary not recorded, drill-down not recorded | 1 | level "Product Detail", showing "Prod Dsc" | [kb:sampleapp::bmm::SAMPLESALES.Products Hierararchy.Product Detail] [kb:sampleapp::bmm::SAMPLESALES.Products Hierararchy.Product Detail.key.Dim Products Detail_Key] [kb:sampleapp::bmm::SAMPLESALES.Products Hierararchy.Product Detail.key.Dim Products Detail_Key1] [kb:sampleapp::pres::Sample Sales.Dim Products.Products Hierararchy.Product Detail] [kb:sampleapp::pres::Sample Sales.Dim Products.Products Hierararchy.Product Detail.0] |

For every key above, chronological key: not recorded. Offered to report authors as the hierarchy "Products Hierararchy" in the table "Dim Products", with 5 levels [kb:sampleapp::pres::Sample Sales.Dim Products.Products Hierararchy].

### Sales Rep Hierarchy

A parent-child drill path (value based: the depth is not fixed); 2 levels; marked as a time dimension: not recorded [kb:sampleapp::bmm::SAMPLESALES.Sales Rep Hierarchy]. Its default root level is "Total" [kb:sampleapp::bmm::SAMPLESALES.Sales Rep Hierarchy].

Path: Total, Detail.

| Level | Level above | Level(s) below | Key column | Keys (name; fields; primary and drill-down as recorded) | Member count as recorded | Shown to report authors as | Evidence |
|---|---|---|---|---|---|---|---|
| Total (grand total) | none recorded (a top level) | Detail | none recorded | none | 1 | no level is shown | [kb:sampleapp::bmm::SAMPLESALES.Sales Rep Hierarchy.Total] |
| Detail | Total | none recorded (a lowest level) | Dim Sales Rep, Employee ID | EMP_ID; Employee ID; primary true, drill-down true | 1 | no level is shown | [kb:sampleapp::bmm::SAMPLESALES.Sales Rep Hierarchy.Detail] [kb:sampleapp::bmm::SAMPLESALES.Sales Rep Hierarchy.Detail.key.EMP_ID] |

The parent-child relationships are held in the database table EMP_PARENT_CHILD, which the source "LTS Employee" of Dim Sales Rep names; in that table the member column is MEMBER_KEY, the parent (ancestor) column ANCESTOR_KEY, the distance column (steps between the two) DISTANCE and the leaf-flag column IS_LEAF [kb:sampleapp::bmm::SAMPLESALES.Sales Rep Hierarchy]. See the sections Dim Sales Rep and Database table EMP_PARENT_CHILD.

Offered to report authors as the hierarchy "Sales Rep Hierarchy" in the table "Dim Sales Rep", with 0 levels, shown by the field "Employee Name" [kb:sampleapp::pres::Sample Sales.Dim Sales Rep.Sales Rep Hierarchy].

### Time Hierarchy

A drill path with levels; 7 levels; marked as a time dimension: true [kb:sampleapp::bmm::SAMPLESALES.Time Hierarchy]. Its default root level is "Dim Time Total" [kb:sampleapp::bmm::SAMPLESALES.Time Hierarchy].

Path: Dim Time Total, Year, Half Year, Quarter, Month, Week, Dim Time Detail.

| Level | Level above | Level(s) below | Key column | Keys (name; fields; primary, chronological and drill-down as recorded) | Member count as recorded | Shown to report authors as | Evidence |
|---|---|---|---|---|---|---|---|
| Dim Time Total (grand total) | none recorded (a top level) | Year | none recorded | none | 1 | level "Dim Time Total" | [kb:sampleapp::bmm::SAMPLESALES.Time Hierarchy.Dim Time Total] [kb:sampleapp::pres::Sample Sales.Dim Time.Time Hierarchy.Dim Time Total] |
| Year | Dim Time Total | Half Year | Dim Time, Per Name Year | Per Name Year; Per Name Year; primary true, chronological true, drill-down true | 2 | level "Year", showing "Per Name Year" | [kb:sampleapp::bmm::SAMPLESALES.Time Hierarchy.Year] [kb:sampleapp::bmm::SAMPLESALES.Time Hierarchy.Year.key.Per Name Year] [kb:sampleapp::pres::Sample Sales.Dim Time.Time Hierarchy.Year] [kb:sampleapp::pres::Sample Sales.Dim Time.Time Hierarchy.Year.0] |
| Half Year | Year | Quarter | Dim Time, Per Name Half | Per Name Half; Per Name Half; primary true, chronological true, drill-down true | 180 | level "Half Year", showing "Per Name Half" | [kb:sampleapp::bmm::SAMPLESALES.Time Hierarchy.Half Year] [kb:sampleapp::bmm::SAMPLESALES.Time Hierarchy.Half Year.key.Per Name Half] [kb:sampleapp::pres::Sample Sales.Dim Time.Time Hierarchy.Half Year] [kb:sampleapp::pres::Sample Sales.Dim Time.Time Hierarchy.Half Year.0] |
| Quarter | Half Year | Month | Dim Time, Per Name Qtr | Per Name Qtr; Per Name Qtr; primary true, chronological true, drill-down true | 270 | level "Quarter", showing "Per Name Qtr" | [kb:sampleapp::bmm::SAMPLESALES.Time Hierarchy.Quarter] [kb:sampleapp::bmm::SAMPLESALES.Time Hierarchy.Quarter.key.Per Name Qtr] [kb:sampleapp::pres::Sample Sales.Dim Time.Time Hierarchy.Quarter] [kb:sampleapp::pres::Sample Sales.Dim Time.Time Hierarchy.Quarter.0] |
| Month | Quarter | Week | Dim Time, Per Name Month | Per Name Month; Per Name Month; primary true, chronological true, drill-down true | 322 | level "Month", showing "Per Name Month" | [kb:sampleapp::bmm::SAMPLESALES.Time Hierarchy.Month] [kb:sampleapp::bmm::SAMPLESALES.Time Hierarchy.Month.key.Per Name Month] [kb:sampleapp::pres::Sample Sales.Dim Time.Time Hierarchy.Month] [kb:sampleapp::pres::Sample Sales.Dim Time.Time Hierarchy.Month.0] |
| Week | Month | Dim Time Detail | Dim Time, Per Name Week | Per Name Week; Per Name Week; primary true, chronological true, drill-down true | 350 | level "Week", showing "Per Name Week" | [kb:sampleapp::bmm::SAMPLESALES.Time Hierarchy.Week] [kb:sampleapp::bmm::SAMPLESALES.Time Hierarchy.Week.key.Per Name Week] [kb:sampleapp::pres::Sample Sales.Dim Time.Time Hierarchy.Week] [kb:sampleapp::pres::Sample Sales.Dim Time.Time Hierarchy.Week.0] |
| Dim Time Detail | Week | none recorded (a lowest level) | Dim Time, Calendar Date | Dim Time Detail_Key; Calendar Date; primary true, chronological true, drill-down true | 365 | level "Dim Time Detail", showing "Calendar Date" | [kb:sampleapp::bmm::SAMPLESALES.Time Hierarchy.Dim Time Detail] [kb:sampleapp::bmm::SAMPLESALES.Time Hierarchy.Dim Time Detail.key.Dim Time Detail_Key] [kb:sampleapp::pres::Sample Sales.Dim Time.Time Hierarchy.Dim Time Detail] [kb:sampleapp::pres::Sample Sales.Dim Time.Time Hierarchy.Dim Time Detail.0] |

Offered to report authors as the hierarchy "Time Hierarchy" in the table "Dim Time", with 7 levels [kb:sampleapp::pres::Sample Sales.Dim Time.Time Hierarchy].

### Points to note

- The member counts of "Time Hierarchy" are copied as recorded: Year 2, Half Year 180, Quarter 270, Month 322, Week 350, Dim Time Detail 365 [kb:sampleapp::bmm::SAMPLESALES.Time Hierarchy.Year] [kb:sampleapp::bmm::SAMPLESALES.Time Hierarchy.Half Year] [kb:sampleapp::bmm::SAMPLESALES.Time Hierarchy.Quarter] [kb:sampleapp::bmm::SAMPLESALES.Time Hierarchy.Month] [kb:sampleapp::bmm::SAMPLESALES.Time Hierarchy.Week] [kb:sampleapp::bmm::SAMPLESALES.Time Hierarchy.Dim Time Detail]. How they were obtained is not recorded (open question Q11).
- "Sales Rep Hierarchy" is offered to report authors with 0 levels, shown by "Employee Name" [kb:sampleapp::pres::Sample Sales.Dim Sales Rep.Sales Rep Hierarchy].

## Values set per user or for the whole system

These counts come from the Slice and have no Row identifier:

- Variable names used inside VALUEOF(...) by the subject area's formulas, display-name formulas, filters and SQL texts: 10.
- Of these, defined in the repository: 1; used by name and not defined in the repository: 9.
- Variables of the repository in this subject area: 1.

### Variables the repository defines

#### PREFERRED_CURRENCY

PREFERRED_CURRENCY is a value set per user at login (a "session variable"); its security mask is `2` [kb:sampleapp::var::PREFERRED_CURRENCY]. Its default value is exactly `'gc1'` [kb:sampleapp::var::PREFERRED_CURRENCY].

It is set by the query that sets the value (the "init block") "initBlockPrefCurrency": session true; enabled not recorded; refresh period not recorded [kb:sampleapp::var::init.initBlockPrefCurrency]. That query is exactly [kb:sampleapp::var::init.initBlockPrefCurrency]:

```
select 'VALUEOF(NQ_SESSION.PREFERED_CURRENCY)' from dual
```

The text inside the quotation marks names PREFERED_CURRENCY, with one R; that name is listed below as used by name and not defined in the repository.

Where the subject area uses it: the formula of the field "Preferred Currency" of Fact Revenue names it inside VALUEOF(...), and that formula's reference list points at it (see the section Fact Revenue; listed in the packet without a Row identifier). What value a user has at run time is not recorded (open question Q2).

### Names used and not defined in the repository

For each name below the Knowledge Base has no variable, neither with exactly this name nor with a name that differs only in letter case. In the packet's words, such a name can be a session variable the server sets itself, or one set outside the repository; the repository does not say which. Each is "used by name, not defined in the repository" (no Row identifier).

| Name | Used in |
|---|---|
| CN_Sample_Sales_Dim_Time | the name shown for the table "Dim Time" (see the section What report authors see) |
| CN_Sample_Sales_Dim_Time_Calendar_Date | the name shown for the field "Calendar Date" of Dim Time (see the section Dim Time) |
| CN_Sample_Sales_Dim_Time_Per_Name_Half | the name shown for the field "Per Name Half" of Dim Time |
| CN_Sample_Sales_Dim_Time_Per_Name_Month | the name shown for the field "Per Name Month" of Dim Time |
| CN_Sample_Sales_Dim_Time_Per_Name_Qtr | the name shown for the field "Per Name Qtr" of Dim Time |
| CN_Sample_Sales_Dim_Time_Per_Name_Week | the name shown for the field "Per Name Week" of Dim Time |
| CN_Sample_Sales_Dim_Time_Per_Name_Year | the name shown for the field "Per Name Year" of Dim Time |
| PREFERED_CURRENCY | the query of the init block "initBlockPrefCurrency" (above) |
| WEBLANGUAGE | the formula of the field "Prod Dsc (Translated)" of Dim Products (see the section Dim Products) |

What these values hold at run time is not recorded (open questions Q1, Q2 and Q8).

## Security

### Row filters

No row filter is recorded for any source of this subject area.

### Permissions on objects

The packet lists 6 permissions, all on the two fields of "Fact Target" that report authors see (counted).

| Permission | On | Privilege package | Held by | Evidence |
|---|---|---|---|---|
| `write` | field "Revenue" of the table "Fact Target" | PrivPack_4209:18101355661677 | BI Author Role | [kb:sampleapp::sec::obj.ObjPriv_1500:18131355661677.o0] |
| `write` | field "Units" of the table "Fact Target" | PrivPack_4209:18101355661677 | BI Author Role | [kb:sampleapp::sec::obj.ObjPriv_1500:18131355661677.o1] |
| `read` | field "Revenue" of the table "Fact Target" | PrivPack_4209:18141355661677 | no role is recorded in the slice for that privilege package | [kb:sampleapp::sec::obj.ObjPriv_1500:18161355661677.o0] |
| `read` | field "Units" of the table "Fact Target" | PrivPack_4209:18141355661677 | no role is recorded in the slice for that privilege package | [kb:sampleapp::sec::obj.ObjPriv_1500:18161355661677.o1] |
| `read` | field "Revenue" of the table "Fact Target" | PrivPack_4209:18181355661677 | no role is recorded in the slice for that privilege package | [kb:sampleapp::sec::obj.ObjPriv_1500:18201355661677.o0] |
| `write` | field "Revenue" of the table "Fact Target" | PrivPack_4209:18181355661677 | no role is recorded in the slice for that privilege package | [kb:sampleapp::sec::obj.ObjPriv_1500:18211355661677.o0] |

Who holds the two privilege packages for which no role is recorded is not recorded (open question Q12).

### Roles

- Role with display name `BI Author Role`; name in the repository, exactly (the space is part of the name):

```
6D7874D30FD9EC2E7C15CF8DD0C61529AF3BDCAD955E5AD1372E6D902B14F0A9 3FA146A018E99435FCE83B3F7BDDBF5A30BAF7BEF918C3AFAA354A94E310F903
```

  guid `BIAuthor`. It holds the query privilege `QueryPriv_4204:18331355753308` of the privilege package `PrivPack_4209:18101355661677` (kind query_privilege) [kb:sampleapp::sec::pkg.PrivPack_4209:18101355661677.q0].

### Points to note

- The only object permissions recorded for this subject area are on "Revenue" and "Units" of "Fact Target"; "write" is given through two privilege packages [kb:sampleapp::sec::obj.ObjPriv_1500:18131355661677.o0] [kb:sampleapp::sec::obj.ObjPriv_1500:18211355661677.o0] (open question Q7).
- The overview counts 2 object permissions of the estate whose target was not recorded; they are not described here (see the section What report authors see).

## Reports built on this subject area

The estate has no Web Catalog half, so reports and dashboards were not examined. Calculations, filters and prompts defined inside reports are unknown (from the manifest; no Row identifier).

## What the export did not capture

The rows below come from the errors and unparsed tables of the Slice and from its manifest; they have no Row identifier.

### Errors

None: the extraction recorded no error about an object of this subject area (errors).

### Totals of source content that no table holds

From the unparsed table:

- All kinds together: 95 rows, 1769 occurrences in the source.
- Kind unread_attribute: 78 rows, 1674 occurrences.
- Kind unread_element: 15 rows, 91 occurrences.
- Kind unread_text: 2 rows, 4 occurrences.

A row is one attribute, element or text of one kind of object; its occurrences are how often it is in the source over the whole estate, not only in this subject area.

### Not read, per kind of object (whole estate)

Each item below was not read. The number in brackets is how often it occurs.

| Kind | Object | Rows | Occurrences | Not read |
|---|---|---|---|---|
| unread_attribute | ComplexJoin | 3 | 12 | ComplexJoin@mdsid (x4); Expr@mdsid (x4); Expr@name (x4) |
| unread_attribute | Dimension | 7 | 179 | LogicalKey@mdsid (x24); LogicalLevel@isFDUC (x22); LogicalLevel@levelConst (x22); RefLogicalColumn@logicalColumnRef (x34); RefLogicalColumn@refId (x58); RefLogicalLevel@logicalLevelRef (x1); RefLogicalLevel@refId (x18) |
| unread_attribute | Group | 4 | 14 | Group@displayName (x5); Group@guid (x5); RefGroup@groupRef (x2); RefGroup@refId (x2) |
| unread_attribute | InitBlock | 5 | 9 | DBMapItem@name (x2); InitBlock@connectionPoolRef (x2); InitBlock@isRowWiseInit (x1); RefVariable@refId (x2); RefVariable@variableRef (x2) |
| unread_attribute | LogicalComplexJoin | 1 | 9 | LogicalComplexJoin@mdsid (x9) |
| unread_attribute | LogicalTable | 13 | 177 | AggrRule@mdsid (x13); Expr@mdsid (x15); Expr@name (x15); LogicalColumn@isDerived (x2); LogicalKey@mdsid (x8); MeasureDefn@isCommutative (x13); MeasureDefn@mdsid (x13); MeasureDefn@name (x13); RefLogicalColumn@refId (x12); RefLogicalTableSource@logicalTableSourceRef (x17); RefLogicalTableSource@refId (x17); RefObject@objectTypeId (x19); RefObject@refId (x20) |
| unread_attribute | LogicalTableSource | 12 | 894 | Expr@mdsid (x95); Expr@name (x95); FragmentContent@mdsid (x17); FragmentContent@name (x17); GroupBy@mdsid (x17); GroupBy@name (x17); LogicalColumnExpr@mdsid (x95); LogicalColumnExpr@name (x95); RefObject@objectTypeId (x206); RefObject@refId (x206); WhereClause@mdsid (x17); WhereClause@name (x17) |
| unread_attribute | ObjectPrivilege | 3 | 22 | ObjectPrivilege@mdsid (x6); RefObject@objectTypeId (x8); RefObject@refId (x8) |
| unread_attribute | PhysicalTable | 10 | 89 | PhysicalColumn@lastUpdated (x2); PhysicalColumn@rowCount (x2); PhysicalForeignKey@mdsid (x18); PhysicalKey@mdsid (x14); PhysicalTable@cacheExpiry (x1); PhysicalTable@iconIndex (x4); PhysicalTable@isCacheable (x4); PhysicalTable@lastUpdated (x4); PhysicalTable@rowCount (x4); RefColumn@refId (x36) |
| unread_attribute | PresentationCatalog | 4 | 26 | PresentationCatalog@hasDispDescription (x3); PresentationCatalog@hasDispName (x3); RefPresentationTable@presentationTableRef (x10); RefPresentationTable@refId (x10) |
| unread_attribute | PresentationHierarchy | 6 | 87 | PresentationHierarchy@hasDispDescription (x5); PresentationHierarchy@hasDispName (x5); PresentationLevel@containerRef (x20); PresentationLevel@hasDispDescription (x20); PresentationLevel@hasDispName (x20); RefPresentationColumn@refId (x17) |
| unread_attribute | PresentationTable | 6 | 148 | PresentationColumn@hasDispDescription (x59); PresentationColumn@hasDispName (x59); PresentationTable@hasDispDescription (x10); PresentationTable@hasDispName (x10); RefPresentationHierarchy@presentationHierarchyRef (x5); RefPresentationHierarchy@refId (x5) |
| unread_attribute | PrivilegePackage | 2 | 4 | PrivilegePackage@userRef (x3); QueryPrivilegeMapping@databaseRef (x1) |
| unread_attribute | Variable | 2 | 4 | Expr@mdsid (x2); Expr@name (x2) |
| unread_element | Dimension | 4 | 54 | RefLogicalColumn (x34); RefLogicalColumns (x18); RefLogicalLevel (x1); RefPreferredDrillDown (x1) |
| unread_element | Group | 3 | 6 | Description (x2); RefChildGroups (x2); RefGroup (x2) |
| unread_element | InitBlock | 4 | 8 | DBMapItem (x2); RefVariable (x2); RefVariables (x2); Value (x2) |
| unread_element | PresentationCatalog | 2 | 13 | RefPresentationTable (x10); RefTables (x3) |
| unread_element | PresentationTable | 2 | 10 | RefHierarchies (x5); RefPresentationHierarchy (x5) |
| unread_text | Group | 1 | 2 | Description#text (x2) |
| unread_text | InitBlock | 1 | 2 | Value#text (x2) |

Whether any of the items not read under LogicalTableSource or PrivilegePackage would change what this document says about sources or permissions is not recorded (open question Q13).

### Left out of the Slice

From the manifest:

- Rows of the Knowledge Base's errors table that are not about an object of this subject area: 0.
- Rows of the unparsed table about whole object types or files with no reader, or about kinds of object the Slice does not hold: 15 (15 unmodelled_type). These are whole types of object that have no reader; nothing of such an object is in the Knowledge Base. Per object type:

| Object type not read | Rows | Objects |
|---|---|---|
| LogicalSourceFolder | 11 | 11 |
| QueryPrivilege | 1 | 1 |
| User | 3 | 3 |

- Rows that are in the Slice only so that a reference from another row resolves: 0.

## Observations across sections

Each line sets side by side what two sections record. No conclusion is drawn; where one is needed, an open question asks for it.

- The name shown for the table "Dim Time" is `VALUEOF(NQ_SESSION.CN_Sample_Sales_Dim_Time)` [kb:sampleapp::pres::Sample Sales.Dim Time]; CN_Sample_Sales_Dim_Time is used by name and not defined in the repository (section Values set per user or for the whole system; no Row identifier).
- The name shown for the field "Calendar Date" of "Dim Time" is `VALUEOF(NQ_SESSION.CN_Sample_Sales_Dim_Time_Calendar_Date)` [kb:sampleapp::pres::Sample Sales.Dim Time.Calendar Date]; CN_Sample_Sales_Dim_Time_Calendar_Date is used by name and not defined in the repository (section Values set per user or for the whole system; no Row identifier).
- The formula of "Preferred Currency" of Fact Revenue reads PREFERRED_CURRENCY [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Preferred Currency]; the repository defines PREFERRED_CURRENCY as a session variable with the default 'gc1' [kb:sampleapp::var::PREFERRED_CURRENCY].
- The formula of "Preferred Currency" reads PREFERRED_CURRENCY [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Preferred Currency]; the query of the init block that sets it names PREFERED_CURRENCY, with one R, which is used by name and not defined in the repository (section Values set per user or for the whole system; no Row identifier).
- The formula of "Prod Dsc (Translated)" of Dim Products reads WEBLANGUAGE [kb:sampleapp::bmm::SAMPLESALES.Dim Products.Prod Dsc (Translated)]; WEBLANGUAGE is used by name and not defined in the repository (section Values set per user or for the whole system; no Row identifier).
- The formula of "Prod Dsc (Translated)" names "PROD_DESC" of "Dim Products (Translated)" [kb:sampleapp::bmm::SAMPLESALES.Dim Products.Prod Dsc (Translated)]; "PROD_DESC" is mapped from SAMP_PRODUCTS_D_TRANSLATION.PROD_DESC [kb:sampleapp::bmm::SAMPLESALES.Dim Products (Translated).src.SAMP_PRODUCTS_D_TRANSLATION#map1].
- The level of detail of the source "SAMP_REVENUE_F" of Fact Revenue names the level "Detail" of "Sales Rep Hierarchy" [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.SAMP_REVENUE_F]; the level of detail of the source "SAMP_TARGETS_F" of Fact Target names no level of "Sales Rep Hierarchy" [kb:sampleapp::bmm::SAMPLESALES.Fact Target.src.SAMP_TARGETS_F].
- The business model lists no join between "Fact Target" and "Dim Sales Rep" (section How the tables relate; counted, no Row identifier); the level of detail of the source "SAMP_TARGETS_F" names no level of "Sales Rep Hierarchy" [kb:sampleapp::bmm::SAMPLESALES.Fact Target.src.SAMP_TARGETS_F].
- The field "Bill Mth Key" of Fact Target is mapped from SAMP_TARGETS_F.BILL_MTH_KEY [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TARGETS_F.BILL_MTH_KEY]; the level of detail of the source "SAMP_TARGETS_F" names the level "Dim Time Detail" of "Time Hierarchy" [kb:sampleapp::bmm::SAMPLESALES.Fact Target.src.SAMP_TARGETS_F].
- The level of detail of the source "Aggregate Revenue" names the level "Month" of "Time Hierarchy" [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue]; the level "Month" of "Time Hierarchy" has the key column "Per Name Month" [kb:sampleapp::bmm::SAMPLESALES.Time Hierarchy.Month].
- The level of detail of the source "SAMP_OFFICES_D" of Dim Offices is "Office Detail" of "Office Hierarchy" [kb:sampleapp::bmm::SAMPLESALES.Dim Offices.src.SAMP_OFFICES_D]; "Office Hierarchy" has 5 levels, from "Dim Offices Total" to "Office Detail" [kb:sampleapp::bmm::SAMPLESALES.Office Hierarchy].
- The level of detail of the source "LTS Employee" of Dim Sales Rep is "Detail" of "Sales Rep Hierarchy" [kb:sampleapp::bmm::SAMPLESALES.Dim Sales Rep.src.LTS Employee]; "Sales Rep Hierarchy" is a parent-child drill path with 2 levels, "Total" and "Detail" [kb:sampleapp::bmm::SAMPLESALES.Sales Rep Hierarchy].
- The source "LTS Employee" names EMP_PARENT_CHILD as the table that holds the parent-child relationships [kb:sampleapp::bmm::SAMPLESALES.Dim Sales Rep.src.LTS Employee]; EMP_PARENT_CHILD is recorded as the relationship table of a parent-child hierarchy, with member MEMBER_KEY, parent ANCESTOR_KEY, distance DISTANCE and leaf flag IS_LEAF [kb:sampleapp::phys::xe.SAMPLESALES.EMP_PARENT_CHILD].
- The source "LTS Employee" joins EMP_PARENT_CHILD.ANCESTOR_KEY to EMPLOYEE.EMP_ID [kb:sampleapp::bmm::SAMPLESALES.Dim Sales Rep.src.LTS Employee#table0.join0]; the foreign key SAMP_REVENUE_F_FKey#4 joins SAMP_REVENUE_F.EMPLOYEE_KEY (INT) to EMP_PARENT_CHILD.MEMBER_KEY (DOUBLE) [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F.SAMP_REVENUE_F_FKey#4].
- The source "FactCrossJoin" maps "Preserve" from FactCrossJoin.Preserve, type DOUBLE [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.FactCrossJoin#map0]; the SQL of FactCrossJoin is select 1 Preserve from dual [kb:sampleapp::phys::xe.SAMPLESALES.FactCrossJoin.def.DefaultMulDB].
- The source "Aggregate Revenue By Month" reads SAMP_REVENUE_A_MTH [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue By Month#table0]; SAMP_REVENUE_A_MTH has no key recorded (section Database table SAMP_REVENUE_A_MTH; no Row identifier).
- The source "SAMP_TARGETS_F" of Fact Target reads SAMP_TARGETS_F [kb:sampleapp::bmm::SAMPLESALES.Fact Target.src.SAMP_TARGETS_F#table0]; SAMP_TARGETS_F has no key recorded (section Database table SAMP_TARGETS_F; no Row identifier).
- The measure "Revenue" of Fact Target is mapped from SAMP_TARGETS_F.REVENUE [kb:sampleapp::bmm::SAMPLESALES.Fact Target.src.SAMP_TARGETS_F#map3]; the measure of Fact Revenue with the same name, "Revenue", is mapped in the source "SAMP_REVENUE_F" from SAMP_REVENUE_F.REVENUE [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.SAMP_REVENUE_F#map9].
- The measure "Units" of Fact Target is mapped from SAMP_TARGETS_F.UNITS [kb:sampleapp::bmm::SAMPLESALES.Fact Target.src.SAMP_TARGETS_F#map4]; the measure of Fact Revenue with the same name, "Units", is mapped in the source "SAMP_REVENUE_F" from SAMP_REVENUE_F.UNITS [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.SAMP_REVENUE_F#map10].
- The foreign key SAMP_REVENUE_A_YR_Foreign Key joins SAMP_REVENUE_A_YR.BILL_YEAR_KEY to SAMP_TIME_MTH_D.CAL_YEAR [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A_YR.SAMP_REVENUE_A_YR_Foreign Key]; SAMP_TIME_MTH_D has a key SAMP_TIME_MTH_D_Key#1 on CAL_YEAR [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.key.SAMP_TIME_MTH_D_Key#1].
- The column MTH_KEY of SAMP_TIME_MTH_D, at which four foreign keys point, is not mapped by any field of this subject area [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.MTH_KEY]; the key of Dim Time is "Calendar Date" [kb:sampleapp::bmm::SAMPLESALES.Dim Time.key.SAMP_TIME_DAY_D_Key].
- "write" on the field "Revenue" of Fact Target is given to "BI Author Role" [kb:sampleapp::sec::obj.ObjPriv_1500:18131355661677.o0]; the measure "Revenue" of Fact Target is recorded as writeable [kb:sampleapp::bmm::SAMPLESALES.Fact Target.Revenue].

## Open questions

These are the points the export could not settle. Each says how to find the answer and ends with an empty answer line. Write the answer on that line, or tell the agent; the document will then be updated.

### Q1. What name do report authors see for the table "Dim Time"?
Raised in: overview
Why it matters: the name shown to users for the table "Dim Time" is recorded as the formula VALUEOF(NQ_SESSION.CN_Sample_Sales_Dim_Time), not as a fixed text. The value of the variable CN_Sample_Sales_Dim_Time at run time is not recorded, so the name a report author sees for this table cannot be stated.
Assumed meanwhile: nothing. The table is called "Dim Time" in this document, and the formula is quoted exactly.
How to get the answer: log in to OBIEE as a report author, open the subject area "Sample Sales" and bring back the name shown for the table whose business table is "Dim Time". Then run this statement where OBIEE lets you issue logical SQL and bring back the value: select VALUEOF(NQ_SESSION.CN_Sample_Sales_Dim_Time) from "Sample Sales". If the value differs between users or languages, bring back two examples.
See also: table-07-dim-time, the six fields of "Dim Time" also have VALUEOF formulas as their shown names (CN_Sample_Sales_Dim_Time_Calendar_Date, _Per_Name_Half, _Per_Name_Month, _Per_Name_Qtr, _Per_Name_Week, _Per_Name_Year); bring back the names shown for them too.
See also: variables, none of the seven CN_Sample_Sales_Dim_Time names is defined in the repository; where their values are set is therefore not recorded either.
Answer:

### Q2. What value does PREFERRED_CURRENCY hold for a user at run time, and so which measure does "Preferred Currency" show?
Raised in: table-01-fact-revenue-p1
Why it matters: the field "Preferred Currency" of "Fact Revenue" is the formula INDEXCOL( CASE VALUEOF(NQ_SESSION."PREFERRED_CURRENCY") WHEN 'gc1' THEN 0 WHEN 'gc2' THEN 1 WHEN 'gc3' THEN 2 END , ...), which names after it the fields "Revenue", "Revenue (EUR)" and "Revenue (GBP)". The value the variable holds for a user is not recorded, nor what happens for a value other than 'gc1', 'gc2' or 'gc3'.
Assumed meanwhile: nothing. The formula is quoted exactly in the section Fact Revenue; no run-time value is stated.
How to get the answer: log in to OBIEE as a report author, run this statement where OBIEE lets you issue logical SQL, and bring back the value: select VALUEOF(NQ_SESSION.PREFERRED_CURRENCY) from "Sample Sales". Then put the fields "Preferred Currency" and "Revenue" side by side in one analysis and bring back whether the two totals are equal. If users can choose a currency in their own preferences, bring back the list of choices offered and the value after changing it.
See also: variables, PREFERRED_CURRENCY is a session variable with default 'gc1'; the init block "initBlockPrefCurrency" that sets it runs a query whose quoted text names PREFERED_CURRENCY, with one R, which the repository does not define. Bring back also what that query returns when run in the database.
Answer:

### Q3. What distinguishes "All Months Revenue" and "All Products Revenue" from "Revenue"?
Raised in: table-01-fact-revenue-p2
Why it matters: in both sources that map them, "Aggregate Revenue" and "SAMP_REVENUE_F", the three measures are mapped from the same database column REVENUE, and all three are summarised with SUM. Nothing in the export records any other rule that would distinguish them.
Assumed meanwhile: nothing. The three measures are described exactly as recorded, and no difference in meaning is stated.
How to get the answer: in the OBIEE Administration Tool, open the business table "Fact Revenue" and, for each of the fields "All Months Revenue" and "All Products Revenue", bring back whatever its properties show about levels of drill paths it is fixed at (for example a level set for "Time Hierarchy" or "Products Hierararchy"). Then run one analysis in "Sample Sales" with the fields "Revenue", "All Months Revenue" and "All Products Revenue" by month and by product, and bring back whether the three columns differ.
Answer:

### Q4. What do "Revenue (EUR)", "Revenue (GBP)" and "Revenue (INR)" hold, and why are they multiplied in one source only?
Raised in: table-01-fact-revenue-p2
Why it matters: in the source "SAMP_REVENUE_F" the three measures are the database column REVENUE multiplied by 0.76, 0.62 and 54.68; in the source "Aggregate Revenue" they are the column REVENUE of SAMP_REVENUE_A with no multiplication. As recorded, the same measure gives different numbers depending on the source OBIEE picks. Where the three fixed numbers come from is not recorded.
Assumed meanwhile: nothing. Both mappings are described exactly as recorded, and no currency meaning is stated.
How to get the answer: in the OBIEE Administration Tool, open the source "Aggregate Revenue" of the business table "Fact Revenue" and bring back the expression its column mapping shows for each of the three measures. Then run these two queries against the schema SAMPLESALES and bring back both totals: select sum(REVENUE) from SAMP_REVENUE_A, and select sum(REVENUE) from SAMP_REVENUE_F. For the numbers 0.76, 0.62 and 54.68, bring back the rate table or document they were taken from, if there is one, and the date they applied.
Answer:

### Q5. Which of the five sources of "Fact Revenue" does OBIEE use for a query, and in what order of preference?
Raised in: table-01-fact-revenue-p2
Why it matters: "Fact Revenue" has five active sources. Only "Aggregate Revenue" has a priority group recorded (1); for "Aggregate Revenue By Month", "Aggregate Revenue by Year", "FactCrossJoin" and "SAMP_REVENUE_F" it is not recorded. "FactCrossJoin" has no level of detail recorded. "Revenue" and "Units" are mapped in four of the sources, so the number a report shows depends on which source is used, and the export does not say how the choice is made.
Assumed meanwhile: nothing. Each source is described as recorded; no order of preference is stated.
How to get the answer: in the OBIEE Administration Tool, open each of the five sources of the business table "Fact Revenue" and bring back its priority group and the levels its content settings show. Then run in "Sample Sales" three analyses with "Revenue": by year, by month, and by product and day; for each, bring back the physical SQL that OBIEE logs for the query and the database table it reads.
Answer:

### Q6. What does the measure "Preserve" hold?
Raised in: table-01-fact-revenue-p2
Why it matters: "Preserve" is shown to report authors in "Fact Revenue", summarised with MAX, and mapped only in the source "FactCrossJoin", from the column Preserve of FactCrossJoin, which is a query kept in the repository and not a database table. The export records no description of what the number is or how a report author should use it.
Assumed meanwhile: nothing. The measure is described as recorded; no business meaning is stated.
How to get the answer: read the query of FactCrossJoin quoted in the section Database table FactCrossJoin, run it against the schema SAMPLESALES and bring back the rows it returns. Then run one analysis in "Sample Sales" with "Preserve" alone and one with "Preserve" and "Revenue" by year, and bring back both results.
See also: dbtable-03-factcrossjoin, the query is select 1 Preserve from dual, and FactCrossJoin has complex joins to SAMP_OFFICES_D, SAMP_PRODUCTS_D and EMPLOYEE; the question is now why the measure exists and how report authors are meant to use it.
See also: joins, the three complex joins from FactCrossJoin to SAMP_OFFICES_D, SAMP_PRODUCTS_D and EMPLOYEE have the condition 1=1 and cardinality ONE TO ONE.
Answer:

### Q7. What do the target measures of "Fact Target" hold, and what happens to a value a user types into them?
Raised in: table-02-fact-target
Why it matters: "Fact Target" holds two measures, "Revenue" and "Units", both summed, both mapped from the database table SAMP_TARGETS_F, and both recorded as writeable. The export says nothing on what a target is (for whom, for which period), and nothing on whether typed values are written back to SAMP_TARGETS_F, by whom, or whether they are kept.
Assumed meanwhile: nothing. Both measures are described as recorded; no business meaning and no write-back behaviour are stated.
How to get the answer: in the OBIEE Administration Tool, open the business table "Fact Target" and bring back whatever the properties of "Revenue" and "Units" show about writing values back, and whether the database connection used for SAMP_TARGETS_F allows writing. Then run against the schema SAMPLESALES the query select count(*), min(BILL_MTH_KEY), max(BILL_MTH_KEY) from SAMP_TARGETS_F and bring back the result; and bring back any dashboard or form in which users enter targets, if there is one.
See also: overview, "Fact Target"."Revenue" is the measure OBIEE uses when a query names none (moved here by the review from "Why it matters").
Answer:

### Q8. What does "Prod Dsc (Translated)" show a user, and which languages are there?
Raised in: table-04-dim-products
Why it matters: the field "Prod Dsc (Translated)" of "Dim Products" is a formula that starts with Lookup and names the field "PROD_DESC" of the business table "Dim Products (Translated)", the fields "Prod Dsc" and "Prod Key" of "Dim Products", and the variable WEBLANGUAGE. The value WEBLANGUAGE holds for a user, the languages for which translations exist, and what is shown when no translation is found are not recorded.
Assumed meanwhile: nothing. The formula is quoted exactly in the section Dim Products; no run-time behaviour is stated.
How to get the answer: log in to OBIEE as a report author, run this statement where OBIEE lets you issue logical SQL, and bring back the value: select VALUEOF(NQ_SESSION.WEBLANGUAGE) from "Sample Sales". Then, in the database table that the source of "Dim Products (Translated)" reads (named in the section Dim Products (Translated)), count the rows per value of the column that the lookup matches against the variable, and bring back the result. Finally show "Prod Dsc" and "Prod Dsc (Translated)" side by side in one analysis, once with the user interface in English and once in another language, and bring back both results.
See also: table-05-dim-products-translated, the lookup table is keyed on PROD_KEY and LANG_ID (VARCHAR, precision 2); the count can be run as select LANG_ID, count(*) from SAMP_PRODUCTS_D_TRANSLATION group by LANG_ID.
See also: variables, WEBLANGUAGE is used by name and not defined in the repository.
Answer:

### Q9. What does one row of "Dim Sales Rep" stand for, and what does "Distance" hold?
Raised in: table-06-dim-sales-rep
Why it matters: the source "LTS Employee" joins EMPLOYEE to the closure table EMP_PARENT_CHILD on EMP_PARENT_CHILD.ANCESTOR_KEY = EMPLOYEE.EMP_ID, as an inner join, one to many, and maps the field "Distance", which report authors see, from EMP_PARENT_CHILD.DISTANCE. The key of the business table is "Employee ID" alone. The export does not say whether a report that shows "Dim Sales Rep" fields gets one row per employee or one row per employee and related employee, nor what a value of "Distance" means.
Assumed meanwhile: nothing. The join and the mapping are described as recorded; no row meaning is stated.
How to get the answer: run against the schema SAMPLESALES the queries select count(*) from EMPLOYEE and select count(*) from EMPLOYEE e join EMP_PARENT_CHILD p on p.ANCESTOR_KEY = e.EMP_ID, and bring back both counts. Then run one analysis in "Sample Sales" with "Employee ID", "Employee Name" and "Distance" and one with "Employee ID" and "Revenue" of "Fact Revenue", and bring back the row counts and the total of "Revenue" in the second compared with the total of "Revenue" alone.
See also: dbtable-02-emp-parent-child, the table is recorded as one row per member and ancestor, with DISTANCE the steps between the two and IS_LEAF a leaf flag; SAMP_REVENUE_F points at its MEMBER_KEY, while the source joins on ANCESTOR_KEY.
Answer:

### Q10. Does the column CAL_YEAR identify one row of SAMP_TIME_MTH_D?
Raised in: dbtable-13-samp-time-mth-d
Why it matters: the repository declares two keys for SAMP_TIME_MTH_D, one on MTH_KEY and one on CAL_YEAR, and does not say which is the primary key. The foreign key "SAMP_REVENUE_A_YR_Foreign Key" from SAMP_REVENUE_A_YR (column BILL_YEAR_KEY) points at CAL_YEAR. The table also holds the columns CAL_MONTH, MTH_KEY and PER_NAME_MONTH. If a value of CAL_YEAR appears on several rows, a key on CAL_YEAR does not hold in the data, and a join on it would match several rows.
Assumed meanwhile: nothing. Both keys are described as recorded.
How to get the answer: run against the schema SAMPLESALES the query select count(*), count(distinct CAL_YEAR), count(distinct MTH_KEY) from SAMP_TIME_MTH_D and bring back the three numbers. Then, in the OBIEE Administration Tool, open the database table SAMP_TIME_MTH_D and bring back which of its two keys is marked as the primary key, if either.
Answer:

### Q11. Are the member counts recorded for the levels of "Time Hierarchy" current?
Raised in: hierarchies
Why it matters: the repository records these member counts for "Time Hierarchy": Year 2, Half Year 180, Quarter 270, Month 322, Week 350, Dim Time Detail 365. They are copied as recorded. The export does not say when or how they were obtained, and a level with 2 members sits above a level with 180. Anyone sizing or checking the new tables against these counts needs to know whether they describe the data.
Assumed meanwhile: nothing. The counts are copied as recorded and not used for any other statement.
How to get the answer: run against the schema SAMPLESALES the query select count(distinct PER_NAME_YEAR), count(distinct PER_NAME_HALF), count(distinct PER_NAME_QTR), count(distinct PER_NAME_MONTH), count(distinct PER_NAME_WEEK), count(distinct CALENDAR_DATE) from SAMP_TIME_DAY_D and bring back the six numbers. Then, in the OBIEE Administration Tool, open the drill path "Time Hierarchy" and bring back the member count each level shows and, if shown, when it was last updated.
Answer:

### Q12. Who holds the privilege packages PrivPack_4209:18141355661677 and PrivPack_4209:18181355661677?
Raised in: security
Why it matters: these two privilege packages give "read" on the fields "Revenue" and "Units" of "Fact Target", and "write" on "Revenue", but no role is recorded in the export for either. The only role recorded, "BI Author Role", holds a third package, PrivPack_4209:18101355661677, which gives "write" on both fields. As recorded, it cannot be said which users may read or change the target values, nor whether users outside "BI Author Role" are restricted.
Assumed meanwhile: nothing. The permissions are listed as recorded; no holder is stated for the two packages.
How to get the answer: in the OBIEE Administration Tool, open the permissions of the fields "Revenue" and "Units" of the table "Fact Target" in the subject area "Sample Sales" and bring back, for each permission, the application role or user it is granted to. If the roles are kept outside the repository, bring back the members of each role named there.
Answer:

### Q13. Do the items the export did not read for sources and privilege packages hold anything this document should state?
Raised in: gaps
Why it matters: the export did not read, among others, WhereClause@mdsid and WhereClause@name (17 occurrences each) and FragmentContent@mdsid and FragmentContent@name (17 each) in LogicalTableSource, PrivilegePackage@userRef (3) in PrivilegePackage, and the whole object types QueryPrivilege (1 object) and User (3 objects). The sections on sources and on security describe only what was read; if any of these items carries a condition on a source or the holder of a privilege package, those sections are incomplete.
Assumed meanwhile: nothing. Each section states what was read; nothing is assumed about what was not read.
How to get the answer: in the OBIEE Administration Tool, open each of the 12 sources of the business model "SAMPLESALES" and bring back, for each, whatever its content settings show as a condition on rows (a where clause) or as a fragment, or that there is none. Then open the users and the privilege packages PrivPack_4209:18141355661677 and PrivPack_4209:18181355661677 and bring back which users or roles each package is assigned to.
Answer:

### Q14. Does the column MEMBER_KEY identify one row of EMP_PARENT_CHILD?
Raised in: dbtable-02-emp-parent-child
Why it matters: the repository records EMP_PARENT_CHILD as the relationship table of a parent-child hierarchy, with one row per member and ancestor, and declares one key for it, "EMP_PARENT_CHILD_Key", made of MEMBER_KEY alone. The foreign key "SAMP_REVENUE_F_FKey#4" from SAMP_REVENUE_F (column EMPLOYEE_KEY) points at MEMBER_KEY. The export does not say whether a value of MEMBER_KEY appears on one row or on several; if on several, a key on MEMBER_KEY alone does not hold in the data.
Assumed meanwhile: nothing. The key and the foreign key are described as recorded; no uniqueness is stated.
How to get the answer: run against the schema SAMPLESALES the query select count(*), count(distinct MEMBER_KEY), count(distinct ANCESTOR_KEY) from EMP_PARENT_CHILD, and the query select count(*) from (select MEMBER_KEY, ANCESTOR_KEY from EMP_PARENT_CHILD group by MEMBER_KEY, ANCESTOR_KEY), and bring back the four numbers. Then, in the OBIEE Administration Tool, open the database table EMP_PARENT_CHILD and bring back the columns of its key "EMP_PARENT_CHILD_Key" and whether it is marked as the primary key.
Answer:

## Answered questions

Questions that have been answered. Statements that rest on an answer carry the question's number in their marker.

## Not described

Rows of the Knowledge Base that this document deliberately leaves out, each with the reason.

Nothing was left out: every required row is described above.

## Questions the export answered

Questions raised while one section was written and settled by a later section. Their numbers are not used again.

None.
