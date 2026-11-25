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
