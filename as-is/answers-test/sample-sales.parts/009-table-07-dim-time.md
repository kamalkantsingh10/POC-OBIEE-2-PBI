## Dim Time

"Dim Time" is a business table of the business model "SAMPLESALES"; the repository declares 2 sources for it [kb:sampleapp::bmm::SAMPLESALES.Dim Time]. In this subject area it has 6 fields, none of them measures, and all 6 are shown to report authors (counted). Its two sources are "LTS Time Day" and "LTS Time Month" (counted).

### Keys

| Key | Primary key (as recorded) | Fields | Evidence |
|---|---|---|---|
| SAMP_TIME_DAY_D_Key | true | Calendar Date | [kb:sampleapp::bmm::SAMPLESALES.Dim Time.key.SAMP_TIME_DAY_D_Key] |

### Measures

This table has no measures.

### Fields

For every field, the name shown to users is a formula, not a fixed text; the formulas are quoted in the third column.

| Field | In sources | Name shown to users, as written | Evidence |
|---|---|---|---|
| Calendar Date | 1 of 2: "LTS Time Day" | `VALUEOF(NQ_SESSION.CN_Sample_Sales_Dim_Time_Calendar_Date)` | [kb:sampleapp::bmm::SAMPLESALES.Dim Time.Calendar Date] [kb:sampleapp::pres::Sample Sales.Dim Time.Calendar Date] |
| Per Name Half | 2 of 2: "LTS Time Day", "LTS Time Month" | `VALUEOF(NQ_SESSION.CN_Sample_Sales_Dim_Time_Per_Name_Half)` | [kb:sampleapp::bmm::SAMPLESALES.Dim Time.Per Name Half] [kb:sampleapp::pres::Sample Sales.Dim Time.Per Name Half] |
| Per Name Month | 2 of 2: "LTS Time Day", "LTS Time Month" | `VALUEOF(NQ_SESSION.CN_Sample_Sales_Dim_Time_Per_Name_Month)` | [kb:sampleapp::bmm::SAMPLESALES.Dim Time.Per Name Month] [kb:sampleapp::pres::Sample Sales.Dim Time.Per Name Month] |
| Per Name Qtr | 2 of 2: "LTS Time Day", "LTS Time Month" | `VALUEOF(NQ_SESSION.CN_Sample_Sales_Dim_Time_Per_Name_Qtr)` | [kb:sampleapp::bmm::SAMPLESALES.Dim Time.Per Name Qtr] [kb:sampleapp::pres::Sample Sales.Dim Time.Per Name Qtr] |
| Per Name Week | 1 of 2: "LTS Time Day" | `VALUEOF(NQ_SESSION.CN_Sample_Sales_Dim_Time_Per_Name_Week)` | [kb:sampleapp::bmm::SAMPLESALES.Dim Time.Per Name Week] [kb:sampleapp::pres::Sample Sales.Dim Time.Per Name Week] |
| Per Name Year | 2 of 2: "LTS Time Day", "LTS Time Month" | `VALUEOF(NQ_SESSION.CN_Sample_Sales_Dim_Time_Per_Name_Year)` | [kb:sampleapp::bmm::SAMPLESALES.Dim Time.Per Name Year] [kb:sampleapp::pres::Sample Sales.Dim Time.Per Name Year] |

Each formula names a variable; the section Values set per user or for the whole system says whether the repository defines it. The names report authors actually see are not recorded (open question Q1).

### Measures and fields report authors do not see

Every field of this table is shown to report authors (counted).

### Where the data comes from

#### Source "LTS Time Day"

Active: true. Priority group not recorded (empty; it does not mean lowest) [kb:sampleapp::bmm::SAMPLESALES.Dim Time.src.LTS Time Day]. Level of detail, exactly: `GROUPBYLEVEL("SAMPLESALES"."Time Hierarchy"."Dim Time Detail")`, the level "Dim Time Detail" of the drill path "Time Hierarchy" [kb:sampleapp::bmm::SAMPLESALES.Dim Time.src.LTS Time Day].

No content filter is recorded for this source.

| Database table | Schema | Type | Evidence |
|---|---|---|---|
| SAMP_TIME_DAY_D (see the section Database table SAMP_TIME_DAY_D) | `xe.SAMPLESALES` | `none` | [kb:sampleapp::bmm::SAMPLESALES.Dim Time.src.LTS Time Day#table0] |

This source reads one table, so it makes no join.

| Field | Database column | Type, precision, nullable | Evidence |
|---|---|---|---|
| Per Name Year | SAMP_TIME_DAY_D.PER_NAME_YEAR | type VARCHAR, precision 4, nullable not stated | [kb:sampleapp::bmm::SAMPLESALES.Dim Time.src.LTS Time Day#map0] |
| Per Name Week | SAMP_TIME_DAY_D.PER_NAME_WEEK | type VARCHAR, precision 12, nullable not stated | [kb:sampleapp::bmm::SAMPLESALES.Dim Time.src.LTS Time Day#map1] |
| Per Name Half | SAMP_TIME_DAY_D.PER_NAME_HALF | type VARCHAR, precision 8, nullable not stated | [kb:sampleapp::bmm::SAMPLESALES.Dim Time.src.LTS Time Day#map2] |
| Per Name Month | SAMP_TIME_DAY_D.PER_NAME_MONTH | type VARCHAR, precision 9, nullable not stated | [kb:sampleapp::bmm::SAMPLESALES.Dim Time.src.LTS Time Day#map3] |
| Per Name Qtr | SAMP_TIME_DAY_D.PER_NAME_QTR | type VARCHAR, precision 7, nullable not stated | [kb:sampleapp::bmm::SAMPLESALES.Dim Time.src.LTS Time Day#map4] |
| Calendar Date | SAMP_TIME_DAY_D.CALENDAR_DATE | type DATE, nullable not stated | [kb:sampleapp::bmm::SAMPLESALES.Dim Time.src.LTS Time Day#map5] |

#### Source "LTS Time Month"

Active: true. Priority group not recorded (empty; it does not mean lowest) [kb:sampleapp::bmm::SAMPLESALES.Dim Time.src.LTS Time Month]. Level of detail, exactly: `GROUPBYLEVEL("SAMPLESALES"."Time Hierarchy"."Month")`, the level "Month" of the drill path "Time Hierarchy" [kb:sampleapp::bmm::SAMPLESALES.Dim Time.src.LTS Time Month].

No content filter is recorded for this source.

| Database table | Schema | Type | Evidence |
|---|---|---|---|
| SAMP_TIME_MTH_D (see the section Database table SAMP_TIME_MTH_D) | `xe.SAMPLESALES` | `none` | [kb:sampleapp::bmm::SAMPLESALES.Dim Time.src.LTS Time Month#table0] |

This source reads one table, so it makes no join.

| Field | Database column | Type, precision, nullable | Evidence |
|---|---|---|---|
| Per Name Year | SAMP_TIME_MTH_D.PER_NAME_YEAR | type VARCHAR, precision 4, nullable true | [kb:sampleapp::bmm::SAMPLESALES.Dim Time.src.LTS Time Month#map0] |
| Per Name Half | SAMP_TIME_MTH_D.PER_NAME_HALF | type VARCHAR, precision 8, nullable true | [kb:sampleapp::bmm::SAMPLESALES.Dim Time.src.LTS Time Month#map1] |
| Per Name Month | SAMP_TIME_MTH_D.PER_NAME_MONTH | type VARCHAR, precision 9, nullable true | [kb:sampleapp::bmm::SAMPLESALES.Dim Time.src.LTS Time Month#map2] |
| Per Name Qtr | SAMP_TIME_MTH_D.PER_NAME_QTR | type VARCHAR, precision 7, nullable true | [kb:sampleapp::bmm::SAMPLESALES.Dim Time.src.LTS Time Month#map3] |

### Points to note

- "LTS Time Month" maps neither "Calendar Date" nor "Per Name Week"; "LTS Time Day" maps all six fields [kb:sampleapp::bmm::SAMPLESALES.Dim Time.Calendar Date] [kb:sampleapp::bmm::SAMPLESALES.Dim Time.Per Name Week].
- The name shown to users of every field is a VALUEOF formula that names a variable [kb:sampleapp::pres::Sample Sales.Dim Time.Calendar Date] (open question Q1).
