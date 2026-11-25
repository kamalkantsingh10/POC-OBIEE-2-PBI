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
