### Model table Dim Products

Proposal (not from OBIEE; to be signed): a table of the Power BI model named "Dim Products", like the business table, read from the new table SAMP_PRODUCTS_D.

The business table "Dim Products" is in business model "SAMPLESALES", with 1 table source declared. [kb:sampleapp::bmm::SAMPLESALES.Dim Products] It has 12 columns in this slice, none of them measures; 10 are not confirmed for the tab. The tab uses two fields, "Brand" and "Prod Dsc".

#### Keys

Key "SAMP_PRODUCTS_D_Key": marked as primary key true; made of column(s) "Prod Key". [kb:sampleapp::bmm::SAMPLESALES.Dim Products.key.SAMP_PRODUCTS_D_Key]

Proposal (not from OBIEE; to be signed): "Prod Key" is the key of the model table Dim Products, the one side of its relationships.

#### Where it reads from

| Source | Active | Priority group | Level of detail, exactly | Reads | Evidence |
|---|---|---|---|---|---|
| SAMP_PRODUCTS_D | true | not recorded (empty; it does not mean lowest) | ` GROUPBYLEVEL("SAMPLESALES"."Products Hierararchy"."Product Detail")` | new table SAMP_PRODUCTS_D (old "SAMP_PRODUCTS_D" in `xe.SAMPLESALES`, type `none`) [kb:sampleapp::bmm::SAMPLESALES.Dim Products.src.SAMP_PRODUCTS_D#table0] | [kb:sampleapp::bmm::SAMPLESALES.Dim Products.src.SAMP_PRODUCTS_D] |

One source, one table, no join, no content filter.

#### Fields carried into the model

| Field | From new table.column | Type, precision, nullable as the packet prints them | Tab items | Evidence |
|---|---|---|---|---|
| Brand | SAMP_PRODUCTS_D.BRAND, mapped as `"xe".""."SAMPLESALES"."SAMP_PRODUCTS_D"."BRAND"` | type VARCHAR, precision 8, nullable true | F3 | [kb:sampleapp::bmm::SAMPLESALES.Dim Products.src.SAMP_PRODUCTS_D#map2] |
| Prod Dsc | SAMP_PRODUCTS_D.PROD_DSC, mapped as `"xe".""."SAMPLESALES"."SAMP_PRODUCTS_D"."PROD_DSC"` | type VARCHAR, precision 26, nullable true | V2-product | [kb:sampleapp::bmm::SAMPLESALES.Dim Products.src.SAMP_PRODUCTS_D#map6] |

"Brand" is shown to report authors as "Brand" in presentation table "Dim Products"; name set by hand: false; confirmed for the tab, item F3. [kb:sampleapp::pres::Sample Sales.Dim Products.Brand] "Prod Dsc" is shown as "Prod Dsc"; name set by hand: false; confirmed for the tab, item V2-product. [kb:sampleapp::pres::Sample Sales.Dim Products.Prod Dsc]

The identifier of "Prod Dsc" is held by "Dim Products"."Prod Key" (OBIEE's "descriptor ID"): "Prod Dsc" is the text shown, and filters use "Prod Key" in its place. [kb:sampleapp::bmm::SAMPLESALES.Dim Products.Prod Dsc] Proposal (not from OBIEE; to be signed): the model table keeps "Prod Key", so that two products with the same description stay apart in a visual.

The 10 fields not confirmed ("Attribute 1", "Attribute 2", "Brand Key", "Lob", "Lob Key", "Prod Dsc (Translated)", "Prod Key", "Sequence", "Type", "Type Key") are not carried as fields. "Prod Key" is the key field: the relationships need its column, mapped as `"xe".""."SAMPLESALES"."SAMP_PRODUCTS_D"."PROD_KEY"` (type DOUBLE, precision 15, nullable not stated) [kb:sampleapp::bmm::SAMPLESALES.Dim Products.src.SAMP_PRODUCTS_D#map7]; see Relationships. "Prod Dsc (Translated)" is a lookup on a value set per user and is not used by the tab [kb:sampleapp::bmm::SAMPLESALES.Dim Products.Prod Dsc (Translated)]; see also as-is document, question Q8.

#### Measures

None: the table holds no measure.
