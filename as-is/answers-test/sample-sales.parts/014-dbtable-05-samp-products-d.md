## Database table SAMP_PRODUCTS_D

SAMP_PRODUCTS_D is a database table in `xe.SAMPLESALES`, type `none`; it is not an alias of another table, and no description is recorded [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_PRODUCTS_D]. No separate object name in the database is recorded, so the repository knows it only by the name SAMP_PRODUCTS_D (listed in the packet without a Row identifier). The export lists 11 columns for it, all 11 mapped by a field of this subject area, and 1 key (counted).

### Columns

| # | Column | Type | Precision | Scale | Nullable | External name | Mapped by | Evidence |
|---|---|---|---|---|---|---|---|---|
| 1 | ATTRIBUTE_1 | VARCHAR | 7 | not recorded | true | `samplesales` | Dim Products, Attribute 1 | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_PRODUCTS_D.ATTRIBUTE_1] |
| 2 | ATTRIBUTE_2 | VARCHAR | 6 | not recorded | true | `samplesales` | Dim Products, Attribute 2 | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_PRODUCTS_D.ATTRIBUTE_2] |
| 3 | BRAND | VARCHAR | 8 | not recorded | true | `samplesales` | Dim Products, Brand | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_PRODUCTS_D.BRAND] |
| 4 | BRAND_KEY | DOUBLE | 15 | not recorded | not stated | `samplesales` | Dim Products, Brand Key | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_PRODUCTS_D.BRAND_KEY] |
| 5 | LOB | VARCHAR | 13 | not recorded | true | `samplesales` | Dim Products, Lob | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_PRODUCTS_D.LOB] |
| 6 | LOB_KEY | DOUBLE | 15 | not recorded | not stated | `samplesales` | Dim Products, Lob Key | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_PRODUCTS_D.LOB_KEY] |
| 7 | PROD_DSC | VARCHAR | 26 | not recorded | true | `samplesales` | Dim Products, Prod Dsc | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_PRODUCTS_D.PROD_DSC] |
| 8 | PROD_KEY | DOUBLE | 15 | not recorded | not stated | `samplesales` | Dim Products, Prod Key | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_PRODUCTS_D.PROD_KEY] |
| 9 | SEQUENCE | DOUBLE | 15 | not recorded | not stated | `samplesales` | Dim Products, Sequence | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_PRODUCTS_D.SEQUENCE] |
| 10 | TYPE | VARCHAR | 12 | not recorded | true | `samplesales` | Dim Products, Type | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_PRODUCTS_D.TYPE] |
| 11 | TYPE_KEY | DOUBLE | 15 | not recorded | not stated | `samplesales` | Dim Products, Type Key | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_PRODUCTS_D.TYPE_KEY] |

Nullable: the export records this only when it is true. "Not stated" means the export does not carry it; it is not a statement that the column must be filled. "Not recorded" means the repository says nothing; it does not mean no, and it does not mean 0.

### Keys

| Key | Columns | Evidence |
|---|---|---|
| SAMP_PRODUCTS_D_Key | PROD_KEY | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_PRODUCTS_D.key.SAMP_PRODUCTS_D_Key] |

The repository does not say whether a physical key is the primary key; this is the only key it declares for the table. Four foreign keys of this subject area point at exactly PROD_KEY [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_PRODUCTS_D.key.SAMP_PRODUCTS_D_Key].

### Foreign keys and joins

- The complex join "Complex Join#1" between FactCrossJoin and this table; its condition is stated in full in the section How the tables relate.
- The foreign key "SAMP_PRODUCTS_D_TRANSLATION_FKey" from SAMP_PRODUCTS_D_TRANSLATION (column PROD_KEY) to this table (column PROD_KEY); stated in full in the section How the tables relate.
- The foreign key "SAMP_REVENUE_A_FKey#1" from SAMP_REVENUE_A (column PROD_KEY) to this table (column PROD_KEY); stated in full in the section How the tables relate.
- The foreign key "SAMP_REVENUE_F_FKey#2" from SAMP_REVENUE_F (column PROD_KEY) to this table (column PROD_KEY); stated in full in the section How the tables relate.
- The foreign key "SAMP_TARGETS_F_FKey#2" from SAMP_TARGETS_F (column PROD_KEY) to this table (column PROD_KEY); stated in full in the section How the tables relate.

### Read by

The source "SAMP_PRODUCTS_D" of the business table Dim Products (listed in the packet without a Row identifier).
