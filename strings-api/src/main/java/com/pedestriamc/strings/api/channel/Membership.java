package com.pedestriamc.strings.api.channel;

/**
 * Represents the membership of a {@link Channel}. This helps determine eligibility to send and receive messages.
 */
public enum Membership {
    /**
     * Channels set to this {@code Membership} are open to all players.
     * These channels will be considered by order of priority when a player sends a
     * message from the {@code DefaultChannel}, using {@link Channel#getPriority()}
     */
    DEFAULT,
    /**
     * Channels with this {@code Membership} are not open to players by default and require permissions to join.
     * If a Player is still eligible for this Channel, it's possible to be selected by the {@code DefaultChannel}.
     */
    PERMISSION,
    /**
     * Channels with this Membership are typically internal Channels, behavior may vary.
     */
    PROTECTED,
}
