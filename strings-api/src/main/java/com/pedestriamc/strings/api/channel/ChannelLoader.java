package com.pedestriamc.strings.api.channel;

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
