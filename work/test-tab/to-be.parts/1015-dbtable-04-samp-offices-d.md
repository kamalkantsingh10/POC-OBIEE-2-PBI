### New table SAMP_OFFICES_D

Proposal (not from OBIEE; to be signed): build SAMP_OFFICES_D as a table, like for like from the OBIEE database table "SAMP_OFFICES_D". The old table is in `xe.SAMPLESALES`, type `none`. [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_OFFICES_D] It is not an alias of another physical table. Its object in the database: no separate name is recorded, so the repository knows it only by the name above. Description in the repository: not recorded.

#### Columns

| # | New column | Proposed type | Length or precision | Decimal places | May be empty | From old column | Old field | Used by tab items | Evidence |
|---|---|---|---|---|---|---|---|---|---|
| 1 | COMPANY | text | length 14 | not applicable | yes, as recorded | SAMP_OFFICES_D.COMPANY | "Dim Offices"."Company" | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_OFFICES_D.COMPANY] |
| 2 | COMPANY_KEY | number | precision 15 | missing | missing (not stated in the export) | SAMP_OFFICES_D.COMPANY_KEY | "Dim Offices"."Company Key" | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_OFFICES_D.COMPANY_KEY] |
| 3 | DEPARTMENT | text | length 25 | not applicable | yes, as recorded | SAMP_OFFICES_D.DEPARTMENT | "Dim Offices"."Department" | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_OFFICES_D.DEPARTMENT] |
| 4 | DEPT_KEY | number | precision 15 | missing | missing (not stated in the export) | SAMP_OFFICES_D.DEPT_KEY | "Dim Offices"."Dept Key" | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_OFFICES_D.DEPT_KEY] |
| 5 | OFFICE_DSC | text | length 17 | not applicable | yes, as recorded | SAMP_OFFICES_D.OFFICE_DSC | "Dim Offices"."Office Dsc" | F2, V2-office | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_OFFICES_D.OFFICE_DSC] |
| 6 | OFFICE_KEY | number | precision 15 | missing | missing (not stated in the export) | SAMP_OFFICES_D.OFFICE_KEY | "Dim Offices"."Office Key" | none (key of Dim Offices) | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_OFFICES_D.OFFICE_KEY] |
| 7 | ORGANIZATION | text | length 18 | not applicable | yes, as recorded | SAMP_OFFICES_D.ORGANIZATION | "Dim Offices"."Organization" | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_OFFICES_D.ORGANIZATION] |
| 8 | ORG_KEY | number | precision 15 | missing | missing (not stated in the export) | SAMP_OFFICES_D.ORG_KEY | "Dim Offices"."Org Key" | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_OFFICES_D.ORG_KEY] |
| 9 | SEQUENCE | number | precision 15 | missing | yes, as recorded | SAMP_OFFICES_D.SEQUENCE | "Dim Offices"."Sequence" | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_OFFICES_D.SEQUENCE] |
| 10 | AREA_NAME | text | length 25 | not applicable | yes, as recorded | SAMP_OFFICES_D.AREA_NAME | "Dim Offices"."Area Name" | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_OFFICES_D.AREA_NAME] |

Columns 1 to 9 record the external name `samplesales`; AREA_NAME records none.

#### Key

Key "SAMP_OFFICES_D_Key", made of column(s) "OFFICE_KEY", the only key the repository declares for the table. [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_OFFICES_D.key.SAMP_OFFICES_D_Key] Proposal (not from OBIEE; to be signed): OFFICE_KEY is the key of the new table SAMP_OFFICES_D.

#### Grain

The level of detail is stated in the section Model table Dim Offices.

#### Links to other tables

- Foreign key "SAMP_REVENUE_F_FKey#1" from SAMP_REVENUE_F (OFFICE_KEY) to SAMP_OFFICES_D (OFFICE_KEY): proposed as a foreign key; stated in full under Relationships.
- Foreign key "SAMP_TARGETS_F_FKey#1" from SAMP_TARGETS_F (OFFICE_KEY) to SAMP_OFFICES_D (OFFICE_KEY): proposed as a foreign key; stated in full under Relationships.
- Complex join "Complex Join" between FactCrossJoin and SAMP_OFFICES_D: stated in full under Relationships.

#### Read by

Source "SAMP_OFFICES_D" of the model table Dim Offices.

#### Missing

- Decimal places of COMPANY_KEY, DEPT_KEY, OFFICE_KEY, ORG_KEY and SEQUENCE (scale not recorded).
- Whether COMPANY_KEY, DEPT_KEY, OFFICE_KEY and ORG_KEY must be filled (nullable not stated).
- Indexes, partitioning, volumes, and how and when the table is loaded.
