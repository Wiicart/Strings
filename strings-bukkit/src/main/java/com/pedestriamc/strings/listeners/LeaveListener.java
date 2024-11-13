package com.pedestriamc.strings.listeners;

import com.pedestriamc.strings.user.User;
import com.pedestriamc.strings.user.UserUtil;
import com.pedestriamc.strings.misc.ServerMessages;
import com.pedestriamc.strings.Strings;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class LeaveListener implements Listener {

    private final Strings strings;
    private final boolean modifyLeaveMessage;
    private final ServerMessages serverMessages;

    public LeaveListener(Strings strings) {
        this.strings = strings;
        modifyLeaveMessage = strings.getConfig().getBoolean("custom-join-leave-message", false);
        serverMessages = strings.getServerMessages();
    }

    @EventHandler
    public void onEvent(PlayerQuitEvent event) {
        User user = strings.getUser(event.getPlayer());
        if (modifyLeaveMessage) {
            event.setQuitMessage(serverMessages.leaveMessage(event.getPlayer()));
        }
        UserUtil.UserMap.removeUser(event.getPlayer().getUniqueId());
        user.logOff();
    }
}
