## Values set per user or for the whole system

The expressions, filters and queries of this subject area name 2 variables inside VALUEOF(...). Of these, 1 is defined in the repository and 1 is used by name and not defined in the repository. The Slice holds 1 variable row (counted; no Row identifier).

### Defined in the repository

**BI_YEAR** is a value set once for the whole system (a "repository variable"); whether it is a session variable is not recorded [kb:bi-analytics-2013::var::BI_YEAR]. Its default value is `2010.0d` [kb:bi-analytics-2013::var::BI_YEAR].

It is set by the query "BI_IB" (an init block); whether that query runs per session, and whether it is enabled, are not recorded; its refresh period is 3600 [kb:bi-analytics-2013::var::init.BI_IB]. The query that sets the value, exactly [kb:bi-analytics-2013::var::init.BI_IB]:

```
SELECT MAX(EXTRACT (YEAR from EVENT_DT )) FROM FCT_TRANSACTIONS_DAILY
```

Put mechanically: the query takes the largest year found in the column EVENT_DT of the database table FCT_TRANSACTIONS_DAILY. The unit of the refresh period is not stated in the packet. What value BI_YEAR holds at run time is not recorded (open question Q13).

BI_YEAR is named in one place of this subject area: the query of the database table FCT_TRANSACTIONS_ACTUAL (see the section Database table FCT_TRANSACTIONS_ACTUAL) [kb:bi-analytics-2013::var::BI_YEAR].

### Used by name, not defined in the repository

| Name | Used in | Evidence |
|---|---|---|
| ROLEGUIDS | the content filter of the source "FCT_TRANSACTIONS_ACTUAL" (see the section FCT_TRANSACTIONS_ACTUAL); the content filter of the source "FCT_TRANSACTIONS_DAILY" (see the section TRANSACTIONS_DAILY); the row filter on each of these two sources (see the section Security) | listed in the packet without a Row identifier |

For ROLEGUIDS the Knowledge Base has no variable row, neither with exactly this name nor with one that differs only in letter case. The packet notes that such a name can be a session variable the server sets itself, or one set outside the repository; the repository does not say which. It is named inside VALUEOF(...) in 4 rows of this subject area. What it holds for a user at run time is not recorded (open question Q3).
