package com.pedestriamc.strings.api;

import org.bukkit.entity.Player;

/**
 * Provides methods to interact with the StringsModeration module.
 */
@SuppressWarnings("unused")
public interface StringsModeration {

    /**
     * Checks if a Player is currently on a chat cooldown.
     * @param player The Player to check.
     * @return A boolean containing if the Player is on cooldown.
     */
    boolean isOnCooldown(final Player player);

    /**
     * Starts a chat cooldown for a Player
     * @param player The Player to start the cooldown on.
     */
    void startCooldown(final Player player);

    /**
     * Checks if the last message the player sent was the same.
     * @param player The sender
     * @param message The message
     * @return A boolean containing the value of if the last message is a duplicate.
     */
    boolean isRepeating(final Player player, final String message);

}
