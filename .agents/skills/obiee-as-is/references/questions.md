# Open questions, and observations across sections

Read this with `document-structure.md` before you write the first section.

## What a good open question is

A good open question is one that a named piece of the export leaves open, that changes what someone would build or how they would read a number, and that a person can settle in one sitting by looking at something specific. Raise one whenever:

- the packet does not answer something a reader needs;
- two readings are possible (two measures with the same rule and column; several sources and no recorded priority; one measure mapped differently in two sources);
- more information is wanted to describe the thing properly: the business meaning of a measure or table where it matters, what a variable holds at run time, what a database function or a query-defined table does, why a fixed number is written into a formula;
- the gaps packet shows something unread that affects what a section says.

Before you raise one:

- Do not ask what the packet already answers.
- Read the question list in your notes. If an earlier question already covers the doubt, do not ask again: point to it from your section. If yours adds an angle, you may add one line to the earlier question, directly above its `Answer:` line: `See also: <unit_id>, <what this unit adds, in a few words>`. Never change the earlier question's own wording.
- If a later part of the same split unit is likely to hold the answer (the packet says "stated with each source"), do not ask yet. Write `- To check in a later part: …` in the notes and decide when you write that part.
- Do not ask one question per field: one question may cover several fields that share the same doubt.

In the section, point to the question where the doubt arises: "(open question Q7)".

## The form

The form is fixed, because a program reads it. Two examples from a real run:

```
### Q3. What do "Revenue (EUR)", "Revenue (GBP)" and "Revenue (INR)" hold, and why are they multiplied in one source only?
Raised in: table-01-fact-revenue-p2
Why it matters: in the source "SAMP_REVENUE_F" the three measures are the database column REVENUE multiplied by 0.76, 0.62 and 54.68; in the source "Aggregate Revenue" they are the column REVENUE of SAMP_REVENUE_A with no multiplication. As recorded, the same measure gives different numbers depending on the source OBIEE picks. Where the three fixed numbers come from is not recorded.
Assumed meanwhile: nothing. Both mappings are described exactly as recorded, and no currency meaning is stated.
How to get the answer: in the OBIEE Administration Tool, open the source "Aggregate Revenue" of the business table "Fact Revenue" and bring back the expression its column mapping shows for each of the three measures. Then run these two queries against the schema SAMPLESALES and bring back both totals: select sum(REVENUE) from SAMP_REVENUE_A, and select sum(REVENUE) from SAMP_REVENUE_F. For the numbers 0.76, 0.62 and 54.68, bring back the rate table or document they were taken from, if there is one, and the date they applied.
Answer:

### Q16. What value does PREFERRED_CURRENCY hold for a user at run time, and how is it changed?
Raised in: variables
Why it matters: the field "Preferred Currency" shows a different measure depending on this value. Its default is 'gc1'. The query recorded as setting it reads no table of user settings and names PREFERED_CURRENCY, with one R, which the repository does not define. As recorded it cannot be said whether any user ever gets another value than the default.
Assumed meanwhile: nothing. The default and the query are quoted exactly in the section; no run-time value is stated.
How to get the answer: log in to OBIEE as a report author, run this statement where OBIEE lets you issue logical SQL, and bring back the value: select VALUEOF(NQ_SESSION.PREFERRED_CURRENCY) from "Sample Sales". Then change the currency in the user's own preferences, if a currency choice is offered there, log out and in, run the statement again, and bring back the new value and the list of choices offered.
Answer:
```

- The heading is three hashes, a space, `Q`, the number, a full stop, a space, the question. A number is used once, ever.
- The five lines start exactly with `Raised in:` (the unit id), `Why it matters:`, `Assumed meanwhile:`, `How to get the answer:`, `Answer:`. `Answer:` is always the last line of a question: whatever follows it, up to the next heading, is read as the answer. Leave it empty; it is the user's.
- **Why it matters** and **Assumed meanwhile** state facts, and the same rules hold for them as for a section: only what the packet says, nothing sharpened.
- **How to get the answer** is a step someone can take without asking you anything more: what to look at or run, where, and what to bring back. It may name any table, column, variable, source, object or file that appears in a packet. It may describe an action in general terms ("in the OBIEE Administration Tool, open the physical table X and look at its foreign keys"). It may give a query, written as ordinary text with no backticks. It does not give menu paths, tab names or screen names as if they had been verified: you have not seen the screens. It does not name a person, a team or a role, and it is never "ask the developer" or "check with the business".

## When the export answers a question later

A question can go stale: asked in one unit, answered by a later packet (or by a later part of the same split unit). While writing, you only record it: a line `- Settles Q<n>: <the fact, with its marker>` in the notes of the unit whose packet answers it. After the last unit (step 5 of the skill), each such question is taken out of `990-open-questions.md` and appended to `993-questions-the-export-answered.md` as its heading, its `Raised in:` line, and one line `Settled by: <the fact, as the note states it, with the note's marker>. See the section "<that unit's heading>".` It is not deleted and its number is not used again.

## Observations across sections

Some things show only when two units are put side by side: one fact table's source names a level that the other's does not; a formula reads a variable that the variables unit lists as not defined in the repository. A unit's section cannot say them, because it may cite only its own packet. While you write a unit, put such a thing in the notes as a line starting `- Observation:`, saying what this packet says, with its marker, and naming the thing it should be set beside ("to set beside the sources of Fact Revenue").

After the last unit, the part `980-observations.md` is written from the notes:

```
## Observations across sections

- The source "SAMP_REVENUE_F" of Fact Revenue is recorded at a level of the drill path "Sales Rep Hierarchy" [kb:…Fact Revenue.src.SAMP_REVENUE_F]; the level of detail of the source of Fact Target names no level of that drill path [kb:…Fact Target.src.SAMP_TARGETS_F].
- The formula of "Preferred Currency" reads PREFERRED_CURRENCY [kb:…Fact Revenue.Preferred Currency]; the query that sets it names PREFERED_CURRENCY, which is used by name and not defined in the repository (section Values set per user or for the whole system; no Row identifier).
```

- **Pairing.** Two sides belong together when they name the same thing (a table, a field, a drill path, a variable). The other side of an `- Observation:` line may be another such line or an ordinary note line of another unit that carries a marker. An observation that finds no second side is dropped.
- **One marker per side**, and that marker's row must carry the whole of that side's claim. If a side needs two rows, or there are three sides, write two observations.
- **A side with no marker.** When the packet marks the fact "no Row identifier" (a count, a name used and not defined in the repository), the side is cited by naming its section and adding "no Row identifier", as in the second example.
- Each observation states only what the cited lines say, side by side, and draws no conclusion from them. If the reader needs a conclusion, it is an open question. If there is none, the part says: "None."
