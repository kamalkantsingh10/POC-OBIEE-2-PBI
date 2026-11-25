# [p1] Functional Specification — Sales Performance Dashboard, tab "Monthly Overview"

[p2] Document owner: Sales Operations. Version 0.3 (draft). Target: Power BI.

## [p3] 1. Purpose

[p4] The "Monthly Overview" tab gives regional sales managers a one-page view of how sales are tracking against plan for the current year. It replaces the OBIEE dashboard page "Sample Sales / Overview".

## [p5] 2. Audience

[p6] Regional sales managers and the Head of Sales. Each manager should see only their own offices; the Head of Sales sees everything.

## [p7] 3. Filters (slicers at the top of the page)

| [t1.r1.c1] # | [t1.r1.c2] Filter | [t1.r1.c3] Description | [t1.r1.c4] Default |
|---|---|---|---|
| [t1.r2.c1] F1 | [t1.r2.c2] Year | [t1.r2.c3] Calendar year of the order | [t1.r2.c4] Current year |
| [t1.r3.c1] F2 | [t1.r3.c2] Office | [t1.r3.c3] The sales office | [t1.r3.c4] All |
| [t1.r4.c1] F3 | [t1.r4.c2] Product brand | [t1.r4.c3] Brand of the product sold | [t1.r4.c4] All |
| [t1.r5.c1] F4 | [t1.r5.c2] Region | [t1.r5.c3] Sales region the office belongs to | [t1.r5.c4] All |

## [p8] 4. Key figures (cards across the top)

| [t2.r1.c1] # | [t2.r1.c2] Figure | [t2.r1.c3] Definition |
|---|---|---|
| [t2.r2.c1] K1 | [t2.r2.c2] Net revenue | [t2.r2.c3] Revenue after discounts, for the filtered period |
| [t2.r3.c1] K2 | [t2.r3.c2] Units sold | [t2.r3.c3] Number of units sold |
| [t2.r4.c1] K3 | [t2.r4.c2] Revenue vs target | [t2.r4.c3] Net revenue as a percentage of the revenue target for the same period |
| [t2.r5.c1] K4 | [t2.r5.c2] Average selling price | [t2.r5.c3] Net revenue divided by units sold |
| [t2.r6.c1] K5 | [t2.r6.c2] Active customers | [t2.r6.c3] Number of distinct customers who placed at least one order in the period |

## [p9] 5. Visuals

[p10] **V1. Monthly revenue trend.** Line chart. X axis: month of the year. Lines: net revenue and revenue target, one point per month.

[p11] **V2. Revenue by product and office.** Table. Rows: product, then office. Columns: net revenue, units sold, discount amount.

[p12] **V3. Top sales representatives.** Bar chart of the ten sales representatives with the highest net revenue in the period.

## [p13] 6. Notes

- [p14] Amounts are shown in the user's preferred currency where the source supports it.
- [p15] Targets are entered by the sales planning team and may be revised during the year.
