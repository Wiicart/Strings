package com.pedestriamc.strings.api.event.moderation;

import com.pedestriamc.strings.api.event.ChannelChatEvent;
import com.pedestriamc.strings.api.event.strings.StringsEvent;
import com.pedestriamc.strings.api.user.StringsUser;
import org.jetbrains.annotations.ApiStatus.Internal;


@SuppressWarnings("unused")
public final class PlayerSpamEvent implements StringsEvent {

    private final StringsUser player;
    private final ChannelChatEvent event;

    @Internal
    public PlayerSpamEvent(StringsUser player, ChannelChatEvent event) {
        this.player = player;
        this.event = event;
    }

    public StringsUser getPlayer() {
        return player;
    }

    public ChannelChatEvent getChatEvent() {
        return event;
    }

}
