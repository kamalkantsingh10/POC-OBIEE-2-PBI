package obi2pbi;

import java.util.Arrays;
import obi2pbi.core.IdentifierException;
import obi2pbi.core.Identifiers;
import obi2pbi.core.Text;

/** Row identifiers are built one way (Architecture AD-4). */
public final class IdentifiersTest {

    public void testMakeAndParseRoundTrip() {
        String id = Identifiers.makeId("my-estate", Identifiers.PHYSICAL, "DB", "SCHEMA", "A Table", "COL #1");
        Check.equal("my-estate::phys::DB.SCHEMA.A Table.COL #1", id, "identifier");
        Check.equal(Arrays.asList("my-estate", "phys", "DB.SCHEMA.A Table.COL #1"),
                Arrays.asList(Identifiers.parseId(id)), "parsed back");
    }

    public void testNamesAreKeptExactly() {
        Check.equal("e::phys:: lead.Trail ", Identifiers.makeId("e", "phys", " lead", "Trail "), "no trimming");
    }

    public void testEstateNameMustBeLowerCaseWithHyphens() {
        for (String bad : new String[] {"TestRPD", "test_rpd", "test rpd", "-test", "test-", "a--b", ""}) {
            Check.raises(IdentifierException.class, () -> Identifiers.validateEstateName(bad), "estate '" + bad + "'");
        }
        Check.equal("bi-analytics-2013", Identifiers.validateEstateName("bi-analytics-2013"), "valid name");
    }

    public void testPartsThatCannotBeReadBackAreRefused() {
        Check.raises(IdentifierException.class, () -> Identifiers.makeId("e", "phys"), "no parts");
        Check.raises(IdentifierException.class, () -> Identifiers.makeId("e", "phys", "a", ""), "empty part");
        Check.raises(IdentifierException.class, () -> Identifiers.makeId("e", "phys", "   "), "only spaces");
        Check.raises(IdentifierException.class, () -> Identifiers.makeId("e", "phys", " "),
                "only a no-break space, which the reference also treats as white space");
        Check.raises(IdentifierException.class, () -> Identifiers.makeId("e", "phys", "a::b"), "contains ::");
        Check.raises(IdentifierException.class, () -> Identifiers.makeId("e", "nowhere", "a"), "unknown layer");
        Check.raises(IdentifierException.class, () -> Identifiers.parseId("e::phys"), "two pieces");
    }

    public void testCodePointOrder() {
        Check.that(Text.compareCodePoints("B", "a") < 0, "upper case before lower case");
        Check.that(Text.compareCodePoints("a", "ab") < 0, "prefix first");
        Check.that(Text.compareCodePoints("～", "😀") < 0, "U+FF5E before U+1F600");
        Check.equal(0, Text.compareCodePoints("x😀", "x😀"), "equal strings");
    }
}
