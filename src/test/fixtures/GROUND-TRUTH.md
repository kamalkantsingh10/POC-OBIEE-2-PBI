# Fixture Ground Truth — `testrpd`

**What this covers:** the known-correct extraction result for each fixture, recorded when the fixture was authored.
**Who it is for:** anyone changing the `mds_xml` adapter, and anyone auditing what the extractor's tests actually prove.

Under AD-19 a construct without a fixture carrying recorded Ground Truth is treated as **unimplemented**. These fixtures are hand-authored, modelled on real corpus files, and their Ground Truth is authorial intent — see Story 1.9 for why that is weaker than Administration-Tool-generated cases and how the accuracy claim must be labelled.

`rpd-id`: **`testrpd`** — distinct from every corpus `rpd-id`, so fixture identifiers can never collide with real content (AD-7, AD-8).

## Physical layer — `PhysicalTable/pt1.xml`

| Expected | Value |
|---|---|
| Table identifier | `testrpd::phys::SALES_SCHEMA.SAMP_REVENUE_F` |
| Container resolution | `@containerRef` → `Schema/s1.xml#msch1` → name `SALES_SCHEMA` |
| Column identifiers | `testrpd::phys::SALES_SCHEMA.SAMP_REVENUE_F.REVENUE`, `…00123_CODE` |
| `REVENUE` data type | `DOUBLE`, nullable `true` |

**Why `00123_CODE` exists.** A column name a spreadsheet would coerce to `123`. It exercises the FR-28 guard end to end: the value must survive extraction and reappear byte-identical after a round trip through the CSV writer.

## BMM layer — `LogicalTable/lt1.xml`

| Expected | Value |
|---|---|
| Table identifier | `testrpd::bmm::Sample Sales.Fact Revenue` |
| Business model resolution | `@subjectAreaRef` → `BusinessModel/bm1.xml#mbm1` → `Sample Sales` |
| Logical columns | `…Fact Revenue.Revenue` (measure), `…Fact Revenue.Negative Margin` (not a measure) |

### `Revenue` — two aggregation rules

**Exactly two rows must be produced.** FR-4 requires dimension-specific aggregation overrides captured, and collapsing multiple `AggrRule` elements into one loses precisely that — a loss that yields a plausible wrong number rather than an error.

| Rule | `is_default` | `expr_text` | `expr_text_desc` |
|---|---|---|---|
| `AggRule_default` | `true` | `` SUM("%1")`` | `` SUM("Sample Sales"."Fact Revenue"."Revenue")`` |
| `AggRule_override` | `false` | `COUNT("%1")` | `COUNT("Sample Sales"."Fact Revenue"."Revenue")` |

**Both leading spaces are significant.** `ExprText` and `ExprTextDesc` on the default rule begin with a space, exactly as real corpus `AggrRule` expressions do. FR-2 forbids reformatting anywhere in the chain, so the extractor must not strip it.

The default rule's `ObjectRefList` resolves to `testrpd::phys::SALES_SCHEMA.SAMP_REVENUE_F.REVENUE`. The override rule has **no** `ObjectRefList` — a real shape, and its `expr_refs` must be empty rather than inherited from the sibling rule.

### `Negative Margin` — the FR-2 / FR-28 collision in one value

```
-1 * "%1", "x"
next
```

This single expression carries every hazard at once: a **leading `-`** (a spreadsheet reads it as a formula), an **embedded comma** (CSV delimiter), **embedded double quotes** (CSV quoting), and an **embedded newline** (row boundary).

Expected: stored character-for-character, guarded on write with `derived_expr_text_guarded = true`, and recovered byte-identical on read. If any part of this value changes, FR-2 is broken — and this is the fixture that says so.

`Negative Margin` is **not** a measure: it has no `MeasureDefn`, so `is_measure` is `false` and it contributes no aggregation-rule rows.

## Coverage expectations

| Object type | Parsed |
|---|---|
| `PhysicalTable` | 1 |
| `PhysicalColumn` | 2 |
| `LogicalTable` | 1 |
| `LogicalColumn` | 2 |
| `AggrRule` | 2 |
| `Schema`, `BusinessModel` | 1 each — index-only, contributing names rather than rows |

No unparsed entries are expected from this fixture set. Every reference resolves, and every object type present is modelled.

## Presentation layer — `PresentationCatalog/pcat1.xml`, `PresentationTable/ptab1.xml`

| Expected | Value |
|---|---|
| Subject Area identifier | `testrpd::pres::Sample Sales SA` |
| Business model | `Sample Sales`, via `@subjectAreaRef` |
| **Implicit fact column** | `testrpd::bmm::Sample Sales.Fact Revenue.Revenue`, via `@defaultFactColumnRef` |
| Alias | `Old Sales SA` |
| Presentation table | `testrpd::pres::Sample Sales SA.Facts` |
| Presentation columns | `…Facts.Total Revenue`, `…Facts.Margin` |

**Rename chain.** `Total Revenue` is a presentation name over the logical column `Revenue`: `@overrideLogicalName="true"` with `@dispName="Revenue $"`. All three components — logical column reference, override flag, display name — are stored separately, so each step of physical → logical → presentation stays traceable (NFR2). A pre-joined string would lose that.

`@defaultFactColumnRef` is the implicit fact column FR-4 requires. It appears on just 1 of 21 Subject Areas in the real corpus, so this fixture is the primary evidence the construct is handled at all.

## Logical Table Sources and lineage — `LogicalTableSource/lts*.xml`

Two sources on one logical table, deliberately asymmetric:

| Source | Maps `Revenue` | Maps `Negative Margin` |
|---|---|---|
| `SRC_DETAIL` | yes → `…SAMP_REVENUE_F.REVENUE` | yes → `…SAMP_REVENUE_F.00123_CODE` |
| `SRC_AGG` | yes → `…SAMP_REVENUE_F.REVENUE` | **no** |

### Expected counts — the distinction that matters

| Column | `source_count` | `mapped_source_count` | Lineage rows |
|---|---|---|---|
| `Revenue` | 2 | 2 | 2 |
| `Negative Margin` | 2 | **1** | **1** |

`source_count` counts sources on the owning **table**; `mapped_source_count` counts sources that actually map **that column**. They differ whenever a source cannot supply a column — 53 real columns in the corpus show this gap.

Asserting lineage rows against `source_count` would produce a false failure here. Asserting against `mapped_source_count` is correct, and the **gap between the two is itself the signal**: a source that cannot supply a column cannot satisfy a query needing it, which is a genuine parity hazard.

Total lineage rows across the fixture: **3**.

### `SRC_DETAIL` content filter

```
-1 = VALUEOF(NQ_SESSION.X)
```

A `WhereClause` beginning with `-`. It is a content filter (FR-4) *and* a spreadsheet hazard (FR-28) in one value: expected stored verbatim, guarded on write, recovered byte-identical.

`FragmentContent` is present on `SRC_DETAIL` and **empty**, mirroring the real corpus where all 92 sources carry the element and none populate it. Expected: recorded as present-and-empty. "Empty" and "absent" must not read the same — the corpus does not exercise fragmentation, which is different from having no fragmentation.

**`priority` is expected to be empty on both sources.** MDS XML records no priority attribute. Empty means *not recorded*, never *lowest*; inferring order from document position is the guess AD-3 forbids.

## Dimensions and hierarchies — `Dimension/dim*.xml`, `PresentationHierarchy/ph*.xml`

Added with story 1.6 of the Java port; the values are those the Python reference writes for the same files.

| Expected | Value |
|---|---|
| Dimensions | `testrpd::bmm::Sample Sales.Time Dim` (ragged, level-based, 2 levels, default root level `…Time Dim.Total`), `testrpd::bmm::Sample Sales.Rep Tree` (parent-child, 1 level) |
| Logical levels | `…Time Dim.Total` (position 0, grand total, no drill key), `…Time Dim.Year` (position 1, drill key `testrpd::bmm::Sample Sales.Fact Revenue.Revenue`), `…Rep Tree.Rep` (position 0) |
| Presentation hierarchies | `testrpd::pres::Sample Sales SA.Facts.Time` (2 levels, no column of its own), `…Facts.Rep Tree` (0 levels, `display_column_id` = `…Facts.Margin`) |
| Presentation levels | `…Facts.Time.Total` (position 0), `…Facts.Time.Year` (position 1), each tied to the logical level of the same name |
| Level columns | `…Facts.Time.Year.0` → `…Facts.Total Revenue`, `…Facts.Time.Year.1` → `…Facts.Margin` |

**The flags are the point.** `Time Dim` is ragged and `Rep Tree` is parent-child; both flags must reach `logical_dimensions` as they are. `Rep Tree` shows that a parent-child dimension can still have a logical level while its presentation hierarchy has none: its single column sits on the hierarchy itself.

Coverage: `Dimension` 2, `LogicalLevel` 3, `PresentationHierarchy` 2, `PresentationLevel` 2. No errors.
