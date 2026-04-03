package com.pedestriamc.strings.discord.listener.bukkit

import com.pedestriamc.strings.api.event.StringsReloadEvent
import com.pedestriamc.strings.api.event.strings.Listener
import com.pedestriamc.strings.discord.StringsDiscord
import org.jetbrains.annotations.NotNull

class StringsReloadListener(private val strings: StringsDiscord) {

    @Listener
    internal fun onEvent(@NotNull event: StringsReloadEvent) {
        strings.reload();
    }

}