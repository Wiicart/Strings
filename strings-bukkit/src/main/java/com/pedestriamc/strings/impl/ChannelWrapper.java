package com.pedestriamc.strings.impl;

import com.pedestriamc.strings.User;
import com.pedestriamc.strings.api.StringsChannel;
import com.pedestriamc.strings.api.StringsUser;
import com.pedestriamc.strings.api.Type;
import com.pedestriamc.strings.channels.Channel;
import org.bukkit.entity.Player;

import java.util.Set;

public record ChannelWrapper(Channel channel) implements StringsChannel {

    public Channel getChannel() {
        return this.channel;
    }

    @Override
    public void sendMessage(Player player, String message) {
        channel.sendMessage(player, message);
    }

    @Override
    public void broadcastMessage(String message) {
        channel.broadcastMessage(message);
    }

    @Override
    public String getFormat() {
        return channel.getFormat();
    }

    @Override
    public String getDefaultColor() {
        return channel.getDefaultColor();
    }

    @Override
    public String getName() {
        return channel.getName();
    }

    @Override
    public void setName(String name) {
        channel.setName(name);
    }

    @Override
    public void setDefaultColor(String defaultColor) {
        channel.setDefaultColor(defaultColor);
    }

    @Override
    public void setFormat(String format) {
        channel.setFormat(format);
    }

    @Override
    public void addPlayer(Player player) {
        channel.addPlayer(player);
    }

    @Override
    public void removePlayer(Player player) {
        channel.removePlayer(player);
    }

    @Override
    public void addPlayer(StringsUser stringsUser) {
        User user = ((UserWrapper) stringsUser).user();
        channel.addPlayer(user);
    }

    @Override
    public void removePlayer(StringsUser stringsUser) {
        User user = ((UserWrapper) stringsUser).user();
        channel.removePlayer(user);
    }

    @Override
    public Set<Player> getMembers() {
        return channel.getMembers();
    }

    @Override
    public boolean doURLFilter() {
        return channel.doURLFilter();
    }

    @Override
    public boolean doProfanityFilter() {
        return channel.doProfanityFilter();
    }

    @Override
    public boolean doCooldown() {
        return channel.doCooldown();
    }

    @Override
    public Type getType() {
        return channel.getType();
    }

    @Override
    public void setEnabled(boolean isEnabled) {
        channel.setEnabled(isEnabled);
    }

    @Override
    public boolean isEnabled() {
        return channel.isEnabled();
    }
}
