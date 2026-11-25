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
