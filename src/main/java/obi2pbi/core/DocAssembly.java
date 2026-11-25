package obi2pbi.core;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

/**
 * Joins the parts of a skill-written document into the document (Architecture AD-17), and finds answers a
 * user typed into a document that the answers file does not hold yet (AD-16). No I/O.
 *
 * <p>A skill writes one small file per section into {@code <document without .md>.parts/}; the document is
 * those files joined in file-name order. Joining is fixed so that the same parts always give the same bytes:
 * line ends become line feeds, empty lines at the start and end of a part are dropped, a part with no other
 * line adds nothing, one empty line stands between two parts, and the text ends with one line feed.
 */
public final class DocAssembly {

    /** The joined document and where each part landed in it. */
    public static final class Joined {
        public final String text;
        /** The parts that added lines, in order: {@code {file name, first line, last line}}, lines from 1. */
        public final List<String[]> parts = new ArrayList<>();
        /** The unit of every line of the text (index 0 is line 1); see {@link #unitOf}. */
        public final List<String> unitOfLine = new ArrayList<>();
        public final int lines;

        Joined(String text, int lines) {
            this.text = text;
            this.lines = lines;
        }
    }

    private DocAssembly() {
    }

    /**
     * @param names the parts' file names, already in the order to join them
     * @param texts the text of each part
     */
    public static Joined join(List<String> names, List<String> texts) {
        StringBuilder out = new StringBuilder();
        List<String[]> parts = new ArrayList<>();
        List<String> unitOfLine = new ArrayList<>();
        int line = 0;
        for (int p = 0; p < names.size(); p++) {
            List<String> lines = lines(texts.get(p));
            int first = 0;
            int last = lines.size() - 1;
            while (first <= last && Text.isBlank(lines.get(first))) {
                first++;
            }
            while (last >= first && Text.isBlank(lines.get(last))) {
                last--;
            }
            if (first > last) {
                continue;
            }
            String unit = unitOf(names.get(p));
            if (line > 0) {
                out.append('\n');
                line++;
                unitOfLine.add(unit);
            }
            int start = line + 1;
            for (int i = first; i <= last; i++) {
                out.append(lines.get(i)).append('\n');
                line++;
                unitOfLine.add(unit);
            }
            parts.add(new String[] {names.get(p), Integer.toString(start), Integer.toString(line)});
        }
        Joined joined = new Joined(out.toString(), line);
        joined.parts.addAll(parts);
        joined.unitOfLine.addAll(unitOfLine);
        return joined;
    }

    /**
     * The unit a part file belongs to: its name without {@code .md} and without the leading number and
     * hyphen. {@code 002-table-01-fact-revenue.md} gives {@code table-01-fact-revenue}; {@code 000-summary.md}
     * gives {@code summary}.
     */
    public static String unitOf(String partFileName) {
        String name = partFileName.endsWith(".md") ? partFileName.substring(0, partFileName.length() - 3) : partFileName;
        int i = 0;
        while (i < name.length() && name.charAt(i) >= '0' && name.charAt(i) <= '9') {
            i++;
        }
        if (i > 0 && i < name.length() && name.charAt(i) == '-') {
            return name.substring(i + 1);
        }
        return name;
    }

    /**
     * The answers in the document that are not in the answers file, as question number to answer: a question under
     * 'Open questions' or 'Answered questions' whose {@code Answer:} has text, where no entry of the answers
     * file for that number contains that text (runs of white space compared as one space).
     *
     * @param document the document's text
     * @param answers  the answers file's text, or null when there is none
     */
    public static Map<String, String> unrecordedAnswers(String document, String answers) {
        Map<String, List<String>> recorded = recordedAnswers(answers);
        Map<String, String> missing = new LinkedHashMap<>();
        for (Map.Entry<String, String> typed : typedAnswers(document).entrySet()) {
            boolean found = false;
            for (String entry : recorded.getOrDefault(typed.getKey(), List.of())) {
                found |= entry.contains(typed.getValue());
            }
            if (!found) {
                missing.put(typed.getKey(), typed.getValue());
            }
        }
        return missing;
    }

    /** Question number to the answer written in the document, for the questions that have one. */
    static Map<String, String> typedAnswers(String document) {
        Map<String, String> typed = new LinkedHashMap<>();
        List<String> lines = lines(document);
        String section = "";
        int i = 0;
        while (i < lines.size()) {
            String line = lines.get(i);
            if (line.startsWith("## ") || line.startsWith("# ")) {
                section = line.substring(line.indexOf(' ') + 1).trim();
            }
            boolean questions = section.equalsIgnoreCase(DocCheck.OPEN_QUESTIONS)
                    || section.equalsIgnoreCase(DocCheck.ANSWERED_QUESTIONS);
            Matcher heading = DocCheck.QUESTION_HEADING.matcher(line);
            i++;
            if (!questions || !heading.matches()) {
                continue;
            }
            StringBuilder answer = null;
            while (i < lines.size() && !lines.get(i).startsWith("### ") && !lines.get(i).startsWith("## ")
                    && !lines.get(i).startsWith("# ")) {
                String text = lines.get(i);
                if (answer != null) {
                    answer.append(' ').append(text);
                } else if (text.startsWith(DocCheck.ANSWER)) {
                    answer = new StringBuilder(text.substring(DocCheck.ANSWER.length()));
                }
                i++;
            }
            String text = answer == null ? "" : oneSpaced(answer.toString());
            if (!text.isEmpty()) {
                typed.merge(heading.group(1), text, (a, b) -> a + " " + b);
            }
        }
        return typed;
    }

    /** Question number to the answers recorded for it, oldest first; a number may be answered more than once. */
    static Map<String, List<String>> recordedAnswers(String answers) {
        Map<String, List<String>> recorded = new LinkedHashMap<>();
        if (answers == null) {
            return recorded;
        }
        String number = null;
        StringBuilder answer = null;
        List<String> lines = lines(answers);
        for (int i = 0; i <= lines.size(); i++) {
            String line = i < lines.size() ? lines.get(i) : "# end";
            if (line.startsWith("## ") || line.startsWith("# ")) {
                if (number != null && answer != null) {
                    recorded.computeIfAbsent(number, n -> new ArrayList<>()).add(oneSpaced(answer.toString()));
                }
                Matcher heading = DocCheck.ANSWER_HEADING.matcher(line);
                number = heading.matches() ? heading.group(1) : null;
                answer = null;
            } else if (answer != null) {
                answer.append(' ').append(line);
            } else if (number != null && line.startsWith(DocCheck.ANSWER)) {
                answer = new StringBuilder(line.substring(DocCheck.ANSWER.length()));
            }
        }
        return recorded;
    }

    private static String oneSpaced(String text) {
        StringBuilder out = new StringBuilder();
        boolean space = false;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (Text.isWhitespace(c)) {
                space = out.length() > 0;
            } else {
                if (space) {
                    out.append(' ');
                }
                space = false;
                out.append(c);
            }
        }
        return out.toString();
    }

    /** The lines of a text whatever its line ends, without a byte-order mark and without a last empty line. */
    private static List<String> lines(String text) {
        String clean = text.startsWith("﻿") ? text.substring(1) : text;
        clean = clean.replace("\r\n", "\n").replace('\r', '\n');
        List<String> lines = new ArrayList<>();
        for (String line : clean.split("\n", -1)) {
            lines.add(line);
        }
        if (!lines.isEmpty() && lines.get(lines.size() - 1).isEmpty()) {
            lines.remove(lines.size() - 1);
        }
        return lines;
    }
}
