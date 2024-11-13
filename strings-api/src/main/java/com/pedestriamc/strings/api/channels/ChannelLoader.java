package com.pedestriamc.strings.api.channels;

import com.pedestriamc.strings.api.channels.data.ChannelData;
import org.jetbrains.annotations.Nullable;

public interface ChannelLoader {

    /**
     * Registers a Channel
     * @param channel The channel to be registered
     */
    void registerChannel(StringsChannel channel);

    /**
     * Saves a Channel
     * @param channel The channel to be saved
     */
    void saveChannel(StringsChannel channel);

    /**
     * Builds and returns a Channel
     * @param data The ChannelData object loaded with information. If making a WorldChannel or ProximityChannel, use extending classes ProximityChannelData or WorldChannelData
     * @param type The type of channel.  Must be NORMAL, WORLD, or PROXIMITY or will return null.
     * @return A new StringsChannel
     * @throws Exception When
     */
    @Nullable
    StringsChannel build(ChannelData data, Type type) throws Exception;

    /**
     * Provides a StringsChannel based off its name, if it exists.
     * @param name The name to search under.
     * @return A StringsChannel, if it exists.
     */
    @Nullable
    StringsChannel getChannel(String name);
}
