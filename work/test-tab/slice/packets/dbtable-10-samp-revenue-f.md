# Physical table "SAMP_REVENUE_F"
Unit: dbtable-10-samp-revenue-f
Kind: physical_table
Tab: test-tab (subject areas: Sample Sales; estate sampleapp)
Facts from: physical_columns, physical_table_definitions, physical_tables
Rows of this packet a document must cover: 14. Each is on a line that ends with (REQUIRED).
To cite a fact, copy the marker in square brackets that follows it, exactly as written. "->" points at another row.

## The table, as the repository knows it

- Physical table "SAMP_REVENUE_F" in `xe.SAMPLESALES` (the containers above it in the repository, outermost first; usually database and schema); type `none`. [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F] (REQUIRED)
    - It is not an alias of another physical table.
    - Description in the repository: not recorded.
    - Its object in the database for database type `DefaultMulDB`: `SAMP_REVENUE_F` [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F.def.DefaultMulDB] (REQUIRED)
- Columns the repository lists for it: 12, of which mapped by a logical column of this slice: 12; not mapped by any: 0. Keys: 0. (counted; no Row identifier)
- Logical table sources of this slice that read it: 1.
    - Source "SAMP_REVENUE_F" of logical table "Fact Revenue" (unit table-01-fact-revenue) -> sampleapp::bmm::SAMPLESALES.Fact Revenue.src.SAMP_REVENUE_F

## Keys

- Keys of physical table "SAMP_REVENUE_F": not recorded. The repository declares no key for it, so nothing in the repository says which column(s) identify a row. (physical_keys; no Row identifier)

## Foreign keys and joins (each is stated in full in the joins unit)

- Foreign key "SAMP_REVENUE_F_FKey" FROM this table ("BILL_MTH_KEY") to "SAMP_TIME_MTH_D" ("MTH_KEY"). -> sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F.SAMP_REVENUE_F_FKey
- Foreign key "SAMP_REVENUE_F_FKey#1" FROM this table ("OFFICE_KEY") to "SAMP_OFFICES_D" ("OFFICE_KEY"). -> sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F.SAMP_REVENUE_F_FKey#1
- Foreign key "SAMP_REVENUE_F_FKey#2" FROM this table ("PROD_KEY") to "SAMP_PRODUCTS_D" ("PROD_KEY"). -> sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F.SAMP_REVENUE_F_FKey#2
- Foreign key "SAMP_REVENUE_F_FKey#3" FROM this table ("BILL_DAY_DT") to "SAMP_TIME_DAY_D" ("CALENDAR_DATE"). -> sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F.SAMP_REVENUE_F_FKey#3
- Foreign key "SAMP_REVENUE_F_FKey#4" FROM this table ("EMPLOYEE_KEY") to "EMP_PARENT_CHILD" ("MEMBER_KEY"). -> sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F.SAMP_REVENUE_F_FKey#4

## Columns, in the order the repository lists them

- Each line below is one column: its place, name, data type, precision (length), scale (digits after the decimal point), whether it may be empty (nullable), its external name, and whether a logical column of this slice is mapped from it. "not recorded" means the repository says nothing; it does not mean no, and it does not mean 0.
- About nullable: the repository export writes the nullable attribute only as true; it never writes false. "nullable not stated" therefore means the attribute is absent: most likely the column is not nullable in the repository, but the file does not say so, and the repository is not proof of the constraint in the database.
- Column 1 "BILL_DAY_DT": type DATE; precision not recorded; scale not recorded; nullable true; external name not recorded; MAPPED by logical column(s) "Fact Revenue"."Bill Day Dt". [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F.BILL_DAY_DT] (REQUIRED)
- Column 2 "BILL_MTH_KEY": type DOUBLE; precision 15; scale not recorded; nullable true; external name `samplesales`; MAPPED by logical column(s) "Fact Revenue"."Bill Mth Key". [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F.BILL_MTH_KEY] (REQUIRED)
- Column 3 "DISCNT_VALUE": type DOUBLE; precision 6; scale 2; nullable true; external name `samplesales`; MAPPED by logical column(s) "Fact Revenue"."Discnt Value". [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F.DISCNT_VALUE] (REQUIRED)
- Column 4 "OFFICE_KEY": type DOUBLE; precision 15; scale not recorded; nullable true; external name `samplesales`; MAPPED by logical column(s) "Fact Revenue"."Office Key". [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F.OFFICE_KEY] (REQUIRED)
- Column 5 "ORDER_DAY_DT": type DATETIME; precision not recorded; scale not recorded; nullable true; external name `samplesales`; MAPPED by logical column(s) "Fact Revenue"."Order Day Dt". [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F.ORDER_DAY_DT] (REQUIRED)
- Column 6 "ORDER_NUMBER": type DOUBLE; precision 15; scale not recorded; nullable true; external name `samplesales`; MAPPED by logical column(s) "Fact Revenue"."Order Number". [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F.ORDER_NUMBER] (REQUIRED)
- Column 7 "ORDER_STATUS": type VARCHAR; precision 11; scale not recorded; nullable true; external name `samplesales`; MAPPED by logical column(s) "Fact Revenue"."Order Status". [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F.ORDER_STATUS] (REQUIRED)
- Column 8 "ORDER_TYPE": type VARCHAR; precision 8; scale not recorded; nullable true; external name `samplesales`; MAPPED by logical column(s) "Fact Revenue"."Order Type". [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F.ORDER_TYPE] (REQUIRED)
- Column 9 "PROD_KEY": type DOUBLE; precision 15; scale not recorded; nullable true; external name `samplesales`; MAPPED by logical column(s) "Fact Revenue"."Prod Key". [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F.PROD_KEY] (REQUIRED)
- Column 10 "REVENUE": type DOUBLE; precision 7; scale 2; nullable true; external name `samplesales`; MAPPED by logical column(s) "Fact Revenue"."All Products Revenue", "Fact Revenue"."All Months Revenue", "Fact Revenue"."Revenue (GBP)", "Fact Revenue"."Revenue (EUR)", "Fact Revenue"."Revenue (INR)", "Fact Revenue"."Revenue". [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F.REVENUE] (REQUIRED)
- Column 11 "UNITS": type DOUBLE; precision 15; scale not recorded; nullable true; external name `samplesales`; MAPPED by logical column(s) "Fact Revenue"."Units". [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F.UNITS] (REQUIRED)
- Column 12 "EMPLOYEE_KEY": type INT; precision not recorded; scale not recorded; nullable not stated; external name not recorded; MAPPED by logical column(s) "Fact Revenue"."Sales Employee Key". [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F.EMPLOYEE_KEY] (REQUIRED)

