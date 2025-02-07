package com.pedestriamc.strings.chat.channels;

import com.pedestriamc.strings.api.channels.Buildable;
import com.pedestriamc.strings.api.channels.Type;
import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.channels.Membership;
import com.pedestriamc.strings.api.channels.LocalChannel;
import com.pedestriamc.strings.api.channels.data.ChannelData;
import com.pedestriamc.strings.chat.channels.base.AbstractChannel;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ProximityChannel extends AbstractChannel implements Buildable, LocalChannel {

    private final Set<Player> members;
    private double distance;
    private final Set<World> worlds;
    private final Set<Player> monitors;

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
                data.getPriority()
        );

        this.members = ConcurrentHashMap.newKeySet();
        this.worlds = data.getWorlds();
        this.distance = data.getDistance();
        this.monitors = new HashSet<>();

    }


    public Set<Player> getRecipients(Player sender) {

        if(sender == null) {
            return defaultSet();
        }

        // WORK OUT MESSAGES FOR MEMBERS OF THE CHANNEL. REVISIT WORLDCHANNEL TOO

        if(members.contains(sender)) {
            return defaultSet();
        }

        World senderWorld = sender.getWorld();
        HashSet<Player> recipients = new HashSet<>(members);

        Location senderLocation = sender.getLocation();
        for(Player p : senderWorld.getPlayers()){
            Location pLocation = p.getLocation();
            if(senderLocation.distance(pLocation) < distance) {
                recipients.add(p);
            }
        }

        for(Player p : Bukkit.getOnlinePlayers()) {
            if(p.hasPermission("strings.channels." + getName() + ".receive")) {
                recipients.add(p);
            }
        }

        return recipients;

    }

    protected HashSet<Player> defaultSet() {
        HashSet<Player> set = new HashSet<>(members);
        for(World w : worlds){
            set.addAll(w.getPlayers());
        }
        return set;
    }

    @Override
    public void addPlayer(Player player) {
        members.add(player);
    }

    @Override
    public void removePlayer(Player player) {
        members.remove(player);
    }

    @Override
    public Set<Player> getMembers() {
        return new HashSet<>(members);
    }

    @Override
    public Type getType() {
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
    }

    @Override
    public Set<World> getWorlds() {
        return new HashSet<>(worlds);
    }

    @Override
    public List<String> getWorldNames() {
        ArrayList<String> worlds = new ArrayList<>();
        for (World w : getWorlds() ){
            worlds.add(w.getName());
        }

        return worlds;
    }

    @Override
    public boolean allows(Player player) {

        if(getMembers().contains(player)) {
            return true;
        }

        if(getMembership() == Membership.DEFAULT) {
            return worlds.contains(player.getWorld());
        }

        return (
                player.hasPermission("strings.channels." + getName()) ||
                player.hasPermission("strings.channels.*") ||
                player.hasPermission("strings.*")
        );

    }

    @Override
    public void addMonitor(Player player) {
        monitors.add(player);
    }

    @Override
    public void removeMonitor(Player player) {
        monitors.remove(player);
    }

    @Override
    public Set<Player> getMonitors() {
        return new HashSet<>(monitors);
    }
}
