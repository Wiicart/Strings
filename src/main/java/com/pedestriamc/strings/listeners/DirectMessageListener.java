package com.pedestriamc.strings.listeners;

import com.pedestriamc.strings.SocialSpy;
import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.directmessage.PlayerDirectMessageEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class DirectMessageListener implements Listener {

    private final SocialSpy socialSpy = Strings.getInstance().getSocialSpy();

    @EventHandler
    public void onPlayerDirectMessageEvent(PlayerDirectMessageEvent event){
        socialSpy.sendOutMessage(event.getSender(), event.getRecipient(), event.getMessage());
    }
}
