package com.pedestriamc.strings.impl;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.User;
import com.pedestriamc.strings.api.StringsAPI;
import com.pedestriamc.strings.api.StringsChannel;
import com.pedestriamc.strings.api.StringsUser;
import com.pedestriamc.strings.channels.Channel;
import com.pedestriamc.strings.channels.ChannelManager;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class StringsImpl implements StringsAPI {

    private final Strings strings;
    private final ChannelManager channelManager;
    private final short version = 1;

    public StringsImpl(@NotNull Strings strings){
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
        User user = strings.getUser(uuid);
        if(user == null){
            return Optional.empty();
        }
        return Optional.of(user.getStringsUser());
    }

    public short getVersion(){
        return version;
    }
}
