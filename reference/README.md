# Reference assets from POC-RPD

Everything in this folder was copied unchanged from the first attempt, https://github.com/kamalkantsingh10/POC-RPD, at commit 220b56e (18 November 2026). Treat it as read-only.

| Folder | What it is | Used for |
|---|---|---|
| `knowledge-base/` | The Knowledge Base the Python extractor produced from the two sample repositories and the sample catalog | The expected output. The Java jar's tables are compared with these, apart from the deliberate differences in `docs/port-guide/00-read-me-first.md` |
| `test-corpus/` | Notes on the sample data and where to get it (`corpus.md`, `corpus-manifest.csv`), the format notes, and `constructed-cases/`, a small hand-made repository and catalog pair with recorded expected results | Test fixtures; instructions for fetching the large samples |
| `processed-inputs/` | One real sample file per object type, for the repository and the catalog | Seeing what the source files look like |
| `analysis/` | The as-is document the first attempt produced for Sample Sales, with the slice it was written from | The model for the new as-is skill's output |
| `as-is-skill/` | The first attempt's as-is skill | The starting point for the new as-is skill |

The small repository fixture used by the first attempt's unit tests is at `src/test/fixtures/` (`testrpd/` and `GROUND-TRUTH.md`).

**The large sample data is not here.** The two sample repositories (1,383 files) and the sample catalog (5,371 files) were kept outside the first attempt's repo. `test-corpus/corpus.md` and `test-corpus/corpus-manifest.csv` say where they come from and which versions to fetch.

**Licence.** The Knowledge Base, the analysis and the sample files carry names and formulas from public sample repositories. Kamal confirmed on 23 November 2026 that licence is not a problem for this project.

## Added 23 November 2026

| Folder | What it is | Used for |
|---|---|---|
| `python-extractor/` | The first attempt's Python source (`tools/rpd_extract/` and `tools/catalog_survey.py`), unchanged | The reference implementation itself. Read it when a port guide is unclear, and run it on a fixture to see the expected output: from `reference/python-extractor`, `python3 -m tools.rpd_extract.cli --corpus <folder> --rpd-id <name> --out <folder>` |

The large sample data is on this machine at `~/Documents/Garage/obiee-corpus/`, at the pinned versions. `estates/` (git-ignored) links to it: `estates/bi-analytics-2013/rpd`, `estates/sampleapp/rpd`, `estates/sampleapp-v607/catalog`.
