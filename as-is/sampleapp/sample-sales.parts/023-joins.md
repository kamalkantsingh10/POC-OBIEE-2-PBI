## How the tables relate

### Joins between business tables

The business model records 7 joins between business tables. The row counts in brackets are copied as printed.

| From | Rows | To | Rows | Join type | Evidence |
|---|---|---|---|---|---|
| Dim Time | 0..1 | Fact Target | 0..n | Inner | [kb:sampleapp::bmm::joins.Relationship_2004:11061353928600] |
| Dim Time | 0..1 | Fact Revenue | 0..n | Inner | [kb:sampleapp::bmm::joins.Relationship_2004:11091353928666] |
| Dim Sales Rep | 1..1 | Fact Revenue | 0..n | Inner | [kb:sampleapp::bmm::joins.Relationship_2004:17511354089145] |
| Fact Revenue | n..n | Dim Offices | 0..1 | Inner | [kb:sampleapp::bmm::joins.Relationship_2004:2111353851711] |
| Fact Revenue | n..n | Dim Products | 0..1 | Inner | [kb:sampleapp::bmm::joins.Relationship_2004:2141353851711] |
| Fact Target | n..n | Dim Offices | 0..1 | Inner | [kb:sampleapp::bmm::joins.Relationship_2004:2301353851711] |
| Fact Target | n..n | Dim Products | 0..1 | Inner | [kb:sampleapp::bmm::joins.Relationship_2004:2331353851711] |

No join between "Fact Target" and "Dim Sales Rep" is listed, and none for "Dim Products (Translated)" (counted from the rows above).

### Joins between database tables

The packet lists 17 joins between database tables: 3 complex joins and 14 foreign keys (counted).

| Kind | Name | From table (column; type, precision, nullable) | To table (column; type, precision, nullable) | Cardinality | Evidence |
|---|---|---|---|---|---|
| complex join | Complex Join | FactCrossJoin | SAMP_OFFICES_D | `ONE TO ONE` | [kb:sampleapp::phys::joins.Complex Join] |
| complex join | Complex Join#1 | FactCrossJoin | SAMP_PRODUCTS_D | `ONE TO ONE` | [kb:sampleapp::phys::joins.Complex Join#1] |
| complex join | Complex Join#2 | FactCrossJoin | EMPLOYEE | `ONE TO ONE` | [kb:sampleapp::phys::joins.Complex Join#2] |
| foreign key | EMP_PARENT_CHILD_FKey | EMP_PARENT_CHILD (ANCESTOR_KEY; DOUBLE, precision 15, nullable not stated) | EMPLOYEE (EMP_ID; DOUBLE, precision 15, nullable not stated) | not recorded | [kb:sampleapp::phys::xe.SAMPLESALES.EMP_PARENT_CHILD.EMP_PARENT_CHILD_FKey] |
| foreign key | SAMP_PRODUCTS_D_TRANSLATION_FKey | SAMP_PRODUCTS_D_TRANSLATION (PROD_KEY; DOUBLE, precision 15, nullable not stated) | SAMP_PRODUCTS_D (PROD_KEY; DOUBLE, precision 15, nullable not stated) | not recorded | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_PRODUCTS_D_TRANSLATION.SAMP_PRODUCTS_D_TRANSLATION_FKey] |
| foreign key | SAMP_REVENUE_A_FKey | SAMP_REVENUE_A (BILL_MTH_KEY; DOUBLE, precision 15, nullable true) | SAMP_TIME_MTH_D (MTH_KEY; DOUBLE, precision 15, nullable true) | not recorded | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A.SAMP_REVENUE_A_FKey] |
| foreign key | SAMP_REVENUE_A_FKey#1 | SAMP_REVENUE_A (PROD_KEY; DOUBLE, precision 15, nullable true) | SAMP_PRODUCTS_D (PROD_KEY; DOUBLE, precision 15, nullable not stated) | not recorded | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A.SAMP_REVENUE_A_FKey#1] |
| foreign key | SAMP_REVENUE_A_MTH_Foreign Key | SAMP_REVENUE_A_MTH (BILL_MTH_KEY; DOUBLE, precision 22, nullable true) | SAMP_TIME_MTH_D (MTH_KEY; DOUBLE, precision 15, nullable true) | not recorded | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A_MTH.SAMP_REVENUE_A_MTH_Foreign Key] |
| foreign key | SAMP_REVENUE_A_YR_Foreign Key | SAMP_REVENUE_A_YR (BILL_YEAR_KEY; DOUBLE, nullable true) | SAMP_TIME_MTH_D (CAL_YEAR; DOUBLE, precision 15, nullable true) | not recorded | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A_YR.SAMP_REVENUE_A_YR_Foreign Key] |
| foreign key | SAMP_REVENUE_F_FKey | SAMP_REVENUE_F (BILL_MTH_KEY; DOUBLE, precision 15, nullable true) | SAMP_TIME_MTH_D (MTH_KEY; DOUBLE, precision 15, nullable true) | not recorded | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F.SAMP_REVENUE_F_FKey] |
| foreign key | SAMP_REVENUE_F_FKey#1 | SAMP_REVENUE_F (OFFICE_KEY; DOUBLE, precision 15, nullable true) | SAMP_OFFICES_D (OFFICE_KEY; DOUBLE, precision 15, nullable not stated) | not recorded | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F.SAMP_REVENUE_F_FKey#1] |
| foreign key | SAMP_REVENUE_F_FKey#2 | SAMP_REVENUE_F (PROD_KEY; DOUBLE, precision 15, nullable true) | SAMP_PRODUCTS_D (PROD_KEY; DOUBLE, precision 15, nullable not stated) | not recorded | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F.SAMP_REVENUE_F_FKey#2] |
| foreign key | SAMP_REVENUE_F_FKey#3 | SAMP_REVENUE_F (BILL_DAY_DT; DATE, nullable true) | SAMP_TIME_DAY_D (CALENDAR_DATE; DATE, nullable not stated) | not recorded | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F.SAMP_REVENUE_F_FKey#3] |
| foreign key | SAMP_REVENUE_F_FKey#4 | SAMP_REVENUE_F (EMPLOYEE_KEY; INT, nullable not stated) | EMP_PARENT_CHILD (MEMBER_KEY; DOUBLE, precision 15, nullable not stated) | not recorded | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F.SAMP_REVENUE_F_FKey#4] |
| foreign key | SAMP_TARGETS_F_FKey | SAMP_TARGETS_F (BILL_MTH_KEY; DOUBLE, precision 15, nullable true) | SAMP_TIME_MTH_D (MTH_KEY; DOUBLE, precision 15, nullable true) | not recorded | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TARGETS_F.SAMP_TARGETS_F_FKey] |
| foreign key | SAMP_TARGETS_F_FKey#1 | SAMP_TARGETS_F (OFFICE_KEY; DOUBLE, precision 15, nullable true) | SAMP_OFFICES_D (OFFICE_KEY; DOUBLE, precision 15, nullable not stated) | not recorded | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TARGETS_F.SAMP_TARGETS_F_FKey#1] |
| foreign key | SAMP_TARGETS_F_FKey#2 | SAMP_TARGETS_F (PROD_KEY; DOUBLE, precision 15, nullable true) | SAMP_PRODUCTS_D (PROD_KEY; DOUBLE, precision 15, nullable not stated) | not recorded | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TARGETS_F.SAMP_TARGETS_F_FKey#2] |

Each foreign key has one column pair, the two columns named in its row. Nullable: the export records this only when it is true; "not stated" is not a statement that the column must be filled.

The three complex joins each have the join condition `1=1` [kb:sampleapp::phys::joins.Complex Join] [kb:sampleapp::phys::joins.Complex Join#1] [kb:sampleapp::phys::joins.Complex Join#2]. The condition names no column of either table (open question Q6).

### Points to note

- The foreign key SAMP_REVENUE_F_FKey#4 pairs a column of type INT (SAMP_REVENUE_F.EMPLOYEE_KEY) with a column of type DOUBLE (EMP_PARENT_CHILD.MEMBER_KEY) [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F.SAMP_REVENUE_F_FKey#4].
- The foreign key SAMP_REVENUE_A_YR_Foreign Key goes from SAMP_REVENUE_A_YR.BILL_YEAR_KEY to SAMP_TIME_MTH_D.CAL_YEAR, not to MTH_KEY [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A_YR.SAMP_REVENUE_A_YR_Foreign Key] (open question Q10).
