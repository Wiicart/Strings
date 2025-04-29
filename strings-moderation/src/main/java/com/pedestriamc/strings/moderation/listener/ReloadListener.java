package com.pedestriamc.strings.moderation.listener;

import com.pedestriamc.strings.api.event.StringsReloadEvent;
import com.pedestriamc.strings.moderation.StringsModeration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ReloadListener implements Listener {

    private final StringsModeration plugin;

    public ReloadListener(StringsModeration plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEvent(StringsReloadEvent event) {
        plugin.reload();
    }
}
