package com.pedestriamc.strings.api.moderation;

import com.pedestriamc.strings.api.StringsProvider;
import com.pedestriamc.strings.api.user.StringsUser;
import org.jetbrains.annotations.NotNull;

/**
 * Provides methods to interact with the StringsModeration module.<br/>
 * Get instance with {@link StringsProvider#getModeration()}
 */
@SuppressWarnings("unused")
public interface StringsModeration {

    /**
     * Checks if a Player is currently on a chat cooldown.
     * @param user The Player to check.
     * @return A boolean containing if the Player is on cooldown.
     */
    boolean isOnCooldown(@NotNull StringsUser user);

    /**
     * Starts a chat cooldown for a Player
     * @param user The Player to start the cooldown on.
     */
    void startCooldown(@NotNull StringsUser user);

    /**
     * Checks if the last message the player sent was the same.
     * @param user The sender
     * @param message The message
     * @return A boolean containing the value of if the last message is a duplicate.
     */
    boolean isRepeating(@NotNull StringsUser user, @NotNull String message);

    /**
     * Provides the {@link ModerationSettings} instance
     * @return A ModerationSettings
     */
    @NotNull
    ModerationSettings getSettings();

}
