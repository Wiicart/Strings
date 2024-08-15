package com.pedestriamc.strings.listeners;

import com.pedestriamc.strings.channels.SocialSpyChannel;
import com.pedestriamc.strings.api.event.PlayerDirectMessageEvent;
import com.pedestriamc.strings.Strings;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class DirectMessageListener implements Listener {

    private final SocialSpyChannel socialSpy = (SocialSpyChannel) Strings.getInstance().getChannel("socialspy");

    @EventHandler
    public void onPlayerDirectMessageEvent(PlayerDirectMessageEvent event){
        socialSpy.sendOutMessage(event.getSender(), event.getRecipient(), event.getMessage());
    }
}
