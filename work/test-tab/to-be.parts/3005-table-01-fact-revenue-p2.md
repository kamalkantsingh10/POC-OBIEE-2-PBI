### Model table Fact Revenue (part 2 of 2)

#### Where it reads from

"Fact Revenue" has five sources, all active. None of them joins tables: each reads one database table.

| Source | Active | Priority group | Level of detail, exactly | Reads | Evidence |
|---|---|---|---|---|---|
| Aggregate Revenue | true | `1` | ` GROUPBYLEVEL("SAMPLESALES"."Time Hierarchy"."Month", "SAMPLESALES"."Products Hierararchy"."Product Detail")` | new table SAMP_REVENUE_A (old "SAMP_REVENUE_A" in `xe.SAMPLESALES`, type `none`) [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue#table0] | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue] |
| Aggregate Revenue By Month | true | not recorded (empty; it does not mean lowest) | ` GROUPBYLEVEL("SAMPLESALES"."Time Hierarchy"."Month")` | new table SAMP_REVENUE_A_MTH (old "SAMP_REVENUE_A_MTH" in `xe.SAMPLESALES`, type `none`) [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue By Month#table0] | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue By Month] |
| Aggregate Revenue by Year | true | not recorded (empty; it does not mean lowest) | ` GROUPBYLEVEL("SAMPLESALES"."Time Hierarchy"."Year")` | new table SAMP_REVENUE_A_YR (old "SAMP_REVENUE_A_YR" in `xe.SAMPLESALES`, type `none`) [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue by Year#table0] | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue by Year] |
| FactCrossJoin | true | not recorded (empty; it does not mean lowest) | not recorded; the repository gives this source no level of detail | new view FactCrossJoin (old "FactCrossJoin" in `xe.SAMPLESALES`, type `select`, a query kept in the repository) [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.FactCrossJoin#table0] | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.FactCrossJoin] |
| SAMP_REVENUE_F | true | not recorded (empty; it does not mean lowest) | ` GROUPBYLEVEL("SAMPLESALES"."Sales Rep Hierarchy"."Detail", "SAMPLESALES"."Time Hierarchy"."Dim Time Detail", "SAMPLESALES"."Office Hierarchy"."Office Detail", "SAMPLESALES"."Products Hierararchy"."Product Detail")` | new table SAMP_REVENUE_F (old "SAMP_REVENUE_F" in `xe.SAMPLESALES`, type `none`) [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.SAMP_REVENUE_F#table0] | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.SAMP_REVENUE_F] |

Four sources at different levels of detail feed the same measures: this is decision Q3. "FactCrossJoin" maps only "Preserve", which the tab does not use [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.FactCrossJoin#map0]; whether its query is kept is decided in the section New view FactCrossJoin.

#### What each source maps the confirmed measures from

| Measure | Source | Mapped from, exactly | Physical column as printed | Evidence |
|---|---|---|---|---|
| Revenue | Aggregate Revenue | `"xe".""."SAMPLESALES"."SAMP_REVENUE_A"."REVENUE"` | type DOUBLE, precision 8, scale 2, nullable true | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue#map3] |
| Revenue | Aggregate Revenue By Month | `"xe".""."SAMPLESALES"."SAMP_REVENUE_A_MTH"."REVENUE"` | type DOUBLE, precision 22, nullable true | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue By Month#map0] |
| Revenue | Aggregate Revenue by Year | `"xe".""."SAMPLESALES"."SAMP_REVENUE_A_YR"."REVENUE"` | type DOUBLE, precision 22, nullable true | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue by Year#map0] |
| Revenue | SAMP_REVENUE_F | `"xe".""."SAMPLESALES"."SAMP_REVENUE_F"."REVENUE"` | type DOUBLE, precision 7, scale 2, nullable true | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.SAMP_REVENUE_F#map9] |
| Discount Value ("Discnt Value") | Aggregate Revenue | `"xe".""."SAMPLESALES"."SAMP_REVENUE_A"."DISCNT_VALUE"` | type DOUBLE, precision 7, scale 2, nullable true | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue#map1] |
| Discount Value ("Discnt Value") | SAMP_REVENUE_F | `"xe".""."SAMPLESALES"."SAMP_REVENUE_F"."DISCNT_VALUE"` | type DOUBLE, precision 6, scale 2, nullable true | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.SAMP_REVENUE_F#map2] |
| Units | Aggregate Revenue | `"xe".""."SAMPLESALES"."SAMP_REVENUE_A"."UNITS"` | type DOUBLE, precision 15, nullable true | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue#map4] |
| Units | Aggregate Revenue By Month | `"xe".""."SAMPLESALES"."SAMP_REVENUE_A_MTH"."UNITS"` | type DOUBLE, precision 22, nullable true | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue By Month#map1] |
| Units | Aggregate Revenue by Year | `"xe".""."SAMPLESALES"."SAMP_REVENUE_A_YR"."UNITS"` | type DOUBLE, precision 22, nullable true | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue by Year#map1] |
| Units | SAMP_REVENUE_F | `"xe".""."SAMPLESALES"."SAMP_REVENUE_F"."UNITS"` | type DOUBLE, precision 15, nullable true | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.SAMP_REVENUE_F#map10] |

The measure "Discnt Value" is not mapped in "Aggregate Revenue By Month" or "Aggregate Revenue by Year". Those two sources map the column DISCNT_VALUE to a separate field "DISCNT_VALUE", which is not a measure ([kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue By Month#map3], [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue by Year#map2]). So if the summary tables are kept (decision Q3), Net revenue cannot be taken from them as mapped today.

#### The currency measures the switch "Preferred Currency" needs

"Revenue (EUR)" and "Revenue (GBP)" are mapped differently in the two sources that map them. In "Aggregate Revenue" both are the column REVENUE as it is: `"xe".""."SAMPLESALES"."SAMP_REVENUE_A"."REVENUE"` [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue#map8] (EUR) and [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue#map7] (GBP). In "SAMP_REVENUE_F" each is multiplied by a fixed number: `"xe".""."SAMPLESALES"."SAMP_REVENUE_F"."REVENUE" * 0.76` [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.SAMP_REVENUE_F#map15] and `"xe".""."SAMPLESALES"."SAMP_REVENUE_F"."REVENUE" * 0.62` [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.SAMP_REVENUE_F#map14]. These matter only if decision Q2 keeps the currency switch; they are decisions Q4 and Q5. See also as-is document, question Q4.

#### Columns the relationships need

The detail source "SAMP_REVENUE_F" maps the fields that link to the dimension tables. They are not confirmed for the tab and are not offered as fields, but the model table keeps the columns the relationships need (see Relationships).

| Field | Mapped from, exactly | Physical column as printed | Evidence |
|---|---|---|---|
| Bill Day Dt | `"xe".""."SAMPLESALES"."SAMP_REVENUE_F"."BILL_DAY_DT"` | type DATE, nullable true | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.SAMP_REVENUE_F#map0] |
| Bill Mth Key | `"xe".""."SAMPLESALES"."SAMP_REVENUE_F"."BILL_MTH_KEY"` | type DOUBLE, precision 15, nullable true | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.SAMP_REVENUE_F#map1] |
| Office Key | `"xe".""."SAMPLESALES"."SAMP_REVENUE_F"."OFFICE_KEY"` | type DOUBLE, precision 15, nullable true | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.SAMP_REVENUE_F#map3] |
| Prod Key | `"xe".""."SAMPLESALES"."SAMP_REVENUE_F"."PROD_KEY"` | type DOUBLE, precision 15, nullable true | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.SAMP_REVENUE_F#map8] |
| Sales Employee Key | `"xe".""."SAMPLESALES"."SAMP_REVENUE_F"."EMPLOYEE_KEY"` | type INT, nullable not stated | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.SAMP_REVENUE_F#map13] |
| Order Day Dt | `"xe".""."SAMPLESALES"."SAMP_REVENUE_F"."ORDER_DAY_DT"` | type DATETIME, nullable true | [kb:sampleapp::bmm::SAMPLESALES.Fact Revenue.src.SAMP_REVENUE_F#map4] |

The other mappings of this packet (in "Aggregate Revenue": "Bill Mth Key", "Prod Key", "All Products Revenue", "All Months Revenue", "Revenue (INR)"; in the month and year sources: "BILL_MTH_KEY", "BILL_YEAR_KEY"; in "SAMP_REVENUE_F": "All Products Revenue", "All Months Revenue", "Revenue (INR)", "Order Number", "Order Status", "Order Type") are of fields the tab does not use.

Which of the new tables SAMP_REVENUE_A, SAMP_REVENUE_A_MTH, SAMP_REVENUE_A_YR and SAMP_REVENUE_F the model table reads is not chosen here: it is decision Q3. The measures of part 1 are defined over the model table Fact Revenue whichever tables feed it.
