## Dim Products

"Dim Products" is a business table of the business model "SAMPLESALES"; the repository declares 1 source for it [kb:sampleapp::bmm::SAMPLESALES.Dim Products]. In this subject area it has 12 fields and no measures; 3 of the 12 are not shown to report authors here (counted; no Row identifier). Its one source is "SAMP_PRODUCTS_D" (counted; no Row identifier).

### Keys

| Key | Primary key (as recorded) | Fields | Evidence |
|---|---|---|---|
| SAMP_PRODUCTS_D_Key | true | Prod Key | [kb:sampleapp::bmm::SAMPLESALES.Dim Products.key.SAMP_PRODUCTS_D_Key] |

### Measures

This table has no measures.

### Fields

All fields below are shown to report authors in the table "Dim Products" under the same name.

| Field | In sources | Evidence |
|---|---|---|
| Brand | 1 of 1: "SAMP_PRODUCTS_D" | [kb:sampleapp::bmm::SAMPLESALES.Dim Products.Brand] [kb:sampleapp::pres::Sample Sales.Dim Products.Brand] |
| Brand Key | 1 of 1: "SAMP_PRODUCTS_D" | [kb:sampleapp::bmm::SAMPLESALES.Dim Products.Brand Key] [kb:sampleapp::pres::Sample Sales.Dim Products.Brand Key] |
| Lob | 1 of 1: "SAMP_PRODUCTS_D" | [kb:sampleapp::bmm::SAMPLESALES.Dim Products.Lob] [kb:sampleapp::pres::Sample Sales.Dim Products.Lob] |
| Lob Key | 1 of 1: "SAMP_PRODUCTS_D" | [kb:sampleapp::bmm::SAMPLESALES.Dim Products.Lob Key] [kb:sampleapp::pres::Sample Sales.Dim Products.Lob Key] |
| Prod Dsc | 1 of 1: "SAMP_PRODUCTS_D" | [kb:sampleapp::bmm::SAMPLESALES.Dim Products.Prod Dsc] [kb:sampleapp::pres::Sample Sales.Dim Products.Prod Dsc] |
| Prod Dsc (Translated) | 0 of 1 (calculated, see below) | [kb:sampleapp::bmm::SAMPLESALES.Dim Products.Prod Dsc (Translated)] [kb:sampleapp::pres::Sample Sales.Dim Products.Prod Dsc (Translated)] |
| Prod Key | 1 of 1: "SAMP_PRODUCTS_D" | [kb:sampleapp::bmm::SAMPLESALES.Dim Products.Prod Key] [kb:sampleapp::pres::Sample Sales.Dim Products.Prod Key] |
| Type | 1 of 1: "SAMP_PRODUCTS_D" | [kb:sampleapp::bmm::SAMPLESALES.Dim Products.Type] [kb:sampleapp::pres::Sample Sales.Dim Products.Type] |
| Type Key | 1 of 1: "SAMP_PRODUCTS_D" | [kb:sampleapp::bmm::SAMPLESALES.Dim Products.Type Key] [kb:sampleapp::pres::Sample Sales.Dim Products.Type Key] |

Three fields have their identifier held by another field (OBIEE's "descriptor ID"): the field is the text shown, and filters use the other field in its place.

| Field shown | Identifier held by | Evidence |
|---|---|---|
| Lob | Lob Key | [kb:sampleapp::bmm::SAMPLESALES.Dim Products.Lob] |
| Prod Dsc | Prod Key | [kb:sampleapp::bmm::SAMPLESALES.Dim Products.Prod Dsc] |
| Type | Type Key | [kb:sampleapp::bmm::SAMPLESALES.Dim Products.Type] |

"Prod Dsc (Translated)" is calculated from other fields of the model and is mapped in no source. Its formula, exactly [kb:sampleapp::bmm::SAMPLESALES.Dim Products.Prod Dsc (Translated)]:

```
Lookup(SPARSE   "SAMPLESALES"."Dim Products (Translated)"."PROD_DESC" ,  "SAMPLESALES"."Dim Products"."Prod Dsc"  , "SAMPLESALES"."Dim Products"."Prod Key", VALUEOF(NQ_SESSION.WEBLANGUAGE) )
```

It names the field "PROD_DESC" of the business table "Dim Products (Translated)" (see the section Dim Products (Translated)), the fields "Prod Dsc" and "Prod Key" of this table, and the variable WEBLANGUAGE. Whether the repository defines WEBLANGUAGE is stated in the section Values set per user or for the whole system. What the field shows a given user is not recorded (open question Q8).

### Measures and fields report authors do not see

These fields are not shown to report authors in this subject area. They are described because their business table is part of the model behind the subject area.

| Field | In sources | Evidence |
|---|---|---|
| Attribute 1 | 1 of 1: "SAMP_PRODUCTS_D" | [kb:sampleapp::bmm::SAMPLESALES.Dim Products.Attribute 1] |
| Attribute 2 | 1 of 1: "SAMP_PRODUCTS_D" | [kb:sampleapp::bmm::SAMPLESALES.Dim Products.Attribute 2] |
| Sequence | 1 of 1: "SAMP_PRODUCTS_D" | [kb:sampleapp::bmm::SAMPLESALES.Dim Products.Sequence] |

### Where the data comes from

#### Source "SAMP_PRODUCTS_D"

Active: true. Priority group not recorded (empty; it does not mean lowest) [kb:sampleapp::bmm::SAMPLESALES.Dim Products.src.SAMP_PRODUCTS_D]. Level of detail, exactly: `GROUPBYLEVEL("SAMPLESALES"."Products Hierararchy"."Product Detail")`, the level "Product Detail" of the drill path "Products Hierararchy" [kb:sampleapp::bmm::SAMPLESALES.Dim Products.src.SAMP_PRODUCTS_D].

No content filter is recorded for this source.

| Database table | Schema | Type | Evidence |
|---|---|---|---|
| SAMP_PRODUCTS_D (see the section Database table SAMP_PRODUCTS_D) | `xe.SAMPLESALES` | `none` | [kb:sampleapp::bmm::SAMPLESALES.Dim Products.src.SAMP_PRODUCTS_D#table0] |

This source reads one table, so it makes no join.

| Field | Database column | Type, precision, nullable | Evidence |
|---|---|---|---|
| Attribute 1 | SAMP_PRODUCTS_D.ATTRIBUTE_1 | type VARCHAR, precision 7, nullable true | [kb:sampleapp::bmm::SAMPLESALES.Dim Products.src.SAMP_PRODUCTS_D#map0] |
| Attribute 2 | SAMP_PRODUCTS_D.ATTRIBUTE_2 | type VARCHAR, precision 6, nullable true | [kb:sampleapp::bmm::SAMPLESALES.Dim Products.src.SAMP_PRODUCTS_D#map1] |
| Type Key | SAMP_PRODUCTS_D.TYPE_KEY | type DOUBLE, precision 15, nullable not stated | [kb:sampleapp::bmm::SAMPLESALES.Dim Products.src.SAMP_PRODUCTS_D#map10] |
| Brand | SAMP_PRODUCTS_D.BRAND | type VARCHAR, precision 8, nullable true | [kb:sampleapp::bmm::SAMPLESALES.Dim Products.src.SAMP_PRODUCTS_D#map2] |
| Brand Key | SAMP_PRODUCTS_D.BRAND_KEY | type DOUBLE, precision 15, nullable not stated | [kb:sampleapp::bmm::SAMPLESALES.Dim Products.src.SAMP_PRODUCTS_D#map3] |
| Lob | SAMP_PRODUCTS_D.LOB | type VARCHAR, precision 13, nullable true | [kb:sampleapp::bmm::SAMPLESALES.Dim Products.src.SAMP_PRODUCTS_D#map4] |
| Lob Key | SAMP_PRODUCTS_D.LOB_KEY | type DOUBLE, precision 15, nullable not stated | [kb:sampleapp::bmm::SAMPLESALES.Dim Products.src.SAMP_PRODUCTS_D#map5] |
| Prod Dsc | SAMP_PRODUCTS_D.PROD_DSC | type VARCHAR, precision 26, nullable true | [kb:sampleapp::bmm::SAMPLESALES.Dim Products.src.SAMP_PRODUCTS_D#map6] |
| Prod Key | SAMP_PRODUCTS_D.PROD_KEY | type DOUBLE, precision 15, nullable not stated | [kb:sampleapp::bmm::SAMPLESALES.Dim Products.src.SAMP_PRODUCTS_D#map7] |
| Sequence | SAMP_PRODUCTS_D.SEQUENCE | type DOUBLE, precision 15, nullable not stated | [kb:sampleapp::bmm::SAMPLESALES.Dim Products.src.SAMP_PRODUCTS_D#map8] |
| Type | SAMP_PRODUCTS_D.TYPE | type VARCHAR, precision 12, nullable true | [kb:sampleapp::bmm::SAMPLESALES.Dim Products.src.SAMP_PRODUCTS_D#map9] |

### Points to note

- "Prod Dsc (Translated)" is mapped in no source; its formula starts with Lookup and names the field "PROD_DESC" of the business table "Dim Products (Translated)", the fields "Prod Dsc" and "Prod Key" of this table and the variable WEBLANGUAGE [kb:sampleapp::bmm::SAMPLESALES.Dim Products.Prod Dsc (Translated)] (open question Q8).
- Filters on "Lob", "Prod Dsc" and "Type" use "Lob Key", "Prod Key" and "Type Key" in their place [kb:sampleapp::bmm::SAMPLESALES.Dim Products.Lob] [kb:sampleapp::bmm::SAMPLESALES.Dim Products.Prod Dsc] [kb:sampleapp::bmm::SAMPLESALES.Dim Products.Type].
- The primary key of "Dim Products" is the field "Prod Key" [kb:sampleapp::bmm::SAMPLESALES.Dim Products.key.SAMP_PRODUCTS_D_Key].
