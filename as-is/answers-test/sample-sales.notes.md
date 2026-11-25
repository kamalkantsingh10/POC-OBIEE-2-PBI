# Notes for the summary
## overview: What report authors see
- "Sample Sales" is built on the business model "SAMPLESALES"; six tables report authors see, 49 fields of which 8 measures; when a query names no measure OBIEE uses "Fact Target"."Revenue" [kb:sampleapp::pres::Sample Sales].
- Knowledge Base extracted 2026-11-23T20:55:33Z; only the rpd half extracted; no Web Catalog half, so reports and dashboards were not examined (manifest; no Row identifier).
- The shown name of "Dim Time" is a VALUEOF formula [kb:sampleapp::pres::Sample Sales.Dim Time].
- Questions: Q1 kept (name shown for Dim Time); none new
## table-01-fact-revenue-p1: Measures and fields report authors do not see
- Fact Revenue: 22 fields, 9 measures, 7 not shown, five sources, no key recorded [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue].
- "Revenue (EUR)", "Revenue (GBP)", "Revenue (INR)" are not shown to report authors [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Revenue (EUR)].
- Point to note: "Revenue", "All Products Revenue" and "All Months Revenue" are all summarised with SUM [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Revenue#aggr0] [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.All Products Revenue#aggr0] [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.All Months Revenue#aggr0].
- Point to note: "Preferred Currency" is mapped in no source; its formula names PREFERRED_CURRENCY and "Revenue", "Revenue (EUR)", "Revenue (GBP)", not "Revenue (INR)" [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Preferred Currency].
- Point to note: the table holds "DISCNT_VALUE" (not shown) and "Discnt Value" (measure, shown), and "BILL_MTH_KEY", "BILL_YEAR_KEY" not shown [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.DISCNT_VALUE] [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Discnt Value] [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.BILL_MTH_KEY] [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.BILL_YEAR_KEY].
- Observation: "Preserve" is shown to report authors [kb:sampleapp::pres::Sample Sales.Fact Revenue.Preserve]; set beside the answer to Q6 that it is never shown in a report.
- Questions: Q2 kept (PREFERRED_CURRENCY at run time); none new
## table-01-fact-revenue-p2: Points to note
- Five sources; only "Aggregate Revenue" has a priority group (1) [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue]; "FactCrossJoin" has no level of detail [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.FactCrossJoin].
- "Revenue (EUR/GBP/INR)" are REVENUE multiplied by 0.76, 0.62, 54.68 in "SAMP_REVENUE_F" [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.SAMP_REVENUE_F#map15] and plain REVENUE in "Aggregate Revenue" [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue#map8]; the multipliers are exchange rates of 2014-01-01 and "Aggregate Revenue" already holds converted amounts [answer:Q4].
- "Preserve" always holds 1, only keeps rows in queries, never shown in a report [answer:Q6].
- Questions: Q3, Q5, Q15 kept open; Q4 and Q6 answered and applied; none new
## table-02-fact-target: Fact Target
- Fact Target holds two measures, Revenue and Units, both summed, both from the database table SAMP_TARGETS_F [kb:sampleapp::bmm::SAMPLESALES.Fact Target.src.SAMP_TARGETS_F].
- Both measures are writeable [kb:sampleapp::bmm::SAMPLESALES.Fact Target.Revenue] [kb:sampleapp::bmm::SAMPLESALES.Fact Target.Units].
- Observation: the level of detail of "SAMP_TARGETS_F" names no level of "Sales Rep Hierarchy" [kb:sampleapp::bmm::SAMPLESALES.Fact Target.src.SAMP_TARGETS_F]; set beside the source "SAMP_REVENUE_F" of Fact Revenue.
- Questions: Q7 kept; none new
## table-03-dim-offices: Dim Offices
- Dim Offices: 10 fields, no measures, keyed on "Office Key" (primary key true) [kb:sampleapp::bmm::SAMPLESALES.Dim Offices.key.SAMP_OFFICES_D_Key]; one source reading SAMP_OFFICES_D [kb:sampleapp::bmm::SAMPLESALES.Dim Offices.src.SAMP_OFFICES_D#table0].
- Observation: the level of detail of "SAMP_OFFICES_D" is "Office Detail" of "Office Hierarchy" [kb:sampleapp::bmm::SAMPLESALES.Dim Offices.src.SAMP_OFFICES_D]; set beside the drill path "Office Hierarchy".
- Questions: none
## table-04-dim-products: Dim Products
- Dim Products: 12 fields, no measures, keyed on "Prod Key" (primary key true) [kb:sampleapp::bmm::SAMPLESALES.Dim Products.key.SAMP_PRODUCTS_D_Key]; one source reading SAMP_PRODUCTS_D [kb:sampleapp::bmm::SAMPLESALES.Dim Products.src.SAMP_PRODUCTS_D#table0].
- Observation: "Prod Dsc (Translated)" reads WEBLANGUAGE and names "PROD_DESC" of "Dim Products (Translated)" [kb:sampleapp::bmm::SAMPLESALES.Dim Products.Prod Dsc (Translated)]; set beside the variables unit and Dim Products (Translated).
- Questions: Q8 kept; none new
## table-05-dim-products-translated: Dim Products (Translated)
- A lookup table, 3 fields none shown, keyed on PROD_KEY and LANG_ID [kb:sampleapp::bmm::SAMPLESALES.Dim Products (Translated).key.PK_PROD_TRANS1]; reads SAMP_PRODUCTS_D_TRANSLATION [kb:sampleapp::bmm::SAMPLESALES.Dim Products (Translated).src.SAMP_PRODUCTS_D_TRANSLATION#table0].
- Observation: "PROD_DESC" is mapped from SAMP_PRODUCTS_D_TRANSLATION.PROD_DESC [kb:sampleapp::bmm::SAMPLESALES.Dim Products (Translated).src.SAMP_PRODUCTS_D_TRANSLATION#map1]; set beside the formula of "Prod Dsc (Translated)" of Dim Products.
- Questions: none new (Q8 covers the languages)
## table-06-dim-sales-rep: Dim Sales Rep
- Dim Sales Rep: 5 fields, keyed on "Employee ID" [kb:sampleapp::bmm::SAMPLESALES.Dim Sales Rep.key.EMP_ID]; source "LTS Employee" joins EMPLOYEE to EMP_PARENT_CHILD on ANCESTOR_KEY [kb:sampleapp::bmm::SAMPLESALES.Dim Sales Rep.src.LTS Employee#table0.join0]; "Distance" from EMP_PARENT_CHILD.DISTANCE [kb:sampleapp::bmm::SAMPLESALES.Dim Sales Rep.src.LTS Employee#map4].
- Observation: the source names EMP_PARENT_CHILD as the parent-child table [kb:sampleapp::bmm::SAMPLESALES.Dim Sales Rep.src.LTS Employee]; its level of detail is "Detail" of "Sales Rep Hierarchy"; set beside the drill path and the EMP_PARENT_CHILD table.
- Questions: Q9 kept; none new
## table-07-dim-time: Dim Time
- Dim Time: 6 fields, keyed on "Calendar Date" [kb:sampleapp::bmm::SAMPLESALES.Dim Time.key.SAMP_TIME_DAY_D_Key]; sources "LTS Time Day" (SAMP_TIME_DAY_D) and "LTS Time Month" (SAMP_TIME_MTH_D) [kb:sampleapp::bmm::SAMPLESALES.Dim Time.src.LTS Time Month#table0].
- Observation: the shown name of "Calendar Date" is VALUEOF(NQ_SESSION.CN_Sample_Sales_Dim_Time_Calendar_Date) [kb:sampleapp::pres::Sample Sales.Dim Time.Calendar Date]; set beside the variables unit.
- Questions: none new (Q1 covers the shown names)
## dbtable-01-employee: Database table EMPLOYEE
- EMPLOYEE: 4 columns, key EMP_ID on EMP_ID, read by "LTS Employee" of Dim Sales Rep [kb:sampleapp::phys::xe.SAMPLESALES.EMPLOYEE].
- Questions: none
## dbtable-02-emp-parent-child: Database table EMP_PARENT_CHILD
- EMP_PARENT_CHILD: 4 columns, key EMP_PARENT_CHILD_Key on MEMBER_KEY, read by "LTS Employee" of Dim Sales Rep; the relationship table of a parent-child hierarchy, member MEMBER_KEY, parent ANCESTOR_KEY, distance DISTANCE, leaf IS_LEAF [kb:sampleapp::phys::xe.SAMPLESALES.EMP_PARENT_CHILD].
- Observation: EMP_PARENT_CHILD is recorded as the relationship table [kb:sampleapp::phys::xe.SAMPLESALES.EMP_PARENT_CHILD]; set beside the source "LTS Employee" that names it.
- Questions: Q14 kept; none new
## dbtable-03-factcrossjoin: Database table FactCrossJoin
- FactCrossJoin: a query kept in the repository, select 1 Preserve from dual [kb:sampleapp::phys::xe.SAMPLESALES.FactCrossJoin.def.DefaultMulDB]; 1 column, no key recorded, read by "FactCrossJoin" of Fact Revenue [kb:sampleapp::phys::xe.SAMPLESALES.FactCrossJoin].
- Observation: the SQL is select 1 Preserve from dual [kb:sampleapp::phys::xe.SAMPLESALES.FactCrossJoin.def.DefaultMulDB]; set beside the source "FactCrossJoin" mapping "Preserve".
- Questions: none new (Q6 answered, applied)
## dbtable-04-samp-offices-d: Database table SAMP_OFFICES_D
- SAMP_OFFICES_D: 10 columns, key SAMP_OFFICES_D_Key on OFFICE_KEY, read by "SAMP_OFFICES_D" of Dim Offices [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_OFFICES_D].
- Questions: none
## dbtable-05-samp-products-d: Database table SAMP_PRODUCTS_D
- SAMP_PRODUCTS_D: 11 columns, key SAMP_PRODUCTS_D_Key on PROD_KEY, read by "SAMP_PRODUCTS_D" of Dim Products [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_PRODUCTS_D].
- Questions: none
## dbtable-06-samp-products-d-translation: Database table SAMP_PRODUCTS_D_TRANSLATION
- SAMP_PRODUCTS_D_TRANSLATION: 3 columns, key PK_PROD_TRANS1 on LANG_ID, PROD_KEY, read by "SAMP_PRODUCTS_D_TRANSLATION" of Dim Products (Translated) [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_PRODUCTS_D_TRANSLATION].
- Questions: none
## dbtable-07-samp-revenue-a: Database table SAMP_REVENUE_A
- SAMP_REVENUE_A: 5 columns, no key recorded, read by "Aggregate Revenue" of Fact Revenue [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A]; REVENUE mapped by six fields [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A.REVENUE].
- Questions: none new (Q3, Q15 open; Q4 answered, applied)
## dbtable-08-samp-revenue-a-mth: Database table SAMP_REVENUE_A_MTH
- SAMP_REVENUE_A_MTH: 4 columns, no key recorded, read by "Aggregate Revenue By Month" of Fact Revenue [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A_MTH].
- Questions: none
## dbtable-09-samp-revenue-a-yr: Database table SAMP_REVENUE_A_YR
- SAMP_REVENUE_A_YR: 4 columns, no key recorded, read by "Aggregate Revenue by Year" of Fact Revenue [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A_YR].
- Questions: none new (Q16 pointed to)
## dbtable-10-samp-revenue-f: Database table SAMP_REVENUE_F
- SAMP_REVENUE_F: 12 columns, no key recorded, read by "SAMP_REVENUE_F" of Fact Revenue [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F].
- Questions: none new
## dbtable-11-samp-targets-f: Database table SAMP_TARGETS_F
- SAMP_TARGETS_F: 5 columns, no key recorded, read by "SAMP_TARGETS_F" of Fact Target [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TARGETS_F].
- Questions: none
## dbtable-12-samp-time-day-d: Database table SAMP_TIME_DAY_D
- SAMP_TIME_DAY_D: 14 columns, key SAMP_TIME_DAY_D_Key on CALENDAR_DATE, read by "LTS Time Day" of Dim Time [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_DAY_D].
- Questions: none
## dbtable-13-samp-time-mth-d: Database table SAMP_TIME_MTH_D
- SAMP_TIME_MTH_D: 36 columns, keys SAMP_TIME_MTH_D_Key on MTH_KEY and SAMP_TIME_MTH_D_Key#1 on CAL_YEAR, read by "LTS Time Month" of Dim Time [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D].
- One row per month; CAL_YEAR repeats twelve times [answer:Q10].
- Observation: a key on CAL_YEAR alone [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.key.SAMP_TIME_MTH_D_Key#1]; set beside the foreign key SAMP_REVENUE_A_YR_Foreign Key in the joins unit.
- Observation: MTH_KEY, at which four foreign keys point, is not mapped by any field [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.MTH_KEY]; set beside the key of Dim Time.
- Questions: Q16 kept open; Q10 answered and applied; none new
## joins: How the tables relate
- 7 joins between business tables, all Inner; no join between Fact Target and Dim Sales Rep (counted) [kb:sampleapp::bmm::joins.Relationship_2004:11061353928600].
- 17 database joins: 3 complex joins from FactCrossJoin with condition 1=1 [kb:sampleapp::phys::joins.Complex Join], 14 foreign keys.
- Observation: SAMP_REVENUE_A_YR_Foreign Key goes to SAMP_TIME_MTH_D.CAL_YEAR [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A_YR.SAMP_REVENUE_A_YR_Foreign Key]; set beside the key on CAL_YEAR of SAMP_TIME_MTH_D.
- Observation: SAMP_REVENUE_F_FKey#4 joins SAMP_REVENUE_F.EMPLOYEE_KEY (INT) to EMP_PARENT_CHILD.MEMBER_KEY (DOUBLE) [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F.SAMP_REVENUE_F_FKey#4]; set beside the source "LTS Employee" joining on ANCESTOR_KEY.
- Questions: none new (Q16 pointed to; Q6, Q10 answers applied)
## hierarchies: Drill paths
- Four drill paths, all offered to report authors: "Office Hierarchy" [kb:sampleapp::bmm::SAMPLESALES.Office Hierarchy], "Products Hierararchy" [kb:sampleapp::bmm::SAMPLESALES.Products Hierararchy], parent-child "Sales Rep Hierarchy" [kb:sampleapp::bmm::SAMPLESALES.Sales Rep Hierarchy], "Time Hierarchy" (7 levels, a time dimension) [kb:sampleapp::bmm::SAMPLESALES.Time Hierarchy].
- Observation: the level "Month" of "Time Hierarchy" has the key column "Per Name Month" [kb:sampleapp::bmm::SAMPLESALES.Time Hierarchy.Month]; set beside the level of detail of "Aggregate Revenue".
- Questions: Q11 kept; none new
## variables: Values set per user or for the whole system
- PREFERRED_CURRENCY: a session variable, default 'gc1', set by init block "initBlockPrefCurrency" whose query names PREFERED_CURRENCY [kb:sampleapp::var::PREFERRED_CURRENCY] [kb:sampleapp::var::init.initBlockPrefCurrency].
- 9 names used and not defined in the repository: seven CN_Sample_Sales_Dim_Time names, PREFERED_CURRENCY, WEBLANGUAGE (no Row identifier).
- Questions: none new (Q1, Q2, Q8 pointed to)
## security: Security
- No row filter; 6 permissions, all on "Revenue" and "Units" of Fact Target; "write" on both given to "BI Author Role" through PrivPack_4209:18101355661677 [kb:sampleapp::sec::obj.ObjPriv_1500:18131355661677.o0].
- The two other privilege packages are also held by BI Author Role [answer:Q12].
- Observation: "write" on "Revenue" of Fact Target is given to "BI Author Role" [kb:sampleapp::sec::obj.ObjPriv_1500:18131355661677.o0]; set beside the measure being writeable.
- Questions: none new (Q12 answered, applied; Q7 pointed to)
## reports: Reports built on this subject area
- Not examined: the estate has no Web Catalog half (manifest; no Row identifier).
- Questions: none
## gaps: What the export did not capture
- No error recorded; 95 kinds of source content not read (1769 occurrences); 15 objects of three types with no reader left out (LogicalSourceFolder 11, QueryPrivilege 1, User 3) (no Row identifier).
- Questions: Q13 kept; none new
