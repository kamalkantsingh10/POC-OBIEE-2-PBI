package obi2pbi;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import obi2pbi.core.Citation;
import obi2pbi.core.KbData;
import obi2pbi.core.KbRows;

/**
 * The citation form {@code [kb:<row identifier>]} and the rule that makes it unambiguous to read back
 * (Architecture AD-12; docs/skill-conventions.md).
 */
public final class CitationTest {

    private static List<String> found(String line) {
        List<String> ids = new ArrayList<>();
        for (Citation citation : Citation.find(line, 1)) {
            ids.add(citation.rowId);
        }
        return ids;
    }

    public void testAnIdentifierWithSpacesDotsQuotesAndBracketsIsReadBackExactly() {
        String[] ids = {
            "sampleapp::bmm::SAMPLESALES.Fact Revenue.Revenue (EUR)",
            "e::pres::A.\"Quoted\" name, with: punctuation; [and] brackets",
            "e::cat::shared/a]b/c\\d",
            "e::pres::A.ends with a bracket]",
            "e::pres::A.ends with a backslash\\",
            "e::pres::A.two\nlines\r",
            "e::pres::A. spaces at both ends ",
            "e::pres::A.[kb:nested]",
            "e::cat::path/report#c0.1#lineage",
        };
        for (String id : ids) {
            String line = "Revenue is summed " + Citation.of(id) + ", and again " + Citation.of(id) + ".";
            Check.equal(List.of(id, id), found(line), "read back: " + id);
            Check.that(!Citation.of(id).contains("\n"), "a citation stays on one line: " + id);
        }
        Check.equal("[kb:e::pres::A.x\\]y\\\\z]", Citation.of("e::pres::A.x]y\\z"), "']' is written '\\]' and '\\' is written '\\\\'");
        Check.equal("[kb:e::pres::A.plain name]", Citation.of("e::pres::A.plain name"), "anything else is written as it is");
        Check.that(Citation.isPlain("e::pres::A.plain [name") && !Citation.isPlain("e::pres::A.a]b"), "isPlain");
    }

    public void testSeveralCitationsOnALineAndOneThatIsNeverClosed() {
        List<Citation> found = Citation.find("| Revenue | sum | [kb:e::bmm::M.F.Revenue] [kb:e::bmm::M.F.Revenue#aggr0] |", 7);
        Check.equal(2, found.size(), "two citations");
        Check.equal("e::bmm::M.F.Revenue", found.get(0).rowId, "first");
        Check.equal("e::bmm::M.F.Revenue#aggr0", found.get(1).rowId, "second");
        Check.equal(7, found.get(1).line, "line number");
        Check.equal(" |", found.get(1).rest, "what follows it");
        List<Citation> open = Citation.find("see [kb:e::bmm::M.F.Revenue and so on", 1);
        Check.equal(1, open.size(), "an unclosed citation is returned");
        Check.that(open.get(0).rowId == null, "with no identifier, so it can be reported");
        Check.equal(List.of(), found("no citation here, only [brackets] and kb: text"), "nothing to find");
    }

    /**
     * Every Row identifier of the three sample Knowledge Bases, when they have been extracted into kb/:
     * each is written as a citation inside a sentence and read back. Prints how many needed escaping.
     */
    public void testEveryIdentifierOfTheSampleKnowledgeBasesIsReadBackExactly() throws Exception {
        int estates = 0;
        long total = 0;
        long escaped = 0;
        long edgeSpaces = 0;
        for (String estate : new String[] {"sampleapp", "bi-analytics-2013", "sampleapp-v607"}) {
            Path kb = TestSupport.root().resolve("kb").resolve(estate);
            if (!Files.isRegularFile(kb.resolve("manifest.csv"))) {
                continue;
            }
            estates++;
            KbData data = SliceFixture.read(kb);
            for (String name : data.tableNames()) {
                KbRows table = data.table(name);
                if (!table.has("row_id")) {
                    continue;
                }
                for (String[] row : table.rows) {
                    String id = table.get(row, "row_id");
                    total++;
                    escaped += Citation.isPlain(id) ? 0 : 1;
                    edgeSpaces += id.equals(id.trim()) ? 0 : 1;
                    List<String> back = found("It says so " + Citation.of(id) + ". Next sentence [kb:" + estate + "::pres::other].");
                    Check.equal(2, back.size(), "two citations found around " + id);
                    Check.equal(id, back.get(0), "read back exactly");
                }
            }
        }
        if (estates == 0) {
            throw new SkipException("no sample Knowledge Base under kb/");
        }
        System.out.println("      citation syntax: " + total + " identifiers in " + estates + " Knowledge Bases; needed escaping: "
                + escaped + "; with a space at either end: " + edgeSpaces);
    }
}
