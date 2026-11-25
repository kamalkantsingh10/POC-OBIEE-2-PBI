## Database table SAMP_REVENUE_A

SAMP_REVENUE_A is a database table in `xe.SAMPLESALES`, type `none`; it is not an alias of another table, and no description is recorded [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A]. Its object in the database, for database type `DefaultMulDB`, is `SAMP_REVENUE_A` [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A.def.DefaultMulDB]. The export lists 5 columns for it, all 5 mapped by a field of this subject area and none not mapped, and no key (counted; no Row identifier).

### Columns

| # | Column | Type | Precision | Scale | Nullable | External name | Mapped by | Evidence |
|---|---|---|---|---|---|---|---|---|
| 1 | BILL_MTH_KEY | DOUBLE | 15 | not recorded | true | `samplesales` | Fact Revenue, Bill Mth Key | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A.BILL_MTH_KEY] |
| 2 | DISCNT_VALUE | DOUBLE | 7 | 2 | true | `samplesales` | Fact Revenue, Discnt Value | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A.DISCNT_VALUE] |
| 3 | PROD_KEY | DOUBLE | 15 | not recorded | true | `samplesales` | Fact Revenue, Prod Key | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A.PROD_KEY] |
| 4 | REVENUE | DOUBLE | 8 | 2 | true | `samplesales` | Fact Revenue: Revenue, All Products Revenue, All Months Revenue, Revenue (GBP), Revenue (EUR), Revenue (INR) | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A.REVENUE] |
| 5 | UNITS | DOUBLE | 15 | not recorded | true | `samplesales` | Fact Revenue, Units | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A.UNITS] |

Nullable: the export records this only when it is true. "Not stated" means the export does not carry it; it is not a statement that the column must be filled. "Not recorded" means the repository says nothing; it does not mean no, and it does not mean 0.

The one column REVENUE is mapped by six fields of Fact Revenue [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A.REVENUE] (open questions Q3 and Q4).

### Keys

Keys of SAMP_REVENUE_A: not recorded. The repository declares no key for it, so nothing in the repository says which column(s) identify a row (listed in the packet without a Row identifier).

### Foreign keys and joins

- The foreign key "SAMP_REVENUE_A_FKey" from this table (column BILL_MTH_KEY) to SAMP_TIME_MTH_D (column MTH_KEY); stated in full in the section How the tables relate.
- The foreign key "SAMP_REVENUE_A_FKey#1" from this table (column PROD_KEY) to SAMP_PRODUCTS_D (column PROD_KEY); stated in full in the section How the tables relate.

### Read by

The source "Aggregate Revenue" of the business table Fact Revenue (listed in the packet without a Row identifier).
