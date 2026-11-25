# Physical table "SAMP_TIME_MTH_D"
Unit: dbtable-13-samp-time-mth-d
Kind: physical_table
Tab: test-tab (subject areas: Sample Sales; estate sampleapp)
Facts from: physical_columns, physical_keys, physical_tables
Rows of this packet a document must cover: 39. Each is on a line that ends with (REQUIRED).
To cite a fact, copy the marker in square brackets that follows it, exactly as written. "->" points at another row.

## The table, as the repository knows it

- Physical table "SAMP_TIME_MTH_D" in `xe.SAMPLESALES` (the containers above it in the repository, outermost first; usually database and schema); type `none`. [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D] (REQUIRED)
    - It is not an alias of another physical table.
    - Description in the repository: not recorded.
    - Its object in the database: no separate name is recorded, so the repository knows it only by the name above. (physical_table_definitions; no Row identifier)
- Columns the repository lists for it: 36, of which mapped by a logical column of this slice: 4; not mapped by any: 32. Keys: 2. (counted; no Row identifier)
- Logical table sources of this slice that read it: 1.
    - Source "LTS Time Month" of logical table "Dim Time" (unit table-07-dim-time) -> sampleapp::bmm::SAMPLESALES.Dim Time.src.LTS Time Month

## Keys

- Key "SAMP_TIME_MTH_D_Key" of physical table "SAMP_TIME_MTH_D": made of column(s) "MTH_KEY". [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.key.SAMP_TIME_MTH_D_Key] (REQUIRED)
    - Key 1 of 2 the repository declares for the table. The repository does not say whether a physical key is the primary key. Foreign keys of this slice that point at exactly these column(s): 4.
- Key "SAMP_TIME_MTH_D_Key#1" of physical table "SAMP_TIME_MTH_D": made of column(s) "CAL_YEAR". [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.key.SAMP_TIME_MTH_D_Key#1] (REQUIRED)
    - Key 2 of 2 the repository declares for the table. The repository does not say whether a physical key is the primary key. Foreign keys of this slice that point at exactly these column(s): 1.

## Foreign keys and joins (each is stated in full in the joins unit)

- Foreign key "SAMP_REVENUE_A_FKey" of "SAMP_REVENUE_A" ("BILL_MTH_KEY") TO this table ("MTH_KEY"). -> sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A.SAMP_REVENUE_A_FKey
- Foreign key "SAMP_REVENUE_A_MTH_Foreign Key" of "SAMP_REVENUE_A_MTH" ("BILL_MTH_KEY") TO this table ("MTH_KEY"). -> sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A_MTH.SAMP_REVENUE_A_MTH_Foreign Key
- Foreign key "SAMP_REVENUE_A_YR_Foreign Key" of "SAMP_REVENUE_A_YR" ("BILL_YEAR_KEY") TO this table ("CAL_YEAR"). -> sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A_YR.SAMP_REVENUE_A_YR_Foreign Key
- Foreign key "SAMP_REVENUE_F_FKey" of "SAMP_REVENUE_F" ("BILL_MTH_KEY") TO this table ("MTH_KEY"). -> sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F.SAMP_REVENUE_F_FKey
- Foreign key "SAMP_TARGETS_F_FKey" of "SAMP_TARGETS_F" ("BILL_MTH_KEY") TO this table ("MTH_KEY"). -> sampleapp::phys::xe.SAMPLESALES.SAMP_TARGETS_F.SAMP_TARGETS_F_FKey

## Columns, in the order the repository lists them

- Each line below is one column: its place, name, data type, precision (length), scale (digits after the decimal point), whether it may be empty (nullable), its external name, and whether a logical column of this slice is mapped from it. "not recorded" means the repository says nothing; it does not mean no, and it does not mean 0.
- About nullable: the repository export writes the nullable attribute only as true; it never writes false. "nullable not stated" therefore means the attribute is absent: most likely the column is not nullable in the repository, but the file does not say so, and the repository is not proof of the constraint in the database.
- Column 1 "BEG_OF_QTR_WID": type DOUBLE; precision 15; scale not recorded; nullable true; external name `samplesales`; NOT MAPPED by any logical column of this slice. [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.BEG_OF_QTR_WID] (REQUIRED)
- Column 2 "CAL_HALF": type DOUBLE; precision 15; scale not recorded; nullable true; external name `samplesales`; NOT MAPPED by any logical column of this slice. [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.CAL_HALF] (REQUIRED)
- Column 3 "CAL_HALF_YEAR_END_DATE": type DOUBLE; precision 15; scale not recorded; nullable true; external name `samplesales`; NOT MAPPED by any logical column of this slice. [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.CAL_HALF_YEAR_END_DATE] (REQUIRED)
- Column 4 "CAL_MONTH": type DOUBLE; precision 15; scale not recorded; nullable true; external name `samplesales`; NOT MAPPED by any logical column of this slice. [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.CAL_MONTH] (REQUIRED)
- Column 5 "CAL_MONTH_END_DATE": type DOUBLE; precision 15; scale not recorded; nullable true; external name `samplesales`; NOT MAPPED by any logical column of this slice. [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.CAL_MONTH_END_DATE] (REQUIRED)
- Column 6 "CAL_QTR": type DOUBLE; precision 15; scale not recorded; nullable true; external name `samplesales`; NOT MAPPED by any logical column of this slice. [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.CAL_QTR] (REQUIRED)
- Column 7 "CAL_QTR_END_DATE": type DOUBLE; precision 15; scale not recorded; nullable true; external name `samplesales`; NOT MAPPED by any logical column of this slice. [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.CAL_QTR_END_DATE] (REQUIRED)
- Column 8 "CAL_YEAR": type DOUBLE; precision 15; scale not recorded; nullable true; external name `samplesales`; NOT MAPPED by any logical column of this slice. [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.CAL_YEAR] (REQUIRED)
- Column 9 "CAL_YEAR_END_DATE": type DOUBLE; precision 15; scale not recorded; nullable true; external name `samplesales`; NOT MAPPED by any logical column of this slice. [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.CAL_YEAR_END_DATE] (REQUIRED)
- Column 10 "DAYS_IN_FSCL_HALF_YEAR": type DOUBLE; precision 15; scale not recorded; nullable true; external name `samplesales`; NOT MAPPED by any logical column of this slice. [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.DAYS_IN_FSCL_HALF_YEAR] (REQUIRED)
- Column 11 "DAYS_IN_FSCL_MONTH": type DOUBLE; precision 15; scale not recorded; nullable true; external name `samplesales`; NOT MAPPED by any logical column of this slice. [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.DAYS_IN_FSCL_MONTH] (REQUIRED)
- Column 12 "DAYS_IN_FSCL_QTR": type DOUBLE; precision 15; scale not recorded; nullable true; external name `samplesales`; NOT MAPPED by any logical column of this slice. [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.DAYS_IN_FSCL_QTR] (REQUIRED)
- Column 13 "DAYS_IN_FSCL_YEAR": type DOUBLE; precision 15; scale not recorded; nullable true; external name `samplesales`; NOT MAPPED by any logical column of this slice. [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.DAYS_IN_FSCL_YEAR] (REQUIRED)
- Column 14 "DAYS_IN_HALF_YEAR": type DOUBLE; precision 15; scale not recorded; nullable true; external name `samplesales`; NOT MAPPED by any logical column of this slice. [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.DAYS_IN_HALF_YEAR] (REQUIRED)
- Column 15 "DAYS_IN_MTH": type DOUBLE; precision 15; scale not recorded; nullable true; external name `samplesales`; NOT MAPPED by any logical column of this slice. [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.DAYS_IN_MTH] (REQUIRED)
- Column 16 "DAYS_IN_QTR": type DOUBLE; precision 15; scale not recorded; nullable true; external name `samplesales`; NOT MAPPED by any logical column of this slice. [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.DAYS_IN_QTR] (REQUIRED)
- Column 17 "DAYS_IN_YEAR": type DOUBLE; precision 15; scale not recorded; nullable true; external name `samplesales`; NOT MAPPED by any logical column of this slice. [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.DAYS_IN_YEAR] (REQUIRED)
- Column 18 "FSCL_HALF": type DOUBLE; precision 15; scale not recorded; nullable true; external name `samplesales`; NOT MAPPED by any logical column of this slice. [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.FSCL_HALF] (REQUIRED)
- Column 19 "FSCL_HALF_YEAR_END_DATE": type DOUBLE; precision 15; scale not recorded; nullable true; external name `samplesales`; NOT MAPPED by any logical column of this slice. [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.FSCL_HALF_YEAR_END_DATE] (REQUIRED)
- Column 20 "FSCL_MONTH": type DOUBLE; precision 15; scale not recorded; nullable true; external name `samplesales`; NOT MAPPED by any logical column of this slice. [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.FSCL_MONTH] (REQUIRED)
- Column 21 "FSCL_MONTH_END_DATE": type DOUBLE; precision 15; scale not recorded; nullable true; external name `samplesales`; NOT MAPPED by any logical column of this slice. [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.FSCL_MONTH_END_DATE] (REQUIRED)
- Column 22 "FSCL_QTR": type DOUBLE; precision 15; scale not recorded; nullable true; external name `samplesales`; NOT MAPPED by any logical column of this slice. [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.FSCL_QTR] (REQUIRED)
- Column 23 "FSCL_QTR_END_DATE": type DOUBLE; precision 15; scale not recorded; nullable true; external name `samplesales`; NOT MAPPED by any logical column of this slice. [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.FSCL_QTR_END_DATE] (REQUIRED)
- Column 24 "FSCL_YEAR": type DOUBLE; precision 15; scale not recorded; nullable true; external name `samplesales`; NOT MAPPED by any logical column of this slice. [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.FSCL_YEAR] (REQUIRED)
- Column 25 "FSCL_YEAR_END_DATE": type DOUBLE; precision 15; scale not recorded; nullable true; external name `samplesales`; NOT MAPPED by any logical column of this slice. [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.FSCL_YEAR_END_DATE] (REQUIRED)
- Column 26 "JULIAN_MONTH_NUM": type DOUBLE; precision 15; scale not recorded; nullable true; external name `samplesales`; NOT MAPPED by any logical column of this slice. [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.JULIAN_MONTH_NUM] (REQUIRED)
- Column 27 "JULIAN_QTR_NUM": type DOUBLE; precision 15; scale not recorded; nullable true; external name `samplesales`; NOT MAPPED by any logical column of this slice. [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.JULIAN_QTR_NUM] (REQUIRED)
- Column 28 "LOADED": type DOUBLE; precision 15; scale not recorded; nullable true; external name `samplesales`; NOT MAPPED by any logical column of this slice. [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.LOADED] (REQUIRED)
- Column 29 "MONTH_AGO_WID": type DOUBLE; precision 15; scale not recorded; nullable true; external name `samplesales`; NOT MAPPED by any logical column of this slice. [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.MONTH_AGO_WID] (REQUIRED)
- Column 30 "MTH_KEY": type DOUBLE; precision 15; scale not recorded; nullable true; external name `samplesales`; NOT MAPPED by any logical column of this slice. [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.MTH_KEY] (REQUIRED)
- Column 31 "PER_NAME_HALF": type VARCHAR; precision 8; scale not recorded; nullable true; external name `samplesales`; MAPPED by logical column(s) "Dim Time"."Per Name Half". [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.PER_NAME_HALF] (REQUIRED)
- Column 32 "PER_NAME_MONTH": type VARCHAR; precision 9; scale not recorded; nullable true; external name `samplesales`; MAPPED by logical column(s) "Dim Time"."Per Name Month". [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.PER_NAME_MONTH] (REQUIRED)
- Column 33 "PER_NAME_QTR": type VARCHAR; precision 7; scale not recorded; nullable true; external name `samplesales`; MAPPED by logical column(s) "Dim Time"."Per Name Qtr". [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.PER_NAME_QTR] (REQUIRED)
- Column 34 "PER_NAME_YEAR": type VARCHAR; precision 4; scale not recorded; nullable true; external name `samplesales`; MAPPED by logical column(s) "Dim Time"."Per Name Year". [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.PER_NAME_YEAR] (REQUIRED)
- Column 35 "QUARTER_AGO_WID": type DOUBLE; precision 15; scale not recorded; nullable true; external name `samplesales`; NOT MAPPED by any logical column of this slice. [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.QUARTER_AGO_WID] (REQUIRED)
- Column 36 "YEAR_AGO_WID": type DOUBLE; precision 15; scale not recorded; nullable true; external name `samplesales`; NOT MAPPED by any logical column of this slice. [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.YEAR_AGO_WID] (REQUIRED)

