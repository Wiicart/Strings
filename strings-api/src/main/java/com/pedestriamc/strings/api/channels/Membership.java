package com.pedestriamc.strings.api.channels;

/**
 * Represents the default membership of a StringsChannel.
 * If the membership is Membership.DEFAULT, all players receive messages from the channel by default.
 * @see #DEFAULT
 * @see #PERMISSION
 * @see #PROTECTED
 * @see #PARTY
 */
public enum Membership {
    /**
     * Channels set to Membership.DEFAULT are open to all players.
     * These channels will be considered by order of priority when a player sends a
     * message from the default channel.
     */
    DEFAULT,
    /**
     * Channels with this Membership are not open to all players and require permissions to join.
     * These channels are not considered when a player's channel is set to default.
     */
    PERMISSION,
    /**
     * Channels with this Membership are typically internal Strings channels, and this Membership basically tells
     * anything that involves Membership to ignore the channel.
     */
    PROTECTED,
    /**
     * Only PartyChannel(s) can have this Membership.
     */
    PARTY
}
