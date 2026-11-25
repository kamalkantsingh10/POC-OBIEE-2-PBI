## Database table DIM_TIMES_DAILY

DIM_TIMES_DAILY is listed in `Iryna_Bradzetskaya_MT.U_SAL` with type `select`: it is not a table of the database but a query kept in the repository. It is not an alias of another table, and no description is recorded [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_TIMES_DAILY]. Its query, for the database type `DefaultMulDB`, is quoted here exactly as extracted [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_TIMES_DAILY.def.DefaultMulDB]:

```
SELECT TIME_ID,
  DAY_NAME,
  DAY_NUMBER_IN_WEEK,
  DAY_NUMBER_IN_MONTH,
  DAY_NUMBER_IN_YEAR,
  CALENDAR_WEEK_NUMBER,
  WEEK_ENDING_DATE,
  CALENDAR_MONTH_NUMBER,
  DAYS_IN_CAL_MONTH,
  END_OF_CAL_MONTH,
  CALENDAR_MONTH_NAME,
  DAYS_IN_CAL_QUARTER,
  BEG_OF_CAL_QUARTER,
  END_OF_CAL_QUARTER,
  CALENDAR_QUARTER_NUMBER,
  CALENDAR_YEAR,
  DAYS_IN_CAL_YEAR,
  BEG_OF_CAL_YEAR,
  END_OF_CAL_YEAR,
  TO_NUMBER (CALENDAR_YEAR || CALENDAR_QUARTER_NUMBER) AS YEAR_Q,
  TO_NUMBER (CALENDAR_YEAR || CALENDAR_MONTH_NUMBER) AS YEAR_M,
  TO_NUMBER (CALENDAR_YEAR || CALENDAR_QUARTER_NUMBER || CALENDAR_MONTH_NUMBER || CALENDAR_WEEK_NUMBER) AS YEAR_W
FROM DIM_TIMES_DAILY
```

Put mechanically: the query reads a database table also named DIM_TIMES_DAILY, passes 19 of its columns through unchanged, and adds three columns. YEAR_Q is `TO_NUMBER (CALENDAR_YEAR || CALENDAR_QUARTER_NUMBER)`, YEAR_M is `TO_NUMBER (CALENDAR_YEAR || CALENDAR_MONTH_NUMBER)`, and YEAR_W is `TO_NUMBER (CALENDAR_YEAR || CALENDAR_QUARTER_NUMBER || CALENDAR_MONTH_NUMBER || CALENDAR_WEEK_NUMBER)`: each joins the texts of the named columns end to end and turns the result into a number [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_TIMES_DAILY.def.DefaultMulDB]. What YEAR_W is meant to identify is not recorded (open question Q10). The export lists 22 columns for it, all 22 mapped by a field of this subject area and none not mapped, and 1 key (counted).

### Columns

| # | Column | Type | Precision | Scale | Nullable | External name | Mapped by | Evidence |
|---|---|---|---|---|---|---|---|---|
| 1 | BEG_OF_CAL_QUARTER | DATETIME | not recorded | not recorded | true | not recorded | TIMES_DAILY, BEG_OF_CAL_QUARTER | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_TIMES_DAILY.BEG_OF_CAL_QUARTER] |
| 2 | BEG_OF_CAL_YEAR | DATETIME | not recorded | not recorded | true | not recorded | TIMES_DAILY, BEG_OF_CAL_YEAR | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_TIMES_DAILY.BEG_OF_CAL_YEAR] |
| 3 | CALENDAR_MONTH_NAME | VARCHAR | 9 | not recorded | true | not recorded | TIMES_DAILY, CALENDAR_MONTH_NAME | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_TIMES_DAILY.CALENDAR_MONTH_NAME] |
| 4 | CALENDAR_MONTH_NUMBER | VARCHAR | 2 | not recorded | true | not recorded | TIMES_DAILY, CALENDAR_MONTH_NUMBER | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_TIMES_DAILY.CALENDAR_MONTH_NUMBER] |
| 5 | CALENDAR_QUARTER_NUMBER | VARCHAR | 1 | not recorded | true | not recorded | TIMES_DAILY, CALENDAR_QUARTER_NUMBER | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_TIMES_DAILY.CALENDAR_QUARTER_NUMBER] |
| 6 | CALENDAR_WEEK_NUMBER | VARCHAR | 1 | not recorded | true | not recorded | TIMES_DAILY, CALENDAR_WEEK_NUMBER | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_TIMES_DAILY.CALENDAR_WEEK_NUMBER] |
| 7 | CALENDAR_YEAR | VARCHAR | 4 | not recorded | true | not recorded | TIMES_DAILY, CALENDAR_YEAR | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_TIMES_DAILY.CALENDAR_YEAR] |
| 8 | DAYS_IN_CAL_MONTH | VARCHAR | 2 | not recorded | true | not recorded | TIMES_DAILY, DAYS_IN_CAL_MONTH | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_TIMES_DAILY.DAYS_IN_CAL_MONTH] |
| 9 | DAYS_IN_CAL_QUARTER | DOUBLE | 22 | not recorded | true | not recorded | TIMES_DAILY, DAYS_IN_CAL_QUARTER | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_TIMES_DAILY.DAYS_IN_CAL_QUARTER] |
| 10 | DAYS_IN_CAL_YEAR | DOUBLE | 22 | not recorded | true | not recorded | TIMES_DAILY, DAYS_IN_CAL_YEAR | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_TIMES_DAILY.DAYS_IN_CAL_YEAR] |
| 11 | DAY_NAME | VARCHAR | 9 | not recorded | true | not recorded | TIMES_DAILY, DAY_NAME | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_TIMES_DAILY.DAY_NAME] |
| 12 | DAY_NUMBER_IN_MONTH | VARCHAR | 2 | not recorded | true | not recorded | TIMES_DAILY, DAY_NUMBER_IN_MONTH | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_TIMES_DAILY.DAY_NUMBER_IN_MONTH] |
| 13 | DAY_NUMBER_IN_WEEK | VARCHAR | 1 | not recorded | true | not recorded | TIMES_DAILY, DAY_NUMBER_IN_WEEK | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_TIMES_DAILY.DAY_NUMBER_IN_WEEK] |
| 14 | DAY_NUMBER_IN_YEAR | VARCHAR | 3 | not recorded | true | not recorded | TIMES_DAILY, DAY_NUMBER_IN_YEAR | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_TIMES_DAILY.DAY_NUMBER_IN_YEAR] |
| 15 | END_OF_CAL_MONTH | DATETIME | 3 | not recorded | true | not recorded | TIMES_DAILY, END_OF_CAL_MONTH | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_TIMES_DAILY.END_OF_CAL_MONTH] |
| 16 | END_OF_CAL_QUARTER | DATETIME | 3 | not recorded | true | not recorded | TIMES_DAILY, END_OF_CAL_QUARTER | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_TIMES_DAILY.END_OF_CAL_QUARTER] |
| 17 | END_OF_CAL_YEAR | DATETIME | 3 | not recorded | true | not recorded | TIMES_DAILY, END_OF_CAL_YEAR | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_TIMES_DAILY.END_OF_CAL_YEAR] |
| 18 | TIME_ID | DATETIME | 3 | not recorded | not stated | not recorded | TIMES_DAILY, TIME_ID | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_TIMES_DAILY.TIME_ID] |
| 19 | WEEK_ENDING_DATE | DATETIME | 3 | not recorded | true | not recorded | TIMES_DAILY, WEEK_ENDING_DATE | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_TIMES_DAILY.WEEK_ENDING_DATE] |
| 20 | YEAR_M | INT | not recorded | not recorded | not stated | not recorded | TIMES_DAILY, YEAR_M | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_TIMES_DAILY.YEAR_M] |
| 21 | YEAR_Q | INT | not recorded | not recorded | not stated | not recorded | TIMES_DAILY, YEAR_Q | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_TIMES_DAILY.YEAR_Q] |
| 22 | YEAR_W | INT | not recorded | not recorded | not stated | not recorded | TIMES_DAILY, YEAR_W | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_TIMES_DAILY.YEAR_W] |

Nullable: the export records this only when it is true. "Not stated" means the export does not carry it; it is not a statement that the column must be filled.

### Keys

| Key | Columns | Evidence |
|---|---|---|
| DIM_TIMES_DAILY_PK | TIME_ID | [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_TIMES_DAILY.key.DIM_TIMES_DAILY_PK] |

This is key 1 of 1 the repository declares for the table. The repository does not say whether a physical key is the primary key; this is the only key it declares. Two foreign keys of this subject area point at exactly this column [kb:bi-analytics-2013::phys::Iryna_Bradzetskaya_MT.U_SAL.DIM_TIMES_DAILY.key.DIM_TIMES_DAILY_PK].

### Foreign keys and joins

- Foreign key FK_TRANS_TIMES from FCT_TRANSACTIONS_ACTUAL (EVENT_DT) to this table (TIME_ID); stated in full in the section How the tables relate.
- Foreign key FK_TRANS_TIMES from FCT_TRANSACTIONS_DAILY (EVENT_DT) to this table (TIME_ID); stated in full in the section How the tables relate.

### Read by

The source "DIM_TIMES_DAILY" of the business table TIMES_DAILY (listed in the packet without a Row identifier).
