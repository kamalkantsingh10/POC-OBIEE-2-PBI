### New table SAMP_TIME_DAY_D

Proposal (not from OBIEE; to be signed): build SAMP_TIME_DAY_D as a table, like for like from the OBIEE database table "SAMP_TIME_DAY_D". The old table is in `xe.SAMPLESALES`, type `none`. [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_DAY_D] It is not an alias of another physical table. Its object in the database for database type `DefaultMulDB`: `SAMP_TIME_DAY_D` [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_DAY_D.def.DefaultMulDB]. Description in the repository: not recorded.

#### Columns

| # | New column | Proposed type | Length or precision | Decimal places | May be empty | From old column | Old field | Used by tab items | Evidence |
|---|---|---|---|---|---|---|---|---|---|
| 1 | CALENDAR_DATE | date | not applicable | not applicable | missing (not stated in the export) | SAMP_TIME_DAY_D.CALENDAR_DATE | "Dim Time"."Calendar Date" | none (key of Dim Time) | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_DAY_D.CALENDAR_DATE] |
| 2 | DAY_KEY | date | not applicable | not applicable | missing (not stated in the export) | SAMP_TIME_DAY_D.DAY_KEY | not mapped | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_DAY_D.DAY_KEY] |
| 3 | DAY_OF_MONTH | number | precision 15 | missing | missing (not stated in the export) | SAMP_TIME_DAY_D.DAY_OF_MONTH | not mapped | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_DAY_D.DAY_OF_MONTH] |
| 4 | DAY_OF_WEEK | number | precision 15 | missing | missing (not stated in the export) | SAMP_TIME_DAY_D.DAY_OF_WEEK | not mapped | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_DAY_D.DAY_OF_WEEK] |
| 5 | DAY_OF_YEAR | number | precision 15 | missing | missing (not stated in the export) | SAMP_TIME_DAY_D.DAY_OF_YEAR | not mapped | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_DAY_D.DAY_OF_YEAR] |
| 6 | JULIAN_DAY_NUM | number | precision 15 | missing | missing (not stated in the export) | SAMP_TIME_DAY_D.JULIAN_DAY_NUM | not mapped | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_DAY_D.JULIAN_DAY_NUM] |
| 7 | JULIAN_MONTH_NUM | number | precision 15 | missing | missing (not stated in the export) | SAMP_TIME_DAY_D.JULIAN_MONTH_NUM | not mapped | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_DAY_D.JULIAN_MONTH_NUM] |
| 8 | JULIAN_QTR_NUM | number | precision 15 | missing | missing (not stated in the export) | SAMP_TIME_DAY_D.JULIAN_QTR_NUM | not mapped | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_DAY_D.JULIAN_QTR_NUM] |
| 9 | JULIAN_WEEK_NUM | number | precision 15 | missing | missing (not stated in the export) | SAMP_TIME_DAY_D.JULIAN_WEEK_NUM | not mapped | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_DAY_D.JULIAN_WEEK_NUM] |
| 10 | PER_NAME_HALF | text | length 8 | not applicable | missing (not stated in the export) | SAMP_TIME_DAY_D.PER_NAME_HALF | "Dim Time"."Per Name Half" | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_DAY_D.PER_NAME_HALF] |
| 11 | PER_NAME_MONTH | text | length 9 | not applicable | missing (not stated in the export) | SAMP_TIME_DAY_D.PER_NAME_MONTH | "Dim Time"."Per Name Month" | V1-month | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_DAY_D.PER_NAME_MONTH] |
| 12 | PER_NAME_QTR | text | length 7 | not applicable | missing (not stated in the export) | SAMP_TIME_DAY_D.PER_NAME_QTR | "Dim Time"."Per Name Qtr" | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_DAY_D.PER_NAME_QTR] |
| 13 | PER_NAME_WEEK | text | length 12 | not applicable | missing (not stated in the export) | SAMP_TIME_DAY_D.PER_NAME_WEEK | "Dim Time"."Per Name Week" | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_DAY_D.PER_NAME_WEEK] |
| 14 | PER_NAME_YEAR | text | length 4 | not applicable | missing (not stated in the export) | SAMP_TIME_DAY_D.PER_NAME_YEAR | "Dim Time"."Per Name Year" | F1 | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_DAY_D.PER_NAME_YEAR] |

Columns 3 to 14 record the external name `samplesales`; CALENDAR_DATE and DAY_KEY record none.

#### Key

Key "SAMP_TIME_DAY_D_Key", made of column(s) "CALENDAR_DATE", the only key the repository declares for the table. [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_DAY_D.key.SAMP_TIME_DAY_D_Key] Proposal (not from OBIEE; to be signed): CALENDAR_DATE is the key of the new table SAMP_TIME_DAY_D.

#### Grain

The level of detail is stated in the section Model table Dim Time, source "LTS Time Day".

#### Links to other tables

- Foreign key "SAMP_REVENUE_F_FKey#3" from SAMP_REVENUE_F (BILL_DAY_DT) to SAMP_TIME_DAY_D (CALENDAR_DATE): proposed as a foreign key; stated in full under Relationships.

#### Read by

Source "LTS Time Day" of the model table Dim Time.

#### Missing

- Decimal places of DAY_OF_MONTH, DAY_OF_WEEK, DAY_OF_YEAR and the four JULIAN columns (scale not recorded).
- Whether any column must be filled (nullable not stated for all fourteen).
- Whether the table holds every day of the period the facts cover (needed if time calculations are added later).
- Indexes, partitioning, volumes, and how and when the table is loaded.
