# Notes

## as-is
(All as-is questions are open; none answered. Which bear on this tab is checked unit by unit.)
- as-is Q1 open: display name of "Dim Time" and its six fields is VALUEOF(NQ_SESSION.CN_Sample_Sales_Dim_Time...); bears on Dim Time.
- as-is Q2 open: PREFERRED_CURRENCY run-time value; "Preferred Currency" of Fact Revenue (INDEXCOL).
- as-is Q3 open: what distinguishes "All Months Revenue"/"All Products Revenue" from "Revenue" (Fact Revenue).
- as-is Q4 open: Revenue (EUR/GBP/INR) multiplied by 0.76/0.62/54.68 in SAMP_REVENUE_F source only (Fact Revenue).
- as-is Q5 open: which of the five sources of Fact Revenue OBIEE uses; priority groups not recorded except Aggregate Revenue (1).
- as-is Q6 open: meaning of "Preserve" (FactCrossJoin, select 1 Preserve from dual; 1=1 joins).
- as-is Q7 open: Fact Target Revenue/Units writeable; what a target is; write-back.
- as-is Q8 open: "Prod Dsc (Translated)" Lookup on WEBLANGUAGE; languages (Dim Products / Dim Products (Translated)).
- as-is Q9 open: row meaning of Dim Sales Rep (EMPLOYEE join EMP_PARENT_CHILD on ANCESTOR_KEY), "Distance".
- as-is Q10 open: does CAL_YEAR identify one row of SAMP_TIME_MTH_D (two keys).
- as-is Q11 open: member counts of Time Hierarchy levels current?
- as-is Q12 open: holders of privilege packages on Fact Target Revenue/Units.
- as-is Q13 open: unread WhereClause/FragmentContent of sources, QueryPrivilege, User.
- as-is Q14 open: does MEMBER_KEY identify one row of EMP_PARENT_CHILD.

## overview: What this design covers
- One subject area "Sample Sales" [kb:sampleapp::pres::Sample Sales]; 11 confirmed columns, 21 FSD items (18 confirmed, 1 rejected, 2 unmatched).
- Confirmed presentation columns: Dim Offices.Office Dsc; Dim Products.Brand, Prod Dsc; Dim Sales Rep.Employee Name; Dim Time.Per Name Month, Per Name Year; Fact Revenue.Discnt Value, Preferred Currency, Revenue, Units; Fact Target.Revenue.
- Slice: 7 logical tables, 12 sources, 13 physical tables (113 columns), 7 BM joins, 17 physical joins, 4 hierarchies, 1 variable, 5 security rows, 0 reports.
- Questions: none

## tab-fields-p1: The tab's fields (part 1 of 2)
- 17 confirmed items in this part; 3 not confirmed to Unresolved: S1-own-offices (rejected) [kb:item::S1-own-offices], F4 (unmatched) [kb:item::F4], K5 (unmatched) [kb:item::K5].
- Model item: Dim Time.Per Name Year = filter Year; items F1
- Model item: Dim Offices.Office Dsc = office filter/attribute; items F2, V2-office
- Model item: Dim Products.Brand = brand filter; items F3
- Model item: Dim Products.Prod Dsc = product; items V2-product
- Model item: Dim Time.Per Name Month = month; items V1-month
- Model item: Dim Sales Rep.Employee Name = sales representative; items V3-rep
- Model item: measure Revenue = Fact Revenue.Revenue; items K1, K3, K4, V1-net-revenue, V2-net-revenue, V3-net-revenue (via Net revenue)
- Model item: measure Discount Value = Fact Revenue.Discnt Value; items V2-discount, plus via Net revenue K1, K3, K4, V1/V2/V3-net-revenue
- Model item: measure Units = Fact Revenue.Units; items K2, V2-units, K4 (via Average selling price)
- Model item: measure Revenue Target = Fact Target.Revenue; items V1-target, K3 (via Revenue vs target)
- Model item: FSD measure Net revenue = [Revenue] - [Discount Value]; items K1, V1-net-revenue, V2-net-revenue, V3-net-revenue
- Model item: FSD measure Revenue vs target = DIVIDE([Net revenue],[Revenue Target]); items K3
- Model item: FSD measure Average selling price = DIVIDE([Net revenue],[Units]); items K4
- Field counts: fields table 17 rows (p1); unresolved 3 rows.
- S1-own-offices (security by office) is unresolved: point to it from Security.
- Questions: Q1 is Revenue before discount
- TODO: Q1 query names columns REVENUE, DISCNT_VALUE, UNITS of SAMP_REVENUE_F before seeing that packet; check names in dbtable-10 and correct Q1 if they differ.

## tab-fields-p2: The tab's fields (part 2 of 2)
- Model item: Fact Revenue.Preferred Currency = user's preferred currency; items N1-currency [kb:item::N1-currency]
- Field counts: fields table 1 row (p2). Total fields rows 17+1 = 18; unresolved 3; FSD items 21.
- Questions: none

## table-01-fact-revenue-p1: Model table Fact Revenue (part 1 of 2)
- Fact Revenue: 5 sources [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue]; no key recorded; 22 columns, 9 measures.
- Discnt Value mapped only in "Aggregate Revenue" and "SAMP_REVENUE_F" (not in month/year aggregates); Revenue and Units in 4 sources.
- Model item: measure Revenue = SUM('Fact Revenue'[Revenue]); items K1, K3, K4, V1-net-revenue, V2-net-revenue, V3-net-revenue [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Revenue]
- Model item: measure Discount Value = SUM('Fact Revenue'[Discnt Value]); items K1, K3, K4, V1/V2/V3-net-revenue, V2-discount [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Discnt Value]
- Model item: measure Units = SUM('Fact Revenue'[Units]); items K2, K4, V2-units [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Units]
- Model item: field Preferred Currency = INDEXCOL switch, no definition (Q2); items N1-currency
- Tab uses: Fact Revenue, Preferred Currency (items N1-currency), read from no column (formula)
- Tab uses: Fact Revenue, Revenue / Discnt Value / Units (measures), columns stated per source in p2
- Decision: INDEXCOL on VALUEOF(NQ_SESSION."PREFERRED_CURRENCY") for "Preferred Currency" [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Preferred Currency]; options (a) slicer (b) per-user table + role (c) fixed choice; like for like (b); Q2
- Planned: Q3 = several sources of Fact Revenue at different levels (to raise in p2). Revenue (EUR)/(GBP) described as dependencies only.
- Not described: All Months Revenue, All Products Revenue, Preserve, Revenue (INR).
- Questions: Q2 preferred currency switch

## table-01-fact-revenue-p2: Model table Fact Revenue (part 2 of 2)
- Sources: Aggregate Revenue (priority 1, Month x Product Detail, SAMP_REVENUE_A) [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue]; Aggregate Revenue By Month (SAMP_REVENUE_A_MTH) [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue By Month]; Aggregate Revenue by Year (SAMP_REVENUE_A_YR) [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue by Year]; FactCrossJoin (select, no level; maps only Preserve) [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.FactCrossJoin]; SAMP_REVENUE_F (detail: Sales Rep Detail, Dim Time Detail, Office Detail, Product Detail) [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.SAMP_REVENUE_F]
- Tab uses: Fact Revenue, Revenue (items K1,K3,K4,V1/V2/V3-net-revenue), read from SAMP_REVENUE_F.REVENUE [#map9], SAMP_REVENUE_A.REVENUE [#map3], SAMP_REVENUE_A_MTH.REVENUE [#map0], SAMP_REVENUE_A_YR.REVENUE [#map0]
- Tab uses: Fact Revenue, Discnt Value (items K1,K3,K4,V1/V2/V3-net-revenue,V2-discount), read from SAMP_REVENUE_F.DISCNT_VALUE [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.SAMP_REVENUE_F#map2], SAMP_REVENUE_A.DISCNT_VALUE [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue#map1]; NOT mapped as measure in MTH/YR (there a non-measure field DISCNT_VALUE)
- Tab uses: Fact Revenue, Units (items K2,K4,V2-units), read from SAMP_REVENUE_F.UNITS [#map10], SAMP_REVENUE_A.UNITS [#map4], SAMP_REVENUE_A_MTH.UNITS [#map1], SAMP_REVENUE_A_YR.UNITS [#map1]
- Key columns kept for relationships (SAMP_REVENUE_F): BILL_DAY_DT, BILL_MTH_KEY, OFFICE_KEY, PROD_KEY, EMPLOYEE_KEY, ORDER_DAY_DT.
- So for physical tables: SAMP_REVENUE_F columns REVENUE, DISCNT_VALUE -> items K1,K3,K4,V1/V2/V3-net-revenue (+V2-discount for DISCNT_VALUE); UNITS -> K2,K4,V2-units. Same for SAMP_REVENUE_A, and REVENUE/UNITS of _MTH and _YR. Other columns: none.
- Decision: several sources at different levels for Fact Revenue (4 sources map Revenue/Units) [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue]; options (a) detail only (b) summaries as aggregations (c) all, per visual; like for like (b); Q3
- Decision: Revenue (EUR)/(GBP) mapped differently (plain in Aggregate Revenue, *0.76/*0.62 in SAMP_REVENUE_F) [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.SAMP_REVENUE_F#map15]; options (a) detail (b) summary (c) both; like for like (c); Q4 (only if Q2 keeps switch)
- Decision: fixed numbers 0.76 / 0.62 in mapping [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.SAMP_REVENUE_F#map14]; options (a) keep (b) rate table; like for like (a); Q5
- Settles TODO of tab-fields-p1: SAMP_REVENUE_F has REVENUE, DISCNT_VALUE, UNITS; Q1 query is right.
- Questions: Q3 sources; Q4 EUR/GBP mapping; Q5 fixed factors

## table-02-fact-target: Model table Fact Target
- Fact Target: 1 source SAMP_TARGETS_F [kb:sampleapp::bmm::SAMPLESALES.Fact Target.src.SAMP_TARGETS_F], level Dim Time Detail x Office Detail x Product Detail, but only time column is BILL_MTH_KEY; no sales rep level. No key recorded.
- Model item: measure Revenue Target = SUM('Fact Target'[Revenue]); items K3, V1-target [kb:sampleapp::bmm::SAMPLESALES.Fact Target.Revenue]
- Tab uses: Fact Target, Revenue (items K3, V1-target), read from SAMP_TARGETS_F.REVENUE [kb:sampleapp::bmm::SAMPLESALES.Fact Target.src.SAMP_TARGETS_F#map3]
- So for SAMP_TARGETS_F: REVENUE -> items K3, V1-target; other columns none. Keys BILL_MTH_KEY, OFFICE_KEY, PROD_KEY kept for relationships.
- Decision: writeable measure "Revenue" of Fact Target [kb:sampleapp::bmm::SAMPLESALES.Fact Target.Revenue]; options (a) read-only loaded (b) entry form (c) no write-back; like for like (b); Q6
- Not described: Fact Target.Units.
- Questions: Q6 write-back of targets

## table-03-dim-offices: Model table Dim Offices
- Key SAMP_OFFICES_D_Key = Office Key, primary [kb:sampleapp::bmm::SAMPLESALES.Dim Offices.key.SAMP_OFFICES_D_Key]; 1 source SAMP_OFFICES_D, level Office Detail.
- Model item: Dim Offices.Office Dsc = office; items F2, V2-office
- Tab uses: Dim Offices, Office Dsc (items F2, V2-office), read from SAMP_OFFICES_D.OFFICE_DSC [kb:sampleapp::bmm::SAMPLESALES.Dim Offices.src.SAMP_OFFICES_D#map4]
- So for SAMP_OFFICES_D: OFFICE_DSC -> F2, V2-office; OFFICE_KEY key; others none.
- Questions: none

## table-04-dim-products: Model table Dim Products
- Key SAMP_PRODUCTS_D_Key = Prod Key, primary [kb:sampleapp::bmm::SAMPLESALES.Dim Products.key.SAMP_PRODUCTS_D_Key]; 1 source SAMP_PRODUCTS_D, level Product Detail.
- Prod Dsc has descriptor ID Prod Key [kb:sampleapp::bmm::SAMPLESALES.Dim Products.Prod Dsc]; Prod Dsc (Translated) Lookup on WEBLANGUAGE not used.
- Model item: Dim Products.Brand = brand; items F3
- Model item: Dim Products.Prod Dsc = product; items V2-product
- Tab uses: Dim Products, Brand (items F3), read from SAMP_PRODUCTS_D.BRAND [kb:sampleapp::bmm::SAMPLESALES.Dim Products.src.SAMP_PRODUCTS_D#map2]
- Tab uses: Dim Products, Prod Dsc (items V2-product), read from SAMP_PRODUCTS_D.PROD_DSC [kb:sampleapp::bmm::SAMPLESALES.Dim Products.src.SAMP_PRODUCTS_D#map6]
- So for SAMP_PRODUCTS_D: BRAND -> F3; PROD_DSC -> V2-product; PROD_KEY key; others none.
- Questions: none

## table-05-dim-products-translated: Model table Dim Products (Translated)
- Lookup table, no field confirmed; proposed: no model table. Key PK_PROD_TRANS1 (PROD_KEY, LANG_ID). Reads SAMP_PRODUCTS_D_TRANSLATION.
- So for SAMP_PRODUCTS_D_TRANSLATION: all columns none; read by no model table of this tab.
- Questions: none

## table-06-dim-sales-rep: Model table Dim Sales Rep
- Key EMP_ID = Employee ID [kb:sampleapp::bmm::SAMPLESALES.Dim Sales Rep.key.EMP_ID]; source LTS Employee level Sales Rep Hierarchy Detail; closure table EMP_PARENT_CHILD [kb:sampleapp::bmm::SAMPLESALES.Dim Sales Rep.src.LTS Employee]
- Join inside source: EMPLOYEE Inner ONE TO MANY EMP_PARENT_CHILD on ANCESTOR_KEY = EMP_ID [kb:sampleapp::bmm::SAMPLESALES.Dim Sales Rep.src.LTS Employee#table0.join0]
- Model item: Dim Sales Rep.Employee Name = sales representative; items V3-rep
- Tab uses: Dim Sales Rep, Employee Name (items V3-rep), read from EMPLOYEE.EMP_NAME [kb:sampleapp::bmm::SAMPLESALES.Dim Sales Rep.src.LTS Employee#map1]
- So for EMPLOYEE: EMP_NAME -> V3-rep; EMP_ID key; others none. EMP_PARENT_CHILD: all none (DISTANCE mapped, not used).
- Decision: join inside source LTS Employee (EMPLOYEE x EMP_PARENT_CHILD) [kb:sampleapp::bmm::SAMPLESALES.Dim Sales Rep.src.LTS Employee#table0.join0]; options (a) view (b) separate tables related (c) EMPLOYEE alone; like for like (a); Q7
- Planned: parent-child drill path decision in hierarchies unit; Q7 option (c) points to "Hierarchies" by name, no number.
- Questions: Q7 shape of Dim Sales Rep

## table-07-dim-time: Model table Dim Time
- Key SAMP_TIME_DAY_D_Key = Calendar Date [kb:sampleapp::bmm::SAMPLESALES.Dim Time.key.SAMP_TIME_DAY_D_Key]; sources LTS Time Day (day, SAMP_TIME_DAY_D) [kb:sampleapp::bmm::SAMPLESALES.Dim Time.src.LTS Time Day], LTS Time Month (month, SAMP_TIME_MTH_D) [kb:sampleapp::bmm::SAMPLESALES.Dim Time.src.LTS Time Month]
- Model item: Dim Time.Per Name Year = year; items F1
- Model item: Dim Time.Per Name Month = month; items V1-month
- Tab uses: Dim Time, Per Name Year (items F1), read from SAMP_TIME_DAY_D.PER_NAME_YEAR [kb:sampleapp::bmm::SAMPLESALES.Dim Time.src.LTS Time Day#map0] and SAMP_TIME_MTH_D.PER_NAME_YEAR [kb:sampleapp::bmm::SAMPLESALES.Dim Time.src.LTS Time Month#map0]
- Tab uses: Dim Time, Per Name Month (items V1-month), read from SAMP_TIME_DAY_D.PER_NAME_MONTH [kb:sampleapp::bmm::SAMPLESALES.Dim Time.src.LTS Time Day#map3] and SAMP_TIME_MTH_D.PER_NAME_MONTH [kb:sampleapp::bmm::SAMPLESALES.Dim Time.src.LTS Time Month#map2]
- So for SAMP_TIME_DAY_D: PER_NAME_YEAR -> F1, PER_NAME_MONTH -> V1-month, CALENDAR_DATE key; SAMP_TIME_MTH_D: PER_NAME_YEAR -> F1, PER_NAME_MONTH -> V1-month.
- Decision: two sources at different levels for Dim Time (day/month) [kb:sampleapp::bmm::SAMPLESALES.Dim Time.src.LTS Time Month]; options (a) day only (b) month as well (c) both per visual; like for like (b); Q8
- Decision: displayed names set by variable VALUEOF(NQ_SESSION.CN_Sample_Sales_Dim_Time_Per_Name_Year/_Month) [kb:sampleapp::pres::Sample Sales.Dim Time.Per Name Year]; options (a) fixed (b) translations; Q9
- Questions: Q8 day/month time tables; Q9 display names

## dbtable-01-employee: New table EMPLOYEE
- New table: EMPLOYEE, 4 columns, key EMP_ID (EMP_ID) [kb:sampleapp::phys::xe.SAMPLESALES.EMPLOYEE.key.EMP_ID], read by LTS Employee (Dim Sales Rep)
- Links: EMP_PARENT_CHILD_FKey (ANCESTOR_KEY -> EMP_ID); Complex Join#2 from FactCrossJoin.
- Questions: none

## dbtable-02-emp-parent-child: New table EMP_PARENT_CHILD
- New table: EMP_PARENT_CHILD, 4 columns, key EMP_PARENT_CHILD_Key (MEMBER_KEY) [kb:sampleapp::phys::xe.SAMPLESALES.EMP_PARENT_CHILD.key.EMP_PARENT_CHILD_Key], read by LTS Employee (Dim Sales Rep)
- Parent-child relationship table: member MEMBER_KEY, ancestor ANCESTOR_KEY, DISTANCE, IS_LEAF [kb:sampleapp::phys::xe.SAMPLESALES.EMP_PARENT_CHILD]
- Links: EMP_PARENT_CHILD_FKey (ANCESTOR_KEY -> EMPLOYEE.EMP_ID); SAMP_REVENUE_F_FKey#4 (SAMP_REVENUE_F.EMPLOYEE_KEY -> MEMBER_KEY).
- Questions: Q10 MEMBER_KEY uniqueness

## dbtable-03-factcrossjoin: New view FactCrossJoin
- New table: FactCrossJoin (view), 1 column, key missing, read by source FactCrossJoin of Fact Revenue; SQL select 1 Preserve from dual [kb:sampleapp::phys::xe.SAMPLESALES.FactCrossJoin.def.DefaultMulDB]; not used by tab.
- Decision: a table that is a query (FactCrossJoin, select) [kb:sampleapp::phys::xe.SAMPLESALES.FactCrossJoin]; options (a) view (b) calculated table (c) not built; like for like (a); Q11
- Links: Complex Join, #1, #2 to SAMP_OFFICES_D, SAMP_PRODUCTS_D, EMPLOYEE.
- Questions: Q11 FactCrossJoin

## dbtable-04-samp-offices-d: New table SAMP_OFFICES_D
- New table: SAMP_OFFICES_D, 10 columns, key SAMP_OFFICES_D_Key (OFFICE_KEY) [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_OFFICES_D.key.SAMP_OFFICES_D_Key], read by SAMP_OFFICES_D (Dim Offices)
- Links: SAMP_REVENUE_F_FKey#1, SAMP_TARGETS_F_FKey#1 (OFFICE_KEY); Complex Join from FactCrossJoin.
- Questions: none

## dbtable-05-samp-products-d: New table SAMP_PRODUCTS_D
- New table: SAMP_PRODUCTS_D, 11 columns, key SAMP_PRODUCTS_D_Key (PROD_KEY) [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_PRODUCTS_D.key.SAMP_PRODUCTS_D_Key], read by SAMP_PRODUCTS_D (Dim Products)
- Links: FKs from SAMP_REVENUE_F#2, SAMP_REVENUE_A#1, SAMP_TARGETS_F#2, SAMP_PRODUCTS_D_TRANSLATION (PROD_KEY); Complex Join#1 from FactCrossJoin.
- Questions: none

## dbtable-06-samp-products-d-translation: New table SAMP_PRODUCTS_D_TRANSLATION
- New table: SAMP_PRODUCTS_D_TRANSLATION, 3 columns, key PK_PROD_TRANS1 (LANG_ID, PROD_KEY) [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_PRODUCTS_D_TRANSLATION.key.PK_PROD_TRANS1], read by Dim Products (Translated) only, which is not in the model; built only if needed later.
- Questions: none

## dbtable-07-samp-revenue-a: New table SAMP_REVENUE_A
- New table: SAMP_REVENUE_A, 5 columns, key missing, read by Aggregate Revenue (Fact Revenue); built if Q3 keeps summaries.
- Links: SAMP_REVENUE_A_FKey (BILL_MTH_KEY -> SAMP_TIME_MTH_D.MTH_KEY); SAMP_REVENUE_A_FKey#1 (PROD_KEY -> SAMP_PRODUCTS_D).
- Q12 asks the key of SAMP_REVENUE_A; later fact tables without key: add to Q12 (edit its text) rather than a new question.
- Questions: Q12 key of revenue tables

## dbtable-08-samp-revenue-a-mth: New table SAMP_REVENUE_A_MTH
- New table: SAMP_REVENUE_A_MTH, 4 columns, key missing (added to Q12), read by Aggregate Revenue By Month (Fact Revenue).
- Links: SAMP_REVENUE_A_MTH_Foreign Key (BILL_MTH_KEY -> SAMP_TIME_MTH_D.MTH_KEY).
- Questions: none new (Q12 extended)

## dbtable-09-samp-revenue-a-yr: New table SAMP_REVENUE_A_YR
- New table: SAMP_REVENUE_A_YR, 4 columns, key missing (added to Q12), read by Aggregate Revenue by Year (Fact Revenue).
- Links: SAMP_REVENUE_A_YR_Foreign Key (BILL_YEAR_KEY -> SAMP_TIME_MTH_D.CAL_YEAR) -- see as-is Q10 (CAL_YEAR uniqueness).
- Questions: none new (Q12 extended)

## dbtable-10-samp-revenue-f: New table SAMP_REVENUE_F
- New table: SAMP_REVENUE_F, 12 columns, key missing (added to Q12), read by SAMP_REVENUE_F (Fact Revenue).
- Links: FKey (BILL_MTH_KEY -> SAMP_TIME_MTH_D.MTH_KEY), #1 OFFICE_KEY, #2 PROD_KEY, #3 BILL_DAY_DT -> SAMP_TIME_DAY_D.CALENDAR_DATE, #4 EMPLOYEE_KEY -> EMP_PARENT_CHILD.MEMBER_KEY.
- Time links use the bill date (BILL_DAY_DT / BILL_MTH_KEY); ORDER_DAY_DT has no FK. F1 says "calendar year of the order" -> raise in joins.
- Settles nothing. TODO of tab-fields-p1 confirmed: REVENUE, DISCNT_VALUE, UNITS exist.
- Questions: none new (Q12 extended)

## dbtable-11-samp-targets-f: New table SAMP_TARGETS_F
- New table: SAMP_TARGETS_F, 5 columns, key missing (added to Q12), read by SAMP_TARGETS_F (Fact Target).
- Links: FKey BILL_MTH_KEY -> SAMP_TIME_MTH_D.MTH_KEY (targets meet time at month only), #1 OFFICE_KEY, #2 PROD_KEY.
- Questions: none new (Q12 extended, title now "revenue and target tables")

## dbtable-12-samp-time-day-d: New table SAMP_TIME_DAY_D
- New table: SAMP_TIME_DAY_D, 14 columns, key SAMP_TIME_DAY_D_Key (CALENDAR_DATE) [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_DAY_D.key.SAMP_TIME_DAY_D_Key], read by LTS Time Day (Dim Time)
- Links: SAMP_REVENUE_F_FKey#3 (BILL_DAY_DT -> CALENDAR_DATE). 8 columns not mapped.
- Questions: none

## dbtable-13-samp-time-mth-d: New table SAMP_TIME_MTH_D
- New table: SAMP_TIME_MTH_D, 36 columns, key proposed MTH_KEY (SAMP_TIME_MTH_D_Key) [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.key.SAMP_TIME_MTH_D_Key]; second key CAL_YEAR [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D.key.SAMP_TIME_MTH_D_Key#1]; read by LTS Time Month (Dim Time)
- Links: FKs on MTH_KEY from SAMP_REVENUE_F, SAMP_REVENUE_A, SAMP_REVENUE_A_MTH, SAMP_TARGETS_F; on CAL_YEAR from SAMP_REVENUE_A_YR.
- Questions: Q13 keys of SAMP_TIME_MTH_D

## joins: Relationships
- 7 BM joins, all Inner, dimension one side, fact many side [kb:sampleapp::bmm::joins.Relationship_2004:11061353928600 etc.]. 6 relationships proposed (Offices/Products x Revenue/Target; Time-Revenue on BILL_DAY_DT; Sales Rep-Revenue on Employee ID = Sales Employee Key, subject to Q7/Q10). Time-Target: none proposed (Q14).
- 14 physical foreign keys proposed as FKs; 3 complex joins 1=1 (FactCrossJoin), no relationship.
- Decision: Dim Time to Fact Target, targets only at month (BILL_MTH_KEY -> SAMP_TIME_MTH_D.MTH_KEY) [kb:sampleapp::bmm::joins.Relationship_2004:11061353928600]; options (a) month-level time (b) separate month table (c) spread to days; like for like (a); Q14
- Decision: time relationship on bill date vs FSD "year of the order" [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F.SAMP_REVENUE_F_FKey#3]; options (a) bill date (b) order date (c) both, inactive; like for like (a); Q15
- Decision: complex joins with condition 1=1 [kb:sampleapp::phys::joins.Complex Join]; options (a) nearest cardinality (b) none, view/measure (c) dropped with FactCrossJoin; like for like (b); Q16
- Questions: Q14 targets/time; Q15 bill vs order date; Q16 1=1 joins

## hierarchies: Hierarchies
- 4 drill paths, all offered: Office Hierarchy (5 levels), Products Hierararchy (5), Time Hierarchy (7, time dimension true), Sales Rep Hierarchy (parent-child, 2 levels) [kb:sampleapp::bmm::SAMPLESALES.Sales Rep Hierarchy].
- Proposed: 3 hierarchies (Office, Products, Time) + Dim Time marked as date table. Need non-confirmed fields.
- Decision: parent-child drill path Sales Rep Hierarchy [kb:sampleapp::bmm::SAMPLESALES.Sales Rep Hierarchy]; options (a) flatten (b) parent/child path in model (c) no drill path; like for like (a)/(b); Q17
- Questions: Q17 parent-child

## variables: Values set per user or for the whole system
- PREFERRED_CURRENCY session var, default 'gc1', init block initBlockPrefCurrency query names PREFERED_CURRENCY (one R) [kb:sampleapp::var::PREFERRED_CURRENCY] -> Q2 (not asked twice).
- 9 names used, not defined; CN_..._Per_Name_Year/_Month/Dim_Time bear on the tab -> Q9.
- Questions: none new

## security: Security
- No row filters. 4 permissions on Fact Target.Revenue (write via BI Author Role package; read x2 and write x1 via packages with no role) [kb:sampleapp::sec::obj.ObjPriv_1500:18161355661677.o0]. S1-own-offices pointed to Unresolved.
- Decision: permissions on field Fact Target.Revenue [kb:sampleapp::sec::obj.ObjPriv_1500:18161355661677.o0]; options (a) model roles (b) no restriction; like for like (a); Q18
- Questions: Q18 who sees targets

## reports: Report-level calculations
- None: no confirmed column is a report column.
- Questions: none

## gaps: What this design could not see
- No errors. Unparsed 95 rows / 1769 occurrences. rowCount attributes not read -> volumes missing; PrivilegePackage@userRef not read -> Q18. Left out: LogicalSourceFolder 11, QueryPrivilege 1, User 3.
- Questions: none

## review
- Correction (tab-fields-p1): added: the decision note of F1 reads that it was kept for the test although the review showed revenue joins on the bill date, and every confirmed item's decision note says it is a TEST DECISION by the lead build agent, not by Kamal [kb:item::F1]; supersedes no note
- Correction (tab-fields-p2): added: the decision note of N1-currency says it is a TEST DECISION by the lead build agent, not by Kamal [kb:item::N1-currency]; supersedes no note
- Correction (table-01-fact-revenue-p1): the formula of Preferred Currency names the variable PREFERRED_CURRENCY and the measures Revenue, Revenue (EUR) and Revenue (GBP) [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Preferred Currency]; no longer: "It picks, by the value of the variable"; supersedes: to be matched
- Correction (joins): the seven business-model joins are inner joins, each between a dimension table printed 0..1 or 1..1 and a fact table printed 0..n or n..n [kb:sampleapp::bmm::joins.Relationship_2004:2111353851711]; whether that is plain one to many and what happens to unmatched fact rows is Q19; no longer: "All seven business-model joins are inner joins between a dimension table on the one side"; supersedes: to be matched
- Decision: business-model joins printed n..n / 0..n against 0..1 / 1..1, all Inner [kb:sampleapp::bmm::joins.Relationship_2004:2111353851711]; options (a) one to many, unmatched rows under blank, (b) one to many, unmatched rows left out in the load, (c) another cardinality; Q19
- Correction (gaps): no volumes are stated, and who holds the privilege packages with no recorded role is Q18; the export does not say what the unread attributes hold; no longer: "Two of these bear on what this design asks: PhysicalTable@rowCount"; supersedes: to be matched
- Correction (dbtable-03-factcrossjoin): Q11 no longer says dual is specific to the old database; whether the SQL runs on the new platform is not recorded [kb:sampleapp::phys::xe.SAMPLESALES.FactCrossJoin.def.DefaultMulDB]; no longer: "and dual is specific to the old database"; supersedes: to be matched
- Correction (table-01-fact-revenue-p1) supersedes no note
- Correction (joins) supersedes the note "7 BM joins, all Inner, dimension one side, fact many side"
- Correction (gaps) supersedes the note "No errors. Unparsed 95 rows / 1769 occurrences. rowCount attributes not read"
- Correction (dbtable-03-factcrossjoin) supersedes no note
- Correction (_summary): the opening's note that the decisions are test decisions now carries the markers [kb:item::F1] [kb:item::N1-currency]; supersedes no note
- Review: the opening and 6000-decisions now count 15 decisions and 19 open questions, after Q19 (joins).
