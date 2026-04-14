package com.pedestriamc.strings.common.event.events;

import com.pedestriamc.strings.api.channel.Channel;
import com.pedestriamc.strings.api.event.strings.user.ChannelUnmonitorEvent;
import com.pedestriamc.strings.api.user.StringsUser;
import org.jetbrains.annotations.NotNull;

public class UserChannelUnmonitorEvent extends UserChannelEvent implements ChannelUnmonitorEvent{

    public UserChannelUnmonitorEvent(@NotNull Channel channel, @NotNull StringsUser user) {
        super(channel, user);
    }

}
