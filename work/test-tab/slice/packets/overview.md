# Overview of tab "test-tab"
Unit: overview
Kind: overview
Tab: test-tab (subject areas: Sample Sales; estate sampleapp)
Facts from: presentation_subject_areas, presentation_tables
Rows of this packet a document must cover: 7. Each is on a line that ends with (REQUIRED).
To cite a fact, copy the marker in square brackets that follows it, exactly as written. "->" points at another row.

## The tab

- Tab "test-tab": this slice holds what the columns the Operator confirmed for the tab's FSD items need, walked as a subject-area slice is walked. Decisions: 27 rows (26 confirmed, 1 rejected); items: 18 confirmed, 1 rejected, 2 unmatched, 0 undecided. (manifest; no Row identifier)
- Unit tab-fields lists every FSD item with its confirmed columns; items not confirmed contribute nothing to the slice. (tab_fields; no Row identifier)

## Subject areas the confirmed columns are in

- Subject area "Sample Sales", built on business model "SAMPLESALES". [kb:sampleapp::pres::Sample Sales] (REQUIRED)

## Presentation tables the confirmed columns are in

- Presentation table "Dim Offices" of subject area "Sample Sales": confirmed columns "Office Dsc". [kb:sampleapp::pres::Sample Sales.Dim Offices] (REQUIRED)
- Presentation table "Dim Products" of subject area "Sample Sales": confirmed columns "Brand", "Prod Dsc". [kb:sampleapp::pres::Sample Sales.Dim Products] (REQUIRED)
- Presentation table "Dim Sales Rep" of subject area "Sample Sales": confirmed columns "Employee Name". [kb:sampleapp::pres::Sample Sales.Dim Sales Rep] (REQUIRED)
- Presentation table "Dim Time" of subject area "Sample Sales": confirmed columns "Per Name Month", "Per Name Year". [kb:sampleapp::pres::Sample Sales.Dim Time] (REQUIRED)
- Presentation table "Fact Revenue" of subject area "Sample Sales": confirmed columns "Discnt Value", "Preferred Currency", "Revenue", "Units". [kb:sampleapp::pres::Sample Sales.Fact Revenue] (REQUIRED)
- Presentation table "Fact Target" of subject area "Sample Sales": confirmed columns "Revenue". [kb:sampleapp::pres::Sample Sales.Fact Target] (REQUIRED)

## Counts (counted from the slice's tables; a count has no Row identifier)

- Confirmed columns: 11; FSD items: 21.
- Logical tables described: 7 (each whole); logical columns: 63, of which measures: 11.
- Logical table sources: 12; physical tables: 13 (each whole); their columns: 113; their keys: 8.
- Business-model joins: 7; physical joins: 17; dimensions: 4; hierarchies offered to report authors: 4.
- Variables: 1; security rows: 5; reports: 0.

## Where this came from (manifest; no Row identifier)

- Estate: sampleapp; halves of the estate that were extracted: rpd.
- Knowledge Base extracted at 2026-11-24T02:56:24Z by extractor version 0.1.0.
- Units of this slice, in writing order: overview, tab-fields, table-01-fact-revenue, table-02-fact-target, table-03-dim-offices, table-04-dim-products, table-05-dim-products-translated, table-06-dim-sales-rep, table-07-dim-time, dbtable-01-employee, dbtable-02-emp-parent-child, dbtable-03-factcrossjoin, dbtable-04-samp-offices-d, dbtable-05-samp-products-d, dbtable-06-samp-products-d-translation, dbtable-07-samp-revenue-a, dbtable-08-samp-revenue-a-mth, dbtable-09-samp-revenue-a-yr, dbtable-10-samp-revenue-f, dbtable-11-samp-targets-f, dbtable-12-samp-time-day-d, dbtable-13-samp-time-mth-d, joins, hierarchies, variables, security, reports (one unit per dashboard or folder), saved-filters (when there are any), gaps.

