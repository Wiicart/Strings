package com.pedestriamc.strings.listeners;

import com.pedestriamc.strings.chat.channel.SocialSpyChannel;
import com.pedestriamc.strings.api.event.PlayerDirectMessageEvent;
import com.pedestriamc.strings.Strings;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class DirectMessageListener implements Listener {

    private final SocialSpyChannel socialSpy;

    public DirectMessageListener(Strings strings) {
        socialSpy = (SocialSpyChannel) strings.getChannel("socialspy");
    }

    @EventHandler
    public void onEvent(PlayerDirectMessageEvent event) {
        socialSpy.sendOutMessage(
                event.getSender(),
                event.getRecipient(),
                event.getMessage()
        );
    }
}
