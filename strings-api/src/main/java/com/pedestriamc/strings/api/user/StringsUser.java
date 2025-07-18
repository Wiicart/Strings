package com.pedestriamc.strings.api.user;

import com.pedestriamc.strings.api.StringsAPI;
import com.pedestriamc.strings.api.channel.Channel;
import com.pedestriamc.strings.api.channel.Monitorable;
import com.pedestriamc.strings.api.text.format.StringsComponent;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.UUID;

/**
 * This represents the Strings internal User object which stores data on individual players.
 * Changes are not saved by default, you must use {@link StringsAPI#saveStringsUser(StringsUser)} to save.
 */
@SuppressWarnings("unused")
public interface StringsUser {

    /**
     * Provides the StringsUser's UUID.
     * @return A UUID.
     */
    @NotNull
    UUID getUniqueId();

    void message(@NotNull String message);

    /**
     * Provides the StringsUser's name.
     * @return A String containing the name.
     */
    @NotNull
    String getName();

    /**
     * Provides the StringsUser's chat color.
     * If the StringsUser's chat color is null, the StringsUser's active channel's chat color is returned.
     *
     * @deprecated Use {@link StringsUser#getChatColorComponent()} instead.
     * @return A chat color.
     */
    @Nullable
    @ApiStatus.Obsolete
    String getChatColor();

    /**
     * Sets the chat color of the User.
     * @deprecated Use {@link StringsUser#getChatColorComponent()} instead.
     * @param chatColor The new chat color.
     */
    @ApiStatus.Obsolete
    void setChatColor(String chatColor);

    /**
     * Provides a {@link StringsComponent} of the Player's chat color.
     * Simply a wrapper for adventure's {@code Component}
     *
     * @return The Player's chat color StringsComponent
     */
    StringsComponent getChatColorComponent();

    /**
     * Sets a {@link StringsComponent} of the Player's chat color.
     * {@link StringsComponent} is simply a wrapper for adventure's {@code Component}
     */
    void setChatColorComponent(StringsComponent chatColor);

    /**
     * Provides the prefix of the StringsUser, if it exists.
     * @return A String of the prefix.
     */
    String getPrefix();

    /**
     * Sets the StringsUser's prefix.
     * @param prefix The new prefix.
     */
    void setPrefix(@NotNull String prefix);

    /**
     * Provides the suffix of the StringsUser, if it exists.
     * @return A String of the suffix.
     */
    String getSuffix();

    /**
     * Sets the StringsUser's suffix.
     * @param suffix The new suffix.
     */
    void setSuffix(@NotNull String suffix);

    /**
     * Provides the StringsUser's display name.
     * Falls back to the server if none is set with Strings.
     * @return The display name.
     */
    @NotNull
    String getDisplayName();

    /**
     * Sets the StringsUser's display name.
     * @param displayName The new display name.
     */
    void setDisplayName(@NotNull String displayName);

    /**
     * Provides the active StringsChannel of the StringsUser.
     * @return The active StringsChannel.
     */
    @NotNull
    Channel getActiveChannel();

    /**
     * Sets the StringsUser's active StringChannel.
     * This channel is where messages will be sent by the player.
     * @param channel The StringsChannel to be set to active.
     */
    void setActiveChannel(@NotNull Channel channel);

    /**
     * Provides a Set of all the channels the User is in.
     * @return A populated Set.
     */
    Set<Channel> getChannels();

    /**
     * Has this StringsUser join a Channel.
     * @param channel The channel to join.
     */
    void joinChannel(@NotNull Channel channel);

    /**
     * Has the StringsUser leave a Channel.
     * @param channel The channel to leave.
     */
    void leaveChannel(Channel channel);

    /**
     * Returns if this StringsUser is a member of a defined StringsChannel.
     * @param channel The Channel to check
     * @return True/false if the StringsUser is a member.
     */
    boolean memberOf(Channel channel);

    /**
     * Tells if the StringsUser has mentions enabled.
     * @return A boolean if the User has mentions enabled or not.
     */
    boolean isMentionsEnabled();

    /**
     * Sets if the StringsUser has mentions enabled.
     * @param mentionsEnabled true/false if the StringsUser should have mentions enabled.
     */
    void setMentionsEnabled(boolean mentionsEnabled);

    /**
     * Ignores a Player.
     * @param player The Player to ignore.
     */
    void ignore(@NotNull Player player);

    /**
     * Stops ignoring a Player.
     * @param player The Player to stop ignoring.
     */
    void stopIgnoring(@NotNull Player player);

    /**
     * Provides a Set of Players that this User has ignored (for /msg).
     * @return A populated Set.
     */
    Set<UUID> getIgnoredPlayers();

    /**
     * Provides the Player this StringsUser is associated with.
     * @return The Player.
     */
    Player getPlayer();

    /**
     * Monitors a Channel
     * @param monitorable The Channel to monitor.
     */
    void monitor(@NotNull Monitorable monitorable);

    /**
     * Unmonitors a Channel
     * @param monitorable The Channel to unmonitor.
     */
    void unmonitor(@NotNull Monitorable monitorable);

}
