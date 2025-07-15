package com.pedestriamc.strings.discord.listener.bukkit;

import com.pedestriamc.strings.api.event.StringsReloadEvent;
import com.pedestriamc.strings.discord.StringsDiscord;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

public class StringsReloadListener implements Listener {

    private final StringsDiscord strings;

    public StringsReloadListener(@NotNull StringsDiscord strings) {
        this.strings = strings;
    }

    @EventHandler
    void onEvent(@NotNull StringsReloadEvent event) {
        strings.reload();
    }

}
