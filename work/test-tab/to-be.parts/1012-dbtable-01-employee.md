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
