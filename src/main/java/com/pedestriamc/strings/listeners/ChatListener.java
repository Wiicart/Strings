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
            return;
        }
        User user = Strings.getInstance().getUser(playerSender);
        if(chatManager.isOnCoolDown(playerSender)){
            cancelEvent(event);
            Messenger.sendMessage(Message.COOL_DOWN, event.getPlayer());
        }else{
            //cancel event, as it is being replaced by this plugin
            Channel c = user.getActiveChannel();
            if(c != null){
                user.getActiveChannel().sendMessage(playerSender,playerMessage);
            }else{
                Strings.getInstance().getChannel("global").sendMessage(playerSender,playerMessage);
                user.leaveChannel(user.getActiveChannel());
            }
            cancelEvent(event);
            if(!event.isCancelled()){
                chatManager.startCoolDown(playerSender);
            }
        }
    }

    public void cancelEvent(AsyncPlayerChatEvent event){
        event.setCancelled(true);
    }
}

