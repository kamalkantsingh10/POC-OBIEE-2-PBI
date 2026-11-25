# Notes for the summary
## overview: What report authors see
- Subject area "Sample Sales" is built on the business model "SAMPLESALES"; when a query names no measure OBIEE uses "Fact Target"."Revenue" [kb:sampleapp::pres::Sample Sales].
- Report authors see 6 tables (Dim Offices, Dim Products, Dim Sales Rep, Dim Time, Fact Revenue, Fact Target) with 49 fields, 8 of them measures; behind them are 7 business tables with 63 fields, 11 of them measures; 12 sources, 13 database tables, 7 business-model joins, 17 database joins, 4 drill paths, 1 variable, 7 security rows (counted).
- The displayed name of "Dim Time" is a formula reading a per-user value: `VALUEOF(NQ_SESSION.CN_Sample_Sales_Dim_Time)` [kb:sampleapp::pres::Sample Sales.Dim Time].
- For the opening: Knowledge Base extracted at 2026-11-23T08:22:16Z by extractor version 0.1.0; only the rpd half of the estate was extracted; the estate has no Web Catalog half, so reports and dashboards were NOT examined. Errors about objects of the Slice: 0; kinds of source content not read: 131. Estate-wide items in no subject area: 2 object permissions with no recorded target, 1 init block, 1 variable (from the manifest).
- Questions: Q1 what name users see for the table "Dim Time"
## table-01-fact-revenue: Fact Revenue
- Fact Revenue has 22 fields, 9 measures, report authors see 15 (counted); 5 sources: Aggregate Revenue (SAMP_REVENUE_A, levels Month and Product Detail, priority group 1), Aggregate Revenue By Month (SAMP_REVENUE_A_MTH), Aggregate Revenue by Year (SAMP_REVENUE_A_YR), SAMP_REVENUE_F (detail), FactCrossJoin (a query, `select 1 Preserve from dual`) [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue] [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue].
- Shown measures: Revenue, Units (4 sources each), All Months Revenue, All Products Revenue, Discnt Value (2 sources each), Preserve (maximum, from the fixed query). All Months Revenue and All Products Revenue are summed from the same column as Revenue [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.All Months Revenue] [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.All Products Revenue].
- Hidden measures Revenue (EUR), (GBP), (INR) are REVENUE multiplied by 0.76, 0.62, 54.68 in source SAMP_REVENUE_F but not multiplied in source Aggregate Revenue [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.SAMP_REVENUE_F#map15] [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue#map8]. The shown field Preferred Currency picks Revenue, Revenue (EUR) or Revenue (GBP) from the per-user value PREFERRED_CURRENCY ('gc1','gc2','gc3') [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Preferred Currency].
- Questions: Q2 what distinguishes All Months Revenue / All Products Revenue from Revenue; Q3 what the EUR/GBP/INR measures hold and why multiplied in one source only; Q4 which source is used for Revenue/Units and whether totals agree; Q5 what Preserve is for; Q6 what Preferred Currency returns for other values of PREFERRED_CURRENCY (also asks its default and init query)
## table-02-fact-target: Fact Target
- Fact Target holds two measures, Revenue and Units, both summed, both from the database table SAMP_TARGETS_F, one source at the levels Dim Time Detail, Office Detail, Product Detail [kb:sampleapp::bmm::SAMPLESALES.Fact Target.src.SAMP_TARGETS_F].
- Both measures are writeable: users can type values in [kb:sampleapp::bmm::SAMPLESALES.Fact Target.Revenue] [kb:sampleapp::bmm::SAMPLESALES.Fact Target.Units].
- Questions: Q7 what the target measures hold and what happens to typed values
## table-03-dim-offices: Dim Offices
- Dim Offices has 10 fields, no measure, 9 shown; one source, the database table SAMP_OFFICES_D, at the level Office Detail of Office Hierarchy [kb:sampleapp::bmm::SAMPLESALES.Dim Offices.src.SAMP_OFFICES_D].
- The field Sequence is not shown to report authors [kb:sampleapp::bmm::SAMPLESALES.Dim Offices.Sequence].
- Questions: none
## table-04-dim-products: Dim Products
- Dim Products has 12 fields, no measure, 9 shown; one source, the database table SAMP_PRODUCTS_D, at the level Product Detail of "Products Hierararchy" (spelled so) [kb:sampleapp::bmm::SAMPLESALES.Dim Products.src.SAMP_PRODUCTS_D].
- The shown field "Prod Dsc (Translated)" is a lookup into the business table "Dim Products (Translated)" by Prod Key and the per-user value WEBLANGUAGE, so text can differ per user [kb:sampleapp::bmm::SAMPLESALES.Dim Products.Prod Dsc (Translated)].
- Hidden fields: Attribute 1, Attribute 2, Sequence.
- Questions: Q8 which languages Prod Dsc (Translated) covers, what WEBLANGUAGE holds, and what shows when no translation exists
## table-05-dim-products-translated: Dim Products (Translated)
- Dim Products (Translated) has 3 fields (LANG_ID, PROD_DESC, PROD_KEY), none shown to report authors; one source, the database table SAMP_PRODUCTS_D_TRANSLATION, level of detail not recorded [kb:sampleapp::bmm::SAMPLESALES.Dim Products (Translated).src.SAMP_PRODUCTS_D_TRANSLATION]. It exists to supply the lookup of "Prod Dsc (Translated)".
- Questions: none new (Q8 covers the languages; the section gives the exact query on LANG_ID)
## table-06-dim-sales-rep: Dim Sales Rep
- Dim Sales Rep has 5 fields, all shown, no measure; one source "LTS Employee" at the level Detail of Sales Rep Hierarchy, reading two database tables, EMPLOYEE (4 fields) and EMP_PARENT_CHILD (the field Distance) [kb:sampleapp::bmm::SAMPLESALES.Dim Sales Rep.src.LTS Employee].
- Questions: Q9 how the source combines EMPLOYEE and EMP_PARENT_CHILD (rows per employee) and what Distance holds
## table-07-dim-time: Dim Time
- Dim Time has 6 fields, all shown, no measure; two sources: "LTS Time Day" (SAMP_TIME_DAY_D, level Dim Time Detail of Time Hierarchy) and "LTS Time Month" (SAMP_TIME_MTH_D, level Month); Calendar Date and Per Name Week exist only in the day source [kb:sampleapp::bmm::SAMPLESALES.Dim Time.src.LTS Time Day] [kb:sampleapp::bmm::SAMPLESALES.Dim Time.src.LTS Time Month].
- The displayed names of all six fields are formulas reading per-user values named CN_Sample_Sales_Dim_Time_... [kb:sampleapp::pres::Sample Sales.Dim Time.Calendar Date].
- Questions: Q10 what names users see for the six fields of Dim Time (and which init block sets the CN_ values)
## joins: How the tables relate
- 7 business-model joins, all Inner: Fact Revenue joins Dim Time, Dim Sales Rep, Dim Offices, Dim Products; Fact Target joins Dim Time, Dim Offices, Dim Products (no join to Dim Sales Rep recorded; none for Dim Products (Translated)) [kb:sampleapp::bmm::joins.Relationship_2004:17511354089145] [kb:sampleapp::bmm::joins.Relationship_2004:11061353928600].
- 17 database joins: 3 from the query FactCrossJoin to SAMP_OFFICES_D, SAMP_PRODUCTS_D, EMPLOYEE with the condition `1=1` [kb:sampleapp::phys::joins.Complex Join]; 14 foreign keys whose far table and column were NOT recorded, e.g. [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F.SAMP_REVENUE_F_FKey].
- Questions: Q11 which table and column each of the 14 foreign keys points to; Q12 whether the differing row counts (0..n / n..n, 1..1 / 0..1) on business-model joins are intended
## hierarchies: Drill paths
- 4 drill paths, all offered to report authors: Office Hierarchy (Total, Company, Organization, Department, Office Detail keyed by Office Dsc) [kb:sampleapp::bmm::SAMPLESALES.Office Hierarchy]; Products Hierararchy (Total, Brand, Line Of Business, Products Type, Product Detail keyed by Prod Dsc) [kb:sampleapp::bmm::SAMPLESALES.Products Hierararchy]; Time Hierarchy (Total, Year, Half Year, Quarter, Month, Week, Dim Time Detail keyed by Calendar Date) [kb:sampleapp::bmm::SAMPLESALES.Time Hierarchy]; Sales Rep Hierarchy (Total, Detail keyed by Employee ID), recorded as parent-child with no fixed depth [kb:sampleapp::bmm::SAMPLESALES.Sales Rep Hierarchy].
- Level keys of the office and product paths are text fields, not the key fields [kb:sampleapp::bmm::SAMPLESALES.Office Hierarchy.Office Detail]. Which measures each drill path applies to is not stated by this packet beyond the levels of detail named by the sources.
- Questions: Q13 whether weeks nest in months and what the member numbers mean; Q14 whether the names used as level keys are unique; Q15 which field is the parent in Sales Rep Hierarchy and how deep it goes
## variables: Values set per user or for the whole system
- One variable is recorded: PREFERRED_CURRENCY, set per user at login, default `'gc1'` [kb:sampleapp::var::PREFERRED_CURRENCY]; set by the query `select 'VALUEOF(NQ_SESSION.PREFERED_CURRENCY)' from dual` (name spelled with one R, reads no table) [kb:sampleapp::var::init.initBlockPrefCurrency]; used only by Fact Revenue "Preferred Currency".
- WEBLANGUAGE and the seven CN_Sample_Sales_Dim_Time... values are read by formulas but have no variable row in the Slice (from the variables packet).
- Questions: Q16 what PREFERRED_CURRENCY holds at run time and how it is changed
## security: Security
- No row filter is recorded. All 6 recorded permissions are on "Fact Target"."Revenue" and "Units": write on both through one package, read on both through a second, read and write on Revenue through a third [kb:sampleapp::sec::obj.ObjPriv_1500:18131355661677.o0] [kb:sampleapp::sec::obj.ObjPriv_1500:18161355661677.o0] [kb:sampleapp::sec::obj.ObjPriv_1500:18211355661677.o0].
- The only holder recorded is a role given as a code, not a name; two packages have no holder recorded [kb:sampleapp::sec::pkg.PrivPack_4209:18101355661677.q0].
- Questions: Q17 which roles hold the permissions and what they are called
## reports: Reports built on this subject area
- Reports and dashboards were NOT examined: the estate has no Web Catalog half; calculations, filters and prompts defined inside reports are unknown (from the manifest).
- Questions: Q18 which reports and dashboards use the subject area and whether the Web Catalog can be exported
## gaps: What the export did not capture
- No extraction error about any object of the subject area (from the errors table). 131 kinds of unread detail over the whole estate (from the unparsed table); 15 rows of kind unmodelled_type (whole object types with no reader) left out of the Slice (from the manifest).
- Unread details that matter here: the far side of foreign keys (Q11); joins inside sources (Q9); parent-child settings (Q15); fields attached to levels, where level-fixed measures would be (Q2); group names and package owners (Q17); row-wise init blocks (Q1, Q8, Q10); keys of business and database tables, so no primary key is stated anywhere.
- Questions: Q19 which kinds of object have no reader at all
