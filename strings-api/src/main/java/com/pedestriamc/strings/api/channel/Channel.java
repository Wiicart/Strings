package com.pedestriamc.strings.api.channel;

import com.pedestriamc.strings.api.channel.data.ChannelBuilder;
import com.pedestriamc.strings.api.user.StringsUser;
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
     * Provides a new {@link ChannelBuilder}, with the Channel's name, format, and membership defined.
     *
     * @return A new {@link ChannelBuilder}
     */
    @Contract(value = "_, _, _ -> new", pure = true)
    static @NotNull ChannelBuilder builder(@NotNull String name, @NotNull String format, @NotNull Membership membership) {
        return new ChannelBuilder(name, format, membership);
    }

    /**
     * Provides the Channel identifier, i.e., {@code StringChannel.class} -> {@code stringchannel}.
     * This must be constant across instances of an implementation, and already set at compile-time.
     * This must be unique from other implementations.
     *
     * @return The Channel type identifier
     */
    @NotNull
    @Contract(pure = true)
    String getIdentifier();

    /**
     * Sends a message from a player to the channel.
     * Internally, messages might not be processed using this method.
     * For custom implementations, ensure {@link Channel#getFormat()}, {@link Channel#getRecipients(StringsUser)}, etc.
     * behave as intended, to ensure consistent behavior.
     *
     * @param user The player sending the message.
     * @param message The player's message.
     */
    void sendMessage(@NotNull StringsUser user, @NotNull String message);

    /**
     * Compares the priorities of the Channels.
     *
     * @param channel the object to be compared.
     * @return the param's priority - the channel instance priority
     */
    int compareTo(@NotNull Channel channel);

    /**
     * Resolves the final Channel a message would be directed too.
     * In most implementations, the Channel instance will return itself.
     *
     * @param user The sender.
     * @return A Channel, possibly {@code this}
     */
    @NotNull Channel resolve(@NotNull StringsUser user);

    /**
     * Provides the recipients of a message if the sender were to send a message in the Channel.
     * All implementations should account for Channel mutes.
     *
     * @param user The message sender.
     * @return A Set of Players.
     */
    Set<StringsUser> getRecipients(@NotNull StringsUser user);

    /**
     * Provides a Set of Players providing the greatest possible number of players in the scope of the Channel,
     * including monitors, members,
     * non-member recipients that are covered by default membership, and anyone else.
     *
     * @return A populated Set.
     */
    Set<StringsUser> getPlayersInScope();

    /**
     * Broadcasts a message to a Channel.
     * Broadcast recipients are determined using {@link Channel#getPlayersInScope()}
     *
     * @param message The broadcast message.
     */
    void broadcast(@NotNull String message);

    /**
     * Broadcasts a message to the Channel.
     * Unlike {@link Channel#broadcast(String)}, no formatting will be applied,
     * and the String will be broadcast to the Channel as is.
     * Broadcast recipients are determined using {@link Channel#getPlayersInScope()}.
     * @param message The message to broadcast.
     */
    void broadcastPlain(@NotNull String message);

    /**
     * Provides the formatting of the Channel.
     * Placeholders: {@code {prefix}, {suffix}, {displayname}, {message}}
     *
     * @return The Channel format.
     */
    @NotNull String getFormat();

    /**
     * Provides the formatting for broadcasts in the Channel.
     *
     * @return A String of the broadcast format.
     */
    @NotNull String getBroadcastFormat();

    /**
     * Sets the Channel's format.
     * Placeholders: {@code {prefix}, {suffix}, {displayname}, {message}}
     *
     * @param format The new format.
     */
    void setFormat(@NotNull String format);

    /**
     * Provides the Channel's name.
     *
     * @return The Channel name.
     */
    @NotNull String getName();

    /**
     * Sets the Channel's name.
     * To ensure this change is properly updated with the plugin, reload Strings or unregister then re-register
     * this Channel with the {@link ChannelLoader}
     *
     * @param name The new name.
     */
    void setName(@NotNull String name);

    /**
     * Provides the default chat color of the Channel.
     * Obsolete—Colors should be defined in the Channel's format instead.
     *
     * @return The default chat color.
     */
    @ApiStatus.Obsolete
    String getDefaultColor();

    /**
     * Sets the Channel's default chat color.
     * Obsolete—define a color in the Channel's format instead. {@link Channel#getFormat()}
     *
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
     * @param user The User to be added.
     */
    @ApiStatus.Internal
    void addMember(@NotNull StringsUser user);

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
    Set<StringsUser> getMembers();

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
    boolean callsEvents();

}
