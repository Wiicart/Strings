package com.pedestriamc.strings.listener.chat;

import com.pedestriamc.strings.api.event.channel.ChannelChatEvent;
import com.pedestriamc.strings.user.User;
import com.pedestriamc.strings.api.channel.Channel;
import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.user.util.UserUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.jetbrains.annotations.NotNull;

public class SpigotChatListener extends AbstractChatListener {

    private final Channel defaultChannel;
    private final UserUtil userUtil;

    public SpigotChatListener(@NotNull Strings strings) {
        super(strings);
        defaultChannel = strings.getChannelLoader().getDefaultChannel();
        userUtil = strings.users();
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

}
