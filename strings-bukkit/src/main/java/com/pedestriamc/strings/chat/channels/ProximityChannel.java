package com.pedestriamc.strings.chat.channels;

import com.pedestriamc.strings.api.Type;
import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.Membership;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ProximityChannel extends AbstractChannel{

    private final Set<Player> members;
    private double distance;
    private final World world;

    public ProximityChannel(Strings strings, String name, String format, String defaultColor, ChannelManager channelManager, boolean callEvent, boolean doURLFilter, boolean doProfanityFilter, boolean doCooldown, double distance, Membership membership, int priority, World world){
        super(strings, channelManager, name, defaultColor, format, membership, doCooldown, doProfanityFilter, doURLFilter, callEvent, priority);
        this.distance = distance;
        this.members = ConcurrentHashMap.newKeySet();
        this.world = world;
        channelManager.registerChannel(this);
    }


    public @NotNull HashSet<Player> getRecipients(Player sender){
        HashSet<Player> recipients = new HashSet<>(members);

        if(sender == null || !sender.getWorld().equals(world)){
            return new HashSet<>(world.getPlayers());
        }

        Location senderLocation = sender.getLocation();
        for(Player p : world.getPlayers()){
            Location pLocation = p.getLocation();
            if(senderLocation.distance(pLocation) < distance){
                recipients.add(p);
            }else if(p.hasPermission("strings.channels." + this.getName() + ".receive")){
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
        return Type.PROXIMITY;
    }

    @Override
    public Map<String, String> getData() {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
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
        map.put("world", world.getName());
        return map;
    }

    public double getProximity(){
        return distance;
    }

    public void setProximity(double proximity){
        this.distance = proximity;
    }

    public World getWorld(){
        return this.world;
    }

}
