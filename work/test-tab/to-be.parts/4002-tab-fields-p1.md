### The tab's fields (part 1 of 2)

One row per confirmed item of the FSD, in the FSD's order. The model items named under "Supplied by" are proposals; they are designed in the sections of the model tables and in the FSD measures below. Items that are not confirmed are listed under Unresolved.

| Item | FSD anchor | FSD text | Supplied by | Confirmed column(s) | Evidence |
|---|---|---|---|---|---|
| F1 (filter) | t1.r2.c2 t1.r2.c3 | `Year: Calendar year of the order` | Model table Dim Time, field Per Name Year | "Dim Time"."Per Name Year" | [kb:item::F1] |
| F2 (filter) | t1.r3.c2 t1.r3.c3 | `Office: The sales office` | Model table Dim Offices, field Office Dsc | "Dim Offices"."Office Dsc" | [kb:item::F2] |
| F3 (filter) | t1.r4.c2 t1.r4.c3 | `Product brand: Brand of the product sold` | Model table Dim Products, field Brand | "Dim Products"."Brand" | [kb:item::F3] |
| K1 (calculation) | t2.r2.c2 t2.r2.c3 | `Net revenue: Revenue after discounts, for the filtered period` | FSD measure Net revenue | "Fact Revenue"."Revenue", "Fact Revenue"."Discnt Value" | [kb:item::K1] |
| K2 (measure) | t2.r3.c2 t2.r3.c3 | `Units sold: Number of units sold` | Measure Units | "Fact Revenue"."Units" | [kb:item::K2] |
| K3 (calculation) | t2.r4.c2 t2.r4.c3 | `Revenue vs target: Net revenue as a percentage of the revenue target for the same period` | FSD measure Revenue vs target | "Fact Revenue"."Revenue", "Fact Revenue"."Discnt Value", "Fact Target"."Revenue" | [kb:item::K3] |
| K4 (calculation) | t2.r5.c2 t2.r5.c3 | `Average selling price: Net revenue divided by units sold` | FSD measure Average selling price | "Fact Revenue"."Revenue", "Fact Revenue"."Discnt Value", "Fact Revenue"."Units" | [kb:item::K4] |
| V1-month (attribute) | p10 | `month of the year` | Model table Dim Time, field Per Name Month | "Dim Time"."Per Name Month" | [kb:item::V1-month] |
| V1-net-revenue (calculation) | p10 | `net revenue` | FSD measure Net revenue | "Fact Revenue"."Revenue", "Fact Revenue"."Discnt Value" | [kb:item::V1-net-revenue] |
| V1-target (measure) | p10 | `revenue target` | Measure Revenue Target | "Fact Target"."Revenue" | [kb:item::V1-target] |
| V2-product (attribute) | p11 | `product` | Model table Dim Products, field Prod Dsc | "Dim Products"."Prod Dsc" | [kb:item::V2-product] |
| V2-office (attribute) | p11 | `office` | Model table Dim Offices, field Office Dsc | "Dim Offices"."Office Dsc" | [kb:item::V2-office] |
| V2-net-revenue (calculation) | p11 | `net revenue` | FSD measure Net revenue | "Fact Revenue"."Revenue", "Fact Revenue"."Discnt Value" | [kb:item::V2-net-revenue] |
| V2-units (measure) | p11 | `units sold` | Measure Units | "Fact Revenue"."Units" | [kb:item::V2-units] |
| V2-discount (measure) | p11 | `discount amount` | Measure Discount Value | "Fact Revenue"."Discnt Value" | [kb:item::V2-discount] |
| V3-rep (attribute) | p12 | `sales representatives` | Model table Dim Sales Rep, field Employee Name | "Dim Sales Rep"."Employee Name" | [kb:item::V3-rep] |
| V3-net-revenue (calculation) | p12 | `highest net revenue` | FSD measure Net revenue | "Fact Revenue"."Revenue", "Fact Revenue"."Discnt Value" | [kb:item::V3-net-revenue] |

Items S1-own-offices [kb:item::S1-own-offices], F4 [kb:item::F4] and K5 [kb:item::K5] are not confirmed and are listed under Unresolved.

The decision note of F1 reads `TEST DECISION by the lead build agent, not by Kamal: kept for the test although the review showed revenue joins on the bill date` [kb:item::F1]. The decision note of every other confirmed item of this part reads `TEST DECISION by the lead build agent, not by Kamal` [kb:item::F2] [kb:item::F3] [kb:item::K1] [kb:item::K2] [kb:item::K3] [kb:item::K4] [kb:item::V1-month] [kb:item::V1-net-revenue] [kb:item::V1-target] [kb:item::V2-product] [kb:item::V2-office] [kb:item::V2-net-revenue] [kb:item::V2-units] [kb:item::V2-discount] [kb:item::V3-rep] [kb:item::V3-net-revenue].

Proposal (not from OBIEE; to be signed): for V3-net-revenue, the FSD measure Net revenue is used and the ranking by highest value is left to the visual.

#### FSD measure: Net revenue

- Supplies items K1, V1-net-revenue, V2-net-revenue and V3-net-revenue.
- The FSD's definition: `Net revenue: Revenue after discounts, for the filtered period` [kb:item::K1]
- Built from the confirmed columns "Fact Revenue"."Revenue" and "Fact Revenue"."Discnt Value", through the measures Revenue and Discount Value (section Model table Fact Revenue).
- The combination is the FSD's definition, not something OBIEE records. The FSD says "after discounts"; whether the OBIEE measure "Revenue" already has the discount taken off is not recorded (question Q1).
- Proposal (not from OBIEE; to be signed):

      Net revenue = [Revenue] - [Discount Value]

#### FSD measure: Revenue vs target

- Supplies item K3.
- The FSD's definition: `Revenue vs target: Net revenue as a percentage of the revenue target for the same period` [kb:item::K3]
- Built from the confirmed columns "Fact Revenue"."Revenue", "Fact Revenue"."Discnt Value" and "Fact Target"."Revenue", through the FSD measure Net revenue and the measure Revenue Target (section Model table Fact Target).
- The combination is the FSD's definition, not something OBIEE records. "For the same period" holds only if both fact tables are filtered by the same time field; at which level of time the targets are held is stated in the section Model table Fact Target.
- Proposal (not from OBIEE; to be signed), shown as a percentage:

      Revenue vs target = DIVIDE([Net revenue], [Revenue Target])

#### FSD measure: Average selling price

- Supplies item K4.
- The FSD's definition: `Average selling price: Net revenue divided by units sold` [kb:item::K4]
- Built from the confirmed columns "Fact Revenue"."Revenue", "Fact Revenue"."Discnt Value" and "Fact Revenue"."Units", through the FSD measure Net revenue and the measure Units.
- The combination is the FSD's definition, not something OBIEE records.
- Proposal (not from OBIEE; to be signed):

      Average selling price = DIVIDE([Net revenue], [Units])
