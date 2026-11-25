package obi2pbi.core;

/** A piece of a paragraph's text with one formatting: the text exactly as written, and whether it is bold. */
public final class FsdRun {

    public final String text;
    public final boolean bold;

    public FsdRun(String text, boolean bold) {
        this.text = text;
        this.bold = bold;
    }
}
