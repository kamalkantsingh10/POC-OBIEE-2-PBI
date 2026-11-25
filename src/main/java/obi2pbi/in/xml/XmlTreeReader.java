package obi2pbi.in.xml;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

/**
 * Reads an XML file into a tree of {@link XmlNode}, using the JDK's streaming reader.
 *
 * <p>Text is kept character for character: nothing is trimmed and CDATA is merged into the surrounding
 * text. Comments and processing instructions are dropped. DTDs and external entities are switched off.
 * The file's own encoding declaration is honoured: UTF-8 and ASCII are decoded here, strictly, and any other
 * encoding is left to the parser, which is given the bytes.
 */
public final class XmlTreeReader {

    private static final XMLInputFactory FACTORY = factory();

    private XmlTreeReader() {
    }

    /**
     * @throws IOException        the file could not be read
     * @throws XMLStreamException the file is not well-formed XML
     */
    public static XmlNode read(Path file) throws IOException, XMLStreamException {
        // Read first, parse second, so "cannot be opened" and "does not parse" stay two different failures.
        return parse(Files.readAllBytes(file));
    }

    public static XmlNode parse(byte[] content) throws XMLStreamException {
        return parse(content, root -> true);
    }

    /**
     * Parses the whole document, so that it is known to be well-formed, and keeps the tree only when asked.
     *
     * @param keepTree given the root element (name and attributes only), says whether the tree below it is
     *                 wanted. When it is not, the root is returned alone, with no children and no text: a
     *                 large file of a type nobody reads then costs no memory.
     */
    public static XmlNode parse(byte[] content, Predicate<XmlNode> keepTree) throws XMLStreamException {
        XMLStreamReader reader = open(content);
        try {
            XmlNode root = null;
            boolean keep = true;
            List<XmlNode> open = new ArrayList<>();
            while (reader.hasNext()) {
                int event = reader.next();
                if (!keep) {
                    continue;
                }
                switch (event) {
                    case XMLStreamConstants.START_ELEMENT:
                        XmlNode node = element(reader);
                        if (open.isEmpty()) {
                            root = node;
                            keep = keepTree.test(root);
                        } else {
                            open.get(open.size() - 1).addChild(node);
                        }
                        open.add(node);
                        break;
                    case XMLStreamConstants.END_ELEMENT:
                        open.remove(open.size() - 1);
                        break;
                    case XMLStreamConstants.CHARACTERS:
                    case XMLStreamConstants.CDATA:
                    case XMLStreamConstants.SPACE:
                        if (!open.isEmpty()) {
                            open.get(open.size() - 1).appendCharacters(reader.getText());
                        }
                        break;
                    default:
                        break;
                }
            }
            if (root == null) {
                throw new XMLStreamException("no root element");
            }
            return root;
        } finally {
            reader.close();
        }
    }

    /**
     * Opens the parser on the content. A document in UTF-8 or ASCII (nearly every one) is decoded here,
     * strictly, and given to the parser as text; any other encoding is left to the parser.
     *
     * <p>The reason is the JDK's parser: given bytes that are not valid UTF-8 it throws, as it should, and
     * also prints a "[Fatal Error]" line to the console, which cannot be switched off through the public
     * API. A catalog holds hundreds of images and documents, each of which would print one. Decoding first
     * finds those files without the parser ever seeing them.
     */
    private static XMLStreamReader open(byte[] content) throws XMLStreamException {
        Charset charset = declaredCharset(content);
        if (charset == null) {
            return FACTORY.createXMLStreamReader(new ByteArrayInputStream(content));
        }
        int start = startsWith(content, UTF8_MARK) ? UTF8_MARK.length : 0;
        String text;
        try {
            text = charset.newDecoder()
                    .onMalformedInput(CodingErrorAction.REPORT)
                    .onUnmappableCharacter(CodingErrorAction.REPORT)
                    .decode(ByteBuffer.wrap(content, start, content.length - start))
                    .toString();
        } catch (CharacterCodingException e) {
            throw new XMLStreamException("the content is not valid " + charset.name() + ": " + e);
        }
        return FACTORY.createXMLStreamReader(new StringReader(text));
    }

    /**
     * UTF-8 or US-ASCII when the document is in one of them by the XML rules (a UTF-8 byte-order mark, a
     * declaration naming it, or neither mark nor declared encoding); null for anything else.
     */
    private static Charset declaredCharset(byte[] content) {
        if (startsWith(content, UTF8_MARK)) {
            return StandardCharsets.UTF_8;
        }
        if (content.length >= 2 && (content[0] == 0 || content[1] == 0
                || ((content[0] & 0xFF) >= 0xFE && (content[1] & 0xFF) >= 0xFE))) {
            // UTF-16 or UTF-32, with or without a byte-order mark.
            return null;
        }
        if (!startsWith(content, DECLARATION)) {
            return StandardCharsets.UTF_8;
        }
        int end = 0;
        while (end < content.length && end < 1024 && content[end] != '>') {
            end++;
        }
        Matcher encoding = ENCODING.matcher(new String(content, 0, end, StandardCharsets.ISO_8859_1));
        if (!encoding.find()) {
            return StandardCharsets.UTF_8;
        }
        String name = encoding.group(1).toUpperCase(Locale.ROOT);
        if (name.equals("UTF-8") || name.equals("UTF8")) {
            return StandardCharsets.UTF_8;
        }
        return name.equals("US-ASCII") || name.equals("ASCII") ? StandardCharsets.US_ASCII : null;
    }

    private static boolean startsWith(byte[] content, byte[] prefix) {
        if (content.length < prefix.length) {
            return false;
        }
        for (int i = 0; i < prefix.length; i++) {
            if (content[i] != prefix[i]) {
                return false;
            }
        }
        return true;
    }

    private static final byte[] UTF8_MARK = {(byte) 0xEF, (byte) 0xBB, (byte) 0xBF};
    private static final byte[] DECLARATION = {'<', '?', 'x', 'm', 'l'};
    private static final Pattern ENCODING = Pattern.compile("\\sencoding\\s*=\\s*[\"']([A-Za-z][A-Za-z0-9._-]*)[\"']");

    private static XmlNode element(XMLStreamReader reader) {
        // getAttributeCount leaves namespace declarations out, so they can never be reported as unread.
        Map<String, String> attributes = new LinkedHashMap<>();
        for (int i = 0; i < reader.getAttributeCount(); i++) {
            attributes.put(
                    XmlNode.qualified(reader.getAttributeNamespace(i), reader.getAttributeLocalName(i)),
                    reader.getAttributeValue(i));
        }
        return new XmlNode(reader.getNamespaceURI(), reader.getLocalName(), attributes);
    }

    private static XMLInputFactory factory() {
        XMLInputFactory factory = XMLInputFactory.newFactory();
        factory.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, Boolean.TRUE);
        factory.setProperty(XMLInputFactory.IS_COALESCING, Boolean.TRUE);
        factory.setProperty(XMLInputFactory.SUPPORT_DTD, Boolean.FALSE);
        factory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, Boolean.FALSE);
        return factory;
    }
}
