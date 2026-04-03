package com.pedestriamc.strings.hytale;

import com.pedestriamc.strings.api.channel.Channel;
import com.pedestriamc.strings.api.event.DirectMessageEvent;
import com.pedestriamc.strings.api.event.StringsChatEvent;
import com.pedestriamc.strings.api.event.moderation.MessageDeletionEvent;
import com.pedestriamc.strings.api.event.strings.user.ActiveChannelUpdateEvent;
import com.pedestriamc.strings.api.event.strings.user.ChannelJoinEvent;
import com.pedestriamc.strings.api.event.strings.user.ChannelLeaveEvent;
import com.pedestriamc.strings.api.event.strings.user.ChannelMonitorEvent;
import com.pedestriamc.strings.api.event.strings.user.ChannelUnmonitorEvent;
import com.pedestriamc.strings.api.platform.EventFactory;
import com.pedestriamc.strings.api.user.StringsUser;
import net.kyori.adventure.chat.SignedMessage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class HytaleEventFactory implements EventFactory {

    @Override
    public @NotNull StringsChatEvent chatEvent(boolean async, boolean cancellable, @NotNull StringsUser sender, @NotNull String message, @NotNull Set<StringsUser> recipients, @NotNull Channel channel, @Nullable SignedMessage signedMessage) {
        return null;
    }

    @Override
    public @NotNull DirectMessageEvent directMessage(@NotNull StringsUser sender, @NotNull StringsUser recipient, @NotNull String message) {
        return null;
    }

    @Override
    public @NotNull MessageDeletionEvent messageDeletion(@NotNull SignedMessage signedMessage) {
        return null;
    }

    @Override
    public @NotNull ChannelJoinEvent channelJoin(@NotNull StringsUser user, @NotNull Channel channel) {
        return null;
    }

    @Override
    public @NotNull ChannelLeaveEvent channelLeave(@NotNull StringsUser user, @NotNull Channel channel) {
        return null;
    }

    @Override
    public @NotNull ChannelMonitorEvent channelMonitor(@NotNull StringsUser user, @NotNull Channel channel) {
        return null;
    }

    @Override
    public @NotNull ChannelUnmonitorEvent channelUnmonitor(@NotNull StringsUser user, @NotNull Channel channel) {
        return null;
    }

    @Override
    public @NotNull ActiveChannelUpdateEvent channelActive(@NotNull StringsUser user, @NotNull Channel channel) {
        return null;
    }

}
