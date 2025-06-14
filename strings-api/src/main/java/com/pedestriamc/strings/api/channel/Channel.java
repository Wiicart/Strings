package com.pedestriamc.strings.api.channel;

import com.pedestriamc.strings.api.channel.data.ChannelBuilder;
import com.pedestriamc.strings.api.user.StringsUser;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permissible;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.util.Map;
import java.util.Set;

/**
 * The interface for all Channels.
 */
@SuppressWarnings("unused")
public interface Channel extends Comparable<Channel> {

    /**
     * Provides a new {@link ChannelBuilder}, with a name defined.
     * @return A new {@link ChannelBuilder}
     */
    @Contract(value = "_, _, _ -> new", pure = true)
    static @NotNull ChannelBuilder builder(@NotNull String name, @NotNull String format, @NotNull Membership membership) {
        return new ChannelBuilder(name, format, membership);
    }

    /**
     * Provides the Channel identifier, i.e., {@code StringChannel} -> {@code stringchannel}.
     * This must be constant across instances of an implementation
     * but must be unique from other implementations.
     *
     * @return The Channel type identifier
     */
    @NotNull
    @Contract(pure = true)
    String getIdentifier();

    /**
     * Sends a message from a player to the channel.
     * @param player The player sending the message.
     * @param message The player's message.
     */
    void sendMessage(@NotNull Player player, @NotNull String message);

    /**
     * Compares the priorities of the Channels.
     * @param channel the object to be compared.
     * @return the param's priority - the channel instance priority
     */
    int compareTo(@NotNull Channel channel);

    /**
     * Resolves the final Channel a message would be directed too.
     * In most implementations, the Channel will return itself.
     * @param player The sender.
     * @return A Channel
     */
    Channel resolve(@NotNull Player player);

    /**
     * Provides the recipients of a message if the sender were to send a message in the Channel.
     * @param sender The message sender.
     * @return A Set of Players.
     */
    Set<Player> getRecipients(@NotNull Player sender);

    /**
     * Broadcasts a message to a Channel.
     * @param message The broadcast message.
     */
    void broadcast(String message);

    /**
     * Provides the formatting of the Channel.
     * @return The Channel format.
     */
    @NotNull
    String getFormat();

    /**
     * Provides the formatting for broadcasts in the Channel.
     * @return A String of the broadcast format.
     */
    String getBroadcastFormat();

    /**
     * Sets the Channel's format.
     * @param format The new format.
     */
    void setFormat(@NotNull String format);

    /**
     * Provides the Channel's name.
     * @return The Channel name.
     */
    @NotNull
    String getName();

    /**
     * Sets the Channel's name.
     * @param name The new name.
     */
    void setName(@NotNull String name);

    /**
     * Provides the default chat color of the Channel.
     * Obsolete - define a color in the Channels format instead. {@link Channel#getFormat()}
     * @return The default chat color.
     */
    @ApiStatus.Obsolete
    String getDefaultColor();

    /**
     * Sets the Channel's default chat color.
     * @param defaultColor The new default chat color.
     */
    @ApiStatus.Obsolete
    void setDefaultColor(String defaultColor);

    /**
     * Tells if the channel requires URL filtering.
     *
     * @return If the Channel does URL filtering.
     */
    boolean isUrlFiltering();

    /**
     * Sets if the channel should do URL filtering.
     * @param doUrlFilter If the channel should filter URLs.
     */
    void setUrlFilter(boolean doUrlFilter);

    /**
     * Tells if the Channel requires profanity filtering.
     * @return If the Channel does profanity filtering.
     */
    boolean isProfanityFiltering();

    /**
     * Sets if the Channel should preform profanity filtering.
     * @param doProfanityFilter If the Channel should do profanity filtering.
     */
    void setProfanityFilter(boolean doProfanityFilter);

    /**
     * Tells if the Channel requires a chat cool-down.
     * @return If the Channel does a chat cooldown.
     */
    boolean isCooldownEnabled();

    /**
     * Sets if the Channel should do cool-downs.
     * @param doCooldown Should the Channel do cool downs?
     */
    void setDoCooldown(boolean doCooldown);

    /**
     * For internal usage - use {@link StringsUser#joinChannel(Channel)} instead.
     * Adds a User to the channel.
     *
     * @param player The Player to be added.
     */
    @ApiStatus.Internal
    void addMember(@NotNull Player player);

    /**
     * For internal usage - use {@link StringsUser#joinChannel(Channel)} instead.
     * Adds a User to the channel.
     *
     * @param user The User to be added.
     */
    @ApiStatus.Internal
    void addMember(@NotNull StringsUser user);

    /**
     * For internal usage - use {@link StringsUser#leaveChannel(Channel)} instead.
     * Removes a player from the Channel.
     *
     * @param player The player to be removed.
     */
    @ApiStatus.Internal
    void removeMember(@NotNull Player player);

    /**
     * For internal usage - use {@link StringsUser#leaveChannel(Channel)} instead.
     * Removes a player from the Channel.
     *
     * @param user The user to be removed.
     */
    @ApiStatus.Internal
    void removeMember(@NotNull StringsUser user);

    /**
     * Provides a Set of the members of the Channel.
     * @return A populated Set of Players.
     */
    Set<Player> getMembers();

    /**
     * Provides the Channel's {@link Type}.
     * @return The Channel's Type.
     */
    @NotNull
    Type getType();


    /**
     * Provides all the Channel's information in a Map.
     * @return A populated Map containing the Channel's information.
     */
    Map<String, Object> getData();

    /**
     * Provides the Channel's default {@link Membership} enum.
     * @return The Channel's Membership enum.
     */
    Membership getMembership();

    /**
     * Provides the priority of the Channel.
     * @return An int representing the priority.
     */
    @Range(from = Integer.MIN_VALUE, to = Integer.MAX_VALUE)
    int getPriority();

    /**
     * Returns if a player has permission to use the Channel.
     * @param permissible The player to check
     * @return If the player has permission
     */
    boolean allows(@NotNull Permissible permissible);

    /**
     * Signifies if the Channel calls an Event when a message is sent in Spigot environments.
     * @return If an Event is called.
     */
    boolean isCallEvent();

}
