## Database table SAMP_TIME_DAY_D

SAMP_TIME_DAY_D is a database table in `xe.SAMPLESALES`, type `none`; it is not an alias of another table, and no description is recorded [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_DAY_D]. Its object in the database, for database type `DefaultMulDB`, is `SAMP_TIME_DAY_D` [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_DAY_D.def.DefaultMulDB]. The export lists 14 columns for it, 6 mapped by a field of this subject area and 8 not, and 1 key (counted).

### Columns

| # | Column | Type | Precision | Scale | Nullable | External name | Mapped by | Evidence |
|---|---|---|---|---|---|---|---|---|
| 1 | CALENDAR_DATE | DATE | not recorded | not recorded | not stated | not recorded | Dim Time, Calendar Date | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_DAY_D.CALENDAR_DATE] |
| 2 | DAY_KEY | DATE | not recorded | not recorded | not stated | not recorded | not mapped | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_DAY_D.DAY_KEY] |
| 3 | DAY_OF_MONTH | DOUBLE | 15 | not recorded | not stated | `samplesales` | not mapped | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_DAY_D.DAY_OF_MONTH] |
| 4 | DAY_OF_WEEK | DOUBLE | 15 | not recorded | not stated | `samplesales` | not mapped | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_DAY_D.DAY_OF_WEEK] |
| 5 | DAY_OF_YEAR | DOUBLE | 15 | not recorded | not stated | `samplesales` | not mapped | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_DAY_D.DAY_OF_YEAR] |
| 6 | JULIAN_DAY_NUM | DOUBLE | 15 | not recorded | not stated | `samplesales` | not mapped | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_DAY_D.JULIAN_DAY_NUM] |
| 7 | JULIAN_MONTH_NUM | DOUBLE | 15 | not recorded | not stated | `samplesales` | not mapped | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_DAY_D.JULIAN_MONTH_NUM] |
| 8 | JULIAN_QTR_NUM | DOUBLE | 15 | not recorded | not stated | `samplesales` | not mapped | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_DAY_D.JULIAN_QTR_NUM] |
| 9 | JULIAN_WEEK_NUM | DOUBLE | 15 | not recorded | not stated | `samplesales` | not mapped | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_DAY_D.JULIAN_WEEK_NUM] |
| 10 | PER_NAME_HALF | VARCHAR | 8 | not recorded | not stated | `samplesales` | Dim Time, Per Name Half | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_DAY_D.PER_NAME_HALF] |
| 11 | PER_NAME_MONTH | VARCHAR | 9 | not recorded | not stated | `samplesales` | Dim Time, Per Name Month | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_DAY_D.PER_NAME_MONTH] |
| 12 | PER_NAME_QTR | VARCHAR | 7 | not recorded | not stated | `samplesales` | Dim Time, Per Name Qtr | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_DAY_D.PER_NAME_QTR] |
| 13 | PER_NAME_WEEK | VARCHAR | 12 | not recorded | not stated | `samplesales` | Dim Time, Per Name Week | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_DAY_D.PER_NAME_WEEK] |
| 14 | PER_NAME_YEAR | VARCHAR | 4 | not recorded | not stated | `samplesales` | Dim Time, Per Name Year | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_DAY_D.PER_NAME_YEAR] |

Nullable: the export records this only when it is true. "Not stated" means the export does not carry it; it is not a statement that the column must be filled. "Not recorded" means the repository says nothing; it does not mean no, and it does not mean 0.

### Keys

| Key | Columns | Evidence |
|---|---|---|
| SAMP_TIME_DAY_D_Key | CALENDAR_DATE | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_DAY_D.key.SAMP_TIME_DAY_D_Key] |

The repository does not say whether a physical key is the primary key; this is the only key it declares for the table. One foreign key of this subject area points at exactly CALENDAR_DATE [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_DAY_D.key.SAMP_TIME_DAY_D_Key].

### Foreign keys and joins

- The foreign key "SAMP_REVENUE_F_FKey#3" from SAMP_REVENUE_F (column BILL_DAY_DT) to this table (column CALENDAR_DATE); stated in full in the section How the tables relate.

### Read by

The source "LTS Time Day" of the business table Dim Time (listed in the packet without a Row identifier).
