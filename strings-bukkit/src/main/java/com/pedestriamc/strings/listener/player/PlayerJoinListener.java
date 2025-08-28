package com.pedestriamc.strings.listener.player;

import com.pedestriamc.strings.api.settings.Option;
import com.pedestriamc.strings.configuration.Configuration;
import com.pedestriamc.strings.user.util.UserUtil;
import com.pedestriamc.strings.misc.ServerMessages;
import com.pedestriamc.strings.Strings;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;

public class PlayerJoinListener implements Listener {

    private final UserUtil userUtil;
    private final boolean modifyJoinMessage;
    private final ServerMessages serverMessages;
    private final boolean doMotd;
    private final boolean doJoinMessage;

    public PlayerJoinListener(@NotNull Strings strings) {
        userUtil = strings.users();
        serverMessages = strings.getServerMessages();

        Configuration configuration = strings.getConfiguration();
        modifyJoinMessage = configuration.get(Option.Bool.USE_CUSTOM_JOIN_LEAVE);
        doMotd = configuration.get(Option.Bool.ENABLE_MOTD);
        doJoinMessage = configuration.get(Option.Bool.ENABLE_JOIN_LEAVE_MESSAGE);
    }

    @EventHandler(priority = EventPriority.HIGH)
    void onEvent(@NotNull PlayerJoinEvent event) {
        Player player = event.getPlayer();
        userUtil.loadUserAsync(player.getUniqueId());

        if(!doJoinMessage) {
            event.setJoinMessage(null);
        } else if(modifyJoinMessage) {
            event.setJoinMessage(serverMessages.joinMessage(player));
        }

        if(doMotd) {
            serverMessages.sendMOTD(player);
        }
    }
}
