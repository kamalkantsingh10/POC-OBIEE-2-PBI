# Questions about the trace of sample-sales-monthly

## Open questions

### Q1. What is the Row identifier of the OBIEE dashboard page "Sample Sales / Overview" that this tab replaces?
Item: R1
FSD anchor: p4
Why it matters: the FSD says the tab replaces the OBIEE dashboard page "Sample Sales / Overview". The Knowledge Base of sampleapp holds only the repository half of the export (no catalog), so it knows no dashboards, pages or reports, and no existing report can confirm or contradict any match proposed here. Every match is therefore scored on names, tables and summarisation only.
Assumed meanwhile: the replaced page is unknown; R1 has no identifier and no match is scored on evidence from an existing report.
How to get the answer: add the catalog half of the sampleapp export (the web catalog that holds the dashboard "Sample Sales" and its page "Overview") to the estate, read it again with the jar's extract command, and bring back the Row identifier of that dashboard page as printed among a candidate's report columns by find-columns. If the page no longer exists, bring back that fact instead.
Answer:

### Q2. How is it known which offices belong to which regional sales manager?
Item: S1-own-offices
FSD anchor: p6
Why it matters: the FSD says each manager sees only their own offices and the Head of Sales sees everything. Sample Sales has the office (Dim Offices.Office Key, Dim Offices.Office Dsc) and a column Dim Sales Rep.Manager ID, but no column that links a person who opens the report to the offices they manage. Without that link the restriction cannot be built, or is built on a guess.
Assumed meanwhile: the rows are restricted by Dim Offices.Office Key; the link from a manager to their offices is unknown.
How to get the answer: decide where the list of managers and their offices is kept (for example a table that gives, for each manager's login, the OFFICE_KEY values of SAMP_OFFICES_D they may see), and bring back its name and columns. If the restriction is meant to follow Dim Sales Rep.Manager ID and Dim Sales Rep.Office ID (database columns EMPLOYEE.MANAGER_ID and EMPLOYEE.OFFICE_ID), bring back that rule instead, and say how the Head of Sales is recognised.
Answer:

### Q3. Is "Per Name Year" the calendar year of the order date?
Item: F1
FSD anchor: t1.r2.c2 t1.r2.c3
Why it matters: the FSD's Year filter is the calendar year of the order. The only year column of Sample Sales is Dim Time.Per Name Year (SAMP_TIME_DAY_D.PER_NAME_YEAR and SAMP_TIME_MTH_D.PER_NAME_YEAR, 4 characters of text). Nothing recorded says whether it is a calendar or a fiscal year. The export does record that revenue rows join the time tables on the bill date, not the order date: SAMP_REVENUE_F.BILL_DAY_DT to SAMP_TIME_DAY_D.CALENDAR_DATE, and SAMP_REVENUE_F.BILL_MTH_KEY to SAMP_TIME_MTH_D.MTH_KEY. Fact Revenue also has the column Order Day Dt (SAMP_REVENUE_F.ORDER_DAY_DT). A fiscal year, or the bill date in place of the order date, would put some orders in a different year. The same holds for the month on the axis of V1.
Assumed meanwhile: Per Name Year is the calendar year, and revenue is placed by its bill date, as the recorded join does.
How to get the answer: in the database table SAMP_TIME_DAY_D, compare PER_NAME_YEAR with the year of CALENDAR_DATE for a few dates near the start and end of a year, and bring back whether they always agree. Then decide whether the Year filter (t1.r2.c3) and the month axis of V1 (p10) should follow the order date (SAMP_REVENUE_F.ORDER_DAY_DT) or the bill date (SAMP_REVENUE_F.BILL_DAY_DT) that OBIEE joins on, and bring back which; if the order date, also bring back, from SAMP_REVENUE_F, how many rows have ORDER_DAY_DT and BILL_DAY_DT in different years.
See also: V1-month
Answer:

### Q4. What is the "sales region" an office belongs to: is it the office's Area Name?
Item: F4
FSD anchor: t1.r5.c2 t1.r5.c3
Why it matters: the FSD has a Region filter, "sales region the office belongs to". Sample Sales has no column named region or territory. The nearest is Dim Offices.Area Name (SAMP_OFFICES_D.AREA_NAME); nothing recorded says whether an area is a sales region. Dim Offices also has Company, Organization and Department, any of which could be a grouping of offices. F4 is left unmatched.
Assumed meanwhile: no column; F4 is unmatched.
How to get the answer: list the distinct values of AREA_NAME, ORGANIZATION and COMPANY in the database table SAMP_OFFICES_D and compare them with the sales regions the regional sales managers are responsible for; bring back which column holds the regions, or that none does and where the regions are kept.
Answer:

### Q5. Does "Net revenue" mean Revenue minus the discount value, or is Revenue already net?
Item: K1
FSD anchor: t2.r2.c2 t2.r2.c3
Why it matters: the FSD defines net revenue as revenue after discounts. Sample Sales has a measure Fact Revenue.Revenue and a measure Fact Revenue.Discnt Value and no column that computes net revenue, and nothing recorded says whether Revenue already has discounts taken off. The two readings give different numbers on every card, chart and table that shows net revenue (K1, K3, K4, V1, V2, V3).
Assumed meanwhile: Revenue minus Discnt Value; both columns are proposed for K1, and the same pieces for K3, K4 and the net revenue of V1, V2 and V3.
How to get the answer: for a few orders of the database table SAMP_REVENUE_F, compare the columns REVENUE and DISCNT_VALUE with the invoiced amount of each order, and bring back whether REVENUE is the amount before or after the discount. If the FSD means something else by "discounts", bring back the wording to put at t2.r2.c3.
See also: K3
See also: K4
See also: V1-net-revenue
See also: V2-net-revenue
See also: V3-net-revenue
Answer:

### Q6. Is the revenue target set before or after discounts, and for which periods is it entered?
Item: K3
FSD anchor: t2.r4.c2 t2.r4.c3
Why it matters: K3 divides net revenue by the revenue target "for the same period", and V1 draws the target as one point per month. Sample Sales has one revenue target, Fact Target.Revenue (database column SAMP_TARGETS_F.REVENUE). If the target is set on gross revenue, comparing it with net revenue understates performance. If targets are entered per quarter or per year rather than per month, the monthly points of V1 and the "same period" of K3 need a rule to spread or compare them. The FSD also says targets may be revised during the year (p15), which raises whether the latest or the original target is meant.
Assumed meanwhile: Fact Target.Revenue is the target for net revenue, at the level of time it is stored at, and the latest revision is meant.
How to get the answer: from the place where the revenue targets are entered or their entry rules are written down, find out whether a revenue target is set before or after discounts, and at which level of time (month, quarter, year) it is entered. The export records that SAMP_TARGETS_F joins the month table SAMP_TIME_MTH_D on BILL_MTH_KEY; in the database table SAMP_TARGETS_F, check whether a revised target replaces the old row or is added as a new one (for example, more than one row with the same BILL_MTH_KEY, OFFICE_KEY and PROD_KEY). Bring back the three facts.
See also: V1-target
Answer:

### Q7. Where are customers recorded, so that "Active customers" can be counted?
Item: K5
FSD anchor: t2.r6.c2 t2.r6.c3
Why it matters: the FSD's card K5 counts the distinct customers who placed at least one order in the period. No column of Sample Sales, nor of any other subject area of sampleapp, has the word customer, and no formula in Sample Sales holds COUNT. The nearest column, Fact Revenue.Order Number (SAMP_REVENUE_F.ORDER_NUMBER), identifies orders, not customers. Without a customer column the card cannot be built.
Assumed meanwhile: no column; K5 is unmatched.
How to get the answer: check whether the database table SAMP_REVENUE_F, or another table of the SAMPLESALES schema, holds a customer identifier for each order, and bring back the table and column name; or decide whether K5 should be dropped or redefined (for example as the number of orders), and bring back the new wording to put at t2.r6.c3.
Answer:

### Q8. Are the people in "Dim Sales Rep" all sales representatives, and is a person's revenue only their own sales?
Item: V3-rep
FSD anchor: p12
Why it matters: V3 ranks the ten sales representatives with the highest net revenue. The proposed column is Dim Sales Rep.Employee Name (EMPLOYEE.EMP_NAME). The table reads EMPLOYEE joined to the parent-child table EMP_PARENT_CHILD, so it may hold managers as well as representatives, and a manager's revenue may include the revenue of everyone below them. Either would put managers at the top of the chart in place of representatives.
Assumed meanwhile: every person in EMPLOYEE who has revenue is a sales representative, and each is ranked on the revenue of their own orders.
How to get the answer: in the database table EMPLOYEE, look at whether a column or the MANAGER_ID values tell representatives from managers; and check, for one manager, whether the revenue OBIEE shows for them equals the sum of their own orders in SAMP_REVENUE_F or that sum plus their team's. Bring back how a sales representative is recognised and which of the two sums is meant.
Answer:

### Q9. Which amounts must follow the user's preferred currency, and where do the other currencies come from for discounts and targets?
Item: N1-currency
FSD anchor: p14
Why it matters: the FSD asks for amounts in the user's preferred currency where the source supports it. Sample Sales has Fact Revenue.Preferred Currency, a column computed from the user's session variable PREFERRED_CURRENCY that picks Revenue, Revenue (EUR) or Revenue (GBP). No such choice was found for Discnt Value or for the revenue target Fact Target.Revenue. If net revenue is shown in the preferred currency, its discount part would be in another currency unless a converted discount exists.
Assumed meanwhile: only revenue is shown in the preferred currency; discounts and targets stay in the currency they are stored in.
How to get the answer: decide whether "where the source supports it" means revenue only; and check whether the database tables SAMP_REVENUE_F and SAMP_TARGETS_F hold discount and target amounts in EUR and GBP. Bring back which amounts must follow the preferred currency and the database columns that hold each currency.
See also: K1
Answer:

## Answered questions
