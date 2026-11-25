## Database table SAMP_OFFICES_D

SAMP_OFFICES_D is a database table in `xe.SAMPLESALES`, type `none`; it is not an alias of another table, and no description is recorded [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_OFFICES_D]. No separate object name in the database is recorded, so the repository knows it only by the name SAMP_OFFICES_D (listed in the packet without a Row identifier). The export lists 10 columns for it, all 10 mapped by a field of this subject area, and 1 key (counted).

### Columns

| # | Column | Type | Precision | Scale | Nullable | External name | Mapped by | Evidence |
|---|---|---|---|---|---|---|---|---|
| 1 | COMPANY | VARCHAR | 14 | not recorded | true | `samplesales` | Dim Offices, Company | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_OFFICES_D.COMPANY] |
| 2 | COMPANY_KEY | DOUBLE | 15 | not recorded | not stated | `samplesales` | Dim Offices, Company Key | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_OFFICES_D.COMPANY_KEY] |
| 3 | DEPARTMENT | VARCHAR | 25 | not recorded | true | `samplesales` | Dim Offices, Department | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_OFFICES_D.DEPARTMENT] |
| 4 | DEPT_KEY | DOUBLE | 15 | not recorded | not stated | `samplesales` | Dim Offices, Dept Key | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_OFFICES_D.DEPT_KEY] |
| 5 | OFFICE_DSC | VARCHAR | 17 | not recorded | true | `samplesales` | Dim Offices, Office Dsc | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_OFFICES_D.OFFICE_DSC] |
| 6 | OFFICE_KEY | DOUBLE | 15 | not recorded | not stated | `samplesales` | Dim Offices, Office Key | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_OFFICES_D.OFFICE_KEY] |
| 7 | ORGANIZATION | VARCHAR | 18 | not recorded | true | `samplesales` | Dim Offices, Organization | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_OFFICES_D.ORGANIZATION] |
| 8 | ORG_KEY | DOUBLE | 15 | not recorded | not stated | `samplesales` | Dim Offices, Org Key | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_OFFICES_D.ORG_KEY] |
| 9 | SEQUENCE | DOUBLE | 15 | not recorded | true | `samplesales` | Dim Offices, Sequence | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_OFFICES_D.SEQUENCE] |
| 10 | AREA_NAME | VARCHAR | 25 | not recorded | true | not recorded | Dim Offices, Area Name | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_OFFICES_D.AREA_NAME] |

Nullable: the export records this only when it is true. "Not stated" means the export does not carry it; it is not a statement that the column must be filled. "Not recorded" means the repository says nothing; it does not mean no, and it does not mean 0.

### Keys

| Key | Columns | Evidence |
|---|---|---|
| SAMP_OFFICES_D_Key | OFFICE_KEY | [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_OFFICES_D.key.SAMP_OFFICES_D_Key] |

The repository does not say whether a physical key is the primary key; this is the only key it declares for the table. Two foreign keys of this subject area point at exactly OFFICE_KEY [kb:sampleapp::phys::xe.SAMPLESALES.SAMP_OFFICES_D.key.SAMP_OFFICES_D_Key].

### Foreign keys and joins

- The complex join "Complex Join" between FactCrossJoin and this table; its condition is stated in full in the section How the tables relate.
- The foreign key "SAMP_REVENUE_F_FKey#1" from SAMP_REVENUE_F (column OFFICE_KEY) to this table (column OFFICE_KEY); stated in full in the section How the tables relate.
- The foreign key "SAMP_TARGETS_F_FKey#1" from SAMP_TARGETS_F (column OFFICE_KEY) to this table (column OFFICE_KEY); stated in full in the section How the tables relate.

### Read by

The source "SAMP_OFFICES_D" of the business table Dim Offices (listed in the packet without a Row identifier).
