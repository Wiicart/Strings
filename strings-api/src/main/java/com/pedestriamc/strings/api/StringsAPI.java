package com.pedestriamc.strings.api;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * The API for Strings
 */
@SuppressWarnings("unused")
public interface StringsAPI {

    /**
     * Provides a short with the plugin's version number.
     * @return A short with the version.
     */
    short getVersion();

    /**
     * Provides a Set of all registered Channels.
     * @return A populated Set.
     */
    Set<StringsChannel> getChannels();

    /**
     * Gets the Channel with the specified name, if it exists.
     * @param name The name of the Channel to search for.
     * @return The Channel, if it exists.
     */
    @Deprecated
    Optional<StringsChannel> getOptionalChannel(String name);

    /**
     * Provides a StringsChannel based off its name, if it exists.
     * @param name The name to search under.
     * @return A StringsChannel, if it exists.
     */
    @Nullable
    StringsChannel getChannel(String name);

    /**
     * Provides an Optional of StringsUser based on UUID
     * @param uuid The UUID of the Player.
     * @return An Optional containing a StringsUser if the StringsUser exists.
     */
    @Deprecated
    Optional<StringsUser> getOptionalStringsUser(UUID uuid);

    /**
     * Provides a StringsUser based off a UUID, if it exists.
     * @param uuid The UUID to search under.
     * @return A StringsUser, if it exists.
     */
    @Nullable
    StringsUser getStringsUser(UUID uuid);

    /**
     * Provides an Array of StringsChannels that are for a specific world.
     * @param world The World to search with.
     * @return An Array of StringsChannels.
     */
    StringsChannel[] getWorldChannels(World world);

    /**
     * Deletes a StringsChannel and removes it from the channels file.
     * This is permanent and cannot be recovered!
     * As a global channel must exist, this method will not delete the global channel.
     * @param channel The StringsChannel to be deleted.
     */
    void deleteChannel(StringsChannel channel);

    /**
     * Returns true if the server is running Paper or a fork.
     * @return If the server is Paper.
     */
    boolean isPaper();

    /**
     * Determines if a Player is on chat cool-down.
     * Some channels ignore this, and if the Player has the right permission, this is ignored.
     * @param player The Player to check.
     * @return If the player is on cool-down.
     */
    boolean isOnCooldown(Player player);

    /**
     * Starts a chat cool-down for a player.
     * @param player The player to start the cool-down on.
     */
    void startCooldown(Player player);

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
}

