package com.pedestriamc.strings.api.channel.data;

import com.pedestriamc.strings.api.annotation.Platform;
import com.pedestriamc.strings.api.channel.Membership;
import com.pedestriamc.strings.api.channel.local.LocalChannel;
import com.pedestriamc.strings.api.channel.local.Locality;
import org.jetbrains.annotations.NotNull;

import static org.jetbrains.annotations.ApiStatus.Internal;

import java.util.Set;

/**
 * A {@link ChannelBuilder}, with additional fields required to construct a {@link LocalChannel}.
 * Ensure this is parameterized correctly with the World implementation on your platform, or construction will fail.
 * @param <T> The World implementation on the current platform, such as <code>org.bukkit.World</code>
 */
@Platform.Agnostic
public final class LocalChannelBuilder<T> extends AbstractChannelBuilder<LocalChannelBuilder<T>> {

    private Set<Locality<T>> worlds;
    private double distance;

    /**
     * ChannelBuilder constructor with universally required parameters.
     *
     * @param name       The Channel name.
     * @param format     The Channel format.
     * @param membership The Channel membership
     * @param worlds The worlds the Channel will be used in
     */
    public LocalChannelBuilder(@NotNull String name, @NotNull String format, @NotNull Membership membership, Set<Locality<T>> worlds) {
        super(name, format, membership);
        this.worlds = worlds;
    }

    /**
     * Sets the worlds this Channel contains if it's an instance of {@link LocalChannel}
     * @param worlds A Set of the worlds.
     * @return this
     */
    public LocalChannelBuilder<T> setWorlds(final Set<Locality<T>> worlds) {
        this.worlds = worlds;
        return this;
    }

    /**
     * Provides the worlds this Channel should have in scope.
     * @return A Set of Worlds.
     */
    @Internal
    public Set<Locality<T>> getWorlds() {
        return worlds != null ? worlds : Set.of();
    }

    /**
     * Sets the proximity that messages will be sent around a player.
     * Only applicable to ProximityChannel.
     * @param distance The double value of the distance.
     * @return this
     */
    public LocalChannelBuilder<T> setDistance(double distance) {
        this.distance = distance;
        return this;
    }

    /**
     * Provides the proximity that messages will be sent around a player.
     * @return A double.
     */
    @Internal
    public double getDistance() {
        return distance;
    }

}
