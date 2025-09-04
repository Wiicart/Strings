package com.pedestriamc.strings.discord.configuration;

import com.pedestriamc.strings.api.discord.DiscordSettings;
import com.pedestriamc.strings.api.discord.Option;
import com.pedestriamc.strings.api.settings.SettingsRegistry;
import com.pedestriamc.strings.discord.StringsDiscord;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

public class Configuration implements DiscordSettings {

    private final SettingsRegistry registry;

    public Configuration(@NotNull StringsDiscord strings) {
        FileConfiguration config = getConfig(strings);
        registry = new SettingsRegistry(builder ->
                builder
                        .putAll(loadValues(config, Option.Bool.class, FileConfiguration::getBoolean))
                        .putAll(loadValues(config, Option.Text.class, FileConfiguration::getString))
                        .putAll(loadValues(config, Option.StringList.class, FileConfiguration::getStringList))
        );
    }

    @NotNull
    private <E extends Enum<E> & Option.DiscordKey<V>, V> Map<E, V> loadValues(
            @NotNull FileConfiguration config,
            @NotNull Class<E> clazz,
            @NotNull BiFunction<FileConfiguration, String, V> method
    ) {
        Map<E, V> map = new HashMap<>();
        for (E key : clazz.getEnumConstants()) {
            V val = key.defaultValue();
            if (config.contains(key.key())) {
                val = method.apply(config, key.key());
            }
            map.put(key, val);
        }

        return map;
    }

    @NotNull
    private FileConfiguration getConfig(@NotNull StringsDiscord strings) throws IllegalStateException {
        Plugin plugin = strings.getServer().getPluginManager().getPlugin("Strings");
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

    @Override
    @NotNull
    public <E extends Enum<E> & Option.DiscordKey<V>, V> V get(@NotNull E key) {
        return registry.get(key);
    }

    @NotNull
    public String getColored(@NotNull Option.Text key) {
        return ChatColor.translateAlternateColorCodes('&', get(key));
    }

}
