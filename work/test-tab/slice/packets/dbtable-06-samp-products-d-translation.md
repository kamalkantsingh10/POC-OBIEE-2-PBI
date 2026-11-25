# Physical table "SAMP_PRODUCTS_D_TRANSLATION"
Unit: dbtable-06-samp-products-d-translation
Kind: physical_table
Tab: test-tab (subject areas: Sample Sales; estate sampleapp)
Facts from: physical_columns, physical_keys, physical_table_definitions, physical_tables
Rows of this packet a document must cover: 6. Each is on a line that ends with (REQUIRED).
To cite a fact, copy the marker in square brackets that follows it, exactly as written. "->" points at another row.

## The table, as the repository knows it

- Physical table "SAMP_PRODUCTS_D_TRANSLATION" in `xe.SAMPLESALES` (the containers above it in the repository, outermost first; usually database and schema); type `none`. [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_PRODUCTS_D_TRANSLATION] (REQUIRED)
    - It is not an alias of another physical table.
    - Description in the repository: not recorded.
    - Its object in the database for database type `DefaultMulDB`: `SAMP_PRODUCTS_D_TRANSLATION` [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_PRODUCTS_D_TRANSLATION.def.DefaultMulDB] (REQUIRED)
- Columns the repository lists for it: 3, of which mapped by a logical column of this slice: 3; not mapped by any: 0. Keys: 1. (counted; no Row identifier)
- Logical table sources of this slice that read it: 1.
    - Source "SAMP_PRODUCTS_D_TRANSLATION" of logical table "Dim Products (Translated)" (unit table-05-dim-products-translated) -> sampleapp::bmm::SAMPLESALES.Dim Products (Translated).src.SAMP_PRODUCTS_D_TRANSLATION

## Keys

- Key "PK_PROD_TRANS1" of physical table "SAMP_PRODUCTS_D_TRANSLATION": made of column(s) "LANG_ID", "PROD_KEY". [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_PRODUCTS_D_TRANSLATION.key.PK_PROD_TRANS1] (REQUIRED)
    - Key 1 of 1 the repository declares for the table. The repository does not say whether a physical key is the primary key; this is the only key it declares for the table. Foreign keys of this slice that point at exactly these column(s): 0.

## Foreign keys and joins (each is stated in full in the joins unit)

- Foreign key "SAMP_PRODUCTS_D_TRANSLATION_FKey" FROM this table ("PROD_KEY") to "SAMP_PRODUCTS_D" ("PROD_KEY"). -> sampleapp::phys::xe.SAMPLESALES.SAMP_PRODUCTS_D_TRANSLATION.SAMP_PRODUCTS_D_TRANSLATION_FKey

## Columns, in the order the repository lists them

- Each line below is one column: its place, name, data type, precision (length), scale (digits after the decimal point), whether it may be empty (nullable), its external name, and whether a logical column of this slice is mapped from it. "not recorded" means the repository says nothing; it does not mean no, and it does not mean 0.
- About nullable: the repository export writes the nullable attribute only as true; it never writes false. "nullable not stated" therefore means the attribute is absent: most likely the column is not nullable in the repository, but the file does not say so, and the repository is not proof of the constraint in the database.
- Column 1 "LANG_ID": type VARCHAR; precision 2; scale not recorded; nullable not stated; external name `samplesales`; MAPPED by logical column(s) "Dim Products (Translated)"."LANG_ID". [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_PRODUCTS_D_TRANSLATION.LANG_ID] (REQUIRED)
- Column 2 "PROD_DESC": type VARCHAR; precision 50; scale not recorded; nullable not stated; external name `samplesales`; MAPPED by logical column(s) "Dim Products (Translated)"."PROD_DESC". [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_PRODUCTS_D_TRANSLATION.PROD_DESC] (REQUIRED)
- Column 3 "PROD_KEY": type DOUBLE; precision 15; scale not recorded; nullable not stated; external name `samplesales`; MAPPED by logical column(s) "Dim Products (Translated)"."PROD_KEY". [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_PRODUCTS_D_TRANSLATION.PROD_KEY] (REQUIRED)

