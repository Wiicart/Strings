package com.pedestriamc.strings.listeners;

import com.pedestriamc.strings.configuration.Configuration;
import com.pedestriamc.strings.configuration.ConfigurationOption;
import com.pedestriamc.strings.user.UserUtil;
import com.pedestriamc.strings.misc.ServerMessages;
import com.pedestriamc.strings.Strings;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;

public class JoinListener implements Listener {

    private final UserUtil userUtil;
    private final boolean modifyJoinMessage;
    private final ServerMessages serverMessages;
    private final boolean doMotd;
    private final boolean doJoinLeaveMessage;

    public JoinListener(@NotNull Strings strings) {
        userUtil = strings.getUserUtil();
        serverMessages = strings.getServerMessages();

        Configuration configuration = strings.getConfigClass();
        modifyJoinMessage = configuration.getBoolean(ConfigurationOption.CUSTOM_JOIN_LEAVE);
        doMotd = configuration.getBoolean(ConfigurationOption.ENABLE_MOTD);
        doJoinLeaveMessage = configuration.getBoolean(ConfigurationOption.JOIN_LEAVE);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onEvent(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        userUtil.loadUserAsync(player.getUniqueId());

        if(!doJoinLeaveMessage) {
            event.setJoinMessage(null);
        } else if(modifyJoinMessage) {
            event.setJoinMessage(serverMessages.joinMessage(player));
        }

        if(doMotd) {
            serverMessages.sendMOTD(player);
        }
    }
}
