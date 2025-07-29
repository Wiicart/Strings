package com.pedestriamc.strings.configuration;

import com.pedestriamc.strings.api.settings.Settings;
import com.pedestriamc.strings.api.settings.Option;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

public class Configuration implements Settings {

    private final EnumMap<Option.Text, String> stringMap = new EnumMap<>(Option.Text.class);

    private final EnumMap<Option.Bool, Boolean> booleans = new EnumMap<>(Option.Bool.class);

    private final EnumMap<Option.Double, Double> doubles = new EnumMap<>(Option.Double.class);

    private final EnumMap<Option.StringList, List<String>> lists = new EnumMap<>(Option.StringList.class);

    public Configuration(@NotNull FileConfiguration config) {
        loadStrings(config);
        loadDoubles(config);
        loadBooleans(config);
        loadStringLists(config);
    }

    private void loadStrings(@NotNull FileConfiguration config) {
        for (Option.Text option : Option.Text.values()) {
            String val = config.getString(option.getKey());
            if (val != null) {
                stringMap.put(option, val);
            } else {
                stringMap.put(option, option.getDefault());
            }
        }
    }

    private void loadBooleans(@NotNull FileConfiguration config) {
        for (Option.Bool bool : Option.Bool.values()) {
            String key = bool.getKey();
            if (config.contains(key)) {
                booleans.put(bool, config.getBoolean(key));
            } else {
                booleans.put(bool, bool.getDefault());
            }
        }
    }

    private void loadDoubles(@NotNull FileConfiguration config) {
        for (Option.Double option : Option.Double.values()) {
            String key = option.getKey();
            if (config.contains(key)) {
                doubles.put(option, config.getDouble(key));
            } else {
                doubles.put(option, option.getDefault());
            }
        }
    }

    private void loadStringLists(@NotNull FileConfiguration config) {
        for (Option.StringList list : Option.StringList.values()) {
            String key = list.getKey();
            if (config.contains(key)) {
                lists.put(list, config.getStringList(key));
            } else {
                lists.put(list, list.getDefault());
            }
        }
    }

    @Override
    public @NotNull String getString(Option.@NotNull Text option) {
        return stringMap.get(option);
    }

    @Override
    public @NotNull String getColored(Option.@NotNull Text option) {
        return ChatColor.translateAlternateColorCodes('&', getString(option));
    }

    @Override
    public @NotNull List<String> getStringList(Option.@NotNull StringList option) {
        return new ArrayList<>(lists.get(option));
    }

    @Override
    public boolean getBoolean(Option.@NotNull Bool option) {
        return booleans.get(option);
    }

    @Override
    public double getDouble(Option.@NotNull Double option) {
        return doubles.get(option);
    }

    @Override
    public float getFloat(Option.@NotNull Double option) {
        return (float) getDouble(option);
    }
}
