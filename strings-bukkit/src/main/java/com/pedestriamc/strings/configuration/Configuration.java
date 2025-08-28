package com.pedestriamc.strings.configuration;

import com.pedestriamc.strings.api.settings.Settings;
import com.pedestriamc.strings.api.settings.Option;
import com.pedestriamc.strings.api.settings.SettingsRegistry;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

public class Configuration implements Settings {

    private final SettingsRegistry registry;

    public Configuration(@NotNull FileConfiguration config) {
        registry = new SettingsRegistry(builder -> builder
                .putAll(loadValues(config, Option.Bool.class, FileConfiguration::getBoolean))
                .putAll(loadValues(config, Option.Double.class, FileConfiguration::getDouble))
                .putAll(loadValues(config, Option.StringList.class, FileConfiguration::getStringList))
                .putAll(loadValues(config, Option.Text.class, FileConfiguration::getString))
        );
    }

    @NotNull
    private <E extends Enum<E> & Option.CoreKey<V>, V> Map<E, V> loadValues(
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

    @Override
    @NotNull
    public <E extends Enum<E> & Option.CoreKey<V>, V> V get(@NotNull E key) {
        return registry.get(key);
    }

    @NotNull
    public String getColored(@NotNull Option.Text option) {
        return ChatColor.translateAlternateColorCodes('&', get(option));
    }

    public float getFloat(@NotNull Option.Double option) {
        return get(option).floatValue();
    }
}
