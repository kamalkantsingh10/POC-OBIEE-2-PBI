---
title: 'Take one FSD from start to finish'
type: 'feature'
created: '2026-11-23'
status: 'done'
route: ''
review_loop_iteration: 0
context:
  - '{project-root}/documents/planning-artifacts/architecture/architecture-POC-OBIEE-2-PBI-2026-11-23/ARCHITECTURE-SPINE.md'
  - '{project-root}/documents/planning-artifacts/prds/prd-POC-OBIEE-2-PBI-2026-11-23/prd.md'
---

# Story 4.4: Take one FSD from start to finish

<frozen-after-approval reason="human-owned intent — do not modify unless human renegotiates">

## Intent

**Problem:** Each part has been built and tested alone. Phase 1 is finished only when one FSD has gone through the whole chain, and the steps are written down so the Operator can repeat it.

**Approach:** Run the whole chain once, fix what breaks, write the README's step-by-step, and record the result honestly, including what data it ran on.

## Boundaries & Constraints

**Always:**

- Use only the jar and the three skills.
- Record what actually happened: what failed, what needed a manual fix, what was not verified.
- State which data the run used. A run on hand-made or sample data does not prove the approach on a real estate.

**Never:**

- Changing a skill's rules to make the run pass without recording why.
- Claiming SM-1 on real data if the run used fixtures.

## I/O & Edge-Case Matrix

| Scenario | Input / State | Expected Output / Behavior | Error Handling |
|----------|--------------|---------------------------|----------------|
| Real inputs available | A real FSD and an estate with both halves from one OBIEE instance | Full run; results recorded; SM-1 met | N/A |
| Only sample data | The sample repositories and catalog, plus a hand-written FSD | Full run; recorded as met on sample data only | N/A |
| Only fixtures | The constructed estate and the hand-made FSD | Full run; recorded as met on fixture data only | N/A |
| A step fails | Any command or skill fails | Fix it in the story it belongs to, or record it in `documents/implementation-artifacts/deferred-work.md` | N/A |

</frozen-after-approval>

## Code Map

- `documents/planning-artifacts/prds/prd-POC-OBIEE-2-PBI-2026-11-23/prd.md` -- UJ-1 (the eight steps), SM-0 to SM-3, open questions
- `documents/planning-artifacts/migration-plan.md` -- stages 1 to 7 and section 3, how to get the files out of OBIEE

## Tasks & Acceptance

**Execution:**

- [ ] `README.md` -- the eight steps with the exact commands, the folder layout, and what each output file is for
- [ ] `work/<tab>/` -- the files of the run
- [ ] `documents/implementation-artifacts/4-4-take-one-fsd-from-start-to-finish.md` -- Implementation Notes: data used, results, the twenty-statement check, open questions answered
- [ ] `documents/planning-artifacts/migration-plan.md` -- update section 6, "Where we are today"

**Acceptance Criteria:**

- Given the inputs available, when the eight steps in the README are followed, then `work/<tab>/to-be.md` exists and was produced using only the jar and the three skills.
- Given the As-is document used in the run, when twenty statements are checked against the rows they cite, then the count supported is recorded (SM-3).
- Given the run, when the story is closed, then Implementation Notes say which data it ran on and which of the PRD's open questions it answered.

## Implementation Notes

## Spec Change Log

## Review Triage Log

## Verification

**Commands:**

- `./build.sh` -- expected: the jar is built at `build/obi2pbi.jar` and every test passes

**Manual checks (if no CLI):**

- Follow the README from a clean checkout on a second machine, or the target system if available, and record whether the jar runs there on Java 11.
