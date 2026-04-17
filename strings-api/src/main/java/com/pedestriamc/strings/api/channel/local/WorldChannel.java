package com.pedestriamc.strings.api.channel.local;


import com.pedestriamc.strings.api.channel.Type;
import org.jetbrains.annotations.NotNull;

/**
 * Basic {@link LocalChannel} variant that focuses scope solely on worlds.
 * WorldChannels may contain multiple worlds, all included in one scope.
 * Messages sent through this channel will be sent to all players in the channel's scope.
 * @see LocalChannel
 * @see Locality
 * @param <T> The platform "locality" (world) type.
 */
public interface WorldChannel<T> extends LocalChannel<T> {

    @NotNull
    @Override
    default Type getType() {
        return Type.WORLD;
    }

}
