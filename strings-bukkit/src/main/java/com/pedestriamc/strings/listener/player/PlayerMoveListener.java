package com.pedestriamc.strings.listener.player;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.user.User;
import com.pedestriamc.strings.user.util.UserUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jetbrains.annotations.NotNull;

/** Keeps the cross-region-safe position snapshot current on the entity thread. */
public final class PlayerMoveListener implements Listener {

    private final UserUtil users;

    public PlayerMoveListener(@NotNull Strings strings) {
        users = strings.users();
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    void onMove(@NotNull PlayerMoveEvent event) {
        if (event.getTo() == null) {
            return;
        }
        User user = users.getUser(event.getPlayer());
        user.updatePositionSnapshot(event.getTo());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    void onTeleport(@NotNull PlayerTeleportEvent event) {
        if (event.getTo() == null) {
            return;
        }
        User user = users.getUser(event.getPlayer());
        user.updatePositionSnapshot(event.getTo());
    }
}
