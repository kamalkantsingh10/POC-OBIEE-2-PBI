## What this design covers

This design covers the tab "test-tab" of the estate sampleapp. The slice holds what the columns the Operator confirmed for the tab's FSD items need. Decisions: 27 rows (26 confirmed, 1 rejected); items: 18 confirmed, 1 rejected, 2 unmatched, 0 undecided.

The confirmed columns are in one subject area: "Sample Sales", built on business model "SAMPLESALES". [kb:sampleapp::pres::Sample Sales]

### Tables report authors see, with the confirmed columns

| Table | Confirmed fields | Evidence |
|---|---|---|
| Dim Offices | "Office Dsc" | [kb:sampleapp::pres::Sample Sales.Dim Offices] |
| Dim Products | "Brand", "Prod Dsc" | [kb:sampleapp::pres::Sample Sales.Dim Products] |
| Dim Sales Rep | "Employee Name" | [kb:sampleapp::pres::Sample Sales.Dim Sales Rep] |
| Dim Time | "Per Name Month", "Per Name Year" | [kb:sampleapp::pres::Sample Sales.Dim Time] |
| Fact Revenue | "Discnt Value", "Preferred Currency", "Revenue", "Units" | [kb:sampleapp::pres::Sample Sales.Fact Revenue] |
| Fact Target | "Revenue" | [kb:sampleapp::pres::Sample Sales.Fact Target] |

### What the slice holds

- Confirmed columns: 11; FSD items: 21.
- Logical tables described: 7 (each whole); logical columns: 63, of which measures: 11.
- Logical table sources: 12; physical tables: 13 (each whole); their columns: 113; their keys: 8.
- Business-model joins: 7; physical joins: 17; dimensions: 4; hierarchies offered to report authors: 4.
- Variables: 1; security rows: 5; reports: 0.

### Where it came from

The estate sampleapp, of which the rpd half was extracted. The Knowledge Base was extracted at 2026-11-24T02:56:24Z by extractor version 0.1.0.

The as-is document used is that of "Sample Sales", as-is/sampleapp/sample-sales.md; its status line reads "Status: reviewed 2026-11-23 (25 findings corrected, 1 turned into questions, 14 questions open)". Its open questions are referred to as "as-is document, question Q<n>".
