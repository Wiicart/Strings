package com.pedestriamc.strings.channel.local;

import com.pedestriamc.strings.api.channel.Type;
import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.channel.Membership;
import com.pedestriamc.strings.api.channel.data.ChannelBuilder;
import com.pedestriamc.strings.api.channel.local.LocalChannel;
import com.pedestriamc.strings.channel.DefaultChannel;
import com.pedestriamc.strings.channel.base.AbstractChannel;
import org.bukkit.Bukkit;
import org.bukkit.Location;
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
 * Channel implementation that sends messages based off players in proximity in the sender's {@link World}.
 * Worlds this Channel is effective in must be defined.
 * Expected to be used with the {@link DefaultChannel}
 */
public class ProximityChannel extends AbstractChannel implements LocalChannel {

    private double distance;

    // for more efficient calculations.
    private double distanceSquared;

    private final Set<World> worlds;

    public ProximityChannel(JavaPlugin plugin, ChannelBuilder builder) {
        this((Strings) plugin, builder);
    }

    public ProximityChannel(@NotNull Strings strings, @NotNull ChannelBuilder builder) {
        super(strings, builder);
        worlds = new HashSet<>(builder.getWorlds());
        distance = builder.getDistance();
        distanceSquared = distance * distance;
    }

    public @NotNull Set<Player> getRecipients(@NotNull Player sender) {
        Set<Player> members = getMembers();
        if(members.contains(sender)) {
            return universalSet();
        }

        World senderWorld = sender.getWorld();
        HashSet<Player> recipients = new HashSet<>(members);

        Location senderLocation = sender.getLocation();
        for(Player p : senderWorld.getPlayers()) {
            Location pLocation = p.getLocation();
            if(senderLocation.distanceSquared(pLocation) < distanceSquared) {
                recipients.add(p);
            }
        }

        for(Player p : Bukkit.getOnlinePlayers()) {
            if(p.hasPermission(CHANNEL_PERMISSION + getName() + ".receive")) {
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

    protected double getDistanceSquared() {
        return distanceSquared;
    }

    @Override
    public @NotNull Type getType() {
        return Type.PROXIMITY;
    }

    @Override
    public Map<String, Object> getData() {
        Map<String, Object> map = super.getData();
        map.put("distance", String.valueOf(distance));
        map.put("worlds", getWorldNames());
        return map;
    }

    @Override
    public double getProximity() {
        return distance;
    }

    @Override
    public void setProximity(double proximity) {
        this.distance = proximity;
        distanceSquared = distance * distance;
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

    @Override
    public @NotNull String getIdentifier() {
        return "proximitychannel";
    }

}
