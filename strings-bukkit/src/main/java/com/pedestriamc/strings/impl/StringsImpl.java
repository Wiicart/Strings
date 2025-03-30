package com.pedestriamc.strings.impl;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.channel.ChannelLoader;
import com.pedestriamc.strings.api.message.Messenger;
import com.pedestriamc.strings.api.*;
import com.pedestriamc.strings.api.user.StringsUser;
import com.pedestriamc.strings.chat.Mentioner;
import com.pedestriamc.strings.api.channel.Channel;
import com.pedestriamc.strings.chat.ChannelManager;
import com.pedestriamc.strings.user.User;
import com.pedestriamc.strings.user.UserUtil;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public final class StringsImpl implements StringsAPI {

    private final com.pedestriamc.strings.Strings strings;
    private final ChannelManager channelLoader;
    private final Mentioner mentioner;
    private final UserUtil userUtil;
    private boolean apiUsed;

    public StringsImpl(@NotNull Strings strings) {
        this.strings = strings;
        channelLoader = strings.getChannelLoader();
        mentioner = strings.getMentioner();
        userUtil = strings.getUserUtil();
    }

    @Override
    public Set<Channel> getChannels() {
        this.apiUsed = true;
        return channelLoader.getChannels();
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
    public @Nullable StringsUser getStringsUser(UUID uuid) {
        this.apiUsed = true;
        return strings.getUser(uuid);
    }

    @Override
    public @Nullable StringsUser getStringsUser(Player player) {
        return getStringsUser(player.getUniqueId());
    }

    @Override
    public void saveStringsUser(StringsUser user) {
        if(user instanceof User u) {
            userUtil.saveUser(u);
        }
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
    public void mention(@NotNull StringsUser subject, @NotNull StringsUser sender) {
        mentioner.mention(subject.getPlayer(), sender.getPlayer());
    }

    @Override
    public ChannelLoader getChannelLoader() {
        return strings.getChannelLoader();
    }

    public short getVersion() {
        this.apiUsed = true;
        return strings.getPluginNum();
    }

    public boolean isApiUsed() {
        return apiUsed;
    }

    public Messenger getMessenger() {
        return strings.getMessenger();
    }
}
