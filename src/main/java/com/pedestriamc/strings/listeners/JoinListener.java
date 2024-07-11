package com.pedestriamc.strings.listeners;

import com.pedestriamc.strings.User;
import com.pedestriamc.strings.UserUtil;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener implements Listener {
    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent event){
        if(UserUtil.loadUser(event.getPlayer().getUniqueId()) == null){
            Bukkit.getLogger().info("[Strings] Join Listener - loadUser returned null");
            new User(event.getPlayer().getUniqueId());
        }
    }
}
