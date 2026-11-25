package obi2pbi;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import obi2pbi.core.ColumnSearch;

/**
 * {@code find-columns}: a ranked list of the presentation columns a term may mean, by plain text matching
 * (story 3.2). Run on {@code src/test/fixtures/linked-estate}, the hand-made estate with both halves, whose
 * presentation table "Facts" has Amount, Margin (a formula), Note, Quantity, REGION (logical column "Region
 * Upper"), Region and Unbound (no logical column), and whose reports use several of them.
 */
public final class FindColumnsTest {

    private static final Pattern CANDIDATE = Pattern.compile("(?m)^([0-9]+)\\. \\[kb:([^\\]]*)\\]  \\(([^:)]+)");

    private static Path kb;

    private static Path kb() {
        if (kb == null) {
            kb = SliceTest.linkedKb(TestSupport.work("find-columns"));
        }
        return kb;
    }

    private static TestSupport.Run find(String... arguments) {
        List<String> line = new ArrayList<>(List.of("find-columns", kb().toString()));
        line.addAll(List.of(arguments));
        return TestSupport.run(line.toArray(new String[0]));
    }

    /** The candidates shown, as "row id | group". */
    private static List<String> shown(TestSupport.Run run) {
        Check.equal(0, run.exit, "exit code; err: " + run.err);
        List<String> found = new ArrayList<>();
        Matcher match = CANDIDATE.matcher(run.out);
        while (match.find()) {
            found.add(match.group(2).replace("linked::pres::Linked SA.Facts.", "") + " | " + match.group(3));
        }
        return found;
    }

    public void testWholeNamesComeFirstThenAllWordsThenSomeWords() {
        Check.equal(List.of("REGION | whole name", "Region | whole name", "Amount | some words", "Margin | some words",
                        "Note | some words", "Quantity | some words"),
                shown(find("Facts region", "--limit", "6")),
                "the table's name and the column's name is a whole name; then the columns that only share the table's "
                        + "name; ties in Row identifier order");
        Check.equal(List.of("REGION | all words", "Region | some words"), shown(find("upper region")),
                "all the words in another order is not a whole name, and outranks some of the words");
        Check.equal(List.of("REGION | whole name", "Region | some words"), shown(find("region-UPPER")),
                "the logical column's name counts as a whole name; case and punctuation are ignored");
    }

    public void testInsideAGroupTheColumnsOwnNameOutranksATableName() {
        Check.equal("Note | whole name", shown(find("facts note")).get(0), "the table's name, then the column's");
        List<String> found = shown(find("note facts"));
        Check.equal("Note | all words", found.get(0), "the other way round: all words, and first of them, as its own "
                + "name has one of the words");
        found = shown(find("note amounts"));
        Check.equal(List.of("Amount | some words", "Note | some words", "Margin | some words"), found,
                "a plural is the word; Amount and Note each have a word in their own name (the Row identifier "
                        + "orders them), Margin only in its formula");
    }

    public void testTheLimitCutsTheListAndSaysSo() {
        TestSupport.Run all = find("facts");
        Check.equal(7, shown(all).size(), "every column of the table, by its table's name, when the limit is above");
        Check.contains(all.out, "candidates: 7\n", "the count");
        TestSupport.Run two = find("facts", "--limit", "2");
        Check.equal(shown(all).subList(0, 2), shown(two), "the limit keeps the first ones, in the same order");
        Check.contains(two.out, "candidates: 7, the first 2 shown (--limit 2)", "says how many were left out");
        Check.equal(2, find("facts", "--limit", "0").exit, "a limit below 1 is wrong usage");
        Check.equal(2, find("facts", "--limit", "x").exit, "a limit that is not a number is wrong usage");
    }

    public void testEachCandidateSaysWhatTheSkillNeeds() {
        String out = find("amount", "--limit", "1").out;
        Check.contains(out, "1. [kb:linked::pres::Linked SA.Facts.Amount]  (whole name: amount; score 3)", "the citation and the match");
        Check.contains(out, "subject area \"Linked SA\", presentation table \"Facts\", column \"Amount\"", "where it is");
        Check.contains(out, "logical column [kb:linked::bmm::Linked BM.Fact.Amount]: not a measure", "the logical column");
        Check.contains(out, "database columns: [kb:linked::phys::LK.F_AGG.AMOUNT], [kb:linked::phys::LK.F_DETAIL.AMOUNT] "
                + "(2 lineage paths)", "the database columns through lineage");
        Check.contains(out, "report columns: ", "the report columns");
        Check.contains(out, "[kb:linked::cat::shared/linked/every outcome#c0.0] in report \"shared/linked/every outcome\"",
                "a report column that uses it, by its Row identifier");

        String margin = find("margin").out;
        Check.contains(margin, "formula: \"Linked BM\".\"Fact\".\"Amount\" - \"Linked BM\".\"Fact\".\"Quantity\"", "the formula");
        Check.contains(margin, "formula refers to: [kb:linked::bmm::Linked BM.Fact.Amount], [kb:linked::bmm::Linked BM.Fact.Quantity]",
                "what it is computed from");
        Check.contains(margin, "database columns: none through lineage (a derived column: computed by its formula)",
                "a derived column has no lineage");
        Check.contains(find("unbound").out, "logical column: none recorded", "a presentation column with no logical column");
    }

    public void testAbbreviationsCountForLessThanTheWord() {
        Check.equal(List.of("Quantity | all words", "Margin | all words"), shown(find("qty")),
                "an abbreviation the FSD uses; Margin has the word only in its formula, so it comes second");
        Check.that(ColumnSearch.likeness("discount", "Discnt".toLowerCase()) > ColumnSearch.likeness("discount", "dsc"),
                "the closer abbreviation counts more");
        Check.that(ColumnSearch.likeness("discount", "dsc") < 1, "and less than the word");
        Check.equal(0.0, ColumnSearch.likeness("month", "year"), "different words");
        Check.equal(1.0, ColumnSearch.likeness("offices", "office"), "a plural");
        Check.equal(List.of("discnt", "value", "order", "day", "dt"), ColumnSearch.words("DiscntValue order_DAY.Dt"),
                "words: split at capitals after small letters and at punctuation, lower case");
        Check.equal(List.of(), shown(find("customers")), "nothing that fits: no candidate");
        Check.contains(find("customers").out, "No presentation column matches.", "and says so");
    }

    public void testFormulaContainsFindsColumnsByWhatTheyComputeFrom() {
        Check.equal(List.of("Margin | formula"), shown(find("--formula-contains", "\"FACT\".\"amount\"  -")),
                "letter case and runs of spaces ignored; no term needed");
        Check.equal(List.of("Margin | all words"), shown(find("margin quantity", "--formula-contains", "amount\" -")),
                "with a term, the term ranks what the formula allows; a word of the formula counts");
    }

    public void testAMatchByFormulaShowsWhichFormulaMatchedAndTheTextAroundIt() {
        String out = find("--formula-contains", "\"fact\".\"quantity\"").out;
        Check.contains(out, "   matched in the derived formula: \"Linked BM\".\"Fact\".\"Amount\" - \"Linked BM\".\"Fact\".\"Quantity\"",
                "a short formula is shown whole, with what it is");
        Check.contains(find("--formula-contains", "F_DETAIL").out, "   matched in the mapping from the database [kb:",
                "a mapping names its row");
        String formula = "SUM(" + "\"x\" + ".repeat(40) + "CASE WHEN 1 THEN 2 END" + " + \"y\"".repeat(40) + ")";
        String excerpt = obi2pbi.core.ColumnSearch.excerpt(formula, "case when 1");
        Check.that(excerpt.startsWith("... ") && excerpt.endsWith(" ...") && excerpt.contains("CASE WHEN 1 THEN 2 END"),
                "a long formula: the part around the match, letter case kept: " + excerpt);
        Check.that(excerpt.length() < 140, "and short: " + excerpt.length());
    }

    public void testSubjectAreaAndUsageErrors() {
        Check.equal(7, shown(find("facts", "--subject-area", "Linked SA")).size(), "by name");
        Check.equal(7, shown(find("facts", "--subject-area", "linked::pres::Linked SA")).size(), "by Row identifier");
        TestSupport.Run unknown = find("facts", "--subject-area", "Nowhere");
        Check.equal(2, unknown.exit, "an unknown subject area");
        Check.contains(unknown.err, "The subject areas: Linked SA", "the names are listed");
        Check.equal(2, TestSupport.run("find-columns", kb().toString()).exit, "no term");
        Check.equal(2, TestSupport.run("find-columns", "build/no-such-kb", "x").exit, "no Knowledge Base");
        Check.equal(2, find("a", "b").exit, "two terms: a term of several words goes in quotes");
    }

    public void testTheSameSearchGivesTheSameText() {
        Check.equal(find("facts amount").out, find("facts amount").out, "the same text");
    }
}
