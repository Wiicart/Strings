package com.pedestriamc.strings.api.user;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Set;
import java.util.UUID;

public interface UserManager {

    /**
     * Gets the {@link StringsUser} correlated to the UUID, if online
     * @param uuid The UUID of the StringsUser
     * @return A StringsUser
     */
    @Nullable
    StringsUser getUser(@NotNull UUID uuid);

    /**
     * Saves a StringsUser
     * @param user the User to save
     */
    void saveUser(@NotNull StringsUser user);

    /**
     * Provides a Set containing all online {@link StringsUser}(s).
     * @return An unmodifiable Set
     */
    @UnmodifiableView
    @NotNull
    Set<StringsUser> getUsers();

}
