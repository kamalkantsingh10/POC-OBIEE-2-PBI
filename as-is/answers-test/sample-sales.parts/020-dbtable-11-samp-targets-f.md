## Database table SAMP_TARGETS_F

SAMP_TARGETS_F is a database table in `xe.SAMPLESALES`, type `none`; it is not an alias of another table, and no description is recorded [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TARGETS_F]. Its object in the database, for database type `DefaultMulDB`, is `SAMP_TARGETS_F` [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TARGETS_F.def.DefaultMulDB]. The export lists 5 columns for it, all 5 mapped by a field of this subject area, and no key (counted).

### Columns

| # | Column | Type | Precision | Scale | Nullable | External name | Mapped by | Evidence |
|---|---|---|---|---|---|---|---|---|
| 1 | BILL_MTH_KEY | DOUBLE | 15 | not recorded | true | `samplesales` | Fact Target, Bill Mth Key | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TARGETS_F.BILL_MTH_KEY] |
| 2 | OFFICE_KEY | DOUBLE | 15 | not recorded | true | `samplesales` | Fact Target, Office Key | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TARGETS_F.OFFICE_KEY] |
| 3 | PROD_KEY | DOUBLE | 15 | not recorded | true | `samplesales` | Fact Target, Prod Key | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TARGETS_F.PROD_KEY] |
| 4 | REVENUE | DOUBLE | 8 | 2 | true | `samplesales` | Fact Target, Revenue | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TARGETS_F.REVENUE] |
| 5 | UNITS | DOUBLE | 15 | not recorded | true | `samplesales` | Fact Target, Units | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TARGETS_F.UNITS] |

Nullable: the export records this only when it is true. "Not stated" means the export does not carry it; it is not a statement that the column must be filled. "Not recorded" means the repository says nothing; it does not mean no, and it does not mean 0.

### Keys

Keys of SAMP_TARGETS_F: not recorded. The repository declares no key for it, so nothing in the repository says which column(s) identify a row (listed in the packet without a Row identifier).

### Foreign keys and joins

- The foreign key "SAMP_TARGETS_F_FKey" from this table (column BILL_MTH_KEY) to SAMP_TIME_MTH_D (column MTH_KEY); stated in full in the section How the tables relate.
- The foreign key "SAMP_TARGETS_F_FKey#1" from this table (column OFFICE_KEY) to SAMP_OFFICES_D (column OFFICE_KEY); stated in full in the section How the tables relate.
- The foreign key "SAMP_TARGETS_F_FKey#2" from this table (column PROD_KEY) to SAMP_PRODUCTS_D (column PROD_KEY); stated in full in the section How the tables relate.

### Read by

The source "SAMP_TARGETS_F" of the business table Fact Target (listed in the packet without a Row identifier).
