package com.pedestriamc.strings.channel;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.channel.Membership;
import com.pedestriamc.strings.api.channel.Type;
import com.pedestriamc.strings.api.channel.data.ChannelBuilder;
import com.pedestriamc.strings.channel.base.AbstractChannel;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

/**
 * Standard Channel implementation
 */
public final class StringChannel extends AbstractChannel {

    public StringChannel(JavaPlugin plugin, ChannelBuilder builder) {
        this((Strings) plugin, builder);
    }

    public StringChannel(@NotNull Strings strings, @NotNull ChannelBuilder builder) {
        super(strings, builder);
    }

    @Override
    public @NotNull Set<Player> getRecipients(@NotNull Player sender) {
        Set<Player> recipients = new HashSet<>(getMembers());
        recipients.addAll(getMonitors());
        if(getMembership() == Membership.DEFAULT) {
            for(Player p : Bukkit.getOnlinePlayers()) {
                recipients.add(p);
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
        };
    }

    @Override
    public @NotNull Type getType() {
        return Type.NORMAL;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "stringchannel";
    }
}