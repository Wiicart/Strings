package com.pedestriamc.strings.channel.local;

import com.pedestriamc.strings.api.channel.Type;
import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.channel.Membership;
import com.pedestriamc.strings.api.channel.local.LocalChannel;
import com.pedestriamc.strings.api.channel.data.ChannelBuilder;
import com.pedestriamc.strings.channel.DefaultChannel;
import com.pedestriamc.strings.channel.base.AbstractChannel;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permissible;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Channel implementation that focuses on one or more {@link World}(s) on the server.
 * Expected to be used with the {@link DefaultChannel}
 */
public class WorldChannel extends AbstractChannel implements LocalChannel {

    private final Set<World> worlds;

    public WorldChannel(JavaPlugin plugin, ChannelBuilder builder) {
        this((Strings) plugin, builder);
    }

    public WorldChannel(@NotNull Strings strings, @NotNull ChannelBuilder builder) {
        super(strings, builder);
        worlds = new HashSet<>(builder.getWorlds());
    }

    @Override
    public @NotNull Set<Player> getRecipients(@NotNull Player sender) {
        HashSet<Player> recipients = new HashSet<>();

        for(World w : getWorlds()) {
            recipients.addAll(w.getPlayers());
        }

        recipients.addAll(getMembers());
        for(Player p : Bukkit.getOnlinePlayers()) {
            if(p.hasPermission("strings.channels." + this.getName() + ".receive")) {
                recipients.add(p);
            }
        }
        
        return recipients;
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

    @Override
    public @NotNull Type getType() {
        return Type.WORLD;
    }

    @Override
    public Map<String, Object> getData() {
        Map<String, Object> map = super.getData();
        map.put("worlds", getWorldNames());
        return map;
    }

    @Override
    public Set<World> getWorlds() {
        return new HashSet<>(worlds);
    }

    public List<String> getWorldNames() {
        ArrayList<String> worldNames = new ArrayList<>();
        for (World w : getWorlds()) {
            worldNames.add(w.getName());
        }
        return worldNames;
    }

    @Override
    public boolean allows(@NotNull Permissible permissible) {
        if(permissible instanceof Player player &&
                getMembership() == Membership.DEFAULT && worlds.contains(player.getWorld())) {
            return true;
        }
        return super.allows(permissible);
    }

    protected Set<Player> universalSet() {
        HashSet<Player> set = new HashSet<>(getMembers());
        set.addAll(getMonitors());
        for(World w : worlds) {
            set.addAll(w.getPlayers());
        }
        return set;
    }

    // N/A to this implementation, UnsupportedOperationException always thrown.
    @Override
    public double getProximity() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("getProximity() called on WorldChannel instance, which is unsupported.");
    }

    // N/A to this implementation, UnsupportedOperationException always thrown.
    @Override
    public void setProximity(double proximity) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("setProximity() called on WorldChannel instance, which is unsupported.");
    }

    @Override
    public @NotNull String getIdentifier() {
        return "worldchannel";
    }

}

