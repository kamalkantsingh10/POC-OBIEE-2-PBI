package obi2pbi.core;

import java.util.List;

/**
 * An expression in the three forms the repository holds (Architecture AD-5): the template
 * ({@code SUM("%1")}), the template rendered with names, and the identifiers the placeholders stand for.
 * All three are kept exactly as written.
 */
public final class Expression {

    public static final Expression EMPTY = new Expression("", "", List.of());

    public final String text;
    public final String textDesc;
    /** One entry per placeholder, in order. An entry is empty when its reference did not resolve. */
    public final List<String> refs;

    public Expression(String text, String textDesc, List<String> refs) {
        this.text = text;
        this.textDesc = textDesc;
        this.refs = List.copyOf(refs);
    }

    public boolean isEmpty() {
        return Text.isBlank(text) && Text.isBlank(textDesc);
    }
}
