package com.pedestriamc.strings.impl;

import com.pedestriamc.strings.User;
import com.pedestriamc.strings.chat.channels.Channel;
import com.pedestriamc.strings.api.StringsChannel;
import com.pedestriamc.strings.api.StringsUser;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@SuppressWarnings("ClassCanBeRecord")
public final class UserWrapper implements StringsUser {

    private final User user;

    public UserWrapper(User user){
        this.user = user;
    }

    @Override
    public @NotNull UUID getUuid() {
        return user.getUuid();
    }

    @Override
    public @NotNull String getName() {
        return user.getName();
    }

    @Override
    public String getChatColor() {
        return user.getChatColor();
    }

    @Override
    public void setChatColor(@NotNull String chatColor) {
        user.setChatColor(chatColor);
    }

    @Override
    public String getPrefix() {
        return user.getPrefix();
    }

    @Override
    public void setPrefix(@NotNull String prefix) {
        user.setPrefix(prefix);
    }

    @Override
    public String getSuffix() {
        return user.getSuffix();
    }

    @Override
    public void setSuffix(@NotNull String suffix) {
        user.setSuffix(suffix);
    }

    @Override
    public @NotNull String getDisplayName() {
        return user.getDisplayName();
    }

    @Override
    public void setDisplayName(@NotNull String displayName) {
        user.setDisplayName(displayName);
    }

    @Override
    public @NotNull StringsChannel getActiveChannel() {
        return user.getActiveChannel().getStringsChannel();
    }

    @Override
    public void setActiveChannel(@NotNull StringsChannel channel) {
        Channel newChannel = ((ChannelWrapper) channel).getChannel();
        user.setActiveChannel(newChannel);
    }

    @Override
    public Set<StringsChannel> getChannels() {
        return user.getChannels().stream()
                .map(Channel::getStringsChannel)
                .collect(Collectors.toSet());
    }

    @Override
    public void joinChannel(@NotNull StringsChannel channel) {
        Channel newChannel = ((ChannelWrapper) channel).getChannel();
        user.joinChannel(newChannel);
    }

    @Override
    public void leaveChannel(StringsChannel channel) {
        Channel newChannel = ((ChannelWrapper) channel).getChannel();
        user.leaveChannel(newChannel);
    }

    @Override
    public boolean memberOf(StringsChannel channel) {
        Channel newChannel = ((ChannelWrapper) channel).getChannel();
        return user.memberOf(newChannel);
    }

    public User getUser(){
        return this.user;
    }
}
