package com.pedestriamc.strings.listeners;

import com.pedestriamc.strings.chat.ChatManager;
import com.pedestriamc.strings.user.User;
import com.pedestriamc.strings.chat.channels.Channel;
import com.pedestriamc.strings.message.Message;
import com.pedestriamc.strings.message.Messenger;
import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.chat.ChannelManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class ChatListener implements Listener {

    private final Strings strings;
    private final ChatManager chatManager;
    private final Channel defaultChannel;
    private final Map<String, Channel> symbolMap;
    private final Messenger messenger;

    public ChatListener(Strings strings) {
        this.strings = strings;
        chatManager = strings.getChatManager();
        ChannelManager channelManager = strings.getChannelManager();
        defaultChannel = channelManager.getChannel("default");
        symbolMap = strings.getChannelManager().getChannelSymbols();
        messenger = strings.getMessenger();
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onEvent(AsyncPlayerChatEvent event) {
        Player playerSender = event.getPlayer();
        String playerMessage = event.getMessage();

        User user = strings.getUser(playerSender);

        Container container = processSymbol(playerMessage, user);
        Channel channel = container.channel();
        playerMessage = container.message();

        if (channel == null) {
            user.setActiveChannel(defaultChannel);
            channel = user.getActiveChannel();
        }

        if (chatManager.isOnCoolDown(playerSender) && user.getActiveChannel().doCooldown()) {
            event.setCancelled(true);
            messenger.sendMessage(Message.COOL_DOWN, playerSender);
            return;
        }

        if(!channel.isCallEvent()) {
            event.setCancelled(true);
            channel.sendMessage(playerSender, playerMessage);
            return;
        }

        channel.sendMessage(playerSender, playerMessage, event);


    }

    public Container processSymbol(String msg, User user) {
        for (String key : symbolMap.keySet()) {
            if (msg.startsWith(key)) {
                Channel c = symbolMap.get(key);
                if (c.allows(user.getPlayer())) {
                    msg = msg.substring(key.length());
                    return new Container(c, msg);
                }
            }
        }
        return new Container(user.getActiveChannel(), msg);
    }

    private record Container(Channel channel, String message){}

}
