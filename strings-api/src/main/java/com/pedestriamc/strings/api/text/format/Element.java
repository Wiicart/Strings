package com.pedestriamc.strings.api.text.format;

import net.kyori.adventure.text.ComponentLike;
import net.md_5.bungee.api.ChatColor;
import org.jetbrains.annotations.NotNull;

/**
 * An interface for {@link StringsTextDecoration} and {@link StringsTextColor}.
 * that aim to combine Adventure API elements with {@link ChatColor} elements.
 * All implementations are immutable.
 * @param <T> The Adventure API equivalent
 */
public sealed interface Element<T> extends ComponentLike permits StringsTextColor, StringsTextComponent, StringsTextDecoration, StringsTextReset
{
    /**
     * Should return the same as {@link ChatColor#toString()} if representing a ChatColor,
     * or a String value of the Element's text.
     * @return The String representation of the Element.
     */
    @Override
    @NotNull String toString();

    /**
     * Provides the Adventure API equivalent for {@link StringsTextColor} and {@link StringsTextDecoration}.
     * @return The adventure equivalent.
     */
    @NotNull T toAdventure();

    /**
     * Provides what type of Element this is.
     * @return The Type.
     */
    @NotNull Type getType();

    enum Type {
        COLOR,
        DECORATION,
        TEXT,
        RESET,
    }
}
