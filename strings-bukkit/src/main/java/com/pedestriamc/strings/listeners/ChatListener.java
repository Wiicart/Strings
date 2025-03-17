package com.pedestriamc.strings.listeners;

import com.pedestriamc.strings.api.channel.ChannelLoader;
import com.pedestriamc.strings.api.event.ChannelChatEvent;
import com.pedestriamc.strings.chat.StringsChannelLoader;
import com.pedestriamc.strings.user.User;
import com.pedestriamc.strings.api.channel.Channel;
import com.pedestriamc.strings.Strings;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Map;

public class ChatListener implements Listener {

    private final Strings strings;
    private final Channel defaultChannel;
    private final Map<String, Channel> symbolMap;

    public ChatListener(Strings strings) {
        this.strings = strings;
        ChannelLoader channelLoader = strings.getChannelLoader();
        defaultChannel = channelLoader.getChannel("default");
        symbolMap = ((StringsChannelLoader) strings.getChannelLoader()).getChannelSymbols();
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEvent(AsyncPlayerChatEvent event) {
        if (event instanceof ChannelChatEvent) {
            return;
        } else {
            event.setCancelled(true);
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

        channel.sendMessage(playerSender, playerMessage);
    }

    private Container processSymbol(String msg, User user) {
        for(Map.Entry<String, Channel> entry : symbolMap.entrySet()) {
            if(msg.startsWith(entry.getKey())) {
                Channel c = entry.getValue();
                if (c.allows(user.getPlayer())) {
                    msg = msg.substring(entry.getKey().length());
                    return new Container(c, msg);
                }
            }
        }
        return new Container(user.getActiveChannel(), msg);
    }

    private record Container(Channel channel, String message) {}

}
