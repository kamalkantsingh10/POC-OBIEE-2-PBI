# Planted errors: answer key

Document: `as-is/sampleapp-planted/sample-sales.md` (parts in `as-is/sampleapp-planted/sample-sales.parts/`), project `/home/kamal/office-work/POC-OBIEE-2-PBI`.
Slice: `slices/sampleapp/sample-sales`. A byte-for-byte copy of the planted folder as it was before planting is in `/tmp/claude-1000/-home-kamal-office-work-POC-OBIEE-2-PBI/d2e8c268-2885-44d1-a9a8-16c4b9fe4729/scratchpad/orig/` (at that moment it was identical to `as-is/sampleapp/`; the real document has since been changed by another process, so compare against `orig/`, not against `as-is/sampleapp/`).

After planting: `assemble-doc` exit 0 (21 parts, 1022 lines); `check-doc` exit 0: required 125, covered 125, citations 573 checked (574 before: 3 added, 4 removed), questions 19 open, quotes 112, findings 0, warnings 0. Line 1 is `Status: draft, not reviewed`. No edit had to be adjusted to make the check pass.

12 errors in 11 part files (10 unit parts plus `990-open-questions.md`). Kinds: 3 wrong fact (1-3), 3 unsupported statement (4-6), 2 missing content (7-8), 2 misquoted formula with backticks unchanged (9-10), 2 bad open question (11-12).

## 1. wrong fact

- Part file: `as-is/sampleapp-planted/sample-sales.parts/010-joins.md`
- Packet: `slices/sampleapp/sample-sales/packets/joins.md`

Original text:

~~~~
| Dim Sales Rep | Fact Revenue | "Dim Sales Rep" (1..1), "Fact Revenue" (0..n) |
~~~~

Planted text:

~~~~
| Dim Sales Rep | Fact Revenue | "Dim Sales Rep" (0..1), "Fact Revenue" (0..n) |
~~~~

What the packet says: Line 15: `"Dim Sales Rep" (1..1) joins "Fact Revenue" (0..n); join type Inner.` The cardinality of the Dim Sales Rep side is 1..1, not 0..1.

Could `check-doc` have caught it: No. The citation is unchanged and still valid; check-doc does not judge truth.

## 2. wrong fact

- Part file: `as-is/sampleapp-planted/sample-sales.parts/008-table-06-dim-sales-rep.md`
- Packet: `slices/sampleapp/sample-sales/packets/table-06-dim-sales-rep.md`

Original text:

~~~~
| Distance | EMP_PARENT_CHILD.DISTANCE | INT |
~~~~

Planted text:

~~~~
| Distance | EMP_PARENT_CHILD.DISTANCE | DOUBLE, precision 15 |
~~~~

What the packet says: Physical column "EMP_PARENT_CHILD"."DISTANCE" is `(type INT)`, not DOUBLE precision 15.

Could `check-doc` have caught it: No. No citation or backticked text changed.

## 3. wrong fact

- Part file: `as-is/sampleapp-planted/sample-sales.parts/013-security.md`
- Packet: `slices/sampleapp/sample-sales/packets/security.md`

Original text:

~~~~
| `write` | field "Revenue" of "Fact Target" | PrivPack_4209:18181355661677 | no role is recorded in the slice for that privilege package |
~~~~

Planted text:

~~~~
| `write` | field "Revenue" of "Fact Target" | PrivPack_4209:18181355661677 | 1 role: BI Author Role |
~~~~

What the packet says: Permission `write` on "Revenue" through PrivPack_4209:18181355661677 (ObjPriv_1500:18211355661677.o0): "Held by: no role is recorded in the slice for that privilege package." BI Author Role holds only PrivPack_4209:18101355661677.

Could `check-doc` have caught it: No.

## 4. unsupported statement

- Part file: `as-is/sampleapp-planted/sample-sales.parts/004-table-02-fact-target.md`
- Packet: `slices/sampleapp/sample-sales/packets/table-02-fact-target.md`

Original text:

~~~~
Its one source is "SAMP_TARGETS_F" (counted).
~~~~

Planted text:

~~~~
Its one source is "SAMP_TARGETS_F" (counted). It holds the sales targets that the numbers of Fact Revenue are measured against [kb:sampleapp::bmm::SAMPLESALES.Fact Target].
~~~~

What the packet says: The packet states only the table, its columns, aggregation rules and source. It says nothing about what the table holds in business terms or about a comparison with Fact Revenue. The citation used is a real one from this packet (the logical table row). The part itself says further down that the business meaning is not recorded (Q8).

Could `check-doc` have caught it: No. The citation exists in the unit's own packet.

## 5. unsupported statement

- Part file: `as-is/sampleapp-planted/sample-sales.parts/005-table-03-dim-offices.md`
- Packet: `slices/sampleapp/sample-sales/packets/table-03-dim-offices.md`

Original text:

~~~~
- "Sequence" is mapped but not shown to report authors [kb:sampleapp::bmm::SAMPLESALES.Dim Offices.Sequence].
~~~~

Planted text:

~~~~
- "Sequence" is mapped but not shown to report authors [kb:sampleapp::bmm::SAMPLESALES.Dim Offices.Sequence]. It sets the order in which the offices are listed in reports [kb:sampleapp::bmm::SAMPLESALES.Dim Offices.Sequence].
~~~~

What the packet says: Line 53: "Sequence": not a measure; writeable false; mapped in 1 of 1 table sources; not shown in this subject area. Nothing about what it is used for or any ordering.

Could `check-doc` have caught it: No. The citation exists in the unit's own packet.

## 6. unsupported statement

- Part file: `as-is/sampleapp-planted/sample-sales.parts/012-variables.md`
- Packet: `slices/sampleapp/sample-sales/packets/variables.md`

Original text:

~~~~
- Its default value is `'gc1'` [kb:sampleapp::var::PREFERRED_CURRENCY].
~~~~

Planted text:

~~~~
- Its default value is `'gc1'` [kb:sampleapp::var::PREFERRED_CURRENCY]. A user for whom the query below returns no row keeps this value [kb:sampleapp::var::PREFERRED_CURRENCY].
~~~~

What the packet says: Default value `'gc1'`; init block "initBlockPrefCurrency": session true; enabled not recorded; refresh period not recorded; and the query text. Nothing about what happens at run time when the query returns no row. The part itself says the value a user gets at login is not recorded (Q16).

Could `check-doc` have caught it: No. The citation exists in the unit's own packet.

## 7. missing content

- Part file: `as-is/sampleapp-planted/sample-sales.parts/002-table-01-fact-revenue-p1.md`
- Packet: `slices/sampleapp/sample-sales/packets/table-01-fact-revenue-p1.md`

Original text:

~~~~
| Sales Employee Key | 1 of 5: SAMP_REVENUE_F | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.Sales Employee Key] |
~~~~

Planted text:

~~~~
(nothing: the whole line, with its line end, was removed)
~~~~

What the packet says: Line 111: Logical column "Sales Employee Key": not a measure; mapped in 1 of 5 table sources; not shown in this subject area. It is the 4th hidden non-measure field; the part's opening still says 7 of 22 are not shown, and the two tables now list only 6. The row is not a required row (a logical column that is not a measure, with no presentation column).

Could `check-doc` have caught it: No. The removed citation is not in required_rows.csv, so coverage still passes (verified: 125 of 125).

## 8. missing content

- Part file: `as-is/sampleapp-planted/sample-sales.parts/011-hierarchies.md`
- Packet: `slices/sampleapp/sample-sales/packets/hierarchies.md`

Original text:

~~~~
| 5 Week | "Per Name Week" of Dim Time | 350 | level "Week", field "Per Name Week" | [kb:sampleapp::bmm::SAMPLESALES.Time Hierarchy.Week] [kb:sampleapp::pres::Sample Sales.Dim Time.Time Hierarchy.Week] [kb:sampleapp::pres::Sample Sales.Dim Time.Time Hierarchy.Week.0] |
~~~~

Planted text:

~~~~
(nothing: the whole line, with its line end, was removed)
~~~~

What the packet says: Line 60: Level 5 "Week"; member count as recorded 350; key column "Dim Time"."Per Name Week"; lines 72-73: presentation level 5 "Week", shows column "Per Name Week". The planted table goes from level 4 to level 6; the "Path:" line and "7 levels" above it still name Week. Levels are not required rows.

Could `check-doc` have caught it: No. Level rows are not in required_rows.csv (only the dimension and the presentation hierarchy are).

## 9. misquoted formula

- Part file: `as-is/sampleapp-planted/sample-sales.parts/009-table-07-dim-time.md`
- Packet: `slices/sampleapp/sample-sales/packets/table-07-dim-time.md`

Original text:

~~~~
| Per Name Half | `VALUEOF(NQ_SESSION.CN_Sample_Sales_Dim_Time_Per_Name_Half)` | [kb:sampleapp::pres::Sample Sales.Dim Time.Per Name Half] |
| Per Name Month | `VALUEOF(NQ_SESSION.CN_Sample_Sales_Dim_Time_Per_Name_Month)` | [kb:sampleapp::pres::Sample Sales.Dim Time.Per Name Month] |
| Per Name Qtr | `VALUEOF(NQ_SESSION.CN_Sample_Sales_Dim_Time_Per_Name_Qtr)` | [kb:sampleapp::pres::Sample Sales.Dim Time.Per Name Qtr] |
~~~~

Planted text:

~~~~
| Per Name Half | `VALUEOF(NQ_SESSION.CN_Sample_Sales_Dim_Time_Per_Name_Qtr)` | [kb:sampleapp::pres::Sample Sales.Dim Time.Per Name Half] |
| Per Name Month | `VALUEOF(NQ_SESSION.CN_Sample_Sales_Dim_Time_Per_Name_Month)` | [kb:sampleapp::pres::Sample Sales.Dim Time.Per Name Month] |
| Per Name Qtr | `VALUEOF(NQ_SESSION.CN_Sample_Sales_Dim_Time_Per_Name_Half)` | [kb:sampleapp::pres::Sample Sales.Dim Time.Per Name Qtr] |
~~~~

What the packet says: Line 25: display name of "Per Name Half" is `VALUEOF(NQ_SESSION.CN_Sample_Sales_Dim_Time_Per_Name_Half)`; line 37: display name of "Per Name Qtr" is `VALUEOF(NQ_SESSION.CN_Sample_Sales_Dim_Time_Per_Name_Qtr)`. The two quotes were swapped between the rows.

Could `check-doc` have caught it: No. Both quotes still occur character for character in the packet; the citations are unchanged.

## 10. misquoted formula

- Part file: `as-is/sampleapp-planted/sample-sales.parts/003-table-01-fact-revenue-p2.md`
- Packet: `slices/sampleapp/sample-sales/packets/table-01-fact-revenue-p2.md`

Original text:

~~~~
| Revenue (GBP) | `"xe".""."SAMPLESALES"."SAMP_REVENUE_F"."REVENUE" * 0.62` (the database column REVENUE multiplied by 0.62) | DOUBLE, precision 7 | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.SAMP_REVENUE_F#map14] [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F.REVENUE] |
| Revenue (EUR) | `"xe".""."SAMPLESALES"."SAMP_REVENUE_F"."REVENUE" * 0.76` (the database column REVENUE multiplied by 0.76) |
~~~~

Planted text:

~~~~
| Revenue (GBP) | `"xe".""."SAMPLESALES"."SAMP_REVENUE_F"."REVENUE" * 0.76` (the database column REVENUE multiplied by 0.76) | DOUBLE, precision 7 | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.SAMP_REVENUE_F#map14] [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F.REVENUE] |
| Revenue (EUR) | `"xe".""."SAMPLESALES"."SAMP_REVENUE_F"."REVENUE" * 0.62` (the database column REVENUE multiplied by 0.62) |
~~~~

What the packet says: Line 84: "Revenue (GBP)" is `"xe".""."SAMPLESALES"."SAMP_REVENUE_F"."REVENUE" * 0.62`; line 85: "Revenue (EUR)" is `... * 0.76`. The two formulas (and their plain-text glosses) were swapped between the rows. The part's own "Points to note about the sources" still lists GBP, EUR, INR with 0.62, 0.76, 54.68 in the correct order.

Could `check-doc` have caught it: No. Both quotes still occur in the packet.

## 11. bad open question (vague how-to)

- Part file: `as-is/sampleapp-planted/sample-sales.parts/990-open-questions.md`
- Packet: `slices/sampleapp/sample-sales/packets/table-01-fact-revenue-p1.md`

Original text:

~~~~
How to get the answer: in the OBIEE Administration Tool, open the measures "All Months Revenue" and "All Products Revenue" of the business table "Fact Revenue" and bring back everything recorded for each beyond the sum: any level they are fixed to, and the expression each source maps them from. Then build an analysis on "Sample Sales" with one field of "Dim Time", one field of "Dim Products", "Revenue", "All Months Revenue" and "All Products Revenue", and bring back the result.
~~~~

Planted text:

~~~~
How to get the answer: ask the business owner of the revenue reports what "All Months Revenue" and "All Products Revenue" are meant to show and how they differ from "Revenue".
~~~~

What the packet says: Not a packet fact: the question (Q5, raised in table-01-fact-revenue-p1) is still legitimate, but "How to get the answer" now names a role (the business owner) and no step, tool, object or query. The original gave concrete steps in the Administration Tool and an analysis to build.

Could `check-doc` have caught it: No. The line still starts with "How to get the answer:", so the question is well formed.

## 12. bad open question (packet answers it)

- Part file: `as-is/sampleapp-planted/sample-sales.parts/990-open-questions.md`
- Packet: `slices/sampleapp/sample-sales/packets/table-01-fact-revenue-p1.md`

Original text:

~~~~
### Q4. Is "DISCNT_VALUE" of Fact Revenue the same thing as the measure "Discnt Value", and how is it meant to be summarised?
Raised in: table-01-fact-revenue-p1
Why it matters: the business table holds "Discnt Value", a measure that is summed and is in the sources "Aggregate Revenue" and "SAMP_REVENUE_F", and "DISCNT_VALUE", recorded as not a measure, in the sources "Aggregate Revenue By Month" and "Aggregate Revenue by Year", and not shown to report authors. The same holds for "Bill Mth Key" and "BILL_MTH_KEY". Two readings are possible: the upper-case fields are left over and unused, or they carry the same numbers at month and year level and something relies on them.
Assumed meanwhile: nothing. Both fields of each pair are listed as recorded and are treated as separate fields.
How to get the answer: in the OBIEE Administration Tool, open the business table "Fact Revenue" and, for "DISCNT_VALUE", "BILL_MTH_KEY" and "BILL_YEAR_KEY", bring back the list of objects that use each (other fields, sources, tables that report authors see in any subject area). If nothing uses them, say so.
~~~~

Planted text:

~~~~
### Q4. Is "DISCNT_VALUE" of Fact Revenue a measure like "Discnt Value", and which of the five sources hold it?
Raised in: table-01-fact-revenue-p1
Why it matters: the business table holds "Discnt Value", a measure that is summed, and "DISCNT_VALUE", whose name differs only in letter case and underscore and which is not shown to report authors. The same holds for "Bill Mth Key" and "BILL_MTH_KEY". If "DISCNT_VALUE" is a measure too, it needs a rule for summarising it, and which sources hold it decides the level at which its numbers exist.
Assumed meanwhile: nothing. Both fields of each pair are listed as recorded and are treated as separate fields.
How to get the answer: in the OBIEE Administration Tool, open the business table "Fact Revenue", open "DISCNT_VALUE" and bring back whether an aggregation rule is set for it. Then open each of the five sources of the table and bring back the names of those whose column mapping lists "DISCNT_VALUE".
~~~~

What the packet says: Lines 44-46 answer the new question in full: Logical column "DISCNT_VALUE": not a measure; mapped in 2 of 5 table sources: "Aggregate Revenue By Month", "Aggregate Revenue by Year"; no aggregation rule line is given for it. The document's own part 002 states the same. The original question asked what the export cannot say (whether it is the same thing as "Discnt Value", and whether anything uses it).

Could `check-doc` have caught it: No. The question keeps its form (heading, Why it matters, How to get the answer, Answer).
