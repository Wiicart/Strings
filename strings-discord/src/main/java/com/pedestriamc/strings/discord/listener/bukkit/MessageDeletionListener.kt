package com.pedestriamc.strings.discord.listener.bukkit

import com.pedestriamc.strings.api.event.moderation.MessageDeletionEvent
import com.pedestriamc.strings.api.event.strings.Listener
import com.pedestriamc.strings.discord.StringsDiscord
import com.pedestriamc.strings.discord.manager.DiscordManager

class MessageDeletionListener(strings: StringsDiscord) {

    val manager: DiscordManager = strings.manager;

    @Listener
    fun onEvent(event: MessageDeletionEvent) {
        manager.deleteMessage(event.message);
    }

}