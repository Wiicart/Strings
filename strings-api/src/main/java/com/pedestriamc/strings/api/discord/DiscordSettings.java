package com.pedestriamc.strings.api.discord;

import org.jetbrains.annotations.NotNull;

/**
 * Used to retrieve values from StringsDiscord's {@link Option}
 */
public interface DiscordSettings {

    /**
     * Gets a configuration value based off the key.
     * @param key The key
     * @return The value
     * @param <E> The enum type
     * @param <V> The value type
     */
    @NotNull
    <E extends Enum<E> & Option.DiscordKey<V>, V> V get(@NotNull E key);

}
