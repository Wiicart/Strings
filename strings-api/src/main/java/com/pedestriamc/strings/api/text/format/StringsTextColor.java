package com.pedestriamc.strings.api.text.format;

import net.kyori.adventure.text.format.TextColor;
import net.md_5.bungee.api.ChatColor;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.Color;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Class that combines Bungee and Adventure Colors into one class.
 * Implements Adventure's {@link TextColor} for drop-in compatability with Adventure.
 */
@SuppressWarnings("unused")
public final class StringsTextColor implements Element<TextColor>, TextColor, Serializable {
    /**
     * Represents black.
     */
    public static final StringsTextColor BLACK = new StringsTextColor(0x000000);
    /**
     * Represents dark blue.
     */
    public static final StringsTextColor DARK_BLUE = new StringsTextColor(0x0000AA);
    /**
     * Represents dark green.
     */
    public static final StringsTextColor DARK_GREEN = new StringsTextColor(0x00AA00);
    /**
     * Represents dark aqua.
     */
    public static final StringsTextColor DARK_AQUA = new StringsTextColor(0x00AAAA);
    /**
     * Represents dark red.
     */
    public static final StringsTextColor DARK_RED = new StringsTextColor(0xAA0000);
    /**
     * Represents dark purple.
     */
    public static final StringsTextColor DARK_PURPLE = new StringsTextColor(0xAA00AA);
    /**
     * Represents gold.
     */
    public static final StringsTextColor GOLD = new StringsTextColor(0xFFAA00);
    /**
     * Represents gray.
     */
    public static final StringsTextColor GRAY = new StringsTextColor(0xAAAAAA);
    /**
     * Represents dark gray.
     */
    public static final StringsTextColor DARK_GRAY = new StringsTextColor(0x555555);
    /**
     * Represents blue.
     */
    public static final StringsTextColor BLUE = new StringsTextColor(0x5555FF);
    /**
     * Represents green.
     */
    public static final StringsTextColor GREEN = new StringsTextColor(0x55FF55);
    /**
     * Represents aqua.
     */
    public static final StringsTextColor AQUA = new StringsTextColor(0x55FFFF);
    /**
     * Represents red.
     */
    public static final StringsTextColor RED = new StringsTextColor(0xFF5555);
    /**
     * Represents pink.
     */
    public static final StringsTextColor PINK = new StringsTextColor(0xFF55FF);
    /**
     * Represents yellow.
     */
    public static final StringsTextColor YELLOW = new StringsTextColor(0xFFFF55);
    /**
     * Represents white.
     */
    public static final StringsTextColor WHITE = new StringsTextColor(0xFFFFFF);

    private static final Map<Character, StringsTextColor> legacyCodes;
    static {
        legacyCodes = new HashMap<>();
        legacyCodes.put('0', BLACK);
        legacyCodes.put('1', DARK_BLUE);
        legacyCodes.put('2', DARK_GREEN);
        legacyCodes.put('3', DARK_AQUA);
        legacyCodes.put('4', DARK_RED);
        legacyCodes.put('5', DARK_PURPLE);
        legacyCodes.put('6', GOLD);
        legacyCodes.put('7', GRAY);
        legacyCodes.put('8', DARK_GRAY);
        legacyCodes.put('9', BLUE);
        legacyCodes.put('a', GREEN);
        legacyCodes.put('b', AQUA);
        legacyCodes.put('c', RED);
        legacyCodes.put('d', PINK);
        legacyCodes.put('e', YELLOW);
        legacyCodes.put('f', WHITE);
    }


    private final int value;

    /**
     * Constructs a StringsTextColor from a Bungee {@link net.md_5.bungee.api.ChatColor}
     * @param chatColor The Bungee ChatColor
     */
    @ApiStatus.Internal
    private StringsTextColor(@NotNull net.md_5.bungee.api.ChatColor chatColor) {
        value = chatColor.getColor().getRGB();
    }

    /**
     * Constructs a StringsTextColor from a Bukkit {@link org.bukkit.ChatColor}
     * @param chatColor The Bukkit ChatColor
     */
    @ApiStatus.Internal
    private StringsTextColor(@NotNull org.bukkit.ChatColor chatColor) {
        this(chatColor.asBungee());
    }

    /**
     * Constructs a StringsTextColor from an int value.
     * @param value The int value of the Color.
     */
    @ApiStatus.Internal
    private StringsTextColor(int value) {
        this.value = value;
    }

    /**
     * Provides a StringsTextColor representation of a Bungee {@link net.md_5.bungee.api.ChatColor}.
     * @param color The Bungee ChatColor
     * @return The StringsTextColor equivalent.
     */
    public static @NotNull StringsTextColor of(@NotNull net.md_5.bungee.api.ChatColor color) {
        return new StringsTextColor(color);
    }

    /**
     * Provides a StringsTextColor representation of a Bukkit {@link org.bukkit.ChatColor}.
     * @param color The Bukkit ChatColor
     * @return The StringsTextColor equivalent.
     */
    public static @NotNull StringsTextColor of(@NotNull org.bukkit.ChatColor color) {
        return new StringsTextColor(color);
    }

    /**
     * Provides a StringsTextColor representation of a {@link Color}.
     * @param color The Color
     * @return The StringsTextColor equivalent.
     */
    public static @NotNull StringsTextColor of(@NotNull Color color) {
        return new StringsTextColor(ChatColor.of(color));
    }

    /**
     * Provides a StringsTextColor based off Bukkit color code chars.
     * @param legacyChar The char associated with the color code.
     * @return A StringsTextColor.
     */
    public static @Nullable StringsTextColor fromLegacyChar(char legacyChar) {
        return legacyCodes.get(legacyChar);
    }

    /**
     * Converts an Adventure {@link TextColor} to {@link net.md_5.bungee.api.ChatColor}.
     * @param textColor The TextColor to convert
     * @return A Bungee ChatColor
     */
    public static @NotNull ChatColor asChatColor(@NotNull TextColor textColor) {
        return ChatColor.of(new Color(textColor.red(), textColor.green(), textColor.blue()));
    }

    /**
     * Provides the Bungee {@link net.md_5.bungee.api.ChatColor} representation of this Color.
     * @return A ChatColor.
     */
    public @NotNull ChatColor chatColor() {
        return asChatColor(this);
    }

    @Override
    public int value() {
        return value;
    }

    @Override
    public @NotNull String toString() {
        return chatColor().toString();
    }

    @Override
    public @NotNull TextColor toAdventure() {
        return this;
    }

    @Override
    public @NotNull Type getType() {
        return Type.COLOR;
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
