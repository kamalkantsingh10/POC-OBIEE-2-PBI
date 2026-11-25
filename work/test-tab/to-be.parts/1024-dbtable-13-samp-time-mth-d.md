### New table SAMP_TIME_MTH_D

Proposal (not from OBIEE; to be signed): build SAMP_TIME_MTH_D as a table, like for like from the OBIEE database table "SAMP_TIME_MTH_D", if decision Q8 keeps the month table or the facts held by month need it. The old table is in `xe.SAMPLESALES`, type `none`. [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D] It is not an alias of another physical table. Its object in the database: no separate name is recorded, so the repository knows it only by the name above. Description in the repository: not recorded.

#### Columns

All 36 columns are listed. Unless the row says otherwise, a column is a number of precision 15 with decimal places missing (scale not recorded), may be empty (yes, as recorded), records the external name `samplesales`, and is not mapped by any field.

| # | New column | Proposed type | Length or precision | Decimal places | May be empty | From old column | Old field | Used by tab items | Evidence |
|---|---|---|---|---|---|---|---|---|---|
| 1 | BEG_OF_QTR_WID | number | precision 15 | missing | yes, as recorded | SAMP_TIME_MTH_D.BEG_OF_QTR_WID | not mapped | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.BEG_OF_QTR_WID] |
| 2 | CAL_HALF | number | precision 15 | missing | yes, as recorded | SAMP_TIME_MTH_D.CAL_HALF | not mapped | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.CAL_HALF] |
| 3 | CAL_HALF_YEAR_END_DATE | number | precision 15 | missing | yes, as recorded | SAMP_TIME_MTH_D.CAL_HALF_YEAR_END_DATE | not mapped | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.CAL_HALF_YEAR_END_DATE] |
| 4 | CAL_MONTH | number | precision 15 | missing | yes, as recorded | SAMP_TIME_MTH_D.CAL_MONTH | not mapped | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.CAL_MONTH] |
| 5 | CAL_MONTH_END_DATE | number | precision 15 | missing | yes, as recorded | SAMP_TIME_MTH_D.CAL_MONTH_END_DATE | not mapped | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.CAL_MONTH_END_DATE] |
| 6 | CAL_QTR | number | precision 15 | missing | yes, as recorded | SAMP_TIME_MTH_D.CAL_QTR | not mapped | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.CAL_QTR] |
| 7 | CAL_QTR_END_DATE | number | precision 15 | missing | yes, as recorded | SAMP_TIME_MTH_D.CAL_QTR_END_DATE | not mapped | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.CAL_QTR_END_DATE] |
| 8 | CAL_YEAR | number | precision 15 | missing | yes, as recorded | SAMP_TIME_MTH_D.CAL_YEAR | not mapped | none (second key) | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.CAL_YEAR] |
| 9 | CAL_YEAR_END_DATE | number | precision 15 | missing | yes, as recorded | SAMP_TIME_MTH_D.CAL_YEAR_END_DATE | not mapped | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.CAL_YEAR_END_DATE] |
| 10 | DAYS_IN_FSCL_HALF_YEAR | number | precision 15 | missing | yes, as recorded | SAMP_TIME_MTH_D.DAYS_IN_FSCL_HALF_YEAR | not mapped | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.DAYS_IN_FSCL_HALF_YEAR] |
| 11 | DAYS_IN_FSCL_MONTH | number | precision 15 | missing | yes, as recorded | SAMP_TIME_MTH_D.DAYS_IN_FSCL_MONTH | not mapped | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.DAYS_IN_FSCL_MONTH] |
| 12 | DAYS_IN_FSCL_QTR | number | precision 15 | missing | yes, as recorded | SAMP_TIME_MTH_D.DAYS_IN_FSCL_QTR | not mapped | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.DAYS_IN_FSCL_QTR] |
| 13 | DAYS_IN_FSCL_YEAR | number | precision 15 | missing | yes, as recorded | SAMP_TIME_MTH_D.DAYS_IN_FSCL_YEAR | not mapped | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.DAYS_IN_FSCL_YEAR] |
| 14 | DAYS_IN_HALF_YEAR | number | precision 15 | missing | yes, as recorded | SAMP_TIME_MTH_D.DAYS_IN_HALF_YEAR | not mapped | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.DAYS_IN_HALF_YEAR] |
| 15 | DAYS_IN_MTH | number | precision 15 | missing | yes, as recorded | SAMP_TIME_MTH_D.DAYS_IN_MTH | not mapped | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.DAYS_IN_MTH] |
| 16 | DAYS_IN_QTR | number | precision 15 | missing | yes, as recorded | SAMP_TIME_MTH_D.DAYS_IN_QTR | not mapped | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.DAYS_IN_QTR] |
| 17 | DAYS_IN_YEAR | number | precision 15 | missing | yes, as recorded | SAMP_TIME_MTH_D.DAYS_IN_YEAR | not mapped | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.DAYS_IN_YEAR] |
| 18 | FSCL_HALF | number | precision 15 | missing | yes, as recorded | SAMP_TIME_MTH_D.FSCL_HALF | not mapped | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.FSCL_HALF] |
| 19 | FSCL_HALF_YEAR_END_DATE | number | precision 15 | missing | yes, as recorded | SAMP_TIME_MTH_D.FSCL_HALF_YEAR_END_DATE | not mapped | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.FSCL_HALF_YEAR_END_DATE] |
| 20 | FSCL_MONTH | number | precision 15 | missing | yes, as recorded | SAMP_TIME_MTH_D.FSCL_MONTH | not mapped | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.FSCL_MONTH] |
| 21 | FSCL_MONTH_END_DATE | number | precision 15 | missing | yes, as recorded | SAMP_TIME_MTH_D.FSCL_MONTH_END_DATE | not mapped | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.FSCL_MONTH_END_DATE] |
| 22 | FSCL_QTR | number | precision 15 | missing | yes, as recorded | SAMP_TIME_MTH_D.FSCL_QTR | not mapped | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.FSCL_QTR] |
| 23 | FSCL_QTR_END_DATE | number | precision 15 | missing | yes, as recorded | SAMP_TIME_MTH_D.FSCL_QTR_END_DATE | not mapped | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.FSCL_QTR_END_DATE] |
| 24 | FSCL_YEAR | number | precision 15 | missing | yes, as recorded | SAMP_TIME_MTH_D.FSCL_YEAR | not mapped | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.FSCL_YEAR] |
| 25 | FSCL_YEAR_END_DATE | number | precision 15 | missing | yes, as recorded | SAMP_TIME_MTH_D.FSCL_YEAR_END_DATE | not mapped | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.FSCL_YEAR_END_DATE] |
| 26 | JULIAN_MONTH_NUM | number | precision 15 | missing | yes, as recorded | SAMP_TIME_MTH_D.JULIAN_MONTH_NUM | not mapped | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.JULIAN_MONTH_NUM] |
| 27 | JULIAN_QTR_NUM | number | precision 15 | missing | yes, as recorded | SAMP_TIME_MTH_D.JULIAN_QTR_NUM | not mapped | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.JULIAN_QTR_NUM] |
| 28 | LOADED | number | precision 15 | missing | yes, as recorded | SAMP_TIME_MTH_D.LOADED | not mapped | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.LOADED] |
| 29 | MONTH_AGO_WID | number | precision 15 | missing | yes, as recorded | SAMP_TIME_MTH_D.MONTH_AGO_WID | not mapped | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.MONTH_AGO_WID] |
| 30 | MTH_KEY | number | precision 15 | missing | yes, as recorded | SAMP_TIME_MTH_D.MTH_KEY | not mapped | none (key; links the facts by month) | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.MTH_KEY] |
| 31 | PER_NAME_HALF | text | length 8 | not applicable | yes, as recorded | SAMP_TIME_MTH_D.PER_NAME_HALF | "Dim Time"."Per Name Half" | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.PER_NAME_HALF] |
| 32 | PER_NAME_MONTH | text | length 9 | not applicable | yes, as recorded | SAMP_TIME_MTH_D.PER_NAME_MONTH | "Dim Time"."Per Name Month" | V1-month | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.PER_NAME_MONTH] |
| 33 | PER_NAME_QTR | text | length 7 | not applicable | yes, as recorded | SAMP_TIME_MTH_D.PER_NAME_QTR | "Dim Time"."Per Name Qtr" | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.PER_NAME_QTR] |
| 34 | PER_NAME_YEAR | text | length 4 | not applicable | yes, as recorded | SAMP_TIME_MTH_D.PER_NAME_YEAR | "Dim Time"."Per Name Year" | F1 | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.PER_NAME_YEAR] |
| 35 | QUARTER_AGO_WID | number | precision 15 | missing | yes, as recorded | SAMP_TIME_MTH_D.QUARTER_AGO_WID | not mapped | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.QUARTER_AGO_WID] |
| 36 | YEAR_AGO_WID | number | precision 15 | missing | yes, as recorded | SAMP_TIME_MTH_D.YEAR_AGO_WID | not mapped | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.YEAR_AGO_WID] |

Every column records the external name `samplesales`.

#### Key

The repository declares two keys and does not say which is the primary key: "SAMP_TIME_MTH_D_Key", made of "MTH_KEY" [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.key.SAMP_TIME_MTH_D_Key], and "SAMP_TIME_MTH_D_Key#1", made of "CAL_YEAR" [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.key.SAMP_TIME_MTH_D_Key#1]. Proposal (not from OBIEE; to be signed): MTH_KEY is the key of the new table, because four of the five foreign keys to the table point at it. Whether CAL_YEAR identifies a row is question Q13.

#### Grain

The level of detail is stated in the section Model table Dim Time, source "LTS Time Month".

#### Links to other tables

- Foreign key "SAMP_REVENUE_F_FKey" from SAMP_REVENUE_F (BILL_MTH_KEY) to SAMP_TIME_MTH_D (MTH_KEY): proposed as a foreign key; stated in full under Relationships.
- Foreign key "SAMP_REVENUE_A_FKey" from SAMP_REVENUE_A (BILL_MTH_KEY) to SAMP_TIME_MTH_D (MTH_KEY): proposed as a foreign key; stated in full under Relationships.
- Foreign key "SAMP_REVENUE_A_MTH_Foreign Key" from SAMP_REVENUE_A_MTH (BILL_MTH_KEY) to SAMP_TIME_MTH_D (MTH_KEY): proposed as a foreign key; stated in full under Relationships.
- Foreign key "SAMP_TARGETS_F_FKey" from SAMP_TARGETS_F (BILL_MTH_KEY) to SAMP_TIME_MTH_D (MTH_KEY): proposed as a foreign key; stated in full under Relationships.
- Foreign key "SAMP_REVENUE_A_YR_Foreign Key" from SAMP_REVENUE_A_YR (BILL_YEAR_KEY) to SAMP_TIME_MTH_D (CAL_YEAR): proposed as a foreign key; stated in full under Relationships.

#### Read by

Source "LTS Time Month" of the model table Dim Time.

#### Missing

- Decimal places of all 32 number columns (scale not recorded).
- Which of the two keys is the primary key, and whether CAL_YEAR identifies a row (question Q13).
- Indexes, partitioning, volumes, and how and when the table is loaded.
