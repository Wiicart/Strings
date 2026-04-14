package com.pedestriamc.strings.common.event.events;

import com.pedestriamc.strings.api.channel.Channel;
import com.pedestriamc.strings.api.event.strings.user.ChannelJoinEvent;
import com.pedestriamc.strings.api.user.StringsUser;
import org.jetbrains.annotations.NotNull;

public class UserChannelJoinEvent extends UserChannelEvent implements ChannelJoinEvent {

    public UserChannelJoinEvent(@NotNull Channel channel, @NotNull StringsUser user) {
        super(channel, user);
    }

}
