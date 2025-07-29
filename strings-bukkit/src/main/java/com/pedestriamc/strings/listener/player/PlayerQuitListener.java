package com.pedestriamc.strings.listener.player;

import com.pedestriamc.strings.api.settings.Option;
import com.pedestriamc.strings.configuration.Configuration;
import com.pedestriamc.strings.user.User;
import com.pedestriamc.strings.user.util.UserUtil;
import com.pedestriamc.strings.misc.ServerMessages;
import com.pedestriamc.strings.Strings;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

public class PlayerQuitListener implements Listener {

    private final UserUtil userUtil;
    private final boolean modifyLeaveMessage;
    private final ServerMessages serverMessages;
    private final boolean doQuitMessage;

    public PlayerQuitListener(@NotNull Strings strings) {
        userUtil = strings.users();
        serverMessages = strings.getServerMessages();

        Configuration config = strings.getConfiguration();
        modifyLeaveMessage = config.getBoolean(Option.Bool.USE_CUSTOM_JOIN_LEAVE);
        doQuitMessage = config.getBoolean(Option.Bool.ENABLE_JOIN_LEAVE_MESSAGE);
    }

    @EventHandler
    void onEvent(@NotNull PlayerQuitEvent event) {
        Bukkit.getLogger().info("Using custom leave message: " + modifyLeaveMessage);
        Bukkit.getLogger().info("Doing any quit messages: " + doQuitMessage);
        User user = userUtil.getUser(event.getPlayer());

        if(!doQuitMessage) {
            event.setQuitMessage(null);
        } else if(modifyLeaveMessage) {
            event.setQuitMessage(serverMessages.leaveMessage(event.getPlayer()));
        }

        userUtil.removeUser(user.getUniqueId());
        user.logOff();
    }

}
