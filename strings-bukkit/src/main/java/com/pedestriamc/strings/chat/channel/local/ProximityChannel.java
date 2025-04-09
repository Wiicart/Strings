package com.pedestriamc.strings.chat.channel.local;

import com.pedestriamc.strings.api.channel.Type;
import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.channel.Membership;
import com.pedestriamc.strings.api.channel.LocalChannel;
import com.pedestriamc.strings.api.channel.data.ChannelData;
import com.pedestriamc.strings.chat.channel.base.AbstractChannel;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permissible;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ProximityChannel extends AbstractChannel implements LocalChannel {

    private double distance;
    private double distanceSquared;
    private final Set<World> worlds;

    public ProximityChannel(Strings strings, ChannelData data) {
        super(
                strings,
                strings.getChannelLoader(),
                data.getName(),
                data.getDefaultColor(),
                data.getFormat(),
                data.getMembership(),
                data.isDoCooldown(),
                data.isDoProfanityFilter(),
                data.isDoUrlFilter(),
                data.isCallEvent(),
                data.getPriority(),
                data.getBroadcastFormat()
        );
        this.worlds = new HashSet<>(data.getWorlds());
        this.distance = data.getDistance();
        distanceSquared = distance * distance;
    }

    public Set<Player> getRecipients(@NotNull Player sender) {
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
    public Set<Player> getPlayersInScope() {
        switch(getMembership()) {
            case DEFAULT -> {
                return universalSet();
            }
            case PERMISSION -> {
                HashSet<Player> scoped = new HashSet<>(universalSet());
                scoped.removeIf(p -> !allows(p));
                return scoped;
            }
            default -> {
                HashSet<Player> scoped = new HashSet<>(getMembers());
                scoped.addAll(getMonitors());
                return scoped;
            }
        }
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
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        map.put("format", getFormat());
        map.put("default-color", getDefaultColor());
        map.put("call-event", String.valueOf(isCallEvent()));
        map.put("filter-profanity", String.valueOf(doProfanityFilter()));
        map.put("block-urls", String.valueOf(doUrlFilter()));
        map.put("cooldown", String.valueOf(doCooldown()));
        map.put("type", String.valueOf(getType()));
        map.put("membership", String.valueOf(getMembership()));
        map.put("distance", String.valueOf(distance));
        map.put("priority", String.valueOf(getPriority()));
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

    @Override
    public List<String> getWorldNames() {
        ArrayList<String> worldNames = new ArrayList<>();
        for (World w : getWorlds()) {
            worldNames.add(w.getName());
        }

        return worldNames;
    }

    @Override
    public boolean allows(@NotNull Permissible permissible) {
        if(permissible instanceof Player player) {
            if(getMembers().contains(player)) {
                return true;
            }
            if(getMembership() == Membership.DEFAULT && worlds.contains(player.getWorld())) {
                return worlds.contains(player.getWorld());
            }
        }

        return (
                permissible.hasPermission(CHANNEL_PERMISSION + getName()) ||
                permissible.hasPermission(CHANNEL_PERMISSION + "*") ||
                permissible.hasPermission("strings.*")
        );
    }
}
