package com.pedestriamc.strings.user.util;

import com.pedestriamc.strings.user.User;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Loads and stores all Users for Strings
 */
public sealed interface UserUtil permits YamlUserUtil {

    /**
     * Saves a User to either a file or database.
     * @param user The User to be saved.
     */
    void saveUser(@NotNull User user);

    /**
     * Synchronously loads a User from a file or database.
     * Automatically saves the User to the Map
     * @param uuid The UUID of the User to load.
     * @return The User if present, otherwise null.
     */
    @Nullable
    @SuppressWarnings("UnusedReturnValue")
    User loadUser(UUID uuid);

    /**
     * Asynchronously loads a User from a file or database.
     * Automatically saves the User to the Map
     * @param uuid the UUID of the User to load
     * @return A CompletableFuture<User>
     */
    @SuppressWarnings("UnusedReturnValue")
    CompletableFuture<User> loadUserAsync(@NotNull UUID uuid);

    /**
     * Provides a User base off UUID. If there is no record of the User, a new User object will be returned.
     * @param uuid The UUID of the Player
     * @return A User
     */
    @NotNull
    User getUser(UUID uuid);

    /**
     * Provides a User base off Player. If there is no record of the User, a new User object will be returned.
     * @param player The Player
     * @return A User
     */
    @NotNull
    User getUser(Player player);

    void addUser(User user);

    void removeUser(UUID uuid);

    Set<User> getUsers();
    
}
