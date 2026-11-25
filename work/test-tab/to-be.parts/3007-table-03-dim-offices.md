### Model table Dim Offices

Proposal (not from OBIEE; to be signed): a table of the Power BI model named "Dim Offices", like the business table, read from the new table SAMP_OFFICES_D.

The business table "Dim Offices" is in business model "SAMPLESALES", with 1 table source declared. [kb:sampleapp::bmm::SAMPLESALES.Dim Offices] It has 10 columns in this slice, none of them measures; 9 are not confirmed for the tab. The tab uses one field, "Office Dsc".

#### Keys

Key "SAMP_OFFICES_D_Key": marked as primary key true; made of column(s) "Office Key". [kb:sampleapp::bmm::SAMPLESALES.Dim Offices.key.SAMP_OFFICES_D_Key]

Proposal (not from OBIEE; to be signed): "Office Key" is the key of the model table Dim Offices, the one side of its relationships.

#### Where it reads from

| Source | Active | Priority group | Level of detail, exactly | Reads | Evidence |
|---|---|---|---|---|---|
| SAMP_OFFICES_D | true | not recorded (empty; it does not mean lowest) | ` GROUPBYLEVEL("SAMPLESALES"."Office Hierarchy"."Office Detail")` | new table SAMP_OFFICES_D (old "SAMP_OFFICES_D" in `xe.SAMPLESALES`, type `none`) [kb:sampleapp::bmm::SAMPLESALES.Dim Offices.src.SAMP_OFFICES_D#table0] | [kb:sampleapp::bmm::SAMPLESALES.Dim Offices.src.SAMP_OFFICES_D] |

One source, one table, no join, no content filter.

#### Fields carried into the model

| Field | From new table.column | Type, precision, nullable as the packet prints them | Tab items | Evidence |
|---|---|---|---|---|
| Office Dsc | SAMP_OFFICES_D.OFFICE_DSC, mapped as `"xe".""."SAMPLESALES"."SAMP_OFFICES_D"."OFFICE_DSC"` | type VARCHAR, precision 17, nullable true | F2, V2-office | [kb:sampleapp::bmm::SAMPLESALES.Dim Offices.src.SAMP_OFFICES_D#map4] |

"Office Dsc" is shown to report authors as "Office Dsc" in presentation table "Dim Offices"; name set by hand: false; confirmed for the tab, items F2 and V2-office. [kb:sampleapp::pres::Sample Sales.Dim Offices.Office Dsc]

The 9 fields not confirmed ("Area Name", "Company", "Company Key", "Department", "Dept Key", "Office Key", "Org Key", "Organization", "Sequence") are not carried as fields. "Office Key" is the key field: the relationships need its column, mapped as `"xe".""."SAMPLESALES"."SAMP_OFFICES_D"."OFFICE_KEY"` (type DOUBLE, precision 15, nullable not stated) [kb:sampleapp::bmm::SAMPLESALES.Dim Offices.src.SAMP_OFFICES_D#map5]; see Relationships. The drill path "Office Hierarchy" may need others; see Hierarchies.

#### Measures

None: the table holds no measure.
