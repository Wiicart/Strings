package com.pedestriamc.strings.chat.channel;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.channel.Membership;
import com.pedestriamc.strings.api.channel.Type;
import com.pedestriamc.strings.api.channel.data.ChannelData;
import com.pedestriamc.strings.chat.channel.base.AbstractChannel;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Standard Channel implementation
 */
public class StringChannel extends AbstractChannel {

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
                data.getPriority(),
                data.getBroadcastFormat()
        );
    }

    @Override
    public Set<Player> getRecipients(@NotNull Player sender) {
        Set<Player> recipients = new HashSet<>(getMembers());
        recipients.addAll(getMonitors());
        if(getMembership() == Membership.DEFAULT) {
            for(Player p : Bukkit.getOnlinePlayers()) {
                if(p.hasPermission(CHANNEL_PERMISSION + getName() + ".receive")) {
                    recipients.add(p);
                }
            }
        }
        return recipients;
    }

    @Override
    public Set<Player> getPlayersInScope() {
        switch (getMembership()) {
            case DEFAULT -> { return new HashSet<>(Bukkit.getOnlinePlayers()); }
            case PROTECTED -> { return getMembers(); }
            case PERMISSION -> {
                HashSet<Player> scoped = new HashSet<>(getMembers());
                scoped.addAll(getMonitors());
                for(Player p : Bukkit.getOnlinePlayers()) {
                    if(allows(p)) {
                        scoped.add(p);
                    }
                }
                return scoped;
            }
            default -> { return Collections.emptySet(); }
        }
    }

    @Override
    public Type getType() {
        return Type.NORMAL;
    }

}