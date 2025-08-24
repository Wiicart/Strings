package com.pedestriamc.strings.discord.listener.bukkit

import com.pedestriamc.strings.api.event.MessageDeletionEvent
import com.pedestriamc.strings.discord.StringsDiscord
import com.pedestriamc.strings.discord.manager.DiscordManager
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class MessageDeletionListener(strings: StringsDiscord) : Listener {

    val manager: DiscordManager = strings.manager;

    @EventHandler
    fun onEvent(event: MessageDeletionEvent) {
        manager.deleteMessage(event.message);
    }

}