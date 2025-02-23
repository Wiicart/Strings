package com.pedestriamc.strings.chat.channel.local;

import com.pedestriamc.strings.api.channels.Buildable;
import com.pedestriamc.strings.api.channels.Type;
import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.channels.Membership;
import com.pedestriamc.strings.api.channels.LocalChannel;
import com.pedestriamc.strings.api.channels.data.ChannelData;
import com.pedestriamc.strings.chat.channel.base.AbstractChannel;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permissible;

import java.util.*;

public class WorldChannel extends AbstractChannel implements Buildable, LocalChannel {

    private final Set<World> worlds;

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
                data.getPriority(),
                data.getBroadcastFormat()
        );
        this.worlds = data.getWorlds();
    }

    @Override
    public Set<Player> getRecipients(Player sender) {
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
    public Set<Player> getPlayersInScope() {
        switch(getMembership()) {
            case DEFAULT -> {
                return defaultSet();
            }
            case PERMISSION -> {
                HashSet<Player> scoped = new HashSet<>(defaultSet());
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
        ArrayList<String> worldNames = new ArrayList<>();
        for (World w : getWorlds() ){
            worldNames.add(w.getName());
        }

        return worldNames;
    }

    @Override
    public boolean allows(Permissible permissible) {
        if(permissible instanceof Player player) {
            if(getMembers().contains(player)) {
                return true;
            }

            if (
                    player.hasPermission("strings.channels." + getName()) ||
                    player.hasPermission("strings.channels.*") ||
                    player.hasPermission("strings.*")
            ) {
                return true;
            }

            if(getMembership() == Membership.DEFAULT && worlds.contains(player.getWorld())) {
                return true;
            }
        }

        return (
                permissible.hasPermission(CHANNEL_PERMISSION + getName()) ||
                permissible.hasPermission(CHANNEL_PERMISSION + "*") ||
                permissible.hasPermission("strings.*")
        );
    }

    protected HashSet<Player> defaultSet() {
        HashSet<Player> set = new HashSet<>(getMembers());
        set.addAll(getMonitors());
        for(World w : worlds){
            set.addAll(w.getPlayers());
        }
        return set;
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

