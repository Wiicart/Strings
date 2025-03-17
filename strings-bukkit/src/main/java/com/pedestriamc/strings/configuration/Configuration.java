package com.pedestriamc.strings.configuration;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public class Configuration {

    private final FileConfiguration config;

    public Configuration(FileConfiguration config) {
        this.config = config;
    }

    private Object get(@NotNull ConfigurationOption option) {
        return config.get(option.getIdentifier());
    }

    public boolean getBoolean(@NotNull ConfigurationOption option) {
        return config.getBoolean(option.getIdentifier());
    }

    @Nullable
    public String getString(@NotNull ConfigurationOption option) {
        String val = config.getString(option.getIdentifier());
        if(val == null && option.getDefault() instanceof String) {
            return option.getDefault().toString();
        }
        return val;
    }

    @Nullable
    public String colored(@NotNull ConfigurationOption option) {
        String str = getString(option);
        if(str != null) {
            return ChatColor.translateAlternateColorCodes('&', str);
        }
        return null;
    }

    public float getFloat(@NotNull ConfigurationOption option) {
        return (float) config.getDouble(option.getIdentifier());
    }

    /**
     * Sets a config option.
     * Returns false if the wrong type is provided.
     * @param option The ConfigurationOption to be changed
     * @param value The new value
     * @return True if the change is successful, false otherwise
     */
    public boolean set(@NotNull ConfigurationOption option, @NotNull Object value) {
        if(value.getClass().equals(option.getType())) {
            config.set(option.getIdentifier(), value);
            return true;
        }
        return false;
    }

}
