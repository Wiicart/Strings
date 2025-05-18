package com.pedestriamc.strings.api.text.format;

import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.ChatColor;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

@SuppressWarnings("unused")
public final class Combination {

    private static final Pattern HEX = Pattern.compile("\"§x(§[0-9a-fA-F]){6}\"");
    private static final char COLOR_CHAR = ChatColor.COLOR_CHAR;

    Set<StringsFormat> set = new HashSet<>();

    public Combination(StringsFormat @NotNull... formats) {
        set.addAll(Arrays.asList(formats));
    }

    public Component apply(String original) {
        Component component = Component.text(original);
        for(StringsFormat format : set) {
            if(format instanceof StringsTextColor color) {
                component = component.color(color);
            }
            if(format instanceof StringsTextDecoration decoration) {
                component = component.decorate(decoration.decoration());
            }
        }
        return component;
    }

    public static Component apply(Set<StringsFormat> formats, String codes) {
        return null;
    }

    public static Combination fromString(String original) {
        return null;
    }

    @Override
    public @NotNull String toString() {
        StringBuilder builder = new StringBuilder();
        for(StringsFormat format : set) {
            if(format instanceof StringsTextColor color) {
                builder.append(color.chatColor());
            }
            if(format instanceof StringsTextDecoration decoration) {
                builder.append(decoration.decoration());
            }
        }
        return builder.toString();
    }

}
