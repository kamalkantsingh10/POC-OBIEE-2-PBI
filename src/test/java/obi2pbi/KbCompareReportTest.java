package obi2pbi;

import java.util.List;

/** The comparison report's wording rules: what a value looks like in it, and what is never shown. */
public final class KbCompareReportTest {

    public void testAValueIsShownOnOneLineAndCutWithItsLengthGiven() {
        Check.equal("a\\n\\tb", KbCompareReport.shorten("a\n\tb"), "line breaks and tabs are shown as escapes");
        String cut = KbCompareReport.shorten("x".repeat(500));
        Check.that(cut.startsWith("x".repeat(140) + " ...("), "cut at a fixed length: " + cut);
        Check.contains(cut, "500 characters in all", "and the full length is given");
    }

    public void testACodeSpanSurvivesBackticksPipesAndEmptyValues() {
        Check.equal("(empty)", KbCompareReport.code(""), "an empty value is said in words");
        Check.equal("`a\\|b`", KbCompareReport.code("a|b"), "a bar would end a table cell");
        Check.equal("``a`b``", KbCompareReport.code("a`b"), "a longer fence around a backtick");
        Check.equal("`  x `", KbCompareReport.code(" x"), "leading space kept visible");
    }

    public void testACredentialIsNeverShown() {
        KbCompare.Result result = new KbCompare.Result("variables");
        result.columns = List.of("row_id", "rpd_id", "expr_text", "expr_refs");
        String[] row = {"e::var::USER", "e", "hunter2", ""};
        String cell = KbCompareReport.example("variables", result,
                new KbCompare.Change(19, KbCompare.Change.CELL, row, "expr_text", "hunter2", ""));
        Check.that(!cell.contains("hunter2"), "difference 19 names the row only: " + cell);
        Check.contains(cell, "e::var::USER", "the row identifier is given");

        String other = KbCompareReport.example("variables", result,
                new KbCompare.Change(0, KbCompare.Change.CELL, row, "expr_text", "hunter2", "hunter3"));
        Check.that(!other.contains("hunter"), "nor is any other value of the variables table: " + other);
        String missing = KbCompareReport.example("variables", result,
                new KbCompare.Change(0, KbCompare.Change.MISSING, row, "", "", ""));
        Check.that(!missing.contains("hunter2"), "nor a whole row of it: " + missing);

        String[] gap = {"e", "redacted_value", "Variable", "USER - value withheld"};
        KbCompare.Result unparsed = new KbCompare.Result("unparsed");
        unparsed.columns = List.of("rpd_id", "kind", "object_type", "detail");
        String extra = KbCompareReport.example("unparsed", unparsed,
                new KbCompare.Change(19, KbCompare.Change.EXTRA, gap, "", "", ""));
        Check.that(!extra.contains("USER"), "for difference 19 the content of the row is not shown: " + extra);
    }
}
