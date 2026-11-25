package obi2pbi;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import javax.xml.stream.XMLStreamException;
import obi2pbi.core.Estate;
import obi2pbi.core.Unparsed;
import obi2pbi.in.xml.ReadAudit;
import obi2pbi.in.xml.SourceTree;
import obi2pbi.in.xml.XmlNode;
import obi2pbi.in.xml.XmlTreeReader;

/**
 * The recording XML layer and the audit built on it (guide 03 §1 and §8.5). Every check that reports
 * something is paired with one showing it stays quiet when the content is read.
 */
public final class XmlLayerTest {

    private static final String DOC =
            "<Thing name='t' colour='red' xmlns='urn:x'>"
                    + "<Part size='1' pick='a'>words</Part>"
                    + "<Wrap><Part size='2' pick='b'/></Wrap>"
                    + "<Unknown flag='y'><Deep/></Unknown>"
                    + "</Thing>";

    // ------------------------------------------------------------------ the tree

    public void testNamespaceDeclarationsAreNotAttributes() throws Exception {
        XmlNode root = parse("<a:Root xmlns:a='urn:a' xmlns='urn:d' xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'"
                + " plain='1' xsi:type='a:kind'><Child/></a:Root>");
        Check.equal("{urn:a}Root", root.tag(), "full name");
        Check.equal("Root", root.localName(), "local name");
        Check.equal(List.of("plain", "{http://www.w3.org/2001/XMLSchema-instance}type"),
                new ArrayList<>(root.attributeNames()), "attributes: no xmlns entries, namespaced name in full");
        Check.equal("a:kind", root.attr("{http://www.w3.org/2001/XMLSchema-instance}type"), "namespaced attribute");
        Check.equal("{urn:d}Child", root.children().get(0).tag(), "default namespace applies to the child");
        Check.that(root.firstChild("Child") != null && root.firstChild("{urn:d}Child") != null,
                "lookup by local name or by full name");
        Check.that(root.firstChild("{urn:a}Child") == null, "a full name must match the namespace");
    }

    public void testTextIsTheTextBeforeTheFirstChildAndDeepTextIsAllOfIt() throws Exception {
        XmlNode root = parse("<e>CAST(<x>EVALUATE(<y>1</y>)</x>) tail</e>");
        Check.equal("CAST(", root.text(), "text stops at the first child element");
        Check.equal("CAST(EVALUATE(1)) tail", root.deepText(), "all text, nested parts included, in order");
    }

    public void testTakingTheWholeTextReadsTheNestedTextButDoesNotLookAtTheNestedElements() throws Exception {
        XmlNode root = parse("<e>CAST(<x k='1'>EVALUATE(<y>1</y>)</x>)</e>");
        XmlNode nested = root.children().get(0);
        Check.that(!nested.wasTextRead() && !nested.wasTouched(), "nothing read yet");
        root.deepText();
        Check.that(root.wasTextRead() && root.wasTouched(), "the element itself was read");
        Check.that(nested.wasTextRead() && nested.children().get(0).wasTextRead(), "the nested text was taken, at every depth");
        Check.that(!nested.wasTouched() && !nested.wasAttributeRead("k"),
                "but the nested element was not looked at: the audit still lists it, and its attributes");
    }

    public void testTextIsKeptCharacterForCharacter() throws Exception {
        XmlNode root = parse("<e>  lead &amp; <![CDATA[<raw> ]]>\n -1 * \"%1\"<!-- note --> more\t</e>");
        Check.equal("  lead & <raw> \n -1 * \"%1\" more\t", root.text(),
                "no trimming; entities and CDATA merged; a comment does not cut the text");
        Check.equal("", parse("<e/>").text(), "an element with no text gives the empty string");
        Check.equal("", parse("<e a='1'/>").attr("missing"), "an absent attribute gives the empty string");
        Check.equal("?", parse("<e/>").attr("mdsid", "?"), "or the stated fallback");
    }

    public void testEncodingDeclarationIsHonoured() throws Exception {
        byte[] latin1 = "<?xml version='1.0' encoding='ISO-8859-1'?><e n='café'/>".getBytes(StandardCharsets.ISO_8859_1);
        Check.equal("café", XmlTreeReader.parse(latin1).attr("n"), "ISO-8859-1 file");
    }

    public void testBrokenXmlIsRefused() {
        Check.raises(XMLStreamException.class, () -> parse("<a><b></a>"), "mismatched tags");
        Check.raises(XMLStreamException.class, () -> parse(""), "empty file");
    }

    // ------------------------------------------------------------------ the audit

    public void testWithNoReadsEverythingBelowTheRootIsReported() throws Exception {
        Estate estate = audit(parse(DOC));
        Check.equal(List.of(
                "unread_attribute Part@pick 2", "unread_attribute Part@size 2", "unread_attribute Thing@colour 1",
                "unread_attribute Thing@name 1", "unread_attribute Unknown@flag 1",
                "unread_element Deep 1", "unread_element Part 2", "unread_element Thing 1",
                "unread_element Unknown 1", "unread_element Wrap 1", "unread_text Part#text 1"),
                gaps(estate), "every attribute, element and text");
    }

    public void testWhatIsReadIsNotReported() throws Exception {
        XmlNode root = parse(DOC);
        root.attr("name");
        XmlNode part = root.firstChild("Part");
        part.attr("size");
        part.text();
        List<String> gaps = gaps(audit(root));
        Check.that(!gaps.contains("unread_attribute Thing@name 1"), "a read attribute is not reported");
        Check.that(gaps.contains("unread_attribute Thing@colour 1"), "its unread neighbour still is");
        Check.that(!gaps.contains("unread_text Part#text 1"), "text read through text() is not reported");
        Check.that(!contains(gaps, "unread_element Thing"), "the root of a handled object is not an unread element");
    }

    public void testReadsAreCountedPerElementNotPerTagName() throws Exception {
        XmlNode root = parse(DOC);
        XmlNode first = root.firstChild("Part");
        first.attr("pick");
        first.attr("size");
        first.text();
        List<String> gaps = gaps(audit(root));
        Check.that(gaps.contains("unread_attribute Part@pick 1"),
                "the same tag and attribute under another parent is still reported, once: " + gaps);
        Check.that(gaps.contains("unread_element Part 1"), "and so is that other element");
    }

    public void testAChildLookedUpButNotUsedIsNotAnUnreadElement() throws Exception {
        XmlNode root = parse(DOC);
        root.firstChild("Unknown");
        List<String> gaps = gaps(audit(root));
        Check.that(!gaps.contains("unread_element Unknown 1"), "looked for, so not an unread element");
        Check.that(gaps.contains("unread_attribute Unknown@flag 1"), "its unread attribute is still reported");
        Check.that(gaps.contains("unread_element Deep 1"), "and so is what lies below it");
    }

    public void testAWrapperWalkedThroughIsNotReported() throws Exception {
        XmlNode root = parse(DOC);
        for (XmlNode part : root.descendants("Part")) {
            part.attr("size");
        }
        List<String> gaps = gaps(audit(root));
        Check.that(!contains(gaps, "unread_element Wrap"), "something below the wrapper was read");
        Check.that(!contains(gaps, "unread_element Part"), "both parts were found, at any depth");
    }

    public void testAskingForAnAbsentAttributeCountsAsTouching() throws Exception {
        XmlNode root = parse("<Thing><Part/></Thing>");
        root.children().get(0).attr("notThere");
        Check.equal(List.of(), gaps(audit(root)), "nothing to report");
    }

    public void testLookingWithoutRecordingDoesNotSilenceTheAudit() throws Exception {
        XmlNode root = parse(DOC);
        root.rawAttr("name");
        root.rawText();
        root.attributeNames();
        for (XmlNode child : root.children()) {
            child.tag();
        }
        Check.equal(11, gaps(audit(root)).size(), "the non-recording accessors leave everything reported");
    }

    public void testBlankTextIsNotReportedAndUnhandledTypesAreSkipped() throws Exception {
        XmlNode root = parse("<Thing>\n   <Part>x</Part>\n</Thing>");
        root.firstChild("Part").text();
        Check.equal(List.of(), gaps(audit(root)), "white space alone is not unread text");

        Estate estate = new Estate();
        ReadAudit.audit(List.of(new SourceTree("e", "Other", "f.xml", parse(DOC))), Set.of("Thing"), estate);
        Check.equal(0, estate.unparsed.size(), "a type with no reader is reported whole elsewhere, not audited");
    }

    public void testScopeExclusionGoesUnderItsOwnKind() throws Exception {
        Estate estate = new Estate();
        ReadAudit.audit(List.of(new SourceTree("e", "Thing", "f.xml", parse(DOC))), Set.of("Thing"),
                XmlNode::tag, (tag, attribute) -> attribute.equals("colour"), Unparsed.EXCLUDED_BY_SCOPE, estate);
        List<String> gaps = gaps(estate);
        Check.that(gaps.contains("excluded_by_scope {urn:x}Thing@colour 1"), "excluded, still written: " + gaps);
        Check.that(!gaps.contains("unread_attribute {urn:x}Thing@colour 1"), "and not also as unread");
        Check.that(gaps.contains("unread_attribute {urn:x}Thing@name 1"), "other attributes unaffected");
    }

    public void testGapsAreNeverErrors() throws Exception {
        Estate estate = audit(parse(DOC));
        Check.equal(0, estate.errors.size(), "the audit writes gaps only");
        Set<String> kinds = Set.of(Unparsed.UNREAD_ATTRIBUTE, Unparsed.UNREAD_ELEMENT, Unparsed.UNREAD_TEXT);
        for (Unparsed gap : estate.unparsed) {
            Check.that(kinds.contains(gap.kind), "known kind: " + gap.kind);
            Check.equal("Thing", gap.objectType, "the object type is the file's, not the element's");
        }
    }

    // ------------------------------------------------------------------ helpers

    private static XmlNode parse(String xml) throws XMLStreamException {
        return XmlTreeReader.parse(xml.getBytes(StandardCharsets.UTF_8));
    }

    private static Estate audit(XmlNode root) {
        Estate estate = new Estate();
        ReadAudit.audit(List.of(new SourceTree("e", "Thing", "f.xml", root)), Set.of("Thing"), estate);
        return estate;
    }

    private static List<String> gaps(Estate estate) {
        List<String> lines = new ArrayList<>();
        for (Unparsed gap : estate.unparsed) {
            lines.add(gap.kind + " " + gap.detail + " " + gap.occurrences);
        }
        String[] sorted = lines.toArray(new String[0]);
        Arrays.sort(sorted);
        return Arrays.asList(sorted);
    }

    private static boolean contains(List<String> gaps, String prefix) {
        return gaps.stream().anyMatch(g -> g.startsWith(prefix + " "));
    }

    // ------------------------------------------------------------------ story 1.7: what the catalog reader needs

    public void testBelowLeavesTheElementItselfOutAndMarksWhatItFinds() throws Exception {
        XmlNode root = XmlTreeReader.parse("<a><a id=\"1\"><b/><a id=\"2\"/></a></a>".getBytes(java.nio.charset.StandardCharsets.UTF_8));
        java.util.List<XmlNode> below = root.below("a");
        Check.equal(2, below.size(), "the two inner elements, not the root");
        Check.equal("1", below.get(0).rawAttr("id"), "document order");
        Check.that(below.get(1).wasTouched() && !root.wasTouched(), "found elements are marked as looked for; the start is not");
        Check.equal(3, root.descendants("a").size(), "descendants() includes the element itself");
    }

    public void testATreeThatIsNotWantedIsCheckedButNotKept() throws Exception {
        byte[] good = "<big v=\"7\">text<row>1</row><row>2</row></big>".getBytes(java.nio.charset.StandardCharsets.UTF_8);
        XmlNode root = XmlTreeReader.parse(good, candidate -> !candidate.tag().equals("big"));
        Check.equal("big", root.tag(), "the root is returned");
        Check.equal("7", root.rawAttr("v"), "with its attributes");
        Check.equal(0, root.children().size(), "and nothing below it");
        Check.equal("", root.rawText(), "nor its text");
        Check.equal(2, XmlTreeReader.parse(good, candidate -> true).children().size(), "the same content, kept when wanted");
        byte[] bad = "<big><row>1</big>".getBytes(java.nio.charset.StandardCharsets.UTF_8);
        Check.raises(javax.xml.stream.XMLStreamException.class, () -> XmlTreeReader.parse(bad, candidate -> false),
                "the whole document is still checked for being well-formed");
    }

    public void testEncodingsAreHonouredAndBytesThatAreNotTextNeverReachTheConsole() throws Exception {
        java.io.PrintStream console = System.err;
        java.io.ByteArrayOutputStream printed = new java.io.ByteArrayOutputStream();
        System.setErr(new java.io.PrintStream(printed, true));
        try {
            byte[] latin = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?><a n=\"caf\u00e9\"/>".getBytes(java.nio.charset.StandardCharsets.ISO_8859_1);
            Check.equal("caf\u00e9", XmlTreeReader.parse(latin).rawAttr("n"), "a declared Latin-1 document");
            byte[] utf = "\uFEFF<?xml version='1.0' encoding='utf-8'?><a n=\"caf\u00e9\"/>".getBytes(java.nio.charset.StandardCharsets.UTF_8);
            Check.equal("caf\u00e9", XmlTreeReader.parse(utf).rawAttr("n"), "UTF-8 with a byte-order mark and a declaration");
            byte[] wide = "<a n=\"caf\u00e9\"/>".getBytes(java.nio.charset.StandardCharsets.UTF_16);
            Check.equal("caf\u00e9", XmlTreeReader.parse(wide).rawAttr("n"), "UTF-16 with a byte-order mark");
            byte[] undeclared = "<a n=\"caf\u00e9\"/>".getBytes(java.nio.charset.StandardCharsets.ISO_8859_1);
            Check.raises(javax.xml.stream.XMLStreamException.class, () -> XmlTreeReader.parse(undeclared), "Latin-1 bytes with no declaration are not UTF-8");
            byte[] image = {(byte) 0x89, 'P', 'N', 'G', '\r', '\n', 0x1a, '\n', (byte) 0xff, (byte) 0xd8};
            Check.raises(javax.xml.stream.XMLStreamException.class, () -> XmlTreeReader.parse(image), "an image");
            Check.raises(javax.xml.stream.XMLStreamException.class, () -> XmlTreeReader.parse(new byte[0]), "an empty file");
        } finally {
            System.setErr(console);
        }
        Check.equal("", printed.toString(), "nothing is printed: every failure is an exception");
    }
}
