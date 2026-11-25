## Values set per user or for the whole system

These counts come from the Slice and have no Row identifier:

- Variable names used inside VALUEOF(...) by the subject area's formulas, display-name formulas, filters and SQL texts: 10.
- Of these, defined in the repository: 1; used by name and not defined in the repository: 9.
- Variables of the repository in this subject area: 1.

### Variables the repository defines

#### PREFERRED_CURRENCY

PREFERRED_CURRENCY is a value set per user at login (a "session variable"); its security mask is `2` [kb:sampleapp::var::PREFERRED_CURRENCY]. Its default value is exactly `'gc1'` [kb:sampleapp::var::PREFERRED_CURRENCY].

It is set by the query that sets the value (the "init block") "initBlockPrefCurrency": session true; enabled not recorded; refresh period not recorded [kb:sampleapp::var::init.initBlockPrefCurrency]. That query is exactly [kb:sampleapp::var::init.initBlockPrefCurrency]:

```
select 'VALUEOF(NQ_SESSION.PREFERED_CURRENCY)' from dual
```

The text inside the quotation marks names PREFERED_CURRENCY, with one R; that name is listed below as used by name and not defined in the repository.

Where the subject area uses it: the formula of the field "Preferred Currency" of Fact Revenue names it inside VALUEOF(...), and that formula's reference list points at it (see the section Fact Revenue; listed in the packet without a Row identifier). What value a user has at run time is not recorded (open question Q2).

### Names used and not defined in the repository

For each name below the Knowledge Base has no variable, neither with exactly this name nor with a name that differs only in letter case. In the packet's words, such a name can be a session variable the server sets itself, or one set outside the repository; the repository does not say which. Each is "used by name, not defined in the repository" (no Row identifier).

| Name | Used in |
|---|---|
| CN_Sample_Sales_Dim_Time | the name shown for the table "Dim Time" (see the section What report authors see) |
| CN_Sample_Sales_Dim_Time_Calendar_Date | the name shown for the field "Calendar Date" of Dim Time (see the section Dim Time) |
| CN_Sample_Sales_Dim_Time_Per_Name_Half | the name shown for the field "Per Name Half" of Dim Time |
| CN_Sample_Sales_Dim_Time_Per_Name_Month | the name shown for the field "Per Name Month" of Dim Time |
| CN_Sample_Sales_Dim_Time_Per_Name_Qtr | the name shown for the field "Per Name Qtr" of Dim Time |
| CN_Sample_Sales_Dim_Time_Per_Name_Week | the name shown for the field "Per Name Week" of Dim Time |
| CN_Sample_Sales_Dim_Time_Per_Name_Year | the name shown for the field "Per Name Year" of Dim Time |
| PREFERED_CURRENCY | the query of the init block "initBlockPrefCurrency" (above) |
| WEBLANGUAGE | the formula of the field "Prod Dsc (Translated)" of Dim Products (see the section Dim Products) |

What these values hold at run time is not recorded (open questions Q1, Q2 and Q8).
