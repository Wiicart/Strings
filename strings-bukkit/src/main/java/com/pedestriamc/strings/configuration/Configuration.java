package com.pedestriamc.strings.configuration;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.settings.Settings;
import com.pedestriamc.strings.api.settings.Option;
import com.pedestriamc.strings.api.settings.SettingsRegistry;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

public class Configuration implements Settings {

    private final SettingsRegistry registry;

    public Configuration(@NotNull Strings strings) {
        FileConfiguration config = strings.files().getConfig();
        registry = new SettingsRegistry(builder -> builder
                .putAll(loadValues(config, Option.Bool.class, FileConfiguration::getBoolean))
                .putAll(loadValues(config, Option.Double.class, FileConfiguration::getDouble))
                .putAll(loadValues(config, Option.StringList.class, FileConfiguration::getStringList))
                .putAll(loadValues(config, Option.Text.class, FileConfiguration::getString))
                .putAll(loadDeathMessageOptions(strings)) // stored in a death-messages.yml
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

    @NotNull
    private Map<Option.Bool, Boolean> loadDeathMessageOptions(@NotNull Strings strings) {
        FileConfiguration config = strings.files().getDeathMessagesFileConfig();
        Map<Option.Bool, Boolean> map = new EnumMap<>(Option.Bool.class);

        map.put(
                Option.Bool.DEATH_MESSAGES_ENABLE,
                config.getBoolean("enable", Option.Bool.DEATH_MESSAGES_ENABLE.defaultValue())
        );

        map.put(
                Option.Bool.DEATH_MESSAGES_USE_CUSTOM,
                config.getBoolean("custom", Option.Bool.DEATH_MESSAGES_USE_CUSTOM.defaultValue())
        );

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
