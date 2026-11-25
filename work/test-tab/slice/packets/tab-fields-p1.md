# The tab's fields: each FSD item and its confirmed columns (part 1 of 2)
Unit: tab-fields-p1
Kind: tab_fields
Tab: test-tab (subject areas: Sample Sales; estate sampleapp)
Facts from: tab_fields
Rows of this packet a document must cover: 20. Each is on a line that ends with (REQUIRED).
To cite a fact, copy the marker in square brackets that follows it, exactly as written. "->" points at another row.
This is part 1 of 2 of what was too long for one packet (the parts are tab-fields-p1 to tab-fields-p2). Each part is a unit of its own: write this one as its own section, from this packet alone.

## How to read this unit

- One entry per FSD item that needs data, in the FSD's order. A confirmed column is described in the unit of its logical table (named after it); an item that is not confirmed contributes nothing. The FSD's words are quoted from the tab's fsd.md; the anchor says where. (tab_fields; no Row identifier)

## FSD items

- Item S1-own-offices (filter), FSD anchor p6: NOT CONFIRMED (rejected): contributes nothing [kb:item::S1-own-offices] (REQUIRED)
    - FSD text: `Each manager should see only their own offices; the Head of Sales sees everything.` [kb:item::S1-own-offices]
    - Decision note: `TEST DECISION by the lead build agent, not by Kamal: the manager-to-office link is unknown (Q2)` [kb:item::S1-own-offices]

- Item F1 (filter), FSD anchor t1.r2.c2 t1.r2.c3: CONFIRMED [kb:item::F1] (REQUIRED)
    - FSD text: `Year: Calendar year of the order` [kb:item::F1]
    - Column: presentation column "Dim Time"."Per Name Year", logical column "Dim Time"."Per Name Year", not a measure (unit table-07-dim-time) -> sampleapp::pres::Sample Sales.Dim Time.Per Name Year
    - Decision note: `TEST DECISION by the lead build agent, not by Kamal: kept for the test although the review showed revenue joins on the bill date` [kb:item::F1]

- Item F2 (filter), FSD anchor t1.r3.c2 t1.r3.c3: CONFIRMED [kb:item::F2] (REQUIRED)
    - FSD text: `Office: The sales office` [kb:item::F2]
    - Column: presentation column "Dim Offices"."Office Dsc", logical column "Dim Offices"."Office Dsc", not a measure (unit table-03-dim-offices) -> sampleapp::pres::Sample Sales.Dim Offices.Office Dsc
    - Decision note: `TEST DECISION by the lead build agent, not by Kamal` [kb:item::F2]

- Item F3 (filter), FSD anchor t1.r4.c2 t1.r4.c3: CONFIRMED [kb:item::F3] (REQUIRED)
    - FSD text: `Product brand: Brand of the product sold` [kb:item::F3]
    - Column: presentation column "Dim Products"."Brand", logical column "Dim Products"."Brand", not a measure (unit table-04-dim-products) -> sampleapp::pres::Sample Sales.Dim Products.Brand
    - Decision note: `TEST DECISION by the lead build agent, not by Kamal` [kb:item::F3]

- Item F4 (filter), FSD anchor t1.r5.c2 t1.r5.c3: NOT CONFIRMED (unmatched): contributes nothing [kb:item::F4] (REQUIRED)
    - FSD text: `Region: Sales region the office belongs to` [kb:item::F4]

- Item K1 (calculation), FSD anchor t2.r2.c2 t2.r2.c3: CONFIRMED [kb:item::K1] (REQUIRED)
    - FSD text: `Net revenue: Revenue after discounts, for the filtered period` [kb:item::K1]
    - Column: presentation column "Fact Revenue"."Revenue", logical column "Fact Revenue"."Revenue", a measure (unit table-01-fact-revenue) -> sampleapp::pres::Sample Sales.Fact Revenue.Revenue
    - Column: presentation column "Fact Revenue"."Discnt Value", logical column "Fact Revenue"."Discnt Value", a measure (unit table-01-fact-revenue) -> sampleapp::pres::Sample Sales.Fact Revenue.Discnt Value
    - Decision note: `TEST DECISION by the lead build agent, not by Kamal` [kb:item::K1]

- Item K2 (measure), FSD anchor t2.r3.c2 t2.r3.c3: CONFIRMED [kb:item::K2] (REQUIRED)
    - FSD text: `Units sold: Number of units sold` [kb:item::K2]
    - Column: presentation column "Fact Revenue"."Units", logical column "Fact Revenue"."Units", a measure (unit table-01-fact-revenue) -> sampleapp::pres::Sample Sales.Fact Revenue.Units
    - Decision note: `TEST DECISION by the lead build agent, not by Kamal` [kb:item::K2]

- Item K3 (calculation), FSD anchor t2.r4.c2 t2.r4.c3: CONFIRMED [kb:item::K3] (REQUIRED)
    - FSD text: `Revenue vs target: Net revenue as a percentage of the revenue target for the same period` [kb:item::K3]
    - Column: presentation column "Fact Revenue"."Revenue", logical column "Fact Revenue"."Revenue", a measure (unit table-01-fact-revenue) -> sampleapp::pres::Sample Sales.Fact Revenue.Revenue
    - Column: presentation column "Fact Revenue"."Discnt Value", logical column "Fact Revenue"."Discnt Value", a measure (unit table-01-fact-revenue) -> sampleapp::pres::Sample Sales.Fact Revenue.Discnt Value
    - Column: presentation column "Fact Target"."Revenue", logical column "Fact Target"."Revenue", a measure (unit table-02-fact-target) -> sampleapp::pres::Sample Sales.Fact Target.Revenue
    - Decision note: `TEST DECISION by the lead build agent, not by Kamal` [kb:item::K3]

- Item K4 (calculation), FSD anchor t2.r5.c2 t2.r5.c3: CONFIRMED [kb:item::K4] (REQUIRED)
    - FSD text: `Average selling price: Net revenue divided by units sold` [kb:item::K4]
    - Column: presentation column "Fact Revenue"."Revenue", logical column "Fact Revenue"."Revenue", a measure (unit table-01-fact-revenue) -> sampleapp::pres::Sample Sales.Fact Revenue.Revenue
    - Column: presentation column "Fact Revenue"."Discnt Value", logical column "Fact Revenue"."Discnt Value", a measure (unit table-01-fact-revenue) -> sampleapp::pres::Sample Sales.Fact Revenue.Discnt Value
    - Column: presentation column "Fact Revenue"."Units", logical column "Fact Revenue"."Units", a measure (unit table-01-fact-revenue) -> sampleapp::pres::Sample Sales.Fact Revenue.Units
    - Decision note: `TEST DECISION by the lead build agent, not by Kamal` [kb:item::K4]

- Item K5 (measure), FSD anchor t2.r6.c2 t2.r6.c3: NOT CONFIRMED (unmatched): contributes nothing [kb:item::K5] (REQUIRED)
    - FSD text: `Active customers: Number of distinct customers who placed at least one order in the period` [kb:item::K5]

- Item V1-month (attribute), FSD anchor p10: CONFIRMED [kb:item::V1-month] (REQUIRED)
    - FSD text: `month of the year` [kb:item::V1-month]
    - Column: presentation column "Dim Time"."Per Name Month", logical column "Dim Time"."Per Name Month", not a measure (unit table-07-dim-time) -> sampleapp::pres::Sample Sales.Dim Time.Per Name Month
    - Decision note: `TEST DECISION by the lead build agent, not by Kamal` [kb:item::V1-month]

- Item V1-net-revenue (calculation), FSD anchor p10: CONFIRMED [kb:item::V1-net-revenue] (REQUIRED)
    - FSD text: `net revenue` [kb:item::V1-net-revenue]
    - Column: presentation column "Fact Revenue"."Revenue", logical column "Fact Revenue"."Revenue", a measure (unit table-01-fact-revenue) -> sampleapp::pres::Sample Sales.Fact Revenue.Revenue
    - Column: presentation column "Fact Revenue"."Discnt Value", logical column "Fact Revenue"."Discnt Value", a measure (unit table-01-fact-revenue) -> sampleapp::pres::Sample Sales.Fact Revenue.Discnt Value
    - Decision note: `TEST DECISION by the lead build agent, not by Kamal` [kb:item::V1-net-revenue]

- Item V1-target (measure), FSD anchor p10: CONFIRMED [kb:item::V1-target] (REQUIRED)
    - FSD text: `revenue target` [kb:item::V1-target]
    - Column: presentation column "Fact Target"."Revenue", logical column "Fact Target"."Revenue", a measure (unit table-02-fact-target) -> sampleapp::pres::Sample Sales.Fact Target.Revenue
    - Decision note: `TEST DECISION by the lead build agent, not by Kamal` [kb:item::V1-target]

- Item V2-product (attribute), FSD anchor p11: CONFIRMED [kb:item::V2-product] (REQUIRED)
    - FSD text: `product` [kb:item::V2-product]
    - Column: presentation column "Dim Products"."Prod Dsc", logical column "Dim Products"."Prod Dsc", not a measure (unit table-04-dim-products) -> sampleapp::pres::Sample Sales.Dim Products.Prod Dsc
    - Decision note: `TEST DECISION by the lead build agent, not by Kamal` [kb:item::V2-product]

- Item V2-office (attribute), FSD anchor p11: CONFIRMED [kb:item::V2-office] (REQUIRED)
    - FSD text: `office` [kb:item::V2-office]
    - Column: presentation column "Dim Offices"."Office Dsc", logical column "Dim Offices"."Office Dsc", not a measure (unit table-03-dim-offices) -> sampleapp::pres::Sample Sales.Dim Offices.Office Dsc
    - Decision note: `TEST DECISION by the lead build agent, not by Kamal` [kb:item::V2-office]

- Item V2-net-revenue (calculation), FSD anchor p11: CONFIRMED [kb:item::V2-net-revenue] (REQUIRED)
    - FSD text: `net revenue` [kb:item::V2-net-revenue]
    - Column: presentation column "Fact Revenue"."Revenue", logical column "Fact Revenue"."Revenue", a measure (unit table-01-fact-revenue) -> sampleapp::pres::Sample Sales.Fact Revenue.Revenue
    - Column: presentation column "Fact Revenue"."Discnt Value", logical column "Fact Revenue"."Discnt Value", a measure (unit table-01-fact-revenue) -> sampleapp::pres::Sample Sales.Fact Revenue.Discnt Value
    - Decision note: `TEST DECISION by the lead build agent, not by Kamal` [kb:item::V2-net-revenue]

- Item V2-units (measure), FSD anchor p11: CONFIRMED [kb:item::V2-units] (REQUIRED)
    - FSD text: `units sold` [kb:item::V2-units]
    - Column: presentation column "Fact Revenue"."Units", logical column "Fact Revenue"."Units", a measure (unit table-01-fact-revenue) -> sampleapp::pres::Sample Sales.Fact Revenue.Units
    - Decision note: `TEST DECISION by the lead build agent, not by Kamal` [kb:item::V2-units]

- Item V2-discount (measure), FSD anchor p11: CONFIRMED [kb:item::V2-discount] (REQUIRED)
    - FSD text: `discount amount` [kb:item::V2-discount]
    - Column: presentation column "Fact Revenue"."Discnt Value", logical column "Fact Revenue"."Discnt Value", a measure (unit table-01-fact-revenue) -> sampleapp::pres::Sample Sales.Fact Revenue.Discnt Value
    - Decision note: `TEST DECISION by the lead build agent, not by Kamal` [kb:item::V2-discount]

- Item V3-rep (attribute), FSD anchor p12: CONFIRMED [kb:item::V3-rep] (REQUIRED)
    - FSD text: `sales representatives` [kb:item::V3-rep]
    - Column: presentation column "Dim Sales Rep"."Employee Name", logical column "Dim Sales Rep"."Employee Name", not a measure (unit table-06-dim-sales-rep) -> sampleapp::pres::Sample Sales.Dim Sales Rep.Employee Name
    - Decision note: `TEST DECISION by the lead build agent, not by Kamal` [kb:item::V3-rep]

- Item V3-net-revenue (calculation), FSD anchor p12: CONFIRMED [kb:item::V3-net-revenue] (REQUIRED)
    - FSD text: `highest net revenue` [kb:item::V3-net-revenue]
    - Column: presentation column "Fact Revenue"."Revenue", logical column "Fact Revenue"."Revenue", a measure (unit table-01-fact-revenue) -> sampleapp::pres::Sample Sales.Fact Revenue.Revenue
    - Column: presentation column "Fact Revenue"."Discnt Value", logical column "Fact Revenue"."Discnt Value", a measure (unit table-01-fact-revenue) -> sampleapp::pres::Sample Sales.Fact Revenue.Discnt Value
    - Decision note: `TEST DECISION by the lead build agent, not by Kamal` [kb:item::V3-net-revenue]

