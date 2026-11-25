## What report authors see

The subject area "Sample Sales" is built on the business model "SAMPLESALES" [kb:sampleapp::pres::Sample Sales]. It has one other name (alias): `SAMPLESALES` [kb:sampleapp::pres::Sample Sales]. When a query names no measure, OBIEE uses the measure "Fact Target"."Revenue" (the "implicit fact column") [kb:sampleapp::pres::Sample Sales].

### Tables report authors see

| Table | Fields | Business table behind it | Evidence |
|---|---|---|---|
| Dim Offices | 9 | Dim Offices (see the section Dim Offices) | [kb:sampleapp::pres::Sample Sales.Dim Offices] |
| Dim Products | 9 | Dim Products (see the section Dim Products) | [kb:sampleapp::pres::Sample Sales.Dim Products] |
| Dim Sales Rep | 5 | Dim Sales Rep (see the section Dim Sales Rep) | [kb:sampleapp::pres::Sample Sales.Dim Sales Rep] |
| Dim Time | 6 | Dim Time (see the section Dim Time) | [kb:sampleapp::pres::Sample Sales.Dim Time] |
| Fact Revenue | 15 | Fact Revenue (see the section Fact Revenue) | [kb:sampleapp::pres::Sample Sales.Fact Revenue] |
| Fact Target | 5 | Fact Target (see the section Fact Target) | [kb:sampleapp::pres::Sample Sales.Fact Target] |

The name shown to users for the table "Dim Time" is not a fixed text but a formula, as written: `VALUEOF(NQ_SESSION.CN_Sample_Sales_Dim_Time)` [kb:sampleapp::pres::Sample Sales.Dim Time]. It names the variable CN_Sample_Sales_Dim_Time; whether the repository defines it is stated in the section Values set per user or for the whole system. What name report authors actually see is not recorded (open question Q1).

### Counts

These are counted from the Slice's tables and have no Row identifier.

- Tables report authors see: 6; fields they see: 49, of which measures: 8.
- Business tables described: 7; their fields: 63, of which measures: 11 (measures not shown to report authors are described with their business table).
- Sources: 12; database tables: 13; their columns: 113 (every column of each table, whether or not a field maps it); their keys: 8.
- Business-model joins: 7; database joins: 17.
- Drill paths: 4; drill paths offered to report authors: 4.
- Variables: 1; security rows: 7.
- Reports built on the subject area: 0; dashboards placing them: 0; saved filters they use: 0.
- Errors about objects of this subject area: 0; kinds of source content not read: 95 (both listed in the section What the export did not capture).

### Items of the estate that are in no subject area

These exist in the full Knowledge Base and belong to no subject area, or not to this one. They are not described in this document. The counts come from the manifest and have no Row identifier.

- Object permissions whose target was not recorded: 2.
- Role privileges of packages that no permission of this subject area goes through: 0.
- Queries that set a value (init blocks) and set no variable of this subject area: 1.
- Variables that no expression of this subject area names: 1.

### What was examined

The estate is sampleapp; of its halves, only the repository half ("rpd") was extracted. The estate has no Web Catalog half, so reports and dashboards were not examined. The Knowledge Base was extracted at 2026-11-23T20:55:33Z by extractor version 0.1.0. (From the manifest; no Row identifier.)

This document was written from these units, in order: overview, table-01-fact-revenue, table-02-fact-target, table-03-dim-offices, table-04-dim-products, table-05-dim-products-translated, table-06-dim-sales-rep, table-07-dim-time, dbtable-01-employee, dbtable-02-emp-parent-child, dbtable-03-factcrossjoin, dbtable-04-samp-offices-d, dbtable-05-samp-products-d, dbtable-06-samp-products-d-translation, dbtable-07-samp-revenue-a, dbtable-08-samp-revenue-a-mth, dbtable-09-samp-revenue-a-yr, dbtable-10-samp-revenue-f, dbtable-11-samp-targets-f, dbtable-12-samp-time-day-d, dbtable-13-samp-time-mth-d, joins, hierarchies, variables, security, reports (one unit per dashboard or folder), saved-filters (when there are any), gaps.
