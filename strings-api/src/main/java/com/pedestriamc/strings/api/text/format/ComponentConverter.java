package com.pedestriamc.strings.api.text.format;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.jetbrains.annotations.NotNull;

/**
 * Converts Bukkit/Bungee {@code ChatColor} and String messages to an Adventure {@code Component}, and vice versa.
 * Uses Adventure's {@link LegacyComponentSerializer}
 */
public final class ComponentConverter {

    private static final LegacyComponentSerializer HEX_SERIALIZER = LegacyComponentSerializer.builder()
            .hexColors()
            .useUnusualXRepeatedCharacterHexFormat()
            .build();

    private static final PlainTextComponentSerializer PLAIN_TEXT_SERIALIZER = PlainTextComponentSerializer.builder().build();

    private ComponentConverter() {}

    /**
     * Converts {@link String} text with legacy color codes to a {@link Component}
     * @param text The text to convert
     * @return The Component representation of the String.
     */
    public static @NotNull Component fromString(@NotNull String text) {
        return HEX_SERIALIZER.deserialize(text);
    }

    /**
     * Converts an Adventure {@link Component} to a {@link String}.
     * @param component The Component to convert.
     * @return String representation of the Component.
     */
    public static @NotNull String toString(@NotNull Component component) {
        return PLAIN_TEXT_SERIALIZER.serialize(component);
    }
}
