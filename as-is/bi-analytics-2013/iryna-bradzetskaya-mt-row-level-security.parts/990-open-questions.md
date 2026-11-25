## Open questions

These are the points the export could not settle. Each says how to find the answer and ends with an empty answer line. Write the answer on that line, or tell the agent; the document will then be updated.

### Q1. What do the measures AMOUNT_PAYMENT and COUNT_PAYMENT of TRANSACTIONS_DAILY hold for one row, and is it right to add COUNT_PAYMENT up?
Raised in: table-01-transactions-daily-p1
Why it matters: both measures are summarised with SUM by default and both are defined on the database columns AMOUNT_PAYMENT and COUNT_PAYMENT of FCT_TRANSACTIONS_DAILY. The export records no description of either column and nothing about the currency of the amount or what is counted, so a reader cannot tell what a total means.
Assumed meanwhile: nothing. Both measures are described with their rules exactly as recorded, and no meaning is stated.
How to get the answer: run these queries against the schema U_SAL and bring back the results: select count(*), sum(COUNT_PAYMENT), sum(AMOUNT_PAYMENT) from FCT_TRANSACTIONS_DAILY, and select * from FCT_TRANSACTIONS_DAILY where rownum <= 20. Bring back also any description of the two columns kept in the database or the load process that fills the table, and say whether one row of FCT_TRANSACTIONS_DAILY is one day per combination of the other columns.
Answer:

### Q2. What does the field PAYMENT_WHIS_TAX stand for, and why is AMOUNT_PAYMENT multiplied by 20?
Raised in: table-01-transactions-daily-p1
Why it matters: PAYMENT_WHIS_TAX is calculated as the measure AMOUNT_PAYMENT multiplied by the fixed number 20, is not itself a measure, and is mapped in no source. Where the number 20 comes from, whether it is a tax rate, a factor or a percentage, and whether the name means "with tax", are not recorded. A reader may take the field for an amount including tax when the formula gives twenty times the amount.
Assumed meanwhile: nothing. The formula is quoted exactly and no meaning is given to the number or the name.
How to get the answer: in the OBIEE Administration Tool, open the business table TRANSACTIONS_DAILY of the business model "Iryna_Bradzetskaya_MT_Row-level_Security", open the field PAYMENT_WHIS_TAX and bring back its description, if any, and its formula as shown. Bring back the rule or document the number 20 was taken from, if there is one, and say whether the intended result is AMOUNT_PAYMENT plus 20 percent or AMOUNT_PAYMENT times 20.
Answer:

### Q3. Which currencies do the numbers in the content filter of the source FCT_TRANSACTIONS_DAILY stand for, and what is a user who holds neither Role_MT_A nor Role_MT_F, or both, meant to see?
Raised in: table-01-transactions-daily-p2
Why it matters: the only source of TRANSACTIONS_DAILY carries a content filter of two conditions joined by "or": one pairs the text ";Role_MT_A;" in the value ROLEGUIDS with CURRENCY_SUR_ID in (12,10,15,11), the other pairs ";Role_MT_F;" with CURRENCY_SUR_ID in (13,14, 16,17). The export records which numbers are listed, not which currencies they are, and does not record whether ROLEGUIDS holds role names or role guids, or what the filter is meant to give a user who has neither role or both. Anyone rebuilding the access rule needs the intended result for each kind of user.
Assumed meanwhile: nothing. The filter is quoted exactly and no effect at run time is stated.
How to get the answer: run this query against the schema U_SAL and bring back the result: select * from DIM_CURRENCY_SCD where CURRENCY_ID in (10,11,12,13,14,15,16,17), or, if the key column has another name, select the rows of DIM_CURRENCY_SCD whose key holds these eight numbers. Then log in to OBIEE as a user who holds Role_MT_A only, as one who holds Role_MT_F only, and as one who holds neither, run where OBIEE lets you issue logical SQL: select VALUEOF(NQ_SESSION.ROLEGUIDS) from "Iryna_Bradzetskaya_MT_Row-level_Security", and a count of TRANSACTIONS_DAILY rows per CURRENCY_SUR_ID; bring back the three results, and say which result each kind of user is meant to get.
See also: table-02-currency, the key column of DIM_CURRENCY_SCD is CURRENCY_SUR_ID, so the first query is select * from DIM_CURRENCY_SCD where CURRENCY_SUR_ID in (10,11,12,13,14,15,16,17).
See also: table-05-fct-transactions-actual-p2, the source FCT_TRANSACTIONS_ACTUAL has the same filter but looks for ";MT_User_A;" and ";MT_User_F;" instead of ";Role_MT_A;" and ";Role_MT_F;"; say also which of these four names a user's ROLEGUIDS can hold.
Answer:

### Q4. What does CURRENCY_TO_DOLLAR hold, and can one currency have several rows in DIM_CURRENCY_SCD with different VALID_FROM and VALID_TO dates?
Raised in: table-02-currency
Why it matters: the business table CURRENCY offers CURRENCY_TO_DOLLAR (type DOUBLE) as a plain field, not a measure, and VALID_FROM and VALID_TO (type DATETIME); its key is CURRENCY_SUR_ID alone. The export does not say whether CURRENCY_TO_DOLLAR is an exchange rate and in which direction, nor which row of a currency applies on a given date. Whoever rebuilds currency conversion or the link from transactions to currency needs both.
Assumed meanwhile: nothing. The fields are described as recorded and no meaning is given to them.
How to get the answer: run these queries against the schema U_SAL and bring back the results: select CURRENCY_CODE, count(*), min(VALID_FROM), max(VALID_TO) from DIM_CURRENCY_SCD group by CURRENCY_CODE, and select * from DIM_CURRENCY_SCD order by CURRENCY_CODE, VALID_FROM. Bring back also the description of the column CURRENCY_TO_DOLLAR from the load process that fills the table, if one exists.
Answer:

### Q5. What do the endings "_REC" and "_SEND" in the names of the customer and location tables stand for?
Raised in: table-03-customers-rec-p1
Why it matters: the business table CUSTOMERS_REC, built on the database table DIM_CUSTOMERS_REC, has the key CUST_ID. The subject area also offers a table CUSTOMERS_SEND. The export records no description that says which party of a transaction each one is, so a report author cannot tell which customer a figure is broken down by.
Assumed meanwhile: nothing. The names are used as recorded and no role in a transaction is given to either table.
How to get the answer: in the OBIEE Administration Tool, open the business tables CUSTOMERS_REC and CUSTOMERS_SEND of the business model "Iryna_Bradzetskaya_MT_Row-level_Security" and bring back their descriptions, if any. Bring back also the description of the columns CUS_REC_ID and CUST_SEND_ID of the database table FCT_TRANSACTIONS_DAILY from the load process that fills it, and say which party of a transaction each column names.
See also: dbtable-03-dim-customers-send, both DIM_CUSTOMERS_REC and DIM_CUSTOMERS_SEND are queries with the same text, SELECT * FROM DIM_CUSTOMERS; say whether one customer table is meant to play two roles.
Answer:

### Q6. Should AMOUNT_PAYMENT and COUNT_PAYMENT of FCT_TRANSACTIONS_ACTUAL be totals that add up, and what do PERIOD_ID, TA_COUNTRY_REC_ID and TA_COUNTRY_SEND_ID hold?
Raised in: table-05-fct-transactions-actual-p1
Why it matters: the business table FCT_TRANSACTIONS_ACTUAL has 14 fields and no measures; AMOUNT_PAYMENT and COUNT_PAYMENT are recorded as "not a measure", with no rule for how they are summarised, and no key is declared for the table. In OBIEE generally, a field that is not a measure is shown row by row and is not added up by the query. The export does not say whether that is intended, nor what the three fields PERIOD_ID, TA_COUNTRY_REC_ID and TA_COUNTRY_SEND_ID hold.
Assumed meanwhile: nothing. The fields are described as recorded: plain fields, each defined on the column of the same name of FCT_TRANSACTIONS_ACTUAL.
How to get the answer: in the OBIEE Administration Tool, open the business table FCT_TRANSACTIONS_ACTUAL of the business model "Iryna_Bradzetskaya_MT_Row-level_Security" and bring back, for AMOUNT_PAYMENT and COUNT_PAYMENT, the aggregation rule shown, if any, and any description of the table. Run against the schema U_SAL and bring back: select PERIOD_ID, TA_COUNTRY_REC_ID, TA_COUNTRY_SEND_ID, count(*) from FCT_TRANSACTIONS_ACTUAL group by PERIOD_ID, TA_COUNTRY_REC_ID, TA_COUNTRY_SEND_ID, and say what each of the three columns names (a period, a country, something else) and what one row of the table stands for.
See also: dbtable-10-fct-transactions-actual, the table is a query SELECT * FROM FCT_TRANSACTIONS_DAILY limited to the year VALUEOF(BI_YEAR); say whether the database table FCT_TRANSACTIONS_DAILY has the columns PERIOD_ID, TA_COUNTRY_REC_ID and TA_COUNTRY_SEND_ID.
Answer:

### Q7. In the location tables, which column identifies one row (GEO_ID or GEO_SURR_ID), what does LEVEL_CODE hold, and can one location have several rows by VALID_FROM and VALID_TO?
Raised in: table-06-locations-rec-p1
Why it matters: the business table LOCATIONS_REC has the key PC_LOC on GEO_ID, and also offers GEO_SURR_ID, LEVEL_CODE, VALID_FROM and VALID_TO, together with country, region and "part" columns. The export does not say whether a row is a country, a region or a part depending on LEVEL_CODE, nor whether GEO_ID stays the same across versions of a location. Whoever rebuilds the link from transactions to locations needs to know which column to join on and whether dates must be compared.
Assumed meanwhile: nothing. The key is stated as recorded (GEO_ID) and no meaning is given to the other columns.
How to get the answer: run these queries against the schema U_SAL and bring back the results: select LEVEL_CODE, count(*), count(distinct GEO_ID), count(distinct GEO_SURR_ID) from DIM_LOCATIONS_REC group by LEVEL_CODE, and select * from DIM_LOCATIONS_REC where rownum <= 20. If DIM_LOCATIONS_REC is a query kept in the repository and cannot be selected from directly, run the query given in the section Database table DIM_LOCATIONS_REC instead.
Answer:

### Q8. What do TARIFF_MIN_PAYMENT, TARIFF_MAX_PAYMENT and TARIFF_PAYMENT_SUM of TARIFFS hold, and are they meant to be used with the amount of a transaction?
Raised in: table-09-tariffs
Why it matters: the business table TARIFFS offers the three as plain fields, not measures, each mapped from the column of the same name of DIM_TARIFFS (type DOUBLE). The export records no description of them and no formula that uses them, so it cannot be said whether they are limits, a fixed fee or a percentage, or whether any report figure is meant to be computed from them.
Assumed meanwhile: nothing. The fields are described as recorded and no meaning is given to them.
How to get the answer: run against the schema U_SAL and bring back: select * from DIM_TARIFFS. Bring back also any description of these three columns from the load process that fills DIM_TARIFFS, and say whether a payment's fee is worked out from them and how.
Answer:

### Q9. Does the database table DIM_OPERATIONS hold the columns OPERATION_METHOD_NAME, OPERATION_METHOD_TYPE and OPERATION_METHOD_TYPE_ID, and is OPERATION_METHOD_ID unique in the result of the query DIM_OPERATION_METHODS?
Raised in: dbtable-07-dim-operation-methods
Why it matters: DIM_OPERATION_METHODS is a query kept in the repository that selects the distinct combinations of OPERATION_METHOD_ID, OPERATION_METHOD_NAME, OPERATION_METHOD_TYPE and OPERATION_METHOD_TYPE_ID from DIM_OPERATIONS where OPERATION_METHOD_ID is not null. Its key PK_OP_M is OPERATION_METHOD_ID alone. If one method id appears with two names or types, the query returns two rows for it, and the join from operations to methods then pairs an operation with more than one method row.
Assumed meanwhile: nothing. The query is quoted exactly and the key is stated as recorded.
How to get the answer: run against the schema U_SAL and bring back the result: select OPERATION_METHOD_ID, count(*) from (SELECT DISTINCT OPERATION_METHOD_ID, OPERATION_METHOD_NAME, OPERATION_METHOD_TYPE, OPERATION_METHOD_TYPE_ID FROM DIM_OPERATIONS WHERE OPERATION_METHOD_ID IS NOT NULL) group by OPERATION_METHOD_ID having count(*) > 1. Bring back also the column list of the database table DIM_OPERATIONS as the database describes it.
Answer:

### Q10. What are YEAR_Q, YEAR_M and YEAR_W of DIM_TIMES_DAILY meant to identify, and what does CALENDAR_WEEK_NUMBER hold?
Raised in: dbtable-09-dim-times-daily
Why it matters: the query DIM_TIMES_DAILY builds YEAR_Q, YEAR_M and YEAR_W by joining the texts CALENDAR_YEAR, CALENDAR_QUARTER_NUMBER, CALENDAR_MONTH_NUMBER and CALENDAR_WEEK_NUMBER end to end and turning the result into a number. CALENDAR_WEEK_NUMBER is recorded as VARCHAR with precision 1, and CALENDAR_MONTH_NUMBER as VARCHAR with precision 2. Whether the month number carries a leading zero, and whether the week number is a week of the month or of the year, decide whether these numbers sort in time order and whether two periods can get the same number; the export does not say.
Assumed meanwhile: nothing. The query is quoted exactly; no meaning is given to the three numbers.
How to get the answer: run against the schema U_SAL and bring back the result: select CALENDAR_YEAR, CALENDAR_QUARTER_NUMBER, CALENDAR_MONTH_NUMBER, CALENDAR_WEEK_NUMBER, count(*) from DIM_TIMES_DAILY group by CALENDAR_YEAR, CALENDAR_QUARTER_NUMBER, CALENDAR_MONTH_NUMBER, CALENDAR_WEEK_NUMBER order by 1, 2, 3, 4. Say also which period YEAR_W is meant to stand for in reports.
Answer:

### Q11. Should FCT_TRANSACTIONS_ACTUAL be reportable by customer and by location, and is the absence of those business-model joins intended?
Raised in: joins
Why it matters: in the business model, TRANSACTIONS_DAILY is joined to all eight descriptive tables, but FCT_TRANSACTIONS_ACTUAL only to TIMES_DAILY, CURRENCY, OPERATIONS and TARIFFS. In the database, foreign keys from FCT_TRANSACTIONS_ACTUAL to DIM_CUSTOMERS_REC, DIM_CUSTOMERS_SEND, DIM_LOCATIONS_REC and DIM_LOCATIONS_SEND are recorded. As recorded, the business model offers no path from FCT_TRANSACTIONS_ACTUAL to the customer and location tables; whether that is a choice or an omission is not recorded.
Assumed meanwhile: nothing. The joins are listed exactly as recorded.
How to get the answer: in the OBIEE Administration Tool, open the business model diagram of "Iryna_Bradzetskaya_MT_Row-level_Security" and bring back the list of joins of FCT_TRANSACTIONS_ACTUAL. Then, as a report author, build an analysis with FCT_TRANSACTIONS_ACTUAL.AMOUNT_PAYMENT and CUSTOMERS_REC.CUST_ID and bring back what OBIEE returns or the error it shows. Say whether such reports are needed.
Answer:

### Q12. Are the levels Quarter, Month and Week of the drill path "Times" meant to be identified by their number alone, or by the number together with the year?
Raised in: hierarchies-p2
Why it matters: the primary key of level Quarter is CALENDAR_QUARTER_NUMBER, of Month is CALENDAR_MONTH_NUMBER, and of Week is CALENDAR_WEEK_NUMBER; the keys YEAR_Q, YEAR_M and YEAR_W, built with the year, are recorded only as further keys (YEAR_Q and YEAR_M as chronological). The member counts are recorded as 12 years, 48 quarters, 144 months, 650 weeks and 4400 days. Whether a drill from one year to a quarter is meant to show that year's quarter only, and how weeks and days are numbered, decides how totals are grouped.
Assumed meanwhile: nothing. Keys and member counts are stated as recorded.
How to get the answer: in the OBIEE Administration Tool, open the drill path "Times" of the business model "Iryna_Bradzetskaya_MT_Row-level_Security" and bring back, for each of the levels Quarter, Month and Week, which key is marked as the primary key and which is used for display. Then, as a report author, drill in the hierarchy "Times - Quarter - Week" from one year down to Week and bring back the rows shown. See also Q10 for what YEAR_W holds.
Answer:

### Q13. What value does BI_YEAR hold at run time, and is FCT_TRANSACTIONS_ACTUAL meant to show only the latest year of FCT_TRANSACTIONS_DAILY?
Raised in: variables
Why it matters: BI_YEAR is a repository variable with the default `2010.0d`, set by the init block "BI_IB" with the query SELECT MAX(EXTRACT (YEAR from EVENT_DT )) FROM FCT_TRANSACTIONS_DAILY and a refresh period of 3600. The database table FCT_TRANSACTIONS_ACTUAL is a query that keeps the rows of FCT_TRANSACTIONS_DAILY whose year of EVENT_DT equals BI_YEAR. Whether the init block is enabled is not recorded, so it cannot be said whether the value is the default or the result of the query.
Assumed meanwhile: nothing. The default, the query and the refresh period are quoted as recorded; no run-time value is stated.
How to get the answer: log in to OBIEE and, where OBIEE lets you issue logical SQL, run select VALUEOF(BI_YEAR) from "Iryna_Bradzetskaya_MT_Row-level_Security" and bring back the value. In the OBIEE Administration Tool, open the init block "BI_IB" and bring back whether it is enabled and the unit of its refresh period. Run against the schema U_SAL: SELECT MAX(EXTRACT (YEAR from EVENT_DT )) FROM FCT_TRANSACTIONS_DAILY, and bring back the result. Say whether "actual" is meant to mean the latest year.
Answer:

### Q14. What are "Role_MT_A", "Role_MT_F", "MT_User_A" and "MT_User_F", and which users hold each of them?
Raised in: security
Why it matters: the two row filters, and the content filters of the same two sources, decide which rows of TRANSACTIONS_DAILY and FCT_TRANSACTIONS_ACTUAL a user sees by looking for these four texts in ROLEGUIDS. The Slice records no role, permission or privilege package for any of them, and ROLEGUIDS is not defined in the repository. Anyone rebuilding the access rule needs the list of users behind each name, and needs to know why one table uses "Role_MT_" names and the other "MT_User_" names.
Assumed meanwhile: nothing. The filters are quoted exactly; no user or group is named as holding any of the four.
How to get the answer: in the OBIEE Administration Tool, open the identity manager and bring back, for each of the four names, whether it exists as an application role, a group or a user, and its members. Then log in as one member of each and, where OBIEE lets you issue logical SQL, run select VALUEOF(NQ_SESSION.ROLEGUIDS) from "Iryna_Bradzetskaya_MT_Row-level_Security" and bring back the four values.
See also: gaps, the display names, guids and descriptions of groups were not read, and the 72 query privileges belong to a type of object the extraction has no reader for.
Answer:

