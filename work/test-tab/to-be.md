Status: reviewed 2026-11-24 (11 findings corrected, 1 turned into questions, 19 questions open)

# test-tab — to-be design

**What this is.** A like-for-like design for the tab "test-tab": the new tables and views, the Power BI semantic model, and what supplies each field of the FSD. It is built from the columns the Operator confirmed. Everything marked "Proposal" is a suggestion to be signed by a person; nothing here has been agreed.

**How to read it.** A statement that ends with a marker in square brackets is a fact from the OBIEE export, and the marker names the row it came from. Text shown as code is copied from OBIEE or from the FSD exactly. A marker that starts with "answer" means the statement rests on an answer a person gave. "Missing" means the export does not hold it. Types are platform-neutral, because no target platform has been chosen. Today's model is described in the as-is document; this document points to it and does not repeat it.

Note for this run: the decisions this design is built from are test decisions written for testing, not the Operator's; every decision note says so (section Fields on the tab) [kb:item::F1] [kb:item::N1-currency].

## The tab in one page

**FSD items.** 21 items need data: 18 are confirmed and supplied below; 3 are unresolved: S1-own-offices (managers see only their own offices; rejected), F4 (Region; unmatched) and K5 (Active customers; unmatched).

**New tables and views.** 12 new tables and 1 new view, all like for like.

| New table or view | Columns | Key or "missing" | Read by |
|---|---|---|---|
| EMPLOYEE | 4 | EMP_ID | Dim Sales Rep |
| EMP_PARENT_CHILD | 4 | MEMBER_KEY (uniqueness asked, Q10) | Dim Sales Rep |
| FactCrossJoin (view) | 1 | missing | Fact Revenue (not used by the tab; Q11) |
| SAMP_OFFICES_D | 10 | OFFICE_KEY | Dim Offices |
| SAMP_PRODUCTS_D | 11 | PROD_KEY | Dim Products |
| SAMP_PRODUCTS_D_TRANSLATION | 3 | LANG_ID, PROD_KEY | Dim Products (Translated), not in this model |
| SAMP_REVENUE_A | 5 | missing | Fact Revenue (if Q3 keeps summaries) |
| SAMP_REVENUE_A_MTH | 4 | missing | Fact Revenue (if Q3 keeps summaries) |
| SAMP_REVENUE_A_YR | 4 | missing | Fact Revenue (if Q3 keeps summaries) |
| SAMP_REVENUE_F | 12 | missing | Fact Revenue |
| SAMP_TARGETS_F | 5 | missing | Fact Target |
| SAMP_TIME_DAY_D | 14 | CALENDAR_DATE | Dim Time |
| SAMP_TIME_MTH_D | 36 | MTH_KEY (a second key on CAL_YEAR; Q13) | Dim Time (if Q8 keeps it) |

**Model.** Six model tables: Fact Revenue, Fact Target, Dim Offices, Dim Products, Dim Sales Rep, Dim Time (Dim Products (Translated) is not carried). Four model measures: Revenue, Discount Value, Units (Fact Revenue) and Revenue Target (Fact Target), each with a proposed definition. Three FSD measures: Net revenue, Revenue vs target, Average selling price. The field Preferred Currency has no definition until decision Q2. Six relationships and three hierarchies are proposed; no relationship from Dim Time to Fact Target until Q14, no hierarchy for sales representatives until Q17, no security role until Q18.

**Decisions needed.** 15. The three that matter most:

- Q3: which of the four revenue tables at different levels the model keeps; it decides what the measures read, and the month and year summaries do not hold the discount as a measure.
- Q14: how targets, held by month, meet the time table; without it Revenue vs target cannot compare the same period.
- Q2: how the currency switch "Preferred Currency" (item N1-currency) works without OBIEE's per-user session value.

**Missing most often.** Decimal places of number columns (scale not recorded) and whether key columns must be filled (nullable not stated); the keys of all five revenue and target tables (Q12); indexes, partitioning, volumes and loading for every table.

**Open questions.** 19, of which 15 are decisions.

## What this design covers

This design covers the tab "test-tab" of the estate sampleapp. The slice holds what the columns the Operator confirmed for the tab's FSD items need. Decisions: 27 rows (26 confirmed, 1 rejected); items: 18 confirmed, 1 rejected, 2 unmatched, 0 undecided.

The confirmed columns are in one subject area: "Sample Sales", built on business model "SAMPLESALES". [kb:sampleapp::pres::Sample Sales]

### Tables report authors see, with the confirmed columns

| Table | Confirmed fields | Evidence |
|---|---|---|
| Dim Offices | "Office Dsc" | [kb:sampleapp::pres::Sample Sales.Dim Offices] |
| Dim Products | "Brand", "Prod Dsc" | [kb:sampleapp::pres::Sample Sales.Dim Products] |
| Dim Sales Rep | "Employee Name" | [kb:sampleapp::pres::Sample Sales.Dim Sales Rep] |
| Dim Time | "Per Name Month", "Per Name Year" | [kb:sampleapp::pres::Sample Sales.Dim Time] |
| Fact Revenue | "Discnt Value", "Preferred Currency", "Revenue", "Units" | [kb:sampleapp::pres::Sample Sales.Fact Revenue] |
| Fact Target | "Revenue" | [kb:sampleapp::pres::Sample Sales.Fact Target] |

### What the slice holds

- Confirmed columns: 11; FSD items: 21.
- Logical tables described: 7 (each whole); logical columns: 63, of which measures: 11.
- Logical table sources: 12; physical tables: 13 (each whole); their columns: 113; their keys: 8.
- Business-model joins: 7; physical joins: 17; dimensions: 4; hierarchies offered to report authors: 4.
- Variables: 1; security rows: 5; reports: 0.

### Where it came from

The estate sampleapp, of which the rpd half was extracted. The Knowledge Base was extracted at 2026-11-24T02:56:24Z by extractor version 0.1.0.

The as-is document used is that of "Sample Sales", as-is/sampleapp/sample-sales.md; its status line reads "Status: reviewed 2026-11-23 (25 findings corrected, 1 turned into questions, 14 questions open)". Its open questions are referred to as "as-is document, question Q<n>".

## New tables and views

One section per database table the tab's model reads. Each is a proposal to be signed by Data Engineering. It is like for like: the old table, column by column. Types are platform-neutral, because no target platform has been chosen. What the export does not hold is marked "missing" and is not guessed.

### New table EMPLOYEE

Proposal (not from OBIEE; to be signed): build EMPLOYEE as a table, like for like from the OBIEE database table "EMPLOYEE". The old table is in `xe.SAMPLESALES`, type `none`. [kb:sampleapp::phys::xe.SAMPLESALES.EMPLOYEE] It is not an alias of another physical table. Its object in the database: no separate name is recorded, so the repository knows it only by the name above. Description in the repository: not recorded.

#### Columns

| # | New column | Proposed type | Length or precision | Decimal places | May be empty | From old column | Old field | Used by tab items | Evidence |
|---|---|---|---|---|---|---|---|---|---|
| 1 | EMP_ID | number | precision 15 | missing | missing (not stated in the export) | EMPLOYEE.EMP_ID | "Dim Sales Rep"."Employee ID" | none (key of Dim Sales Rep) | [kb:sampleapp::phys::xe.SAMPLESALES.EMPLOYEE.EMP_ID] |
| 2 | EMP_NAME | text | length 40 | not applicable | yes, as recorded | EMPLOYEE.EMP_NAME | "Dim Sales Rep"."Employee Name" | V3-rep | [kb:sampleapp::phys::xe.SAMPLESALES.EMPLOYEE.EMP_NAME] |
| 3 | MANAGER_ID | number | precision 15 | missing | yes, as recorded | EMPLOYEE.MANAGER_ID | "Dim Sales Rep"."Manager ID" | none | [kb:sampleapp::phys::xe.SAMPLESALES.EMPLOYEE.MANAGER_ID] |
| 4 | OFFICE_ID | number | precision 15 | missing | yes, as recorded | EMPLOYEE.OFFICE_ID | "Dim Sales Rep"."Office ID" | none | [kb:sampleapp::phys::xe.SAMPLESALES.EMPLOYEE.OFFICE_ID] |

Every column records the external name `samplesales`.

#### Key

Key "EMP_ID", made of column(s) "EMP_ID", the only key the repository declares for the table. [kb:sampleapp::phys::xe.SAMPLESALES.EMPLOYEE.key.EMP_ID] Proposal (not from OBIEE; to be signed): EMP_ID is the key of the new table EMPLOYEE.

#### Grain

The level of detail is stated in the section Model table Dim Sales Rep.

#### Links to other tables

- Foreign key "EMP_PARENT_CHILD_FKey" from EMP_PARENT_CHILD (ANCESTOR_KEY) to EMPLOYEE (EMP_ID): proposed as a foreign key; stated in full under Relationships.
- Complex join "Complex Join#2" between FactCrossJoin and EMPLOYEE: stated in full under Relationships.

#### Read by

Source "LTS Employee" of the model table Dim Sales Rep.

#### Missing

- Decimal places of EMP_ID, MANAGER_ID and OFFICE_ID (scale not recorded).
- Whether EMP_ID must be filled (nullable not stated).
- Indexes, partitioning, volumes, and how and when the table is loaded.

### New table EMP_PARENT_CHILD

Proposal (not from OBIEE; to be signed): build EMP_PARENT_CHILD as a table, like for like from the OBIEE database table "EMP_PARENT_CHILD". The old table is in `xe.SAMPLESALES`, type `none`. [kb:sampleapp::phys::xe.SAMPLESALES.EMP_PARENT_CHILD] It is not an alias of another physical table. Its object in the database for database type `DefaultMulDB`: `EMP_PARENT_CHILD` [kb:sampleapp::phys::xe.SAMPLESALES.EMP_PARENT_CHILD.def.DefaultMulDB]. Description in the repository: not recorded.

The repository records it as the relationship table of a parent-child hierarchy, one row per member and ancestor: member column MEMBER_KEY, parent (ancestor) column ANCESTOR_KEY, distance column DISTANCE, leaf-flag column IS_LEAF. [kb:sampleapp::phys::xe.SAMPLESALES.EMP_PARENT_CHILD] Whether the new model needs it depends on decision Q7 and on the decision on the drill path under Hierarchies.

#### Columns

| # | New column | Proposed type | Length or precision | Decimal places | May be empty | From old column | Old field | Used by tab items | Evidence |
|---|---|---|---|---|---|---|---|---|---|
| 1 | MEMBER_KEY | number | precision 15 | missing | missing (not stated in the export) | EMP_PARENT_CHILD.MEMBER_KEY | not mapped | none | [kb:sampleapp::phys::xe.SAMPLESALES.EMP_PARENT_CHILD.MEMBER_KEY] |
| 2 | ANCESTOR_KEY | number | precision 15 | missing | missing (not stated in the export) | EMP_PARENT_CHILD.ANCESTOR_KEY | not mapped | none | [kb:sampleapp::phys::xe.SAMPLESALES.EMP_PARENT_CHILD.ANCESTOR_KEY] |
| 3 | DISTANCE | whole number | missing | not applicable | missing (not stated in the export) | EMP_PARENT_CHILD.DISTANCE | "Dim Sales Rep"."Distance" | none | [kb:sampleapp::phys::xe.SAMPLESALES.EMP_PARENT_CHILD.DISTANCE] |
| 4 | IS_LEAF | whole number | missing | not applicable | missing (not stated in the export) | EMP_PARENT_CHILD.IS_LEAF | not mapped | none | [kb:sampleapp::phys::xe.SAMPLESALES.EMP_PARENT_CHILD.IS_LEAF] |

No column records an external name.

#### Key

Key "EMP_PARENT_CHILD_Key", made of column(s) "MEMBER_KEY", the only key the repository declares for the table. [kb:sampleapp::phys::xe.SAMPLESALES.EMP_PARENT_CHILD.key.EMP_PARENT_CHILD_Key] Proposal (not from OBIEE; to be signed): MEMBER_KEY is the key of the new table EMP_PARENT_CHILD, as recorded. A table of one row per member and ancestor may hold a member on several rows, and the revenue facts point at MEMBER_KEY: question Q10.

#### Grain

The level of detail is stated in the section Model table Dim Sales Rep.

#### Links to other tables

- Foreign key "EMP_PARENT_CHILD_FKey" from EMP_PARENT_CHILD (ANCESTOR_KEY) to EMPLOYEE (EMP_ID): proposed as a foreign key; stated in full under Relationships.
- Foreign key "SAMP_REVENUE_F_FKey#4" from SAMP_REVENUE_F (EMPLOYEE_KEY) to EMP_PARENT_CHILD (MEMBER_KEY): proposed as a foreign key; stated in full under Relationships.

#### Read by

Source "LTS Employee" of the model table Dim Sales Rep.

#### Missing

- Decimal places of MEMBER_KEY and ANCESTOR_KEY (scale not recorded); precision of DISTANCE and IS_LEAF.
- Whether any column must be filled (nullable not stated for all four).
- Whether MEMBER_KEY identifies one row (question Q10).
- Indexes, partitioning, volumes, and how and when the table is loaded.

### New view FactCrossJoin

Proposal (not from OBIEE; to be signed): if it is kept (decision Q11), build FactCrossJoin as a view, like for like from the OBIEE table "FactCrossJoin". The old table is in `xe.SAMPLESALES`, type `select`: not a database table, but a query kept in the repository. [kb:sampleapp::phys::xe.SAMPLESALES.FactCrossJoin] It is not an alias of another physical table. Description in the repository: not recorded. Its SQL for database type `DefaultMulDB`, exactly:

```
select 1 Preserve from dual
```

[kb:sampleapp::phys::xe.SAMPLESALES.FactCrossJoin.def.DefaultMulDB]

No item of the tab uses it: its only column feeds the measure "Preserve" of "Fact Revenue", which is not confirmed for the tab.

#### Columns

| # | New column | Proposed type | Length or precision | Decimal places | May be empty | From old column | Old field | Used by tab items | Evidence |
|---|---|---|---|---|---|---|---|---|---|
| 1 | Preserve | number | missing | missing | missing (not stated in the export) | FactCrossJoin.Preserve | "Fact Revenue"."Preserve" | none | [kb:sampleapp::phys::xe.SAMPLESALES.FactCrossJoin.Preserve] |

No column records an external name.

#### Key

Missing: the export records no key for this table. The tab does not read it, so no question is asked about its key.

#### Grain

The level of detail is stated in the section Model table Fact Revenue, which records no level of detail for its source "FactCrossJoin".

#### Links to other tables

- Complex join "Complex Join" between FactCrossJoin and SAMP_OFFICES_D: stated in full under Relationships.
- Complex join "Complex Join#1" between FactCrossJoin and SAMP_PRODUCTS_D: stated in full under Relationships.
- Complex join "Complex Join#2" between FactCrossJoin and EMPLOYEE: stated in full under Relationships.

#### Read by

Source "FactCrossJoin" of the model table Fact Revenue.

#### Missing

- Precision and decimal places of Preserve; whether it must be filled (nullable not stated).
- The key.
- Indexes, partitioning, volumes, and how and when it is refreshed.

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

### New table SAMP_PRODUCTS_D

Proposal (not from OBIEE; to be signed): build SAMP_PRODUCTS_D as a table, like for like from the OBIEE database table "SAMP_PRODUCTS_D". The old table is in `xe.SAMPLESALES`, type `none`. [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_PRODUCTS_D] It is not an alias of another physical table. Its object in the database: no separate name is recorded, so the repository knows it only by the name above. Description in the repository: not recorded.

#### Columns

| # | New column | Proposed type | Length or precision | Decimal places | May be empty | From old column | Old field | Used by tab items | Evidence |
|---|---|---|---|---|---|---|---|---|---|
| 1 | ATTRIBUTE_1 | text | length 7 | not applicable | yes, as recorded | SAMP_PRODUCTS_D.ATTRIBUTE_1 | "Dim Products"."Attribute 1" | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_PRODUCTS_D.ATTRIBUTE_1] |
| 2 | ATTRIBUTE_2 | text | length 6 | not applicable | yes, as recorded | SAMP_PRODUCTS_D.ATTRIBUTE_2 | "Dim Products"."Attribute 2" | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_PRODUCTS_D.ATTRIBUTE_2] |
| 3 | BRAND | text | length 8 | not applicable | yes, as recorded | SAMP_PRODUCTS_D.BRAND | "Dim Products"."Brand" | F3 | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_PRODUCTS_D.BRAND] |
| 4 | BRAND_KEY | number | precision 15 | missing | missing (not stated in the export) | SAMP_PRODUCTS_D.BRAND_KEY | "Dim Products"."Brand Key" | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_PRODUCTS_D.BRAND_KEY] |
| 5 | LOB | text | length 13 | not applicable | yes, as recorded | SAMP_PRODUCTS_D.LOB | "Dim Products"."Lob" | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_PRODUCTS_D.LOB] |
| 6 | LOB_KEY | number | precision 15 | missing | missing (not stated in the export) | SAMP_PRODUCTS_D.LOB_KEY | "Dim Products"."Lob Key" | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_PRODUCTS_D.LOB_KEY] |
| 7 | PROD_DSC | text | length 26 | not applicable | yes, as recorded | SAMP_PRODUCTS_D.PROD_DSC | "Dim Products"."Prod Dsc" | V2-product | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_PRODUCTS_D.PROD_DSC] |
| 8 | PROD_KEY | number | precision 15 | missing | missing (not stated in the export) | SAMP_PRODUCTS_D.PROD_KEY | "Dim Products"."Prod Key" | none (key of Dim Products) | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_PRODUCTS_D.PROD_KEY] |
| 9 | SEQUENCE | number | precision 15 | missing | missing (not stated in the export) | SAMP_PRODUCTS_D.SEQUENCE | "Dim Products"."Sequence" | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_PRODUCTS_D.SEQUENCE] |
| 10 | TYPE | text | length 12 | not applicable | yes, as recorded | SAMP_PRODUCTS_D.TYPE | "Dim Products"."Type" | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_PRODUCTS_D.TYPE] |
| 11 | TYPE_KEY | number | precision 15 | missing | missing (not stated in the export) | SAMP_PRODUCTS_D.TYPE_KEY | "Dim Products"."Type Key" | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_PRODUCTS_D.TYPE_KEY] |

Every column records the external name `samplesales`.

#### Key

Key "SAMP_PRODUCTS_D_Key", made of column(s) "PROD_KEY", the only key the repository declares for the table. [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_PRODUCTS_D.key.SAMP_PRODUCTS_D_Key] Proposal (not from OBIEE; to be signed): PROD_KEY is the key of the new table SAMP_PRODUCTS_D.

#### Grain

The level of detail is stated in the section Model table Dim Products.

#### Links to other tables

- Foreign key "SAMP_REVENUE_F_FKey#2" from SAMP_REVENUE_F (PROD_KEY) to SAMP_PRODUCTS_D (PROD_KEY): proposed as a foreign key; stated in full under Relationships.
- Foreign key "SAMP_REVENUE_A_FKey#1" from SAMP_REVENUE_A (PROD_KEY) to SAMP_PRODUCTS_D (PROD_KEY): proposed as a foreign key; stated in full under Relationships.
- Foreign key "SAMP_TARGETS_F_FKey#2" from SAMP_TARGETS_F (PROD_KEY) to SAMP_PRODUCTS_D (PROD_KEY): proposed as a foreign key; stated in full under Relationships.
- Foreign key "SAMP_PRODUCTS_D_TRANSLATION_FKey" from SAMP_PRODUCTS_D_TRANSLATION (PROD_KEY) to SAMP_PRODUCTS_D (PROD_KEY): proposed as a foreign key; stated in full under Relationships.
- Complex join "Complex Join#1" between FactCrossJoin and SAMP_PRODUCTS_D: stated in full under Relationships.

#### Read by

Source "SAMP_PRODUCTS_D" of the model table Dim Products.

#### Missing

- Decimal places of BRAND_KEY, LOB_KEY, PROD_KEY, SEQUENCE and TYPE_KEY (scale not recorded).
- Whether BRAND_KEY, LOB_KEY, PROD_KEY, SEQUENCE and TYPE_KEY must be filled (nullable not stated).
- Indexes, partitioning, volumes, and how and when the table is loaded.

### New table SAMP_PRODUCTS_D_TRANSLATION

Proposal (not from OBIEE; to be signed): build SAMP_PRODUCTS_D_TRANSLATION as a table, like for like from the OBIEE database table "SAMP_PRODUCTS_D_TRANSLATION", only if a later tab needs translated product names. The tab's model does not read it (section Model table Dim Products (Translated)). The old table is in `xe.SAMPLESALES`, type `none`. [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_PRODUCTS_D_TRANSLATION] It is not an alias of another physical table. Its object in the database for database type `DefaultMulDB`: `SAMP_PRODUCTS_D_TRANSLATION` [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_PRODUCTS_D_TRANSLATION.def.DefaultMulDB]. Description in the repository: not recorded.

#### Columns

| # | New column | Proposed type | Length or precision | Decimal places | May be empty | From old column | Old field | Used by tab items | Evidence |
|---|---|---|---|---|---|---|---|---|---|
| 1 | LANG_ID | text | length 2 | not applicable | missing (not stated in the export) | SAMP_PRODUCTS_D_TRANSLATION.LANG_ID | "Dim Products (Translated)"."LANG_ID" | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_PRODUCTS_D_TRANSLATION.LANG_ID] |
| 2 | PROD_DESC | text | length 50 | not applicable | missing (not stated in the export) | SAMP_PRODUCTS_D_TRANSLATION.PROD_DESC | "Dim Products (Translated)"."PROD_DESC" | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_PRODUCTS_D_TRANSLATION.PROD_DESC] |
| 3 | PROD_KEY | number | precision 15 | missing | missing (not stated in the export) | SAMP_PRODUCTS_D_TRANSLATION.PROD_KEY | "Dim Products (Translated)"."PROD_KEY" | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_PRODUCTS_D_TRANSLATION.PROD_KEY] |

Every column records the external name `samplesales`.

#### Key

Key "PK_PROD_TRANS1", made of column(s) "LANG_ID", "PROD_KEY", the only key the repository declares for the table. [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_PRODUCTS_D_TRANSLATION.key.PK_PROD_TRANS1] Proposal (not from OBIEE; to be signed): LANG_ID and PROD_KEY together are the key of the new table.

#### Grain

The level of detail is stated in the section Model table Dim Products (Translated), whose source records none.

#### Links to other tables

- Foreign key "SAMP_PRODUCTS_D_TRANSLATION_FKey" from SAMP_PRODUCTS_D_TRANSLATION (PROD_KEY) to SAMP_PRODUCTS_D (PROD_KEY): proposed as a foreign key; stated in full under Relationships.

#### Read by

Source "SAMP_PRODUCTS_D_TRANSLATION" of the business table "Dim Products (Translated)", which this design does not carry into the model.

#### Missing

- Decimal places of PROD_KEY (scale not recorded).
- Whether any column must be filled (nullable not stated for all three).
- Indexes, partitioning, volumes, and how and when the table is loaded.

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

### New table SAMP_TIME_DAY_D

Proposal (not from OBIEE; to be signed): build SAMP_TIME_DAY_D as a table, like for like from the OBIEE database table "SAMP_TIME_DAY_D". The old table is in `xe.SAMPLESALES`, type `none`. [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_DAY_D] It is not an alias of another physical table. Its object in the database for database type `DefaultMulDB`: `SAMP_TIME_DAY_D` [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_DAY_D.def.DefaultMulDB]. Description in the repository: not recorded.

#### Columns

| # | New column | Proposed type | Length or precision | Decimal places | May be empty | From old column | Old field | Used by tab items | Evidence |
|---|---|---|---|---|---|---|---|---|---|
| 1 | CALENDAR_DATE | date | not applicable | not applicable | missing (not stated in the export) | SAMP_TIME_DAY_D.CALENDAR_DATE | "Dim Time"."Calendar Date" | none (key of Dim Time) | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_DAY_D.CALENDAR_DATE] |
| 2 | DAY_KEY | date | not applicable | not applicable | missing (not stated in the export) | SAMP_TIME_DAY_D.DAY_KEY | not mapped | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_DAY_D.DAY_KEY] |
| 3 | DAY_OF_MONTH | number | precision 15 | missing | missing (not stated in the export) | SAMP_TIME_DAY_D.DAY_OF_MONTH | not mapped | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_DAY_D.DAY_OF_MONTH] |
| 4 | DAY_OF_WEEK | number | precision 15 | missing | missing (not stated in the export) | SAMP_TIME_DAY_D.DAY_OF_WEEK | not mapped | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_DAY_D.DAY_OF_WEEK] |
| 5 | DAY_OF_YEAR | number | precision 15 | missing | missing (not stated in the export) | SAMP_TIME_DAY_D.DAY_OF_YEAR | not mapped | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_DAY_D.DAY_OF_YEAR] |
| 6 | JULIAN_DAY_NUM | number | precision 15 | missing | missing (not stated in the export) | SAMP_TIME_DAY_D.JULIAN_DAY_NUM | not mapped | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_DAY_D.JULIAN_DAY_NUM] |
| 7 | JULIAN_MONTH_NUM | number | precision 15 | missing | missing (not stated in the export) | SAMP_TIME_DAY_D.JULIAN_MONTH_NUM | not mapped | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_DAY_D.JULIAN_MONTH_NUM] |
| 8 | JULIAN_QTR_NUM | number | precision 15 | missing | missing (not stated in the export) | SAMP_TIME_DAY_D.JULIAN_QTR_NUM | not mapped | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_DAY_D.JULIAN_QTR_NUM] |
| 9 | JULIAN_WEEK_NUM | number | precision 15 | missing | missing (not stated in the export) | SAMP_TIME_DAY_D.JULIAN_WEEK_NUM | not mapped | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_DAY_D.JULIAN_WEEK_NUM] |
| 10 | PER_NAME_HALF | text | length 8 | not applicable | missing (not stated in the export) | SAMP_TIME_DAY_D.PER_NAME_HALF | "Dim Time"."Per Name Half" | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_DAY_D.PER_NAME_HALF] |
| 11 | PER_NAME_MONTH | text | length 9 | not applicable | missing (not stated in the export) | SAMP_TIME_DAY_D.PER_NAME_MONTH | "Dim Time"."Per Name Month" | V1-month | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_DAY_D.PER_NAME_MONTH] |
| 12 | PER_NAME_QTR | text | length 7 | not applicable | missing (not stated in the export) | SAMP_TIME_DAY_D.PER_NAME_QTR | "Dim Time"."Per Name Qtr" | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_DAY_D.PER_NAME_QTR] |
| 13 | PER_NAME_WEEK | text | length 12 | not applicable | missing (not stated in the export) | SAMP_TIME_DAY_D.PER_NAME_WEEK | "Dim Time"."Per Name Week" | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_DAY_D.PER_NAME_WEEK] |
| 14 | PER_NAME_YEAR | text | length 4 | not applicable | missing (not stated in the export) | SAMP_TIME_DAY_D.PER_NAME_YEAR | "Dim Time"."Per Name Year" | F1 | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_DAY_D.PER_NAME_YEAR] |

Columns 3 to 14 record the external name `samplesales`; CALENDAR_DATE and DAY_KEY record none.

#### Key

Key "SAMP_TIME_DAY_D_Key", made of column(s) "CALENDAR_DATE", the only key the repository declares for the table. [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_DAY_D.key.SAMP_TIME_DAY_D_Key] Proposal (not from OBIEE; to be signed): CALENDAR_DATE is the key of the new table SAMP_TIME_DAY_D.

#### Grain

The level of detail is stated in the section Model table Dim Time, source "LTS Time Day".

#### Links to other tables

- Foreign key "SAMP_REVENUE_F_FKey#3" from SAMP_REVENUE_F (BILL_DAY_DT) to SAMP_TIME_DAY_D (CALENDAR_DATE): proposed as a foreign key; stated in full under Relationships.

#### Read by

Source "LTS Time Day" of the model table Dim Time.

#### Missing

- Decimal places of DAY_OF_MONTH, DAY_OF_WEEK, DAY_OF_YEAR and the four JULIAN columns (scale not recorded).
- Whether any column must be filled (nullable not stated for all fourteen).
- Whether the table holds every day of the period the facts cover (needed if time calculations are added later).
- Indexes, partitioning, volumes, and how and when the table is loaded.

### New table SAMP_TIME_MTH_D

Proposal (not from OBIEE; to be signed): build SAMP_TIME_MTH_D as a table, like for like from the OBIEE database table "SAMP_TIME_MTH_D", if decision Q8 keeps the month table or the facts held by month need it. The old table is in `xe.SAMPLESALES`, type `none`. [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D] It is not an alias of another physical table. Its object in the database: no separate name is recorded, so the repository knows it only by the name above. Description in the repository: not recorded.

#### Columns

All 36 columns are listed. Unless the row says otherwise, a column is a number of precision 15 with decimal places missing (scale not recorded), may be empty (yes, as recorded), records the external name `samplesales`, and is not mapped by any field.

| # | New column | Proposed type | Length or precision | Decimal places | May be empty | From old column | Old field | Used by tab items | Evidence |
|---|---|---|---|---|---|---|---|---|---|
| 1 | BEG_OF_QTR_WID | number | precision 15 | missing | yes, as recorded | SAMP_TIME_MTH_D.BEG_OF_QTR_WID | not mapped | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.BEG_OF_QTR_WID] |
| 2 | CAL_HALF | number | precision 15 | missing | yes, as recorded | SAMP_TIME_MTH_D.CAL_HALF | not mapped | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.CAL_HALF] |
| 3 | CAL_HALF_YEAR_END_DATE | number | precision 15 | missing | yes, as recorded | SAMP_TIME_MTH_D.CAL_HALF_YEAR_END_DATE | not mapped | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.CAL_HALF_YEAR_END_DATE] |
| 4 | CAL_MONTH | number | precision 15 | missing | yes, as recorded | SAMP_TIME_MTH_D.CAL_MONTH | not mapped | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.CAL_MONTH] |
| 5 | CAL_MONTH_END_DATE | number | precision 15 | missing | yes, as recorded | SAMP_TIME_MTH_D.CAL_MONTH_END_DATE | not mapped | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.CAL_MONTH_END_DATE] |
| 6 | CAL_QTR | number | precision 15 | missing | yes, as recorded | SAMP_TIME_MTH_D.CAL_QTR | not mapped | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.CAL_QTR] |
| 7 | CAL_QTR_END_DATE | number | precision 15 | missing | yes, as recorded | SAMP_TIME_MTH_D.CAL_QTR_END_DATE | not mapped | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.CAL_QTR_END_DATE] |
| 8 | CAL_YEAR | number | precision 15 | missing | yes, as recorded | SAMP_TIME_MTH_D.CAL_YEAR | not mapped | none (second key) | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.CAL_YEAR] |
| 9 | CAL_YEAR_END_DATE | number | precision 15 | missing | yes, as recorded | SAMP_TIME_MTH_D.CAL_YEAR_END_DATE | not mapped | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.CAL_YEAR_END_DATE] |
| 10 | DAYS_IN_FSCL_HALF_YEAR | number | precision 15 | missing | yes, as recorded | SAMP_TIME_MTH_D.DAYS_IN_FSCL_HALF_YEAR | not mapped | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.DAYS_IN_FSCL_HALF_YEAR] |
| 11 | DAYS_IN_FSCL_MONTH | number | precision 15 | missing | yes, as recorded | SAMP_TIME_MTH_D.DAYS_IN_FSCL_MONTH | not mapped | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.DAYS_IN_FSCL_MONTH] |
| 12 | DAYS_IN_FSCL_QTR | number | precision 15 | missing | yes, as recorded | SAMP_TIME_MTH_D.DAYS_IN_FSCL_QTR | not mapped | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.DAYS_IN_FSCL_QTR] |
| 13 | DAYS_IN_FSCL_YEAR | number | precision 15 | missing | yes, as recorded | SAMP_TIME_MTH_D.DAYS_IN_FSCL_YEAR | not mapped | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.DAYS_IN_FSCL_YEAR] |
| 14 | DAYS_IN_HALF_YEAR | number | precision 15 | missing | yes, as recorded | SAMP_TIME_MTH_D.DAYS_IN_HALF_YEAR | not mapped | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.DAYS_IN_HALF_YEAR] |
| 15 | DAYS_IN_MTH | number | precision 15 | missing | yes, as recorded | SAMP_TIME_MTH_D.DAYS_IN_MTH | not mapped | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.DAYS_IN_MTH] |
| 16 | DAYS_IN_QTR | number | precision 15 | missing | yes, as recorded | SAMP_TIME_MTH_D.DAYS_IN_QTR | not mapped | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.DAYS_IN_QTR] |
| 17 | DAYS_IN_YEAR | number | precision 15 | missing | yes, as recorded | SAMP_TIME_MTH_D.DAYS_IN_YEAR | not mapped | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.DAYS_IN_YEAR] |
| 18 | FSCL_HALF | number | precision 15 | missing | yes, as recorded | SAMP_TIME_MTH_D.FSCL_HALF | not mapped | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.FSCL_HALF] |
| 19 | FSCL_HALF_YEAR_END_DATE | number | precision 15 | missing | yes, as recorded | SAMP_TIME_MTH_D.FSCL_HALF_YEAR_END_DATE | not mapped | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.FSCL_HALF_YEAR_END_DATE] |
| 20 | FSCL_MONTH | number | precision 15 | missing | yes, as recorded | SAMP_TIME_MTH_D.FSCL_MONTH | not mapped | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.FSCL_MONTH] |
| 21 | FSCL_MONTH_END_DATE | number | precision 15 | missing | yes, as recorded | SAMP_TIME_MTH_D.FSCL_MONTH_END_DATE | not mapped | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.FSCL_MONTH_END_DATE] |
| 22 | FSCL_QTR | number | precision 15 | missing | yes, as recorded | SAMP_TIME_MTH_D.FSCL_QTR | not mapped | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.FSCL_QTR] |
| 23 | FSCL_QTR_END_DATE | number | precision 15 | missing | yes, as recorded | SAMP_TIME_MTH_D.FSCL_QTR_END_DATE | not mapped | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.FSCL_QTR_END_DATE] |
| 24 | FSCL_YEAR | number | precision 15 | missing | yes, as recorded | SAMP_TIME_MTH_D.FSCL_YEAR | not mapped | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.FSCL_YEAR] |
| 25 | FSCL_YEAR_END_DATE | number | precision 15 | missing | yes, as recorded | SAMP_TIME_MTH_D.FSCL_YEAR_END_DATE | not mapped | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.FSCL_YEAR_END_DATE] |
| 26 | JULIAN_MONTH_NUM | number | precision 15 | missing | yes, as recorded | SAMP_TIME_MTH_D.JULIAN_MONTH_NUM | not mapped | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.JULIAN_MONTH_NUM] |
| 27 | JULIAN_QTR_NUM | number | precision 15 | missing | yes, as recorded | SAMP_TIME_MTH_D.JULIAN_QTR_NUM | not mapped | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.JULIAN_QTR_NUM] |
| 28 | LOADED | number | precision 15 | missing | yes, as recorded | SAMP_TIME_MTH_D.LOADED | not mapped | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.LOADED] |
| 29 | MONTH_AGO_WID | number | precision 15 | missing | yes, as recorded | SAMP_TIME_MTH_D.MONTH_AGO_WID | not mapped | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.MONTH_AGO_WID] |
| 30 | MTH_KEY | number | precision 15 | missing | yes, as recorded | SAMP_TIME_MTH_D.MTH_KEY | not mapped | none (key; links the facts by month) | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.MTH_KEY] |
| 31 | PER_NAME_HALF | text | length 8 | not applicable | yes, as recorded | SAMP_TIME_MTH_D.PER_NAME_HALF | "Dim Time"."Per Name Half" | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.PER_NAME_HALF] |
| 32 | PER_NAME_MONTH | text | length 9 | not applicable | yes, as recorded | SAMP_TIME_MTH_D.PER_NAME_MONTH | "Dim Time"."Per Name Month" | V1-month | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.PER_NAME_MONTH] |
| 33 | PER_NAME_QTR | text | length 7 | not applicable | yes, as recorded | SAMP_TIME_MTH_D.PER_NAME_QTR | "Dim Time"."Per Name Qtr" | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.PER_NAME_QTR] |
| 34 | PER_NAME_YEAR | text | length 4 | not applicable | yes, as recorded | SAMP_TIME_MTH_D.PER_NAME_YEAR | "Dim Time"."Per Name Year" | F1 | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.PER_NAME_YEAR] |
| 35 | QUARTER_AGO_WID | number | precision 15 | missing | yes, as recorded | SAMP_TIME_MTH_D.QUARTER_AGO_WID | not mapped | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.QUARTER_AGO_WID] |
| 36 | YEAR_AGO_WID | number | precision 15 | missing | yes, as recorded | SAMP_TIME_MTH_D.YEAR_AGO_WID | not mapped | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.YEAR_AGO_WID] |

Every column records the external name `samplesales`.

#### Key

The repository declares two keys and does not say which is the primary key: "SAMP_TIME_MTH_D_Key", made of "MTH_KEY" [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.key.SAMP_TIME_MTH_D_Key], and "SAMP_TIME_MTH_D_Key#1", made of "CAL_YEAR" [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.key.SAMP_TIME_MTH_D_Key#1]. Proposal (not from OBIEE; to be signed): MTH_KEY is the key of the new table, because four of the five foreign keys to the table point at it. Whether CAL_YEAR identifies a row is question Q13.

#### Grain

The level of detail is stated in the section Model table Dim Time, source "LTS Time Month".

#### Links to other tables

- Foreign key "SAMP_REVENUE_F_FKey" from SAMP_REVENUE_F (BILL_MTH_KEY) to SAMP_TIME_MTH_D (MTH_KEY): proposed as a foreign key; stated in full under Relationships.
- Foreign key "SAMP_REVENUE_A_FKey" from SAMP_REVENUE_A (BILL_MTH_KEY) to SAMP_TIME_MTH_D (MTH_KEY): proposed as a foreign key; stated in full under Relationships.
- Foreign key "SAMP_REVENUE_A_MTH_Foreign Key" from SAMP_REVENUE_A_MTH (BILL_MTH_KEY) to SAMP_TIME_MTH_D (MTH_KEY): proposed as a foreign key; stated in full under Relationships.
- Foreign key "SAMP_TARGETS_F_FKey" from SAMP_TARGETS_F (BILL_MTH_KEY) to SAMP_TIME_MTH_D (MTH_KEY): proposed as a foreign key; stated in full under Relationships.
- Foreign key "SAMP_REVENUE_A_YR_Foreign Key" from SAMP_REVENUE_A_YR (BILL_YEAR_KEY) to SAMP_TIME_MTH_D (CAL_YEAR): proposed as a foreign key; stated in full under Relationships.

#### Read by

Source "LTS Time Month" of the model table Dim Time.

#### Missing

- Decimal places of all 32 number columns (scale not recorded).
- Which of the two keys is the primary key, and whether CAL_YEAR identifies a row (question Q13).
- Indexes, partitioning, volumes, and how and when the table is loaded.

## Old-to-new column list

Each row is a proposal to be signed by Data Engineering. It is like for like: the old table, column by column. Types are platform-neutral, because no target platform has been chosen. What the export does not hold is marked "missing" and is not guessed.

| New table.column | Old database column | Old field | Used by tab items | Evidence | Status |
|---|---|---|---|---|---|
| EMPLOYEE.EMP_ID | EMPLOYEE.EMP_ID | "Dim Sales Rep"."Employee ID" | none | [kb:sampleapp::phys::xe.SAMPLESALES.EMPLOYEE.EMP_ID] | proposal, to be signed by Data Engineering |
| EMPLOYEE.EMP_NAME | EMPLOYEE.EMP_NAME | "Dim Sales Rep"."Employee Name" | V3-rep | [kb:sampleapp::phys::xe.SAMPLESALES.EMPLOYEE.EMP_NAME] | proposal, to be signed by Data Engineering |
| EMPLOYEE.MANAGER_ID | EMPLOYEE.MANAGER_ID | "Dim Sales Rep"."Manager ID" | none | [kb:sampleapp::phys::xe.SAMPLESALES.EMPLOYEE.MANAGER_ID] | proposal, to be signed by Data Engineering |
| EMPLOYEE.OFFICE_ID | EMPLOYEE.OFFICE_ID | "Dim Sales Rep"."Office ID" | none | [kb:sampleapp::phys::xe.SAMPLESALES.EMPLOYEE.OFFICE_ID] | proposal, to be signed by Data Engineering |
| EMP_PARENT_CHILD.MEMBER_KEY | EMP_PARENT_CHILD.MEMBER_KEY | not mapped | none | [kb:sampleapp::phys::xe.SAMPLESALES.EMP_PARENT_CHILD.MEMBER_KEY] | proposal, to be signed by Data Engineering |
| EMP_PARENT_CHILD.ANCESTOR_KEY | EMP_PARENT_CHILD.ANCESTOR_KEY | not mapped | none | [kb:sampleapp::phys::xe.SAMPLESALES.EMP_PARENT_CHILD.ANCESTOR_KEY] | proposal, to be signed by Data Engineering |
| EMP_PARENT_CHILD.DISTANCE | EMP_PARENT_CHILD.DISTANCE | "Dim Sales Rep"."Distance" | none | [kb:sampleapp::phys::xe.SAMPLESALES.EMP_PARENT_CHILD.DISTANCE] | proposal, to be signed by Data Engineering |
| EMP_PARENT_CHILD.IS_LEAF | EMP_PARENT_CHILD.IS_LEAF | not mapped | none | [kb:sampleapp::phys::xe.SAMPLESALES.EMP_PARENT_CHILD.IS_LEAF] | proposal, to be signed by Data Engineering |
| FactCrossJoin.Preserve | FactCrossJoin.Preserve | "Fact Revenue"."Preserve" | none | [kb:sampleapp::phys::xe.SAMPLESALES.FactCrossJoin.Preserve] | proposal, to be signed by Data Engineering |
| SAMP_OFFICES_D.COMPANY | SAMP_OFFICES_D.COMPANY | "Dim Offices"."Company" | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_OFFICES_D.COMPANY] | proposal, to be signed by Data Engineering |
| SAMP_OFFICES_D.COMPANY_KEY | SAMP_OFFICES_D.COMPANY_KEY | "Dim Offices"."Company Key" | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_OFFICES_D.COMPANY_KEY] | proposal, to be signed by Data Engineering |
| SAMP_OFFICES_D.DEPARTMENT | SAMP_OFFICES_D.DEPARTMENT | "Dim Offices"."Department" | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_OFFICES_D.DEPARTMENT] | proposal, to be signed by Data Engineering |
| SAMP_OFFICES_D.DEPT_KEY | SAMP_OFFICES_D.DEPT_KEY | "Dim Offices"."Dept Key" | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_OFFICES_D.DEPT_KEY] | proposal, to be signed by Data Engineering |
| SAMP_OFFICES_D.OFFICE_DSC | SAMP_OFFICES_D.OFFICE_DSC | "Dim Offices"."Office Dsc" | F2, V2-office | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_OFFICES_D.OFFICE_DSC] | proposal, to be signed by Data Engineering |
| SAMP_OFFICES_D.OFFICE_KEY | SAMP_OFFICES_D.OFFICE_KEY | "Dim Offices"."Office Key" | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_OFFICES_D.OFFICE_KEY] | proposal, to be signed by Data Engineering |
| SAMP_OFFICES_D.ORGANIZATION | SAMP_OFFICES_D.ORGANIZATION | "Dim Offices"."Organization" | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_OFFICES_D.ORGANIZATION] | proposal, to be signed by Data Engineering |
| SAMP_OFFICES_D.ORG_KEY | SAMP_OFFICES_D.ORG_KEY | "Dim Offices"."Org Key" | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_OFFICES_D.ORG_KEY] | proposal, to be signed by Data Engineering |
| SAMP_OFFICES_D.SEQUENCE | SAMP_OFFICES_D.SEQUENCE | "Dim Offices"."Sequence" | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_OFFICES_D.SEQUENCE] | proposal, to be signed by Data Engineering |
| SAMP_OFFICES_D.AREA_NAME | SAMP_OFFICES_D.AREA_NAME | "Dim Offices"."Area Name" | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_OFFICES_D.AREA_NAME] | proposal, to be signed by Data Engineering |
| SAMP_PRODUCTS_D.ATTRIBUTE_1 | SAMP_PRODUCTS_D.ATTRIBUTE_1 | "Dim Products"."Attribute 1" | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_PRODUCTS_D.ATTRIBUTE_1] | proposal, to be signed by Data Engineering |
| SAMP_PRODUCTS_D.ATTRIBUTE_2 | SAMP_PRODUCTS_D.ATTRIBUTE_2 | "Dim Products"."Attribute 2" | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_PRODUCTS_D.ATTRIBUTE_2] | proposal, to be signed by Data Engineering |
| SAMP_PRODUCTS_D.BRAND | SAMP_PRODUCTS_D.BRAND | "Dim Products"."Brand" | F3 | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_PRODUCTS_D.BRAND] | proposal, to be signed by Data Engineering |
| SAMP_PRODUCTS_D.BRAND_KEY | SAMP_PRODUCTS_D.BRAND_KEY | "Dim Products"."Brand Key" | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_PRODUCTS_D.BRAND_KEY] | proposal, to be signed by Data Engineering |
| SAMP_PRODUCTS_D.LOB | SAMP_PRODUCTS_D.LOB | "Dim Products"."Lob" | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_PRODUCTS_D.LOB] | proposal, to be signed by Data Engineering |
| SAMP_PRODUCTS_D.LOB_KEY | SAMP_PRODUCTS_D.LOB_KEY | "Dim Products"."Lob Key" | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_PRODUCTS_D.LOB_KEY] | proposal, to be signed by Data Engineering |
| SAMP_PRODUCTS_D.PROD_DSC | SAMP_PRODUCTS_D.PROD_DSC | "Dim Products"."Prod Dsc" | V2-product | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_PRODUCTS_D.PROD_DSC] | proposal, to be signed by Data Engineering |
| SAMP_PRODUCTS_D.PROD_KEY | SAMP_PRODUCTS_D.PROD_KEY | "Dim Products"."Prod Key" | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_PRODUCTS_D.PROD_KEY] | proposal, to be signed by Data Engineering |
| SAMP_PRODUCTS_D.SEQUENCE | SAMP_PRODUCTS_D.SEQUENCE | "Dim Products"."Sequence" | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_PRODUCTS_D.SEQUENCE] | proposal, to be signed by Data Engineering |
| SAMP_PRODUCTS_D.TYPE | SAMP_PRODUCTS_D.TYPE | "Dim Products"."Type" | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_PRODUCTS_D.TYPE] | proposal, to be signed by Data Engineering |
| SAMP_PRODUCTS_D.TYPE_KEY | SAMP_PRODUCTS_D.TYPE_KEY | "Dim Products"."Type Key" | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_PRODUCTS_D.TYPE_KEY] | proposal, to be signed by Data Engineering |
| SAMP_PRODUCTS_D_TRANSLATION.LANG_ID | SAMP_PRODUCTS_D_TRANSLATION.LANG_ID | "Dim Products (Translated)"."LANG_ID" | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_PRODUCTS_D_TRANSLATION.LANG_ID] | proposal, to be signed by Data Engineering |
| SAMP_PRODUCTS_D_TRANSLATION.PROD_DESC | SAMP_PRODUCTS_D_TRANSLATION.PROD_DESC | "Dim Products (Translated)"."PROD_DESC" | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_PRODUCTS_D_TRANSLATION.PROD_DESC] | proposal, to be signed by Data Engineering |
| SAMP_PRODUCTS_D_TRANSLATION.PROD_KEY | SAMP_PRODUCTS_D_TRANSLATION.PROD_KEY | "Dim Products (Translated)"."PROD_KEY" | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_PRODUCTS_D_TRANSLATION.PROD_KEY] | proposal, to be signed by Data Engineering |
| SAMP_REVENUE_A.BILL_MTH_KEY | SAMP_REVENUE_A.BILL_MTH_KEY | "Fact Revenue"."Bill Mth Key" | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A.BILL_MTH_KEY] | proposal, to be signed by Data Engineering |
| SAMP_REVENUE_A.DISCNT_VALUE | SAMP_REVENUE_A.DISCNT_VALUE | "Fact Revenue"."Discnt Value" | K1, K3, K4, V1-net-revenue, V2-net-revenue, V2-discount, V3-net-revenue | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A.DISCNT_VALUE] | proposal, to be signed by Data Engineering |
| SAMP_REVENUE_A.PROD_KEY | SAMP_REVENUE_A.PROD_KEY | "Fact Revenue"."Prod Key" | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A.PROD_KEY] | proposal, to be signed by Data Engineering |
| SAMP_REVENUE_A.REVENUE | SAMP_REVENUE_A.REVENUE | "Fact Revenue"."Revenue" and five other measures | K1, K3, K4, V1-net-revenue, V2-net-revenue, V3-net-revenue | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A.REVENUE] | proposal, to be signed by Data Engineering |
| SAMP_REVENUE_A.UNITS | SAMP_REVENUE_A.UNITS | "Fact Revenue"."Units" | K2, K4, V2-units | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A.UNITS] | proposal, to be signed by Data Engineering |
| SAMP_REVENUE_A_MTH.BILL_MTH_KEY | SAMP_REVENUE_A_MTH.BILL_MTH_KEY | "Fact Revenue"."BILL_MTH_KEY" | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A_MTH.BILL_MTH_KEY] | proposal, to be signed by Data Engineering |
| SAMP_REVENUE_A_MTH.DISCNT_VALUE | SAMP_REVENUE_A_MTH.DISCNT_VALUE | "Fact Revenue"."DISCNT_VALUE" | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A_MTH.DISCNT_VALUE] | proposal, to be signed by Data Engineering |
| SAMP_REVENUE_A_MTH.REVENUE | SAMP_REVENUE_A_MTH.REVENUE | "Fact Revenue"."Revenue" | K1, K3, K4, V1-net-revenue, V2-net-revenue, V3-net-revenue | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A_MTH.REVENUE] | proposal, to be signed by Data Engineering |
| SAMP_REVENUE_A_MTH.UNITS | SAMP_REVENUE_A_MTH.UNITS | "Fact Revenue"."Units" | K2, K4, V2-units | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A_MTH.UNITS] | proposal, to be signed by Data Engineering |
| SAMP_REVENUE_A_YR.BILL_YEAR_KEY | SAMP_REVENUE_A_YR.BILL_YEAR_KEY | "Fact Revenue"."BILL_YEAR_KEY" | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A_YR.BILL_YEAR_KEY] | proposal, to be signed by Data Engineering |
| SAMP_REVENUE_A_YR.DISCNT_VALUE | SAMP_REVENUE_A_YR.DISCNT_VALUE | "Fact Revenue"."DISCNT_VALUE" | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A_YR.DISCNT_VALUE] | proposal, to be signed by Data Engineering |
| SAMP_REVENUE_A_YR.REVENUE | SAMP_REVENUE_A_YR.REVENUE | "Fact Revenue"."Revenue" | K1, K3, K4, V1-net-revenue, V2-net-revenue, V3-net-revenue | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A_YR.REVENUE] | proposal, to be signed by Data Engineering |
| SAMP_REVENUE_A_YR.UNITS | SAMP_REVENUE_A_YR.UNITS | "Fact Revenue"."Units" | K2, K4, V2-units | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A_YR.UNITS] | proposal, to be signed by Data Engineering |
| SAMP_REVENUE_F.BILL_DAY_DT | SAMP_REVENUE_F.BILL_DAY_DT | "Fact Revenue"."Bill Day Dt" | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F.BILL_DAY_DT] | proposal, to be signed by Data Engineering |
| SAMP_REVENUE_F.BILL_MTH_KEY | SAMP_REVENUE_F.BILL_MTH_KEY | "Fact Revenue"."Bill Mth Key" | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F.BILL_MTH_KEY] | proposal, to be signed by Data Engineering |
| SAMP_REVENUE_F.DISCNT_VALUE | SAMP_REVENUE_F.DISCNT_VALUE | "Fact Revenue"."Discnt Value" | K1, K3, K4, V1-net-revenue, V2-net-revenue, V2-discount, V3-net-revenue | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F.DISCNT_VALUE] | proposal, to be signed by Data Engineering |
| SAMP_REVENUE_F.OFFICE_KEY | SAMP_REVENUE_F.OFFICE_KEY | "Fact Revenue"."Office Key" | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F.OFFICE_KEY] | proposal, to be signed by Data Engineering |
| SAMP_REVENUE_F.ORDER_DAY_DT | SAMP_REVENUE_F.ORDER_DAY_DT | "Fact Revenue"."Order Day Dt" | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F.ORDER_DAY_DT] | proposal, to be signed by Data Engineering |
| SAMP_REVENUE_F.ORDER_NUMBER | SAMP_REVENUE_F.ORDER_NUMBER | "Fact Revenue"."Order Number" | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F.ORDER_NUMBER] | proposal, to be signed by Data Engineering |
| SAMP_REVENUE_F.ORDER_STATUS | SAMP_REVENUE_F.ORDER_STATUS | "Fact Revenue"."Order Status" | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F.ORDER_STATUS] | proposal, to be signed by Data Engineering |
| SAMP_REVENUE_F.ORDER_TYPE | SAMP_REVENUE_F.ORDER_TYPE | "Fact Revenue"."Order Type" | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F.ORDER_TYPE] | proposal, to be signed by Data Engineering |
| SAMP_REVENUE_F.PROD_KEY | SAMP_REVENUE_F.PROD_KEY | "Fact Revenue"."Prod Key" | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F.PROD_KEY] | proposal, to be signed by Data Engineering |
| SAMP_REVENUE_F.REVENUE | SAMP_REVENUE_F.REVENUE | "Fact Revenue"."Revenue" and five other measures | K1, K3, K4, V1-net-revenue, V2-net-revenue, V3-net-revenue | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F.REVENUE] | proposal, to be signed by Data Engineering |
| SAMP_REVENUE_F.UNITS | SAMP_REVENUE_F.UNITS | "Fact Revenue"."Units" | K2, K4, V2-units | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F.UNITS] | proposal, to be signed by Data Engineering |
| SAMP_REVENUE_F.EMPLOYEE_KEY | SAMP_REVENUE_F.EMPLOYEE_KEY | "Fact Revenue"."Sales Employee Key" | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F.EMPLOYEE_KEY] | proposal, to be signed by Data Engineering |
| SAMP_TARGETS_F.BILL_MTH_KEY | SAMP_TARGETS_F.BILL_MTH_KEY | "Fact Target"."Bill Mth Key" | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TARGETS_F.BILL_MTH_KEY] | proposal, to be signed by Data Engineering |
| SAMP_TARGETS_F.OFFICE_KEY | SAMP_TARGETS_F.OFFICE_KEY | "Fact Target"."Office Key" | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TARGETS_F.OFFICE_KEY] | proposal, to be signed by Data Engineering |
| SAMP_TARGETS_F.PROD_KEY | SAMP_TARGETS_F.PROD_KEY | "Fact Target"."Prod Key" | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TARGETS_F.PROD_KEY] | proposal, to be signed by Data Engineering |
| SAMP_TARGETS_F.REVENUE | SAMP_TARGETS_F.REVENUE | "Fact Target"."Revenue" | K3, V1-target | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TARGETS_F.REVENUE] | proposal, to be signed by Data Engineering |
| SAMP_TARGETS_F.UNITS | SAMP_TARGETS_F.UNITS | "Fact Target"."Units" | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TARGETS_F.UNITS] | proposal, to be signed by Data Engineering |
| SAMP_TIME_DAY_D.CALENDAR_DATE | SAMP_TIME_DAY_D.CALENDAR_DATE | "Dim Time"."Calendar Date" | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_DAY_D.CALENDAR_DATE] | proposal, to be signed by Data Engineering |
| SAMP_TIME_DAY_D.DAY_KEY | SAMP_TIME_DAY_D.DAY_KEY | not mapped | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_DAY_D.DAY_KEY] | proposal, to be signed by Data Engineering |
| SAMP_TIME_DAY_D.DAY_OF_MONTH | SAMP_TIME_DAY_D.DAY_OF_MONTH | not mapped | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_DAY_D.DAY_OF_MONTH] | proposal, to be signed by Data Engineering |
| SAMP_TIME_DAY_D.DAY_OF_WEEK | SAMP_TIME_DAY_D.DAY_OF_WEEK | not mapped | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_DAY_D.DAY_OF_WEEK] | proposal, to be signed by Data Engineering |
| SAMP_TIME_DAY_D.DAY_OF_YEAR | SAMP_TIME_DAY_D.DAY_OF_YEAR | not mapped | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_DAY_D.DAY_OF_YEAR] | proposal, to be signed by Data Engineering |
| SAMP_TIME_DAY_D.JULIAN_DAY_NUM | SAMP_TIME_DAY_D.JULIAN_DAY_NUM | not mapped | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_DAY_D.JULIAN_DAY_NUM] | proposal, to be signed by Data Engineering |
| SAMP_TIME_DAY_D.JULIAN_MONTH_NUM | SAMP_TIME_DAY_D.JULIAN_MONTH_NUM | not mapped | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_DAY_D.JULIAN_MONTH_NUM] | proposal, to be signed by Data Engineering |
| SAMP_TIME_DAY_D.JULIAN_QTR_NUM | SAMP_TIME_DAY_D.JULIAN_QTR_NUM | not mapped | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_DAY_D.JULIAN_QTR_NUM] | proposal, to be signed by Data Engineering |
| SAMP_TIME_DAY_D.JULIAN_WEEK_NUM | SAMP_TIME_DAY_D.JULIAN_WEEK_NUM | not mapped | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_DAY_D.JULIAN_WEEK_NUM] | proposal, to be signed by Data Engineering |
| SAMP_TIME_DAY_D.PER_NAME_HALF | SAMP_TIME_DAY_D.PER_NAME_HALF | "Dim Time"."Per Name Half" | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_DAY_D.PER_NAME_HALF] | proposal, to be signed by Data Engineering |
| SAMP_TIME_DAY_D.PER_NAME_MONTH | SAMP_TIME_DAY_D.PER_NAME_MONTH | "Dim Time"."Per Name Month" | V1-month | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_DAY_D.PER_NAME_MONTH] | proposal, to be signed by Data Engineering |
| SAMP_TIME_DAY_D.PER_NAME_QTR | SAMP_TIME_DAY_D.PER_NAME_QTR | "Dim Time"."Per Name Qtr" | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_DAY_D.PER_NAME_QTR] | proposal, to be signed by Data Engineering |
| SAMP_TIME_DAY_D.PER_NAME_WEEK | SAMP_TIME_DAY_D.PER_NAME_WEEK | "Dim Time"."Per Name Week" | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_DAY_D.PER_NAME_WEEK] | proposal, to be signed by Data Engineering |
| SAMP_TIME_DAY_D.PER_NAME_YEAR | SAMP_TIME_DAY_D.PER_NAME_YEAR | "Dim Time"."Per Name Year" | F1 | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_DAY_D.PER_NAME_YEAR] | proposal, to be signed by Data Engineering |
| SAMP_TIME_MTH_D.BEG_OF_QTR_WID | SAMP_TIME_MTH_D.BEG_OF_QTR_WID | not mapped | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.BEG_OF_QTR_WID] | proposal, to be signed by Data Engineering |
| SAMP_TIME_MTH_D.CAL_HALF | SAMP_TIME_MTH_D.CAL_HALF | not mapped | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.CAL_HALF] | proposal, to be signed by Data Engineering |
| SAMP_TIME_MTH_D.CAL_HALF_YEAR_END_DATE | SAMP_TIME_MTH_D.CAL_HALF_YEAR_END_DATE | not mapped | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.CAL_HALF_YEAR_END_DATE] | proposal, to be signed by Data Engineering |
| SAMP_TIME_MTH_D.CAL_MONTH | SAMP_TIME_MTH_D.CAL_MONTH | not mapped | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.CAL_MONTH] | proposal, to be signed by Data Engineering |
| SAMP_TIME_MTH_D.CAL_MONTH_END_DATE | SAMP_TIME_MTH_D.CAL_MONTH_END_DATE | not mapped | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.CAL_MONTH_END_DATE] | proposal, to be signed by Data Engineering |
| SAMP_TIME_MTH_D.CAL_QTR | SAMP_TIME_MTH_D.CAL_QTR | not mapped | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.CAL_QTR] | proposal, to be signed by Data Engineering |
| SAMP_TIME_MTH_D.CAL_QTR_END_DATE | SAMP_TIME_MTH_D.CAL_QTR_END_DATE | not mapped | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.CAL_QTR_END_DATE] | proposal, to be signed by Data Engineering |
| SAMP_TIME_MTH_D.CAL_YEAR | SAMP_TIME_MTH_D.CAL_YEAR | not mapped | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.CAL_YEAR] | proposal, to be signed by Data Engineering |
| SAMP_TIME_MTH_D.CAL_YEAR_END_DATE | SAMP_TIME_MTH_D.CAL_YEAR_END_DATE | not mapped | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.CAL_YEAR_END_DATE] | proposal, to be signed by Data Engineering |
| SAMP_TIME_MTH_D.DAYS_IN_FSCL_HALF_YEAR | SAMP_TIME_MTH_D.DAYS_IN_FSCL_HALF_YEAR | not mapped | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.DAYS_IN_FSCL_HALF_YEAR] | proposal, to be signed by Data Engineering |
| SAMP_TIME_MTH_D.DAYS_IN_FSCL_MONTH | SAMP_TIME_MTH_D.DAYS_IN_FSCL_MONTH | not mapped | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.DAYS_IN_FSCL_MONTH] | proposal, to be signed by Data Engineering |
| SAMP_TIME_MTH_D.DAYS_IN_FSCL_QTR | SAMP_TIME_MTH_D.DAYS_IN_FSCL_QTR | not mapped | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.DAYS_IN_FSCL_QTR] | proposal, to be signed by Data Engineering |
| SAMP_TIME_MTH_D.DAYS_IN_FSCL_YEAR | SAMP_TIME_MTH_D.DAYS_IN_FSCL_YEAR | not mapped | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.DAYS_IN_FSCL_YEAR] | proposal, to be signed by Data Engineering |
| SAMP_TIME_MTH_D.DAYS_IN_HALF_YEAR | SAMP_TIME_MTH_D.DAYS_IN_HALF_YEAR | not mapped | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.DAYS_IN_HALF_YEAR] | proposal, to be signed by Data Engineering |
| SAMP_TIME_MTH_D.DAYS_IN_MTH | SAMP_TIME_MTH_D.DAYS_IN_MTH | not mapped | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.DAYS_IN_MTH] | proposal, to be signed by Data Engineering |
| SAMP_TIME_MTH_D.DAYS_IN_QTR | SAMP_TIME_MTH_D.DAYS_IN_QTR | not mapped | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.DAYS_IN_QTR] | proposal, to be signed by Data Engineering |
| SAMP_TIME_MTH_D.DAYS_IN_YEAR | SAMP_TIME_MTH_D.DAYS_IN_YEAR | not mapped | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.DAYS_IN_YEAR] | proposal, to be signed by Data Engineering |
| SAMP_TIME_MTH_D.FSCL_HALF | SAMP_TIME_MTH_D.FSCL_HALF | not mapped | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.FSCL_HALF] | proposal, to be signed by Data Engineering |
| SAMP_TIME_MTH_D.FSCL_HALF_YEAR_END_DATE | SAMP_TIME_MTH_D.FSCL_HALF_YEAR_END_DATE | not mapped | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.FSCL_HALF_YEAR_END_DATE] | proposal, to be signed by Data Engineering |
| SAMP_TIME_MTH_D.FSCL_MONTH | SAMP_TIME_MTH_D.FSCL_MONTH | not mapped | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.FSCL_MONTH] | proposal, to be signed by Data Engineering |
| SAMP_TIME_MTH_D.FSCL_MONTH_END_DATE | SAMP_TIME_MTH_D.FSCL_MONTH_END_DATE | not mapped | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.FSCL_MONTH_END_DATE] | proposal, to be signed by Data Engineering |
| SAMP_TIME_MTH_D.FSCL_QTR | SAMP_TIME_MTH_D.FSCL_QTR | not mapped | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.FSCL_QTR] | proposal, to be signed by Data Engineering |
| SAMP_TIME_MTH_D.FSCL_QTR_END_DATE | SAMP_TIME_MTH_D.FSCL_QTR_END_DATE | not mapped | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.FSCL_QTR_END_DATE] | proposal, to be signed by Data Engineering |
| SAMP_TIME_MTH_D.FSCL_YEAR | SAMP_TIME_MTH_D.FSCL_YEAR | not mapped | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.FSCL_YEAR] | proposal, to be signed by Data Engineering |
| SAMP_TIME_MTH_D.FSCL_YEAR_END_DATE | SAMP_TIME_MTH_D.FSCL_YEAR_END_DATE | not mapped | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.FSCL_YEAR_END_DATE] | proposal, to be signed by Data Engineering |
| SAMP_TIME_MTH_D.JULIAN_MONTH_NUM | SAMP_TIME_MTH_D.JULIAN_MONTH_NUM | not mapped | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.JULIAN_MONTH_NUM] | proposal, to be signed by Data Engineering |
| SAMP_TIME_MTH_D.JULIAN_QTR_NUM | SAMP_TIME_MTH_D.JULIAN_QTR_NUM | not mapped | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.JULIAN_QTR_NUM] | proposal, to be signed by Data Engineering |
| SAMP_TIME_MTH_D.LOADED | SAMP_TIME_MTH_D.LOADED | not mapped | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.LOADED] | proposal, to be signed by Data Engineering |
| SAMP_TIME_MTH_D.MONTH_AGO_WID | SAMP_TIME_MTH_D.MONTH_AGO_WID | not mapped | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.MONTH_AGO_WID] | proposal, to be signed by Data Engineering |
| SAMP_TIME_MTH_D.MTH_KEY | SAMP_TIME_MTH_D.MTH_KEY | not mapped | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.MTH_KEY] | proposal, to be signed by Data Engineering |
| SAMP_TIME_MTH_D.PER_NAME_HALF | SAMP_TIME_MTH_D.PER_NAME_HALF | "Dim Time"."Per Name Half" | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.PER_NAME_HALF] | proposal, to be signed by Data Engineering |
| SAMP_TIME_MTH_D.PER_NAME_MONTH | SAMP_TIME_MTH_D.PER_NAME_MONTH | "Dim Time"."Per Name Month" | V1-month | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.PER_NAME_MONTH] | proposal, to be signed by Data Engineering |
| SAMP_TIME_MTH_D.PER_NAME_QTR | SAMP_TIME_MTH_D.PER_NAME_QTR | "Dim Time"."Per Name Qtr" | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.PER_NAME_QTR] | proposal, to be signed by Data Engineering |
| SAMP_TIME_MTH_D.PER_NAME_YEAR | SAMP_TIME_MTH_D.PER_NAME_YEAR | "Dim Time"."Per Name Year" | F1 | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.PER_NAME_YEAR] | proposal, to be signed by Data Engineering |
| SAMP_TIME_MTH_D.QUARTER_AGO_WID | SAMP_TIME_MTH_D.QUARTER_AGO_WID | not mapped | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.QUARTER_AGO_WID] | proposal, to be signed by Data Engineering |
| SAMP_TIME_MTH_D.YEAR_AGO_WID | SAMP_TIME_MTH_D.YEAR_AGO_WID | not mapped | none | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.YEAR_AGO_WID] | proposal, to be signed by Data Engineering |

## Power BI semantic model

The model tables, their measures, relationships, hierarchies and security, each tied to what OBIEE holds. Every definition is a proposal to be signed. Where OBIEE behaves in a way Power BI does not reproduce by itself, no definition is proposed; the choice is listed under Decisions needed.

### Model table Fact Revenue (part 1 of 2)

Proposal (not from OBIEE; to be signed): a table of the Power BI model named "Fact Revenue", like the business table, read from the new tables named with its sources in part 2 of this section.

The business table "Fact Revenue" is in business model "SAMPLESALES", and the repository declares 5 table sources for it. [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue] It has 22 columns in this slice, of which 9 are measures; 18 are not confirmed for the tab. Its sources are "Aggregate Revenue", "Aggregate Revenue By Month", "Aggregate Revenue by Year", "FactCrossJoin" and "SAMP_REVENUE_F"; they are described in part 2. The tab uses four of its fields: "Discnt Value", "Preferred Currency", "Revenue" and "Units".

#### Keys

Keys of logical table "Fact Revenue": not recorded. The repository declares no key for it.

#### Fields carried into the model

| Field | From new table.column | Type, precision, nullable as the packet prints them | Tab items | Evidence |
|---|---|---|---|---|
| Preferred Currency | none: mapped in 0 of 5 table sources; calculated from other logical objects | not a measure; writeable false; type not printed | N1-currency | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Preferred Currency] |

"Preferred Currency" is shown to report authors as "Preferred Currency" in presentation table "Fact Revenue"; name set by hand: false; confirmed for the tab, item N1-currency. [kb:sampleapp::pres::Sample Sales.Fact Revenue.Preferred Currency] Its formula, calculated from other logical objects, is:

```
 INDEXCOL( CASE  VALUEOF(NQ_SESSION."PREFERRED_CURRENCY") WHEN 'gc1' THEN 0 WHEN 'gc2' THEN 1 WHEN 'gc3' THEN 2 END , "SAMPLESALES"."Fact Revenue"."Revenue", "SAMPLESALES"."Fact Revenue"."Revenue (EUR)", "SAMPLESALES"."Fact Revenue"."Revenue (GBP)")
```

[kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Preferred Currency]

The formula names the variable PREFERRED_CURRENCY inside VALUEOF(NQ_SESSION. …) and the measures "Revenue", "Revenue (EUR)" and "Revenue (GBP)" of "Fact Revenue" [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Preferred Currency]. This is a switch on a value set per user: no definition is proposed until decision Q2 is taken. "Revenue (EUR)" [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Revenue (EUR)] and "Revenue (GBP)" [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Revenue (GBP)] are not confirmed for the tab and are not designed as measures here; they are needed only if decision Q2 keeps the switch. Both are measures, writeable false, mapped in 2 of 5 table sources, with the aggregation rules ` SUM("SAMPLESALES"."Fact Revenue"."Revenue (EUR)")` [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Revenue (EUR)#aggr0] and ` SUM("SAMPLESALES"."Fact Revenue"."Revenue (GBP)")` [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Revenue (GBP)#aggr0]; how each source maps them is stated in part 2. The variable is described in the section Values set per user or for the whole system. See also as-is document, question Q2.

The 12 fields of this part that are not measures and not confirmed ("BILL_MTH_KEY", "BILL_YEAR_KEY", "Bill Day Dt", "Bill Mth Key", "DISCNT_VALUE", "Office Key", "Order Day Dt", "Order Number", "Order Status", "Order Type", "Prod Key", "Sales Employee Key") are not carried as fields. Some of them are key fields: the relationships to the dimension tables may still need their columns; see Relationships.

#### Measure: Revenue

- Replaces: the OBIEE measure "Revenue" of "Fact Revenue" [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Revenue], shown to report authors as "Revenue" [kb:sampleapp::pres::Sample Sales.Fact Revenue.Revenue]; confirmed for the tab, items K1, K3, K4, V1-net-revenue, V2-net-revenue, V3-net-revenue.
- OBIEE formula it replaces, exactly: ` SUM("SAMPLESALES"."Fact Revenue"."Revenue")` [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Revenue#aggr0]
- Read from: mapped in 4 of 5 table sources ("Aggregate Revenue By Month", "Aggregate Revenue by Year", "Aggregate Revenue", "SAMP_REVENUE_F"); the column each maps it from is stated with each source in part 2. Which new tables are kept is decision Q3 (part 2).
- Writeable in OBIEE: false.
- Proposal (not from OBIEE; to be signed):

      Revenue = SUM('Fact Revenue'[Revenue])

#### Measure: Discount Value

- Replaces: the OBIEE measure "Discnt Value" of "Fact Revenue" [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Discnt Value], shown to report authors as "Discnt Value" [kb:sampleapp::pres::Sample Sales.Fact Revenue.Discnt Value]; confirmed for the tab, items K1, K3, K4, V1-net-revenue, V2-net-revenue, V2-discount, V3-net-revenue.
- OBIEE formula it replaces, exactly: ` SUM("SAMPLESALES"."Fact Revenue"."Discnt Value")` [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Discnt Value#aggr0]
- Read from: mapped in 2 of 5 table sources ("Aggregate Revenue", "SAMP_REVENUE_F"); the column each maps it from is stated with each source in part 2.
- Writeable in OBIEE: false.
- Proposal (not from OBIEE; to be signed):

      Discount Value = SUM('Fact Revenue'[Discnt Value])

#### Measure: Units

- Replaces: the OBIEE measure "Units" of "Fact Revenue" [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Units], shown to report authors as "Units" [kb:sampleapp::pres::Sample Sales.Fact Revenue.Units]; confirmed for the tab, items K2, K4, V2-units.
- OBIEE formula it replaces, exactly: ` SUM("SAMPLESALES"."Fact Revenue"."Units")` [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Units#aggr0]
- Read from: mapped in 4 of 5 table sources ("Aggregate Revenue By Month", "Aggregate Revenue by Year", "Aggregate Revenue", "SAMP_REVENUE_F"); the column each maps it from is stated with each source in part 2.
- Writeable in OBIEE: false.
- Proposal (not from OBIEE; to be signed):

      Units = SUM('Fact Revenue'[Units])

#### Not used by this tab

The measures "All Months Revenue", "All Products Revenue", "Preserve" and "Revenue (INR)" are not confirmed for the tab and are not designed; they are listed under Not described.

### Model table Fact Revenue (part 2 of 2)

#### Where it reads from

"Fact Revenue" has five sources, all active. None of them joins tables: each reads one database table.

| Source | Active | Priority group | Level of detail, exactly | Reads | Evidence |
|---|---|---|---|---|---|
| Aggregate Revenue | true | `1` | ` GROUPBYLEVEL("SAMPLESALES"."Time Hierarchy"."Month", "SAMPLESALES"."Products Hierararchy"."Product Detail")` | new table SAMP_REVENUE_A (old "SAMP_REVENUE_A" in `xe.SAMPLESALES`, type `none`) [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue#table0] | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue] |
| Aggregate Revenue By Month | true | not recorded (empty; it does not mean lowest) | ` GROUPBYLEVEL("SAMPLESALES"."Time Hierarchy"."Month")` | new table SAMP_REVENUE_A_MTH (old "SAMP_REVENUE_A_MTH" in `xe.SAMPLESALES`, type `none`) [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue By Month#table0] | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue By Month] |
| Aggregate Revenue by Year | true | not recorded (empty; it does not mean lowest) | ` GROUPBYLEVEL("SAMPLESALES"."Time Hierarchy"."Year")` | new table SAMP_REVENUE_A_YR (old "SAMP_REVENUE_A_YR" in `xe.SAMPLESALES`, type `none`) [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue by Year#table0] | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue by Year] |
| FactCrossJoin | true | not recorded (empty; it does not mean lowest) | not recorded; the repository gives this source no level of detail | new view FactCrossJoin (old "FactCrossJoin" in `xe.SAMPLESALES`, type `select`, a query kept in the repository) [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.FactCrossJoin#table0] | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.FactCrossJoin] |
| SAMP_REVENUE_F | true | not recorded (empty; it does not mean lowest) | ` GROUPBYLEVEL("SAMPLESALES"."Sales Rep Hierarchy"."Detail", "SAMPLESALES"."Time Hierarchy"."Dim Time Detail", "SAMPLESALES"."Office Hierarchy"."Office Detail", "SAMPLESALES"."Products Hierararchy"."Product Detail")` | new table SAMP_REVENUE_F (old "SAMP_REVENUE_F" in `xe.SAMPLESALES`, type `none`) [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.SAMP_REVENUE_F#table0] | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.SAMP_REVENUE_F] |

Four sources at different levels of detail feed the same measures: this is decision Q3. "FactCrossJoin" maps only "Preserve", which the tab does not use [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.FactCrossJoin#map0]; whether its query is kept is decided in the section New view FactCrossJoin.

#### What each source maps the confirmed measures from

| Measure | Source | Mapped from, exactly | Physical column as printed | Evidence |
|---|---|---|---|---|
| Revenue | Aggregate Revenue | `"xe".""."SAMPLESALES"."SAMP_REVENUE_A"."REVENUE"` | type DOUBLE, precision 8, scale 2, nullable true | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue#map3] |
| Revenue | Aggregate Revenue By Month | `"xe".""."SAMPLESALES"."SAMP_REVENUE_A_MTH"."REVENUE"` | type DOUBLE, precision 22, nullable true | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue By Month#map0] |
| Revenue | Aggregate Revenue by Year | `"xe".""."SAMPLESALES"."SAMP_REVENUE_A_YR"."REVENUE"` | type DOUBLE, precision 22, nullable true | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue by Year#map0] |
| Revenue | SAMP_REVENUE_F | `"xe".""."SAMPLESALES"."SAMP_REVENUE_F"."REVENUE"` | type DOUBLE, precision 7, scale 2, nullable true | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.SAMP_REVENUE_F#map9] |
| Discount Value ("Discnt Value") | Aggregate Revenue | `"xe".""."SAMPLESALES"."SAMP_REVENUE_A"."DISCNT_VALUE"` | type DOUBLE, precision 7, scale 2, nullable true | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue#map1] |
| Discount Value ("Discnt Value") | SAMP_REVENUE_F | `"xe".""."SAMPLESALES"."SAMP_REVENUE_F"."DISCNT_VALUE"` | type DOUBLE, precision 6, scale 2, nullable true | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.SAMP_REVENUE_F#map2] |
| Units | Aggregate Revenue | `"xe".""."SAMPLESALES"."SAMP_REVENUE_A"."UNITS"` | type DOUBLE, precision 15, nullable true | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue#map4] |
| Units | Aggregate Revenue By Month | `"xe".""."SAMPLESALES"."SAMP_REVENUE_A_MTH"."UNITS"` | type DOUBLE, precision 22, nullable true | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue By Month#map1] |
| Units | Aggregate Revenue by Year | `"xe".""."SAMPLESALES"."SAMP_REVENUE_A_YR"."UNITS"` | type DOUBLE, precision 22, nullable true | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue by Year#map1] |
| Units | SAMP_REVENUE_F | `"xe".""."SAMPLESALES"."SAMP_REVENUE_F"."UNITS"` | type DOUBLE, precision 15, nullable true | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.SAMP_REVENUE_F#map10] |

The measure "Discnt Value" is not mapped in "Aggregate Revenue By Month" or "Aggregate Revenue by Year". Those two sources map the column DISCNT_VALUE to a separate field "DISCNT_VALUE", which is not a measure ([kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue By Month#map3], [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue by Year#map2]). So if the summary tables are kept (decision Q3), Net revenue cannot be taken from them as mapped today.

#### The currency measures the switch "Preferred Currency" needs

"Revenue (EUR)" and "Revenue (GBP)" are mapped differently in the two sources that map them. In "Aggregate Revenue" both are the column REVENUE as it is: `"xe".""."SAMPLESALES"."SAMP_REVENUE_A"."REVENUE"` [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue#map8] (EUR) and [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue#map7] (GBP). In "SAMP_REVENUE_F" each is multiplied by a fixed number: `"xe".""."SAMPLESALES"."SAMP_REVENUE_F"."REVENUE" * 0.76` [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.SAMP_REVENUE_F#map15] and `"xe".""."SAMPLESALES"."SAMP_REVENUE_F"."REVENUE" * 0.62` [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.SAMP_REVENUE_F#map14]. These matter only if decision Q2 keeps the currency switch; they are decisions Q4 and Q5. See also as-is document, question Q4.

#### Columns the relationships need

The detail source "SAMP_REVENUE_F" maps the fields that link to the dimension tables. They are not confirmed for the tab and are not offered as fields, but the model table keeps the columns the relationships need (see Relationships).

| Field | Mapped from, exactly | Physical column as printed | Evidence |
|---|---|---|---|
| Bill Day Dt | `"xe".""."SAMPLESALES"."SAMP_REVENUE_F"."BILL_DAY_DT"` | type DATE, nullable true | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.SAMP_REVENUE_F#map0] |
| Bill Mth Key | `"xe".""."SAMPLESALES"."SAMP_REVENUE_F"."BILL_MTH_KEY"` | type DOUBLE, precision 15, nullable true | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.SAMP_REVENUE_F#map1] |
| Office Key | `"xe".""."SAMPLESALES"."SAMP_REVENUE_F"."OFFICE_KEY"` | type DOUBLE, precision 15, nullable true | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.SAMP_REVENUE_F#map3] |
| Prod Key | `"xe".""."SAMPLESALES"."SAMP_REVENUE_F"."PROD_KEY"` | type DOUBLE, precision 15, nullable true | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.SAMP_REVENUE_F#map8] |
| Sales Employee Key | `"xe".""."SAMPLESALES"."SAMP_REVENUE_F"."EMPLOYEE_KEY"` | type INT, nullable not stated | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.SAMP_REVENUE_F#map13] |
| Order Day Dt | `"xe".""."SAMPLESALES"."SAMP_REVENUE_F"."ORDER_DAY_DT"` | type DATETIME, nullable true | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.SAMP_REVENUE_F#map4] |

The other mappings of this packet (in "Aggregate Revenue": "Bill Mth Key", "Prod Key", "All Products Revenue", "All Months Revenue", "Revenue (INR)"; in the month and year sources: "BILL_MTH_KEY", "BILL_YEAR_KEY"; in "SAMP_REVENUE_F": "All Products Revenue", "All Months Revenue", "Revenue (INR)", "Order Number", "Order Status", "Order Type") are of fields the tab does not use.

Which of the new tables SAMP_REVENUE_A, SAMP_REVENUE_A_MTH, SAMP_REVENUE_A_YR and SAMP_REVENUE_F the model table reads is not chosen here: it is decision Q3. The measures of part 1 are defined over the model table Fact Revenue whichever tables feed it.

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

### Model table Dim Products

Proposal (not from OBIEE; to be signed): a table of the Power BI model named "Dim Products", like the business table, read from the new table SAMP_PRODUCTS_D.

The business table "Dim Products" is in business model "SAMPLESALES", with 1 table source declared. [kb:sampleapp::bmm::SAMPLESALES.Dim Products] It has 12 columns in this slice, none of them measures; 10 are not confirmed for the tab. The tab uses two fields, "Brand" and "Prod Dsc".

#### Keys

Key "SAMP_PRODUCTS_D_Key": marked as primary key true; made of column(s) "Prod Key". [kb:sampleapp::bmm::SAMPLESALES.Dim Products.key.SAMP_PRODUCTS_D_Key]

Proposal (not from OBIEE; to be signed): "Prod Key" is the key of the model table Dim Products, the one side of its relationships.

#### Where it reads from

| Source | Active | Priority group | Level of detail, exactly | Reads | Evidence |
|---|---|---|---|---|---|
| SAMP_PRODUCTS_D | true | not recorded (empty; it does not mean lowest) | ` GROUPBYLEVEL("SAMPLESALES"."Products Hierararchy"."Product Detail")` | new table SAMP_PRODUCTS_D (old "SAMP_PRODUCTS_D" in `xe.SAMPLESALES`, type `none`) [kb:sampleapp::bmm::SAMPLESALES.Dim Products.src.SAMP_PRODUCTS_D#table0] | [kb:sampleapp::bmm::SAMPLESALES.Dim Products.src.SAMP_PRODUCTS_D] |

One source, one table, no join, no content filter.

#### Fields carried into the model

| Field | From new table.column | Type, precision, nullable as the packet prints them | Tab items | Evidence |
|---|---|---|---|---|
| Brand | SAMP_PRODUCTS_D.BRAND, mapped as `"xe".""."SAMPLESALES"."SAMP_PRODUCTS_D"."BRAND"` | type VARCHAR, precision 8, nullable true | F3 | [kb:sampleapp::bmm::SAMPLESALES.Dim Products.src.SAMP_PRODUCTS_D#map2] |
| Prod Dsc | SAMP_PRODUCTS_D.PROD_DSC, mapped as `"xe".""."SAMPLESALES"."SAMP_PRODUCTS_D"."PROD_DSC"` | type VARCHAR, precision 26, nullable true | V2-product | [kb:sampleapp::bmm::SAMPLESALES.Dim Products.src.SAMP_PRODUCTS_D#map6] |

"Brand" is shown to report authors as "Brand" in presentation table "Dim Products"; name set by hand: false; confirmed for the tab, item F3. [kb:sampleapp::pres::Sample Sales.Dim Products.Brand] "Prod Dsc" is shown as "Prod Dsc"; name set by hand: false; confirmed for the tab, item V2-product. [kb:sampleapp::pres::Sample Sales.Dim Products.Prod Dsc]

The identifier of "Prod Dsc" is held by "Dim Products"."Prod Key" (OBIEE's "descriptor ID"): "Prod Dsc" is the text shown, and filters use "Prod Key" in its place. [kb:sampleapp::bmm::SAMPLESALES.Dim Products.Prod Dsc] Proposal (not from OBIEE; to be signed): the model table keeps "Prod Key", so that two products with the same description stay apart in a visual.

The 10 fields not confirmed ("Attribute 1", "Attribute 2", "Brand Key", "Lob", "Lob Key", "Prod Dsc (Translated)", "Prod Key", "Sequence", "Type", "Type Key") are not carried as fields. "Prod Key" is the key field: the relationships need its column, mapped as `"xe".""."SAMPLESALES"."SAMP_PRODUCTS_D"."PROD_KEY"` (type DOUBLE, precision 15, nullable not stated) [kb:sampleapp::bmm::SAMPLESALES.Dim Products.src.SAMP_PRODUCTS_D#map7]; see Relationships. "Prod Dsc (Translated)" is a lookup on a value set per user and is not used by the tab [kb:sampleapp::bmm::SAMPLESALES.Dim Products.Prod Dsc (Translated)]; see also as-is document, question Q8.

#### Measures

None: the table holds no measure.

### Model table Dim Products (Translated)

The business table "Dim Products (Translated)" is in business model "SAMPLESALES", with 1 table source declared, and is marked in the repository as a lookup table: it is read through the LOOKUP function and is not joined like a dimension. [kb:sampleapp::bmm::SAMPLESALES.Dim Products (Translated)] It has 3 columns in this slice, none of them measures, and none confirmed for the tab.

Proposal (not from OBIEE; to be signed): no model table for "Dim Products (Translated)" in this tab's model. No field of it is confirmed for the tab, and the only field that reads it, "Prod Dsc (Translated)" of "Dim Products", is not confirmed either (section Model table Dim Products). If a later tab needs translated product names, it becomes the decision on lookup and translation (see also as-is document, question Q8).

For the record: its key is "PK_PROD_TRANS1", marked as primary key true, made of "PROD_KEY" and "LANG_ID" [kb:sampleapp::bmm::SAMPLESALES.Dim Products (Translated).key.PK_PROD_TRANS1]; its one source "SAMP_PRODUCTS_D_TRANSLATION" is active, with priority group not recorded and no level of detail recorded, and reads the database table SAMP_PRODUCTS_D_TRANSLATION [kb:sampleapp::bmm::SAMPLESALES.Dim Products (Translated).src.SAMP_PRODUCTS_D_TRANSLATION]. Its rows are listed under Not described.

#### Measures

None: the table holds no measure.

### Model table Dim Sales Rep

Proposal (not from OBIEE; to be signed): a table of the Power BI model named "Dim Sales Rep", like the business table, read from the new table EMPLOYEE and, depending on decision Q7, the new table EMP_PARENT_CHILD.

The business table "Dim Sales Rep" is in business model "SAMPLESALES", with 1 table source declared. [kb:sampleapp::bmm::SAMPLESALES.Dim Sales Rep] It has 5 columns in this slice, none of them measures; 4 are not confirmed for the tab. The tab uses one field, "Employee Name".

#### Keys

Key "EMP_ID": marked as primary key true; made of column(s) "Employee ID". [kb:sampleapp::bmm::SAMPLESALES.Dim Sales Rep.key.EMP_ID]

#### Where it reads from

| Source | Active | Priority group | Level of detail, exactly | Reads | Evidence |
|---|---|---|---|---|---|
| LTS Employee | true | not recorded (empty; it does not mean lowest) | ` GROUPBYLEVEL("SAMPLESALES"."Sales Rep Hierarchy"."Detail")` | new table EMPLOYEE (old "EMPLOYEE" in `xe.SAMPLESALES`, type `none`) [kb:sampleapp::bmm::SAMPLESALES.Dim Sales Rep.src.LTS Employee#table0] and new table EMP_PARENT_CHILD (old "EMP_PARENT_CHILD" in `xe.SAMPLESALES`, type `none`) [kb:sampleapp::bmm::SAMPLESALES.Dim Sales Rep.src.LTS Employee#table1] | [kb:sampleapp::bmm::SAMPLESALES.Dim Sales Rep.src.LTS Employee] |

The source names "EMP_PARENT_CHILD" as the table that holds the parent-child relationships (its closure table); the drill path is set out under Hierarchies.

The source joins its two tables:

| Listed under | Joined to | Join type | Cardinality as printed | On | Evidence |
|---|---|---|---|---|---|
| EMPLOYEE | EMP_PARENT_CHILD | `Inner` | ` ONE TO MANY` | foreign key "EMP_PARENT_CHILD_FKey": "EMP_PARENT_CHILD" ("ANCESTOR_KEY") = "EMPLOYEE" ("EMP_ID") | [kb:sampleapp::bmm::SAMPLESALES.Dim Sales Rep.src.LTS Employee#table0.join0] |
| EMP_PARENT_CHILD | EMPLOYEE | `Inner` | `MANY TO ONE` | foreign key "EMP_PARENT_CHILD_FKey": "EMP_PARENT_CHILD" ("ANCESTOR_KEY") = "EMPLOYEE" ("EMP_ID") | [kb:sampleapp::bmm::SAMPLESALES.Dim Sales Rep.src.LTS Employee#table1.join0] |

The two lines are one join, listed once under each table. A join inside a source is a decision: Q7. As joined, one employee can give several rows, one per row of EMP_PARENT_CHILD whose ANCESTOR_KEY is that employee [kb:sampleapp::bmm::SAMPLESALES.Dim Sales Rep.src.LTS Employee#table0.join0]; see also as-is document, question Q9.

#### Fields carried into the model

| Field | From new table.column | Type, precision, nullable as the packet prints them | Tab items | Evidence |
|---|---|---|---|---|
| Employee Name | EMPLOYEE.EMP_NAME, mapped as `"xe".""."SAMPLESALES"."EMPLOYEE"."EMP_NAME"` | type VARCHAR, precision 40, nullable true | V3-rep | [kb:sampleapp::bmm::SAMPLESALES.Dim Sales Rep.src.LTS Employee#map1] |

"Employee Name" is shown to report authors as "Employee Name" in presentation table "Dim Sales Rep"; name set by hand: false; confirmed for the tab, item V3-rep. [kb:sampleapp::pres::Sample Sales.Dim Sales Rep.Employee Name]

The 4 fields not confirmed ("Distance", "Employee ID", "Manager ID", "Office ID") are not carried as fields. "Employee ID" is the key field: the relationships need its column, mapped as `"xe".""."SAMPLESALES"."EMPLOYEE"."EMP_ID"` (type DOUBLE, precision 15, nullable not stated) [kb:sampleapp::bmm::SAMPLESALES.Dim Sales Rep.src.LTS Employee#map0]; see Relationships. "Distance" is the one field read from EMP_PARENT_CHILD, as `"xe".""."SAMPLESALES"."EMP_PARENT_CHILD"."DISTANCE"` [kb:sampleapp::bmm::SAMPLESALES.Dim Sales Rep.src.LTS Employee#map4]; the tab does not use it.

#### Measures

None: the table holds no measure.

### Model table Dim Time

Proposal (not from OBIEE; to be signed): a table of the Power BI model named "Dim Time", like the business table, read from the new table SAMP_TIME_DAY_D and, depending on decision Q8, the new table SAMP_TIME_MTH_D.

The business table "Dim Time" is in business model "SAMPLESALES", with 2 table sources declared. [kb:sampleapp::bmm::SAMPLESALES.Dim Time] It has 6 columns in this slice, none of them measures; 4 are not confirmed for the tab. The tab uses two fields, "Per Name Month" and "Per Name Year".

#### Keys

Key "SAMP_TIME_DAY_D_Key": marked as primary key true; made of column(s) "Calendar Date". [kb:sampleapp::bmm::SAMPLESALES.Dim Time.key.SAMP_TIME_DAY_D_Key]

#### Where it reads from

| Source | Active | Priority group | Level of detail, exactly | Reads | Evidence |
|---|---|---|---|---|---|
| LTS Time Day | true | not recorded (empty; it does not mean lowest) | ` GROUPBYLEVEL("SAMPLESALES"."Time Hierarchy"."Dim Time Detail")` | new table SAMP_TIME_DAY_D (old "SAMP_TIME_DAY_D" in `xe.SAMPLESALES`, type `none`) [kb:sampleapp::bmm::SAMPLESALES.Dim Time.src.LTS Time Day#table0] | [kb:sampleapp::bmm::SAMPLESALES.Dim Time.src.LTS Time Day] |
| LTS Time Month | true | not recorded (empty; it does not mean lowest) | ` GROUPBYLEVEL("SAMPLESALES"."Time Hierarchy"."Month")` | new table SAMP_TIME_MTH_D (old "SAMP_TIME_MTH_D" in `xe.SAMPLESALES`, type `none`) [kb:sampleapp::bmm::SAMPLESALES.Dim Time.src.LTS Time Month#table0] | [kb:sampleapp::bmm::SAMPLESALES.Dim Time.src.LTS Time Month] |

Two sources at different levels of detail, day and month: this is decision Q8. Neither joins tables; no content filter is recorded.

#### Fields carried into the model

| Field | From new table.column | Type, precision, nullable as the packet prints them | Tab items | Evidence |
|---|---|---|---|---|
| Per Name Year | SAMP_TIME_DAY_D.PER_NAME_YEAR, mapped as `"xe".""."SAMPLESALES"."SAMP_TIME_DAY_D"."PER_NAME_YEAR"` | type VARCHAR, precision 4, nullable not stated | F1 | [kb:sampleapp::bmm::SAMPLESALES.Dim Time.src.LTS Time Day#map0] |
| Per Name Year | SAMP_TIME_MTH_D.PER_NAME_YEAR, mapped as `"xe".""."SAMPLESALES"."SAMP_TIME_MTH_D"."PER_NAME_YEAR"` | type VARCHAR, precision 4, nullable true | F1 | [kb:sampleapp::bmm::SAMPLESALES.Dim Time.src.LTS Time Month#map0] |
| Per Name Month | SAMP_TIME_DAY_D.PER_NAME_MONTH, mapped as `"xe".""."SAMPLESALES"."SAMP_TIME_DAY_D"."PER_NAME_MONTH"` | type VARCHAR, precision 9, nullable not stated | V1-month | [kb:sampleapp::bmm::SAMPLESALES.Dim Time.src.LTS Time Day#map3] |
| Per Name Month | SAMP_TIME_MTH_D.PER_NAME_MONTH, mapped as `"xe".""."SAMPLESALES"."SAMP_TIME_MTH_D"."PER_NAME_MONTH"` | type VARCHAR, precision 9, nullable true | V1-month | [kb:sampleapp::bmm::SAMPLESALES.Dim Time.src.LTS Time Month#map2] |

Each confirmed field is mapped in both sources; one row per source.

"Per Name Year" is shown to report authors as "Per Name Year" in presentation table "Dim Time"; name set by hand: false; confirmed for the tab, item F1. [kb:sampleapp::pres::Sample Sales.Dim Time.Per Name Year] Its name shown to users is written as `VALUEOF(NQ_SESSION.CN_Sample_Sales_Dim_Time_Per_Name_Year)`. "Per Name Month" is shown as "Per Name Month"; name set by hand: false; confirmed for the tab, item V1-month. [kb:sampleapp::pres::Sample Sales.Dim Time.Per Name Month] Its name shown to users is written as `VALUEOF(NQ_SESSION.CN_Sample_Sales_Dim_Time_Per_Name_Month)`. A displayed name set by a variable is decision Q9; see also as-is document, question Q1.

The 4 fields not confirmed ("Calendar Date", "Per Name Half", "Per Name Qtr", "Per Name Week") are not carried as fields. "Calendar Date" is the key field: the relationships need its column, mapped as `"xe".""."SAMPLESALES"."SAMP_TIME_DAY_D"."CALENDAR_DATE"` (type DATE, nullable not stated) [kb:sampleapp::bmm::SAMPLESALES.Dim Time.src.LTS Time Day#map5]; see Relationships. The drill path "Time Hierarchy" may need the others; see Hierarchies.

#### Measures

None: the table holds no measure.

### Relationships

#### Between model tables

All seven business-model joins are inner joins. Each joins a dimension table, printed 0..1 or 1..1, to a fact table, printed 0..n or n..n. Whether these printed rows mean a plain one-to-many join, and whether fact rows with no matching dimension row must stay out as the inner join keeps them out, is decision Q19; the relationships below assume one to many until it is answered.

| From model table | Rows (as printed) | To model table | Rows (as printed) | Join type | Evidence |
|---|---|---|---|---|---|
| Dim Time | 0..1 | Fact Target | 0..n | Inner | [kb:sampleapp::bmm::joins.Relationship_2004:11061353928600] |
| Dim Time | 0..1 | Fact Revenue | 0..n | Inner | [kb:sampleapp::bmm::joins.Relationship_2004:11091353928666] |
| Dim Sales Rep | 1..1 | Fact Revenue | 0..n | Inner | [kb:sampleapp::bmm::joins.Relationship_2004:17511354089145] |
| Fact Revenue | n..n | Dim Offices | 0..1 | Inner | [kb:sampleapp::bmm::joins.Relationship_2004:2111353851711] |
| Fact Revenue | n..n | Dim Products | 0..1 | Inner | [kb:sampleapp::bmm::joins.Relationship_2004:2141353851711] |
| Fact Target | n..n | Dim Offices | 0..1 | Inner | [kb:sampleapp::bmm::joins.Relationship_2004:2301353851711] |
| Fact Target | n..n | Dim Products | 0..1 | Inner | [kb:sampleapp::bmm::joins.Relationship_2004:2331353851711] |

The business-model joins name no columns; the columns below come from the foreign keys in the next table and from the sections of the model tables.

Proposal (not from OBIEE; to be signed): Dim Offices to Fact Revenue, one to many, on Dim Offices "Office Key" = Fact Revenue "Office Key" (SAMP_REVENUE_F.OFFICE_KEY), single filter direction from Dim Offices to Fact Revenue.

Proposal (not from OBIEE; to be signed): Dim Products to Fact Revenue, one to many, on Dim Products "Prod Key" = Fact Revenue "Prod Key" (SAMP_REVENUE_F.PROD_KEY), single filter direction from Dim Products to Fact Revenue.

Proposal (not from OBIEE; to be signed): Dim Offices to Fact Target, one to many, on Dim Offices "Office Key" = Fact Target "Office Key" (SAMP_TARGETS_F.OFFICE_KEY), single filter direction from Dim Offices to Fact Target.

Proposal (not from OBIEE; to be signed): Dim Products to Fact Target, one to many, on Dim Products "Prod Key" = Fact Target "Prod Key" (SAMP_TARGETS_F.PROD_KEY), single filter direction from Dim Products to Fact Target.

Proposal (not from OBIEE; to be signed): Dim Time to Fact Revenue, one to many, on Dim Time "Calendar Date" = Fact Revenue "Bill Day Dt" (SAMP_REVENUE_F.BILL_DAY_DT), single filter direction from Dim Time to Fact Revenue. The FSD asks for the calendar year of the order (item F1), while OBIEE relates revenue to time on the bill date: decision Q15.

Proposal (not from OBIEE; to be signed): Dim Sales Rep to Fact Revenue, one to many, on Dim Sales Rep "Employee ID" = Fact Revenue "Sales Employee Key" (SAMP_REVENUE_F.EMPLOYEE_KEY), single filter direction from Dim Sales Rep to Fact Revenue. In the database the foreign key goes to EMP_PARENT_CHILD.MEMBER_KEY, not to EMPLOYEE.EMP_ID, so this proposal holds only once decisions Q7 and Q10 are answered.

Dim Time to Fact Target: no relationship is proposed. Targets carry only a month key, BILL_MTH_KEY, which the foreign key relates to SAMP_TIME_MTH_D.MTH_KEY; the model table Dim Time has the day as its key. This is decision Q14.

#### Between new tables (proposed foreign keys)

| Kind | Name | From new table.column | To new table.column | Evidence |
|---|---|---|---|---|
| foreign key | EMP_PARENT_CHILD_FKey | EMP_PARENT_CHILD.ANCESTOR_KEY | EMPLOYEE.EMP_ID | [kb:sampleapp::phys::xe.SAMPLESALES.EMP_PARENT_CHILD.EMP_PARENT_CHILD_FKey] |
| foreign key | SAMP_PRODUCTS_D_TRANSLATION_FKey | SAMP_PRODUCTS_D_TRANSLATION.PROD_KEY | SAMP_PRODUCTS_D.PROD_KEY | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_PRODUCTS_D_TRANSLATION.SAMP_PRODUCTS_D_TRANSLATION_FKey] |
| foreign key | SAMP_REVENUE_A_FKey | SAMP_REVENUE_A.BILL_MTH_KEY | SAMP_TIME_MTH_D.MTH_KEY | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A.SAMP_REVENUE_A_FKey] |
| foreign key | SAMP_REVENUE_A_FKey#1 | SAMP_REVENUE_A.PROD_KEY | SAMP_PRODUCTS_D.PROD_KEY | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A.SAMP_REVENUE_A_FKey#1] |
| foreign key | SAMP_REVENUE_A_MTH_Foreign Key | SAMP_REVENUE_A_MTH.BILL_MTH_KEY | SAMP_TIME_MTH_D.MTH_KEY | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A_MTH.SAMP_REVENUE_A_MTH_Foreign Key] |
| foreign key | SAMP_REVENUE_A_YR_Foreign Key | SAMP_REVENUE_A_YR.BILL_YEAR_KEY | SAMP_TIME_MTH_D.CAL_YEAR | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A_YR.SAMP_REVENUE_A_YR_Foreign Key] |
| foreign key | SAMP_REVENUE_F_FKey | SAMP_REVENUE_F.BILL_MTH_KEY | SAMP_TIME_MTH_D.MTH_KEY | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F.SAMP_REVENUE_F_FKey] |
| foreign key | SAMP_REVENUE_F_FKey#1 | SAMP_REVENUE_F.OFFICE_KEY | SAMP_OFFICES_D.OFFICE_KEY | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F.SAMP_REVENUE_F_FKey#1] |
| foreign key | SAMP_REVENUE_F_FKey#2 | SAMP_REVENUE_F.PROD_KEY | SAMP_PRODUCTS_D.PROD_KEY | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F.SAMP_REVENUE_F_FKey#2] |
| foreign key | SAMP_REVENUE_F_FKey#3 | SAMP_REVENUE_F.BILL_DAY_DT | SAMP_TIME_DAY_D.CALENDAR_DATE | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F.SAMP_REVENUE_F_FKey#3] |
| foreign key | SAMP_REVENUE_F_FKey#4 | SAMP_REVENUE_F.EMPLOYEE_KEY | EMP_PARENT_CHILD.MEMBER_KEY | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F.SAMP_REVENUE_F_FKey#4] |
| foreign key | SAMP_TARGETS_F_FKey | SAMP_TARGETS_F.BILL_MTH_KEY | SAMP_TIME_MTH_D.MTH_KEY | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TARGETS_F.SAMP_TARGETS_F_FKey] |
| foreign key | SAMP_TARGETS_F_FKey#1 | SAMP_TARGETS_F.OFFICE_KEY | SAMP_OFFICES_D.OFFICE_KEY | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TARGETS_F.SAMP_TARGETS_F_FKey#1] |
| foreign key | SAMP_TARGETS_F_FKey#2 | SAMP_TARGETS_F.PROD_KEY | SAMP_PRODUCTS_D.PROD_KEY | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TARGETS_F.SAMP_TARGETS_F_FKey#2] |

Proposal (not from OBIEE; to be signed): each of these 14 foreign keys is kept as a foreign key between the new tables. In SAMP_REVENUE_F_FKey#4 the two columns differ in type as recorded: EMPLOYEE_KEY is INT and MEMBER_KEY is DOUBLE, precision 15 [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F.SAMP_REVENUE_F_FKey#4]; in SAMP_REVENUE_A_MTH_Foreign Key BILL_MTH_KEY has precision 22 and MTH_KEY precision 15 [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A_MTH.SAMP_REVENUE_A_MTH_Foreign Key].

#### Complex joins with a condition

| Kind | Name | From new table | To new table | Cardinality as printed | Condition, exactly | Evidence |
|---|---|---|---|---|---|---|
| complex join | Complex Join | FactCrossJoin | SAMP_OFFICES_D | `ONE TO ONE` | `1=1` | [kb:sampleapp::phys::joins.Complex Join] |
| complex join | Complex Join#1 | FactCrossJoin | SAMP_PRODUCTS_D | `ONE TO ONE` | `1=1` | [kb:sampleapp::phys::joins.Complex Join#1] |
| complex join | Complex Join#2 | FactCrossJoin | EMPLOYEE | `ONE TO ONE` | `1=1` | [kb:sampleapp::phys::joins.Complex Join#2] |

A condition of 1=1 pairs every row with every row and has no relationship form in a model: decision Q16. No item of the tab uses FactCrossJoin (decision Q11).

### Hierarchies

Four drill paths are in the slice, and all four are offered to report authors. The tab's confirmed fields sit on three of them: "Office Dsc" (Office Hierarchy), "Brand" and "Prod Dsc" (Products Hierararchy), "Per Name Year" and "Per Name Month" (Time Hierarchy); "Employee Name" shows the fourth. A hierarchy of the same levels needs fields of the model tables that the tab does not confirm (for example "Company" or "Per Name Qtr"); they are carried only if the hierarchy is signed.

#### Office Hierarchy

Dimension "Office Hierarchy": 5 levels; marked as a time dimension: not recorded; default root level "Dim Offices Total". [kb:sampleapp::bmm::SAMPLESALES.Office Hierarchy] Offered to report authors as hierarchy "Office Hierarchy" in presentation table "Dim Offices", 5 levels. [kb:sampleapp::pres::Sample Sales.Dim Offices.Office Hierarchy]

| Level | Key field | Keys | Evidence |
|---|---|---|---|
| 0 Dim Offices Total (grand total) | none recorded | none | [kb:sampleapp::bmm::SAMPLESALES.Office Hierarchy.Dim Offices Total] |
| 1 Company | "Dim Offices"."Company" | "Company Key" (primary key of the level true); "Company" (used for drill-down true) | [kb:sampleapp::bmm::SAMPLESALES.Office Hierarchy.Company] [kb:sampleapp::bmm::SAMPLESALES.Office Hierarchy.Company.key.Company Key] [kb:sampleapp::bmm::SAMPLESALES.Office Hierarchy.Company.key.Company] |
| 2 Organization | "Dim Offices"."Organization" | "Org Key" (primary key of the level true); "Organization" (used for drill-down true) | [kb:sampleapp::bmm::SAMPLESALES.Office Hierarchy.Organization] [kb:sampleapp::bmm::SAMPLESALES.Office Hierarchy.Organization.key.Org Key] [kb:sampleapp::bmm::SAMPLESALES.Office Hierarchy.Organization.key.Organization] |
| 3 Department | "Dim Offices"."Department" | "Dept Key" (primary key of the level true); "Department" (used for drill-down true) | [kb:sampleapp::bmm::SAMPLESALES.Office Hierarchy.Department] [kb:sampleapp::bmm::SAMPLESALES.Office Hierarchy.Department.key.Dept Key] [kb:sampleapp::bmm::SAMPLESALES.Office Hierarchy.Department.key.Department] |
| 4 Office Detail | "Dim Offices"."Office Dsc" | "Office Key" (primary key of the level true); "Office Dsc" (used for drill-down true) | [kb:sampleapp::bmm::SAMPLESALES.Office Hierarchy.Office Detail] [kb:sampleapp::bmm::SAMPLESALES.Office Hierarchy.Office Detail.key.Office Key] [kb:sampleapp::bmm::SAMPLESALES.Office Hierarchy.Office Detail.key.Office Dsc] |

Every level records a member count of 1.

Proposal (not from OBIEE; to be signed): a hierarchy Office Hierarchy in the model table Dim Offices with the levels Company, Organization, Department and Office Dsc, in that order.

#### Products Hierararchy

Dimension "Products Hierararchy" (spelled so in the repository): 5 levels; marked as a time dimension: not recorded; default root level "Dim Products Total". [kb:sampleapp::bmm::SAMPLESALES.Products Hierararchy] Offered to report authors as hierarchy "Products Hierararchy" in presentation table "Dim Products", 5 levels. [kb:sampleapp::pres::Sample Sales.Dim Products.Products Hierararchy]

| Level | Key field | Keys | Evidence |
|---|---|---|---|
| 0 Dim Products Total (grand total) | none recorded | none | [kb:sampleapp::bmm::SAMPLESALES.Products Hierararchy.Dim Products Total] |
| 1 Brand | "Dim Products"."Brand" | "Brand" (primary key of the level true; used for drill-down true) | [kb:sampleapp::bmm::SAMPLESALES.Products Hierararchy.Brand] [kb:sampleapp::bmm::SAMPLESALES.Products Hierararchy.Brand.key.Brand] |
| 2 Line Of Business | "Dim Products"."Lob" | "Lob" (primary key of the level true; used for drill-down true) | [kb:sampleapp::bmm::SAMPLESALES.Products Hierararchy.Line Of Business] [kb:sampleapp::bmm::SAMPLESALES.Products Hierararchy.Line Of Business.key.Lob] |
| 3 Products Type | "Dim Products"."Type" | "Type" (primary key of the level true; used for drill-down true) | [kb:sampleapp::bmm::SAMPLESALES.Products Hierararchy.Products Type] [kb:sampleapp::bmm::SAMPLESALES.Products Hierararchy.Products Type.key.Type] |
| 4 Product Detail | "Dim Products"."Prod Dsc" | "Dim Products Detail_Key" on "Prod Dsc" (primary key of the level true); "Dim Products Detail_Key1" on "Prod Key" | [kb:sampleapp::bmm::SAMPLESALES.Products Hierararchy.Product Detail] [kb:sampleapp::bmm::SAMPLESALES.Products Hierararchy.Product Detail.key.Dim Products Detail_Key] [kb:sampleapp::bmm::SAMPLESALES.Products Hierararchy.Product Detail.key.Dim Products Detail_Key1] |

Every level records a member count of 1.

Proposal (not from OBIEE; to be signed): a hierarchy Products Hierarchy in the model table Dim Products with the levels Brand, Lob, Type and Prod Dsc, in that order.

#### Time Hierarchy

Dimension "Time Hierarchy": 7 levels; marked as a time dimension: true; default root level "Dim Time Total". [kb:sampleapp::bmm::SAMPLESALES.Time Hierarchy] Offered to report authors as hierarchy "Time Hierarchy" in presentation table "Dim Time", 7 levels. [kb:sampleapp::pres::Sample Sales.Dim Time.Time Hierarchy]

| Level | Key field | Keys | Evidence |
|---|---|---|---|
| 0 Dim Time Total (grand total) | none recorded | none | [kb:sampleapp::bmm::SAMPLESALES.Time Hierarchy.Dim Time Total] |
| 1 Year | "Dim Time"."Per Name Year" | "Per Name Year" (primary key of the level true; chronological key true); member count as recorded 2 | [kb:sampleapp::bmm::SAMPLESALES.Time Hierarchy.Year] [kb:sampleapp::bmm::SAMPLESALES.Time Hierarchy.Year.key.Per Name Year] |
| 2 Half Year | "Dim Time"."Per Name Half" | "Per Name Half" (primary, chronological); member count as recorded 180 | [kb:sampleapp::bmm::SAMPLESALES.Time Hierarchy.Half Year] [kb:sampleapp::bmm::SAMPLESALES.Time Hierarchy.Half Year.key.Per Name Half] |
| 3 Quarter | "Dim Time"."Per Name Qtr" | "Per Name Qtr" (primary, chronological); member count as recorded 270 | [kb:sampleapp::bmm::SAMPLESALES.Time Hierarchy.Quarter] [kb:sampleapp::bmm::SAMPLESALES.Time Hierarchy.Quarter.key.Per Name Qtr] |
| 4 Month | "Dim Time"."Per Name Month" | "Per Name Month" (primary, chronological); member count as recorded 322 | [kb:sampleapp::bmm::SAMPLESALES.Time Hierarchy.Month] [kb:sampleapp::bmm::SAMPLESALES.Time Hierarchy.Month.key.Per Name Month] |
| 5 Week | "Dim Time"."Per Name Week" | "Per Name Week" (primary, chronological); member count as recorded 350 | [kb:sampleapp::bmm::SAMPLESALES.Time Hierarchy.Week] [kb:sampleapp::bmm::SAMPLESALES.Time Hierarchy.Week.key.Per Name Week] |
| 6 Dim Time Detail | "Dim Time"."Calendar Date" | "Dim Time Detail_Key" (primary, chronological); member count as recorded 365 | [kb:sampleapp::bmm::SAMPLESALES.Time Hierarchy.Dim Time Detail] [kb:sampleapp::bmm::SAMPLESALES.Time Hierarchy.Dim Time Detail.key.Dim Time Detail_Key] |

The member counts are copied as recorded; whether they are current is as-is document, question Q11.

Proposal (not from OBIEE; to be signed): a hierarchy Time Hierarchy in the model table Dim Time with the levels Per Name Year, Per Name Half, Per Name Qtr, Per Name Month, Per Name Week and Calendar Date, in that order. Proposal (not from OBIEE; to be signed): Dim Time is marked as the model's date table on Calendar Date, as OBIEE marks the dimension as a time dimension. Week sits below Month in OBIEE; whether weeks are cut at month ends is not recorded.

#### Sales Rep Hierarchy

Dimension "Sales Rep Hierarchy": 2 levels; parent-child (value based: the depth is not fixed); marked as a time dimension: not recorded; default root level "Total". [kb:sampleapp::bmm::SAMPLESALES.Sales Rep Hierarchy] Offered to report authors as hierarchy "Sales Rep Hierarchy" in presentation table "Dim Sales Rep"; 0 levels; shown by column "Employee Name". [kb:sampleapp::pres::Sample Sales.Dim Sales Rep.Sales Rep Hierarchy]

| Level | Key field | Keys | Evidence |
|---|---|---|---|
| 0 Total (grand total) | none recorded | none | [kb:sampleapp::bmm::SAMPLESALES.Sales Rep Hierarchy.Total] |
| 1 Detail | "Dim Sales Rep"."Employee ID" | "EMP_ID" (primary key of the level true; used for drill-down true) | [kb:sampleapp::bmm::SAMPLESALES.Sales Rep Hierarchy.Detail] [kb:sampleapp::bmm::SAMPLESALES.Sales Rep Hierarchy.Detail.key.EMP_ID] |

The parent-child relationships are held in EMP_PARENT_CHILD (member MEMBER_KEY, ancestor ANCESTOR_KEY, DISTANCE, IS_LEAF), as stated in the sections Model table Dim Sales Rep and New table EMP_PARENT_CHILD. A parent-child drill path is decision Q17: no hierarchy is proposed until it is taken.

### Values set per user or for the whole system

The slice's formulas, shown names and queries use 10 variable names inside VALUEOF; the repository defines 1 of them, and 9 are used by name and not defined in the repository.

#### Defined in the repository

Variable "PREFERRED_CURRENCY": session variable (a value per user, set at login); security mask `2`. [kb:sampleapp::var::PREFERRED_CURRENCY] Its default value is `'gc1'`. It is set by the init block "initBlockPrefCurrency": session true; enabled not recorded; refresh period not recorded. [kb:sampleapp::var::init.initBlockPrefCurrency] The init block's query, exactly:

```
select 'VALUEOF(NQ_SESSION.PREFERED_CURRENCY)' from dual
```

[kb:sampleapp::var::init.initBlockPrefCurrency]

The query names `PREFERED_CURRENCY`, with one R, which the repository does not define (below). The confirmed field "Preferred Currency" (item N1-currency) depends on this variable: it is decision Q2, already raised in the section Model table Fact Revenue. A Power BI model has no session variables; no model object is proposed for the variable until Q2 is answered.

#### Used by name, not defined in the repository

| Name | Where it is used | Bears on the tab |
|---|---|---|
| `CN_Sample_Sales_Dim_Time` | shown name of presentation table "Dim Time" | yes: the table holds the confirmed fields; decision Q9 |
| `CN_Sample_Sales_Dim_Time_Calendar_Date` | shown name of "Dim Time"."Calendar Date" | no: field not confirmed |
| `CN_Sample_Sales_Dim_Time_Per_Name_Half` | shown name of "Dim Time"."Per Name Half" | no: field not confirmed |
| `CN_Sample_Sales_Dim_Time_Per_Name_Month` | shown name of "Dim Time"."Per Name Month" | yes: item V1-month; decision Q9 |
| `CN_Sample_Sales_Dim_Time_Per_Name_Qtr` | shown name of "Dim Time"."Per Name Qtr" | no: field not confirmed |
| `CN_Sample_Sales_Dim_Time_Per_Name_Week` | shown name of "Dim Time"."Per Name Week" | no: field not confirmed |
| `CN_Sample_Sales_Dim_Time_Per_Name_Year` | shown name of "Dim Time"."Per Name Year" | yes: item F1; decision Q9 |
| `PREFERED_CURRENCY` | the query of the init block "initBlockPrefCurrency" | yes, through "Preferred Currency"; decision Q2 |
| `WEBLANGUAGE` | formula of "Dim Products"."Prod Dsc (Translated)" | no: field not confirmed |

Such a name can be a session variable the server sets itself, or one set outside the repository; the repository does not say which. Where their values are set is not recorded (see also as-is document, questions Q1 and Q2).

### Security

#### Row filters

The packet records no row filter on any source of the slice. The FSD item S1-own-offices, that each manager sees only their own offices, is not confirmed and is listed under Unresolved; this design builds no row-level security for it.

#### Permissions on fields

All four permissions in the slice are on the confirmed field "Revenue" of "Fact Target", which becomes the measure Revenue Target.

| Permission | On | Privilege package | Held by | Evidence |
|---|---|---|---|---|
| `write` | "Fact Target"."Revenue" | PrivPack_4209:18101355661677 | the role `BI Author Role` | [kb:sampleapp::sec::obj.ObjPriv_1500:18131355661677.o0] |
| `read` | "Fact Target"."Revenue" | PrivPack_4209:18141355661677 | no role is recorded in the slice for that privilege package | [kb:sampleapp::sec::obj.ObjPriv_1500:18161355661677.o0] |
| `read` | "Fact Target"."Revenue" | PrivPack_4209:18181355661677 | no role is recorded in the slice for that privilege package | [kb:sampleapp::sec::obj.ObjPriv_1500:18201355661677.o0] |
| `write` | "Fact Target"."Revenue" | PrivPack_4209:18181355661677 | no role is recorded in the slice for that privilege package | [kb:sampleapp::sec::obj.ObjPriv_1500:18211355661677.o0] |

#### Roles

Role with display name `BI Author Role`; guid `BIAuthor`. It holds query privilege `QueryPriv_4204:18331355753308` of privilege package `PrivPack_4209:18101355661677` (kind query_privilege). [kb:sampleapp::sec::pkg.PrivPack_4209:18101355661677.q0]

The "write" permissions belong with decision Q6 (whether targets are typed in). Who may read the target figures is decision Q18. No model role is proposed until it is taken.

### Report-level calculations

No confirmed column of the tab is a report column, so no report is in the slice. No report-level calculation is designed.

## Fields on the tab

Every item of the FSD that needs data, once: here with what supplies it, or under Unresolved.

### The tab's fields (part 1 of 2)

One row per confirmed item of the FSD, in the FSD's order. The model items named under "Supplied by" are proposals; they are designed in the sections of the model tables and in the FSD measures below. Items that are not confirmed are listed under Unresolved.

| Item | FSD anchor | FSD text | Supplied by | Confirmed column(s) | Evidence |
|---|---|---|---|---|---|
| F1 (filter) | t1.r2.c2 t1.r2.c3 | `Year: Calendar year of the order` | Model table Dim Time, field Per Name Year | "Dim Time"."Per Name Year" | [kb:item::F1] |
| F2 (filter) | t1.r3.c2 t1.r3.c3 | `Office: The sales office` | Model table Dim Offices, field Office Dsc | "Dim Offices"."Office Dsc" | [kb:item::F2] |
| F3 (filter) | t1.r4.c2 t1.r4.c3 | `Product brand: Brand of the product sold` | Model table Dim Products, field Brand | "Dim Products"."Brand" | [kb:item::F3] |
| K1 (calculation) | t2.r2.c2 t2.r2.c3 | `Net revenue: Revenue after discounts, for the filtered period` | FSD measure Net revenue | "Fact Revenue"."Revenue", "Fact Revenue"."Discnt Value" | [kb:item::K1] |
| K2 (measure) | t2.r3.c2 t2.r3.c3 | `Units sold: Number of units sold` | Measure Units | "Fact Revenue"."Units" | [kb:item::K2] |
| K3 (calculation) | t2.r4.c2 t2.r4.c3 | `Revenue vs target: Net revenue as a percentage of the revenue target for the same period` | FSD measure Revenue vs target | "Fact Revenue"."Revenue", "Fact Revenue"."Discnt Value", "Fact Target"."Revenue" | [kb:item::K3] |
| K4 (calculation) | t2.r5.c2 t2.r5.c3 | `Average selling price: Net revenue divided by units sold` | FSD measure Average selling price | "Fact Revenue"."Revenue", "Fact Revenue"."Discnt Value", "Fact Revenue"."Units" | [kb:item::K4] |
| V1-month (attribute) | p10 | `month of the year` | Model table Dim Time, field Per Name Month | "Dim Time"."Per Name Month" | [kb:item::V1-month] |
| V1-net-revenue (calculation) | p10 | `net revenue` | FSD measure Net revenue | "Fact Revenue"."Revenue", "Fact Revenue"."Discnt Value" | [kb:item::V1-net-revenue] |
| V1-target (measure) | p10 | `revenue target` | Measure Revenue Target | "Fact Target"."Revenue" | [kb:item::V1-target] |
| V2-product (attribute) | p11 | `product` | Model table Dim Products, field Prod Dsc | "Dim Products"."Prod Dsc" | [kb:item::V2-product] |
| V2-office (attribute) | p11 | `office` | Model table Dim Offices, field Office Dsc | "Dim Offices"."Office Dsc" | [kb:item::V2-office] |
| V2-net-revenue (calculation) | p11 | `net revenue` | FSD measure Net revenue | "Fact Revenue"."Revenue", "Fact Revenue"."Discnt Value" | [kb:item::V2-net-revenue] |
| V2-units (measure) | p11 | `units sold` | Measure Units | "Fact Revenue"."Units" | [kb:item::V2-units] |
| V2-discount (measure) | p11 | `discount amount` | Measure Discount Value | "Fact Revenue"."Discnt Value" | [kb:item::V2-discount] |
| V3-rep (attribute) | p12 | `sales representatives` | Model table Dim Sales Rep, field Employee Name | "Dim Sales Rep"."Employee Name" | [kb:item::V3-rep] |
| V3-net-revenue (calculation) | p12 | `highest net revenue` | FSD measure Net revenue | "Fact Revenue"."Revenue", "Fact Revenue"."Discnt Value" | [kb:item::V3-net-revenue] |

Items S1-own-offices [kb:item::S1-own-offices], F4 [kb:item::F4] and K5 [kb:item::K5] are not confirmed and are listed under Unresolved.

The decision note of F1 reads `TEST DECISION by the lead build agent, not by Kamal: kept for the test although the review showed revenue joins on the bill date` [kb:item::F1]. The decision note of every other confirmed item of this part reads `TEST DECISION by the lead build agent, not by Kamal` [kb:item::F2] [kb:item::F3] [kb:item::K1] [kb:item::K2] [kb:item::K3] [kb:item::K4] [kb:item::V1-month] [kb:item::V1-net-revenue] [kb:item::V1-target] [kb:item::V2-product] [kb:item::V2-office] [kb:item::V2-net-revenue] [kb:item::V2-units] [kb:item::V2-discount] [kb:item::V3-rep] [kb:item::V3-net-revenue].

Proposal (not from OBIEE; to be signed): for V3-net-revenue, the FSD measure Net revenue is used and the ranking by highest value is left to the visual.

#### FSD measure: Net revenue

- Supplies items K1, V1-net-revenue, V2-net-revenue and V3-net-revenue.
- The FSD's definition: `Net revenue: Revenue after discounts, for the filtered period` [kb:item::K1]
- Built from the confirmed columns "Fact Revenue"."Revenue" and "Fact Revenue"."Discnt Value", through the measures Revenue and Discount Value (section Model table Fact Revenue).
- The combination is the FSD's definition, not something OBIEE records. The FSD says "after discounts"; whether the OBIEE measure "Revenue" already has the discount taken off is not recorded (question Q1).
- Proposal (not from OBIEE; to be signed):

      Net revenue = [Revenue] - [Discount Value]

#### FSD measure: Revenue vs target

- Supplies item K3.
- The FSD's definition: `Revenue vs target: Net revenue as a percentage of the revenue target for the same period` [kb:item::K3]
- Built from the confirmed columns "Fact Revenue"."Revenue", "Fact Revenue"."Discnt Value" and "Fact Target"."Revenue", through the FSD measure Net revenue and the measure Revenue Target (section Model table Fact Target).
- The combination is the FSD's definition, not something OBIEE records. "For the same period" holds only if both fact tables are filtered by the same time field; at which level of time the targets are held is stated in the section Model table Fact Target.
- Proposal (not from OBIEE; to be signed), shown as a percentage:

      Revenue vs target = DIVIDE([Net revenue], [Revenue Target])

#### FSD measure: Average selling price

- Supplies item K4.
- The FSD's definition: `Average selling price: Net revenue divided by units sold` [kb:item::K4]
- Built from the confirmed columns "Fact Revenue"."Revenue", "Fact Revenue"."Discnt Value" and "Fact Revenue"."Units", through the FSD measure Net revenue and the measure Units.
- The combination is the FSD's definition, not something OBIEE records.
- Proposal (not from OBIEE; to be signed):

      Average selling price = DIVIDE([Net revenue], [Units])

### The tab's fields (part 2 of 2)

| Item | FSD anchor | FSD text | Supplied by | Confirmed column(s) | Evidence |
|---|---|---|---|---|---|
| N1-currency (attribute) | p14 | `Amounts are shown in the user's preferred currency where the source supports it.` | Model table Fact Revenue, field Preferred Currency (its design waits for a decision; see the section Model table Fact Revenue) | "Fact Revenue"."Preferred Currency" | [kb:item::N1-currency] |

The decision note of N1-currency reads `TEST DECISION by the lead build agent, not by Kamal` [kb:item::N1-currency].

## Unresolved

Items of the FSD that no confirmed column supplies. The design does not cover them.

| Item | FSD anchor | FSD text | Status | Decision note | Evidence |
|---|---|---|---|---|---|
| S1-own-offices (filter) | p6 | `Each manager should see only their own offices; the Head of Sales sees everything.` | not confirmed (rejected) | `TEST DECISION by the lead build agent, not by Kamal: the manager-to-office link is unknown (Q2)` (trace question) | [kb:item::S1-own-offices] |
| F4 (filter) | t1.r5.c2 t1.r5.c3 | `Region: Sales region the office belongs to` | not confirmed (unmatched) | none recorded | [kb:item::F4] |
| K5 (measure) | t2.r6.c2 t2.r6.c3 | `Active customers: Number of distinct customers who placed at least one order in the period` | not confirmed (unmatched) | none recorded | [kb:item::K5] |

## Decisions needed

Each row is a place where OBIEE does something Power BI does not do the same way, or where the design has two reasonable shapes. Nothing here has been chosen: each decision is taken by answering its open question.

| What OBIEE does | Where | Options | Like for like would be | Question |
|---|---|---|---|---|
| "Preferred Currency" switches between "Revenue", "Revenue (EUR)" and "Revenue (GBP)" by the per-user value PREFERRED_CURRENCY [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Preferred Currency] | Model table Fact Revenue | (a) a slicer the user sets; (b) a per-user setting in a table applied by a security role; (c) one fixed choice for everyone | (b) | Q2 |
| "Fact Revenue" is read from four tables at different levels of detail, OBIEE choosing per query [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue] | Model table Fact Revenue | (a) detail table only; (b) summary tables as aggregations of the detail table; (c) all kept, chosen per visual | (b) | Q3 |
| "Revenue (EUR)" and "Revenue (GBP)" are plain revenue in one source and revenue times a factor in another [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.SAMP_REVENUE_F#map15] | Model table Fact Revenue | (a) the detail mapping; (b) the summary mapping; (c) both as separate measures | (c) | Q4 |
| Fixed currency factors 0.76 and 0.62 in the mapping [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.SAMP_REVENUE_F#map14] | Model table Fact Revenue | (a) keep the fixed numbers; (b) read them from a rate table | (a) | Q5 |
| The target "Revenue" of "Fact Target" is writeable [kb:sampleapp::bmm::SAMPLESALES.Fact Target.Revenue] | Model table Fact Target | (a) read-only, loaded by a process; (b) a separate entry form writes to SAMP_TARGETS_F; (c) write-back no longer needed | (b) | Q6 |
| "Dim Sales Rep" is read from EMPLOYEE joined to EMP_PARENT_CHILD inside one source [kb:sampleapp::bmm::SAMPLESALES.Dim Sales Rep.src.LTS Employee#table0.join0] | Model table Dim Sales Rep | (a) a view joining them the same way; (b) separate tables related in the model; (c) EMPLOYEE alone | (a) | Q7 |
| "Dim Time" is read from a day table and a month table [kb:sampleapp::bmm::SAMPLESALES.Dim Time.src.LTS Time Month] | Model table Dim Time | (a) day table only; (b) month table kept as well; (c) both, chosen per visual | (b) | Q8 |
| The names users see for "Per Name Year" and "Per Name Month" are set by variables [kb:sampleapp::pres::Sample Sales.Dim Time.Per Name Year] | Model table Dim Time | (a) fixed names; (b) the model's translations | (b) if names differ per language, otherwise (a) | Q9 |
| FactCrossJoin is a query kept in the repository [kb:sampleapp::phys::xe.SAMPLESALES.FactCrossJoin] | New view FactCrossJoin | (a) a view with the same SQL; (b) a calculated table in the model; (c) not built for this tab | (a) | Q11 |
| Targets meet time only through a month key, while Dim Time is keyed on the day [kb:sampleapp::bmm::joins.Relationship_2004:11061353928600] | Relationships | (a) time at month level, or with a month key, for both facts; (b) a separate month table for targets; (c) targets spread to days | (a) | Q14 |
| Revenue meets time on the bill date; the FSD asks for the year of the order [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F.SAMP_REVENUE_F_FKey#3] | Relationships | (a) bill date; (b) order date; (c) both, the order date as an inactive relationship | (a) | Q15 |
| Three complex joins from FactCrossJoin with the condition 1=1 [kb:sampleapp::phys::joins.Complex Join] | Relationships | (a) a relationship with the nearest cardinality; (b) no relationship, done in a view or measure; (c) dropped with FactCrossJoin | (b) | Q16 |
| The business-model joins are inner joins printed 0..n or n..n on the fact side and 0..1 or 1..1 on the dimension side [kb:sampleapp::bmm::joins.Relationship_2004:2111353851711] | Relationships | (a) one to many, fact rows with no matching dimension row shown under a blank member; (b) one to many, those rows left out in the load or a view; (c) another cardinality for the joins printed n..n | (b) | Q19 |
| "Sales Rep Hierarchy" is a parent-child drill path of no fixed depth [kb:sampleapp::bmm::SAMPLESALES.Sales Rep Hierarchy] | Hierarchies | (a) flatten to fixed level columns; (b) parent and child fields, path computed in the model; (c) no drill path | (a) or (b) | Q17 |
| "read" and "write" on the target "Revenue" are given through privilege packages, two with no role recorded [kb:sampleapp::sec::obj.ObjPriv_1500:18161355661677.o0] | Security | (a) the same restrictions as model roles; (b) no restriction below the report | (a) | Q18 |

15 decisions in all. Besides them, four open questions ask for facts the export does not hold: Q1 (is Revenue before discount), Q10 (does MEMBER_KEY identify a row), Q12 (the keys of the revenue and target tables), Q13 (the keys of SAMP_TIME_MTH_D).

## What this design could not see

#### Errors

None: the extraction recorded no error about an object of the slice (from the errors table).

#### Source content that no table holds (from the unparsed table)

Totals, over the whole estate: all kinds together 95 rows of unparsed, 1769 occurrences in the source; kind unread_attribute 78 rows, 1674 occurrences; kind unread_element 15 rows, 91 occurrences; kind unread_text 2 rows, 4 occurrences. The occurrences count the whole estate, not only the subject areas this tab touches.

| Kind | Object | Rows | Occurrences | What was not read |
|---|---|---|---|---|
| unread_attribute | Dimension | 7 | 179 | LogicalLevel@isFDUC (x22); LogicalLevel@levelConst (x22); RefLogicalColumn@logicalColumnRef (x34); RefLogicalLevel@logicalLevelRef (x1) |
| unread_attribute | Group | 4 | 14 | Group@displayName (x5); Group@guid (x5); RefGroup@groupRef (x2) |
| unread_attribute | InitBlock | 5 | 9 | DBMapItem@name (x2); InitBlock@connectionPoolRef (x2); InitBlock@isRowWiseInit (x1); RefVariable@variableRef (x2) |
| unread_attribute | LogicalTable | 13 | 177 | LogicalColumn@isDerived (x2); MeasureDefn@isCommutative (x13); RefLogicalTableSource@logicalTableSourceRef (x17); RefObject@objectTypeId (x19) |
| unread_attribute | LogicalTableSource | 12 | 894 | RefObject@objectTypeId (x206) |
| unread_attribute | ObjectPrivilege | 3 | 22 | RefObject@objectTypeId (x8) |
| unread_attribute | PhysicalTable | 10 | 89 | PhysicalColumn@lastUpdated (x2); PhysicalColumn@rowCount (x2); PhysicalTable@cacheExpiry (x1); PhysicalTable@iconIndex (x4); PhysicalTable@isCacheable (x4); PhysicalTable@lastUpdated (x4); PhysicalTable@rowCount (x4) |
| unread_attribute | PresentationCatalog | 4 | 26 | PresentationCatalog@hasDispDescription (x3); PresentationCatalog@hasDispName (x3); RefPresentationTable@presentationTableRef (x10) |
| unread_attribute | PresentationHierarchy | 6 | 87 | PresentationHierarchy@hasDispDescription (x5); PresentationHierarchy@hasDispName (x5); PresentationLevel@containerRef (x20); PresentationLevel@hasDispDescription (x20); PresentationLevel@hasDispName (x20) |
| unread_attribute | PresentationTable | 6 | 148 | PresentationColumn@hasDispDescription (x59); PresentationColumn@hasDispName (x59); PresentationTable@hasDispDescription (x10); PresentationTable@hasDispName (x10); RefPresentationHierarchy@presentationHierarchyRef (x5) |
| unread_attribute | PrivilegePackage | 2 | 4 | PrivilegePackage@userRef (x3); QueryPrivilegeMapping@databaseRef (x1) |
| unread_element | Dimension | 4 | 54 | RefLogicalColumn (x34); RefLogicalColumns (x18); RefLogicalLevel (x1); RefPreferredDrillDown (x1) |
| unread_element | Group | 3 | 6 | Description (x2); RefChildGroups (x2); RefGroup (x2) |
| unread_element | InitBlock | 4 | 8 | DBMapItem (x2); RefVariable (x2); RefVariables (x2); Value (x2) |
| unread_element | PresentationCatalog | 2 | 13 | RefPresentationTable (x10); RefTables (x3) |
| unread_element | PresentationTable | 2 | 10 | RefHierarchies (x5); RefPresentationHierarchy (x5) |
| unread_text | Group | 1 | 2 | Description#text (x2) |
| unread_text | InitBlock | 1 | 2 | Value#text (x2) |

The rows that are only internal identifiers (@mdsid, @name and @refId of elements whose content is read) are counted in the totals above and are not content that was missed.

No volumes are stated for the new tables (see the Missing list of each). Who holds the privilege packages for which the slice records no role is decision Q18. What the unread attributes above hold is not stated in the export.

#### Left out of the slice (from the manifest)

- Rows of the Knowledge Base's errors table that are not about an object of the slice: 0.
- Whole object types with no reader, kind unmodelled_type: 3 object types, 15 rows, 15 objects: `LogicalSourceFolder` 11 rows, 11 objects; `QueryPrivilege` 1 row, 1 object; `User` 3 rows, 3 objects.
- Rows that are in the slice only so that a reference from another row resolves: 9.

## Open questions

Each says how to find the answer and ends with an empty answer line. Write the answer on that line, or tell the agent.

### Q1. Does the OBIEE measure "Revenue" of "Fact Revenue" hold revenue before the discount is taken off?
Raised in: tab-fields-p1
Why it matters: the FSD defines Net revenue as "Revenue after discounts" (item K1) and the Operator confirmed for it both "Fact Revenue"."Revenue" and "Fact Revenue"."Discnt Value". The proposed Net revenue takes Discount Value off Revenue. If Revenue already has the discount taken off, the discount would be taken off twice.
Assumed meanwhile: the FSD measure Net revenue is proposed as Revenue minus Discount Value; nothing is agreed.
How to get the answer: run against the schema SAMPLESALES the query select sum(REVENUE), sum(DISCNT_VALUE), sum(UNITS) from SAMP_REVENUE_F, and compare it with a few order lines whose list price, discount and revenue are known from the source system; bring back whether REVENUE is before or after the discount, and confirm with the FSD's author that Net revenue means Revenue minus Discount Value.
Answer:

### Q2. "Preferred Currency" switches between three revenue measures by a value OBIEE sets per user. How should the new tab choose the currency?
Raised in: table-01-fact-revenue-p1
Why it matters: the field "Preferred Currency" of "Fact Revenue", confirmed for item N1-currency, is an INDEXCOL formula on VALUEOF(NQ_SESSION."PREFERRED_CURRENCY") that shows "Revenue" for 'gc1', "Revenue (EUR)" for 'gc2' and "Revenue (GBP)" for 'gc3'. A Power BI model has no session variables, so what a user sees cannot be carried over as it is. "Revenue (EUR)" and "Revenue (GBP)" are not confirmed for the tab, but the switch needs them.
Options: (a) a slicer on which the user picks the currency, and a measure that switches on it; (b) a per-user setting held in a table and applied by a security role; (c) one fixed choice for everyone (for example "Revenue" only). Like for like would be (b).
Assumed meanwhile: nothing. No definition is proposed for "Preferred Currency"; "Revenue (EUR)" and "Revenue (GBP)" are not designed.
How to get the answer: with the FSD's author, decide which option the tab needs (the FSD speaks of the user's preferred currency at p14). Bring back the option; for (b), where each user's currency is held today (the init block that sets PREFERRED_CURRENCY is named in the section Values set per user or for the whole system) and the values it can take. See also as-is document, question Q2.
Answer:

### Q3. "Fact Revenue" is read from four tables at different levels of detail. Which of them does the new model keep?
Raised in: table-01-fact-revenue-p2
Why it matters: "Revenue" and "Units" are mapped in four sources: "Aggregate Revenue" (month and product, priority group 1), "Aggregate Revenue By Month" (month), "Aggregate Revenue by Year" (year) and "SAMP_REVENUE_F" (sales rep, day, office and product); the priority group of the last three is not recorded. OBIEE picks a source per query; Power BI reads the tables it is given. "Discnt Value", needed for Net revenue, is mapped only in "Aggregate Revenue" and "SAMP_REVENUE_F". The tab filters and groups by office, product brand, product, sales representative, month and year.
Options: (a) keep only the detail table SAMP_REVENUE_F and let the model add up; (b) keep the summary tables as aggregations of the detail table; (c) keep all and choose per visual. Like for like would be (b).
Assumed meanwhile: the measures Revenue, Discount Value and Units are defined over the model table Fact Revenue; which new tables feed it is not chosen. The new tables of all four are described, like for like.
How to get the answer: run against the schema SAMPLESALES the queries select sum(REVENUE), sum(UNITS), sum(DISCNT_VALUE) from SAMP_REVENUE_F; the same from SAMP_REVENUE_A; the same from SAMP_REVENUE_A_MTH; and the same from SAMP_REVENUE_A_YR, and bring back the four rows of totals, to show whether detail and summary tables agree. Bring back also who loads the three summary tables and when. Then decide with Data Engineering and the Power BI builder which option applies. See also as-is document, question Q5.
Answer:

### Q4. "Revenue (EUR)" and "Revenue (GBP)" are the plain revenue in one source and revenue times a fixed number in another. Which mapping is meant?
Raised in: table-01-fact-revenue-p2
Why it matters: this matters only if decision Q2 keeps the currency switch "Preferred Currency". In the source "Aggregate Revenue" both measures are the column REVENUE of SAMP_REVENUE_A unchanged; in the source "SAMP_REVENUE_F" they are REVENUE times 0.76 and times 0.62. The number shown therefore depends on the source OBIEE picks, and there is no single definition to carry over.
Options: (a) the detail mapping, REVENUE times the factor, is the definition; (b) the summary mapping, REVENUE unchanged, is; (c) both exist as separate measures. Like for like would be (c).
Assumed meanwhile: nothing. No currency measure is designed.
How to get the answer: in "Sample Sales", run one analysis with "Revenue" and "Revenue (EUR)" by month and product, and one by day; bring back both results and the physical SQL OBIEE logs for each, which show the source used. Then decide with the FSD's author which mapping the tab is meant to show. See also as-is document, question Q4.
Answer:

### Q5. The currency factors 0.76 and 0.62 are fixed numbers in the mapping. Are they kept as they are, or read from a table?
Raised in: table-01-fact-revenue-p2
Why it matters: this matters only if decision Q2 keeps the currency switch and decision Q4 keeps the detail mapping. The source "SAMP_REVENUE_F" multiplies REVENUE by 0.76 for "Revenue (EUR)" and by 0.62 for "Revenue (GBP)". A like-for-like design would hard-code them again; whether they are still right, and for which dates, is not recorded.
Options: (a) keep the fixed numbers; (b) read them from a table of rates. Like for like would be (a).
Assumed meanwhile: nothing. No currency measure is designed.
How to get the answer: look in the old database schema SAMPLESALES, and in the documents of the load of SAMP_REVENUE_F, for a table or file of currency rates that holds 0.76 for EUR and 0.62 for GBP, and in the change history of the repository for when the mappings of "Revenue (EUR)" and "Revenue (GBP)" in the source "SAMP_REVENUE_F" were set; bring back the source of the numbers and the date they applied. Then ask the FSD's author whether the tab must show amounts at current rates (the FSD speaks of the preferred currency at p14), and bring back the answer.
Answer:

### Q6. Revenue targets can be typed in by users in OBIEE. Should the new tab allow that, and what loads the targets?
Raised in: table-02-fact-target
Why it matters: the measure "Revenue" of "Fact Target", confirmed for items K3 and V1-target, is recorded as writeable. A Power BI measure cannot be typed into. If users still enter targets, something outside the model has to take them and write them to the new table SAMP_TARGETS_F.
Options: (a) targets become read-only in the tab and are loaded into the new table SAMP_TARGETS_F by a process to be named; (b) a separate entry form writes to that table; (c) writing back is no longer needed. Like for like would be (b).
Assumed meanwhile: the measure Revenue Target is designed as read from the new table; no write-back is designed.
How to get the answer: find, in the FSD or with its author, whether the tab is meant to take target entries (the FSD speaks of the revenue target at t2.r4 and p10), and bring back which of the three options applies and, for (a), the name of the process that loads SAMP_TARGETS_F. See also as-is document, questions Q7 and Q12.
Answer:

### Q7. "Dim Sales Rep" is read from EMPLOYEE joined to the closure table EMP_PARENT_CHILD. What shape should the new model table take?
Raised in: table-06-dim-sales-rep
Why it matters: the source "LTS Employee" joins EMPLOYEE to EMP_PARENT_CHILD as an inner join, one to many, on EMP_PARENT_CHILD.ANCESTOR_KEY = EMPLOYEE.EMP_ID. The model table has to come from one object or from related tables, and as joined one employee may give several rows. The tab uses only "Employee Name" (item V3-rep), which comes from EMPLOYEE; no field the tab uses comes from EMP_PARENT_CHILD.
Options: (a) a new view that joins the two tables the same way; (b) separate new tables EMPLOYEE and EMP_PARENT_CHILD, related in the model; (c) the model table reads EMPLOYEE alone, one row per employee, and EMP_PARENT_CHILD is kept only for the drill path (its decision is under Hierarchies). Like for like would be (a).
Assumed meanwhile: nothing. "Employee Name" is designed as read from EMPLOYEE.EMP_NAME; the row shape is not chosen.
How to get the answer: run against the schema SAMPLESALES the queries select count(*) from EMPLOYEE and select count(*) from EMPLOYEE e join EMP_PARENT_CHILD p on p.ANCESTOR_KEY = e.EMP_ID, and bring back both counts. Then decide with the FSD's author whether "sales representatives" at p12 means one row per employee, or a rollup of each manager over the employees below them, and bring back the option. See also as-is document, question Q9.
Answer:

### Q8. "Dim Time" is read from a day table and a month table. Which of them does the new model keep?
Raised in: table-07-dim-time
Why it matters: "Dim Time" has two active sources, "LTS Time Day" at the day level (SAMP_TIME_DAY_D) and "LTS Time Month" at the month level (SAMP_TIME_MTH_D); both map "Per Name Year" and "Per Name Month", which the tab uses (items F1 and V1-month). OBIEE picks the source per query, for example the month table with a summary fact table at month level. Power BI reads the tables it is given. Revenue targets carry only a month key (section Model table Fact Target), so the choice also decides how targets meet the time table.
Options: (a) keep only the day table and let the model add up to month and year; (b) keep the month table as well, as the time table for facts held by month; (c) keep both and choose per visual. Like for like would be (b).
Assumed meanwhile: nothing. Both new tables are described; the confirmed fields are shown from both sources.
How to get the answer: run against the schema SAMPLESALES the query select count(distinct PER_NAME_MONTH), count(distinct PER_NAME_YEAR) from SAMP_TIME_DAY_D, and the same from SAMP_TIME_MTH_D, and bring back whether the months and years agree. Then decide with the Power BI builder together with decision Q3 (summary fact tables) and bring back the option.
Answer:

### Q9. The names users see for "Per Name Year" and "Per Name Month" are set by variables. What names should the new tab show?
Raised in: table-07-dim-time
Why it matters: the shown names of the two confirmed fields are written as VALUEOF(NQ_SESSION.CN_Sample_Sales_Dim_Time_Per_Name_Year) and VALUEOF(NQ_SESSION.CN_Sample_Sales_Dim_Time_Per_Name_Month). Names in a Power BI model are fixed text, or translated through the model's own translations. The value these variables hold at run time is not recorded.
Options: (a) fixed names; (b) the model's translations, one name per language. Like for like would be (b) if the names differ per language, otherwise (a).
Assumed meanwhile: the fields are called Per Name Year and Per Name Month in this design; nothing is agreed about the names users see.
How to get the answer: log in to OBIEE as a report author, open the subject area "Sample Sales" and bring back the names shown for the fields "Per Name Year" and "Per Name Month" of "Dim Time"; if users work in more than one language, bring back the names per language. See also as-is document, question Q1.
Answer:

### Q10. Does MEMBER_KEY identify one row of EMP_PARENT_CHILD, through which revenue reaches the sales representatives?
Raised in: dbtable-02-emp-parent-child
Why it matters: the repository records EMP_PARENT_CHILD as one row per member and ancestor, and declares one key, "EMP_PARENT_CHILD_Key", on MEMBER_KEY alone. The foreign key "SAMP_REVENUE_F_FKey#4" from SAMP_REVENUE_F (EMPLOYEE_KEY) points at MEMBER_KEY. The tab shows net revenue per sales representative (items V3-rep and V3-net-revenue). If a member appears on several rows, a one-to-many relationship on MEMBER_KEY cannot be built, and revenue could be counted more than once.
Assumed meanwhile: MEMBER_KEY is described as the key, as recorded; no uniqueness is stated.
How to get the answer: run against the schema SAMPLESALES the queries select count(*), count(distinct MEMBER_KEY) from EMP_PARENT_CHILD and select count(*) from EMP_PARENT_CHILD where DISTANCE = 0, and bring back the three numbers. See also as-is document, question Q14.
Answer:

### Q11. FactCrossJoin is a query kept in the OBIEE repository, and the tab does not use it. Is it built at all, and if so where?
Raised in: dbtable-03-factcrossjoin
Why it matters: FactCrossJoin is not a database table but the query select 1 Preserve from dual, kept in the repository. It has to exist somewhere if it is kept. Its only column feeds the measure "Preserve" of "Fact Revenue", which no item of the tab uses. Whether this SQL, which reads from dual, runs on the new platform is not recorded.
Options: (a) a view with the same SQL on the new platform; (b) a calculated table in the model; (c) it is not built for this tab. Like for like would be (a).
Assumed meanwhile: the view is described, like for like; nothing in the tab's model reads it.
How to get the answer: decide with Data Engineering and the Power BI builder whether any report still needs the measure "Preserve" (see also as-is document, question Q6), and bring back the option.
Answer:

### Q12. Which columns identify one row of the revenue and target tables the tab reads, for which the export records no key?
Raised in: dbtable-07-samp-revenue-a
Why it matters: the repository declares no key for SAMP_REVENUE_A, SAMP_REVENUE_A_MTH, SAMP_REVENUE_A_YR, the detail table SAMP_REVENUE_F or the targets table SAMP_TARGETS_F. A data engineer building the new table needs to know what one row stands for, to load it without duplicates and to check its totals against the old table. The tab's revenue figures (items K1 to K4, V1 to V3) may be read from it, depending on decision Q3.
Assumed meanwhile: nothing. No key is proposed; the new table is described column by column.
How to get the answer: run against the schema SAMPLESALES the queries select count(*) from SAMP_REVENUE_A and select count(*) from (select BILL_MTH_KEY, PROD_KEY from SAMP_REVENUE_A group by BILL_MTH_KEY, PROD_KEY), and bring back both counts; if they are equal, BILL_MTH_KEY and PROD_KEY identify a row. Do the same for SAMP_REVENUE_A_MTH with BILL_MTH_KEY alone, and for SAMP_REVENUE_A_YR with BILL_YEAR_KEY alone. For SAMP_REVENUE_F, bring back select count(*), count(distinct ORDER_NUMBER) from SAMP_REVENUE_F, and from the owner of the source system which columns identify an order line. For SAMP_TARGETS_F, bring back select count(*) from SAMP_TARGETS_F and select count(*) from (select BILL_MTH_KEY, OFFICE_KEY, PROD_KEY from SAMP_TARGETS_F group by BILL_MTH_KEY, OFFICE_KEY, PROD_KEY). Bring back also the key of the table in the database, if one is defined there.
Answer:

### Q13. Which of the two keys of SAMP_TIME_MTH_D is its primary key, and does CAL_YEAR identify one row?
Raised in: dbtable-13-samp-time-mth-d
Why it matters: the repository declares two keys for SAMP_TIME_MTH_D, one on MTH_KEY and one on CAL_YEAR, and does not say which is the primary key. The revenue and target tables reach it on MTH_KEY; the year summary SAMP_REVENUE_A_YR reaches it on CAL_YEAR. A table holding a month per row would hold each year on several rows; then a relationship on CAL_YEAR would match several rows and the yearly figures could be multiplied.
Assumed meanwhile: MTH_KEY is proposed as the key; nothing is assumed about CAL_YEAR.
How to get the answer: run against the schema SAMPLESALES the query select count(*), count(distinct MTH_KEY), count(distinct CAL_YEAR) from SAMP_TIME_MTH_D and bring back the three numbers. See also as-is document, question Q10.
Answer:

### Q14. Revenue targets carry only a month key. How should they meet the time table, so that Revenue vs target compares the same period?
Raised in: joins
Why it matters: OBIEE joins "Dim Time" to "Fact Target" (inner, 0..1 to 0..n). The target table SAMP_TARGETS_F holds only BILL_MTH_KEY, whose foreign key points at SAMP_TIME_MTH_D.MTH_KEY, while the model table Dim Time is keyed on the day (Calendar Date). Item K3 asks for net revenue as a percentage of the target for the same period, and item V1-target shows the target by month.
Options: (a) the model's time table is at month level, or carries a month key, and both facts relate to it on the month; (b) a separate month table relates to Fact Target, and the year and month fields are shared through it; (c) targets are spread to days in the new table SAMP_TARGETS_F. Like for like would be (a).
Assumed meanwhile: no relationship from Dim Time to Fact Target is proposed; the measure Revenue Target is defined but cannot yet be cut by month or year.
How to get the answer: decide with the Power BI builder together with decision Q8 (day and month tables), and bring back the option. To check what OBIEE does today, run in "Sample Sales" one analysis with "Per Name Month" and "Fact Target"."Revenue", and bring back the physical SQL OBIEE logs for it.
Answer:

### Q15. The FSD asks for the calendar year of the order, but OBIEE relates revenue to time on the bill date. Which date should drive the Year filter and the months?
Raised in: joins
Why it matters: item F1 is "Year: Calendar year of the order", and its confirmed column is "Dim Time"."Per Name Year". OBIEE relates revenue to time through SAMP_REVENUE_F.BILL_DAY_DT and BILL_MTH_KEY; the order date ORDER_DAY_DT is in the table but has no relationship to time. The two dates can fall in different years or months.
Options: (a) the bill date, like OBIEE; (b) the order date, with a relationship from ORDER_DAY_DT (date and time) to the time table; (c) both, the order date as an inactive relationship used by chosen measures. Like for like would be (a).
Assumed meanwhile: the relationship is proposed on the bill date, like OBIEE; nothing is agreed.
How to get the answer: ask the FSD's author whether "calendar year of the order" (at t1.r2) means the order date or simply the period of the sale as reported today, and bring back the option. To see how much it matters, run select count(*) from SAMP_REVENUE_F where extract(year from ORDER_DAY_DT) <> extract(year from BILL_DAY_DT) and bring back the count.
Answer:

### Q16. FactCrossJoin is joined to SAMP_OFFICES_D, SAMP_PRODUCTS_D and EMPLOYEE on the condition 1=1. What are these joins for, and are they kept?
Raised in: joins
Why it matters: the three complex joins "Complex Join", "Complex Join#1" and "Complex Join#2" have the condition 1=1 and cardinality ONE TO ONE. A condition that pairs every row with every row has no relationship form in a Power BI model. No item of the tab uses FactCrossJoin.
Options: (a) a relationship with the nearest cardinality; (b) no relationship, and whatever the joins are for is done in a view or a measure; (c) dropped together with FactCrossJoin if decision Q11 is (c). Like for like would be (b).
Assumed meanwhile: no relationship is proposed for the three joins.
How to get the answer: decide together with decision Q11; if FactCrossJoin is kept, bring back from the owner of the OBIEE repository what the measure "Preserve" and these joins are meant to achieve (see also as-is document, question Q6).
Answer:

### Q17. "Sales Rep Hierarchy" is a parent-child drill path of no fixed depth. How should managers and their teams be shown in the new tab?
Raised in: hierarchies
Why it matters: OBIEE records "Sales Rep Hierarchy" as parent-child (value based: the depth is not fixed), with its relationships in EMP_PARENT_CHILD, and offers it to report authors shown by "Employee Name". Power BI has no parent-child hierarchy type. The tab lists sales representatives with the highest net revenue (items V3-rep and V3-net-revenue); whether a manager's line should include the revenue of the people below them is not said.
Options: (a) flatten to a fixed number of level columns in the new table or a view; (b) keep parent and child fields and compute the path in the model; (c) no drill path for this tab: one row per employee, their own revenue only. Like for like would be (a) or (b).
Assumed meanwhile: no hierarchy is proposed for Dim Sales Rep; "Employee Name" is a plain field.
How to get the answer: run against the schema SAMPLESALES the query select max(DISTANCE) from EMP_PARENT_CHILD and bring back the greatest depth. Then ask the FSD's author whether the list of sales representatives at p12 should roll up managers over their teams, and bring back the option. See also decision Q7.
Answer:

### Q18. Who may see the revenue targets in the new tab?
Raised in: security
Why it matters: OBIEE gives "read" on "Fact Target"."Revenue", the target shown by items K3 and V1-target, through two privilege packages for which no role is recorded, and "write" through the package held by "BI Author Role" and through one with no role recorded. A Power BI model can hide a measure from some users only through roles, and the roles and their members are not all in the export.
Options: (a) the same restrictions as model roles, one per OBIEE role; (b) no restriction below the report: everyone who can open the tab sees the targets. Like for like would be (a).
Assumed meanwhile: no role is proposed; the measure Revenue Target is designed as visible to everyone who can open the tab.
How to get the answer: in the OBIEE repository, look up the permissions on the presentation column "Revenue" of the presentation table "Fact Target" in the subject area "Sample Sales", and the holders of the privilege packages PrivPack_4209:18141355661677 and PrivPack_4209:18181355661677, and bring back, for each permission, the application role or user it is granted to, and the members of each role. Then confirm with the FSD's author who should see targets on the tab. See also as-is document, questions Q12 and Q13.
Answer:

### Q19. The business-model joins print the fact side as 0..n for Dim Time and Dim Sales Rep but as n..n for Dim Offices and Dim Products, and the dimension side as 0..1 or 1..1, all as inner joins. Are these plain one-to-many joins, and must fact rows with no matching dimension row be left out as OBIEE's inner join leaves them out?
Raised in: joins
Why it matters: the section Relationships proposes every relationship as one to many with a single filter direction. The packet prints "Fact Revenue" (n..n) joins "Dim Offices" (0..1), join type Inner, and "Dim Time" (0..1) joins "Fact Revenue" (0..n), join type Inner; the two spellings of the many side are not explained in the export. An inner join drops fact rows whose key has no dimension row; a Power BI relationship keeps them and shows them under a blank member, so totals can differ.
Options: (a) one-to-many relationships as proposed, and fact rows with no matching dimension row shown under a blank member; (b) one-to-many relationships as proposed, and the load of the new fact tables or a view leaves out the rows with no matching dimension row; (c) another cardinality for the joins printed n..n, if the repository means something other than many. Like for like would be (b).
Assumed meanwhile: the relationships are designed one to many as proposed; nothing is decided about unmatched rows.
How to get the answer: in the repository's business model "SAMPLESALES", look at the logical joins of "Fact Revenue" and "Fact Target" with "Dim Offices" and "Dim Products" and bring back the cardinality each shows on each side; then run, on the old database, a count of the rows of SAMP_REVENUE_F and SAMP_TARGETS_F whose OFFICE_KEY or PROD_KEY has no row in SAMP_OFFICES_D or SAMP_PRODUCTS_D, and bring back the counts.
Answer:

## Answered questions

## Not described

Rows of the Slice that this design leaves out, each with the reason.

- [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.All Months Revenue] — not used by this tab
- [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.All Products Revenue] — not used by this tab
- [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Preserve] — not used by this tab
- [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Revenue (INR)] — not used by this tab
- [kb:sampleapp::bmm::SAMPLESALES.Fact Target.Units] — not used by this tab
- [kb:sampleapp::bmm::SAMPLESALES.Dim Products (Translated)] — not used by this tab
- [kb:sampleapp::bmm::SAMPLESALES.Dim Products (Translated).key.PK_PROD_TRANS1] — not used by this tab
- [kb:sampleapp::bmm::SAMPLESALES.Dim Products (Translated).src.SAMP_PRODUCTS_D_TRANSLATION] — not used by this tab

## Questions the export answered

None.
