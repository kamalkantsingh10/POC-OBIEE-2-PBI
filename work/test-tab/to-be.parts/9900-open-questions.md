## Open questions

Each says how to find the answer and ends with an empty answer line. Write the answer on that line, or tell the agent.

### Q1. Does the OBIEE measure "Revenue" of "Fact Revenue" hold revenue before the discount is taken off?
Raised in: tab-fields-p1
Why it matters: the FSD defines Net revenue as "Revenue after discounts" (item K1) and the Operator confirmed for it both "Fact Revenue"."Revenue" and "Fact Revenue"."Discnt Value". The proposed Net revenue takes Discount Value off Revenue. If Revenue already has the discount taken off, the discount would be taken off twice.
Assumed meanwhile: the FSD measure Net revenue is proposed as Revenue minus Discount Value; nothing is agreed.
How to get the answer: run against the schema SAMPLESALES the query select sum(REVENUE), sum(DISCNT_VALUE), sum(UNITS) from SAMP_REVENUE_F, and compare it with a few order lines whose list price, discount and revenue are known from the source system; bring back whether REVENUE is before or after the discount, and confirm with the FSD's author that Net revenue means Revenue minus Discount Value.
Answer:

### Q2. "Preferred Currency" switches between three revenue measures by a value OBIEE sets per user. How should the new tab choose the currency?
Raised in: table-01-fact-revenue-p1
Why it matters: the field "Preferred Currency" of "Fact Revenue", confirmed for item N1-currency, is an INDEXCOL formula on VALUEOF(NQ_SESSION."PREFERRED_CURRENCY") that shows "Revenue" for 'gc1', "Revenue (EUR)" for 'gc2' and "Revenue (GBP)" for 'gc3'. A Power BI model has no session variables, so what a user sees cannot be carried over as it is. "Revenue (EUR)" and "Revenue (GBP)" are not confirmed for the tab, but the switch needs them.
Options: (a) a slicer on which the user picks the currency, and a measure that switches on it; (b) a per-user setting held in a table and applied by a security role; (c) one fixed choice for everyone (for example "Revenue" only). Like for like would be (b).
Assumed meanwhile: nothing. No definition is proposed for "Preferred Currency"; "Revenue (EUR)" and "Revenue (GBP)" are not designed.
How to get the answer: with the FSD's author, decide which option the tab needs (the FSD speaks of the user's preferred currency at p14). Bring back the option; for (b), where each user's currency is held today (the init block that sets PREFERRED_CURRENCY is named in the section Values set per user or for the whole system) and the values it can take. See also as-is document, question Q2.
Answer:

### Q3. "Fact Revenue" is read from four tables at different levels of detail. Which of them does the new model keep?
Raised in: table-01-fact-revenue-p2
Why it matters: "Revenue" and "Units" are mapped in four sources: "Aggregate Revenue" (month and product, priority group 1), "Aggregate Revenue By Month" (month), "Aggregate Revenue by Year" (year) and "SAMP_REVENUE_F" (sales rep, day, office and product); the priority group of the last three is not recorded. OBIEE picks a source per query; Power BI reads the tables it is given. "Discnt Value", needed for Net revenue, is mapped only in "Aggregate Revenue" and "SAMP_REVENUE_F". The tab filters and groups by office, product brand, product, sales representative, month and year.
Options: (a) keep only the detail table SAMP_REVENUE_F and let the model add up; (b) keep the summary tables as aggregations of the detail table; (c) keep all and choose per visual. Like for like would be (b).
Assumed meanwhile: the measures Revenue, Discount Value and Units are defined over the model table Fact Revenue; which new tables feed it is not chosen. The new tables of all four are described, like for like.
How to get the answer: run against the schema SAMPLESALES the queries select sum(REVENUE), sum(UNITS), sum(DISCNT_VALUE) from SAMP_REVENUE_F; the same from SAMP_REVENUE_A; the same from SAMP_REVENUE_A_MTH; and the same from SAMP_REVENUE_A_YR, and bring back the four rows of totals, to show whether detail and summary tables agree. Bring back also who loads the three summary tables and when. Then decide with Data Engineering and the Power BI builder which option applies. See also as-is document, question Q5.
Answer:

### Q4. "Revenue (EUR)" and "Revenue (GBP)" are the plain revenue in one source and revenue times a fixed number in another. Which mapping is meant?
Raised in: table-01-fact-revenue-p2
Why it matters: this matters only if decision Q2 keeps the currency switch "Preferred Currency". In the source "Aggregate Revenue" both measures are the column REVENUE of SAMP_REVENUE_A unchanged; in the source "SAMP_REVENUE_F" they are REVENUE times 0.76 and times 0.62. The number shown therefore depends on the source OBIEE picks, and there is no single definition to carry over.
Options: (a) the detail mapping, REVENUE times the factor, is the definition; (b) the summary mapping, REVENUE unchanged, is; (c) both exist as separate measures. Like for like would be (c).
Assumed meanwhile: nothing. No currency measure is designed.
How to get the answer: in "Sample Sales", run one analysis with "Revenue" and "Revenue (EUR)" by month and product, and one by day; bring back both results and the physical SQL OBIEE logs for each, which show the source used. Then decide with the FSD's author which mapping the tab is meant to show. See also as-is document, question Q4.
Answer:

### Q5. The currency factors 0.76 and 0.62 are fixed numbers in the mapping. Are they kept as they are, or read from a table?
Raised in: table-01-fact-revenue-p2
Why it matters: this matters only if decision Q2 keeps the currency switch and decision Q4 keeps the detail mapping. The source "SAMP_REVENUE_F" multiplies REVENUE by 0.76 for "Revenue (EUR)" and by 0.62 for "Revenue (GBP)". A like-for-like design would hard-code them again; whether they are still right, and for which dates, is not recorded.
Options: (a) keep the fixed numbers; (b) read them from a table of rates. Like for like would be (a).
Assumed meanwhile: nothing. No currency measure is designed.
How to get the answer: look in the old database schema SAMPLESALES, and in the documents of the load of SAMP_REVENUE_F, for a table or file of currency rates that holds 0.76 for EUR and 0.62 for GBP, and in the change history of the repository for when the mappings of "Revenue (EUR)" and "Revenue (GBP)" in the source "SAMP_REVENUE_F" were set; bring back the source of the numbers and the date they applied. Then ask the FSD's author whether the tab must show amounts at current rates (the FSD speaks of the preferred currency at p14), and bring back the answer.
Answer:

### Q6. Revenue targets can be typed in by users in OBIEE. Should the new tab allow that, and what loads the targets?
Raised in: table-02-fact-target
Why it matters: the measure "Revenue" of "Fact Target", confirmed for items K3 and V1-target, is recorded as writeable. A Power BI measure cannot be typed into. If users still enter targets, something outside the model has to take them and write them to the new table SAMP_TARGETS_F.
Options: (a) targets become read-only in the tab and are loaded into the new table SAMP_TARGETS_F by a process to be named; (b) a separate entry form writes to that table; (c) writing back is no longer needed. Like for like would be (b).
Assumed meanwhile: the measure Revenue Target is designed as read from the new table; no write-back is designed.
How to get the answer: find, in the FSD or with its author, whether the tab is meant to take target entries (the FSD speaks of the revenue target at t2.r4 and p10), and bring back which of the three options applies and, for (a), the name of the process that loads SAMP_TARGETS_F. See also as-is document, questions Q7 and Q12.
Answer:

### Q7. "Dim Sales Rep" is read from EMPLOYEE joined to the closure table EMP_PARENT_CHILD. What shape should the new model table take?
Raised in: table-06-dim-sales-rep
Why it matters: the source "LTS Employee" joins EMPLOYEE to EMP_PARENT_CHILD as an inner join, one to many, on EMP_PARENT_CHILD.ANCESTOR_KEY = EMPLOYEE.EMP_ID. The model table has to come from one object or from related tables, and as joined one employee may give several rows. The tab uses only "Employee Name" (item V3-rep), which comes from EMPLOYEE; no field the tab uses comes from EMP_PARENT_CHILD.
Options: (a) a new view that joins the two tables the same way; (b) separate new tables EMPLOYEE and EMP_PARENT_CHILD, related in the model; (c) the model table reads EMPLOYEE alone, one row per employee, and EMP_PARENT_CHILD is kept only for the drill path (its decision is under Hierarchies). Like for like would be (a).
Assumed meanwhile: nothing. "Employee Name" is designed as read from EMPLOYEE.EMP_NAME; the row shape is not chosen.
How to get the answer: run against the schema SAMPLESALES the queries select count(*) from EMPLOYEE and select count(*) from EMPLOYEE e join EMP_PARENT_CHILD p on p.ANCESTOR_KEY = e.EMP_ID, and bring back both counts. Then decide with the FSD's author whether "sales representatives" at p12 means one row per employee, or a rollup of each manager over the employees below them, and bring back the option. See also as-is document, question Q9.
Answer:

### Q8. "Dim Time" is read from a day table and a month table. Which of them does the new model keep?
Raised in: table-07-dim-time
Why it matters: "Dim Time" has two active sources, "LTS Time Day" at the day level (SAMP_TIME_DAY_D) and "LTS Time Month" at the month level (SAMP_TIME_MTH_D); both map "Per Name Year" and "Per Name Month", which the tab uses (items F1 and V1-month). OBIEE picks the source per query, for example the month table with a summary fact table at month level. Power BI reads the tables it is given. Revenue targets carry only a month key (section Model table Fact Target), so the choice also decides how targets meet the time table.
Options: (a) keep only the day table and let the model add up to month and year; (b) keep the month table as well, as the time table for facts held by month; (c) keep both and choose per visual. Like for like would be (b).
Assumed meanwhile: nothing. Both new tables are described; the confirmed fields are shown from both sources.
How to get the answer: run against the schema SAMPLESALES the query select count(distinct PER_NAME_MONTH), count(distinct PER_NAME_YEAR) from SAMP_TIME_DAY_D, and the same from SAMP_TIME_MTH_D, and bring back whether the months and years agree. Then decide with the Power BI builder together with decision Q3 (summary fact tables) and bring back the option.
Answer:

### Q9. The names users see for "Per Name Year" and "Per Name Month" are set by variables. What names should the new tab show?
Raised in: table-07-dim-time
Why it matters: the shown names of the two confirmed fields are written as VALUEOF(NQ_SESSION.CN_Sample_Sales_Dim_Time_Per_Name_Year) and VALUEOF(NQ_SESSION.CN_Sample_Sales_Dim_Time_Per_Name_Month). Names in a Power BI model are fixed text, or translated through the model's own translations. The value these variables hold at run time is not recorded.
Options: (a) fixed names; (b) the model's translations, one name per language. Like for like would be (b) if the names differ per language, otherwise (a).
Assumed meanwhile: the fields are called Per Name Year and Per Name Month in this design; nothing is agreed about the names users see.
How to get the answer: log in to OBIEE as a report author, open the subject area "Sample Sales" and bring back the names shown for the fields "Per Name Year" and "Per Name Month" of "Dim Time"; if users work in more than one language, bring back the names per language. See also as-is document, question Q1.
Answer:

### Q10. Does MEMBER_KEY identify one row of EMP_PARENT_CHILD, through which revenue reaches the sales representatives?
Raised in: dbtable-02-emp-parent-child
Why it matters: the repository records EMP_PARENT_CHILD as one row per member and ancestor, and declares one key, "EMP_PARENT_CHILD_Key", on MEMBER_KEY alone. The foreign key "SAMP_REVENUE_F_FKey#4" from SAMP_REVENUE_F (EMPLOYEE_KEY) points at MEMBER_KEY. The tab shows net revenue per sales representative (items V3-rep and V3-net-revenue). If a member appears on several rows, a one-to-many relationship on MEMBER_KEY cannot be built, and revenue could be counted more than once.
Assumed meanwhile: MEMBER_KEY is described as the key, as recorded; no uniqueness is stated.
How to get the answer: run against the schema SAMPLESALES the queries select count(*), count(distinct MEMBER_KEY) from EMP_PARENT_CHILD and select count(*) from EMP_PARENT_CHILD where DISTANCE = 0, and bring back the three numbers. See also as-is document, question Q14.
Answer:

### Q11. FactCrossJoin is a query kept in the OBIEE repository, and the tab does not use it. Is it built at all, and if so where?
Raised in: dbtable-03-factcrossjoin
Why it matters: FactCrossJoin is not a database table but the query select 1 Preserve from dual, kept in the repository. It has to exist somewhere if it is kept. Its only column feeds the measure "Preserve" of "Fact Revenue", which no item of the tab uses. Whether this SQL, which reads from dual, runs on the new platform is not recorded.
Options: (a) a view with the same SQL on the new platform; (b) a calculated table in the model; (c) it is not built for this tab. Like for like would be (a).
Assumed meanwhile: the view is described, like for like; nothing in the tab's model reads it.
How to get the answer: decide with Data Engineering and the Power BI builder whether any report still needs the measure "Preserve" (see also as-is document, question Q6), and bring back the option.
Answer:

### Q12. Which columns identify one row of the revenue and target tables the tab reads, for which the export records no key?
Raised in: dbtable-07-samp-revenue-a
Why it matters: the repository declares no key for SAMP_REVENUE_A, SAMP_REVENUE_A_MTH, SAMP_REVENUE_A_YR, the detail table SAMP_REVENUE_F or the targets table SAMP_TARGETS_F. A data engineer building the new table needs to know what one row stands for, to load it without duplicates and to check its totals against the old table. The tab's revenue figures (items K1 to K4, V1 to V3) may be read from it, depending on decision Q3.
Assumed meanwhile: nothing. No key is proposed; the new table is described column by column.
How to get the answer: run against the schema SAMPLESALES the queries select count(*) from SAMP_REVENUE_A and select count(*) from (select BILL_MTH_KEY, PROD_KEY from SAMP_REVENUE_A group by BILL_MTH_KEY, PROD_KEY), and bring back both counts; if they are equal, BILL_MTH_KEY and PROD_KEY identify a row. Do the same for SAMP_REVENUE_A_MTH with BILL_MTH_KEY alone, and for SAMP_REVENUE_A_YR with BILL_YEAR_KEY alone. For SAMP_REVENUE_F, bring back select count(*), count(distinct ORDER_NUMBER) from SAMP_REVENUE_F, and from the owner of the source system which columns identify an order line. For SAMP_TARGETS_F, bring back select count(*) from SAMP_TARGETS_F and select count(*) from (select BILL_MTH_KEY, OFFICE_KEY, PROD_KEY from SAMP_TARGETS_F group by BILL_MTH_KEY, OFFICE_KEY, PROD_KEY). Bring back also the key of the table in the database, if one is defined there.
Answer:

### Q13. Which of the two keys of SAMP_TIME_MTH_D is its primary key, and does CAL_YEAR identify one row?
Raised in: dbtable-13-samp-time-mth-d
Why it matters: the repository declares two keys for SAMP_TIME_MTH_D, one on MTH_KEY and one on CAL_YEAR, and does not say which is the primary key. The revenue and target tables reach it on MTH_KEY; the year summary SAMP_REVENUE_A_YR reaches it on CAL_YEAR. A table holding a month per row would hold each year on several rows; then a relationship on CAL_YEAR would match several rows and the yearly figures could be multiplied.
Assumed meanwhile: MTH_KEY is proposed as the key; nothing is assumed about CAL_YEAR.
How to get the answer: run against the schema SAMPLESALES the query select count(*), count(distinct MTH_KEY), count(distinct CAL_YEAR) from SAMP_TIME_MTH_D and bring back the three numbers. See also as-is document, question Q10.
Answer:

### Q14. Revenue targets carry only a month key. How should they meet the time table, so that Revenue vs target compares the same period?
Raised in: joins
Why it matters: OBIEE joins "Dim Time" to "Fact Target" (inner, 0..1 to 0..n). The target table SAMP_TARGETS_F holds only BILL_MTH_KEY, whose foreign key points at SAMP_TIME_MTH_D.MTH_KEY, while the model table Dim Time is keyed on the day (Calendar Date). Item K3 asks for net revenue as a percentage of the target for the same period, and item V1-target shows the target by month.
Options: (a) the model's time table is at month level, or carries a month key, and both facts relate to it on the month; (b) a separate month table relates to Fact Target, and the year and month fields are shared through it; (c) targets are spread to days in the new table SAMP_TARGETS_F. Like for like would be (a).
Assumed meanwhile: no relationship from Dim Time to Fact Target is proposed; the measure Revenue Target is defined but cannot yet be cut by month or year.
How to get the answer: decide with the Power BI builder together with decision Q8 (day and month tables), and bring back the option. To check what OBIEE does today, run in "Sample Sales" one analysis with "Per Name Month" and "Fact Target"."Revenue", and bring back the physical SQL OBIEE logs for it.
Answer:

### Q15. The FSD asks for the calendar year of the order, but OBIEE relates revenue to time on the bill date. Which date should drive the Year filter and the months?
Raised in: joins
Why it matters: item F1 is "Year: Calendar year of the order", and its confirmed column is "Dim Time"."Per Name Year". OBIEE relates revenue to time through SAMP_REVENUE_F.BILL_DAY_DT and BILL_MTH_KEY; the order date ORDER_DAY_DT is in the table but has no relationship to time. The two dates can fall in different years or months.
Options: (a) the bill date, like OBIEE; (b) the order date, with a relationship from ORDER_DAY_DT (date and time) to the time table; (c) both, the order date as an inactive relationship used by chosen measures. Like for like would be (a).
Assumed meanwhile: the relationship is proposed on the bill date, like OBIEE; nothing is agreed.
How to get the answer: ask the FSD's author whether "calendar year of the order" (at t1.r2) means the order date or simply the period of the sale as reported today, and bring back the option. To see how much it matters, run select count(*) from SAMP_REVENUE_F where extract(year from ORDER_DAY_DT) <> extract(year from BILL_DAY_DT) and bring back the count.
Answer:

### Q16. FactCrossJoin is joined to SAMP_OFFICES_D, SAMP_PRODUCTS_D and EMPLOYEE on the condition 1=1. What are these joins for, and are they kept?
Raised in: joins
Why it matters: the three complex joins "Complex Join", "Complex Join#1" and "Complex Join#2" have the condition 1=1 and cardinality ONE TO ONE. A condition that pairs every row with every row has no relationship form in a Power BI model. No item of the tab uses FactCrossJoin.
Options: (a) a relationship with the nearest cardinality; (b) no relationship, and whatever the joins are for is done in a view or a measure; (c) dropped together with FactCrossJoin if decision Q11 is (c). Like for like would be (b).
Assumed meanwhile: no relationship is proposed for the three joins.
How to get the answer: decide together with decision Q11; if FactCrossJoin is kept, bring back from the owner of the OBIEE repository what the measure "Preserve" and these joins are meant to achieve (see also as-is document, question Q6).
Answer:

### Q17. "Sales Rep Hierarchy" is a parent-child drill path of no fixed depth. How should managers and their teams be shown in the new tab?
Raised in: hierarchies
Why it matters: OBIEE records "Sales Rep Hierarchy" as parent-child (value based: the depth is not fixed), with its relationships in EMP_PARENT_CHILD, and offers it to report authors shown by "Employee Name". Power BI has no parent-child hierarchy type. The tab lists sales representatives with the highest net revenue (items V3-rep and V3-net-revenue); whether a manager's line should include the revenue of the people below them is not said.
Options: (a) flatten to a fixed number of level columns in the new table or a view; (b) keep parent and child fields and compute the path in the model; (c) no drill path for this tab: one row per employee, their own revenue only. Like for like would be (a) or (b).
Assumed meanwhile: no hierarchy is proposed for Dim Sales Rep; "Employee Name" is a plain field.
How to get the answer: run against the schema SAMPLESALES the query select max(DISTANCE) from EMP_PARENT_CHILD and bring back the greatest depth. Then ask the FSD's author whether the list of sales representatives at p12 should roll up managers over their teams, and bring back the option. See also decision Q7.
Answer:

### Q18. Who may see the revenue targets in the new tab?
Raised in: security
Why it matters: OBIEE gives "read" on "Fact Target"."Revenue", the target shown by items K3 and V1-target, through two privilege packages for which no role is recorded, and "write" through the package held by "BI Author Role" and through one with no role recorded. A Power BI model can hide a measure from some users only through roles, and the roles and their members are not all in the export.
Options: (a) the same restrictions as model roles, one per OBIEE role; (b) no restriction below the report: everyone who can open the tab sees the targets. Like for like would be (a).
Assumed meanwhile: no role is proposed; the measure Revenue Target is designed as visible to everyone who can open the tab.
How to get the answer: in the OBIEE repository, look up the permissions on the presentation column "Revenue" of the presentation table "Fact Target" in the subject area "Sample Sales", and the holders of the privilege packages PrivPack_4209:18141355661677 and PrivPack_4209:18181355661677, and bring back, for each permission, the application role or user it is granted to, and the members of each role. Then confirm with the FSD's author who should see targets on the tab. See also as-is document, questions Q12 and Q13.
Answer:

### Q19. The business-model joins print the fact side as 0..n for Dim Time and Dim Sales Rep but as n..n for Dim Offices and Dim Products, and the dimension side as 0..1 or 1..1, all as inner joins. Are these plain one-to-many joins, and must fact rows with no matching dimension row be left out as OBIEE's inner join leaves them out?
Raised in: joins
Why it matters: the section Relationships proposes every relationship as one to many with a single filter direction. The packet prints "Fact Revenue" (n..n) joins "Dim Offices" (0..1), join type Inner, and "Dim Time" (0..1) joins "Fact Revenue" (0..n), join type Inner; the two spellings of the many side are not explained in the export. An inner join drops fact rows whose key has no dimension row; a Power BI relationship keeps them and shows them under a blank member, so totals can differ.
Options: (a) one-to-many relationships as proposed, and fact rows with no matching dimension row shown under a blank member; (b) one-to-many relationships as proposed, and the load of the new fact tables or a view leaves out the rows with no matching dimension row; (c) another cardinality for the joins printed n..n, if the repository means something other than many. Like for like would be (b).
Assumed meanwhile: the relationships are designed one to many as proposed; nothing is decided about unmatched rows.
How to get the answer: in the repository's business model "SAMPLESALES", look at the logical joins of "Fact Revenue" and "Fact Target" with "Dim Offices" and "Dim Products" and bring back the cardinality each shows on each side; then run, on the old database, a count of the rows of SAMP_REVENUE_F and SAMP_TARGETS_F whose OFFICE_KEY or PROD_KEY has no row in SAMP_OFFICES_D or SAMP_PRODUCTS_D, and bring back the counts.
Answer:
