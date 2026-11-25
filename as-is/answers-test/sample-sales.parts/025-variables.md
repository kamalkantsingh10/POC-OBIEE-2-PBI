## Values set per user or for the whole system

The formulas, shown names, filters and query texts of this subject area name 10 variables inside VALUEOF(...); 1 of these names is defined in the repository and 9 are used by name and not defined in it (counted).

### Defined in the repository

**PREFERRED_CURRENCY** is a value set per user at login (a session variable), with security mask `2` [kb:sampleapp::var::PREFERRED_CURRENCY]. Its default is `'gc1'` [kb:sampleapp::var::PREFERRED_CURRENCY]. It is set by the query (init block) "initBlockPrefCurrency": session true; enabled not recorded; refresh period not recorded [kb:sampleapp::var::init.initBlockPrefCurrency]. That query, exactly [kb:sampleapp::var::init.initBlockPrefCurrency]:

```
select 'VALUEOF(NQ_SESSION.PREFERED_CURRENCY)' from dual
```

The query names PREFERED_CURRENCY, with one R, which is listed below as not defined in the repository. PREFERRED_CURRENCY is used in the formula of the field "Preferred Currency" of Fact Revenue (see the section Fact Revenue); what it holds for a user at run time is not recorded (open question Q2).

### Used by name and not defined in the repository

For each name below the export has no definition, under this name or one that differs only in letter case. Such a name can be set by the server itself or outside the repository; the repository does not say which (listed in the packet without a Row identifier).

| Name | Used in |
|---|---|
| CN_Sample_Sales_Dim_Time | the shown name of the table "Dim Time" |
| CN_Sample_Sales_Dim_Time_Calendar_Date | the shown name of the field "Calendar Date" of "Dim Time" |
| CN_Sample_Sales_Dim_Time_Per_Name_Half | the shown name of the field "Per Name Half" of "Dim Time" |
| CN_Sample_Sales_Dim_Time_Per_Name_Month | the shown name of the field "Per Name Month" of "Dim Time" |
| CN_Sample_Sales_Dim_Time_Per_Name_Qtr | the shown name of the field "Per Name Qtr" of "Dim Time" |
| CN_Sample_Sales_Dim_Time_Per_Name_Week | the shown name of the field "Per Name Week" of "Dim Time" |
| CN_Sample_Sales_Dim_Time_Per_Name_Year | the shown name of the field "Per Name Year" of "Dim Time" |
| PREFERED_CURRENCY | the query of the init block "initBlockPrefCurrency" |
| WEBLANGUAGE | the formula of the field "Prod Dsc (Translated)" of "Dim Products" |

Each is used by name and not defined in the repository. What any of them holds at run time is not recorded (open questions Q1, Q2 and Q8).
