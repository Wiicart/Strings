package com.pedestriamc.strings.api.text.format;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

/**
 * Immutable {@link String} wrapper that implements {@link Element} for use in a {@link Combination}.
 */
@SuppressWarnings("unused")
public final class TextSegment implements Element<String> {

    private final String text;

    /**
     * Creates a new TextSegment from a String
     * @param text The String value of this TextSegment
     */
    public TextSegment(String text) {
        this.text = text;
    }

    @Contract("_ -> new")
    public @NotNull TextSegment concat(@NotNull TextSegment segment) {
        return new TextSegment(text.concat(segment.text));
    }

    @Contract(" -> new")
    public @NotNull TextSegment toLowerCase() {
        return new TextSegment(text.toLowerCase());
    }

    @Contract("_ -> new")
    public @NotNull TextSegment toLowerCase(Locale locale) {
        return new TextSegment(text.toLowerCase(locale));
    }

    @Contract(" -> new")
    public @NotNull TextSegment toUpperCase() {
        return new TextSegment(text.toUpperCase());
    }

    @Contract("_ -> new")
    public @NotNull TextSegment toUpperCase(Locale locale) {
        return new TextSegment(text.toUpperCase(locale));
    }

    @Contract(" -> new")
    public @NotNull TextSegment trim() {
        return new TextSegment(text.trim());
    }

    @Contract(" -> new")
    public @NotNull TextSegment strip() {
        return new TextSegment(text.strip());
    }

    @Override
    public boolean equals(Object object) {
        if(object instanceof TextSegment textSegment) {
            return text.equals(textSegment.text);
        }
        if(object instanceof String) {
            return text.equals(object);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return text.hashCode();
    }

    @Override
    public @NotNull String toString() {
        return text;
    }

    @Override
    public @NotNull String toAdventure() {
        return toString();
    }

    @Override
    public @NotNull Type getType() {
        return Type.TEXT;
    }
}
