Status: reviewed 2026-11-23 (25 findings corrected, 1 turned into questions, 14 questions open)

# Sample Sales — the semantic model as it exists today

**What this is.** A plain-language description of the Sample Sales subject area in OBIEE: what report authors see, what each number is, how the tables relate, where the data comes from and what the database tables hold. It describes the existing system only.

**How to read it.** Statements of fact end with a marker in square brackets. The marker names the row of the Knowledge Base the statement came from, so any statement can be checked. A marker that starts with "answer" means the statement rests on an answer a person gave, not on OBIEE. Text shown as code is copied from OBIEE exactly. Where the export does not settle something, the text points to a numbered open question near the end; each says how to find the answer and has an empty answer line for you.

| | |
|---|---|
| Subject area | Sample Sales |
| Estate | sampleapp |
| Knowledge Base extracted | 2026-11-23T20:55:33Z |
| Reports and dashboards | Not examined: the estate has no Web Catalog half; only the repository half ("rpd") was extracted |

## The model in one page

**What was not examined.** Only the repository half of the estate was extracted. The estate has no Web Catalog half, so reports and dashboards were not examined, and calculations, filters and prompts defined inside reports are unknown (from the manifest; no Row identifier). The extraction recorded no error about an object of this subject area; 95 kinds of source content were not read, and 15 objects of three types that have no reader were left out (see the section What the export did not capture).

**What the subject area holds.** "Sample Sales" is built on the business model "SAMPLESALES" [kb:sampleapp::pres::Sample Sales]. Report authors see six tables: Dim Offices (9 fields), Dim Products (9), Dim Sales Rep (5), Dim Time (6), Fact Revenue (15) and Fact Target (5); 49 fields in all, of which 8 are measures. Behind them are 7 business tables with 63 fields and 11 measures, 12 sources, 13 database tables with 113 columns and 8 keys, 7 joins between business tables, 17 joins between database tables, 4 drill paths, 1 variable and 7 security rows (counted; no Row identifier).

- **Fact Revenue** has 9 measures (All Months Revenue, All Products Revenue, Discnt Value, Preserve, Revenue, Revenue (EUR), Revenue (GBP), Revenue (INR), Units) and five sources: "Aggregate Revenue", "Aggregate Revenue By Month", "Aggregate Revenue by Year", "FactCrossJoin" and "SAMP_REVENUE_F" [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue].
- **Fact Target** holds two measures, Revenue and Units, both summed, both from the database table SAMP_TARGETS_F [kb:sampleapp::bmm::SAMPLESALES.Fact Target.src.SAMP_TARGETS_F]. When a query names no measure, OBIEE uses "Fact Target"."Revenue" [kb:sampleapp::pres::Sample Sales].
- **Dimensions**: Dim Offices, keyed on "Office Key" [kb:sampleapp::bmm::SAMPLESALES.Dim Offices.key.SAMP_OFFICES_D_Key]; Dim Products, keyed on "Prod Key" [kb:sampleapp::bmm::SAMPLESALES.Dim Products.key.SAMP_PRODUCTS_D_Key]; Dim Products (Translated), a lookup table [kb:sampleapp::bmm::SAMPLESALES.Dim Products (Translated)]; Dim Sales Rep, keyed on "Employee ID" [kb:sampleapp::bmm::SAMPLESALES.Dim Sales Rep.key.EMP_ID]; Dim Time, keyed on "Calendar Date" [kb:sampleapp::bmm::SAMPLESALES.Dim Time.key.SAMP_TIME_DAY_D_Key].
- **Drill paths**: "Office Hierarchy" [kb:sampleapp::bmm::SAMPLESALES.Office Hierarchy], "Products Hierararchy" [kb:sampleapp::bmm::SAMPLESALES.Products Hierararchy], the parent-child "Sales Rep Hierarchy" [kb:sampleapp::bmm::SAMPLESALES.Sales Rep Hierarchy] and "Time Hierarchy" [kb:sampleapp::bmm::SAMPLESALES.Time Hierarchy].

**The database tables.**

| Database table | Columns | Key | Read by |
|---|---|---|---|
| EMPLOYEE | 4 | EMP_ID on EMP_ID | "LTS Employee" of Dim Sales Rep |
| EMP_PARENT_CHILD | 4 | EMP_PARENT_CHILD_Key on MEMBER_KEY | "LTS Employee" of Dim Sales Rep |
| FactCrossJoin (a query kept in the repository) | 1 | none recorded | "FactCrossJoin" of Fact Revenue |
| SAMP_OFFICES_D | 10 | SAMP_OFFICES_D_Key on OFFICE_KEY | "SAMP_OFFICES_D" of Dim Offices |
| SAMP_PRODUCTS_D | 11 | SAMP_PRODUCTS_D_Key on PROD_KEY | "SAMP_PRODUCTS_D" of Dim Products |
| SAMP_PRODUCTS_D_TRANSLATION | 3 | PK_PROD_TRANS1 on LANG_ID, PROD_KEY | "SAMP_PRODUCTS_D_TRANSLATION" of Dim Products (Translated) |
| SAMP_REVENUE_A | 5 | none recorded | "Aggregate Revenue" of Fact Revenue |
| SAMP_REVENUE_A_MTH | 4 | none recorded | "Aggregate Revenue By Month" of Fact Revenue |
| SAMP_REVENUE_A_YR | 4 | none recorded | "Aggregate Revenue by Year" of Fact Revenue |
| SAMP_REVENUE_F | 12 | none recorded | "SAMP_REVENUE_F" of Fact Revenue |
| SAMP_TARGETS_F | 5 | none recorded | "SAMP_TARGETS_F" of Fact Target |
| SAMP_TIME_DAY_D | 14 | SAMP_TIME_DAY_D_Key on CALENDAR_DATE | "LTS Time Day" of Dim Time |
| SAMP_TIME_MTH_D | 36 | SAMP_TIME_MTH_D_Key on MTH_KEY; SAMP_TIME_MTH_D_Key#1 on CAL_YEAR | "LTS Time Month" of Dim Time |

The repository does not say whether any key of a database table is its primary key. Each table's section gives its evidence.

**Before relying on a number.**

- "Revenue (EUR)", "Revenue (GBP)" and "Revenue (INR)" are the column REVENUE multiplied by 0.76, 0.62 and 54.68 in the source "SAMP_REVENUE_F" [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.SAMP_REVENUE_F#map15], but the plain column REVENUE in the source "Aggregate Revenue" [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue#map8] (open question Q4). "Revenue", "All Months Revenue" and "All Products Revenue" all map the same column REVENUE (open question Q3).
- Of the five sources of Fact Revenue, only "Aggregate Revenue" has a priority group recorded [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue]; which source a query uses is not recorded (open question Q5).
- Both measures of Fact Target can be typed into [kb:sampleapp::bmm::SAMPLESALES.Fact Target.Revenue] [kb:sampleapp::bmm::SAMPLESALES.Fact Target.Units], and "write" on them is given to "BI Author Role" [kb:sampleapp::sec::obj.ObjPriv_1500:18131355661677.o0] (open questions Q7 and Q12).
- The formula of "Preferred Currency" names the fields "Revenue", "Revenue (EUR)" and "Revenue (GBP)" and the variable PREFERRED_CURRENCY [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Preferred Currency], a session variable with the default 'gc1' [kb:sampleapp::var::PREFERRED_CURRENCY]; the query that sets it names PREFERED_CURRENCY, which the repository does not define (open question Q2).
- Dim Sales Rep joins EMPLOYEE to the closure table EMP_PARENT_CHILD on ANCESTOR_KEY [kb:sampleapp::bmm::SAMPLESALES.Dim Sales Rep.src.LTS Employee#table0.join0], and its field "Distance" comes from that table [kb:sampleapp::bmm::SAMPLESALES.Dim Sales Rep.src.LTS Employee#map4] (open question Q9).

**Open questions.** 14 questions are open, at the end of the document (counted).
