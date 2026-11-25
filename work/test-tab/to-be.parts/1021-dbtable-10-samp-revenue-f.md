### New table SAMP_REVENUE_F

Proposal (not from OBIEE; to be signed): build SAMP_REVENUE_F as a table, like for like from the OBIEE database table "SAMP_REVENUE_F". The old table is in `xe.SAMPLESALES`, type `none`. [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F] It is not an alias of another physical table. Its object in the database for database type `DefaultMulDB`: `SAMP_REVENUE_F` [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F.def.DefaultMulDB]. Description in the repository: not recorded.

#### Columns

| # | New column | Proposed type | Length or precision | Decimal places | May be empty | From old column | Old field | Used by tab items | Evidence |
|---|---|---|---|---|---|---|---|---|---|
| 1 | BILL_DAY_DT | date | not applicable | not applicable | yes, as recorded | SAMP_REVENUE_F.BILL_DAY_DT | "Fact Revenue"."Bill Day Dt" | none (links to Dim Time) | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F.BILL_DAY_DT] |
| 2 | BILL_MTH_KEY | number | precision 15 | missing | yes, as recorded | SAMP_REVENUE_F.BILL_MTH_KEY | "Fact Revenue"."Bill Mth Key" | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F.BILL_MTH_KEY] |
| 3 | DISCNT_VALUE | number | precision 6 | 2 | yes, as recorded | SAMP_REVENUE_F.DISCNT_VALUE | "Fact Revenue"."Discnt Value" | K1, K3, K4, V1-net-revenue, V2-net-revenue, V2-discount, V3-net-revenue | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F.DISCNT_VALUE] |
| 4 | OFFICE_KEY | number | precision 15 | missing | yes, as recorded | SAMP_REVENUE_F.OFFICE_KEY | "Fact Revenue"."Office Key" | none (links to Dim Offices) | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F.OFFICE_KEY] |
| 5 | ORDER_DAY_DT | date and time | not applicable | not applicable | yes, as recorded | SAMP_REVENUE_F.ORDER_DAY_DT | "Fact Revenue"."Order Day Dt" | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F.ORDER_DAY_DT] |
| 6 | ORDER_NUMBER | number | precision 15 | missing | yes, as recorded | SAMP_REVENUE_F.ORDER_NUMBER | "Fact Revenue"."Order Number" | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F.ORDER_NUMBER] |
| 7 | ORDER_STATUS | text | length 11 | not applicable | yes, as recorded | SAMP_REVENUE_F.ORDER_STATUS | "Fact Revenue"."Order Status" | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F.ORDER_STATUS] |
| 8 | ORDER_TYPE | text | length 8 | not applicable | yes, as recorded | SAMP_REVENUE_F.ORDER_TYPE | "Fact Revenue"."Order Type" | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F.ORDER_TYPE] |
| 9 | PROD_KEY | number | precision 15 | missing | yes, as recorded | SAMP_REVENUE_F.PROD_KEY | "Fact Revenue"."Prod Key" | none (links to Dim Products) | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F.PROD_KEY] |
| 10 | REVENUE | number | precision 7 | 2 | yes, as recorded | SAMP_REVENUE_F.REVENUE | "Fact Revenue"."Revenue" (also "All Products Revenue", "All Months Revenue", "Revenue (GBP)", "Revenue (EUR)", "Revenue (INR)") | K1, K3, K4, V1-net-revenue, V2-net-revenue, V3-net-revenue | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F.REVENUE] |
| 11 | UNITS | number | precision 15 | missing | yes, as recorded | SAMP_REVENUE_F.UNITS | "Fact Revenue"."Units" | K2, K4, V2-units | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F.UNITS] |
| 12 | EMPLOYEE_KEY | whole number | missing | not applicable | missing (not stated in the export) | SAMP_REVENUE_F.EMPLOYEE_KEY | "Fact Revenue"."Sales Employee Key" | none (links to Dim Sales Rep) | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F.EMPLOYEE_KEY] |

Columns 2 to 11 record the external name `samplesales`; BILL_DAY_DT and EMPLOYEE_KEY record none.

#### Key

Missing: the export records no key for this table (question Q12).

#### Grain

The level of detail is stated in the section Model table Fact Revenue, source "SAMP_REVENUE_F".

#### Links to other tables

- Foreign key "SAMP_REVENUE_F_FKey" from SAMP_REVENUE_F (BILL_MTH_KEY) to SAMP_TIME_MTH_D (MTH_KEY): proposed as a foreign key; stated in full under Relationships.
- Foreign key "SAMP_REVENUE_F_FKey#1" from SAMP_REVENUE_F (OFFICE_KEY) to SAMP_OFFICES_D (OFFICE_KEY): proposed as a foreign key; stated in full under Relationships.
- Foreign key "SAMP_REVENUE_F_FKey#2" from SAMP_REVENUE_F (PROD_KEY) to SAMP_PRODUCTS_D (PROD_KEY): proposed as a foreign key; stated in full under Relationships.
- Foreign key "SAMP_REVENUE_F_FKey#3" from SAMP_REVENUE_F (BILL_DAY_DT) to SAMP_TIME_DAY_D (CALENDAR_DATE): proposed as a foreign key; stated in full under Relationships.
- Foreign key "SAMP_REVENUE_F_FKey#4" from SAMP_REVENUE_F (EMPLOYEE_KEY) to EMP_PARENT_CHILD (MEMBER_KEY): proposed as a foreign key; stated in full under Relationships.

#### Read by

Source "SAMP_REVENUE_F" of the model table Fact Revenue.

#### Missing

- Decimal places of BILL_MTH_KEY, OFFICE_KEY, ORDER_NUMBER, PROD_KEY and UNITS (scale not recorded); precision of EMPLOYEE_KEY.
- Whether EMPLOYEE_KEY must be filled (nullable not stated).
- The key (question Q12).
- Indexes, partitioning, volumes, and how and when the table is loaded.
