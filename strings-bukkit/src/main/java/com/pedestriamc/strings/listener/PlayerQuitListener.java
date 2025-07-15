package com.pedestriamc.strings.listener;

import com.pedestriamc.strings.configuration.Configuration;
import com.pedestriamc.strings.configuration.Option;
import com.pedestriamc.strings.user.User;
import com.pedestriamc.strings.user.util.UserUtil;
import com.pedestriamc.strings.misc.ServerMessages;
import com.pedestriamc.strings.Strings;
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
        modifyLeaveMessage = config.getBoolean(Option.USE_CUSTOM_JOIN_LEAVE);
        doQuitMessage = config.getBoolean(Option.ENABLE_JOIN_LEAVE_MESSAGE);
    }

    @EventHandler
    void onEvent(@NotNull PlayerQuitEvent event) {
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
