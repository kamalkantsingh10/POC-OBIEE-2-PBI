Status: reviewed 2026-11-24 (11 findings corrected, 1 turned into questions, 19 questions open)

# test-tab — to-be design

**What this is.** A like-for-like design for the tab "test-tab": the new tables and views, the Power BI semantic model, and what supplies each field of the FSD. It is built from the columns the Operator confirmed. Everything marked "Proposal" is a suggestion to be signed by a person; nothing here has been agreed.

**How to read it.** A statement that ends with a marker in square brackets is a fact from the OBIEE export, and the marker names the row it came from. Text shown as code is copied from OBIEE or from the FSD exactly. A marker that starts with "answer" means the statement rests on an answer a person gave. "Missing" means the export does not hold it. Types are platform-neutral, because no target platform has been chosen. Today's model is described in the as-is document; this document points to it and does not repeat it.

Note for this run: the decisions this design is built from are test decisions written for testing, not the Operator's; every decision note says so (section Fields on the tab) [kb:item::F1] [kb:item::N1-currency].

## The tab in one page

**FSD items.** 21 items need data: 18 are confirmed and supplied below; 3 are unresolved: S1-own-offices (managers see only their own offices; rejected), F4 (Region; unmatched) and K5 (Active customers; unmatched).

**New tables and views.** 12 new tables and 1 new view, all like for like.

| New table or view | Columns | Key or "missing" | Read by |
|---|---|---|---|
| EMPLOYEE | 4 | EMP_ID | Dim Sales Rep |
| EMP_PARENT_CHILD | 4 | MEMBER_KEY (uniqueness asked, Q10) | Dim Sales Rep |
| FactCrossJoin (view) | 1 | missing | Fact Revenue (not used by the tab; Q11) |
| SAMP_OFFICES_D | 10 | OFFICE_KEY | Dim Offices |
| SAMP_PRODUCTS_D | 11 | PROD_KEY | Dim Products |
| SAMP_PRODUCTS_D_TRANSLATION | 3 | LANG_ID, PROD_KEY | Dim Products (Translated), not in this model |
| SAMP_REVENUE_A | 5 | missing | Fact Revenue (if Q3 keeps summaries) |
| SAMP_REVENUE_A_MTH | 4 | missing | Fact Revenue (if Q3 keeps summaries) |
| SAMP_REVENUE_A_YR | 4 | missing | Fact Revenue (if Q3 keeps summaries) |
| SAMP_REVENUE_F | 12 | missing | Fact Revenue |
| SAMP_TARGETS_F | 5 | missing | Fact Target |
| SAMP_TIME_DAY_D | 14 | CALENDAR_DATE | Dim Time |
| SAMP_TIME_MTH_D | 36 | MTH_KEY (a second key on CAL_YEAR; Q13) | Dim Time (if Q8 keeps it) |

**Model.** Six model tables: Fact Revenue, Fact Target, Dim Offices, Dim Products, Dim Sales Rep, Dim Time (Dim Products (Translated) is not carried). Four model measures: Revenue, Discount Value, Units (Fact Revenue) and Revenue Target (Fact Target), each with a proposed definition. Three FSD measures: Net revenue, Revenue vs target, Average selling price. The field Preferred Currency has no definition until decision Q2. Six relationships and three hierarchies are proposed; no relationship from Dim Time to Fact Target until Q14, no hierarchy for sales representatives until Q17, no security role until Q18.

**Decisions needed.** 15. The three that matter most:

- Q3: which of the four revenue tables at different levels the model keeps; it decides what the measures read, and the month and year summaries do not hold the discount as a measure.
- Q14: how targets, held by month, meet the time table; without it Revenue vs target cannot compare the same period.
- Q2: how the currency switch "Preferred Currency" (item N1-currency) works without OBIEE's per-user session value.

**Missing most often.** Decimal places of number columns (scale not recorded) and whether key columns must be filled (nullable not stated); the keys of all five revenue and target tables (Q12); indexes, partitioning, volumes and loading for every table.

**Open questions.** 19, of which 15 are decisions.
