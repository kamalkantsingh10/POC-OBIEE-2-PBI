## TRANSACTIONS_DAILY

TRANSACTIONS_DAILY is a business table ("logical table") of the business model "Iryna_Bradzetskaya_MT_Row-level_Security"; the repository declares 1 source for it [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TRANSACTIONS_DAILY]. In this subject area it has 11 fields, of which 2 are measures, and all 11 are shown to report authors; its one source is "FCT_TRANSACTIONS_DAILY", described under "Where the data comes from" (counted; no Row identifier).

### Keys

Keys of the business table TRANSACTIONS_DAILY: not recorded. The repository declares no key for it (listed in the packet without a Row identifier).

### Measures

| Measure | How it is summarised | Can be typed into | In sources | Evidence |
|---|---|---|---|---|
| AMOUNT_PAYMENT | SUM, by default; the rule is quoted below | no | 1 of 1: "FCT_TRANSACTIONS_DAILY" | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TRANSACTIONS_DAILY.AMOUNT_PAYMENT] [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TRANSACTIONS_DAILY.AMOUNT_PAYMENT#aggr0] |
| COUNT_PAYMENT | SUM, by default; the rule is quoted below | no | 1 of 1: "FCT_TRANSACTIONS_DAILY" | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TRANSACTIONS_DAILY.COUNT_PAYMENT] [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TRANSACTIONS_DAILY.COUNT_PAYMENT#aggr0] |

The rule for AMOUNT_PAYMENT, exactly [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TRANSACTIONS_DAILY.AMOUNT_PAYMENT#aggr0]:

```
SUM("Iryna_Bradzetskaya_MT_Row-level_Security"."TRANSACTIONS_DAILY"."AMOUNT_PAYMENT")
```

The rule for COUNT_PAYMENT, exactly [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TRANSACTIONS_DAILY.COUNT_PAYMENT#aggr0]:

```
SUM("Iryna_Bradzetskaya_MT_Row-level_Security"."TRANSACTIONS_DAILY"."COUNT_PAYMENT")
```

Both measures are shown to report authors under the same name in the table TRANSACTIONS_DAILY [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.TRANSACTIONS_DAILY.AMOUNT_PAYMENT] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.TRANSACTIONS_DAILY.COUNT_PAYMENT]. The definition kept on each measure itself is one database column: FCT_TRANSACTIONS_DAILY.AMOUNT_PAYMENT [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TRANSACTIONS_DAILY.AMOUNT_PAYMENT] and FCT_TRANSACTIONS_DAILY.COUNT_PAYMENT [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TRANSACTIONS_DAILY.COUNT_PAYMENT]. What each is mapped from in the source is stated with the source. That a count of payments is summed is recorded as above; what COUNT_PAYMENT holds per row is not recorded (open question Q1).

### Fields

Every field is shown to report authors under its own name in the table TRANSACTIONS_DAILY. None can be typed into. Except for PAYMENT_WHIS_TAX, the definition kept on each field is one database column of the same name in FCT_TRANSACTIONS_DAILY.

| Field | Shown to report authors as | Kept definition | In sources | Evidence |
|---|---|---|---|---|
| CURRENCY_SUR_ID | CURRENCY_SUR_ID | FCT_TRANSACTIONS_DAILY.CURRENCY_SUR_ID | 1 of 1: "FCT_TRANSACTIONS_DAILY" | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TRANSACTIONS_DAILY.CURRENCY_SUR_ID] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.TRANSACTIONS_DAILY.CURRENCY_SUR_ID] |
| CUST_SEND_ID | CUST_SEND_ID | FCT_TRANSACTIONS_DAILY.CUST_SEND_ID | 1 of 1: "FCT_TRANSACTIONS_DAILY" | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TRANSACTIONS_DAILY.CUST_SEND_ID] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.TRANSACTIONS_DAILY.CUST_SEND_ID] |
| CUS_REC_ID | CUS_REC_ID | FCT_TRANSACTIONS_DAILY.CUS_REC_ID | 1 of 1: "FCT_TRANSACTIONS_DAILY" | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TRANSACTIONS_DAILY.CUS_REC_ID] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.TRANSACTIONS_DAILY.CUS_REC_ID] |
| EVENT_DT | EVENT_DT | FCT_TRANSACTIONS_DAILY.EVENT_DT | 1 of 1: "FCT_TRANSACTIONS_DAILY" | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TRANSACTIONS_DAILY.EVENT_DT] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.TRANSACTIONS_DAILY.EVENT_DT] |
| GEO_REC_SUR_ID | GEO_REC_SUR_ID | FCT_TRANSACTIONS_DAILY.GEO_REC_SUR_ID | 1 of 1: "FCT_TRANSACTIONS_DAILY" | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TRANSACTIONS_DAILY.GEO_REC_SUR_ID] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.TRANSACTIONS_DAILY.GEO_REC_SUR_ID] |
| GEO_SEND_SUR_ID | GEO_SEND_SUR_ID | FCT_TRANSACTIONS_DAILY.GEO_SEND_SUR_ID | 1 of 1: "FCT_TRANSACTIONS_DAILY" | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TRANSACTIONS_DAILY.GEO_SEND_SUR_ID] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.TRANSACTIONS_DAILY.GEO_SEND_SUR_ID] |
| OPERATION_ID | OPERATION_ID | FCT_TRANSACTIONS_DAILY.OPERATION_ID | 1 of 1: "FCT_TRANSACTIONS_DAILY" | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TRANSACTIONS_DAILY.OPERATION_ID] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.TRANSACTIONS_DAILY.OPERATION_ID] |
| PAYMENT_WHIS_TAX | PAYMENT_WHIS_TAX | a formula, quoted below | 0 of 1 | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TRANSACTIONS_DAILY.PAYMENT_WHIS_TAX] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.TRANSACTIONS_DAILY.PAYMENT_WHIS_TAX] |
| TARIFF_ID | TARIFF_ID | FCT_TRANSACTIONS_DAILY.TARIFF_ID | 1 of 1: "FCT_TRANSACTIONS_DAILY" | [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TRANSACTIONS_DAILY.TARIFF_ID] [kb:bi-analytics-2013::pres::Iryna_Bradzetskaya_MT_Row-level_Security.TRANSACTIONS_DAILY.TARIFF_ID] |

PAYMENT_WHIS_TAX is not a measure and is mapped in no source; it is calculated from another field of this table, the measure AMOUNT_PAYMENT multiplied by 20 [kb:bi-analytics-2013::bmm::Iryna_Bradzetskaya_MT_Row-level_Security.TRANSACTIONS_DAILY.PAYMENT_WHIS_TAX]. Its formula, exactly:

```
"Iryna_Bradzetskaya_MT_Row-level_Security"."TRANSACTIONS_DAILY"."AMOUNT_PAYMENT" * 20
```

What the number 20 stands for, and what the name PAYMENT_WHIS_TAX means, are not recorded (open question Q2).

### Measures and fields report authors do not see

This table has no measures or fields that report authors do not see.
