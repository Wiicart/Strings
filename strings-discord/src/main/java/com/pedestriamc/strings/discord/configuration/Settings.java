package com.pedestriamc.strings.discord.configuration;

import com.pedestriamc.strings.discord.StringsDiscord;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.EnumMap;
import java.util.List;

public final class Settings {

    private final StringsDiscord stringsDiscord;

    private final EnumMap<Option.Text, String> stringMap = new EnumMap<>(Option.Text.class);

    private final EnumMap<Option.Bool, Boolean> booleanMap = new EnumMap<>(Option.Bool.class);

    private final EnumMap<Option.StringList, List<String>> lists = new EnumMap<>(Option.StringList.class);

    public Settings(@NotNull StringsDiscord strings) throws IllegalStateException {
        stringsDiscord = strings;
        FileConfiguration config = getConfig();

        loadStrings(config);
        loadBooleans(config);
        loadStringLists(config);
    }

    private @NotNull FileConfiguration getConfig() throws IllegalStateException {
        Plugin plugin = stringsDiscord.getServer().getPluginManager().getPlugin("Strings");
        if (plugin == null) {
            throw new IllegalStateException("Strings plugin not found");
        }

        File configFile = new File(plugin.getDataFolder(), "discord.yml");
        if (configFile.exists()) {
            return YamlConfiguration.loadConfiguration(configFile);
        } else {
            throw new IllegalStateException("Failed to load discord.yml");
        }
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

    private void loadStringLists(@NotNull FileConfiguration config) {
        for (Option.StringList stringList : Option.StringList.values()) {
            if (config.contains(stringList.key)) {
                lists.put(stringList, config.getStringList(stringList.key));
            } else {
                lists.put(stringList, stringList.defaultValue);
            }
        }
    }

    public @NotNull String getColoredString(@NotNull Option.Text text) {
        return ChatColor.translateAlternateColorCodes('&', getString(text));
    }

    public @NotNull String getString(@NotNull Option.Text text) {
        return stringMap.get(text);
    }

    public @NotNull List<String> getStringList(@NotNull Option.StringList list) {
        return lists.get(list);
    }

    public boolean getBoolean(@NotNull Option.Bool bool) {
        return booleanMap.get(bool);
    }
}
