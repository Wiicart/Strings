package com.pedestriamc.strings.common.event.events;

import com.pedestriamc.strings.api.channel.Channel;
import com.pedestriamc.strings.api.event.strings.user.UserChannelEvent;
import com.pedestriamc.strings.api.user.StringsUser;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("ClassCanBeRecord")
abstract class AbstractUserChannelEvent implements UserChannelEvent {

    private final Channel channel;
    private final StringsUser user;

    protected AbstractUserChannelEvent(@NotNull Channel channel, @NotNull StringsUser user) {
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
