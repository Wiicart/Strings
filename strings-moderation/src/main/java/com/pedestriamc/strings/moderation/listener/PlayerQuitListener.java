package com.pedestriamc.strings.moderation.listener;

import com.pedestriamc.strings.api.event.server.PlayerQuitEvent;
import com.pedestriamc.strings.api.event.strings.Listener;
import com.pedestriamc.strings.moderation.manager.RepetitionManager;
import org.jetbrains.annotations.NotNull;

public class PlayerQuitListener {

    private final RepetitionManager manager;

    public PlayerQuitListener(RepetitionManager manager) {
        this.manager = manager;
    }

    @Listener
    public void onEvent(@NotNull PlayerQuitEvent event) {
        try {
            manager.logOut(event.getPlayer());
        } catch(Exception ignored) {}
    }
}
