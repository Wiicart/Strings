package com.pedestriamc.strings.api;

import com.pedestriamc.strings.api.channel.ChannelLoader;
import com.pedestriamc.strings.api.message.Messenger;
import com.pedestriamc.strings.api.user.StringsUser;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * The API for Strings
 */
@SuppressWarnings("unused")
public interface StringsAPI {

    /**
     * Provides the ChannelLoader instance.
     * @return The ChannelLoader.
     */
    ChannelLoader getChannelLoader();

    /**
     * Provides a short with the plugin's version number.
     * @return A short with the version.
     */
    short getVersion();

    /**
     * Provides a StringsUser based off a UUID if it exists.
     * @param uuid The UUID to search under.
     * @return A StringsUser, if it exists.
     */
    @Nullable
    StringsUser getStringsUser(UUID uuid);

    /**
     * Provides a StringsUser based off a Player if it exists.
     * @param player The Player to search under.
     * @return A StringsUser, if it exists.
     */
    @Nullable
    StringsUser getStringsUser(Player player);

    /**
     * Saves a StringsUser
     * Modifications to Users do not persist by default, you must save it to persist.
     * @param user The user to be saved.
     */
    void saveStringsUser(StringsUser user);

    /**
     * Returns true if the server is running Paper or a fork.
     * @return If the server is Paper.
     */
    boolean isPaper();

    /**
     * Mentions a Player.
     * Uses the format found in the Strings config.yml
     * @param subject The player to be mentioned.
     * @param sender The sender of the mention.
     */
    void mention(Player subject, Player sender);

    /**
     * Mentions a Player.
     * Uses the format found in the Strings config.yml
     * @param subject The StringsUser object of the player to be mentioned.
     * @param sender The StringsUser object of the sender of the mention.
     */
    void mention(StringsUser subject, StringsUser sender);

    /**
     * Provides the Messenger instance
     * @return The Strings Messenger instance
     */
    Messenger getMessenger();

}

