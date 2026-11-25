# Knowledge Base comparison report

This report is written by `./build.sh compare-all`. Do not edit it by hand: run the command again. Last written on 2026-11-24.

**Confirmation by Kamal: NOT YET GIVEN.** Epic 1 is not done until Kamal has read this report and confirmed it. This line is to be changed by a person, not by the program.

## The result in short

| | |
|---|---|
| Sample estates compared | 3 of 3 |
| Reference tables | 39 (each compared once per estate: 117 table comparisons) |
| Reference rows compared | 60,138 |
| Generated rows compared | 61,073 |
| Rows identical on both sides | 59,061 |
| **Unexplained differences** | **0** |

Every difference between the reference Knowledge Base and the one the jar writes is one of the numbered deliberate differences in `docs/port-guide/00-read-me-first.md`. Each was checked cell by cell or row by row, as described below; none was accepted because of the table or column it is in.

## 1. What was compared, and how

**The reference** is `reference/knowledge-base/`: the 39 CSV tables the first attempt's Python program wrote, in one run, from two sample repositories (`sampleapp`, `bi-analytics-2013`) and one sample Web Catalog (`sampleapp-v607`).

**The generated Knowledge Bases** are `kb/<estate>/`, written by this command with `java -jar build/obi2pbi.jar extract estates/<estate>` from the same input files.

| Estate | Input | `extract` exit code | What `extract` printed |
|---|---|---|---|
| `sampleapp` | `estates/sampleapp` | 0 | estate:   sampleapp (rpd); parsed:   625 objects; skipped:  15 objects; gaps:     110 not modelled  -> kb/sampleapp/unparsed.csv; errors:   0                 -> kb/sampleapp/errors.csv; written:  kb/sampleapp |
| `bi-analytics-2013` | `estates/bi-analytics-2013` | 0 | estate:   bi-analytics-2013 (rpd); parsed:   6254 objects; skipped:  102 objects; gaps:     211 not modelled  -> kb/bi-analytics-2013/unparsed.csv; errors:   0                 -> kb/bi-analytics-2013/errors.csv; written:  kb/bi-analytics-2013 |
| `sampleapp-v607` | `estates/sampleapp-v607` | 3 | estate:   sampleapp-v607 (catalog); parsed:   13729 objects; skipped:  2676 objects; gaps:     1053 not modelled  -> kb/sampleapp-v607/unparsed.csv; errors:   1185                 -> kb/sampleapp-v607/errors.csv; written:  kb/sampleapp-v607 |

Exit code 3 means the Knowledge Base was written and `errors.csv` has rows; for the catalog these are references to parts of the catalog that were not exported, as in the reference.

**How a table is compared.**

1. Only the rows of the one estate are taken from the reference table (its `rpd_id` column).
2. The reference's `..._guarded` columns are left out, and the apostrophe its writer put in front of a guarded value is taken off, so that values are compared as the source held them (difference 1).
3. Rows are paired by `row_id`. `unparsed` is paired by estate, kind, object type and detail; `coverage` by estate, layer and object type; a table with no `row_id` is compared as whole rows. A `row_id` that occurs twice is compared as whole rows.
4. Paired rows are compared cell by cell, in every column both sides have. A row only one side has is "missing" (reference only) or "extra" (generated only).
5. Every difference is then tested against the deliberate differences. A difference counts as explained only when a check passes: either a stated transformation of the reference value gives the generated value, or the estate's own files, read a second time by the comparison with the JDK's own XML parser, bear the generated value out. Section 3 says what was checked for each. Anything else is unexplained.

The comparison is `src/test/java/obi2pbi/KbCompare.java`; this report is written by `KbCompareReport.java` beside it. In the examples below, a line break inside a value is shown as `\n` and a tab as `\t`, and a long value is cut short with its full length given.

## 2. Every table, per estate

Column meanings: **Identical** = rows present on both sides and equal in every shared column. **Differ only as explained** = rows present on both sides whose differing cells are all explained. **Missing** = reference rows the jar did not write; **Extra** = rows the jar wrote that the reference lacks (both counts include explained rows; the last column says how many are not). **Unexplained** = cells, rows or columns no deliberate difference explains. **Explained by** names the difference number and what it accounts for. Reference rows = identical + differ only as explained + rows with unexplained cells + missing. `manifest.csv` is one file of keys and values for all three estates; its row here counts the keys that concern the estate, and the table after the three estates shows each of them.

### sampleapp

| Table | Reference rows | Generated rows | Identical | Differ only as explained | Missing | Extra | Unexplained | Explained by |
|---|---:|---:|---:|---:|---:|---:|---:|---|
| `aggregation_rules` | 13 | 13 | 13 | 0 | 0 | 0 | 0 |  |
| `analyses` | 0 | 0 | 0 | 0 | 0 | 0 | 0 |  |
| `analysis_columns` | 0 | 0 | 0 | 0 | 0 | 0 | 0 |  |
| `analysis_criteria` | 0 | 0 | 0 | 0 | 0 | 0 | 0 |  |
| `analysis_filters` | 0 | 0 | 0 | 0 | 0 | 0 | 0 | 10: 2 added columns |
| `analysis_prompts` | 0 | 0 | 0 | 0 | 0 | 0 | 0 |  |
| `analysis_views` | 0 | 0 | 0 | 0 | 0 | 0 | 0 |  |
| `bmm_joins` | 9 | 9 | 9 | 0 | 0 | 0 | 0 |  |
| `catalog_versions` | 0 | 0 | 0 | 0 | 0 | 0 | 0 |  |
| `coverage` | 30 | 33 | 29 | 0 | 1 | 4 | 0 | 5: 1 missing; 28: 1 extra; 29: 1 extra; 34: 2 extra |
| `coverage_summary` | 0 | 0 | 0 | 0 | 0 | 0 | 0 |  |
| `dashboard_contents` | 0 | 0 | 0 | 0 | 0 | 0 | 0 |  |
| `dashboard_pages` | 0 | 0 | 0 | 0 | 0 | 0 | 0 |  |
| `dashboards` | 0 | 0 | 0 | 0 | 0 | 0 | 0 |  |
| `errors` | 2 | 0 | 0 | 0 | 2 | 0 | 0 | 5: 2 missing |
| `init_blocks` | 2 | 2 | 2 | 0 | 0 | 0 | 0 |  |
| `lineage` | 72 | 72 | 72 | 0 | 0 | 0 | 0 |  |
| `logical_columns` | 77 | 77 | 75 | 2 | 0 | 0 | 0 | 5: 2 cells; 37: 1 added columns |
| `logical_dimensions` | 5 | 5 | 5 | 0 | 0 | 0 | 0 | 32: 1 added columns |
| `logical_levels` | 22 | 22 | 22 | 0 | 0 | 0 | 0 | 35: 2 added columns |
| `logical_table_sources` | 17 | 17 | 14 | 3 | 0 | 0 | 0 | 6: 3 cells; 36: 1 added columns |
| `logical_tables` | 11 | 11 | 11 | 0 | 0 | 0 | 0 | 37: 1 added columns |
| `lts_column_map` | 95 | 95 | 95 | 0 | 0 | 0 | 0 |  |
| `manifest` | 7 | 7 | 2 | 4 | 0 | 0 | 0 | 15: 1 row; 16: 1 row; 17: 2 rows; `extracted_at`, 1 row, is the time of the run and is counted in no column |
| `physical_columns` | 139 | 139 | 139 | 0 | 0 | 0 | 0 | 31: 2 added columns; 37: 1 added columns |
| `physical_joins` | 22 | 22 | 4 | 18 | 0 | 0 | 0 | 7: 18 cells; 26: 18 cells, 1 added columns |
| `physical_table_definitions` | 12 | 12 | 12 | 0 | 0 | 0 | 0 |  |
| `physical_tables` | 22 | 22 | 22 | 0 | 0 | 0 | 0 | 36: 4 added columns; 37: 1 added columns |
| `presentation_columns` | 59 | 59 | 59 | 0 | 0 | 0 | 0 |  |
| `presentation_hierarchies` | 5 | 5 | 5 | 0 | 0 | 0 | 0 |  |
| `presentation_level_columns` | 16 | 16 | 16 | 0 | 0 | 0 | 0 |  |
| `presentation_levels` | 20 | 20 | 20 | 0 | 0 | 0 | 0 |  |
| `presentation_subject_areas` | 3 | 3 | 3 | 0 | 0 | 0 | 0 |  |
| `presentation_tables` | 10 | 10 | 10 | 0 | 0 | 0 | 0 |  |
| `report_lineage` | 0 | 0 | 0 | 0 | 0 | 0 | 0 |  |
| `saved_filters` | 0 | 0 | 0 | 0 | 0 | 0 | 0 |  |
| `security_rls` | 9 | 9 | 9 | 0 | 0 | 0 | 0 | 27: 2 added columns |
| `unparsed` | 146 | 110 | 104 | 6 | 36 | 0 | 0 | 26: 1 missing; 27: 2 cells; 28: 5 missing; 29: 6 missing; 30: 2 cells, 4 missing; 31: 1 missing; 32: 1 missing; 34: 8 missing; 35: 2 cells, 1 missing; 36: 5 missing; 37: 4 missing |
| `variables` | 2 | 2 | 2 | 0 | 0 | 0 | 0 |  |
| **Total** | **827** | **792** | **754** | **33** | **39** | **4** | **0** | |

Tables the jar writes that the reference does not have: `logical_keys` (8 rows): new table (difference 29); `logical_level_keys` (24 rows): new table (difference 30); `lts_joins` (2 rows): new table (difference 34); `lts_tables` (18 rows): new table (difference 34); `physical_keys` (14 rows): new table (difference 28); `schema` (378 rows): new table (difference 16); `schema_tables` (46 rows): new table (difference 16).

Difference 1 applies to every table: 108 `..._guarded` columns in 36 reference tables are not in the generated tables, and 0 cells of this estate carry the guard apostrophe in the reference. They are not counted in the columns above.

### bi-analytics-2013

| Table | Reference rows | Generated rows | Identical | Differ only as explained | Missing | Extra | Unexplained | Explained by |
|---|---:|---:|---:|---:|---:|---:|---:|---|
| `aggregation_rules` | 22 | 22 | 22 | 0 | 0 | 0 | 0 |  |
| `analyses` | 0 | 0 | 0 | 0 | 0 | 0 | 0 |  |
| `analysis_columns` | 0 | 0 | 0 | 0 | 0 | 0 | 0 |  |
| `analysis_criteria` | 0 | 0 | 0 | 0 | 0 | 0 | 0 |  |
| `analysis_filters` | 0 | 0 | 0 | 0 | 0 | 0 | 0 | 10: 2 added columns |
| `analysis_prompts` | 0 | 0 | 0 | 0 | 0 | 0 | 0 |  |
| `analysis_views` | 0 | 0 | 0 | 0 | 0 | 0 | 0 |  |
| `bmm_joins` | 75 | 75 | 75 | 0 | 0 | 0 | 0 |  |
| `catalog_versions` | 0 | 0 | 0 | 0 | 0 | 0 | 0 |  |
| `coverage` | 28 | 31 | 27 | 0 | 1 | 4 | 0 | 5: 1 missing; 28: 1 extra; 29: 1 extra; 34: 2 extra |
| `coverage_summary` | 0 | 0 | 0 | 0 | 0 | 0 | 0 |  |
| `dashboard_contents` | 0 | 0 | 0 | 0 | 0 | 0 | 0 |  |
| `dashboard_pages` | 0 | 0 | 0 | 0 | 0 | 0 | 0 |  |
| `dashboards` | 0 | 0 | 0 | 0 | 0 | 0 | 0 |  |
| `errors` | 2 | 0 | 0 | 0 | 2 | 0 | 0 | 5: 2 missing |
| `init_blocks` | 152 | 152 | 152 | 0 | 0 | 0 | 0 |  |
| `lineage` | 1,501 | 1,501 | 1,501 | 0 | 0 | 0 | 0 |  |
| `logical_columns` | 933 | 933 | 924 | 9 | 0 | 0 | 0 | 5: 2 cells; 6: 7 cells; 37: 1 added columns |
| `logical_dimensions` | 20 | 20 | 20 | 0 | 0 | 0 | 0 | 32: 1 added columns |
| `logical_levels` | 84 | 84 | 84 | 0 | 0 | 0 | 0 | 35: 2 added columns |
| `logical_table_sources` | 75 | 75 | 75 | 0 | 0 | 0 | 0 | 36: 1 added columns |
| `logical_tables` | 71 | 71 | 71 | 0 | 0 | 0 | 0 | 37: 1 added columns |
| `lts_column_map` | 940 | 940 | 940 | 0 | 0 | 0 | 0 |  |
| `manifest` | 7 | 7 | 2 | 4 | 0 | 0 | 0 | 15: 1 row; 16: 1 row; 17: 2 rows; `extracted_at`, 1 row, is the time of the run and is counted in no column |
| `physical_columns` | 957 | 957 | 957 | 0 | 0 | 0 | 0 | 31: 2 added columns; 37: 1 added columns |
| `physical_joins` | 82 | 82 | 0 | 82 | 0 | 0 | 0 | 7: 82 cells; 26: 82 cells, 1 added columns |
| `physical_table_definitions` | 32 | 32 | 32 | 0 | 0 | 0 | 0 |  |
| `physical_tables` | 78 | 78 | 78 | 0 | 0 | 0 | 0 | 36: 4 added columns; 37: 1 added columns |
| `presentation_columns` | 1,484 | 1,484 | 1,484 | 0 | 0 | 0 | 0 |  |
| `presentation_hierarchies` | 53 | 53 | 53 | 0 | 0 | 0 | 0 |  |
| `presentation_level_columns` | 183 | 183 | 183 | 0 | 0 | 0 | 0 |  |
| `presentation_levels` | 226 | 226 | 226 | 0 | 0 | 0 | 0 |  |
| `presentation_subject_areas` | 18 | 18 | 18 | 0 | 0 | 0 | 0 |  |
| `presentation_tables` | 118 | 118 | 118 | 0 | 0 | 0 | 0 |  |
| `report_lineage` | 0 | 0 | 0 | 0 | 0 | 0 | 0 |  |
| `saved_filters` | 0 | 0 | 0 | 0 | 0 | 0 | 0 |  |
| `security_rls` | 189 | 189 | 189 | 0 | 0 | 0 | 0 | 27: 2 added columns |
| `unparsed` | 238 | 211 | 204 | 6 | 28 | 1 | 0 | 19: 1 extra; 26: 1 missing; 27: 2 cells; 28: 5 missing; 29: 6 missing; 30: 2 cells, 4 missing; 31: 1 missing; 32: 1 missing; 34: 8 missing; 35: 2 cells, 1 missing; 37: 1 missing |
| `variables` | 343 | 343 | 342 | 1 | 0 | 0 | 0 | 19: 3 cells |
| **Total** | **7,911** | **7,885** | **7,777** | **102** | **31** | **5** | **0** | |

Tables the jar writes that the reference does not have: `logical_keys` (57 rows): new table (difference 29); `logical_level_keys` (127 rows): new table (difference 30); `lts_joins` (36 rows): new table (difference 34); `lts_tables` (93 rows): new table (difference 34); `physical_keys` (63 rows): new table (difference 28); `schema` (378 rows): new table (difference 16); `schema_tables` (46 rows): new table (difference 16).

Difference 1 applies to every table: 108 `..._guarded` columns in 36 reference tables are not in the generated tables, and 0 cells of this estate carry the guard apostrophe in the reference. They are not counted in the columns above.

### sampleapp-v607

| Table | Reference rows | Generated rows | Identical | Differ only as explained | Missing | Extra | Unexplained | Explained by |
|---|---:|---:|---:|---:|---:|---:|---:|---|
| `aggregation_rules` | 0 | 0 | 0 | 0 | 0 | 0 | 0 |  |
| `analyses` | 1,607 | 1,607 | 1,607 | 0 | 0 | 0 | 0 |  |
| `analysis_columns` | 11,143 | 11,143 | 11,128 | 15 | 0 | 0 | 0 | 8: 15 cells |
| `analysis_criteria` | 1,789 | 1,789 | 1,789 | 0 | 0 | 0 | 0 |  |
| `analysis_filters` | 7,135 | 8,495 | 6,695 | 440 | 0 | 1,360 | 0 | 8: 440 cells; 9: 1,360 extra; 10: 2 added columns |
| `analysis_prompts` | 4 | 4 | 4 | 0 | 0 | 0 | 0 |  |
| `analysis_views` | 10,879 | 10,879 | 10,879 | 0 | 0 | 0 | 0 |  |
| `bmm_joins` | 0 | 0 | 0 | 0 | 0 | 0 | 0 |  |
| `catalog_versions` | 60 | 60 | 60 | 0 | 0 | 0 | 0 |  |
| `coverage` | 65 | 65 | 63 | 2 | 0 | 0 | 0 | 9: 1 cell; 11: 1 cell |
| `coverage_summary` | 5 | 5 | 5 | 0 | 0 | 0 | 0 |  |
| `dashboard_contents` | 3,718 | 3,718 | 3,718 | 0 | 0 | 0 | 0 |  |
| `dashboard_pages` | 1,036 | 1,036 | 1,036 | 0 | 0 | 0 | 0 |  |
| `dashboards` | 111 | 111 | 111 | 0 | 0 | 0 | 0 |  |
| `errors` | 1,552 | 1,185 | 1,166 | 0 | 386 | 19 | 0 | 9: 19 extra; 11: 386 missing |
| `init_blocks` | 0 | 0 | 0 | 0 | 0 | 0 | 0 |  |
| `lineage` | 0 | 0 | 0 | 0 | 0 | 0 | 0 |  |
| `logical_columns` | 0 | 0 | 0 | 0 | 0 | 0 | 0 | 37: 1 added columns |
| `logical_dimensions` | 0 | 0 | 0 | 0 | 0 | 0 | 0 | 32: 1 added columns |
| `logical_levels` | 0 | 0 | 0 | 0 | 0 | 0 | 0 | 35: 2 added columns |
| `logical_table_sources` | 0 | 0 | 0 | 0 | 0 | 0 | 0 | 36: 1 added columns |
| `logical_tables` | 0 | 0 | 0 | 0 | 0 | 0 | 0 | 37: 1 added columns |
| `lts_column_map` | 0 | 0 | 0 | 0 | 0 | 0 | 0 |  |
| `manifest` | 7 | 7 | 2 | 4 | 0 | 0 | 0 | 15: 1 row; 16: 1 row; 17: 2 rows; `extracted_at`, 1 row, is the time of the run and is counted in no column |
| `physical_columns` | 0 | 0 | 0 | 0 | 0 | 0 | 0 | 31: 2 added columns; 37: 1 added columns |
| `physical_joins` | 0 | 0 | 0 | 0 | 0 | 0 | 0 | 26: 1 added columns |
| `physical_table_definitions` | 0 | 0 | 0 | 0 | 0 | 0 | 0 |  |
| `physical_tables` | 0 | 0 | 0 | 0 | 0 | 0 | 0 | 36: 4 added columns; 37: 1 added columns |
| `presentation_columns` | 0 | 0 | 0 | 0 | 0 | 0 | 0 |  |
| `presentation_hierarchies` | 0 | 0 | 0 | 0 | 0 | 0 | 0 |  |
| `presentation_level_columns` | 0 | 0 | 0 | 0 | 0 | 0 | 0 |  |
| `presentation_levels` | 0 | 0 | 0 | 0 | 0 | 0 | 0 |  |
| `presentation_subject_areas` | 0 | 0 | 0 | 0 | 0 | 0 | 0 |  |
| `presentation_tables` | 0 | 0 | 0 | 0 | 0 | 0 | 0 |  |
| `report_lineage` | 11,143 | 11,143 | 11,128 | 15 | 0 | 0 | 0 | 8: 15 cells |
| `saved_filters` | 96 | 96 | 93 | 3 | 0 | 0 | 0 | 8: 3 cells |
| `security_rls` | 0 | 0 | 0 | 0 | 0 | 0 | 0 | 27: 2 added columns |
| `unparsed` | 1,050 | 1,053 | 1,046 | 1 | 3 | 6 | 0 | 8: 1 cell; 9: 3 missing; 21: 6 extra |
| `variables` | 0 | 0 | 0 | 0 | 0 | 0 | 0 |  |
| **Total** | **51,400** | **52,396** | **50,530** | **480** | **389** | **1,385** | **0** | |

Tables the jar writes that the reference does not have: `logical_keys` (0 rows): new table (difference 29); `logical_level_keys` (0 rows): new table (difference 30); `lts_joins` (0 rows): new table (difference 34); `lts_tables` (0 rows): new table (difference 34); `physical_keys` (0 rows): new table (difference 28); `schema` (378 rows): new table (difference 16); `schema_tables` (46 rows): new table (difference 16).

Difference 1 applies to every table: 108 `..._guarded` columns in 36 reference tables are not in the generated tables, and 1,403 cells of this estate carry the guard apostrophe in the reference. They are not counted in the columns above.

### `manifest.csv`, key by key

The reference manifest was written once for all three estates by the Python program; each generated manifest is for one estate. Lines of the reference about other estates are left out, as rows of other estates are in every table.

| Estate | Key | Reference | Generated | Verdict |
|---|---|---|---|---|
| `sampleapp` | `extractor_version` | `0.2.0` | `0.1.0` | explained by difference 17: each program's own version number |
| `sampleapp` | `extracted_at` | `2026-11-18T09:32:58Z` | `2026-11-24T02:56:24Z` | not comparable: the time each program was run |
| `sampleapp` | `corpus_root` | `/home/kamal/Documents/Garage/obiee-corpus/clean` | `estates/sampleapp` | explained by difference 17: the folder each program was given |
| `sampleapp` | `rpd_ids` | `bi-analytics-2013 sampleapp sampleapp-v607` | `sampleapp` | explained by difference 15: one estate per run; the reference's list contains it |
| `sampleapp` | `halves_present:sampleapp` | `rpd` | `rpd` | identical |
| `sampleapp` | `pairing:sampleapp` | `not paired: only the rpd half was supplied` | `not paired: only the rpd half was supplied` | identical |
| `sampleapp` | `tables` | `aggregation_rules analyses analysis_columns analysis_criteri ...(614 characters in all)` | `aggregation_rules analyses analysis_columns analysis_criteri ...(702 characters in all)` | explained by difference 16: the reference's list plus `schema` and `schema_tables` (16) and the new tables [logical_keys, logical_level_keys, lts_joins, lts_tables, physical_keys] (differences 28 to 30 and 34), each checked row by row against the estate's files |
| `bi-analytics-2013` | `extractor_version` | `0.2.0` | `0.1.0` | explained by difference 17: each program's own version number |
| `bi-analytics-2013` | `extracted_at` | `2026-11-18T09:32:58Z` | `2026-11-24T02:56:26Z` | not comparable: the time each program was run |
| `bi-analytics-2013` | `corpus_root` | `/home/kamal/Documents/Garage/obiee-corpus/clean` | `estates/bi-analytics-2013` | explained by difference 17: the folder each program was given |
| `bi-analytics-2013` | `rpd_ids` | `bi-analytics-2013 sampleapp sampleapp-v607` | `bi-analytics-2013` | explained by difference 15: one estate per run; the reference's list contains it |
| `bi-analytics-2013` | `halves_present:bi-analytics-2013` | `rpd` | `rpd` | identical |
| `bi-analytics-2013` | `pairing:bi-analytics-2013` | `not paired: only the rpd half was supplied` | `not paired: only the rpd half was supplied` | identical |
| `bi-analytics-2013` | `tables` | `aggregation_rules analyses analysis_columns analysis_criteri ...(614 characters in all)` | `aggregation_rules analyses analysis_columns analysis_criteri ...(702 characters in all)` | explained by difference 16: the reference's list plus `schema` and `schema_tables` (16) and the new tables [logical_keys, logical_level_keys, lts_joins, lts_tables, physical_keys] (differences 28 to 30 and 34), each checked row by row against the estate's files |
| `sampleapp-v607` | `extractor_version` | `0.2.0` | `0.1.0` | explained by difference 17: each program's own version number |
| `sampleapp-v607` | `extracted_at` | `2026-11-18T09:32:58Z` | `2026-11-24T02:56:31Z` | not comparable: the time each program was run |
| `sampleapp-v607` | `corpus_root` | `/home/kamal/Documents/Garage/obiee-corpus/clean` | `estates/sampleapp-v607` | explained by difference 17: the folder each program was given |
| `sampleapp-v607` | `rpd_ids` | `bi-analytics-2013 sampleapp sampleapp-v607` | `sampleapp-v607` | explained by difference 15: one estate per run; the reference's list contains it |
| `sampleapp-v607` | `halves_present:sampleapp-v607` | `catalog` | `catalog` | identical |
| `sampleapp-v607` | `pairing:sampleapp-v607` | `not paired: only the catalog half was supplied` | `not paired: only the catalog half was supplied` | identical |
| `sampleapp-v607` | `tables` | `aggregation_rules analyses analysis_columns analysis_criteri ...(614 characters in all)` | `aggregation_rules analyses analysis_columns analysis_criteri ...(702 characters in all)` | explained by difference 16: the reference's list plus `schema` and `schema_tables` (16) and the new tables [logical_keys, logical_level_keys, lts_joins, lts_tables, physical_keys] (differences 28 to 30 and 34), each checked row by row against the estate's files |

## 3. The deliberate differences that occurred

The numbers are those of the list in `docs/port-guide/00-read-me-first.md`, which gives the old behaviour, the new behaviour and the reason for each.

### Difference 1

The reference has a `..._guarded` column beside every text column and puts an apostrophe in front of a value a spreadsheet could alter; the jar writes plain CSV with neither.

**Accounts for:**

- `sampleapp`: 108 guard columns in 36 tables; 0 cells with the apostrophe.
- `bi-analytics-2013`: 108 guard columns in 36 tables; 0 cells with the apostrophe.
- `sampleapp-v607`: 108 guard columns in 36 tables; 1,403 cells with the apostrophe.

**How it was checked:** the guard columns are left out and the apostrophe taken off before comparing; the values must then be equal, and are counted under "identical" when they are.

**Examples** (reference, then generated):

- `analysis_columns`, row `sampleapp-v607::cat::00. va projects/general examples/infographic u.s auto_062416/_projectdefn#c0.12`, column `column_id`: reference `'060` with `column_id_guarded` = `true`; generated `060`, and no `column_id_guarded` column.
- `analysis_filters`, row `sampleapp-v607::cat::01. quickstart/new features/11.1.1.7.1/line geometries thickness#f0.0`, column `expr_text`: reference ` '\t\t\t\t\n             ` with `expr_text_guarded` = `true`; generated ` \t\t\t\t\n            \n\t\t\t\t\n             `, and no `expr_text_guarded` column.
- `report_lineage`, row `sampleapp-v607::cat::01. quickstart/new features/11.1.1.9.0/global variables#c0.3#lineage`, column `reference_text`: reference `'@{global.variables.Cost_Percent_Variation}` with `reference_text_guarded` = `true`; generated `@{global.variables.Cost_Percent_Variation}`, and no `reference_text_guarded` column.

### Difference 5

The reference resolved references while it was still reading, so a reference to something read later was dropped and reported as an error; the jar resolves after reading everything, so the reference is filled in and the error row is gone.

**Accounts for:**

- `sampleapp`: `coverage` 1 reference row gone; `errors` 2 reference rows gone; `logical_columns` 2 cells.
- `bi-analytics-2013`: `coverage` 1 reference row gone; `errors` 2 reference rows gone; `logical_columns` 2 cells.

**How it was checked:** for a cell, the reference's list must be the generated list with entries taken out, in the same order, and the reference's own `errors.csv` must report at least that many unresolved references for that object. For an error row that is gone, the generated tables must hold that many more resolved references for the object. For the `coverage` row, its count must equal the number of such error rows.

**Examples** (reference, then generated):

- `coverage`: reference has this row, generated does not: `layer=rpd, object_type=ExpressionRef, parsed=0, skipped=2`.
- `errors`: reference has this row, generated does not: `kind=unresolved_reference, source_id=sampleapp::bmm::SAMPLESALES.Dim Products.Prod Dsc (Translated), object_type=ExpressionRef, reason=refer ...(213 characters in all)`.
- `logical_columns`, row `sampleapp::bmm::SAMPLESALES.Dim Products.Prod Dsc (Translated)`, column `derived_expr_refs`: reference `sampleapp::bmm::SAMPLESALES.Dim Products.Prod Dsc sampleapp::bmm::SAMPLESALES.Dim Products.Prod Key`; generated `sampleapp::bmm::SAMPLESALES.Dim Products (Translated).PROD_DESC\nsampleapp::bmm::SAMPLESALES.Dim Products.Prod Dsc\nsampleapp::bmm::SAMPLESA ...(163 characters in all)`.

### Difference 6

A list of references in one cell (`..._refs`) was joined with spaces in the reference; the jar writes one reference per line.

**Accounts for:**

- `sampleapp`: `logical_table_sources` 3 cells.
- `bi-analytics-2013`: `logical_columns` 7 cells.

**How it was checked:** the reference cell is split where an identifier starts (the text `<estate>::<layer>::`, which cannot occur inside an identifier) and must give exactly the generated cell's lines, in order.

**Examples** (reference, then generated):

- `logical_table_sources`, row `sampleapp::bmm::SAMPLESALES.Fact Revenue.src.Aggregate Revenue`, column `group_by_refs`: reference `sampleapp::bmm::SAMPLESALES.Time Hierarchy.Month sampleapp::bmm::SAMPLESALES.Products Hierararchy.Product Detail`; generated `sampleapp::bmm::SAMPLESALES.Time Hierarchy.Month\nsampleapp::bmm::SAMPLESALES.Products Hierararchy.Product Detail`.
- `logical_columns`, row `bi-analytics-2013::bmm::VASILI_KORZUN_DW.DIM_TIMES.YEAR_MONTH`, column `derived_expr_refs`: reference `bi-analytics-2013::bmm::VASILI_KORZUN_DW.DIM_TIMES.CALENDAR_YEAR bi-analytics-2013::bmm::VASILI_KORZUN_DW.DIM_TIMES.CALENDAR_MONTH_NUMBER`; generated `bi-analytics-2013::bmm::VASILI_KORZUN_DW.DIM_TIMES.CALENDAR_YEAR\nbi-analytics-2013::bmm::VASILI_KORZUN_DW.DIM_TIMES.CALENDAR_MONTH_NUMBER`.
- `logical_table_sources`, row `sampleapp::bmm::SAMPLESALES.Fact Revenue.src.SAMP_REVENUE_F`, column `group_by_refs`: reference `sampleapp::bmm::SAMPLESALES.Sales Rep Hierarchy.Detail sampleapp::bmm::SAMPLESALES.Time Hierarchy.Dim Time Detail sampleapp::bmm::SAMPLESALE ...(236 characters in all)`; generated `sampleapp::bmm::SAMPLESALES.Sales Rep Hierarchy.Detail\nsampleapp::bmm::SAMPLESALES.Time Hierarchy.Dim Time Detail\nsampleapp::bmm::SAMPLESA ...(236 characters in all)`.

### Difference 7

`physical_joins.columns` held OBIEE's internal ids in the reference; the jar writes Row identifiers.

**Accounts for:**

- `sampleapp`: `physical_joins` 18 cells.
- `bi-analytics-2013`: `physical_joins` 82 cells.

**How it was checked:** each internal id in the reference cell is looked up in the estate's repository files by the comparison itself; the Row identifier built from the names found there must be the generated entry in the same position, and a row of the generated `physical_columns`.

**Examples** (reference, then generated):

- `physical_joins`, row `sampleapp::phys::xe.SAMPLESALES.EMP_PARENT_CHILD.EMP_PARENT_CHILD_FKey`, column `columns`: reference `m00000835-0bbb-0000-714b-e31d00000000`; generated `sampleapp::phys::xe.SAMPLESALES.EMP_PARENT_CHILD.ANCESTOR_KEY`.
- `physical_joins`, row `sampleapp::phys::xe.SAMPLESALES.POP_POPULATION.CITY_ID`, column `columns`: reference `meb1c85b7-0c97-1000-ac27-c0a838650000`; generated `sampleapp::phys::xe.SAMPLESALES.POP_POPULATION.CITY_ID`.
- `physical_joins`, row `sampleapp::phys::xe.SAMPLESALES.POP_POPULATION.STATE_ID`, column `columns`: reference `meb1c85b9-0c97-1000-ac27-c0a838650000`; generated `sampleapp::phys::xe.SAMPLESALES.POP_POPULATION.STATE_ID`.

### Difference 8

The reference stored a formula or filter text only up to its first nested element; the jar stores all of it.

**Accounts for:**

- `sampleapp-v607`: `analysis_columns` 15 cells; `analysis_filters` 440 cells; `report_lineage` 15 cells; `saved_filters` 3 cells; `unparsed` 1 cell.

**How it was checked:** the estate's catalog file is read again with the JDK's DOM parser and the element the row stands for is found. The reference value must be that element's text before its first nested element, and the generated value its whole text (a formula) or its own text without nested expressions (a filter). `report_lineage.reference_text` is a copy of the report column's formula: it must equal `analysis_columns.formula` on each side, and that difference must itself pass the check. A count in `unparsed` must drop by exactly the number of nested texts counted in the files.

**Examples** (reference, then generated):

- `analysis_columns`, row `sampleapp-v607::cat::03. mobile/tablet dark/bar_2#c0.0`, column `formula`: reference (empty); generated `"Products"."P4  Brand""Products"."P4  Brand"BizTech1-Billing"Products"."P4  Brand"FunPod3-Other2-Support`.
- `analysis_filters`, row `sampleapp-v607::cat::01. quickstart/new features/11.1.1.7.1/line geometries thickness#f0.0`, column `expr_text`: reference ` \t\t\t\t\n             `; generated ` \t\t\t\t\n            \n\t\t\t\t\n             `.
- `report_lineage`, row `sampleapp-v607::cat::03. mobile/tablet dark/bar_2#c0.0#lineage`, column `reference_text`: reference (empty); generated `"Products"."P4  Brand""Products"."P4  Brand"BizTech1-Billing"Products"."P4  Brand"FunPod3-Other2-Support`.

### Difference 9

The reference kept only the outermost expression of a saved filter; the jar writes every expression, resolves the references between saved filters, and no longer lists that content as unread.

**Accounts for:**

- `sampleapp-v607`: `analysis_filters` 1,360 rows added; `coverage` 1 cell; `errors` 19 rows added; `unparsed` 3 reference rows gone.

**How it was checked:** an added `analysis_filters` row must be, column for column, what the saved filter's file holds at that position, and the saved filter must have exactly as many rows as it has expressions. An added error row must belong to such a row whose reference did not resolve. The `coverage` count must rise by exactly the number of those error rows, and the `unparsed` counts must fall by exactly what the files hold.

**Examples** (reference, then generated):

- `analysis_filters`: reference has no such row, generated has it: `row_id=sampleapp-v607::cat::01. quickstart/_filters/a - sample sales/short prompted filters#f0, scope=saved_filter, owner_id=sampleapp-v607: ...(391 characters in all)`.
- `coverage`, row `layer=cat, object_type={com.siebel.analytics.web/expression/v1.1}expr`, column `skipped`: reference `176`; generated `195`.
- `errors`: reference has no such row, generated has it: `kind=unresolved_reference, source_id=03. mobile/_filters/y13 - nfl demo/all prompted filters, object_type={com.siebel.analytics.web/expressi ...(305 characters in all)`.

### Difference 10

`analysis_filters` has two more columns, `parent_id` and `depth`, so the grouping of conditions can be rebuilt.

**Accounts for:**

- `sampleapp`: `analysis_filters` 2 columns added, on 0 rows.
- `bi-analytics-2013`: `analysis_filters` 2 columns added, on 0 rows.
- `sampleapp-v607`: `analysis_filters` 2 columns added, on 8,495 rows.

**How it was checked:** parent and depth are worked out again from the estate's files for every row; one wrong value and both columns would be listed as unexplained.

**Examples** (reference, then generated):

- `analysis_filters`, row `sampleapp-v607::cat::01. quickstart/_filters/a - sample sales/short prompted filters#f0`: the reference has no such columns; generated `parent_id` = (empty), `depth` = `0`.
- `analysis_filters`, row `sampleapp-v607::cat::01. quickstart/_filters/a - sample sales/short prompted filters#f1`: the reference has no such columns; generated `parent_id` = `sampleapp-v607::cat::01. quickstart/_filters/a - sample sales/short prompted filters#f0`, `depth` = `1`.
- `analysis_filters`, row `sampleapp-v607::cat::01. quickstart/_filters/a - sample sales/short prompted filters#f11`: the reference has no such columns; generated `parent_id` = `sampleapp-v607::cat::01. quickstart/_filters/a - sample sales/short prompted filters#f10`, `depth` = `2`.

### Difference 11

The reference resolved each dashboard prompt reference twice, doubling its error rows and skipped counts; the jar resolves it once.

**Accounts for:**

- `sampleapp-v607`: `coverage` 1 cell; `errors` 386 reference rows gone.

**How it was checked:** the reference must hold exactly twice as many copies of the very same error row as the generated table, and the generated `dashboard_contents` must have one unresolved placement for each generated error of that page. The `coverage` count must be exactly halved.

**Examples** (reference, then generated):

- `coverage`, row `layer=cat, object_type={com.siebel.analytics.web/dashboard/v1.1}globalFilterView`, column `skipped`: reference `772`; generated `386`.
- `errors`: reference has this row, generated does not: `kind=unresolved_reference, source_id=06. dashboard design/_portal/6.60 accessibility/accessibility designs, object_type={com.siebel.analytic ...(371 characters in all)`.
- `errors`: reference has this row, generated does not: `kind=unresolved_reference, source_id=06. dashboard design/_portal/6.60 accessibility/designing for sighted accessibility users, object_type= ...(391 characters in all)`.

### Difference 15

The reference was written by one run over three estates; the jar is run once per estate, so its manifest names one estate.

**Accounts for:**

- `sampleapp`: `manifest` 1 row.
- `bi-analytics-2013`: `manifest` 1 row.
- `sampleapp-v607`: `manifest` 1 row.

**How it was checked:** the generated `rpd_ids` must be the one estate, and the reference's list must contain it.

**Examples** (reference, then generated):

- `manifest` of `sampleapp`, key `rpd_ids`: reference `bi-analytics-2013 sampleapp sampleapp-v607`; generated `sampleapp`.
- `manifest` of `bi-analytics-2013`, key `rpd_ids`: reference `bi-analytics-2013 sampleapp sampleapp-v607`; generated `bi-analytics-2013`.
- `manifest` of `sampleapp-v607`, key `rpd_ids`: reference `bi-analytics-2013 sampleapp sampleapp-v607`; generated `sampleapp-v607`.

### Difference 16

The jar writes two more tables, `schema_tables` and `schema`, which describe the Knowledge Base; the reference has no such tables.

**Accounts for:**

- `sampleapp`: `manifest` 1 row; new table `schema`, 378 rows; new table `schema_tables`, 46 rows.
- `bi-analytics-2013`: `manifest` 1 row; new table `schema`, 378 rows; new table `schema_tables`, 46 rows.
- `sampleapp-v607`: `manifest` 1 row; new table `schema`, 378 rows; new table `schema_tables`, 46 rows.

**How it was checked:** the generated table list must be the reference's list plus exactly `schema` and `schema_tables`. The content of these two tables has no reference to compare with; the project's own tests check it (`SchemaTest`).

**Examples** (reference, then generated):

- `manifest` of `sampleapp`, key `tables`: the generated list is the reference's list with `schema` and `schema_tables` added.
- Reference: no such table. Generated `schema_tables`, first row: `table=physical_tables, description=The database tables the repository knows about, including aliases and tables that are really SQL statements., row_meaning=One physical table object in the repository., key_column=row_id, layer=phys`.
- Reference: no such table. Generated `schema`, first row: `table=physical_tables, position=1, column=row_id, meaning=Row identifier: <estate>::phys::<container>.<table name>., is_key=true`.

### Difference 17

The manifest describes the program that wrote it: its own version number and the estate folder it was given.

**Accounts for:**

- `sampleapp`: `manifest` 2 rows.
- `bi-analytics-2013`: `manifest` 2 rows.
- `sampleapp-v607`: `manifest` 2 rows.

**How it was checked:** `extractor_version` and `corpus_root` are expected to differ; `corpus_root` must end in the estate name. Nothing else in the manifest may differ under this number.

**Examples** (reference, then generated):

- `manifest` of `sampleapp`, key `extractor_version`: reference `0.2.0`; generated `0.1.0`.
- `manifest` of `sampleapp`, key `corpus_root`: reference `/home/kamal/Documents/Garage/obiee-corpus/clean`; generated `estates/sampleapp`.
- `manifest` of `bi-analytics-2013`, key `extractor_version`: reference `0.2.0`; generated `0.1.0`.

### Difference 19

The reference wrote out a variable's value when the same value was a credential under another variable's name; the jar withholds it and records that it did.

**Accounts for:**

- `bi-analytics-2013`: `unparsed` 1 row added; `variables` 3 cells.

**How it was checked:** the generated row must be marked `redacted` with empty value cells where the reference row is not, and the value the reference wrote must be, in the estate's own files, the value of a variable whose name indicates a credential. The added `unparsed` row must record that variable.

**Examples** (reference, then generated):

- `unparsed`, row `kind=redacted_value, object_type=Variable`: reference has no such row, generated has it. (Content not shown.)
- `variables`, row `bi-analytics-2013::var::internal - do not ship - REP OLAP USER`, column `redacted`: the reference holds a value, the generated cell is `true`. (Value not shown.)
- `variables`, row `bi-analytics-2013::var::internal - do not ship - REP OLAP USER`, column `expr_text`: the reference holds a value, the generated cell is empty. (Value not shown.)

### Difference 21

A catalog file whose name cannot be fully decoded was used without comment by the reference; the jar adds a row to `unparsed` naming it.

**Accounts for:**

- `sampleapp-v607`: `unparsed` 6 rows added.

**How it was checked:** the file must be in the estate's catalog, and the JDK's own URL decoder must also find its name malformed.

**Examples** (reference, then generated):

- `unparsed`: reference has no such row, generated has it: `kind=undecoded_name, object_type=File, detail=05%2e+published+reporting/a%2e+overview/sin+t%edtulo%2exdo/_datamodel%2exdm — the name holds a ...(261 characters in all)`.
- `unparsed`: reference has no such row, generated has it: `kind=undecoded_name, object_type=File, detail=05%2e+published+reporting/a%2e+overview/sin+t%edtulo%2exdo/_report%2exdo — the name holds an e ...(255 characters in all)`.
- `unparsed`: reference has no such row, generated has it: `kind=undecoded_name, object_type=File, detail=05%2e+published+reporting/a%2e+overview/sin+t%edtulo%2exdo/sample%2exmlsample — the name holds ...(265 characters in all)`.

### Difference 26

A foreign key row in the reference did not say which table it points at; the jar follows the foreign key to the key it refers to, fills `physical_joins.table2_id`, adds the column `referenced_columns`, and no longer lists that link as unread. (The key itself, which this difference was the first to read, is a row of its own since difference 28.)

**Accounts for:**

- `sampleapp`: `physical_joins` 18 cells, 1 columns added, on 22 rows; `unparsed` 1 reference row gone.
- `bi-analytics-2013`: `physical_joins` 82 cells, 1 columns added, on 82 rows; `unparsed` 1 reference row gone.
- `sampleapp-v607`: `physical_joins` 1 columns added, on 0 rows.

**How it was checked:** the comparison reads the estate's repository files itself. For `table2_id` the reference cell must be empty on a foreign key row, and the generated value must be the Row identifier, built from the names in the files, of the table that holds the key the foreign key's link names; it must be a row of the generated `physical_tables`. The added column is checked on every row: the key's columns in the key's own order for a foreign key, empty for a complex join. A count in `unparsed` must fall by exactly the number of links, keys, column lists and column references counted in the files.

**Examples** (reference, then generated):

- `physical_joins`: reference has no column `referenced_columns`; generated has it on every row.
- `physical_joins`, row `sampleapp::phys::xe.SAMPLESALES.EMP_PARENT_CHILD.EMP_PARENT_CHILD_FKey`, column `table2_id`: reference (empty); generated `sampleapp::phys::xe.SAMPLESALES.EMPLOYEE`.
- `unparsed`: reference has this row, generated does not: `kind=unread_attribute, object_type=PhysicalTable, detail=PhysicalForeignKey@counterPartKeyRef, occurrences=18`.

### Difference 27

The reference knew a role only by its name; the jar adds the role's display name and guid to `security_rls` (`role_display_name`, `role_guid`) and no longer lists them as unread.

**Accounts for:**

- `sampleapp`: `security_rls` 2 columns added, on 9 rows; `unparsed` 2 cells.
- `bi-analytics-2013`: `security_rls` 2 columns added, on 189 rows; `unparsed` 2 cells.
- `sampleapp-v607`: `security_rls` 2 columns added, on 0 rows.

**How it was checked:** the comparison reads the estate's repository files itself. On every query privilege row the two added cells must be the display name and guid in the file of the role the row's privilege package points at; on every other row they must be empty. A count in `unparsed` must fall by exactly the number of roles so pointed at that have the attribute.

**Examples** (reference, then generated):

- `security_rls`: reference has no column `role_display_name`; generated has it on every row.
- `security_rls`: reference has no column `role_guid`; generated has it on every row.
- `unparsed`, row `kind=unread_attribute, object_type=Group`, column `occurrences`: reference `6`; generated `5`.

### Difference 28

The reference read no key of a physical table and listed them as unread; the jar writes a new table, `physical_keys`, with one row per key (its table, its name, its columns in order), counts the keys in `coverage`, and no longer lists them as unread.

**Accounts for:**

- `sampleapp`: `coverage` 1 row added; `unparsed` 5 reference rows gone; new table `physical_keys`, 14 rows.
- `bi-analytics-2013`: `coverage` 1 row added; `unparsed` 5 reference rows gone; new table `physical_keys`, 63 rows.
- `sampleapp-v607`: new table `physical_keys`, 0 rows.

**How it was checked:** the comparison reads the estate's repository files itself (`KeySource`, the JDK's streaming parser, not the jar's reader). The new table must hold exactly one row for each key the files hold, no more and no fewer, and every cell must be what the files say: the Row identifier built from the names in the files, the owner, the key's name, its flags as written, and its columns one per line in the files' order. One wrong cell and the whole table is listed as unexplained. A count in `unparsed` is explained only when it is exactly the number of occurrences the files hold outside what the jar now reads, and lower than the reference's. The added `coverage` row must count exactly the keys in the files.

**Examples** (reference, then generated):

- Reference: no such table. Generated `physical_keys` of `sampleapp`, first row: `row_id=sampleapp::phys::xe.SAMPLESALES.EMPLOYEE.key.EMP_ID, table_id=sampleapp::phys::xe.SAMPLESALES.EMPLOYEE, name=EMP_ID, columns=sampleapp::phys::xe.SAMPLESALES.EMPLOYEE.EMP_ID`.
- `coverage`: reference has no such row, generated has it: `layer=rpd, object_type=PhysicalKey, parsed=14, skipped=0`.
- `unparsed`: reference has this row, generated does not: `kind=unread_attribute, object_type=PhysicalTable, detail=PhysicalKey@name, occurrences=14`.

### Difference 29

The reference read no key of a logical table and listed them as unread; the jar writes a new table, `logical_keys`, with one row per key (its logical table, its name, the primary flag as written, its columns in order), counts the keys in `coverage`, and no longer lists them as unread.

**Accounts for:**

- `sampleapp`: `coverage` 1 row added; `unparsed` 6 reference rows gone; new table `logical_keys`, 8 rows.
- `bi-analytics-2013`: `coverage` 1 row added; `unparsed` 6 reference rows gone; new table `logical_keys`, 57 rows.
- `sampleapp-v607`: new table `logical_keys`, 0 rows.

**How it was checked:** the comparison reads the estate's repository files itself (`KeySource`, the JDK's streaming parser, not the jar's reader). The new table must hold exactly one row for each key the files hold, no more and no fewer, and every cell must be what the files say: the Row identifier built from the names in the files, the owner, the key's name, its flags as written, and its columns one per line in the files' order. One wrong cell and the whole table is listed as unexplained. A count in `unparsed` is explained only when it is exactly the number of occurrences the files hold outside what the jar now reads, and lower than the reference's. The added `coverage` row must count exactly the keys in the files; keys of logical tables and of levels are both `LogicalKey` elements and share one row, reported under 29.

**Examples** (reference, then generated):

- Reference: no such table. Generated `logical_keys` of `sampleapp`, first row: `row_id=sampleapp::bmm::Densification - US Crime By State.US_CRIME_BY_STATE.key.PK_US_CRIME_BY_STATE, logical_table_id=sampleapp::bmm::Densification - US Crime By State.US_CRIME_BY_STATE, name=PK_US_CRIME_BY_STATE, is_primary=true, columns=sampleapp::bmm::Densification - US Crime By State.US_CRIME_BY_STATE.CRIME\nsampleapp::bmm::Densification - US Crime By State.US_CRIME_BY_STATE.TYPE_OF_CRIME\nsampleapp::bmm::Densifi ...(548 characters in all)`.
- `coverage`: reference has no such row, generated has it: `layer=rpd, object_type=LogicalKey, parsed=32, skipped=0`.
- `unparsed`: reference has this row, generated does not: `kind=unread_attribute, object_type=LogicalTable, detail=LogicalKey@isPrimary, occurrences=8`.

### Difference 30

Of the keys of a level the reference kept one column (`logical_levels.drill_key_column_id`) and listed the rest as unread; the jar also writes a new table, `logical_level_keys`, with one row per key (its level, its name, the primary, chronological and drill-down flags as written, all its columns in order), and no longer lists them as unread.

**Accounts for:**

- `sampleapp`: `unparsed` 2 cells, 4 reference rows gone; new table `logical_level_keys`, 24 rows.
- `bi-analytics-2013`: `unparsed` 2 cells, 4 reference rows gone; new table `logical_level_keys`, 127 rows.
- `sampleapp-v607`: new table `logical_level_keys`, 0 rows.

**How it was checked:** the comparison reads the estate's repository files itself (`KeySource`, the JDK's streaming parser, not the jar's reader). The new table must hold exactly one row for each key the files hold, no more and no fewer, and every cell must be what the files say: the Row identifier built from the names in the files, the owner, the key's name, its flags as written, and its columns one per line in the files' order. One wrong cell and the whole table is listed as unexplained. A count in `unparsed` is explained only when it is exactly the number of occurrences the files hold outside what the jar now reads, and lower than the reference's. The added `coverage` row must count exactly the keys in the files; keys of logical tables and of levels are both `LogicalKey` elements and share one row, reported under 29.

**Examples** (reference, then generated):

- Reference: no such table. Generated `logical_level_keys` of `sampleapp`, first row: `row_id=sampleapp::bmm::Population.RegionDim.City.key.CITY, level_id=sampleapp::bmm::Population.RegionDim.City, name=CITY, is_primary=true, is_for_drill_down=true, columns=sampleapp::bmm::Population.Region.City`.
- `unparsed`, row `kind=unread_attribute, object_type=Dimension`, column `occurrences`: reference `41`; generated `34`.
- `unparsed`: reference has this row, generated does not: `kind=unread_attribute, object_type=Dimension, detail=LogicalKey@isChronKey, occurrences=6`.

### Difference 31

The reference did not read a physical column's scale and wrote the columns in name order only; the jar adds `physical_columns.scale` and `physical_columns.position` (the column's place in its table in the repository) and no longer lists the scale as unread.

**Accounts for:**

- `sampleapp`: `physical_columns` 2 columns added, on 139 rows; `unparsed` 1 reference row gone.
- `bi-analytics-2013`: `physical_columns` 2 columns added, on 957 rows; `unparsed` 1 reference row gone.
- `sampleapp-v607`: `physical_columns` 2 columns added, on 0 rows.

**How it was checked:** the comparison reads the estate's repository files itself. On every row of `physical_columns` the scale must be the column's `scale` attribute in the file (empty when there is none) and the position its place among the columns of its table in the file, counting from 1. The `unparsed` row for the scale must be gone, or hold exactly the count the files leave outside what the jar reads.

**Examples** (reference, then generated):

- `physical_columns`, row `sampleapp::phys::xe.SAMPLESALES.EMPLOYEE.EMP_ID`: the reference has no such column; generated `position` = `1`.
- `physical_columns`, row `sampleapp::phys::xe.SAMPLESALES.SAMP_REVENUE_A.DISCNT_VALUE`: the reference has no such column; generated `scale` = `2`.
- `physical_columns`: reference has no column `scale`; generated has it on every row.

### Difference 32

The reference did not read a dimension's time flag; the jar adds `logical_dimensions.is_time` and no longer lists the flag as unread.

**Accounts for:**

- `sampleapp`: `logical_dimensions` 1 columns added, on 5 rows; `unparsed` 1 reference row gone.
- `bi-analytics-2013`: `logical_dimensions` 1 columns added, on 20 rows; `unparsed` 1 reference row gone.
- `sampleapp-v607`: `logical_dimensions` 1 columns added, on 0 rows.

**How it was checked:** the comparison reads the estate's repository files itself. On every row of `logical_dimensions` the value must be the dimension's `isTimeDim` attribute exactly as written, and empty when the file has none. The `unparsed` row for the flag must be gone.

**Examples** (reference, then generated):

- `logical_dimensions`, row `sampleapp::bmm::SAMPLESALES.Time Hierarchy`: the reference has no such column; generated `is_time` = `true`.
- `logical_dimensions`: reference has no column `is_time`; generated has it on every row.
- `unparsed`: reference has this row, generated does not: `kind=unread_attribute, object_type=Dimension, detail=Dimension@isTimeDim, occurrences=1`.

### Difference 34

The reference did not read which physical tables a table source is built on, or the joins it makes between them, and listed them as unread; the jar writes two new tables, `lts_tables` and `lts_joins` (join type and cardinality as written, and the physical join that gives the condition), counts them in `coverage`, and no longer lists them as unread.

**Accounts for:**

- `sampleapp`: `coverage` 2 rows added; `unparsed` 8 reference rows gone; new table `lts_joins`, 2 rows; new table `lts_tables`, 18 rows.
- `bi-analytics-2013`: `coverage` 2 rows added; `unparsed` 8 reference rows gone; new table `lts_joins`, 36 rows; new table `lts_tables`, 93 rows.
- `sampleapp-v607`: new table `lts_joins`, 0 rows; new table `lts_tables`, 0 rows.

**How it was checked:** the comparison reads the estate's repository files itself (`KeySource`). Each of the two new tables must hold exactly one row for each table and each join the files list under a source, with the Row identifier built from the names in the files, the join type and cardinality character for character, and the tables and the physical join as the identifiers their own names give. One wrong cell and the whole table is listed as unexplained. The two added `coverage` rows must count exactly the tables and joins in the files, and each `unparsed` row must be gone or hold exactly what the files leave outside what the jar reads.

**Examples** (reference, then generated):

- Reference: no such table. Generated `lts_joins` of `sampleapp`, first row: `row_id=sampleapp::bmm::SAMPLESALES.Dim Sales Rep.src.LTS Employee#table0.join0, source_id=sampleapp::bmm::SAMPLESALES.Dim Sales Rep.src.LTS Employee, source_table_id=sampleapp::bmm::SAMPLESALES.Dim Sales Rep.src.LTS Employee#table0, position=0, join_type=Inner, cardinality= ONE TO MANY, table1_id=sampleapp::phys::xe.SAMPLESALES.EMPLOYEE, table2_id=sampleapp::phys::xe.SAMPLESALES.EMP_PARENT_CHILD, physical_join_id=sam ...(487 characters in all)`.
- Reference: no such table. Generated `lts_tables` of `sampleapp`, first row: `row_id=sampleapp::bmm::Densification - US Crime By State.US_CRIME_BY_STATE.src.US_CRIME_BY_STATE#table0, source_id=sampleapp::bmm::Densification - US Crime By State.US_CRIME_BY_STATE.src.US_CRIME_BY_STATE, position=0, physical_table_id=sampleapp::phys::xe.SAMPLESALES.US_CRIME_BY_STATE`.
- `coverage`: reference has no such row, generated has it: `layer=rpd, object_type=Join, parsed=2, skipped=0`.

### Difference 35

The reference did not read which level is the child of which; the jar adds `logical_levels.child_level_ids` and `parent_level_ids` and no longer lists a level's list of children as unread.

**Accounts for:**

- `sampleapp`: `logical_levels` 2 columns added, on 22 rows; `unparsed` 2 cells, 1 reference row gone.
- `bi-analytics-2013`: `logical_levels` 2 columns added, on 84 rows; `unparsed` 2 cells, 1 reference row gone.
- `sampleapp-v607`: `logical_levels` 2 columns added, on 0 rows.

**How it was checked:** the comparison reads the estate's repository files itself (`KeySource`). On every row of the table the added cells must be what the files say: an attribute exactly as written, a text exactly as written, or the Row identifier built from the names of the element a link points at; a level's parents must be exactly the levels of its dimension that list it as a child, in the files' order. One wrong value and all the added columns of that table are listed as unexplained. An `unparsed` count is explained only when it is exactly what the files leave outside what the jar now reads.

**Examples** (reference, then generated):

- `logical_levels`, row `sampleapp::bmm::Population.RegionDim.Grand Total`: the reference has no such column; generated `child_level_ids` = `sampleapp::bmm::Population.RegionDim.State`.
- `logical_levels`, row `sampleapp::bmm::Population.RegionDim.City`: the reference has no such column; generated `parent_level_ids` = `sampleapp::bmm::Population.RegionDim.State`.
- `logical_levels`: reference has no column `child_level_ids`; generated has it on every row.

### Difference 36

The reference did not read what defines a parent-child hierarchy; the jar adds the four columns of the relationship table to `physical_tables` (`member_key_column_id`, `parent_key_column_id`, `distance_column_id`, `leaf_column_id`) and the table a source names to `logical_table_sources` (`closure_table_id`), and no longer lists them as unread.

**Accounts for:**

- `sampleapp`: `logical_table_sources` 1 columns added, on 17 rows; `physical_tables` 4 columns added, on 22 rows; `unparsed` 5 reference rows gone.
- `bi-analytics-2013`: `logical_table_sources` 1 columns added, on 75 rows; `physical_tables` 4 columns added, on 78 rows.
- `sampleapp-v607`: `logical_table_sources` 1 columns added, on 0 rows; `physical_tables` 4 columns added, on 0 rows.

**How it was checked:** the comparison reads the estate's repository files itself (`KeySource`). On every row of the table the added cells must be what the files say: an attribute exactly as written, a text exactly as written, or the Row identifier built from the names of the element a link points at. One wrong value and all the added columns of that table are listed as unexplained. An `unparsed` count is explained only when it is exactly what the files leave outside what the jar now reads.

**Examples** (reference, then generated):

- `logical_table_sources`, row `sampleapp::bmm::SAMPLESALES.Dim Sales Rep.src.LTS Employee`: the reference has no such column; generated `closure_table_id` = `sampleapp::phys::xe.SAMPLESALES.EMP_PARENT_CHILD`.
- `physical_tables`, row `sampleapp::phys::xe.SAMPLESALES.EMP_PARENT_CHILD`: the reference has no such column; generated `distance_column_id` = `sampleapp::phys::xe.SAMPLESALES.EMP_PARENT_CHILD.DISTANCE`.
- `physical_tables`, row `sampleapp::phys::xe.SAMPLESALES.EMP_PARENT_CHILD`: the reference has no such column; generated `leaf_column_id` = `sampleapp::phys::xe.SAMPLESALES.EMP_PARENT_CHILD.IS_LEAF`.

### Difference 37

Four smaller attributes the reference listed as unread are now columns: `physical_tables.description`, `physical_columns.source_column_id` (the column an alias column stands for), `logical_tables.is_lookup_table` and `logical_columns.descriptor_id_column_id`.

**Accounts for:**

- `sampleapp`: `logical_columns` 1 columns added, on 77 rows; `logical_tables` 1 columns added, on 11 rows; `physical_columns` 1 columns added, on 139 rows; `physical_tables` 1 columns added, on 22 rows; `unparsed` 4 reference rows gone.
- `bi-analytics-2013`: `logical_columns` 1 columns added, on 933 rows; `logical_tables` 1 columns added, on 71 rows; `physical_columns` 1 columns added, on 957 rows; `physical_tables` 1 columns added, on 78 rows; `unparsed` 1 reference row gone.
- `sampleapp-v607`: `logical_columns` 1 columns added, on 0 rows; `logical_tables` 1 columns added, on 0 rows; `physical_columns` 1 columns added, on 0 rows; `physical_tables` 1 columns added, on 0 rows.

**How it was checked:** the comparison reads the estate's repository files itself (`KeySource`). On every row of the table the added cells must be what the files say: an attribute exactly as written, a text exactly as written, or the Row identifier built from the names of the element a link points at. One wrong value and all the added columns of that table are listed as unexplained. An `unparsed` count is explained only when it is exactly what the files leave outside what the jar now reads.

**Examples** (reference, then generated):

- `logical_columns`, row `sampleapp::bmm::SAMPLESALES.Dim Products.Lob`: the reference has no such column; generated `descriptor_id_column_id` = `sampleapp::bmm::SAMPLESALES.Dim Products.Lob Key`.
- `logical_tables`, row `sampleapp::bmm::SAMPLESALES.Dim Products (Translated)`: the reference has no such column; generated `is_lookup_table` = `true`.
- `physical_tables`, row `sampleapp::phys::xe.SAMPLESALES.US_CRIME_YEAR_2K_ONWARDS`: the reference has no such column; generated `description` = `Year 2000 to Year 2010`.

### Deliberate differences that did not occur in this comparison

| # | Why it does not show here |
|---|---|
| 2 | The estate name is the folder's name. The three estate folders carry the names the reference used, so no table shows it. |
| 3 | A name that cannot be part of a Row identifier becomes an error row and the run continues. The samples have no such name: `errors` rows of kind `invalid_name` in the generated Knowledge Bases: 0. Checked by the project's tests only. |
| 4 | A source file that cannot be read becomes an error row and the run continues. The samples have none: `errors` rows of kind `unreadable`: 0. Checked by the project's tests only. |
| 12 | About rows for files that were not discovered. Generated `unparsed` rows of kind `undiscovered_file`: 0; where there are any, they equal the reference's. |
| 13 | About the slicer, which is not part of `extract`. Not covered by this comparison. |
| 14 | About emptying the output folder before writing. It changes no table. Checked by the project's tests only. |
| 18 | A repository folder that cannot be listed. The samples have none. |
| 20 | A catalog folder that cannot be listed. The samples have none. |
| 22 | It can only occur where a report column resolves to a presentation column, and none does on the samples. On the hand-made estates (section 5) it accounts for 3 cells. |

## 4. Unexplained differences

None. Every difference found on the 3 sample estates is assigned to a numbered deliberate difference.

## 5. Linking report columns to the repository: the hand-made estates

On the samples no report column links to a repository (section 6). The link is therefore checked on two small hand-made estates that have both halves. For each, this command builds the estate folder under `build/compare-all/in/`, runs the jar on it, runs the reference implementation (the Python program in `reference/python-extractor/`) on the same folder, and compares the two outputs in the same way as above.

- `constructed-pair`: the first attempt's pair, `reference/test-corpus/constructed-cases/` (`constructed/` is the repository, `constructed-catalog/` the catalog).
- `linked-estate`: `src/test/fixtures/linked-estate/`, written in story 1.10 so that every value of `resolution` and `source_state` occurs, and so that difference 22 occurs.

The project's tests (`ReportLineageTest`) separately require the jar's rows for `constructed-pair` to equal the expected result recorded with that pair (`GROUND-TRUTH.md` and guide 02, section 6.5), and the rows for `linked-estate` to equal values worked out by hand from the rules.

### constructed-pair

| Table | Reference rows | Generated rows | Identical | Differ only as explained | Missing | Extra | Unexplained | Explained by |
|---|---:|---:|---:|---:|---:|---:|---:|---|
| `analysis_filters` | 3 | 5 | 2 | 1 | 0 | 2 | 0 | 8: 1 cell; 9: 2 extra; 10: 2 added columns |
| `logical_columns` | 2 | 2 | 2 | 0 | 0 | 0 | 0 | 37: 1 added columns |
| `logical_dimensions` | 0 | 0 | 0 | 0 | 0 | 0 | 0 | 32: 1 added columns |
| `logical_levels` | 0 | 0 | 0 | 0 | 0 | 0 | 0 | 35: 2 added columns |
| `logical_table_sources` | 2 | 2 | 2 | 0 | 0 | 0 | 0 | 36: 1 added columns |
| `logical_tables` | 1 | 1 | 1 | 0 | 0 | 0 | 0 | 37: 1 added columns |
| `manifest` | 7 | 7 | 2 | 4 | 0 | 0 | 0 | 16: 1 row; 17: 3 rows; `extracted_at`, 1 row, is the time of the run and is counted in no column |
| `physical_columns` | 4 | 4 | 4 | 0 | 0 | 0 | 0 | 31: 2 added columns; 37: 1 added columns |
| `physical_joins` | 0 | 0 | 0 | 0 | 0 | 0 | 0 | 26: 1 added columns |
| `physical_tables` | 2 | 2 | 2 | 0 | 0 | 0 | 0 | 36: 4 added columns; 37: 1 added columns |
| `saved_filters` | 1 | 1 | 0 | 1 | 0 | 0 | 0 | 8: 1 cell |
| `security_rls` | 0 | 0 | 0 | 0 | 0 | 0 | 0 | 27: 2 added columns |
| `unparsed` | 29 | 28 | 28 | 0 | 1 | 0 | 0 | 9: 1 missing |
| *26 other tables, identical* | 44 | 44 | 44 | 0 | 0 | 0 | 0 | |
| **Total** | **95** | **96** | **87** | **6** | **1** | **2** | **0** | |

Tables the jar writes that the reference does not have: `logical_keys` (0 rows): new table (difference 29); `logical_level_keys` (0 rows): new table (difference 30); `lts_joins` (0 rows): new table (difference 34); `lts_tables` (0 rows): new table (difference 34); `physical_keys` (0 rows): new table (difference 28); `schema` (378 rows): new table (difference 16); `schema_tables` (46 rows): new table (difference 16).

Report columns by outcome (`report_lineage`):

| `resolution` | `source_state` | Jar | Reference implementation |
|---|---|---:|---:|
| `exact` | `computed_not_sourced` | 1 | 1 |
| `exact` | `sourced` | 1 | 1 |

### linked-estate

| Table | Reference rows | Generated rows | Identical | Differ only as explained | Missing | Extra | Unexplained | Explained by |
|---|---:|---:|---:|---:|---:|---:|---:|---|
| `analysis_filters` | 0 | 0 | 0 | 0 | 0 | 0 | 0 | 10: 2 added columns |
| `logical_columns` | 6 | 6 | 5 | 1 | 0 | 0 | 0 | 6: 1 cell; 37: 1 added columns |
| `logical_dimensions` | 0 | 0 | 0 | 0 | 0 | 0 | 0 | 32: 1 added columns |
| `logical_levels` | 0 | 0 | 0 | 0 | 0 | 0 | 0 | 35: 2 added columns |
| `logical_table_sources` | 2 | 2 | 2 | 0 | 0 | 0 | 0 | 36: 1 added columns |
| `logical_tables` | 1 | 1 | 1 | 0 | 0 | 0 | 0 | 37: 1 added columns |
| `manifest` | 7 | 7 | 2 | 4 | 0 | 0 | 0 | 16: 1 row; 17: 3 rows; `extracted_at`, 1 row, is the time of the run and is counted in no column |
| `physical_columns` | 5 | 5 | 5 | 0 | 0 | 0 | 0 | 31: 2 added columns; 37: 1 added columns |
| `physical_joins` | 0 | 0 | 0 | 0 | 0 | 0 | 0 | 26: 1 added columns |
| `physical_tables` | 2 | 2 | 2 | 0 | 0 | 0 | 0 | 36: 4 added columns; 37: 1 added columns |
| `report_lineage` | 20 | 20 | 17 | 3 | 0 | 0 | 0 | 22: 3 cells |
| `security_rls` | 0 | 0 | 0 | 0 | 0 | 0 | 0 | 27: 2 added columns |
| *27 other tables, identical* | 86 | 86 | 86 | 0 | 0 | 0 | 0 | |
| **Total** | **129** | **129** | **120** | **8** | **0** | **0** | **0** | |

Tables the jar writes that the reference does not have: `logical_keys` (0 rows): new table (difference 29); `logical_level_keys` (0 rows): new table (difference 30); `lts_joins` (0 rows): new table (difference 34); `lts_tables` (0 rows): new table (difference 34); `physical_keys` (0 rows): new table (difference 28); `schema` (378 rows): new table (difference 16); `schema_tables` (46 rows): new table (difference 16).

Report columns by outcome (`report_lineage`):

| `resolution` | `source_state` | Jar | Reference implementation |
|---|---|---:|---:|
| `case_normalised` | `sourced` | 3 | 3 |
| `exact` | `computed_not_sourced` | 2 | 2 |
| `exact` | `sourced` | 4 | 4 |
| `not_a_column_reference` | (empty) | 6 | 6 |
| `outside_semantic_model` | (empty) | 1 | 1 |
| `unresolved` | (empty) | 4 | 4 |

Difference 22 on this estate:

- `report_lineage`, row `linked-estate::cat::shared/linked/area in other case#c0.0#lineage`, column `physical_column_id`: reference `linked-estate::phys::LK.F_AGG.AMOUNT`; generated `linked-estate::phys::LK.F_DETAIL.AMOUNT`.
- `report_lineage`, row `linked-estate::cat::shared/linked/area without quotes#c0.0#lineage`, column `physical_column_id`: reference `linked-estate::phys::LK.F_AGG.AMOUNT`; generated `linked-estate::phys::LK.F_DETAIL.AMOUNT`.
- `report_lineage`, row `linked-estate::cat::shared/linked/every outcome#c0.0#lineage`, column `physical_column_id`: reference `linked-estate::phys::LK.F_AGG.AMOUNT`; generated `linked-estate::phys::LK.F_DETAIL.AMOUNT`.

## 6. What this comparison does not prove

- **It proves the jar matches the Python program, not that either is right about OBIEE.** Where the Python program misreads a file, the jar misreads it the same way, and the comparison is silent. Neither program has been checked against a running OBIEE system.
- **The sample data comes from three unrelated sources.** Two public repositories and one public catalog, each from a different system. No estate has a repository and a catalog that belong together. Features the samples do not use were not compared at all; the list of deliberate differences that did not occur (section 3) names some.
- **Report lineage never resolves on the samples.** Of the 11,143 `report_lineage` rows the jar wrote, 0 name a presentation column; in the reference, 0. The comparison shows the two programs agree on why each report column does not link, nothing more. That a link is found, and the right one, is shown only on the two small hand-made estates of section 5, whose halves were written by hand to match each other. It stays unproven on real data until an estate with both halves from one OBIEE system is available.
- **The schema files have no reference counterpart.** `schema_tables.csv` and `schema.csv` are new (difference 16). Their wording was written in this project and is checked only by its own tests, which require a meaning for every column and a real target for every stated link.
- **`manifest.csv` differs by design.** Its version, input folder, estate list and table list are expected to differ (differences 15, 16, 17), and its time stamp always does. Only the two lines that say which halves were present, and how they were paired, are compared for equality.
- **The checks for explained differences are only as good as their rules.** A difference is explained when it passes the check described in section 3. For differences 7, 8, 9, 10, 19, 21 and 26 to 37 the comparison reads the source files itself, with different code from the jar's. Differences 5, 6, 11 and 22 are checked from the two Knowledge Bases alone.
- **Identical means identical in the columns both sides have.** The reference's guard columns are left out (difference 1), and the columns the jar adds (two in `analysis_filters`, difference 10; one in `physical_joins`, difference 26; two in `security_rls`, difference 27; and the columns of differences 31, 32 and 35 to 37 in seven repository tables) have no reference value to compare with; they are checked against the source files instead.
- **Five tables have no reference counterpart.** `physical_keys`, `logical_keys`, `logical_level_keys` (differences 28 to 30), `lts_tables` and `lts_joins` (difference 34) are new. They are not compared with the reference, which has nothing to compare with; every row is checked against the estate's own files, read a second time. That shows the jar wrote what the files hold. It does not show what a key means in the database: the repository has no flag that says which physical key is the primary key, and nobody has compared these keys with the constraints of a real database. Likewise a join type in `lts_joins` is the repository's own word (`Inner`, `LeftOuter`); that the server writes that join into its SQL has not been checked against a running OBIEE.
- **One run, one machine.** The comparison was run on Linux with the JDK on this machine. The Windows build script `build.cmd` has the same commands and was not run here.
- **Later commands are not covered.** The slicer and the skills that read the Knowledge Base are other stories.
