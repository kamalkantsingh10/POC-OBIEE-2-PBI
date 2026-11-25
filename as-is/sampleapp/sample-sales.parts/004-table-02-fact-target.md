## Fact Target

"Fact Target" is a business table of the business model "SAMPLESALES"; the repository declares 1 source for it [kb:sampleapp::bmm::SAMPLESALES.Fact Target]. In this subject area it has 5 fields, of which 2 are measures; all 5 are shown to report authors (counted; no Row identifier). Its one source is "SAMP_TARGETS_F" (counted; no Row identifier).

### Keys

Keys of the business table "Fact Target": not recorded. The repository declares no key for it (listed in the packet without a Row identifier).

### Measures

Both measures are shown to report authors in the table "Fact Target" under the same name.

| Measure | How it is summarised | Can be typed into | In sources | Evidence |
|---|---|---|---|---|
| Revenue | `SUM("SAMPLESALES"."Fact Target"."Revenue")` | yes | 1 of 1: "SAMP_TARGETS_F" | [kb:sampleapp::bmm::SAMPLESALES.Fact Target.Revenue] [kb:sampleapp::pres::Sample Sales.Fact Target.Revenue] [kb:sampleapp::bmm::SAMPLESALES.Fact Target.Revenue#aggr0] |
| Units | `SUM("SAMPLESALES"."Fact Target"."Units")` | yes | 1 of 1: "SAMP_TARGETS_F" | [kb:sampleapp::bmm::SAMPLESALES.Fact Target.Units] [kb:sampleapp::pres::Sample Sales.Fact Target.Units] [kb:sampleapp::bmm::SAMPLESALES.Fact Target.Units#aggr0] |

Both measures are recorded as writeable (open question Q7).

### Fields

All fields below are shown to report authors in the table "Fact Target" under the same name.

| Field | In sources | Evidence |
|---|---|---|
| Bill Mth Key | 1 of 1: "SAMP_TARGETS_F" | [kb:sampleapp::bmm::SAMPLESALES.Fact Target.Bill Mth Key] [kb:sampleapp::pres::Sample Sales.Fact Target.Bill Mth Key] |
| Office Key | 1 of 1: "SAMP_TARGETS_F" | [kb:sampleapp::bmm::SAMPLESALES.Fact Target.Office Key] [kb:sampleapp::pres::Sample Sales.Fact Target.Office Key] |
| Prod Key | 1 of 1: "SAMP_TARGETS_F" | [kb:sampleapp::bmm::SAMPLESALES.Fact Target.Prod Key] [kb:sampleapp::pres::Sample Sales.Fact Target.Prod Key] |

### Measures and fields report authors do not see

This table has no measures or fields that report authors do not see.

### Where the data comes from

#### Source "SAMP_TARGETS_F"

Active: true. Priority group not recorded (empty; it does not mean lowest) [kb:sampleapp::bmm::SAMPLESALES.Fact Target.src.SAMP_TARGETS_F]. Level of detail, exactly [kb:sampleapp::bmm::SAMPLESALES.Fact Target.src.SAMP_TARGETS_F]:

```
GROUPBYLEVEL("SAMPLESALES"."Time Hierarchy"."Dim Time Detail", "SAMPLESALES"."Office Hierarchy"."Office Detail", "SAMPLESALES"."Products Hierararchy"."Product Detail")
```

That is the level "Dim Time Detail" of "Time Hierarchy", "Office Detail" of "Office Hierarchy" and "Product Detail" of "Products Hierararchy" [kb:sampleapp::bmm::SAMPLESALES.Fact Target.src.SAMP_TARGETS_F].

No content filter is recorded for this source.

| Database table | Schema | Type | Evidence |
|---|---|---|---|
| SAMP_TARGETS_F (see the section Database table SAMP_TARGETS_F) | `xe.SAMPLESALES` | `none` | [kb:sampleapp::bmm::SAMPLESALES.Fact Target.src.SAMP_TARGETS_F#table0] |

This source reads one table, so it makes no join.

| Field | Database column | Type, precision, nullable | Evidence |
|---|---|---|---|
| Bill Mth Key | SAMP_TARGETS_F.BILL_MTH_KEY | type DOUBLE, precision 15, nullable true | [kb:sampleapp::bmm::SAMPLESALES.Fact Target.src.SAMP_TARGETS_F#map0] |
| Office Key | SAMP_TARGETS_F.OFFICE_KEY | type DOUBLE, precision 15, nullable true | [kb:sampleapp::bmm::SAMPLESALES.Fact Target.src.SAMP_TARGETS_F#map1] |
| Prod Key | SAMP_TARGETS_F.PROD_KEY | type DOUBLE, precision 15, nullable true | [kb:sampleapp::bmm::SAMPLESALES.Fact Target.src.SAMP_TARGETS_F#map2] |
| Revenue | SAMP_TARGETS_F.REVENUE | type DOUBLE, precision 8, scale 2, nullable true | [kb:sampleapp::bmm::SAMPLESALES.Fact Target.src.SAMP_TARGETS_F#map3] |
| Units | SAMP_TARGETS_F.UNITS | type DOUBLE, precision 15, nullable true | [kb:sampleapp::bmm::SAMPLESALES.Fact Target.src.SAMP_TARGETS_F#map4] |

### Points to note

- Both measures, "Revenue" and "Units", can be typed into (writeable true) [kb:sampleapp::bmm::SAMPLESALES.Fact Target.Revenue] [kb:sampleapp::bmm::SAMPLESALES.Fact Target.Units]. What a typed value does is not recorded (open question Q7).
- The field "Bill Mth Key" is mapped from the column BILL_MTH_KEY, while the level of detail of the source names the level "Dim Time Detail" of "Time Hierarchy" [kb:sampleapp::bmm::SAMPLESALES.Fact Target.src.SAMP_TARGETS_F#map0] [kb:sampleapp::bmm::SAMPLESALES.Fact Target.src.SAMP_TARGETS_F].
