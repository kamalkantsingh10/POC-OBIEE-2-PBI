# Scoring a match, and wording its basis

The trace skill scores with this page; the review checks scores against it. The bands are fixed by `docs/skill-conventions.md`, section 10.6, and `check-matches` fails a score outside its band.

## The bands

| Band | Means | Evidence that earns it |
|---|---|---|
| 90-100 | Almost certainly this column | The FSD names the subject area and the column exactly; or a report of the page the tab replaces uses this column for the same figure; or a recorded answer of the user says so |
| 60-89 | Probably this column | A close name **and** something more that fits: the way it is summarised (a sum for an amount, a count for "number of"), the table it sits in (actuals where the FSD speaks of sales, targets where it speaks of plan), a formula that does what the FSD's definition says, or the as-is document's description of it |
| 30-59 | Plausible, needs the Operator | A name only; or a fitting column where another fits about as well; or a column whose meaning the FSD's definition leaves open |
| 0-29 | No column | `unmatched`, empty `row_id`. Never the nearest name to fill the gap |

A `proposed` row scores 30 or more; an `unmatched` row less than 30.

## Choosing the number inside a band

Start from the evidence, not from how much you would like the item matched.

- **Evidence from an existing report outranks a name.** If the replaced page's report uses a column for this figure, that column scores 90 or more even if its name is odd; a column the page does not use, however well named, stays below 90.
- **A name alone never passes 59.** "Whole name" in the output of `find-columns` says the words agree. It says nothing about meaning.
- **Each further piece of evidence that fits moves the score up within the band; each thing left open moves it down.** Within 60-89: the name and the way it is summarised fit, 65 to 75; the table fits as well, 75 to 85; the as-is document or a formula also says what the FSD's definition says, up to 89.
- **Two candidates about equally good**: the one you propose scores at most 59, and the other is named in the basis.
- **A piece of a calculation** is scored for itself: how sure you are that this column is the part the basis says it is. If the way the pieces combine is your reading of the FSD and not something OBIEE records, no piece scores above 79, and the basis says "computed as …, not recorded in OBIEE".
- **A column in another subject area** than the tab's: at most 59, and the basis names the subject area.
- **A column that a formula computes** may be proposed; quote the formula in the basis.
- **Unmatched**: 0 when the lookups returned nothing; 10 to 29 when they returned candidates that you judged not to fit. The basis names the nearest and says why not.

## The basis

One line, written so that a reviewer who runs the same lookups can check every word of it. It holds, in this order, separated by "; ":

1. `searched "<term>"` for each lookup you ran for the item, with `--formula-contains "<text>"` when you used it, and `no subject area` when you searched the whole estate;
2. what the lookup showed about the proposed column: the group and words (`all words: product, brand`), whether it is a measure and how it is summarised, its table, its formula if it has one, the report column that uses it;
3. anything else you used: `as-is section "<heading>" (reviewed)` or `(draft)` and what it says; `answer Q<n>`;
4. for a piece of a calculation: how the item is computed and which piece this row is;
5. the runners-up, by table and column, and why they lost;
6. what is left open, with the question number.

For `unmatched`: the terms searched, what came back, and why none fits.

Do not write into a basis anything the lookups, the as-is section or an answer did not show. "Standard revenue measure" or "commonly used" is not evidence.

## Examples

| Item | Row and score | Basis |
|---|---|---|
| F3 Product brand: "Brand of the product sold" | `Dim Products.Brand`, 80 | searched "product brand"; all words: product, brand; not a measure; table Dim Products, column Brand; runner-up Dim Products.Brand Key, a key and not the name |
| F4 Region: "Sales region the office belongs to" | unmatched, 15 | searched "region", "sales region", "area", "territory"; no column of Sample Sales has the word region; nearest is Dim Offices.Area Name, and nothing shows that an area is a sales region (question Q3); "region" with no subject area finds only the table Region of Population, which holds cities and states |
| K1 Net revenue, first piece | `Fact Revenue.Revenue`, 70 | searched "net revenue", "revenue", "discount"; some words: revenue, not found: net; measure, SUM, table Fact Revenue; net revenue = Revenue minus Discnt Value, computed so from the FSD's definition "revenue after discounts", not recorded in OBIEE; this row is the Revenue part; runner-up Fact Target.Revenue is the target, All Months Revenue and All Products Revenue read the same database column; whether Revenue is already net is open (question Q2) |
