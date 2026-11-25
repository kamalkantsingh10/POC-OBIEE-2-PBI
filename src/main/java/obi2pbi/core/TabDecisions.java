package obi2pbi.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

/**
 * Turns a tab's {@code fsd_items.csv}, {@code matches.csv} and {@code decisions.csv} into the starting columns of
 * the tab's Slice and the rows of its {@code tab_fields} table (story 4.1; Architecture AD-11). Only decisions
 * {@code confirmed} contribute: the column is the decision's {@code row_id}, or, when that is empty, the item's one
 * proposed column. No I/O; never writes a decision.
 */
public final class TabDecisions {

    /** What the decisions give. */
    public static final class Result {
        /** The confirmed columns, each once, in decision order. */
        public final List<String> columns = new ArrayList<>();
        /** Rows of {@code tab_fields}, in the order of {@code fsd_items.csv}. */
        public final List<String[]> fields = new ArrayList<>();
        /** Decisions that cannot be used, each as a sentence; when there is one, nothing is cut. */
        public final List<String> problems = new ArrayList<>();
        /** The counts of items per status, as words: "18 confirmed, 1 rejected, 2 unmatched, 0 undecided". */
        public String statusCounts() {
            List<String> parts = new ArrayList<>();
            for (Map.Entry<String, Integer> status : statuses.entrySet()) {
                parts.add(status.getValue() + " " + status.getKey());
            }
            return String.join(", ", parts);
        }

        /** Items per status: confirmed, rejected, unmatched, undecided. */
        public final Map<String, Integer> statuses = new LinkedHashMap<>();
        public int confirmedDecisions;
        public int rejectedDecisions;
    }

    private TabDecisions() {
    }

    /**
     * @param matches null when the tab has no {@code matches.csv}
     * @param known   true for a Row identifier that is a presentation, logical or report column of the Knowledge Base
     */
    public static Result read(KbRows items, KbRows matches, KbRows decisions, Predicate<String> known) {
        Result result = new Result();
        for (String status : List.of("confirmed", "rejected", "unmatched", "undecided")) {
            result.statuses.put(status, 0);
        }
        Map<String, String[]> itemById = new LinkedHashMap<>();
        for (String[] row : items.rows) {
            if (!items.get(row, "kind").equals(MatchSchema.REPLACES)) {
                itemById.putIfAbsent(items.get(row, "item_id"), row);
            }
        }
        Map<String, List<String>> proposed = new HashMap<>();
        Set<String> unmatched = new LinkedHashSet<>();
        if (matches != null) {
            for (String[] row : matches.rows) {
                String id = matches.get(row, "item_id");
                if (matches.get(row, "status").equals("proposed") && !matches.get(row, "row_id").isEmpty()) {
                    proposed.computeIfAbsent(id, k -> new ArrayList<>()).add(matches.get(row, "row_id"));
                } else if (matches.get(row, "status").equals("unmatched")) {
                    unmatched.add(id);
                }
            }
        }
        Map<String, List<String>> confirmedOf = new HashMap<>();
        Map<String, List<String>> notesOf = new HashMap<>();
        Set<String> decided = new LinkedHashSet<>();
        Set<String> columns = new LinkedHashSet<>();
        for (int i = 0; i < decisions.rows.size(); i++) {
            String[] row = decisions.rows.get(i);
            String where = "decisions.csv row " + (i + 1);
            String id = decisions.get(row, "item_id");
            String decision = decisions.get(row, "decision");
            String rowId = decisions.get(row, "row_id");
            if (!itemById.containsKey(id)) {
                result.problems.add(where + ": item '" + id + "' is not an item of fsd_items.csv that needs data.");
                continue;
            }
            decided.add(id);
            List<String> notes = notesOf.computeIfAbsent(id, k -> new ArrayList<>());
            if (!decisions.get(row, "note").isEmpty() && !notes.contains(decisions.get(row, "note"))) {
                notes.add(decisions.get(row, "note"));
            }
            if (decision.equals("rejected")) {
                result.rejectedDecisions++;
                continue;
            }
            if (!decision.equals("confirmed")) {
                result.problems.add(where + ", item " + id + ": decision '" + decision + "' is not confirmed or rejected.");
                continue;
            }
            result.confirmedDecisions++;
            if (rowId.isEmpty()) {
                List<String> proposals = proposed.getOrDefault(id, List.of());
                if (proposals.size() != 1) {
                    result.problems.add(where + ", item " + id + ": row_id is empty and the item has " + proposals.size()
                            + " proposed columns in matches.csv, so the column is not known. Name it in row_id.");
                    continue;
                }
                rowId = proposals.get(0);
            }
            if (!known.test(rowId)) {
                result.problems.add(where + ", item " + id + ": " + rowId + " is not a presentation, logical or report "
                        + "column of the Knowledge Base.");
                continue;
            }
            List<String> confirmed = confirmedOf.computeIfAbsent(id, k -> new ArrayList<>());
            if (!confirmed.contains(rowId)) {
                confirmed.add(rowId);
            }
            columns.add(rowId);
        }
        result.columns.addAll(columns);
        for (Map.Entry<String, String[]> item : itemById.entrySet()) {
            String id = item.getKey();
            String[] row = item.getValue();
            List<String> confirmed = confirmedOf.getOrDefault(id, List.of());
            String status = !confirmed.isEmpty() ? "confirmed" : decided.contains(id) ? "rejected"
                    : unmatched.contains(id) ? "unmatched" : "undecided";
            result.statuses.merge(status, 1, Integer::sum);
            result.fields.add(new String[] {"item::" + id, id, items.get(row, "kind"), items.get(row, "fsd_anchor"),
                    items.get(row, "fsd_text"), status, String.join("\n", confirmed),
                    String.join("\n", notesOf.getOrDefault(id, List.of()))});
        }
        return result;
    }
}
