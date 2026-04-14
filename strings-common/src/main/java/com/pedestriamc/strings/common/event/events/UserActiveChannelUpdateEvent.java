package com.pedestriamc.strings.common.event.events;

import com.pedestriamc.strings.api.channel.Channel;
import com.pedestriamc.strings.api.event.strings.user.ActiveChannelUpdateEvent;
import com.pedestriamc.strings.api.user.StringsUser;
import org.jetbrains.annotations.NotNull;

public class UserActiveChannelUpdateEvent extends UserChannelEvent implements ActiveChannelUpdateEvent {

    public UserActiveChannelUpdateEvent(@NotNull Channel channel, @NotNull StringsUser user) {
        super(channel, user);
    }

}
