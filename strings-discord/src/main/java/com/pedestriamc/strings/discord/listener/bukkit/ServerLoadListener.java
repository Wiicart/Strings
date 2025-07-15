package com.pedestriamc.strings.discord.listener.bukkit;

import com.pedestriamc.strings.discord.StringsDiscord;
import com.pedestriamc.strings.discord.configuration.Option;
import com.pedestriamc.strings.discord.manager.DiscordManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerLoadEvent;
import org.jetbrains.annotations.NotNull;

public class ServerLoadListener implements Listener {

    private final DiscordManager manager;

    private final String onlineMessage;

    public ServerLoadListener(@NotNull StringsDiscord strings) {
        manager = strings.getManager();
        onlineMessage = strings.getSettings().getString(Option.Text.SERVER_ONLINE_MESSAGE);
    }

    @EventHandler
    void onEvent(ServerLoadEvent event) {
        manager.broadcastDiscordMessage(onlineMessage);
    }
}
