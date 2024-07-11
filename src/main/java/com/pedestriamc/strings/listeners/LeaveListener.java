package com.pedestriamc.strings.listeners;

import com.pedestriamc.strings.JoinLeaveMessage;
import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.UserUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;

public class LeaveListener {

    private final boolean modifyLeaveMessage = Strings.getInstance().modifyJoinLeaveMessages();
    private final JoinLeaveMessage joinLeaveMessage = Strings.getInstance().getJoinLeaveMessage();

    @EventHandler
    public void onPlayerQuitEvent(PlayerQuitEvent event){
        UserUtil.UserMap.removeUser(event.getPlayer().getUniqueId());
        if(modifyLeaveMessage){
            event.setQuitMessage(joinLeaveMessage.leaveMessage(event.getPlayer()));
        }
    }
}
