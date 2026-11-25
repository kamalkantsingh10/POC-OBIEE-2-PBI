### New table SAMP_REVENUE_A

Proposal (not from OBIEE; to be signed): build SAMP_REVENUE_A as a table, like for like from the OBIEE database table "SAMP_REVENUE_A", if decision Q3 keeps the summary tables. The old table is in `xe.SAMPLESALES`, type `none`. [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A] It is not an alias of another physical table. Its object in the database for database type `DefaultMulDB`: `SAMP_REVENUE_A` [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A.def.DefaultMulDB]. Description in the repository: not recorded.

#### Columns

| # | New column | Proposed type | Length or precision | Decimal places | May be empty | From old column | Old field | Used by tab items | Evidence |
|---|---|---|---|---|---|---|---|---|---|
| 1 | BILL_MTH_KEY | number | precision 15 | missing | yes, as recorded | SAMP_REVENUE_A.BILL_MTH_KEY | "Fact Revenue"."Bill Mth Key" | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A.BILL_MTH_KEY] |
| 2 | DISCNT_VALUE | number | precision 7 | 2 | yes, as recorded | SAMP_REVENUE_A.DISCNT_VALUE | "Fact Revenue"."Discnt Value" | K1, K3, K4, V1-net-revenue, V2-net-revenue, V2-discount, V3-net-revenue | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A.DISCNT_VALUE] |
| 3 | PROD_KEY | number | precision 15 | missing | yes, as recorded | SAMP_REVENUE_A.PROD_KEY | "Fact Revenue"."Prod Key" | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A.PROD_KEY] |
| 4 | REVENUE | number | precision 8 | 2 | yes, as recorded | SAMP_REVENUE_A.REVENUE | "Fact Revenue"."Revenue" (also "All Products Revenue", "All Months Revenue", "Revenue (GBP)", "Revenue (EUR)", "Revenue (INR)") | K1, K3, K4, V1-net-revenue, V2-net-revenue, V3-net-revenue | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A.REVENUE] |
| 5 | UNITS | number | precision 15 | missing | yes, as recorded | SAMP_REVENUE_A.UNITS | "Fact Revenue"."Units" | K2, K4, V2-units | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A.UNITS] |

Every column records the external name `samplesales`.

#### Key

Missing: the export records no key for this table (question Q12).

#### Grain

The level of detail is stated in the section Model table Fact Revenue, source "Aggregate Revenue".

#### Links to other tables

- Foreign key "SAMP_REVENUE_A_FKey" from SAMP_REVENUE_A (BILL_MTH_KEY) to SAMP_TIME_MTH_D (MTH_KEY): proposed as a foreign key; stated in full under Relationships.
- Foreign key "SAMP_REVENUE_A_FKey#1" from SAMP_REVENUE_A (PROD_KEY) to SAMP_PRODUCTS_D (PROD_KEY): proposed as a foreign key; stated in full under Relationships.

#### Read by

Source "Aggregate Revenue" of the model table Fact Revenue.

#### Missing

- Decimal places of BILL_MTH_KEY, PROD_KEY and UNITS (scale not recorded).
- The key (question Q12).
- Indexes, partitioning, volumes, and how and when the table is loaded (asked in question Q3: who loads the summary tables).
