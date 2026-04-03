package com.pedestriamc.strings.bukkit;

import com.pedestriamc.strings.api.event.BukkitMessageDeletionEvent;
import com.pedestriamc.strings.api.event.strings.user.ActiveChannelUpdateEvent;
import com.pedestriamc.strings.api.event.strings.user.ChannelJoinEvent;
import com.pedestriamc.strings.api.event.strings.user.ChannelLeaveEvent;
import com.pedestriamc.strings.api.event.strings.user.ChannelMonitorEvent;
import com.pedestriamc.strings.api.event.strings.user.ChannelUnmonitorEvent;
import com.pedestriamc.strings.api.platform.EventFactory;
import com.pedestriamc.strings.api.channel.Channel;
import com.pedestriamc.strings.api.user.StringsUser;
import com.pedestriamc.strings.common.event.events.UserChannelEvent;
import com.pedestriamc.strings.event.ChannelChatEvent2;
import com.pedestriamc.strings.event.BukkitDirectMessageEvent;
import net.kyori.adventure.chat.SignedMessage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class BukkitEventFactory implements EventFactory {

    public BukkitEventFactory() {
        // no need to do anything here
    }

    @Override
    @NotNull
    public ChannelChatEvent2 chatEvent(
            boolean async,
            boolean cancellable,
            @NotNull StringsUser sender,
            @NotNull String message,
            @NotNull Set<StringsUser> recipients,
            @NotNull Channel channel,
            @Nullable SignedMessage signedMessage
    ) {
        return new ChannelChatEvent2(async, cancellable, sender, message, recipients, channel, signedMessage);
    }

    @Override
    @NotNull
    public BukkitDirectMessageEvent directMessage(
            @NotNull StringsUser sender,
            @NotNull StringsUser recipient,
            @NotNull String message
    ) {
        return new BukkitDirectMessageEvent(sender, recipient, message);
    }

    @Override
    @NotNull
    public BukkitMessageDeletionEvent messageDeletion(@NotNull SignedMessage signedMessage) {
        return new BukkitMessageDeletionEvent(signedMessage);
    }

    @Override
    public @NotNull ChannelJoinEvent channelJoin(@NotNull StringsUser user, @NotNull Channel channel) {
        return new UserChannelEvent(channel, user);
    }

    @Override
    public @NotNull ChannelLeaveEvent channelLeave(@NotNull StringsUser user, @NotNull Channel channel) {
        return new UserChannelEvent(channel, user);
    }

    @Override
    public @NotNull ChannelMonitorEvent channelMonitor(@NotNull StringsUser user, @NotNull Channel channel) {
        return new UserChannelEvent(channel, user);
    }

    @Override
    public @NotNull ChannelUnmonitorEvent channelUnmonitor(@NotNull StringsUser user, @NotNull Channel channel) {
        return new UserChannelEvent(channel, user);
    }

    @Override
    public @NotNull ActiveChannelUpdateEvent channelActive(@NotNull StringsUser user, @NotNull Channel channel) {
        return new UserChannelEvent(channel, user);
    }
}
