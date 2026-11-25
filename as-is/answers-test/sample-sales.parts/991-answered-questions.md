## Answered questions

Questions that have been answered. Statements that rest on an answer carry the question's number in their marker.

### Q4. What do "Revenue (EUR)", "Revenue (GBP)" and "Revenue (INR)" hold, and why are they multiplied in one source only?
Raised in: table-01-fact-revenue-p2
Why it matters: in the source "SAMP_REVENUE_F" the three measures are the database column REVENUE multiplied by 0.76, 0.62 and 54.68; in the source "Aggregate Revenue" they are the column REVENUE of SAMP_REVENUE_A with no multiplication. As recorded, the same measure gives different numbers depending on the source OBIEE picks. Where the three fixed numbers come from is not recorded.
Assumed meanwhile: nothing. Both mappings are described exactly as recorded, and no currency meaning is stated.
How to get the answer: in the OBIEE Administration Tool, open the source "Aggregate Revenue" of the business table "Fact Revenue" and bring back the expression its column mapping shows for each of the three measures. Then run these two queries against the schema SAMPLESALES and bring back both totals: select sum(REVENUE) from SAMP_REVENUE_A, and select sum(REVENUE) from SAMP_REVENUE_F. For the numbers 0.76, 0.62 and 54.68, bring back the rate table or document they were taken from, if there is one, and the date they applied.
Answer: TEST ANSWER: the three multipliers are fixed exchange rates from a rate sheet dated 2014-01-01; the source Aggregate Revenue already holds amounts converted at the same rates.

### Q6. What does the measure "Preserve" hold?
Raised in: table-01-fact-revenue-p2
Why it matters: "Preserve" is shown to report authors in "Fact Revenue", summarised with MAX, and mapped only in the source "FactCrossJoin", from the column Preserve of FactCrossJoin, which is a query kept in the repository and not a database table. The export records no description of what the number is or how a report author should use it.
Assumed meanwhile: nothing. The measure is described as recorded; no business meaning is stated.
How to get the answer: read the query of FactCrossJoin quoted in the section Database table FactCrossJoin, run it against the schema SAMPLESALES and bring back the rows it returns. Then run one analysis in "Sample Sales" with "Preserve" alone and one with "Preserve" and "Revenue" by year, and bring back both results.
See also: dbtable-03-factcrossjoin, the query is select 1 Preserve from dual, and FactCrossJoin has complex joins to SAMP_OFFICES_D, SAMP_PRODUCTS_D and EMPLOYEE; the question is now why the measure exists and how report authors are meant to use it.
See also: joins, the three complex joins from FactCrossJoin to SAMP_OFFICES_D, SAMP_PRODUCTS_D and EMPLOYEE have the condition 1=1 and cardinality ONE TO ONE.
Answer: TEST ANSWER: Preserve is a helper that always holds 1 and is used only to keep rows in queries; it is never shown in a report.

### Q10. Does the column CAL_YEAR identify one row of SAMP_TIME_MTH_D?
Raised in: dbtable-13-samp-time-mth-d
Why it matters: the repository declares two keys for SAMP_TIME_MTH_D, one on MTH_KEY and one on CAL_YEAR, and does not say which is the primary key. The foreign key "SAMP_REVENUE_A_YR_Foreign Key" from SAMP_REVENUE_A_YR (column BILL_YEAR_KEY) points at CAL_YEAR. The table also holds the columns CAL_MONTH, MTH_KEY and PER_NAME_MONTH. If a value of CAL_YEAR appears on several rows, a key on CAL_YEAR does not hold in the data, and a join on it would match several rows.
Assumed meanwhile: nothing. Both keys are described as recorded.
How to get the answer: run against the schema SAMPLESALES the query select count(*), count(distinct CAL_YEAR), count(distinct MTH_KEY) from SAMP_TIME_MTH_D and bring back the three numbers. Then, in the OBIEE Administration Tool, open the database table SAMP_TIME_MTH_D and bring back which of its two keys is marked as the primary key, if either.
Answer: TEST ANSWER: no. SAMP_TIME_MTH_D holds one row per month; CAL_YEAR repeats twelve times.

### Q12. Who holds the privilege packages PrivPack_4209:18141355661677 and PrivPack_4209:18181355661677?
Raised in: security
Why it matters: these two privilege packages give "read" on the fields "Revenue" and "Units" of "Fact Target", and "write" on "Revenue", but no role is recorded in the export for either. The only role recorded, "BI Author Role", holds a third package, PrivPack_4209:18101355661677, which gives "write" on both fields. As recorded, it cannot be said which users may read or change the target values, nor whether users outside "BI Author Role" are restricted.
Assumed meanwhile: nothing. The permissions are listed as recorded; no holder is stated for the two packages.
How to get the answer: in the OBIEE Administration Tool, open the permissions of the fields "Revenue" and "Units" of the table "Fact Target" in the subject area "Sample Sales" and bring back, for each permission, the application role or user it is granted to. If the roles are kept outside the repository, bring back the members of each role named there.
Answer: TEST ANSWER: both privilege packages are held by the role BI Author Role.
