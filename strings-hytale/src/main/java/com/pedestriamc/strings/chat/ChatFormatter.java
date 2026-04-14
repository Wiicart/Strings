package com.pedestriamc.strings.chat;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.event.events.player.PlayerChatEvent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import org.jetbrains.annotations.NotNull;

public class ChatFormatter implements PlayerChatEvent.Formatter {

    @Override
    public @NotNull Message format(@NotNull PlayerRef playerRef, @NotNull String s) {
        Message.raw("ger");
        return null;
    }
}
