package com.pedestriamc.strings.listeners;

import com.pedestriamc.strings.api.event.ChannelChatEvent;
import com.pedestriamc.strings.chat.ChannelManager;
import com.pedestriamc.strings.user.User;
import com.pedestriamc.strings.api.channel.Channel;
import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.user.util.UserUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class ChatListener implements Listener {

    private final Channel defaultChannel;
    private final ChannelManager channelLoader;
    private final UserUtil userUtil;

    public ChatListener(@NotNull Strings strings) {
        channelLoader = strings.getChannelLoader();
        defaultChannel = channelLoader.getChannel("default");
        userUtil = strings.getUserUtil();
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    void onEvent(AsyncPlayerChatEvent event) {
        if (event instanceof ChannelChatEvent) {
            return;
        } else {
            event.setCancelled(true);
        }

        Player playerSender = event.getPlayer();
        String playerMessage = event.getMessage();

        User user = userUtil.getUser(playerSender);

        Container container = processSymbol(playerMessage, user);
        Channel channel = container.channel();
        playerMessage = container.message();

        if (channel == null) {
            user.setActiveChannel(defaultChannel);
            userUtil.saveUser(user);
            channel = user.getActiveChannel();
        }

        channel.sendMessage(playerSender, playerMessage);
    }

    @Contract("_, _ -> new")
    private @NotNull Container processSymbol(String msg, User user) {
        for(Map.Entry<String, Channel> entry : channelLoader.getChannelSymbols().entrySet()) {
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
