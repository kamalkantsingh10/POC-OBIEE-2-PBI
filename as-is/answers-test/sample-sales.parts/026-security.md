## Security

### Row filters

No row filter is recorded for any source of this subject area.

### Permissions on objects

The packet lists 6 permissions, all on the two fields "Revenue" and "Units" of "Fact Target" that report authors see (counted).

| Permission | On | Privilege package | Held by | Evidence |
|---|---|---|---|---|
| `write` | field "Revenue" of the table "Fact Target" | PrivPack_4209:18101355661677 | BI Author Role | [kb:sampleapp::sec::obj.ObjPriv_1500:18131355661677.o0] |
| `write` | field "Units" of the table "Fact Target" | PrivPack_4209:18101355661677 | BI Author Role | [kb:sampleapp::sec::obj.ObjPriv_1500:18131355661677.o1] |
| `read` | field "Revenue" of the table "Fact Target" | PrivPack_4209:18141355661677 | no role is recorded in the slice for that privilege package | [kb:sampleapp::sec::obj.ObjPriv_1500:18161355661677.o0] |
| `read` | field "Units" of the table "Fact Target" | PrivPack_4209:18141355661677 | no role is recorded in the slice for that privilege package | [kb:sampleapp::sec::obj.ObjPriv_1500:18161355661677.o1] |
| `read` | field "Revenue" of the table "Fact Target" | PrivPack_4209:18181355661677 | no role is recorded in the slice for that privilege package | [kb:sampleapp::sec::obj.ObjPriv_1500:18201355661677.o0] |
| `write` | field "Revenue" of the table "Fact Target" | PrivPack_4209:18181355661677 | no role is recorded in the slice for that privilege package | [kb:sampleapp::sec::obj.ObjPriv_1500:18211355661677.o0] |

The export records no role for the privilege packages PrivPack_4209:18141355661677 and PrivPack_4209:18181355661677 [kb:sampleapp::sec::obj.ObjPriv_1500:18161355661677.o0] [kb:sampleapp::sec::obj.ObjPriv_1500:18201355661677.o0]. According to the user, both privilege packages are held by the role BI Author Role [answer:Q12].

### Roles

- Role with display name `BI Author Role`; name in the repository, exactly (the space is part of the name):

```
6D7874D30FD9EC2E7C15CF8DD0C61529AF3BDCAD955E5AD1372E6D902B14F0A9 3FA146A018E99435FCE83B3F7BDDBF5A30BAF7BEF918C3AFAA354A94E310F903
```

  guid `BIAuthor`. It holds the query privilege `QueryPriv_4204:18331355753308` of the privilege package `PrivPack_4209:18101355661677` (kind query_privilege) [kb:sampleapp::sec::pkg.PrivPack_4209:18101355661677.q0].

### Points to note

- The only object permissions recorded for this subject area are on "Revenue" and "Units" of "Fact Target"; "write" is given through two privilege packages [kb:sampleapp::sec::obj.ObjPriv_1500:18131355661677.o0] [kb:sampleapp::sec::obj.ObjPriv_1500:18211355661677.o0] (open question Q7).
- The overview counts 2 object permissions of the estate whose target was not recorded; they are not described here (see the section What report authors see).
