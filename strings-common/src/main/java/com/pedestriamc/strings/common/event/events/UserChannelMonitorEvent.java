package com.pedestriamc.strings.common.event.events;

import com.pedestriamc.strings.api.channel.Channel;
import com.pedestriamc.strings.api.event.strings.user.ChannelMonitorEvent;
import com.pedestriamc.strings.api.user.StringsUser;
import org.jetbrains.annotations.NotNull;

public class UserChannelMonitorEvent extends UserChannelEvent implements ChannelMonitorEvent {

    public UserChannelMonitorEvent(@NotNull Channel channel, @NotNull StringsUser user) {
        super(channel, user);
    }

}
