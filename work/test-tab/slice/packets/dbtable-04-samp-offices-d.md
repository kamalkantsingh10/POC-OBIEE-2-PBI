# Physical table "SAMP_OFFICES_D"
Unit: dbtable-04-samp-offices-d
Kind: physical_table
Tab: test-tab (subject areas: Sample Sales; estate sampleapp)
Facts from: physical_columns, physical_keys, physical_tables
Rows of this packet a document must cover: 12. Each is on a line that ends with (REQUIRED).
To cite a fact, copy the marker in square brackets that follows it, exactly as written. "->" points at another row.

## The table, as the repository knows it

- Physical table "SAMP_OFFICES_D" in `xe.SAMPLESALES` (the containers above it in the repository, outermost first; usually database and schema); type `none`. [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_OFFICES_D] (REQUIRED)
    - It is not an alias of another physical table.
    - Description in the repository: not recorded.
    - Its object in the database: no separate name is recorded, so the repository knows it only by the name above. (physical_table_definitions; no Row identifier)
- Columns the repository lists for it: 10, of which mapped by a logical column of this slice: 10; not mapped by any: 0. Keys: 1. (counted; no Row identifier)
- Logical table sources of this slice that read it: 1.
    - Source "SAMP_OFFICES_D" of logical table "Dim Offices" (unit table-03-dim-offices) -> sampleapp::bmm::SAMPLESALES.Dim Offices.src.SAMP_OFFICES_D

## Keys

- Key "SAMP_OFFICES_D_Key" of physical table "SAMP_OFFICES_D": made of column(s) "OFFICE_KEY". [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_OFFICES_D.key.SAMP_OFFICES_D_Key] (REQUIRED)
    - Key 1 of 1 the repository declares for the table. The repository does not say whether a physical key is the primary key; this is the only key it declares for the table. Foreign keys of this slice that point at exactly these column(s): 2.

## Foreign keys and joins (each is stated in full in the joins unit)

- complex_join "Complex Join" between "FactCrossJoin" and "SAMP_OFFICES_D"; its condition is in the joins unit. -> sampleapp::phys::joins.Complex Join
- Foreign key "SAMP_REVENUE_F_FKey#1" of "SAMP_REVENUE_F" ("OFFICE_KEY") TO this table ("OFFICE_KEY"). -> sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F.SAMP_REVENUE_F_FKey#1
- Foreign key "SAMP_TARGETS_F_FKey#1" of "SAMP_TARGETS_F" ("OFFICE_KEY") TO this table ("OFFICE_KEY"). -> sampleapp::phys::xe.SAMPLESALES.SAMP_TARGETS_F.SAMP_TARGETS_F_FKey#1

## Columns, in the order the repository lists them

- Each line below is one column: its place, name, data type, precision (length), scale (digits after the decimal point), whether it may be empty (nullable), its external name, and whether a logical column of this slice is mapped from it. "not recorded" means the repository says nothing; it does not mean no, and it does not mean 0.
- About nullable: the repository export writes the nullable attribute only as true; it never writes false. "nullable not stated" therefore means the attribute is absent: most likely the column is not nullable in the repository, but the file does not say so, and the repository is not proof of the constraint in the database.
- Column 1 "COMPANY": type VARCHAR; precision 14; scale not recorded; nullable true; external name `samplesales`; MAPPED by logical column(s) "Dim Offices"."Company". [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_OFFICES_D.COMPANY] (REQUIRED)
- Column 2 "COMPANY_KEY": type DOUBLE; precision 15; scale not recorded; nullable not stated; external name `samplesales`; MAPPED by logical column(s) "Dim Offices"."Company Key". [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_OFFICES_D.COMPANY_KEY] (REQUIRED)
- Column 3 "DEPARTMENT": type VARCHAR; precision 25; scale not recorded; nullable true; external name `samplesales`; MAPPED by logical column(s) "Dim Offices"."Department". [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_OFFICES_D.DEPARTMENT] (REQUIRED)
- Column 4 "DEPT_KEY": type DOUBLE; precision 15; scale not recorded; nullable not stated; external name `samplesales`; MAPPED by logical column(s) "Dim Offices"."Dept Key". [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_OFFICES_D.DEPT_KEY] (REQUIRED)
- Column 5 "OFFICE_DSC": type VARCHAR; precision 17; scale not recorded; nullable true; external name `samplesales`; MAPPED by logical column(s) "Dim Offices"."Office Dsc". [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_OFFICES_D.OFFICE_DSC] (REQUIRED)
- Column 6 "OFFICE_KEY": type DOUBLE; precision 15; scale not recorded; nullable not stated; external name `samplesales`; MAPPED by logical column(s) "Dim Offices"."Office Key". [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_OFFICES_D.OFFICE_KEY] (REQUIRED)
- Column 7 "ORGANIZATION": type VARCHAR; precision 18; scale not recorded; nullable true; external name `samplesales`; MAPPED by logical column(s) "Dim Offices"."Organization". [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_OFFICES_D.ORGANIZATION] (REQUIRED)
- Column 8 "ORG_KEY": type DOUBLE; precision 15; scale not recorded; nullable not stated; external name `samplesales`; MAPPED by logical column(s) "Dim Offices"."Org Key". [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_OFFICES_D.ORG_KEY] (REQUIRED)
- Column 9 "SEQUENCE": type DOUBLE; precision 15; scale not recorded; nullable true; external name `samplesales`; MAPPED by logical column(s) "Dim Offices"."Sequence". [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_OFFICES_D.SEQUENCE] (REQUIRED)
- Column 10 "AREA_NAME": type VARCHAR; precision 25; scale not recorded; nullable true; external name not recorded; MAPPED by logical column(s) "Dim Offices"."Area Name". [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_OFFICES_D.AREA_NAME] (REQUIRED)

