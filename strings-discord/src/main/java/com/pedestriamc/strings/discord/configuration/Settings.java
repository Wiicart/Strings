package com.pedestriamc.strings.discord.configuration;

import com.pedestriamc.strings.discord.StringsDiscord;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.EnumMap;

public final class Settings {

    private final EnumMap<Option.Text, String> stringMap = new EnumMap<>(Option.Text.class);

    private final EnumMap<Option.Bool, Boolean> booleanMap = new EnumMap<>(Option.Bool.class);

    public Settings(@NotNull StringsDiscord strings) {
        FileConfiguration config = strings.getConfig();

        loadStrings(config);
        loadBooleans(config);
    }

    private void loadStrings(@NotNull FileConfiguration config) {
        for (Option.Text text : Option.Text.values()) {
            String val = config.getString(text.key);
            if (val != null) {
                stringMap.put(text, val);
            } else {
                stringMap.put(text, text.defaultValue);
            }
        }
    }

    private void loadBooleans(@NotNull FileConfiguration config) {
        for (Option.Bool bool : Option.Bool.values()) {
            if (config.contains(bool.key)) {
                booleanMap.put(bool, config.getBoolean(bool.key));
            } else {
                booleanMap.put(bool, bool.defaultValue);
            }
        }
    }

    public @NotNull String getColoredString(@NotNull Option.Text text) {
        return ChatColor.translateAlternateColorCodes('&', getString(text));
    }

    public @NotNull String getString(@NotNull Option.Text text) {
        return stringMap.get(text);
    }

    public boolean getBoolean(@NotNull Option.Bool bool) {
        return booleanMap.get(bool);
    }
}
