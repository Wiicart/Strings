package com.pedestriamc.strings.listener.player;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.user.util.UserUtil;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.NotNull;

public class PlayerDamageListener implements Listener {

    private final UserUtil userUtil;

    public PlayerDamageListener(@NotNull Strings strings) {
        userUtil = strings.users();
    }

    @EventHandler
    void onEvent(@NotNull EntityDamageEvent event) {
        Entity damaged = event.getEntity();
        if (damaged instanceof Player player) {
            userUtil.getUser(player).pushDamageEvent(event);
        }
    }

}
