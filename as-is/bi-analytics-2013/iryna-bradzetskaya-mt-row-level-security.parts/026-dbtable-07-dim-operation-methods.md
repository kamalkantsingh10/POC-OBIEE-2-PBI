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
