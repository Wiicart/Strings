package com.pedestriamc.strings.listeners;

import com.pedestriamc.strings.channel.SocialSpyChannel;
import com.pedestriamc.strings.api.event.PlayerDirectMessageEvent;
import com.pedestriamc.strings.Strings;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class DirectMessageListener implements Listener {

    private final SocialSpyChannel socialSpy;

    public DirectMessageListener(@NotNull Strings strings) {
        socialSpy = (SocialSpyChannel) Objects.requireNonNull(strings.getChannelLoader().getChannel("socialspy"));
    }

    @EventHandler
    void onEvent(@NotNull PlayerDirectMessageEvent event) {
        socialSpy.sendOutMessage(
                event.getSender(),
                event.getRecipient(),
                event.getMessage()
        );
    }
}
