### Values set per user or for the whole system

The slice's formulas, shown names and queries use 10 variable names inside VALUEOF; the repository defines 1 of them, and 9 are used by name and not defined in the repository.

#### Defined in the repository

Variable "PREFERRED_CURRENCY": session variable (a value per user, set at login); security mask `2`. [kb:sampleapp::var::PREFERRED_CURRENCY] Its default value is `'gc1'`. It is set by the init block "initBlockPrefCurrency": session true; enabled not recorded; refresh period not recorded. [kb:sampleapp::var::init.initBlockPrefCurrency] The init block's query, exactly:

```
select 'VALUEOF(NQ_SESSION.PREFERED_CURRENCY)' from dual
```

[kb:sampleapp::var::init.initBlockPrefCurrency]

The query names `PREFERED_CURRENCY`, with one R, which the repository does not define (below). The confirmed field "Preferred Currency" (item N1-currency) depends on this variable: it is decision Q2, already raised in the section Model table Fact Revenue. A Power BI model has no session variables; no model object is proposed for the variable until Q2 is answered.

#### Used by name, not defined in the repository

| Name | Where it is used | Bears on the tab |
|---|---|---|
| `CN_Sample_Sales_Dim_Time` | shown name of presentation table "Dim Time" | yes: the table holds the confirmed fields; decision Q9 |
| `CN_Sample_Sales_Dim_Time_Calendar_Date` | shown name of "Dim Time"."Calendar Date" | no: field not confirmed |
| `CN_Sample_Sales_Dim_Time_Per_Name_Half` | shown name of "Dim Time"."Per Name Half" | no: field not confirmed |
| `CN_Sample_Sales_Dim_Time_Per_Name_Month` | shown name of "Dim Time"."Per Name Month" | yes: item V1-month; decision Q9 |
| `CN_Sample_Sales_Dim_Time_Per_Name_Qtr` | shown name of "Dim Time"."Per Name Qtr" | no: field not confirmed |
| `CN_Sample_Sales_Dim_Time_Per_Name_Week` | shown name of "Dim Time"."Per Name Week" | no: field not confirmed |
| `CN_Sample_Sales_Dim_Time_Per_Name_Year` | shown name of "Dim Time"."Per Name Year" | yes: item F1; decision Q9 |
| `PREFERED_CURRENCY` | the query of the init block "initBlockPrefCurrency" | yes, through "Preferred Currency"; decision Q2 |
| `WEBLANGUAGE` | formula of "Dim Products"."Prod Dsc (Translated)" | no: field not confirmed |

Such a name can be a session variable the server sets itself, or one set outside the repository; the repository does not say which. Where their values are set is not recorded (see also as-is document, questions Q1 and Q2).
