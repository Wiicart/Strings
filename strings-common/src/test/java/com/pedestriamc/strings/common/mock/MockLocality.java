package com.pedestriamc.strings.common.mock;

import com.pedestriamc.strings.api.channel.local.Locality;
import com.pedestriamc.strings.api.user.StringsUser;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public class MockLocality implements Locality<MockLocality> {

    private final Set<StringsUser> users;

    public MockLocality() {
        users = new HashSet<>();
    }

    public MockLocality(Set<StringsUser> users) {
        this.users = users;
    }

    public MockLocality(StringsUser... users) {
        this(Set.of(users));
    }

    @Override
    public @NotNull MockLocality get() {
        return this;
    }

    @Override
    public @NotNull String getName() {
        return "MockLocality";
    }

    @Override
    public boolean contains(@NotNull StringsUser user) {
        return users.contains(user);
    }

    @Override
    public @NotNull Set<StringsUser> getUsers() {
        return users;
    }
}
