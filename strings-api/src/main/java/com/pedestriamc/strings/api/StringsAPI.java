package com.pedestriamc.strings.api;

import org.bukkit.World;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * The API for Strings
 */
public interface StringsAPI {

    /**
     * Provides a short with the plugin's version number.
     * @return A short with the version.
     */
    short getVersion();

    /**
     * Provides a Set of all registered Channels.
     * @return A populated Set.
     */
    Set<StringsChannel> getChannels();

    /**
     * Gets the Channel with the specified name, if it exists.
     * @param name The name of the Channel to search for.
     * @return The Channel, if it exists.
     */
    Optional<StringsChannel> getChannel(String name);

    /**
     * Provides an Optional of StringsUser based on UUID
     * @param uuid The UUID of the Player.
     * @return An Optional containing a StringsUser if the StringsUser exists.
     */
    Optional<StringsUser> getStringsUser(UUID uuid);

    /**
     * Provides an Array of StringsChannels that are for a specific world.
     * @param world The World to search with.
     * @return An Array of StringsChannels.
     */
    StringsChannel[] getWorldChannels(World world);

    /**
     * Creates a StringsChannel that is Type.NORMAL.
     * @param name The name of the channel.
     * @param format The format of the channel.
     * @param defaultColor The default chat color of the channel.
     * @param callEvent Should this channel call events when players send messages?
     * @param doURLFilter Should this channel filter out URLs?
     * @param doProfanityFilter Should this channel filter profanity?
     * @param doCooldown Should this channel have chat cool-downs?
     * @param active Is this channel active?
     * @param membership The default membership of the Channel.
     * @param priority The Channel's priority.
     * @return A new StringsChannel that has been registered with Strings.
     */
    StringsChannel createChannel(String name, String format, String defaultColor, boolean callEvent, boolean doURLFilter, boolean doProfanityFilter, boolean doCooldown, boolean active, Membership membership, int priority);

    /**
     * Creates a World StringsChannel that is Type.WORLD.
     * @param name The name of the channel.
     * @param format The format of the channel.
     * @param defaultColor The default color of the channel.
     * @param callEvent Should this channel call events when players send messages?
     * @param doURLFilter Should this channel filter out URLs?
     * @param doProfanityFilter Should this channel filter profanity?
     * @param doCooldown Should this channel have chat cool-downs?
     * @param active Is this channel active?
     * @param world The world the channel is for.
     * @param membership The default membership of the Channel.
     * @param priority The Channel's priority.
     * @return A new StringsChannel that has been registered with Strings.
     */
    StringsChannel createChannel(String name, String format, String defaultColor, boolean callEvent, boolean doURLFilter, boolean doProfanityFilter, boolean doCooldown, boolean active, World world, Membership membership, int priority);

    /**
     * Creates a Proximity StringsChannel that is Type.PROXIMITY.
     * @param name The name of the channel.
     * @param format The format of the channel.
     * @param defaultColor The default color of the channel.
     * @param callEvent Should this channel call events when players send messages?
     * @param doURLFilter Should this channel filter out URLs?
     * @param doProfanityFilter Should this channel filter profanity?
     * @param doCooldown Should this channel have chat cool-downs?
     * @param active Is this channel active?
     * @param distance The proximity players have to be within to receive messages.
     * @param membership The default membership of the Channel.
     * @param priority The Channel's priority.
     * @return A new StringsChannel that has been registered with Strings.
     */
    StringsChannel createChannel(String name, String format, String defaultColor, boolean callEvent, boolean doURLFilter, boolean doProfanityFilter, boolean doCooldown, boolean active, int distance, Membership membership, int priority);

    /**
     * Deletes a StringsChannel and removes it from the channels file.
     * This is permanent and cannot be recovered!
     * As a global channel must exist, this method will not delete the global channel.
     * @param channel The StringsChannel to be deleted.
     */
    void deleteChannel(StringsChannel channel);

    /**
     * Returns true if the server is running Paper or a fork.
     * @return If the server is Paper.
     */
    boolean isPaper();


}

