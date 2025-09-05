package com.pedestriamc.strings.discord.listener.bukkit

import com.pedestriamc.strings.api.event.StringsReloadEvent
import com.pedestriamc.strings.discord.StringsDiscord
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.jetbrains.annotations.NotNull

class StringsReloadListener(private val strings: StringsDiscord) : Listener {

    @EventHandler
    internal fun onEvent(@NotNull event: StringsReloadEvent) {
        strings.reload();
    }

}