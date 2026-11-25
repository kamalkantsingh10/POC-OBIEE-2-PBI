# Joins: how the tables relate
Unit: joins
Kind: joins
Tab: test-tab (subject areas: Sample Sales; estate sampleapp)
Facts from: bmm_joins, physical_joins
Rows of this packet a document must cover: 24. Each is on a line that ends with (REQUIRED).
To cite a fact, copy the marker in square brackets that follows it, exactly as written. "->" points at another row.

## Business-model joins (between logical tables)

- "Dim Time" (0..1) joins "Fact Target" (0..n); join type Inner. [kb:sampleapp::bmm::joins.Relationship_2004:11061353928600] (REQUIRED)

- "Dim Time" (0..1) joins "Fact Revenue" (0..n); join type Inner. [kb:sampleapp::bmm::joins.Relationship_2004:11091353928666] (REQUIRED)

- "Dim Sales Rep" (1..1) joins "Fact Revenue" (0..n); join type Inner. [kb:sampleapp::bmm::joins.Relationship_2004:17511354089145] (REQUIRED)

- "Fact Revenue" (n..n) joins "Dim Offices" (0..1); join type Inner. [kb:sampleapp::bmm::joins.Relationship_2004:2111353851711] (REQUIRED)

- "Fact Revenue" (n..n) joins "Dim Products" (0..1); join type Inner. [kb:sampleapp::bmm::joins.Relationship_2004:2141353851711] (REQUIRED)

- "Fact Target" (n..n) joins "Dim Offices" (0..1); join type Inner. [kb:sampleapp::bmm::joins.Relationship_2004:2301353851711] (REQUIRED)

- "Fact Target" (n..n) joins "Dim Products" (0..1); join type Inner. [kb:sampleapp::bmm::joins.Relationship_2004:2331353851711] (REQUIRED)

## Physical joins (between database tables of the slice)

- complex_join "Complex Join": "FactCrossJoin" to "SAMP_OFFICES_D"; cardinality `ONE TO ONE`. [kb:sampleapp::phys::joins.Complex Join] (REQUIRED)
    - Join condition: `1=1` [kb:sampleapp::phys::joins.Complex Join]

- complex_join "Complex Join#1": "FactCrossJoin" to "SAMP_PRODUCTS_D"; cardinality `ONE TO ONE`. [kb:sampleapp::phys::joins.Complex Join#1] (REQUIRED)
    - Join condition: `1=1` [kb:sampleapp::phys::joins.Complex Join#1]

- complex_join "Complex Join#2": "FactCrossJoin" to "EMPLOYEE"; cardinality `ONE TO ONE`. [kb:sampleapp::phys::joins.Complex Join#2] (REQUIRED)
    - Join condition: `1=1` [kb:sampleapp::phys::joins.Complex Join#2]

- foreign_key "EMP_PARENT_CHILD_FKey": from "EMP_PARENT_CHILD" ("ANCESTOR_KEY") to "EMPLOYEE" ("EMP_ID"). [kb:sampleapp::phys::xe.SAMPLESALES.EMP_PARENT_CHILD.EMP_PARENT_CHILD_FKey] (REQUIRED)
    - Column pair 1 of 1: "EMP_PARENT_CHILD"."ANCESTOR_KEY" (type DOUBLE, precision 15, nullable not stated) = "EMPLOYEE"."EMP_ID" (type DOUBLE, precision 15, nullable not stated). [kb:sampleapp::phys::xe.SAMPLESALES.EMP_PARENT_CHILD.EMP_PARENT_CHILD_FKey]

- foreign_key "SAMP_PRODUCTS_D_TRANSLATION_FKey": from "SAMP_PRODUCTS_D_TRANSLATION" ("PROD_KEY") to "SAMP_PRODUCTS_D" ("PROD_KEY"). [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_PRODUCTS_D_TRANSLATION.SAMP_PRODUCTS_D_TRANSLATION_FKey] (REQUIRED)
    - Column pair 1 of 1: "SAMP_PRODUCTS_D_TRANSLATION"."PROD_KEY" (type DOUBLE, precision 15, nullable not stated) = "SAMP_PRODUCTS_D"."PROD_KEY" (type DOUBLE, precision 15, nullable not stated). [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_PRODUCTS_D_TRANSLATION.SAMP_PRODUCTS_D_TRANSLATION_FKey]

- foreign_key "SAMP_REVENUE_A_FKey": from "SAMP_REVENUE_A" ("BILL_MTH_KEY") to "SAMP_TIME_MTH_D" ("MTH_KEY"). [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A.SAMP_REVENUE_A_FKey] (REQUIRED)
    - Column pair 1 of 1: "SAMP_REVENUE_A"."BILL_MTH_KEY" (type DOUBLE, precision 15, nullable true) = "SAMP_TIME_MTH_D"."MTH_KEY" (type DOUBLE, precision 15, nullable true). [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A.SAMP_REVENUE_A_FKey]

- foreign_key "SAMP_REVENUE_A_FKey#1": from "SAMP_REVENUE_A" ("PROD_KEY") to "SAMP_PRODUCTS_D" ("PROD_KEY"). [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A.SAMP_REVENUE_A_FKey#1] (REQUIRED)
    - Column pair 1 of 1: "SAMP_REVENUE_A"."PROD_KEY" (type DOUBLE, precision 15, nullable true) = "SAMP_PRODUCTS_D"."PROD_KEY" (type DOUBLE, precision 15, nullable not stated). [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A.SAMP_REVENUE_A_FKey#1]

- foreign_key "SAMP_REVENUE_A_MTH_Foreign Key": from "SAMP_REVENUE_A_MTH" ("BILL_MTH_KEY") to "SAMP_TIME_MTH_D" ("MTH_KEY"). [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A_MTH.SAMP_REVENUE_A_MTH_Foreign Key] (REQUIRED)
    - Column pair 1 of 1: "SAMP_REVENUE_A_MTH"."BILL_MTH_KEY" (type DOUBLE, precision 22, nullable true) = "SAMP_TIME_MTH_D"."MTH_KEY" (type DOUBLE, precision 15, nullable true). [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A_MTH.SAMP_REVENUE_A_MTH_Foreign Key]

- foreign_key "SAMP_REVENUE_A_YR_Foreign Key": from "SAMP_REVENUE_A_YR" ("BILL_YEAR_KEY") to "SAMP_TIME_MTH_D" ("CAL_YEAR"). [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A_YR.SAMP_REVENUE_A_YR_Foreign Key] (REQUIRED)
    - Column pair 1 of 1: "SAMP_REVENUE_A_YR"."BILL_YEAR_KEY" (type DOUBLE, nullable true) = "SAMP_TIME_MTH_D"."CAL_YEAR" (type DOUBLE, precision 15, nullable true). [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A_YR.SAMP_REVENUE_A_YR_Foreign Key]

- foreign_key "SAMP_REVENUE_F_FKey": from "SAMP_REVENUE_F" ("BILL_MTH_KEY") to "SAMP_TIME_MTH_D" ("MTH_KEY"). [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F.SAMP_REVENUE_F_FKey] (REQUIRED)
    - Column pair 1 of 1: "SAMP_REVENUE_F"."BILL_MTH_KEY" (type DOUBLE, precision 15, nullable true) = "SAMP_TIME_MTH_D"."MTH_KEY" (type DOUBLE, precision 15, nullable true). [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F.SAMP_REVENUE_F_FKey]

- foreign_key "SAMP_REVENUE_F_FKey#1": from "SAMP_REVENUE_F" ("OFFICE_KEY") to "SAMP_OFFICES_D" ("OFFICE_KEY"). [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F.SAMP_REVENUE_F_FKey#1] (REQUIRED)
    - Column pair 1 of 1: "SAMP_REVENUE_F"."OFFICE_KEY" (type DOUBLE, precision 15, nullable true) = "SAMP_OFFICES_D"."OFFICE_KEY" (type DOUBLE, precision 15, nullable not stated). [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F.SAMP_REVENUE_F_FKey#1]

- foreign_key "SAMP_REVENUE_F_FKey#2": from "SAMP_REVENUE_F" ("PROD_KEY") to "SAMP_PRODUCTS_D" ("PROD_KEY"). [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F.SAMP_REVENUE_F_FKey#2] (REQUIRED)
    - Column pair 1 of 1: "SAMP_REVENUE_F"."PROD_KEY" (type DOUBLE, precision 15, nullable true) = "SAMP_PRODUCTS_D"."PROD_KEY" (type DOUBLE, precision 15, nullable not stated). [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F.SAMP_REVENUE_F_FKey#2]

- foreign_key "SAMP_REVENUE_F_FKey#3": from "SAMP_REVENUE_F" ("BILL_DAY_DT") to "SAMP_TIME_DAY_D" ("CALENDAR_DATE"). [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F.SAMP_REVENUE_F_FKey#3] (REQUIRED)
    - Column pair 1 of 1: "SAMP_REVENUE_F"."BILL_DAY_DT" (type DATE, nullable true) = "SAMP_TIME_DAY_D"."CALENDAR_DATE" (type DATE, nullable not stated). [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F.SAMP_REVENUE_F_FKey#3]

- foreign_key "SAMP_REVENUE_F_FKey#4": from "SAMP_REVENUE_F" ("EMPLOYEE_KEY") to "EMP_PARENT_CHILD" ("MEMBER_KEY"). [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F.SAMP_REVENUE_F_FKey#4] (REQUIRED)
    - Column pair 1 of 1: "SAMP_REVENUE_F"."EMPLOYEE_KEY" (type INT, nullable not stated) = "EMP_PARENT_CHILD"."MEMBER_KEY" (type DOUBLE, precision 15, nullable not stated). [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F.SAMP_REVENUE_F_FKey#4]

- foreign_key "SAMP_TARGETS_F_FKey": from "SAMP_TARGETS_F" ("BILL_MTH_KEY") to "SAMP_TIME_MTH_D" ("MTH_KEY"). [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TARGETS_F.SAMP_TARGETS_F_FKey] (REQUIRED)
    - Column pair 1 of 1: "SAMP_TARGETS_F"."BILL_MTH_KEY" (type DOUBLE, precision 15, nullable true) = "SAMP_TIME_MTH_D"."MTH_KEY" (type DOUBLE, precision 15, nullable true). [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TARGETS_F.SAMP_TARGETS_F_FKey]

- foreign_key "SAMP_TARGETS_F_FKey#1": from "SAMP_TARGETS_F" ("OFFICE_KEY") to "SAMP_OFFICES_D" ("OFFICE_KEY"). [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TARGETS_F.SAMP_TARGETS_F_FKey#1] (REQUIRED)
    - Column pair 1 of 1: "SAMP_TARGETS_F"."OFFICE_KEY" (type DOUBLE, precision 15, nullable true) = "SAMP_OFFICES_D"."OFFICE_KEY" (type DOUBLE, precision 15, nullable not stated). [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TARGETS_F.SAMP_TARGETS_F_FKey#1]

- foreign_key "SAMP_TARGETS_F_FKey#2": from "SAMP_TARGETS_F" ("PROD_KEY") to "SAMP_PRODUCTS_D" ("PROD_KEY"). [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TARGETS_F.SAMP_TARGETS_F_FKey#2] (REQUIRED)
    - Column pair 1 of 1: "SAMP_TARGETS_F"."PROD_KEY" (type DOUBLE, precision 15, nullable true) = "SAMP_PRODUCTS_D"."PROD_KEY" (type DOUBLE, precision 15, nullable not stated). [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TARGETS_F.SAMP_TARGETS_F_FKey#2]

