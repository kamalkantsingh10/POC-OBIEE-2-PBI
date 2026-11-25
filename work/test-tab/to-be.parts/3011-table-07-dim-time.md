### Model table Dim Time

Proposal (not from OBIEE; to be signed): a table of the Power BI model named "Dim Time", like the business table, read from the new table SAMP_TIME_DAY_D and, depending on decision Q8, the new table SAMP_TIME_MTH_D.

The business table "Dim Time" is in business model "SAMPLESALES", with 2 table sources declared. [kb:sampleapp::bmm::SAMPLESALES.Dim Time] It has 6 columns in this slice, none of them measures; 4 are not confirmed for the tab. The tab uses two fields, "Per Name Month" and "Per Name Year".

#### Keys

Key "SAMP_TIME_DAY_D_Key": marked as primary key true; made of column(s) "Calendar Date". [kb:sampleapp::bmm::SAMPLESALES.Dim Time.key.SAMP_TIME_DAY_D_Key]

#### Where it reads from

| Source | Active | Priority group | Level of detail, exactly | Reads | Evidence |
|---|---|---|---|---|---|
| LTS Time Day | true | not recorded (empty; it does not mean lowest) | ` GROUPBYLEVEL("SAMPLESALES"."Time Hierarchy"."Dim Time Detail")` | new table SAMP_TIME_DAY_D (old "SAMP_TIME_DAY_D" in `xe.SAMPLESALES`, type `none`) [kb:sampleapp::bmm::SAMPLESALES.Dim Time.src.LTS Time Day#table0] | [kb:sampleapp::bmm::SAMPLESALES.Dim Time.src.LTS Time Day] |
| LTS Time Month | true | not recorded (empty; it does not mean lowest) | ` GROUPBYLEVEL("SAMPLESALES"."Time Hierarchy"."Month")` | new table SAMP_TIME_MTH_D (old "SAMP_TIME_MTH_D" in `xe.SAMPLESALES`, type `none`) [kb:sampleapp::bmm::SAMPLESALES.Dim Time.src.LTS Time Month#table0] | [kb:sampleapp::bmm::SAMPLESALES.Dim Time.src.LTS Time Month] |

Two sources at different levels of detail, day and month: this is decision Q8. Neither joins tables; no content filter is recorded.

#### Fields carried into the model

| Field | From new table.column | Type, precision, nullable as the packet prints them | Tab items | Evidence |
|---|---|---|---|---|
| Per Name Year | SAMP_TIME_DAY_D.PER_NAME_YEAR, mapped as `"xe".""."SAMPLESALES"."SAMP_TIME_DAY_D"."PER_NAME_YEAR"` | type VARCHAR, precision 4, nullable not stated | F1 | [kb:sampleapp::bmm::SAMPLESALES.Dim Time.src.LTS Time Day#map0] |
| Per Name Year | SAMP_TIME_MTH_D.PER_NAME_YEAR, mapped as `"xe".""."SAMPLESALES"."SAMP_TIME_MTH_D"."PER_NAME_YEAR"` | type VARCHAR, precision 4, nullable true | F1 | [kb:sampleapp::bmm::SAMPLESALES.Dim Time.src.LTS Time Month#map0] |
| Per Name Month | SAMP_TIME_DAY_D.PER_NAME_MONTH, mapped as `"xe".""."SAMPLESALES"."SAMP_TIME_DAY_D"."PER_NAME_MONTH"` | type VARCHAR, precision 9, nullable not stated | V1-month | [kb:sampleapp::bmm::SAMPLESALES.Dim Time.src.LTS Time Day#map3] |
| Per Name Month | SAMP_TIME_MTH_D.PER_NAME_MONTH, mapped as `"xe".""."SAMPLESALES"."SAMP_TIME_MTH_D"."PER_NAME_MONTH"` | type VARCHAR, precision 9, nullable true | V1-month | [kb:sampleapp::bmm::SAMPLESALES.Dim Time.src.LTS Time Month#map2] |

Each confirmed field is mapped in both sources; one row per source.

"Per Name Year" is shown to report authors as "Per Name Year" in presentation table "Dim Time"; name set by hand: false; confirmed for the tab, item F1. [kb:sampleapp::pres::Sample Sales.Dim Time.Per Name Year] Its name shown to users is written as `VALUEOF(NQ_SESSION.CN_Sample_Sales_Dim_Time_Per_Name_Year)`. "Per Name Month" is shown as "Per Name Month"; name set by hand: false; confirmed for the tab, item V1-month. [kb:sampleapp::pres::Sample Sales.Dim Time.Per Name Month] Its name shown to users is written as `VALUEOF(NQ_SESSION.CN_Sample_Sales_Dim_Time_Per_Name_Month)`. A displayed name set by a variable is decision Q9; see also as-is document, question Q1.

The 4 fields not confirmed ("Calendar Date", "Per Name Half", "Per Name Qtr", "Per Name Week") are not carried as fields. "Calendar Date" is the key field: the relationships need its column, mapped as `"xe".""."SAMPLESALES"."SAMP_TIME_DAY_D"."CALENDAR_DATE"` (type DATE, nullable not stated) [kb:sampleapp::bmm::SAMPLESALES.Dim Time.src.LTS Time Day#map5]; see Relationships. The drill path "Time Hierarchy" may need the others; see Hierarchies.

#### Measures

None: the table holds no measure.
