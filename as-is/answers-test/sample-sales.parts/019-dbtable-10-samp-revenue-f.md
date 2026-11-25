## Database table SAMP_REVENUE_F

SAMP_REVENUE_F is a database table in `xe.SAMPLESALES`, type `none`; it is not an alias of another table, and no description is recorded [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F]. Its object in the database, for database type `DefaultMulDB`, is `SAMP_REVENUE_F` [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F.def.DefaultMulDB]. The export lists 12 columns for it, all 12 mapped by a field of this subject area, and no key (counted).

### Columns

| # | Column | Type | Precision | Scale | Nullable | External name | Mapped by | Evidence |
|---|---|---|---|---|---|---|---|---|
| 1 | BILL_DAY_DT | DATE | not recorded | not recorded | true | not recorded | Fact Revenue, Bill Day Dt | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F.BILL_DAY_DT] |
| 2 | BILL_MTH_KEY | DOUBLE | 15 | not recorded | true | `samplesales` | Fact Revenue, Bill Mth Key | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F.BILL_MTH_KEY] |
| 3 | DISCNT_VALUE | DOUBLE | 6 | 2 | true | `samplesales` | Fact Revenue, Discnt Value | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F.DISCNT_VALUE] |
| 4 | OFFICE_KEY | DOUBLE | 15 | not recorded | true | `samplesales` | Fact Revenue, Office Key | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F.OFFICE_KEY] |
| 5 | ORDER_DAY_DT | DATETIME | not recorded | not recorded | true | `samplesales` | Fact Revenue, Order Day Dt | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F.ORDER_DAY_DT] |
| 6 | ORDER_NUMBER | DOUBLE | 15 | not recorded | true | `samplesales` | Fact Revenue, Order Number | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F.ORDER_NUMBER] |
| 7 | ORDER_STATUS | VARCHAR | 11 | not recorded | true | `samplesales` | Fact Revenue, Order Status | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F.ORDER_STATUS] |
| 8 | ORDER_TYPE | VARCHAR | 8 | not recorded | true | `samplesales` | Fact Revenue, Order Type | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F.ORDER_TYPE] |
| 9 | PROD_KEY | DOUBLE | 15 | not recorded | true | `samplesales` | Fact Revenue, Prod Key | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F.PROD_KEY] |
| 10 | REVENUE | DOUBLE | 7 | 2 | true | `samplesales` | Fact Revenue: All Products Revenue, All Months Revenue, Revenue (GBP), Revenue (EUR), Revenue (INR), Revenue | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F.REVENUE] |
| 11 | UNITS | DOUBLE | 15 | not recorded | true | `samplesales` | Fact Revenue, Units | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F.UNITS] |
| 12 | EMPLOYEE_KEY | INT | not recorded | not recorded | not stated | not recorded | Fact Revenue, Sales Employee Key | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F.EMPLOYEE_KEY] |

Nullable: the export records this only when it is true. "Not stated" means the export does not carry it; it is not a statement that the column must be filled. "Not recorded" means the repository says nothing; it does not mean no, and it does not mean 0.

The one column REVENUE is mapped by six fields of Fact Revenue [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F.REVENUE]; how each is mapped is in the section Fact Revenue (open question Q3; question Q4, answered).

### Keys

Keys of SAMP_REVENUE_F: not recorded. The repository declares no key for it, so nothing in the repository says which column(s) identify a row (listed in the packet without a Row identifier).

### Foreign keys and joins

- The foreign key "SAMP_REVENUE_F_FKey" from this table (column BILL_MTH_KEY) to SAMP_TIME_MTH_D (column MTH_KEY); stated in full in the section How the tables relate.
- The foreign key "SAMP_REVENUE_F_FKey#1" from this table (column OFFICE_KEY) to SAMP_OFFICES_D (column OFFICE_KEY); stated in full in the section How the tables relate.
- The foreign key "SAMP_REVENUE_F_FKey#2" from this table (column PROD_KEY) to SAMP_PRODUCTS_D (column PROD_KEY); stated in full in the section How the tables relate.
- The foreign key "SAMP_REVENUE_F_FKey#3" from this table (column BILL_DAY_DT) to SAMP_TIME_DAY_D (column CALENDAR_DATE); stated in full in the section How the tables relate.
- The foreign key "SAMP_REVENUE_F_FKey#4" from this table (column EMPLOYEE_KEY) to EMP_PARENT_CHILD (column MEMBER_KEY); stated in full in the section How the tables relate.

### Read by

The source "SAMP_REVENUE_F" of the business table Fact Revenue (listed in the packet without a Row identifier).
