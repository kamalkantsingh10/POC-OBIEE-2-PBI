package obi2pbi.core;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Checks a skill-written document mechanically (Architecture AD-12, AD-16, AD-18). No I/O, no AI, and no
 * judgement of whether a statement is true: only coverage, citations, the shape of questions, and the status.
 *
 * <p>The rows a document must cover are a parameter. For an as-is document they are a Slice's
 * {@code required_rows}; a to-be design passes a different list to the same check.
 *
 * <p>When the packets of the Slice are given, each part of the document that is a unit of the Slice is also
 * compared with its packet: a citation must be stated in a packet, and a text between backticks or fence
 * lines must occur in a packet character for character.
 *
 * <p>The text conventions relied on are few and are written down in {@code docs/skill-conventions.md}:
 * the status on the first line; citations {@code [kb:<row identifier>]} and {@code [answer:Q<n>]}; the
 * sections {@code ## Open questions}, {@code ## Answered questions} and {@code ## Not described}; a question
 * heading {@code ### Q<n>. <question>} followed by lines starting {@code Why it matters:},
 * {@code How to get the answer:} and {@code Answer:}.
 */
public final class DocCheck {

    public static final String STATUS_DRAFT = "Status: draft, not reviewed";
    public static final String STATUS_REVIEWED = "Status: reviewed ";
    public static final String OPEN_QUESTIONS = "Open questions";
    public static final String ANSWERED_QUESTIONS = "Answered questions";
    public static final String NOT_DESCRIBED = "Not described";

    private static final Pattern REVIEWED = Pattern.compile("Status: reviewed \\d{4}-\\d{2}-\\d{2}(\\s.*)?");
    private static final Pattern ANSWER_CITATION = Pattern.compile("\\[answer:([^\\]]*)\\]");
    private static final Pattern QUESTION_NUMBER = Pattern.compile("Q(\\d+)");
    static final Pattern QUESTION_HEADING = Pattern.compile("### Q(\\d+)\\. \\S.*");
    static final Pattern ANSWER_HEADING = Pattern.compile("## Q(\\d+)(\\W.*)?");
    private static final String WHY = "Why it matters:";
    private static final String HOW = "How to get the answer:";
    static final String ANSWER = "Answer:";

    private DocCheck() {
    }

    /**
     * @param document        the document's text
     * @param answers         the text of the answers file beside it, or null when there is none
     * @param requiredRows    the rows the document must cover, as {@code {row_id, table}} or
     *                        {@code {row_id, table, unit_id}}
     * @param knownRows       every Row identifier a citation may name, with the table it is in
     * @param reviewLogExists whether the review log is beside the document
     */
    public static DocCheckResult check(String document, String answers, List<String[]> requiredRows,
            Map<String, String> knownRows, boolean reviewLogExists) {
        return check(document, answers, requiredRows, knownRows, reviewLogExists, null);
    }

    /**
     * The same, naming the unit of each finding that is at a line.
     *
     * @param unitOfLine the unit each line of the document belongs to (index 0 is line 1), or null
     */
    public static DocCheckResult check(String document, String answers, List<String[]> requiredRows,
            Map<String, String> knownRows, boolean reviewLogExists, List<String> unitOfLine) {
        return check(document, answers, requiredRows, knownRows, reviewLogExists, unitOfLine, null);
    }

    /**
     * The same, comparing each part that is a unit of the Slice with its packet.
     *
     * @param packets the text of every packet of the Slice by unit id, in unit order, or null. With packets, a
     *                citation anywhere in the document must be stated in a packet ({@code unknown_citation}
     *                otherwise); in a part that is a unit, a citation its own packet does not state is the
     *                warning {@code citation_from_other_unit}, and a text between backticks or fence lines
     *                that no packet holds is {@code quote_not_in_packet}
     */
    public static DocCheckResult check(String document, String answers, List<String[]> requiredRows,
            Map<String, String> knownRows, boolean reviewLogExists, List<String> unitOfLine, Map<String, String> packets) {
        DocCheckResult result = new DocCheckResult();
        result.unitOfLine = unitOfLine;
        PacketTexts texts = packets == null ? null : new PacketTexts(packets);
        result.packetsCompared = texts != null && unitOfLine != null;
        String[] lines = document.split("\n", -1);
        for (int i = 0; i < lines.length; i++) {
            if (lines[i].endsWith("\r")) {
                lines[i] = lines[i].substring(0, lines[i].length() - 1);
            }
        }
        Set<String> answered = answersIn(answers);

        // Citations, and the entries of the 'Not described' section.
        Set<String> cited = new LinkedHashSet<>();
        Set<String> described = new LinkedHashSet<>();
        String section = "";
        boolean hasOpenQuestions = false;
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            if (line.startsWith("## ") || line.startsWith("# ")) {
                section = line.substring(line.indexOf(' ') + 1).trim();
                hasOpenQuestions |= section.equalsIgnoreCase(OPEN_QUESTIONS);
            }
            boolean notDescribed = section.equalsIgnoreCase(NOT_DESCRIBED);
            List<Citation> citations = Citation.find(line, i + 1);
            for (int c = 0; c < citations.size(); c++) {
                Citation citation = citations.get(c);
                result.citationsChecked++;
                if (citation.rowId == null) {
                    result.add("unknown_citation", "", "", i + 1, "A citation starting at column " + citation.column
                            + " has no closing ']' on its line.");
                } else if (!knownRows.containsKey(citation.rowId)) {
                    result.add("unknown_citation", citation.rowId, "", i + 1,
                            "The citation names a row that is not in the Slice.");
                } else if (texts != null && !texts.unitsStating.containsKey(citation.rowId)) {
                    result.add("unknown_citation", citation.rowId, knownRows.get(citation.rowId), i + 1,
                            "The citation names a row that is in the Slice's tables and that no packet states, so "
                                    + "it was not copied from a packet.");
                } else {
                    String unit = unitOfLine != null && i < unitOfLine.size() ? unitOfLine.get(i) : "";
                    if (texts != null && packets.containsKey(unit) && !texts.unitsStating.get(citation.rowId).contains(unit)) {
                        result.add("citation_from_other_unit", citation.rowId, knownRows.get(citation.rowId), i + 1,
                                "Warning: this part cites a row its own packet (" + unit + ") does not state. It is "
                                        + "stated in the packet of: " + String.join(", ", texts.unitsStating.get(citation.rowId))
                                        + ". Allowed when the part really speaks about another unit's row.");
                    }
                    cited.add(citation.rowId);
                    if (!notDescribed) {
                        described.add(citation.rowId);
                    } else if (c == citations.size() - 1 && reason(citation.rest).isEmpty()) {
                        result.add("not_described_without_reason", citation.rowId, knownRows.get(citation.rowId), i + 1,
                                "The 'Not described' entry gives no reason after the citation.");
                    }
                }
            }
            Matcher answer = ANSWER_CITATION.matcher(line);
            while (answer.find()) {
                result.answerCitationsChecked++;
                Matcher number = QUESTION_NUMBER.matcher(answer.group(1));
                if (!number.matches()) {
                    result.add("unknown_answer", "", "", i + 1, "'" + answer.group() + "' is not of the form [answer:Q<n>].");
                } else if (!answered.contains(number.group(1))) {
                    result.add("unknown_answer", "", "", i + 1, "'" + answer.group() + "' is cited and the answers "
                            + "file has no answer for Q" + number.group(1) + ".");
                }
            }
        }

        if (result.packetsCompared) {
            quotes(lines, unitOfLine, packets, texts, result);
        }
        questions(lines, answered, result);
        if (!hasOpenQuestions) {
            result.add("malformed_question", "", "", 0, "The document has no '## " + OPEN_QUESTIONS + "' section.");
        }

        // A row is a Row identifier in a table. Two kinds of object can have the same identifier text (a
        // dimension and a logical table of one name); a citation of that text covers both rows.
        Map<String, String[]> required = new LinkedHashMap<>();
        for (String[] row : requiredRows) {
            required.putIfAbsent(row[0] + "\u0000" + row[1], row);
        }
        result.rowsRequired = required.size();
        for (String[] row : required.values()) {
            if (cited.contains(row[0])) {
                result.rowsCovered++;
                if (!described.contains(row[0])) {
                    result.rowsNotDescribed++;
                }
            } else {
                result.add("not_covered", row[0], row[1], 0, row.length > 2 ? row[2] : "", "The document neither cites this row nor "
                        + "lists it in its 'Not described' section.");
            }
        }

        // The status line (AD-18), last, because a reviewed document must have no other finding.
        String first = lines.length > 0 ? lines[0] : "";
        if (first.equals(STATUS_DRAFT)) {
            result.status = "draft";
        } else if (REVIEWED.matcher(first).matches()) {
            result.status = "reviewed";
            if (result.failing() > 0) {
                result.add("reviewed_with_findings", "", "", 1, "The document is marked reviewed and this check has "
                        + result.failing() + " finding(s).");
            }
            if (!reviewLogExists) {
                result.add("reviewed_without_review_log", "", "", 1, "The document is marked reviewed and there is "
                        + "no review log beside it.");
            }
        } else {
            result.add("bad_status", "", "", 1, "The first line must be '" + STATUS_DRAFT + "' or '" + STATUS_REVIEWED
                    + "<yyyy-mm-dd>'.");
        }
        return result;
    }

    /** The packets as the comparison needs them: who states which row, and the text a quote must be found in. */
    private static final class PacketTexts {
        /** Row identifier to the units whose packet cites it, in unit order. */
        final Map<String, List<String>> unitsStating = new LinkedHashMap<>();
        /** Every packet's text, line ends as line feeds. */
        final List<String> texts = new ArrayList<>();
        /**
         * All packets one after the other with every value that was cut over two parts put together again:
         * the closing fence, the two notes, the next part's header and the new opening fence taken out.
         */
        final String rejoined;

        PacketTexts(Map<String, String> packets) {
            StringBuilder all = new StringBuilder();
            for (Map.Entry<String, String> packet : packets.entrySet()) {
                String text = packet.getValue().replace("\r\n", "\n").replace("\r", "\n");
                texts.add(text);
                all.append(text);
                String[] lines = text.split("\n", -1);
                for (int i = 0; i < lines.length; i++) {
                    for (Citation citation : Citation.find(lines[i], i + 1)) {
                        if (citation.rowId != null) {
                            List<String> units = unitsStating.computeIfAbsent(citation.rowId, k -> new ArrayList<>());
                            if (!units.contains(packet.getKey())) {
                                units.add(packet.getKey());
                            }
                        }
                    }
                }
            }
            rejoined = CUT_VALUE.matcher(all).replaceAll("\n");
        }

        boolean holds(String quote) {
            for (String text : texts) {
                if (text.contains(quote)) {
                    return true;
                }
            }
            return rejoined.contains(quote);
        }
    }

    /** What stands between the two halves of a value that a packet boundary cut: see {@code Packets.pieces}. */
    private static final Pattern CUT_VALUE = Pattern.compile(
            "\n(`{3,})\n\\(the value continues in the next part\\)\n[\\s\\S]*?\n\\(the value continues from the previous part\\)\n\\1\n");

    /**
     * Compares what a part quotes with the packets. A quote is (a) the text between two runs of backticks of
     * the same length on one line, and (b) the lines between a fence line (three or more backticks or
     * tildes, after any indentation) and the next fence line of the same kind and at least the same length.
     * White space at the two ends of a quote is ignored, and the indentation of an indented fence is taken
     * off its lines. Only parts that are units of the Slice are compared.
     */
    private static void quotes(String[] lines, List<String> unitOfLine, Map<String, String> packets, PacketTexts texts,
            DocCheckResult result) {
        char fenceChar = 0;
        int fenceLength = 0;
        int fenceIndent = 0;
        int fenceLine = 0;
        String fenceUnit = "";
        List<String> fenced = new ArrayList<>();
        for (int i = 0; i <= lines.length; i++) {
            String unit = i < lines.length && i < unitOfLine.size() ? unitOfLine.get(i) : "";
            boolean end = i == lines.length || (fenceChar != 0 && !unit.equals(fenceUnit));
            String line = i < lines.length ? lines[i] : "";
            int indent = 0;
            while (indent < line.length() && line.charAt(indent) == ' ') {
                indent++;
            }
            int run = 0;
            char first = indent < line.length() ? line.charAt(indent) : 0;
            if (first == '`' || first == '~') {
                while (indent + run < line.length() && line.charAt(indent + run) == first) {
                    run++;
                }
            }
            String rest = line.substring(Math.min(line.length(), indent + run));
            if (fenceChar != 0) {
                boolean closes = !end && first == fenceChar && run >= fenceLength && Text.isBlank(rest);
                if (!closes && !end) {
                    int strip = 0;
                    while (strip < fenceIndent && strip < line.length() && line.charAt(strip) == ' ') {
                        strip++;
                    }
                    fenced.add(line.substring(strip));
                    continue;
                }
                quote(String.join("\n", fenced), fenceLine, fenceUnit, "between fence lines", packets, texts, result);
                fenced.clear();
                fenceChar = 0;
                if (closes) {
                    continue;
                }
            }
            if (i == lines.length) {
                break;
            }
            if (run >= 3 && (first == '~' || rest.indexOf('`') < 0)) {
                fenceChar = first;
                fenceLength = run;
                fenceIndent = indent;
                fenceLine = i + 1;
                fenceUnit = unit;
                continue;
            }
            // Text between backticks on this line.
            int at = line.indexOf('`');
            while (at >= 0) {
                int open = at;
                while (at < line.length() && line.charAt(at) == '`') {
                    at++;
                }
                int length = at - open;
                int close = -1;
                int search = at;
                while (search < line.length() && close < 0) {
                    int next = line.indexOf('`', search);
                    if (next < 0) {
                        break;
                    }
                    int stop = next;
                    while (stop < line.length() && line.charAt(stop) == '`') {
                        stop++;
                    }
                    if (stop - next == length) {
                        close = next;
                    }
                    search = stop;
                }
                if (close < 0) {
                    // No closing run of the same length: these backticks are ordinary characters.
                    at = line.indexOf('`', at);
                    continue;
                }
                quote(line.substring(at, close), i + 1, unit, "between backticks", packets, texts, result);
                at = line.indexOf('`', close + length);
            }
        }
    }

    private static void quote(String text, int line, String unit, String where, Map<String, String> packets,
            PacketTexts texts, DocCheckResult result) {
        String quote = Text.strip(text);
        if (quote.isEmpty() || !packets.containsKey(unit)) {
            return;
        }
        result.quotesChecked++;
        if (!texts.holds(quote)) {
            String shown = quote.replace("\n", "\\n");
            result.add("quote_not_in_packet", "", "", line, "The text " + where + " is not in the packet of this unit, "
                    + "nor in any other packet of the Slice, character for character: "
                    + (shown.length() > 200 ? shown.substring(0, 200) + " ... (" + shown.length() + " characters)" : shown));
        }
    }

    private static void questions(String[] lines, Set<String> answered, DocCheckResult result) {
        Set<String> seen = new LinkedHashSet<>();
        String section = "";
        int i = 0;
        while (i < lines.length) {
            String line = lines[i];
            if (line.startsWith("## ") || line.startsWith("# ")) {
                section = line.substring(line.indexOf(' ') + 1).trim();
            }
            boolean open = section.equalsIgnoreCase(OPEN_QUESTIONS);
            boolean closed = section.equalsIgnoreCase(ANSWERED_QUESTIONS);
            if (!(open || closed) || !line.startsWith("### ")) {
                i++;
                continue;
            }
            int start = i;
            i++;
            List<String> body = new ArrayList<>();
            while (i < lines.length && !lines[i].startsWith("### ") && !lines[i].startsWith("## ")
                    && !lines[i].startsWith("# ")) {
                body.add(lines[i]);
                i++;
            }
            Matcher heading = QUESTION_HEADING.matcher(line);
            if (!heading.matches()) {
                result.add("malformed_question", "", "", start + 1, "A heading in this section must read "
                        + "'### Q<n>. <question>'.");
                continue;
            }
            String number = heading.group(1);
            if (open) {
                result.openQuestions++;
            } else {
                result.answeredQuestions++;
            }
            List<String> missing = new ArrayList<>();
            if (!seen.add(number)) {
                missing.add("the number Q" + number + " is used twice");
            }
            String answer = null;
            boolean why = false;
            boolean how = false;
            for (int b = 0; b < body.size(); b++) {
                String text = body.get(b);
                why |= text.startsWith(WHY);
                how |= text.startsWith(HOW);
                if (answer == null && text.startsWith(ANSWER)) {
                    StringBuilder rest = new StringBuilder(text.substring(ANSWER.length()));
                    for (int r = b + 1; r < body.size(); r++) {
                        rest.append(' ').append(body.get(r));
                    }
                    answer = Text.strip(rest.toString());
                }
            }
            if (!why) {
                missing.add("no line starting '" + WHY + "'");
            }
            if (!how) {
                missing.add("no line starting '" + HOW + "'");
            }
            if (answer == null) {
                missing.add("no line starting '" + ANSWER + "'");
            } else if (closed && answer.isEmpty()) {
                missing.add("it is under '" + ANSWERED_QUESTIONS + "' and its '" + ANSWER + "' line is empty");
            }
            if (!missing.isEmpty()) {
                result.add("malformed_question", "", "", start + 1, "Q" + number + ": " + String.join("; ", missing) + ".");
            }
            if (closed && !answered.contains(number)) {
                result.add("unknown_answer", "", "", start + 1, "Q" + number + " is under '" + ANSWERED_QUESTIONS
                        + "' and the answers file has no answer for it.");
            }
        }
    }

    /**
     * The question numbers an answers file answers: a heading {@code ## Q<n>} with, before the next
     * {@code ## } heading, a line starting {@code Answer:} that has text after it or on the lines below it.
     */
    public static Set<String> answersIn(String answers) {
        Set<String> found = new LinkedHashSet<>();
        if (answers == null) {
            return found;
        }
        String number = null;
        boolean inAnswer = false;
        for (String raw : answers.split("\n", -1)) {
            String line = raw.endsWith("\r") ? raw.substring(0, raw.length() - 1) : raw;
            if (line.startsWith("## ") || line.startsWith("# ")) {
                Matcher heading = ANSWER_HEADING.matcher(line);
                number = heading.matches() ? heading.group(1) : null;
                inAnswer = false;
                continue;
            }
            if (number == null) {
                continue;
            }
            String text = line;
            if (!inAnswer && line.startsWith(ANSWER)) {
                inAnswer = true;
                text = line.substring(ANSWER.length());
            }
            if (inAnswer && !Text.isBlank(text)) {
                found.add(number);
            }
        }
        return found;
    }

    /** The reason of a 'Not described' entry: what follows the citation, without the separator before it. */
    private static String reason(String rest) {
        int i = 0;
        while (i < rest.length() && (Text.isWhitespace(rest.charAt(i)) || "-–—:.,;|".indexOf(rest.charAt(i)) >= 0)) {
            i++;
        }
        return Text.strip(rest.substring(i)).toLowerCase(Locale.ROOT).isEmpty() ? "" : rest.substring(i);
    }
}
