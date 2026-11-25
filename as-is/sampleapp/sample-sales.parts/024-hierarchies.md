## Drill paths

The business model has four drill paths ("dimensions"); each is offered to report authors as a hierarchy.

### Office Hierarchy

A drill path with levels; 5 levels; marked as a time dimension: not recorded [kb:sampleapp::bmm::SAMPLESALES.Office Hierarchy]. Its default root level is "Dim Offices Total" [kb:sampleapp::bmm::SAMPLESALES.Office Hierarchy].

Path: Dim Offices Total, Company, Organization, Department, Office Detail.

| Level | Level above | Level(s) below | Key column | Keys (name; fields; primary and drill-down as recorded) | Member count as recorded | Shown to report authors as | Evidence |
|---|---|---|---|---|---|---|---|
| Dim Offices Total (grand total) | none recorded (a top level) | Company | none recorded | none | 1 | level "Dim Offices Total" | [kb:sampleapp::bmm::SAMPLESALES.Office Hierarchy.Dim Offices Total] [kb:sampleapp::pres::Sample Sales.Dim Offices.Office Hierarchy.Dim Offices Total] |
| Company | Dim Offices Total | Organization | Dim Offices, Company | Company; Company; primary not recorded, drill-down true. Company Key; Company Key; primary true, drill-down not recorded | 1 | level "Company", showing "Company" | [kb:sampleapp::bmm::SAMPLESALES.Office Hierarchy.Company] [kb:sampleapp::bmm::SAMPLESALES.Office Hierarchy.Company.key.Company] [kb:sampleapp::bmm::SAMPLESALES.Office Hierarchy.Company.key.Company Key] [kb:sampleapp::pres::Sample Sales.Dim Offices.Office Hierarchy.Company] [kb:sampleapp::pres::Sample Sales.Dim Offices.Office Hierarchy.Company.0] |
| Organization | Company | Department | Dim Offices, Organization | Org Key; Org Key; primary true, drill-down not recorded. Organization; Organization; primary not recorded, drill-down true | 1 | level "Organization", showing "Organization" | [kb:sampleapp::bmm::SAMPLESALES.Office Hierarchy.Organization] [kb:sampleapp::bmm::SAMPLESALES.Office Hierarchy.Organization.key.Org Key] [kb:sampleapp::bmm::SAMPLESALES.Office Hierarchy.Organization.key.Organization] [kb:sampleapp::pres::Sample Sales.Dim Offices.Office Hierarchy.Organization] [kb:sampleapp::pres::Sample Sales.Dim Offices.Office Hierarchy.Organization.0] |
| Department | Organization | Office Detail | Dim Offices, Department | Department; Department; primary not recorded, drill-down true. Dept Key; Dept Key; primary true, drill-down not recorded | 1 | level "Department", showing "Department" | [kb:sampleapp::bmm::SAMPLESALES.Office Hierarchy.Department] [kb:sampleapp::bmm::SAMPLESALES.Office Hierarchy.Department.key.Department] [kb:sampleapp::bmm::SAMPLESALES.Office Hierarchy.Department.key.Dept Key] [kb:sampleapp::pres::Sample Sales.Dim Offices.Office Hierarchy.Department] [kb:sampleapp::pres::Sample Sales.Dim Offices.Office Hierarchy.Department.0] |
| Office Detail | Department | none recorded (a lowest level) | Dim Offices, Office Dsc | Office Dsc; Office Dsc; primary not recorded, drill-down true. Office Key; Office Key; primary true, drill-down not recorded | 1 | level "Office Detail", showing "Office Dsc" | [kb:sampleapp::bmm::SAMPLESALES.Office Hierarchy.Office Detail] [kb:sampleapp::bmm::SAMPLESALES.Office Hierarchy.Office Detail.key.Office Dsc] [kb:sampleapp::bmm::SAMPLESALES.Office Hierarchy.Office Detail.key.Office Key] [kb:sampleapp::pres::Sample Sales.Dim Offices.Office Hierarchy.Office Detail] [kb:sampleapp::pres::Sample Sales.Dim Offices.Office Hierarchy.Office Detail.0] |

For every key above, chronological key: not recorded. Offered to report authors as the hierarchy "Office Hierarchy" in the table "Dim Offices", with 5 levels [kb:sampleapp::pres::Sample Sales.Dim Offices.Office Hierarchy].

### Products Hierararchy

The name is spelled so in the repository. A drill path with levels; 5 levels; marked as a time dimension: not recorded [kb:sampleapp::bmm::SAMPLESALES.Products Hierararchy]. Its default root level is "Dim Products Total" [kb:sampleapp::bmm::SAMPLESALES.Products Hierararchy].

Path: Dim Products Total, Brand, Line Of Business, Products Type, Product Detail.

| Level | Level above | Level(s) below | Key column | Keys (name; fields; primary and drill-down as recorded) | Member count as recorded | Shown to report authors as | Evidence |
|---|---|---|---|---|---|---|---|
| Dim Products Total (grand total) | none recorded (a top level) | Brand | none recorded | none | 1 | level "Dim Products Total" | [kb:sampleapp::bmm::SAMPLESALES.Products Hierararchy.Dim Products Total] [kb:sampleapp::pres::Sample Sales.Dim Products.Products Hierararchy.Dim Products Total] |
| Brand | Dim Products Total | Line Of Business | Dim Products, Brand | Brand; Brand; primary true, drill-down true | 1 | level "Brand", showing "Brand" | [kb:sampleapp::bmm::SAMPLESALES.Products Hierararchy.Brand] [kb:sampleapp::bmm::SAMPLESALES.Products Hierararchy.Brand.key.Brand] [kb:sampleapp::pres::Sample Sales.Dim Products.Products Hierararchy.Brand] [kb:sampleapp::pres::Sample Sales.Dim Products.Products Hierararchy.Brand.0] |
| Line Of Business | Brand | Products Type | Dim Products, Lob | Lob; Lob; primary true, drill-down true | 1 | level "Line Of Business", showing "Lob" | [kb:sampleapp::bmm::SAMPLESALES.Products Hierararchy.Line Of Business] [kb:sampleapp::bmm::SAMPLESALES.Products Hierararchy.Line Of Business.key.Lob] [kb:sampleapp::pres::Sample Sales.Dim Products.Products Hierararchy.Line Of Business] [kb:sampleapp::pres::Sample Sales.Dim Products.Products Hierararchy.Line Of Business.0] |
| Products Type | Line Of Business | Product Detail | Dim Products, Type | Type; Type; primary true, drill-down true | 1 | level "Products Type", showing "Type" | [kb:sampleapp::bmm::SAMPLESALES.Products Hierararchy.Products Type] [kb:sampleapp::bmm::SAMPLESALES.Products Hierararchy.Products Type.key.Type] [kb:sampleapp::pres::Sample Sales.Dim Products.Products Hierararchy.Products Type] [kb:sampleapp::pres::Sample Sales.Dim Products.Products Hierararchy.Products Type.0] |
| Product Detail | Products Type | none recorded (a lowest level) | Dim Products, Prod Dsc | Dim Products Detail_Key; Prod Dsc; primary true, drill-down true. Dim Products Detail_Key1; Prod Key; primary not recorded, drill-down not recorded | 1 | level "Product Detail", showing "Prod Dsc" | [kb:sampleapp::bmm::SAMPLESALES.Products Hierararchy.Product Detail] [kb:sampleapp::bmm::SAMPLESALES.Products Hierararchy.Product Detail.key.Dim Products Detail_Key] [kb:sampleapp::bmm::SAMPLESALES.Products Hierararchy.Product Detail.key.Dim Products Detail_Key1] [kb:sampleapp::pres::Sample Sales.Dim Products.Products Hierararchy.Product Detail] [kb:sampleapp::pres::Sample Sales.Dim Products.Products Hierararchy.Product Detail.0] |

For every key above, chronological key: not recorded. Offered to report authors as the hierarchy "Products Hierararchy" in the table "Dim Products", with 5 levels [kb:sampleapp::pres::Sample Sales.Dim Products.Products Hierararchy].

### Sales Rep Hierarchy

A parent-child drill path (value based: the depth is not fixed); 2 levels; marked as a time dimension: not recorded [kb:sampleapp::bmm::SAMPLESALES.Sales Rep Hierarchy]. Its default root level is "Total" [kb:sampleapp::bmm::SAMPLESALES.Sales Rep Hierarchy].

Path: Total, Detail.

| Level | Level above | Level(s) below | Key column | Keys (name; fields; primary and drill-down as recorded) | Member count as recorded | Shown to report authors as | Evidence |
|---|---|---|---|---|---|---|---|
| Total (grand total) | none recorded (a top level) | Detail | none recorded | none | 1 | no level is shown | [kb:sampleapp::bmm::SAMPLESALES.Sales Rep Hierarchy.Total] |
| Detail | Total | none recorded (a lowest level) | Dim Sales Rep, Employee ID | EMP_ID; Employee ID; primary true, drill-down true | 1 | no level is shown | [kb:sampleapp::bmm::SAMPLESALES.Sales Rep Hierarchy.Detail] [kb:sampleapp::bmm::SAMPLESALES.Sales Rep Hierarchy.Detail.key.EMP_ID] |

The parent-child relationships are held in the database table EMP_PARENT_CHILD, which the source "LTS Employee" of Dim Sales Rep names; in that table the member column is MEMBER_KEY, the parent (ancestor) column ANCESTOR_KEY, the distance column (steps between the two) DISTANCE and the leaf-flag column IS_LEAF [kb:sampleapp::bmm::SAMPLESALES.Sales Rep Hierarchy]. See the sections Dim Sales Rep and Database table EMP_PARENT_CHILD.

Offered to report authors as the hierarchy "Sales Rep Hierarchy" in the table "Dim Sales Rep", with 0 levels, shown by the field "Employee Name" [kb:sampleapp::pres::Sample Sales.Dim Sales Rep.Sales Rep Hierarchy].

### Time Hierarchy

A drill path with levels; 7 levels; marked as a time dimension: true [kb:sampleapp::bmm::SAMPLESALES.Time Hierarchy]. Its default root level is "Dim Time Total" [kb:sampleapp::bmm::SAMPLESALES.Time Hierarchy].

Path: Dim Time Total, Year, Half Year, Quarter, Month, Week, Dim Time Detail.

| Level | Level above | Level(s) below | Key column | Keys (name; fields; primary, chronological and drill-down as recorded) | Member count as recorded | Shown to report authors as | Evidence |
|---|---|---|---|---|---|---|---|
| Dim Time Total (grand total) | none recorded (a top level) | Year | none recorded | none | 1 | level "Dim Time Total" | [kb:sampleapp::bmm::SAMPLESALES.Time Hierarchy.Dim Time Total] [kb:sampleapp::pres::Sample Sales.Dim Time.Time Hierarchy.Dim Time Total] |
| Year | Dim Time Total | Half Year | Dim Time, Per Name Year | Per Name Year; Per Name Year; primary true, chronological true, drill-down true | 2 | level "Year", showing "Per Name Year" | [kb:sampleapp::bmm::SAMPLESALES.Time Hierarchy.Year] [kb:sampleapp::bmm::SAMPLESALES.Time Hierarchy.Year.key.Per Name Year] [kb:sampleapp::pres::Sample Sales.Dim Time.Time Hierarchy.Year] [kb:sampleapp::pres::Sample Sales.Dim Time.Time Hierarchy.Year.0] |
| Half Year | Year | Quarter | Dim Time, Per Name Half | Per Name Half; Per Name Half; primary true, chronological true, drill-down true | 180 | level "Half Year", showing "Per Name Half" | [kb:sampleapp::bmm::SAMPLESALES.Time Hierarchy.Half Year] [kb:sampleapp::bmm::SAMPLESALES.Time Hierarchy.Half Year.key.Per Name Half] [kb:sampleapp::pres::Sample Sales.Dim Time.Time Hierarchy.Half Year] [kb:sampleapp::pres::Sample Sales.Dim Time.Time Hierarchy.Half Year.0] |
| Quarter | Half Year | Month | Dim Time, Per Name Qtr | Per Name Qtr; Per Name Qtr; primary true, chronological true, drill-down true | 270 | level "Quarter", showing "Per Name Qtr" | [kb:sampleapp::bmm::SAMPLESALES.Time Hierarchy.Quarter] [kb:sampleapp::bmm::SAMPLESALES.Time Hierarchy.Quarter.key.Per Name Qtr] [kb:sampleapp::pres::Sample Sales.Dim Time.Time Hierarchy.Quarter] [kb:sampleapp::pres::Sample Sales.Dim Time.Time Hierarchy.Quarter.0] |
| Month | Quarter | Week | Dim Time, Per Name Month | Per Name Month; Per Name Month; primary true, chronological true, drill-down true | 322 | level "Month", showing "Per Name Month" | [kb:sampleapp::bmm::SAMPLESALES.Time Hierarchy.Month] [kb:sampleapp::bmm::SAMPLESALES.Time Hierarchy.Month.key.Per Name Month] [kb:sampleapp::pres::Sample Sales.Dim Time.Time Hierarchy.Month] [kb:sampleapp::pres::Sample Sales.Dim Time.Time Hierarchy.Month.0] |
| Week | Month | Dim Time Detail | Dim Time, Per Name Week | Per Name Week; Per Name Week; primary true, chronological true, drill-down true | 350 | level "Week", showing "Per Name Week" | [kb:sampleapp::bmm::SAMPLESALES.Time Hierarchy.Week] [kb:sampleapp::bmm::SAMPLESALES.Time Hierarchy.Week.key.Per Name Week] [kb:sampleapp::pres::Sample Sales.Dim Time.Time Hierarchy.Week] [kb:sampleapp::pres::Sample Sales.Dim Time.Time Hierarchy.Week.0] |
| Dim Time Detail | Week | none recorded (a lowest level) | Dim Time, Calendar Date | Dim Time Detail_Key; Calendar Date; primary true, chronological true, drill-down true | 365 | level "Dim Time Detail", showing "Calendar Date" | [kb:sampleapp::bmm::SAMPLESALES.Time Hierarchy.Dim Time Detail] [kb:sampleapp::bmm::SAMPLESALES.Time Hierarchy.Dim Time Detail.key.Dim Time Detail_Key] [kb:sampleapp::pres::Sample Sales.Dim Time.Time Hierarchy.Dim Time Detail] [kb:sampleapp::pres::Sample Sales.Dim Time.Time Hierarchy.Dim Time Detail.0] |

Offered to report authors as the hierarchy "Time Hierarchy" in the table "Dim Time", with 7 levels [kb:sampleapp::pres::Sample Sales.Dim Time.Time Hierarchy].

### Points to note

- The member counts of "Time Hierarchy" are copied as recorded: Year 2, Half Year 180, Quarter 270, Month 322, Week 350, Dim Time Detail 365 [kb:sampleapp::bmm::SAMPLESALES.Time Hierarchy.Year] [kb:sampleapp::bmm::SAMPLESALES.Time Hierarchy.Half Year] [kb:sampleapp::bmm::SAMPLESALES.Time Hierarchy.Quarter] [kb:sampleapp::bmm::SAMPLESALES.Time Hierarchy.Month] [kb:sampleapp::bmm::SAMPLESALES.Time Hierarchy.Week] [kb:sampleapp::bmm::SAMPLESALES.Time Hierarchy.Dim Time Detail]. How they were obtained is not recorded (open question Q11).
- "Sales Rep Hierarchy" is offered to report authors with 0 levels, shown by "Employee Name" [kb:sampleapp::pres::Sample Sales.Dim Sales Rep.Sales Rep Hierarchy].
