# Physical table "FactCrossJoin"
Unit: dbtable-03-factcrossjoin
Kind: physical_table
Tab: test-tab (subject areas: Sample Sales; estate sampleapp)
Facts from: physical_columns, physical_table_definitions, physical_tables
Rows of this packet a document must cover: 3. Each is on a line that ends with (REQUIRED).
To cite a fact, copy the marker in square brackets that follows it, exactly as written. "->" points at another row.

## The table, as the repository knows it

- Physical table "FactCrossJoin" in `xe.SAMPLESALES` (the containers above it in the repository, outermost first; usually database and schema); type `select` (not a database table: it is a query kept in the repository). [kb:sampleapp::phys::xe.SAMPLESALES.FactCrossJoin] (REQUIRED)
    - It is not an alias of another physical table.
    - Description in the repository: not recorded.
    - Its SQL for database type `DefaultMulDB`: `select 1 Preserve from dual` [kb:sampleapp::phys::xe.SAMPLESALES.FactCrossJoin.def.DefaultMulDB] (REQUIRED)
- Columns the repository lists for it: 1, of which mapped by a logical column of this slice: 1; not mapped by any: 0. Keys: 0. (counted; no Row identifier)
- Logical table sources of this slice that read it: 1.
    - Source "FactCrossJoin" of logical table "Fact Revenue" (unit table-01-fact-revenue) -> sampleapp::bmm::SAMPLESALES.Fact Revenue.src.FactCrossJoin

## Keys

- Keys of physical table "FactCrossJoin": not recorded. The repository declares no key for it, so nothing in the repository says which column(s) identify a row. (physical_keys; no Row identifier)

## Foreign keys and joins (each is stated in full in the joins unit)

- complex_join "Complex Join" between "FactCrossJoin" and "SAMP_OFFICES_D"; its condition is in the joins unit. -> sampleapp::phys::joins.Complex Join
- complex_join "Complex Join#1" between "FactCrossJoin" and "SAMP_PRODUCTS_D"; its condition is in the joins unit. -> sampleapp::phys::joins.Complex Join#1
- complex_join "Complex Join#2" between "FactCrossJoin" and "EMPLOYEE"; its condition is in the joins unit. -> sampleapp::phys::joins.Complex Join#2

## Columns, in the order the repository lists them

- Each line below is one column: its place, name, data type, precision (length), scale (digits after the decimal point), whether it may be empty (nullable), its external name, and whether a logical column of this slice is mapped from it. "not recorded" means the repository says nothing; it does not mean no, and it does not mean 0.
- About nullable: the repository export writes the nullable attribute only as true; it never writes false. "nullable not stated" therefore means the attribute is absent: most likely the column is not nullable in the repository, but the file does not say so, and the repository is not proof of the constraint in the database.
- Column 1 "Preserve": type DOUBLE; precision not recorded; scale not recorded; nullable not stated; external name not recorded; MAPPED by logical column(s) "Fact Revenue"."Preserve". [kb:sampleapp::phys::xe.SAMPLESALES.FactCrossJoin.Preserve] (REQUIRED)

