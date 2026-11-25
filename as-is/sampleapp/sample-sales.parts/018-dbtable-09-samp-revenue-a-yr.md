## Database table SAMP_REVENUE_A_YR

SAMP_REVENUE_A_YR is a database table in `xe.SAMPLESALES`, type `none`; it is not an alias of another table, and no description is recorded [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A_YR]. Its object in the database, for database type `DefaultMulDB`, is `SAMP_REVENUE_A_YR` [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A_YR.def.DefaultMulDB]. The export lists 4 columns for it, all 4 mapped by a field of this subject area and none not mapped, and no key (counted; no Row identifier).

### Columns

| # | Column | Type | Precision | Scale | Nullable | External name | Mapped by | Evidence |
|---|---|---|---|---|---|---|---|---|
| 1 | BILL_YEAR_KEY | DOUBLE | not recorded | not recorded | true | not recorded | Fact Revenue, BILL_YEAR_KEY | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A_YR.BILL_YEAR_KEY] |
| 2 | DISCNT_VALUE | DOUBLE | 22 | not recorded | true | `samplesales` | Fact Revenue, DISCNT_VALUE | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A_YR.DISCNT_VALUE] |
| 3 | REVENUE | DOUBLE | 22 | not recorded | true | `samplesales` | Fact Revenue, Revenue | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A_YR.REVENUE] |
| 4 | UNITS | DOUBLE | 22 | not recorded | true | `samplesales` | Fact Revenue, Units | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A_YR.UNITS] |

Nullable: the export records this only when it is true. "Not stated" means the export does not carry it; it is not a statement that the column must be filled. "Not recorded" means the repository says nothing; it does not mean no, and it does not mean 0.

### Keys

Keys of SAMP_REVENUE_A_YR: not recorded. The repository declares no key for it, so nothing in the repository says which column(s) identify a row (listed in the packet without a Row identifier).

### Foreign keys and joins

- The foreign key "SAMP_REVENUE_A_YR_Foreign Key" from this table (column BILL_YEAR_KEY) to SAMP_TIME_MTH_D (column CAL_YEAR); stated in full in the section How the tables relate.

### Read by

The source "Aggregate Revenue by Year" of the business table Fact Revenue (listed in the packet without a Row identifier).
