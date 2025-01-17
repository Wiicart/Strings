package com.pedestriamc.strings.api.channels;

import com.pedestriamc.strings.api.StringsUser;
import com.pedestriamc.strings.api.Membership;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Set;

/**
 * The interface for all Channels.
 */
public interface Channel {

    /**
     * Sends a message from a player to the channel.
     * @param player The player sending the message.
     * @param message The player's message.
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
     * Sets the Channel's format.
     * @param format The new format.
     */
    void setFormat(String format);

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
     * Provides the default chat color of the Channel.
     * @return The default chat color.
     */
    String getDefaultColor();

    /**
     * Sets the Channel's default chat color.
     * @param defaultColor The new default chat color.
     */
    void setDefaultColor(String defaultColor);

    /**
     * Tells if the channel requires URL filtering.
     * @return If the Channel does URL filtering.
     */
    boolean doUrlFilter();

    /**
     * Sets if the channel should do URL filtering.
     * @param doUrlFilter If the channel should filter URLs.
     */
    void setUrlFilter(boolean doUrlFilter);

    /**
     * Tells if the Channel requires profanity filtering.
     * @return If the Channel does profanity filtering.
     */
    boolean doProfanityFilter();

    /**
     * Sets if the Channel should preform profanity filtering.
     * @param doProfanityFilter If the Channel should do profanity filtering.
     */
    void setProfanityFilter(boolean doProfanityFilter);

    /**
     * Tells if the Channel requires a chat cool-down.
     * @return If the Channel does a chat cool-down.
     */
    boolean doCooldown();

    /**
     * Sets if the Channel should do cool-downs.
     * @param doCooldown Should the Channel do cool-downs.
     */
    void setDoCooldown(boolean doCooldown);

    /**
     * Adds a player to the Channel.
     * @param player The player to be added.
     */
    void addPlayer(Player player);

    /**
     * Adds a User to the channel.
     * @param user The User to be added.
     */
    void addPlayer(StringsUser user);

    /**
     * Removes a player from the Channel.
     * @param player The player to be removed.
     */
    void removePlayer(Player player);

    /**
     * Removes a User from the Channel.
     * @param user The User to be removed.
     */
    void removePlayer(StringsUser user);

    /**
     * Provides a Set of the members of the Channel.
     * @return A populated Set of Players.
     */
    Set<Player> getMembers();

    /**
     * Provides the Channel's Type.
     * @return The Channel's Type.
     */
    Type getType();


    /**
     * Provides all the Channel's information in a Map.
     * @return A populated Map containing the Channel's information.
     */
    Map<String, Object> getData();

    /**
     * Provides the Channel's default Membership enum.
     * @return The Channel's membership enum.
     */
    Membership getMembership();

    /**
     * Provides the priority of the Channel.
     * @return An int representing the priority.
     */
    int getPriority();

    /**
     * Deprecated - use ChannelLoader#saveChannel(Channel channel) instead
     * Saves the Channel to channels.yml
     */
    @Deprecated
    void saveChannel();

    /**
     * Returns if a player has permission to use the Channel.
     * @param player The player to check
     * @return If the player has permission
     */
    boolean allows(Player player);

    boolean isCallEvent();

}
