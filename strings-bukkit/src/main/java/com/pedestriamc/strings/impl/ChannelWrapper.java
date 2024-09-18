package com.pedestriamc.strings.impl;

import com.pedestriamc.strings.User;
import com.pedestriamc.strings.api.Membership;
import com.pedestriamc.strings.api.StringsChannel;
import com.pedestriamc.strings.api.StringsUser;
import com.pedestriamc.strings.api.Type;
import com.pedestriamc.strings.chat.channels.Channel;
import com.pedestriamc.strings.chat.channels.ProximityChannel;
import com.pedestriamc.strings.chat.channels.WorldChannel;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.Set;

@SuppressWarnings("ClassCanBeRecord")
public final class ChannelWrapper implements StringsChannel{

    private final Channel channel;

    public ChannelWrapper(Channel channel){
        this.channel = channel;
    }

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
        User user = ((UserWrapper) stringsUser).getUser();
        channel.addPlayer(user);
    }

    @Override
    public void removePlayer(StringsUser stringsUser) {
        User user = ((UserWrapper) stringsUser).getUser();
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

    @Override
    public Optional<World> getWorld() {
        if(channel.getType() == Type.WORLD){
            return Optional.of(((WorldChannel) channel).getWorld());
        }
        return Optional.empty();
    }

    @Override
    public OptionalInt getProximity() {
        return null;
    }


    @Override
    public Membership getMembership() {
        return channel.getMembership();
    }

    @Override
    public int getPriority() {
        return channel.getPriority();
    }

    @Override
    public void setProximity(int i) {

    }


}
