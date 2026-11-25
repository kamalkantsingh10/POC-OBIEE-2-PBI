## Security

This is the security that the repository records for this subject area: 2 row filters, each on one source of a business table. The packet records no permission and no role.

### Row filters

**Row filter on the source "FCT_TRANSACTIONS_ACTUAL"** of the business table FCT_TRANSACTIONS_ACTUAL [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.FCT_TRANSACTIONS_ACTUAL.src.FCT_TRANSACTIONS_ACTUAL#filter]. The filter, exactly as extracted:

```
(
Position (';MT_User_A;' in ';' || valueof(NQ_SESSION.ROLEGUIDS) || ';') > 0 and  "Iryna_Bradzetskaya_MT".""."U_SAL"."FCT_TRANSACTIONS_ACTUAL"."CURRENCY_SUR_ID"   in (12,10,15,11)
)
or
(
Position (';MT_User_F;' in ';' || valueof(NQ_SESSION.ROLEGUIDS) || ';') > 0 and "Iryna_Bradzetskaya_MT".""."U_SAL"."FCT_TRANSACTIONS_ACTUAL"."CURRENCY_SUR_ID"  in (13,14, 16,17)
)
```

Put mechanically: two bracketed conditions joined by "or". The first looks for the text ";MT_User_A;" inside the value `ROLEGUIDS` (framed by ";" on both sides) and requires the database column FCT_TRANSACTIONS_ACTUAL.CURRENCY_SUR_ID to be one of 12, 10, 15, 11; the second looks for ";MT_User_F;" and requires one of 13, 14, 16, 17 [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.FCT_TRANSACTIONS_ACTUAL.src.FCT_TRANSACTIONS_ACTUAL#filter].

**Row filter on the source "FCT_TRANSACTIONS_DAILY"** of the business table TRANSACTIONS_DAILY [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TRANSACTIONS_DAILY.src.FCT_TRANSACTIONS_DAILY#filter]. The filter, exactly as extracted:

```
(
Position (';Role_MT_A;' in ';' || valueof(NQ_SESSION.ROLEGUIDS) || ';') > 0 and  "Iryna_Bradzetskaya_MT".""."U_SAL"."FCT_TRANSACTIONS_DAILY"."CURRENCY_SUR_ID"   in (12,10,15,11)
)
or
(
Position (';Role_MT_F;' in ';' || valueof(NQ_SESSION.ROLEGUIDS) || ';') > 0 and "Iryna_Bradzetskaya_MT".""."U_SAL"."FCT_TRANSACTIONS_DAILY"."CURRENCY_SUR_ID"  in (13,14, 16,17)
)
```

Put mechanically: the same two conditions, with the texts ";Role_MT_A;" and ";Role_MT_F;" and the column FCT_TRANSACTIONS_DAILY.CURRENCY_SUR_ID, and the same two lists of numbers [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TRANSACTIONS_DAILY.src.FCT_TRANSACTIONS_DAILY#filter].

Both filters read `ROLEGUIDS`, a name used and not defined in the repository (see the section Values set per user or for the whole system). No row filter is recorded for any other source, so the business tables CURRENCY, CUSTOMERS_REC, CUSTOMERS_SEND, LOCATIONS_REC, LOCATIONS_SEND, OPERATIONS, TARIFFS and TIMES_DAILY carry none (counted). Which currencies the numbers stand for, and what a user holding none or both of the named texts is meant to see, are not recorded (open question Q3).

### Permissions

No permission on an object of this subject area is recorded in the packet.

### Roles

No role is recorded in the packet: neither "Role_MT_A", "Role_MT_F", "MT_User_A" nor "MT_User_F" appears as a role, with a name in the repository, a guid, a privilege package or a query privilege. Who holds these four, and whether they are roles, groups or user names, is not recorded (open question Q14).
