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
