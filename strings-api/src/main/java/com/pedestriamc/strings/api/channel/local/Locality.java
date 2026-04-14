package com.pedestriamc.strings.api.channel.local;

import com.pedestriamc.strings.api.user.StringsUser;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

/**
 * Platform independent World wrapper.
 * Implementing classes should override {@link Object#equals(Object)}
 * such that it returns true if the backing object is the same.
 * @param <T> The World class on the current platform, such as <code>org.bukkit.World</code>
 */
public interface Locality<T> {

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
    @NotNull
    T get();

    /**
     * Provides the name of the Locality
     * @return A String
     */
    @NotNull
    String getName();

    /**
     * Tells if this Locality contains a User
     * @param user The User to check
     * @return True if this Locality contains the User
     */
    boolean contains(@NotNull StringsUser user);

    /**
     * Provides a Set of all the Users in this world
     * @return A Set
     */
    @NotNull
    Set<StringsUser> getUsers();

}
