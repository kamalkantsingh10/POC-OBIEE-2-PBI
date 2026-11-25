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
