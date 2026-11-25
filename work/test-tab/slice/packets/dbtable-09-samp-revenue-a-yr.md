# Physical table "SAMP_REVENUE_A_YR"
Unit: dbtable-09-samp-revenue-a-yr
Kind: physical_table
Tab: test-tab (subject areas: Sample Sales; estate sampleapp)
Facts from: physical_columns, physical_table_definitions, physical_tables
Rows of this packet a document must cover: 6. Each is on a line that ends with (REQUIRED).
To cite a fact, copy the marker in square brackets that follows it, exactly as written. "->" points at another row.

## The table, as the repository knows it

- Physical table "SAMP_REVENUE_A_YR" in `xe.SAMPLESALES` (the containers above it in the repository, outermost first; usually database and schema); type `none`. [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A_YR] (REQUIRED)
    - It is not an alias of another physical table.
    - Description in the repository: not recorded.
    - Its object in the database for database type `DefaultMulDB`: `SAMP_REVENUE_A_YR` [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A_YR.def.DefaultMulDB] (REQUIRED)
- Columns the repository lists for it: 4, of which mapped by a logical column of this slice: 4; not mapped by any: 0. Keys: 0. (counted; no Row identifier)
- Logical table sources of this slice that read it: 1.
    - Source "Aggregate Revenue by Year" of logical table "Fact Revenue" (unit table-01-fact-revenue) -> sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue by Year

## Keys

- Keys of physical table "SAMP_REVENUE_A_YR": not recorded. The repository declares no key for it, so nothing in the repository says which column(s) identify a row. (physical_keys; no Row identifier)

## Foreign keys and joins (each is stated in full in the joins unit)

- Foreign key "SAMP_REVENUE_A_YR_Foreign Key" FROM this table ("BILL_YEAR_KEY") to "SAMP_TIME_MTH_D" ("CAL_YEAR"). -> sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A_YR.SAMP_REVENUE_A_YR_Foreign Key

## Columns, in the order the repository lists them

- Each line below is one column: its place, name, data type, precision (length), scale (digits after the decimal point), whether it may be empty (nullable), its external name, and whether a logical column of this slice is mapped from it. "not recorded" means the repository says nothing; it does not mean no, and it does not mean 0.
- About nullable: the repository export writes the nullable attribute only as true; it never writes false. "nullable not stated" therefore means the attribute is absent: most likely the column is not nullable in the repository, but the file does not say so, and the repository is not proof of the constraint in the database.
- Column 1 "BILL_YEAR_KEY": type DOUBLE; precision not recorded; scale not recorded; nullable true; external name not recorded; MAPPED by logical column(s) "Fact Revenue"."BILL_YEAR_KEY". [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A_YR.BILL_YEAR_KEY] (REQUIRED)
- Column 2 "DISCNT_VALUE": type DOUBLE; precision 22; scale not recorded; nullable true; external name `samplesales`; MAPPED by logical column(s) "Fact Revenue"."DISCNT_VALUE". [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A_YR.DISCNT_VALUE] (REQUIRED)
- Column 3 "REVENUE": type DOUBLE; precision 22; scale not recorded; nullable true; external name `samplesales`; MAPPED by logical column(s) "Fact Revenue"."Revenue". [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A_YR.REVENUE] (REQUIRED)
- Column 4 "UNITS": type DOUBLE; precision 22; scale not recorded; nullable true; external name `samplesales`; MAPPED by logical column(s) "Fact Revenue"."Units". [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A_YR.UNITS] (REQUIRED)

