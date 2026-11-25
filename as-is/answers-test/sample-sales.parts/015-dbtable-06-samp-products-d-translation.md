## Database table SAMP_PRODUCTS_D_TRANSLATION

SAMP_PRODUCTS_D_TRANSLATION is a database table in `xe.SAMPLESALES`, type `none`; it is not an alias of another table, and no description is recorded [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_PRODUCTS_D_TRANSLATION]. Its object in the database, for database type `DefaultMulDB`, is `SAMP_PRODUCTS_D_TRANSLATION` [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_PRODUCTS_D_TRANSLATION.def.DefaultMulDB]. The export lists 3 columns for it, all 3 mapped by a field of this subject area, and 1 key (counted).

### Columns

| # | Column | Type | Precision | Scale | Nullable | External name | Mapped by | Evidence |
|---|---|---|---|---|---|---|---|---|
| 1 | LANG_ID | VARCHAR | 2 | not recorded | not stated | `samplesales` | Dim Products (Translated), LANG_ID | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_PRODUCTS_D_TRANSLATION.LANG_ID] |
| 2 | PROD_DESC | VARCHAR | 50 | not recorded | not stated | `samplesales` | Dim Products (Translated), PROD_DESC | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_PRODUCTS_D_TRANSLATION.PROD_DESC] |
| 3 | PROD_KEY | DOUBLE | 15 | not recorded | not stated | `samplesales` | Dim Products (Translated), PROD_KEY | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_PRODUCTS_D_TRANSLATION.PROD_KEY] |

Nullable: the export records this only when it is true. "Not stated" means the export does not carry it; it is not a statement that the column must be filled. "Not recorded" means the repository says nothing; it does not mean no, and it does not mean 0.

### Keys

| Key | Columns | Evidence |
|---|---|---|
| PK_PROD_TRANS1 | LANG_ID, PROD_KEY | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_PRODUCTS_D_TRANSLATION.key.PK_PROD_TRANS1] |

The repository does not say whether a physical key is the primary key; this is the only key it declares for the table. No foreign key of this subject area points at exactly these columns [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_PRODUCTS_D_TRANSLATION.key.PK_PROD_TRANS1].

### Foreign keys and joins

- The foreign key "SAMP_PRODUCTS_D_TRANSLATION_FKey" from this table (column PROD_KEY) to SAMP_PRODUCTS_D (column PROD_KEY); stated in full in the section How the tables relate.

### Read by

The source "SAMP_PRODUCTS_D_TRANSLATION" of the business table Dim Products (Translated) (listed in the packet without a Row identifier).
