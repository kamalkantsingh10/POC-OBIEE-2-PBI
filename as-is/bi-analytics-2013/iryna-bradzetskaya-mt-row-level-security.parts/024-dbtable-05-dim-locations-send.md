## Database table DIM_LOCATIONS_SEND

DIM_LOCATIONS_SEND is listed in `Iryna_Bradzetskaya_MT.U_SAL` with type `select`: it is not a table of the database but a query kept in the repository. It is not an alias of another table, and no description is recorded [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_LOCATIONS_SEND]. Its query, for the database type `DefaultMulDB`, is `SELECT * FROM DIM_LOCATIONS_SCD` [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_LOCATIONS_SEND.def.DefaultMulDB]: it reads every column of a database table named DIM_LOCATIONS_SCD. The export lists 16 columns for it, all 16 mapped by a field of this subject area and none not mapped, and 1 key (counted).

### Columns

| # | Column | Type | Precision | Scale | Nullable | External name | Mapped by | Evidence |
|---|---|---|---|---|---|---|---|---|
| 1 | COUNTRY_CODE_A2 | VARCHAR | 5 | not recorded | true | not recorded | LOCATIONS_SEND, COUNTRY_CODE_A2 | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_LOCATIONS_SEND.COUNTRY_CODE_A2] |
| 2 | COUNTRY_CODE_A3 | VARCHAR | 5 | not recorded | true | not recorded | LOCATIONS_SEND, COUNTRY_CODE_A3 | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_LOCATIONS_SEND.COUNTRY_CODE_A3] |
| 3 | COUNTRY_ID | DOUBLE | 22 | not recorded | true | not recorded | LOCATIONS_SEND, COUNTRY_ID | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_LOCATIONS_SEND.COUNTRY_ID] |
| 4 | GEO_COUNTRY_ID | DOUBLE | 22 | not recorded | true | not recorded | LOCATIONS_SEND, GEO_COUNTRY_ID | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_LOCATIONS_SEND.GEO_COUNTRY_ID] |
| 5 | GEO_COUNTRY_NAME | VARCHAR | 80 | not recorded | true | not recorded | LOCATIONS_SEND, GEO_COUNTRY_NAME | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_LOCATIONS_SEND.GEO_COUNTRY_NAME] |
| 6 | GEO_ID | DOUBLE | 22 | not recorded | not stated | not recorded | LOCATIONS_SEND, GEO_ID | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_LOCATIONS_SEND.GEO_ID] |
| 7 | GEO_PART_CODE | VARCHAR | 30 | not recorded | true | not recorded | LOCATIONS_SEND, GEO_PART_CODE | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_LOCATIONS_SEND.GEO_PART_CODE] |
| 8 | GEO_PART_DESC | VARCHAR | 50 | not recorded | true | not recorded | LOCATIONS_SEND, GEO_PART_DESC | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_LOCATIONS_SEND.GEO_PART_DESC] |
| 9 | GEO_PART_ID | DOUBLE | 22 | not recorded | true | not recorded | LOCATIONS_SEND, GEO_PART_ID | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_LOCATIONS_SEND.GEO_PART_ID] |
| 10 | GEO_REGION_CODE | VARCHAR | 3 | not recorded | true | not recorded | LOCATIONS_SEND, GEO_REGION_CODE | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_LOCATIONS_SEND.GEO_REGION_CODE] |
| 11 | GEO_REGION_DESC | VARCHAR | 50 | not recorded | true | not recorded | LOCATIONS_SEND, GEO_REGION_DESC | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_LOCATIONS_SEND.GEO_REGION_DESC] |
| 12 | GEO_REGION_ID | DOUBLE | 22 | not recorded | true | not recorded | LOCATIONS_SEND, GEO_REGION_ID | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_LOCATIONS_SEND.GEO_REGION_ID] |
| 13 | GEO_SURR_ID | DOUBLE | 22 | not recorded | not stated | not recorded | LOCATIONS_SEND, GEO_SURR_ID | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_LOCATIONS_SEND.GEO_SURR_ID] |
| 14 | LEVEL_CODE | VARCHAR | 30 | not recorded | true | not recorded | LOCATIONS_SEND, LEVEL_CODE | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_LOCATIONS_SEND.LEVEL_CODE] |
| 15 | VALID_FROM | DATETIME | 30 | not recorded | true | not recorded | LOCATIONS_SEND, VALID_FROM | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_LOCATIONS_SEND.VALID_FROM] |
| 16 | VALID_TO | DATETIME | 30 | not recorded | true | not recorded | LOCATIONS_SEND, VALID_TO | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_LOCATIONS_SEND.VALID_TO] |

Nullable: the export records this only when it is true. "Not stated" means the export does not carry it; it is not a statement that the column must be filled.

### Keys

| Key | Columns | Evidence |
|---|---|---|
| PC_LOC | GEO_ID | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_LOCATIONS_SEND.key.PC_LOC] |

This is key 1 of 1 the repository declares for the table. The repository does not say whether a physical key is the primary key; this is the only key it declares. Two foreign keys of this subject area point at exactly this column [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_LOCATIONS_SEND.key.PC_LOC]. The column GEO_SURR_ID is not part of any recorded key (open question Q7).

### Foreign keys and joins

- Foreign key FK_TRANS_LOC_SEND from FCT_TRANSACTIONS_ACTUAL (GEO_SEND_SUR_ID) to this table (GEO_ID); stated in full in the section How the tables relate.
- Foreign key FK_TRANS_LOC_SEND from FCT_TRANSACTIONS_DAILY (GEO_SEND_SUR_ID) to this table (GEO_ID); stated in full in the section How the tables relate.

### Read by

The source "DIM_LOCATIONS_SEND" of the business table LOCATIONS_SEND (listed in the packet without a Row identifier).
