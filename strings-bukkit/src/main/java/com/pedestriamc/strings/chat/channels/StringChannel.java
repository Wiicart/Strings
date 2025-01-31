package com.pedestriamc.strings.chat.channels;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.channels.Membership;
import com.pedestriamc.strings.api.channels.Buildable;
import com.pedestriamc.strings.api.channels.ChannelLoader;
import com.pedestriamc.strings.api.channels.Type;
import com.pedestriamc.strings.api.channels.data.ChannelData;
import com.pedestriamc.strings.chat.channels.base.AbstractChannel;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class StringChannel extends AbstractChannel implements Buildable {

    private final Set<Player> members;

    @Deprecated
    public StringChannel(Strings strings, String name, String format, String defaultColor, ChannelLoader channelLoader, boolean callEvent, boolean doURLFilter, boolean doProfanityFilter, boolean doCooldown, Membership membership, int priority) {
        super(strings, channelLoader, name, defaultColor, format, membership, doCooldown, doProfanityFilter, doURLFilter, callEvent, priority);
        this.members = ConcurrentHashMap.newKeySet();
    }

    public StringChannel(Strings strings, ChannelData data) {

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

    }

    @Override
    public Set<Player> getRecipients(Player sender) {
        Set<Player> recipients = new HashSet<>(members);
        if(getMembership() == Membership.DEFAULT) {
            for(Player p : Bukkit.getOnlinePlayers()) {
                if(p.hasPermission("strings.channels." + getName() + ".receive")){
                    recipients.add(p);
                }
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
        return Type.NORMAL;
    }

}