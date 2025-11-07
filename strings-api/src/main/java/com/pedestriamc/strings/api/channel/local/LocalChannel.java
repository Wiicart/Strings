package com.pedestriamc.strings.api.channel.local;

import com.pedestriamc.strings.api.channel.Channel;
import com.pedestriamc.strings.api.user.StringsUser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.util.Set;

/**
 * A Channel interface for {@code WorldChannel} and {@code ProximityChannel}.
 * This interface provides methods unique to these classes.
 * {@link #getProximity()} and {@link #setProximity(double)} are only supported by {@code ProximityChannel} instances.
 * To check this, you can call {@link #getType()} before calling these methods.
 */
@SuppressWarnings("unused")
public interface LocalChannel<T> extends Channel {

    /**
     * Casts a Channel to a LocalChannel if it implements LocalChannel
     * @param channel The Channel to be cast
     * @return A LocalChannel if possible, otherwise null
     */
    @Nullable
    static LocalChannel<?> of(Channel channel) {
        if (channel instanceof LocalChannel<?> localChannel) {
            return localChannel;
        }

        return null;
    }

    /**
     * Checks if a Player is in the scope of the Channel, meaning that they are in a world this Channel is effective in.
     * A player not being in scope does not necessarily mean they can't send or receive messages from the Channel.
     * @param user The Player to check
     * @return If the Player is in scope
     */
    boolean containsInScope(@NotNull StringsUser user);

    /**
     * Provides a Set of all the Worlds the Channel is used in.
     * @return A populated Set.
     */
    Set<? extends Locality<T>> getWorlds();

    /**
     * Sets the Worlds this LocalChannel contains.
     * Channel implementations use a copy of the provided Set,
     * so updates made to the provided Set after invocation will not be reflected.
     *
     * @param worlds The Set of Worlds the Channel should contain.
     */
    void setWorlds(@NotNull Set<Locality<T>> worlds);

    /**
     * Tells if this LocalChannel contains a specific World.
     * Convenience method that handles Locality unwrapping for searching.
     *
     * @param world The world to search for
     * @return If the LocalChannel contains the World or not.
     */
    boolean containsWorld(@NotNull T world);

    /**
     * Tells if this Channel contains a World behind a Locality.
     * Accepts a Locality with a wildcard generic, but it must be of the same type as this channel.
     * @param locality The Locality
     * @return If this Channel contains the World behind the Locality
     */
    @SuppressWarnings("unchecked")
    default boolean containsLocality(@NotNull Locality<?> locality) {
        return containsWorld((T) locality.get());
    }

    /**
     * If this is an instance of a {@code ProximityChannel}, this will provide the proximity the Channel is set to.
     * Check if this is an instance of ProximityChannel with {@link LocalChannel#getType()}
     * @return A double of the proximity (max distance for players to receive a message).
     * @throws UnsupportedOperationException If this is an instance of a {@code WorldChannel}, this exception will be thrown.
     * Before calling, check {@link LocalChannel#getType()}
     */
    @Range(from = -1, to = Integer.MAX_VALUE)
    double getProximity() throws UnsupportedOperationException;

    /**
     * Sets the Proximity of the Channel if it's an instance of {@code ProximityChannel}.
     * Check if this is an instance of {@code ProximityChannel} with {@link LocalChannel#getType()}
     * @param proximity The proximity to set the Channel to
     * @throws UnsupportedOperationException If the Channel is not an instance of {@code ProximityChannel}
     */
    void setProximity(@Range(from = -1, to = Integer.MAX_VALUE) double proximity) throws UnsupportedOperationException;
}
