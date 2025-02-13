package com.pedestriamc.strings.api.channels;

import com.pedestriamc.strings.api.channels.data.ChannelData;
import org.jetbrains.annotations.Nullable;

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
     * @throws Exception Throws an exception if the Channel is not registered.
     */
    void unregisterChannel(Channel channel) throws Exception;

    /**
     * Saves a Channel
     *
     * @param channel The channel to be saved
     */
    void saveChannel(Channel channel);

    /**
     * Builds and returns a Channel
     *
     * @param data The ChannelData object loaded with information. If making a WorldChannel or ProximityChannel, use extending classes ProximityChannelData or WorldChannelData
     * @param type The type of channel.  Must be NORMAL, WORLD, or PROXIMITY or will return null.
     * @return A new StringsChannel
     * @throws Exception When
     */
    @Nullable
    Channel build(ChannelData data, String type) throws Exception;

    /**
     * Provides a StringsChannel based off its name, if it exists.
     *
     * @param name The name to search under.
     * @return A StringsChannel, if it exists.
     */
    @Nullable
    Channel getChannel(String name);

}
