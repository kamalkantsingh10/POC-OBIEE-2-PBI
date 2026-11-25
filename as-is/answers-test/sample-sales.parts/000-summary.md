Status: draft, not reviewed

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

**What was not examined.** Only the repository half of the estate was extracted. The estate has no Web Catalog half, so reports and dashboards were not examined (from the manifest; no Row identifier). The extraction recorded no error about an object of this subject area; 95 kinds of source content were not read, and 15 objects of three types that have no reader were left out (see the section What the export did not capture).

**What the subject area holds.** "Sample Sales" is built on the business model "SAMPLESALES"; report authors see six tables with 49 fields, of which 8 are measures; when a query names no measure OBIEE uses "Fact Target"."Revenue" [kb:sampleapp::pres::Sample Sales].

- **Fact Revenue** has 22 fields, 9 of them measures, five sources and no key recorded [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue].
- **Fact Target** holds two measures, Revenue and Units, both summed, both from the database table SAMP_TARGETS_F [kb:sampleapp::bmm::SAMPLESALES.Fact Target.src.SAMP_TARGETS_F]; both are writeable [kb:sampleapp::bmm::SAMPLESALES.Fact Target.Revenue] [kb:sampleapp::bmm::SAMPLESALES.Fact Target.Units].
- **Dimensions**: Dim Offices, keyed on "Office Key" [kb:sampleapp::bmm::SAMPLESALES.Dim Offices.key.SAMP_OFFICES_D_Key]; Dim Products, keyed on "Prod Key" [kb:sampleapp::bmm::SAMPLESALES.Dim Products.key.SAMP_PRODUCTS_D_Key]; Dim Products (Translated), a lookup table keyed on PROD_KEY and LANG_ID [kb:sampleapp::bmm::SAMPLESALES.Dim Products (Translated).key.PK_PROD_TRANS1]; Dim Sales Rep, keyed on "Employee ID" [kb:sampleapp::bmm::SAMPLESALES.Dim Sales Rep.key.EMP_ID]; Dim Time, keyed on "Calendar Date" [kb:sampleapp::bmm::SAMPLESALES.Dim Time.key.SAMP_TIME_DAY_D_Key].
- **Drill paths**: "Office Hierarchy" [kb:sampleapp::bmm::SAMPLESALES.Office Hierarchy], "Products Hierararchy" [kb:sampleapp::bmm::SAMPLESALES.Products Hierararchy], the parent-child "Sales Rep Hierarchy" [kb:sampleapp::bmm::SAMPLESALES.Sales Rep Hierarchy] and "Time Hierarchy" [kb:sampleapp::bmm::SAMPLESALES.Time Hierarchy].

**The database tables.**

| Database table | Columns | Key | Read by | Evidence |
|---|---|---|---|---|
| EMPLOYEE | 4 | EMP_ID on EMP_ID | "LTS Employee" of Dim Sales Rep | [kb:sampleapp::phys::xe.SAMPLESALES.EMPLOYEE] |
| EMP_PARENT_CHILD | 4 | EMP_PARENT_CHILD_Key on MEMBER_KEY | "LTS Employee" of Dim Sales Rep | [kb:sampleapp::phys::xe.SAMPLESALES.EMP_PARENT_CHILD] |
| FactCrossJoin (a query kept in the repository) | 1 | none recorded | "FactCrossJoin" of Fact Revenue | [kb:sampleapp::phys::xe.SAMPLESALES.FactCrossJoin] |
| SAMP_OFFICES_D | 10 | SAMP_OFFICES_D_Key on OFFICE_KEY | "SAMP_OFFICES_D" of Dim Offices | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_OFFICES_D] |
| SAMP_PRODUCTS_D | 11 | SAMP_PRODUCTS_D_Key on PROD_KEY | "SAMP_PRODUCTS_D" of Dim Products | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_PRODUCTS_D] |
| SAMP_PRODUCTS_D_TRANSLATION | 3 | PK_PROD_TRANS1 on LANG_ID, PROD_KEY | "SAMP_PRODUCTS_D_TRANSLATION" of Dim Products (Translated) | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_PRODUCTS_D_TRANSLATION] |
| SAMP_REVENUE_A | 5 | none recorded | "Aggregate Revenue" of Fact Revenue | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A] |
| SAMP_REVENUE_A_MTH | 4 | none recorded | "Aggregate Revenue By Month" of Fact Revenue | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A_MTH] |
| SAMP_REVENUE_A_YR | 4 | none recorded | "Aggregate Revenue by Year" of Fact Revenue | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A_YR] |
| SAMP_REVENUE_F | 12 | none recorded | "SAMP_REVENUE_F" of Fact Revenue | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F] |
| SAMP_TARGETS_F | 5 | none recorded | "SAMP_TARGETS_F" of Fact Target | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TARGETS_F] |
| SAMP_TIME_DAY_D | 14 | SAMP_TIME_DAY_D_Key on CALENDAR_DATE | "LTS Time Day" of Dim Time | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_DAY_D] |
| SAMP_TIME_MTH_D | 36 | SAMP_TIME_MTH_D_Key on MTH_KEY; SAMP_TIME_MTH_D_Key#1 on CAL_YEAR | "LTS Time Month" of Dim Time | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TIME_MTH_D] |

The repository does not say whether any key of a database table is its primary key. Each table's section gives its evidence.

**Before relying on a number.**

- "Revenue (EUR)", "Revenue (GBP)" and "Revenue (INR)" are the column REVENUE multiplied by 0.76, 0.62 and 54.68 in the source "SAMP_REVENUE_F" [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.SAMP_REVENUE_F#map15], but the plain column REVENUE in the source "Aggregate Revenue" [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue#map8]. According to the user, the multipliers are fixed exchange rates from a rate sheet dated 2014-01-01, and "Aggregate Revenue" already holds amounts converted at the same rates [answer:Q4]; which of the four revenue measures is right from that source is open (open question Q15).
- Only "Aggregate Revenue" of the five sources of Fact Revenue has a priority group recorded [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue]; which source a query uses is not recorded (open question Q5).
- According to the user, SAMP_TIME_MTH_D holds one row per month and CAL_YEAR repeats twelve times [answer:Q10], although the repository declares a key on CAL_YEAR alone; whether a total by year is counted twelve times is open (open question Q16).
- "write" on both measures of Fact Target is given to "BI Author Role" [kb:sampleapp::sec::obj.ObjPriv_1500:18131355661677.o0] (open question Q7); according to the user, the two privilege packages for which the export records no role are also held by BI Author Role [answer:Q12].
- "Preserve" always holds 1 and is used only to keep rows in queries, according to the user [answer:Q6].

**Open questions.** 12 questions are open and 4 are answered, at the end of the document (counted).
