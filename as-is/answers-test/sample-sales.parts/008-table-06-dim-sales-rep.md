## Dim Sales Rep

"Dim Sales Rep" is a business table of the business model "SAMPLESALES"; the repository declares 1 source for it [kb:sampleapp::bmm::SAMPLESALES.Dim Sales Rep]. In this subject area it has 5 fields, none of them measures, and all 5 are shown to report authors (counted). Its one source is "LTS Employee" (counted).

### Keys

| Key | Primary key (as recorded) | Fields | Evidence |
|---|---|---|---|
| EMP_ID | true | Employee ID | [kb:sampleapp::bmm::SAMPLESALES.Dim Sales Rep.key.EMP_ID] |

### Measures

This table has no measures.

### Fields

| Field | In sources | Evidence |
|---|---|---|
| Distance | 1 of 1: "LTS Employee" | [kb:sampleapp::bmm::SAMPLESALES.Dim Sales Rep.Distance] [kb:sampleapp::pres::Sample Sales.Dim Sales Rep.Distance] |
| Employee ID | 1 of 1: "LTS Employee" | [kb:sampleapp::bmm::SAMPLESALES.Dim Sales Rep.Employee ID] [kb:sampleapp::pres::Sample Sales.Dim Sales Rep.Employee ID] |
| Employee Name | 1 of 1: "LTS Employee" | [kb:sampleapp::bmm::SAMPLESALES.Dim Sales Rep.Employee Name] [kb:sampleapp::pres::Sample Sales.Dim Sales Rep.Employee Name] |
| Manager ID | 1 of 1: "LTS Employee" | [kb:sampleapp::bmm::SAMPLESALES.Dim Sales Rep.Manager ID] [kb:sampleapp::pres::Sample Sales.Dim Sales Rep.Manager ID] |
| Office ID | 1 of 1: "LTS Employee" | [kb:sampleapp::bmm::SAMPLESALES.Dim Sales Rep.Office ID] [kb:sampleapp::pres::Sample Sales.Dim Sales Rep.Office ID] |

### Measures and fields report authors do not see

Every field of this table is shown to report authors (counted).

### Where the data comes from

#### Source "LTS Employee"

Active: true. Priority group not recorded (empty; it does not mean lowest) [kb:sampleapp::bmm::SAMPLESALES.Dim Sales Rep.src.LTS Employee]. Level of detail, exactly: `GROUPBYLEVEL("SAMPLESALES"."Sales Rep Hierarchy"."Detail")`, the level "Detail" of the drill path "Sales Rep Hierarchy" [kb:sampleapp::bmm::SAMPLESALES.Dim Sales Rep.src.LTS Employee]. This source names the database table EMP_PARENT_CHILD as the table that holds the parent-child relationships (its closure table); see the section Database table EMP_PARENT_CHILD for its member, parent, distance and leaf columns [kb:sampleapp::bmm::SAMPLESALES.Dim Sales Rep.src.LTS Employee].

No content filter is recorded for this source.

| Database table | Schema | Type | Evidence |
|---|---|---|---|
| EMPLOYEE (see the section Database table EMPLOYEE) | `xe.SAMPLESALES` | `none` | [kb:sampleapp::bmm::SAMPLESALES.Dim Sales Rep.src.LTS Employee#table0] |
| EMP_PARENT_CHILD (see the section Database table EMP_PARENT_CHILD) | `xe.SAMPLESALES` | `none` | [kb:sampleapp::bmm::SAMPLESALES.Dim Sales Rep.src.LTS Employee#table1] |

The source joins its two tables; the repository lists the one join once under each table:

| Listed under | From | To | Join type | Cardinality | On | Evidence |
|---|---|---|---|---|---|---|
| EMPLOYEE | EMPLOYEE | EMP_PARENT_CHILD | `Inner` | `ONE TO MANY` | foreign key "EMP_PARENT_CHILD_FKey": EMP_PARENT_CHILD.ANCESTOR_KEY = EMPLOYEE.EMP_ID | [kb:sampleapp::bmm::SAMPLESALES.Dim Sales Rep.src.LTS Employee#table0.join0] |
| EMP_PARENT_CHILD | EMP_PARENT_CHILD | EMPLOYEE | `Inner` | `MANY TO ONE` | foreign key "EMP_PARENT_CHILD_FKey": EMP_PARENT_CHILD.ANCESTOR_KEY = EMPLOYEE.EMP_ID | [kb:sampleapp::bmm::SAMPLESALES.Dim Sales Rep.src.LTS Employee#table1.join0] |

| Field | Database column | Type, precision, nullable | Evidence |
|---|---|---|---|
| Employee ID | EMPLOYEE.EMP_ID | type DOUBLE, precision 15, nullable not stated | [kb:sampleapp::bmm::SAMPLESALES.Dim Sales Rep.src.LTS Employee#map0] |
| Employee Name | EMPLOYEE.EMP_NAME | type VARCHAR, precision 40, nullable true | [kb:sampleapp::bmm::SAMPLESALES.Dim Sales Rep.src.LTS Employee#map1] |
| Manager ID | EMPLOYEE.MANAGER_ID | type DOUBLE, precision 15, nullable true | [kb:sampleapp::bmm::SAMPLESALES.Dim Sales Rep.src.LTS Employee#map2] |
| Office ID | EMPLOYEE.OFFICE_ID | type DOUBLE, precision 15, nullable true | [kb:sampleapp::bmm::SAMPLESALES.Dim Sales Rep.src.LTS Employee#map3] |
| Distance | EMP_PARENT_CHILD.DISTANCE | type INT, nullable not stated | [kb:sampleapp::bmm::SAMPLESALES.Dim Sales Rep.src.LTS Employee#map4] |

### Points to note

- The source joins EMPLOYEE to the closure table EMP_PARENT_CHILD on EMP_PARENT_CHILD.ANCESTOR_KEY = EMPLOYEE.EMP_ID, inner, one to many [kb:sampleapp::bmm::SAMPLESALES.Dim Sales Rep.src.LTS Employee#table0.join0], while the key of the business table is "Employee ID" alone [kb:sampleapp::bmm::SAMPLESALES.Dim Sales Rep.key.EMP_ID]. What one row stands for, and what "Distance" holds, is not recorded (open question Q9).
