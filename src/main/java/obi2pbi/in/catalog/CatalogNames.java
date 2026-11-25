package obi2pbi.in.catalog;

import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;

/**
 * Turns a name as it is on disk into the name the catalog shows.
 *
 * <p>The rule, applied to one path segment: first every {@code +} becomes a space, then every {@code %XX}
 * (two hex digits, either case) becomes a byte, and the bytes are read as UTF-8. The order matters:
 * {@code a%2bb} is {@code a+b}. The rule is general, not a table: {@code co%6dponents} is
 * {@code components}. Letter case is never changed.
 *
 * <p>Nothing here throws. A {@code %} that is not followed by two hex digits is kept as it is, and bytes
 * that are not UTF-8 become U+FFFD, exactly as the reference implementation's decoder does. Either case is
 * flagged, so the caller can report the name.
 */
final class CatalogNames {

    /** One decoded segment, and whether every escape in it could be decoded. */
    static final class Decoded {
        final String text;
        final boolean clean;

        Decoded(String text, boolean clean) {
            this.text = text;
            this.clean = clean;
        }
    }

    private CatalogNames() {
    }

    static Decoded decode(String segment) {
        String spaced = segment.replace('+', ' ');
        if (spaced.indexOf('%') < 0) {
            return new Decoded(spaced, true);
        }
        StringBuilder out = new StringBuilder();
        boolean clean = true;
        int i = 0;
        while (i < spaced.length()) {
            if (spaced.charAt(i) >= 0x80) {
                // Characters outside ASCII are never part of an escape and pass through unchanged.
                out.append(spaced.charAt(i++));
                continue;
            }
            // One run of ASCII: its escapes become bytes and the run is read as UTF-8 as a whole, so an
            // escaped multi-byte character is put together across several escapes.
            int end = i;
            while (end < spaced.length() && spaced.charAt(end) < 0x80) {
                end++;
            }
            byte[] bytes = new byte[end - i];
            int length = 0;
            while (i < end) {
                char c = spaced.charAt(i);
                if (c == '%') {
                    int high = i + 1 < end ? Character.digit(spaced.charAt(i + 1), 16) : -1;
                    int low = i + 2 < end ? Character.digit(spaced.charAt(i + 2), 16) : -1;
                    if (high >= 0 && low >= 0) {
                        bytes[length++] = (byte) (high * 16 + low);
                        i += 3;
                        continue;
                    }
                    clean = false;
                }
                bytes[length++] = (byte) c;
                i++;
            }
            try {
                out.append(StandardCharsets.UTF_8.newDecoder()
                        .onMalformedInput(CodingErrorAction.REPORT)
                        .onUnmappableCharacter(CodingErrorAction.REPORT)
                        .decode(ByteBuffer.wrap(bytes, 0, length)));
            } catch (CharacterCodingException e) {
                clean = false;
                out.append(new String(bytes, 0, length, StandardCharsets.UTF_8));
            }
        }
        return new Decoded(out.toString(), clean);
    }

    /** The last segment of a decoded path. */
    static String nameOf(String decodedPath) {
        return decodedPath.substring(decodedPath.lastIndexOf('/') + 1);
    }

    /** The decoded path without its last segment; empty for something at the top of the catalog. */
    static String folderOf(String decodedPath) {
        int cut = decodedPath.lastIndexOf('/');
        return cut < 0 ? "" : decodedPath.substring(0, cut);
    }
}
