package com.pedestriamc.strings.api.channel;

import com.pedestriamc.strings.api.channel.local.LocalChannel;
import com.pedestriamc.strings.api.user.StringsUser;

/**
 * Represents all {@link Channel} types, signifying Channel behavior.
 */
public enum Type {
    /**
     * Represents Channels for internal plugin use. These Channels should not be modified.
     */
    PROTECTED(),
    /**
     * Represents "normal" channels such as a {@code StringChannel}
     */
    NORMAL(),
    /**
     * Represents all {@code ProximityChannel} implementations, which are instances of {@link LocalChannel}
     */
    PROXIMITY(),
    /**
     * Represents all {@code WorldChannel} implementations, which are instances of {@link LocalChannel}
     */
    WORLD(),
    /**
     * Represents the {@code DefaultChannel}, a channel that does not process any messages; it instead
     * forwards them to a different channel.
     * The final destination can be determined with {@link Channel#resolve(StringsUser)}
     */
    DEFAULT(),
    /**
     * Represents PartyChannels
     */
    PARTY(),
    /**
     * Represents custom Channels.
     */
    CUSTOM(),
}
