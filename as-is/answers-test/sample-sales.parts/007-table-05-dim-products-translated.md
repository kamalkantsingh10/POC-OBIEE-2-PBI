## Dim Products (Translated)

"Dim Products (Translated)" is a business table of the business model "SAMPLESALES"; the repository declares 1 source for it [kb:sampleapp::bmm::SAMPLESALES.Dim Products (Translated)]. It is marked in the repository as a lookup table: it is read through the LOOKUP function and is not joined like a dimension [kb:sampleapp::bmm::SAMPLESALES.Dim Products (Translated)]. In this subject area it has 3 fields, none of them measures, and none is shown to report authors (counted). Its one source is "SAMP_PRODUCTS_D_TRANSLATION" (counted).

### Keys

| Key | Primary key (as recorded) | Fields | Evidence |
|---|---|---|---|
| PK_PROD_TRANS1 | true | PROD_KEY, LANG_ID | [kb:sampleapp::bmm::SAMPLESALES.Dim Products (Translated).key.PK_PROD_TRANS1] |

### Measures

This table has no measures.

### Fields

No field of this table is shown to report authors (counted).

### Measures and fields report authors do not see

| Field | In sources | Evidence |
|---|---|---|
| LANG_ID | 1 of 1: "SAMP_PRODUCTS_D_TRANSLATION" | [kb:sampleapp::bmm::SAMPLESALES.Dim Products (Translated).LANG_ID] |
| PROD_DESC | 1 of 1: "SAMP_PRODUCTS_D_TRANSLATION" | [kb:sampleapp::bmm::SAMPLESALES.Dim Products (Translated).PROD_DESC] |
| PROD_KEY | 1 of 1: "SAMP_PRODUCTS_D_TRANSLATION" | [kb:sampleapp::bmm::SAMPLESALES.Dim Products (Translated).PROD_KEY] |

### Where the data comes from

#### Source "SAMP_PRODUCTS_D_TRANSLATION"

Active: true. Priority group not recorded (empty; it does not mean lowest) [kb:sampleapp::bmm::SAMPLESALES.Dim Products (Translated).src.SAMP_PRODUCTS_D_TRANSLATION]. Level of detail: not recorded. The repository gives this source no level of detail [kb:sampleapp::bmm::SAMPLESALES.Dim Products (Translated).src.SAMP_PRODUCTS_D_TRANSLATION].

No content filter is recorded for this source.

| Database table | Schema | Type | Evidence |
|---|---|---|---|
| SAMP_PRODUCTS_D_TRANSLATION (see the section Database table SAMP_PRODUCTS_D_TRANSLATION) | `xe.SAMPLESALES` | `none` | [kb:sampleapp::bmm::SAMPLESALES.Dim Products (Translated).src.SAMP_PRODUCTS_D_TRANSLATION#table0] |

This source reads one table, so it makes no join.

| Field | Database column | Type, precision, nullable | Evidence |
|---|---|---|---|
| LANG_ID | SAMP_PRODUCTS_D_TRANSLATION.LANG_ID | type VARCHAR, precision 2, nullable not stated | [kb:sampleapp::bmm::SAMPLESALES.Dim Products (Translated).src.SAMP_PRODUCTS_D_TRANSLATION#map0] |
| PROD_DESC | SAMP_PRODUCTS_D_TRANSLATION.PROD_DESC | type VARCHAR, precision 50, nullable not stated | [kb:sampleapp::bmm::SAMPLESALES.Dim Products (Translated).src.SAMP_PRODUCTS_D_TRANSLATION#map1] |
| PROD_KEY | SAMP_PRODUCTS_D_TRANSLATION.PROD_KEY | type DOUBLE, precision 15, nullable not stated | [kb:sampleapp::bmm::SAMPLESALES.Dim Products (Translated).src.SAMP_PRODUCTS_D_TRANSLATION#map2] |

### Points to note

- The table is keyed on PROD_KEY and LANG_ID together [kb:sampleapp::bmm::SAMPLESALES.Dim Products (Translated).key.PK_PROD_TRANS1]; LANG_ID is VARCHAR of precision 2 [kb:sampleapp::bmm::SAMPLESALES.Dim Products (Translated).src.SAMP_PRODUCTS_D_TRANSLATION#map0]. Which languages it holds is not recorded (open question Q8).
