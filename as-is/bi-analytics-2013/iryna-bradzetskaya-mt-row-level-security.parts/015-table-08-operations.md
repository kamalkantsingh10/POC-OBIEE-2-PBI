## OPERATIONS

OPERATIONS is a business table ("logical table") of the business model "Iryna_Bradzetskaya_MT_Row-level_Security"; the repository declares 1 source for it [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.OPERATIONS]. In this subject area it has 8 fields and no measures, and all 8 are shown to report authors; its one source is "DIM_OPERATIONS", which reads two database tables (counted; no Row identifier).

### Keys

| Key | Primary key (as recorded) | Fields | Evidence |
|---|---|---|---|
| PK_DIM_OPERATIONS | true | OPERATION_ID | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.OPERATIONS.key.PK_DIM_OPERATIONS] |

### Measures

This table has no measures.

### Fields

Every field is shown to report authors under its own name in the table OPERATIONS. None can be typed into. The definition kept on each field is one database column of the same name: four in DIM_OPERATIONS and four in DIM_OPERATION_METHODS.

| Field | Shown to report authors as | Kept definition | In sources | Evidence |
|---|---|---|---|---|
| OPERATION_ID | OPERATION_ID | DIM_OPERATIONS.OPERATION_ID | 1 of 1: "DIM_OPERATIONS" | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.OPERATIONS.OPERATION_ID] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.OPERATIONS.OPERATION_ID] |
| OPERATION_MAX_AMOUNT | OPERATION_MAX_AMOUNT | DIM_OPERATIONS.OPERATION_MAX_AMOUNT | 1 of 1: "DIM_OPERATIONS" | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.OPERATIONS.OPERATION_MAX_AMOUNT] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.OPERATIONS.OPERATION_MAX_AMOUNT] |
| OPERATION_METHOD_ID | OPERATION_METHOD_ID | DIM_OPERATION_METHODS.OPERATION_METHOD_ID | 1 of 1: "DIM_OPERATIONS" | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.OPERATIONS.OPERATION_METHOD_ID] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.OPERATIONS.OPERATION_METHOD_ID] |
| OPERATION_METHOD_NAME | OPERATION_METHOD_NAME | DIM_OPERATION_METHODS.OPERATION_METHOD_NAME | 1 of 1: "DIM_OPERATIONS" | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.OPERATIONS.OPERATION_METHOD_NAME] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.OPERATIONS.OPERATION_METHOD_NAME] |
| OPERATION_METHOD_TYPE | OPERATION_METHOD_TYPE | DIM_OPERATION_METHODS.OPERATION_METHOD_TYPE | 1 of 1: "DIM_OPERATIONS" | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.OPERATIONS.OPERATION_METHOD_TYPE] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.OPERATIONS.OPERATION_METHOD_TYPE] |
| OPERATION_METHOD_TYPE_ID | OPERATION_METHOD_TYPE_ID | DIM_OPERATION_METHODS.OPERATION_METHOD_TYPE_ID | 1 of 1: "DIM_OPERATIONS" | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.OPERATIONS.OPERATION_METHOD_TYPE_ID] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.OPERATIONS.OPERATION_METHOD_TYPE_ID] |
| OPERATION_MIN_AMOUNT | OPERATION_MIN_AMOUNT | DIM_OPERATIONS.OPERATION_MIN_AMOUNT | 1 of 1: "DIM_OPERATIONS" | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.OPERATIONS.OPERATION_MIN_AMOUNT] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.OPERATIONS.OPERATION_MIN_AMOUNT] |
| OPERATION_NAME | OPERATION_NAME | DIM_OPERATIONS.OPERATION_NAME | 1 of 1: "DIM_OPERATIONS" | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.OPERATIONS.OPERATION_NAME] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.OPERATIONS.OPERATION_NAME] |

OPERATION_MIN_AMOUNT and OPERATION_MAX_AMOUNT are plain fields, not measures [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.OPERATIONS.OPERATION_MIN_AMOUNT] [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.OPERATIONS.OPERATION_MAX_AMOUNT].

### Measures and fields report authors do not see

This table has no measures or fields that report authors do not see.

### Where the data comes from

OPERATIONS has one source.

#### Source "DIM_OPERATIONS"

The source is active; its priority group is not recorded (empty; it does not mean lowest) [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.OPERATIONS.src.DIM_OPERATIONS]. Level of detail (grain): not recorded. The repository gives this source no level of detail [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.OPERATIONS.src.DIM_OPERATIONS]. The packet says nothing about a parent-child hierarchy for this source.

No content filter is recorded for this source.

**Database tables it reads.** The source is built on 2 database tables, so it joins them.

| Database table | Schema | Type | Evidence |
|---|---|---|---|
| DIM_OPERATIONS (see the section Database table DIM_OPERATIONS) | `Iryna_Bradzetskaya_MT.U_SAL` | `none` | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.OPERATIONS.src.DIM_OPERATIONS#table0] |
| DIM_OPERATION_METHODS (see the section Database table DIM_OPERATION_METHODS); a query kept in the repository | `Iryna_Bradzetskaya_MT.U_SAL` | `select` | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.OPERATIONS.src.DIM_OPERATIONS#table1] |

**Joins between them.** The packet gives 2 lines for 1 join: the repository lists a join once under each of its two tables. "Left" and "right" in a join type, and the cardinality, read from the first table named on the line to the second.

| Listed under | From | To | Join type | Cardinality | On | Evidence |
|---|---|---|---|---|---|---|
| DIM_OPERATIONS | DIM_OPERATIONS | DIM_OPERATION_METHODS | `LeftOuter` | `MANY TO ONE` | foreign key "DIM_OPERATIONS_Foreign Key": DIM_OPERATIONS (OPERATION_METHOD_ID) = DIM_OPERATION_METHODS (OPERATION_METHOD_ID) | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.OPERATIONS.src.DIM_OPERATIONS#table0.join0] |
| DIM_OPERATION_METHODS | DIM_OPERATION_METHODS | DIM_OPERATIONS | `RightOuter` | ONE TO MANY | foreign key "DIM_OPERATIONS_Foreign Key": DIM_OPERATIONS (OPERATION_METHOD_ID) = DIM_OPERATION_METHODS (OPERATION_METHOD_ID) | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.OPERATIONS.src.DIM_OPERATIONS#table1.join0] |

The cardinality "ONE TO MANY" is printed in the packet with a leading space; it is copied here without it.

**Mapping.** Every mapping of this source is one database column.

| Field | Database column | Type, precision, nullable | Evidence |
|---|---|---|---|
| OPERATION_ID | DIM_OPERATIONS.OPERATION_ID | type DOUBLE, precision 22, nullable not stated | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.OPERATIONS.src.DIM_OPERATIONS#map0] |
| OPERATION_MAX_AMOUNT | DIM_OPERATIONS.OPERATION_MAX_AMOUNT | type DOUBLE, precision 22, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.OPERATIONS.src.DIM_OPERATIONS#map1] |
| OPERATION_METHOD_ID | DIM_OPERATION_METHODS.OPERATION_METHOD_ID | type DOUBLE, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.OPERATIONS.src.DIM_OPERATIONS#map2] |
| OPERATION_MIN_AMOUNT | DIM_OPERATIONS.OPERATION_MIN_AMOUNT | type DOUBLE, precision 22, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.OPERATIONS.src.DIM_OPERATIONS#map3] |
| OPERATION_NAME | DIM_OPERATIONS.OPERATION_NAME | type VARCHAR, precision 50, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.OPERATIONS.src.DIM_OPERATIONS#map4] |
| OPERATION_METHOD_NAME | DIM_OPERATION_METHODS.OPERATION_METHOD_NAME | type VARCHAR, precision 30, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.OPERATIONS.src.DIM_OPERATIONS#map5] |
| OPERATION_METHOD_TYPE | DIM_OPERATION_METHODS.OPERATION_METHOD_TYPE | type VARCHAR, precision 50, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.OPERATIONS.src.DIM_OPERATIONS#map6] |
| OPERATION_METHOD_TYPE_ID | DIM_OPERATION_METHODS.OPERATION_METHOD_TYPE_ID | type DOUBLE, precision 22, nullable true | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.OPERATIONS.src.DIM_OPERATIONS#map7] |

For OPERATION_METHOD_ID the packet gives no precision; it prints only "type DOUBLE, nullable true" [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.OPERATIONS.src.DIM_OPERATIONS#map2].

### Points to note

- The field OPERATION_METHOD_ID is mapped from DIM_OPERATION_METHODS, the table on the far side of a `LeftOuter` join, not from the column OPERATION_METHOD_ID of DIM_OPERATIONS that the join uses [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.OPERATIONS.src.DIM_OPERATIONS#map2] [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.OPERATIONS.src.DIM_OPERATIONS#table0.join0].
- DIM_OPERATION_METHODS is a query kept in the repository, not a database table [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.OPERATIONS.src.DIM_OPERATIONS#table1].
