package com.pedestriamc.strings.api.channel;

/**
 * Represents the types of StringsChannels.
 * Type.PROTECTED represents an internal channel for Strings and should not be modified.
 */
public enum Type {
    /**
     * Represents Channels for internal plugin use
     */
    PROTECTED,

    /**
     * Represents "normal" channels such as StringChannels
     */
    NORMAL(),

    /**
     * Represents ProximityChannels
     */
    PROXIMITY,

    /**
     * Represents WorldChannels
     */
    WORLD,

    /**
     * Represents the DefaultChannel, a channel that does not process any messages; it instead
     * forwards them to a different channel.
     */
    DEFAULT,

    /**
     * Represents PartyChannels
     */
    PARTY,

    CUSTOM
}
