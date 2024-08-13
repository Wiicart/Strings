package com.pedestriamc.strings.listeners;

import com.pedestriamc.strings.ChatManager;
import com.pedestriamc.strings.User;
import com.pedestriamc.strings.channels.Channel;
import com.pedestriamc.strings.channels.ChannelChatEvent;
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
    private final Channel global = channelManager.getChannel("global");

    @EventHandler
    public void onAsyncPlayerChatEvent(AsyncPlayerChatEvent event) {
        Player playerSender = event.getPlayer();
        String playerMessage = event.getMessage();

        if(event instanceof ChannelChatEvent){
            chatManager.startCoolDown(playerSender);
            return;
        }

        User user = Strings.getInstance().getUser(playerSender);
        Channel channel = user.getActiveChannel();


        Channel worldChannel = channelManager.getWorldChannel(user.getWorld());

        if(worldChannel != null){
            channel = worldChannel;
        }

        if(channel == null){
            user.setActiveChannel(Strings.getInstance().getChannel("global"));
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
