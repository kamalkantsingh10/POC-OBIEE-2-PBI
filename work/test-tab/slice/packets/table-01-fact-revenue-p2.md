# Logical table "Fact Revenue" (part 2 of 2)
Unit: table-01-fact-revenue-p2
Kind: logical_table
Tab: test-tab (subject areas: Sample Sales; estate sampleapp)
Facts from: lineage, logical_table_sources, lts_column_map, lts_tables
Rows of this packet a document must cover: 5. Each is on a line that ends with (REQUIRED).
To cite a fact, copy the marker in square brackets that follows it, exactly as written. "->" points at another row.
This is part 2 of 2 of what was too long for one packet (the parts are table-01-fact-revenue-p1 to table-01-fact-revenue-p2). Each part is a unit of its own: write this one as its own section, from this packet alone.

## Table sources: where the table's data comes from

- Source "Aggregate Revenue" of logical table "Fact Revenue": active true; priority group `1`. [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue] (REQUIRED)
    - Level of detail (grain; the source's group by): ` GROUPBYLEVEL("SAMPLESALES"."Time Hierarchy"."Month", "SAMPLESALES"."Products Hierararchy"."Product Detail")` [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue]
        - %1 -> sampleapp::bmm::SAMPLESALES.Time Hierarchy.Month
        - %2 -> sampleapp::bmm::SAMPLESALES.Products Hierararchy.Product Detail
    - Physical tables this source is built on: 1 (one table, so the source makes no join).
        - Reads physical table "SAMP_REVENUE_A" in `xe.SAMPLESALES`; type `none`. Unit dbtable-07-samp-revenue-a states the table whole: what it is in the database, every column, its keys. -> sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue#table0]
    - Logical column "Bill Mth Key" here is `"xe".""."SAMPLESALES"."SAMP_REVENUE_A"."BILL_MTH_KEY"`; physical column "SAMP_REVENUE_A"."BILL_MTH_KEY" (type DOUBLE, precision 15, nullable true). [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue#map0]
    - Logical column "Discnt Value" here is `"xe".""."SAMPLESALES"."SAMP_REVENUE_A"."DISCNT_VALUE"`; physical column "SAMP_REVENUE_A"."DISCNT_VALUE" (type DOUBLE, precision 7, scale 2, nullable true). [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue#map1]
        - Lineage: presentation column "Fact Revenue"."Discnt Value" <- source "Aggregate Revenue" <- physical column "SAMP_REVENUE_A"."DISCNT_VALUE". [kb:sampleapp::pres::Sample Sales.Fact Revenue.Discnt Value#sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue]
    - Logical column "Prod Key" here is `"xe".""."SAMPLESALES"."SAMP_REVENUE_A"."PROD_KEY"`; physical column "SAMP_REVENUE_A"."PROD_KEY" (type DOUBLE, precision 15, nullable true). [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue#map2]
    - Logical column "Revenue" here is `"xe".""."SAMPLESALES"."SAMP_REVENUE_A"."REVENUE"`; physical column "SAMP_REVENUE_A"."REVENUE" (type DOUBLE, precision 8, scale 2, nullable true). [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue#map3]
        - Lineage: presentation column "Fact Revenue"."Revenue" <- source "Aggregate Revenue" <- physical column "SAMP_REVENUE_A"."REVENUE". [kb:sampleapp::pres::Sample Sales.Fact Revenue.Revenue#sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue]
    - Logical column "Units" here is `"xe".""."SAMPLESALES"."SAMP_REVENUE_A"."UNITS"`; physical column "SAMP_REVENUE_A"."UNITS" (type DOUBLE, precision 15, nullable true). [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue#map4]
        - Lineage: presentation column "Fact Revenue"."Units" <- source "Aggregate Revenue" <- physical column "SAMP_REVENUE_A"."UNITS". [kb:sampleapp::pres::Sample Sales.Fact Revenue.Units#sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue]
    - Logical column "All Products Revenue" here is `"xe".""."SAMPLESALES"."SAMP_REVENUE_A"."REVENUE"`; physical column "SAMP_REVENUE_A"."REVENUE" (type DOUBLE, precision 8, scale 2, nullable true). [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue#map5]
    - Logical column "All Months Revenue" here is `"xe".""."SAMPLESALES"."SAMP_REVENUE_A"."REVENUE"`; physical column "SAMP_REVENUE_A"."REVENUE" (type DOUBLE, precision 8, scale 2, nullable true). [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue#map6]
    - Logical column "Revenue (GBP)" here is `"xe".""."SAMPLESALES"."SAMP_REVENUE_A"."REVENUE"`; physical column "SAMP_REVENUE_A"."REVENUE" (type DOUBLE, precision 8, scale 2, nullable true). [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue#map7]
    - Logical column "Revenue (EUR)" here is `"xe".""."SAMPLESALES"."SAMP_REVENUE_A"."REVENUE"`; physical column "SAMP_REVENUE_A"."REVENUE" (type DOUBLE, precision 8, scale 2, nullable true). [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue#map8]
    - Logical column "Revenue (INR)" here is `"xe".""."SAMPLESALES"."SAMP_REVENUE_A"."REVENUE"`; physical column "SAMP_REVENUE_A"."REVENUE" (type DOUBLE, precision 8, scale 2, nullable true). [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue#map9]

- Source "Aggregate Revenue By Month" of logical table "Fact Revenue": active true; priority group not recorded (empty; it does not mean lowest). [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue By Month] (REQUIRED)
    - Level of detail (grain; the source's group by): ` GROUPBYLEVEL("SAMPLESALES"."Time Hierarchy"."Month")` [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue By Month]
        - %1 -> sampleapp::bmm::SAMPLESALES.Time Hierarchy.Month
    - Physical tables this source is built on: 1 (one table, so the source makes no join).
        - Reads physical table "SAMP_REVENUE_A_MTH" in `xe.SAMPLESALES`; type `none`. Unit dbtable-08-samp-revenue-a-mth states the table whole: what it is in the database, every column, its keys. -> sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A_MTH [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue By Month#table0]
    - Logical column "Revenue" here is `"xe".""."SAMPLESALES"."SAMP_REVENUE_A_MTH"."REVENUE"`; physical column "SAMP_REVENUE_A_MTH"."REVENUE" (type DOUBLE, precision 22, nullable true). [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue By Month#map0]
        - Lineage: presentation column "Fact Revenue"."Revenue" <- source "Aggregate Revenue By Month" <- physical column "SAMP_REVENUE_A_MTH"."REVENUE". [kb:sampleapp::pres::Sample Sales.Fact Revenue.Revenue#sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue By Month]
    - Logical column "Units" here is `"xe".""."SAMPLESALES"."SAMP_REVENUE_A_MTH"."UNITS"`; physical column "SAMP_REVENUE_A_MTH"."UNITS" (type DOUBLE, precision 22, nullable true). [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue By Month#map1]
        - Lineage: presentation column "Fact Revenue"."Units" <- source "Aggregate Revenue By Month" <- physical column "SAMP_REVENUE_A_MTH"."UNITS". [kb:sampleapp::pres::Sample Sales.Fact Revenue.Units#sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue By Month]
    - Logical column "BILL_MTH_KEY" here is `"xe".""."SAMPLESALES"."SAMP_REVENUE_A_MTH"."BILL_MTH_KEY"`; physical column "SAMP_REVENUE_A_MTH"."BILL_MTH_KEY" (type DOUBLE, precision 22, nullable true). [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue By Month#map2]
    - Logical column "DISCNT_VALUE" here is `"xe".""."SAMPLESALES"."SAMP_REVENUE_A_MTH"."DISCNT_VALUE"`; physical column "SAMP_REVENUE_A_MTH"."DISCNT_VALUE" (type DOUBLE, precision 22, nullable true). [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue By Month#map3]

- Source "Aggregate Revenue by Year" of logical table "Fact Revenue": active true; priority group not recorded (empty; it does not mean lowest). [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue by Year] (REQUIRED)
    - Level of detail (grain; the source's group by): ` GROUPBYLEVEL("SAMPLESALES"."Time Hierarchy"."Year")` [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue by Year]
        - %1 -> sampleapp::bmm::SAMPLESALES.Time Hierarchy.Year
    - Physical tables this source is built on: 1 (one table, so the source makes no join).
        - Reads physical table "SAMP_REVENUE_A_YR" in `xe.SAMPLESALES`; type `none`. Unit dbtable-09-samp-revenue-a-yr states the table whole: what it is in the database, every column, its keys. -> sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A_YR [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue by Year#table0]
    - Logical column "Revenue" here is `"xe".""."SAMPLESALES"."SAMP_REVENUE_A_YR"."REVENUE"`; physical column "SAMP_REVENUE_A_YR"."REVENUE" (type DOUBLE, precision 22, nullable true). [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue by Year#map0]
        - Lineage: presentation column "Fact Revenue"."Revenue" <- source "Aggregate Revenue by Year" <- physical column "SAMP_REVENUE_A_YR"."REVENUE". [kb:sampleapp::pres::Sample Sales.Fact Revenue.Revenue#sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue by Year]
    - Logical column "Units" here is `"xe".""."SAMPLESALES"."SAMP_REVENUE_A_YR"."UNITS"`; physical column "SAMP_REVENUE_A_YR"."UNITS" (type DOUBLE, precision 22, nullable true). [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue by Year#map1]
        - Lineage: presentation column "Fact Revenue"."Units" <- source "Aggregate Revenue by Year" <- physical column "SAMP_REVENUE_A_YR"."UNITS". [kb:sampleapp::pres::Sample Sales.Fact Revenue.Units#sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue by Year]
    - Logical column "DISCNT_VALUE" here is `"xe".""."SAMPLESALES"."SAMP_REVENUE_A_YR"."DISCNT_VALUE"`; physical column "SAMP_REVENUE_A_YR"."DISCNT_VALUE" (type DOUBLE, precision 22, nullable true). [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue by Year#map2]
    - Logical column "BILL_YEAR_KEY" here is `"xe".""."SAMPLESALES"."SAMP_REVENUE_A_YR"."BILL_YEAR_KEY"`; physical column "SAMP_REVENUE_A_YR"."BILL_YEAR_KEY" (type DOUBLE, nullable true). [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue by Year#map3]

- Source "FactCrossJoin" of logical table "Fact Revenue": active true; priority group not recorded (empty; it does not mean lowest). [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.FactCrossJoin] (REQUIRED)
    - Level of detail (grain): not recorded. The repository gives this source no level of detail. [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.FactCrossJoin]
    - Physical tables this source is built on: 1 (one table, so the source makes no join).
        - Reads physical table "FactCrossJoin" in `xe.SAMPLESALES`; type `select` (not a database table: it is a query kept in the repository). Unit dbtable-03-factcrossjoin states the table whole: what it is in the database (its SQL), every column, its keys. -> sampleapp::phys::xe.SAMPLESALES.FactCrossJoin [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.FactCrossJoin#table0]
    - Logical column "Preserve" here is `"xe".""."SAMPLESALES"."FactCrossJoin"."Preserve"`; physical column "FactCrossJoin"."Preserve" (type DOUBLE, nullable not stated). [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.FactCrossJoin#map0]

- Source "SAMP_REVENUE_F" of logical table "Fact Revenue": active true; priority group not recorded (empty; it does not mean lowest). [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.SAMP_REVENUE_F] (REQUIRED)
    - Level of detail (grain; the source's group by): ` GROUPBYLEVEL("SAMPLESALES"."Sales Rep Hierarchy"."Detail", "SAMPLESALES"."Time Hierarchy"."Dim Time Detail", "SAMPLESALES"."Office Hierarchy"."Office Detail", "SAMPLESALES"."Products Hierararchy"."Product Detail")` [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.SAMP_REVENUE_F]
        - %1 -> sampleapp::bmm::SAMPLESALES.Sales Rep Hierarchy.Detail
        - %2 -> sampleapp::bmm::SAMPLESALES.Time Hierarchy.Dim Time Detail
        - %3 -> sampleapp::bmm::SAMPLESALES.Office Hierarchy.Office Detail
        - %4 -> sampleapp::bmm::SAMPLESALES.Products Hierararchy.Product Detail
    - Physical tables this source is built on: 1 (one table, so the source makes no join).
        - Reads physical table "SAMP_REVENUE_F" in `xe.SAMPLESALES`; type `none`. Unit dbtable-10-samp-revenue-f states the table whole: what it is in the database, every column, its keys. -> sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.SAMP_REVENUE_F#table0]
    - Logical column "Bill Day Dt" here is `"xe".""."SAMPLESALES"."SAMP_REVENUE_F"."BILL_DAY_DT"`; physical column "SAMP_REVENUE_F"."BILL_DAY_DT" (type DATE, nullable true). [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.SAMP_REVENUE_F#map0]
    - Logical column "Bill Mth Key" here is `"xe".""."SAMPLESALES"."SAMP_REVENUE_F"."BILL_MTH_KEY"`; physical column "SAMP_REVENUE_F"."BILL_MTH_KEY" (type DOUBLE, precision 15, nullable true). [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.SAMP_REVENUE_F#map1]
    - Logical column "Units" here is `"xe".""."SAMPLESALES"."SAMP_REVENUE_F"."UNITS"`; physical column "SAMP_REVENUE_F"."UNITS" (type DOUBLE, precision 15, nullable true). [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.SAMP_REVENUE_F#map10]
        - Lineage: presentation column "Fact Revenue"."Units" <- source "SAMP_REVENUE_F" <- physical column "SAMP_REVENUE_F"."UNITS". [kb:sampleapp::pres::Sample Sales.Fact Revenue.Units#sampleapp::bmm::SAMPLESALES.Fact Revenue.src.SAMP_REVENUE_F]
    - Logical column "All Products Revenue" here is `"xe".""."SAMPLESALES"."SAMP_REVENUE_F"."REVENUE"`; physical column "SAMP_REVENUE_F"."REVENUE" (type DOUBLE, precision 7, scale 2, nullable true). [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.SAMP_REVENUE_F#map11]
    - Logical column "All Months Revenue" here is `"xe".""."SAMPLESALES"."SAMP_REVENUE_F"."REVENUE"`; physical column "SAMP_REVENUE_F"."REVENUE" (type DOUBLE, precision 7, scale 2, nullable true). [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.SAMP_REVENUE_F#map12]
    - Logical column "Sales Employee Key" here is `"xe".""."SAMPLESALES"."SAMP_REVENUE_F"."EMPLOYEE_KEY"`; physical column "SAMP_REVENUE_F"."EMPLOYEE_KEY" (type INT, nullable not stated). [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.SAMP_REVENUE_F#map13]
    - Logical column "Revenue (GBP)" here is `"xe".""."SAMPLESALES"."SAMP_REVENUE_F"."REVENUE" * 0.62`; physical column "SAMP_REVENUE_F"."REVENUE" (type DOUBLE, precision 7, scale 2, nullable true). [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.SAMP_REVENUE_F#map14]
    - Logical column "Revenue (EUR)" here is `"xe".""."SAMPLESALES"."SAMP_REVENUE_F"."REVENUE" * 0.76`; physical column "SAMP_REVENUE_F"."REVENUE" (type DOUBLE, precision 7, scale 2, nullable true). [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.SAMP_REVENUE_F#map15]
    - Logical column "Revenue (INR)" here is `"xe".""."SAMPLESALES"."SAMP_REVENUE_F"."REVENUE" * 54.68`; physical column "SAMP_REVENUE_F"."REVENUE" (type DOUBLE, precision 7, scale 2, nullable true). [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.SAMP_REVENUE_F#map16]
    - Logical column "Discnt Value" here is `"xe".""."SAMPLESALES"."SAMP_REVENUE_F"."DISCNT_VALUE"`; physical column "SAMP_REVENUE_F"."DISCNT_VALUE" (type DOUBLE, precision 6, scale 2, nullable true). [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.SAMP_REVENUE_F#map2]
        - Lineage: presentation column "Fact Revenue"."Discnt Value" <- source "SAMP_REVENUE_F" <- physical column "SAMP_REVENUE_F"."DISCNT_VALUE". [kb:sampleapp::pres::Sample Sales.Fact Revenue.Discnt Value#sampleapp::bmm::SAMPLESALES.Fact Revenue.src.SAMP_REVENUE_F]
    - Logical column "Office Key" here is `"xe".""."SAMPLESALES"."SAMP_REVENUE_F"."OFFICE_KEY"`; physical column "SAMP_REVENUE_F"."OFFICE_KEY" (type DOUBLE, precision 15, nullable true). [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.SAMP_REVENUE_F#map3]
    - Logical column "Order Day Dt" here is `"xe".""."SAMPLESALES"."SAMP_REVENUE_F"."ORDER_DAY_DT"`; physical column "SAMP_REVENUE_F"."ORDER_DAY_DT" (type DATETIME, nullable true). [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.SAMP_REVENUE_F#map4]
    - Logical column "Order Number" here is `"xe".""."SAMPLESALES"."SAMP_REVENUE_F"."ORDER_NUMBER"`; physical column "SAMP_REVENUE_F"."ORDER_NUMBER" (type DOUBLE, precision 15, nullable true). [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.SAMP_REVENUE_F#map5]
    - Logical column "Order Status" here is `"xe".""."SAMPLESALES"."SAMP_REVENUE_F"."ORDER_STATUS"`; physical column "SAMP_REVENUE_F"."ORDER_STATUS" (type VARCHAR, precision 11, nullable true). [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.SAMP_REVENUE_F#map6]
    - Logical column "Order Type" here is `"xe".""."SAMPLESALES"."SAMP_REVENUE_F"."ORDER_TYPE"`; physical column "SAMP_REVENUE_F"."ORDER_TYPE" (type VARCHAR, precision 8, nullable true). [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.SAMP_REVENUE_F#map7]
    - Logical column "Prod Key" here is `"xe".""."SAMPLESALES"."SAMP_REVENUE_F"."PROD_KEY"`; physical column "SAMP_REVENUE_F"."PROD_KEY" (type DOUBLE, precision 15, nullable true). [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.SAMP_REVENUE_F#map8]
    - Logical column "Revenue" here is `"xe".""."SAMPLESALES"."SAMP_REVENUE_F"."REVENUE"`; physical column "SAMP_REVENUE_F"."REVENUE" (type DOUBLE, precision 7, scale 2, nullable true). [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.SAMP_REVENUE_F#map9]
        - Lineage: presentation column "Fact Revenue"."Revenue" <- source "SAMP_REVENUE_F" <- physical column "SAMP_REVENUE_F"."REVENUE". [kb:sampleapp::pres::Sample Sales.Fact Revenue.Revenue#sampleapp::bmm::SAMPLESALES.Fact Revenue.src.SAMP_REVENUE_F]

