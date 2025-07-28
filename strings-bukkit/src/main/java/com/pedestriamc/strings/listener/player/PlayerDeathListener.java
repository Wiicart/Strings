package com.pedestriamc.strings.listener.player;

import com.pedestriamc.strings.Strings;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.jetbrains.annotations.NotNull;

public class PlayerDeathListener implements Listener {

    public PlayerDeathListener(@NotNull Strings strings) {

    }

    @EventHandler
    void onEvent(@NotNull PlayerDeathEvent event) {
        event.getEntity().getKiller();
    }
}
