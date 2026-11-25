### Where the data comes from

TIMES_DAILY has one source.

#### Source "DIM_TIMES_DAILY"

The source is active; its priority group is not recorded (empty; it does not mean lowest) [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TIMES_DAILY.src.DIM_TIMES_DAILY]. Level of detail (grain): not recorded. The repository gives this source no level of detail [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TIMES_DAILY.src.DIM_TIMES_DAILY]. The packet says nothing about a parent-child hierarchy for this source.

No content filter is recorded for this source.

**Database tables it reads.** The one table is a query kept in the repository (type `select`), not a table of the database; its query is given in the section Database table DIM_TIMES_DAILY.

| Database table | Schema | Type | Evidence |
|---|---|---|---|
| DIM_TIMES_DAILY (see the section Database table DIM_TIMES_DAILY) | `Iryna_Bradzetskaya_MT.U_SAL` | `select` | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TIMES_DAILY.src.DIM_TIMES_DAILY#table0] |

This source reads one table, so it makes no join.

**Mapping.** Every mapping of this source is one database column of DIM_TIMES_DAILY. The rows are in the packet's order; where the packet prints no precision, none is given.

| Field | Database column | Type, precision, nullable | Evidence |
|---|---|---|---|
| BEG_OF_CAL_QUARTER | DIM_TIMES_DAILY.BEG_OF_CAL_QUARTER | type DATETIME, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TIMES_DAILY.src.DIM_TIMES_DAILY#map0] |
| BEG_OF_CAL_YEAR | DIM_TIMES_DAILY.BEG_OF_CAL_YEAR | type DATETIME, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TIMES_DAILY.src.DIM_TIMES_DAILY#map1] |
| DAY_NUMBER_IN_YEAR | DIM_TIMES_DAILY.DAY_NUMBER_IN_YEAR | type VARCHAR, precision 3, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TIMES_DAILY.src.DIM_TIMES_DAILY#map10] |
| DAYS_IN_CAL_MONTH | DIM_TIMES_DAILY.DAYS_IN_CAL_MONTH | type VARCHAR, precision 2, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TIMES_DAILY.src.DIM_TIMES_DAILY#map11] |
| DAYS_IN_CAL_QUARTER | DIM_TIMES_DAILY.DAYS_IN_CAL_QUARTER | type DOUBLE, precision 22, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TIMES_DAILY.src.DIM_TIMES_DAILY#map12] |
| DAYS_IN_CAL_YEAR | DIM_TIMES_DAILY.DAYS_IN_CAL_YEAR | type DOUBLE, precision 22, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TIMES_DAILY.src.DIM_TIMES_DAILY#map13] |
| END_OF_CAL_MONTH | DIM_TIMES_DAILY.END_OF_CAL_MONTH | type DATETIME, precision 3, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TIMES_DAILY.src.DIM_TIMES_DAILY#map14] |
| END_OF_CAL_QUARTER | DIM_TIMES_DAILY.END_OF_CAL_QUARTER | type DATETIME, precision 3, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TIMES_DAILY.src.DIM_TIMES_DAILY#map15] |
| END_OF_CAL_YEAR | DIM_TIMES_DAILY.END_OF_CAL_YEAR | type DATETIME, precision 3, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TIMES_DAILY.src.DIM_TIMES_DAILY#map16] |
| TIME_ID | DIM_TIMES_DAILY.TIME_ID | type DATETIME, precision 3, nullable not stated | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TIMES_DAILY.src.DIM_TIMES_DAILY#map17] |
| WEEK_ENDING_DATE | DIM_TIMES_DAILY.WEEK_ENDING_DATE | type DATETIME, precision 3, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TIMES_DAILY.src.DIM_TIMES_DAILY#map18] |
| YEAR_M | DIM_TIMES_DAILY.YEAR_M | type INT, nullable not stated | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TIMES_DAILY.src.DIM_TIMES_DAILY#map19] |
| CALENDAR_MONTH_NAME | DIM_TIMES_DAILY.CALENDAR_MONTH_NAME | type VARCHAR, precision 9, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TIMES_DAILY.src.DIM_TIMES_DAILY#map2] |
| YEAR_Q | DIM_TIMES_DAILY.YEAR_Q | type INT, nullable not stated | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TIMES_DAILY.src.DIM_TIMES_DAILY#map20] |
| YEAR_W | DIM_TIMES_DAILY.YEAR_W | type INT, nullable not stated | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TIMES_DAILY.src.DIM_TIMES_DAILY#map21] |
| CALENDAR_MONTH_NUMBER | DIM_TIMES_DAILY.CALENDAR_MONTH_NUMBER | type VARCHAR, precision 2, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TIMES_DAILY.src.DIM_TIMES_DAILY#map3] |
| CALENDAR_QUARTER_NUMBER | DIM_TIMES_DAILY.CALENDAR_QUARTER_NUMBER | type VARCHAR, precision 1, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TIMES_DAILY.src.DIM_TIMES_DAILY#map4] |
| CALENDAR_WEEK_NUMBER | DIM_TIMES_DAILY.CALENDAR_WEEK_NUMBER | type VARCHAR, precision 1, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TIMES_DAILY.src.DIM_TIMES_DAILY#map5] |
| CALENDAR_YEAR | DIM_TIMES_DAILY.CALENDAR_YEAR | type VARCHAR, precision 4, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TIMES_DAILY.src.DIM_TIMES_DAILY#map6] |
| DAY_NAME | DIM_TIMES_DAILY.DAY_NAME | type VARCHAR, precision 9, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TIMES_DAILY.src.DIM_TIMES_DAILY#map7] |
| DAY_NUMBER_IN_MONTH | DIM_TIMES_DAILY.DAY_NUMBER_IN_MONTH | type VARCHAR, precision 2, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TIMES_DAILY.src.DIM_TIMES_DAILY#map8] |
| DAY_NUMBER_IN_WEEK | DIM_TIMES_DAILY.DAY_NUMBER_IN_WEEK | type VARCHAR, precision 1, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TIMES_DAILY.src.DIM_TIMES_DAILY#map9] |

### Points to note

- The key field TIME_ID is mapped from a column of type DATETIME, not a number [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TIMES_DAILY.src.DIM_TIMES_DAILY#map17].
- Calendar numbers such as CALENDAR_YEAR, CALENDAR_MONTH_NUMBER and DAY_NUMBER_IN_MONTH are mapped from VARCHAR columns, while YEAR_M, YEAR_Q and YEAR_W are INT [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TIMES_DAILY.src.DIM_TIMES_DAILY#map6] [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TIMES_DAILY.src.DIM_TIMES_DAILY#map19]. CALENDAR_WEEK_NUMBER is recorded as VARCHAR with precision 1 [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TIMES_DAILY.src.DIM_TIMES_DAILY#map5].
- The source reads a query kept in the repository, not a database table [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TIMES_DAILY.src.DIM_TIMES_DAILY#table0].
