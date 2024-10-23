package com.pedestriamc.strings.impl;

import com.pedestriamc.strings.user.User;
import com.pedestriamc.strings.chat.channels.Channel;
import com.pedestriamc.strings.api.StringsChannel;
import com.pedestriamc.strings.api.StringsUser;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
    @NotNull
    public UUID getUuid() {
        return user.getUuid();
    }

    @Override
    @NotNull
    public String getName() {
        return user.getName();
    }

    @Override
    @Nullable
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
    @NotNull
    public String getDisplayName() {
        return user.getDisplayName();
    }

    @Override
    public void setDisplayName(@NotNull String displayName) {
        user.setDisplayName(displayName);
    }

    @Override
    @NotNull
    public StringsChannel getActiveChannel() {
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

    @Override
    public boolean mentionsEnabled() {
        return user.isMentionsEnabled();
    }

    @Override
    public void setMentionsEnabled(boolean mentionsEnabled) {
        user.setMentionsEnabled(mentionsEnabled);
    }

    public User getUser(){
        return this.user;
    }

    public Player getPlayer(){
        return user.getPlayer();
    }
}
