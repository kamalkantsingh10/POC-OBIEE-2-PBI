# Build rules for every story

These rules apply to whoever builds a story in this project. The story file says what to build; this file says how to work.

## Read first

1. The story file you were given, in `documents/implementation-artifacts/`.
2. `docs/port-guide/00-read-me-first.md` — the port rule and the numbered list of deliberate differences.
3. The port guide sections the story's Code Map names.
4. `documents/planning-artifacts/architecture/architecture-POC-OBIEE-2-PBI-2026-11-23/ARCHITECTURE-SPINE.md` — the rules (AD-1 to AD-18).
5. The code already in `src/`, so you follow the patterns that exist.

## The reference implementation

- The first attempt's Python source is in `reference/python-extractor/tools/rpd_extract/`. It is the truth about what to port. Where a port guide and the Python disagree, the Python wins; say so in your notes.
- You may run it to see expected output: `cd reference/python-extractor && python3 -m tools.rpd_extract.cli --corpus <folder> --rpd-id <name> --out <a folder under build/>`. Never write its output anywhere except under `build/`.
- Its output on the full sample data is in `reference/knowledge-base/` (39 tables). Estate names there: `bi-analytics-2013` and `sampleapp` (repositories), `sampleapp-v607` (catalog).
- The same sample data is linked at `estates/bi-analytics-2013/rpd`, `estates/sampleapp/rpd`, `estates/sampleapp-v607/catalog`. Run the jar on it and compare with `reference/knowledge-base/` using `KbCompare`.

## Hard limits

- Java language level 11. Only `java.base` and `java.xml`. No Maven, Gradle, JUnit or any downloaded library. The machine has JDK 21; compile with `--release 11`.
- Build and test only through `./build.sh` (keep `build.cmd` in step with it).
- Do not `git commit`, `git add`, or change git state in any way.
- Do not edit anything under `reference/`, `docs/port-guide/01-*`, `02-*`, `03-*`, `estates/`, `_bmad/`, `.claude/skills/bmad-*`, `.agents/skills/bmad-*`, or `.github/agents/bmad-*`.
- Do not delete or weaken an existing test to make the build pass. If a test is wrong, fix it and say why in your notes.
- Write build output only under `build/`, and Knowledge Base output only under `kb/` or `build/`.
- Never write a credential or secret to any file.

## Schema

- Every table is declared once in the code, with a description, and every column with a plain-English meaning and, where it refers to another table, the target table and column. `schema_tables.csv` and `schema.csv` are generated from those declarations.
- When a story adds a table or a column, add its description and references in the same story. An empty meaning fails a test.

## Deliberate differences

- Port the Python as it is. Change behaviour only for a numbered entry in `docs/port-guide/00-read-me-first.md`.
- If you find a new case where the Python loses data, corrupts data or stops the run, you may fix it: add a numbered entry to that list (old behaviour, new behaviour, why), and report it.
- If you are unsure whether something is a defect, port it as it is and report it as an open point.

## When you finish

1. `./build.sh` passes, with every test passing.
2. In the story file: tick the tasks you did, fill **Implementation Notes** (what you built, files, decisions you took and why, anything that surprised you, what you did not do), and set the frontmatter `status` to `in-review`.
3. Do not touch other story files or `sprint-status.yaml`.
4. Your final message, 40 lines at most:
   - **Built:** what now works.
   - **Tests:** the exact commands you ran and the numbers (tests passed, rows compared, differences).
   - **Decisions:** each choice you made that the story did not fix, with the reason.
   - **Open points:** anything unresolved, uncertain, or that a person must decide.
   - **Not done:** anything in the story you did not finish, stated plainly.

Report honestly. A failing test or an unexplained difference reported plainly is useful; a hidden one is not.
