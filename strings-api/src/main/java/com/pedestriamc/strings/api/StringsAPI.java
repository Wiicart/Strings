package com.pedestriamc.strings.api;

import com.pedestriamc.strings.api.channels.Channel;
import com.pedestriamc.strings.api.channels.ChannelLoader;
import com.pedestriamc.strings.api.message.Messenger;
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
     * Provides a Set of all registered Channels.
     * @return A populated Set.
     */
    Set<Channel> getChannels();

    /**
     * Gets the Channel with the specified name if it exists.
     * @param name The name of the Channel to search for.
     * @return The Channel, if it exists.
     */
    @Deprecated
    Optional<Channel> getOptionalChannel(String name);

    /**
     * Provides a StringsChannel based off its name, if it exists.
     * Deprecated - use ChannelLoader#getChannel() instead.
     * @param name The name to search under.
     * @return A StringsChannel, if it exists.
     */
    @Nullable
    @Deprecated
    Channel getChannel(String name);

    /**
     * Provides an Optional of StringsUser based on UUID
     * @param uuid The UUID of the Player.
     * @return An Optional containing a StringsUser if the StringsUser exists.
     */
    @Deprecated
    Optional<StringsUser> getOptionalStringsUser(UUID uuid);

    /**
     * Provides a StringsUser based off a UUID if it exists.
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
    Set<Channel> getWorldChannels(World world);

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

    /**
     * Provides the Messenger instance
     * @return The Strings Messenger instance
     */
    Messenger getMessenger();

}

