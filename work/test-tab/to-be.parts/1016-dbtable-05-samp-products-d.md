### New table SAMP_PRODUCTS_D

Proposal (not from OBIEE; to be signed): build SAMP_PRODUCTS_D as a table, like for like from the OBIEE database table "SAMP_PRODUCTS_D". The old table is in `xe.SAMPLESALES`, type `none`. [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_PRODUCTS_D] It is not an alias of another physical table. Its object in the database: no separate name is recorded, so the repository knows it only by the name above. Description in the repository: not recorded.

#### Columns

| # | New column | Proposed type | Length or precision | Decimal places | May be empty | From old column | Old field | Used by tab items | Evidence |
|---|---|---|---|---|---|---|---|---|---|
| 1 | ATTRIBUTE_1 | text | length 7 | not applicable | yes, as recorded | SAMP_PRODUCTS_D.ATTRIBUTE_1 | "Dim Products"."Attribute 1" | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_PRODUCTS_D.ATTRIBUTE_1] |
| 2 | ATTRIBUTE_2 | text | length 6 | not applicable | yes, as recorded | SAMP_PRODUCTS_D.ATTRIBUTE_2 | "Dim Products"."Attribute 2" | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_PRODUCTS_D.ATTRIBUTE_2] |
| 3 | BRAND | text | length 8 | not applicable | yes, as recorded | SAMP_PRODUCTS_D.BRAND | "Dim Products"."Brand" | F3 | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_PRODUCTS_D.BRAND] |
| 4 | BRAND_KEY | number | precision 15 | missing | missing (not stated in the export) | SAMP_PRODUCTS_D.BRAND_KEY | "Dim Products"."Brand Key" | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_PRODUCTS_D.BRAND_KEY] |
| 5 | LOB | text | length 13 | not applicable | yes, as recorded | SAMP_PRODUCTS_D.LOB | "Dim Products"."Lob" | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_PRODUCTS_D.LOB] |
| 6 | LOB_KEY | number | precision 15 | missing | missing (not stated in the export) | SAMP_PRODUCTS_D.LOB_KEY | "Dim Products"."Lob Key" | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_PRODUCTS_D.LOB_KEY] |
| 7 | PROD_DSC | text | length 26 | not applicable | yes, as recorded | SAMP_PRODUCTS_D.PROD_DSC | "Dim Products"."Prod Dsc" | V2-product | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_PRODUCTS_D.PROD_DSC] |
| 8 | PROD_KEY | number | precision 15 | missing | missing (not stated in the export) | SAMP_PRODUCTS_D.PROD_KEY | "Dim Products"."Prod Key" | none (key of Dim Products) | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_PRODUCTS_D.PROD_KEY] |
| 9 | SEQUENCE | number | precision 15 | missing | missing (not stated in the export) | SAMP_PRODUCTS_D.SEQUENCE | "Dim Products"."Sequence" | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_PRODUCTS_D.SEQUENCE] |
| 10 | TYPE | text | length 12 | not applicable | yes, as recorded | SAMP_PRODUCTS_D.TYPE | "Dim Products"."Type" | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_PRODUCTS_D.TYPE] |
| 11 | TYPE_KEY | number | precision 15 | missing | missing (not stated in the export) | SAMP_PRODUCTS_D.TYPE_KEY | "Dim Products"."Type Key" | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_PRODUCTS_D.TYPE_KEY] |

Every column records the external name `samplesales`.

#### Key

Key "SAMP_PRODUCTS_D_Key", made of column(s) "PROD_KEY", the only key the repository declares for the table. [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_PRODUCTS_D.key.SAMP_PRODUCTS_D_Key] Proposal (not from OBIEE; to be signed): PROD_KEY is the key of the new table SAMP_PRODUCTS_D.

#### Grain

The level of detail is stated in the section Model table Dim Products.

#### Links to other tables

- Foreign key "SAMP_REVENUE_F_FKey#2" from SAMP_REVENUE_F (PROD_KEY) to SAMP_PRODUCTS_D (PROD_KEY): proposed as a foreign key; stated in full under Relationships.
- Foreign key "SAMP_REVENUE_A_FKey#1" from SAMP_REVENUE_A (PROD_KEY) to SAMP_PRODUCTS_D (PROD_KEY): proposed as a foreign key; stated in full under Relationships.
- Foreign key "SAMP_TARGETS_F_FKey#2" from SAMP_TARGETS_F (PROD_KEY) to SAMP_PRODUCTS_D (PROD_KEY): proposed as a foreign key; stated in full under Relationships.
- Foreign key "SAMP_PRODUCTS_D_TRANSLATION_FKey" from SAMP_PRODUCTS_D_TRANSLATION (PROD_KEY) to SAMP_PRODUCTS_D (PROD_KEY): proposed as a foreign key; stated in full under Relationships.
- Complex join "Complex Join#1" between FactCrossJoin and SAMP_PRODUCTS_D: stated in full under Relationships.

#### Read by

Source "SAMP_PRODUCTS_D" of the model table Dim Products.

#### Missing

- Decimal places of BRAND_KEY, LOB_KEY, PROD_KEY, SEQUENCE and TYPE_KEY (scale not recorded).
- Whether BRAND_KEY, LOB_KEY, PROD_KEY, SEQUENCE and TYPE_KEY must be filled (nullable not stated).
- Indexes, partitioning, volumes, and how and when the table is loaded.
