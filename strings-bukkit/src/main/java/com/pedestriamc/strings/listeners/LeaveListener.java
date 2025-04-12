package com.pedestriamc.strings.listeners;

import com.pedestriamc.strings.configuration.Configuration;
import com.pedestriamc.strings.configuration.ConfigurationOption;
import com.pedestriamc.strings.user.User;
import com.pedestriamc.strings.user.UserUtil;
import com.pedestriamc.strings.misc.ServerMessages;
import com.pedestriamc.strings.Strings;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

public class LeaveListener implements Listener {

    private final Strings strings;
    private final UserUtil userUtil;
    private final boolean modifyLeaveMessage;
    private final ServerMessages serverMessages;
    private final boolean doJoinLeaveMessage;

    public LeaveListener(@NotNull Strings strings) {
        this.strings = strings;
        userUtil = strings.getUserUtil();
        serverMessages = strings.getServerMessages();

        Configuration config = strings.getConfigClass();
        modifyLeaveMessage = config.getBoolean(ConfigurationOption.JOIN_LEAVE);
        doJoinLeaveMessage = config.getBoolean(ConfigurationOption.CUSTOM_JOIN_LEAVE);
    }

    @EventHandler
    public void onEvent(@NotNull PlayerQuitEvent event) {
        User user = strings.getUser(event.getPlayer());

        if(!doJoinLeaveMessage) {
            event.setQuitMessage(null);
        } else if(modifyLeaveMessage) {
            event.setQuitMessage(serverMessages.leaveMessage(event.getPlayer()));
        }

        userUtil.removeUser(user.getUuid());
        user.logOff();
    }

}
