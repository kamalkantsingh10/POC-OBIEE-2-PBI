# Security: who may see or change what
Unit: security
Kind: security
Tab: test-tab (subject areas: Sample Sales; estate sampleapp)
Facts from: security_rls
Rows of this packet a document must cover: 5. Each is on a line that ends with (REQUIRED).
To cite a fact, copy the marker in square brackets that follows it, exactly as written. "->" points at another row.

## Permissions on objects

- Permission `write` on presentation column "Revenue" -> sampleapp::pres::Sample Sales.Fact Target.Revenue; given through privilege package -> sampleapp::sec::pkg.PrivPack_4209:18101355661677. [kb:sampleapp::sec::obj.ObjPriv_1500:18131355661677.o0] (REQUIRED)
    - Held by 1 role(s), through that privilege package, one per line:
        - Role with display name `BI Author Role`; name in the repository `6D7874D30FD9EC2E7C15CF8DD0C61529AF3BDCAD955E5AD1372E6D902B14F0A9 3FA146A018E99435FCE83B3F7BDDBF5A30BAF7BEF918C3AFAA354A94E310F903` (this is the name of one role: the space is part of the name); guid `BIAuthor`. -> sampleapp::sec::pkg.PrivPack_4209:18101355661677.q0

- Permission `read` on presentation column "Revenue" -> sampleapp::pres::Sample Sales.Fact Target.Revenue; given through privilege package -> sampleapp::sec::pkg.PrivPack_4209:18141355661677. [kb:sampleapp::sec::obj.ObjPriv_1500:18161355661677.o0] (REQUIRED)
    - Held by: no role is recorded in the slice for that privilege package.

- Permission `read` on presentation column "Revenue" -> sampleapp::pres::Sample Sales.Fact Target.Revenue; given through privilege package -> sampleapp::sec::pkg.PrivPack_4209:18181355661677. [kb:sampleapp::sec::obj.ObjPriv_1500:18201355661677.o0] (REQUIRED)
    - Held by: no role is recorded in the slice for that privilege package.

- Permission `write` on presentation column "Revenue" -> sampleapp::pres::Sample Sales.Fact Target.Revenue; given through privilege package -> sampleapp::sec::pkg.PrivPack_4209:18181355661677. [kb:sampleapp::sec::obj.ObjPriv_1500:18211355661677.o0] (REQUIRED)
    - Held by: no role is recorded in the slice for that privilege package.

## Roles that hold a privilege package (one role per line)

- Role with display name `BI Author Role`; name in the repository `6D7874D30FD9EC2E7C15CF8DD0C61529AF3BDCAD955E5AD1372E6D902B14F0A9 3FA146A018E99435FCE83B3F7BDDBF5A30BAF7BEF918C3AFAA354A94E310F903` (this is the name of one role: the space is part of the name); guid `BIAuthor`. It holds query privilege `QueryPriv_4204:18331355753308` of privilege package `PrivPack_4209:18101355661677` (kind query_privilege). [kb:sampleapp::sec::pkg.PrivPack_4209:18101355661677.q0] (REQUIRED)

