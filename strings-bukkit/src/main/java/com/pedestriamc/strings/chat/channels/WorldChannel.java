package com.pedestriamc.strings.chat.channels;

import com.pedestriamc.strings.api.channels.Buildable;
import com.pedestriamc.strings.api.channels.ChannelLoader;
import com.pedestriamc.strings.api.channels.Type;
import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.Membership;
import com.pedestriamc.strings.api.channels.data.ChannelData;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class WorldChannel extends AbstractChannel implements Buildable {

    private final Set<Player> members;
    private World world;
    private Set<World> worlds;

    @Deprecated
    public WorldChannel(Strings strings, String name, String format, String defaultColor, ChannelLoader channelLoader, boolean callEvent, boolean doURLFilter, boolean doProfanityFilter, boolean doCooldown, World world, Membership membership, int priority) {
        super(strings, channelLoader, name, defaultColor, format, membership, doCooldown, doProfanityFilter, doURLFilter,callEvent, priority);
        this.members = ConcurrentHashMap.newKeySet();
        this.world = world;
    }

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

    public Set<World> getWorlds(){
        return worlds;
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
