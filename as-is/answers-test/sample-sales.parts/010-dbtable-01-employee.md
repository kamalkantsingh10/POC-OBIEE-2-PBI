## Database table EMPLOYEE

EMPLOYEE is a database table in `xe.SAMPLESALES`, type `none`; it is not an alias of another table, and no description is recorded [kb:sampleapp::phys::xe.SAMPLESALES.EMPLOYEE]. No separate object name in the database is recorded, so the repository knows it only by the name EMPLOYEE (listed in the packet without a Row identifier). The export lists 4 columns for it, all 4 mapped by a field of this subject area and none not mapped, and 1 key (counted).

### Columns

| # | Column | Type | Precision | Scale | Nullable | External name | Mapped by | Evidence |
|---|---|---|---|---|---|---|---|---|
| 1 | EMP_ID | DOUBLE | 15 | not recorded | not stated | `samplesales` | Dim Sales Rep, Employee ID | [kb:sampleapp::phys::xe.SAMPLESALES.EMPLOYEE.EMP_ID] |
| 2 | EMP_NAME | VARCHAR | 40 | not recorded | true | `samplesales` | Dim Sales Rep, Employee Name | [kb:sampleapp::phys::xe.SAMPLESALES.EMPLOYEE.EMP_NAME] |
| 3 | MANAGER_ID | DOUBLE | 15 | not recorded | true | `samplesales` | Dim Sales Rep, Manager ID | [kb:sampleapp::phys::xe.SAMPLESALES.EMPLOYEE.MANAGER_ID] |
| 4 | OFFICE_ID | DOUBLE | 15 | not recorded | true | `samplesales` | Dim Sales Rep, Office ID | [kb:sampleapp::phys::xe.SAMPLESALES.EMPLOYEE.OFFICE_ID] |

Nullable: the export records this only when it is true. "Not stated" means the export does not carry it; it is not a statement that the column must be filled. "Not recorded" means the repository says nothing; it does not mean no, and it does not mean 0.

### Keys

| Key | Columns | Evidence |
|---|---|---|
| EMP_ID | EMP_ID | [kb:sampleapp::phys::xe.SAMPLESALES.EMPLOYEE.key.EMP_ID] |

The repository does not say whether a physical key is the primary key; this is the only key it declares for the table. One foreign key of this subject area points at exactly EMP_ID [kb:sampleapp::phys::xe.SAMPLESALES.EMPLOYEE.key.EMP_ID].

### Foreign keys and joins

- The complex join "Complex Join#2" between FactCrossJoin and this table; its condition is stated in full in the section How the tables relate.
- The foreign key "EMP_PARENT_CHILD_FKey" from EMP_PARENT_CHILD (column ANCESTOR_KEY) to this table (column EMP_ID); stated in full in the section How the tables relate.

### Read by

The source "LTS Employee" of the business table Dim Sales Rep (listed in the packet without a Row identifier).
