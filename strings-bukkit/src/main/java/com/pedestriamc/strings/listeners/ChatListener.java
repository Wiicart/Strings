package com.pedestriamc.strings.listeners;

import com.pedestriamc.strings.chat.ChatManager;
import com.pedestriamc.strings.User;
import com.pedestriamc.strings.api.event.ChannelChatEvent;
import com.pedestriamc.strings.chat.channels.Channel;
import com.pedestriamc.strings.message.Message;
import com.pedestriamc.strings.message.Messenger;
import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.chat.channels.ChannelManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Map;

public class ChatListener implements Listener {

    private final Strings strings;
    private final ChatManager chatManager;
    private final Channel defaultChannel;
    private final Map<String, Channel> symbolMap;

    public ChatListener(Strings strings) {
        this.strings = strings;
        chatManager = strings.getChatManager();
        ChannelManager channelManager = strings.getChannelManager();
        defaultChannel = channelManager.getChannel("default");
        symbolMap = strings.getChannelManager().getChannelSymbols();
    }

    @EventHandler
    public void onEvent(AsyncPlayerChatEvent event) {

        if (event instanceof ChannelChatEvent) {
            return;
        }

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
            Messenger.sendMessage(Message.COOL_DOWN, playerSender);
            return;
        }

        channel.sendMessage(playerSender, playerMessage);
        event.setCancelled(true);
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
