Status: reviewed 2026-11-23 (16 findings corrected, 0 turned into a question, 19 questions open)

# Sample Sales — the semantic model as it exists today

**What this is.** A plain-language description of the Sample Sales subject area in OBIEE: what report authors see, what each number is, how the tables relate and where the data comes from. It describes the existing system only.

**How to read it.** Statements of fact end with a marker in square brackets. The marker names the row of the Knowledge Base the statement came from, so any statement can be checked. A marker that starts with "answer" means the statement rests on an answer a person gave, not on OBIEE. Where the export does not settle something, the text points to a numbered open question near the end; each says how to find the answer and has an empty Answer line for you.

| | |
|---|---|
| Subject area | Sample Sales |
| Estate | sampleapp |
| Knowledge Base extracted | 2026-11-23T08:22:16Z |
| Reports and dashboards | not examined |

## The model in one page

**What was not examined.** Only the repository half of the estate was extracted. The estate has no Web Catalog half, so reports and dashboards were not examined; calculations, filters and prompts defined inside reports are unknown (from the manifest). The extraction recorded no error about any object of the subject area (from the errors table). 131 kinds of detail in the export were not read, counted over the whole estate, and 15 whole kinds of object have no reader (from the unparsed table and the manifest). The unread details that matter most here are the far side of every foreign key, the joins inside sources, the parent-child settings, and the names of roles; the section What the export did not capture lists them.

**What the subject area holds.** "Sample Sales" is built on the business model "SAMPLESALES"; when a query names no measure, OBIEE uses "Fact Target"."Revenue" [kb:sampleapp::pres::Sample Sales]. Report authors see 6 tables (Dim Offices, Dim Products, Dim Sales Rep, Dim Time, Fact Revenue, Fact Target) with 49 fields, 8 of them measures. Behind them are 7 business tables with 63 fields, 11 of them measures, fed by 12 sources from 13 database tables, with 7 business-model joins, 17 database joins, 4 drill paths, 1 variable and 7 security rows (counted).

- Fact Revenue has 22 fields, 9 of them measures, and report authors see 15; it has 5 sources [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue]. The measures shown are Revenue, Units, All Months Revenue, All Products Revenue, Discnt Value and Preserve; the measures Revenue (EUR), Revenue (GBP) and Revenue (INR) are not shown (counted).
- Fact Target holds two measures, Revenue and Units, both summed, from the database table SAMP_TARGETS_F [kb:sampleapp::bmm::SAMPLESALES.Fact Target.src.SAMP_TARGETS_F].
- Dim Offices (database table SAMP_OFFICES_D), Dim Products (SAMP_PRODUCTS_D), Dim Sales Rep (EMPLOYEE and EMP_PARENT_CHILD) and Dim Time (SAMP_TIME_DAY_D and SAMP_TIME_MTH_D) hold no measures [kb:sampleapp::bmm::SAMPLESALES.Dim Offices.src.SAMP_OFFICES_D] [kb:sampleapp::bmm::SAMPLESALES.Dim Products.src.SAMP_PRODUCTS_D] [kb:sampleapp::bmm::SAMPLESALES.Dim Sales Rep.src.LTS Employee] [kb:sampleapp::bmm::SAMPLESALES.Dim Time.src.LTS Time Day] [kb:sampleapp::bmm::SAMPLESALES.Dim Time.src.LTS Time Month].
- Dim Products (Translated) is a seventh business table that report authors do not see [kb:sampleapp::bmm::SAMPLESALES.Dim Products (Translated).src.SAMP_PRODUCTS_D_TRANSLATION]; the formula of the field "Prod Dsc (Translated)" of Dim Products names it [kb:sampleapp::bmm::SAMPLESALES.Dim Products.Prod Dsc (Translated)].

**How the tables relate and drill.** All 7 business-model joins are inner joins. Fact Revenue joins Dim Time, Dim Sales Rep, Dim Offices and Dim Products; Fact Target joins Dim Time, Dim Offices and Dim Products, and no join between Fact Target and Dim Sales Rep is recorded [kb:sampleapp::bmm::joins.Relationship_2004:17511354089145] [kb:sampleapp::bmm::joins.Relationship_2004:11061353928600]. There are 4 drill paths, all offered to report authors: Office Hierarchy [kb:sampleapp::bmm::SAMPLESALES.Office Hierarchy], Products Hierararchy [kb:sampleapp::bmm::SAMPLESALES.Products Hierararchy], Time Hierarchy [kb:sampleapp::bmm::SAMPLESALES.Time Hierarchy] and Sales Rep Hierarchy, which is recorded as parent-child with no fixed depth [kb:sampleapp::bmm::SAMPLESALES.Sales Rep Hierarchy]. Which drill paths apply to which measures is recorded only through the levels of detail that each source names; they are quoted in the sections of the tables.

**Behaviours to know before relying on a number.**

1. Revenue and Units of Fact Revenue are fed by four sources at different levels of detail (detail, month and product, month, year), and a priority is recorded for only one of them [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue] [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue]. Which source answers a query is not recorded (open question Q4).
2. All Months Revenue and All Products Revenue are summed from the same database column as Revenue, so as recorded the three return the same number [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.All Months Revenue] [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.All Products Revenue] (open question Q2).
3. The hidden measures Revenue (EUR), (GBP) and (INR) are the column REVENUE multiplied by 0.76, 0.62 and 54.68 in the source SAMP_REVENUE_F, but not multiplied in the source Aggregate Revenue [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.SAMP_REVENUE_F#map15] [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue#map8] (open question Q3). The shown field "Preferred Currency" picks Revenue, Revenue (EUR) or Revenue (GBP) from the per-user value PREFERRED_CURRENCY [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Preferred Currency], whose default is `'gc1'` [kb:sampleapp::var::PREFERRED_CURRENCY] and whose recorded query reads no table and spells the name differently [kb:sampleapp::var::init.initBlockPrefCurrency] (open questions Q6 and Q16).
4. Both measures of Fact Target are writeable: users can type values in [kb:sampleapp::bmm::SAMPLESALES.Fact Target.Revenue] [kb:sampleapp::bmm::SAMPLESALES.Fact Target.Units]. The only security recorded for the subject area is on these two measures; the one holder recorded is a role given as a code, not a name, and no row filter is recorded [kb:sampleapp::sec::obj.ObjPriv_1500:18131355661677.o0] [kb:sampleapp::sec::pkg.PrivPack_4209:18101355661677.q0] (open questions Q7 and Q17).
5. Some of what users see is built from values set per user at login: the displayed names of the table Dim Time and of its six fields are formulas that read such values [kb:sampleapp::pres::Sample Sales.Dim Time] [kb:sampleapp::pres::Sample Sales.Dim Time.Calendar Date], and so is the formula of the field "Prod Dsc (Translated)" [kb:sampleapp::bmm::SAMPLESALES.Dim Products.Prod Dsc (Translated)]. What users actually see is not recorded (open questions Q1, Q8 and Q10).

**Joins between database tables are incomplete.** Of 17 database joins, 3 run from the query "FactCrossJoin" to SAMP_OFFICES_D, SAMP_PRODUCTS_D and EMPLOYEE with the condition `1=1` [kb:sampleapp::phys::joins.Complex Join]. The other 14 are foreign keys for which the table and column they point to were not recorded, for example [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F.SAMP_REVENUE_F_FKey] (open question Q11). No section states a primary key, because keys were not read.

**Open questions.** 19 questions are open (Q1 to Q19) and none is answered. They are at the end of the document; each says how to find the answer.

## What report authors see

"Sample Sales" is a subject area built on the business model "SAMPLESALES" [kb:sampleapp::pres::Sample Sales]. It has one other name (an alias): `SAMPLESALES` [kb:sampleapp::pres::Sample Sales].

When a query names no measure, OBIEE uses the measure "Revenue" of the business table "Fact Target" (the "implicit fact column") [kb:sampleapp::pres::Sample Sales].

### The tables report authors see

| Table | Number of fields | Business table behind it | Evidence |
|---|---|---|---|
| Dim Offices | 9 | Dim Offices | [kb:sampleapp::pres::Sample Sales.Dim Offices] |
| Dim Products | 9 | Dim Products | [kb:sampleapp::pres::Sample Sales.Dim Products] |
| Dim Sales Rep | 5 | Dim Sales Rep | [kb:sampleapp::pres::Sample Sales.Dim Sales Rep] |
| Dim Time | 6 | Dim Time | [kb:sampleapp::pres::Sample Sales.Dim Time] |
| Fact Revenue | 15 | Fact Revenue | [kb:sampleapp::pres::Sample Sales.Fact Revenue] |
| Fact Target | 5 | Fact Target | [kb:sampleapp::pres::Sample Sales.Fact Target] |

Each business table has a section of its own below, under its name.

The name shown to users for the table "Dim Time" is not fixed text. It is recorded as a formula that reads a value set per user at login: `VALUEOF(NQ_SESSION.CN_Sample_Sales_Dim_Time)` [kb:sampleapp::pres::Sample Sales.Dim Time]. What name a user actually sees is not recorded (open question Q1).

### Counts

These numbers were counted from the tables of the Slice (counted).

| What | Count |
|---|---|
| Tables report authors see | 6 |
| Fields report authors see | 49, of which 8 are measures |
| Business tables described | 7 |
| Fields of the business tables | 63, of which 11 are measures |
| Sources | 12 |
| Database tables | 13 |
| Database columns used | 71 |
| Business-model joins | 7 |
| Database joins | 17 |
| Drill paths | 4, all 4 offered to report authors |
| Variables | 1 |
| Security rows | 7 |
| Reports built on the subject area | 0 |
| Dashboards placing them | 0 |
| Saved filters they use | 0 |
| Errors about objects of the Slice | 0 |
| Kinds of source content not read | 131 |

There is one more business table than tables report authors see, and three more measures than report authors see. Measures that report authors do not see are described with their business table. Errors and unread content are listed in the section What the export did not capture.

The three zeros for reports, dashboards and saved filters do not mean that none exist. Only the repository half ("rpd") of the estate sampleapp was extracted; the estate has no Web Catalog half, so reports and dashboards were not examined (from the manifest). The Knowledge Base was extracted at 2026-11-23T08:22:16Z by extractor version 0.1.0 (from the manifest).

### Items of the estate that are in no subject area

These exist in the full Knowledge Base and belong to no subject area, or not to this one. This document does not describe them (counts from the manifest of the Slice).

- Object permissions whose target was not recorded: 2.
- Role privileges of packages that no permission of this subject area goes through: 0.
- Queries that set a value ("init blocks") but set no variable of this subject area: 1.
- Variables that no expression of this subject area names: 1.

## Fact Revenue

Fact Revenue is a business table of the model "SAMPLESALES", and the repository declares 5 sources for it [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue]. It has 22 fields, 9 of them measures; report authors see 15 of the 22 and do not see 7 (counted).

### Measures

All measures below are recorded as not writeable. "Comes from" names the database table and column for each source that feeds the measure.

| Measure | How it is summarised | Comes from | Evidence |
|---|---|---|---|
| Revenue | Sum: ` SUM("SAMPLESALES"."Fact Revenue"."Revenue")` | Source "Aggregate Revenue By Month": `SAMP_REVENUE_A_MTH.REVENUE` (DOUBLE, precision 22). Source "Aggregate Revenue by Year": `SAMP_REVENUE_A_YR.REVENUE` (DOUBLE, precision 22). Source "Aggregate Revenue": `SAMP_REVENUE_A.REVENUE` (DOUBLE, precision 8). Source "SAMP_REVENUE_F": `SAMP_REVENUE_F.REVENUE` (DOUBLE, precision 7). In 4 of the 5 sources | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Revenue] [kb:sampleapp::pres::Sample Sales.Fact Revenue.Revenue] [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Revenue#aggr0] [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue By Month#map0] [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue by Year#map0] [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue#map3] [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.SAMP_REVENUE_F#map9] [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A_MTH.REVENUE] [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A_YR.REVENUE] [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A.REVENUE] [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F.REVENUE] |
| Units | Sum: ` SUM("SAMPLESALES"."Fact Revenue"."Units")` | Source "Aggregate Revenue By Month": `SAMP_REVENUE_A_MTH.UNITS` (DOUBLE, precision 22). Source "Aggregate Revenue by Year": `SAMP_REVENUE_A_YR.UNITS` (DOUBLE, precision 22). Source "Aggregate Revenue": `SAMP_REVENUE_A.UNITS` (DOUBLE, precision 15). Source "SAMP_REVENUE_F": `SAMP_REVENUE_F.UNITS` (DOUBLE, precision 15). In 4 of the 5 sources | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Units] [kb:sampleapp::pres::Sample Sales.Fact Revenue.Units] [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Units#aggr0] [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue By Month#map1] [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue by Year#map1] [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue#map4] [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.SAMP_REVENUE_F#map10] [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A_MTH.UNITS] [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A_YR.UNITS] [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A.UNITS] [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F.UNITS] |
| All Months Revenue | Sum: ` SUM("SAMPLESALES"."Fact Revenue"."All Months Revenue")` | Source "Aggregate Revenue": `SAMP_REVENUE_A.REVENUE` (DOUBLE, precision 8). Source "SAMP_REVENUE_F": `SAMP_REVENUE_F.REVENUE` (DOUBLE, precision 7). In 2 of the 5 sources | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.All Months Revenue] [kb:sampleapp::pres::Sample Sales.Fact Revenue.All Months Revenue] [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.All Months Revenue#aggr0] [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue#map6] [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.SAMP_REVENUE_F#map12] [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A.REVENUE] [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F.REVENUE] |
| All Products Revenue | Sum: ` SUM("SAMPLESALES"."Fact Revenue"."All Products Revenue")` | Source "Aggregate Revenue": `SAMP_REVENUE_A.REVENUE` (DOUBLE, precision 8). Source "SAMP_REVENUE_F": `SAMP_REVENUE_F.REVENUE` (DOUBLE, precision 7). In 2 of the 5 sources | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.All Products Revenue] [kb:sampleapp::pres::Sample Sales.Fact Revenue.All Products Revenue] [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.All Products Revenue#aggr0] [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue#map5] [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.SAMP_REVENUE_F#map11] [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A.REVENUE] [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F.REVENUE] |
| Discnt Value | Sum: ` SUM("SAMPLESALES"."Fact Revenue"."Discnt Value")` | Source "Aggregate Revenue": `SAMP_REVENUE_A.DISCNT_VALUE` (DOUBLE, precision 7). Source "SAMP_REVENUE_F": `SAMP_REVENUE_F.DISCNT_VALUE` (DOUBLE, precision 6). In 2 of the 5 sources | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Discnt Value] [kb:sampleapp::pres::Sample Sales.Fact Revenue.Discnt Value] [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Discnt Value#aggr0] [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue#map1] [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.SAMP_REVENUE_F#map2] [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A.DISCNT_VALUE] [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F.DISCNT_VALUE] |
| Preserve | Maximum: ` MAX("SAMPLESALES"."Fact Revenue"."Preserve")` | Source "FactCrossJoin": column `Preserve` of "FactCrossJoin", which is a query and not a database table (DOUBLE). In 1 of the 5 sources | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Preserve] [kb:sampleapp::pres::Sample Sales.Fact Revenue.Preserve] [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Preserve#aggr0] [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.FactCrossJoin#map0] [kb:sampleapp::phys::xe.SAMPLESALES.FactCrossJoin.Preserve] |

"All Months Revenue" and "All Products Revenue" are summed from the same two database columns as "Revenue" in the sources "Aggregate Revenue" and "SAMP_REVENUE_F" [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue#map6] [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue#map5] [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue#map3]. Nothing recorded here sets them apart from "Revenue" (open question Q2).

What "Preserve" is for is not recorded (open question Q5).

### Measures and fields report authors do not see

Three measures are not shown to report authors in this subject area. Each is fed by two sources, and the two sources do not treat it the same way: in the source "Aggregate Revenue" it is the database column as it is, and in the source "SAMP_REVENUE_F" it is the database column multiplied by a fixed number.

| Measure | How it is summarised | In source "Aggregate Revenue" | In source "SAMP_REVENUE_F" | Evidence |
|---|---|---|---|---|
| Revenue (EUR) | Sum: ` SUM("SAMPLESALES"."Fact Revenue"."Revenue (EUR)")` | `SAMP_REVENUE_A.REVENUE` (DOUBLE, precision 8), not multiplied | `"xe".""."SAMPLESALES"."SAMP_REVENUE_F"."REVENUE" * 0.76`: the database column `SAMP_REVENUE_F.REVENUE` (DOUBLE, precision 7) multiplied by 0.76 | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Revenue (EUR)] [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Revenue (EUR)#aggr0] [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue#map8] [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.SAMP_REVENUE_F#map15] [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A.REVENUE] [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F.REVENUE] |
| Revenue (GBP) | Sum: ` SUM("SAMPLESALES"."Fact Revenue"."Revenue (GBP)")` | `SAMP_REVENUE_A.REVENUE` (DOUBLE, precision 8), not multiplied | `"xe".""."SAMPLESALES"."SAMP_REVENUE_F"."REVENUE" * 0.62`: the database column `SAMP_REVENUE_F.REVENUE` (DOUBLE, precision 7) multiplied by 0.62 | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Revenue (GBP)] [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Revenue (GBP)#aggr0] [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue#map7] [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.SAMP_REVENUE_F#map14] [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A.REVENUE] [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F.REVENUE] |
| Revenue (INR) | Sum: ` SUM("SAMPLESALES"."Fact Revenue"."Revenue (INR)")` | `SAMP_REVENUE_A.REVENUE` (DOUBLE, precision 8), not multiplied | `"xe".""."SAMPLESALES"."SAMP_REVENUE_F"."REVENUE" * 54.68`: the database column `SAMP_REVENUE_F.REVENUE` (DOUBLE, precision 7) multiplied by 54.68 | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Revenue (INR)] [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Revenue (INR)#aggr0] [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue#map9] [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.SAMP_REVENUE_F#map16] [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A.REVENUE] [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F.REVENUE] |

The names suggest currencies, but what the three measures hold, why the numbers 0.76, 0.62 and 54.68 are written into the formulas, and why the source "Aggregate Revenue" does not multiply, are not recorded (open question Q3).

Four fields that are not measures are also not shown to report authors:

| Field | Database column | Type | Evidence |
|---|---|---|---|
| BILL_MTH_KEY | Source "Aggregate Revenue By Month": `SAMP_REVENUE_A_MTH.BILL_MTH_KEY`. In 1 of the 5 sources | DOUBLE, precision 22 | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.BILL_MTH_KEY] [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue By Month#map2] [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A_MTH.BILL_MTH_KEY] |
| BILL_YEAR_KEY | Source "Aggregate Revenue by Year": `SAMP_REVENUE_A_YR.BILL_YEAR_KEY`. In 1 of the 5 sources | DOUBLE, precision not recorded | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.BILL_YEAR_KEY] [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue by Year#map3] [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A_YR.BILL_YEAR_KEY] |
| DISCNT_VALUE | Source "Aggregate Revenue By Month": `SAMP_REVENUE_A_MTH.DISCNT_VALUE`. Source "Aggregate Revenue by Year": `SAMP_REVENUE_A_YR.DISCNT_VALUE`. In 2 of the 5 sources | DOUBLE, precision 22, in both | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.DISCNT_VALUE] [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue By Month#map3] [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue by Year#map2] [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A_MTH.DISCNT_VALUE] [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A_YR.DISCNT_VALUE] |
| Sales Employee Key | Source "SAMP_REVENUE_F": `SAMP_REVENUE_F.EMPLOYEE_KEY`. In 1 of the 5 sources | INT | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Sales Employee Key] [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.SAMP_REVENUE_F#map13] [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F.EMPLOYEE_KEY] |

### Fields

These fields are shown to report authors and are recorded as not measures and not writeable.

| Field | Database column | Type | Evidence |
|---|---|---|---|
| Bill Day Dt | Source "SAMP_REVENUE_F": `SAMP_REVENUE_F.BILL_DAY_DT`. In 1 of the 5 sources | DATE | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Bill Day Dt] [kb:sampleapp::pres::Sample Sales.Fact Revenue.Bill Day Dt] [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.SAMP_REVENUE_F#map0] [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F.BILL_DAY_DT] |
| Bill Mth Key | Source "Aggregate Revenue": `SAMP_REVENUE_A.BILL_MTH_KEY`. Source "SAMP_REVENUE_F": `SAMP_REVENUE_F.BILL_MTH_KEY`. In 2 of the 5 sources | DOUBLE, precision 15, in both | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Bill Mth Key] [kb:sampleapp::pres::Sample Sales.Fact Revenue.Bill Mth Key] [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue#map0] [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.SAMP_REVENUE_F#map1] [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A.BILL_MTH_KEY] [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F.BILL_MTH_KEY] |
| Office Key | Source "SAMP_REVENUE_F": `SAMP_REVENUE_F.OFFICE_KEY`. In 1 of the 5 sources | DOUBLE, precision 15 | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Office Key] [kb:sampleapp::pres::Sample Sales.Fact Revenue.Office Key] [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.SAMP_REVENUE_F#map3] [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F.OFFICE_KEY] |
| Order Day Dt | Source "SAMP_REVENUE_F": `SAMP_REVENUE_F.ORDER_DAY_DT`. In 1 of the 5 sources | DATETIME | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Order Day Dt] [kb:sampleapp::pres::Sample Sales.Fact Revenue.Order Day Dt] [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.SAMP_REVENUE_F#map4] [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F.ORDER_DAY_DT] |
| Order Number | Source "SAMP_REVENUE_F": `SAMP_REVENUE_F.ORDER_NUMBER`. In 1 of the 5 sources | DOUBLE, precision 15 | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Order Number] [kb:sampleapp::pres::Sample Sales.Fact Revenue.Order Number] [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.SAMP_REVENUE_F#map5] [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F.ORDER_NUMBER] |
| Order Status | Source "SAMP_REVENUE_F": `SAMP_REVENUE_F.ORDER_STATUS`. In 1 of the 5 sources | VARCHAR, precision 11 | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Order Status] [kb:sampleapp::pres::Sample Sales.Fact Revenue.Order Status] [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.SAMP_REVENUE_F#map6] [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F.ORDER_STATUS] |
| Order Type | Source "SAMP_REVENUE_F": `SAMP_REVENUE_F.ORDER_TYPE`. In 1 of the 5 sources | VARCHAR, precision 8 | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Order Type] [kb:sampleapp::pres::Sample Sales.Fact Revenue.Order Type] [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.SAMP_REVENUE_F#map7] [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F.ORDER_TYPE] |
| Prod Key | Source "Aggregate Revenue": `SAMP_REVENUE_A.PROD_KEY`. Source "SAMP_REVENUE_F": `SAMP_REVENUE_F.PROD_KEY`. In 2 of the 5 sources | DOUBLE, precision 15, in both | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Prod Key] [kb:sampleapp::pres::Sample Sales.Fact Revenue.Prod Key] [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue#map2] [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.SAMP_REVENUE_F#map8] [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A.PROD_KEY] [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F.PROD_KEY] |
| Preferred Currency | No database column: it is calculated from other fields, and is in 0 of the 5 sources. Formula below | not recorded | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Preferred Currency] [kb:sampleapp::pres::Sample Sales.Fact Revenue.Preferred Currency] |

The formula of "Preferred Currency", exactly as recorded [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Preferred Currency]:

```
 INDEXCOL( CASE  VALUEOF(NQ_SESSION."PREFERRED_CURRENCY") WHEN 'gc1' THEN 0 WHEN 'gc2' THEN 1 WHEN 'gc3' THEN 2 END , "SAMPLESALES"."Fact Revenue"."Revenue", "SAMPLESALES"."Fact Revenue"."Revenue (EUR)", "SAMPLESALES"."Fact Revenue"."Revenue (GBP)")
```

In plain words: the formula reads the value "PREFERRED_CURRENCY", which is set per user at login. It turns `'gc1'` into 0, `'gc2'` into 1 and `'gc3'` into 2, and uses that number to pick one of three measures in the order listed: "Revenue", "Revenue (EUR)", "Revenue (GBP)" [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Preferred Currency]. The formula has no branch for any other value, and it does not list "Revenue (INR)" [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Preferred Currency]. What it returns for another value is not recorded (open question Q6). The per-user value itself is in the section Values set per user or for the whole system.

### Where the data comes from

All 5 sources are recorded as active.

| Source | Level of detail | Database table | Priority | Evidence |
|---|---|---|---|---|
| Aggregate Revenue | see below | `SAMP_REVENUE_A` | priority group `1` | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue] [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A] |
| Aggregate Revenue By Month | ` GROUPBYLEVEL("SAMPLESALES"."Time Hierarchy"."Month")` | `SAMP_REVENUE_A_MTH` | priority group not recorded | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue By Month] [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A_MTH] |
| Aggregate Revenue by Year | ` GROUPBYLEVEL("SAMPLESALES"."Time Hierarchy"."Year")` | `SAMP_REVENUE_A_YR` | priority group not recorded | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue by Year] [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A_YR] |
| FactCrossJoin | not recorded | "FactCrossJoin", which is a query and not a database table (see below) | priority group not recorded | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.FactCrossJoin] [kb:sampleapp::phys::xe.SAMPLESALES.FactCrossJoin] |
| SAMP_REVENUE_F | see below | `SAMP_REVENUE_F` | priority group not recorded | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.SAMP_REVENUE_F] [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F] |

A priority group that is not recorded is empty in the export. It does not mean lowest.

The level of detail of the source "Aggregate Revenue", exactly as recorded [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue]:

```
 GROUPBYLEVEL("SAMPLESALES"."Time Hierarchy"."Month", "SAMPLESALES"."Products Hierararchy"."Product Detail")
```

That is the level "Month" of the drill path "Time Hierarchy" and the level "Product Detail" of the drill path "Products Hierararchy" [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue].

The level of detail of the source "SAMP_REVENUE_F", exactly as recorded [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.SAMP_REVENUE_F]:

```
 GROUPBYLEVEL("SAMPLESALES"."Sales Rep Hierarchy"."Detail", "SAMPLESALES"."Time Hierarchy"."Dim Time Detail", "SAMPLESALES"."Office Hierarchy"."Office Detail", "SAMPLESALES"."Products Hierararchy"."Product Detail")
```

That is the level "Detail" of "Sales Rep Hierarchy", the level "Dim Time Detail" of "Time Hierarchy", the level "Office Detail" of "Office Hierarchy" and the level "Product Detail" of "Products Hierararchy" [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.SAMP_REVENUE_F]. The source "Aggregate Revenue By Month" is at the level "Month" of "Time Hierarchy", and the source "Aggregate Revenue by Year" at the level "Year" of "Time Hierarchy" [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue By Month] [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue by Year]. The section Drill paths says what each level is.

The database tables are all in `xe.SAMPLESALES`:

| Database table | Kind as recorded | Its object in the database (database type `DefaultMulDB`) | Evidence |
|---|---|---|---|
| SAMP_REVENUE_A | type `none` | `SAMP_REVENUE_A` | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A] [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A.def.DefaultMulDB] |
| SAMP_REVENUE_A_MTH | type `none` | not recorded | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A_MTH] |
| SAMP_REVENUE_A_YR | type `none` | `SAMP_REVENUE_A_YR` | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A_YR] [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A_YR.def.DefaultMulDB] |
| SAMP_REVENUE_F | type `none` | `SAMP_REVENUE_F` | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F] [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F.def.DefaultMulDB] |
| FactCrossJoin | type `select`: a query kept in the repository, not a database table | the query below | [kb:sampleapp::phys::xe.SAMPLESALES.FactCrossJoin] [kb:sampleapp::phys::xe.SAMPLESALES.FactCrossJoin.def.DefaultMulDB] |

The query behind "FactCrossJoin", for the database type `DefaultMulDB`, is `select 1 Preserve from dual` [kb:sampleapp::phys::xe.SAMPLESALES.FactCrossJoin.def.DefaultMulDB]. In plain words it selects the fixed number 1, under the column name Preserve, from `dual`; it names no table of this subject area.

### Points to note

- "Revenue" and "Units" are fed by four sources at different levels of detail, and a priority group is recorded for only one of them, "Aggregate Revenue" [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Revenue] [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Units] [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue]. Which source answers a given query, and whether the four hold the same totals, is not recorded (open question Q4).
- "All Months Revenue" and "All Products Revenue" have the same rule and the same database columns as each other, and the same columns as "Revenue" in those two sources [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.All Months Revenue] [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.All Products Revenue] (open question Q2).
- "Revenue (EUR)", "Revenue (GBP)" and "Revenue (INR)" are multiplied by a fixed number in the source "SAMP_REVENUE_F" and not multiplied in the source "Aggregate Revenue" [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.SAMP_REVENUE_F#map15] [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue#map8]. As recorded, the same measure can therefore return different numbers depending on the source used (open question Q3).
- "Preferred Currency" is shown to report authors, is calculated from a value set per user at login, and picks among "Revenue" and two measures that report authors do not see [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Preferred Currency]. It is recorded as not a measure, although the three fields it picks from are measures [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Preferred Currency] (open question Q6).
- The measure "Discnt Value" is fed only by the sources "Aggregate Revenue" and "SAMP_REVENUE_F" [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Discnt Value]. The sources "Aggregate Revenue By Month" and "Aggregate Revenue by Year" feed a different field, "DISCNT_VALUE", which is not a measure and which report authors do not see [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.DISCNT_VALUE].
- "Preserve" is the only field fed by the source "FactCrossJoin", whose data is the fixed query `select 1 Preserve from dual` and not a database table [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.FactCrossJoin#map0] [kb:sampleapp::phys::xe.SAMPLESALES.FactCrossJoin.def.DefaultMulDB] (open question Q5).
- The same database column is recorded with different precision in different tables, for example REVENUE with precision 22 in `SAMP_REVENUE_A_MTH`, 8 in `SAMP_REVENUE_A` and 7 in `SAMP_REVENUE_F` [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A_MTH.REVENUE] [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A.REVENUE] [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F.REVENUE].

## Fact Target

Fact Target is a business table of the model "SAMPLESALES", and the repository declares 1 source for it [kb:sampleapp::bmm::SAMPLESALES.Fact Target]. It has 5 fields, 2 of them measures, and report authors see all 5 (counted).

### Measures

| Measure | How it is summarised | Comes from | Evidence |
|---|---|---|---|
| Revenue | Sum: ` SUM("SAMPLESALES"."Fact Target"."Revenue")` | `SAMP_TARGETS_F.REVENUE` (DOUBLE, precision 8) | [kb:sampleapp::bmm::SAMPLESALES.Fact Target.Revenue] [kb:sampleapp::pres::Sample Sales.Fact Target.Revenue] [kb:sampleapp::bmm::SAMPLESALES.Fact Target.Revenue#aggr0] [kb:sampleapp::bmm::SAMPLESALES.Fact Target.src.SAMP_TARGETS_F#map3] [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TARGETS_F.REVENUE] |
| Units | Sum: ` SUM("SAMPLESALES"."Fact Target"."Units")` | `SAMP_TARGETS_F.UNITS` (DOUBLE, precision 15) | [kb:sampleapp::bmm::SAMPLESALES.Fact Target.Units] [kb:sampleapp::pres::Sample Sales.Fact Target.Units] [kb:sampleapp::bmm::SAMPLESALES.Fact Target.Units#aggr0] [kb:sampleapp::bmm::SAMPLESALES.Fact Target.src.SAMP_TARGETS_F#map4] [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TARGETS_F.UNITS] |

Both measures are recorded as writeable: OBIEE lets a user type a value in [kb:sampleapp::bmm::SAMPLESALES.Fact Target.Revenue] [kb:sampleapp::bmm::SAMPLESALES.Fact Target.Units]. Who may do so is in the section Security. What the two measures hold in business terms, and what happens to a typed value, is not recorded (open question Q7).

The section What report authors see says which measure OBIEE uses when a query names none.

### Fields

These fields are recorded as not measures and not writeable.

| Field | Database column | Type | Evidence |
|---|---|---|---|
| Bill Mth Key | `SAMP_TARGETS_F.BILL_MTH_KEY` | DOUBLE, precision 15 | [kb:sampleapp::bmm::SAMPLESALES.Fact Target.Bill Mth Key] [kb:sampleapp::pres::Sample Sales.Fact Target.Bill Mth Key] [kb:sampleapp::bmm::SAMPLESALES.Fact Target.src.SAMP_TARGETS_F#map0] [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TARGETS_F.BILL_MTH_KEY] |
| Office Key | `SAMP_TARGETS_F.OFFICE_KEY` | DOUBLE, precision 15 | [kb:sampleapp::bmm::SAMPLESALES.Fact Target.Office Key] [kb:sampleapp::pres::Sample Sales.Fact Target.Office Key] [kb:sampleapp::bmm::SAMPLESALES.Fact Target.src.SAMP_TARGETS_F#map1] [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TARGETS_F.OFFICE_KEY] |
| Prod Key | `SAMP_TARGETS_F.PROD_KEY` | DOUBLE, precision 15 | [kb:sampleapp::bmm::SAMPLESALES.Fact Target.Prod Key] [kb:sampleapp::pres::Sample Sales.Fact Target.Prod Key] [kb:sampleapp::bmm::SAMPLESALES.Fact Target.src.SAMP_TARGETS_F#map2] [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TARGETS_F.PROD_KEY] |

### Where the data comes from

| Source | Level of detail | Database table | Priority | Evidence |
|---|---|---|---|---|
| SAMP_TARGETS_F (active) | see below | `SAMP_TARGETS_F` in `xe.SAMPLESALES`, type `none` | priority group not recorded | [kb:sampleapp::bmm::SAMPLESALES.Fact Target.src.SAMP_TARGETS_F] [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TARGETS_F] |

The level of detail of the source, exactly as recorded [kb:sampleapp::bmm::SAMPLESALES.Fact Target.src.SAMP_TARGETS_F]:

```
 GROUPBYLEVEL("SAMPLESALES"."Time Hierarchy"."Dim Time Detail", "SAMPLESALES"."Office Hierarchy"."Office Detail", "SAMPLESALES"."Products Hierararchy"."Product Detail")
```

That is the level "Dim Time Detail" of the drill path "Time Hierarchy", the level "Office Detail" of "Office Hierarchy" and the level "Product Detail" of "Products Hierararchy" [kb:sampleapp::bmm::SAMPLESALES.Fact Target.src.SAMP_TARGETS_F]. The section Drill paths says what each level is.

The object in the database behind the table, for the database type `DefaultMulDB`, is `SAMP_TARGETS_F` [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TARGETS_F.def.DefaultMulDB].

### Points to note

- Both measures can be typed into [kb:sampleapp::bmm::SAMPLESALES.Fact Target.Revenue] [kb:sampleapp::bmm::SAMPLESALES.Fact Target.Units] (open question Q7).
- The level of detail of the source names levels of three drill paths only: "Time Hierarchy", "Office Hierarchy" and "Products Hierararchy" [kb:sampleapp::bmm::SAMPLESALES.Fact Target.src.SAMP_TARGETS_F].

## Dim Offices

Dim Offices is a business table of the model "SAMPLESALES", and the repository declares 1 source for it [kb:sampleapp::bmm::SAMPLESALES.Dim Offices]. It has 10 fields, none of them a measure; report authors see 9 and do not see 1 (counted).

### Measures and fields report authors do not see

| Field | Database column | Type | Evidence |
|---|---|---|---|
| Sequence | `SAMP_OFFICES_D.SEQUENCE` | DOUBLE, precision 15, may be empty | [kb:sampleapp::bmm::SAMPLESALES.Dim Offices.Sequence] [kb:sampleapp::bmm::SAMPLESALES.Dim Offices.src.SAMP_OFFICES_D#map8] [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_OFFICES_D.SEQUENCE] |

"Sequence" has no formula: it is the database column as it is [kb:sampleapp::bmm::SAMPLESALES.Dim Offices.src.SAMP_OFFICES_D#map8].

### Fields

All fields are recorded as not measures and not writeable, and each is fed by the one source. "May be empty" is written where the database column is recorded as nullable; where nothing is written, that property is not recorded.

| Field | Database column | Type | Evidence |
|---|---|---|---|
| Area Name | `SAMP_OFFICES_D.AREA_NAME` | VARCHAR, precision 25, may be empty | [kb:sampleapp::bmm::SAMPLESALES.Dim Offices.Area Name] [kb:sampleapp::pres::Sample Sales.Dim Offices.Area Name] [kb:sampleapp::bmm::SAMPLESALES.Dim Offices.src.SAMP_OFFICES_D#map9] [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_OFFICES_D.AREA_NAME] |
| Company | `SAMP_OFFICES_D.COMPANY` | VARCHAR, precision 14, may be empty | [kb:sampleapp::bmm::SAMPLESALES.Dim Offices.Company] [kb:sampleapp::pres::Sample Sales.Dim Offices.Company] [kb:sampleapp::bmm::SAMPLESALES.Dim Offices.src.SAMP_OFFICES_D#map0] [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_OFFICES_D.COMPANY] |
| Company Key | `SAMP_OFFICES_D.COMPANY_KEY` | DOUBLE, precision 15 | [kb:sampleapp::bmm::SAMPLESALES.Dim Offices.Company Key] [kb:sampleapp::pres::Sample Sales.Dim Offices.Company Key] [kb:sampleapp::bmm::SAMPLESALES.Dim Offices.src.SAMP_OFFICES_D#map1] [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_OFFICES_D.COMPANY_KEY] |
| Department | `SAMP_OFFICES_D.DEPARTMENT` | VARCHAR, precision 25, may be empty | [kb:sampleapp::bmm::SAMPLESALES.Dim Offices.Department] [kb:sampleapp::pres::Sample Sales.Dim Offices.Department] [kb:sampleapp::bmm::SAMPLESALES.Dim Offices.src.SAMP_OFFICES_D#map2] [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_OFFICES_D.DEPARTMENT] |
| Dept Key | `SAMP_OFFICES_D.DEPT_KEY` | DOUBLE, precision 15 | [kb:sampleapp::bmm::SAMPLESALES.Dim Offices.Dept Key] [kb:sampleapp::pres::Sample Sales.Dim Offices.Dept Key] [kb:sampleapp::bmm::SAMPLESALES.Dim Offices.src.SAMP_OFFICES_D#map3] [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_OFFICES_D.DEPT_KEY] |
| Office Dsc | `SAMP_OFFICES_D.OFFICE_DSC` | VARCHAR, precision 17, may be empty | [kb:sampleapp::bmm::SAMPLESALES.Dim Offices.Office Dsc] [kb:sampleapp::pres::Sample Sales.Dim Offices.Office Dsc] [kb:sampleapp::bmm::SAMPLESALES.Dim Offices.src.SAMP_OFFICES_D#map4] [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_OFFICES_D.OFFICE_DSC] |
| Office Key | `SAMP_OFFICES_D.OFFICE_KEY` | DOUBLE, precision 15 | [kb:sampleapp::bmm::SAMPLESALES.Dim Offices.Office Key] [kb:sampleapp::pres::Sample Sales.Dim Offices.Office Key] [kb:sampleapp::bmm::SAMPLESALES.Dim Offices.src.SAMP_OFFICES_D#map5] [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_OFFICES_D.OFFICE_KEY] |
| Org Key | `SAMP_OFFICES_D.ORG_KEY` | DOUBLE, precision 15 | [kb:sampleapp::bmm::SAMPLESALES.Dim Offices.Org Key] [kb:sampleapp::pres::Sample Sales.Dim Offices.Org Key] [kb:sampleapp::bmm::SAMPLESALES.Dim Offices.src.SAMP_OFFICES_D#map6] [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_OFFICES_D.ORG_KEY] |
| Organization | `SAMP_OFFICES_D.ORGANIZATION` | VARCHAR, precision 18, may be empty | [kb:sampleapp::bmm::SAMPLESALES.Dim Offices.Organization] [kb:sampleapp::pres::Sample Sales.Dim Offices.Organization] [kb:sampleapp::bmm::SAMPLESALES.Dim Offices.src.SAMP_OFFICES_D#map7] [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_OFFICES_D.ORGANIZATION] |

### Where the data comes from

| Source | Level of detail | Database table | Priority | Evidence |
|---|---|---|---|---|
| SAMP_OFFICES_D (active) | ` GROUPBYLEVEL("SAMPLESALES"."Office Hierarchy"."Office Detail")` | `SAMP_OFFICES_D` in `xe.SAMPLESALES`, type `none` | priority group not recorded | [kb:sampleapp::bmm::SAMPLESALES.Dim Offices.src.SAMP_OFFICES_D] [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_OFFICES_D] |

The level of detail is the level "Office Detail" of the drill path "Office Hierarchy" [kb:sampleapp::bmm::SAMPLESALES.Dim Offices.src.SAMP_OFFICES_D]. The section Drill paths says what that level is.

## Dim Products

Dim Products is a business table of the model "SAMPLESALES", and the repository declares 1 source for it [kb:sampleapp::bmm::SAMPLESALES.Dim Products]. It has 12 fields, none of them a measure; report authors see 9 and do not see 3 (counted).

### Measures and fields report authors do not see

Each of the three is the database column as it is, with no formula.

| Field | Database column | Type | Evidence |
|---|---|---|---|
| Attribute 1 | `SAMP_PRODUCTS_D.ATTRIBUTE_1` | VARCHAR, precision 7, may be empty | [kb:sampleapp::bmm::SAMPLESALES.Dim Products.Attribute 1] [kb:sampleapp::bmm::SAMPLESALES.Dim Products.src.SAMP_PRODUCTS_D#map0] [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_PRODUCTS_D.ATTRIBUTE_1] |
| Attribute 2 | `SAMP_PRODUCTS_D.ATTRIBUTE_2` | VARCHAR, precision 6, may be empty | [kb:sampleapp::bmm::SAMPLESALES.Dim Products.Attribute 2] [kb:sampleapp::bmm::SAMPLESALES.Dim Products.src.SAMP_PRODUCTS_D#map1] [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_PRODUCTS_D.ATTRIBUTE_2] |
| Sequence | `SAMP_PRODUCTS_D.SEQUENCE` | DOUBLE, precision 15 | [kb:sampleapp::bmm::SAMPLESALES.Dim Products.Sequence] [kb:sampleapp::bmm::SAMPLESALES.Dim Products.src.SAMP_PRODUCTS_D#map8] [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_PRODUCTS_D.SEQUENCE] |

### Fields

All fields are recorded as not measures and not writeable. "May be empty" is written where the database column is recorded as nullable; where nothing is written, that property is not recorded.

| Field | Database column | Type | Evidence |
|---|---|---|---|
| Brand | `SAMP_PRODUCTS_D.BRAND` | VARCHAR, precision 8, may be empty | [kb:sampleapp::bmm::SAMPLESALES.Dim Products.Brand] [kb:sampleapp::pres::Sample Sales.Dim Products.Brand] [kb:sampleapp::bmm::SAMPLESALES.Dim Products.src.SAMP_PRODUCTS_D#map2] [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_PRODUCTS_D.BRAND] |
| Brand Key | `SAMP_PRODUCTS_D.BRAND_KEY` | DOUBLE, precision 15 | [kb:sampleapp::bmm::SAMPLESALES.Dim Products.Brand Key] [kb:sampleapp::pres::Sample Sales.Dim Products.Brand Key] [kb:sampleapp::bmm::SAMPLESALES.Dim Products.src.SAMP_PRODUCTS_D#map3] [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_PRODUCTS_D.BRAND_KEY] |
| Lob | `SAMP_PRODUCTS_D.LOB` | VARCHAR, precision 13, may be empty | [kb:sampleapp::bmm::SAMPLESALES.Dim Products.Lob] [kb:sampleapp::pres::Sample Sales.Dim Products.Lob] [kb:sampleapp::bmm::SAMPLESALES.Dim Products.src.SAMP_PRODUCTS_D#map4] [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_PRODUCTS_D.LOB] |
| Lob Key | `SAMP_PRODUCTS_D.LOB_KEY` | DOUBLE, precision 15 | [kb:sampleapp::bmm::SAMPLESALES.Dim Products.Lob Key] [kb:sampleapp::pres::Sample Sales.Dim Products.Lob Key] [kb:sampleapp::bmm::SAMPLESALES.Dim Products.src.SAMP_PRODUCTS_D#map5] [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_PRODUCTS_D.LOB_KEY] |
| Prod Dsc | `SAMP_PRODUCTS_D.PROD_DSC` | VARCHAR, precision 26, may be empty | [kb:sampleapp::bmm::SAMPLESALES.Dim Products.Prod Dsc] [kb:sampleapp::pres::Sample Sales.Dim Products.Prod Dsc] [kb:sampleapp::bmm::SAMPLESALES.Dim Products.src.SAMP_PRODUCTS_D#map6] [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_PRODUCTS_D.PROD_DSC] |
| Prod Dsc (Translated) | No database column: it is calculated from other fields, and is in 0 of the 1 sources. Formula below | not recorded | [kb:sampleapp::bmm::SAMPLESALES.Dim Products.Prod Dsc (Translated)] [kb:sampleapp::pres::Sample Sales.Dim Products.Prod Dsc (Translated)] |
| Prod Key | `SAMP_PRODUCTS_D.PROD_KEY` | DOUBLE, precision 15 | [kb:sampleapp::bmm::SAMPLESALES.Dim Products.Prod Key] [kb:sampleapp::pres::Sample Sales.Dim Products.Prod Key] [kb:sampleapp::bmm::SAMPLESALES.Dim Products.src.SAMP_PRODUCTS_D#map7] [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_PRODUCTS_D.PROD_KEY] |
| Type | `SAMP_PRODUCTS_D.TYPE` | VARCHAR, precision 12, may be empty | [kb:sampleapp::bmm::SAMPLESALES.Dim Products.Type] [kb:sampleapp::pres::Sample Sales.Dim Products.Type] [kb:sampleapp::bmm::SAMPLESALES.Dim Products.src.SAMP_PRODUCTS_D#map9] [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_PRODUCTS_D.TYPE] |
| Type Key | `SAMP_PRODUCTS_D.TYPE_KEY` | DOUBLE, precision 15 | [kb:sampleapp::bmm::SAMPLESALES.Dim Products.Type Key] [kb:sampleapp::pres::Sample Sales.Dim Products.Type Key] [kb:sampleapp::bmm::SAMPLESALES.Dim Products.src.SAMP_PRODUCTS_D#map10] [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_PRODUCTS_D.TYPE_KEY] |

The formula of "Prod Dsc (Translated)", exactly as recorded [kb:sampleapp::bmm::SAMPLESALES.Dim Products.Prod Dsc (Translated)]:

```
Lookup(SPARSE   "SAMPLESALES"."Dim Products (Translated)"."PROD_DESC" ,  "SAMPLESALES"."Dim Products"."Prod Dsc"  , "SAMPLESALES"."Dim Products"."Prod Key", VALUEOF(NQ_SESSION.WEBLANGUAGE) ) 
```

In plain words: the formula looks up the field "PROD_DESC" of the business table "Dim Products (Translated)", using the field "Prod Key" of this table and the per-user value "WEBLANGUAGE", and it also names the field "Prod Dsc" of this table [kb:sampleapp::bmm::SAMPLESALES.Dim Products.Prod Dsc (Translated)]. In OBIEE generally, a lookup written with the word SPARSE returns the looked-up text when a row is found and the second item listed (here "Prod Dsc") when none is found. Which values "WEBLANGUAGE" takes, and for which of them a translation exists, is not recorded (open question Q8). The business table "Dim Products (Translated)" has a section of its own.

### Where the data comes from

| Source | Level of detail | Database table | Priority | Evidence |
|---|---|---|---|---|
| SAMP_PRODUCTS_D (active) | ` GROUPBYLEVEL("SAMPLESALES"."Products Hierararchy"."Product Detail")` | `SAMP_PRODUCTS_D` in `xe.SAMPLESALES`, type `none` | priority group not recorded | [kb:sampleapp::bmm::SAMPLESALES.Dim Products.src.SAMP_PRODUCTS_D] [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_PRODUCTS_D] |

The level of detail is the level "Product Detail" of the drill path "Products Hierararchy" (the name is spelled this way in the repository) [kb:sampleapp::bmm::SAMPLESALES.Dim Products.src.SAMP_PRODUCTS_D]. The section Drill paths says what that level is.

### Points to note

- "Prod Dsc (Translated)" is shown to report authors and its formula reads a value set per user at login, "WEBLANGUAGE" [kb:sampleapp::bmm::SAMPLESALES.Dim Products.Prod Dsc (Translated)] (open question Q8).

## Dim Products (Translated)

Dim Products (Translated) is a business table of the model "SAMPLESALES", and the repository declares 1 source for it [kb:sampleapp::bmm::SAMPLESALES.Dim Products (Translated)]. It has 3 fields, none of them a measure, and report authors see none of the 3 in this subject area (counted). It is described because the business table is part of the model behind the subject area (as the export states for each of its fields). The section Dim Products quotes a formula that names its field "PROD_DESC".

### Measures and fields report authors do not see

Each field is recorded as not a measure and not writeable, and is the database column as it is, with no formula.

| Field | Database column | Type | Evidence |
|---|---|---|---|
| LANG_ID | `SAMP_PRODUCTS_D_TRANSLATION.LANG_ID` | VARCHAR, precision 2 | [kb:sampleapp::bmm::SAMPLESALES.Dim Products (Translated).LANG_ID] [kb:sampleapp::bmm::SAMPLESALES.Dim Products (Translated).src.SAMP_PRODUCTS_D_TRANSLATION#map0] [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_PRODUCTS_D_TRANSLATION.LANG_ID] |
| PROD_DESC | `SAMP_PRODUCTS_D_TRANSLATION.PROD_DESC` | VARCHAR, precision 50 | [kb:sampleapp::bmm::SAMPLESALES.Dim Products (Translated).PROD_DESC] [kb:sampleapp::bmm::SAMPLESALES.Dim Products (Translated).src.SAMP_PRODUCTS_D_TRANSLATION#map1] [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_PRODUCTS_D_TRANSLATION.PROD_DESC] |
| PROD_KEY | `SAMP_PRODUCTS_D_TRANSLATION.PROD_KEY` | DOUBLE, precision 15 | [kb:sampleapp::bmm::SAMPLESALES.Dim Products (Translated).PROD_KEY] [kb:sampleapp::bmm::SAMPLESALES.Dim Products (Translated).src.SAMP_PRODUCTS_D_TRANSLATION#map2] [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_PRODUCTS_D_TRANSLATION.PROD_KEY] |

### Where the data comes from

| Source | Level of detail | Database table | Priority | Evidence |
|---|---|---|---|---|
| SAMP_PRODUCTS_D_TRANSLATION (active) | not recorded | `SAMP_PRODUCTS_D_TRANSLATION` in `xe.SAMPLESALES`, type `none` | priority group not recorded | [kb:sampleapp::bmm::SAMPLESALES.Dim Products (Translated).src.SAMP_PRODUCTS_D_TRANSLATION] [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_PRODUCTS_D_TRANSLATION] |

The object in the database behind the table, for the database type `DefaultMulDB`, is `SAMP_PRODUCTS_D_TRANSLATION` [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_PRODUCTS_D_TRANSLATION.def.DefaultMulDB].

### Points to note

- Which values "LANG_ID" holds is not recorded. This is part of open question Q8; the query that answers it is `select LANG_ID, count(*) from SAMP_PRODUCTS_D_TRANSLATION group by LANG_ID`, run against the schema SAMPLESALES.

## Dim Sales Rep

Dim Sales Rep is a business table of the model "SAMPLESALES", and the repository declares 1 source for it [kb:sampleapp::bmm::SAMPLESALES.Dim Sales Rep]. It has 5 fields, none of them a measure, and report authors see all 5 (counted).

### Fields

All fields are recorded as not measures and not writeable. "May be empty" is written where the database column is recorded as nullable; where nothing is written, that property is not recorded.

| Field | Database column | Type | Evidence |
|---|---|---|---|
| Distance | `EMP_PARENT_CHILD.DISTANCE` | INT | [kb:sampleapp::bmm::SAMPLESALES.Dim Sales Rep.Distance] [kb:sampleapp::pres::Sample Sales.Dim Sales Rep.Distance] [kb:sampleapp::bmm::SAMPLESALES.Dim Sales Rep.src.LTS Employee#map4] [kb:sampleapp::phys::xe.SAMPLESALES.EMP_PARENT_CHILD.DISTANCE] |
| Employee ID | `EMPLOYEE.EMP_ID` | DOUBLE, precision 15 | [kb:sampleapp::bmm::SAMPLESALES.Dim Sales Rep.Employee ID] [kb:sampleapp::pres::Sample Sales.Dim Sales Rep.Employee ID] [kb:sampleapp::bmm::SAMPLESALES.Dim Sales Rep.src.LTS Employee#map0] [kb:sampleapp::phys::xe.SAMPLESALES.EMPLOYEE.EMP_ID] |
| Employee Name | `EMPLOYEE.EMP_NAME` | VARCHAR, precision 40, may be empty | [kb:sampleapp::bmm::SAMPLESALES.Dim Sales Rep.Employee Name] [kb:sampleapp::pres::Sample Sales.Dim Sales Rep.Employee Name] [kb:sampleapp::bmm::SAMPLESALES.Dim Sales Rep.src.LTS Employee#map1] [kb:sampleapp::phys::xe.SAMPLESALES.EMPLOYEE.EMP_NAME] |
| Manager ID | `EMPLOYEE.MANAGER_ID` | DOUBLE, precision 15, may be empty | [kb:sampleapp::bmm::SAMPLESALES.Dim Sales Rep.Manager ID] [kb:sampleapp::pres::Sample Sales.Dim Sales Rep.Manager ID] [kb:sampleapp::bmm::SAMPLESALES.Dim Sales Rep.src.LTS Employee#map2] [kb:sampleapp::phys::xe.SAMPLESALES.EMPLOYEE.MANAGER_ID] |
| Office ID | `EMPLOYEE.OFFICE_ID` | DOUBLE, precision 15, may be empty | [kb:sampleapp::bmm::SAMPLESALES.Dim Sales Rep.Office ID] [kb:sampleapp::pres::Sample Sales.Dim Sales Rep.Office ID] [kb:sampleapp::bmm::SAMPLESALES.Dim Sales Rep.src.LTS Employee#map3] [kb:sampleapp::phys::xe.SAMPLESALES.EMPLOYEE.OFFICE_ID] |

### Where the data comes from

| Source | Level of detail | Database table | Priority | Evidence |
|---|---|---|---|---|
| LTS Employee (active) | ` GROUPBYLEVEL("SAMPLESALES"."Sales Rep Hierarchy"."Detail")` | `EMPLOYEE` and `EMP_PARENT_CHILD`, both in `xe.SAMPLESALES`, both type `none` | priority group not recorded | [kb:sampleapp::bmm::SAMPLESALES.Dim Sales Rep.src.LTS Employee] [kb:sampleapp::phys::xe.SAMPLESALES.EMPLOYEE] [kb:sampleapp::phys::xe.SAMPLESALES.EMP_PARENT_CHILD] |

The level of detail is the level "Detail" of the drill path "Sales Rep Hierarchy" [kb:sampleapp::bmm::SAMPLESALES.Dim Sales Rep.src.LTS Employee]. The section Drill paths says what that level is.

The object in the database behind `EMP_PARENT_CHILD`, for the database type `DefaultMulDB`, is `EMP_PARENT_CHILD` [kb:sampleapp::phys::xe.SAMPLESALES.EMP_PARENT_CHILD.def.DefaultMulDB]. For `EMPLOYEE` the object in the database is not recorded [kb:sampleapp::phys::xe.SAMPLESALES.EMPLOYEE].

### Points to note

- The one source reads two database tables: four fields come from `EMPLOYEE` and the field "Distance" comes from `EMP_PARENT_CHILD` [kb:sampleapp::bmm::SAMPLESALES.Dim Sales Rep.src.LTS Employee#map4] [kb:sampleapp::bmm::SAMPLESALES.Dim Sales Rep.src.LTS Employee#map0]. How the source combines the two tables, and so how many rows it holds per employee, is not stated with the source; the database joins are in the section How the tables relate (open question Q9).

## Dim Time

Dim Time is a business table of the model "SAMPLESALES", and the repository declares 2 sources for it [kb:sampleapp::bmm::SAMPLESALES.Dim Time]. It has 6 fields, none of them a measure, and report authors see all 6 (counted).

### Fields

All fields are recorded as not measures and not writeable. "May be empty" is written where the database column is recorded as nullable; where nothing is written, that property is not recorded.

| Field | Database column | Type | Evidence |
|---|---|---|---|
| Calendar Date | Source "LTS Time Day": `SAMP_TIME_DAY_D.CALENDAR_DATE`. In 1 of the 2 sources | DATE | [kb:sampleapp::bmm::SAMPLESALES.Dim Time.Calendar Date] [kb:sampleapp::pres::Sample Sales.Dim Time.Calendar Date] [kb:sampleapp::bmm::SAMPLESALES.Dim Time.src.LTS Time Day#map5] [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_DAY_D.CALENDAR_DATE] |
| Per Name Half | Source "LTS Time Day": `SAMP_TIME_DAY_D.PER_NAME_HALF`. Source "LTS Time Month": `SAMP_TIME_MTH_D.PER_NAME_HALF`. In 2 of the 2 sources | VARCHAR, precision 8, in both; may be empty in SAMP_TIME_MTH_D | [kb:sampleapp::bmm::SAMPLESALES.Dim Time.Per Name Half] [kb:sampleapp::pres::Sample Sales.Dim Time.Per Name Half] [kb:sampleapp::bmm::SAMPLESALES.Dim Time.src.LTS Time Day#map2] [kb:sampleapp::bmm::SAMPLESALES.Dim Time.src.LTS Time Month#map1] [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_DAY_D.PER_NAME_HALF] [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.PER_NAME_HALF] |
| Per Name Month | Source "LTS Time Day": `SAMP_TIME_DAY_D.PER_NAME_MONTH`. Source "LTS Time Month": `SAMP_TIME_MTH_D.PER_NAME_MONTH`. In 2 of the 2 sources | VARCHAR, precision 9, in both; may be empty in SAMP_TIME_MTH_D | [kb:sampleapp::bmm::SAMPLESALES.Dim Time.Per Name Month] [kb:sampleapp::pres::Sample Sales.Dim Time.Per Name Month] [kb:sampleapp::bmm::SAMPLESALES.Dim Time.src.LTS Time Day#map3] [kb:sampleapp::bmm::SAMPLESALES.Dim Time.src.LTS Time Month#map2] [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_DAY_D.PER_NAME_MONTH] [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.PER_NAME_MONTH] |
| Per Name Qtr | Source "LTS Time Day": `SAMP_TIME_DAY_D.PER_NAME_QTR`. Source "LTS Time Month": `SAMP_TIME_MTH_D.PER_NAME_QTR`. In 2 of the 2 sources | VARCHAR, precision 7, in both; may be empty in SAMP_TIME_MTH_D | [kb:sampleapp::bmm::SAMPLESALES.Dim Time.Per Name Qtr] [kb:sampleapp::pres::Sample Sales.Dim Time.Per Name Qtr] [kb:sampleapp::bmm::SAMPLESALES.Dim Time.src.LTS Time Day#map4] [kb:sampleapp::bmm::SAMPLESALES.Dim Time.src.LTS Time Month#map3] [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_DAY_D.PER_NAME_QTR] [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.PER_NAME_QTR] |
| Per Name Week | Source "LTS Time Day": `SAMP_TIME_DAY_D.PER_NAME_WEEK`. In 1 of the 2 sources | VARCHAR, precision 12 | [kb:sampleapp::bmm::SAMPLESALES.Dim Time.Per Name Week] [kb:sampleapp::pres::Sample Sales.Dim Time.Per Name Week] [kb:sampleapp::bmm::SAMPLESALES.Dim Time.src.LTS Time Day#map1] [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_DAY_D.PER_NAME_WEEK] |
| Per Name Year | Source "LTS Time Day": `SAMP_TIME_DAY_D.PER_NAME_YEAR`. Source "LTS Time Month": `SAMP_TIME_MTH_D.PER_NAME_YEAR`. In 2 of the 2 sources | VARCHAR, precision 4, in both; may be empty in SAMP_TIME_MTH_D | [kb:sampleapp::bmm::SAMPLESALES.Dim Time.Per Name Year] [kb:sampleapp::pres::Sample Sales.Dim Time.Per Name Year] [kb:sampleapp::bmm::SAMPLESALES.Dim Time.src.LTS Time Day#map0] [kb:sampleapp::bmm::SAMPLESALES.Dim Time.src.LTS Time Month#map0] [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_DAY_D.PER_NAME_YEAR] [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.PER_NAME_YEAR] |

The name shown to users for each of the six fields is not fixed text. It is recorded as a formula that reads a value set per user at login:

| Field | Name shown to users, as written | Evidence |
|---|---|---|
| Calendar Date | `VALUEOF(NQ_SESSION.CN_Sample_Sales_Dim_Time_Calendar_Date)` | [kb:sampleapp::pres::Sample Sales.Dim Time.Calendar Date] |
| Per Name Half | `VALUEOF(NQ_SESSION.CN_Sample_Sales_Dim_Time_Per_Name_Half)` | [kb:sampleapp::pres::Sample Sales.Dim Time.Per Name Half] |
| Per Name Month | `VALUEOF(NQ_SESSION.CN_Sample_Sales_Dim_Time_Per_Name_Month)` | [kb:sampleapp::pres::Sample Sales.Dim Time.Per Name Month] |
| Per Name Qtr | `VALUEOF(NQ_SESSION.CN_Sample_Sales_Dim_Time_Per_Name_Qtr)` | [kb:sampleapp::pres::Sample Sales.Dim Time.Per Name Qtr] |
| Per Name Week | `VALUEOF(NQ_SESSION.CN_Sample_Sales_Dim_Time_Per_Name_Week)` | [kb:sampleapp::pres::Sample Sales.Dim Time.Per Name Week] |
| Per Name Year | `VALUEOF(NQ_SESSION.CN_Sample_Sales_Dim_Time_Per_Name_Year)` | [kb:sampleapp::pres::Sample Sales.Dim Time.Per Name Year] |

What names users actually see is not recorded (open question Q10). The table's own displayed name is built the same way; see the section What report authors see and open question Q1.

### Where the data comes from

Both sources are recorded as active.

| Source | Level of detail | Database table | Priority | Evidence |
|---|---|---|---|---|
| LTS Time Day | ` GROUPBYLEVEL("SAMPLESALES"."Time Hierarchy"."Dim Time Detail")` | `SAMP_TIME_DAY_D` in `xe.SAMPLESALES`, type `none` | priority group not recorded | [kb:sampleapp::bmm::SAMPLESALES.Dim Time.src.LTS Time Day] [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_DAY_D] |
| LTS Time Month | ` GROUPBYLEVEL("SAMPLESALES"."Time Hierarchy"."Month")` | `SAMP_TIME_MTH_D` in `xe.SAMPLESALES`, type `none` | priority group not recorded | [kb:sampleapp::bmm::SAMPLESALES.Dim Time.src.LTS Time Month] [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D] |

The source "LTS Time Day" is at the level "Dim Time Detail" of the drill path "Time Hierarchy", and the source "LTS Time Month" at its level "Month" [kb:sampleapp::bmm::SAMPLESALES.Dim Time.src.LTS Time Day] [kb:sampleapp::bmm::SAMPLESALES.Dim Time.src.LTS Time Month]. The section Drill paths says what each level is.

The object in the database behind `SAMP_TIME_DAY_D`, for the database type `DefaultMulDB`, is `SAMP_TIME_DAY_D` [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_DAY_D.def.DefaultMulDB]. For `SAMP_TIME_MTH_D` it is not recorded [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D].

### Points to note

- "Calendar Date" and "Per Name Week" exist only in the source "LTS Time Day"; the source "LTS Time Month" does not supply them [kb:sampleapp::bmm::SAMPLESALES.Dim Time.Calendar Date] [kb:sampleapp::bmm::SAMPLESALES.Dim Time.Per Name Week].
- The four fields fed by both sources come from two different database tables, `SAMP_TIME_DAY_D` and `SAMP_TIME_MTH_D`, and no priority is recorded for either source [kb:sampleapp::bmm::SAMPLESALES.Dim Time.src.LTS Time Day] [kb:sampleapp::bmm::SAMPLESALES.Dim Time.src.LTS Time Month].
- The displayed names of all six fields depend on values set per user at login, for example for "Calendar Date" [kb:sampleapp::pres::Sample Sales.Dim Time.Calendar Date] (open question Q10).

## How the tables relate

There are 7 joins between business tables and 17 joins between database tables (counted).

### Joins between business tables

The numbers in brackets are how many rows on each side, as recorded. No join columns are recorded for these joins.

| From | To | Join type | Evidence |
|---|---|---|---|
| Dim Time (0..1) | Fact Target (0..n) | Inner | [kb:sampleapp::bmm::joins.Relationship_2004:11061353928600] |
| Dim Time (0..1) | Fact Revenue (0..n) | Inner | [kb:sampleapp::bmm::joins.Relationship_2004:11091353928666] |
| Dim Sales Rep (1..1) | Fact Revenue (0..n) | Inner | [kb:sampleapp::bmm::joins.Relationship_2004:17511354089145] |
| Fact Revenue (n..n) | Dim Offices (0..1) | Inner | [kb:sampleapp::bmm::joins.Relationship_2004:2111353851711] |
| Fact Revenue (n..n) | Dim Products (0..1) | Inner | [kb:sampleapp::bmm::joins.Relationship_2004:2141353851711] |
| Fact Target (n..n) | Dim Offices (0..1) | Inner | [kb:sampleapp::bmm::joins.Relationship_2004:2301353851711] |
| Fact Target (n..n) | Dim Products (0..1) | Inner | [kb:sampleapp::bmm::joins.Relationship_2004:2331353851711] |

What a reader should see in this table:

- Fact Revenue is joined to four tables: Dim Time, Dim Sales Rep, Dim Offices and Dim Products. Fact Target is joined to three: Dim Time, Dim Offices and Dim Products. No join between Fact Target and Dim Sales Rep is recorded (from the seven joins above).
- No join of the business table "Dim Products (Translated)" is recorded here (from the seven joins above). The section Dim Products shows it is reached by a lookup formula.
- The fact side is recorded as `0..n` in three joins and as `n..n` in four, and the other side as `1..1` for Dim Sales Rep and `0..1` in the six other joins [kb:sampleapp::bmm::joins.Relationship_2004:17511354089145] [kb:sampleapp::bmm::joins.Relationship_2004:2111353851711]. Whether these differences are intended is not recorded (open question Q12).

### Joins between database tables

Three joins are written as a condition and not as a key. All three start at "FactCrossJoin", which is a query and not a database table (see the section Fact Revenue).

| Join | From | To | Rows on each side, as recorded | Join condition | Evidence |
|---|---|---|---|---|---|
| Complex Join | FactCrossJoin | SAMP_OFFICES_D | `ONE TO ONE` | `1=1` | [kb:sampleapp::phys::joins.Complex Join] |
| Complex Join#1 | FactCrossJoin | SAMP_PRODUCTS_D | `ONE TO ONE` | `1=1` | [kb:sampleapp::phys::joins.Complex Join#1] |
| Complex Join#2 | FactCrossJoin | EMPLOYEE | `ONE TO ONE` | `1=1` | [kb:sampleapp::phys::joins.Complex Join#2] |

The condition `1=1` compares no column of either table [kb:sampleapp::phys::joins.Complex Join] [kb:sampleapp::phys::joins.Complex Join#1] [kb:sampleapp::phys::joins.Complex Join#2]. Why this is set up is not recorded (open question Q5).

Fourteen joins are foreign keys. For every one of them the export recorded the table the key is on and the key column, but not the table it points to nor the column on that side (open question Q11).

| Foreign key | On database table | Key column | Points to | Evidence |
|---|---|---|---|---|
| EMP_PARENT_CHILD_FKey | EMP_PARENT_CHILD | ANCESTOR_KEY (DOUBLE, precision 15) | not recorded | [kb:sampleapp::phys::xe.SAMPLESALES.EMP_PARENT_CHILD.EMP_PARENT_CHILD_FKey] |
| SAMP_PRODUCTS_D_TRANSLATION_FKey | SAMP_PRODUCTS_D_TRANSLATION | PROD_KEY (DOUBLE, precision 15) | not recorded | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_PRODUCTS_D_TRANSLATION.SAMP_PRODUCTS_D_TRANSLATION_FKey] |
| SAMP_REVENUE_A_FKey | SAMP_REVENUE_A | BILL_MTH_KEY (DOUBLE, precision 15, may be empty) | not recorded | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A.SAMP_REVENUE_A_FKey] |
| SAMP_REVENUE_A_FKey#1 | SAMP_REVENUE_A | PROD_KEY (DOUBLE, precision 15, may be empty) | not recorded | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A.SAMP_REVENUE_A_FKey#1] |
| SAMP_REVENUE_A_MTH_Foreign Key | SAMP_REVENUE_A_MTH | BILL_MTH_KEY (DOUBLE, precision 22, may be empty) | not recorded | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A_MTH.SAMP_REVENUE_A_MTH_Foreign Key] |
| SAMP_REVENUE_A_YR_Foreign Key | SAMP_REVENUE_A_YR | BILL_YEAR_KEY (DOUBLE, may be empty) | not recorded | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A_YR.SAMP_REVENUE_A_YR_Foreign Key] |
| SAMP_REVENUE_F_FKey | SAMP_REVENUE_F | BILL_MTH_KEY (DOUBLE, precision 15, may be empty) | not recorded | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F.SAMP_REVENUE_F_FKey] |
| SAMP_REVENUE_F_FKey#1 | SAMP_REVENUE_F | OFFICE_KEY (DOUBLE, precision 15, may be empty) | not recorded | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F.SAMP_REVENUE_F_FKey#1] |
| SAMP_REVENUE_F_FKey#2 | SAMP_REVENUE_F | PROD_KEY (DOUBLE, precision 15, may be empty) | not recorded | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F.SAMP_REVENUE_F_FKey#2] |
| SAMP_REVENUE_F_FKey#3 | SAMP_REVENUE_F | BILL_DAY_DT (DATE, may be empty) | not recorded | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F.SAMP_REVENUE_F_FKey#3] |
| SAMP_REVENUE_F_FKey#4 | SAMP_REVENUE_F | EMPLOYEE_KEY (INT) | not recorded | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F.SAMP_REVENUE_F_FKey#4] |
| SAMP_TARGETS_F_FKey | SAMP_TARGETS_F | BILL_MTH_KEY (DOUBLE, precision 15, may be empty) | not recorded | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TARGETS_F.SAMP_TARGETS_F_FKey] |
| SAMP_TARGETS_F_FKey#1 | SAMP_TARGETS_F | OFFICE_KEY (DOUBLE, precision 15, may be empty) | not recorded | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TARGETS_F.SAMP_TARGETS_F_FKey#1] |
| SAMP_TARGETS_F_FKey#2 | SAMP_TARGETS_F | PROD_KEY (DOUBLE, precision 15, may be empty) | not recorded | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TARGETS_F.SAMP_TARGETS_F_FKey#2] |

Because the far side of every foreign key is missing, this document cannot say which database table each fact table is joined to, nor on which column of that table. In particular, how `EMP_PARENT_CHILD` is joined to `EMPLOYEE` (open question Q9) is not settled by these rows: the only key recorded on `EMP_PARENT_CHILD` is on ANCESTOR_KEY and its target is not recorded [kb:sampleapp::phys::xe.SAMPLESALES.EMP_PARENT_CHILD.EMP_PARENT_CHILD_FKey].

## Drill paths

The business model has 4 drill paths, and all 4 are offered to report authors (counted). In the tables below, "Key field" is the field of the business table that identifies a member of the level, and "Members, as recorded" is the number stored in the repository for the level; it is a recorded number, not a count of the data.

### Office Hierarchy

Dim Offices Total → Company → Organization → Department → Office Detail. The drill path has 5 levels and its default top level is "Dim Offices Total" [kb:sampleapp::bmm::SAMPLESALES.Office Hierarchy].

| Level | Key field | Members, as recorded | Shown to report authors as | Evidence |
|---|---|---|---|---|
| 0 Dim Offices Total (grand total) | none recorded | 1 | level "Dim Offices Total", no field | [kb:sampleapp::bmm::SAMPLESALES.Office Hierarchy.Dim Offices Total] [kb:sampleapp::pres::Sample Sales.Dim Offices.Office Hierarchy.Dim Offices Total] |
| 1 Company | "Dim Offices"."Company" | 1 | level "Company", field "Company" | [kb:sampleapp::bmm::SAMPLESALES.Office Hierarchy.Company] [kb:sampleapp::pres::Sample Sales.Dim Offices.Office Hierarchy.Company] [kb:sampleapp::pres::Sample Sales.Dim Offices.Office Hierarchy.Company.0] |
| 2 Organization | "Dim Offices"."Organization" | 1 | level "Organization", field "Organization" | [kb:sampleapp::bmm::SAMPLESALES.Office Hierarchy.Organization] [kb:sampleapp::pres::Sample Sales.Dim Offices.Office Hierarchy.Organization] [kb:sampleapp::pres::Sample Sales.Dim Offices.Office Hierarchy.Organization.0] |
| 3 Department | "Dim Offices"."Department" | 1 | level "Department", field "Department" | [kb:sampleapp::bmm::SAMPLESALES.Office Hierarchy.Department] [kb:sampleapp::pres::Sample Sales.Dim Offices.Office Hierarchy.Department] [kb:sampleapp::pres::Sample Sales.Dim Offices.Office Hierarchy.Department.0] |
| 4 Office Detail | "Dim Offices"."Office Dsc" | 1 | level "Office Detail", field "Office Dsc" | [kb:sampleapp::bmm::SAMPLESALES.Office Hierarchy.Office Detail] [kb:sampleapp::pres::Sample Sales.Dim Offices.Office Hierarchy.Office Detail] [kb:sampleapp::pres::Sample Sales.Dim Offices.Office Hierarchy.Office Detail.0] |

Report authors are offered it as the hierarchy "Office Hierarchy" in the table "Dim Offices", with 5 levels [kb:sampleapp::pres::Sample Sales.Dim Offices.Office Hierarchy].

### Products Hierararchy

Dim Products Total → Brand → Line Of Business → Products Type → Product Detail. The drill path has 5 levels and its default top level is "Dim Products Total"; its name is spelled "Products Hierararchy" in the repository [kb:sampleapp::bmm::SAMPLESALES.Products Hierararchy].

| Level | Key field | Members, as recorded | Shown to report authors as | Evidence |
|---|---|---|---|---|
| 0 Dim Products Total (grand total) | none recorded | 1 | level "Dim Products Total", no field | [kb:sampleapp::bmm::SAMPLESALES.Products Hierararchy.Dim Products Total] [kb:sampleapp::pres::Sample Sales.Dim Products.Products Hierararchy.Dim Products Total] |
| 1 Brand | "Dim Products"."Brand" | 1 | level "Brand", field "Brand" | [kb:sampleapp::bmm::SAMPLESALES.Products Hierararchy.Brand] [kb:sampleapp::pres::Sample Sales.Dim Products.Products Hierararchy.Brand] [kb:sampleapp::pres::Sample Sales.Dim Products.Products Hierararchy.Brand.0] |
| 2 Line Of Business | "Dim Products"."Lob" | 1 | level "Line Of Business", field "Lob" | [kb:sampleapp::bmm::SAMPLESALES.Products Hierararchy.Line Of Business] [kb:sampleapp::pres::Sample Sales.Dim Products.Products Hierararchy.Line Of Business] [kb:sampleapp::pres::Sample Sales.Dim Products.Products Hierararchy.Line Of Business.0] |
| 3 Products Type | "Dim Products"."Type" | 1 | level "Products Type", field "Type" | [kb:sampleapp::bmm::SAMPLESALES.Products Hierararchy.Products Type] [kb:sampleapp::pres::Sample Sales.Dim Products.Products Hierararchy.Products Type] [kb:sampleapp::pres::Sample Sales.Dim Products.Products Hierararchy.Products Type.0] |
| 4 Product Detail | "Dim Products"."Prod Dsc" | 1 | level "Product Detail", field "Prod Dsc" | [kb:sampleapp::bmm::SAMPLESALES.Products Hierararchy.Product Detail] [kb:sampleapp::pres::Sample Sales.Dim Products.Products Hierararchy.Product Detail] [kb:sampleapp::pres::Sample Sales.Dim Products.Products Hierararchy.Product Detail.0] |

Report authors are offered it as the hierarchy "Products Hierararchy" in the table "Dim Products", with 5 levels [kb:sampleapp::pres::Sample Sales.Dim Products.Products Hierararchy].

In both drill paths above, the key field of each level below the total is the same field that the level shows to report authors, for example "Office Dsc" and "Prod Dsc" at the lowest levels [kb:sampleapp::bmm::SAMPLESALES.Office Hierarchy.Office Detail] [kb:sampleapp::bmm::SAMPLESALES.Products Hierararchy.Product Detail]. Whether those texts are unique is not recorded (open question Q14).

### Sales Rep Hierarchy

Total → Detail. The drill path has 2 levels and is recorded as parent-child (value based): its depth is not fixed [kb:sampleapp::bmm::SAMPLESALES.Sales Rep Hierarchy]. Its default top level is "Total" [kb:sampleapp::bmm::SAMPLESALES.Sales Rep Hierarchy].

| Level | Key field | Members, as recorded | Shown to report authors as | Evidence |
|---|---|---|---|---|
| 0 Total (grand total) | none recorded | 1 | no level recorded | [kb:sampleapp::bmm::SAMPLESALES.Sales Rep Hierarchy.Total] |
| 1 Detail | "Dim Sales Rep"."Employee ID" | 1 | no level recorded | [kb:sampleapp::bmm::SAMPLESALES.Sales Rep Hierarchy.Detail] |

Report authors are offered it as the hierarchy "Sales Rep Hierarchy" in the table "Dim Sales Rep"; it is recorded there with 0 levels and is shown by the field "Employee Name" [kb:sampleapp::pres::Sample Sales.Dim Sales Rep.Sales Rep Hierarchy]. Which field holds the parent of a member is not recorded (open question Q15).

### Time Hierarchy

Dim Time Total → Year → Half Year → Quarter → Month → Week → Dim Time Detail. The drill path has 7 levels and its default top level is "Dim Time Total" [kb:sampleapp::bmm::SAMPLESALES.Time Hierarchy].

| Level | Key field | Members, as recorded | Shown to report authors as | Evidence |
|---|---|---|---|---|
| 0 Dim Time Total (grand total) | none recorded | 1 | level "Dim Time Total", no field | [kb:sampleapp::bmm::SAMPLESALES.Time Hierarchy.Dim Time Total] [kb:sampleapp::pres::Sample Sales.Dim Time.Time Hierarchy.Dim Time Total] |
| 1 Year | "Dim Time"."Per Name Year" | 2 | level "Year", field "Per Name Year" | [kb:sampleapp::bmm::SAMPLESALES.Time Hierarchy.Year] [kb:sampleapp::pres::Sample Sales.Dim Time.Time Hierarchy.Year] [kb:sampleapp::pres::Sample Sales.Dim Time.Time Hierarchy.Year.0] |
| 2 Half Year | "Dim Time"."Per Name Half" | 180 | level "Half Year", field "Per Name Half" | [kb:sampleapp::bmm::SAMPLESALES.Time Hierarchy.Half Year] [kb:sampleapp::pres::Sample Sales.Dim Time.Time Hierarchy.Half Year] [kb:sampleapp::pres::Sample Sales.Dim Time.Time Hierarchy.Half Year.0] |
| 3 Quarter | "Dim Time"."Per Name Qtr" | 270 | level "Quarter", field "Per Name Qtr" | [kb:sampleapp::bmm::SAMPLESALES.Time Hierarchy.Quarter] [kb:sampleapp::pres::Sample Sales.Dim Time.Time Hierarchy.Quarter] [kb:sampleapp::pres::Sample Sales.Dim Time.Time Hierarchy.Quarter.0] |
| 4 Month | "Dim Time"."Per Name Month" | 322 | level "Month", field "Per Name Month" | [kb:sampleapp::bmm::SAMPLESALES.Time Hierarchy.Month] [kb:sampleapp::pres::Sample Sales.Dim Time.Time Hierarchy.Month] [kb:sampleapp::pres::Sample Sales.Dim Time.Time Hierarchy.Month.0] |
| 5 Week | "Dim Time"."Per Name Week" | 350 | level "Week", field "Per Name Week" | [kb:sampleapp::bmm::SAMPLESALES.Time Hierarchy.Week] [kb:sampleapp::pres::Sample Sales.Dim Time.Time Hierarchy.Week] [kb:sampleapp::pres::Sample Sales.Dim Time.Time Hierarchy.Week.0] |
| 6 Dim Time Detail | "Dim Time"."Calendar Date" | 365 | level "Dim Time Detail", field "Calendar Date" | [kb:sampleapp::bmm::SAMPLESALES.Time Hierarchy.Dim Time Detail] [kb:sampleapp::pres::Sample Sales.Dim Time.Time Hierarchy.Dim Time Detail] [kb:sampleapp::pres::Sample Sales.Dim Time.Time Hierarchy.Dim Time Detail.0] |

Report authors are offered it as the hierarchy "Time Hierarchy" in the table "Dim Time", with 7 levels [kb:sampleapp::pres::Sample Sales.Dim Time.Time Hierarchy].

The level "Week" is recorded below the level "Month" and above "Dim Time Detail" [kb:sampleapp::bmm::SAMPLESALES.Time Hierarchy.Week]. Whether every week of the data lies inside one month, and what the recorded member numbers stand for, is not recorded (open question Q13).

The lowest level, "Dim Time Detail", is identified by the field "Calendar Date" [kb:sampleapp::bmm::SAMPLESALES.Time Hierarchy.Dim Time Detail]. Sources of other sections name this level and the level "Month" as their level of detail.

## Values set per user or for the whole system

The export records 1 variable that the formulas of this subject area use (counted).

### PREFERRED_CURRENCY

"PREFERRED_CURRENCY" is a value set per user at login (a "session variable"); its security mask is recorded as `2` [kb:sampleapp::var::PREFERRED_CURRENCY].

- Default: `'gc1'` [kb:sampleapp::var::PREFERRED_CURRENCY].
- It is set by the query "initBlockPrefCurrency" (an "init block"), which is recorded as a per-session query; whether it is enabled is not recorded, and its refresh period is not recorded [kb:sampleapp::var::init.initBlockPrefCurrency].
- The query that sets the value, exactly as recorded: `select 'VALUEOF(NQ_SESSION.PREFERED_CURRENCY)' from dual` [kb:sampleapp::var::init.initBlockPrefCurrency].
- Where the subject area uses it: in 1 place, the formula of the field "Preferred Currency" of the business table Fact Revenue (see the section Fact Revenue) [kb:sampleapp::var::PREFERRED_CURRENCY].

Two things in the query stand out. The name inside it is spelled PREFERED_CURRENCY, with one R, while the variable is PREFERRED_CURRENCY, with two [kb:sampleapp::var::init.initBlockPrefCurrency] [kb:sampleapp::var::PREFERRED_CURRENCY]. And the query reads no table of user settings: it selects a piece of text from `dual` [kb:sampleapp::var::init.initBlockPrefCurrency]. What value a user ends up with at run time, and how a user comes to have 'gc2' or 'gc3', is not recorded (open question Q16). Open question Q6 asked for the default and the query; both are now stated above, and what remains of Q6 is what the field shows for other values.

### Per-user values that are named but not recorded as variables

Formulas in other sections read further per-user values: "WEBLANGUAGE" (section Dim Products) and seven values whose names start with "CN_Sample_Sales_Dim_Time" (sections What report authors see and Dim Time). The export lists no variable row for them in this subject area (from the packet of this unit, which lists one variable). Their defaults and the queries that set them are therefore not recorded; see open questions Q1, Q8 and Q10.

## Security

The export records 7 security rows for this subject area: 6 permissions on fields and 1 row saying who holds a set of permissions (counted). No row filter is recorded for this subject area (from the packet of this unit, which lists none).

### Permissions on fields

All six permissions are on the two measures of the table "Fact Target" that report authors see. No permission on any other table or field of the subject area is recorded. Each permission is given through a set of permissions (a "privilege package"); the set is named in the second column.

| Permission | On field | Given through package | Who holds the package | Evidence |
|---|---|---|---|---|
| `write` | "Fact Target"."Revenue" | PrivPack_4209:18101355661677 | the role code below | [kb:sampleapp::sec::obj.ObjPriv_1500:18131355661677.o0] |
| `write` | "Fact Target"."Units" | PrivPack_4209:18101355661677 | the role code below | [kb:sampleapp::sec::obj.ObjPriv_1500:18131355661677.o1] |
| `read` | "Fact Target"."Revenue" | PrivPack_4209:18141355661677 | none recorded | [kb:sampleapp::sec::obj.ObjPriv_1500:18161355661677.o0] |
| `read` | "Fact Target"."Units" | PrivPack_4209:18141355661677 | none recorded | [kb:sampleapp::sec::obj.ObjPriv_1500:18161355661677.o1] |
| `read` | "Fact Target"."Revenue" | PrivPack_4209:18181355661677 | none recorded | [kb:sampleapp::sec::obj.ObjPriv_1500:18201355661677.o0] |
| `write` | "Fact Target"."Revenue" | PrivPack_4209:18181355661677 | none recorded | [kb:sampleapp::sec::obj.ObjPriv_1500:18211355661677.o0] |

In plain words: one package gives write on both measures; a second gives read on both; a third gives read and write on "Revenue" only. The section Fact Target says that both measures are recorded as writeable.

### Who holds the permissions

Only the first package has a holder recorded. The holder is recorded as a code, not as a name [kb:sampleapp::sec::pkg.PrivPack_4209:18101355661677.q0]:

```
6D7874D30FD9EC2E7C15CF8DD0C61529AF3BDCAD955E5AD1372E6D902B14F0A9 3FA146A018E99435FCE83B3F7BDDBF5A30BAF7BEF918C3AFAA354A94E310F903
```

This role code holds the query privilege `QueryPriv_4204:18331355753308` of the package `PrivPack_4209:18101355661677` [kb:sampleapp::sec::pkg.PrivPack_4209:18101355661677.q0]. The code is two strings separated by a space; whether that is one role or two is not recorded, and neither is its name (open question Q17).

For the packages `PrivPack_4209:18141355661677` and `PrivPack_4209:18181355661677` no holder is recorded in this subject area's rows [kb:sampleapp::sec::obj.ObjPriv_1500:18161355661677.o0] [kb:sampleapp::sec::obj.ObjPriv_1500:18201355661677.o0]. Who has them is part of open question Q17.

The section What report authors see mentions 2 further object permissions in the estate whose target was not recorded; they are not described here.

## Reports built on this subject area

Reports and dashboards were not examined. The export of this estate has no Web Catalog half, which is the part of OBIEE that holds reports and dashboards (from the manifest).

What is therefore unknown: which reports and dashboards use this subject area, and any calculation, filter or prompt that is defined inside a report and not in the model described above (from the manifest). A number that users know from a report may be built from a report-level formula that this document cannot show (open question Q18).

## What the export did not capture

### Errors

None. The extraction recorded no error about an object of this subject area, and the errors table of the Knowledge Base holds no row about anything outside it either (from the errors table and the manifest).

### Content of the export that was not read

The repository export holds details that no table of the Knowledge Base stores. They are listed per kind of object and counted over the whole estate, not over this subject area only (from the unparsed table). There are 131 kinds of unread detail in 25 groups. The numbers below are how often each occurs, added up from the packet; the full list, item by item, is in the packet file `slices/sampleapp/sample-sales/packets/gaps.md`.

Unread properties ("unread_attribute"), 14 kinds of object:

- Database joins written as a condition ("ComplexJoin"): 3 properties, 12 occurrences: internal identifiers and names of the join and of its expression.
- Drill paths ("Dimension"): 11 properties, 251 occurrences: whether the drill path is a time dimension, whether a level key is chronological or primary, names and identifiers of level keys, two settings of levels (isFDUC, levelConst), and the references from levels to fields and to other levels.
- Groups ("Group"): 4 properties, 16 occurrences: the display name and the identifier of each group, and references between groups.
- Queries that set values ("InitBlock"): 5 properties, 9 occurrences: the connection the query runs on, whether it sets values row by row (isRowWiseInit), per-database variants, and the references to the variables it sets.
- Joins between business tables ("LogicalComplexJoin"): 1 property, 9 occurrences: internal identifiers.
- Business tables ("LogicalTable"): 18 properties, 209 occurrences: identifiers and names of rules, expressions, keys and measure definitions; whether a field is derived (isDerived); a descriptor reference of a field (descriptorIDRef); whether a table is a lookup table (isLookupTable); whether a measure is commutative; references to fields, sources and other objects.
- Sources ("LogicalTableSource"): 19 properties, 923 occurrences: identifiers and names of expressions, of level-of-detail and filter entries; for joins inside a source, their two tables, type and cardinality; the starting table of links; a closure table reference; references to other objects.
- Permissions on objects ("ObjectPrivilege"): 3 properties, 22 occurrences: identifiers and references to the objects concerned.
- Database tables ("PhysicalTable"): 18 properties, 148 occurrences: the scale of columns, row counts and last-updated dates, caching settings, names and identifiers of keys, the key on the far side of each foreign key (counterPartKeyRef, 18 occurrences), and for one table the member key, parent key, distance and leaf columns of a parent-child relationship.
- Subject areas ("PresentationCatalog"): 4 properties, 26 occurrences: whether a display name or description is set, and references to their tables.
- Hierarchies offered to report authors ("PresentationHierarchy"): 6 properties, 87 occurrences: whether display names and descriptions are set, the container of a level, references to fields.
- Tables report authors see ("PresentationTable"): 6 properties, 148 occurrences: whether display names and descriptions are set on tables and fields, and references to hierarchies.
- Sets of permissions ("PrivilegePackage"): 2 properties, 4 occurrences: the user or role a package belongs to (userRef, 3 occurrences) and a database reference.
- Variables ("Variable"): 2 properties, 4 occurrences: identifiers and names of expressions.

Unread parts ("unread_element"), 8 kinds of object:

- Drill paths ("Dimension"): 6 parts, 102 occurrences: child levels, the fields attached to levels, references between levels, a preferred drill-down.
- Groups ("Group"): 3 parts, 6 occurrences: descriptions and child groups.
- Queries that set values ("InitBlock"): 4 parts, 8 occurrences: per-database variants, the variables set, and a part called Value.
- Business tables ("LogicalTable"): 3 parts, 28 occurrences: the keys of business tables and the fields in them.
- Sources ("LogicalTableSource"): 2 parts, 20 occurrences: the joins inside a source (2) and its links (18).
- Database tables ("PhysicalTable"): 4 parts, 47 occurrences: a description, the keys of tables and the columns in keys.
- Subject areas ("PresentationCatalog"): 2 parts, 13 occurrences: references to tables.
- Tables report authors see ("PresentationTable"): 2 parts, 10 occurrences: references to hierarchies.

Unread text ("unread_text"), 3 kinds of object:

- Groups ("Group"): description text, 2 occurrences.
- Queries that set values ("InitBlock"): the text of the part called Value, 2 occurrences.
- Database tables ("PhysicalTable"): description text, 1 occurrence.

### Left out of the Slice

- Rows of the errors table that are not about an object of this subject area: 0 (from the manifest).
- Rows of the unparsed table about whole object types or files that the extractor has no reader for, or about kinds of object this subject area does not hold: 15, all of the kind "unmodelled_type" (from the manifest). The packet does not name these types (open question Q19).
- Rows that are in the Slice only so that a reference from another row resolves: 0 (from the manifest).

### What these gaps mean for earlier sections

- The far side of every foreign key is missing in the section How the tables relate. Among the unread properties of database tables is one called "counterPartKeyRef" on foreign keys, 18 occurrences; its name suggests the key on the other side, but what it holds was not read. This is open question Q11.
- The joins inside a source were not read (2 occurrences). The section Dim Sales Rep could not say how its source combines two database tables; this is open question Q9.
- The parent-child settings of one database table (member key, parent key, distance and leaf columns) and the closure table reference of one source were not read. The section Drill paths could not name the parent field of "Sales Rep Hierarchy"; this is open question Q15.
- The fields attached to levels of drill paths were not read. In OBIEE generally, a measure that is fixed to a level of a drill path is recorded there. This bears on open question Q2 about "All Months Revenue" and "All Products Revenue".
- Display names and identifiers of groups, and the owner reference of sets of permissions, were not read. The section Security could give a role only as a code and found no holder for two packages; this is open question Q17.
- Whether an init block sets values row by row was not read, and neither was the text of its Value part. The per-user values behind the displayed names of "Dim Time" and behind "WEBLANGUAGE" have no variable row (section Values set per user or for the whole system); this is part of open questions Q1, Q8 and Q10.
- The keys of business tables and of database tables were not read, so no section states a primary key.
- Whether a business table is a lookup table and whether a drill path is a time dimension were not read, so the sections Dim Products (Translated) and Drill paths do not state it.

## Open questions

These are the points the export could not settle. Each says how to find the answer. Write the answer after "Answer:", on the same line, or tell the agent; the document will then be updated.

### Q1. What name do users see for the table "Dim Time"?
Raised in: overview
Why it matters: the displayed name of the table is recorded as a formula that reads a value set per user at login, so the name on screen can differ from "Dim Time" and can differ from one user to another. People who confirm this document know the table by the name they see.
Assumed meanwhile: nothing. The table is called "Dim Time" in this document, which is its recorded name, and the formula is quoted as recorded.
How to get the answer: log in to OBIEE as an ordinary report author, start a new analysis on the subject area "Sample Sales", and bring back the name shown for the time table in the list of tables. If users with another language setting exist, do the same as one of them and bring back that name too.
Answer:

### Q2. What distinguishes "All Months Revenue" and "All Products Revenue" from "Revenue"?
Raised in: table-01-fact-revenue
Why it matters: all three are summed, and in the sources "Aggregate Revenue" and "SAMP_REVENUE_F" all three come from the same database column REVENUE, so as recorded they return the same number. If one is meant to stay fixed while the user drills by month or by product, the description of it here is incomplete.
Assumed meanwhile: nothing. The three measures are described exactly as recorded.
How to get the answer: in the OBIEE Administration Tool open the business model SAMPLESALES, open the properties of the column "All Months Revenue" of the table "Fact Revenue", and bring back any level of a drill path the column is set to, with the name of the drill path. Do the same for "All Products Revenue".
Answer:

### Q3. What do "Revenue (EUR)", "Revenue (GBP)" and "Revenue (INR)" hold, and why are they multiplied in one source only?
Raised in: table-01-fact-revenue
Why it matters: in the source "SAMP_REVENUE_F" the three measures are the database column REVENUE multiplied by 0.76, 0.62 and 54.68; in the source "Aggregate Revenue" they are the column REVENUE of SAMP_REVENUE_A with no multiplication. As recorded, the same measure gives different numbers depending on the source OBIEE picks. The fixed numbers are written into the formulas and their origin is not recorded.
Assumed meanwhile: nothing. Both mappings are described exactly as recorded, and no currency meaning is stated.
How to get the answer: in the OBIEE Administration Tool open the business model SAMPLESALES, the table "Fact Revenue", the source "Aggregate Revenue", and bring back the expression mapped to "Revenue (EUR)", "Revenue (GBP)" and "Revenue (INR)" on its column mapping. Then run `select sum(REVENUE) from SAMP_REVENUE_A` and `select sum(REVENUE) from SAMP_REVENUE_F` against the schema SAMPLESALES and bring back both totals, so it can be seen whether SAMP_REVENUE_A already holds converted amounts. For the numbers 0.76, 0.62 and 54.68, bring back the document or rate table they were taken from, if one exists, and the date they applied.
Answer:

### Q4. When several sources can supply "Revenue" or "Units", which one is used, and do they hold the same totals?
Raised in: table-01-fact-revenue
Why it matters: "Revenue" and "Units" are fed by four sources (SAMP_REVENUE_A_MTH, SAMP_REVENUE_A_YR, SAMP_REVENUE_A, SAMP_REVENUE_F). A priority group is recorded only for "Aggregate Revenue" (1); for the others it is empty. If the four tables do not hold the same totals, a number depends on which source answers the query.
Assumed meanwhile: nothing. Each source is described with its level of detail and its priority as recorded; no order between them is stated.
How to get the answer: run these four queries against the schema SAMPLESALES and bring back the eight numbers: `select sum(REVENUE), sum(UNITS) from SAMP_REVENUE_F`; `select sum(REVENUE), sum(UNITS) from SAMP_REVENUE_A`; `select sum(REVENUE), sum(UNITS) from SAMP_REVENUE_A_MTH`; `select sum(REVENUE), sum(UNITS) from SAMP_REVENUE_A_YR`. In the OBIEE Administration Tool also open each of the five sources of "Fact Revenue" in the business model SAMPLESALES and bring back the value of its priority group.
Answer:

### Q5. What is the measure "Preserve" used for?
Raised in: table-01-fact-revenue
Why it matters: "Preserve" is shown to report authors, is summarised as a maximum, and comes from a query kept in the repository, `select 1 Preserve from dual`, which returns the fixed number 1 and reads no business data. Whether any report relies on it decides whether it has to be described further.
Assumed meanwhile: nothing. It is described as a measure that returns the maximum of a column whose only value is 1.
How to get the answer: in OBIEE, search the catalog for analyses that use the column "Fact Revenue"."Preserve" of the subject area "Sample Sales" (Catalog Manager, search the XML of the analyses for the text `"Fact Revenue"."Preserve"`), and bring back the names of the analyses found, or that none was found. Also bring back the description text of the column "Preserve" and of the source "FactCrossJoin" from their properties in the OBIEE Administration Tool, if any is filled in.
Answer:

### Q6. What does "Preferred Currency" return when the per-user value is not 'gc1', 'gc2' or 'gc3'?
Raised in: table-01-fact-revenue
Why it matters: the formula picks "Revenue" for 'gc1', "Revenue (EUR)" for 'gc2' and "Revenue (GBP)" for 'gc3', and has no branch for any other value. "Revenue (INR)" exists but cannot be picked. The field is also recorded as not a measure although it picks among measures. A user whose value is something else may see an empty column or an error.
Assumed meanwhile: nothing. The formula is quoted exactly and its three branches are described; no behaviour is stated for other values.
How to get the answer: in the OBIEE Administration Tool open Manage, Variables, and bring back the default value of the session variable PREFERRED_CURRENCY and the query of the initialization block that sets it; run that query and bring back the distinct values it can return. Then log in to OBIEE as a user whose value is none of the three, build an analysis with "Fact Revenue"."Preferred Currency", and bring back what is shown.
Answer:

### Q7. What do the measures "Revenue" and "Units" of "Fact Target" hold, and what happens to a value a user types in?
Raised in: table-02-fact-target
Why it matters: both measures are recorded as writeable, so OBIEE lets users type values in. The export does not say whether typed values are saved to the database table SAMP_TARGETS_F, who types them, or at which level (month, office, product) they are entered. Without this the table cannot be described as either loaded data or data entered by users.
Assumed meanwhile: nothing. The measures are described as sums of SAMP_TARGETS_F.REVENUE and SAMP_TARGETS_F.UNITS that are recorded as writeable.
How to get the answer: in the OBIEE Administration Tool open the business model SAMPLESALES, the table "Fact Target", the columns "Revenue" and "Units", and bring back the setting "Writeable" and any description text. In the physical layer open the connection pool of the database "xe" and bring back whether a write-back connection or write-back template is configured. Then run `select count(*), min(BILL_MTH_KEY), max(BILL_MTH_KEY) from SAMP_TARGETS_F` against the schema SAMPLESALES and bring back the three numbers.
Answer:

### Q8. Which languages does "Prod Dsc (Translated)" cover, and what does a user see when there is no translation?
Raised in: table-04-dim-products
Why it matters: the field is calculated by a lookup into the business table "Dim Products (Translated)" using the per-user value WEBLANGUAGE. The export does not record which values WEBLANGUAGE takes, which languages have translated product descriptions, or what is shown when none exists. Without this the field can be quoted but not described.
Assumed meanwhile: nothing about this system. The formula is quoted exactly; the general behaviour of a sparse lookup in OBIEE is stated as general and carries no marker.
How to get the answer: in the OBIEE Administration Tool open the business model SAMPLESALES, the table "Dim Products (Translated)", and bring back the name of the database table behind its source and the name of its language column. Then run against that database table a query of the form `select <language column>, count(*) from <table> group by <language column>` and bring back the result. Finally log in to OBIEE with the user interface set to a language that is not in that result, build an analysis with "Dim Products"."Prod Dsc" and "Dim Products"."Prod Dsc (Translated)", and bring back whether the two columns show the same text.
Answer:

### Q9. How does the source of "Dim Sales Rep" combine EMPLOYEE and EMP_PARENT_CHILD, and what does "Distance" hold?
Raised in: table-06-dim-sales-rep
Why it matters: one source reads two database tables. Four fields come from EMPLOYEE and "Distance" comes from EMP_PARENT_CHILD. If EMP_PARENT_CHILD holds several rows per employee, the business table holds several rows per employee too, and "Distance" has several values for the same person. The record of the source does not say which.
Assumed meanwhile: nothing. The fields are described by the database column each one comes from; no statement is made about how many rows there are per employee.
How to get the answer: run `select count(*) from EMPLOYEE` and `select count(*), count(distinct DISTANCE), min(DISTANCE), max(DISTANCE) from EMP_PARENT_CHILD` against the schema SAMPLESALES and bring back both results, with the list of column names of EMP_PARENT_CHILD. In the OBIEE Administration Tool open the business model SAMPLESALES, the table "Dim Sales Rep", the source "LTS Employee", and bring back the tables and the joins listed on its General tab.
Answer:

### Q10. What names do users see for the six fields of "Dim Time"?
Raised in: table-07-dim-time
Why it matters: the displayed name of each field of "Dim Time" is recorded as a formula that reads a value set per user at login (for example `VALUEOF(NQ_SESSION.CN_Sample_Sales_Dim_Time_Per_Name_Month)`), so the names on screen can differ from the names in this document and from one user to another. Readers who confirm this document know the fields by the names they see.
Assumed meanwhile: nothing. The fields are called by their recorded names (Calendar Date, Per Name Half, Per Name Month, Per Name Qtr, Per Name Week, Per Name Year), and each formula is quoted as recorded.
How to get the answer: log in to OBIEE as an ordinary report author, start a new analysis on the subject area "Sample Sales", open the time table in the list of tables, and bring back the six field names in the order shown. If users with another language setting exist, do the same as one of them. Also bring back, from the OBIEE Administration Tool under Manage, Variables, the name and the query of the initialization block that sets session variables whose names start with CN_Sample_Sales.
Answer:

### Q11. Which table and column does each of the fourteen foreign keys point to?
Raised in: joins
Why it matters: for all fourteen foreign keys between database tables the export recorded the table the key is on and its column, but not the table and column it points to. Without the far side, the joins between the fact tables (SAMP_REVENUE_F, SAMP_REVENUE_A, SAMP_REVENUE_A_MTH, SAMP_REVENUE_A_YR, SAMP_TARGETS_F) and the tables around them, and of EMP_PARENT_CHILD and SAMP_PRODUCTS_D_TRANSLATION, cannot be stated. Anyone building tables from this document needs them.
Assumed meanwhile: nothing. Each key is listed with the side that was recorded, and "Points to" says not recorded. No target is inferred from column names.
How to get the answer: in the OBIEE Administration Tool, in the physical layer, open the schema SAMPLESALES of the database "xe", and for each of the tables EMP_PARENT_CHILD, SAMP_PRODUCTS_D_TRANSLATION, SAMP_REVENUE_A, SAMP_REVENUE_A_MTH, SAMP_REVENUE_A_YR, SAMP_REVENUE_F and SAMP_TARGETS_F open its Foreign Keys tab and bring back, per key, the key name, the referenced table and the full join expression. A physical diagram of the schema showing the same is equally good.
Answer:

### Q12. Are the different row counts recorded on the business-model joins intended?
Raised in: joins
Why it matters: the seven joins between business tables record the "many" side as 0..n in three joins and as n..n in four, and the "one" side as 1..1 for Dim Sales Rep and 0..1 for the others. If the differences are real, some joins allow fact rows with no matching row in the other table and some do not, which changes what an inner join drops. If they are only two spellings of the same thing, the reader should know that. No join columns are recorded for these joins either.
Assumed meanwhile: nothing. The row counts are shown exactly as recorded.
How to get the answer: in the OBIEE Administration Tool open the business model SAMPLESALES, open the business model diagram, double-click each of the seven joins of "Fact Revenue" and "Fact Target", and bring back for each the cardinality shown on both sides and the join type. To test whether fact rows without a match exist, run for example `select count(*) from SAMP_REVENUE_F f where not exists (select 1 from SAMP_OFFICES_D o where o.OFFICE_KEY = f.OFFICE_KEY)` against the schema SAMPLESALES and bring back the count.
Answer:

### Q13. Does every week lie inside one month in the "Time Hierarchy", and what do the recorded member numbers stand for?
Raised in: hierarchies
Why it matters: the drill path is recorded as Year, Half Year, Quarter, Month, Week, Dim Time Detail, so each week must belong to exactly one month for totals to add up when a user drills from month to week. Calendar weeks usually cross month ends. The recorded member numbers (2, 180, 270, 322, 350, 365) also do not look like counts of years, half years, quarters, months, weeks and days, so they cannot be read as such.
Assumed meanwhile: nothing. The levels, their order, their key fields and their recorded numbers are stated as recorded.
How to get the answer: run against the schema SAMPLESALES `select PER_NAME_WEEK, count(distinct PER_NAME_MONTH) from SAMP_TIME_DAY_D group by PER_NAME_WEEK having count(distinct PER_NAME_MONTH) > 1` and bring back the number of rows returned and three example rows. Also run `select count(distinct PER_NAME_YEAR), count(distinct PER_NAME_HALF), count(distinct PER_NAME_QTR), count(distinct PER_NAME_MONTH), count(distinct PER_NAME_WEEK), count(*) from SAMP_TIME_DAY_D` and bring back the six numbers.
Answer:

### Q14. Are the names used as level keys in the office and product drill paths unique?
Raised in: hierarchies
Why it matters: every level of "Office Hierarchy" and "Products Hierararchy" is identified by a text field (Company, Organization, Department, Office Dsc; Brand, Lob, Type, Prod Dsc) and not by the key fields that exist beside them (Company Key, Org Key, Dept Key, Office Key; Brand Key, Lob Key, Type Key, Prod Key). If two offices or two products share a description, OBIEE treats them as one member of the level.
Assumed meanwhile: nothing. The key field of each level is stated as recorded.
How to get the answer: run against the schema SAMPLESALES `select count(*), count(distinct OFFICE_KEY), count(distinct OFFICE_DSC), count(distinct DEPARTMENT), count(distinct DEPT_KEY) from SAMP_OFFICES_D` and `select count(*), count(distinct PROD_KEY), count(distinct PROD_DSC), count(distinct TYPE), count(distinct TYPE_KEY) from SAMP_PRODUCTS_D`, and bring back both result rows.
Answer:

### Q15. Which field holds the parent in the "Sales Rep Hierarchy", and how deep does it go?
Raised in: hierarchies
Why it matters: the drill path is recorded as parent-child with no fixed depth, identified by "Employee ID" and shown by "Employee Name", and report authors are offered it with 0 levels. The field that points from a member to its parent is not recorded, so the shape of the hierarchy (who reports to whom, how many layers) cannot be described.
Assumed meanwhile: nothing. The drill path is described as parent-child with the key field "Employee ID"; no parent field is named.
How to get the answer: in the OBIEE Administration Tool open the business model SAMPLESALES, open the dimension "Sales Rep Hierarchy", and bring back the member key and the parent column shown on its General tab, and, from its Parent-Child Settings, the name of the relationship table and its four column names. Then run `select max(DISTANCE) from EMP_PARENT_CHILD` against the schema SAMPLESALES and bring back the number.
Answer:

### Q16. What value does PREFERRED_CURRENCY hold for a user at run time, and how is it changed?
Raised in: variables
Why it matters: the field "Preferred Currency" shows a different measure depending on this value. Its default is 'gc1'. The query recorded as setting it is `select 'VALUEOF(NQ_SESSION.PREFERED_CURRENCY)' from dual`, which reads no table of user settings and spells the name with one R, unlike the variable. As recorded it is not possible to say whether any user ever gets 'gc2' or 'gc3', and so whether "Preferred Currency" ever shows anything other than "Revenue".
Assumed meanwhile: nothing. The default and the query are quoted exactly; no run-time value is stated.
How to get the answer: log in to OBIEE as a report author and run, under Administration, Issue SQL, the statement `select VALUEOF(NQ_SESSION.PREFERRED_CURRENCY) from "Sample Sales"`, and bring back the value. Then open My Account, Preferences, change the currency if a currency choice is offered, log out and in, run the statement again and bring back the new value together with the list of choices offered. Also bring back the content of the file userpref_currencies.xml of the OBIEE installation, if it exists.
Answer:

### Q17. Which roles hold the permissions on "Fact Target"."Revenue" and "Units", and what are they called?
Raised in: security
Why it matters: write permission on the two target measures is held by a role recorded only as a code (two long strings separated by a space), so it is not known who may type target values, nor whether the code is one role or two. Two further packages (one giving read on both measures, one giving read and write on "Revenue") have no holder recorded at all. Without names, the security of the subject area cannot be confirmed by anyone.
Assumed meanwhile: nothing. The code is quoted exactly and the two other packages are stated as having no holder recorded.
How to get the answer: in the OBIEE Administration Tool open Manage, Identity, select the tab Application Roles, and for each role open Permissions, Object Permissions; bring back the names of the roles that list the presentation columns "Sample Sales"."Fact Target"."Revenue" or "Units", with the permission shown for each (Read, Read/Write, No Access). Alternatively, in the presentation layer open the properties of each of the two columns, press Permissions, tick "Show all users/application roles", and bring back the list.
Answer:

### Q18. Which reports and dashboards use "Sample Sales", and can the Web Catalog be exported?
Raised in: reports
Why it matters: the export contained only the repository. Reports and dashboards were not examined, so calculations, filters and prompts defined inside reports are unknown, and it is not known which of the fields and measures described here are actually used. Open questions Q5 and Q7 also depend on what reports do.
Assumed meanwhile: nothing. The document describes the model only and states that reports were not examined.
How to get the answer: in OBIEE Catalog Manager, open the catalog in online mode, select the folder /shared, and use File, Archive to export it; place the result in the estate folder next to the repository export so it can be read. If an archive cannot be made, use Catalog Manager's Tools, Create Report with the type Analysis and the columns Name, Path and Subject Area, and bring back the rows whose subject area is "Sample Sales".
Answer:

### Q19. Which kinds of object in the export have no reader at all?
Raised in: gaps
Why it matters: 15 rows of the unparsed table are of the kind "unmodelled_type": whole object types or files that the extractor does not read. They were left out of the Slice and are not named anywhere in this document. If one of them concerns this subject area (for example connection pools, users, or custom groups), a section here may be silent about something that exists.
Assumed meanwhile: nothing. The document states that 15 such rows exist and does not describe them.
How to get the answer: open the file kb/sampleapp/unparsed.csv, filter the rows whose kind is unmodelled_type, and bring back the 15 rows (the object type or file each one names, and its count).
Answer:

## Answered questions

Questions that have been answered. Statements that rest on an answer carry the question's number in their marker.

## Not described

Rows of the Knowledge Base that this document deliberately leaves out, each with the reason.

Nothing was left out: every required row is described above.
