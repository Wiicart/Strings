package com.pedestriamc.strings.bukkit.locality;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.channel.local.Locality;
import com.pedestriamc.strings.api.user.StringsUser;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.stream.Collectors;

class BukkitLocality implements Locality<World> {

    private final Strings strings;

    private final World world;

    BukkitLocality(@NotNull Strings strings, @NotNull World world) {
        this.strings = strings;
        this.world = world;
    }

    @Override
    public @NotNull World get() {
        return world;
    }

    @Override
    public @NotNull String getName() {
        return world.getName();
    }

    @Override
    public boolean contains(@NotNull StringsUser user) {
        return user.getLocality().equals(this);
    }

    @Override
    public @NotNull Set<StringsUser> getUsers() {
        return strings.users().getUsers().stream()
                .filter(user -> user.getLocality().equals(this))
                .collect(Collectors.toSet());
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Locality<?> l && l.get().equals(this.get());
    }

    @Override
    public int hashCode() {
        return get().hashCode();
    }
}
