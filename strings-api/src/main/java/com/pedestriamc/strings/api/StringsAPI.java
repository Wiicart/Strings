package com.pedestriamc.strings.api;

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
}
