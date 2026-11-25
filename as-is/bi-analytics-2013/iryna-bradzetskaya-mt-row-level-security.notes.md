# Notes for the summary
## overview: What report authors see
- Subject area "Iryna_Bradzetskaya_MT_Row-level_Security" on the business model of the same name; no implicit fact column recorded; auto-aggregate true [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security].
- 10 tables report authors see, each with one business table of the same name: CURRENCY 9, CUSTOMERS_REC 12, CUSTOMERS_SEND 12, FCT_TRANSACTIONS_ACTUAL 14, LOCATIONS_REC 16, LOCATIONS_SEND 16, OPERATIONS 8, TARIFFS 9, TIMES_DAILY 22, TRANSACTIONS_DAILY 11 fields (no Row identifier for the counts). Counts: 129 fields, 2 measures; 10 sources; 11 database tables, 133 columns, 9 keys; 12 business joins, 17 database joins; 3 drill paths, 6 hierarchies; 1 variable; 2 security rows; 0 reports; 0 errors; 107 kinds of content not read.
- KB extracted 2026-11-23T20:54:06Z, extractor 0.1.0; only the rpd half extracted; no Web Catalog half, so reports and dashboards were not examined (manifest, no Row identifier).
- Estate-wide not in slice: 0 permissions without target, 72 role privileges, 151 init blocks, 342 variables.
- Questions: none
## table-01-transactions-daily-p1: TRANSACTIONS_DAILY
- TRANSACTIONS_DAILY: 11 fields, 2 measures (AMOUNT_PAYMENT, COUNT_PAYMENT, both SUM by default), all shown; 1 source "FCT_TRANSACTIONS_DAILY" [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TRANSACTIONS_DAILY]. No key declared (no Row identifier).
- PAYMENT_WHIS_TAX is AMOUNT_PAYMENT * 20, not a measure, mapped in 0 of 1 sources [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TRANSACTIONS_DAILY.PAYMENT_WHIS_TAX].
- Observation: TRANSACTIONS_DAILY fields CURRENCY_SUR_ID, CUST_SEND_ID, CUS_REC_ID, GEO_REC_SUR_ID, GEO_SEND_SUR_ID, OPERATION_ID, TARIFF_ID, EVENT_DT are defined on columns of FCT_TRANSACTIONS_DAILY [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TRANSACTIONS_DAILY.CUST_SEND_ID]; to set beside the business joins of TRANSACTIONS_DAILY in the joins unit.
- Point to note: PAYMENT_WHIS_TAX is calculated from the measure AMOUNT_PAYMENT multiplied by 20 and is not itself a measure [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TRANSACTIONS_DAILY.PAYMENT_WHIS_TAX].
- Point to note: no key is declared for TRANSACTIONS_DAILY (no Row identifier).
- To check in a later part: what each field is mapped from in the source FCT_TRANSACTIONS_DAILY ("stated with each source").
- Questions: Q1 what AMOUNT_PAYMENT and COUNT_PAYMENT hold per row and whether COUNT_PAYMENT may be summed; Q2 what PAYMENT_WHIS_TAX stands for and why times 20
## table-01-transactions-daily-p2: Where the data comes from (TRANSACTIONS_DAILY, last part)
- Source "FCT_TRANSACTIONS_DAILY": active, priority group not recorded, no level of detail; content filter: Role_MT_A in ROLEGUIDS and CURRENCY_SUR_ID in (12,10,15,11), or Role_MT_F and CURRENCY_SUR_ID in (13,14, 16,17) [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TRANSACTIONS_DAILY.src.FCT_TRANSACTIONS_DAILY]. A row filter also restricts it (unit security).
- Reads 5 tables: FCT_TRANSACTIONS_DAILY (type none) and DIM_CUSTOMERS_REC, DIM_CUSTOMERS_SEND, DIM_LOCATIONS_REC, DIM_LOCATIONS_SEND (type select, no field mapped); 4 joins, inner except FK_TRANS_LOC_REC LeftOuter/RightOuter [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TRANSACTIONS_DAILY.src.FCT_TRANSACTIONS_DAILY#table2.join0].
- Observation: the content filter of source FCT_TRANSACTIONS_DAILY names ROLEGUIDS inside valueof(NQ_SESSION.ROLEGUIDS) [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TRANSACTIONS_DAILY.src.FCT_TRANSACTIONS_DAILY]; to set beside the variables unit (whether the repository defines ROLEGUIDS).
- Observation: the content filter names roles Role_MT_A and Role_MT_F [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TRANSACTIONS_DAILY.src.FCT_TRANSACTIONS_DAILY]; to set beside the roles and row filter of the security unit.
- Observation: the content filter restricts CURRENCY_SUR_ID to 12,10,15,11 and 13,14,16,17 [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TRANSACTIONS_DAILY.src.FCT_TRANSACTIONS_DAILY]; to set beside the source of CURRENCY and the table DIM_CURRENCY_SCD.
- Questions: Q3 which currencies the content filter's numbers are and what a user with neither/both of Role_MT_A, Role_MT_F should see
## table-02-currency: CURRENCY
- CURRENCY: 9 fields, no measures, all shown; 1 source "DIM_CURRENCY_SCD" reading the one table DIM_CURRENCY_SCD, no content filter, no join [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.CURRENCY].
- Key PK_DIM_CURRENCY_SCD on CURRENCY_SUR_ID, primary true [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.CURRENCY.key.PK_DIM_CURRENCY_SCD]; fields VALID_FROM, VALID_TO, CURRENCY_TO_DOLLAR (DOUBLE) are plain fields.
- Observation: CURRENCY's field CURRENCY_SUR_ID is mapped from DIM_CURRENCY_SCD.CURRENCY_SUR_ID [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.CURRENCY.src.DIM_CURRENCY_SCD#map3]; to set beside the content filter of source FCT_TRANSACTIONS_DAILY, which lists CURRENCY_SUR_ID values per role.
- Questions: Q4 what CURRENCY_TO_DOLLAR holds and whether a currency has several rows by VALID_FROM/VALID_TO
## table-03-customers-rec-p1: CUSTOMERS_REC
- CUSTOMERS_REC: 12 fields, no measures, all shown; 1 source "DIM_CUSTOMERS_REC" [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.CUSTOMERS_REC]. Key PK_DIM_CUSTOMERS on CUST_ID, primary true [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.CUSTOMERS_REC.key.PK_DIM_CUSTOMERS].
- Fields include CUST_PASS_NUMBER, CUST_EMAIL, CUST_BALANCE (plain field), CUST_GEO_ID, all shown to report authors [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.CUSTOMERS_REC.CUST_PASS_NUMBER].
- To check in a later part: what each field is mapped from in the source DIM_CUSTOMERS_REC, and whether it has a content filter.
- Questions: Q5 what _REC and _SEND stand for (customer and location tables)
## table-03-customers-rec-p2: Where the data comes from (CUSTOMERS_REC, last part)
- Source "DIM_CUSTOMERS_REC": active, priority group not recorded, no level of detail, no content filter; reads the one table DIM_CUSTOMERS_REC, type select (a query in the repository) [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.CUSTOMERS_REC.src.DIM_CUSTOMERS_REC#table0]. 12 mappings, each one column of the same name.
- CUST_YEAR_OF_BIRTH mapped from a DATETIME column [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.CUSTOMERS_REC.src.DIM_CUSTOMERS_REC#map9].
- Questions: none
## table-04-customers-send-p1: CUSTOMERS_SEND
- CUSTOMERS_SEND: 12 fields, no measures, all shown; the same 12 field names as CUSTOMERS_REC; 1 source "DIM_CUSTOMERS_SEND" [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.CUSTOMERS_SEND].
- Observation: the key of CUSTOMERS_SEND is named PK_DIM_CUSTOMERS, on CUST_ID [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.CUSTOMERS_SEND.key.PK_DIM_CUSTOMERS]; to set beside the key of CUSTOMERS_REC, which has the same name [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.CUSTOMERS_REC.key.PK_DIM_CUSTOMERS].
- To check in a later part: what each field is mapped from in the source DIM_CUSTOMERS_SEND, and whether it has a content filter.
- Questions: none new (Q5 covers _SEND)
## table-04-customers-send-p2: Where the data comes from (CUSTOMERS_SEND, last part)
- Source "DIM_CUSTOMERS_SEND": active, priority group not recorded, no level of detail, no content filter; reads the one table DIM_CUSTOMERS_SEND, type select [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.CUSTOMERS_SEND.src.DIM_CUSTOMERS_SEND#table0]. 12 mappings, each one column of the same name, same types as CUSTOMERS_REC.
- Questions: none
## table-05-fct-transactions-actual-p1: FCT_TRANSACTIONS_ACTUAL
- FCT_TRANSACTIONS_ACTUAL: 14 fields, no measures, all shown; no key declared (no Row identifier); 1 source "FCT_TRANSACTIONS_ACTUAL" [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.FCT_TRANSACTIONS_ACTUAL].
- Observation: AMOUNT_PAYMENT of FCT_TRANSACTIONS_ACTUAL is "not a measure" [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.FCT_TRANSACTIONS_ACTUAL.AMOUNT_PAYMENT]; to set beside AMOUNT_PAYMENT of TRANSACTIONS_DAILY, a measure summed with SUM [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TRANSACTIONS_DAILY.AMOUNT_PAYMENT].
- Fields beyond those of TRANSACTIONS_DAILY: PERIOD_ID, INSERT_DT, TA_COUNTRY_REC_ID, TA_COUNTRY_SEND_ID [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.FCT_TRANSACTIONS_ACTUAL.PERIOD_ID].
- Point to note: no measures; AMOUNT_PAYMENT and COUNT_PAYMENT are plain fields [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.FCT_TRANSACTIONS_ACTUAL.AMOUNT_PAYMENT].
- To check in a later part: mappings and content filter of source FCT_TRANSACTIONS_ACTUAL.
- Questions: Q6 whether AMOUNT_PAYMENT/COUNT_PAYMENT of FCT_TRANSACTIONS_ACTUAL should add up, and what PERIOD_ID, TA_COUNTRY_REC_ID, TA_COUNTRY_SEND_ID hold
## table-05-fct-transactions-actual-p2: Where the data comes from (FCT_TRANSACTIONS_ACTUAL, last part)
- Source "FCT_TRANSACTIONS_ACTUAL": active, priority group not recorded, no level of detail; reads one table FCT_TRANSACTIONS_ACTUAL, type select [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.FCT_TRANSACTIONS_ACTUAL.src.FCT_TRANSACTIONS_ACTUAL#table0]; 14 mappings, one column each. A row filter also restricts it (unit security).
- Observation: the content filter of source FCT_TRANSACTIONS_ACTUAL looks for ';MT_User_A;' and ';MT_User_F;' in ROLEGUIDS, with the same CURRENCY_SUR_ID lists [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.FCT_TRANSACTIONS_ACTUAL.src.FCT_TRANSACTIONS_ACTUAL]; to set beside the content filter of source FCT_TRANSACTIONS_DAILY, which looks for ';Role_MT_A;' and ';Role_MT_F;' [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TRANSACTIONS_DAILY.src.FCT_TRANSACTIONS_DAILY], and beside the role names in the security unit.
- Questions: none new; See also added to Q3 (MT_User_A/MT_User_F)
## table-06-locations-rec-p1: LOCATIONS_REC
- LOCATIONS_REC: 16 fields, no measures, all shown; 1 source "DIM_LOCATIONS_REC" [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.LOCATIONS_REC]. Fields: country codes A2/A3, COUNTRY_ID, GEO_COUNTRY_*, GEO_PART_*, GEO_REGION_*, GEO_ID, GEO_SURR_ID, LEVEL_CODE, VALID_FROM, VALID_TO.
- Observation: the key of LOCATIONS_REC is PC_LOC on GEO_ID [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.LOCATIONS_REC.key.PC_LOC]; to set beside the join in source FCT_TRANSACTIONS_DAILY from FCT_TRANSACTIONS_DAILY.GEO_REC_SUR_ID to DIM_LOCATIONS_REC.GEO_ID [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TRANSACTIONS_DAILY.src.FCT_TRANSACTIONS_DAILY#table2.join0], while the table also has a column GEO_SURR_ID.
- To check in a later part: mappings and content filter of source DIM_LOCATIONS_REC.
- Questions: Q7 which of GEO_ID/GEO_SURR_ID identifies a row, what LEVEL_CODE holds, whether versions by VALID_FROM/VALID_TO
## table-06-locations-rec-p2: Where the data comes from (LOCATIONS_REC, last part)
- Source "DIM_LOCATIONS_REC": active, priority group not recorded, no level of detail, no content filter; reads one table DIM_LOCATIONS_REC, type select [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.LOCATIONS_REC.src.DIM_LOCATIONS_REC#table0]; 16 mappings, one column each; GEO_ID and GEO_SURR_ID nullable not stated.
- Questions: none new (Q7)
## table-07-locations-send-p1: LOCATIONS_SEND
- LOCATIONS_SEND: 16 fields, no measures, all shown, the same field names as LOCATIONS_REC; 1 source "DIM_LOCATIONS_SEND" [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.LOCATIONS_SEND]. Key PC_LOC on GEO_ID [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.LOCATIONS_SEND.key.PC_LOC].
- To check in a later part: mappings and content filter of source DIM_LOCATIONS_SEND.
- Questions: none new (Q5, Q7 cover it)
## table-07-locations-send-p2: Where the data comes from (LOCATIONS_SEND, last part)
- Source "DIM_LOCATIONS_SEND": active, priority group not recorded, no level of detail, no content filter; reads one table DIM_LOCATIONS_SEND, type select [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.LOCATIONS_SEND.src.DIM_LOCATIONS_SEND#table0]; 16 mappings, one column each, same types as LOCATIONS_REC.
- Questions: none new (Q7)
## table-08-operations: OPERATIONS
- OPERATIONS: 8 fields, no measures, all shown; key PK_DIM_OPERATIONS on OPERATION_ID [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.OPERATIONS.key.PK_DIM_OPERATIONS]; 1 source "DIM_OPERATIONS" reading DIM_OPERATIONS (none) and DIM_OPERATION_METHODS (select), joined LeftOuter on OPERATION_METHOD_ID [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.OPERATIONS.src.DIM_OPERATIONS#table0.join0]; no content filter.
- OPERATION_METHOD_ID field mapped from DIM_OPERATION_METHODS.OPERATION_METHOD_ID, no precision printed [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.OPERATIONS.src.DIM_OPERATIONS#map2].
- Questions: none
## table-09-tariffs: TARIFFS
- TARIFFS: 9 fields, no measures, all shown; key PK_TARIFFS on TARIFF_ID [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TARIFFS.key.PK_TARIFFS]; 1 source "DIM_TARIFFS" reading the one table DIM_TARIFFS (type none), no content filter [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TARIFFS.src.DIM_TARIFFS#table0].
- Questions: Q8 what TARIFF_MIN_PAYMENT, TARIFF_MAX_PAYMENT, TARIFF_PAYMENT_SUM hold
## table-10-times-daily-p1: Fields (TIMES_DAILY, first 17 of 22 fields)
- TIMES_DAILY: 22 fields, no measures, all shown; key DIM_TIMES_DAILY_PK on TIME_ID [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TIMES_DAILY.key.DIM_TIMES_DAILY_PK]; 1 source "DIM_TIMES_DAILY" [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TIMES_DAILY].
- Part 1 stopped inside the Fields table after END_OF_CAL_YEAR (17 fields written); the next part continues "### Fields (continued)".
- To check in a later part: remaining 5 fields, the source's mappings and content filter.
- Questions: none
## table-10-times-daily-p2: Fields (continued) (TIMES_DAILY, fields 18 to 22)
- Fields TIME_ID (the key), WEEK_ENDING_DATE, YEAR_M, YEAR_Q, YEAR_W, all shown, all one column of DIM_TIMES_DAILY [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TIMES_DAILY.TIME_ID].
- Part 2 ended with "### Measures and fields report authors do not see"; part 3 starts with "### Where the data comes from".
- Questions: none
## table-10-times-daily-p3: Where the data comes from (TIMES_DAILY, last part)
- Source "DIM_TIMES_DAILY": active, priority group not recorded, no level of detail, no content filter; reads one table DIM_TIMES_DAILY, type select [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TIMES_DAILY.src.DIM_TIMES_DAILY#table0]; 22 mappings, one column each.
- Observation: TIME_ID of TIMES_DAILY is mapped from DIM_TIMES_DAILY.TIME_ID, type DATETIME [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TIMES_DAILY.src.DIM_TIMES_DAILY#map17]; to set beside the join of the transaction tables to DIM_TIMES_DAILY in the joins unit.
- Questions: none
## dbtable-01-dim-currency-scd: Database table DIM_CURRENCY_SCD
- DIM_CURRENCY_SCD: 9 columns (all mapped); key PK_DIM_CURRENCY_SCD on CURRENCY_SUR_ID; type none, no separate object name; read by source "DIM_CURRENCY_SCD" of CURRENCY [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_CURRENCY_SCD].
- Foreign keys FK_TRANS_CURRENCY of FCT_TRANSACTIONS_ACTUAL and of FCT_TRANSACTIONS_DAILY point at CURRENCY_SUR_ID [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_CURRENCY_SCD.key.PK_DIM_CURRENCY_SCD].
- Questions: none
## dbtable-02-dim-customers-rec: Database table DIM_CUSTOMERS_REC
- DIM_CUSTOMERS_REC: 12 columns (all mapped); key PK_DIM_CUSTOMERS on CUST_ID; type select; read by source "DIM_CUSTOMERS_REC" of CUSTOMERS_REC [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_CUSTOMERS_REC].
- Observation: the query of DIM_CUSTOMERS_REC is SELECT * FROM DIM_CUSTOMERS [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_CUSTOMERS_REC.def.DefaultMulDB]; to set beside the query of DIM_CUSTOMERS_SEND.
- Foreign keys FK_TR_CUST_REC of FCT_TRANSACTIONS_ACTUAL and FCT_TRANSACTIONS_DAILY (CUS_REC_ID) point at CUST_ID.
- Questions: none (Q5 still open)
## dbtable-03-dim-customers-send: Database table DIM_CUSTOMERS_SEND
- DIM_CUSTOMERS_SEND: 12 columns (all mapped); key PK_DIM_CUSTOMERS on CUST_ID; type select; read by source "DIM_CUSTOMERS_SEND" of CUSTOMERS_SEND [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_CUSTOMERS_SEND].
- Observation: the query of DIM_CUSTOMERS_SEND is SELECT * FROM DIM_CUSTOMERS [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_CUSTOMERS_SEND.def.DefaultMulDB]; to set beside the query of DIM_CUSTOMERS_REC, which is the same text [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_CUSTOMERS_REC.def.DefaultMulDB].
- Foreign keys FK_TR_CUST_SEND of FCT_TRANSACTIONS_ACTUAL and FCT_TRANSACTIONS_DAILY (CUST_SEND_ID) point at CUST_ID.
- Questions: none; See also added to Q5 (both read DIM_CUSTOMERS)
## dbtable-04-dim-locations-rec: Database table DIM_LOCATIONS_REC
- DIM_LOCATIONS_REC: 16 columns (all mapped); key PC_LOC on GEO_ID; type select; read by source "DIM_LOCATIONS_REC" of LOCATIONS_REC [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_LOCATIONS_REC].
- Observation: the query of DIM_LOCATIONS_REC is SELECT * FROM DIM_LOCATIONS_SCD [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_LOCATIONS_REC.def.DefaultMulDB]; to set beside the query of DIM_LOCATIONS_SEND.
- Foreign keys FK_TRANS_LOC_REC of FCT_TRANSACTIONS_ACTUAL and FCT_TRANSACTIONS_DAILY (GEO_REC_SUR_ID) point at GEO_ID.
- Questions: none (Q7)
## dbtable-05-dim-locations-send: Database table DIM_LOCATIONS_SEND
- DIM_LOCATIONS_SEND: 16 columns (all mapped); key PC_LOC on GEO_ID; type select; read by source "DIM_LOCATIONS_SEND" of LOCATIONS_SEND [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_LOCATIONS_SEND].
- Observation: the query of DIM_LOCATIONS_SEND is SELECT * FROM DIM_LOCATIONS_SCD [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_LOCATIONS_SEND.def.DefaultMulDB]; to set beside the query of DIM_LOCATIONS_REC, the same text [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_LOCATIONS_REC.def.DefaultMulDB].
- Foreign keys FK_TRANS_LOC_SEND of FCT_TRANSACTIONS_ACTUAL and FCT_TRANSACTIONS_DAILY (GEO_SEND_SUR_ID) point at GEO_ID.
- Questions: none (Q5, Q7)
## dbtable-06-dim-operations: Database table DIM_OPERATIONS
- DIM_OPERATIONS: 5 columns (4 mapped, OPERATION_METHOD_ID not mapped); key PK_DIM_OPERATIONS on OPERATION_ID; type none, object DIM_OPERATIONS; read by source "DIM_OPERATIONS" of OPERATIONS [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_OPERATIONS].
- Foreign keys: DIM_OPERATIONS_Foreign Key to DIM_OPERATION_METHODS; FK_TRANS_OPERATIONS of both fact tables point at OPERATION_ID.
- Questions: none
## dbtable-07-dim-operation-methods: Database table DIM_OPERATION_METHODS
- DIM_OPERATION_METHODS: 4 columns (all mapped); key PK_OP_M on OPERATION_METHOD_ID; type select; query SELECT DISTINCT of 4 method columns FROM DIM_OPERATIONS WHERE OPERATION_METHOD_ID IS NOT NULL; read by source "DIM_OPERATIONS" of OPERATIONS [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_OPERATION_METHODS.def.DefaultMulDB].
- Observation: the query of DIM_OPERATION_METHODS reads OPERATION_METHOD_NAME, OPERATION_METHOD_TYPE and OPERATION_METHOD_TYPE_ID from DIM_OPERATIONS [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_OPERATION_METHODS.def.DefaultMulDB]; to set beside the database table DIM_OPERATIONS, for which the repository lists 5 columns, none of them these three [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_OPERATIONS].
- Questions: Q9 whether DIM_OPERATIONS has the method columns and whether OPERATION_METHOD_ID is unique in the query result
## dbtable-08-dim-tariffs: Database table DIM_TARIFFS
- DIM_TARIFFS: 9 columns (all mapped); key PK_TARIFFS on TARIFF_ID; type none, no separate object name; read by source "DIM_TARIFFS" of TARIFFS [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_TARIFFS]. FK_TRANS_TARIFFS of both fact tables point at TARIFF_ID.
- Questions: none
## dbtable-09-dim-times-daily: Database table DIM_TIMES_DAILY
- DIM_TIMES_DAILY: 22 columns (all mapped); key DIM_TIMES_DAILY_PK on TIME_ID (DATETIME); type select, a query over the database table DIM_TIMES_DAILY adding YEAR_Q, YEAR_M, YEAR_W with TO_NUMBER of concatenations; read by source "DIM_TIMES_DAILY" of TIMES_DAILY [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_TIMES_DAILY.def.DefaultMulDB].
- FK_TRANS_TIMES of FCT_TRANSACTIONS_ACTUAL and FCT_TRANSACTIONS_DAILY (EVENT_DT) point at TIME_ID [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_TIMES_DAILY.key.DIM_TIMES_DAILY_PK].
- Questions: Q10 what YEAR_Q, YEAR_M, YEAR_W identify and what CALENDAR_WEEK_NUMBER holds
## dbtable-10-fct-transactions-actual: Database table FCT_TRANSACTIONS_ACTUAL
- FCT_TRANSACTIONS_ACTUAL: 14 columns (all mapped); no key recorded; type select; query SELECT * FROM FCT_TRANSACTIONS_DAILY WHERE EXTRACT (YEAR from EVENT_DT) = VALUEOF(BI_YEAR); read by source "FCT_TRANSACTIONS_ACTUAL"; 8 foreign keys to the 8 dimension tables [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.FCT_TRANSACTIONS_ACTUAL].
- Observation: the query of FCT_TRANSACTIONS_ACTUAL names BI_YEAR inside VALUEOF [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.FCT_TRANSACTIONS_ACTUAL.def.DefaultMulDB]; to set beside the variables unit.
- Observation: FCT_TRANSACTIONS_ACTUAL reads SELECT * FROM FCT_TRANSACTIONS_DAILY and lists 14 columns including PERIOD_ID, TA_COUNTRY_REC_ID, TA_COUNTRY_SEND_ID [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.FCT_TRANSACTIONS_ACTUAL.def.DefaultMulDB]; to set beside the column count of the table FCT_TRANSACTIONS_DAILY.
- Questions: none new; See also added to Q6
## dbtable-11-fct-transactions-daily: Database table FCT_TRANSACTIONS_DAILY
- FCT_TRANSACTIONS_DAILY: 14 columns (10 mapped; INSERT_DT, PERIOD_ID, TA_COUNTRY_REC_ID, TA_COUNTRY_SEND_ID not mapped); no key recorded; type none, object FCT_TRANSACTIONS_DAILY; read by source "FCT_TRANSACTIONS_DAILY" of TRANSACTIONS_DAILY; 8 foreign keys to the 8 dimension tables [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.FCT_TRANSACTIONS_DAILY].
- Observation: FCT_TRANSACTIONS_DAILY lists 14 columns, among them PERIOD_ID, TA_COUNTRY_REC_ID, TA_COUNTRY_SEND_ID, not mapped here [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.FCT_TRANSACTIONS_DAILY.PERIOD_ID]; to set beside FCT_TRANSACTIONS_ACTUAL, whose query reads SELECT * FROM FCT_TRANSACTIONS_DAILY and whose 14 columns are all mapped [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.FCT_TRANSACTIONS_ACTUAL.def.DefaultMulDB].
- Questions: none (Q6 stays open: the repository lists these columns for FCT_TRANSACTIONS_DAILY, but not what they hold)
## joins: How the tables relate
- 12 business-model joins, all Inner, (n..n) to (0..1): TRANSACTIONS_DAILY to the 8 descriptive tables, FCT_TRANSACTIONS_ACTUAL to TIMES_DAILY, CURRENCY, OPERATIONS, TARIFFS only [kb:bi-analytics-2013::bmm::joins.Iryna_Bradzetskaya_MT#1:Relationship_2004:4072641381133744].
- 17 database foreign keys, one column pair each, no cardinality recorded: 8 from each fact table to the dimension tables, 1 from DIM_OPERATIONS to DIM_OPERATION_METHODS [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_OPERATIONS.DIM_OPERATIONS_Foreign Key].
- Observation: the business-model join of TRANSACTIONS_DAILY to TIMES_DAILY is Inner [kb:bi-analytics-2013::bmm::joins.Iryna_Bradzetskaya_MT#1:Relationship_2004:4041821380622953]; to set beside the join type inside source FCT_TRANSACTIONS_DAILY to DIM_LOCATIONS_REC, LeftOuter.
- Questions: Q11 whether FCT_TRANSACTIONS_ACTUAL should be reportable by customer and location
## hierarchies-p1: Drill paths (Locations_Rec and Locations_Send; part 2 holds the third)
- Drill paths Locations_Rec (All Part → Part → Region → Country) and Locations_Send (All_Part → Part → Region → Country), 4 levels each, offered to report authors in LOCATIONS_REC and LOCATIONS_SEND; member counts all recorded as 1; Country keyed by GEO_ID [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.Locations_Rec].
- Part 1 ended after the Locations_Send table and the closing paragraph; part 2 continues with "### Drill path ..." for the third drill path.
- Questions: none new (Q7)
## hierarchies-p2: Drill path "Times" (last part)
- Drill path Times: 6 levels, time dimension true; Total Years → Years (12) → Quarter (48) → Month (144) → Day (4400), Week (650); Day primary key TIME_ID; Quarter/Month/Week primary keys are the number columns alone [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.Times].
- Offered as 4 hierarchies in TIMES_DAILY: Times - Month - Day, Times - Month - Week, Times - Quarter - Day, Times - Quarter - Week [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.TIMES_DAILY.Times - Quarter - Week].
- Questions: Q12 whether Quarter, Month, Week are identified by number alone
## variables: Values set per user or for the whole system
- BI_YEAR: repository variable, default 2010.0d, set by init block BI_IB (SELECT MAX(EXTRACT (YEAR from EVENT_DT )) FROM FCT_TRANSACTIONS_DAILY; refresh 3600; enabled not recorded), named in the query of FCT_TRANSACTIONS_ACTUAL [kb:bi-analytics-2013::var::BI_YEAR].
- ROLEGUIDS: used by name, not defined in the repository; named in the 2 content filters and 2 row filters (section Values set per user or for the whole system; no Row identifier).
- Questions: Q13 run-time value of BI_YEAR
## security: Security
- 2 row filters, on source FCT_TRANSACTIONS_ACTUAL (texts ;MT_User_A; ;MT_User_F;) [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.FCT_TRANSACTIONS_ACTUAL.src.FCT_TRANSACTIONS_ACTUAL#filter] and on source FCT_TRANSACTIONS_DAILY (texts ;Role_MT_A; ;Role_MT_F;) [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TRANSACTIONS_DAILY.src.FCT_TRANSACTIONS_DAILY#filter], each limiting CURRENCY_SUR_ID to (12,10,15,11) or (13,14, 16,17). No permission and no role recorded.
- Observation: the row filter on source FCT_TRANSACTIONS_DAILY has the same text as that source's content filter [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TRANSACTIONS_DAILY.src.FCT_TRANSACTIONS_DAILY#filter]; to set beside the content filter [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TRANSACTIONS_DAILY.src.FCT_TRANSACTIONS_DAILY].
- Observation: the row filter on source FCT_TRANSACTIONS_ACTUAL has the same text as that source's content filter [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.FCT_TRANSACTIONS_ACTUAL.src.FCT_TRANSACTIONS_ACTUAL#filter]; to set beside the content filter [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.FCT_TRANSACTIONS_ACTUAL.src.FCT_TRANSACTIONS_ACTUAL].
- Questions: Q14 what the four role/user names are and who holds them
## reports: Reports built on the subject area
- No Web Catalog half: reports and dashboards not examined; calculations, filters and prompts inside reports unknown (manifest, no Row identifier).
- Questions: none
## gaps: What the export did not capture
- No errors about objects of the slice. 107 unparsed rows (82 unread_attribute, 21 unread_element, 4 unread_text; 25205 occurrences estate-wide). Group display names, guids and descriptions not read; QueryPrivilege (72) and Project (30) types have no reader; 2 Variable values redacted (manifest, no Row identifier).
- Questions: none new; See also added to Q14
