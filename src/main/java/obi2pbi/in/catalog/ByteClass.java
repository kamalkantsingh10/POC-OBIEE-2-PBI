package obi2pbi.in.catalog;

import java.nio.charset.StandardCharsets;

/**
 * Says what a file that is not XML is, from its first bytes. There is no extension to go by: the endings
 * that do occur are inside folder names.
 */
final class ByteClass {

    static final String UNRECOGNISED = "unrecognised binary or text";

    /** Leading bytes and the label they give, tested in this order. */
    private static final Object[][] SIGNATURES = {
            {new byte[] {(byte) 0x89, 'P', 'N', 'G', '\r', '\n'}, "PNG image"},
            {new byte[] {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF}, "JPEG image"},
            {ascii("GIF87a"), "GIF image"},
            {ascii("GIF89a"), "GIF image"},
            {ascii("%PDF-"), "PDF document"},
            {ascii("{\\rtf"), "RTF template"},
            {new byte[] {'P', 'K', 3, 4}, "ZIP container (Office document or archive)"},
            {new byte[] {(byte) 0xD0, (byte) 0xCF, 0x11, (byte) 0xE0}, "OLE compound document (legacy Office)"},
            {ascii("CWS"), "Flash movie"},
            {ascii("FWS"), "Flash movie"},
            {ascii("data:"), "data: URI text"},
    };

    private ByteClass() {
    }

    static String classify(byte[] content) {
        for (Object[] signature : SIGNATURES) {
            if (startsWith(content, (byte[]) signature[0])) {
                return (String) signature[1];
            }
        }
        return UNRECOGNISED;
    }

    private static boolean startsWith(byte[] content, byte[] signature) {
        if (content.length < signature.length) {
            return false;
        }
        for (int i = 0; i < signature.length; i++) {
            if (content[i] != signature[i]) {
                return false;
            }
        }
        return true;
    }

    private static byte[] ascii(String text) {
        return text.getBytes(StandardCharsets.US_ASCII);
    }
}
