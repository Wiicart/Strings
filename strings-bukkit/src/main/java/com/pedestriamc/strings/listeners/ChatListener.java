package com.pedestriamc.strings.listeners;

import com.pedestriamc.strings.ChatManager;
import com.pedestriamc.strings.User;
import com.pedestriamc.strings.api.event.ChannelChatEvent;
import com.pedestriamc.strings.channels.Channel;
import com.pedestriamc.strings.message.Message;
import com.pedestriamc.strings.message.Messenger;
import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.channels.ChannelManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener implements Listener {

    private final ChatManager chatManager = Strings.getInstance().getChatManager();
    private final ChannelManager channelManager = Strings.getInstance().getChannelManager();
    private final Channel defaultChannel = channelManager.getChannel("default");

    @EventHandler
    public void onAsyncPlayerChatEvent(AsyncPlayerChatEvent event) {

        if(event instanceof ChannelChatEvent){
            return;
        }

        Player playerSender = event.getPlayer();
        String playerMessage = event.getMessage();

        User user = Strings.getInstance().getUser(playerSender);
        Channel channel = user.getActiveChannel();

        if(channel == null){
            user.setActiveChannel(defaultChannel);
        }
        if(chatManager.isOnCoolDown(playerSender) && user.getActiveChannel().doCooldown()){
            event.setCancelled(true);
            Messenger.sendMessage(Message.COOL_DOWN, playerSender);
            return;
        }

        channel.sendMessage(playerSender, playerMessage);
        event.setCancelled(true);
    }
}
