package com.pedestriamc.strings.channel.local;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.channel.Membership;
import com.pedestriamc.strings.api.channel.data.LocalChannelBuilder;
import com.pedestriamc.strings.api.channel.local.LocalChannel;
import com.pedestriamc.strings.api.channel.local.Locality;
import com.pedestriamc.strings.api.user.StringsUser;
import com.pedestriamc.strings.channel.base.BukkitChannel;
import com.pedestriamc.strings.user.User;
import com.pedestriamc.strings.user.util.UserUtil;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Class that implements common LocalChannel elements
 */
abstract class AbstractLocalChannel extends BukkitChannel implements LocalChannel<World> {

    private final UserUtil userUtil;

    private final Set<Locality<World>> worlds;

    protected AbstractLocalChannel(@NotNull Strings strings, @NotNull LocalChannelBuilder<?> data) {
        super(strings, data);
        userUtil = getUserUtil();
        try {
            @SuppressWarnings("unchecked")
            LocalChannelBuilder<World> builder = (LocalChannelBuilder<World>) data;
            worlds = new HashSet<>(builder.getWorlds());
        } catch (ClassCastException e) {
            throw new IllegalArgumentException("Invalid World implementation", e);
        }


        if(worlds.isEmpty()) {
            throw new IllegalArgumentException("Worlds cannot be empty");
        }
    }

    @Override
    public boolean containsInScope(@NotNull StringsUser user) {
        return containsWorld(User.of(user).getWorld());
    }

    @Override
    public Set<Locality<World>> getWorlds() {
        return new HashSet<>(worlds);
    }

    @Override
    public void setWorlds(@NotNull Set<Locality<World>> worlds) {
        this.worlds.clear();
        this.worlds.addAll(worlds);
    }

    @Override
    public boolean allows(@NotNull StringsUser user) {
        Player player = User.playerOf(user);
        return (getMembership() == Membership.DEFAULT && containsWorld(player.getWorld())) || super.allows(user);
    }

    @Override
    public Map<String, Object> getData() {
        Map<String, Object> map = super.getData();
        map.put("worlds", getWorldNames());
        return map;
    }

    @Override
    public @NotNull Set<StringsUser> getPlayersInScope() {
        return switch(getMembership()) {
            case DEFAULT -> universalSet();
            case PERMISSION -> {
                HashSet<StringsUser> scoped = new HashSet<>(universalSet());
                scoped.removeIf(user -> !allows(user));
                yield scoped;
            }
            default -> {
                HashSet<StringsUser> scoped = new HashSet<>(getMembers());
                scoped.addAll(getMonitors());
                yield scoped;
            }
        };
    }

    @Override
    public boolean containsWorld(@NotNull World world) {
        for (Locality<World> locality : worlds) {
            if (locality.get().equals(world)) {
                return true;
            }
        }

        return false;
    }

    protected List<String> getWorldNames() {
        ArrayList<String> worldNames = new ArrayList<>();
        for (Locality<World> w : getWorlds()) {
            worldNames.add(w.get().getName());
        }
        return worldNames;
    }

    protected @NotNull Set<User> convertToUsers(@NotNull Collection<Player> players) {
        return players.stream()
                .map(userUtil::getUser)
                .collect(Collectors.toSet());
    }

    /**
     * Provides a Set of all Players that can be eligible to receive messages.
     * Intended for determining recipients for an admin.
     * @return A populated Set
     */
    protected Set<StringsUser> universalSet() {
        HashSet<StringsUser> set = new HashSet<>(getMembers());
        set.addAll(getMonitors());

        for(Locality<World> w : worlds) {
            set.addAll(
                    w.get()
                    .getPlayers()
                    .stream()
                    .map(getUserUtil()::getUser)
                    .collect(Collectors.toSet())
            );
        }

        return set;
    }
}


