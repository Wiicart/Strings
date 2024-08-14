package com.pedestriamc.strings.api;

import org.bukkit.World;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * The API for Strings
 */
public interface StringsAPI {

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
    Optional<StringsChannel> getChannel(String name);

    /**
     * Provides an Optional of StringsUser based on UUID
     * @param uuid The UUID of the Player.
     * @return An Optional containing a StringsUser if the StringsUser exists.
     */
    Optional<StringsUser> getStringsUser(UUID uuid);

    /**
     * Provides the WorldChannel for a World, if it exists.
     * @param world The World to search with.
     * @return An Optional that contains the StringsChannel if it exists.
     */
    Optional<StringsChannel> getChannel(World world);

    /**
     * Provides a short with the plugin's version number.
     * @return A short with the version.
     */
    short getVersion();
}

