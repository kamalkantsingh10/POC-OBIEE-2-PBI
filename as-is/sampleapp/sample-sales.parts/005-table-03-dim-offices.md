## Dim Offices

"Dim Offices" is a business table of the business model "SAMPLESALES"; the repository declares 1 source for it [kb:sampleapp::bmm::SAMPLESALES.Dim Offices]. In this subject area it has 10 fields and no measures; 1 of the 10 is not shown to report authors here (counted; no Row identifier). Its one source is "SAMP_OFFICES_D" (counted; no Row identifier).

### Keys

| Key | Primary key (as recorded) | Fields | Evidence |
|---|---|---|---|
| SAMP_OFFICES_D_Key | true | Office Key | [kb:sampleapp::bmm::SAMPLESALES.Dim Offices.key.SAMP_OFFICES_D_Key] |

### Measures

This table has no measures.

### Fields

All fields below are shown to report authors in the table "Dim Offices" under the same name.

| Field | In sources | Evidence |
|---|---|---|
| Area Name | 1 of 1: "SAMP_OFFICES_D" | [kb:sampleapp::bmm::SAMPLESALES.Dim Offices.Area Name] [kb:sampleapp::pres::Sample Sales.Dim Offices.Area Name] |
| Company | 1 of 1: "SAMP_OFFICES_D" | [kb:sampleapp::bmm::SAMPLESALES.Dim Offices.Company] [kb:sampleapp::pres::Sample Sales.Dim Offices.Company] |
| Company Key | 1 of 1: "SAMP_OFFICES_D" | [kb:sampleapp::bmm::SAMPLESALES.Dim Offices.Company Key] [kb:sampleapp::pres::Sample Sales.Dim Offices.Company Key] |
| Department | 1 of 1: "SAMP_OFFICES_D" | [kb:sampleapp::bmm::SAMPLESALES.Dim Offices.Department] [kb:sampleapp::pres::Sample Sales.Dim Offices.Department] |
| Dept Key | 1 of 1: "SAMP_OFFICES_D" | [kb:sampleapp::bmm::SAMPLESALES.Dim Offices.Dept Key] [kb:sampleapp::pres::Sample Sales.Dim Offices.Dept Key] |
| Office Dsc | 1 of 1: "SAMP_OFFICES_D" | [kb:sampleapp::bmm::SAMPLESALES.Dim Offices.Office Dsc] [kb:sampleapp::pres::Sample Sales.Dim Offices.Office Dsc] |
| Office Key | 1 of 1: "SAMP_OFFICES_D" | [kb:sampleapp::bmm::SAMPLESALES.Dim Offices.Office Key] [kb:sampleapp::pres::Sample Sales.Dim Offices.Office Key] |
| Org Key | 1 of 1: "SAMP_OFFICES_D" | [kb:sampleapp::bmm::SAMPLESALES.Dim Offices.Org Key] [kb:sampleapp::pres::Sample Sales.Dim Offices.Org Key] |
| Organization | 1 of 1: "SAMP_OFFICES_D" | [kb:sampleapp::bmm::SAMPLESALES.Dim Offices.Organization] [kb:sampleapp::pres::Sample Sales.Dim Offices.Organization] |

### Measures and fields report authors do not see

This field is not shown to report authors in this subject area. It is described because its business table is part of the model behind the subject area.

| Field | In sources | Evidence |
|---|---|---|
| Sequence | 1 of 1: "SAMP_OFFICES_D" | [kb:sampleapp::bmm::SAMPLESALES.Dim Offices.Sequence] |

### Where the data comes from

#### Source "SAMP_OFFICES_D"

Active: true. Priority group not recorded (empty; it does not mean lowest) [kb:sampleapp::bmm::SAMPLESALES.Dim Offices.src.SAMP_OFFICES_D]. Level of detail, exactly: `GROUPBYLEVEL("SAMPLESALES"."Office Hierarchy"."Office Detail")`, the level "Office Detail" of the drill path "Office Hierarchy" [kb:sampleapp::bmm::SAMPLESALES.Dim Offices.src.SAMP_OFFICES_D].

No content filter is recorded for this source.

| Database table | Schema | Type | Evidence |
|---|---|---|---|
| SAMP_OFFICES_D (see the section Database table SAMP_OFFICES_D) | `xe.SAMPLESALES` | `none` | [kb:sampleapp::bmm::SAMPLESALES.Dim Offices.src.SAMP_OFFICES_D#table0] |

This source reads one table, so it makes no join.

| Field | Database column | Type, precision, nullable | Evidence |
|---|---|---|---|
| Company | SAMP_OFFICES_D.COMPANY | type VARCHAR, precision 14, nullable true | [kb:sampleapp::bmm::SAMPLESALES.Dim Offices.src.SAMP_OFFICES_D#map0] |
| Company Key | SAMP_OFFICES_D.COMPANY_KEY | type DOUBLE, precision 15, nullable not stated | [kb:sampleapp::bmm::SAMPLESALES.Dim Offices.src.SAMP_OFFICES_D#map1] |
| Department | SAMP_OFFICES_D.DEPARTMENT | type VARCHAR, precision 25, nullable true | [kb:sampleapp::bmm::SAMPLESALES.Dim Offices.src.SAMP_OFFICES_D#map2] |
| Dept Key | SAMP_OFFICES_D.DEPT_KEY | type DOUBLE, precision 15, nullable not stated | [kb:sampleapp::bmm::SAMPLESALES.Dim Offices.src.SAMP_OFFICES_D#map3] |
| Office Dsc | SAMP_OFFICES_D.OFFICE_DSC | type VARCHAR, precision 17, nullable true | [kb:sampleapp::bmm::SAMPLESALES.Dim Offices.src.SAMP_OFFICES_D#map4] |
| Office Key | SAMP_OFFICES_D.OFFICE_KEY | type DOUBLE, precision 15, nullable not stated | [kb:sampleapp::bmm::SAMPLESALES.Dim Offices.src.SAMP_OFFICES_D#map5] |
| Org Key | SAMP_OFFICES_D.ORG_KEY | type DOUBLE, precision 15, nullable not stated | [kb:sampleapp::bmm::SAMPLESALES.Dim Offices.src.SAMP_OFFICES_D#map6] |
| Organization | SAMP_OFFICES_D.ORGANIZATION | type VARCHAR, precision 18, nullable true | [kb:sampleapp::bmm::SAMPLESALES.Dim Offices.src.SAMP_OFFICES_D#map7] |
| Sequence | SAMP_OFFICES_D.SEQUENCE | type DOUBLE, precision 15, nullable true | [kb:sampleapp::bmm::SAMPLESALES.Dim Offices.src.SAMP_OFFICES_D#map8] |
| Area Name | SAMP_OFFICES_D.AREA_NAME | type VARCHAR, precision 25, nullable true | [kb:sampleapp::bmm::SAMPLESALES.Dim Offices.src.SAMP_OFFICES_D#map9] |

### Points to note

- The primary key of "Dim Offices" is the field "Office Key" [kb:sampleapp::bmm::SAMPLESALES.Dim Offices.key.SAMP_OFFICES_D_Key], mapped from SAMP_OFFICES_D.OFFICE_KEY [kb:sampleapp::bmm::SAMPLESALES.Dim Offices.src.SAMP_OFFICES_D#map5].
- Every field comes from the one database table SAMP_OFFICES_D, with no calculation [kb:sampleapp::bmm::SAMPLESALES.Dim Offices.src.SAMP_OFFICES_D#table0].
