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
