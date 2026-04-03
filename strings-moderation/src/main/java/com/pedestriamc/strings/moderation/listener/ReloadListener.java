package com.pedestriamc.strings.moderation.listener;

import com.pedestriamc.strings.api.event.StringsReloadEvent;
import com.pedestriamc.strings.api.event.strings.Listener;
import com.pedestriamc.strings.moderation.StringsModeration;
import org.jetbrains.annotations.NotNull;

public class ReloadListener {

    private final StringsModeration plugin;

    public ReloadListener(StringsModeration plugin) {
        this.plugin = plugin;
    }

    @Listener
    void onEvent(@NotNull StringsReloadEvent event) {
        plugin.reload();
    }
}
