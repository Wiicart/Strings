package com.pedestriamc.strings.api.text.format;

import net.kyori.adventure.text.format.TextDecoration;
import net.md_5.bungee.api.ChatColor;
import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


/**
 * Class that combines Bungee and Adventure styles/decorations into one class.
 * Unlike {@link StringsTextColor}, this class cannot be directly used with Adventure.
 * Use {@link #decoration()} to get the Adventure TextDecoration.
 */
@SuppressWarnings("unused")
public enum StringsTextDecoration implements Element<TextDecoration> {
    /**
     * Represents bold.
     */
    BOLD(TextDecoration.BOLD, ChatColor.BOLD),
    /**
     * Represents italic.
     */
    ITALIC(TextDecoration.ITALIC, ChatColor.ITALIC),
    /**
     * Represents underline.
     */
    UNDERLINE(TextDecoration.UNDERLINED, ChatColor.UNDERLINE),
    /**
     * Represents strikethrough.
     */
    STRIKETHROUGH(TextDecoration.STRIKETHROUGH, ChatColor.STRIKETHROUGH),
    /**
     * Represents magic/obfuscated.
     */
    MAGIC(TextDecoration.OBFUSCATED, ChatColor.MAGIC);

    private static final BidiMap<TextDecoration, ChatColor> mappings;
    static {
        mappings = new DualHashBidiMap<>();
        mappings.put(TextDecoration.BOLD, ChatColor.BOLD);
        mappings.put(TextDecoration.ITALIC, ChatColor.ITALIC);
        mappings.put(TextDecoration.UNDERLINED, ChatColor.UNDERLINE);
        mappings.put(TextDecoration.STRIKETHROUGH, ChatColor.STRIKETHROUGH);
        mappings.put(TextDecoration.OBFUSCATED, ChatColor.MAGIC);
    }

    /**
     * Provides the {@link TextDecoration} equivalent to a Bungee {@link net.md_5.bungee.api.ChatColor}.
     * @param color The Bungee ChatColor.
     * @return The TextDecoration equivalent, if it exists.
     */
    private static @Nullable TextDecoration to(@NotNull ChatColor color) {
        return mappings.getKey(color);
    }

    /**
     * Provides the Bungee {@link net.md_5.bungee.api.ChatColor} equivalent to a {@link TextDecoration}.
     * @param decoration The TextDecoration.
     * @return The Bungee ChatColor equivalent, if it exists.
     */
    private static @Nullable ChatColor to(@NotNull TextDecoration decoration) {
        return mappings.get(decoration);
    }

    /**
     * Provides the {@link TextDecoration} equivalent to a Bungee {@link org.bukkit.ChatColor}.
     * @param color The Bukkit ChatColor.
     * @return The TextDecoration equivalent, if it exists.
     */
    private static @Nullable TextDecoration to(@NotNull org.bukkit.ChatColor color) {
        return mappings.getKey(color.asBungee());
    }

    private final TextDecoration decoration;
    private final net.md_5.bungee.api.ChatColor chatColor;

    StringsTextDecoration(TextDecoration decoration, net.md_5.bungee.api.ChatColor chatColor) {
        this.decoration = decoration;
        this.chatColor = chatColor;
    }

    /**
     * Provides the Bungee {@link net.md_5.bungee.api.ChatColor} version of this StringsTextDecoration.
     * @return A Bungee ChatColor
     */
    public @NotNull ChatColor chatColor() {
        return chatColor;
    }

    /**
     * Provides the {@link TextDecoration} version of this StringsTextDecoration.
     * @return A TextDecoration
     */
    public @NotNull TextDecoration decoration() {
        return decoration;
    }

    @Override
    public @NotNull String toString() {
        return chatColor.toString();
    }

    @Override
    public @NotNull TextDecoration toAdventure() {
        return decoration();
    }

    @Override
    public @NotNull Type getType() {
        return Type.DECORATION;
    }
}
