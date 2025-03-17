package com.pedestriamc.strings.impl;

import com.pedestriamc.strings.api.channel.ChannelLoader;
import com.pedestriamc.strings.api.message.Messenger;
import com.pedestriamc.strings.api.*;
import com.pedestriamc.strings.chat.Mentioner;
import com.pedestriamc.strings.api.channel.Channel;
import com.pedestriamc.strings.chat.StringsChannelLoader;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public final class StringsImpl implements StringsAPI {

    private final com.pedestriamc.strings.Strings strings;
    private final StringsChannelLoader channelLoader;
    private final Mentioner mentioner;
    private boolean apiUsed;

    public StringsImpl(@NotNull com.pedestriamc.strings.Strings strings) {
        this.strings = strings;
        this.channelLoader = (StringsChannelLoader) strings.getChannelLoader();
        this.mentioner = strings.getMentioner();
    }

    @Override
    public Set<Channel> getChannels() {
        this.apiUsed = true;
        return new HashSet<>(channelLoader.getChannelList());
    }

    @Override
    public Optional<Channel> getOptionalChannel(String name) {
        this.apiUsed = true;
        return Optional.ofNullable(this.getChannel(name));
    }

    @Override
    public @Nullable Channel getChannel(String name) {
        this.apiUsed = true;
        return channelLoader.getChannel(name);
    }

    @Override
    public Optional<StringsUser> getOptionalStringsUser(UUID uuid) {
        this.apiUsed = true;
        return Optional.ofNullable(strings.getUser(uuid));
    }

    @Override
    public @Nullable StringsUser getStringsUser(UUID uuid){
        this.apiUsed = true;
        return strings.getUser(uuid);
    }

    @Override
    public Set<Channel> getWorldChannels(World world) {
        this.apiUsed = true;
        return channelLoader.getChannels(world);
    }

    @Override
    public boolean isPaper() {
        this.apiUsed = true;
        return strings.isPaper();
    }

    @Override
    public void mention(Player subject, Player sender) {
        mentioner.mention(subject, sender);
    }

    @Override
    public void mention(StringsUser subject, StringsUser sender) {
        mentioner.mention(subject.getPlayer(), sender.getPlayer());
    }

    @Override
    public ChannelLoader getChannelLoader() {
        return strings.getChannelLoader();
    }

    public short getVersion(){
        this.apiUsed = true;
        return strings.getPluginNum();
    }

    public boolean isApiUsed(){
        return apiUsed;
    }

    public Messenger getMessenger() {
        return strings.getMessenger();
    }
}
