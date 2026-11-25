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
