# Functional Specification — Sales Performance Dashboard, tab "Monthly Overview"

Document owner: Sales Operations. Version 0.3 (draft). Target: Power BI.

## 1. Purpose

The "Monthly Overview" tab gives regional sales managers a one-page view of how sales are tracking against plan for the current year. It replaces the OBIEE dashboard page "Sample Sales / Overview".

## 2. Audience

Regional sales managers and the Head of Sales. Each manager should see only their own offices; the Head of Sales sees everything.

## 3. Filters (slicers at the top of the page)

| # | Filter | Description | Default |
|---|---|---|---|
| F1 | Year | Calendar year of the order | Current year |
| F2 | Office | The sales office | All |
| F3 | Product brand | Brand of the product sold | All |
| F4 | Region | Sales region the office belongs to | All |

## 4. Key figures (cards across the top)

| # | Figure | Definition |
|---|---|---|
| K1 | Net revenue | Revenue after discounts, for the filtered period |
| K2 | Units sold | Number of units sold |
| K3 | Revenue vs target | Net revenue as a percentage of the revenue target for the same period |
| K4 | Average selling price | Net revenue divided by units sold |
| K5 | Active customers | Number of distinct customers who placed at least one order in the period |

## 5. Visuals

**V1. Monthly revenue trend.** Line chart. X axis: month of the year. Lines: net revenue and revenue target, one point per month.

**V2. Revenue by product and office.** Table. Rows: product, then office. Columns: net revenue, units sold, discount amount.

**V3. Top sales representatives.** Bar chart of the ten sales representatives with the highest net revenue in the period.

## 6. Notes

- Amounts are shown in the user's preferred currency where the source supports it.
- Targets are entered by the sales planning team and may be revised during the year.
