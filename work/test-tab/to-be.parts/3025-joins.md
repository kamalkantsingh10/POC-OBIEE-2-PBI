### Relationships

#### Between model tables

All seven business-model joins are inner joins. Each joins a dimension table, printed 0..1 or 1..1, to a fact table, printed 0..n or n..n. Whether these printed rows mean a plain one-to-many join, and whether fact rows with no matching dimension row must stay out as the inner join keeps them out, is decision Q19; the relationships below assume one to many until it is answered.

| From model table | Rows (as printed) | To model table | Rows (as printed) | Join type | Evidence |
|---|---|---|---|---|---|
| Dim Time | 0..1 | Fact Target | 0..n | Inner | [kb:sampleapp::bmm::joins.Relationship_2004:11061353928600] |
| Dim Time | 0..1 | Fact Revenue | 0..n | Inner | [kb:sampleapp::bmm::joins.Relationship_2004:11091353928666] |
| Dim Sales Rep | 1..1 | Fact Revenue | 0..n | Inner | [kb:sampleapp::bmm::joins.Relationship_2004:17511354089145] |
| Fact Revenue | n..n | Dim Offices | 0..1 | Inner | [kb:sampleapp::bmm::joins.Relationship_2004:2111353851711] |
| Fact Revenue | n..n | Dim Products | 0..1 | Inner | [kb:sampleapp::bmm::joins.Relationship_2004:2141353851711] |
| Fact Target | n..n | Dim Offices | 0..1 | Inner | [kb:sampleapp::bmm::joins.Relationship_2004:2301353851711] |
| Fact Target | n..n | Dim Products | 0..1 | Inner | [kb:sampleapp::bmm::joins.Relationship_2004:2331353851711] |

The business-model joins name no columns; the columns below come from the foreign keys in the next table and from the sections of the model tables.

Proposal (not from OBIEE; to be signed): Dim Offices to Fact Revenue, one to many, on Dim Offices "Office Key" = Fact Revenue "Office Key" (SAMP_REVENUE_F.OFFICE_KEY), single filter direction from Dim Offices to Fact Revenue.

Proposal (not from OBIEE; to be signed): Dim Products to Fact Revenue, one to many, on Dim Products "Prod Key" = Fact Revenue "Prod Key" (SAMP_REVENUE_F.PROD_KEY), single filter direction from Dim Products to Fact Revenue.

Proposal (not from OBIEE; to be signed): Dim Offices to Fact Target, one to many, on Dim Offices "Office Key" = Fact Target "Office Key" (SAMP_TARGETS_F.OFFICE_KEY), single filter direction from Dim Offices to Fact Target.

Proposal (not from OBIEE; to be signed): Dim Products to Fact Target, one to many, on Dim Products "Prod Key" = Fact Target "Prod Key" (SAMP_TARGETS_F.PROD_KEY), single filter direction from Dim Products to Fact Target.

Proposal (not from OBIEE; to be signed): Dim Time to Fact Revenue, one to many, on Dim Time "Calendar Date" = Fact Revenue "Bill Day Dt" (SAMP_REVENUE_F.BILL_DAY_DT), single filter direction from Dim Time to Fact Revenue. The FSD asks for the calendar year of the order (item F1), while OBIEE relates revenue to time on the bill date: decision Q15.

Proposal (not from OBIEE; to be signed): Dim Sales Rep to Fact Revenue, one to many, on Dim Sales Rep "Employee ID" = Fact Revenue "Sales Employee Key" (SAMP_REVENUE_F.EMPLOYEE_KEY), single filter direction from Dim Sales Rep to Fact Revenue. In the database the foreign key goes to EMP_PARENT_CHILD.MEMBER_KEY, not to EMPLOYEE.EMP_ID, so this proposal holds only once decisions Q7 and Q10 are answered.

Dim Time to Fact Target: no relationship is proposed. Targets carry only a month key, BILL_MTH_KEY, which the foreign key relates to SAMP_TIME_MTH_D.MTH_KEY; the model table Dim Time has the day as its key. This is decision Q14.

#### Between new tables (proposed foreign keys)

| Kind | Name | From new table.column | To new table.column | Evidence |
|---|---|---|---|---|
| foreign key | EMP_PARENT_CHILD_FKey | EMP_PARENT_CHILD.ANCESTOR_KEY | EMPLOYEE.EMP_ID | [kb:sampleapp::phys::xe.SAMPLESALES.EMP_PARENT_CHILD.EMP_PARENT_CHILD_FKey] |
| foreign key | SAMP_PRODUCTS_D_TRANSLATION_FKey | SAMP_PRODUCTS_D_TRANSLATION.PROD_KEY | SAMP_PRODUCTS_D.PROD_KEY | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_PRODUCTS_D_TRANSLATION.SAMP_PRODUCTS_D_TRANSLATION_FKey] |
| foreign key | SAMP_REVENUE_A_FKey | SAMP_REVENUE_A.BILL_MTH_KEY | SAMP_TIME_MTH_D.MTH_KEY | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A.SAMP_REVENUE_A_FKey] |
| foreign key | SAMP_REVENUE_A_FKey#1 | SAMP_REVENUE_A.PROD_KEY | SAMP_PRODUCTS_D.PROD_KEY | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A.SAMP_REVENUE_A_FKey#1] |
| foreign key | SAMP_REVENUE_A_MTH_Foreign Key | SAMP_REVENUE_A_MTH.BILL_MTH_KEY | SAMP_TIME_MTH_D.MTH_KEY | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A_MTH.SAMP_REVENUE_A_MTH_Foreign Key] |
| foreign key | SAMP_REVENUE_A_YR_Foreign Key | SAMP_REVENUE_A_YR.BILL_YEAR_KEY | SAMP_TIME_MTH_D.CAL_YEAR | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A_YR.SAMP_REVENUE_A_YR_Foreign Key] |
| foreign key | SAMP_REVENUE_F_FKey | SAMP_REVENUE_F.BILL_MTH_KEY | SAMP_TIME_MTH_D.MTH_KEY | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F.SAMP_REVENUE_F_FKey] |
| foreign key | SAMP_REVENUE_F_FKey#1 | SAMP_REVENUE_F.OFFICE_KEY | SAMP_OFFICES_D.OFFICE_KEY | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F.SAMP_REVENUE_F_FKey#1] |
| foreign key | SAMP_REVENUE_F_FKey#2 | SAMP_REVENUE_F.PROD_KEY | SAMP_PRODUCTS_D.PROD_KEY | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F.SAMP_REVENUE_F_FKey#2] |
| foreign key | SAMP_REVENUE_F_FKey#3 | SAMP_REVENUE_F.BILL_DAY_DT | SAMP_TIME_DAY_D.CALENDAR_DATE | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F.SAMP_REVENUE_F_FKey#3] |
| foreign key | SAMP_REVENUE_F_FKey#4 | SAMP_REVENUE_F.EMPLOYEE_KEY | EMP_PARENT_CHILD.MEMBER_KEY | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F.SAMP_REVENUE_F_FKey#4] |
| foreign key | SAMP_TARGETS_F_FKey | SAMP_TARGETS_F.BILL_MTH_KEY | SAMP_TIME_MTH_D.MTH_KEY | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TARGETS_F.SAMP_TARGETS_F_FKey] |
| foreign key | SAMP_TARGETS_F_FKey#1 | SAMP_TARGETS_F.OFFICE_KEY | SAMP_OFFICES_D.OFFICE_KEY | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TARGETS_F.SAMP_TARGETS_F_FKey#1] |
| foreign key | SAMP_TARGETS_F_FKey#2 | SAMP_TARGETS_F.PROD_KEY | SAMP_PRODUCTS_D.PROD_KEY | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_TARGETS_F.SAMP_TARGETS_F_FKey#2] |

Proposal (not from OBIEE; to be signed): each of these 14 foreign keys is kept as a foreign key between the new tables. In SAMP_REVENUE_F_FKey#4 the two columns differ in type as recorded: EMPLOYEE_KEY is INT and MEMBER_KEY is DOUBLE, precision 15 [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_F.SAMP_REVENUE_F_FKey#4]; in SAMP_REVENUE_A_MTH_Foreign Key BILL_MTH_KEY has precision 22 and MTH_KEY precision 15 [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A_MTH.SAMP_REVENUE_A_MTH_Foreign Key].

#### Complex joins with a condition

| Kind | Name | From new table | To new table | Cardinality as printed | Condition, exactly | Evidence |
|---|---|---|---|---|---|---|
| complex join | Complex Join | FactCrossJoin | SAMP_OFFICES_D | `ONE TO ONE` | `1=1` | [kb:sampleapp::phys::joins.Complex Join] |
| complex join | Complex Join#1 | FactCrossJoin | SAMP_PRODUCTS_D | `ONE TO ONE` | `1=1` | [kb:sampleapp::phys::joins.Complex Join#1] |
| complex join | Complex Join#2 | FactCrossJoin | EMPLOYEE | `ONE TO ONE` | `1=1` | [kb:sampleapp::phys::joins.Complex Join#2] |

A condition of 1=1 pairs every row with every row and has no relationship form in a model: decision Q16. No item of the tab uses FactCrossJoin (decision Q11).
