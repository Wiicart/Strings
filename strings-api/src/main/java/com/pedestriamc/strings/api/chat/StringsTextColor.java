package com.pedestriamc.strings.api.chat;

import net.kyori.adventure.text.format.TextColor;
import net.md_5.bungee.api.ChatColor;
import org.jetbrains.annotations.NotNull;

import java.awt.Color;
import java.io.Serializable;
import java.util.Objects;

/**
 * Class that combines Bungee and Adventure Colors into one class.
 * Implements Adventure's TextColor for drop-in compatability with Adventure.
 */
@SuppressWarnings("unused")
public final class StringsTextColor implements TextColor, Serializable {

    public static final StringsTextColor BLACK = of(new Color(0x000000));

    public static final StringsTextColor DARK_BLUE = of(ChatColor.DARK_BLUE);

    public static final StringsTextColor DARK_GREEN = of(ChatColor.DARK_GREEN);

    public static final StringsTextColor DARK_AQUA = of(ChatColor.DARK_AQUA);

    public static final StringsTextColor DARK_RED = of(ChatColor.DARK_RED);

    public static final StringsTextColor DARK_PURPLE = of(ChatColor.DARK_PURPLE);

    public static final StringsTextColor GOLD = of(ChatColor.GOLD);

    public static final StringsTextColor GRAY = of(ChatColor.GRAY);

    public static final StringsTextColor DARK_GRAY = of(ChatColor.DARK_GRAY);

    public static final StringsTextColor BLUE = of(ChatColor.BLUE);

    public static final StringsTextColor GREEN = of(ChatColor.GREEN);

    public static final StringsTextColor AQUA = of(ChatColor.AQUA);

    public static final StringsTextColor RED = of(ChatColor.RED);

    public static final StringsTextColor PINK = of(ChatColor.LIGHT_PURPLE);

    public static final StringsTextColor YELLOW = of(ChatColor.YELLOW);

    public static final StringsTextColor WHITE = of(ChatColor.WHITE);

    private final transient net.md_5.bungee.api.ChatColor chatColor;
    private final int value;

    private StringsTextColor(@NotNull net.md_5.bungee.api.ChatColor chatColor) {
        this.chatColor = chatColor;
        value = chatColor.getColor().getRGB();
    }

    private StringsTextColor(@NotNull org.bukkit.ChatColor chatColor) {
        this(chatColor.asBungee());
    }

    /**
     * Converts a Adventure TextColor to ChatColor.
     * @param textColor The TextColor to convert
     * @return A Bungee ChatColor
     */
    public static @NotNull ChatColor asChatColor(@NotNull TextColor textColor) {
        return ChatColor.of(new Color(textColor.red(), textColor.green(), textColor.blue()));
    }

    public static @NotNull StringsTextColor of(@NotNull net.md_5.bungee.api.ChatColor color) {
        return new StringsTextColor(color);
    }

    public static @NotNull StringsTextColor of(@NotNull org.bukkit.ChatColor color) {
        return new StringsTextColor(color);
    }

    public static @NotNull StringsTextColor of(@NotNull Color color) {
        return new StringsTextColor(ChatColor.of(color));
    }

    /**
     * Provides the BungeeAPI ChatColor representation of this Color.
     * @return A ChatColor.
     */
    public @NotNull ChatColor chatColor() {
        return chatColor;
    }

    @Override
    public int value() {
        return value;
    }

    @Override
    public boolean equals(Object object) {
        if(object == null || getClass() != object.getClass()) return false;
        StringsTextColor that = (StringsTextColor) object;
        return value == that.value;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}
