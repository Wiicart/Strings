package com.pedestriamc.strings.api.channel;

import com.pedestriamc.strings.api.channel.data.ChannelData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.NoSuchElementException;
import java.util.Set;

/**
 * Manages registration, un-registration, and access for all {@link Channel}(s)
 */
@SuppressWarnings("unused")
public interface ChannelLoader {
    /**
     * Registers a {@link Channel}
     * @param channel The Channel to be registered
     */
    void registerChannel(Channel channel);

    /**
     * Unregisters a {@link Channel}
     * @param channel The Channel to be unregistered
     * @throws NoSuchElementException Thrown if the Channel is not registered when this is called
     */
    void unregisterChannel(Channel channel) throws NoSuchElementException;

    /**
     * Saves a {@link Channel}
     * @param channel The Channel to be saved
     */
    void saveChannel(Channel channel);

    /**
     * Builds and returns a {@link Channel}.
     * @param data The {@link ChannelData} object loaded with the information necessary to construct a Channel.
     * @param type The type of channel. Must be one of {@code stringchannel}, {@code proximity}, {@code world}, {@code proximity_strict}, {@code world_strict}, {@code helpop}
     * @return A new Channel
     * @throws UnsupportedOperationException When the Channel type cannot be built
     */
    @Nullable
    Channel build(ChannelData data, String type) throws UnsupportedOperationException;

    /**
     * Provides a {@link Channel} based off its name, if it exists.
     * @param name The name of the Channel to search for.
     * @return A Channel, if it exists.
     */
    @Nullable
    Channel getChannel(String name);

    /**
     * Provides a {@link Set} of all registered {@link Channel}(s)
     * @return A populated Set
     */
    @NotNull
    Set<Channel> getChannels();
}
