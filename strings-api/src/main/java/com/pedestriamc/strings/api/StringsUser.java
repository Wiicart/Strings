package com.pedestriamc.strings.api;

import java.util.Set;
import java.util.UUID;

/**
 * This represents the Strings internal User object which stores data on individual players.
 */
public interface StringsUser {

    /**
     * Provides the StringsUser's UUID.
     * @return A UUID.
     */
    UUID getUuid();

    /**
     * Provides the StringsUser's name.
     * @return A String containing the name.
     */
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
    void setChatColor(String chatColor);

    /**
     * Provides the prefix of the StringsUser, if it exists.
     * @return A String of the prefix.
     */
    String getPrefix();

    /**
     * Sets the StringsUser's prefix.
     * @param prefix The new prefix.
     */
    void setPrefix(String prefix);

    /**
     * Provides the suffix of the StringsUser, if it exists.
     * @return A String of the suffix.
     */
    String getSuffix();

    /**
     * Sets the StringsUser's suffix.
     * @param suffix The new suffix.
     */
    void setSuffix(String suffix);

    /**
     * Provides the StringsUser's display name.
     * Falls back to the server if none is set with Strings.
     * @return The display name.
     */
    String getDisplayName();

    /**
     * Sets the StringsUser's display name.
     *
     * @param displayName The new display name.
     */
    void setDisplayName(String displayName);

    /**
     * Provides a the active StringsChannel of the StringsUser.
     * @return The active StringsChannel.
     */
    StringsChannel getActiveChannel();

    /**
     * Sets the StringsUser's active StringChannel.
     * This channel is where messages will be sent by the player.
     * @param channel The StringsChannel to be set to active.
     */
    void setActiveChannel(StringsChannel channel);

    /**
     * Provides a Set of all the channels the User is in.
     * @return A populated Set.
     */
    Set<StringsChannel> getChannels();

    /**
     * Has this StringsUser join a StringsChannel.
     * @param channel The channel to join.
     */
    void joinChannel(StringsChannel channel);

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
}
