package com.pedestriamc.strings.listeners;

import com.pedestriamc.strings.user.User;
import com.pedestriamc.strings.user.YamlUserUtil;
import com.pedestriamc.strings.misc.ServerMessages;
import com.pedestriamc.strings.Strings;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener implements Listener {

    private final boolean modifyJoinMessage;
    private final ServerMessages serverMessages;
    private final boolean doMotd;
    private final boolean doJoinLeaveMessage;

    public JoinListener(Strings strings) {
        modifyJoinMessage = strings.getConfig().getBoolean("custom-join-leave-message", false);
        serverMessages = strings.getServerMessages();
        doMotd = strings.getConfig().getBoolean("enable-motd", false);
        doJoinLeaveMessage = strings.getConfig().getBoolean("enable-join-leave-messages");
    }

    @EventHandler
    public void onEvent(PlayerJoinEvent event) {
        if(YamlUserUtil.loadUser(event.getPlayer().getUniqueId()) == null) {
            new User(event.getPlayer().getUniqueId());
        }

        if(!doJoinLeaveMessage) {
            event.setJoinMessage(null);
        } else if(modifyJoinMessage) {
            event.setJoinMessage(serverMessages.joinMessage(event.getPlayer()));
        }

        if(doMotd) {
            serverMessages.sendMOTD(event.getPlayer());
        }
    }
}
