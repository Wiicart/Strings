package com.pedestriamc.strings.api.text;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Map;

public interface EmojiManager {

    /**
     * Replaces all emoji placeholders (like :smile:) with their Unicode equivalent
     * @param input The input
     * @return A String with all emoji placeholders replaced.
     */
    @NotNull
    String applyEmojis(@NotNull String input);

    /**
     * Replaces all emoji placeholders (like :smile:) with their Unicode equivalent
     * @param input The input
     * @return A Component with all emoji placeholders replaced.
     */
    @NotNull
    Component applyEmojis(@NotNull Component input);

    /**
     * Provides the backing Map for emojis
     * @return An unmodifiable Map
     */
    @Unmodifiable
    @NotNull
    Map<String, String> mappings();

}
