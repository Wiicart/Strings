package com.pedestriamc.strings.chat.channel;

import com.pedestriamc.strings.api.channel.Monitorable;
import com.pedestriamc.strings.chat.channel.base.ProtectedChannel;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permissible;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("unused")
public class LogChannel extends ProtectedChannel implements Monitorable {

    private final Set<Player> members;
    private final Set<Player> monitors;

    protected LogChannel(String name) {
        super(name);
        members = new HashSet<>();
        monitors = new HashSet<>();
    }

    @Override
    public void broadcast(String message) {
        Set<Player> recipients = new HashSet<>(members);
        recipients.addAll(monitors);
        for(Player player : recipients) {
            player.sendMessage(message);
        }
    }

    @Override
    public boolean allows(@NotNull Permissible permissible) {
        return false;
    }

    @Override
    public void addMember(Player player) {
        members.add(player);
    }

    @Override
    public void removeMember(Player player) {
        members.remove(player);
    }

    @Override
    public Set<Player> getMembers() {
        return new HashSet<>(members);
    }

    @Override
    public Set<Player> getMonitors() {
        return new HashSet<>(monitors);
    }

    @Override
    public void addMonitor(Player player) {
        monitors.add(player);
    }

    @Override
    public void removeMonitor(Player player) {
        monitors.remove(player);
    }

}
