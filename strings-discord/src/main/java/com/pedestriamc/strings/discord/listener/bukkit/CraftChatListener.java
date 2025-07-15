package com.pedestriamc.strings.discord.listener.bukkit;

import com.pedestriamc.strings.api.event.channel.ChannelChatEvent;
import com.pedestriamc.strings.discord.StringsDiscord;
import com.pedestriamc.strings.discord.manager.DiscordManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.jetbrains.annotations.NotNull;

import static org.bukkit.event.EventPriority.HIGH;

public final class CraftChatListener implements Listener {

    private final DiscordManager manager;

    public CraftChatListener(@NotNull StringsDiscord strings) {
        manager = strings.getManager();
    }

    @EventHandler(priority = HIGH)
    void onEvent(@NotNull AsyncPlayerChatEvent asyncPlayerChatEvent) {
        if(asyncPlayerChatEvent instanceof ChannelChatEvent event) {
            manager.sendDiscordMessageFromEvent(event);
        }
    }
}
