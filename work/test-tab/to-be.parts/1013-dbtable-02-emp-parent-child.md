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
