## Database table SAMP_REVENUE_A_MTH

SAMP_REVENUE_A_MTH is a database table in `xe.SAMPLESALES`, type `none`; it is not an alias of another table, and no description is recorded [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A_MTH]. No separate object name in the database is recorded, so the repository knows it only by the name SAMP_REVENUE_A_MTH (listed in the packet without a Row identifier). The export lists 4 columns for it, all 4 mapped by a field of this subject area and none not mapped, and no key (counted; no Row identifier).

### Columns

| # | Column | Type | Precision | Scale | Nullable | External name | Mapped by | Evidence |
|---|---|---|---|---|---|---|---|---|
| 1 | BILL_MTH_KEY | DOUBLE | 22 | not recorded | true | `samplesales` | Fact Revenue, BILL_MTH_KEY | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A_MTH.BILL_MTH_KEY] |
| 2 | DISCNT_VALUE | DOUBLE | 22 | not recorded | true | `samplesales` | Fact Revenue, DISCNT_VALUE | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A_MTH.DISCNT_VALUE] |
| 3 | REVENUE | DOUBLE | 22 | not recorded | true | `samplesales` | Fact Revenue, Revenue | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A_MTH.REVENUE] |
| 4 | UNITS | DOUBLE | 22 | not recorded | true | `samplesales` | Fact Revenue, Units | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A_MTH.UNITS] |

Nullable: the export records this only when it is true. "Not stated" means the export does not carry it; it is not a statement that the column must be filled. "Not recorded" means the repository says nothing; it does not mean no, and it does not mean 0.

### Keys

Keys of SAMP_REVENUE_A_MTH: not recorded. The repository declares no key for it, so nothing in the repository says which column(s) identify a row (listed in the packet without a Row identifier).

### Foreign keys and joins

- The foreign key "SAMP_REVENUE_A_MTH_Foreign Key" from this table (column BILL_MTH_KEY) to SAMP_TIME_MTH_D (column MTH_KEY); stated in full in the section How the tables relate.

### Read by

The source "Aggregate Revenue By Month" of the business table Fact Revenue (listed in the packet without a Row identifier).
