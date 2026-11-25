# The tab's fields: each FSD item and its confirmed columns (part 2 of 2)
Unit: tab-fields-p2
Kind: tab_fields
Tab: test-tab (subject areas: Sample Sales; estate sampleapp)
Facts from: tab_fields
Rows of this packet a document must cover: 1. Each is on a line that ends with (REQUIRED).
To cite a fact, copy the marker in square brackets that follows it, exactly as written. "->" points at another row.
This is part 2 of 2 of what was too long for one packet (the parts are tab-fields-p1 to tab-fields-p2). Each part is a unit of its own: write this one as its own section, from this packet alone.

## FSD items

- Item N1-currency (attribute), FSD anchor p14: CONFIRMED [kb:item::N1-currency] (REQUIRED)
    - FSD text: `Amounts are shown in the user's preferred currency where the source supports it.` [kb:item::N1-currency]
    - Column: presentation column "Fact Revenue"."Preferred Currency", logical column "Fact Revenue"."Preferred Currency", not a measure (unit table-01-fact-revenue) -> sampleapp::pres::Sample Sales.Fact Revenue.Preferred Currency
    - Decision note: `TEST DECISION by the lead build agent, not by Kamal` [kb:item::N1-currency]

