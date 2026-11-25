### Where the data comes from

TRANSACTIONS_DAILY has one source.

#### Source "FCT_TRANSACTIONS_DAILY"

The source is active; its priority group is not recorded (empty; it does not mean lowest) [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TRANSACTIONS_DAILY.src.FCT_TRANSACTIONS_DAILY]. Level of detail (grain): not recorded. The repository gives this source no level of detail [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TRANSACTIONS_DAILY.src.FCT_TRANSACTIONS_DAILY]. The packet says nothing about a parent-child hierarchy for this source.

**Content filter.** The source has a content filter. It is quoted here exactly as extracted [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TRANSACTIONS_DAILY.src.FCT_TRANSACTIONS_DAILY]:

```
(
Position (';Role_MT_A;' in ';' || valueof(NQ_SESSION.ROLEGUIDS) || ';') > 0 and  "Iryna_Bradzetskaya_MT".""."U_SAL"."FCT_TRANSACTIONS_DAILY"."CURRENCY_SUR_ID"   in (12,10,15,11)
)
or
(
Position (';Role_MT_F;' in ';' || valueof(NQ_SESSION.ROLEGUIDS) || ';') > 0 and "Iryna_Bradzetskaya_MT".""."U_SAL"."FCT_TRANSACTIONS_DAILY"."CURRENCY_SUR_ID"  in (13,14, 16,17)
)
```

Put mechanically: the filter has two bracketed conditions joined by "or". The first looks for the text ";Role_MT_A;" inside the value set per user at login `ROLEGUIDS` (framed by ";" on both sides) and requires the database column FCT_TRANSACTIONS_DAILY.CURRENCY_SUR_ID to be one of 12, 10, 15, 11. The second does the same with ";Role_MT_F;" and the values 13, 14, 16, 17 [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TRANSACTIONS_DAILY.src.FCT_TRANSACTIONS_DAILY]. The variable `ROLEGUIDS` is described in the section Values set per user or for the whole system. Which currencies the numbers stand for, and what a user who holds neither role, or both, is meant to see, are not recorded (open question Q3).

**Row filter.** A row filter also restricts this source; it is described in the section Security (listed in the packet without a Row identifier).

**Database tables it reads.** The source is built on 5 database tables, so it joins them. Four of the five are queries kept in the repository (type `select`), not tables of the database, and no field of this subject area is mapped from those four; the source still joins them.

| Database table | Schema | Type | Evidence |
|---|---|---|---|
| DIM_CUSTOMERS_REC (see the section Database table DIM_CUSTOMERS_REC); no field is mapped from it | `Iryna_Bradzetskaya_MT.U_SAL` | `select` | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TRANSACTIONS_DAILY.src.FCT_TRANSACTIONS_DAILY#table0] |
| DIM_LOCATIONS_SEND (see the section Database table DIM_LOCATIONS_SEND); no field is mapped from it | `Iryna_Bradzetskaya_MT.U_SAL` | `select` | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TRANSACTIONS_DAILY.src.FCT_TRANSACTIONS_DAILY#table1] |
| FCT_TRANSACTIONS_DAILY (see the section Database table FCT_TRANSACTIONS_DAILY) | `Iryna_Bradzetskaya_MT.U_SAL` | `none` | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TRANSACTIONS_DAILY.src.FCT_TRANSACTIONS_DAILY#table2] |
| DIM_CUSTOMERS_SEND (see the section Database table DIM_CUSTOMERS_SEND); no field is mapped from it | `Iryna_Bradzetskaya_MT.U_SAL` | `select` | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TRANSACTIONS_DAILY.src.FCT_TRANSACTIONS_DAILY#table3] |
| DIM_LOCATIONS_REC (see the section Database table DIM_LOCATIONS_REC); no field is mapped from it | `Iryna_Bradzetskaya_MT.U_SAL` | `select` | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TRANSACTIONS_DAILY.src.FCT_TRANSACTIONS_DAILY#table4] |

**Joins between them.** The packet gives 8 lines for 4 joins: the repository lists a join once under each of its two tables. "Left" and "right" in a join type, and the cardinality, read from the first table named on the line to the second. Join type and cardinality are copied as printed.

| Listed under | From | To | Join type | Cardinality | On | Evidence |
|---|---|---|---|---|---|---|
| DIM_CUSTOMERS_REC | DIM_CUSTOMERS_REC | FCT_TRANSACTIONS_DAILY | `Inner` | ONE TO MANY | foreign key "FK_TR_CUST_REC": FCT_TRANSACTIONS_DAILY (CUS_REC_ID) = DIM_CUSTOMERS_REC (CUST_ID) | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TRANSACTIONS_DAILY.src.FCT_TRANSACTIONS_DAILY#table0.join0] |
| DIM_LOCATIONS_SEND | DIM_LOCATIONS_SEND | FCT_TRANSACTIONS_DAILY | `Inner` | ONE TO MANY | foreign key "FK_TRANS_LOC_SEND": FCT_TRANSACTIONS_DAILY (GEO_SEND_SUR_ID) = DIM_LOCATIONS_SEND (GEO_ID) | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TRANSACTIONS_DAILY.src.FCT_TRANSACTIONS_DAILY#table1.join0] |
| FCT_TRANSACTIONS_DAILY | FCT_TRANSACTIONS_DAILY | DIM_LOCATIONS_REC | `LeftOuter` | `MANY TO ONE` | foreign key "FK_TRANS_LOC_REC": FCT_TRANSACTIONS_DAILY (GEO_REC_SUR_ID) = DIM_LOCATIONS_REC (GEO_ID) | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TRANSACTIONS_DAILY.src.FCT_TRANSACTIONS_DAILY#table2.join0] |
| FCT_TRANSACTIONS_DAILY | FCT_TRANSACTIONS_DAILY | DIM_LOCATIONS_SEND | `Inner` | `MANY TO ONE` | foreign key "FK_TRANS_LOC_SEND": FCT_TRANSACTIONS_DAILY (GEO_SEND_SUR_ID) = DIM_LOCATIONS_SEND (GEO_ID) | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TRANSACTIONS_DAILY.src.FCT_TRANSACTIONS_DAILY#table2.join1] |
| FCT_TRANSACTIONS_DAILY | FCT_TRANSACTIONS_DAILY | DIM_CUSTOMERS_SEND | `Inner` | `MANY TO ONE` | foreign key "FK_TR_CUST_SEND": FCT_TRANSACTIONS_DAILY (CUST_SEND_ID) = DIM_CUSTOMERS_SEND (CUST_ID) | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TRANSACTIONS_DAILY.src.FCT_TRANSACTIONS_DAILY#table2.join2] |
| FCT_TRANSACTIONS_DAILY | FCT_TRANSACTIONS_DAILY | DIM_CUSTOMERS_REC | `Inner` | `MANY TO ONE` | foreign key "FK_TR_CUST_REC": FCT_TRANSACTIONS_DAILY (CUS_REC_ID) = DIM_CUSTOMERS_REC (CUST_ID) | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TRANSACTIONS_DAILY.src.FCT_TRANSACTIONS_DAILY#table2.join3] |
| DIM_CUSTOMERS_SEND | DIM_CUSTOMERS_SEND | FCT_TRANSACTIONS_DAILY | `Inner` | ONE TO MANY | foreign key "FK_TR_CUST_SEND": FCT_TRANSACTIONS_DAILY (CUST_SEND_ID) = DIM_CUSTOMERS_SEND (CUST_ID) | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TRANSACTIONS_DAILY.src.FCT_TRANSACTIONS_DAILY#table3.join0] |
| DIM_LOCATIONS_REC | DIM_LOCATIONS_REC | FCT_TRANSACTIONS_DAILY | `RightOuter` | ONE TO MANY | foreign key "FK_TRANS_LOC_REC": FCT_TRANSACTIONS_DAILY (GEO_REC_SUR_ID) = DIM_LOCATIONS_REC (GEO_ID) | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TRANSACTIONS_DAILY.src.FCT_TRANSACTIONS_DAILY#table4.join0] |

The cardinality "ONE TO MANY" is printed in the packet with a leading space; it is copied here without it.

**Mapping.** Every mapping of this source is one database column of FCT_TRANSACTIONS_DAILY.

| Field | Database column | Type, precision, nullable | Evidence |
|---|---|---|---|
| AMOUNT_PAYMENT | FCT_TRANSACTIONS_DAILY.AMOUNT_PAYMENT | type DOUBLE, precision 22, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TRANSACTIONS_DAILY.src.FCT_TRANSACTIONS_DAILY#map0] |
| COUNT_PAYMENT | FCT_TRANSACTIONS_DAILY.COUNT_PAYMENT | type DOUBLE, precision 22, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TRANSACTIONS_DAILY.src.FCT_TRANSACTIONS_DAILY#map1] |
| CURRENCY_SUR_ID | FCT_TRANSACTIONS_DAILY.CURRENCY_SUR_ID | type DOUBLE, precision 22, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TRANSACTIONS_DAILY.src.FCT_TRANSACTIONS_DAILY#map2] |
| CUS_REC_ID | FCT_TRANSACTIONS_DAILY.CUS_REC_ID | type DOUBLE, precision 22, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TRANSACTIONS_DAILY.src.FCT_TRANSACTIONS_DAILY#map3] |
| CUST_SEND_ID | FCT_TRANSACTIONS_DAILY.CUST_SEND_ID | type DOUBLE, precision 22, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TRANSACTIONS_DAILY.src.FCT_TRANSACTIONS_DAILY#map4] |
| EVENT_DT | FCT_TRANSACTIONS_DAILY.EVENT_DT | type DATETIME, precision 22, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TRANSACTIONS_DAILY.src.FCT_TRANSACTIONS_DAILY#map5] |
| GEO_REC_SUR_ID | FCT_TRANSACTIONS_DAILY.GEO_REC_SUR_ID | type DOUBLE, precision 22, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TRANSACTIONS_DAILY.src.FCT_TRANSACTIONS_DAILY#map6] |
| GEO_SEND_SUR_ID | FCT_TRANSACTIONS_DAILY.GEO_SEND_SUR_ID | type DOUBLE, precision 22, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TRANSACTIONS_DAILY.src.FCT_TRANSACTIONS_DAILY#map7] |
| OPERATION_ID | FCT_TRANSACTIONS_DAILY.OPERATION_ID | type DOUBLE, precision 22, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TRANSACTIONS_DAILY.src.FCT_TRANSACTIONS_DAILY#map8] |
| TARIFF_ID | FCT_TRANSACTIONS_DAILY.TARIFF_ID | type DOUBLE, precision 22, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TRANSACTIONS_DAILY.src.FCT_TRANSACTIONS_DAILY#map9] |

PAYMENT_WHIS_TAX has no mapping in this source; it is calculated from AMOUNT_PAYMENT (see Fields above).

### Points to note

- The only source of TRANSACTIONS_DAILY has a content filter that reads the user's roles through `ROLEGUIDS` and restricts CURRENCY_SUR_ID to fixed lists of numbers, and a row filter in the section Security restricts it as well [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TRANSACTIONS_DAILY.src.FCT_TRANSACTIONS_DAILY] (open question Q3).
- The source joins four tables from which no field is mapped. Three of the four joins are `Inner`; the join to DIM_LOCATIONS_REC is listed as `LeftOuter` under FCT_TRANSACTIONS_DAILY and `RightOuter` under DIM_LOCATIONS_REC [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TRANSACTIONS_DAILY.src.FCT_TRANSACTIONS_DAILY#table2.join0] [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TRANSACTIONS_DAILY.src.FCT_TRANSACTIONS_DAILY#table4.join0].
- PAYMENT_WHIS_TAX is calculated from the measure AMOUNT_PAYMENT multiplied by 20 and is not itself a measure [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TRANSACTIONS_DAILY.PAYMENT_WHIS_TAX] (open question Q2).
- No key is declared for the business table TRANSACTIONS_DAILY (listed in the packet without a Row identifier).
