# Physical table "SAMP_TARGETS_F"
Unit: dbtable-11-samp-targets-f
Kind: physical_table
Tab: test-tab (subject areas: Sample Sales; estate sampleapp)
Facts from: physical_columns, physical_table_definitions, physical_tables
Rows of this packet a document must cover: 7. Each is on a line that ends with (REQUIRED).
To cite a fact, copy the marker in square brackets that follows it, exactly as written. "->" points at another row.

## The table, as the repository knows it

- Physical table "SAMP_TARGETS_F" in `xe.SAMPLESALES` (the containers above it in the repository, outermost first; usually database and schema); type `none`. [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TARGETS_F] (REQUIRED)
    - It is not an alias of another physical table.
    - Description in the repository: not recorded.
    - Its object in the database for database type `DefaultMulDB`: `SAMP_TARGETS_F` [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TARGETS_F.def.DefaultMulDB] (REQUIRED)
- Columns the repository lists for it: 5, of which mapped by a logical column of this slice: 5; not mapped by any: 0. Keys: 0. (counted; no Row identifier)
- Logical table sources of this slice that read it: 1.
    - Source "SAMP_TARGETS_F" of logical table "Fact Target" (unit table-02-fact-target) -> sampleapp::bmm::SAMPLESALES.Fact Target.src.SAMP_TARGETS_F

## Keys

- Keys of physical table "SAMP_TARGETS_F": not recorded. The repository declares no key for it, so nothing in the repository says which column(s) identify a row. (physical_keys; no Row identifier)

## Foreign keys and joins (each is stated in full in the joins unit)

- Foreign key "SAMP_TARGETS_F_FKey" FROM this table ("BILL_MTH_KEY") to "SAMP_TIME_MTH_D" ("MTH_KEY"). -> sampleapp::phys::xe.SAMPLESALES.SAMP_TARGETS_F.SAMP_TARGETS_F_FKey
- Foreign key "SAMP_TARGETS_F_FKey#1" FROM this table ("OFFICE_KEY") to "SAMP_OFFICES_D" ("OFFICE_KEY"). -> sampleapp::phys::xe.SAMPLESALES.SAMP_TARGETS_F.SAMP_TARGETS_F_FKey#1
- Foreign key "SAMP_TARGETS_F_FKey#2" FROM this table ("PROD_KEY") to "SAMP_PRODUCTS_D" ("PROD_KEY"). -> sampleapp::phys::xe.SAMPLESALES.SAMP_TARGETS_F.SAMP_TARGETS_F_FKey#2

## Columns, in the order the repository lists them

- Each line below is one column: its place, name, data type, precision (length), scale (digits after the decimal point), whether it may be empty (nullable), its external name, and whether a logical column of this slice is mapped from it. "not recorded" means the repository says nothing; it does not mean no, and it does not mean 0.
- About nullable: the repository export writes the nullable attribute only as true; it never writes false. "nullable not stated" therefore means the attribute is absent: most likely the column is not nullable in the repository, but the file does not say so, and the repository is not proof of the constraint in the database.
- Column 1 "BILL_MTH_KEY": type DOUBLE; precision 15; scale not recorded; nullable true; external name `samplesales`; MAPPED by logical column(s) "Fact Target"."Bill Mth Key". [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TARGETS_F.BILL_MTH_KEY] (REQUIRED)
- Column 2 "OFFICE_KEY": type DOUBLE; precision 15; scale not recorded; nullable true; external name `samplesales`; MAPPED by logical column(s) "Fact Target"."Office Key". [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TARGETS_F.OFFICE_KEY] (REQUIRED)
- Column 3 "PROD_KEY": type DOUBLE; precision 15; scale not recorded; nullable true; external name `samplesales`; MAPPED by logical column(s) "Fact Target"."Prod Key". [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TARGETS_F.PROD_KEY] (REQUIRED)
- Column 4 "REVENUE": type DOUBLE; precision 8; scale 2; nullable true; external name `samplesales`; MAPPED by logical column(s) "Fact Target"."Revenue". [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TARGETS_F.REVENUE] (REQUIRED)
- Column 5 "UNITS": type DOUBLE; precision 15; scale not recorded; nullable true; external name `samplesales`; MAPPED by logical column(s) "Fact Target"."Units". [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TARGETS_F.UNITS] (REQUIRED)

