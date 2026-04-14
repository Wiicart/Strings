package com.pedestriamc.strings.user;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.user.StringsUser;
import com.pedestriamc.strings.api.user.UserManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class StringsUserManager implements UserManager {

    private final Strings strings;
    private final Map<UUID, StringsUser> map;
    private final Collection<StringsUser> users;

    public StringsUserManager(Strings strings) {
        this.strings = strings;
        map = new ConcurrentHashMap<>();
        users = Collections.unmodifiableCollection(map.values());
    }

    @Override
    public @NotNull StringsUser getUser(@NotNull UUID uuid) {
        return null;
    }

    @Override
    public void saveUser(@NotNull StringsUser user) {

    }

    @Override
    public @UnmodifiableView @NotNull Collection<StringsUser> getUsers() {
        return users;
    }

    public @NotNull CompletableFuture<StringsUser> loadUserAsync(@NotNull UUID uuid) {
        return null;
    }

    public @NotNull User loadUser(@NotNull UUID uuid) {
        return null;
    }

}
