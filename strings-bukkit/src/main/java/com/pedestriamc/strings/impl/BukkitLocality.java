package com.pedestriamc.strings.impl;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.channel.local.Locality;
import com.pedestriamc.strings.api.platform.Platform;
import com.pedestriamc.strings.api.user.StringsUser;
import com.pedestriamc.strings.user.User;
import com.pedestriamc.strings.user.UserUtil;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("unused")
public class BukkitLocality implements Locality {

    private final World world;
    private final UserUtil userUtil;

    public BukkitLocality(@NotNull Strings strings, @NotNull World world) {
        this.world = world;
        userUtil = strings.getUserUtil();
    }

    @Nullable
    public static BukkitLocality of(Locality locality) {
        if(locality instanceof BukkitLocality bukkitLocality) {
            return bukkitLocality;
        }
        return null;
    }

    @Override
    public boolean contains(@NotNull StringsUser user) {
        User u = User.of(user);
        if(u == null) {
            return false;
        }
        return world.getPlayers().contains(u.getPlayer());
    }

    @Override
    public Set<StringsUser> getUsers() {
        Set<StringsUser> set = new HashSet<>();
        for(Player p : world.getPlayers()) {
            set.add(userUtil.getUser(p));
        }
        return set;
    }

    @Override
    public Platform getPlatform() {
        return Platform.BUKKIT;
    }

    public World getWorld() {
        return world;
    }

}
