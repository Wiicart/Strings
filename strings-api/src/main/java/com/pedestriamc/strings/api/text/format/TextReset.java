package com.pedestriamc.strings.api.text.format;

import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;

public final class TextReset implements Element<String> {

    public static final TextReset RESET = new TextReset();

    private TextReset() {}

    @Override
    public @NotNull String toAdventure() {
        return this.toString();
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
