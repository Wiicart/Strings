package com.pedestriamc.strings.api.event.strings.user;

import com.pedestriamc.strings.api.channel.Channel;
import com.pedestriamc.strings.api.event.strings.StringsEvent;
import com.pedestriamc.strings.api.user.StringsUser;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;

@Internal
public interface UserChannelEvent extends StringsEvent {

    /**
     * Provides the User associated with the Event.
     *
     * @return The User.
     */
    @NotNull
    StringsUser getUser();

    /**
     * Provides the Channel associated with the Event.
     *
     * @return A Channel
     */
    @NotNull
    Channel getChannel();

}