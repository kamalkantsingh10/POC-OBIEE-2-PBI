# Hierarchies: the drill paths
Unit: hierarchies
Kind: hierarchies
Tab: test-tab (subject areas: Sample Sales; estate sampleapp)
Facts from: logical_dimensions, logical_level_keys, logical_levels, presentation_hierarchies, presentation_level_columns, presentation_levels
Rows of this packet a document must cover: 8. Each is on a line that ends with (REQUIRED).
To cite a fact, copy the marker in square brackets that follows it, exactly as written. "->" points at another row.

## Dimensions and their levels

- Dimension "Office Hierarchy" in business model "SAMPLESALES": 5 levels; marked as a time dimension: not recorded. [kb:sampleapp::bmm::SAMPLESALES.Office Hierarchy] (REQUIRED)
    - Default root level: "Dim Offices Total". [kb:sampleapp::bmm::SAMPLESALES.Office Hierarchy]
    - Level 0 "Dim Offices Total" (grand total); member count as recorded 1; key column: none recorded [kb:sampleapp::bmm::SAMPLESALES.Office Hierarchy.Dim Offices Total]
        - Level directly above it (its parent): none recorded (a top level); level(s) directly below it (its children): "Company". [kb:sampleapp::bmm::SAMPLESALES.Office Hierarchy.Dim Offices Total]
    - Level 1 "Company"; member count as recorded 1; key column: "Dim Offices"."Company" -> sampleapp::bmm::SAMPLESALES.Dim Offices.Company [kb:sampleapp::bmm::SAMPLESALES.Office Hierarchy.Company]
        - Level directly above it (its parent): "Dim Offices Total"; level(s) directly below it (its children): "Organization". [kb:sampleapp::bmm::SAMPLESALES.Office Hierarchy.Company]
        - Key "Company" of level "Company": primary key of the level not recorded; chronological key not recorded; used for drill-down true; made of column(s) "Dim Offices"."Company". [kb:sampleapp::bmm::SAMPLESALES.Office Hierarchy.Company.key.Company]
        - Key "Company Key" of level "Company": primary key of the level true; chronological key not recorded; used for drill-down not recorded; made of column(s) "Dim Offices"."Company Key". [kb:sampleapp::bmm::SAMPLESALES.Office Hierarchy.Company.key.Company Key]
    - Level 2 "Organization"; member count as recorded 1; key column: "Dim Offices"."Organization" -> sampleapp::bmm::SAMPLESALES.Dim Offices.Organization [kb:sampleapp::bmm::SAMPLESALES.Office Hierarchy.Organization]
        - Level directly above it (its parent): "Company"; level(s) directly below it (its children): "Department". [kb:sampleapp::bmm::SAMPLESALES.Office Hierarchy.Organization]
        - Key "Org Key" of level "Organization": primary key of the level true; chronological key not recorded; used for drill-down not recorded; made of column(s) "Dim Offices"."Org Key". [kb:sampleapp::bmm::SAMPLESALES.Office Hierarchy.Organization.key.Org Key]
        - Key "Organization" of level "Organization": primary key of the level not recorded; chronological key not recorded; used for drill-down true; made of column(s) "Dim Offices"."Organization". [kb:sampleapp::bmm::SAMPLESALES.Office Hierarchy.Organization.key.Organization]
    - Level 3 "Department"; member count as recorded 1; key column: "Dim Offices"."Department" -> sampleapp::bmm::SAMPLESALES.Dim Offices.Department [kb:sampleapp::bmm::SAMPLESALES.Office Hierarchy.Department]
        - Level directly above it (its parent): "Organization"; level(s) directly below it (its children): "Office Detail". [kb:sampleapp::bmm::SAMPLESALES.Office Hierarchy.Department]
        - Key "Department" of level "Department": primary key of the level not recorded; chronological key not recorded; used for drill-down true; made of column(s) "Dim Offices"."Department". [kb:sampleapp::bmm::SAMPLESALES.Office Hierarchy.Department.key.Department]
        - Key "Dept Key" of level "Department": primary key of the level true; chronological key not recorded; used for drill-down not recorded; made of column(s) "Dim Offices"."Dept Key". [kb:sampleapp::bmm::SAMPLESALES.Office Hierarchy.Department.key.Dept Key]
    - Level 4 "Office Detail"; member count as recorded 1; key column: "Dim Offices"."Office Dsc" -> sampleapp::bmm::SAMPLESALES.Dim Offices.Office Dsc [kb:sampleapp::bmm::SAMPLESALES.Office Hierarchy.Office Detail]
        - Level directly above it (its parent): "Department"; level(s) directly below it (its children): none recorded (a lowest level). [kb:sampleapp::bmm::SAMPLESALES.Office Hierarchy.Office Detail]
        - Key "Office Dsc" of level "Office Detail": primary key of the level not recorded; chronological key not recorded; used for drill-down true; made of column(s) "Dim Offices"."Office Dsc". [kb:sampleapp::bmm::SAMPLESALES.Office Hierarchy.Office Detail.key.Office Dsc]
        - Key "Office Key" of level "Office Detail": primary key of the level true; chronological key not recorded; used for drill-down not recorded; made of column(s) "Dim Offices"."Office Key". [kb:sampleapp::bmm::SAMPLESALES.Office Hierarchy.Office Detail.key.Office Key]
    - Offered to report authors as hierarchy "Office Hierarchy" in presentation table "Dim Offices"; 5 levels. [kb:sampleapp::pres::Sample Sales.Dim Offices.Office Hierarchy] (REQUIRED)
        - Presentation level 0 "Dim Offices Total"; logical level "Dim Offices Total". [kb:sampleapp::pres::Sample Sales.Dim Offices.Office Hierarchy.Dim Offices Total]
        - Presentation level 1 "Company"; logical level "Company". [kb:sampleapp::pres::Sample Sales.Dim Offices.Office Hierarchy.Company]
            - Shows column "Company". [kb:sampleapp::pres::Sample Sales.Dim Offices.Office Hierarchy.Company.0]
        - Presentation level 2 "Organization"; logical level "Organization". [kb:sampleapp::pres::Sample Sales.Dim Offices.Office Hierarchy.Organization]
            - Shows column "Organization". [kb:sampleapp::pres::Sample Sales.Dim Offices.Office Hierarchy.Organization.0]
        - Presentation level 3 "Department"; logical level "Department". [kb:sampleapp::pres::Sample Sales.Dim Offices.Office Hierarchy.Department]
            - Shows column "Department". [kb:sampleapp::pres::Sample Sales.Dim Offices.Office Hierarchy.Department.0]
        - Presentation level 4 "Office Detail"; logical level "Office Detail". [kb:sampleapp::pres::Sample Sales.Dim Offices.Office Hierarchy.Office Detail]
            - Shows column "Office Dsc". [kb:sampleapp::pres::Sample Sales.Dim Offices.Office Hierarchy.Office Detail.0]

- Dimension "Products Hierararchy" in business model "SAMPLESALES": 5 levels; marked as a time dimension: not recorded. [kb:sampleapp::bmm::SAMPLESALES.Products Hierararchy] (REQUIRED)
    - Default root level: "Dim Products Total". [kb:sampleapp::bmm::SAMPLESALES.Products Hierararchy]
    - Level 0 "Dim Products Total" (grand total); member count as recorded 1; key column: none recorded [kb:sampleapp::bmm::SAMPLESALES.Products Hierararchy.Dim Products Total]
        - Level directly above it (its parent): none recorded (a top level); level(s) directly below it (its children): "Brand". [kb:sampleapp::bmm::SAMPLESALES.Products Hierararchy.Dim Products Total]
    - Level 1 "Brand"; member count as recorded 1; key column: "Dim Products"."Brand" -> sampleapp::bmm::SAMPLESALES.Dim Products.Brand [kb:sampleapp::bmm::SAMPLESALES.Products Hierararchy.Brand]
        - Level directly above it (its parent): "Dim Products Total"; level(s) directly below it (its children): "Line Of Business". [kb:sampleapp::bmm::SAMPLESALES.Products Hierararchy.Brand]
        - Key "Brand" of level "Brand": primary key of the level true; chronological key not recorded; used for drill-down true; made of column(s) "Dim Products"."Brand". [kb:sampleapp::bmm::SAMPLESALES.Products Hierararchy.Brand.key.Brand]
    - Level 2 "Line Of Business"; member count as recorded 1; key column: "Dim Products"."Lob" -> sampleapp::bmm::SAMPLESALES.Dim Products.Lob [kb:sampleapp::bmm::SAMPLESALES.Products Hierararchy.Line Of Business]
        - Level directly above it (its parent): "Brand"; level(s) directly below it (its children): "Products Type". [kb:sampleapp::bmm::SAMPLESALES.Products Hierararchy.Line Of Business]
        - Key "Lob" of level "Line Of Business": primary key of the level true; chronological key not recorded; used for drill-down true; made of column(s) "Dim Products"."Lob". [kb:sampleapp::bmm::SAMPLESALES.Products Hierararchy.Line Of Business.key.Lob]
    - Level 3 "Products Type"; member count as recorded 1; key column: "Dim Products"."Type" -> sampleapp::bmm::SAMPLESALES.Dim Products.Type [kb:sampleapp::bmm::SAMPLESALES.Products Hierararchy.Products Type]
        - Level directly above it (its parent): "Line Of Business"; level(s) directly below it (its children): "Product Detail". [kb:sampleapp::bmm::SAMPLESALES.Products Hierararchy.Products Type]
        - Key "Type" of level "Products Type": primary key of the level true; chronological key not recorded; used for drill-down true; made of column(s) "Dim Products"."Type". [kb:sampleapp::bmm::SAMPLESALES.Products Hierararchy.Products Type.key.Type]
    - Level 4 "Product Detail"; member count as recorded 1; key column: "Dim Products"."Prod Dsc" -> sampleapp::bmm::SAMPLESALES.Dim Products.Prod Dsc [kb:sampleapp::bmm::SAMPLESALES.Products Hierararchy.Product Detail]
        - Level directly above it (its parent): "Products Type"; level(s) directly below it (its children): none recorded (a lowest level). [kb:sampleapp::bmm::SAMPLESALES.Products Hierararchy.Product Detail]
        - Key "Dim Products Detail_Key" of level "Product Detail": primary key of the level true; chronological key not recorded; used for drill-down true; made of column(s) "Dim Products"."Prod Dsc". [kb:sampleapp::bmm::SAMPLESALES.Products Hierararchy.Product Detail.key.Dim Products Detail_Key]
        - Key "Dim Products Detail_Key1" of level "Product Detail": primary key of the level not recorded; chronological key not recorded; used for drill-down not recorded; made of column(s) "Dim Products"."Prod Key". [kb:sampleapp::bmm::SAMPLESALES.Products Hierararchy.Product Detail.key.Dim Products Detail_Key1]
    - Offered to report authors as hierarchy "Products Hierararchy" in presentation table "Dim Products"; 5 levels. [kb:sampleapp::pres::Sample Sales.Dim Products.Products Hierararchy] (REQUIRED)
        - Presentation level 0 "Dim Products Total"; logical level "Dim Products Total". [kb:sampleapp::pres::Sample Sales.Dim Products.Products Hierararchy.Dim Products Total]
        - Presentation level 1 "Brand"; logical level "Brand". [kb:sampleapp::pres::Sample Sales.Dim Products.Products Hierararchy.Brand]
            - Shows column "Brand". [kb:sampleapp::pres::Sample Sales.Dim Products.Products Hierararchy.Brand.0]
        - Presentation level 2 "Line Of Business"; logical level "Line Of Business". [kb:sampleapp::pres::Sample Sales.Dim Products.Products Hierararchy.Line Of Business]
            - Shows column "Lob". [kb:sampleapp::pres::Sample Sales.Dim Products.Products Hierararchy.Line Of Business.0]
        - Presentation level 3 "Products Type"; logical level "Products Type". [kb:sampleapp::pres::Sample Sales.Dim Products.Products Hierararchy.Products Type]
            - Shows column "Type". [kb:sampleapp::pres::Sample Sales.Dim Products.Products Hierararchy.Products Type.0]
        - Presentation level 4 "Product Detail"; logical level "Product Detail". [kb:sampleapp::pres::Sample Sales.Dim Products.Products Hierararchy.Product Detail]
            - Shows column "Prod Dsc". [kb:sampleapp::pres::Sample Sales.Dim Products.Products Hierararchy.Product Detail.0]

- Dimension "Sales Rep Hierarchy" in business model "SAMPLESALES": 2 levels; parent-child (value based: the depth is not fixed); marked as a time dimension: not recorded. [kb:sampleapp::bmm::SAMPLESALES.Sales Rep Hierarchy] (REQUIRED)
    - Default root level: "Total". [kb:sampleapp::bmm::SAMPLESALES.Sales Rep Hierarchy]
    - Level 0 "Total" (grand total); member count as recorded 1; key column: none recorded [kb:sampleapp::bmm::SAMPLESALES.Sales Rep Hierarchy.Total]
        - Level directly above it (its parent): none recorded (a top level); level(s) directly below it (its children): "Detail". [kb:sampleapp::bmm::SAMPLESALES.Sales Rep Hierarchy.Total]
    - Level 1 "Detail"; member count as recorded 1; key column: "Dim Sales Rep"."Employee ID" -> sampleapp::bmm::SAMPLESALES.Dim Sales Rep.Employee ID [kb:sampleapp::bmm::SAMPLESALES.Sales Rep Hierarchy.Detail]
        - Level directly above it (its parent): "Total"; level(s) directly below it (its children): none recorded (a lowest level). [kb:sampleapp::bmm::SAMPLESALES.Sales Rep Hierarchy.Detail]
        - Key "EMP_ID" of level "Detail": primary key of the level true; chronological key not recorded; used for drill-down true; made of column(s) "Dim Sales Rep"."Employee ID". [kb:sampleapp::bmm::SAMPLESALES.Sales Rep Hierarchy.Detail.key.EMP_ID]
    - Parent-child relationships: table source "LTS Employee" of logical table "Dim Sales Rep" names physical table "EMP_PARENT_CHILD" as the table that holds them (stated with its citation in unit table-06-dim-sales-rep). -> sampleapp::bmm::SAMPLESALES.Dim Sales Rep.src.LTS Employee
        - In that table: Member column: "EMP_PARENT_CHILD"."MEMBER_KEY"; parent (ancestor) column: "EMP_PARENT_CHILD"."ANCESTOR_KEY"; distance column (steps between the two): "EMP_PARENT_CHILD"."DISTANCE"; leaf-flag column: "EMP_PARENT_CHILD"."IS_LEAF" (stated with its citation in unit dbtable-02-emp-parent-child). -> sampleapp::phys::xe.SAMPLESALES.EMP_PARENT_CHILD
    - Offered to report authors as hierarchy "Sales Rep Hierarchy" in presentation table "Dim Sales Rep"; 0 levels; shown by column "Employee Name". [kb:sampleapp::pres::Sample Sales.Dim Sales Rep.Sales Rep Hierarchy] (REQUIRED)

- Dimension "Time Hierarchy" in business model "SAMPLESALES": 7 levels; marked as a time dimension: true. [kb:sampleapp::bmm::SAMPLESALES.Time Hierarchy] (REQUIRED)
    - Default root level: "Dim Time Total". [kb:sampleapp::bmm::SAMPLESALES.Time Hierarchy]
    - Level 0 "Dim Time Total" (grand total); member count as recorded 1; key column: none recorded [kb:sampleapp::bmm::SAMPLESALES.Time Hierarchy.Dim Time Total]
        - Level directly above it (its parent): none recorded (a top level); level(s) directly below it (its children): "Year". [kb:sampleapp::bmm::SAMPLESALES.Time Hierarchy.Dim Time Total]
    - Level 1 "Year"; member count as recorded 2; key column: "Dim Time"."Per Name Year" -> sampleapp::bmm::SAMPLESALES.Dim Time.Per Name Year [kb:sampleapp::bmm::SAMPLESALES.Time Hierarchy.Year]
        - Level directly above it (its parent): "Dim Time Total"; level(s) directly below it (its children): "Half Year". [kb:sampleapp::bmm::SAMPLESALES.Time Hierarchy.Year]
        - Key "Per Name Year" of level "Year": primary key of the level true; chronological key true; used for drill-down true; made of column(s) "Dim Time"."Per Name Year". [kb:sampleapp::bmm::SAMPLESALES.Time Hierarchy.Year.key.Per Name Year]
    - Level 2 "Half Year"; member count as recorded 180; key column: "Dim Time"."Per Name Half" -> sampleapp::bmm::SAMPLESALES.Dim Time.Per Name Half [kb:sampleapp::bmm::SAMPLESALES.Time Hierarchy.Half Year]
        - Level directly above it (its parent): "Year"; level(s) directly below it (its children): "Quarter". [kb:sampleapp::bmm::SAMPLESALES.Time Hierarchy.Half Year]
        - Key "Per Name Half" of level "Half Year": primary key of the level true; chronological key true; used for drill-down true; made of column(s) "Dim Time"."Per Name Half". [kb:sampleapp::bmm::SAMPLESALES.Time Hierarchy.Half Year.key.Per Name Half]
    - Level 3 "Quarter"; member count as recorded 270; key column: "Dim Time"."Per Name Qtr" -> sampleapp::bmm::SAMPLESALES.Dim Time.Per Name Qtr [kb:sampleapp::bmm::SAMPLESALES.Time Hierarchy.Quarter]
        - Level directly above it (its parent): "Half Year"; level(s) directly below it (its children): "Month". [kb:sampleapp::bmm::SAMPLESALES.Time Hierarchy.Quarter]
        - Key "Per Name Qtr" of level "Quarter": primary key of the level true; chronological key true; used for drill-down true; made of column(s) "Dim Time"."Per Name Qtr". [kb:sampleapp::bmm::SAMPLESALES.Time Hierarchy.Quarter.key.Per Name Qtr]
    - Level 4 "Month"; member count as recorded 322; key column: "Dim Time"."Per Name Month" -> sampleapp::bmm::SAMPLESALES.Dim Time.Per Name Month [kb:sampleapp::bmm::SAMPLESALES.Time Hierarchy.Month]
        - Level directly above it (its parent): "Quarter"; level(s) directly below it (its children): "Week". [kb:sampleapp::bmm::SAMPLESALES.Time Hierarchy.Month]
        - Key "Per Name Month" of level "Month": primary key of the level true; chronological key true; used for drill-down true; made of column(s) "Dim Time"."Per Name Month". [kb:sampleapp::bmm::SAMPLESALES.Time Hierarchy.Month.key.Per Name Month]
    - Level 5 "Week"; member count as recorded 350; key column: "Dim Time"."Per Name Week" -> sampleapp::bmm::SAMPLESALES.Dim Time.Per Name Week [kb:sampleapp::bmm::SAMPLESALES.Time Hierarchy.Week]
        - Level directly above it (its parent): "Month"; level(s) directly below it (its children): "Dim Time Detail". [kb:sampleapp::bmm::SAMPLESALES.Time Hierarchy.Week]
        - Key "Per Name Week" of level "Week": primary key of the level true; chronological key true; used for drill-down true; made of column(s) "Dim Time"."Per Name Week". [kb:sampleapp::bmm::SAMPLESALES.Time Hierarchy.Week.key.Per Name Week]
    - Level 6 "Dim Time Detail"; member count as recorded 365; key column: "Dim Time"."Calendar Date" -> sampleapp::bmm::SAMPLESALES.Dim Time.Calendar Date [kb:sampleapp::bmm::SAMPLESALES.Time Hierarchy.Dim Time Detail]
        - Level directly above it (its parent): "Week"; level(s) directly below it (its children): none recorded (a lowest level). [kb:sampleapp::bmm::SAMPLESALES.Time Hierarchy.Dim Time Detail]
        - Key "Dim Time Detail_Key" of level "Dim Time Detail": primary key of the level true; chronological key true; used for drill-down true; made of column(s) "Dim Time"."Calendar Date". [kb:sampleapp::bmm::SAMPLESALES.Time Hierarchy.Dim Time Detail.key.Dim Time Detail_Key]
    - Offered to report authors as hierarchy "Time Hierarchy" in presentation table "Dim Time"; 7 levels. [kb:sampleapp::pres::Sample Sales.Dim Time.Time Hierarchy] (REQUIRED)
        - Presentation level 0 "Dim Time Total"; logical level "Dim Time Total". [kb:sampleapp::pres::Sample Sales.Dim Time.Time Hierarchy.Dim Time Total]
        - Presentation level 1 "Year"; logical level "Year". [kb:sampleapp::pres::Sample Sales.Dim Time.Time Hierarchy.Year]
            - Shows column "Per Name Year". [kb:sampleapp::pres::Sample Sales.Dim Time.Time Hierarchy.Year.0]
        - Presentation level 2 "Half Year"; logical level "Half Year". [kb:sampleapp::pres::Sample Sales.Dim Time.Time Hierarchy.Half Year]
            - Shows column "Per Name Half". [kb:sampleapp::pres::Sample Sales.Dim Time.Time Hierarchy.Half Year.0]
        - Presentation level 3 "Quarter"; logical level "Quarter". [kb:sampleapp::pres::Sample Sales.Dim Time.Time Hierarchy.Quarter]
            - Shows column "Per Name Qtr". [kb:sampleapp::pres::Sample Sales.Dim Time.Time Hierarchy.Quarter.0]
        - Presentation level 4 "Month"; logical level "Month". [kb:sampleapp::pres::Sample Sales.Dim Time.Time Hierarchy.Month]
            - Shows column "Per Name Month". [kb:sampleapp::pres::Sample Sales.Dim Time.Time Hierarchy.Month.0]
        - Presentation level 5 "Week"; logical level "Week". [kb:sampleapp::pres::Sample Sales.Dim Time.Time Hierarchy.Week]
            - Shows column "Per Name Week". [kb:sampleapp::pres::Sample Sales.Dim Time.Time Hierarchy.Week.0]
        - Presentation level 6 "Dim Time Detail"; logical level "Dim Time Detail". [kb:sampleapp::pres::Sample Sales.Dim Time.Time Hierarchy.Dim Time Detail]
            - Shows column "Calendar Date". [kb:sampleapp::pres::Sample Sales.Dim Time.Time Hierarchy.Dim Time Detail.0]

