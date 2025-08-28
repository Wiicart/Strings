package com.pedestriamc.strings.api;

import com.pedestriamc.strings.api.channel.ChannelLoader;
import com.pedestriamc.strings.api.message.Messenger;
import com.pedestriamc.strings.api.moderation.StringsModeration;
import com.pedestriamc.strings.api.settings.Settings;
import com.pedestriamc.strings.api.user.StringsUser;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * The main API class for Strings
 */
@SuppressWarnings("unused")
public interface StringsAPI {

    /**
     * Provides the {@link ChannelLoader} instance.
     * @return The ChannelLoader.
     */
    @NotNull ChannelLoader getChannelLoader();

    /**
     * Provides a short with the plugin's version number.
     * @return A short with the version.
     */
    short getVersion();

    /**
     * Provides a {@link StringsUser} based off a UUID if it exists.
     * @param uuid The UUID to search under.
     * @return A StringsUser, if it exists.
     */
    @Nullable
    StringsUser getStringsUser(@NotNull UUID uuid);

    /**
     * Provides a {@link StringsUser} based off a Player if it exists.
     * @param player The Player to search under.
     * @return A StringsUser, if it exists.
     */
    @Nullable
    StringsUser getStringsUser(Player player);

    /**
     * Saves a {@link StringsUser}.
     * Modifications to Users do not persist by default, you must save it to persist.
     * @param user The user to be saved.
     */
    void saveStringsUser(@NotNull StringsUser user);

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
    void mention(@NotNull Player subject, @NotNull  Player sender);

    /**
     * Mentions a Player.
     * Uses the format found in the Strings config.yml
     * @param subject The StringsUser object of the player to be mentioned.
     * @param sender The StringsUser object of the sender of the mention.
     */
    void mention(@NotNull StringsUser subject, @NotNull StringsUser sender);

    /**
     * Mentions a Player.
     * Sends a message to the Player's action bar, and plays the mention Sound.
     *
     * @param player The Player to mention
     * @param message The message to display in the Player's actionbar.
     */
    void sendMention(@NotNull Player player, @NotNull String message);

    /**
     * Provides the {@link Messenger} instance
     * @return The Strings Messenger instance
     */
    @NotNull Messenger getMessenger();

    /**
     * Provides the {@link Settings} instance
     * @return The Settings instance
     */
    @NotNull Settings getSettings();

    /**
     * Provides the StringsModeration instance, offering some moderation methods.
     * @throws IllegalStateException Check if StringsModeration is available by checking if the plugin is enabled.
     * @return The StringsModeration implementation.
     */
    default StringsModeration getModeration() {
        return StringsProvider.getModeration();
    }

}

