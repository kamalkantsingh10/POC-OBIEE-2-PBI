### New table SAMP_TARGETS_F

Proposal (not from OBIEE; to be signed): build SAMP_TARGETS_F as a table, like for like from the OBIEE database table "SAMP_TARGETS_F". The old table is in `xe.SAMPLESALES`, type `none`. [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TARGETS_F] It is not an alias of another physical table. Its object in the database for database type `DefaultMulDB`: `SAMP_TARGETS_F` [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TARGETS_F.def.DefaultMulDB]. Description in the repository: not recorded. Whether users write targets into it is decision Q6.

#### Columns

| # | New column | Proposed type | Length or precision | Decimal places | May be empty | From old column | Old field | Used by tab items | Evidence |
|---|---|---|---|---|---|---|---|---|---|
| 1 | BILL_MTH_KEY | number | precision 15 | missing | yes, as recorded | SAMP_TARGETS_F.BILL_MTH_KEY | "Fact Target"."Bill Mth Key" | none (links to the time table) | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TARGETS_F.BILL_MTH_KEY] |
| 2 | OFFICE_KEY | number | precision 15 | missing | yes, as recorded | SAMP_TARGETS_F.OFFICE_KEY | "Fact Target"."Office Key" | none (links to Dim Offices) | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TARGETS_F.OFFICE_KEY] |
| 3 | PROD_KEY | number | precision 15 | missing | yes, as recorded | SAMP_TARGETS_F.PROD_KEY | "Fact Target"."Prod Key" | none (links to Dim Products) | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TARGETS_F.PROD_KEY] |
| 4 | REVENUE | number | precision 8 | 2 | yes, as recorded | SAMP_TARGETS_F.REVENUE | "Fact Target"."Revenue" | K3, V1-target | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TARGETS_F.REVENUE] |
| 5 | UNITS | number | precision 15 | missing | yes, as recorded | SAMP_TARGETS_F.UNITS | "Fact Target"."Units" | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TARGETS_F.UNITS] |

Every column records the external name `samplesales`.

#### Key

Missing: the export records no key for this table (question Q12).

#### Grain

The level of detail is stated in the section Model table Fact Target.

#### Links to other tables

- Foreign key "SAMP_TARGETS_F_FKey" from SAMP_TARGETS_F (BILL_MTH_KEY) to SAMP_TIME_MTH_D (MTH_KEY): proposed as a foreign key; stated in full under Relationships.
- Foreign key "SAMP_TARGETS_F_FKey#1" from SAMP_TARGETS_F (OFFICE_KEY) to SAMP_OFFICES_D (OFFICE_KEY): proposed as a foreign key; stated in full under Relationships.
- Foreign key "SAMP_TARGETS_F_FKey#2" from SAMP_TARGETS_F (PROD_KEY) to SAMP_PRODUCTS_D (PROD_KEY): proposed as a foreign key; stated in full under Relationships.

#### Read by

Source "SAMP_TARGETS_F" of the model table Fact Target.

#### Missing

- Decimal places of BILL_MTH_KEY, OFFICE_KEY, PROD_KEY and UNITS (scale not recorded).
- The key (question Q12).
- Indexes, partitioning, volumes, and how and when the table is loaded (see decision Q6).
