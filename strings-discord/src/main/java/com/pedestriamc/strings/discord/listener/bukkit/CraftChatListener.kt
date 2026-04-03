package com.pedestriamc.strings.discord.listener.bukkit

import com.pedestriamc.strings.api.event.ChannelChatEvent
import com.pedestriamc.strings.api.event.strings.Listener
import com.pedestriamc.strings.discord.StringsDiscord
import com.pedestriamc.strings.discord.manager.DiscordManager

class CraftChatListener(strings: StringsDiscord) {

    private val manager: DiscordManager = strings.manager;

    @Listener
    fun onEvent(event: ChannelChatEvent) {
        manager.processCraftEvent(event);
    }

}