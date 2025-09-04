package com.pedestriamc.strings.moderation.configuration;

import com.pedestriamc.strings.api.moderation.ModerationSettings;
import com.pedestriamc.strings.api.moderation.Option;
import com.pedestriamc.strings.api.settings.SettingsRegistry;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

public class Configuration implements ModerationSettings {

    private final SettingsRegistry registry;

    // Requires the Strings core plugin instance, NOT StringsModeration
    public Configuration(@NotNull Plugin strings) {
        File file = new File(strings.getDataFolder(), "moderation.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        registry = new SettingsRegistry(builder ->
                builder
                        .putAll(loadValues(config, Option.Bool.class, FileConfiguration::getBoolean))
                        .putAll(loadValues(config, Option.Text.class, FileConfiguration::getString))
                        .putAll(loadValues(config, Option.Double.class, FileConfiguration::getDouble))
                        .putAll(loadValues(config, Option.Int.class, FileConfiguration::getInt))
                        .putAll(loadValues(config, Option.StringList.class, FileConfiguration::getStringList))
        );
    }

    @NotNull
    private <E extends Enum<E> & Option.ModerationKey<V>, V> Map<E, V> loadValues(
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
    @Override
    public <E extends Enum<E> & Option.ModerationKey<V>, V> V get(@NotNull E key) {
        return registry.get(key);
    }

}
