package com.pedestriamc.strings.chat;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.md_5.bungee.api.ChatColor;
import org.jetbrains.annotations.NotNull;

import java.awt.Color;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class MessageUtilities {

    private static final Pattern HEX_PATTERN = Pattern.compile("&#[0-9A-Fa-f]{6}");

    private MessageUtilities() {

    }

    @NotNull
    public static String translateColorCodes(@NotNull String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }

    // Translates any HEX color codes (formatted &#<HEX> to ChatColor).
    @NotNull
    public static String colorHex(@NotNull String string) {
        Matcher matcher = HEX_PATTERN.matcher(string);
        StringBuilder sb = new StringBuilder();

        while(matcher.find()) {
            try {
                String stringHex = matcher.group();
                ChatColor colorCode;

                Color color = Color.decode(stringHex.substring(1));
                colorCode = ChatColor.of(color);

                matcher.appendReplacement(sb, Matcher.quoteReplacement(colorCode.toString()));
            } catch(NumberFormatException e) {
                matcher.appendReplacement(sb,  Matcher.quoteReplacement(matcher.group()));
            }
        }

        matcher.appendTail(sb);
        return sb.toString();
    }

    @NotNull
    public static Component setPlaceholder(@NotNull Component target, final @NotNull String original, final @NotNull String replacement) {
        return target.replaceText(TextReplacementConfig.builder()
                .matchLiteral(original)
                .replacement(replacement)
                .build());
    }

    @NotNull
    public static Component setPlaceholder(@NotNull Component target, final @NotNull String original, final @NotNull Component replacement) {
        return target.replaceText(TextReplacementConfig.builder()
                .matchLiteral(original)
                .replacement(replacement)
                .build());
    }

}
