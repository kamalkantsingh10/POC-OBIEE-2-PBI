### Hierarchies

Four drill paths are in the slice, and all four are offered to report authors. The tab's confirmed fields sit on three of them: "Office Dsc" (Office Hierarchy), "Brand" and "Prod Dsc" (Products Hierararchy), "Per Name Year" and "Per Name Month" (Time Hierarchy); "Employee Name" shows the fourth. A hierarchy of the same levels needs fields of the model tables that the tab does not confirm (for example "Company" or "Per Name Qtr"); they are carried only if the hierarchy is signed.

#### Office Hierarchy

Dimension "Office Hierarchy": 5 levels; marked as a time dimension: not recorded; default root level "Dim Offices Total". [kb:sampleapp::bmm::SAMPLESALES.Office Hierarchy] Offered to report authors as hierarchy "Office Hierarchy" in presentation table "Dim Offices", 5 levels. [kb:sampleapp::pres::Sample Sales.Dim Offices.Office Hierarchy]

| Level | Key field | Keys | Evidence |
|---|---|---|---|
| 0 Dim Offices Total (grand total) | none recorded | none | [kb:sampleapp::bmm::SAMPLESALES.Office Hierarchy.Dim Offices Total] |
| 1 Company | "Dim Offices"."Company" | "Company Key" (primary key of the level true); "Company" (used for drill-down true) | [kb:sampleapp::bmm::SAMPLESALES.Office Hierarchy.Company] [kb:sampleapp::bmm::SAMPLESALES.Office Hierarchy.Company.key.Company Key] [kb:sampleapp::bmm::SAMPLESALES.Office Hierarchy.Company.key.Company] |
| 2 Organization | "Dim Offices"."Organization" | "Org Key" (primary key of the level true); "Organization" (used for drill-down true) | [kb:sampleapp::bmm::SAMPLESALES.Office Hierarchy.Organization] [kb:sampleapp::bmm::SAMPLESALES.Office Hierarchy.Organization.key.Org Key] [kb:sampleapp::bmm::SAMPLESALES.Office Hierarchy.Organization.key.Organization] |
| 3 Department | "Dim Offices"."Department" | "Dept Key" (primary key of the level true); "Department" (used for drill-down true) | [kb:sampleapp::bmm::SAMPLESALES.Office Hierarchy.Department] [kb:sampleapp::bmm::SAMPLESALES.Office Hierarchy.Department.key.Dept Key] [kb:sampleapp::bmm::SAMPLESALES.Office Hierarchy.Department.key.Department] |
| 4 Office Detail | "Dim Offices"."Office Dsc" | "Office Key" (primary key of the level true); "Office Dsc" (used for drill-down true) | [kb:sampleapp::bmm::SAMPLESALES.Office Hierarchy.Office Detail] [kb:sampleapp::bmm::SAMPLESALES.Office Hierarchy.Office Detail.key.Office Key] [kb:sampleapp::bmm::SAMPLESALES.Office Hierarchy.Office Detail.key.Office Dsc] |

Every level records a member count of 1.

Proposal (not from OBIEE; to be signed): a hierarchy Office Hierarchy in the model table Dim Offices with the levels Company, Organization, Department and Office Dsc, in that order.

#### Products Hierararchy

Dimension "Products Hierararchy" (spelled so in the repository): 5 levels; marked as a time dimension: not recorded; default root level "Dim Products Total". [kb:sampleapp::bmm::SAMPLESALES.Products Hierararchy] Offered to report authors as hierarchy "Products Hierararchy" in presentation table "Dim Products", 5 levels. [kb:sampleapp::pres::Sample Sales.Dim Products.Products Hierararchy]

| Level | Key field | Keys | Evidence |
|---|---|---|---|
| 0 Dim Products Total (grand total) | none recorded | none | [kb:sampleapp::bmm::SAMPLESALES.Products Hierararchy.Dim Products Total] |
| 1 Brand | "Dim Products"."Brand" | "Brand" (primary key of the level true; used for drill-down true) | [kb:sampleapp::bmm::SAMPLESALES.Products Hierararchy.Brand] [kb:sampleapp::bmm::SAMPLESALES.Products Hierararchy.Brand.key.Brand] |
| 2 Line Of Business | "Dim Products"."Lob" | "Lob" (primary key of the level true; used for drill-down true) | [kb:sampleapp::bmm::SAMPLESALES.Products Hierararchy.Line Of Business] [kb:sampleapp::bmm::SAMPLESALES.Products Hierararchy.Line Of Business.key.Lob] |
| 3 Products Type | "Dim Products"."Type" | "Type" (primary key of the level true; used for drill-down true) | [kb:sampleapp::bmm::SAMPLESALES.Products Hierararchy.Products Type] [kb:sampleapp::bmm::SAMPLESALES.Products Hierararchy.Products Type.key.Type] |
| 4 Product Detail | "Dim Products"."Prod Dsc" | "Dim Products Detail_Key" on "Prod Dsc" (primary key of the level true); "Dim Products Detail_Key1" on "Prod Key" | [kb:sampleapp::bmm::SAMPLESALES.Products Hierararchy.Product Detail] [kb:sampleapp::bmm::SAMPLESALES.Products Hierararchy.Product Detail.key.Dim Products Detail_Key] [kb:sampleapp::bmm::SAMPLESALES.Products Hierararchy.Product Detail.key.Dim Products Detail_Key1] |

Every level records a member count of 1.

Proposal (not from OBIEE; to be signed): a hierarchy Products Hierarchy in the model table Dim Products with the levels Brand, Lob, Type and Prod Dsc, in that order.

#### Time Hierarchy

Dimension "Time Hierarchy": 7 levels; marked as a time dimension: true; default root level "Dim Time Total". [kb:sampleapp::bmm::SAMPLESALES.Time Hierarchy] Offered to report authors as hierarchy "Time Hierarchy" in presentation table "Dim Time", 7 levels. [kb:sampleapp::pres::Sample Sales.Dim Time.Time Hierarchy]

| Level | Key field | Keys | Evidence |
|---|---|---|---|
| 0 Dim Time Total (grand total) | none recorded | none | [kb:sampleapp::bmm::SAMPLESALES.Time Hierarchy.Dim Time Total] |
| 1 Year | "Dim Time"."Per Name Year" | "Per Name Year" (primary key of the level true; chronological key true); member count as recorded 2 | [kb:sampleapp::bmm::SAMPLESALES.Time Hierarchy.Year] [kb:sampleapp::bmm::SAMPLESALES.Time Hierarchy.Year.key.Per Name Year] |
| 2 Half Year | "Dim Time"."Per Name Half" | "Per Name Half" (primary, chronological); member count as recorded 180 | [kb:sampleapp::bmm::SAMPLESALES.Time Hierarchy.Half Year] [kb:sampleapp::bmm::SAMPLESALES.Time Hierarchy.Half Year.key.Per Name Half] |
| 3 Quarter | "Dim Time"."Per Name Qtr" | "Per Name Qtr" (primary, chronological); member count as recorded 270 | [kb:sampleapp::bmm::SAMPLESALES.Time Hierarchy.Quarter] [kb:sampleapp::bmm::SAMPLESALES.Time Hierarchy.Quarter.key.Per Name Qtr] |
| 4 Month | "Dim Time"."Per Name Month" | "Per Name Month" (primary, chronological); member count as recorded 322 | [kb:sampleapp::bmm::SAMPLESALES.Time Hierarchy.Month] [kb:sampleapp::bmm::SAMPLESALES.Time Hierarchy.Month.key.Per Name Month] |
| 5 Week | "Dim Time"."Per Name Week" | "Per Name Week" (primary, chronological); member count as recorded 350 | [kb:sampleapp::bmm::SAMPLESALES.Time Hierarchy.Week] [kb:sampleapp::bmm::SAMPLESALES.Time Hierarchy.Week.key.Per Name Week] |
| 6 Dim Time Detail | "Dim Time"."Calendar Date" | "Dim Time Detail_Key" (primary, chronological); member count as recorded 365 | [kb:sampleapp::bmm::SAMPLESALES.Time Hierarchy.Dim Time Detail] [kb:sampleapp::bmm::SAMPLESALES.Time Hierarchy.Dim Time Detail.key.Dim Time Detail_Key] |

The member counts are copied as recorded; whether they are current is as-is document, question Q11.

Proposal (not from OBIEE; to be signed): a hierarchy Time Hierarchy in the model table Dim Time with the levels Per Name Year, Per Name Half, Per Name Qtr, Per Name Month, Per Name Week and Calendar Date, in that order. Proposal (not from OBIEE; to be signed): Dim Time is marked as the model's date table on Calendar Date, as OBIEE marks the dimension as a time dimension. Week sits below Month in OBIEE; whether weeks are cut at month ends is not recorded.

#### Sales Rep Hierarchy

Dimension "Sales Rep Hierarchy": 2 levels; parent-child (value based: the depth is not fixed); marked as a time dimension: not recorded; default root level "Total". [kb:sampleapp::bmm::SAMPLESALES.Sales Rep Hierarchy] Offered to report authors as hierarchy "Sales Rep Hierarchy" in presentation table "Dim Sales Rep"; 0 levels; shown by column "Employee Name". [kb:sampleapp::pres::Sample Sales.Dim Sales Rep.Sales Rep Hierarchy]

| Level | Key field | Keys | Evidence |
|---|---|---|---|
| 0 Total (grand total) | none recorded | none | [kb:sampleapp::bmm::SAMPLESALES.Sales Rep Hierarchy.Total] |
| 1 Detail | "Dim Sales Rep"."Employee ID" | "EMP_ID" (primary key of the level true; used for drill-down true) | [kb:sampleapp::bmm::SAMPLESALES.Sales Rep Hierarchy.Detail] [kb:sampleapp::bmm::SAMPLESALES.Sales Rep Hierarchy.Detail.key.EMP_ID] |

The parent-child relationships are held in EMP_PARENT_CHILD (member MEMBER_KEY, ancestor ANCESTOR_KEY, DISTANCE, IS_LEAF), as stated in the sections Model table Dim Sales Rep and New table EMP_PARENT_CHILD. A parent-child drill path is decision Q17: no hierarchy is proposed until it is taken.
