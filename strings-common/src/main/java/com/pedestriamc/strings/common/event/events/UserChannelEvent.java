package com.pedestriamc.strings.common.event.events;

import com.pedestriamc.strings.api.channel.Channel;
import com.pedestriamc.strings.api.event.strings.user.ActiveChannelUpdateEvent;
import com.pedestriamc.strings.api.event.strings.user.ChannelJoinEvent;
import com.pedestriamc.strings.api.event.strings.user.ChannelLeaveEvent;
import com.pedestriamc.strings.api.event.strings.user.ChannelMonitorEvent;
import com.pedestriamc.strings.api.event.strings.user.ChannelUnmonitorEvent;
import com.pedestriamc.strings.api.user.StringsUser;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("ClassCanBeRecord")
abstract class UserChannelEvent implements ChannelJoinEvent, ChannelLeaveEvent, ChannelMonitorEvent, ChannelUnmonitorEvent, ActiveChannelUpdateEvent {

    private final Channel channel;
    private final StringsUser user;

    protected UserChannelEvent(@NotNull Channel channel, @NotNull StringsUser user) {
        this.channel = channel;
        this.user = user;
    }

    @Override
    public @NotNull StringsUser getUser() {
        return user;
    }

    @Override
    public @NotNull Channel getChannel() {
        return channel;
    }
}
