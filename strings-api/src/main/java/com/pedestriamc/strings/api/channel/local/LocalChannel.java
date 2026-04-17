package com.pedestriamc.strings.api.channel.local;

import com.pedestriamc.strings.api.channel.Channel;
import com.pedestriamc.strings.api.user.StringsUser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Set;

/**
 * Universal methods for LocalChannels.
 * LocalChannels are defined within a specific scope of one or more {@link Locality}(s).
 * @param <T> The platform "locality" (world) type.
 * @see ProximityChannel
 * @see WorldChannel
 * @see Locality
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
    @NotNull
    @UnmodifiableView
    Set<? extends Locality<T>> getWorlds();

    /**
     * Updates the worlds within the scope of this channel.
     * Channels will use a copy of this set, so any changes made within the provided Set may not be reflected.
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
    boolean containsLocality(@NotNull Locality<?> locality);

    /**
     * Tells if this channel is {@code strict}.<br/>
     * Strict LocalChannel variants give no special treatment to channel members,
     * as standard local channels do.
     * @return If this channel is strict.
     */
    boolean isStrict();
}
