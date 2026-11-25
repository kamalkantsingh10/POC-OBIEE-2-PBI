### New table SAMP_PRODUCTS_D_TRANSLATION

Proposal (not from OBIEE; to be signed): build SAMP_PRODUCTS_D_TRANSLATION as a table, like for like from the OBIEE database table "SAMP_PRODUCTS_D_TRANSLATION", only if a later tab needs translated product names. The tab's model does not read it (section Model table Dim Products (Translated)). The old table is in `xe.SAMPLESALES`, type `none`. [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_PRODUCTS_D_TRANSLATION] It is not an alias of another physical table. Its object in the database for database type `DefaultMulDB`: `SAMP_PRODUCTS_D_TRANSLATION` [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_PRODUCTS_D_TRANSLATION.def.DefaultMulDB]. Description in the repository: not recorded.

#### Columns

| # | New column | Proposed type | Length or precision | Decimal places | May be empty | From old column | Old field | Used by tab items | Evidence |
|---|---|---|---|---|---|---|---|---|---|
| 1 | LANG_ID | text | length 2 | not applicable | missing (not stated in the export) | SAMP_PRODUCTS_D_TRANSLATION.LANG_ID | "Dim Products (Translated)"."LANG_ID" | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_PRODUCTS_D_TRANSLATION.LANG_ID] |
| 2 | PROD_DESC | text | length 50 | not applicable | missing (not stated in the export) | SAMP_PRODUCTS_D_TRANSLATION.PROD_DESC | "Dim Products (Translated)"."PROD_DESC" | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_PRODUCTS_D_TRANSLATION.PROD_DESC] |
| 3 | PROD_KEY | number | precision 15 | missing | missing (not stated in the export) | SAMP_PRODUCTS_D_TRANSLATION.PROD_KEY | "Dim Products (Translated)"."PROD_KEY" | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_PRODUCTS_D_TRANSLATION.PROD_KEY] |

Every column records the external name `samplesales`.

#### Key

Key "PK_PROD_TRANS1", made of column(s) "LANG_ID", "PROD_KEY", the only key the repository declares for the table. [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_PRODUCTS_D_TRANSLATION.key.PK_PROD_TRANS1] Proposal (not from OBIEE; to be signed): LANG_ID and PROD_KEY together are the key of the new table.

#### Grain

The level of detail is stated in the section Model table Dim Products (Translated), whose source records none.

#### Links to other tables

- Foreign key "SAMP_PRODUCTS_D_TRANSLATION_FKey" from SAMP_PRODUCTS_D_TRANSLATION (PROD_KEY) to SAMP_PRODUCTS_D (PROD_KEY): proposed as a foreign key; stated in full under Relationships.

#### Read by

Source "SAMP_PRODUCTS_D_TRANSLATION" of the business table "Dim Products (Translated)", which this design does not carry into the model.

#### Missing

- Decimal places of PROD_KEY (scale not recorded).
- Whether any column must be filled (nullable not stated for all three).
- Indexes, partitioning, volumes, and how and when the table is loaded.
