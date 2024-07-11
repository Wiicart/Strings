package com.pedestriamc.strings.listeners;

import com.pedestriamc.strings.ChatManager;
import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.message.Message;
import com.pedestriamc.strings.message.Messenger;
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
        if(chatManager.isOnCoolDown(playerSender)){
            event.setCancelled(true);
            Messenger.sendMessage(Message.COOL_DOWN, event.getPlayer());
        }else{
            event.setFormat(chatManager.formatMessage(playerSender));
            event.setMessage(chatManager.processMessage(playerSender,playerMessage));
            if(!event.isCancelled()){
                chatManager.startCoolDown(playerSender);
            }
        }
    }
}
