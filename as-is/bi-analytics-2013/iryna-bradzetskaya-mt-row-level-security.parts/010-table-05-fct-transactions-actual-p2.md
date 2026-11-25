### Where the data comes from

FCT_TRANSACTIONS_ACTUAL has one source.

#### Source "FCT_TRANSACTIONS_ACTUAL"

The source is active; its priority group is not recorded (empty; it does not mean lowest) [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.FCT_TRANSACTIONS_ACTUAL.src.FCT_TRANSACTIONS_ACTUAL]. Level of detail (grain): not recorded. The repository gives this source no level of detail [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.FCT_TRANSACTIONS_ACTUAL.src.FCT_TRANSACTIONS_ACTUAL]. The packet says nothing about a parent-child hierarchy for this source.

**Content filter.** The source has a content filter. It is quoted here exactly as extracted [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.FCT_TRANSACTIONS_ACTUAL.src.FCT_TRANSACTIONS_ACTUAL]:

```
(
Position (';MT_User_A;' in ';' || valueof(NQ_SESSION.ROLEGUIDS) || ';') > 0 and  "Iryna_Bradzetskaya_MT".""."U_SAL"."FCT_TRANSACTIONS_ACTUAL"."CURRENCY_SUR_ID"   in (12,10,15,11)
)
or
(
Position (';MT_User_F;' in ';' || valueof(NQ_SESSION.ROLEGUIDS) || ';') > 0 and "Iryna_Bradzetskaya_MT".""."U_SAL"."FCT_TRANSACTIONS_ACTUAL"."CURRENCY_SUR_ID"  in (13,14, 16,17)
)
```

Put mechanically: the filter has two bracketed conditions joined by "or". The first looks for the text ";MT_User_A;" inside the value set per user at login `ROLEGUIDS` (framed by ";" on both sides) and requires the database column FCT_TRANSACTIONS_ACTUAL.CURRENCY_SUR_ID to be one of 12, 10, 15, 11. The second does the same with ";MT_User_F;" and the values 13, 14, 16, 17 [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.FCT_TRANSACTIONS_ACTUAL.src.FCT_TRANSACTIONS_ACTUAL]. The variable `ROLEGUIDS` is described in the section Values set per user or for the whole system. Which currencies the numbers stand for, and what "MT_User_A" and "MT_User_F" name, are not recorded (open question Q3).

**Row filter.** A row filter also restricts this source; it is described in the section Security (listed in the packet without a Row identifier).

**Database tables it reads.** The one table is a query kept in the repository (type `select`), not a table of the database; its query is given in the section Database table FCT_TRANSACTIONS_ACTUAL.

| Database table | Schema | Type | Evidence |
|---|---|---|---|
| FCT_TRANSACTIONS_ACTUAL (see the section Database table FCT_TRANSACTIONS_ACTUAL) | `Iryna_Bradzetskaya_MT.U_SAL` | `select` | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.FCT_TRANSACTIONS_ACTUAL.src.FCT_TRANSACTIONS_ACTUAL#table0] |

This source reads one table, so it makes no join.

**Mapping.** Every mapping of this source is one database column of FCT_TRANSACTIONS_ACTUAL. The rows are in the packet's order.

| Field | Database column | Type, precision, nullable | Evidence |
|---|---|---|---|
| AMOUNT_PAYMENT | FCT_TRANSACTIONS_ACTUAL.AMOUNT_PAYMENT | type DOUBLE, precision 22, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.FCT_TRANSACTIONS_ACTUAL.src.FCT_TRANSACTIONS_ACTUAL#map0] |
| COUNT_PAYMENT | FCT_TRANSACTIONS_ACTUAL.COUNT_PAYMENT | type DOUBLE, precision 22, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.FCT_TRANSACTIONS_ACTUAL.src.FCT_TRANSACTIONS_ACTUAL#map1] |
| PERIOD_ID | FCT_TRANSACTIONS_ACTUAL.PERIOD_ID | type DOUBLE, precision 22, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.FCT_TRANSACTIONS_ACTUAL.src.FCT_TRANSACTIONS_ACTUAL#map10] |
| TA_COUNTRY_REC_ID | FCT_TRANSACTIONS_ACTUAL.TA_COUNTRY_REC_ID | type DOUBLE, precision 22, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.FCT_TRANSACTIONS_ACTUAL.src.FCT_TRANSACTIONS_ACTUAL#map11] |
| TA_COUNTRY_SEND_ID | FCT_TRANSACTIONS_ACTUAL.TA_COUNTRY_SEND_ID | type DOUBLE, precision 22, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.FCT_TRANSACTIONS_ACTUAL.src.FCT_TRANSACTIONS_ACTUAL#map12] |
| TARIFF_ID | FCT_TRANSACTIONS_ACTUAL.TARIFF_ID | type DOUBLE, precision 22, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.FCT_TRANSACTIONS_ACTUAL.src.FCT_TRANSACTIONS_ACTUAL#map13] |
| CURRENCY_SUR_ID | FCT_TRANSACTIONS_ACTUAL.CURRENCY_SUR_ID | type DOUBLE, precision 22, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.FCT_TRANSACTIONS_ACTUAL.src.FCT_TRANSACTIONS_ACTUAL#map2] |
| CUS_REC_ID | FCT_TRANSACTIONS_ACTUAL.CUS_REC_ID | type DOUBLE, precision 22, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.FCT_TRANSACTIONS_ACTUAL.src.FCT_TRANSACTIONS_ACTUAL#map3] |
| CUST_SEND_ID | FCT_TRANSACTIONS_ACTUAL.CUST_SEND_ID | type DOUBLE, precision 22, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.FCT_TRANSACTIONS_ACTUAL.src.FCT_TRANSACTIONS_ACTUAL#map4] |
| EVENT_DT | FCT_TRANSACTIONS_ACTUAL.EVENT_DT | type DATETIME, precision 22, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.FCT_TRANSACTIONS_ACTUAL.src.FCT_TRANSACTIONS_ACTUAL#map5] |
| GEO_REC_SUR_ID | FCT_TRANSACTIONS_ACTUAL.GEO_REC_SUR_ID | type DOUBLE, precision 22, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.FCT_TRANSACTIONS_ACTUAL.src.FCT_TRANSACTIONS_ACTUAL#map6] |
| GEO_SEND_SUR_ID | FCT_TRANSACTIONS_ACTUAL.GEO_SEND_SUR_ID | type DOUBLE, precision 22, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.FCT_TRANSACTIONS_ACTUAL.src.FCT_TRANSACTIONS_ACTUAL#map7] |
| INSERT_DT | FCT_TRANSACTIONS_ACTUAL.INSERT_DT | type DATETIME, precision 22, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.FCT_TRANSACTIONS_ACTUAL.src.FCT_TRANSACTIONS_ACTUAL#map8] |
| OPERATION_ID | FCT_TRANSACTIONS_ACTUAL.OPERATION_ID | type DOUBLE, precision 22, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.FCT_TRANSACTIONS_ACTUAL.src.FCT_TRANSACTIONS_ACTUAL#map9] |

### Points to note

- The table has no measures: AMOUNT_PAYMENT and COUNT_PAYMENT are plain fields [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.FCT_TRANSACTIONS_ACTUAL.AMOUNT_PAYMENT] (open question Q6).
- No key is declared for the business table FCT_TRANSACTIONS_ACTUAL (listed in the packet without a Row identifier).
- The only source has a content filter that reads `ROLEGUIDS` and looks for ";MT_User_A;" and ";MT_User_F;", and a row filter in the section Security restricts it as well [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.FCT_TRANSACTIONS_ACTUAL.src.FCT_TRANSACTIONS_ACTUAL] (open question Q3).
- The source reads a query kept in the repository, not a database table [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.FCT_TRANSACTIONS_ACTUAL.src.FCT_TRANSACTIONS_ACTUAL#table0].
