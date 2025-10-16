package com.pedestriamc.strings.channel;

import com.pedestriamc.strings.api.StringsPlatform;
import com.pedestriamc.strings.api.channel.Channel;
import com.pedestriamc.strings.api.channel.ChannelLoader;
import com.pedestriamc.strings.api.channel.Type;
import com.pedestriamc.strings.api.channel.local.LocalChannel;
import com.pedestriamc.strings.api.user.StringsUser;
import com.pedestriamc.strings.channel.base.ProtectedChannel;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * The Channel players are assigned to by default.<br/>
 * Does not process any messages, it instead routes them to Channels that can process them.
 */
class DefaultChannel extends ProtectedChannel {

    private static final Component NOT_MEMBER_MESSAGE = Component
            .text("[Strings] You aren't a member of any channels.  Please contact a server operator for help.")
            .color(NamedTextColor.RED);

    private final Set<StringsUser> members = new HashSet<>();
    private final ChannelLoader loader;

    DefaultChannel(@NotNull StringsPlatform strings) {
        super("default");
        loader = strings.getChannelLoader();
    }

    @Override
    public void sendMessage(@NotNull StringsUser user, @NotNull String message) {
        Channel channel = resolve(user);
        if (!channel.equals(this)) {
            channel.sendMessage(user, message);
            return;
        }

        user.sendMessage(NOT_MEMBER_MESSAGE);
    }

    @Override
    public @NotNull Channel resolve(@NotNull StringsUser player) {
        SortedSet<Channel> channels = new TreeSet<>(loader.getSortedChannelSet());
        channels.removeIf(channel -> !channel.allows(player));
        channels.removeIf(channel -> channel instanceof LocalChannel<?> l && !l.containsInScope(player));
        channels.removeIf(player::hasChannelMuted);

        if (!channels.isEmpty()) {
            return channels.first();
        }

        SortedSet<Channel> usersChannels = new TreeSet<>(player.getChannels());
        usersChannels.remove(this);
        if (!usersChannels.isEmpty()) {
            return usersChannels.first();
        }

        return this;
    }

    @Override
    public boolean allows(@NotNull StringsUser user) {
        return true;
    }

    @Override
    @NotNull
    public Type getType() {
        return Type.DEFAULT;
    }

    @Override
    public void addMember(@NotNull StringsUser user) {
        members.add(user);
    }

    @Override
    public void removeMember(@NotNull StringsUser user) {
        members.remove(user);
    }

    @Override
    public Set<StringsUser> getMembers() {
        return Collections.unmodifiableSet(members);
    }


}
