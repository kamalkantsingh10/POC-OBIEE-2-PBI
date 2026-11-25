## Database table FactCrossJoin

FactCrossJoin is listed in `xe.SAMPLESALES` with type `select`: it is not a database table but a query kept in the repository [kb:sampleapp::phys::xe.SAMPLESALES.FactCrossJoin]. It is not an alias of another table, and no description is recorded [kb:sampleapp::phys::xe.SAMPLESALES.FactCrossJoin]. Its SQL, for database type `DefaultMulDB`, is exactly `select 1 Preserve from dual` [kb:sampleapp::phys::xe.SAMPLESALES.FactCrossJoin.def.DefaultMulDB]. Put mechanically, the query selects the number 1 under the name Preserve from the table dual. The export lists 1 column for it, mapped by a field of this subject area, and no key (counted; no Row identifier).

### Columns

| # | Column | Type | Precision | Scale | Nullable | External name | Mapped by | Evidence |
|---|---|---|---|---|---|---|---|---|
| 1 | Preserve | DOUBLE | not recorded | not recorded | not stated | not recorded | Fact Revenue, Preserve | [kb:sampleapp::phys::xe.SAMPLESALES.FactCrossJoin.Preserve] |

Nullable: the export records this only when it is true. "Not stated" means the export does not carry it; it is not a statement that the column must be filled. "Not recorded" means the repository says nothing; it does not mean no, and it does not mean 0.

### Keys

Keys of FactCrossJoin: not recorded. The repository declares no key for it, so nothing in the repository says which column(s) identify a row (listed in the packet without a Row identifier).

### Foreign keys and joins

- A complex join "Complex Join" between FactCrossJoin and SAMP_OFFICES_D; its condition is stated in full in the section How the tables relate.
- A complex join "Complex Join#1" between FactCrossJoin and SAMP_PRODUCTS_D; its condition is stated in full in the section How the tables relate.
- A complex join "Complex Join#2" between FactCrossJoin and EMPLOYEE; its condition is stated in full in the section How the tables relate.

### Read by

The source "FactCrossJoin" of the business table Fact Revenue (listed in the packet without a Row identifier). Why the measure "Preserve" is built on this query is not recorded (open question Q6).
