## Open questions

These are the points the export could not settle. Each says how to find the answer and ends with an empty answer line. Write the answer on that line, or tell the agent; the document will then be updated.

### Q1. What name do report authors see for the table "Dim Time"?
Raised in: overview
Why it matters: the name shown to users for the table "Dim Time" is recorded as the formula VALUEOF(NQ_SESSION.CN_Sample_Sales_Dim_Time), not as a fixed text. The value of the variable CN_Sample_Sales_Dim_Time at run time is not recorded, so the name a report author sees for this table cannot be stated.
Assumed meanwhile: nothing. The table is called "Dim Time" in this document, and the formula is quoted exactly.
How to get the answer: log in to OBIEE as a report author, open the subject area "Sample Sales" and bring back the name shown for the table whose business table is "Dim Time". Then run this statement where OBIEE lets you issue logical SQL and bring back the value: select VALUEOF(NQ_SESSION.CN_Sample_Sales_Dim_Time) from "Sample Sales". If the value differs between users or languages, bring back two examples.
See also: table-07-dim-time, the six fields of "Dim Time" also have VALUEOF formulas as their shown names (CN_Sample_Sales_Dim_Time_Calendar_Date, _Per_Name_Half, _Per_Name_Month, _Per_Name_Qtr, _Per_Name_Week, _Per_Name_Year); bring back the names shown for them too.
See also: variables, none of the seven CN_Sample_Sales_Dim_Time names is defined in the repository; where their values are set is therefore not recorded either.
Answer:

### Q2. What value does PREFERRED_CURRENCY hold for a user at run time, and so which measure does "Preferred Currency" show?
Raised in: table-01-fact-revenue-p1
Why it matters: the field "Preferred Currency" of "Fact Revenue" is the formula INDEXCOL( CASE VALUEOF(NQ_SESSION."PREFERRED_CURRENCY") WHEN 'gc1' THEN 0 WHEN 'gc2' THEN 1 WHEN 'gc3' THEN 2 END , ...), which names after it the fields "Revenue", "Revenue (EUR)" and "Revenue (GBP)". The value the variable holds for a user is not recorded, nor what happens for a value other than 'gc1', 'gc2' or 'gc3'.
Assumed meanwhile: nothing. The formula is quoted exactly in the section Fact Revenue; no run-time value is stated.
How to get the answer: log in to OBIEE as a report author, run this statement where OBIEE lets you issue logical SQL, and bring back the value: select VALUEOF(NQ_SESSION.PREFERRED_CURRENCY) from "Sample Sales". Then put the fields "Preferred Currency" and "Revenue" side by side in one analysis and bring back whether the two totals are equal. If users can choose a currency in their own preferences, bring back the list of choices offered and the value after changing it.
See also: variables, PREFERRED_CURRENCY is a session variable with default 'gc1'; the init block "initBlockPrefCurrency" that sets it runs a query whose quoted text names PREFERED_CURRENCY, with one R, which the repository does not define. Bring back also what that query returns when run in the database.
Answer:

### Q3. What distinguishes "All Months Revenue" and "All Products Revenue" from "Revenue"?
Raised in: table-01-fact-revenue-p2
Why it matters: in both sources that map them, "Aggregate Revenue" and "SAMP_REVENUE_F", the three measures are mapped from the same database column REVENUE, and all three are summarised with SUM. Nothing in the export records any other rule that would distinguish them.
Assumed meanwhile: nothing. The three measures are described exactly as recorded, and no difference in meaning is stated.
How to get the answer: in the OBIEE Administration Tool, open the business table "Fact Revenue" and, for each of the fields "All Months Revenue" and "All Products Revenue", bring back whatever its properties show about levels of drill paths it is fixed at (for example a level set for "Time Hierarchy" or "Products Hierararchy"). Then run one analysis in "Sample Sales" with the fields "Revenue", "All Months Revenue" and "All Products Revenue" by month and by product, and bring back whether the three columns differ.
Answer:

### Q4. What do "Revenue (EUR)", "Revenue (GBP)" and "Revenue (INR)" hold, and why are they multiplied in one source only?
Raised in: table-01-fact-revenue-p2
Why it matters: in the source "SAMP_REVENUE_F" the three measures are the database column REVENUE multiplied by 0.76, 0.62 and 54.68; in the source "Aggregate Revenue" they are the column REVENUE of SAMP_REVENUE_A with no multiplication. As recorded, the same measure gives different numbers depending on the source OBIEE picks. Where the three fixed numbers come from is not recorded.
Assumed meanwhile: nothing. Both mappings are described exactly as recorded, and no currency meaning is stated.
How to get the answer: in the OBIEE Administration Tool, open the source "Aggregate Revenue" of the business table "Fact Revenue" and bring back the expression its column mapping shows for each of the three measures. Then run these two queries against the schema SAMPLESALES and bring back both totals: select sum(REVENUE) from SAMP_REVENUE_A, and select sum(REVENUE) from SAMP_REVENUE_F. For the numbers 0.76, 0.62 and 54.68, bring back the rate table or document they were taken from, if there is one, and the date they applied.
Answer:

### Q5. Which of the five sources of "Fact Revenue" does OBIEE use for a query, and in what order of preference?
Raised in: table-01-fact-revenue-p2
Why it matters: "Fact Revenue" has five active sources. Only "Aggregate Revenue" has a priority group recorded (1); for "Aggregate Revenue By Month", "Aggregate Revenue by Year", "FactCrossJoin" and "SAMP_REVENUE_F" it is not recorded. "FactCrossJoin" has no level of detail recorded. "Revenue" and "Units" are mapped in four of the sources, so the number a report shows depends on which source is used, and the export does not say how the choice is made.
Assumed meanwhile: nothing. Each source is described as recorded; no order of preference is stated.
How to get the answer: in the OBIEE Administration Tool, open each of the five sources of the business table "Fact Revenue" and bring back its priority group and the levels its content settings show. Then run in "Sample Sales" three analyses with "Revenue": by year, by month, and by product and day; for each, bring back the physical SQL that OBIEE logs for the query and the database table it reads.
Answer:

### Q6. What does the measure "Preserve" hold?
Raised in: table-01-fact-revenue-p2
Why it matters: "Preserve" is shown to report authors in "Fact Revenue", summarised with MAX, and mapped only in the source "FactCrossJoin", from the column Preserve of FactCrossJoin, which is a query kept in the repository and not a database table. The export records no description of what the number is or how a report author should use it.
Assumed meanwhile: nothing. The measure is described as recorded; no business meaning is stated.
How to get the answer: read the query of FactCrossJoin quoted in the section Database table FactCrossJoin, run it against the schema SAMPLESALES and bring back the rows it returns. Then run one analysis in "Sample Sales" with "Preserve" alone and one with "Preserve" and "Revenue" by year, and bring back both results.
See also: dbtable-03-factcrossjoin, the query is select 1 Preserve from dual, and FactCrossJoin has complex joins to SAMP_OFFICES_D, SAMP_PRODUCTS_D and EMPLOYEE; the question is now why the measure exists and how report authors are meant to use it.
See also: joins, the three complex joins from FactCrossJoin to SAMP_OFFICES_D, SAMP_PRODUCTS_D and EMPLOYEE have the condition 1=1 and cardinality ONE TO ONE.
Answer:

### Q7. What do the target measures of "Fact Target" hold, and what happens to a value a user types into them?
Raised in: table-02-fact-target
Why it matters: "Fact Target" holds two measures, "Revenue" and "Units", both summed, both mapped from the database table SAMP_TARGETS_F, and both recorded as writeable. The export says nothing on what a target is (for whom, for which period), and nothing on whether typed values are written back to SAMP_TARGETS_F, by whom, or whether they are kept.
Assumed meanwhile: nothing. Both measures are described as recorded; no business meaning and no write-back behaviour are stated.
How to get the answer: in the OBIEE Administration Tool, open the business table "Fact Target" and bring back whatever the properties of "Revenue" and "Units" show about writing values back, and whether the database connection used for SAMP_TARGETS_F allows writing. Then run against the schema SAMPLESALES the query select count(*), min(BILL_MTH_KEY), max(BILL_MTH_KEY) from SAMP_TARGETS_F and bring back the result; and bring back any dashboard or form in which users enter targets, if there is one.
See also: overview, "Fact Target"."Revenue" is the measure OBIEE uses when a query names none (moved here by the review from "Why it matters").
Answer:

### Q8. What does "Prod Dsc (Translated)" show a user, and which languages are there?
Raised in: table-04-dim-products
Why it matters: the field "Prod Dsc (Translated)" of "Dim Products" is a formula that starts with Lookup and names the field "PROD_DESC" of the business table "Dim Products (Translated)", the fields "Prod Dsc" and "Prod Key" of "Dim Products", and the variable WEBLANGUAGE. The value WEBLANGUAGE holds for a user, the languages for which translations exist, and what is shown when no translation is found are not recorded.
Assumed meanwhile: nothing. The formula is quoted exactly in the section Dim Products; no run-time behaviour is stated.
How to get the answer: log in to OBIEE as a report author, run this statement where OBIEE lets you issue logical SQL, and bring back the value: select VALUEOF(NQ_SESSION.WEBLANGUAGE) from "Sample Sales". Then, in the database table that the source of "Dim Products (Translated)" reads (named in the section Dim Products (Translated)), count the rows per value of the column that the lookup matches against the variable, and bring back the result. Finally show "Prod Dsc" and "Prod Dsc (Translated)" side by side in one analysis, once with the user interface in English and once in another language, and bring back both results.
See also: table-05-dim-products-translated, the lookup table is keyed on PROD_KEY and LANG_ID (VARCHAR, precision 2); the count can be run as select LANG_ID, count(*) from SAMP_PRODUCTS_D_TRANSLATION group by LANG_ID.
See also: variables, WEBLANGUAGE is used by name and not defined in the repository.
Answer:

### Q9. What does one row of "Dim Sales Rep" stand for, and what does "Distance" hold?
Raised in: table-06-dim-sales-rep
Why it matters: the source "LTS Employee" joins EMPLOYEE to the closure table EMP_PARENT_CHILD on EMP_PARENT_CHILD.ANCESTOR_KEY = EMPLOYEE.EMP_ID, as an inner join, one to many, and maps the field "Distance", which report authors see, from EMP_PARENT_CHILD.DISTANCE. The key of the business table is "Employee ID" alone. The export does not say whether a report that shows "Dim Sales Rep" fields gets one row per employee or one row per employee and related employee, nor what a value of "Distance" means.
Assumed meanwhile: nothing. The join and the mapping are described as recorded; no row meaning is stated.
How to get the answer: run against the schema SAMPLESALES the queries select count(*) from EMPLOYEE and select count(*) from EMPLOYEE e join EMP_PARENT_CHILD p on p.ANCESTOR_KEY = e.EMP_ID, and bring back both counts. Then run one analysis in "Sample Sales" with "Employee ID", "Employee Name" and "Distance" and one with "Employee ID" and "Revenue" of "Fact Revenue", and bring back the row counts and the total of "Revenue" in the second compared with the total of "Revenue" alone.
See also: dbtable-02-emp-parent-child, the table is recorded as one row per member and ancestor, with DISTANCE the steps between the two and IS_LEAF a leaf flag; SAMP_REVENUE_F points at its MEMBER_KEY, while the source joins on ANCESTOR_KEY.
Answer:

### Q10. Does the column CAL_YEAR identify one row of SAMP_TIME_MTH_D?
Raised in: dbtable-13-samp-time-mth-d
Why it matters: the repository declares two keys for SAMP_TIME_MTH_D, one on MTH_KEY and one on CAL_YEAR, and does not say which is the primary key. The foreign key "SAMP_REVENUE_A_YR_Foreign Key" from SAMP_REVENUE_A_YR (column BILL_YEAR_KEY) points at CAL_YEAR. The table also holds the columns CAL_MONTH, MTH_KEY and PER_NAME_MONTH. If a value of CAL_YEAR appears on several rows, a key on CAL_YEAR does not hold in the data, and a join on it would match several rows.
Assumed meanwhile: nothing. Both keys are described as recorded.
How to get the answer: run against the schema SAMPLESALES the query select count(*), count(distinct CAL_YEAR), count(distinct MTH_KEY) from SAMP_TIME_MTH_D and bring back the three numbers. Then, in the OBIEE Administration Tool, open the database table SAMP_TIME_MTH_D and bring back which of its two keys is marked as the primary key, if either.
Answer:

### Q11. Are the member counts recorded for the levels of "Time Hierarchy" current?
Raised in: hierarchies
Why it matters: the repository records these member counts for "Time Hierarchy": Year 2, Half Year 180, Quarter 270, Month 322, Week 350, Dim Time Detail 365. They are copied as recorded. The export does not say when or how they were obtained, and a level with 2 members sits above a level with 180. Anyone sizing or checking the new tables against these counts needs to know whether they describe the data.
Assumed meanwhile: nothing. The counts are copied as recorded and not used for any other statement.
How to get the answer: run against the schema SAMPLESALES the query select count(distinct PER_NAME_YEAR), count(distinct PER_NAME_HALF), count(distinct PER_NAME_QTR), count(distinct PER_NAME_MONTH), count(distinct PER_NAME_WEEK), count(distinct CALENDAR_DATE) from SAMP_TIME_DAY_D and bring back the six numbers. Then, in the OBIEE Administration Tool, open the drill path "Time Hierarchy" and bring back the member count each level shows and, if shown, when it was last updated.
Answer:

### Q12. Who holds the privilege packages PrivPack_4209:18141355661677 and PrivPack_4209:18181355661677?
Raised in: security
Why it matters: these two privilege packages give "read" on the fields "Revenue" and "Units" of "Fact Target", and "write" on "Revenue", but no role is recorded in the export for either. The only role recorded, "BI Author Role", holds a third package, PrivPack_4209:18101355661677, which gives "write" on both fields. As recorded, it cannot be said which users may read or change the target values, nor whether users outside "BI Author Role" are restricted.
Assumed meanwhile: nothing. The permissions are listed as recorded; no holder is stated for the two packages.
How to get the answer: in the OBIEE Administration Tool, open the permissions of the fields "Revenue" and "Units" of the table "Fact Target" in the subject area "Sample Sales" and bring back, for each permission, the application role or user it is granted to. If the roles are kept outside the repository, bring back the members of each role named there.
Answer:

### Q13. Do the items the export did not read for sources and privilege packages hold anything this document should state?
Raised in: gaps
Why it matters: the export did not read, among others, WhereClause@mdsid and WhereClause@name (17 occurrences each) and FragmentContent@mdsid and FragmentContent@name (17 each) in LogicalTableSource, PrivilegePackage@userRef (3) in PrivilegePackage, and the whole object types QueryPrivilege (1 object) and User (3 objects). The sections on sources and on security describe only what was read; if any of these items carries a condition on a source or the holder of a privilege package, those sections are incomplete.
Assumed meanwhile: nothing. Each section states what was read; nothing is assumed about what was not read.
How to get the answer: in the OBIEE Administration Tool, open each of the 12 sources of the business model "SAMPLESALES" and bring back, for each, whatever its content settings show as a condition on rows (a where clause) or as a fragment, or that there is none. Then open the users and the privilege packages PrivPack_4209:18141355661677 and PrivPack_4209:18181355661677 and bring back which users or roles each package is assigned to.
Answer:

### Q14. Does the column MEMBER_KEY identify one row of EMP_PARENT_CHILD?
Raised in: dbtable-02-emp-parent-child
Why it matters: the repository records EMP_PARENT_CHILD as the relationship table of a parent-child hierarchy, with one row per member and ancestor, and declares one key for it, "EMP_PARENT_CHILD_Key", made of MEMBER_KEY alone. The foreign key "SAMP_REVENUE_F_FKey#4" from SAMP_REVENUE_F (column EMPLOYEE_KEY) points at MEMBER_KEY. The export does not say whether a value of MEMBER_KEY appears on one row or on several; if on several, a key on MEMBER_KEY alone does not hold in the data.
Assumed meanwhile: nothing. The key and the foreign key are described as recorded; no uniqueness is stated.
How to get the answer: run against the schema SAMPLESALES the query select count(*), count(distinct MEMBER_KEY), count(distinct ANCESTOR_KEY) from EMP_PARENT_CHILD, and the query select count(*) from (select MEMBER_KEY, ANCESTOR_KEY from EMP_PARENT_CHILD group by MEMBER_KEY, ANCESTOR_KEY), and bring back the four numbers. Then, in the OBIEE Administration Tool, open the database table EMP_PARENT_CHILD and bring back the columns of its key "EMP_PARENT_CHILD_Key" and whether it is marked as the primary key.
Answer:
