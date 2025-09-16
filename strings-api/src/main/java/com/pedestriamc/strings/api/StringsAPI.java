package com.pedestriamc.strings.api;

import com.pedestriamc.strings.api.channel.ChannelLoader;
import com.pedestriamc.strings.api.discord.StringsDiscord;
import com.pedestriamc.strings.api.message.Messenger;
import com.pedestriamc.strings.api.moderation.StringsModeration;
import com.pedestriamc.strings.api.settings.Settings;
import com.pedestriamc.strings.api.text.EmojiManager;
import com.pedestriamc.strings.api.user.StringsUser;
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
    StringsUser getUser(@NotNull UUID uuid);

    /**
     * Saves a {@link StringsUser}.
     * Modifications to Users do not persist by default, you must save it to persist.
     * @param user The user to be saved.
     */
    void saveUser(@NotNull StringsUser user);

    /**
     * Returns true if the server is running Paper or a fork.
     * @return If the server is Paper.
     */
    boolean isPaper();

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
     * @param user The Player to mention
     * @param message The message to display in the Player's actionbar.
     */
    void sendMention(@NotNull StringsUser user, @NotNull String message);

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
     * Provides a {@link EmojiManager}.<br/>
     * An instance is not always present by default,
     * so an instance may need to be created.
     * @return A EmojiManager
     */
    @NotNull EmojiManager emojiManager();

    /**
     * Provides the StringsModeration instance, offering some moderation methods.
     * @throws IllegalStateException Check if StringsModeration is available by checking if the plugin is enabled.
     * @return The StringsModeration implementation.
     */
    default StringsModeration getModeration() {
        return StringsProvider.getModeration();
    }

    /**
     * Provides the {@link StringsDiscord} instance
     * Only available after onEnable
     * @throws IllegalStateException If not available.
     * @return The StringsDiscord instance
     */
    default StringsDiscord getDiscord() { return StringsProvider.getDiscord(); }

}

