# Physical table "SAMP_TIME_DAY_D"
Unit: dbtable-12-samp-time-day-d
Kind: physical_table
Tab: test-tab (subject areas: Sample Sales; estate sampleapp)
Facts from: physical_columns, physical_keys, physical_table_definitions, physical_tables
Rows of this packet a document must cover: 17. Each is on a line that ends with (REQUIRED).
To cite a fact, copy the marker in square brackets that follows it, exactly as written. "->" points at another row.

## The table, as the repository knows it

- Physical table "SAMP_TIME_DAY_D" in `xe.SAMPLESALES` (the containers above it in the repository, outermost first; usually database and schema); type `none`. [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_DAY_D] (REQUIRED)
    - It is not an alias of another physical table.
    - Description in the repository: not recorded.
    - Its object in the database for database type `DefaultMulDB`: `SAMP_TIME_DAY_D` [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_DAY_D.def.DefaultMulDB] (REQUIRED)
- Columns the repository lists for it: 14, of which mapped by a logical column of this slice: 6; not mapped by any: 8. Keys: 1. (counted; no Row identifier)
- Logical table sources of this slice that read it: 1.
    - Source "LTS Time Day" of logical table "Dim Time" (unit table-07-dim-time) -> sampleapp::bmm::SAMPLESALES.Dim Time.src.LTS Time Day

## Keys

- Key "SAMP_TIME_DAY_D_Key" of physical table "SAMP_TIME_DAY_D": made of column(s) "CALENDAR_DATE". [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_DAY_D.key.SAMP_TIME_DAY_D_Key] (REQUIRED)
    - Key 1 of 1 the repository declares for the table. The repository does not say whether a physical key is the primary key; this is the only key it declares for the table. Foreign keys of this slice that point at exactly these column(s): 1.

## Foreign keys and joins (each is stated in full in the joins unit)

- Foreign key "SAMP_REVENUE_F_FKey#3" of "SAMP_REVENUE_F" ("BILL_DAY_DT") TO this table ("CALENDAR_DATE"). -> sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F.SAMP_REVENUE_F_FKey#3

## Columns, in the order the repository lists them

- Each line below is one column: its place, name, data type, precision (length), scale (digits after the decimal point), whether it may be empty (nullable), its external name, and whether a logical column of this slice is mapped from it. "not recorded" means the repository says nothing; it does not mean no, and it does not mean 0.
- About nullable: the repository export writes the nullable attribute only as true; it never writes false. "nullable not stated" therefore means the attribute is absent: most likely the column is not nullable in the repository, but the file does not say so, and the repository is not proof of the constraint in the database.
- Column 1 "CALENDAR_DATE": type DATE; precision not recorded; scale not recorded; nullable not stated; external name not recorded; MAPPED by logical column(s) "Dim Time"."Calendar Date". [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_DAY_D.CALENDAR_DATE] (REQUIRED)
- Column 2 "DAY_KEY": type DATE; precision not recorded; scale not recorded; nullable not stated; external name not recorded; NOT MAPPED by any logical column of this slice. [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_DAY_D.DAY_KEY] (REQUIRED)
- Column 3 "DAY_OF_MONTH": type DOUBLE; precision 15; scale not recorded; nullable not stated; external name `samplesales`; NOT MAPPED by any logical column of this slice. [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_DAY_D.DAY_OF_MONTH] (REQUIRED)
- Column 4 "DAY_OF_WEEK": type DOUBLE; precision 15; scale not recorded; nullable not stated; external name `samplesales`; NOT MAPPED by any logical column of this slice. [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_DAY_D.DAY_OF_WEEK] (REQUIRED)
- Column 5 "DAY_OF_YEAR": type DOUBLE; precision 15; scale not recorded; nullable not stated; external name `samplesales`; NOT MAPPED by any logical column of this slice. [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_DAY_D.DAY_OF_YEAR] (REQUIRED)
- Column 6 "JULIAN_DAY_NUM": type DOUBLE; precision 15; scale not recorded; nullable not stated; external name `samplesales`; NOT MAPPED by any logical column of this slice. [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_DAY_D.JULIAN_DAY_NUM] (REQUIRED)
- Column 7 "JULIAN_MONTH_NUM": type DOUBLE; precision 15; scale not recorded; nullable not stated; external name `samplesales`; NOT MAPPED by any logical column of this slice. [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_DAY_D.JULIAN_MONTH_NUM] (REQUIRED)
- Column 8 "JULIAN_QTR_NUM": type DOUBLE; precision 15; scale not recorded; nullable not stated; external name `samplesales`; NOT MAPPED by any logical column of this slice. [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_DAY_D.JULIAN_QTR_NUM] (REQUIRED)
- Column 9 "JULIAN_WEEK_NUM": type DOUBLE; precision 15; scale not recorded; nullable not stated; external name `samplesales`; NOT MAPPED by any logical column of this slice. [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_DAY_D.JULIAN_WEEK_NUM] (REQUIRED)
- Column 10 "PER_NAME_HALF": type VARCHAR; precision 8; scale not recorded; nullable not stated; external name `samplesales`; MAPPED by logical column(s) "Dim Time"."Per Name Half". [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_DAY_D.PER_NAME_HALF] (REQUIRED)
- Column 11 "PER_NAME_MONTH": type VARCHAR; precision 9; scale not recorded; nullable not stated; external name `samplesales`; MAPPED by logical column(s) "Dim Time"."Per Name Month". [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_DAY_D.PER_NAME_MONTH] (REQUIRED)
- Column 12 "PER_NAME_QTR": type VARCHAR; precision 7; scale not recorded; nullable not stated; external name `samplesales`; MAPPED by logical column(s) "Dim Time"."Per Name Qtr". [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_DAY_D.PER_NAME_QTR] (REQUIRED)
- Column 13 "PER_NAME_WEEK": type VARCHAR; precision 12; scale not recorded; nullable not stated; external name `samplesales`; MAPPED by logical column(s) "Dim Time"."Per Name Week". [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_DAY_D.PER_NAME_WEEK] (REQUIRED)
- Column 14 "PER_NAME_YEAR": type VARCHAR; precision 4; scale not recorded; nullable not stated; external name `samplesales`; MAPPED by logical column(s) "Dim Time"."Per Name Year". [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_DAY_D.PER_NAME_YEAR] (REQUIRED)

