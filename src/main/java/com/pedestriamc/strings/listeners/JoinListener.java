package com.pedestriamc.strings.listeners;

import com.pedestriamc.strings.JoinLeaveMessage;
import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.User;
import com.pedestriamc.strings.UserUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener implements Listener {

    private final boolean modifyJoinMessage = Strings.getInstance().modifyJoinLeaveMessages();
    private final JoinLeaveMessage joinLeaveMessage = Strings.getInstance().getJoinLeaveMessage();

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent event){
        if(UserUtil.loadUser(event.getPlayer().getUniqueId()) == null){
            new User(event.getPlayer().getUniqueId());
        }
        if(modifyJoinMessage){
            event.setJoinMessage(joinLeaveMessage.joinMessage(event.getPlayer()));
        }
    }
}
