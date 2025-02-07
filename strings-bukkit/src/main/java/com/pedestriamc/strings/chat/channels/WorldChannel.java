package com.pedestriamc.strings.chat.channels;

import com.pedestriamc.strings.api.channels.Buildable;
import com.pedestriamc.strings.api.channels.Type;
import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.channels.Membership;
import com.pedestriamc.strings.api.channels.LocalChannel;
import com.pedestriamc.strings.api.channels.data.ChannelData;
import com.pedestriamc.strings.chat.channels.base.AbstractChannel;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class WorldChannel extends AbstractChannel implements Buildable, LocalChannel {

    private final Set<Player> members;
    private final Set<World> worlds;
    private final Set<Player> monitors;

    public WorldChannel(Strings strings, ChannelData data) {

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
        this.monitors = new HashSet<>();

    }



    @Override
    public Set<Player> getRecipients(Player sender) {
        HashSet<Player> recipients = new HashSet<>();

        for(World w : getWorlds()) {
            recipients.addAll(w.getPlayers());
        }

        recipients.addAll(members);
        for(Player p : Bukkit.getOnlinePlayers()) {
            if(p.hasPermission("strings.channels." + this.getName() + ".receive")) {
                recipients.add(p);
            }
        }
        return recipients;
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
        return Type.WORLD;
    }

    @Override
    public Map<String, Object> getData() {
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        map.put("format", this.getFormat());
        map.put("default-color", this.getDefaultColor());
        map.put("call-event", String.valueOf(this.isCallEvent()));
        map.put("filter-profanity", String.valueOf(this.doProfanityFilter()));
        map.put("block-urls", String.valueOf(this.doUrlFilter()));
        map.put("cooldown", String.valueOf(this.doCooldown()));
        map.put("type", String.valueOf(this.getType()));
        map.put("membership", String.valueOf(this.getMembership()));
        map.put("priority", String.valueOf(this.getPriority()));
        map.put("worlds", getWorldNames());
        return map;
    }

    @Override
    public Set<World> getWorlds(){
        return worlds;
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

    @Override
    public double getProximity() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("getProximity() called on WorldChannel instance, which is unsupported.");
    }

    @Override
    public void setProximity(double proximity) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("setProximity() called on WorldChannel instance, which is unsupported.");
    }

}

