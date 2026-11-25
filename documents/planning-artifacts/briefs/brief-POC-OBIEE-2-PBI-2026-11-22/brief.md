---
title: Product Brief — OBIEE to Power BI Migration Assistant (reboot)
status: final
created: 2026-11-22
updated: 2026-11-23
---

# Product Brief — OBIEE to Power BI Migration Assistant (reboot)

**Owner:** Kamal · **Reader:** Kamal and whoever writes the PRD and architecture next · **Kind:** working brief, not a pitch

## Summary

Migrating OBIEE reports to Power BI is slow because someone has to work out by hand what each metric in OBIEE computes and which database tables it comes from. The first attempt, POC-RPD, built a program that does that reading automatically. The project then kept growing in scope and never reached a point anyone could call finished.

This reboot starts again with one chain of steps to get working end to end. A plain Java program, delivered as a jar, builds a **knowledge base** from an OBIEE export. Work then starts from a **functional specification document (FSD)** that is provided to us; one FSD describes one new report, which is a single tab in a dashboard. One FSD, one tab, is one piece of work. An **agent in Copilot** traces the FSD to what sits behind it in OBIEE, describes today's model (the as-is semantic model), and produces a **to-be design**.

**Phase 1 is finished when we can read through an FSD and come out with a to-be design.** The steps follow the first attempt's migration plan, with one change: the FSD is the starting point for each piece of work.

## The problem

- Understanding OBIEE is the expensive step. It is manual, and only a few people can do it.
- The first attempt dragged. It planned nine stages, two migrations and several agents before any one stage was usable end to end.
- The target system, where the jar and the agent run, does not allow importing libraries, so anything built on third-party packages cannot run where the work happens.

## Who this serves

- **Kamal, as operator and builder.** He runs the jar, reads what it could not parse, and runs the agent.
- **The people who read the output:** OBIEE and business owners who confirm the as-is document, and whoever builds the tables and the Power BI model from the to-be design.

## What we are building in phase 1

| Step | What it does | Built with |
|---|---|---|
| **Extract** | Reads the OBIEE repository and the Web Catalog and writes the knowledge base | Java 11, no added libraries |
| **Slice** | Cuts the knowledge base down to what one piece of work needs, small enough for an agent to read | Java 11, no added libraries |
| **Trace the FSD** | Matches each thing the FSD asks for to the presentation tables, logical tables, logical table sources and database tables behind it. Each match is a proposal with a confidence score | Agent in Copilot |
| **Check the matches** | A workflow checks the proposed matches automatically, before a person confirms them | Workflow |
| **Describe today's model** | Writes the as-is semantic model for each subject area the tab touches, with open questions where OBIEE does not give an answer | Agent in Copilot |
| **Write the to-be design** | Sets out the new tables and views, the Power BI semantic model, and the report's fields, each tied back to what it comes from today | Agent in Copilot |

What extraction captures:

- **From the repository:** tables, columns, formulas, joins, hierarchies, variables, security rules, and lineage from presentation column down to physical column.
- **From the catalog:** dashboards, their pages, reports, report columns and filters.

**Platform and formats**

- The Java program is delivered as a jar file. The target system runs Java 11.
- The knowledge base is a folder of CSV files.
- The FSD arrives as a `.docx` file. If Copilot cannot open Word files, the jar turns the `.docx` into plain text first.

**Rules**

- No AI takes part in extraction or slicing. The same export always produces the same knowledge base.
- A person confirms the matches. A matching name does not prove the same meaning, so the agent proposes; the check and the person decide.
- A tab can draw on more than one subject area.

**Step detail**

- The automatic check is structural: every cited row exists, lineage reaches a database table, nothing in the FSD is left unmatched, and matches agree with the existing report in the catalog where there is one. It does not compare numbers, because there is no database access.
- The report's fields in the to-be design are the fields shown on the tab, each mapped to the OBIEE column it comes from.
- The to-be design is a document.

## Phase 1 is done when

1. One command on the target system turns an OBIEE export (repository and catalog) into a knowledge base, using nothing beyond Java 11.
2. A second command produces the slice for a piece of work.
3. Given an FSD, the agent lists the presentation tables, logical tables, logical table sources and database tables behind it, each match with a confidence score.
4. The workflow has checked those matches and a person has confirmed them.
5. The agent writes the as-is semantic model for each subject area the tab touches.
6. The agent produces the to-be design for the tab.
7. Each factual statement in the as-is document can be traced to a knowledge base row.
8. Someone who knows OBIEE has read one as-is document and one to-be design and found them correct enough to work from.

## What we keep from the first attempt

- Formulas are copied character for character and never rewritten.
- The jar never guesses. Anything it cannot read is listed in the output, so a gap is visible.
- Every row has a stable identifier, so a document can point at the exact row behind each statement.
- We reuse the knowledge of the two OBIEE export formats (repository and catalog), written up in POC-RPD's schema documents.

## Out of phase 1, and what comes next

- Generating the tables, views and Power BI files from the to-be design.
- Checking numbers against the legacy system, and the sign-off report.
- The review-and-correction loop for the as-is document.
- The accuracy report, benchmark tiers and hand-built test cases. In phase 1, a small set of ordinary tests stands in for them.

None of these is rejected. They are out of phase 1 so that phase 1 can finish.

After phase 1, the work proceeds along the migration plan, one agent at a time on top of the same knowledge base: building the designed tables and views, generating the Power BI model, and checking its numbers. The order will be decided once phase 1 has been used on a real tab.

## What we do not know

**Known gaps**

- **No export from our own OBIEE.** This is unchanged since the first attempt: there are only public sample repositories and one sample catalog. They carry no licence, so our right to use them is unsettled.
- **The samples come from different systems.** In the first attempt, report columns could not be linked to repository columns for that reason: 0 of 11,143 report columns resolved. Linking the two halves stays unproven until we have both from one OBIEE instance.
- **No database access.** Whether migrated numbers match the legacy numbers is untested and outside phase 1.

**Open questions**

- **FSD wording.** Does the FSD name OBIEE columns or only business terms? Business terms make tracing slower and more error-prone.
- **Copilot on the target system.** Which Copilot product is it, can it run the Java commands itself, and can it open a Word file?
- **Platform for the new tables.** Which platform are the new tables and views designed for, and whose naming rules apply?
