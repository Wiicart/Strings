package com.pedestriamc.strings.api.settings;

import com.pedestriamc.strings.api.StringsAPI;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

/**
 * Provides all plugin configuration values. All values are retrieved with {@link Option} enums.
 * Falls-back to default values if the option cannot be found.
 * Instance provided by {@link StringsAPI#getSettings()}
 */
public interface Settings {

    /**
     * Gets a configuration value based off the key.
     * @param key The key
     * @return The value
     * @param <E> The enum type
     * @param <V> The value type
     */
    @NotNull
    <E extends Enum<E> & Option.CoreKey<V>, V> V get(@NotNull E key);

    /**
     * Gets a double configuration value as a float.
     * @param option The configuration option.
     * @return A float.
     */
    default float getFloat(@NotNull Option.Double option) {
        return get(option).floatValue();
    }

    /**
     * Gets a String as a Kyori Component.
     * In Hytale environments, an Exception will be thrown.
     * @param option The configuration option
     * @return The String as a component
     * @throws UnsupportedOperationException In Hytale environments.
     */
    Component getComponent(@NotNull Option.Text option);

}
