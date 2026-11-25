## Database table EMP_PARENT_CHILD

EMP_PARENT_CHILD is a database table in `xe.SAMPLESALES`, type `none`; it is not an alias of another table, and no description is recorded [kb:sampleapp::phys::xe.SAMPLESALES.EMP_PARENT_CHILD]. Its object in the database, for database type `DefaultMulDB`, is `EMP_PARENT_CHILD` [kb:sampleapp::phys::xe.SAMPLESALES.EMP_PARENT_CHILD.def.DefaultMulDB]. The export lists 4 columns for it, 1 mapped by a field of this subject area and 3 not, and 1 key (counted; no Row identifier).

It is the relationship table of a parent-child hierarchy, with one row per member and ancestor [kb:sampleapp::phys::xe.SAMPLESALES.EMP_PARENT_CHILD]:

| Role | Column | Evidence |
|---|---|---|
| Member | MEMBER_KEY | [kb:sampleapp::phys::xe.SAMPLESALES.EMP_PARENT_CHILD] |
| Parent (ancestor) | ANCESTOR_KEY | [kb:sampleapp::phys::xe.SAMPLESALES.EMP_PARENT_CHILD] |
| Distance (steps between the two) | DISTANCE | [kb:sampleapp::phys::xe.SAMPLESALES.EMP_PARENT_CHILD] |
| Leaf flag | IS_LEAF | [kb:sampleapp::phys::xe.SAMPLESALES.EMP_PARENT_CHILD] |

### Columns

| # | Column | Type | Precision | Scale | Nullable | External name | Mapped by | Evidence |
|---|---|---|---|---|---|---|---|---|
| 1 | MEMBER_KEY | DOUBLE | 15 | not recorded | not stated | not recorded | not mapped | [kb:sampleapp::phys::xe.SAMPLESALES.EMP_PARENT_CHILD.MEMBER_KEY] |
| 2 | ANCESTOR_KEY | DOUBLE | 15 | not recorded | not stated | not recorded | not mapped | [kb:sampleapp::phys::xe.SAMPLESALES.EMP_PARENT_CHILD.ANCESTOR_KEY] |
| 3 | DISTANCE | INT | not recorded | not recorded | not stated | not recorded | Dim Sales Rep, Distance | [kb:sampleapp::phys::xe.SAMPLESALES.EMP_PARENT_CHILD.DISTANCE] |
| 4 | IS_LEAF | INT | not recorded | not recorded | not stated | not recorded | not mapped | [kb:sampleapp::phys::xe.SAMPLESALES.EMP_PARENT_CHILD.IS_LEAF] |

Nullable: the export records this only when it is true. "Not stated" means the export does not carry it; it is not a statement that the column must be filled. "Not recorded" means the repository says nothing; it does not mean no, and it does not mean 0.

### Keys

| Key | Columns | Evidence |
|---|---|---|
| EMP_PARENT_CHILD_Key | MEMBER_KEY | [kb:sampleapp::phys::xe.SAMPLESALES.EMP_PARENT_CHILD.key.EMP_PARENT_CHILD_Key] |

This is key 1 of 1 the repository declares for the table. The repository does not say whether a physical key is the primary key; this is the only key it declares for the table. One foreign key of this subject area points at exactly this column [kb:sampleapp::phys::xe.SAMPLESALES.EMP_PARENT_CHILD.key.EMP_PARENT_CHILD_Key]. Whether MEMBER_KEY holds one row per value in this table is not recorded (open question Q14).

### Foreign keys and joins

- The foreign key "EMP_PARENT_CHILD_FKey" from this table (column ANCESTOR_KEY) to EMPLOYEE (column EMP_ID); stated in full in the section How the tables relate.
- The foreign key "SAMP_REVENUE_F_FKey#4" from SAMP_REVENUE_F (column EMPLOYEE_KEY) to this table (column MEMBER_KEY); stated in full in the section How the tables relate.

### Read by

The source "LTS Employee" of the business table Dim Sales Rep (listed in the packet without a Row identifier).
