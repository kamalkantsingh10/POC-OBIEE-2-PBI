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
