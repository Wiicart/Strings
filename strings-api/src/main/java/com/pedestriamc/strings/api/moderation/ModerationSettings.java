package com.pedestriamc.strings.api.moderation;

import org.jetbrains.annotations.NotNull;

public interface ModerationSettings {

    /**
     * Gets a configuration value based off the key.
     * @param key The key
     * @return The value
     * @param <E> The enum type
     * @param <V> The value type
     */
    @NotNull
    <E extends Enum<E> & Option.ModerationKey<V>, V> V get(@NotNull E key);

}
