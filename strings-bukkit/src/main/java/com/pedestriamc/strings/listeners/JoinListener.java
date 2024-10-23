package com.pedestriamc.strings.listeners;

import com.pedestriamc.strings.user.User;
import com.pedestriamc.strings.user.UserUtil;
import com.pedestriamc.strings.ServerMessages;
import com.pedestriamc.strings.Strings;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener implements Listener {

    private final boolean modifyJoinMessage;
    private final ServerMessages serverMessages;
    private final boolean doMotd;

    public JoinListener(Strings strings){
        modifyJoinMessage = strings.getConfig().getBoolean("custom-join-leave-message", false);
        serverMessages = strings.getServerMessages();
        doMotd = strings.getConfig().getBoolean("enable-motd", false);
    }

    @EventHandler
    public void onEvent(PlayerJoinEvent event){
        if(UserUtil.loadUser(event.getPlayer().getUniqueId()) == null){
            new User(event.getPlayer().getUniqueId());
        }
        if(modifyJoinMessage){
            event.setJoinMessage(serverMessages.joinMessage(event.getPlayer()));
        }
        if(doMotd){
            serverMessages.sendMOTD(event.getPlayer());
        }
    }
}
