package com.pedestriamc.strings.impl;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.channel.ChannelLoader;
import com.pedestriamc.strings.api.message.Messenger;
import com.pedestriamc.strings.api.*;
import com.pedestriamc.strings.api.user.StringsUser;
import com.pedestriamc.strings.chat.Mentioner;
import com.pedestriamc.strings.user.User;
import com.pedestriamc.strings.user.util.UserUtil;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public final class StringsImpl implements StringsAPI {

    private final Strings strings;
    private final Mentioner mentioner;
    private final UserUtil userUtil;

    public StringsImpl(@NotNull Strings strings) {
        this.strings = strings;
        mentioner = strings.getMentioner();
        userUtil = strings.getUserUtil();
    }

    @Override
    public @NotNull StringsUser getStringsUser(UUID uuid) {
        return userUtil.getUser(uuid);
    }

    @Override
    public @NotNull StringsUser getStringsUser(Player player) {
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
        return Strings.PLUGIN_NUM;
    }

    public Messenger getMessenger() {
        return strings.getMessenger();
    }

}
