package com.pedestriamc.strings.chat.channels;

import com.pedestriamc.strings.api.Type;
import com.pedestriamc.strings.User;
import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.Membership;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class WorldChannel extends AbstractChannel{

    private final Set<Player> members;
    private final World world;

    public WorldChannel(Strings strings, String name, String format, String defaultColor, ChannelManager channelManager, boolean callEvent, boolean doURLFilter, boolean doProfanityFilter, boolean doCooldown, World world, Membership membership, int priority){
        super(strings, channelManager, name, defaultColor, format, membership, doCooldown, doProfanityFilter, doURLFilter,callEvent, priority);
        this.members = ConcurrentHashMap.newKeySet();
        this.world = world;
        channelManager.registerChannel(this);
    }


    @Override
    public Set<Player> getRecipients(Player sender){
        HashSet<Player> recipients = new HashSet<>(world.getPlayers());
        recipients.addAll(members);
        for(Player p : Bukkit.getOnlinePlayers()){
            if(p.hasPermission("strings.channels." + this.getName() + ".receive")){
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
    public void addPlayer(User user){
        this.addPlayer(user.getPlayer());
    }

    @Override
    public void removePlayer(Player player) {
        members.remove(player);
    }

    @Override
    public void removePlayer(User user){
        this.removePlayer(user.getPlayer());
    }

    @Override
    public Set<Player> getMembers() {
        return new HashSet<>(members);
    }

    @Override
    public Type getType() {
        return Type.WORLD;
    }

    public World getWorld(){
        return world;
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
        map.put("priority", String.valueOf(this.getPriority()));
        map.put("world", world.getName());
        return map;
    }

}
