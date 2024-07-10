package com.pedestriamc.strings.listeners;

import com.pedestriamc.strings.ChatManager;
import com.pedestriamc.strings.Strings;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener implements Listener {

    private final ChatManager chatManager = Strings.getInstance().getChatManager();

    @EventHandler
    public void onAsyncPlayerChatEvent(AsyncPlayerChatEvent event) {
        Player playerSender = event.getPlayer();
        String playerMessage = event.getMessage();
        event.setFormat(chatManager.formatMessage(playerSender));
        event.setMessage(chatManager.processMessage(playerSender,playerMessage));
    }
}
