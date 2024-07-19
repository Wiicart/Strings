package com.pedestriamc.strings.listeners;

import com.pedestriamc.strings.ChatManager;
import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.User;
import com.pedestriamc.strings.channels.Channel;
import com.pedestriamc.strings.channels.ChannelChatEvent;
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

        //this signifies that this event came from a Channel, and has been processed
        if(event instanceof ChannelChatEvent){
            chatManager.startCoolDown(playerSender);
            return;
        }
        User user = Strings.getInstance().getUser(playerSender);
        Channel c = user.getActiveChannel();
        if(c == null){
            user.setActiveChannel(Strings.getInstance().getChannel("global"));
        }
        if(chatManager.isOnCoolDown(playerSender) && user.getActiveChannel().doCooldown()){
            event.setCancelled(true);
            Messenger.sendMessage(Message.COOL_DOWN, playerSender);
            return;
        }
        user.getActiveChannel().sendMessage(playerSender,playerMessage);
        event.setCancelled(true);

    }
}
