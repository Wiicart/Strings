package com.pedestriamc.strings.discord.listener.discord;

import com.pedestriamc.strings.discord.StringsDiscord;
import com.pedestriamc.strings.api.discord.Option;
import com.pedestriamc.strings.discord.manager.ChannelDiscordManager;
import com.pedestriamc.strings.discord.manager.GlobalDiscordManager;
import com.pedestriamc.strings.discord.manager.QueuedDiscordManager;
import net.dv8tion.jda.api.events.StatusChangeEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class StatusListener extends ListenerAdapter {

    private final StringsDiscord strings;

    private final QueuedDiscordManager manager;

    private boolean connected = false;

    // Use this if JDA isn't ready at onEnable - this will invoke loadChannels when JDA is ready.
    public StatusListener(@NotNull StringsDiscord strings, @NotNull QueuedDiscordManager manager) {
        this.strings = strings;
        this.manager = manager;
    }

    // Standard constructor, use when JDA is ready at onEnable.
    public StatusListener(@NotNull StringsDiscord strings) {
        this.strings = strings;
        this.manager = null;
    }

    @Override
    public void onStatusChange(@NotNull StatusChangeEvent event) {
        switch(event.getNewStatus()) {
            case LOGGING_IN -> sendLogInMessage();
            case CONNECTED -> completeManager(); // Will cancel itself if not needed
            default -> {}
        }
    }

    private void completeManager() {
        if (connected) {
            return;
        }
        connected = true;

        if (manager == null) {
            return;
        }

        try {
            strings.getLogger().info("DiscordManager initializing...");
            if (strings.getConfiguration().get(Option.Bool.GLOBAL)) {
                manager.complete(new GlobalDiscordManager(strings));
            } else {
                manager.complete(new ChannelDiscordManager(strings));
            }
        } catch(IllegalStateException ignored) {}
    }

    private void sendLogInMessage() {
        strings.getLogger().info("Logging into Bot....");
    }
}
