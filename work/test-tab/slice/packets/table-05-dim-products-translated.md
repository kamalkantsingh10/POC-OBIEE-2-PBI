# Logical table "Dim Products (Translated)"
Unit: table-05-dim-products-translated
Kind: logical_table
Tab: test-tab (subject areas: Sample Sales; estate sampleapp)
Facts from: logical_columns, logical_keys, logical_table_sources, logical_tables, lts_column_map, lts_tables
Rows of this packet a document must cover: 3. Each is on a line that ends with (REQUIRED).
To cite a fact, copy the marker in square brackets that follows it, exactly as written. "->" points at another row.

## The table

- Logical table "Dim Products (Translated)" in business model "SAMPLESALES"; table sources the repository declares for it: 1. [kb:sampleapp::bmm::SAMPLESALES.Dim Products (Translated)] (REQUIRED)
- Columns of it in this slice: 3, of which measures: 0; not confirmed for the tab: 3. (counted; no Row identifier)
- Table sources of it in this slice: 1: "SAMP_PRODUCTS_D_TRANSLATION". Each is described below under its own heading, with the physical tables it reads and what it maps each column from. (counted; no Row identifier)
- Marked in the repository as a lookup table: true. A lookup table is read through the LOOKUP function (typically for translated names) and is not joined like a dimension. [kb:sampleapp::bmm::SAMPLESALES.Dim Products (Translated)]
- Key "PK_PROD_TRANS1" of logical table "Dim Products (Translated)": marked as primary key true; made of column(s) "PROD_KEY", "LANG_ID". [kb:sampleapp::bmm::SAMPLESALES.Dim Products (Translated).key.PK_PROD_TRANS1] (REQUIRED)

## Columns: what the table offers

- Logical column "LANG_ID": not a measure; writeable false; mapped in 1 of 1 table sources. [kb:sampleapp::bmm::SAMPLESALES.Dim Products (Translated).LANG_ID]
    - NOT CONFIRMED FOR THE TAB: no item of the tab uses it. It is described because its logical table is part of the model behind the tab.
    - Mapped in table source(s): "SAMP_PRODUCTS_D_TRANSLATION". What it is mapped from is stated with each source.

- Logical column "PROD_DESC": not a measure; writeable false; mapped in 1 of 1 table sources. [kb:sampleapp::bmm::SAMPLESALES.Dim Products (Translated).PROD_DESC]
    - NOT CONFIRMED FOR THE TAB: no item of the tab uses it. It is described because its logical table is part of the model behind the tab.
    - Mapped in table source(s): "SAMP_PRODUCTS_D_TRANSLATION". What it is mapped from is stated with each source.

- Logical column "PROD_KEY": not a measure; writeable false; mapped in 1 of 1 table sources. [kb:sampleapp::bmm::SAMPLESALES.Dim Products (Translated).PROD_KEY]
    - NOT CONFIRMED FOR THE TAB: no item of the tab uses it. It is described because its logical table is part of the model behind the tab.
    - Mapped in table source(s): "SAMP_PRODUCTS_D_TRANSLATION". What it is mapped from is stated with each source.

## Table sources: where the table's data comes from

- Source "SAMP_PRODUCTS_D_TRANSLATION" of logical table "Dim Products (Translated)": active true; priority group not recorded (empty; it does not mean lowest). [kb:sampleapp::bmm::SAMPLESALES.Dim Products (Translated).src.SAMP_PRODUCTS_D_TRANSLATION] (REQUIRED)
    - Level of detail (grain): not recorded. The repository gives this source no level of detail. [kb:sampleapp::bmm::SAMPLESALES.Dim Products (Translated).src.SAMP_PRODUCTS_D_TRANSLATION]
    - Physical tables this source is built on: 1 (one table, so the source makes no join).
        - Reads physical table "SAMP_PRODUCTS_D_TRANSLATION" in `xe.SAMPLESALES`; type `none`. Unit dbtable-06-samp-products-d-translation states the table whole: what it is in the database, every column, its keys. -> sampleapp::phys::xe.SAMPLESALES.SAMP_PRODUCTS_D_TRANSLATION [kb:sampleapp::bmm::SAMPLESALES.Dim Products (Translated).src.SAMP_PRODUCTS_D_TRANSLATION#table0]
    - Logical column "LANG_ID" here is `"xe".""."SAMPLESALES"."SAMP_PRODUCTS_D_TRANSLATION"."LANG_ID"`; physical column "SAMP_PRODUCTS_D_TRANSLATION"."LANG_ID" (type VARCHAR, precision 2, nullable not stated). [kb:sampleapp::bmm::SAMPLESALES.Dim Products (Translated).src.SAMP_PRODUCTS_D_TRANSLATION#map0]
    - Logical column "PROD_DESC" here is `"xe".""."SAMPLESALES"."SAMP_PRODUCTS_D_TRANSLATION"."PROD_DESC"`; physical column "SAMP_PRODUCTS_D_TRANSLATION"."PROD_DESC" (type VARCHAR, precision 50, nullable not stated). [kb:sampleapp::bmm::SAMPLESALES.Dim Products (Translated).src.SAMP_PRODUCTS_D_TRANSLATION#map1]
    - Logical column "PROD_KEY" here is `"xe".""."SAMPLESALES"."SAMP_PRODUCTS_D_TRANSLATION"."PROD_KEY"`; physical column "SAMP_PRODUCTS_D_TRANSLATION"."PROD_KEY" (type DOUBLE, precision 15, nullable not stated). [kb:sampleapp::bmm::SAMPLESALES.Dim Products (Translated).src.SAMP_PRODUCTS_D_TRANSLATION#map2]

