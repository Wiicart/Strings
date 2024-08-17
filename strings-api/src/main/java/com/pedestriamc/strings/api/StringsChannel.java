package com.pedestriamc.strings.api;

import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.Set;

/**
 * An API Wrapper for all types of Channels.
 */
public interface StringsChannel {

    /**
     * Sends a message in a Channel from a player.
     * @param player The sender.
     * @param message The message being sent.
     */
    void sendMessage(Player player, String message);

    /**
     * Broadcasts a message to a Channel.
     * @param message The broadcast message.
     */
    void broadcastMessage(String message);

    /**
     * Provides the formatting of the Channel.
     * @return The Channel format.
     */
    String getFormat();

    /**
     * Provides the default chat color of the Channel.
     * @return The default chat color.
     */
    String getDefaultColor();

    /**
     * Provides the Channel's name.
     * @return The Channel name.
     */
    String getName();

    /**
     * Sets the Channel's name.
     * @param name The new name.
     */
    void setName(String name);

    /**
     * Sets the Channel's default chat color.
     * @param defaultColor The new default chat color.
     */
    void setDefaultColor(String defaultColor);

    /**
     * Sets the Channel's format.
     * @param format The new format.
     */
    void setFormat(String format);

    /**
     * Adds a player to the Channel.
     * @param player The player to be added.
     */
    void addPlayer(Player player);

    /**
     * Removes a player from the Channel.
     * @param player The player to be removed.
     */
    void removePlayer(Player player);

    /**
     * Adds a User to the channel.
     * @param stringsUser The User to be added.
     */
    void addPlayer(StringsUser stringsUser);

    /**
     * Removes a User from the Channel.
     * @param stringsUser The User to be removed.
     */
    void removePlayer(StringsUser stringsUser);

    /**
     * Provides a Set of the members of the Channel.
     * @return A populated Set of Players.
     */
    Set<Player> getMembers();

    /**
     * Tells if the channel requires URL filtering.
     * @return If the Channel does URL filtering.
     */
    boolean doURLFilter();

    /**
     * Tells if the Channel requires profanity filtering.
     * @return If the Channel does profanity filtering.
     */
    boolean doProfanityFilter();

    /**
     * Tells if the Channel requires a chat cool-down.
     * @return If the Channel does a chat cool-down.
     */
    boolean doCooldown();

    /**
     * Provides the Channel's Type.
     * @return The Channel's Type.
     */
    Type getType();

    /**
     * Allows the enabling and disabling of the Channel.
     * When a channel is disabled it still exists, however messages can't be sent in the channel.
     * @param isEnabled If the Channel should be enabled/disabled.
     */
    void setEnabled(boolean isEnabled);

    /**
     * Provides if the Channel is enabled.
     * @return The status of the Channel.
     */
    boolean isEnabled();

    /**
     * Provides the World the StringsChannel is for.
     * If the StringsChannel is not a WorldChannel, the Optional will be empty.
     * @return The World the StringsChannel is for, if it's a WorldChannel.
     */
    Optional<World> getWorld();

    /**
     * Provides the proximity for the StringsChannel.
     * If the StringsChannel is not a ProximityChannel, the Optional will be empty.
     * @return An Optional potentially containing the channel proximity.
     */
    OptionalDouble getOptionalProximity();

    /**
     * Provides the proximity for the StringsChannel.
     * If the StringsChannel is not a ProximityChannel, -1 will be returned.
     * @return A double.
     */
    double getProximity();

    /**
     * Provides the StringsChannel's default Membership enum.
     * @return The StringsChannel's membership enum.
     */
    Membership getMembership();

    /**
     * Provides the priority this StringsChannel has.
     * The higher the number, the higher the priority.
     * @return An int of the priority.
     */
    int getPriority();

    /**
     * Sets the StringsChannel's proximity, if it's a ProximityChannel.
     * If the channel is not a ProximityChannel, nothing will happen.
     * @param proximity The new proximity.
     */
    void setProximity(double proximity);
}
