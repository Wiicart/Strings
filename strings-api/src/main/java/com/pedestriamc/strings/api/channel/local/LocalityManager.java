package com.pedestriamc.strings.api.channel.local;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public interface LocalityManager<T> {

    /**
     * Provides a Locality
     * @param object The backing class (<code>org.bukkit.World</code>, etc.)
     *               This should be the same as the parameterized type, and must mach the standard world-class
     *               of the platform, otherwise an {@link IllegalArgumentException} will be thrown.
     * @return A Locality representing the backing class.
     */
    Locality<T> get(Object object);

    /**
     * Converts Worlds to Localities.
     * @param worlds The backing class should be (<code>org.bukkit.World</code>, etc.)
     *               This should be the same as the parameterized type, and must mach the standard world-class
     *               of the platform, otherwise an {@link IllegalArgumentException} will be thrown.
     * @return A Set
     */
    default <W> Set<Locality<T>> convertToLocalities(@NotNull Collection<W> worlds) {
        Set<Locality<T>> result = new HashSet<>();
        for (Object world : worlds) {
            result.add(get(world));
        }

        return result;
    }

}
