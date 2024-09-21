package com.pedestriamc.strings.chat.channels;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.Membership;
import com.pedestriamc.strings.api.Type;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class StringChannel extends AbstractChannel{

    private final Set<Player> members;

    public StringChannel(Strings strings, String name, String format, String defaultColor, ChannelManager channelManager, boolean callEvent, boolean doURLFilter, boolean doProfanityFilter, boolean doCooldown, Membership membership, int priority){
        super(strings, channelManager, name, defaultColor, format, membership, doCooldown, doProfanityFilter, doURLFilter, callEvent, priority);
        this.members = ConcurrentHashMap.newKeySet();
        channelManager.registerChannel(this);
    }

    @Override
    public Set<Player> getRecipients(Player sender){

        Set<Player> recipients = new HashSet<>(members);

        if(super.getMembership() == Membership.DEFAULT){
            for(Player p : Bukkit.getOnlinePlayers()){
                if(!p.hasPermission("strings.channels." + super.getName() + ".receive")){
                    continue;
                }
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

}