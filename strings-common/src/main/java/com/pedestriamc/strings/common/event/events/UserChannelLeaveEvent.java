package com.pedestriamc.strings.common.event.events;

import com.pedestriamc.strings.api.channel.Channel;
import com.pedestriamc.strings.api.event.strings.user.ChannelLeaveEvent;
import com.pedestriamc.strings.api.user.StringsUser;
import org.jetbrains.annotations.NotNull;

public class UserChannelLeaveEvent extends UserChannelEvent implements ChannelLeaveEvent {

    public UserChannelLeaveEvent(@NotNull Channel channel, @NotNull StringsUser user) {
        super(channel, user);
    }

}
