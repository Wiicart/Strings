package com.pedestriamc.strings.discord.listener.bukkit

import com.pedestriamc.strings.api.event.channel.ChannelChatEvent
import com.pedestriamc.strings.discord.StringsDiscord
import com.pedestriamc.strings.discord.manager.DiscordManager
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent

class CraftChatListener(strings: StringsDiscord) : Listener {

    private val manager: DiscordManager = strings.manager;

    // ChannelChatEvent should either be called as an AsyncPlayerChatEvent, or a ChannelChatEvent directly
    @EventHandler(priority = EventPriority.HIGHEST)
    fun onEvent(event: AsyncPlayerChatEvent) {
        if (event !is ChannelChatEvent) return;
        manager.processCraftEvent(event);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onEvent(event: ChannelChatEvent) {
        manager.processCraftEvent(event);
    }

}