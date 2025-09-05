package com.pedestriamc.strings.moderation.listener;

import com.pedestriamc.strings.api.StringsProvider;
import com.pedestriamc.strings.moderation.manager.RepetitionManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

public class PlayerQuitListener implements Listener {

    private final RepetitionManager manager;

    public PlayerQuitListener(RepetitionManager manager) {
        this.manager = manager;
    }

    @EventHandler
    public void onEvent(@NotNull PlayerQuitEvent event) {
        try {
            manager.logOut(StringsProvider.get().getUser(event.getPlayer().getUniqueId()));
        } catch(Exception ignored) {}
    }
}
