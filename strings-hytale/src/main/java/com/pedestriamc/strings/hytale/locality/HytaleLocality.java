package com.pedestriamc.strings.hytale.locality;

import com.hypixel.hytale.server.core.universe.world.World;
import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.channel.local.Locality;
import com.pedestriamc.strings.api.user.StringsUser;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.stream.Collectors;

class HytaleLocality implements Locality<World> {

    private final Strings strings;
    private final World world;

    HytaleLocality(@NotNull Strings strings, @NotNull World world) {
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
        return getUsers().contains(user);
    }

    @Override
    public @NotNull Set<StringsUser> getUsers() {
        return world.getPlayerRefs().stream()
                .map(player -> strings.users().getUser(player.getUuid()))
                .collect(Collectors.toSet());
    }


}
