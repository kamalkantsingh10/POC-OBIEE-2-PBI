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
