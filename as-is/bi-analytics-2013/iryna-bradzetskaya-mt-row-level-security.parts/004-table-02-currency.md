## CURRENCY

CURRENCY is a business table ("logical table") of the business model "Iryna_Bradzetskaya_MT_Row-level_Security"; the repository declares 1 source for it [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.CURRENCY]. In this subject area it has 9 fields and no measures, and all 9 are shown to report authors; its one source is "DIM_CURRENCY_SCD" (counted; no Row identifier).

### Keys

| Key | Primary key (as recorded) | Fields | Evidence |
|---|---|---|---|
| PK_DIM_CURRENCY_SCD | true | CURRENCY_SUR_ID | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.CURRENCY.key.PK_DIM_CURRENCY_SCD] |

### Measures

This table has no measures.

### Fields

Every field is shown to report authors under its own name in the table CURRENCY. None can be typed into. The definition kept on each field is one database column of the same name in DIM_CURRENCY_SCD.

| Field | Shown to report authors as | Kept definition | In sources | Evidence |
|---|---|---|---|---|
| CURRENCY_CODE | CURRENCY_CODE | DIM_CURRENCY_SCD.CURRENCY_CODE | 1 of 1: "DIM_CURRENCY_SCD" | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.CURRENCY.CURRENCY_CODE] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.CURRENCY.CURRENCY_CODE] |
| CURRENCY_DESC | CURRENCY_DESC | DIM_CURRENCY_SCD.CURRENCY_DESC | 1 of 1: "DIM_CURRENCY_SCD" | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.CURRENCY.CURRENCY_DESC] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.CURRENCY.CURRENCY_DESC] |
| CURRENCY_NAME | CURRENCY_NAME | DIM_CURRENCY_SCD.CURRENCY_NAME | 1 of 1: "DIM_CURRENCY_SCD" | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.CURRENCY.CURRENCY_NAME] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.CURRENCY.CURRENCY_NAME] |
| CURRENCY_SUR_ID | CURRENCY_SUR_ID | DIM_CURRENCY_SCD.CURRENCY_SUR_ID | 1 of 1: "DIM_CURRENCY_SCD" | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.CURRENCY.CURRENCY_SUR_ID] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.CURRENCY.CURRENCY_SUR_ID] |
| CURRENCY_TO_DOLLAR | CURRENCY_TO_DOLLAR | DIM_CURRENCY_SCD.CURRENCY_TO_DOLLAR | 1 of 1: "DIM_CURRENCY_SCD" | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.CURRENCY.CURRENCY_TO_DOLLAR] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.CURRENCY.CURRENCY_TO_DOLLAR] |
| CURRENCY_TYPE_DESC | CURRENCY_TYPE_DESC | DIM_CURRENCY_SCD.CURRENCY_TYPE_DESC | 1 of 1: "DIM_CURRENCY_SCD" | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.CURRENCY.CURRENCY_TYPE_DESC] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.CURRENCY.CURRENCY_TYPE_DESC] |
| CURRENCY_TYPE_ID | CURRENCY_TYPE_ID | DIM_CURRENCY_SCD.CURRENCY_TYPE_ID | 1 of 1: "DIM_CURRENCY_SCD" | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.CURRENCY.CURRENCY_TYPE_ID] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.CURRENCY.CURRENCY_TYPE_ID] |
| VALID_FROM | VALID_FROM | DIM_CURRENCY_SCD.VALID_FROM | 1 of 1: "DIM_CURRENCY_SCD" | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.CURRENCY.VALID_FROM] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.CURRENCY.VALID_FROM] |
| VALID_TO | VALID_TO | DIM_CURRENCY_SCD.VALID_TO | 1 of 1: "DIM_CURRENCY_SCD" | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.CURRENCY.VALID_TO] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.CURRENCY.VALID_TO] |

What CURRENCY_TO_DOLLAR holds, and what the pair VALID_FROM and VALID_TO means for the rows of this table, are not recorded (open question Q4).

### Measures and fields report authors do not see

This table has no measures or fields that report authors do not see.

### Where the data comes from

CURRENCY has one source.

#### Source "DIM_CURRENCY_SCD"

The source is active; its priority group is not recorded (empty; it does not mean lowest) [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.CURRENCY.src.DIM_CURRENCY_SCD]. Level of detail (grain): not recorded. The repository gives this source no level of detail [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.CURRENCY.src.DIM_CURRENCY_SCD]. The packet says nothing about a parent-child hierarchy for this source.

No content filter is recorded for this source.

**Database tables it reads.**

| Database table | Schema | Type | Evidence |
|---|---|---|---|
| DIM_CURRENCY_SCD (see the section Database table DIM_CURRENCY_SCD) | `Iryna_Bradzetskaya_MT.U_SAL` | `none` | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.CURRENCY.src.DIM_CURRENCY_SCD#table0] |

This source reads one table, so it makes no join.

**Mapping.** Every mapping of this source is one database column of DIM_CURRENCY_SCD.

| Field | Database column | Type, precision, nullable | Evidence |
|---|---|---|---|
| CURRENCY_CODE | DIM_CURRENCY_SCD.CURRENCY_CODE | type DOUBLE, precision 22, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.CURRENCY.src.DIM_CURRENCY_SCD#map0] |
| CURRENCY_DESC | DIM_CURRENCY_SCD.CURRENCY_DESC | type VARCHAR, precision 30, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.CURRENCY.src.DIM_CURRENCY_SCD#map1] |
| CURRENCY_NAME | DIM_CURRENCY_SCD.CURRENCY_NAME | type VARCHAR, precision 10, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.CURRENCY.src.DIM_CURRENCY_SCD#map2] |
| CURRENCY_SUR_ID | DIM_CURRENCY_SCD.CURRENCY_SUR_ID | type DOUBLE, precision 22, nullable not stated | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.CURRENCY.src.DIM_CURRENCY_SCD#map3] |
| CURRENCY_TO_DOLLAR | DIM_CURRENCY_SCD.CURRENCY_TO_DOLLAR | type DOUBLE, precision 22, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.CURRENCY.src.DIM_CURRENCY_SCD#map4] |
| CURRENCY_TYPE_DESC | DIM_CURRENCY_SCD.CURRENCY_TYPE_DESC | type VARCHAR, precision 50, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.CURRENCY.src.DIM_CURRENCY_SCD#map5] |
| CURRENCY_TYPE_ID | DIM_CURRENCY_SCD.CURRENCY_TYPE_ID | type DOUBLE, precision 22, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.CURRENCY.src.DIM_CURRENCY_SCD#map6] |
| VALID_FROM | DIM_CURRENCY_SCD.VALID_FROM | type DATETIME, precision 22, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.CURRENCY.src.DIM_CURRENCY_SCD#map7] |
| VALID_TO | DIM_CURRENCY_SCD.VALID_TO | type DATETIME, precision 22, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.CURRENCY.src.DIM_CURRENCY_SCD#map8] |

### Points to note

- The key of CURRENCY is CURRENCY_SUR_ID alone [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.CURRENCY.key.PK_DIM_CURRENCY_SCD], while the table also carries VALID_FROM and VALID_TO [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.CURRENCY.src.DIM_CURRENCY_SCD#map7] [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.CURRENCY.src.DIM_CURRENCY_SCD#map8] (open question Q4).
- CURRENCY_CODE is mapped from a column of type DOUBLE, not a text column [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.CURRENCY.src.DIM_CURRENCY_SCD#map0].
