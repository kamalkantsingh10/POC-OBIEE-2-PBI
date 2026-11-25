# Variables: values set per user or per repository
Unit: variables
Kind: variables
Tab: test-tab (subject areas: Sample Sales; estate sampleapp)
Facts from: init_blocks, variables
Rows of this packet a document must cover: 1. Each is on a line that ends with (REQUIRED).
To cite a fact, copy the marker in square brackets that follows it, exactly as written. "->" points at another row.

## Summary (counted from the slice's tables; a count has no Row identifier)

- Variable names the slice's expressions, display-name formulas, filters and SQL texts use inside VALUEOF(...): 10.
- Of these names, defined in the repository (the Knowledge Base has a variables row): 1; used by name and not defined in the repository: 9.
- Variables rows in the slice: 1 (a variable named inside VALUEOF, or one a formula's reference list points at).

## Variables defined in the repository

- Variable "PREFERRED_CURRENCY": session variable (a value per user, set at login); security mask `2`. [kb:sampleapp::var::PREFERRED_CURRENCY] (REQUIRED)
    - Default value: `'gc1'` [kb:sampleapp::var::PREFERRED_CURRENCY]
    - Set by init block "initBlockPrefCurrency": session true; enabled not recorded; refresh period not recorded. [kb:sampleapp::var::init.initBlockPrefCurrency]
    - The init block's query: `select 'VALUEOF(NQ_SESSION.PREFERED_CURRENCY)' from dual` [kb:sampleapp::var::init.initBlockPrefCurrency]
        - Names variable `PREFERED_CURRENCY` inside VALUEOF(...): unit variables lists every such name and says whether the repository defines it.
    - Named exactly, inside VALUEOF(...), in 1 row(s) of the slice:
        - logical_columns -> sampleapp::bmm::SAMPLESALES.Fact Revenue.Preferred Currency (in derived_expr_text_desc)
    - Pointed at by a formula's reference list (a %n placeholder) in 1 row(s) of the slice:
        - logical_columns -> sampleapp::bmm::SAMPLESALES.Fact Revenue.Preferred Currency (in derived_expr_refs)

## Variable names used that the repository does not define

- For each name below the Knowledge Base has no variables row: not with exactly this name, and not with a name that differs only in letter case. Such a name can be a session variable the server sets itself, or one set outside the repository; the repository does not say which. State each as "used by name, not defined in the repository". (variables; no Row identifier)

- Name `CN_Sample_Sales_Dim_Time`: used by name, not defined in the repository. (variables; no Row identifier)
    - Named inside VALUEOF(...) in 1 row(s) of the slice:
        - presentation_tables -> sampleapp::pres::Sample Sales.Dim Time (in display_name)

- Name `CN_Sample_Sales_Dim_Time_Calendar_Date`: used by name, not defined in the repository. (variables; no Row identifier)
    - Named inside VALUEOF(...) in 1 row(s) of the slice:
        - presentation_columns -> sampleapp::pres::Sample Sales.Dim Time.Calendar Date (in display_name)

- Name `CN_Sample_Sales_Dim_Time_Per_Name_Half`: used by name, not defined in the repository. (variables; no Row identifier)
    - Named inside VALUEOF(...) in 1 row(s) of the slice:
        - presentation_columns -> sampleapp::pres::Sample Sales.Dim Time.Per Name Half (in display_name)

- Name `CN_Sample_Sales_Dim_Time_Per_Name_Month`: used by name, not defined in the repository. (variables; no Row identifier)
    - Named inside VALUEOF(...) in 1 row(s) of the slice:
        - presentation_columns -> sampleapp::pres::Sample Sales.Dim Time.Per Name Month (in display_name)

- Name `CN_Sample_Sales_Dim_Time_Per_Name_Qtr`: used by name, not defined in the repository. (variables; no Row identifier)
    - Named inside VALUEOF(...) in 1 row(s) of the slice:
        - presentation_columns -> sampleapp::pres::Sample Sales.Dim Time.Per Name Qtr (in display_name)

- Name `CN_Sample_Sales_Dim_Time_Per_Name_Week`: used by name, not defined in the repository. (variables; no Row identifier)
    - Named inside VALUEOF(...) in 1 row(s) of the slice:
        - presentation_columns -> sampleapp::pres::Sample Sales.Dim Time.Per Name Week (in display_name)

- Name `CN_Sample_Sales_Dim_Time_Per_Name_Year`: used by name, not defined in the repository. (variables; no Row identifier)
    - Named inside VALUEOF(...) in 1 row(s) of the slice:
        - presentation_columns -> sampleapp::pres::Sample Sales.Dim Time.Per Name Year (in display_name)

- Name `PREFERED_CURRENCY`: used by name, not defined in the repository. (variables; no Row identifier)
    - Named inside VALUEOF(...) in 1 row(s) of the slice:
        - init_blocks -> sampleapp::var::init.initBlockPrefCurrency (in init_string)

- Name `WEBLANGUAGE`: used by name, not defined in the repository. (variables; no Row identifier)
    - Named inside VALUEOF(...) in 1 row(s) of the slice:
        - logical_columns -> sampleapp::bmm::SAMPLESALES.Dim Products.Prod Dsc (Translated) (in derived_expr_text, derived_expr_text_desc)

