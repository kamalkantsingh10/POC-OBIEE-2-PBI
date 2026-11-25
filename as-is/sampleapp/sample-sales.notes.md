# Notes for the summary

## overview: What report authors see
- Subject area "Sample Sales" on business model "SAMPLESALES"; alias SAMPLESALES; implicit fact column "Fact Target"."Revenue" [kb:sampleapp::pres::Sample Sales].
- Six tables report authors see: Dim Offices (9 fields), Dim Products (9), Dim Sales Rep (5), Dim Time (6), Fact Revenue (15), Fact Target (5); 49 fields, 8 measures; 7 business tables, 63 fields, 11 measures; 12 sources; 13 database tables, 113 columns, 8 keys; 7 business-model joins, 17 database joins; 4 drill paths; 1 variable; 7 security rows; 0 reports (counted, no Row identifier).
- KB extracted 2026-11-23T20:55:33Z, extractor 0.1.0; only the rpd half extracted; no Web Catalog half, so reports and dashboards were not examined (manifest, no Row identifier). Estate-wide, not in this subject area: 2 object permissions with no target, 1 init block, 1 variable.
- Observation: the display name of "Dim Time" is `VALUEOF(NQ_SESSION.CN_Sample_Sales_Dim_Time)` [kb:sampleapp::pres::Sample Sales.Dim Time]; to set beside whether the variables unit lists CN_Sample_Sales_Dim_Time as defined.
- Questions: Q1 what name report authors see for "Dim Time" (display name is VALUEOF of a session variable)

## table-01-fact-revenue-p1: Fact Revenue
- Fact Revenue: 22 fields, 9 measures (All Months Revenue, All Products Revenue, Discnt Value, Preserve, Revenue, Revenue (EUR), Revenue (GBP), Revenue (INR), Units), 7 not shown; 5 sources: "Aggregate Revenue", "Aggregate Revenue By Month", "Aggregate Revenue by Year", "FactCrossJoin", "SAMP_REVENUE_F" [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue]. No key recorded. Part 1 ended with the block "Measures and fields report authors do not see"; the sources are in part 2.
- All measures SUM except Preserve, MAX [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Preserve#aggr0]; none writeable. Revenue (EUR), (GBP), (INR) not shown to report authors [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Revenue (EUR)].
- Observation: "Preferred Currency" formula reads VALUEOF(NQ_SESSION."PREFERRED_CURRENCY") [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Preferred Currency]; to set beside whether the variables unit lists PREFERRED_CURRENCY as defined.
- Point to note: "Preferred Currency" is mapped in no source and picks Revenue, Revenue (EUR) or Revenue (GBP) by the value of PREFERRED_CURRENCY; Revenue (INR) is not among them [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Preferred Currency].
- Point to note: the table holds both "DISCNT_VALUE" (field, not shown, in the two Month/Year aggregate sources) and "Discnt Value" (measure, shown, in "Aggregate Revenue" and "SAMP_REVENUE_F") [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.DISCNT_VALUE] [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Discnt Value].
- To check in a later part: what "All Months Revenue" and "All Products Revenue" are mapped from, and how they differ from "Revenue" (mapping stated with each source).
- To check in a later part: what "Preserve" (MAX, only in "FactCrossJoin") is mapped from.
- To check in a later part: whether "BILL_MTH_KEY"/"Bill Mth Key" and "DISCNT_VALUE"/"Discnt Value" map the same database columns.
- Questions: Q2 what value PREFERRED_CURRENCY holds at run time and so which measure "Preferred Currency" shows

## table-01-fact-revenue-p2: Points to note (part 2 ended with "Points to note"; it held "Where the data comes from")
- Five sources, all active; priority group `1` only for "Aggregate Revenue" [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue]; not recorded for the others, e.g. [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.SAMP_REVENUE_F]. Each reads one database table: SAMP_REVENUE_A, SAMP_REVENUE_A_MTH, SAMP_REVENUE_A_YR, FactCrossJoin (a `select` query), SAMP_REVENUE_F.
- Revenue (EUR/GBP/INR) are REVENUE * 0.76 / 0.62 / 54.68 in "SAMP_REVENUE_F" [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.SAMP_REVENUE_F#map15], plain REVENUE in "Aggregate Revenue" [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue#map8]. Revenue, All Months Revenue, All Products Revenue all map REVENUE.
- Observation: the level of detail of source "SAMP_REVENUE_F" names levels "Detail" of "Sales Rep Hierarchy", "Dim Time Detail", "Office Detail", "Product Detail" [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.SAMP_REVENUE_F]; to set beside the sources of Fact Target and the drill paths.
- Observation: the level of detail of source "Aggregate Revenue" names "Month" of "Time Hierarchy" and "Product Detail" of "Products Hierararchy" [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue]; to set beside the section Drill paths.
- Observation: source "FactCrossJoin" maps "Preserve" from FactCrossJoin.Preserve, type DOUBLE [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.FactCrossJoin#map0]; to set beside the query of database table FactCrossJoin.
- Observation: source "Aggregate Revenue By Month" reads SAMP_REVENUE_A_MTH [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue By Month#table0]; to set beside the keys and joins of that database table.
- The three "To check in a later part" lines of part 1 are settled here: they became Q3 (All Months/All Products vs Revenue) and Q6 (Preserve); BILL_MTH_KEY/DISCNT_VALUE map columns of the month and year aggregate tables, written as a point to note.
- Questions: Q3 what distinguishes All Months Revenue and All Products Revenue from Revenue; Q4 what Revenue (EUR/GBP/INR) hold and why multiplied in one source only; Q5 which of the five sources OBIEE uses; Q6 what Preserve holds

## table-02-fact-target: Fact Target
- Fact Target holds two measures, Revenue and Units, both summed, both from the database table SAMP_TARGETS_F; 5 fields, all shown; no key recorded [kb:sampleapp::bmm::SAMPLESALES.Fact Target.src.SAMP_TARGETS_F].
- Both measures are writeable: users can type values in [kb:sampleapp::bmm::SAMPLESALES.Fact Target.Revenue] [kb:sampleapp::bmm::SAMPLESALES.Fact Target.Units].
- Observation: the level of detail of the source "SAMP_TARGETS_F" names "Dim Time Detail", "Office Detail", "Product Detail" and no level of "Sales Rep Hierarchy" [kb:sampleapp::bmm::SAMPLESALES.Fact Target.src.SAMP_TARGETS_F]; to set beside the source "SAMP_REVENUE_F" of Fact Revenue.
- Observation: source "SAMP_TARGETS_F" reads SAMP_TARGETS_F [kb:sampleapp::bmm::SAMPLESALES.Fact Target.src.SAMP_TARGETS_F#table0]; to set beside that database table's keys.
- Questions: Q7 what the target measures hold and what happens to typed values

## table-03-dim-offices: Dim Offices
- Dim Offices: 10 fields, no measures, 1 not shown (Sequence); one source "SAMP_OFFICES_D" reading the database table SAMP_OFFICES_D [kb:sampleapp::bmm::SAMPLESALES.Dim Offices.src.SAMP_OFFICES_D#table0].
- Primary key SAMP_OFFICES_D_Key on "Office Key" [kb:sampleapp::bmm::SAMPLESALES.Dim Offices.key.SAMP_OFFICES_D_Key].
- Observation: the level of detail of source "SAMP_OFFICES_D" is "Office Detail" of "Office Hierarchy" [kb:sampleapp::bmm::SAMPLESALES.Dim Offices.src.SAMP_OFFICES_D]; to set beside the section Drill paths.
- Questions: none

## table-04-dim-products: Dim Products
- Dim Products: 12 fields, no measures, 3 not shown (Attribute 1, Attribute 2, Sequence); one source "SAMP_PRODUCTS_D" reading SAMP_PRODUCTS_D [kb:sampleapp::bmm::SAMPLESALES.Dim Products.src.SAMP_PRODUCTS_D#table0]. Primary key on "Prod Key" [kb:sampleapp::bmm::SAMPLESALES.Dim Products.key.SAMP_PRODUCTS_D_Key].
- "Lob", "Prod Dsc", "Type" have descriptor IDs "Lob Key", "Prod Key", "Type Key" [kb:sampleapp::bmm::SAMPLESALES.Dim Products.Prod Dsc].
- Observation: "Prod Dsc (Translated)" is a Lookup(SPARSE ...) into "Dim Products (Translated)"."PROD_DESC" by "Prod Key" and VALUEOF(NQ_SESSION.WEBLANGUAGE) [kb:sampleapp::bmm::SAMPLESALES.Dim Products.Prod Dsc (Translated)]; to set beside the section Dim Products (Translated) and whether the variables unit lists WEBLANGUAGE as defined.
- Questions: Q8 what "Prod Dsc (Translated)" shows a user and which languages exist

## table-05-dim-products-translated: Dim Products (Translated)
- Dim Products (Translated): a lookup table [kb:sampleapp::bmm::SAMPLESALES.Dim Products (Translated)]; 3 fields (LANG_ID, PROD_DESC, PROD_KEY), none shown; primary key PK_PROD_TRANS1 on PROD_KEY, LANG_ID [kb:sampleapp::bmm::SAMPLESALES.Dim Products (Translated).key.PK_PROD_TRANS1]; one source reading SAMP_PRODUCTS_D_TRANSLATION, no level of detail recorded [kb:sampleapp::bmm::SAMPLESALES.Dim Products (Translated).src.SAMP_PRODUCTS_D_TRANSLATION].
- Observation: "PROD_DESC" of this table is mapped from SAMP_PRODUCTS_D_TRANSLATION.PROD_DESC [kb:sampleapp::bmm::SAMPLESALES.Dim Products (Translated).src.SAMP_PRODUCTS_D_TRANSLATION#map1]; to set beside the formula of "Prod Dsc (Translated)" of Dim Products.
- Questions: none (Q8 covers the language values; See also line added to Q8)

## table-06-dim-sales-rep: Dim Sales Rep
- Dim Sales Rep: 5 fields, all shown (Distance, Employee ID, Employee Name, Manager ID, Office ID), no measures; primary key EMP_ID on "Employee ID" [kb:sampleapp::bmm::SAMPLESALES.Dim Sales Rep.key.EMP_ID]; one source "LTS Employee" reading EMPLOYEE and EMP_PARENT_CHILD, named as parent-child closure table [kb:sampleapp::bmm::SAMPLESALES.Dim Sales Rep.src.LTS Employee].
- The source joins them inner on EMP_PARENT_CHILD.ANCESTOR_KEY = EMPLOYEE.EMP_ID [kb:sampleapp::bmm::SAMPLESALES.Dim Sales Rep.src.LTS Employee#table0.join0]; "Distance" comes from EMP_PARENT_CHILD.DISTANCE [kb:sampleapp::bmm::SAMPLESALES.Dim Sales Rep.src.LTS Employee#map4].
- Observation: the source "LTS Employee" names EMP_PARENT_CHILD as its parent-child closure table [kb:sampleapp::bmm::SAMPLESALES.Dim Sales Rep.src.LTS Employee]; to set beside the member, parent, distance and leaf columns named in the section Database table EMP_PARENT_CHILD and the parent-child drill path.
- Observation: the level of detail of "LTS Employee" is "Detail" of "Sales Rep Hierarchy" [kb:sampleapp::bmm::SAMPLESALES.Dim Sales Rep.src.LTS Employee]; to set beside the section Drill paths.
- Questions: Q9 what one row of Dim Sales Rep stands for and what Distance holds

## table-07-dim-time: Dim Time
- Dim Time: 6 fields, all shown, no measures; primary key SAMP_TIME_DAY_D_Key on "Calendar Date" [kb:sampleapp::bmm::SAMPLESALES.Dim Time.key.SAMP_TIME_DAY_D_Key]; two sources, "LTS Time Day" (SAMP_TIME_DAY_D, level "Dim Time Detail") [kb:sampleapp::bmm::SAMPLESALES.Dim Time.src.LTS Time Day] and "LTS Time Month" (SAMP_TIME_MTH_D, level "Month") [kb:sampleapp::bmm::SAMPLESALES.Dim Time.src.LTS Time Month]; no priority recorded for either.
- Every field's shown name is a VALUEOF(NQ_SESSION.CN_Sample_Sales_Dim_Time_...) formula [kb:sampleapp::pres::Sample Sales.Dim Time.Calendar Date].
- Observation: the shown names of the six fields read session variables CN_Sample_Sales_Dim_Time_Calendar_Date, _Per_Name_Half, _Per_Name_Month, _Per_Name_Qtr, _Per_Name_Week, _Per_Name_Year [kb:sampleapp::pres::Sample Sales.Dim Time.Calendar Date]; to set beside whether the variables unit lists them as defined.
- Questions: none new (See also line added to Q1)

## dbtable-01-employee: Database table EMPLOYEE
- EMPLOYEE: 4 columns, all mapped; key EMP_ID on column EMP_ID (the only key; not stated whether primary); read by "LTS Employee" of Dim Sales Rep [kb:sampleapp::phys::xe.SAMPLESALES.EMPLOYEE].
- Every column has external name `samplesales` [kb:sampleapp::phys::xe.SAMPLESALES.EMPLOYEE.EMP_ID]; joined by complex join "Complex Join#2" from FactCrossJoin and by foreign key EMP_PARENT_CHILD_FKey from EMP_PARENT_CHILD.
- Questions: none

## dbtable-02-emp-parent-child: Database table EMP_PARENT_CHILD
- EMP_PARENT_CHILD: 4 columns, 1 mapped (DISTANCE, by Dim Sales Rep "Distance"), 3 not (MEMBER_KEY, ANCESTOR_KEY, IS_LEAF); key EMP_PARENT_CHILD_Key on MEMBER_KEY (the only key; not stated whether primary); read by "LTS Employee" of Dim Sales Rep; object name EMP_PARENT_CHILD [kb:sampleapp::phys::xe.SAMPLESALES.EMP_PARENT_CHILD].
- Observation: it is recorded as the relationship table of a parent-child hierarchy, member MEMBER_KEY, parent ANCESTOR_KEY, distance DISTANCE, leaf IS_LEAF [kb:sampleapp::phys::xe.SAMPLESALES.EMP_PARENT_CHILD]; to set beside the source "LTS Employee" of Dim Sales Rep and the parent-child drill path.
- Observation: foreign key SAMP_REVENUE_F_FKey#4 from SAMP_REVENUE_F.EMPLOYEE_KEY points at MEMBER_KEY (pointer only, no marker); to set beside the section How the tables relate.
- Questions: none new (See also line added to Q9)

## dbtable-03-factcrossjoin: Database table FactCrossJoin
- FactCrossJoin: a `select` query, not a database table, SQL select 1 Preserve from dual [kb:sampleapp::phys::xe.SAMPLESALES.FactCrossJoin.def.DefaultMulDB]; 1 column (Preserve, mapped by Fact Revenue "Preserve"); no key recorded; read by source "FactCrossJoin" of Fact Revenue [kb:sampleapp::phys::xe.SAMPLESALES.FactCrossJoin].
- Complex joins "Complex Join", "Complex Join#1", "Complex Join#2" to SAMP_OFFICES_D, SAMP_PRODUCTS_D, EMPLOYEE (pointers; stated in the joins unit).
- Observation: the SQL of FactCrossJoin is select 1 Preserve from dual [kb:sampleapp::phys::xe.SAMPLESALES.FactCrossJoin.def.DefaultMulDB]; to set beside the source "FactCrossJoin" of Fact Revenue mapping "Preserve".
- Questions: none new (See also line added to Q6; Q6 not settled: the query gives the value, not the purpose)

## dbtable-04-samp-offices-d: Database table SAMP_OFFICES_D
- SAMP_OFFICES_D: 10 columns, all mapped by Dim Offices; key SAMP_OFFICES_D_Key on OFFICE_KEY (the only key; not stated whether primary); read by source "SAMP_OFFICES_D" of Dim Offices [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_OFFICES_D].
- Foreign keys from SAMP_REVENUE_F and SAMP_TARGETS_F (OFFICE_KEY) point at it; complex join "Complex Join" from FactCrossJoin. External name `samplesales` on 9 of 10 columns, not recorded on AREA_NAME [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_OFFICES_D.AREA_NAME].
- Questions: none

## dbtable-05-samp-products-d: Database table SAMP_PRODUCTS_D
- SAMP_PRODUCTS_D: 11 columns, all mapped by Dim Products; key SAMP_PRODUCTS_D_Key on PROD_KEY (the only key; not stated whether primary); read by source "SAMP_PRODUCTS_D" of Dim Products [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_PRODUCTS_D].
- Four foreign keys point at PROD_KEY (from SAMP_PRODUCTS_D_TRANSLATION, SAMP_REVENUE_A, SAMP_REVENUE_F, SAMP_TARGETS_F) [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_PRODUCTS_D.key.SAMP_PRODUCTS_D_Key]; complex join "Complex Join#1" from FactCrossJoin.
- Questions: none

## dbtable-06-samp-products-d-translation: Database table SAMP_PRODUCTS_D_TRANSLATION
- SAMP_PRODUCTS_D_TRANSLATION: 3 columns (LANG_ID, PROD_DESC, PROD_KEY), all mapped by Dim Products (Translated); key PK_PROD_TRANS1 on LANG_ID, PROD_KEY (the only key; not stated whether primary); read by source "SAMP_PRODUCTS_D_TRANSLATION" of Dim Products (Translated); object name SAMP_PRODUCTS_D_TRANSLATION [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_PRODUCTS_D_TRANSLATION].
- Foreign key SAMP_PRODUCTS_D_TRANSLATION_FKey from PROD_KEY to SAMP_PRODUCTS_D.PROD_KEY (pointer).
- Questions: none

## dbtable-07-samp-revenue-a: Database table SAMP_REVENUE_A
- SAMP_REVENUE_A: 5 columns, all mapped by Fact Revenue; no key recorded; read by source "Aggregate Revenue" of Fact Revenue; object name SAMP_REVENUE_A [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A].
- Column REVENUE is mapped by six fields: Revenue, All Products Revenue, All Months Revenue, Revenue (GBP), Revenue (EUR), Revenue (INR) [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A.REVENUE]. Foreign keys from BILL_MTH_KEY to SAMP_TIME_MTH_D.MTH_KEY and PROD_KEY to SAMP_PRODUCTS_D (pointers).
- Questions: none new (Q3, Q4 cover the six mappings)

## dbtable-08-samp-revenue-a-mth: Database table SAMP_REVENUE_A_MTH
- SAMP_REVENUE_A_MTH: 4 columns (BILL_MTH_KEY, DISCNT_VALUE, REVENUE, UNITS), all mapped by Fact Revenue (BILL_MTH_KEY, DISCNT_VALUE, Revenue, Units); no key recorded; read by source "Aggregate Revenue By Month" of Fact Revenue [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A_MTH].
- Foreign key from BILL_MTH_KEY to SAMP_TIME_MTH_D.MTH_KEY (pointer).
- Questions: none

## dbtable-09-samp-revenue-a-yr: Database table SAMP_REVENUE_A_YR
- SAMP_REVENUE_A_YR: 4 columns (BILL_YEAR_KEY, DISCNT_VALUE, REVENUE, UNITS), all mapped by Fact Revenue; no key recorded; read by source "Aggregate Revenue by Year" of Fact Revenue; object name SAMP_REVENUE_A_YR [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A_YR].
- Observation: foreign key "SAMP_REVENUE_A_YR_Foreign Key" goes from BILL_YEAR_KEY to SAMP_TIME_MTH_D.CAL_YEAR (pointer only, no marker); to set beside the keys of SAMP_TIME_MTH_D and the section How the tables relate.
- Questions: none

## dbtable-10-samp-revenue-f: Database table SAMP_REVENUE_F
- SAMP_REVENUE_F: 12 columns, all mapped by Fact Revenue; no key recorded; read by source "SAMP_REVENUE_F" of Fact Revenue; object name SAMP_REVENUE_F [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F].
- Five foreign keys out: BILL_MTH_KEY to SAMP_TIME_MTH_D.MTH_KEY, OFFICE_KEY to SAMP_OFFICES_D, PROD_KEY to SAMP_PRODUCTS_D, BILL_DAY_DT to SAMP_TIME_DAY_D.CALENDAR_DATE, EMPLOYEE_KEY to EMP_PARENT_CHILD.MEMBER_KEY (pointers). Column REVENUE mapped by six fields [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F.REVENUE].
- Questions: none

## dbtable-11-samp-targets-f: Database table SAMP_TARGETS_F
- SAMP_TARGETS_F: 5 columns (BILL_MTH_KEY, OFFICE_KEY, PROD_KEY, REVENUE, UNITS), all mapped by Fact Target; no key recorded; read by source "SAMP_TARGETS_F" of Fact Target; object name SAMP_TARGETS_F [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TARGETS_F].
- Observation: the table has no day or date column and no employee column; its month column BILL_MTH_KEY has a foreign key to SAMP_TIME_MTH_D.MTH_KEY [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TARGETS_F.BILL_MTH_KEY]; to set beside the level of detail of source "SAMP_TARGETS_F" of Fact Target ("Dim Time Detail").
- Questions: none

## dbtable-12-samp-time-day-d: Database table SAMP_TIME_DAY_D
- SAMP_TIME_DAY_D: 14 columns, 6 mapped by Dim Time, 8 not (DAY_KEY, DAY_OF_MONTH, DAY_OF_WEEK, DAY_OF_YEAR, JULIAN_DAY_NUM, JULIAN_MONTH_NUM, JULIAN_QTR_NUM, JULIAN_WEEK_NUM); key SAMP_TIME_DAY_D_Key on CALENDAR_DATE (the only key; not stated whether primary); read by source "LTS Time Day" of Dim Time; object name SAMP_TIME_DAY_D [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_DAY_D].
- Foreign key from SAMP_REVENUE_F.BILL_DAY_DT points at CALENDAR_DATE (pointer).
- Questions: none

## dbtable-13-samp-time-mth-d: Database table SAMP_TIME_MTH_D
- SAMP_TIME_MTH_D: 36 columns, 4 mapped by Dim Time (PER_NAME_HALF, PER_NAME_MONTH, PER_NAME_QTR, PER_NAME_YEAR), 32 not; two keys, SAMP_TIME_MTH_D_Key on MTH_KEY and SAMP_TIME_MTH_D_Key#1 on CAL_YEAR (not stated which is primary); read by source "LTS Time Month" of Dim Time [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D].
- Four foreign keys point at MTH_KEY [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.key.SAMP_TIME_MTH_D_Key]; one (from SAMP_REVENUE_A_YR) at CAL_YEAR [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.key.SAMP_TIME_MTH_D_Key#1]. MTH_KEY itself is mapped by no field.
- Observation: MTH_KEY, the column four foreign keys point at, is not mapped by any field of this subject area [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.MTH_KEY]; to set beside the fields of Dim Time and its key "Calendar Date".
- Questions: Q10 whether CAL_YEAR identifies one row of SAMP_TIME_MTH_D

## joins: How the tables relate
- 7 business-model joins, all Inner: Dim Time to Fact Target and to Fact Revenue; Dim Sales Rep (1..1) to Fact Revenue [kb:sampleapp::bmm::joins.Relationship_2004:17511354089145]; Fact Revenue and Fact Target each to Dim Offices and Dim Products. No business-model join between Fact Target and Dim Sales Rep (counted).
- 17 database joins: 3 complex joins from FactCrossJoin with condition `1=1` [kb:sampleapp::phys::joins.Complex Join], 14 foreign keys.
- Observation: the business model has no join between "Fact Target" and "Dim Sales Rep" (section How the tables relate, counted, no Row identifier); to set beside the level of detail of source "SAMP_TARGETS_F", which names no level of "Sales Rep Hierarchy".
- Observation: foreign key SAMP_REVENUE_F_FKey#4 joins SAMP_REVENUE_F.EMPLOYEE_KEY (INT) to EMP_PARENT_CHILD.MEMBER_KEY (DOUBLE) [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F.SAMP_REVENUE_F_FKey#4]; to set beside the join of source "LTS Employee", which pairs EMPLOYEE.EMP_ID with ANCESTOR_KEY.
- Observation: foreign key SAMP_REVENUE_A_YR_Foreign Key joins BILL_YEAR_KEY to SAMP_TIME_MTH_D.CAL_YEAR [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A_YR.SAMP_REVENUE_A_YR_Foreign Key]; to set beside the key SAMP_TIME_MTH_D_Key#1 on CAL_YEAR.
- Questions: none new (See also line added to Q6)

## hierarchies: Drill paths
- Four drill paths, each offered to report authors: "Office Hierarchy" (5 levels, Dim Offices Total to Office Detail) [kb:sampleapp::bmm::SAMPLESALES.Office Hierarchy]; "Products Hierararchy" (5 levels, Dim Products Total to Product Detail) [kb:sampleapp::bmm::SAMPLESALES.Products Hierararchy]; "Sales Rep Hierarchy" (parent-child, 2 levels, Total and Detail) [kb:sampleapp::bmm::SAMPLESALES.Sales Rep Hierarchy]; "Time Hierarchy" (time dimension, 7 levels, Dim Time Total to Dim Time Detail) [kb:sampleapp::bmm::SAMPLESALES.Time Hierarchy].
- "Sales Rep Hierarchy" is offered with 0 levels, shown by "Employee Name" [kb:sampleapp::pres::Sample Sales.Dim Sales Rep.Sales Rep Hierarchy].
- Observation: "Sales Rep Hierarchy" is parent-child, its relationship table EMP_PARENT_CHILD with member MEMBER_KEY, parent ANCESTOR_KEY, distance DISTANCE, leaf IS_LEAF [kb:sampleapp::bmm::SAMPLESALES.Sales Rep Hierarchy]; to set beside the section Database table EMP_PARENT_CHILD.
- Observation: level "Month" of "Time Hierarchy" has key column "Per Name Month" [kb:sampleapp::bmm::SAMPLESALES.Time Hierarchy.Month]; to set beside the sources "Aggregate Revenue" and "Aggregate Revenue By Month" of Fact Revenue, whose level of detail names "Month".
- Questions: Q11 whether the member counts of Time Hierarchy are current

## variables: Values set per user or for the whole system
- 10 variable names used inside VALUEOF(...); 1 defined in the repository (PREFERRED_CURRENCY), 9 used by name and not defined (seven CN_Sample_Sales_Dim_Time names, PREFERED_CURRENCY, WEBLANGUAGE) (counted, no Row identifier).
- PREFERRED_CURRENCY: session variable, default `'gc1'` [kb:sampleapp::var::PREFERRED_CURRENCY]; set by init block "initBlockPrefCurrency", query select 'VALUEOF(NQ_SESSION.PREFERED_CURRENCY)' from dual [kb:sampleapp::var::init.initBlockPrefCurrency].
- Observation: PREFERRED_CURRENCY is defined in the repository as a session variable with default 'gc1' [kb:sampleapp::var::PREFERRED_CURRENCY]; to set beside the formula of "Preferred Currency" of Fact Revenue.
- Observation: the init block's query names PREFERED_CURRENCY (one R), used by name and not defined in the repository (section Values set per user or for the whole system; no Row identifier); to set beside the formula of "Preferred Currency", which reads PREFERRED_CURRENCY.
- Observation: CN_Sample_Sales_Dim_Time and the six CN_Sample_Sales_Dim_Time_... names are used by name, not defined in the repository (section Values set per user or for the whole system; no Row identifier); to set beside the display names of Dim Time and its fields.
- Observation: WEBLANGUAGE is used by name, not defined in the repository (section Values set per user or for the whole system; no Row identifier); to set beside the formula of "Prod Dsc (Translated)".
- Questions: none new (See also lines added to Q1, Q2, Q8)

## security: Security
- No row filter recorded. 6 object permissions, all on "Revenue" and "Units" of Fact Target: write through PrivPack_4209:18101355661677 held by "BI Author Role" [kb:sampleapp::sec::obj.ObjPriv_1500:18131355661677.o0]; read and write through two packages with no role recorded [kb:sampleapp::sec::obj.ObjPriv_1500:18161355661677.o0].
- One role: "BI Author Role", guid BIAuthor, holds query privilege QueryPriv_4204:18331355753308 [kb:sampleapp::sec::pkg.PrivPack_4209:18101355661677.q0].
- Observation: "write" on "Revenue" of Fact Target is given to "BI Author Role" [kb:sampleapp::sec::obj.ObjPriv_1500:18131355661677.o0]; to set beside the measure "Revenue" of Fact Target recorded as writeable.
- Questions: Q12 who holds the two privilege packages with no role recorded

## reports: Reports built on this subject area
- The estate has no Web Catalog half, so reports and dashboards were not examined; calculations, filters and prompts inside reports are unknown (manifest, no Row identifier).
- Questions: none

## gaps: What the export did not capture
- No errors about objects of this subject area. 95 unparsed rows, 1769 occurrences (78 unread_attribute, 15 unread_element, 2 unread_text); left out of the Slice: 15 unmodelled_type rows (LogicalSourceFolder 11, QueryPrivilege 1, User 3) (no Row identifier).
- Questions: Q13 whether unread items of sources and privilege packages hold conditions or holders this document should state

## review

- Correction (table-01-fact-revenue-p1): removed, the packet does not say it: "that number picks one of the three fields listed after it" [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Preferred Currency]; supersedes the note "to be matched"
- Correction (table-01-fact-revenue-p2): "Preferred Currency" is mapped in no source; its formula names the variable PREFERRED_CURRENCY and the fields "Revenue", "Revenue (EUR)" and "Revenue (GBP)" [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Preferred Currency]; no longer: "picks Revenue, Revenue (EUR) or Revenue (GBP) by the value"; supersedes: to be matched
- Observation: the measures "Revenue" and "Units" of Fact Target are the columns REVENUE and UNITS of SAMP_TARGETS_F [kb:sampleapp::bmm::SAMPLESALES.Fact Target.src.SAMP_TARGETS_F#map3]; to set beside the measures "Revenue" and "Units" of Fact Revenue, which carry the same names (moved here by the review from the section Fact Target)
- Correction (table-02-fact-target): removed, the packet does not say it: "The measures carry the same names, Revenue and Units, as two measures of Fact Revenue"; supersedes: to be matched
- Correction (table-04-dim-products): "Prod Dsc (Translated)" is mapped in no source; its formula starts with Lookup and names PROD_DESC of "Dim Products (Translated)", "Prod Dsc", "Prod Key" and the variable WEBLANGUAGE [kb:sampleapp::bmm::SAMPLESALES.Dim Products.Prod Dsc (Translated)]; no longer: "it is a lookup into the business table Dim Products (Translated) by Prod Key"; supersedes: to be matched
- Correction (table-05-dim-products-translated): the formula of "Prod Dsc (Translated)" of Dim Products names this table's field PROD_DESC (pointer to the section Dim Products); no longer: "The field Prod Dsc (Translated) of Dim Products reads it"; supersedes: to be matched
- Correction (joins): the foreign key SAMP_REVENUE_A_YR_Foreign Key goes from SAMP_REVENUE_A_YR.BILL_YEAR_KEY to SAMP_TIME_MTH_D.CAL_YEAR [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A_YR.SAMP_REVENUE_A_YR_Foreign Key]; no longer: "goes from the year column BILL_YEAR_KEY"; supersedes: to be matched
- Correction (hierarchies): for every key of "Office Hierarchy" and of "Products Hierararchy", chronological key: not recorded [kb:sampleapp::bmm::SAMPLESALES.Office Hierarchy] [kb:sampleapp::bmm::SAMPLESALES.Products Hierararchy]; no longer: "No key is chronological"; supersedes: to be matched
- Observation: permission write on the field "Revenue" of Fact Target is given through the privilege package PrivPack_4209:18101355661677 [kb:sampleapp::sec::obj.ObjPriv_1500:18131355661677.o0]; to set beside the measure "Revenue" of Fact Target, recorded as writeable true (moved here by the review from the section Security)
- Correction (security): removed, the packet does not say it: "the two measures recorded as writeable"; supersedes: to be matched
- Correction (dbtable-02-emp-parent-child): added: whether MEMBER_KEY holds one row per value of EMP_PARENT_CHILD is not recorded (open question Q14) [kb:sampleapp::phys::xe.SAMPLESALES.EMP_PARENT_CHILD.key.EMP_PARENT_CHILD_Key]; supersedes no note
- Correction (table-01-fact-revenue-p1) supersedes the note "Point to note: "Preferred Currency" is mapped in no source and picks"
- Correction (table-01-fact-revenue-p2) supersedes the note "Point to note: "Preferred Currency" is mapped in no source and picks"
- Correction (table-02-fact-target) supersedes no note
- Correction (table-04-dim-products) supersedes the note "Observation: "Prod Dsc (Translated)" is a Lookup(SPARSE ...) into"
- Correction (table-05-dim-products-translated) supersedes no note
- Correction (joins) supersedes no note
- Correction (hierarchies) supersedes no note
- Correction (security) supersedes no note
- Correction (dbtable-02-emp-parent-child) supersedes no note
- Correction (table-07-dim-time): the name shown to users of every field of Dim Time is a VALUEOF formula that names a variable [kb:sampleapp::pres::Sample Sales.Dim Time.Calendar Date]; no longer: "a VALUEOF formula of a session variable"; supersedes the note "Every field's shown name is a VALUEOF(NQ_SESSION.CN_Sample_Sales_Dim_Time_...) formula"
- Correction (table-07-dim-time) supersedes the note "Observation: the shown names of the six fields read session variables"
