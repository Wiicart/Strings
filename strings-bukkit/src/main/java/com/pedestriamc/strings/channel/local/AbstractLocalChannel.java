package com.pedestriamc.strings.channel.local;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.channel.Membership;
import com.pedestriamc.strings.api.channel.data.ChannelBuilder;
import com.pedestriamc.strings.api.channel.local.LocalChannel;
import com.pedestriamc.strings.channel.base.AbstractChannel;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permissible;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Class that implements common LocalChannel elements
 */
abstract class AbstractLocalChannel extends AbstractChannel implements LocalChannel {

    private Set<World> worlds;

    protected AbstractLocalChannel(@NotNull Strings strings, @NotNull ChannelBuilder data) {
        super(strings, data);
        worlds = new HashSet<>(data.getWorlds());
        if(worlds.isEmpty()) {
            throw new IllegalArgumentException("Worlds cannot be empty");
        }
    }

    @Override
    public boolean containsInScope(@NotNull Player player) {
        return getWorlds().contains(player.getWorld());
    }

    @Override
    public Set<World> getWorlds() {
        return new HashSet<>(worlds);
    }

    @Override
    public void setWorlds(@NotNull Set<World> worlds) {
        this.worlds = new HashSet<>(worlds);
    }

    @Override
    public boolean allows(@NotNull Permissible permissible) {
        if(permissible instanceof Player player &&
                getMembership() == Membership.DEFAULT && worlds.contains(player.getWorld())) {
            return true;
        }
        return super.allows(permissible);
    }

    @Override
    public Map<String, Object> getData() {
        Map<String, Object> map = super.getData();
        map.put("worlds", getWorldNames());
        return map;
    }

    @Override
    public @NotNull Set<Player> getPlayersInScope() {
        return switch(getMembership()) {
            case DEFAULT -> universalSet();
            case PERMISSION -> {
                HashSet<Player> scoped = new HashSet<>(universalSet());
                scoped.removeIf(p -> !allows(p));
                yield scoped;
            }
            default -> {
                HashSet<Player> scoped = new HashSet<>(getMembers());
                scoped.addAll(getMonitors());
                yield scoped;
            }
        };
    }

    protected List<String> getWorldNames() {
        ArrayList<String> worldNames = new ArrayList<>();
        for (World w : getWorlds()) {
            worldNames.add(w.getName());
        }
        return worldNames;
    }

    /**
     * Provides a Set of all Players that can be eligible to receive messages.
     * Intended for determining recipients for an admin.
     * @return A populated Set
     */
    protected Set<Player> universalSet() {
        HashSet<Player> set = new HashSet<>(getMembers());
        set.addAll(getMonitors());
        for(World w : worlds) {
            set.addAll(w.getPlayers());
        }
        return set;
    }
}


