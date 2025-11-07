package com.pedestriamc.strings.api.channel.local;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Platform independent World wrapper.
 * @param <T> The World class on the current platform, such as <code>org.bukkit.World</code>
 */
public interface Locality<T> {

    /**
     * Converts a World to a Locality
     * @param w The world
     * @return The world wrapped as a <code>Locality</code>
     * @param <W> The World class
     */
    static <W> @NotNull Locality<W> of(W w, String name) {
        return new LocalityImpl<>(w, name);
    }

    /**
     * Converts a Set of Worlds to a Set of Localities
     * @param worlds The original Set
     * @return The Set converted to Localities
     * @param <W> The World class
     */
    static <W> @NotNull Set<Locality<W>> convertToLocalities(@NotNull Collection<W> worlds) {
        Set<Locality<W>> result = new HashSet<>();
        for (W world : worlds) {
            result.add(of(world, "unknown"));
        }

        return result;
    }

    /**
     * Converts a Set of Localities to a Set of Worlds
     * @param localities The original Set
     * @return The Set converted to Worlds
     * @param <W> The World class
     */
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

    @NotNull String getName();

}
