## TARIFFS

TARIFFS is a business table ("logical table") of the business model "Iryna_Bradzetskaya_MT_Row-level_Security"; the repository declares 1 source for it [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TARIFFS]. In this subject area it has 9 fields and no measures, and all 9 are shown to report authors; its one source is "DIM_TARIFFS" (counted; no Row identifier).

### Keys

| Key | Primary key (as recorded) | Fields | Evidence |
|---|---|---|---|
| PK_TARIFFS | true | TARIFF_ID | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TARIFFS.key.PK_TARIFFS] |

### Measures

This table has no measures.

### Fields

Every field is shown to report authors under its own name in the table TARIFFS. None can be typed into. The definition kept on each field is one database column of the same name in DIM_TARIFFS.

| Field | Shown to report authors as | Kept definition | In sources | Evidence |
|---|---|---|---|---|
| INSERT_DT | INSERT_DT | DIM_TARIFFS.INSERT_DT | 1 of 1: "DIM_TARIFFS" | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TARIFFS.INSERT_DT] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.TARIFFS.INSERT_DT] |
| TARIFF_CODE | TARIFF_CODE | DIM_TARIFFS.TARIFF_CODE | 1 of 1: "DIM_TARIFFS" | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TARIFFS.TARIFF_CODE] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.TARIFFS.TARIFF_CODE] |
| TARIFF_ID | TARIFF_ID | DIM_TARIFFS.TARIFF_ID | 1 of 1: "DIM_TARIFFS" | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TARIFFS.TARIFF_ID] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.TARIFFS.TARIFF_ID] |
| TARIFF_MAX_PAYMENT | TARIFF_MAX_PAYMENT | DIM_TARIFFS.TARIFF_MAX_PAYMENT | 1 of 1: "DIM_TARIFFS" | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TARIFFS.TARIFF_MAX_PAYMENT] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.TARIFFS.TARIFF_MAX_PAYMENT] |
| TARIFF_MIN_PAYMENT | TARIFF_MIN_PAYMENT | DIM_TARIFFS.TARIFF_MIN_PAYMENT | 1 of 1: "DIM_TARIFFS" | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TARIFFS.TARIFF_MIN_PAYMENT] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.TARIFFS.TARIFF_MIN_PAYMENT] |
| TARIFF_NAME | TARIFF_NAME | DIM_TARIFFS.TARIFF_NAME | 1 of 1: "DIM_TARIFFS" | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TARIFFS.TARIFF_NAME] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.TARIFFS.TARIFF_NAME] |
| TARIFF_PAYMENT_SUM | TARIFF_PAYMENT_SUM | DIM_TARIFFS.TARIFF_PAYMENT_SUM | 1 of 1: "DIM_TARIFFS" | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TARIFFS.TARIFF_PAYMENT_SUM] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.TARIFFS.TARIFF_PAYMENT_SUM] |
| TARIFF_TYPE | TARIFF_TYPE | DIM_TARIFFS.TARIFF_TYPE | 1 of 1: "DIM_TARIFFS" | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TARIFFS.TARIFF_TYPE] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.TARIFFS.TARIFF_TYPE] |
| UPDATE_DT | UPDATE_DT | DIM_TARIFFS.UPDATE_DT | 1 of 1: "DIM_TARIFFS" | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TARIFFS.UPDATE_DT] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.TARIFFS.UPDATE_DT] |

TARIFF_MIN_PAYMENT, TARIFF_MAX_PAYMENT and TARIFF_PAYMENT_SUM are plain fields, not measures. What the three hold, and how they relate to the amounts of a transaction, is not recorded (open question Q8).

### Measures and fields report authors do not see

This table has no measures or fields that report authors do not see.

### Where the data comes from

TARIFFS has one source.

#### Source "DIM_TARIFFS"

The source is active; its priority group is not recorded (empty; it does not mean lowest) [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TARIFFS.src.DIM_TARIFFS]. Level of detail (grain): not recorded. The repository gives this source no level of detail [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TARIFFS.src.DIM_TARIFFS]. The packet says nothing about a parent-child hierarchy for this source.

No content filter is recorded for this source.

**Database tables it reads.**

| Database table | Schema | Type | Evidence |
|---|---|---|---|
| DIM_TARIFFS (see the section Database table DIM_TARIFFS) | `Iryna_Bradzetskaya_MT.U_SAL` | `none` | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TARIFFS.src.DIM_TARIFFS#table0] |

This source reads one table, so it makes no join.

**Mapping.** Every mapping of this source is one database column of DIM_TARIFFS.

| Field | Database column | Type, precision, nullable | Evidence |
|---|---|---|---|
| INSERT_DT | DIM_TARIFFS.INSERT_DT | type DATETIME, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TARIFFS.src.DIM_TARIFFS#map0] |
| TARIFF_CODE | DIM_TARIFFS.TARIFF_CODE | type VARCHAR, precision 5, nullable not stated | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TARIFFS.src.DIM_TARIFFS#map1] |
| TARIFF_ID | DIM_TARIFFS.TARIFF_ID | type DOUBLE, precision 22, nullable not stated | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TARIFFS.src.DIM_TARIFFS#map2] |
| TARIFF_MAX_PAYMENT | DIM_TARIFFS.TARIFF_MAX_PAYMENT | type DOUBLE, precision 22, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TARIFFS.src.DIM_TARIFFS#map3] |
| TARIFF_MIN_PAYMENT | DIM_TARIFFS.TARIFF_MIN_PAYMENT | type DOUBLE, precision 22, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TARIFFS.src.DIM_TARIFFS#map4] |
| TARIFF_NAME | DIM_TARIFFS.TARIFF_NAME | type VARCHAR, precision 30, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TARIFFS.src.DIM_TARIFFS#map5] |
| TARIFF_PAYMENT_SUM | DIM_TARIFFS.TARIFF_PAYMENT_SUM | type DOUBLE, precision 22, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TARIFFS.src.DIM_TARIFFS#map6] |
| TARIFF_TYPE | DIM_TARIFFS.TARIFF_TYPE | type CHAR, precision 30, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TARIFFS.src.DIM_TARIFFS#map7] |
| UPDATE_DT | DIM_TARIFFS.UPDATE_DT | type DATETIME, precision 30, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TARIFFS.src.DIM_TARIFFS#map8] |

For INSERT_DT the packet gives no precision; it prints only "type DATETIME, nullable true" [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TARIFFS.src.DIM_TARIFFS#map0].

### Points to note

- TARIFF_PAYMENT_SUM, TARIFF_MIN_PAYMENT and TARIFF_MAX_PAYMENT are plain fields and are not summarised [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TARIFFS.TARIFF_PAYMENT_SUM] (open question Q8).
- TARIFF_TYPE is mapped from a column of type CHAR with precision 30 [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TARIFFS.src.DIM_TARIFFS#map7].
