# Build report — decisions, open points and testing

**For:** Kamal, to review on return. **Kept by:** the agent doing the build. **Started:** 23 November 2026.

Kamal asked for all stories to be started and completed without him, with every decision and open point written down, and all testing recorded. This is that record. It is added to as each story finishes.

## Read this first

*Written 24 November 2026, when the unattended build finished.*

**All 22 stories are built.** 24 of 26 sprint entries are done. Two wait for Kamal: story 1.11 (the comparison report) and therefore Epic 1.

**What exists**

- **A Java 11 jar** with no added libraries and 11 commands: `extract`, `slice`, `assemble-doc`, `check-doc`, `fsd-text`, `find-columns`, `find-pages`, `check-matches`, and the `compare`/`compare-all` and `skills` build targets. 267 tests pass.
- **An agent, `obiee-migration-agent`,** for GitHub Copilot (`.github/agents/`) and Claude, with four skills: `obiee-as-is`, `obiee-trace`, `obiee-to-be`, `obiee-review`.
- **Worked outputs on public sample data:**
  - `as-is/sampleapp/sample-sales.md`: today's model for Sample Sales, reviewed.
  - `as-is/bi-analytics-2013/iryna-bradzetskaya-mt-row-level-security.md`: a second subject area, draft.
  - `work/sample-sales-monthly/`: a traced test FSD.
  - `work/test-tab/to-be.md`: a reviewed to-be design, **built on test decisions written by the build agent, not by Kamal**.
- **Evidence for every run** in `documents/implementation-artifacts/skill-runs/`.

**Needs Kamal, most important first**

1. **Confirm the port:** read `documents/implementation-artifacts/kb-comparison-report.md` (0 unexplained differences against the first attempt's Knowledge Base) and record the confirmation in it. Epic 1 closes then.
2. **Confirm or reject deliberate differences 14 to 37** in `docs/port-guide/00-read-me-first.md` (1 to 13 you confirmed). The biggest are keys, full database tables, foreign-key targets, join paths inside sources and parent-child definitions: the first program never read them, and the as-is and to-be documents depend on them.
3. **Confirm or change the decisions taken without you,** D1 to D70 below. The ones that change behaviour you will notice: D46 (the review runs in a new chat, not automatically), D62 (scoring caps on matches), D65 (test decisions in `work/test-tab/`), D66 (unused measures listed as "Not described" in a to-be design), D68 (proposed DAX shown as an indented block).
4. **Decide O41:** let the jar write the mechanical tables of a document, so the AI writes only prose and questions. Both large runs found copying the tables the heaviest work, and one writer broke the no-scripts rule to do it.
5. **Provide a real FSD and a real export** (repository and catalog from one OBIEE instance). Everything so far is proven on public samples and one invented FSD.
6. **Run once in GitHub Copilot with GPT-5.6 Sol.** No skill has been run there; every run here was in Claude.

**Not done or not proven**

- No real FSD, no real decisions, no real export.
- Nothing run on Windows (`build.cmd` never run) or in GitHub Copilot.
- Linking report columns to the repository works only on hand-made data.
- Numbers have not been compared with OBIEE; that is stages 8 to 11 of the migration plan, not built.
- Nothing is committed to git.

## Where the build stands

*Updated 24 November 2026.* The build is finished. All stories are done except 1.11, which waits for Kamal's confirmation.

## How the build was run

- Each story, or a small group of stories, was given to a separate build agent with the story file, the port guides, the architecture and the reference assets.
- After each one, the lead agent ran the build and the tests itself and checked the result before moving on.
- Nothing was committed to git. Kamal has not asked for commits, so all work is in the working folder.

## Setup done before the first story

| What | Result |
|---|---|
| Sample data | Already on this machine at `~/Documents/Garage/obiee-corpus/`, at the three pinned versions the first attempt used. Linked into `estates/` (git-ignored). Nothing was downloaded |
| Python reference | The first attempt's source copied to `reference/python-extractor/`, so the Java port can be checked by reading and running the original, not only from the written guides |
| `.gitignore` | `build/`, `estates/`, `kb/`, `slices/` |

## Decisions taken without Kamal

| # | Story | Decision | Why | Easy to change? |
|---|---|---|---|---|
| D1 | setup | The Python source was copied into `reference/python-extractor/` | "Use the same assets as the original repo"; the source is the most exact description of what to port | Yes, delete the folder |
| D3 | 1.1 | The Knowledge Base describes itself in two files, `schema_tables.csv` (one row per table) and `schema.csv` (one row per column, with meaning and what it refers to), generated from the same definitions the writer uses | Kamal asked for a schema CSV so every agent can work out how to query the tables. Two files, because table-level and column-level facts have different shapes | Yes |
| D2 | setup || D68 | 4.2 | **A proposed DAX definition is written as an indented block under a line "Proposal (not from OBIEE; to be signed):"**, never in backticks or a fence | Backticks and fences are reserved for exact packet quotes, which `check-doc` verifies |
| D69 | 4.2 | Every OBIEE feature without a direct Power BI equivalent (17 listed: time-series functions, sources at different grain, session-variable switches, translation lookups, parent-child hierarchies, row filters, writeable measures, and more) becomes an open question with an `Options:` line; a measure that depends on an undecided feature gets no proposed definition | Nothing that needs a person's choice is decided silently |
| D70 | 4.2 | The to-be document's parts are numbered by section, not by unit, so it reads in a fixed order: the tab in one page, new tables and views, old-to-new column list, Power BI model, fields on the tab, decisions needed, unresolved, questions | Readability |
| D2 | setup || D66 | 4.1 | A tab Slice brings each touched logical table whole, so measures the tab did not ask for (for example All Months Revenue) are required rows of the to-be document too. **The lead agent decided** the to-be design may list such rows under "Not described" with the reason "not used by this tab", which `check-doc` already accepts | Keeps the completeness check without a Java change; a reader sees what was left out and why |
| D67 | 4.1 | `find-pages` searches dashboards, pages and reports; `find-columns --formula-contains` prints the matched formula; the gaps packet lists identifier-only unread attributes apart ("its content is read") | Open points O39 and O46, and trace run 1's note on `--formula-contains` |
| D2 | setup || D65 | Epic 4 | **Test decisions were written by the lead agent,** in a separate folder `work/test-tab/`, so Epic 4 could be built and tested. Every row's note says "TEST DECISION by the lead build agent, not by Kamal", and the folder has a README saying the same. 26 rows confirmed, the access-rule match rejected | Only Kamal confirms matches (FR-12); the alternative was to stop the build. The real tab folder `work/sample-sales-monthly/` has no decisions |
| D2 | setup || D62 | 3.2 | **Scoring caps** for the trace skill: a name alone never scores above 59; two near-equal candidates, at most 59; a column from another subject area, at most 59; each column of a calculation the writer read from the FSD, at most 79 | A confident-sounding wrong match is the most expensive error in this step |
| D63 | 3.2 | An access rule in the FSD ("each manager sees only their own offices") is listed as an item of kind `filter` | The conventions have no security kind; it still needs a field to filter on |
| D64 | 3.2 | The match review only lowers scores; a better candidate it finds is logged for the Operator, not swapped in | Only the Operator confirms; the review must not quietly make the choice |
| D2 | setup || D60 | 3.3 | The page a tab replaces is a row of `fsd_items.csv` of kind `replaces`; one FSD item may have several match rows (a figure computed from two columns) | Net revenue is revenue less discount: two columns, one item |
| D61 | 3.1 | `fsd-text` writes a manifest with the Word file's size and hash, so a later step can tell when the FSD changed; a long FSD is also split into sections a short-context model can read one at a time | Short-context model |
| D2 | setup || D59 | Epic 3 | **A test FSD was written by the lead agent** (`work/sample-sales-monthly/fsd-source.md`, turned into `.docx` by the build), for a Sample Sales tab, in business terms only. It is deliberately hard: "net revenue" (a calculation, revenue less discount), a percentage against target, an average price, a "Region" filter and an "Active customers" figure that Sample Sales does not have, and a security requirement | No real FSD has been provided. An invented FSD tests the mechanism, not how real FSDs are written |
| D2 | setup || D55 | 2.2 | **Each database table gets its own section:** an opening (what it is, alias, query, special role), a Columns table with one row per column (position, column, type, precision, scale, nullable, external name, mapped by, evidence), then Keys, Foreign keys and joins (pointers), and Read by | The section a table-builder will use most |
| D56 | 2.2 | Question and observation rules moved to their own reference file, `skills/obiee-as-is/references/questions.md` | `document-structure.md` was near its line limit |
| D57 | 2.4 | The review does not read the writer's notes before its last step; it only appends corrections, and matches them to the notes at the end | So the writer's summary does not colour the reading of the packets |
| D58 | 2.2 | A later unit may add a "See also Q<n>" line to an earlier question, never changing its wording | Run 2 accumulated overlapping questions. This is the one place the writer edits an existing question |
| D2 | setup || D53 | after run 2 | Deliberate differences 34 to 37: join path and join type inside each table source; parent and child levels; the parent-child definition; table descriptions, alias column sources, lookup-table flag, descriptor columns | D52 |
| D54 | after run 2 | `nullable` is written in both repositories only as `true` (635 columns) and left out on the rest (461); `false` never occurs. Packets print `nullable true` or `nullable not stated`, never `false` | The files do not say what an absent value means |
| D2 | setup || D50 | after run 2 | **Deliberate differences 28 to 33: keys and full tables.** New tables `physical_keys`, `logical_keys`, `logical_level_keys`; `physical_columns` gains scale and position; `logical_dimensions` gains a time flag; a database table comes into a slice whole, with one work unit per database table | The independent reviewer judged the document "not enough to create the tables": no key for any table, and only mapped database columns listed. The old program never read keys |
| D51 | after run 2 | Every column of a database table is a required row of that table's unit, so a document cannot drop one unnoticed | Completeness for the table-builder |
| D52 | after run 2 | More is being extracted on the same grounds: the join path and join type inside a multi-table source, level order in a dimension, the definition of a parent-child hierarchy, table descriptions, lookup-table flag, alias column sources | An inner join inside a source drops rows without changing any formula, which the original project set out to capture; the parent-child definition answers one of the document's open questions directly |
| D2 | setup || D46 | 2.2, 2.4 | **After writing, the agent stops and asks for the review in a new chat**, unless the request already covers the whole task. If the review runs in the same chat, the status line says so | Run 1's review was done by the same agent that wrote the text, which is weak. Story 2.4 had said the review runs automatically; this departs from it and needs your yes |
| D47 | 2.2 | A question that a later section answers is moved to a section "Questions the export answered", with the fact and its citation. It is not deleted and its number is not reused | Run 1 left such questions open although the document already held the answer. Your answers file stays yours alone |
| D48 | 2.2 | Observations that span two sections go in their own part, "Observations across sections", each citing the rows on both sides | Run 1's review removed such observations as unsupported, although they are among the most useful things in the document |
| D49 | 2.2 | A how-to step may name tables, columns, variables and files from the packets and describe an action in general terms. It must not give OBIEE menu paths or screen names as if verified | The run 1 writer admitted the screen names came from its general knowledge |
| D2 | setup || D41 | after run 1 | **Deliberate difference 26: a foreign key records what it points to.** `physical_joins` now gives the referenced table and the referenced columns | Run 1 showed all 14 foreign keys in Sample Sales with their far side missing, so the document could not state a single fact-to-dimension database join. The old Python had the same gap |
| D42 | after run 1 | Deliberate difference 27: a security row also records the role's display name and guid | The role's readable name is in the source; without it the document could only print a 64-character code |
| D43 | after run 1 | Packets are at most 120 lines by default (was 400), settable with `--max-packet-lines`; a large logical table becomes several units | Run 1's largest packet was about 200 long lines and had to be written in one piece; production is a short-context model |
| D44 | after run 1 | `check-doc` also checks each section against the packets: text in backticks or code fences must occur exactly in a packet, and a citation from another unit's packet is flagged as a warning | The run 1 reviewer had to write its own script to do this. Backticks are therefore reserved for exact quotes |
| D45 | after run 1 | The slice finds variables used by name inside expressions (`VALUEOF(...)`), matched by exact name, then ignoring letter case; names with no repository row are listed as "used by name, not defined in the repository" | Run 1's variables packet listed 1 variable while formulas used 9 more |
| D2 | setup || D34 | 2.2 | **One agent, `obiee-migration-agent`, with skills `obiee-as-is` and `obiee-review`.** The skills were first named `as-is` and `review`; the lead agent had them renamed | They sat confusingly next to BMad's `bmad-review` in the client's skill list |
| D35 | 2.2 | **A seventh jar command, `assemble-doc`**, joins the section files into the document | The skill was using shell commands that differ between Linux and Windows and were untested on Windows. The command also refuses to overwrite a document holding an answer you typed that is not yet recorded |
| D36 | 2.2 | The skill writes each unit's section as its own small file and the summary last; the document is only ever produced by `assemble-doc` | The summary sits on top but is written last, and a correction must not mean rewriting the whole document. Suits a short-context model |
| D37 | 2.2 | Each packet marks the line of every required row with `(REQUIRED)` | So the skill needs no separate list of what must be covered |
| D38 | 2.3 | `check-doc` names the unit each finding belongs to | So the skill can go straight to the section to fix |
| D39 | 2.2 | A question also carries two extra lines, `Raised in:` and `Assumed meanwhile:` | So the reader knows where it arose and what the text assumes until it is answered |
| D40 | 2.2 | A corrected answer is added as a further entry; the last one applies. Nothing in the answers file is ever changed or removed | Your answers are kept as you gave them |
| D2 | setup || D28 | 2.1 | **Deliberate difference 25: a logical table comes into a slice whole**, including columns the subject area does not show, marked as such | You asked for today's semantic model to be complete, and the old document mentions such a column (`Revenue (INR)`). Sent back by the lead agent |
| D29 | 2.1 | Lineage rows are not copied for those not-shown columns | Lineage is keyed by presentation column, so copying would pull another subject area into the slice. The column's source mapping is copied and gives the same database column |
| D30 | 2.1 | Each slice's overview lists, as counts, estate-wide items that are in no slice (for example permissions with no recorded target), and where to find them | So a reader knows they exist |
| D31 | 2.1 | Deliberate differences 23 and 24: a slice carries the security rows about any of its rows, and every reference resolves inside the slice | Completeness of the slice |
| D32 | 2.1 | Work units: overview, one per logical table, joins, hierarchies, variables, security, reports, saved filters, and a gaps unit at the end | Variables and security are separate sections; errors and unread content are too long for the overview |
| D33 | 2.3 | The text rules a skill must follow so its document can be checked are fixed in `docs/skill-conventions.md`: the status line, `[kb:...]` and `[answer:Q<n>]` citations, the three fixed headings, and the layout of a question (question, Why it matters, How to get the answer, Answer) | `check-doc` needs exact, simple rules |
| D2 | setup || D26 | 1.10 | Deliberate difference 22: where a presentation column has several lineage rows, the report link uses the first in `lineage.csv` order | The old program's choice depended on OBIEE internal file names that the Knowledge Base does not hold. On the samples 47 columns have several rows and the two rules differ for 8 |
| D27 | 1.11 | The time stamp in `manifest.csv` is reported as not comparable, counted neither as a match nor a difference | It is the time of the run |
| D2 | setup || D22 | 1.8, 1.9 | **Difference 8 means two things.** A column or prompt formula is stored whole, nested parts included. A filter row stores only its own text, plus text of nested parts that have no row of their own | The first build stored a group filter's children run together (`"Flight Facts"."# of Flights"20`), which repeats the child rows and reads like an expression when it is not one. The lead agent sent it back. After the change, the 443 filter cells that still differ from the reference differ only in white space |
| D23 | 1.9 | Saved-filter conditions are rows in `analysis_filters` with scope `saved_filter`; `saved_filters.csv` keeps one row per saved filter | One place to look for any filter |
| D24 | 1.9 | References from one saved filter to another are now resolved (76 found: 57 resolve, 19 give error rows) | So a row that carries a reference also says how it resolved |
| D25 | 1.8 | The catalog reader handles one object at a time and releases it | Keeps memory under 100 MB on the sample; output is byte-identical |
| D2 | setup || D18 | 1.7 | Deliberate difference 20: a catalog folder that cannot be listed gets an error row; the old program skipped it silently | Nothing should vanish without a trace |
| D19 | 1.7 | Deliberate difference 21: a file name that cannot be fully decoded is decoded as the old program did and also listed in `unparsed.csv` (6 such files in the sample) | So the oddity is visible |
| D20 | 1.7 | Files are decoded as strict UTF-8 before parsing | The JDK parser otherwise prints an error to the console for each of the 681 non-UTF-8 files in the sample |
| D21 | 1.7 | `.git` folders are left out and `.gitignore` is counted as not a catalog object, as the old program did | Port rule |
| D2 | setup || D15 | 1.6 | **Deliberate difference 19: a variable's value is also withheld when it equals a value that is withheld by name.** | The old program decides by the variable's name only. In `bi-analytics-2013` a variable with "USER" in its name holds exactly the same text as the variable with "PASSWORD" in its name, so the old program wrote that text out. Withholding more is the safe side |
| D16 | 1.6 | Two more schema layers, `var` and `sec`, for variables, init blocks and security rows | They belong to none of the four layers |
| D17 | 1.6 | `physical_joins.columns` holds one Row identifier per line | Same form as other reference lists |
| D2 | setup || D12 | 1.3 | Dimensions, levels and variables are given names in story 1.3, ahead of their readers in 1.6 | Three of the four reading-order errors point at a level or a variable, so they could not be removed otherwise. Story 1.6 folds this into its readers |
| D13 | 1.3 | The comparison tool reports differences explained by a numbered deliberate difference separately from unexplained ones, and exits 0 only when nothing is unexplained | So an explained difference never hides an unexplained one |
| D14 | 1.5 | A derived column produces no lineage row, following the Python. The story text had said "a row with empty physical identifiers"; the Python wins under the port rule | Port rule |
| D2 | setup | Stories are built by delegated build agents and verified by the lead agent, not through the full BMad build ceremony with its approval stops | Kamal asked for an unattended run; the ceremony stops for a human at several points | n/a |

## Open points for Kamal

| # | Story | Open point | What it blocks |
|---|---|---|---|
| O50 | 4.3 | Nothing in the skills stops a review or a design when `decisions.csv` was not written by the Operator; the status can become reviewed on test decisions. A check (for example a signed-by line in `decisions.csv`) would prevent it | Mistaking a test design for a real one |
| O51 | 4.2 | The to-be layout has no place for the decision notes of confirmed items; the review added them | Visibility of caveats a person wrote when confirming |
| O48 | 4.1 | The tab Slice's manifest records only counts of decisions, so a decision changed without changing the counts is not detected as a change. A hash of `decisions.csv` would close this | Regeneration after a decision changes |
| O49 | 4.2 | The rule for time-series functions (AGO, TODATE) has not been exercised: the test tab has none | Confidence on a real tab with year-on-year measures |
| O47 | 3.2 | When the match review finds a mismatched `row_id` whose basis plainly describes another column, it can only make the row unmatched. A rule that lets it propose the column the basis describes (at a capped score) would save the Operator work | Operator effort |
| O46 | 3.2 | There is no jar command to look up the dashboard page an FSD replaces (`dashboard_pages` has 1,037 rows in the sample catalog). The skill asks for the page's identifier instead. A small `find-pages` command would close this | Comparing matches with the existing report automatically |
| O44 | 2.2 | Taking in answers needs a resume procedure, and a rule for other sections that point at an answered question | Reliability after an interruption |
| **O45** | Epic 3 | **A real FSD is still needed.** The trace skill is being built and tested on an invented one | Confidence that tracing works on your real FSDs |
| **O41** | 2.2 | **Recommendation: let the jar write the mechanical parts of the document.** The column tables of database tables, the mapping tables, and the join tables are pure copying from packet lines. Both run 3 writers found them the heaviest work, and one broke the no-scripts rule to do them. If `slice` wrote these tables ready-made (as a second file per unit), the AI would write only the prose, the observations and the questions. This would cut the document's AI-written share and the context needed per unit substantially, and remove a whole class of copying errors. Not done: it changes the design and needs your yes | Running at scale on a short-context model |
| O42 | 2.2 | The notes file grows with every unit (about 13,000 to 25,000 tokens by the end). Both run 3 writers stopped re-reading it whole. Either the notes should be kept shorter (one line per unit) or the rule should say "read the notes' summary lines" | Short-context model |
| O43 | 2.1 | The security packet does not say that a source's content filter and its row filter are the same text when they are; the writer had to work it out | Clarity of the security section |
| O39 | 2.1 | The gaps packet lists internal id and name attributes of wrapper elements (for example `WhereClause@mdsid`) as unread, which reads as if the content was missed. It was not. The packet should mark such rows as "identifier only, content read" | A misleading open question (Q13 in Sample Sales) |
| O40 | 2.2 | The writer's notes can read meaning into names ("users can type values in"), and these are only reviewed if they reach the summary | Small risk of an unreviewed claim in the opening |
| O38 | 2.2 | The as-is document for Sample Sales is 1,627 lines for 49 fields and 13 database tables. Re-reading the whole notes file before every unit grew to about 13,000 tokens per read by the end; the writer read only the tail twice. On a short-context model this is the main cost | Running in GitHub Copilot with GPT-5.6 Sol |
| O36 | after run 2 | Deliberate differences 34 to 37 need your yes | Nothing now |
| O37 | after run 2 | `nullable not stated` most likely means NOT NULL, but nothing in the files confirms it; check one table against the database | Exact column definitions |
| O34 | after run 2 | Deliberate differences 28 to 33 (and those that follow from D52) need your yes | Nothing now |
| **O35** | after run 2 | **What a table-builder still cannot get from the repository at all:** a primary key for the fact tables (none is recorded); native database types, lengths, indexes, constraints, defaults, partitioning (the repository holds OBIEE's types only); proof that the recorded column order is the database's. These need the database's own table definitions | Final table scripts |
| **O31** | 2.2 | **The as-is document is not yet enough to create tables from.** The independent reviewer found: no primary key or grain recorded for any table (keys are not extracted, an old gap); whether a column may be empty is left out of the document; only database columns that are mapped are listed. These are being fixed in the jar and the skill | Using the document as the brief for new tables |
| O32 | 2.2 | The document is long and dense: 1,024 lines and 575 row references for one small subject area. A larger subject area will be several times that. Consider whether references should move to a column or footnotes, or whether a short reader's edition is wanted beside the full one | Readability for business readers |
| O33 | 2.2 | 19 open questions remain for Sample Sales, including which of four revenue tables OBIEE picks for a query and what the three currency measures mean. None can be answered from the export; they need you or someone with access to OBIEE and the database | A design built before they are answered rests on guesses |
| O28 | after run 1 | Deliberate differences 26 and 27 need your yes. The letter-case fallback for variable names widens confirmed difference 13 slightly (one case in the samples) | Nothing now |
| O29 | after run 1 | The packet limit counts lines, not characters; one 103-line packet is about 23,000 characters | Tuning for the production model |
| O30 | after run 1 | Grants made to a named user (`PrivilegePackage@userRef`) are still not read, as in the old program; they show in `unparsed.csv` | Describing per-user grants |
| O27 | 2.2 | **Run 1 found that the extract does not say what a foreign key points to.** All 14 foreign keys in Sample Sales have their far side missing, so the document cannot state how a fact table joins a dimension table at the database level. The old Python had the same gap. It is being fixed in the jar as a new deliberate difference, which will need your yes | Building tables from the as-is document |
| O25 | 2.2 | In a question's "How to get the answer", the skill may name OBIEE Administration Tool screens from the model's own knowledge. It is told to name a screen only when sure, but nothing checks it | A how-to step could point at the wrong screen |
| O26 | 2.2 | `build.cmd skills` and `build.cmd compare-all` have never been run (no Windows machine here) | Building on Windows |
| O21 | 2.1 | Reports landing in a slice is proven only on hand-made data; no real sample report sits on a real sample subject area | Confidence in the report-level section on a real estate |
| O22 | 2.1 | Deliberate differences 23, 24 and 25 need your yes | Nothing now |
| O23 | 2.1 | Two kinds of object can share one identifier text (a dimension named like a logical table; a foreign key named like a column). One citation then covers both rows | Rare ambiguity in citations |
| O24 | 2.1 | The 400-line packet limit has not been tried in GitHub Copilot | Tuning for the production model |
| **O17** | 1.11 | **Read `kb-comparison-report.md` and confirm.** Epic 1 stays open until you do. Deliberate differences 14 to 22 were added during the build and also need your yes; 1 to 13 you confirmed on 23 November | Closing Epic 1 |
| O18 | 1.10 | Linking report columns to the repository is proven only on hand-made data. The comparison shows the jar agrees with the old program; it does not show either is right about a real OBIEE estate | Trust in report lineage until a real matched export is available |
| O19 | 1.10 | Two presentation columns or subject areas whose names differ only in letter case resolve to whichever was read last, silently (old behaviour, kept) | Rare; could mislink on a case-sensitive estate |
| O20 | 1.11 | The review of the differences list rests on the stories' notes and the comparison. Behaviour the samples never exercise was not read line by line against the Python | Unknown differences in untested paths |
| O15 | 1.9 | 443 filter cells differ from the reference only in white space, because text after a nested element is now kept and nothing is trimmed. If you prefer exact equality there, the rule becomes "own text, unless it is all white space" | Nothing |
| O16 | 1.8 | A binned column's formula is stored as run-together text; the bin structure itself is not modelled and still shows in `unparsed.csv` | Describing binned columns precisely |
| O12 | 1.7 | Deliberate differences 20 and 21 need your yes | Nothing now |
| O13 | 1.7 | Each catalog file is read whole into memory. Fine on the sample (largest file 16 MB); a catalog with files of several hundred MB would need a different approach | Very large catalogs only |
| O14 | 1.7 | Whether the JDK's XML parser accepts exactly the same files as the old program's parser has only been measured on this one sample catalog | Confidence on a real catalog |
| **O9** | 1.6 | **A withheld value is present in the copied reference.** `reference/knowledge-base/variables.csv`, copied unchanged from POC-RPD, holds in one row the same text the old program withheld in another (see D15). The same file is in the POC-RPD repository on GitHub. The text comes from a public third-party sample repository dated 2013, so it is probably not a live secret, but that is your call. I did not edit the reference copy. Decide whether to redact it here and in POC-RPD | Nothing in the build |
| O10 | 1.6 | The same text could appear inside a longer value, or in an init block's SQL, which is never withheld (old behaviour, kept). Decide whether init block text should be scanned too | Nothing now |
| O11 | 1.6 | Deliberate difference 19 and the two new schema layers need your yes | Nothing now |
| O5 | 1.3 | In a column mapping, the column recorded is the first reference that resolved (as in the Python). Should it be empty when the first reference fails? Not seen in the samples | Nothing now |
| O6 | 1.3 | An empty reference-list cell means either "no references" or "one unresolved reference"; only `errors.csv` tells them apart | Nothing now |
| O7 | 1.5 | A derived column (a formula with no database column behind it) has no row in `lineage.csv`. Confirm that is what you want, or ask for a row with empty database columns | Nothing now; the slicer (2.1) includes such columns by another route |
| O8 | 1.1 | `build.cmd` (Windows) has never been run, and its `compare` takes at most eight arguments | Building on Windows |

## Testing record

| Story | What was run | Result |
|---|---|---|
| 4.2, 4.3 | **To-be run 1** on the test tab by a fresh agent | 1,594 lines; 12 new tables and 1 view; 113 old-to-new column rows; 4 model measures and 3 FSD measures, all with proposed definitions; 18 FSD items placed, 3 unresolved; 14 decisions needed; `check-doc` 256 of 256, 0 findings on first run. It raised the bill-date versus order-date mismatch as a decision with options |
| 4.3 | **Independent review of the to-be design** by a separate fresh agent | 12 findings corrected or asked (5 unsupported, 2 missing, 2 bad questions, 1 unclear turned into a new decision). Status `reviewed 2026-11-24`; lead agent re-ran `check-doc`: 256 of 256, 0 findings. Copies in `documents/implementation-artifacts/skill-runs/to-be-run-1/` |
| 4.3 | The reviewer's verdict as Data Engineer and Power BI developer | **Tables:** mostly buildable column by column; still needs keys of the five fact tables, decimal places, volumes and load, a target platform. **Model:** buildable for offices, products, day-level time and revenue measures; not reliably for targets by month (no target-to-time relationship), revenue per sales rep (the foreign key goes to the parent-child table), net revenue (is Revenue already net?), year (bill or order date), currency switch, security. Three FSD items have no design: own-offices security, region, active customers |
| 4.4 | End to end | The chain has been run in pieces on sample data: extract, compare, as-is (written, reviewed, answers kept), FSD text, trace (reviewed), test decisions, tab slice, to-be (reviewed). README now opens with the eight steps; migration plan section 6 updated. SM-1 is met **on sample data and test decisions only** |
| 4.2, 4.3 | To-be skill and to-be review written; lead agent re-ran `./build.sh` | 267 tests passed. Skill author's dry check on 3 units of the test tab: 0 findings other than rows of units not yet written |
| 4.1 | Lead agent re-ran `./build.sh` and `slice --tab` on the test tab | 267 tests passed. Test tab: 26 confirmed and 1 rejected decision; 18 of 21 items confirmed; 11 confirmed columns start the walk; 30 units, largest 120 lines; 256 required rows; the as-is document for Sample Sales is found and reported as reviewed. `compare-all` still 0 unexplained (build agent's run) |
| 4.1 | `find-pages` on the sample catalog for "overview" | 124 candidates; the top one is a page with 4 reports on "A - Sample Sales", printed with its Row identifier to cite (build agent's figure) |
| 2.1 | Gaps packets re-cut with identifier-only rows listed apart | Only the gaps unit of each of the 21 subject areas changed; `check-doc` still 0 findings on both as-is documents |
| 3.2 | **Blind review of traced matches.** Lead agent copied trace run 1's matches and changed 3 rows to plausible wrong columns (Product brand to Brand Key; Units sold to the target units; Office to the sales rep's office ID), leaving score and basis as they were; `check-matches` still passed. A fresh agent reviewed the copy without being told | **3 of 3 planted errors found;** each made unmatched with the right column named for the Operator. Answer key and review log in `documents/implementation-artifacts/skill-runs/trace-run-1/` |
| 3.2 | What the match review found that the trace had missed | **The time join is on the bill date, not the order date.** The FSD's Year filter is "calendar year of the order"; the review read the joins section of the as-is document and found revenue joins Dim Time on BILL_DAY_DT, and that an Order Day Dt column exists. It lowered Year from 55 to 40, named Order Day Dt for the Operator, and rewrote the related question. It also lowered two scores that broke the near-equal-candidates cap, and rewrote one how-to step that named a team instead of a step |
| 3.2 | **Trace run 1.** A fresh agent followed the agent and trace skill on the test FSD (`work/sample-sales-monthly/fsd.docx`). It was not allowed to read the FSD's source text or search the Knowledge Base files itself | Finished. 21 items that need data plus the page the tab replaces; 29 match rows (27 proposed, 2 unmatched); 9 questions. `check-matches` 0 failures, 1 warning (Preferred Currency is a derived column, expected). Files copied to `documents/implementation-artifacts/skill-runs/trace-run-1/` |
| 3.2 | How trace run 1 handled the traps in the test FSD | **Region:** unmatched (15), with a question asking whether Area Name is the sales region. **Active customers:** unmatched (15); it noted Order Number identifies orders, not customers. **Net revenue:** two rows, Revenue and Discnt Value, with the formula stated as the FSD's reading and a question whether Revenue is already net; the same split carried into revenue vs target and average price. **Units sold:** the actuals, with the target units named as the runner-up to avoid. **Manager sees own offices:** proposed Office Key at 35 only, with a question asking where the manager-to-office link is kept. **Preferred currency:** found the currency switch formula, and noted it covers revenue only, not discount or target |
| 3.2 | Limits of trace run 1 | One invented FSD on one subject area, written by the lead agent. The page the tab replaces could not be looked up (no catalog for this estate). `--formula-contains` marks matching columns but does not print the formula, so it was of little use |
| 3.2 | Trace skill and match review written; lead agent re-ran `./build.sh` | 256 tests passed. Skill author's dry check on 3 items of the test FSD: Product brand matched to Brand at 80; Region unmatched at 15 with the reason; Net revenue matched to two rows (Revenue 70, Discnt Value 65) with the calculation stated as the FSD's, not OBIEE's; `check-matches` 0 failures |
| 3.1, 3.3 | Lead agent re-ran `./build.sh` and `compare-all` after the Epic 3 Java commands | 256 tests passed, 0 failed; 0 unexplained differences |
| 3.1 | `fsd-text` on the test FSD (`work/sample-sales-monthly/fsd.docx`, generated with the JDK only and opened in LibreOffice to confirm it is a real Word file) | 43-line `fsd.md` with an anchor on every paragraph and table cell; with anchors removed it equals the source text character for character (a test checks this) |
| 3.2 (Java part) | `find-columns` for each item of the test FSD on `kb/sampleapp` | Usable candidates for every item that exists: Net revenue, Units sold, revenue target, Office, Product brand (Brand first), Year, month, sales representative, discount (Discnt Value first). "Region" finds nothing in Sample Sales and "customers" finds nothing at all, as they should. Code columns (Office Key, Brand Key) rank as high as names; the skill has to judge |
| 3.3 | `check-matches` on a hand-made set with one fault of each kind | Each of 11 planted faults found exactly once |
| 2.2 | **Answers test.** On a copy of the reviewed Sample Sales document, a fresh agent took in four test answers (three given in chat, one typed into the document's `Answer:` line), then regenerated the whole document from scratch | **Passed.** `assemble-doc` refused to overwrite while the typed answer was unrecorded, and printed it. All four answers were recorded in the answers file before any section changed, and moved to Answered questions. After regeneration the answers file was byte-identical (checked with `cmp`), all four answers were applied again with `[answer:Q<n>]` citations, and `check-doc` reported 0 findings (16 answer citations). Copies in `documents/implementation-artifacts/skill-runs/answers-test/` |
| 2.2 | Answers test: the skill pushed back on a wrong answer | The test answer to Q4 said one source already holds converted amounts. The agent noticed that in that source all four revenue measures map the same single column, so they cannot each hold a different currency. It stated both the answer and the recorded mapping and raised a new question (Q15) instead of accepting the answer silently. It did the same for Q10 (Q16) |
| 2.2 | Answers test: limits | Taking in answers has no resume procedure: after the cut-off the agent resumed only by inspecting files, and a fresh session could apply an answer twice or miss one. Other sections that point at an answered question are not covered by the skill (the agent fixed them by judgement) |
| 2.2 | **Run 3b, writing, second subject area** `Iryna_Bradzetskaya_MT_Row-level_Security` (repository bi-analytics-2013), chosen because it has row-level security and is not the subject area the skill's examples came from | Finished after one resume (usage limit). 1,875 lines from 43 parts; 37 of 37 units; 14 open questions; 15 observations; nothing left undescribed. Lead agent re-ran `check-doc`: 382 of 382 required rows covered, 894 citations, 119 quotes, 0 findings, 2 expected warnings. Both row filters are quoted exactly and restated mechanically. Not reviewed (to save usage); copy in `documents/implementation-artifacts/skill-runs/run-3b-row-level-security/` |
| 2.2 | Where run 3b showed the skill shaped around Sample Sales | The security layout assumed permissions and roles; this area has only row filters, which name roles as text inside a session variable. 7 of its 11 database tables are queries kept in the repository, some of which filter or deduplicate data, which the "do not say what a query does" rule makes hard to explain. One how-to step again named an OBIEE screen (left for the review) |
| 2.2 | Rules the run 3b writer broke | It used one-line `sed`/`awk` commands to build long column tables and mirror sections from packet lines, then checked them against the packet markers. It also read only the new tail of the notes file from unit 6 on (the notes reached about 25,000 tokens). Both say the same thing: the mechanical parts are too big to copy by hand in a short context |
| 2.4 | **Run 3, independent review (Sample Sales).** Separate fresh agent, new session, all 28 packets re-read from disk | Finished. 26 findings: 25 corrected (mostly sentences that read a run-time effect into a formula, such as "picks one of three fields"), 1 turned into a new question (Q14). Status `reviewed 2026-11-23`; `check-doc` 274 of 274, 673 citations, 0 findings. Reviewed copy and log in `documents/implementation-artifacts/skill-runs/run-3-sample-sales/` |
| 2.4 | The reviewer's verdict as a table-builder, run 3 | **Now enough:** all 13 database tables with every column's type, precision, scale, nullable as recorded and mapping; keys; all 14 foreign keys; every field-to-column mapping per source; measure rules; joins with cardinalities; drill paths with level keys. **Still missing:** no key on any fact or aggregate table; which of the 5 revenue sources serves a query; run-time values of session variables; meaning of several measures; reports and dashboards not examined (no catalog half for this estate) |
| 2.4 | Lead agent checked the reviewer's point that content filters may not have been read (question Q13) | False alarm in substance: the filter text is read and is empty for all 17 sources; only the wrapper element's internal id and name attributes are listed as unread. The gaps packet does not make that distinction clear, so the writer asked about it |
| 2.2 | **Run 3, writing (Sample Sales).** Fresh agent, corrected skills, 28-unit slice, no helper scripts | Finished. 1,627 lines from 34 parts; 28 of 28 units; 13 open questions (19 in run 2; overlap reduced with "See also" lines); 20 observations; nothing left undescribed. `check-doc` passed first time. Copy in `documents/implementation-artifacts/skill-runs/run-3-sample-sales/` |
| 2.2 | Lead agent re-ran `check-doc` on the run 3 draft | 274 of 274 required rows covered (every database column included); 660 citations, none unknown; 229 quoted texts all found in packets; 0 findings; 3 warnings, all expected (part-1 facts gathered in part 2's "Points to note") |
| 2.2 | Lead agent checked an oddity the writer flagged: every column's external name is `samplesales` | Real: it is in 124 source XML entries and in the reference Knowledge Base the same way. Not a jar defect |
| 2.2 | Limit of the Sample Sales runs | The skill's worked examples were drawn from Sample Sales, so the writer could copy them (it reported one open question follows an example "almost word for word"). A second subject area, from the other repository, is being written to test the skill on data it was not shaped around |
| 2.2, 2.4 | Last round of skill fixes (new packet layout plus about 40 points from runs 1 and 2); lead agent re-ran `./build.sh` | 224 tests passed. Skill author's dry check on 3 units of the 28-unit slice: 0 quote or citation findings in what it wrote |
| after run 2 | Lead agent re-ran `./build.sh` and `compare-all` after join paths, level order and parent-child definitions were added | 224 tests passed, 0 failed. 0 unexplained differences; report regenerated |
| after run 2 | Build agent's figures | `unparsed` rows: sampleapp 128 to 110, bi-analytics-2013 221 to 211. Sample Sales: 28 units, 274 required rows. The one multi-table source (`LTS Employee` of Dim Sales Rep) now states its inner join of EMPLOYEE to EMP_PARENT_CHILD on ANCESTOR_KEY = EMP_ID. The Sales Rep parent-child hierarchy now states its relationship table and its member, parent, distance and leaf columns |
| after run 2 | Lead agent re-ran `./build.sh` and `./build.sh compare-all` after keys and full database columns were added | 222 tests passed, 0 failed. 117 table comparisons, 60,138 reference rows, 0 unexplained differences; report regenerated |
| after run 2 | Build agent's figures for the keys change | `unparsed` rows fell from 145 to 128 (sampleapp) and 238 to 221 (bi-analytics-2013). Sample Sales: 28 units (was 15), 272 required rows (was 125), 113 database columns listed (was 74), of which 43 are mapped by no logical column. A key is now known for 7 of its 13 database tables; the repository records none for the 5 fact tables and the helper query |
| 2.4 | **Planted-error test of the review.** 12 known errors were planted in a copy of the run 2 draft by one agent (3 wrong facts, 3 unsupported statements, 2 removed items, 2 correct quotes attached to the wrong item, 2 bad open questions). The mechanical check still passed on the planted copy, so only a careful reader could find them. A third fresh agent then reviewed the copy blind, with the answer key kept outside the project | **12 of 12 planted errors found and corrected.** The reviewer logged 22 findings in all. The other 10 were real weaknesses of the draft, and 8 of them are the same ones the independent reviewer of the real copy found separately. The answer key and the review log are kept in `documents/implementation-artifacts/skill-runs/planted-error-test/` |
| 2.4 | Limits of the planted-error test | One model family wrote, planted and reviewed. Several planted errors left a clue inside the document (a count that no longer added up, a gap in level numbers), so the reviewer could catch some without the packet. The two independent reviewers of the same text agreed on 8 findings but each also found things the other left: the review is good, not exhaustive. Not run in GitHub Copilot with GPT-5.6 Sol |
| 2.4 | **Run 2, independent review.** A separate fresh agent, in a new session, reviewed the run 2 draft with the review skill. No helper scripts; every packet re-read from disk | Finished. All 15 units read packet-then-section; no table row skipped. 14 findings, all about meaning (the mechanical check was already clean): 6 unsupported statements, 4 wrong facts, 4 poor how-to steps. All corrected in place. Status line now `reviewed 2026-11-23 (14 findings corrected, 0 turned into questions, 19 questions open)` |
| 2.4 | Lead agent re-ran `check-doc` on the reviewed document | Status reviewed; 125 of 125 rows covered; 575 citations; 112 quotes; 0 findings, 0 warnings |
| 2.4 | Examples of what the independent review caught | "The key of every level is a descriptive field" was wrong for one hierarchy; "LogicalKey was not read under Dimension" was wrong (only four of its attributes were unread); three how-to steps still named OBIEE screens as if verified |
| 2.4 | The reviewer's own verdict on the document, as someone who must build from it | Most useful: per-source mapping tables with exact types; the joins section with both sides of all 17 database joins; the points to note and open questions with runnable SQL. Weakest: no primary key or grain for any table; nullable left out; only mapped database columns listed; 19 core questions still open; heavy to read at 1,024 lines |
| 2.2 | Skills corrected for the 17 points from run 1; lead agent re-ran `./build.sh` | 217 tests passed, 0 failed |
| 2.2 | **Run 2, writing.** A fresh agent followed the corrected skills on the re-cut Sample Sales slice (15 units). No helper scripts allowed. It stopped at the draft, as the agent skill now instructs | `as-is/sampleapp/sample-sales.md`: 1,024 lines from 21 parts; 15 of 15 units; 19 open questions; 11 observations across sections; nothing left undescribed. `check-doc` passed on its first run. A copy of the draft is in `documents/implementation-artifacts/skill-runs/run-2-sample-sales/` |
| 2.2 | Lead agent re-ran `check-doc` on the run 2 draft | 125 of 125 required rows covered; 574 citations, none unknown; 112 quoted texts all found in the packets; 0 findings, 0 warnings |
| 2.2 | Resume, tested by accident | The writing agent was cut off twice by server errors, after 2 units were written. It resumed from the progress file using the skill's own steps and redid nothing |
| 2.2 | Limits of run 2 | The writer re-read the whole notes file at only 5 of 15 units (the newer tail at the rest). It reported 16 more places where the skill text is unclear, mostly layout details the skill does not fix (empty blocks, extra columns, how to cite a fact under a pointer) |
| after run 1 | Lead agent re-ran `./build.sh` after the jar fixes from run 1 | 217 tests passed, 0 failed |
| after run 1 | Lead agent re-ran `./build.sh compare-all` | 117 table comparisons, 60,138 reference rows, **0 unexplained differences**; the comparison report was regenerated |
| after run 1 | Build agent re-cut all 21 sample subject areas at the new 120-line limit | sampleapp: 33 units, largest packet 119 lines. bi-analytics-2013: 325 units (245 at the old 400 limit), largest 120 lines. Sample Sales: 15 units; all 14 foreign keys now state both sides; 10 variable names listed, 1 defined in the repository and 9 used by name only (build agent's figures) |
| after run 1 | Build agent ran the new per-section checks on the run 1 document | 2 quotes not found in any packet (a suggested SQL statement and a file path written as if quoted); 125 of 125 rows still covered |
| 2.2, 2.4 | **First real run of the skills (run 1).** A fresh agent that had never seen the project followed the agent and skill files to describe Sample Sales, then reviewed it. It was forbidden to read the old document, the port guides or the code | Finished. `as-is/sampleapp/sample-sales.md`: 840 lines from 18 parts, 14 of 14 units written, 19 open questions, nothing left undescribed. The review logged 16 findings and corrected all (9 unsupported statements, 5 wrong, 1 missing, 1 vague question). A copy is kept in `documents/implementation-artifacts/skill-runs/run-1-sample-sales/` |
| 2.2 | Lead agent re-ran `check-doc` on that document | 125 of 125 required rows covered; 543 citations checked, none unknown; 19 questions open; 0 findings |
| 2.2 | Lead agent read the opening and section list | Plain business language, citations after statements, a one-page summary, and the sections in the order of the old Sample Sales document |
| 2.4 | Honest limits of run 1 | The review was done by the same agent in the same conversation that wrote the text, so it is less independent than intended. It also took shortcuts the skill does not allow: it re-read only 2 of 14 packets from disk, and it wrote its own helper script to compare citations. Run 1 produced a list of 21 places where the skills or the packets were unclear or wrong; these are being fixed before run 2 |
| 2.2, 2.4 | Lead agent re-ran `./build.sh` after the skills and the follow-up changes | 204 tests passed, 0 failed. Includes the test that the skill copies in `.agents/skills/` and `.claude/skills/` are identical and within their line limits |
| 2.2 | Build agent's dry check: two units of Sample Sales written by hand, joined with `assemble-doc`, checked with `check-doc` | 6 parts, 131 lines; 15 of 125 required rows covered (all rows of the two units); 37 citations, none unknown; 3 open questions parsed. A typed answer made `assemble-doc` refuse to overwrite until it was recorded |
| 2.1, 2.3 | Lead agent re-ran `./build.sh` after the whole-table change | 192 tests passed, 0 failed |
| 2.1 | Lead agent ran `slice` on the sample repository: list mode, then `--all` | Lists 3 subject areas; Sample Sales gives 14 units; largest packet 205 lines; `Revenue (INR)` is in the packet marked "not shown in this subject area" |
| 2.1 | Build agent cut every subject area of both repositories | sampleapp: 3 areas, 32 packets, 158 required rows. bi-analytics-2013: 18 areas, 245 packets, 2,502 required rows. Largest packet 314 lines; no unit needed splitting (build agent's figures) |
| 2.1 | Sample Sales slice against the first attempt's slice | All 349 reference rows present, 0 missing (build agent's figure) |
| 2.1 | Citation syntax tested against all 58,299 identifiers in the three Knowledge Bases | 0 needed special handling (build agent's figure) |
| 2.3 | `check-doc` on a stand-in document citing every Sample Sales row, and on variants with one fault of each kind | 0 findings on the clean one; each fault gives exactly its finding (part of `./build.sh`) |
| 1.10, 1.11 | Lead agent re-ran `./build.sh` | 153 tests passed, 0 failed |
| 1.11 | Lead agent re-ran `./build.sh compare-all` | 3 estates, 117 table comparisons (39 tables each), 60,138 reference rows against 61,129 generated; 59,137 rows identical; **0 unexplained differences**. The report is `documents/implementation-artifacts/kb-comparison-report.md` (535 lines) |
| 1.10 | `report_lineage` on the sample catalog | 11,143 rows on both sides; 11,128 identical, 15 differ only in the copied formula text (difference 8). None resolves to the repository on either side, as expected: the sample halves come from different systems (build agent's figures, covered by the lead agent's compare-all run) |
| 1.10 | Jar against the Python on the hand-made matched pair | The 2 report-lineage rows are identical and equal the recorded expected results (build agent's report) |
| 1.10 | Jar against the Python on a new hand-made estate with 20 report columns covering every outcome | 17 rows identical; 3 cells differ, all difference 22 (build agent's report) |
| 1.8, 1.9 | Lead agent re-ran `./build.sh` after the narrowed filter-text rule | 138 tests passed, 0 failed |
| 1.8, 1.9 | Lead agent ran `extract` on the sample catalog with a 128 MB heap | Completes (about 2.4 s); exit 3 for the expected unresolved references |
| 1.8, 1.9 | Lead agent ran the full, unfiltered comparison of the catalog with the reference | **No unexplained differences.** `analyses` 1,607, `analysis_criteria` 1,789, `analysis_columns` 11,143, `analysis_views` 10,879, `analysis_prompts` 4, `saved_filters` 96: same row counts as the reference. `analysis_filters` 8,495 against 7,135: the 1,360 extra rows are saved-filter conditions (difference 9). Explained: difference 8: 459 cells, 9: 1,383, 10: 2 added columns, 11: 387, 21: 6. Only `report_lineage` is not yet written |
| 1.8, 1.9 | Lead agent re-ran the comparison on both repositories | No unexplained differences |
| 1.8 | Count of formulas the reference had cut short | 15 column formulas, all of the "binned" kind; the reference stored nothing for them. The `CAST(` shape from the port guide does not occur in the sample (build agent's finding) |
| 1.7 | Lead agent re-ran `./build.sh` | 122 tests passed, 0 failed |
| 1.7 | Lead agent ran `extract` on the sample catalog (`estates/sampleapp-v607`, 5,371 files) with a 256 MB heap | Finished in 1.8 seconds, about 394 MB peak memory. Exit 3 because of 990 unresolved references, which is expected: they point into a catalog folder the sample does not contain |
| 1.7 | Lead agent compared `dashboards`, `dashboard_pages`, `dashboard_contents`, `catalog_versions` with the reference | 111, 1,036, 3,718 and 60 rows, the same as the reference; 0 missing, 0 extra, 0 differing cells |
| 1.7 | Lead agent re-ran the full comparison on both repositories | Still no unexplained differences (nothing regressed) |
| 1.7 | Build agent compared catalog `errors`, `unparsed`, `coverage`, `coverage_summary`, filtered to object types read so far | No unexplained differences; 386 error rows fewer (difference 11), 6 extra not-read rows (difference 21). Unfiltered, differences remain for reports and saved filters, which stories 1.8 and 1.9 read (build agent's report) |
| 1.7 | Build agent ran the constructed catalog and the Python on the same pair | 1 dashboard, 1 page, 3 placements, all resolved; identical to the Python output (build agent's report) |
| 1.6 | Lead agent re-ran `./build.sh` | 101 tests passed, 0 failed |
| 1.6 | Lead agent re-ran `extract` on both repositories | Both exit 0; `errors.csv` empty (the reference has 2 reading-order errors each); not-read rows 146 (reference 146) and 239 (reference 238) |
| 1.6 | Lead agent ran the full, unfiltered comparison of all 25 repository and run tables with the reference, both estates | **No unexplained differences.** Every table has the reference's row count. Explained cells: sampleapp 26 (differences 5, 6, 7); bi-analytics-2013 98 (differences 5, 6, 7, 19) |
| 1.6 | Credential leak tests (part of `./build.sh`) | Pass on planted values and on both sample estates: no withheld value in any output file or console output |
| 1.6 | Build agent ran the Python reference on six hand-made test estates | Nothing unexplained (build agent's report; not re-run by the lead agent) |
| 1.3, 1.4, 1.5 | Lead agent re-ran `./build.sh` | 86 tests passed, 0 failed |
| 1.3, 1.4, 1.5 | Lead agent re-ran `extract` and compared nine tables plus `errors` with the reference, both estates | Every table has the same row count as the reference (for example `lineage` 72 and 1,501; `presentation_columns` 59 and 1,484; `logical_columns` 77 and 933). 0 missing rows, 0 extra rows, 0 unexplained differing cells. Explained differences: 8 cells from difference 5 and 10 from difference 6 across both estates |
| 1.3 | Same run, `errors.csv` | The reference's 4 reading-order errors (2 per estate) are gone, as difference 5 intends; `errors.csv` is empty for both estates |
| 1.3 to 1.5 | Build agent ran the Python reference on four hand-made test estates | Matched the Java output apart from differences 5 and 6 (build agent's report; not re-run by the lead agent) |
| 1.1, 1.2 | Lead agent re-ran `./build.sh` at 05:10 on 23 Nov | 68 tests passed, 0 failed; jar built |
| 1.1, 1.2 | Lead agent re-ran `extract` on `estates/sampleapp` and `estates/bi-analytics-2013` | Both exit 0 |
| 1.1, 1.2 | Lead agent re-ran the comparison of the three physical tables with the reference, both estates | 0 missing, 0 extra, 0 differing cells (22/139/12 and 78/957/32 rows) |

## Story status

| Story | Status | Notes |
|---|---|---|
| 4.2 | Done | |
| 4.3 | Done | |
| 4.4 | Done | On sample data and test decisions only |
| Epic 4 | Done | |
| 4.1 | Done | |
| 3.2 | Done | Trace run 1 and its blind review (3 of 3) |
| 3.3 | Done | |
| Epic 3 | Done | A real FSD is still needed (O45) |
| 3.1 | Done | |
| 3.3 | Built; closes with 3.2 | Its last check needs the trace skill's real output |
| 2.2 | Done | Run 3 Sample Sales reviewed; run 3b second subject area written; answers test passed. GitHub Copilot with GPT-5.6 Sol not verified |
| 2.3 | Done | |
| 2.4 | Done | Planted-error test 12 of 12; three independent reviews |
| Epic 2 | Done | Open points O25 to O43 apply |
| 2.2 | Built and run twice; final run pending | The reviewed Sample Sales document from run 2 is in `as-is/sampleapp/`. It will be regenerated once more after the jar gains keys and full database columns |
| 2.4 | Built and tested | Independent review done; planted-error test 12 of 12 |
| 2.1 | Done | |
| 2.3 | Built; closes with 2.2 | Its last check needs the real document from story 2.2 |
| 1.10 | Done | The jar now writes every table the reference has, plus the two schema files |
| 1.11 | **Waiting for Kamal** | Comparison report written; 0 unexplained differences. Epic 1 is not marked done until Kamal confirms |
| 1.8 | Done | |
| 1.9 | Done | One change sent back by the lead agent before closing (D22) |
| 1.7 | Done | Catalog and repository are read into one Knowledge Base |
| 1.6 | Done | The repository half is complete: 25 tables match the reference |
| 1.3 | Done | |
| 1.4 | Done | |
| 1.5 | Done | A derived column gets no lineage row, as in the Python (see O7) |
| 1.1 | Done | About 5,100 lines of Java including tests. `build.cmd` never run (no Windows machine) |
| 1.2 | Done | |

## Stories 1.1 and 1.2 — as reported by the build agent, then verified

**Tests it reports**

| What was run | Result reported |
|---|---|
| `./build.sh` | 68 tests passed, 0 failed. Includes the two architecture tests, a check that the jar needs only `java.base` and `java.xml`, a class-file version check, and four schema tests |
| `extract` on `estates/sampleapp` and `estates/bi-analytics-2013` | Both exit 0 |
| Compare `physical_tables`, `physical_columns`, `physical_table_definitions` with the reference | No missing rows, no extra rows, no differing cells; row order identical. sampleapp 22 / 139 / 12 rows; bi-analytics-2013 78 / 957 / 32 rows |
| Compare `unparsed` and `coverage`, limited to the object types read so far | No differences |
| Compare with no filter | Differences in `coverage`, `unparsed` and `errors`, all from object types whose readers come in later stories |

**Decisions it took**

| # | Decision | Why |
|---|---|---|
| D4 | The recording XML layer is its own package, `obi2pbi.in.xml` | So the catalog reader can reuse it without one reader importing another |
| D5 | Readers work in two phases: name every object first, then read rows | Builds in deliberate difference 5 from the start |
| D6 | Foreign keys are read into the model now; `physical_joins.csv` is still written in story 1.6 | So the coverage and not-read counts match the reference |
| D7 | The output folder is always emptied of its CSV files on each run; there is no `--clean` flag | Matches the architecture rule; the old program needed a flag |
| D8 | `extract` accepts only an estate folder; a bare repository tree is refused with a hint | One layout to explain |
| D9 | New error kinds `invalid_name` and `unreadable` for a bad name and an unreadable file | The old program stopped the run in those cases |
| D10 | `--timestamp` kept from the Python; manifest version is `0.1.0` | Lets tests fix the time in `manifest.csv` |
| D11 | A CSV reader for Knowledge Base folders was built now, earlier than planned | The comparison and round-trip tests need it |

**Open points it raised**

| # | Open point |
|---|---|
| O1 | It added entries 14 to 18 to the deliberate-differences list, marked "not yet confirmed": always clean, estate folder only, schema files, manifest values, and reporting a folder that cannot be listed. They need Kamal's yes |
| O2 | A stray file placed beside `rpd/` and `catalog/` is not reported; only files under `rpd/` are checked, as in the Python |
| O3 | The duplicate-identifier error message still uses the old project's wording ("Group Spec") |
| O4 | Schema rows are written in declared order (table, then column position), not sorted |

**Not done, as it reported**

- `build.cmd` is written and has never been run; there is no Windows machine here.
- The catalog is recognised and listed in the manifest but not read yet (that is stories 1.7 onwards).
- The Python reference was not run on the small fixtures; results were checked against the recorded expected results and, for the samples, against the reference Knowledge Base.
