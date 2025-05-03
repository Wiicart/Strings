package com.pedestriamc.strings.api.channel;

import com.pedestriamc.strings.api.channel.data.ChannelData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.NoSuchElementException;
import java.util.Set;

public interface ChannelLoader {

    /**
     * Registers a Channel with Strings
     *
     * @param channel The channel to be registered
     */
    void registerChannel(Channel channel);

    /**
     * Unregisters a Channel
     *
     * @param channel The Channel to be unregistered
     * @throws NoSuchElementException Throws an exception if the Channel is not registered.
     */
    void unregisterChannel(Channel channel) throws NoSuchElementException;

    /**
     * Saves a Channel
     *
     * @param channel The channel to be saved
     */
    void saveChannel(Channel channel);

    /**
     * Builds and returns a Channel.
     * Acceptable types: stringchannel, proximity, world, proximity_strict, world_strict, helpop
     * @param data The ChannelData object loaded with information. If making a WorldChannel or ProximityChannel, use extending classes ProximityChannelData or WorldChannelData
     * @param type The type of channel.  Must be NORMAL, WORLD, or PROXIMITY or will return null.
     * @return A new StringsChannel
     * @throws UnsupportedOperationException When the Channel type cannot be built
     */
    @Nullable
    Channel build(ChannelData data, String type) throws UnsupportedOperationException;

    /**
     * Provides a StringsChannel based off its name, if it exists.
     *
     * @param name The name to search under.
     * @return A StringsChannel, if it exists.
     */
    @Nullable
    Channel getChannel(String name);

    /**
     * Provides a Set of all registered Channels
     * @return A populated Set
     */
    @NotNull
    Set<Channel> getChannels();

}
