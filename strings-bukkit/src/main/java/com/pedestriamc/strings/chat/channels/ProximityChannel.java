package com.pedestriamc.strings.chat.channels;

import com.pedestriamc.strings.api.channels.Buildable;
import com.pedestriamc.strings.api.channels.ChannelLoader;
import com.pedestriamc.strings.api.channels.Type;
import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.Membership;
import com.pedestriamc.strings.api.channels.data.ChannelData;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ProximityChannel extends AbstractChannel implements Buildable {

    private final Set<Player> members;
    private double distance;
    private final Set<World> worlds;

    @Deprecated
    public ProximityChannel(Strings strings, String name, String format, String defaultColor, ChannelLoader channelLoader, boolean callEvent, boolean doURLFilter, boolean doProfanityFilter, boolean doCooldown, double distance, Membership membership, int priority, World world){
        super(strings, channelLoader, name, defaultColor, format, membership, doCooldown, doProfanityFilter, doURLFilter, callEvent, priority);
        this.distance = distance;
        this.members = ConcurrentHashMap.newKeySet();
        this.worlds = new HashSet<>();
        worlds.add(world);
    }

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

    }


    public @NotNull HashSet<Player> getRecipients(Player sender) {

        if(sender == null) {
            return defaultSet();
        }

        World senderWorld = sender.getWorld();
        if(!worlds.contains(senderWorld)){
            return defaultSet();
        }

        HashSet<Player> recipients = new HashSet<>(members);

        Location senderLocation = sender.getLocation();
        for(Player p : senderWorld.getPlayers()){
            Location pLocation = p.getLocation();
            if(senderLocation.distance(pLocation) < distance){
                recipients.add(p);
            }else if(p.hasPermission("strings.channels." + getName() + ".receive")){
                recipients.add(p);
            }
        }

        return recipients;

    }

    private HashSet<Player> defaultSet() {
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
        map.put("format", this.getFormat());
        map.put("default-color", this.getDefaultColor());
        map.put("call-event", String.valueOf(this.isCallEvent()));
        map.put("filter-profanity", String.valueOf(this.doProfanityFilter()));
        map.put("block-urls", String.valueOf(this.doUrlFilter()));
        map.put("cooldown", String.valueOf(this.doCooldown()));
        map.put("type", String.valueOf(this.getType()));
        map.put("membership", String.valueOf(this.getMembership()));
        map.put("distance", String.valueOf(distance));
        map.put("priority", String.valueOf(this.getPriority()));
        map.put("worlds", getWorldNames());
        return map;
    }

    @SuppressWarnings("unused")
    public double getProximity(){
        return distance;
    }

    @SuppressWarnings("unused")
    public void setProximity(double proximity){
        this.distance = proximity;
    }

    public Set<World> getWorlds() {
        return new HashSet<>(worlds);
    }

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

}
