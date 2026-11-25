# Physical table "SAMP_PRODUCTS_D"
Unit: dbtable-05-samp-products-d
Kind: physical_table
Tab: test-tab (subject areas: Sample Sales; estate sampleapp)
Facts from: physical_columns, physical_keys, physical_tables
Rows of this packet a document must cover: 13. Each is on a line that ends with (REQUIRED).
To cite a fact, copy the marker in square brackets that follows it, exactly as written. "->" points at another row.

## The table, as the repository knows it

- Physical table "SAMP_PRODUCTS_D" in `xe.SAMPLESALES` (the containers above it in the repository, outermost first; usually database and schema); type `none`. [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_PRODUCTS_D] (REQUIRED)
    - It is not an alias of another physical table.
    - Description in the repository: not recorded.
    - Its object in the database: no separate name is recorded, so the repository knows it only by the name above. (physical_table_definitions; no Row identifier)
- Columns the repository lists for it: 11, of which mapped by a logical column of this slice: 11; not mapped by any: 0. Keys: 1. (counted; no Row identifier)
- Logical table sources of this slice that read it: 1.
    - Source "SAMP_PRODUCTS_D" of logical table "Dim Products" (unit table-04-dim-products) -> sampleapp::bmm::SAMPLESALES.Dim Products.src.SAMP_PRODUCTS_D

## Keys

- Key "SAMP_PRODUCTS_D_Key" of physical table "SAMP_PRODUCTS_D": made of column(s) "PROD_KEY". [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_PRODUCTS_D.key.SAMP_PRODUCTS_D_Key] (REQUIRED)
    - Key 1 of 1 the repository declares for the table. The repository does not say whether a physical key is the primary key; this is the only key it declares for the table. Foreign keys of this slice that point at exactly these column(s): 4.

## Foreign keys and joins (each is stated in full in the joins unit)

- complex_join "Complex Join#1" between "FactCrossJoin" and "SAMP_PRODUCTS_D"; its condition is in the joins unit. -> sampleapp::phys::joins.Complex Join#1
- Foreign key "SAMP_PRODUCTS_D_TRANSLATION_FKey" of "SAMP_PRODUCTS_D_TRANSLATION" ("PROD_KEY") TO this table ("PROD_KEY"). -> sampleapp::phys::xe.SAMPLESALES.SAMP_PRODUCTS_D_TRANSLATION.SAMP_PRODUCTS_D_TRANSLATION_FKey
- Foreign key "SAMP_REVENUE_A_FKey#1" of "SAMP_REVENUE_A" ("PROD_KEY") TO this table ("PROD_KEY"). -> sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A.SAMP_REVENUE_A_FKey#1
- Foreign key "SAMP_REVENUE_F_FKey#2" of "SAMP_REVENUE_F" ("PROD_KEY") TO this table ("PROD_KEY"). -> sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F.SAMP_REVENUE_F_FKey#2
- Foreign key "SAMP_TARGETS_F_FKey#2" of "SAMP_TARGETS_F" ("PROD_KEY") TO this table ("PROD_KEY"). -> sampleapp::phys::xe.SAMPLESALES.SAMP_TARGETS_F.SAMP_TARGETS_F_FKey#2

## Columns, in the order the repository lists them

- Each line below is one column: its place, name, data type, precision (length), scale (digits after the decimal point), whether it may be empty (nullable), its external name, and whether a logical column of this slice is mapped from it. "not recorded" means the repository says nothing; it does not mean no, and it does not mean 0.
- About nullable: the repository export writes the nullable attribute only as true; it never writes false. "nullable not stated" therefore means the attribute is absent: most likely the column is not nullable in the repository, but the file does not say so, and the repository is not proof of the constraint in the database.
- Column 1 "ATTRIBUTE_1": type VARCHAR; precision 7; scale not recorded; nullable true; external name `samplesales`; MAPPED by logical column(s) "Dim Products"."Attribute 1". [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_PRODUCTS_D.ATTRIBUTE_1] (REQUIRED)
- Column 2 "ATTRIBUTE_2": type VARCHAR; precision 6; scale not recorded; nullable true; external name `samplesales`; MAPPED by logical column(s) "Dim Products"."Attribute 2". [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_PRODUCTS_D.ATTRIBUTE_2] (REQUIRED)
- Column 3 "BRAND": type VARCHAR; precision 8; scale not recorded; nullable true; external name `samplesales`; MAPPED by logical column(s) "Dim Products"."Brand". [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_PRODUCTS_D.BRAND] (REQUIRED)
- Column 4 "BRAND_KEY": type DOUBLE; precision 15; scale not recorded; nullable not stated; external name `samplesales`; MAPPED by logical column(s) "Dim Products"."Brand Key". [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_PRODUCTS_D.BRAND_KEY] (REQUIRED)
- Column 5 "LOB": type VARCHAR; precision 13; scale not recorded; nullable true; external name `samplesales`; MAPPED by logical column(s) "Dim Products"."Lob". [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_PRODUCTS_D.LOB] (REQUIRED)
- Column 6 "LOB_KEY": type DOUBLE; precision 15; scale not recorded; nullable not stated; external name `samplesales`; MAPPED by logical column(s) "Dim Products"."Lob Key". [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_PRODUCTS_D.LOB_KEY] (REQUIRED)
- Column 7 "PROD_DSC": type VARCHAR; precision 26; scale not recorded; nullable true; external name `samplesales`; MAPPED by logical column(s) "Dim Products"."Prod Dsc". [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_PRODUCTS_D.PROD_DSC] (REQUIRED)
- Column 8 "PROD_KEY": type DOUBLE; precision 15; scale not recorded; nullable not stated; external name `samplesales`; MAPPED by logical column(s) "Dim Products"."Prod Key". [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_PRODUCTS_D.PROD_KEY] (REQUIRED)
- Column 9 "SEQUENCE": type DOUBLE; precision 15; scale not recorded; nullable not stated; external name `samplesales`; MAPPED by logical column(s) "Dim Products"."Sequence". [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_PRODUCTS_D.SEQUENCE] (REQUIRED)
- Column 10 "TYPE": type VARCHAR; precision 12; scale not recorded; nullable true; external name `samplesales`; MAPPED by logical column(s) "Dim Products"."Type". [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_PRODUCTS_D.TYPE] (REQUIRED)
- Column 11 "TYPE_KEY": type DOUBLE; precision 15; scale not recorded; nullable not stated; external name `samplesales`; MAPPED by logical column(s) "Dim Products"."Type Key". [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_PRODUCTS_D.TYPE_KEY] (REQUIRED)

