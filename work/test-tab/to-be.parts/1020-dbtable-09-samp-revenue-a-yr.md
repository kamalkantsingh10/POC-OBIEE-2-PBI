### New table SAMP_REVENUE_A_YR

Proposal (not from OBIEE; to be signed): build SAMP_REVENUE_A_YR as a table, like for like from the OBIEE database table "SAMP_REVENUE_A_YR", if decision Q3 keeps the summary tables. The old table is in `xe.SAMPLESALES`, type `none`. [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A_YR] It is not an alias of another physical table. Its object in the database for database type `DefaultMulDB`: `SAMP_REVENUE_A_YR` [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A_YR.def.DefaultMulDB]. Description in the repository: not recorded.

#### Columns

| # | New column | Proposed type | Length or precision | Decimal places | May be empty | From old column | Old field | Used by tab items | Evidence |
|---|---|---|---|---|---|---|---|---|---|
| 1 | BILL_YEAR_KEY | number | missing | missing | yes, as recorded | SAMP_REVENUE_A_YR.BILL_YEAR_KEY | "Fact Revenue"."BILL_YEAR_KEY" | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A_YR.BILL_YEAR_KEY] |
| 2 | DISCNT_VALUE | number | precision 22 | missing | yes, as recorded | SAMP_REVENUE_A_YR.DISCNT_VALUE | "Fact Revenue"."DISCNT_VALUE" (not the measure "Discnt Value") | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A_YR.DISCNT_VALUE] |
| 3 | REVENUE | number | precision 22 | missing | yes, as recorded | SAMP_REVENUE_A_YR.REVENUE | "Fact Revenue"."Revenue" | K1, K3, K4, V1-net-revenue, V2-net-revenue, V3-net-revenue | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A_YR.REVENUE] |
| 4 | UNITS | number | precision 22 | missing | yes, as recorded | SAMP_REVENUE_A_YR.UNITS | "Fact Revenue"."Units" | K2, K4, V2-units | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A_YR.UNITS] |

Columns 2 to 4 record the external name `samplesales`; BILL_YEAR_KEY records none.

#### Key

Missing: the export records no key for this table (question Q12).

#### Grain

The level of detail is stated in the section Model table Fact Revenue, source "Aggregate Revenue by Year".

#### Links to other tables

- Foreign key "SAMP_REVENUE_A_YR_Foreign Key" from SAMP_REVENUE_A_YR (BILL_YEAR_KEY) to SAMP_TIME_MTH_D (CAL_YEAR): proposed as a foreign key; stated in full under Relationships.

#### Read by

Source "Aggregate Revenue by Year" of the model table Fact Revenue.

#### Missing

- Precision of BILL_YEAR_KEY; decimal places of all four columns (scale not recorded).
- The key (question Q12).
- Indexes, partitioning, volumes, and how and when the table is loaded.
