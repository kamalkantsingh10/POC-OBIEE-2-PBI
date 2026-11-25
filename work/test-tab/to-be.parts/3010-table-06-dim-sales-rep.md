### Model table Dim Sales Rep

Proposal (not from OBIEE; to be signed): a table of the Power BI model named "Dim Sales Rep", like the business table, read from the new table EMPLOYEE and, depending on decision Q7, the new table EMP_PARENT_CHILD.

The business table "Dim Sales Rep" is in business model "SAMPLESALES", with 1 table source declared. [kb:sampleapp::bmm::SAMPLESALES.Dim Sales Rep] It has 5 columns in this slice, none of them measures; 4 are not confirmed for the tab. The tab uses one field, "Employee Name".

#### Keys

Key "EMP_ID": marked as primary key true; made of column(s) "Employee ID". [kb:sampleapp::bmm::SAMPLESALES.Dim Sales Rep.key.EMP_ID]

#### Where it reads from

| Source | Active | Priority group | Level of detail, exactly | Reads | Evidence |
|---|---|---|---|---|---|
| LTS Employee | true | not recorded (empty; it does not mean lowest) | ` GROUPBYLEVEL("SAMPLESALES"."Sales Rep Hierarchy"."Detail")` | new table EMPLOYEE (old "EMPLOYEE" in `xe.SAMPLESALES`, type `none`) [kb:sampleapp::bmm::SAMPLESALES.Dim Sales Rep.src.LTS Employee#table0] and new table EMP_PARENT_CHILD (old "EMP_PARENT_CHILD" in `xe.SAMPLESALES`, type `none`) [kb:sampleapp::bmm::SAMPLESALES.Dim Sales Rep.src.LTS Employee#table1] | [kb:sampleapp::bmm::SAMPLESALES.Dim Sales Rep.src.LTS Employee] |

The source names "EMP_PARENT_CHILD" as the table that holds the parent-child relationships (its closure table); the drill path is set out under Hierarchies.

The source joins its two tables:

| Listed under | Joined to | Join type | Cardinality as printed | On | Evidence |
|---|---|---|---|---|---|
| EMPLOYEE | EMP_PARENT_CHILD | `Inner` | ` ONE TO MANY` | foreign key "EMP_PARENT_CHILD_FKey": "EMP_PARENT_CHILD" ("ANCESTOR_KEY") = "EMPLOYEE" ("EMP_ID") | [kb:sampleapp::bmm::SAMPLESALES.Dim Sales Rep.src.LTS Employee#table0.join0] |
| EMP_PARENT_CHILD | EMPLOYEE | `Inner` | `MANY TO ONE` | foreign key "EMP_PARENT_CHILD_FKey": "EMP_PARENT_CHILD" ("ANCESTOR_KEY") = "EMPLOYEE" ("EMP_ID") | [kb:sampleapp::bmm::SAMPLESALES.Dim Sales Rep.src.LTS Employee#table1.join0] |

The two lines are one join, listed once under each table. A join inside a source is a decision: Q7. As joined, one employee can give several rows, one per row of EMP_PARENT_CHILD whose ANCESTOR_KEY is that employee [kb:sampleapp::bmm::SAMPLESALES.Dim Sales Rep.src.LTS Employee#table0.join0]; see also as-is document, question Q9.

#### Fields carried into the model

| Field | From new table.column | Type, precision, nullable as the packet prints them | Tab items | Evidence |
|---|---|---|---|---|
| Employee Name | EMPLOYEE.EMP_NAME, mapped as `"xe".""."SAMPLESALES"."EMPLOYEE"."EMP_NAME"` | type VARCHAR, precision 40, nullable true | V3-rep | [kb:sampleapp::bmm::SAMPLESALES.Dim Sales Rep.src.LTS Employee#map1] |

"Employee Name" is shown to report authors as "Employee Name" in presentation table "Dim Sales Rep"; name set by hand: false; confirmed for the tab, item V3-rep. [kb:sampleapp::pres::Sample Sales.Dim Sales Rep.Employee Name]

The 4 fields not confirmed ("Distance", "Employee ID", "Manager ID", "Office ID") are not carried as fields. "Employee ID" is the key field: the relationships need its column, mapped as `"xe".""."SAMPLESALES"."EMPLOYEE"."EMP_ID"` (type DOUBLE, precision 15, nullable not stated) [kb:sampleapp::bmm::SAMPLESALES.Dim Sales Rep.src.LTS Employee#map0]; see Relationships. "Distance" is the one field read from EMP_PARENT_CHILD, as `"xe".""."SAMPLESALES"."EMP_PARENT_CHILD"."DISTANCE"` [kb:sampleapp::bmm::SAMPLESALES.Dim Sales Rep.src.LTS Employee#map4]; the tab does not use it.

#### Measures

None: the table holds no measure.
