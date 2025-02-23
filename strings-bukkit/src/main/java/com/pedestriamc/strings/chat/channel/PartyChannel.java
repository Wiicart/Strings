package com.pedestriamc.strings.chat.channel;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.channels.ChannelLoader;
import com.pedestriamc.strings.api.channels.Membership;
import com.pedestriamc.strings.api.channels.Type;
import com.pedestriamc.strings.chat.channel.base.AbstractChannel;
import org.bukkit.entity.Player;

import java.util.Set;

/**
 * A Channel used for Parties, these channels cannot be saved and are deleted on reboot or when all players leave.
 */
public class PartyChannel extends AbstractChannel {

    private String name;
    private Player leader;
    private Set<Player> members;
    private String format;
    private String color;
    private ChannelLoader channelLoader;

    protected PartyChannel(Strings strings, ChannelLoader channelLoader, String name, String defaultColor, String format, Membership membership, boolean doCooldown, boolean doProfanityFilter, boolean doUrlFilter, boolean callEvent, int priority) {
        super(strings, channelLoader, name, defaultColor, format, membership, doCooldown, doProfanityFilter, doUrlFilter, callEvent, priority, null);
    }

    @Override
    public Set<Player> getRecipients(Player sender) {
        return Set.of();
    }

    @Override
    public Set<Player> getPlayersInScope() {
        return Set.of();
    }

    @Override
    public Set<Player> getMembers() {
        return Set.of();
    }

    @Override
    public void addPlayer(Player player) {

    }

    @Override
    public void removePlayer(Player player) {

    }

    @Override
    public Type getType() {
        return Type.PARTY;
    }

    @Override
    public void addMonitor(Player player) {

    }

    @Override
    public void removeMonitor(Player player) {

    }

    @Override
    public Set<Player> getMonitors() {
        return Set.of();
    }
}
