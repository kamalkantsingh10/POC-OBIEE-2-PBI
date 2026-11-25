### Model table Dim Products (Translated)

The business table "Dim Products (Translated)" is in business model "SAMPLESALES", with 1 table source declared, and is marked in the repository as a lookup table: it is read through the LOOKUP function and is not joined like a dimension. [kb:sampleapp::bmm::SAMPLESALES.Dim Products (Translated)] It has 3 columns in this slice, none of them measures, and none confirmed for the tab.

Proposal (not from OBIEE; to be signed): no model table for "Dim Products (Translated)" in this tab's model. No field of it is confirmed for the tab, and the only field that reads it, "Prod Dsc (Translated)" of "Dim Products", is not confirmed either (section Model table Dim Products). If a later tab needs translated product names, it becomes the decision on lookup and translation (see also as-is document, question Q8).

For the record: its key is "PK_PROD_TRANS1", marked as primary key true, made of "PROD_KEY" and "LANG_ID" [kb:sampleapp::bmm::SAMPLESALES.Dim Products (Translated).key.PK_PROD_TRANS1]; its one source "SAMP_PRODUCTS_D_TRANSLATION" is active, with priority group not recorded and no level of detail recorded, and reads the database table SAMP_PRODUCTS_D_TRANSLATION [kb:sampleapp::bmm::SAMPLESALES.Dim Products (Translated).src.SAMP_PRODUCTS_D_TRANSLATION]. Its rows are listed under Not described.

#### Measures

None: the table holds no measure.
