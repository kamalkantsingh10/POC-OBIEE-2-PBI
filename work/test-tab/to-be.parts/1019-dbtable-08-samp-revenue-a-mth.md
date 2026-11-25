### New table SAMP_REVENUE_A_MTH

Proposal (not from OBIEE; to be signed): build SAMP_REVENUE_A_MTH as a table, like for like from the OBIEE database table "SAMP_REVENUE_A_MTH", if decision Q3 keeps the summary tables. The old table is in `xe.SAMPLESALES`, type `none`. [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A_MTH] It is not an alias of another physical table. Its object in the database: no separate name is recorded, so the repository knows it only by the name above. Description in the repository: not recorded.

#### Columns

| # | New column | Proposed type | Length or precision | Decimal places | May be empty | From old column | Old field | Used by tab items | Evidence |
|---|---|---|---|---|---|---|---|---|---|
| 1 | BILL_MTH_KEY | number | precision 22 | missing | yes, as recorded | SAMP_REVENUE_A_MTH.BILL_MTH_KEY | "Fact Revenue"."BILL_MTH_KEY" | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A_MTH.BILL_MTH_KEY] |
| 2 | DISCNT_VALUE | number | precision 22 | missing | yes, as recorded | SAMP_REVENUE_A_MTH.DISCNT_VALUE | "Fact Revenue"."DISCNT_VALUE" (not the measure "Discnt Value") | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A_MTH.DISCNT_VALUE] |
| 3 | REVENUE | number | precision 22 | missing | yes, as recorded | SAMP_REVENUE_A_MTH.REVENUE | "Fact Revenue"."Revenue" | K1, K3, K4, V1-net-revenue, V2-net-revenue, V3-net-revenue | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A_MTH.REVENUE] |
| 4 | UNITS | number | precision 22 | missing | yes, as recorded | SAMP_REVENUE_A_MTH.UNITS | "Fact Revenue"."Units" | K2, K4, V2-units | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A_MTH.UNITS] |

Every column records the external name `samplesales`.

#### Key

Missing: the export records no key for this table (question Q12).

#### Grain

The level of detail is stated in the section Model table Fact Revenue, source "Aggregate Revenue By Month".

#### Links to other tables

- Foreign key "SAMP_REVENUE_A_MTH_Foreign Key" from SAMP_REVENUE_A_MTH (BILL_MTH_KEY) to SAMP_TIME_MTH_D (MTH_KEY): proposed as a foreign key; stated in full under Relationships.

#### Read by

Source "Aggregate Revenue By Month" of the model table Fact Revenue.

#### Missing

- Decimal places of all four columns (scale not recorded).
- The key (question Q12).
- Indexes, partitioning, volumes, and how and when the table is loaded.
