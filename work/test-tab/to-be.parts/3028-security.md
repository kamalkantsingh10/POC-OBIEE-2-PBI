### Security

#### Row filters

The packet records no row filter on any source of the slice. The FSD item S1-own-offices, that each manager sees only their own offices, is not confirmed and is listed under Unresolved; this design builds no row-level security for it.

#### Permissions on fields

All four permissions in the slice are on the confirmed field "Revenue" of "Fact Target", which becomes the measure Revenue Target.

| Permission | On | Privilege package | Held by | Evidence |
|---|---|---|---|---|
| `write` | "Fact Target"."Revenue" | PrivPack_4209:18101355661677 | the role `BI Author Role` | [kb:sampleapp::sec::obj.ObjPriv_1500:18131355661677.o0] |
| `read` | "Fact Target"."Revenue" | PrivPack_4209:18141355661677 | no role is recorded in the slice for that privilege package | [kb:sampleapp::sec::obj.ObjPriv_1500:18161355661677.o0] |
| `read` | "Fact Target"."Revenue" | PrivPack_4209:18181355661677 | no role is recorded in the slice for that privilege package | [kb:sampleapp::sec::obj.ObjPriv_1500:18201355661677.o0] |
| `write` | "Fact Target"."Revenue" | PrivPack_4209:18181355661677 | no role is recorded in the slice for that privilege package | [kb:sampleapp::sec::obj.ObjPriv_1500:18211355661677.o0] |

#### Roles

Role with display name `BI Author Role`; guid `BIAuthor`. It holds query privilege `QueryPriv_4204:18331355753308` of privilege package `PrivPack_4209:18101355661677` (kind query_privilege). [kb:sampleapp::sec::pkg.PrivPack_4209:18101355661677.q0]

The "write" permissions belong with decision Q6 (whether targets are typed in). Who may read the target figures is decision Q18. No model role is proposed until it is taken.
