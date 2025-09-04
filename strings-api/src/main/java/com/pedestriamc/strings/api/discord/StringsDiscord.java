package com.pedestriamc.strings.api.discord;

import com.pedestriamc.strings.api.StringsProvider;
import net.kyori.adventure.chat.SignedMessage;
import org.jetbrains.annotations.NotNull;

/**
 * Public API for the StringsDiscord module.<br/>
 * Get instance from the {@link StringsProvider#getDiscord()}
 */
public interface StringsDiscord {

    /**
     * Provides the {@link DiscordSettings} instance.
     * @return The Settings instance
     */
    @NotNull
    DiscordSettings getSettings();

    /**
     * Sends a String message to all channels the bot is configured to use.
     * @param message The message to send
     */
    void broadcastDiscordMessage(@NotNull String message);

    /**
     * Deletes a Discord message corresponding to a {@link SignedMessage}.<br/>
     * This only works for the most recent 100 messages.
     * If not possible, this will silently fail.
     * @param message The SignedMessage representing what message to delete.
     */
    void deleteMessage(@NotNull SignedMessage message);

}
