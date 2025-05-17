package com.pedestriamc.strings.channel;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.channel.ChannelLoader;
import com.pedestriamc.strings.api.channel.Membership;
import com.pedestriamc.strings.api.channel.Type;
import com.pedestriamc.strings.api.text.format.StringsTextColor;
import com.pedestriamc.strings.channel.base.AbstractChannel;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

/**
 * A Channel used for Parties, these channels cannot be saved and are deleted on reboot or when all players leave.
 */
@SuppressWarnings("all")
public class PartyChannel extends AbstractChannel {

    private String name;
    private Player leader;
    private Set<Player> members;
    private String format;
    private String color;
    private ChannelLoader channelLoader;
    private Set<Player> monitors;

    protected PartyChannel(Strings strings, ChannelLoader channelLoader, String name, String defaultColor, String format, Membership membership, boolean doCooldown, boolean doProfanityFilter, boolean doUrlFilter, boolean callEvent, int priority) {
        super(strings, name, StringsTextColor.of(ChatColor.valueOf(defaultColor)), format, membership, doCooldown, doProfanityFilter, doUrlFilter, callEvent, priority, null);
    }

    @Override
    public @NotNull Set<Player> getRecipients(@NotNull Player sender) {
        return Set.of();
    }

    @Override
    public @NotNull Set<Player> getPlayersInScope() {
        return Set.of();
    }

    @Override
    public Set<Player> getMembers() {
        return Set.of();
    }

    @Override
    public void addMember(@NotNull Player player) {
        members.add(player);
    }

    @Override
    public void removeMember(@NotNull Player player) {
        members.remove(player);
    }

    @Override
    public @NotNull Type getType() {
        return Type.PARTY;
    }

    @Override
    public void addMonitor(@NotNull Player player) {
        monitors.add(player);
    }

    @Override
    public void removeMonitor(@NotNull Player player) {
        monitors.remove(player);
    }

    @Override
    public @NotNull Set<Player> getMonitors() {
        return new HashSet<>(monitors);
    }
}
