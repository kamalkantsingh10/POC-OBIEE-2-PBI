### Where the data comes from

LOCATIONS_SEND has one source.

#### Source "DIM_LOCATIONS_SEND"

The source is active; its priority group is not recorded (empty; it does not mean lowest) [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.LOCATIONS_SEND.src.DIM_LOCATIONS_SEND]. Level of detail (grain): not recorded. The repository gives this source no level of detail [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.LOCATIONS_SEND.src.DIM_LOCATIONS_SEND]. The packet says nothing about a parent-child hierarchy for this source.

No content filter is recorded for this source.

**Database tables it reads.** The one table is a query kept in the repository (type `select`), not a table of the database; its query is given in the section Database table DIM_LOCATIONS_SEND.

| Database table | Schema | Type | Evidence |
|---|---|---|---|
| DIM_LOCATIONS_SEND (see the section Database table DIM_LOCATIONS_SEND) | `Iryna_Bradzetskaya_MT.U_SAL` | `select` | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.LOCATIONS_SEND.src.DIM_LOCATIONS_SEND#table0] |

This source reads one table, so it makes no join.

**Mapping.** Every mapping of this source is one database column of DIM_LOCATIONS_SEND. The rows are in the packet's order.

| Field | Database column | Type, precision, nullable | Evidence |
|---|---|---|---|
| COUNTRY_CODE_A2 | DIM_LOCATIONS_SEND.COUNTRY_CODE_A2 | type VARCHAR, precision 5, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.LOCATIONS_SEND.src.DIM_LOCATIONS_SEND#map0] |
| COUNTRY_CODE_A3 | DIM_LOCATIONS_SEND.COUNTRY_CODE_A3 | type VARCHAR, precision 5, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.LOCATIONS_SEND.src.DIM_LOCATIONS_SEND#map1] |
| GEO_REGION_DESC | DIM_LOCATIONS_SEND.GEO_REGION_DESC | type VARCHAR, precision 50, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.LOCATIONS_SEND.src.DIM_LOCATIONS_SEND#map10] |
| GEO_REGION_ID | DIM_LOCATIONS_SEND.GEO_REGION_ID | type DOUBLE, precision 22, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.LOCATIONS_SEND.src.DIM_LOCATIONS_SEND#map11] |
| GEO_SURR_ID | DIM_LOCATIONS_SEND.GEO_SURR_ID | type DOUBLE, precision 22, nullable not stated | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.LOCATIONS_SEND.src.DIM_LOCATIONS_SEND#map12] |
| LEVEL_CODE | DIM_LOCATIONS_SEND.LEVEL_CODE | type VARCHAR, precision 30, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.LOCATIONS_SEND.src.DIM_LOCATIONS_SEND#map13] |
| VALID_FROM | DIM_LOCATIONS_SEND.VALID_FROM | type DATETIME, precision 30, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.LOCATIONS_SEND.src.DIM_LOCATIONS_SEND#map14] |
| VALID_TO | DIM_LOCATIONS_SEND.VALID_TO | type DATETIME, precision 30, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.LOCATIONS_SEND.src.DIM_LOCATIONS_SEND#map15] |
| COUNTRY_ID | DIM_LOCATIONS_SEND.COUNTRY_ID | type DOUBLE, precision 22, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.LOCATIONS_SEND.src.DIM_LOCATIONS_SEND#map2] |
| GEO_COUNTRY_ID | DIM_LOCATIONS_SEND.GEO_COUNTRY_ID | type DOUBLE, precision 22, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.LOCATIONS_SEND.src.DIM_LOCATIONS_SEND#map3] |
| GEO_COUNTRY_NAME | DIM_LOCATIONS_SEND.GEO_COUNTRY_NAME | type VARCHAR, precision 80, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.LOCATIONS_SEND.src.DIM_LOCATIONS_SEND#map4] |
| GEO_ID | DIM_LOCATIONS_SEND.GEO_ID | type DOUBLE, precision 22, nullable not stated | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.LOCATIONS_SEND.src.DIM_LOCATIONS_SEND#map5] |
| GEO_PART_CODE | DIM_LOCATIONS_SEND.GEO_PART_CODE | type VARCHAR, precision 30, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.LOCATIONS_SEND.src.DIM_LOCATIONS_SEND#map6] |
| GEO_PART_DESC | DIM_LOCATIONS_SEND.GEO_PART_DESC | type VARCHAR, precision 50, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.LOCATIONS_SEND.src.DIM_LOCATIONS_SEND#map7] |
| GEO_PART_ID | DIM_LOCATIONS_SEND.GEO_PART_ID | type DOUBLE, precision 22, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.LOCATIONS_SEND.src.DIM_LOCATIONS_SEND#map8] |
| GEO_REGION_CODE | DIM_LOCATIONS_SEND.GEO_REGION_CODE | type VARCHAR, precision 3, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.LOCATIONS_SEND.src.DIM_LOCATIONS_SEND#map9] |

### Points to note

- The source reads a query kept in the repository, not a database table; what that query reads is in the section Database table DIM_LOCATIONS_SEND [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.LOCATIONS_SEND.src.DIM_LOCATIONS_SEND#table0].
- Both GEO_ID and GEO_SURR_ID are mapped from columns whose nullable is "not stated", while every other column of this source is "nullable true" [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.LOCATIONS_SEND.src.DIM_LOCATIONS_SEND#map5] [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.LOCATIONS_SEND.src.DIM_LOCATIONS_SEND#map12] (open question Q7).
- No content filter is recorded for this source.
