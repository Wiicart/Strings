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
public final class StringChannel extends AbstractChannel {

    public StringChannel(@NotNull Strings strings, @NotNull ChannelData data) {
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
    public @NotNull Set<Player> getRecipients(@NotNull Player sender) {
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
    public @NotNull Set<Player> getPlayersInScope() {
        return switch (getMembership()) {
            case DEFAULT -> new HashSet<>(Bukkit.getOnlinePlayers());
            case PROTECTED -> getMembers();
            case PERMISSION -> {
                HashSet<Player> scoped = new HashSet<>(getMembers());
                scoped.addAll(getMonitors());
                for(Player p : Bukkit.getOnlinePlayers()) {
                    if(allows(p)) {
                        scoped.add(p);
                    }
                }
                yield scoped;
            }
            default -> Collections.emptySet();
        };
    }

    @Override
    public @NotNull Type getType() {
        return Type.NORMAL;
    }

}