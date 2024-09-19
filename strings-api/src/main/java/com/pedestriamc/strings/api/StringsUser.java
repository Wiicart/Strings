package com.pedestriamc.strings.api;

import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.UUID;

/**
 * This represents the Strings internal User object which stores data on individual players.
 */
@SuppressWarnings("unused")
public interface StringsUser {

    /**
     * Provides the StringsUser's UUID.
     * @return A UUID.
     */
    @NotNull
    UUID getUuid();

    /**
     * Provides the StringsUser's name.
     * @return A String containing the name.
     */
    @NotNull
    String getName();

    /**
     * Provides the StringsUser's chat color.
     * If the StringsUser's chat color is null, the StringsUser's active channel's chat color is returned.
     * @return A chat color.
     */
    String getChatColor();

    /**
     * Sets the chat color of the User.
     * @param chatColor The new chat color.
     */
    void setChatColor(@NotNull String chatColor);

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
     *
     * @param displayName The new display name.
     */
    void setDisplayName(@NotNull String displayName);

    /**
     * Provides the active StringsChannel of the StringsUser.
     * @return The active StringsChannel.
     */
    @NotNull
    StringsChannel getActiveChannel();

    /**
     * Sets the StringsUser's active StringChannel.
     * This channel is where messages will be sent by the player.
     * @param channel The StringsChannel to be set to active.
     */
    void setActiveChannel(@NotNull StringsChannel channel);

    /**
     * Provides a Set of all the channels the User is in.
     * @return A populated Set.
     */
    Set<StringsChannel> getChannels();

    /**
     * Has this StringsUser join a StringsChannel.
     * @param channel The channel to join.
     */
    void joinChannel(@NotNull StringsChannel channel);

    /**
     * Has this StringsUser leave a StringsChannel.
     * @param channel The channel to leave.
     */
    void leaveChannel(StringsChannel channel);

    /**
     * Returns if this StringsUser is a member of a defined StringsChannel.
     * @param channel The Channel to check
     * @return True/false if the StringsUser is a member.
     */
    boolean memberOf(StringsChannel channel);

    /**
     * Tells if the StringsUser has mentions enabled.
     * @return A boolean if the User has mentions enabled or not.
     */
    boolean mentionsEnabled();

    /**
     * Sets if the StringsUser has mentions enabled.
     * @param mentionsEnabled true/false if the StringsUser should have mentions enabled.
     */
    void setMentionsEnabled(boolean mentionsEnabled);
}
