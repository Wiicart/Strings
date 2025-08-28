package com.pedestriamc.strings.api.moderation;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Provides methods to interact with the StringsModeration module.
 */
@SuppressWarnings("unused")
public interface StringsModeration {

    /**
     * Checks if a Player is currently on a chat cooldown.
     * @param user The Player to check.
     * @return A boolean containing if the Player is on cooldown.
     */
    boolean isOnCooldown(@NotNull Player user);

    /**
     * Starts a chat cooldown for a Player
     * @param user The Player to start the cooldown on.
     */
    void startCooldown(@NotNull Player user);

    /**
     * Checks if the last message the player sent was the same.
     * @param user The sender
     * @param message The message
     * @return A boolean containing the value of if the last message is a duplicate.
     */
    boolean isRepeating(@NotNull Player user, @NotNull String message);

    /**
     * Provides the {@link ModerationSettings} instance
     * @return A ModerationSettings
     */
    @NotNull
    ModerationSettings getSettings();

}
