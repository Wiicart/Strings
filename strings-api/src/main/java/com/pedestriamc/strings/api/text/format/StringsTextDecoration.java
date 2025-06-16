package com.pedestriamc.strings.api.text.format;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.md_5.bungee.api.ChatColor;
import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;


/**
 * Class that combines Bungee and Adventure styles/decorations into one class.
 * Unlike {@link StringsTextColor}, this class cannot be directly used with Adventure.
 * Use {@link #toAdventure()} to get the Adventure TextDecoration.
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
    /**
     * Represents a reset of color and style.
     * Not a true StringsTextDecoration,
     * but stored in this class for lack of a better location, and its similar behavior.
     */
    public static final Element<Component> RESET = StringsTextReset.INSTANCE;

    private static final BidiMap<TextDecoration, ChatColor> mappings;
    static {
        mappings = new DualHashBidiMap<>();
        mappings.put(TextDecoration.BOLD, ChatColor.BOLD);
        mappings.put(TextDecoration.ITALIC, ChatColor.ITALIC);
        mappings.put(TextDecoration.UNDERLINED, ChatColor.UNDERLINE);
        mappings.put(TextDecoration.STRIKETHROUGH, ChatColor.STRIKETHROUGH);
        mappings.put(TextDecoration.OBFUSCATED, ChatColor.MAGIC);
    }

    private static final Map<Character, Element<?>> legacyCodes;
    static {
        legacyCodes = new HashMap<>();
        legacyCodes.put('k', MAGIC);
        legacyCodes.put('l', BOLD);
        legacyCodes.put('m', STRIKETHROUGH);
        legacyCodes.put('o', ITALIC);
        legacyCodes.put('n', UNDERLINE);
        legacyCodes.put('r', RESET);
    }

    public static Element<?> fromChar(char legacyChar) {
        return legacyCodes.get(legacyChar);
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
    private final Component component;

    StringsTextDecoration(TextDecoration decoration, net.md_5.bungee.api.ChatColor chatColor) {
        this.decoration = decoration;
        this.chatColor = chatColor;
        this.component = Component.text().decorate(this.toAdventure()).build();
    }

    /**
     * Provides the Bungee {@link net.md_5.bungee.api.ChatColor} version of this StringsTextDecoration.
     * @return A Bungee ChatColor
     */
    public @NotNull ChatColor chatColor() {
        return chatColor;
    }

    @Override
    public @NotNull String toString() {
        return chatColor.toString();
    }

    @Override
    public @NotNull TextDecoration toAdventure() {
        return decoration;
    }

    @Override
    public @NotNull Type getType() {
        return Type.DECORATION;
    }

    @Override
    public @NotNull Component asComponent() {
        return component;
    }
}
