package com.pedestriamc.strings.impl;

import com.pedestriamc.strings.api.Membership;
import com.pedestriamc.strings.api.StringsAPI;
import com.pedestriamc.strings.api.StringsChannel;
import com.pedestriamc.strings.api.StringsUser;
import com.pedestriamc.strings.channels.Channel;
import com.pedestriamc.strings.channels.ChannelManager;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public final class StringsImpl implements StringsAPI {

    private final com.pedestriamc.strings.Strings strings;
    private final ChannelManager channelManager;

    public StringsImpl(@NotNull com.pedestriamc.strings.Strings strings){
        this.strings = strings;
        this.channelManager = strings.getChannelManager();
    }

    @Override
    public Set<StringsChannel> getChannels() {
        return channelManager.getChannelList().stream()
                .map(Channel::getStringsChannel)
                .collect(Collectors.toSet());
    }

    @Override
    public Optional<StringsChannel> getChannel(String name) {
        Channel channel = channelManager.getChannel(name);
        if(channel == null){
            return Optional.empty();
        }
        return Optional.of(channel.getStringsChannel());
    }

    @Override
    public Optional<StringsUser> getStringsUser(UUID uuid) {
        return Optional.ofNullable(strings.getUser(uuid).getStringsUser());
    }

    @Override
    public StringsChannel[] getWorldChannels(World world) {
        Channel[] worldChannels = channelManager.getWorldChannels(world);
        StringsChannel[] worldStringsChannels = new StringsChannel[worldChannels.length];
        for(int i=0; i<worldChannels.length; i++){
            worldStringsChannels[i] = worldChannels[i].getStringsChannel();
        }
        return worldStringsChannels;
    }

    @Override
    public StringsChannel createChannel(String name, String format, String defaultColor, boolean callEvent, boolean doURLFilter, boolean doProfanityFilter, boolean doCooldown, boolean active, Membership membership, int priority) {
        return channelManager.createChannel(name, format, defaultColor, callEvent, doURLFilter, doProfanityFilter, doCooldown, active, membership, priority).getStringsChannel();
    }

    @Override
    public StringsChannel createChannel(String name, String format, String defaultColor, boolean callEvent, boolean doURLFilter, boolean doProfanityFilter, boolean doCooldown, boolean active, World world, Membership membership, int priority) {
        return channelManager.createChannel(name, format, defaultColor, callEvent, doURLFilter, doProfanityFilter, doCooldown, active, world, membership, priority).getStringsChannel();
    }

    @Override
    public StringsChannel createChannel(String name, String format, String defaultColor, boolean callEvent, boolean doURLFilter, boolean doProfanityFilter, boolean doCooldown, boolean active, int distance, Membership membership, int priority) {
        return channelManager.createChannel(name, format, defaultColor, callEvent, doURLFilter, doProfanityFilter, doCooldown, active, distance, membership, priority).getStringsChannel();
    }

    @Override
    public void deleteChannel(StringsChannel channel) {
        ChannelWrapper wrapper = (ChannelWrapper) channel;
        channelManager.deleteChannel(wrapper.getChannel());
    }

    @Override
    public boolean isPaper() {
        return strings.isPaper();
    }

    public short getVersion(){
        return 1;
    }
}
