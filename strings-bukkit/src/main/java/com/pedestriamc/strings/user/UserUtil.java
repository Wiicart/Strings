package com.pedestriamc.strings.user;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.UUID;

@SuppressWarnings("unused")
public interface UserUtil {

    /**
     * Saves a User to either a file or database.
     * @param user The User to be saved.
     */
    void saveUser(@NotNull User user);

    /**
     * Loads a User either from a file or database.
     * Does not save the User to the Map<User>
     * @param uuid The UUID of the User to load.
     * @return The User if present, otherwise null.
     */
    @Nullable
    User loadUser(UUID uuid);

    User getUser(UUID uuid);

    User getUser(Player player);

    void addUser(User user);

    void removeUser(UUID uuid);

    Set<User> getUsers();
    
}
