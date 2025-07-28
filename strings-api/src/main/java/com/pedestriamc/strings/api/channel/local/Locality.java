package com.pedestriamc.strings.api.channel.local;

import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

/**
 * Platform independent World wrapper.
 * @param <T> The World class on the current platform, such as <code>org.bukkit.World</code>
 */
public interface Locality<T> {

    static <W> @NotNull Set<Locality<W>> convertToLocalities(@NotNull Set<W> worlds) {
        Set<Locality<W>> result = new HashSet<>();
        for (W world : worlds) {
            result.add(new LocalityImpl<>(world));
        }

        return result;
    }

    static <W> @NotNull Set<W> convertToWorlds(@NotNull Set<Locality<W>> localities) {
        Set<W> result = new HashSet<>();
        for (Locality<W> locality : localities) {
            result.add(locality.get());
        }

        return result;
    }

    /**
     * Provides the platform-specific World implementation
     * @return The platform-specific Object
     */
    @NotNull T get();

}
