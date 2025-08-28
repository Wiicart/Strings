package com.pedestriamc.strings.api.settings;

import com.pedestriamc.strings.api.StringsAPI;
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

}
