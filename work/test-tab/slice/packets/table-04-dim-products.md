# Logical table "Dim Products"
Unit: table-04-dim-products
Kind: logical_table
Tab: test-tab (subject areas: Sample Sales; estate sampleapp)
Facts from: lineage, logical_columns, logical_keys, logical_table_sources, logical_tables, lts_column_map, lts_tables, presentation_columns
Rows of this packet a document must cover: 5. Each is on a line that ends with (REQUIRED).
To cite a fact, copy the marker in square brackets that follows it, exactly as written. "->" points at another row.

## The table

- Logical table "Dim Products" in business model "SAMPLESALES"; table sources the repository declares for it: 1. [kb:sampleapp::bmm::SAMPLESALES.Dim Products] (REQUIRED)
- Columns of it in this slice: 12, of which measures: 0; not confirmed for the tab: 10. (counted; no Row identifier)
- Table sources of it in this slice: 1: "SAMP_PRODUCTS_D". Each is described below under its own heading, with the physical tables it reads and what it maps each column from. (counted; no Row identifier)
- Key "SAMP_PRODUCTS_D_Key" of logical table "Dim Products": marked as primary key true; made of column(s) "Prod Key". [kb:sampleapp::bmm::SAMPLESALES.Dim Products.key.SAMP_PRODUCTS_D_Key] (REQUIRED)

## Columns: what the table offers

- Logical column "Attribute 1": not a measure; writeable false; mapped in 1 of 1 table sources. [kb:sampleapp::bmm::SAMPLESALES.Dim Products.Attribute 1]
    - NOT CONFIRMED FOR THE TAB: no item of the tab uses it. It is described because its logical table is part of the model behind the tab.
    - Mapped in table source(s): "SAMP_PRODUCTS_D". What it is mapped from is stated with each source.

- Logical column "Attribute 2": not a measure; writeable false; mapped in 1 of 1 table sources. [kb:sampleapp::bmm::SAMPLESALES.Dim Products.Attribute 2]
    - NOT CONFIRMED FOR THE TAB: no item of the tab uses it. It is described because its logical table is part of the model behind the tab.
    - Mapped in table source(s): "SAMP_PRODUCTS_D". What it is mapped from is stated with each source.

- Logical column "Brand": not a measure; writeable false; mapped in 1 of 1 table sources. [kb:sampleapp::bmm::SAMPLESALES.Dim Products.Brand]
    - Shown to report authors as "Brand" in presentation table "Dim Products"; name set by hand: false; CONFIRMED FOR THE TAB, items F3. [kb:sampleapp::pres::Sample Sales.Dim Products.Brand] (REQUIRED)
    - Mapped in table source(s): "SAMP_PRODUCTS_D". What it is mapped from is stated with each source.

- Logical column "Brand Key": not a measure; writeable false; mapped in 1 of 1 table sources. [kb:sampleapp::bmm::SAMPLESALES.Dim Products.Brand Key]
    - NOT CONFIRMED FOR THE TAB: no item of the tab uses it. It is described because its logical table is part of the model behind the tab.
    - Mapped in table source(s): "SAMP_PRODUCTS_D". What it is mapped from is stated with each source.

- Logical column "Lob": not a measure; writeable false; mapped in 1 of 1 table sources. [kb:sampleapp::bmm::SAMPLESALES.Dim Products.Lob]
    - NOT CONFIRMED FOR THE TAB: no item of the tab uses it. It is described because its logical table is part of the model behind the tab.
    - Its identifier is held by logical column "Dim Products"."Lob Key" (OBIEE's "descriptor ID"): this column is the text shown, and filters use that column in its place. -> sampleapp::bmm::SAMPLESALES.Dim Products.Lob Key [kb:sampleapp::bmm::SAMPLESALES.Dim Products.Lob]
    - Mapped in table source(s): "SAMP_PRODUCTS_D". What it is mapped from is stated with each source.

- Logical column "Lob Key": not a measure; writeable false; mapped in 1 of 1 table sources. [kb:sampleapp::bmm::SAMPLESALES.Dim Products.Lob Key]
    - NOT CONFIRMED FOR THE TAB: no item of the tab uses it. It is described because its logical table is part of the model behind the tab.
    - Mapped in table source(s): "SAMP_PRODUCTS_D". What it is mapped from is stated with each source.

- Logical column "Prod Dsc": not a measure; writeable false; mapped in 1 of 1 table sources. [kb:sampleapp::bmm::SAMPLESALES.Dim Products.Prod Dsc]
    - Shown to report authors as "Prod Dsc" in presentation table "Dim Products"; name set by hand: false; CONFIRMED FOR THE TAB, items V2-product. [kb:sampleapp::pres::Sample Sales.Dim Products.Prod Dsc] (REQUIRED)
    - Its identifier is held by logical column "Dim Products"."Prod Key" (OBIEE's "descriptor ID"): this column is the text shown, and filters use that column in its place. -> sampleapp::bmm::SAMPLESALES.Dim Products.Prod Key [kb:sampleapp::bmm::SAMPLESALES.Dim Products.Prod Dsc]
    - Mapped in table source(s): "SAMP_PRODUCTS_D". What it is mapped from is stated with each source.

- Logical column "Prod Dsc (Translated)": not a measure; writeable false; mapped in 0 of 1 table sources. [kb:sampleapp::bmm::SAMPLESALES.Dim Products.Prod Dsc (Translated)]
    - NOT CONFIRMED FOR THE TAB: no item of the tab uses it. It is described because its logical table is part of the model behind the tab.
    - Formula (calculated from other logical objects): `Lookup(SPARSE   "SAMPLESALES"."Dim Products (Translated)"."PROD_DESC" ,  "SAMPLESALES"."Dim Products"."Prod Dsc"  , "SAMPLESALES"."Dim Products"."Prod Key", VALUEOF(NQ_SESSION.WEBLANGUAGE) ) ` [kb:sampleapp::bmm::SAMPLESALES.Dim Products.Prod Dsc (Translated)]
        - %1 -> sampleapp::bmm::SAMPLESALES.Dim Products (Translated).PROD_DESC
        - %2 -> sampleapp::bmm::SAMPLESALES.Dim Products.Prod Dsc
        - %3 -> sampleapp::bmm::SAMPLESALES.Dim Products.Prod Key
        - Names variable `WEBLANGUAGE` inside VALUEOF(...): unit variables lists every such name and says whether the repository defines it.

- Logical column "Prod Key": not a measure; writeable false; mapped in 1 of 1 table sources. [kb:sampleapp::bmm::SAMPLESALES.Dim Products.Prod Key]
    - NOT CONFIRMED FOR THE TAB: no item of the tab uses it. It is described because its logical table is part of the model behind the tab.
    - Mapped in table source(s): "SAMP_PRODUCTS_D". What it is mapped from is stated with each source.

- Logical column "Sequence": not a measure; writeable false; mapped in 1 of 1 table sources. [kb:sampleapp::bmm::SAMPLESALES.Dim Products.Sequence]
    - NOT CONFIRMED FOR THE TAB: no item of the tab uses it. It is described because its logical table is part of the model behind the tab.
    - Mapped in table source(s): "SAMP_PRODUCTS_D". What it is mapped from is stated with each source.

- Logical column "Type": not a measure; writeable false; mapped in 1 of 1 table sources. [kb:sampleapp::bmm::SAMPLESALES.Dim Products.Type]
    - NOT CONFIRMED FOR THE TAB: no item of the tab uses it. It is described because its logical table is part of the model behind the tab.
    - Its identifier is held by logical column "Dim Products"."Type Key" (OBIEE's "descriptor ID"): this column is the text shown, and filters use that column in its place. -> sampleapp::bmm::SAMPLESALES.Dim Products.Type Key [kb:sampleapp::bmm::SAMPLESALES.Dim Products.Type]
    - Mapped in table source(s): "SAMP_PRODUCTS_D". What it is mapped from is stated with each source.

- Logical column "Type Key": not a measure; writeable false; mapped in 1 of 1 table sources. [kb:sampleapp::bmm::SAMPLESALES.Dim Products.Type Key]
    - NOT CONFIRMED FOR THE TAB: no item of the tab uses it. It is described because its logical table is part of the model behind the tab.
    - Mapped in table source(s): "SAMP_PRODUCTS_D". What it is mapped from is stated with each source.

## Table sources: where the table's data comes from

- Source "SAMP_PRODUCTS_D" of logical table "Dim Products": active true; priority group not recorded (empty; it does not mean lowest). [kb:sampleapp::bmm::SAMPLESALES.Dim Products.src.SAMP_PRODUCTS_D] (REQUIRED)
    - Level of detail (grain; the source's group by): ` GROUPBYLEVEL("SAMPLESALES"."Products Hierararchy"."Product Detail")` [kb:sampleapp::bmm::SAMPLESALES.Dim Products.src.SAMP_PRODUCTS_D]
        - %1 -> sampleapp::bmm::SAMPLESALES.Products Hierararchy.Product Detail
    - Physical tables this source is built on: 1 (one table, so the source makes no join).
        - Reads physical table "SAMP_PRODUCTS_D" in `xe.SAMPLESALES`; type `none`. Unit dbtable-05-samp-products-d states the table whole: what it is in the database, every column, its keys. -> sampleapp::phys::xe.SAMPLESALES.SAMP_PRODUCTS_D [kb:sampleapp::bmm::SAMPLESALES.Dim Products.src.SAMP_PRODUCTS_D#table0]
    - Logical column "Attribute 1" here is `"xe".""."SAMPLESALES"."SAMP_PRODUCTS_D"."ATTRIBUTE_1"`; physical column "SAMP_PRODUCTS_D"."ATTRIBUTE_1" (type VARCHAR, precision 7, nullable true). [kb:sampleapp::bmm::SAMPLESALES.Dim Products.src.SAMP_PRODUCTS_D#map0]
    - Logical column "Attribute 2" here is `"xe".""."SAMPLESALES"."SAMP_PRODUCTS_D"."ATTRIBUTE_2"`; physical column "SAMP_PRODUCTS_D"."ATTRIBUTE_2" (type VARCHAR, precision 6, nullable true). [kb:sampleapp::bmm::SAMPLESALES.Dim Products.src.SAMP_PRODUCTS_D#map1]
    - Logical column "Type Key" here is `"xe".""."SAMPLESALES"."SAMP_PRODUCTS_D"."TYPE_KEY"`; physical column "SAMP_PRODUCTS_D"."TYPE_KEY" (type DOUBLE, precision 15, nullable not stated). [kb:sampleapp::bmm::SAMPLESALES.Dim Products.src.SAMP_PRODUCTS_D#map10]
    - Logical column "Brand" here is `"xe".""."SAMPLESALES"."SAMP_PRODUCTS_D"."BRAND"`; physical column "SAMP_PRODUCTS_D"."BRAND" (type VARCHAR, precision 8, nullable true). [kb:sampleapp::bmm::SAMPLESALES.Dim Products.src.SAMP_PRODUCTS_D#map2]
        - Lineage: presentation column "Dim Products"."Brand" <- source "SAMP_PRODUCTS_D" <- physical column "SAMP_PRODUCTS_D"."BRAND". [kb:sampleapp::pres::Sample Sales.Dim Products.Brand#sampleapp::bmm::SAMPLESALES.Dim Products.src.SAMP_PRODUCTS_D]
    - Logical column "Brand Key" here is `"xe".""."SAMPLESALES"."SAMP_PRODUCTS_D"."BRAND_KEY"`; physical column "SAMP_PRODUCTS_D"."BRAND_KEY" (type DOUBLE, precision 15, nullable not stated). [kb:sampleapp::bmm::SAMPLESALES.Dim Products.src.SAMP_PRODUCTS_D#map3]
    - Logical column "Lob" here is `"xe".""."SAMPLESALES"."SAMP_PRODUCTS_D"."LOB"`; physical column "SAMP_PRODUCTS_D"."LOB" (type VARCHAR, precision 13, nullable true). [kb:sampleapp::bmm::SAMPLESALES.Dim Products.src.SAMP_PRODUCTS_D#map4]
    - Logical column "Lob Key" here is `"xe".""."SAMPLESALES"."SAMP_PRODUCTS_D"."LOB_KEY"`; physical column "SAMP_PRODUCTS_D"."LOB_KEY" (type DOUBLE, precision 15, nullable not stated). [kb:sampleapp::bmm::SAMPLESALES.Dim Products.src.SAMP_PRODUCTS_D#map5]
    - Logical column "Prod Dsc" here is `"xe".""."SAMPLESALES"."SAMP_PRODUCTS_D"."PROD_DSC"`; physical column "SAMP_PRODUCTS_D"."PROD_DSC" (type VARCHAR, precision 26, nullable true). [kb:sampleapp::bmm::SAMPLESALES.Dim Products.src.SAMP_PRODUCTS_D#map6]
        - Lineage: presentation column "Dim Products"."Prod Dsc" <- source "SAMP_PRODUCTS_D" <- physical column "SAMP_PRODUCTS_D"."PROD_DSC". [kb:sampleapp::pres::Sample Sales.Dim Products.Prod Dsc#sampleapp::bmm::SAMPLESALES.Dim Products.src.SAMP_PRODUCTS_D]
    - Logical column "Prod Key" here is `"xe".""."SAMPLESALES"."SAMP_PRODUCTS_D"."PROD_KEY"`; physical column "SAMP_PRODUCTS_D"."PROD_KEY" (type DOUBLE, precision 15, nullable not stated). [kb:sampleapp::bmm::SAMPLESALES.Dim Products.src.SAMP_PRODUCTS_D#map7]
    - Logical column "Sequence" here is `"xe".""."SAMPLESALES"."SAMP_PRODUCTS_D"."SEQUENCE"`; physical column "SAMP_PRODUCTS_D"."SEQUENCE" (type DOUBLE, precision 15, nullable not stated). [kb:sampleapp::bmm::SAMPLESALES.Dim Products.src.SAMP_PRODUCTS_D#map8]
    - Logical column "Type" here is `"xe".""."SAMPLESALES"."SAMP_PRODUCTS_D"."TYPE"`; physical column "SAMP_PRODUCTS_D"."TYPE" (type VARCHAR, precision 12, nullable true). [kb:sampleapp::bmm::SAMPLESALES.Dim Products.src.SAMP_PRODUCTS_D#map9]

