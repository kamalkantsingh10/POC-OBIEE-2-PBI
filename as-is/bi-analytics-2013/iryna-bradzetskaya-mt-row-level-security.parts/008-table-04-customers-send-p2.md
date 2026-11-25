### Where the data comes from

CUSTOMERS_SEND has one source.

#### Source "DIM_CUSTOMERS_SEND"

The source is active; its priority group is not recorded (empty; it does not mean lowest) [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.CUSTOMERS_SEND.src.DIM_CUSTOMERS_SEND]. Level of detail (grain): not recorded. The repository gives this source no level of detail [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.CUSTOMERS_SEND.src.DIM_CUSTOMERS_SEND]. The packet says nothing about a parent-child hierarchy for this source.

No content filter is recorded for this source.

**Database tables it reads.** The one table is a query kept in the repository (type `select`), not a table of the database; its query is given in the section Database table DIM_CUSTOMERS_SEND.

| Database table | Schema | Type | Evidence |
|---|---|---|---|
| DIM_CUSTOMERS_SEND (see the section Database table DIM_CUSTOMERS_SEND) | `Iryna_Bradzetskaya_MT.U_SAL` | `select` | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.CUSTOMERS_SEND.src.DIM_CUSTOMERS_SEND#table0] |

This source reads one table, so it makes no join.

**Mapping.** Every mapping of this source is one database column of DIM_CUSTOMERS_SEND. The rows are in the packet's order.

| Field | Database column | Type, precision, nullable | Evidence |
|---|---|---|---|
| CUST_BALANCE | DIM_CUSTOMERS_SEND.CUST_BALANCE | type DOUBLE, precision 22, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.CUSTOMERS_SEND.src.DIM_CUSTOMERS_SEND#map0] |
| CUST_EMAIL | DIM_CUSTOMERS_SEND.CUST_EMAIL | type VARCHAR, precision 50, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.CUSTOMERS_SEND.src.DIM_CUSTOMERS_SEND#map1] |
| INSERT_DT | DIM_CUSTOMERS_SEND.INSERT_DT | type DATETIME, precision 30, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.CUSTOMERS_SEND.src.DIM_CUSTOMERS_SEND#map10] |
| UPDATE_DT | DIM_CUSTOMERS_SEND.UPDATE_DT | type DATETIME, precision 30, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.CUSTOMERS_SEND.src.DIM_CUSTOMERS_SEND#map11] |
| CUST_FIRST_NAME | DIM_CUSTOMERS_SEND.CUST_FIRST_NAME | type VARCHAR, precision 30, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.CUSTOMERS_SEND.src.DIM_CUSTOMERS_SEND#map2] |
| CUST_GENDER | DIM_CUSTOMERS_SEND.CUST_GENDER | type VARCHAR, precision 1, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.CUSTOMERS_SEND.src.DIM_CUSTOMERS_SEND#map3] |
| CUST_GEO_ID | DIM_CUSTOMERS_SEND.CUST_GEO_ID | type DOUBLE, precision 22, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.CUSTOMERS_SEND.src.DIM_CUSTOMERS_SEND#map4] |
| CUST_ID | DIM_CUSTOMERS_SEND.CUST_ID | type DOUBLE, precision 22, nullable not stated | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.CUSTOMERS_SEND.src.DIM_CUSTOMERS_SEND#map5] |
| CUST_LAST_NAME | DIM_CUSTOMERS_SEND.CUST_LAST_NAME | type VARCHAR, precision 30, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.CUSTOMERS_SEND.src.DIM_CUSTOMERS_SEND#map6] |
| CUST_LEVEL_INCOME | DIM_CUSTOMERS_SEND.CUST_LEVEL_INCOME | type DOUBLE, precision 22, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.CUSTOMERS_SEND.src.DIM_CUSTOMERS_SEND#map7] |
| CUST_PASS_NUMBER | DIM_CUSTOMERS_SEND.CUST_PASS_NUMBER | type VARCHAR, precision 30, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.CUSTOMERS_SEND.src.DIM_CUSTOMERS_SEND#map8] |
| CUST_YEAR_OF_BIRTH | DIM_CUSTOMERS_SEND.CUST_YEAR_OF_BIRTH | type DATETIME, precision 30, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.CUSTOMERS_SEND.src.DIM_CUSTOMERS_SEND#map9] |

### Points to note

- The source reads a query kept in the repository, not a database table; what that query reads is in the section Database table DIM_CUSTOMERS_SEND [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.CUSTOMERS_SEND.src.DIM_CUSTOMERS_SEND#table0].
- CUST_YEAR_OF_BIRTH is mapped from a column recorded as type DATETIME, not a number [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.CUSTOMERS_SEND.src.DIM_CUSTOMERS_SEND#map9].
