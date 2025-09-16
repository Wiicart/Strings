package com.pedestriamc.strings.impl;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.channel.ChannelLoader;
import com.pedestriamc.strings.api.*;
import com.pedestriamc.strings.api.message.Messenger;
import com.pedestriamc.strings.api.settings.Settings;
import com.pedestriamc.strings.api.text.EmojiManager;
import com.pedestriamc.strings.api.user.StringsUser;
import com.pedestriamc.strings.chat.Mentioner;
import com.pedestriamc.strings.user.User;
import com.pedestriamc.strings.user.util.UserUtil;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public final class StringsImpl implements StringsAPI {

    private final Strings strings;
    private final Mentioner mentioner;
    private final UserUtil userUtil;

    public StringsImpl(@NotNull Strings strings) {
        this.strings = strings;
        mentioner = strings.getMentioner();
        userUtil = strings.users();
    }

    @Override
    @NotNull
    public StringsUser getUser(@NotNull UUID uuid) {
        return userUtil.getUser(uuid);
    }

    @Override
    public void saveUser(@NotNull StringsUser user) {
        if(user instanceof User u) {
            userUtil.saveUser(u);
        }
    }

    @Override
    public boolean isPaper() {
        return strings.isPaper();
    }

    @Override
    public void mention(@NotNull StringsUser subject, @NotNull StringsUser sender) {
        mentioner.mention(User.playerOf(subject), User.playerOf(sender));
    }

    @Override
    public void sendMention(@NotNull StringsUser subject, @NotNull String message) {
        mentioner.sendMention(User.playerOf(subject), message);
    }

    @Override
    @NotNull
    public ChannelLoader getChannelLoader() {
        return strings.getChannelLoader();
    }

    public short getVersion() {
        return Strings.VERSION_NUM;
    }

    @NotNull
    public Messenger getMessenger() {
        return strings.getMessenger();
    }

    @Override
    @NotNull
    public Settings getSettings() {
        return strings.getConfiguration();
    }

    @Override
    public @NotNull EmojiManager emojiManager() {
        return strings.getEmojiManager();
    }

}
