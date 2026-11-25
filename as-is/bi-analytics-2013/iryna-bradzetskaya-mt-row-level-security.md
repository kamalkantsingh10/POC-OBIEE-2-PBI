Status: draft, not reviewed

# Iryna_Bradzetskaya_MT_Row-level_Security — the semantic model as it exists today

**What this is.** A plain-language description of the Iryna_Bradzetskaya_MT_Row-level_Security subject area in OBIEE: what report authors see, what each number is, how the tables relate, where the data comes from and what the database tables hold. It describes the existing system only.

**How to read it.** Statements of fact end with a marker in square brackets. The marker names the row of the Knowledge Base the statement came from, so any statement can be checked. A marker that starts with "answer" means the statement rests on an answer a person gave, not on OBIEE. Text shown as code is copied from OBIEE exactly. Where the export does not settle something, the text points to a numbered open question near the end; each says how to find the answer and has an empty answer line for you.

| | |
|---|---|
| Subject area | Iryna_Bradzetskaya_MT_Row-level_Security |
| Estate | bi-analytics-2013 |
| Knowledge Base extracted | 2026-11-23T20:54:06Z |
| Reports and dashboards | Not examined: the estate has no Web Catalog half, only the repository half was extracted |

## The model in one page

**What was not examined, up front.** Only the repository half of the estate was extracted; there is no Web Catalog half, so no report or dashboard was examined, and calculations, filters and prompts defined inside reports are unknown. The extraction recorded no error about an object of this subject area. 107 kinds of source content were not read, among them the display names, guids and descriptions of groups; query privileges (72) are a type of object the extraction has no reader for (see the section What the export did not capture; no Row identifier).

**What it holds.** The subject area is built on the business model of the same name, with no implicit fact column recorded [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security]. Report authors see 10 tables with 129 fields; each table has one business table of the same name behind it. Only 2 of the 129 fields are measures: AMOUNT_PAYMENT and COUNT_PAYMENT of TRANSACTIONS_DAILY, both summed [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TRANSACTIONS_DAILY]. The other tables are FCT_TRANSACTIONS_ACTUAL (14 fields, no measures) [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.FCT_TRANSACTIONS_ACTUAL], CURRENCY (9) [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.CURRENCY], CUSTOMERS_REC and CUSTOMERS_SEND (12 each) [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.CUSTOMERS_REC] [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.CUSTOMERS_SEND], LOCATIONS_REC and LOCATIONS_SEND (16 each) [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.LOCATIONS_REC] [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.LOCATIONS_SEND], OPERATIONS (8) [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.OPERATIONS.key.PK_DIM_OPERATIONS], TARIFFS (9) [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TARIFFS.key.PK_TARIFFS] and TIMES_DAILY (22) [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TIMES_DAILY]. There are 3 drill paths, Locations_Rec, Locations_Send and Times, offered to report authors as 6 hierarchies [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.Locations_Rec] [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.Times].

**The database tables.** Each business table has one source. Seven of the eleven database tables are queries kept in the repository, not tables of the database.

| Database table | Columns | Key | Read by |
|---|---|---|---|
| DIM_CURRENCY_SCD | 9 | PK_DIM_CURRENCY_SCD (CURRENCY_SUR_ID) | source "DIM_CURRENCY_SCD" of CURRENCY [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_CURRENCY_SCD] |
| DIM_CUSTOMERS_REC (query) | 12 | PK_DIM_CUSTOMERS (CUST_ID) | source "DIM_CUSTOMERS_REC" of CUSTOMERS_REC [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_CUSTOMERS_REC] |
| DIM_CUSTOMERS_SEND (query) | 12 | PK_DIM_CUSTOMERS (CUST_ID) | source "DIM_CUSTOMERS_SEND" of CUSTOMERS_SEND [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_CUSTOMERS_SEND] |
| DIM_LOCATIONS_REC (query) | 16 | PC_LOC (GEO_ID) | source "DIM_LOCATIONS_REC" of LOCATIONS_REC [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_LOCATIONS_REC] |
| DIM_LOCATIONS_SEND (query) | 16 | PC_LOC (GEO_ID) | source "DIM_LOCATIONS_SEND" of LOCATIONS_SEND [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_LOCATIONS_SEND] |
| DIM_OPERATIONS | 5 | PK_DIM_OPERATIONS (OPERATION_ID) | source "DIM_OPERATIONS" of OPERATIONS [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_OPERATIONS] |
| DIM_OPERATION_METHODS (query) | 4 | PK_OP_M (OPERATION_METHOD_ID) | source "DIM_OPERATIONS" of OPERATIONS [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_OPERATION_METHODS.def.DefaultMulDB] |
| DIM_TARIFFS | 9 | PK_TARIFFS (TARIFF_ID) | source "DIM_TARIFFS" of TARIFFS [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_TARIFFS] |
| DIM_TIMES_DAILY (query) | 22 | DIM_TIMES_DAILY_PK (TIME_ID) | source "DIM_TIMES_DAILY" of TIMES_DAILY [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_TIMES_DAILY.def.DefaultMulDB] |
| FCT_TRANSACTIONS_ACTUAL (query) | 14 | none recorded | source "FCT_TRANSACTIONS_ACTUAL" of FCT_TRANSACTIONS_ACTUAL [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.FCT_TRANSACTIONS_ACTUAL] |
| FCT_TRANSACTIONS_DAILY | 14 | none recorded | source "FCT_TRANSACTIONS_DAILY" of TRANSACTIONS_DAILY [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.FCT_TRANSACTIONS_DAILY] |

**Before relying on a number, know these.**

1. **Who sees which rows.** Both transaction tables are restricted twice by the same text, once as a content filter of their source and once as a row filter: a user sees rows of currency numbers 12, 10, 15, 11 or 13, 14, 16, 17 depending on whether a named text is found in ROLEGUIDS [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TRANSACTIONS_DAILY.src.FCT_TRANSACTIONS_DAILY] [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.FCT_TRANSACTIONS_ACTUAL.src.FCT_TRANSACTIONS_ACTUAL#filter]. TRANSACTIONS_DAILY looks for "Role_MT_A" and "Role_MT_F", FCT_TRANSACTIONS_ACTUAL for "MT_User_A" and "MT_User_F". ROLEGUIDS is not defined in the repository, and no role, permission or privilege package of this subject area is recorded (sections Security and Values set per user or for the whole system; open questions Q3 and Q14).
2. **FCT_TRANSACTIONS_ACTUAL has no measures.** Its AMOUNT_PAYMENT and COUNT_PAYMENT are plain fields [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.FCT_TRANSACTIONS_ACTUAL.AMOUNT_PAYMENT], and it is a query over FCT_TRANSACTIONS_DAILY limited to one year, the value BI_YEAR [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.FCT_TRANSACTIONS_ACTUAL]. BI_YEAR defaults to 2010.0d and is set by a query; its run-time value is not recorded [kb:bi-analytics-2013::var::BI_YEAR] (open questions Q6 and Q13). In the business model it is joined only to TIMES_DAILY, CURRENCY, OPERATIONS and TARIFFS [kb:bi-analytics-2013::bmm::joins.Iryna_Bradzetskaya_MT#1:Relationship_2004:4072641381133744] (open question Q11).
3. **PAYMENT_WHIS_TAX is AMOUNT_PAYMENT multiplied by 20**, a plain field, not a measure; what 20 stands for is not recorded [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TRANSACTIONS_DAILY.PAYMENT_WHIS_TAX] (open question Q2).
4. **The fact tables have no key**, and the customer and location tables come in two copies each (REC and SEND) built on the same query text [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_CUSTOMERS_SEND.def.DefaultMulDB] [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_LOCATIONS_SEND.def.DefaultMulDB]; what REC and SEND stand for is not recorded (open question Q5).
5. **Time keys.** The time table's key TIME_ID is a date column, and the levels Quarter, Month and Week of the drill path Times are keyed by their number alone [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.Times] (open questions Q10 and Q12).

**Open questions.** 14 questions are open, each at the end with how to find the answer; none has been answered yet and none was settled by a later section.

## What report authors see

The subject area "Iryna_Bradzetskaya_MT_Row-level_Security" is built on the business model of the same name, "Iryna_Bradzetskaya_MT_Row-level_Security" [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security]. No other name is recorded for it.

No implicit fact column (the measure OBIEE uses when a query names no measure) is recorded: the packet says "none recorded" [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security]. The auto-aggregate setting is written as `true` [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security].

### Tables report authors see

Report authors see 10 tables. Each has exactly one business table behind it, with the same name.

| Table | Fields | Business table behind it | Evidence |
|---|---|---|---|
| CURRENCY | 9 | CURRENCY (see the section CURRENCY) | [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.CURRENCY] |
| CUSTOMERS_REC | 12 | CUSTOMERS_REC (see the section CUSTOMERS_REC) | [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.CUSTOMERS_REC] |
| CUSTOMERS_SEND | 12 | CUSTOMERS_SEND (see the section CUSTOMERS_SEND) | [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.CUSTOMERS_SEND] |
| FCT_TRANSACTIONS_ACTUAL | 14 | FCT_TRANSACTIONS_ACTUAL (see the section FCT_TRANSACTIONS_ACTUAL) | [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.FCT_TRANSACTIONS_ACTUAL] |
| LOCATIONS_REC | 16 | LOCATIONS_REC (see the section LOCATIONS_REC) | [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.LOCATIONS_REC] |
| LOCATIONS_SEND | 16 | LOCATIONS_SEND (see the section LOCATIONS_SEND) | [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.LOCATIONS_SEND] |
| OPERATIONS | 8 | OPERATIONS (see the section OPERATIONS) | [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.OPERATIONS] |
| TARIFFS | 9 | TARIFFS (see the section TARIFFS) | [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.TARIFFS] |
| TIMES_DAILY | 22 | TIMES_DAILY (see the section TIMES_DAILY) | [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.TIMES_DAILY] |
| TRANSACTIONS_DAILY | 11 | TRANSACTIONS_DAILY (see the section TRANSACTIONS_DAILY) | [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.TRANSACTIONS_DAILY] |

No displayed name in this packet is a formula.

### Counts

These counts were made from the Slice's tables and carry no marker.

- Tables report authors see: 10; their fields: 129, of which measures: 2.
- Business tables described: 10; their fields: 129, of which measures: 2 (measures not shown to report authors are described with their business table).
- Sources: 10; database tables: 11; their columns: 133 (every column of each table, whether or not a field maps it); their keys: 9.
- Business-model joins: 12; database joins: 17.
- Drill paths (dimensions): 3; hierarchies offered to report authors: 6.
- Variables: 1; security rows: 2.
- Reports built on the subject area: 0; dashboards placing them: 0; saved filters they use: 0.
- Errors about objects of the Slice: 0; kinds of source content not read: 107 (both listed in the section What the export did not capture).

### Items of the estate that are in no subject area

These exist in the full Knowledge Base and belong to no subject area, or not to this one. They are not described in this document. The counts come from the manifest and carry no marker.

- Object permissions whose target was not recorded: 0.
- Role privileges of privilege packages that no permission of this subject area goes through: 72.
- Queries that set a value (init blocks) and set no variable of this subject area: 151.
- Variables that no expression of this subject area names: 342.

### What was examined

The estate is bi-analytics-2013. Of its two halves, only the repository half ("rpd") was extracted. The estate has no Web Catalog half, so reports and dashboards were not examined. The Knowledge Base was extracted at 2026-11-23T20:54:06Z by extractor version 0.1.0. (From the manifest; no Row identifier.)

The count of reports built on the subject area is 0, and the count of dashboards placing them is 0; reports and dashboards were not examined because the estate has no Web Catalog half.

This document was written from these units, in order: overview, table-01-transactions-daily, table-02-currency, table-03-customers-rec, table-04-customers-send, table-05-fct-transactions-actual, table-06-locations-rec, table-07-locations-send, table-08-operations, table-09-tariffs, table-10-times-daily, dbtable-01-dim-currency-scd, dbtable-02-dim-customers-rec, dbtable-03-dim-customers-send, dbtable-04-dim-locations-rec, dbtable-05-dim-locations-send, dbtable-06-dim-operations, dbtable-07-dim-operation-methods, dbtable-08-dim-tariffs, dbtable-09-dim-times-daily, dbtable-10-fct-transactions-actual, dbtable-11-fct-transactions-daily, joins, hierarchies, variables, security, reports (one unit per dashboard or folder), saved-filters (when there are any), gaps.

## TRANSACTIONS_DAILY

TRANSACTIONS_DAILY is a business table ("logical table") of the business model "Iryna_Bradzetskaya_MT_Row-level_Security"; the repository declares 1 source for it [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TRANSACTIONS_DAILY]. In this subject area it has 11 fields, of which 2 are measures, and all 11 are shown to report authors; its one source is "FCT_TRANSACTIONS_DAILY", described under "Where the data comes from" (counted; no Row identifier).

### Keys

Keys of the business table TRANSACTIONS_DAILY: not recorded. The repository declares no key for it (listed in the packet without a Row identifier).

### Measures

| Measure | How it is summarised | Can be typed into | In sources | Evidence |
|---|---|---|---|---|
| AMOUNT_PAYMENT | SUM, by default; the rule is quoted below | no | 1 of 1: "FCT_TRANSACTIONS_DAILY" | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TRANSACTIONS_DAILY.AMOUNT_PAYMENT] [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TRANSACTIONS_DAILY.AMOUNT_PAYMENT#aggr0] |
| COUNT_PAYMENT | SUM, by default; the rule is quoted below | no | 1 of 1: "FCT_TRANSACTIONS_DAILY" | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TRANSACTIONS_DAILY.COUNT_PAYMENT] [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TRANSACTIONS_DAILY.COUNT_PAYMENT#aggr0] |

The rule for AMOUNT_PAYMENT, exactly [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TRANSACTIONS_DAILY.AMOUNT_PAYMENT#aggr0]:

```
SUM("Iryna_Bradzetskaya_MT_Row-level_Security"."TRANSACTIONS_DAILY"."AMOUNT_PAYMENT")
```

The rule for COUNT_PAYMENT, exactly [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TRANSACTIONS_DAILY.COUNT_PAYMENT#aggr0]:

```
SUM("Iryna_Bradzetskaya_MT_Row-level_Security"."TRANSACTIONS_DAILY"."COUNT_PAYMENT")
```

Both measures are shown to report authors under the same name in the table TRANSACTIONS_DAILY [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.TRANSACTIONS_DAILY.AMOUNT_PAYMENT] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.TRANSACTIONS_DAILY.COUNT_PAYMENT]. The definition kept on each measure itself is one database column: FCT_TRANSACTIONS_DAILY.AMOUNT_PAYMENT [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TRANSACTIONS_DAILY.AMOUNT_PAYMENT] and FCT_TRANSACTIONS_DAILY.COUNT_PAYMENT [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TRANSACTIONS_DAILY.COUNT_PAYMENT]. What each is mapped from in the source is stated with the source. That a count of payments is summed is recorded as above; what COUNT_PAYMENT holds per row is not recorded (open question Q1).

### Fields

Every field is shown to report authors under its own name in the table TRANSACTIONS_DAILY. None can be typed into. Except for PAYMENT_WHIS_TAX, the definition kept on each field is one database column of the same name in FCT_TRANSACTIONS_DAILY.

| Field | Shown to report authors as | Kept definition | In sources | Evidence |
|---|---|---|---|---|
| CURRENCY_SUR_ID | CURRENCY_SUR_ID | FCT_TRANSACTIONS_DAILY.CURRENCY_SUR_ID | 1 of 1: "FCT_TRANSACTIONS_DAILY" | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TRANSACTIONS_DAILY.CURRENCY_SUR_ID] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.TRANSACTIONS_DAILY.CURRENCY_SUR_ID] |
| CUST_SEND_ID | CUST_SEND_ID | FCT_TRANSACTIONS_DAILY.CUST_SEND_ID | 1 of 1: "FCT_TRANSACTIONS_DAILY" | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TRANSACTIONS_DAILY.CUST_SEND_ID] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.TRANSACTIONS_DAILY.CUST_SEND_ID] |
| CUS_REC_ID | CUS_REC_ID | FCT_TRANSACTIONS_DAILY.CUS_REC_ID | 1 of 1: "FCT_TRANSACTIONS_DAILY" | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TRANSACTIONS_DAILY.CUS_REC_ID] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.TRANSACTIONS_DAILY.CUS_REC_ID] |
| EVENT_DT | EVENT_DT | FCT_TRANSACTIONS_DAILY.EVENT_DT | 1 of 1: "FCT_TRANSACTIONS_DAILY" | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TRANSACTIONS_DAILY.EVENT_DT] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.TRANSACTIONS_DAILY.EVENT_DT] |
| GEO_REC_SUR_ID | GEO_REC_SUR_ID | FCT_TRANSACTIONS_DAILY.GEO_REC_SUR_ID | 1 of 1: "FCT_TRANSACTIONS_DAILY" | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TRANSACTIONS_DAILY.GEO_REC_SUR_ID] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.TRANSACTIONS_DAILY.GEO_REC_SUR_ID] |
| GEO_SEND_SUR_ID | GEO_SEND_SUR_ID | FCT_TRANSACTIONS_DAILY.GEO_SEND_SUR_ID | 1 of 1: "FCT_TRANSACTIONS_DAILY" | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TRANSACTIONS_DAILY.GEO_SEND_SUR_ID] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.TRANSACTIONS_DAILY.GEO_SEND_SUR_ID] |
| OPERATION_ID | OPERATION_ID | FCT_TRANSACTIONS_DAILY.OPERATION_ID | 1 of 1: "FCT_TRANSACTIONS_DAILY" | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TRANSACTIONS_DAILY.OPERATION_ID] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.TRANSACTIONS_DAILY.OPERATION_ID] |
| PAYMENT_WHIS_TAX | PAYMENT_WHIS_TAX | a formula, quoted below | 0 of 1 | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TRANSACTIONS_DAILY.PAYMENT_WHIS_TAX] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.TRANSACTIONS_DAILY.PAYMENT_WHIS_TAX] |
| TARIFF_ID | TARIFF_ID | FCT_TRANSACTIONS_DAILY.TARIFF_ID | 1 of 1: "FCT_TRANSACTIONS_DAILY" | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TRANSACTIONS_DAILY.TARIFF_ID] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.TRANSACTIONS_DAILY.TARIFF_ID] |

PAYMENT_WHIS_TAX is not a measure and is mapped in no source; it is calculated from another field of this table, the measure AMOUNT_PAYMENT multiplied by 20 [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TRANSACTIONS_DAILY.PAYMENT_WHIS_TAX]. Its formula, exactly:

```
"Iryna_Bradzetskaya_MT_Row-level_Security"."TRANSACTIONS_DAILY"."AMOUNT_PAYMENT" * 20
```

What the number 20 stands for, and what the name PAYMENT_WHIS_TAX means, are not recorded (open question Q2).

### Measures and fields report authors do not see

This table has no measures or fields that report authors do not see.

### Where the data comes from

TRANSACTIONS_DAILY has one source.

#### Source "FCT_TRANSACTIONS_DAILY"

The source is active; its priority group is not recorded (empty; it does not mean lowest) [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TRANSACTIONS_DAILY.src.FCT_TRANSACTIONS_DAILY]. Level of detail (grain): not recorded. The repository gives this source no level of detail [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TRANSACTIONS_DAILY.src.FCT_TRANSACTIONS_DAILY]. The packet says nothing about a parent-child hierarchy for this source.

**Content filter.** The source has a content filter. It is quoted here exactly as extracted [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TRANSACTIONS_DAILY.src.FCT_TRANSACTIONS_DAILY]:

```
(
Position (';Role_MT_A;' in ';' || valueof(NQ_SESSION.ROLEGUIDS) || ';') > 0 and  "Iryna_Bradzetskaya_MT".""."U_SAL"."FCT_TRANSACTIONS_DAILY"."CURRENCY_SUR_ID"   in (12,10,15,11)
)
or
(
Position (';Role_MT_F;' in ';' || valueof(NQ_SESSION.ROLEGUIDS) || ';') > 0 and "Iryna_Bradzetskaya_MT".""."U_SAL"."FCT_TRANSACTIONS_DAILY"."CURRENCY_SUR_ID"  in (13,14, 16,17)
)
```

Put mechanically: the filter has two bracketed conditions joined by "or". The first looks for the text ";Role_MT_A;" inside the value set per user at login `ROLEGUIDS` (framed by ";" on both sides) and requires the database column FCT_TRANSACTIONS_DAILY.CURRENCY_SUR_ID to be one of 12, 10, 15, 11. The second does the same with ";Role_MT_F;" and the values 13, 14, 16, 17 [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TRANSACTIONS_DAILY.src.FCT_TRANSACTIONS_DAILY]. The variable `ROLEGUIDS` is described in the section Values set per user or for the whole system. Which currencies the numbers stand for, and what a user who holds neither role, or both, is meant to see, are not recorded (open question Q3).

**Row filter.** A row filter also restricts this source; it is described in the section Security (listed in the packet without a Row identifier).

**Database tables it reads.** The source is built on 5 database tables, so it joins them. Four of the five are queries kept in the repository (type `select`), not tables of the database, and no field of this subject area is mapped from those four; the source still joins them.

| Database table | Schema | Type | Evidence |
|---|---|---|---|
| DIM_CUSTOMERS_REC (see the section Database table DIM_CUSTOMERS_REC); no field is mapped from it | `Iryna_Bradzetskaya_MT.U_SAL` | `select` | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TRANSACTIONS_DAILY.src.FCT_TRANSACTIONS_DAILY#table0] |
| DIM_LOCATIONS_SEND (see the section Database table DIM_LOCATIONS_SEND); no field is mapped from it | `Iryna_Bradzetskaya_MT.U_SAL` | `select` | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TRANSACTIONS_DAILY.src.FCT_TRANSACTIONS_DAILY#table1] |
| FCT_TRANSACTIONS_DAILY (see the section Database table FCT_TRANSACTIONS_DAILY) | `Iryna_Bradzetskaya_MT.U_SAL` | `none` | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TRANSACTIONS_DAILY.src.FCT_TRANSACTIONS_DAILY#table2] |
| DIM_CUSTOMERS_SEND (see the section Database table DIM_CUSTOMERS_SEND); no field is mapped from it | `Iryna_Bradzetskaya_MT.U_SAL` | `select` | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TRANSACTIONS_DAILY.src.FCT_TRANSACTIONS_DAILY#table3] |
| DIM_LOCATIONS_REC (see the section Database table DIM_LOCATIONS_REC); no field is mapped from it | `Iryna_Bradzetskaya_MT.U_SAL` | `select` | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TRANSACTIONS_DAILY.src.FCT_TRANSACTIONS_DAILY#table4] |

**Joins between them.** The packet gives 8 lines for 4 joins: the repository lists a join once under each of its two tables. "Left" and "right" in a join type, and the cardinality, read from the first table named on the line to the second. Join type and cardinality are copied as printed.

| Listed under | From | To | Join type | Cardinality | On | Evidence |
|---|---|---|---|---|---|---|
| DIM_CUSTOMERS_REC | DIM_CUSTOMERS_REC | FCT_TRANSACTIONS_DAILY | `Inner` | ONE TO MANY | foreign key "FK_TR_CUST_REC": FCT_TRANSACTIONS_DAILY (CUS_REC_ID) = DIM_CUSTOMERS_REC (CUST_ID) | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TRANSACTIONS_DAILY.src.FCT_TRANSACTIONS_DAILY#table0.join0] |
| DIM_LOCATIONS_SEND | DIM_LOCATIONS_SEND | FCT_TRANSACTIONS_DAILY | `Inner` | ONE TO MANY | foreign key "FK_TRANS_LOC_SEND": FCT_TRANSACTIONS_DAILY (GEO_SEND_SUR_ID) = DIM_LOCATIONS_SEND (GEO_ID) | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TRANSACTIONS_DAILY.src.FCT_TRANSACTIONS_DAILY#table1.join0] |
| FCT_TRANSACTIONS_DAILY | FCT_TRANSACTIONS_DAILY | DIM_LOCATIONS_REC | `LeftOuter` | `MANY TO ONE` | foreign key "FK_TRANS_LOC_REC": FCT_TRANSACTIONS_DAILY (GEO_REC_SUR_ID) = DIM_LOCATIONS_REC (GEO_ID) | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TRANSACTIONS_DAILY.src.FCT_TRANSACTIONS_DAILY#table2.join0] |
| FCT_TRANSACTIONS_DAILY | FCT_TRANSACTIONS_DAILY | DIM_LOCATIONS_SEND | `Inner` | `MANY TO ONE` | foreign key "FK_TRANS_LOC_SEND": FCT_TRANSACTIONS_DAILY (GEO_SEND_SUR_ID) = DIM_LOCATIONS_SEND (GEO_ID) | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TRANSACTIONS_DAILY.src.FCT_TRANSACTIONS_DAILY#table2.join1] |
| FCT_TRANSACTIONS_DAILY | FCT_TRANSACTIONS_DAILY | DIM_CUSTOMERS_SEND | `Inner` | `MANY TO ONE` | foreign key "FK_TR_CUST_SEND": FCT_TRANSACTIONS_DAILY (CUST_SEND_ID) = DIM_CUSTOMERS_SEND (CUST_ID) | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TRANSACTIONS_DAILY.src.FCT_TRANSACTIONS_DAILY#table2.join2] |
| FCT_TRANSACTIONS_DAILY | FCT_TRANSACTIONS_DAILY | DIM_CUSTOMERS_REC | `Inner` | `MANY TO ONE` | foreign key "FK_TR_CUST_REC": FCT_TRANSACTIONS_DAILY (CUS_REC_ID) = DIM_CUSTOMERS_REC (CUST_ID) | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TRANSACTIONS_DAILY.src.FCT_TRANSACTIONS_DAILY#table2.join3] |
| DIM_CUSTOMERS_SEND | DIM_CUSTOMERS_SEND | FCT_TRANSACTIONS_DAILY | `Inner` | ONE TO MANY | foreign key "FK_TR_CUST_SEND": FCT_TRANSACTIONS_DAILY (CUST_SEND_ID) = DIM_CUSTOMERS_SEND (CUST_ID) | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TRANSACTIONS_DAILY.src.FCT_TRANSACTIONS_DAILY#table3.join0] |
| DIM_LOCATIONS_REC | DIM_LOCATIONS_REC | FCT_TRANSACTIONS_DAILY | `RightOuter` | ONE TO MANY | foreign key "FK_TRANS_LOC_REC": FCT_TRANSACTIONS_DAILY (GEO_REC_SUR_ID) = DIM_LOCATIONS_REC (GEO_ID) | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TRANSACTIONS_DAILY.src.FCT_TRANSACTIONS_DAILY#table4.join0] |

The cardinality "ONE TO MANY" is printed in the packet with a leading space; it is copied here without it.

**Mapping.** Every mapping of this source is one database column of FCT_TRANSACTIONS_DAILY.

| Field | Database column | Type, precision, nullable | Evidence |
|---|---|---|---|
| AMOUNT_PAYMENT | FCT_TRANSACTIONS_DAILY.AMOUNT_PAYMENT | type DOUBLE, precision 22, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TRANSACTIONS_DAILY.src.FCT_TRANSACTIONS_DAILY#map0] |
| COUNT_PAYMENT | FCT_TRANSACTIONS_DAILY.COUNT_PAYMENT | type DOUBLE, precision 22, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TRANSACTIONS_DAILY.src.FCT_TRANSACTIONS_DAILY#map1] |
| CURRENCY_SUR_ID | FCT_TRANSACTIONS_DAILY.CURRENCY_SUR_ID | type DOUBLE, precision 22, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TRANSACTIONS_DAILY.src.FCT_TRANSACTIONS_DAILY#map2] |
| CUS_REC_ID | FCT_TRANSACTIONS_DAILY.CUS_REC_ID | type DOUBLE, precision 22, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TRANSACTIONS_DAILY.src.FCT_TRANSACTIONS_DAILY#map3] |
| CUST_SEND_ID | FCT_TRANSACTIONS_DAILY.CUST_SEND_ID | type DOUBLE, precision 22, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TRANSACTIONS_DAILY.src.FCT_TRANSACTIONS_DAILY#map4] |
| EVENT_DT | FCT_TRANSACTIONS_DAILY.EVENT_DT | type DATETIME, precision 22, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TRANSACTIONS_DAILY.src.FCT_TRANSACTIONS_DAILY#map5] |
| GEO_REC_SUR_ID | FCT_TRANSACTIONS_DAILY.GEO_REC_SUR_ID | type DOUBLE, precision 22, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TRANSACTIONS_DAILY.src.FCT_TRANSACTIONS_DAILY#map6] |
| GEO_SEND_SUR_ID | FCT_TRANSACTIONS_DAILY.GEO_SEND_SUR_ID | type DOUBLE, precision 22, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TRANSACTIONS_DAILY.src.FCT_TRANSACTIONS_DAILY#map7] |
| OPERATION_ID | FCT_TRANSACTIONS_DAILY.OPERATION_ID | type DOUBLE, precision 22, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TRANSACTIONS_DAILY.src.FCT_TRANSACTIONS_DAILY#map8] |
| TARIFF_ID | FCT_TRANSACTIONS_DAILY.TARIFF_ID | type DOUBLE, precision 22, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TRANSACTIONS_DAILY.src.FCT_TRANSACTIONS_DAILY#map9] |

PAYMENT_WHIS_TAX has no mapping in this source; it is calculated from AMOUNT_PAYMENT (see Fields above).

### Points to note

- The only source of TRANSACTIONS_DAILY has a content filter that reads the user's roles through `ROLEGUIDS` and restricts CURRENCY_SUR_ID to fixed lists of numbers, and a row filter in the section Security restricts it as well [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TRANSACTIONS_DAILY.src.FCT_TRANSACTIONS_DAILY] (open question Q3).
- The source joins four tables from which no field is mapped. Three of the four joins are `Inner`; the join to DIM_LOCATIONS_REC is listed as `LeftOuter` under FCT_TRANSACTIONS_DAILY and `RightOuter` under DIM_LOCATIONS_REC [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TRANSACTIONS_DAILY.src.FCT_TRANSACTIONS_DAILY#table2.join0] [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TRANSACTIONS_DAILY.src.FCT_TRANSACTIONS_DAILY#table4.join0].
- PAYMENT_WHIS_TAX is calculated from the measure AMOUNT_PAYMENT multiplied by 20 and is not itself a measure [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TRANSACTIONS_DAILY.PAYMENT_WHIS_TAX] (open question Q2).
- No key is declared for the business table TRANSACTIONS_DAILY (listed in the packet without a Row identifier).

## CURRENCY

CURRENCY is a business table ("logical table") of the business model "Iryna_Bradzetskaya_MT_Row-level_Security"; the repository declares 1 source for it [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.CURRENCY]. In this subject area it has 9 fields and no measures, and all 9 are shown to report authors; its one source is "DIM_CURRENCY_SCD" (counted; no Row identifier).

### Keys

| Key | Primary key (as recorded) | Fields | Evidence |
|---|---|---|---|
| PK_DIM_CURRENCY_SCD | true | CURRENCY_SUR_ID | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.CURRENCY.key.PK_DIM_CURRENCY_SCD] |

### Measures

This table has no measures.

### Fields

Every field is shown to report authors under its own name in the table CURRENCY. None can be typed into. The definition kept on each field is one database column of the same name in DIM_CURRENCY_SCD.

| Field | Shown to report authors as | Kept definition | In sources | Evidence |
|---|---|---|---|---|
| CURRENCY_CODE | CURRENCY_CODE | DIM_CURRENCY_SCD.CURRENCY_CODE | 1 of 1: "DIM_CURRENCY_SCD" | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.CURRENCY.CURRENCY_CODE] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.CURRENCY.CURRENCY_CODE] |
| CURRENCY_DESC | CURRENCY_DESC | DIM_CURRENCY_SCD.CURRENCY_DESC | 1 of 1: "DIM_CURRENCY_SCD" | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.CURRENCY.CURRENCY_DESC] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.CURRENCY.CURRENCY_DESC] |
| CURRENCY_NAME | CURRENCY_NAME | DIM_CURRENCY_SCD.CURRENCY_NAME | 1 of 1: "DIM_CURRENCY_SCD" | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.CURRENCY.CURRENCY_NAME] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.CURRENCY.CURRENCY_NAME] |
| CURRENCY_SUR_ID | CURRENCY_SUR_ID | DIM_CURRENCY_SCD.CURRENCY_SUR_ID | 1 of 1: "DIM_CURRENCY_SCD" | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.CURRENCY.CURRENCY_SUR_ID] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.CURRENCY.CURRENCY_SUR_ID] |
| CURRENCY_TO_DOLLAR | CURRENCY_TO_DOLLAR | DIM_CURRENCY_SCD.CURRENCY_TO_DOLLAR | 1 of 1: "DIM_CURRENCY_SCD" | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.CURRENCY.CURRENCY_TO_DOLLAR] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.CURRENCY.CURRENCY_TO_DOLLAR] |
| CURRENCY_TYPE_DESC | CURRENCY_TYPE_DESC | DIM_CURRENCY_SCD.CURRENCY_TYPE_DESC | 1 of 1: "DIM_CURRENCY_SCD" | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.CURRENCY.CURRENCY_TYPE_DESC] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.CURRENCY.CURRENCY_TYPE_DESC] |
| CURRENCY_TYPE_ID | CURRENCY_TYPE_ID | DIM_CURRENCY_SCD.CURRENCY_TYPE_ID | 1 of 1: "DIM_CURRENCY_SCD" | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.CURRENCY.CURRENCY_TYPE_ID] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.CURRENCY.CURRENCY_TYPE_ID] |
| VALID_FROM | VALID_FROM | DIM_CURRENCY_SCD.VALID_FROM | 1 of 1: "DIM_CURRENCY_SCD" | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.CURRENCY.VALID_FROM] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.CURRENCY.VALID_FROM] |
| VALID_TO | VALID_TO | DIM_CURRENCY_SCD.VALID_TO | 1 of 1: "DIM_CURRENCY_SCD" | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.CURRENCY.VALID_TO] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.CURRENCY.VALID_TO] |

What CURRENCY_TO_DOLLAR holds, and what the pair VALID_FROM and VALID_TO means for the rows of this table, are not recorded (open question Q4).

### Measures and fields report authors do not see

This table has no measures or fields that report authors do not see.

### Where the data comes from

CURRENCY has one source.

#### Source "DIM_CURRENCY_SCD"

The source is active; its priority group is not recorded (empty; it does not mean lowest) [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.CURRENCY.src.DIM_CURRENCY_SCD]. Level of detail (grain): not recorded. The repository gives this source no level of detail [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.CURRENCY.src.DIM_CURRENCY_SCD]. The packet says nothing about a parent-child hierarchy for this source.

No content filter is recorded for this source.

**Database tables it reads.**

| Database table | Schema | Type | Evidence |
|---|---|---|---|
| DIM_CURRENCY_SCD (see the section Database table DIM_CURRENCY_SCD) | `Iryna_Bradzetskaya_MT.U_SAL` | `none` | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.CURRENCY.src.DIM_CURRENCY_SCD#table0] |

This source reads one table, so it makes no join.

**Mapping.** Every mapping of this source is one database column of DIM_CURRENCY_SCD.

| Field | Database column | Type, precision, nullable | Evidence |
|---|---|---|---|
| CURRENCY_CODE | DIM_CURRENCY_SCD.CURRENCY_CODE | type DOUBLE, precision 22, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.CURRENCY.src.DIM_CURRENCY_SCD#map0] |
| CURRENCY_DESC | DIM_CURRENCY_SCD.CURRENCY_DESC | type VARCHAR, precision 30, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.CURRENCY.src.DIM_CURRENCY_SCD#map1] |
| CURRENCY_NAME | DIM_CURRENCY_SCD.CURRENCY_NAME | type VARCHAR, precision 10, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.CURRENCY.src.DIM_CURRENCY_SCD#map2] |
| CURRENCY_SUR_ID | DIM_CURRENCY_SCD.CURRENCY_SUR_ID | type DOUBLE, precision 22, nullable not stated | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.CURRENCY.src.DIM_CURRENCY_SCD#map3] |
| CURRENCY_TO_DOLLAR | DIM_CURRENCY_SCD.CURRENCY_TO_DOLLAR | type DOUBLE, precision 22, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.CURRENCY.src.DIM_CURRENCY_SCD#map4] |
| CURRENCY_TYPE_DESC | DIM_CURRENCY_SCD.CURRENCY_TYPE_DESC | type VARCHAR, precision 50, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.CURRENCY.src.DIM_CURRENCY_SCD#map5] |
| CURRENCY_TYPE_ID | DIM_CURRENCY_SCD.CURRENCY_TYPE_ID | type DOUBLE, precision 22, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.CURRENCY.src.DIM_CURRENCY_SCD#map6] |
| VALID_FROM | DIM_CURRENCY_SCD.VALID_FROM | type DATETIME, precision 22, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.CURRENCY.src.DIM_CURRENCY_SCD#map7] |
| VALID_TO | DIM_CURRENCY_SCD.VALID_TO | type DATETIME, precision 22, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.CURRENCY.src.DIM_CURRENCY_SCD#map8] |

### Points to note

- The key of CURRENCY is CURRENCY_SUR_ID alone [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.CURRENCY.key.PK_DIM_CURRENCY_SCD], while the table also carries VALID_FROM and VALID_TO [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.CURRENCY.src.DIM_CURRENCY_SCD#map7] [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.CURRENCY.src.DIM_CURRENCY_SCD#map8] (open question Q4).
- CURRENCY_CODE is mapped from a column of type DOUBLE, not a text column [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.CURRENCY.src.DIM_CURRENCY_SCD#map0].

## CUSTOMERS_REC

CUSTOMERS_REC is a business table ("logical table") of the business model "Iryna_Bradzetskaya_MT_Row-level_Security"; the repository declares 1 source for it [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.CUSTOMERS_REC]. In this subject area it has 12 fields and no measures, and all 12 are shown to report authors; its one source is "DIM_CUSTOMERS_REC", described under "Where the data comes from" (counted; no Row identifier). What the ending "_REC" of the name stands for is not recorded (open question Q5).

### Keys

| Key | Primary key (as recorded) | Fields | Evidence |
|---|---|---|---|
| PK_DIM_CUSTOMERS | true | CUST_ID | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.CUSTOMERS_REC.key.PK_DIM_CUSTOMERS] |

### Measures

This table has no measures.

### Fields

Every field is shown to report authors under its own name in the table CUSTOMERS_REC. None can be typed into. The definition kept on each field is one database column of the same name in DIM_CUSTOMERS_REC.

| Field | Shown to report authors as | Kept definition | In sources | Evidence |
|---|---|---|---|---|
| CUST_BALANCE | CUST_BALANCE | DIM_CUSTOMERS_REC.CUST_BALANCE | 1 of 1: "DIM_CUSTOMERS_REC" | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.CUSTOMERS_REC.CUST_BALANCE] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.CUSTOMERS_REC.CUST_BALANCE] |
| CUST_EMAIL | CUST_EMAIL | DIM_CUSTOMERS_REC.CUST_EMAIL | 1 of 1: "DIM_CUSTOMERS_REC" | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.CUSTOMERS_REC.CUST_EMAIL] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.CUSTOMERS_REC.CUST_EMAIL] |
| CUST_FIRST_NAME | CUST_FIRST_NAME | DIM_CUSTOMERS_REC.CUST_FIRST_NAME | 1 of 1: "DIM_CUSTOMERS_REC" | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.CUSTOMERS_REC.CUST_FIRST_NAME] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.CUSTOMERS_REC.CUST_FIRST_NAME] |
| CUST_GENDER | CUST_GENDER | DIM_CUSTOMERS_REC.CUST_GENDER | 1 of 1: "DIM_CUSTOMERS_REC" | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.CUSTOMERS_REC.CUST_GENDER] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.CUSTOMERS_REC.CUST_GENDER] |
| CUST_GEO_ID | CUST_GEO_ID | DIM_CUSTOMERS_REC.CUST_GEO_ID | 1 of 1: "DIM_CUSTOMERS_REC" | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.CUSTOMERS_REC.CUST_GEO_ID] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.CUSTOMERS_REC.CUST_GEO_ID] |
| CUST_ID | CUST_ID | DIM_CUSTOMERS_REC.CUST_ID | 1 of 1: "DIM_CUSTOMERS_REC" | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.CUSTOMERS_REC.CUST_ID] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.CUSTOMERS_REC.CUST_ID] |
| CUST_LAST_NAME | CUST_LAST_NAME | DIM_CUSTOMERS_REC.CUST_LAST_NAME | 1 of 1: "DIM_CUSTOMERS_REC" | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.CUSTOMERS_REC.CUST_LAST_NAME] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.CUSTOMERS_REC.CUST_LAST_NAME] |
| CUST_LEVEL_INCOME | CUST_LEVEL_INCOME | DIM_CUSTOMERS_REC.CUST_LEVEL_INCOME | 1 of 1: "DIM_CUSTOMERS_REC" | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.CUSTOMERS_REC.CUST_LEVEL_INCOME] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.CUSTOMERS_REC.CUST_LEVEL_INCOME] |
| CUST_PASS_NUMBER | CUST_PASS_NUMBER | DIM_CUSTOMERS_REC.CUST_PASS_NUMBER | 1 of 1: "DIM_CUSTOMERS_REC" | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.CUSTOMERS_REC.CUST_PASS_NUMBER] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.CUSTOMERS_REC.CUST_PASS_NUMBER] |
| CUST_YEAR_OF_BIRTH | CUST_YEAR_OF_BIRTH | DIM_CUSTOMERS_REC.CUST_YEAR_OF_BIRTH | 1 of 1: "DIM_CUSTOMERS_REC" | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.CUSTOMERS_REC.CUST_YEAR_OF_BIRTH] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.CUSTOMERS_REC.CUST_YEAR_OF_BIRTH] |
| INSERT_DT | INSERT_DT | DIM_CUSTOMERS_REC.INSERT_DT | 1 of 1: "DIM_CUSTOMERS_REC" | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.CUSTOMERS_REC.INSERT_DT] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.CUSTOMERS_REC.INSERT_DT] |
| UPDATE_DT | UPDATE_DT | DIM_CUSTOMERS_REC.UPDATE_DT | 1 of 1: "DIM_CUSTOMERS_REC" | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.CUSTOMERS_REC.UPDATE_DT] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.CUSTOMERS_REC.UPDATE_DT] |

CUST_BALANCE is a plain field, not a measure [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.CUSTOMERS_REC.CUST_BALANCE].

### Measures and fields report authors do not see

This table has no measures or fields that report authors do not see.

### Where the data comes from

CUSTOMERS_REC has one source.

#### Source "DIM_CUSTOMERS_REC"

The source is active; its priority group is not recorded (empty; it does not mean lowest) [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.CUSTOMERS_REC.src.DIM_CUSTOMERS_REC]. Level of detail (grain): not recorded. The repository gives this source no level of detail [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.CUSTOMERS_REC.src.DIM_CUSTOMERS_REC]. The packet says nothing about a parent-child hierarchy for this source.

No content filter is recorded for this source.

**Database tables it reads.** The one table is a query kept in the repository (type `select`), not a table of the database; its query is given in the section Database table DIM_CUSTOMERS_REC.

| Database table | Schema | Type | Evidence |
|---|---|---|---|
| DIM_CUSTOMERS_REC (see the section Database table DIM_CUSTOMERS_REC) | `Iryna_Bradzetskaya_MT.U_SAL` | `select` | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.CUSTOMERS_REC.src.DIM_CUSTOMERS_REC#table0] |

This source reads one table, so it makes no join.

**Mapping.** Every mapping of this source is one database column of DIM_CUSTOMERS_REC. The rows are in the packet's order.

| Field | Database column | Type, precision, nullable | Evidence |
|---|---|---|---|
| CUST_BALANCE | DIM_CUSTOMERS_REC.CUST_BALANCE | type DOUBLE, precision 22, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.CUSTOMERS_REC.src.DIM_CUSTOMERS_REC#map0] |
| CUST_EMAIL | DIM_CUSTOMERS_REC.CUST_EMAIL | type VARCHAR, precision 50, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.CUSTOMERS_REC.src.DIM_CUSTOMERS_REC#map1] |
| INSERT_DT | DIM_CUSTOMERS_REC.INSERT_DT | type DATETIME, precision 30, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.CUSTOMERS_REC.src.DIM_CUSTOMERS_REC#map10] |
| UPDATE_DT | DIM_CUSTOMERS_REC.UPDATE_DT | type DATETIME, precision 30, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.CUSTOMERS_REC.src.DIM_CUSTOMERS_REC#map11] |
| CUST_FIRST_NAME | DIM_CUSTOMERS_REC.CUST_FIRST_NAME | type VARCHAR, precision 30, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.CUSTOMERS_REC.src.DIM_CUSTOMERS_REC#map2] |
| CUST_GENDER | DIM_CUSTOMERS_REC.CUST_GENDER | type VARCHAR, precision 1, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.CUSTOMERS_REC.src.DIM_CUSTOMERS_REC#map3] |
| CUST_GEO_ID | DIM_CUSTOMERS_REC.CUST_GEO_ID | type DOUBLE, precision 22, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.CUSTOMERS_REC.src.DIM_CUSTOMERS_REC#map4] |
| CUST_ID | DIM_CUSTOMERS_REC.CUST_ID | type DOUBLE, precision 22, nullable not stated | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.CUSTOMERS_REC.src.DIM_CUSTOMERS_REC#map5] |
| CUST_LAST_NAME | DIM_CUSTOMERS_REC.CUST_LAST_NAME | type VARCHAR, precision 30, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.CUSTOMERS_REC.src.DIM_CUSTOMERS_REC#map6] |
| CUST_LEVEL_INCOME | DIM_CUSTOMERS_REC.CUST_LEVEL_INCOME | type DOUBLE, precision 22, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.CUSTOMERS_REC.src.DIM_CUSTOMERS_REC#map7] |
| CUST_PASS_NUMBER | DIM_CUSTOMERS_REC.CUST_PASS_NUMBER | type VARCHAR, precision 30, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.CUSTOMERS_REC.src.DIM_CUSTOMERS_REC#map8] |
| CUST_YEAR_OF_BIRTH | DIM_CUSTOMERS_REC.CUST_YEAR_OF_BIRTH | type DATETIME, precision 30, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.CUSTOMERS_REC.src.DIM_CUSTOMERS_REC#map9] |

### Points to note

- The source reads a query kept in the repository, not a database table; what that query reads is in the section Database table DIM_CUSTOMERS_REC [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.CUSTOMERS_REC.src.DIM_CUSTOMERS_REC#table0].
- CUST_YEAR_OF_BIRTH is mapped from a column recorded as type DATETIME, not a number [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.CUSTOMERS_REC.src.DIM_CUSTOMERS_REC#map9].
- No content filter is recorded for this source, unlike the source of TRANSACTIONS_DAILY (see the section TRANSACTIONS_DAILY).

## CUSTOMERS_SEND

CUSTOMERS_SEND is a business table ("logical table") of the business model "Iryna_Bradzetskaya_MT_Row-level_Security"; the repository declares 1 source for it [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.CUSTOMERS_SEND]. In this subject area it has 12 fields and no measures, and all 12 are shown to report authors; its one source is "DIM_CUSTOMERS_SEND", described under "Where the data comes from" (counted; no Row identifier). What the ending "_SEND" of the name stands for is not recorded (open question Q5).

### Keys

| Key | Primary key (as recorded) | Fields | Evidence |
|---|---|---|---|
| PK_DIM_CUSTOMERS | true | CUST_ID | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.CUSTOMERS_SEND.key.PK_DIM_CUSTOMERS] |

### Measures

This table has no measures.

### Fields

Every field is shown to report authors under its own name in the table CUSTOMERS_SEND. None can be typed into. The definition kept on each field is one database column of the same name in DIM_CUSTOMERS_SEND.

| Field | Shown to report authors as | Kept definition | In sources | Evidence |
|---|---|---|---|---|
| CUST_BALANCE | CUST_BALANCE | DIM_CUSTOMERS_SEND.CUST_BALANCE | 1 of 1: "DIM_CUSTOMERS_SEND" | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.CUSTOMERS_SEND.CUST_BALANCE] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.CUSTOMERS_SEND.CUST_BALANCE] |
| CUST_EMAIL | CUST_EMAIL | DIM_CUSTOMERS_SEND.CUST_EMAIL | 1 of 1: "DIM_CUSTOMERS_SEND" | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.CUSTOMERS_SEND.CUST_EMAIL] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.CUSTOMERS_SEND.CUST_EMAIL] |
| CUST_FIRST_NAME | CUST_FIRST_NAME | DIM_CUSTOMERS_SEND.CUST_FIRST_NAME | 1 of 1: "DIM_CUSTOMERS_SEND" | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.CUSTOMERS_SEND.CUST_FIRST_NAME] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.CUSTOMERS_SEND.CUST_FIRST_NAME] |
| CUST_GENDER | CUST_GENDER | DIM_CUSTOMERS_SEND.CUST_GENDER | 1 of 1: "DIM_CUSTOMERS_SEND" | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.CUSTOMERS_SEND.CUST_GENDER] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.CUSTOMERS_SEND.CUST_GENDER] |
| CUST_GEO_ID | CUST_GEO_ID | DIM_CUSTOMERS_SEND.CUST_GEO_ID | 1 of 1: "DIM_CUSTOMERS_SEND" | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.CUSTOMERS_SEND.CUST_GEO_ID] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.CUSTOMERS_SEND.CUST_GEO_ID] |
| CUST_ID | CUST_ID | DIM_CUSTOMERS_SEND.CUST_ID | 1 of 1: "DIM_CUSTOMERS_SEND" | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.CUSTOMERS_SEND.CUST_ID] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.CUSTOMERS_SEND.CUST_ID] |
| CUST_LAST_NAME | CUST_LAST_NAME | DIM_CUSTOMERS_SEND.CUST_LAST_NAME | 1 of 1: "DIM_CUSTOMERS_SEND" | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.CUSTOMERS_SEND.CUST_LAST_NAME] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.CUSTOMERS_SEND.CUST_LAST_NAME] |
| CUST_LEVEL_INCOME | CUST_LEVEL_INCOME | DIM_CUSTOMERS_SEND.CUST_LEVEL_INCOME | 1 of 1: "DIM_CUSTOMERS_SEND" | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.CUSTOMERS_SEND.CUST_LEVEL_INCOME] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.CUSTOMERS_SEND.CUST_LEVEL_INCOME] |
| CUST_PASS_NUMBER | CUST_PASS_NUMBER | DIM_CUSTOMERS_SEND.CUST_PASS_NUMBER | 1 of 1: "DIM_CUSTOMERS_SEND" | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.CUSTOMERS_SEND.CUST_PASS_NUMBER] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.CUSTOMERS_SEND.CUST_PASS_NUMBER] |
| CUST_YEAR_OF_BIRTH | CUST_YEAR_OF_BIRTH | DIM_CUSTOMERS_SEND.CUST_YEAR_OF_BIRTH | 1 of 1: "DIM_CUSTOMERS_SEND" | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.CUSTOMERS_SEND.CUST_YEAR_OF_BIRTH] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.CUSTOMERS_SEND.CUST_YEAR_OF_BIRTH] |
| INSERT_DT | INSERT_DT | DIM_CUSTOMERS_SEND.INSERT_DT | 1 of 1: "DIM_CUSTOMERS_SEND" | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.CUSTOMERS_SEND.INSERT_DT] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.CUSTOMERS_SEND.INSERT_DT] |
| UPDATE_DT | UPDATE_DT | DIM_CUSTOMERS_SEND.UPDATE_DT | 1 of 1: "DIM_CUSTOMERS_SEND" | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.CUSTOMERS_SEND.UPDATE_DT] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.CUSTOMERS_SEND.UPDATE_DT] |

CUST_BALANCE is a plain field, not a measure [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.CUSTOMERS_SEND.CUST_BALANCE].

### Measures and fields report authors do not see

This table has no measures or fields that report authors do not see.

### Where the data comes from

CUSTOMERS_SEND has one source.

#### Source "DIM_CUSTOMERS_SEND"

The source is active; its priority group is not recorded (empty; it does not mean lowest) [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.CUSTOMERS_SEND.src.DIM_CUSTOMERS_SEND]. Level of detail (grain): not recorded. The repository gives this source no level of detail [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.CUSTOMERS_SEND.src.DIM_CUSTOMERS_SEND]. The packet says nothing about a parent-child hierarchy for this source.

No content filter is recorded for this source.

**Database tables it reads.** The one table is a query kept in the repository (type `select`), not a table of the database; its query is given in the section Database table DIM_CUSTOMERS_SEND.

| Database table | Schema | Type | Evidence |
|---|---|---|---|
| DIM_CUSTOMERS_SEND (see the section Database table DIM_CUSTOMERS_SEND) | `Iryna_Bradzetskaya_MT.U_SAL` | `select` | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.CUSTOMERS_SEND.src.DIM_CUSTOMERS_SEND#table0] |

This source reads one table, so it makes no join.

**Mapping.** Every mapping of this source is one database column of DIM_CUSTOMERS_SEND. The rows are in the packet's order.

| Field | Database column | Type, precision, nullable | Evidence |
|---|---|---|---|
| CUST_BALANCE | DIM_CUSTOMERS_SEND.CUST_BALANCE | type DOUBLE, precision 22, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.CUSTOMERS_SEND.src.DIM_CUSTOMERS_SEND#map0] |
| CUST_EMAIL | DIM_CUSTOMERS_SEND.CUST_EMAIL | type VARCHAR, precision 50, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.CUSTOMERS_SEND.src.DIM_CUSTOMERS_SEND#map1] |
| INSERT_DT | DIM_CUSTOMERS_SEND.INSERT_DT | type DATETIME, precision 30, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.CUSTOMERS_SEND.src.DIM_CUSTOMERS_SEND#map10] |
| UPDATE_DT | DIM_CUSTOMERS_SEND.UPDATE_DT | type DATETIME, precision 30, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.CUSTOMERS_SEND.src.DIM_CUSTOMERS_SEND#map11] |
| CUST_FIRST_NAME | DIM_CUSTOMERS_SEND.CUST_FIRST_NAME | type VARCHAR, precision 30, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.CUSTOMERS_SEND.src.DIM_CUSTOMERS_SEND#map2] |
| CUST_GENDER | DIM_CUSTOMERS_SEND.CUST_GENDER | type VARCHAR, precision 1, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.CUSTOMERS_SEND.src.DIM_CUSTOMERS_SEND#map3] |
| CUST_GEO_ID | DIM_CUSTOMERS_SEND.CUST_GEO_ID | type DOUBLE, precision 22, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.CUSTOMERS_SEND.src.DIM_CUSTOMERS_SEND#map4] |
| CUST_ID | DIM_CUSTOMERS_SEND.CUST_ID | type DOUBLE, precision 22, nullable not stated | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.CUSTOMERS_SEND.src.DIM_CUSTOMERS_SEND#map5] |
| CUST_LAST_NAME | DIM_CUSTOMERS_SEND.CUST_LAST_NAME | type VARCHAR, precision 30, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.CUSTOMERS_SEND.src.DIM_CUSTOMERS_SEND#map6] |
| CUST_LEVEL_INCOME | DIM_CUSTOMERS_SEND.CUST_LEVEL_INCOME | type DOUBLE, precision 22, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.CUSTOMERS_SEND.src.DIM_CUSTOMERS_SEND#map7] |
| CUST_PASS_NUMBER | DIM_CUSTOMERS_SEND.CUST_PASS_NUMBER | type VARCHAR, precision 30, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.CUSTOMERS_SEND.src.DIM_CUSTOMERS_SEND#map8] |
| CUST_YEAR_OF_BIRTH | DIM_CUSTOMERS_SEND.CUST_YEAR_OF_BIRTH | type DATETIME, precision 30, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.CUSTOMERS_SEND.src.DIM_CUSTOMERS_SEND#map9] |

### Points to note

- The source reads a query kept in the repository, not a database table; what that query reads is in the section Database table DIM_CUSTOMERS_SEND [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.CUSTOMERS_SEND.src.DIM_CUSTOMERS_SEND#table0].
- CUST_YEAR_OF_BIRTH is mapped from a column recorded as type DATETIME, not a number [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.CUSTOMERS_SEND.src.DIM_CUSTOMERS_SEND#map9].

## FCT_TRANSACTIONS_ACTUAL

FCT_TRANSACTIONS_ACTUAL is a business table ("logical table") of the business model "Iryna_Bradzetskaya_MT_Row-level_Security"; the repository declares 1 source for it [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.FCT_TRANSACTIONS_ACTUAL]. In this subject area it has 14 fields and no measures, and all 14 are shown to report authors; its one source is "FCT_TRANSACTIONS_ACTUAL", described under "Where the data comes from" (counted; no Row identifier).

### Keys

Keys of the business table FCT_TRANSACTIONS_ACTUAL: not recorded. The repository declares no key for it (listed in the packet without a Row identifier).

### Measures

This table has no measures. AMOUNT_PAYMENT and COUNT_PAYMENT are recorded here as plain fields ("not a measure"), with no rule for how they are summarised [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.FCT_TRANSACTIONS_ACTUAL.AMOUNT_PAYMENT] [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.FCT_TRANSACTIONS_ACTUAL.COUNT_PAYMENT] (open question Q6).

### Fields

Every field is shown to report authors under its own name in the table FCT_TRANSACTIONS_ACTUAL. None can be typed into. The definition kept on each field is one database column of the same name in FCT_TRANSACTIONS_ACTUAL.

| Field | Shown to report authors as | Kept definition | In sources | Evidence |
|---|---|---|---|---|
| AMOUNT_PAYMENT | AMOUNT_PAYMENT | FCT_TRANSACTIONS_ACTUAL.AMOUNT_PAYMENT | 1 of 1: "FCT_TRANSACTIONS_ACTUAL" | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.FCT_TRANSACTIONS_ACTUAL.AMOUNT_PAYMENT] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.FCT_TRANSACTIONS_ACTUAL.AMOUNT_PAYMENT] |
| COUNT_PAYMENT | COUNT_PAYMENT | FCT_TRANSACTIONS_ACTUAL.COUNT_PAYMENT | 1 of 1: "FCT_TRANSACTIONS_ACTUAL" | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.FCT_TRANSACTIONS_ACTUAL.COUNT_PAYMENT] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.FCT_TRANSACTIONS_ACTUAL.COUNT_PAYMENT] |
| CURRENCY_SUR_ID | CURRENCY_SUR_ID | FCT_TRANSACTIONS_ACTUAL.CURRENCY_SUR_ID | 1 of 1: "FCT_TRANSACTIONS_ACTUAL" | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.FCT_TRANSACTIONS_ACTUAL.CURRENCY_SUR_ID] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.FCT_TRANSACTIONS_ACTUAL.CURRENCY_SUR_ID] |
| CUST_SEND_ID | CUST_SEND_ID | FCT_TRANSACTIONS_ACTUAL.CUST_SEND_ID | 1 of 1: "FCT_TRANSACTIONS_ACTUAL" | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.FCT_TRANSACTIONS_ACTUAL.CUST_SEND_ID] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.FCT_TRANSACTIONS_ACTUAL.CUST_SEND_ID] |
| CUS_REC_ID | CUS_REC_ID | FCT_TRANSACTIONS_ACTUAL.CUS_REC_ID | 1 of 1: "FCT_TRANSACTIONS_ACTUAL" | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.FCT_TRANSACTIONS_ACTUAL.CUS_REC_ID] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.FCT_TRANSACTIONS_ACTUAL.CUS_REC_ID] |
| EVENT_DT | EVENT_DT | FCT_TRANSACTIONS_ACTUAL.EVENT_DT | 1 of 1: "FCT_TRANSACTIONS_ACTUAL" | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.FCT_TRANSACTIONS_ACTUAL.EVENT_DT] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.FCT_TRANSACTIONS_ACTUAL.EVENT_DT] |
| GEO_REC_SUR_ID | GEO_REC_SUR_ID | FCT_TRANSACTIONS_ACTUAL.GEO_REC_SUR_ID | 1 of 1: "FCT_TRANSACTIONS_ACTUAL" | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.FCT_TRANSACTIONS_ACTUAL.GEO_REC_SUR_ID] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.FCT_TRANSACTIONS_ACTUAL.GEO_REC_SUR_ID] |
| GEO_SEND_SUR_ID | GEO_SEND_SUR_ID | FCT_TRANSACTIONS_ACTUAL.GEO_SEND_SUR_ID | 1 of 1: "FCT_TRANSACTIONS_ACTUAL" | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.FCT_TRANSACTIONS_ACTUAL.GEO_SEND_SUR_ID] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.FCT_TRANSACTIONS_ACTUAL.GEO_SEND_SUR_ID] |
| INSERT_DT | INSERT_DT | FCT_TRANSACTIONS_ACTUAL.INSERT_DT | 1 of 1: "FCT_TRANSACTIONS_ACTUAL" | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.FCT_TRANSACTIONS_ACTUAL.INSERT_DT] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.FCT_TRANSACTIONS_ACTUAL.INSERT_DT] |
| OPERATION_ID | OPERATION_ID | FCT_TRANSACTIONS_ACTUAL.OPERATION_ID | 1 of 1: "FCT_TRANSACTIONS_ACTUAL" | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.FCT_TRANSACTIONS_ACTUAL.OPERATION_ID] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.FCT_TRANSACTIONS_ACTUAL.OPERATION_ID] |
| PERIOD_ID | PERIOD_ID | FCT_TRANSACTIONS_ACTUAL.PERIOD_ID | 1 of 1: "FCT_TRANSACTIONS_ACTUAL" | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.FCT_TRANSACTIONS_ACTUAL.PERIOD_ID] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.FCT_TRANSACTIONS_ACTUAL.PERIOD_ID] |
| TARIFF_ID | TARIFF_ID | FCT_TRANSACTIONS_ACTUAL.TARIFF_ID | 1 of 1: "FCT_TRANSACTIONS_ACTUAL" | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.FCT_TRANSACTIONS_ACTUAL.TARIFF_ID] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.FCT_TRANSACTIONS_ACTUAL.TARIFF_ID] |
| TA_COUNTRY_REC_ID | TA_COUNTRY_REC_ID | FCT_TRANSACTIONS_ACTUAL.TA_COUNTRY_REC_ID | 1 of 1: "FCT_TRANSACTIONS_ACTUAL" | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.FCT_TRANSACTIONS_ACTUAL.TA_COUNTRY_REC_ID] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.FCT_TRANSACTIONS_ACTUAL.TA_COUNTRY_REC_ID] |
| TA_COUNTRY_SEND_ID | TA_COUNTRY_SEND_ID | FCT_TRANSACTIONS_ACTUAL.TA_COUNTRY_SEND_ID | 1 of 1: "FCT_TRANSACTIONS_ACTUAL" | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.FCT_TRANSACTIONS_ACTUAL.TA_COUNTRY_SEND_ID] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.FCT_TRANSACTIONS_ACTUAL.TA_COUNTRY_SEND_ID] |

What PERIOD_ID, TA_COUNTRY_REC_ID and TA_COUNTRY_SEND_ID hold is not recorded (open question Q6).

### Measures and fields report authors do not see

This table has no measures or fields that report authors do not see.

### Where the data comes from

FCT_TRANSACTIONS_ACTUAL has one source.

#### Source "FCT_TRANSACTIONS_ACTUAL"

The source is active; its priority group is not recorded (empty; it does not mean lowest) [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.FCT_TRANSACTIONS_ACTUAL.src.FCT_TRANSACTIONS_ACTUAL]. Level of detail (grain): not recorded. The repository gives this source no level of detail [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.FCT_TRANSACTIONS_ACTUAL.src.FCT_TRANSACTIONS_ACTUAL]. The packet says nothing about a parent-child hierarchy for this source.

**Content filter.** The source has a content filter. It is quoted here exactly as extracted [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.FCT_TRANSACTIONS_ACTUAL.src.FCT_TRANSACTIONS_ACTUAL]:

```
(
Position (';MT_User_A;' in ';' || valueof(NQ_SESSION.ROLEGUIDS) || ';') > 0 and  "Iryna_Bradzetskaya_MT".""."U_SAL"."FCT_TRANSACTIONS_ACTUAL"."CURRENCY_SUR_ID"   in (12,10,15,11)
)
or
(
Position (';MT_User_F;' in ';' || valueof(NQ_SESSION.ROLEGUIDS) || ';') > 0 and "Iryna_Bradzetskaya_MT".""."U_SAL"."FCT_TRANSACTIONS_ACTUAL"."CURRENCY_SUR_ID"  in (13,14, 16,17)
)
```

Put mechanically: the filter has two bracketed conditions joined by "or". The first looks for the text ";MT_User_A;" inside the value set per user at login `ROLEGUIDS` (framed by ";" on both sides) and requires the database column FCT_TRANSACTIONS_ACTUAL.CURRENCY_SUR_ID to be one of 12, 10, 15, 11. The second does the same with ";MT_User_F;" and the values 13, 14, 16, 17 [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.FCT_TRANSACTIONS_ACTUAL.src.FCT_TRANSACTIONS_ACTUAL]. The variable `ROLEGUIDS` is described in the section Values set per user or for the whole system. Which currencies the numbers stand for, and what "MT_User_A" and "MT_User_F" name, are not recorded (open question Q3).

**Row filter.** A row filter also restricts this source; it is described in the section Security (listed in the packet without a Row identifier).

**Database tables it reads.** The one table is a query kept in the repository (type `select`), not a table of the database; its query is given in the section Database table FCT_TRANSACTIONS_ACTUAL.

| Database table | Schema | Type | Evidence |
|---|---|---|---|
| FCT_TRANSACTIONS_ACTUAL (see the section Database table FCT_TRANSACTIONS_ACTUAL) | `Iryna_Bradzetskaya_MT.U_SAL` | `select` | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.FCT_TRANSACTIONS_ACTUAL.src.FCT_TRANSACTIONS_ACTUAL#table0] |

This source reads one table, so it makes no join.

**Mapping.** Every mapping of this source is one database column of FCT_TRANSACTIONS_ACTUAL. The rows are in the packet's order.

| Field | Database column | Type, precision, nullable | Evidence |
|---|---|---|---|
| AMOUNT_PAYMENT | FCT_TRANSACTIONS_ACTUAL.AMOUNT_PAYMENT | type DOUBLE, precision 22, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.FCT_TRANSACTIONS_ACTUAL.src.FCT_TRANSACTIONS_ACTUAL#map0] |
| COUNT_PAYMENT | FCT_TRANSACTIONS_ACTUAL.COUNT_PAYMENT | type DOUBLE, precision 22, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.FCT_TRANSACTIONS_ACTUAL.src.FCT_TRANSACTIONS_ACTUAL#map1] |
| PERIOD_ID | FCT_TRANSACTIONS_ACTUAL.PERIOD_ID | type DOUBLE, precision 22, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.FCT_TRANSACTIONS_ACTUAL.src.FCT_TRANSACTIONS_ACTUAL#map10] |
| TA_COUNTRY_REC_ID | FCT_TRANSACTIONS_ACTUAL.TA_COUNTRY_REC_ID | type DOUBLE, precision 22, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.FCT_TRANSACTIONS_ACTUAL.src.FCT_TRANSACTIONS_ACTUAL#map11] |
| TA_COUNTRY_SEND_ID | FCT_TRANSACTIONS_ACTUAL.TA_COUNTRY_SEND_ID | type DOUBLE, precision 22, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.FCT_TRANSACTIONS_ACTUAL.src.FCT_TRANSACTIONS_ACTUAL#map12] |
| TARIFF_ID | FCT_TRANSACTIONS_ACTUAL.TARIFF_ID | type DOUBLE, precision 22, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.FCT_TRANSACTIONS_ACTUAL.src.FCT_TRANSACTIONS_ACTUAL#map13] |
| CURRENCY_SUR_ID | FCT_TRANSACTIONS_ACTUAL.CURRENCY_SUR_ID | type DOUBLE, precision 22, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.FCT_TRANSACTIONS_ACTUAL.src.FCT_TRANSACTIONS_ACTUAL#map2] |
| CUS_REC_ID | FCT_TRANSACTIONS_ACTUAL.CUS_REC_ID | type DOUBLE, precision 22, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.FCT_TRANSACTIONS_ACTUAL.src.FCT_TRANSACTIONS_ACTUAL#map3] |
| CUST_SEND_ID | FCT_TRANSACTIONS_ACTUAL.CUST_SEND_ID | type DOUBLE, precision 22, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.FCT_TRANSACTIONS_ACTUAL.src.FCT_TRANSACTIONS_ACTUAL#map4] |
| EVENT_DT | FCT_TRANSACTIONS_ACTUAL.EVENT_DT | type DATETIME, precision 22, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.FCT_TRANSACTIONS_ACTUAL.src.FCT_TRANSACTIONS_ACTUAL#map5] |
| GEO_REC_SUR_ID | FCT_TRANSACTIONS_ACTUAL.GEO_REC_SUR_ID | type DOUBLE, precision 22, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.FCT_TRANSACTIONS_ACTUAL.src.FCT_TRANSACTIONS_ACTUAL#map6] |
| GEO_SEND_SUR_ID | FCT_TRANSACTIONS_ACTUAL.GEO_SEND_SUR_ID | type DOUBLE, precision 22, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.FCT_TRANSACTIONS_ACTUAL.src.FCT_TRANSACTIONS_ACTUAL#map7] |
| INSERT_DT | FCT_TRANSACTIONS_ACTUAL.INSERT_DT | type DATETIME, precision 22, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.FCT_TRANSACTIONS_ACTUAL.src.FCT_TRANSACTIONS_ACTUAL#map8] |
| OPERATION_ID | FCT_TRANSACTIONS_ACTUAL.OPERATION_ID | type DOUBLE, precision 22, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.FCT_TRANSACTIONS_ACTUAL.src.FCT_TRANSACTIONS_ACTUAL#map9] |

### Points to note

- The table has no measures: AMOUNT_PAYMENT and COUNT_PAYMENT are plain fields [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.FCT_TRANSACTIONS_ACTUAL.AMOUNT_PAYMENT] (open question Q6).
- No key is declared for the business table FCT_TRANSACTIONS_ACTUAL (listed in the packet without a Row identifier).
- The only source has a content filter that reads `ROLEGUIDS` and looks for ";MT_User_A;" and ";MT_User_F;", and a row filter in the section Security restricts it as well [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.FCT_TRANSACTIONS_ACTUAL.src.FCT_TRANSACTIONS_ACTUAL] (open question Q3).
- The source reads a query kept in the repository, not a database table [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.FCT_TRANSACTIONS_ACTUAL.src.FCT_TRANSACTIONS_ACTUAL#table0].

## LOCATIONS_REC

LOCATIONS_REC is a business table ("logical table") of the business model "Iryna_Bradzetskaya_MT_Row-level_Security"; the repository declares 1 source for it [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.LOCATIONS_REC]. In this subject area it has 16 fields and no measures, and all 16 are shown to report authors; its one source is "DIM_LOCATIONS_REC", described under "Where the data comes from" (counted; no Row identifier). What the ending "_REC" stands for is not recorded (open question Q5).

### Keys

| Key | Primary key (as recorded) | Fields | Evidence |
|---|---|---|---|
| PC_LOC | true | GEO_ID | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.LOCATIONS_REC.key.PC_LOC] |

### Measures

This table has no measures.

### Fields

Every field is shown to report authors under its own name in the table LOCATIONS_REC. None can be typed into. The definition kept on each field is one database column of the same name in DIM_LOCATIONS_REC.

| Field | Shown to report authors as | Kept definition | In sources | Evidence |
|---|---|---|---|---|
| COUNTRY_CODE_A2 | COUNTRY_CODE_A2 | DIM_LOCATIONS_REC.COUNTRY_CODE_A2 | 1 of 1: "DIM_LOCATIONS_REC" | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.LOCATIONS_REC.COUNTRY_CODE_A2] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.LOCATIONS_REC.COUNTRY_CODE_A2] |
| COUNTRY_CODE_A3 | COUNTRY_CODE_A3 | DIM_LOCATIONS_REC.COUNTRY_CODE_A3 | 1 of 1: "DIM_LOCATIONS_REC" | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.LOCATIONS_REC.COUNTRY_CODE_A3] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.LOCATIONS_REC.COUNTRY_CODE_A3] |
| COUNTRY_ID | COUNTRY_ID | DIM_LOCATIONS_REC.COUNTRY_ID | 1 of 1: "DIM_LOCATIONS_REC" | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.LOCATIONS_REC.COUNTRY_ID] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.LOCATIONS_REC.COUNTRY_ID] |
| GEO_COUNTRY_ID | GEO_COUNTRY_ID | DIM_LOCATIONS_REC.GEO_COUNTRY_ID | 1 of 1: "DIM_LOCATIONS_REC" | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.LOCATIONS_REC.GEO_COUNTRY_ID] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.LOCATIONS_REC.GEO_COUNTRY_ID] |
| GEO_COUNTRY_NAME | GEO_COUNTRY_NAME | DIM_LOCATIONS_REC.GEO_COUNTRY_NAME | 1 of 1: "DIM_LOCATIONS_REC" | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.LOCATIONS_REC.GEO_COUNTRY_NAME] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.LOCATIONS_REC.GEO_COUNTRY_NAME] |
| GEO_ID | GEO_ID | DIM_LOCATIONS_REC.GEO_ID | 1 of 1: "DIM_LOCATIONS_REC" | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.LOCATIONS_REC.GEO_ID] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.LOCATIONS_REC.GEO_ID] |
| GEO_PART_CODE | GEO_PART_CODE | DIM_LOCATIONS_REC.GEO_PART_CODE | 1 of 1: "DIM_LOCATIONS_REC" | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.LOCATIONS_REC.GEO_PART_CODE] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.LOCATIONS_REC.GEO_PART_CODE] |
| GEO_PART_DESC | GEO_PART_DESC | DIM_LOCATIONS_REC.GEO_PART_DESC | 1 of 1: "DIM_LOCATIONS_REC" | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.LOCATIONS_REC.GEO_PART_DESC] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.LOCATIONS_REC.GEO_PART_DESC] |
| GEO_PART_ID | GEO_PART_ID | DIM_LOCATIONS_REC.GEO_PART_ID | 1 of 1: "DIM_LOCATIONS_REC" | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.LOCATIONS_REC.GEO_PART_ID] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.LOCATIONS_REC.GEO_PART_ID] |
| GEO_REGION_CODE | GEO_REGION_CODE | DIM_LOCATIONS_REC.GEO_REGION_CODE | 1 of 1: "DIM_LOCATIONS_REC" | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.LOCATIONS_REC.GEO_REGION_CODE] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.LOCATIONS_REC.GEO_REGION_CODE] |
| GEO_REGION_DESC | GEO_REGION_DESC | DIM_LOCATIONS_REC.GEO_REGION_DESC | 1 of 1: "DIM_LOCATIONS_REC" | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.LOCATIONS_REC.GEO_REGION_DESC] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.LOCATIONS_REC.GEO_REGION_DESC] |
| GEO_REGION_ID | GEO_REGION_ID | DIM_LOCATIONS_REC.GEO_REGION_ID | 1 of 1: "DIM_LOCATIONS_REC" | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.LOCATIONS_REC.GEO_REGION_ID] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.LOCATIONS_REC.GEO_REGION_ID] |
| GEO_SURR_ID | GEO_SURR_ID | DIM_LOCATIONS_REC.GEO_SURR_ID | 1 of 1: "DIM_LOCATIONS_REC" | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.LOCATIONS_REC.GEO_SURR_ID] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.LOCATIONS_REC.GEO_SURR_ID] |
| LEVEL_CODE | LEVEL_CODE | DIM_LOCATIONS_REC.LEVEL_CODE | 1 of 1: "DIM_LOCATIONS_REC" | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.LOCATIONS_REC.LEVEL_CODE] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.LOCATIONS_REC.LEVEL_CODE] |
| VALID_FROM | VALID_FROM | DIM_LOCATIONS_REC.VALID_FROM | 1 of 1: "DIM_LOCATIONS_REC" | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.LOCATIONS_REC.VALID_FROM] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.LOCATIONS_REC.VALID_FROM] |
| VALID_TO | VALID_TO | DIM_LOCATIONS_REC.VALID_TO | 1 of 1: "DIM_LOCATIONS_REC" | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.LOCATIONS_REC.VALID_TO] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.LOCATIONS_REC.VALID_TO] |

The table carries both GEO_ID, its key, and GEO_SURR_ID, as well as LEVEL_CODE, VALID_FROM and VALID_TO. Which of GEO_ID and GEO_SURR_ID identifies one version of a location, and what LEVEL_CODE holds, are not recorded (open question Q7).

### Measures and fields report authors do not see

This table has no measures or fields that report authors do not see.

### Where the data comes from

LOCATIONS_REC has one source.

#### Source "DIM_LOCATIONS_REC"

The source is active; its priority group is not recorded (empty; it does not mean lowest) [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.LOCATIONS_REC.src.DIM_LOCATIONS_REC]. Level of detail (grain): not recorded. The repository gives this source no level of detail [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.LOCATIONS_REC.src.DIM_LOCATIONS_REC]. The packet says nothing about a parent-child hierarchy for this source.

No content filter is recorded for this source.

**Database tables it reads.** The one table is a query kept in the repository (type `select`), not a table of the database; its query is given in the section Database table DIM_LOCATIONS_REC.

| Database table | Schema | Type | Evidence |
|---|---|---|---|
| DIM_LOCATIONS_REC (see the section Database table DIM_LOCATIONS_REC) | `Iryna_Bradzetskaya_MT.U_SAL` | `select` | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.LOCATIONS_REC.src.DIM_LOCATIONS_REC#table0] |

This source reads one table, so it makes no join.

**Mapping.** Every mapping of this source is one database column of DIM_LOCATIONS_REC. The rows are in the packet's order.

| Field | Database column | Type, precision, nullable | Evidence |
|---|---|---|---|
| COUNTRY_CODE_A2 | DIM_LOCATIONS_REC.COUNTRY_CODE_A2 | type VARCHAR, precision 5, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.LOCATIONS_REC.src.DIM_LOCATIONS_REC#map0] |
| COUNTRY_CODE_A3 | DIM_LOCATIONS_REC.COUNTRY_CODE_A3 | type VARCHAR, precision 5, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.LOCATIONS_REC.src.DIM_LOCATIONS_REC#map1] |
| GEO_REGION_DESC | DIM_LOCATIONS_REC.GEO_REGION_DESC | type VARCHAR, precision 50, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.LOCATIONS_REC.src.DIM_LOCATIONS_REC#map10] |
| GEO_REGION_ID | DIM_LOCATIONS_REC.GEO_REGION_ID | type DOUBLE, precision 22, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.LOCATIONS_REC.src.DIM_LOCATIONS_REC#map11] |
| GEO_SURR_ID | DIM_LOCATIONS_REC.GEO_SURR_ID | type DOUBLE, precision 22, nullable not stated | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.LOCATIONS_REC.src.DIM_LOCATIONS_REC#map12] |
| LEVEL_CODE | DIM_LOCATIONS_REC.LEVEL_CODE | type VARCHAR, precision 30, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.LOCATIONS_REC.src.DIM_LOCATIONS_REC#map13] |
| VALID_FROM | DIM_LOCATIONS_REC.VALID_FROM | type DATETIME, precision 30, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.LOCATIONS_REC.src.DIM_LOCATIONS_REC#map14] |
| VALID_TO | DIM_LOCATIONS_REC.VALID_TO | type DATETIME, precision 30, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.LOCATIONS_REC.src.DIM_LOCATIONS_REC#map15] |
| COUNTRY_ID | DIM_LOCATIONS_REC.COUNTRY_ID | type DOUBLE, precision 22, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.LOCATIONS_REC.src.DIM_LOCATIONS_REC#map2] |
| GEO_COUNTRY_ID | DIM_LOCATIONS_REC.GEO_COUNTRY_ID | type DOUBLE, precision 22, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.LOCATIONS_REC.src.DIM_LOCATIONS_REC#map3] |
| GEO_COUNTRY_NAME | DIM_LOCATIONS_REC.GEO_COUNTRY_NAME | type VARCHAR, precision 80, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.LOCATIONS_REC.src.DIM_LOCATIONS_REC#map4] |
| GEO_ID | DIM_LOCATIONS_REC.GEO_ID | type DOUBLE, precision 22, nullable not stated | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.LOCATIONS_REC.src.DIM_LOCATIONS_REC#map5] |
| GEO_PART_CODE | DIM_LOCATIONS_REC.GEO_PART_CODE | type VARCHAR, precision 30, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.LOCATIONS_REC.src.DIM_LOCATIONS_REC#map6] |
| GEO_PART_DESC | DIM_LOCATIONS_REC.GEO_PART_DESC | type VARCHAR, precision 50, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.LOCATIONS_REC.src.DIM_LOCATIONS_REC#map7] |
| GEO_PART_ID | DIM_LOCATIONS_REC.GEO_PART_ID | type DOUBLE, precision 22, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.LOCATIONS_REC.src.DIM_LOCATIONS_REC#map8] |
| GEO_REGION_CODE | DIM_LOCATIONS_REC.GEO_REGION_CODE | type VARCHAR, precision 3, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.LOCATIONS_REC.src.DIM_LOCATIONS_REC#map9] |

### Points to note

- The source reads a query kept in the repository, not a database table; what that query reads is in the section Database table DIM_LOCATIONS_REC [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.LOCATIONS_REC.src.DIM_LOCATIONS_REC#table0].
- Both GEO_ID and GEO_SURR_ID are mapped from columns whose nullable is "not stated", while every other column of this source is "nullable true" [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.LOCATIONS_REC.src.DIM_LOCATIONS_REC#map5] [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.LOCATIONS_REC.src.DIM_LOCATIONS_REC#map12] (open question Q7).
- No content filter is recorded for this source.

## LOCATIONS_SEND

LOCATIONS_SEND is a business table ("logical table") of the business model "Iryna_Bradzetskaya_MT_Row-level_Security"; the repository declares 1 source for it [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.LOCATIONS_SEND]. In this subject area it has 16 fields and no measures, and all 16 are shown to report authors; its one source is "DIM_LOCATIONS_SEND", described under "Where the data comes from" (counted; no Row identifier). What the ending "_SEND" stands for is not recorded (open question Q5).

### Keys

| Key | Primary key (as recorded) | Fields | Evidence |
|---|---|---|---|
| PC_LOC | true | GEO_ID | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.LOCATIONS_SEND.key.PC_LOC] |

### Measures

This table has no measures.

### Fields

Every field is shown to report authors under its own name in the table LOCATIONS_SEND. None can be typed into. The definition kept on each field is one database column of the same name in DIM_LOCATIONS_SEND.

| Field | Shown to report authors as | Kept definition | In sources | Evidence |
|---|---|---|---|---|
| COUNTRY_CODE_A2 | COUNTRY_CODE_A2 | DIM_LOCATIONS_SEND.COUNTRY_CODE_A2 | 1 of 1: "DIM_LOCATIONS_SEND" | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.LOCATIONS_SEND.COUNTRY_CODE_A2] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.LOCATIONS_SEND.COUNTRY_CODE_A2] |
| COUNTRY_CODE_A3 | COUNTRY_CODE_A3 | DIM_LOCATIONS_SEND.COUNTRY_CODE_A3 | 1 of 1: "DIM_LOCATIONS_SEND" | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.LOCATIONS_SEND.COUNTRY_CODE_A3] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.LOCATIONS_SEND.COUNTRY_CODE_A3] |
| COUNTRY_ID | COUNTRY_ID | DIM_LOCATIONS_SEND.COUNTRY_ID | 1 of 1: "DIM_LOCATIONS_SEND" | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.LOCATIONS_SEND.COUNTRY_ID] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.LOCATIONS_SEND.COUNTRY_ID] |
| GEO_COUNTRY_ID | GEO_COUNTRY_ID | DIM_LOCATIONS_SEND.GEO_COUNTRY_ID | 1 of 1: "DIM_LOCATIONS_SEND" | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.LOCATIONS_SEND.GEO_COUNTRY_ID] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.LOCATIONS_SEND.GEO_COUNTRY_ID] |
| GEO_COUNTRY_NAME | GEO_COUNTRY_NAME | DIM_LOCATIONS_SEND.GEO_COUNTRY_NAME | 1 of 1: "DIM_LOCATIONS_SEND" | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.LOCATIONS_SEND.GEO_COUNTRY_NAME] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.LOCATIONS_SEND.GEO_COUNTRY_NAME] |
| GEO_ID | GEO_ID | DIM_LOCATIONS_SEND.GEO_ID | 1 of 1: "DIM_LOCATIONS_SEND" | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.LOCATIONS_SEND.GEO_ID] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.LOCATIONS_SEND.GEO_ID] |
| GEO_PART_CODE | GEO_PART_CODE | DIM_LOCATIONS_SEND.GEO_PART_CODE | 1 of 1: "DIM_LOCATIONS_SEND" | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.LOCATIONS_SEND.GEO_PART_CODE] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.LOCATIONS_SEND.GEO_PART_CODE] |
| GEO_PART_DESC | GEO_PART_DESC | DIM_LOCATIONS_SEND.GEO_PART_DESC | 1 of 1: "DIM_LOCATIONS_SEND" | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.LOCATIONS_SEND.GEO_PART_DESC] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.LOCATIONS_SEND.GEO_PART_DESC] |
| GEO_PART_ID | GEO_PART_ID | DIM_LOCATIONS_SEND.GEO_PART_ID | 1 of 1: "DIM_LOCATIONS_SEND" | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.LOCATIONS_SEND.GEO_PART_ID] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.LOCATIONS_SEND.GEO_PART_ID] |
| GEO_REGION_CODE | GEO_REGION_CODE | DIM_LOCATIONS_SEND.GEO_REGION_CODE | 1 of 1: "DIM_LOCATIONS_SEND" | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.LOCATIONS_SEND.GEO_REGION_CODE] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.LOCATIONS_SEND.GEO_REGION_CODE] |
| GEO_REGION_DESC | GEO_REGION_DESC | DIM_LOCATIONS_SEND.GEO_REGION_DESC | 1 of 1: "DIM_LOCATIONS_SEND" | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.LOCATIONS_SEND.GEO_REGION_DESC] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.LOCATIONS_SEND.GEO_REGION_DESC] |
| GEO_REGION_ID | GEO_REGION_ID | DIM_LOCATIONS_SEND.GEO_REGION_ID | 1 of 1: "DIM_LOCATIONS_SEND" | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.LOCATIONS_SEND.GEO_REGION_ID] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.LOCATIONS_SEND.GEO_REGION_ID] |
| GEO_SURR_ID | GEO_SURR_ID | DIM_LOCATIONS_SEND.GEO_SURR_ID | 1 of 1: "DIM_LOCATIONS_SEND" | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.LOCATIONS_SEND.GEO_SURR_ID] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.LOCATIONS_SEND.GEO_SURR_ID] |
| LEVEL_CODE | LEVEL_CODE | DIM_LOCATIONS_SEND.LEVEL_CODE | 1 of 1: "DIM_LOCATIONS_SEND" | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.LOCATIONS_SEND.LEVEL_CODE] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.LOCATIONS_SEND.LEVEL_CODE] |
| VALID_FROM | VALID_FROM | DIM_LOCATIONS_SEND.VALID_FROM | 1 of 1: "DIM_LOCATIONS_SEND" | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.LOCATIONS_SEND.VALID_FROM] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.LOCATIONS_SEND.VALID_FROM] |
| VALID_TO | VALID_TO | DIM_LOCATIONS_SEND.VALID_TO | 1 of 1: "DIM_LOCATIONS_SEND" | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.LOCATIONS_SEND.VALID_TO] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.LOCATIONS_SEND.VALID_TO] |

The table carries both GEO_ID, its key, and GEO_SURR_ID, as well as LEVEL_CODE, VALID_FROM and VALID_TO. Which of GEO_ID and GEO_SURR_ID identifies one version of a location, and what LEVEL_CODE holds, are not recorded (open question Q7).

### Measures and fields report authors do not see

This table has no measures or fields that report authors do not see.

### Where the data comes from

LOCATIONS_SEND has one source.

#### Source "DIM_LOCATIONS_SEND"

The source is active; its priority group is not recorded (empty; it does not mean lowest) [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.LOCATIONS_SEND.src.DIM_LOCATIONS_SEND]. Level of detail (grain): not recorded. The repository gives this source no level of detail [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.LOCATIONS_SEND.src.DIM_LOCATIONS_SEND]. The packet says nothing about a parent-child hierarchy for this source.

No content filter is recorded for this source.

**Database tables it reads.** The one table is a query kept in the repository (type `select`), not a table of the database; its query is given in the section Database table DIM_LOCATIONS_SEND.

| Database table | Schema | Type | Evidence |
|---|---|---|---|
| DIM_LOCATIONS_SEND (see the section Database table DIM_LOCATIONS_SEND) | `Iryna_Bradzetskaya_MT.U_SAL` | `select` | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.LOCATIONS_SEND.src.DIM_LOCATIONS_SEND#table0] |

This source reads one table, so it makes no join.

**Mapping.** Every mapping of this source is one database column of DIM_LOCATIONS_SEND. The rows are in the packet's order.

| Field | Database column | Type, precision, nullable | Evidence |
|---|---|---|---|
| COUNTRY_CODE_A2 | DIM_LOCATIONS_SEND.COUNTRY_CODE_A2 | type VARCHAR, precision 5, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.LOCATIONS_SEND.src.DIM_LOCATIONS_SEND#map0] |
| COUNTRY_CODE_A3 | DIM_LOCATIONS_SEND.COUNTRY_CODE_A3 | type VARCHAR, precision 5, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.LOCATIONS_SEND.src.DIM_LOCATIONS_SEND#map1] |
| GEO_REGION_DESC | DIM_LOCATIONS_SEND.GEO_REGION_DESC | type VARCHAR, precision 50, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.LOCATIONS_SEND.src.DIM_LOCATIONS_SEND#map10] |
| GEO_REGION_ID | DIM_LOCATIONS_SEND.GEO_REGION_ID | type DOUBLE, precision 22, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.LOCATIONS_SEND.src.DIM_LOCATIONS_SEND#map11] |
| GEO_SURR_ID | DIM_LOCATIONS_SEND.GEO_SURR_ID | type DOUBLE, precision 22, nullable not stated | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.LOCATIONS_SEND.src.DIM_LOCATIONS_SEND#map12] |
| LEVEL_CODE | DIM_LOCATIONS_SEND.LEVEL_CODE | type VARCHAR, precision 30, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.LOCATIONS_SEND.src.DIM_LOCATIONS_SEND#map13] |
| VALID_FROM | DIM_LOCATIONS_SEND.VALID_FROM | type DATETIME, precision 30, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.LOCATIONS_SEND.src.DIM_LOCATIONS_SEND#map14] |
| VALID_TO | DIM_LOCATIONS_SEND.VALID_TO | type DATETIME, precision 30, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.LOCATIONS_SEND.src.DIM_LOCATIONS_SEND#map15] |
| COUNTRY_ID | DIM_LOCATIONS_SEND.COUNTRY_ID | type DOUBLE, precision 22, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.LOCATIONS_SEND.src.DIM_LOCATIONS_SEND#map2] |
| GEO_COUNTRY_ID | DIM_LOCATIONS_SEND.GEO_COUNTRY_ID | type DOUBLE, precision 22, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.LOCATIONS_SEND.src.DIM_LOCATIONS_SEND#map3] |
| GEO_COUNTRY_NAME | DIM_LOCATIONS_SEND.GEO_COUNTRY_NAME | type VARCHAR, precision 80, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.LOCATIONS_SEND.src.DIM_LOCATIONS_SEND#map4] |
| GEO_ID | DIM_LOCATIONS_SEND.GEO_ID | type DOUBLE, precision 22, nullable not stated | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.LOCATIONS_SEND.src.DIM_LOCATIONS_SEND#map5] |
| GEO_PART_CODE | DIM_LOCATIONS_SEND.GEO_PART_CODE | type VARCHAR, precision 30, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.LOCATIONS_SEND.src.DIM_LOCATIONS_SEND#map6] |
| GEO_PART_DESC | DIM_LOCATIONS_SEND.GEO_PART_DESC | type VARCHAR, precision 50, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.LOCATIONS_SEND.src.DIM_LOCATIONS_SEND#map7] |
| GEO_PART_ID | DIM_LOCATIONS_SEND.GEO_PART_ID | type DOUBLE, precision 22, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.LOCATIONS_SEND.src.DIM_LOCATIONS_SEND#map8] |
| GEO_REGION_CODE | DIM_LOCATIONS_SEND.GEO_REGION_CODE | type VARCHAR, precision 3, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.LOCATIONS_SEND.src.DIM_LOCATIONS_SEND#map9] |

### Points to note

- The source reads a query kept in the repository, not a database table; what that query reads is in the section Database table DIM_LOCATIONS_SEND [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.LOCATIONS_SEND.src.DIM_LOCATIONS_SEND#table0].
- Both GEO_ID and GEO_SURR_ID are mapped from columns whose nullable is "not stated", while every other column of this source is "nullable true" [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.LOCATIONS_SEND.src.DIM_LOCATIONS_SEND#map5] [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.LOCATIONS_SEND.src.DIM_LOCATIONS_SEND#map12] (open question Q7).
- No content filter is recorded for this source.

## OPERATIONS

OPERATIONS is a business table ("logical table") of the business model "Iryna_Bradzetskaya_MT_Row-level_Security"; the repository declares 1 source for it [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.OPERATIONS]. In this subject area it has 8 fields and no measures, and all 8 are shown to report authors; its one source is "DIM_OPERATIONS", which reads two database tables (counted; no Row identifier).

### Keys

| Key | Primary key (as recorded) | Fields | Evidence |
|---|---|---|---|
| PK_DIM_OPERATIONS | true | OPERATION_ID | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.OPERATIONS.key.PK_DIM_OPERATIONS] |

### Measures

This table has no measures.

### Fields

Every field is shown to report authors under its own name in the table OPERATIONS. None can be typed into. The definition kept on each field is one database column of the same name: four in DIM_OPERATIONS and four in DIM_OPERATION_METHODS.

| Field | Shown to report authors as | Kept definition | In sources | Evidence |
|---|---|---|---|---|
| OPERATION_ID | OPERATION_ID | DIM_OPERATIONS.OPERATION_ID | 1 of 1: "DIM_OPERATIONS" | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.OPERATIONS.OPERATION_ID] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.OPERATIONS.OPERATION_ID] |
| OPERATION_MAX_AMOUNT | OPERATION_MAX_AMOUNT | DIM_OPERATIONS.OPERATION_MAX_AMOUNT | 1 of 1: "DIM_OPERATIONS" | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.OPERATIONS.OPERATION_MAX_AMOUNT] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.OPERATIONS.OPERATION_MAX_AMOUNT] |
| OPERATION_METHOD_ID | OPERATION_METHOD_ID | DIM_OPERATION_METHODS.OPERATION_METHOD_ID | 1 of 1: "DIM_OPERATIONS" | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.OPERATIONS.OPERATION_METHOD_ID] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.OPERATIONS.OPERATION_METHOD_ID] |
| OPERATION_METHOD_NAME | OPERATION_METHOD_NAME | DIM_OPERATION_METHODS.OPERATION_METHOD_NAME | 1 of 1: "DIM_OPERATIONS" | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.OPERATIONS.OPERATION_METHOD_NAME] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.OPERATIONS.OPERATION_METHOD_NAME] |
| OPERATION_METHOD_TYPE | OPERATION_METHOD_TYPE | DIM_OPERATION_METHODS.OPERATION_METHOD_TYPE | 1 of 1: "DIM_OPERATIONS" | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.OPERATIONS.OPERATION_METHOD_TYPE] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.OPERATIONS.OPERATION_METHOD_TYPE] |
| OPERATION_METHOD_TYPE_ID | OPERATION_METHOD_TYPE_ID | DIM_OPERATION_METHODS.OPERATION_METHOD_TYPE_ID | 1 of 1: "DIM_OPERATIONS" | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.OPERATIONS.OPERATION_METHOD_TYPE_ID] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.OPERATIONS.OPERATION_METHOD_TYPE_ID] |
| OPERATION_MIN_AMOUNT | OPERATION_MIN_AMOUNT | DIM_OPERATIONS.OPERATION_MIN_AMOUNT | 1 of 1: "DIM_OPERATIONS" | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.OPERATIONS.OPERATION_MIN_AMOUNT] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.OPERATIONS.OPERATION_MIN_AMOUNT] |
| OPERATION_NAME | OPERATION_NAME | DIM_OPERATIONS.OPERATION_NAME | 1 of 1: "DIM_OPERATIONS" | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.OPERATIONS.OPERATION_NAME] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.OPERATIONS.OPERATION_NAME] |

OPERATION_MIN_AMOUNT and OPERATION_MAX_AMOUNT are plain fields, not measures [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.OPERATIONS.OPERATION_MIN_AMOUNT] [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.OPERATIONS.OPERATION_MAX_AMOUNT].

### Measures and fields report authors do not see

This table has no measures or fields that report authors do not see.

### Where the data comes from

OPERATIONS has one source.

#### Source "DIM_OPERATIONS"

The source is active; its priority group is not recorded (empty; it does not mean lowest) [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.OPERATIONS.src.DIM_OPERATIONS]. Level of detail (grain): not recorded. The repository gives this source no level of detail [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.OPERATIONS.src.DIM_OPERATIONS]. The packet says nothing about a parent-child hierarchy for this source.

No content filter is recorded for this source.

**Database tables it reads.** The source is built on 2 database tables, so it joins them.

| Database table | Schema | Type | Evidence |
|---|---|---|---|
| DIM_OPERATIONS (see the section Database table DIM_OPERATIONS) | `Iryna_Bradzetskaya_MT.U_SAL` | `none` | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.OPERATIONS.src.DIM_OPERATIONS#table0] |
| DIM_OPERATION_METHODS (see the section Database table DIM_OPERATION_METHODS); a query kept in the repository | `Iryna_Bradzetskaya_MT.U_SAL` | `select` | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.OPERATIONS.src.DIM_OPERATIONS#table1] |

**Joins between them.** The packet gives 2 lines for 1 join: the repository lists a join once under each of its two tables. "Left" and "right" in a join type, and the cardinality, read from the first table named on the line to the second.

| Listed under | From | To | Join type | Cardinality | On | Evidence |
|---|---|---|---|---|---|---|
| DIM_OPERATIONS | DIM_OPERATIONS | DIM_OPERATION_METHODS | `LeftOuter` | `MANY TO ONE` | foreign key "DIM_OPERATIONS_Foreign Key": DIM_OPERATIONS (OPERATION_METHOD_ID) = DIM_OPERATION_METHODS (OPERATION_METHOD_ID) | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.OPERATIONS.src.DIM_OPERATIONS#table0.join0] |
| DIM_OPERATION_METHODS | DIM_OPERATION_METHODS | DIM_OPERATIONS | `RightOuter` | ONE TO MANY | foreign key "DIM_OPERATIONS_Foreign Key": DIM_OPERATIONS (OPERATION_METHOD_ID) = DIM_OPERATION_METHODS (OPERATION_METHOD_ID) | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.OPERATIONS.src.DIM_OPERATIONS#table1.join0] |

The cardinality "ONE TO MANY" is printed in the packet with a leading space; it is copied here without it.

**Mapping.** Every mapping of this source is one database column.

| Field | Database column | Type, precision, nullable | Evidence |
|---|---|---|---|
| OPERATION_ID | DIM_OPERATIONS.OPERATION_ID | type DOUBLE, precision 22, nullable not stated | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.OPERATIONS.src.DIM_OPERATIONS#map0] |
| OPERATION_MAX_AMOUNT | DIM_OPERATIONS.OPERATION_MAX_AMOUNT | type DOUBLE, precision 22, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.OPERATIONS.src.DIM_OPERATIONS#map1] |
| OPERATION_METHOD_ID | DIM_OPERATION_METHODS.OPERATION_METHOD_ID | type DOUBLE, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.OPERATIONS.src.DIM_OPERATIONS#map2] |
| OPERATION_MIN_AMOUNT | DIM_OPERATIONS.OPERATION_MIN_AMOUNT | type DOUBLE, precision 22, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.OPERATIONS.src.DIM_OPERATIONS#map3] |
| OPERATION_NAME | DIM_OPERATIONS.OPERATION_NAME | type VARCHAR, precision 50, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.OPERATIONS.src.DIM_OPERATIONS#map4] |
| OPERATION_METHOD_NAME | DIM_OPERATION_METHODS.OPERATION_METHOD_NAME | type VARCHAR, precision 30, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.OPERATIONS.src.DIM_OPERATIONS#map5] |
| OPERATION_METHOD_TYPE | DIM_OPERATION_METHODS.OPERATION_METHOD_TYPE | type VARCHAR, precision 50, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.OPERATIONS.src.DIM_OPERATIONS#map6] |
| OPERATION_METHOD_TYPE_ID | DIM_OPERATION_METHODS.OPERATION_METHOD_TYPE_ID | type DOUBLE, precision 22, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.OPERATIONS.src.DIM_OPERATIONS#map7] |

For OPERATION_METHOD_ID the packet gives no precision; it prints only "type DOUBLE, nullable true" [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.OPERATIONS.src.DIM_OPERATIONS#map2].

### Points to note

- The field OPERATION_METHOD_ID is mapped from DIM_OPERATION_METHODS, the table on the far side of a `LeftOuter` join, not from the column OPERATION_METHOD_ID of DIM_OPERATIONS that the join uses [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.OPERATIONS.src.DIM_OPERATIONS#map2] [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.OPERATIONS.src.DIM_OPERATIONS#table0.join0].
- DIM_OPERATION_METHODS is a query kept in the repository, not a database table [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.OPERATIONS.src.DIM_OPERATIONS#table1].

## TARIFFS

TARIFFS is a business table ("logical table") of the business model "Iryna_Bradzetskaya_MT_Row-level_Security"; the repository declares 1 source for it [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TARIFFS]. In this subject area it has 9 fields and no measures, and all 9 are shown to report authors; its one source is "DIM_TARIFFS" (counted; no Row identifier).

### Keys

| Key | Primary key (as recorded) | Fields | Evidence |
|---|---|---|---|
| PK_TARIFFS | true | TARIFF_ID | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TARIFFS.key.PK_TARIFFS] |

### Measures

This table has no measures.

### Fields

Every field is shown to report authors under its own name in the table TARIFFS. None can be typed into. The definition kept on each field is one database column of the same name in DIM_TARIFFS.

| Field | Shown to report authors as | Kept definition | In sources | Evidence |
|---|---|---|---|---|
| INSERT_DT | INSERT_DT | DIM_TARIFFS.INSERT_DT | 1 of 1: "DIM_TARIFFS" | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TARIFFS.INSERT_DT] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.TARIFFS.INSERT_DT] |
| TARIFF_CODE | TARIFF_CODE | DIM_TARIFFS.TARIFF_CODE | 1 of 1: "DIM_TARIFFS" | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TARIFFS.TARIFF_CODE] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.TARIFFS.TARIFF_CODE] |
| TARIFF_ID | TARIFF_ID | DIM_TARIFFS.TARIFF_ID | 1 of 1: "DIM_TARIFFS" | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TARIFFS.TARIFF_ID] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.TARIFFS.TARIFF_ID] |
| TARIFF_MAX_PAYMENT | TARIFF_MAX_PAYMENT | DIM_TARIFFS.TARIFF_MAX_PAYMENT | 1 of 1: "DIM_TARIFFS" | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TARIFFS.TARIFF_MAX_PAYMENT] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.TARIFFS.TARIFF_MAX_PAYMENT] |
| TARIFF_MIN_PAYMENT | TARIFF_MIN_PAYMENT | DIM_TARIFFS.TARIFF_MIN_PAYMENT | 1 of 1: "DIM_TARIFFS" | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TARIFFS.TARIFF_MIN_PAYMENT] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.TARIFFS.TARIFF_MIN_PAYMENT] |
| TARIFF_NAME | TARIFF_NAME | DIM_TARIFFS.TARIFF_NAME | 1 of 1: "DIM_TARIFFS" | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TARIFFS.TARIFF_NAME] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.TARIFFS.TARIFF_NAME] |
| TARIFF_PAYMENT_SUM | TARIFF_PAYMENT_SUM | DIM_TARIFFS.TARIFF_PAYMENT_SUM | 1 of 1: "DIM_TARIFFS" | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TARIFFS.TARIFF_PAYMENT_SUM] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.TARIFFS.TARIFF_PAYMENT_SUM] |
| TARIFF_TYPE | TARIFF_TYPE | DIM_TARIFFS.TARIFF_TYPE | 1 of 1: "DIM_TARIFFS" | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TARIFFS.TARIFF_TYPE] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.TARIFFS.TARIFF_TYPE] |
| UPDATE_DT | UPDATE_DT | DIM_TARIFFS.UPDATE_DT | 1 of 1: "DIM_TARIFFS" | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TARIFFS.UPDATE_DT] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.TARIFFS.UPDATE_DT] |

TARIFF_MIN_PAYMENT, TARIFF_MAX_PAYMENT and TARIFF_PAYMENT_SUM are plain fields, not measures. What the three hold, and how they relate to the amounts of a transaction, is not recorded (open question Q8).

### Measures and fields report authors do not see

This table has no measures or fields that report authors do not see.

### Where the data comes from

TARIFFS has one source.

#### Source "DIM_TARIFFS"

The source is active; its priority group is not recorded (empty; it does not mean lowest) [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TARIFFS.src.DIM_TARIFFS]. Level of detail (grain): not recorded. The repository gives this source no level of detail [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TARIFFS.src.DIM_TARIFFS]. The packet says nothing about a parent-child hierarchy for this source.

No content filter is recorded for this source.

**Database tables it reads.**

| Database table | Schema | Type | Evidence |
|---|---|---|---|
| DIM_TARIFFS (see the section Database table DIM_TARIFFS) | `Iryna_Bradzetskaya_MT.U_SAL` | `none` | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TARIFFS.src.DIM_TARIFFS#table0] |

This source reads one table, so it makes no join.

**Mapping.** Every mapping of this source is one database column of DIM_TARIFFS.

| Field | Database column | Type, precision, nullable | Evidence |
|---|---|---|---|
| INSERT_DT | DIM_TARIFFS.INSERT_DT | type DATETIME, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TARIFFS.src.DIM_TARIFFS#map0] |
| TARIFF_CODE | DIM_TARIFFS.TARIFF_CODE | type VARCHAR, precision 5, nullable not stated | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TARIFFS.src.DIM_TARIFFS#map1] |
| TARIFF_ID | DIM_TARIFFS.TARIFF_ID | type DOUBLE, precision 22, nullable not stated | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TARIFFS.src.DIM_TARIFFS#map2] |
| TARIFF_MAX_PAYMENT | DIM_TARIFFS.TARIFF_MAX_PAYMENT | type DOUBLE, precision 22, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TARIFFS.src.DIM_TARIFFS#map3] |
| TARIFF_MIN_PAYMENT | DIM_TARIFFS.TARIFF_MIN_PAYMENT | type DOUBLE, precision 22, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TARIFFS.src.DIM_TARIFFS#map4] |
| TARIFF_NAME | DIM_TARIFFS.TARIFF_NAME | type VARCHAR, precision 30, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TARIFFS.src.DIM_TARIFFS#map5] |
| TARIFF_PAYMENT_SUM | DIM_TARIFFS.TARIFF_PAYMENT_SUM | type DOUBLE, precision 22, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TARIFFS.src.DIM_TARIFFS#map6] |
| TARIFF_TYPE | DIM_TARIFFS.TARIFF_TYPE | type CHAR, precision 30, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TARIFFS.src.DIM_TARIFFS#map7] |
| UPDATE_DT | DIM_TARIFFS.UPDATE_DT | type DATETIME, precision 30, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TARIFFS.src.DIM_TARIFFS#map8] |

For INSERT_DT the packet gives no precision; it prints only "type DATETIME, nullable true" [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TARIFFS.src.DIM_TARIFFS#map0].

### Points to note

- TARIFF_PAYMENT_SUM, TARIFF_MIN_PAYMENT and TARIFF_MAX_PAYMENT are plain fields and are not summarised [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TARIFFS.TARIFF_PAYMENT_SUM] (open question Q8).
- TARIFF_TYPE is mapped from a column of type CHAR with precision 30 [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TARIFFS.src.DIM_TARIFFS#map7].

## TIMES_DAILY

TIMES_DAILY is a business table ("logical table") of the business model "Iryna_Bradzetskaya_MT_Row-level_Security"; the repository declares 1 source for it [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TIMES_DAILY]. In this subject area it has 22 fields and no measures, and all 22 are shown to report authors; its one source is "DIM_TIMES_DAILY", described under "Where the data comes from" (counted; no Row identifier).

### Keys

| Key | Primary key (as recorded) | Fields | Evidence |
|---|---|---|---|
| DIM_TIMES_DAILY_PK | true | TIME_ID | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TIMES_DAILY.key.DIM_TIMES_DAILY_PK] |

### Measures

This table has no measures.

### Fields

Every field is shown to report authors under its own name in the table TIMES_DAILY. None can be typed into. The definition kept on each field is one database column of the same name in DIM_TIMES_DAILY.

| Field | Shown to report authors as | Kept definition | In sources | Evidence |
|---|---|---|---|---|
| BEG_OF_CAL_QUARTER | BEG_OF_CAL_QUARTER | DIM_TIMES_DAILY.BEG_OF_CAL_QUARTER | 1 of 1: "DIM_TIMES_DAILY" | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TIMES_DAILY.BEG_OF_CAL_QUARTER] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.TIMES_DAILY.BEG_OF_CAL_QUARTER] |
| BEG_OF_CAL_YEAR | BEG_OF_CAL_YEAR | DIM_TIMES_DAILY.BEG_OF_CAL_YEAR | 1 of 1: "DIM_TIMES_DAILY" | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TIMES_DAILY.BEG_OF_CAL_YEAR] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.TIMES_DAILY.BEG_OF_CAL_YEAR] |
| CALENDAR_MONTH_NAME | CALENDAR_MONTH_NAME | DIM_TIMES_DAILY.CALENDAR_MONTH_NAME | 1 of 1: "DIM_TIMES_DAILY" | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TIMES_DAILY.CALENDAR_MONTH_NAME] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.TIMES_DAILY.CALENDAR_MONTH_NAME] |
| CALENDAR_MONTH_NUMBER | CALENDAR_MONTH_NUMBER | DIM_TIMES_DAILY.CALENDAR_MONTH_NUMBER | 1 of 1: "DIM_TIMES_DAILY" | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TIMES_DAILY.CALENDAR_MONTH_NUMBER] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.TIMES_DAILY.CALENDAR_MONTH_NUMBER] |
| CALENDAR_QUARTER_NUMBER | CALENDAR_QUARTER_NUMBER | DIM_TIMES_DAILY.CALENDAR_QUARTER_NUMBER | 1 of 1: "DIM_TIMES_DAILY" | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TIMES_DAILY.CALENDAR_QUARTER_NUMBER] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.TIMES_DAILY.CALENDAR_QUARTER_NUMBER] |
| CALENDAR_WEEK_NUMBER | CALENDAR_WEEK_NUMBER | DIM_TIMES_DAILY.CALENDAR_WEEK_NUMBER | 1 of 1: "DIM_TIMES_DAILY" | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TIMES_DAILY.CALENDAR_WEEK_NUMBER] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.TIMES_DAILY.CALENDAR_WEEK_NUMBER] |
| CALENDAR_YEAR | CALENDAR_YEAR | DIM_TIMES_DAILY.CALENDAR_YEAR | 1 of 1: "DIM_TIMES_DAILY" | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TIMES_DAILY.CALENDAR_YEAR] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.TIMES_DAILY.CALENDAR_YEAR] |
| DAYS_IN_CAL_MONTH | DAYS_IN_CAL_MONTH | DIM_TIMES_DAILY.DAYS_IN_CAL_MONTH | 1 of 1: "DIM_TIMES_DAILY" | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TIMES_DAILY.DAYS_IN_CAL_MONTH] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.TIMES_DAILY.DAYS_IN_CAL_MONTH] |
| DAYS_IN_CAL_QUARTER | DAYS_IN_CAL_QUARTER | DIM_TIMES_DAILY.DAYS_IN_CAL_QUARTER | 1 of 1: "DIM_TIMES_DAILY" | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TIMES_DAILY.DAYS_IN_CAL_QUARTER] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.TIMES_DAILY.DAYS_IN_CAL_QUARTER] |
| DAYS_IN_CAL_YEAR | DAYS_IN_CAL_YEAR | DIM_TIMES_DAILY.DAYS_IN_CAL_YEAR | 1 of 1: "DIM_TIMES_DAILY" | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TIMES_DAILY.DAYS_IN_CAL_YEAR] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.TIMES_DAILY.DAYS_IN_CAL_YEAR] |
| DAY_NAME | DAY_NAME | DIM_TIMES_DAILY.DAY_NAME | 1 of 1: "DIM_TIMES_DAILY" | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TIMES_DAILY.DAY_NAME] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.TIMES_DAILY.DAY_NAME] |
| DAY_NUMBER_IN_MONTH | DAY_NUMBER_IN_MONTH | DIM_TIMES_DAILY.DAY_NUMBER_IN_MONTH | 1 of 1: "DIM_TIMES_DAILY" | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TIMES_DAILY.DAY_NUMBER_IN_MONTH] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.TIMES_DAILY.DAY_NUMBER_IN_MONTH] |
| DAY_NUMBER_IN_WEEK | DAY_NUMBER_IN_WEEK | DIM_TIMES_DAILY.DAY_NUMBER_IN_WEEK | 1 of 1: "DIM_TIMES_DAILY" | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TIMES_DAILY.DAY_NUMBER_IN_WEEK] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.TIMES_DAILY.DAY_NUMBER_IN_WEEK] |
| DAY_NUMBER_IN_YEAR | DAY_NUMBER_IN_YEAR | DIM_TIMES_DAILY.DAY_NUMBER_IN_YEAR | 1 of 1: "DIM_TIMES_DAILY" | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TIMES_DAILY.DAY_NUMBER_IN_YEAR] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.TIMES_DAILY.DAY_NUMBER_IN_YEAR] |
| END_OF_CAL_MONTH | END_OF_CAL_MONTH | DIM_TIMES_DAILY.END_OF_CAL_MONTH | 1 of 1: "DIM_TIMES_DAILY" | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TIMES_DAILY.END_OF_CAL_MONTH] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.TIMES_DAILY.END_OF_CAL_MONTH] |
| END_OF_CAL_QUARTER | END_OF_CAL_QUARTER | DIM_TIMES_DAILY.END_OF_CAL_QUARTER | 1 of 1: "DIM_TIMES_DAILY" | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TIMES_DAILY.END_OF_CAL_QUARTER] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.TIMES_DAILY.END_OF_CAL_QUARTER] |
| END_OF_CAL_YEAR | END_OF_CAL_YEAR | DIM_TIMES_DAILY.END_OF_CAL_YEAR | 1 of 1: "DIM_TIMES_DAILY" | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TIMES_DAILY.END_OF_CAL_YEAR] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.TIMES_DAILY.END_OF_CAL_YEAR] |

### Fields (continued)

| Field | Shown to report authors as | Kept definition | In sources | Evidence |
|---|---|---|---|---|
| TIME_ID | TIME_ID | DIM_TIMES_DAILY.TIME_ID | 1 of 1: "DIM_TIMES_DAILY" | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TIMES_DAILY.TIME_ID] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.TIMES_DAILY.TIME_ID] |
| WEEK_ENDING_DATE | WEEK_ENDING_DATE | DIM_TIMES_DAILY.WEEK_ENDING_DATE | 1 of 1: "DIM_TIMES_DAILY" | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TIMES_DAILY.WEEK_ENDING_DATE] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.TIMES_DAILY.WEEK_ENDING_DATE] |
| YEAR_M | YEAR_M | DIM_TIMES_DAILY.YEAR_M | 1 of 1: "DIM_TIMES_DAILY" | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TIMES_DAILY.YEAR_M] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.TIMES_DAILY.YEAR_M] |
| YEAR_Q | YEAR_Q | DIM_TIMES_DAILY.YEAR_Q | 1 of 1: "DIM_TIMES_DAILY" | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TIMES_DAILY.YEAR_Q] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.TIMES_DAILY.YEAR_Q] |
| YEAR_W | YEAR_W | DIM_TIMES_DAILY.YEAR_W | 1 of 1: "DIM_TIMES_DAILY" | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TIMES_DAILY.YEAR_W] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.TIMES_DAILY.YEAR_W] |

### Measures and fields report authors do not see

This table has no measures or fields that report authors do not see.

### Where the data comes from

TIMES_DAILY has one source.

#### Source "DIM_TIMES_DAILY"

The source is active; its priority group is not recorded (empty; it does not mean lowest) [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TIMES_DAILY.src.DIM_TIMES_DAILY]. Level of detail (grain): not recorded. The repository gives this source no level of detail [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TIMES_DAILY.src.DIM_TIMES_DAILY]. The packet says nothing about a parent-child hierarchy for this source.

No content filter is recorded for this source.

**Database tables it reads.** The one table is a query kept in the repository (type `select`), not a table of the database; its query is given in the section Database table DIM_TIMES_DAILY.

| Database table | Schema | Type | Evidence |
|---|---|---|---|
| DIM_TIMES_DAILY (see the section Database table DIM_TIMES_DAILY) | `Iryna_Bradzetskaya_MT.U_SAL` | `select` | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TIMES_DAILY.src.DIM_TIMES_DAILY#table0] |

This source reads one table, so it makes no join.

**Mapping.** Every mapping of this source is one database column of DIM_TIMES_DAILY. The rows are in the packet's order; where the packet prints no precision, none is given.

| Field | Database column | Type, precision, nullable | Evidence |
|---|---|---|---|
| BEG_OF_CAL_QUARTER | DIM_TIMES_DAILY.BEG_OF_CAL_QUARTER | type DATETIME, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TIMES_DAILY.src.DIM_TIMES_DAILY#map0] |
| BEG_OF_CAL_YEAR | DIM_TIMES_DAILY.BEG_OF_CAL_YEAR | type DATETIME, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TIMES_DAILY.src.DIM_TIMES_DAILY#map1] |
| DAY_NUMBER_IN_YEAR | DIM_TIMES_DAILY.DAY_NUMBER_IN_YEAR | type VARCHAR, precision 3, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TIMES_DAILY.src.DIM_TIMES_DAILY#map10] |
| DAYS_IN_CAL_MONTH | DIM_TIMES_DAILY.DAYS_IN_CAL_MONTH | type VARCHAR, precision 2, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TIMES_DAILY.src.DIM_TIMES_DAILY#map11] |
| DAYS_IN_CAL_QUARTER | DIM_TIMES_DAILY.DAYS_IN_CAL_QUARTER | type DOUBLE, precision 22, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TIMES_DAILY.src.DIM_TIMES_DAILY#map12] |
| DAYS_IN_CAL_YEAR | DIM_TIMES_DAILY.DAYS_IN_CAL_YEAR | type DOUBLE, precision 22, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TIMES_DAILY.src.DIM_TIMES_DAILY#map13] |
| END_OF_CAL_MONTH | DIM_TIMES_DAILY.END_OF_CAL_MONTH | type DATETIME, precision 3, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TIMES_DAILY.src.DIM_TIMES_DAILY#map14] |
| END_OF_CAL_QUARTER | DIM_TIMES_DAILY.END_OF_CAL_QUARTER | type DATETIME, precision 3, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TIMES_DAILY.src.DIM_TIMES_DAILY#map15] |
| END_OF_CAL_YEAR | DIM_TIMES_DAILY.END_OF_CAL_YEAR | type DATETIME, precision 3, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TIMES_DAILY.src.DIM_TIMES_DAILY#map16] |
| TIME_ID | DIM_TIMES_DAILY.TIME_ID | type DATETIME, precision 3, nullable not stated | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TIMES_DAILY.src.DIM_TIMES_DAILY#map17] |
| WEEK_ENDING_DATE | DIM_TIMES_DAILY.WEEK_ENDING_DATE | type DATETIME, precision 3, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TIMES_DAILY.src.DIM_TIMES_DAILY#map18] |
| YEAR_M | DIM_TIMES_DAILY.YEAR_M | type INT, nullable not stated | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TIMES_DAILY.src.DIM_TIMES_DAILY#map19] |
| CALENDAR_MONTH_NAME | DIM_TIMES_DAILY.CALENDAR_MONTH_NAME | type VARCHAR, precision 9, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TIMES_DAILY.src.DIM_TIMES_DAILY#map2] |
| YEAR_Q | DIM_TIMES_DAILY.YEAR_Q | type INT, nullable not stated | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TIMES_DAILY.src.DIM_TIMES_DAILY#map20] |
| YEAR_W | DIM_TIMES_DAILY.YEAR_W | type INT, nullable not stated | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TIMES_DAILY.src.DIM_TIMES_DAILY#map21] |
| CALENDAR_MONTH_NUMBER | DIM_TIMES_DAILY.CALENDAR_MONTH_NUMBER | type VARCHAR, precision 2, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TIMES_DAILY.src.DIM_TIMES_DAILY#map3] |
| CALENDAR_QUARTER_NUMBER | DIM_TIMES_DAILY.CALENDAR_QUARTER_NUMBER | type VARCHAR, precision 1, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TIMES_DAILY.src.DIM_TIMES_DAILY#map4] |
| CALENDAR_WEEK_NUMBER | DIM_TIMES_DAILY.CALENDAR_WEEK_NUMBER | type VARCHAR, precision 1, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TIMES_DAILY.src.DIM_TIMES_DAILY#map5] |
| CALENDAR_YEAR | DIM_TIMES_DAILY.CALENDAR_YEAR | type VARCHAR, precision 4, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TIMES_DAILY.src.DIM_TIMES_DAILY#map6] |
| DAY_NAME | DIM_TIMES_DAILY.DAY_NAME | type VARCHAR, precision 9, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TIMES_DAILY.src.DIM_TIMES_DAILY#map7] |
| DAY_NUMBER_IN_MONTH | DIM_TIMES_DAILY.DAY_NUMBER_IN_MONTH | type VARCHAR, precision 2, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TIMES_DAILY.src.DIM_TIMES_DAILY#map8] |
| DAY_NUMBER_IN_WEEK | DIM_TIMES_DAILY.DAY_NUMBER_IN_WEEK | type VARCHAR, precision 1, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TIMES_DAILY.src.DIM_TIMES_DAILY#map9] |

### Points to note

- The key field TIME_ID is mapped from a column of type DATETIME, not a number [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TIMES_DAILY.src.DIM_TIMES_DAILY#map17].
- Calendar numbers such as CALENDAR_YEAR, CALENDAR_MONTH_NUMBER and DAY_NUMBER_IN_MONTH are mapped from VARCHAR columns, while YEAR_M, YEAR_Q and YEAR_W are INT [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TIMES_DAILY.src.DIM_TIMES_DAILY#map6] [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TIMES_DAILY.src.DIM_TIMES_DAILY#map19]. CALENDAR_WEEK_NUMBER is recorded as VARCHAR with precision 1 [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TIMES_DAILY.src.DIM_TIMES_DAILY#map5].
- The source reads a query kept in the repository, not a database table [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TIMES_DAILY.src.DIM_TIMES_DAILY#table0].

## Database table DIM_CURRENCY_SCD

DIM_CURRENCY_SCD is a database table in `Iryna_Bradzetskaya_MT.U_SAL`, type `none`; it is not an alias of another table, and no description is recorded [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_CURRENCY_SCD]. No separate object name in the database is recorded, so the repository knows it only by the name above (listed in the packet without a Row identifier). The export lists 9 columns for it, all 9 mapped by a field of this subject area and none not mapped, and 1 key (counted).

### Columns

| # | Column | Type | Precision | Scale | Nullable | External name | Mapped by | Evidence |
|---|---|---|---|---|---|---|---|---|
| 1 | CURRENCY_CODE | DOUBLE | 22 | not recorded | true | not recorded | CURRENCY, CURRENCY_CODE | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_CURRENCY_SCD.CURRENCY_CODE] |
| 2 | CURRENCY_DESC | VARCHAR | 30 | not recorded | true | not recorded | CURRENCY, CURRENCY_DESC | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_CURRENCY_SCD.CURRENCY_DESC] |
| 3 | CURRENCY_NAME | VARCHAR | 10 | not recorded | true | not recorded | CURRENCY, CURRENCY_NAME | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_CURRENCY_SCD.CURRENCY_NAME] |
| 4 | CURRENCY_SUR_ID | DOUBLE | 22 | not recorded | not stated | not recorded | CURRENCY, CURRENCY_SUR_ID | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_CURRENCY_SCD.CURRENCY_SUR_ID] |
| 5 | CURRENCY_TO_DOLLAR | DOUBLE | 22 | not recorded | true | not recorded | CURRENCY, CURRENCY_TO_DOLLAR | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_CURRENCY_SCD.CURRENCY_TO_DOLLAR] |
| 6 | CURRENCY_TYPE_DESC | VARCHAR | 50 | not recorded | true | not recorded | CURRENCY, CURRENCY_TYPE_DESC | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_CURRENCY_SCD.CURRENCY_TYPE_DESC] |
| 7 | CURRENCY_TYPE_ID | DOUBLE | 22 | not recorded | true | not recorded | CURRENCY, CURRENCY_TYPE_ID | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_CURRENCY_SCD.CURRENCY_TYPE_ID] |
| 8 | VALID_FROM | DATETIME | 22 | not recorded | true | not recorded | CURRENCY, VALID_FROM | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_CURRENCY_SCD.VALID_FROM] |
| 9 | VALID_TO | DATETIME | 22 | not recorded | true | not recorded | CURRENCY, VALID_TO | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_CURRENCY_SCD.VALID_TO] |

Nullable: the export records this only when it is true. "Not stated" means the export does not carry it; it is not a statement that the column must be filled.

### Keys

| Key | Columns | Evidence |
|---|---|---|
| PK_DIM_CURRENCY_SCD | CURRENCY_SUR_ID | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_CURRENCY_SCD.key.PK_DIM_CURRENCY_SCD] |

This is key 1 of 1 the repository declares for the table. The repository does not say whether a physical key is the primary key; this is the only key it declares. Two foreign keys of this subject area point at exactly this column [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_CURRENCY_SCD.key.PK_DIM_CURRENCY_SCD].

### Foreign keys and joins

- Foreign key FK_TRANS_CURRENCY from FCT_TRANSACTIONS_ACTUAL (CURRENCY_SUR_ID) to this table (CURRENCY_SUR_ID); stated in full in the section How the tables relate.
- Foreign key FK_TRANS_CURRENCY from FCT_TRANSACTIONS_DAILY (CURRENCY_SUR_ID) to this table (CURRENCY_SUR_ID); stated in full in the section How the tables relate.

### Read by

The source "DIM_CURRENCY_SCD" of the business table CURRENCY (listed in the packet without a Row identifier).

## Database table DIM_CUSTOMERS_REC

DIM_CUSTOMERS_REC is listed in `Iryna_Bradzetskaya_MT.U_SAL` with type `select`: it is not a table of the database but a query kept in the repository. It is not an alias of another table, and no description is recorded [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_CUSTOMERS_REC]. Its query, for the database type `DefaultMulDB`, is `SELECT * FROM DIM_CUSTOMERS` [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_CUSTOMERS_REC.def.DefaultMulDB]: it reads every column of a database table named DIM_CUSTOMERS. The export lists 12 columns for it, all 12 mapped by a field of this subject area and none not mapped, and 1 key (counted).

### Columns

| # | Column | Type | Precision | Scale | Nullable | External name | Mapped by | Evidence |
|---|---|---|---|---|---|---|---|---|
| 1 | CUST_BALANCE | DOUBLE | 22 | not recorded | true | not recorded | CUSTOMERS_REC, CUST_BALANCE | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_CUSTOMERS_REC.CUST_BALANCE] |
| 2 | CUST_EMAIL | VARCHAR | 50 | not recorded | true | not recorded | CUSTOMERS_REC, CUST_EMAIL | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_CUSTOMERS_REC.CUST_EMAIL] |
| 3 | CUST_FIRST_NAME | VARCHAR | 30 | not recorded | true | not recorded | CUSTOMERS_REC, CUST_FIRST_NAME | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_CUSTOMERS_REC.CUST_FIRST_NAME] |
| 4 | CUST_GENDER | VARCHAR | 1 | not recorded | true | not recorded | CUSTOMERS_REC, CUST_GENDER | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_CUSTOMERS_REC.CUST_GENDER] |
| 5 | CUST_GEO_ID | DOUBLE | 22 | not recorded | true | not recorded | CUSTOMERS_REC, CUST_GEO_ID | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_CUSTOMERS_REC.CUST_GEO_ID] |
| 6 | CUST_ID | DOUBLE | 22 | not recorded | not stated | not recorded | CUSTOMERS_REC, CUST_ID | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_CUSTOMERS_REC.CUST_ID] |
| 7 | CUST_LAST_NAME | VARCHAR | 30 | not recorded | true | not recorded | CUSTOMERS_REC, CUST_LAST_NAME | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_CUSTOMERS_REC.CUST_LAST_NAME] |
| 8 | CUST_LEVEL_INCOME | DOUBLE | 22 | not recorded | true | not recorded | CUSTOMERS_REC, CUST_LEVEL_INCOME | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_CUSTOMERS_REC.CUST_LEVEL_INCOME] |
| 9 | CUST_PASS_NUMBER | VARCHAR | 30 | not recorded | true | not recorded | CUSTOMERS_REC, CUST_PASS_NUMBER | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_CUSTOMERS_REC.CUST_PASS_NUMBER] |
| 10 | CUST_YEAR_OF_BIRTH | DATETIME | 30 | not recorded | true | not recorded | CUSTOMERS_REC, CUST_YEAR_OF_BIRTH | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_CUSTOMERS_REC.CUST_YEAR_OF_BIRTH] |
| 11 | INSERT_DT | DATETIME | 30 | not recorded | true | not recorded | CUSTOMERS_REC, INSERT_DT | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_CUSTOMERS_REC.INSERT_DT] |
| 12 | UPDATE_DT | DATETIME | 30 | not recorded | true | not recorded | CUSTOMERS_REC, UPDATE_DT | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_CUSTOMERS_REC.UPDATE_DT] |

Nullable: the export records this only when it is true. "Not stated" means the export does not carry it; it is not a statement that the column must be filled.

### Keys

| Key | Columns | Evidence |
|---|---|---|
| PK_DIM_CUSTOMERS | CUST_ID | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_CUSTOMERS_REC.key.PK_DIM_CUSTOMERS] |

This is key 1 of 1 the repository declares for the table. The repository does not say whether a physical key is the primary key; this is the only key it declares. Two foreign keys of this subject area point at exactly this column [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_CUSTOMERS_REC.key.PK_DIM_CUSTOMERS].

### Foreign keys and joins

- Foreign key FK_TR_CUST_REC from FCT_TRANSACTIONS_ACTUAL (CUS_REC_ID) to this table (CUST_ID); stated in full in the section How the tables relate.
- Foreign key FK_TR_CUST_REC from FCT_TRANSACTIONS_DAILY (CUS_REC_ID) to this table (CUST_ID); stated in full in the section How the tables relate.

### Read by

The source "DIM_CUSTOMERS_REC" of the business table CUSTOMERS_REC (listed in the packet without a Row identifier).

## Database table DIM_CUSTOMERS_SEND

DIM_CUSTOMERS_SEND is listed in `Iryna_Bradzetskaya_MT.U_SAL` with type `select`: it is not a table of the database but a query kept in the repository. It is not an alias of another table, and no description is recorded [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_CUSTOMERS_SEND]. Its query, for the database type `DefaultMulDB`, is `SELECT * FROM DIM_CUSTOMERS` [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_CUSTOMERS_SEND.def.DefaultMulDB]: it reads every column of a database table named DIM_CUSTOMERS. The export lists 12 columns for it, all 12 mapped by a field of this subject area and none not mapped, and 1 key (counted).

### Columns

| # | Column | Type | Precision | Scale | Nullable | External name | Mapped by | Evidence |
|---|---|---|---|---|---|---|---|---|
| 1 | CUST_BALANCE | DOUBLE | 22 | not recorded | true | not recorded | CUSTOMERS_SEND, CUST_BALANCE | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_CUSTOMERS_SEND.CUST_BALANCE] |
| 2 | CUST_EMAIL | VARCHAR | 50 | not recorded | true | not recorded | CUSTOMERS_SEND, CUST_EMAIL | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_CUSTOMERS_SEND.CUST_EMAIL] |
| 3 | CUST_FIRST_NAME | VARCHAR | 30 | not recorded | true | not recorded | CUSTOMERS_SEND, CUST_FIRST_NAME | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_CUSTOMERS_SEND.CUST_FIRST_NAME] |
| 4 | CUST_GENDER | VARCHAR | 1 | not recorded | true | not recorded | CUSTOMERS_SEND, CUST_GENDER | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_CUSTOMERS_SEND.CUST_GENDER] |
| 5 | CUST_GEO_ID | DOUBLE | 22 | not recorded | true | not recorded | CUSTOMERS_SEND, CUST_GEO_ID | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_CUSTOMERS_SEND.CUST_GEO_ID] |
| 6 | CUST_ID | DOUBLE | 22 | not recorded | not stated | not recorded | CUSTOMERS_SEND, CUST_ID | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_CUSTOMERS_SEND.CUST_ID] |
| 7 | CUST_LAST_NAME | VARCHAR | 30 | not recorded | true | not recorded | CUSTOMERS_SEND, CUST_LAST_NAME | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_CUSTOMERS_SEND.CUST_LAST_NAME] |
| 8 | CUST_LEVEL_INCOME | DOUBLE | 22 | not recorded | true | not recorded | CUSTOMERS_SEND, CUST_LEVEL_INCOME | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_CUSTOMERS_SEND.CUST_LEVEL_INCOME] |
| 9 | CUST_PASS_NUMBER | VARCHAR | 30 | not recorded | true | not recorded | CUSTOMERS_SEND, CUST_PASS_NUMBER | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_CUSTOMERS_SEND.CUST_PASS_NUMBER] |
| 10 | CUST_YEAR_OF_BIRTH | DATETIME | 30 | not recorded | true | not recorded | CUSTOMERS_SEND, CUST_YEAR_OF_BIRTH | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_CUSTOMERS_SEND.CUST_YEAR_OF_BIRTH] |
| 11 | INSERT_DT | DATETIME | 30 | not recorded | true | not recorded | CUSTOMERS_SEND, INSERT_DT | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_CUSTOMERS_SEND.INSERT_DT] |
| 12 | UPDATE_DT | DATETIME | 30 | not recorded | true | not recorded | CUSTOMERS_SEND, UPDATE_DT | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_CUSTOMERS_SEND.UPDATE_DT] |

Nullable: the export records this only when it is true. "Not stated" means the export does not carry it; it is not a statement that the column must be filled.

### Keys

| Key | Columns | Evidence |
|---|---|---|
| PK_DIM_CUSTOMERS | CUST_ID | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_CUSTOMERS_SEND.key.PK_DIM_CUSTOMERS] |

This is key 1 of 1 the repository declares for the table. The repository does not say whether a physical key is the primary key; this is the only key it declares. Two foreign keys of this subject area point at exactly this column [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_CUSTOMERS_SEND.key.PK_DIM_CUSTOMERS].

### Foreign keys and joins

- Foreign key FK_TR_CUST_SEND from FCT_TRANSACTIONS_ACTUAL (CUST_SEND_ID) to this table (CUST_ID); stated in full in the section How the tables relate.
- Foreign key FK_TR_CUST_SEND from FCT_TRANSACTIONS_DAILY (CUST_SEND_ID) to this table (CUST_ID); stated in full in the section How the tables relate.

### Read by

The source "DIM_CUSTOMERS_SEND" of the business table CUSTOMERS_SEND (listed in the packet without a Row identifier).

## Database table DIM_LOCATIONS_REC

DIM_LOCATIONS_REC is listed in `Iryna_Bradzetskaya_MT.U_SAL` with type `select`: it is not a table of the database but a query kept in the repository. It is not an alias of another table, and no description is recorded [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_LOCATIONS_REC]. Its query, for the database type `DefaultMulDB`, is `SELECT * FROM DIM_LOCATIONS_SCD` [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_LOCATIONS_REC.def.DefaultMulDB]: it reads every column of a database table named DIM_LOCATIONS_SCD. The export lists 16 columns for it, all 16 mapped by a field of this subject area and none not mapped, and 1 key (counted).

### Columns

| # | Column | Type | Precision | Scale | Nullable | External name | Mapped by | Evidence |
|---|---|---|---|---|---|---|---|---|
| 1 | COUNTRY_CODE_A2 | VARCHAR | 5 | not recorded | true | not recorded | LOCATIONS_REC, COUNTRY_CODE_A2 | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_LOCATIONS_REC.COUNTRY_CODE_A2] |
| 2 | COUNTRY_CODE_A3 | VARCHAR | 5 | not recorded | true | not recorded | LOCATIONS_REC, COUNTRY_CODE_A3 | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_LOCATIONS_REC.COUNTRY_CODE_A3] |
| 3 | COUNTRY_ID | DOUBLE | 22 | not recorded | true | not recorded | LOCATIONS_REC, COUNTRY_ID | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_LOCATIONS_REC.COUNTRY_ID] |
| 4 | GEO_COUNTRY_ID | DOUBLE | 22 | not recorded | true | not recorded | LOCATIONS_REC, GEO_COUNTRY_ID | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_LOCATIONS_REC.GEO_COUNTRY_ID] |
| 5 | GEO_COUNTRY_NAME | VARCHAR | 80 | not recorded | true | not recorded | LOCATIONS_REC, GEO_COUNTRY_NAME | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_LOCATIONS_REC.GEO_COUNTRY_NAME] |
| 6 | GEO_ID | DOUBLE | 22 | not recorded | not stated | not recorded | LOCATIONS_REC, GEO_ID | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_LOCATIONS_REC.GEO_ID] |
| 7 | GEO_PART_CODE | VARCHAR | 30 | not recorded | true | not recorded | LOCATIONS_REC, GEO_PART_CODE | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_LOCATIONS_REC.GEO_PART_CODE] |
| 8 | GEO_PART_DESC | VARCHAR | 50 | not recorded | true | not recorded | LOCATIONS_REC, GEO_PART_DESC | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_LOCATIONS_REC.GEO_PART_DESC] |
| 9 | GEO_PART_ID | DOUBLE | 22 | not recorded | true | not recorded | LOCATIONS_REC, GEO_PART_ID | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_LOCATIONS_REC.GEO_PART_ID] |
| 10 | GEO_REGION_CODE | VARCHAR | 3 | not recorded | true | not recorded | LOCATIONS_REC, GEO_REGION_CODE | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_LOCATIONS_REC.GEO_REGION_CODE] |
| 11 | GEO_REGION_DESC | VARCHAR | 50 | not recorded | true | not recorded | LOCATIONS_REC, GEO_REGION_DESC | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_LOCATIONS_REC.GEO_REGION_DESC] |
| 12 | GEO_REGION_ID | DOUBLE | 22 | not recorded | true | not recorded | LOCATIONS_REC, GEO_REGION_ID | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_LOCATIONS_REC.GEO_REGION_ID] |
| 13 | GEO_SURR_ID | DOUBLE | 22 | not recorded | not stated | not recorded | LOCATIONS_REC, GEO_SURR_ID | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_LOCATIONS_REC.GEO_SURR_ID] |
| 14 | LEVEL_CODE | VARCHAR | 30 | not recorded | true | not recorded | LOCATIONS_REC, LEVEL_CODE | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_LOCATIONS_REC.LEVEL_CODE] |
| 15 | VALID_FROM | DATETIME | 30 | not recorded | true | not recorded | LOCATIONS_REC, VALID_FROM | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_LOCATIONS_REC.VALID_FROM] |
| 16 | VALID_TO | DATETIME | 30 | not recorded | true | not recorded | LOCATIONS_REC, VALID_TO | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_LOCATIONS_REC.VALID_TO] |

Nullable: the export records this only when it is true. "Not stated" means the export does not carry it; it is not a statement that the column must be filled.

### Keys

| Key | Columns | Evidence |
|---|---|---|
| PC_LOC | GEO_ID | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_LOCATIONS_REC.key.PC_LOC] |

This is key 1 of 1 the repository declares for the table. The repository does not say whether a physical key is the primary key; this is the only key it declares. Two foreign keys of this subject area point at exactly this column [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_LOCATIONS_REC.key.PC_LOC]. The column GEO_SURR_ID is not part of any recorded key (open question Q7).

### Foreign keys and joins

- Foreign key FK_TRANS_LOC_REC from FCT_TRANSACTIONS_ACTUAL (GEO_REC_SUR_ID) to this table (GEO_ID); stated in full in the section How the tables relate.
- Foreign key FK_TRANS_LOC_REC from FCT_TRANSACTIONS_DAILY (GEO_REC_SUR_ID) to this table (GEO_ID); stated in full in the section How the tables relate.

### Read by

The source "DIM_LOCATIONS_REC" of the business table LOCATIONS_REC (listed in the packet without a Row identifier).

## Database table DIM_LOCATIONS_SEND

DIM_LOCATIONS_SEND is listed in `Iryna_Bradzetskaya_MT.U_SAL` with type `select`: it is not a table of the database but a query kept in the repository. It is not an alias of another table, and no description is recorded [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_LOCATIONS_SEND]. Its query, for the database type `DefaultMulDB`, is `SELECT * FROM DIM_LOCATIONS_SCD` [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_LOCATIONS_SEND.def.DefaultMulDB]: it reads every column of a database table named DIM_LOCATIONS_SCD. The export lists 16 columns for it, all 16 mapped by a field of this subject area and none not mapped, and 1 key (counted).

### Columns

| # | Column | Type | Precision | Scale | Nullable | External name | Mapped by | Evidence |
|---|---|---|---|---|---|---|---|---|
| 1 | COUNTRY_CODE_A2 | VARCHAR | 5 | not recorded | true | not recorded | LOCATIONS_SEND, COUNTRY_CODE_A2 | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_LOCATIONS_SEND.COUNTRY_CODE_A2] |
| 2 | COUNTRY_CODE_A3 | VARCHAR | 5 | not recorded | true | not recorded | LOCATIONS_SEND, COUNTRY_CODE_A3 | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_LOCATIONS_SEND.COUNTRY_CODE_A3] |
| 3 | COUNTRY_ID | DOUBLE | 22 | not recorded | true | not recorded | LOCATIONS_SEND, COUNTRY_ID | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_LOCATIONS_SEND.COUNTRY_ID] |
| 4 | GEO_COUNTRY_ID | DOUBLE | 22 | not recorded | true | not recorded | LOCATIONS_SEND, GEO_COUNTRY_ID | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_LOCATIONS_SEND.GEO_COUNTRY_ID] |
| 5 | GEO_COUNTRY_NAME | VARCHAR | 80 | not recorded | true | not recorded | LOCATIONS_SEND, GEO_COUNTRY_NAME | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_LOCATIONS_SEND.GEO_COUNTRY_NAME] |
| 6 | GEO_ID | DOUBLE | 22 | not recorded | not stated | not recorded | LOCATIONS_SEND, GEO_ID | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_LOCATIONS_SEND.GEO_ID] |
| 7 | GEO_PART_CODE | VARCHAR | 30 | not recorded | true | not recorded | LOCATIONS_SEND, GEO_PART_CODE | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_LOCATIONS_SEND.GEO_PART_CODE] |
| 8 | GEO_PART_DESC | VARCHAR | 50 | not recorded | true | not recorded | LOCATIONS_SEND, GEO_PART_DESC | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_LOCATIONS_SEND.GEO_PART_DESC] |
| 9 | GEO_PART_ID | DOUBLE | 22 | not recorded | true | not recorded | LOCATIONS_SEND, GEO_PART_ID | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_LOCATIONS_SEND.GEO_PART_ID] |
| 10 | GEO_REGION_CODE | VARCHAR | 3 | not recorded | true | not recorded | LOCATIONS_SEND, GEO_REGION_CODE | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_LOCATIONS_SEND.GEO_REGION_CODE] |
| 11 | GEO_REGION_DESC | VARCHAR | 50 | not recorded | true | not recorded | LOCATIONS_SEND, GEO_REGION_DESC | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_LOCATIONS_SEND.GEO_REGION_DESC] |
| 12 | GEO_REGION_ID | DOUBLE | 22 | not recorded | true | not recorded | LOCATIONS_SEND, GEO_REGION_ID | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_LOCATIONS_SEND.GEO_REGION_ID] |
| 13 | GEO_SURR_ID | DOUBLE | 22 | not recorded | not stated | not recorded | LOCATIONS_SEND, GEO_SURR_ID | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_LOCATIONS_SEND.GEO_SURR_ID] |
| 14 | LEVEL_CODE | VARCHAR | 30 | not recorded | true | not recorded | LOCATIONS_SEND, LEVEL_CODE | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_LOCATIONS_SEND.LEVEL_CODE] |
| 15 | VALID_FROM | DATETIME | 30 | not recorded | true | not recorded | LOCATIONS_SEND, VALID_FROM | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_LOCATIONS_SEND.VALID_FROM] |
| 16 | VALID_TO | DATETIME | 30 | not recorded | true | not recorded | LOCATIONS_SEND, VALID_TO | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_LOCATIONS_SEND.VALID_TO] |

Nullable: the export records this only when it is true. "Not stated" means the export does not carry it; it is not a statement that the column must be filled.

### Keys

| Key | Columns | Evidence |
|---|---|---|
| PC_LOC | GEO_ID | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_LOCATIONS_SEND.key.PC_LOC] |

This is key 1 of 1 the repository declares for the table. The repository does not say whether a physical key is the primary key; this is the only key it declares. Two foreign keys of this subject area point at exactly this column [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_LOCATIONS_SEND.key.PC_LOC]. The column GEO_SURR_ID is not part of any recorded key (open question Q7).

### Foreign keys and joins

- Foreign key FK_TRANS_LOC_SEND from FCT_TRANSACTIONS_ACTUAL (GEO_SEND_SUR_ID) to this table (GEO_ID); stated in full in the section How the tables relate.
- Foreign key FK_TRANS_LOC_SEND from FCT_TRANSACTIONS_DAILY (GEO_SEND_SUR_ID) to this table (GEO_ID); stated in full in the section How the tables relate.

### Read by

The source "DIM_LOCATIONS_SEND" of the business table LOCATIONS_SEND (listed in the packet without a Row identifier).

## Database table DIM_OPERATIONS

DIM_OPERATIONS is a database table in `Iryna_Bradzetskaya_MT.U_SAL`, type `none`; it is not an alias of another table, and no description is recorded [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_OPERATIONS]. Its object in the database, for the database type `DefaultMulDB`, is `DIM_OPERATIONS` [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_OPERATIONS.def.DefaultMulDB]. The export lists 5 columns for it, 4 mapped by a field of this subject area and 1 not, and 1 key (counted).

### Columns

| # | Column | Type | Precision | Scale | Nullable | External name | Mapped by | Evidence |
|---|---|---|---|---|---|---|---|---|
| 1 | OPERATION_ID | DOUBLE | 22 | not recorded | not stated | not recorded | OPERATIONS, OPERATION_ID | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_OPERATIONS.OPERATION_ID] |
| 2 | OPERATION_MAX_AMOUNT | DOUBLE | 22 | not recorded | true | not recorded | OPERATIONS, OPERATION_MAX_AMOUNT | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_OPERATIONS.OPERATION_MAX_AMOUNT] |
| 3 | OPERATION_METHOD_ID | DOUBLE | 22 | not recorded | true | not recorded | not mapped | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_OPERATIONS.OPERATION_METHOD_ID] |
| 4 | OPERATION_MIN_AMOUNT | DOUBLE | 22 | not recorded | true | not recorded | OPERATIONS, OPERATION_MIN_AMOUNT | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_OPERATIONS.OPERATION_MIN_AMOUNT] |
| 5 | OPERATION_NAME | VARCHAR | 50 | not recorded | true | not recorded | OPERATIONS, OPERATION_NAME | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_OPERATIONS.OPERATION_NAME] |

Nullable: the export records this only when it is true. "Not stated" means the export does not carry it; it is not a statement that the column must be filled.

The column OPERATION_METHOD_ID is not mapped by any field; it is the column the table's foreign key to DIM_OPERATION_METHODS uses (see below) [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_OPERATIONS.OPERATION_METHOD_ID].

### Keys

| Key | Columns | Evidence |
|---|---|---|
| PK_DIM_OPERATIONS | OPERATION_ID | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_OPERATIONS.key.PK_DIM_OPERATIONS] |

This is key 1 of 1 the repository declares for the table. The repository does not say whether a physical key is the primary key; this is the only key it declares. Two foreign keys of this subject area point at exactly this column [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_OPERATIONS.key.PK_DIM_OPERATIONS].

### Foreign keys and joins

- Foreign key "DIM_OPERATIONS_Foreign Key" from this table (OPERATION_METHOD_ID) to DIM_OPERATION_METHODS (OPERATION_METHOD_ID); stated in full in the section How the tables relate.
- Foreign key FK_TRANS_OPERATIONS from FCT_TRANSACTIONS_ACTUAL (OPERATION_ID) to this table (OPERATION_ID); stated in full in the section How the tables relate.
- Foreign key FK_TRANS_OPERATIONS from FCT_TRANSACTIONS_DAILY (OPERATION_ID) to this table (OPERATION_ID); stated in full in the section How the tables relate.

### Read by

The source "DIM_OPERATIONS" of the business table OPERATIONS (listed in the packet without a Row identifier).

## Database table DIM_OPERATION_METHODS

DIM_OPERATION_METHODS is listed in `Iryna_Bradzetskaya_MT.U_SAL` with type `select`: it is not a table of the database but a query kept in the repository. It is not an alias of another table, and no description is recorded [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_OPERATION_METHODS]. Its query, for the database type `DefaultMulDB`, is quoted here exactly as extracted [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_OPERATION_METHODS.def.DefaultMulDB]:

```
SELECT DISTINCT OPERATION_METHOD_ID, OPERATION_METHOD_NAME, OPERATION_METHOD_TYPE, OPERATION_METHOD_TYPE_ID
FROM DIM_OPERATIONS
WHERE OPERATION_METHOD_ID IS NOT NULL
```

Put mechanically: the query selects the distinct combinations of the four columns OPERATION_METHOD_ID, OPERATION_METHOD_NAME, OPERATION_METHOD_TYPE and OPERATION_METHOD_TYPE_ID from the database table DIM_OPERATIONS, keeping the rows where OPERATION_METHOD_ID is not null. The export lists 4 columns for it, all 4 mapped by a field of this subject area and none not mapped, and 1 key (counted).

### Columns

| # | Column | Type | Precision | Scale | Nullable | External name | Mapped by | Evidence |
|---|---|---|---|---|---|---|---|---|
| 1 | OPERATION_METHOD_ID | DOUBLE | not recorded | not recorded | true | not recorded | OPERATIONS, OPERATION_METHOD_ID | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_OPERATION_METHODS.OPERATION_METHOD_ID] |
| 2 | OPERATION_METHOD_NAME | VARCHAR | 30 | not recorded | true | not recorded | OPERATIONS, OPERATION_METHOD_NAME | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_OPERATION_METHODS.OPERATION_METHOD_NAME] |
| 3 | OPERATION_METHOD_TYPE | VARCHAR | 50 | not recorded | true | not recorded | OPERATIONS, OPERATION_METHOD_TYPE | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_OPERATION_METHODS.OPERATION_METHOD_TYPE] |
| 4 | OPERATION_METHOD_TYPE_ID | DOUBLE | 22 | not recorded | true | not recorded | OPERATIONS, OPERATION_METHOD_TYPE_ID | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_OPERATION_METHODS.OPERATION_METHOD_TYPE_ID] |

Nullable: the export records this only when it is true. "Not stated" means the export does not carry it; it is not a statement that the column must be filled.

### Keys

| Key | Columns | Evidence |
|---|---|---|
| PK_OP_M | OPERATION_METHOD_ID | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_OPERATION_METHODS.key.PK_OP_M] |

This is key 1 of 1 the repository declares for the table. The repository does not say whether a physical key is the primary key; this is the only key it declares. One foreign key of this subject area points at exactly this column [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_OPERATION_METHODS.key.PK_OP_M]. Whether OPERATION_METHOD_ID is unique in the query's result is not recorded (open question Q9).

### Foreign keys and joins

- Foreign key "DIM_OPERATIONS_Foreign Key" from DIM_OPERATIONS (OPERATION_METHOD_ID) to this table (OPERATION_METHOD_ID); stated in full in the section How the tables relate.

### Read by

The source "DIM_OPERATIONS" of the business table OPERATIONS (listed in the packet without a Row identifier).

## Database table DIM_TARIFFS

DIM_TARIFFS is a database table in `Iryna_Bradzetskaya_MT.U_SAL`, type `none`; it is not an alias of another table, and no description is recorded [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_TARIFFS]. No separate object name in the database is recorded, so the repository knows it only by the name above (listed in the packet without a Row identifier). The export lists 9 columns for it, all 9 mapped by a field of this subject area and none not mapped, and 1 key (counted).

### Columns

| # | Column | Type | Precision | Scale | Nullable | External name | Mapped by | Evidence |
|---|---|---|---|---|---|---|---|---|
| 1 | INSERT_DT | DATETIME | not recorded | not recorded | true | not recorded | TARIFFS, INSERT_DT | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_TARIFFS.INSERT_DT] |
| 2 | TARIFF_CODE | VARCHAR | 5 | not recorded | not stated | not recorded | TARIFFS, TARIFF_CODE | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_TARIFFS.TARIFF_CODE] |
| 3 | TARIFF_ID | DOUBLE | 22 | not recorded | not stated | not recorded | TARIFFS, TARIFF_ID | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_TARIFFS.TARIFF_ID] |
| 4 | TARIFF_MAX_PAYMENT | DOUBLE | 22 | not recorded | true | not recorded | TARIFFS, TARIFF_MAX_PAYMENT | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_TARIFFS.TARIFF_MAX_PAYMENT] |
| 5 | TARIFF_MIN_PAYMENT | DOUBLE | 22 | not recorded | true | not recorded | TARIFFS, TARIFF_MIN_PAYMENT | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_TARIFFS.TARIFF_MIN_PAYMENT] |
| 6 | TARIFF_NAME | VARCHAR | 30 | not recorded | true | not recorded | TARIFFS, TARIFF_NAME | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_TARIFFS.TARIFF_NAME] |
| 7 | TARIFF_PAYMENT_SUM | DOUBLE | 22 | not recorded | true | not recorded | TARIFFS, TARIFF_PAYMENT_SUM | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_TARIFFS.TARIFF_PAYMENT_SUM] |
| 8 | TARIFF_TYPE | CHAR | 30 | not recorded | true | not recorded | TARIFFS, TARIFF_TYPE | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_TARIFFS.TARIFF_TYPE] |
| 9 | UPDATE_DT | DATETIME | 30 | not recorded | true | not recorded | TARIFFS, UPDATE_DT | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_TARIFFS.UPDATE_DT] |

Nullable: the export records this only when it is true. "Not stated" means the export does not carry it; it is not a statement that the column must be filled.

### Keys

| Key | Columns | Evidence |
|---|---|---|
| PK_TARIFFS | TARIFF_ID | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_TARIFFS.key.PK_TARIFFS] |

This is key 1 of 1 the repository declares for the table. The repository does not say whether a physical key is the primary key; this is the only key it declares. Two foreign keys of this subject area point at exactly this column [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_TARIFFS.key.PK_TARIFFS].

### Foreign keys and joins

- Foreign key FK_TRANS_TARIFFS from FCT_TRANSACTIONS_ACTUAL (TARIFF_ID) to this table (TARIFF_ID); stated in full in the section How the tables relate.
- Foreign key FK_TRANS_TARIFFS from FCT_TRANSACTIONS_DAILY (TARIFF_ID) to this table (TARIFF_ID); stated in full in the section How the tables relate.

### Read by

The source "DIM_TARIFFS" of the business table TARIFFS (listed in the packet without a Row identifier).

## Database table DIM_TIMES_DAILY

DIM_TIMES_DAILY is listed in `Iryna_Bradzetskaya_MT.U_SAL` with type `select`: it is not a table of the database but a query kept in the repository. It is not an alias of another table, and no description is recorded [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_TIMES_DAILY]. Its query, for the database type `DefaultMulDB`, is quoted here exactly as extracted [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_TIMES_DAILY.def.DefaultMulDB]:

```
SELECT TIME_ID,
  DAY_NAME,
  DAY_NUMBER_IN_WEEK,
  DAY_NUMBER_IN_MONTH,
  DAY_NUMBER_IN_YEAR,
  CALENDAR_WEEK_NUMBER,
  WEEK_ENDING_DATE,
  CALENDAR_MONTH_NUMBER,
  DAYS_IN_CAL_MONTH,
  END_OF_CAL_MONTH,
  CALENDAR_MONTH_NAME,
  DAYS_IN_CAL_QUARTER,
  BEG_OF_CAL_QUARTER,
  END_OF_CAL_QUARTER,
  CALENDAR_QUARTER_NUMBER,
  CALENDAR_YEAR,
  DAYS_IN_CAL_YEAR,
  BEG_OF_CAL_YEAR,
  END_OF_CAL_YEAR,
  TO_NUMBER (CALENDAR_YEAR || CALENDAR_QUARTER_NUMBER) AS YEAR_Q,
  TO_NUMBER (CALENDAR_YEAR || CALENDAR_MONTH_NUMBER) AS YEAR_M,
  TO_NUMBER (CALENDAR_YEAR || CALENDAR_QUARTER_NUMBER || CALENDAR_MONTH_NUMBER || CALENDAR_WEEK_NUMBER) AS YEAR_W
FROM DIM_TIMES_DAILY
```

Put mechanically: the query reads a database table also named DIM_TIMES_DAILY, passes 19 of its columns through unchanged, and adds three columns. YEAR_Q is `TO_NUMBER (CALENDAR_YEAR || CALENDAR_QUARTER_NUMBER)`, YEAR_M is `TO_NUMBER (CALENDAR_YEAR || CALENDAR_MONTH_NUMBER)`, and YEAR_W is `TO_NUMBER (CALENDAR_YEAR || CALENDAR_QUARTER_NUMBER || CALENDAR_MONTH_NUMBER || CALENDAR_WEEK_NUMBER)`: each joins the texts of the named columns end to end and turns the result into a number [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_TIMES_DAILY.def.DefaultMulDB]. What YEAR_W is meant to identify is not recorded (open question Q10). The export lists 22 columns for it, all 22 mapped by a field of this subject area and none not mapped, and 1 key (counted).

### Columns

| # | Column | Type | Precision | Scale | Nullable | External name | Mapped by | Evidence |
|---|---|---|---|---|---|---|---|---|
| 1 | BEG_OF_CAL_QUARTER | DATETIME | not recorded | not recorded | true | not recorded | TIMES_DAILY, BEG_OF_CAL_QUARTER | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_TIMES_DAILY.BEG_OF_CAL_QUARTER] |
| 2 | BEG_OF_CAL_YEAR | DATETIME | not recorded | not recorded | true | not recorded | TIMES_DAILY, BEG_OF_CAL_YEAR | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_TIMES_DAILY.BEG_OF_CAL_YEAR] |
| 3 | CALENDAR_MONTH_NAME | VARCHAR | 9 | not recorded | true | not recorded | TIMES_DAILY, CALENDAR_MONTH_NAME | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_TIMES_DAILY.CALENDAR_MONTH_NAME] |
| 4 | CALENDAR_MONTH_NUMBER | VARCHAR | 2 | not recorded | true | not recorded | TIMES_DAILY, CALENDAR_MONTH_NUMBER | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_TIMES_DAILY.CALENDAR_MONTH_NUMBER] |
| 5 | CALENDAR_QUARTER_NUMBER | VARCHAR | 1 | not recorded | true | not recorded | TIMES_DAILY, CALENDAR_QUARTER_NUMBER | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_TIMES_DAILY.CALENDAR_QUARTER_NUMBER] |
| 6 | CALENDAR_WEEK_NUMBER | VARCHAR | 1 | not recorded | true | not recorded | TIMES_DAILY, CALENDAR_WEEK_NUMBER | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_TIMES_DAILY.CALENDAR_WEEK_NUMBER] |
| 7 | CALENDAR_YEAR | VARCHAR | 4 | not recorded | true | not recorded | TIMES_DAILY, CALENDAR_YEAR | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_TIMES_DAILY.CALENDAR_YEAR] |
| 8 | DAYS_IN_CAL_MONTH | VARCHAR | 2 | not recorded | true | not recorded | TIMES_DAILY, DAYS_IN_CAL_MONTH | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_TIMES_DAILY.DAYS_IN_CAL_MONTH] |
| 9 | DAYS_IN_CAL_QUARTER | DOUBLE | 22 | not recorded | true | not recorded | TIMES_DAILY, DAYS_IN_CAL_QUARTER | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_TIMES_DAILY.DAYS_IN_CAL_QUARTER] |
| 10 | DAYS_IN_CAL_YEAR | DOUBLE | 22 | not recorded | true | not recorded | TIMES_DAILY, DAYS_IN_CAL_YEAR | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_TIMES_DAILY.DAYS_IN_CAL_YEAR] |
| 11 | DAY_NAME | VARCHAR | 9 | not recorded | true | not recorded | TIMES_DAILY, DAY_NAME | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_TIMES_DAILY.DAY_NAME] |
| 12 | DAY_NUMBER_IN_MONTH | VARCHAR | 2 | not recorded | true | not recorded | TIMES_DAILY, DAY_NUMBER_IN_MONTH | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_TIMES_DAILY.DAY_NUMBER_IN_MONTH] |
| 13 | DAY_NUMBER_IN_WEEK | VARCHAR | 1 | not recorded | true | not recorded | TIMES_DAILY, DAY_NUMBER_IN_WEEK | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_TIMES_DAILY.DAY_NUMBER_IN_WEEK] |
| 14 | DAY_NUMBER_IN_YEAR | VARCHAR | 3 | not recorded | true | not recorded | TIMES_DAILY, DAY_NUMBER_IN_YEAR | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_TIMES_DAILY.DAY_NUMBER_IN_YEAR] |
| 15 | END_OF_CAL_MONTH | DATETIME | 3 | not recorded | true | not recorded | TIMES_DAILY, END_OF_CAL_MONTH | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_TIMES_DAILY.END_OF_CAL_MONTH] |
| 16 | END_OF_CAL_QUARTER | DATETIME | 3 | not recorded | true | not recorded | TIMES_DAILY, END_OF_CAL_QUARTER | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_TIMES_DAILY.END_OF_CAL_QUARTER] |
| 17 | END_OF_CAL_YEAR | DATETIME | 3 | not recorded | true | not recorded | TIMES_DAILY, END_OF_CAL_YEAR | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_TIMES_DAILY.END_OF_CAL_YEAR] |
| 18 | TIME_ID | DATETIME | 3 | not recorded | not stated | not recorded | TIMES_DAILY, TIME_ID | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_TIMES_DAILY.TIME_ID] |
| 19 | WEEK_ENDING_DATE | DATETIME | 3 | not recorded | true | not recorded | TIMES_DAILY, WEEK_ENDING_DATE | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_TIMES_DAILY.WEEK_ENDING_DATE] |
| 20 | YEAR_M | INT | not recorded | not recorded | not stated | not recorded | TIMES_DAILY, YEAR_M | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_TIMES_DAILY.YEAR_M] |
| 21 | YEAR_Q | INT | not recorded | not recorded | not stated | not recorded | TIMES_DAILY, YEAR_Q | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_TIMES_DAILY.YEAR_Q] |
| 22 | YEAR_W | INT | not recorded | not recorded | not stated | not recorded | TIMES_DAILY, YEAR_W | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_TIMES_DAILY.YEAR_W] |

Nullable: the export records this only when it is true. "Not stated" means the export does not carry it; it is not a statement that the column must be filled.

### Keys

| Key | Columns | Evidence |
|---|---|---|
| DIM_TIMES_DAILY_PK | TIME_ID | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_TIMES_DAILY.key.DIM_TIMES_DAILY_PK] |

This is key 1 of 1 the repository declares for the table. The repository does not say whether a physical key is the primary key; this is the only key it declares. Two foreign keys of this subject area point at exactly this column [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_TIMES_DAILY.key.DIM_TIMES_DAILY_PK].

### Foreign keys and joins

- Foreign key FK_TRANS_TIMES from FCT_TRANSACTIONS_ACTUAL (EVENT_DT) to this table (TIME_ID); stated in full in the section How the tables relate.
- Foreign key FK_TRANS_TIMES from FCT_TRANSACTIONS_DAILY (EVENT_DT) to this table (TIME_ID); stated in full in the section How the tables relate.

### Read by

The source "DIM_TIMES_DAILY" of the business table TIMES_DAILY (listed in the packet without a Row identifier).

## Database table FCT_TRANSACTIONS_ACTUAL

FCT_TRANSACTIONS_ACTUAL is listed in `Iryna_Bradzetskaya_MT.U_SAL` with type `select`: it is not a table of the database but a query kept in the repository. It is not an alias of another table, and no description is recorded [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.FCT_TRANSACTIONS_ACTUAL]. Its query, for the database type `DefaultMulDB`, is quoted here exactly as extracted [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.FCT_TRANSACTIONS_ACTUAL.def.DefaultMulDB]:

```
SELECT * FROM FCT_TRANSACTIONS_DAILY
WHERE
EXTRACT (YEAR from EVENT_DT) = VALUEOF(BI_YEAR)
```

Put mechanically: the query reads every column of the database table FCT_TRANSACTIONS_DAILY and keeps the rows whose year of EVENT_DT equals the value `BI_YEAR` [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.FCT_TRANSACTIONS_ACTUAL.def.DefaultMulDB]. The variable `BI_YEAR` is described in the section Values set per user or for the whole system. The export lists 14 columns for it, all 14 mapped by a field of this subject area and none not mapped, and no key (counted).

### Columns

| # | Column | Type | Precision | Scale | Nullable | External name | Mapped by | Evidence |
|---|---|---|---|---|---|---|---|---|
| 1 | AMOUNT_PAYMENT | DOUBLE | 22 | not recorded | true | not recorded | FCT_TRANSACTIONS_ACTUAL, AMOUNT_PAYMENT | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.FCT_TRANSACTIONS_ACTUAL.AMOUNT_PAYMENT] |
| 2 | COUNT_PAYMENT | DOUBLE | 22 | not recorded | true | not recorded | FCT_TRANSACTIONS_ACTUAL, COUNT_PAYMENT | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.FCT_TRANSACTIONS_ACTUAL.COUNT_PAYMENT] |
| 3 | CURRENCY_SUR_ID | DOUBLE | 22 | not recorded | true | not recorded | FCT_TRANSACTIONS_ACTUAL, CURRENCY_SUR_ID | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.FCT_TRANSACTIONS_ACTUAL.CURRENCY_SUR_ID] |
| 4 | CUST_SEND_ID | DOUBLE | 22 | not recorded | true | not recorded | FCT_TRANSACTIONS_ACTUAL, CUST_SEND_ID | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.FCT_TRANSACTIONS_ACTUAL.CUST_SEND_ID] |
| 5 | CUS_REC_ID | DOUBLE | 22 | not recorded | true | not recorded | FCT_TRANSACTIONS_ACTUAL, CUS_REC_ID | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.FCT_TRANSACTIONS_ACTUAL.CUS_REC_ID] |
| 6 | EVENT_DT | DATETIME | 22 | not recorded | true | not recorded | FCT_TRANSACTIONS_ACTUAL, EVENT_DT | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.FCT_TRANSACTIONS_ACTUAL.EVENT_DT] |
| 7 | GEO_REC_SUR_ID | DOUBLE | 22 | not recorded | true | not recorded | FCT_TRANSACTIONS_ACTUAL, GEO_REC_SUR_ID | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.FCT_TRANSACTIONS_ACTUAL.GEO_REC_SUR_ID] |
| 8 | GEO_SEND_SUR_ID | DOUBLE | 22 | not recorded | true | not recorded | FCT_TRANSACTIONS_ACTUAL, GEO_SEND_SUR_ID | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.FCT_TRANSACTIONS_ACTUAL.GEO_SEND_SUR_ID] |
| 9 | INSERT_DT | DATETIME | 22 | not recorded | true | not recorded | FCT_TRANSACTIONS_ACTUAL, INSERT_DT | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.FCT_TRANSACTIONS_ACTUAL.INSERT_DT] |
| 10 | OPERATION_ID | DOUBLE | 22 | not recorded | true | not recorded | FCT_TRANSACTIONS_ACTUAL, OPERATION_ID | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.FCT_TRANSACTIONS_ACTUAL.OPERATION_ID] |
| 11 | PERIOD_ID | DOUBLE | 22 | not recorded | true | not recorded | FCT_TRANSACTIONS_ACTUAL, PERIOD_ID | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.FCT_TRANSACTIONS_ACTUAL.PERIOD_ID] |
| 12 | TARIFF_ID | DOUBLE | 22 | not recorded | true | not recorded | FCT_TRANSACTIONS_ACTUAL, TARIFF_ID | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.FCT_TRANSACTIONS_ACTUAL.TARIFF_ID] |
| 13 | TA_COUNTRY_REC_ID | DOUBLE | 22 | not recorded | true | not recorded | FCT_TRANSACTIONS_ACTUAL, TA_COUNTRY_REC_ID | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.FCT_TRANSACTIONS_ACTUAL.TA_COUNTRY_REC_ID] |
| 14 | TA_COUNTRY_SEND_ID | DOUBLE | 22 | not recorded | true | not recorded | FCT_TRANSACTIONS_ACTUAL, TA_COUNTRY_SEND_ID | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.FCT_TRANSACTIONS_ACTUAL.TA_COUNTRY_SEND_ID] |

Nullable: the export records this only when it is true. "Not stated" means the export does not carry it; it is not a statement that the column must be filled.

### Keys

Keys of FCT_TRANSACTIONS_ACTUAL: not recorded. The repository declares no key for it, so nothing in the repository says which column(s) identify a row (listed in the packet without a Row identifier).

### Foreign keys and joins

- Foreign key FK_TRANS_CURRENCY from this table (CURRENCY_SUR_ID) to DIM_CURRENCY_SCD (CURRENCY_SUR_ID); stated in full in the section How the tables relate.
- Foreign key FK_TRANS_LOC_REC from this table (GEO_REC_SUR_ID) to DIM_LOCATIONS_REC (GEO_ID); stated in full in the section How the tables relate.
- Foreign key FK_TRANS_LOC_SEND from this table (GEO_SEND_SUR_ID) to DIM_LOCATIONS_SEND (GEO_ID); stated in full in the section How the tables relate.
- Foreign key FK_TRANS_OPERATIONS from this table (OPERATION_ID) to DIM_OPERATIONS (OPERATION_ID); stated in full in the section How the tables relate.
- Foreign key FK_TRANS_TARIFFS from this table (TARIFF_ID) to DIM_TARIFFS (TARIFF_ID); stated in full in the section How the tables relate.
- Foreign key FK_TRANS_TIMES from this table (EVENT_DT) to DIM_TIMES_DAILY (TIME_ID); stated in full in the section How the tables relate.
- Foreign key FK_TR_CUST_REC from this table (CUS_REC_ID) to DIM_CUSTOMERS_REC (CUST_ID); stated in full in the section How the tables relate.
- Foreign key FK_TR_CUST_SEND from this table (CUST_SEND_ID) to DIM_CUSTOMERS_SEND (CUST_ID); stated in full in the section How the tables relate.

### Read by

The source "FCT_TRANSACTIONS_ACTUAL" of the business table FCT_TRANSACTIONS_ACTUAL (listed in the packet without a Row identifier).

## Database table FCT_TRANSACTIONS_DAILY

FCT_TRANSACTIONS_DAILY is a database table in `Iryna_Bradzetskaya_MT.U_SAL`, type `none`; it is not an alias of another table, and no description is recorded [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.FCT_TRANSACTIONS_DAILY]. Its object in the database, for the database type `DefaultMulDB`, is `FCT_TRANSACTIONS_DAILY` [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.FCT_TRANSACTIONS_DAILY.def.DefaultMulDB]. The export lists 14 columns for it, 10 mapped by a field of this subject area and 4 not, and no key (counted).

### Columns

| # | Column | Type | Precision | Scale | Nullable | External name | Mapped by | Evidence |
|---|---|---|---|---|---|---|---|---|
| 1 | AMOUNT_PAYMENT | DOUBLE | 22 | not recorded | true | not recorded | TRANSACTIONS_DAILY, AMOUNT_PAYMENT | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.FCT_TRANSACTIONS_DAILY.AMOUNT_PAYMENT] |
| 2 | COUNT_PAYMENT | DOUBLE | 22 | not recorded | true | not recorded | TRANSACTIONS_DAILY, COUNT_PAYMENT | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.FCT_TRANSACTIONS_DAILY.COUNT_PAYMENT] |
| 3 | CURRENCY_SUR_ID | DOUBLE | 22 | not recorded | true | not recorded | TRANSACTIONS_DAILY, CURRENCY_SUR_ID | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.FCT_TRANSACTIONS_DAILY.CURRENCY_SUR_ID] |
| 4 | CUST_SEND_ID | DOUBLE | 22 | not recorded | true | not recorded | TRANSACTIONS_DAILY, CUST_SEND_ID | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.FCT_TRANSACTIONS_DAILY.CUST_SEND_ID] |
| 5 | CUS_REC_ID | DOUBLE | 22 | not recorded | true | not recorded | TRANSACTIONS_DAILY, CUS_REC_ID | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.FCT_TRANSACTIONS_DAILY.CUS_REC_ID] |
| 6 | EVENT_DT | DATETIME | 22 | not recorded | true | not recorded | TRANSACTIONS_DAILY, EVENT_DT | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.FCT_TRANSACTIONS_DAILY.EVENT_DT] |
| 7 | GEO_REC_SUR_ID | DOUBLE | 22 | not recorded | true | not recorded | TRANSACTIONS_DAILY, GEO_REC_SUR_ID | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.FCT_TRANSACTIONS_DAILY.GEO_REC_SUR_ID] |
| 8 | GEO_SEND_SUR_ID | DOUBLE | 22 | not recorded | true | not recorded | TRANSACTIONS_DAILY, GEO_SEND_SUR_ID | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.FCT_TRANSACTIONS_DAILY.GEO_SEND_SUR_ID] |
| 9 | INSERT_DT | DATETIME | 22 | not recorded | true | not recorded | not mapped | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.FCT_TRANSACTIONS_DAILY.INSERT_DT] |
| 10 | OPERATION_ID | DOUBLE | 22 | not recorded | true | not recorded | TRANSACTIONS_DAILY, OPERATION_ID | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.FCT_TRANSACTIONS_DAILY.OPERATION_ID] |
| 11 | PERIOD_ID | DOUBLE | 22 | not recorded | true | not recorded | not mapped | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.FCT_TRANSACTIONS_DAILY.PERIOD_ID] |
| 12 | TARIFF_ID | DOUBLE | 22 | not recorded | true | not recorded | TRANSACTIONS_DAILY, TARIFF_ID | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.FCT_TRANSACTIONS_DAILY.TARIFF_ID] |
| 13 | TA_COUNTRY_REC_ID | DOUBLE | 22 | not recorded | true | not recorded | not mapped | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.FCT_TRANSACTIONS_DAILY.TA_COUNTRY_REC_ID] |
| 14 | TA_COUNTRY_SEND_ID | DOUBLE | 22 | not recorded | true | not recorded | not mapped | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.FCT_TRANSACTIONS_DAILY.TA_COUNTRY_SEND_ID] |

Nullable: the export records this only when it is true. "Not stated" means the export does not carry it; it is not a statement that the column must be filled.

Four columns are not mapped by any field of this subject area: INSERT_DT, PERIOD_ID, TA_COUNTRY_REC_ID and TA_COUNTRY_SEND_ID [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.FCT_TRANSACTIONS_DAILY.PERIOD_ID].

### Keys

Keys of FCT_TRANSACTIONS_DAILY: not recorded. The repository declares no key for it, so nothing in the repository says which column(s) identify a row (listed in the packet without a Row identifier).

### Foreign keys and joins

- Foreign key FK_TRANS_CURRENCY from this table (CURRENCY_SUR_ID) to DIM_CURRENCY_SCD (CURRENCY_SUR_ID); stated in full in the section How the tables relate.
- Foreign key FK_TRANS_LOC_REC from this table (GEO_REC_SUR_ID) to DIM_LOCATIONS_REC (GEO_ID); stated in full in the section How the tables relate.
- Foreign key FK_TRANS_LOC_SEND from this table (GEO_SEND_SUR_ID) to DIM_LOCATIONS_SEND (GEO_ID); stated in full in the section How the tables relate.
- Foreign key FK_TRANS_OPERATIONS from this table (OPERATION_ID) to DIM_OPERATIONS (OPERATION_ID); stated in full in the section How the tables relate.
- Foreign key FK_TRANS_TARIFFS from this table (TARIFF_ID) to DIM_TARIFFS (TARIFF_ID); stated in full in the section How the tables relate.
- Foreign key FK_TRANS_TIMES from this table (EVENT_DT) to DIM_TIMES_DAILY (TIME_ID); stated in full in the section How the tables relate.
- Foreign key FK_TR_CUST_REC from this table (CUS_REC_ID) to DIM_CUSTOMERS_REC (CUST_ID); stated in full in the section How the tables relate.
- Foreign key FK_TR_CUST_SEND from this table (CUST_SEND_ID) to DIM_CUSTOMERS_SEND (CUST_ID); stated in full in the section How the tables relate.

### Read by

The source "FCT_TRANSACTIONS_DAILY" of the business table TRANSACTIONS_DAILY (listed in the packet without a Row identifier).

## How the tables relate

### Business-model joins

The business model records 12 joins between business tables: 8 from TRANSACTIONS_DAILY and 4 from FCT_TRANSACTIONS_ACTUAL. Rows are copied as printed.

| From | Rows | To | Rows | Join type | Evidence |
|---|---|---|---|---|---|
| TRANSACTIONS_DAILY | n..n | TIMES_DAILY | 0..1 | Inner | [kb:bi-analytics-2013::bmm::joins.Iryna_Bradzetskaya_MT#1:Relationship_2004:4041821380622953] |
| TRANSACTIONS_DAILY | n..n | LOCATIONS_SEND | 0..1 | Inner | [kb:bi-analytics-2013::bmm::joins.Iryna_Bradzetskaya_MT#1:Relationship_2004:4041851380622953] |
| TRANSACTIONS_DAILY | n..n | CURRENCY | 0..1 | Inner | [kb:bi-analytics-2013::bmm::joins.Iryna_Bradzetskaya_MT#1:Relationship_2004:4041881380622953] |
| TRANSACTIONS_DAILY | n..n | CUSTOMERS_REC | 0..1 | Inner | [kb:bi-analytics-2013::bmm::joins.Iryna_Bradzetskaya_MT#1:Relationship_2004:4041911380622953] |
| TRANSACTIONS_DAILY | n..n | OPERATIONS | 0..1 | Inner | [kb:bi-analytics-2013::bmm::joins.Iryna_Bradzetskaya_MT#1:Relationship_2004:4041941380622953] |
| TRANSACTIONS_DAILY | n..n | TARIFFS | 0..1 | Inner | [kb:bi-analytics-2013::bmm::joins.Iryna_Bradzetskaya_MT#1:Relationship_2004:4041971380622953] |
| TRANSACTIONS_DAILY | n..n | CUSTOMERS_SEND | 0..1 | Inner | [kb:bi-analytics-2013::bmm::joins.Iryna_Bradzetskaya_MT#1:Relationship_2004:4042281380622953] |
| TRANSACTIONS_DAILY | n..n | LOCATIONS_REC | 0..1 | Inner | [kb:bi-analytics-2013::bmm::joins.Iryna_Bradzetskaya_MT#1:Relationship_2004:4042671380622953] |
| FCT_TRANSACTIONS_ACTUAL | n..n | TIMES_DAILY | 0..1 | Inner | [kb:bi-analytics-2013::bmm::joins.Iryna_Bradzetskaya_MT#1:Relationship_2004:4072641381133744] |
| FCT_TRANSACTIONS_ACTUAL | n..n | CURRENCY | 0..1 | Inner | [kb:bi-analytics-2013::bmm::joins.Iryna_Bradzetskaya_MT#1:Relationship_2004:4072671381133744] |
| FCT_TRANSACTIONS_ACTUAL | n..n | OPERATIONS | 0..1 | Inner | [kb:bi-analytics-2013::bmm::joins.Iryna_Bradzetskaya_MT#1:Relationship_2004:4072701381133744] |
| FCT_TRANSACTIONS_ACTUAL | n..n | TARIFFS | 0..1 | Inner | [kb:bi-analytics-2013::bmm::joins.Iryna_Bradzetskaya_MT#1:Relationship_2004:4072731381133744] |

FCT_TRANSACTIONS_ACTUAL is joined in the business model to TIMES_DAILY, CURRENCY, OPERATIONS and TARIFFS only; no business-model join is recorded from it to CUSTOMERS_REC, CUSTOMERS_SEND, LOCATIONS_REC or LOCATIONS_SEND (counted from the rows above; open question Q11).

### Database joins

The repository records 17 foreign keys between the database tables of this subject area. Each joins one column pair. No cardinality is recorded for any of them. Type, precision and nullable are copied as printed.

| Kind | Name | From table (column; type, precision, nullable) | To table (column; type, precision, nullable) | Cardinality | Evidence |
|---|---|---|---|---|---|
| foreign key | DIM_OPERATIONS_Foreign Key | DIM_OPERATIONS (OPERATION_METHOD_ID; type DOUBLE, precision 22, nullable true) | DIM_OPERATION_METHODS (OPERATION_METHOD_ID; type DOUBLE, nullable true) | not recorded | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_OPERATIONS.DIM_OPERATIONS_Foreign Key] |
| foreign key | FK_TRANS_CURRENCY | FCT_TRANSACTIONS_ACTUAL (CURRENCY_SUR_ID; type DOUBLE, precision 22, nullable true) | DIM_CURRENCY_SCD (CURRENCY_SUR_ID; type DOUBLE, precision 22, nullable not stated) | not recorded | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.FCT_TRANSACTIONS_ACTUAL.FK_TRANS_CURRENCY] |
| foreign key | FK_TRANS_LOC_REC | FCT_TRANSACTIONS_ACTUAL (GEO_REC_SUR_ID; type DOUBLE, precision 22, nullable true) | DIM_LOCATIONS_REC (GEO_ID; type DOUBLE, precision 22, nullable not stated) | not recorded | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.FCT_TRANSACTIONS_ACTUAL.FK_TRANS_LOC_REC] |
| foreign key | FK_TRANS_LOC_SEND | FCT_TRANSACTIONS_ACTUAL (GEO_SEND_SUR_ID; type DOUBLE, precision 22, nullable true) | DIM_LOCATIONS_SEND (GEO_ID; type DOUBLE, precision 22, nullable not stated) | not recorded | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.FCT_TRANSACTIONS_ACTUAL.FK_TRANS_LOC_SEND] |
| foreign key | FK_TRANS_OPERATIONS | FCT_TRANSACTIONS_ACTUAL (OPERATION_ID; type DOUBLE, precision 22, nullable true) | DIM_OPERATIONS (OPERATION_ID; type DOUBLE, precision 22, nullable not stated) | not recorded | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.FCT_TRANSACTIONS_ACTUAL.FK_TRANS_OPERATIONS] |
| foreign key | FK_TRANS_TARIFFS | FCT_TRANSACTIONS_ACTUAL (TARIFF_ID; type DOUBLE, precision 22, nullable true) | DIM_TARIFFS (TARIFF_ID; type DOUBLE, precision 22, nullable not stated) | not recorded | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.FCT_TRANSACTIONS_ACTUAL.FK_TRANS_TARIFFS] |
| foreign key | FK_TRANS_TIMES | FCT_TRANSACTIONS_ACTUAL (EVENT_DT; type DATETIME, precision 22, nullable true) | DIM_TIMES_DAILY (TIME_ID; type DATETIME, precision 3, nullable not stated) | not recorded | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.FCT_TRANSACTIONS_ACTUAL.FK_TRANS_TIMES] |
| foreign key | FK_TR_CUST_REC | FCT_TRANSACTIONS_ACTUAL (CUS_REC_ID; type DOUBLE, precision 22, nullable true) | DIM_CUSTOMERS_REC (CUST_ID; type DOUBLE, precision 22, nullable not stated) | not recorded | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.FCT_TRANSACTIONS_ACTUAL.FK_TR_CUST_REC] |
| foreign key | FK_TR_CUST_SEND | FCT_TRANSACTIONS_ACTUAL (CUST_SEND_ID; type DOUBLE, precision 22, nullable true) | DIM_CUSTOMERS_SEND (CUST_ID; type DOUBLE, precision 22, nullable not stated) | not recorded | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.FCT_TRANSACTIONS_ACTUAL.FK_TR_CUST_SEND] |
| foreign key | FK_TRANS_CURRENCY | FCT_TRANSACTIONS_DAILY (CURRENCY_SUR_ID; type DOUBLE, precision 22, nullable true) | DIM_CURRENCY_SCD (CURRENCY_SUR_ID; type DOUBLE, precision 22, nullable not stated) | not recorded | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.FCT_TRANSACTIONS_DAILY.FK_TRANS_CURRENCY] |
| foreign key | FK_TRANS_LOC_REC | FCT_TRANSACTIONS_DAILY (GEO_REC_SUR_ID; type DOUBLE, precision 22, nullable true) | DIM_LOCATIONS_REC (GEO_ID; type DOUBLE, precision 22, nullable not stated) | not recorded | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.FCT_TRANSACTIONS_DAILY.FK_TRANS_LOC_REC] |
| foreign key | FK_TRANS_LOC_SEND | FCT_TRANSACTIONS_DAILY (GEO_SEND_SUR_ID; type DOUBLE, precision 22, nullable true) | DIM_LOCATIONS_SEND (GEO_ID; type DOUBLE, precision 22, nullable not stated) | not recorded | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.FCT_TRANSACTIONS_DAILY.FK_TRANS_LOC_SEND] |
| foreign key | FK_TRANS_OPERATIONS | FCT_TRANSACTIONS_DAILY (OPERATION_ID; type DOUBLE, precision 22, nullable true) | DIM_OPERATIONS (OPERATION_ID; type DOUBLE, precision 22, nullable not stated) | not recorded | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.FCT_TRANSACTIONS_DAILY.FK_TRANS_OPERATIONS] |
| foreign key | FK_TRANS_TARIFFS | FCT_TRANSACTIONS_DAILY (TARIFF_ID; type DOUBLE, precision 22, nullable true) | DIM_TARIFFS (TARIFF_ID; type DOUBLE, precision 22, nullable not stated) | not recorded | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.FCT_TRANSACTIONS_DAILY.FK_TRANS_TARIFFS] |
| foreign key | FK_TRANS_TIMES | FCT_TRANSACTIONS_DAILY (EVENT_DT; type DATETIME, precision 22, nullable true) | DIM_TIMES_DAILY (TIME_ID; type DATETIME, precision 3, nullable not stated) | not recorded | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.FCT_TRANSACTIONS_DAILY.FK_TRANS_TIMES] |
| foreign key | FK_TR_CUST_REC | FCT_TRANSACTIONS_DAILY (CUS_REC_ID; type DOUBLE, precision 22, nullable true) | DIM_CUSTOMERS_REC (CUST_ID; type DOUBLE, precision 22, nullable not stated) | not recorded | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.FCT_TRANSACTIONS_DAILY.FK_TR_CUST_REC] |
| foreign key | FK_TR_CUST_SEND | FCT_TRANSACTIONS_DAILY (CUST_SEND_ID; type DOUBLE, precision 22, nullable true) | DIM_CUSTOMERS_SEND (CUST_ID; type DOUBLE, precision 22, nullable not stated) | not recorded | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.FCT_TRANSACTIONS_DAILY.FK_TR_CUST_SEND] |

Two of the database joins differ from the rest in what the packet prints: on DIM_OPERATIONS_Foreign Key the column of DIM_OPERATION_METHODS has no precision recorded [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_OPERATIONS.DIM_OPERATIONS_Foreign Key]; and on both FK_TRANS_TIMES joins the date column EVENT_DT (precision 22) is paired with TIME_ID (precision 3) [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.FCT_TRANSACTIONS_DAILY.FK_TRANS_TIMES] [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.FCT_TRANSACTIONS_ACTUAL.FK_TRANS_TIMES].

## Drill paths

The business model records 3 drill paths ("dimensions"). Two of them, for the location tables, are described here; the third follows below. All member counts are as recorded.

### Drill path "Locations_Rec"

A drill path of levels with 4 levels; whether it is marked as a time dimension is not recorded [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.Locations_Rec]. Its default root level is "All Part" [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.Locations_Rec].

The path: All Part → Part → Region → Country.

| Level | Level above | Level(s) below | Key column | Keys (name; fields; primary and drill-down as recorded) | Member count as recorded | Shown to report authors as | Evidence |
|---|---|---|---|---|---|---|---|
| All Part (grand total) | none recorded (a top level) | Part | none recorded | none recorded | 1 | level 0 "All Part" | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.Locations_Rec.All Part] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.LOCATIONS_REC.Locations_Rec.All Part] |
| Part | All Part | Region | LOCATIONS_REC.GEO_PART_DESC | Part_id; GEO_PART_ID; primary true, drill-down not recorded. Part_name; GEO_PART_DESC; primary not recorded, drill-down true | 1 | level 1 "Part", showing GEO_PART_DESC | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.Locations_Rec.Part] [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.Locations_Rec.Part.key.Part_id] [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.Locations_Rec.Part.key.Part_name] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.LOCATIONS_REC.Locations_Rec.Part] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.LOCATIONS_REC.Locations_Rec.Part.0] |
| Region | Part | Country | LOCATIONS_REC.GEO_REGION_DESC | Reg_id; GEO_REGION_ID; primary true, drill-down not recorded. Reg_name; GEO_REGION_DESC; primary not recorded, drill-down true | 1 | level 2 "Region", showing GEO_REGION_DESC | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.Locations_Rec.Region] [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.Locations_Rec.Region.key.Reg_id] [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.Locations_Rec.Region.key.Reg_name] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.LOCATIONS_REC.Locations_Rec.Region] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.LOCATIONS_REC.Locations_Rec.Region.0] |
| Country | Region | none recorded (a lowest level) | LOCATIONS_REC.GEO_COUNTRY_NAME | C_id; GEO_ID; primary true, drill-down not recorded. Country_name; GEO_COUNTRY_NAME; primary not recorded, drill-down true | 1 | level 3 "Country", showing GEO_COUNTRY_NAME | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.Locations_Rec.Country] [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.Locations_Rec.Country.key.C_id] [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.Locations_Rec.Country.key.Country_name] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.LOCATIONS_REC.Locations_Rec.Country] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.LOCATIONS_REC.Locations_Rec.Country.0] |

It is offered to report authors as the hierarchy "Locations_Rec" in the table LOCATIONS_REC, with 4 levels [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.LOCATIONS_REC.Locations_Rec].

### Drill path "Locations_Send"

A drill path of levels with 4 levels; whether it is marked as a time dimension is not recorded [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.Locations_Send]. Its default root level is "All_Part" [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.Locations_Send].

The path: All_Part → Part → Region → Country.

| Level | Level above | Level(s) below | Key column | Keys (name; fields; primary and drill-down as recorded) | Member count as recorded | Shown to report authors as | Evidence |
|---|---|---|---|---|---|---|---|
| All_Part (grand total) | none recorded (a top level) | Part | none recorded | none recorded | 1 | level 0 "All_Part" | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.Locations_Send.All_Part] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.LOCATIONS_SEND.Locations_Send.All_Part] |
| Part | All_Part | Region | LOCATIONS_SEND.GEO_PART_DESC | Part; GEO_PART_ID; primary true, drill-down not recorded. Part_name; GEO_PART_DESC; primary not recorded, drill-down true | 1 | level 1 "Part", showing GEO_PART_DESC | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.Locations_Send.Part] [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.Locations_Send.Part.key.Part] [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.Locations_Send.Part.key.Part_name] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.LOCATIONS_SEND.Locations_Send.Part] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.LOCATIONS_SEND.Locations_Send.Part.0] |
| Region | Part | Country | LOCATIONS_SEND.GEO_REGION_DESC | Reg_name; GEO_REGION_DESC; primary not recorded, drill-down true. Region; GEO_REGION_ID; primary true, drill-down not recorded | 1 | level 2 "Region", showing GEO_REGION_DESC | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.Locations_Send.Region] [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.Locations_Send.Region.key.Reg_name] [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.Locations_Send.Region.key.Region] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.LOCATIONS_SEND.Locations_Send.Region] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.LOCATIONS_SEND.Locations_Send.Region.0] |
| Country | Region | none recorded (a lowest level) | LOCATIONS_SEND.GEO_COUNTRY_NAME | Country_id; GEO_ID; primary true, drill-down not recorded. Country_name; GEO_COUNTRY_NAME; primary not recorded, drill-down true | 1 | level 3 "Country", showing GEO_COUNTRY_NAME | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.Locations_Send.Country] [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.Locations_Send.Country.key.Country_id] [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.Locations_Send.Country.key.Country_name] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.LOCATIONS_SEND.Locations_Send.Country] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.LOCATIONS_SEND.Locations_Send.Country.0] |

It is offered to report authors as the hierarchy "Locations_Send" in the table LOCATIONS_SEND, with 4 levels [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.LOCATIONS_SEND.Locations_Send].

In both drill paths the lowest level, "Country", has GEO_ID as its primary key, the same column that is the key of the business table [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.Locations_Rec.Country.key.C_id] [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.Locations_Send.Country.key.Country_id]. Every level's member count is recorded as 1. Whether every row of the location tables is a country, or whether LEVEL_CODE marks rows of other levels, is not recorded (open question Q7).

### Drill path "Times"

A drill path of levels with 6 levels, marked as a time dimension: true [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.Times]. Its default root level is "Total Years" [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.Times].

The path, levels in the packet's order: Total Years → Years → Quarter → Month → Day, Week. Month has two levels above it (Years and Quarter) and two below it (Day and Week); Day has two levels above it (Month and Week).

| Level | Level above | Level(s) below | Key column | Keys (name; fields; primary, chronological and drill-down as recorded) | Member count as recorded | Evidence |
|---|---|---|---|---|---|---|
| Total Years (grand total) | none recorded (a top level) | Years | none recorded | none recorded | 1 | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.Times.Total Years] |
| Years | Total Years | Quarter, Month | TIMES_DAILY.CALENDAR_YEAR | Year; CALENDAR_YEAR; primary true, chronological true, drill-down true | 12 | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.Times.Years] [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.Times.Years.key.Year] |
| Quarter | Years | Month | TIMES_DAILY.CALENDAR_QUARTER_NUMBER | Q; YEAR_Q; primary not recorded, chronological true, drill-down not recorded. Quarter; CALENDAR_QUARTER_NUMBER; primary true, chronological not recorded, drill-down true | 48 | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.Times.Quarter] [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.Times.Quarter.key.Q] [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.Times.Quarter.key.Quarter] |
| Month | Years, Quarter | Day, Week | TIMES_DAILY.CALENDAR_MONTH_NAME | M; YEAR_M; primary not recorded, chronological true, drill-down not recorded. Month; CALENDAR_MONTH_NAME; primary not recorded, chronological not recorded, drill-down true. Month_num; CALENDAR_MONTH_NUMBER; primary true, chronological not recorded, drill-down not recorded | 144 | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.Times.Month] [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.Times.Month.key.M] [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.Times.Month.key.Month] [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.Times.Month.key.Month_num] |
| Day | Month, Week | none recorded (a lowest level) | TIMES_DAILY.DAY_NUMBER_IN_MONTH | DAy; TIME_ID; primary true, chronological true, drill-down not recorded. Day_number; DAY_NUMBER_IN_MONTH; primary not recorded, chronological not recorded, drill-down true | 4400 | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.Times.Day] [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.Times.Day.key.DAy] [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.Times.Day.key.Day_number] |
| Week | Month | Day | TIMES_DAILY.CALENDAR_WEEK_NUMBER | W; YEAR_W; primary not recorded, chronological not recorded, drill-down not recorded. Week; CALENDAR_WEEK_NUMBER; primary true, chronological not recorded, drill-down true | 650 | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.Times.Week] [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.Times.Week.key.W] [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.Times.Week.key.Week] |

The primary keys of the levels Quarter, Month and Week are the columns CALENDAR_QUARTER_NUMBER, CALENDAR_MONTH_NUMBER and CALENDAR_WEEK_NUMBER alone; the keys built with the year (YEAR_Q, YEAR_M, YEAR_W) are recorded as further keys, not as primary [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.Times.Quarter.key.Quarter] [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.Times.Month.key.Month_num] [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.Times.Week.key.Week] (open question Q12).

#### Offered to report authors

The drill path is offered to report authors as four hierarchies in the table TIMES_DAILY.

| Hierarchy | Levels (shown column) | Evidence |
|---|---|---|
| Times - Month - Day (4 levels) | Total Years; Years (CALENDAR_YEAR); Month (CALENDAR_MONTH_NAME); Day (DAY_NUMBER_IN_MONTH) | [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.TIMES_DAILY.Times - Month - Day] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.TIMES_DAILY.Times - Month - Day.Total Years] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.TIMES_DAILY.Times - Month - Day.Years] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.TIMES_DAILY.Times - Month - Day.Years.0] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.TIMES_DAILY.Times - Month - Day.Month] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.TIMES_DAILY.Times - Month - Day.Month.0] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.TIMES_DAILY.Times - Month - Day.Day] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.TIMES_DAILY.Times - Month - Day.Day.0] |
| Times - Month - Week (5 levels) | Total Years; Years (CALENDAR_YEAR); Month (CALENDAR_MONTH_NAME); Week (CALENDAR_WEEK_NUMBER); Day (DAY_NUMBER_IN_MONTH) | [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.TIMES_DAILY.Times - Month - Week] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.TIMES_DAILY.Times - Month - Week.Total Years] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.TIMES_DAILY.Times - Month - Week.Years] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.TIMES_DAILY.Times - Month - Week.Years.0] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.TIMES_DAILY.Times - Month - Week.Month] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.TIMES_DAILY.Times - Month - Week.Month.0] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.TIMES_DAILY.Times - Month - Week.Week] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.TIMES_DAILY.Times - Month - Week.Week.0] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.TIMES_DAILY.Times - Month - Week.Day] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.TIMES_DAILY.Times - Month - Week.Day.0] |
| Times - Quarter - Day (5 levels) | Total Years; Years (CALENDAR_YEAR); Quarter (CALENDAR_QUARTER_NUMBER); Month (CALENDAR_MONTH_NAME); Day (DAY_NUMBER_IN_MONTH) | [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.TIMES_DAILY.Times - Quarter - Day] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.TIMES_DAILY.Times - Quarter - Day.Total Years] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.TIMES_DAILY.Times - Quarter - Day.Years] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.TIMES_DAILY.Times - Quarter - Day.Years.0] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.TIMES_DAILY.Times - Quarter - Day.Quarter] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.TIMES_DAILY.Times - Quarter - Day.Quarter.0] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.TIMES_DAILY.Times - Quarter - Day.Month] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.TIMES_DAILY.Times - Quarter - Day.Month.0] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.TIMES_DAILY.Times - Quarter - Day.Day] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.TIMES_DAILY.Times - Quarter - Day.Day.0] |
| Times - Quarter - Week (6 levels) | Total Years; Years (CALENDAR_YEAR); Quarter (CALENDAR_QUARTER_NUMBER); Month (CALENDAR_MONTH_NAME); Week (CALENDAR_WEEK_NUMBER); Day (DAY_NUMBER_IN_MONTH) | [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.TIMES_DAILY.Times - Quarter - Week] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.TIMES_DAILY.Times - Quarter - Week.Total Years] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.TIMES_DAILY.Times - Quarter - Week.Years] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.TIMES_DAILY.Times - Quarter - Week.Years.0] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.TIMES_DAILY.Times - Quarter - Week.Quarter] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.TIMES_DAILY.Times - Quarter - Week.Quarter.0] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.TIMES_DAILY.Times - Quarter - Week.Month] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.TIMES_DAILY.Times - Quarter - Week.Month.0] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.TIMES_DAILY.Times - Quarter - Week.Week] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.TIMES_DAILY.Times - Quarter - Week.Week.0] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.TIMES_DAILY.Times - Quarter - Week.Day] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.TIMES_DAILY.Times - Quarter - Week.Day.0] |

In total, report authors are offered 6 hierarchies: Locations_Rec, Locations_Send and the four above (counted).

## Values set per user or for the whole system

The expressions, filters and queries of this subject area name 2 variables inside VALUEOF(...). Of these, 1 is defined in the repository and 1 is used by name and not defined in the repository. The Slice holds 1 variable row (counted; no Row identifier).

### Defined in the repository

**BI_YEAR** is a value set once for the whole system (a "repository variable"); whether it is a session variable is not recorded [kb:bi-analytics-2013::var::BI_YEAR]. Its default value is `2010.0d` [kb:bi-analytics-2013::var::BI_YEAR].

It is set by the query "BI_IB" (an init block); whether that query runs per session, and whether it is enabled, are not recorded; its refresh period is 3600 [kb:bi-analytics-2013::var::init.BI_IB]. The query that sets the value, exactly [kb:bi-analytics-2013::var::init.BI_IB]:

```
SELECT MAX(EXTRACT (YEAR from EVENT_DT )) FROM FCT_TRANSACTIONS_DAILY
```

Put mechanically: the query takes the largest year found in the column EVENT_DT of the database table FCT_TRANSACTIONS_DAILY. The unit of the refresh period is not stated in the packet. What value BI_YEAR holds at run time is not recorded (open question Q13).

BI_YEAR is named in one place of this subject area: the query of the database table FCT_TRANSACTIONS_ACTUAL (see the section Database table FCT_TRANSACTIONS_ACTUAL) [kb:bi-analytics-2013::var::BI_YEAR].

### Used by name, not defined in the repository

| Name | Used in | Evidence |
|---|---|---|
| ROLEGUIDS | the content filter of the source "FCT_TRANSACTIONS_ACTUAL" (see the section FCT_TRANSACTIONS_ACTUAL); the content filter of the source "FCT_TRANSACTIONS_DAILY" (see the section TRANSACTIONS_DAILY); the row filter on each of these two sources (see the section Security) | listed in the packet without a Row identifier |

For ROLEGUIDS the Knowledge Base has no variable row, neither with exactly this name nor with one that differs only in letter case. The packet notes that such a name can be a session variable the server sets itself, or one set outside the repository; the repository does not say which. It is named inside VALUEOF(...) in 4 rows of this subject area. What it holds for a user at run time is not recorded (open question Q3).

## Security

This is the security that the repository records for this subject area: 2 row filters, each on one source of a business table. The packet records no permission and no role.

### Row filters

**Row filter on the source "FCT_TRANSACTIONS_ACTUAL"** of the business table FCT_TRANSACTIONS_ACTUAL [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.FCT_TRANSACTIONS_ACTUAL.src.FCT_TRANSACTIONS_ACTUAL#filter]. The filter, exactly as extracted:

```
(
Position (';MT_User_A;' in ';' || valueof(NQ_SESSION.ROLEGUIDS) || ';') > 0 and  "Iryna_Bradzetskaya_MT".""."U_SAL"."FCT_TRANSACTIONS_ACTUAL"."CURRENCY_SUR_ID"   in (12,10,15,11)
)
or
(
Position (';MT_User_F;' in ';' || valueof(NQ_SESSION.ROLEGUIDS) || ';') > 0 and "Iryna_Bradzetskaya_MT".""."U_SAL"."FCT_TRANSACTIONS_ACTUAL"."CURRENCY_SUR_ID"  in (13,14, 16,17)
)
```

Put mechanically: two bracketed conditions joined by "or". The first looks for the text ";MT_User_A;" inside the value `ROLEGUIDS` (framed by ";" on both sides) and requires the database column FCT_TRANSACTIONS_ACTUAL.CURRENCY_SUR_ID to be one of 12, 10, 15, 11; the second looks for ";MT_User_F;" and requires one of 13, 14, 16, 17 [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.FCT_TRANSACTIONS_ACTUAL.src.FCT_TRANSACTIONS_ACTUAL#filter].

**Row filter on the source "FCT_TRANSACTIONS_DAILY"** of the business table TRANSACTIONS_DAILY [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TRANSACTIONS_DAILY.src.FCT_TRANSACTIONS_DAILY#filter]. The filter, exactly as extracted:

```
(
Position (';Role_MT_A;' in ';' || valueof(NQ_SESSION.ROLEGUIDS) || ';') > 0 and  "Iryna_Bradzetskaya_MT".""."U_SAL"."FCT_TRANSACTIONS_DAILY"."CURRENCY_SUR_ID"   in (12,10,15,11)
)
or
(
Position (';Role_MT_F;' in ';' || valueof(NQ_SESSION.ROLEGUIDS) || ';') > 0 and "Iryna_Bradzetskaya_MT".""."U_SAL"."FCT_TRANSACTIONS_DAILY"."CURRENCY_SUR_ID"  in (13,14, 16,17)
)
```

Put mechanically: the same two conditions, with the texts ";Role_MT_A;" and ";Role_MT_F;" and the column FCT_TRANSACTIONS_DAILY.CURRENCY_SUR_ID, and the same two lists of numbers [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TRANSACTIONS_DAILY.src.FCT_TRANSACTIONS_DAILY#filter].

Both filters read `ROLEGUIDS`, a name used and not defined in the repository (see the section Values set per user or for the whole system). No row filter is recorded for any other source, so the business tables CURRENCY, CUSTOMERS_REC, CUSTOMERS_SEND, LOCATIONS_REC, LOCATIONS_SEND, OPERATIONS, TARIFFS and TIMES_DAILY carry none (counted). Which currencies the numbers stand for, and what a user holding none or both of the named texts is meant to see, are not recorded (open question Q3).

### Permissions

No permission on an object of this subject area is recorded in the packet.

### Roles

No role is recorded in the packet: neither "Role_MT_A", "Role_MT_F", "MT_User_A" nor "MT_User_F" appears as a role, with a name in the repository, a guid, a privilege package or a query privilege. Who holds these four, and whether they are roles, groups or user names, is not recorded (open question Q14).

## Reports built on the subject area

The estate has no Web Catalog half, so reports and dashboards were not examined. Calculations, filters and prompts defined inside reports are unknown (from the manifest; no Row identifier).

## What the export did not capture

The figures below come from the errors and unparsed tables of the Knowledge Base and from the manifest; they carry no marker.

### Errors

None: the extraction recorded no error about an object of this subject area (errors table).

### Totals of source content that no table holds

| Kind | Rows | Occurrences in the source |
|---|---|---|
| All kinds together | 107 | 25205 |
| unread_attribute | 82 | 22368 |
| unread_element | 21 | 2160 |
| unread_text | 4 | 677 |

A row is one attribute, element or text of one kind of object; its occurrences are counted over the whole estate, not only this subject area.

### What was not read, per kind of object (unparsed table)

| Kind | Object | Rows | Occurrences | What was not read (occurrences) |
|---|---|---|---|---|
| unread_attribute | Dimension | 7 | 1041 | LogicalKey@mdsid (127); LogicalLevel@isFDUC (84); LogicalLevel@levelConst (84); RefLogicalColumn@logicalColumnRef (268); RefLogicalColumn@refId (396); RefLogicalLevel@logicalLevelRef (5); RefLogicalLevel@refId (77) |
| unread_attribute | Group | 4 | 23 | Group@displayName (9); Group@guid (10); RefGroup@groupRef (2); RefGroup@refId (2) |
| unread_attribute | InitBlock | 10 | 1171 | DBMapItem@name (396); InitBlock@baseTime (50); InitBlock@connectionPoolRef (4); InitBlock@iconIndex (3); InitBlock@isRowWiseCacheEnabled (80); InitBlock@isRowWiseInit (12); RefInitBlock@predecessorRef (44); RefInitBlock@refId (44); RefVariable@refId (269); RefVariable@variableRef (269) |
| unread_attribute | LogicalComplexJoin | 1 | 75 | LogicalComplexJoin@mdsid (75) |
| unread_attribute | LogicalTable | 16 | 6103 | AggrRule@mdsid (22); AttrDefn@mdsid (933); AttrDefn@name (933); Expr@mdsid (968); Expr@name (968); LogicalColumn@customSortOrderRef (6); LogicalColumn@isDerived (13); LogicalKey@mdsid (57); MeasureDefn@isCommutative (22); MeasureDefn@mdsid (22); MeasureDefn@name (22); RefLogicalColumn@refId (57); RefLogicalTableSource@logicalTableSourceRef (75); RefLogicalTableSource@refId (75); RefObject@objectTypeId (965); RefObject@refId (965) |
| unread_attribute | LogicalTableSource | 12 | 8004 | Expr@mdsid (940); Expr@name (940); FragmentContent@mdsid (75); FragmentContent@name (75); GroupBy@mdsid (75); GroupBy@name (75); LogicalColumnExpr@mdsid (940); LogicalColumnExpr@name (940); RefObject@objectTypeId (1897); RefObject@refId (1897); WhereClause@mdsid (75); WhereClause@name (75) |
| unread_attribute | ObjectPrivilege | 3 | 284 | ObjectPrivilege@mdsid (62); RefObject@objectTypeId (111); RefObject@refId (111) |
| unread_attribute | PhysicalTable | 7 | 330 | PhysicalForeignKey@mdsid (82); PhysicalKey@mdsid (63); PhysicalTable@isCacheable (6); PhysicalTable@lastUpdated (10); PhysicalTable@pollFreq (8); PhysicalTable@rowCount (10); RefColumn@refId (151) |
| unread_attribute | PresentationCatalog | 5 | 282 | PresentationCatalog@hasDispDescription (18); PresentationCatalog@hasDispName (18); PresentationCatalog@isDefaultReadable (10); RefPresentationTable@presentationTableRef (118); RefPresentationTable@refId (118) |
| unread_attribute | PresentationHierarchy | 7 | 981 | Alias@name (14); PresentationHierarchy@hasDispDescription (53); PresentationHierarchy@hasDispName (53); PresentationLevel@containerRef (226); PresentationLevel@hasDispDescription (226); PresentationLevel@hasDispName (226); RefPresentationColumn@refId (183) |
| unread_attribute | PresentationTable | 7 | 3316 | Alias@name (6); PresentationColumn@hasDispDescription (1484); PresentationColumn@hasDispName (1484); PresentationTable@hasDispDescription (118); PresentationTable@hasDispName (118); RefPresentationHierarchy@presentationHierarchyRef (53); RefPresentationHierarchy@refId (53) |
| unread_attribute | PrivilegePackage | 1 | 72 | QueryPrivilegeMapping@databaseRef (72) |
| unread_attribute | Variable | 2 | 686 | Expr@mdsid (343); Expr@name (343) |
| unread_element | Dimension | 4 | 347 | RefLogicalColumn (268); RefLogicalColumns (69); RefLogicalLevel (5); RefPreferredDrillDown (5) |
| unread_element | Group | 3 | 16 | Description (12); RefChildGroups (2); RefGroup (2) |
| unread_element | InitBlock | 7 | 1307 | DBMapItem (396); Description (21); RefInitBlock (44); RefPredecessors (42); RefVariable (269); RefVariables (139); Value (396) |
| unread_element | PresentationCatalog | 2 | 136 | RefPresentationTable (118); RefTables (18) |
| unread_element | PresentationHierarchy | 1 | 14 | Alias (14) |
| unread_element | PresentationTable | 3 | 92 | Alias (6); RefHierarchies (33); RefPresentationHierarchy (53) |
| unread_element | Variable | 1 | 248 | Description (248) |
| unread_text | Group | 1 | 12 | Description#text (12) |
| unread_text | InitBlock | 2 | 417 | Description#text (21); Value#text (396) |
| unread_text | Variable | 1 | 248 | Description#text (248) |

Each item above was not read by the extraction.

### Left out of the Slice (manifest)

- Rows of the errors table that are not about an object of this subject area: 0.
- Rows of the unparsed table about whole object types or files with no reader, or about kinds of object the Slice does not hold: 104 (2 redacted_value, 102 unmodelled_type).
  - Kind redacted_value: 1 object type, 2 rows, 2 occurrences: Variable, 2 rows, 2 occurrences.
  - Kind unmodelled_type (a whole type of object that has no reader; nothing of such an object is in the Knowledge Base): 2 object types, 102 rows, 102 objects: Project, 30 rows, 30 objects; QueryPrivilege, 72 rows, 72 objects.
- Rows in the Slice only so that a reference from another row resolves: 0.

## Observations across sections

- The content filter of the source "FCT_TRANSACTIONS_DAILY" reads ROLEGUIDS through valueof(NQ_SESSION.ROLEGUIDS) [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TRANSACTIONS_DAILY.src.FCT_TRANSACTIONS_DAILY]; ROLEGUIDS is used by name and not defined in the repository (section Values set per user or for the whole system; no Row identifier).
- The row filter on the source "FCT_TRANSACTIONS_DAILY" looks for the texts ";Role_MT_A;" and ";Role_MT_F;" [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TRANSACTIONS_DAILY.src.FCT_TRANSACTIONS_DAILY#filter]; the row filter on the source "FCT_TRANSACTIONS_ACTUAL" looks for ";MT_User_A;" and ";MT_User_F;", with the same two lists of CURRENCY_SUR_ID values [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.FCT_TRANSACTIONS_ACTUAL.src.FCT_TRANSACTIONS_ACTUAL#filter]; no role of any of these four names is recorded (section Security; no Row identifier).
- The content filter of the source "FCT_TRANSACTIONS_DAILY" has the same text as the row filter on that source: the content filter [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TRANSACTIONS_DAILY.src.FCT_TRANSACTIONS_DAILY]; the row filter [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TRANSACTIONS_DAILY.src.FCT_TRANSACTIONS_DAILY#filter].
- The content filter of the source "FCT_TRANSACTIONS_ACTUAL" has the same text as the row filter on that source: the content filter [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.FCT_TRANSACTIONS_ACTUAL.src.FCT_TRANSACTIONS_ACTUAL]; the row filter [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.FCT_TRANSACTIONS_ACTUAL.src.FCT_TRANSACTIONS_ACTUAL#filter].
- The content filter of the source "FCT_TRANSACTIONS_DAILY" limits CURRENCY_SUR_ID to 12, 10, 15, 11 or 13, 14, 16, 17 [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TRANSACTIONS_DAILY.src.FCT_TRANSACTIONS_DAILY]; CURRENCY_SUR_ID is the key of the business table CURRENCY, mapped from DIM_CURRENCY_SCD.CURRENCY_SUR_ID [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.CURRENCY.src.DIM_CURRENCY_SCD#map3].
- AMOUNT_PAYMENT of FCT_TRANSACTIONS_ACTUAL is recorded as not a measure [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.FCT_TRANSACTIONS_ACTUAL.AMOUNT_PAYMENT]; AMOUNT_PAYMENT of TRANSACTIONS_DAILY is a measure summarised with SUM [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TRANSACTIONS_DAILY.AMOUNT_PAYMENT].
- The query of the database table FCT_TRANSACTIONS_ACTUAL reads every column of FCT_TRANSACTIONS_DAILY for the year VALUEOF(BI_YEAR) [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.FCT_TRANSACTIONS_ACTUAL.def.DefaultMulDB]; BI_YEAR is a repository variable with the default 2010.0d [kb:bi-analytics-2013::var::BI_YEAR].
- The database table FCT_TRANSACTIONS_DAILY lists PERIOD_ID, TA_COUNTRY_REC_ID and TA_COUNTRY_SEND_ID as columns not mapped by any field [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.FCT_TRANSACTIONS_DAILY.PERIOD_ID]; the query FCT_TRANSACTIONS_ACTUAL, which reads every column of FCT_TRANSACTIONS_DAILY, has all 14 of its columns mapped, these three among them [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.FCT_TRANSACTIONS_ACTUAL.def.DefaultMulDB].
- The business tables CUSTOMERS_REC and CUSTOMERS_SEND both have a key named PK_DIM_CUSTOMERS on CUST_ID: CUSTOMERS_REC [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.CUSTOMERS_REC.key.PK_DIM_CUSTOMERS]; CUSTOMERS_SEND [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.CUSTOMERS_SEND.key.PK_DIM_CUSTOMERS].
- The queries DIM_CUSTOMERS_REC and DIM_CUSTOMERS_SEND have the same text, SELECT * FROM DIM_CUSTOMERS: DIM_CUSTOMERS_REC [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_CUSTOMERS_REC.def.DefaultMulDB]; DIM_CUSTOMERS_SEND [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_CUSTOMERS_SEND.def.DefaultMulDB].
- The queries DIM_LOCATIONS_REC and DIM_LOCATIONS_SEND have the same text, SELECT * FROM DIM_LOCATIONS_SCD: DIM_LOCATIONS_REC [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_LOCATIONS_REC.def.DefaultMulDB]; DIM_LOCATIONS_SEND [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_LOCATIONS_SEND.def.DefaultMulDB].
- The key of the business table LOCATIONS_REC is PC_LOC on GEO_ID [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.LOCATIONS_REC.key.PC_LOC]; inside the source "FCT_TRANSACTIONS_DAILY" the transactions are joined to DIM_LOCATIONS_REC on GEO_REC_SUR_ID = GEO_ID, with join type LeftOuter [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TRANSACTIONS_DAILY.src.FCT_TRANSACTIONS_DAILY#table2.join0].
- The business-model join of TRANSACTIONS_DAILY to TIMES_DAILY is Inner [kb:bi-analytics-2013::bmm::joins.Iryna_Bradzetskaya_MT#1:Relationship_2004:4041821380622953]; inside the source "FCT_TRANSACTIONS_DAILY", three of the four joins are Inner and the join to DIM_LOCATIONS_REC is LeftOuter [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TRANSACTIONS_DAILY.src.FCT_TRANSACTIONS_DAILY#table2.join0].
- The query DIM_OPERATION_METHODS reads the columns OPERATION_METHOD_NAME, OPERATION_METHOD_TYPE and OPERATION_METHOD_TYPE_ID from DIM_OPERATIONS [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_OPERATION_METHODS.def.DefaultMulDB]; the repository lists 5 columns for the database table DIM_OPERATIONS, none of them these three [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_OPERATIONS].
- The field TIME_ID of TIMES_DAILY is mapped from a DATETIME column [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TIMES_DAILY.src.DIM_TIMES_DAILY#map17]; the foreign keys FK_TRANS_TIMES of both transaction tables point from EVENT_DT at TIME_ID [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_TIMES_DAILY.key.DIM_TIMES_DAILY_PK].

## Open questions

These are the points the export could not settle. Each says how to find the answer and ends with an empty answer line. Write the answer on that line, or tell the agent; the document will then be updated.

### Q1. What do the measures AMOUNT_PAYMENT and COUNT_PAYMENT of TRANSACTIONS_DAILY hold for one row, and is it right to add COUNT_PAYMENT up?
Raised in: table-01-transactions-daily-p1
Why it matters: both measures are summarised with SUM by default and both are defined on the database columns AMOUNT_PAYMENT and COUNT_PAYMENT of FCT_TRANSACTIONS_DAILY. The export records no description of either column and nothing about the currency of the amount or what is counted, so a reader cannot tell what a total means.
Assumed meanwhile: nothing. Both measures are described with their rules exactly as recorded, and no meaning is stated.
How to get the answer: run these queries against the schema U_SAL and bring back the results: select count(*), sum(COUNT_PAYMENT), sum(AMOUNT_PAYMENT) from FCT_TRANSACTIONS_DAILY, and select * from FCT_TRANSACTIONS_DAILY where rownum <= 20. Bring back also any description of the two columns kept in the database or the load process that fills the table, and say whether one row of FCT_TRANSACTIONS_DAILY is one day per combination of the other columns.
Answer:

### Q2. What does the field PAYMENT_WHIS_TAX stand for, and why is AMOUNT_PAYMENT multiplied by 20?
Raised in: table-01-transactions-daily-p1
Why it matters: PAYMENT_WHIS_TAX is calculated as the measure AMOUNT_PAYMENT multiplied by the fixed number 20, is not itself a measure, and is mapped in no source. Where the number 20 comes from, whether it is a tax rate, a factor or a percentage, and whether the name means "with tax", are not recorded. A reader may take the field for an amount including tax when the formula gives twenty times the amount.
Assumed meanwhile: nothing. The formula is quoted exactly and no meaning is given to the number or the name.
How to get the answer: in the OBIEE Administration Tool, open the business table TRANSACTIONS_DAILY of the business model "Iryna_Bradzetskaya_MT_Row-level_Security", open the field PAYMENT_WHIS_TAX and bring back its description, if any, and its formula as shown. Bring back the rule or document the number 20 was taken from, if there is one, and say whether the intended result is AMOUNT_PAYMENT plus 20 percent or AMOUNT_PAYMENT times 20.
Answer:

### Q3. Which currencies do the numbers in the content filter of the source FCT_TRANSACTIONS_DAILY stand for, and what is a user who holds neither Role_MT_A nor Role_MT_F, or both, meant to see?
Raised in: table-01-transactions-daily-p2
Why it matters: the only source of TRANSACTIONS_DAILY carries a content filter of two conditions joined by "or": one pairs the text ";Role_MT_A;" in the value ROLEGUIDS with CURRENCY_SUR_ID in (12,10,15,11), the other pairs ";Role_MT_F;" with CURRENCY_SUR_ID in (13,14, 16,17). The export records which numbers are listed, not which currencies they are, and does not record whether ROLEGUIDS holds role names or role guids, or what the filter is meant to give a user who has neither role or both. Anyone rebuilding the access rule needs the intended result for each kind of user.
Assumed meanwhile: nothing. The filter is quoted exactly and no effect at run time is stated.
How to get the answer: run this query against the schema U_SAL and bring back the result: select * from DIM_CURRENCY_SCD where CURRENCY_ID in (10,11,12,13,14,15,16,17), or, if the key column has another name, select the rows of DIM_CURRENCY_SCD whose key holds these eight numbers. Then log in to OBIEE as a user who holds Role_MT_A only, as one who holds Role_MT_F only, and as one who holds neither, run where OBIEE lets you issue logical SQL: select VALUEOF(NQ_SESSION.ROLEGUIDS) from "Iryna_Bradzetskaya_MT_Row-level_Security", and a count of TRANSACTIONS_DAILY rows per CURRENCY_SUR_ID; bring back the three results, and say which result each kind of user is meant to get.
See also: table-02-currency, the key column of DIM_CURRENCY_SCD is CURRENCY_SUR_ID, so the first query is select * from DIM_CURRENCY_SCD where CURRENCY_SUR_ID in (10,11,12,13,14,15,16,17).
See also: table-05-fct-transactions-actual-p2, the source FCT_TRANSACTIONS_ACTUAL has the same filter but looks for ";MT_User_A;" and ";MT_User_F;" instead of ";Role_MT_A;" and ";Role_MT_F;"; say also which of these four names a user's ROLEGUIDS can hold.
Answer:

### Q4. What does CURRENCY_TO_DOLLAR hold, and can one currency have several rows in DIM_CURRENCY_SCD with different VALID_FROM and VALID_TO dates?
Raised in: table-02-currency
Why it matters: the business table CURRENCY offers CURRENCY_TO_DOLLAR (type DOUBLE) as a plain field, not a measure, and VALID_FROM and VALID_TO (type DATETIME); its key is CURRENCY_SUR_ID alone. The export does not say whether CURRENCY_TO_DOLLAR is an exchange rate and in which direction, nor which row of a currency applies on a given date. Whoever rebuilds currency conversion or the link from transactions to currency needs both.
Assumed meanwhile: nothing. The fields are described as recorded and no meaning is given to them.
How to get the answer: run these queries against the schema U_SAL and bring back the results: select CURRENCY_CODE, count(*), min(VALID_FROM), max(VALID_TO) from DIM_CURRENCY_SCD group by CURRENCY_CODE, and select * from DIM_CURRENCY_SCD order by CURRENCY_CODE, VALID_FROM. Bring back also the description of the column CURRENCY_TO_DOLLAR from the load process that fills the table, if one exists.
Answer:

### Q5. What do the endings "_REC" and "_SEND" in the names of the customer and location tables stand for?
Raised in: table-03-customers-rec-p1
Why it matters: the business table CUSTOMERS_REC, built on the database table DIM_CUSTOMERS_REC, has the key CUST_ID. The subject area also offers a table CUSTOMERS_SEND. The export records no description that says which party of a transaction each one is, so a report author cannot tell which customer a figure is broken down by.
Assumed meanwhile: nothing. The names are used as recorded and no role in a transaction is given to either table.
How to get the answer: in the OBIEE Administration Tool, open the business tables CUSTOMERS_REC and CUSTOMERS_SEND of the business model "Iryna_Bradzetskaya_MT_Row-level_Security" and bring back their descriptions, if any. Bring back also the description of the columns CUS_REC_ID and CUST_SEND_ID of the database table FCT_TRANSACTIONS_DAILY from the load process that fills it, and say which party of a transaction each column names.
See also: dbtable-03-dim-customers-send, both DIM_CUSTOMERS_REC and DIM_CUSTOMERS_SEND are queries with the same text, SELECT * FROM DIM_CUSTOMERS; say whether one customer table is meant to play two roles.
Answer:

### Q6. Should AMOUNT_PAYMENT and COUNT_PAYMENT of FCT_TRANSACTIONS_ACTUAL be totals that add up, and what do PERIOD_ID, TA_COUNTRY_REC_ID and TA_COUNTRY_SEND_ID hold?
Raised in: table-05-fct-transactions-actual-p1
Why it matters: the business table FCT_TRANSACTIONS_ACTUAL has 14 fields and no measures; AMOUNT_PAYMENT and COUNT_PAYMENT are recorded as "not a measure", with no rule for how they are summarised, and no key is declared for the table. In OBIEE generally, a field that is not a measure is shown row by row and is not added up by the query. The export does not say whether that is intended, nor what the three fields PERIOD_ID, TA_COUNTRY_REC_ID and TA_COUNTRY_SEND_ID hold.
Assumed meanwhile: nothing. The fields are described as recorded: plain fields, each defined on the column of the same name of FCT_TRANSACTIONS_ACTUAL.
How to get the answer: in the OBIEE Administration Tool, open the business table FCT_TRANSACTIONS_ACTUAL of the business model "Iryna_Bradzetskaya_MT_Row-level_Security" and bring back, for AMOUNT_PAYMENT and COUNT_PAYMENT, the aggregation rule shown, if any, and any description of the table. Run against the schema U_SAL and bring back: select PERIOD_ID, TA_COUNTRY_REC_ID, TA_COUNTRY_SEND_ID, count(*) from FCT_TRANSACTIONS_ACTUAL group by PERIOD_ID, TA_COUNTRY_REC_ID, TA_COUNTRY_SEND_ID, and say what each of the three columns names (a period, a country, something else) and what one row of the table stands for.
See also: dbtable-10-fct-transactions-actual, the table is a query SELECT * FROM FCT_TRANSACTIONS_DAILY limited to the year VALUEOF(BI_YEAR); say whether the database table FCT_TRANSACTIONS_DAILY has the columns PERIOD_ID, TA_COUNTRY_REC_ID and TA_COUNTRY_SEND_ID.
Answer:

### Q7. In the location tables, which column identifies one row (GEO_ID or GEO_SURR_ID), what does LEVEL_CODE hold, and can one location have several rows by VALID_FROM and VALID_TO?
Raised in: table-06-locations-rec-p1
Why it matters: the business table LOCATIONS_REC has the key PC_LOC on GEO_ID, and also offers GEO_SURR_ID, LEVEL_CODE, VALID_FROM and VALID_TO, together with country, region and "part" columns. The export does not say whether a row is a country, a region or a part depending on LEVEL_CODE, nor whether GEO_ID stays the same across versions of a location. Whoever rebuilds the link from transactions to locations needs to know which column to join on and whether dates must be compared.
Assumed meanwhile: nothing. The key is stated as recorded (GEO_ID) and no meaning is given to the other columns.
How to get the answer: run these queries against the schema U_SAL and bring back the results: select LEVEL_CODE, count(*), count(distinct GEO_ID), count(distinct GEO_SURR_ID) from DIM_LOCATIONS_REC group by LEVEL_CODE, and select * from DIM_LOCATIONS_REC where rownum <= 20. If DIM_LOCATIONS_REC is a query kept in the repository and cannot be selected from directly, run the query given in the section Database table DIM_LOCATIONS_REC instead.
Answer:

### Q8. What do TARIFF_MIN_PAYMENT, TARIFF_MAX_PAYMENT and TARIFF_PAYMENT_SUM of TARIFFS hold, and are they meant to be used with the amount of a transaction?
Raised in: table-09-tariffs
Why it matters: the business table TARIFFS offers the three as plain fields, not measures, each mapped from the column of the same name of DIM_TARIFFS (type DOUBLE). The export records no description of them and no formula that uses them, so it cannot be said whether they are limits, a fixed fee or a percentage, or whether any report figure is meant to be computed from them.
Assumed meanwhile: nothing. The fields are described as recorded and no meaning is given to them.
How to get the answer: run against the schema U_SAL and bring back: select * from DIM_TARIFFS. Bring back also any description of these three columns from the load process that fills DIM_TARIFFS, and say whether a payment's fee is worked out from them and how.
Answer:

### Q9. Does the database table DIM_OPERATIONS hold the columns OPERATION_METHOD_NAME, OPERATION_METHOD_TYPE and OPERATION_METHOD_TYPE_ID, and is OPERATION_METHOD_ID unique in the result of the query DIM_OPERATION_METHODS?
Raised in: dbtable-07-dim-operation-methods
Why it matters: DIM_OPERATION_METHODS is a query kept in the repository that selects the distinct combinations of OPERATION_METHOD_ID, OPERATION_METHOD_NAME, OPERATION_METHOD_TYPE and OPERATION_METHOD_TYPE_ID from DIM_OPERATIONS where OPERATION_METHOD_ID is not null. Its key PK_OP_M is OPERATION_METHOD_ID alone. If one method id appears with two names or types, the query returns two rows for it, and the join from operations to methods then pairs an operation with more than one method row.
Assumed meanwhile: nothing. The query is quoted exactly and the key is stated as recorded.
How to get the answer: run against the schema U_SAL and bring back the result: select OPERATION_METHOD_ID, count(*) from (SELECT DISTINCT OPERATION_METHOD_ID, OPERATION_METHOD_NAME, OPERATION_METHOD_TYPE, OPERATION_METHOD_TYPE_ID FROM DIM_OPERATIONS WHERE OPERATION_METHOD_ID IS NOT NULL) group by OPERATION_METHOD_ID having count(*) > 1. Bring back also the column list of the database table DIM_OPERATIONS as the database describes it.
Answer:

### Q10. What are YEAR_Q, YEAR_M and YEAR_W of DIM_TIMES_DAILY meant to identify, and what does CALENDAR_WEEK_NUMBER hold?
Raised in: dbtable-09-dim-times-daily
Why it matters: the query DIM_TIMES_DAILY builds YEAR_Q, YEAR_M and YEAR_W by joining the texts CALENDAR_YEAR, CALENDAR_QUARTER_NUMBER, CALENDAR_MONTH_NUMBER and CALENDAR_WEEK_NUMBER end to end and turning the result into a number. CALENDAR_WEEK_NUMBER is recorded as VARCHAR with precision 1, and CALENDAR_MONTH_NUMBER as VARCHAR with precision 2. Whether the month number carries a leading zero, and whether the week number is a week of the month or of the year, decide whether these numbers sort in time order and whether two periods can get the same number; the export does not say.
Assumed meanwhile: nothing. The query is quoted exactly; no meaning is given to the three numbers.
How to get the answer: run against the schema U_SAL and bring back the result: select CALENDAR_YEAR, CALENDAR_QUARTER_NUMBER, CALENDAR_MONTH_NUMBER, CALENDAR_WEEK_NUMBER, count(*) from DIM_TIMES_DAILY group by CALENDAR_YEAR, CALENDAR_QUARTER_NUMBER, CALENDAR_MONTH_NUMBER, CALENDAR_WEEK_NUMBER order by 1, 2, 3, 4. Say also which period YEAR_W is meant to stand for in reports.
Answer:

### Q11. Should FCT_TRANSACTIONS_ACTUAL be reportable by customer and by location, and is the absence of those business-model joins intended?
Raised in: joins
Why it matters: in the business model, TRANSACTIONS_DAILY is joined to all eight descriptive tables, but FCT_TRANSACTIONS_ACTUAL only to TIMES_DAILY, CURRENCY, OPERATIONS and TARIFFS. In the database, foreign keys from FCT_TRANSACTIONS_ACTUAL to DIM_CUSTOMERS_REC, DIM_CUSTOMERS_SEND, DIM_LOCATIONS_REC and DIM_LOCATIONS_SEND are recorded. As recorded, the business model offers no path from FCT_TRANSACTIONS_ACTUAL to the customer and location tables; whether that is a choice or an omission is not recorded.
Assumed meanwhile: nothing. The joins are listed exactly as recorded.
How to get the answer: in the OBIEE Administration Tool, open the business model diagram of "Iryna_Bradzetskaya_MT_Row-level_Security" and bring back the list of joins of FCT_TRANSACTIONS_ACTUAL. Then, as a report author, build an analysis with FCT_TRANSACTIONS_ACTUAL.AMOUNT_PAYMENT and CUSTOMERS_REC.CUST_ID and bring back what OBIEE returns or the error it shows. Say whether such reports are needed.
Answer:

### Q12. Are the levels Quarter, Month and Week of the drill path "Times" meant to be identified by their number alone, or by the number together with the year?
Raised in: hierarchies-p2
Why it matters: the primary key of level Quarter is CALENDAR_QUARTER_NUMBER, of Month is CALENDAR_MONTH_NUMBER, and of Week is CALENDAR_WEEK_NUMBER; the keys YEAR_Q, YEAR_M and YEAR_W, built with the year, are recorded only as further keys (YEAR_Q and YEAR_M as chronological). The member counts are recorded as 12 years, 48 quarters, 144 months, 650 weeks and 4400 days. Whether a drill from one year to a quarter is meant to show that year's quarter only, and how weeks and days are numbered, decides how totals are grouped.
Assumed meanwhile: nothing. Keys and member counts are stated as recorded.
How to get the answer: in the OBIEE Administration Tool, open the drill path "Times" of the business model "Iryna_Bradzetskaya_MT_Row-level_Security" and bring back, for each of the levels Quarter, Month and Week, which key is marked as the primary key and which is used for display. Then, as a report author, drill in the hierarchy "Times - Quarter - Week" from one year down to Week and bring back the rows shown. See also Q10 for what YEAR_W holds.
Answer:

### Q13. What value does BI_YEAR hold at run time, and is FCT_TRANSACTIONS_ACTUAL meant to show only the latest year of FCT_TRANSACTIONS_DAILY?
Raised in: variables
Why it matters: BI_YEAR is a repository variable with the default `2010.0d`, set by the init block "BI_IB" with the query SELECT MAX(EXTRACT (YEAR from EVENT_DT )) FROM FCT_TRANSACTIONS_DAILY and a refresh period of 3600. The database table FCT_TRANSACTIONS_ACTUAL is a query that keeps the rows of FCT_TRANSACTIONS_DAILY whose year of EVENT_DT equals BI_YEAR. Whether the init block is enabled is not recorded, so it cannot be said whether the value is the default or the result of the query.
Assumed meanwhile: nothing. The default, the query and the refresh period are quoted as recorded; no run-time value is stated.
How to get the answer: log in to OBIEE and, where OBIEE lets you issue logical SQL, run select VALUEOF(BI_YEAR) from "Iryna_Bradzetskaya_MT_Row-level_Security" and bring back the value. In the OBIEE Administration Tool, open the init block "BI_IB" and bring back whether it is enabled and the unit of its refresh period. Run against the schema U_SAL: SELECT MAX(EXTRACT (YEAR from EVENT_DT )) FROM FCT_TRANSACTIONS_DAILY, and bring back the result. Say whether "actual" is meant to mean the latest year.
Answer:

### Q14. What are "Role_MT_A", "Role_MT_F", "MT_User_A" and "MT_User_F", and which users hold each of them?
Raised in: security
Why it matters: the two row filters, and the content filters of the same two sources, decide which rows of TRANSACTIONS_DAILY and FCT_TRANSACTIONS_ACTUAL a user sees by looking for these four texts in ROLEGUIDS. The Slice records no role, permission or privilege package for any of them, and ROLEGUIDS is not defined in the repository. Anyone rebuilding the access rule needs the list of users behind each name, and needs to know why one table uses "Role_MT_" names and the other "MT_User_" names.
Assumed meanwhile: nothing. The filters are quoted exactly; no user or group is named as holding any of the four.
How to get the answer: in the OBIEE Administration Tool, open the identity manager and bring back, for each of the four names, whether it exists as an application role, a group or a user, and its members. Then log in as one member of each and, where OBIEE lets you issue logical SQL, run select VALUEOF(NQ_SESSION.ROLEGUIDS) from "Iryna_Bradzetskaya_MT_Row-level_Security" and bring back the four values.
See also: gaps, the display names, guids and descriptions of groups were not read, and the 72 query privileges belong to a type of object the extraction has no reader for.
Answer:

## Answered questions

Questions that have been answered. Statements that rest on an answer carry the question's number in their marker.

## Not described

Rows of the Knowledge Base that this document deliberately leaves out, each with the reason.

Nothing was left out: every required row is described above.

## Questions the export answered

Questions raised while one section was written and settled by a later section. Their numbers are not used again.

None.
