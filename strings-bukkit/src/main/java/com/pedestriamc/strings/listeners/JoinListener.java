package com.pedestriamc.strings.listeners;

import com.pedestriamc.strings.User;
import com.pedestriamc.strings.UserUtil;
import com.pedestriamc.stringscustoms.ServerMessages;
import com.pedestriamc.strings.Strings;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener implements Listener {

    private final Strings strings = Strings.getInstance();
    private final boolean modifyJoinMessage = strings.modifyJoinLeaveMessages();
    private final ServerMessages serverMessages = strings.getJoinLeaveMessage();
    private final boolean doMOTD = strings.getConfig().getBoolean("enable-motd", false);

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent event){
        if(UserUtil.loadUser(event.getPlayer().getUniqueId()) == null){
            new User(event.getPlayer().getUniqueId());
        }
        if(modifyJoinMessage){
            event.setJoinMessage(serverMessages.joinMessage(event.getPlayer()));
        }
        if(doMOTD){
            serverMessages.sendMOTD(event.getPlayer());
        }
    }
}
