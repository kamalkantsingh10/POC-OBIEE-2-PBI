### Model table Fact Target

Proposal (not from OBIEE; to be signed): a table of the Power BI model named "Fact Target", like the business table, read from the new table SAMP_TARGETS_F.

The business table "Fact Target" is in business model "SAMPLESALES", with 1 table source declared. [kb:sampleapp::bmm::SAMPLESALES.Fact Target] It has 5 columns in this slice, of which 2 are measures; 4 are not confirmed for the tab. The tab uses one of its fields, the measure "Revenue".

#### Keys

Keys of logical table "Fact Target": not recorded. The repository declares no key for it.

#### Where it reads from

| Source | Active | Priority group | Level of detail, exactly | Reads | Evidence |
|---|---|---|---|---|---|
| SAMP_TARGETS_F | true | not recorded (empty; it does not mean lowest) | ` GROUPBYLEVEL("SAMPLESALES"."Time Hierarchy"."Dim Time Detail", "SAMPLESALES"."Office Hierarchy"."Office Detail", "SAMPLESALES"."Products Hierararchy"."Product Detail")` | new table SAMP_TARGETS_F (old "SAMP_TARGETS_F" in `xe.SAMPLESALES`, type `none`) [kb:sampleapp::bmm::SAMPLESALES.Fact Target.src.SAMP_TARGETS_F#table0] | [kb:sampleapp::bmm::SAMPLESALES.Fact Target.src.SAMP_TARGETS_F] |

One source, one table, no join, no content filter. The level of detail names the day level of "Time Hierarchy" ("Dim Time Detail"), while the only time column the source maps is "Bill Mth Key", from BILL_MTH_KEY [kb:sampleapp::bmm::SAMPLESALES.Fact Target.src.SAMP_TARGETS_F#map0]. The level of detail names no level of "Sales Rep Hierarchy", and the source maps no employee column. How targets relate to the time table is set out under Relationships; it decides whether "for the same period" in the FSD's Revenue vs target holds.

Columns the relationships need, not offered as fields:

| Field | Mapped from, exactly | Physical column as printed | Evidence |
|---|---|---|---|
| Bill Mth Key | `"xe".""."SAMPLESALES"."SAMP_TARGETS_F"."BILL_MTH_KEY"` | type DOUBLE, precision 15, nullable true | [kb:sampleapp::bmm::SAMPLESALES.Fact Target.src.SAMP_TARGETS_F#map0] |
| Office Key | `"xe".""."SAMPLESALES"."SAMP_TARGETS_F"."OFFICE_KEY"` | type DOUBLE, precision 15, nullable true | [kb:sampleapp::bmm::SAMPLESALES.Fact Target.src.SAMP_TARGETS_F#map1] |
| Prod Key | `"xe".""."SAMPLESALES"."SAMP_TARGETS_F"."PROD_KEY"` | type DOUBLE, precision 15, nullable true | [kb:sampleapp::bmm::SAMPLESALES.Fact Target.src.SAMP_TARGETS_F#map2] |

#### Fields carried into the model

No field of "Fact Target" other than the measure "Revenue" is confirmed for the tab. The three key fields "Bill Mth Key", "Office Key" and "Prod Key" are not carried as fields; the relationships still need their columns (see Relationships).

#### Measure: Revenue Target

- Replaces: the OBIEE measure "Revenue" of "Fact Target" [kb:sampleapp::bmm::SAMPLESALES.Fact Target.Revenue], shown to report authors as "Revenue" in presentation table "Fact Target" [kb:sampleapp::pres::Sample Sales.Fact Target.Revenue]; confirmed for the tab, items K3, V1-target.
- OBIEE formula it replaces, exactly: ` SUM("SAMPLESALES"."Fact Target"."Revenue")` [kb:sampleapp::bmm::SAMPLESALES.Fact Target.Revenue#aggr0]
- Read from: new table SAMP_TARGETS_F, column REVENUE: `"xe".""."SAMPLESALES"."SAMP_TARGETS_F"."REVENUE"`, physical column type DOUBLE, precision 8, scale 2, nullable true. [kb:sampleapp::bmm::SAMPLESALES.Fact Target.src.SAMP_TARGETS_F#map3]
- Writeable in OBIEE: true. A model measure cannot be typed into: decision Q6. The definition below is read-only.
- Proposal (not from OBIEE; to be signed):

      Revenue Target = SUM('Fact Target'[Revenue])

The name Revenue Target is proposed so that it is not confused with the measure Revenue of Fact Revenue; report authors see both as "Revenue" today.

#### Not used by this tab

The measure "Units" of "Fact Target" is not confirmed for the tab and is not designed; it is listed under Not described.
