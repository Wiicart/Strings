package com.pedestriamc.strings.api.text.format;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.md_5.bungee.api.ChatColor;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a style/color RESET, like Bukkit/Bungee's ChatColor.RESET
 */
@ApiStatus.Internal
final class StringsTextReset implements Element<Component> {

    // The sole instance of this class, for use in StringsTextDecoration
    static final StringsTextReset INSTANCE = new StringsTextReset();

    private static final Component RESET = Component.empty()
            .color(null)
            .decoration(TextDecoration.BOLD, false)
            .decoration(TextDecoration.ITALIC, false)
            .decoration(TextDecoration.UNDERLINED, false)
            .decoration(TextDecoration.STRIKETHROUGH, false)
            .decoration(TextDecoration.OBFUSCATED, false);

    // There should only be one instance
    private StringsTextReset() {}

    @Override
    public @NotNull Component toAdventure() {
        return RESET;
    }

    @Override
    public @NotNull Component asComponent() {
        return RESET;
    }

    @Override
    public @NotNull Type getType() {
        return Type.RESET;
    }

    @Override
    public @NotNull String toString() {
        return ChatColor.RESET.toString();
    }
}
